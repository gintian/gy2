package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
/**
 * <p>Title:TestKhObjTrans.java</p>
 * <p>Description:结果归档前验证考核对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-28 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TestKhObjTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String planid = (String)this.getFormHM().get("planID");
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	String objs="";
	boolean flag=false;
	try
	{
	    String objectType = "2";
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", planid);
		try
		{
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		if (vo.getString("object_type") != null)
		    objectType = vo.getString("object_type");
		StringBuffer buf = new StringBuffer();
		buf.append("select a0101 from per_result_" + planid);
		if ("2".equals(objectType))
		    buf.append(" WHERE NOT (object_id IN (SELECT A0100 FROM USRA01))");
		else
		    buf.append(" WHERE NOT (object_id IN (SELECT B0110 FROM B01))");
	    
	    RowSet rs = dao.search(buf.toString());
	  
	    while(rs.next())
	    {
		flag=true;
		objs+=","+rs.getString("a0101");
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	this.getFormHM().put("isExist", (flag?"true":"false"));
	this.getFormHM().put("objs",(objs.length()>0?objs.substring(1):""));

    }
}
