package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hjsj.hrms.businessobject.info.AgentsetUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
public class AgentTrans extends IBusiness {
	public void execute() throws GeneralException {
		String agentIds=(String)this.getFormHM().get("agentId");
		HttpSession session =(HttpSession)this.getFormHM().get("session");
		if(agentIds==null||agentIds.length()<=0)
			return;
		String agentIdArr[]=agentIds.split("`");
		String agentId=agentIdArr[0];
		String id=agentIdArr[1];
		String nbase=agentId.substring(0,3);
		String a0100=agentId.substring(3);
		DbNameBo DbNameBo=new DbNameBo(this.getFrameconn(),this.userView);
		String username=DbNameBo.getLogonUserNameField();
		String sql="select "+username+" as username from "+nbase+"A01 where a0100='"+a0100+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String logon_username="";
		StringBuffer agent_func_str=new StringBuffer();
		HashMap warnPrivMap = new HashMap();
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				logon_username=this.frowset.getString("username");
			//赋值功能编号
			if(logon_username==null||logon_username.length()<=0)
				return;
			this.userView.setFuncpriv(null);
			this.userView.reSetResourceMx(null, 7);
			this.userView.reSetResourceMx(null, 8);
			this.userView.reSetResourceMx(null, 17);
			this.userView.setUserName(logon_username);
			this.userView.canLogin(false,this.getFrameconn());
			this.userView.setBAgent(false);
			//System.out.println("mpcode---"+this.userView.getManagePrivCodeValue());
			if(id!=null&&id.length()>0&&!"-1".equals(id))
			{
				AgentsetUtils agentsetUtils=new AgentsetUtils(this.getFrameconn());
				String fuct_s=agentsetUtils.getFunctionprivStr(id);
				String warn_p=agentsetUtils.getWarnprivStr(id);
				warnPrivMap = agentsetUtils.analyseParameter(warn_p);
				fuct_s=fuct_s!=null&&fuct_s.length()>0?fuct_s:"";
				if(!haveTheFunc(fuct_s,"0"))
				{
					agent_func_str.append(",0,"+fuct_s);
				}else
					agent_func_str.append(fuct_s);
				if(haveTheFunc(agent_func_str.toString(),"0107") && !haveTheFunc(agent_func_str.toString(),"01"))
				   agent_func_str.append(",01,");
				if(haveTheFunc(agent_func_str.toString(),"0KR0101")||haveTheFunc(agent_func_str.toString(),"0KR0102"))
					agent_func_str.append(",0KR,");
				if(!warnPrivMap.isEmpty()){
					if( (!"".equals((String)warnPrivMap.get("rsbd")) || !"".equals((String)warnPrivMap.get("gzbd")) || !"".equals((String)warnPrivMap.get("ins_bd")))&&!haveTheFunc(agent_func_str.toString(),"01"))
					{
						agent_func_str.append(",01,");
					}
				}
				this.userView.setBAgent(true);
				this.userView.setFuncpriv(agent_func_str);
				this.userView.reSetResourceMx((String)warnPrivMap.get("rsbd"), 7);
				this.userView.reSetResourceMx((String)warnPrivMap.get("gzbd"), 8);
				this.userView.reSetResourceMx((String)warnPrivMap.get("ins_bd"), 17);
			}
				
			//System.out.println("mpcode---"+this.userView.getManagePrivCodeValue());
		    session.setAttribute(WebConstant.userView, this.userView);
			session.setAttribute("curAgenter", agentIds);
			session.setAttribute("isAgenter", "yes");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
     * 当前对象是否有
     * @param func_str ，用户已授权的功能串列如 ,2020,30,
     * @param func_id
     * @return
     */
    private boolean haveTheFunc(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1)
    		return false;
    	else
    		return true;
    }
}
