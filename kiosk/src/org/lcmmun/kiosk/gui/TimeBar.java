package org.lcmmun.kiosk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JProgressBar;
import javax.swing.Timer;

import tools.customizable.Time;

/**
 * A progress bar for displaying time.
 * 
 * @author William Chargin
 * 
 */
public class TimeBar extends JProgressBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The total amount of time; the goal.
	 */
	private Time totalTime;

	/**
	 * The amount of time elapsed.
	 */
	private Time elapsed;

	/**
	 * The timer that manages the {@code TimeBar}.
	 */
	private final Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			setValue(getValue() + 1);
			if (getValue() == getMaximum()) {
				timer.stop();
			}
			elapsed = Time.fromSeconds(getValue());
			refreshString();
		}
	});

	/**
	 * Creates the time bar.
	 */
	public TimeBar() {
		setStringPainted(true);
		refreshString();
	}

	/**
	 * Determines whether the time bar is finished.
	 * 
	 * @return {@code true} if it is finished, or {@code false} otherwise
	 */
	public boolean isFinished() {
		return getValue() == getMaximum();
	}

	/**
	 * Determines if the timer is currently running.
	 * 
	 * @return {@code true} if the timer is running, or {@code false} otherwise
	 */
	public boolean isRunning() {
		return timer.isRunning();
	}

	/**
	 * Pauses the timer without changing any settings.
	 */
	public void pause() {
		timer.stop();
	}

	/**
	 * Refreshes the string displayed.
	 */
	private void refreshString() {
		final String placeholder = Character.toString(' ');
		if (isRunning()) {
			if (elapsed == null || totalTime == null) {
				setString(placeholder);
			} else {
				setString(elapsed.toString() + " / " + totalTime.toString()); //$NON-NLS-1$
			}
		} else {
			setString(placeholder);
		}
	}

	/**
	 * Resumes the timer without changing any settings.
	 */
	public void resume() {
		timer.start();
	}

	/**
	 * Starts or restarts the timer, counting until the given time.
	 * 
	 * @param totalTime
	 *            the total time
	 */
	public void start(Time totalTime) {
		this.totalTime = totalTime;
		elapsed = Time.fromSeconds(0);
		setMaximum(totalTime.getTotalSeconds());
		setValue(0);

		timer.restart();
		refreshString();
	}

	/**
	 * Stops and resets the timer.
	 */
	public void stop() {
		timer.stop();
		setValue(0);
		refreshString();
	}
}
