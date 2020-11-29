package com.hjsj.hrms.transaction.kq.options.sign_point.person;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author szk
 *
 */
public class SearchPersonListTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList persons=(ArrayList)this.getFormHM().get("persons");	
		String pid=(String)this.getFormHM().get("pid");	
		ArrayList paralist = new ArrayList();
		ArrayList dellist = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   ArrayList list = new ArrayList();
					   ArrayList list2 = new ArrayList();
					   String [] para=persons.get(i).toString().split("`");
					   list.add(pid);//pid
					   list.add(para[0]);//a0100
					   list.add(para[5]);//dbase
					   list.add(para[1]);//a0101
					   list.add(para[2]);//b0110
					   list.add(para[3]);//e0122
					   list.add(para[4]);//e01a1
					   //zxj para[5]是nbase,一定有值,所以a0000是空的话，para[6]就没有了
					   if(para.length > 6){
						   //linbz 25782 当a0000为null时存入空字符串
						   if("null".equalsIgnoreCase(para[6].toString()))
							   list.add("");//a0000
						   else
	                           list.add(para[6]);
					   }else{
                           list.add("");
					   }
					   paralist.add(list);
					   list2.add(pid);//pid
					   list2.add(para[0]);//a0100
					   list2.add(para[5]);//dbase
					   dellist.add(list2);
				   }
			dao.batchUpdate("delete from kq_sign_point_emp  where pid=? and a0100=? and nbase=?", dellist);
			dao.batchUpdate("INSERT INTO kq_sign_point_emp (pid,a0100,nbase,a0101,b0110,e0122,e01a1,a0000) VALUES (?,?,?,?,?,?,?,?) ", paralist);		
		
		}catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);  
		}

	}

}
