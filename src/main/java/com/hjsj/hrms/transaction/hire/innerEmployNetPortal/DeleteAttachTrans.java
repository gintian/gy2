package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:DeleteAttachTrans.java</p>
 * <p>Description>:DeleteAttachTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 12, 2011  1:41:17 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class DeleteAttachTrans extends IBusiness{
	public void execute() throws GeneralException {
		try{
			String ids = SafeCode.decode((String)this.getFormHM().get("ids"));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String[] arr = ids.split("/");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				
				String[] temp = arr[i].split("`");
				String a0100 = PubFunc.decrypt(temp[0]);
				String nbase = PubFunc.decrypt(temp[2]);
				String i9999 = PubFunc.decrypt(temp[1]);
				
				ArrayList params = new ArrayList();
				params.add(a0100);
				params.add(i9999);
				dao.delete("delete from "+ nbase +"A00 where a0100=? and i9999=?", params);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
