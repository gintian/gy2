package com.hjsj.hrms.transaction.performance.evaluation.expressions;

import com.hjsj.hrms.transaction.performance.LoadXml;
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
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:CheckFormulaTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 23, 2008:5:12:09 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class CheckFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String temp = (String)this.formHM.get("temp");
		String formula = (String)this.formHM.get("formula");
		formula=formula!=null&&formula.trim().length()>0?formula:"";
		formula = SafeCode.decode(formula);
		ArrayList filelist = new ArrayList();
		FieldItem item = new FieldItem();
		item.setItemid("original_score");
		item.setItemdesc("本次得分");
		item.setItemtype("N");
		item.setItemlength(8);
		item.setDecimalwidth(2);
		filelist.add(item);
		
		item = new FieldItem();
		item.setItemid("body_id");
		item.setItemdesc("对象类别");
		item.setItemtype("N");
		item.setDecimalwidth(0);
		filelist.add(item);
		
		item = new FieldItem();
		item.setItemid("e0122");
		item.setItemdesc("所属部门");
		item.setItemtype("A");
		item.setCodesetid("UM");
		filelist.add(item);
		
		
		String planid = (String)this.getFormHM().get("planid");
		String khObjWhere2 =  (String)this.getFormHM().get("khObjWhere2");
		khObjWhere2=khObjWhere2!=null&&khObjWhere2.trim().length()>0?khObjWhere2:"";
		khObjWhere2=SafeCode.decode(khObjWhere2);
		
		LoadXml loadxml = new LoadXml(this.frameconn,planid,"");
		ArrayList planlist = loadxml.getRelatePlanValue("Plan","ID");
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_plan ");
		if(planlist.size()>0){
			sql.append(" where plan_id in (");
			for(int i=0;i<planlist.size();i++){
				sql.append("'"+planlist.get(i).toString().trim()+"',");
			}
			sql.setLength(sql.length()-1);
			sql.append(")");
			try {
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					item = new FieldItem();
					item.setItemid("G_"+this.frowset.getInt("plan_id"));
					item.setItemdesc(this.frowset.getString("name"));
					item.setItemtype("N");
					item.setItemlength(8);
					item.setDecimalwidth(2);
					filelist.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		String flag = "",fsql = "";
		
		if (formula != null && formula.trim().length() > 0)
		{
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
			    dbmodel.reloadTableModel("per_result_"+planid);
			}	
			String sqlstr = "update per_result_"+planid+" set a0100=object_id where 1=1 "+khObjWhere2;
			try
			{
				dao.update(sqlstr);
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			YksjParser yp = new YksjParser(getUserView(), filelist, YksjParser.forSearch,YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
//			yp.setCon(this.getFrameconn());
			
			YksjParser yp=new YksjParser( this.userView ,filelist,YksjParser.forNormal, YksjParser.FLOAT,YksjParser.forPerson , "Ht", "");
			formula=formula.replaceAll("\\[","");
			formula=formula.replaceAll("\\]","");
//			yp.run(formula,this.frameconn," 1=1 "+khObjWhere2,"per_result_"+planid);			

			boolean b =false;
			b=yp.Verify_where(formula.trim());			
		
			if (b) // 校验通过
			{
				flag="ok";
				fsql = yp.getSQL();
			}
			else
			{
				flag = yp.getStrError();
			}
			
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
			
		}else{
			flag="ok";
		}
		
		if(temp!=null&&temp.length()>0&& "save".equalsIgnoreCase(temp)){
			this.getFormHM().put("temp",temp);
		}
		this.getFormHM().put("fsql",SafeCode.encode(fsql));
		this.getFormHM().put("mess",SafeCode.encode(flag));
		this.getFormHM().put("planid",planid);
	}

}
