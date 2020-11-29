/*
 * Created on 2006-2-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.statics.singlestatic;

import com.hjsj.hrms.businessobject.general.statics.singlestatic.SingleStaticBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveStaticTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String user=userView.getUserName();
		ArrayList valuelist=new ArrayList();
		String where_str = (String)this.getFormHM().get("where_str");
		String dbpre = (String)this.getFormHM().get("dbpre");
		SingleStaticBo singlestaticbo=new SingleStaticBo(this.getFrameconn(),this.userView);
		if(dbpre==null||dbpre.length()<=0)
			return;
		String sql=null;
		if("b".equalsIgnoreCase(dbpre))
			sql="select  b01.b0110 " + where_str;
		else if("k".equalsIgnoreCase(dbpre))
			sql="select k01.e01a1 " + where_str;
		else 
		 sql="select b,b0110,db " + where_str;
		try{
			List rs = ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
			for(int i=0;i<rs.size();i++)
			{
				valuelist.add(rs.get(i));
			}
		}catch(Exception sqle)
		{
			 sqle.printStackTrace();
	    	 throw GeneralExceptionHandler.Handle(sqle);   
		}		
		singlestaticbo.savesinglestatic(dbpre,valuelist,user);
	}
}
