package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class PartyFeesFieldSetTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// 获取党组织所有子集
//		ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.USED_FIELD_SET);
		ArrayList fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);//获取权限子集
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		map.put("fieldsetid", "");
		map.put("fieldsetdesc", "请选择...");
		list.add(map);
		ArrayList fieldsetidlist =new ArrayList();
		for (int i = 0; i < fieldsetlist.size(); i++) {
			HashMap fieldsetMap = new HashMap();
			FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
			if (!"1".equalsIgnoreCase(fieldset.getChangeflag()))
				continue;
			if("0".equalsIgnoreCase(fieldset.getUseflag()))//子集未构库,页面不显示  bug 36235 wangb 20180330
				continue;
			fieldsetMap.put("fieldsetid", fieldset.getFieldsetid());
//			fieldsetMap.put("fieldsetdesc", fieldset.getFieldsetdesc());
			fieldsetMap.put("fieldsetdesc", fieldset.getCustomdesc());//子集名称使用构库后的 bug 36235 wangb 20180408
			list.add(fieldsetMap);
			fieldsetidlist.add(fieldset.getFieldsetid());
		}
		this.formHM.put("fieldsetlist", list);

		// 获取党费收缴配置参数
		RecordVo vo = ConstantParamter.getRealConstantVo("PARTY_PARAM");
		HashMap paramMap = new HashMap();
		if (vo != null) {
			if (vo.getString("str_value").toLowerCase() != null
					&& vo.getString("str_value").toLowerCase().trim().length() > 0
					&& vo.getString("str_value").toLowerCase().indexOf("xml") != -1) {
				Document doc = null;
				try {
					doc = PubFunc.generateDom(vo.getString("str_value"));
					Element root = doc.getRootElement();
					Element partyfees = root.getChild("partyfees");// 获取党费收缴配置参数
					if (partyfees == null) {// 没配置
						this.formHM.put("paramlist", "");
					} else {
						String set = partyfees.getAttributeValue("set");
						if(fieldsetidlist.contains(set)){//判断当前用户是否有配置的参数子集
							paramMap.put("setid", set);
							paramMap.put("computeFeesFieldId",partyfees.getAttributeValue("computeFeesField"));
							paramMap.put("payFeesFieldId",  partyfees.getAttributeValue("payFeesField"));
							paramMap.put("payStatusFieldId", partyfees.getAttributeValue("payStatusField"));
							paramMap.put("payTimeFieldId", partyfees.getAttributeValue("payTimeField"));
							paramMap.put("payFeesMessage",  partyfees.getAttributeValue("payFeesMessage"));
							this.formHM.put("paramlist", paramMap);
						}else{
							this.formHM.put("paramlist", "");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 获取配置子集对应指标的数据
		if (paramMap.get("setid") == null) {// 没有配置党费收缴子集参数返回空
			this.formHM.put("feesFieldlist", "");
			this.formHM.put("payStatusFieldlist", "");
			this.formHM.put("payTimeFieldlist", "");
			return;
		}
//		ArrayList fielditemlist = DataDictionary.getFieldList((String) paramMap.get("setid"), Constant.USED_FIELD_SET);
		ArrayList fielditemlist = this.userView.getPrivFieldList((String) paramMap.get("setid"), Constant.USED_FIELD_SET);//获取权限指标
		if (fielditemlist == null)
			fielditemlist = new ArrayList();
		ArrayList feesFieldlist = new ArrayList();
		ArrayList payStatusFieldlist = new ArrayList();
		ArrayList payTimeFieldlist = new ArrayList();
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if("0".equalsIgnoreCase(fielditem.getUseflag()))//未构库指标不能出现在页面
				continue;
			HashMap fielditemmap = new HashMap();
			if ("N".equalsIgnoreCase(fielditem.getItemtype()) && !"次数".equalsIgnoreCase(fielditem.getItemdesc())
					&& fielditem.getDecimalwidth() == 2) {
				fielditemmap.put("itemid", fielditem.getItemid());
				fielditemmap.put("itemdesc", fielditem.getItemdesc());
				feesFieldlist.add(fielditemmap);
				continue;
			}
			if ("A".equalsIgnoreCase(fielditem.getItemtype()) && "45".equalsIgnoreCase(fielditem.getCodesetid())) {
				fielditemmap.put("itemid", fielditem.getItemid());
				fielditemmap.put("itemdesc", fielditem.getItemdesc());
				payStatusFieldlist.add(fielditemmap);
				continue;
			}
			if ("D".equalsIgnoreCase(fielditem.getItemtype()) && fielditem.getItemlength() == 10
					&& !"年月标识".equalsIgnoreCase(fielditem.getItemdesc())) {
				fielditemmap.put("itemid", fielditem.getItemid());
				fielditemmap.put("itemdesc", fielditem.getItemdesc());
				payTimeFieldlist.add(fielditemmap);
			}
		}
		this.formHM.put("feesFieldlist", feesFieldlist);
		this.formHM.put("payStatusFieldlist", payStatusFieldlist);
		this.formHM.put("payTimeFieldlist", payTimeFieldlist);
	}

}
