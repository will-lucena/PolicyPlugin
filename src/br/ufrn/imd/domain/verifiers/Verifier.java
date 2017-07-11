package br.ufrn.imd.domain.verifiers;

import java.util.List;

import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import epl.model.Compartment;
import epl.model.JavaType;
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
	
	protected void verifyCannotRule(Compartment compartment, List<JavaType> exceptions, Marker marker, DependencyType type)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Cannot) && rule.getDependencyType().equals(type))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return;
						}
					}
				}
			}
		}
	}
	
	protected void verifyMustRule(Compartment compartment, List<JavaType> exceptions, Marker marker, DependencyType type)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Must) && rule.getDependencyType().equals(type))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
						{
							return;
						}
					}
					marker.setRule(rule.toString());
					Controller.addMarker(marker);
					return;
				}
			}
		}
	}

	protected void verifyOnlyMayRule(Compartment compartment, List<JavaType> exceptions, Marker marker, DependencyType type)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.OnlyMay) && rule.getDependencyType().equals(type))
			{
				if (compartment != null && !rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return;
						}
					}
				}
			}
		}
	}

	protected void verifyMayOnlyRule(Compartment compartment, List<JavaType> exceptions, Marker marker, DependencyType type)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.MayOnly) && rule.getDependencyType().equals(type))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (!rule.getExceptionExpressions().contains(exception.toString()))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
						}
					}
				}
			}
		}
	}
}
