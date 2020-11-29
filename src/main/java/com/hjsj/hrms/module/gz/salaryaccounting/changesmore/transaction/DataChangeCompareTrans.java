package com.hjsj.hrms.module.gz.salaryaccounting.changesmore.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.changesmore.businessobject.ChangesmoreBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryPropertyBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 项目名称 ：hcm7.x
 * 类名称：DataChangeCompareTrans
 * 类描述：数据比对 主页面表单数据获取
 * 创建人： zhanghua
 * 创建时间：2016-11-09
 */
public class DataChangeCompareTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String imodule = (String) this.getFormHM().get("imodule");
			imodule = PubFunc.decrypt(SafeCode.decode(imodule));
			/**type=1 薪资发放中的数据比对  =2薪资审批中的数据比对**/
			String type = (String) this.getFormHM().get("type");
			/**业务日期**/
			String appdate = (String) this.getFormHM().get("appdate");
			appdate = PubFunc.decrypt(SafeCode.decode(appdate));
			/**次数**/
			String count = (String) this.getFormHM().get("count");
			count = PubFunc.decrypt(SafeCode.decode(count));
			
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String tableName= "1".equals(type)?gzbo.getGz_tablename():"SALARYHISTORY";
			//如果用户没有当前薪资类别的资源权限   
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,imodule);
			
			ChangesmoreBo bo = new ChangesmoreBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			ArrayList<LazyDynaBean> salaryItems = bo.getSalaryTemplateBo().getSalaryItemList("", salaryid, 1);
			ArrayList<LazyDynaBean> fieldItems=new ArrayList<LazyDynaBean>();
			
			String tName = "";
			if("1".equals(type))
				tName="salary_"+salaryid;
			else
				tName="salaryspdetail_"+salaryid;
			//栏目设置已经存在，则从数据库中取
			int schemeId = gzbo.getSchemeId(tName);
			// 从数据库中得到可以显示的薪资项目代码
			if(schemeId > 0){
				fieldItems = bo.getSchemedHeadItemList_simpleSort(salaryItems, schemeId);
			}else {
				for(LazyDynaBean bean:salaryItems)//获取待比对指标。 仅取非系统项数字型指标
					if(!"3".equals(bean.get("initflag").toString())&& "N".equals(bean.get("itemtype").toString()))
						fieldItems.add(bean);
			}
			
			//拼写已选单位部门的查询条件----------------------------------------------
			String selectID = (String) this.getFormHM().get("selectID");
			String deptSql="";
			SalaryPropertyBo PropertyBo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
			String deptid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
			String orgid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");//归属单位
			if(deptid.trim().length()==0)
				deptid="e0122";
			if(orgid.trim().length()==0)
				orgid="b0110";
			String errorStr=bo.checkOrgField(tableName, deptid, orgid);//检查归属单位 部门 是否在数据库中存在
			if(errorStr.trim().length()!=0){
				throw GeneralExceptionHandler.Handle(new Exception(errorStr));
			}
			
			if(selectID!=null&&selectID.trim().length()>0){
				deptSql=bo.getDeptSqlWhere(selectID, deptid,orgid);
			}
			
			
			//---------------------------------------------------------------------
			
			boolean ispriv=false;
			if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))//共享非管理员
				ispriv=true;
			//取得发放日期 次数和权限条件
			ArrayList<String> arrStr=bo.getSqlWhere(tableName,appdate, count, type,ispriv);
			String strWhere=arrStr.get(0);//上次发放日期 where语句
			String strWhereNow=arrStr.get(1);//本次发放日期 where语句
			String tableNameOld=bo.isArchive(strWhere);
			
			if("salaryarchive".equalsIgnoreCase(tableNameOld))
				strWhere=strWhere.replaceAll("salaryhistory", "salaryarchive");
			ArrayList dataList=bo.getChangeMainDataList(tableName,tableNameOld, type, fieldItems,deptSql,strWhere,strWhereNow);
			salaryItems=null;
			String titleBarText=bo.titleAmountText(tableName,tableNameOld, type, deptSql,strWhere,strWhereNow);
//			/** 获取列头 */
//			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
//			columnsInfo = bo.getColumnList();
				
//			TableConfigBuilder builder = new TableConfigBuilder("salarychangesmore", columnsInfo, "salaryaccountingchangesmore", userView, this.getFrameconn());
//			builder.setDataList(dataList);
//			builder.setRowdbclick("gz_changesmore.mainRowdbclick");//行双击事件
//			builder.setSelectable(true);
//			builder.setAutoRender(false);
//			builder.setLockable(true);
//			builder.setPageSize(20);
//			builder.setSortable(true);
//			builder.setScheme(true);
//			builder.setSetScheme(false);
//			builder.setSelectable(false);

//			String config = builder.createExtTableConfig();
			this.getFormHM().put("orgTreeTitle", DataDictionary.getFieldItem(orgid).getItemdesc());
			this.getFormHM().put("data", dataList);//表格数据
			this.getFormHM().put("titleBarText", titleBarText);
			//this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("msg", "ok");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
