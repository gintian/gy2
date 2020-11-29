/*
 * Created on 2005-6-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchDoubleDataTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	
		String userbase=(String)this.getFormHM().get("userbase");
		String statId=(String)this.getFormHM().get("statid");
		String querycond=(String)this.getFormHM().get("querycond");
		String infokind=(String)this.getFormHM().get("infokind");
		String home=(String)this.getFormHM().get("home");
		String backdates = (String)this.getFormHM().get("backdates");
		String backdate = (String)this.getFormHM().get("backdate");
		if(backdates.length()>10){
	    	this.getFormHM().put("backdates", backdate);
	    	backdates=backdate;
		}
		String allbackdates = (String)this.getFormHM().get("allbackdates");
		String[] tmp =allbackdates.split(",");
		String html="";  
		String filename="";
		ArrayList backdateslist = new ArrayList();
		try{
			StatDataEncapsulation simplestat=new StatDataEncapsulation();
			String[] tt =simplestat.getDoubleData(userbase, Integer.parseInt(statId),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind, backdates);
			for(int i=0;i<tmp.length;i++){
				CommonData data = new CommonData(tmp[i],tmp[i]);
				backdateslist.add(data);
			}
			if(tt!=null&&tt.length==2){
				html=tt[0];
				filename=tt[1];
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("html", html);
			this.getFormHM().put("filename", PubFunc.encrypt(filename));
			this.getFormHM().put("backdateslist", backdateslist);
		}
	    
	
	}

}
