package excite;

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
		Violation vio = (Violation) obj;
		if (vio.getMethodName().equals(this.methodName))
		{
			if (vio.getViolation().equals(this.violation))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Method: ");
		sb.append(this.methodName);
		sb.append("\n");
		sb.append("Violation: ");
		sb.append(this.violation.toString());
		sb.append("\n");
		
		return sb.toString();
	}
}
