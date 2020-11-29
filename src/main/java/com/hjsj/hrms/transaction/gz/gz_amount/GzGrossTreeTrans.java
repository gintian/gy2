package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GzGrossTreeTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
			HashMap pamaHm=(HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag=(String)pamaHm.get("returnflag"); 
			this.getFormHM().put("returnflag",returnflag);
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map = bo.getValuesMap();
			if(map==null)
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
			String priv = "1";
			/**是否级联显示机构，根据总额参数是否定义按层级控制，如果不按层级控制，则不显示级联机构,默认显示级联*/
			String ctrl_by_level="1";
			if(ctrl_type!=null)
			{
	    		if("0".equals(ctrl_type))
	    			priv="1";
    			if("1".equals(ctrl_type))
	    			priv="2";
			}
			else
			{
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			}
			ctrl_by_level=(String)map.get("ctrl_by_level");
			/**=0按管理范围控制=1按操作单位控制=3按模块操作单位控制*/
			String viewUnit="0";
			if(this.getUserView().getUnit_id()!=null&&this.getUserView().getUnit_id().trim().length()>0&&!"UN".equalsIgnoreCase(this.getUserView().getUnit_id()))
			{
				viewUnit="1";
			}
			if(this.userView.getUnitIdByBusi("2")!=null&&this.userView.getUnitIdByBusi("2").length()>0&&!"UN".equalsIgnoreCase(this.userView.getUnitIdByBusi("2")))
			{
				viewUnit="3";
			}
			String ubb=this.userView.getUnitIdByBusi("2");
			/* 薪资总额月份保存优化 xiaoyun 2014-10-23 start */
			this.getFormHM().put("filtervalue","0");
			/* 薪资总额月份保存优化 xiaoyun 2014-10-23 end */
			this.getFormHM().put("ctrl_type",priv);
			this.getFormHM().put("viewUnit", viewUnit);
			this.getFormHM().put("cascadingctrl", "0".equals(ctrl_by_level)?"1":"0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		
	}

}
