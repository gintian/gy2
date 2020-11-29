package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class ImpInterviewRecordTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String tab_id=(String)this.getFormHM().get("tab_id");
			String objectid=(String)this.getFormHM().get("objectid");
			objectid = PubFunc.decrypt(objectid);
			String pre=(String)this.getFormHM().get("pre");
			String planid=(String)this.getFormHM().get("planid");
			planid = PubFunc.decrypt(planid);
			ArrayList a0100list=new ArrayList();
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
			
			String strDesT=this.userView.getUserName()+"templet_"+tab_id;
			
			tablebo.createTempTemplateTable(this.userView.getUserName());			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			RecordVo vo=new RecordVo("per_plan");
			vo.setInt("plan_id",Integer.parseInt(planid));
			vo=dao.findByPrimaryKey(vo);
			if(vo.getInt("object_type")==1||vo.getInt("object_type")==3||vo.getInt("object_type")==4)
			{
				RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"' and body_id=-1");
				if(rowSet.next())
					objectid=rowSet.getString("mainbody_id");
			}
			
			
			this.frowset=dao.search("select * from "+pre+"A01 where a0100='"+objectid+"'");
			if(this.frowset.next())
			{
				
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("此考核对象已不在当前人员库，操作失败!"));
			
			
			a0100list.add(objectid);
			
			
			dao.update("delete from "+strDesT+" where a0100!='"+objectid+"'");
			RowSet rowSet=dao.search("select * from "+strDesT+" where a0100='"+objectid+"' and upper(basepre)='"+pre.toUpperCase()+"'");
			if(rowSet.next())
			{
				
			}
			else
			{
				tablebo.impDataFromArchive(a0100list,pre);
				dao.update("update "+strDesT+" set submitflag=1 where  a0100='"+objectid+"' and upper(basepre)='"+pre.toUpperCase()+"'");
			}
			this.getFormHM().put("tab_id",tab_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
