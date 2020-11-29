package com.hjsj.hrms.transaction.ht.param;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * <p>
 * Title:SearchContractPramTrans.java
 * </p>
 * <p>
 * Description:合同参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchContractPramTrans extends IBusiness {
	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String menuid = (String) hm.get("menuid");

		ContractBo bo = new ContractBo(this.frameconn, this.userView);
		ConstantXml xml = new ConstantXml(this.frameconn, "HT_PARAM", "Params");
		if (menuid != null && "1".equals(menuid)) {
			String nbaseStr = xml.getTextValue("/Params/nbase");
			String[] nbaseArray = nbaseStr.split(",");
			HashMap nbaseMap = new HashMap();
			for (int i = 0; i < nbaseArray.length; i++)
				nbaseMap.put(nbaseArray[i].toLowerCase(), nbaseArray[i]);

			ArrayList nbaseList = bo.searchNbase(nbaseMap, this.userView);
			this.getFormHM().put("nbase", nbaseList);
		}

		if (menuid != null && "2".equals(menuid)) {
			String mfields = xml.getTextValue("/Params/mfield");
			ArrayList fieldlist = this.userView.getPrivFieldList("A01",
					Constant.USED_FIELD_SET);
			ArrayList empIndex = new ArrayList();
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem item = (FieldItem) fieldlist.get(i);
				String itemid = item.getItemid();
				String itemdesc = item.getItemdesc();
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", itemid);
				abean.set("itemdesc", itemdesc);
				if (mfields.indexOf(itemid) == -1)
					abean.set("indexsel", "0");
				else
					abean.set("indexsel", "1");
				empIndex.add(abean);
			}
			this.getFormHM().put("empIndex", empIndex);
		}

		if (menuid != null && "3".equals(menuid)) {
			// 合同相关子集列表
			ArrayList htRelSubSet = new ArrayList();
			// String htset = xml.getTextValue("/Params/htset");
			// htset=htset==null?"":htset;

			String[] itemSel = (String[]) this.getFormHM().get("right_fields");
			String htset = "";
			if (itemSel == null) {
				htset = xml.getTextValue("/Params/htset");
				htset = htset == null ? "" : htset;
			} else {
				StringBuffer htSetStr = new StringBuffer();
				for (int i = 0; i < itemSel.length; i++) {
					htSetStr.append(",");
					htSetStr.append(itemSel[i]);
				}
				htset = htSetStr.substring(1);
			}

			// 人员信息集已构库的子集
			ArrayList empSubSet = new ArrayList();
			HashMap empSubSetMap = new HashMap();
			ArrayList list = this.userView
					.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			if (list.size() == 0) {
				throw new GeneralException("没有子集权限！");
			}
			ArrayList sortList = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				FieldSet fieldset = (FieldSet) list.get(i);

				if ("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				if ("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				if ("A01".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				ArrayList checklist = this.userView.getPrivFieldList(
						fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
				if (checklist.size() < 1)
					continue;
				CommonData temp = new CommonData(fieldset.getFieldsetid(),
						fieldset.getFieldsetid() + ":"
								+ fieldset.getCustomdesc());
				empSubSetMap.put(fieldset.getFieldsetid(), temp);
				sortList.add(fieldset.getFieldsetid());
				if (htset.indexOf(fieldset.getFieldsetid()) != -1)
					htRelSubSet.add(temp);

			}

			Collections.sort(sortList);
			for (int i = 0; i < sortList.size(); i++)
				empSubSet.add(empSubSetMap.get((String) sortList.get(i)));

			this.getFormHM().put("empSubSet", empSubSet);
			// this.getFormHM().put("empSubSet", bo.searchEmpSubSet());
			this.getFormHM().put("htRelSubSet", htRelSubSet);

			// 已选择的合同子集
			String htmain = (String) this.getFormHM().get("htSubSet");
			if (htmain == null || htmain.length() <= 0) {
				htmain = xml.getTextValue("/Params/htmain");
				htmain = htmain == null ? "" : htmain;
			}

			// 查看保存的指标是否有权限
			ArrayList fieldList = this.userView
					.getPrivFieldSetList(Constant.USED_FIELD_SET);
			boolean flag = false;
			for (int i = 0; i < fieldList.size(); i++) {
				FieldSet fieldset = (FieldSet) fieldList.get(i);
				if (fieldset.getFieldsetid().equalsIgnoreCase(htmain)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				htmain = "";
			}

			if (htmain == null || htmain.length() <= 0) {
				if (sortList.size() > 0) {
					htmain = (String) sortList.get(0);
				}
			}
			//新增判断
			if("".equalsIgnoreCase(htmain)){
				throw GeneralExceptionHandler.Handle(new GeneralException("您还没有合同信息集，请先构库后再操作或者您没有改权限！"));
			}
			this.getFormHM().put("htSubSet", htmain);

			// 合同标识代码类
			String httype = xml.getTextValue("/Params/httype");
			httype = httype == null ? "" : httype;
			this.getFormHM().put("httype", httype);

			// ArrayList codeset = bo.searchCodeSet();
			ArrayList codeset = bo.searchCodeSet(htmain, this.userView);
			this.getFormHM().put("codeset", codeset);
		}
	}
}
