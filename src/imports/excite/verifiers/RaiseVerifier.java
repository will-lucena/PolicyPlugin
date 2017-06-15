package excite.verifiers;

import java.util.List;

import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.AplicacaoJar;
import excite.InstanceCreatorVisitor;
import excite.Marker;
import policiesplugin.handlers.ConsumirEpl;

public class RaiseVerifier
{
	private static RaiseVerifier instance = null;
	
	private RaiseVerifier()
	{
		
	}

	public static RaiseVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (RaiseVerifier.class)
			{
				if (instance == null)
				{
					instance = new RaiseVerifier();
				}
			}
		}
		return instance;
	}
	
	public Method getRaisedExceptions(ThrowStatement node, Method method)
	{
		InstanceCreatorVisitor visitor = new InstanceCreatorVisitor();
		node.accept(visitor);
		String exceptionType = visitor.getType();
		method.addExceptionRaised(new JavaType(exceptionType));
		
		return method;
	}
	
	public void checkRaiseViolation(Method method, Marker m)
	{
		checkRaiseViolation(method.getCompartment(), method.getExceptionsRaised(), method.getFullyQualifiedName(), m);
	}
	
	private void checkRaiseViolation(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marcador)
	{
		int fIndex = marcador.getFirstIndex();
		int lIndex = marcador.getLastIndex();
		verifyCannotRule(compartment, exceptions, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMustRule(compartment, exceptions, new Marker(fIndex, lIndex));
	}
	
	private void verifyCannotRule(Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(DependencyType.Raise))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception.toString()))
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
	
	private void verifyMustRule(Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Must) && r.getDependencyType().equals(DependencyType.Raise))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception.toString()))
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

	private void verifyOnlyMayRule(Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(DependencyType.Raise))
			{
				if (compartment != null && !r.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception.toString()))
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

	private void verifyMayOnlyRule(Compartment compartment, List<JavaType> exceptions, Marker marcador)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(DependencyType.Raise))
			{
				if (compartment != null && r.getCompartmentId().equals(compartment.getId()))
				{
					if (!r.getExceptionExpressions().containsAll(exceptions))
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
