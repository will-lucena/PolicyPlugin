package br.ufrn.imd.domain.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import epl.model.Method;
import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.verifiers.PropagateVerifier;

public class MethodDeclarationVisitor extends ASTVisitor
{
	private Method method;
	
	public MethodDeclarationVisitor()
	{
		
	}
	
	public Method updateMethod()
	{
		return this.method;
	}

	@Override
	public boolean visit(MethodDeclaration node)
	{
		this.method = new Method();
		
		getMethodInfos(node);
		checkMethod(node);
		
		CatchClauseVisitor catchClauseVisitor = new CatchClauseVisitor(method);
		node.accept(catchClauseVisitor);	
		method = catchClauseVisitor.updateMethod();
		
		return super.visit(node);
	}
	
	private void getMethodInfos(MethodDeclaration node)
	{
		this.method.setName(node.getName().toString());
		this.method.setClassName(node.resolveBinding().getDeclaringClass().getName().toString());
		this.method.setPackageName(node.resolveBinding().getDeclaringClass().getPackage().getName().toString());
		this.method.setCompartment(PropagateVerifier.getInstance().findCompartment(this.method.getFullyQualifiedName()));
	}
	
	private void checkMethod(MethodDeclaration node)
	{
		Marker marker = Controller.prepareMarker(node);
		this.method = PropagateVerifier.getInstance().getPropagatedExceptions(node, this.method, marker);

		if (this.method.getCompartment() != null)
		{
			this.method.getCompartment().addMethod(this.method);
			Controller.updateCompartment(this.method.getCompartment());
		}
		PropagateVerifier.getInstance().checkViolation(this.method, marker);
	}
}
