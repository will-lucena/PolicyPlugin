package excite;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class CatchClauseVisitor extends ASTVisitor
{
	private String classInstanceCreationType = null;

	@Override
	public boolean visit(ClassInstanceCreation node)
	{
		if (verifyRethrow(node))
		{
			this.classInstanceCreationType = node.getType().toString();
		}
		return super.visit(node);
	}

	private boolean verifyRethrow(ClassInstanceCreation node)
	{
		ASTNode parent = node.getParent();

		while (!(parent instanceof CatchClause))
		{
			if (parent instanceof ThrowStatement)
			{
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	public String getType()
	{
		return this.classInstanceCreationType;
	}
}
