package br.ufrn.imd.domain.criteria;

import java.util.List;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.RuleType;

public class MayOnlyCriteria implements RuleCriteria
{
	private static MayOnlyCriteria INSTANCE = null;
	private static final RuleType RULE_TYPE = RuleType.MayOnly;
	
	private MayOnlyCriteria()
	{
		 
	}
	
	public static MayOnlyCriteria getInstance()
	{
		if (INSTANCE == null)
		{
			synchronized (MayOnlyCriteria.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new MayOnlyCriteria();
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
		return !expressions.contains(expression);
	}
	
	@Override
	public RuleType getRuleType()
	{
		return RULE_TYPE;
	}
}
