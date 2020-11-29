package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 批量签批代码型
 * @author Owner
 * wangyao
 */
public class GroupSubTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			SearchAllApp searchAllApp=new SearchAllApp();
			String table = (String)hm.get("table");
			String ta = table.toLowerCase();
			ArrayList fieldList = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
			for(int i=0;i<fieldList.size();i++)
			{
				FieldItem field=(FieldItem)fieldList.get(i);
				if(field.getItemid().equals(ta+"11"))
				{
					this.getFormHM().put("group11",searchAllApp.getOneList11(field.getItemid(),this.getFrameconn()));
					this.getFormHM().put("unit11", "1");
				}else if(field.getItemid().equals(ta+"15"))
				{
					this.getFormHM().put("group15",searchAllApp.getOneList15(field.getItemid(),this.getFrameconn()));
					this.getFormHM().put("unit15", "2");
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
