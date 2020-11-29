package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @ClassName: ItemCorrespondTrans
 * @Description: TODO指标对应
 * @author xmsh
 * @date 2013-12-27 上午11:32:04
 * 
 */
public class ItemCorrespondTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String resumeset = null;
		String fieldSet = null;

		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(this.getFrameconn());
		EmployResumeBo bo = new EmployResumeBo(this.getFrameconn());
		ArrayList itemIDList = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		ArrayList contentList = new ArrayList();
		ArrayList itemList = new ArrayList();

		try {
			resumeset = (String) hm.get("resumeset");
			resumeset = SafeCode.decode(resumeset);
			fieldSet = (String) hm.get("fieldset");
			// 检测数据库中的xml子集指标是否修改

			resumeImportSchemeXmlBo.checkMenu(resumeset); // 设置数据库中xml的menu

			ArrayList itemXmlList = resumeImportSchemeXmlBo.getSchemeitemXmlList(resumeset);
			// itemIDList = bo.getitemIDList(fieldSet);
			itemIDList = DataDictionary.getFieldList(fieldSet, Constant.USED_FIELD_SET);

			itemList.add(new CommonData("", "请选择..."));
			if (itemIDList != null) {
				for (int i = 0; i < itemIDList.size(); i++) {
					FieldItem set = (FieldItem) itemIDList.get(i);
					CommonData cd = new CommonData();

					cd.setDataName(set.getItemid().toUpperCase() + ":" + set.getItemdesc());
					cd.setDataValue(set.getItemid().toUpperCase());
					itemList.add(cd);
				}
			}
			String validAll="true";//用于验证是否全选 true：全选  false：没有全选
			for (int i = 0; i < itemXmlList.size(); i++) {
				bean = (LazyDynaBean) itemXmlList.get(i);
				if (bean.get("resumefld") != null) {
					String valid = "true";
					LazyDynaBean contentBean = new LazyDynaBean();

					contentBean.set("resumefld", bean.get("resumefld"));
					contentBean.set("ehrfld", bean.get("ehrfld"));
					if ("1".equals(bean.get("valid"))) {
						valid = "false";
					}
					if("true".equalsIgnoreCase(valid)){
					    validAll="false";
					}
					contentBean.set("itemList", itemList);
					contentBean.set("valid", bo.getCheckboxHtml((String) bean.get("resumefld"), valid));
					contentBean.set("iselected", (String) bean.get("ehrfld"));
					contentList.add(contentBean);
				}
			}
			this.getFormHM().put("validAll", validAll);
			this.getFormHM().put("itemlist", contentList);
			this.getFormHM().put("resumeset", resumeset);
			this.getFormHM().put("ilist", itemList);
			this.getFormHM().put("fieldset", fieldSet);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
