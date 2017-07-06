package excite.verifiers;

import epl.model.Compartment;
import epl.model.ExceptionPair;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.AplicacaoJar;
import excite.Marker;
import policiesplugin.handlers.ConsumirEpl;

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
	
	public void checkRemapViolation(Method method, Marker marcador)
	{
		checkRemapViolation(method.getCompartment(), method.getExceptionsRemapped().get(0),
				method.getFullyQualifiedName(), marcador);
	}

	private void checkRemapViolation(Compartment compartment, ExceptionPair exceptionPair, String methodName,
			Marker marcador)
	{
		int fIndex = marcador.getFirstIndex();
		int lIndex = marcador.getLastIndex();

		verifyCannotRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
		verifyMustRule(compartment, exceptionPair, new Marker(fIndex, lIndex));
	}

	private String montarExpressao(ExceptionPair pair)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("from ");
		sb.append(pair.getFrom().toString());
		sb.append(" to ");
		sb.append(pair.getTo().toString());

		return sb.toString();
	}

	private void verifyCannotRule(Compartment compartment, ExceptionPair exceptionPair, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					String pair = montarExpressao(exceptionPair);
					for (String expression : r.getExceptionExpressions())
					{
						if (!expression.equals(pair))
						{
							marcador.setRule(r.toString());
							AplicacaoJar.addMarker(marcador);
							return;
						}
					}
				}
			}
		}
	}

	private void verifyMustRule(Compartment compartment, ExceptionPair exceptionPair, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Must) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					String pair = montarExpressao(exceptionPair);
					for (String expression : r.getExceptionExpressions())
					{
						if (expression.equals(pair))
						{
							return;
						}
					}
					marcador.setRule(r.toString());
					AplicacaoJar.addMarker(marcador);
					return;
				}
			}
		}
	}

	private void verifyOnlyMayRule(Compartment compartment, ExceptionPair exceptionPair, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && !r.getCompartmentId().equals(compartment.getId()))
				{
					String pair = montarExpressao(exceptionPair);
					for (String expression : r.getExceptionExpressions())
					{
						if (expression.equals(pair))
						{
							marcador.setRule(r.toString());
							AplicacaoJar.addMarker(marcador);
							return;
						}
					}
				}
			}
		}
	}

	private void verifyMayOnlyRule(Compartment compartment, ExceptionPair exceptionPair, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					String pair = montarExpressao(exceptionPair);
					for (String expression : r.getExceptionExpressions())
					{
						if (!expression.equals(pair))
						{
							marcador.setRule(r.toString());
							AplicacaoJar.addMarker(marcador);
							return;
						}
					}
				}
			}
		}
	}
}
