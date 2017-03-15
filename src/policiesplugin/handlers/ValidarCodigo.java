package policiesplugin.handlers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import epl.model.*;
import epl.model.Rule.DependencyType;
import excite.AplicacaoJar;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ValidarCodigo extends AbstractHandler {	
	private static AplicacaoJar app = new AplicacaoJar();
	private static Policy policy = ConsumirEpl.policy;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		showExceptions();
		validarCodigo();
		return null;
	}
	
	private void validarCodigo()
	{
		System.out.println("Has propagate violation: " + searchPropagateViolation());
		System.out.println("Has raise violation: " + searchRaiseViolation());
	}
	
	private void showExceptions()
	{
		System.out.println("Propagated exceptions");
		showPropagatedException();
		System.out.println("Raised exceptions");
		showRaisedException();
	}
	
	private boolean searchPropagateViolation()
	{
		Map<String, List<String>> map = app.getPropagatedException();
		Set<String> keys = map.keySet();
		
		for (Rule r : policy.getRules())
		{
			if (r.getDependencyType().equals(DependencyType.Propagate))
			{
				for (String method : r.getCompartment().getExpressions())
				{
					if (keys.contains(method))
					{
						if (map.get(method).size() > 0)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean searchRaiseViolation()
	{
		Map<String, List<String>> map = app.getRaisedException();
		Set<String> keys = map.keySet();
		
		for (Rule r : policy.getRules())
		{
			if (r.getDependencyType().equals(DependencyType.Raise))
			{
				for (String method : r.getCompartment().getExpressions())
				{
					if (keys.contains(method))
					{
						if (map.get(method).size() > 0)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private void showPropagatedException()
	{
		Map<String, List<String>> map = app.getPropagatedException();
		Set<String> keys = map.keySet();
		
		for (String key : keys)
		{
			System.out.println("Method: " + key);
			for (String ex : map.get(key))
			{
				System.out.println("\tException: " + ex);
			}
		}
	}
	
	private void showRaisedException()
	{
		Map<String, List<String>> map = app.getRaisedException();
		Set<String> keys = map.keySet();
		
		for (String key : keys)
		{
			System.out.println("Method: " + key);
			for (String ex : map.get(key))
			{
				System.out.println("\tException: " + ex);
			}
		}
	}
	
	private void printEpl()
	{
		ConsumirEpl.printCompartments();
		ConsumirEpl.printRules();
	}
}