package org.phynx.vdownload;

import org.phynx.vdownload.streams.BandwidthManager;

import com.google.inject.Module;

/**
 * Standalone apps that run in command line. 
 * 
 * @author pandupradhana
 *
 */
public class ShellExec {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(howToUse());
			return;
		}
		Module modules[] = {new YoutubeModule() };		
		DownloadClient client = DownloadClient.Factory.newClient(modules);

		System.out.println("Start to download.. ");
		System.setProperty(Monitor.TOTAL_READ_STREAM, "1");
		System.setProperty(Monitor.TOTAL_TIME_GRAB, "1");
		long start = System.currentTimeMillis();
		
		VideoService vs = client.getVideoService(); 
		vs.addDownloadListener(new DownloadListener() {
			public void onProgress(DownloadStatus aStatus) {
				System.out.println("Progress : " + aStatus.getPercentage() + "%");
				System.out.println("Current : " + (aStatus.getCurrentTotalRead() / 1024) + "KBytes");
			}
		});
		if (vs instanceof BandwidthManager) {
			((BandwidthManager)vs).setBWLimit(20);
		}
		
		if (args.length > 1) {
			client.grabVideo(args[0],args[1]);
		} else {
			client.grabVideo(args[0]);
		}
		System.out.println("End of download. Takes : " + (System.currentTimeMillis() - start) + "ms.");
		
	}
	
	/**
	 * Just a guide of how to use the application
	 * @return
	 */
	private static String howToUse() {
		return 
			"java -cp LIBS ShellExec url_to_dowbnload [filename]\n" +
			"Where LIBS is your libraries paths,\n" +
			"	   url_to_dowbnload is valid url,\n" +
			"	   filename (optional) is video file to save";
	}
}
