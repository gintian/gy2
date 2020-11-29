package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchAllLawBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Dec 12, 2008:11:52:02 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SearchAllClassTrans extends IBusiness {

	public void execute() throws GeneralException {
	    ArrayList list = new ArrayList();
	    ArrayList kqlist = new ArrayList();
	    KqUtilsClass kqcl = new KqUtilsClass(this.frameconn,this.userView);
		try {
            list = kqcl.getKqClassListInPriv();
            LazyDynaBean ldb = new LazyDynaBean();
            for(int i=0;i<list.size();i++){
                ldb = (LazyDynaBean) list.get(i);
                if("0".equals((String)ldb.get("classId")))
                    continue;
                CommonData ordervo = new CommonData((String)ldb.get("classId"), (String)ldb.get("name"));
                kqlist.add(ordervo);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("kqlist", kqlist);
		}

	}

}
