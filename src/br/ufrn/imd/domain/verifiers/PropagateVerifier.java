package br.ufrn.imd.domain.verifiers;

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
import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;
import policiesplugin.handlers.Application;

public class PropagateVerifier extends Verifier
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
		
	public Method getPropagatedExceptions(MethodDeclaration node, Method method, Marker marker)
	{
		for (Iterator<?> iter = node.thrownExceptionTypes().iterator(); iter.hasNext();)
		{		
			SimpleType exceptionType = (SimpleType) iter.next();	
			method.addExceptionPropagated(new JavaType(exceptionType.resolveBinding().getName()));
			marker.setLastIndex(exceptionType.getStartPosition() + exceptionType.getLength());
		}
		
		return method;
	}
	
	public void checkPropagateViolation(Method method, Marker marker)
	{
		checkPropagateViolation(method.getCompartment(), method.getExceptionsPropagated(), method.getFullyQualifiedName(), marker);
	}
	
	private void checkPropagateViolation(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
	{
		int fIndex = marker.getFirstIndex();
		int lIndex = marker.getLastIndex();
		verifyCannotRule(compartment, exceptions, new Marker(fIndex, lIndex));
		verifyOnlyMayRule(compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMayOnlyRule(compartment, exceptions, new Marker(fIndex, lIndex));
		verifyMustRule(compartment, exceptions, new Marker(fIndex, lIndex));
	}
	
	private void verifyCannotRule(Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Cannot) && rule.getDependencyType().equals(DependencyType.Propagate))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
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
	
	private void verifyMustRule(Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.Must) && rule.getDependencyType().equals(DependencyType.Propagate))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
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

	private void verifyOnlyMayRule(Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.OnlyMay) && rule.getDependencyType().equals(DependencyType.Propagate))
			{
				if (compartment != null && !rule.getCompartmentId().equals(compartment.getId()))
				{
					for (JavaType exception : exceptions)
					{
						if (rule.getExceptionExpressions().contains(exception.toString()))
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

	private void verifyMayOnlyRule(Compartment compartment, List<JavaType> exceptions, Marker marker)
	{
		for (Rule rule : Application.getPolicy().getRules())
		{
			if (rule.getRuleType().equals(RuleType.MayOnly) && rule.getDependencyType().equals(DependencyType.Propagate))
			{
				if (compartment != null && rule.getCompartmentId().equals(compartment.getId()))
				{
					if (!rule.getExceptionExpressions().containsAll(exceptions))
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
