package br.ufrn.imd.domain.verifiers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule.DependencyType;
import br.ufrn.imd.domain.Marker;

public class PropagateVerifier extends Verifier
{
	private static PropagateVerifier instance = null;
	
	private PropagateVerifier()
	{

	}

	public static PropagateVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (PropagateVerifier.class)
			{
				if (instance == null)
				{
					instance = new PropagateVerifier();
				}
			}
		}
		return instance;
	}
		
	public Method getPropagatedExceptions(MethodDeclaration node, Method method, Marker marker)
	{
		for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
		{		
			SimpleType exceptionType = (SimpleType) iter.next();	
			method.addExceptionPropagated(new JavaType(exceptionType.resolveBinding().getName()));
			marker.setLastIndex(exceptionType.getStartPosition() + exceptionType.getLength());
		}
		
		return method;
	}
	
	@Override
	public void checkViolation(Method method, Marker marker)
	{
		check(method.getCompartment(), method.getExceptionsPropagated(), method.getFullyQualifiedName(), marker);
	}
	
	private void check(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
 		verifyCannotRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Propagate);
		verifyOnlyMayRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Propagate);
		verifyMayOnlyRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Propagate);
		verifyMustRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Propagate);
	}
}
