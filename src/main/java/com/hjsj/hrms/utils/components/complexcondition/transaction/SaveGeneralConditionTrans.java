package com.hjsj.hrms.utils.components.complexcondition.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Arrays;

public class SaveGeneralConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		RowSet set = null;
		try
		{
			String expr=(String)this.getFormHM().get("expr");
			expr = SafeCode.decode(expr);
			expr=PubFunc.keyWord_reback(expr);
			String name=(String)this.getFormHM().get("name");
			String msg = "0";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select * from gwhere where name =?";
			
			set = dao.search(sql, Arrays.asList(name));
			if(set.next())
				msg = "当前名称已存在，请重新命名";
			if(!"0".equals(msg))
				throw GeneralExceptionHandler.Handle(new Throwable(msg));
			RecordVo vo=new RecordVo("gwhere");
			int id=DbNameBo.getPrimaryKey("gwhere","id",this.getFrameconn());  //取得主键值
			vo.setInt("id",id);
			vo.setString("name",name);
			vo.setString("lexpr",expr);
			vo.setString("type","1");
			vo.setString("moduleflag","10000000000000000000");
			dao.addValueObject(vo);
			this.getFormHM().put("msg",msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(set);
		}

	}

}
