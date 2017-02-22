package policiesplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import epl.model.Policy;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ValidarCodigo extends AbstractHandler {	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Policy policy = ConsumirEpl.policy;
		printEpl();
		return null;
	}
	
	private void printEpl()
	{
		ConsumirEpl.printCompartments();
		ConsumirEpl.printRules();
	}
}