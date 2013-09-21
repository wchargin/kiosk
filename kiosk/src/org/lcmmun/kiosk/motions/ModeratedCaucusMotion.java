package org.lcmmun.kiosk.motions;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

import tools.customizable.TextProperty;
import tools.customizable.Time;
import tools.customizable.TimeProperty;

/**
 * A motion for a moderated caucus. In a moderated caucus, debate is still
 * regulated, but instead of a speakers' list, delegates must raise their
 * placards each time they wish to speak.
 * 
 * @author William Chargin
 * 
 */
public class ModeratedCaucusMotion extends CaucusMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The time for each speaker in this caucus.
	 */
	private Time speakingTime;

	/**
	 * The provided purpose for this caucus.
	 */
	private String purpose = new String();

	/**
	 * Creates the motion for a moderated caucus.
	 * 
	 * @param proposingDelegate
	 *            the delegate who proposed the motion
	 */
	public ModeratedCaucusMotion(Delegate proposingDelegate) {
		super(proposingDelegate);

		speakingTime = new Time(0, 0, 30);
		final TimeProperty tpSpeakingTime = new TimeProperty(Messages.getString("ModeratedCaucusMotion.SpeakingTime"), //$NON-NLS-1$
				speakingTime);
		propertySet.add(tpSpeakingTime);
		tpSpeakingTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				ModeratedCaucusMotion.this.speakingTime = tpSpeakingTime
						.getValue();
			}
		});

		final TextProperty purpose = new TextProperty(Messages.getString("ModeratedCaucusMotion.PropertyPurpose"), new String()); //$NON-NLS-1$
		propertySet.add(purpose);
		purpose.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ModeratedCaucusMotion.this.purpose = purpose.getValue();
			}
		});

	}

	@Override
	public ArrayList<String> getDescriptions() {
		ArrayList<String> desc = super.getDescriptions();
		desc.add(totalTime.toString() + " @ " + speakingTime.toString()); //$NON-NLS-1$
		desc.add(purpose.length() > 32 ? (purpose.substring(0, 32) + " ... ") //$NON-NLS-1$
				: purpose);
		return desc;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("ModeratedCaucusMotion.ModeratedCaucus"); //$NON-NLS-1$
	}

	/**
	 * Gets the purpose of this moderated caucus.
	 * 
	 * @return the provided purpose
	 */
	public String getPurpose() {
		return purpose;
	}

	/**
	 * Gets the time allotted for each speaker in this caucus.
	 * 
	 * @return the individual speaking time
	 */
	public Time getSpeakingTime() {
		return speakingTime;
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}
}
