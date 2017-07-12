package br.ufrn.imd.domain.criteria;

import java.util.List;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.RuleType;

public class MustCriteria implements RuleCriteria
{
	private static MustCriteria INSTANCE = null;
	private static final RuleType RULE_TYPE = RuleType.Must;
	
	private MustCriteria()
	{
		 
	}
	
	public static MustCriteria getInstance()
	{
		if (INSTANCE == null)
		{
			synchronized (MustCriteria.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new MustCriteria();
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
