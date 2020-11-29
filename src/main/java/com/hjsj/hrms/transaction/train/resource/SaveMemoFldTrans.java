package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:QueryMemoFldTrans.java</p>
 * <p>
 * Description:保存备注字段交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveMemoFldTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String type = (String) hm.get("type");
	String priFld=(String) hm.get("priFld");
	priFld = PubFunc.decrypt(SafeCode.decode(priFld));
	String memoFldName = (String)hm.get("memoFldName");	
	String classid = (String)hm.get("classid");
	String dbname = hm.get("dbname").toString();
	String itemdesc = hm.get("itemdesc").toString();
	TrainResourceBo bo = null;
	if("9".equals(type)) {
	    dbname = PubFunc.decrypt(SafeCode.decode(dbname));
		bo = new TrainResourceBo(this.frameconn, type, classid);
	} else
		bo = new TrainResourceBo(this.frameconn, type);
	this.getFormHM().put("itemdesc", itemdesc);
	String value = (String)this.getFormHM().get("memoFld");
	bo.updateMemoFld(priFld, memoFldName, value , dbname);
    }
    
}
