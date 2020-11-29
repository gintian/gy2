package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
public class SaveTemplateSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String saveandcontinue=(String)this.getFormHM().get("saveandcontinue");
			String type=(String)this.getFormHM().get("type");
			/**当type=0时，templatesetid是父模板的id，当type=1时，templatesetid是要修改的模板的id*/
			String templatesetid = (String)this.getFormHM().get("parentid");
			String subsys_id =(String)this.getFormHM().get("subsys_id");
			String fname=SafeCode.decode((String)this.getFormHM().get("fname"));
			String scope = (String)this.getFormHM().get("scope");
			String validflag = (String)this.getFormHM().get("flag");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			int id=0;
			String b0110 ="HJSJ";
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("0".equals(type))//新增操作
			{
			
//				if(!this.userView.isAdmin() && !this.userView.getGroupId().equals("1"))
//				{
//					b0110 = KhTemplateBo.getyxb0110(userView, this.getFrameconn());
//				}
				String ab0110 = KhTemplateBo.getyxb0110(userView, this.getFrameconn());
				b0110 = ab0110==null|| "".equalsIgnoreCase(ab0110)?"HJSJ":ab0110;
				RecordVo vo = new RecordVo("per_template_set");
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				id = new Integer(idg.getId("per_template_set.template_setId")).intValue();
			   // id = bo.getMaxId("per_template_set", "template_setid");
			    vo.setInt("template_setid",id);
			    vo.setString("name",fname);
			    if(!"root".equalsIgnoreCase(templatesetid))
			    {
	    		    vo.setString("parent_id", templatesetid);
			    }
			    vo.setString("b0110",b0110);
			    vo.setString("validflag",validflag);
			    vo.setString("subsys_id",subsys_id);
			    if(!"root".equalsIgnoreCase(templatesetid))
			    {
			    	  bo.setChild_id(id+"", templatesetid);
			    }
			    vo.setInt("scope", Integer.parseInt(scope));
			    dao.addValueObject(vo);
				StringBuffer context = new StringBuffer();
				context.append("新增模板分类：【"+fname+"】<br>");
				context.append("有效标识："+validflag+"(0:无效；1:有效)<br>");
				this.getFormHM().put("@eventlog", context.toString());
			    /*
			    if(!(this.userView.isSuper_admin())&&!this.userView.getGroupId().equals("1"))
				{
					UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
					user_bo.saveResource(id+"",this.userView,IResourceConstant.KH_MODULE);
				}*/
			   

			}
			else// edit opt
			{
				RecordVo vo = new RecordVo("per_template_set");
				vo.setInt("template_setid", Integer.parseInt(templatesetid));
				b0110=vo.getString("b0110");
				RecordVo avo = dao.findByPrimaryKey(vo);
				avo.setString("name", fname);
				avo.setString("validflag",validflag);
				avo.setString("scope",scope);
				dao.updateValueObject(avo);
				StringBuffer context = new StringBuffer();
				context.append("修改模板分类：【"+fname+"】<br>");
				context.append("有效标识："+validflag+"(0:无效；1:有效)<br>");
				this.getFormHM().put("@eventlog", context.toString());
			}
			this.getFormHM().put("name",SafeCode.encode(fname));
			this.getFormHM().put("id", "0".equals(type)?(id+""):templatesetid);
			this.getFormHM().put("subsys_id", subsys_id);
			this.getFormHM().put("type",type);
			this.getFormHM().put("b0110",b0110);
			this.getFormHM().put("isClose",saveandcontinue);
			this.getFormHM().put("isrefresh","2");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
