package com.fline.hadoop.data.common.annotation;

public @interface DataSourceType {
	boolean remote() default true;
}
