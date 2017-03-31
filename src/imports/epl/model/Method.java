package epl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import epl.util.EPLConstants;

public class Method implements Comparable<Method>, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 6790360218390799321L;

	private String packageName;
	private String className;
	private String name;

	private List<String> parameters;

	private List<JavaType> exceptionsCaught;

	private List<JavaType> exceptionsHandled;
	private List<JavaType> exceptionsPropagated;
	private List<JavaType> exceptionsRaised;
	private List<ExceptionPair> exceptionsRemapped;
	private List<JavaType> exceptionsRethrown;
	private StringBuilder tokenSequence;

	private Set<String> vocabulary;

	private Compartment compartment;

	private String fullyQualifiedName;

	private String fullyQualifiedNameWitParameters;

	public Method()
	{
		this.setParameters(new ArrayList<String>());
		this.setExceptionsCaught(new ArrayList<JavaType>());
		this.setExceptionsHandled(new ArrayList<JavaType>());
		this.setExceptionsPropagated(new ArrayList<JavaType>());
		this.setExceptionsRaised(new ArrayList<JavaType>());
		this.setExceptionsRemapped(new ArrayList<ExceptionPair>());
		this.setExceptionsRethrown(new ArrayList<JavaType>());
		this.setTokenSequence(new StringBuilder());
		this.setVocabulary(new TreeSet<String>());
	}

	public void addExceptionCaught(final JavaType type)
	{
		this.getExceptionsCaught().add(type);
	}

	public void addExceptionHandled(final JavaType type)
	{
		this.getExceptionsHandled().add(type);
	}

	public void addExceptionPropagated(final JavaType type)
	{
		this.getExceptionsPropagated().add(type);
	}

	public void addExceptionRaised(final JavaType type)
	{
		this.getExceptionsRaised().add(type);
	}

	public void addExceptionRemapped(final ExceptionPair pair)
	{
		this.getExceptionsRemapped().add(pair);
	}

	public void addExceptionRethrown(final JavaType type)
	{
		this.getExceptionsRethrown().add(type);
	}

	public void addParameter(final String parameter)
	{
		this.parameters.add(parameter);
	}

	@Override
	public Method clone()
	{
		final Method method = new Method();
		method.setClassName(this.className);
		method.setCompartment(this.compartment);
		method.setExceptionsCaught(this.exceptionsCaught);
		method.setExceptionsHandled(this.exceptionsHandled);
		method.setExceptionsPropagated(this.exceptionsPropagated);
		method.setExceptionsRaised(this.exceptionsRaised);
		method.setExceptionsRemapped(this.exceptionsRemapped);
		method.setExceptionsRethrown(this.exceptionsRethrown);
		method.setName(this.className);
		method.setPackageName(this.packageName);
		method.setParameters(this.parameters);
		method.setTokenSequence(this.tokenSequence);
		method.setVocabulary(this.vocabulary);
		return method;
	}

	@Override
	public int compareTo(final Method o)
	{
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(final Object obj)
	{
		final Method m = (Method) obj;
		return this.hashCode() == m.hashCode();
	}

	public String getClassName()
	{
		return this.className;
	}

	public Compartment getCompartment()
	{
		return this.compartment;
	}

	public List<JavaType> getExceptionsCaught()
	{
		return this.exceptionsCaught;
	}

	public List<JavaType> getExceptionsHandled()
	{
		return this.exceptionsHandled;
	}

	public List<JavaType> getExceptionsPropagated()
	{
		return this.exceptionsPropagated;
	}

	public List<JavaType> getExceptionsRaised()
	{
		return this.exceptionsRaised;
	}

	public List<ExceptionPair> getExceptionsRemapped()
	{
		return this.exceptionsRemapped;
	}

	public List<JavaType> getExceptionsRethrown()
	{
		return this.exceptionsRethrown;
	}

	public String getFullyQualifiedName()
	{
		if (this.fullyQualifiedName == null)
		{
			this.fullyQualifiedName = String.format(EPLConstants.METHOD_FULLY_QUALIFIED_NAME_FORMAT, this.packageName, this.className, this.name);
		}
		return this.fullyQualifiedName;
	}

	public String getFullyQualifiedNameWithParameters()
	{
		if (this.fullyQualifiedNameWitParameters == null)
		{
			this.fullyQualifiedNameWitParameters = String.format(EPLConstants.METHOD_NAME_WITH_PARAMETER_FORMAT, this.getFullyQualifiedName(),
					this.getParameters());
		}
		return this.fullyQualifiedNameWitParameters;
	}

	public String getName()
	{
		return this.name;
	}

	public String getPackageName()
	{
		return this.packageName;
	}

	public List<String> getParameters()
	{
		return this.parameters;
	}

	public StringBuilder getTokenSequence()
	{
		return this.tokenSequence;
	}

	public Set<String> getVocabulary()
	{
		return this.vocabulary;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		final int result = prime * +this.toString().hashCode();
		return result;
	}

	public void setClassName(final String className)
	{
		this.className = className;
	}

	public void setCompartment(final Compartment compartment)
	{
		this.compartment = compartment;
	}

	private void setExceptionsCaught(final List<JavaType> exceptionsCaught)
	{
		this.exceptionsCaught = exceptionsCaught;
	}

	private void setExceptionsHandled(final List<JavaType> exceptionsHandled)
	{
		this.exceptionsHandled = exceptionsHandled;
	}

	private void setExceptionsPropagated(final List<JavaType> exceptionsPropagated)
	{
		this.exceptionsPropagated = exceptionsPropagated;
	}

	private void setExceptionsRaised(final List<JavaType> exceptionsRaised)
	{
		this.exceptionsRaised = exceptionsRaised;
	}

	private void setExceptionsRemapped(final List<ExceptionPair> exceptionsRemapped)
	{
		this.exceptionsRemapped = exceptionsRemapped;
	}

	private void setExceptionsRethrown(final List<JavaType> exceptionsRethrown)
	{
		this.exceptionsRethrown = exceptionsRethrown;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setPackageName(final String packageName)
	{
		this.packageName = packageName;
	}

	private void setParameters(final List<String> parameters)
	{
		this.parameters = parameters;
	}

	public void setTokenSequence(final StringBuilder tokenSequence)
	{
		this.tokenSequence = tokenSequence;
	}

	public void setVocabulary(final Set<String> vocabulary)
	{
		this.vocabulary = vocabulary;
	}

	@Override
	public String toString()
	{
		return this.getFullyQualifiedNameWithParameters();
	}

}
