package excite.verifiers;

import java.util.ArrayList;
import java.util.List;

import epl.model.Compartment;
import epl.model.ExceptionPair;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.AplicacaoJar;
import excite.Marker;
import policiesplugin.handlers.ConsumirEpl;

public class RemapVerifier
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
	
	
	private List<String> gerarPares(List<ExceptionPair> pares)
	{
		List<String> novosPares = new ArrayList<>();
		StringBuilder sb = null;
		
		for (ExceptionPair par : pares)
		{
			sb = new StringBuilder();
			
			sb.append("from ");
			sb.append(par.getFrom());
			sb.append(" to ");
			sb.append(par.getTo());
			
			novosPares.add(sb.toString());
		}		
		return novosPares;
	}
	
	public void checkRemapViolation(Method method, Marker m)
	{
		checkRemapViolation(method.getCompartment(), method.getExceptionsRemapped(), method.getFullyQualifiedName(), m);
	}
	
	private void checkRemapViolation(Compartment compartment, List<ExceptionPair> exceptions, String methodName, Marker marcador)
	{
		int fIndex = marcador.getFirstIndex();
		int lIndex = marcador.getLastIndex();
		List<String> ex = gerarPares(exceptions);
 		verifyCannotRule(methodName, compartment, ex, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(methodName, compartment, ex, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(methodName, compartment, ex, new Marker(fIndex, lIndex));
	}

	private boolean verifyCannotRule(String methodName, Compartment compartment, List<String> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception.toString()))
						{
							marcador.setRule(r.toString());
							AplicacaoJar.addMarker(marcador);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean verifyOnlyMayRule(String methodName, Compartment compartment, List<String> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && !r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception.toString()))
						{
							marcador.setRule(r.toString());
							AplicacaoJar.addMarker(marcador);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean verifyMayOnlyRule(String methodName, Compartment compartment, List<String> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(DependencyType.Remap))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					if (!r.getExceptionExpressions().containsAll(exceptions))
					{
						marcador.setRule(r.toString());
						AplicacaoJar.addMarker(marcador);
						return true;
					}
				}
			}
		}
		return false;
	}
}
