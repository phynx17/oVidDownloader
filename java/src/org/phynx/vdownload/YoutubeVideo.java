package org.phynx.vdownload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.phynx.vdownload.annotations.MaximumDLLimit;
import org.phynx.vdownload.annotations.YoutubeDownloadURLPrefix;
import org.phynx.vdownload.streams.BandwidthManager;
import org.phynx.vdownload.streams.ControllableBWStream;
import org.phynx.vdownload.streams.HttpStream;
import org.phynx.vdownload.streams.VideoStream;

import com.google.inject.Inject;

/**
 * Handle youtube video download. It supports bandwith limiter
 * 
 * <pre>
 * Note:
 * 	Methods done here inspired from this URL :
 *  	http://www.epastebin.com/?id=4b07c5f5bf301b9532f39b3d76b81792
 *  Courtesy of Christopher Robinson.
 *  
 * </pre>
 * 
 * <pre>
 * Note: Current implementation design is for per-url download process.
 * 		 Multiple thread access for this object will resulting a miss behavior in 
 * 		 download listener. Should not use this class directly instead using
 * 		 {@link DownloadClient}
 * 			  
 * </pre>
 * 
 * @author pandupradhana
 *
 */
class YoutubeVideo implements VideoService, BandwidthManager {

	/**
	 * You tube video file extension
	 */
	private static final String YOU_TUBE_VIDEO_EXT = ".flv";
	
	/**
	 * Set buffer size for 2k
	 */
	private static final int BUFFER_SIZE = 2048;
	
	/**
	 * Http Client. 
	 */
	private HttpClient mHttpClient = prepareHttpClient();
	
	/**
	 * Logger
	 */
	private static final Log LOG = LogFactory.getLog(YoutubeVideo.class);	
	
	/**
	 * Set default to -1. It means no bandwidth limit.
	 */
	private int mBandwithLimit = -1;
		
	/**
	 * Is resumable download
	 */
	private boolean mResumable = false;
	
	/**
	 * List of listeners
	 */
	private List<DownloadListener> mListeners = new ArrayList<DownloadListener>();

	/**
	 * The get video url. Let Guice inject this
	 */
	@Inject @YoutubeDownloadURLPrefix String DOWNLOAD_URL_PREFIX;
	
	/**
	 * Maximum tolarable bandwitdh limit (bytes measure). Should big enough :)
	 */
	@Inject @MaximumDLLimit int MAXIMUM_DL_LIMIT;
	
	/**
	 * FIll THIS docs!
	 * 
	 */
	public VideoStream grabVideo(String aUrl) {
		return grabResumableVideo(aUrl,false);
	}

	/**
	 * Grab video and save it to a file. 
	 * 
	 */
	public void grabVideo(String aUrl, String aFileName) {
		VideoStream vs = null;
		try {
			File f = new File(aFileName);
			FileOutputStream fos = 
				new FileOutputStream(f,(f.exists() && isDownloadResumable()));
			vs = grabResumableVideo(aUrl,true);
			if (vs == null) {
				throw new DownloadInterruptedException("Problem while getting the video due to VideoStream is null"); 
			}
			//More than 4Kbps make buffer to 4k
			int sz = getCurrentLimit() > 4 ? (2*BUFFER_SIZE) : BUFFER_SIZE;
			byte buff[] = new byte[sz];
			int read;
			while((read = vs.read(buff)) >= 0){
				fos.write(buff, 0, read);
			}
		} catch (FileNotFoundException e) {
			// File not found? :p
			e.printStackTrace();
		} catch (IOException ioe) {
			throw new DownloadInterruptedException("Problem while getting the video due to IOException. Message: " +
					ioe.getMessage(),ioe);
		} finally {
			if (vs != null) {
				vs.close();
			}
		}
	}

	/**
	 * Check whether the video exists. It is done by reading the HTTP response code, 
	 * so we don't have to get all the resource (through stream) or continue next process
	 * if the resource itself not found.
	 * 
	 */
	public boolean isVideoExists(String aUrl) {
		GetMethod get = new GetMethod(aUrl);
		try {
			return isVideoExists(get);
		} finally {
			get.releaseConnection();
		}	
	}

	/**
	 * Return ".flv"
	 */
	public String videoFileExtenstion() {
		return YOU_TUBE_VIDEO_EXT;
	}
	
	/**
	 * Add download listener
	 * 
	 * @param aListener listener
	 */
	public void addDownloadListener(DownloadListener aListener) {
		mListeners.add(aListener);
	}	
	
	/**
	 * Remove listener. This should be called to prevent memory leaks
	 * 
	 * @param aListener listener
	 */
	public void removeDownloadListener(DownloadListener aListener) {
		mListeners.remove(aListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.VideoService#isDownloadResumable()
	 */
	public boolean isDownloadResumable() {
		return mResumable;
	}

	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.VideoService#setResumableDownload(boolean)
	 */
	public void setResumableDownload(boolean aResume) {
		//mResumable = aResume;
	}	
	
	
	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.streams.BandwidthManger#setBWLimit(int)
	 */
	public void setBWLimit(int aKBytesPerSec) {
		if ((1024*aKBytesPerSec) >= MAXIMUM_DL_LIMIT ) {
			mBandwithLimit = MAXIMUM_DL_LIMIT;
			LOG.warn("New bandwidth value is larger than defined : " + MAXIMUM_DL_LIMIT 
					+ ". Using the maximum defined instead.");
		} else {
			mBandwithLimit = 1024*aKBytesPerSec;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.streams.BandwidthManager#getCurrentLimit()
	 */
	public int getCurrentLimit() {
		return mBandwithLimit;
	}
	
	
	/*-------------------------------------------------------------
	 * 						PRIVATES
	 *------------------------------------------------------------*/

	/**
	 * Grab video with resumable functionality. Application will search in current
	 * download history
	 * 
	 * Nope currently not support for resumable :D It's because Youtube (now Google)
	 * uses a lot of server in different domain(cross domain), so we can't get exactly 'final'
	 * URL for the video. 
	 * 
	 * @param aUrl URL
	 * @param aSupport true if current grab supporting resumable downlaod
	 * @return VideoStream
	 */
	protected VideoStream grabResumableVideo(String aUrl, boolean aSupport) {
		if (aUrl == null) {
			throw new IllegalArgumentException("URL cannot be null");
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("Going to download: " + aUrl);
		}
		//
        //GetMethod get = new GetMethod(aUrl);
		GetMethod get = new GetMethod(
		"http://www.youtube.com/get_video?t=vjVQa1PpcFOpSbdtYDqOPeZjv_20w-9jvfd8ZV3PEME=&asv=3&noflv=1&fmt=34&video_id=UY1qYI3oY-0&el=detailpage"
				);
		
        try {
    		if (!isVideoExists(get)) {
    			throw new DownloadInterruptedException("Prolem while getting the video: You entered the wrong URL. ");
    		}
    		InputStream is = get.getResponseBodyAsStream();
    		if (LOG.isDebugEnabled()) {
    			LOG.debug("URL exists, continue parse response to get the real URL");
    			LOG.debug("Start to parse response body");
    		}
			long st = System.currentTimeMillis();
			String reqParam[] = parseResponseBody(is);
			//System.out.println("takes time : " + (System.currentTimeMillis() - st) + "ms.");
			if (LOG.isDebugEnabled()) {
				LOG.debug("End parse reposnse body. Takes :" + (System.currentTimeMillis() - st) + "ms.");
			}
			if (reqParam == null) {
				throw new DownloadInterruptedException("Prolem while getting the video: " +
						"Cannot find required field in afrer reponse. It's not a valid Youtube video URL");
			}
			
			get.releaseConnection();
			
			//Process the real url here
			//Mmm.. do we need l parameter?
			String newURL = DOWNLOAD_URL_PREFIX + reqParam[0] + "&t=" + reqParam[2];
			get = new GetMethod(newURL);
			
			//Example: (rfc2616. Section 14.16)
			//get.addRequestHeader("Content-Range", "bytes 47104-102400");
			get.setFollowRedirects(true);
			st = System.currentTimeMillis();
			int responseCode = mHttpClient.executeMethod(get); 
			//System.out.println("real URL takes time : " + (System.currentTimeMillis() - st) + "ms.");
			//System.out.println("--> : " + get.getResponseContentLength());
			if (HttpURLConnection.HTTP_OK == responseCode) {
				//VideoStream vStream = new VideoStream(new HttpStream(get), get.getResponseBodyAsStream()); 
				VideoStream vStream = 
					new ControllableBWStream(new HttpStream(get),
											get.getResponseBodyAsStream(),
											this); 
				vStream.getInformer().addListeners(mListeners);
				vStream.setContentLength(get.getResponseContentLength());
				return vStream;
			}else {
				throw new DownloadInterruptedException("Prolem while getting the video: Cannot get the real Youtube video URL. "+ newURL);
			}
		} catch (HttpException e) {
			throw new DownloadInterruptedException("Problem while getting the video. Message: " +
					e.getMessage(),e);
		} catch (IOException ioe) {
			throw new DownloadInterruptedException("Problem while getting the video due to IOException. Message: " +
					ioe.getMessage(),ioe);
		} 
	}
	
	/**
	 * Prepare the HttpClient here
	 * @return HttpClient
	 */
	private HttpClient prepareHttpClient() {
		HttpClientParams htcParams = new HttpClientParams();
		mHttpClient = new HttpClient();
		htcParams.setConnectionManagerTimeout(300000);
		htcParams.setSoTimeout(300000);
		mHttpClient.setParams(htcParams);
		return mHttpClient;
	}

	/**
	 * Checking if video exists using GetMethod. 
	 * @param aGetMethod GetMethod
	 * @return true if exists
	 */
	private boolean isVideoExists(GetMethod aGetMethod) {
		/* This already called while this class is instantiated
		if (mHttpClient == null) {
			mHttpClient = prepareHttpClient();
		}*/
        try {
        	int r = mHttpClient.executeMethod(aGetMethod);
        	System.out.println("--> " + r);
			if (HttpURLConnection.HTTP_OK == r) {
				return true;
			}
		} catch (HttpException e) {
			//mm.. silence the exception and just print the stacktrace?
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new DownloadInterruptedException("Problem while getting the video due to IOException. Message: " +
					ioe.getMessage(),ioe);
		} 
		return false;
	}
	
	/**
	 * Parse response body. Find necessary parameter. 
	 * <pre>
	 * This will return array of necessary parameter :
	 * [0] : video_id
	 * [1] : l
	 * [2] : t
	 * 
	 * @param aInputStream InputStream
	 * @return list of needed parameters
	 */
	private String[] parseResponseBody(InputStream aInputStream) {
		String param[] = new String[3];
		StringBuffer strBuff = new StringBuffer();
		StringBuffer allBuff = new StringBuffer();
		byte buff[] = new byte[1024];
		int read;		
		final String PREFIX_SEARCH  ="/watch_fullscreen?video_id=";
		
		try {
			boolean found1st = false;
			boolean foundPrm = false;
			while((read = aInputStream.read(buff)) >= 0){
				allBuff.append(new String(buff));
				for (int i = 0;i < read;i++){
					char c = (char)buff[i];
					if (c == '/') {
						strBuff.setLength(0);
						strBuff.append(c);
						found1st = true;
					} else if (found1st) {
						strBuff.append(c);
						if (strBuff.length() == PREFIX_SEARCH.length()) {
							found1st = false;
							if (strBuff.toString().contains(PREFIX_SEARCH)) {
								//yeah.. we found it
								foundPrm = true;
							}
							strBuff.setLength(0);
						}
					} else if (foundPrm) {
						if (c == '&') {
							for (int y = 0;y < 3; y++) {
								if (param[y] == null){
									//remove parameter name and '=' 
									param[y] = y > 0 ? strBuff.toString().substring(2) : strBuff.toString();
									if (y == 2) {
										return param;
									} else {
										break;
									}
								}
							}
							strBuff.setLength(0);
						} else {
							strBuff.append(c);
						}
					}
				}
			}
			
			File f = new File(String.valueOf(System.currentTimeMillis() + ".txt"));
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(allBuff.toString().getBytes());
			fos.close();
			System.out.println(allBuff.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//If we go here, then the given parameter is not valid, or there's an exception
		return null;
	}

	
}
