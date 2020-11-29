package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.attestation.zgpt.IMISPendProceed;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:向其它系统发代办接口</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 14, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class MessageToOtherSys {
	private Connection conn;
	private UserView userView;
	
	public MessageToOtherSys(Connection con,UserView userView)
	{
		this.conn=con;
		this.userView=userView;
	}
	
	
	/** ----------------  普天人事异动代办  --------------------- */
	/**
	 * 取得业务模板的内容
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	private RecordVo readTemplate(int tabid)throws GeneralException
	{
		
		return TemplateUtilBo.readTemplate(tabid,this.conn);
	}
	
	
//	待办信息在应用系统的唯一标识代号
	public String getPendingCode(String taskid)
	{
		return "HRMS-"+taskid;
	//	Date d=new Date();
	//	return  "HRMS-"+nbase+a0100+"-"+d.getTime()+Math.round(Math.ceil(Math.random()*10));

//		return "IMIS-003-"+d.getTime()+a0100+Math.round(Math.ceil(Math.random()*10));
	}
	
	
	
	
	public void sendDealWithInfo_gz(String pre_pendingID,int opt,String salaryid,String to_username)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			PendingTask imip=new PendingTask();
			String pendingType="工资审批"; 
			//将旧的代办信息置为已处理状态 
			if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
			{ 
				imip.updatePending("G",pre_pendingID,1,pendingType,this.userView);
			}
			if(opt!=0)
			{
				RecordVo vo=new RecordVo("operuser"); 
				vo.setString("username",to_username);
				vo=dao.findByPrimaryKey(vo);
				String nbase=vo.getString("nbase");
				String a0100=vo.getString("a0100");
				String password=vo.getString("password");
				if(a0100!=null&&nbase!=null&&a0100.trim().length()>0&&nbase.trim().length()>0)
				{
					vo=new RecordVo("salarytemplate");
					vo.setInt("salaryid",Integer.parseInt(salaryid));
					vo=dao.findByPrimaryKey(vo);
					String cname=vo.getString("cname"); 
					String url="/gz/gz_accounting/gz_sp_orgtree.do?b_query=link&ori=0&salaryid="+salaryid+"&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(to_username+","+password));
					imip.insertPending(null,"G",cname+"  待审批。",this.userView.getDbname()+this.userView.getA0100(),nbase+a0100,
							url,0,1,pendingType,this.userView);
				}
				
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public void copeYpTask(String pre_pendingID)
	{
		if(SystemConfig.getPropertyValue("clientName")!=null&& "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
		{
			IMISPendProceed imip=new IMISPendProceed();
			String pendingType="考核申诉";
			//将旧的代办信息置为已处理状态
			if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
			{
				imip.updatePendingsStateByUID(pre_pendingID,1,pendingType);
			}
		}
		else
		{
			PendingTask imip=new PendingTask();
			String pendingType="业务模板";
			//将旧的代办信息置为已处理状态 
			if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
			{ 
				imip.updatePending("T",pre_pendingID,1,pendingType,this.userView);
			}
		}
	}
	/**
	 * 给OA等第三方系统发送待办信息， 报批单据：bs_flag=1  报备单据：bs_flag=2（目前和邮件的url保持一致，如果更改，也许把邮件改掉）
	 * opt: 0不发新增待办 1：处理  新增标识opt=2 标识驳回 同类wf_node。
	 * @return
	 * @throws GeneralException
	 */
	public void sendDealWithInfo(String pre_pendingID,int opt,String tabid,int ins_id,String taskid,String nbase,String a0100,String bs_flag)
	{
		try
		{
			int pendingStatus=0;//待办标记 
			if ("2".equals(bs_flag))//报备 发通知消息
            {
                pendingStatus=2;
            }
			if(SystemConfig.getPropertyValue("clientName")!=null&& "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				IMISPendProceed imip=new IMISPendProceed();
				String pendingType="考核申诉";
				//将旧的代办信息置为已处理状态
				if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
				{
					imip.updatePendingsStateByUID(pre_pendingID,1,pendingType);
				}
				
				
				if(opt!=0)
				{
				//	RecordVo vo=readTemplate(Integer.parseInt(tabid));
					String pendingCode=getPendingCode(taskid);
					
					
					//在待办任务中判断模板设置的展现方式，以默认显示 20150923 liuzy
	                int tab_id=Integer.parseInt(tabid);
	                TemplateUtilBo tb=new TemplateUtilBo(this.conn,this.userView);
	                String view = tb.getTemplateView(tab_id);
	                String url="";
	            	if(view!=null&& "list".equalsIgnoreCase(view)){
	            		taskid=PubFunc.decrypt(taskid);
	             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag="+bs_flag+"&ins_id="+ins_id+"&returnflag=3&task_id="+taskid+"&tabid="+tabid+"&index_template=1&pre_pendingID="+pendingCode+"&appfwd=1&etoken="+getEtoken_str(a0100,nbase)+"";
	            	    taskid=PubFunc.encrypt(taskid);
	            	}else {
	            		url="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+taskid+"&sp_flag="+bs_flag+"&returnflag=3&pre_pendingID="+pendingCode+"&appfwd=1&etoken="+getEtoken_str(a0100,nbase)+"";
	            	}
					
					//String url="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+taskid+"&sp_flag="+bs_flag+"&returnflag=3&pre_pendingID="+pendingCode+"&appfwd=1&etoken="+getEtoken_str(a0100,nbase)+"";
					imip.insertPending(pendingCode,getTopic(PubFunc.decrypt(taskid),opt),this.userView.getDbname()+this.userView.getA0100(),nbase+a0100,
							url,0,1,pendingType);
					
				}
			}
			else
			{
				 
				PendingTask imip=new PendingTask();
				String pendingType="业务模板";
			//	RecordVo vo=readTemplate(Integer.parseInt(tabid));  
				//将旧的代办信息置为已处理状态 
				if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
				{ 
					imip.updatePending("T",pre_pendingID,1,pendingType,this.userView);
				}
				if(opt!=0)
				{ 
					String businessModel="0";
					if ("2".equals(bs_flag)) {
                        businessModel="61" ;//报备。
                    }
					
					String pendingCode=getPendingCode(taskid); 
					
					//在待办任务中判断模板设置的展现方式，以默认显示 20150923 liuzy
	                int tab_id=Integer.parseInt(tabid);
	                TemplateUtilBo tb=new TemplateUtilBo(this.conn,this.userView);
	                String view = tb.getTemplateView(tab_id);
	                String url="";
	            	if(view!=null&& "list".equalsIgnoreCase(view)){
	            		taskid=PubFunc.decrypt(taskid);
	             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+ins_id+"&returnflag=3&task_id="+taskid+"&tabid="+tabid+"&index_template=1&pre_pendingID="+pendingCode;
	             		taskid=PubFunc.encrypt(taskid);
	            	}else {
	            		url="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+taskid+"&businessModel="+businessModel+"&sp_flag=1&returnflag=3&pre_pendingID="+pendingCode;
	            	}
	            	
	                if (PubFunc.isUseNewPrograme(this.userView)){
	                    String approve_flag="1";
	                    if ("2".equals(bs_flag)){
	                        approve_flag="3";
	                    }
	                    String newUrl ="/module/template/templatemain/templatemain.html?b_query=link&task_id="
	                        +taskid+"&tab_id="+tabid+"&return_flag=13"
	                        +"&approve_flag="+approve_flag
	                        +"&pre_pendingID="+pendingCode;
	                    url="/module/utils/jsp.do?br_query=link&param=" +SafeCode.encode(newUrl)  ;
	                }
					
					//String url="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+taskid+"&businessModel="+businessModel+"&sp_flag=1&returnflag=3&pre_pendingID="+pendingCode;
					imip.insertPending(pendingCode,"T",getTopic(PubFunc.decrypt(taskid),opt),this.userView.getDbname()+this.userView.getA0100(),nbase+a0100,
							url,pendingStatus,1,pendingType,this.userView);
					
				}
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 给OA等第三方系统发送待办信息，报批单据 bs_flag=1
	 * @return
	 * @throws GeneralException
	 */
	public void sendDealWithInfo(String pre_pendingID,int opt,String tabid,int ins_id,String taskid,String nbase,String a0100)
	{
		try
		{
			sendDealWithInfo(pre_pendingID,opt,tabid,ins_id,taskid,nbase,a0100,"1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	
	
	/**
	 * 获得模板待办信息名称
	 * @param taskid
	 * @param opt 2:驳回
	 * @author dengcan
	 * @return
	 */
	public String getTopic(String taskid,int opt)
	{
		String topic="";
		try
		{
			String start_date="";
			String state="";
			String bs_flag="";
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select twt.task_topic,twt.start_date,twt.State,twt.bs_flag  from t_wf_task twt,t_wf_instance twi  where twt.ins_id=twi.ins_id and   twt.task_id=?";
			ArrayList valueList=new ArrayList();
			valueList.add(taskid);
			RowSet rowSet=dao.search(sql,valueList);
			if(rowSet.next())
			{
			
				Date startDate=rowSet.getDate("start_date");
				if(Sql_switcher.searchDbServer()==2)
				{//oracle 取不到时分秒
					Timestamp ta=rowSet.getTimestamp("start_date");
					if(ta!=null)
					{
						startDate=new Date(ta.getTime());
					}
				}
				
				topic=rowSet.getString("task_topic");
				start_date=df.format(startDate);
				state=rowSet.getString("state");
				bs_flag=rowSet.getString("bs_flag");
			}
			
			int index=topic.indexOf(",共");
			String applyStr="已申请,请审批。";
			if (opt==2){//驳回的单据 t_wf_task 未保存07标识
				applyStr="已驳回，请查阅。";
			}
			if ("3".equals(bs_flag)){
				applyStr="已批准，请知悉。";
			}
			if(index!=-1)
			{
				String temp=topic.substring(0,index);
				String temp2=topic.substring(index);
				topic=temp+"  "+start_date+temp2+applyStr;
			}
			else {
                topic+=applyStr;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 
		return topic;
	}
	
	/**-----------------     end          ---------------------*/
	
	public String getEtoken_str(String a0100,String nbase)
	{
		LazyDynaBean _abean=getUserNamePassword(nbase+a0100);
		String username="";
		String password="";
		if(_abean!=null)
		{
			username=(String)_abean.get("username");
			password=(String)_abean.get("password");	
		}
		return PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
	}


	private LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		String nbase=value.substring(0,3);
		String a0100=value.substring(3);
		AttestationUtils utils=new AttestationUtils();
		LazyDynaBean fieldbean=utils.getUserNamePassField();
		String username_field=(String)fieldbean.get("name");
	    String password_field=(String)fieldbean.get("pass");
	    StringBuffer sql=new StringBuffer();
	    sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
	    sql.append(" where a0100='"+a0100+"'");
	    List rs=ExecuteSQL.executeMyQuery(sql.toString());
	    LazyDynaBean rec=null;
	    if(rs!=null&&rs.size()>0)
	    {
	    	rec=(LazyDynaBean)rs.get(0);	    	
	    }
	    return rec;
	}
	

}
