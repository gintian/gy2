package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveAgentTrans extends IBusiness {

	public void execute() throws GeneralException {
		String editflag=(String)this.getFormHM().get("editflag");
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");		
		String agent_id=(String)this.getFormHM().get("agent_id");
		String agent_status=(String)this.getFormHM().get("agent_status");//代理人状态业务用户||账号用户
		String agent_agentname = ""; //代理人姓名初始化	author:zangxj  day:2014-06-10
		if(agent_status==null||agent_status.length()<=0)
			agent_status="4";
		if(start_date==null||start_date.length()<=0)
			 throw GeneralExceptionHandler.Handle(new GeneralException("","有效日期起不能为空","",""));
		if(end_date==null||end_date.length()<=0)
			 throw GeneralExceptionHandler.Handle(new GeneralException("","有效日期止不能为空","",""));
		start_date=start_date.replaceAll("\\.","-");
		end_date=end_date.replaceAll("\\.","-");
	  	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  	java.util.Calendar c1=java.util.Calendar.getInstance();
	  	java.util.Calendar c2_server=java.util.Calendar.getInstance();
	  	try
	  	{
	  		 c1.setTime(formatter.parse(start_date));
	  		 c2_server.setTime(formatter.parse(end_date));
	  		 
	  	 }catch(Exception e)
	  	 {
	  		 throw GeneralExceptionHandler.Handle(new GeneralException("","日期时间类型不对!","",""));
	  	 }
	  	 int result=c1.compareTo(c2_server);
	  	 if(result>0)
	  	 {
	  		  throw GeneralExceptionHandler.Handle(new GeneralException("","有效日期起日期不能大于有效日期止日期!","","")); 
	  	 }
	  	ContentDAO dao=new ContentDAO(this.getFrameconn());
	  	RecordVo vo=new RecordVo("agent_set");
	  	vo.setDate("start_date", start_date);
	  	vo.setDate("end_date", end_date);
	  	if("4".equals(agent_status))
	  	{
	  		String a0100=(String)this.getFormHM().get("a0100");
	  		String nbase=(String)this.getFormHM().get("nbase");
	  		DbNameBo dbNameBo=new DbNameBo(this.getFrameconn(),this.userView);
	  		String usernamefieldcolumn=dbNameBo.getLogonUserNameField();	  		
	  		String sql="select "+usernamefieldcolumn+" as username ,a0101 from "+nbase+"a01 where a0100='"+a0100+"'";
	  		try {
				this.frowset=dao.search(sql);
				if(this.frowset.next())
		  		{
					agent_id=this.frowset.getString("username");
					agent_agentname=this.frowset.getString("a0101");
					agent_id=agent_id!=null&&agent_id.length()>0?agent_id:"";
		  		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			vo.setString("agent_id", agent_id);
			vo.setString("nbase", nbase);
			vo.setString("a0100", a0100);
	  	}else
	  	{
	  		vo.setString("agent_id", agent_id);
	  	}	  	
	  	
	  	String save_result="ok";
	  	try
	  	{
	  		if(editflag!=null&& "update".equals(editflag))
			{
				String id=(String)this.getFormHM().get("id");
				vo.setInt("id", Integer.parseInt(id));				
				dao.updateValueObject(vo);
				//编辑代理人日志 author:zangxj  day:2014-06-07
				this.getFormHM().put("@eventlog",ResourceFactory.getProperty("agent.proxy.edit")+"("+agent_agentname+")," +
						ResourceFactory.getProperty("agent.proxy.validperiod")+start_date+ResourceFactory.getProperty("label.to")+end_date);

			}else
			{
				int id=getId();
				String principal_id=this.userView.getDbname()+this.userView.getA0100();
				vo.setString("principal_id", principal_id);
				vo.setString("principal_fullname", this.userView.getUserFullName());
				vo.setInt("agent_status", this.userView.getStatus());//this.userView.getStatus()
				vo.setInt("principal_status", this.userView.getStatus());//this.userView.getStatus()
				vo.setInt("id", id);
				dao.addValueObject(vo);

				Date statr_d=vo.getDate("start_date");
				Date end_d=vo.getDate("end_date");
				String str = agent_id;
				//添加代理人日志 author:zangxj  day:2014-06-07
				this.getFormHM().put("@eventlog",ResourceFactory.getProperty("agent.proxy.add")+"("+agent_agentname+")," +
						ResourceFactory.getProperty("agent.proxy.validperiod")+DateUtils.format(statr_d,"yyyy-MM-dd")+ResourceFactory.getProperty("label.to")+DateUtils.format(end_d,"yyyy-MM-dd"));
				
			}
	  	}catch(Exception e)
	  	{
	  		 save_result="fail"; 
	  		 e.printStackTrace();
	  		 throw GeneralExceptionHandler.Handle(new GeneralException("","保存失败!","","")); 	  		
	  	}
		this.getFormHM().put("save_result", save_result);
	}
    private synchronized int getId()
    {
    	String sql="select max(id)+1 id from agent_set";
    	int id=0;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				id=this.frowset.getInt("id");
				if(id==0)
					id=1;
			}else
				id=1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return id;
    }
    
}
