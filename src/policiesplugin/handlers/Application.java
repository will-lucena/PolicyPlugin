package policiesplugin.handlers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import br.ufrn.imd.controller.Controller;
import epl.EPLParser;
import epl.model.Policy;

public class Application extends AbstractHandler
{
	private static Policy POLICY = null;
	private static final String SOURCE = "C:\\Users\\Will\\git\\PoliciesPlugin\\src\\util\\rules.epl";

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
				System.out.println(path);
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
}