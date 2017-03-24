package structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassAnalyzer
{
	private String classe;
	private int NOC;
	private int DIT;
	private int numberOfMethods;
	private int numberOfPublicMethods;
	private int numberOfPrivateMethods;
	private int numberOfProtectedMethods;
	private int numberOfFields;
	private int numberOfPublicFields;
	private int numberOfPrivateFields;
	private int numberOfProtectedFields;
	private boolean isException;
	private Map<String, Integer> fieldTypes;
	private List<MethodAnalyzer> methods;
	
	public ClassAnalyzer()
	{
		this.classe = "";
		this.DIT = 0;
		this.NOC = 0;
		this.numberOfFields = 0;
		this.numberOfMethods = 0;
		this.numberOfPublicFields = 0;
		this.numberOfPublicMethods = 0;
		this.numberOfPrivateFields = 0;
		this.numberOfPrivateMethods = 0;
		this.numberOfProtectedFields = 0;
		this.numberOfProtectedMethods = 0;
		this.isException = false;
		this.fieldTypes = new HashMap<String, Integer>();
		this.methods = new ArrayList<>();
	}
	
	public void addPropagatedException(String methodName, String exception)
	{
		boolean existe = false;
		for (MethodAnalyzer method : this.methods)
		{
			if (method.getName().equals(methodName))
			{
				method.addPropagatedException(exception);
				existe = true;
				break;
			}
		}
		
		if (!existe)
		{
			MethodAnalyzer method = new MethodAnalyzer(methodName);
			method.addPropagatedException(exception);
			methods.add(method);
		}
	}

	public void addRaisedException(String methodName, String exception)
	{
		boolean existe = false;
		for (MethodAnalyzer method : this.methods)
		{
			if (method.getName().equals(methodName))
			{
				method.addRaisedException(exception);
				existe = true;
				break;
			}
		}
		
		if (!existe)
		{
			MethodAnalyzer method = new MethodAnalyzer(methodName);
			method.addRaisedException(exception);
			methods.add(method);
		}
	}
	
	public List<MethodAnalyzer> getMethods()
	{
		return this.methods;
	}
	
	public Map<String, Integer> getFieldTypes()
	{
		return fieldTypes;
	}

	public void setFieldTypes(Map<String, Integer> fieldTypes)
	{
		this.fieldTypes = fieldTypes;
	}

	public String getClasse()
	{
		return classe;
	}

	public void setClasse(String classe)
	{
		this.classe = classe;
	}

	public int getNOC()
	{
		return NOC;
	}

	public void setNOC(int nOC)
	{
		NOC = nOC;
	}

	public int getDIT()
	{
		return DIT;
	}

	public void setDIT(int dIT)
	{
		DIT = dIT;
	}

	public int getNumberOfMethods()
	{
		return numberOfMethods;
	}

	public void setNumberOfMethods(int numberOfMethods)
	{
		this.numberOfMethods = numberOfMethods;
	}

	public int getPublicMethods()
	{
		return numberOfPublicMethods;
	}

	public void setPublicMethods(int publicMethods)
	{
		this.numberOfPublicMethods = publicMethods;
	}

	public int getPrivateMethods()
	{
		return numberOfPrivateMethods;
	}

	public void setPrivateMethods(int privateMethods)
	{
		this.numberOfPrivateMethods = privateMethods;
	}

	public int getProtectedMethods()
	{
		return numberOfProtectedMethods;
	}

	public void setProtectedMethods(int protectedMethods)
	{
		this.numberOfProtectedMethods = protectedMethods;
	}

	public int getNumberOfFields()
	{
		return numberOfFields;
	}

	public void setNumberOfFields(int numberOfFields)
	{
		this.numberOfFields = numberOfFields;
	}

	public int getPublicFields()
	{
		return numberOfPublicFields;
	}

	public void setPublicFields(int publicFields)
	{
		this.numberOfPublicFields = publicFields;
	}

	public int getPrivateFields()
	{
		return numberOfPrivateFields;
	}

	public void setPrivateFields(int privateFields)
	{
		this.numberOfPrivateFields = privateFields;
	}

	public int getProtectedFields()
	{
		return numberOfProtectedFields;
	}

	public void setProtectedFields(int protectedFields)
	{
		this.numberOfProtectedFields = protectedFields;
	}

	public boolean isException()
	{
		return isException;
	}

	public void setException(boolean isException)
	{
		this.isException = isException;
	}
}
