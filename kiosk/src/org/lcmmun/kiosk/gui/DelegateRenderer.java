package org.lcmmun.kiosk.gui;

import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.lcmmun.kiosk.Delegate;

/**
 * A renderer for a {@link Delegate}. This class uses a
 * {@link DefaultListCellRenderer} to do its job, and provides it with the
 * {@code Delegate}'s {@code name} as a value.
 * 
 * @author William Chargin
 * 
 */
public class DelegateRenderer implements ListCellRenderer {

	/**
	 * The default list cell renderer.
	 */
	private static final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

	@Override
	public JLabel getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null || !(value instanceof Delegate)) {
			return (JLabel) dlcr.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);
		}
		Delegate delegate = (Delegate) value;
		JLabel label = (JLabel) dlcr.getListCellRendererComponent(list,
				delegate.getName(), index, isSelected, cellHasFocus);
		label.setIcon(delegate.getSmallIcon());
		int mask = 0;
		if (!delegate.getStatus().canVoteSubstantive) {
			mask |= Font.ITALIC;
		}
		if (delegate.getStatus().hasVetoPower) {
			mask |= Font.BOLD;
		}
		label.setFont(label.getFont().deriveFont(mask));
		return label;
	}
}
