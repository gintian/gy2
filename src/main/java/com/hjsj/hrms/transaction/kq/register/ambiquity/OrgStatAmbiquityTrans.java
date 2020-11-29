package com.hjsj.hrms.transaction.kq.register.ambiquity;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OrgStatAmbiquityTrans extends IBusiness{
	private String error_return="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body&flag=noself";
    public void execute() throws GeneralException
    {
    	String error_message="";
   	   String error_flag="0";
       //String stat_type=(String)this.getFormHM().get("stat_type");    
   	//不定期 展现如果是2 不展现不定期
	   String flag="2";
	   this.getFormHM().put("flag", flag);
   	   String stat_type="1";
       ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
       String code=(String)this.getFormHM().get("code");
       String kind=(String)this.getFormHM().get("kind");
       if(kind==null||kind.length()<=0)
       {
    	   kind="2";
       }
       if(code==null||code.length()<=0)
       {
    	   code=RegisterInitInfoData.getKqPrivCodeValue(userView);
       }
       if(kq_dbase_list==null||kq_dbase_list.size()<=0)
	   {
    	   kq_dbase_list=userView.getPrivDbList(); 
	   }
       
       // 过滤参数
	    KqParameter kq_paramter = new KqParameter(formHM,userView,"UN",this.frameconn);	              
		String kqBase=kq_paramter.getNbase();
       ArrayList list2 = new ArrayList();
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String str = kq_dbase_list.get(i).toString();
			String vStr = "," + kqBase + ",";
			if (vStr.contains("," + str + "")) {
				list2.add(str);
			}
		}
		kq_dbase_list = list2;
      
       String q03z0="";
       
       ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
       String kq_period="";
       ArrayList a0100whereIN= new ArrayList();
		 for(int i=0;i<kq_dbase_list.size();i++)
		 {
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				a0100whereIN.add(whereA0100In);
		 }
       String whereE0122="";
       if("0".equals(stat_type.trim()))
       {
    	   //按考勤期间范围内    	   
    	   String coursedate=(String)this.getFormHM().get("coursedate");
           q03z0=coursedate.substring(0,4);
    	   ArrayList datelist=RegisterDate.getKqDate(this.getFrameconn(),coursedate);
    	   String start_date=datelist.get(0).toString();
    	   String end_date=datelist.get(datelist.size()-1).toString();
    	   kq_period=CollectRegister.getMonthRegisterDate(start_date,end_date);
    	   getCourseCollect(q03z0,start_date,end_date,kq_dbase_list,fielditemlist,kq_period,a0100whereIN);
    	   whereE0122=OrgRegister.selcet_kq_OrgId(start_date,end_date,"e0122",a0100whereIN,"");
    	  
       }else if("1".equals(stat_type.trim()))
       {
    	   //按期间范围内
    	   String count_duration=(String)this.getFormHM().get("count_duration");
           q03z0=count_duration.substring(0,4);
    	   String stat_start=(String)this.getFormHM().get("stat_start");
    	   String stat_end=(String)this.getFormHM().get("stat_end");
    	   stat_start=stat_start.replaceAll("-","\\.");
    	   stat_end=stat_end.replaceAll("-","\\.");
    	   kq_period=CollectRegister.getMonthRegisterDate(stat_start,stat_end);
    	   getCourseCollect(q03z0,stat_start,stat_end,kq_dbase_list,fielditemlist,kq_period,a0100whereIN);
    	   whereE0122=OrgRegister.selcet_kq_AllOrgId(stat_start,stat_end,"e0122",a0100whereIN);
    	   
       }else
       {
    	   //抛异常
    	   error_message=ResourceFactory.getProperty("kq.register.collect.lost");	
 		   this.getFormHM().put("error_message",error_message);
 	       this.getFormHM().put("error_return",this.error_return);  
 	       this.getFormHM().put("error_flag","3");
 	       return;
    	   //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"",""));
       }
         ArrayList list= OrgRegister.newFieldItemList(fielditemlist);
		 
		 String codesetid="UN";
		 if(!userView.isSuper_admin()) 
        {
		 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			codesetid="UM";	
        }
		 list=OrgRegister.newFieldItemListQ09(list,codesetid);
		 
		 String b0110=code;
		 if(b0110==null||b0110.length()<=0)
		 {
			 b0110=RegisterInitInfoData.getKqPrivCodeValue(userView);
		 }
		 
		
		ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
		StringBuffer b0110Str=new StringBuffer();		
		for(int i=0;i<orgide0122List.size();i++)
		{
			b0110Str.append("'"+orgide0122List.get(i).toString()+"',");
		}
		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
		 String userOrgId=managePrivCode.getPrivOrgId();  
		 if(userOrgId!=null&&userOrgId.length()>0)
		 {
				b0110Str.append("'"+userOrgId+"',");
		 }
		String b0100s="";
		 if(b0110Str.toString()!=null&&b0110Str.toString().length()>0)
		 {
			 b0100s= b0110Str.toString().substring(0,b0110Str.length()-1);	
		 }else
		 {
			 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.date.no.record"),"",""));
		 }	
		 ArrayList sqllist=OrgRegister.getSqlstrOrg(list,b0100s ,q03z0,code,"q09");
		 this.getFormHM().put("sqlstr", sqllist.get(0).toString());	 
		 this.getFormHM().put("strwhere", sqllist.get(1).toString());
		 this.getFormHM().put("orderby",sqllist.get(2).toString());	
		 this.getFormHM().put("columns", sqllist.get(3).toString());		 	 
		 this.getFormHM().put("fielditemlist", list);		 
		 this.getFormHM().put("code",code);		 
		 this.getFormHM().put("kq_period",kq_period);
		 this.getFormHM().put("error_flag",error_flag);
		 
		 // 将导出模板的sql语句保存至服务器
	     String kq_sql_unit = sqllist.get(0).toString()+sqllist.get(1).toString()+sqllist.get(2).toString();
	     this.userView.getHm().put("kq_sql_unit",kq_sql_unit);
    }
    public void getCourseCollect(String q03z0,String start_date,String end_date,ArrayList kq_dbase_list,ArrayList fielditemlist,String kq_period,ArrayList a0100whereIN)throws GeneralException
    {
    	
			/************得到部门权限**********/

		String whereE0122=OrgRegister.selcet_kq_AllOrgId(start_date,end_date,"e0122",a0100whereIN);
		
		ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
		
		for(int r=0;r<orgide0122List.size();r++)
		{
			String e0122=orgide0122List.get(r).toString();
			// 判断是否已经汇总过返回false则表示没有
			boolean delrecord=delRecord(e0122,q03z0,"UM");
			if(delrecord)
			{
				collectRecord("e0122",e0122,start_date,end_date,q03z0,"UM",kq_period);
			}else{
				   String error_message=ResourceFactory.getProperty("kq.register.collect.lost");	
		 		   this.getFormHM().put("error_message",error_message);
		 	       this.getFormHM().put("error_return",this.error_return);  
		 	       this.getFormHM().put("error_flag","3");
		 	       return;
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"","")); 
			}
		}
		/**********得到单位权限**********/
		String whereB0110=OrgRegister.selcet_kq_AllOrgId(start_date,end_date,"b0110",a0100whereIN);
		ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.frameconn,whereB0110,"b0110");
		for(int r=0;r<orgidb0110List.size();r++)
		{
			String b0110=orgidb0110List.get(r).toString();
			
			boolean delrecord=delRecord(b0110,q03z0,"UN");
			if(delrecord)
			{
				collectRecord("b0110",b0110,start_date,end_date,q03z0,"UN",kq_period);
			}else{
				 String error_message=ResourceFactory.getProperty("kq.register.collect.lost");	
		 		   this.getFormHM().put("error_message",error_message);
		 	       this.getFormHM().put("error_return",this.error_return);  
		 	       this.getFormHM().put("error_flag","3");
		 	       return;		
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"","")); 
		  }
	   }
    }
    
    /**************汇总纪录到部门日表****************
	* @param fieldsetlist 操作表的子集
    * 
	* @param coursedate  考勤期间
	* @param code 部门	 * 
	*/
	public void collectRecord(String org_id,String org_value,String start_date,String end_date,String q03z0,String codesetid,String kq_period)throws GeneralException{
		try{
		StringBuffer statcolumn=new StringBuffer();
	    StringBuffer insertcolumn=new StringBuffer();
	    StringBuffer un_statcolumn=new StringBuffer();
		StringBuffer un_insertcolumn=new StringBuffer();
		int un_num=0;
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		// 人员库控制
		   KqParameter kq_paramter = new KqParameter(formHM,userView,"UN",this.frameconn);	              
		   String kq__BASE=kq_paramter.getNbase();
		   String []base = kq__BASE.split(",");
		int num=0;
		for(int i=0;i<fielditemlist.size();i++){
		   FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		   if("N".equals(fielditem.getItemtype())){
			  if(!"i9999".equals(fielditem.getItemid())) 
			  {
				 int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
			    	
		        if(want_sum==1)
		    	{
		        	statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");			 
		        	insertcolumn.append(""+fielditem.getItemid()+",");
			      num++;
		    	}
	    		un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
	    		un_insertcolumn.append(""+fielditem.getItemid()+",");
	    		un_num++;
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
		
		StringBuffer wheresql=new StringBuffer();
		wheresql.append("select "+org_id+","+q03z0+", ");
		wheresql.append(statcolumnstr);	
 	    wheresql.append(" from Q03");
 	    wheresql.append(" where "+org_id+"='"+org_value+"' ");
 	    wheresql.append(OrgRegister.where_Date(start_date,end_date)); 
 	    
 	   for (int i = 0; i < base.length; i++) {
		   if (i == 0) {
			   wheresql.append(" and (");
			   wheresql.append("upper(nbase)='"+base[i].toUpperCase()+"'");
		   } else {
			   wheresql.append(" or ");
			   wheresql.append("upper(nbase)='"+base[i].toUpperCase()+"'");
		   }
		   
		   if (i == base.length - 1) {
			   wheresql.append(")");
		   }
	   }
 	
 	    wheresql.append(" group by "+org_id); 	    
		ArrayList orgstatlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		//System.out.println(wheresql.toString());
		/**************得到汇总结果***************/
		this.frowset = dao.search(wheresql.toString());		    
		if(this.frowset.next())
		{
			 ArrayList onelist= new ArrayList();
			 for(int r=1;r<=num+2;r++)
			 {
				if(r<3)
				{
					onelist.add(this.frowset.getString(r)+"");
				}else
				{
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
			        onelist.add("1");
			    	onelist.add(codesetid);
			    	onelist.add("01");
			    	onelist.add(kq_period);
			    	orgstatlist.add(onelist);			    	
	    }		   
		     
		       //	   拼写insert语句
		  String insetSQL=insertSQL(insertcolumnstr);	
		  dao.batchInsert(insetSQL,orgstatlist);
		}catch(Exception e){
			  e.printStackTrace();
			  throw GeneralExceptionHandler.Handle(e); 
		  }	   
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
  public boolean delRecord(String b0110,String q03z0,String codesetid)
  {
	   boolean iscorrect=false;
	   try{
	   ContentDAO dao = new ContentDAO(this.getFrameconn());
	   //判断是否已经汇总过
	   StringBuffer delete_org=new StringBuffer();
	   delete_org.append("delete from Q09 where");	   
	   delete_org.append(" b0110 =? ");
	   delete_org.append(" and Q03Z0 = ?");
	   delete_org.append(" and setid=?");
	   ArrayList dellist=new ArrayList();	  
	   dellist.add(b0110);
	   dellist.add(q03z0);
	   dellist.add(codesetid);
	   ArrayList list= new ArrayList();
	   list.add(dellist);
	   
	     dao.batchUpdate(delete_org.toString(),list);	
	     iscorrect=true;
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return iscorrect;
  }  
  public static String insertSQL(String insertcolumn){
  	   StringBuffer insertsql=new StringBuffer();
  	   StringBuffer valuesql= new StringBuffer();    	
  	   int i=0;
	   int r=0;		  
	   insertcolumn=insertcolumn+",";
	   insertsql.append("insert into Q09 (");
	   insertsql.append("b0110,Q03Z0,");
	   valuesql.append(" values(");
	   while(i!=-1){		  
	   i=insertcolumn.indexOf(",",r);			   
	     if(i!=-1){
	       String str=insertcolumn.substring(r,i);
	       str=str.trim();	
	       insertsql.append(str+",");
	       valuesql.append("?,");	      
	     }
	     r=i+1;
	   }
	   insertsql.append("q03z3,setid,q03z5,scope)");
	   valuesql.append("?,?,?,?,?,?)");
	   String sqlstr=insertsql.toString()+valuesql.toString();
  	return sqlstr;
  }
}
