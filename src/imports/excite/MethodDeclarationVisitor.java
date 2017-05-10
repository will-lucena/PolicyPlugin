package excite;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import epl.model.Method;
import excite.verifiers.PropagateVerifier;

public class MethodDeclarationVisitor extends ASTVisitor
{
	private Method method;

	@Override
	public boolean visit(MethodDeclaration node)
	{
		this.method = new Method();
		Marker marcador = new Marker();
		this.method.setName(node.getName().toString());
		this.method.setClassName(node.resolveBinding().getDeclaringClass().getName().toString());
		this.method.setPackageName(node.resolveBinding().getDeclaringClass().getPackage().getName().toString());

		marcador.setFirstIndex(node.getStartPosition());

		this.method.setCompartment(PropagateVerifier.getInstance().getCompartment(this.method.getFullyQualifiedName()));
		this.method = PropagateVerifier.getInstance().getPropagatedExceptions(node, this.method, marcador);

		if (this.method.getCompartment() != null)
		{
			this.method.getCompartment().addMethod(this.method);
			AplicacaoJar.updateCompartment(this.method.getCompartment());
		}
	
		PropagateVerifier.getInstance().checkPropagateViolation(this.method, marcador);
		
		ThrowStatementVisitor tsVisitor = new ThrowStatementVisitor(this.method);
		node.accept(tsVisitor);
		this.method = tsVisitor.updateMethod();
		
		return super.visit(node);
	}
}
