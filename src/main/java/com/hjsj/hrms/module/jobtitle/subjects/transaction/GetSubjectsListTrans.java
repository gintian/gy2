package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 获取学科组信息（列表方式）
 * <p>Title: GetSubjectsListTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Jul 28, 2016 10:40:47 AM</p>
 * @author liuy
 * @version 7.x
 */
@SuppressWarnings("serial")
public class GetSubjectsListTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);//工具类
		String group_id = (String)this.getFormHM().get("group_id");//学科组编号
		group_id = PubFunc.decrypt(group_id);
		String isshowall = (String)this.getFormHM().get("isshowall");//是否显示全部 1：是 0：否
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			ArrayList fieldList = DataDictionary.getFieldList("w01", Constant.USED_FIELD_SET);

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


			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = subjectsBo.getColumnList(fieldList);
			/** 获取查询语句 */
			String sql = subjectsBo.getSubjectPersonSql(fieldList, group_id, isshowall);
			
			TableConfigBuilder builder = new TableConfigBuilder("jobtitle_subject_00001", columnList, "jobtitle_subject", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy("order by role desc,w0101 asc");//组长放在显示列表的第一位 haosl 20160903
			builder.setTitle(JobtitleUtil.ZC_MENU_SUBJECTSSHOWTEXT + "成员");
			builder.setAutoRender(true);
			builder.setColumnFilter(true);//统计过滤
			if(this.userView.hasTheFunction("380020310")){//栏目设置权限
				builder.setScheme(true);//栏目设置
				builder.setSetScheme(true);
				builder.setShowPublicPlan(this.userView.hasTheFunction("38002031001"));//公有
			}
			builder.setLockable(true);
			builder.setSelectable(true);
			builder.setEditable(true);
			builder.setAnalyse(false);
			builder.setConstantName("jobtitle/subject");
			
			String state = "";
			rs = dao.search("select state from zc_subjectgroup where group_id='"+ group_id +"'");
			if(rs.next()){
				state = rs.getString("state");
			}
			builder.setTableTools(subjectsBo.getButtonList(state));
			builder.setPageSize(20);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			
			this.getFormHM().put("state", state);
			this.getFormHM().put("addVersion", this.userView.hasTheFunction("380020301"));//新建学科组权限
			this.getFormHM().put("editVersion", this.userView.hasTheFunction("380020302"));//编辑学科组权限
			this.getFormHM().put("deleteVersion", this.userView.hasTheFunction("380020303"));//删除学科组权限
			this.getFormHM().put("addPersonVersion", this.userView.hasTheFunction("380020304"));//新增成员权限
			this.getFormHM().put("deletePersonVersion", this.userView.hasTheFunction("380020305"));//删除成员权限
			this.getFormHM().put("showHistoryVersion", this.userView.hasTheFunction("380020306"));//显示历史权限

			this.getFormHM().put("func", func);
			this.getFormHM().put("orgid",orgid);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
