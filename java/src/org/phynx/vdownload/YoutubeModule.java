package org.phynx.vdownload;

import org.phynx.vdownload.annotations.MaximumDLLimit;
import org.phynx.vdownload.annotations.YoutubeDownloadURLPrefix;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Youtube Video module, to bind dependencies. This should be called by Guice later.
 * 
 * @author pandupradhana
 *
 */
public class YoutubeModule implements Module {

	public void configure(Binder binder) {
		binder.bind(VideoService.class).to(YoutubeVideo.class);
		binder.bindConstant().annotatedWith(YoutubeDownloadURLPrefix.class)
			.to("http://www.youtube.com/get_video?video_id=");
		//80K
		binder.bindConstant().annotatedWith(MaximumDLLimit.class)
			.to(81920);		
	}
	
}
