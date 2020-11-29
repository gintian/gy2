package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
* 
* 类名称：DefineResumeImportSchemeTrans   
* 类描述：定义简历解析导入方案
* Company:HJSJ   
* 创建人：zhaozk
* 创建时间：Nov 18, 2013 3:48:37 PM     
* @version    
*
 */
public class DefineResumeImportSchemeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(this.getFrameconn());

		LazyDynaBean bean = new LazyDynaBean();
		ArrayList contentList = new ArrayList();

		EmployResumeBo bo = new EmployResumeBo(this.getFrameconn());
		ArrayList itemIDList = new ArrayList();//标识指标和次关键指标
		ArrayList modeList = new ArrayList(); // 更新方式
		ArrayList flist = new ArrayList(); // 人员库指标集
		ArrayList fieldSetList = new ArrayList();
		String state = null; // 选择的人员指标集
		try {
			
			ArrayList resumeXmlList = resumeImportSchemeXmlBo.getResumeXmlList();
			ArrayList schemeParameterList = resumeImportSchemeXmlBo.getSchemeParameterList();
			resumeImportSchemeXmlBo.UpdateImportScheme();
			fieldSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);// 所有已构库人员库子集
			flist.add(new CommonData("", "请选择..."));
			for (int i = 0; i < fieldSetList.size(); i++) {
				FieldSet set = (FieldSet) fieldSetList.get(i);
				CommonData cd = new CommonData();

				cd.setDataName(set.getFieldsetid() + ":" + set.getFieldsetdesc());
				cd.setDataValue(set.getFieldsetid());
				flist.add(cd);
			}
			for (int i = 0; i < resumeXmlList.size(); i++) {
				bean = (LazyDynaBean) resumeXmlList.get(i);
				if (bean.get("resumeset") != null) {

					LazyDynaBean contentBean = new LazyDynaBean();

					contentBean.set("resumeset", bean.get("resumeset"));
					contentBean.set("fselected", (String) bean.get("ehrset")); // 选中
					contentBean.set("ehrset", (String) bean.get("ehrset"));
					contentBean.set("edit", bo.getEditHtml((String) bean.get("resumeset"), (String) bean.get("ehrset"),i));
					contentList.add(contentBean);
				}

			}

			itemIDList = bo.getitemIDList();
			modeList = bo.getModeList();

			for (int i = 0; i < schemeParameterList.size(); i++) {
				bean = (LazyDynaBean) schemeParameterList.get(i);
				hm.put("itemID", bean.get("identifyfld"));
				hm.put("secitemID", bean.get("sencondfld"));
				hm.put("mode", bean.get("imptype"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		hm.put("itemIDList", itemIDList);
		hm.put("modelist", modeList);
		hm.put("flist", flist);
		hm.put("codelist", contentList);

	}

}
