package excite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;
import epl.model.Rule.DependencyType;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor
{	
	@Override
	public boolean visit(MethodDeclaration node)
	{
		// esse m�todo pertence a qual compartimento?
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." 
				+ node.getName().toString();

		Compartment compartment = AplicacaoJar.findCompartment(methodName);
		if (compartment != null)
		{
			List<String> exceptions = getExcecptionTypes(node);
			if (exceptions != null)
			{
				checkPropagateViolation(compartment, exceptions, methodName);
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
	
	private void checkPropagateViolation(Compartment compartment, List<String> exceptions, String methodName)
	{
		String cannotViolation = AplicacaoJar.verifyCannotRule(compartment, exceptions, DependencyType.Propagate);
		String onlyMayViolation = AplicacaoJar.verifyOnlyMayRule(compartment, exceptions, DependencyType.Propagate);
		
		if (cannotViolation != null)
		{
			Violation v = new Violation(methodName, cannotViolation);
			AplicacaoJar.setViolation(v);
		}
		if (onlyMayViolation != null)
		{
			Violation v = new Violation(methodName, onlyMayViolation);
			AplicacaoJar.setViolation(v);
		}
	}
}
