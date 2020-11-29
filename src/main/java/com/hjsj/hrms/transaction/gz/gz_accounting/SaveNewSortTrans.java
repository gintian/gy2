package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 30200710260
 * <p>Title:SaveNewSortTrans.java</p>
 * <p>Description>:SaveNewSortTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 30, 2009 11:31:26 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveNewSortTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String record=(String)this.getFormHM().get("record");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String opt=(String)this.getFormHM().get("opt");//=1保存排序=0重命名
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("1".equals(opt))
			{
		    	String[] arr=record.split("/");
		    	int norder=this.getSeq();
		    	for(int i=0;i<arr.length;i++)
	    		{
		    		if(arr[i]==null|| "".equals(arr[i]))
	    				continue;
		    		RecordVo vo = new RecordVo("gzItem_filter");
		    		vo.setInt("id", Integer.parseInt(arr[i]));
		    		vo = dao.findByPrimaryKey(vo);
		    		vo.setInt("norder", norder+i);
		    		dao.updateValueObject(vo);
		     	}
			}
			else
			{
				String name=SafeCode.decode((String)this.getFormHM().get("name"));
				RecordVo vo = new RecordVo("gzItem_filter");
	    		vo.setInt("id", Integer.parseInt(record));
	    		vo = dao.findByPrimaryKey(vo);
	    		vo.setString("chz", name);
	    		dao.updateValueObject(vo);
			}
			this.getFormHM().put("salaryid", salaryid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public int getSeq()
	{
		int i=0;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select max(norder) norder from gzItem_filter");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				i=this.frowset.getInt("norder")+1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}

}
