package br.ufrn.imd.domain.visitors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.ExceptionPair;
import epl.model.JavaType;
import epl.model.Method;
import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.verifiers.RaiseVerifier;
import br.ufrn.imd.domain.verifiers.RemapVerifier;
import br.ufrn.imd.domain.verifiers.RethrowVerifier;

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
		InstanceCreatorVisitor visitor = new InstanceCreatorVisitor();
		catchClause.getBody().accept(visitor);

		String fromExceptionType = catchClause.getException().getType().resolveBinding().getName();
		String toExceptionType = visitor.getType();

		Marker marker = Controller.prepareMarker(throwStatement);
		if (fromExceptionType.equals(toExceptionType))
		{
			// isRethrow
			this.method.addExceptionRethrown(new JavaType(toExceptionType));
			RethrowVerifier.getInstance().checkRethrowViolation(this.method, marker);
		} else
		{
			// isRemap
			ExceptionPair pair = new ExceptionPair(new JavaType(fromExceptionType), new JavaType(toExceptionType));
			this.method.addExceptionRemapped(pair);
			RemapVerifier.getInstance().checkRemapViolation(this.method, marker);
		}
	}
	
	private void verifyRaise(ThrowStatement node)
	{
		if (this.method.getCompartment() != null)
		{
			Marker marker = Controller.prepareMarker(node);
			this.method = RaiseVerifier.getInstance().getRaisedExceptions(node, this.method);
			RaiseVerifier.getInstance().checkRaiseViolation(this.method, marker);	
		}
	}	
}
