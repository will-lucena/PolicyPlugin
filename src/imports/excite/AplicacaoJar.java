package excite;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import epl.model.Compartment;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import policiesplugin.handlers.ConsumirEpl;

public class AplicacaoJar
{
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private static final List<Violation> violations = new ArrayList<>();

	public AplicacaoJar()
	{
		getProjects();
	}
	
	private void getProjects()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// Acessa a raiz do workspace
		IWorkspaceRoot root = workspace.getRoot();
		// Pega todos projetos na raiz do workspace
		IProject[] projects = root.getProjects();

		for (IProject iProject : projects)
		{
			try
			{
				if (iProject.isNatureEnabled(JDT_NATURE))
				{
					analyzeProject(iProject);
				}
			} catch (JavaModelException e)
			{
				JOptionPane.showMessageDialog(null, "JavaModelException");
				e.printStackTrace();
			} catch (CoreException e)
			{
				JOptionPane.showMessageDialog(null, "CoreException");
				e.printStackTrace();
			}
		}
	}

	private void analyzeProject(IProject project) throws JavaModelException
	{
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages)
		{
			if (mypackage.getElementName().equals("main"))
			{
				// System.out.println("Package: " + mypackage.getElementName());
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE)
				{
					for (ICompilationUnit unit : mypackage.getCompilationUnits())
					{
						// Now create the AST for the ICompilationUnits
						CompilationUnit compilationUnit = parse(unit);
						CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor();

						compilationUnit.accept(compilationUnitVisitor);
					}
				}
			}
		}
	}

	private CompilationUnit parse(ICompilationUnit cu)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(cu);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

	public static Rule verifyCannotRule(Compartment compartment, List<String> exceptions, DependencyType dependecy)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.Cannot) && r.getDependencyType().equals(dependecy))
			{
				if (r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception))
						{
							return r;
						}
					}
				}
			}
		}
		return null;
	}

	public static Rule verifyOnlyMayRule(Compartment compartment, List<String> exceptions, DependencyType dependecy)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.OnlyMay) && r.getDependencyType().equals(dependecy))
			{
				if (!r.getCompartmentId().equals(compartment.getId()))
				{
					for (String exception : exceptions)
					{
						if (r.getExceptionExpressions().contains(exception))
						{
							return r;
						}
					}
				}
			}
		}
		return null;
	}

	public static Rule verifyMayOnlyRule(Compartment compartment, List<String> exceptions, DependencyType dependecy)
	{
		for (Rule r : ConsumirEpl.getPolicy().getRules())
		{
			if (r.getRuleType().equals(RuleType.MayOnly) && r.getDependencyType().equals(dependecy))
			{
				if (r.getCompartmentId().equals(compartment.getId()))
				{
					if (!r.getExceptionExpressions().containsAll(exceptions))
					{
						return r;
					}
				}
			}
		}
		return null;
	}
	
	public static Compartment findCompartment(String methodName)
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

	public static void setViolation(Violation violation)
	{
		if (!AplicacaoJar.violations.contains(violation))
		{
			AplicacaoJar.violations.add(violation);
		}
	}

	public static void showViolations()
	{
		for (Violation v : AplicacaoJar.violations)
		{
			System.out.println(v);
		}
	}
}
/**/