package com.hjsj.hrms.transaction.general.query;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * Title:HandworkSelectTrans2.java
 * </p>
 * <p>
 * Description:考核实施/手工选择单位，团队，人员，部门
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-05-04 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class HandworkSelectTrans2 extends IBusiness
{

    public void execute() throws GeneralException
    {

	ContentDAO dao = new ContentDAO(this.getFrameconn());
	ArrayList list = new ArrayList();
	try
	{
	    // 1为选人 2为选单位 3为选团队，4为部门
	    String codeID = (String) this.getFormHM().get("codeid");
	    String codeItemID = (String) this.getFormHM().get("codeItemID"); // 选取的值
	    String dbpre_arr = (String) this.getFormHM().get("dbpre_arr");
	    boolean isUsr = false; // 是否有人员库的权限
	    if ("1".equals(codeID))
	    {
		ArrayList dblist = userView.getPrivDbList();
		for (Iterator t = dblist.iterator(); t.hasNext();)
		{
		    String temp = ((String) t.next()).toLowerCase();
		    if (temp.equals(dbpre_arr.toLowerCase()))
		    {
			isUsr = true;
			break;
		    }
		}
	    }
		isUsr = true;//给在职库权限 绩效用
	    StringBuffer sql = new StringBuffer("");
	    if ("1".equals(codeID))
	    {
		String codesetid = codeItemID.substring(0, 2);
		String selectedCodeid = codeItemID.substring(2);
		if(!"UN".equalsIgnoreCase(codesetid) && !"UM".equalsIgnoreCase(codesetid) && !"@k".equalsIgnoreCase(codesetid))
		{
		    selectedCodeid=codeItemID;
		    this.frowset = dao.search("select codesetid from organization where codeitemid='"+selectedCodeid+"'");
		    if (this.frowset.next())
			codesetid=this.frowset.getString(1);
		}
		
		sql.append("select A0100,A0101  from " + dbpre_arr + "A01 where 1=1 and A0101 is not null ");
		if ("UN".equalsIgnoreCase(codesetid)) // 单位
		{
		    sql.append(" and B0110 like '" + selectedCodeid + "%'");
		} else if ("UM".equalsIgnoreCase(codesetid)) // 部门
		{
		    sql.append(" and E0122 like '" + selectedCodeid + "%'");
		} else if ("@K".equalsIgnoreCase(codesetid)) // 职位
		{
		    sql.append(" and E01A1='" + selectedCodeid + "'");
		}
		// 权限控制
		if (!userView.isSuper_admin() && isUsr)
		{
		    String conditionSql = " select " + dbpre_arr + "A01.A0100 " + userView.getPrivSQLExpression(dbpre_arr, true);
		    sql.append(" and " + dbpre_arr + "A01.A0100 in (" + conditionSql + " )");
		} else if (!userView.isSuper_admin() && !isUsr)
		{
		    sql.append(" and 1=2 ");
		}
		sql.append(" order by a0000");
	    } else
	    {
		String codeValue = this.userView.getManagePrivCodeValue();		
		String codeSetid = "";
		if ("3".equals(codeID))
		    codeSetid = " ( codesetid='UN' or codesetid='UM' )  and  codeitemid like '" + codeValue + "%'";
		else if ("2".equals(codeID))
		    codeSetid = " codesetid='UN'  and  codeitemid like '" + codeValue + "%'";
		else if ("4".equals(codeID))
		    codeSetid = " codesetid='UM'  and  codeitemid like '" + codeValue + "%'";
		
		if (codeItemID.length() >= 2
			&& ("UN".equalsIgnoreCase(codeItemID.substring(0, 2)) || "UM".equalsIgnoreCase(codeItemID.substring(0, 2)) || "@K".equalsIgnoreCase(codeItemID.substring(0, 2))))
		    codeItemID = codeItemID.substring(2);
		sql.append("select codeitemid,codeitemdesc,codesetid from organization  where " + codeSetid + "  and codeitemdesc is not null and codeitemid like '" + codeItemID + "%'");
		// if (codeID.equals("3") || codeID.equals("5"))
		// sql.append(" and codeitemid in (select B0110 from B01 where
                // B0110 like '" + codeItemID + "%')");
		// if (codeID.equals("4"))
		// sql.append(" and codeitemid in (select E01A1 from K01 where
                // E01A1 like '" + codeItemID + "%')");

		sql.append(" order by a0000");

	    }

	    this.frowset = dao.search(sql.toString());
	    int countNum = 0;
	    while (this.frowset.next())
	    {
		String codeid = this.frowset.getString(1);
		String codeName = this.frowset.getString(2) != null ? this.frowset.getString(2) : "";

		CommonData dataobj = new CommonData(codeid, codeName);
		list.add(dataobj);
		if (countNum > 1500)
		    break;
		countNum++;
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally
	{
	    this.getFormHM().clear();
	    this.getFormHM().put("fieldlist", list);
	}

    }

}
