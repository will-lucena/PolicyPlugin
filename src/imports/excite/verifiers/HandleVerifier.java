package excite.verifiers;

import java.util.List;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.Controller;
import excite.Marker;
import policiesplugin.handlers.Application;

public class HandleVerifier extends Verifier
{
	private static HandleVerifier instance = null;

	private HandleVerifier()
	{

	}

	public static HandleVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (HandleVerifier.class)
			{
				if (instance == null)
				{
					instance = new HandleVerifier();
				}
			}
		}
		return instance;
	}

	public void checkHandleViolation(Method method, Marker marker)
	{
		checkHandleViolation(method.getCompartment(), method.getExceptionsPropagated(), method.getFullyQualifiedName(), marker);
	}

	private void checkHandleViolation(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
		verifyCannotRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMustRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
	}

	private boolean verifyCannotRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Cannot) && rule.getDependencyType().equals(DependencyType.Handle))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean verifyMustRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Must) && rule.getDependencyType().equals(DependencyType.Handle))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (!rule.getExceptionExpressions().contains(exception.toString()))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean verifyOnlyMayRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.OnlyMay) && rule.getDependencyType().equals(DependencyType.Handle))
			{
				if (compartment != null && !rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean verifyMayOnlyRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.MayOnly) && rule.getDependencyType().equals(DependencyType.Handle))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					if (!rule.getExceptionExpressions().containsAll(exceptions))
					{
						marker.setRule(rule.toString());
						Controller.addMarker(marker);
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
