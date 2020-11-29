package com.hjsj.hrms.businessobject.sys.cmpp;

public class Test2{
	public static void main(String[] str) {
		
		String content = "y测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信测试短信【九华发电人资部】";
		String phone = "152109403181";
		
		MsgConfig.setConnectCount(3);
		MsgConfig.setIsmgIp("117.79.237.7");//IP
		MsgConfig.setIsmgPort(7890);//端口
		MsgConfig.setSpCode("");
		MsgConfig.setSpId("200183");//spid
		MsgConfig.setSpSharedSecret("910348");//密码
		MsgConfig.setTimeOut(10000);
//		Timer timer = new Timer();
//		timer.schedule(new MsgActivityTimer(), 100);
		MsgContainer2.sendMsg(content, phone);
		
		MsgContainer2.sendMsg("22" + content, phone);
		
		
//		MsgContainer.sendMsg(content + "测试短信【九华发电人资部】", phone);
//		
//		MsgContainer.sendMsg( content + "3333【九华发电人资部】", phone);
	}
}
