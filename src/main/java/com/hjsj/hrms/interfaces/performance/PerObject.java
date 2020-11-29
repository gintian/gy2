package com.hjsj.hrms.interfaces.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;


public class PerObject {
	/**数据库连接*/
	private Connection conn;
	private ContentDAO dao;
	
	
	public PerObject(Connection conn) {
		this.conn=conn;
		dao=new ContentDAO(conn);
	}

	
	
	
	/**批量插入考核对象
	 * @param objectIdList 对象id集
	 * @param plan_id      考核计划id
	 * @param objectType   对象类型  1：组织 2.人员
	 * @author dengc
	 * created:  2006-3-17
	 */
	
	public void insertObject(ArrayList objectIdList,String plan_id,String objectType)
	{
		try
		{
			//计划所属机构
			String plan_b0110 = "hjsj";
			RowSet frowset=dao.search("select b0110 from per_plan where plan_id="+plan_id);
			if(frowset.next())
				plan_b0110 = frowset.getString(1);
			
		StringBuffer sql=new StringBuffer("");
		StringBuffer whl_sql=new StringBuffer("");
		if("2".equals(objectType))
		{
			sql.append("select B0110,E0122,E01A1,A0100,A0101,a0000 from UsrA01  where  ");
			if(!"hjsj".equalsIgnoreCase(plan_b0110))
			{
				if(AdminCode.getCode("UM",plan_b0110)!=null)
					sql.append("  e0122 like '"+plan_b0110+"%' and ");
				else if(AdminCode.getCode("UN",plan_b0110)!=null)
					sql.append("  b0110 like '"+plan_b0110+"%' and ");
			}			
		}			
		else if("1".equals(objectType) || "3".equals(objectType) || "4".equals(objectType))
		{
			sql.append("select codesetid,codeitemid,codeitemdesc,parentid from organization where  ");
			if(!"hjsj".equalsIgnoreCase(plan_b0110))
			{
				sql.append("  codeitemid like '"+plan_b0110+"%' and ");
			}
		}
		
		StringBuffer sql2=new StringBuffer("select object_id from per_object where ( ");
		StringBuffer whl_sql2=new StringBuffer("");
		
		ArrayList selectedID=new ArrayList();  //选中的人员的id
		for(Iterator t=objectIdList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			whl_sql2.append(" or object_id='"+temp+"'");	
		}
		sql2.append(whl_sql2.substring(4)+" ) and plan_id="+plan_id);
		
			/** 过滤数据库中已存在的对象集 */	
	    	sql.append(" ("+getEfficiencyWhl(sql2.toString(),objectIdList,objectType)+") ");
	    	
	    	/** 进行插入操作 */	    
	    	frowset=dao.search(sql.toString());
		    ArrayList objectList=new ArrayList();
			while(frowset.next())
			{
				RecordVo vo=new RecordVo("per_object");
	
				IDGenerator idg=new IDGenerator(2,conn);
				String id=idg.getId("per_object.id");
	            vo.setInt("id",Integer.parseInt(id));
	            if("2".equals(objectType))
	            {
		            if(frowset.getString("b0110")!=null)
		            	vo.setString("b0110",frowset.getString("b0110"));
		            if(frowset.getString("e0122")!=null)
		            	vo.setString("e0122",frowset.getString("e0122"));
		            if(frowset.getString("e01a1")!=null)
		            	vo.setString("e01a1",frowset.getString("e01a1"));	          
		            if(frowset.getString("a0100")!=null)
		            	vo.setString("object_id",frowset.getString("a0100"));	       
		            if(frowset.getString("a0101")!=null)
		            	vo.setString("a0101",frowset.getString("a0101"));
		            if(frowset.getString("a0000")!=null)
		            	vo.setString("a0000",frowset.getString("a0000"));
	            }
	            else
	            {
	        	String codesetid = frowset.getString("codesetid");
	        	vo.setString("object_id",frowset.getString("codeitemid"));
	            	vo.setString("a0101",frowset.getString("codeitemdesc"));
	        	
	            	if("UN".equals(codesetid))
	            	{
	            	    vo.setString("b0110",getUNcode(frowset.getString("codeitemid")));
	            	    vo.setString("e0122","");
	            	}else if("UM".equals(codesetid))
	            	{
	            	    vo.setString("e0122",frowset.getString("codeitemid"));
	            	    vo.setString("b0110",getUNcode(frowset.getString("parentid")));
	            	}	
	            }
	            vo.setInt("plan_id",Integer.parseInt(plan_id));	            
	            objectList.add(vo);
			}	
			dao.addValueObject(objectList);

			/**     判断计划主体类别中是否包含本人，如果包含则自动插入主体和相应的权限指标 */
			if("2".equals(objectType))
			{
				boolean isSelf=false;
				int body_id=0;
				String level = "level";
				if(Sql_switcher.searchDbServer() == Constant.ORACEL)
				    level = "level_O";
				frowset=dao.search("select per_plan_body.body_id body_id from per_plan_body,per_mainbodyset where per_plan_body.body_id=per_mainbodyset.body_id and per_mainbodyset."+level+"=5  and plan_id="+plan_id);
				if(frowset.next())
				{
						body_id=frowset.getInt("body_id");
						isSelf=true;
				}
				if(isSelf)
				{
					saveMainBody2(objectIdList,plan_id,body_id);	
				}
			}
		//	frowset.close();  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 根据部门父结点id找到所属直属单位
	 * @param codeitemid 父结点id
	 * @return
	 */
	public String getUNcode(String codeitemid)
	{
		String un_codeID="";
		RowSet frowset=null;
		boolean isUn=false;
		try
		{
			while(!isUn)
			{
				frowset=dao.search("select codesetid,codeitemid,parentid from organization where  codeitemid='"+codeitemid+"'");
				if(frowset.next())
				{
					String codesetid=frowset.getString("codesetid");
					if("UN".equalsIgnoreCase(codesetid))
					{
						un_codeID=frowset.getString("codeitemid");
						isUn=true;
					}
					else
					{
						codeitemid=frowset.getString("parentid");			
					}
				}
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return un_codeID;
	}
	
	
	

	/** 过滤数据库中已存在的对象集 */
	private String getEfficiencyWhl(String sql,ArrayList selectedID,String objectType) 
	{
		StringBuffer whl_sql=new StringBuffer("");
		try
		{
			
			RowSet frowset=dao.search(sql.toString());
	    	ArrayList existID=new ArrayList();
	    	
	    	while(frowset.next())
	    	{
	    		existID.add(frowset.getString(1));
	    		
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
	    		if("2".equals(objectType))
	    			whl_sql.append(" or A0100='"+temp+"'");
	    		else if("1".equals(objectType) || "3".equals(objectType) || "4".equals(objectType))
	    			whl_sql.append(" or codeitemid='"+temp+"'");
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(whl_sql.length()>0)
			return whl_sql.substring(4);
		else
			return " 1=2 ";
	}
	
	
	/**   保存考核主体 */
	private void saveMainBody2(ArrayList fields,String plan_id,int body_id)
	{
		StringBuffer sql=new StringBuffer("select B0110,E0122,E01A1,A0100,A0101 from UsrA01 where  ");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl_sql=new StringBuffer("");
			StringBuffer sql2=new StringBuffer("select mainbody_id from per_mainbody where ( ");
			StringBuffer whl_sql2=new StringBuffer("");
						
			
			for(Iterator t=fields.iterator();t.hasNext();)
	        {
	            String temp=(String)t.next();
        		whl_sql2.append(" or (mainbody_id='"+temp+"' and object_id='"+temp+"') ");	
        	}
			sql2.append(whl_sql2.substring(4)+" ) and plan_id="+plan_id);
			
			/** 过滤数据库中已存在的对象集 */
	    	sql.append(getEfficiencyWhl(sql2.toString(),fields,"2"));
	    	
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
		            vo.setString("object_id",frowset.getString("a0100"));
		            vo2.setString("mainbody_id",frowset.getString("a0100"));
		            vo2.setString("object_id",frowset.getString("a0100"));
		            vo.setInt("body_id",body_id);
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
						
			//	frowset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	
	
	
	
}
