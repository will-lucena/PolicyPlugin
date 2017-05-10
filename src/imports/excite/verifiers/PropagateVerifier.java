package excite.verifiers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.AplicacaoJar;
import excite.Marker;
import policiesplugin.handlers.ConsumirEpl;

public class PropagateVerifier
{
	private static PropagateVerifier instance = null;
	
	private PropagateVerifier()
	{

	}

	public static PropagateVerifier getInstance()
	{
		if (instance == null)
		{
			synchronized (PropagateVerifier.class)
			{
				if (instance == null)
				{
					instance = new PropagateVerifier();
				}
			}
		}
		return instance;
	}
	
	public Compartment getCompartment(String name)
	{
		return Verifier.getInstance().findCompartment(name);
	}
	
	public Method getPropagatedExceptions(MethodDeclaration node, Method method, Marker marcador)
	{
		for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
		{		
			SimpleType exceptionType = (SimpleType) iter.next();	
			method.addExceptionPropagated(new JavaType(exceptionType.resolveBinding().getName()));
			marcador.setLastIndex(exceptionType.getStartPosition() + exceptionType.getLength());
		}
		
		return method;
	}
	
	public void checkPropagateViolation(Method method, Marker m)
	{
		checkPropagateViolation(method.getCompartment(), method.getExceptionsPropagated(), method.getFullyQualifiedName(), m);
	}
	
	private void checkPropagateViolation(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marcador)
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
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(DependencyType.Propagate))
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
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(DependencyType.Propagate))
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
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(DependencyType.Propagate))
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
