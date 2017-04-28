package excite;

public class Marker
{
	private int firstIndex;
	private int lastIndex;
	private String rule;
	
	public Marker()
	{
		this.firstIndex = -1;
		this.lastIndex = -1;
		this.rule = null;
	}
	
	public Marker(int firstIndex, int lastIndex)
	{
		this.firstIndex = firstIndex;
		this.lastIndex = lastIndex;
		this.rule = null;
	}
	
	public int getLastIndex()
	{
		return lastIndex;
	}

	public int getFirstIndex()
	{
		return firstIndex;
	}
	
	public String getRule()
	{
		return rule;
	}
	
	public void setLastIndex(int length)
	{
		this.lastIndex = length;
	}
	
	public void setFirstIndex(int startPosition)
	{
		this.firstIndex = startPosition;
	}
	
	public void setRule(String name)
	{
		this.rule = name;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Rule: ");
		sb.append(this.rule);
		sb.append("\tFirstIndex: ");
		sb.append(this.firstIndex);
		sb.append("\tLastIndex: ");
		sb.append(this.lastIndex);
		
		return sb.toString();
	}
}
