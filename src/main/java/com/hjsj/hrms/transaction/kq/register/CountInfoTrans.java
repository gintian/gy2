package com.hjsj.hrms.transaction.kq.register;


import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 通过自定义计算公式，关联考勤业务表，计算当前考勤日期的数据
 * *///u
public class CountInfoTrans extends IBusiness{
	private String error_return = "/kq/register/daily_registerdata.do?b_query=link";
	private String error_message = "";
	public void execute()throws GeneralException
    {
		String error_flag="0";
		String error_stuts="0";
		String count_duration=(String)this.getFormHM().get("count_duration");
		String count_type=(String)this.getFormHM().get("count_type");
		String count_start=(String)this.getFormHM().get("count_start");
		String count_end=(String)this.getFormHM().get("count_end");
		String start_date="";
		String end_date="";		
		
		String registerdate = (String) this.getFormHM().get("registerdate");
		String code=(String) this.getFormHM().get("code");		
		if(code==null||code.length()<=0)
		{
			 code="";			 		 
		}
		if(count_type==null||count_type.length()<=0)
		 {
			 count_type="1";
		 }	
		String kind = (String)this.getFormHM().get("kind");
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");			
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    	
    	ArrayList columnlist= new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	       FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	       if(!("i9999").equalsIgnoreCase(fielditem.getItemid()))
   	       {
   	    	 columnlist.add(fielditem);
   	       }
   	       
   	    }        
    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);   
    	ArrayList kq_dbase_list=kqUtilsClass.setKqPerList("","2");
    	
        /********考勤指标项*********/
    	DataProcedureAnalyse dAnalyse = new DataProcedureAnalyse(frameconn, userView);
    	ArrayList kq_target_list=dAnalyse.getTargetList();
    	boolean isCorrect=true;
    	
		if("0".equals(count_type))//按当前考勤数据
		{
		    /*if(code==null||code.length()<=0)
		    {
		    	  error_message=ResourceFactory.getProperty("kq.count.no.code");	
		    	  
	   			  this.getFormHM().put("error_message",error_message);
	   	    	  this.getFormHM().put("error_return",this.error_return);  
	   	    	  this.getFormHM().put("error_flag","2");
	   	    	  this.getFormHM().put("error_stuts","1");
	   	    	  return;
		    	//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.count.no.code"),"",""));
		    }*/
			kqUtilsClass.leadingInItemToQ03(kq_dbase_list,registerdate,registerdate,"Q03","");//加入导入项
			try
	    	{
			 isCorrect=curCountKQ(registerdate,registerdate,code,kind,kq_target_list,columnlist);
	    	}catch(Exception e)
	    	{
	    		isCorrect=false;
	    		e.printStackTrace();    	    		 		
	    	}
		}else if("1".equals(count_type))//当前考勤时间
		{
			 
			 ArrayList countlist=RegisterDate.getKqDate(this.getFrameconn(),count_duration);
	    	 start_date=countlist.get(0).toString();
	    	 end_date=countlist.get(countlist.size()-1).toString();
	    	 kqUtilsClass.leadingInItemToQ03(kq_dbase_list,start_date,end_date,"Q03","");//加入导入项
	    	 try
 	    	 {
	    	   isCorrect=countKQInfo(start_date,end_date,kq_target_list,columnlist);
 	    	}catch(Exception e)
	    	{
	    		isCorrect=false;
	    		e.printStackTrace();    	    		 		
	    	}
		}else if("2".equals(count_type))//按选定时间范围
		{
			start_date=count_start.replaceAll("-","\\.");
			end_date=count_end.replaceAll("-","\\.");
			start_date=RegisterInitInfoData.getDateStr(start_date);
			end_date=RegisterInitInfoData.getDateStr(end_date);
			kqUtilsClass.leadingInItemToQ03(kq_dbase_list,start_date,end_date,"Q03","");//加入导入项
			try{
			  isCorrect=countKQInfo(start_date,end_date,kq_target_list,columnlist);
			}catch(Exception e)
	    	{
	    		isCorrect=false;
	    		e.printStackTrace();    	    		 		
	    	}
			
		}else
		{
			  error_message=ResourceFactory.getProperty("kq.countdate.nosave");	
			  isCorrect=false;
 			  this.getFormHM().put("error_message",error_message);
 	    	  this.getFormHM().put("error_return",this.error_return);  
 	    	  this.getFormHM().put("error_flag","2");
 	    	  this.getFormHM().put("error_stuts","1");
 	    	  return;
		}
    	
		
    	if(isCorrect)
 	    {
 	    	this.getFormHM().put("sp_result","数据计算成功！");
 	    }else
 	    {
 	    	this.getFormHM().put("sp_result",SafeCode.encode("数据计算失败！\n" + error_message));
 	    }
    	this.getFormHM().put("re_url",this.error_return);
		/***一个考勤期间的结束****/ 	   
		this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("registerdate",registerdate);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("error_flag",error_flag);
		this.getFormHM().put("error_stuts",error_stuts);
    }
	public boolean countKQInfo(String start_date,String end_date , ArrayList kq_target_list,ArrayList columnlist)throws GeneralException
	{
		   boolean isCorrect=true;
		   ArrayList dblist=new ArrayList();
		   if(this.userView.isSuper_admin())
		   {
			   KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
			   dblist=kqUtilsClass.setKqPerList("","2");
		   }else
		   {
			   dblist=userView.getPrivDbList();
		   }
		    for(int r=0;r<dblist.size();r++)
	 	    {
	 	    	String userbase=dblist.get(r).toString();
	 	    
	 	        String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);		   
		        
		   	    if(!userView.isSuper_admin())
				{   
		   	    	String whereB0110=RegisterInitInfoData.selcet_OrgId(userbase,"b0110",whereIN);
					 ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.getFrameconn(),whereB0110,"b0110");
					 for(int t=0;t<orgidb0110List.size();t++)
					 {
						 String b0110_one=orgidb0110List.get(t).toString();			
						 String nbase=RegisterInitInfoData.getOneB0110Dase(this.getFormHM(),this.userView,userbase,b0110_one,this.getFrameconn());
						 /********按照该单位的人员库的操作*********/
						 if(nbase!=null&&nbase.length()>0)
						 {
							 isCorrect= oneCountKQ(start_date,end_date,whereIN,userbase,b0110_one,"2",kq_target_list,columnlist);	 
						 }											
					 }
				}else
				{  
					ArrayList b0100list=RegisterInitInfoData.getAllBaseOrgid(userbase,"b0110",whereIN,this.getFrameconn());
					 for(int n=0;n<b0100list.size();n++)
					 {
						 String b0110_one=b0100list.get(n).toString();
						 String nbase=RegisterInitInfoData.getOneB0110Dase(this.getFormHM(),this.userView,userbase,b0110_one,this.getFrameconn());
						 /********按照该单位的人员库的操作*********/
						 if(nbase!=null&&nbase.length()>0)
						 {
							 isCorrect= oneCountKQ(start_date,end_date,whereIN,userbase,b0110_one,"2",kq_target_list,columnlist);	
						 }
				   }
			   } 	  	   
		   	}
		    return isCorrect;
	}
/**
 * 
 * @param datelist  时间
 * @param whereIN  操作人员权限
 * @param e0122  部门
 * @param kq_target_list  考勤项目指标里面是个HashMap,HashMap存了两个属性 fielditemid=指标项;c_expr=公式;
 * @param filedlist  指标项集
 * @throws GeneralException
 */
    public boolean oneCountKQ(String start_date,String end_date, String whereIN,String userbase,String code,String kind,ArrayList kq_target_list,ArrayList filedlist)throws GeneralException
    {
    	
    		boolean isCorrect=true;
        	ContentDAO dao = new ContentDAO(this.getFrameconn());
        	try
            {
        	    for(int i=0;i<kq_target_list.size();i++)
                {
                	
                	HashMap onemap=(HashMap)kq_target_list.get(i);
                	String fielditemid=onemap.get("fielditemid").toString();
                	FieldItem item=DataDictionary.getFieldItem(fielditemid);                	
                	if(item==null)
                		continue;
                	if(item.getUseflag()!=null&& "0".equals(item.getUseflag()))
                	  continue;
                	String c_expr=onemap.get("c_expr").toString();
                	if(c_expr==null||c_expr.length()<=0)
                		c_expr="";
                	int s=c_expr.indexOf("^");
                	if(s>0)
                	{
                		c_expr=c_expr.substring(0,s);
                	}else
                	{
                		continue;
                	}
                	
                	if(fielditemid==null||fielditemid.length()<=0)
                	{
                		continue;
                	}else if(c_expr==null||c_expr.length()<=0)
                	{
                		continue;
                	}else
                	{
                		
                		StringBuffer w_sql=new StringBuffer("");
                		w_sql.append("   nbase='"+userbase+"'");
                     	if("1".equals(kind))
                 		{
                     		w_sql.append(" and e0122 like '"+code+"%'");
                 		}else if("0".equals(kind))
                 		{
                 			w_sql.append(" and e01a1 like '"+code+"%'");	
                 	    }else
                 		{
                 	    	w_sql.append(" and b0110 like '"+code+"%'");	
                 		}
                     	w_sql.append(" and Q03Z0 >='"+start_date+"'");
                     	w_sql.append(" and Q03Z0 <='"+end_date+"'");  	    	
                     	w_sql.append(" and q03z5 in('01','07')"); 
                     	//sql.append(" and a0100 in(select a0100 "+whereIN+")");
                     	if(!this.userView.isSuper_admin())
                      	{
                      		if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
        		    		    {
                      				w_sql.append(" and EXISTS(select a0100 "+whereIN+" and "+userbase+"A01.a0100=q03.a0100)");
        		    		    }else
        		    		    	w_sql.append(" and EXISTS(select a0100 "+whereIN+" where "+userbase+"A01.a0100=q03.a0100)");
                      	}
                		
                		
                		DataProcedureAnalyse dAnalyse = new DataProcedureAnalyse(frameconn, userView);
                		int lxtype = dAnalyse.getLXtype(filedlist,fielditemid);    
                        //  解析公式                		
                    	YksjParser yp = new YksjParser(
                    			getUserView()//Trans交易类子类中可以直接获取userView
                    			,filedlist
                    			,YksjParser.forNormal
                    			,lxtype//此处需要调用者知道该公式的数据类型
                    			,YksjParser.forPerson
                    			,"","");
                    	yp.setWhereText(w_sql.toString());
                    	yp.setTempTableName("Q03");
                    	yp.setStdTmpTable("Q03");
                    	c_expr=c_expr.trim();
                    	yp.setCon(this.getFrameconn());
                    	if( !yp.Verify(c_expr.trim()) ){//校验不通过
                    		error_message = "“" + item.getItemdesc() + "”" + "计算公式中" + yp.getStrError();
                    		isCorrect=false;
                    		this.getFormHM().put("error_return",this.error_return);  
                    		this.getFormHM().put("error_flag","2");
                    		this.getFormHM().put("error_stuts","1");
                    		throw new GeneralException(ResourceFactory.getProperty("kq.error.count.Yksj"));
                    	}
                    	// 37474 由于上一步校验时已包含run方法 此处不需要再次run
//                    	yp.run(c_expr);   
                    	// 增加校验 如果是存储过程计算公式 则不需要执行update
                    	String FSQL=yp.getSQL();
                    	if(!("已执行存储过程".equalsIgnoreCase(FSQL) || FSQL.contains("存储过程"))) {
                    		
                    		StringBuffer sql=new StringBuffer();
                    		sql.append(getUpdateSQL(fielditemid,FSQL,kind,whereIN));
                    		sql.append(" where "+w_sql.toString() );
                    		dao.update(sql.toString());
                    	}
                	}        	
                }  
            	
            }catch(Exception e)
            {
            	  e.printStackTrace();
            	  isCorrect=false;
    			  this.getFormHM().put("error_message",error_message);
    	    	  this.getFormHM().put("error_return",this.error_return);  
    	    	  this.getFormHM().put("error_flag","2");
    	    	  this.getFormHM().put("error_stuts","1");
    	    	  return isCorrect;
            } 
            return isCorrect;
    }
    /**
     * 对当前考勤数据进行计算
     * @param start_date
     * @param end_date
     * @param kq_dbase_list
     * @param code
     * @param kind
     * @param kq_target_list
     * @param columnlist
     * @throws GeneralException
     */
    public boolean curCountKQ(String start_date,String end_date,String code,String kind,ArrayList kq_target_list,ArrayList columnlist)throws GeneralException
    {
	    boolean isCorrect=true;  
    	ArrayList filedlist=columnlist;
    	if(code==null||code.length()<=0)
    	{
    		isCorrect=countKQInfo(start_date,end_date ,kq_target_list,filedlist);
    	}else
    	{
    		for(int r=0;r<this.userView.getPrivDbList().size();r++)
    	    {
    	 	    	String userbase=this.userView.getPrivDbList().get(r).toString();	 	    
    	 	        String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);	 	        
    	 	       isCorrect= oneCountKQ(start_date,end_date,whereIN,userbase,code,kind,kq_target_list,filedlist);
    	 	}
    	}
    	return isCorrect;
    	
    }
    public String getUpdateSQL(String fielditemid,String FSQL,String kind,String whereIN)
    {
    	StringBuffer updateSQL=new StringBuffer();
    	updateSQL.append("update Q03 set "+fielditemid+"="+FSQL);    	
    	
    	return updateSQL.toString();
    }
  
    /**********判断是否可以重新计算*********
     * 
 	 * @param userbase  数据库前缀
 	 * @param collectdate  操作时间
 	 * @param code 部门	
 	 * @param userbase  数据库前缀
 	 * @return 是否可以起草
     *
    * *****/
    /*public boolean if_Refer(String userbase,String code,String registerdate,String whereIN){
    	  boolean isCorrect=false;
    	  RowSet rowSet=null;
    	     StringBuffer sql=new StringBuffer();          
    	     sql.append("select Q03Z5 from Q05 where ");
    	     sql.append(" nbase='"+userbase+"'");
    	     sql.append(" and e0122 like '"+code+"%'"); 		
    	     sql.append(" and Q03Z0='"+registerdate+"'");
             sql.append("  and a0100 in(select a0100 "+whereIN+")");             
          ContentDAO dao = new ContentDAO(this.getFrameconn());
          try{
        	  rowSet= dao.search(sql.toString());
            if(rowSet.next())
            {
        	     String Q03Z5= (String)rowSet.getString("Q03Z5");
        	      if(Q03Z5.equals("01")||Q03Z5.equals("07"))
        	      {
        		   isCorrect=true;
        	      }
             }else{
                isCorrect=true;//第一次汇总	
             }
        }catch(Exception e){
        	 e.printStackTrace();
        }
    	return isCorrect;
    }*/
    public ArrayList getNewFileds(ArrayList oldFileds,float work_time)
    {
    	 ArrayList fileds=oldFileds;
    	 FieldItem fielditem=new FieldItem();
		 fielditem.setFieldsetid("Q03");
		 fielditem.setItemdesc(ResourceFactory.getProperty("kq.item.worktime"));
		 fielditem.setItemid(work_time+"");
		 fielditem.setItemtype("N");		 
		 fielditem.setDecimalwidth(2);	
		 fileds.add(fielditem);
		 return fileds;
    }

}
