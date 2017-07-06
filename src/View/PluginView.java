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

import excite.AplicacaoJar;
import excite.Marker;

public class PluginView extends ViewPart
{
	private static TableViewer viewer;

	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		createColumn(parent, viewer);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(AplicacaoJar.getViolations());

		selectionListenner();

		getSite().setSelectionProvider(viewer);
	}

	private void selectionListenner()
	{
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = viewer.getStructuredSelection();
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
				Marker m = (Marker) element;
				return super.getText(m.getRule().toString());
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
	}

	public static void insertViolations(String violation)
	{
		viewer.setInput(AplicacaoJar.getViolations());
	}

	private void openALine(Marker marker)
	{
		IEditorPart openEditor = null;
		String filePath = "C:\\Users\\William\\workspace\\runtime-Default\\TestEpl\\src\\main\\Rules.java";
		final IFile inputFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(Path.fromOSString(filePath));
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