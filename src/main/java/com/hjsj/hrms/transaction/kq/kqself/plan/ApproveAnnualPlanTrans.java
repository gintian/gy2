package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ApproveAnnualPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		        
        ArrayList fieldlist = DataDictionary.getFieldList("q29",Constant.USED_FIELD_SET);
        ArrayList list= new ArrayList();
        for(int i=0;i< fieldlist.size();i++) 
		{
	      	FieldItem field=(FieldItem)fieldlist.get(i);
	      	field.setValue("");
	      	field.setViewvalue("");
	      	if("q2913".equals(field.getItemid()))
	      	{
	      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	      		String strDate = sdf.format(new java.util.Date());
	      		field.setValue(strDate);
	      	}
	        if("q2913".equals(field.getItemid())|| "q29z0".equals(field.getItemid())|| "q29z7".equals(field.getItemid()))
	        {
	        	field.setVisible(true);
	        	list.add(field.cloneItem());
	        } 
	        	
	   	}	
	    this.getFormHM().put("approvelist",list);
	    this.getFormHM().put("q29z0list",getStartList());
	}
    public ArrayList getStartList()
    {
    	StringBuffer sql= new StringBuffer();
    	sql.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='30'");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ArrayList list = new ArrayList();
    	try
    	{
    		this.frowset=dao.search(sql.toString());
    		while(this.frowset.next())
    		{
    			CommonData vo = new CommonData();
    			vo.setDataName(this.frowset.getString("codeitemdesc"));
    			vo.setDataValue(this.frowset.getString("codeitemid"));
    			list.add(vo);
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
}
