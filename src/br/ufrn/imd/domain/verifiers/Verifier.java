package br.ufrn.imd.domain.verifiers;

import java.util.List;

import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import br.ufrn.imd.domain.criteria.RuleCriteria;
import epl.model.Compartment;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import policiesplugin.handlers.Application;

public abstract class Verifier
{
	public Compartment findCompartment(String methodName)
	{
		for (Compartment compartment : Application.getPolicy().getCompartments())
		{
			for (String expression : compartment.getExpressions())
			{
				if (methodName.matches(expression))
				{
					return compartment;
				}
			}
		}
		return null;
	}

	public abstract void checkViolation(Method method, Marker marker);

	private boolean verifyRule(Rule rule, RuleType ruleType, DependencyType dependencyType)
	{
		return rule.getRuleType().equals(ruleType) && rule.getDependencyType().equals(dependencyType);
	}

	protected void verifyRule(Compartment compartment, List<String> expressions, Marker marker, DependencyType dependencyType, RuleCriteria criteria)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (verifyRule(rule, criteria.getRuleType(), dependencyType) && criteria.compartmentCriteria(compartment, rule))
			{
				for (String expression : expressions)
				{
					if (criteria.expressionCriteria(rule.getExceptionExpressions(), expression))
					{
						marker.setRule(rule.toString());
						Controller.addMarker(marker);
					}
				}
			}
		}
	}
}
