/**
 * CSC 600 Assignment #3
 * 
 * On my honor, Tyler Hetland, this assignment is my own work and I have
 * not shared my solution with anyone.
 *
 */

package p.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import helper.TypeHolder;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class RunAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public RunAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@Override
	public void run(IAction action) {
		// Compilation unit points to root node of parse tree
		// for a given class declaration
		List<ICompilationUnit> iCUs = new ArrayList<ICompilationUnit>();

		// get workspace
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		// get all projects in workspace
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		IProject iProject = iProjectList[0]; // JW: we're interested in the first project only.
		IJavaProject iJavaProject = JavaCore.create(iProject);

		try {
			// get all packages from the projects
			IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
			for (IPackageFragment iPackageFragment : iPackageFragmentList) {
				if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
					continue;
				}

				// JW: this part is changed.
				// Here we collect all iCompilationUnits first.
				// get compilation unit list from each project
				ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
				for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
					iCUs.add(iCompilationUnit);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// JW: this part is newly added (or revised).
		// We now create all ASTs (not a single one at a time) altogether.
		// Just copy the following code.
		// creating parse trees
		ICompilationUnit[] compUnits = iCUs.toArray(new ICompilationUnit[0]);
		final Map<ICompilationUnit, ASTNode> parsedCompilationUnits = new HashMap<ICompilationUnit, ASTNode>();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setEnvironment(null, null, null, true);
		parser.setProject(iJavaProject);
		parser.createASTs(compUnits, new String[0], new ASTRequestor() {
			@Override
			public final void acceptAST(final ICompilationUnit unit, final CompilationUnit node) {
				parsedCompilationUnits.put(unit, node);
			}

			@Override
			public final void acceptBinding(final String key, final IBinding binding) {
				// Do nothing
			}
		}, null);

		// JW: below is what you would be interested.
		// Each compilation unit is now retrieved from a hashmap above and then accepts
		// an ASTVisitor.

		// JW: below is what you would be interested.
		// Each compilation unit is now retrieved from a hashmap above and then accepts
		// an ASTVisitor.
		Iterator<ICompilationUnit> keySetIterator = parsedCompilationUnits.keySet().iterator();
		while (keySetIterator.hasNext()) {
			ICompilationUnit iCU = keySetIterator.next();
			try {
				IResource iResource = iCU.getUnderlyingResource();
				IFile iFile = (IFile) iResource;
				String fullPath = iFile.getRawLocation().toString();
				ICompilationUnit workingCopy = iCU.getWorkingCopy(null);
				ASTParser astParser = ASTParser.newParser(AST.JLS3);
				astParser.setResolveBindings(true);
				astParser.setSource(workingCopy);
				CompilationUnit cu = (CompilationUnit) astParser.createAST(null);

				// need to pass current file path to visitor to differentiate files
				MyVisitor.getInstance().setSourceAndFilePath(fullPath, workingCopy.getSource());
				cu.accept(MyVisitor.getInstance());

				
//				PrintStream output;
//				try {
//					output = new PrintStream(new File(fullPath)); 
//					//output.println(MyVisitor.getInstance().source);
//				} catch (FileNotFoundException e) { // TODO Auto-generated catch block e.printStackTr
//				}
				 
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// check preconditions

		// if passed visit with code change visitor
		// note start positions of all places to change
		// go from start pos to start pos changing and copying everything after

		// typeHolder.display();
		String currName = "i";
		String newName = "j";
		String className = "A";
		String packageName = "test";
		TypeHolder.determineChildren();
		Preconditions preconditions = new Preconditions(currName, newName, className, packageName);
		if (preconditions.checkPreconditions()) {
			// If the application meets the precondition checks then
			// continue by performing the code changes.
			TypeHolder.getInstance().findVariableStartPositions(className, currName, packageName);
			
			RenameAction.changeVariableName(currName, newName, TypeHolder.getInstance().findVariableStartPositions(className, currName, packageName), MyVisitor.getInstance().getSourceByFilePath());

		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of the
	 * 'real' action here if we want, but this can only happen after the delegate
	 * has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	@Override
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell for
	 * the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}