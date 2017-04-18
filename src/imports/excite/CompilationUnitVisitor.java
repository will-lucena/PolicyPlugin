package excite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;
import epl.model.Rule.DependencyType;
import policiesplugin.handlers.ConsumirEpl;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor
{	
	@Override
	public boolean visit(MethodDeclaration node)
	{
		// esse m�todo pertence a qual compartimento?
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." 
				+ node.getName().toString();

		Compartment compartment = findCompartment(methodName);
		if (compartment != null)
		{
			List<String> exceptions = getExcecptionTypes(node);
			if (exceptions != null)
			{
				String violation = checkPropagateViolation(compartment, exceptions);
				if (violation != null)
				{
					Violation v = new Violation(methodName, violation);
					AplicacaoJar.setViolation(v);
				}
			}
		}
		MethodVisitor visitor = new MethodVisitor();
		// visitor -> visitador de declara��es de m�todo
		node.accept(visitor);
		return super.visit(node);
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

	private Compartment findCompartment(String methodName)
	{
		for (Compartment c : ConsumirEpl.getPolicy().getCompartments())
		{
			for (String method : c.getExpressions())
			{
				if (methodName.matches(method))
				{
					return c;
				}
			}
		}
		return null;
	}
	
	private String checkPropagateViolation(Compartment compartment, List<String> exceptions)
	{
		return AplicacaoJar.searchViolation(compartment, exceptions, DependencyType.Propagate);			
	}
}
