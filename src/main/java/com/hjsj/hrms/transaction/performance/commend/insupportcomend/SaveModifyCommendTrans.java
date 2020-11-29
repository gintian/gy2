package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveModifyCommendTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm = this.getFormHM();
		String tabname = (String)hm.get("p02_set_table");
		ArrayList list = (ArrayList)hm.get("p02_set_record");
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			if(!(list ==null|| list.size()==0))
			{
			     for(int i=0;i<list.size();i++){
			         RecordVo vo = (RecordVo)list.get(i);
			         if(vo.getState()==-1)
			         {
			           /**新建时,状态标识为起草01*/
			           vo.setString("p0209", "01");
			           dao.addValueObject(vo);
			           
			         }
			         else if(vo.getState()==2)
			    	   dao.updateValueObject(vo);
			         else
			         {
			           /**在此增加对删除记录的判断,如果记录不能删除，则抛异常*/
			           dao.deleteValueObject(vo);
			         }
			     }
			}
			
		}catch(Exception e){
			/***/
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	
}
