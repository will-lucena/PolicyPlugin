package br.ufrn.imd.domain.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

public class InstanceCreatorVisitor extends ASTVisitor
{
	private String classInstanceCreationType = null;

	@Override
	public boolean visit(ClassInstanceCreation node)
	{
		this.classInstanceCreationType = node.getType().resolveBinding().getName();
		return super.visit(node);
	}
	
	public String getType()
	{
		return this.classInstanceCreationType;
	}
}
