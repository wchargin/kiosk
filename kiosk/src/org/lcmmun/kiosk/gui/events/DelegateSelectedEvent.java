package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

import org.lcmmun.kiosk.Delegate;

/**
 * An event indicating that a delegate has been selected in an interface.
 * 
 * @author William Chargin
 * 
 */
public class DelegateSelectedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate who was selected.
	 */
	public final Delegate delegate;

	public DelegateSelectedEvent(Object source, Delegate delegate) {
		super(source);
		this.delegate = delegate;
	}

}
