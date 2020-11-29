package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SearchPerMainBodyTrans.java</p>
 * <p> Description:考核关系考核主体</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-15 13:00:00</p> 
 * @author FanZhiGuo
 * @version 1.0 
 */
public class SearchGenMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	String object_id=(String)hm.get("objectid");
	
	GenRelationBo bo = new GenRelationBo(this.frameconn);
	String relation_id = (String)this.getFormHM().get("relationid");
	String actor_type = (String)this.getFormHM().get("actor_type");
	object_id = SafeCode.decode(object_id);
	ArrayList genMainBodys = bo.getMainBodys(object_id,relation_id,actor_type);
//	ArrayList mainbodyTypes = bo.getMainBodyTypes2();
//	ArrayList allMainbodyTypes = bo.getMainBodyTypes3();
	this.getFormHM().put("genMainbodys", genMainBodys);
//	this.getFormHM().put("allBodyTypes", allMainbodyTypes);
//	this.getFormHM().put("bodyTypes", mainbodyTypes);
	this.getFormHM().put("objSelected", object_id);
//	HashMap joinedObjs = bo.getJoinedObjs();
//	if(joinedObjs.get(object_id)!=null)
//		this.getFormHM().put("enableFlag", "0");
//	else
//		this.getFormHM().put("enableFlag", "1");
    }
}
