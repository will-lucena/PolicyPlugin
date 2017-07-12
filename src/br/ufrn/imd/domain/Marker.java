package br.ufrn.imd.domain;

public class Marker
{
	private int firstIndex;
	private int lastIndex;
	private String rule;
	private String resourcePath;
	
	public Marker()
	{
		this.firstIndex = -1;
		this.lastIndex = -1;
		this.rule = null;
		this.resourcePath = null;
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
	
	public void setResourcePath(String resourcePath)
	{
		this.resourcePath = resourcePath;
	}
	
	public String getResourcePath()
	{
		return resourcePath;
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
