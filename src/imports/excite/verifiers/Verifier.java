package excite.verifiers;

import epl.model.Compartment;
import policiesplugin.handlers.ConsumirEpl;

public abstract class Verifier
{	
	public Compartment findCompartment(String methodName)
	{
		for (Compartment c : ConsumirEpl.getPolicy().getCompartments())
		{
			for (String ex : c.getExpressions())
			{
				if (methodName.matches(ex))
				{
					return c;
				}
			}
		}
		return null;
	}
}
