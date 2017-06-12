package excite;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor
{	
	@Override
	public boolean visit(TypeDeclaration node)
	{
		MethodDeclarationVisitor mdVisitor = new MethodDeclarationVisitor();
		node.accept(mdVisitor);
		
		return super.visit(node);
	}
}
