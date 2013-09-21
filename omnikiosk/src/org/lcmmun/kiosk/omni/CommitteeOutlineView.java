package org.lcmmun.kiosk.omni;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.broadcast.CommitteeOutline;
import org.lcmmun.kiosk.omni.resources.ImageFetcher;

/**
 * A view for a committee outline.
 * 
 * @author William Chargin
 * 
 */
public class CommitteeOutlineView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The label showing the committee name, whose icon shows the status..
	 */
	private final JLabel lblCommitteeName = new JLabel();

	/**
	 * The label containing the current topic.
	 */
	private final JLabel lblTopic = new JLabel();

	/**
	 * The chair comments.
	 */
	private final JLabel lblComments = new JLabel();

	/**
	 * The label indicating when this was last updated.
	 */
	private final JLabel lblUpdated = new JLabel();

	/**
	 * The time since this was last updated.
	 */
	private int updatedTime;

	/**
	 * The button labeled "Remove." It is up to the parent component to actually
	 * remove the view.
	 */
	private final JButton btnRemove = new JButton("Remove");

	/**
	 * The timer that updates the {@link #lblUpdated} label.
	 */
	private final Timer updatedTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			boolean probablyDisconnected = updatedTime >= 30;
			lblCommitteeName.setForeground(probablyDisconnected ? Color.GRAY
					: Color.BLACK);
			btnRemove.setFont(btnRemove.getFont().deriveFont(
					probablyDisconnected ? (Font.BOLD) : (~Font.BOLD)));
			lblUpdated.setText(++updatedTime < 5 ? "Just now"
					: (updatedTime + (updatedTime == 1 ? "second ago"
							: " seconds ago")));
			float red = updatedTime < 10 ? 0f : (updatedTime - 10) / 10f;
			if (red > 1f) {
				red = 1f;
			}
			lblUpdated.setForeground(new Color(red, 0f, 0f));
		}
	});

	/**
	 * Creates the committee outline view.
	 */
	public CommitteeOutlineView() {
		super(new MigLayout(new LC().flowY()));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblCommitteeName.setFont(lblCommitteeName.getFont()
				.deriveFont(Font.BOLD)
				.deriveFont(lblCommitteeName.getFont().getSize() * 1.5f));
		lblUpdated.setFont(lblUpdated.getFont().deriveFont(Font.BOLD)
				.deriveFont(lblUpdated.getFont().getSize() * 0.75f));
		for (JLabel lbl : new JLabel[] { lblCommitteeName, lblTopic,
				lblComments, lblUpdated }) {
			lbl.setText(Character.toString(' ')); // for pack()'s benefit
			add(lbl, new CC().grow().pushX());
			lbl.setIcon(new ImageIcon(new BufferedImage(16, 16,
					BufferedImage.TYPE_INT_ARGB)));
		}
		add(btnRemove, new CC().grow().pushX());
	}

	/**
	 * Updates the components of this view to match the given outline.
	 * 
	 * @param outline
	 *            the outline to display
	 */
	public void updateForOutline(CommitteeOutline outline) {
		lblCommitteeName.setText(outline.name + ' ');
		lblCommitteeName.setIcon(ImageFetcher
				.fetchImageIcon(outline.state.imageType));
		lblCommitteeName.setToolTipText(outline.state.description);
		lblTopic.setText(outline.topic + ' ');

		final int sideLength = 16;
		BufferedImage imageWP_ResoCount = new BufferedImage(sideLength,
				sideLength, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) imageWP_ResoCount.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.BLACK);
		g2d.drawOval(0, 0, imageWP_ResoCount.getWidth() - 1,
				imageWP_ResoCount.getHeight() - 1);
		String s = Integer.toString(outline.wprCount);
		final float desiredPadding = (float) ((1.5 / Math.sqrt(2)) * (sideLength / 2));
		final float desiredWidth = sideLength - (desiredPadding);
		final float testSize = 100f;
		final float testWidth = g2d.getFontMetrics(
				g2d.getFont().deriveFont(testSize)).stringWidth(s);
		final float desiredSize = testSize * (desiredWidth / testWidth);
		g2d.setFont(g2d.getFont().deriveFont(desiredSize));
		int x = (int) Math.round((sideLength - desiredWidth) / 2);
		int y = (int) Math.round((sideLength + (g2d.getFontMetrics()
				.getAscent() * 2f / 3f)) / 2);
		g2d.drawString(s, x, y);
		lblTopic.setIcon(new ImageIcon(imageWP_ResoCount));
		lblTopic.setToolTipText("This topic has "
				+ s
				+ " "
				+ (outline.wprCount == 1 ? "working paper/resolution"
						: " working papers/resolutions"));

		lblComments.setText(outline.comments + ' ');
		lblUpdated.setText("Just now");
		updatedTime = 0;
		updatedTimer.stop();
		updatedTimer.start();
	}

	/**
	 * Adds the given {@link ActionListener} to the "Remove" button.
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addRemoveClickedListener(ActionListener l) {
		btnRemove.addActionListener(l);
	}

	/**
	 * Removes the given {@link ActionListener} from the "Remove" button.
	 * 
	 * @param l
	 *            the listener to remove
	 */
	public void removeRemoveClickedListener(ActionListener l) {
		btnRemove.removeActionListener(l);
	}

}
