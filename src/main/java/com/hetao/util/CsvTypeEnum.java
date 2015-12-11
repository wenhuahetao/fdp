package com.hetao.util;

import cn.safetys.util.IEnum;

public enum CsvTypeEnum implements IEnum{

	CSV_STRING_TYPE {
		@Override public int getType() { return 1; }
		@Override public String getText() { return "字符串类型"; }
	},
	CSV_NUM_TYPE {
		@Override public int getType() { return 2; }
		@Override public String getText() { return "整型"; }
	},
	CSV_DATE_TYPE {
		@Override public int getType() { return 3; }
		@Override public String getText() { return "日期类型"; }
	};
	
	public abstract int getType();
	
	@Override
	public String getCode() {
		return name();
	}
}
