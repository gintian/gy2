package com.hjsj.hrms.interfaces.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

public class PerMainBody {
	private ContentDAO dao;
	
	/**数据库连接*/
	private Connection conn;
	/**被考核人号*/
	private String object_id;
	/**考核人号*/
	private String mainbody_id;
	/**考核计划号*/
	private String plan_id;
	public PerMainBody(Connection conn,String object_id,String mainbody_id,String plan_id)
	{
		this.object_id=object_id;
		this.mainbody_id=mainbody_id;
		this.plan_id=plan_id;
		this.conn=conn;
		
	}
	
	
	/**
	 * 
	 * @param conn
	 */
	public PerMainBody(Connection conn)
	{
		this.conn=conn;	
		this.dao=new ContentDAO(conn);
	}
	/**
	 * 求打分状态
	 * @param object_id
	 * @param mainbody_id
	 * @param plan_id
	 * @return返回编辑状态
	 */
	public String getEditStatus(String object_id,String mainbody_id,String plan_id)throws GeneralException
	{
		String status="0";
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		strsql.append("select status from per_mainbody where object_id=? and mainbody_id=? and plan_id=?");
		list.add(object_id);
		list.add(mainbody_id);
		list.add(plan_id);
		//RowSet set=null;
		Connection con=null;
		ResultSet set=null;
		try
		{
			con=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			set=dao.search(strsql.toString(),list);
			if(set.next())
				status=set.getString("status");
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
   	     	throw GeneralExceptionHandler.Handle(ee); 
		}
		finally
		{	try
			{
				if(set!=null)
					set.close();
				if(con!=null)
					con.close();
			}
			catch(Exception eee)
			{
				eee.printStackTrace();
			}
		}
		return status;
	}
	
	/**
	 * 
	 * @param object_id
	 * @param mainbody_id
	 * @param plan_id
	 * @param status
	 */
	public void updateEditStatus(String object_id,String mainbody_id,String plan_id,String status)
	{
		ContentDAO dao=new ContentDAO(conn);
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		strsql.append("update per_mainbody set status=? where object_id=? and mainbody_id=? and plan_id=?");
		list.add(status);
		list.add(object_id);
		list.add(mainbody_id);
		list.add(plan_id);
		try
		{
			dao.update(strsql.toString(),list);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	
	
	
	

	/** 过滤数据库中已存在的对象集 */
	private String getEfficiencyWhl(String sql,ArrayList selectedID) 
	{
		StringBuffer whl_sql=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet frowset=dao.search(sql.toString());
			ArrayList existID=new ArrayList();
	    	
	    	while(frowset.next())
	    	{
	    		existID.add(frowset.getString("mainbody_id"));
	    		
	    	}	
	    	//frowset.close();
	    	/**  排除选中的对象中已存在的对象  */
	    	for(Iterator t=selectedID.iterator();t.hasNext();)
	    	{	
	    		String temp=(String)t.next();
	    		boolean isexist=false;   //判断是否已经存在
	    		for(Iterator t2=existID.iterator();t2.hasNext();)
	    		{
	    			String temp2=(String)t2.next();
	    			if(temp2.equals(temp))
	    			{
	    				isexist=true;
	    				break;
	    			}		
	    		}
	    		if(isexist)
	    			continue;
	    		whl_sql.append(" or A0100='"+temp+"'");
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(whl_sql.toString().trim().length()>0)
			return " where "+whl_sql.substring(4);
		else
		{
			whl_sql.append(" where 1=2");
			return whl_sql.toString();
		}
	}
	
	
	
	
	
	
	
	/**   保存考核主体 */
	public void saveMainBody(ArrayList bodyIdList,String  plan_id,String objectID,String bodyID)
	{
		StringBuffer sql=new StringBuffer("select B0110,E0122,E01A1,A0100,A0101 from UsrA01   ");
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl_sql=new StringBuffer("");
//			StringBuffer sql2=new StringBuffer("select mainbody_id from per_mainbody where ( ");
//			StringBuffer whl_sql2=new StringBuffer("");
//						
//			ArrayList selectedID=new ArrayList();  //选中的人员的id
//			for(Iterator t=bodyIdList.iterator();t.hasNext();)
//	        {
//	            String temp=(String)t.next();
//        		selectedID.add(temp);
//        		whl_sql2.append(" or mainbody_id='"+temp+"'");	
//        	}
//			sql2.append(whl_sql2.substring(4)+" ) and plan_id="+plan_id+" and object_id='"+objectID+"'");

			StringBuffer sql2=new StringBuffer("select mainbody_id from per_mainbody where 1=1 ");
			StringBuffer whl_sql2=new StringBuffer("and  mainbody_id in (");
						
			ArrayList selectedID=new ArrayList();  //选中的人员的id
			for(Iterator t=bodyIdList.iterator();t.hasNext();)
			{
			    String temp=(String)t.next();
			    selectedID.add(temp);
			    whl_sql2.append("'"+temp+"',");	
			}
			if(bodyIdList.size()>0)
			{
			    whl_sql2.setLength(whl_sql2.length()-1);
			    whl_sql2.append(") ");
			    sql2.append(whl_sql2.toString());
			}
			sql2.append(" and plan_id="+plan_id+" and object_id='"+objectID+"'");
			
			/** 过滤数据库中已存在的对象集 */
	    	sql.append(getEfficiencyWhl(sql2.toString(),selectedID));

	    	/**  get 相应考核计划的指标因子集合  */
			StringBuffer pp_sql=new StringBuffer("select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e");
			pp_sql.append(" where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id="+plan_id);
						
				RowSet frowset=dao.search(pp_sql.toString());
				ArrayList per_point_list=new ArrayList();
				while(frowset.next())
				{
					per_point_list.add(frowset.getString("point_id"));
				}								
				frowset=dao.search(sql.toString());
			    ArrayList objectList=new ArrayList();   //插入考核主体表中的对象
			    ArrayList prePointPrivList=new ArrayList();  //插入考核主体要素权限表的对象
			   
			    
			    DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("per_pointpriv_"+plan_id);
			    
				while(frowset.next())
				{
					RecordVo vo=new RecordVo("per_mainbody");
					RecordVo vo2=new RecordVo("per_pointpriv_"+plan_id);					
					
					IDGenerator idg=new IDGenerator(2,this.conn);
					String id=idg.getId("per_mainbody.id");					
		            vo.setInt("id",Integer.parseInt(id));
		            vo2.setInt("id",Integer.parseInt(id));		            
		            vo.setString("b0110",frowset.getString("b0110"));	
		            vo2.setString("b0110",frowset.getString("b0110"));		            
		            vo.setString("e0122",frowset.getString("e0122"));
		            vo2.setString("e0122",frowset.getString("e0122"));		            
		            vo.setString("e01a1",frowset.getString("e01a1"));	
		            vo2.setString("e01a1",frowset.getString("e01a1"));	            
		            vo.setString("mainbody_id",frowset.getString("a0100"));
		            vo.setString("object_id",objectID);
		            vo2.setString("mainbody_id",frowset.getString("a0100"));
		            vo2.setString("object_id",objectID);
		            vo.setInt("body_id",Integer.parseInt(bodyID));
		            vo.setString("a0101",frowset.getString("a0101"));	
		            vo2.setString("bodyname",frowset.getString("a0101"));	            
		            vo.setInt("plan_id",Integer.parseInt(plan_id));
		            vo.setInt("status",0);			           
		            for(Iterator t=per_point_list.iterator();t.hasNext();)
		            {
		            	String temp="C_"+(String)t.next();
		            	vo2.setInt(temp.toLowerCase(),1);
		            }		            
		            objectList.add(vo);
		            prePointPrivList.add(vo2);
				}
								
				dao.addValueObject(objectList);
				dao.addValueObject(prePointPrivList);
						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	

}
