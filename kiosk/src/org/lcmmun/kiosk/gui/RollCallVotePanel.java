package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.DelegateIcon;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.Vote;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechEvent.SpeechEventType;
import org.lcmmun.kiosk.gui.events.SpeechListener;

import tools.customizable.Time;

/**
 * A panel that allows for processing of a roll call vote.
 * 
 * @author William Chargin
 * 
 */
public class RollCallVotePanel extends JPanel {

	/**
	 * 
	 //
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The panel containing the voting options for the first round.
	 */
	private final JPanel pnlFirstRound;

	/**
	 * The panel containing the voting options for the second round.
	 */
	private final JPanel pnlSecondRound;

	/**
	 * The card layout used for the voting panel.
	 */
	private final CardLayout layout = new CardLayout();

	/**
	 * The panel used to show the voting panels.
	 */
	private final JPanel pnlVoting = new JPanel(layout);

	/**
	 * The label displaying the current voter.
	 */
	private final JLabel lblCurrentVoter;

	/**
	 * The map of delegates to their votes.
	 */
	private final Map<Delegate, Vote> votes;

	/**
	 * The map of buttons in round one.
	 */
	private final Map<Vote, JButton> buttons_roundOne = new LinkedHashMap<Vote, JButton>();

	/**
	 * The map of buttons in round two.
	 */
	private final Map<Vote, JButton> buttons_roundTwo = new LinkedHashMap<Vote, JButton>();

	/**
	 * The delegate who is currently voting.
	 */
	private Delegate currentlyVoting;

	/**
	 * The thread that manages the votes.
	 */
	private Thread votingThread;

	/**
	 * The buttons used for casting votes.
	 */
	private Vector<JButton> votingButtons = new Vector<JButton>();

	/**
	 * Whether the item in question has been vetoed.
	 */
	private boolean vetoed;

	/**
	 * The event listener list for this object.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * Creates the panel for the given committee.
	 * 
	 * @param committee
	 *            the committee that will be voting
	 * @throws IllegalArgumentException
	 *             if there are no present delegates in the committee who can
	 *             vote, as determined by their member status
	 */
	public RollCallVotePanel(Committee committee)
			throws IllegalArgumentException {
		super(new BorderLayout());

		final ArrayList<Delegate> delegates = new ArrayList<Delegate>(
				committee.getPresentDelegates());
		Iterator<Delegate> it = delegates.iterator();
		while (it.hasNext()) {
			Delegate d = it.next();
			if (!d.getStatus().canVoteSubstantive) {
				// If it can't vote, we don't need to show it.
				it.remove();
			}
		}
		if (delegates.isEmpty()) {
			throw new IllegalArgumentException(
					Messages.getString("RollCallVotePanel.NoPresentDelegatesWithVotingRights")); //$NON-NLS-1$
		}
		Collections.sort(delegates); // just in case

		votes = new LinkedHashMap<Delegate, Vote>();
		for (Delegate delegate : delegates) {
			votes.put(delegate, null);
		}

		add(pnlVoting, BorderLayout.CENTER);

		pnlFirstRound = new JPanel(new GridLayout(3, 2, 5, 5));
		pnlFirstRound.setName(Messages.getString("RollCallVotePanel.VotingFirstRound")); //$NON-NLS-1$
		pnlVoting.add(pnlFirstRound, pnlFirstRound.getName());
		for (final Vote vote : Vote.values()) {
			JButton btn = new JButton(vote.vetoText);
			btn.setFocusable(false);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					// Prevent hyperclicking glitches
					for (JButton button : votingButtons) {
						button.setEnabled(false);
					}
					delegateVoted(vote);
				}
			});
			votingButtons.add(btn);
			pnlFirstRound.add(btn);
			buttons_roundOne.put(vote, btn);
		}

		pnlSecondRound = new JPanel(new GridLayout(1, 2, 5, 5));
		pnlSecondRound.setName(Messages.getString("RollCallVotePanel.VotingSecondRound")); //$NON-NLS-1$
		pnlVoting.add(pnlSecondRound, pnlSecondRound.getName());
		for (final Vote vote : Vote.values()) {
			if (!vote.inSecondRound) {
				continue;
			}
			JButton btn = new JButton(Character.toString(' '));
			btn.setFocusable(false);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					// Prevent hyperclicking glitches
					for (JButton button : votingButtons) {
						button.setEnabled(false);
					}
					delegateVoted(vote);
				}
			});
			votingButtons.add(btn);
			pnlSecondRound.add(btn);
			buttons_roundTwo.put(vote, btn);
		}

		lblCurrentVoter = new JLabel(Character.toString(' '));
		lblCurrentVoter.setIcon(new ImageIcon(new BufferedImage(
				DelegateIcon.IconSet.SMALL_SIZE,
				DelegateIcon.IconSet.SMALL_SIZE, BufferedImage.TYPE_INT_ARGB))); // blank
																					// for
																					// size
		lblCurrentVoter.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblCurrentVoter, BorderLayout.NORTH);
	}

	/**
	 * Allows those delegates who elected to vote with rights to speak for
	 * thirty seconds.
	 */
	public void beginSpeeches() {
		final ArrayList<Delegate> withRights = new ArrayList<Delegate>();
		for (Delegate delegate : votes.keySet()) {
			Vote vote = votes.get(delegate);
			if (vote == null) {
				continue;
			}
			if (vote.withRights) {
				withRights.add(delegate);
			}
		}
		if (withRights.isEmpty()) {
			return;
		}
		final SpeechPanel sp = new SpeechPanel(false, null);
		final Time speakingTime = new Time(0, 0, 30); // 30 seconds

		for (final Delegate delegate : withRights) {

			JPanel pnlMain = new JPanel(new BorderLayout());
			pnlMain.add(sp, BorderLayout.CENTER);

			final Vote vote = votes.get(delegate);
			final JLabel lblVote = new JLabel(Character.toString(' '));
			lblVote.setHorizontalAlignment(SwingConstants.CENTER);
			lblVote.setFont(lblVote.getFont()
					.deriveFont((float) (lblVote.getFont().getSize() * 2))
					.deriveFont(Font.BOLD));
			pnlMain.add(lblVote, BorderLayout.SOUTH);

			final Window ancestor = SwingUtilities
					.getWindowAncestor(RollCallVotePanel.this);
			final JDialog dialog = new JDialog(ancestor);
			dialog.setTitle(Messages.getString("RollCallVotePanel.SpeechPrefix") //$NON-NLS-1$
					+ delegate.getName()
					+ Messages.getString("RollCallVotePanel.SpeechSeparator") //$NON-NLS-1$
					+ (delegate.getStatus().hasVetoPower ? vote.vetoText
							: vote.normalText));
			dialog.setContentPane(pnlMain);
			dialog.pack();
			dialog.setLocationRelativeTo(ancestor);
			dialog.setModalityType(ModalityType.APPLICATION_MODAL);
			sp.addSpeechListener(new SpeechListener() {
				@Override
				public void speechActionPerformed(SpeechEvent se) {
					for (SpeechListener sfl : listenerList
							.getListeners(SpeechListener.class)) {
						sfl.speechActionPerformed(new SpeechEvent(
								RollCallVotePanel.this, delegate,
								SpeechEventType.FINISHED));
					}
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			sp.startSpeech(delegate, speakingTime, null);
			dialog.setVisible(true);
		}
	}

	/**
	 * Begins the voting procedures.
	 */
	public synchronized void beginVoting() {
		votingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// votes should be filled with [each delegate] -> `null`
				for (int i = 0; i < 2 /* rounds */; i++) {
					for (Delegate delegate : votes.keySet()) {
						if (votes.get(delegate) != null) {
							// Already voted.
							continue;
						}
						currentlyVoting = delegate;
						lblCurrentVoter.setText(delegate.getName());
						lblCurrentVoter.setIcon(delegate.getSmallIcon());

						@SuppressWarnings("unchecked")
						final List<Map<Vote, JButton>> maplist = Arrays.asList(
								buttons_roundOne, buttons_roundTwo);
						for (Map<Vote, JButton> map : maplist) {
							for (Vote vote : map.keySet()) {
								map.get(vote)
										.setText(
												delegate.getStatus().hasVetoPower ? vote.vetoText
														: vote.normalText);
							}
						}
						try {
							synchronized (votingThread) {
								votingThread.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						final Vote cast = votes.get(delegate);
						if (cast == Vote.PASS) {
							votes.put(delegate, null);
							// So he goes again.
						}
						for (JButton button : votingButtons) {
							button.setEnabled(true);
						}
					}
					layout.show(pnlVoting, pnlSecondRound.getName());
				}
				// When done, disable again.
				for (JButton button : votingButtons) {
					button.setEnabled(false);
				}
				currentlyVoting = null;
				lblCurrentVoter.setText(null);
				lblCurrentVoter.setIcon(null);
				pnlVoting.setVisible(false);
			}
		});
		votingThread.start();
	}

	/**
	 * Indicates that the currently voting delegate has cast a vote.
	 * 
	 * @param voteType
	 *            the vote the delegate cast
	 */
	private synchronized void delegateVoted(Vote voteType) {
		votes.put(currentlyVoting, voteType);
		if (currentlyVoting.getStatus().hasVetoPower && voteType.isVoteAgainst) {
			// A delegate with veto power voted against.
			vetoed = true;
		}
		synchronized (votingThread) {
			votingThread.notify();
		}
	}

	/**
	 * Gets the map of delegates to their votes. A {@code null} value indicates
	 * that a delegate has either not voted at all, or has elected to pass and
	 * not voted in the second round.
	 * 
	 * @return the map
	 */
	public Map<Delegate, Vote> getVotes() {
		return new LinkedHashMap<Delegate, Vote>(votes);
	}

	/**
	 * Determines whether any delegate with veto power voted no (vetoed).
	 * 
	 * @return {@code true} if the item was vetoed, or {@code false} if it was
	 *         not
	 */
	public boolean isVetoed() {
		return vetoed;
	}
}
