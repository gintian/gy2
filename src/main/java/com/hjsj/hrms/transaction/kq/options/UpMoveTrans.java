package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class UpMoveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList slist = (ArrayList)this.getFormHM().get("selectedlist");
		 StringBuffer sb=new StringBuffer();
	     StringBuffer sbs=new StringBuffer();
	     ContentDAO dao=new ContentDAO(this.getFrameconn());  
		try
		{
			if(slist!=null&&slist.size()>0)
			{
			 for(int n=0;n<slist.size();n++)
			  {
				RecordVo rvo=(RecordVo)slist.get(n);
				String cc=rvo.getString("item_id");
				int mm=rvo.getInt("displayorder");
				if(mm==1)
				 return;
				sbs.delete(0,sbs.length());
				sbs.append("select item_id  from kq_item where displayorder=");
				sbs.append(mm-1);
				sbs.append(" order by displayorder");
				this.frowset=dao.search(sbs.toString());
				String tem="";
		    	 if(this.frowset.next())
		    	  tem=this.frowset.getString("item_id");
		    	 
		    	 sb.delete(0,sb.length());
		    	 sb.append("update kq_item set displayorder=");
		    	 sb.append(mm);
		    	 sb.append(" where item_id='");
		    	 sb.append(tem);
		    	 sb.append("'");
		    	 
		    	 dao.update(sb.toString());
		    	 sb.delete(0,sb.length());
		    	 sb.append("update kq_item set displayorder=");
		    	 sb.append(mm-1);
		    	 sb.append(" where item_id='");
		    	 sb.append(cc);
		    	 sb.append("'");
		    	 
		    	 dao.update(sb.toString());
			  }
				
			}
	       
		}catch(Exception exx)
		{
			exx.printStackTrace();
		}

	}

}
