package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

public class SaveTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		try
		{
			String opt=(String)this.getFormHM().get("opt");
			String type=(String)this.getFormHM().get("type");
		    if("1".equals(opt))//对模板id进行检查
		    {
		    	String templateid=(String)this.getFormHM().get("templateid");
	    		String templatename=SafeCode.decode((String)this.getFormHM().get("templatename"));
	    		String oldid=(String)this.getFormHM().get("oldid");
	    		String parentsetid=(String)this.getFormHM().get("parentsetid");
	    		KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
	    		boolean flag = bo.isHave(templateid, templatename,type,oldid,parentsetid);
	    		String msg = "0";
	    		if(!flag)//存在
	    		{
	    			msg="1";
	    		}
	    		this.getFormHM().put("msg",msg);
		    }
		    else
		    {
	    		String templateid=(String)this.getFormHM().get("templateid");
	    		String templatesetid=(String)this.getFormHM().get("templatesetid");
	    		String templatename=SafeCode.decode((String)this.getFormHM().get("templatename"));
	    		String topscore = (String)this.getFormHM().get("topscore");
	    		String status = (String)this.getFormHM().get("status");
	    		String subsys_id = (String)this.getFormHM().get("subsys_id");
	    		RecordVo vo = new RecordVo("per_template");
	    		ContentDAO dao = new ContentDAO(this.getFrameconn());
	    		if("0".equals(type))//new
	    		{
	    			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
	    			int seq = bo.getMaxId("per_template", "seq");
			    	vo.setString("template_id", templateid);
		    		vo.setInt("template_setid",Integer.parseInt(templatesetid));
		    		vo.setString("name", templatename);
		    		vo.setInt("seq",seq);
		    		vo.setDouble("topscore",Double.parseDouble(topscore));
		    		vo.setDouble("currentscore",Double.parseDouble("0.00"));
		    		vo.setInt("validflag", 1);
		    		vo.setDate("create_date", new Date());
		    		vo.setString("status", status);
		    		vo.setDate("valid_date", new Date());
		    		dao.addValueObject(vo);
					StringBuffer context = new StringBuffer();
					context.append("新增模板：【"+templateid+":"+templatename+"】<br>");
					context.append("模板类型："+status+"(0:分值；1:权重)<br>");
					context.append("总分："+topscore+"");
					this.getFormHM().put("@eventlog", context.toString());
		    		if(!(this.userView.isSuper_admin())&&!"1".equals(this.userView.getGroupId()))
					{
						UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
						user_bo.saveResource(templateid,this.userView,IResourceConstant.KH_MODULE);
					}

		    	}
		    	else//edit
	    		{
	    			/**如果是修改模板，templatesetid就是未改之前的templateid*/
	    			vo.setString("template_id",templatesetid);
	     			vo = dao.findByPrimaryKey(vo);
	     			String beforeStatus=vo.getString("status");
	    			vo.setString("template_id",templateid);
		    		vo.setString("name",templatename);
	     			vo.setDouble("topscore",Double.parseDouble(topscore));
		    		vo.setString("status", status);
		     		vo.setDate("modify_date",new Date());
		     		if(!(this.userView.isSuper_admin())&&!"1".equals(this.userView.getGroupId()))
					{
						UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
						user_bo.saveResource(templateid,this.userView,IResourceConstant.KH_MODULE);
					}
		    		dao.updateValueObject(vo);
					StringBuffer context = new StringBuffer();
					context.append("新增模板：【"+templateid+":"+templatename+"】<br>");
					context.append("模板类型："+status+"(0:分值；1:权重)<br>");
					context.append("总分："+topscore+"");
					this.getFormHM().put("@eventlog", context.toString());
		    		dao.update("update per_template_item set template_id='"+templateid+"' where UPPER(template_id)='"+templateid.toUpperCase()+"'");
		    		if(!beforeStatus.equals(status))
		    		{
		    	    	if("0".equals(beforeStatus))
		    	    	{
		    	    		dao.update("update per_template_item set score="+Double.parseDouble(topscore)+",rank=0 where UPPER(template_id)='"+templateid.toUpperCase()+"'");
		    	    		dao.update("update per_template_point set score="+Double.parseDouble(topscore)+",rank=0 where item_id in (select item_id from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"')");
		    	    	}
		    	    	/**分值变权重*/
		    	    	else
		    	    	{
		        			dao.update("update per_template_item set rank=1,score=0 where UPPER(template_id)='"+templateid.toUpperCase()+"'");
		    	    		dao.update("update per_template_point set rank=1,score=0 where item_id in (select item_id from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"')");
		    	    	}
		    		}
		    		else
		    		{
		    			/*if(beforeStatus.equals("1"))
		    			{
		    				dao.update("update per_template_item set score="+Double.parseDouble(topscore)+",rank=0 where UPPER(template_id)='"+templateid.toUpperCase()+"'");
		    	    		dao.update("update per_template_point set score="+Double.parseDouble(topscore)+",rank=0 where item_id in (select item_id from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"')");
		    			}*/
		    		}
		    		if("1".equals(status))
		    		{
		    			dao.update("update per_template_item set score="+Double.parseDouble(topscore)+" where UPPER(template_id)='"+templateid.toUpperCase()+"'");
	    	    		dao.update("update per_template_point set score="+Double.parseDouble(topscore)+" where item_id in (select item_id from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"')");

		    		}
	    	 	}
	    		this.getFormHM().put("id",templateid);
	    		this.getFormHM().put("name",SafeCode.encode(templatename));
	    		this.getFormHM().put("subsys_id",subsys_id);
	    		this.getFormHM().put("type",type);
	    		
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
