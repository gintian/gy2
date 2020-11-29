/*
 * Created on 2006-4-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchFilterPosListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String pos_id_value=(String)this.getFormHM().get("pos_id_value");
		try{
			if(pos_id_value==null || pos_id_value.length()==0)
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				String sql="select min(pos_id) as pos_id from zp_position";
				this.frowset=dao.search(sql);
				if(this.frowset.next())
					pos_id_value=this.frowset.getString("pos_id");
				else
					return;
				this.getFormHM().put("pos_id_value",pos_id_value);
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
		    StringBuffer sqlstr=new StringBuffer();
		    RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
			String dbpre = "";
			if(rv!=null)
	        {
	            dbpre=rv.getString("str_value");
	        }
		    String sqlselect="select a.a0101,a.a0100 ";
		    sqlstr.append(" from ");
		    sqlstr.append(dbpre);
		    sqlstr.append("a01 a,zp_pos_tache,zp_position where a.a0100=zp_pos_tache.a0100 and zp_position.zp_pos_id=zp_pos_tache.zp_pos_id and zp_position.pos_id='");
		    sqlstr.append(pos_id_value);
		    sqlstr.append("'");
		    String sqlcolumns="a0101,a0100";
		    this.getFormHM().put("cond_sql",sqlselect);
		    this.getFormHM().put("strwhere",sqlstr.toString());
		    this.getFormHM().put("columns",sqlcolumns);
	    }catch(Exception e)
		{
	    	e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
	    }

	}

}
