package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:Delete_JP_Pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Delete_JP_Pos extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			HashMap hm=this.getFormHM();
			String name=(String)hm.get("z07_set_table");  //数据集别名+"_table"
			cat.debug("table name="+name);
			ArrayList list=(ArrayList)hm.get("z07_set_record");//选中的记录，数据集别名+"_record"	
			for(int i=0;i<list.size();i++){
				RecordVo vo =(RecordVo)list.get(i);
				if("05".equalsIgnoreCase(vo.getString("z0713"))|| "09".equalsIgnoreCase(vo.getString("z0713")))
					throw new GeneralException("", "请选择结束状态的竞聘岗位进行删除！","", "");
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			dao.deleteValueObject(list);
			
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}