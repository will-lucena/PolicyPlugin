package excite;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import epl.model.Method;
import excite.verifiers.PropagateVerifier;
import excite.verifiers.Verifier;

public class MethodDeclarationVisitor extends ASTVisitor
{
	private Method method;
	
	public MethodDeclarationVisitor(Method method)
	{
		this.method = method;
	}
	
	public Method updateMethod()
	{
		return this.method;
	}

	@Override
	public boolean visit(MethodDeclaration node)
	{
		this.method.setName(node.getName().toString());
		this.method.setClassName(node.resolveBinding().getDeclaringClass().getName().toString());
		this.method.setPackageName(node.resolveBinding().getDeclaringClass().getPackage().getName().toString());
		this.method.setCompartment(Verifier.getInstance().findCompartment(this.method.getFullyQualifiedName()));
		
		Marker marcador = AplicacaoJar.prepareMarker(node);
		this.method = PropagateVerifier.getInstance().getPropagatedExceptions(node, this.method, marcador);

		if (this.method.getCompartment() != null)
		{
			this.method.getCompartment().addMethod(this.method);
			AplicacaoJar.updateCompartment(this.method.getCompartment());
		}
		PropagateVerifier.getInstance().checkPropagateViolation(this.method, marcador);
		
		return super.visit(node);
	}
}
