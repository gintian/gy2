package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:PasteMainBodyTrans.java</p>
 * <p>Description:考核关系/粘贴考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-04-20 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class PasteMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String khObjectCopyed = (String)this.getFormHM().get("khObjectCopyed");//被复制的考核主体信息
	if(khObjectCopyed==null || (khObjectCopyed!=null && khObjectCopyed.length()==0))
	    return;
	
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String objectIds = (String) hm.get("objectIDs");//被粘贴的考核对象,可以是多个
	PerRelationBo bo = new PerRelationBo(this.frameconn);
	String[] objs = objectIds.split("@");
	for(int i=0;i<objs.length;i++)
	{
	    String obj = (String)objs[i];
	    if("".equals(obj.trim()))
		continue;
	    bo.pasteKhMainBody(obj,khObjectCopyed);
	}	
	
	this.getFormHM().put("khObjectCopyed", null);
    }

}
