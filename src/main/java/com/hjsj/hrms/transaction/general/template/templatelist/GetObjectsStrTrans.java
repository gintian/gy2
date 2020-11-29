package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
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

public class GetObjectsStrTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			 /* 卡片类型：
	         1: 模板
	         2: 模板归档信息
	         3: 员工申请临时表, g_templet_模板号
	         4: 审批临时表, templet_模板号
	      */
			String cardtype=(String)this.getFormHM().get("cardtype");
			String ins_id=(String)this.getFormHM().get("ins_id");
			String task_id=(String)this.getFormHM().get("task_id");
			String sp_batch=(String)this.getFormHM().get("sp_batch");
			/**安全平台改造，判断taskid是否在后台中**/
			HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(templateMap!=null&&!templateMap.containsKey(task_id)&&!sp_batch.equals("1")){//如果是批量审批的话,templateMap存放的是batch_task中的task_id，那么就不需要从这里查询了
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			String tabid=(String)this.getFormHM().get("tabid");
			String infor_type = (String)this.getFormHM().get("infor_type");
			
			String batch_task=(String)this.getFormHM().get("batch_task");
			String tablename="templet_"+tabid;
			if((task_id==null|| "0".equalsIgnoreCase(task_id))&&!"1".equals(sp_batch))
			{
				tablename=this.userView.getUserName()+"templet_"+tabid;
			}
			StringBuffer sql=new StringBuffer("select * from "+tablename+"  ");
			
			if("1".equals(sp_batch))
			{ 
				String[] lists=StringUtils.split(batch_task,",");
				sql.append(" where  exists (select null from t_wf_task_objlink where "+tablename+".seqnum=t_wf_task_objlink.seqnum and "+tablename+".ins_id=t_wf_task_objlink.ins_id  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
				sql.append(" and   task_id in (");
				StringBuffer buf=new StringBuffer("");
				for(int i=0;i<lists.length;i++)
				{
					if(lists[i]==null||lists[i].trim().length()==0)
						 continue;
					buf.append(",");
					/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
					if(templateMap!=null&&!templateMap.containsKey(lists[i])){//如果操作的taskid不在后台存在，那么禁止进一步操作
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
					}
					*/
					buf.append(lists[i]);
				}
				sql.append(buf.substring(1)+" ) ");
				sql.append(" and  (state is null or state<>3))");
			}
			else
			{
				if(!((task_id==null|| "0".equalsIgnoreCase(task_id)))){
					sql.append(" where  exists (select null from t_wf_task_objlink where "+tablename+".seqnum=t_wf_task_objlink.seqnum and "+tablename+".ins_id=t_wf_task_objlink.ins_id  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
					sql.append(" and task_id="+task_id);
					sql.append(" and  (state is null or state<>3))");
				}else{
					sql.append("where submitflag=1 ");	
					}
			}
			sql.append(" order by A0000");
			this.frowset=dao.search(sql.toString());
			
			HashMap map=new HashMap();
			if("1".equals(infor_type))
			{
				ArrayList objlist=new ArrayList();			
				while(this.frowset.next())
				{ 
					objlist.add(this.frowset.getString("basepre")+this.frowset.getString("a0100"));
				} 
				if(objlist.size()==0)
					throw new GeneralException(ResourceFactory.getProperty("templa.noselected.object"));
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView); 
				if(tablebo.getOperationtype()!=0&&tablebo.getLlexpr()!=null&&tablebo.getLlexpr().trim().length()>0){ 
					map=judgeIsLlexpr(objlist,tablebo.getLlexpr());
				}
				
				if(map.size()==0)
					 this.getFormHM().put("judgeisllexpr","1");
				this.frowset.beforeFirst();
			}
			else
				 this.getFormHM().put("judgeisllexpr","1");
			
			StringBuffer str=new StringBuffer("");
			Des des= new Des();//调用cs控件打印时采用这个对象对nbase和A0100进行加密
			while(this.frowset.next())
			{
				   /* A0100参数格式：
				         模板, 员工申请临时表:
				         <NBASE></NBASE><A0100></A0100>
				       模板归档:
				         <ArchiveID></ArchiveID><NBASE></NBASE><A0100></A0100>
				       审批临时表:
				    	  <INS_ID>实例号</INS_ID><NBASE></NBASE><A0100></A0100>      
			      */
				/**安全平台改造,将Nbase和A0100进行加密，当Nbase为空时也进行加密,方便cs端处理 xcs 2014-9-18 按照和耿立诤的约定登记表打印暂时不处理**/
				if("1".equals(infor_type)){
					if("1".equals(cardtype))
					{
						
						if(map.get(this.frowset.getString("basepre").toLowerCase()+this.frowset.getString("a0100"))!=null)
							continue;
						str.append("`<NBASE>"+des.EncryPwdStr(this.frowset.getString("basepre"))+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</A0100>");
					}
					else if("4".equals(cardtype))
					{
						if("1".equals(sp_batch))
						{
							str.append("`<INS_ID>"+this.frowset.getString("ins_id")+"</INS_ID><NBASE>"+des.EncryPwdStr(this.frowset.getString("basepre"))+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</A0100>");
						}
						else
							str.append("`<INS_ID>"+ins_id+"</INS_ID><NBASE>"+des.EncryPwdStr(this.frowset.getString("basepre"))+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</A0100>");
					}
					else if("5".equals(cardtype)) //登记表打印
					{
						str.append("`<NBASE>"+this.frowset.getString("basepre")+"</NBASE><ID>"+this.frowset.getString("a0100")+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>");
					}
				}else if("2".equals(infor_type)){
					if("1".equals(cardtype))
					{
						str.append("`<NBASE>"+des.EncryPwdStr("")+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("b0110"))+"</A0100>");
					}
					else if("4".equals(cardtype))
					{
						if("1".equals(sp_batch))
						{
							str.append("`<INS_ID>"+this.frowset.getString("ins_id")+"</INS_ID><NBASE>"+des.EncryPwdStr("")+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("b0110"))+"</A0100>");
						}
						else
							str.append("`<INS_ID>"+ins_id+"</INS_ID><NBASE>"+des.EncryPwdStr("")+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("b0110"))+"</A0100>");
					}
					else if("5".equals(cardtype)) //登记表打印
					{
						str.append("`<NBASE></NBASE><ID>"+this.frowset.getString("b0110")+"</ID><NAME>"+this.frowset.getString("codeitemdesc_1")+"</NAME>");
					}
				}else if ("3".equals(infor_type)){
					if("1".equals(cardtype))
					{
						str.append("`<NBASE>"+des.EncryPwdStr("")+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("e01a1"))+"</A0100>");
					}
					else if("4".equals(cardtype))
					{
						if("1".equals(sp_batch))
						{
							str.append("`<INS_ID>"+this.frowset.getString("ins_id")+"</INS_ID><NBASE>"+des.EncryPwdStr("")+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("e01a1"))+"</A0100>");
						}
						else
							str.append("`<INS_ID>"+ins_id+"</INS_ID><NBASE>"+des.EncryPwdStr("")+"</NBASE><A0100>"+des.EncryPwdStr(this.frowset.getString("e01a1"))+"</A0100>");
					}
					else if("5".equals(cardtype)) //登记表打印
					{
						str.append("`<NBASE></NBASE><ID>"+this.frowset.getString("e01a1")+"</ID><NAME>"+this.frowset.getString("codeitemdesc_1")+"</NAME>");
					}
				}
			
				
			}
			
			if(str.length()>0)
			{
				this.getFormHM().put("a0100_str", SafeCode.encode(str.substring(1)));
			}
			else
			{
			    this.getFormHM().put("a0100_str","");
			    throw new GeneralException(ResourceFactory.getProperty("templa.noselected.object"));
				
			}
			this.getFormHM().put("cardtype",cardtype);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	
	
	
	private HashMap judgeIsLlexpr(ArrayList objlist,String llexpr)
	{
		HashMap map=new HashMap();
		
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
	    	  
	    	  rset=dao.search(judgeSql.toString()); 
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
	    		  map.put(rset.getString("basepre")+rset.getString("a0100"),"1");
	          }
	    	  if(bl)
	    	  { 
	    		   
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
			 
		}
		
		return map;
	
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
	
	
	
	

}
