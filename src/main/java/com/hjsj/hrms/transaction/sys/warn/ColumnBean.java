package com.hjsj.hrms.transaction.sys.warn;

public class ColumnBean {

	private String columnName;	//显示列名称
	private String columnType;	//字段类型(字符 备注 左对齐 日期 数值 右对齐) 标识(L R)
	private String codesetid;
	private String columndesc;
/*	指标类型(A,D,N,M)
	=A字符型
	=D日期型
	 显示方式根据长度来定
	 itemlength=10  2003.10.10 
	           =7   2003.10
	=N数值型
	 根据小数点的位数
	 decimalwidth=0,没有小数点
	=M备注型
	*/
	
	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getColumndesc() {
		return columndesc;
	}

	public void setColumndesc(String columndesc) {
		this.columndesc = columndesc;
	}	
}
