package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveUnsteadyShtifTrans extends IBusiness implements KqClassConstant {

	public void execute() throws GeneralException
	{
	   String addclass=(String)this.getFormHM().get("addclass");
	   String code=(String)this.getFormHM().get("code");
	   String kind=(String)this.getFormHM().get("kind");
	   String class_Array[]=addclass.split("`");
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
		ArrayList insertlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql="";
		String flag="false";
		try
		{
			for(int i=0;i<class_Array.length;i++)
			{
				ArrayList list =new ArrayList();
				sql="select * from kq_org_dept_able_shift where class_id='"+class_Array[i]+"'";
				sql=sql+" and codesetid='"+codesetid+"' and org_dept_id='"+code+"'";
				this.frowset=dao.search(sql);
				if(!this.frowset.next())
				{
					list.add(code);
					list.add(codesetid);
					list.add(class_Array[i]);
					insertlist.add(list);
				}
				
			}
            //org_dept_id 部门编号  ，class_id  班次编号，   codesetid			
			String insert="insert into kq_org_dept_able_shift(org_dept_id,codesetid,class_id) values(?,?,?)";
			dao.batchInsert(insert,insertlist);
			flag="true";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("flag",flag);
	}

}
