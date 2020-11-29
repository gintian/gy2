package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/*
 * 更新考核主体指标权限划分
 */
public class UpdatePointPowerTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String leftStr = (String) hm.get("leftStr");
	String rightStr = (String) hm.get("rightStr");
	String rightAll = (String) hm.get("rightAll");
	String plan_id = (String) hm.get("plan_id");
	String objectIds = (String) hm.get("objIds");
	// 主体
	StringBuffer bodyStr = new StringBuffer();
	String[] bodys = leftStr.split("@");
	for (int i = 0; i < bodys.length; i++)
	{
	    if ("".equals(bodys[i]))
		continue;
	    bodyStr.append(",'" + bodys[i] + "'");
	}
	// 对象
	StringBuffer objectStr = new StringBuffer();
	String[] objects = objectIds.split("@");
	for (int i = 0; i < objects.length; i++)
	{
	    if ("".equals(objects[i]))
		continue;
	    objectStr.append(",'" + objects[i] + "'");
	}
	// 指标
	String allPoints[] = rightAll.split("@");
	StringBuffer updateSql = new StringBuffer();
	if (rightStr == null || "".equals(rightStr))
	{
	    for (int i = 0; i < allPoints.length; i++)
		updateSql.append("," + allPoints[i] + "=0");
	} else
	{
	    String[] pointSel = rightStr.split("@");
	    for (int i = 0; i < allPoints.length; i++)
	    {
		boolean flag = false;
		for (int j = 0; j < pointSel.length; j++)
		{
		    if (pointSel[j].equals(allPoints[i]))
		    {
			flag = true;
			break;
		    }
		}
		if (flag)
		    updateSql.append("," + allPoints[i] + "=1");
		else
		    updateSql.append("," + allPoints[i] + "=0");
	    }
	}
	StringBuffer strSql = new StringBuffer();
	strSql.append("update PER_POINTPRIV_" + plan_id + " set ");
	strSql.append(updateSql.substring(1));
	strSql.append(" where mainbody_id IN (");
	strSql.append(bodyStr.substring(1));
	strSql.append(") AND object_id IN (");
	strSql.append(objectStr.substring(1));
	strSql.append(")");

	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    dao.update(strSql.toString());
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }

}
