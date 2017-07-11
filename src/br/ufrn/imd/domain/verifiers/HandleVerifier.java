package br.ufrn.imd.domain.verifiers;

import java.util.List;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule.DependencyType;
import br.ufrn.imd.domain.Marker;

public class HandleVerifier extends Verifier
{
	private static HandleVerifier instance = null;

	private HandleVerifier()
	{

	}

	public static HandleVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (HandleVerifier.class)
			{
				if (instance == null)
				{
					instance = new HandleVerifier();
				}
			}
		}
		return instance;
	}

	@Override
	public void checkViolation(Method method, Marker marker)
	{
		check(method.getCompartment(), method.getExceptionsHandled(), method.getFullyQualifiedName(), marker);
	}
	
	private void check(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
 		verifyCannotRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Handle);
		verifyOnlyMayRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Handle);
		verifyMayOnlyRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Handle);
		verifyMustRule(compartment, exceptions, new Marker(fIndex, lIndex), DependencyType.Handle);
	}	
}
