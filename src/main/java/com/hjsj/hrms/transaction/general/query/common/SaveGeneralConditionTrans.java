package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
public class SaveGeneralConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String expr=(String)this.getFormHM().get("expr");
			expr=PubFunc.keyWord_reback(expr);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String name=(String)this.getFormHM().get("name");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("gwhere");
			int id=DbNameBo.getPrimaryKey("gwhere","id",this.getFrameconn());  //取得主键值
			vo.setInt("id",id);
			vo.setString("name",name);
			vo.setString("lexpr",expr);
			vo.setString("type","1");
			vo.setString("moduleflag","10000000000000000000");
			dao.addValueObject(vo);
			this.getFormHM().put("expr",expr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
