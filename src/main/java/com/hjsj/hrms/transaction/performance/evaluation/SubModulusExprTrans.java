package com.hjsj.hrms.transaction.performance.evaluation;

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
 * 
 *<p>Title:</p> 
 *<p>Description:提交绩效系数公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 19, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SubModulusExprTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
//			对计算的考核对象范围的限制 也就是只对界面看到的考核对象计算
			String khObjWhere2 = (String)this.getFormHM().get("khObjWhere2");
			khObjWhere2=SafeCode.decode(khObjWhere2);			
			
			String c_expr = (String) this.getFormHM().get("c_expr");
			c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
			c_expr=SafeCode.decode(c_expr);
			String planid=(String)this.getFormHM().get("planid");
			String isReCalcu = (String)this.getFormHM().get("isReCalcu");
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
			String xiFormula=c_expr.trim();
			c_expr=c_expr.trim().replaceAll("\\[","");
			c_expr=c_expr.replaceAll("\\]","");
						
			String flag = "";
			boolean b =false;
			b=yp.Verify_where(c_expr.trim());
		//	b=yp.Verify_whereNoRetTypte(c_expr.trim());

			if (b) 
			{// 校验通过
				flag="ok";
				
				yp.run(c_expr,this.frameconn,"","per_result_"+planid);
				
				LoadXml loadXml=new LoadXml(this.getFrameconn(),planid,"");
				loadXml.saveAttribute("PerPlan_Parameter", "xiFormula", xiFormula);				
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				StringBuffer buf = new StringBuffer();
				buf.append("update per_result_"+planid+" set exX_object=("+yp.getSQL()+") where 1=1 ");
				if(khObjWhere2.length()>0)	
					buf.append(khObjWhere2);
				if(isReCalcu!=null && "ok".equalsIgnoreCase(isReCalcu))
					dao.update(buf.toString());
				
				if(c_expr.trim().length()>0)
//				    yp.run(c_expr.trim());
				{
					
				}
				else//如果内容为空	
				{
				    buf = new StringBuffer();
				    buf.append("UPDATE PER_RESULT_"+planid);
				    buf.append(" SET exX_object=per_degreedesc.xishu FROM per_degreedesc WHERE PER_RESULT_"+planid);
				    buf.append(".grade_id=per_degreedesc.id ");
				    if(khObjWhere2.length()>0)		
				    {
				    	buf.append(" and PER_RESULT_"+planid+".object_id in (select object_id from per_object where 1=1 ");
				    	buf.append(khObjWhere2+")");
				    }				    	
				    if(isReCalcu!=null && "ok".equalsIgnoreCase(isReCalcu))
				    	dao.update(buf.toString());

				}
			}
			else
			{
				flag = yp.getStrError();
				flag=SafeCode.encode(flag);
			}
			if (!dbWizard.isExistField("per_result_"+planid, "A0100",false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
			    dbWizard.dropColumns(table);// 更新列		
			    dbmodel.reloadTableModel("per_plan");
			}
			this.getFormHM().put("info", flag);	
			this.getFormHM().put("isReCalcu", isReCalcu);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
