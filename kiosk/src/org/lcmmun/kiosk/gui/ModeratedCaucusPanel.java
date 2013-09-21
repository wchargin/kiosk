package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.Speech;
import org.lcmmun.kiosk.gui.events.CaucusEvent;
import org.lcmmun.kiosk.gui.events.CaucusEvent.CaucusEventType;
import org.lcmmun.kiosk.gui.events.CaucusListener;
import org.lcmmun.kiosk.gui.events.DelegateSelectedEvent;
import org.lcmmun.kiosk.gui.events.DelegateSelectedListener;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechEvent.SpeechEventType;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.speechanalysis.AnalysisCue;
import org.lcmmun.kiosk.speechanalysis.AnalysisServer;
import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Context;

import tools.customizable.Time;

/**
 * A panel to facilitate running of a moderated caucus.
 * 
 * @author William Chargin
 * 
 */
public class ModeratedCaucusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The time allotted for each delegate to speak.
	 */
	private Time speakingTime;

	/**
	 * The time bar showing the total time for the caucus.
	 */
	private TimeBar tbTotal;

	/**
	 * The time bar showing the speaker timer.
	 */
	private TimeBar tbSpeaker;

	/**
	 * The delegate view.
	 */
	private DelegateView delegateView;

	/**
	 * The current delegate.
	 */
	private Delegate delegate;

	/**
	 * The {@code EventListenerList} for this object.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * The selection panel.
	 */
	private DelegateSelectionPanel dsp;

	/**
	 * The analysis server (or {@code null} if none is active).
	 */
	public AnalysisServer analysisServer;

	public ModeratedCaucusPanel() {
		super(new BorderLayout());

		JPanel pnlMain = new JPanel(new GridLayout(1, 2));
		add(pnlMain, BorderLayout.CENTER);

		dsp = new DelegateSelectionPanel(null, true);

		pnlMain.add(dsp);
		dsp.addDelegateSelectedListener(new DelegateSelectedListener() {
			@Override
			public void delegateSelected(DelegateSelectedEvent dse) {
				startSpeech(dse.delegate);
			}
		});

		JPanel pnlRight = new JPanel(new BorderLayout());
		pnlMain.add(pnlRight);

		JPanel pnlTime = new JPanel(new GridLayout(2, 1, 5, 5));
		pnlRight.add(pnlTime, BorderLayout.NORTH);

		tbTotal = new TimeBar();
		tbSpeaker = new TimeBar();

		tbTotal.setToolTipText(Messages
				.getString("ModeratedCaucusPanel.TooltipTotalTime")); //$NON-NLS-1$

		pnlTime.add(tbTotal);
		pnlTime.add(tbSpeaker);

		tbTotal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 1) {
					// Single-click; pause or resume.

					pauseResumeCaucus();

				} else {
					// 2x or more. End.
					if (JOptionPane.showConfirmDialog(
							ModeratedCaucusPanel.this,
							Messages.getString("Kiosk.CaucusReturnPrompt"), //$NON-NLS-1$
							Messages.getString("Kiosk.CaucusReturnTitle"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						stopCaucus();
					} else {
						// Before double-click came a pausing single-click.
						// Undo it.
						pauseResumeCaucus();
					}
				}
			}
		});

		tbSpeaker.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				if (tbSpeaker.isFinished()) {
					stopSpeech();
					// Is the caucus over?
					if (tbTotal.isFinished()) {
						stopCaucus();
					}
				} else if (tbSpeaker.getMaximum() - tbSpeaker.getValue() == Speech.THRESHOLD) {
					for (SpeechListener se : listenerList
							.getListeners(SpeechListener.class)) {
						se.speechActionPerformed(new SpeechEvent(
								ModeratedCaucusPanel.this, delegate,
								SpeechEventType.ALMOST_FINISHED));
					}
				}

			}
		});

		tbTotal.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				if (tbTotal.isFinished()) {
					// Caucus is over.
					// If no one's speaking...
					if (tbSpeaker.isFinished()) {
						stopCaucus();
					}
				}
			}
		});

		delegateView = new DelegateView();
		pnlRight.add(delegateView, BorderLayout.CENTER);

		delegateView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (delegateView.isEnabled() && delegate != null) {
					if (me.getClickCount() == 1) {
						// Pause.
						pauseResumeSpeech();
					} else {
						// Double-click or higher. Stop.
						stopSpeech();
					}
				}
			}
		});

	}

	/**
	 * Creates the panel, and sets the delegates to the delegates present in the
	 * given committee.
	 * 
	 * @param committee
	 *            the committee to use
	 */
	public ModeratedCaucusPanel(Committee committee) {
		this();
		setDelegates(committee.getPresentDelegates());
	}

	/**
	 * Adds the given {@code CaucusListener} to the list of listeners.
	 * 
	 * @param cel
	 *            the listener to add
	 */
	public void addCaucusListener(CaucusListener cel) {
		listenerList.add(CaucusListener.class, cel);
	}

	/**
	 * Adds the given {@code SpeechListener} to the list of listeners.
	 * 
	 * @param sl
	 *            the listener to add
	 */
	public void addSpeechListener(SpeechListener sl) {
		listenerList.add(SpeechListener.class, sl);
	}

	/**
	 * Removes the given {@code CaucusListener} from the list of listeners.
	 * 
	 * @param cel
	 *            the listener to remove
	 */
	public void removeCaucusListener(CaucusListener cel) {
		listenerList.remove(CaucusListener.class, cel);
	}

	/**
	 * Removes the given {@code SpeechListener} from the list of listeners.
	 * 
	 * @param safl
	 *            the listener to remove
	 */
	public void removeSpeechListener(SpeechListener safl) {
		listenerList.remove(SpeechListener.class, safl);
	}

	/**
	 * Sets the list of possible delegates to the new list.
	 * 
	 * @param newDelegates
	 *            the new delegates
	 */
	public void setDelegates(Collection<Delegate> newDelegates) {
		for (Delegate delegate : dsp.listModel.getList()) {
			if (!newDelegates.contains(delegate)) {
				dsp.listModel.remove(delegate);
			}
		}
		for (Delegate delegate : newDelegates) {
			if (!dsp.listModel.contains(delegate)) {
				dsp.listModel.add(delegate);
			}
		}
	}

	/**
	 * Starts the caucus with the given parameters.
	 * 
	 * @param totalTime
	 *            the total time of the caucus
	 * @param speakingTime
	 *            the time allotted for each delegate to speak
	 * @param purpose
	 *            the purpose of the caucus
	 */
	public void start(Time totalTime, Time speakingTime, String purpose) {
		this.speakingTime = speakingTime;
		tbTotal.start(totalTime);
		tbSpeaker.setValue(0);
		delegateView.setEnabled(true);
		delegateView.setDelegate(null);
	}

	/**
	 * Starts a speech for the given delegate.
	 * 
	 * @param delegate
	 *            the delegate to speak
	 * 
	 */
	protected void startSpeech(Delegate delegate) {
		delegateView.setDelegate(delegate);
		this.delegate = delegate;
		tbSpeaker.start(speakingTime);
		for (SpeechListener sl : listenerList
				.getListeners(SpeechListener.class)) {
			sl.speechActionPerformed(new SpeechEvent(this, delegate,
					SpeechEventType.STARTED));
		}
		delegateView.setToolTipText(Messages
				.getString("ModeratedCaucusPanel.TooltipSpeechTime")); //$NON-NLS-1$
		if (analysisServer != null) {
			analysisServer.pushCue(new AnalysisCue(delegate.getName(), delegate
					.getAffiliation(), Context.MODERATED_CAUCUS));
		}
	}

	/**
	 * Stops the current caucus.
	 */
	protected void stopCaucus() {
		// Don't call stopSpeech() - that would fire a speech event. Do it
		// manually.
		tbSpeaker.stop();
		delegateView.setDelegate(null);

		tbTotal.stop();
		for (CaucusListener sfe : listenerList
				.getListeners(CaucusListener.class)) {
			sfe.caucusActionPerformed(new CaucusEvent(
					ModeratedCaucusPanel.this, CaucusEventType.ELAPSED));
		}
	}

	/**
	 * Stops the current speech.
	 */
	private void stopSpeech() {
		tbSpeaker.stop();
		delegateView.setDelegate(null);
		for (SpeechListener sfe : listenerList
				.getListeners(SpeechListener.class)) {
			sfe.speechActionPerformed(new SpeechEvent(
					ModeratedCaucusPanel.this, delegate,
					SpeechEventType.FINISHED));
		}
		delegateView.setToolTipText(null);
	}

	/**
	 * Pauses or resumes the current caucus.
	 */
	private void pauseResumeCaucus() {
		final boolean currentlyRunning = tbTotal.isRunning();

		if (currentlyRunning) {
			// We need to pause it.
			tbTotal.pause();
			delegateView.setEnabled(false);
		} else {
			// We need to resume it.
			tbTotal.resume();
			delegateView.setEnabled(true);
		}

		for (CaucusListener cl : listenerList
				.getListeners(CaucusListener.class)) {
			cl.caucusActionPerformed(new CaucusEvent(ModeratedCaucusPanel.this,
					CaucusEventType.PAUSED));
		}

		// Is someone speaking?
		if (delegate != null) {
			if (currentlyRunning) {
				tbSpeaker.pause();
			} else {
				tbSpeaker.resume();
			}
			for (SpeechListener sl : listenerList
					.getListeners(SpeechListener.class)) {
				sl.speechActionPerformed(new SpeechEvent(
						ModeratedCaucusPanel.this, delegate,
						SpeechEventType.PAUSED));
			}
		}
	}

	private void pauseResumeSpeech() {
		if (tbSpeaker.isRunning()) {
			tbSpeaker.pause();
		} else {
			tbSpeaker.resume();
		}
		for (SpeechListener sl : listenerList
				.getListeners(SpeechListener.class)) {
			sl.speechActionPerformed(new SpeechEvent(ModeratedCaucusPanel.this,
					delegate, SpeechEventType.PAUSED));
		}
	}

}
