package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.query.QueryUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * <p>Title:GetExcelFieldListTrans.java</p>
 * <p>Description>:GetExcelFieldListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 29, 2010  2:56:36 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetExcelFieldListTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String infokind=(String)this.getFormHM().get("infokind");
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			if(fieldsetid==null|| "".equals(fieldsetid))
			{
				
				ArrayList fieldsetlist = null;
				ArrayList itemlist=new ArrayList();
				if("1".equals(infokind))
					fieldsetlist = QueryUtils.getFieldSetListByInfokind(Constant.EMPLOY_FIELD_SET,this.userView);
				else if("2".equals(infokind))
					fieldsetlist = QueryUtils.getFieldSetListByInfokind(Constant.UNIT_FIELD_SET,this.userView);
				else if("3".equals(infokind))
					fieldsetlist=QueryUtils.getFieldSetListByInfokind(Constant.POS_FIELD_SET,this.userView);
				else
					fieldsetlist=new ArrayList();
				if(fieldsetlist.size()>0)
				{
					CommonData cd=(CommonData)fieldsetlist.get(0);
					String setid=cd.getDataValue();
					itemlist=QueryUtils.getFieldItemList(setid,this.userView);
				}
				this.getFormHM().put("setlist", fieldsetlist);
				this.getFormHM().put("itemlist", itemlist);
			}
			else
			{
				ArrayList itemlist=QueryUtils.getFieldItemList(fieldsetid,this.userView);
				this.getFormHM().put("itemlist", itemlist);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
