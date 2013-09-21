package org.lcmmun.kiosk;

import org.lcmmun.kiosk.resources.ImageType;

/**
 * The various states a committee can be in.
 * 
 * @author William Chargin
 * 
 */
/**
 * @author William
 * 
 */
public enum CommitteeState {

	/**
	 * Indicates that that committee in question is speaking from the general
	 * speakers' list.
	 */
	SPEAKERS_LIST(Messages.getString("CommitteeState.GeneralSpeakersList"), //$NON-NLS-1$
			ImageType.OS_SPEAKERS_LIST),

	/**
	 * Indicates that the committee in question is in a moderated.
	 */
	MODERATED_CAUCUS(Messages.getString("CommitteeState.ModeratedCaucus"), //$NON-NLS-1$
			ImageType.OS_MODERATED_CAUCUS),

	/**
	 * Indicates that the committee in question is in an unmoderated caucus.
	 */
	UNMODERATED_CAUCUS(Messages.getString("CommitteeState.UnmoderatedCaucus"), //$NON-NLS-1$
			ImageType.OS_UNMODERATED_CAUCUS),

	/**
	 * Indicates that the committee in question is in voting bloc.
	 */
	VOTING_BLOC(Messages.getString("CommitteeState.VotingBloc"), ImageType.OS_VOTING_BLOC), //$NON-NLS-1$

	/**
	 * Indicates that the committee in question is in formal caucus.
	 */
	FORMAL_CAUCUS(Messages.getString("CommitteeState.FormalCaucus"), //$NON-NLS-1$
			ImageType.OS_FORMAL_CAUCUS),

	/**
	 * Indicates that the committee in question is suspended or adjourned.
	 */
	ADJOURNED(Messages.getString("CommitteeState.Adjourned"), ImageType.OS_ADJOURNED); //$NON-NLS-1$

	/**
	 * The human-readable description of this committee state.
	 */
	public final String description;

	/**
	 * The image type associated with the icon for this committee state.
	 */
	public final ImageType imageType;

	/**
	 * Creates the {@code CommitteeState} with the given description and image
	 * type.
	 * 
	 * @param description
	 *            the description
	 * @param imageType
	 *            the image type
	 */
	private CommitteeState(String description, ImageType imageType) {
		this.description = description;
		this.imageType = imageType;
	}

}
