package org.lcmmun.kiosk.motions;

import java.io.Serializable;
import java.util.ArrayList;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.MotionResult;

import tools.customizable.PropertySet;

/**
 * A motion in an MUN debate. Delegates (or the committee director) may move to
 * change the flow of debate (a procedural motion; e.g., enter a moderated
 * caucus) or the substance of debate (a substantive motion; e.g., vote on a
 * resolution).
 * 
 * @author William Chargin
 * 
 */
public interface Motion extends Serializable {

	/**
	 * The extent to which a motion is debatable.
	 * 
	 * @author William Chargin
	 * 
	 */
	public enum Debatability {
		/**
		 * Indicates that the motion may not be debated.
		 */
		NONE,

		/**
		 * Indicates that speakers against the motion will be entertained, but
		 * not speakers for the motion.
		 */
		AGAINST_ONLY,

		/**
		 * Indicates that speakers both for and against the motion will be
		 * entertained.
		 */
		FOR_AND_AGAINST;
	}

	/**
	 * Determines whether speakers for and against this motion will be
	 * entertained.
	 * 
	 * @return the debatability of this motion
	 */
	public Debatability getDebatability();

	/**
	 * Gets the list of descriptions.
	 * 
	 * @return the list of descriptions
	 */
	public ArrayList<String> getDescriptions();

	/**
	 * Gets the majority type for this motion.
	 * 
	 * @return the majority type
	 */
	public MajorityType getMajorityType();

	/**
	 * Gets the name of this motion.
	 * 
	 * @return the name of the motion (e.g., "moderated caucus")
	 */
	public String getMotionName();

	/**
	 * Gets the property set required to customize the motion.
	 * 
	 * @return the property set
	 */
	public PropertySet getPropertySet();

	/**
	 * Gets the delegate who proposed the motion.
	 * 
	 * @return the proposing delegate
	 */
	public Delegate getProposingDelegate();

	/**
	 * Gets the result of this motion (did it pass? fail? did the committee
	 * vote, or did the chairs decide?). This may be {@code null} if it hasn't
	 * yet been voted on.
	 * 
	 * @return the result
	 * @see MotionResult
	 */
	public MotionResult getResult();

	/**
	 * Determines if this motion is substantive or not. Delegates may abstain
	 * from voting only on substantive matters.
	 * 
	 * @return {@code true} if this motion is substantive, or {@code false} if
	 *         it is procedural
	 */
	public boolean isSubstantive();

	/**
	 * Sets which delegate proposed the motion.
	 * 
	 * @param proposingDelegate
	 *            the delegate who proposed the motion
	 */
	public void setProposingDelegate(Delegate proposingDelegate);

	/**
	 * Sets the result of this motion.
	 * 
	 * @param result
	 *            the new result.
	 */
	public void setResult(MotionResult result);

}
