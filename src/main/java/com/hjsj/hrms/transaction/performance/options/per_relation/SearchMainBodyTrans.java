package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchMainBodyTrans.java
 * </p>
 * <p>
 * Description:考核实施/指定考核主体/查找考核主体
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-06-01 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
	String typeCode = (String) hm.get("code");
	hm.remove("code");
	if(typeCode==null || (typeCode!=null && "".equals(typeCode)))
	    typeCode="all";
	
	String khObject = (String) this.getFormHM().get("khObject");

	HashMap objs = new HashMap();
	if(khObject==null || (khObject!=null && "all".equals(khObject)))
	{
	    ArrayList objs1=(ArrayList)this.getFormHM().get("khObjectList");
	    for(int i = 0;i<objs1.size();i++)
	    {
		CommonData cd = (CommonData)objs1.get(i);
		objs.put(cd.getDataValue(),cd.getDataName());      
	    }
	    this.getFormHM().put("khObject", "all");
	}else
	{
	    ArrayList objs1=(ArrayList)this.getFormHM().get("khObjectList");
	    for(int i = 0;i<objs1.size();i++)
	    {
		CommonData cd = (CommonData)objs1.get(i);
		if(khObject.equals(cd.getDataValue()))
		    objs.put(cd.getDataValue(),cd.getDataName());       
	    }
	}
	PerRelationBo bo = new PerRelationBo(this.frameconn);
	ArrayList list = bo.getMainBodyList(objs, typeCode);
	
	this.getFormHM().put("mainbodys", list);
    }

}
