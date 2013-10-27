package org.lcmmun.kiosk.motions;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.gui.TimePresetProperty;

import tools.customizable.CounterProperty;
import tools.customizable.Time;
import tools.customizable.TimeProperty;

/**
 * A motion to change the speaking time of the General Speakers' List.
 * 
 * @author William Chargin
 * 
 */
public class SpeakingTimeMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The new speaking time.
	 */
	private Time time;

	/**
	 * The new comment count.
	 */
	private int commentCount;

	/**
	 * The new comment time.
	 */
	private Time commentTime;

	/**
	 * Creates the motion with the given proposing delegate and starting time.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 * @param startingTime
	 *            the current speaking time
	 * @param startingCommentCount
	 *            the current comment count
	 * @param startingCommentTime
	 *            the current comment time
	 */
	public SpeakingTimeMotion(Delegate proposingDelegate, Time startingTime,
			int startingCommentCount, Time startingCommentTime) {
		super(proposingDelegate);

		time = startingTime;
		commentCount = startingCommentCount;
		commentTime = startingCommentTime;

		final TimeProperty tpSpeakingTime = new TimePresetProperty(
				Messages.getString("SpeakingTimeMotion.PropertySpeakingTime"), time); //$NON-NLS-1$
		tpSpeakingTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				time = tpSpeakingTime.getValue();
			}
		});
		propertySet.add(tpSpeakingTime);

		final CounterProperty cpCommentCount = new CounterProperty(
				Messages.getString("SpeakingTimeMotion.PropertyCommentCount"), startingCommentCount); //$NON-NLS-1$
		cpCommentCount.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				commentCount = cpCommentCount.getValue();
			}
		});
		propertySet.add(cpCommentCount);

		final TimeProperty tpCommentTime = new TimePresetProperty(
				Messages.getString("SpeakingTimeMotion.PropertyCommentTime"), //$NON-NLS-1$
				startingCommentTime);
		tpCommentTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				commentTime = tpCommentTime.getValue();
			}
		});
		propertySet.add(tpCommentTime);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.NONE;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("SpeakingTimeMotion.SetSpeakingTime"); //$NON-NLS-1$
	}

	/**
	 * Gets the new speaking time, as proposed by this motion.
	 * 
	 * @return the new speaking time
	 */
	public Time getTime() {
		return time;
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

	@Override
	public ArrayList<String> getDescriptions() {
		ArrayList<String> desc = super.getDescriptions();
		desc.add(time.toString()
				+ Messages.getString("SpeakingTimeMotion.DescriptionWith") //$NON-NLS-1$
				+ (commentCount > 0 ? (commentCount
						+ " " //$NON-NLS-1$
						+ (commentCount == 1 ? Messages
								.getString("SpeakingTimeMotion.DescriptionCommentSingular") : Messages.getString("SpeakingTimeMotion.DescriptionCommentPlural")) + " @ " + commentTime //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						.toString())
						: Messages
								.getString("SpeakingTimeMotion.DescriptionNoComments"))); //$NON-NLS-1$
		return desc;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public Time getCommentTime() {
		return commentTime;
	}

}
