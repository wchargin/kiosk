package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.DelegateIcon.IconSet;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.Speech.YieldType;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.gui.events.YieldActionEvent;
import org.lcmmun.kiosk.gui.events.YieldActionEvent.CommentActionType;
import org.lcmmun.kiosk.gui.events.YieldActionEvent.QuestionActionType;
import org.lcmmun.kiosk.gui.events.YieldActionListener;
import org.lcmmun.kiosk.gui.events.YieldEvent;
import org.lcmmun.kiosk.gui.events.YieldListener;

import tools.customizable.Time;

/**
 * The GSL widget for the public display.
 * 
 * @author William Chargin
 * 
 */
public class PublicDisplayGSLPanel extends JPanel implements
		YieldActionListener, YieldListener, SpeechListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The main progress bar.
	 */
	private final RadialImageProgressBar ripbMain;

	/**
	 * The progress bar for the yielded speaker.
	 */
	private final RadialImageProgressBar ripbYielded;

	/**
	 * The speech timer.
	 */
	private final Timer tmSpeech = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			(yieldedSpeaker == null ? ripbMain : ripbYielded)
					.setPercentage((double) (++secondsElapsed)
							/ (double) (totalTime.getTotalSeconds()));
			if (secondsElapsed >= totalTime.getTotalSeconds()) {
				tmSpeech.stop();
			}
		}
	});

	/**
	 * The current speaker.
	 */
	private Delegate speaker;

	/**
	 * The delegate to whom time was yielded.
	 */
	private Delegate yieldedSpeaker;

	/**
	 * The number of seconds elapsed in the current speech, or {@code -1} if no
	 * speech is ongoing.
	 */
	private int secondsElapsed;

	/**
	 * The total speaking time.
	 */
	private Time totalTime;

	/**
	 * The card layout for the yield panel.
	 */
	private final CardLayout clYield = new CardLayout();

	/**
	 * The panel displaying the yield status.
	 */
	private final JPanel pnlYield = new JPanel(clYield);

	/**
	 * The panel for the {@link YieldType#COMMENTS} yield type.
	 */
	private final JPanel ypnlComments;

	/**
	 * The panel for the {@link YieldType#QUESTIONS} yield type.
	 */
	private final JPanel ypnlQuestions;

	/**
	 * The panel for the {@link YieldType#DELEGATE} yield type.
	 */
	private final JPanel ypnlDelegate;

	/**
	 * The committee.
	 */
	private Committee committee;

	/**
	 * The current comment number. This can be:
	 * <ul>
	 * <li><strong><code>-1</code></strong> &ndash; if the delegate did not
	 * yield to comments</li>
	 * <li><strong><code>&nbsp;0</code></strong> &ndash; if the delegate yielded
	 * to comments but no comment has started</li>
	 * <li><strong>a positive integer</strong> &ndash; representing the current
	 * comment number (one-based)
	 * </ul>
	 */
	protected int commentNumber = -1;

	/**
	 * The label indicating the current comment number.
	 */
	private JLabel lblCommentNumber;

	/**
	 * The time bar for the comments.
	 */
	private TimeBar tbComment;

	/**
	 * The "waiting for questions" label.
	 */
	private final JLabel lblWaitingForQuestions;

	/**
	 * The "asking question" label.
	 */
	private final JLabel lblAskingQuestion;

	/**
	 * The question asker.
	 */
	private Delegate asker;

	/**
	 * The "responding" label.
	 */
	private final JLabel lblResponding;

	/**
	 * A blank image icon.
	 */
	private final ImageIcon iiBlank = new ImageIcon(
			new BufferedImage(IconSet.SMALL_SIZE, IconSet.SMALL_SIZE,
					BufferedImage.TYPE_INT_ARGB));

	/**
	 * The current question state.
	 */
	private QuestionActionType questionState = QuestionActionType.WAITING_FOR_QUESTIONS;

	public PublicDisplayGSLPanel(Committee committee) {
		super(new MigLayout());
		this.committee = committee;

		ripbMain = new RadialImageProgressBar();
		add(ripbMain, new CC().grow().push());

		add(pnlYield, new CC().growX().push().alignY("center").hideMode(3)); //$NON-NLS-1$
		pnlYield.setVisible(false);

		// Create each yield type's panel.
		ypnlComments = new JPanel(new BorderLayout());
		pnlYield.add(ypnlComments, YieldType.COMMENTS.name());
		lblCommentNumber = new JLabel();
		lblCommentNumber.setFont(lblCommentNumber.getFont()
				.deriveFont(Font.BOLD)
				.deriveFont(lblCommentNumber.getFont().getSize() * 1.5f));
		ypnlComments.add(lblCommentNumber, BorderLayout.CENTER);
		lblCommentNumber.setHorizontalAlignment(JLabel.CENTER);
		tbComment = new TimeBar();
		ypnlComments.add(tbComment, BorderLayout.SOUTH);
		updateCommentsPanel();

		ypnlQuestions = new JPanel();
		ypnlQuestions.setLayout(new BoxLayout(ypnlQuestions,
				BoxLayout.PAGE_AXIS));
		ypnlQuestions.add(Box.createVerticalGlue());
		lblWaitingForQuestions = new JLabel(
				Messages.getString("PublicDisplayGSLPanel.WaitingForQuestions")); //$NON-NLS-1$
		lblWaitingForQuestions.setName(QuestionActionType.WAITING_FOR_QUESTIONS
				.name());
		lblAskingQuestion = new JLabel(
				Messages.getString("PublicDisplayGSLPanel.AskingQuestion")); //$NON-NLS-1$
		lblAskingQuestion.setName(QuestionActionType.ASKING.name());
		lblResponding = new JLabel(
				Messages.getString("PublicDisplayGSLPanel.Responding")); //$NON-NLS-1$
		lblResponding.setName(QuestionActionType.ANSWERING.name());
		for (JLabel label : new JLabel[] { lblWaitingForQuestions,
				lblAskingQuestion, lblResponding }) {
			label.setIcon(iiBlank);
			label.setFont(label.getFont().deriveFont(Font.BOLD)
					.deriveFont(label.getFont().getSize() * 1.5f));
			label.setForeground(Color.GRAY);
			label.setHorizontalAlignment(JLabel.LEADING);
			ypnlQuestions.add(label);
		}
		ypnlQuestions.add(Box.createVerticalGlue());
		pnlYield.add(ypnlQuestions, YieldType.QUESTIONS.name());
		updateQuestionsPanel();

		ypnlDelegate = new JPanel(new BorderLayout());
		ripbYielded = new RadialImageProgressBar();
		ypnlDelegate.add(ripbYielded, BorderLayout.CENTER);
		pnlYield.add(ypnlDelegate, YieldType.DELEGATE.name());
		updateDelegatePanel();
	}

	/**
	 * Sets the committee.
	 * 
	 * @param committee
	 *            the new committee
	 */
	public void setCommittee(Committee committee) {
		this.committee = committee;
	}

	/**
	 * Updates the comments panel.
	 */
	private void updateCommentsPanel() {
		if (committee != null) {
			lblCommentNumber
					.setText(commentNumber == 0 ? Messages
							.getString("PublicDisplayGSLPanel.AwaitingComments") //$NON-NLS-1$
							: (Messages
									.getString("PublicDisplayGSLPanel.CommentPrefix") + commentNumber + Messages.getString("PublicDisplayGSLPanel.CommentSuffix") + committee.numComments)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Updates the delegate yield panel.
	 */
	private void updateDelegatePanel() {
		if (yieldedSpeaker != null) {
			ripbYielded.setImage(yieldedSpeaker.getImage());
			ripbYielded.setPercentage((double) (secondsElapsed)
					/ (double) (totalTime.getTotalSeconds()));
		}
	}

	/**
	 * Updates the questions yield panel.
	 */
	private void updateQuestionsPanel() {
		for (JLabel label : new JLabel[] { lblWaitingForQuestions,
				lblAskingQuestion, lblResponding }) {
			label.setForeground(label.getName().equals(questionState.name()) ? Color.BLACK
					: Color.GRAY);
		}
		lblAskingQuestion.setIcon(questionState == QuestionActionType.ASKING
				&& asker != null ? asker.getSmallIcon() : iiBlank);
		lblResponding
				.setIcon(questionState == QuestionActionType.ANSWERING ? speaker
						.getSmallIcon() : iiBlank);
	}

	@Override
	public void yieldActionPerformed(YieldActionEvent yae) {
		if (yae.actionType instanceof CommentActionType) {
			switch ((CommentActionType) yae.actionType) {
			case COMMENT_STARTED:
				commentNumber++;
				tbComment.start(committee.commentTime);
				updateCommentsPanel();
				break;
			case COMMENT_ENDED:
				tbComment.stop();
				break;
			default:
				break;
			}
		} else if (yae.actionType instanceof QuestionActionType) {
			questionState = (QuestionActionType) yae.actionType;
			asker = yae.questionAsker;
			updateQuestionsPanel();
		} else {
			System.err.println("PublicDisplayGSLPanel - unimplemented action " //$NON-NLS-1$
					+ yae.actionType);
		}
	}

	@Override
	public void yield(YieldEvent ye) {
		if (ye.yield.type == null || ye.yield.type == YieldType.CHAIR) {
			tmSpeech.stop();
			ripbMain.setImage(null);
			pnlYield.setVisible(false);
			commentNumber = -1;
			return;
		}
		switch (ye.yield.type) {
		case CHAIR:
			// This should have been covered by the if statement above.
			assert false;
			break;
		case COMMENTS:
			commentNumber = 0;
			tmSpeech.stop();
			tbComment.stop();
			updateCommentsPanel();
			break;
		case QUESTIONS:
			updateQuestionsPanel();
			break;
		case DELEGATE:
			yieldedSpeaker = ye.yield.target;
			updateDelegatePanel();
			break;
		}
		pnlYield.setVisible(true);
		clYield.show(pnlYield, ye.yield.type.name());
	}

	/**
	 * Starts a speech for the given delegate.
	 * 
	 * @param delegate
	 *            the delegate speaking
	 * @param speakingTime
	 *            the speaking time
	 */
	public void startSpeech(Delegate delegate, Time speakingTime) {
		speaker = delegate;
		secondsElapsed = 0;
		ripbMain.setPercentage(0);
		ripbMain.setImage(speaker.getImage());
		totalTime = speakingTime;
		yieldedSpeaker = null;
		tmSpeech.start();
	}

	/**
	 * Pauses or resumes the current speech.
	 */
	public void pauseSpeech() {
		if (tmSpeech.isRunning()) {
			tmSpeech.stop();
		} else {
			tmSpeech.start();
		}
	}

	@Override
	public void speechActionPerformed(SpeechEvent se) {
		switch (se.type) {
		case PAUSED: // or resumed
			if (tmSpeech.isRunning()) {
				tmSpeech.stop();
			} else {
				tmSpeech.start();
			}
			break;
		case FINISHED:
			tmSpeech.stop();
			ripbMain.setImage(null);
			pnlYield.setVisible(false);
			commentNumber = -1;
			break;
		case ALMOST_FINISHED:
		default:
			// Don't care.
			break;
		}
	}

}
