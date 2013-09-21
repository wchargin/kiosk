package org.lcmmun.kiosk.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.lcmmun.kiosk.Speech.YieldType;

/**
 * A renderer for yield types.
 * 
 * @author William Chargin
 * 
 */
public class YieldRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list,
				value instanceof YieldType ? ((YieldType) value).name : value,
				index, isSelected, cellHasFocus);
	}

}
