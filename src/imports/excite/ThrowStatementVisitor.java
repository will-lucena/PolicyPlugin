package excite;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.Method;
import excite.verifiers.RaiseVerifier;

public class ThrowStatementVisitor extends ASTVisitor
{
	private Method method;
	
	public ThrowStatementVisitor(Method method)
	{
		this.method = method;
	}
	
	public Method updateMethod()
	{
		return this.method;
	}
	
	@Override
	public boolean visit(ThrowStatement node)
	{
		boolean isRaise = true;
		ASTNode parent = node.getParent();

		while (!(parent instanceof MethodDeclaration))
		{
			parent = parent.getParent();

			if (parent instanceof CatchClause)
			{
				isRaise = false;
				
				CatchClauseVisitor visitor = new CatchClauseVisitor(this.method);
				node.accept(visitor);	
				this.method = visitor.updateMethod();
				break;
			}
		}

		if (isRaise)
		{
			if (this.method != null)
			{
				verifyRaise(node);
			}
		}

		return super.visit(node);
	}
	
	private void verifyRaise(ThrowStatement node)
	{
		if (this.method.getCompartment() != null)
		{
			Marker marcador = new Marker();
			marcador.setFirstIndex(node.getStartPosition());
			marcador.setLastIndex(node.getStartPosition() + node.getLength());
			
			this.method = RaiseVerifier.getInstance().getRaisedExceptions(node, this.method);
			
			RaiseVerifier.getInstance().checkRaiseViolation(this.method, marcador);	
		}
	}
}
