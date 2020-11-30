package helper;

import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import p.vo.LocalField;

public class StatementHandler {
	
	public static void handleStatement(Statement statement, TypeDeclaration parent) {
		if (statement instanceof VariableDeclarationStatement) {
			handleVariableDeclarations((VariableDeclarationStatement) statement, parent);
		} else if (statement instanceof IfStatement) {
			handleIfStatement((IfStatement) statement, parent);
		} else if (statement instanceof ForStatement) {
			handleForStatement((ForStatement) statement, parent);
		} else if (statement instanceof EnhancedForStatement) {
			handleEnhancedForStatement((EnhancedForStatement) statement, parent);
		} else if (statement instanceof DoStatement) {
			handleDoStatement((DoStatement) statement, parent);
		} else if (statement instanceof WhileStatement) {
			handleWhileStatement((WhileStatement) statement, parent);
		} else if (statement instanceof TryStatement) {
			handleTryStatement((TryStatement) statement, parent);
		} else if (statement instanceof SwitchStatement) {
			handleSwitchStatement((SwitchStatement) statement, parent);
		} else if (statement instanceof SynchronizedStatement) {
			handleSynchronizedStatement((SynchronizedStatement) statement, parent);
		} else if (statement instanceof Block) {
			processBlock((Block) statement, parent);
		} 
	}

	public static void handleVariableDeclarationExpression(VariableDeclarationExpression vdfExpression, TypeDeclaration parent) {
		List<VariableDeclarationFragment> fragements = vdfExpression.fragments();
		for (VariableDeclarationFragment fragement: fragements) {
			processLocalField(fragement.getName().getIdentifier(), parent);
		}
	}
	
	private static void handleIfStatement(IfStatement ifStatement, TypeDeclaration parent) {
		//Handle body of if statement
		Block ifBlock = (Block) ((IfStatement) ifStatement).getThenStatement();
		processBlock(ifBlock, parent);
		
		//Handle else of if statement
		Statement ifOrElse = ifStatement.getElseStatement();
		if (ifOrElse != null) {
			if (ifOrElse instanceof Block) {
				processBlock((Block) ifOrElse, parent);
			} else if(ifOrElse instanceof IfStatement) {
				handleStatement((Statement) ifOrElse, parent);
			}
		}
	}
	
	private static void handleForStatement(ForStatement forStatement, TypeDeclaration parent) {
		List<VariableDeclarationExpression> initializers = forStatement.initializers();
		for (VariableDeclarationExpression initializer : initializers) {
			handleVariableDeclarationExpression(initializer, parent);
		}
		
		processBlock((Block) forStatement.getBody(), parent);
	}
	
	private static void handleEnhancedForStatement(EnhancedForStatement enhancedForStatement, TypeDeclaration parent) {
		//Handle iterator
		String fieldName = enhancedForStatement.getParameter().getName().getIdentifier();
		processLocalField(fieldName, parent);
		
		//Handle body of loop
		processBlock((Block) enhancedForStatement.getBody(), parent);
		
	}
	
	private static void handleDoStatement(DoStatement doStatement, TypeDeclaration parent) {
		processBlock((Block) doStatement.getBody(), parent);
	}
	
	private static void handleWhileStatement(WhileStatement whileStatement, TypeDeclaration parent) {
		processBlock((Block) whileStatement.getBody(), parent);
	}
	
	private static void handleTryStatement(TryStatement tryStatement, TypeDeclaration parent) {
		//Try block
		processBlock((Block) tryStatement.getBody(), parent);
		//Catch block
		List<CatchClause> clauses = tryStatement.catchClauses();
		for (CatchClause clause: clauses) {
			processBlock((Block) clause.getBody(), parent);
		}
		//Finally block
		processBlock((Block) tryStatement.getFinally(), parent);
	}
	
	private static void handleSwitchStatement(SwitchStatement switchStatement, TypeDeclaration parent) {
		//There are no blocks within a switch object which is why processBlock isn't called.
		List<Statement> statements = switchStatement.statements();
		for (Statement statement : statements) {
			handleStatement(statement, parent);
		}
	}
	
	private static void handleSynchronizedStatement(SynchronizedStatement synchronizedStatement, TypeDeclaration parent) {
		processBlock((Block) synchronizedStatement.getBody(), parent);
	}
	
	private static void handleVariableDeclarations(VariableDeclarationStatement vds, TypeDeclaration parent) {
		List<VariableDeclarationFragment> fragments = ((VariableDeclarationStatement) vds).fragments();
		for (VariableDeclarationFragment vfd: fragments) {
			processLocalField(vfd.getName().getIdentifier(), parent);
		}
	}
	
	private static void processBlock(Block block, TypeDeclaration parent) {
		List<Statement> statements = block.statements();
		
		for (Statement statement : statements) {
			handleStatement(statement, parent);
		}
	}
	
	private static void processLocalField(String name, TypeDeclaration parent) {
		TypeHolder.getInstance().typesByName.get(parent.resolveBinding().getName()).addLocalField(new LocalField(name));
	}
}
