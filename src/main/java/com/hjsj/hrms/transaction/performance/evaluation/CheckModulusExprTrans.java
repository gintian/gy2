package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:检查绩效考核系数公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 19, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class CheckModulusExprTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String c_expr = (String) this.getFormHM().get("c_expr");
			c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
			c_expr=SafeCode.decode(c_expr);
			
			String khObjWhere2 = (String) this.getFormHM().get("khObjWhere2");
			khObjWhere2=khObjWhere2!=null&&khObjWhere2.trim().length()>0?khObjWhere2:"";
			khObjWhere2=SafeCode.decode(khObjWhere2);
						
			ArrayList fieldList=new ArrayList();
			
			FieldItem item=new FieldItem();
			item.setItemid("score");
			item.setItemdesc("总分");
			item.setItemtype("N");
			item.setDecimalwidth(2);
			item.setItemlength(12);			
			fieldList.add(item);
			
			item = new FieldItem();
			item.setItemid("body_id");
			item.setItemdesc("对象类别");
			item.setItemtype("N");
			item.setDecimalwidth(0);
			fieldList.add(item);
			
			item = new FieldItem();
			item.setItemid("e0122");
			item.setItemdesc("所属部门");
			item.setItemtype("A");
			item.setCodesetid("UM");
			fieldList.add(item);
			
			String planid = (String)this.getFormHM().get("planid");
			Table table = new Table("per_result_"+planid);
			DbWizard dbWizard = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
			if (!dbWizard.isExistField("per_result_"+planid, "A0100",false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
			    dbWizard.addColumns(table);// 更新列		
			    dbmodel.reloadTableModel("per_plan");
			}	
			String sqlstr = "update per_result_"+planid+" set a0100=object_id ";
			try
			{
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.update(sqlstr);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			
			YksjParser yp=new YksjParser( this.userView ,fieldList,YksjParser.forNormal, YksjParser.FLOAT,YksjParser.forPerson , "Ht", "");
			c_expr=c_expr.trim().replaceAll("\\[","");
			c_expr=c_expr.replaceAll("\\]","");
//			yp.run(c_expr,this.frameconn," 1=1 "+khObjWhere2,"per_result_"+planid);	
			
//			YksjParser yp = new YksjParser(getUserView(), fieldList, YksjParser.forSearch,YksjParser.FLOAT
//					, YksjParser.forPerson, "Ht", "");
//			yp.setCon(this.getFrameconn());
			String flag = "";
			boolean b =false;
			b=yp.Verify_where(c_expr.trim());
		//	b=yp.Verify_whereNoRetTypte(c_expr.trim());
			
			if (b) {// 校验通过
				flag="ok";
			}else{
				flag = yp.getStrError();
				flag=SafeCode.encode(flag);
			} 
			this.getFormHM().put("info", flag);
			
			if (dbWizard.isExistField("per_result_"+planid, "A0100",false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
			    dbWizard.dropColumns(table);// 更新列		
			    dbmodel.reloadTableModel("per_plan");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
