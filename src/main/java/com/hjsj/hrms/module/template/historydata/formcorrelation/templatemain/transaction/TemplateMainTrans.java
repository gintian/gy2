package com.hjsj.hrms.module.template.historydata.formcorrelation.templatemain.transaction;

import com.hjsj.hrms.module.template.historydata.formcorrelation.templatetoolbar.businessobject.TemplateToolBarBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 人事异动主页面交易
* @Title: TemplateMainTrans
* @Description:
* @author: hej
* @date 2019年11月19日 下午4:42:24
* @version
 */
public class TemplateMainTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			this.userView.getHm().put("useNewTemplateUrl","true");
			HashMap formMap= this.getFormHM();
			TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
			String tabId = TemplateFuncBo.getValueFromMap(formMap,"tab_id");
			String approveFlag = frontProperty.getApproveFlag();
			if(!PubFunc.isUseNewPrograme(this.userView))
		    	throw GeneralExceptionHandler.Handle(new Exception("新异动程序仅支持70版本以上的加密锁!"));
			if(tabId==null || "".equals(tabId)){
			    tabId="1";
				formMap.put("tab_id", tabId);
			}
			String taskId = TemplateFuncBo.getDecValueFromMap(formMap,"task_id");
			String archive_id = frontProperty.getOtherParam("archive_id");
		    if(!"0".equals(taskId)) {
				String[] taskidarr = taskId.split(",");
				String taskids_ = "";
				for(int j=0;j<taskidarr.length;j++) {
					String taskid = taskidarr[j];
					taskids_ += this.getSplitTaskId(taskid);
				}
				taskids_ = taskids_.substring(0,taskids_.length()-1);
				String[] taskids = taskids_.split(",");
				String taskids_en = "";
				for(int i=0;i<taskids.length;i++) {
					if(i==0) {
						taskids_en = PubFunc.encrypt(taskids[i]);
					}else {
						if(taskids_en.indexOf(PubFunc.encrypt(taskids[i]))==-1)
							taskids_en+= ","+PubFunc.encrypt(taskids[i]);
					}
				}
				this.getFormHM().put("task_id", taskids_en);
			}
		    //如果用户配置连接错误，模版id不存在提示用户
			String view_type = TemplateFuncBo.getValueFromMap(formMap,"view_type");
			String prefix = TemplateFuncBo.getValueFromMap(formMap,"prefix");
			TemplateParam tableParamBo=new TemplateParam(frameconn, userView, Integer.parseInt(tabId),archive_id);
			//进入时人员库设置
			String initbase = tableParamBo.getInit_base();
			ArrayList privDbList = this.userView.getPrivDbList();
			if(StringUtils.isNotBlank(initbase)){
				Boolean isHaveInitBase=false;
				for(int i=0;i<privDbList.size();i++){
					String dbname = (String) privDbList.get(i);
					if(initbase.equalsIgnoreCase(dbname)){
						isHaveInitBase=true;
						break;
					}
				}
				if(!isHaveInitBase){
					initbase="-1";
				}
			}
			if(tableParamBo.getTable_vo().getValues().size()==0)//liuyz bug32523  如果根据模版id不存在提示用户
			{
				throw new GeneralException("此模板不存在！");
			}
			
			if (!"card".equals(view_type)&&!"list".equals(view_type)){//如果没设置，则取默认定义方式
				view_type = tableParamBo.getView();
				this.getFormHM().put("view_type", view_type);		
			}

			this.getFormHM().put("approve_flag",approveFlag); 			
			/**  往templateMain.js传递的一些参数，模板号固定后这些值一般都不会发生变化 start  */
			//业务类型
			this.getFormHM().put("operation_type", tableParamBo.getOperationType()+"");
			this.getFormHM().put("infor_type",tableParamBo.getInfor_type()+"");
			this.getFormHM().put("nbases", initbase);			
			//返回功能按钮及标题栏json
			ArrayList buttonList = new ArrayList();
			String visible_toolbar=frontProperty.getOtherParam("visible_toolbar");			
			//获得第一个设置页码的模板页
			int firstPageNo = this.getFirstPageNo(Integer.parseInt(tabId));
			this.getFormHM().put("firstPageNo", firstPageNo);
			ArrayList columnList = new ArrayList();
			TemplateToolBarBo toolBarBo= new TemplateToolBarBo(this.frameconn,this.userView);			
			//返回功能按钮及标题栏json
			if (!"0".equals(visible_toolbar)){
			    buttonList = toolBarBo.getAllToolButtonList(tableParamBo,formMap);
			}			
			String visible_title=frontProperty.getOtherParam("visible_title");
			this.getFormHM().put("visible_toolbar",visible_toolbar);
			this.getFormHM().put("visible_title",visible_title);
			
			String subModuleId=prefix;
			TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt(subModuleId)), columnList, 
					subModuleId, userView,this.getFrameconn());
			builder.setTableTools(buttonList);
			String title =tableParamBo.getName();
			builder.setTitle(title);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config);
			//版本标识
			this.getFormHM().put("bos_flag", this.userView.getBosflag());  
		    this.getFormHM().put("@eventlog","表单号:"+tabId+",任务号:"+taskId);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private String getSplitTaskId(String taskId) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String taskids = taskId+",";
		try {
			String sql  = "select task_id from t_wf_task where originate_id="+taskId+" order by task_id";
			rowSet = dao.search(sql);
			while(rowSet.next()) {
				String task_id = rowSet.getString("task_id");
				//判断此task_id是否结束了
				RecordVo taskvo = new RecordVo("t_wf_task");
				taskvo.setInt("task_id", Integer.parseInt(task_id));
				taskvo = dao.findByPrimaryKey(taskvo);
				String task_state = taskvo.getString("task_state");
				if(!"4".equals(task_state)&&!"5".equals(task_state)&&!"6".equals(task_state)) {//非结束得
					taskids+=getSplitTaskId(task_id);
				}else {
					continue;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return taskids;
	}
	
	/**
	 * 获得第一个插入页码的模板页
	 * @param tabid
	 * @return
	 */
	private int getFirstPageNo(int tabid) {
		int pageid = -1;
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select min(pageid) as pageid from template_title where tabid=");
			strsql.append(tabid);
			strsql.append(" and flag=5");
			rowSet = dao.search(strsql.toString());
			if(rowSet.next()) {
				pageid = rowSet.getInt("pageid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return pageid;
	}
}
