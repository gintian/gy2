package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class AddOrEditTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String subsys_id = (String)map.get("subsys_id");
			String type=(String)map.get("type");
			String templatesetid = (String)this.getFormHM().get("templatesetid");
			String parentsetid = (String)this.getFormHM().get("parentsetid");
			String setname = (String)this.getFormHM().get("setname");
			String templatename="";
			String topscore="0";
			String status = "0";
			String templateid="";
			String templateUsed="0";
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			if("0".equals(type))//新增
			{
				if(!"root".equalsIgnoreCase(templatesetid)){
					HashMap hm =bo.getTemplateSetById(templatesetid);
					String unit = (String)hm.get("b0110");
					if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()) && !"HJSJ".equalsIgnoreCase(unit)){
						if(!(unit.length()>KhTemplateBo.getyxb0110(userView, this.getFrameconn()).length()?unit.substring(0, KhTemplateBo.getyxb0110(userView, this.getFrameconn()).length()):unit).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, this.getFrameconn()))){
							throw new GeneralException("您没有该模板分类的编辑权限！");//新增
						}
					}
				}
				templateid=bo.getNextSeq("template_id", "per_template");
			}
			else{//修改
				if("3".equalsIgnoreCase(type))//模板另存
				{
					templateid=bo.getNextSeq("template_id", "per_template");
					LazyDynaBean bean = bo.getTemplateInfo(templatesetid);
					templatename = (String)bean.get("templatename")+"(复件)";
					topscore = (String)bean.get("topscore");
					status=(String)bean.get("status");
					LazyDynaBean abean = bo.getTemplateSetInfo(templatesetid);
					parentsetid = (String)abean.get("parentsetid");
					setname = (String)abean.get("setname");
				}
				else
				{
					templateid=templatesetid;
				//	HashMap hm = new HashMap();
					if(!"root".equalsIgnoreCase(templateid)){
				//		hm =bo.getTemplateSetById(templateid);
						if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId())){
							if(!userView.isRWHaveResource(IResourceConstant.KH_MODULE,templateid)){
								throw new GeneralException("您没有该模板的可写权限！");//修改
							}
						}
					}
			     	LazyDynaBean bean = bo.getTemplateInfo(templateid);
		    		templatename = (String)bean.get("templatename");
		    		topscore = (String)bean.get("topscore");
			    	status=(String)bean.get("status");
			    	templateUsed=(String)this.getFormHM().get("templateUsed");
				}
			}
			
			this.getFormHM().put("parentsetid",parentsetid);
			this.getFormHM().put("setname",setname);
			this.getFormHM().put("type",type);
			this.getFormHM().put("templatesetid", templatesetid);
			this.getFormHM().put("templatename",templatename);
			this.getFormHM().put("topscore",topscore);
			this.getFormHM().put("status", status);
			this.getFormHM().put("templateid",templateid);
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("isclose", "1");
			this.getFormHM().put("templateUsed", templateUsed);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
