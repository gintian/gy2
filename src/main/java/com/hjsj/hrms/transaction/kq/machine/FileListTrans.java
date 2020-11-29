package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class FileListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		String sql="select rule_id,rule_name from kq_data_rule";
		ArrayList file_list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		CommonData vo = new CommonData() ;
		vo.setDataName("");
		vo.setDataValue("");
		file_list.add(vo);
		try
		{
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				vo = new CommonData() ;
				vo.setDataName(this.frowset.getString("rule_name"));
				vo.setDataValue(this.frowset.getString("rule_id"));
				file_list.add(vo);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("file_list",file_list);
	}

}
