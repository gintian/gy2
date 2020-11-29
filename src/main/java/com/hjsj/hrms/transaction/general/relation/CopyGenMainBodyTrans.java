package com.hjsj.hrms.transaction.general.relation;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:CopyMainBodyTrans.java</p>
 * <p>Description:考核关系/复制考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-20 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class CopyGenMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String objectID = (String) hm.get("objectID");//被复制的考核对象
	objectID = SafeCode.decode(objectID);
	this.getFormHM().put("khObjectCopyed", objectID);
    }
}
