package com.hjsj.hrms.businessobject.train.station;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class TrainStationBo {
	
	/***
	 * 岗位培训指标参数
	 * @return HashMap:nbase=比对人员库,emp_setid=人员培训子集,
	 * emp_coursecloumn=人员参培指标,post_setid=岗位培训子集,post_coursecloumn=岗位培训指标
	 */
	public HashMap getStationSett(Connection conn){
		HashMap map=new HashMap();
		ConstantXml constantbo = new ConstantXml(conn,"TR_PARAM");
		String nbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		map.put("nbase", nbase);
		String emp_setid = constantbo.getTextValue("/param/post_traincourse/emp_setid");
		map.put("emp_setid", emp_setid);
		String emp_coursecloumn = constantbo.getTextValue("/param/post_traincourse/emp_coursecloumn");
		map.put("emp_coursecloumn", emp_coursecloumn);
		String post_setid = constantbo.getTextValue("/param/post_traincourse/post_setid");
		map.put("post_setid", post_setid);
		String post_coursecloumn = constantbo.getTextValue("/param/post_traincourse/post_coursecloumn");
		map.put("post_coursecloumn", post_coursecloumn);
		
		String emp_passcloumn = constantbo.getTextValue("/param/post_traincourse/emp_passcloumn");
		map.put("emp_passcloumn", emp_passcloumn);
		String emp_passvalues = constantbo.getTextValue("/param/post_traincourse/emp_passvalues");
		map.put("emp_passvalues", emp_passvalues);
		return map;
	}
	/**
	 * 比对参培信息时建立人员信息临时表
	 * @param table_name
	 * @param conn
	 * @return
	 */
	public boolean createEmpInfoTempTable(String table_name,Connection conn)throws GeneralException
	{		 
		 boolean isCorrect= false;
		 KqUtilsClass kqUtilsClass=new KqUtilsClass(conn);
		 dropTable(table_name,conn);
		 DbWizard dbWizard =new DbWizard(conn);
		 Table table=new Table(table_name);		
		 Field temp = new Field("nbase","库前缀");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("a0100","人员编号");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(20);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);		
		 temp=new Field("e01a1","岗位编号");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("post_rule","岗位参培代码");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("self_rule","岗位参培代码");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 try
		 {
			dbWizard.createTable(table);
			isCorrect=true;
		 }catch(Exception e)
		 {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		 }	
		 return isCorrect;
	}	
	public void dropTable(String tablename,Connection conn) {
		Table table = new Table(tablename);
		DbWizard dbWizard = new DbWizard(conn);
		if (dbWizard.isExistTable(tablename.toLowerCase(), false)) {
			String deleteSQL = "delete from " + tablename + "";
			ArrayList deletelist = new ArrayList();
			ContentDAO dao = new ContentDAO(conn);
			try {
				dao.delete(deleteSQL, deletelist);
				dbWizard.dropTable(table);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
