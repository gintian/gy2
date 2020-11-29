package com.hjsj.hrms.module.template.templatenavigation.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatenavigation.transaction.SearchTemplateTreeTrans;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateLayoutBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Pattern;
/**
 * 
 * <p>Title:TemplateTableBo.java</p>
 * <p>Description>:业务模块业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 10, 2016 3:44:00 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class TemplateNavigationBo {

	private UserView userview;
	private Connection conn=null;
	private String    module_id="";
	public String getModule_id() {
        return module_id;
    }
    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }
    public TemplateNavigationBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	/**
	 * 
	 * @Title: getDbTaskButtons   
	 * @Description: 待办任务页签按钮   
	 * @param @return 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList getDbTaskButtons(){
		ArrayList buttonList = new ArrayList();
		try{
			//VersionControl ver = new VersionControl();
			/*if(userview.hasTheFunction("3240306")||userview.hasTheFunction("3270306")){
			}*/
			String functions="400040129,32103,37003,37103,37203,37303,33001029,33101029,2701529,0C34829,32029,324010129,325010129,010729,3800729";
			if ( haveFunctionIds(functions)){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"), "DbTaskScope.deleteTask"));
			}
		   functions="400040102,32102,37002,37102,37202,37302,33001002,33101002,2701502,0C34802,32002,324010101,325010101,010707,3800702";
			if (haveFunctionIds(functions)){
				ButtonInfo batchApprove = new ButtonInfo("批量审批","DbTaskScope.batchApprove");
				batchApprove.setId("batchApproveId");		
				buttonList.add(batchApprove);
			}
			//buttonList.add(new ButtonInfo("查询","DbTaskScope.query"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	/**
	 * 
	 * @Title: getDbColumnsInfo   
	 * @Description: 获取待办页签表头各列信息 
	 * @param @return 
	 * @return ArrayList<ColumnsInfo> 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList<ColumnsInfo> getDbColumnsInfo(){
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item.setItemid("states");
			item.setItemdesc(ResourceFactory.getProperty("column.sys.status"));//状态
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("23");
			ColumnsInfo info = new ColumnsInfo(item);
//			info.setEditableValidFunc("false");//不允许编辑
			info.setColumnWidth(60);// 显示列宽
			list.add(info);				

			item = new FieldItem();
			item.setItemid("task_pri");
			item.setItemdesc("<img src='/images/imail.gif' width='5' height='13'>");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setHintText(ResourceFactory.getProperty("conlumn.board.priority"));
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("DbTaskScope.getPri");
			info.setColumnWidth(50);// 显示列宽
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("bread");
			item.setItemdesc("<img src='/images/quick_query.gif' width='16' height='16'>");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setHintText(ResourceFactory.getProperty("template_new.columnHasRead"));
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("DbTaskScope.getBread");
			info.setColumnWidth(50);// 显示列宽
			list.add(info);	
			
/*			item = new FieldItem();
			item.setItemid("bfile");
			item.setItemdesc("<img src='/images/amail_1.gif' width='8' height='13'>");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setHintText(ResourceFactory.getProperty("template_new.columnHasFile"));
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("DbTaskScope.getBfile");
			info.setColumnWidth(50);// 显示列宽
			list.add(info);*/
			
			item = new FieldItem();
			item.setItemid("a0101_1");
			item.setItemdesc(ResourceFactory.getProperty("column.sender"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("task_topic");
			item.setItemdesc(ResourceFactory.getProperty("conlumn.board.topic"));//主题
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(450);
			info.setRendererFunc("DbTaskScope.getTopic");
			//info.setSortable(false);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("start_date");
			item.setItemdesc(ResourceFactory.getProperty("column.accept.date"));//接收时间
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(120);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("unitname");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.applyunit"));//发起单位
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("UM");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("ins_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("task_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
		//	info.setEncrypted(true);//是否加密  lis add 20160412
			//info.setKey(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			//liuyz bug26219
			item = new FieldItem();
			item.setItemid("ismessage");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @Title: getYbTaskButtons   
	 * @Description: 待办任务页签按钮   
	 * @param @return 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList getYbTaskButtons(){
		ArrayList buttonList = new ArrayList();
		try{
			//buttonList.add(new ButtonInfo("查询","YbTaskScope.query"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	/**
	 * 
	 * @Title: getYbColumnsInfo   
	 * @Description: 获取已办页签表头各列信息 
	 * @param @return 
	 * @return ArrayList<ColumnsInfo> 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList<ColumnsInfo> getYbColumnsInfo(){
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item = new FieldItem();
			item.setItemid("task_topic");
			item.setItemdesc(ResourceFactory.getProperty("conlumn.board.topic"));//主题
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(300);
			info.setRendererFunc("YbTaskScope.getTopic");
			//info.setSortable(false);
			list.add(info);
			
			item.setItemid("fullname");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.applyemp"));//
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");//不允许编辑
			list.add(info);				

			item = new FieldItem();
			item.setItemid("unitname");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.applyunit"));//发起单位
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("UN");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("start_date");
			item.setItemdesc(ResourceFactory.getProperty("general.template.applyStartDate"));//发起时间
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("end_date");
			item.setItemdesc(ResourceFactory.getProperty("general.template.spEndDate"));//结束时间
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("sp_info");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.task.curremp"));//当前审批人
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
//			info.setRendererFunc("DbTaskScope.getPri");
			info.setRendererFunc("YbTaskScope.getRoleInfo");
			list.add(info);
			
			
			item = new FieldItem();
			item.setItemid("sploop");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.sploop"));//审批过程
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("YbTaskScope.getSploop");
			info.setSortable(false);
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("browsePrint");
			item.setItemdesc("浏览打印");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("YbTaskScope.getBrowsePrint");
			info.setSortable(false);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("flag");
			item.setItemdesc(ResourceFactory.getProperty("task.state"));//任务状态
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("ins_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("task_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("actor_type");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @Title: getCtrlTaskButtons   
	 * @Description: 待办任务页签按钮   
	 * @param @return 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList getCtrlTaskButtons(){
		ArrayList buttonList = new ArrayList();
		try{
			//VersionControl ver = new VersionControl();
			//功能导航按钮
			ArrayList navigationList = new ArrayList();
			ArrayList menuList = new ArrayList();			
			LazyDynaBean oneBean = null;
			if(TemplateFuncBo.haveFunctionIds("40004021,3800911,270161,33001011,33101103,32011,32111,324010203,325010203,230672904,2311022904,3254010203",this.userview)){
				//buttonList.add(new ButtonInfo("重新分派",""));
				ArrayList list = new ArrayList();
				//组织单元
				LazyDynaBean bean = TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("task.selectobject.orgcell"), "CtrlTaskScope.selectObject(3)","",new ArrayList());
				bean.set("id","organization");
				list.add(bean);
				//用户
				list.add(TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("task.selectobject.user"), "CtrlTaskScope.selectObject(4)","",new ArrayList()));
				//人员
				list.add(TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("task.selectobject.personnel"), "CtrlTaskScope.selectObject(1)","",new ArrayList()));
				//角色
				list.add(TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("task.selectobject.role"), "CtrlTaskScope.selectObject(2)","",new ArrayList()));
				//String menu = TemplateLayoutBo.getMenuStr(ResourceFactory.getProperty("button.reassign"), list);//重新分派
				//buttonList.add(menu);
				oneBean = new LazyDynaBean();
				oneBean.set("text", ResourceFactory.getProperty("button.reassign"));//重新分派
				oneBean.set("menu", list);
				oneBean.set("id", "reAssignId");
				navigationList.add(oneBean);	
			}
			if(TemplateFuncBo.haveFunctionIds("40004023,3800913,270163,33001013,33101104,32013,32113,324010204,3254010204,2311022905,230672905",this.userview)){
				//导出Excel
				oneBean = TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("goabroad.collect.educe.excel"), "CtrlTaskScope.exportExcel()", "", new ArrayList());
				navigationList.add(oneBean);	
			}
			if(TemplateFuncBo.haveFunctionIds("40004020,3800910,270160,33001033,324010202,325010202,32131,32010,33101105,2311022901,230672901",this.userview)){
				oneBean = TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("template_new.processEnd"), "CtrlTaskScope.processEnd()", "", new ArrayList());
				oneBean.set("id", "processEndId");
				navigationList.add(oneBean);	
			}
			//变动日志菜单
			if(TemplateFuncBo.haveFunctionIds("230672906,2311022907,32014,33101107,3800914,33001014,270168,324010206,3254010206,32112",this.userview)){
				oneBean = TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("template.changeLog.changelog"), "CtrlTaskScope.showChangeInfo()", "", new ArrayList());
				oneBean.set("id", "changeInfo");
				navigationList.add(oneBean);
			}
			//流程归档
			if(TemplateFuncBo.haveFunctionIds("230672908,2311022908,32015,33101108,3800915,33001015,270169,324010207,3254010207,32114",this.userview)){
			    if(!("7".equals(this.module_id)||"8".equals(this.module_id))) {
			        oneBean = TemplateLayoutBo.getMenuBean(ResourceFactory.getProperty("template.processArchiving"), "CtrlTaskScope.processArchiving()", "", new ArrayList());
			        oneBean.set("id", "processArchiving");
			        navigationList.add(oneBean);
			    }
				if(this.userview.isSuper_admin()) {
					LazyDynaBean repreated_approval= new LazyDynaBean();
					repreated_approval.set("text", "重提结束单据数据");//重新分派
					repreated_approval.set("menu", new ArrayList());
					repreated_approval.set("id", "repreated_approval");
					repreated_approval.set("handler", "CtrlTaskScope.repreated()");
					navigationList.add(repreated_approval);
				}
			}
			if (navigationList.size()>0){
			    String menu = TemplateLayoutBo.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"),"",
			            navigationList);			
			    buttonList.add(menu);
			}
			
			String runningDel = "40004022,3800912,270162,33001012,33101101,32012,32112,37012,37112,37212,37312,324010201,325010201,230672902,2311022902";//运行中删除
			String endedDel = "40004027,3800927,270167,33001027,33101102,32027,32127,324010227,325010227,230672903,2311022903";//结束删除
			String stopDel =   "40004024,3800941,32041,32138,33101106,33001034,270164,324010205,325010205,2311022906,230672906";//已终止删除

			if(TemplateFuncBo.haveFunctionIds(runningDel,this.userview) || TemplateFuncBo.haveFunctionIds(endedDel,this.userview) || TemplateFuncBo.haveFunctionIds(stopDel,this.userview)){
				ButtonInfo deleteBut = new ButtonInfo(ResourceFactory.getProperty("button.delete"),ButtonInfo.FNTYPE_DELETE, "MB00006008");
				deleteBut.setId("ctrlDelId");
				//删除
				buttonList.add(deleteBut);
			}
			//buttonList.add(new ButtonInfo(ResourceFactory.getProperty("goabroad.collect.educe.excel"),"CtrlTaskScope.exportExcel"));
			//查询
			//buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.query"),"CtrlTaskScope.query"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	
	/**
	 * 
	 * @Title: getCtrlColumnsInfo   
	 * @Description: 获取已办页签表头各列信息 
	 * @param sp_flag 1:我的申请  2:任务监控
	 * @return ArrayList<ColumnsInfo> 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList<ColumnsInfo> getCtrlColumnsInfo(String sp_flag){
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item = new FieldItem();
			item.setItemid("name");
			item.setItemdesc(ResourceFactory.getProperty("conlumn.board.topic"));//主题
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(300);
			if("2".equals(sp_flag))
				info.setRendererFunc("CtrlTaskScope.getTopic");
			else if("1".equals(sp_flag))
				info.setRendererFunc("MyApplyScope.getTopic");
				
			list.add(info);
			
			item.setItemid("fullname");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.applyemp"));//
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");//不允许编辑
			list.add(info);				

			if("2".equals(sp_flag))
			{
				item = new FieldItem();
				item.setItemid("unitname");
				item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.applyunit"));//发起单位
				item.setItemtype("A");
				item.setReadonly(true);
				item.setItemlength(50);
				item.setCodesetid("UM");
				info = new ColumnsInfo(item);
				info.setEditableValidFunc("false");
				list.add(info);
			}
			item = new FieldItem();
			item.setItemid("ins_start_date");
			item.setItemdesc(ResourceFactory.getProperty("general.template.applyStartDate"));//发起时间
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("ins_end_date");
			item.setItemdesc(ResourceFactory.getProperty("general.template.spEndDate"));//结束时间
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("actorname");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.task.curremp"));//当前审批人
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			if("2".equals(sp_flag))
				info.setRendererFunc("CtrlTaskScope.getRoleInfo");
			else if("1".equals(sp_flag))
				info.setRendererFunc("MyApplyScope.getRoleInfo");
//			info.setRendererFunc("DbTaskScope.getPri");
			list.add(info);
			
			
			item = new FieldItem();
			item.setItemid("sploop");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.sploop"));//审批过程
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setFilterable(false);
			info.setTextAlign("center");
			if("2".equals(sp_flag))
				info.setRendererFunc("CtrlTaskScope.getSploop");
			else if("1".equals(sp_flag))
				info.setRendererFunc("MyApplyScope.getSploop");
			info.setSortable(false);
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("browsePrint");
			item.setItemdesc("浏览打印");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setFilterable(false);
			info.setTextAlign("center");
			if("2".equals(sp_flag))
				info.setRendererFunc("CtrlTaskScope.getBrowsePrint");
			else if("1".equals(sp_flag))
				info.setRendererFunc("MyApplyScope.getBrowsePrint");
			info.setSortable(false);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("finished");
			item.setItemdesc(ResourceFactory.getProperty("task.state"));//任务状态
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("38");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			if("1".equals(sp_flag))
				info.setRendererFunc("MyApplyScope.showRecallFlag");
			list.add(info);	
			
			
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("ins_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("task_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			info.setEncrypted(true);
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("actor_type");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			if("1".equals(sp_flag)){
				item = new FieldItem();
				item.setItemid("recallflag");
				item.setItemtype("A");
				item.setItemlength(50);
				item.setCodesetid("0");
				info = new ColumnsInfo(item);
				info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
				list.add(info);
				
				item = new FieldItem();
				item.setItemid("actortype");
				item.setItemtype("A");
				item.setItemlength(50);
				item.setCodesetid("0");
				info = new ColumnsInfo(item);
				info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
				list.add(info);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @Title: getBusinessApplyColumnsInfo   
	 * @Description:获取业务申请表头信息 
	 * @param @return 
	 * @return ArrayList<ColumnsInfo> 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList<ColumnsInfo> getBusinessApplyColumnsInfo(){
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemdesc(ResourceFactory.getProperty("report.number"));//编号
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("name");
			item.setItemdesc(ResourceFactory.getProperty("myapply.bussinessname"));//业务申请
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(500);
			info.setRendererFunc("BusinessApplyScope.getTopic");
			list.add(info);	

			item = new FieldItem();
			item.setItemid("finished");
			item.setItemdesc(ResourceFactory.getProperty("column.operation"));//操作
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setTextAlign("center");
			info.setEditableValidFunc("false");
			info.setRendererFunc("BusinessApplyScope.getSploop");
			info.setSortable(false);
			list.add(info);	
			
			
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("ins_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @Title: getTemplateList   
	 * @Description: 获取模板列表
	 * @param @param tabidSet
	 * @param @return 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList getTemplateList(HashSet tabidSet)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("id", "-1");
			bean.set("name", "全部");
			list.add(bean);
			if(tabidSet.size()>0){
				StringBuffer sql=new StringBuffer();
				StringBuffer tabidStr=new StringBuffer("");
				for(Iterator t=tabidSet.iterator();t.hasNext();)
				{
					tabidStr.append(","+(String)t.next());
				}
				sql.append("select   tabid,  name  from   Template_table   ");
				sql.append(" where tabid in ("+tabidStr.substring(1)+")  order by tabid");
				RowSet rowSet=dao.search(sql.toString());
				while(rowSet.next())
				{
					bean = new LazyDynaBean();
					bean.set("id", rowSet.getString("tabid"));
					bean.set("name", rowSet.getString("name"));
					list.add(bean);
				} 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 校验日期是否正确
	 * @return
	 */
	public boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		try
		{
			Date date=DateStyle.parseDate(datestr);
			if(date==null)
				bflag=false;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	
	/**
	 * 是否有模板资源权限
	 * @author dengcan
	 * @return
	 */
	public boolean hasTemplateResource()
	{
		return this.userview.isHavetemplateid(IResourceConstant.RSBD)||this.userview.isHavetemplateid(IResourceConstant.ORG_BD)||this.userview.isHavetemplateid(IResourceConstant.POS_BD)||this.userview.isHavetemplateid(IResourceConstant.GZBD)||this.userview.isHavetemplateid(IResourceConstant.INS_BD)||this.userview.isHavetemplateid(IResourceConstant.PSORGANS)||this.userview.isHavetemplateid(IResourceConstant.PSORGANS_FG)||this.userview.isHavetemplateid(IResourceConstant.PSORGANS_GX)||this.userview.isHavetemplateid(IResourceConstant.PSORGANS_JCG);
			
	}
	
	
	
	/**
	 * 校验天数
	 * @return
	 */
	public boolean validateNum(String date)
	{
		boolean bflag=true;
		if(date==null|| "".equals(date))
			return false;
		try
		{
			Pattern pattern = Pattern.compile("[0-9]+");
			bflag=pattern.matcher(date).matches();
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 模块id转成业务模块id
	 * @param module_id
	 * @return
	 */
	public String moduleid2staticid(String module_id){
		String openseal = "0";
		if("1".equals(module_id))
			openseal = "37";     //人事异动
		else if("2".equals(module_id))
			openseal = "34";     //薪资变动
		else if("3".equals(module_id))
			openseal="38";//劳动合同
		else if("4".equals(module_id))
		    openseal="39";//保险变动
		else if("5".equals(module_id))
		    openseal="40";//出国管理
		else if("6".equals(module_id))
			openseal="55";//资格评审
		else if("7".equals(module_id))
			openseal = "56";      //组织机构
		else if("8".equals(module_id))
			openseal = "57";      //岗位变动
//		else if(module_id.equals("9"))//业务申请（自动）
//			openseal = "38";		
		else if("10".equals(module_id))//考勤业务
			openseal = "60";			
		else if("11".equals(module_id))
			openseal = "52";//职称评审
		else if("12".equals(module_id))//证照管理
			openseal = "61";
		return openseal;
	}
	/** 
	* @Title: haveFunctionIds 
	* @Description:是否有权限 
	* @param @param fuctionIds 权限号 以逗号分隔
	* @param @return
	* @return boolean
	*/ 
	public boolean haveFunctionIds(String fuctionIds)
	{
		boolean b=false;
		String []ids= fuctionIds.split(",");
		try
		{
			for (int i=0;i<ids.length;i++){
				String fucId=ids[i];
				if (this.userview.hasTheFunction(fucId)){
					b=true;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b;
	}
	public ArrayList<ColumnsInfo> getDbFColumnsInfo(){

		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item.setItemid("states");
			item.setItemdesc(ResourceFactory.getProperty("column.sys.status"));//状态
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("23");
			ColumnsInfo info = new ColumnsInfo(item);
//			info.setEditableValidFunc("false");//不允许编辑
			info.setColumnWidth(60);// 显示列宽
			list.add(info);				

			item = new FieldItem();
			item.setItemid("task_pri");
			item.setItemdesc("<img src='/images/imail.gif' width='5' height='13'>");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("templateform.getPri");
			info.setColumnWidth(50);// 显示列宽
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("bread");
			item.setItemdesc("<img src='/images/quick_query.gif' width='16' height='16'>");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("templateform.getBread");
			info.setColumnWidth(50);// 显示列宽
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("bfile");
			item.setItemdesc("<img src='/images/amail_1.gif' width='8' height='13'>");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setTextAlign("center");
			info.setRendererFunc("templateform.getBfile");
			info.setColumnWidth(50);// 显示列宽
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("a0101_1");
			item.setItemdesc(ResourceFactory.getProperty("column.sender"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("task_topic");
			item.setItemdesc(ResourceFactory.getProperty("conlumn.board.topic"));//主题
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(450);
			info.setRendererFunc("templateform.getTopic");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("start_date");
			item.setItemdesc(ResourceFactory.getProperty("column.accept.date"));//接收时间
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(120);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("unitname");
			item.setItemdesc(ResourceFactory.getProperty("rsbd.wf.applyunit"));//发起单位
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("UM");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("ins_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
			
			item = new FieldItem();
			item.setItemid("task_id");
			item.setItemtype("A");
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
		//	info.setEncrypted(true);//是否加密  lis add 20160412
			//info.setKey(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
			list.add(info);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	public ArrayList getDbFTaskButtons(){
		ArrayList buttonList = new ArrayList();
		try{
			/*String functions="32103,37003,37103,37203,37303,33001029,33101029,2701529,0C34829,32029,324010129,325010129,010729,3800729";
			if ( haveFunctionIds(functions)){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"), "DbTaskScope.deleteTask"));
			}*/
		    String functions="400040102,32102,37002,37102,37202,37302,33001002,33101002,2701502,0C34802,32002,324010101,325010101,010707,3800702";
			if (haveFunctionIds(functions)){
				buttonList.add(new ButtonInfo("批量审批","templateform.batchApprove"));
			}
			//buttonList.add(new ButtonInfo("查询","DbTaskScope.query"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	//liuyz 考勤支持业务模版 begin
	public String getKqTabIds(String module_id) throws Exception {
		StringBuffer kq_tabids = new StringBuffer();
		TemplateTableParamBo tp = new TemplateTableParamBo(this.conn);
		SubsysOperation subsysOperation = new SubsysOperation();
		ArrayList paramslist = subsysOperation.getView_tag("30");
		if (paramslist.size() == 0) {
			kq_tabids.append(tp.getAllDefineKqTabs(0));
		} else {
			String isFlag = (String) subsysOperation.getMap().get("30");
			if ("true".equals(isFlag)) {
				for (int i = 0; i < paramslist.size(); i++) {
					String sortname = paramslist.get(i).toString();
					String ids=subsysOperation.getView_value("30", sortname);
					if(ids.trim().length()>0)
					{
						kq_tabids.append(",");
						kq_tabids.append(ids);
					}
				}
			} else {
				String _static = "1";
				String res_flag = "7";
				String static_="static";
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					static_="static_o";
				}
				StringBuffer strsql = new StringBuffer();
				String unit_type = null;
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rset = null;
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
				unit_type = sysbo.getValue(Sys_Oth_Parameter.UNITTYPE, "type");
				if (unit_type == null || "".equals(unit_type))
					unit_type = "3";
				if (module_id != null
						&& ("3".equals(module_id) || "5".equals(module_id) || "6"
								.equals(module_id)))
					unit_type = "0";
				strsql.append("select distinct a.operationcode,b.operationname ,operationid from ");
				strsql.append("template_table a ,operation b where a.operationcode=b.operationcode and b."+static_+"=");
				strsql.append(_static);
				strsql.append(" and (");
				String[] units = unit_type.split(",");
				for (int i = 0; i < units.length; i++) {
					strsql.append("a.flag =" + Integer.parseInt(units[i]));
					if (i < units.length - 1)
						strsql.append(" or ");
				}
				strsql.append(")");
				strsql.append(" order by a.operationcode, operationid");
				rset = dao.search(strsql.toString());
				/** 业务分类 */
				while (rset.next()) {
					SearchTemplateTreeTrans trans = new SearchTemplateTreeTrans();
					ArrayList childrenList = this.getTemplates(module_id, rset.getString("operationcode"), res_flag, unit_type);
					for(int num=0;num<childrenList.size();num++)
						kq_tabids.append(",").append(childrenList.get(num));
					}
				}
			}
			return kq_tabids.toString();
		}
	/**
	 * 获取模版数
	 * @param type
	 * @param module
	 * @param res_flag
	 * @param unit_type
	 * @return
	 * @throws Exception
	 */
	private ArrayList getTemplates(String type, String module, String res_flag,
			String unit_type) throws Exception {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select tabid,name,ctrl_para from template_table where operationcode='");
		strsql.append(module);
		strsql.append("' and (");
		String[] units = unit_type.split(",");
		for (int i = 0; i < units.length; i++) {
			strsql.append(" flag =" + Integer.parseInt(units[i]));
			if (i < units.length - 1)
				strsql.append(" or ");
		}
		strsql.append(") order by tabid");
		RowSet rset = null;
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());
			while (rset.next()) {
				if (!this.userview.isHaveResource(Integer.parseInt(res_flag),
						rset.getString("tabid")))
					continue;
				HashMap paramMap = this.getTemplateParam(Sql_switcher.readMemo(rset,
						"ctrl_para"));
				String isKq = (String) paramMap.get("isKq");
				if ("false".equals(isKq)) {
					continue;
				}
				list.add(rset.getString("tabid"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(rset);
		}
		return list;
	}

	/**
	 * 检查是否是考勤使用的模板 及模板显示方式 为了提高效率 单独处理
	 * 
	 * @param ctrl_para
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getTemplateParam(String ctrl_para) throws GeneralException {
		HashMap map = new HashMap();
		try {
			map.put("isKq", "false");
			map.put("view", "list");
			if (ctrl_para != null && ctrl_para.trim().length() > 0) {
				Document doc = null;
				Element element = null;
				String xpath = "/params/sp_flag";
				doc = PubFunc.generateDom(ctrl_para);;
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist = findPath.selectNodes(doc);
				if (childlist != null && childlist.size() > 0) {
					element = (Element) childlist.get(0);
					if (element.getAttribute("kq_type") != null
							&& element.getAttribute("kq_field_mapping") != null) {
						String _kq_type = ((String) element
								.getAttributeValue("kq_type")).trim();
						String _kq_field_mapping = (String) element
								.getAttributeValue("kq_field_mapping");
						if (_kq_type != null && _kq_type.trim().length() > 0) {
							if (_kq_field_mapping != null
									&& _kq_field_mapping.trim().length() > 0) {
								map.put("isKq", "true");
							}
						}
					}
				}

				xpath = "/params/init_view";
				findPath = XPath.newInstance(xpath);
				childlist = findPath.selectNodes(doc);
				if (childlist != null && childlist.size() > 0) {
					element = (Element) childlist.get(0);
					if (element.getAttribute("view") != null)
						map.put("view", (String) element
								.getAttributeValue("view"));
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	////liuyz 考勤支持业务模版 end
	/**
	 * 生成我的申请功能按钮
	 * @return
	 */
	public ArrayList getMyapplyButtons() {
		ArrayList buttonList = new ArrayList();
		try{
			//撤回按钮 暂时不用 等以后要用可以直接解开注释即可使用批量
			/*ButtonInfo recallButton = new ButtonInfo("撤回","MyApplyScope.recallTask(-1)");
			recallButton.setId("recallbutton");
			buttonList.add(recallButton);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	/**
	 * 得到业务分类设置对应的模板id
	 * @param module_id
	 * @return
	 * @throws Exception
	 */
	public String getBusinessTabid(String module_id) throws Exception {
		String staticid = this.moduleid2staticid(module_id);
		String tabids = "";
		SubsysOperation subsysOperation = new SubsysOperation();
		ArrayList paramslist = subsysOperation.getView_tag(staticid);
		if (paramslist.size() > 0) {
			for (int i = 0; i < paramslist.size(); i++) {
				String sortname = paramslist.get(i).toString();
				String ids=subsysOperation.getView_value(staticid, sortname);
				if(ids.trim().length()>0)
				{
					String[] idarr = ids.split(",");
					for(int j=0;j<idarr.length;j++) {
						String id = idarr[j];
						if(this.checkResource(module_id,staticid,id))
							tabids += id+",";
					}
				}
			}
		}
		if(tabids.length()>0) {
			tabids = tabids.substring(0, tabids.length()-1);
		}
		return tabids;
	}
	/**
	 * 校验是否有模板资源权限
	 * @param module_id
	 * @param staticid
	 * @param tabid 
	 */
	private boolean checkResource(String module_id, String staticid, String tabid) {
		boolean ishave = false;
		if(("37").equals(staticid)||("38").equals(staticid)||("55").equals(staticid)||("61").equals(staticid)){//人事异动37,合同管理38,资格评审55,证照管理 61
			if(this.userview.isHaveResource(IResourceConstant.RSBD,tabid)){
			  ishave = true;
			}
		}
		else if(("34").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.GZBD,tabid)){
			  ishave = true;
			}
		}
		else if(("39").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.INS_BD,tabid)){
			  ishave = true;
			}
		}
		else if(("40").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.RSBD,tabid)){
			  ishave = true;
			}
		}
		else if(("56").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tabid)){
			  ishave = true;
			}
		}
		else if(("57").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.POS_BD,tabid)){
			  ishave = true;
			}
		}
		else if(("51").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tabid)){
				  ishave = true;
			}
		}
		else if(("52").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tabid)){
				  ishave = true;
			}
		}
		else if(("53").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tabid)){
				  ishave = true;
			}
		}
		else if(("54").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tabid)){
				  ishave = true;
			}
		}
		else if(("60").equals(staticid)||("30").equals(staticid)){
			if(this.userview.isHaveResource(IResourceConstant.RSBD,tabid)){
			  ishave = true;
			}
		}
		if (StringUtils.isNotEmpty(module_id)&&"11".equals(module_id)) {
			if(this.userview.isHaveResource(IResourceConstant.RSBD,tabid)){
			  ishave = true;
			}
		}
		return ishave;
	}
}
