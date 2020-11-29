package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * SearchAppiaryLeaderTrans.java
 * Description: 查找报批的领导
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 6, 2013 11:05:33 AM Jianghe created
 */
public class SearchAppiaryLeaderTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		ArrayList list = new ArrayList();
		String nbase = (String)this.userView.getHm().get("nbase");
		String a0100 = (String)this.userView.getHm().get("a0100");
		String sp_relation = (String)this.getFormHM().get("sp_relation"); // 审批关系
		String state = (String)this.getFormHM().get("state"); 
		String personPage = (String)this.getFormHM().get("personPage"); 
		String p0100 = (String)this.getFormHM().get("p0100"); 
		ArrayList listldb = getSuperiorUser(nbase, a0100, sp_relation, new ContentDAO(this.getFrameconn()));
		for (int i = 0; i < listldb.size(); i++) 
		{
			LazyDynaBean ldb=(LazyDynaBean) listldb.get(i);
			list.add(ldb.get("a0100")+":"+ldb.get("a0101"));
		}
		this.getFormHM().put("outname", list);
		this.getFormHM().put("state", state);
		this.getFormHM().put("personPage", personPage);
		this.getFormHM().put("p0100", p0100);
	}
	/**
	 * 得到直接上级姓名
	 * @param a0100
	 * @param dao
	 * @return
	 */
	public ArrayList getSuperiorUser(String nbase,String a0100,String sp_relation,ContentDAO dao) throws GeneralException 
	{
		String sql="";
		RowSet rs = null;
		ArrayList list = new ArrayList();
		NworkPlanBo bo = new NworkPlanBo(this.getUserView(),this.getFrameconn());
		try 
		{
			RecordVo vo = new RecordVo(nbase+"a01");
			vo.setString("a0100", a0100);
			vo = dao.findByPrimaryKey(vo);
			String b0110 = vo.getString("b0110");
			String e0122 = vo.getString("e0122");
			String e01a1 = vo.getString("e01a1");
		// 参数设置中设置了审批关系就按设置的走，否则按之前的日志（考核关系）走
		if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
		{
			sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
			if(!bo.isHaveResults(sql,dao)){
				//先判断岗位
				sql=bo.getSuperSql(9, sp_relation, e01a1, "", "");
				//再判断部门
				if(!bo.isHaveResults(sql,dao)){
				  sql=bo.getSuperSql(9, sp_relation, "", e0122, "");
				}
				//再判断单位
				if(!bo.isHaveResults(sql,dao)){
				  sql=bo.getSuperSql(9, sp_relation, "", "", b0110);
				}
			}
		}else
		{	
				sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sql+=" level_o";
				else
					sql+=" level ";
				//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
				String body_id="1";
				sql+="="+body_id+"  and pmb.object_id='"+a0100+"'";
			
		}
			if(!"".equals(sql.trim())){
				rs = dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("a0101", rs.getString("a0101"));
					if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
						bean.set("a0100", rs.getString("mainbody_id"));
					else
						bean.set("a0100", "Usr"+rs.getString("mainbody_id"));
					list.add(bean);
				}
			}
		} catch (SQLException e) 
		{
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}	
}
