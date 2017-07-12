package br.ufrn.imd.domain.criteria;

import java.util.List;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.RuleType;

public interface RuleCriteria
{
	public boolean compartmentCriteria(Compartment compartment, Rule rule);
	public boolean expressionCriteria(List<String> expressions, String expression);
	public RuleType getRuleType();
}
