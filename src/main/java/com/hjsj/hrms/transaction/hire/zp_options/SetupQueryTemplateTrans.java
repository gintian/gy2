/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SetupQueryTemplateTrans</p>
 * <p>Description:查询模板列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SetupQueryTemplateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList fields = new ArrayList();
		ArrayList fieldlist = new ArrayList();
		/**查询类型*/
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        if(hm!=null)
        {
            String query_type=(String)hm.get("a_query");
            String pos_id_value = (String)hm.get("a_posid");
            try{
               String sql = "select pos_cond from organization where codeitemid = '"+pos_id_value+"'";
               this.frowset = dao.search(sql);
               while(this.frowset.next()){   
               	   String pos_cond = this.getFrowset().getString("pos_cond");
               	   if(pos_cond == null || "".equals(pos_cond)){
               	      this.getFormHM().put("succeedinfo","");
 		    	      this.getFormHM().put("query_type",query_type);
 	                  this.getFormHM().put("pos_id_value",pos_id_value);
 	                  this.getFormHM().put("right_fields",null);
 	                  this.getFormHM().put("fieldlist",null);
 	                  return;
               	   }
               	   else if(pos_cond != null && !"".equals(pos_cond)){
               	      int subIndex = pos_cond.indexOf("|");	   
               	      if(subIndex != -1){ 
               	   	      String subposcond = pos_cond.substring(subIndex+1,pos_cond.length());
               	   	      if(subposcond != null && !"".equals(subposcond)){
               	   	          fields.add(subposcond.substring(0,5));
               	   	          for(int i=0;i<subposcond.length();i++){
               	   	              if(subposcond.charAt(i) == '`'){
               	   	           	      if(i+6 < subposcond.length()){
               	   	                     fields.add(subposcond.substring(i+1,i+6));
               	   	           	      }
               	   	               }
               	   	           }
               	   	 
               	   	      }
               	      }
               	   }
               }
               if(fields.size() > 0){
                  for(int i=0;i<fields.size();i++){
               	      FieldItem item=null;
               	      String fieldname=(String)fields.get(i);
               	      if(fieldname==null|| "".equals(fieldname))
                          continue;
                      item=DataDictionary.getFieldItem(fieldname.toUpperCase());
                      if(item!=null)
                      {
                          CommonData datavo=new CommonData(item.getItemid(),item.getItemdesc());
                          fieldlist.add(datavo);
                      }
                  }
               }
            }catch(SQLException sqle)
		    {
		         sqle.printStackTrace();
		         throw GeneralExceptionHandler.Handle(sqle);
		    }finally{
		    	this.getFormHM().put("succeedinfo","");
		    	this.getFormHM().put("query_type",query_type);
	            this.getFormHM().put("pos_id_value",pos_id_value);
	            this.getFormHM().put("fieldlist",fieldlist);
		    }
        }
        

	}

}
