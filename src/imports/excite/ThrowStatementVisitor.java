package excite;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.ExceptionPair;
import epl.model.JavaType;
import epl.model.Method;
import excite.verifiers.PropagateVerifier;
import excite.verifiers.RaiseVerifier;
import excite.verifiers.RemapVerifier;
import excite.verifiers.RethrowVerifier;
import excite.verifiers.Verifier;

public class ThrowStatementVisitor extends ASTVisitor
{
	private Method method;
	private boolean isRaise;
	
	public ThrowStatementVisitor(Method method)
	{
		this.method = method;
		this.isRaise = true;
	}
	
	public Method updateMethod()
	{
		return this.method;
	}
	
	public boolean isRaise()
	{
		return this.isRaise;
	}
	
	@Override
	public boolean visit(ThrowStatement node)
	{
		ASTNode parent = node.getParent();
		this.isRaise = true;
		
		while (!(parent instanceof MethodDeclaration))
		{
			parent = parent.getParent();

			if (parent instanceof CatchClause)
			{
				this.isRaise = false;				
				verifyCatch((CatchClause) parent, node);
				break;
			}
		}

		if (this.isRaise)
		{
			if (this.method != null)
			{
				verifyRaise(node);
			}
		}

		return super.visit(node);
	}
	
	private void verifyCatch(CatchClause catchClause, ThrowStatement throwStatement)
	{
		this.method.setCompartment(Verifier.getInstance().findCompartment(this.method.getFullyQualifiedName()));
		InstanceCreatorVisitor visitor = new InstanceCreatorVisitor();
		catchClause.getBody().accept(visitor);

		String from = catchClause.getException().getType().resolveBinding().getName();
		String to = visitor.getType();

		if (from.equals(to))
		{
			// isRethrow
			this.method.addExceptionRethrown(new JavaType(to));
			Marker marcador = AplicacaoJar.prepareMarker(throwStatement);
			RethrowVerifier.getInstance().checkRethrowViolation(this.method, marcador);
		} else
		{
			// isRemap
			ExceptionPair pair = new ExceptionPair(new JavaType(from), new JavaType(to));
			this.method.addExceptionRemapped(pair);
			Marker marcador = AplicacaoJar.prepareMarker(throwStatement);
			RemapVerifier.getInstance().checkRemapViolation(this.method, marcador);
		}
	}
	
	private void verifyRaise(ThrowStatement node)
	{
		if (this.method.getCompartment() != null)
		{
			Marker marcador = AplicacaoJar.prepareMarker(node);
			this.method = RaiseVerifier.getInstance().getRaisedExceptions(node, this.method);
			RaiseVerifier.getInstance().checkRaiseViolation(this.method, marcador);	
		}
	}	
}
