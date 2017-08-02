package br.ufrn.imd.domain.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.RuleType;
import epl.util.EPLConstants;
import policiesplugin.handlers.Application;
import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.verifiers.MustWildcarVerifier;
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
		
		checkMustWildcard(node);
		
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
	
	private void checkMustWildcard(MethodDeclaration node)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (method.getCompartment() != null && rule.getRuleType().equals(RuleType.Must) && rule.getCompartment().getId().equals(method.getCompartment().getId()))
			{
				if (rule.getExceptionExpressions().get(0).equals(EPLConstants.WILDCARD_REPLACEMENT_REGEX))
				{
					MustWildcarVerifier.getInstance().checkViolation(method, Controller.prepareMarker(node));
				}
			}
		}
	}
}
