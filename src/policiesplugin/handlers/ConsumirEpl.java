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
import excite.AplicacaoJar;
import excite.verifiers.Verifier;
import epl.model.Compartment;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ConsumirEpl extends AbstractHandler
{
	private static Policy policy = null;
	private static final String path = "C:\\Users\\William\\workspace\\teste.epl";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			if (path != null)
			{
				Verifier.getInstance();
				construirPolicy(path);
				new AplicacaoJar();
			}
			return null;
		} catch (IllegalArgumentException ex)
		{
			JOptionPane.showMessageDialog(null, "Arquivo invalido");
			String path = abrirArquivo();
			if (path != null)
			{
				Verifier.getInstance();
				construirPolicy(path);
				new AplicacaoJar();
			}
			return null;
		}
		
	}

	public static Policy getPolicy()
	{
		if (policy != null)
		{
			return policy;
		}
		return null;
	}
	
	private String abrirArquivo()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("C:/Users/William/workspace/runtime-Default/test/src"));
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			return chooser.getSelectedFile().getAbsolutePath();
		} else
		{
			JOptionPane.showMessageDialog(null, "Falha ao abrir arquivo");
			return null;
		}
	}

	public void construirPolicy(String path)
	{
		policy = EPLParser.gerarPolicy(path);
		JOptionPane.showMessageDialog(null, "Arquivo epl consumido com sucesso");
	}

	public static void printCompartments()
	{
		StringBuilder sb = new StringBuilder();
		for (Compartment c : policy.getCompartments())
		{
			sb.append(c);
		}
		if (escreverArquivo(sb.toString(), "compartments.txt"))
		{
			JOptionPane.showMessageDialog(null, "Arquivo compartments gerado com sucesso");
		} else
		{
			JOptionPane.showMessageDialog(null, "Falha ao gerar arquivo compartments");
		}
	}

	public static void printRules()
	{
		StringBuilder sb = new StringBuilder();
		for (Rule r : policy.getRules())
		{
			sb.append(r);
			sb.append("\n");
		}
		if (escreverArquivo(sb.toString(), "rules.txt"))
		{
			JOptionPane.showMessageDialog(null, "Arquivo rules gerado com sucesso");
		} else
		{
			JOptionPane.showMessageDialog(null, "Falha ao gerar arquivo rules");
		}
	}

	private static boolean escreverArquivo(String texto, String nomeArquivo)
	{
		try (FileWriter arquivo = new FileWriter("C:/Users/William/Documents/" + nomeArquivo);
				PrintWriter writer = new PrintWriter(arquivo);)
		{
			for (String linha : texto.split("\n"))
			{
				writer.println(linha);
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