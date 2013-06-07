package org.phynx.vdownload;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Video download client. 
 * 
 * @author pandupradhana
 *
 */
public class DownloadClient {
	
	/**
	 * Default file name extension. 
	 */
	private static final String DEFAULT_FILE_EXT = ".video";
		
	/**
	 * If download in progress?
	 */
	private boolean mDownloadInProgress = false;
	

	/**
	 * Videoservice that should be injected to.
	 */
	private VideoService mService;
	
	/**
	 * Constructor. This shoud be injected by Guice
	 * 
	 * @param aService a video service
	 */
	@Inject 
	private DownloadClient(VideoService aService){
		mService = aService;
	}

	
	/**
	 * Grab the video from given url. File name is generated
	 * by the the application using <code>System.currentTimeMillis()</code>.
	 * If <tt>videoService</tt> specifies default extenstion then it will 
	 * use it as file extension, else it will give ".video".
	 * 
	 * @param aUrl a url
	 * @return file to save
	 */
	public void grabVideo(String aUrl) {
		String generatedFileName = String.valueOf(System.currentTimeMillis());
		String ext = mService.videoFileExtenstion();
		if (ext == null) {
			generatedFileName += DEFAULT_FILE_EXT;
		} else {
			generatedFileName += 
				(ext.trim().length() < 1 ? DEFAULT_FILE_EXT :
					(ext.startsWith(".") ? ext : ("." + ext)));
		}
		grabVideo(aUrl,generatedFileName);

	}
	
	/**
	 * Grab the video. 
	 * 
	 * @param aUrl url 
	 * @param aFileName file to save the video
	 * @return file
	 */
	public void grabVideo(String aUrl, String aFileName) {
		if (mDownloadInProgress) {
			throw new DownloadInterruptedException("Current download in progress. Wait for its complete");
		}
		mDownloadInProgress = true;
		mService.grabVideo(aUrl, aFileName);
		mDownloadInProgress = false;
	}
	
	/**
	 * Reset current download status. 
	 *
	 */
	public void reset() {
		mDownloadInProgress = false;
	}
	
	
	/**
	 * Add download listener
	 * 
	 * @param aListener listener
	 *
	public void addDownloadListener(DownloadListener aListener) {
		mService.addDownloadListener(aListener);
	}
	*/
	
	public VideoService getVideoService() {
		return mService;
	}
	
	
	/**
	 * Provides factory for DownloadClient
	 * 
	 * @author pandupradhana
	 *
	 */
	static class Factory {
		
		/**
		 * Instantiate DownloadClient
		 * @param modules module
		 * @return DownloadClient
		 */
		public static DownloadClient newClient(Module modules[]) {
			Injector injector = Guice.createInjector(modules[0]);
			return injector.getInstance(DownloadClient.class);
		}
	}
	
	
}
