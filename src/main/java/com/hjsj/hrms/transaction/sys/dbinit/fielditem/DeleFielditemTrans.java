package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:删除指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 22, 2008:5:15:24 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DeleFielditemTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String typeidss = (String)this.getFormHM().get("deletestr");
		//指标体系，同时删除两个以上指标时，删除不了  jingq add 2014.11.7
		typeidss = PubFunc.keyWord_reback(typeidss);
		String typeids = typeidss.substring(0,typeidss.length()-1);		
		String [] temp = typeids.split("/");
		if(!"".equals(typeids)){
			this.deleteFactor(temp);
			//this.getFormHM().put("info","true");
		}
		
	}
	public void deleteFactor(String [] temp) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer itemid=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        try {
			 for(int i=0;i<temp.length;i++){
				 itemid.append("'");
				 itemid.append(temp[i]);
				 itemid.append("',");
	         }    
			 itemid.setLength(itemid.length()-1);
			 
			 strsql.append("delete from fielditem where ");
			 strsql.append(" itemid in("+itemid.toString()+")");
			
			 dao.delete(strsql.toString(),new ArrayList());
			//dao.delete(strsql.toString(),new ArrayList());
			//dao.batchUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

}
