package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.Sms_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

public class ImportInterfaceParamTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		
		List list1=null;
		
		    Sms_Parameter sparam=new Sms_Parameter(this.getFrameconn());
		    
		    try {
		    	list1= sparam.ImportCommPort();
		    } catch (Exception e){
		    	e.printStackTrace();
		    	throw GeneralExceptionHandler.Handle(new Exception(e.getMessage()));
		    }
		    
		    try
			{
		    if(list1.size()>1){
				for(int i=0;i<list1.size();i++){
					for(int j=1;j<list1.size();j++){
						LazyDynaBean bean1=(LazyDynaBean)list1.get(i);
						LazyDynaBean bean2=(LazyDynaBean)list1.get(j);
						int b1=0;
						int b2=0;
						
						if(bean1.get("com").toString().indexOf("COM")!=-1)
						{
						    b1=Integer.parseInt(bean1.get("com").toString().substring(3, bean1.get("com").toString().length()));
						    b2=Integer.parseInt(bean2.get("com").toString().substring(3, bean2.get("com").toString().length()));
						}
						else if(bean1.get("com").toString().indexOf("ttyS")!=-1)
						{
							b1=Integer.parseInt(bean1.get("com").toString().substring(4, bean1.get("com").toString().length()));
							b2=Integer.parseInt(bean2.get("com").toString().substring(4, bean2.get("com").toString().length()));
						}
						
						if(b1 > b2){
							LazyDynaBean bean3=new LazyDynaBean();
							bean3=(LazyDynaBean)list1.get(i);
							list1.remove(i);
							list1.add(bean3);
						}
					}
				}
			}
		   
		    this.getFormHM().put("ywList", list1);
		} catch (Exception ex){
  			ex.printStackTrace();
  		}
	}
}
	


