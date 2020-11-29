package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class PersonFilterTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String selectFields=(String)this.getFormHM().get("rightFields");
			String condid=(String)this.getFormHM().get("filterCondId");
			String salaryid=(String)this.getFormHM().get("salaryid");
			HashMap requestPamaHM =(HashMap)this.getFormHM().get("requestPamaHM");
			ArrayList list = new ArrayList();
			ArrayList fieldlist = new ArrayList();
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			String model=(String) this.getFormHM().get("model");//history 表示为薪资历史数据分析进入
			String[] fields = selectFields.split(",");
			String expr = "";
			StringBuffer strexpr = new StringBuffer();
			LazyDynaBean bean = null;
			int j = 0;
			String tableName = this.userView.getUserName() + "_salary_" + salaryid;
			if(!"history".equalsIgnoreCase(model)) {
				String xml = bo.getXml(salaryid);
				SalaryLProgramBo slpb = new SalaryLProgramBo(xml);
				HashMap hm = slpb.getServiceItemMap();
				String str = (String) hm.get(condid);
				HashMap map = bo.getCondField(str, salaryid);

				for (int i = 0; i < fields.length; i++) {
					if (str == null || str.length() <= 0)
						expr += "*" + (i + 1);
					String fieldname = fields[i];
					if (fieldname == null || "".equals(fieldname))
						continue;
					bean = bo.getFiledItemProperty(salaryid, fieldname);
					Factor factor = null;
					if (bean != null) {
						/**选中的指标列表*/
						CommonData vo = new CommonData();
						vo.setDataName((String) bean.get("itemdesc"));
						vo.setDataValue((String) bean.get("itemid"));
						fieldlist.add(vo);
					}
					factor = new Factor(1);
					factor.setCodeid((String) bean.get("codesetid"));
					factor.setFieldname((String) bean.get("itemid"));
					factor.setHz((String) bean.get("itemdesc"));
					factor.setFieldtype((String) bean.get("itemtype"));
					String s = (String) bean.get("itemlength");
					factor.setItemlen(Integer.parseInt((String) bean.get("itemlength")));

					factor.setItemdecimal(Integer.parseInt((String) bean.get("decwidth")));
					if (map.get(fieldname.toUpperCase() + i) != null) {
						LazyDynaBean abean = (LazyDynaBean) map.get(fieldname.toUpperCase() + i);
						factor.setValue((String) abean.get("value"));
						//factor.setLog((String)abean.get("log"));
						factor.setOper((String) abean.get("oper"));
						if ("A".equalsIgnoreCase((String) bean.get("itemtype")) && !"0".equals((String) bean.get("codesetid"))) {
							factor.setHzvalue(AdminCode.getCodeName((String) bean.get("codesetid"), (String) abean.get("value")));
						}
					} else {
						factor.setOper("=");// default
						//factor.setLog("*");// default
					}
					list.add(factor);
					++j;
					strexpr.append(j);
					strexpr.append("*");
				}
				if (strexpr.length() > 0)
					strexpr.setLength(strexpr.length() - 1);
				if (str != null && str.length() > 0)
					expr = str.substring(0, str.indexOf("|"));
				else if (expr.length() > 0)
					expr = expr.substring(1);
			}else{
				tableName="salaryarchive";
				HistoryDataBo hbo = new HistoryDataBo(this.getFrameconn(), this.getUserView());
				HashMap hm = hbo.getServiceItemMapFromHistory();
				String str = (String) hm.get(condid);
				HashMap map = bo.getCondField(str, "");
				for (int i = 0; i < fields.length; i++) {
					if (str == null || str.length() <= 0)
						expr += "*" + (i + 1);
					String fieldname = fields[i];
					if (fieldname == null || "".equals(fieldname))
						continue;

					bean = hbo.getFiledItemProperty(fieldname);
					Factor factor = null;
					if (bean != null) {
						/**选中的指标列表*/
						CommonData vo = new CommonData();
						vo.setDataName((String) bean.get("itemdesc"));
						vo.setDataValue((String) bean.get("itemid"));
						fieldlist.add(vo);
					}
					factor = new Factor(1);
					factor.setCodeid((String) bean.get("codesetid"));
					factor.setFieldname((String) bean.get("itemid"));
					factor.setHz((String) bean.get("itemdesc"));
					factor.setFieldtype((String) bean.get("itemtype"));
					factor.setItemlen(Integer.parseInt(String.valueOf( bean.get("itemlength"))));
					factor.setItemdecimal(Integer.parseInt(String.valueOf( bean.get("decwidth"))));
					if (map.get(fieldname.toUpperCase() + i) != null) {
						LazyDynaBean abean = (LazyDynaBean) map.get(fieldname.toUpperCase() + i);
						factor.setValue((String) abean.get("value"));
						factor.setOper((String) abean.get("oper"));
						if ("A".equalsIgnoreCase((String) bean.get("itemtype")) && !"0".equals((String) bean.get("codesetid"))) {
							factor.setHzvalue(AdminCode.getCodeName((String) bean.get("codesetid"), (String) abean.get("value")));
						}
					} else {
						factor.setOper("=");// default
					}
					list.add(factor);
					++j;
					strexpr.append(j);
					strexpr.append("*");
				}
				if (strexpr.length() > 0)
					strexpr.setLength(strexpr.length() - 1);
				if (str != null && str.length() > 0)
					expr = str.substring(0, str.indexOf("|"));
				else if (expr.length() > 0)
					expr = expr.substring(1);
				this.getFormHM().put("model",model);
				requestPamaHM.put("model","");
			}

			//--list
			this.getFormHM().put("personFilterList",list);
			this.getFormHM().put("selectedFieldList",fieldlist);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("filterSql","");
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("rightFields",selectFields);
			this.getFormHM().put("issave","1");
			this.getFormHM().put("filterCondId",condid);
			this.getFormHM().put("expr",expr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
