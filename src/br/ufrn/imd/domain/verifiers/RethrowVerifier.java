package br.ufrn.imd.domain.verifiers;

import java.util.List;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule.DependencyType;
import br.ufrn.imd.domain.Marker;

public class RethrowVerifier extends Verifier
{
	private static RethrowVerifier instance = null;
	
	private RethrowVerifier()
	{
		
	}

	public static RethrowVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (RethrowVerifier.class)
			{
				if (instance == null)
				{
					instance = new RethrowVerifier();
				}
			}
		}
		return instance;
	}
	
	@Override
	public void checkViolation(Method method, Marker marker)
	{
		check(method.getCompartment(), method.getExceptionsRethrown(), method.getFullyQualifiedName(), marker);
	}
	
	private void check(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
 		verifyCannotRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Rethrow);
		verifyOnlyMayRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Rethrow);
		verifyMayOnlyRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Rethrow);
		verifyMustRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Rethrow);
	}
}
