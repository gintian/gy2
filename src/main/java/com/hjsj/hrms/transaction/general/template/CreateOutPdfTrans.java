/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
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

/**
 * <p>Title:CreateOutPdfTrans</p>
 * <p>Description:创建模板PDF文件</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 11, 20069:47:07 AM
 * @author chenmengqing
 * @version 4.0
 */
public class CreateOutPdfTrans extends IBusiness {

	private ArrayList getInsList(String batch_task)throws GeneralException
	{
		ArrayList inslist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select ins_id from t_wf_task where task_id in (");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
				inslist.add(rset.getString("ins_id"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return inslist;
	}
	
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		String[] lists=StringUtils.split(batch_task,",");
		ArrayList list=new ArrayList();
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++){
			String temptaskid=lists[i];
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(templateMap!=null&&!templateMap.containsKey(temptaskid)){
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			list.add(lists[i]);
		}
		return list;
		
	}
	
	//兼容以前版本产生的数据
	boolean isSeqnum(String ins_id,String tabid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
	//		RowSet rowSet=dao.search("select seqnum from templet_"+tabid+" where ins_id="+ins_id);
			RowSet rowSet=dao.search("select seqnum from t_wf_task_objlink where ins_id="+ins_id);
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
	
	
	public void execute() throws GeneralException {
		String setname=(String)this.getFormHM().get("setname");
		String ins_id=(String)this.getFormHM().get("ins_id");
		String task_id=(String)this.getFormHM().get("task_id");
		String filterStr =  this.userView.getHm().get("filterStr")==null?"":(String)this.userView.getHm().get("filterStr");
		HashMap templateMap = (HashMap) this.userView.getHm().get("tenplateMap");
		/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
		if(templateMap!=null&&!templateMap.containsKey(task_id)){
			throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
		}
		*/
		String tab_id=(String)this.getFormHM().get("tabid");
		String flag=(String)this.getFormHM().get("flag");
		String infor_type=(String)this.getFormHM().get("infor_type");
		String task_sp_flag=(String)this.getFormHM().get("task_sp_flag");
		String businessModel_yp=(String)this.getFormHM().get("businessModel_yp");
		if(flag==null|| "".equals(flag))
			flag="0";
		/**批量审批*/
		ArrayList inslist=null;	
		ArrayList tasklist=null;
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		if(sp_batch==null|| "".equals(sp_batch))
			sp_batch="0";//单个任务审批
		if("1".equals(sp_batch))
		{
		//	if(flag.equals("1"))
			{
				String batch_task=(String)this.getFormHM().get("batch_task");
				tasklist=getTaskList(batch_task);//getInsList(batch_task);
			}
		/*	else
			{
				tasklist=new ArrayList();				
				tasklist.add(task_id);
			} */
		}
		else
		{
			tasklist=new ArrayList();
			tasklist.add(task_id);
		}		
		String yj=(String)this.getFormHM().get("yj");
		String pageno=(String)this.getFormHM().get("pageno");
		/**如果打印审批意见表时，但页号为空时，退出*/
		if("1".equals(yj)&&(pageno==null|| "".equalsIgnoreCase(pageno)))
				return;
		try
		{
			ArrayList objlist=new ArrayList();			
			/**打印全部人员*/
			if("1".equals(flag))
			{
				StringBuffer buf=new StringBuffer();
				if(!"0".equals(ins_id))
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100,ins_id from ");
					}else if("2".equals(infor_type)){
						buf.append("select b0110,ins_id from ");
					}else if("3".equals(infor_type)){
						buf.append("select e01a1,ins_id from ");
					}else{
						buf.append("select basepre,a0100,ins_id from ");
					}
					buf.append(setname);	
					buf.append(" where 1=1 ");
				
					boolean isSeqnum=isSeqnum(ins_id,tab_id);
					
					if(isSeqnum)
					{
						buf.append(" and exists (select null from t_wf_task_objlink where "+setname+".seqnum=t_wf_task_objlink.seqnum  and "+setname+".ins_id=t_wf_task_objlink.ins_id  ");
						if(!"2".equals(task_sp_flag)&&!"3".equals(businessModel_yp)){
							buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
						}
					}
					
					if("1".equals(sp_batch))
					{
						//buf.append(" where submitflag=1 and  ins_id in (");						
					//	buf.append(" where submitflag=1 and  task_id in (");	
						buf.append(" and   task_id in (");
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
						//buf.append(" where submitflag=1 and ins_id=");
						if(task_id.trim().length()>0)
						{
							//buf.append(" where submitflag=1 and task_id=");
							buf.append(" and  task_id=");
							buf.append(task_id);
						}
						else //绩效面谈用到
						{
						//	buf.append(" where submitflag=1 and ins_id=");
							buf.append(" and  ins_id=");
							buf.append(ins_id);
						}
					}
					if(isSeqnum)
						buf.append(" and (state is null or state<>3) ) ");
				}
				else
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100  from ");	
						}else if("2".equals(infor_type)){
						buf.append("select b0110 from ");
						}else if("3".equals(infor_type)){
						buf.append("select e01a1 from ");
						}else{
							buf.append("select basepre,a0100  from ");	
						}
					
					buf.append(setname);
					buf.append(" where 1=1 ");
				}
				
				if(filterStr.trim().length()>0){
				    buf.append(" and "+filterStr);
				}
				buf.append("  order by a0000");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				RowSet rset=dao.search(buf.toString());
				/**求每个对应的实例*/
				inslist=new ArrayList();
				while(rset.next())
				{
					if(!"0".equals(ins_id))
						inslist.add(rset.getString("ins_id"));
					else
						inslist.add("0");
					if("1".equals(infor_type)){
						objlist.add(rset.getString("basepre")+rset.getString("a0100"));
						}else if("2".equals(infor_type)){
							objlist.add(rset.getString("b0110"));
						}else if("3".equals(infor_type)){
							objlist.add(rset.getString("e01a1"));
						}else{
							objlist.add(rset.getString("basepre")+rset.getString("a0100"));	
						}
					
				}
			}
			else if ("2".equals(flag))//选中人员
			{
				StringBuffer buf=new StringBuffer();
				if(!"0".equals(ins_id))
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100,ins_id from ");
						}else if("2".equals(infor_type)){
						buf.append("select b0110,ins_id from ");
						}else if("3".equals(infor_type)){
						buf.append("select e01a1,ins_id from ");
						}else{
							buf.append("select basepre,a0100,ins_id from ");
						}
					buf.append(setname);
					
					boolean isSeqnum=isSeqnum(ins_id,tab_id);
					
					buf.append(" where 1=1  ");
					if(isSeqnum)
					{
						buf.append(" and exists (select null from t_wf_task_objlink where "+setname+".seqnum=t_wf_task_objlink.seqnum and "+setname+".ins_id=t_wf_task_objlink.ins_id  ");
						if(!"2".equals(task_sp_flag)&&!"3".equals(businessModel_yp)){
							buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
						}
					}
					if("1".equals(sp_batch))
					{
						//buf.append(" where submitflag=1 and  ins_id in (");						
						buf.append(" and submitflag=1 and  task_id in (");						
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
						//buf.append(" where submitflag=1 and ins_id=");
						if(task_id.trim().length()>0)
						{
							buf.append(" and submitflag=1 and task_id=");
							buf.append(task_id);
						}
						else  //绩效面谈用到
						{
							buf.append(" and submitflag=1 and ins_id=");
							buf.append(ins_id);
						}
					}
					if(isSeqnum)
						buf.append(" and (state is null or state<>3) ) ");
				}

				else
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100  from ");	
						}else if("2".equals(infor_type)){
						buf.append("select b0110 from ");
						}else if("3".equals(infor_type)){
						buf.append("select e01a1 from ");
						}else{
							buf.append("select basepre,a0100  from ");	
						}
					buf.append(setname);	
					buf.append(" where submitflag=1");					
				}
				buf.append("  order by a0000");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				RowSet rset=dao.search(buf.toString());
				/**求每个对应的实例*/
				inslist=new ArrayList();
				while(rset.next())
				{
					if(!"0".equals(ins_id))
						inslist.add(rset.getString("ins_id"));
					else
						inslist.add("0");
					if("1".equals(infor_type)){
						objlist.add(rset.getString("basepre")+rset.getString("a0100"));
						}else if("2".equals(infor_type)){
							objlist.add(rset.getString("b0110"));
						}else if("3".equals(infor_type)){
							objlist.add(rset.getString("e01a1"));
						}else{
							objlist.add(rset.getString("basepre")+rset.getString("a0100"));	
						}
				}				
			}			
			else//当前人员
			{
				inslist=new ArrayList();
				inslist.add(ins_id);
				String basepre="";
				String a0100="";
				if("1".equals(infor_type)){
					basepre=(String)this.getFormHM().get("basepre");
					a0100=(String)this.getFormHM().get("a0100");
					objlist.add(basepre+a0100);
					}else if("2".equals(infor_type)){
						a0100=(String)this.getFormHM().get("a0100");
						objlist.add(a0100);
					}else if("3".equals(infor_type)){
						a0100=(String)this.getFormHM().get("a0100");
						objlist.add(a0100);
					}else{
						a0100=(String)this.getFormHM().get("a0100");
						basepre=(String)this.getFormHM().get("basepre");
						objlist.add(basepre+a0100);
					}
					if(objlist.size()==1&&(objlist.get(0)==null||((String)objlist.get(0)).trim().length()==0))
						objlist=new ArrayList();
			}
			
			if(objlist.size()==0){
				if("1".equals(infor_type)){
					throw new GeneralException("请选择需要生成PDF的人员!");
					}else if("2".equals(infor_type)){
						throw new GeneralException("请选择需要生成PDF的机构!");
					}else if("3".equals(infor_type)){
						throw new GeneralException("请选择需要生成PDF的岗位!");
					}
			}
				
			OutWordBo owbo = new OutWordBo(this.getFrameconn(),this.userView,Integer.parseInt(tab_id),tasklist.get(0).toString());            
            String filename=null;
            owbo.setSigntype(0);//60锁只有金格签章
            owbo.setDowntype("1");
            owbo.setOuttype("0");
            owbo.setShow_pageno("-1".equals(pageno)?"":pageno);
            
            filename=owbo.outword(objlist,1,inslist);
			/**为了解决任意文件下载漏洞,需要将文件名进行加密begin**/
			filename=PubFunc.encrypt(filename);
			/**为了解决任意文件下载漏洞,需要将文件名进行加密end**/
			this.getFormHM().clear();//清空参数，减少网络数据传输量
			this.getFormHM().put("filename",filename);
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
			
			if(tablebo.getOperationtype()!=0){
				if("1".equals(infor_type))
				judgeIsLlexpr(objlist,tablebo.getLlexpr());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
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
			
			
		    judgeSql.append("select A0101 from ");
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
	    	  //System.out.println(judgeSql.toString() + llexpr);
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
	          }
	    	  if(bl)
	    	  {
	    		  this.getFormHM().put("sp_flag","3");
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
}
