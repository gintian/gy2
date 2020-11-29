package com.hjsj.hrms.transaction.general.query.complex;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 4, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class ComplexSaveNameTrans extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{
		String complex_name=(String)this.getFormHM().get("complex_name");
		int id=DbNameBo.getPrimaryKey("gwhere","id","",this.getFrameconn());
		RecordVo vo=new RecordVo("gwhere");
		vo.setInt("id", id);
		vo.setString("name", complex_name);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.addValueObject(vo);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		ArrayList list=new ArrayList();
    	String sql="select id,name from gwhere order by id";
    	CommonData da=new CommonData();
    	try
    	{
    		this.frowset=dao.search(sql);
    		while(this.frowset.next())
    		{
    			da=new CommonData();
    	    	da.setDataName(this.frowset.getString("name"));
    	    	da.setDataValue(this.frowset.getString("id"));
    	    	list.add(da);
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		this.getFormHM().put("complexList", list);
		this.getFormHM().put("complex_id", id+"");
	}
    
}
