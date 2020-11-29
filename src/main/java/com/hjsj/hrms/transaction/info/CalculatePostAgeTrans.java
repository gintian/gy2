/**
 * 
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Owner
 *
 */
public class CalculatePostAgeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
//		 TODO Auto-generated method stub
		 String postdatevalue=(String)this.getFormHM().get("postdatevalue");
		 if(this.filterDate(postdatevalue))
		      	this.getFormHM().put("postagevalue", new SortFilter().getWorkAge(postdatevalue));
		 
	}
	public boolean filterDate(String date){
		boolean flag=true;
		StringBuffer sbdate=new StringBuffer();
		String[] temp=date.split("\\.");
		if(temp.length<1){
			temp=date.split("-");
		}
		if(temp.length>0){
			for(int i=0;i<temp.length;i++){
				sbdate.append(temp[i]);
			}
			try{
				String da=sbdate.toString();
				Integer in=new Integer(da);
				
			}catch(Exception e){
				flag=false;
			}
		}else{
			flag=false;
		}
		return flag;
	}
}