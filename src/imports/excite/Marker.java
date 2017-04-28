package excite;

public class Marker
{
	private int startPosition;
	private int length;
	private String rule;
	
	public int getLength()
	{
		return length;
	}

	public int getStartPosition()
	{
		return startPosition;
	}
	
	public String getRule()
	{
		return rule;
	}
	
	public void setLength(int length)
	{
		this.length = length;
	}
	
	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
	}
	
	public void setRule(String name)
	{
		this.rule = name;
	}
}
