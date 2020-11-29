package com.hjsj.hrms.transaction.kq.app_check_in.redeploy_rest;

import com.hjsj.hrms.businessobject.kq.app_check_in.exchange_class.ExchangeClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ViewRedeployAppTrans extends IBusiness {

	private String table_name="q25";
	public void execute() throws GeneralException 
	{
		RecordVo vo=new RecordVo(this.table_name);
		String id=(String)this.getFormHM().get("id");
		id=PubFunc.decrypt(id);
		vo.setString(this.table_name+"01",id);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			vo=dao.findByPrimaryKey(vo);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		ExchangeClass exchangeClass=new ExchangeClass();
		
		//String class_id=exchangeClass.getClassId(vo.getString("nbase"),vo.getString("a0100"),vo.getString(this.table_name+"z3"),this.getFrameconn());
		String class_id=vo.getString(this.table_name+"z7");
		String class_name=exchangeClass.getClassName(class_id,this.getFrameconn());
		/*String exclass_id=exchangeClass.getClassId(vo.getString("nbase"),vo.getString("a0100"),vo.getString(this.table_name+"z3"),this.getFrameconn());
		String exclass_name=exchangeClass.getClassName(exclass_id,this.getFrameconn());*/
		this.getFormHM().put("ex_vo",vo);
		this.getFormHM().put("class_name",class_name);
		
	}

}
