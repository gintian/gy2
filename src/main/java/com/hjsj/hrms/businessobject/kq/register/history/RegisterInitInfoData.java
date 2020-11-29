package com.hjsj.hrms.businessobject.kq.register.history;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.KQ_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class RegisterInitInfoData {
	
	/*通过传进的参数，组合select语句
	 * @param fieldsetlist 操作表的子集
	 * @param userbase  数据库前缀
	 * @param cur_date  操作时间
	 * @param code 部门
	 * @param whereIN select.in字句
	 * @param tablename 表明
	 * @return list，一个完整的sql 语句
	 * */
	public static ArrayList getSqlstr(ArrayList fieldsetlist,String userbase,String cur_date,String code,String whereIN,String kind,String tablename){
 	   
		StringBuffer wheresql=new StringBuffer();		
		
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);			
			   column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr+" ";
		wheresql.append(" from "+tablename+" ");		
		wheresql.append("where Q03Z0='"+cur_date+"'");
		if("1".equals(kind))
		{
		  wheresql.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			wheresql.append(" and e01a1 like '"+code+"%'");	
		}else
		{
		  wheresql.append(" and b0110 like '"+code+"%'");	
		}
		
		wheresql.append(" and UPPER(nbase)='"+userbase.toUpperCase()+"'");
		wheresql.append(" and a0100 in(select a0100 "+whereIN+")");
		String ordeby=" order by b0110,e0122,e01a1";
		ArrayList list= new ArrayList();
		list.add(0,sqlstr);
	    list.add(1,wheresql.toString());
	    list.add(2,ordeby);
	    list.add(3,columnstr);
	    return list;
  }
	//得到update-SQL语句
	public String updateSQL(String column){
    	StringBuffer updatesql=new StringBuffer();
    	StringBuffer wheresql= new StringBuffer();
    	//column=column+",";
    	  int i=0;
		  int r=0;		  
		  ArrayList list= new ArrayList();
		  while(i!=-1){		  
		   i=column.indexOf(",",r);			   
		   if(i!=-1){
		   String str=column.substring(r,i);
		   str=str.trim();	
		   list.add(str);
		   }		   
		   r=i+1;
		  }
		  updatesql.append("update Q03 set ");
		  wheresql.append(" where 1=1 and "+Sql_switcher.isnull("Q03Z3","'01'")+"<>'03' ");
		  for(int s=0;s<list.size();s++){
			  if(s<7){
			  wheresql.append(" and "+list.get(s).toString()+"=?");	  
			  }else{				
			  updatesql.append(list.get(s).toString()+"=?,");				
			  }
		  }
		  String updatestr=updatesql.toString();
		  int d=updatestr.lastIndexOf(",");
		  updatestr=updatestr.substring(0,d);
		  String wherestr=wheresql.toString();
		  String sqlstr=updatestr+wherestr;		  
    	return sqlstr;
    }
	//把考勤表单中提交的2维数据，从新整合，返回，多条记录的list
    public ArrayList getFormsList(ArrayList formslist,ArrayList fielditemlist,Connection conn){    	
    	int i=0; 
    	int r=0;  
    	int line=0;
    	ArrayList updateList=new ArrayList();
    	ArrayList valuelist=new ArrayList();
    	//判断几条记录
    	if(formslist!=null&&formslist.size()>0){
    	  ArrayList linelist=(ArrayList)formslist.get(0);
    	  line=linelist.size();
    	}
    	boolean isCorrect=false;
    	
    	for(;!isCorrect;i++){    		
    		if(i==formslist.size()){
    			i=0;
    			r++;
    		    updateList.add(valuelist);
    		    valuelist=new ArrayList();    		  
    		}
    		    		
    		ArrayList rr= new ArrayList();
    	   if(i<formslist.size()) {
               rr=(ArrayList)formslist.get(i);
           }
    	      
    		if(r==line){
    			isCorrect=true;    
    			continue;
    		}
    		if(rr!=null&&rr.size()==line){
    		    valuelist.add(rr.get(r).toString());
    		}else{
    			valuelist.add("");
    		}
    	}    
    	ArrayList recordlist =  getRecordList(updateList,fielditemlist,conn);    	
    	return recordlist;    	
    }
    public ArrayList getRecordList(ArrayList updateList){
    	ArrayList recordlist=new ArrayList();
    	ArrayList datalist=new ArrayList();
    	for(int i=0;i<updateList.size();i++){
    		ArrayList valuelist=(ArrayList)updateList.get(i);    		
    		boolean isCorrect=false;
    		int r=7;
    		while(!isCorrect){    			
    		  if(r==valuelist.size()){
        		   r=0;
        		}    
    		  if(r>=7)
    		  {
    			  String rrr=valuelist.get(r).toString();
    			  if(rrr==null||rrr.length()<=0) {
                      rrr="0";
                  }
    			  datalist.add(new Float(rrr));  
    		  }else
    		  {
    			  String rrr=valuelist.get(r).toString();
    			  if(rrr==null||rrr.length()<=0) {
                      rrr="";
                  }
    			  datalist.add(rrr);
    		  }
    		      			
    		  r++;
    		  if(r==7){    			 
    			  recordlist.add(datalist);
    			  datalist=new ArrayList();
    			  isCorrect=true;
    		  }
    		}
    	}
    	return recordlist;
    }
    public ArrayList getRecordList(ArrayList updateList,ArrayList fielditemlist,Connection conn){
    	ArrayList recordlist=new ArrayList();
    	ArrayList datalist=new ArrayList();
    	String value_s="";
    	String nbase="";
    	String a0100="";
    	String q03z0="";    	
    	for(int i=0;i<updateList.size();i++){
    		ArrayList valuelist=(ArrayList)updateList.get(i);     		
    		datalist=new ArrayList();  
    		int s=0;
    		for(int r=0;r<fielditemlist.size();r++)
    	   {
    	    		FieldItem fielditem=(FieldItem)fielditemlist.get(r); 
    	    		if("state".equals(fielditem.getItemid())) {
                        continue;
                    }
    	    		if(!"nbase".equals(fielditem.getItemid())&&!"a0100".equals(fielditem.getItemid())&&!"q03z0".equals(fielditem.getItemid()))
        			{
    	    			//增加过滤A01主集中的指标
    	    			boolean booindex=getindexA01(fielditem.getItemtype(),fielditem.getItemid(),fielditem.getItemdesc(),conn);
    	    			if(booindex)
    	    			{
    	    				if("A".equals(fielditem.getItemtype()))
        					{
        							value_s=(String)valuelist.get(s);
        							if(value_s==null||value_s.length()<=0) {
                                        value_s="";
                                    }
        							datalist.add(value_s);
        					}else if("N".equals(fielditem.getItemtype()))
        					{
        							value_s=(String)valuelist.get(s);
        							if(value_s==null||value_s.length()<=0) {
                                        value_s="0";
                                    }
        							datalist.add(new Float(value_s));
        					}
        					else if("D".equals(fielditem.getItemtype()))
        					{
        							value_s=(String)valuelist.get(s);
        							if(value_s==null||value_s.length()<=0){
        								value_s="0";
        								datalist.add(DateUtils.getDate(value_s,"yyyy-MM-dd"));
        							}else{
        								//修改添加；如果有数据DateUtils.getDate，后面批量修改会不支持这个时间格式；
        								value_s = value_s.replaceAll("\\.", "-");
        								datalist.add(DateUtils.getSqlDate(value_s,"yyyy-MM-dd"));
        							}
        					}
    	    			}
//        					if(fielditem.getItemtype().equals("A"))
//        					{
//        							value_s=(String)valuelist.get(s);
//        							if(value_s==null||value_s.length()<=0)
//        								value_s="";
//        							datalist.add(value_s);
//        					}else if(fielditem.getItemtype().equals("N"))
//        					{
//        							value_s=(String)valuelist.get(s);
//        							if(value_s==null||value_s.length()<=0)
//        								value_s="0";
//        							datalist.add(new Float(value_s));
//        					}
//        					else if(fielditem.getItemtype().equals("D"))
//        					{
//        							value_s=(String)valuelist.get(s);
//        							if(value_s==null||value_s.length()<=0){
//        								value_s="0";
//        								datalist.add(DateUtils.getDate(value_s,"yyyy-MM-dd"));
//        							}else{
//        								//修改添加；如果有数据DateUtils.getDate，后面批量修改会不支持这个时间格式；
//        								value_s = value_s.replaceAll("\\.", "-");
//        								datalist.add(DateUtils.getSqlDate(value_s,"yyyy-MM-dd"));
//        							}
//        					}
        			}else if("nbase".equals(fielditem.getItemid()))
        			{
        									
        						nbase=(String)valuelist.get(s);
        			}else if("a0100".equals(fielditem.getItemid()))
        			{
        						a0100=(String)valuelist.get(s);
        			}else if("q03z0".equals(fielditem.getItemid()))
        			{
        						q03z0=(String)valuelist.get(s);
        			}
    	    		s++;
    	    }
    		datalist.add(nbase);
    		datalist.add(a0100);
    		datalist.add(q03z0);    		
    		recordlist.add(datalist);
    	}
    	return recordlist;
    }
    /**
     * 断Q03中那些指标是从A01主集中取得的
     * @param itemtype
     * @param itemid
     * @param itemdesc
     * @return
     */
    public boolean getindexA01(String itemtype,String itemid,String itemdesc,Connection conn)
    {
    	boolean field=true;
    	itemtype=itemtype.toUpperCase();
    	itemid=itemid.toUpperCase();
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs=null;
    	String sql="select itemid from fielditem where fieldsetid='A01' and itemid='"+itemid+"' and itemtype='"+itemtype+"' and itemdesc='"+itemdesc+"'";
    	try
    	{
    		rs=dao.search(sql.toString());
    		while(rs.next())
    		{
    			String itemi = rs.getString("itemid");
    			if(!"A0101".equals(itemi)&&!"E0122".equals(itemi))
    			{
    				if(itemi!=null&&itemi.length()>0)
        			{
        				field=false;
        			}
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}catch(SQLException e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return field;
    }
    public String getWhereSQL(String userbase,String code,String coursedate,String whereIN,String kind,String tablename){
    	StringBuffer wheresql=new StringBuffer();
 	    wheresql.append(" from "+tablename+" ");
 	    wheresql.append(" where Q03Z0 like '"+coursedate+"%'");
 	    if("1".equals(kind))
		{
 	       wheresql.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			wheresql.append(" and e01a1 like '"+code+"%'");	
		}else{
		   wheresql.append(" and b0110 like '"+code+"%'");	
		}
 	    wheresql.append(" and dbase='"+userbase+"'");
 	    wheresql.append("and a0100 in(select a0100 "+whereIN+")");
 	    return wheresql.toString();
    }  
    public static int getI9999(String tablename,String userbase,String a0100){
    	int i9999=1;
    	String sql="select max(i9999) as i9999 from "+tablename+" where UPPER(nbase)='"+userbase.toUpperCase()+"' and a0100='"+a0100+"'";
    	List rs=null;
    	rs = ExecuteSQL.executeMyQuery(sql);
	       if(!rs.isEmpty())
	       {
	    	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);	    	  
	    	  String str_i9999=rec.get("i9999")!=null?rec.get("i9999").toString():"1";	  
	    	  if(str_i9999!=null&&str_i9999.length()>0){
	    		  i9999=Integer.parseInt(str_i9999); 
	    		  i9999=i9999+1;
	    	  }
	       }
    	return i9999;
    }
    public static ArrayList newFieldItemList(ArrayList fielditemlist)
    {
    	ArrayList list=new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			//System.out.println(fielditem.getItemid()+"--"+fielditem.getItemtype());
			if("1".equals(fielditem.getState()))
			{
						
						fielditem.setVisible(true);
			}else
			{
						fielditem.setVisible(false);
			}
			if(!"state".equals(fielditem.getItemid()))
			{
				list.add(fielditem.clone());
			}
				/*if(!"i9999".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid()))
				{
					
						//System.out.println(fielditem.getState());
					if("q03z5".equals(fielditem.getItemid()))
					{
						fielditem_c=fielditem;
					}else
					{
						list.add(fielditem);
					}	
					
				}*/
		}
    	
    	return list;
    }
    
    public static ArrayList newFieldOneList(ArrayList fielditemlist)
    {
    	ArrayList list=new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			if("A".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype()))
			{
				if(!"i9999".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())&&!"q03z5".equals(fielditem.getItemid()))
				{
					if("nbase".equals(fielditem.getItemid())||"a0100".equals(fielditem.getItemid())||"e0122".equals(fielditem.getItemid())||"a0101".equals(fielditem.getItemid())||"b0110".equals(fielditem.getItemid())||"e01a1".equals(fielditem.getItemid()))
					{
						fielditem.setVisible(false);
					}else
					{
						fielditem.setVisible(true);
					}
					list.add(fielditem.clone());
				}
			}
			
		}
    	
    	return list;
    }    
    public static String getUNB0110(String b0110)
    {
    	
        	b0110="UN"+b0110;
       
       return b0110;
    }
    /**根据权限,生成select.IN中的查询串
     * @param code
     *        链接级别
     * @param userbase
     *        库前缀
     * @param cur_date
     *        考勤日期
     * @return 返回查询串
     * */
    public static String getWhereINSql(UserView userView,String userbase){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
			{
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0) {
                        factor+=userView.getManagePrivCodeValue();
                    }
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			        try
			        {        
				        
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			        	if(userView.getKqManageValue()!=null&&!"".equals(userView.getKqManageValue())) {
                            strwhere=userView.getKqPrivSQLExpression("",userbase,fieldlist);
                        } else {
                            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
                        }
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
			        
			     
		
			}else{
				StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				kind="2";
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }
    
    // ------获得考勤部门的人员------start
    
    /**根据权限,生成select.IN中的查询串
     * @param code
     *        链接级别
     * @param userbase
     *        库前缀
     * @param cur_date
     *        考勤日期
     * @return 返回查询串
     * */
    public static String getWhereINSql2(UserView userView,String userbase){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
			{
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0) {
                        factor+=userView.getManagePrivCodeValue();
                    }
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			        try
			        {        
				        
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			        	if(userView.getKqManageValue()!=null&&!"".equals(userView.getKqManageValue())) {
                            strwhere=userView.getKqPrivSQLExpression("",userbase,fieldlist);
                        } else {
                            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
                        }
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
			        strwhere += getUnionSql (userView, userbase);
			     
		
			}else{
				StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				kind="2";
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }
    
    
    /**
     * 和部门e0122一块使用的条件
     * @param code
     * @param tableName
     * @param nbase
     * @return String 例如or exists(...)
     */
    public static String getKQ_DEPT_SQL_OR(String code, String nbase,UserView userView,String tableName) {
    	StringBuffer buff = new StringBuffer();
    	String field = KqParam.getInstance().getKqDepartment();
    	if (!"".equalsIgnoreCase(field)) {
    		if(code!=null&&code.length()>0) {
	    		buff.append(" or exists( select 1 from "+nbase+"a01 where "+field+" like '"+code+"%' and "+tableName+".a0100="+nbase+"a01.a0100 and "+tableName+".nbase='"+nbase+"')");
    		} else {
    			if(!userView.isSuper_admin()) {
    				buff.append(" or exists( select 1 from "+nbase+"a01 where "+field+" in (select distinct e0122 "+getWhereINSql(userView,nbase)+"))");
    			}
    		}
    	} else {
    		buff.append(" ");
    	}
    	
    	return buff.toString();
    }
    
    /**
     * 和部门e0122一块使用的条件
     * @param code
     * @param tableName
     * @param nbase
     * @return String 例如or exists(...)
     */
    public static String getAllKQ_DEPT_SQL_OR(String code, UserView userView) {
    	StringBuffer buff = new StringBuffer();
    	ArrayList list = userView.getPrivDbList();
    	String field = KqParam.getInstance().getKqDepartment();
    	if (list != null && list.size() > 0) {
    		for (int i = 0; i < list.size(); i++) {
		    	if (!"".equalsIgnoreCase(field)) {
		    		if(code!=null&&code.length()>0) {
			    		buff.append(" or exists( select 1 from "+list.get(i).toString()+"a01 where "+field+" like '"+code+"%')");
		    		} else {
		    			if(!userView.isSuper_admin()) {
		    				buff.append(" or exists( select 1 from "+list.get(i).toString()+"a01 where"+field+" in (select distinct e0122 "+getWhereINSql(userView,list.get(i).toString())+"))");
		    			}
		    		}
		    	} else {
		    		buff.append(" ");
		    	}
    		}
    	}
    	
    	return buff.toString();
    }
    
    
    /**
     * 和whereIN一块使用的sql
     * @param sqlstr
     * @param wherestr
     * @param code
     * @param userView
     * @param userbase
     * @return String 例如select a0100 from XXXa01 where xxxx lik '123%'
     */
    public static String getUnionSql (UserView userView, String nbase) {
    	StringBuffer buff= new StringBuffer();
    	if(!userView.isSuper_admin()) {
	    	buff.append(" union select a0100 from ");
	    	buff.append(nbase);
	    	buff.append("a01 where ");
	    	String field = KqParam.getInstance().getKqDepartment();
	    	/** 
	    	 * 如果设置了考勤部门，既根据管理范围权限控制，
	    	 *同时也需要兼顾考勤归属部门。
	    	 *变动前后部门的考勤员都可以看到异动人员的全月数据。
	    	 */	
		        
	    	if (! "".equalsIgnoreCase(field)) {
	    		String code = getKqPrivCodeValue(userView);
	    		if (code == null || code.length() <= 0) {//走管理范围
	    			code = userView.getManagePrivCodeValue();
	    			buff.append(field+" like '"+code+"%'");
	    		} else {// 考勤范围
	    			buff.append(field+" like '"+code+"%'");
	    		}
	    	} else {
	    		return " ";
	    	}
    	}
    	
    	return buff.toString();
    }
    
    public static String getWhereINSql2(UserView userView,String userbase,Connection conn){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
			{
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0) {
                        factor+=userView.getManagePrivCodeValue();
                    }
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			        try
			        {        
				        
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			        	if(userView.getKqManageValue()!=null&&!"".equals(userView.getKqManageValue())) {
                            strwhere=userView.getKqPrivSQLExpression("",userbase,fieldlist);
                        } else {
                            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
                        }
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
			        
		
			}else{
				StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				kind="2";
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }
//  得到可操作人员库人员
    public static ArrayList getDase3(HashMap formHM, UserView userView,Connection conn)
    {  
    	ArrayList kq_dbase_list=new ArrayList();
    	try{
    	ManagePrivCode managePrivCode=new ManagePrivCode(userView,conn);
    	String b0110=managePrivCode.getPrivOrgId();  
    	KqParameter kq_paramter = new KqParameter(formHM,userView,b0110,conn);	              
	    String kq__BASE=kq_paramter.getNbase();
    	ArrayList dbaselist=userView.getPrivDbList();     	
    	for(int i=0;i<dbaselist.size();i++){
    		String userbase=dbaselist.get(i).toString();
    		if(kq__BASE.indexOf(userbase)!=-1){
    			kq_dbase_list.add(userbase);
    		}
    	}
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
    	return kq_dbase_list;
    }
//  得到可操作人员库人员
    public static ArrayList getUNDase(Connection conn)
    {  
    	ArrayList kq_dbase_list=new ArrayList();
    	try{
        KQ_Parameter kq_Parameter=new KQ_Parameter();
        HashMap map=kq_Parameter.getUNParameter(conn);
	    String kq__BASE=(String)map.get("nbase");	    
    	ArrayList dbaselist=DataDictionary.getDbpreList();     	
    	for(int i=0;i<dbaselist.size();i++){
    		String userbase=dbaselist.get(i).toString();
    		if(kq__BASE.indexOf(userbase)!=-1){
    			kq_dbase_list.add(userbase);
    		}
    	}
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
    	return kq_dbase_list;
    }
    /**
     * 一个单位的人员库权限
     * @param formHM
     * @param userView
     * @param conn
     * @return
     */
    public static ArrayList getB0110Dase(HashMap formHM, UserView userView,Connection conn,String one_b0110)
    {  
    	ArrayList kq_dbase_list=new ArrayList();
    	try{
    	String b0110="UN"+one_b0110;
    	KqParameter kq_paramter = new KqParameter(formHM,userView,b0110,conn);	              
	    String kq__BASE=kq_paramter.getNbase();
    	ArrayList dbaselist=userView.getPrivDbList();    	
    	for(int i=0;i<dbaselist.size();i++){
    		String userbase=dbaselist.get(i).toString();
    		if(kq__BASE.indexOf(userbase)!=-1){
    			kq_dbase_list.add(userbase);
    		}
    	}
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
    	return kq_dbase_list;
    }
/**
 * 通过单位得到可操作的人员数据库
 * @param formHM
 * @param userView
 * @param b0110
 * @param conn
 * @return
 */
    public static String getOneB0110Dase(HashMap formHM,UserView userView,String userbase,String b0110,Connection conn)
    {  
    	String nbase="";
    	try{
    	String b0110UN="UN"+b0110;
    	KqParameter kq_paramter = new KqParameter(formHM,userView,b0110UN,conn);	              
	    String kq__BASE=kq_paramter.getNbase();
    	if(kq__BASE.indexOf(userbase)!=-1){
    			nbase=userbase;    		
    	}
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
    	return nbase;
    }
    /**得到当前考勤员可操作的部门
     * **/
    public static String selcet_OrgId(String nbase,String org_id,String whereIN){
 	   StringBuffer sqlstr= new StringBuffer();
 	   sqlstr.append("select distinct "+org_id+" from "+nbase+"A01 ");	  
 	   sqlstr.append(" where  "); 	    
 	   sqlstr.append("   "+org_id+" in(select distinct "+org_id+" "+whereIN+") "); 
 	   sqlstr.append(" and EXISTS(select 1 from organization where "+nbase+"A01."+org_id+"=organization.codeitemid)");
 	   return sqlstr.toString();
    } 
    /**得到当前考勤员可操作的部门
     * **/
    public static String selcet_one_b0100_OrgId(String nbase,String b0110,String org_id,String whereIN){
 	   StringBuffer sqlstr= new StringBuffer();
 	   sqlstr.append("select distinct "+org_id+" from "+nbase+"A01 ");	  
 	   sqlstr.append(" where b0110='"+b0110+"' "); 	    
 	   sqlstr.append("  and "+org_id+" in(select distinct "+org_id+" "+whereIN+") "); 
 	   sqlstr.append(" and EXISTS(select 1 from organization where "+nbase+"A01."+org_id+"=organization.codeitemid)");
 	   return sqlstr.toString();
    } 
    /**
     * 得到所有参加考勤的部门编号
     * @param nbase  参加考勤的人员库前缀
     * @param whereIN 可操作人员库子句
     * @param kq_type 参加考勤的关键字
     * @param conn 数据连接
     * @return  list 
     * */
    public static ArrayList getAllBaseE0122(String nbase,String whereIN,Connection conn)
    {
    	
    	
    	ArrayList list = new ArrayList();
    	RowSet rowSet=null;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select distinct e0122,b0110 from "+nbase+"A01");
    	sql.append(" where 1=1");
    	sql.append("and a0100 in(select a0100 "+whereIN+")");
    	sql.append(" and EXISTS(select 1 from organization where "+nbase+"A01.e0122=organization.codeitemid)");
    	ContentDAO dao = new ContentDAO(conn);
    	try
    	{
    		rowSet=dao.search(sql.toString());
    		while(rowSet.next())
    		{
    		    String e0122=rowSet.getString("e0122");    			
    			if(e0122!=null&&e0122.length()>0)
    			{
    				list.add(e0122);
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     }    		
    	return list;
    }
    public static ArrayList getAllBaseOrgid(String nbase,String orgid,String whereIN,Connection conn)
    {
    	
    	
    	ArrayList list = new ArrayList();
    	RowSet rowSet=null;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select distinct "+orgid+" from "+nbase+"A01");
    	sql.append(" where 1=1");
    	sql.append(" and a0100 in(select a0100 "+whereIN+")");
    	sql.append(" and EXISTS(select 1 from organization where "+nbase+"A01."+orgid+"=organization.codeitemid)");
    	ContentDAO dao = new ContentDAO(conn);
    	try
    	{
    		rowSet=dao.search(sql.toString());
    		while(rowSet.next())
    		{
    		    String e0122=rowSet.getString(orgid);    			
    			if(e0122!=null&&e0122.length()>0)
    			{
    				list.add(e0122);
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     }    		
    	return list;
    }
    public static ArrayList getAllBaseOrgid(String nbase,String orgid,String whereIN,Connection conn,String code)
    {
    	
    	
    	ArrayList list = new ArrayList();
    	RowSet rowSet=null;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select distinct "+orgid+" from "+nbase+"A01");
    	sql.append(" where 1=1");
    	sql.append(" and a0100 in(select a0100 "+whereIN+")");
    	sql.append(" and EXISTS(select 1 from organization where "+nbase+"A01."+orgid+"=organization.codeitemid) and "+orgid+" like '"+code+"%'");
    	ContentDAO dao = new ContentDAO(conn);
    	try
    	{
    		rowSet=dao.search(sql.toString());
    		while(rowSet.next())
    		{
    		    String e0122=rowSet.getString(orgid);    			
    			if(e0122!=null&&e0122.length()>0)
    			{
    				list.add(e0122);
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     }    		
    	return list;
    }
    /**
     * 得到一个单位所有参加考勤的部门编号
     * @param nbase  参加考勤的人员库前缀
     * @param whereIN 可操作人员库子句
     * @param kq_type 参加考勤的关键字
     * @param conn 数据连接
     * @return  list 
     * */
    public static ArrayList getOneBaseE0122(String nbase,String b0110,String whereIN,Connection conn)
    {
    	ArrayList list = new ArrayList();
    	RowSet rowSet=null;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select distinct e0122 from "+nbase+"A01");
    	sql.append(" where b0110='"+b0110+"' ");
    	sql.append("and a0100 in(select a0100 "+whereIN+")");
    	ContentDAO dao = new ContentDAO(conn);
    	try
    	{
    		rowSet=dao.search(sql.toString());
    		while(rowSet.next())
    		{
    		    String e0122=rowSet.getString("e0122");    			
    			if(e0122!=null&&e0122.length()>0)
    			{
    				list.add(e0122);
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
	     }    		
    	return list;
    }
    /**
     * 考勤日期的下拉菜单
     * @param datelist 当前考勤期间的所有日期 
     *        CommonData 
     *        getDataValue  值
     *        getDataName  显示
     * @param  registerdate 当前日期    
     * @return 
     *        select的html代码   
     */
    public static String getDateSelectHtml(ArrayList datelist,String registerdate)
    {
    	StringBuffer selecthtml= new StringBuffer();
    	String kqDateZs = SystemConfig.getPropertyValue("kqDaysHide");//考勤期间下拉列表框按默认方式显示工作日标识
    	String kqDateYs = SystemConfig.getPropertyValue("kqHolidaysColor");//是否改变颜色
    	selecthtml.append("<select name='registerdate' size='1' onchange='javascript:change()'>");
    	if(registerdate==null||registerdate.length()<=0)
		{
    		CommonData vo=(CommonData)datelist.get(0);
			registerdate=vo.getDataValue();
		}
    	String rest_state=ResourceFactory.getProperty("kq.date.work");
    	for(int i=0;i<datelist.size();i++)
    	{
    		CommonData vo=(CommonData)datelist.get(i);
    		String style="";
    		if("1".equals(kqDateYs)){
    			if (vo.getDataName().indexOf("星期日") != -1
						|| vo.getDataName().indexOf("星期六") != -1) {
					style = "style='COLOR: #009966'";
				} else {
					style = "style='COLOR: #000000'";
				}
    		}else{
    			if (vo.getDataName().indexOf(rest_state) != -1) {
					style = "style='COLOR: #000000'";
				} else {
					// style="style='COLOR: #FF0000'"; 改为绿色与排班统一
					style = "style='COLOR: #009966'";
				}
    		}
    		if(registerdate.equals(vo.getDataValue().trim()))
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+" selected='selected'>");
    		}else
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+">");
    		}
    		if("1".equals(kqDateZs)&& !"全部".equals(vo.getDataName())){
    			String name = vo.getDataName();
    			int endIndex = name.lastIndexOf(" ");
    			selecthtml.append(vo.getDataName().substring(0, endIndex));
    		}else{
    			selecthtml.append(vo.getDataName());
    		}
    		selecthtml.append("</option>");
    	}
    	selecthtml.append("</select> ");
    	return selecthtml.toString();
    }
    /**
     * 考勤日期的下拉菜单
     * @param datelist 当前考勤期间的所有日期 
     *        CommonData 
     *        getDataValue  值
     *        getDataName  显示
     * @param  registerdate 当前日期    
     * @return 
     *        select的html代码   
     */
    public static String getStatSelectHtml(ArrayList datelist,String registerdate)
    {
    	StringBuffer selecthtml= new StringBuffer();
    	selecthtml.append("<select name='registerdate' size='1'>");
    	if(registerdate==null||registerdate.length()<=0)
		{
    		CommonData vo=(CommonData)datelist.get(0);
			registerdate=vo.getDataValue();
		}
    	String rest_state=ResourceFactory.getProperty("kq.date.work");
    	for(int i=0;i<datelist.size();i++)
    	{
    		CommonData vo=(CommonData)datelist.get(i);
    		String style="";    		
    		if(vo.getDataName().indexOf(rest_state)!=-1)
    		{
    			style="style='COLOR: #000000'";
    		}else
    		{
    			style="style='COLOR: #FF0000'";
    		}
    		if(registerdate.equals(vo.getDataValue().trim()))
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+" selected='selected'>");
    		}else
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+">");
    		}
    		selecthtml.append(vo.getDataName());
    		selecthtml.append("</option>");
    	}
    	selecthtml.append("</select> ");
    	return selecthtml.toString();
    }
    /**
     * 考勤日期的下拉菜单
     * @param datelist 当前考勤期间的所有日期 
     *        CommonData 
     *        getDataValue  值
     *        getDataName  显示
     * @param  jsdatetime 当前日期    
     * @return 
     *        select的html代码   
     */
    public static String getStatSelectjsHtml(ArrayList datelist,String jsdatetime)
    {
    	StringBuffer selecthtml= new StringBuffer();
    	selecthtml.append("<select name='jsdatetime' size='1'>");
    	if(jsdatetime==null||jsdatetime.length()<=0)
		{
    		CommonData vo=(CommonData)datelist.get(0);
    		jsdatetime=vo.getDataValue();
		}
    	String rest_state=ResourceFactory.getProperty("kq.date.work");
    	for(int i=0;i<datelist.size();i++)
    	{
    		CommonData vo=(CommonData)datelist.get(i);
    		String style="";    		
    		if(vo.getDataName().indexOf(rest_state)!=-1)
    		{
    			style="style='COLOR: #000000'";
    		}else
    		{
    			style="style='COLOR: #FF0000'";
    		}
    		if(jsdatetime.equals(vo.getDataValue().trim()))
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+" selected='selected'>");
    		}else
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+">");
    		}
    		selecthtml.append(vo.getDataName());
    		selecthtml.append("</option>");
    	}
    	selecthtml.append("</select> ");
    	return selecthtml.toString();
    }
    /**
     * 新建临时表的名字
     * **/
    public static String getTmpTableName(String UserId,String PrivCode) 
    {
    	StringBuffer tablename=new StringBuffer();
		tablename.append("kqs");
		tablename.append("_");
		if("@K".equalsIgnoreCase(PrivCode))
		{
			tablename.append("zw"+UserId);
		}else
		{
			tablename.append(PrivCode);
		}
		tablename.append("_");
		tablename.append(UserId);
		return tablename.toString();
    }
    /*通过传进的参数，组合select语句
	 * @param fieldsetlist 操作表的子集
	 * @param userbase  数据库前缀
	 * @param cur_date  操作时间
	 * @param code 部门
	 * @param whereIN select.in字句
	 * @param tablename 表明
	 * @return list，一个完整的sql 语句
	 * */
	public static ArrayList getSqlstr2(ArrayList fieldsetlist,ArrayList kq_dbase_list,String cur_date,String code,String kind,String tablename,UserView userView,String showtype,String where_c)
	{

		StringBuffer wheresql=new StringBuffer();
		StringBuffer condition=new StringBuffer();//打印高级花名册的条件
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);			
			   column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr+" ";
		wheresql.append(" from "+tablename+" where");		
		condition.append(" Q03Z0='"+cur_date+"'");
		if(code==null||code.length()<=0)
		{
			code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		}
		// ------获得考勤部门的人员------start
		if("1".equals(kind))
		{
			condition.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			condition.append(" and e01a1 like '"+code+"%'");	
		}else
		{
			condition.append(" and b0110 like '"+code+"%'");	
		}
		InfoUtils infoUtils =new InfoUtils ();
		// ------获得考勤部门的人员------end
		if(!"all".equals(showtype))
		{
			condition.append(" and q03z5='"+showtype+"'");	
		}
		if(where_c!=null&&where_c.length()>0) {
            condition.append(" "+where_c+"");
        }
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			if(i>0)
			{
				condition.append(" or ");  
			}else
			{
				condition.append(" and ( ");	
			}
			condition.append(" (UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
			String dbase=kq_dbase_list.get(i).toString();			
			String whereIN=getWhereINSql(userView,dbase);
			// ------获得考勤部门的人员------start
			whereIN=infoUtils.getPrivSqlExists(whereIN,kq_dbase_list.get(i).toString()+"A01","A");
			condition.append(" and exists(select a0100 "+whereIN+" and "+tablename+".a0100=A.a0100) ");
			/*if (code.length() > 0) {
				condition.append(" and a0100 in(select a0100 "+whereIN+getUnionSql (userView, dbase)+") ");
			} else {
				condition.append(" and a0100 in(select a0100 "+whereIN+") ");
			}*/
			// ------获得考勤部门的人员------end
			condition.append(")");
			if(i==kq_dbase_list.size()-1) {
                condition.append(")");
            }
		}
		String ordeby=" order by b0110,e0122,e01a1,a0100";
		wheresql.append(" "+condition.toString());
		ArrayList list= new ArrayList();
		list.add(0,sqlstr);		
		list.add(1,wheresql.toString());
	    list.add(2,ordeby);
	    list.add(3,columnstr);
	    list.add(4,condition.toString());
	    return list;
  }
	
	/*通过传进的参数，组合select语句
	 * @param fieldsetlist 操作表的子集
	 * @param userbase  数据库前缀
	 * @param cur_date  操作时间
	 * @param code 部门
	 * @param whereIN select.in字句
	 * @param tablename 表明
	 * @return list，一个完整的sql 语句
	 * */
	public static ArrayList getSqlstr5(ArrayList fieldsetlist,ArrayList kq_dbase_list,String cur_date,String code,String kind,String tablename,UserView userView,String showtype,String where_c)
	{

		StringBuffer wheresql=new StringBuffer();
		StringBuffer condition=new StringBuffer();//打印高级花名册的条件
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);			
			   column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr+" ";		
		condition.append(" Q03Z0='"+cur_date+"'");
		if(code==null||code.length()<=0)
		{
			code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		}
		// ------获得考勤部门的人员------start
		if("1".equals(kind))
		{
			condition.append(" and e0122 like '"+code+"%'");			
		}else if("0".equals(kind))
		{
			condition.append(" and e01a1 like '"+code+"%'");	
		}else
		{
			condition.append(" and b0110 like '"+code+"%'");	
		}
		InfoUtils infoUtils =new InfoUtils ();
		// ------获得考勤部门的人员------end
		if(!"all".equals(showtype))
		{
			condition.append(" and q03z5='"+showtype+"'");	
		}
		StringBuffer sql=new StringBuffer();
		if(where_c!=null&&where_c.length()>0) {
            condition.append(" "+where_c+"");
        }
		wheresql.append(condition);
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			sql.append(sqlstr);
			sql.append(" from "+tablename+" where");		
			if(i>0)
			{
				wheresql.append(" or ");  
			}else
			{
				wheresql.append(" and ( ");	
			}
			sql.append(" "+condition.toString());
			sql.append(" and  UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
			wheresql.append(" (UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
			String dbase=kq_dbase_list.get(i).toString();	
			if(!userView.isSuper_admin())
			{
				String whereIN=getWhereINSql(userView,dbase);
				// ------获得考勤部门的人员------start
				whereIN=infoUtils.getPrivSqlExists(whereIN,kq_dbase_list.get(i).toString()+"A01","A");
				sql.append(" and exists(select a0100 "+whereIN+" and "+tablename+".a0100=A.a0100) ");
				wheresql.append(" and exists(select a0100 "+whereIN+" and "+tablename+".a0100=A.a0100) ");
			}
			sql.append(" union ");
			wheresql.append(")");
			if(i==kq_dbase_list.size()-1) {
                wheresql.append(")");
            }
		}
		sql.setLength(sql.length()-7);
		String ordeby=" order by b0110,e0122,e01a1,a0100";		
		ArrayList list= new ArrayList();
		list.add(0,sql.toString());		
		list.add(1,"");
	    list.add(2,ordeby);
	    list.add(3,columnstr);
	    list.add(4,wheresql.toString());
	    //System.out.println(wheresql.toString());
	    return list;
  }
	
	 /*通过传进的参数，组合select语句
	 * @param fieldsetlist 操作表的子集
	 * @param userbase  数据库前缀
	 * @param cur_date  操作时间
	 * @param code 部门
	 * @param whereIN select.in字句
	 * @param tablename 表明
	 * @return list，一个完整的sql 语句
	 * */
	public static ArrayList getSqlstr6(ArrayList fieldsetlist,ArrayList kq_dbase_list,String cur_date,String code,String kind,String tablename,UserView userView,String showtype,String where_c,Connection conn)
	{
 	   
		StringBuffer wheresql=new StringBuffer();
		StringBuffer condition=new StringBuffer();//打印高级花名册的条件
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		StringBuffer columnJoin =new StringBuffer();
		StringBuffer joinTable = new StringBuffer();
		KqParameter para = new KqParameter(userView, "", conn);
		HashMap hashmap = para.getKqParamterMap();
		String g_no = (String) hashmap.get("g_no");
		String cardno = (String) hashmap.get("cardno");
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);
			String itemid = fielditem.getItemid();
			if(column.length()< 1){
				column.append(itemid);
				if ("A0100".equalsIgnoreCase(itemid)
						|| "nbase".equalsIgnoreCase(itemid)) {
					columnJoin.append("Q." + itemid);
				}else if(itemid.equalsIgnoreCase(g_no)
						|| itemid.equalsIgnoreCase(cardno)){
					columnJoin.append("A." + itemid);
				}else{
					columnJoin.append(itemid);
				}
			}else{
				column.append("," + itemid);
				if ("A0100".equalsIgnoreCase(itemid)
						|| "nbase".equalsIgnoreCase(itemid)) {
					columnJoin.append(",Q." + itemid);
				}else if(itemid.equalsIgnoreCase(g_no)
						|| itemid.equalsIgnoreCase(cardno)){
					columnJoin.append(",A." + itemid);
				}else{
					columnJoin.append("," + itemid);
				}
			}
		}
		columnJoin.append(",dbid,A0000");
		String columnstr=column.toString() + ",dbid,a0000";
		String sqlstr="select "+columnstr+" ";
		wheresql.append(" from ");
		wheresql.append("");
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		String expres = userView.getPrivExpression();
		String field = KqParam.getInstance().getKqDepartment();
		
		String str = "";
		if("1".equals(kind))
		{
			str = "E0122";
		}else if("0".equals(kind))
		{
			str = "E01A1";	
		}else
		{
			str = "B0110";		
		}
		for(Iterator it = kq_dbase_list.iterator();it.hasNext();){
			String nbase = ((String)it.next()).toUpperCase();
			String whereIN = null;
			try {
				if(expres.length() > 0){
					whereIN = userView.getPrivSQLExpression(expres,nbase,false,new ArrayList());
					
				}else{
					whereIN = getWhereINSql(userView,nbase);
				}
				
				if(joinTable.length()<1){
					joinTable.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN);
					if (field != null && field.length() > 0) {
						joinTable.append(" union SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN.replaceAll(str, field));
					} 
				}else{
					joinTable.append(" UNION SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN);
					
					if (field != null && field.length() > 0) {
						joinTable.append(" union SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN.replaceAll(str, field));
					} 
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			}
		}
		wheresql.append("(SELECT " + columnJoin + " FROM " + tablename + " Q INNER JOIN (" + joinTable + ") A ON Q.A0100=A.A0100 AND UPPER(Q.nbase)=A.nbase) B");
		wheresql.append(" where");
		condition.append(" Q03Z0='"+cur_date+"'");
		if(code==null||code.length()<=0)
		{
			code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		}
		// ------获得考勤部门的人员------start
		condition.append(" and " + str + " like '"+code+"%'");	
		
		// ------获得考勤部门的人员------end
		if(!"all".equals(showtype))
		{
			condition.append(" and q03z5='"+showtype+"'");	
		}
		if(where_c!=null&&where_c.length()>0) {
            condition.append(" "+where_c+"");
        }
		wheresql.append(" "+condition.toString());
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			if(i>0)
			{
				condition.append(" or ");  
			}else
			{
				condition.append(" and ( ");	
			}
			condition.append(" (UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
			String dbase=kq_dbase_list.get(i).toString();			
			String whereIN=getWhereINSql(userView,dbase);			
			// ------获得考勤部门的人员------start
			if(whereIN.toLowerCase().indexOf("where")!=-1) {
                condition.append(" and EXISTS(select "+dbase+"A01.a0100 "+whereIN+" and "+dbase+"A01.a0100="+tablename+".a0100) ");
            } else{
				condition.append(" and EXISTS(select "+dbase+"A01.a0100 "+whereIN+" WHERE "+dbase+"A01.a0100="+tablename+".a0100) ");
			}
			// ------获得考勤部门的人员------end
			condition.append(")");
			if(i==kq_dbase_list.size()-1) {
                condition.append(")");
            }
		}
		//String ordeby=" order by dbid,a0000,q03z0";
		String ordeby = " order by b0110,e0122,e01a1,a0100,nbase";
		ArrayList list= new ArrayList();
		list.add(0,sqlstr);		
		list.add(1,wheresql.toString());
	    list.add(2,ordeby);
	    list.add(3,columnstr);
	    list.add(4,condition.toString());
	    return list;
  }
	
	/**
	 * 获得sql语句
	 * @param fieldsetlist ArrayList 列
	 * @param kq_dbase_list ArrayList 人员库
	 * @param cur_date String 当前时间
	 * @param code 组织机构代码
	 * @param kind 组织机构类别，
	 * @param tablename String 表名
	 * @param userView UserView 用户
	 * @param showtype String 显示类型
	 * @param where_c String 条件
	 * @return ArrayList sql语句
	 */
	public static ArrayList getSqlstr4(ArrayList fieldsetlist,ArrayList kq_dbase_list,String cur_date,String code,String kind,String tablename,UserView userView,String showtype,String where_c) {
 	   
		StringBuffer wheresql = new StringBuffer();
		StringBuffer condition = new StringBuffer();//打印高级花名册的条件
		
		//生成没有高级条件的from后的sql语句
		StringBuffer column = new StringBuffer();
		for(int i = 0; i < fieldsetlist.size(); i++) {
			FieldItem fielditem = (FieldItem)fieldsetlist.get(i);			
			column.append(fielditem.getItemid()+",");
		}
		
		int l = column.toString().length()-1;
		String columnstr = column.toString().substring(0,l);
		String sqlstr = "select " + columnstr + " ";
		//wheresql.append(" from " + tablename + " where");		
		condition.append(" Q03Z0='"+cur_date+"'");
		
		if("1".equals(kind)) {
			condition.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind)) {
			condition.append(" and e01a1 like '"+code+"%'");	
		}else {
			condition.append(" and b0110 like '"+code+"%'");	
		}
		
		
		if(!"all".equals(showtype)) {
			condition.append(" and q03z5='" + showtype + "'");	
		}
		
		if(where_c != null && where_c.length() > 0) {
			condition.append(" "+where_c+"");
		}
		
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			if(i>0)
			{
				condition.append(" or ");  
			}else
			{
				condition.append(" and ( ");	
			}
			condition.append(" (UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
			String dbase=kq_dbase_list.get(i).toString();
			
			String expres = userView.getPrivExpression();
			String field = KqParam.getInstance().getKqDepartment();
			if(field!=null&&field.length()>0) {
                expres = expres.replaceAll("E0122", field);
            }
			String whereIN = "";
			try {
				whereIN = userView.getPrivSQLExpression(expres,dbase,false,new ArrayList());
				if(field!=null&&field.length()>0) {
                    whereIN = whereIN.replaceAll("E0122", field);
                }
			} catch(Exception e){
				e.printStackTrace();
			} 

			condition.append(" and a0100 in(select a0100 "+whereIN+") ");

			condition.append(")");
			if(i==kq_dbase_list.size()-1) {
                condition.append(")");
            }
		}
		String ordeby=" order by b0110,e0122,e01a1,a0100";
		wheresql.append(" "+condition.toString());
		ArrayList list= new ArrayList();
		list.add(0,sqlstr);		
		list.add(1,wheresql.toString());
	    list.add(2,ordeby);
	    list.add(3,columnstr);
	    list.add(4,condition.toString());
	    return list;
  }
	
	public static ArrayList getSqlstr3(ArrayList fieldsetlist,ArrayList kq_dbase_list,String cur_date,String code,String kind,String tablename,UserView userView,String showtype,String where_c)
	{
		StringBuffer wheresql=new StringBuffer();
		StringBuffer condition=new StringBuffer();//打印高级花名册的条件
		//生成没有高级条件的from后的sql语句
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldsetlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldsetlist.get(i);			
			   column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr+" ";
		wheresql.append(" from "+tablename+" where");		
		condition.append(" Q03Z0='"+cur_date+"'");
		if(code==null||code.length()<=0)
		{
			code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		}
		// ------获得考勤部门的人员------start
		if("1".equals(kind))
		{
			condition.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			condition.append(" and e01a1 like '"+code+"%'");	
		}else
		{
			condition.append(" and b0110 like '"+code+"%'");	
		}
		// ------获得考勤部门的人员------end
		if(!"all".equals(showtype))
		{
			condition.append(" and q03z5='"+showtype+"'");	
		}
		if(where_c!=null&&where_c.length()>0) {
            condition.append(" "+where_c+"");
        }
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			if(i>0)
			{
				condition.append(" or ");  
			}else
			{
				condition.append(" and ( ");	
			}
			condition.append(" (UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
			String dbase=kq_dbase_list.get(i).toString();			
			String whereIN=getWhereINSql(userView,dbase);
			// ------获得考勤部门的人员------start
			condition.append(" and a0100 in(select a0100 "+whereIN+") ");
			//condition.append(" and a0100 in(select a0100 "+whereIN+getUnionSql (userView, dbase)+") ");
			// ------获得考勤部门的人员------end
			condition.append(")");
			if(i==kq_dbase_list.size()-1) {
                condition.append(")");
            }
		}
		String ordeby=" order by b0110,e0122,e01a1,a0100";
		wheresql.append(" "+condition.toString());
		ArrayList list= new ArrayList();
		list.add(0,sqlstr);		
		list.add(1,wheresql.toString());
	    list.add(2,ordeby);
	    list.add(3,columnstr);
	    list.add(4,condition.toString());
	    return list;
  }
	public static String getDateStr(String datestr)
	{
		Date d=DateUtils.getDate(datestr,"yyyy.MM.dd");
		datestr=DateUtils.format(d,"yyyy.MM.dd");
		return datestr;
	}	
	 /**
	    * 字符串串联
	    * @return
	    */
	 public static String getResult()
	 {
		   String result="+";
		   switch(Sql_switcher.searchDbServer())
			{
			  case Constant.MSSQL:
			  {
				  result = "+";
				  break;
			  }			 
			   case Constant.ORACEL:
			  {
				   
				   result = "||";
				  break;
			  }
			  case Constant.DB2:
			  {
				  result = "||";
				  break;
			  }
			}
			 return result;	  
	 }
	 /**
		 * 取登录考勤用户库的列表
		 * @return
		 * @throws Exception
		 */
	    public static ArrayList getDbList(String code ,String kind,HashMap formHM, UserView userView,Connection conn)throws GeneralException
	    {
	        String b0110=code;
	        String codesetid="";
	        if("1".equals(kind)|| "0".equals(kind))
	        {
	        	codesetid=code;
	        	do
	        	{
	        		String codeset[]=getB0100(b0110,conn);
	        		if(codeset!=null&&codeset.length>=0)
	            	{
	            		codesetid=codeset[0];
	            		b0110=codeset[1];
	            	}
	        	}while(!"UN".equals(codesetid));
	        	
	        }
			ArrayList dblist=RegisterInitInfoData.getB0110Dase(formHM,userView,conn,b0110);
			
			       
	        return dblist;
	    }
	    /**
	     * 得到单位
	     * @param code
	     * @param kind
	     * @param formHM
	     * @param userView
	     * @param conn
	     * @return
	     * @throws GeneralException
	     */
	    public static String getDbB0100(String code ,String kind,HashMap formHM, UserView userView,Connection conn)throws GeneralException
	    {
	        String b0110=code;
	        String codesetid="";
	        if("1".equals(kind)|| "0".equals(kind))
	        {
	        	codesetid=code;
	        	do
	        	{
	        		String codeset[]=getB0100(b0110,conn);
	        		if(codeset!=null&&codeset.length>=0)
	            	{
	            		codesetid=codeset[0];
	            		b0110=codeset[1];
	            	}
	        	}while(!"UN".equals(codesetid));
	        	
	        }		
			return b0110;
	    }
	    public static String[] getB0100(String codeitemid,Connection conn)throws GeneralException
	    {
	    	String codeset[]=new String[2];
	    	String parentid="";  
	    	String codesetid="";
	    	RowSet rs=null;
	    	try
	        {
	     	   String orgSql="SELECT parentid,codeitemid,codesetid  from organization where codeitemid='"+ codeitemid +"'";
	     	   ContentDAO dao = new ContentDAO(conn);
	     	   
	     	   rs=dao.search(orgSql);
	     	   if(rs.next())
	     	   {
	     		  parentid=rs.getString("parentid");
	     		  codesetid=rs.getString("codesetid");
	     		 if(codesetid!=null&& "UN".equalsIgnoreCase(codesetid))
	     		  {
	     			  codeset[0]="UN";
	        		  codeset[1]=parentid;
	     		  }else
	     		  {
	     			 orgSql="SELECT parentid,codesetid from organization where codeitemid='"+ parentid +"'";
	     			 rs=dao.search(orgSql);
	     			 if(rs.next())
	     			 {
	     			  codeset[0]=rs.getString("codesetid");
	          		  codeset[1]=parentid;
	     			 }
	     		  } 
	     		}else
	     	   {
	     			codeset[0]="UN";
	          		codeset[1]=parentid;
	     		  //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.param.nosave.userbase"),"",""));
	     	   }
	        }catch(Exception e)
	        {
	     	   e.printStackTrace();
	     	  throw GeneralExceptionHandler.Handle(e);
	        }finally
		     {
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		     } 
	        return codeset;
	    }
	    /**
	     * 得到一个select的下拉框
	     * @return
	     */
	    public static ArrayList getShowType()
	    {
	    	CommonData typevo=null;
	    	ArrayList list=new ArrayList();
	    	typevo = new CommonData("all","全部");
			list.add(typevo);
			typevo = new CommonData("01","起草");
			list.add(typevo);
			typevo = new CommonData("02","已报批");
			list.add(typevo);
			typevo = new CommonData("08","报审");
			list.add(typevo);
			typevo = new CommonData("07","驳回");
			list.add(typevo);
			typevo = new CommonData("03","已批");
			list.add(typevo);
			return list;
	    }
	    
	    public static String getPrvListWhere(ArrayList kq_dbase_list,UserView userView)
	    {
	    	StringBuffer condition=new StringBuffer();
	    	for(int i=0;i<kq_dbase_list.size();i++)
			{
				if(i>0)
				{
					condition.append(" or ");  
				}else
				{
					condition.append(" and ( ");	
				}
				condition.append(" UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
				if(i==kq_dbase_list.size()-1) {
                    condition.append(")");
                }
			}
	    	for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();			
				String whereIN=RegisterInitInfoData.getWhereINSql(userView,dbase);
				if(i>0)
				   {
					   condition.append(" or ");  
				   }else
				   {
					   condition.append(" and ( ");    
				   }
				   condition.append(" a0100 in(select a0100 "+whereIN+") "); 
			   if(i==kq_dbase_list.size()-1) {
                   condition.append(")");
               }
			}	
	    	return condition.toString();
	    }
	    public static String getKindValue(String kind,UserView userView)
	    {
	    	String r_value="";
	    	if(kind==null||kind.length()<=0)
	    	{
	    		String code=getKqPrivCodeValue(userView);
	    		if(code!=null)
	    		{
	    			if("UN".equals(getKqPrivCode(userView)))
					{
	    				r_value="2";
					}
					else if("UM".equals(getKqPrivCode(userView)))
					{
						r_value="1";
					}else if("@K".equalsIgnoreCase(getKqPrivCode(userView)))
					{
						r_value="0";
					}else
					{
						r_value="2";
					}
	    		}else
	    		{
	    			r_value="2";
	    		}
	    	}
	    	return r_value;
	    }
	    /**
	     * 通过a_code:如UN01返回where条件子传
	     * @param a_code
	     * @return
	     */
	    public String getCodeItemWhere(String a_code)
		{
			String where="";
			String org_str="";
			if(a_code!=null&&a_code.length()>0)
			{
				String codesetid=a_code.substring(0,2);
				if("UN".equalsIgnoreCase(codesetid))
				{
					org_str="b0110";
				}else if("UM".equalsIgnoreCase(codesetid))
				{
					org_str="e0122";
				}else if("@K".equalsIgnoreCase(codesetid))
				{
					org_str="e01a1";
				}else if("EP".equalsIgnoreCase(codesetid))
				{
					org_str="a0100";
				}
				if(a_code.length()>=3)
				{
					String codeitemid=a_code.substring(2);				
					if(codeitemid!=null&&codeitemid.length()>0)
					{
						where=org_str+" like '"+codeitemid+"%'";						
					}
				}				
			}		
			return where;
		}
	    public String getKindValueFrom_acode(String a_code,UserView userView)
	    {
	    	String r_value="";
	    	if(a_code!=null&&a_code.length()>0)
			{
	    		String codesetid=a_code.substring(0,2);
				if("UN".equalsIgnoreCase(codesetid))
				{
					r_value="2";
				}else if("UM".equalsIgnoreCase(codesetid))
				{
					r_value="1";
				}else if("@K".equalsIgnoreCase(codesetid))
				{
					r_value="0";
				}else if("EP".equalsIgnoreCase(codesetid))
				{
					r_value="";
				}
			}else
			{
				r_value=getKindValue("",userView);
			}
	    	return r_value;
	    }
	    /**
	     * 人员考勤数据状态
	     * @param wherestr
	     * @param conn
	     * @return
	     */
	    public String getStateMessage(String wherestr,Connection conn)
	    {
	    	StringBuffer message=new StringBuffer();
	    	message.append("状态提示： ");
	    	//01起草
	    	message.append("起草");
	    	message.append("("+getCountStateNum(wherestr,"01",conn)+")");
	    	message.append("人 ");	    	
	    	//08报审
	    	message.append("报审");
	    	message.append("("+getCountStateNum(wherestr,"08",conn)+")");
	    	message.append("人 ");	
//	    	02报批
	    	message.append("报批");
	    	message.append("("+getCountStateNum(wherestr,"02",conn)+")");
	    	message.append("人 ");
	    	//03批准
	    	message.append("批准");
	    	message.append("("+getCountStateNum(wherestr,"03",conn)+")");
	    	message.append("人 ");	
            //07驳回
	    	message.append("驳回");
	    	message.append("("+getCountStateNum(wherestr,"07",conn)+")");
	    	message.append("人");	
	    	return message.toString();
	    }
	    public String getCountStateNum(String wherestr,String state,Connection conn)
	    {
	    	StringBuffer sql=new StringBuffer();
	    	sql.append("select count(*) a ");
	    	sql.append(" "+wherestr);
	    	sql.append(" and q03z5='"+state+"'");
	    	ContentDAO dao=new ContentDAO (conn);
	    	RowSet rs=null;
	    	String count="";
	    	try
	    	{
	    		rs=dao.search(sql.toString());
	    		if(rs.next())
	    		{
	    			count=rs.getString("a");
	    		}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally
		     {
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		     } 
	    	return count;
	    }
	    public  String selcet_B0110_E0122Id(String nbase,String b0110,String whereIN){
	  	   StringBuffer sqlstr= new StringBuffer();
	  	   sqlstr.append("select distinct e0122 from "+nbase+"A01 ");	  
	  	   sqlstr.append(" where 1=1 and  b0110='"+b0110+"'"); 	    
	  	   sqlstr.append("  and e0122 in(select distinct e0122 "+whereIN+") "); 
	  	   return sqlstr.toString();
	     } 
	    /**
	     * 得到总单位
	     * @param conn
	     * @return
	     */
	    public static String getTopB0110(Connection conn)
	    {
	    	String orgSql="SELECT parentid,codeitemid,codesetid  from organization where codesetid='UN'";
	    	orgSql=orgSql+" and parentid=codeitemid";
	    	ContentDAO dao=new ContentDAO (conn);
	    	RowSet rs=null;
	    	String codeitemid="";
	    	try
	    	{
	    		rs=dao.search(orgSql);
	    		if(rs.next())
	    		{
	    			codeitemid=rs.getString("codeitemid");
	    		}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally
		     {
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		     } 
	    	return codeitemid;
	    }
	    /**
	     * 当点击组织机构是清空Q03，Q05的state全选！
	     * @param dao
	     * @param start_date
	     * @param end_date
	     * @param kq_duration
	     * @param where_In
	     * @return
	     */
	    public boolean cleanState(ContentDAO dao,UserView userView,ArrayList kq_dbase_list,String start_date, String end_date,String kq_duration)
	    {
	    	 
			 boolean isCorrect=true;
			 try
			 {
				 for(int i=0;i<kq_dbase_list.size();i++)
			   	 {
					 String userbase=kq_dbase_list.get(i).toString();
					 String where_In=RegisterInitInfoData.getWhereINSql(userView,userbase);					 
					 String up1="update q03 set state='0' where nbase='"+userbase+"' and state='1' and q03z0>='"+start_date+"' and q03z0<='"+end_date+"' ";
					 String up2="update q05 set state='0' where nbase='"+userbase+"' and state='1' and q03z0='"+kq_duration+"' ";
					 if(!userView.isSuper_admin())
					  {
					    	 if(where_In.indexOf("WHERE")!=-1||where_In.indexOf("where")!=-1)
					    	 { 
					    		 up1=up1+(" and  EXISTS(select a0100 "+where_In+" and "+userbase+"A01.a0100=q03.a0100 and q03z0>='"+start_date+"' and q03z0<='"+end_date+"')");
					    		 up2=up2+(" and  EXISTS(select a0100 "+where_In+" and "+userbase+"A01.a0100=q05.a0100 and q03z0='"+kq_duration+"')");
					    	 }
						     else
						     {
						    	 up1=up1+(" and  EXISTS(select a0100 "+where_In+" where "+userbase+"A01.a0100=q03.a0100 and q03z0>='"+start_date+"' and q03z0<='"+end_date+"')");
					    	     up2=up2+(" and  EXISTS(select a0100 "+where_In+" where "+userbase+"A01.a0100=q05.a0100 and q03z0='"+kq_duration+"')");
						     }
						    	
					  }
					 dao.update(up1);
					 dao.update(up2);
			   	 }
		    	
			 }catch(Exception e)
			 {
				 isCorrect=false;
				 e.printStackTrace();
			 }
			 return isCorrect;
	    }
	    public String getOverruleFormat(String overrule,String sturt,String username)
	    {
	    	 StringBuffer str=new StringBuffer();
	    	 str.append(AdminCode.getCodeName("23", sturt)+"：");
         	 str.append(PubFunc.FormatDate(new Date(),"yyyy.MM.dd HH:mm")+"  ");
         	 str.append(username);	
	         if(overrule!=null&&overrule.length()>0)
		     {
	         	str.append("  意见:"+overrule);
	         }
	         str.append(";");
	         return str.toString();
	    }
	    /**
	     * 得到考勤管理范围编码
	     * @param userView
	     * @return
	     */
	    public static String getKqPrivCode(UserView userView)
	    {
	    	String privCode=userView.getKqManageValue();
	    	if(privCode!=null&&privCode.length()>0) {
                privCode=privCode.substring(0,2);
            } else {
                privCode=userView.getManagePrivCode();
            }
	    	return privCode;
	    }
	    /**
	     * 得到考勤范围编码值
	     * @param userView
	     * @return
	     */
	    public static String getKqPrivCodeValue(UserView userView)
	    {
	    	if(userView.isSuper_admin()) {
                return "";
            }
	    	String privCode=userView.getKqManageValue();
	    	if(privCode!=null&&privCode.length()>0) {
                privCode=privCode.substring(2);
            } else {
                privCode=userView.getManagePrivCodeValue();
            }
	    	return privCode;
	    }
	    public static LazyDynaBean getKqPrivCodeAndKind(UserView userView)
	    {
	    	String code="";
	    	String kind="";
	    	LazyDynaBean bean=new LazyDynaBean();
	    	code=RegisterInitInfoData.getKqPrivCodeValue(userView); 
	    	if("UN".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			{
	    		kind="2";
			}else if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			{
	    		kind="1";
			}else if("@K".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			{
	    		kind="0";
			}else
			{
				kind="2";				
			}	  
	    	bean.set("code", code);
	    	bean.set("kind", kind);
	    	return bean;
	    }
	    
	    public static ArrayList isExistsG_noAndCardno(String endfield,String table,String g_no,String cardno,ArrayList list){
			g_no = g_no.toLowerCase();
			cardno = cardno.toLowerCase();
			FieldItem addfield = new FieldItem();
			for(int i = 0;i < list.size();i++){
				FieldItem field = (FieldItem)list.get(i);
				String itemid = field.getItemid();
				if(endfield != null && endfield.equalsIgnoreCase(itemid)){
					if(list.toString().indexOf(cardno) == -1){
						addfield=new FieldItem();
						addfield.setFieldsetid(table);
						addfield.setItemid(cardno);
						addfield.setItemtype("A");
						addfield.setCodesetid("0");
						addfield.setItemdesc("考勤卡号");
						list.add(i + 1,addfield);
					}
					if(list.toString().indexOf(g_no) == -1){
						addfield=new FieldItem();
						addfield.setFieldsetid(table);
						addfield.setItemid(g_no);
						addfield.setItemtype("A");
						addfield.setCodesetid("0");
						addfield.setItemdesc("工号");
						list.add(i + 1,addfield);
					}
				}
			}
	    	return list;
	    }
	    
	    /**
	     * 判断是否是考勤员角色
	     * @param nbase
	     * @param a0100
	     * @param conn
	     * @return
	     */
	    public static String ischecker(String nbase,String a0100,Connection conn){
	        String ischecker = "0";
	        StringBuffer staff_id = new StringBuffer();
	        StringBuffer sb = new StringBuffer();
	        staff_id.append(nbase);
	        staff_id.append(a0100);
	        sb.append("select * from t_sys_staff_in_role where staff_id = '" + staff_id.toString() + "' and role_id = '00000007'" );
	        ContentDAO dao = new ContentDAO(conn);
	        RowSet rs = null;
	        try
	        {
	            rs = dao.search(sb.toString());
	            while(rs.next()){
	                ischecker = "1";
	            }
	        }
	        catch (SQLException e1)
	        {
	            e1.printStackTrace();
	        }finally{
	            if(rs!=null){
	                try
	                {
	                    rs.close();
	                }
	                catch (SQLException e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return ischecker;
	    }
}