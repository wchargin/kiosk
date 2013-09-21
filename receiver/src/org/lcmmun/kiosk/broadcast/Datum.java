package org.lcmmun.kiosk.broadcast;

import java.io.Serializable;
import java.util.Arrays;

import javax.swing.ImageIcon;

/**
 * A datum (piece of information) that updates chairs about the status of the
 * committee.
 * 
 * @author William Chargin
 * 
 */
public class Datum implements Serializable {

	/**
	 * Creates the datum with an optional list of arguments of variable length.
	 * 
	 * @param args
	 *            the arguments
	 */
	public Datum(ImageIcon icon, Object... args) {
		super();
		this.icon = icon;
		this.args = args;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of arguments.
	 */
	protected final Object[] args;

	/**
	 * The icon for this datum. This may be {@code null}.
	 */
	protected final ImageIcon icon;

	/**
	 * Gets the text to be displayed.
	 * 
	 * @return the text
	 */
	public String getDisplayText() {
		return String.format(args[0].toString(), Arrays.<Object> asList(args)
				.subList(1, args.length).toArray());
	}

	/**
	 * Gets the icon to be displayed alongside this datum. This may be
	 * {@code null}; if this is the case, no icon should be displayed.
	 * 
	 * @return the icon to be displayed
	 */
	public ImageIcon getIcon() {
		return icon;
	}
}
