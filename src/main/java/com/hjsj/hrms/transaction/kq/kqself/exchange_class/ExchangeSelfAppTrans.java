package com.hjsj.hrms.transaction.kq.kqself.exchange_class;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExchangeSelfAppTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		RecordVo vo=new RecordVo("q19");
		String cur_date=PubFunc.getStringDate("yyyy.MM.dd");	
		vo.setString("nbase",userView.getDbname());
        vo.setString("a0100",userView.getA0100());
        vo.setString("b0110",userView.getUserOrgId());
        vo.setString("e0122",userView.getUserDeptId());
        vo.setString("a0101",userView.getUserFullName());
        vo.setString("e01a1",userView.getUserPosId());
		vo.setString("q1905",cur_date);
		vo.setString("q19z1",cur_date);
		vo.setString("q19z3",cur_date) ;
		this.getFormHM().put("ex_vo",vo);
	}

}