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

public class AplicacaoJar
{
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private static List<String> compartmentsWithPropagateViolation = new ArrayList<>();
	private static List<String> compartmentsWithRaiseViolation = new ArrayList<>();

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
				//System.out.println("Package: " + mypackage.getElementName());

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
	
	public static void addcompartmentsWithPropagateViolation(String compartment)
	{
		if (!compartmentsWithPropagateViolation.contains(compartment))
		{
			compartmentsWithPropagateViolation.add(compartment);
		}
	}
	
	public static void showCompartmentsWithPropagateViolation()
	{
		System.out.println("Compartment with propagate violation:");
		for (String s : compartmentsWithPropagateViolation)
		{
			System.out.println("\t" + s);
		}
	}
	
	public static void showCompartmentsWithRaiseViolation()
	{
		System.out.println("Compartment with raise violation:");
		for (String s : compartmentsWithRaiseViolation)
		{
			System.out.println("\t" + s);
		}
	}
	
	public static void addcompartmentsWithRaiseViolation(String compartment)
	{
		if (!compartmentsWithRaiseViolation.contains(compartment))
		{
			compartmentsWithRaiseViolation.add(compartment);
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
}
/**/