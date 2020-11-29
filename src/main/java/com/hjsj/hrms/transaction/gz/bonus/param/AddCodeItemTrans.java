package com.hjsj.hrms.transaction.gz.bonus.param;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

/**
 * <p>
 * Title:AddCodeItemTrans.java
 * </p>
 * <p>
 * Description:新增代码表数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-03 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddCodeItemTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String menuid = (String) hm.get("menuid");
	hm.remove("menuid");
	this.getFormHM().put("menuid", menuid);

	String codeitemid = (String) hm.get("codeitemid");
	hm.remove("codeitemid");

	String codesetid = "";
	ContentDAO dao = new ContentDAO(this.frameconn);
	RecordVo vo = new RecordVo("codeitem");
	if (menuid != null && "2".equals(menuid))
	    codesetid = "49";
	else if (menuid != null && "3".equals(menuid))
	    codesetid = "50";

	// 没有对应的代码项，则第一个长度为任意长度，只要小于30就可以 ;建第二个代码项时，必须和上次建的等长
	String codeLen = "30";
	String sql = "select * from codeitem where codesetid='" + codesetid + "'";
	try
	{
	    RowSet rs = dao.search(sql);
	    if (rs.next())
	    {
		String codeitemid1 = rs.getString("codeitemid");
		codeLen = Integer.toString(codeitemid1.length());
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	this.getFormHM().put("codeLen", codeLen);

	vo.setString("codesetid", codesetid);

	if ("0".equals(codeitemid))// 新增
	{
	    vo.setString("codeitemid", "");
	    vo.setString("codeitemdesc", "");
	} else
	{
	    vo.setString("codeitemid", codeitemid);
	    try
	    {
		vo = dao.findByPrimaryKey(vo);
	    } catch (Exception e)
	    {
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	    }
	}

	this.getFormHM().put("codeitemVo", vo);
    }
}
