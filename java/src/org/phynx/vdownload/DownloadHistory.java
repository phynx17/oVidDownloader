package org.phynx.vdownload;

/**
 * Uttility class to search current download history
 * 
 * @author Pandu
 *
 */
public final class DownloadHistory {

	/**
	 * Constructor
	 *
	 */
	private DownloadHistory() {
		//nothing
	}
	
	/**
	 * Singleton instance
	 */
	private static DownloadHistory mInstance = new DownloadHistory();
	
	/**
	 * Get this class singleton instance.
	 * @return DownloadHistory
	 */
	public static DownloadHistory getInstance() {
		return mInstance;
	}
	
}
