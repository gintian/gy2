package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddDefinitionTypeTrans extends IBusiness {

	public void execute() throws GeneralException {

		String fne=(String)this.getFormHM().get("type");
		String fid=(String)this.getFormHM().get("typeid");
		String sta=(String)this.getFormHM().get("sel");
		if(sta==null|| "".equals(sta)){
			sta="0";
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			  RecordVo vo=new RecordVo("ds_key_factortype");
              if(fid==null|| "".equals(fid)){
            	  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
                  String tid=idg.getId("ds_key_factortype.typeid");
                  vo.setString("typeid",tid);
                  vo.setString("name",fne); 
                  vo.setString("status",sta);
                  
	              dao.addValueObject(vo);
	             
    	    	}else{
    	    		
                  vo.setString("typeid",fid);
                  vo.setString("name",fne); 
                  vo.setString("status",sta);
    	   		  dao.updateValueObject(vo);		
    	    	}
	  }catch(Exception exx){
	       exx.printStackTrace();
	       throw GeneralExceptionHandler.Handle(exx);
	  }finally{
          this.getFormHM().put("type",null);
          this.getFormHM().put("typeid","");
      }

	}

}
