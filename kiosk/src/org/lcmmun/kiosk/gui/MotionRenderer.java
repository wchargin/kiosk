package org.lcmmun.kiosk.gui;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.gui.Kiosk.ProposedMotion;
import org.lcmmun.kiosk.motions.Motion;

/**
 * A renderer for a motion or proposed motion.
 * 
 * @author William Chargin
 * 
 */
public class MotionRenderer implements ListCellRenderer {

	private static DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

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
		Motion motion = (Motion) value;
		StringBuffer sb = new StringBuffer();
		sb.append("<html><strong>"); //$NON-NLS-1$
		boolean endedStrong = false;
		for (String desc : motion.getDescriptions()) {
			String endStrong = endedStrong ? "" : "</strong>"; //$NON-NLS-1$ //$NON-NLS-2$
			endedStrong = true;
			sb.append(desc + endStrong + "<br/>"); //$NON-NLS-1$
		}
		sb.append("</html>"); //$NON-NLS-1$
		final JLabel label = (JLabel) dlcr.getListCellRendererComponent(list,
				sb.toString(), index, isSelected, cellHasFocus);
		Delegate d = motion.getProposingDelegate();
		if (d != null) {
			label.setIcon(d.getSmallIcon());
		}
		label.setEnabled(motion.getResult() == null
				|| motion.getResult().passed);
		return label;
	}
}
