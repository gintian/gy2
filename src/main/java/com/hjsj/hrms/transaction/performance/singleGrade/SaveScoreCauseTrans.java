package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:赋予原因</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 29, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SaveScoreCauseTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)hm.get("objectid");
			String plan_id=(String)hm.get("plan_id");
			String userID=(String)hm.get("userID");
			String point_id=(String)hm.get("point_id");
			String type=(String)hm.get("type"); // 0:360计划  1：目标
			
			if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = object_id.substring(1); 
	        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			if(userID!=null && userID.trim().length()>0 && "~".equalsIgnoreCase(userID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = userID.substring(1); 
	        	userID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }						
			
			String scoreCause=(String)this.getFormHM().get("scoreCause");			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			if("0".equals(type))
			{
				RowSet rowSet=dao.search("select id from per_table_"+plan_id+" where  object_id='"+object_id+"' and mainbody_id='"+userID+"' and point_id='"+point_id+"'");
				int id=0;
				if(rowSet.next())
					id=rowSet.getInt("id");
			    if(id!=0)
			    {
					RecordVo vo=new RecordVo("per_table_"+plan_id);
					vo.setInt("id",id);
					vo=dao.findByPrimaryKey(vo);
					vo.setObject("reasons",scoreCause);
					dao.updateValueObject(vo);
			    }
			    else
			    {
			    	RecordVo vo=new RecordVo("per_table_"+plan_id);
			    	IDGenerator idg = new IDGenerator(2, this.getFrameconn());
					String aid = idg.getId("per_table_xxx.id");
					vo.setInt("id",Integer.parseInt(aid));
					vo.setString("object_id",object_id);
					vo.setString("mainbody_id",userID);
					vo.setString("point_id", point_id);
					vo.setObject("reasons",scoreCause);
					dao.addValueObject(vo);
			    }
			}
			else
			{
				RowSet rowSet=dao.search("select id from per_target_evaluation where plan_id="+plan_id+" and  object_id='"+object_id+"' and mainbody_id='"+userID+"' and p0400='"+point_id+"'");
				int id=0;
				if(rowSet.next())
					id=rowSet.getInt("id");
			    if(id!=0)
			    {
					RecordVo vo=new RecordVo("per_target_evaluation");
					vo.setInt("id",id);
					vo=dao.findByPrimaryKey(vo);
					vo.setObject("reasons",scoreCause);
					dao.updateValueObject(vo);
			    }
			    else
			    {
			    	RecordVo vo=new RecordVo("per_target_evaluation");
			    	IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			    	String aid = idg.getId("per_target_evaluation.id");
					vo.setInt("id",Integer.parseInt(aid));
					vo.setString("object_id",object_id);
					vo.setString("mainbody_id",userID);
					vo.setString("p0400", point_id);
					vo.setInt("plan_id",Integer.parseInt(plan_id));
					vo.setObject("reasons",scoreCause);
					dao.addValueObject(vo);
			    }
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
