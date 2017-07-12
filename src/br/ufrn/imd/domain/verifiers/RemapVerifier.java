package br.ufrn.imd.domain.verifiers;

import epl.model.Compartment;
import epl.model.ExceptionPair;
import epl.model.Method;
import epl.model.Rule.DependencyType;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.criteria.CannotCriteria;
import br.ufrn.imd.domain.criteria.MayOnlyCriteria;
import br.ufrn.imd.domain.criteria.MustCriteria;
import br.ufrn.imd.domain.criteria.OnlyMayCriteria;

public class RemapVerifier extends Verifier
{
	private static RemapVerifier instance = null;

	private RemapVerifier()
	{

	}

	public static RemapVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (RemapVerifier.class)
			{
				if (instance == null)
				{
					instance = new RemapVerifier();
				}
			}
		}
		return instance;
	}
	
	@Override
	public void checkViolation(Method method, Marker marker)
	{
		check(method.getCompartment(), method.getExceptionsRemapped(), method.getFullyQualifiedName(), marker);
	}
	
	private void check(Compartment compartment, List<ExceptionPair> pairs, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
		
		List<String> expressions = new ArrayList<>();

		for (ExceptionPair pair : pairs)
		{
			expressions.add(buildExpression(pair));
		}
		
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Remap, CannotCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Remap, OnlyMayCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Remap, MayOnlyCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Remap, MustCriteria.getInstance());
	}
	
	private String buildExpression(ExceptionPair pair)
	{
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("from ");
		stringBuilder.append(pair.getFrom().toString());
		stringBuilder.append(" to ");
		stringBuilder.append(pair.getTo().toString());

		return stringBuilder.toString();
	}
}
