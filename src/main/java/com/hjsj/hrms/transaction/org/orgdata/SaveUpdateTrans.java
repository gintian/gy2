package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");

		String fieldPri = this.userView.analyseTablePriv(name);
		if(!"2".equals(fieldPri))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.update.record.competence")+"！"));
		try
		{
			if("K".equalsIgnoreCase(name.substring(0,1))){
				for(int i=0;i<list.size();i++){
					RecordVo vo=(RecordVo)list.get(i);
					String B0110 = vo.getString("b0110");
					B0110=B0110!=null?B0110:"";
				
					String E0122 = vo.getString("e0122");
					E0122=E0122!=null?E0122:"";
					String E01A1 = vo.getString("e01a1");
					E01A1=E01A1!=null?E01A1:"";
					if(E0122.indexOf(B0110)==-1&&E0122.length()>0)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.depart")+"！"));
					if(E01A1.indexOf(E0122)==-1&&name.indexOf("A01")!=-1&&E01A1.length()>0)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.job")+"！"));
				}
			}
		
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
