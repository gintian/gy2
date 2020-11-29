package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:SearchNewLeaderTrans.java</p>
 * <p>Description>:SearchNewLeaderTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 22, 2010  8:59:13 AM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchNewLeaderTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			ArrayList list = new ArrayList();
			String field = SystemConfig.getPropertyValue("recommend_visible_field");
			if(field!=null&&!"".equals(field))
			{
				String arr[]=field.split(",");
				for(int i=0;i<arr.length;i++)
				{
					FieldItem item  = DataDictionary.getFieldItem(arr[i].toLowerCase());
					if((item!=null&& "1".equals(item.getUseflag()))|| "b0110".equalsIgnoreCase(arr[i])|| "e0122".equalsIgnoreCase(arr[i])|| "e01a1".equalsIgnoreCase(arr[i]))
						list.add(item);
				}
			}
			String newLeaderField = SystemConfig.getPropertyValue("recommend_flag_item");
			if(newLeaderField==null|| "".equals(newLeaderField))
				throw GeneralExceptionHandler.Handle(new Exception("系统未设置新选拔任用领导标识参数！"));
			FieldItem fi =  DataDictionary.getFieldItem(newLeaderField.toLowerCase());
			if(fi==null|| "0".equals(fi.getUseflag()))
				throw GeneralExceptionHandler.Handle(new Exception("系统设置的新选拔任用领导标识参数无效！"));
			CommendTableBo ctb = new CommendTableBo(this.getFrameconn(),this.getUserView());
			ctb.createNewLeaderResultTable();
			ArrayList newLeaderList = ctb.getLeaderList(list, newLeaderField);
			String newLeaderStatus = ctb.getNewLeaderStatus();
			this.getFormHM().put("fieldList", list);
			this.getFormHM().put("newLeaderList", newLeaderList);
			this.getFormHM().put("newLeaderStatus", newLeaderStatus);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
