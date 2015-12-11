package com.fline.hadoop.data.common.event;

import java.util.EventObject;

public class ProgressEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProgressEvent(ProgressSource source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
}
