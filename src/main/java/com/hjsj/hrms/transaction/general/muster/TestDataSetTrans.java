/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;


import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:TestDataSetTrans</p>
 * <p>Description:测试通用数据集交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-16:10:12:01</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class TestDataSetTrans extends IBusiness {

	/**
	 * 
	 */
	public TestDataSetTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("test_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("test_set_record");
		/**数据集字段列表*/
		ArrayList fieldlist=(ArrayList)hm.get("test_set_items");
		if(list!=null)
		{
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				cat.debug("vo="+vo.toString());
			}
			for(int j=0;j<fieldlist.size();j++)
			{
				Field field=(Field)fieldlist.get(j);
				cat.debug("fielditem="+field.toString());
			}
		}
	}

}
