package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 3000000236
 * <p>Title:SearchSpContentTrans.java</p>
 * <p>Description>:SearchSpContentTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 19, 2009 7:08:43 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchSpContentTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**不同入口，=0是需求审核，=1是从邮件进入 =2是从招聘订单进入*/
			String intype=(String)hm.get("intype");
			if("0".equals(intype))
			{
				
			}
			else if("2".equals(intype))
			{
			    String assignType = "";
			    ArrayList assignTypeList = new ArrayList();
			    ArrayList fieldlist = DataDictionary.getFieldList("Z04", Constant.USED_FIELD_SET);
			    for (int i = 0; i < fieldlist.size(); i++)
			    {
				FieldItem field = (FieldItem) fieldlist.get(i);
				//z0414 是人力资源部负责人帐号字段 z0409是招聘负责人帐号字段
				if ("z0409".equalsIgnoreCase(field.getItemid()) || "z0414".equalsIgnoreCase(field.getItemid()))
				{
				    CommonData temp = new CommonData(field.getItemid(),field.getItemdesc());
				    assignTypeList.add(temp);
				    if(assignType.length()==0)
					assignType=field.getItemid();
				}
			    }
			    this.getFormHM().put("assignType", assignType);
			    this.getFormHM().put("assignTypeList",assignTypeList);
			}
			else
			{
				String z0301 = (String)hm.get("z0301");
				String sp_flag=(String)hm.get("sp_flag");
				String spPerson=(String)hm.get("spperson");
				String spRelation = (String) hm.get("spRelation");
				//PositionDemand bo = new PositionDemand(this.getFrameconn());
				//String reasons=bo.getReasons(z0301);
				String content="";
				this.getFormHM().put("content", content);
				//this.getFormHM().put("reasons", reasons);
				this.getFormHM().put("sp_flag", sp_flag);
				this.getFormHM().put("z0301", z0301);
				this.getFormHM().put("a0100", spPerson);
				this.getFormHM().put("zparrplist", getAppList(spRelation));
			}
			
			ArrayList rolelist=AdminCode.getCodeItemList("41");
			ArrayList templist=new ArrayList();
			for(int i=0;i<rolelist.size();i++)
			{
				CodeItem  item=(CodeItem)rolelist.get(i);
				if("0".equals(item.getCodeitem())|| "2".equals(item.getCodeitem())|| "3".equals(item.getCodeitem()))
					continue;
				templist.add(item);
			}
			this.getFormHM().put("roleid","#");
			this.getFormHM().put("rolelist", templist);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	/**
	 * 获取当前用户在审批关系中的直接领导
	 * @param spRelation
	 * @return
	 */
	public ArrayList getAppList(String spRelation) {
		ArrayList list = new ArrayList();
		String Objectid = "";
		String Actortype = "";
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		spRelation = spRelation == null ? "" : spRelation;
		
		try {
			this.frowset = dao.search("select Actor_type from t_wf_relation  where relation_id='" + spRelation + "'");
			if (this.frowset.next())
				Actortype = this.frowset.getString("Actor_type");

			if ("1".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getDbname() + this.userView.getA0100();
			else if ("4".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getUserName();
			
			this.formHM.put("actortype", Actortype);

			if ("1".equalsIgnoreCase(Actortype)) {
				sql.append(" Select Mainbody_id from t_wf_mainbody where");
				sql.append(" Object_id='" + Objectid + "'");
				sql.append(" and relation_id='" + spRelation + "'");
				sql.append(" and SP_GRADE='9'");
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next()) {
					LazyDynaBean abean = new LazyDynaBean();
					String Mainbodyid = this.frowset.getString("Mainbody_id");
					String dbname = Mainbodyid.substring(0, 3);
					String a0100 = Mainbodyid.substring(3, Mainbodyid.length());
					this.frecset = dao.search("select a0100,a0101,b0110,e0122 from " + dbname + "a01 where a0100='" + a0100 + "'");
					if (this.frecset.next()) {
						abean.set("a0100", dbname + this.frecset.getString("a0100"));
						abean.set("a0101", this.frecset.getString("a0101"));
						abean.set("b0110", this.frecset.getString("b0110"));
						abean.set("e0122", this.frecset.getString("e0122"));
						list.add(abean);
					}
				}
			} else if ("4".equalsIgnoreCase(Actortype)) {
				sql.append("Select username,a.fullName,a.GroupId,GroupName,a.a0100,a.nbase");
				sql.append(" from operuser a,UserGroup b,t_wf_mainbody c");
				sql.append(" where a.GroupId=b.GroupId and a.username=c.Mainbody_id");
				sql.append(" and a.username<>b.GroupName");
				sql.append(" and c.Actor_type='4'");
				sql.append(" and Object_id='" + Objectid + "'");
				sql.append(" and relation_id='" + spRelation + "'");
				sql.append(" and SP_GRADE='9'");
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next()) {
					LazyDynaBean abean = new LazyDynaBean();
					String a0100 = this.frowset.getString("a0100");
					String nbase = this.frowset.getString("nbase");
					if (this.frowset.getString("fullName") != null && this.frowset.getString("fullName").length() > 0) {
						abean.set("a0101", this.frecset.getString("a0101"));
					} else if (a0100 != null && a0100.length() > 0 && nbase != null && nbase.length() > 0) {
						this.frecset = dao.search("select a0101 from " + nbase + "a01 where a0100='" + a0100 + "'");
						if (this.frecset.next()) {
							abean.set("a0101", this.frecset.getString("a0101"));
						}
					} else {
						abean.set("a0101", this.frowset.getString("username"));
					}
					abean.set("groupName", this.frowset.getString("GroupName"));
					abean.set("username", this.frowset.getString("username"));
					list.add(abean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	} 
}
