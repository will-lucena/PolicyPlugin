package View;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import excite.Violation;

public class PluginView extends ViewPart
{
	private static TableViewer viewer;
	private Violation violation;
	private static Label label;

	public void createPartControl(Composite parent)
	{
		label = new Label(parent, 0);
		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.BORDER);
		createColumn(parent, viewer);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(violation);
		
		getSite().setSelectionProvider(viewer);
	}
	
	private void createColumn(final Composite parent, final TableViewer viewer)
	{
		TableViewerColumn column = createTableViewerColumn("Violations", 200);
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				Violation v = (Violation) element;
				return super.getText(v);
			}
		});
	}
	
	private TableViewerColumn createTableViewerColumn(String title, int bound)
	{
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
	}

	public void setFocus()
	{
		viewer.getControl().setFocus();
		label.setFocus();
	}

	public static void insertViolations(String violation)
	{
		label.setText(label.getText() + "\n" + violation);
	}
}