package com.hjsj.hrms.transaction.train.b_plan;

public class MessBean implements Comparable {

	private String key2num;
	private String keyid;
	private String content;
	public int compareTo(Object arg0) {
		MessBean mb=(MessBean)arg0;
		if(new Integer(this.key2num).intValue()>new Integer(mb.getKey2num()).intValue())
			return 1;
		else if(new Integer(this.key2num).intValue()<new Integer(mb.getKey2num()).intValue()){
			return -1;
		}else
			return 0;
	}
	public String getKey2num() {
		return key2num;
	}
	public void setKey2num(String key2num) {
		this.key2num = key2num;
	}
	public String getKeyid() {
		return keyid;
	}
	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
