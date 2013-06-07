package org.phynx.vdownload.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * <p>This will be used by bandwidth manager to set its maximum bandwidth limit.
 * Later should be use by Guice to inject constant dependencies</p>
 * 
 * <p>Value should be in bytes measure</p>
 * 
 * @author Pandu
 *
 */
@Retention(RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@BindingAnnotation
public @interface MaximumDLLimit{}

