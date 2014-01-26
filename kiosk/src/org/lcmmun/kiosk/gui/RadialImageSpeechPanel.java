package org.lcmmun.kiosk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.gui.events.SpeechListener;

import tools.customizable.Time;

/**
 * A panel showing a delegate's progress in a speech.
 * 
 * @author William Chargin
 * 
 */
public class RadialImageSpeechPanel extends RadialImageProgressBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The number of seconds elapsed in this speech.
	 */
	private int secondsElapsed;

	/**
	 * The number of seconds for this speech.
	 */
	private int totalSeconds;

	/**
	 * The timer to update the progress bar.
	 */
	private final Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			setPercentage((double) (++secondsElapsed) / (double) (totalSeconds));
			if (secondsElapsed == totalSeconds) {
				setImage(null);
			}
		}
	});

	/**
	 * Adds the given {@code SpeechListener} to the list of listeners.
	 * 
	 * @param safl
	 *            the listener to add
	 */
	public void addSpeechListener(SpeechListener safl) {
		listenerList.add(SpeechListener.class, safl);
	}

	public int getSecondsElapsed() {
		return secondsElapsed;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	/**
	 * Removes the given {@code SpeechAlmostFinishedListener} from the list of
	 * listeners.
	 * 
	 * @param safl
	 *            the listener to remove
	 */
	public void removeSpeechAlmostFinishedListener(SpeechListener safl) {
		listenerList.remove(SpeechListener.class, safl);
	}

	/**
	 * Starts a speech for the given delegate with the given speaking time.
	 * 
	 * @param delegate
	 *            the delegate who will speak
	 * @param speakingTime
	 *            the amount of time the delegate will have to speak
	 */
	public void startSpeech(Delegate delegate, Time speakingTime) {
		stopSpeech();
		this.secondsElapsed = 0;
		this.totalSeconds = speakingTime.getTotalSeconds();
		setImage(delegate.getImage());
		setText(delegate.getName());
		setPercentage(0);
		timer.start();
	}

	/**
	 * Stops the current speech.
	 */
	public void stopSpeech() {
		timer.stop();
		secondsElapsed = totalSeconds = 0;
		setImage(null);
		setText(null);
	}

	/**
	 * Pauses or resumes the current speech.
	 */
	public void pauseSpeech() {
		if (!timer.isRunning()) {
			timer.start();
		} else {
			timer.stop();
		}
	}

}
