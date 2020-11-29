package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DeletePerDegreedescTrans extends IBusiness {

	public void execute() throws GeneralException {	
		String typeidss = (String)this.getFormHM().get("deletestr");
		String typeids = typeidss.substring(0,typeidss.length()-1);		
		String [] temp = typeids.replaceAll("／", "/").split("/");
		if(!"".equals(typeids)){
			this.deleteFactor(temp);
			this.getFormHM().put("info","true");
		}
	}
	
	public void deleteFactor(String [] temp) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ids=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        try {
        	HashMap grade_ids= new HashMap();
			 for(int i=0;i<temp.length;i++){
				 ids.append("'");
				 ids.append(temp[i]);
				 grade_ids.put(temp[i], temp[i]);
				 ids.append("',");
	         }    
			 ids.setLength(ids.length()-1);
			 
			 strsql.setLength(0);
			 strsql.append("select plan_id from per_plan where status=7");
			 this.frowset = dao.search(strsql.toString());
			 strsql.setLength(0);			 
			 while(this.frowset.next())
			 {
				 strsql.append("select grade_id from per_result_"+this.frowset.getString(1));
				 strsql.append(" union all ");
			 }
			 if(strsql.length()>0)
			 {
				 
				 String sql=strsql.substring(0, (strsql.length()-" union all ".length()));
				 this.frowset=dao.search(sql);
				 while(this.frowset.next())
				 {
					 if(grade_ids.get(this.frowset.getString(1))!=null)
						 throw new GeneralException("选定等级已经使用,不能删除！");
				 }
			 }
			 
			strsql.setLength(0);
            strsql.append("delete from per_degreedesc where id  in(");
            strsql.append(ids.toString());
            strsql.append(")");
			dao.delete(strsql.toString(),new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

}
