package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 
 *  
 * <p>Title:SendYdNodeWarnJob.java</p>
 * <p>Description>:人事异动节点消息通知</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 12, 2011 9:59:50 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author:dengc
 */
public class SendYdNodeWarnJob implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		
		Connection conn=null;
		RowSet rs =null;
		try {
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			
			
			String password = "";
			rs = dao.search("select Password from operuser where UserName='su'"); 
			if(rs.next()){
				password = rs.getString("Password");
				password=password!=null?password:""; 
			}
			UserView uv = new UserView("su",password,conn);
			if(uv!=null){
				//解决获取代办连接是旧链接问题 给userview赋值锁版本。
				String lock=SystemConfig.getPropertyValue("lock_version");
				if(StringUtils.isNotBlank(lock)){
					uv.setVersion(Integer.parseInt(lock));
				}
				else {
					throw new Exception("请在system.properties中配置lock_version属性");
				}
			}
			String hrp_logon_url=SystemConfig.getPropertyValue("hrp_logon_url");
			if(hrp_logon_url!=null&&hrp_logon_url.trim().length()>0&&uv.getServerurl()!=null&&uv.getServerurl().length()==0) {
				uv.setServerurl(hrp_logon_url);
			}
			HashMap nodeInfoMap=getNodeInfoMap(dao);
			HashMap tabBoMap=new HashMap();
			if(nodeInfoMap.size()>0)
			{
				rs=dao.search("select ins_id,task_id,task_topic,t_wf_task.node_id,actorid,actor_type,actorname,start_date,t_wf_node.tabid from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id and task_state='3' and bs_flag='1' and task_topic not like '%,共0人%' ");//bug 33690 执行后台作业 自动发邮件催办没有过滤报备的单子
				TemplateTableBo tablebo=null;
				while(rs.next())
				{
					String node_id=rs.getString("node_id");
					String tabid=rs.getString("tabid");
					int task_id=rs.getInt("task_id");
					if(nodeInfoMap.get(node_id)!=null)
					{
						LazyDynaBean nodeTimeBean=(LazyDynaBean)nodeInfoMap.get(node_id);
						Date startDate=rs.getDate("start_date");
						int valid=validateStartWar(startDate,(String)nodeTimeBean.get("timeValue"),(String)nodeTimeBean.get("timeValue_befor"));  // 1:提前预警 2：超时预警
						if(valid!=0)
						{
							if(tabBoMap.get(tabid)==null)
							{
								tablebo=new TemplateTableBo(conn,Integer.parseInt(tabid),uv);
								tabBoMap.put(tabid,tablebo);
							}
							else {
								tablebo=(TemplateTableBo)tabBoMap.get(tabid);
							}
						 
							RecordVo ins_vo=new RecordVo("t_wf_instance");
							ins_vo.setInt("ins_id",rs.getInt("ins_id"));
							ins_vo=dao.findByPrimaryKey(ins_vo);
							
							if(valid==1)
							{
								String email=nodeTimeBean.get("email")==null?"":(String)nodeTimeBean.get("email");
								String mobile=nodeTimeBean.get("mobile")==null?"":(String)nodeTimeBean.get("mobile");
								String template_sp=nodeTimeBean.get("template_sp")==null?"":(String)nodeTimeBean.get("template_sp");
								String template_staff=nodeTimeBean.get("template_staff")==null?"":(String)nodeTimeBean.get("template_staff");
								String email_staff=nodeTimeBean.get("email_staff")==null?"":(String)nodeTimeBean.get("email_staff");
								
									if("true".equalsIgnoreCase(email)) {
										tablebo.setBemail(true);
									} else {
										tablebo.setBemail(false);
									}
									if("true".equalsIgnoreCase(mobile)) {
										tablebo.setBsms(true);
									} else {
										tablebo.setBsms(false);
									}
									if(template_sp!=null&&template_sp.trim().length()>0)
									{
										tablebo.setTemplate_sp(template_sp);
									}
									else
									{
										tablebo.setTemplate_sp("");
									}
									
									if(email_staff!=null&& "true".equalsIgnoreCase(email_staff))
									{
										tablebo.setEmail_staff(true);
										
										if(template_staff!=null&&template_staff.trim().length()>0)
										{
											tablebo.setTemplate_staff(template_staff); 
										}
										else
										{
											tablebo.setTemplate_staff("");
										} 
									}
									else
									{ 
										tablebo.setEmail_staff(false);
										tablebo.setTemplate_staff("");
									}
									
								
								
								
								WF_Node wf_node=new WF_Node(Integer.parseInt(node_id),conn,tablebo);
								wf_node.setNodeParam(String.valueOf(tablebo.getTabid()),task_id,"templet_"+tablebo.getTabid(),tablebo); 
								StringBuffer sql=new StringBuffer("");
								sql.append("select templet_"+tabid+".* from t_wf_task_objlink td,templet_"+tabid+" where templet_"+tabid+".seqnum=td.seqnum ");
								sql.append(" and td.task_id="+task_id); 
								wf_node.setObjs_sql(sql.toString());
								wf_node.setOpt("1");
								 
								wf_node.setIns_vo(ins_vo);
								WF_Actor actor=new WF_Actor();
								actor.setActorid(rs.getString("actorid"));
								actor.setActorname(rs.getString("actorname"));
								actor.setActortype(rs.getString("actor_type"));
								String isSendMessage="0";
								if("true".equals(nodeTimeBean.get("email"))&&"true".equals(nodeTimeBean.get("mobile"))) {
									isSendMessage="3";
								} else if("true".equals(nodeTimeBean.get("email"))) {
									isSendMessage="1";
								} else if("true".equals(nodeTimeBean.get("mobile"))) {
									isSendMessage="2";
								}
								wf_node.setIsSendMessage(isSendMessage);
								if("true".equals(email_staff)) {
									wf_node.setEmail_staff_value("1");
								}
								wf_node.setSendresource("1");
								//发送催办邮件，邮件标题中没有具体人员名称
								RecordVo task_vo=new RecordVo("t_wf_task");
								task_vo.setInt("task_id",task_id);
								task_vo=dao.findByPrimaryKey(task_vo);
								wf_node.setTask_vo(task_vo);
								wf_node.sendMessage(actor,task_id,Integer.parseInt(node_id));//发短信及邮件
							}
							else if(valid==2)
							{
								String email=nodeTimeBean.get("email")==null?"":(String)nodeTimeBean.get("email");
								String mobile=nodeTimeBean.get("mobile")==null?"":(String)nodeTimeBean.get("mobile");
								String template_sp=nodeTimeBean.get("template_sp")==null?"":(String)nodeTimeBean.get("template_sp");
								String template_staff=nodeTimeBean.get("template_staff")==null?"":(String)nodeTimeBean.get("template_staff");
								String email_staff=nodeTimeBean.get("email_staff")==null?"":(String)nodeTimeBean.get("email_staff");
								
								if(tabBoMap.get(tabid+"_1")==null)
								{
									tablebo=new TemplateTableBo(conn,Integer.parseInt(tabid),uv);
									if("true".equalsIgnoreCase(email)) {
										tablebo.setBemail(true);
									} else {
										tablebo.setBemail(false);
									}
									if("true".equalsIgnoreCase(mobile)) {
										tablebo.setBsms(true);
									} else {
										tablebo.setBsms(false);
									}
									if(template_sp!=null&&template_sp.trim().length()>0)
									{
										tablebo.setTemplate_sp(template_sp);
									}
									else
									{
										tablebo.setTemplate_sp("");
									}
									
									if(email_staff!=null&& "true".equalsIgnoreCase(email_staff))
									{
										tablebo.setEmail_staff(true);
										
										if(template_staff!=null&&template_staff.trim().length()>0)
										{
											tablebo.setTemplate_staff(template_staff); 
										}
										else
										{
											tablebo.setTemplate_staff("");
										} 
									}
									else
									{ 
										tablebo.setEmail_staff(false);
										tablebo.setTemplate_staff("");
									}
									
									tabBoMap.put(tabid+"_1",tablebo);
								}
								else {
									tablebo=(TemplateTableBo)tabBoMap.get(tabid+"_1");
								}
								
								WF_Node wf_node=new WF_Node(Integer.parseInt(node_id),conn,tablebo);
								wf_node.setNodeParam(String.valueOf(tablebo.getTabid()),task_id,"templet_"+tablebo.getTabid(),tablebo); 
								StringBuffer sql=new StringBuffer("");
								sql.append("select templet_"+tabid+".* from t_wf_task_objlink td,templet_"+tabid+" where templet_"+tabid+".seqnum=td.seqnum ");
								sql.append(" and td.task_id="+task_id); 
								wf_node.setObjs_sql(sql.toString());
								wf_node.setOpt("1");
								String isSendMessage="0";
								if(tablebo.isBemail()&&tablebo.isBsms()) {
									isSendMessage="3";
								} else if(tablebo.isBemail()) {
									isSendMessage="1";
								} else if(tablebo.isBsms()) {
									isSendMessage="2";
								}
								wf_node.setIsSendMessage(isSendMessage);
								wf_node.setIns_vo(ins_vo);
								WF_Actor actor=new WF_Actor();
								actor.setActorid(rs.getString("actorid"));
								actor.setActorname(rs.getString("actorname"));
								actor.setActortype(rs.getString("actor_type")); 
								if(email_staff!=null&& "true".equalsIgnoreCase(email_staff)) {
									wf_node.setEmail_staff_value("1");
								} else {
									wf_node.setEmail_staff_value("0");
								}
								wf_node.setSendresource("1");
								RecordVo task_vo=new RecordVo("t_wf_task");
								task_vo.setInt("task_id",task_id);
								task_vo=dao.findByPrimaryKey(task_vo);
								wf_node.setTask_vo(task_vo);
								wf_node.sendMessage(actor,task_id,Integer.parseInt(node_id),ResourceFactory.getProperty("sendYdemail.Urge"));//发短信及邮件 
							}
						
						}
						
					}
				}
				
			}
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null) {
					rs.close();
				}
				if(conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	private int  validateStartWar(Date startDate,String timeValue,String wartimeValue )
	{
		int valid=0;  // 1:提前预警 2：超时预警
		String[] temps=timeValue.split("\\."); 
		long time=0;
		for(int i=0;i<temps.length;i++)
		{
			if(!"0".equals(temps[i]))
			{
				if(i==0)
				{
					time=Integer.parseInt(temps[i])*24*60*60;
				}
				else if(i==1)
				{
					time+=Integer.parseInt(temps[i])*60*60;
				}
				else if(i==2)
				{
					time+=Integer.parseInt(temps[i])*60;
				}
			}
		}
		
		long endTime=(startDate.getTime()/1000)+time; 
		long warnTime=endTime;
		if(wartimeValue!=null&&wartimeValue.trim().length()>0)
		{
			temps=wartimeValue.split("\\."); 
			time=0;
			for(int i=0;i<temps.length;i++)
			{
				if(!"0".equals(temps[i]))
				{
					if(i==0)
					{
						time=Integer.parseInt(temps[i])*24*60*60;
					}
					else if(i==1)
					{
						time+=Integer.parseInt(temps[i])*60*60;
					}
					else if(i==2)
					{
						time+=Integer.parseInt(temps[i])*60;
					}
				}
			}
			warnTime=endTime-time;
			
		}
		long today=(new Date()).getTime()/1000; 
		if(today>=warnTime&&today<=endTime) {
			valid=1;
		}
		if(today>endTime) {
			valid=2;
		}
	 
		return valid;
	}
	
	
	private HashMap getNodeInfoMap(ContentDAO dao)
	{
		HashMap nodeInfoMap=new HashMap();
		RowSet rs =null;
		try {
		
			rs=dao.search("select * from t_wf_node where node_id in (select node_id from t_wf_task where task_state='3' and bs_flag='1' and task_topic not like '%共0人%'  )");//bug 33690 执行后台作业 自动发邮件催办没有过滤报备的单子
			LazyDynaBean abean=null;
			Document doc=null;
			Element element=null;
			while(rs.next())
			{
				abean=new LazyDynaBean();
				abean.set("node_id",rs.getString("node_id"));
				String ext_param=Sql_switcher.readMemo(rs,"ext_param");
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					doc=PubFunc.generateDom(ext_param); 
					String xpath="/params/time_limit";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					
					List childlist=findPath.selectNodes(doc);
					if(childlist.size()==0){
						xpath="/param/time_limit";
						 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						 childlist=findPath.selectNodes(doc);
					}
					element = (Element)findPath.selectSingleNode(doc);
					if(element!=null&&element.getAttributeValue("valid")!=null&&"true".equals(element.getAttributeValue("valid"))){
					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							String valid=element.getAttributeValue("valid");
							if(valid!=null&& "true".equalsIgnoreCase(valid))
							{
								String timeValue=element.getAttributeValue("value");
								if(timeValue!=null&&!"0.0.0".equalsIgnoreCase(timeValue))
								{
									abean.set("timeValue",timeValue);
									
									xpath="/params/time_limit/warn";
									findPath = XPath.newInstance(xpath);// 取得符合条件的节点
									List childlist0=findPath.selectNodes(doc);
									if(childlist0.size()==0){
										xpath="/param/time_limit/warn";
										 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
										 childlist0=findPath.selectNodes(doc);
									}
									if(childlist0!=null&&childlist0.size()>0)
									{
										for(int j=0;j<childlist0.size();j++)
										{
											element=(Element)childlist0.get(j);
											String value=element.getAttributeValue("value");  //提前xxxx时间报警
											abean.set("timeValue_befor",value);
											valid=element.getAttributeValue("valid");
											if(valid!=null&& "true".equalsIgnoreCase(valid))
											{
												abean.set("overTimeValid", "true");
												abean.set("email", element.getAttributeValue("email"));
												abean.set("mobile",element.getAttributeValue("mobile"));
												
												abean.set("email_staff",element.getAttributeValue("email_staff"));  //抄送到本人
												abean.set("template_staff",element.getAttributeValue("template_staff")); //抄送到本人模板
												abean.set("template_sp",element.getAttributeValue("template_sp"));     //审批通知模板
											}
											else
											{
												abean.set("overTimeValid", "false");
												abean.set("email", "false");
												abean.set("mobile", "false");
												abean.set("email_staff", "false");
												abean.set("template_staff", "");
												abean.set("template_sp", "");
											
											}
											
										}
									}
								
									nodeInfoMap.put((String)abean.get("node_id"), abean);
								}
							}
						}
					}
				  }
					
				} 
			}
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null) {
					rs.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nodeInfoMap;
	}
	

	
}
