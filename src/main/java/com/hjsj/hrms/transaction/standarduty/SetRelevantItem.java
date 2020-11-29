package com.hjsj.hrms.transaction.standarduty;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SetRelevantItem extends IBusiness {

	public void execute() throws GeneralException {
		try{  
		  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		  String submit=hm.get("submit").toString();
		  this.getFormHM().put("submitflag", submit);
		  String setitem = hm.get("b_setitem").toString();
		  HashMap relevantset=(HashMap)this.getFormHM().get("relevantset");
		  if(!"H00".equals(setitem)){
		      String[] param=hm.get("b_setitem").toString().split("-");
	  
	          HashMap sdutyitem=(HashMap)this.getFormHM().get("sdutyitem");
	          HashMap dutyitem=(HashMap)this.getFormHM().get("dutyitem");
	          ArrayList duty=(ArrayList)this.getFormHM().get("duty");
	          ArrayList sduty=(ArrayList)this.getFormHM().get("sduty");

	          ArrayList relevantitem=new ArrayList();
	          LazyDynaBean ldb=(LazyDynaBean)relevantset.get(param[1]);
	          String target=ldb.get("target").toString();
	          
	          if(target.equalsIgnoreCase(param[0])){
	        	  relevantitem=(ArrayList)ldb.get("field");
	          }else{
	        	  relevantitem.clear();
	        	  ldb.set(target, target);
	        	  ldb.set("field", relevantitem);
	          }
	          
		      ArrayList targetitems=(ArrayList)dutyitem.get(param[0]);
		      ArrayList sourceitems=(ArrayList)sdutyitem.get(param[1]);
	          this.getFormHM().put("fieldsetid", param[1]);
	          this.getFormHM().put("targetsetid", param[0]);
	          this.getFormHM().put("relevantitem", relevantitem);
	          this.getFormHM().put("sourceitems", sourceitems);
	          this.getFormHM().put("targetitems", targetitems);
	          
	          String targetdesc = getsetdesc(duty,param[0]);
	          String fielddesc = getsetdesc(sduty,param[1]);
	          
	          this.getFormHM().put("targetsetdesc", targetdesc);
	          this.getFormHM().put("fieldsetdesc", fielddesc);	  
	  		}else{			 
				  this.getFormHM().put("fieldsetid","H00");
		          this.getFormHM().put("targetsetid","K00");
		          this.getFormHM().put("fieldsetdesc", "基准岗位多媒体分类");
			      this.getFormHM().put("targetsetdesc", "岗位多媒体分类");
			      
			      ArrayList typelist = getTypelist("H00");
			      this.getFormHM().put("sourceitems", typelist);
			      
			      typelist = getTypelist("K00");
			      
		          this.getFormHM().put("targetitems", typelist);
		          ArrayList relevantitem=new ArrayList();
		          LazyDynaBean ldb=(LazyDynaBean)relevantset.get("H00");
		          if(ldb!=null)
		        	  relevantitem=(ArrayList)ldb.get("field");
		          
		          this.getFormHM().put("relevantitem", relevantitem);
		  }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getsetdesc(ArrayList fieldsetlist,String fieldsetid){
		String fieldsetdesc="";
		for(int i=0;i<fieldsetlist.size();i++){
			 FieldSet fs = (FieldSet)fieldsetlist.get(i);
			 if(fieldsetid.equalsIgnoreCase(fs.getFieldsetid())){
				 fieldsetdesc = fs.getFieldsetdesc();
				 break;
			 }
		}
		return fieldsetdesc;
	}
	
	/**
	 * 
	 * @param table 多媒体表 只限岗位和基准岗位
	 */
	public ArrayList getTypelist(String table){
		
		ArrayList arr = new ArrayList();
		StringBuffer sql = new StringBuffer(" select flag,sortname from mediasort where ");
		if("H00".equals(table.toUpperCase())){
			sql.append(" dbflag='4' ");
		}else{
			sql.append(" dbflag='3' ");
			// yangj 2015-01-07  k代号已成为多媒体岗位说明书固定分类
			if (this.getUserView().hasTheMediaSet("K")) {
				FieldItem fi = new FieldItem();
				fi.setItemid("K");
				String sortname = ResourceFactory.getProperty("lable.pos.e01a1.manual");
				fi.setItemdesc(sortname);
				fi.setItemtype("A");
				arr.add(fi);
			}
		}
		
		try{
			ContentDAO dao = new ContentDAO(frameconn);
			frowset = dao.search(sql.toString());
		    while(frowset.next()){
		    	FieldItem fi = new FieldItem();
		    	fi.setItemid(frowset.getString("flag"));
		    	fi.setItemdesc(frowset.getString("sortname"));
		    	fi.setItemtype("A");
		    	arr.add(fi);
		    }
		
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return arr;
	}
}
