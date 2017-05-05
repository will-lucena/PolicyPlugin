package excite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.Compartment;
import epl.model.Method;
import excite.verifiers.PropagateVerifier;
import excite.verifiers.Verifier;

public class MethodDeclarationVisitor extends ASTVisitor
{
	private Method method = new Method();
	private Marker marcador = new Marker();

	@Override
	public boolean visit(MethodDeclaration node)
	{
		// novo
		this.method.setName(node.getName().toString());
		this.method.setClassName(node.resolveBinding().getDeclaringClass().getName().toString());
		this.method.setPackageName(node.resolveBinding().getDeclaringClass().getPackage().getName().toString());

		this.marcador.setFirstIndex(node.getStartPosition());

		PropagateVerifier verifier = new PropagateVerifier();

		this.method.setCompartment(verifier.getCompartment(this.method.getFullyQualifiedName()));
		this.method = verifier.getPropagatedExceptions(node, method, marcador);

		if (this.method.getCompartment() != null)
		{
			this.method.getCompartment().addMethod(this.method);
			AplicacaoJar.updateCompartment(this.method.getCompartment());
		}

		//verifier.checkPropagateViolation(this.method, this.marcador);

		// antigo
		verificarMethodDeclaration(node);

		// fixo
		return super.visit(node);
	}

	private void verificarMethodDeclaration(MethodDeclaration node)
	{
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "."
				+ node.getName().toString();

		// descobrir compartimento
		Compartment compartment = Verifier.getInstance().findCompartment(methodName);
		if (compartment != null)
		{
			// pegar exceções declaradas
			Marker m = new Marker();
			List<String> exceptions = getExcecptionTypes(node, m);
			if (exceptions != null)
			{
				// validar method
				m.setFirstIndex(node.getStartPosition());
				Verifier.getInstance().checkPropagateViolation(compartment, exceptions, methodName, m);
			}
		}
	}

	private List<String> getExcecptionTypes(MethodDeclaration node, Marker marcador)
	{
		List<String> thrownExceptions = new ArrayList<>();

		for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
		{
			SimpleType exceptionType = (SimpleType) iter.next();
			thrownExceptions.add(exceptionType.getName().toString());
			marcador.setLastIndex(exceptionType.getStartPosition() + exceptionType.getLength());
		}

		return thrownExceptions;
	}

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
		// System.out.println(catchClause.getException().getType());
		// System.out.println(node.getExpression());

		CatchClauseVisitor visitor = new CatchClauseVisitor();
		catchClause.accept(visitor);

		// *
		if (visitor.getType() != null)
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
				// index 0 = exception thrown
				// index 1 = exception catched
				System.out.println("Exception thrown: " + visitor.getType());
				System.out.println("Exception catched: " + catchClause.getException().getType().toString());
				exceptions.add(0, visitor.getType());
				exceptions.add(1, catchClause.getException().getType().toString());

				Verifier.getInstance().checkRemapViolation(compartment, exceptions, methodName, m);
			}
		}
		/**/

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
