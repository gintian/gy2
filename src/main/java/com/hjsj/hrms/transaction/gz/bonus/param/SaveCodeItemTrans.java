package com.hjsj.hrms.transaction.gz.bonus.param;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * <p>
 * Title:SaveCodeItemTrans.java
 * </p>
 * <p>
 * Description:保存代码表数据
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
public class SaveCodeItemTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String menuid = (String) this.getFormHM().get("menuid");
	String codeitemid = (String) this.getFormHM().get("codeitemid");
	String name = (String) this.getFormHM().get("name");
	String type = (String) this.getFormHM().get("type");
	this.getFormHM().put("type", type);
	name = SafeCode.decode(name);

	String codesetid = "";
	if (menuid != null && "2".equals(menuid))
	    codesetid = "49";
	else if (menuid != null && "3".equals(menuid))
	    codesetid = "50";

	RecordVo vo = new RecordVo("codeitem");
	vo.setString("codesetid", codesetid);
	vo.setString("codeitemid", codeitemid);
	ContentDAO dao = new ContentDAO(this.frameconn);
	try
	{
	    String sql = "select * from codeitem where codesetid='"+codesetid+"' and codeitemid='"+codeitemid+"'";
	    RowSet rs = dao.search(sql);
	    if(rs.next())//编辑
	    {
		vo.setString("codeitemdesc", name);
		dao.updateValueObject(vo);
	    }else//新增
	    {
		//代码或代码名称不可以重复		
		vo.setString("codeitemdesc", name);
		vo.setInt("flag", 0);
		vo.setInt("invalid", 1);
		vo.setString("parentid", codeitemid);
		vo.setString("childid", codeitemid);
		dao.addValueObject(vo);
	    }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}

    }

}
