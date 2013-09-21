package org.lcmmun.kiosk.omni;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.lcmmun.kiosk.CommitteeState;

/**
 * An outline of a committee. The properties of instances of this class will be
 * updated when committees "check in" with the OmniKiosk.
 * 
 * @author William Chargin
 * 
 */
public class CommitteeOutline {

	/**
	 * The index of this committee in the list of committees.
	 */
	public final int index;

	/**
	 * The total number of committee outlines.
	 */
	public static int count;

	/**
	 * The name of the committee.
	 */
	private String name;

	/**
	 * The state of the committee.
	 */
	private CommitteeState state;

	/**
	 * Any comments from the chairs of the committee regarding its status.
	 */
	private String comments;

	/**
	 * The list of listeners registered on this object.
	 */
	private final transient EventListenerList listenerList = new EventListenerList();

	/**
	 * Creates the outline.
	 */
	public CommitteeOutline() {
		index = count++;
	}

	/**
	 * Adds the given listener to the list of listeners.
	 * 
	 * @param cl
	 *            the listener to add
	 */
	public void addChangeListener(ChangeListener cl) {
		listenerList.add(ChangeListener.class, cl);
	}

	/**
	 * Fires a {@link ChangeEvent} to all registered listeners.
	 */
	private void fireChangeEvent() {
		for (ChangeListener cl : listenerList
				.getListeners(ChangeListener.class)) {
			cl.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Gets the chairs' comments about this committee.
	 * 
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Gets the name of the committee.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the state of the committee.
	 * 
	 * @return the state
	 */
	public CommitteeState getState() {
		return state;
	}

	/**
	 * Removes the given listener from the list of listeners.
	 * 
	 * @param cl
	 *            the listener to remove
	 */
	public void removeChangeListener(ChangeListener cl) {
		listenerList.remove(ChangeListener.class, cl);
	}

	/**
	 * Sets the comments of this committee.
	 * 
	 * @param comments
	 *            the new comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
		fireChangeEvent();
	}

	/**
	 * Sets the name of the committee.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
		fireChangeEvent();
	}

	/**
	 * Sets the state of the committee.
	 * 
	 * @param state
	 *            the new state
	 */
	public void setState(CommitteeState state) {
		this.state = state;
		fireChangeEvent();
	}

}
