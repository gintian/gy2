
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchExamReportTrans</p>
 * <p>Description:查询考试成绩</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchExamReportTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList fieldList = new ArrayList();
		ArrayList nameList = new ArrayList();
		ArrayList list=new ArrayList();
		StringBuffer sb = new StringBuffer();
		RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String dbpre = "";
        if(rv!=null)
        {
            dbpre=rv.getString("str_value");
            if(dbpre==null || dbpre!=null &&dbpre.length()==0)
            	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
        } 
        else
        {
        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
        }
        FieldItem fielditem=new FieldItem();
        fielditem.setItemid("a0101");        
        fieldList.add(fielditem);
        FieldItem fielditemr=new FieldItem();
        fielditemr.setItemid("read_score");        
        fieldList.add(fielditemr);
        FieldItem fielditemw=new FieldItem();
        fielditemw.setItemid("written_score");        
        fieldList.add(fielditemw);
        String sqle = "select subject_id,subject_name from zp_exam_subject";
        try{
           ResultSet rstst = dao.search(sqle,list);
           while(rstst.next()){
           	 FieldItem fielditemk=new FieldItem();
             fielditemk.setItemid("k_"+rstst.getString("subject_id"));
           	 fieldList.add(fielditemk);
           	 nameList.add(rstst.getString("subject_name"));
           }
        }catch(SQLException e){
        	 e.printStackTrace();
		     throw GeneralExceptionHandler.Handle(e);
        }
        FieldItem fielditems=new FieldItem();
        fielditems.setItemid("sum_score");        
        fieldList.add(fielditems);
        
        
		StringBuffer sqlstr=new StringBuffer();
		StringBuffer strwhere=new StringBuffer();
		StringBuffer orderby=new StringBuffer();
		StringBuffer columns=new StringBuffer();
		sqlstr.append("select ");
        for(int i=0;i<fieldList.size();i++)
        {
        	FieldItem fieldItem=(FieldItem)fieldList.get(i);
        	if("a0101".equalsIgnoreCase(fieldItem.getItemid()) || "a0100".equalsIgnoreCase(fieldItem.getItemid()))
        	{
        		if(!"a0100".equalsIgnoreCase(fieldItem.getItemid()))
        		{
        			sqlstr.append(dbpre);
            		sqlstr.append("a01.a0101,");
            		columns.append("a0101,");
            		columns.append("a0100,");
        		}        		
        	}
        	else
        	{
        		sqlstr.append("round(zp_exam_report.");
        		sqlstr.append(fieldItem.getItemid());
        		
        		sqlstr.append(",");
        		sqlstr.append(fieldItem.getDecimalwidth());
        		sqlstr.append(") as " +  fieldItem.getItemid() + ",");
           		columns.append(fieldItem.getItemid());
       		    columns.append(",");
        	}
        }
        sqlstr.append(dbpre);
        sqlstr.append("a01.a0100 ");
        strwhere.append(" from zp_exam_report,");
        strwhere.append(dbpre);
        strwhere.append("a01 where zp_exam_report.a0100=");
        strwhere.append(dbpre);
        strwhere.append("a01.a0100");
        orderby.append(" order by ");
        orderby.append(dbpre);
        orderby.append("a01.a0000");
        this.getFormHM().put("strwhere",strwhere.toString());
        this.getFormHM().put("sqlstr",sqlstr.toString());
        //System.out.println(sqlstr.toString());
        this.getFormHM().put("orderby",orderby.toString());
        this.getFormHM().put("columns",columns.toString());
        this.getFormHM().put("fieldList",fieldList);
    	this.getFormHM().put("nameList",nameList);
        /* sqlstr.append("select zp_exam_report.*,");
		sqlstr.append(dbpre);
		sqlstr.append("a01.a0101 from zp_exam_report,");
		sqlstr.append(dbpre);
	    sqlstr.append("a01 where zp_exam_report.a0100=");
	    sqlstr.append(dbpre);
	    sqlstr.append("a01.a0100");
	   try
	    {
	      this.frecset= dao.search(strsql.toString());
	      while(this.frecset.next())
	      {
	      	LazyDynaBean rec=new LazyDynaBean();
	      	rec.set("a0100",this.frecset.getString("a0100"));
	      	if(this.frecset.getString("a0101")!=null && !this.frecset.getString("a0101").equals(""))
	      	    rec.set("a0101",this.frecset.getString("a0101"));
	      	else
	      		rec.set("a0101","");
	        for(int i=2;i<fieldList.size();i++){
	        	if(this.frecset.getString(fieldList.get(i).toString())!=null && !this.frecset.getString(fieldList.get(i).toString()).equals(""))
	        	    rec.set(fieldList.get(i).toString(),this.frecset.getString(fieldList.get(i).toString()));
                else
                	rec.set(fieldList.get(i).toString(),"");
	        }             
            list.add(rec);
	      } 
	    }catch(SQLException e)
        {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }finally{
	    	this.getFormHM().put("sortCondList",list);
	    	this.getFormHM().put("fieldList",fieldList);
	    	this.getFormHM().put("nameList",nameList);
	    }*/
	}
}
