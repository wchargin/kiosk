package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Yield;

/**
 * An event fired when a delegate performs a yield of any type.
 * 
 * @author William Chargin
 * 
 */
public class YieldEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate who yielded.
	 */
	public final Delegate delegate;

	/**
	 * How the delegate yielded.
	 */
	public final Yield yield;

	/**
	 * Creates the event with the given delegate and yield.
	 * 
	 * @param source
	 *            the source
	 * @param delegate
	 *            the delegate
	 * @param yield
	 *            the yield
	 */
	public YieldEvent(Object source, Delegate delegate, Yield yield) {
		super(source);
		this.delegate = delegate;
		this.yield = yield;
	}

}
