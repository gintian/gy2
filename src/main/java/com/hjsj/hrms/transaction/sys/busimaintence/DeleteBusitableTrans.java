package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:业务字典维护(子集删除)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 16, 2008:5:13:59 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DeleteBusitableTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fieldsetid = (String)this.getFormHM().get("str");
		String fieldset = fieldsetid.substring(0,fieldsetid.length()-1);
		//【7088】业务字典子集中选中两个或者更多的子集，点击删除，不起作用  jingq add 2015.01.28
		fieldsetid = PubFunc.keyWord_reback(fieldsetid);
		fieldset = PubFunc.keyWord_reback(fieldset);
		String [] temp = fieldset.split("/");
		if(!"".equals(fieldset)){
			this.deleteFactor(temp);
			//this.getFormHM().put("info","true");
		}
	}
	public void deleteFactor(String[] temp) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer fieldSetId=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        StringBuffer str=new StringBuffer();
        try{
        	for(int i=0;i<temp.length;i++){
				 fieldSetId.append("'");
				 fieldSetId.append(temp[i]);
				 fieldSetId.append("',");
	         }    
        	fieldSetId.setLength(fieldSetId.length()-1);
        	strsql.append("delete from t_hr_busitable where ");
        	strsql.append("fieldSetId in("+fieldSetId.toString()+")");
        	str.append("delete from t_hr_busifield where ");
        	str.append("fieldsetid in("+fieldSetId.toString()+")");
        	dao.delete(strsql.toString(), new ArrayList());
        	dao.delete(str.toString(), new ArrayList());
        }
        catch(SQLException e){
        	e.printStackTrace();
        }
	}

}
