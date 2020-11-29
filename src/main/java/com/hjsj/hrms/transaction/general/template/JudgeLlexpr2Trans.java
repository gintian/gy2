package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class JudgeLlexpr2Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			
			String setname=(String)this.getFormHM().get("setname");
			String ins_id=(String)this.getFormHM().get("ins_id");
			String task_id=(String)this.getFormHM().get("task_id");
			String tab_id=(String)this.getFormHM().get("tabid");
			String infor_type=(String)this.getFormHM().get("infor_type");
			String task_sp_flag=(String)this.getFormHM().get("task_sp_flag");
			String businessModel=(String)this.getFormHM().get("businessModel");
			String businessModel_yp=(String)this.getFormHM().get("businessModel_yp");
		
			
			/**批量审批*/
			ArrayList tasklist=null;
			String sp_batch=(String)this.getFormHM().get("sp_batch");
			if(sp_batch==null|| "".equals(sp_batch))
				sp_batch="0";//单个任务审批
			if("1".equals(sp_batch))
			{ 
					String batch_task=(String)this.getFormHM().get("batch_task");
					tasklist=getTaskList(batch_task); 
			}
			else
			{
				tasklist=new ArrayList();
				tasklist.add(task_id);
			}		 
		 
			StringBuffer objStr=new StringBuffer("");
			ArrayList objlist=new ArrayList();	 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			 
			
			StringBuffer buf=new StringBuffer();
			buf.append("select * from ");
			buf.append(setname);
			if(!"0".equals(ins_id))
			{
						boolean isSeqnum=isSeqnum(ins_id,tab_id);
								
						buf.append(" where 1=1 ");
						if(isSeqnum)
						{
							buf.append(" and exists (select null from t_wf_task_objlink where "+setname+".seqnum=t_wf_task_objlink.seqnum and "+setname+".ins_id=t_wf_task_objlink.ins_id  ");
							if(!"2".equals(task_sp_flag)&&!"3".equals(businessModel_yp)){
								buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
							}
						}
						if("1".equals(sp_batch))
						{
							buf.append(" and  submitflag=1 and  task_id in (");						
							for(int i=0;i<tasklist.size();i++)
							{
								if(i!=0)
									buf.append(",");
								buf.append(tasklist.get(i));
							}
							buf.append(")");
						}
						else
						{
							if(task_id.trim().length()>0)
							{
								if("3".equals(businessModel_yp)&&!isSeqnum) //已批
								{
									buf.append("  and submitflag=1 and ins_id=(select ins_id from t_wf_task where task_id="+task_id+")"); 
								}
								else
								{
									buf.append("  and submitflag=1 and task_id=");
									buf.append(task_id);
								}
							}
							else  //绩效面谈用到
							{
								buf.append(" and submitflag=1 and ins_id=");
								buf.append(ins_id);
							}
						}
						if(isSeqnum)
							buf.append(" and (state is null or state<>3) ) ");
				}else
				{ 
						buf.append(" where submitflag=1");					
				}
				buf.append(" order by A0000");  // 加默认顺序, 解决9054：中船_打印预演人员顺序和界面顺序显示不一致
				this.frowset=dao.search(buf.toString());
					/**求每个对应的实例*/
				/**安全平台改造,将basepre和a0100加密**/
				Des des = new Des();
				while(this.frowset.next())
				{
					if("1".equals(infor_type))
					{
						objlist.add(this.frowset.getString("basepre")+this.frowset.getString("a0100"));
					}
					
					
					if("1".equals(infor_type))
					{
						if(!"0".equals(ins_id))
							objStr.append("`"+des.EncryPwdStr(this.frowset.getString("basepre"))+"|"+des.EncryPwdStr(this.frowset.getString("a0100"))+"|"+this.frowset.getString("ins_id"));
						else
							objStr.append("`"+des.EncryPwdStr(this.frowset.getString("basepre"))+"|"+des.EncryPwdStr(this.frowset.getString("a0100"))+"|0");
					}
					else if("2".equals(infor_type))
					{
						if(!"0".equals(ins_id))
							objStr.append("`"+des.EncryPwdStr(this.frowset.getString("b0110"))+"|"+des.EncryPwdStr(this.frowset.getString("b0110"))+"|"+this.frowset.getString("ins_id")); 
						else
							objStr.append("`"+des.EncryPwdStr(this.frowset.getString("b0110"))+"|"+des.EncryPwdStr(this.frowset.getString("b0110"))+"|0"); 
					}
					else if("3".equals(infor_type))
					{
						if(!"0".equals(ins_id))
							objStr.append("`"+des.EncryPwdStr(this.frowset.getString("E01A1"))+"|"+des.EncryPwdStr(this.frowset.getString("E01A1"))+"|"+this.frowset.getString("ins_id")); 
						else
							objStr.append("`"+des.EncryPwdStr(this.frowset.getString("E01A1"))+"|"+des.EncryPwdStr(this.frowset.getString("E01A1"))+"|0"); 
					}
					 
				}	
				
				if(objStr.length()>0)
					this.getFormHM().put("objStr",objStr.substring(1));
				else
					this.getFormHM().put("objStr","");
				if(!"1".equals(infor_type))
				{
					this.getFormHM().put("judgeisllexpr","1");
					return;
				}  
				if(objlist.size()==0)
					throw new GeneralException("未选中打印对象!");
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
				
				if(tablebo.getOperationtype()!=0){
					if("1".equals(infor_type))
						judgeIsLlexpr(objlist,tablebo.getLlexpr());
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	//兼容以前版本产生的数据
	boolean isSeqnum(String ins_id,String tabid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select seqnum from templet_"+tabid+" where ins_id="+ins_id);
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0)
					flag=true;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
		private void judgeIsLlexpr(ArrayList objlist,String llexpr)
		{

			
			HashMap hm=new HashMap();
			ArrayList a0100lists=null;
			String first_base=null;
			String a0100=null;
			try{
				for(int i=0;i<objlist.size();i++)
				{
					String obj_id=(String)objlist.get(i);
					if(obj_id==null|| "".equals(obj_id))
						continue;
					String pre=obj_id.substring(0,2).toLowerCase();
					/**对人员信息群时，过滤单位、部门及职位*/
					if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
						continue;
					pre=obj_id.substring(0,3).toLowerCase();
					/**按人员库进行分类*/
					if(!hm.containsKey(pre))
					{
						a0100lists=new ArrayList();
					}
					else
					{
						a0100lists=(ArrayList)hm.get(pre);
					}
					a0100lists.add(obj_id.substring(3));
					if(i==0)
					{
						first_base=pre;
						a0100=obj_id.substring(3);
					}
					hm.put(pre,a0100lists);
				}//for i loop end.
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
			
			
			
			
		    /**加规则过滤*/
			ArrayList alUsedFields=null;
			String temptable=null;
			if(llexpr!=null && llexpr.trim().length()>0)
		    { 
			  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			  temptable=createSearchTempTable(this.getFrameconn());
		    }
			
		    Iterator iterator=hm.entrySet().iterator();
		    StringBuffer judgeSql=new StringBuffer();

			while(iterator.hasNext())
			{

				Entry entry=(Entry)iterator.next();
				String pre=entry.getKey().toString();
				ArrayList a0100list =(ArrayList)entry.getValue();
				
				
			    judgeSql.append("select A0101,a0100,'"+pre.toLowerCase()+"' basepre from ");
				judgeSql.append(pre);
				judgeSql.append("A01 where a0100 in(''");
				for(int i=0;i<a0100list.size();i++)
				{
					judgeSql.append(",'");
					judgeSql.append(a0100list.get(i));
					judgeSql.append("'");
				}
				judgeSql.append(")");
				judgeSql.append(" and ");
				judgeSql.append(getFilterSQL(temptable,pre,alUsedFields,llexpr));
				judgeSql.append(" UNION ");
				//System.out.println(a0100list + pre);		
			}
			if(judgeSql.length()>7)
			{
			  judgeSql.setLength(judgeSql.length()-7);
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
		      RowSet rset=null;
		      String judgedesc="";
		      boolean bl=false;
		      
		      try
		      {
		    	  Des des = new Des();
		    	  rset=dao.search(judgeSql.toString());
		   		  StringBuffer _str=new StringBuffer("");
		    	  while(rset.next())
		    	  {
		    		  if(bl==false)
		    		  {
		    			  judgedesc=rset.getString("a0101");
		    			  bl=true;
		    		  }else
		    		  {
		    			  judgedesc+="," + rset.getString("a0101");  
		    		  }	  
		    		  _str.append(","+des.EncryPwdStr(rset.getString("basepre"))+des.EncryPwdStr(rset.getString("a0100")));
		          }
		    	  if(bl)
		    	  { 
		    		  this.getFormHM().put("remove_a0100",_str.toString());
		    		  this.getFormHM().put("judgeisllexpr",judgedesc + ResourceFactory.getProperty("general.template.ishavenotjudge"));
		    	  }else
		    	  {
		    		  this.getFormHM().put("judgeisllexpr","1");
		    	  }
		      }
		      catch(Exception ex)
		      {
		    	  ex.printStackTrace();
		      }
			}else
			{
				this.getFormHM().put("judgeisllexpr","1");
			}
		
		
		}
		private String getFilterSQL(String temptable,String BasePre,ArrayList alUsedFields,String llexpr)
		{
			String sql=" (1=2)";
			try{
			if(llexpr!=null && llexpr.length()>0)
			{
				StringBuffer inserSql=new StringBuffer();
				inserSql.append("insert into ");
				inserSql.append(temptable);
				inserSql.append("(a0100) select '");
				inserSql.append(BasePre);
				inserSql.append("' "+Sql_switcher.concat()+"a0100 from ");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				//this.filterfactor="性别 <> '1'";
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic								
				String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
				whereIN="select a0100 "+whereIN;							
				YksjParser yp = new YksjParser( this.userView ,alUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
				YearMonthCount ymc=null;							
				yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.getFrameconn(),"A", null);
				String tempTableName = yp.getTempTableName();
				sql="('" + BasePre + "'" +Sql_switcher.concat()+"" +  BasePre +"A01.a0100 in  (select distinct a0100 from " + temptable + "))";
				inserSql.append(tempTableName);
				inserSql.append(" where " + yp.getSQL());
				dao.insert(inserSql.toString(),new ArrayList());	
			}	
			}catch(Exception e)
			{
				e.printStackTrace();
			}	
			return sql;
		}
		private String createSearchTempTable(Connection conn)
		{
			String temptable="temp_search_xry_01";
			try{
				StringBuffer sql=new StringBuffer();
				sql.delete(0,sql.length());
				sql.append("drop table ");
				sql.append(temptable);
				try{
				  ExecuteSQL.createTable(sql.toString(),conn);
				}catch(Exception e)
				{
					//e.printStackTrace();
				}
				sql.delete(0,sql.length());
				sql.append("CREATE TABLE ");
				sql.append(temptable);
				//sql.append("(a0100  varchar (100) PRIMARY KEY (a0100))");
				sql.append("(a0100  varchar (100))");
				try{
					  ExecuteSQL.createTable(sql.toString(),conn);				  			  
			    }catch(Exception e)
				{
					e.printStackTrace();
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return temptable;
		}
		
		private ArrayList getTaskList(String batch_task)throws GeneralException
		{
			String[] lists=StringUtils.split(batch_task,",");
			ArrayList list=new ArrayList();
			for(int i=0;i<lists.length;i++)
				list.add(lists[i]);
			return list;
			
		}
}
