package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SaveKqBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		String[] mes=(String[])this.getFormHM().get("messi");
	    ArrayList sList=(ArrayList)this.getFormHM().get("slist");
        ArrayList sellist=(ArrayList)this.getFormHM().get("selist"); 
		
       
        
        
		StringBuffer stbs=new StringBuffer();
		if(mes!=null)
		{
			for(int n=0;n<mes.length;n++)
			{
				stbs.append(mes[n]);
				stbs.append(",");
			}
		}
		for(int i=0;i<sList.size();i++)
        {
           CommonData data=(CommonData)sList.get(i);
           String dbpre=data.getDataValue();
          if(stbs.indexOf(dbpre)!=-1)
              sellist.set(i,"1");
          else
              sellist.set(i,"0");                    
        }
		
		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
		String b0110=managePrivCode.getPrivOrgId();  
		KqParameter para=new KqParameter(this.getFormHM(),this.userView,"UN"+b0110,this.getFrameconn());
		
		para.setNbase(stbs.toString());
		
		this.getFormHM().put("sige","1");

	}

}
