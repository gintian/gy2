package com.hjsj.hrms.transaction.performance.evaluation.expressions;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
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
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>
 * Title:SaveExprTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * Create time:Jun 23, 2008:5:12:13 PM
 * </p>
 * 
 * @author huaitao
 * @version 1.0
 */
public class SaveExprTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		// 对计算的考核对象范围的限制 也就是只对界面看到的考核对象计算
		String khObjWhere2 = (String) this.getFormHM().get("khObjWhere2");

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String fsql = (String) hm.get("fsql");
		String isReCalcu = (String) hm.get("isReCalcu");

		String planid = (String) this.getFormHM().get("planid");
		String formula = (String) this.getFormHM().get("formula");
		fsql = SafeCode.decode(fsql);
		LoadXml loadxml = new LoadXml(this.frameconn, planid, "");
		ArrayList list = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("Caption", formula);
		bean.set("Value", fsql);
		list.add(bean);
		ArrayList idlist = new ArrayList();
		idlist.add("Caption");
		idlist.add("Value");
		loadxml.saveRelatePlanValue("Formula", idlist, list);
		if (fsql.trim().length() == 0)
			return;
		String sql = "update per_result_" + planid + " set score = " + fsql;
		if (khObjWhere2.length() > 0)
			sql += " where 1=1 " + khObjWhere2;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			if (isReCalcu != null && "ok".equalsIgnoreCase(isReCalcu))
			{
				//算总分
				dao.update(sql);
				PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn(), planid, "", this.userView);
				bo.updateGroupFields();

				// 算完总分 如果系数没有设置公式,先算等级,再算系数。 如果系数设置了公式，先算系数再算等级
				Hashtable htxml = new Hashtable();
				htxml = loadxml.getDegreeWhole();
				String xiFormula = (String) htxml.get("xiFormula");
				String gradeFormulaExpre=(String)htxml.get("GradeFormula");
				if (xiFormula == null || xiFormula != null && xiFormula.trim().length() == 0)// 如果系数没有设置公式,先算等级,再算系数。
				{
					//算等级
//					String gradeFormula = "0";
//					String procedureName = "";					
//					if(gradeFormulaExpre!=null && gradeFormulaExpre.length()>0)
//					{
//						String[] gradeFormulaExpres =  gradeFormulaExpre.split(":");
//						gradeFormula  = gradeFormulaExpres[0];
//						if(gradeFormulaExpres.length==1)
//							procedureName = "";
//						else
//							procedureName = gradeFormulaExpres[1];
//					}					
//					
//					if(gradeFormula.equals("0"))
//					{
//						
//					}else if(gradeFormula.equals("1"))					
//					{
//						
//					}
					//计算等级 会同时计算出系数 因为系数公式为空时候系数由等级来
					String gradeID = (String)htxml.get("GradeClass");
					bo.setGradeValue(gradeID,1);  
					//算系数
//					StringBuffer buf = new StringBuffer();
//				    buf.append("UPDATE PER_RESULT_"+planid);
//				    buf.append(" SET exX_object=per_degreedesc.xishu FROM per_degreedesc WHERE PER_RESULT_"+planid);
//				    buf.append(".grade_id=per_degreedesc.id ");
//				    if(khObjWhere2.length()>0)		
//				    {
//				    	buf.append(" and PER_RESULT_"+planid+".object_id in (select object_id from per_object where 1=1 ");
//				    	buf.append(khObjWhere2+")");
//				    }
					
				} else
				// 如果系数设置了公式，先算系数再算等级
				{
					// 算系数
					Table table = new Table("per_result_" + planid);
					DbWizard dbWizard = new DbWizard(this.frameconn);
					DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
					if (!dbWizard.isExistField("per_result_" + planid, "A0100", false))
					{
						Field obj = new Field("A0100");
						obj.setDatatype(DataType.STRING);
						obj.setLength(8);
						obj.setKeyable(false);
						table.addField(obj);
						dbWizard.addColumns(table);// 更新列
						dbmodel.reloadTableModel("per_plan");
					}
					String sqlstr = "update per_result_" + planid + " set a0100=object_id ";
					try
					{
						dao.update(sqlstr);
					} catch (SQLException e)
					{
						e.printStackTrace();
					}

					ArrayList fieldList = new ArrayList();

					FieldItem item = new FieldItem();
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

					YksjParser yp = new YksjParser(this.userView, fieldList, YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
					xiFormula = xiFormula.trim().replaceAll("\\[", "");
					xiFormula = xiFormula.replaceAll("\\]", "");

					yp.run(xiFormula, this.frameconn, "", "per_result_" + planid);

					LoadXml loadXml = new LoadXml(this.getFrameconn(), planid, "");
					loadXml.saveAttribute("PerPlan_Parameter", "xiFormula", xiFormula);
					StringBuffer buf = new StringBuffer();
					buf.append("update per_result_" + planid + " set exX_object=(" + yp.getSQL() + ") where 1=1 ");
					if (khObjWhere2.length() > 0)
						buf.append(khObjWhere2);
					dao.update(buf.toString());

					if (!dbWizard.isExistField("per_result_" + planid, "A0100", false))
					{
						Field obj = new Field("A0100");
						obj.setDatatype(DataType.STRING);
						obj.setLength(8);
						obj.setKeyable(false);
						table.addField(obj);
						dbWizard.dropColumns(table);// 更新列
						dbmodel.reloadTableModel("per_plan");
					}
					// 算等级
					String gradeID = (String)htxml.get("GradeClass");
					bo.setGradeValue(gradeID,1);
				}
			}

		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

}
