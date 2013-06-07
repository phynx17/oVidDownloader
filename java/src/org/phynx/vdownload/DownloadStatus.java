package org.phynx.vdownload;

import java.io.Serializable;

/**
 * Provides download status information. This useful if an application wants 
 * to make a 'progress-bar' like mechanism while downloading 
 * 
 * @author Pandu
 *
 */
public interface DownloadStatus extends Serializable {

	/**
	 * A rounded percentage of current download
	 * @return percentage
	 */
	public int getPercentage();
	
	/**
	 * Get current total byte read
	 * @return
	 */
	public long getCurrentTotalRead() ;

}
