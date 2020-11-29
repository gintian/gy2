package com.hjsj.hrms.businessobject.gz.gz_budget.budget_add;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 
 * 新建预算业务类
 * <p>Title:BudgetAddBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 24, 2012 5:31:24 PM</p>
 * <p>@version: 5.0</p>
 * 
 */
	public class BudgetAddBo {
		private Connection conn = null;
		private UserView userView = null;
		
		public BudgetAddBo(Connection conn, UserView userView)
		{
			this.conn = conn;
			this.userView = userView;
			
		}
		
		
		
		
	/**
	 * 获得一级预算单位编码列 （''，''，）
	 * @return
	 * @throws GeneralException
	 */
	
	public String getUnitno() throws GeneralException {
		BudgetSysBo sysbo=new BudgetSysBo(this.conn,this.userView);
		String units = (String)sysbo.getSysValueMap().get("units");//获得一级预算单位编码
		String[] str=units.split(",");
		String unitno = "('')";
		if(str.length!=0){
			unitno="('";
			for(int i=0;i<str.length;i++){
				if(i==str.length-1){
					unitno +=str[i]+"')";
				}else{
					unitno +=str[i]+"','";
				}
		}
	}
		return unitno;
	
	}

	
/**
 * 	获得顶级单位编码
 * @return
 * @throws GeneralException
 */
	public String getCodeitemid() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		String codeitemid="";
		String unitno=this.getUnitno();
		StringBuffer jgsql=new StringBuffer();
		/* 薪资预算-预算分配，点新增预算，确定的时候报错(oracle库) xiaoyun 2014-10-25 start */
		if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
			jgsql.append("select codeitemid from organization ,( select codeitemid AS elseitemid from organization where codeitemid in ");
			jgsql.append(unitno+")A ");
			jgsql.append("where codeitemid=substr(a.elseitemid,0,length(codeitemid))  and codeitemid=parentid and rownum<2");
		}else if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
			jgsql.append("select top 1 codeitemid from organization ,( select codeitemid AS elseitemid from organization where codeitemid in ");
			jgsql.append(unitno+")A ");
			jgsql.append("where codeitemid=left(a.elseitemid,len(codeitemid))  and codeitemid=parentid");
		}
		/* 薪资预算-预算分配，点新增预算，确定的时候报错(oracle库) xiaoyun 2014-10-25 end */
		try {
			rowSet = dao.search(jgsql.toString());
			while(rowSet.next()){
				 codeitemid = rowSet.getString("codeitemid");
			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return codeitemid;	
	}
/**
 * 	获得一级预算单位名称列 ，...，...，
 * @return
 * @throws GeneralException
 */
	public String getUnitname() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSets=null;
		String codeitemdesc="";
		String unitno=this.getUnitno();
		StringBuffer ssql=new StringBuffer();
		String unitname="";
		ssql.append("select codeitemdesc from organization where codesetid='UN' and codeitemid in");
		ssql.append(unitno);
		try {
			rowSets = dao.search(ssql.toString());
			while(rowSets.next()){
				codeitemdesc = rowSets.getString("codeitemdesc");
				unitname +=codeitemdesc+",";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		return unitname;
		
	}
	/**
	 * 根据机构编码获得预算总额表的顺序号i9999
	 */
	public int getId(String codeitemid) throws GeneralException {
		 BudgetSysBo sysbo=new BudgetSysBo(this.conn,this.userView);
		 String tabName = (String)sysbo.getSysValueMap().get("ysze_set");//获得预算总额子集
		 StringBuffer sql=new StringBuffer();
		 sql.append("select max(i9999) as i9999 from ");
		 sql.append(tabName);
		 sql.append(" where b0110 ='"+codeitemid+"'");
		 ContentDAO dao=new ContentDAO(this.conn);
		 RowSet ros=null;
		 int id=0;
		 try {
			 ros=dao.search(sql.toString());
			 while(ros.next()){
				  id=ros.getInt("i9999")+1;
			 }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return id;
	
	}
	/**
	 * 根据机构编码获得预算参数表的顺序号i9999
	 */
	public int getCanShuId(String codeitemid) throws GeneralException {
		 BudgetSysBo sysbo=new BudgetSysBo(this.conn,this.userView);
		 String tabName = (String)sysbo.getSysValueMap().get("ysparam_set");//获得预算总额子集
		 StringBuffer sql=new StringBuffer();
		 sql.append("select max(i9999) as i9999 from ");
		 sql.append(tabName);
		 sql.append(" where b0110 ='"+codeitemid+"'");
		 ContentDAO dao=new ContentDAO(this.conn);
		 RowSet ros=null;
		 int id=0;
		 try {
			 ros=dao.search(sql.toString());
			 while(ros.next()){
				  id=ros.getInt("i9999")+1;
			 }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return id;
	
	}
	/**
	 * 获得每年预算(索引表，总额表)的次数i9999
	 */
	public int getI9999(Object vo){
		 ContentDAO dao=new ContentDAO(this.conn);
		 DynaBean codesetbean = (LazyDynaBean) vo;//获得前台页面传来的对象
		 String yearnum=(String) codesetbean.get("yearnum");//获得前台弹出页面传来的值
		 StringBuffer sql=new StringBuffer();
		 sql.append("select max(i9999) as i9999 from gz_budget_index where yearnum= ");
		 sql.append(yearnum);
		 RowSet ros=null;
		 int i9999 = 0;
		 try {
			ros=dao.search(sql.toString());
			while(ros.next()){
				i9999= ros.getInt("i9999");
		    }
		} catch (SQLException e) {
			e.printStackTrace();			
		}
		
		return i9999;
	}
	/**
	 * 获得每年预算(预算参数表)的次数i9999
	 */
	public int getNum(String riqi,String b0110){
		BudgetSysBo sysbo=new BudgetSysBo(this.conn,this.userView);
		String tabName = ((String)sysbo.getSysValueMap().get("ysparam_set")).toLowerCase();
		ContentDAO dao=new ContentDAO(this.conn);
		 StringBuffer sql=new StringBuffer();
		 sql.append("select max("+tabName+"z1) as num from "+ tabName +" where "+ Sql_switcher.dateToChar(tabName+"z0", "YYYY-MM-DD")+"='");
		 sql.append(riqi+"' and b0110='"+b0110+"'");
		
		 RowSet ros=null;
		 int num = 0;
		 try {
			ros=dao.search(sql.toString());
			while(ros.next()){
				num= ros.getInt("num");
		    }
		} catch (SQLException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return num;
	}

	/**
	 * 获得每年预算(预算总额表)的次数i9999
	 */
	public int getNumZe(String riqi,String b0110){
		BudgetSysBo sysbo=new BudgetSysBo(this.conn,this.userView);
		String tabName = ((String)sysbo.getSysValueMap().get("ysze_set")).toLowerCase();
		ContentDAO dao=new ContentDAO(this.conn);
		 StringBuffer sql=new StringBuffer();
         sql.append("select max("+tabName+"z1) as num from "+ tabName +" where "+ Sql_switcher.dateToChar(tabName+"z0", "YYYY-MM-DD")+"='");
         sql.append(riqi+"' and b0110='"+b0110+"'");
		 RowSet ros=null;
		 int num = 0;
		 try {
			ros=dao.search(sql.toString());
			while(ros.next()){
				num= ros.getInt("num");
		    }
		} catch (SQLException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return num;
	}
}
