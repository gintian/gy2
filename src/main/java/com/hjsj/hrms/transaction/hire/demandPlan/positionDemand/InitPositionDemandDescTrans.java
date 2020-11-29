package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class InitPositionDemandDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operate=(String)hm.get("operate");			
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
			
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			String hireMajor="";
			if(map!=null&&(map.get("hireMajor"))!=null)
			{
				hireMajor=(String)map.get("hireMajor");
			}
			String hireMajorCode="";
			if(map!=null&&(map.get("hireMajorCode"))!=null)
			{
				hireMajorCode=(String)map.get("hireMajorCode");
			}
			this.getFormHM().put("hireMajor", hireMajor);
			this.getFormHM().put("hireMajorCode", hireMajorCode);
			this.getFormHM().put("schoolPosition", map.get("schoolPosition")!=null?(String)map.get("schoolPosition"):"");
			FieldItem a_item=DataDictionary.getFieldItem("Z0336","Z03");
			if(a_item==null)
				throw GeneralExceptionHandler.Handle(new Exception("用工需求表中缺少招聘渠道（Z0336）字段！"));
			if("init".equals(operate))
			{
				ArrayList positionDemandDescList=positionDemand.getPositionDemandDescList(list,"-1");			
				//简历筛选条件列表
				ArrayList conditionList=positionDemand.getResetPosConditionList("0","-1");			
				this.getFormHM().put("posConditionList",conditionList);
				this.getFormHM().put("positionDemandDescList",positionDemandDescList);
				this.getFormHM().put("mailTemplateList",positionDemand.getMailTemplateList());
				this.getFormHM().put("isRevert","0");
				this.getFormHM().put("mailTemplateID","");
				String isOrgWillTableIdDefine="0";
				/**是否定义单位部门预算表*/
				if(map!=null&&map.get("orgWillTableId")!=null&&!"#".equals((String)map.get("orgWillTableId")))
				{
					isOrgWillTableIdDefine="1";
				}
				this.getFormHM().put("isOrgWillTableIdDefine", isOrgWillTableIdDefine);
			}
			else if("read".equals(operate))
			{
				String z0301=(String)hm.get("z0301");
				z0301 = PubFunc.decrypt(z0301);
				ArrayList positionDemandDescList=positionDemand.getPositionDemandDescList(list,z0301);			
				//简历筛选条件列表
				ArrayList conditionList=positionDemand.getResetPosConditionList("0",z0301);			
				this.getFormHM().put("posConditionList",conditionList);
				this.getFormHM().put("positionDemandDescList",positionDemandDescList);
				this.getFormHM().put("mailTemplateList",positionDemand.getMailTemplateList());
				
				DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
				HashMap conditionMap=bo.getAttributeValues("answer_mail");
				LazyDynaBean abean=(LazyDynaBean)conditionMap.get("answer_mail");
				String flag="false";
				if(abean!=null&&abean.get("flag")!=null)
					flag=(String)abean.get("flag");
				String template="";
				if(abean!=null&&abean.get("template")!=null)
				    template=(String)abean.get("template");
				if("true".equalsIgnoreCase(flag))
					this.getFormHM().put("isRevert","1");
				else
					this.getFormHM().put("isRevert","0");
				this.getFormHM().put("mailTemplateID",template);
				this.getFormHM().put("z0301",z0301);
				
				EmployNetPortalBo employNetPortalBo =new EmployNetPortalBo(this.getFrameconn());
				RecordVo z03Vo=employNetPortalBo.getRecordVo(z0301);
				this.getFormHM().put("isPosBooklet",employNetPortalBo.getPosIsBooklet(z03Vo.getString("z0311")));
				this.getFormHM().put("e01a1",z03Vo.getString("z0311"));
				
				
			}
			else if("edit".equals(operate))
			{
				String z0301=(String)hm.get("z0301");
				z0301 = PubFunc.decrypt(z0301);
				ArrayList positionDemandDescList=positionDemand.getPositionDemandDescList(list,z0301);			
				//简历筛选条件列表
				ArrayList conditionList=positionDemand.getResetPosConditionList("0",z0301);			
				this.getFormHM().put("posConditionList",conditionList);
				this.getFormHM().put("positionDemandDescList",positionDemandDescList);
				this.getFormHM().put("mailTemplateList",positionDemand.getMailTemplateList());
				
				DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
				HashMap conditionMap=bo.getAttributeValues("answer_mail");
				if(conditionMap!=null&&conditionMap.size()!=0){
					LazyDynaBean abean=(LazyDynaBean)conditionMap.get("answer_mail");
					
					String flag=(String)abean.get("flag");
					String template=(String)abean.get("template");
					if("true".equalsIgnoreCase(flag))
						this.getFormHM().put("isRevert","1");
					else
						this.getFormHM().put("isRevert","0");
					this.getFormHM().put("mailTemplateID",template);
					this.getFormHM().put("z0301",z0301);
					
					EmployNetPortalBo employNetPortalBo =new EmployNetPortalBo(this.getFrameconn());
					RecordVo z03Vo=employNetPortalBo.getRecordVo(z0301);
					this.getFormHM().put("isPosBooklet",employNetPortalBo.getPosIsBooklet(z03Vo.getString("z0311")));
					this.getFormHM().put("e01a1",z03Vo.getString("z0311"));
				}
				
			}else if("browse".equals(operate))
			{
				/**=1从需求报批接口进入，=2从需求审核接口进入，=3从审核查询接口进入，=4从招聘计划接口进入，*/
				String entertype=(String)hm.get("entertype");
				String z0301=(String)hm.get("z0301");
				z0301 = PubFunc.decrypt(z0301);
				String posState=(String)hm.get("posState");
				String positionStateDesc=AdminCode.getCodeName("23", posState);
				String positionState="0";
				/**从需求审核接口进入,都可编辑*/
				if("2".equals(entertype))
				{
					if(posState!=null&&("02".equals(posState)|| "01".equals(posState)|| "07".equals(posState)))
				    	positionState="1";
				}
				else
				{
	    			if(posState!=null&&("01".equals(posState)|| "07".equals(posState)))
		    			positionState="1";
				}
				ArrayList positionDemandDescList=positionDemand.getPositionDemandDescList(list,z0301);			
				//简历筛选条件列表
				ArrayList conditionList=positionDemand.getResetPosConditionList("0",z0301);			
				this.getFormHM().put("posConditionList",conditionList);
				this.getFormHM().put("positionDemandDescList",positionDemandDescList);
				this.getFormHM().put("mailTemplateList",positionDemand.getMailTemplateList());
				
				DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
				HashMap conditionMap=bo.getAttributeValues("answer_mail");
				if(conditionMap!=null){
					LazyDynaBean abean=(LazyDynaBean)conditionMap.get("answer_mail");
					String flag="false";
					if(abean!=null&&abean.get("flag")!=null)
						flag=(String)abean.get("flag");
					String template="";
					if(abean!=null&&abean.get("template")!=null)
						template=(String)abean.get("template");;
					if("true".equalsIgnoreCase(flag))
						this.getFormHM().put("isRevert","1");
					else
						this.getFormHM().put("isRevert","0");
					this.getFormHM().put("mailTemplateID",template);
					this.getFormHM().put("z0301",z0301);
					
					EmployNetPortalBo employNetPortalBo =new EmployNetPortalBo(this.getFrameconn());
					RecordVo z03Vo=employNetPortalBo.getRecordVo(z0301);
					this.getFormHM().put("isPosBooklet",employNetPortalBo.getPosIsBooklet(z03Vo.getString("z0311")));
					this.getFormHM().put("e01a1",z03Vo.getString("z0311"));
					this.getFormHM().put("positionState",positionState);
					this.getFormHM().put("positionStateDesc", positionStateDesc);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
