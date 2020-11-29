package com.hjsj.hrms.transaction.dtgh.party;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SearchMediaInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		 String h0100 = this.getFormHM().get("codeitemid").toString();
		 String sql = "select h0100,title,i9999 from h00 where h0100='"+h0100+"'"; 
		 ArrayList list = new ArrayList();
		 ContentDAO dao = new ContentDAO(this.frameconn);
		 try{
			 
			 frowset =dao.search(sql);
			if(frowset.next()){
				 LazyDynaBean ldb = new LazyDynaBean();
				 ldb.set("h0100", frowset.getString("h0100"));
				 ldb.set("title", frowset.getString("title"));
				 ldb.set("i9999", frowset.getString("i9999")); 
				 list.add(ldb);
			}
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 this.getFormHM().put("medialist", list);//多媒体list
		 
		 
	}

}