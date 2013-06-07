package org.phynx.vdownload;

/**
 * Exception while downloading video.
 * 
 * @author pandupradhana
 *
 */
public class DownloadInterruptedException extends RuntimeException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4418411421716739239L;

	/**
	 * Constructor with given message
	 * @param aMessage a message
	 */
	public DownloadInterruptedException(String aMessage) {
		super(aMessage);
	}
	
	/**
	 * Constructor with given message and its cause
	 * @param aMessage message
	 * @param aCause cause.
	 */
	public DownloadInterruptedException(String aMessage, Throwable aCause) {
		super(aMessage, aCause);
	}
	
}
