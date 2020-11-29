package com.hjsj.hrms.transaction.dutyinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class GetSetParams extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		RecordVo option_vo=ConstantParamter.getRealConstantVo("PS_C_JOB");
		if(option_vo== null){
			this.getFormHM().put("ps_c_sduty", "hidden");
			return;
		}
		String setitem=option_vo.getString("str_value");
		if(!"#".equals(setitem)){
			boolean exsit=setItemExsit(setitem);
			if(exsit)
				 this.getFormHM().put("ps_c_sduty", "show");
			else
				this.getFormHM().put("ps_c_sduty", "hidden");
		}
		else
			this.getFormHM().put("ps_c_sduty", "hidden");
	}
	
	public boolean setItemExsit(String setitem){
		boolean itemexsit=false;
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			String sql="select 1 from fielditem where fieldsetid='K01' and itemid='"+setitem+"' and useflag=1";
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				itemexsit=true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemexsit;
	}

}
