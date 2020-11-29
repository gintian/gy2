package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitEmailTemplateTrans 
 * 类描述：薪资发放-发放通知主界面
 * 创建人：sunming
 * 创建时间：2015-7-3
 * @version
 */
public class InitSendMsgTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException
	{
		try
		{
			String priv_mode="",privSql="";
			//薪资id
			String salaryid=(String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			//业务日期
			String appdate = (String) this.getFormHM().get("appdate");
			appdate = PubFunc.decrypt(SafeCode.decode(appdate));
			String init = (String) this.getFormHM().get("init");//为'init2'时，模板发生变化时触发，为'init'时，则为初次加载
			String send_ok=(String) this.getFormHM().get("send_ok");
			String _appdate = (String) this.getFormHM().get("_appdate");//业务日期明文，用来初始化日期组件并传递日期组件返回的值
	    	if(_appdate!=null&&_appdate.length()>0){
	    		appdate = _appdate+"-01";
	    	}
			String subModuleId = "";
			SendMsgBo bo = new SendMsgBo(this.getFrameconn(), userView);
			
			String sendok="";
			String templateId="1";

		    if(salaryid==null||salaryid.length()<1){
		    	MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");//查询组件返回条件集合
		    	salaryid = (String) bean.get("salaryid");
		    	salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		    	appdate = (String) bean.get("appdate");
		    	appdate = PubFunc.decrypt(SafeCode.decode(appdate));
		    	_appdate = (String) bean.get("_appdate");
		    	if(_appdate!=null&&_appdate.length()>0){
		    		appdate = _appdate+"-01";
		    	}
		    	init = (String) bean.get("init");
				if("init".equals(init))
				{
					sendok="3";
		     		templateId=String.valueOf(bo.getMinTemplateId("2"));
				}else if("init2".equals(init)){
		    		templateId = (String) bean.get("templateId");
		    		sendok = (String) bean.get("sendOk");
		    	}
		    	subModuleId = (String) bean.get("subModuleId");
		    }else{
				if("init".equals(init))
				{
					sendok="3";
		     		templateId=String.valueOf(bo.getMinTemplateId("2"));
				}
				else if("init2".equals(init))
				{
					templateId=(String)this.getFormHM().get("templateId");
					sendok=(String) this.getFormHM().get("sendOk");
				}
		    }
		    
			String tableName="";
			
			//初始化send_ok列
			bo.updateTableCloumns();
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			//String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag"); 
			String orderBy="";
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				 tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				 tableName=manager+"_salary_"+salaryid;
			//2016-08-18 zhanghua 由于过滤权限导致发送通知页面数据和薪资发放页面不一致。取消权限过滤。
//			if(manager.trim().length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
//				 priv_mode="1";
//			String privSql=gzbo.getWhlByUnits("s",true);
			
			ArrayList templateList=bo.getEmailTemplateList(2);//取得所有薪资发放的邮件模板
			
			/**
			 *取得列头 
			 */
			ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
			ArrayList fieldList = bo.getColumnList();
			columns = bo.toColumnsInfo(fieldList)	;
			String timesql=bo.queryRecordByTime(appdate);
			if(send_ok!=null&&send_ok.length()>0){
				sendok = send_ok;
			}
			String sql=bo.getSearchPersonByCondSql("send_ok",sendok,templateId,tableName,timesql,priv_mode,privSql,null,orderBy,salaryid);
			
		    ArrayList<String> valuesList = (ArrayList) this.getFormHM().get("inputValues");//页面查询框返回的内容
		    String condSql = this.getCondSql(subModuleId, valuesList,tableName);
		    this.userView.getHm().put("condSql", condSql.replaceAll("data.", ""));
			if("init2".equals(init)||condSql.length()>0){//页面模糊查询
		//		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt(tableName)));
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tableName+"_msg");
				if("init2".equals(init))
					tableCache.setTableSql(sql);
				if(condSql.length()>0)
					tableCache.setQuerySql(" and "+condSql.replaceAll("data.", ""));//去掉表名，防止表格工具追加后报错
				else
					tableCache.setQuerySql("");
				this.userView.getHm().put(tableName+"_msg", tableCache);
				return;
			}
			//拼table
		//	TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt(tableName)), columns, "salaryaccountingsendmsg", userView,this.getFrameconn());
			TableConfigBuilder builder = new TableConfigBuilder(tableName+"_msg", columns, "salaryaccountingsendmsg", userView,this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy(" order by B0110,e0122,a0100");
			builder.setSelectable(true);
			builder.setAutoRender(false);
			builder.setLockable(true);
			builder.setPageSize(20);
			builder.setSortable(true);
			builder.setColumnFilter(true);
			builder.setTableTools(bo.getSalaryAccountingButtonList());
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfigSendMsg", config.toString());
			this.getFormHM().put("_appdate", appdate);
		}
		catch(Exception e)	
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
	}
	/**
	 * 
	 * @Title: getCondSql   
	 * @Description: 获取查询条件sql片段
	 * @param @param subModuleId
	 * @param @param valuesList
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	private String getCondSql(String subModuleId,ArrayList valuesList,String tableName){
		String condSql = "";
		try{
			if("salaryaccountingsendmsg".equals(subModuleId)){
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if("1".equals(type)) {
					// 输入的内容
					StringBuffer str = new StringBuffer();
					for(int i=0;i<valuesList.size();i++){
						String queryValue = SafeCode.decode((String) valuesList.get(i));
						if(i==0){
							str.append("a0101 like '%"+queryValue+"%'");
						}else{
							str.append(" or a0101 like '%"+queryValue+"%'");
						}
					}
					if(valuesList.size()>0)
						condSql += str.toString();
				} else if ("2".equals(type)) {
					String exp = (String) this.getFormHM().get("exp");
					String cond = (String) this.getFormHM().get("cond");
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tableName + "_msg");
					HashMap queryFields = tableCache.getQueryFields();
					// 解析表达式并获得sql语句
					FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
					condSql += parser.getSingleTableSqlExpression("data");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return condSql;
	}

}
