package excite;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

import epl.model.JavaType;
import epl.model.Method;
import excite.verifiers.HandleVerifier;
import excite.verifiers.Verifier;


public class CatchClauseVisitor extends ASTVisitor
{
	private Method method;
	private boolean isHandle;

	public CatchClauseVisitor(Method method)
	{
		this.method = method;
	}

	public Method updateMethod()
	{
		return this.method;
	}

	@Override
	public boolean visit(CatchClause node)
	{
		this.method.setCompartment(Verifier.getInstance().findCompartment(this.method.getFullyQualifiedName()));
		InstanceCreatorVisitor icVisitor = new InstanceCreatorVisitor();
		node.getBody().accept(icVisitor);
		
		String exCatched = icVisitor.getType();
		ThrowStatementVisitor tsVisitor = new ThrowStatementVisitor(this.method);
		node.getBody().accept(tsVisitor);
		this.isHandle = tsVisitor.isRaise();
		
		if (isHandle)
		{
			Marker marcador = AplicacaoJar.prepareMarker(node);
			this.method.addExceptionHandled(new JavaType(exCatched));
			HandleVerifier.getInstance().checkHandleViolation(this.method, marcador);
		}
		
		return super.visit(node);
	}
}
