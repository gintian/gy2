
package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:ModuleQueryTrans</p>
 * <p>Description:模板查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 10, 2005:4:43:44 PM</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class ModuleQueryTrans extends IBusiness {

    public void execute() throws GeneralException {
    	String report_id = (String)this.getFormHM().get("report_id");
    	EditReport editReport=new EditReport();
    	ArrayList fieldlist=editReport.getU02FieldList(this.getFrameconn(),report_id,true);
		ArrayList list = editReport.getU02QueryList(fieldlist);
        String like=(String)this.getFormHM().get("like");
        HashMap map = getcodeitemMap(this.getFrameconn());
       StringBuffer consql = new StringBuffer();
        if("1".equals(like)){
        	for(int i=0;i<list.size();i++){
        		FieldItem field=(FieldItem)list.get(i);
        		String value="";
        		if("D".equalsIgnoreCase(field.getItemtype())){
        			
          			 value = (String)this.getFormHM().get(field.getItemid()+"view");
          			 if(!"".equals(value)&&value.length()>0){
          				 value=value.replace("-", ".");
          				 //Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
          				 consql.append(" and ");
          				 consql.append(field.getItemid()+" >='"+value+"'");

              			 value = (String)this.getFormHM().get(field.getItemid()+"view2");
              			 if(!"".equals(value)&&value.length()>0){
              				 value=value.replace("-", ".");
              				 // vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
              					 consql.append(" and ");
                  				 consql.append(field.getItemid()+" <='"+value+"'");

          			 }
          			
          		}
          		}
        			else if("N".equalsIgnoreCase(field.getItemtype())){
            			
               			 value = (String)this.getFormHM().get(field.getItemid()+"view");
               			 if(value==null)
              				 value = (String)this.getFormHM().get(field.getItemid()); 
               			 if(value!=null&&!"".equals(value)&&value.length()>0){
               				 consql.append(" and ");
               				 consql.append(field.getItemid()+" like '%"+value+"%'");
               			 }
        	
        			}
        	else if("A".equalsIgnoreCase(field.getItemtype())){
    			
          			
          			 value = (String)this.getFormHM().get(field.getItemid()+"view");
          			 if(value==null)
          				 value = (String)this.getFormHM().get(field.getItemid()); 
           			 if(value!=null&&!"".equals(value)&&value.length()>0){
           				if(map!=null&&map.get(field.getCodesetid()+"_"+value)!=null)
           					value =(String)map.get(field.getCodesetid()+"_"+value);
			 if(!"".equals(value)&&value.length()>0){
				 consql.append(" and ");
				 consql.append(field.getItemid()+" like '%"+value+"%'");
			 }
           }
   	   
       }
      }
     }else{
    	 
    	 for(int i=0;i<list.size();i++){
     		FieldItem field=(FieldItem)list.get(i);
     		String value="";
     		if("D".equalsIgnoreCase(field.getItemtype())){
    			
   			 value = (String)this.getFormHM().get(field.getItemid()+"view");
   			 if(!"".equals(value)&&value.length()>0){
   				 value=value.replace("-", ".");
   				 //Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
   				 consql.append(" and ");
   				 consql.append(field.getItemid()+" >='"+value+"'");

       			 value = (String)this.getFormHM().get(field.getItemid()+"view2");
       			 if(!"".equals(value)&&value.length()>0){
       				 value=value.replace("-", ".");
       				 // vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
       					 consql.append(" and ");
           				 consql.append(field.getItemid()+" <='"+value+"'");

   			 }
   			
   		}
   		}
     			else if("N".equalsIgnoreCase(field.getItemtype())){
         			
            			 value = (String)this.getFormHM().get(field.getItemid()+"view");
            			 if(value==null)
            				 value = (String)this.getFormHM().get(field.getItemid());
            			 if(value!=null&&!"".equals(value)&&value.length()>0){
            				 consql.append(" and ");
            				 consql.append(field.getItemid()+" = "+value+"");
            			 }
     	
     			}
     	else if("A".equalsIgnoreCase(field.getItemtype())){
       			 value = (String)this.getFormHM().get(field.getItemid()+"view");
       			 if(value==null)
       				value = (String)this.getFormHM().get(field.getItemid());
       			 if(value!=null&&!"".equals(value)&&value.length()>0){
       				if(map!=null&&map.get(field.getCodesetid()+"_"+value)!=null)
       					value =(String)map.get(field.getCodesetid()+"_"+value);
       				
       				 consql.append(" and ");
       				 consql.append(field.getItemid()+" = '"+value+"'");
       			 }
	   }
   }

  }
        this.getFormHM().put("subquerysql", consql.toString());   
       
  }

	public HashMap getcodeitemMap(Connection conn) {
		// TODO Auto-generated method stub
		String strsql = " select * from codeitem ";
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset=dao.search(strsql);
			while(this.frowset.next()){
				map.put(this.frowset.getString("codesetid")+"_"+this.frowset.getString("codeitemdesc"),this.frowset.getString("codeitemid"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
}
   
	
	