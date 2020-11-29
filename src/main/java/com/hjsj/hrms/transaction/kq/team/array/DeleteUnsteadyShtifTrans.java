package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteUnsteadyShtifTrans  extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException
	{
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedlist");
        if(selectedlist==null||selectedlist.size()<=0)
        {
        	return;
        }
        String code=(String)this.getFormHM().get("code");
 	    String kind=(String)this.getFormHM().get("kind");
        String codesetid="";
		if("2".equalsIgnoreCase(kind))
		{
			codesetid="UN";
		}else if("1".equalsIgnoreCase(kind))
		{
			codesetid="UM";
		}else if("0".equalsIgnoreCase(kind))
		{
			codesetid="@K";
		}else
		{
			codesetid="UN";
		}
        ArrayList dellist=new ArrayList();
        for(int i=0;i<selectedlist.size();i++)
        {
        	ArrayList list=new ArrayList();
        	RecordVo vo=(RecordVo)selectedlist.get(i);
        	list.add(code);
        	list.add(vo.getString("class_id"));
        	list.add(codesetid);
        	dellist.add(list);
        }
        
        String del="delete from kq_org_dept_able_shift where org_dept_id=? and class_id=? and codesetid=?";
        try
        {
        	ContentDAO dao=new ContentDAO(this.getFrameconn());
        	dao.batchUpdate(del,dellist);
        }catch(Exception e)
        {
           e.printStackTrace();	
        }
	}
}
