package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.InnerHireBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitPosDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");			 
			String z0301=(String)hm.get("z0301");
			if (null != z0301 || !"".equals(z0301))
			    z0301 = PubFunc.decrypt(z0301);
			z0301 = "".equals(z0301) ? (String)hm.get("z0301") : z0301;
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
			HashMap fieldMap=new HashMap();
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				fieldMap.put(item.getItemid().toLowerCase(),item.getItemdesc()+"^"+item.getItemtype()+"^"+item.getCodesetid());
			}
			ArrayList posDescFiledList=employNetPortalBo.getPosDescFiledList(z0301,fieldMap); //职位详细信息 指标列表
			
			employNetPortalBo.addStatInfo(1, z0301);
			ArrayList applyedPosList=new ArrayList();   //已申请的职位信息列表
			String clientName=SystemConfig.getPropertyValue("clientName");//得到一个专有的客户名称
			//如果为登陆用户，则得到其已申请职位的信息列表
			if(!"recommend".equals(hm.get("fromRecommend")))//fromRecommend=recommend 来自推荐岗位
			{
	             if(clientName!=null&& "hkyh".equalsIgnoreCase(clientName)){
	                    Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
	                    String onlyFlag = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
	                    
	                    if(onlyFlag==null||onlyFlag.trim().length()<=0){
	                        throw GeneralExceptionHandler.Handle(new Exception("请配置系统的唯一性指标!"));
	                    }
	                    
	                    InnerHireBo bo=new InnerHireBo(this.getFrameconn());
	                    String onlyName = bo.getEmailAddress(onlyFlag,this.getUserView());//这里得到的是人员的唯一性标识
	                    String a0100=bo.getZpkA0100(onlyName,onlyFlag);
	                    
	                    if(a0100.length()>0)
	                    {
	                        this.getFormHM().put("zpkA0100",a0100);
	                        applyedPosList=employNetPortalBo.getApplyedPosList(a0100.trim());
	                    }
	                    else
	                    {
	                        this.getFormHM().put("zpkA0100","");
	                    }
                } else {
                    RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL");
                    if (vo != null) {
                        InnerHireBo bo = new InnerHireBo(this.getFrameconn());
                        if (vo.getString("str_value").replaceAll("#", "").trim().length() == 0)
                            throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中指定邮件指标!"));
                        String email = bo.getEmailAddress(vo.getString("str_value"), this.getUserView());
                        String a0100 = bo.getZpkA0100(email, vo.getString("str_value"));
                        if (a0100.length() > 0) {
                            this.getFormHM().put("zpkA0100", a0100);
                            applyedPosList = employNetPortalBo.getApplyedPosList(a0100.trim());
                        } else {
                            this.getFormHM().put("zpkA0100", "");
                        }
                    } else
                        throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中指定邮件指标!"));
                }
			}
			hm.remove("fromRecommend");
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			if(vo==null)
				throw GeneralExceptionHandler.Handle(new Exception("没有设置外聘库!"));
			String dbname=vo.getString("str_value");
			RecordVo z03Vo=employNetPortalBo.getRecordVo(z0301);
			this.getFormHM().put("isPosBooklet",employNetPortalBo.getPosIsBooklet(z03Vo.getString("z0311")));
			this.getFormHM().put("e01a1",z03Vo.getString("z0311"));
			this.getFormHM().put("dbname",dbname);
			this.getFormHM().put("posID",z0301);
			this.getFormHM().put("applyedPosList",applyedPosList);
			this.getFormHM().put("posDescFiledList",posDescFiledList);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	
	
	
}
