package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class SearchOrgInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String fieldstr=(String)this.getFormHM().get("fieldstr");
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		String orglike=(String)this.getFormHM().get("orglike");
		String querylike=(String)this.getFormHM().get("querylike");
		ArrayList fieldList=(ArrayList)this.getFormHM().get("fieldList");
		ArrayList list = new ArrayList();
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldList.get(i);
			String itemid=fieldItem.getItemid();	
			String flag = this.userView.analyseFieldPriv(itemid);
            if ("0".equals(flag))
              continue;
            list.add(fieldItem);
		}
		fieldList = list;
		
		String query=(String)this.getFormHM().get("query");
		ArrayList factorlist=(ArrayList)this.getFormHM().get("selectfieldlist");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		InfoUtils infoUtils=new InfoUtils();	
		 Map map = (Map)this.getFormHM().get("requestPamaHM");
		 String backdate = (String) map.get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String busiPriv = (String)map.get("busiPriv");
		busiPriv = busiPriv==null?"":busiPriv;
		String uplevel=(String)this.getFormHM().get("uplevel");
		if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
		//String backdate =sdf.format(new Date());
		StringBuffer columns=new StringBuffer();
		columns.append("code");
	    if(fieldstr!=null&&fieldstr.length()>0){	
	    	String[] fieldArr = fieldstr.split(",");
	    	for(String s : fieldArr) {
	    		if(StringUtils.isEmpty(s)) {
	    			continue ;
	    		}
	    		String flag = this.userView.analyseFieldPriv(s);
	            if ("0".equals(flag))
	              continue;
	    		columns.append(","+s);
	    	}
		}
	    StringBuffer sql=new StringBuffer();
	    sql.append("select end_date,codeitemid as "+columns);
	    sql.append(" from ");	   	  
	    StringBuffer wheresql=new StringBuffer();
	    
	   
	    wheresql.append(" where 1=1");
	   // wheresql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	    if(query!=null&& "1".equals(query))
	    {
	    	String whereQuery=getWhereSql(factorlist,querylike, code); 
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
	    //leftSQLOrg=getLeftJoinSql("" ,fieldstr, wheresql.toString(),code,backdate,orglike);
	    //System.out.println(leftSQLOrg);
	     leftSQLOrg=getLeftJoinSql("",fieldList,wheresql.toString(),code,backdate,orglike,busiPriv);
	    //System.out.println(leftSQLOrg);
	    sql.append(" ("+leftSQLOrg);
	    sql.append(" union ");
	    //String leftSQLvOrg=getLeftJoinSql("v" ,fieldstr, wheresql.toString(),code,backdate,orglike);
	    String leftSQLvOrg=getLeftJoinSql("v",fieldList,wheresql.toString(),code,backdate,orglike,busiPriv);
	    sql.append(leftSQLvOrg);
	    //sql.append(" and parentid in(select codeitemid from organization)");
	    sql.append(") a");		  
	   String codemess=AdminCode.getCodeName("UN", code);
	   if(codemess==null||codemess.length()<=0)
	   {
		   CodeItem codeitem=AdminCode.getCode("UM", code,Integer.parseInt(uplevel));
		   if(codeitem!=null)
		      codemess=codeitem.getCodename();
	   }  
	   if(query!=null&& "1".equals(query))
		{
			infoUtils.saveQueryResult("2","",sql.toString(),this.getFrameconn(),this.userView);
		}
	   //System.out.println(sql.toString());
	   this.getFormHM().put("codemess", codemess);
	   this.getFormHM().put("sqlstr", sql.toString());
	   this.getFormHM().put("wherestr", "");
	   this.getFormHM().put("columnstr",columns.append(",end_date").toString());
	   this.getFormHM().put("orderby", "order by a0000,codeitemid,orgtype");
	   this.getFormHM().put("fieldList", fieldList);
	   //只为清除数据
	   //this.getFormHM().put("codelist", new ArrayList());
	   //this.getFormHM().put("codesetid", "");
	   //this.getFormHM().put("itemid","");
	   //this.getFormHM().put("codeitemdesc", "");
	   //this.getFormHM().put("issuperuser", "");
		//this.getFormHM().put("manageprive", "");
		//this.getFormHM().put("isrefresh", "");
	}
    
	public boolean IsLastCode(String table,String code,String backdate)
	{
		boolean isCorrect =true;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		//将 where parentid ='"+code+"' and ...   改为 like ;  guodd 2015-02-04
		//当code的第一级子节点没有虚拟机构，但是二三级子节点可能有虚拟机构，会造成虚拟机构加载不出来
		String sql="select 1 from "+table+" where parentid like '"+code+"%' and  codesetid<>'@K'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date"; 
		try {
			this.frowset=dao.search(sql);
			if(!this.frowset.next())
				isCorrect=false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isCorrect;
	}
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like, String parentId) throws GeneralException
	{
		InfoUtils infoUtils=new InfoUtils();
		infoUtils.setParentId(parentId);
		String whereTrem=infoUtils.combine_SQL(this.userView,factorlist,like,"","2");
		
		if(whereTrem!=null&&whereTrem.length()>0)
		{
			
			whereTrem="(AAA.b0110 in(select B01.b0110 "+whereTrem+"))";		
		}else
			return "";
		/*if(whereTrem!=null&&whereTrem.length()>0)
		{
			if(whereTrem.indexOf("WHERE")!=-1)
			   whereTrem=whereTrem.substring(whereTrem.indexOf("WHERE")+5);
			else
				return "";
		}	*/	
		return whereTrem;
	}
	private String getLeftJoinSql(String t,String fieldstr,String where,String code,String backdate,String orglike)
	{
		 String table = t+"organization";
		 StringBuffer leftsql=new StringBuffer();
		 leftsql.append("select codeitemid,'"+t+"org' as orgtype,a0000");
		 if(fieldstr!=null&&fieldstr.length()>0)
		    leftsql.append(fieldstr);
		 leftsql.append(" FROM "+table+" left join b01 ON "+table+".codeitemid=b01.b0110");
		 leftsql.append(where);		 
		 String like_where="";
		 if(code==null||code.length()<=0)
		    {
		    	if(!this.userView.isSuper_admin()){
					  if("UN".equalsIgnoreCase(this.userView.getManagePrivCode())&&(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0))
					  {
						   
						  leftsql.append(" and  codeitemid=parentid ");
						  leftsql.append(" and codesetid<>'@K'");
						  leftsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					  }else
					  {
						  if(orglike!=null&& "1".equals(orglike))
					 	  {
						    	like_where=" and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'";
					 	  }else
					 	  {
					 	    	like_where=" and codeitemid = '"+this.userView.getManagePrivCodeValue()+"'";
					 	  }
						  leftsql.append(" and codesetid<>'@K'   "+like_where);
						  leftsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					 	  
					  }
	            }else
	            {
	            	if(orglike!=null&& "1".equals(orglike))
				 	{
	            		leftsql.append(" and codesetid<>'@K'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				 	}else
	            	  leftsql.append(" and codesetid<>'@K' and parentid=codeitemid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	            }
		    }else
		    {
		    	
		    	if(orglike!=null&& "1".equals(orglike))
		 	    {
			    	like_where=" and (parentid like '"+code+"%'or codeitemid='"+code+"')";
		 	    }else
		 	    {
		 	    	like_where=" and (parentid = '"+code+"' or codeitemid='"+code+"')";
		 	    }	    	
		    	
		    	boolean isCorrect=IsLastCode(table,code,backdate);
		    	leftsql.append(" and codesetid<>'@K'");
		 	    if(!isCorrect)
		 	    	leftsql.append(" and codeitemid='"+code+"'");
		 	    else
		 	    	leftsql.append("    "+like_where);
		 	   leftsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		 	}
		 return leftsql.toString();
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
	
	public String getLeftJoinSql(String t,ArrayList fieldList,String where,String code,String backdate,String orglike,String busiPriv)
	{
			
		StringBuffer select_str=new StringBuffer();
		StringBuffer select_columns=new StringBuffer();
		int n=0;
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldList.get(i);
			String itemid=fieldItem.getItemid();	
			if("b0110".equalsIgnoreCase(itemid))
			  select_columns.append("AAA."+itemid+" as "+itemid+",");
			else
			  select_columns.append(itemid+i+"."+itemid+" as "+itemid+",");
			n++;
		}
		// 增加长度控制。否则length==0时数组越界 chent 20171129 add
		if(select_columns.length() > 0) {
			select_columns.deleteCharAt(select_columns.length()-1);
		}
		FieldItem fielditem00=null;
		String table = t+"organization";		
		select_str.append("select end_date,codesetid,codeitemid,'"+t+"org' as orgtype,a0000");
		if(select_columns!=null&&select_columns.length()>0)
		{
			select_str.append(","+select_columns.toString());
		}
		select_str.append(" from "+table);
		select_str.append(" left join (select "+Sql_switcher.isnull("b0110","''")+" as b0110");
		select_str.append(" from B01 B");
		select_str.append(") AAA");
		select_str.append(" on ");
		select_str.append(table+".codeitemid=AAA.b0110 ");
		String itemid="";
		String oldItemid="";
		for(int i=0;i<fieldList.size();i++)
		{
		   	FieldItem fielditem=(FieldItem)fieldList.get(i);
		   	if(fielditem==null)
		   		continue;
			String setid=fielditem.getFieldsetid();		
			itemid=fielditem.getItemid();
			if("b0110".equalsIgnoreCase(itemid))
				continue;
			if("B01".equals(setid))
			{
				select_str.append(" left join (select "+itemid+",A.b0110 from b01 A");
				select_str.append(") "+itemid+i);
				select_str.append(" on ");
			}else
			{
				select_str.append(" left join (select "+itemid+",A.b0110 from "+setid+" A,");
				select_str.append("(select b0110,max(i9999) as i9999 from "+setid+" group by b0110)B");
				select_str.append(" where A.b0110=B.b0110 and A.i9999=B.i9999) "+itemid+i);
				select_str.append(" on ");
			}
			select_str.append(table+".codeitemid="+itemid+i+".b0110 ");
			oldItemid=itemid+i;
		}		
		select_str.append(where);		 
		String like_where="";
		 if(code==null||code.length()<=0)
		    {
		    	if(!this.userView.isSuper_admin()){
		    		String busi = this.getBusi_org_dept(busiPriv);
		    		if(busi.length()>2){
		    			this.getFormHM().put("busi_have", "1");
		    			select_str.append(" and (1=2");
						String[] org_depts = busi.split("`");
						for(int i=0;i<org_depts.length;i++){
							String org_dept = org_depts[i];
							if(org_dept.length()>2){
								if(orglike!=null&& "1".equals(orglike)){
									select_str.append(" or (codesetid<>'@K' and codeitemid like '"+org_dept.substring(2)+"%')");
								}else{
									select_str.append(" or (codesetid<>'@K' and codeitemid='"+org_dept.substring(2)+"')");
								}
							}else{
								if(orglike!=null&& "1".equals(orglike)){
									select_str.append(" or (1=1)");
								}else{
									select_str.append(" or (parentid=codeitemid)");
								}
							}
						}
						select_str.append(") and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		    			
		    		}else if("UN".equalsIgnoreCase(this.userView.getManagePrivCode())&&(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0))
					  {
						   
						  select_str.append(" and  codeitemid=parentid ");
						  select_str.append(" and codesetid<>'@K'");
						  select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					  }else
					  {
						  if(orglike!=null&& "1".equals(orglike)&&this.userView.getManagePrivCode().length()>0)
					 	  {
						    	like_where=" and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'";
					 	  }else
					 	  {
					 	    	like_where=" and codeitemid = '"+this.userView.getManagePrivCodeValue()+"'";
					 	  }
						  select_str.append(" and codesetid<>'@K'   "+like_where);
						  select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					 	  
					  }
	            }else
	            {
	            	if(orglike!=null&& "1".equals(orglike))
				 	{
	            		select_str.append(" and codesetid<>'@K'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				 	}else
				 		select_str.append(" and codesetid<>'@K' and parentid=codeitemid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	            }
		    }else
		    {
		    	
		    	if(orglike!=null&& "1".equals(orglike))
		 	    {
			    	//like_where=" and (parentid like '"+code+"%'or codeitemid='"+code+"')";
			    	like_where=" and (parentid like '"+code+"%' and (codeitemid<>'"+code+"' or not exists (select codeitemid from "+table+" where parentid='"+code+"' and codeitemid<>'"+code+"' and codesetid<>'@K' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)))";
		 	    }else
		 	    {
		 	    	//like_where=" and (parentid = '"+code+"' or codeitemid='"+code+"')";
		 	    	like_where=" and (parentid = '"+code+"' and (codeitemid<>'"+code+"' or not exists (select codeitemid from "+table+" where parentid='"+code+"' and codeitemid<>'"+code+"' and codesetid<>'@K' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)))";
		 	    }	    	
		    	
		    	boolean isCorrect=IsLastCode(table,code,backdate);
		    	select_str.append(" and codesetid<>'@K'");
		 	    if(!isCorrect)
		 	    	select_str.append(" and codeitemid='"+code+"'");
		 	    else
		 	    	select_str.append("    "+like_where);
		 	   select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		 	}
		return select_str.toString();
	}
	
	private String getBusi_org_dept(String busiPriv){
		String busi="";
		if("1".equals(busiPriv)){
		int status = this.userView.getStatus();
		if (!this.userView.isSuper_admin() /*&& 0 == status*/) {// 非超级用户组下业务用户
			String busi_org_dept = "";
			try {
				/*ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select busi_org_dept from operuser where username='"
						+ this.userView.getUserName() + "'";
				this.frecset = dao.search(sql);
				while (this.frecset.next()) {
					busi_org_dept = Sql_switcher.readMemo(this.frecset,
							"busi_org_dept");
				}*/
				busi_org_dept = this.userView.getUnitIdByBusi("4");
				if (busi_org_dept.length() > 0) {
					/*String str[] = busi_org_dept.split("\\|");
					for (int i = 0; i < str.length; i++) {// 1,UNxxx`UM9191`
						String tmp = str[i];
						String ts[] = tmp.split(",");
						if (ts.length == 2) {
							if("4".equals(ts[0])){
							busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(ts[1]);
								break;
							}
						}
					}*/
					busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`

			}
		}
		}
		return busi;
	}
}
