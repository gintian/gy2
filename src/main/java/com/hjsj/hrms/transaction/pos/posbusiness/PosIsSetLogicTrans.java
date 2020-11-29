package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class PosIsSetLogicTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			RecordVo constantuser_vo=ConstantParamter.getRealConstantVo("PS_CODE");
	   	  if(constantuser_vo==null)
	   	  {
	   		  this.getFormHM().put("issetpos","0");
	   	  }else
	   	  {
	   		  this.getFormHM().put("issetpos","1");
	   	  }	
		}catch(Exception e)
		{
			e.printStackTrace();			
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
