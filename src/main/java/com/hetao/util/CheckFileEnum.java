package com.hetao.util;

import cn.safetys.util.IEnum;

public enum CheckFileEnum implements IEnum{

	TYPEERROR {
		@Override public String getText() { return "类型错误"; }
	},
	EMPTYCSV {
		@Override public String getText() { return "空字符串"; }
	},
	EMPTYLINE {
		@Override public String getText() { return "空字符串"; }
	},
	REPEAT {
		@Override public String getText() { return "内容重复"; }
	},
	REPEATCHAR {
		@Override public String getText() { return "字符重复"; }
	};
	
	@Override
	public String getCode() {
		return name();
	}
	
}
