package org.lcmmun.kiosk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A working paper <em>or</em> draft resolution for a topic.
 * 
 * @author William Chargin
 * 
 */
public class WorkingPaper implements Serializable, Document {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of delegates submitting the working paper.
	 */
	public final List<Delegate> submitters = new ArrayList<Delegate>();

	/**
	 * The list of signatories. Only applicable to resolutions.
	 */
	private ArrayList<Delegate> signatories = new ArrayList<Delegate>();

	/**
	 * The topic index.
	 */
	public final int index_topic;

	/**
	 * The index of this working paper in the list of working papers.
	 */
	public final int index_workingPaper;

	/**
	 * The link to the file containing the working paper itself.
	 */
	private File file;

	/**
	 * The string for the Google Docs ID for this document.
	 * <p>
	 * An example resolution is available with ID:
	 * 
	 * <pre>
	 * <code>1GiiqHRYnWr1sXZjXsk38HGWsLB0dr1pbPRTndDqS_O0</code>
	 * </pre>
	 */
	private String googleDocsId;

	/**
	 * Whether the working paper has been introduced as a draft resolution.
	 */
	private boolean isDraftResolution = false;

	/**
	 * Creates the working paper with the given topic index and working paper
	 * index.
	 * 
	 * @param index_topic
	 *            the index of the topic in the topic list
	 * @param index_workingPaper
	 *            the index of the working paper in the topic
	 */
	public WorkingPaper(int index_topic, int index_workingPaper) {
		super();
		this.index_topic = index_topic;
		this.index_workingPaper = index_workingPaper;
	}

	@Override
	public WorkingPaper clone() {
		WorkingPaper wp = new WorkingPaper(index_topic, index_workingPaper);
		wp.submitters.addAll(submitters);
		wp.signatories = new ArrayList<Delegate>(signatories);
		wp.file = file;
		wp.isDraftResolution = isDraftResolution;
		return wp;
	}

	/**
	 * Gets the reference to the working paper file.
	 * 
	 * @return the reference to the working paper
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Gets the identifier of this working paper or draft resolution.
	 * <p>
	 * Examples include <em>Draft Resolution 2.1</em> or
	 * <em>Working Paper 1.3</em>.
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		final String shortForm = (isDraftResolution ? Messages
				.getString("WorkingPaper.DraftResolution") //$NON-NLS-1$
				: Messages.getString("WorkingPaper.WorkingPaper")) //$NON-NLS-1$
				+ " " //$NON-NLS-1$
				+ (index_topic + 1) + "." //$NON-NLS-1$
				+ (index_workingPaper + 1);
		return shortForm;
	}

	/**
	 * Gets the long form of the working paper's string representation.
	 * 
	 * @return the long name
	 */
	private String getLongName() {
		String ret = getIdentifier();

		String submitterString = getSubmitterString();
		if (!submitterString.isEmpty()) {
			ret += " (" + submitterString + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ret;
	}

	/**
	 * Gets the list of signatories, provided that this working paper has been
	 * introduced as a draft resolution.
	 * 
	 * @return the list of signatories
	 * @throws IllegalStateException
	 *             if it is not a draft resolution
	 * @see #introduceAsDraftResolution()
	 */
	public ArrayList<Delegate> getSignatories() throws IllegalStateException {
		if (!isDraftResolution) {
			throw new IllegalStateException();
		}
		return signatories;
	}

	/**
	 * Gets the submitters as a string.
	 * <p>
	 * Examples include:
	 * <ul>
	 * <li>For one submitter: <em>United Kingdom</em>
	 * <li>For multiple submitters: <em>Belgium, et. al.</em>
	 * </ul>
	 * 
	 * @return the submitters
	 */
	public String getSubmitterString() {
		return submitters.isEmpty() ? new String() : submitters.get(0)
				+ (submitters.size() > 1 ? Messages
						.getString("WorkingPaper.EtAlSuffix") : new String()); //$NON-NLS-1$
	}

	/**
	 * Sets the record such that it shall indicate that the working paper has
	 * been introduced as a draft resolution (i.e., further calls to
	 * {@link #isDraftResolution()} will return {@code true}).
	 */
	public void introduceAsDraftResolution() {
		this.isDraftResolution = true;
	}

	/**
	 * Determines whether the working paper has been introduced as a draft
	 * resolution.
	 * 
	 * @return whether the working paper has been introduced as a draft
	 *         resoution
	 */
	public boolean isDraftResolution() {
		return isDraftResolution;
	}

	/**
	 * Sets the reference to the working paper file.
	 * 
	 * @param file
	 *            the new reference to the working paper
	 */
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return getLongName();
	}

	/**
	 * Gets the Google Docs ID for this document.
	 * 
	 * @return the Google docs ID
	 */
	public String getGoogleDocsId() {
		return googleDocsId;
	}

	/**
	 * Sets the Google Docs ID for this document.
	 * 
	 * @param googleDocsId
	 *            the Google docs ID
	 */
	public void setGoogleDocsId(String googleDocsId) {
		this.googleDocsId = googleDocsId;
	}

}
