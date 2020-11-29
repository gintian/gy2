package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>
 * Title:BatchUpdateTrans.java
 * </p>
 * <p>
 * Description:批量更新
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-14 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class BatchUpdateTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String bonusSet = (String) this.getFormHM().get("bonusSet");
	ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
	ArrayList itemList = new ArrayList();// 批量修改指标名称列表
	String itemid1 = "";
	for (int i = 0; i < list.size(); i++)
	{
	    FieldItem fielditem = (FieldItem) list.get(i);
	    Field field = fielditem.cloneField();
	    String itemid = field.getName();
	    if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
		continue;
	    if ("1".equals(this.userView.analyseFieldPriv(itemid, 0)) && "1".equals(this.userView.analyseFieldPriv(itemid, 1)))
		continue;
	    if (!"2".equals(this.userView.analyseTablePriv(bonusSet)))
		continue;

	    if ("处理状态".equals(fielditem.getItemdesc()) || "业务日期".equals(fielditem.getItemdesc()))//只读字段不许修改
		continue;

	    if (itemid1.length() == 0)
		itemid1 = itemid;
	    CommonData dataobj = new CommonData(fielditem.getItemid() + ":" + fielditem.getItemtype() + ":" + fielditem.getCodesetid(), fielditem.getItemdesc());
	    itemList.add(dataobj);
	}

	this.getFormHM().put("itemid", itemid1);
	this.getFormHM().put("itemList", itemList);
	this.getFormHM().put("refItem", "");
	this.getFormHM().put("refItemList", this.refList(""));

    }

    public ArrayList refList(String itemid)
    {

	ArrayList list = new ArrayList();
	if (itemid != null && itemid.trim().length() > 0)
	{
	    FieldItem fielditem = DataDictionary.getFieldItem(itemid);
	    if ("2".equalsIgnoreCase(this.userView.analyseFieldPriv(fielditem.getItemid())))
	    {
		if (!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs")))
		{
		    String fieldtype = fielditem.getItemtype();
		    ArrayList dylist = DataDictionary.getFieldList(fielditem.getFieldsetid(), Constant.USED_FIELD_SET);
		    if (dylist != null)
		    {
			if (fielditem.isCode())
			{
			    for (int i = 0; i < dylist.size(); i++)
			    {
				FieldItem field = (FieldItem) dylist.get(i);
				if (field.isCode())
				{
				    if (field.getCodesetid().equalsIgnoreCase(fielditem.getCodesetid()) && !field.getItemid().equalsIgnoreCase(itemid))
				    {
					CommonData dataobj = new CommonData(field.getItemid(), field.getItemdesc());
					list.add(dataobj);
				    }
				}
			    }
			} else
			{
			    for (int i = 0; i < dylist.size(); i++)
			    {
				FieldItem field = (FieldItem) dylist.get(i);
				if (!field.isCode())
				{
				    if (fieldtype.equalsIgnoreCase(field.getItemtype()))
				    {
					if (!"A".equalsIgnoreCase(fielditem.getItemtype()))
					{
					    if (!fielditem.getItemid().equalsIgnoreCase(field.getItemid()) && fielditem.getItemlength() >= field.getItemlength())
					    {
						CommonData dataobj = new CommonData(field.getItemid(), field.getItemdesc());
						list.add(dataobj);
					    }
					} else
					{
					    if (!fielditem.getItemid().equalsIgnoreCase(field.getItemid()) && fielditem.getItemlength() >= field.getItemlength())
					    {
						CommonData dataobj = new CommonData(field.getItemid(), field.getItemdesc());
						list.add(dataobj);
					    }
					}
				    }
				}
			    }
			}
		    }
		}
	    } else
	    {
		CommonData dataobj = new CommonData("", "");
		list.add(dataobj);
	    }
	} else
	{
	    CommonData dataobj = new CommonData(" ", " ");
	    list.add(dataobj);
	}
	return list;
    }
}
