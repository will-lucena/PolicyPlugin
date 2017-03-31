package excite;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import policiesplugin.handlers.ConsumirEpl;

public class MethodVisitor extends ASTVisitor
{
	@Override
	public boolean visit(ThrowStatement node)
	{
		ASTNode parent = node.getParent();
		boolean isRaise = true;
		
		String exception = node.getExpression().resolveTypeBinding().getName();
		
		MethodDeclaration method = null;
		
		while (!(parent instanceof MethodDeclaration))
		{
			parent = parent.getParent();
			
			if (parent instanceof MethodDeclaration)
			{
				method = (MethodDeclaration) parent;
			}
			
			if (parent instanceof TryStatement)
			{
				isRaise = false;
			}
		}
		
		if (isRaise)
		{
			if (method != null)
			{
				String methodName = method.resolveBinding().getDeclaringClass().getQualifiedName() + "." + method.getName().toString();
				checkMethod(methodName, exception);
			}
			else
			{
				System.out.println("null");
			}
		}
		
		// TODO Validação das regras raise
		return super.visit(node);
	}
	
	public void checkMethod(String methodName, String exception)
	{
		for (Compartment c : ConsumirEpl.getPolicy().getCompartments())
		{
			for (String ex : c.getExpressions())
			{
				if (methodName.matches(ex))
				{
					if (searchRaiseViolation(c, exception))
					{
						AplicacaoJar.addcompartmentsWithRaiseViolation(c.getId());
					}
				}
			}
		}
	}
	
	private boolean searchRaiseViolation(Compartment compartment, String exception)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getDependencyType().equals(DependencyType.Raise))
			{
				if (r.getCompartmentId().equals(compartment.getId()))
				{
					if (r.getExceptionExpressions().contains(exception))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
