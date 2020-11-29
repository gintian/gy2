package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:SaveKeyMatterTrans.java</p>
 * <p>Description:保存关健事件交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveKeyMatterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String dateSQ=(String)hm.get("dateSQ");
		
		String eventType = (String) this.getFormHM().get("eventType");	
		RecordVo votemp = (RecordVo) this.getFormHM().get("keyEventVo");	
		String event_id = votemp.getString("event_id");
		
//		String orgCode = (String) this.getFormHM().get("code");		
//		String logoMark = (String)hm.get("logoMark");
		
		String b0110 = "";
		String e0122 = "";
		String a0101 = "";
		String a0100 = "";
		
//		if(orgCode!=null && orgCode.trim().length()>0)
//		{
//			b0110 = votemp.getString("b0110");
//			e0122 = votemp.getString("e0122");
//			a0101 = votemp.getString("a0101");
//			a0100 = votemp.getString("a0100");
			
//		}else
		{
			b0110 = (String) this.getFormHM().get("objectB0110");
			e0122 = (String) this.getFormHM().get("objectE0122");
			a0101 = (String) this.getFormHM().get("objectName");
			a0100 = (String) this.getFormHM().get("objectA0100");
		}
		
		String nbase = votemp.getString("nbase");
		String key_event = votemp.getString("key_event");
		String object_type = votemp.getString("object_type");
		String temp = votemp.getString("busi_date");/*通过这样写已经可以取出完整日期了*/
	//	Date busi_date = votemp.getDate("busi_date");
	//	System.out.println("---"+temp+"="+busi_date);
		String point_id = votemp.getString("point_id");
		String score = votemp.getString("score");
		score = new Float(PubFunc.round(score, 2)).toString();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("per_key_event");
		vo.setString("b0110", b0110);
		vo.setString("e0122", e0122);
		vo.setString("a0101", a0101);
		vo.setString("a0100", a0100);
		vo.setString("nbase", nbase);
		vo.setString("key_event", key_event);
		vo.setString("object_type", object_type);
	//	vo.setDate("busi_date", busi_date);
		vo.setDate("busi_date", dateSQ);
		vo.setString("score", score);   
		vo.setString("point_id", point_id);  
		vo.setString("status", "01");   
		
		if("-1".equalsIgnoreCase(eventType))
			vo.setString("key_set", ""); 
		else
			vo.setString("key_set", eventType);
		
		if (event_id == null || "".equals(event_id))// 添加
		{
		    IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		    event_id = idg.getId("per_key_event.event_id");
		    Integer eventid = new Integer(event_id);
		    vo.setString("event_id", eventid.toString());
		    dao.addValueObject(vo);
		    
		} else
		{
		    try
		    {
		    	vo.setString("event_id", event_id);
		    	dao.updateValueObject(vo);
		    	
		    } catch (SQLException e)
		    {
		    	throw new GeneralException("更新数据异常");
		    }
		}
		
		String type = (String) hm.get("type");
		hm.remove("type");
		String nowDate = PubFunc.getStringDate("yyyy-MM-dd");
		if("save_continue".equals(type))
		{
		    vo.setString("event_id", "");
		    vo.setString("b0110", b0110);
		    vo.setString("e0122", e0122);
		    vo.setString("a0101", a0101);
		    vo.setString("a0100", a0100);
		    vo.setString("nbase", nbase);
		    vo.setString("key_event", "");
		    vo.setString("object_type", object_type);
		    vo.setDate("busi_date", nowDate);
		    vo.setString("score", "");
		    this.getFormHM().put("act", "add");	    
		    this.getFormHM().put("keyEventVo", vo);
		    this.getFormHM().put("eventType", "");
//		    this.getFormHM().put("objectName", "");
		}	
    }
}
