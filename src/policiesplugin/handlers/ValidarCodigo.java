package policiesplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import excite.Verifier;
/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ValidarCodigo extends AbstractHandler {		
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Verifier.getInstance().showCannotViolations();
		return null;
	}
}