package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.Speech.YieldType;
import org.lcmmun.kiosk.Yield;
import org.lcmmun.kiosk.gui.events.YieldActionEvent;
import org.lcmmun.kiosk.gui.events.YieldActionEvent.CommentActionType;
import org.lcmmun.kiosk.gui.events.YieldActionEvent.QuestionActionType;
import org.lcmmun.kiosk.gui.events.YieldActionListener;
import org.lcmmun.kiosk.gui.events.YieldEvent;
import org.lcmmun.kiosk.gui.events.YieldListener;

import tools.customizable.CounterProperty;
import tools.customizable.MultipleChoiceProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;

/**
 * A panel for managing yields.
 * 
 * @author William Chargin
 * 
 */
public class YieldsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate currently speaking.
	 */
	private Delegate speaking;

	/**
	 * The card layout of this component.
	 */
	private final CardLayout layout = new CardLayout();

	/**
	 * The yield type property.
	 */
	private final MultipleChoiceProperty<YieldType> mcpYieldType;

	/**
	 * The yield button.
	 */
	private final JButton btnYield;

	/**
	 * The committee.
	 */
	public Committee committee;

	/**
	 * Creates the panel.
	 */
	public YieldsPanel(final Committee committee, final SpeechPanel parent) {
		super();
		setLayout(layout);
		this.committee = committee;

		mcpYieldType = MultipleChoiceProperty.createFromEnum(
				Messages.getString("YieldsPanel.YieldType"), //$NON-NLS-1$
				YieldType.class);
		mcpYieldType.setRenderer(new YieldRenderer());

		JPanel pnlYield = new JPanel(new BorderLayout());
		add(pnlYield, new String());

		pnlYield.add(new PropertyPanel(new PropertySet(mcpYieldType), true,
				false), BorderLayout.CENTER);

		btnYield = new JButton(Messages.getString("YieldsPanel.YieldButton")); //$NON-NLS-1$
		pnlYield.add(btnYield, BorderLayout.SOUTH);

		final JPanel ypnlComments = new JPanel(new MigLayout());
		final JPanel ypnlQuestions = new JPanel(new MigLayout());
		final JPanel ypnlDelegate = new JPanel(new BorderLayout());
		btnYield.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				final YieldType value = mcpYieldType.getValue();
				if (value != YieldType.DELEGATE) {
					fireYieldEvent(new YieldEvent(YieldsPanel.this, speaking,
							value == YieldType.DELEGATE ? new Yield(speaking)
									: new Yield(value)));
				}
				switch (value) {
				case DELEGATE:
					setupDelegatePanel(ypnlDelegate,
							YieldsPanel.this.committee, parent);
					break;
				case QUESTIONS:
					setupQuestionsPanel(ypnlQuestions,
							YieldsPanel.this.committee, parent);
					break;
				case COMMENTS:
					setupCommentsPanel(ypnlComments, YieldsPanel.this.committee);
					parent.yield(new Yield(YieldType.COMMENTS));
					break;
				default:
					parent.yield(new Yield(value));
					break;
				}
				layout.show(YieldsPanel.this, value.name());
			}
		});

		setupCommentsPanel(ypnlComments, committee);
		setupQuestionsPanel(ypnlQuestions, committee, parent);
		setupDelegatePanel(ypnlDelegate, committee, parent);

		add(ypnlComments, YieldType.COMMENTS.name());
		add(ypnlQuestions, YieldType.QUESTIONS.name());
		add(ypnlDelegate, YieldType.DELEGATE.name());

		setSpeakingDelegate(null);

	}

	/**
	 * Sets up the question panel.
	 * 
	 * @param ypnlQuestions
	 *            the panel to set up
	 * @param committee
	 *            the relevant committee
	 */
	private void setupQuestionsPanel(JPanel ypnlQuestions, Committee committee,
			final SpeechPanel parent) {
		ypnlQuestions.removeAll();

		ArrayList<Delegate> candidates = new ArrayList<Delegate>(
				committee.getPresentDelegates());
		// You can't ask yourself a question.
		// ... although that would be interesting.
		candidates.remove(speaking);

		if (candidates.isEmpty()) {
			final JLabel label = new JLabel(
					Messages.getString("YieldsPanel.NoDelegatesToAsk")); //$NON-NLS-1$
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			ypnlQuestions.add(label, new CC().grow().push());
		} else {
			parent.pauseSpeech();
			final MultipleChoiceProperty<Delegate> mcpRecognize = new MultipleChoiceProperty<Delegate>(
					Messages.getString("YieldsPanel.Recognize"), candidates, null); //$NON-NLS-1$
			ypnlQuestions.add(new PropertyPanel(new PropertySet(mcpRecognize),
					true, false), new CC().grow().push().spanX().wrap());

			final Map<String, ActionListener> buttonValues = new LinkedHashMap<String, ActionListener>();

			buttonValues
					.put(Messages.getString("YieldsPanel.StartQuestion"), new ActionListener() { //$NON-NLS-1$
								@Override
								public void actionPerformed(ActionEvent ae) {
									fireYieldActionEvent(new YieldActionEvent(
											speaking, new Yield(
													YieldType.QUESTIONS),
											QuestionActionType.ASKING,
											mcpRecognize.getValue()));

								}
							});
			final JButton btn = new JButton(new ArrayList<String>(
					buttonValues.keySet()).get(0));

			buttonValues
					.put(Messages.getString("YieldsPanel.StartAnswer"), new ActionListener() { //$NON-NLS-1$
								@Override
								public void actionPerformed(ActionEvent ae) {
									parent.pauseSpeech();
									fireYieldActionEvent(new YieldActionEvent(
											speaking, new Yield(
													YieldType.QUESTIONS),
											QuestionActionType.ANSWERING));
								}
							});

			buttonValues
					.put(Messages.getString("YieldsPanel.StopAnswer"), new ActionListener() { //$NON-NLS-1$
								@Override
								public void actionPerformed(ActionEvent ae) {
									parent.pauseSpeech();
									fireYieldActionEvent(new YieldActionEvent(
											speaking,
											new Yield(YieldType.QUESTIONS),
											QuestionActionType.WAITING_FOR_QUESTIONS));
								}
							});

			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					ArrayList<String> list = new ArrayList<String>(buttonValues
							.keySet());
					int index = list.indexOf(btn.getText());
					int next = (index + 1) % list.size();
					buttonValues.get(list.get(index)).actionPerformed(ae);
					btn.setText(list.get(next));
				}
			});

			ypnlQuestions.add(btn, new CC().growX().pushX());
		}

		ypnlQuestions.revalidate();
		ypnlQuestions.repaint();
	}

	/**
	 * Adds the given listener to the list of listeners.
	 * 
	 * @param yl
	 *            the listener to add
	 */
	public void addYieldListener(YieldListener yl) {
		listenerList.add(YieldListener.class, yl);
	}

	/**
	 * Removes the given listener from the list of listeners.
	 * 
	 * @param yl
	 *            the listener to remove
	 */
	public void removeYieldListener(YieldListener yl) {
		listenerList.remove(YieldListener.class, yl);
	}

	/**
	 * Adds the given listener to the list of listeners.
	 * 
	 * @param yl
	 *            the listener to add
	 */
	public void addYieldActionListener(YieldActionListener yl) {
		listenerList.add(YieldActionListener.class, yl);
	}

	/**
	 * Removes the given listener from the list of listeners.
	 * 
	 * @param yl
	 *            the listener to remove
	 */
	public void removeYieldActionListener(YieldActionListener yl) {
		listenerList.remove(YieldActionListener.class, yl);
	}

	/**
	 * Fires a {@link YieldActionEvent} to all registered listeners.
	 * 
	 * @param yieldActionEvent
	 *            the event to fire
	 */
	protected void fireYieldActionEvent(YieldActionEvent yieldActionEvent) {
		for (YieldActionListener yl : listenerList
				.getListeners(YieldActionListener.class)) {
			yl.yieldActionPerformed(yieldActionEvent);
		}
	}

	public void setupDelegatePanel(final JPanel ypnlDelegate,
			final Committee committee, final SpeechPanel parent) {
		ypnlDelegate.removeAll();

		final ArrayList<Delegate> validYields = new ArrayList<Delegate>(
				committee.getPresentDelegates());
		if (validYields.isEmpty()) {
			final JLabel label = new JLabel(
					Messages.getString("YieldsPanel.NoDelegateToYieldTo")); //$NON-NLS-1$
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			ypnlDelegate.add(label, BorderLayout.CENTER);
		} else {
			validYields.remove(speaking);
			final MultipleChoiceProperty<Delegate> mcpTarget = new MultipleChoiceProperty<Delegate>(
					Messages.getString("YieldsPanel.Target"), validYields, validYields.get(0)); //$NON-NLS-1$
			ypnlDelegate.add(new PropertyPanel(new PropertySet(mcpTarget),
					true, false), BorderLayout.CENTER);

			final JButton btnYield = new JButton(
					Messages.getString("YieldsPanel.YieldToDelegate")); //$NON-NLS-1$
			ypnlDelegate.add(btnYield, BorderLayout.SOUTH);
			btnYield.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					mcpTarget.setEnabled(false);
					btnYield.setEnabled(false);
					parent.yield(new Yield(mcpTarget.getValue()));
				}
			});
		}
		ypnlDelegate.revalidate();
		ypnlDelegate.repaint();
	}

	/**
	 * Sets up the comments panel.
	 * 
	 * @param ypnlComments
	 *            the panel to set up
	 * @param committee
	 *            the committee
	 */
	public void setupCommentsPanel(JPanel ypnlComments,
			final Committee committee) {
		ypnlComments.removeAll();
		if (committee.numComments > 0) {
			final TimeBar tbComment = new TimeBar();
			final CounterProperty cpCommentNumber = new CounterProperty(
					new String(), 1, 1, committee.numComments);
			tbComment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					if (me.getClickCount() >= 2
							&& (tbComment.isRunning() || tbComment.isFinished())) {
						tbComment.stop();
						fireYieldActionEvent(new YieldActionEvent(speaking,
								new Yield(YieldType.COMMENTS),
								CommentActionType.COMMENT_ENDED,
								committee.numComments
										- cpCommentNumber.getValue() + 1));
					}
				}

			});
			tbComment.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					if (tbComment.isFinished()) {
						tbComment.stop();
						fireYieldActionEvent(new YieldActionEvent(speaking,
								new Yield(YieldType.COMMENTS),
								CommentActionType.COMMENT_ENDED,
								committee.numComments
										- cpCommentNumber.getValue() + 1));
					}
				}
			});

			final JButton btnStartComment = new JButton(
					Messages.getString("YieldsPanel.DefaultCommentButtonText")); //$NON-NLS-1$
			btnStartComment.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					cpCommentNumber.setValue(cpCommentNumber.getValue() + 1);
					if (cpCommentNumber.getValue() > committee.numComments) {
						btnStartComment.setEnabled(false);
						btnStartComment.setText(Messages
								.getString("YieldsPanel.NoMoreComments")); //$NON-NLS-1$
					} else {
						btnStartComment.setText(Messages
								.getString("YieldsPanel.StartCommentButtonPrefix") //$NON-NLS-1$
								+ (cpCommentNumber.getValue()));
					}
					fireYieldActionEvent(new YieldActionEvent(speaking,
							new Yield(YieldType.COMMENTS),
							CommentActionType.COMMENT_STARTED));
					tbComment.start(committee.commentTime);
				}
			});
			ypnlComments.add(btnStartComment, new CC().grow().push());
			ypnlComments.add(tbComment, new CC().growX().pushX().spanX()
					.newline());
		} else {
			final JLabel label = new JLabel(
					Messages.getString("YieldsPanel.NoCommentsAllowedText")); //$NON-NLS-1$
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			ypnlComments.add(label, new CC().grow().push());
		}
		ypnlComments.revalidate();
		ypnlComments.repaint();
	}

	/**
	 * Gets the delegate currently speaking.
	 * 
	 * @return the currently speaking delegate, or {@code null} if no one is
	 *         speaking
	 */
	public Delegate getSpeakingDelegate() {
		return speaking;
	}

	/**
	 * Sets the delegate speaking.
	 * 
	 * @param speakingDelegate
	 *            the currently speaking delegate, or {@code null} if no one is
	 *            speaking
	 */
	public void setSpeakingDelegate(Delegate speakingDelegate) {
		this.speaking = speakingDelegate;
		mcpYieldType.setEnabled(speakingDelegate != null);
		mcpYieldType.setValue(YieldType.CHAIR);
		btnYield.setEnabled(speakingDelegate != null);
		layout.show(this, new String());
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
	 * Fires a {@link YieldEvent} to all registered listeners.
	 * 
	 * @param yieldActionEvent
	 *            the event to fire
	 */
	protected void fireYieldEvent(YieldEvent yieldEvent) {
		for (YieldListener yl : listenerList.getListeners(YieldListener.class)) {
			yl.yield(yieldEvent);
		}
	}

}
