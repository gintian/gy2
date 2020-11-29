/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.valueobject.sys.UserInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 21, 20065:43:07 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchLinkEmployTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String user_name=(String)this.getFormHM().get("username");
			RecordVo operuser=new RecordVo("operuser");
			operuser.setString("username",user_name);
			operuser=dao.findByPrimaryKey(operuser);
			if(operuser==null)
				return;
				//throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.user.notexist")));
			String nbase=operuser.getString("nbase");
			String a0100=operuser.getString("a0100");
			if(nbase==null|| "".equals(nbase))
				return;
			RecordVo uservo=new RecordVo(nbase+"A01");
			uservo.setString("a0100",a0100);
			uservo=dao.findByPrimaryKey(uservo);
			if(uservo==null)
				return;
			UserInfo userinfo=new UserInfo();
			userinfo.setA0100(a0100);
			userinfo.setName(uservo.getString("a0101"));
			userinfo.setB0110(uservo.getString("b0110"));
			userinfo.setE0122(uservo.getString("e0122"));
			userinfo.setE01a1(uservo.getString("e01a1"));
			userinfo.setNbase(nbase);
			this.getFormHM().put("userinfo",userinfo);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
