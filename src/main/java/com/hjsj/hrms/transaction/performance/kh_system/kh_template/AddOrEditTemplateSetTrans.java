package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddOrEditTemplateSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			String templatesetid=(String)map.get("templatesetid");
			HashMap hm = new HashMap();
			if(!"root".equalsIgnoreCase(templatesetid)){
				
				KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
				hm =bo.getTemplateSetById(templatesetid);
				String unit = (String)hm.get("b0110");
				if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()) && !"HJSJ".equalsIgnoreCase(unit)){
					if(!(unit.length()>KhTemplateBo.getyxb0110(userView, this.getFrameconn()).length()?unit.substring(0, KhTemplateBo.getyxb0110(userView, this.getFrameconn()).length()):unit).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, this.getFrameconn()))){
						throw new GeneralException("您没有该模板分类的编辑权限！");
					}
				}
			}
			String subsys_id = (String)map.get("subsys_id");
			String fname="";
			String fvalidflag = "1";
			String scope = "0";
			if("0".equals(type))//new
			{
				
			}
			if("1".equals(type))//edit
			{
				fname = (String)hm.get("name");
				fvalidflag = (String)hm.get("flag");
				scope = (String)hm.get("scope");
				
			}
			this.getFormHM().put("type",type);
			this.getFormHM().put("templatesetid",templatesetid);
			this.getFormHM().put("fname",fname);
			this.getFormHM().put("fvalidflag",fvalidflag);
			this.getFormHM().put("scope",scope);
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("parentid", templatesetid);
			this.getFormHM().put("isClose","2");
			this.getFormHM().put("isrefresh","1");
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
