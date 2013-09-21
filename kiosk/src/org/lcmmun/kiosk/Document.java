package org.lcmmun.kiosk;

import java.io.File;

/**
 * A document of any type that can be rendered.
 * 
 * @author William Chargin
 * 
 */
public interface Document {

	/**
	 * Gets a reference to the file.
	 * 
	 * @return the file, or {@code null} if none has been set
	 */
	public File getFile();

	/**
	 * Gets the Google Docs ID for this document.
	 * 
	 * @return the Google docs ID
	 */
	public String getGoogleDocsId();
}
