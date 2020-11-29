package com.hjsj.hrms.utils;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * 发送消息到外部系统
 * @author xus
 * 17/4/28
 */
public class OutSysMsgBo {
	private static Logger log =  Logger.getLogger(OutSysMsgBo.class.getName());
	private static RecordVo recordVo = ConstantParamter.getConstantVo("SS_QQWX");
	private static RecordVo DDrecordVo = ConstantParamter.getConstantVo("DINGTALK");
	/**
	 * 发送单条消息到多人usernames
	 * @param usernames
	 * @param title
	 * @param description
	 * @param picUrl 图片路径
	 * @param url
	 */
	public static boolean sendMsgToPerson(List usernames, String title, String description, String picUrl, String url){
		boolean flag=false;
		//发送微信消息
		if(!WeiXinBo.sendMsgToPerson(usernames, title,description, picUrl, url))
			log.info("微信消息发送失败");
		else
			flag=true;
		
		//发送钉钉消息
		if(!DTalkBo.sendMessage(usernames, title, description,picUrl,url))
			log.info("钉钉消息发送失败");
		else
			flag=true;
		
		return flag;
	}
	
	public static boolean sendMsgToPerson(List A0100s,String Nbase, String title, String description, String picUrl, String url){
		if(Nbase==null|| "".equals(Nbase)){
			log.info("Nbase is not exist !");
			return false;
		}
		boolean flag=false;
		//发送微信消息
		if(!WeiXinBo.sendMsgToPerson(Nbase,A0100s, title,description, picUrl, url))
			log.info("微信消息发送失败");
		else
			flag=true;
		//发送钉钉消息
		if(!DTalkBo.sendMessage(A0100s,Nbase, title, description, picUrl, url))
			log.info("钉钉消息发送失败");
		else
			flag=true;
		
		return flag;
	}
	
	public static boolean sendMsgToPerson(String username, String title, String description, String picUrl, String url){
		boolean flag=false;
		//发送微信消息
		
		// 判断数据库中是否存在
		if(recordVo != null){
			if(!WeiXinBo.sendMsgToPerson(username, title,description, picUrl, url))
				log.info("微信消息发送失败");
			else
				flag=true;
		}
		//发送钉钉消息
		if(DDrecordVo != null){
			if(!DTalkBo.sendMessage(username, title, description, picUrl, url))
				log.info("钉钉消息发送失败");
			else
				flag=true;
		}
		return flag;
	}
	
	
	public static boolean sendMsgToPerson(String A0100,String Nbase, String title, String description, String picUrl, String url){
		if(Nbase==null|| "".equals(Nbase)){
			log.info("Nbase is not exist !");
			return false;
		}
		boolean flag=false;
		//发送微信消息
		if(WeiXinBo.sendMsgToPerson(Nbase,A0100, title,description, picUrl, url)){
			flag=true;
		}else
			log.info("微信消息发送失败");
		//发送钉钉消息
		if(DTalkBo.sendMessage(A0100,Nbase, title, description, picUrl, url))
			flag=true;
		else
			log.info("钉钉消息发送失败");
		return flag;
	}
	
	public static boolean sendMsgToPerson(List usernames, List articles){
		boolean flag=false;
		//发送微信消息
		if(WeiXinBo.sendMsgToPerson(usernames,articles)){
			flag=true;
		}else
			log.info("微信消息发送失败");
		//发送钉钉消息
		if(DTalkBo.sendMessage( usernames, articles))
			flag=true;
		else
			log.info("钉钉消息发送失败");
		
		return flag;
	}
	
	public static boolean sendMsgToDept(List orgids, List articles){
		boolean flag=false;
		//发送微信消息
		if(WeiXinBo.sendMsgToDept(orgids,articles)){
			flag=true;
		}else
			log.info("微信消息发送失败");
		//发送钉钉消息
		if(DTalkBo.sendMsgToDept(orgids,articles))
			flag=true;
		else
			log.info("钉钉消息发送失败");
		
		return flag;
	}
	
	public static boolean sendMsgToDept(List orgids, String title,String description,String picUrl,String url){
		boolean flag=false;
		//发送微信消息
		if(WeiXinBo.sendMsgToDept( orgids,  title, description, picUrl, url)){
			flag=true;
		}else
			log.info("微信消息发送失败");
		//发送钉钉消息
		if(DTalkBo.sendMsgToDept(orgids,  title, description, picUrl, url))
			flag=true;
		else
			log.info("钉钉消息发送失败");
		
		return flag;
	}
}
