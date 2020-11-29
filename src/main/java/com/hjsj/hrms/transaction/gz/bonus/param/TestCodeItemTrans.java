package com.hjsj.hrms.transaction.gz.bonus.param;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
/**
 * <p>
 * Title:TestCodeItemTrans.java
 * </p>
 * <p>
 * Description:保存代码表前的验证
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
public class TestCodeItemTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String menuid = (String) this.getFormHM().get("menuid");
	String codeitemid = (String) this.getFormHM().get("codeitemid");
	String name = (String) this.getFormHM().get("name");
	name = SafeCode.decode(name);
	String type = (String) this.getFormHM().get("type");
	this.getFormHM().put("type", type);
	ContentDAO dao = new ContentDAO(this.frameconn);
	
	String codesetid = "";
	if (menuid != null && "2".equals(menuid))
	    codesetid = "49";
	else if (menuid != null && "3".equals(menuid))
	    codesetid = "50";
	boolean flag = false;
	try
	{
	    String sql = "select * from codeitem where codesetid='" + codesetid+"'";
	    RowSet rs = dao.search(sql);
	    while (rs.next())// 编辑
	    {
		String codeitemid1 = rs.getString("codeitemid");
		String codeitemDesc = rs.getString("codeitemdesc");
		if("0".equals(type) || "2".equals(type))//新增保存 和 保存后继续
		{
		    if(codeitemid1.equals(codeitemid) || codeitemDesc.equals(name))
			{
			    flag = true;
			    break;
			}
		}
		if("1".equals(type))//编辑保存
		{
		    if(codeitemDesc.equals(name))
		    {
			    flag = true;
			    break;
		    }
		}
	    }
	    if (flag)
		this.getFormHM().put("flag", "1");
	    else
		this.getFormHM().put("flag", "0");
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}

    }

}
