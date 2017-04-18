package excite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import epl.model.Compartment;
import epl.model.Rule;
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
					checkRaiseViolation(compartment, exceptions, methodName);
				}
			}
		}
		return super.visit(node);
	}

	private void checkRaiseViolation(Compartment compartment, List<String> exceptions, String methodName)
	{
		Rule cannotViolation = AplicacaoJar.verifyCannotRule(compartment, exceptions, DependencyType.Raise);
		Rule onlyMayViolation = AplicacaoJar.verifyOnlyMayRule(compartment, exceptions, DependencyType.Raise);
		Rule mayOnlyViolation = AplicacaoJar.verifyMayOnlyRule(compartment, exceptions, DependencyType.Raise);
		
		if (cannotViolation != null)
		{
			//*
			Violation v = new Violation(methodName, cannotViolation);
			AplicacaoJar.setViolation(v);
			/**/
		}
		if (onlyMayViolation != null)
		{
			//*
			Violation v = new Violation(methodName, onlyMayViolation);
			AplicacaoJar.setViolation(v);
			/**/
		}
		if (mayOnlyViolation != null)
		{
			//*
			Violation v = new Violation(methodName, mayOnlyViolation);
			AplicacaoJar.setViolation(v);
			/**/
		}
	}
}
