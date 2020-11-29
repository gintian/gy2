package com.hjsj.hrms.businessobject.general.operation;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

public class OperationSQLStr {
	public static String[] getstaticSql(String operationcode){
		String[] sqlstr=new String[4];
		String column="operationname,tabid,name,flag,sp_flag,ctrl_para,operationcode";
		String sql="select operationname=oper.operationname,tabid,name,flag,sp_flag,ctrl_para,tt.operationcode";
		String where="";
		if(operationcode.length()==2){
			where=" from t_wf_define  tt left join (select * from operation)oper on oper.operationcode=tt.operationcode where tt.operationcode like '"+operationcode+"%'  ";
		}else{
			where=" from t_wf_define tt left join (select * from operation)oper on oper.operationcode=tt.operationcode where tt.operationcode = '"+operationcode+"'  ";
		}
		String orderby="order by tt.operationcode";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return sqlstr;
	}
	/***
	 * 求业务分类的模板列表
	 * @param operationcode
	 * @return
	 */
	public static String[] getCustomSql(String operationcode,Connection conn){
		String[] sqlstr=new String[4];
		String column="operationname,tabid,name,flag,sp_flag,ctrl_para,operationcode";
		String sql="select "+column;
		String where="";
		if("-1".equals(operationcode))
		{
			//where=" from template_table where operationcode like '"+operationcode+"%'  ";
			where=" from template_table where 1=1 ";
		}else{
			where=" from template_table where operationcode like '"+operationcode+"%'  ";			
		}
		

		/* 装载业务模板节点,分业务类型加载模板
		 * =1,国家机关
		 * =2,事业单位
		 * =3,企业单位
		 * =4,军队使用
		 * =5,其    它 */
		String unit_type=null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
		if(unit_type==null|| "".equals(unit_type)) {
            unit_type="3";
        }
		//考虑单位性质
		where+=" and (";
		String units[]=unit_type.split(",");
		for(int i=0;i<units.length;i++)
		{
			if(units[i]==null||units[i].trim().length()==0) {
                continue;
            }
			where+=" flag ="+Integer.parseInt(units[i]);
			if(i<units.length-1) {
                where+=" or ";
            }
		}			
		where+=")";
		
		
		String orderby="order by operationcode,tabid";//xcs modify 2013-10-29 增加在某个模版分类后按照tabid进行排序
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return sqlstr;
	}
	public static String getOperationname(ContentDAO dao,String operationcode,String flag) throws SQLException, GeneralException{
		StringBuffer sbsel=new StringBuffer();
		String sql="";
		if(operationcode.length()==2){
			sql="select * from operation where operationcode like '"+operationcode+"__'";
		}else{
			if("1".equals(flag)){
				sql="select * from operation where operationcode like '"+operationcode.substring(0,2)+"__'";
			}else{
				sql="select * from operation where operationcode = '"+operationcode+"'";
			}
		}
		RowSet rs=dao.search(sql);
		sbsel.append("<select name='t_wf_defineVo.string(operationcode)'>");
		if(!rs.next()){
			
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.message.add.fails"),"",""));

		}else{
			sbsel.append("<option value='"+rs.getString("operationcode")+"'>");
			sbsel.append(rs.getString("operationname"));
			sbsel.append("</option>");
		}
		while(rs.next()){
			sbsel.append("<option value='"+rs.getString("operationcode")+"'>");
			sbsel.append(rs.getString("operationname"));
			sbsel.append("</option>");
		}
		
		sbsel.append("</select>");
		return sbsel.toString();
	}
	public static String getvalideflag(String flag){
		StringBuffer sbch=new StringBuffer();
		if("1".equals(flag)){
			sbch.append("<input type=\"checkbox\" name=\"validateflag\" checked=\"checked\" value=\"1\"/>");
		}else{
			sbch.append("<input type=\"checkbox\" name=\"validateflag\"  value=\"0\"/>");
		}
		return sbch.toString();
	}
}
