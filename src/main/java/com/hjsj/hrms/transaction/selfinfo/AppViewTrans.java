package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AppViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		// String userbase = (String) this.getFormHM().get("userbase");// 人员库
		String flag = (String)reqhm.get("flag");
		String userbase = this.userView.getDbname();
		userbase = userbase != null && userbase.trim().length() > 0 ? userbase : "Usr";

		String allflag = (String) this.getFormHM().get("allflag");
		allflag = allflag != null && allflag.trim().length() > 0 ? allflag : "01";

		String a0100 = this.userView.getA0100();

		String actiontype = (String) this.getFormHM().get("actiontype");
		actiontype = actiontype != null && actiontype.trim().length() > 0 ? actiontype : "";

		MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn, this.userView);

		String chg_id = (String) reqhm.get("chg_id");
		chg_id = chg_id != null && chg_id.trim().length() > 0 ? chg_id : "";
		reqhm.remove("chg_id");

		String[] arr = chg_id.split(",");

		String savEdit = (String) reqhm.get("savEdit");
		savEdit = savEdit != null && savEdit.trim().length() > 0 ? savEdit : "search";
		reqhm.remove("savEdit");

		if ("appbaopi".equals(savEdit)) {
			//tianye add 判断是否修改当前登陆人的信息，修改前代码没有判断，去掉if(!"infoself".equalsIgnoreCase(flag)){ 直接执行了if里的语句，导致子集整体报批失去了作用
			if(!"infoself".equalsIgnoreCase(flag)){
				a0100 = (String) reqhm.get("a0100");
				userbase = (String) reqhm.get("userbase");
			}
			String sql = "select chg_id from t_hr_mydata_chg where nbase='"
					+ userbase + "' and a0100='" + a0100
					+ "' and (sp_flag = '01' or sp_flag = '07')";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				RowSet set = dao.search(sql);
				if (set != null && set.next()) {
					chg_id = set.getString("chg_id");
					if (chg_id != null && chg_id.length() > 0) {
						mysel.batchMyselfDataApply(chg_id, "02");
						String name = setOrgInfo(this.userView.getDbname(),
								this.userView.getA0100(), this.frameconn);
						//zxj 20160613 【19018】
		                if("///".equals(name)){
		                    name = userView.getUserFullName();
		                }
						mysel.approval(this.userView, name, "报批", chg_id, "");
					}
				}
				//多媒体报批
				sql = "update " + userbase + "A00 set state='1' where a0100='"+ a0100 +"' and (state<>'3' or state is null)" ;
				dao.update(sql);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			if (arr != null && arr.length > 0) {
				if ("baopi".equalsIgnoreCase(savEdit)) {
					for (int i = 0; i < arr.length; i++) {
						if (arr[i] != null && arr[i].trim().length() > 0)
							mysel.batchMyselfDataApply(PubFunc.decrypt(arr[i].toString()), "02");
						String name = setOrgInfo(userbase, a0100, this.frameconn);
						//zxj 20160613 【19018】
                        if("///".equals(name)){
                            name = userView.getUserFullName();
                        }
						mysel.approval(this.userView, name, "报批", PubFunc.decrypt(arr[i]), "");
					}
					//多媒体报批
					try {
					    ContentDAO dao = new ContentDAO(this.getFrameconn());
					    String sql = "update " + userbase + "A00 set state='1' where a0100='"+ a0100 +"' and (state<>'3' or state is null)" ;
					    dao.update(sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
					allflag = "02";
				} else if ("delAll".equalsIgnoreCase(savEdit)) {
					for (int i = 0; i < arr.length; i++) {
						if (arr[i] != null && arr[i].trim().length() > 0)
							mysel.deleteMyselfData(PubFunc.decrypt(arr[i]));
					}
				}
			}
		}

		String sqlStr[] = getStatesql(userbase, a0100, allflag);
		this.getFormHM().put("sql", sqlStr[0]);
		this.getFormHM().put("where", sqlStr[2]);
		this.getFormHM().put("column", sqlStr[1]);
		this.getFormHM().put("spflaglist", spflagList());
		this.getFormHM().put("a0100", PubFunc.encrypt(a0100));
		this.getFormHM().put("userbase", PubFunc.encrypt(userbase));
		this.getFormHM().put("actiontype", actiontype);
		this.getFormHM().put("allflag", allflag);
	}

	public String[] getStatesql(String userbase, String a0100, String allflag) {
		String[] sql = new String[4];
		
		/*使用oracle数据库时，create_time的数据类型为date类型，在paginationdb标签中会将
		*date类型的数据自动转成YYYY-MM-DD类型，无法显示时分秒，所以在使用oracle数据库时将
		*create_time转成字符窜类型，避免标签的自动转化，从而显示时分秒
		*
		*Sql_switcher.searchDbServer()从服务器下的system.properties文件中获得dbserver的值，
		*1为sqlserver数据库，2为oracle数据库
		*
		*wangzhongjun 2010-01-21
		*/
		if (Sql_switcher.searchDbServer() == 2) {
			sql[0] = "select chg_id,sp_idea,sp_flag,to_char(create_time,'YYYY-MM-DD HH24:MI:SS') as create_time,description";
		} else {
			sql[0] = "select chg_id,sp_idea,sp_flag,create_time,description";
		}
		
		sql[1] = "chg_id,sp_idea,sp_flag,create_time,description";
		sql[2] = "from t_hr_mydata_chg where nbase='" + userbase
				+ "' and a0100='" + a0100 + "'";
		if (!"all".equalsIgnoreCase(allflag))
			sql[2] += " and sp_flag='" + allflag + "'";
		sql[3] = "order by create_time desc";
		return sql;
	}

	private ArrayList spflagList() {
		ArrayList list = new ArrayList();
		String name[] = { "全部", "起草", "已报批", "已批", "退回" };
		String id[] = { "all", "01", "02", "03", "07" };
		for (int i = 0; i < id.length; i++) {
			CommonData obj = new CommonData(id[i], name[i]);
			list.add(obj);
		}

		return list;
	}

	/**
	 * 获得“单位/部门/职位/姓名”形式字符窜
	 * 
	 * @param userbase
	 * @param A0100
	 * @param dao
	 */
	private String setOrgInfo(String userbase, String A0100,
			Connection connection) {
		ContentDAO dao = new ContentDAO(connection);
		StringBuffer strsql = new StringBuffer();
		StringBuffer name = new StringBuffer();
		String b0110 = "";
		String e0122 = "";
		String e01a1 = "";
		String a0101 = "";
		try {
			if (userbase != null && userbase.length() > 0 && A0100 != null &&A0100.length() > 0) {
				strsql.append("select b0110,e0122,e01a1,a0101 from ");
				strsql.append(userbase);
				strsql.append("A01 where a0100='");
				strsql.append(A0100);
				strsql.append("'");
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) {
					b0110 = this.getFrowset().getString("B0110");
					e0122 = this.getFrowset().getString("E0122");
					e01a1 = this.getFrowset().getString("E01A1");
					a0101 = this.getFrowset().getString("a0101");
				}
			}
		} catch (Exception e) {

		} finally {
			if (b0110 != null && b0110.trim().length() > 0)
				b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode
						.getCode("UN", b0110).getCodename() : "";
			if (e0122 != null && e0122.trim().length() > 0)
				e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode
						.getCode("UM", e0122).getCodename() : "";
			if (e01a1 != null && e01a1.trim().length() > 0)
				e01a1 = AdminCode.getCode("@K", e01a1) != null ? AdminCode
						.getCode("@K", e01a1).getCodename() : "";
		}
		
		if (b0110 == null) {
			name.append("");
		} else {
			name.append(b0110);
		}		
		name.append("/");
		if (e0122 == null) {
			name.append("");
		} else {
			name.append(e0122);
		}
		name.append("/");
		if (e01a1 == null) {
			name.append("");
		} else {
			name.append(e01a1);
		}
		name.append("/");
		if (a0101 == null) {
			name.append("");
		} else {
			name.append(a0101);
		}
		
		return name.toString();
	}
}
