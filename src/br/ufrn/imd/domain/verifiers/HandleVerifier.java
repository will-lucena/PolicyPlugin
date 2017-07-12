package br.ufrn.imd.domain.verifiers;

import java.util.ArrayList;
import java.util.List;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule.DependencyType;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.criteria.CannotCriteria;
import br.ufrn.imd.domain.criteria.MayOnlyCriteria;
import br.ufrn.imd.domain.criteria.MustCriteria;
import br.ufrn.imd.domain.criteria.OnlyMayCriteria;

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
		
		List<String> expressions = new ArrayList<>();
		
		for (JavaType exception : exceptions)
		{
			expressions.add(exception.toString());
		}
		
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Handle, CannotCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Handle, OnlyMayCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Handle, MayOnlyCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Handle, MustCriteria.getInstance());
	}	
}
