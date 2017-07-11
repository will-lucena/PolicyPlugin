package br.ufrn.imd.domain.verifiers;

import java.util.List;

import org.eclipse.jdt.core.dom.ThrowStatement;

import br.ufrn.imd.domain.visitors.InstanceCreatorVisitor;
import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule.DependencyType;
import br.ufrn.imd.domain.Marker;

public class RaiseVerifier extends Verifier
{
	private static RaiseVerifier instance = null;
	
	private RaiseVerifier()
	{
		
	}

	public static RaiseVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (RaiseVerifier.class)
			{
				if (instance == null)
				{
					instance = new RaiseVerifier();
				}
			}
		}
		return instance;
	}
	
	public Method getRaisedExceptions(ThrowStatement node, Method method)
	{
		InstanceCreatorVisitor visitor = new InstanceCreatorVisitor();
		node.accept(visitor);
		String exceptionType = visitor.getType();
		method.addExceptionRaised(new JavaType(exceptionType));
		
		return method;
	}
	
	@Override
	public void checkViolation(Method method, Marker marker)
	{
		check(method.getCompartment(), method.getExceptionsRaised(), method.getFullyQualifiedName(), marker);
	}
	
	private void check(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
 		verifyCannotRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Raise);
		verifyOnlyMayRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Raise);
		verifyMayOnlyRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Raise);
		verifyMustRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Raise);
	}
}
