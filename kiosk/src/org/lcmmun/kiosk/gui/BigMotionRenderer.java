package org.lcmmun.kiosk.gui;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.lcmmun.kiosk.gui.Kiosk.ProposedMotion;
import org.lcmmun.kiosk.motions.Motion;

public class BigMotionRenderer implements ListCellRenderer {

	private static MotionRenderer mr = new MotionRenderer();

	@Override
	public JLabel getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof ProposedMotion) {
			@SuppressWarnings("unchecked")
			final ProposedMotion<? extends Motion> proposedMotion = (ProposedMotion<? extends Motion>) value;
			value = proposedMotion.motion;
		}
		if (!(value instanceof Motion)) {
			throw new IllegalArgumentException("!(value instanceof Motion): " //$NON-NLS-1$
					+ value.getClass().getName());

		}
		JLabel label = mr.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		label.setFont(label.getFont()
				.deriveFont(label.getFont().getSize() * 2f));
		return label;
	}

}
