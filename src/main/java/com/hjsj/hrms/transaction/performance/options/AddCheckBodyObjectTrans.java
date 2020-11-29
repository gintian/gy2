package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class AddCheckBodyObjectTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	String body_id = (String)this.getFormHM().get("body_id");
	String name = (String)this.getFormHM().get("name");
	String status = (String)this.getFormHM().get("status");
	String level = (String)this.getFormHM().get("level");
	String bodyType = (String)this.getFormHM().get("bodyType");
	String scope = (String)this.getFormHM().get("scope");
	String noself = (String)this.getFormHM().get("noself");
	
	String type = (String) this.getFormHM().get("type");
	this.getFormHM().put("type", type);
	name = SafeCode.decode(name);
		
//	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
//	RecordVo votemp = (RecordVo) this.getFormHM().get("checkbodyobjectvo");
//	String body_id = votemp.getString("body_id");
//	String name = votemp.getString("name");
//	String status = votemp.getString("status");

	String levelFild = "level";
	if (Sql_switcher.searchDbServer() == Constant.ORACEL)
	    levelFild = "level_o";

//	String bodyType = (String) this.getFormHM().get("bodyType");
	RecordVo vo = new RecordVo("per_mainbodyset");
	vo.setString("name", name);
	vo.setString("status", status);
	if (bodyType != null && "0".equals(bodyType))
	{
//	    String level = votemp.getString(levelFild);
	    vo.setString(levelFild, level);
	}
	vo.setString("body_type", bodyType);
	if("1".equals(bodyType))
	{
		String object_type = (String)this.getFormHM().get("object_type");
		if(object_type.length()>0)
			vo.setInt("object_type", Integer.parseInt(object_type));
		else
			vo.setString("object_type", null);
	}
	if(noself!=null&&!"1".equals(noself)&&bodyType != null && "0".equals(bodyType)){
		vo.setString("scope", scope);	
	}
	
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	if ("".equals(body_id) || body_id == null)
	{
	    IDGenerator idg = new IDGenerator(2, this.getFrameconn());
	    body_id = idg.getId("per_mainbodyset.body_id");
	    vo.setString("body_id", body_id);
	    vo.setString("seq", "" + this.getSeq());
	    dao.addValueObject(vo);

	} else
	{
	    try
	    {
		vo.setString("body_id", body_id);
		dao.updateValueObject(vo);
	    } catch (SQLException e)
	    {
		throw new GeneralException("更新数据异常");
	    }
	}

//	String type = (String) hm.get("type");
//	hm.remove("type");
//	if (type.equals("save_continue"))
//	{
//	    vo.setString("body_id", "");
//	    vo.setString("name", "");
//	    vo.setString("status", "");
//	    vo.setString("seq", "");
//	    vo.setString("body_type", bodyType);
//	    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
//		vo.setString("level_o", "6");
//	    else
//		vo.setString("level", "6");
//	    this.getFormHM().put("checkbodyobjectvo", vo);
//	}
    }

    public synchronized int getBodyId() throws GeneralException
    {

	int num = 0; // 序号默认为0
	String sql = "select max(Body_id) as num  from per_mainbodyset";
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    this.frowset = dao.search(sql.toString());
	    if (this.frowset.next())
	    {
		num = this.frowset.getInt("num");
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return num + 1;
    }

    public synchronized int getSeq() throws GeneralException
    {

	int num = 0; // 序号默认为0
	String sql = "select max(seq) as num  from per_mainbodyset";
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    this.frowset = dao.search(sql.toString());
	    if (this.frowset.next())
	    {
		num = this.frowset.getInt("num");
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return num + 1;
    }
}
