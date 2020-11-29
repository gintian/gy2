/**
 * 
 */
package com.hjsj.hrms.test;

import com.blogsky.smsif.*;
import com.blogsky.smsif.transport.HttpTool;
import com.blogsky.smsif.transport.SimpleHttpEnSmsSender;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2008-3-18:上午08:43:12</p> 
 *@author cmq
 *@version 4.0
 */
public class TestSms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String url="http://smsengine.blogsky.com.cn/smsengine/SmsIfReceiver";

			SimpleHttpEnSmsSender sender=new SimpleHttpEnSmsSender(url);
			MTEnSms mtsms=new MTEnSms(1,"13801297310","ok。测试短信","","40"); //参数分别为：序号，目标号码，短信内容，源号码后缀，channelid
			EnSmsMTMessage mtmessage=new EnSmsMTMessage();
			mtmessage.setUsername("shoukai_res");//平台分配的用户名。
			mtmessage.setPassword("shoukai_res");//平台分配的密码。
			mtmessage.append(mtsms);		
			byte[] data=EnSmsMessageTools.toByte(mtmessage);
			EnSmsMessage msg2=EnSmsMessageTools.toMessage(data);
			data=HttpTool.send(EnSmsMessageTools.toByte(mtmessage),url);
			EnSmsRespMessage resp=(EnSmsRespMessage)EnSmsMessageTools.toMessage(data);
			Status[] statues=resp.getStatuses();
			if(statues.length==1&&statues[0].getData()==0){
				System.out.println(true);
			}else{
				System.out.println(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}


}
