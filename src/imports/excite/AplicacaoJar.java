package excite;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.ThrowStatement;

import structs.ClassAnalyzer;
import structs.MethodAnalyzer;

public class AplicacaoJar
{
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private List<ClassAnalyzer> analisadores = new ArrayList<>();
	private static final String path = "C:/Users/William/Documents/";

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
		//generateCSV(path);
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

						FieldVisitor fVisitor = new FieldVisitor();

						compilationUnit.accept(fVisitor);

						/*
						for (IType iType : unit.getTypes())
						{
							if (iType.isClass())
							{
								ClassAnalyzer a = new ClassAnalyzer();
								a.setClasse(iType.getElementName());
								a.setDIT(getDIT(iType));
								a.setException(isException(iType));
								a.setNOC(getNOC(iType));
								classVisitor.getMethods().size();
								a = methodInfo(classVisitor, a);
								a = fieldInfo(fVisitor, a);
								a = setPropagateExceptions(classVisitor, a);
								setRaisedExceptions(classVisitor, a);
								analisadores.add(a);
							}
						}
						/**/
					}
				}
			}
		}
	}

	private ClassAnalyzer methodInfo(CompilationUnitVisitor visitor, ClassAnalyzer a)
	{
		int publics = 0;
		int privates = 0;
		int protecteds = 0;

		for (MethodDeclaration method : visitor.getMethods())
		{
			if (Modifier.isPrivate(method.getModifiers()))
			{
				privates++;
			}
			if (Modifier.isPublic(method.getModifiers()))
			{
				publics++;
			}
			if (Modifier.isProtected(method.getModifiers()))
			{
				protecteds++;
			}
		}
		a.setNumberOfMethods(visitor.getMethods().size());
		a.setPrivateMethods(privates);
		a.setPublicMethods(publics);
		a.setProtectedMethods(protecteds);
		return a;
	}

	private ClassAnalyzer setPropagateExceptions(CompilationUnitVisitor visitor, ClassAnalyzer a)
	{
		for (MethodDeclaration m : visitor.getMethods())
		{
			for (Iterator iter = m.thrownExceptionTypes().iterator(); iter.hasNext();)
			{
				SimpleType exceptionType = (SimpleType) iter.next();
				a.addPropagatedException(m.getName().toString(), exceptionType.getName().toString());
			}
		}
		return a;
	}
	
	private ClassAnalyzer setRaisedExceptions(CompilationUnitVisitor visitor, ClassAnalyzer a)
	{
		//m.getBody().THROW_STATEMENT;
		for (MethodDeclaration m : visitor.getMethods())
		{
			for (Iterator iter = m.getBody().statements().iterator(); iter.hasNext();)
			{
				ThrowStatement ts = (ThrowStatement) iter.next();
				//System.out.println("Throws: " + ts.getExpression().resolveTypeBinding().getName());
				String e = ts.getExpression().resolveTypeBinding().getName().toString();
				a.addRaisedException(m.getName().toString(), e);
			}
		}
		return a;
	}
	
	public Map<String, List<String>> getPropagatedException()
	{
		Map<String, List<String>> map = new HashMap<>();
		for(ClassAnalyzer analisador : analisadores)
		{
			for (MethodAnalyzer m : analisador.getMethods())
			{
				map.put(m.getName(), m.getExceptionsPropagated());
			}
		}
		return map;
	}
	
	public Map<String, List<String>> getRaisedException()
	{
		Map<String, List<String>> map = new HashMap<>();
		for(ClassAnalyzer analisador : analisadores)
		{
			for (MethodAnalyzer m : analisador.getMethods())
			{
				map.put(m.getName(), m.getExceptionsRaised());
			}
		}
		return map;
	}
	
	private ClassAnalyzer fieldInfo(FieldVisitor visitor, ClassAnalyzer a)
	{
		int publics = 0;
		int privates = 0;
		int protecteds = 0;
		Map<String, Integer> hash = new HashMap<String, Integer>();

		for (FieldDeclaration field : visitor.getFields())
		{
			if (Modifier.isPrivate(field.getModifiers()))
			{
				privates++;
			}
			if (Modifier.isPublic(field.getModifiers()))
			{
				publics++;
			}
			if (Modifier.isProtected(field.getModifiers()))
			{
				protecteds++;
			}

			try
			{
				hash.put(field.getType().toString(), hash.get(field.getType().toString()) + 1);
			} catch (NullPointerException e)
			{
				hash.put(field.getType().toString(), 1);
			}
		}

		a.setFieldTypes(hash);
		a.setNumberOfFields(visitor.getFields().size());
		a.setPrivateFields(privates);
		a.setProtectedFields(protecteds);
		a.setPublicFields(publics);
		return a;
	}

	private boolean isException(IType iType) throws JavaModelException
	{
		ITypeHierarchy typeTree = iType.newTypeHierarchy(new NullProgressMonitor());
		if (typeTree.getAllSuperclasses(iType).length > 1)
		{
			if (typeTree.getAllSuperclasses(iType)[1].getElementName().contains("Throwable")
					|| typeTree.getAllSuperclasses(iType)[1].getElementName().contains("Exception"))
			{
				return true;
			}
		}
		return false;
	}

	private int getNOC(IType iType) throws JavaModelException
	{
		ITypeHierarchy typeTree = iType.newTypeHierarchy(new NullProgressMonitor());
		return typeTree.getSubclasses(iType).length;
	}

	private int getDIT(IType iType) throws JavaModelException
	{
		ITypeHierarchy typeTree = iType.newTypeHierarchy(new NullProgressMonitor());
		return typeTree.getAllSuperclasses(iType).length + 1;
	}

	private void generateCSV(String path)
	{
		final String COMMA_DELIMITER = ";";
		final String NEW_LINE_SEPARATOR = "\n";
		String HEADER = "Class," + "Number of Methods," + "Public Methods," + "Private Methods," + "Protected Methods,"
				+ "Number of Fields," + "Public Fields," + "Private Fields," + "Protected Fields," + "Field Types,"
				+ "Number of Chilren," + "Depth of Inheritance," + "Is Exception";

		HEADER = HEADER.replaceAll(",", ";");

		try (FileWriter arquivo = new FileWriter(path + "analyze.csv"))
		{
			arquivo.append(HEADER);
			arquivo.append(NEW_LINE_SEPARATOR);
			for (ClassAnalyzer a : analisadores)
			{
				arquivo.append(a.getClasse());
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getPrivateMethods()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getPublicMethods()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getPrivateMethods()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getProtectedMethods()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getNumberOfFields()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getPublicFields()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getPrivateFields()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getProtectedFields()));
				arquivo.append(COMMA_DELIMITER);
				for (String s : a.getFieldTypes().keySet())
				{
					arquivo.append(s);
					arquivo.append(" => ");
					arquivo.append(String.valueOf(a.getFieldTypes().get(s)));
					arquivo.append(", ");
				}
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getNOC()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.getDIT()));
				arquivo.append(COMMA_DELIMITER);
				arquivo.append(String.valueOf(a.isException()));
				arquivo.append(NEW_LINE_SEPARATOR);
			}

		} catch (IOException e)
		{
			System.out.println("deu merda berg");
			e.printStackTrace();
		} finally
		{
			System.out.println(">>> Arquivo finalizado <<<");
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