package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SortFieldClassTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			DynaBean  vo =(LazyDynaBean)this.getFormHM().get("newsortvo");
			String ids= (String)vo.get("ids");
			String pointsetid = (String)vo.get("pointsetid");
			String subsys_id = (String)vo.get("subsys_id");
			String sorttype=(String)vo.get("sorttype");
			
			StringBuffer buf = new StringBuffer();
			String[] pointsetids=ids.replaceAll("／", "/").split("/");
			for(int i=0;i<pointsetids.length;i++)
			{
				buf.append("'");
				buf.append(pointsetids[i]);
				buf.append("',");
			}
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			if(buf.toString().length()>1)
				buf.setLength(buf.length()-1);
			if("1".equals(sorttype))
			{
	    		ArrayList seqlist = bo.getFieldClassSeq(buf.toString());
    			bo.reFieldClassSort(seqlist, pointsetids);
			}
			else
			{
				ArrayList seqlsit = bo.getFieldSeq(pointsetid);
				bo.reFieldSort(seqlsit, pointsetids, pointsetid);
				this.getFormHM().put("pointsetid",pointsetid);
				this.getFormHM().put("subsys_id",subsys_id);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
