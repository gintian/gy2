package com.hjsj.hrms.businessobject.gz.gz_budget.budget_revoke;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 撤销预算业务类
 * <p>Title:BudgetRevokeBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 24, 2012 5:09:26 PM</p>
 * <p>@version: 5.0</p>
 * 
 */
public class BudgetRevokeBo {
	
	
	private Connection conn = null;
	private UserView userView = null;
	HashMap xmlMap = new HashMap();
	String tabName ="";
	String suoYin = "";
	String zhuangTai = "";
	String ysparam_set = "";
	String ysparam_idx_menu = "";
	
	public BudgetRevokeBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
		BudgetSysBo sysbo=new BudgetSysBo(this.conn,this.userView);
		xmlMap = (HashMap)sysbo.getSysValueMap();
		tabName = ((String)xmlMap.get("ysze_set")).toLowerCase();//获得预算总额子集指标
		suoYin = ((String)xmlMap.get("ysze_idx_menu")).toLowerCase();//获得预算总额子集的预算索引指标
		zhuangTai= ((String)xmlMap.get("ysze_status_menu")).toLowerCase();//获得预算状态指标
		ysparam_set =((String)xmlMap.get("ysparam_set")).toLowerCase();//预算参数子集指标
		ysparam_idx_menu =((String)xmlMap.get("ysparam_idx_menu")).toLowerCase();//算参数子集的预算索引指标
	}

	
	/**
	 * 获得前台传来的Id
	 */
	public String getBudgetId(Object obj){
		DynaBean codesetbean = (LazyDynaBean)obj;
	    String budget_id=(String) codesetbean.get("budget_id");//获得前台页面传来
	    return budget_id;
	
	}
	/**
	 * 判断预算和总额的审批状态
	 */
	public String validate(String budget_id){
		String flag = "";
	    try {
			ContentDAO dao=new ContentDAO(this.conn);
		    RecordVo vo=new RecordVo("gz_budget_index");
		    vo.setString("budget_id", budget_id);
			vo=dao.findByPrimaryKey(vo);
			String spflag=vo.getString("spflag");
			if(!"01".equals(spflag)){
				    flag=("01");
			    	return flag ;
			 }
			StringBuffer hql=new StringBuffer();
			
			hql.append("select ");
			hql.append(zhuangTai+  " from "   );
			hql.append(tabName +" where " +suoYin);
			hql.append("="+budget_id);
			RowSet ros=null;
			String status="";
			ros=dao.search(hql.toString());
			while(ros.next()){
				status=ros.getString(zhuangTai);
				if(!"01".equals(status)){
					flag=("02");
					return flag; 
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;

	}
	/**
	 * 根据id获得要删除总额记录的sql语句
	 */
	public String getZongE_Sql(String budget_id){
	    StringBuffer sql=new StringBuffer();
        sql.append("delete from ");
        sql.append(tabName+" where ");
        sql.append(suoYin+"=");
        sql.append(budget_id);
	    return sql.toString();
	}
	/**
	 * 根据id获得要删除薪资预算执行表记录的sql语句
	 */
	public String getExec_Sql(String budget_id){
	    StringBuffer sql=new StringBuffer();
        sql.append("delete from gz_budget_exec  where budget_id=");
        sql.append(budget_id);
	    return sql.toString();
	}
	/**
	 * 根据id获得要删除薪资预算参数表记录的sql语句
	 */
	public String getCanShu_Sql(String budget_id){
	    StringBuffer sql=new StringBuffer();
        sql.append("delete from ");
        sql.append(ysparam_set+" where ");
        sql.append(ysparam_idx_menu+"=");
        sql.append(budget_id);
	    return sql.toString();
	}
	/**
	 * 根据id获得要删除薪资预算名册表的sql语句
	 */
	public String getSC01_Sql(String budget_id){
	    StringBuffer sql=new StringBuffer();
        sql.append("delete from SC01  where budget_id=");
        sql.append(budget_id);
	    return sql.toString();
	}
	/**
	 * 根据id获得要删除用工计划表的sql语句
	 */
	public String getSC02_Sql(String budget_id){
	    StringBuffer sql=new StringBuffer();
        sql.append("delete from SC02  where budget_id=");
        sql.append(budget_id);
	    return sql.toString();
	}
	/**
	 * 根据id获得要删除薪资预算月度明细表的sql语句
	 */
	public String getSC03_Sql(String budget_id){
	    StringBuffer sql=new StringBuffer();
        sql.append("delete from SC03  where budget_id=");
        sql.append(budget_id);
	    return sql.toString();
	}
	
	
	
	
}
