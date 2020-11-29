package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitInterviewArrangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			
			if(hm.get("operateType")!=null)
			{
				this.getFormHM().put("operateType",(String)hm.get("operateType"));
				
			}
			String b_query=(String)hm.get("b_query");
			hm.remove("b_query");
			String codeid=(String)hm.get("code");
			String br_query=(String)hm.get("br_query");
			hm.remove("br_query");
			String operate=(String)hm.get("operate");
			String model=(String)hm.get("model");
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			if(vo==null)
				throw GeneralExceptionHandler.Handle(new Exception("请设置应聘人才库！"));
			String dbname=vo.getString("str_value");  //应用库前缀
			InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
			
			if("0".equals(b_query)|| "1".equals(b_query))
			{
				
				if("0".equals(b_query))
					model="5";
				if("1".equals(b_query))
					model="6";
				//codeid="";
				
			}
			String codeset="";
			if(br_query!=null&&br_query.length()!=0){
				
				codeid=null;
			}
			if(codeid==null|| "-1".equals(codeid))
			{
				if(!(userView.isAdmin()&& "1".equals(userView.getGroupId())))
				{
					/**
					 * modify dml 2012-3-31 15:50:33
					 * reason 因增加业务管理范围导致权限规则改边
					 * */
//					if(this.userView.getStatus()==4/*hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ")*/)
//					{
//						codeset=this.getUserView().getManagePrivCode();
//						codeid=this.getUserView().getManagePrivCodeValue();
//						if(codeset==null||codeset.equals(""))
//						{
//							codeid="#";
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
					
					codeid="";
				}
			}
			else
			{
				if(codeid.indexOf("UN")!=-1||codeid.indexOf("un")!=-1||codeid.indexOf("UM")!=-1||codeid.indexOf("um")!=-1)
				{
				//	codeid=codeid.substring(2);
					
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
			String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=bo2.getAttributeValues();
			if(map!=null&&map.get("resume_state")!=null&&((String)map.get("resume_state")).trim().length()>0)
				resume_state_field=(String)map.get("resume_state");
			
			
		if(resume_state_field==null|| "".equals(resume_state_field)|| "#".equals(resume_state_field))
			throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));
		FieldItem it=DataDictionary.getFieldItem(resume_state_field);
		       
		if(it==null)
			throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));
	   String xx=it.getUseflag();
	   if("0".equals(xx))
			throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));
	    String interviewingRevertItemid="";
	     if(map!=null&&map.get("interviewing_itemid")!=null)
	 	{
			interviewingRevertItemid=(String)map.get("interviewing_itemid");
		} 
			
			interviewEvaluatingBo.changeState(codeid,dbname,resume_state_field);      // 改变人员简历状态，将已选人员 改为 待通知状态
			
			String extendWhereSql=(String)this.getFormHM().get("extendWhereSql");
			if(extendWhereSql!=null)
			    extendWhereSql=PubFunc.decrypt(extendWhereSql);
			
			String orderSql=(String)this.getFormHM().get("orderSql");
			if(orderSql!=null&&orderSql.trim().length()>0){
				orderSql=PubFunc.decrypt(orderSql);
			}else{
				orderSql=" order by Z05.state asc,Z05.z0509 asc";
			}
			
			if((operate!=null&& "init".equals(operate)))
			{
				extendWhereSql="";
				//orderSql=" order by Z05.state asc,Z05.a0100 asc";
				if (Sql_switcher.searchDbServer()!=Constant.ORACEL){
					orderSql=" order by z05.Z0515 desc,Z0509A desc";
				}
				else{
					orderSql=" order by Z05.Z0515 desc,Z05.Z0509 desc";// zzk 2013/12/19  中核华兴  面试默认按面试时间降序排列
				}
				this.getFormHM().put("orderSql",PubFunc.encrypt(orderSql));
				this.getFormHM().put("extendWhereSql"," ");
			}
			
			
			this.getFormHM().put("dbName",dbname);
			String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
			String isPhoneField=email_phone.split("/")[1];
			String isMailField=email_phone.split("/")[0];
			ArrayList list=DataDictionary.getFieldList("Z05",Constant.USED_FIELD_SET);
			ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
			ArrayList removeItemList= new ArrayList();//在面试安排中不可以编辑的字段
			if(testTemplatAdvance!=null){//如果设置了高级测评
				for(int i=0;i<testTemplatAdvance.size();i++){
		            HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
		            String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
		            FieldItem item = DataDictionary.getFieldItem(score_item);
		            if(item!=null){
		                if(removeItemList.contains(score_item.toLowerCase())){
		                	continue;
		                }
		                removeItemList.add(score_item.toLowerCase());
		            }
		        }
		        interviewEvaluatingBo.setRemoveItemList(removeItemList);
			}

	/*		if(b_query.equals("0")||b_query.equals("1"))
			{
				
				if(b_query.equals("0"))
					model="5";
				if(b_query.equals("1"))
					model="6";
		
				if(!(userView.isAdmin()&&userView.getGroupId().equals("1"))){
					if(hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ"))
					{
						codeid=this.getUserView().getManagePrivCodeValue();				
						if(codeid==null||codeid.trim().length()==0)
						{
								String userDeptId=userView.getUserDeptId();
								String userOrgId=userView.getUserOrgId();
								if(userDeptId!=null&&userDeptId.trim().length()>0)
								{
									codeid=userDeptId;
								}
								else if(userOrgId!=null&&userOrgId.trim().length()>0)
								{
									codeid=userOrgId;
								}
						}
					}
					else
					{
						if(this.getUserView().getUnit_id()!=null&&this.getUserView().getUnit_id().trim().length()>1)
						{
							String temp0=this.getUserView().getUnit_id();
							String[] temps=temp0.split("`");
							for(int i=0;i<temps.length;i++)
								codeid+=temps[i].substring(2)+"`";
							//codeid=this.getUserView().getUnit_id().substring(2);
						}
						else
							codeid="#";
					}
				}
				else			
					codeid="0";
			}*/
			
			if(this.userView.isSuper_admin()&&(codeid==null||codeid.length()==0))
				codeid="0";
			
			hm.remove("operateType");
			ArrayList interviewArrangeInfoList=interviewEvaluatingBo.getInterviewArrangeInfoList(codeid,dbname,isMailField,isPhoneField,list,extendWhereSql,orderSql,model,this.userView);
			interviewEvaluatingBo.setAddProfessionalColumnName(true);
			ArrayList columnsList=interviewEvaluatingBo.getColumnList(list,isMailField,isPhoneField,dbname);
			
			this.getFormHM().put("columnsList",columnsList);
			this.getFormHM().put("isMailField",isMailField);
			this.getFormHM().put("isPhoneField",isPhoneField);
			this.getFormHM().put("codeid",codeid);
			this.getFormHM().put("interviewArrangeList",interviewArrangeInfoList);
			this.getFormHM().put("username",this.getUserView().getUserName());
			this.getFormHM().put("linkDesc",b_query);
			this.getFormHM().put("interviewingRevertItemid",interviewingRevertItemid);
			String dbpre_str=interviewEvaluatingBo.getDbpre_str(this.getUserView(), dbname);
			this.getFormHM().put("dbpre_str",dbpre_str);
			hm.remove("operate");
			this.getFormHM().put("extendWhereSql1", PubFunc.encrypt(extendWhereSql));
			if(hm.get("operateType")!=null)
			{
				hm.remove("operateType");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
