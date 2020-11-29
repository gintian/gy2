/*
 * Created on 2005-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchFrontBaseOptionsTrans extends IBusiness {
	    /**
         * 
        */
        public SearchFrontBaseOptionsTrans() {
        }
		 /**根据传过的的指标串，分解成对应的指标对象*/
	    private ArrayList splitField(String strfields)
	    {
	        ArrayList list=new ArrayList();
	        strfields=strfields+",";
	        int pos=0;
	        StringTokenizer st = new StringTokenizer(strfields, ",");
	        while (st.hasMoreTokens())
	        {
	            /** for examples A01.A0405*/
	            String fieldname=st.nextToken();
	            list.add(fieldname);
	        }
	        return list;
	    }
	    
	    /* 
	     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	     */
		public void execute() throws GeneralException {
			    ContentDAO dao=new ContentDAO(this.getFrameconn());
			    StringBuffer strsql=new StringBuffer();
			    ArrayList list = new ArrayList();
			    RecordVo vo= ConstantParamter.getRealConstantVo("ZP_DBNAME");
			    RecordVo rv= ConstantParamter.getRealConstantVo("ZP_SUBSET_LIST");
		        if(vo!=null)
		        {
		            String dbpre=vo.getString("str_value");
		            try{
		            	String sqle = "select DBName from dbname where Pre = '"+dbpre+"'";
		            	ResultSet rs = dao.search(sqle,list);
		            	while(rs.next()){
		            		dbpre = rs.getString("dbname");
		            	}
		               String setlist=rv.getString("str_value");
		               ArrayList fieldlist=splitField(setlist);
		               for(int i=0;i<fieldlist.size();i++){
		            	  String sql = "select fieldSetDesc from fieldSet where fieldSetId = '"+fieldlist.get(i)+"'";
		            	  this.frowset = dao.search(sql);
		            	  while(this.frowset.next()){
		            	       strsql.append(this.getFrowset().getString("fieldSetDesc"));
		            	       strsql.append(" ");
		            	  }
		                }  
		            }catch(SQLException sqle)
				    {
		            	 sqle.printStackTrace();
		          	     throw GeneralExceptionHandler.Handle(sqle);            
		            }
		            finally{
		               this.getFormHM().put("dbpre",dbpre);
		               this.getFormHM().put("strsql",strsql.toString());
		            }
		      }

	      }
}

