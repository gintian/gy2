package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class ChangeJobTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String codesetid = (String)this.getFormHM().get("codesetid");
		ArrayList fieldlist =new ArrayList();
		try{
			CommonData cd =new CommonData();
			cd.setDataName(ResourceFactory.getProperty("label.select.dot")+"");
			cd.setDataValue("#");
			fieldlist.add(cd);
			String sql="select itemid,itemdesc from fielditem where useflag='1' and  fieldsetid='K01' and codesetid='" + codesetid + "'";
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				cd =new CommonData();
				String itemid=this.frowset.getString("itemid");
				String itemdesc=this.frowset.getString("itemdesc");
				cd.setDataName("("+itemid+")"+itemdesc);
				cd.setDataValue(itemid);
				fieldlist.add(cd);
			}
		}catch(Exception e)
		{
			e.printStackTrace();			
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.formHM.put("fieldlist", fieldlist);
		}
	}
}
