package org.lcmmun.kiosk.gui;

import javax.swing.JLabel;
import javax.swing.JList;

/**
 * A delegate renderer that renders the delegates twice as large.
 * 
 * @author William Chargin
 * 
 */
public class LargeDelegateRenderer extends DelegateRenderer {

	@Override
	public JLabel getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		label.setFont(label.getFont().deriveFont(
				(float) (label.getFont().getSize() * 2)));
		return label;
	}

}
