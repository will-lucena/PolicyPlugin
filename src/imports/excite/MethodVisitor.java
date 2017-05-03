package excite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.Compartment;

public class MethodVisitor extends ASTVisitor
{
	@Override
	public boolean visit(ThrowStatement node)
	{
		boolean isRaise = true;
		boolean isRethrow = false;
		MethodDeclaration method = null;
		CatchClause catchClause = null;
		ASTNode parent = node.getParent();

		while (!(parent instanceof MethodDeclaration))
		{
			parent = parent.getParent();

			if (parent instanceof MethodDeclaration)
			{
				method = (MethodDeclaration) parent;
			}

			if (parent instanceof CatchClause)
			{
				catchClause = (CatchClause) parent;
				
				isRethrow = true;
				isRaise = false;
			}
		}
		

		if (isRaise)
		{
			if (method != null && method instanceof MethodDeclaration)
			{
				verifyRaise(node, method);
			}
		}
		
		if (isRethrow)
		{
			verifyCatch(catchClause, node, method);
		}
		
		return super.visit(node);
	}
	
	private void verifyRaise(ThrowStatement node, MethodDeclaration method)
	{
		String methodName = method.resolveBinding().getDeclaringClass().getQualifiedName() + "."
				+ method.getName().toString();

		Compartment compartment = Verifier.getInstance().findCompartment(methodName);

		if (compartment != null)
		{
			Marker m = new Marker();
			m.setFirstIndex(node.getStartPosition());
			m.setLastIndex(node.getStartPosition() + node.getLength());
			
			List<String> exceptions = new ArrayList<>();
			exceptions.add(node.getExpression().resolveTypeBinding().getName());
			Verifier.getInstance().checkRaiseViolation(compartment, exceptions, methodName, m);
		}
	}
	
	private void verifyCatch(CatchClause catchClause, ThrowStatement node, MethodDeclaration method)
	{		
		//System.out.println(catchClause.getException().getType());
		//System.out.println(node.getExpression());
		
		CatchClauseVisitor visitor = new CatchClauseVisitor();
		catchClause.accept(visitor);
		
		//System.out.println("ClassInstanceCreation: " + visitor.getType());
		
		//melhorar match usando equals dos tipos de node e cathClause
		if (visitor.getType() != null && visitor.getType().equals(catchClause.getException().getType().toString()))
		{
			String methodName = method.resolveBinding().getDeclaringClass().getQualifiedName() + "."
					+ method.getName().toString();

			Compartment compartment = Verifier.getInstance().findCompartment(methodName);
			
			if (compartment != null)
			{
				Marker m = new Marker();
				m.setFirstIndex(node.getStartPosition());
				m.setLastIndex(node.getStartPosition() + node.getLength());
				
				List<String> exceptions = new ArrayList<>();
				exceptions.add(node.getExpression().resolveTypeBinding().getName());
				
				Verifier.getInstance().checkRethrowViolation(compartment, exceptions, methodName, m);
			}
		}
	}
}
