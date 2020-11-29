package com.hjsj.hrms.transaction.hire.employSummarise.personnelEmploy;

import com.hjsj.hrms.businessobject.hire.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitPersonnelEmploy extends IBusiness {

	public void execute() throws GeneralException {
		
		try
		{
			DbWizard dbWizard = new DbWizard(this.frameconn);
			// 如果z05.z0515(拟录用时间)字段不存在，则新建 by 刘蒙
			if (!dbWizard.isExistField("z05", "Z0515")) {
				Table table=new Table("z05");
				Field obj = new Field("Z0515");
				obj.setDatatype(DataType.DATETIME);
				obj.setNullable(true);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.addColumns(table);
			}
			
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			String testTemplateIds= parameterXMLBo.getTestTemplateIds();
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
			boolean flag=parameterSetBo.createEvaluatingTable(testTemplateIds);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b_query=(String)hm.get("b_query");
			//hm.remove("b_query");
			String codeid=(String)hm.get("code");
			String codesetid=(String)hm.get("codeset");	
			String operate=(String)hm.get("operate");
			String model=(String)hm.get("model");
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();  //应用库前缀		
			InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());	
			String extendWhereSql=PubFunc.decrypt((String)this.getFormHM().get("extendWhereSql"));
			String orderSql=PubFunc.decrypt((String)this.getFormHM().get("orderSql"));
			String returnflag="";
			if(hm.get("returnflag")!=null)
			{
				returnflag=(String)hm.get("returnflag");
			}
			else
				returnflag=(String)this.getFormHM().get("returnflag");
			this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
			if(operate!=null&& "init".equals(operate))
			{	
				//orderSql="order by "+dbname+"A01.state desc";
				if (Sql_switcher.searchDbServer()!=Constant.ORACEL){
					orderSql=" order by Z0515 desc,Z0509A desc"; // 优先按照拟录用时间倒序排列 by 刘蒙
				}
				else{
					orderSql=" order by Z05.Z0515 desc,Z05.Z0509 desc";// zzk 2013/12/19  中核华兴  面试默认按面试时间降序排列
				}
				this.getFormHM().put("extendWhereSql","");
				extendWhereSql="";
				//this.getFormHM().put("orderSql","  order by "+dbname+"A01.state desc");
				this.getFormHM().put("orderSql",PubFunc.encrypt(orderSql));// zzk 2013/12/19  中核华兴  面试默认按面试时间降序排列
			}
			if(codeid==null|| "0".equals(b_query))
			{
				if(!(userView.isAdmin()&& "1".equals(userView.getGroupId())))
				{
					/**
					 * modify dml 2012-3-31 15:50:33
					 * reason 因增加业务管理范围导致权限规则改边
					 * */
//					if(this.userView.getStatus()==4/*hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ")*/)
//					{
//						String codeset=this.getUserView().getManagePrivCode();
//						codeid=this.getUserView().getManagePrivCodeValue();
//						if(codeset==null||codeset.equals(""))
//						{
//							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//						}							
//					}
//					else
//					{
//						if(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().equals("")||this.getUserView().getUnit_id().equalsIgnoreCase("UN"))
//						{
//							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//						}
//						else
//						{
//							String temp0=this.getUserView().getUnit_id();
//							if(temp0.equals(""))
//							{
//								codeid="#";
//							}else if(temp0.trim().length()==3)
//							{
//								codeid="";
//							}
//							else
//							{
//								if(temp0.indexOf("`")==-1)
//								{
//									codeid=temp0.substring(2);
//								}
//								else
//								{
//				        			String[] temps=temp0.split("`");
//				        			codeid="";
//				           			for(int i=0;i<temps.length;i++)
//				        				codeid+=temps[i].substring(2)+"`";
//								}
//							}
//						}
//					}
					codeid=this.userView.getUnitIdByBusi("7");
					if(codeid==null|| "".equals(codeid)|| "UN".equalsIgnoreCase(codeid)){
						throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
					}
					if(codeid.trim().length()==3)
					{
						codeid="";
					}else{
						if(codeid.indexOf("`")==-1)
						{
							codeid=codeid.substring(2);
						}
						else
						{
		        			String[] temps=codeid.split("`");
		        			codeid="";
		           			for(int i=0;i<temps.length;i++)
		        				codeid+=temps[i].substring(2)+"`";
						}
					}
				}
				else
				{
					
					codeid="0";
				}
			}
			else
			{
				if(codeid.indexOf("UN")!=-1||codeid.indexOf("un")!=-1||codeid.indexOf("UM")!=-1||codeid.indexOf("um")!=-1)
				{
				
					if(codeid.indexOf("`")==-1)
						codeid=codeid.substring(2);
					else
					{
						String[] temps=codeid.split("`");
						String _str="";
	           			for(int i=0;i<temps.length;i++)
	           				_str+=temps[i].substring(2)+"`";
	           			codeid=_str;
					}
				}
			}
			String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
			String isPhoneField=email_phone.split("/")[1];
			String isMailField=email_phone.split("/")[0];
			ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
			PersonnelEmploy personnelEmploy=new PersonnelEmploy(this.getFrameconn());
			personnelEmploy.setTestTemplatAdvance(testTemplatAdvance);
			ArrayList columnsList=personnelEmploy.getColumnList(isMailField,isPhoneField,dbname,"1");
			ArrayList personnelEmployList=personnelEmploy.getPersonnelEmployList(codeid,columnsList,dbname,extendWhereSql,orderSql);
			
			String businessTemplateIds="";
			String type="0";
			ArrayList tlist= new ArrayList();
			if(map!=null&&map.get("business_template")!=null);
			{
				businessTemplateIds=(String)map.get("business_template");
			}  
			if(businessTemplateIds!=null&&!"".equals(businessTemplateIds))
			{
				type="1";
				tlist=personnelEmploy.getBusinessTemplate(businessTemplateIds);
			}else
			{
				tlist=personnelEmploy.getDbnameList();
			}
			this.getFormHM().put("type", type);
			this.getFormHM().put("templateList", tlist);
			this.getFormHM().put("username",this.getUserView().getUserName());
			this.getFormHM().put("dbName",dbname);
			this.getFormHM().put("columnsList",columnsList);
			this.getFormHM().put("isMailField",isMailField);
			this.getFormHM().put("isPhoneField",isPhoneField);
			this.getFormHM().put("codeid",codeid);
			this.getFormHM().put("personnelEmployList",personnelEmployList);
			hm.remove("operate");
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
