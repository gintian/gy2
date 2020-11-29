package com.hjsj.hrms.transaction.gz.gz_analyse.gz_fare_analyse;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GzFareAnalyseTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map = bo.getValuesMap();
			if(map ==null)
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
			if(ctrl_type==null)
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			String priv = "1";
			if("0".equals(ctrl_type))
				priv="1";
			if("1".equals(ctrl_type))
				priv="2";
			this.getFormHM().put("ctrl_type",priv);
			String isYD="0";
			if("bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				isYD="1";
			}
			this.getFormHM().put("isYd", isYD);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
