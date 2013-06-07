package org.phynx.vdownload;

/**
 * Provides listener download progress, so client can keep track of 
 * current download
 * 
 * @author pandupradhana
 *
 */
public interface DownloadListener {

	/**
	 * What to do while downloading
	 * 
	 * @param aStatus
	 */
	void onProgress(DownloadStatus aStatus);
	
}
