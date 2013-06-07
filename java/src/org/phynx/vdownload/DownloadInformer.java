package org.phynx.vdownload;

import java.util.Iterator;
import java.util.List;


/**
 * Responsible for giving information of current download status. Service implementers
 * that wants to keep its download status should use this.
 * 
 * @author pandupradhana
 *
 */
public class DownloadInformer {

	/**
	 * List of listener
	 */
	private List<DownloadListener> mListeners;
	
	/**
	 * Download status to monitor
	 */
	private DownloadStatus mStatus;
	
	/**
	 * Constructor
	 * @param aStatus
	 */
	private DownloadInformer(DownloadStatus aStatus){
		mStatus = aStatus;
	}
	
	
	/**
	 * Create new informer 
	 * @param aStatus download status
	 * @return instance of DownloadInformer
	 */
	public static DownloadInformer createInformer(DownloadStatus aStatus) {
		return new DownloadInformer(aStatus);
	}

	/**
	 * 
	 * @param aListeners
	 */
	public void addListeners(List<DownloadListener> aListeners) {
		mListeners = aListeners;
	}
	
	/**
	 * Update listners for current byte reads
	 * @param aReadSoFar byte read so far
	 */
	public void updateProgress() {
		notifyAllListener();
	}
	
	/**
	 * Notify all listener
	 *
	 */
	protected void notifyAllListener() {
		if (mListeners != null) {
			for (Iterator<DownloadListener> it = mListeners.iterator(); it.hasNext();) {
				it.next().onProgress(mStatus);
			}	
		}
	}
}
