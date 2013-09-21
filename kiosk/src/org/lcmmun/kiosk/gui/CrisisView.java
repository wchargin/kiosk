package org.lcmmun.kiosk.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Crisis;
import org.lcmmun.kiosk.Messages;

/**
 * A renderer for a crisis.
 * 
 * @author William Chargin
 * 
 */
public class CrisisView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The label containing the crisis name.
	 */
	private final JLabel lblCrisisName;

	/**
	 * The label containing the guest speaker's name.
	 */
	private final JLabel lblGuestSpeaker;

	/**
	 * The label containing the file name.
	 */
	private final JLabel lblBriefing;

	/**
	 * The color for the deployment band.
	 */
	private Color color;

	/**
	 * The deployment color band.
	 */
	private JComponent band = new JComponent() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			setMinimumSize(new Dimension(8, 1));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			RoundRectangle2D rr2d = new RoundRectangle2D.Double(0, 0,
					getWidth() - 1, getHeight() - 1, getWidth(), getWidth());
			g2d.setColor(color);
			g2d.fill(rr2d);

			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			g2d.setColor(Color.BLACK);
			g2d.draw(rr2d);
		}
	};

	public CrisisView() {
		super(new MigLayout(new LC().flowY()));

		add(band, new CC().growY().spanY().wrap());
		add(Box.createHorizontalStrut(5), new CC().growY().spanY().wrap());

		final CC cc = new CC().growX().pushX();

		lblCrisisName = new JLabel();
		add(lblCrisisName, cc);
		lblCrisisName.setFont(lblCrisisName.getFont().deriveFont(Font.BOLD));

		lblGuestSpeaker = new JLabel();
		add(lblGuestSpeaker, cc);

		lblBriefing = new JLabel();
		add(lblBriefing, cc);
	}

	/**
	 * Updates the components of this {@code CrisisView} to match the given
	 * crisis, and then repaints.
	 * 
	 * @param crisis
	 *            the crisis
	 */
	public void setCrisis(Crisis crisis) {
		lblCrisisName.setText(crisis.getName() + ' ');
		lblGuestSpeaker.setText(crisis.isGuestSpeaker() ? crisis
				.getGuestSpeakerName() : Messages.getString("CrisisView.NoGuestSpeaker") + ' '); //$NON-NLS-1$
		final File file = crisis.getFile();
		lblBriefing.setText(file == null ? Messages.getString("CrisisView.NoBriefing") //$NON-NLS-1$
				: (file.getName() + ' '));
		color = crisis.isDeployed() ? Color.GREEN : Color.RED;
		repaint();
	}
}
