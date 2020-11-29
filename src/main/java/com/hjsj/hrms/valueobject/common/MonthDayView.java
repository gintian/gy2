package com.hjsj.hrms.valueobject.common;

import java.io.Serializable;

public class MonthDayView implements Serializable{
	private String fmonth;
	private String fday;
	private String fyear;
	
	public MonthDayView(String fmonth,String fday,String fyear){
	  this.fmonth=fmonth;
	  this.fday=fday;
	  this.fyear=fyear;
	}
	public String getFday() {
		return fday;
	}
	public void setFday(String fday) {
		this.fday = fday;
	}
	public String getFmonth() {
		return fmonth;
	}
	public void setFmonth(String fmonth) {
		this.fmonth = fmonth;
	}
	public String getFyear() {
		return fyear;
	}
	public void setFyear(String fyear) {
		this.fyear = fyear;
	}
	
	

}
