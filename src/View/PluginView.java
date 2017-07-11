package View;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import br.ufrn.imd.controller.Controller;
import br.ufrn.imd.domain.Marker;

public class PluginView extends ViewPart
{
	private static TableViewer VIEWER;

	public void createPartControl(Composite parent)
	{
		VIEWER = new TableViewer(parent, SWT.VIRTUAL | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		createColumn(parent, VIEWER);

		final Table table = VIEWER.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		VIEWER.setContentProvider(ArrayContentProvider.getInstance());
		VIEWER.setInput(Controller.getViolations());

		selectionListenner();

		getSite().setSelectionProvider(VIEWER);
	}

	private void selectionListenner()
	{
		VIEWER.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = VIEWER.getStructuredSelection();
				Marker element = (Marker) selection.getFirstElement();

				openALine(element);
			}
		});
	}

	private void createColumn(final Composite parent, final TableViewer viewer)
	{
		TableViewerColumn column = createTableViewerColumn("Violations", 500);
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				Marker marker = (Marker) element;
				return super.getText(marker.getRule().toString());
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound)
	{
		final TableViewerColumn viewerColumn = new TableViewerColumn(VIEWER, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public void setFocus()
	{
		VIEWER.getControl().setFocus();
	}

	public static void insertViolations(String violation)
	{
		VIEWER.setInput(Controller.getViolations());
	}

	private void openALine(Marker marker)
	{
		IEditorPart openEditor = null;
		//Pegar caminho de forma dinamica, adicionar informação ao marcador talvez funcione
		String filePath = "F:\\Eclipse workspaces\\runtime-EclipseApplication\\PluginTest\\src\\app\\PluginTest.java";
		final IFile inputFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(filePath));
		if (inputFile != null)
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try
			{
				openEditor = IDE.openEditor(page, inputFile);
				if (openEditor instanceof ITextEditor)
				{
					ITextEditor textEditor = (ITextEditor) openEditor;
					textEditor.selectAndReveal(marker.getFirstIndex(), marker.getLastIndex() - marker.getFirstIndex());
				}
			} catch (PartInitException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}