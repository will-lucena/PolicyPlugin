package br.ufrn.imd.domain.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CompilationUnitVisitor extends ASTVisitor
{	
	@Override
	public boolean visit(TypeDeclaration node)
	{
		MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor();
		node.accept(methodDeclarationVisitor);
		
		return super.visit(node);
	}
}
