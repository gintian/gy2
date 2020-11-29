package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Iterator;

public class BatchSelectMainBodyTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{	
//			得到选中的考核计划id
			String dbpre=(String)this.getFormHM().get("dbpre");
			ArrayList perObjectlist=(ArrayList)this.getFormHM().get("selectedList");
			if(perObjectlist.size()==0)
				return;		
			RecordVo constant_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR"); 
			if(constant_vo==null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("performance.implement.info4")));
			String   fieldStr=constant_vo.getString("str_value");					//直接上级字段
			if("#".equals(fieldStr))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("performance.implement.info4")));
			ArrayList bodySetList= getBodySet(dbpre);
			ArrayList objectList = new ArrayList();
			ArrayList prePointPrivList = new ArrayList();
			if(bodySetList.size()!=0)
			{	
			    ContentDAO dao=new ContentDAO(this.getFrameconn());			    
			    /**  get 相应考核计划的指标因子集合  */
				StringBuffer pp_sql=new StringBuffer("select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e");
				pp_sql.append(" where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id="+dbpre);
							
					RowSet frowset=dao.search(pp_sql.toString());
					ArrayList per_point_list=new ArrayList();
					while(frowset.next())
					{
						per_point_list.add(frowset.getString("point_id"));
					}		    
			 
			    
			  //对每个考核对象设置考核主体
				for(Iterator t=perObjectlist.iterator();t.hasNext();)
			    	{
				    String sql = "select a.mainbody_id,a.b0110,a.object_id,a.e0122,a.e01a1,a.a0101,a.body_id from per_mainbody_std a,per_object b ";
				    sql+="where a.object_id=b.object_id and a.body_id in (select body_id from per_plan_body where plan_id="+dbpre+") and b.plan_id="+dbpre;
//				    StringBuffer buf = new StringBuffer();
			    		LazyDynaBean a=(LazyDynaBean)t.next();	
			    		String object_id = (String)a.get("object_id");
//			    		buf.append(",'"+object_id+"'");
			    	
//				if(buf.length()>0)
				    sql+=" and b.object_id in ('"+object_id+"')";
//				    sql+=" and b.object_id in ("+buf.substring(1)+")";
				
				sql+=" and a.mainbody_id not in (select mainbody_id from per_mainbody where plan_id="+dbpre+" and object_id in ('"+object_id+"'))";
			
				frowset=dao.search(sql.toString());
				
				 DbWizard dbWizard = new DbWizard(this.frameconn);
				
				while(frowset.next())
				{
					RecordVo vo=new RecordVo("per_mainbody");
					RecordVo vo2 = null;
				
					 try
					{
						vo2=new RecordVo("per_pointpriv_"+dbpre);
					} catch (RuntimeException e)
					{
						  if (dbWizard.isExistTable("per_pointpriv_"+dbpre, false))
						    {
						DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
						 dbmodel.reloadTableModel("per_pointpriv_"+dbpre);
						 vo2=new RecordVo("per_pointpriv_"+dbpre);
						    }
			
					}		
					
					IDGenerator idg=new IDGenerator(2,this.frameconn);
					String id=idg.getId("per_mainbody.id");					
		            vo.setInt("id",Integer.parseInt(id));		            
		            vo.setString("b0110",frowset.getString("b0110"));		            
		            vo.setString("e0122",frowset.getString("e0122"));	            
		            vo.setString("e01a1",frowset.getString("e01a1"));			            
		            vo.setString("mainbody_id",frowset.getString("mainbody_id"));
		            vo.setString("object_id",frowset.getString("object_id"));		            
		            vo.setInt("body_id",frowset.getInt("body_id"));
		            vo.setString("a0101",frowset.getString("a0101"));		            
		            vo.setInt("plan_id",Integer.parseInt(dbpre));
		            vo.setInt("status",0); 	           
		            objectList.add(vo);
		            
		            vo2.setInt("id",Integer.parseInt(id));
		            vo2.setString("b0110",frowset.getString("b0110"));		            
		            vo2.setString("e0122",frowset.getString("e0122"));	            
		            vo2.setString("e01a1",frowset.getString("e01a1"));
		            vo2.setString("mainbody_id",frowset.getString("mainbody_id"));
		            vo2.setString("object_id",frowset.getString("object_id"));	
		            vo2.setString("bodyname",frowset.getString("a0101"));	
		            
		            for(Iterator t1=per_point_list.iterator();t1.hasNext();)
		            {
		            	String temp="C_"+(String)t1.next();
		            	vo2.setInt(temp.toLowerCase(),1);
		            }		            
		            prePointPrivList.add(vo2);
		            }	
			
			  }	
				if(objectList.size()>0)
				{
				    dao.addValueObject(objectList);
				    dao.addValueObject(prePointPrivList);
				}
//				ArrayList list=getObjectList(fieldStr,perObjectlist);
//				ArrayList  objectList=(ArrayList)list.get(0);    					   //考核对象信息
//				ArrayList  preObjectList=(ArrayList)list.get(1);  				   	  //直接上级的信息
//				ArrayList  subObjectList=(ArrayList)list.get(2);  					  //下属的信息
//				
//				PerMainBody perMainBody=new PerMainBody(this.getFrameconn());				
//				for(Iterator t=bodySetList.iterator();t.hasNext();)
//				{
//					LazyDynaBean abean=(LazyDynaBean)t.next();
//					String bodySetID=(String)abean.get("body_id");
//					String level=(String)abean.get("level");
//				//	String bodySetID=(String)t.next();
//					for(Iterator t1=objectList.iterator();t1.hasNext();)
//					{
//						String[] temp=(String[])t1.next();
//						ArrayList fieldlist=new ArrayList();
//						if(level.equals("1"))
//						{
//							for(Iterator t2=preObjectList.iterator();t2.hasNext();)
//							{
//								String[] temp2=(String[])t2.next();
//								if(temp[2].equals(temp2[1]))
//									fieldlist.add(temp2[0]);
//							}
//						}
//						else if(level.equals("3"))
//						{
//							for(Iterator t2=subObjectList.iterator();t2.hasNext();)
//							{
//								String[] temp2=(String[])t2.next();
//								if(temp2[2].equals(temp[1]))
//									fieldlist.add(temp2[0]);
//							}						
//						}					
//						perMainBody.saveMainBody(fieldlist,dbpre,temp[0],bodySetID);		
//					}
//				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	
	/**
	 * 取得相关信息集合
	 * @param fieldStr	直接上级字段
	 * @return
	 */
	public ArrayList getObjectList(String fieldStr,ArrayList perObjectlist )
	{
		ArrayList list=new ArrayList();
		ArrayList  objectList=new ArrayList();     //考核对象信息
		ArrayList  preObjectList=new ArrayList();  //直接上级的信息
		ArrayList  subObjectList=new ArrayList();  //下属的信息
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer(" select a.A0100,a.E01A1,");
			sql.append(fieldStr);
			sql.append(" from UsrA01 a,K01 k where a.E01A1=k.E01A1 and ");
			sql.append(fieldStr);
			sql.append(" is not null and ( ");			
			StringBuffer whl_sql=new StringBuffer("");
			String sql0="select object_id from per_object where  ";
			StringBuffer whl_sql0=new StringBuffer("");
			
			for(Iterator t=perObjectlist.iterator();t.hasNext();)
	    	{
	    		LazyDynaBean a=(LazyDynaBean)t.next();	
	    		whl_sql0.append(" or id="+a.get("id"));		
	    	}
			sql0+=whl_sql0.substring(3);
			this.frowset=dao.search(sql0);
			while(this.frowset.next())
			{
				whl_sql.append(" or a.A0100='"+this.frowset.getString(1)+"'");	
			}
			
			
			sql.append(whl_sql.substring(3)+" )");
			this.frowset=dao.search(sql.toString());
			
			StringBuffer  sql2=new StringBuffer("select A0100,E01A1 from UsrA01 where ");
			StringBuffer whl_sql2=new StringBuffer("");
			StringBuffer sql3=new StringBuffer(" select a.A0100,a.E01A1,");
			sql3.append(fieldStr);
			sql3.append(" from UsrA01 a,K01 k where a.E01A1=k.E01A1 and ");
			sql3.append(fieldStr);
			sql3.append(" is not null and ( ");			
			StringBuffer whl_sql3=new StringBuffer("");
			while(this.frowset.next())
			{
				String[] temp=new String[3];
				temp[0]=this.frowset.getString(1);
				temp[1]=this.frowset.getString(2);
				temp[2]=this.frowset.getString(3);
				objectList.add(temp);
				whl_sql2.append(" or E01A1='"+temp[2]+"'");	
				whl_sql3.append(" or "+fieldStr+"='");
				whl_sql3.append(temp[1]);
				whl_sql3.append("'");
			}
			if(whl_sql2.length()>0)
			{
				sql2.append(whl_sql2.substring(3));
				this.frowset=dao.search(sql2.toString());
				while(this.frowset.next())
				{
					String[] temp=new String[2];
					temp[0]=this.frowset.getString(1);
					temp[1]=this.frowset.getString(2);
					preObjectList.add(temp);
				}
			}
			if(whl_sql3.length()>0)
			{
				sql3.append(whl_sql3.substring(3)+" )");
				this.frowset=dao.search(sql3.toString());
				while(this.frowset.next())
				{
					String[] temp=new String[3];
					temp[0]=this.frowset.getString(1);
					temp[1]=this.frowset.getString(2);
					temp[2]=this.frowset.getString(3);
					subObjectList.add(temp);
				}
			}
			list.add(objectList);
			list.add(preObjectList);
			list.add(subObjectList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		return list;
	}
	
	
	
	
	/**
	 * 得到考核计划中是否包含（直接上级 1&&下属 3）的主体类别
	 * @param plan_id
	 * @return
	 */
	public ArrayList getBodySet(String plan_id)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer sql=new StringBuffer("select b.body_id,b.name");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql.append(",b.level_o");
			else
				sql.append(",b.level ");
			sql.append(" from per_plan_body a ,per_mainbodyset b where a.body_id=b.body_id ");
			sql.append(" and b.status=1 and a.plan_id="+plan_id);
			this.frowset=dao.search(sql.toString());
			{
				while(this.frowset.next())
				{
					if(this.frowset.getInt(3)==1||this.frowset.getInt(3)==3)
					{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("body_id",this.frowset.getString(1));
						abean.set("level",this.frowset.getString(3));
						list.add(abean);
					//	list.add(this.frowset.getString(1));
					}
				}
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
}
