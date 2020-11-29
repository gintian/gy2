package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetRelationSchemeTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		try
		{
			String opt = (String) this.getFormHM().get("opt");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if ("del".equals(opt))
			{
				String ids = (String) this.getFormHM().get("ids");
				String[] temps = ids.split("/");
				StringBuffer whl = new StringBuffer("");
				for (int i = 0; i < temps.length; i++)
					whl.append("," + temps[i]);

				dao.delete("delete from gz_relation where id in (" + whl.substring(1) + ")", new ArrayList());
				StringBuffer context = new StringBuffer();
				context.append(this.userView.getUserName()+"删除了："+whl.substring(1)+"对应关系");
				this.getFormHM().put("@eventlog", context.toString());

			} else if ("enter".equals(opt))
			{
				String id = (String) this.getFormHM().get("id");
				String context = "";
				this.frowset = dao.search("select * from gz_relation where id=" + id);
				if (this.frowset.next())
					context = Sql_switcher.readMemo(this.frowset, "rel");

				String[] temps = context.split("\\|");

				ArrayList oppositeItem = new ArrayList();
				if (temps.length > 0)
				{
					String[] oppositeItemArr = temps[0].split("\\,");
					for (int i = 0; i < oppositeItemArr.length; i++)
					{
						if (oppositeItemArr[i].trim().length() > 0)
							oppositeItem.add(oppositeItemArr[i].trim());
					}
				}

				ArrayList relationItem = new ArrayList();
				if (temps.length > 1)
				{
					String[] relationItemArr = temps[1].substring(1, temps[1].length() - 1).split("\\,\\,");
					for (int i = 0; i < relationItemArr.length; i++)
					{
						if (relationItemArr[i].trim().length() > 0)
						{
							if (relationItemArr.length == 1)
								relationItem.add(relationItemArr[i].trim().substring(0, relationItemArr[i].trim().length()));
							else
								relationItem.add(relationItemArr[i].trim());
						}
					}
				}
				this.getFormHM().put("relationItemList", getArrayList(relationItem));
				this.getFormHM().put("oppositeItemList", getArrayList(oppositeItem));

			}
			this.getFormHM().put("opt", opt);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// 导入数据（关联指标）根据 下拉框的值 得到 对应的下拉框 text列表
	public ArrayList getArrayList(ArrayList itemList)
	{
		ArrayList list = new ArrayList();
		if (itemList == null)
			return list;
		CommonData data = null;
		for (int i = 0; i < itemList.size(); i++)
		{
			String value = (String) itemList.get(i);
			/* 薪资发放：导入/设置对应指标页面 点击【读取对应】，选择方案后，点击【确定】，报错 xiaoyun 2014-9-22 start */
			value = PubFunc.keyWord_reback(value);
			/* 薪资发放：导入/设置对应指标页面 点击【读取对应】，选择方案后，点击【确定】，报错 xiaoyun 2014-9-22 end */
			String[] temps = value.split("=");
			String itemDesc = "";
			if ("NBASE".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "人员库标识";
			else if ("A0100".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "人员编号";
			else if ("A0000".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "人员序号";
			else if ("A00Z2".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "发放日期";
			else if ("A00Z3".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "发放次数";
			else if ("A00Z0".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "归属日期";
			else if ("A00Z1".equalsIgnoreCase(temps[1].trim()))
				itemDesc = "归属次数";
			else
			{
				FieldItem item = DataDictionary.getFieldItem(temps[1].trim());
				itemDesc = item.getItemdesc();
			}

			FieldItem item = DataDictionary.getFieldItem(temps[1].trim());
			String name = temps[0].trim() + "=" + itemDesc;
			// data=new CommonData(value,name);
			list.add(SafeCode.encode(value + "#" + name));
		}
		return list;
	}

}
