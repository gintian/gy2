package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchInterviewRevertDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ParameterXMLBo pbo = new ParameterXMLBo(this.getFrameconn(), "1");
			HashMap paraMap= pbo.getAttributeValues();
			String interview_itemid="";
			if(paraMap!=null&&paraMap.get("interviewing_itemid")!=null)
			{
				interview_itemid=(String)paraMap.get("interviewing_itemid");
			}
			else
			{
				throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置面试回复指标！"));
			}
			if("#".equals(interview_itemid)|| "".equals(interview_itemid))
				throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置面试回复指标！"));
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			String codeid= SafeCode.decode((String)map.get("code"));//dml 2011-04-01
			String start_date="";
			String end_date="";
			String value="-1";
			if("2".equals(type)){
				value=(String)this.getFormHM().get("interviewingCodeValue");
				start_date=(String)this.getFormHM().get("start_date");
				end_date=(String)this.getFormHM().get("end_date");
			}else{
				start_date="";
				end_date="";
			}
			String dbName = (String)this.getFormHM().get("dbName");
			String extendWhereSql=(String)this.getFormHM().get("extendWhereSql");
			String orderSql=" order by z0301 ";
			String codeset="";
			if(codeid==null|| "-1".equals(codeid))
			{
				if(!(userView.isAdmin()&& "1".equals(userView.getGroupId())))
				{
					if(this.userView.getStatus()==4/*hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ")*/)
					{
						codeset=this.getUserView().getManagePrivCode();
						codeid=this.getUserView().getManagePrivCodeValue();
						if(codeset==null|| "".equals(codeset))
						{
							codeid="#";
							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
						}					
					}
					else
					{
						if(this.getUserView().getUnit_id()==null|| "".equals(this.getUserView().getUnit_id())|| "UN".equalsIgnoreCase(this.getUserView().getUnit_id()))
						{
							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
						}
						else
						{
							String temp0=this.getUserView().getUnit_id();
							if("".equals(temp0))
							{
								codeid="#";
							}else if(temp0.trim().length()==3)
							{
								codeid="";
							}
							else
							{
								if(temp0.indexOf("'")==-1)
								{
									codeid=temp0.substring(2);
								}
								else
								{
				        			String[] temps=temp0.split("`");
				           			for(int i=0;i<temps.length;i++)
				        				codeid+=temps[i].substring(2)+"`";
								}
							}
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
					codeid=codeid.substring(2);
				}
			}
			if(this.userView.isSuper_admin()&&(codeid==null||codeid.length()==0))
				codeid="0";
			InterviewEvaluatingBo bo = new InterviewEvaluatingBo(this.getFrameconn());
			ArrayList codeVlaueList = bo.getCodeValueList();
			HashMap mp= bo.getInterviewRevertInfoList(codeid, dbName, extendWhereSql, orderSql, value,start_date,end_date);
			this.getFormHM().put("select_sql", (String)mp.get("select"));
			this.getFormHM().put("where_sql", (String)mp.get("where"));
			this.getFormHM().put("order_sql",orderSql );
			this.getFormHM().put("cloumns", (String)mp.get("cloumns"));
			this.getFormHM().put("codesetid", (String)mp.get("codesetid"));
			this.getFormHM().put("interviewingRevertItemCodeList",codeVlaueList);
			this.getFormHM().put("extendWhereSql",extendWhereSql);
			this.getFormHM().put("codeid",codeid);
			this.getFormHM().put("dbName", dbName);
			this.getFormHM().put("interviewingCodeValue", value);
			this.getFormHM().put("start_date", start_date);
			this.getFormHM().put("end_date", end_date);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	
}
