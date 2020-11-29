package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.PickUpOperationData;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 个人业务处理，统计
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 26, 2006:2:07:37 PM</p>
 * @author sx
 * @version 1.0
 *
 */

public class SingPickDataTrans extends IBusiness {
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
    	start_date=start_date+" "+"00:00:00";
    	vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
   	    String end_date=vo_date.getDataValue();
   	    end_date=end_date+" "+"23:59:59";
   	    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);		
 	    ArrayList columnlist= new ArrayList();
 	    for(int i=0;i<fielditemlist.size();i++)
 	    {
	        FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	        if("N".equals(fielditem.getItemtype()))
	        {   		   
		       columnlist.add(fielditemlist.get(i));
		    }				
	     } 	
 	     PickUpOperationData pickOperation= new PickUpOperationData(this.getFrameconn(),this.userView);
 	     String kqTempTable=pickOperation.creat_KqTmp_Table();
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
         		end_date=end_dd+" "+"23:59:59";
         	}
         	KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,b0110,this.getFrameconn());  
		    String kq_type=kq_paramter.getKq_type();	
		    ArrayList timelist= KQRestOper.getOneWorkTiem(kq_paramter.getWhours());
		    if(timelist==null||timelist.size()<=0)
		    {
		    	  String error_message=ResourceFactory.getProperty("kq.register.time.nosave");	
		 		  this.getFormHM().put("error_message",error_message);
		 	      this.getFormHM().put("error_return",this.error_return);  
		 	      this.getFormHM().put("error_flag","2");
		 	      this.getFormHM().put("error_stuts","1");
		 	      return;	
		    	//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.time.nosave"),"",""));
		    }					   
		   	    //计算考勤期间业务表	
			ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
			String rest_date=restList.get(0).toString();
			String rest_b0110=restList.get(1).toString();
			String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,nbase);	
			pickOperation.kq_Q15(nbase,a0100,b0110,start_date,end_date,whereIN,columnlist,rest_date,timelist,rest_b0110,kqTempTable,kq_type);
			pickOperation.kq_Q13(nbase,a0100,b0110,start_date,end_date,whereIN,columnlist,rest_date,timelist,rest_b0110,kqTempTable,kq_type);
			pickOperation.kq_Q11(nbase,a0100,b0110,start_date,end_date,whereIN,columnlist,rest_date,timelist,rest_b0110,kqTempTable,kq_type);
		}
		 pickOperation.pickUPTemp(kqTempTable,columnlist);//把提取业务临时表里的数据返回给日明细表
		 pickOperation.dropOperationTable(kqTempTable);
		 this.getFormHM().put("error_flag","0");
		 this.getFormHM().put("error_stuts","0");		
	}
   
}
