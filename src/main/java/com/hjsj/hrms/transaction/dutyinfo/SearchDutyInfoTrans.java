package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class SearchDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String fieldstr=(String)this.getFormHM().get("fieldstr");
		ArrayList fieldList=(ArrayList)this.getFormHM().get("fieldList");
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		String orglike=(String)this.getFormHM().get("orglike");
		String querylike=(String)this.getFormHM().get("querylike");
		String query=(String)this.getFormHM().get("query");
		ArrayList factorlist=(ArrayList)this.getFormHM().get("selectfieldlist");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String uplevel=(String)this.getFormHM().get("uplevel");
		if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String backdate = (String)map.get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		//String backdate =sdf.format(new Date());
		InfoUtils infoUtils=new InfoUtils();
		StringBuffer columns=new StringBuffer();
		columns.append(fieldstr);	
	    StringBuffer sql=new StringBuffer();
	    columns.append(",end_date"); //需要用的结束时间列
	    sql.append("select "+columns);
	    sql.append(" from ");	
	    String like_where="";	   
	    StringBuffer wheresql=new StringBuffer();
	    wheresql.append(" where 1=1");
	    if(query!=null&& "1".equals(query))
	    {
	    	String whereQuery=getWhereSql(factorlist,querylike);
	    	if(whereQuery!=null&&whereQuery.length()>0)
	    	{
	    		wheresql.append(" and  "+whereQuery);
//	    		orglike="1";
//	    		this.getFormHM().put("orglike", "1");	
	    	}
            this.getFormHM().put("isShowCondition", "");	
	    }else
	    {
	    	if(query!=null&& "0".equals(query))
	    	{
	    		this.getFormHM().put("scanfieldlist",infoUtils.clearFieldValueList(factorlist));
	    	}
	    	this.getFormHM().put("query", "");	    	
	    	this.getFormHM().put("isShowCondition", "none");	
	    }
	    String leftSQLOrg="";
	    
	     leftSQLOrg=getLeftJoinSql("",fieldList,wheresql.toString(),code,backdate,orglike);
	    //System.out.println(leftSQLOrg);
	    sql.append(" ("+leftSQLOrg);
	    sql.append(" union all ");
	    //String leftSQLvOrg=getLeftJoinSql("v" ,fieldstr, wheresql.toString(),code,backdate,orglike);
	    String leftSQLvOrg=getLeftJoinSql("v",fieldList,wheresql.toString(),code,backdate,orglike);
	    sql.append(leftSQLvOrg);
	    sql.append(" and parentid in(select codeitemid from organization)");
	    sql.append(") a "); // ,organization 是后来加的 用于岗位历史节点的问题 前面的end_date列也是此处加上的
	    
	    
	    String codemess=AdminCode.getCodeName("UN", code);
	    if(codemess==null||codemess.length()<=0)
		{
			   CodeItem codeitem=AdminCode.getCode("UM", code,Integer.parseInt(uplevel));
			   if(codeitem!=null)
			      codemess=codeitem.getCodename();
		 }  
	    //System.out.println(sql.toString());
	    if(query!=null&& "1".equals(query))
		{
			infoUtils.saveQueryResult("3","",sql.toString(),this.getFrameconn(),this.userView);
		}
		this.getFormHM().put("codemess", codemess);
		this.getFormHM().put("sqlstr", sql.toString());
		this.getFormHM().put("wherestr", "");
		this.getFormHM().put("columnstr", columns.toString());
		this.getFormHM().put("orderby", /*"order by e01a1"*/"order by a0000");
	}	
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like) throws GeneralException
	{
		InfoUtils infoUtils=new InfoUtils();
		String whereTrem=infoUtils.combine_SQL(this.userView,factorlist,like,"","3");
		if(whereTrem!=null&&whereTrem.length()>0)
		{
			
			whereTrem="(AAA.e01a1 in(select K01.e01a1 "+whereTrem+"))";		
		}else
			return "";
		/*if(whereTrem!=null&&whereTrem.length()>0)
		{
			if(whereTrem.indexOf("WHERE")!=-1)
				   whereTrem=whereTrem.substring(whereTrem.indexOf("WHERE")+5);
				else
					return "";
		}		*/
		return whereTrem;
	}
	/**
	 * 
	 * @param candid_setid  子集
	 * @param candid_codesetid 指标项
	 * @param candid_value  指标值
	 * @param display_list  显示项
	 * @param code  
	 * @param kind
	 * @return
	 */
	
	public String getLeftJoinSql(String t,ArrayList fieldList,String where,String code,String backdate,String orglike)
	{
			
		StringBuffer select_str=new StringBuffer();
		StringBuffer select_columns=new StringBuffer();
		int n=0;
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldList.get(i);
			String itemid=fieldItem.getItemid();	
			if("e01a1".equalsIgnoreCase(itemid))
			  select_columns.append("codeitemid as "+itemid+",");
			else if("e0122".equalsIgnoreCase(itemid))
			  select_columns.append("parentid as e0122,");
			else
			  select_columns.append(itemid+i+"."+itemid+" as "+itemid+",");
			n++;
		}
		
		select_columns.setLength(select_columns.length()-1);
		FieldItem fielditem00=null;
		String table = t+"organization";		
		select_str.append("select codeitemid,parentid,'"+t+"org' as orgtype,a0000");
		if(select_columns!=null&&select_columns.length()>0)
		{
			select_str.append(","+select_columns.toString());
		}
		select_str.append(",end_date from "+table); //新加了个end_date 列 此处需要用到
		select_str.append(" left join (select "+Sql_switcher.isnull("e01a1","''")+" as e01a1");
		select_str.append(" from k01 B");
		select_str.append(") AAA");
		select_str.append(" on ");
		select_str.append(table+".codeitemid=AAA.e01a1 ");
		String itemid="";
		String oldItemid="";
		for(int i=0;i<fieldList.size();i++)
		{
		   	FieldItem fielditem=(FieldItem)fieldList.get(i);
		   	if(fielditem==null)
		   		continue;
			String setid=fielditem.getFieldsetid();		
			itemid=fielditem.getItemid();
			if("e01a1".equalsIgnoreCase(itemid))
				continue;	
			if("e0122".equalsIgnoreCase(itemid))
				continue;	
			if("K01".equalsIgnoreCase(setid))
			{
				select_str.append(" left join (select "+itemid+",A.e01a1 from k01 A");
				select_str.append(") "+itemid+i);
				select_str.append(" on ");
			}else
			{
				select_str.append(" left join (select "+itemid+",A.e01a1 from "+setid+" A,");
				select_str.append("(select e01a1,max(i9999) as i9999 from "+setid+" group by e01a1)B");
				select_str.append(" where A.e01a1=B.e01a1 and A.i9999=B.i9999) "+itemid+i);
				select_str.append(" on ");
			}
			select_str.append(table+".codeitemid="+itemid+i+".e01a1 ");
			oldItemid=itemid+i;
		}		
		select_str.append(where);		 
		String like_where="";
		 if(code==null||code.length()<=0)
		    {
		    	if(!this.userView.isSuper_admin()){
					  /*if(this.userView.getManagePrivCode().equalsIgnoreCase("UN")&&(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0))
					  {
						   
						  select_str.append(" and  codeitemid=parentid ");
						  select_str.append(" and codesetid='@K'");
						  select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					  }else
					  {
						  if(orglike!=null&&orglike.equals("1")&&this.userView.getManagePrivCode().length()>0)
					 	  {
						    	like_where=" and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'";
					 	  }else
					 	  {
					 	    	like_where=" and codeitemid = '"+this.userView.getManagePrivCodeValue()+"'";
					 	  }
						  select_str.append(" and codesetid='@K'   "+like_where);
						  select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					 	  
					  }*/
		    		
		    		String busi=this.getBusi_org_dept(userView);
					if(busi.length()>2){
						if(busi.indexOf("`")!=-1){
							select_str.append(" and (");
							String[] tmps=busi.split("`");
							for(int i=0;i<tmps.length;i++){
								String a_code=tmps[i];
								if(a_code.length()>2){
									if(orglike!=null&& "1".equals(orglike))
								 	  {
										select_str.append(" codeitemid like '"+a_code.substring(2)+"%' or");
								 	  }else
								 	  {
								 		 select_str.append(" codeitemid = '"+a_code.substring(2)+"' or");
								 	  }
								}else if(a_code.length()==2){
									if(orglike!=null&& "1".equals(orglike))
								 	{
					            		select_str.append(" 1=1 or ");
								 	}else
								 		select_str.append(" parentid=codeitemid or ");
								}
							}
							select_str.append(" 1=2) ");
						}else{
							//select_str.append(" where codeitemid='"+busi.substring(2)+"' ");
							if(orglike!=null&& "1".equals(orglike))
						 	  {
								select_str.append(" and codeitemid like '"+busi.substring(2)+"%'");
						 	  }else
						 	  {
									select_str.append(" and codeitemid = '"+busi.substring(2)+"'");
						 	  }
						}
					}else{
						select_str.append(" and 1=2 ");
					}
					select_str.append(" and codesetid='@K'");
					  select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				 	  
	            }else
	            {
	            	if(orglike!=null&& "1".equals(orglike))
				 	{
	            		select_str.append(" and codesetid='@K'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				 	}else
				 		select_str.append(" and codesetid='@K' and parentid=codeitemid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	            }
		    }else
		    {
		    	
		    	if(orglike!=null&& "1".equals(orglike)&&this.userView.getManagePrivCode().length()>0)
		 	    {
			    	like_where=" and (parentid like '"+code+"%'or codeitemid='"+code+"')";
		 	    }else
		 	    {
		 	    	like_where=" and (parentid = '"+code+"' or codeitemid='"+code+"')";
		 	    }	    	
		    	
		    	select_str.append(" and codesetid='@K'");
		 	    select_str.append("    "+like_where);
		 	    select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		 	}
		return select_str.toString();
	}
	
	private String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
		return busi;
	}
}


