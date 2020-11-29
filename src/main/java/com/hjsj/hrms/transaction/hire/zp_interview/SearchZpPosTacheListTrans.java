/*
 * Created on 2005-9-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_interview;

import com.hjsj.hrms.utils.PubFunc;
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
import java.util.HashMap;

/**
 * <p>Title:SearchZpPosTacheListTrans</p>
 * <p>Description:查询候选人列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 16, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchZpPosTacheListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo rvo= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String dbpre = rvo.getString("str_value");
		RecordVo rv=(RecordVo)this.getFormHM().get("zptachevo");
		String tache_id = rv.getString("tache_id");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String zp_pos_id=(String)hm.get("a_id");
		StringBuffer strsql=new StringBuffer();
		if(tache_id == null || "".equals(tache_id)){
	       strsql.append("select zp_pos_id,a0100,apply_date from zp_pos_tache where zp_pos_id = '"+zp_pos_id+"' and tache_id = 1");
		}else{
			 strsql.append("select zp_pos_id,a0100,apply_date from zp_pos_tache where zp_pos_id = '"+zp_pos_id+"' and tache_id = "+tache_id);	
		}
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    ArrayList tempList = new ArrayList();
	    try
	    {
	      ResultSet rs = dao.search(strsql.toString(),list);
	      while(rs.next())
	      {
	          DynaBean vo=new LazyDynaBean();
	          vo.set("zp_pos_id",rs.getString("zp_pos_id"));
	          vo.set("a0100",rs.getString("a0100"));
	          String sql = "select a0101 from "+dbpre+"a01 where a0100 = '"+rs.getString("a0100")+"'";
	          this.frowset = dao.search(sql);
	          while(this.frowset.next()){
	          	  if(this.getFrowset().getString("a0101") == null || "".equals(this.getFrowset().getString("a0101"))){
	          	     vo.set("a0101","");
	          	  }else{
	          	     vo.set("a0101",this.getFrowset().getString("a0101"));
	          	  }
	          	  break;    
	          } 
	          vo.set("apply_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("apply_date"))));
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
	        this.getFormHM().put("zpPosTachelist",list); 
	        this.getFormHM().put("dbpre",dbpre);
	    }

	}

}
