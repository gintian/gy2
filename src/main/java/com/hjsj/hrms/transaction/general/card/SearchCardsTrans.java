/*
 * Created on 2006-1-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.common.commonfunction;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;





/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchCardsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		ArrayList dblist=userView.getPrivDbList();
		 String dbcond=commonfunction.getDbcondString(dblist);
		this.getFormHM().put("dbcond",dbcond);
		//获得等级表的类型A人员B机构K职位
	    String tabid=(String)hm.get("tabid");
	    if(tabid==null)
	    	tabid=(String)this.getFormHM().get("tabid");
	    String strInfkind=(String)hm.get("inforkind");
	    if(strInfkind!=null)
			this.getFormHM().put("inforkind",strInfkind);
	   // System.out.println(strInfkind);
	    //String a0100=(String)hm.get("a0100");
	    this.getFormHM().put("tabid",tabid);
	    //this.getFormHM().put("a0100",this.userView.getA0100());
	    this.getFormHM().put("currentpage","0");	    
	    this.getFormHM().put("pageWidth", getPageWidth(String.valueOf(tabid)));
	}

	private int getPageWidth(String tabid){
    	ContentDAO dao=new ContentDAO(this.frameconn);
    	RowSet rs=null;
    	int width=0;
		 String sql="select paperH,paperori,paperW,lMargin,rmargin from rname where tabid='"+tabid+"'";
	     	try
	     	{
	     		rs=dao.search(sql);
	     		float w=0;
	     		if(rs.next())
	     		{
	     			String ori=rs.getString("paperori");
	     			if(ori==null||ori.length()<=0)
	     				ori="1";
	     			if("2".equals(ori))
	     				w=rs.getFloat("paperH")+rs.getFloat("lMargin")+rs.getFloat("rmargin");
	     			else
	     				w=rs.getFloat("paperW")+rs.getFloat("lMargin")+rs.getFloat("rmargin");
	     		}
	     		w=w*0.0393701f;
	     		w=w*96f;
	     		width=(int)w;
	     	}catch(Exception e)
	     	{
	     		e.printStackTrace();
	     	}finally{
	     		PubFunc.closeDbObj(rs);
	     	}
			return width;
	
	
	}
}
