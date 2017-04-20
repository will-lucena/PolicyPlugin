package excite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor
{	
	@Override
	public boolean visit(MethodDeclaration node)
	{
		verificarMethod(node);
		
		MethodVisitor visitor = new MethodVisitor();
		node.accept(visitor);
		
		return super.visit(node);
	}
	
	private void verificarMethod(MethodDeclaration node)
	{
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." + node.getName().toString();
		
		//descobrir compartimento
		Compartment compartment = Verifier.getInstance().findCompartment(methodName);
		if (compartment != null)
		{
			//pegar exceções declaradas
			List<String> exceptions = getExcecptionTypes(node);
			if (exceptions != null)
			{
				//validar method
				Verifier.getInstance().checkPropagateViolation(compartment, exceptions, methodName);
			}
		}
	}
	
	private List<String> getExcecptionTypes(MethodDeclaration node)
	{
		List<String> thrownExceptions = new ArrayList<>();
		
		for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
		{
			SimpleType exceptionType = (SimpleType) iter.next();
			thrownExceptions.add(exceptionType.getName().toString());
		}
		return thrownExceptions;
	}
}
