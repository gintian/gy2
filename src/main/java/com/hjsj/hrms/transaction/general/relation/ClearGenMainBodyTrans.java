package com.hjsj.hrms.transaction.general.relation;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ClearMainBodyTrans.java
 * </p>
 * <p>
 * Description:考核关系/清除考核主体
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-04-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class ClearGenMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String oper = (String) hm.get("operflag");
	hm.remove("operflag");
	StringBuffer buf = new StringBuffer();
	String sql = "";
	String relationid = (String)this.getFormHM().get("relationid");
	if ("clearBody".equals(oper))
	{
	    String objectIds = (String) hm.get("objectIDs");
	    objectIds = SafeCode.decode(objectIds);
	    String[] objs = objectIds.split("@");
	    StringBuffer objStr = new StringBuffer();
	    for (int i = 0; i < objs.length; i++)
	    {
		String obj = (String) objs[i];
		if ("".equals(obj.trim()))
		    continue;
		objStr.append(",'" + obj + "'");
	    }
	    if (objStr.length() > 0&&relationid!=null&&relationid.length()>0)
	    {
		buf.append("where object_id in (");
		buf.append(objStr.substring(1));
		buf.append(") and relation_id="+relationid);
	    }
	}else if ("delBody".equals(oper))
	{
	    String objectid = (String) hm.get("objectid");
	    String mainBodyIds = (String) hm.get("mainBodyIds");
	    objectid = SafeCode.decode(objectid);
	    mainBodyIds = SafeCode.decode(mainBodyIds);
	    String[] mainbodys = mainBodyIds.split("@");
	    StringBuffer bodyStr = new StringBuffer();
	    for (int i = 0; i < mainbodys.length; i++)
	    {
		String mainbodyID = (String) mainbodys[i];
		if ("".equals(mainbodyID.trim()))
		    continue;
		bodyStr.append(",'" + mainbodyID + "'");
	    }
	    if (bodyStr.length() > 0)
	    {
		buf.append("where mainbody_id in (");
		buf.append(bodyStr.substring(1));
		buf.append(") and object_id='"+objectid+"' and relation_id="+relationid);
	    }
	}
	if(buf.toString().length()>0){
		sql = "delete from t_wf_mainbody "+buf.toString();
	
	ContentDAO dao = new ContentDAO(this.frameconn);
	try
	{
	    dao.delete(sql, new ArrayList());
	} catch (SQLException e)
	{
	    e.printStackTrace();
	    throw new GeneralException(e.getMessage());
	}
    }
    }

}
