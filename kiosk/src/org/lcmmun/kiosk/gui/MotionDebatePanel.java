package org.lcmmun.kiosk.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.gui.events.DelegateSelectedEvent;
import org.lcmmun.kiosk.gui.events.DelegateSelectedListener;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechEvent.SpeechEventType;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.motions.Motion;
import org.lcmmun.kiosk.motions.Motion.Debatability;

import tools.customizable.Time;

/**
 * A debate panel for motions, allowing speakers for and against.
 * 
 * @author William Chargin
 * 
 */
public class MotionDebatePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The maximum number of speakers on any one side.
	 */
	private static final int MAX_SPEAKERS = 2;

	/**
	 * The number of speakers for, so far.
	 */
	private int speakersFor = 0;

	/**
	 * The number of speakers against, so far.
	 */
	private int speakersAgainst = 0;

	/**
	 * The time allotted for these speeches.
	 */
	public static final Time TIME = new Time(0, 0, 30);

	/**
	 * Whether the "for" side is speaking.
	 */
	private boolean isFor;

	/**
	 * The list of in-favor listeners.
	 */
	private EventListenerList llInFavor = new EventListenerList();

	/**
	 * The list of against-listeners.
	 */
	private EventListenerList llAgainst = new EventListenerList();

	/**
	 * Creates the motion debate panel for the given motion.
	 * 
	 * @param motion
	 *            the motion to be debated
	 * @param committee
	 *            the committee of delegates participating
	 * @throws NullPointerException
	 *             if the motion's debatability is {@code null}
	 * @throws IllegalArgumentException
	 *             if the motion's debatability is {@link Debatability#NONE}
	 */
	public MotionDebatePanel(final Motion motion, Committee committee)
			throws NullPointerException, IllegalArgumentException {
		final Debatability debatability = motion.getDebatability();
		if (debatability == null) {
			throw new NullPointerException();
		} else if (debatability == Debatability.NONE) {
			throw new IllegalArgumentException();
		}

		final boolean forAndAgainst = debatability == Debatability.FOR_AND_AGAINST;

		// If it's both for and against, then the for side starts.
		// If it's not, then the against side starts.
		isFor = forAndAgainst;

		setLayout(new MigLayout());

		final DelegateSelectionPanel dspFor = new DelegateSelectionPanel(null,
				true);
		final DelegateSelectionPanel dspAgainst = new DelegateSelectionPanel(
				null, true);

		final JLabel lblSpeaking = new JLabel(Character.toString(' '));
		lblSpeaking.setFont(lblSpeaking.getFont()
				.deriveFont((float) (lblSpeaking.getFont().getSize() * 2))
				.deriveFont(Font.BOLD));
		lblSpeaking.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpeaking.setAlignmentX(Component.CENTER_ALIGNMENT);

		final SpeechPanel panel = new SpeechPanel(false, null);

		for (Delegate delegate : committee.getPresentDelegates()) {
			dspFor.listModel.add(delegate);

			if (delegate != motion.getProposingDelegate()) {
				// You can't speak against your own motion.
				dspAgainst.listModel.add(delegate);
			}
		}

		add(lblSpeaking, new CC().spanX().growX().pushX().wrap());
		if (forAndAgainst) {
			add(dspFor, new CC().grow().push());
		}
		add(panel, new CC().grow().push());
		add(dspAgainst, new CC().grow().push());

		// Done with layout. Now: controllers.
		if (forAndAgainst) {
			dspFor.setEnabled(true);
			dspAgainst.setEnabled(false);
		} else {
			dspFor.setEnabled(false);
			dspAgainst.setEnabled(true);
		}
		DelegateSelectedListener dsl = new DelegateSelectedListener() {
			@Override
			public void delegateSelected(DelegateSelectedEvent dse) {
				if (isFor) {
					speakersFor++;
				} else {
					speakersAgainst++;
				}
				lblSpeaking
						.setText(isFor ? Messages
								.getString("MotionDebatePanel.InFavor") : Messages.getString("MotionDebatePanel.Against")); //$NON-NLS-1$ //$NON-NLS-2$
				lblSpeaking.setForeground(Color.getHSBColor(isFor ? (1f / 3f)
						: 0f, 1f, 0.75f));
				dspFor.setEnabled(false);
				dspAgainst.setEnabled(false);

				final Delegate delegate = dse.delegate;

				dspFor.listModel.remove(delegate);
				dspAgainst.listModel.remove(delegate);

				panel.startSpeech(delegate, TIME, null);

			}
		};

		panel.addSpeechListener(new SpeechListener() {
			@Override
			public void speechActionPerformed(SpeechEvent se) {
				for (SpeechListener sl : (isFor ? llInFavor : llAgainst)
						.getListeners(SpeechListener.class)) {
					sl.speechActionPerformed(se);
				}
				if (se.type == SpeechEventType.FINISHED) {
					// If we do both, it swaps. Otherwise, against.
					final boolean newFor;
					if (forAndAgainst) {
						newFor = !isFor;
					} else {
						newFor = false;
					}
					int count = (newFor ? speakersFor : speakersAgainst);
					if (count < MAX_SPEAKERS) {
						(newFor ? dspFor : dspAgainst).setEnabled(true);
					}
					lblSpeaking.setText(count < MAX_SPEAKERS ? Character
							.toString(' ') : Messages
							.getString("MotionDebatePanel.Finished")); //$NON-NLS-1$
					lblSpeaking.setForeground(Color.getHSBColor(2f / 3f, 1f,
							0.75f));
					isFor = newFor;
				}
			}
		});

		dspFor.addDelegateSelectedListener(dsl);
		dspAgainst.addDelegateSelectedListener(dsl);
	}

	/**
	 * Adds a speech listener for speeches against.
	 * 
	 * @param sl
	 *            the speech listener to add
	 */
	public void addSpeechAgainstListener(SpeechListener sl) {
		llAgainst.add(SpeechListener.class, sl);
	}

	/**
	 * Adds a speech listener for in-favor speeches.
	 * 
	 * @param sl
	 *            the speech listener to add
	 */
	public void addSpeechInFavorListener(SpeechListener sl) {
		llInFavor.add(SpeechListener.class, sl);
	}

	/**
	 * Adds a speech listener for speeches against.
	 * 
	 * @param sl
	 *            the speech listener to remove
	 */
	public void removeSpeechAgainstListener(SpeechListener sl) {
		llAgainst.remove(SpeechListener.class, sl);
	}

	/**
	 * Removes a speech listener for in-favor speeches.
	 * 
	 * @param sl
	 *            the speech listener to remove
	 */
	public void removeSpeechInFavorListener(SpeechListener sl) {
		llInFavor.remove(SpeechListener.class, sl);
	}

}
