/**
 * 
 */
package com.hjsj.hrms.transaction.browse;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Owner
 *
 */
public class CheckPostDescTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
	      this.getFormHM().put("ishavepostdesc","false");
    	  RecordVo constant_vo=ConstantParamter.getRealConstantVo("ZP_POS_TEMPLATE",this.getFrameconn());/*获得显示的职位说明书的tabid*/
		  if(constant_vo!=null)
		  {
			  if(!"#".equals(constant_vo.getString("str_value"))&&!"".equals(constant_vo.getString("str_value"))){
				  int tabid=Integer.parseInt(constant_vo.getString("str_value")!=null?constant_vo.getString("str_value"):"1");			
				  if(tabid!=0){
  		    	 //String queryflag=constant_vo.getString("type")!=null?constant_vo.getString("type").toString():"0";
					  this.getFormHM().put("ishavepostdesc","true");
				  }else
				  {
					  this.getFormHM().put("ishavepostdesc","false"); 
				  }
			  }
		  }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		     throw GeneralExceptionHandler.Handle(e);
		 }
		

	}
}

