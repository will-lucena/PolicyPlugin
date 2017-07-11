package br.ufrn.imd.domain.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

import epl.model.JavaType;
import epl.model.Method;
import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.verifiers.HandleVerifier;

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
		checkMethod(node);
		
		return super.visit(node);
	}
	
	private void checkMethod(CatchClause node)
	{
		InstanceCreatorVisitor instanceCreatorVisitor = new InstanceCreatorVisitor();
		node.getBody().accept(instanceCreatorVisitor);
		
		String exceptionCaught = instanceCreatorVisitor.getType();
		ThrowStatementVisitor throwStatementVisitor = new ThrowStatementVisitor(this.method);
		node.getBody().accept(throwStatementVisitor);
		
		this.isHandle = throwStatementVisitor.isRaise();
		
		if (isHandle)
		{
			Marker marker = Controller.prepareMarker(node);
			this.method.addExceptionHandled(new JavaType(exceptionCaught));
			HandleVerifier.getInstance().checkHandleViolation(this.method, marker);
		}
	}
}
