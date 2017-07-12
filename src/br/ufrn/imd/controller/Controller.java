package br.ufrn.imd.controller;

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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;

import View.PluginView;
import br.ufrn.imd.domain.visitors.CompilationUnitVisitor;
import epl.model.Compartment;
import policiesplugin.handlers.Application;
import br.ufrn.imd.domain.Marker;
public class Controller
{
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private static final List<Marker> markers = new ArrayList<>();
	private static final String MARKER_TYPE = "excite.markerId";

	public static Compartment updateCompartment(Compartment compartment)
	{
		for (Compartment comp : Application.getPolicy().getCompartments())
		{
			if (comp.getId().equals(compartment.getId()))
			{
				comp = compartment;
				return comp;
			}
		}
		return null;
	}

	public Controller()
	{
		getProjects();
	}

	public static void addMarker(Marker m)
	{
		for (Marker marker : markers)
		{
			if (marker.getRule().equals(m.getRule()))
			{
				break;
			}
		}
		markers.add(m);
	}

	public static List<Marker> getViolations()
	{
		return markers;
	}

	private void getProjects()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
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
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE)
			{
				for (ICompilationUnit unit : mypackage.getCompilationUnits())
				{
					CompilationUnit compilationUnit = parse(unit);
					CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor();

					deleteMarkers(unit.getCorrespondingResource(), MARKER_TYPE);
					compilationUnit.accept(compilationUnitVisitor);

					createMarkers(unit.getCorrespondingResource());
				}
			}
		}
	}

	private void deleteMarkers(IResource resource, String type) throws CoreException
	{
		resource.deleteMarkers(type, false, 0);
		markers.clear();
	}

	public static Marker prepareMarker(ASTNode node)
	{
		Marker marker = new Marker();
		marker.setFirstIndex(node.getStartPosition());
		marker.setLastIndex(node.getStartPosition() + node.getLength());
		return marker;
	}

	private void createMarkers(IResource resource) throws CoreException, BadLocationException
	{
		for (Marker mark : markers)
		{
			IMarker marker = null;
			marker = resource.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.CHAR_START, mark.getFirstIndex());
			marker.setAttribute(IMarker.CHAR_END, mark.getLastIndex());
			marker.setAttribute(IMarker.MESSAGE, mark.getRule());
			System.out.println(mark.getRule());
			mark.setResourcePath(resource.getLocation().toOSString());
			
			PluginView.insertViolations(mark.getRule());
		}
	}

	private CompilationUnit parse(ICompilationUnit compilationUnit)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}
}