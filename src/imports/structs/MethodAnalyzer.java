package structs;

import java.util.ArrayList;
import java.util.List;

public class MethodAnalyzer
{
	private String name;
	private List<String> exceptionsHandled;
	private List<String> exceptionsPropagated;
	private List<String> exceptionsRaised;
	private List<String> exceptionsRemapped;
	private List<String> exceptionsRethrown;
	
	public MethodAnalyzer(String name)
	{
		this.name = name;
		this.exceptionsHandled = new ArrayList<>();
		this.exceptionsPropagated = new ArrayList<>();
		this.exceptionsRaised = new ArrayList<>();
		this.exceptionsRemapped = new ArrayList<>();
		this.exceptionsRethrown = new ArrayList<>();
	}
	
	public void addHandledException(String exception)
	{
		this.exceptionsHandled.add(exception);
	}
	
	public void addPropagatedException(String exception)
	{
		this.exceptionsPropagated.add(exception);
	}
	
	public void addRaisedException(String exception)
	{
		this.exceptionsRaised.add(exception);
	}
	
	public void addRemappedException(String exception)
	{
		this.exceptionsRemapped.add(exception);
	}
	
	public void addRethrownException(String exception)
	{
		this.exceptionsRethrown.add(exception);
	}
	
	public String getName()
	{
		return this.name;
	}

	public List<String> getExceptionsHandled()
	{
		return exceptionsHandled;
	}

	public List<String> getExceptionsPropagated()
	{
		return exceptionsPropagated;
	}

	public List<String> getExceptionsRaised()
	{
		return exceptionsRaised;
	}

	public List<String> getExceptionsRemapped()
	{
		return exceptionsRemapped;
	}

	public List<String> getExceptionsRethrown()
	{
		return exceptionsRethrown;
	}	
}
