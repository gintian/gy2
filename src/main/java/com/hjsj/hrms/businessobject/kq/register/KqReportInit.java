package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KqReportInit {
	private Connection conn=null;
	private static String colums = "";
	//是归档数据
	private boolean isArcData = false;
	private String curTab = "";
	
	private KqReportInit()
	{
		
	}
	public KqReportInit(Connection conn)
	{
		this.conn=conn;
	}
	
	public void checkArcData(String orgTab, String kqDuration) {
	    DbWizard dbWizard = new DbWizard(this.conn);
	    KqDBHelper kqDBHelper = new KqDBHelper(this.conn);
	    String arcTab = orgTab + "_arc";
	    boolean isArc = false;
	    if (dbWizard.isExistTable(arcTab, false)) {
	        if ("q05".equalsIgnoreCase(orgTab)) {
	            isArc = !kqDBHelper.isRecordExist(orgTab, "Q03Z0='" + kqDuration + "'"); 
	        } else if("q03".equalsIgnoreCase(orgTab)) {
	            ArrayList dates = null;
                try {
                    dates = RegisterDate.getOneDurationDate(this.conn, kqDuration);
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
	            if (dates != null && dates.size() == 2) {
                    isArc = !kqDBHelper.isRecordExist(orgTab, "Q03Z0='" + dates.get(0).toString() + "'");
                }
	        }
	    }
	    this.setArcData(isArc);
	    if (isArc) {
            this.curTab = arcTab;
        } else {
            this.curTab = orgTab;
        }
	}
	
	/**
	 * 通过report_id，查询对应的xml文件内容，得到ReportParseVo
	 * */
    public ReportParseVo getParseVo(String report_id)
    {
    	String kq_xpath="/kq_reports/kq_report";
		String report_sql="Select content from kq_report where report_id='"+report_id+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		ReportParseVo parsevo = new ReportParseVo ();
		try
	    {
			rowSet=dao.search(report_sql);
	    	if(rowSet.next())
	    	{	    		
	    		String content=rowSet.getString("content");
	    		if(content!=null&&content.length()>0)
	    		{
	    			ReportParseXml parseXml = new ReportParseXml();	    			
	    		    parsevo=parseXml.ReadOutParseXml(content,kq_xpath);	    			
	    		}	    		
	    	}else{
	    		//抛异常，没有定义的xml参数
	    		
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
	    return parsevo;
    }
    /**
	 * 得到考勤项目表中的设置
	 * 
	 * */
	public  ArrayList  getKq_Item_list()throws GeneralException
	{
		ArrayList fielditemlist = DataDictionary.getFieldList("q03",Constant.USED_FIELD_SET);		
		ArrayList kq_item_list = new ArrayList();   	
    	
    	for(int i=0;i<fielditemlist.size();i++){
   	      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	      /*if("N".equals(fielditem.getItemtype()))
   	      { */ 		   
   		     String fielditemid=fielditem.getItemid();
   		     String sql="SELECT item_symbol,item_color from kq_item where upper(fielditemid)='"+fielditemid.toUpperCase()+"'";
   		     
   		     ContentDAO dao=new ContentDAO(this.conn);
   		     RowSet rowSet=null;
   		     try{
   		    	rowSet=dao.search(sql);
   		    	if(rowSet.next())
   		    	{
//   		    		HashMap hash=new HashMap();
//   		    		hash.put("fielditemid",fielditemid);//考勤对应字段名
//   		    		hash.put("item_symbol",rowSet.getString("item_symbol"));//考勤对应的符号
//   		    		hash.put("item_color",getColor(rowSet.getString("item_color")));//颜色
//   		    		kq_item_list.add(hash);
   		    		
   		    		if(rowSet.getString("item_symbol")!=null)
   		    		{
   		    			HashMap hash=new HashMap();
   	   		    		hash.put("fielditemid",fielditemid);//考勤对应字段名
   	   		    		hash.put("item_symbol",rowSet.getString("item_symbol"));//考勤对应的符号
   	   		    		hash.put("item_color",getColor(rowSet.getString("item_color")));//颜色
   	   		    		kq_item_list.add(hash);
   		    		}
   		    	}	
   		    }catch(Exception e){
   		    	 e.printStackTrace();
   		    	throw GeneralExceptionHandler.Handle(e);
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
   	    	  
   		  //}				
   	   }
       return kq_item_list;    	
	}
	public  ArrayList  getKq_Item_listPdf()throws GeneralException
	{
		ArrayList fielditemlist = DataDictionary.getFieldList("q03",Constant.USED_FIELD_SET);		
		ArrayList kq_item_list = new ArrayList();   	
    	
    	for(int i=0;i<fielditemlist.size();i++){
   	      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	      /*if("N".equals(fielditem.getItemtype()))
   	      { */ 		   
   		     String fielditemid=fielditem.getItemid();
   		     String sql="SELECT item_symbol,item_color from kq_item where upper(fielditemid)='"+fielditemid.toUpperCase()+"'";
   		     
   		     ContentDAO dao=new ContentDAO(this.conn);
   		     RowSet rowSet=null;
   		     try{
   		    	
   		    	rowSet=dao.search(sql);
   		    	if(rowSet.next())
   		    	{
   		    		HashMap hash=new HashMap();
   		    		hash.put("fielditemid",fielditemid);//考勤对应字段名
   		    		hash.put("item_symbol",rowSet.getString("item_symbol"));//考勤对应的符号
   		    		hash.put("item_color",rowSet.getString("item_color"));//颜色
   		    		kq_item_list.add(hash); 
   		    	}	
   		    }catch(Exception e){
   		    	 e.printStackTrace();
   		    	throw GeneralExceptionHandler.Handle(e);
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
   	    	  
   		 // }				
   	   }
       return kq_item_list;    	
	}
	/**
     * 通过数据判断是否出勤
     * */
    public static String[] getKq_Item(String fielditemid,ArrayList item_list)
    {
    	//System.out.println("item_list="+item_list.size());
    	//System.out.println("fielditemid="+fielditemid);
    	String[] kq_item = new String[2];
    	for(int i=0;i<item_list.size();i++)
    	{
    		HashMap hash=(HashMap)item_list.get(i);    		
    		if(fielditemid.equalsIgnoreCase(hash.get("fielditemid").toString().trim()))
    		{
    			//System.out.println(hash.get("item_symbol").toString());
    			//item_symbol考勤规则里符号里可以改为空；为空的时候不导出符号
    			if(hash.get("item_symbol")!=null && hash.get("item_symbol").toString().trim().length() > 0)
    			{
    				kq_item[0]=hash.get("item_symbol").toString();
        			kq_item[1]=hash.get("item_color").toString();
        			break;
    			}
    		}
    	}
    	return kq_item;
    }
  
	/**
	 * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v,int scale){

        if(scale<0)
        {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
            }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
    }
    
    /**
     * 得到权限所有人员的库中人员人数的sql
     * 
     * **/
    public static String count_kq_a0100(String tab, String code,String kind,String start_date,String end_date,ArrayList whereINList,String whereIN)
    {
 	   StringBuffer sqlstr= new StringBuffer();
 	   sqlstr.append("select count(distinct a0100) a ");	
 	   if(whereIN!=null&&whereIN.length()>0)
 	   {
 		   sqlstr.append(" "+whereIN);
  	       if("1".equals(kind))
		   {
	    	  sqlstr.append(" and e0122 like '"+code+"%'");
		   }
  	       else if("-2".equals(kind))
		   {
			  sqlstr.append(" and 1=2");	
		   } 
		   else if("0".equals(kind))
		   {
			  sqlstr.append(" and e01a1 like '"+code+"%'");
		   }
		   else
		   {
			  sqlstr.append(" and b0110 like '"+code+"%'");	
		   } 
  	       //zxj 20150730 补充日期条件
  	       sqlstr.append(" and Q03Z0 >= '"+start_date+"'");
	 	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
 	   }else
 	   {
 		   sqlstr.append(" from Q03 where Q03Z0 >= '"+start_date+"'");
 	 	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
 	 	   
 	 	   if ("-2".equals(kind)) {
 	 	       sqlstr.append(" and 1=2");
 	 	   } else if (StringUtils.isNotEmpty(code)) {
 	 	       if ("1".equals(kind)) {
 	 	           sqlstr.append(" and  e0122 like '" + code + "%'");
 	 	       } else  if ("0".equals(kind)) {
 	 	           sqlstr.append(" and e01a1 like '" + code + "%'");
 	 	       } else {
 	 	           sqlstr.append(" and b0110 like '" + code + "%'");
 	 	       }
 	 	   }
 	 	   
 	 	   for(int i=0;i<whereINList.size();i++)
 	 	   {   
 	 		   if(i>0)
 	 		   {
 	 			   sqlstr.append(" or ");  
 	 		   }else
 	 		   {
 	 			   sqlstr.append(" and ( ");    
 	 		   }
 	 		   sqlstr.append("  a0100 in(select distinct a0100 "+whereINList.get(i).toString()+") "); 
 	 		   if(i==whereINList.size()-1) {
                   sqlstr.append(")");
               }
 	 	   }
 	 	   sqlstr.append(" group by nbase");
 	   } 	   
 	   //sqlstr.append(" and Q03Z5 not in ('01','07','08')");
 	   return sqlstr.toString();
    }
    public static String count_kq_a0100(String tab, String code,String kind,String start_date,String end_date,ArrayList whereINList,String whereIN,String dbtype)
    {
 	   StringBuffer sqlstr= new StringBuffer();
 	   sqlstr.append("select count(distinct a0100) a ");	
 	   if(whereIN!=null&&whereIN.length()>0)
 	   {
 		  sqlstr.append(" "+whereIN);
 		  if ("-2".equals(kind)) {
              sqlstr.append(" and 1=2");
          } else if (StringUtils.isNotEmpty(code)) {
              if ("1".equals(kind)) {
                  sqlstr.append(" and  e0122 like '" + code + "%'");
              } else  if ("0".equals(kind)) {
                  sqlstr.append(" and e01a1 like '" + code + "%'");
              } else {
                  sqlstr.append(" and b0110 like '" + code + "%'");
              }
          }
  	       //zxj 20150730 补充日期条件
  	       sqlstr.append(" and Q03Z0 >= '"+start_date+"'");
	 	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
 	   }else
 	   {
 	       sqlstr.append(" from ").append(tab);
           sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
 	 	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
 	       if ("-2".equals(kind)) {
 	           sqlstr.append(" and 1=2");
 	       } else if (StringUtils.isNotEmpty(code)) {
 	           if ("1".equals(kind)) {
 	               sqlstr.append(" and  e0122 like '" + code + "%'");
 	           } else  if ("0".equals(kind)) {
 	               sqlstr.append(" and e01a1 like '" + code + "%'");
 	           } else {
 	               sqlstr.append(" and b0110 like '" + code + "%'");
 	           }
 	       }
 	 	   for(int i=0;i<whereINList.size();i++)
 	 	   {   
 	 		   if(i>0)
 	 		   {
 	 			   sqlstr.append(" or ");  
 	 		   }else
 	 		   {
 	 			   sqlstr.append(" and ( ");    
 	 		   }
 	 		   
 	 		   if(StringUtils.isNotEmpty(dbtype)) {
                   sqlstr.append(" nbase='"+dbtype+"' and ");
               }
             
               sqlstr.append("a0100 in(select distinct a0100 "+whereINList.get(i).toString()+") "); 
 	 		   if(i==whereINList.size()-1) {
                   sqlstr.append(")");
               }
 	 	   }
// 	 	   sqlstr.append(" group by nbase");
 	   } 	   
 	   //sqlstr.append(" and Q03Z5 not in ('01','07','08')");
 	   return sqlstr.toString();
    }
    /**
     * 得到权限所有人员的库中人员的sql
     *  
     * **/
    public static String select_kq_a0100(String code,String kind,String start_date,String end_date,ArrayList whereINList){
 	   StringBuffer sqlstr= new StringBuffer();
 	   sqlstr.append("select distinct a0100,a0101,q03z0,nbase  from Q03");	  
 	   sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
 	   sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
 	   if (StringUtils.isNotEmpty(code)) {
     	   if("1".equals(kind))
    		{
    	    	sqlstr.append(" and e0122 like '"+code+"%'");
    		}else{
    			sqlstr.append(" and b0110 like '"+code+"%'");	
    		} 
 	   }
 	   
 	   for(int i=0;i<whereINList.size();i++)
 	   {   
 		   if(i>0)
 		   {
 			   sqlstr.append(" or ");  
 		   }else
 		   {
 			   sqlstr.append(" and ( ");    
 		   }
 		   sqlstr.append("  a0100 in(select distinct a0100 "+whereINList.get(i).toString()+") "); 
 		   if(i==whereINList.size()-1) {
               sqlstr.append(")");
           }
 	   }
 	   
 	   //sqlstr.append(" and Q03Z5 not in ('01','07','08')");
 	   return sqlstr.toString();
    }	
    
    public String select_kq_Distincta0100(String tab, String code,String kind,String start_date,String end_date,ArrayList whereINList,ArrayList nbaselist,String whereIN){
        StringBuffer sqlstr= new StringBuffer();
        //根据要求按照e0122进行排序
        sqlstr.append("select ").append(tab).append(".a0100,REPLACE(").append(tab).append(".a0101,' ','') ");
        if (colums != null && colums.length() > 0) {
            sqlstr.append("as a0101,").append(tab).append(".nbase,").append(tab).append(".e0122,").append(tab).append(".b0110,").append(tab).append(".e01a1,"+colums+"dbid,a0000");
        } else {
            sqlstr.append("as a0101,").append(tab).append(".nbase,").append(tab).append(".e0122,").append(tab).append(".b0110,").append(tab).append(".e01a1,dbid,a0000"); 
        }
        sqlstr.append(" from ").append(tab).append(" right join ");
        
        sqlstr.append(" (select a0100,nbase, max(q03z0) q03z0 from ");
        sqlstr.append("(select distinct a0100,REPLACE(a0101,' ','') as a0101,nbase,e0122,b0110,e01a1,q03z0  ");
        if(whereIN!=null&&whereIN.length()>0) {
             sqlstr.append(" "+whereIN);
                if("1".equals(kind))
            {
               sqlstr.append(" and e0122 like '"+code+"%'");
            }else if("-2".equals(kind)){
               sqlstr.append(" and 1=2");    
            } else if("0".equals(kind))
            {
               sqlstr.append(" and e01a1 like '"+code+"%'");
            }else{
               sqlstr.append(" and b0110 like '"+code+"%'"); 
            } 
        } else {
            sqlstr.append(" from ").append(tab);
            sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
            sqlstr.append(" and Q03Z0 <= '"+end_date+"'  ");
            if("1".equals(kind))
                {
                   sqlstr.append(" and e0122 like '"+code+"%'");
                }else if("-2".equals(kind)){
                   sqlstr.append(" and 1=2");    
                } else if("0".equals(kind))
                {
                   sqlstr.append(" and e01a1 like '"+code+"%'");
                }else{
                   sqlstr.append(" and b0110 like '"+code+"%'"); 
                } 
            for(int i=0;i<whereINList.size();i++)
            {   
                if(i>0)
                {
                    sqlstr.append(" or ");  
                }else
                {
                    sqlstr.append(" and ( ");    
                }
                
                String nbase = "";
                if(nbaselist.size() > i) {
                    nbase = nbaselist.get(i).toString();
                } else {
                    nbase = nbaselist.get(0).toString();
                }
                
                sqlstr.append(" nbase='").append(nbase).append("'");
                sqlstr.append(" and  a0100 in(select distinct a0100 "+whereINList.get(i).toString()+") "); 
                if(i == whereINList.size()-1) {
                    sqlstr.append(")");
                }
            }
        }
         sqlstr.append(" ) cc where cc.Q03Z0 >= '" +start_date+ "' and cc.Q03Z0 <= '" +end_date +"' group by a0100,nbase ) m ");
         sqlstr.append("on ").append(tab).append(".a0100=m.a0100");
         sqlstr.append(" and ").append(tab).append(".nbase=m.nbase");
         sqlstr.append(" and ").append(tab).append(".q03z0=m.q03z0");
         sqlstr.append(" order by ").append(tab).append(".b0110,").append(tab).append(".e0122,").append(tab).append(".e01a1,");
         sqlstr.append(tab).append(".a0100,").append(tab).append(".nbase");  
       
        return sqlstr.toString();
    }
    
    public String select_kq_Distincta01002(String tab, String code,String kind,String start_date,String end_date,ArrayList whereINList,String dbtype,String whereIN){
        ArrayList nbaselist = new ArrayList();
        nbaselist.add(dbtype);
        
        return select_kq_Distincta0100(tab, code, kind, start_date, end_date, whereINList, nbaselist, whereIN);
     }
    
    public static String select_kq_one_emp(String tab, String nbase,String a0100,String start_date,String end_date,String code,String kind,String column){
 	   StringBuffer sqlstr= new StringBuffer();
 	   int l=column.toString().length()-1;
 	   String columnstr=column.toString().substring(0,l);
 	   sqlstr.append("select q03z0,"+columnstr+" from ").append(tab); 	   
 	   sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
 	   sqlstr.append(" and Q03Z0 <= '"+end_date+"%'");	  
 	   if("1".equals(kind)){
 	       sqlstr.append(" and e0122 like '");
 	   }else if("0".equals(kind)){
			sqlstr.append(" and e01a1 like '");	
 	   }else{
			sqlstr.append(" and b0110 like '");	
 	   } 
 	 	   //sqlstr.append(" and e0122 like '");  
 	   sqlstr.append(code);
 	   sqlstr.append("%'"); 	   sqlstr.append(" and a0100="+a0100+"");
 	   sqlstr.append(" and nbase='"+nbase+"'");
 	   
 	   return sqlstr.toString();
    }
    
    public static String select_kq_oneDate_emp(String tab, String a0100,String cur_date,String code,String kind,String column){
  	   StringBuffer sqlstr= new StringBuffer();
  	   int l=column.toString().length()-1;
  	   String columnstr=column.toString().substring(0,l);
  	   sqlstr.append("select "+columnstr+" from ").append(tab);  	   
  	   sqlstr.append(" where Q03Z0 ='"+cur_date+"'");  	      
  	   sqlstr.append(" and e0122 like '");  
  	   sqlstr.append(code);
  	   sqlstr.append("%'");
  	   sqlstr.append(" and a0100="+a0100+"");
  	   
  	   return sqlstr.toString();
     }
    /****************月报表*******************/
    /**
     * @param
     * 
     * **/
    public static String count_kq_a0100_q05(String tab, String code, String kind,
			String kq_duration, ArrayList kq_dbase_list, UserView userView) {
		StringBuffer sqlstr = new StringBuffer();
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String dbase = kq_dbase_list.get(i).toString();
			String whereA0100In = RegisterInitInfoData.getWhereINSql(userView,
					dbase);
			
			sqlstr.append("select distinct A0100,nbase  from ").append(tab);
	        sqlstr.append(" where Q03Z0 = '" + kq_duration + "'");
	        if ("-2".equals(kind)) {
                sqlstr.append(" and 1=2");
            } else if (StringUtils.isNotEmpty(code)) {
                if ("1".equals(kind)) {
                    sqlstr.append(" and  e0122 like '" + code + "%'");
                } else  if ("0".equals(kind)) {
                    sqlstr.append(" and e01a1 like '" + code + "%'");
                } else {
                    sqlstr.append(" and b0110 like '" + code + "%'");
                }
            }
	        
	        sqlstr.append(" and ( ");
			sqlstr.append("  a0100 in(select distinct a0100 " + whereA0100In
					+ ") ");
			sqlstr.append("and nbase = '"+dbase+"'");
            if (i < kq_dbase_list.size() - 1) {
                sqlstr.append(") union ");
            }
            if (i == kq_dbase_list.size() - 1) {
                sqlstr.append(")");
            }
            
		}
		String sql = "select count(a0100) a from (" + sqlstr.toString()
				+ ") aaaa";
		return sql;
	}
    /**
     * 得到权限所有人员的库中人员的sql
     *  
     * **/
    public static String select_Q05_a0100(String tab, String code,String kind,String kq_duration,ArrayList whereINList)
    {
 	   StringBuffer sqlstr= new StringBuffer();
 	   sqlstr.append("select distinct a0100,a0101,q03z0,nbase  from  ").append(tab);  
 	   sqlstr.append(" where Q03Z0= '"+kq_duration+"'"); 	   
 	   if ("-2".equals(kind)) {
 	       sqlstr.append(" and 1=2");
 	   } else if (StringUtils.isNotEmpty(code)) {
 	       if ("1".equals(kind)) {
 	           sqlstr.append(" and  e0122 like '" + code + "%'");
 	       } else  if ("0".equals(kind)) {
 	           sqlstr.append(" and e01a1 like '" + code + "%'");
 	       } else {
 	           sqlstr.append(" and b0110 like '" + code + "%'");
 	       }
      }
 	  
 	   for(int i=0;i<whereINList.size();i++)
 	   {   
 		   if(i>0)
 		   {
 			   sqlstr.append(" or ");  
 		   }else
 		   {
 			   sqlstr.append(" and ( ");    
 		   }
 		   sqlstr.append("  a0100 in(select distinct a0100 "+whereINList.get(i).toString()+") "); 
 		   if(i==whereINList.size()-1) {
               sqlstr.append(")");
           }
 	   } 	  
 	   //sqlstr.append(" and Q03Z5 not in ('01','07','08')");
 	   return sqlstr.toString();
    }	
    public static String select_Q05_one_emp(String tab, String a0100,String nbase, String kq_duration,String code,String kind,String column){
  	   StringBuffer sqlstr= new StringBuffer();
  	   int l=column.toString().length()-1;
  	   String columnstr=column.toString().substring(0,l);
  	   sqlstr.append("select "+columnstr+" from  ").append(tab); 	   
  	   sqlstr.append(" where Q03Z0 = '"+kq_duration+"'");  	      
  	   if ("-2".equals(kind)) {
           sqlstr.append(" and 1=2");
       } else if (StringUtils.isNotEmpty(code)) {
           if ("1".equals(kind)) {
               sqlstr.append(" and  e0122 like '" + code + "%'");
           } else  if ("0".equals(kind)) {
               sqlstr.append(" and e01a1 like '" + code + "%'");
           } else {
               sqlstr.append(" and b0110 like '" + code + "%'");
           }
       }   
  	   
  	   sqlstr.append(" and a0100='"+a0100+"' and nbase='"+nbase+"'");
  	   
  	   return sqlstr.toString();
     }
    
    /**
     * 十进制转换十六进制
     * 
     * */
    private static String dec1Hex(int in)
	{
		String res=null;
		for(int m=0;m<16;m++)
        {
        	switch(in)
	    	{
        	  //case 0:
	    	  case 1:
	    	  case 2:
	    	  case 3:
	    	  case 4:
	    	  case 5:
	    	  case 6:
	    	  case 7:
	    	  case 8:
	    	  case 9:
	    		  res=String.valueOf(in);
	    		  break;
	    	  case 10:
	    		  res="A";
	    		  break;
	    	  case 11:
	    		  res="B";
	    		  break;
	    	  case 12:
	    		  res="C";
	    		   break;
	    	  case 13:
	    		  res="D";
	    		  break;
	    	  case 14:
	    		  res="E";
	    		  break;
	    	  case 15:
	    		  res="F";
	    		  break;
	    	  default:
	    		  res="0";
	    	      break;  
	       }
        }
		
		return res;
	}
    /**
     *  十进制转换十六进制
     * */
    public static  String dec2Hex(int dec)
	   {
	        int cc,mm;
	        String tem ,tes;
	        
	        cc=dec/16; 
	        mm=dec%16;
	        
	        tem=dec1Hex(cc);
	        tes=dec1Hex(mm);

	        return tem+tes;
	    
	   }
    public static String getColor(String kq_color)
	{
		String color="";		
		if(kq_color!=null&&kq_color.length()>=10)
    	{
			int cos=Integer.parseInt(kq_color.substring(1,4));
            int cos1=Integer.parseInt(kq_color.substring(4,7));
            int cos2=Integer.parseInt(kq_color.substring(7,10));
            color="#"+dec2Hex(cos)+dec2Hex(cos1)+dec2Hex(cos2);
    	}else
    	{
    		color="#000000";
    	}
    	return color;
	}
	public String getColums() {
		return colums;
	}
	public void setColums(String colums) {
		this.colums = colums;
	}
    public boolean isArcData() {
        return isArcData;
    }
    public void setArcData(boolean isArcData) {
        this.isArcData = isArcData;
    }
    public String getCurTab() {
        return curTab;
    }
    public void setCurTab(String curTab) {
        this.curTab = curTab;
    }
} 