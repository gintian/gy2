package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:InsertObjectTrans.java</p>
 * <p> Description:考核关系/手工选择和条件选择插入考核对象</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-15 13:00:00</p> 
 * @author FanZhiGuo
 * @version 1.0 
 */
public class InsertObjectTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String opt = (String) hm.get("opt");
	try
	{
	    PerRelationBo bo = new PerRelationBo(this.getFrameconn());
	    if ("handselect".equals(opt))
	    {
		String right_fields = (String) hm.get("right_fields");
		String[] temps = right_fields.replaceAll("／", "/").split("/");
		StringBuffer a0100s = new StringBuffer("");
		for (int i = 0; i < temps.length; i++)
		{
		    if (temps[i].length() > 0)
			a0100s.append(",'" + temps[i] + "'");
		}
		bo.handInsertObjects(a0100s.substring(1));

	    } else if ("conditionselect".equals(opt))
	    {
		String str_sql = PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("paramStr")));
		
		PerformanceImplementBo bo1=new PerformanceImplementBo(this.getFrameconn());
	    String whl = bo1.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
		str_sql+=whl;
		bo.handInsertObjects(str_sql);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

}
