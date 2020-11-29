package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SingCountInfoTrans extends IBusiness {
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
 	    ArrayList columnlist= new ArrayList();
 	    for(int i=0;i<fielditemlist.size();i++)
 	    {
	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	          if("N".equals(fielditem.getItemtype()))
	          {   		   
		        columnlist.add(fielditem);
		      }				
	    }  
 	   ArrayList kq_target_list=getTargetList();
 	    for(int i=0;i<opinlist.size();i++)
        {
			
			SingOpinVo rec=(SingOpinVo)opinlist.get(i);   
       	    String nbase=rec.getNbase();
          	String a0100=rec.getA0100();
       	    String b0110=rec.getB0110();         	
           	String end_dd=rec.getQ03z0();
       	   if(end_dd!=null&&end_dd.length()==10)
       	   {
       		   end_dd=end_dd.replaceAll("-","\\.");
       		   end_date=end_dd;
       	   }
           
           countKQInfo(nbase,a0100,b0110,start_date,end_date,kq_target_list,columnlist);
		   	    //计算考勤期间业务表	
			
			
	   }
 	   this.getFormHM().put("error_stuts","0");
 	   this.getFormHM().put("error_flag","0");
 	 
	}
	public void countKQInfo(String nbase,String a0100,String b0110,String start_date,String end_date , ArrayList kq_target_list,ArrayList columnlist)throws GeneralException
	{
		    
	      KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,b0110,this.getFrameconn());  
		  ArrayList timelist= KQRestOper.getOneWorkTiem(kq_paramter.getWhours());
		  float work_time=KQRestOper.getWork_Time(timelist);
		  ArrayList filedlist=getNewFileds(columnlist,work_time);	
		  oneCountKQ(start_date,end_date,a0100,nbase,b0110,"2",kq_target_list,filedlist);	 
						   		     
				
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
    public void oneCountKQ(String start_date,String end_date, String a0100,String userbase,String code,String kind,ArrayList kq_target_list,ArrayList filedlist)throws GeneralException
    {
    	
    		
         	ArrayList SQLlist=new ArrayList();
        	ArrayList valueList= new ArrayList();        	
        	try
            {
        	    for(int i=0;i<kq_target_list.size();i++)
                {
                	
                	HashMap onemap=(HashMap)kq_target_list.get(i);
                	String fielditemid=onemap.get("fielditemid").toString();
                	String c_expr=onemap.get("c_expr").toString();
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
    public void curCountKQ(String start_date,String end_date,String code,String kind,ArrayList kq_target_list,ArrayList columnlist)throws GeneralException
    {
    	KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,code,this.getFrameconn());  
	    ArrayList timelist= KQRestOper.getOneWorkTiem(kq_paramter.getWhours());
	    float work_time=KQRestOper.getWork_Time(timelist);
	    ArrayList filedlist=getNewFileds(columnlist,work_time);	
    	for(int r=0;r<this.userView.getPrivDbList().size();r++)
	 	    {
	 	    	String userbase=this.userView.getPrivDbList().get(r).toString();	 	    
	 	        String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);	 	        
	 	        oneCountKQ(start_date,end_date,whereIN,userbase,code,kind,kq_target_list,filedlist);
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
 
  
    public ArrayList getNewFileds(ArrayList oldFileds,float work_time)
    {
    	 ArrayList fileds=oldFileds;
    	 FieldItem fielditem=new FieldItem();
		 fielditem.setFieldsetid("Q03");
		 fielditem.setItemdesc(ResourceFactory.getProperty("kq.item.worktime"));
		 fielditem.setItemid(work_time+"");
		 fielditem.setItemtype("N");		 
		 fielditem.setDecimalwidth(2);	
		 //fielditem.setVar();
		 fileds.add(fielditem);
		 return fileds;
    }
    public  int getLXtype(ArrayList columnlist,String fielditemid)
	{
		int lxtype=YksjParser.INT;		
		for(int r=0;r<columnlist.size();r++)
 		{
 	   	   FieldItem fielditem=(FieldItem)columnlist.get(r); 	   	   
 	   	   if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
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
 		}
		return lxtype;
	} }
