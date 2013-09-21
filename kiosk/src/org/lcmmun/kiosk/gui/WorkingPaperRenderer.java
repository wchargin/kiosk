package org.lcmmun.kiosk.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.WorkingPaper;

/**
 * A renderer for a working paper.
 * 
 * @author William Chargin
 * 
 */
public class WorkingPaperRenderer implements ListCellRenderer {

	/**
	 * The default list cell renderer.
	 */
	private static final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if (!(value instanceof WorkingPaper)) {
			throw new IllegalArgumentException(
					"!(value instanceof WorkingPaper)"); //$NON-NLS-1$
		}
		WorkingPaper wp = (WorkingPaper) value;

		final File file = wp.getFile();
		String text = "<html><span style=\"line-height: 120%\"><strong>" //$NON-NLS-1$
				+ wp.getIdentifier() + "</strong>" + "<br/>" //$NON-NLS-1$ //$NON-NLS-2$
				+ wp.getSubmitterString() + "<br/>" //$NON-NLS-1$
				+ (file == null ? Messages.getString("WorkingPaperRenderer.NoAssociatedFile") : (file.getName())) //$NON-NLS-1$
				+ "</span></html>"; //$NON-NLS-1$
		return dlcr.getListCellRendererComponent(list, text, index, isSelected,
				cellHasFocus);
	}
}
