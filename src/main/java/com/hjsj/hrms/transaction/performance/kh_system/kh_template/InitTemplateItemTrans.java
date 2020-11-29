package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitTemplateItemTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag=(String)this.getFormHM().get("returnflag");
			String templateid=(String)map.get("templateid");
			String t_type="1";
			RecordVo vo = new RecordVo("per_template_item");
			if(!vo.hasAttribute("kind")||!vo.hasAttribute("score")||!vo.hasAttribute("rank")||!vo.hasAttribute("rank_type"))
				throw GeneralExceptionHandler.Handle(new Exception("未找到在考核模板中定义个性项目所需的指标，请升级数据库！"));
			
			if(templateid!=null && templateid.trim().length()>0 && "~".equalsIgnoreCase(templateid.substring(0,1))) // JinChunhai 2012-08-07 如果是通过转码传过来的需解码
	        { 
				String _temp = SafeCode.decode(templateid);
				templateid = _temp.substring(1); 
	        }
			if(!this.userView.isRWHaveResource(IResourceConstant.KH_MODULE, templateid) && !this.userView.isRWHaveResource(IResourceConstant.KH_MODULE, templateid+"R") && !"-1".equals(templateid))//templateid为-1表示根节点 郭峰修改。
				throw GeneralExceptionHandler.Handle(new Exception("您没有此考核模板的资源权限!"));
			
			if("-1".equals(templateid))
			{
				t_type="0";
			}
			else
			{
		    	String subsys_id = (String)map.get("subsys_id");
	    		/**=1显示右键菜单=2不显示右键菜单*/
	    		String isVisible=(String)map.get("isVisible");
		    	KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),this.userView,isVisible,subsys_id);
		    	if("1".equals(isVisible)&&!this.userView.isRWHaveResource(IResourceConstant.KH_MODULE, templateid)){
		    		bo = new KhTemplateBo(this.getFrameconn(),this.userView,"2",subsys_id);
		    	}
		    	String planStatus="0";
		    	if(map.get("isEdit")!=null)
		    	{
		    		planStatus=(String)map.get("isEdit");
		    		map.remove("isEdit");
		    	}
		    	else
		    	{
		    		planStatus=(String)this.getFormHM().get("planStatus");
		    	}
		    	this.getFormHM().put("planStatus", planStatus);
		     	String html=bo.getObjectCardHtml(templateid,returnflag);
		    	ContentDAO dao = new ContentDAO(this.getFrameconn());
		    	String isUsed = "1";
                boolean bool=bo.templateIsUsed(templateid, dao);
		    	boolean bool3=bo.templateIsUsedByJobtile(templateid, dao);
		    	if(bool)
		    		isUsed="0";
		    	if(bool3){
		    	    isUsed="2";
                }
		    	String isHaveItem="1";
		    	boolean bool2=bo.isHaveItem(templateid);
		    	if(bool2)
		    		isHaveItem="0";
		     	String score_str = bo.getScore_str();
		     	/**status=1权重模板*/
		    	String status = bo.getStatus();
		    	RecordVo vo2 = new RecordVo("per_template");
		    	vo2.setString("template_id", templateid);
		    	String name="";
		    	if(dao.isExistRecordVo(vo2))
		    	{
		    		vo2=dao.findByPrimaryKey(vo2);
		    		name=vo2.getString("name");
		    	}
		     	this.getFormHM().put("status",status);
		    	this.getFormHM().put("score_str",score_str);
		    	this.getFormHM().put("isHaveItem",isHaveItem);
		    	this.getFormHM().put("isUsed",isUsed);
		    	this.getFormHM().put("tableHtml",html);
			    this.getFormHM().put("templateid",templateid);
		    	this.getFormHM().put("subsys_id",subsys_id);
		    	this.getFormHM().put("isVisible", isVisible);
		    	this.getFormHM().put("tname", name);
		    	this.getFormHM().put("returnflag", returnflag);
			}
			this.getFormHM().put("t_type", t_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
