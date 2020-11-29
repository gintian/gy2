package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class JudgeLlexprTrans extends IBusiness {
	private void setReadFlag(String taskid)
	{
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid))
			return;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo taskvo=new RecordVo("t_wf_task");
			taskvo.setInt("task_id",Integer.parseInt(taskid));
			taskvo.setInt("bread",1);
			dao.updateValueObject(taskvo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		if(tabid==null||tabid.length()<=0)
			tabid="0";
		String task_id=(String)this.getFormHM().get("taskid");
		task_id= PubFunc.decrypt(task_id);//liuyz 导出登记表
		/**安全平台改造,判断taskid是否在后台存在**/
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
		if(templateMap!=null&&!templateMap.containsKey(task_id)){
			throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
		}
		*/
		String filetype=(String)this.getFormHM().get("filetype");//liuyz 导出登记表 单人模版还是多人模版
		String ins_id=(String)this.getFormHM().get("ins_id");
		ins_id=ins_id==null?"0":ins_id;
		String batch_task=(String)this.getFormHM().get("batch_task");
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		String infor_type=(String)this.getFormHM().get("infor_type");  //1对人员处理的业务模板  =2对单位处理  =3对职位处理 
		String object_id=(String)this.getFormHM().get("object_id");  //1对人员处理的业务模板  =2对单位处理  =3对职位处理 
		if(infor_type==null)
			infor_type="1";
		if("1".equals(infor_type))
		{
			String a0100=(String)this.getFormHM().get("a0100");
			String pre=(String)this.getFormHM().get("pre");
			//liuyz 导出单人模版和多人模版
			if(a0100==null|| "".equals(a0100)||a0100.trim().length()==0)
			{
				 object_id = PubFunc.decrypt(object_id);
				 int i = object_id.indexOf("`");
                 if (i>0){
                	 pre=object_id.substring(0,i);
                     a0100=object_id.substring(i+1);
                 }
			}
			this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("pre", pre);
		}
		else if("2".equals(infor_type))
		{
			String b0110=(String)this.getFormHM().get("b0110");
			this.getFormHM().put("b0110",b0110);
		}
		else if("3".equals(infor_type))
		{
			String e01a1=(String)this.getFormHM().get("e01a1");
			this.getFormHM().put("e01a1",e01a1);
		}
		this.getFormHM().put("ins_id", ins_id);
		this.getFormHM().put("batch_task", batch_task);
		this.getFormHM().put("sp_flag", sp_flag);
		this.getFormHM().put("sp_batch", sp_batch);
		this.getFormHM().put("tabid", tabid);
		this.getFormHM().put("filetype", filetype);
		/*判断操作类型是否是0的0代表不加业务判断规则非0要加业务判断规则*/
		StringBuffer judgeoperationtype=new StringBuffer();
		judgeoperationtype.append("select operationtype from operation a,template_table b where a.operationcode=b.operationcode and b.tabid='");
		judgeoperationtype.append(tabid);
		judgeoperationtype.append("'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
		  TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);	
			
		  this.frowset=dao.search(judgeoperationtype.toString());
		  if(this.frowset.next())
		  {
			  if("0".equalsIgnoreCase(this.getFrowset().getString("operationtype"))||"5".equalsIgnoreCase(this.getFrowset().getString("operationtype")))
			  {
				  this.getFormHM().put("judgeisllexpr","1"); 
			  }else
			  {
				  
					
					/**审阅标志*/
					//String sp_flag=(String)this.getFormHM().get("sp_flag");
					/**单个人的还是批量的*/
					String flag=(String)this.getFormHM().get("flag");
					
					String singa0100="";
					String singpre="";
					String b0110="";
					String e01a1="";
					if("1".equals(infor_type))
					{
						singa0100=(String)this.getFormHM().get("a0100");
						singpre=(String)this.getFormHM().get("pre"); 
					}
					else if("2".equals(infor_type))
						b0110=(String)this.getFormHM().get("b0110");
					else if("3".equals(infor_type))
						e01a1=(String)this.getFormHM().get("e01a1");
					
					if("1".equals(sp_flag))
						setReadFlag(task_id);
					ArrayList inslist=new ArrayList();
					inslist.add(ins_id);
					tablebo.setInslist(inslist);
					tablebo.setIns_id(Integer.parseInt(ins_id));		
					this.getFormHM().put("flag",flag);
					this.getFormHM().put("id",(String)this.getFormHM().get("id"));
					judgeIsLlexpr(tabid,tablebo.getLlexpr(),Integer.parseInt(ins_id),inslist,flag,singa0100,singpre,e01a1,b0110,infor_type);
			 
			  }
		   }
		  else
			  this.getFormHM().put("judgeisllexpr","1"); 
		    String isSendMessage="0";
			if(tablebo.isBemail()&&tablebo.isBsms())
				isSendMessage="3";
			else if(tablebo.isBemail())
				isSendMessage="1";
			else if(tablebo.isBsms())
				isSendMessage="2";
			this.getFormHM().put("isSendMessage", isSendMessage);
		  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
	}
	private void judgeIsLlexpr(String tabid,String llexpr,int ins_id,ArrayList inslist,String flag,String singa0100,String singpre,String e01a1,String b0110,String infor_type)
	{

		
		HashMap hm=new HashMap();
		ArrayList a0100lists=new ArrayList();
		String first_base=null;
		String a0100=null;
		
		/**员工自助申请标识
		 *＝1员工
		 *＝0业务员 
		 */
		String selfapply=(String)this.getFormHM().get("selfapply");
		if(selfapply==null||selfapply.length()==0)
			selfapply="0";
		
		
		if("1".equals(flag))
		{
			try{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				StringBuffer sql=new StringBuffer();
				sql.append("select * from ");
				if("1".equals(selfapply))
				{
					sql.append("g_templet_");
					sql.append(tabid+" where ");
					sql.append(" basepre='");
					sql.append(this.userView.getDbname());
					sql.append("' and a0100='");
					sql.append(this.userView.getA0100());
					sql.append("'");
				}
				else
				{
					
					if(ins_id==1) //审批表中的数据
					{
						sql.append("templet_");
						sql.append(tabid+" where 1=1 ");
						StringBuffer strins=new StringBuffer();
						for(int i=0;i<inslist.size();i++)
						{
							if(i!=0)
							  strins.append(",");
							strins.append((String)inslist.get(i));
						}
						sql.append(" and ins_id in(");
						sql.append(strins.toString());
						sql.append(")");
						//sql.append(" where ins_id=");
						//sql.append(this.ins_id);
					}else{
						sql.append(this.userView.getUserName());
						sql.append("templet_");
						sql.append(tabid+" where 1=1 ");
					}
					
					sql.append(" and submitflag=1 ");
					
					if("1".equals(infor_type))
						sql.append(" order by a0100");
					else if("2".equals(infor_type))
						sql.append(" order by b0110");
					else if("3".equals(infor_type))
						sql.append(" order by e01a1");
				}
				
				RowSet rset=null;
				rset=dao.search(sql.toString());
				while(rset.next())
				{
					if("1".equals(infor_type))
					{
						String pre=rset.getString("basepre").toLowerCase();
						/**按人员库进行分类*/
						if(!hm.containsKey(pre))
						{
							a0100lists=new ArrayList();
						}
						else
						{
							a0100lists=(ArrayList)hm.get(pre);
						}
						a0100lists.add(rset.getString("a0100"));				
						hm.put(pre,a0100lists);
					}
					else if("2".equals(infor_type))
					{
						a0100lists.add(rset.getString("b0110"));				
					}
					else if("3".equals(infor_type))
					{
						a0100lists.add(rset.getString("e01a1"));				
					}
					
				}//for i loop end.
				
				if("2".equals(infor_type)|| "3".equals(infor_type))
					hm.put("B",a0100lists);
				
		    }catch(Exception e)
		    {
			    e.printStackTrace();
		    }	
		}
		else
		{
			a0100lists=new ArrayList();
			if("1".equals(infor_type))
			{
				a0100lists.add(singa0100); 
				hm.put(singpre,a0100lists);
			}
			else if("2".equals(infor_type))
			{
				a0100lists.add(b0110); 
				hm.put("B",a0100lists);
			}
			else if("3".equals(infor_type))
			{
				a0100lists.add(e01a1); 
				hm.put("B",a0100lists);
			}
		}
		
		
		
	    /**加规则过滤*/
		ArrayList alUsedFields=null;
		String temptable=null;
		if(llexpr!=null && llexpr.trim().length()>0)
	    { 
		  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		  temptable=createSearchTempTable(this.getFrameconn(),infor_type);
	    }
		
	    Iterator iterator=hm.entrySet().iterator();
	    StringBuffer judgeSql=new StringBuffer();

		while(iterator.hasNext())
		{

			Entry entry=(Entry)iterator.next();
			String pre=entry.getKey().toString();
			ArrayList a0100list =(ArrayList)entry.getValue();
			
			String key="a0100";
			if("1".equals(infor_type))
			{
			    judgeSql.append("select A0101 from ");
				judgeSql.append(pre);
				judgeSql.append("A01 where ");
			}
			else if("2".equals(infor_type))
			{
				judgeSql.append("select B0110 from B01 where ");
				key="b0110";
			}
			else if("3".equals(infor_type))
			{
				judgeSql.append("select E01A1 from K01 where ");
				key="e01a1";
			}
			
			
			if("1".equals(flag)&&!"1".equals(selfapply))
			{
				StringBuffer _sql=new StringBuffer("");
			
				if(ins_id==1) //审批表中的数据
				{
					_sql.append("select "+key+" from ");
					_sql.append("templet_");
					_sql.append(tabid+" where 1=1 and submitflag=1  ");
					StringBuffer strins=new StringBuffer();
					for(int i=0;i<inslist.size();i++)
					{
						if(i!=0)
						  strins.append(",");
						strins.append((String)inslist.get(i));
					}
					_sql.append(" and ins_id in(");
					_sql.append(strins.toString());
					_sql.append(")");
				}else{
					_sql.append("select "+key+" from ");
					_sql.append(this.userView.getUserName());
					_sql.append("templet_");
					_sql.append(tabid+" where 1=1 and submitflag=1  ");
				}
				if("1".equals(infor_type))
					_sql.append(" and lower(basepre)='"+pre.toLowerCase()+"'");
				
				judgeSql.append(" "+key+" in ( "+_sql.toString()+" ) ");
				
			}
			else
			{
				judgeSql.append(" "+key+" in(''");
				for(int i=0;i<a0100list.size();i++)
				{
					judgeSql.append(",'");
					judgeSql.append(a0100list.get(i));
					judgeSql.append("'");
				}
				judgeSql.append(")");
			}
			
			judgeSql.append(" and ");
			judgeSql.append(getFilterSQL(temptable,pre,alUsedFields,llexpr,infor_type));
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
	    		  if("1".equals(infor_type))
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
	    		  else if("2".equals(infor_type))
	    		  {
	    			  String codeitemid=rset.getString("b0110");
	    			  String codeitemdesc=AdminCode.getCodeName("UN",codeitemid);
	    			  if(codeitemdesc==null||codeitemdesc.trim().length()==0)
	    				  codeitemdesc=AdminCode.getCodeName("UM",codeitemid);
	    			  if(bl==false)
		    		  {
		    			  judgedesc=codeitemdesc;
		    			  bl=true;
		    		  }else
		    		  {
		    			  judgedesc+="," +codeitemdesc;  
		    		  }	   
	    		  }
	    		  else if("3".equals(infor_type))
	    		  {
	    			  String codeitemid=rset.getString("E01A1");
	    			  String codeitemdesc=AdminCode.getCodeName("@K",codeitemid);
	    			  if(bl==false)
		    		  {
		    			  judgedesc=codeitemdesc;
		    			  bl=true;
		    		  }else
		    		  {
		    			  judgedesc+="," +codeitemdesc;  
		    		  }	    
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
	private String getFilterSQL(String temptable,String BasePre,ArrayList alUsedFields,String llexpr,String infor_type)
	{
		String sql=" (1=2)";
		try{
		if(llexpr!=null && llexpr.length()>0)
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer inserSql=new StringBuffer();
			if("1".equals(infor_type))
			{
				inserSql.append("insert into ");
				inserSql.append(temptable);
				inserSql.append("(a0100) select '");
				inserSql.append(BasePre);
				inserSql.append("'"+Sql_switcher.concat()+"a0100 from ");
				
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
				sql="('" + BasePre + "'"+Sql_switcher.concat()+  BasePre +"A01.a0100 in  (select distinct a0100 from " + temptable + "))";
				inserSql.append(tempTableName);
				inserSql.append(" where " + yp.getSQL());
			}
			else if("2".equals(infor_type))
			{
				inserSql.append("insert into ");
				inserSql.append(temptable);
				inserSql.append("(b0110) select b0110 from ");
				
				//this.filterfactor="性别 <> '1'";
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic								
				String whereIN=InfoUtils.getWhereInOrgSql(this.userView,"2");
				whereIN="select b0110 "+whereIN;							
				YksjParser yp = new YksjParser( this.userView ,alUsedFields,
						YksjParser.forSearch, varType, YksjParser.forUnit, "Ht", BasePre);
				YearMonthCount ymc=null;							
				yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.getFrameconn(),"A", null);
				String tempTableName = yp.getTempTableName();
				sql="( B01.b0110 in  (select distinct b0110 from " + temptable + "))";
				inserSql.append(tempTableName);
				inserSql.append(" where " + yp.getSQL());
				
			}
			else if("3".equals(infor_type))
			{
				inserSql.append("insert into ");
				inserSql.append(temptable);
				inserSql.append("(e01a1) select e01a1 from ");
				
				//this.filterfactor="性别 <> '1'";
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic								
				String whereIN=InfoUtils.getWhereInOrgSql(this.userView,"3");
				whereIN="select e01a1 "+whereIN;							
				YksjParser yp = new YksjParser( this.userView ,alUsedFields,
						YksjParser.forSearch, varType, YksjParser.forPosition, "Ht", BasePre);
				YearMonthCount ymc=null;							
				yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.getFrameconn(),"A", null);
				String tempTableName = yp.getTempTableName();
				sql="( K01.e01a1 in  (select distinct e01a1 from " + temptable + "))";
				inserSql.append(tempTableName);
				inserSql.append(" where " + yp.getSQL());
			} 
			dao.insert(inserSql.toString(),new ArrayList());	
		}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	private String createSearchTempTable(Connection conn,String infor_type)
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
			if("1".equals(infor_type))
				sql.append("(a0100  varchar (100) )");
			else if("2".equals(infor_type))
				sql.append("(b0110  varchar (100) )");
			else if("3".equals(infor_type))
				sql.append("(e01a1  varchar (100) )");
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
