/*
 * Created on 2005-9-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


/**
 * <p>Title:PersonFilterTrans</p>
 * <p>Description:人员筛选</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 12, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class PersonFilterTrans extends IBusiness {

	 /**组合查询SQL*/
    private String combine_SQL(String pos_cond,String dbpre) throws GeneralException
    {
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        for (int i = 0; i < pos_cond.length(); i++) {
           if("|".equals(pos_cond.substring(i, i + 1))){
           	strexpr.append(pos_cond.substring(0,i));
           	strfactor.append(pos_cond.substring(i+1,pos_cond.length()));
           }
        }
        ArrayList fieldlist=new ArrayList();
      
        FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,false,false,1,userView.getUserName());
        return factorlist.getSqlExpression();
    }
    
	public void execute() throws GeneralException {
		RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String dbpre = "";
		if(rv!=null)
        {
            dbpre=rv.getString("str_value");
        }
		StringBuffer strsql=new StringBuffer();
		/*wlh修改速度和业务问题*/
		strsql.append("select a.zp_pos_id,a.pos_id,c.pos_cond from zp_position a,organization c where  c.codeitemid=a.pos_id");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	      	  boolean flag = false;
	          String pos_id = this.getFrowset().getString("pos_id");
	          String zp_pos_id = this.getFrowset().getString("zp_pos_id");
	          String pos_cond = this.getFrowset().getString("pos_cond");
	          if(pos_cond == null || "".equals(pos_cond))
	          	return;
	          String strwhere=combine_SQL(pos_cond,dbpre);
	          StringBuffer sqle = new StringBuffer();
	          /*删除不符合条件的职位申请*/
	          sqle.append("delete from zp_pos_tache where a0100 not in (select ");
	          sqle.append(dbpre);
	          sqle.append("a01.a0100 ");
	          sqle.append(strwhere);
	          sqle.append(") and zp_pos_id='");
	          sqle.append(zp_pos_id);
	          sqle.append("'");
	          dao.delete(sqle.toString(),list);
	          sqle.delete(0,sqle.length());
	          /*过滤出符合条件的职位申请*/
	          sqle.append("insert into zp_pos_tache(a0100,zp_pos_id,tache_id,thenumber,apply_date,status) select ");
	          sqle.append("a.a0100,'");
	          sqle.append(zp_pos_id);
	          sqle.append("',1,");
	          sqle.append("(SELECT COUNT(*) + 1 FROM zp_pos_tache WHERE a0100 = a.a0100)");
			  sqle.append(",a.CreateTime,'0' from ");
	          sqle.append(dbpre);
	          sqle.append("a01 a where a.a0100 not in(select a0100 from zp_pos_tache where zp_pos_id='");
	          sqle.append(zp_pos_id);
	          sqle.append("') and a.a0100 in (select ");
	          sqle.append(dbpre);
	          sqle.append("a01.a0100 ");
	          sqle.append(strwhere);
	          sqle.append(")");
	          dao.insert(sqle.toString(),new ArrayList());	          
	      }
	  }
	  catch(Exception sqle)
	  {
	    sqle.printStackTrace();
	    throw GeneralExceptionHandler.Handle(sqle);
	  }

	}

}
