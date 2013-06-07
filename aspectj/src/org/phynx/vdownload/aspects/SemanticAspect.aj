package org.phynx.vdownload.aspects;

/**
 * This aspect checks of several usage patterns 
 * while compile time.  
 * 
 * @author Pandu
 * 
 */
public aspect SemanticAspect {

	/**
	 * Give warning at compile time if VideoService implemtation is called directly
	 */
	declare warning : call (public org.phynx.vdownload.VideoService+.new(..))
		: "Consider to use org.phynx.vdownload.DownloadClient " +
				"to access VideoService implementation";
	
}
