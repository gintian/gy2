package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * MessageSynTrans.java
 * Description: 审批关系信息同步
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Dec 17, 2012 2:04:28 PM Jianghe created
 */
public class MessageSynTrans extends IBusiness 
{

	public void execute() throws GeneralException {
		String relation_id = (String) this.getFormHM().get("relation_id");
		String flag = "0";
		String actor_type = (String) this.getFormHM().get("actor_type");
		GenRelationBo bo = new GenRelationBo(this.frameconn, this.userView);
		//清空t_wf_mainbody表（这个表是审批关系表）的脏数据   haosl 修改
		/**
		 * 之前清空审批关系的操做实在每次查询之前，高并发的时候很影响性能，所以将删除操作放到 信息同步操作中
		 */
		bo.deleteDataFromRelation(relation_id, "", actor_type);
		if (relation_id != null)
			flag = IsMessageSyn(Integer.parseInt(relation_id));
		this.getFormHM().put("flag", flag);
	}

	public String IsMessageSyn(int relation_id) throws GeneralException {
		String flag = "0";
		try {
			List a01List = this.getA01(relation_id);
			StringBuffer a01Buf = new StringBuffer();
			for(int i=0;i<a01List.size();i++){
				a01Buf.append(" union all");
				a01Buf.append(" select ####, '" + a01List.get(i) + "' + a0100 dbase");
				a01Buf.append(" from " + a01List.get(i) + "A01");
			}
			// 需要修改
			if(a01Buf.length() > 0) {
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				StringBuffer sql = new StringBuffer();
				sql.append("update t_wf_mainbody");
				// 修改b0110
				sql.append(" set b0110=(select b0110 from ("+a01Buf.substring(11).replace("####", "b0110")+") ua where ua.dbase = mainbody_id),");
				// 修改e0122
				sql.append(" e0122=(select e0122 from ("+a01Buf.substring(11).replace("####", "e0122")+") ua where ua.dbase = mainbody_id),");
				// 修改e01a1
				sql.append(" e01a1=(select e01a1 from ("+a01Buf.substring(11).replace("####", "e01a1")+") ua where ua.dbase = mainbody_id),");
				// 修改a0101
				sql.append(" a0101=(select a0101 from ("+a01Buf.substring(11).replace("####", "a0101")+") ua where ua.dbase = mainbody_id)");
				sql.append(" where Relation_id=" + relation_id + " and upper(SUBSTRING(object_id,1,2)) not in('UM','UN','@K')");
				// oracle 数据库分割字段用SUBSTR,连接字段用||
				if (Sql_switcher.searchDbServer() == Constant.ORACEL)
					dao.update(sql.toString().replace("SUBSTRING", "SUBSTR").replace("+", "||"));
				else 
					dao.update(sql.toString());
			}
			flag = "1";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 
	 * @Title: getA01   
	 * @Description: 查询要更新的人员库
	 * @throws GeneralException 
	 * @return List
	 */
	private List getA01(int relation_id) throws GeneralException {
		List list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(SUBSTRING(mainbody_id,1,3)) usr");
			sql.append(" from t_wf_mainbody");
			sql.append(" where Relation_id=" + relation_id + " and upper(SUBSTRING(object_id,1,2)) not in('UM','UN','@K')");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			// oracle 数据库分割字段连接用SUBSTR
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				this.frowset = dao.search(sql.toString().replace("SUBSTRING", "SUBSTR"));
			else 
				this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				list.add(this.frowset.getString("usr"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
}
