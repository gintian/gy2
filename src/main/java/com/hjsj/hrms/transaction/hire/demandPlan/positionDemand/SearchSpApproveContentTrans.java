package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 招聘需求设置审批关系后选择报批的直接领导
 * @author chenxg
 *
 */
public class SearchSpApproveContentTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String Relationid = "";
		String spcount = "";
		
		if (hm != null && hm.size() > 0) {
			Relationid = (String) hm.get("spRelation");
			spcount = (String) hm.get("spcount");
		} else {
			Relationid = (String) this.formHM.get("spRelation");
			spcount = (String) this.formHM.get("spcount");
		}
		String Objectid = "";
		String Actortype = "";
		String title = "";
		String content = "";
		StringBuffer sql = new StringBuffer();
		ArrayList zparrplist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search("select Actor_type from t_wf_relation  where relation_id='" + Relationid + "'");
			if (this.frowset.next())
				Actortype = this.frowset.getString("Actor_type");

			if ("1".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getDbname() + this.userView.getA0100();
			else if ("4".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getUserName();

			if ("1".equalsIgnoreCase(Actortype)) {
				sql.append(" Select Mainbody_id from t_wf_mainbody where");
				sql.append(" Object_id='" + Objectid + "'");
				sql.append(" and relation_id='" + Relationid + "'");
				sql.append(" and SP_GRADE='9'");
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next()) {
					LazyDynaBean abean = new LazyDynaBean();
					String Mainbodyid = this.frowset.getString("Mainbody_id");
					String dbname = Mainbodyid.substring(0, 3);
					String a0100 = Mainbodyid.substring(3, Mainbodyid.length());
					this.frecset = dao.search("select a0100,a0101,b0110,e0122 from " + dbname + "a01 where a0100='" + a0100 + "'");
					if (this.frecset.next()) {
						if (!"1".equalsIgnoreCase(spcount)) {
							abean.set("a0100", dbname + this.frecset.getString("a0100"));
							abean.set("a0101", this.frecset.getString("a0101"));
							abean.set("b0110", this.frecset.getString("b0110"));
							abean.set("e0122", this.frecset.getString("e0122"));
							zparrplist.add(abean);
						} else {
							title = this.frecset.getString("a0101");
							content = dbname + this.frecset.getString("a0100");
						}
					}
				}

			} else if ("4".equalsIgnoreCase(Actortype)) {
				sql.append("Select username,a.fullName,a.GroupId,GroupName,a.a0100,a.nbase");
				sql.append(" from operuser a,UserGroup b,t_wf_mainbody c");
				sql.append(" where a.GroupId=b.GroupId and a.username=c.Mainbody_id");
				sql.append(" and a.username<>b.GroupName");
				sql.append(" and c.Actor_type='4'");
				sql.append(" and Object_id='" + Objectid + "'");
				sql.append(" and relation_id='" + Relationid + "'");
				sql.append(" and SP_GRADE='9'");
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next()) {
					LazyDynaBean abean = new LazyDynaBean();
					String a0100 = this.frowset.getString("a0100");
					String nbase = this.frowset.getString("nbase");
					if (this.frowset.getString("fullName")!=null && this.frowset.getString("fullName").length()>0){
						if (!"1".equalsIgnoreCase(spcount))
							abean.set("a0101", this.frowset.getString("fullName"));
						else
							title = this.frowset.getString("fullName");
					}else if (a0100 != null && a0100.length() > 0 && nbase != null && nbase.length() > 0) {
						this.frecset = dao.search("select a0101 from " + nbase + "a01 where a0100='" + a0100 + "'");
						if (this.frecset.next()) {
							if (!"1".equalsIgnoreCase(spcount))
								abean.set("a0101", this.frecset.getString("a0101"));
							else
								title = this.frecset.getString("a0101");
						}
					} else {
						if (!"1".equalsIgnoreCase(spcount))
							abean.set("a0101", this.frowset.getString("username"));
						else
							title = this.frowset.getString("username");
					}
					if (!"1".equalsIgnoreCase(spcount)) {
						abean.set("groupName", this.frowset.getString("GroupName"));
						abean.set("username", this.frowset.getString("username"));
						zparrplist.add(abean);
					} else {
						content = this.frowset.getString("username");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!"1".equalsIgnoreCase(spcount)) {
			this.getFormHM().put("zparrplist", zparrplist);
		} else {
			this.getFormHM().put("title", title);
			this.getFormHM().put("content", content);
		}
		this.getFormHM().put("actortype", Actortype);
	}

}
