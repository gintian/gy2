package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CalculateFormulaTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String setid=(String)this.getFormHM().get("setid");
			String unit_type=(String)this.getFormHM().get("unit_type");
			String year=(String)this.getFormHM().get("year");
			String sortstr=(String)this.getFormHM().get("sortstr");
			String ids = (String)this.getFormHM().get("ids");
			GrossManagBo gross = new GrossManagBo(this.getFrameconn(),this.getUserView());
			String msg="0";
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map = bo.getValuesMap();
			if(map==null)
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
			String fc_flag=(String)map.get("fc_flag");
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
			String cascadingctrl= "0".equals(ctrl_by_level)?"1":"0";
			gross.setCascadingctrl(cascadingctrl);
			gross.setViewUnit(viewUnit);
			gross.saveSort(this.getFrameconn(), sortstr);
			
			if(fc_flag!=null&&fc_flag.length()!=0){
				gross.setFc_flag(fc_flag);
			}
			if(gross.calculateFormula(setid, unit_type, year,ctrl_type,ids))
			{
				msg="1";
			}
			this.getFormHM().put("msg", msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
