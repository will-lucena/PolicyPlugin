package epl.model;

import java.util.ArrayList;
import java.util.List;

public class Policy
{

	private final List<Compartment> compartments;

	private final List<Rule> rules;

	public Policy()
	{
		this.compartments = new ArrayList<Compartment>();
		this.rules = new ArrayList<>();
	}

	public Policy(final List<Compartment> compartments2, final List<Rule> rules2)
	{
		this.compartments = compartments2;
		this.rules = rules2;
	}

	public void addCompartment(final Compartment compartment)
	{
		this.getCompartments().add(compartment);
	}

	public void addRule(final Rule rule)
	{
		this.getRules().add(rule);
	}

	public boolean check()
	{
		for (final Compartment compartment : this.getCompartments())
		{
			for (final Rule rule : compartment.getRules())
			{
				assert this.getRules().contains(rule);
			}
		}

		for (final Rule rule : this.getRules())
		{
			assert this.getCompartments().contains(rule.getCompartment());
		}

		return true;
	}

	public List<Compartment> getCompartments()
	{
		return this.compartments;
	}

	public List<Rule> getRules()
	{
		return this.rules;
	}

	@Override
	public String toString()
	{
		final StringBuffer writer = new StringBuffer();

		writer.append("Compartments\n");
		for (final Compartment compartment : this.getCompartments())
		{
			writer.append(String.format("- %s\n", compartment));
		}

		writer.append("Rules\n");
		for (final Rule rule : this.getRules())
		{
			writer.append(String.format("- %s\n", rule));
		}

		return writer.toString();
	}
}
