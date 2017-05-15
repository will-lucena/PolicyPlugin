package policiesplugin.handlers;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

public class PluginView extends ViewPart
{
	private static Label label;
	
	
	public static void updateLabel(String texto)
	{
		label.setText(label.getText() + "\n" + texto);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		label = new Label(parent, 0);
	}

	@Override
	public void setFocus()
	{
		label.setFocus();		
	}

}
