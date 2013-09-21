package org.lcmmun.kiosk;

import java.io.File;
import java.io.Serializable;

import tools.customizable.Time;

/**
 * A crisis event.
 * 
 * @author William Chargin
 * 
 */
public class Crisis implements Serializable, Document {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of this crisis.
	 */
	private String name;

	/**
	 * The file attached to this crisis.
	 */
	private File briefing;

	/**
	 * Whether this crisis has been deployed.
	 */
	private boolean deployed;

	/**
	 * Whether there is a guest speaker for this crisis.
	 */
	private boolean guestSpeaker;

	/**
	 * The name of the guest speaker ({@code null} if there is none).
	 */
	private String guestSpeakerName;

	/**
	 * The occupation of the guest speaker (e.g., "CEO, Google"; {@code null} if
	 * there is none).
	 */
	private String guestSpeakerOccupation;

	/**
	 * The amount of time allotted for Q&A ({@code null} if no questions will be
	 * entertained).
	 */
	private Time qaTime;

	/**
	 * The string for the Google Docs ID for this document.
	 * <p>
	 * An example resolution is available with ID:
	 * 
	 * <pre>
	 * <code>1GiiqHRYnWr1sXZjXsk38HGWsLB0dr1pbPRTndDqS_O0</code>
	 * </pre>
	 */
	private String googleDocsId;

	/**
	 * Gets the name of this crisis.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this crisis.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the briefing file displayed during this crisis.
	 * 
	 * @return the briefing
	 */
	public File getFile() {
		return briefing;
	}

	/**
	 * Sets the briefing displayed during this crisis.
	 * 
	 * @param briefing
	 *            the new briefing
	 */
	public void setFile(File briefing) {
		this.briefing = briefing;
	}

	/**
	 * Determines whether this caucus has been deployed.
	 * 
	 * @return whether this caucus has been deployed.
	 */
	public boolean isDeployed() {
		return deployed;
	}

	/**
	 * Sets whether this crisis has been deployed.
	 * 
	 * @param deployed
	 *            whether the crisis has been deployed
	 */
	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}

	/**
	 * Determines whether there will be a guest speaker for this crisis.
	 * 
	 * @return whether there will be a guest speaker for this crisis
	 */
	public boolean isGuestSpeaker() {
		return guestSpeaker;
	}

	/**
	 * Sets whether there will be a guest speaker for this crisis.
	 * 
	 * @param guestSpeaker
	 *            whether there will be a guest speaker
	 */
	public void setGuestSpeaker(boolean guestSpeaker) {
		this.guestSpeaker = guestSpeaker;
	}

	/**
	 * Gets the guest speaker's name.
	 * 
	 * @return the guest speaker's name
	 */
	public String getGuestSpeakerName() {
		return guestSpeakerName;
	}

	/**
	 * Sets the guest speaker's name.
	 * 
	 * @param guestSpeakerName
	 *            the guest speaker's name
	 */
	public void setGuestSpeakerName(String guestSpeakerName) {
		this.guestSpeakerName = guestSpeakerName;
	}

	/**
	 * Gets the guest speaker occupation.
	 * 
	 * @return the occupation
	 */
	public String getGuestSpeakerOccupation() {
		return guestSpeakerOccupation;
	}

	/**
	 * Sets the guest speaker occupation.
	 * 
	 * @param guestSpeakerOccupation
	 *            the occupation
	 */
	public void setGuestSpeakerOccupation(String guestSpeakerOccupation) {
		this.guestSpeakerOccupation = guestSpeakerOccupation;
	}

	/**
	 * Gets the time for the Q&A session.
	 * 
	 * @return the time, or {@code null} if there is none
	 */
	public Time getQaTime() {
		return qaTime;
	}

	/**
	 * Sets the time for the Q&A session.
	 * 
	 * @param qaTime
	 *            the new time, or {@code null} if there is no Q&A
	 */
	public void setQaTime(Time qaTime) {
		this.qaTime = qaTime;
	}

	/**
	 * Gets the Google Docs ID for this document.
	 * 
	 * @return the Google docs ID
	 */
	public String getGoogleDocsId() {
		return googleDocsId;
	}

	/**
	 * Sets the Google Docs ID for this document.
	 * 
	 * @param googleDocsId
	 *            the Google docs ID
	 */
	public void setGoogleDocsId(String googleDocsId) {
		this.googleDocsId = googleDocsId;
	}

	/**
	 * Creates the default, placeholder crisis.
	 * 
	 * @param number
	 *            the number of this crisis (name will be
	 *            {@code "Crisis " + number})
	 * @return the crisis
	 */
	public static Crisis createDefaultCrisis(int number) {
		Crisis crisis = new Crisis();
		crisis.setName(Messages.getString("Crisis.DefaultCrisisNamePrefix") + number); //$NON-NLS-1$
		return crisis;
	}

}
