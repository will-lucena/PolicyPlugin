package epl.model;

import java.io.Serializable;

public class JavaType implements Comparable<JavaType>, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3667640162009371709L;

	private final String fullyQualifiedName;

	public JavaType(final String fullyQualifiedName)
	{
		this.fullyQualifiedName = fullyQualifiedName;
	}

	@Override
	public int compareTo(final JavaType o)
	{
		return this.getFullyQualifiedName().compareTo(o.getFullyQualifiedName());
	}

	@Override
	public boolean equals(final Object t)
	{
		if (!(t instanceof JavaType))
		{
			return false;
		}
		return this.getFullyQualifiedName().equals(((JavaType) t).getFullyQualifiedName());
	}

	public String getFullyQualifiedName()
	{
		return this.fullyQualifiedName;
	}

	@Override
	public String toString()
	{
		return this.getFullyQualifiedName();
	}
}
