package com.hjsj.hrms.businessobject.kq.register.sing;

import com.hrms.hjsj.sys.FieldItem;

import java.util.ArrayList;

public class SingOpintion {

	 public  String getSqlstr(String columnstr,String userbase,String kq_duration,String a0100)
	 {
	 	   
			StringBuffer sqlstr=new StringBuffer();	
			//生成没有高级条件的from后的sql语句
			
			sqlstr.append("select "+columnstr+"");
			sqlstr.append(" from Q05 ");		
			sqlstr.append(" where Q03Z0 = '"+kq_duration+"'");			
			sqlstr.append(" and UPPER(nbase)='"+userbase.toUpperCase()+"'");
			sqlstr.append(" and a0100='"+a0100+"'");			
			return sqlstr.toString();
	   }
	 
	  public  String getWhereSQL(String userbase,String code,String start_date,String end_date,String tablename)
	  {
		   	StringBuffer wheresql=new StringBuffer();
			    wheresql.append(" from "+tablename+" ");	    
			    wheresql.append(" where Q03Z0 >= '"+start_date+"'");
			    wheresql.append(" and Q03Z0 <= '"+end_date+"%'");	
			    wheresql.append(" and b0110 ='"+code+"'");
				wheresql.append(" and UPPER(nbase)='"+userbase.toUpperCase()+"'");			   
			    wheresql.append(" and Q03Z5 in ('01','07')");
			    return wheresql.toString();
	}
	
	 public static ArrayList getOneYearSQLStr(ArrayList fieldsetlist,String nbase,String a0100,String cur_year,String tablename)
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
			condition.append(" Q03Z0 like '%"+cur_year+"%'");
			condition.append(" and a0100='"+a0100+"'");
			condition.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
			
			// 与审批无关，2010-07-09, 但过滤"2010-PT" wangzhongjun
//			condition.append(" and q03z5 not in ('01','02','07','08','03')");
			condition.append(" and UPPER(q03z0) != '");
			condition.append(cur_year);
			condition.append("-PT");
			condition.append("'");
			
			String ordeby=" order by Q03Z0";
			wheresql.append(" "+condition.toString());
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
			list.add(1,wheresql.toString());
		    list.add(2,ordeby);
		    list.add(3,columnstr);
		    list.add(4,condition.toString());
		    return list;
	  }
	 public static ArrayList getOneOrgYearSQLStr(ArrayList fieldsetlist,String b0110,String cur_year,String tablename)
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
			condition.append(" Q03Z0 like '%"+cur_year+"%'");
			condition.append(" and  Q03Z0 <>'"+cur_year+"'");
			condition.append(" and b0110='"+b0110+"'");			
			String ordeby=" order by Q03Z0";
			wheresql.append(" "+condition.toString());
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
			list.add(1,wheresql.toString());
		    list.add(2,ordeby);
		    list.add(3,columnstr);
		    list.add(4,condition.toString());
		    return list;
	  }
	 public static ArrayList getOneOrgMothSQLStr(ArrayList fieldsetlist,String b0110, String start_date,String end_date,String tablename)
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
			wheresql.append(" Q03Z0 >= '"+start_date+"'");
		    wheresql.append(" and Q03Z0 <= '"+end_date+"%'");	
		    wheresql.append(" and b0110='"+b0110+"'");				
			String ordeby=" order by Q03Z0";
			condition.append(" Q03Z0 >= '"+start_date+"' and Q03Z0 <= '"+end_date+"%' and b0110='"+b0110+"'");
			ArrayList list= new ArrayList();
			list.add(0,sqlstr);
			list.add(1,wheresql.toString());
		    list.add(2,ordeby);
		    list.add(3,columnstr);
		    list.add(4,condition.toString());
		    return list;
	  }
	 public static ArrayList newFieldOneList(ArrayList fielditemlist)
	    {
	    	ArrayList list=new ArrayList();
	    	for(int i=0;i<fielditemlist.size();i++){
				FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				if("A".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype()))
				{
					if(!"i9999".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())&&!"q03z5".equals(fielditem.getItemid())&&!"nbase".equals(fielditem.getItemid()))
					{
						if(!"a0100".equals(fielditem.getItemid())&&!"e0122".equals(fielditem.getItemid())&&!"a0101".equals(fielditem.getItemid())&&!"e01a1".equals(fielditem.getItemid()))
						{
							if("b0110".equals(fielditem.getItemid()))
							{
								fielditem.setVisible(false);
							}else
							{
								fielditem.setVisible(true);
							}
							list.add(fielditem);
						}
						
					}
				}
				
			}
	    	
	    	return list;
	    }
}
