package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:初始化赋分原因</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 29, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class InitScoreCauseTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)hm.get("objectid");
			String plan_id=(String)hm.get("plan_id");
			String userID=(String)hm.get("userID");
			String point_id=(String)hm.get("point_id");
			String type=(String)hm.get("type");  // 0；360度打分 1:目标
			String opt="0";
			
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
			
			if(hm.get("opt")!=null)
			{
				opt=(String)hm.get("opt");
				hm.remove("opt");
			}
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String objectStatus="0";
			String scoreCause="";
			RowSet rowSet=null;
			
			this.frowset=dao.search("select * from per_plan where plan_id="+plan_id);
			if(this.frowset.next())
			{
				if(this.frowset.getString("method")!=null&& "2".equals(this.frowset.getString("method")))
					type="1";
				else
					type="0";
			}
			
			
			if("0".equals(type))
			{
				if(userID.equalsIgnoreCase(object_id))
				{
					RecordVo plan_vo=new RecordVo("per_plan");
					plan_vo.setInt("plan_id",Integer.parseInt(plan_id));
					plan_vo=dao.findByPrimaryKey(plan_vo);
					
					if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
					{
						rowSet=dao.search("select * from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
						if(rowSet.next())
						{
							userID=rowSet.getString("mainbody_id");
						}
					}
				}
				rowSet=dao.search("select status from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+userID+"' and object_id='"+object_id+"'");
				if(rowSet.next())
					objectStatus=rowSet.getString("status");
				
				 DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
	             if(!dbmodel.isHaveTheTable("per_table_"+plan_id))
						 dbmodel.reloadTableModel("per_table_"+plan_id);
	             rowSet=dao.search("select * from per_table_"+plan_id+" where 1=2");
				 ResultSetMetaData data=rowSet.getMetaData();
				 boolean isReasons=false;
				 for(int i=0;i<data.getColumnCount();i++)
				 {
					 String name=data.getColumnName(i+1).toLowerCase();
					 if("Reasons".equalsIgnoreCase(name))
						 isReasons=true;
				 }
				 if(!isReasons)
				 {
					 Table table=new Table("per_table_"+plan_id);
					 Field obj=new Field("Reasons","Reasons");	
					 obj.setDatatype(DataType.CLOB);
					 obj.setKeyable(false);			
					 obj.setVisible(false);
					 obj.setAlign("left");		
					 table.addField(obj);
					 DbWizard dbWizard=new DbWizard(this.getFrameconn());
					 dbWizard.addColumns(table);
				 }
				 dbmodel.reloadTableModel("per_table_"+plan_id);
				 rowSet=dao.search("select * from per_table_"+plan_id+" where  object_id='"+object_id+"' and mainbody_id='"+userID+"' and point_id='"+point_id+"'");
				 if(rowSet.next())
				 {
					 scoreCause=Sql_switcher.readMemo(rowSet,"reasons");
				 }
				 
				 if("1".equals(opt))
					 objectStatus="2";
			}
			else  //目标
			{
				rowSet=dao.search("select status from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+userID+"' and object_id='"+object_id+"'");
				if(rowSet.next())
					objectStatus=rowSet.getString("status");
				rowSet=dao.search("select * from per_target_evaluation where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+userID+"' and p0400='"+point_id+"'");
				if(rowSet.next())
			    {
					 scoreCause=Sql_switcher.readMemo(rowSet,"reasons");
				}
				 
				if("1".equals(opt))
					 objectStatus="2";
			}
			 
			 this.getFormHM().put("scoreCause", scoreCause);
			 this.getFormHM().put("objectStatus", objectStatus == null ? "0" : objectStatus);
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
