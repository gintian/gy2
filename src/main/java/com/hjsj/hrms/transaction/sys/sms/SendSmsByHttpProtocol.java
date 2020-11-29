package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.interfaces.sys.SmsProxy;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SendSmsByHttpProtocol implements SmsProxy {

	// 下行链接
	private String url = "http://sdk2.entinfo.cn/z_mdsmssend.aspx";
	// 软件序列号
	private String sn = "234";
	// 密码
	private String pwd = "123";
	// 子号
	private String ext = "";

	public boolean sendMessage(String phone, String msg) {
		url = SystemConfig.getPropertyValue("sms_url").trim();
		sn = SystemConfig.getPropertyValue("sms_sn").trim();
		pwd = SystemConfig.getPropertyValue("sms_pwd").trim();
		ext = SystemConfig.getPropertyValue("sms_ext").trim();
		
		boolean flag  = false;
		// 创建与服务器的连接
		HttpClient client = new HttpClient();
		client.getParams().setContentCharset("GB2312");

		client.getParams().setHttpElementCharset("GB2312");
		PostMethod post = new PostMethod(this.url);
		String xml = "";
		try {
			// 填入各个表单域的值
			String md5 = getMD5Str(sn + pwd).toUpperCase();
			NameValuePair[] data = { new NameValuePair("sn", this.sn),
									new NameValuePair("pwd", md5), 
									new NameValuePair("mobile", phone),
									new NameValuePair("content", msg),
									new NameValuePair("ext", ext), 
									new NameValuePair("rrid", ""),
									new NameValuePair("stime", "")
									};
			// 将表单的值放入postMethod中
			post.setRequestBody(data);
			
			//post.addParameters(data);
			
			client.executeMethod(post);
			String res = post.getResponseBodyAsString();
			if ("1".equals(res)){
				flag = true;
			} else {
				//System.out.println(res);
				if ("-2".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("帐号/密码不正确！1.序列号未注册\r\n2.密码加密不正确\r\n3.密码已被修改！"));
				} else if ("-4".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("余额不足！"));
				} else if ("-5".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("数据格式错误！"));
				} else if ("-6".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("参数有误！看参数传的是否均正常,请调试程序查看各参数"));
				} else if ("-7".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("权限受限！"));
				} else if ("-8".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("流量控制错误！"));
				}else if ("-9".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("扩展码权限错误！该序列号是否已经开通了扩展子号的权限"));
				} else if ("-10".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("内容长度长！短信内容过长检查是否超过500字"));
				} else if ("-11".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("内部数据库错误！系统错误如果长时间返回该项请联系服务方"));
				} else if ("-12".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("序列号状态错误！序列号是否被禁用!"));
				} else if ("-18".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("需等待上次的提交返回！默认不支持多线程提交"));
				} else if ("-19".equals(res)) {
					throw GeneralExceptionHandler.Handle(
							new Exception("禁止同时使用多个接口地址,每个序列号提交只能使用一个接口地址！"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			post.releaseConnection();
		}
		return flag;
	}

	public static void main(String[] str) {
		new SendSmsByHttpProtocol().sendMessage("15210940318", "测试");
	}
	
	private String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			// 重置，防止以前的值影响最后的结果
			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 加密后的字节
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();

		// 将字节数组转成字符窜
		for (int i = 0; i < byteArray.length; i++) {
			int ch = 0xFF & byteArray[i];
			String hex = Integer.toHexString(ch);
			if (hex.length() == 1) {
				md5StrBuff.append("0");
				md5StrBuff.append(hex);
			} else {
				md5StrBuff.append(hex);
			}
		}

		return md5StrBuff.toString();
	}

}
