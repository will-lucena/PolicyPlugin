package excite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import epl.model.Compartment;
import epl.model.Rule.DependencyType;

public class MethodVisitor extends ASTVisitor
{
	@Override
	public boolean visit(ThrowStatement node)
	{
		boolean isRaise = true;
		MethodDeclaration method = null;
		ASTNode parent = node.getParent();

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
			if (method instanceof MethodDeclaration)
			{
				List<String> exceptions = new ArrayList<>();
				exceptions.add(node.getExpression().resolveTypeBinding().getName());
				String methodName = method.resolveBinding().getDeclaringClass().getQualifiedName() + "."
						+ method.getName().toString();

				Compartment compartment = AplicacaoJar.findCompartment(methodName);

				if (compartment != null)
				{
					String rule = checkRaiseViolation(compartment, exceptions);
					if (rule != null)
					{
						AplicacaoJar.setViolation(new Violation(methodName, rule));
					}
				}
			}
		}
		return super.visit(node);
	}

	private String checkRaiseViolation(Compartment compartment, List<String> exceptions)
	{
		return AplicacaoJar.verifyCannotRule(compartment, exceptions, DependencyType.Raise);
	}
}
