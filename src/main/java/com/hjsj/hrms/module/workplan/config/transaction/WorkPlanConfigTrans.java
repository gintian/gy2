package com.hjsj.hrms.module.workplan.config.transaction;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *<p>Title:WorkPlanConfigTrans</p>
 *<p>Description:工作计划交易类</p>
 *<p>Company:HJSJ</p>
 *<p>Create Time:2016-6-12:下午05:00:35</p>
 *@author haosl
 *@version
 */
public class WorkPlanConfigTrans extends IBusiness{
		
	@Override
    public void execute() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			
			String opt = (String)hm.get("opt");
			
			WorkPlanConfigBo bo = new WorkPlanConfigBo(this.getFrameconn(), this.userView);
			//判断数据库中是否有OKR_CONFIG常量，如果没有就插入一条空记录，然后更新
			RowSet rs = bo.getConstant("OKR_CONFIG");
			String xml = null;
			if(rs==null || !rs.next()){
				bo.insertRecord();	//插入一条Constant='OKR_CONFIG'记录的记录
			}
			if("save".equals(opt)){//保存
				MorphDynaBean dynaBean = (MorphDynaBean)hm.get("formMap");
				HashMap formMap = PubFunc.DynaBean2Map(dynaBean);
				String upXml = generateXml("0", formMap);
				bo.updateRecord(upXml);
			}else if("saveNabseEmp".equals(opt)){//保存人员库和人员条件数据
				String nbases = (String) this.getFormHM().get("nbases");
				String emp_scope = (String) this.getFormHM().get("emp_scope");
				HashMap formMap = new HashMap();
				formMap.put("nbases", nbases);
				formMap.put("emp_scope", PubFunc.keyWord_reback(SafeCode.decode(emp_scope)));
				String upXml = generateXml("1", formMap);
				bo.updateRecord(upXml);
			}else if("selectNabseEmp".equals(opt)){//查询人员库和人员条件数据
				Map map = bo.getXmlData();
				String emp_scope = map.get("emp_scope")==null?"":(String)map.get("emp_scope");
				String dbValue = map.get("nbases")==null?"":(String)map.get("nbases");
				ArrayList dbList = bo.getDbList(dbValue);
				String empScopeValue = bo.getCexprCondValue(emp_scope);
				this.getFormHM().put("empScopeValue", empScopeValue);
				this.getFormHM().put("dbList",dbList);
				this.getFormHM().put("emp_scope",SafeCode.encode(emp_scope));
			}else if("selectEmpScope".equals(opt)){//查询人员条件数据解析公式
				String emp_scope = PubFunc.keyWord_reback(SafeCode.decode(hm.get("emp_scope")==null?"":(String)hm.get("emp_scope")));
				String empScopeValue = bo.getCexprCondValue(emp_scope);
				this.getFormHM().put("empScopeValue", empScopeValue);
			}else if("deleteEmp".equals(opt)){//清空人员条件数据
				HashMap formMap = new HashMap();
				formMap.put("emp_scope", "");
				String upXml = generateXml("3", formMap);
				bo.updateRecord(upXml);
			}else if("select".equals(opt)){
				Map map = bo.getXmlData();
				this.getFormHM().put("data", map);
			}else if("taskSource".equals(opt)){
				//获得工作任务子集
				ArrayList<HashMap<String, String>> taskData = bo.getTaskdata();
				this.getFormHM().put("taskData", taskData);
			}else if("taskItem".equals(opt)){
				//获得指定工作任务子集的指标
				String fieldsetid = "";
				Object object = this.getFormHM().get("fieldsetid");
				if(object!=null){
					fieldsetid = (String)object;
				}
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
				if(!StringUtils.isEmpty(fieldsetid)){
					data = bo.gettaskItemData(fieldsetid);
				}
				this.getFormHM().put("taskItemData", data);
			}else if("taskTimeSign".equals(opt)){//任务计时标识下拉数据
				ArrayList<HashMap<String, String>> taskTimeData = bo.getTaskTimeData();
				this.getFormHM().put("taskTimeData",taskTimeData);
			}else if("theme".equals(opt)){
				String themes = "default";
				UserView userView = this.getUserView();
				if(userView!=null){
					themes = SysParamBo.getSysParamValue("THEMES", userView.getUserName());//获得系统的模版
				}
				this.getFormHM().put("themes", themes);	
				
				// 是否在version.xml中启用“我的协作任务”功能，如果没有启用，则在参数设置中将不显示协作任务设置。
				VersionControl vc = new VersionControl();
				boolean isOpenCooperationTaskVersion = vc.searchFunctionId("0KR020303", this.userView.hasTheFunction("0KR020303"));
				this.getFormHM().put("isOpenCooperationTaskVersion", isOpenCooperationTaskVersion);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 解析需要更新的参数
	 * @param flag	标识
	 * @param formMap 数据
	 * @return
	 * @throws GeneralException
	 */
	 private String generateXml(String flag, HashMap formMap) throws GeneralException{
		 try {
			 /** 写作任务处理模式   cooperative_task: =1发布计划,=2协作任务申请 **/
			String cooperative_task = formMap.get("cooperative_task")==null?"":(String)formMap.get("cooperative_task");
			if(StringUtils.isEmpty(cooperative_task)){
				 cooperative_task = "1";
			}
			String plan_weight = formMap.get("plan_weight")==null?"":(String)formMap.get("plan_weight");
			/** 权重范围         from to **/
			String from=formMap.get("from")==null?"":(String)formMap.get("from");
			String to =formMap.get("to")==null?"":(String)formMap.get("to");
			boolean show_task=(formMap.get("show_task")==null?false:(Boolean)formMap.get("show_task"));//总结显示工作任务
			String taskSet = formMap.get("taskSet")==null?"":(String)formMap.get("taskSet");//任务子集
			String taskItem = formMap.get("taskItem")==null?"":(String)formMap.get("taskItem");//任务子集目标
			String taskTimeSign = formMap.get("taskTimeSign")==null?"":(String)formMap.get("taskTimeSign");//任务耗时标志
			String fillModel = (String)formMap.get("fillModel");//文本填写模式
			//linb
			String nbases = formMap.get("nbases")==null?"":(String)formMap.get("nbases");//填报人员库
			if(nbases.startsWith(",")){
				nbases = nbases.substring(1, nbases.length());
			}
			String emp_scope = formMap.get("emp_scope")==null?"":(String)formMap.get("emp_scope");//填报人员条件
			
			//原有参数，取缓存数据
			RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
			String param = "";
			// 有缓存则取缓存数据，没有则取默认参数
			if(null != paramsVo){
				param = paramsVo.getString("str_value");
			}else{
				WorkPlanConfigBo bo = new WorkPlanConfigBo(this.getFrameconn(), this.userView);
				param = bo.generateXml();
			}
			//xus 20/4/23 xml 编码改造
			Document doc = PubFunc.generateDom(param);
			//获得root节点
			Element root = doc.getRootElement();
			Element taskNode = root.getChild("cooperative_task");//协作任务处理模式节点
			Element weightNode = root.getChild("plan_weight");//工作计划权重控制节点,权重范围     
			Element task_set = root.getChild("task_set");//工作任务子集
			Element time_sign = root.getChild("time_sign");//耗时标志
			Element fill_model = root.getChild("fill_model");//总结填写模式
			Element nbasesE = root.getChild("nbases");//填报人员库
			Element emp_scopE = root.getChild("emp_scope");//填报人员条件
			Element summary = root.getChild("summary");//总结显示工作任务
			
			if("1".equals(flag)){
				// 填报人员库
				if(nbasesE!=null){
					nbasesE.setText(nbases);
				}else{
					Element nbasesNode = new Element("nbases");
					nbasesNode.setText(nbases);
					root.addContent(nbasesNode);
				}
				// 填报人员条件
				if(emp_scopE!=null){
					 emp_scopE.setText(emp_scope);
				}else{
					Element emp_scopeNode = new Element("emp_scope");
					emp_scopeNode.setText(emp_scope);
					root.addContent(emp_scopeNode);
				}
			}else if("0".equals(flag)){
				
				//工作任务子集
				if(task_set!=null){
					task_set.setAttribute("setid",taskSet);
					task_set.setAttribute("itemid",taskItem);
				}else{
					 Element taskSetNode = new Element("task_set");
					 taskSetNode.setAttribute("setid",taskSet);
					 taskSetNode.setAttribute("itemid",taskItem);
					 root.addContent(taskSetNode);
				}
				// 耗时标志
				if(time_sign!=null){
					time_sign.setAttribute("itemid",taskTimeSign);
				 }else{
					 Element taskTiemSignNode = new Element("time_sign");
					 taskTiemSignNode.setAttribute("itemid",taskTimeSign);
					 root.addContent(taskTiemSignNode);
				 }
				//总结填写模式
				if(fill_model!=null){
					fill_model.setText(fillModel);
				}else{
					Element taskTiemSignNode = new Element("fill_model");
					taskTiemSignNode.setText(fillModel);
					root.addContent(taskTiemSignNode);
				}
				//获取属性值和文本内容值
				if(taskNode!=null){
					 taskNode.setAttribute("deal_model", cooperative_task);
				 }else{
					 Element cotaskNode = new Element("cooperative_task");
					 cotaskNode.setAttribute("deal_model",cooperative_task);
					 root.addContent(cotaskNode);
				 }
				//工作计划权重控制节点权重
				if(weightNode!=null){
					if(StringUtils.isNotBlank(from)&& StringUtils.isNotBlank(to)){
						weightNode.setAttribute("from", from);
						weightNode.setAttribute("to", to);
					}
					weightNode.setText(plan_weight);
				 }else{
					 Element plweightNode = new Element("plan_weight");
					 if(StringUtils.isNotBlank(from)&& StringUtils.isNotBlank(to)){
						 plweightNode.setAttribute("from", from);
						 plweightNode.setAttribute("to", to);
					 }
					 plweightNode.setText(plan_weight);
					 root.addContent(plweightNode);
				 } 
				//总结显示工作任务
				if(summary!=null){
					summary.setAttribute("show_task", show_task?"true":"false");
				 }else{
					 Element summaryNode = new Element("summary");
					 summaryNode.setAttribute("show_task", show_task?"true":"false");
					 root.addContent(summaryNode);
				 } 
			}else if("3".equals(flag)){//清空填报人员条件
				// 填报人员条件
				if(emp_scopE!=null){
					 emp_scopE.setText("");
				}
			}
			 //设置xml字体编码，然后输出为字符串
			 Format format=Format.getRawFormat();
			 format.setEncoding("UTF-8");
			 XMLOutputter output=new XMLOutputter(format);
			 return output.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	 }
}
