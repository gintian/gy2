package com.hjsj.hrms.transaction.kq.register;


import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.*;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class OrgSumCollectTrans extends IBusiness{
    public void execute()throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());	
    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList kq_dbase_list=kqUtilsClass.setKqPerList("","");		
		
		ArrayList datelist=RegisterDate.getKqDurationList(this.frameconn);		
		String start_date=datelist.get(0).toString();
		String end_date=datelist.get(datelist.size()-1).toString();
		//转换小时 1=默认；2=HH:MM
		String selectys=(String)hm.get("selectys");
		if(selectys==null|| "".equals(selectys))
		{
			selectys="1";
		}
		ArrayList a0100whereIN= new ArrayList();
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			String dbase=kq_dbase_list.get(i).toString();
			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
			a0100whereIN.add(whereA0100In);
		}
		/************得到部门权限月汇总**********/
		String kq_period=OrgRegister.getMonthRegisterDate(start_date,end_date);
		String whereE0122=OrgRegister.selcet_kq_OrgId(start_date,end_date,"e0122",a0100whereIN,"");
		ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		StringBuffer statcolumn=new StringBuffer();
		   StringBuffer insertcolumn=new StringBuffer();
		   StringBuffer un_statcolumn=new StringBuffer();
		   StringBuffer un_insertcolumn=new StringBuffer();
		   DbWizard dbWizard = new DbWizard(this.frameconn);
		   String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
//		   String retud=gettichu(sdao_count_field,dao);
		   int num=0;
		   int un_num=0;
		   CollectRegister collectRegister=new CollectRegister();
		   /*
		    * 首钢 上岛标识 不在考勤规则里，但是月统计还需要计算进来；这里过滤一下
		    */
		   if("".equals(sdao_count_field)||sdao_count_field.length()<0)
		   {
			   for(int i=0;i<fielditemlist.size();i++){
				     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
    		    	  if("N".equals(fielditem.getItemtype()))
    				     {
    				    	 
    				    	 if(!"i9999".equals(fielditem.getItemid()))
    				    	 {
    				    		int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
    				    	
    				    		if(want_sum==1)
    				    		{
    					           statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    					           insertcolumn.append(""+fielditem.getItemid()+",");
    					         
    				    		}
    				    		un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    				    		un_insertcolumn.append(""+fielditem.getItemid()+",");
    				         }
    					  }	
				
				   }
		   }else
		   {
			   if(dbWizard.isExistField("Q03",sdao_count_field.toLowerCase()))
			   {
				   for(int i=0;i<fielditemlist.size();i++)
				   {
					     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
					   //类型为N的时候，如果指标为主集中的指标也不能计算
					     {
					    	 if("N".equals(fielditem.getItemtype()))
						     {
						    	 
						    	 if(!"i9999".equals(fielditem.getItemid()))
						    	 {
						    		int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
						    	
						    		if(want_sum==1||sdao_count_field.equalsIgnoreCase(fielditem.getItemid()))
						    		{
							           statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
							           insertcolumn.append(""+fielditem.getItemid()+",");
							         
						    		}
						    		un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
						    		un_insertcolumn.append(""+fielditem.getItemid()+",");
						         }
							  }
					     }
					   }
			   }else
			   {
				   for(int i=0;i<fielditemlist.size();i++){
					     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
					   //类型为N的时候，如果指标为主集中的指标也不能计算
					     {
					    	 if("N".equals(fielditem.getItemtype()))
						     {
						    	 
						    	 if(!"i9999".equals(fielditem.getItemid()))
						    	 {
						    		int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
						    	
						    		if(want_sum==1)
						    		{
							           statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
							           insertcolumn.append(""+fielditem.getItemid()+",");
							         
						    		}
						    		un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
						    		un_insertcolumn.append(""+fielditem.getItemid()+",");
						         }
							  }	
					     }
					   }
			   }
		   }
		   String statcolumnstr="";
		   String insertcolumnstr="";	  
		   if(statcolumn.toString()!=null&statcolumn.toString().length()>0)
		   {
			  int l=statcolumn.toString().length()-1;
			  statcolumnstr=statcolumn.toString().substring(0,l);
		      l=insertcolumn.toString().length()-1;	  
		      insertcolumnstr=insertcolumn.toString().substring(0,l);	
		   }else
		   {
			  int l=un_statcolumn.toString().length()-1;
			  statcolumnstr=un_statcolumn.toString().substring(0,l);
			  l=un_insertcolumn.toString().length()-1;		  
			  insertcolumnstr=un_insertcolumn.toString().substring(0,l);
			  num=un_num;
		   }  
		   
		   CountMoInfo countMoInfo = new CountMoInfo(this.userView, this.getFrameconn());
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int r=0;r<orgide0122List.size();r++)
		{
			String e0122=orgide0122List.get(r).toString();
			//boolean if_collect=if_OrgCollect("e0122",e0122,start_date,end_date,a0100whereIN);
//			判断员工纪录是否审批通过
			/*if(if_collect)
			{
//				判断该考勤期间是否可以重新统计
				 boolean if_dateEmpty=if_DateEmpty(kq_duration,a0100whereIN);
				 if(!if_dateEmpty){
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.org.obturate.nodata"),"",""));
				 }
				 // 判断是否已经汇总过返回false则表示没有
*/				boolean delrecord=delRecord(e0122,kq_duration,"UM");
				if(delrecord){
					//collectRecord("e0122",e0122,kq_duration,a0100whereIN,"UM",kq_period);
					collectRecord2("e0122",e0122,dao,"UM",kq_duration,insertcolumnstr,statcolumnstr);
					countMoInfo.countOrgKqInfo("Q09", e0122, kq_duration, "");
				}else{
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"","")); 
				}
				
			//}			
		}
		/*********得到单位的部门月汇总***********/
		String whereB0110=OrgRegister.selcet_kq_OrgId(start_date,end_date,"b0110",a0100whereIN,"");
		ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.frameconn,whereB0110,"b0110");
		for(int r=0;r<orgidb0110List.size();r++)
		{
			String b0110=orgidb0110List.get(r).toString();
			/*boolean if_collect=if_OrgCollect("b0110",b0110,start_date,end_date,a0100whereIN);
//			判断员工纪录是否审批通过
			if(if_collect)
			{
//				判断该考勤期间是否可以重新统计
				 boolean if_dateEmpty=if_DateEmpty(kq_duration,a0100whereIN);
				 if(!if_dateEmpty){
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.org.obturate.nodata"),"",""));
				 }*/
				 boolean delrecord=delRecord(b0110,kq_duration,"UN");
				if(delrecord){
					//collectRecord("b0110",b0110,kq_duration,a0100whereIN,"UN",kq_period);
					collectRecord2("b0110",b0110,dao,"UN",kq_duration,insertcolumnstr,statcolumnstr);
					countMoInfo.countOrgKqInfo("Q09", b0110, kq_duration, "");
				}else{
							throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"","")); 
				}
					
				
			//}			
		}    
		
		 ArrayList list= OrgRegister.newFieldItemList(fielditemlist);
		 String codesetid="UN";
		 if(!userView.isSuper_admin()) 
         {
		 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			codesetid="UM";	
         }
		 list=OrgRegister.newFieldItemListQ09(list,codesetid);
		 
		 String code=(String) this.getFormHM().get("code");
		 String b0110=code;
		 if(b0110==null||b0110.length()<=0)
		 {
			 b0110=RegisterInitInfoData.getKqPrivCodeValue(userView);
		 }
		 if(b0110==null||b0110.length()<=0)
		 {
			 ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			 b0110=managePrivCode.getPrivOrgId(); 
			
		 }
		 // 显示部门层数
		 Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		 String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		 if (uplevel == null || uplevel.length() == 0)
			 uplevel = "0";
		 this.getFormHM().put("uplevel",uplevel);
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				
				a0100whereIN.add(whereA0100In);
			}
//		 ArrayList sqllist=OrgRegister.getSumSqlstrIN(list,kq_duration, b0110, "Q09");
		 ArrayList sqllist=OrgRegister.getSumSqlstrLike(list,kq_duration, b0110, "Q09",a0100whereIN);
		 this.getFormHM().put("sqlstr", sqllist.get(0).toString());
		 this.getFormHM().put("strwhere", sqllist.get(1).toString());		  
		 this.getFormHM().put("columns", sqllist.get(2).toString()); 
		 this.getFormHM().put("orderby"," order by b0110");
		 this.getFormHM().put("kq_duration",kq_duration);
		 this.getFormHM().put("fielditemlist", list);		 
		 this.getFormHM().put("code",code);		 
		 this.getFormHM().put("orgsumvali","");
		 this.getFormHM().put("kq_period",kq_period);
		 this.getFormHM().put("selectys",selectys);
		 
		 // 将导出模板的sql语句保存至服务器
		 String kq_sql_unit = sqllist.get(0).toString()+sqllist.get(1).toString()+" order by b0110";
		 this.userView.getHm().put("kq_sql_unit",kq_sql_unit);
			
		 // 高级花名册条件 月汇总条件
		 String strSQLWhere = sqllist.get(1).toString();
		 strSQLWhere = strSQLWhere.substring(" from Q09  where".length());
		 // 涉及SQL注入直接放进userView里
		 this.userView.getHm().put("kq_condition", "9`"+strSQLWhere);
		 this.getFormHM().put("returnURL","/kq/register/daily_registerdata.do?b_query=link");
		 this.getFormHM().put("nprint","9");
		 
    }
    /**
     * 得到统计数据
     * @param code 
     * @param coursedate 
     * 
     * */
    
    public void collectRecord(String org_id,String org_value,String kq_duration,ArrayList a0100whereIN,String codesetid,String kq_period)throws GeneralException{
    	StringBuffer statorgcolumn=new StringBuffer();
	    StringBuffer insertorgcolumn=new StringBuffer();
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		int num=0;
		for(int i=0;i<fielditemlist.size();i++){
		   FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		   if("N".equals(fielditem.getItemtype())){
			  if(!"i9999".equals(fielditem.getItemid())) 
			  {
				int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
		    	
				 if(want_sum==1){
			       statorgcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");	
			       insertorgcolumn.append(""+fielditem.getItemid()+",");
			       num++;
		    	}
			    
			    
			  }
			}				
		}
		int l=statorgcolumn.toString().length()-1;
		String statorgcolumnstr=statorgcolumn.toString().substring(0,l);
		l=insertorgcolumn.toString().length()-1;		  
		String insertorgcolumnstr=insertorgcolumn.toString().substring(0,l);
		
		StringBuffer wheresql=new StringBuffer();
 	    wheresql.append(" from Q05");
 	    wheresql.append(" where Q03Z0 = '"+kq_duration+"'"); 
 	    //wheresql.append(" and Q03Z5='03' ");
	    String selectSum=OrgRegister.getOrgidListSql(org_id,org_value,wheresql.toString(),statorgcolumnstr);
	    //System.out.println(selectSum);
	    ArrayList orgSumlist = new ArrayList();
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    
		 try{
			   /**************得到汇总结果***************/
		     
			     this.frowset = dao.search(selectSum);		    
			     while(this.frowset.next()){
			    	ArrayList onelist= new ArrayList();
			    	for(int r=1;r<=num+2;r++){
			    		if(r<=2)
			    		{
			    			onelist.add(this.frowset.getString(r));
			    		}else
			    		{
			    			//onelist.add(this.frowset.getFloat(r)+"");
			    			 for(int s=0;s<fielditemlist.size();s++)
	 		    			  {
	 		    				 FieldItem fielditem=(FieldItem)fielditemlist.get(s);
	 		    				 if("N".equals(fielditem.getItemtype())&&!"i9999".equals(fielditem.getItemid()))
	 		    		 	     {
	 		    					int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
	 		    			    	if(want_sum==1)
	 		    			    	{
	 		    			    		if(fielditem.getDecimalwidth()>0)
		 	 		    				 {
		 	 		    					onelist.add(this.frowset.getFloat(fielditem.getItemid())+""); 
		 	 		    				 }else
		 	 		    				 {
		 	 		    					onelist.add(this.frowset.getInt(fielditem.getItemid())+""); 
		 	 		    				 }
		 		    					r++;
	 		    			    	}
	 		    		 	     }
	 		    			  }
			    		}			    		
			    	 }
			    	onelist.add(kq_period);
			    	onelist.add(codesetid);
			    	onelist.add("01");
			    	int i9999=OrgRegister.getI9999("Q09",org_value,kq_duration);
			    	
			    	onelist.add(new Integer(i9999));
			    	orgSumlist.add(onelist);			    	
			     }		   
		    
		       //	   拼写insert语句
			  String insetSumSQL=OrgRegister.insertSumSQL(insertorgcolumnstr);			  			  
			  dao.batchInsert(insetSumSQL,orgSumlist);
		   }catch(Exception e){
			  e.printStackTrace();
			  throw GeneralExceptionHandler.Handle(e); 
		  }	 
    }
    
    public boolean collectRecord2(String org_id,String org_value,ContentDAO dao,String codesetid,String kq_duration,String insertcolumnstr,String statcolumnstr)throws GeneralException{
	       boolean isCorrect=true;
		   //建立一张临时表
		  /*  String table_name="kqtemp_collect_"+this.userView.getUserName();	    
		    KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		    kqUtilsClass.dropTable(table_name);
		    kqUtilsClass.createTempTable("q05", table_name, "q05.*","1=2","");	*/
	       String table_name="q09";    
		   //拼写sum的sql语句	   
		   StringBuffer sql=new StringBuffer();
		   
		// 人员库控制
		   KqParameter kq_paramter = new KqParameter(formHM,userView,"UN",this.frameconn);	              
		   String kq__BASE=kq_paramter.getNbase();
		   String []base = kq__BASE.split(",");
		   
		   ArrayList list=new ArrayList();
		    try
		    {
		    	 		
			    	   sql.delete(0, sql.length());
			    	   sql.append("insert into "+table_name+"(b0110,q03z0,"+insertcolumnstr+" ,setid,Q03Z5)");//						  
					   sql.append("select  "+org_id+",q03z0,"+statcolumnstr+",'"+codesetid+"','01' from Q05");
					   sql.append(" where 1=1");
					   sql.append(" and Q03Z0 = '"+kq_duration+"'");	
					   sql.append(" and "+org_id+" ='"+org_value+"'");	 
					   
					   for (int i = 0; i < base.length; i++) {
						   if (i == 0) {
							   sql.append(" and (");
							   sql.append("upper(nbase)='"+base[i].toUpperCase()+"'");
						   } else {
							   sql.append(" or ");
							   sql.append("upper(nbase)='"+base[i].toUpperCase()+"'");
						   }
						   
						   if (i == base.length - 1) {
							   sql.append(")");
						   }
					   }
					   
					   sql.append("  GROUP BY   "+org_id+",Q03Z0 ");
					   
					   String delsql="delete from q07 where  b0110 ='"+org_value+"' and Q03Z0 = '"+kq_duration+"'";
					   dao.delete(delsql, new ArrayList());					   
				       dao.insert(sql.toString(), new ArrayList());
					
		    	 //dao.batchUpdate(list);
		    }catch(Exception e)
		    {
		    	isCorrect=false;
		    	e.printStackTrace();
		    }	
		    return isCorrect;
		   
	   }
    public boolean if_save(String org_value,String kq_duration,String codesetid){
		   boolean iscorrect=false;
		   ContentDAO dao = new ContentDAO(this.getFrameconn());
		   //判断是否已经汇总过
		   String wheresql=OrgRegister.whereSumSQL(org_value,kq_duration,codesetid);
		   String selectsql="select * from Q09 "+wheresql;
		   try{
		     this.frowset = dao.search(selectsql);
		     if(this.frowset.next()){	
		    	 iscorrect=true;
		     }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return iscorrect;
	}
    /**
     * 判断员工日考勤是否有操作考勤的数据
     * @param coursedate  考勤期间
     * @param code 单位编号
     * @param whereIN 查询子句
     * @return  false 没有，true 有
     * */
    public boolean if_DateEmpty(String kq_duration,ArrayList whereINList){
    	boolean isCorrect=false;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select Q03Z5 ");
 	    sql.append(" from Q05");
 	    sql.append(" where Q03Z0 = '"+kq_duration+"'");
 	    for(int i=0;i<whereINList.size();i++)
	    {
 	      
 		  if(i>0)
		  {
			   sql.append(" or ");  
		  }else{
			   sql.append(" and ( ");  
		  }
 	      sql.append("  a0100 in(select a0100 "+whereINList.get(i).toString()+") ");
 	      if(i==whereINList.size()-1)
	        sql.append(")");
	    }  	   
 	    ContentDAO dao = new ContentDAO(this.getFrameconn()); 	   
        try{
    	  this.frowset = dao.search(sql.toString());
          if(this.frowset.next())
          {
        	isCorrect=true;     	     
          }
       }catch(Exception e){
     	 e.printStackTrace();
       }
    	return isCorrect;
    } 
   /**********对部门日表统计过的记录清除纪录*********
    * 
	 * @param userbase  数据库前缀
	 * @param collectdate  操作时间
	 * @param code 部门	
	 * @param userbase  数据库前缀
	 * @return 是否清除成功
    *
   * *****/
  public boolean delRecord(String b0110,String coursedate,String codesetid){
	   boolean iscorrect=false;
	   try{
	   ContentDAO dao = new ContentDAO(this.getFrameconn());
	   //判断是否已经汇总过
	   StringBuffer delete_org=new StringBuffer();
	   delete_org.append("delete from Q09 where");	   
	   delete_org.append(" b0110 =? ");
	   delete_org.append(" and Q03Z0 = ?");
	   //delete_org.append(" and setid=?");
	   ArrayList dellist=new ArrayList();	  
	   dellist.add(b0110);
	   dellist.add(coursedate);
	   //dellist.add(codesetid);
	   ArrayList list= new ArrayList();
	   list.add(dellist);
	   
	     dao.batchUpdate(delete_org.toString(),list);	
	     iscorrect=true;
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return iscorrect;
  }
    

  
    //得到当前考勤时间的开始实践和结束时间
    /**
     * 判断员工考勤是否全部审批通过
     * @param userbase  库前缀
     * @param  org_id  部门||单位字段
     * @param  org_valus  部门||单位对应的值
     * @param  start_date  考勤开始时间
     * @param  end_date  考勤结束时间
     * @param whereIn  人员范围
     * @return  false 有员工没有审批;true全部审批可以汇总
     * **/
    public boolean if_OrgCollect(String org_id,String org_value,String start_date,String end_date,ArrayList whereINList)
    {
  	  boolean iscorrect=true;
  	   ContentDAO dao = new ContentDAO(this.getFrameconn());
  	   //判断是否已经汇总过
  	   StringBuffer sql=new StringBuffer();
  	   sql.append("select * from Q03 where 1=1 ");
  	   sql.append(" and Q03Z5 in ('01','07','08')");
  	   sql.append(OrgRegister.where_Date(start_date,end_date));
  	   sql.append("and "+org_id+" like '"+org_value+"%'");	
  	   for(int i=0;i<whereINList.size();i++)
  	   {
  		   if(i>0)
  		   {
  			   sql.append(" or ");  
  		   }else{
  			   sql.append(" and ( ");  
  		   }
  	       sql.append("  a0100 in(select a0100 "+whereINList.get(i).toString()+") ");
  	     if(i==whereINList.size()-1)
	    	   sql.append(")");
  	   }
  	   try{
  		   this.frowset = dao.search(sql.toString());
  	       if(this.frowset.next())
  	       {
  	    	   iscorrect=false;
  	       }
  	   }catch(Exception e)
  	   {
  		  e.printStackTrace();
  	   }
  	   return iscorrect;
    }
    
}
