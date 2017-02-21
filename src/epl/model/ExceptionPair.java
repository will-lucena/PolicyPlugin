package epl.model;

import java.io.Serializable;

public class ExceptionPair implements Comparable<ExceptionPair>, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -8148045423618341654L;

	private final JavaType from;
	private final JavaType to;

	//	public ExceptionPair(final ITypeBinding from, final ITypeBinding to)
	//	{
	//		this.from = new JavaType(from.getQualifiedName());
	//		this.to = new JavaType(to.getQualifiedName());
	//	}

	public ExceptionPair(final JavaType from, final JavaType to)
	{
		this.from = from;
		this.to = to;
	}

	@Override
	public int compareTo(final ExceptionPair o)
	{
		final boolean isFromEquals = this.getFrom().equals(o.getFrom());
		final boolean isToEquals = this.getTo().equals(o.getTo());
		if (isFromEquals && isToEquals)
		{
			return 0;
		}
		else if (!isFromEquals)
		{
			return this.getFrom().compareTo(o.getFrom());
		}
		else
		{
			return this.getTo().compareTo(o.getTo());
		}
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ExceptionPair))
		{
			return false;
		}
		final ExceptionPair pair = (ExceptionPair) o;
		return this.getFrom().equals(pair.getFrom()) && this.getTo().equals(pair.getTo());
	}

	public JavaType getFrom()
	{
		return this.from;
	}

	public JavaType getTo()
	{
		return this.to;
	}

	@Override
	public String toString()
	{
		return String.format("from %s to %s", this.from, this.to);
	}
}
