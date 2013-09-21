package org.lcmmun.kiosk.gui;

import org.lcmmun.kiosk.Messages;

/**
 * A view state for a committee.
 * 
 * @author William Chargin
 * 
 */
public enum ViewState {
	STANDARD_DEBATE(Messages.getString("ViewState.StandardDebate")), WPR_VIEW(Messages.getString("ViewState.ViewDocuments")), CRISIS_VIEW( //$NON-NLS-1$ //$NON-NLS-2$
			Messages.getString("ViewState.ViewCrises")); //$NON-NLS-1$

	/**
	 * The name of this view state.
	 */
	public final String name;

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Creates the {@code ViewState} with the given name.
	 * 
	 * @param name
	 *            the name.
	 */
	private ViewState(String name) {
		this.name = name;
	}

}
