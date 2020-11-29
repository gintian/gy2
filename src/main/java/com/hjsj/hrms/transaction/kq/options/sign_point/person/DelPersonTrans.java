package com.hjsj.hrms.transaction.kq.options.sign_point.person;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
* @author szk
*
*/
public class DelPersonTrans extends IBusiness {

 

	public void execute() throws GeneralException {
		try{
			ArrayList selectfieldlist=(ArrayList)this.getFormHM().get("selectfieldlist");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String pid = (String)this.getFormHM().get("pid");
			pid=pid.substring(1);//传过来的是p1，截掉p
			ArrayList paralist = new ArrayList();
			StringBuffer delsql = new StringBuffer();
			delsql.append("delete from kq_sign_point_emp");
			delsql.append(" where pid=? and a0100=? and nbase=?");
			for(int i=0;i<selectfieldlist.size();i++)
            {
				//cheakbox选定的属性
    		    LazyDynaBean rec=(LazyDynaBean)selectfieldlist.get(i); 
    		    String a0100 = (String) rec.get("a0100");
    		    String nbase = (String) rec.get("nbase");
    		    ArrayList list = new ArrayList();
    		    list.add(pid);
    		    list.add(a0100);
    		    list.add(nbase);
    		    paralist.add(list);
            }
			dao.batchUpdate(delsql.toString(), paralist);
		}
    catch (Exception e) {
    	 e.printStackTrace();
         throw GeneralExceptionHandler.Handle(e);
	}
    }


   
}
