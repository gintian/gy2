package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 获取评委会组内人员信息
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetCommitteePersonTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		String committee_id = (String)this.getFormHM().get("committee_id");//评委会编号
		committee_id = PubFunc.decrypt(committee_id);
		String isHistory = (String)this.getFormHM().get("ishistory");//是否显示历史 1：是 0：否
		
		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类

			ExpertsBo bo = new ExpertsBo(this.frameconn,this.userView);
			/**获得登陆人的职称管理业务范围   **/
			String unit = this.userView.getUnitIdByBusi("9");
			String orgid = "";
			String func = "";
			if(unit!=null&&!"".equals(unit)){
				if("UN`".equals(unit)){//全部范围
					orgid = unit;
				}
				else{
					String [] unitarr = unit.split("`");
					for(String arr:unitarr){
						arr = arr.substring(2,arr.length());
						orgid+=arr+",";
					}
					orgid = orgid.substring(0,orgid.length()-1);
					String itemid = "";
					if(orgid.indexOf(",")!=-1){
						int index = orgid.indexOf(",");
						itemid = orgid.substring(0,index);
					}else{
						itemid = orgid;
					}
					String codeitemdesc = bo.getItemDesc(itemid);
					func = itemid+"`"+codeitemdesc;
				}
			}


			
			ArrayList fieldList = DataDictionary.getFieldList("w01", Constant.USED_FIELD_SET);
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = committeeBo.getColumnList(fieldList);

			/** 获取查询语句 */
			String sql = committeeBo.getCommitteePersonSql(fieldList, committee_id, isHistory);
			
			TableConfigBuilder builder = new TableConfigBuilder("jobtitle_committee_00001", columnList, "jobtitle_committee", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy("order by role desc,w0101 asc");//组长放在显示列表的第一位 haosl 20170523
			builder.setTitle(JobtitleUtil.ZC_MENU_COMMITTEESHOWTEXT + "成员");
			builder.setAutoRender(true);
			builder.setColumnFilter(true);//统计过滤
			if(this.userView.hasTheFunction("380020208")){//栏目设置权限
				builder.setScheme(true);//栏目设置
				builder.setSetScheme(true);
				builder.setShowPublicPlan(this.userView.hasTheFunction("38002020801"));//公有
			}
			builder.setLockable(true);
			builder.setSelectable(true);
			builder.setEditable(true);
			builder.setAnalyse(false);
			builder.setConstantName("jobtitle/committee");
			builder.setTableTools(committeeBo.getButtonList());
			builder.setPageSize(20);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("func", func);
			this.getFormHM().put("orgid",orgid);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
