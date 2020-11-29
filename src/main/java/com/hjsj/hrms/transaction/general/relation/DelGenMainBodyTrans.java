package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:DelKhMainBodyTrans.java</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-11-15 13:00:00</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class DelGenMainBodyTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String delStr = (String)this.getFormHM().get("paramStr");
	delStr = delStr.substring(0, delStr.length() - 1);
	String[] ids = delStr.split("`");//格式: mainbody_id:objectID:body_id
	
	GenRelationBo bo = new GenRelationBo(this.frameconn);
	bo.delKhMainBody(ids);  
    }

}
