package org.lcmmun.kiosk.resources;

/**
 * The various types of images available for fetching by {@link ImageFetcher}.
 * 
 * @author William Chargin
 * 
 */
@SuppressWarnings("nls")
public enum ImageType {

	AUTOSAVE("autosave.png"), BROADCAST_INFO("broadcast_info.png"), BROADCAST( //$NON-NLS-1$ //$NON-NLS-2$
			"broadcast.png"), CLEAR_FLOOR("clearfloor.png"), COMMENTS( //$NON-NLS-1$ //$NON-NLS-2$
			"comment.png"), CONFIGURE("configure.png"), COPY("page_copy.png"), DELETE("delete.png"), DOWN( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"down.png"), EDIT_WORKING_PAPERS("editwpdr.png"), EXIT("exit.png"), INTRODUCE_DRAFT_RESOLUTION( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"wptodr.png"), INTRODUCE_WORKING_PAPER("introducewp.png"), LOAD( //$NON-NLS-1$ //$NON-NLS-2$
			"load.png"), MANAGE_DELEGATES("managedelegates.png"), NEW("new.png"), OK( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"ok.png"), MANUAL_MESSAGE("pushmessage.png"), OMNI_ICON( //$NON-NLS-1$ //$NON-NLS-2$
			"omni_icon.png"), PASTE("page_paste.png"), QUORUM("quorum.png"), SAVE("save.png"), SAVE_AS( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"saveas.png"), STEP_COMPLETED("bullet_green.png"), STEP_PENDING( //$NON-NLS-1$ //$NON-NLS-2$
			"bullet_blue.png"), UP("up.png"), VOTE("vote.png"), WARNING( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"warning.png"), WIZARD("wand.png"), WORKING_PAPERS("wpdr.png"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	CAUCUS_TIME_UP("alarm_clock.png"), DELEGATES_WISHING_TO_SPEAK( //$NON-NLS-1$
			"question_mark.png"), SPEECH_WARNING("warning24.png"), MOTION_PASSES( //$NON-NLS-1$ //$NON-NLS-2$
			"bigyes.png"), MOTION_FAILS("bigno.png"), //$NON-NLS-1$ //$NON-NLS-2$

	OS_ADJOURNED("omnistatus_adjourned.png"), OS_SPEAKERS_LIST( //$NON-NLS-1$
			"omnistatus_speakers.png"), OS_MODERATED_CAUCUS( //$NON-NLS-1$
			"omnistatus_moderated.png"), OS_UNMODERATED_CAUCUS( //$NON-NLS-1$
			"omnistatus_unmod.png"), OS_FORMAL_CAUCUS("omnistatus_formal.png"), OS_VOTING_BLOC( //$NON-NLS-1$ //$NON-NLS-2$
			"omnistatus_voting.png"), //$NON-NLS-1$

	CREDITS_BG("lcm.png"), ICON("icon.png"), SPLASH("splash.png"), SPLASH_NO_BOX( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"splash_nobox.png"); //$NON-NLS-1$

	/**
	 * The file name of the related image.
	 */
	public final String filename;

	/**
	 * Creates the {@code ImageType} with the specified file name.
	 * 
	 * @param filename
	 *            the file name
	 */
	private ImageType(String filename) {
		this.filename = filename;
	}
}
