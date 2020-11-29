/*
 * Created on 2005-7-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteOrgdetailInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		 String setname = (String)this.getFormHM().get("setname");
		 //System.out.println("delete ");
	        if(selfinfolist==null||selfinfolist.size()==0)
	            return;
	        RecordVo vo = (RecordVo)selfinfolist.get(0);
	        if(!vo.getModelName().equalsIgnoreCase(setname))
	        	return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
	        {
	            dao.deleteValueObject(selfinfolist);
	            
	            ArrayList dblist = userView.getPrivDbList();
	            String emp_e = (String)this.getFormHM().get("emp_e");
	            String b0110 = (String)this.getFormHM().get("b0110");
	            b0110 = PubFunc.nullToStr(b0110);
	            String linkNum = (String)this.getFormHM().get("link_field");
	            linkNum = PubFunc.nullToStr(linkNum);
	            if(" ".equals(b0110) || " ".equals(linkNum) )
	            	return;
	            for(int k = 0; k<dblist.size();k++){
	            	
	            	String sql = " delete from "+dblist.get(k)+emp_e+" where "+b0110+"=? and "+linkNum+"=?";
	            		ArrayList valueList = new ArrayList();
		            for(int i=0;i<selfinfolist.size();i++){
		            	RecordVo item = (RecordVo)selfinfolist.get(i);
		            	ArrayList rowValue = new ArrayList();
		            	rowValue.add(item.getString("b0110"));
		            	rowValue.add(item.getString("i9999"));
		            	valueList.add(rowValue);
		            }
		            dao.batchUpdate(sql, valueList);
	            }
	        }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }

	}

}
