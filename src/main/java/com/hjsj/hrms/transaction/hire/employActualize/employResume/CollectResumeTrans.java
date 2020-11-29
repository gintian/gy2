package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CollectResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String a0100 = (String)this.getFormHM().get("a0100");
			String nbase = (String)this.getFormHM().get("nbase");
			String[] a0100s = PubFunc.hireKeyWord_filter_reback(a0100).split("#");
			HashSet set = new HashSet();
			for(int i=0;i<a0100s.length;i++)
			{
			    if(a0100s[i].trim().length()>0){
                    if(!a0100s[i].matches("^\\d+$"))//如果人员id未加密则不需要解密    chenxg  2015-07-22
                        set.add(PubFunc.decrypt(a0100s[i]));
                    else
                        set.add(a0100s[i]);
                }
			}
			
			ArrayList sqlParams = new ArrayList();
			ArrayList recordList = new ArrayList();
			StringBuffer whl = new StringBuffer("");
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String a_a0100 = (String)t.next();
				RecordVo vo = new RecordVo("zp_resume_pack");
				vo.setString("logonname",this.getUserView().getUserId());
				vo.setString("nbase",nbase);
				vo.setString("a0100",a_a0100);
				vo.setInt("status",this.getUserView().getStatus());
				
				recordList.add(vo);
				whl.append(" or a0100=?");
				sqlParams.add(a_a0100);
			}
			dao.delete("delete from zp_resume_pack where logonname='"+this.getUserView().getUserId()+"' and ( "+whl.substring(3)+" ) ",sqlParams);
			dao.addValueObject(recordList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}

}
