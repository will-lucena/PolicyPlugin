package excite;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jface.text.BadLocationException;

public class AplicacaoJar
{
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private static final List<Marker> markers = new ArrayList<>();
	private static final String MARKER_TYPE = "excite.markerId";

	public AplicacaoJar()
	{
		getProjects();
	}

	public static void addMarker(Marker m)
	{
		markers.add(m);
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
			} catch (BadLocationException e)
			{
				JOptionPane.showMessageDialog(null, "BadLocationException");
				e.printStackTrace();
			}
		}
	}

	private void analyzeProject(IProject project) throws CoreException, BadLocationException
	{
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages)
		{
			// filtrando apenas para projeto usado para de testes
			if (mypackage.getElementName().equals("main"))
			{
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE)
				{
					for (ICompilationUnit unit : mypackage.getCompilationUnits())
					{
						CompilationUnit compilationUnit = parse(unit);
						CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor();

						deleteMarkers(unit.getCorrespondingResource(), MARKER_TYPE);
						
						compilationUnit.accept(compilationUnitVisitor);
						
						createMarker(unit.getCorrespondingResource());
					}
				}
			}
		}
	}

	private void deleteMarkers(IResource res, String type) throws CoreException
	{
		res.deleteMarkers(type, false, 0);
		markers.clear();
	}
	
	private void createMarker(IResource res) throws CoreException, BadLocationException
	{
		for (Marker m : markers)
		{
			IMarker marker = null;
			marker = res.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.CHAR_START, m.getFirstIndex());
			marker.setAttribute(IMarker.CHAR_END, m.getLastIndex());
			marker.setAttribute(IMarker.MESSAGE, m.getRule());
			System.out.println(m.getRule());
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