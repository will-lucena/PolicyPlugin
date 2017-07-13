package br.ufrn.imd.domain.verifiers;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.criteria.MustCriteria;
import br.ufrn.imd.domain.criteria.RuleCriteria;
import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import policiesplugin.handlers.Application;

public class MustWildcarVerifier extends Verifier
{
	private static MustWildcarVerifier instance = null;

	private MustWildcarVerifier()
	{

	}

	public static MustWildcarVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (MustWildcarVerifier.class)
			{
				if (instance == null)
				{
					instance = new MustWildcarVerifier();
				}
			}
		}
		return instance;
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

		List<String> expressions = new ArrayList<>();

		for (JavaType exception : exceptions)
		{
			expressions.add(exception.toString());
		}

		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Handle, MustCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Propagate, MustCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Raise, MustCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Remap, MustCriteria.getInstance());
		verifyRule(compartment, expressions, new Marker(fIndex, lIndex), DependencyType.Rethrow, MustCriteria.getInstance());
	}

	@Override
	protected void verifyRule(Compartment compartment, List<String> expressions, Marker marker, DependencyType dependencyType, RuleCriteria criteria)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (verifyRule(rule, criteria.getRuleType(), dependencyType) && criteria.compartmentCriteria(compartment, rule))
			{
				if (expressions.isEmpty())
				{
					String ruleFormated = String.format("%s %s %s %s", rule.getCompartmentId(), rule.getRuleType(), rule.getDependencyType(), "*");
					marker.setRule(ruleFormated);
					Controller.addMarker(marker);
				}
			}
		}
	}

}
