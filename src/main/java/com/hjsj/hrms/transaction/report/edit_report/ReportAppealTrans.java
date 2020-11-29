package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TableAnalyse;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;


public class ReportAppealTrans extends IBusiness {

	public void execute() throws GeneralException {
	    /* 编辑报表审批、报表汇总审批: 
	        一路往上报，状态全是审批中（4）；只有顶级领导最后批准了，状态才改成上报（1）
	    */
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet recset = null;
		try {
			String tabids = (String) this.getFormHM().get("tabids"); // 报表ID集合
			String operateObject = (String) this.getFormHM().get(
					"operateObject"); // 1：编辑没上报表 2：编辑上报后的表

			TnameBo tnameBo = new TnameBo(this.frameconn);
			//String unitcode = (String) this.getFormHM().get("unitcode"); // 用户对应的填表单位
			String unitcode = (String) this.getFormHM().get("appealUnitcode"); // 用户对应的填表单位
			String content1 = (String) this.getFormHM().get("content1"); // 用户填写的审批意见
			String changeStatus = (String) this.getFormHM().get("changStatus"); // 1:改变上报状态
			String username = SafeCode.decode((String) this.getFormHM().get("username"));
			String _username = getUserName(unitcode,username);
			boolean isChangeStatus = true;  
			if ("0".equals(changeStatus)) {
				isChangeStatus = false;
			}

			String[] tabid = null;
			if (tabids.indexOf("/") == -1) {
				tabid = new String[1];
				tabid[0] = tabids;

			} else {
				tabid = tabids.split("/");
			}

			StringBuffer infos = new StringBuffer("");

			if ("1".equals(operateObject)) { //编辑没上报的
				unitcode = (String) this.getFormHM().get("appealUnitcode"); //用户输入的填报单位编码
				DbWizard dbWizard = new DbWizard(this.getFrameconn());
				TnameExtendBo tnameExtendBo = new TnameExtendBo(this
						.getFrameconn());

				// boolean
				// p_isExist=tnameExtendBo.isExistAppealParamTable(1,"","",dbWizard);
				// //产生全局参数上报表
				// 上报全局参数
				TableAnalyse tableAnalyse = new TableAnalyse(this
						.getFrameconn(), 2, "");
				tableAnalyse.updateParamTable2(0);
				if (true) {
					Table table = new Table("tp_p");
					if (dbWizard.isExistTable(table.getName(),false)) {
						if (!tnameExtendBo.insertParam(unitcode, "tp_p",
								"tt_p", _username)){
							infos.append("\\n  "+ ResourceFactory
													.getProperty("edit_report.param.allParam")
											+ ResourceFactory
													.getProperty("edit_report.appeal.noSuccess"));
						}
					}
				}
				
				StringBuffer tabid_str = new StringBuffer("");
				TnameBo a_tnameBo = null;
				for (int i = 0; i < tabid.length; i++) {
					// 判断表结构是否更改
					a_tnameBo = new TnameBo(this.getFrameconn(), tabid[i]);
					TableAnalyse a_tableAnalyse = new TableAnalyse(this
							.getFrameconn(), 2, Integer.parseInt(tabid[i]),
							a_tnameBo);
					a_tableAnalyse.isExistTable();
					String info = "";
					if(unitcode!=null&&!"".equals(unitcode)){
					 info = tnameBo.ReportAppeal(tabid[i], _username, unitcode, dbWizard,
							tnameExtendBo, isChangeStatus,content1,this.getUserView().getUserFullName());
					}
					if ("1".equals(info)) {
						infos
								.append("\\n "
										+ ResourceFactory
												.getProperty("edit_report.table")
										+ tabid[i]
										+ " "
										+ ResourceFactory
												.getProperty("eidt_report.appeal.success"));

					} else if ("2".equals(info)) {
						infos
								.append("\\n "
										+ ResourceFactory
												.getProperty("edit_report.table")
										+ tabid[i]
										+ " "
										+ ResourceFactory
												.getProperty("edit_report.appeal.noSuccess"));
					}

					// boolean
					// t_isExist=tnameExtendBo.isExistAppealParamTable(3,tabid[i],"",dbWizard);
					// //产生表参数上报表
					boolean t_isExist = a_tableAnalyse.updateParamTable1(2);
					if (t_isExist) {
						Table table = new Table("tp_t" + tabid[i]);
						if (dbWizard.isExistTable(table.getName(),false)) {
							if (!tnameExtendBo.insertParam(unitcode, "tp_t"
									+ tabid[i], "tt_t" + tabid[i], _username, "0",
									tabid[i], 2))
								infos
										.append("\\n "
												+ ResourceFactory
														.getProperty("edit_report.table")
												+ tabid[i]
												+ ResourceFactory
														.getProperty("kq.formula.parameter")
												+ ResourceFactory
														.getProperty("edit_report.appeal.noSuccess"));
						}
					}
					tabid_str.append("," + tabid[i]);
				}

				// System.out.println("select distinct tsort.tsortid,tsort.name
				// from tname,tsort where tname.tsortid=tsort.tsortid and tabid
				// in("+tabid_str.substring(1)+")");
				// 上报表类参数
				recset = dao
						.search("select distinct tsort.tsortid,tsort.name from  tname,tsort where tname.tsortid=tsort.tsortid and tabid in("
								+ tabid_str.substring(1) + ")");
				while (recset.next()) {
					TableAnalyse a_tableAnalyse = new TableAnalyse(this
							.getFrameconn(), 2, recset.getString("tsortid"));
					a_tableAnalyse.updateParamTable2(1);
					// boolean
					// s_isExist=tnameExtendBo.isExistAppealParamTable(2,"",recset.getString("tsortid"),dbWizard);
					// //产生表类参数上报表
					if (true) {
						Table table = new Table("tp_s"
								+ recset.getString("tsortid"));
						if (dbWizard.isExistTable(table.getName(),false)) {
							if (!tnameExtendBo.insertParam(unitcode, "tp_s"
									+ recset.getString("tsortid"), "tt_s"
									+ recset.getString("tsortid"), _username, recset
									.getString("tsortid"), "0", 1))
								infos
										.append("\\n  "
												+ recset.getString("name")
												+ "  "
												+ ResourceFactory
														.getProperty("edit_report.param.tsortParam")
												+ ResourceFactory
														.getProperty("edit_report.appeal.noSuccess"));
						}

					}
				}

			} else {
				if (isChangeStatus) {
					String sql = "update treport_ctrl set status=1 where unitcode='"
							+ unitcode + "' and (";
					StringBuffer ss = new StringBuffer("");
					for (int i = 0; i < tabid.length; i++) {
						ss.append(" or tabid=" + tabid[i]);
					}
					dao.update(sql + ss.substring(3) + " )");
				}
				infos.append(ResourceFactory
						.getProperty("eidt_report.appeal.success")
						+ "！");
			}
			this.getFormHM().put("info", infos.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			try {
				if (recset != null)
					recset.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

	}
	public String getUserName(String uid,String userName){
		
		String sql="select username from operuser   where unitcode='"+uid+"' and username='"+this.userView.getUserName()+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		String username="";
		try {
			if(userName==null|| "".equals(userName)){
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					username = this.userView.getUserName();
				}
				if(username==null||username.trim().length()==0){
					this.frowset = dao.search("select username from operuser   where unitcode='"+uid+"'");
					if(this.frowset.next()){
						username = this.frowset.getString("username");
					}
				}
				if(username==null||username.trim().length()==0){
					username = this.userView.getUserName();
				}
			}else{
				username = userName;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return username;
	}
}
