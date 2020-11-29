package com.hjsj.hrms.transaction.gz.gz_accounting.importMen;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:查询 引入单位\部门变动人员 信息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 21, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SearchPersonnelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String salaryid=(String)hm.get("salaryid");
			String isSalaryManager=(String)hm.get("isSalaryManager");
			
			if(salaryid==null)
			{
				salaryid=(String)this.getFormHM().get("salaryid");
				isSalaryManager=(String)this.getFormHM().get("isSalaryManager");
			}
			else
				this.getFormHM().put("fieldItemId","");
			if(this.userView.hasTheFunction("327121201")||this.userView.hasTheFunction("324021201")||this.userView.hasTheFunction("325021201")||this.userView.hasTheFunction("327021201")||this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
				isSalaryManager="Y";
			else
				isSalaryManager="N";
			String mangerCodeValue="-1";
			if("Y".equalsIgnoreCase(isSalaryManager))
				mangerCodeValue="";
			else
			{
				if(this.userView.getManagePrivCode()!=null&&!"".equals(this.userView.getManagePrivCode()))
				{
					if(this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue()))
						mangerCodeValue="";
					else
						mangerCodeValue=this.userView.getManagePrivCodeValue();
				}
			}
			hm.remove("salaryid");
			hm.remove("isSalaryManager");
			ArrayList tableHeadList=new ArrayList();
			ArrayList tableDataList=new ArrayList();
			ArrayList fieldItemList=new ArrayList();
			
			
			GzAmountXMLBo bo=new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map=bo.getValuesMap();
			String fieldSetId=(String)map.get("chg_set");
			String chg_set_context=(String)map.get("chg_set_context");
			SalaryTemplateBo salarybo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			tableHeadList=salarybo.getTableHeadList(chg_set_context);
			fieldItemList=salarybo.getBelowItemList(fieldSetId);
			
			String p_value="";
			String n_value="";
			String p_viewvalue="";
			String fieldItemId="";
			String allRightField="";
			String expr="";
			String factor="";
			String afactor="";
			String isHistory="0";
			String querytype="0";
			if(opt!=null&& "1".equals(opt))
			{
				p_value=(String)this.getFormHM().get("p_value");
				n_value=(String)this.getFormHM().get("n_value");
				p_viewvalue=(String)this.getFormHM().get("p_viewvalue");
				fieldItemId=(String)hm.get("value");
				expr=(String)this.getFormHM().get("expr");
				factor=(String)this.getFormHM().get("factor");
				afactor=factor;
				querytype=(String)hm.get("querytype");
				if("1".equals(querytype))
				{
					isHistory=(String)hm.get("ishistory");
				}
				HashMap dataMap = salarybo.getTableDataMap(tableHeadList,fieldSetId,fieldItemId,PubFunc.getStr(p_value),PubFunc.getStr(n_value),querytype,expr,factor, "1".equals(isHistory)?true:false,isSalaryManager);
				tableDataList=(ArrayList)dataMap.get("list");
				allRightField=(String)dataMap.get("allright");
			}
			this.getFormHM().put("mangerCodeValue", mangerCodeValue);
			this.getFormHM().put("isSalaryManager", isSalaryManager);
			this.getFormHM().put("allRightField", allRightField);
			this.getFormHM().put("fieldSetId", fieldSetId);
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("fieldItemList",fieldItemList);
			this.getFormHM().put("tableHeadList", tableHeadList);
			this.getFormHM().put("tableDataList",tableDataList);
			this.getFormHM().put("p_value",PubFunc.getTagStr(p_value));
			this.getFormHM().put("n_value",PubFunc.getTagStr(n_value));
			/* 薪资发放-编辑-人员引入-引入单位部门变动人员 空指针异常 xiaoyun 2014-9-28 start */
			if(StringUtils.isNotEmpty(fieldItemId)){
				fieldItemId = fieldItemId.replaceAll("／","/");
			}
			/* 薪资发放-编辑-人员引入-引入单位部门变动人员 空指针异常 xiaoyun 2014-9-28 end */
			this.getFormHM().put("fieldItemId", fieldItemId); //20140912  邓灿
			this.getFormHM().put("p_viewvalue",PubFunc.getTagStr(p_viewvalue));
			this.getFormHM().put("expr", expr);
			this.getFormHM().put("factor",afactor);
			this.getFormHM().put("isHistory", isHistory);
			this.getFormHM().put("querytype", querytype);
			hm.remove("opt");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

	}

}
