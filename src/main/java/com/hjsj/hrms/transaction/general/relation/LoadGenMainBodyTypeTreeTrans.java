package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hrms.frame.codec.SafeCode;
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
public class LoadGenMainBodyTypeTreeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	//接收审批标记为
	String approvalRelation = (String) hm.get("approvalRelation");
	this.getFormHM().put("approvalRelation",approvalRelation);
	hm.remove(approvalRelation);
	String objectIds = (String) this.userView.getHm().get("objectIDs");
	this.userView.getHm().remove("objectIDs");
	objectIds = SafeCode.decode(objectIds);
	String relation_id = (String) hm.get("relation_id");
	String dbpre = (String) hm.get("dbpre");
	String actor_type =(String) hm.get("actor_type");
	GenRelationBo bo = new GenRelationBo(this.getFrameconn());
	String[] objs = objectIds.split("#");
	ArrayList objectList=bo.getKhObjectsList(objs,relation_id,dbpre,actor_type);
	this.getFormHM().put("khObjectList", objectList);
	if(objs.length>1)
	    this.getFormHM().put("khObject", "all");
	else if((objs.length==1))
	    this.getFormHM().put("khObject",objs[0]);	
    }

}
