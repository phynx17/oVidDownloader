package org.phynx.vdownload.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * This annotation will be use to set stream type. 
 * For instance in HTTP, set whether using GET or POST method
 * 
 * @author pandupradhana
 *
 */
@Retention(RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@BindingAnnotation
public @interface StreamType {}
