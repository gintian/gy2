package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:DelKhMainBodyTrans.java</p>
 * <p>Description:考核关系/指定考核主体/删除考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-17 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class DelKhMainBodyTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String delStr = (String)this.getFormHM().get("paramStr");
	delStr = delStr.substring(0, delStr.length() - 1);
	String[] ids = delStr.split("@");//格式: mainbody_id:objectID:body_id
	
	PerRelationBo bo = new PerRelationBo(this.frameconn);
	bo.delKhMainBody(ids);  
    }

}
