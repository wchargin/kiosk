package org.lcmmun.kiosk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lcmmun.kiosk.resources.flags.FileNameGuesser;
import org.lcmmun.kiosk.resources.flags.FileNameGuesser.Result;

/**
 * An icon for a delegate (or, rather, an icon generator).
 * 
 * @author William Chargin
 * 
 */
public class DelegateIcon implements Serializable {

	/**
	 * A set of large and small images.
	 * 
	 * @author William Chargin
	 * 
	 */
	public static class IconSet {

		/**
		 * The side length of the large size icon.
		 */
		public static final int LARGE_SIZE = 256;

		/**
		 * The side length of the small size icon.
		 */
		public static final int SMALL_SIZE = 24;

		/**
		 * The large image.
		 */
		private BufferedImage large;

		/**
		 * The small image.
		 */
		private BufferedImage small;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The type of this icon. This defaults to the colored orb icon type.
	 */
	private IconType type = IconType.COLOR;

	/**
	 * The map of icon types to icon sets.
	 */
	private transient Map<IconType, IconSet> icons = new LinkedHashMap<IconType, IconSet>();

	/**
	 * The color used for the {@link IconType#COLOR} icon type.
	 */
	private Color color;

	/**
	 * The name of the flag for the {@link IconType#FLAG} icon type.
	 */
	private String flag;

	/**
	 * The path to the image used in the {@link IconType#IMAGE} icon type.
	 */
	private File pathToImage;

	/**
	 * Creates the icon with the given type.
	 * 
	 * @param type
	 *            the type
	 */
	public DelegateIcon(IconType type) {
		setType(type);
	}

	/**
	 * Gets the color of the icon. This only matters when the type is
	 * {@link IconType#COLOR}.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets the name of the flag. This only matters when the type is
	 * {@link IconType#FLAG}.
	 * 
	 * @return the name of the flag
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * Gets the large icon for the current type.
	 * 
	 * @return the large icon
	 */
	public BufferedImage getLargeIcon() {
		return icons.get(getType()).large;
	}

	/**
	 * Gets the path to the image. This only matters when the type is
	 * {@link IconType#IMAGE}.
	 * 
	 * @return the image path
	 */
	public File getPathToImage() {
		return pathToImage;
	}

	/**
	 * Gets the small icon for the current type.
	 * 
	 * @return the small icon
	 */
	public BufferedImage getSmallIcon() {
		return icons.get(getType()).small;
	}

	/**
	 * Gets the current icon type.
	 * 
	 * @return the icon type
	 */
	public IconType getType() {
		return type;
	}

	/**
	 * Custom deserialization logic allows the setup of the icon map.
	 */
	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		icons = new LinkedHashMap<IconType, DelegateIcon.IconSet>();
		updateIcon(type);
	}

	/**
	 * Copies all settings from the given icon to this one.
	 * 
	 * @param icon
	 *            the icon whence to copy settings
	 */
	public void set(DelegateIcon icon) {
		pathToImage = icon.pathToImage;
		color = icon.color;
		flag = icon.flag;
		icons.clear();
		icons.putAll(icon.icons);
		type = icon.type;
	}

	/**
	 * Sets the color of the icon and updates it. This only matters when the
	 * type is {@link IconType#COLOR}.
	 * 
	 * @param color
	 *            the new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Sets the name of the flag and updates the icon. This only matters when
	 * the type is {@link IconType#FLAG}.
	 * 
	 * @param flag
	 *            the new flag name
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * Sets the path to the image and updates the icon. This only matters when
	 * the type is {@link IconType#IMAGE}.
	 * 
	 * @param pathToImage
	 *            the new image path
	 */
	public void setPathToImage(File pathToImage) {
		this.pathToImage = pathToImage;
	}

	/**
	 * Sets the icon type and updates the icon.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(IconType type) {
		this.type = type;
		updateIcon(type);
	}

	/**
	 * Updates the given icon type.
	 * 
	 * @param type
	 *            the type to update.
	 */
	private void updateIcon(IconType type) {
		// Check for null and set type.
		type = (type == null ? IconType.NONE : type);
		this.type = type;

		// Set up images.
		IconSet iconSet = new IconSet();
		icons.put(type, iconSet);

		BufferedImage biLarge = new BufferedImage(IconSet.LARGE_SIZE,
				IconSet.LARGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		BufferedImage biSmall = new BufferedImage(IconSet.SMALL_SIZE,
				IconSet.SMALL_SIZE, BufferedImage.TYPE_INT_ARGB);
		iconSet.large = biLarge;
		iconSet.small = biSmall;

		Graphics2D g2dLarge = (Graphics2D) biLarge.getGraphics();
		Graphics2D g2dSmall = (Graphics2D) biSmall.getGraphics();
		g2dLarge.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2dSmall.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Save values.
		Shape originalClipLarge = g2dLarge.getClip();
		Shape originalClipSmall = g2dSmall.getClip();

		// Draw image.
		switch (type) {
		case COLOR:
			// Outlined orb with highlights.
			final int outlineWidthLarge = 4; // pixels
			final int outlineWidthSmall = 2; // pixels

			if (color == null) {
				color = Color.LIGHT_GRAY;
			}
			Color fillTop = color;
			Color fillBottom = fillTop.darker();

			GradientPaint gpFillLarge = new GradientPaint(0, 0, fillTop, 0,
					IconSet.LARGE_SIZE, fillBottom);
			GradientPaint gpFillSmall = new GradientPaint(0, 0, fillTop, 0,
					IconSet.SMALL_SIZE, fillBottom);

			Ellipse2D eFullLarge = new Ellipse2D.Double(0, 0,
					IconSet.LARGE_SIZE - 1, IconSet.LARGE_SIZE - 1);
			Ellipse2D eInsetLarge = new Ellipse2D.Double(outlineWidthLarge / 2,
					outlineWidthLarge / 2, IconSet.LARGE_SIZE
							- outlineWidthLarge - 1, IconSet.LARGE_SIZE
							- outlineWidthLarge - 1);
			Ellipse2D eFullSmall = new Ellipse2D.Double(0, 0,
					IconSet.SMALL_SIZE - 1, IconSet.SMALL_SIZE - 1);
			Ellipse2D eInsetSmall = new Ellipse2D.Double(outlineWidthSmall / 2,
					outlineWidthSmall / 2, IconSet.SMALL_SIZE
							- outlineWidthSmall - 1, IconSet.SMALL_SIZE
							- outlineWidthSmall - 1);
			g2dLarge.setPaint(gpFillLarge);
			g2dSmall.setPaint(gpFillSmall);
			g2dLarge.clip(eFullLarge);
			g2dSmall.clip(eFullSmall);
			g2dLarge.fill(eInsetLarge);
			g2dSmall.fill(eInsetSmall);

			Color highlightTop = new Color(255, 255, 255, 255 / 6);
			Color highlightBottom = new Color(255, 255, 255, 255 / 3);
			GradientPaint gpHighlightLarge = new GradientPaint(0, 0,
					highlightTop, 0, IconSet.LARGE_SIZE / 2, highlightBottom);
			GradientPaint gpHighlightSmall = new GradientPaint(0, 0,
					highlightTop, 0, IconSet.SMALL_SIZE / 2, highlightBottom);

			g2dLarge.setPaint(gpHighlightLarge);
			g2dSmall.setPaint(gpHighlightSmall);

			Ellipse2D eHighlightLarge = new Ellipse2D.Double(
					-IconSet.LARGE_SIZE / 4, -IconSet.LARGE_SIZE / 1.5,
					IconSet.LARGE_SIZE * 1.5, IconSet.LARGE_SIZE);
			Ellipse2D eHighlightSmall = new Ellipse2D.Double(
					-IconSet.SMALL_SIZE / 4, -IconSet.SMALL_SIZE / 1.5,
					IconSet.SMALL_SIZE * 1.5, IconSet.SMALL_SIZE);
			g2dLarge.fill(eHighlightLarge);
			g2dSmall.fill(eHighlightSmall);

			g2dLarge.setClip(new Rectangle2D.Double(0, 0, IconSet.LARGE_SIZE,
					IconSet.LARGE_SIZE));
			g2dSmall.setClip(new Rectangle2D.Double(0, 0, IconSet.SMALL_SIZE,
					IconSet.SMALL_SIZE));
			g2dLarge.setStroke(new BasicStroke(outlineWidthLarge));
			g2dSmall.setStroke(new BasicStroke(outlineWidthSmall));
			g2dLarge.setColor(Color.BLACK);
			g2dSmall.setColor(Color.BLACK);
			g2dLarge.draw(eInsetLarge);
			g2dSmall.draw(eInsetSmall);

			break;
		case FLAG:
			if (flag == null) {
				flag = new String();
			}
			Result guess = FileNameGuesser.guess(flag);
			if (guess == null || guess.icon == null || !guess.confident) {
				// Couldn't find it. Leave blank.
				break;
			} else {
				Image i = guess.icon.getImage();
				g2dLarge.drawImage(i, 0, 0, IconSet.LARGE_SIZE,
						IconSet.LARGE_SIZE, null);
				g2dSmall.drawImage(i, 0, 0, IconSet.SMALL_SIZE,
						IconSet.SMALL_SIZE, null);
			}
			break;
		case IMAGE:
			try {
				if (pathToImage == null) {
					// No image. Leave blank.
					break;
				} else {
					Image i = ImageIO.read(pathToImage);
					Image large = i.getScaledInstance(IconSet.LARGE_SIZE,
							IconSet.LARGE_SIZE, Image.SCALE_SMOOTH);
					Image small = i.getScaledInstance(IconSet.SMALL_SIZE,
							IconSet.SMALL_SIZE, Image.SCALE_SMOOTH);
					g2dLarge.drawImage(large, 0, 0, null);
					g2dSmall.drawImage(small, 0, 0, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case NONE:
		default:
			// Don't draw on the images. Leave them blank.
			break;
		}
		g2dLarge.setClip(originalClipLarge);
		g2dSmall.setClip(originalClipSmall);
	}

}
