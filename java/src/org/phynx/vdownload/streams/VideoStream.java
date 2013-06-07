package org.phynx.vdownload.streams;

import java.io.IOException;
import java.io.InputStream;

import org.phynx.vdownload.DownloadInformer;
import org.phynx.vdownload.DownloadInterruptedException;
import org.phynx.vdownload.DownloadStatus;

/**
 * This class wraps connection made to video service and
 * stream for saving. 
 * 
 * @author pandupradhana
 *
 */
public class VideoStream implements DownloadStatus {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 8614712559855154803L;

	/**
	 * Method that handle connection to video service
	 */
	private ServiceStream mServiceStream;
	
	/**
	 * Input stream. 
	 */
	private InputStream mInputStream;
	
	/**
	 * Download informer
	 */
	private final DownloadInformer mInformer = DownloadInformer.createInformer(this);
	
	/**
	 * Content Length
	 */
	private long mContentLength = 0L;
	
	/**
	 * Current byte read
	 */
	private long mCurrentRead = 0L;
	
	
	/**
	 * Constructor. Providing GetMethod and InputStream
	 * 
	 * @param aGetMethod
	 * @param aInputStream
	 * @param mBufferSize
	 */
	VideoStream(ServiceStream aServiceStream, InputStream aInputStream) {
		if (aServiceStream == null) {
			throw new IllegalArgumentException("ServiceStream cannot be null");
		}
		if (aInputStream == null) {
			throw new IllegalArgumentException("InputStream cannot be null");
		}
		mServiceStream = aServiceStream;
		mInputStream = aInputStream;
	}
	
	
	/**
	 * Read byte from stream to buffer, and also update how many read to 
	 * the DownloadInformer
	 * 
	 * @param aBuffer byte of buffer
	 * @return byte read
	 */
	public int read(byte[] aBuffer) {
		try {
			int now = mInputStream.read(aBuffer);
			mCurrentRead += (long)now;
			mInformer.updateProgress();
			return now;
		} catch (IOException e) {
			throw new DownloadInterruptedException("Failed while reading streams : " +
					e.getMessage(),e);
		}
	}
	
	
	/**
	 * Get download informer
	 * 
	 * @return DownloadInformer
	 */
	public DownloadInformer getInformer() {
		return mInformer;
	}
	
	
	/**
	 * Set downloaded content length
	 *  
	 * @param aContentLength content length
	 */
	public void setContentLength(long aContentLength) {
		mContentLength = aContentLength;
	}
	
	/**
	 * Close stream and release HTTP connection
	 *
	 */
	public void close() {
		try {
			mInputStream.close();
			mServiceStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.DownloadStatus#getCurrentTotalRead()
	 */
	public long getCurrentTotalRead() {
		return mCurrentRead;
	}

	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.DownloadStatus#getPercentage()
	 */
	public int getPercentage() {
		return (int)( (mCurrentRead*100) / mContentLength);
	}

}
