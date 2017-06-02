package excite;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import epl.model.ExceptionPair;
import epl.model.JavaType;
import epl.model.Method;


// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor
{	
	private Method method;
	
	@Override
	public boolean visit(TypeDeclaration node)
	{
		method = new Method();
		
		MethodDeclarationVisitor mdVisitor = new MethodDeclarationVisitor(method);
		node.accept(mdVisitor);
		method = mdVisitor.updateMethod();
		
		CatchClauseVisitor ccVisitor = new CatchClauseVisitor(method);
		node.accept(ccVisitor);	
		method = ccVisitor.updateMethod();
		
		ThrowStatementVisitor tsVisitor = new ThrowStatementVisitor(method);
		node.accept(tsVisitor);
		method = tsVisitor.updateMethod();
		
		System.out.println("Handled");
		for (JavaType s : method.getExceptionsHandled())
		{
			System.out.println(s.getFullyQualifiedName());
		}
		
		System.out.println("Propagated");
		for (JavaType s : method.getExceptionsPropagated())
		{
			System.out.println(s.getFullyQualifiedName());
		}
		
		System.out.println("Raised");
		for (JavaType s : method.getExceptionsRaised())
		{
			System.out.println(s.getFullyQualifiedName());
		}
		
		System.out.println("Remapped");
		for (ExceptionPair s : method.getExceptionsRemapped())
		{
			System.out.println("from: " + s.getFrom() + "\tto: " + s.getTo());
		}
		
		System.out.println("Rethrown");
		for (JavaType s : method.getExceptionsRethrown())
		{
			System.out.println(s.getFullyQualifiedName());
		}
		return super.visit(node);
	}
}
