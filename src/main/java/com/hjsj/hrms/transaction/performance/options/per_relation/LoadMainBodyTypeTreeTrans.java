package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:LoadMainBodyTypeTreeTrans.java</p>
 * <p>Description:考核关系/指定考核主体/加载主体类别树</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-16 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class LoadMainBodyTypeTreeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
	String objectIds = (String) hm.get("objIDs");
	PerRelationBo bo = new PerRelationBo(this.getFrameconn());
	String[] objs = objectIds.split("@");
	ArrayList objectList=bo.getKhObjectsList(objs);
	this.getFormHM().put("khObjectList", objectList);
	if(objs.length>1)
	    this.getFormHM().put("khObject", "all");
	else if((objs.length==1))
	    this.getFormHM().put("khObject",objs[0]);	
    }

}
