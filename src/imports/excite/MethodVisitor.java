package excite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import epl.model.Compartment;

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
				String methodName = method.resolveBinding().getDeclaringClass().getQualifiedName() + "."
						+ method.getName().toString();

				Compartment compartment = Verifier.getInstance().findCompartment(methodName);

				if (compartment != null)
				{
					Marker m = new Marker();
					m.setStartPosition(node.getStartPosition());
					m.setLength(node.getStartPosition() + node.getLength());
					
					List<String> exceptions = new ArrayList<>();
					exceptions.add(node.getExpression().resolveTypeBinding().getName());
					Verifier.getInstance().checkRaiseViolation(compartment, exceptions, methodName, m);
					
				}
			}
		}
		return super.visit(node);
	}
}
