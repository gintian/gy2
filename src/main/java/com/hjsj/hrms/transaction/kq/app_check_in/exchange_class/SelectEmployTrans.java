package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.sys.UserInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SelectEmployTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String a0100=(String)this.getFormHM().get("a0100");
			if(a0100==null|| "".equals(a0100))
				throw new GeneralException(ResourceFactory.getProperty("error.link.employ"));
			
			String nbase=a0100.substring(0,3);			
			String id=a0100.substring(3);
			RecordVo vo=new RecordVo(nbase+"a01");
			vo.setString("a0100",id);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
			{
				
				UserInfo userinfo=new UserInfo();
				userinfo.setName(vo.getString("a0101"));
				userinfo.setNbase(nbase);
				userinfo.setB0110(vo.getString("b0110"));
				userinfo.setE0122(vo.getString("e0122"));
				userinfo.setE01a1(vo.getString("e01a1"));
				userinfo.setA0100(id);
				this.getFormHM().put("userinfo",userinfo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
