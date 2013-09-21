package org.lcmmun.kiosk.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A component that displays progress using a clipmask of an image.
 * 
 * @author William Chargin
 * 
 */
public class RadialImageProgressBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The image being drawn.
	 */
	private Image image;

	/**
	 * The percentage of completeness.
	 */
	private double percentage;

	/**
	 * Creates the progress bar with a {@code null} image.
	 */
	public RadialImageProgressBar() {
		setPreferredSize(new Dimension(256, 256));
	}

	/**
	 * Gets the image to be drawn.
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Gets the current completion percentage.
	 * 
	 * @return the percentage
	 */
	public double getPercentage() {
		return percentage;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			return;
		}

		final BufferedImage tempImage = ((Graphics2D) g)
				.getDeviceConfiguration().createCompatibleImage(getWidth(),
						getHeight(), Transparency.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) tempImage.getGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		final int smaller = getWidth() < getHeight() ? getWidth() : getHeight();
		final int w = smaller > image.getWidth(null) ? image.getWidth(null)
				: smaller;
		final int h = smaller > image.getHeight(null) ? image.getHeight(null)
				: smaller;
		final int x = (getWidth() - w) / 2;
		final int y = (getHeight() - h) / 2;

		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		Arc2D a2d = new Arc2D.Double(x, y, w, h, 90,
				360d - (percentage * 360d), Arc2D.PIE);
		g2d.setComposite(AlphaComposite.Src);
		g2d.fill(a2d);
		g2d.setComposite(AlphaComposite.SrcIn);
		g2d.drawImage(image, x, y, w, h, null);

		g.drawImage(tempImage, 0, 0, null);
	}

	/**
	 * Sets the image to be drawn and repaints.
	 * 
	 * @param image
	 *            the new image
	 */
	public void setImage(Image image) {
		this.image = image;
		repaint();
	}

	/**
	 * Sets the completion percentage and repaints.
	 * 
	 * @param percentage
	 *            the percentage
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
		repaint();
	}
}
