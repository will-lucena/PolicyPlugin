package policiesplugin.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import epl.EPLParser;
import epl.model.Policy;
import epl.model.Rule;
import excite.Controller;
import epl.model.Compartment;


public class Application extends AbstractHandler
{
	private static Policy POLICY = null;
	private static final String SOURCE = "src\\util\\rules.epl";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			if (SOURCE != null)
			{
				buildPolicy(SOURCE);
				new Controller();
			}
			return null;
		} catch (IllegalArgumentException ex)
		{
			JOptionPane.showMessageDialog(null, "Invalid source");
			String path = openArchive();
			if (path != null)
			{
				buildPolicy(path);
				new Controller();
			}
			return null;
		}
		
	}

	public static Policy getPolicy()
	{
		if (POLICY != null)
		{
			return POLICY;
		}
		return null;
	}
	
	private String openArchive()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("C:/Users/William/workspace/runtime-Default/test/src"));
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			return chooser.getSelectedFile().getAbsolutePath();
		} else
		{
			JOptionPane.showMessageDialog(null, "Fail to open archive");
			return null;
		}
	}

	public void buildPolicy(String path)
	{
		POLICY = EPLParser.gerarPolicy(path);
		JOptionPane.showMessageDialog(null, "Policy built successfully");
	}

	public static void printCompartments()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (Compartment compartment : POLICY.getCompartments())
		{
			stringBuilder.append(compartment);
		}
		if (writeArchive(stringBuilder.toString(), "compartments.txt"))
		{
			JOptionPane.showMessageDialog(null, "Compartments archive generated successfully");
		} else
		{
			JOptionPane.showMessageDialog(null, "Fail to generate compartments archive");
		}
	}

	public static void printRules()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (Rule rule : POLICY.getRules())
		{
			stringBuilder.append(rule);
			stringBuilder.append("\n");
		}
		if (writeArchive(stringBuilder.toString(), "rules.txt"))
		{
			JOptionPane.showMessageDialog(null, "Rules archive generated successfully");
		} else
		{
			JOptionPane.showMessageDialog(null, "Fail to generate rules archive");
		}
	}

	private static boolean writeArchive(String text, String archiveName)
	{
		try (FileWriter archive = new FileWriter("C:/Users/William/Documents/" + archiveName);
				PrintWriter writer = new PrintWriter(archive);)
		{
			for (String line : text.split("\n"))
			{
				writer.println(line);
				writer.print("\n");
			}
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}