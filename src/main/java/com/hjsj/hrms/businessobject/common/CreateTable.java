package com.hjsj.hrms.businessobject.common;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateTable {
	private Connection con=null;
	public CreateTable(Connection conn){
		this.con=conn;
	}
	/**
	 * 复制临时表
	 * @param SrcTab 数据源
	 * @param DestTab 临时表名,
	 * @param StrFldlst 字段列表
	 * @param strWhere 条件
	 * @param strGroupBy 分组
	 */
	public void copyTable(String SrcTab,String DestTab,String StrFldlst,
			String strWhere,String strGroupBy){
		StringBuffer sqlstr = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.con);
		if(Sql_switcher.searchDbServer()==Constant.MSSQL){
			sqlstr.append("select ");
			sqlstr.append(StrFldlst);
			sqlstr.append(" Into ");
			sqlstr.append(DestTab);
			sqlstr.append("  from ");
			sqlstr.append(SrcTab);
			if(strWhere!=null&&strWhere.trim().length()>0){
				sqlstr.append(" where ");
				sqlstr.append(strWhere);
			}
			if(strGroupBy!=null&&strGroupBy.trim().length()>0){
				sqlstr.append(" group by  ");
				sqlstr.append(strGroupBy);
			}
		}else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
			sqlstr.append("Create Table ");
			sqlstr.append(DestTab);
			sqlstr.append(" as select  ");
			sqlstr.append(StrFldlst);
			sqlstr.append("  from ");
			sqlstr.append(SrcTab);
			if(strWhere!=null&&strWhere.trim().length()>0){
				sqlstr.append(" where ");
				sqlstr.append(strWhere);
			}
			if(strGroupBy!=null&&strGroupBy.trim().length()>0){
				sqlstr.append(" group by  ");
				sqlstr.append(strGroupBy);
			}
		}else if(Sql_switcher.searchDbServer()==Constant.DB2){
			StringBuffer buf = new StringBuffer();
			buf.append(" SELECT  ");
			buf.append(StrFldlst);
			buf.append(" Into ");
			buf.append(DestTab);
			buf.append("  from ");
			buf.append(SrcTab);
			if(strWhere!=null&&strWhere.trim().length()>0){
				buf.append(" where ");
				buf.append(strWhere);
			}
			if(strGroupBy!=null&&strGroupBy.trim().length()>0){
				buf.append(" group by  ");
				buf.append(strGroupBy);
			}
			
			try {
				dao.update("Create Table "+DestTab+" AS("+buf.toString()+") DEFINITION ONLY");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sqlstr.setLength(0);
			sqlstr.append("INSERT INTO ");
			sqlstr.append(DestTab);
			sqlstr.append(buf.toString());
		}else{
			sqlstr.append("select ");
			sqlstr.append(StrFldlst);
			sqlstr.append(" Into ");
			sqlstr.append(DestTab);
			sqlstr.append("  from ");
			sqlstr.append(SrcTab);
			if(strWhere!=null&&strWhere.trim().length()>0){
				sqlstr.append(" where ");
				sqlstr.append(strWhere);
			}
			if(strGroupBy!=null&&strGroupBy.trim().length()>0){
				sqlstr.append(" group by  ");
				sqlstr.append(strGroupBy);
			}
		}
		try {
			dao.update(sqlstr.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
