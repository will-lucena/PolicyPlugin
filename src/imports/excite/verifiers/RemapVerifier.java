package excite.verifiers;

import epl.model.Compartment;
import epl.model.ExceptionPair;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.Controller;
import excite.Marker;
import policiesplugin.handlers.Application;

public class RemapVerifier extends Verifier
{
	private static RemapVerifier instance = null;

	private RemapVerifier()
	{

	}

	public static RemapVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (RemapVerifier.class)
			{
				if (instance == null)
				{
					instance = new RemapVerifier();
				}
			}
		}
		return instance;
	}
	
	public void checkRemapViolation(Method method, Marker marker)
	{
		checkRemapViolation(method.getCompartment(), method.getExceptionsRemapped().get(0), method.getFullyQualifiedName(), marker);
	}

	private void checkRemapViolation(Compartment compartment, ExceptionPair exceptionPair, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();

		verifyCannotRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
		verifyMustRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
	}

	private String buildExpression(ExceptionPair pair)
	{
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("from ");
		stringBuilder.append(pair.getFrom().toString());
		stringBuilder.append(" to ");
		stringBuilder.append(pair.getTo().toString());

		return stringBuilder.toString();
	}

	private void verifyCannotRule(Compartment compartment, ExceptionPair exceptionPair, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Cannot) && rule.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					String pair = buildExpression(exceptionPair);
					for (String expression : rule.getExceptionExpressions())
					{
						if (!expression.equals(pair))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return;
						}
					}
				}
			}
		}
	}

	private void verifyMustRule(Compartment compartment, ExceptionPair exceptionPair, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Must) && rule.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					String pair = buildExpression(exceptionPair);
					for (String expression : rule.getExceptionExpressions())
					{
						if (expression.equals(pair))
						{
							return;
						}
					}
					marker.setRule(rule.toString());
					Controller.addMarker(marker);
					return;
				}
			}
		}
	}

	private void verifyOnlyMayRule(Compartment compartment, ExceptionPair exceptionPair, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.OnlyMay) && rule.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && !rule.getCompartmentId().equals(compartment.getId()))
				{
					String pair = buildExpression(exceptionPair);
					for (String expression : rule.getExceptionExpressions())
					{
						if (expression.equals(pair))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return;
						}
					}
				}
			}
		}
	}

	private void verifyMayOnlyRule(Compartment compartment, ExceptionPair exceptionPair, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.MayOnly) && rule.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					String pair = buildExpression(exceptionPair);
					for (String expression : rule.getExceptionExpressions())
					{
						if (!expression.equals(pair))
						{
							marker.setRule(rule.toString());
							Controller.addMarker(marker);
							return;
						}
					}
				}
			}
		}
	}
}
