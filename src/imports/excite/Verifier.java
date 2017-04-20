package excite;

import java.util.ArrayList;
import java.util.List;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import policiesplugin.handlers.ConsumirEpl;

public class Verifier
{
	private static Verifier instance = null;
	private static final List<Violation> cannotViolations = new ArrayList<>();
	private static final List<Violation> mayOnlyViolations = new ArrayList<>();
	private static final List<Violation> onlyMayViolations = new ArrayList<>();
	private static final List<Violation> mustViolations = new ArrayList<>();
	
	private Verifier()
	{
	}

	public static Verifier getInstance()
	{
		if (instance == null)
		{
			synchronized (Verifier.class)
			{
				if (instance == null)
				{
					new Verifier();
				}
			}
		}
		return instance;
	}
	
	private void verifyCannotRule(String methodName, Compartment compartment, List<String> exceptions, DependencyType dependecy)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(dependecy))
			{
				if (r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception))
						{
							Violation v = new Violation(methodName, r);
							addCannotViolation(v);
						}
					}
				}
			}
		}
	}

	private void verifyOnlyMayRule(String methodName, Compartment compartment, List<String> exceptions, DependencyType dependecy)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(dependecy))
			{
				if (!r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception))
						{
							Violation v = new Violation(methodName, r);
							addOnlyMayViolation(v);
						}
					}
				}
			}
		}
	}

	private void verifyMayOnlyRule(String methodName, Compartment compartment, List<String> exceptions, DependencyType dependecy)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(dependecy))
			{
				if (r.getCompartmentId().equals(compartment.getId()))
				{
					if (!r.getExceptionExpressions().containsAll(exceptions))
					{
						Violation v = new Violation(methodName, r);
						addMayOnlyViolation(v);
					}
				}
			}
		}
	}
	
	public Compartment findCompartment(String methodName)
	{
		for (Compartment c : ConsumirEpl.getPolicy().getCompartments())
		{
			for (String ex : c.getExpressions())
			{
				if (methodName.matches(ex))
				{
					return c;
				}
			}
		}
		return null;
	}

	private void addCannotViolation(Violation violation)
	{
		if (!cannotViolations.contains(violation))
		{
			cannotViolations.add(violation);
		}
	}
	
	private void addOnlyMayViolation(Violation violation)
	{
		if (!onlyMayViolations.contains(violation))
		{
			onlyMayViolations.add(violation);
		}
	}
	
	private void addMayOnlyViolation(Violation violation)
	{
		if (!mayOnlyViolations.contains(violation))
		{
			mayOnlyViolations.add(violation);
		}
	}
	
	private void addMustViolation(Violation violation)
	{
		if (!mustViolations.contains(violation))
		{
			mustViolations.add(violation);
		}
	}

	public void showCannotViolations()
	{
		System.out.println("\n=== Cannot violations ===");
		for (Violation v : Verifier.cannotViolations)
		{
			System.out.println(v);
		}
	}
	
	public void showOnlyMayViolations()
	{
		System.out.println("\n=== Only may violations ===");
		for (Violation v : onlyMayViolations)
		{
			System.out.println(v);
		}
	}
	
	public void showMayOnlyViolations()
	{
		System.out.println("\n=== May only violations ===");
		for (Violation v : mayOnlyViolations)
		{
			System.out.println(v);
		}
	}
	
	public void showMustViolations()
	{
		System.out.println("\n=== Must violations ===");
		for (Violation v : mustViolations)
		{
			System.out.println(v);
		}
	}
	
	public void checkPropagateViolation(Compartment compartment, List<String> exceptions, String methodName)
	{
		verifyCannotRule(methodName, compartment, exceptions, DependencyType.Propagate);
		verifyOnlyMayRule(methodName, compartment, exceptions, DependencyType.Propagate);
		verifyMayOnlyRule(methodName, compartment, exceptions, DependencyType.Propagate);
	}
	
	public void checkRaiseViolation(Compartment compartment, List<String> exceptions, String methodName)
	{
		verifyCannotRule(methodName, compartment, exceptions, DependencyType.Raise);
		verifyOnlyMayRule(methodName, compartment, exceptions, DependencyType.Raise);
		verifyMayOnlyRule(methodName, compartment, exceptions, DependencyType.Raise);
	}
}
