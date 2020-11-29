package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.businessobject.performance.workplan.PersonListShowBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SendMessageTrans.java</p>
 * <p>Description>:发送短信和邮件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 18, 2010 11:11:11 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SendMessageTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String info="发送成功!";
			String opt=(String)this.getFormHM().get("opt");// 1:目标卡状态  2：打分状态 主体  3: 目标执行（回顾情况） 4：目标执行情况
			String plan_id=(String)this.getFormHM().get("plan_id");
			String object_id=(String)this.getFormHM().get("object_id");
			String to_a0100=(String)this.getFormHM().get("to_a0100");
			String departid=(String)this.getFormHM().get("departid");
			String name=SafeCode.decode((String)this.getFormHM().get("name"));
			String isAll=(String)this.getFormHM().get("isAll");
			String flag=(String)this.getFormHM().get("flag");  //1:发送消息 2：发送邮件
			String title=SafeCode.decode((String)this.getFormHM().get("title"));
			String content=SafeCode.decode((String)this.getFormHM().get("content"));
			String objMainbodys=(String)this.getFormHM().get("objMainbodys");
			content= content.replace("＜", "<").replace("＞", ">");
			
			if(opt!=null && opt.trim().length()>0 && ("3".equalsIgnoreCase(opt) || "4".equalsIgnoreCase(opt)))
			{
				// 保存邮件模板信息
				ConstantXml xml = new ConstantXml(this.frameconn, "PER_PERFORMCASE", "Per_Performcase");								
				xml.setTextValue("/Per_Performcase/Title", title);
				xml.setTextValue("/Per_Performcase/BuiltName", this.userView.getUserName());
				xml.setTextValue("/Per_Performcase/EmailDistri", content);
				xml.saveStrValue();
			}
			
			if("1".equals(flag)) //发送短消息
			{
				AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.getFrameconn());
	    		String mobile_field =autoSendEMailBo.getMobileField();
	    		/**未设置电话指标*/
	    		if(mobile_field==null || "".equals(mobile_field)){
	    			info="未设置电话指标";
	    			this.getFormHM().put("info", info);
	    			return;
	    		}
	    		 RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	    		 /**未设置短信接口参数*/
		         if(sms_vo==null)
		         {
		        	 info="未设置短信接口参数";
		        	 this.getFormHM().put("info", info);
		    		 return;
		         }
		         String param=sms_vo.getString("str_value");
		         if(param==null|| "".equals(param))
		         {
		        	 info="未设置短信接口参数";
		        	 this.getFormHM().put("info", info);
		    		 return;
		         } 
		         ArrayList list=getSendInfoList(mobile_field);
		         if(list.size()==0)
		         {
		        	 info="没有设置电话号码，发送失败!";
		         }
		         else
		         {
			         SmsBo smsbo=new SmsBo(this.getFrameconn());
		        	 smsbo.batchSendMessage(list);
		         }
				
			}
			else
			{
				String fromaddr=this.getFromAddr();
				if(fromaddr==null||fromaddr.trim().length()==0)
				{
					 info="未设置系统邮件服务器";
		        	 this.getFormHM().put("info", info);
		    		 return;
				}
				String email_field="";
				RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
				if(avo!=null)
					email_field=avo.getString("str_value");
				if(email_field==null||email_field.trim().length()==0)
				{
					 info="未设置电子邮箱";
		        	 this.getFormHM().put("info", info);
		    		 return;
				}
				ArrayList list=getSendInfoList(email_field);
				EMailBo bo =null;
				try
				{
					bo=new EMailBo(this.getFrameconn(),true,"");
				}
				catch(Exception e)
				{
					//将错误抛出，终止程序  haosl  2018-2-28
					throw GeneralExceptionHandler.Handle(e);
				}
				if(list.size()<=0)
		        {
					if("1".equals(opt) && (object_id!=null && object_id.trim().length()>0))
					{
						if(to_a0100!=null && to_a0100.trim().length()>0)
						{
							to_a0100 = to_a0100.substring(1);
							String[] maiValues = to_a0100.replaceAll("／", "/").split("/");	
							if(maiValues==null || maiValues.length<=0 || maiValues[0]==null || maiValues[0].trim().length()<=0)
								info="目标卡已办理，系统不再发送邮件通知！";
							else
								info="没有设置邮箱地址，发送失败！";
						}
						else
							info="没有设置邮箱地址，发送失败！";
					}
					else
						info="没有设置邮箱地址，发送失败！";
		        }
		        else
		        {
		        	HashMap mainbodyMap = new HashMap();
		        	String Str = "考核主体：\n    ";
		        	boolean flagStr = false; 
					for(int i=0;i<list.size();i++)
					{
						LazyDynaBean dyvo=(LazyDynaBean)list.get(i);
						String email = (String)dyvo.get("email"); // email地址

						if(opt!=null && opt.trim().length()>0 && ("3".equalsIgnoreCase(opt) || "4".equalsIgnoreCase(opt)))
						{
							String mainbody_id = (String)dyvo.get("mainbody_id");
							String mainbodyName = (String)dyvo.get("mainbodyName");							
							if(email==null || email.trim().length()<=0)	
							{																
								if(mainbodyMap.get(mainbody_id)==null)
									Str+=mainbodyName+";";
								flagStr = true; 
								mainbodyMap.put(mainbody_id, "1");
							}
						}		 
					}					
					Str+="\n没有设置邮箱地址或收件人已经打分，发送失败！";
					
					if(flagStr)
						info = Str;
					else
					{
						for(int i=0;i<list.size();i++)
						{	
							
							LazyDynaBean dyvo=(LazyDynaBean)list.get(i);
							String email = (String)dyvo.get("email"); // email地址	
							String status = (String)dyvo.get("status"); // status
							String a0100 = (String)dyvo.get("a0100"); // status
							
							if("2".equals(opt)){//只针对打分状态发送邮件才判断 status zhaoxg add 2014-7-7  慧聪需求产生的bug
								if(email!=null && email.trim().length()>0&&("0".equals(status)||"1".equals(status)))//status ：0：未打分 1：正在编辑	慧聪网需求   只对这俩状态才发通知  zhaoxg add 2014-6-25		
									bo.sendEmail((String)dyvo.get("title"),(String)dyvo.get("content"),"",fromaddr,(String)dyvo.get("email"));
								if("0".equals(status)||"1".equals(status)){
									String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
									if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
										WeiXinBo.sendMsgToPerson("Usr", a0100, (String)dyvo.get("title"), (String)dyvo.get("content"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
									}
									String ddcorpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
									if(ddcorpid!=null&&ddcorpid.length()>0){//推送钉钉公众号  xus add 2017-6-2
										DTalkBo.sendMessage( a0100,"Usr", (String)dyvo.get("title"), (String)dyvo.get("content"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
									}
								}
							}else{
								if(email!=null && email.trim().length()>0)	
									bo.sendEmail((String)dyvo.get("title"),(String)dyvo.get("content"),"",fromaddr,(String)dyvo.get("email"));
								
								String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
								if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
									WeiXinBo.sendMsgToPerson("Usr", a0100, (String)dyvo.get("title"), (String)dyvo.get("content"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
								}
								
								String ddcorpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
								if(ddcorpid!=null&&ddcorpid.length()>0){//推送钉钉公众号  xus add 2017-6-2
									DTalkBo.sendMessage( a0100,"Usr", (String)dyvo.get("title"), (String)dyvo.get("content"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
								}
							}

						}
					}
		        }
			}			
			
			this.getFormHM().put("info",SafeCode.encode(info));
		}
		catch(Exception e)
		{
			this.getFormHM().put("info","发送失败");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 发送邮件或短信
	 * @param mailOrMessage 发送邮件时是邮件指标，短信时是短信指标
	 * @return
	 */
	private ArrayList getSendInfoList(String mailOrMessage)
	{
		ArrayList list=new ArrayList();
		String opt=(String)this.getFormHM().get("opt");// 1:目标卡状态  2：打分状态 主体  3: 目标执行（回顾情况） 4：目标执行情况  5.填报状态  日志的报批情况
		String logo=(String)this.getFormHM().get("logo"); // 批量发送通知标识
		String plan_id=(String)this.getFormHM().get("plan_id");
		if (plan_id != null && plan_id.length() > 0) {
			if (!plan_id.matches("^\\d+$")) { // 不是纯数字格式，则有可能是加密后的计划id
				plan_id = PubFunc.decrypt(plan_id);
			}
		}
		String object_id=(String)this.getFormHM().get("object_id");
		String to_a0100=(String)this.getFormHM().get("to_a0100");
		String departid=(String)this.getFormHM().get("departid");
		String name=SafeCode.decode((String)this.getFormHM().get("name"));
		String isAll=(String)this.getFormHM().get("isAll");
		String flag=(String)this.getFormHM().get("flag");  //1:发送消息 2：发送邮件
		String title=SafeCode.decode((String)this.getFormHM().get("title"));
		String content=SafeCode.decode((String)this.getFormHM().get("content"));
		content= content.replace("＜", "<").replace("＞", ">");
		String objMainbodys=(String)this.getFormHM().get("objMainbodys");    
		String numberlist = SafeCode.decode((String)this.getFormHM().get("numberlist"));//获得填报状态的参数
		String strFrom = SafeCode.decode((String)this.getFormHM().get("strFrom"));//获得填报状态的参数				
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String sysTime=bartDateFormat.format(new Date());
			
			if("1".equals(opt))
			{
				String sql="";
				
				RecordVo vo = new RecordVo("per_plan");
				vo.setInt("plan_id",Integer.parseInt(plan_id));
				vo = dao.findByPrimaryKey(vo);
				
				// 批量发送通知								
				if(logo!=null && logo.trim().length()>0 && "1".equalsIgnoreCase(logo))
				{									
					if(object_id!=null && object_id.trim().length()>0)
					{
						object_id = object_id.substring(1);
						to_a0100 = to_a0100.substring(1);
						String[] objValues = object_id.replaceAll("／", "/").split("/");
						String[] maiValues = to_a0100.replaceAll("／", "/").split("/");
						for(int i=0;i<objValues.length;i++)
						{
							String object = objValues[i];
							if(maiValues!=null && maiValues.length>0 && maiValues[i]!=null && maiValues[i].trim().length()>0)
							{
								String mainbody = maiValues[i];
								String addresStr = content;								
								if(mainbody.equals(object))
								{									
									if(vo.getInt("object_type")==2)
									{
										sql="select per_object.a0101 as objname,per_object.a0101 as mainname,per_plan.name,usra01."+mailOrMessage+" as address from per_object,UsrA01,per_plan where Usra01.a0100=per_object.object_id and per_object.plan_id=per_plan.plan_id ";
										sql+=" and  per_object.plan_id="+plan_id+" and per_object.object_id='"+object+"' ";
									}
									else
									{						
										sql="select per_object.a0101 as objname,per_mainbody.a0101 as mainname,per_plan.name,usra01."+mailOrMessage+" as address from per_object,per_mainbody,UsrA01,per_plan where Usra01.a0100=per_mainbody.mainbody_id and per_mainbody.plan_id=per_plan.plan_id ";
										sql+=" and  per_object.plan_id="+plan_id+" and per_object.object_id=per_mainbody.object_id ";
										sql+=" and  per_mainbody.plan_id="+plan_id+" and per_mainbody.body_id=-1 and per_mainbody.object_id='"+object+"' ";
									}				
								}
								else
								{ 
									sql="select per_object.a0101 as objname,Usra01.a0101 as mainname,per_plan.name,usra01."+mailOrMessage+" as address from per_object,UsrA01,per_plan ";
									sql+=" where  Usra01.a0100='"+mainbody+"'  and per_object.plan_id=per_plan.plan_id ";
									sql+=" and per_object.object_id='"+object+"' and per_object.plan_id="+plan_id+" ";
								}
								
								this.frowset=dao.search(sql);
								String address_str="";
								String a0101="";
								if(this.frowset.next())
								{
									/*
									content=content.replaceAll("\\(~考核主体名称~\\)",this.frowset.getString("mainname"));
									content=content.replaceAll("\\(~考核对象名称~\\)",this.frowset.getString("objname"));
									content=content.replaceAll("\\(~考核计划名称~\\)",this.frowset.getString("name"));
									content=content.replaceAll("\\(~系统时间~\\)",sysTime); */
								 
									addresStr=addresStr.replaceAll("#","＃");
									addresStr=addresStr.replaceAll("＃报批人名称＃", this.userView.getUserFullName());
									addresStr=addresStr.replaceAll("＃发件人名称＃", this.userView.getUserFullName());
									addresStr=addresStr.replaceAll("＃审批人名称＃", this.frowset.getString("mainname"));
									addresStr=addresStr.replaceAll("＃目标对象名称＃", this.frowset.getString("objname"));
									addresStr=addresStr.replaceAll("＃目标计划名称＃",this.frowset.getString("name"));
									addresStr=addresStr.replaceAll("＃系统时间＃",sysTime);
									if("1".equals(flag))
									{
										addresStr=addresStr.replaceAll(" ", ""); 
										addresStr=addresStr.replaceAll("\\r","");
										addresStr=addresStr.replaceAll("\\n","");
										addresStr=addresStr.replaceAll("\\r\\n","");
									}
									else
									{
										addresStr=addresStr.replaceAll(" ", "&nbsp;&nbsp;");
										addresStr=addresStr.replaceAll("\r\n", "<br>");
									}
									
									address_str=this.frowset.getString("address")!=null?this.frowset.getString("address"):"";
									a0101=this.frowset.getString("mainname");
								}
								if(address_str!=null && address_str.trim().length()>0)
								{					
									LazyDynaBean dyvo=new LazyDynaBean();
									if("1".equals(flag))
									{
										dyvo.set("sender",this.userView.getUserFullName());
										dyvo.set("receiver",a0101);
										dyvo.set("phone_num",address_str);
										dyvo.set("msg",addresStr);
										list.add(dyvo);
									}
									else
									{
										dyvo.set("title",title);
										dyvo.set("a0100",mainbody);
										dyvo.set("content",addresStr);
										dyvo.set("email",address_str);
										list.add(dyvo);
									}					
								}								
							}
						}
					}
				}
				else
				{				
					if(to_a0100.equals(object_id))
					{						
						if(vo.getInt("object_type")==2)
						{
							sql="select per_object.a0101 as objname,per_object.a0101 as mainname,per_plan.name,usra01."+mailOrMessage+" as address from per_object,UsrA01,per_plan where Usra01.a0100=per_object.object_id and per_object.plan_id=per_plan.plan_id ";
							sql+=" and  per_object.plan_id="+plan_id+" and per_object.object_id='"+object_id+"' ";
						}
						else
						{						
							sql="select per_object.a0101 as objname,per_mainbody.a0101 as mainname,per_plan.name,usra01."+mailOrMessage+" as address from per_object,per_mainbody,UsrA01,per_plan where Usra01.a0100=per_mainbody.mainbody_id and per_mainbody.plan_id=per_plan.plan_id ";
							sql+=" and  per_object.plan_id="+plan_id+" and per_object.object_id=per_mainbody.object_id ";
							sql+=" and  per_mainbody.plan_id="+plan_id+" and per_mainbody.body_id=-1 and per_mainbody.object_id='"+object_id+"' ";
						}				
					}
					else
					{
						/*
						sql="select per_object.a0101 as objname,pmb.a0101 as mainname,per_plan.name,usra01."+str+" as address from per_object,per_mainbody pmb,UsrA01,per_plan ";
						sql+="  where pmb.object_id=per_object.object_id and Usra01.a0100=pmb.mainbody_id  and pmb.plan_id=per_plan.plan_id  ";
						sql+="  and pmb.mainbody_id='"+to_a0100+"' and pmb.object_id='"+object_id+"' and pmb.plan_id="+plan_id;
						*/ 
						sql="select per_object.a0101 as objname,Usra01.a0101 as mainname,per_plan.name,usra01."+mailOrMessage+" as address from per_object,UsrA01,per_plan ";
						sql+=" where  Usra01.a0100='"+to_a0100+"'  and per_object.plan_id=per_plan.plan_id ";
						sql+=" and per_object.object_id='"+object_id+"' and per_object.plan_id="+plan_id+" ";
					}
					
					this.frowset=dao.search(sql);
					String address_str="";
					String a0101="";
					if(this.frowset.next())
					{
						/*
						content=content.replaceAll("\\(~考核主体名称~\\)",this.frowset.getString("mainname"));
						content=content.replaceAll("\\(~考核对象名称~\\)",this.frowset.getString("objname"));
						content=content.replaceAll("\\(~考核计划名称~\\)",this.frowset.getString("name"));
						content=content.replaceAll("\\(~系统时间~\\)",sysTime); */
					 
						content=content.replaceAll("#","＃");
						content=content.replaceAll("＃报批人名称＃", this.userView.getUserFullName());
						content=content.replaceAll("＃发件人名称＃", this.userView.getUserFullName());
						content=content.replaceAll("＃审批人名称＃", this.frowset.getString("mainname"));
						content=content.replaceAll("＃目标对象名称＃", this.frowset.getString("objname"));
						content=content.replaceAll("＃目标计划名称＃",this.frowset.getString("name"));
						content=content.replaceAll("＃系统时间＃",sysTime);
						if("1".equals(flag))
						{
							content=content.replaceAll(" ", ""); 
							content=content.replaceAll("\\r","");
							content=content.replaceAll("\\n","");
							content=content.replaceAll("\\r\\n","");
						}
						else
						{
							content=content.replaceAll(" ", "&nbsp;&nbsp;");
							content=content.replaceAll("\r\n", "<br>");
						}
						
						address_str=this.frowset.getString("address")!=null?this.frowset.getString("address"):"";
						a0101=this.frowset.getString("mainname");
					}
					if(address_str!=null && address_str.trim().length()>0)
					{					
						LazyDynaBean dyvo=new LazyDynaBean();
						if("1".equals(flag))
						{
							dyvo.set("sender",this.userView.getUserFullName());
							dyvo.set("receiver",a0101);
							dyvo.set("phone_num",address_str);
							dyvo.set("msg",content);
							list.add(dyvo);
						}
						else
						{
							dyvo.set("title",title);
							dyvo.set("a0100",to_a0100);
							dyvo.set("content",content);
							dyvo.set("email",address_str);
							list.add(dyvo);
						}					
					}
				}				
			}
			else if("2".equals(opt))
			{
				
				MarkStatusBo markStatusBo=new MarkStatusBo(this.getFrameconn(),this.userView);
				StringBuffer sql=new StringBuffer("");
				
				sql.append("select distinct pm.A0101,UsrA01."+mailOrMessage+" as address,per_plan.name ,pm.status,mainbody_id from per_mainbody pm left join UsrA01 ");
				sql.append(" on pm.mainbody_id=Usra01.a0100 left join per_plan on pm.plan_id=per_plan.plan_id where pm.object_id in(select object_id from per_object po where plan_id="+plan_id+"   ");
				sql.append(markStatusBo.getUserViewPrivWhere(userView));   // 考核主体根据考核对象的范围来限制 
				sql.append(") and pm.plan_id="+plan_id+" ");
				if(!"0".equals(departid))
				{ 
					sql.append(" and pm.e0122 like '"+departid+"%'"); 
				}
				if(name.trim().length()>0)
				{ 
					sql.append(" and pm.A0101 like '%"+name.trim()+"%'"); 
				}
				if("0".equalsIgnoreCase(isAll))
				{
					String[] values=to_a0100.split(",");
					StringBuffer _str=new StringBuffer("");
					for(int i=0;i<values.length;i++)
					{
						if(values[i]!=null&&values[i].trim().length()>0)
							_str.append(",'"+PubFunc.decrypt(values[i].trim())+"'"); // 加解密问题 lium
					}
					sql.append(" and pm.mainbody_id in ("+_str.substring(1)+")");
				}
				
				this.frowset=dao.search(sql.toString());
				String address_str="";
				String a0101="";
				while(this.frowset.next())
				{
					String _content=content;
			/*		_content=_content.replaceAll("\\(~考核主体名称~\\)",this.frowset.getString("A0101")); 
					_content=_content.replaceAll("\\(~考核计划名称~\\)",this.frowset.getString("name"));
					_content=_content.replaceAll("\\(~系统时间~\\)",sysTime); 
					*/
					_content=_content.replaceAll("#","＃");
			//		_content=_content.replaceAll("＃报批人名称＃", this.userView.getUserFullName());
					_content=_content.replaceAll("＃发件人名称＃", this.userView.getUserFullName());
			//		_content=_content.replaceAll("＃审批人名称＃", this.frowset.getString("mainname"));
			//		_content=_content.replaceAll("＃目标对象名称＃", this.frowset.getString("objname"));
			//		_content=_content.replaceAll("＃目标计划名称＃",this.frowset.getString("name"));
					
					_content=_content.replaceAll("＃考核主体名称＃",this.frowset.getString("A0101")); 
					_content=_content.replaceAll("＃考核计划名称＃",this.frowset.getString("name"));
					_content=_content.replaceAll("＃系统时间＃",sysTime);
										
					if("1".equals(flag))
					{
						_content=_content.replaceAll(" ", ""); 
						_content=_content.replaceAll("\\r","");
						_content=_content.replaceAll("\\n","");
						_content=_content.replaceAll("\\r\\n","");
					}
					else
					{
						_content=_content.replaceAll(" ", "&nbsp;&nbsp;");
						_content=_content.replaceAll("\r\n", "<br>");
					}					
					address_str=this.frowset.getString("address")!=null?this.frowset.getString("address"):"";
					a0101=this.frowset.getString("A0101");				
					
					
					//这里判断状态
					String status=this.frowset.getString("status");	
					
					if(address_str!=null && address_str.trim().length()>0)
					{						
						LazyDynaBean dyvo=new LazyDynaBean();
						if("1".equals(flag))
						{
							dyvo.set("sender",this.userView.getUserFullName());
							dyvo.set("receiver",a0101);
							dyvo.set("phone_num",address_str);
							dyvo.set("msg",_content);
							list.add(dyvo);
						}
						else
						{
							dyvo.set("title",title);
							dyvo.set("a0100",this.frowset.getString("mainbody_id"));
							dyvo.set("content",_content);
							dyvo.set("email",address_str);
							dyvo.set("status",status);
							list.add(dyvo);
						}						
					}
				}
				
			}else if("3".equals(opt))
			{
				
				StringBuffer sql=new StringBuffer("");
				
				LoadXml parameter_content = null;
    	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
				{
						
    	         	parameter_content = new LoadXml(this.getFrameconn(),plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
				}
				Hashtable params = parameter_content.getDegreeWhole();
				String SpByBodySeq="False";
    			if(params.get("SpByBodySeq")!=null)
    				SpByBodySeq=(String)params.get("SpByBodySeq");
				sql.append("select po.a0101 as objname,pm.a0101 as mainname,pm.mainbody_id as mainbody_id,pp.name,us."+ mailOrMessage +" as address ");
				sql.append(" from per_object po,per_mainbody pm,UsrA01 us,per_plan pp ");
				sql.append(" where us.a0100=pm.mainbody_id and pm.plan_id=pp.plan_id ");
				sql.append(" and po.plan_id="+ plan_id +" and po.object_id=pm.object_id and pm.plan_id="+ plan_id +" ");
				sql.append(" and pm.object_id = '" + object_id + "' ");
/*				
				sql.append("select distinct pm.A0101,UsrA01."+str+" as address,per_plan.name from per_mainbody pm left join UsrA01 ");
				sql.append(" on pm.mainbody_id=Usra01.a0100 left join per_plan on pm.plan_id=per_plan.plan_id where pm.object_id in(select object_id from per_object po where plan_id="+plan_id+" ");
				sql.append(") and pm.plan_id="+plan_id+" ");
*/				
				if(to_a0100!=null && to_a0100.trim().length()>0)
				{
					String[] values=to_a0100.split(",");
					StringBuffer _str=new StringBuffer("");
					for(int i=0;i<values.length;i++)
					{
						if(values[i]!=null&&values[i].trim().length()>0)
							_str.append(",'"+values[i].trim()+"'");
					}
					sql.append(" and pm.mainbody_id in ("+_str.substring(1)+")");
				}
				if("true".equalsIgnoreCase(SpByBodySeq)){
					sql.append(" and pm.sp_seq is not null ");
				}
				sql.append(" order by po.object_id ");
				
				this.frowset=dao.search(sql.toString());
				String address_str="";
				String a0101="";
				while(this.frowset.next())
				{
					String _content=content;			
					_content=_content.replaceAll("#","＃");
					_content=_content.replaceAll("＃发件人名称＃", this.userView.getUserFullName());			
					_content=_content.replaceAll("＃考核对象名称＃",this.frowset.getString("objname")); 					
					_content=_content.replaceAll("＃考核主体名称＃",this.frowset.getString("mainname")); 
					_content=_content.replaceAll("＃考核计划名称＃",this.frowset.getString("name"));
					_content=_content.replaceAll("＃系统时间＃",sysTime);										
					if("1".equals(flag))
					{
						_content=_content.replaceAll(" ", ""); 
						_content=_content.replaceAll("\\r","");
						_content=_content.replaceAll("\\n","");
						_content=_content.replaceAll("\\r\\n","");
					}
					else
					{
						_content=_content.replaceAll(" ", "&nbsp;&nbsp;");
						_content=_content.replaceAll("\r\n", "<br>");
					}					
					address_str=this.frowset.getString("address")!=null?this.frowset.getString("address"):"";
					a0101=this.frowset.getString("mainname");	
					
//					if(address_str!=null && address_str.trim().length()>0)
					{												
						if("1".equals(flag))
						{
							if(address_str!=null && address_str.trim().length()>0)
							{
								LazyDynaBean dyvo=new LazyDynaBean();
								dyvo.set("sender",this.userView.getUserFullName());
								dyvo.set("receiver",a0101);
								dyvo.set("phone_num",address_str);
								dyvo.set("msg",_content);
								list.add(dyvo);
							}
						}
						else
						{
							LazyDynaBean dyvo=new LazyDynaBean();
							dyvo.set("title",title);
							dyvo.set("a0100",this.frowset.getString("mainbody_id"));
							dyvo.set("content",_content);
							dyvo.set("email",address_str);
							dyvo.set("mainbody_id",this.frowset.getString("mainbody_id")); 	
							dyvo.set("mainbodyName",this.frowset.getString("mainname"));
							list.add(dyvo);
						}						
					}
				}
				
			}else if("4".equals(opt))
			{		
				HashMap objMainbodyMap = new HashMap();
				HashMap amap = new HashMap();
				
				if(objMainbodys!=null && objMainbodys.trim().length()>0)
				{
					objMainbodys = objMainbodys.substring(1);
					String[] matters = objMainbodys.split("&");
					for (int i = 0; i < matters.length; i++)
					{						
					    String ma = matters[i];
					    if(ma!=null && ma.trim().length()>0)
						{
					    	String[] mat = ma.split("`");	
					    	amap = new HashMap();
					    	if(mat.length>1)
					    	{					    	
						    	String str = mat[1];
						    	if(str!=null && str.trim().length()>0)
								{
						    		String[] matt = str.replaceAll("／", "/").split("/");	
						    		for (int k = 0; k < matt.length; k++)
									{
						    			String strS = matt[k];					    			
						    			if(amap.get(strS)==null)	
										{	    				   				
						    				amap.put(strS, "1");
										}
									}					    		
								}
							}
					    	if(objMainbodyMap.get(mat[0])==null)	
							{	    				   				
					    		objMainbodyMap.put(mat[0], amap);
							}					    						    	
						}					    
					}										
				}
				
				StringBuffer sql=new StringBuffer("");
				
				sql.append("select po.object_id as object_id,po.a0101 as objname,pm.mainbody_id as mainbody_id,pm.a0101 as mainname,pp.name,us."+mailOrMessage+" as address ");
				sql.append(" from per_object po,per_mainbody pm,UsrA01 us,per_plan pp ");
				sql.append(" where us.a0100=pm.mainbody_id and pm.plan_id=pp.plan_id ");
				sql.append(" and po.plan_id="+plan_id+" and po.object_id=pm.object_id ");
				sql.append(" and pm.plan_id="+plan_id+" ");												
				if(to_a0100!=null && to_a0100.trim().length()>0)
				{
					String[] values=to_a0100.split(",");
					StringBuffer _str=new StringBuffer("");
					for(int i=0;i<values.length;i++)
					{
						if(values[i]!=null&&values[i].trim().length()>0)
							_str.append(",'"+values[i].trim()+"'");
					}
					sql.append(" and po.object_id in ("+_str.substring(1)+")");
				}
				sql.append(" order by po.object_id ");
				
				this.frowset=dao.search(sql.toString());				
				
				String address_str="";
				String mainname="";
				while(this.frowset.next())
				{
					String objectId = (String)this.frowset.getString("object_id"); 	
					String mainbodyId = (String)this.frowset.getString("mainbody_id"); 	
					
					//  过滤已回顾的考核主体
					if(objMainbodyMap!=null && objMainbodyMap.size()>0)
					{												
						HashMap mainMap = (HashMap) objMainbodyMap.get(objectId); // 考核对象对应的考核主体map							
						if(mainMap!=null && mainMap.size()>0)
						{
							if(mainMap.get(mainbodyId)!=null)
								continue;															
						}						
					}					
					
					String _content=content;			
					_content=_content.replaceAll("#","＃");
					_content=_content.replaceAll("＃发件人名称＃", this.userView.getUserFullName());			
					_content=_content.replaceAll("＃考核对象名称＃",this.frowset.getString("objname")); 				
					_content=_content.replaceAll("＃考核主体名称＃",this.frowset.getString("mainname")); 
					_content=_content.replaceAll("＃考核计划名称＃",this.frowset.getString("name"));
					_content=_content.replaceAll("＃系统时间＃",sysTime);										
					if("1".equals(flag))
					{
						_content=_content.replaceAll(" ", ""); 
						_content=_content.replaceAll("\\r","");
						_content=_content.replaceAll("\\n","");
						_content=_content.replaceAll("\\r\\n","");
					}
					else
					{
						_content=_content.replaceAll(" ", "&nbsp;&nbsp;");
						_content=_content.replaceAll("\r\n", "<br>");
					}					
					address_str=this.frowset.getString("address")!=null?this.frowset.getString("address"):"";
					mainname=this.frowset.getString("mainname");
					
//					if(address_str!=null && address_str.trim().length()>0)
					{						
						if("1".equals(flag))
						{
							if(address_str!=null && address_str.trim().length()>0)
							{						
								LazyDynaBean dyvo=new LazyDynaBean();								
								dyvo.set("sender",this.userView.getUserFullName());
								dyvo.set("receiver",mainname);
								dyvo.set("phone_num",address_str);
								dyvo.set("msg",_content);
								list.add(dyvo);
							}
						}
						else
						{
							LazyDynaBean dyvo=new LazyDynaBean();
							dyvo.set("title",title);
							dyvo.set("a0100",this.frowset.getString("mainbody_id"));
							dyvo.set("content",_content);
							dyvo.set("email",address_str);							
							dyvo.set("mainbody_id",this.frowset.getString("mainbody_id")); 	
							dyvo.set("mainbodyName",this.frowset.getString("mainname")); 							
							list.add(dyvo);
						}						
					}
				}				
			}
//////////////////////////////////////绩效管理 填报状态  开始////////////////////////////////////////////////////////			
			else if("5".equals(opt)){

				PersonListShowBo pls = new PersonListShowBo(this.getFrameconn(),this.userView);
				list = pls.sendMessage(flag,mailOrMessage,plan_id,numberlist,strFrom,content,list,title);
			}
//////////////////////////////////////绩效管理 填报状态 结束////////////////////////////////////////////////////////			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 从系统邮件服务器设置中得到发送邮件的地址
	 * @return
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return "";
        try
        {
			Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}

}
