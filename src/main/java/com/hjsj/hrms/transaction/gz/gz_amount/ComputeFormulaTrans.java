package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ComputeFormulaTrans.java</p>
 * <p>Description>:薪资总额计算</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 30, 2012  09:38:12 AM </p>
 * <p>@version: 6.0</p>
 * <p>@author: JinChunhai
 */

public class ComputeFormulaTrans extends IBusiness
{
	
	public void execute() throws GeneralException 
	{
		try
		{
			String setid = (String)this.getFormHM().get("setid"); // 总额子集指标
			String year = (String)this.getFormHM().get("year"); // 年度
			String codeitemid = (String)this.getFormHM().get("codeitemid"); // 选择的单位部门编号
			String filtervalue = (String)this.getFormHM().get("filtervalue"); // 选择的季度或月份
			String spType = (String)this.getFormHM().get("spType"); // 审批状态
			
		//	System.out.println(setid+"-----------"+year+"-----------"+codeitemid+"-----------"+filtervalue);
							
			GrossManagBo gross = new GrossManagBo(this.getFrameconn(),this.getUserView());
			String msg = "0";
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map = bo.getValuesMap();
			if(map==null)
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			String ctrl_type = (String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
			String fc_flag = (String)map.get("fc_flag");
			String priv = "1";
			// 是否级联显示机构，根据总额参数是否定义按层级控制，如果不按层级控制，则不显示级联机构,默认显示级联
			String ctrl_by_level = "1";
			if(ctrl_type!=null)
			{
	    		if("0".equals(ctrl_type))
	    			priv="1";
    			if("1".equals(ctrl_type))
	    			priv="2";
			}
			else	
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			
			ctrl_by_level = (String)map.get("ctrl_by_level");
			// =0按管理范围控制=1按操作单位控制=3按模块操作单位控制
			String viewUnit = "0";
			if(this.getUserView().getUnit_id()!=null && this.getUserView().getUnit_id().trim().length()>0 && !"UN".equalsIgnoreCase(this.getUserView().getUnit_id()))
			{
				viewUnit = "1";
			}
			if(this.userView.getUnitIdByBusi("2")!=null && this.userView.getUnitIdByBusi("2").length()>0 && !"UN".equalsIgnoreCase(this.userView.getUnitIdByBusi("2")))
			{
				viewUnit = "3";
			}
			String cascadingctrl = "0".equals(ctrl_by_level)?"1":"0";
			gross.setCascadingctrl(cascadingctrl);
			gross.setViewUnit(viewUnit);			
			if(fc_flag!=null && fc_flag.length()>0)
				gross.setFc_flag(fc_flag);
						
		//	if(gross.calculateFormula(setid, "", year,ctrl_type,""))
		//	{
		//		msg="1";
		//	}
			
			// 新建根据计算公式计算出薪资总额的临时表并进行操作
			boolean success = gross.creatGzColumTable(setid,year,codeitemid,filtervalue,spType,priv,map);
			if(success)
				msg = "1";
						
			this.getFormHM().put("msg", msg);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}