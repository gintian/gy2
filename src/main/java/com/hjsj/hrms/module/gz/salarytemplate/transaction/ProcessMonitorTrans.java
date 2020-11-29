package com.hjsj.hrms.module.gz.salarytemplate.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytemplate.businessobject.ProcessMonitorBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 薪资流程监控交易类
 * @createtime
 * @author
 *
 */
public class ProcessMonitorTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		try {
			String salaryid = this.getFormHM().get("salaryid")==null?"":PubFunc.decrypt((String) this.getFormHM().get("salaryid"));
			String a00z2 = this.getFormHM().get("a00z2")==null?"":(String) this.getFormHM().get("a00z2");
			String curr_stateOfWrite = this.getFormHM().get("curr_stateOfWrite")==null?"":(String) this.getFormHM().get("curr_stateOfWrite");//当前填报状态
			String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
			String enter =  this.getFormHM().get("enter")==null?"":(String) this.getFormHM().get("enter");//入口，如果是切换类别进来的，需要重置a00z2，因为可能有也可能没有
			
			String url = this.getFormHM().get("url") == null?"":(String)this.getFormHM().get("url");
			if(StringUtils.isNotBlank(url)){
				com.hjsj.hrms.module.gz.salarytemplate.businessobject.SalaryTemplateBo salaryPayListBo = new com.hjsj.hrms.module.gz.salarytemplate.businessobject.SalaryTemplateBo(this.getFrameconn(), this.userView);// 工具类
				gz_module = salaryPayListBo.getValByStr(url, "imodule");// 0:薪资  1:保险
			}


			//如果salaryid为空说明是第一次进来的，这样展示第一个帐套的信息
			if(StringUtils.isBlank(salaryid)) {
				SalaryTypeBo salaryTypeBo = new SalaryTypeBo(this.getFrameconn(),this.userView);
				salaryid = salaryTypeBo.getSalarySetList(Integer.parseInt(gz_module));
			}

			ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.getFrameconn(),this.userView,gz_module,salaryid);


			SalarySetBo setbo = new SalarySetBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			boolean isShare=true;//薪资类别是否共享
			if(StringUtils.isBlank(manager))
			{
				isShare=false;
			}

			ArrayList<String> gzReportingDataList = processMonitorBo.getGzReportingData("1");
			if("1".equals(enter) || StringUtils.isBlank(a00z2)) {//切换类别，需要校验A00z0
				a00z2 = processMonitorBo.getA00z2(a00z2);
			}
			/** 获取列头 */
			ArrayList fieldlist=setbo.searchGzItem();
			/** 获取gz_reporting_log表中的记录，主要获取b0110，fullname*/
			ArrayList<ColumnsInfo> columnsInfo = processMonitorBo.getColumnList(fieldlist,isShare,gzReportingDataList);
			ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
			/** 获取数据 */
			String hiddenStr = ",a0000,a00z0,a0100,add_flag,sp_flag,sp_flag2,a00z1,a00z3,";
			
			if(StringUtils.isBlank(curr_stateOfWrite)||!this.getFormHM().containsKey("dataListAll")) {
				dataList = processMonitorBo.getDataList(manager,isShare,a00z2,fieldlist,gzReportingDataList,curr_stateOfWrite,gzbo,hiddenStr);
				this.getFormHM().put("dataListAll", dataList);
			}else {
				//记录下所有的数据，在切换状态的时候可以直接拿这个数据，不用再查数据库了，TableDataConfigCache由于每次都把数据塞到缓存中，如果从全部切到驳回，没问题，再从驳回切换到已报批就有问题了
				ArrayList dataListAll = (ArrayList) this.getFormHM().get("dataListAll");//薪资和保险区分标识  1：保险  否则是薪资
				for(int i = 0; i < dataListAll.size(); i++) {
					LazyDynaBean mapBean = new LazyDynaBean();
					MorphDynaBean bean = (MorphDynaBean)dataListAll.get(i);
					JSONObject jsonObject = JSONObject.fromObject(bean);//MorphDynaBean如果get没有的属性时，不为null，直接报错。。。。
					if(curr_stateOfWrite.equalsIgnoreCase((String)bean.get("sp_flag_code")) || "All".equalsIgnoreCase(curr_stateOfWrite)) {
						mapBean.set("fullname", jsonObject.get("fullname"));
						mapBean.set("username", jsonObject.get("username"));
						mapBean.set("a00z2", jsonObject.get("a00z2"));
						mapBean.set("count", jsonObject.get("count"));
						mapBean.set("sp_flag_code", jsonObject.get("sp_flag_code"));
						mapBean.set("sp_flag", jsonObject.get("sp_flag"));
						mapBean.set("curr_user", jsonObject.get("curr_user"));
						mapBean.set("curr_user_fullname", jsonObject.get("curr_user_fullname"));
						if(jsonObject.get("b0110") != null)//应用机构
							mapBean.set("b0110", jsonObject.get("b0110"));
						
						for (int j = 0; j < fieldlist.size(); j++) {//把所有的数值型塞入进去
			                FieldItem item = (FieldItem) fieldlist.get(j);
			                if ("N".equals(item.getItemtype()) && !hiddenStr.contains("," + item.getItemid().toLowerCase() + ",") && jsonObject.get(item.getItemid().toLowerCase()) != null) {
			                	mapBean.set(item.getItemid().toLowerCase(), jsonObject.get(item.getItemid().toLowerCase()));
			                }
			            }
						dataList.add(mapBean);
					}
				}
				this.getFormHM().put("dataListAll", dataListAll);
			}
			
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("process_monitor_"+salaryid, columnsInfo, "process_monitor", this.userView, this.getFrameconn());
			builder.setDataList(dataList);
			builder.setColumnFilter(true);
			builder.setAutoRender(true);
			builder.setPageSize(20);
			builder.setTitle(ResourceFactory.getProperty("label.gz.processMonitor"));
			builder.setSchemeSaveCallback("salarymonitor_me.schemeSetting_callBack");
			builder.setSetScheme(true);
			builder.setScheme(true);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());

			//类别集合
			ArrayList<HashMap<String,String>> itemList = processMonitorBo.getSalaryItems();
			//业务日期集合
			ArrayList<HashMap<String,String>> DateList = processMonitorBo.getSalaryDate(manager);
			//审批状态集合
			ArrayList<HashMap<String,String>> stateOfWriteList = new ArrayList<HashMap<String,String>>();
			if(StringUtils.isBlank(curr_stateOfWrite))
				stateOfWriteList = processMonitorBo.getSalaryState(dataList);
			else {
				ArrayList<HashMap<String,String>> stateOfWriteLists = this.getFormHM().get("stateOfWriteLists")==null?null:(ArrayList<HashMap<String,String>>) this.getFormHM().get("stateOfWriteLists");
				stateOfWriteList = stateOfWriteLists;
			}
			//获取可以发送通知的方式
			HashMap<String,Boolean> enableModes = processMonitorBo.getEnableModes();
			this.getFormHM().put("itemList", itemList);
			this.getFormHM().put("DateList", DateList);
			this.getFormHM().put("stateOfWriteList", stateOfWriteList);
			this.getFormHM().put("enableModes", enableModes);
			
			this.getFormHM().put("imodule",gz_module);
			this.getFormHM().put("curr_item", PubFunc.encrypt(salaryid));//当前的类别
			this.getFormHM().put("curr_date", a00z2);//当前业务日期
			this.getFormHM().put("curr_stateOfWrite", StringUtils.isNotBlank(curr_stateOfWrite)?curr_stateOfWrite:stateOfWriteList.get(0).get("id"));//当前填报状态
			
			String isShowPublicPlan = "0";
            if (("1".equals(gz_module) && this.userView.hasTheFunction("3250801")) ||
                    (!"1".equals(gz_module) && this.userView.hasTheFunction("3241501"))) {
                isShowPublicPlan = "1";
            } 
            this.getFormHM().put("isShowPublicPlan", isShowPublicPlan);
            
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
