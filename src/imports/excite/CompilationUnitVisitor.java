package excite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import policiesplugin.handlers.ConsumirEpl;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor {
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// esse método pertence a qual compartimento?
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." + node.getName().toString();		
		
 		for (Compartment c : ConsumirEpl.getPolicy().getCompartments())
		{
			for (String ex : c.getExpressions())
			{
				if (methodName.matches(ex))
				{
					List<String> thrownExceptions = new ArrayList<>();
					
					for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
					{
						SimpleType exceptionType = (SimpleType) iter.next();
						thrownExceptions.add(exceptionType.getName().toString());
					}
	
					//validarCompartmento (validarCodigo.searchPropagateViolation(c, thrownExceptions);
					if (searchPropagateViolation(c, thrownExceptions))
					{
						AplicacaoJar.addcompartmentsWithPropagateViolation(c.getId());
					}
					MethodVisitor visitor = new MethodVisitor();
					// visitor -> visitador de declarações de método
					node.accept(visitor);
				}
			}
		}	
		methods.add(node);
		return super.visit(node);
	}
	
	private boolean searchPropagateViolation(Compartment compartment, List<String> exceptions)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getDependencyType().equals(DependencyType.Propagate))
			{
				if (r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception))
						{
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	//*
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	/**/
}
