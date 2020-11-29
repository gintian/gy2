package com.hjsj.hrms.valueobject.common;

public class FeastType {
  private String feast_id;
  private String feast_name;
  private String sdate;
  private int index;
  //private String edate;
  
  public FeastType(String feast_id,String feast_name,String sdate,int index){
	  this.feast_name=feast_name;
	  this.feast_id=feast_id;
	  this.sdate=sdate;
	  this.index=index;
  }
public String getFeast_name() {
	return feast_name;
}

public void setFeast_name(String feast_name) {
	this.feast_name = feast_name;
}
public String getFeast_id() {
	return feast_id;

}

public void setFeast_id(String feast_id) {
	this.feast_id = feast_id;
}

public String getSdate() {
	return sdate;
}
public void setSdate(String sdate) {
	this.sdate = sdate;
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
	
}
