package org.lcmmun.kiosk.broadcast.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.broadcast.Datum;
import org.lcmmun.kiosk.broadcast.Messages;

/**
 * A visual representation of a {@link Datum}.
 * 
 * @author William Chargin
 * 
 */
public class DatumView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Whether this view has already disappeared.
	 */
	private boolean disappeared = false;

	/**
	 * The timer that allows the view to disappear after a set amount of time.
	 */
	private Timer disappearTimer = new Timer(15 * 1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			disappear();
			disappearTimer.stop();
		}
	});

	/**
	 * Creates the view from a given {@link Datum}.
	 * 
	 * @param datum
	 *            the datum to be displayed
	 */
	public DatumView(Datum datum) {
		super(new MigLayout());
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 0));

		ImageIcon icon = datum.getIcon();
		add(new JLabel(icon == null ? new ImageIcon(new BufferedImage(24, 24,
				BufferedImage.TYPE_INT_ARGB)) : icon), new CC().dockEast());

		String displayText = datum.getDisplayText();
		if (displayText != null
				&& !(displayText = displayText.trim()).isEmpty()) {
			JTextArea txtr = new JTextArea(displayText);
			txtr.setFont(new Font(Font.SANS_SERIF, txtr.getFont().getStyle(),
					txtr.getFont().getSize()));
			txtr.setRows(4);
			txtr.setEditable(false);
			txtr.setDisabledTextColor(txtr.getForeground());
			txtr.setEnabled(false);
			txtr.setLineWrap(true);
			txtr.setWrapStyleWord(true);

			JScrollPane scpn = new JScrollPane(txtr);
			scpn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			add(scpn, new CC().grow().pushX());
		}

		JButton btnDelete = new JButton(Messages.getString("DatumView.DeleteButton")); //$NON-NLS-1$
		add(btnDelete, new CC().dockSouth());
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				disappear();
			}
		});
		disappearTimer.start();
	}

	/**
	 * Attempts to find a {@code DatumHolder} in the hierarchy, and call its
	 * {@link DatumHolder#removeDatumView(DatumView)} method; if unsuccessful,
	 * calls {@link #removeAll()} and {@link #setVisible(boolean)
	 * setVisible(false)}.
	 */
	private void disappear() {
		if (disappeared) {
			// No need.
			return;
		}
		Component ancestor = SwingUtilities.getAncestorOfClass(
				DatumHolder.class, DatumView.this);
		if (ancestor != null) {
			DatumHolder holder = (DatumHolder) ancestor;
			holder.removeDatumView(DatumView.this);
		} else {
			removeAll();
			setVisible(false);
		}
		disappeared = true;
	}
}
