package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.CountMoInfo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SingCollectTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private String error_return="/kq/register/daily_registerdata.do?b_query=link";
	public void execute() throws GeneralException 
	{
		ArrayList opinlist=(ArrayList)this.getFormHM().get("opinlist");	
		if(opinlist==null||opinlist.size()==0)
            return;		
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
		CommonData vo_date=(CommonData)datelist.get(0);
    	String start_date=vo_date.getDataValue();    	
    	vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
   	    String end_date=vo_date.getDataValue();
   	    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);  	
   	    /*********************/
   	    ArrayList fieldlist=RegisterInitInfoData.newFieldItemList(fielditemlist,this.userView,this.frameconn);
	    FieldItem fielditem=new FieldItem();
	    fielditem.setFieldsetid("Q05");
	    fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
	    fielditem.setItemid("scope");
	    fielditem.setItemtype("A");
	    fielditem.setCodesetid("0");
	    fielditem.setVisible(true);
	    fieldlist.add(fielditem);   
	    SingOpintion singOpintion=new SingOpintion();
	    StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem)fieldlist.get(i);			
			column.append(field.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
 	    String kq_duration =RegisterDate.getKqDuration(this.getFrameconn()); 
 	    StringBuffer selectSQL=new StringBuffer();
 	    ContentDAO dao=new ContentDAO(this.getFrameconn());
 	    //对月汇总进行计算
 	   CountMoInfo countMoInfo=new CountMoInfo(this.userView,this.getFrameconn());
 	    for(int i=0;i<opinlist.size();i++)
        {
			
			SingOpinVo rec=(SingOpinVo)opinlist.get(i);   
       	    String nbase=rec.getNbase();
          	String a0100=rec.getA0100();
       	    String b0110=rec.getB0110();         	
           	String end_dd=rec.getQ03z0();
           	String strsql=singOpintion.getSqlstr(columnstr,nbase,kq_duration,a0100);
           	selectSQL.append(strsql);
        	selectSQL.append(" ");
        	selectSQL.append(" UNION ");
       	   if(end_dd!=null&&end_dd.length()==10)
       	   {
       		   end_dd=end_dd.replaceAll("-","\\.");
       		   end_date=end_dd;
       	   }
       	   boolean isColleat=delRecord(nbase,b0110,a0100,kq_duration);
       	   if(isColleat)
	       {
       		  //collectRecord(nbase,a0100,start_date,end_date,b0110,fielditemlist,kq_duration);
       		  collectRecord2(dao,nbase,a0100,start_date,end_date, fielditemlist,kq_duration);
       		  countMoInfo.singCountKQInfo(kq_duration,nbase,a0100);//个人月汇总计算
	       }else{
	    	 //抛出删除失败
	    	   String error_message=ResourceFactory.getProperty("kq.register.collect.lost");	
	 		   this.getFormHM().put("error_message",error_message);
	 	       this.getFormHM().put("error_return",this.error_return);  
	 	       this.getFormHM().put("error_flag","2");
	 	      this.getFormHM().put("error_stuts","1");
	 	       return;
		      //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"",""));    
	       }
       	   
        }
 	   selectSQL.setLength(selectSQL.length()-7); 	
	 
 	   this.getFormHM().put("s_strsql",selectSQL.toString());
	   this.getFormHM().put("s_columns",columnstr);
	   this.getFormHM().put("fieldlist",fieldlist);	
	   this.getFormHM().put("error_flag","0");
	   this.getFormHM().put("error_stuts","0");
	   this.getFormHM().put("opinlist",opinlist);
	}
	 /**************汇总纪录****************
	    * * @param fieldsetlist 操作表的子集
		 * @param userbase  数据库前缀
		 * @param collectdate  操作时间
		 * @param code 部门	 * 
		
	    * 
	    * */
	   
	   public void collectRecord(String userbase,String a0100,String start_date,String end_date,String code, ArrayList fielditemlist,String kq_duration)throws GeneralException{
	       ArrayList a0100list=new ArrayList();
	       a0100list.add(a0100);
	       SingOpintion singOpintion=new SingOpintion();
		      //拼写sum的sql语句	   
		   StringBuffer statcolumn=new StringBuffer();
		   StringBuffer insertcolumn=new StringBuffer();
		   StringBuffer un_statcolumn=new StringBuffer();
		   StringBuffer un_insertcolumn=new StringBuffer();
		   ArrayList sum_filed_list = new ArrayList();
		   ArrayList un_filed_list = new ArrayList();
		   int num=0;
		   int un_num=0;
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
			           sum_filed_list.add(fielditem);
			           insertcolumn.append(""+fielditem.getItemid()+",");
			           num++;
		    		}
		    		un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
		    		un_filed_list.add(fielditem);
		    		un_insertcolumn.append(""+fielditem.getItemid()+",");
		    		un_num++;
		         }
			  }				
		   }
		   String statcolumnstr="";
		   String insertcolumnstr="";
		   ArrayList filedlist=new ArrayList();
		   if(statcolumn.toString()!=null&&statcolumn.toString().length()>0)
		   {
			  int l=statcolumn.toString().length()-1;
			  statcolumnstr=statcolumn.toString().substring(0,l);
		      l=insertcolumn.toString().length()-1;		  
		      filedlist=sum_filed_list;
		      insertcolumnstr=insertcolumn.toString().substring(0,l);	
		   }else
		   {
			  int l=un_statcolumn.toString().length()-1;
			  statcolumnstr=un_statcolumn.toString().substring(0,l);
			  l=un_insertcolumn.toString().length()-1;		  
			  insertcolumnstr=un_insertcolumn.toString().substring(0,l);
			  filedlist=un_filed_list;
			  num=un_num;
		   }  
		    /*通过a0100得到一个月中的员工考勤sql语句，返回的是list*/
		      ContentDAO dao = new ContentDAO(this.getFrameconn());	  
		      String wheresql=singOpintion.getWhereSQL(userbase,code,start_date,end_date,"Q03");	   
		      ArrayList selectlist=CollectRegister.getStatListSql(a0100list,wheresql,statcolumnstr);   
		      ArrayList statlist = new ArrayList();	   
		      String kq_period=CollectRegister.getMonthRegisterDate(start_date,end_date);	  
		      try{
			      /**************得到汇总结果***************/
		         for(int i=0;i<selectlist.size();i++)
		         {
			        String statsql=selectlist.get(i).toString();			       
			        this.frowset = dao.search(statsql.toString());		    
			        if(this.frowset.next())
			        {
			    	   ArrayList onelist= new ArrayList();
			    	  /* for(int r=1;r<=num+6;r++)
			           {
			    		  onelist.add(this.frowset.getString(r));
			    	   }*/
		    	       onelist.add(this.frowset.getString("nbase"));
			    	   onelist.add(this.frowset.getString("a0100"));
			    	   onelist.add(this.frowset.getString("e01a1"));
			    	   onelist.add(this.frowset.getString("b0110"));
			    	   onelist.add(this.frowset.getString("e0122"));
			    	   onelist.add(this.frowset.getString("a0101"));		    	   
			    	   for(int r=0;r<filedlist.size();r++)
			           {
			    		  FieldItem fielditem=(FieldItem)filedlist.get(r);
			    		  int decimalwidth=fielditem.getDecimalwidth();
			    		  String itemid=fielditem.getItemid();
			    		  String value=this.frowset.getString(itemid);
			    		  if(decimalwidth>0)
			    		  {
			    			 value= PubFunc.DoFormatDecimal(value,decimalwidth);
			    		  }
			    		  onelist.add(value);		    		  
			    	   }		    	 
			           onelist.add(kq_duration);
			           onelist.add("01");
			           onelist.add(kq_period);
			           onelist.add("0");//标志
			           int i9999=RegisterInitInfoData.getI9999("Q05",userbase,a0100list.get(i).toString());
			           onelist.add(new Integer(i9999));
			    	   statlist.add(onelist);  
			    	   
			        }		   
		         }
		       //	   拼写insert语句
		         
		         
			     String insetSQL=CollectRegister.insertSQL(insertcolumnstr);	
			     
			     dao.batchInsert(insetSQL,statlist);
		      }catch(Exception e){
			     e.printStackTrace();
			        String error_message=ResourceFactory.getProperty("kq.register.collect.lost");	
		 		   this.getFormHM().put("error_message",error_message);
		 	       this.getFormHM().put("error_return",this.error_return);  
		 	       this.getFormHM().put("error_flag","2");
		 	      this.getFormHM().put("error_stuts","1");
		 	       return;
			     //throw GeneralExceptionHandler.Handle(e); 
		      }	 
		   
	   }
	 
	   /**********对统计过的记录清除纪录*********
	     * 
		 * @param userbase  数据库前缀
		 * @param collectdate  操作时间
		 * @param code 部门	
		 * @param userbase  数据库前缀
		 * @return 是否清除成功
	     *
	    * *****/
	   public boolean delRecord(String userbase,String b0110,String a0100,String kq_duration){
		   boolean iscorrect=false;
		   try{
		   ContentDAO dao = new ContentDAO(this.getFrameconn());
		   //判断是否已经汇总过
		   StringBuffer delete_kq_Sum=new StringBuffer();
		   delete_kq_Sum.append("delete from Q05 where");
		   delete_kq_Sum.append(" nbase=?");	     
	       delete_kq_Sum.append(" and Q03Z0=? and q03z5 in (?,?)");
		   delete_kq_Sum.append(" and a0100=?");	
		   
		   ArrayList dellist=new ArrayList();
		   dellist.add(userbase);		 
		   dellist.add(kq_duration);
		   dellist.add("01");
		   dellist.add("07");
		   dellist.add(a0100);
		   ArrayList list= new ArrayList();
		   list.add(dellist);
		   
		     dao.batchUpdate(delete_kq_Sum.toString(),list);	
		     iscorrect=true;
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return iscorrect;
	   }	
	   public boolean collectRecord2(ContentDAO dao,String userbase,String a0100,String start_date,String end_date, ArrayList fielditemlist,String kq_duration)throws GeneralException{
	       boolean isCorrect=true;
		   //建立一张临时表
		  /*  String table_name="kqtemp_collect_"+this.userView.getUserName();	    
		    KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		    kqUtilsClass.dropTable(table_name);
		    kqUtilsClass.createTempTable("q05", table_name, "q05.*","1=2","");	*/
	       String table_name="q05";    
		   //拼写sum的sql语句	   
		   StringBuffer statcolumn=new StringBuffer();
		   StringBuffer insertcolumn=new StringBuffer();
		   StringBuffer un_statcolumn=new StringBuffer();
		   StringBuffer un_insertcolumn=new StringBuffer();
		   int num=0;
		   int un_num=0;
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
		   
		   String statcolumnstr="";
		   String insertcolumnstr="";	  
		   if(statcolumn.toString()!=null&&statcolumn.toString().length()>0)
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
		   //插入汇总人员记录
		    String kq_period=CollectRegister.getMonthRegisterDate(start_date,end_date);	
		    StringBuffer sql= new StringBuffer();
		    sql.append("insert into "+table_name+"(a0100,nbase,q03z0,"+insertcolumnstr+", scope, Q03Z3, Q03Z5)");
		    sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','0','01' from Q03");
		    sql.append(" where nbase='"+userbase+"'");
		    sql.append(" and Q03Z0 >= '"+start_date+"'");
		    sql.append(" and Q03Z0 <= '"+end_date+"'");	   
		    sql.append(" and a0100 ='"+a0100+"'");	
		    sql.append(" and Q03Z5 in ('01','07') GROUP BY  A0100 ");	   
		    try
		    {
		    	dao.insert(sql.toString(), new ArrayList());
		    	sql.delete(0, sql.length());
		        String destTab=table_name;//目标表xxxxxxxxxxx
				String srcTab="q03";//源表
				String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
			    String strSet=destTab+".b0110="+srcTab+".b0110`"+destTab+".e0122="+srcTab+".e0122`"+destTab+".e01a1="+srcTab+".e01a1`"+destTab+".a0101="+srcTab+".a0101";//更新串  xxx.field_name=yyyy.field_namex,....
				String strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
				
				//String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
				String strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 = '"+end_date+"' and "+srcTab+".a0100 ='"+a0100+"'";
			    String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
				update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
				dao.update(update);
				//同步q03
				sql.delete(0, sql.length());
		        destTab="q03";//目标表xxxxxxxxxxx
				srcTab="q05";//源表
				strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
			    strSet=destTab+".q03z5="+srcTab+".q03z5";//更新串  xxx.field_name=yyyy.field_namex,....
				strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".Q03Z0 >= '"+start_date+"' and "+destTab+".Q03Z0 <= '"+end_date+"' and "+destTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
				strSWhere=srcTab+".nbase='"+userbase+"' and "+srcTab+".q03z0='"+kq_duration+"'  and  "+srcTab+".a0100 ='"+a0100+"'";//源表的过滤条件  
				//strSWhere="";
			    update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
			    
			    update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
			    
			    //System.out.println(update);
				dao.update(update);
		    }catch(Exception e)
		    {
		    	isCorrect=false;
		    	e.printStackTrace();
		    }	
		    return isCorrect;
		   
	   }
}
