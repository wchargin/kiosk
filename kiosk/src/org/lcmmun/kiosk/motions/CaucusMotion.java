package org.lcmmun.kiosk.motions;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.gui.TimePresetProperty;

import tools.customizable.Time;
import tools.customizable.TimeProperty;

/**
 * A motion for a caucus of some sort.
 * 
 * @author William Chargin
 * 
 */
public abstract class CaucusMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The total amount of time for this caucus.
	 */
	protected Time totalTime;

	/**
	 * Creates the caucus motion, proposed by the given delegate.
	 * 
	 * @param proposingDelegate
	 *            the delegate who proposed the motion
	 */
	protected CaucusMotion(Delegate proposingDelegate) {
		super(proposingDelegate);

		totalTime = new Time(0, 10, 0);

		// Configure common feature of all caucuses: time.
		final TimeProperty tpTotalTime = new TimePresetProperty(Messages.getString("CaucusMotion.TotalTime"), //$NON-NLS-1$
				totalTime);
		propertySet.add(tpTotalTime);
		tpTotalTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				CaucusMotion.this.totalTime = tpTotalTime.getValue();
			}
		});
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.NONE;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	/**
	 * Gets the total time allotted for this caucus.
	 * 
	 * @return the total time
	 */
	public Time getTotalTime() {
		return totalTime;
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

}
