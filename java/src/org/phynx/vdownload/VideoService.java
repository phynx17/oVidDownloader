package org.phynx.vdownload;

import org.phynx.vdownload.streams.VideoStream;

/**
 * Provides interface for video web-based service. 
 * 
 * @author pandupradhana
 *
 */
public interface VideoService {

	/**
	 * Check whether video with given url exists.
	 * 
	 * @param aUrl a url
	 * @return true if exists
	 */
	boolean isVideoExists(String aUrl);
	
	/**
	 * Implementation should handle video download from specific 
	 * service here. Response such as no video found should
	 * be handle properly by the implementation. 
	 * 
	 * Note: after usage caller to this method MUST call {@link VideoStream#close()}
	 * 
	 * @param url video url
	 * @return stream
	 * @see VideoStream
	 */
	VideoStream grabVideo(String aUrl);
	
	/**
	 * Grab video with given file name to save. 
	 * 
	 * @param aUrl video url
	 * @param aFileName file name to save
	 * @see #grabVideo(String)
	 */
	void grabVideo(String aUrl,String aFileName);
	
	/**
	 * Provides default file extension for particular video service. 
	 * 
	 * @return file extension
	 */
	String videoFileExtenstion();
	
	/**
	 * Add a download listener. This is a one-to-one relationship
	 * 
	 * @param aListener listener
	 */
	void addDownloadListener(DownloadListener aListener);
	
	/**
	 * Remove listener. 
	 * 
	 * @param aListener listener
	 */
	void removeDownloadListener(DownloadListener aListener);
	
	/**
	 * Set if service support resumable download
	 * 
	 * @param aTrue true to support resume download
	 */
	void setResumableDownload(boolean aResume);
	
	/**
	 * Check whether service support resumable download
	 * 
	 * @return true if support resume download
	 */
	boolean isDownloadResumable();
	
}
