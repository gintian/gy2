package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.interfaces.sys.SmsProxy;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;

import java.io.*;
import java.net.Socket;

public class SendSmsBySocket implements SmsProxy {

	// 服务端ip地址
	private String ip = "127.0.0.1";
	// 服务端接口
	private int port = 5100;
	
	/**
	 * <?xml version='1.0' encoding='GB2312'?>
		<message>
		<phones></phones>
		<content><![CDATA[内容]]></content>
		</message>

	 */
	
	
	public static void main(String[] args) {
		new SendSmsBySocket().send("15210940318", "测试23456");

	}
	
	
	/**
	 * 发送短信
	 * @param phones 手机号码，可以多个号码，多个号码之间用英文逗号隔开
	 * @param content 短信内容
	 * @return 是否发送成功，true为发送成功，false为发送失败
	 */
	public boolean  send(String phones, String content) {
		boolean flag = false;
		PrintWriter wirter = null;
		BufferedReader reader = null;
		Socket socket = null;
		try {
			// 创建客户端socket
			socket = new Socket(this.ip, this.port);
			// 获得读写流，以便发送接受消息
			OutputStream output = socket.getOutputStream();
			InputStream input = socket.getInputStream();
			wirter = new PrintWriter(output, true);
			
			// 将号码、短信内容组成xml格式
			StringBuffer buff = new StringBuffer();
			buff.append("<?xml version='1.0' encoding='GB2312'?>");
			buff.append("<message>");
			buff.append("<phones>");
			buff.append(phones);
			buff.append("</phones>");
			buff.append("<content><![CDATA[");
			buff.append(content);
			buff.append("]]></content>");
			buff.append("</message>");
			
			// 将内容发送给服务端
			wirter.println(buff.toString());
			wirter.flush();
			
			// 服务端返回消息后接收
			InputStreamReader inputReader = new InputStreamReader(input, "GB2312");
			reader = new BufferedReader(inputReader);
			String str = reader.readLine();
			
			// 返回值，0表示发送成功，-1表示发送失败
			if ("0".equals(str)) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			PubFunc.closeResource(wirter);
			PubFunc.closeResource(reader);
			PubFunc.closeResource(socket);
		}
		
		// 返回是否发送成功，ture为成功，false为失败
		return flag;
	}

	public boolean sendMessage(String phone, String msg) {
		this.ip = SystemConfig.getPropertyValue("serversocket_ip");
		this.port = Integer.parseInt(SystemConfig.getPropertyValue("serversocket_port"));
		return send(phone, msg);
	}
}
