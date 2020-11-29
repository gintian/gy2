package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchPerMainBodyTrans.java</p>
 * <p> Description:考核关系考核主体</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-15 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchPerMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String object_id=(String)hm.get("objectid");
	
		PerRelationBo bo = new PerRelationBo(this.frameconn);
		ArrayList perMainBodys = bo.getMainBodys(object_id);
		ArrayList mainbodyTypes = bo.getMainBodyTypes2();
		ArrayList allMainbodyTypes = bo.getMainBodyTypes3();
		this.getFormHM().put("perMainbodys", perMainBodys);
		this.getFormHM().put("allBodyTypes", allMainbodyTypes);
		this.getFormHM().put("bodyTypes", mainbodyTypes);
		this.getFormHM().put("objSelected", object_id);
		HashMap joinedObjs = bo.getJoinedObjs();
		if(joinedObjs.get(object_id)!=null)
			this.getFormHM().put("enableFlag", "0");
		else
			this.getFormHM().put("enableFlag", "1");
    }
}
