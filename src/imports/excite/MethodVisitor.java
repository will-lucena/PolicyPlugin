package excite;



import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class MethodVisitor extends ASTVisitor
{
	@Override
	public boolean visit(ThrowStatement node)
	{
		ASTNode parent = node.getParent();
		while (parent instanceof MethodDeclaration)
		{
			parent = parent.getParent();
		}
		// TODO Validação das regras raise
		return super.visit(node);
	}
}
