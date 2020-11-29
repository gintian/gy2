/*
 * Created on 2005-11-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchShortPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList shortPosList = new ArrayList();
		ArrayList list = new ArrayList();
		RecordVo vo=(RecordVo)this.getFormHM().get("zpplanvo");
	 	String org_id = vo.getString("org_id");
	 	String dept_id = vo.getString("dept_id");
	 	String plan_id = vo.getString("plan_id");
		RecordVo rv= ConstantParamter.getRealConstantVo("PS_WORKOUT");
		ResultSet rst=null;
		if(rv == null){
			return;
		}else{
		   String posWork = rv.getString("str_value");
		   int strIndex = posWork.indexOf("|");
		   if(strIndex != -1){
		   	  String setstr = posWork.substring(0,strIndex);
		   	  int fieldIndex = posWork.indexOf(",");
		   	  if(fieldIndex != -1){
		   	     String firstfieldstr = posWork.substring(strIndex+1,fieldIndex);
		   	     String lastfieldstr = posWork.substring(fieldIndex+1,posWork.length());
		   	     if(setstr.indexOf("01") != -1){
		   	        String ssql = "";
		   	        if("UM".equals(this.userView.getManagePrivCode())){
		   	     	    ssql = "select E01A1,"+firstfieldstr+", "+lastfieldstr+" from "+setstr+" where ("+firstfieldstr+" > "+lastfieldstr+ " or (" + firstfieldstr + ">0 and " + lastfieldstr + " is null)) and  E01A1 like '"+this.userView.getManagePrivCodeValue()+"%'";
		   	        }else{
		   	        	ssql = "select E01A1,"+firstfieldstr+", "+lastfieldstr+" from "+setstr+" where ("+firstfieldstr+" > "+lastfieldstr+ " or (" + firstfieldstr + ">0 and " + lastfieldstr + " is null)) and  E01A1 like '"+org_id+"%'";
		   	        }
		   	     	try{
		   	     	   this.frowset = dao.search(ssql);
		   	     	   while(this.frowset.next()){
		   	     	       LazyDynaBean shortposbean=new LazyDynaBean();
		   	     	       shortposbean.set("e01a1",this.getFrowset().getString("E01A1"));
	        		       String sqlsql="select parentid from organization where codeitemid='" +this.getFrowset().getString("E01A1") + "'";
	        		       rst = dao.search(sqlsql,list);
	        		       String deptid="";
	        		       if(rst.next())
	        		       {
	        		       	deptid=rst.getString("parentid");
	        		       }
	        		       shortposbean.set("name","(" + AdminCode.getCodeName("UN",org_id) + AdminCode.getCodeName("UM",deptid) +")"+ AdminCode.getCodeName("@K",this.getFrowset().getString("E01A1")));
	        		       shortPosList.add(shortposbean);
		   	     	   }
		   	     	}catch(SQLException e){
			   	           e.printStackTrace();
			 		       throw GeneralExceptionHandler.Handle(e);
			   	        }finally{
			   	           this.getFormHM().put("shortPosList",shortPosList);
			   	           this.getFormHM().put("setstr",setstr);
			   	           this.getFormHM().put("firstfieldstr",firstfieldstr);
			   	           this.getFormHM().put("lastfieldstr",lastfieldstr);
			   	           this.getFormHM().put("plan_id",plan_id);
			   	           try{
			   	             if(rst!=null)
			   	           	  rst.close();
			   	            }catch(Exception  e)
							{
			   	            	
			   	            }
			   	        }
		   	     }else{
		   	        try{
    	   	               String strsql = "";
		   	               if("UM".equals(this.userView.getManagePrivCode())){
		   	        	       strsql = "select E01A1,"+firstfieldstr+", "+lastfieldstr+" from "+setstr+" a where I9999 =(select max(i9999) from " + setstr + " where " + setstr + ".e01a1=a.e01a1)  and ("+firstfieldstr+" > "+lastfieldstr+ " or (" + firstfieldstr + ">0 and " + lastfieldstr + " is null)) and  E01A1 like '"+this.userView.getManagePrivCodeValue()+"%'";
		   	               }else{
		   	                   strsql = "select E01A1,"+firstfieldstr+", "+lastfieldstr+" from "+setstr+" a  where I9999 =(select max(i9999) from " + setstr + " where " + setstr + ".e01a1=a.e01a1)  and ("+firstfieldstr+" > "+lastfieldstr+ " or (" + firstfieldstr + ">0 and " + lastfieldstr + " is null)) and  E01A1 like '"+org_id+"%'";
		   	               }
		   	        	   ResultSet rs = dao.search(strsql,list);
		   	        	   while(rs.next()){
		   	        	       LazyDynaBean shortposbean=new LazyDynaBean();
		   	        	       shortposbean.set("e01a1",this.getFrowset().getString("E01A1"));
		   	        		   String sqlsql="select parentid from organization where codeitemid='" +this.getFrowset().getString("E01A1") + "'";
		        		       rst = dao.search(sqlsql,list);
		        		       String deptid="";
		        		       if(rst.next())
		        		       {
		        		       	deptid=rst.getString("parentid");
		        		       }
		        		       shortposbean.set("name","(" + AdminCode.getCodeName("UN",org_id) + AdminCode.getCodeName("UM",deptid) +")"+AdminCode.getCodeName("@K",rs.getString("E01A1")));
		   	        		   shortPosList.add(shortposbean);
		   	        	   }
		   	        }catch(SQLException ee){
		   	           ee.printStackTrace();
		 		       throw GeneralExceptionHandler.Handle(ee);
		   	        }finally{
		   	           this.getFormHM().put("shortPosList",shortPosList);
		   	           this.getFormHM().put("setstr",setstr);
		   	           this.getFormHM().put("firstfieldstr",firstfieldstr);
		   	           this.getFormHM().put("lastfieldstr",lastfieldstr);
		   	           this.getFormHM().put("plan_id",plan_id);
		   	           try{
		   	             if(rst!=null)
		   	           	  rst.close();
		   	            }catch(Exception  e)
						{
		   	            	
		   	            }
		   	        }
		   	     }
		      }
		   }
		}
		

	}

}
