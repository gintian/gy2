package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.options.imports.SearchImportBo;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.CountMoInfo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.Pigeonhole;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SingOperationTrans extends IBusiness {
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
   	    String end_date1=end_date;
   	    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    	
	    ArrayList columnlist= new ArrayList();
	    String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());
	    for(int i=0;i<fielditemlist.size();i++)
	    {
	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	          if(!("i9999").equalsIgnoreCase(fielditem.getItemid()))
	   	       {
	   	    	 columnlist.add(fielditem);
	   	       }			
	    }  
	    ArrayList kq_target_list=getTargetList();
	    ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
		String userOrgId=managePrivCode.getPrivOrgId();  
   	    KqParameter kq_paramter = new KqParameter(this.userView,userOrgId,this.getFrameconn()); 
   	    HashMap hashmap =kq_paramter.getKqParamterMap();
	    String kq_type=(String)hashmap.get("kq_type");
	    String kq_cardno=(String)hashmap.get("cardno");
	    String kq_Gno=(String)hashmap.get("g_no");
		String analyseType="0";
		String dataUpdateType="1";
		String analysBase="all";		
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
     	  if(end_dd!=null&&end_dd.length()==10)
     	  {
     		end_date=end_dd.replaceAll("-","\\.");
     	  }
     	 
          String code=nbase+a0100;
          String kind="-1";
          ArrayList kq_dbase_list=new ArrayList();
          kq_dbase_list.add(nbase);
          DataProcedureAnalyse dataProcedureAnalyse=new DataProcedureAnalyse(this.getFrameconn(),this.userView,analyseType,kq_type,kq_cardno,kq_Gno,dataUpdateType,kq_dbase_list);
  		  dataProcedureAnalyse.dataAnalys(code,kind,start_date,end_date,analysBase);	//走数据处理class
  		 
          oneCountKQ(start_date,end_date,a0100,nbase,b0110,"2",kq_target_list,columnlist);//日明晰计算
          boolean isColleat=delRecord(nbase,b0110,a0100,kq_duration);
          if(isColleat)
          {
        	  //collectRecord(nbase,a0100,start_date,end_date,b0110,fielditemlist,kq_duration);//统计
        	  collectRecord2(dao,nbase,a0100,start_date,end_date, fielditemlist,kq_duration);
        	  leadingInItemToQ05(nbase,a0100,start_date,end_date,kq_duration);//加入导入项
       		  countMoInfo.singCountKQInfo(kq_duration,nbase,a0100);//个人月汇总计算
          }else
          {
//        	抛出删除失败
	    	   String error_message=ResourceFactory.getProperty("kq.register.collect.lost");	
	 		   this.getFormHM().put("error_message",error_message);
	 	       this.getFormHM().put("error_return",this.error_return);  
	 	       this.getFormHM().put("error_flag","2");
	 	       this.getFormHM().put("error_stuts","1");
	 	       return;
          }
         // oneMoCountKQ(kq_duration,a0100,nbase,b0110,"2",kq_target_list,columnlist);//月汇总计算
          updateQ03Sql(a0100,nbase,start_date,end_date1,"03");//修改状态
  	      updateQ05(a0100,nbase,kq_duration,"03");//修改状态
        }
   	    
   	    /**************归档*************/
   	    Pigeonhole pigeonhole=new Pigeonhole(this.getFrameconn(),this.userView);
		String temp_table=pigeonhole.createTempTable(this.userView.getUserName());//建立临时表		
		pigeonhole.insertActivPigeonhole(temp_table);//插入信息
		ArrayList xmlList=getPigeonholeXml();
		pigeonhole.updateActivPigeonhole(temp_table,xmlList);//修改
		String sqlstr="select SrcFldType,SrcFldId,SrcFldName,DestFldId,DestFldName";
		String wherestr=" from "+temp_table;
		String column="SrcFldType,SrcFldId,SrcFldName,DestFldId,DestFldName";
		this.getFormHM().put("po_sqlstr",sqlstr);
		this.getFormHM().put("po_wherestr",wherestr);
		this.getFormHM().put("po_column",column);
		this.getFormHM().put("temp_table",temp_table);		
		this.getFormHM().put("po_userlist",opinlist);
		this.getFormHM().put("error_flag","0");
	}
	/**
	 * 日明晰计算
	 * @param start_date
	 * @param end_date
	 * @param a0100
	 * @param userbase
	 * @param code
	 * @param kind
	 * @param kq_target_list
	 * @param filedlist
	 * @throws GeneralException
	 */
	public void oneCountKQ(String start_date,String end_date, String a0100,String userbase,String code,String kind,ArrayList kq_target_list,ArrayList filedlist)throws GeneralException
	{
	    	ArrayList SQLlist=new ArrayList();
	        ArrayList valueList= new ArrayList();        	
	        	try
	            {
	        	    for(int i=0;i<kq_target_list.size();i++)
	                {
	                	
	                	HashMap onemap=(HashMap)kq_target_list.get(i);
	                	String fielditemid=(String)onemap.get("fielditemid");
	                	String c_expr=(String)onemap.get("c_expr");
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
	                	ArrayList onelist= new ArrayList();
	                	if(fielditemid==null||fielditemid.length()<=0)
	                	{
	                		continue;
	                	}else if(c_expr==null||c_expr.length()<=0)
	                	{
	                		continue;
	                	}else
	                	{
	                		int lxtype=getLXtype(filedlist,fielditemid);    
	                        //  解析公式
	                    	YksjParser yp = new YksjParser(
	                    			getUserView()//Trans交易类子类中可以直接获取userView
	                    			,filedlist
	                    			,YksjParser.forNormal
	                    			,lxtype//此处需要调用者知道该公式的数据类型
	                    			,YksjParser.forPerson
	                    			,"","");
	                    	c_expr=c_expr.trim();
	                    	//System.out.println("c_expr----->"+c_expr);                    	
	                    	yp.run(c_expr);                	
	                    	String FSQL=yp.getSQL();
	                    	//System.out.println("getUpdateSQL(fielditemid,FSQL,kind)---->"+getUpdateSQL(fielditemid,FSQL,kind));
	                    	SQLlist.add(getUpdateSQL(fielditemid,FSQL,kind));                	
	                    	onelist.add(userbase);
	                    	onelist.add(code);
	                    	onelist.add(a0100);
	                    	onelist.add(start_date);
	                    	onelist.add(end_date);
	                    	onelist.add("01");
	                    	onelist.add("07");
	                    	valueList.add(onelist);
	                	}        	
	                }  
	            	ContentDAO dao = new ContentDAO(this.getFrameconn());
	            	//System.out.println("SQLlist----->"+SQLlist);
	            	//System.out.println("valueList---->"+valueList);
	            	dao.batchUpdate(SQLlist,valueList);
	            }catch(Exception e)
	            {
	            	e.printStackTrace();
	            	String error_message=ResourceFactory.getProperty("kq.error.count.Yksj");	
			 		this.getFormHM().put("error_message",error_message);
			 	    this.getFormHM().put("error_return",this.error_return); 
			 	    this.getFormHM().put("error_stuts","1");
			 	    this.getFormHM().put("error_flag","2");
			 	    return;	
	            	//throw GeneralExceptionHandler.Handle(e);
	            } 
	    }
	/**
	 * 月汇总计算
	 * @param start_date
	 * @param end_date
	 * @param a0100
	 * @param userbase
	 * @param code
	 * @param kind
	 * @param kq_target_list
	 * @param filedlist
	 * @throws GeneralException
	 */
	public void oneMoCountKQ(String kq_duration, String a0100,String userbase,String code,String kind,ArrayList kq_target_list,ArrayList filedlist)throws GeneralException
	{
	    	ArrayList SQLlist=new ArrayList();
	        ArrayList valueList= new ArrayList();        	
	        	try
	            {
	        	    for(int i=0;i<kq_target_list.size();i++)
	                {
	                	
	                	HashMap onemap=(HashMap)kq_target_list.get(i);
	                	String fielditemid=(String)onemap.get("fielditemid");
	                	String c_expr=(String)onemap.get("c_expr").toString();
	                	if(c_expr==null||c_expr.length()<=0)
	                		c_expr="";
	                	int s=c_expr.indexOf("^");
	                	if(s!=-1)
	                	{
	                		c_expr=c_expr.substring(s+1);
	                	}else
	                	{
	                		continue;
	                	}
	                	ArrayList onelist= new ArrayList();
	                	if(fielditemid==null||fielditemid.length()<=0)
	                	{
	                		continue;
	                	}else if(c_expr==null||c_expr.length()<=0)
	                	{
	                		continue;
	                	}else
	                	{
	                		int lxtype=getLXtype(filedlist,fielditemid);    
	                        //  解析公式
	                    	YksjParser yp = new YksjParser(
	                    			getUserView()//Trans交易类子类中可以直接获取userView
	                    			,filedlist
	                    			,YksjParser.forNormal
	                    			,lxtype//此处需要调用者知道该公式的数据类型
	                    			,YksjParser.forPerson
	                    			,"","");
	                    	c_expr=c_expr.trim();
	                    	//System.out.println("c_expr----->"+c_expr);                    	
	                    	yp.run(c_expr);                	
	                    	String FSQL=yp.getSQL();
	                    	//System.out.println("getUpdateSQL(fielditemid,FSQL,kind)---->"+getUpdateSQL(fielditemid,FSQL,kind));
	                    	SQLlist.add(getMoUpdateSQL(fielditemid,FSQL,kind));                	
	                    	onelist.add(userbase);
	                    	onelist.add(code);
	                    	onelist.add(a0100);
	                    	onelist.add(kq_duration);	                    	
	                    	onelist.add("01");
	                    	onelist.add("07");
	                    	valueList.add(onelist);
	                	}        	
	                }  
	            	ContentDAO dao = new ContentDAO(this.getFrameconn());
	            	//System.out.println("SQLlist----->"+SQLlist);
	            	//System.out.println("valueList---->"+valueList);
	            	dao.batchUpdate(SQLlist,valueList);
	            }catch(Exception e)
	            {
	            	e.printStackTrace();
	            	String error_message=ResourceFactory.getProperty("kq.error.count.Yksj");	
			 		this.getFormHM().put("error_message",error_message);
			 	    this.getFormHM().put("error_return",this.error_return); 
			 	    this.getFormHM().put("error_stuts","1");
			 	    this.getFormHM().put("error_flag","2");
			 	    return;	
	            	//throw GeneralExceptionHandler.Handle(e);
	            } 
	    }
	public String getUpdateSQL(String fielditemid,String FSQL,String kind)
	{
	    	StringBuffer updateSQL=new StringBuffer();
	    	updateSQL.append("update Q03 set "+fielditemid+"="+FSQL);    	
	    	updateSQL.append(" where nbase=?");
	    	if("1".equals(kind))
			{
	    		updateSQL.append(" and e0122 like ?");
			}else
			{
				updateSQL.append(" and b0110 like ?");	
			}
	    	updateSQL.append(" and a0100=?");
	    	updateSQL.append(" and Q03Z0 >=?");
	    	updateSQL.append(" and Q03Z0 <=?");  	
	    	updateSQL.append(" and q03z5 in(?,?)");    	
	    	return updateSQL.toString();
   }
	/**
	 * 月
	 * @param fielditemid
	 * @param FSQL
	 * @param kind
	 * @return
	 */
	public String getMoUpdateSQL(String fielditemid,String FSQL,String kind)
	{
	    	StringBuffer updateSQL=new StringBuffer();
	    	updateSQL.append("update Q03 set "+fielditemid+"="+FSQL);    	
	    	updateSQL.append(" where nbase=?");
	    	if("1".equals(kind))
			{
	    		updateSQL.append(" and e0122 like ?");
			}else
			{
				updateSQL.append(" and b0110 like ?");	
			}
	    	updateSQL.append(" and a0100=?");
	    	updateSQL.append(" and Q03Z0 =?");	    	 	
	    	updateSQL.append(" and q03z5 in(?,?)");    	
	    	return updateSQL.toString();
   }
	    /**
	     * 得到考勤规则中的考勤指标和公式
	     * @return
	     * @throws GeneralException
	     */
    public  int getLXtype(ArrayList columnlist,String fielditemid)
	{
			int lxtype=YksjParser.INT;		
			for(int r=0;r<columnlist.size();r++)
	 		{
	 	   	   FieldItem fielditem=(FieldItem)columnlist.get(r); 	   	   
	 	   	   if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
	 	   	   {  
	 	   		 if("D".equalsIgnoreCase(fielditem.getItemtype()))
	 	   		 {
	 	   			  lxtype=YksjParser.DATEVALUE;	
		   			  break;
	 	   		 }
	 	   		  else if("N".equalsIgnoreCase(fielditem.getItemtype()))
	 	   		  {
		 	   		  if(fielditem.getDecimalwidth()>0)
			   	      {
		 	   		      lxtype=YksjParser.FLOAT;
		 	   		      break;
			   		  }else
			   		  {   
			   			  lxtype=YksjParser.INT;	  
			   			  break;
			   		  }	
	 	   		  }
	 	   		  else if("A".equalsIgnoreCase(fielditem.getItemtype()))
	 	   		  {
	 	   			  lxtype=YksjParser.STRVALUE;	
	 	   			  break;
	 	   		  }
	 	   	   }
	 		}
			return lxtype;
	}
    /**
     * 得到考勤规则中的考勤指标和公式
     * @return
     * @throws GeneralException
     */

    public ArrayList getTargetList()throws GeneralException
    {
    	StringBuffer kq_Target= new StringBuffer();
    	kq_Target.append("select fielditemid,c_expr from kq_item ");    	
    	kq_Target.append(" order by displayorder");
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	RowSet rowSetF=null; 
    	ArrayList targetlist= new ArrayList();
    	try{
    		rowSetF=dao.search(kq_Target.toString());
        	while(rowSetF.next())
        	{
        		HashMap map=new HashMap();
        		String c_expr=Sql_switcher.readMemo(rowSetF,"c_expr");
        		if(c_expr!=null&&c_expr.length()>0)
        		{
        			map.put("fielditemid",rowSetF.getString("fielditemid"));
            		map.put("c_expr",Sql_switcher.readMemo(rowSetF,"c_expr"));
            		targetlist.add(map);
        		}
        		
        	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		//throw GeneralExceptionHandler.Handle(e);
    	}finally{
			if(rowSetF!=null){
				try {
					rowSetF.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	return targetlist;
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
		   if(statcolumn.toString()!=null&statcolumn.toString().length()>0)
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
			    		  if(value!=null&&value.length()>0)
			    		  {
			    			  onelist.add(value);
			    		  }else
			    		  {
			    			  onelist.add("0");
			    		  }
			    		  		    		  
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
	       delete_kq_Sum.append(" and b0110=?");
	       delete_kq_Sum.append(" and Q03Z0=? and q03z5 in (?,?)");
		   delete_kq_Sum.append(" and a0100=?");	
		   
		   ArrayList dellist=new ArrayList();
		   dellist.add(userbase);
		   dellist.add(b0110);
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
	   /**
	    * 修改q03
	    * @param a0100
	    * @param dbase
	    * @param start_date
	    * @param end_date
	    * @param q03z5
	    * @throws GeneralException
	    */
	   public void updateQ03Sql(String a0100,String dbase,String start_date,String end_date,String q03z5)throws GeneralException
	    {
	    	
	    	StringBuffer updatesql=new StringBuffer();
	    	updatesql.append("update Q03 set ");
	    	updatesql.append(" q03z5=? where ");
	    	updatesql.append(" nbase=? ");    	
	    	updatesql.append(" and Q03Z0 >=? ");
	    	updatesql.append(" and Q03Z0 <=? ");
	    	updatesql.append(" and a0100 =?");
	    	//updatesql.append(" and q03z5 in ('01','07')");
	    	ArrayList u_list=new ArrayList();
	  	    u_list.add(q03z5);
	  	    u_list.add(dbase);  	   
	  	    u_list.add(start_date);
	  	    u_list.add(end_date);
	  	    u_list.add(a0100);
		    ArrayList list= new ArrayList();
		    list.add(u_list);
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    try{
		         dao.batchUpdate(updatesql.toString(),list);	         
		        
		    }catch(Exception e){
		    	   e.printStackTrace();
		    	    String error_message=ResourceFactory.getProperty("kq.register.refer.lost");	
			 		this.getFormHM().put("error_message",error_message);
			 	    this.getFormHM().put("error_return",this.error_return);  
			 	    this.getFormHM().put("error_flag","1");
			 	    return;	
		    	   //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
		    }
		    
	    }
	    /****************
	     * @param whereIN select in子句 
	     * @param tablename 表名
	     * @return 返回？号的update的SQL语句
	     * 
	     * ***/
	    public void updateQ05(String a0100,String dbase,String kq_duration,String q03z5)throws GeneralException
	    {
	    	
	    	StringBuffer updatesql=new StringBuffer();
	    	updatesql.append("update Q05 set ");
	    	updatesql.append(" q03z5=? where ");
	    	updatesql.append(" nbase=? ");    	
	    	updatesql.append(" and Q03Z0 =? ");    
	    	updatesql.append(" and a0100=?");
	    	//updatesql.append(" and q03z5 in ('01','07')");
	    	ArrayList u_list=new ArrayList();
	  	    u_list.add(q03z5);
	  	    u_list.add(dbase);  	   
	  	    u_list.add(kq_duration);
	  	    u_list.add(a0100);
		    ArrayList list= new ArrayList();
		    list.add(u_list);
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    try{
		         dao.batchUpdate(updatesql.toString(),list);	         
		        
		    }catch(Exception e){
		    	   e.printStackTrace();
		    	   String error_message=ResourceFactory.getProperty("kq.register.refer.lost");	
			 		this.getFormHM().put("error_message",error_message);
			 	    this.getFormHM().put("error_return",this.error_return);  
			 	    this.getFormHM().put("error_flag","1");
			 	    return;	
		    	   //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
		    }
	    }
	    /**
		 * 得到归档方案表里的数据,添加到BS归档方案临时表里面
		 *
		 */
	    public ArrayList getPigeonholeXml()
	    {
	    	String sql="select id,bytes from kq_archive_schema";
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	ArrayList list=new ArrayList();
	    	
	    	try
	    	{
	    	     this.frecset=dao.search(sql);
	    	     if(this.frecset.next())
	    	     {
	    	    	
	    	    	 this.getFormHM().put("bytesid",this.frecset.getString("id"));
	    	    	 /*int ch = 0;
	    	    	 InputStream isByte=this.frecset.getBinaryStream("bytes"); 
	    	    	 BufferedReader br = new BufferedReader(new InputStreamReader(isByte,"GB2312")); 
	    	    	 while((ch = br.read())!=-1)
	   	    	     {
	   	    		 System.out.print((char)ch); 
	   	    	     }*/
	    	    	 String xpath="/ArchScheme/RelaSet";
	    	    	 String xmlContent=Sql_switcher.readMemo(this.frecset,"bytes");
	    	    	 	 
	    	    	 if(xmlContent!=null&&xmlContent.length()>0)
	    	    	 {
	    	    		 Document doc = PubFunc.generateDom(xmlContent);     				
	     				 XPath reportPath = XPath.newInstance(xpath);
	     				 List setlist=reportPath.selectNodes(doc);  
	     				 Iterator i = setlist.iterator();
	    				 if(i.hasNext())
	    				 {   
	    					 Element childR=(Element)i.next();
	    					 childR.getAttributeValue("SrcFldSet");//原表表名
	    					 String DestFldSet=childR.getAttributeValue("DestFldSet");//归档目标表名
	    					 this.getFormHM().put("destfld",DestFldSet);
	    					 List fldlist=childR.getChildren();					 
	    					 Iterator tt = fldlist.iterator();
	    					 while(tt.hasNext())
	    					 {
	    						 ArrayList one_list=new ArrayList();
	    						 Element relaFld=(Element)tt.next();
	                             
	    						 one_list.add(relaFld.getAttributeValue("DestFldName"));//归档的字段名称
	    						 one_list.add(relaFld.getAttributeValue("DestFldId"));//归档字段代码
	    						 one_list.add(relaFld.getAttributeValue("DestCodeSet"));//归档的字段类型		
	    						 one_list.add(relaFld.getAttributeValue("SrcFldId"));//原表的字段代码                         
	    						 list.add(one_list);
	    					}
	    				 }
	    	    	 }
	    	     }else
	    	     {
	    	    	 inItPigeonholeXml();
	    	    	 this.getFormHM().put("bytesid","1");
	    	     }
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	    } 
	   public void inItPigeonholeXml()
	   {
		   String insert="insert into kq_archive_schema (id,status) values (?,?)";
		   ArrayList list=new ArrayList ();
		   list.add("1");
		   list.add("1");
		   ContentDAO dao=new ContentDAO(this.getFrameconn());	
		   try
		   {
			  dao.insert(insert,list);
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
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
		   //插入汇总人员记录
		    String kq_period=CollectRegister.getMonthRegisterDate(start_date,end_date);	
		    String mainindex=getmainsql();
			String mainindex1=getmainsql2();
		    StringBuffer sql= new StringBuffer();
		    if(!"".equals(mainindex)||mainindex.length()>0)
		    {
			    sql.append("insert into "+table_name+"(a0100,nbase,q03z0,"+insertcolumnstr+", scope, Q03Z3, Q03Z5,"+mainindex+")");
//			    sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','0','01' from Q03");
			    sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','0','01',"+mainindex1+" from Q03");
			    sql.append(" where nbase='"+userbase+"'");
			    sql.append(" and Q03Z0 >= '"+start_date+"'");
			    sql.append(" and Q03Z0 <= '"+end_date+"'");	   
			    sql.append(" and a0100 ='"+a0100+"'");	
			    sql.append(" and Q03Z5 in ('01','07') GROUP BY  A0100 ");
		    }else
		    {
		    	sql.append("insert into "+table_name+"(a0100,nbase,q03z0,"+insertcolumnstr+", scope, Q03Z3, Q03Z5)");
			    sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','0','01' from Q03");
			    sql.append(" where nbase='"+userbase+"'");
			    sql.append(" and Q03Z0 >= '"+start_date+"'");
			    sql.append(" and Q03Z0 <= '"+end_date+"'");	   
			    sql.append(" and a0100 ='"+a0100+"'");	
			    sql.append(" and Q03Z5 in ('01','07') GROUP BY  A0100 ");	
		    }
		       
		    try
		    {
		    	dao.insert(sql.toString(), new ArrayList());
		    	sql.delete(0, sql.length());
		        String destTab=table_name;//目标表xxxxxxxxxxx
				String srcTab=userbase+"A01";//源表
				///--------修改考勤方式----------
				KqParameter para=new KqParameter(this.userView,"",this.getFrameconn());
			    HashMap hashmap =para.getKqParamterMap();
				String kq_typeField=(String)hashmap.get("kq_type");//考勤方式字段
				String kqtypeSet="";//修改考勤方式
		    	if(kq_typeField!=null&&kq_typeField.length()>0){
		    		kqtypeSet="`"+destTab+".q03z3="+srcTab+"."+kq_typeField+"";
		    		String strJoin=destTab+".A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
				    String strSet=""+destTab+".q03z3="+srcTab+"."+kq_typeField+"`"+destTab+".a0101="+srcTab+".a0101";
					//String strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".q03z5='01'  and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
					String strDWhere=destTab+".a0100='"+a0100+"' and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'";//更新目标的表过滤条件
					String strSWhere=srcTab+".a0100 ='"+a0100+"'";//源表的过滤条件 					
				    String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
				    //strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".b0110 ='"+code+"'";
					update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
					dao.update(update);
		    	}
		    	//同步日明细单位部门到月汇总
			    destTab=table_name;//目标表xxxxxxxxxxx
				srcTab="q03";//源表
				String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
				String strSet=destTab+".b0110="+srcTab+".b0110`"+destTab+".e0122="+srcTab+".e0122`"+destTab+".e01a1="+srcTab+".e01a1";//更新串  xxx.field_name=yyyy.field_namex,....
				String strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'  and "+destTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
					//String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
				String strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 = '"+end_date+"' and "+srcTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
				String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
			    strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'";				
				update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");					
				
				dao.update(update);					
				//如果同步最后一天的单位部门没有,就同步最大部门的
				 switch(Sql_switcher.searchDbServer())
				 {
					  case Constant.MSSQL:
					  {
						  srcTab=userbase+"A01";//源表
						  strSet=destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
						  strJoin=destTab+".A0100="+srcTab+".A0100";
						  strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'  and "+destTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
						  strSWhere=srcTab+".a0100 ='"+a0100+"'";//源表的过滤条件 					  	  
						  update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
						  //System.out.println(update);
						  dao.update(update);
						  break;
					  }
					  case Constant.ORACEL:
					  {
						  strSet=destTab+".b0110=Max("+srcTab+".b0110)`"+destTab+".e0122=Max("+srcTab+".e0122)`"+destTab+".e01a1=Max("+srcTab+".e01a1)`"+destTab+".a0101=Max("+srcTab+".a0101)";//更新串  xxx.field_name=yyyy.field_namex,....
						  strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
						  strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'  and "+destTab+".a0100  ='"+a0100+"'";//更新目标的表过滤条件
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
							//String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
						  strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 >= '"+start_date+"' and "+srcTab+".Q03Z0<='"+end_date+"' and "+srcTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
						  update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
					      strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'";
					      strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
					      strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
					      strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
						  strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
						  String othWhereSql=destTab+".a0100='"+a0100+"'";
						  update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);							  
						  dao.update(update);	
						  break;
					  }
					  case Constant.DB2:
					  {
						  strSet=destTab+".b0110=Max("+srcTab+".b0110)`"+destTab+".e0122=Max("+srcTab+".e0122)`"+destTab+".e01a1=Max("+srcTab+".e01a1)`"+destTab+".a0101=Max("+srcTab+".a0101)";//更新串  xxx.field_name=yyyy.field_namex,....
						  strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
						  strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'  and "+destTab+".a0100  ='"+a0100+"'";//更新目标的表过滤条件
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
						  strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
							//String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
						  strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 >= '"+start_date+"' and "+srcTab+".Q03Z0<='"+end_date+"' and "+srcTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
						  update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
					      strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'";
					      strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
					      strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
					      strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
						  strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
						  String othWhereSql=destTab+".a0100='"+a0100+"'";
						  update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);							  
						  dao.update(update);
						  break;
					  }
					  
				 }
				// 月汇总时，将dbid及a0000一块添加到q05表中
			    KqUtilsClass utils=new KqUtilsClass(this.frameconn);
	  		  	if (utils.addColumnToKq("q05")) {
	  		  		StringBuffer where = new StringBuffer();
	  		  		where.append(" where nbase='"+userbase+"'");   
	  		  		where.append(" and a0100 ='"+a0100+"'");		
	  		  		where.append(" and q03z0 ='"+kq_duration+"'");		  		  		
	  		  		utils.updateQ05(start_date, where.toString());
	  		  	}
			    
				//同步q03
				sql.delete(0, sql.length());
		        destTab="q03";//目标表xxxxxxxxxxx
				srcTab="q05";//源表
				strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
			    strSet=destTab+".q03z5="+srcTab+".q03z5";//更新串  xxx.field_name=yyyy.field_namex,....
				strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".Q03Z0 >= '"+start_date+"' and "+destTab+".Q03Z0 <= '"+end_date+"' and "+destTab+".a0100 ='"+a0100+"'";//更新目标的表过滤条件
//				strSWhere=srcTab+".nbase='"+userbase+"' and "+srcTab+".q03z0='"+kq_duration+"' and "+srcTab+".q03z5='01'  and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//源表的过滤条件  
				strSWhere=srcTab+".nbase='"+userbase+"' and "+srcTab+".q03z0='"+kq_duration+"' and "+srcTab+".a0100='"+a0100+"'";//源表的过滤条件  去掉"+srcTab+".q03z5='01'
				//strSWhere="";
			    update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
			    String othWhereSql=destTab+".a0100='"+a0100+"'";
			    //strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".b0110 ='"+code+"'";
				update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
			    //System.out.println("---"+update);
				dao.update(update);				
		    }catch(Exception e)
		    {
		    	isCorrect=false;
		    	e.printStackTrace();
		    }	
		    return isCorrect;
		   
	   }
	   /**
		  * 判断Q03是否从主集中导入指标
		  * destTab=Q03
		  * srcTab=Q05
		  * @return
		  */
		 public String getmainsql()
		 {
			 String selectSQL="";
			 StringBuffer sql = new StringBuffer();
			 ContentDAO dao = new ContentDAO(this.getFrameconn());
			 ArrayList list = new ArrayList();
			 sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
			 RowSet rowSet=null;
			 try
			 {
				 rowSet=dao.search(sql.toString());
				 while(rowSet.next())
				 {
					 ArrayList noblist = new ArrayList();
					 String itemid = rowSet.getString("itemid");
					 String itemtype = rowSet.getString("itemtype");
					 String itemdesc = rowSet.getString("itemdesc");
					 noblist.add(itemid);
					 noblist.add(itemtype);
					 noblist.add(itemdesc);
					 list.add(noblist);
				 }
				 for(int i=0;i<list.size();i++)
				 {
					 ArrayList lists=(ArrayList)list.get(i);
					 String nobitemid=(String)lists.get(0);
					 String nobitemtype=(String)lists.get(1);
					 String nobitemdesc=(String)lists.get(2);
					 sql.setLength(0);
					 sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
					 sql.append("and itemid='"+nobitemid+"' and itemtype='"+nobitemtype+"' and itemdesc='"+nobitemdesc+"'");
					 rowSet=dao.search(sql.toString());
					 while(rowSet.next())
					 {
						 String itemi = rowSet.getString("itemid");
						 if(!"A0101".equals(itemi)&&!"A0100".equals(itemi)&&!"B0110".equals(itemi)&&!"E0122".equals(itemi)&&!"E01A1".equals(itemi))
						 {
							 selectSQL+=itemi+",";
						 }
					 }
				 }
				 if(selectSQL.length()>0)
					 selectSQL=selectSQL.substring(0,selectSQL.length()-1);
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }finally
			 {
				 if(rowSet!=null)
				 {
					 try
					 {
						 rowSet.close();
					 }catch(SQLException e)
					 {
						 e.printStackTrace();
					 }
				 }
			 }
			 return selectSQL;
		 }
	   /**
	    * 断Q03中那些指标是从A01主集中取得的
	    * @param itemtype
	    * @param itemid
	    * @param itemdesc
	    * @return
	    */
	   public boolean getindexA01(String itemtype,String itemid,String itemdesc)
	   {
	   	boolean field=true;
	   	itemtype=itemtype.toUpperCase();
	   	itemid=itemid.toUpperCase();
	   	ContentDAO dao = new ContentDAO(this.getFrameconn());
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
	   /**
		  * 判断Q03是否从主集中导入指标
		  * destTab=Q03
		  * srcTab=Q05
		  * @return
		  */
		 public String getmainsql2()
		 {
			 String selectSQL="";
			 StringBuffer sql = new StringBuffer();
			 ContentDAO dao = new ContentDAO(this.getFrameconn());
			 ArrayList list = new ArrayList();
			 sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
			 RowSet rowSet=null;
			 try
			 {
				 rowSet=dao.search(sql.toString());
				 while(rowSet.next())
				 {
					 ArrayList noblist = new ArrayList();
					 String itemid = rowSet.getString("itemid");
					 String itemtype = rowSet.getString("itemtype");
					 String itemdesc = rowSet.getString("itemdesc");
					 noblist.add(itemid);
					 noblist.add(itemtype);
					 noblist.add(itemdesc);
					 list.add(noblist);
				 }
				 for(int i=0;i<list.size();i++)
				 {
					 ArrayList lists=(ArrayList)list.get(i);
					 String nobitemid=(String)lists.get(0);
					 String nobitemtype=(String)lists.get(1);
					 String nobitemdesc=(String)lists.get(2);
					 sql.setLength(0);
					 sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
					 sql.append("and itemid='"+nobitemid+"' and itemtype='"+nobitemtype+"' and itemdesc='"+nobitemdesc+"'");
					 rowSet=dao.search(sql.toString());
					 while(rowSet.next())
					 {
						 String itemi = rowSet.getString("itemid");
						 if(!"A0101".equals(itemi)&&!"A0100".equals(itemi)&&!"B0110".equals(itemi)&&!"E0122".equals(itemi)&&!"E01A1".equals(itemi))
						 {
							 selectSQL+="MAX("+itemi+"),";
						 }
					 }
				 }
				 if(selectSQL.length()>0)
					 selectSQL=selectSQL.substring(0,selectSQL.length()-1);
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }finally
			 {
				 if(rowSet!=null)
				 {
					 try
					 {
						 rowSet.close();
					 }catch(SQLException e)
					 {
						 e.printStackTrace();
					 }
				 }
			 }
			 return selectSQL;
		 }
		 /**
			 * 考月汇总导入项,如:岗位指标
			 * 
			 * @param dblist
			 * @param start_date
			 * @param end_date
			 */
			public void leadingInItemToQ05(String nbase,String a0100, String start_date,
					String end_date, String kq_month) throws GeneralException {
				StringBuffer sql = new StringBuffer();
				sql.append("select other_param,fielditemid from kq_item where ");
				sql.append(Sql_switcher.isnull("fielditemid", "'ttt'") + "<>'ttt'");
				// sql.append(" and other_param is not null");
				ContentDAO dao = new ContentDAO(this.frameconn);
				RowSet rs = null;
				try {
					rs = dao.search(sql.toString());
					String other_param = "";
					String fielditemid = "";
					while (rs.next()) {
						other_param = Sql_switcher.readMemo(rs, "other_param");
						if (other_param == null || other_param.length() <= 0)
							continue;
						fielditemid = rs.getString("fielditemid");
						SearchImportBo importBo = new SearchImportBo(other_param);
						String subset = importBo.getValue("subset");
						String setfielditemid = importBo.getValue("field");
						String begindate = importBo.getValue("begindate");
						String enddate = importBo.getValue("enddate");
						if (subset == null || subset.length() <= 0)
							continue;
						if (setfielditemid == null || setfielditemid.length() <= 0)
							continue;
						if (begindate == null || begindate.length() <= 0)
							continue;
						if (enddate == null || enddate.length() <= 0)
							continue;
						if (fielditemid == null || fielditemid.length() <= 0)
							continue;
						upLeadingInItemToQ05(fielditemid, nbase,a0100, start_date, end_date,
								kq_month);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);

				} finally {
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}

			private void upLeadingInItemToQ05(String q03fielditemid, String userbase,String a0100,
					String start_date, String end_date, String kq_month)
					throws GeneralException {
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				
					String whereIN = RegisterInitInfoData.getWhereINSql(this.userView,
							userbase);
					String destTab = "q05";// 目标表
					String srcTab = "q03";// 源表
					String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and "
							+ destTab + ".nbase=" + srcTab + ".nbase";// 关联串
					// xxx.field_name=yyyy.field_namex,....
					String strSet = destTab + "." + q03fielditemid + "=" + srcTab + "."
							+ q03fielditemid;
					;// 更新串 xxx.field_name=yyyy.field_namex,....
					String strDWhere = destTab + ".nbase='" + userbase + "' and "
							+ destTab + ".q03z0='" + kq_month + "' and "+destTab + ".a0100='" + a0100 + "'";// 更新目标的表过滤条件

					// String strSWhere=srcTab+".a0100 in(select a0100
					// "+whereIN+")";//源表的过滤条件
					// String strSWhere="exists (select a0100 "+whereIN+" and
					// q05.a0100="+nbase+"a01.a0100)";//源表的过滤条件 以前
					String strSWhere = srcTab + ".nbase='" + userbase + "' and "
							+ srcTab + ".q03z0='" + end_date + "' and "+srcTab + ".a0100='" + a0100 + "'";
					
					String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
							strJoin, strSet, strDWhere, strSWhere);
					String othWhereSql = destTab + ".a0100 ='"+a0100+"'";
					update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update,
							strDWhere, othWhereSql);
					// System.out.println(update);
					try {
						dao.update(update);
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL: {

						StringBuffer sql = new StringBuffer();
						sql.append("update q05 set q05." + q03fielditemid + " = qq."
								+ q03fielditemid + " from");
						sql.append(" q05 left join (");
						sql.append(" select a0100,q03z0,nbase,max(" + q03fielditemid
								+ ") " + q03fielditemid + " from q03");
						sql.append(" where q03.nbase='" + userbase
								+ "' and q03.q03z0>='" + start_date
								+ "' and q03.q03z0<='" + end_date + "'");
						 sql.append(" and q03.a0100='"+a0100+"'");       
						

						sql.append(" group by a0100,q03z0,nbase");
						sql.append(") qq");
						sql.append(" on q05.A0100=qq.A0100 and q05.nbase=qq.nbase ");
						sql.append(" where q05.nbase='" + userbase
								+ "' and q05.q03z0='" + kq_month+"'");
						sql.append(" and q05.a0100='"+a0100+"'"); 
						 //System.out.println(sql);
						try {
							dao.update(sql.toString());
						} catch (Exception e) {
							e.printStackTrace();
							throw GeneralExceptionHandler.Handle(e);
						}

						break;
					}
					case Constant.ORACEL: {

						strSet = destTab + "." + q03fielditemid + "=MAX(" + srcTab
								+ "." + q03fielditemid + ")";
						strSWhere = srcTab + ".nbase='" + userbase + "' and " + srcTab
								+ ".q03z0>='" + start_date + "' and " + srcTab
								+ ".q03z0<='" + end_date + "' and "+srcTab+".a0100='"+a0100+"'";
						
						update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
								strJoin, strSet, strDWhere, strSWhere);
						othWhereSql = destTab + ".a0100='"+a0100+"'";
						update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin,
								update, strDWhere, othWhereSql);
						// System.out.println(update);
						try {
							dao.update(update);
						} catch (Exception e) {
							e.printStackTrace();
							throw GeneralExceptionHandler.Handle(e);
						}
						break;
					}
				}

				

			}
}
