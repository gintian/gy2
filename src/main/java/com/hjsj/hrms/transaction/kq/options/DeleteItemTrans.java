package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteItemTrans  extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList dlist = (ArrayList)this.getFormHM().get("selectedlist");
		if(dlist==null||dlist.size()==0)
            return;
		StringBuffer sb=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
		   for(int m=0;m<dlist.size();m++)
		   {
			   RecordVo rs=(RecordVo)dlist.get(m);
			   String id=rs.getString("item_id");
			   sb.append("select flag from codeitem where codesetid='27' and codeitemid='");
			   sb.append(id);
			   sb.append("'");
			   
			  // System.out.print(sb.toString()+"----"+id);
			   
			   String mm="";
			  this.frowset=dao.search(sb.toString());
	          while(this.frowset.next())
	          {
	        	  mm=  this.frowset.getString("flag");
	          }
	          
	          if(mm.indexOf("0")!=-1)
	          {
	        	  sb.delete(0,sb.length());
	        	  sb.append("delete kq_item where item_id='");
	        	  sb.append(id);
	        	  sb.append("'");
	        	  
	        	  dao.delete(sb.toString(),new ArrayList());  
	          }

		   }
		}catch(Exception exx)
		{
			exx.printStackTrace();
		}
	}

}
