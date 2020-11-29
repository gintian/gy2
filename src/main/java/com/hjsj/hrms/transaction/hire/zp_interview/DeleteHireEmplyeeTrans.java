/*
 * Created on 2006-3-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_interview;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteHireEmplyeeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		 String dbpre = rv.getString("str_value");
		 ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
         if(selfinfolist==null||selfinfolist.size()==0)
            return;
         ContentDAO dao=new ContentDAO(this.getFrameconn());
         List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
        StringBuffer  deletesql=new StringBuffer();
        Connection conn = null;
        //Statement stmt = null;
         try
	     {
         	conn=this.getFrameconn();
         	//stmt=conn.createStatement();
         	 for(int i=0;i<selfinfolist.size();i++)
             {
             	LazyDynaBean rec=(LazyDynaBean)selfinfolist.get(i);             	
                 for(int j=0;j<infoSetList.size();j++)
	            { 
                	FieldSet fieldset=(FieldSet)infoSetList.get(j);
                   	deletesql.setLength(0);                	
                	deletesql.append("delete from ");
                	deletesql.append(dbpre);
                	deletesql.append(fieldset.getFieldsetid());
                	deletesql.append(" where a0100='");
                	deletesql.append(rec.get("a0100").toString());
                	deletesql.append("'");
                	dao.update(deletesql.toString());
                   	//String sql = "delete from zp_pos_tache where a0100 = '"+rec.get("a0100").toString()+"'";
                   	//stmt.executeUpdate(sql);
                 }
                deletesql.setLength(0);
            	deletesql.append("delete from ");
            	deletesql.append("zp_exam_report where a0100='");
            	deletesql.append(rec.get("a0100").toString());
            	deletesql.append("'");
            	dao.update(deletesql.toString());
            	deletesql.setLength(0);                	
            	deletesql.append("delete from ");
            	deletesql.append("zp_pos_tache where a0100='");
            	deletesql.append(rec.get("a0100").toString());
            	deletesql.append("'");
            	dao.update(deletesql.toString());
            }
         }
		 catch(SQLException sqle)
		 {
		    sqle.printStackTrace();
		    throw GeneralExceptionHandler.Handle(sqle);
		 }

	}

}
