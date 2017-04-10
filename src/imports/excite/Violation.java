package excite;

public class Violation
{
	private String methodName;
	private String violationType;
	
	public Violation(String method, String violation)
	{
		this.methodName = method;
		this.violationType = violation;
	}
	
	public String getMethodName()
	{
		return this.methodName;
	}
	
	public String getViolationType()
	{
		return this.violationType;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Violation vio = (Violation) obj;
		if (vio.getMethodName().equals(this.methodName))
		{
			if (vio.getViolationType().equals(this.violationType))
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
		sb.append(this.violationType);
		sb.append("\n");
		
		return sb.toString();
	}
}
