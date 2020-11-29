package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 业务分类
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 8, 2016</p> 
 *@author hej
 *@version 1.0
 */
public class SearchModuleTemplateTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		String encryptParam=(String)this.getFormHM().get("encryptParam");
		HashMap encryptMap = getParamsDecode(encryptParam);
		String operationname = (String)encryptMap.get("operationname");
		String staticid = (String)encryptMap.get("staticid");
		String module_id = (String)this.getFormHM().get("module_id");
		String tab_ids = (String)this.getFormHM().get("tab_ids");
//		String return_flag = (String)this.getFormHM().get("return_flag");
		if(staticid==null)
			staticid = "";
		if(tab_ids!=null){
			operationname = "";
		}else {			
			if(operationname==null||operationname.length()<=0)
				return;
			operationname=SafeCode.decode(operationname);
		}
		try
		{
//			String res_flag = "7";//(String)this.getFormHM().get("res_flag");
//			String template_ids = (String)hm.get("template_ids");
			ArrayList list=getBuisTemplatelList(operationname,module_id,staticid, tab_ids);
			this.getFormHM().put("templist",list);
		}catch(Exception e)
		{
	       e.printStackTrace();
		}
	}
	private ArrayList getBuisTemplatelList(String operationname,String module_id,String staticid,String template_ids)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		SubsysOperation subsysOperation=new SubsysOperation(this.getFrameconn(),this.userView);
		
//		String return_flag = "0";//返回标识 待定
		String codes = "";
		if(StringUtils.isNotEmpty(template_ids))
			codes = template_ids;
		else 
			codes=subsysOperation.getView_value(staticid, operationname);
		if("60".equals(staticid)) //考勤业务办理
		{
			try
			{
				TemplateTableParamBo tp=new TemplateTableParamBo(this.getFrameconn());  
				if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.overtimeApply"))) //加班
				{
					String tabids=tp.getAllDefineKqTabs(1);  
					if(tabids.length()>0)
						codes=tabids.substring(1);
				}
				if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.leavetimeApply"))) //请假
				{
					String tabids=tp.getAllDefineKqTabs(2);  
					if(tabids.length()>0)
						codes=tabids.substring(1);
				}
				if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.officetimeApply"))) //公出
				{
					String tabids=tp.getAllDefineKqTabs(3);  
					if(tabids.length()>0)
						codes=tabids.substring(1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(codes!=null&&codes.length()>0)
		{
			String[] opertationcodes =codes.split(",");
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<opertationcodes.length;i++)
			{
				buf.append("'"+opertationcodes[i]+"',");
			}
			buf.setLength(buf.length()-1);
			StringBuffer sql=new StringBuffer();
			sql.append("select tabid,name,sp_flag,content from template_table where tabid in("+buf.toString()+") order by tabid");
			try
			{
				RowSet rset=dao.search(sql.toString());
				String sp_flag="0";
				HashMap<String,Integer> tabid_num=new HashMap<String,Integer>();//bug 45476业务模板分类之后的描述没法保存，点击确定没反应。map用于记录每个表单出现次数
				while(rset.next())
				{
					/**此业务模板无权限时，不加(对人事变动)*/
					TemplateTableBo templateTableBo = new  TemplateTableBo( this.getFrameconn(), rset.getInt("tabid"), userView);
					TemplateNavigationBo tnbo = new TemplateNavigationBo(this.frameconn,this.userView);
					String view = templateTableBo.getView();
					if(view==null)
						view ="";
					LazyDynaBean dynabean=new LazyDynaBean();
					String tabid=rset.getString("tabid");
					dynabean.set("tabid",tabid);				
					dynabean.set("name",rset.getString("name"));
					sp_flag=rset.getString("sp_flag")==null?"0":"1";
					dynabean.set("sp_flag",sp_flag);
					dynabean.set("content",Sql_switcher.readMemo(rset,"content"));
					dynabean.set("view",view);
					String ishave = "";
					if("37".equals(staticid)|| "38".equals(staticid)|| "55".equals(staticid)|| "61".equals(staticid))//人事异动37,合同管理38,资格评审55,证照管理 61
					{
						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					if("34".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.GZBD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					if("39".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.INS_BD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					if("40".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					if("56".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.ORG_BD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					if("57".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.POS_BD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					else if("51".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS,rset.getString("tabid"))){
							  ishave = "0";
							  dynabean.set("ishave", ishave);
							}else{
							  ishave = "1";
							  dynabean.set("ishave", ishave);
							}
					}
					else if("52".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG,rset.getString("tabid"))){
							  ishave = "0";
							  dynabean.set("ishave", ishave);
							}else{
							  ishave = "1";
							  dynabean.set("ishave", ishave);
							}
					}
					else if("53".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS_FG,rset.getString("tabid"))){
							  ishave = "0";
							  dynabean.set("ishave", ishave);
							}else{
							  ishave = "1";
							  dynabean.set("ishave", ishave);
							}
					}
					else if("54".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS_GX,rset.getString("tabid"))){
							  ishave = "0";
							  dynabean.set("ishave", ishave);
							}else{
							  ishave = "1";
							  dynabean.set("ishave", ishave);
							}
					}else if("60".equals(staticid)|| "30".equals(staticid)){ //考勤业务办理  liuyz 考勤支持业务模版
 						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid"))){
							  ishave = "0";
							  dynabean.set("ishave", ishave);
							}else{
							  ishave = "1";
							  dynabean.set("ishave", ishave);
							}
					}
					
					if (StringUtils.isNotEmpty(module_id)&&"11".equals(module_id)) {//职称评审走人事异动
						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid"))){
						  ishave = "0";
						  dynabean.set("ishave", ishave);
						}else{
						  ishave = "1";
						  dynabean.set("ishave", ishave);
						}
					}
					
					/*if(staticid.equals("38"))
					{
						if(!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}*/
//					if(!"noback".equals(return_flag)){
//						int version = userView.getVersion();
//						if(view!=null&&view=="list"){
//							return_flag="listhome";
//						}else if(view!=null&&view=="card"){
//							return_flag="9";
//						}else{
//							if(version>=50)
//								return_flag="listhome";
//							else
//								return_flag="9";
//						} 
//					}
//					dynabean.set("return_flag", return_flag);
					String fuctionIds = "";
					String isEdit = "false";//流程说明按钮控制
					String isManage = "false";//业务处理按钮控制
					if(ishave==null||!"1".equals(ishave)){
						fuctionIds = "3300103,331013,3203";
						if(tnbo.haveFunctionIds(fuctionIds)){
							isEdit = "false";
						}
						isManage = "false";
					}else{
						fuctionIds = "3300103,331013,3203,3240113,3250113,3219,3709,3719,3729,3739";
						if(tnbo.haveFunctionIds(fuctionIds)){
							isEdit = "true";
						}
						isManage = "true";
					}
					if("false".equals(isEdit)&&"false".equals(isManage)){// 如果没有查看说明和业务办理的权限就不显示了。 bug25187
						continue;
					}
					dynabean.set("isEdit", isEdit);
					dynabean.set("isManage", isManage);
					int num=0;
					if(tabid_num.containsKey(tabid)){
						num=(tabid_num.get(tabid)==null?0:tabid_num.get(tabid))+1;
					}
					tabid_num.put(tabid, num);
					dynabean.set("timestamp",num);
					list.add(dynabean);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		return list;
	}
	/**
	 * 解析加密串
	 * @param url
	 * @param str
	 * @return
	 */
	private HashMap getParamsDecode(String url) {
		HashMap map = new HashMap();
		try {
			url = PubFunc.decrypt(url);
			String[] params = url.split("&");
			for(int i=0;i<params.length;i++){
				if(params[i].indexOf("=")!=-1){
					String[] temp = params[i].split("=");
					map.put(temp[0], temp[1]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
