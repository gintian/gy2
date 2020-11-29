package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * 
 * <p>Title:BusinessApplyTrans.java</p>
 * <p>Description>:业务申请页签</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 16, 2016 1:39:41 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class GetBusinessApplyTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{ 
			TemplateNavigationBo bo = new TemplateNavigationBo(this.frameconn,this.userView);
			String operationcode=(String) this.getFormHM().get("operationcode"); // 业务分类
		    ArrayList setlist = this.searchBusinessApplyList(operationcode);
			ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
			column = bo.getBusinessApplyColumnsInfo();

			TableConfigBuilder builder = new TableConfigBuilder("businessapply", column, "businessapply1", userView,this.getFrameconn());
			builder.setDataList(setlist);
//			builder.setSelectable(true);
//			builder.setColumnFilter(true);
			builder.setPageSize(20);
			builder.setTableTools(new ArrayList());
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 
	 * @Title: searchBusinessApplyList   
	 * @Description:查询业务申请列表
	 * @param @param operationcode 业务分类
	 * @param @return
	 * @param @throws GeneralException 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList searchBusinessApplyList(String operationcode) throws GeneralException
	{
		UserView userView = this.getUserView();
		//业务用户关联自助用户 按自助用户走
		if(userView.getS_userName()!=null&&userView.getS_userName().length()>0&&userView.getStatus()==0&&userView.getBosflag()!=null){
			userView=new UserView(userView.getS_userName(), userView.getS_pwd(), this.getFrameconn());
			try {
				userView.canLogin();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		/* 模块ID
		 * 1、人事异动
		 * 2、薪资管理
		 * 3、劳动合同
		 * 4、保险管理
		 * 5、出国管理
		 * 6、资格评审
		 * 7、机构管理
		 * 8、岗位管理
		 * 9、业务申请（自助）
		 * 10、考勤管理
		 * 11、职称评审
		*/	
		String module_id=(String)this.getFormHM().get("module_id");
		String tabids="";
		//业务申请包含所有模板
		/*
		if(module_id!=null&&!module_id.equals("10"))
		{
			TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
	    	tabids=tp.getAllDefineKqTabs(0); 
			if(tabids.length()==0)
				tabids+=",-1000"; 
		}*/
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		String _static="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
		strsql.append("select a.TabId,a.Name,a.operationcode,b.operationname,b."+_static+" "+_static+",'0' ins_id from ");
		strsql.append("template_table a ,operation b where a.operationcode=b.operationcode ");
		strsql.append("and b.operationtype <> 0 ");
		if(operationcode!=null&&operationcode.trim().length()>0)
		{
			strsql.append(" and a.operationcode like ？ "); 
		}
		strsql.append(" ORDER BY tabid");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if(operationcode!=null&&operationcode.trim().length()>0)
				this.frowset = dao.search(strsql.toString(),Arrays.asList(new String[]{operationcode+"%"}));
			else
				this.frowset = dao.search(strsql.toString());
			while (this.frowset.next())
		    { // 权限控制
			   if(this.frowset.getString(_static)!=null&& "1".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.RSBD, this.frowset.getString("tabid")))
			        continue;
			   if(this.frowset.getString(_static)!=null&& "2".equals(this.frowset.getString(_static)))
			          if (!userView.isHaveResource(IResourceConstant.GZBD, this.frowset.getString("tabid")))
				        continue;
			   if(this.frowset.getString(_static)!=null&& "8".equals(this.frowset.getString(_static)))
			          if (!userView.isHaveResource(IResourceConstant.INS_BD, this.frowset.getString("tabid")))
				        continue;
			   if(this.frowset.getString(_static)!=null&& "3".equals(this.frowset.getString(_static)))
			          if (!userView.isHaveResource(IResourceConstant.PSORGANS, this.frowset.getString("tabid")))
				        continue;
			   if(this.frowset.getString(_static)!=null&& "4".equals(this.frowset.getString(_static)))
			          if (!userView.isHaveResource(IResourceConstant.PSORGANS_FG, this.frowset.getString("tabid")))
				        continue;
			   if(this.frowset.getString(_static)!=null&& "5".equals(this.frowset.getString(_static)))
			          if (!userView.isHaveResource(IResourceConstant.PSORGANS_GX, this.frowset.getString("tabid")))
				        continue;
			   if(this.frowset.getString(_static)!=null&& "6".equals(this.frowset.getString(_static)))
			          if (!userView.isHaveResource(IResourceConstant.PSORGANS_JCG, this.frowset.getString("tabid")))
				        continue;
			   if(this.frowset.getString(_static)!=null&&("10".equals(this.frowset.getString(_static))|| "11".equals(this.frowset.getString(_static))))  //不包含单位、职位模板
				   continue;
			   LazyDynaBean bean = new LazyDynaBean();
			   String tabid=this.frowset.getString("tabid");
			   if((tabids+",").indexOf(","+tabid+",")!=-1) //不包含考勤业务申请信息
			   {
				   continue;
			   }
			   bean.set("tabid", this.frowset.getString("tabid"));
			   bean.set("name", this.frowset.getString("name"));
			   list.add(bean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

}
