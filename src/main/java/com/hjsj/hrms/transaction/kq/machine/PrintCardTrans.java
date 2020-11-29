package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.ExcelCard;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class PrintCardTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
	    try {
    	    String a_code=(String)this.getFormHM().get("a_code");
    	    //zxj 20150825 安全漏洞
            if (null != a_code && !"".equals(a_code)
                    && !a_code.startsWith("UN") && !a_code.startsWith("UM") && !a_code.startsWith("@K") 
                    && !a_code.startsWith("GP") && !a_code.startsWith("EP"))
                a_code = PubFunc.decryption(a_code);
            
    	    String filter_start_date = (String)this.getFormHM().get("filter_date_s");
    	    String start_date = filter_start_date == null || filter_start_date.length() <= 0 ?
    	    		(String)this.getFormHM().get("start_date") : filter_start_date;
    	    
    	    String filter_start_hh = (String)this.getFormHM().get("filter_hh_s");
    	    String start_hh = filter_start_hh == null || filter_start_hh.length() <= 0 ?
    	    		(String)this.getFormHM().get("start_hh") : filter_start_hh;
    	    
    	    String filter_start_mm = (String)this.getFormHM().get("filter_mm_s");
    	    String start_mm = filter_start_mm == null || filter_start_mm.length() <= 0 ? 
    	    		(String)this.getFormHM().get("start_mm") : filter_start_mm;
    	    
    	    String filter_end_date = (String)this.getFormHM().get("filter_date_e");
    	    String end_date = filter_end_date == null || filter_end_date.length() <= 0 ? 
    	    		(String)this.getFormHM().get("end_date") : filter_end_date;
    	    
    	    String filter_end_hh = (String)this.getFormHM().get("filter_hh_e");
    	    String end_hh = filter_end_hh == null || filter_end_hh.length() <= 0 ? 
    	    		(String)this.getFormHM().get("end_hh") : filter_end_hh;
    	    
    	    String filter_end_mm = (String)this.getFormHM().get("filter_mm_e");
    	    String end_mm = filter_end_mm == null || filter_end_mm.length() <= 0 ? 
    	    		(String)this.getFormHM().get("end_mm") : filter_end_mm;
    	    
    	    String select_name=(String)this.getFormHM().get("select_name");
    	    String select_pre =(String)this.getFormHM().get("select_pre");//人员库
    	    String select_type = (String)this.getFormHM().get("select_type");
    	    String sp_flag =(String)this.getFormHM().get("sp_flag");//审批
    	    String into_flag =(String)this.getFormHM().get("into_flag");//进出
    	    String iscommon=(String)this.getFormHM().get("iscommon");//是否正常签到
    	    String datafrom = (String)this.getFormHM().get("datafrom"); //1:补卡 0：正常
    	    //String nbase=(String)this.getFormHM().get("nbase");
    	    
    	    /****得到人员库****/
    	    ArrayList kq_dbase_list=new ArrayList();
    	    String codeid="";
    	    if(a_code!=null&&a_code.length()>2)
        	{
        		codeid=a_code.substring(2);
        	}
        	if(codeid!=null&&codeid.length()>0)
    		{
        			if(a_code.indexOf("UN")!=-1)
        			{
        				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),codeid);
        			}else if(a_code.indexOf("UM")!=-1||a_code.indexOf("@K")!=-1)
        			{
        				String b0110=codeid;
        				String codesetid=codeid;
        	        	do
        	        	{
        	        		String codeset[]=RegisterInitInfoData.getB0100(b0110,this.getFrameconn());
        	        		if(codeset!=null&&codeset.length>=0)
        	            	{
        	            		codesetid=codeset[0];
        	            		b0110=codeset[1];
        	            	}
        	        	}while(!"UN".equals(codesetid));
        	        	kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),b0110);
        			}
        			else if(a_code.indexOf("EP")!=-1)
        			{
        				String  nbase=(String)this.getFormHM().get("nbase");
        				ArrayList list=new ArrayList();
        				list.add(nbase);
        				kq_dbase_list=list;
        			}
    		}else{
    			 kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn()); 
    		}    
        	if(start_date==null||start_date.length()<=0)
    			start_date=PubFunc.getStringDate("yyyy.MM.dd");	
        	else
        		start_date=start_date.replaceAll("-","\\.");
    		if(end_date==null||end_date.length()<=0)
    			end_date=PubFunc.getStringDate("yyyy.MM.dd");	
    		else
    			end_date=end_date.replaceAll("-","\\.");
            if(start_hh==null||start_hh.length()<=0)
    		   start_hh="00";
    	    if(start_mm==null||start_mm.length()<=0)
    		   start_mm="00";		
    	    if(end_hh==null||end_hh.length()<=0)
    		   end_hh="23";
         	if(end_mm==null||end_mm.length()<=0)
    		   end_mm="59";
    	    String start_time=start_hh+":"+start_mm;
    	    String end_time=end_hh+":"+end_mm;
    	    KqCardData kqCardData=new KqCardData(this.userView,this.getFrameconn());
    	    boolean isInout_flag=kqCardData.isViewInout_flag();
        	ArrayList fielditemlist=kqCardData.machineDataFieldlist(isInout_flag);
        	KqUtilsClass kqUtilsClass = new KqUtilsClass();
    		KqParameter para = new KqParameter(this.userView, "", this.frameconn);
    		HashMap hashmap = para.getKqParamterMap();
    		String g_no = (String) hashmap.get("g_no");
        	String where_in = "";
        	if ("0".equals(select_type)) {
    			where_in += kqUtilsClass.getWhere_C("1", "a0101", select_name);
    		} else if ("1".equals(select_type)) {
    			where_in += kqUtilsClass.getWhere_C("1", g_no, select_name);
    		} else if("2".equals(select_type)){
    			where_in += kqUtilsClass.getWhere_C("1", "card_no", select_name);
    		}
    	    String where=kqCardData.getSQLWhere(kq_dbase_list,a_code,start_date,end_date,start_time,end_time,where_in,select_pre,into_flag,iscommon,sp_flag,datafrom);        
            String column=kqCardData.getKq_originality_column2();
            ExcelCard educeExcel=new ExcelCard(this.getFrameconn());
    //        String name = this.userView.getUserName();
    		String excelfile=educeExcel.creatExcel(column,where,fielditemlist,this.userView);
    		//xiexd 2014.09.12 加密文件名
    		excelfile = PubFunc.encrypt(excelfile);
    		this.getFormHM().put("excelfile",excelfile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
