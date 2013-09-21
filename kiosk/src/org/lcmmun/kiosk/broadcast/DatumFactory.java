package org.lcmmun.kiosk.broadcast;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.Speech;
import org.lcmmun.kiosk.motions.MajorityType;
import org.lcmmun.kiosk.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;

import tools.customizable.Time;

/**
 * A factory class for creating {@code Datum} objects.
 * 
 * @author William Chargin
 * 
 */
public final class DatumFactory {

	/**
	 * Creates and returns a {@code Datum} indicating that the current caucus
	 * has ended.
	 * 
	 * @return a new datum
	 */
	public static Datum createCaucusEndedDatum() {
		return new Datum(ImageFetcher.fetchImageIcon(ImageType.CAUCUS_TIME_UP),
				Messages.getString("DatumFactory.CaucusEnded")); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating that the comments for the
	 * given delegate's speech have been exhausted.
	 * 
	 * @param delegate
	 *            the delegate who made the relevant speech
	 * @return a new datum
	 */
	public static Datum createCommentsExhaustedDatum(Delegate delegate) {
		return new Datum(delegate.getSmallIcon(),
				Messages.getString("DatumFactory.CommentsExhausted")); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating that there are a certain
	 * number of comments remaining.
	 * 
	 * @param delegate
	 *            the delegate who made the relevant speech
	 * @param remaining
	 *            the number of comments remaining
	 * @return a new datum
	 */
	public static Datum createCommentsRemainingDatum(Delegate delegate,
			int remaining) {
		if (remaining == 1) {
			return new Datum(
					delegate.getSmallIcon(),
					Messages.getString("DatumFactory.CommentsRemainingPrefixSingular") + remaining //$NON-NLS-1$
							+ Messages
									.getString("DatumFactory.CommentsRemainingSuffixSingular")); //$NON-NLS-1$
		} else {
			return new Datum(
					delegate.getSmallIcon(),
					Messages.getString("DatumFactory.CommentsRemainingPrefixPlural") + remaining //$NON-NLS-1$
							+ Messages
									.getString("DatumFactory.CommentsRemainingSuffixPlural")); //$NON-NLS-1$
		}
	}

	/**
	 * Creates and returns a {@code Datum} indicating that the next speaker is
	 * the given delegate.
	 * 
	 * @param delegate
	 *            the next speaker
	 * @return a new datum
	 */
	public static Datum createNextSpeakerDatum(Delegate delegate) {
		return new Datum(delegate.getSmallIcon(),
				Messages.getString("DatumFactory.NextSpeaker"), //$NON-NLS-1$
				delegate.getName());
	}

	/**
	 * Creates and returns a {@code Datum} indicating that the chair should
	 * prompt the delegates, and ask them if any want to speak (moderated
	 * caucus).
	 * 
	 * @return a new datum
	 */
	public static Datum createPromptDelegatesDatum() {
		return new Datum(
				ImageFetcher
						.fetchImageIcon(ImageType.DELEGATES_WISHING_TO_SPEAK),
				Messages.getString("DatumFactory.PromptDelegates")); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating that the delegate has ten
	 * seconds remaining in his speech.
	 * 
	 * @return a new datum
	 */
	public static Datum createSpeechWarningDatum() {
		return new Datum(
				ImageFetcher.fetchImageIcon(ImageType.SPEECH_WARNING),
				Messages.getString("DatumFactory.WarningDatumPrefix") + Speech.THRESHOLD //$NON-NLS-1$
						+ Messages.getString("DatumFactory.WarningDatumSuffix")); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating whether a
	 * motion/resolution passed or failed.
	 * 
	 * @return a new datum
	 */
	public static Datum createVoteResultsDatum(int yes, int no, int abstain,
			MajorityType type, boolean vetoed) {
		final boolean passes = type.passes(yes, no);
		return new Datum(
				ImageFetcher.fetchImageIcon(passes ? ImageType.MOTION_PASSES
						: ImageType.MOTION_FAILS),
				Messages.getString("DatumFactory.VoteResultPrefix") //$NON-NLS-1$
						+ yes
						+ Messages
								.getString("DatumFactory.VoteResultYesCountSuffix") //$NON-NLS-1$
						+ no
						+ Messages
								.getString("DatumFactory.VoteResultNoCountSuffix") //$NON-NLS-1$
						+ abstain
						+ Messages
								.getString("DatumFactory.VoteResultAbstainCountSuffix") //$NON-NLS-1$
						+ (abstain == 1 ? Messages
								.getString("DatumFactory.AbstentionSingular") : Messages.getString("DatumFactory.AbstentionPlural")) //$NON-NLS-1$ //$NON-NLS-2$
						+ Messages.getString("DatumFactory.ThisItem") //$NON-NLS-1$
						+ (passes ? Messages.getString("DatumFactory.Passes") : Messages.getString("DatumFactory.Fails")) //$NON-NLS-1$ //$NON-NLS-2$
						+ "." //$NON-NLS-1$
						+ (vetoed ? (' ' + ('\n' //$NON-NLS-1$ //$NON-NLS-2$
						+ (passes ? Messages
								.getString("DatumFactory.VetoedHowever") //$NON-NLS-1$
								: Messages
										.getString("DatumFactory.VetoedInAddition")) + Messages.getString("DatumFactory.ItemWasVetoed"))) //$NON-NLS-1$ //$NON-NLS-2$
								: new String()) //$NON-NLS-1$
						+ ((vetoed || !passes) ? new String() //$NON-NLS-1$
								: ('\n' + Messages
										.getString("DatumFactory.ClappingIsInOrder")))); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating that an unmoderated caucus
	 * for the given amount of time proposed by the given delegate has passed.
	 * 
	 * @param delegate
	 *            the delegate who proposed the caucus
	 * @param time
	 *            the time for the caucus
	 * @return a new datum
	 */
	public static Datum createUnmoderatedCaucusDatum(Delegate delegate,
			Time time) {
		return new Datum(
				delegate.getSmallIcon(),
				Messages.getString("DatumFactory.UnmoderatedCaucusPrefix") //$NON-NLS-1$
						+ time.toString()
						+ Messages
								.getString("DatumFactory.UnmoderatedCaucusSuffix")); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating that an unmoderated caucus
	 * for the given amount of time proposed by the given delegate has passed.
	 * 
	 * @param delegate
	 *            the delegate who proposed the caucus
	 * @param time
	 *            the time for the caucus
	 * @return a new datum
	 */
	public static Datum createModeratedCaucusDatum(Delegate delegate,
			Time totalTime, Time speakingTime, String topic) {
		return new Datum(
				delegate.getSmallIcon(),
				Messages.getString("DatumFactory.ModeratedCaucus1") //$NON-NLS-1$
						+ totalTime.toString()
						+ Messages.getString("DatumFactory.ModeratedCaucus2") //$NON-NLS-1$
						+ speakingTime.toString()
						+ (topic.trim().isEmpty() ? Messages
								.getString("DatumFactory.ModeratedCaucus3NoTopic") : (Messages.getString("DatumFactory.ModeratedCaucus3Topic") //$NON-NLS-1$ //$NON-NLS-2$
										+ topic.trim() + Messages
										.getString("DatumFactory.ModeratedCaucus4")))); //$NON-NLS-1$
	}

	/**
	 * Creates and returns a {@code Datum} indicating that the given delegate is
	 * now open to a certain number of comments.
	 * 
	 * @param delegate
	 *            the delegate who made the relevant speech
	 * @param count
	 *            the number of comments
	 * @return a new datum
	 */
	public static Datum createYieldToCommentsDatum(Delegate delegate, int count) {
		if (count == 1) {
			return new Datum(
					delegate.getSmallIcon(),
					Messages.getString("DatumFactory.OpenToCommentsPrefixSingular") + count + Messages.getString("DatumFactory.OpenToCommentsSuffixSingular")); //$NON-NLS-1$ //$NON-NLS-2$

		} else {
			return new Datum(
					delegate.getSmallIcon(),
					Messages.getString("DatumFactory.OpenToCommentsPrefixPlural") + count + Messages.getString("DatumFactory.OpenToCommentsSuffixPlural")); //$NON-NLS-1$ //$NON-NLS-2$

		}
	}

	/**
	 * Instatiation not allowed.
	 * 
	 * @throws IllegalStateException
	 *             always
	 */
	private DatumFactory() throws IllegalStateException {
		throw new IllegalStateException();
	}
}
