/*
 * Created on 2005-9-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:PersonFilterTrans</p>
 * <p>Description:人员筛选</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 12, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class PersonFilterInsertTrans extends IBusiness {

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
  
            FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,true,true,1,userView.getUserName());
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
		strsql.append("select a.zp_pos_id,b.pos_cond from zp_position a,organization b where b.codeitemid = a.pos_id");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
	    try
	    {
	      ResultSet res = dao.search(strsql.toString());
	      while(res.next())
	      {
	      	String zp_pos_id = res.getString("zp_pos_id");
	      	String pos_cond = res.getString("pos_cond");
          	if(pos_cond == null || "".equals(pos_cond)){
          		break;
          	}
          	String strwhere=combine_SQL(pos_cond,dbpre);
          	StringBuffer sqle = new StringBuffer();
          	sqle.append("select ");
          	sqle.append(dbpre);
          	sqle.append("a01.a0100 ");
          	sqle.append(strwhere); 
          	ResultSet rs = dao.search(sqle.toString(),list);
          	while(rs.next()){
          		boolean flag = false;	
          		String a0100 = rs.getString("a0100");
          		String ssql = "select a0100 from zp_pos_tache";
          		ResultSet rst  = dao.search(ssql,list);
          		while(rst.next()){
          			if(a0100.equals(rst.getString("a0100"))){
          				flag = true;
          			}
          		}
          		if(flag == false){
          			PreparedStatement pstmt=null;
          			try{
          			    String sqlsql = "insert into zp_pos_tache (a0100,zp_pos_id,tache_id,thenumber,apply_date,status) values (?,?,1,0,?,'0')";
          			    list.clear();
          			    list.add(a0100);
          			    list.add(zp_pos_id);
          			    list.add(DateUtils.getSqlDate(new Date()));
          			    dao.insert(sqlsql, list);
          			    /*pstmt=this.getFrameconn().prepareStatement(sqlsql);
       			        pstmt.setString(1,a0100);
       			        pstmt.setString(2,zp_pos_id);
       			        pstmt.setDate(3,DateUtils.getSqlDate(new Date()));
       			        pstmt.executeUpdate();*/
          			}catch(Exception ee)
					{
          				ee.printStackTrace();
          				throw GeneralExceptionHandler.Handle(ee);				
          			}
          			finally
          			{
          				try
          				{
          					if(pstmt!=null)
          						pstmt.close();
          				}
          				catch(SQLException ee)
          				{
          					ee.printStackTrace();
          				}
          			}
          		}
          	}
	      }
	  }
	  catch(Exception sqle)
	  {
	    sqle.printStackTrace();
	    throw GeneralExceptionHandler.Handle(sqle);
	  }
	}

}
