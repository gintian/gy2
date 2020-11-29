package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:TestBeforeOperTrans.java
 * </p>
 * <p>
 * Description:操作前的验证
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-12-18 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TestBeforeOperTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String oper = (String) this.getFormHM().get("oper");
	ContentDAO dao = new ContentDAO(this.frameconn);
	
	try
	{
	    if ("1".equals(oper))// 判断是否所有操作单位都处于下发状态
	    {
		String operOrg = this.userView.getUnit_id();// 操作单位
		String topOrg="";
		boolean isHaveTopOrg = false;
		
		ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
		String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
		String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标

		String sqlStr = "select * from organization where codeitemid=parentid";
		this.frowset = dao.search(sqlStr);
		    if (this.frowset.next())
			topOrg = this.frowset.getString("codeitemid") == null ? "" : this.frowset.getString("codeitemid");		
		
		StringBuffer buf = new StringBuffer();
		buf.append("select count(*)  from " + setid + " where " + dist_field + "='1' ");
		StringBuffer tempSql = new StringBuffer("");
		String[] temp = operOrg.split("`");
		for (int i = 0; i < temp.length; i++)
		{
		    if(temp[i].substring(2).equals(topOrg))
		    {
			isHaveTopOrg=true;
			continue;
		    }
		    tempSql.append(" or  b0110 = '" + temp[i].substring(2) + "'");
		}
		    

		buf.append(" and ( " + tempSql.substring(3) + ")");

		
		this.frowset = dao.search(buf.toString());
		if (this.frowset.next())
		{
		    int count = this.frowset.getInt(1);
		    if(count>0 && isHaveTopOrg)
			count++;
		    if (count == temp.length)
			this.getFormHM().put("ok", "1");
		    else
			this.getFormHM().put("ok", "0");
		}

	    } else if ("2".equals(oper))
	    {

	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
    }
}
