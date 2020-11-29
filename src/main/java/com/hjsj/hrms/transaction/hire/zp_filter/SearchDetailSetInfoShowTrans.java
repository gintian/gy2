package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchDetailSetInfoShowTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */	
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String userbase = (String)hm.get("userbase");
    	String a0100 = (String)hm.get("a0100");
    	String setname = (String)hm.get("setname");
    	String setStr = "";
    	try{
    		String sql = "select str_value from constant where constant = 'ZP_SUBSET_LIST'";
    		this.frowset = dao.search(sql);
    		while(this.frowset.next()){
    			setStr = this.getFrowset().getString("str_value");
    		}
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    	String fieldStr = "";
    	try{
    		String sql = "select str_value from constant where constant = 'ZP_FIELD_LIST'";
    		this.frowset = dao.search(sql);
    		while(this.frowset.next()){
    			fieldStr = this.getFrowset().getString("str_value");
    		}
    	}catch(SQLException ex){
    		ex.printStackTrace();
    	}
		ArrayList zpsetlist=new ArrayList();
		ArrayList zpfieldlist=new ArrayList();		 
		ArrayList infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		if(!infoSetList.isEmpty())
    	{
			    for(int j=0;j<infoSetList.size();j++)
	    	    {
			    	FieldSet fieldset=(FieldSet)infoSetList.get(j);
			 	    if(setStr.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())!=-1){
			 	    	zpsetlist.add(fieldset);
				      }
	    	     }
		}  
    	List zpfieldvaluelist=null;
	    try{
	    	String subfieldstr = "";
	    	int fieldindex = fieldStr.indexOf(setname);
	    	if(fieldindex != -1){
	    		String substr = fieldStr.substring(fieldindex,fieldStr.length());
	    		int subindex = substr.indexOf("},");
	    		subfieldstr = fieldStr.substring(fieldindex,fieldindex+subindex);
	    	}
	    	StringBuffer strsql=new StringBuffer();
		    strsql.append("select * from ");
		    strsql.append(userbase + setname);
		    strsql.append(" where a0100 = '");
			strsql.append(a0100);
			strsql.append("'");
			zpfieldvaluelist = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
	    	ArrayList infofieldlist=DataDictionary.getFieldList(setname,Constant.EMPLOY_FIELD_SET);
	    	if(!infofieldlist.isEmpty())
	    	{
				    for(int j=0;j<infofieldlist.size();j++)
		    	    {
				 	    FieldItem fieldItem=(FieldItem)infofieldlist.get(j);
				 	    if(subfieldstr.toLowerCase().indexOf(fieldItem.getItemid().toLowerCase())!=-1){
				 	    	zpfieldlist.add(fieldItem);
					      }
		    	     }
			}    			
		}catch(Exception e){
		   e.printStackTrace();
		}finally{		
	       this.getFormHM().put("zpfieldlist",zpfieldlist);            //压回页面
	       this.getFormHM().put("zpsetlist",zpsetlist);
	       this.getFormHM().put("zpfieldvaluelist",zpfieldvaluelist);
	       this.getFormHM().put("userbase",userbase);
       }
		
	}

}
