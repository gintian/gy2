package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Date;
/**
 * 编辑代理人员
 * <p>Title:EditAgentTrans.java</p>
 * <p>Description>:EditAgentTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 6, 2010 2:00:44 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class EditAgentTrans extends IBusiness {

	public void execute() throws GeneralException {
		String editflag=(String)this.getFormHM().get("editflag");
		

		if(editflag!=null&& "update".equals(editflag))
		{
			String id=(String)this.getFormHM().get("id");
			RecordVo vo=new RecordVo("agent_set");
			vo.setInt("id", Integer.parseInt(id));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try {
				vo=dao.findByPrimaryKey(vo);
				Date statr_d=vo.getDate("start_date");
				Date end_d=vo.getDate("end_date");
				String agent_id=vo.getString("agent_id");
				String agent_status=vo.getString("agent_status");
				int status=this.userView.getStatus();
				this.getFormHM().put("start_date", DateUtils.format(statr_d,"yyyy-MM-dd"));
				this.getFormHM().put("end_date",DateUtils.format(end_d,"yyyy-MM-dd"));
				this.getFormHM().put("agent_id", agent_id);
				this.getFormHM().put("agent_status", agent_status);
				this.getFormHM().put("id", id);
				if(agent_status!=null&& "4".equals(agent_status))
				{
					agent_id=vo.getString("nbase")+vo.getString("a0100");
				}
				this.getFormHM().put("agent_fullname",getAgent_fullname(agent_id,agent_status));
				this.getFormHM().put("a0100",vo.getString("a0100"));
				this.getFormHM().put("nbase",vo.getString("nbase"));
				this.getFormHM().put("agent_fullname",getAgent_fullname(agent_id,status+""));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		{
			this.getFormHM().put("startr_date", "");
			this.getFormHM().put("end_date","");
			this.getFormHM().put("agent_id", "");
			this.getFormHM().put("agent_fullname","");
		}
		String principal_id=this.userView.getDbname()+this.userView.getA0100();
		this.getFormHM().put("principal_id", principal_id);
	}
	/**
	 * 得到代理人名称
	 * @param agent_id
	 * @param status
	 * @return
	 */
    private String getAgent_fullname(String agent_id,String status)
    {
    	String agent_fullname="";
    	if(status!=null&& "4".equals(status))
    	{
    		String nbase=agent_id.substring(0,3);
    		String a0100=agent_id.substring(3);
    		String sql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		try {
				this.frowset=dao.search(sql);
				if(this.frowset.next())
					agent_fullname=this.frowset.getString("a0101");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    	}
    	return agent_fullname;
    }
}
