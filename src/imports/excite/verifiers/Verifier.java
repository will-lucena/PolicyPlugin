package excite.verifiers;

import epl.model.Compartment;
import policiesplugin.handlers.Application;

public abstract class Verifier
{	
	public Compartment findCompartment(String methodName)
	{
		for (Compartment compartment : Application.getPolicy().getCompartments())
		{
			for (String expression : compartment.getExpressions())
			{
				if (methodName.matches(expression))
				{
					return compartment;
				}
			}
		}
		return null;
	}
}
