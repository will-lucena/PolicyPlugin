package excite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import epl.model.Compartment;
import epl.model.Policy;
import policiesplugin.handlers.ConsumirEpl;

// visitador de compilation unit
public class CompilationUnitVisitor extends ASTVisitor {
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// esse m�todo pertence a qual compartimento?
		String methodName = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." + node.getName().toString();		
		
		Policy p = ConsumirEpl.getPolicy();
		
 		for (Compartment c : p.getCompartments())
		{
			for (String ex : c.getExpressions())
			{
				if (methodName.matches(ex))
				{
					System.out.println("Deu match");
					MethodVisitor visitor = new MethodVisitor();
					// visitor -> visitador de declara��es de m�todo
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
