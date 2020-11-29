package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Hashtable;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:显示绩效系数公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 19, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class ShowPerModulusTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String planid=(String)this.getFormHM().get("planid");
			LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			String xiFormula=(String)htxml.get("xiFormula");
			this.getFormHM().put("expr", xiFormula);
			
			String gradeFormula = "0";
			String procedureName = "";
			String gradeFormulaExpre=(String)htxml.get("GradeFormula");
			if(gradeFormulaExpre!=null && gradeFormulaExpre.length()>0)
			{
				String[] gradeFormulaExpres =  gradeFormulaExpre.split(";");
				gradeFormula  = gradeFormulaExpres[0];
				if(gradeFormulaExpres.length==1)
					procedureName = "";
				else
					procedureName = gradeFormulaExpres[1];
			}
			this.getFormHM().put("gradeFormula", gradeFormula);
			this.getFormHM().put("procedureName", procedureName);
			this.getFormHM().put("planid", planid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
