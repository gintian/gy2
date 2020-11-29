package com.hjsj.hrms.transaction.ht.ctstatic;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>
 * Title:SetFldsTrans.java
 * </p>
 * <p>
 * Description:合同统计设置指标
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-18 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SetFldsTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	String subSet =(String) this.getFormHM().get("subSet");
	ConstantXml xml = new ConstantXml(this.frameconn, "HT_PARAM", "Params");
	String mfields = xml.getTextValue("/Params/mfield");
	ArrayList fieldlist = this.userView.getPrivFieldList("A01", Constant.USED_FIELD_SET);
	ArrayList fieldsSet = new ArrayList();
	//增加单位，部门，岗位，姓名四个字段
	CommonData umun = new CommonData("A01:B0110:单位名称", "单位名称");
	fieldsSet.add(umun);
	umun = new CommonData("A01:E0122:部门", "部门");
	fieldsSet.add(umun);
	umun = new CommonData("A01:E01A1:岗位名称", "岗位名称");
	fieldsSet.add(umun);
	umun = new CommonData("A01:A0101:姓名", "姓名");
	fieldsSet.add(umun);
	for (int i = 0; i < fieldlist.size(); i++)
	{
	    FieldItem item = (FieldItem) fieldlist.get(i);
	    String itemid = item.getItemid();
	    String itemdesc = item.getItemdesc();
	    if (mfields.indexOf(itemid) > -1)
	    {
//		CommonData temp = new CommonData("A01:" + itemid+":"+itemdesc, itemdesc);
//		fieldsSet.add(temp);
	    	
	    	//不重复增加 单位、部门、岗位、姓名四个字段
	    	if ((!"A0101".equalsIgnoreCase(itemid)) && (!"E0122".equalsIgnoreCase(itemid)) && (!"E01A1".equalsIgnoreCase(itemid)) && (!"B0110".equalsIgnoreCase(itemid))) {
	    		CommonData temp = new CommonData("A01:" + itemid+":"+itemdesc, itemdesc);
	    		fieldsSet.add(temp);
	    	}
	    }
	}

	/** 获取子集指标 */
	ArrayList fieldList = DataDictionary.getFieldList(subSet, Constant.USED_FIELD_SET);
	for (int i = 0; i < fieldList.size(); i++)// 循环字段
	{
	    FieldItem fieldItem = (FieldItem) fieldList.get(i);
	    String itemid = fieldItem.getItemid();
	    String itemName = fieldItem.getItemdesc();
	    
	    CommonData temp = new CommonData(subSet+":" + itemid+":"+itemName, itemName);
	    fieldsSet.add(temp);	    
	}
	this.getFormHM().put("fieldsSet", fieldsSet);

    }

}
