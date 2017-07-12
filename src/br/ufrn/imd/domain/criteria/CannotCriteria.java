package br.ufrn.imd.domain.criteria;

import java.util.List;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.RuleType;

public class CannotCriteria implements RuleCriteria
{
	private static CannotCriteria INSTANCE = null;
	private static final RuleType RULE_TYPE = RuleType.Cannot;
	
	private CannotCriteria()
	{
		 
	}
	
	public static CannotCriteria getInstance()
	{
		if (INSTANCE == null)
		{
			synchronized (CannotCriteria.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new CannotCriteria();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public boolean compartmentCriteria(Compartment compartment, Rule rule)
	{
		return compartment != null && rule.getCompartmentId().equals(compartment.getId());
	}

	@Override
	public boolean expressionCriteria(List<String> expressions, String expression)
	{
		return expressions.contains(expression);
	}
	
	@Override
	public RuleType getRuleType()
	{
		return RULE_TYPE;
	}
}
