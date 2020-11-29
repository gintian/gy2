package com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.models;

import net.sf.json.JSONObject;

//自定义消息
public class CustomTxtMessage extends Message {

	private String content;

	public CustomTxtMessage(String content) {
		this.type = "KM:TxtMsg";
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		JSONObject configObj = JSONObject.fromObject(this);
		return configObj.toString();
		//return GsonUtil.toJson(this, CustomTxtMessage.class);
	}
}
