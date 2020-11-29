package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class DeletePerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {	
		String typeidss = (String)this.getFormHM().get("deletestr");
		String typeids = typeidss.substring(0,typeidss.length()-1);		
		String [] temp = typeids.replaceAll("／", "/").split("/");
		if(!"".equals(typeids)){
			this.deleteFactor(temp);
			this.getFormHM().put("info","true");
		}
	}
	
	public String  deleteFactor(String [] temp) throws GeneralException{
		String using = "";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ids=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        try {
			 for(int i=0;i<temp.length;i++){ 
				 if(isExist(temp[i]))  
				 {
					 using = using+temp[i]+",";
					 throw new GeneralException("存在正在使用的记录，不能删除！");									 
				 }					
				 ids.append("'");
				 ids.append(temp[i]);
				 ids.append("',");
	         }    
			 ids.setLength(ids.length()-1);
            strsql.append("delete from per_know where know_id  in(");
            strsql.append(ids.toString());
            strsql.append(")");
			dao.delete(strsql.toString(),new ArrayList());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if("".equals(using))
			return using;
		else
			return using.substring(0, using.length()-1);
	}
	
	public boolean isExist(String knowId) throws GeneralException{
		StringBuffer strsql = new StringBuffer();
		strsql.append("select  know_id from per_mainbody where know_id=");
		strsql.append(knowId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try {
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) 
				return true;
		} catch (SQLException e) {
			throw new GeneralException("查询数据异常！");
		}
		return false;
	}

}
