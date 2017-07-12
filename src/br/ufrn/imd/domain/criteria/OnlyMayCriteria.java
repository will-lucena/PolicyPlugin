package br.ufrn.imd.domain.criteria;

import java.util.List;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.RuleType;

public class OnlyMayCriteria implements RuleCriteria
{
	private static OnlyMayCriteria INSTANCE = null;
	private static final RuleType RULE_TYPE = RuleType.OnlyMay;
	
	private OnlyMayCriteria()
	{
		 
	}
	
	public static OnlyMayCriteria getInstance()
	{
		if (INSTANCE == null)
		{
			synchronized (OnlyMayCriteria.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new OnlyMayCriteria();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public boolean compartmentCriteria(Compartment compartment, Rule rule)
	{
		return compartment != null && !rule.getCompartmentId().equals(compartment.getId());
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
