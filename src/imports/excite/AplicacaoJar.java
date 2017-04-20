package excite;

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

	public AplicacaoJar()
	{
		getProjects();
	}
	
	private void getProjects()
	{
		// Acessa workspace
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
			//filtrando apenas para projeto usado para de testes
			if (mypackage.getElementName().equals("main"))
			{
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE)
				{
					for (ICompilationUnit unit : mypackage.getCompilationUnits())
					{
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
}