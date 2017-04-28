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
			Marker m = new Marker();
			List<String> exceptions = getExcecptionTypes(node, m);
			if (exceptions != null)
			{
				//validar method
				m.setStartPosition(node.getStartPosition());
				Verifier.getInstance().checkPropagateViolation(compartment, exceptions, methodName, m);
			}
		}
	}
	
	private List<String> getExcecptionTypes(MethodDeclaration node, Marker marcador)
	{
		List<String> thrownExceptions = new ArrayList<>();
		
		for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
		{
			SimpleType exceptionType = (SimpleType) iter.next();			
			thrownExceptions.add(exceptionType.getName().toString());
			marcador.setLength(exceptionType.getStartPosition() + exceptionType.getLength());
		}
		
		return thrownExceptions;
	}
}
