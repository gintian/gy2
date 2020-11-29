package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SelectMachineTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		String name=(String)this.getFormHM().get("name");
		if(name==null||name.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","名称不能为空","",""));
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			String sql="select * from kq_machine_location where name like '%"+name+"%'";
			this.frowset=dao.search(sql);
			CommonData vo = new CommonData() ;
			vo.setDataName("");
			vo.setDataValue("");
			list.add(vo);
			while(this.frowset.next())
			{
				if (userView.isHaveResource(IResourceConstant.KQ_MACH, frowset.getString("location_id")))
				{
					vo = new CommonData() ;
					vo.setDataName(this.frowset.getString("name"));
					vo.setDataValue(this.frowset.getString("location_id"));
					list.add(vo);
				}
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("machinelist",list);
	}

}
