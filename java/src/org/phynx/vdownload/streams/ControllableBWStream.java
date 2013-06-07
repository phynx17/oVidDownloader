package org.phynx.vdownload.streams;

import java.io.InputStream;

/**
 * Provides functionality to control current stream, such as bandwith.
 * 
 * @author pandupradhana
 *
 */
public class ControllableBWStream extends VideoStream {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 5200885741938222491L;
	
	/**
	 * To stop woker thread
	 */
	private boolean mStopThread = false;
	
	/**
	 * Statistics
	 */
	private final BWStat mStat = new BWStat();
	
	/**
	 * Bandwidth manager
	 */
	private final BandwidthManager mBWManager;

	/**
	 * Constructor. With no bandwidth manager
	 * 
	 * @param aServiceStream service stream 
	 * @param aInputStream InputStream
	 */
	public ControllableBWStream(ServiceStream aServiceStream, InputStream aInputStream) {
		this(aServiceStream,aInputStream,null);
	}
	
	/**
	 * Constructor with given bandwidth manager. 
	 * 
	 * @param aServiceStream service stream 
	 * @param aInputStream InputStream
	 * @param aBWManager bandwidth manager
	 */
	public ControllableBWStream(ServiceStream aServiceStream, InputStream aInputStream,
			BandwidthManager aBWManager) {
		super(aServiceStream, aInputStream);
		mBWManager = aBWManager;
		new Thread(new BWControlThread()).start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.streams.VideoStream#read(byte[])
	 */
	@Override
	public int read(byte[] aBuffer) {
		int l = (mBWManager == null ? -1 : mBWManager.getCurrentLimit());
		while(l > 0 && (mStat.currentRead() > l)){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//long st = System.currentTimeMillis();
		int t =  super.read(aBuffer);
		//System.out.println("-" + (System.currentTimeMillis()-st));
		mStat.addBytesRead(t);
		return t;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.phynx.vdownload.streams.VideoStream#close()
	 */
	@Override
	public void close() {
		super.close();
		mStopThread = true;
	}
	
	/**
	 * Class to hold current statistics
	 * 
	 * @author pandupradhana
	 *
	 */
	private final class BWStat {
		//primitive value such int, don't need to set it as transient
		int mRead = 0;
		
		/**
		 * Constructor
		 */
		BWStat() {
		}
		
		/**
		 * Add current bytes read
		 * @param aRead read
		 */
		public void addBytesRead(int aRead) {
			mRead += aRead;
		}
		
		/**
		 * How many read at the time
		 * @return
		 */
		public int currentRead() {
			return mRead;
		}
		
		/**
		 * Reset current read
		 *
		 */
		public void reset() {
			mRead = 0;
		}
	}
	
	/**
	 * This thread responsible to limit the bandwidth
	 * @author pandupradhana
	 *
	 */
	class BWControlThread implements Runnable {

		public void run() {
			while (!mStopThread) {
				mStat.reset();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//Any interuption should carry on
					e.printStackTrace();
				}
			}
		}
		
	}
}


