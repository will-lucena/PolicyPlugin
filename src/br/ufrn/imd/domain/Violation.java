package br.ufrn.imd.domain;

import epl.model.Rule;

public class Violation
{
	private String methodName;
	private Rule violation;
	
	public Violation(String method, Rule violation)
	{
		this.methodName = method;
		this.violation = violation;
	}
	
	public String getMethodName()
	{
		return this.methodName;
	}
	
	public Rule getViolation()
	{
		return this.violation;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Violation))
		{
			return false;
		}
		
		Violation violation = (Violation) obj;
		
		if (violation.getMethodName().equals(this.methodName))
		{
			if (violation.getViolation().equals(this.violation))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Method: ");
		stringBuilder.append(this.methodName);
		stringBuilder.append("\n");
		stringBuilder.append("Violation: ");
		stringBuilder.append(this.violation.toString());
		stringBuilder.append("\n");
		
		return stringBuilder.toString();
	}
}
