package com.hjsj.hrms.transaction.kq.app_check_in.redeploy_rest;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class RedeployAppTrans extends IBusiness {

	private String table_name="q25";
	public void execute() throws GeneralException 
	{
		RecordVo vo=new RecordVo(this.table_name);
		String cur_date=PubFunc.getStringDate("yyyy.MM.dd");	   
		vo.setString(this.table_name+"05",cur_date);
		vo.setString(this.table_name+"z1",cur_date);
		vo.setString(this.table_name+"z3",cur_date) ;
		this.getFormHM().put("ex_vo",vo);
	}

}
