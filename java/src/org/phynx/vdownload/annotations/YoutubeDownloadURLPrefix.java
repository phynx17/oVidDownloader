package org.phynx.vdownload.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * This annotation will be use to set Youtube video download URL.
 * 
 * @author pandupradhana
 *
 */
@Retention(RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@BindingAnnotation
public @interface YoutubeDownloadURLPrefix {}
