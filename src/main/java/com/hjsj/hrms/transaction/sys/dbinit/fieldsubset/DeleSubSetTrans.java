package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:删除功能</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 19, 2008:11:17:38 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DeleSubSetTrans  extends IBusiness{

	public void execute() throws GeneralException {
		String typeidss = (String)this.getFormHM().get("deletestr");
		//指标体系，删除子集时，选择两个以上就删除不了了  jingq add 2014.11.7
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
		StringBuffer fieldSetId=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        StringBuffer strs=new StringBuffer();
        try {
			 for(int i=0;i<temp.length;i++){
				 fieldSetId.append("'");
				 fieldSetId.append(temp[i]);
				 fieldSetId.append("',");
	         }    
			 fieldSetId.setLength(fieldSetId.length()-1);
			 
			 strsql.append("delete from fieldset where ");
			 strsql.append(" fieldSetId in("+fieldSetId.toString()+")");
			 strs.append("delete from fielditem where");
			 strs.append(" fieldsetid in("+fieldSetId.toString()+")");
			 dao.delete(strsql.toString(),new ArrayList());
			 dao.delete(strs.toString(),new ArrayList());
			//dao.delete(strsql.toString(),new ArrayList());
			//dao.batchUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

}
