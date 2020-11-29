package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter.KeyMatterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:AddKeyMatterTrans.java</p>
 * <p>Description:添加关健事件交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class AddKeyMatterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String eventId = (String) hm.get("eventId");
		hm.remove("eventId");
		
		String nowDate = PubFunc.getStringDate("yyyy-MM-dd");
		
		String mark = (String) hm.get("mark");  //  标志参数
		hm.remove("mark");
		String object_Name = (String) hm.get("ObjectName");
		hm.remove("ObjectName");
		object_Name=SafeCode.decode(object_Name);
		
		if((mark==null) || (mark.trim().length()<=0))		    
	    {}
		else
		{
			this.getFormHM().put("objectName", object_Name);	
			
/*			RecordVo vo = new RecordVo("per_key_event");
			vo.setString("key_event", "");
			vo.setString("score", "");
			vo.setString("point_id","");
			vo.setString("key_set", "");
			vo.setDate("busi_date", nowDate);
			this.getFormHM().put("keyEventVo", vo);			
			this.getFormHM().put("pointName", "");	
			this.getFormHM().put("eventType", "");
*/			return;
		}
			
		String objectType = (String) this.getFormHM().get("objectType");		
		String userbase = (String) this.getFormHM().get("userbase");
		String orgCode = (String) this.getFormHM().get("code");
		String kind = (String) this.getFormHM().get("kind");
		String unit = "";
		String dept = "";
		String pCode = "";
		String name = "";
		KeyMatterBo bo = new KeyMatterBo(this.getFrameconn());
		if (("1".equals(objectType)) && ((eventId == null || "".equals(eventId))))// 新增团队关键事件
		{
		    if ("2".equals(kind))// 单位
		    {
		    	HashMap map = bo.getUnitUNUM(orgCode, "UN");
			    if (map.size() > 0)
			    {
					name = (String) map.get("codeitemdesc");					
//					unit = (String) map.get("codeitemid");					
			    }			    
		    	unit = orgCode;
		    }
		    else if ("1".equals(kind))// 部门
		    {
		    	HashMap map = bo.getUnitUNUM(orgCode, "UM");
			    if (map.size() > 0)
			    {
					name = (String) map.get("codeitemdesc");					
//					unit = (String) map.get("codeitemid");					
			    }
				dept = orgCode;
				unit = orgCode;
		    }
		} else if (("2".equals(objectType)) && ((eventId == null || "".equals(eventId))))// 新增个人关键事件
		{
			if(!"4".equals(kind))
			{
			    HashMap map = bo.getUnitDept(orgCode, userbase);
			    if (map.size() > 0)
			    {
					name = (String) map.get("name");
					dept = (String) map.get("dept");
					unit = (String) map.get("unit");
					pCode = orgCode;
			    }
			}
		}
	
		ArrayList eventTypeList=new ArrayList();    // 事件类别
		RecordVo vo = new RecordVo("per_key_event");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet = null;
		RowSet rs = null;
		try
		{
						
			StringBuffer str = new StringBuffer();
			str.append("select codeitemid,codeitemdesc from codeitem where codesetid='67'");
			rowSet = dao.search(str.toString());
			
			eventTypeList.add(new CommonData("-1","请选择..."));
			while(rowSet.next())
			{
				String codeitemid = rowSet.getString("codeitemid");
				if(!(this.userView.isSuper_admin()))
				{
					if(!this.userView.isHaveResource(IResourceConstant.KEY_EVENT,codeitemid))      //关键事件权限
					{
						continue;
					}					
				}
				
				eventTypeList.add(new CommonData(rowSet.getString("codeitemid"),rowSet.getString("codeitemdesc")));				
			}
		
		    if (eventId == null || "".equals(eventId))// 新增
		    {
				vo.setString("event_id", "");
				vo.setString("b0110", unit);
				vo.setString("e0122", dept);
				vo.setString("a0101", name);
				vo.setString("a0100", pCode);
				vo.setString("nbase", userbase);
				vo.setString("key_event", "");
				vo.setString("object_type", objectType);
				vo.setDate("busi_date", nowDate);
				vo.setString("score", "");
				vo.setString("point_id","");
				this.getFormHM().put("act", "add");
				this.getFormHM().put("pointName", "");
				this.getFormHM().put("eventType", "");
//				if((mark==null) || (mark.trim().length()<=0))
//				{
					this.getFormHM().put("objectName", name);
			    	this.getFormHM().put("objectB0110", unit);
			    	this.getFormHM().put("objectE0122", dept);
			    	this.getFormHM().put("objectA0100", pCode);			    	
//				}
//				else
//					this.getFormHM().put("objectName", object_Name);
			    	
			    this.getFormHM().put("sign", "false");
			    
		    } else
		    // 编辑
		    {
				//vo.setString("event_id", eventId);
				//vo = dao.findByPrimaryKey(vo);
		    	String key_set = "";
		    	String objectName = "";
				StringBuffer strsql = new StringBuffer();
				strsql.append("select * from per_key_event where event_id=");
				strsql.append(eventId);
				String pointId="";
				rs = dao.search(strsql.toString());
				if (rs.next())
				{
					objectName = rs.getString("a0101")!=null?rs.getString("a0101"):"";	
				    vo.setString("event_id", rs.getString("event_id"));
				    vo.setString("b0110", rs.getString("b0110"));
				    vo.setString("e0122", rs.getString("e0122"));
				    vo.setString("a0101", rs.getString("a0101"));
				    vo.setString("a0100", rs.getString("a0100"));
				    vo.setString("nbase", rs.getString("nbase"));
				    vo.setString("key_event", rs.getString("key_event"));
				    vo.setString("object_type", rs.getString("object_type"));
				    vo.setDate("busi_date", rs.getDate("busi_date"));
				    pointId= rs.getString("point_id");
				    vo.setString("point_id",pointId);
				    String score = rs.getString("score");
				    vo.setString("score", bo.getScore(PubFunc.round(score, 2)));
				    key_set = rs.getString("key_set")!=null?rs.getString("key_set"):"";		
				    
				    this.getFormHM().put("objectName", rs.getString("a0101"));
			    	this.getFormHM().put("objectB0110", rs.getString("b0110"));
			    	this.getFormHM().put("objectE0122", rs.getString("e0122"));
			    	this.getFormHM().put("objectA0100", rs.getString("a0100"));	
				}
				if(pointId!=null && pointId.trim().length()>0)
				{
				    String pointName="";
				    RecordVo point_vo = new RecordVo("per_point");
				    point_vo.setString("point_id", pointId.toUpperCase());		
				    point_vo = dao.findByPrimaryKey(point_vo);		
				    pointName= point_vo.getString("pointname");
				    if(pointName!=null)
				    	this.getFormHM().put("pointName", pointName);
				    else
				    	this.getFormHM().put("pointName", "");
				} else
					this.getFormHM().put("pointName", "");
				
				this.getFormHM().put("act", "edit");								
				this.getFormHM().put("eventType", key_set);
		    }
		    
		    if(rowSet!=null)
		    	rowSet.close();
		    if(rs!=null)
		    	rs.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		} finally
		{
		    this.getFormHM().put("keyEventVo", vo);		    
		    this.getFormHM().put("eventTypeList", eventTypeList);
		}
    }
}
