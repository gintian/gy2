/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:删除花名册选中的记录</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-19:15:03:07</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteMusterRecordTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("muster_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("muster_set_record");
		/**数据集字段列表*/
		ArrayList fieldlist=(ArrayList)hm.get("muster_set_items");
		ContentDAO dao=null;
		try
		{
			if(!(list==null||list.size()==0))
			{
				StringBuffer sql_whl=new StringBuffer("");
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					sql_whl.append(" or recidx="+vo.getInt("recidx"));
				}
				/*
				for(int j=0;j<fieldlist.size();j++)
				{
					Field field=(Field)fieldlist.get(j);
					cat.debug("fielditem="+field.toString());
				}
				*/			
				dao=new ContentDAO(this.getFrameconn());			
   			    dao.delete("delete from "+name+" where "+sql_whl.substring(3),new ArrayList());
   			    MusterBo musterBo=new MusterBo(this.getFrameconn(),this.userView);
   			    musterBo.updateMusterRecidx(name);
				//dao.deleteValueObject(list);
			}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}

}
