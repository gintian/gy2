/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.sys.UserInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 24, 20069:10:10 AM
 * @author chenmengqing
 * @version 4.0
 */
public class LinkEmployTrans extends IBusiness {

	private String getDbString()throws GeneralException
	{
		StringBuffer str_dbff=new StringBuffer();
		try
		{
			DbNameBo db_vo=new DbNameBo(this.getFrameconn());
			ArrayList list=db_vo.getAllDbNameVoList();
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				str_dbff.append(vo.getString("pre").toUpperCase());
				str_dbff.append("'");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);			
		}
		return str_dbff.toString();
	}	
	
	public void execute() throws GeneralException {
		try
		{
			String a0100=(String)this.getFormHM().get("a0100");
			String checkflag=(String)this.getFormHM().get("checkflag");//是否检查该资助用户关联业务用户的情况
			checkflag = checkflag == null ?"":checkflag;
			String selfHelpUser=(String)this.getFormHM().get("selfHelpUser");//是否检查该资助用户关联业务用户的情况
			selfHelpUser = selfHelpUser == null?"":selfHelpUser;
			if("true".equals(checkflag)){
				checkEmplyStatus(selfHelpUser);
				return;
			}
			String username=(String)this.getFormHM().get("username");
			if(a0100==null|| "".equals(a0100))
				throw new GeneralException(ResourceFactory.getProperty("error.link.employ"));
			if(username==null|| "".equals(username))
				return;
			String nbase=a0100.substring(0,3);
			String str_dbff=getDbString().toUpperCase();
			if(str_dbff.indexOf(nbase.toUpperCase())==-1)
			{
				throw new GeneralException(ResourceFactory.getProperty("error.link.employ"));
			}
			String id=a0100.substring(3);
			RecordVo vo=new RecordVo(nbase+"a01");
			vo.setString("a0100",id);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
			{
				RecordVo oper_vo=new RecordVo("operuser");
				oper_vo.setString("username",username);
				oper_vo.setString("a0100",id);
				oper_vo.setString("nbase",nbase);
				oper_vo.setString("fullname",vo.getString("a0101"));
				dao.updateValueObject(oper_vo);
				
				//同步更新流程定义t_wf_actor表里的actorname数据
				dao.update("update t_wf_actor set actorname=? where actor_type=4 and actorid=? ",Arrays.asList(vo.getString("a0101"),username));
				
				
				UserInfo userinfo=new UserInfo();
				userinfo.setName(vo.getString("a0101"));
				userinfo.setNbase(AdminCode.getCodeName("@@",nbase));
				userinfo.setB0110(AdminCode.getCodeName("UN",vo.getString("b0110")));
				userinfo.setE0122(AdminCode.getCodeName("UM",vo.getString("e0122")));
				userinfo.setE01a1(AdminCode.getCodeName("@K",vo.getString("e01a1")));	
				this.getFormHM().put("userinfo",userinfo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void checkEmplyStatus(String selfHelpUser) {
		try {
			String sql = "select username from operuser where a0100 = ? and upper(nbase) = ?";
			ArrayList valuelist = new ArrayList();
			valuelist.add(selfHelpUser.substring(3));
			valuelist.add(selfHelpUser.substring(0,3).toUpperCase());
			
			ContentDAO dao = new ContentDAO(getFrameconn());
			this.frowset = dao.search(sql, valuelist);
			String isRelated = "false";
			String selfUserName = "";
			while(this.frowset.next()){
				isRelated = "true";
				selfUserName +="," +  this.frowset.getString("username");
			}
			if(selfUserName.length() > 0)
				selfUserName = selfUserName.substring(1);
			this.formHM.put("selfUserName", selfUserName);
			this.formHM.put("isRelated", isRelated);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
