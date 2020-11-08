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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.lang.reflect.Modifier;
import p.vo.AccessModifiers;
import p.vo.Field;
import p.vo.LocalField;
import p.vo.TypeHolder;

public class MyVisitor extends ASTVisitor {

	private static MyVisitor singleton = null;

	private MyVisitor() {
	}

	public static MyVisitor getInstance() {
		if (singleton == null)
			singleton = new MyVisitor();
		return singleton;
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
		boolean isInterface = true;
		List<String> implementedInterfaces = new ArrayList<>();
		
		if (!td.isInterface()) {
			name = td.resolveBinding().getName();
			root = td.resolveBinding().getSuperclass().getName();
			parent = td.resolveBinding().getSuperclass().getName();
			isInterface = false;
			System.out.println(td.resolveBinding().getInterfaces());
			for (ITypeBinding itb : td.resolveBinding().getInterfaces()) {
				System.out.println(itb);
				implementedInterfaces.add(itb.getName());
				
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

		TypeHolder th = TypeHolder.getInstance();
		th.addType(name, parent, root, fields, implementedInterfaces, isInterface);

		return true;
	}
	
	// need to consider parameters
	// need to check constructors
	// need to check variables inside block scope
	public boolean visit(MethodDeclaration md) {

		List<LocalField> localFields = new ArrayList<>();
		TypeDeclaration parent = (TypeDeclaration) md.getParent();
		System.out.println("method declaration: " + md.getName().getIdentifier());
		System.out.print(((TypeDeclaration)md.getParent()).getName().getIdentifier());
		
		if (md.getBody() != null) {
			System.out.println(md);
			for (Statement o : (List<Statement>) md.getBody().statements()) {
				if (o instanceof VariableDeclarationStatement) {
					for (VariableDeclarationFragment vfd: (List<VariableDeclarationFragment>) ((VariableDeclarationStatement) o).fragments()) {
						TypeHolder.getInstance().typesByName.get(parent.resolveBinding().getName()).addLocalField(new LocalField(vfd.getName().getIdentifier()));
					}			
				}

				
				// explore IfStatement
				// explore loops [for, while, do-while]
				// explore block
				// https://www.ibm.com/support/knowledgecenter/en/SSZHNR_2.0.0/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/dom/class-use/Statement.html
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
