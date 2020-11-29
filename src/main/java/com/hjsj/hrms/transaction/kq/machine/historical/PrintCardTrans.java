package com.hjsj.hrms.transaction.kq.machine.historical;

import com.hjsj.hrms.businessobject.kq.machine.ExcelCard;
import com.hjsj.hrms.businessobject.kq.machine.historical.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class PrintCardTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		
	    String a_code=(String)this.getFormHM().get("a_code");	
	    String start_date=(String)this.getFormHM().get("start_date");
	    String start_hh=(String)this.getFormHM().get("start_hh");
	    String start_mm=(String)this.getFormHM().get("start_mm");
	    String end_date=(String)this.getFormHM().get("end_date");
	    String end_hh=(String)this.getFormHM().get("end_hh");
	    String end_mm=(String)this.getFormHM().get("end_mm");
	    String select_name=(String)this.getFormHM().get("select_name");
	    String select_pre =(String)this.getFormHM().get("select_pre");//人员库
	    String sp_flag =(String)this.getFormHM().get("sp_flag");//审批
	    String into_flag =(String)this.getFormHM().get("into_flag");//进出
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
	    String where=kqCardData.getSQLWhere(kq_dbase_list,a_code,start_date,end_date,start_time,end_time,select_name,select_pre,into_flag,sp_flag,datafrom);        
        String column=kqCardData.getKq_originality_column();
        ExcelCard educeExcel=new ExcelCard(this.getFrameconn());
//        String name = this.userView.getUserName();
		String excelfile=educeExcel.creatExcel(column,where,fielditemlist,this.userView);
		//xiexd 2014.09.12加密文件名
		excelfile = PubFunc.encrypt(excelfile);
		this.getFormHM().put("excelfile",excelfile);
	}

}
