package com.hjsj.hrms.transaction.ht.inform;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:ContractAddTrans.java
 * </p>
 * <p>
 * Description:合同相关子集的删除操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-17 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class DelContractInfoTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	String delStr = (String)hm.get("delStr");
	hm.remove("delStr");
	
	if(delStr.length()==0)
	    return;
	String a0100 = (String)this.getFormHM().get("a0100");
	String itemtable = (String)this.getFormHM().get("itemtable");
	StringBuffer buf = new StringBuffer();
	buf.append("delete "+itemtable);
	buf.append(" where a0100='");
	buf.append(a0100+"' and i9999 in (");
	buf.append(delStr+")");
	
	ContentDAO dao=new ContentDAO(this.getFrameconn());	
	try
	{
	    dao.delete(buf.toString(), new ArrayList());
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

}
