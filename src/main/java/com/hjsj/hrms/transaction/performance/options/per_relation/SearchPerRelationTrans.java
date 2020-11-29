package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchPerRelationTrans.java</p>
 * <p> Description:考核关系</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-15 13:00:00</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchPerRelationTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);	
		
		String a_code=(String)hm.get("a_code");
		hm.remove("a_code");
		String opt = (String)hm.get("opt");
		hm.remove("opt");
		String querySql="";
		if(opt!=null && "query".equals(opt))
		    querySql=(String)this.getFormHM().get("paramStr");
		PerRelationBo bo = new PerRelationBo(this.frameconn,this.userView);	
		ArrayList perObjects = bo.getPerObjectDataList(a_code,querySql);
		ArrayList objectTypes = bo.getObjTypes();
		ArrayList allObjectTypes = bo.getObjTypes2();		
		
		ArrayList objectTypeList = new ArrayList();
		CommonData temp = new CommonData("", "");
		try
		{
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=1 and status=1 order by seq");
		    RowSet rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				objectTypeList.add(temp);
		    }
		    
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
				
		HashMap joinedObjs = bo.getJoinedObjs();
		this.getFormHM().put("joinedObjs", joinedObjs);	
		
		this.getFormHM().put("perObjects", perObjects);	
		this.getFormHM().put("objectTypes", objectTypes);	
		this.getFormHM().put("allObjectTypes", allObjectTypes);
		this.getFormHM().put("a_code", a_code);
		this.getFormHM().put("objectTypeList", objectTypeList);
    }
}
