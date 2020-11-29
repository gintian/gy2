package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StaticBackValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String[] sel=(String[])this.getFormHM().get("selects");
		 String selx=(String)this.getFormHM().get("mess");
		selx = PubFunc.keyWord_reback(selx);
		String texts = (String) this.getFormHM().get("texts");
		texts = PubFunc.keyWord_reback(texts);
		this.getFormHM().put("texts",texts);
		 ArrayList alist=new ArrayList();
	        StringTokenizer st=new StringTokenizer(selx,"," );
	        while(st.hasMoreTokens())
	        {
	        	alist.add(st.nextToken(","));
	        }
		
		 ArrayList rslist=new ArrayList();
		for(int m=0;m<sel.length;m++)
		{
		   CommonData cdata=new CommonData(sel[m],alist.get(m).toString());
		   rslist.add(cdata);
		}

		this.getFormHM().put("mes","3");
	    this.getFormHM().put("rlist",rslist);
	}

}
