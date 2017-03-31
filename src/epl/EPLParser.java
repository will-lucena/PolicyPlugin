package epl;

import java.io.File;
import java.util.List;

import epl.model.Compartment;
import epl.model.Policy;
import epl.model.Rule;

public class EPLParser
{
	/** 
	 * This main method reads, parses and prints an exception handling policy written in EPL.
	 */
	/*
	public static void main(String[] args) throws Exception
	{
		String path = "C:/Users/William/workspace/runtime-Default/test/src/freecol-FullPolicy.epl";

		final File policyFile = new File(path);
		if (!policyFile.exists() || !policyFile.isFile())
		{
			throw new IllegalArgumentException("Could not find specification at " + policyFile);
		}

		Policy policy = PolicyParser.parseSpecification(policyFile);

		List<Compartment> compartments = policy.getCompartments();

		for (Compartment compartment : compartments)
		{
			System.out.println(compartment);
		}

		List<Rule> rules = policy.getRules();
		for (Rule rule : rules)
		{
			System.out.println(rule);
		}

	}
	/**/
	public static Policy gerarPolicy(String path)
	{
		final File policyFile = new File(path);
		if (!policyFile.exists() || !policyFile.isFile())
		{
			throw new IllegalArgumentException("Could not find specification at " + policyFile);
		}

		Policy policy = PolicyParser.parseSpecification(policyFile);
		return policy;
	}
	
	public static void showCompartments(Policy policy)
	{
		List<Compartment> compartments = policy.getCompartments();

		for (Compartment compartment : compartments)
		{
			System.out.println(compartment);
		}
	}
	
	public static void showRules(Policy policy)
	{
		List<Rule> rules = policy.getRules();
		for (Rule rule : rules)
		{
			System.out.println(rule);
		}
	}
}
