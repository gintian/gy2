package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class InitParameterConfigTrans extends IBusiness {

	public void execute() throws GeneralException {

		try {
			HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
			String returnflag=(String)map.get("returnflag"); 
			this.getFormHM().put("returnflag",returnflag);
			String opt = (String) map.get("opt");
			GrossPayManagement gross = new GrossPayManagement(this.getFrameconn());
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap hm = bo.getValuesMap();
			String setid = "";//
			String sp_flag = "";
			String ctrl_peroid ="";
			String orgid="";
			String deptid = "";
			String ctrl_type = "1";
			String contrlLevelId="1";
			ArrayList dataList = null;
			String un = "ctrl_item";
			String ctrl_by_level="1";
			ArrayList fieldsetlist = gross.fieldsetList();
			String  surplus_compute="0";//封存结余参与计算
			String fc_flag="";
			String ctrlAmountField="";//启用总额控制指标
			if ("init".equals(opt)) {
				if (hm != null) {
						setid = (String) hm.get("setid");
						sp_flag = (String) hm.get("sp_flag");
						ctrl_type = (String) hm.get("ctrl_type");
						ctrl_peroid = (String)hm.get("ctrl_peroid");
						ctrl_by_level=(String)hm.get("ctrl_by_level");
						dataList = (ArrayList) hm.get(un.toLowerCase());
						if(hm.get("surplus_compute")!=null)
							surplus_compute=(String)hm.get("surplus_compute");
						if(hm.get("fc_flag")!=null)
							fc_flag=(String)hm.get("fc_flag");
						if(hm.get("ctrl_field")!=null)
							ctrlAmountField=(String)hm.get("ctrl_field");
						if(hm.get("hs")!=null)
						{
							HashMap hsmap = (HashMap)hm.get("hs");
							orgid = (String)hsmap.get("orgid");
							deptid = (String)hsmap.get("deptid");
							contrlLevelId=(String)hsmap.get("contrlLevelId");
						}
				} else{
					dataList = new ArrayList();
					setid = ((CommonData) fieldsetlist.get(0)).getDataValue();
				}
			}else if("change".equals(opt))
			{
				setid=(String) this.getFormHM().get("fieldsetid");
				if(hm!=null)
				{
					if(setid.equals((String)hm.get("setid")))
					{
						sp_flag = (String) hm.get("sp_flag");
						ctrl_type = (String) hm.get("ctrl_type");
						ctrl_peroid = (String)hm.get("ctrl_peroid");
						ctrl_by_level=(String)hm.get("ctrl_by_level");
						dataList = (ArrayList) hm.get(un.toLowerCase());
						if(hm.get("surplus_compute")!=null)
							surplus_compute=(String)hm.get("surplus_compute");
						if(hm.get("fc_flag")!=null)
							fc_flag=(String)hm.get("fc_flag");
						if(hm.get("ctrl_field")!=null)
							ctrlAmountField=(String)hm.get("ctrl_field");
						if(hm.get("hs")!=null)
						{
							HashMap hsmap = (HashMap)hm.get("hs");
							orgid = (String)hsmap.get("orgid");
							deptid = (String)hsmap.get("deptid");
							contrlLevelId=(String)hsmap.get("contrlLevelId");
						}
					}
					else
					{
						dataList = new ArrayList();
					}
				}
				else{
					dataList = new ArrayList();
				}
			}
			String hasFc="0";
			if(fc_flag!=null&&fc_flag.length()!=0){
				hasFc="1";
				
			}
			this.getFormHM().put("hasFc", hasFc);
			if((setid==null|| "".equals(setid))&&fieldsetlist!=null&&fieldsetlist.size()>0)
				setid = ((CommonData) fieldsetlist.get(0)).getDataValue();
			ArrayList sp_flaglist = gross.spFlagList(setid);
			ArrayList tableList = gross.fielditemList(setid);
			String table = gross.getSelectString(tableList);
			ArrayList orgList =gross.getOrgOrDeptListFromSalaryset("UN");
			ArrayList deptList = gross.getOrgOrDeptListFromSalaryset("UM");
			ArrayList contrlLevelList = gross.getContrlLevelList();
			ArrayList fc_flag_list=gross.getFc_flag_list(setid);
			ArrayList ctrlAmountFieldList=gross.getFc_flag_list("b01");
			this.getFormHM().put("fc_flag_list", fc_flag_list);
			this.getFormHM().put("surplus_compute", surplus_compute);
			this.getFormHM().put("fc_flag", fc_flag);
			this.getFormHM().put("ctrlAmountField", ctrlAmountField);
			this.getFormHM().put("ctrlAmountFieldList",ctrlAmountFieldList);
			this.getFormHM().put("contrlLevelList",contrlLevelList);
			this.getFormHM().put("orgList",orgList);
			this.getFormHM().put("deptList",deptList);
			this.getFormHM().put("table", table);
			this.getFormHM().put("fieldsetlist", fieldsetlist);
			this.getFormHM().put("spflaglist", sp_flaglist);
			this.getFormHM().put("ctrl_type", ctrl_type==null?"":ctrl_type);
			this.getFormHM().put("deptid",deptid==null?"":deptid);
			this.getFormHM().put("orgid",orgid==null?"":orgid);
			this.getFormHM().put("ctrl_peroid", ctrl_peroid==null?"":ctrl_peroid);
			this.getFormHM().put("oldctrl_peroid", ctrl_peroid==null?"":ctrl_peroid);
			this.getFormHM().put("fieldsetid", setid==null?"":setid);
			this.getFormHM().put("spflagid", sp_flag==null?"":sp_flag);
			this.getFormHM().put("contrlLevelId", contrlLevelId);
			this.getFormHM().put("ctrl_by_level", ctrl_by_level==null?"0":ctrl_by_level);
			String strArr = "";
			String formular = "";
			String salaryid="";
			if (dataList != null) {
				strArr = gross.getArrayString(dataList);
				formular =gross.getFormularStr();
				salaryid=gross.getSalarySet();
			}
			this.getFormHM().put("list",dataList==null? new ArrayList():dataList);
			this.getFormHM().put("strArr", strArr==null?"":strArr);
			this.getFormHM().put("formularStr",formular==null?"":formular);
			this.getFormHM().put("salarySet", salaryid);
			/* private String amountAdjustSet;
			    private ArrayList amountAdjustSetList = new ArrayList();
			    *//**项目或分类名称*//*
			    private String amountPlanitemDescField;
			    private ArrayList amountPlanitemDescFieldList = new ArrayList();*/
			ArrayList amountAdjustSetList=new ArrayList();
			for(int j=0;j<fieldsetlist.size();j++)
			{
				CommonData cd=(CommonData)fieldsetlist.get(j);
				if(setid!=null&&setid.trim().length()>0&&cd.getDataValue().equalsIgnoreCase(setid))
					continue;
				amountAdjustSetList.add(cd);
			}
			String amountAdjustSet="";
			if(hm!=null&&hm.get("amountAdjustSet")!=null)
				amountAdjustSet=(String)hm.get("amountAdjustSet");
			String amountPlanitemDescField="";
			if(hm!=null&&hm.get("amountPlanitemDescField")!=null)
				amountPlanitemDescField=(String)hm.get("amountPlanitemDescField");
			ArrayList amountPlanitemDescFieldList=gross.getAmountPlanitemDescFieldList(amountAdjustSet);
			this.getFormHM().put("amountAdjustSet", amountAdjustSet);
			this.getFormHM().put("amountAdjustSetList", amountAdjustSetList);
			this.getFormHM().put("amountPlanitemDescField", amountPlanitemDescField);
			this.getFormHM().put("amountPlanitemDescFieldList", amountPlanitemDescFieldList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
