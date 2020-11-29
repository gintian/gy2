package com.hjsj.hrms.module.system.distributedreporting.drbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "fieldItem")
public class DRFieldItemBean {
	@XmlElement(name = "item")
	private List <DRItemBean> itemList;

	public List<DRItemBean> getItemList() {
		return itemList;
	}

	public void setItemList(List<DRItemBean> itemList) {
		this.itemList = itemList;
	}
	
	public List<String> getItemidList(String opTable){
		List<String> itemidList = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++) {
			DRItemBean itemBean = (DRItemBean) itemList.get(i);
			String setid = itemBean.getSetid();
			if(opTable.equalsIgnoreCase(setid)){
				//String itemtype = itemBean.getItemtype();// 字段类型
				String itemid = itemBean.getItemid();// 字段名 
				itemidList.add(itemid);
			}
		}
		return itemidList;
	}
	
	public Map<String, String> getColumnValue(String opTable) {
		Map<String, String> map =new HashMap<String, String>();
		StringBuffer cols = new StringBuffer();
		StringBuffer values = new StringBuffer();
		StringBuffer update_values = new StringBuffer();
		for (int i = 0; i < itemList.size(); i++) {
			DRItemBean bean = (DRItemBean) itemList.get(i);
			String setid = bean.getSetid();
			if (opTable.equalsIgnoreCase(setid)) {
				String itemid = bean.getItemid();// 字段名
				cols.append(","+itemid);
				values.append(",?");
				update_values.append(",");
				update_values.append(itemid+"=?");
			}
		}
		map.put("cols", cols.toString());
		map.put("values", values.toString());
		map.put("update_values", update_values.toString());
		return map;
	}
	
	public List<String> getUniqFieldList(){
		List<String> uniqFieldList = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++) {
			DRItemBean itemBean = (DRItemBean) itemList.get(i);
			String uniq = itemBean.getUniq();
			if("TRUE".equalsIgnoreCase(uniq)){
				String itemid = itemBean.getItemid();// 字段名 
				uniqFieldList.add(itemid);
			}
		}
		return uniqFieldList;
	}
	
	public List<String> getCodeSetItemList(String opTable) {
		List<String> codeItemList = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++) {
			DRItemBean itemBean = (DRItemBean) itemList.get(i);
			String setid = itemBean.getSetid();
			if (opTable.equalsIgnoreCase(setid)) {
				String codeSetId = itemBean.getCodesetid();
				if (!"0".equalsIgnoreCase(codeSetId)) {
					String itemId = itemBean.getItemid();
					codeItemList.add(itemId);
				}
			}
		}
		return codeItemList;
	}
}
