package excite;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;


// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor
{		
	@Override
	public boolean visit(TypeDeclaration node)
	{
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		
		node.accept(visitor);
		
		return super.visit(node);
	}
}
