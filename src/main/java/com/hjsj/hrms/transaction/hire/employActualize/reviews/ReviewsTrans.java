package com.hjsj.hrms.transaction.hire.employActualize.reviews;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewsTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String  person_type=(String)hm.get("person_type");  // 0:应聘库  1：人才库
			ArrayList levelList=new ArrayList();
			String personid="";
			String level="";
			String content="";
			String info_id="";
			String title="";
			if(hm.get("personid")!=null)
			{
				personid=(String)hm.get("personid");
				hm.remove("personid");
			}
//			if(person_type.equals("1"))
//			{
				ParameterXMLBo bo=new ParameterXMLBo(this.getFrameconn(),"1");
				HashMap  map=bo.getAttributeValues();
				String   codesetid=(String)map.get("resume_level");
				if(codesetid==null|| "".equals(codesetid))
					throw GeneralExceptionHandler.Handle(new Exception("没有设置评语等级代码！"));
				levelList=getLevelList(codesetid);
//			}
			if(personid!=null&&personid.trim().length()>0)
			{
				this.frowset=dao.search("select * from zp_comment_info where UPPER(comment_user)='"+this.getUserView().getUserFullName()+"' and a0100='"+personid+"'");
				while(this.frowset.next())
				{
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						level=this.frowset.getString("level_o");
					}
					else
					{
						level=this.frowset.getString("level");
					}
					content=Sql_switcher.readMemo(this.frowset, "content");
					info_id=this.frowset.getString("info_id");
					title=this.frowset.getString("title");
				}
			}
			this.getFormHM().put("title","");
			this.getFormHM().put("content","");
			this.getFormHM().put("levelList",levelList);
			this.getFormHM().put("person_type",person_type);
			this.getFormHM().put("level", "");
			this.getFormHM().put("info_id", info_id);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
			
	}
	
	
	public ArrayList getLevelList(String codesetid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from codeitem where codesetid='"+codesetid+"'");
			while(this.frowset.next())
			{
				CommonData data1=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				list.add(data1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	

}
