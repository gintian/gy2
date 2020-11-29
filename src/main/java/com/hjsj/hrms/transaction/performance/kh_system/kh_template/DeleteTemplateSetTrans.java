package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

/**
 * <p>Title:DeleteTemplateSetTrans.java</p>
 * <p>Description>:DeleteTemplateSetTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-7 下午06:53:30</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class DeleteTemplateSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String id = (String)this.getFormHM().get("id");
			/**kind=0是对模板分类操作，=1是对模板造作*/
			String kind =(String)this.getFormHM().get("kind");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String msg="0";
			if("0".equals(kind))
			{
				HashMap hm = new HashMap();
				hm =bo.getTemplateSetById(id);
				String unit = (String)hm.get("b0110");
				if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()) && !"HJSJ".equalsIgnoreCase(unit)){
					if(!(unit.length()>KhTemplateBo.getyxb0110(userView, this.getFrameconn()).length()?unit.substring(0, KhTemplateBo.getyxb0110(userView, this.getFrameconn()).length()):unit).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, this.getFrameconn()))){
						msg = "您没有该模板分类的编辑权限！";
						this.getFormHM().put("msg",msg);
						return;
					}
				}
				boolean flag = bo.isHaveUsedTemplate(id, dao);
				if(flag)//有模板被使用，不能删除
				{
					msg = ResourceFactory.getProperty("label.kh.template.nodelset");
				}
				else
				{
					StringBuffer context = new StringBuffer();
					context.append("删除模板分类：");
					String sql = "select name from per_template_set where UPPER(template_setid) in ('"+id.toUpperCase()+"')";
					RowSet rs = dao.search(sql);
					while(rs.next()){
						context.append(rs.getString("name")+",");
					}
					if(context.length()>0){
						this.getFormHM().put("@eventlog", context.toString());
					}
					
					bo.deleteTemplateSet(id, dao);
				}
			}
			else
			{
			//	HashMap hm = new HashMap();
			//	hm =bo.getTemplateSetById(id);
				if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId())){
					if(!userView.isRWHaveResource(IResourceConstant.KH_MODULE,id)){
						msg = "您没有该模板的可写权限！";
						this.getFormHM().put("msg",msg);
						return;
					}
				}
				boolean flag = bo.templateIsUsed(id, dao);
				if(flag)//改模板已经被使用
				{
					msg=ResourceFactory.getProperty("label.kh.template.nodel");
				}
				else
				{
					StringBuffer context = new StringBuffer();
					context.append("删除模板：");
					String sql = "select name from per_template where UPPER(template_id)='"+id.toUpperCase()+"'";
					RowSet rs = dao.search(sql);
					while(rs.next()){
						context.append(rs.getString("name")+",");
					}
					if(context.length()>0){
						this.getFormHM().put("@eventlog", context.toString());
					}
					
					bo.deleteTemplate(id, dao);
				}
			}
			this.getFormHM().put("msg",msg);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
