/*
 * Created on 2005-9-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_employ;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchZpEmployListTrans</p>
 * <p>Description:查询录用员工列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchZpEmployListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		   RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
	       if(rv!=null)
	       {
			    String dbpre = rv.getString("str_value");
			    String toTable=(String)this.getFormHM().get("toTable");
			    String toA0100=(String)this.getFormHM().get("toA0100");
				StringBuffer strsql=new StringBuffer();
			    strsql.append("select b.pos_id,a.a0100,a.status from zp_pos_tache a,zp_position b where a.zp_pos_id = b.zp_pos_id and a.tache_id = 3");
			    ContentDAO dao=new ContentDAO(this.getFrameconn());
			    ArrayList list=new ArrayList();
			    ArrayList tempList=new ArrayList();
			    boolean flag = false;
			    try
			    {
			      ResultSet rs = dao.search(strsql.toString(),list);
			      while(rs.next())
			      {
			          DynaBean vo=new LazyDynaBean();
			          vo.set("pos_id",rs.getString("pos_id"));
			          vo.set("a0100",rs.getString("a0100"));
			          vo.set("status",rs.getString("status"));
			          String sql = "select a0101 from "+dbpre+"a01 where a0100 = '"+rs.getString("a0100")+"'";
			          this.frowset = dao.search(sql);
			          while(this.frowset.next()){
			          	  if(this.getFrowset().getString("a0101") == null || "".equals(this.getFrowset().getString("a0101"))){
			          	     vo.set("a0101",""); 
			          	  }else{
			          	     vo.set("a0101",this.getFrowset().getString("a0101")); 
			          	  }
			          	  flag = true;
			          }
			          if(flag == false){
			          	 sql = "select a0101 from "+toTable+" where a0100 = '"+toA0100+"'";
			          	 ResultSet rst  = dao .search(sql,tempList);
			          	 while(rst.next()){
			          	 	if(rst.getString("a0101") == null || "".equals(rst.getString("a0101"))){
			          	 	   vo.set("a0101",""); 
			          	 	}else{
			          	 	   vo.set("a0101",rst.getString("a0101")); 
			          	 	}
			          	 }
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
			        this.getFormHM().put("zpEmploylist",list); 
			        this.getFormHM().put("dbpre",dbpre);
			    }
		    }else
		    {
		    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
		    }

	}

}
