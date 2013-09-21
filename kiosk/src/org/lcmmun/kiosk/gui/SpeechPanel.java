package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Speech;
import org.lcmmun.kiosk.Yield;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechEvent.SpeechEventType;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.gui.events.YieldActionListener;
import org.lcmmun.kiosk.gui.events.YieldEvent;
import org.lcmmun.kiosk.gui.events.YieldListener;
import org.lcmmun.kiosk.speechanalysis.AnalysisCue;
import org.lcmmun.kiosk.speechanalysis.AnalysisServer;
import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Context;

import tools.customizable.Time;

/**
 * A panel showing a delegate's progress in a speech.
 * 
 * @author William Chargin
 * 
 */
public class SpeechPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate speaking.
	 */
	private Delegate delegate;

	/**
	 * The delegate view.
	 */
	private final DelegateView view;

	/**
	 * The progress bar, showing the progress through the speech.
	 */
	private final TimeBar speechProgress;

	/**
	 * The list of listeners for this object.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * Whether yields are allowed.
	 */
	private final boolean yieldsAllowed;

	/**
	 * The yields panel.
	 */
	private final YieldsPanel yieldsPanel;

	/**
	 * Creates the panel with the given yield permission.
	 * 
	 * @param yieldsAllowed
	 *            whether yields are allowed
	 * @param committee
	 *            the relevant committee; this may be {@code null} only if
	 *            {@code yieldsAllowed} is {@code false}
	 */
	public SpeechPanel(boolean yieldsAllowed, Committee committee) {
		super(new BorderLayout());

		this.yieldsAllowed = yieldsAllowed;

		view = new DelegateView();
		add(view, BorderLayout.CENTER);

		speechProgress = new TimeBar();
		add(speechProgress, BorderLayout.NORTH);
		speechProgress.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				if (speechProgress.isFinished()) {
					stopSpeech();
				} else if (speechProgress.getMaximum()
						- speechProgress.getValue() == Speech.THRESHOLD) {
					SpeechEvent se = new SpeechEvent(SpeechPanel.this,
							delegate, SpeechEventType.ALMOST_FINISHED);
					fireSpeechEvent(se);
				}
			}
		});

		view.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (isEnabled() && delegate != null) {
					if (me.getClickCount() == 1) {
						pauseSpeech(); // or resume
					} else {
						// Double-click or higher. Stop.
						stopSpeech();
					}
				}
			}
		});

		if (this.yieldsAllowed) {
			yieldsPanel = new YieldsPanel(committee, this);
			add(yieldsPanel, BorderLayout.SOUTH);
		} else {
			yieldsPanel = null;
		}
	}

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
		return speechProgress.getValue();
	}

	public int getTotalSeconds() {
		return speechProgress.getMaximum();
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
	 * @param server
	 *            the analysis server
	 */
	public void startSpeech(Delegate delegate, Time speakingTime,
			AnalysisServer server) {
		stopSpeech();
		this.delegate = delegate;
		view.setDelegate(delegate);

		if (yieldsPanel != null) {
			yieldsPanel.setSpeakingDelegate(delegate);
		}

		if (server != null) {
			server.pushCue(new AnalysisCue(delegate.getName(), delegate
					.getAffiliation(), Context.SPEAKERS_LIST));
		}

		speechProgress.start(speakingTime);
	}

	/**
	 * Stops the current speech.
	 */
	public void stopSpeech() {
		if (delegate != null) {
			// There's a speech going.
			speechProgress.stop();
			Delegate speaking = delegate;
			delegate = null;
			for (SpeechListener sl : listenerList
					.getListeners(SpeechListener.class)) {
				sl.speechActionPerformed(new SpeechEvent(SpeechPanel.this,
						speaking, SpeechEventType.FINISHED));
			}
			view.setDelegate(null);
			if (yieldsPanel != null) {
				yieldsPanel.setSpeakingDelegate(null);
			}
		} // else { no_need_to_do_anything(); }
	}

	/**
	 * Sets the committee.
	 * 
	 * @param committee
	 *            the new committee
	 */
	public void setCommittee(Committee committee) {
		yieldsPanel.setCommittee(committee);
	}

	protected void yield(Yield yield) {
		final YieldEvent yieldEvent = new YieldEvent(this, delegate, yield);
		fireYieldEvent(yieldEvent);
		switch (yield.type) {
		case CHAIR:
			stopSpeech();
			return;
		case DELEGATE:
			view.setDelegate(delegate = yield.target);
			yieldsPanel.fireYieldEvent(yieldEvent);
			break;
		case COMMENTS:
		case QUESTIONS:
			fireYieldEvent(yieldEvent);
			speechProgress.stop();
			break;
		default:
			break;
		}
	}

	/**
	 * Fires the given yield event to all registered listeners.
	 * 
	 * @param yieldEvent
	 *            the event to fire
	 */
	protected void fireYieldEvent(YieldEvent yieldEvent) {
		for (YieldListener listener : listenerList
				.getListeners(YieldListener.class)) {
			listener.yield(yieldEvent);
		}
	}

	/**
	 * Gets the delegate currently speaking.
	 * 
	 * @return the delegate
	 */
	public Delegate getDelegate() {
		return delegate;
	}

	public void addYieldListener(YieldListener yl) {
		yieldsPanel.addYieldListener(yl);
	}

	public void removeYieldListener(YieldListener yl) {
		yieldsPanel.removeYieldListener(yl);
	}

	public void addYieldActionListener(YieldActionListener yal) {
		yieldsPanel.addYieldActionListener(yal);
	}

	public void removeYieldActionListener(YieldActionListener yal) {
		yieldsPanel.removeYieldActionListener(yal);
	}

	/**
	 * Pauses or resumes the current speech.
	 */
	public void pauseSpeech() {
		if (speechProgress.isRunning()) {
			speechProgress.pause();
		} else {
			speechProgress.resume();
		}
		fireSpeechEvent(new SpeechEvent(this, delegate, SpeechEventType.PAUSED));
	}

	private void fireSpeechEvent(SpeechEvent se) {
		for (SpeechListener sl : listenerList
				.getListeners(SpeechListener.class)) {
			sl.speechActionPerformed(se);
		}
	}

}
