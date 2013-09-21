package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Yield;

/**
 * An event fired when an action pertaining to a yield occurs.
 * 
 * @author William Chargin
 * 
 */
public class YieldActionEvent extends EventObject {

	public enum CommentActionType implements ActionType {
		COMMENT_STARTED, COMMENT_ENDED;
	}

	public enum QuestionActionType implements ActionType {
		ASKING, ANSWERING, WAITING_FOR_QUESTIONS;
	}

	private interface ActionType {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate who yielded.
	 */
	public final Delegate delegate;

	/**
	 * How the delegate yielded.
	 */
	public final Yield yield;

	/**
	 * The type of action that occurred.
	 */
	public final ActionType actionType;

	/**
	 * The question asker. Only applicable to action type
	 * {@link QuestionActionType#ASKING}.
	 */
	public final Delegate questionAsker;

	/**
	 * The number of questions remaining. Only available to action type
	 * {@link CommentActionType#COMMENT_ENDED}.
	 */
	public final int commentsRemaining;

	/**
	 * Creates the event with the given delegate, yield, and questioner.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param yield
	 *            the yield
	 * @param questionAsker
	 *            the questioner
	 */
	public YieldActionEvent(Delegate delegate, Yield yield,
			ActionType actionType, Delegate questionAsker) {
		this(delegate, yield, actionType, questionAsker, 0);
	}

	/**
	 * Creates the event with the given delegate, yield, question asker, and
	 * comments remaining.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param yield
	 *            the yield
	 * @param questionAsker
	 *            the question asker
	 * @param questionsRemaining
	 *            the number of questions remaining
	 */
	public YieldActionEvent(Delegate delegate, Yield yield,
			ActionType actionType, Delegate questionAsker, int commentsRemaining) {
		super(delegate);
		this.delegate = delegate;
		this.yield = yield;
		this.actionType = actionType;
		this.questionAsker = questionAsker;
		this.commentsRemaining = commentsRemaining;
	}

	/**
	 * Creates the event with the given delegate, yield, and number of comments
	 * remaining.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param yield
	 *            the yield
	 * @param commentsReamining
	 *            the number of comments remaining
	 */
	public YieldActionEvent(Delegate delegate, Yield yield,
			ActionType actionType, int commentsRemaining) {
		this(delegate, yield, actionType, null, commentsRemaining);
	}

	/**
	 * Creates the event with the given delegate and yield.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param yield
	 *            the yield
	 */
	public YieldActionEvent(Delegate delegate, Yield yield,
			ActionType actionType) {
		this(delegate, yield, actionType, null);
	}

}
