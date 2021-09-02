package com.licentamihai.alumni.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Currently working for Groups and channels. Further work is required to generify it.
 * The CascadeDeleteCallback and CascadeSndLevelDelete are used to handle the delete.
 * Only CascadeDeleteCallback needs rework.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CascadeDelete {
}
