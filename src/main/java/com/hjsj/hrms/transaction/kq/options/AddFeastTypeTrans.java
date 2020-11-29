package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.StringTokenizer;

public class AddFeastTypeTrans extends IBusiness {
	
	private String turnDate(String str)
	{
	  String[] ter;
	  StringBuffer stg=new StringBuffer();
	  String tar=str.replaceAll("-",".");
	  
		  ter=tar.split(",");
      for(int n=0;n<ter.length;n++)
      {
    	  if(ter[n].length()>0)
    	  {
             StringTokenizer nn=new StringTokenizer(ter[n],".");
		      for(int mm=0;nn.hasMoreTokens();mm++)
		     {
		    	int tok=0;
		    	String aa="";
			    String mmm=((String) nn.nextElement());
			    if(mmm.length()==1)
			    	aa="0"+mmm;
			    if(mmm.length()==4)
			    	aa=mmm+".";
			    if(mmm.length()==2)
			    	aa=mmm;
			    if((aa.length()==2&&mm==0)||(ter[n].length()>=8&&mm==1&&aa.length()==2))
			    	tok=tok+1;
			    if(tok==1)
			    	aa=aa+".";
			    
			    stg.append(aa);
			     
		      }
		      stg.append(",");
		  }
      }
	
		return stg.toString();
	}
	
     
	public void execute() throws GeneralException {
		String fne=(String)this.getFormHM().get("feast_name");
		String fid=(String)this.getFormHM().get("feast_id");
		String sdate=(String)this.getFormHM().get("sdate");

		String dates="";
		if(sdate!=null||!"".equals(sdate))
			 dates=this.turnDate(sdate);


		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			  RecordVo vo=new RecordVo("kq_feast");
              if(fid==null|| "".equals(fid))
  		       {
            	  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
                  String feast_id=idg.getId("kq_feast.feast_id");
                  vo.setString("feast_id",feast_id);
                  vo.setString("feast_name",fne); 
                  vo.setString("feast_dates",dates);
	              dao.addValueObject(vo);
	             
    	    	}else{
                  vo.setString("feast_id",fid);
                  vo.setString("feast_name",fne); 
                  vo.setString("feast_dates",dates);
    	   		  dao.updateValueObject(vo);		
    	    	}
	  }catch(Exception exx)
      {
	       exx.printStackTrace();
	       throw GeneralExceptionHandler.Handle(exx);
	  }finally
      {
          this.getFormHM().put("feast_id",fid);
          this.getFormHM().put("feast_name","");
      }
	}

}
