package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExchangeAppTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		RecordVo vo=new RecordVo("q19");
		String cur_date=PubFunc.getStringDate("yyyy.MM.dd");	   
		vo.setString("q1905",cur_date);
		vo.setString("q19z1",cur_date);
		vo.setString("q19z3",cur_date) ;
		this.getFormHM().put("ex_vo",vo);
	}

}
