package epl.model;

import java.util.ArrayList;
import java.util.List;

public class Compartment
{
	private final String id;

	private final List<String> expressions;

	private final List<Method> methods;

	private final List<Rule> rules;

	public Compartment(final String id, final List<String> expressions)
	{
		this.id = id;
		this.expressions = expressions;
		this.methods = new ArrayList<Method>();
		this.rules = new ArrayList<Rule>();
	}

	public void addMethod(final Method method)
	{
		this.getMethods().add(method);
	}

	public void addRule(final Rule rule)
	{
		this.getRules().add(rule);
	}

	public boolean check()
	{
		for (final Method method : this.getMethods())
		{
			assert method.getCompartment().equals(this) : String.format("Invalid state in compartment %s: compartment of method %s not equals 'this'.",
					this.getId(), method.getFullyQualifiedName());

			boolean matched = false;
			for (final String expression : this.getExpressions())
			{
				if (method.getFullyQualifiedName().matches(expression))
				{
					matched = true;
					break;
				}
			}
			assert matched : String.format("Invalid state in compartment %s: method %s does not match any of %s.", this.getId(),
					method.getFullyQualifiedName(), this.getExpressions());
		}

		assert this.getRules().stream().allMatch(rule -> rule.getCompartment().equals(this)) : String.format(
				"Invalid state in compartment %s: rule.getCompartment() not equals this.\nAll rules:\n", this.getId(), this.getRules());

		return true;
	}

	public List<String> getExpressions()
	{
		return this.expressions;
	}

	public String getId()
	{
		return this.id;
	}

	public List<Method> getMethods()
	{
		return this.methods;
	}

	public List<Rule> getRules()
	{
		return this.rules;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		for (final Method method : this.getMethods())
		{
			builder.append(String.format("\t%s\n", method.getFullyQualifiedName()));
		}
		return String.format("%s %s\n%s", this.getId(), this.getExpressions(), builder.toString());
	}
}
