package excite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;
import epl.model.Policy;
import policiesplugin.handlers.ConsumirEpl;
import policiesplugin.handlers.ValidarCodigo;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor {
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// esse método pertence a qual compartimento?
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." + node.getName().toString();		
		
		Policy p = ConsumirEpl.getPolicy();
		
 		for (Compartment c : p.getCompartments())
		{
			for (String ex : c.getExpressions())
			{
				if (methodName.matches(ex))
				{
					System.out.println("Deu match");
					
					List<String> thrownExceptions = new ArrayList<>();
					
					for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
					{
						SimpleType exceptionType = (SimpleType) iter.next();
						thrownExceptions.add(exceptionType.getName().toString());
					}
	
					//validarCompartmento (validarCodigo.searchPropagateViolation(c, thrownExceptions);
					
					MethodVisitor visitor = new MethodVisitor();
					// visitor -> visitador de declarações de método
					node.accept(visitor);
				}
			}
		}	
		methods.add(node);
		return super.visit(node);
	}

	//*
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	/**/
}
