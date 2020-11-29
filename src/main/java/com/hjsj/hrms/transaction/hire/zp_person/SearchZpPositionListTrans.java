/*
 * Created on 2005-10-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_person;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchZpPositionListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {    	
		HashMap ahm=(HashMap)this.getFormHM().get("requestPamaHM");
		String edition=(String)ahm.get("edition");
		StringBuffer strsql = new StringBuffer();
	    if(edition==null)
	    	strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position");
	    else if("2".equals(edition))
	    {
	    	
	    	strsql.append(" ");
	    }
	    
	    
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    ArrayList countList = new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_position",1);
	          vo.setString("zp_pos_id",this.getFrowset().getString("zp_pos_id"));
	          vo.setString("amount",this.getFrowset().getString("amount"));
	          String sql = "select parentid from organization where codeitemid = '"+this.getFrowset().getString("dept_id")+"'";
	          ArrayList namelist = new ArrayList();
	          ResultSet rs = dao.search(sql,namelist);
	          while(rs.next()){
	          	 vo.setString("dept_id",rs.getString("parentid"));
	          }
	          vo.setString("pos_id",this.getFrowset().getString("pos_id"));
	          vo.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
	          vo.setString("domain",PubFunc.toHtml(this.getFrowset().getString("domain")));
	          String ssql = "select count(a0100) as count from zp_pos_tache where zp_pos_id = '"+this.getFrowset().getString("zp_pos_id")+"'";
	          ResultSet rst = dao.search(ssql,namelist);
	          while(rst.next()){
	          	 vo.setString("plan_id",rst.getString("count"));
	          }
	          list.add(vo);
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("zppersonlist",list);
	        this.getFormHM().put("countList",countList);
	    }

	}

}
