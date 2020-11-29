package com.hjsj.hrms.service.syncdata;

/**
 * <p>Description>:存储HR系统与对方系统的指标对应关系。</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2017-09-15 </p>
 * <p>@version: 1.0</p>
 */

public class FieldRefBean {	
	//HR系统指标名称
	private String hrField="";	
	//对方系统的指标名称
	private String destField="";	
	//指标描述
	private String flddesc="";	
	

	public  FieldRefBean() {
	
	}


	public String getHrField() {
		return hrField;
	}


	public void setHrField(String hrField) {
		this.hrField = hrField;
	}


	public String getDestField() {
		return destField;
	}


	public void setDestField(String destField) {
		this.destField = destField;
	}


	public String getFlddesc() {
		return flddesc;
	}


	public void setFlddesc(String flddesc) {
		this.flddesc = flddesc;
	}

}
