/**
* CSC 600 Assignment #3
*
* On my honor, Tyler Hetland, this assignment is my own work and I have
* not shared my solution with anyone.
*
*/

package p.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import helper.StatementHandler;
import helper.TypeHolder;

import java.lang.reflect.Modifier;
import p.vo.AccessModifiers;
import p.vo.Field;
import p.vo.LocalField;

public class MyVisitor extends ASTVisitor {

	private static MyVisitor singleton = null;
	String source;

	private MyVisitor() {
	}

	public static MyVisitor getInstance() {
		if (singleton == null)
			singleton = new MyVisitor();
		return singleton;
	}
	
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Visit all type declarations passing off meta data to be aggregated and
	 * operated upon after visiting each and every type declaration. Key data
	 * elements gathered : type name, super type name, relative root, and depth from
	 * java.lang.Object
	 */
	public boolean visit(TypeDeclaration td) {
		ITypeBinding superclass = td.resolveBinding().getSuperclass();
		String root = "";
		String name = "";
		String parent = "";
		String packageName = null;
		boolean isInterface = true;
		
		List<String> implementedInterfaces = new ArrayList<>();
		if (!td.isInterface()) {
			name = td.resolveBinding().getName();
			root = td.resolveBinding().getSuperclass().getName();
			parent = td.resolveBinding().getSuperclass().getName();
			isInterface = false;
			for (ITypeBinding itb : td.resolveBinding().getInterfaces()) {
				implementedInterfaces.add(itb.getName());
				
			}
		} else {
			name = td.resolveBinding().getName();			
			List<SimpleType> interfaces = td.superInterfaceTypes();
			for (SimpleType i : interfaces) {
				implementedInterfaces.add(i.getName().getFullyQualifiedName());
			}

		}
		
		while (null != superclass) {
			superclass = superclass.getSuperclass();
			if (null != superclass && !superclass.getName().equals("Object")) {
				root = superclass.getName();
			}
		}
		
		// determine all instance level fields
		List<Field> fields = new ArrayList<>();
		for (FieldDeclaration fd : td.getFields()) {	
			AccessModifiers accessMods = determineAccessModifier(fd.getModifiers());
			for (VariableDeclarationFragment vfd: (List<VariableDeclarationFragment>) fd.fragments()) {
				Field f = new Field(vfd.getName().getIdentifier(), accessMods);
				fields.add(f);
			}			
		}
		
		CompilationUnit cu = (CompilationUnit) td.getParent();
		if (null != cu.getPackage()) {
			packageName = cu.getPackage().getName().getFullyQualifiedName();
		}

		TypeHolder th = TypeHolder.getInstance();
		th.addType(name, parent, root, fields, implementedInterfaces, isInterface, packageName);

		return true;
	}
	
	// need to consider parameters
	// need to check constructors
	// need to check variables inside block scope
	public boolean visit(MethodDeclaration md) {
		
		String s =source.substring(0 , md.getName().getStartPosition()) + "newname" + source.substring(md.getName().getStartPosition() + 1);
		
		List<LocalField> localFields = new ArrayList<>();
		TypeDeclaration parent = (TypeDeclaration) md.getParent();

		//md.parameters()
		// 1 param SingleVariableDeclaration
		List<SingleVariableDeclaration> parameters = md.parameters();
		for (SingleVariableDeclaration parameter : parameters) {
			TypeHolder.getInstance().typesByName.get(
					parent.resolveBinding().getName())
						.addLocalField(new LocalField(parameter.getName().getIdentifier()));
		}
		
		if (md.getBody() != null) {
			List<Statement> statements = md.getBody().statements();
			for (Statement statement : statements) {
				StatementHandler.handleStatement(statement, parent);
			}
		}
		
		return true;
	}
	
	
	private AccessModifiers determineAccessModifier(int mods) {
		AccessModifiers returnValue;
		if (Modifier.isPrivate(mods)) {
			returnValue = AccessModifiers.PRIVATE;
		} else if (Modifier.isPublic(mods)) {
			returnValue = AccessModifiers.PUBLIC;
		} else if (Modifier.isProtected(mods)) {
			returnValue = AccessModifiers.PROTECTED;
		} else {
			returnValue = AccessModifiers.NO_MODIFIER;
		}
		
		return returnValue;
	}
}
