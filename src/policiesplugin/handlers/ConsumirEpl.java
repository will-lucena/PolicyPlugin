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
import epl.model.Compartment;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ConsumirEpl extends AbstractHandler {	
	
	public static Policy policy;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"PoliciesPlugin",
				"Hello, Eclipse world");
		return null;
		/**/
		
		String path = abrirArquivo();
		
		if (path != null)
		{
			construirPolicy(path);
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
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Falha ao abrir arquivo");
			return null;
		}
	}
	
	public void construirPolicy(String path)
	{
		policy = EPLParser.gerarPolicy(path);
		
		//epl.showCompartments(policy);
		//epl.showRules(policy);
		//printCompartments(policy);
		//printRules(policy);
		
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
		}
		else
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
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Falha ao gerar arquivo rules");
		}
	}
	
	private static boolean escreverArquivo(String texto, String nomeArquivo)
	{
		try (	FileWriter arquivo = new FileWriter(nomeArquivo);
				PrintWriter writer = new PrintWriter(arquivo);) 
		{
			for (String linha : texto.split("\n"))
			{
				writer.println(linha);
				writer.print("\n");
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
