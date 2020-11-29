package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class DelOperatioinTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap operationtypemap=new HashMap();
		operationtypemap.put("0","0");
		operationtypemap.put("1","1");
		operationtypemap.put("2","2");
		operationtypemap.put("3","3");
		RecordVo operationVo =new RecordVo("operation");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String operationid =(String) hm.get("operationid");
		String operationcode=(String)hm.get("operationcode");
		String sql="";
		if(operationcode.length()>2){
			 sql="select tabid from template_table where operationcode like '"+
			 operationcode+"' union select tabid from t_wf_define where operationcode like '"+
			 operationcode+"' ";
		}else{
			sql="select tabid from template_table where operationcode like '"+
			operationcode+"__' union select tabid from t_wf_define where operationcode like '"+
			operationcode+"__' ";
		}
		try {
			RowSet rs=dao.search(sql);
			if(rs.next()){
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.message.delfail.used"),"",""));
			}else{
				operationVo.setString("operationid",operationid);
				operationVo=dao.findByPrimaryKey(operationVo);
				if(operationtypemap.containsKey(operationVo.getString("operationtype"))){
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.message.delfail.sys"),"",""));
				}else{
					delOperation(operationcode);
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.message.delfail"),"",""));
		}

	}
	public void delOperation(String operationcode) throws SQLException{
		Connection conn=this.getFrameconn();
		String sql="";
		
		if(operationcode.length()==2){
			sql="delete from operation where operationcode like '"+operationcode+"%'";
			
		}else{
			sql="delete from operation where operationcode like '"+operationcode+"'";
			
		}
		ContentDAO dao = new ContentDAO(conn);
		dao.update(sql);
		
		conn.close();
	}

}
