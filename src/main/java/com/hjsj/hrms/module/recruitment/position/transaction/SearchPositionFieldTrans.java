package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取招聘职位栏目设置能够添加的指标
 * @author Administrator
 *	20170213
 */
public class SearchPositionFieldTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		ArrayList schemeList = (ArrayList)this.formHM.get("schemeList"); //栏目 指标 id号  wang 20170715
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Z03",0);
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		String itemid = "";
		for (FieldItem item : fieldList) {
			map = new HashMap();
			itemid = item.getItemid(); 
			if("0".equalsIgnoreCase(item.getUseflag()))
				continue;
			
			if("0".equalsIgnoreCase(item.getState()))
				continue;
			
			if("z0351".equalsIgnoreCase(itemid))
				continue;
			if("z0381".equalsIgnoreCase(itemid))
				continue;

			if(schemeList.contains(itemid))// 添加按钮 显示的指标 不显示 栏目存在的指标 wangb  20170715  28107
				continue;
			map.put("id", itemid);
            map.put("fieldItemId", itemid);
            map.put("text", item.getItemdesc());
            map.put("fieldItemType", item.getItemtype());
            map.put("fieldSetId", item.getFieldsetid());
            map.put("checked", false);
            map.put("leaf",Boolean.TRUE);
            list.add(map);
		}
		this.formHM.put("children", list);
	}

}
