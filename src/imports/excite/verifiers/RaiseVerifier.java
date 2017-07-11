package excite.verifiers;

import java.util.List;

import org.eclipse.jdt.core.dom.ThrowStatement;

import epl.model.Compartment;
import epl.model.JavaType;
import epl.model.Method;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import excite.Controller;
import excite.InstanceCreatorVisitor;
import excite.Marker;
import policiesplugin.handlers.Application;

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
	
	public void checkRaiseViolation(Method method, Marker marker)
	{
		checkRaiseViolation(method.getCompartment(), method.getExceptionsRaised(), method.getFullyQualifiedName(), marker);
	}
	
	private void checkRaiseViolation(Compartment compartment, List<JavaType> exceptions, String methodName, Marker marker)
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
			if (rule.getRuleType().equals(RuleType.Cannot) && rule.getDependencyType().equals(DependencyType.Raise))
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
			if (rule.getRuleType().equals(RuleType.Must) && rule.getDependencyType().equals(DependencyType.Raise))
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
			if (rule.getRuleType().equals(RuleType.OnlyMay) && rule.getDependencyType().equals(DependencyType.Raise))
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
			if (rule.getRuleType().equals(RuleType.MayOnly) && rule.getDependencyType().equals(DependencyType.Raise))
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
