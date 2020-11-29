package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class MachineListTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		ArrayList machinelist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select name,location_id from kq_machine_location");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		CommonData vo = new CommonData() ;
		vo.setDataName("          ");
		vo.setDataValue("");
		machinelist.add(vo);
		try
		{
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				if (userView.isHaveResource(IResourceConstant.KQ_MACH, frowset.getString("location_id")))
				{
					vo = new CommonData() ;
					vo.setDataName(this.frowset.getString("name"));
					vo.setDataValue(this.frowset.getString("location_id"));
					machinelist.add(vo);
				}
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("machinelist",machinelist);
		KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	int int_id_len=kqCardLength.tack_CardLen();
    	this.getFormHM().put("cardno_len",int_id_len+"");
	}
}
