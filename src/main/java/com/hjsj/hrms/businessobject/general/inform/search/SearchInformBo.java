package com.hjsj.hrms.businessobject.general.inform.search;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SearchInformBo {
	private Connection conn; //数据库链接
	private UserView userView;//用户信息
	private String a_code; //公司职务代码
	private String dbpre; //库前缀
	private ArrayList dbprelist;
	public SearchInformBo(Connection conn,UserView userView,String a_code,String dbpre){
		this.conn=conn;
		this.userView=userView;
		if("all".equalsIgnoreCase(a_code)){
			if(!userView.isSuper_admin()){
				this.a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
			}else {
                this.a_code=a_code;
            }
		}else {
            this.a_code=a_code;
        }
		this.dbpre=dbpre;
	}
	public SearchInformBo(Connection conn,UserView userView,String a_code,ArrayList dbprelist){
		this.conn=conn;
		this.userView=userView;
		if("all".equalsIgnoreCase(a_code)){
			if(!userView.isSuper_admin()){
				this.a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
			}else {
                this.a_code=a_code;
            }
		}else {
            this.a_code=a_code;
        }
		this.dbprelist=dbprelist;
	}
    /**
     * 查询单位信息，区分查部门＼查单位＼都查
     * @param qobj
     * @param strWhere
     * @return
     */
    public String getQueryObjWhere(){
    	String strfilter="";
    	if(!"all".equalsIgnoreCase(a_code)){
    		String codesetid = a_code!=null&&a_code.trim().length()>1?a_code.substring(0,2):"";
    		strfilter=" and B01.B0110 in (select codeitemid from organization where codesetid='"+codesetid+"')";
    	}
    	return strfilter;
    }
    /**
     * 求单位过滤条件，根据管理范围
     * @param flag=2 单位，=3职位
     * @return
     */
    private String getUnitPosFilterCond(int flag)
    {
    	if(userView.isSuper_admin()) {
            return "";
        }

  	  /**
  	   * 由先前的按人员管理范围控制改成按如规则进行控制
  	   * 单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
  	   * cmq changed at 2012-09-29
  	   */    	
    	/*
    	StringBuffer strcond=new StringBuffer(); 
    	String codeid=userView.getManagePrivCode();
    	String codevalue=userView.getManagePrivCodeValue();
    	
    	if(codeid==null||codeid.equals(""))
    		return "";
    	if(flag==2)
    		strcond.append(" B01.B0110 like ");
    	else
    		strcond.append(" K01.E01A1 like ");
    	strcond.append("'");
    	strcond.append(codevalue);
    	strcond.append("%");
    	strcond.append("'");
    	*/
    	String codevalue="";
    	codevalue=userView.getUnitIdByBusi("4");
	    String[] valuearr=StringUtils.split(codevalue,"`");
	    if(valuearr.length==0) {
            return "";
        }
	    StringBuffer value=new StringBuffer();
	    value.append("(");
	    for(int i=0;i<valuearr.length;i++)
	    {
		  if(i!=0) {
              value.append(" or ");
          }
	      if(flag==2) {
              value.append(" B01.B0110 like '");
          } else {
              value.append(" K01.E01A1 like '");
          }
		  value.append(valuearr[i].substring(2));
		  value.append("%'");
	    }
	    value.append(")");

    	return value.toString();
    } 
    
    public boolean saveSelfServiceQueryResult(String sexpr,String sfactor,String query_type,String history,String like,
            String result,String type,String unite,int queryType) {
        
        return saveSelfServiceQueryResult(sexpr, sfactor, query_type, history, like, result, type, unite, queryType, false);
    }
    
    public boolean saveSelfServiceQueryResult(String sexpr,String sfactor,String query_type,String history,String like,
    		String result,String type,String unite,int queryType, boolean no_manager_priv)
    {
    	boolean flag=true;
    	try
    	{
    		String tabldName = "t_sys_result";
			Table table = new Table(tabldName);
			DbWizard dbWizard = new DbWizard(conn);
			if (!dbWizard.isExistTable(table)) {
				return false;
			}
			UsrResultTable resulttable = new UsrResultTable();
			/**自助用户，统一一个结果表，数字开头的用户名也没事*/
			//if(resulttable.isNumber(this.userView.getUserName())){
				//throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.search")+"!"));
			//}
    		int resultFlag=0;
    		if("2".equals(type)) {
                resultFlag=1;
            }
    		if("3".equals(type)) {
                resultFlag=2;
            } else if("9".equals(type)) // 基准岗位
            {
                resultFlag=5;
            }
    		 sfactor=sfactor.replaceAll("\\$THISMONTH\\[\\]","当月");
    		 /**查询全部库*/
    		if(queryType==1)
    		{
    			for(int i=0;i<dbprelist.size();i++)
    			{
    				String dbtableAll=dbprelist.get(i).toString();
    				String pre = dbtableAll;
    				boolean blike=false;
    		    	if("1".equals(like)) {
                        blike=true;
                    }
    		        boolean bresult=true;
    		    	if("1".equals(result)) {
                        bresult=false;
                    }
    		    	boolean bhis=false;
    		        if("1".equals(history)) {
                        bhis=true;
                    }
    		        String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
    		        StringBuffer strwhere=new StringBuffer("");
    		        ArrayList fieldlist=new ArrayList();
    		        if((!userView.isSuper_admin())&& "1".equals(type)){
    	        		strwhere.append(userView.getPrivSQLExpression(sexpr+"|"+sfactor,dbtableAll,bhis,blike,bresult,fieldlist)) ;
    	        		
    	        	}else{
    	        		FactorList factorslist=new FactorList(sexpr,sfactor,dbtableAll,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
    	        		fieldlist=factorslist.getFieldList();
    	        		
    	        		strwhere.append(factorslist.getSqlExpression());
    	        	}
    		        /**在K01中加入B0110指标，查询时，将sql替换*/
    	        	if("3".equals(type))
    	        	{
    	        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常 start
    	        		//String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01", "K01");
    	        		String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01 ", "K01 ").replaceAll("A01\\.", "K01\\.");
    	        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常  end
    	        		strwhere.setLength(0);
    	        		strwhere.append(str);
    	        	}
    		        /**对单位还得加上管理范围*/
    	        	if("2".equals(type)|| "3".equals(type)){
    	        		String filtercond=getUnitPosFilterCond(Integer.parseInt(type));
    	        		if(filtercond.length()!=0) {
                            strwhere.append(" and "+filtercond);
                        }
    	        		if("2".equals(type)){
    	        			if("0".equals(unite)){
    	        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UN'");
    	        				strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
        	        			strwhere.append(")");
    	        			}else if("1".equals(unite)){
    	        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UM'");
    	        				strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
        	        			strwhere.append(")");
    	        			}else if("2".equals(unite))
    	        			{
    	        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where ");
    	        				strwhere.append(Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
        	        			strwhere.append(")");
    	        			}
    	        			
    	        		}
    	        		if("3".equals(type))
    	        		{
    	        			strwhere.append(" and K01.E01A1 in (select codeitemid from organization where codesetid='@K' ");
    	        			strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
    	        			strwhere.append(")");
    	        		}
    	        	}
    				String str = "delete from " + tabldName+" where flag="+resultFlag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
        			if("1".equals(type))
        			{
        				str+=" and UPPER(nbase)='"+dbtableAll.toUpperCase()+"'";
        			}
        			ContentDAO dao = new ContentDAO(conn);
        			dao.delete(str, new ArrayList());
        			StringBuffer buf_sql = new StringBuffer("");
        			if ("1".equals(type)) {//liuy 2015-4-7 8351：哈药集团：机构花名册插入取值方法，前台无法取数
        				buf_sql.append("insert into " + tabldName);
        				buf_sql.append("(username,nbase,obj_id,flag) ");
        				buf_sql.append("select DISTINCT '"+userView.getUserName()+"' as username,'"+dbtableAll.toUpperCase()+"' as nbase,"+dbtableAll+"A01.A0100 as obj_id, 0 as flag");
        				buf_sql.append(strwhere);
        			} else if ("2".equals(type)) {//liuy 2015-4-7 8351：哈药集团：机构花名册插入取值方法，前台无法取数
        				buf_sql.append("insert into " + tabldName + " (username,obj_id,flag,nbase) ");
        				buf_sql.append("select DISTINCT '"+userView.getUserName()+"' as username,B01.b0110 as obj_id,1 as flag,'B' "+strwhere);
        			} else if ("3".equals(type)) {//liuy 2015-4-7 8351：哈药集团：机构花名册插入取值方法，前台无法取数
        				buf_sql.append("insert into " + tabldName+ " (username,obj_id,flag,nbase)");
        				buf_sql.append("select DISTINCT '"+userView.getUserName()+"' as username,K01.e01a1 as obj_id,2 as flag,'K' "+strwhere);
        			} else if ("9".equals(type)) {  // 基准岗位//liuy 2015-4-7 8351：哈药集团：机构花名册插入取值方法，前台无法取数
                        buf_sql.append("insert into " + tabldName+ " (username,obj_id,flag,nbase)");
                        buf_sql.append("select DISTINCT '"+userView.getUserName()+"' as username,H01.H0100 as obj_id,"+resultFlag+" as flag,'H' "+strwhere);
                    }
        			dao.insert(buf_sql.toString(), new ArrayList());
        			if("9".equals(type)) {
                        break;  // 基准岗位执行一次即可
                    }
    			}
    		}
    		/**查询指定的单个库*/
    		else if(queryType==2)
    		{
    			String whereSQL=strWhere(sexpr,sfactor,query_type,history,like,result,type,unite, no_manager_priv);
    			String str = "delete from " + tabldName+" where flag="+resultFlag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
    			if("1".equals(type))
    			{
    				str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";
    			}
    			ContentDAO dao = new ContentDAO(conn);
    			dao.delete(str, new ArrayList());
    			StringBuffer buf_sql = new StringBuffer("");
    			if ("1".equals(type)) {//liuy 2015-6-24 10116：[审协北京中心]自助账号设置花名册时查询问题
    				buf_sql.append("insert into " + tabldName);
    				buf_sql.append("(username,nbase,obj_id,flag) ");
    				buf_sql.append("select distinct '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,"+dbpre+"A01.A0100 as obj_id, 0 as flag");
    				buf_sql.append(whereSQL);
    			} else if ("2".equals(type)) {//liuy 2015-6-24 10116：[审协北京中心]自助账号设置花名册时查询问题
    				buf_sql.append("insert into " + tabldName + " (username,obj_id,flag,nbase) ");
    				buf_sql.append("select distinct '"+userView.getUserName()+"' as username,B01.b0110 as obj_id,1 as flag,'B' "+whereSQL);
    			} else if ("3".equals(type)) {//liuy 2015-6-24 10116：[审协北京中心]自助账号设置花名册时查询问题
    				buf_sql.append("insert into " + tabldName+ " (username,obj_id,flag,nbase)");
    				buf_sql.append("select distinct '"+userView.getUserName()+"' as username,K01.e01a1 as obj_id,2 as flag,'K' "+whereSQL);
    			} else if ("9".equals(type)) {  // 基准岗位//liuy 2015-6-24 10116：[审协北京中心]自助账号设置花名册时查询问题
    				buf_sql.append("insert into " + tabldName+ " (username,obj_id,flag,nbase)");
    				buf_sql.append("select distinct '"+userView.getUserName()+"' as username,H01.H0100 as obj_id,"+resultFlag+" as flag,'H' "+whereSQL);
    			}
    			dao.insert(buf_sql.toString(), new ArrayList());
    		}
    	}
    	catch(Exception e)
    	{
    		flag=false;
    		e.printStackTrace();
    	}
    	return flag;
    }
    /**
	 * 保存查询结果
	 * @param type
	 * @param dbpre
	 */
	public boolean saveQueryResult(String type,String sql)throws GeneralException{
		boolean check=false;
//		if(this.userView.getStatus()==4)
//			return false;
		String pre = dbpre;
		if("2".equals(type)) {
            pre="b";
        }
		if("3".equals(type)) {
            pre="k";
        }
		String tablename=this.userView.getUserName()+pre+"result";
		StringBuffer inssql=new StringBuffer();
		ContentDAO dao=new ContentDAO(conn);
		try{
			UsrResultTable resulttable = new UsrResultTable();
			if(resulttable.isNumber(this.userView.getUserName())){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.search")+"!"));
			}
			inssql.append("insert into ");
			inssql.append(tablename);
			inssql.append("(");
			if("2".equals(type)){
				inssql.append("B0110)");
				inssql.append(" select ");
				inssql.append("B01.B0110 ");
				inssql.append(sql);
			}else if("3".equals(type)){
				inssql.append("E01A1) ");
				inssql.append(" select ");
				inssql.append("K01.E01A1 ");
				inssql.append(sql);				
			}else{
				String dbname = dbpre+"A01";
				inssql.append("A0100)");
				inssql.append(" select distinct ");
				inssql.append(dbname+".A0100 ");
				inssql.append(sql);				
			}
			Table table = new Table(tablename);	
			if("1".equals(type)){
				FieldItem a0100item = DataDictionary.getFieldItem("A0100");
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				table.addField(a0100item);
				table.addField(b0110item);
			}else if("2".equals(type)){
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				table.addField(b0110item);
			}else if("3".equals(type)){
				FieldItem e01a1item = DataDictionary.getFieldItem("E01A1");
				table.addField(e01a1item);
			}else{
				FieldItem a0100item = DataDictionary.getFieldItem("A0100");
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				table.addField(a0100item);
				table.addField(b0110item);
			}
			DbWizard dbWizard = new DbWizard(this.conn);
			try{
				if(dbWizard.isExistTable(tablename,false)) {
                    dbWizard.dropTable(tablename);
                }
			}catch(Exception e){
			}
			dbWizard.createTable(table);
			dao.update(inssql.toString());
			check = true;
		}catch(Exception ex){
			ex.printStackTrace();
			check = false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return check;
	}
	
	 /**
     * 组成sql语句
     * @param sexpr  因子表达式
     * @param sfactor 条件
     * @param query_type 区别查询类型,[1.简单查询  2.通用查询]
     * @param history 是否为历史记录查询 [1.是  0.否]
     * @param like  是否为模糊查询 [1.是  0.否]
     * @param result  是否为二次结果查询 [1.是  0.否]
     * @param type  区别查询类型 [1.人员查询  2.单位查询  3.职位查询]
     * @return strwhere
	 * @throws GeneralException 
     */
	public String strWhere(String sexpr,String sfactor,String query_type,
			String history,String like,String result,String type) throws GeneralException{
		StringBuffer strwhere = new StringBuffer();
		boolean blike=false;
    	if("1".equals(like)) {
            blike=true;
        }
        boolean bresult=true;
    	if("1".equals(result)) {
            bresult=false;
        }
    	boolean bhis=false;
        if("1".equals(history)) {
            bhis=true;
        }
        ArrayList fieldlist=new ArrayList();
        try{
        	if((!userView.isSuper_admin())){
        		if("1".equals(type)) {
                    strwhere.append(userView.getPrivSQLExpression(sexpr+"|"+sfactor,dbpre,bhis,blike,bresult,fieldlist)) ;
                } else{
        			FactorList factorslist=new FactorList(sexpr,sfactor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
            		fieldlist=factorslist.getFieldList();
            		
            		strwhere.append(factorslist.getSqlExpression());
        		}
        	}else{
        		FactorList factorslist=new FactorList(sexpr,sfactor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
        		fieldlist=factorslist.getFieldList();
        		
        		strwhere.append(factorslist.getSqlExpression());
        	}
        	/**在K01中加入B0110指标，查询时，将sql替换*/
        	if("3".equals(type))
        	{
        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常 start
        		//String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01", "K01");
        		String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01 ", "K01 ").replaceAll("A01\\.", "K01\\.");
        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常  end
        		strwhere.setLength(0);
        		strwhere.append(str);
        	}
        	if("2".equals(type)){
        		if(strwhere.length()>0) {
                    strwhere.append(getQueryObjWhere());
                }
        	}
        	/**对单位还得加上管理范围*/
        	if("2".equals(type)|| "3".equals(type)){
        		String filtercond=getUnitPosFilterCond(Integer.parseInt(type));
        		if(filtercond.length()!=0) {
                    strwhere.append(" and "+filtercond);
                }
        	} 
        } catch (GeneralException e) {
			// TODO Auto-generated catch block
        	throw GeneralExceptionHandler.Handle(e);
		}
		
		return strwhere.toString();
	}
	 /**
     * 组成sql语句
     * @param sexpr  因子表达式
     * @param sfactor 条件
     * @param query_type 区别查询类型,[1.简单查询  2.通用查询]
     * @param history 是否为历史记录查询 [1.是  0.否]
     * @param like  是否为模糊查询 [1.是  0.否]
     * @param result  是否为二次结果查询 [1.是  0.否]
     * @param type  区别查询类型 [1.人员查询  2.单位查询  3.职位查询]
     * @param unit  查询范围 [0.查单位  1.查部门  2.全部]
     * @return strwhere
	 * @throws GeneralException 
     */
	public String strWhere(String sexpr,String sfactor,String query_type,
			String history,String like,String result,String type,String unite) throws GeneralException{
		String strwhere = "";
		strwhere =strWhere(sexpr, sfactor, query_type, history, like, result, type, unite, false);
		return strwhere;
	}
	
	/**
     * 组成sql语句
     * @param sexpr  因子表达式
     * @param sfactor 条件
     * @param query_type 区别查询类型,[1.简单查询  2.通用查询]
     * @param history 是否为历史记录查询 [1.是  0.否]
     * @param like  是否为模糊查询 [1.是  0.否]
     * @param result  是否为二次结果查询 [1.是  0.否]
     * @param type  区别查询类型 [1.人员查询  2.单位查询  3.职位查询]
     * @param unit  查询范围 [0.查单位  1.查部门  2.全部]
     * @param no_manger_priv 不按管理范围
     * @return strwhere
	 * @throws GeneralException 
     */
	public String strWhere(String sexpr,String sfactor,String query_type,
			String history,String like,String result,String type,String unite,boolean no_manager_priv) throws GeneralException{
		StringBuffer strwhere = new StringBuffer();
		boolean blike=false;
		//liuy 2015-4-15 8763：花名册应该只能看见19个人，可是简单查询，杨模糊查询，能查出100多条（模糊授权未按高级授权走） begin
    	//if(like.equals("1"))
    		//blike=true;
		//liuy 2015-4-15 end
        boolean bresult=true;
    	if("1".equals(result)) {
            bresult=false;
        }
    	boolean bhis=false;
        if("1".equals(history)) {
            bhis=true;
        }
        String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
        ArrayList fieldlist=new ArrayList();
        try{
        	sfactor=sfactor.replaceAll("\\$THISMONTH\\[\\]","当月");
    		if((!userView.isSuper_admin())&& "1".equals(type)&&!no_manager_priv){//不按管理范围不走权限判断
    			strwhere.append(userView.getPrivSQLExpression(sexpr+"|"+sfactor,dbpre,bhis,blike,bresult,fieldlist)) ;
    		}else{
    			FactorList factorslist=new FactorList(sexpr,sfactor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
    			fieldlist=factorslist.getFieldList();
    			strwhere.append(factorslist.getSqlExpression());
    		}
        	/**在K01中加入B0110指标，查询时，将sql替换*/
        	if("3".equals(type))
        	{
        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常 start
        		//String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01", "K01");
        		String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01 ", "K01 ").replaceAll("A01\\.", "K01\\.");
        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常  end
        		strwhere.setLength(0);
        		strwhere.append(str);
        	}
        	/**对单位还得加上管理范围*/
        	if("2".equals(type)|| "3".equals(type)){
        		String backdate=DateUtils.format(new Date(), "yyyy-MM-dd");
        		String filtercond=getUnitPosFilterCond(Integer.parseInt(type));
        		if(filtercond.length()!=0) {
                    strwhere.append(" and "+filtercond);
                }
        		if("2".equals(type)){
        			if("0".equals(unite)){

        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UN' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)");

        			}else if("1".equals(unite)){

        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UM'");
        				strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
            			strwhere.append(")");
        			}else if("2".equals(unite))
        			{
        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where ");
        				strwhere.append(Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
            			strwhere.append(")");

        			}

        		}else if("3".equals(type))
        		{
        			strwhere.append(" and K01.E01A1 in (select codeitemid from organization where codesetid='@K' ");
        			strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
        			strwhere.append(")");

        		}
        	} 
        } catch (GeneralException e) {
			// TODO Auto-generated catch block
        	throw GeneralExceptionHandler.Handle(e);
		}
		
		return strwhere.toString();
	}
	/**
	 *查询全部人员库 wangyao
	 * @param type
	 * @return
	 * @throws GeneralException
	 */
	public boolean saveQueryResultAll(String sexpr,String sfactor,String query_type,
			String history,String like,String result,String typea,String unite,String type)throws GeneralException{
		boolean check=false;
		ContentDAO dao=new ContentDAO(conn);
		try{
			for(int i=0;i<dbprelist.size();i++)
			{
				StringBuffer inssql=new StringBuffer();
				StringBuffer buf=new StringBuffer();
				StringBuffer strwhere=new StringBuffer();
				String dbtableAll=dbprelist.get(i).toString();
				String pre = dbtableAll;
				if("2".equals(type)) {
                    pre="b";
                }
				if("3".equals(type)) {
                    pre="k";
                }
				String tablename=this.userView.getUserName()+pre+"result";
				boolean blike=false;
		    	if("1".equals(like)) {
                    blike=true;
                }
		        boolean bresult=true;
		    	if("1".equals(result)) {
                    bresult=false;
                }
		    	boolean bhis=false;
		        if("1".equals(history)) {
                    bhis=true;
                }
		        String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		        ArrayList fieldlist=new ArrayList();
		        if((!userView.isSuper_admin())&& "1".equals(typea)){
	        		strwhere.append(userView.getPrivSQLExpression(sexpr+"|"+PubFunc.getStr(sfactor),dbtableAll,bhis,blike,bresult,fieldlist)) ;
	        		
	        	}else{
	        		FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),dbtableAll,bhis ,blike,bresult,Integer.parseInt(typea),userView.getUserId());
	        		fieldlist=factorslist.getFieldList();
	        		strwhere.append(factorslist.getSqlExpression());
	        	}
		        /**在K01中加入B0110指标，查询时，将sql替换*/
	        	if("3".equals(type))
	        	{
	        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常 start
	        		//String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01", "K01");
	        		String str=strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01 ", "K01 ").replaceAll("A01\\.", "K01\\.");
	        		//liuy 2015-8-7 11709：华东医药股份有限公司： 华东医药股份本部下所有部门岗位信息取不出来，其他单位下取数正常  end
	        		strwhere.setLength(0);
	        		strwhere.append(str);
	        	}
		        /**对单位还得加上管理范围*/
	        	if("2".equals(typea)|| "3".equals(typea)){
	        		String filtercond=getUnitPosFilterCond(Integer.parseInt(typea));
	        		if(filtercond.length()!=0) {
                        strwhere.append(" and "+filtercond);
                    }
	        		if("2".equals(typea)){
	        			if("0".equals(unite)){
	        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UN'");
	        				strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		        			strwhere.append(")");
	        			}else if("1".equals(unite)){
	        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UM'");
	        				strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		        			strwhere.append(")");
	        			}else if("2".equals(unite))
	        			{
	        				strwhere.append(" and B01.B0110 in(select codeitemid from organization where ");
	        				strwhere.append(Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		        			strwhere.append(")");
	        			}
	        			
	        		}
	        		if("3".equals(typea))
	        		{
	        			strwhere.append(" and K01.e01a1 in (select codeitemid from organization where codesetid='@K' ");
	        			strwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	        			strwhere.append(")");
	        		}
	        	}
	        	UsrResultTable resulttable = new UsrResultTable();
				if(resulttable.isNumber(this.userView.getUserName())){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.search")+"!"));
				}
				inssql.append("insert into ");
				inssql.append(tablename);
				inssql.append("(");
				if("2".equals(type)){
					inssql.append("B0110)");
					inssql.append(" select ");
					inssql.append("B01.B0110 ");
					inssql.append(strwhere.toString());
				}else if("3".equals(type)){
					inssql.append("E01A1) ");
					inssql.append(" select ");
					inssql.append("K01.E01A1 ");
					inssql.append(strwhere.toString());				
				}else{
					String dbname = dbtableAll+"A01";
					inssql.append("A0100)");
					inssql.append(" select distinct ");
					inssql.append(dbname+".A0100 ");
					inssql.append(strwhere.toString());				
				}
				Table table = new Table(tablename);	
				if("1".equals(type)){
					FieldItem a0100item = DataDictionary.getFieldItem("A0100");
					FieldItem b0110item = DataDictionary.getFieldItem("B0110");
					table.addField(a0100item);
					table.addField(b0110item);
				}else if("2".equals(type)){
					FieldItem b0110item = DataDictionary.getFieldItem("B0110");
					table.addField(b0110item);
				}else if("3".equals(type)){
					FieldItem e01a1item = DataDictionary.getFieldItem("E01A1");
					table.addField(e01a1item);
				}else{
					FieldItem a0100item = DataDictionary.getFieldItem("A0100");
					FieldItem b0110item = DataDictionary.getFieldItem("B0110");
					table.addField(a0100item);
					table.addField(b0110item);
				}
				DbWizard dbWizard = new DbWizard(this.conn);
				try{
					if(dbWizard.isExistTable(tablename,false)) {
                        dbWizard.dropTable(tablename);
                    }
				}catch(Exception e){
				}
				dbWizard.createTable(table);
				dao.update(inssql.toString());
				check = true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			check = false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return check;
	}
}
