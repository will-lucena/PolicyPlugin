package excite;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.ExceptionPair;
import epl.model.JavaType;
import epl.model.Method;
import excite.verifiers.RemapVerifier;
import excite.verifiers.RethrowVerifier;

public class CatchClauseVisitor extends ASTVisitor
{
	private Method method;
	
	public CatchClauseVisitor(Method method)
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
		ASTNode parent = node.getParent();
		CatchClause catchClause = null;

		while (!(parent instanceof MethodDeclaration))
		{
			parent = parent.getParent();

			if (parent instanceof CatchClause)
			{	
				catchClause = (CatchClause) parent;
				break;
			}
		}
		
		if (catchClause != null)
		{
			InstanceCreatorVisitor visitor = new InstanceCreatorVisitor();
			catchClause.getBody().accept(visitor);
			
			String from = catchClause.getException().getType().resolveBinding().getName();
			String to = visitor.getType();
			
			if (from.equals(to))
			{
				//isRethrow
				this.method.addExceptionRethrown(new JavaType(to));
				
				Marker marcador = new Marker();
				marcador.setFirstIndex(node.getStartPosition());
				marcador.setLastIndex(node.getStartPosition() + node.getLength());

				RethrowVerifier.getInstance().checkRethrowViolation(this.method, marcador);	
			}
			else
			{
				//isRemap
				ExceptionPair pair = new ExceptionPair(new JavaType(from), new JavaType(to));
				this.method.addExceptionRemapped(pair);
				
				Marker marcador = new Marker();
				marcador.setFirstIndex(node.getStartPosition());
				marcador.setLastIndex(node.getStartPosition() + node.getLength());

				RemapVerifier.getInstance().checkRemapViolation(this.method, marcador);	
			}
		}
		return super.visit(node);
	}
}
