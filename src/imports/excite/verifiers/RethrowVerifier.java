package excite.verifiers;

import java.util.List;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.AplicacaoJar;
import excite.Marker;
import policiesplugin.handlers.ConsumirEpl;

public class RethrowVerifier
{
	private static RethrowVerifier instance = null;
	
	private RethrowVerifier()
	{
		
	}

	public static RethrowVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (RethrowVerifier.class)
			{
				if (instance == null)
				{
					instance = new RethrowVerifier();
				}
			}
		}
		return instance;
	}
	
	
	public void checkRethrowViolation(Method method, Marker m)
	{
		checkRethrowViolation(method.getCompartment(), method.getExceptionsRethrown(), method.getFullyQualifiedName(), m);
	}
	
	private void checkRethrowViolation(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marcador)
	{
		int fIndex = marcador.getFirstIndex();
		int lIndex = marcador.getLastIndex();
 		verifyCannotRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(methodName, compartment, exceptions, new Marker(fIndex, lIndex));
	}
	
	private boolean verifyCannotRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(DependencyType.Rethrow))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
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

	private boolean verifyOnlyMayRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(DependencyType.Rethrow))
			{
				if (compartment != null && !r.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
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

	private boolean verifyMayOnlyRule(String methodName, Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(DependencyType.Rethrow))
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
