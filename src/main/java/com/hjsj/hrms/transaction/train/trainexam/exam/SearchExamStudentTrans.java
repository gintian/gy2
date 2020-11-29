package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamStudentBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchExamPlanTrans
 * </p>
 * <p>
 * Description:查询考试人员
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-24
 * </p> 
 * @author zxj
 *
 */
public class SearchExamStudentTrans extends IBusiness {

	public void execute() throws GeneralException
	{ 	
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String a_code = (String)this.getFormHM().get("a_code");
		String a_code1 = a_code;
		if(a_code==null||a_code.length()<3){
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
			//if(a_code!=null&&a_code.indexOf("`")!=-1)
			//	a_code = a_code.split("`")[0];
		}
		
		
		String r5400 = (String)hm.get("planid");
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		
		TrainExamPlanBo planBo = new TrainExamPlanBo(this.frameconn);
		String planStatus = planBo.getPlanStatus(r5400); 
		
		//sql字段
		StringBuffer columns = new StringBuffer();
		ArrayList fields = DataDictionary.getFieldList("R55", Constant.USED_FIELD_SET);
		for(int i=0;i<fields.size();i++)
		{
			FieldItem item = (FieldItem)fields.get(i);
			columns.append(item.getItemid());
			if(i<(fields.size()-1))
				columns.append(",");
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select "+columns.toString());		

		StringBuffer where = new StringBuffer(" FROM R55");
		where.append(" WHERE r5400=" + r5400);
		
		if((a_code != null)&&(a_code.length()>2)&&a_code.indexOf("UN`")==-1)
		{
//			String org = a_code.substring(0, 2);
//			String orgId = a_code.substring(2);
//			
//			if(org.equalsIgnoreCase("UN"))
//			{
//				where.append(" AND B0110 LIKE '");				
//			}
//			else if(org.equalsIgnoreCase("UM"))
//			{
//				where.append(" AND E0122 LIkE '");
//			}
//			else
//			{
//				where.append(" AND E01A1 LIkE '");
//			}
//			
//			where.append(orgId);
//			where.append("%'");
			String tmp[] = a_code.split("`");
			where.append(" and (");
			for (int i = 0; i < tmp.length; i++) {
				if(tmp[i]==null||tmp[i].length()<3)
					continue;
				String t = tmp[i];
				if(t.toUpperCase().startsWith("UN"))
				{
					where.append(" B0110 LIKE '");	
					where.append(t.substring(2));
					where.append("%' or");
				}
				else if(t.toUpperCase().startsWith("UM"))
				{
					where.append(" E0122 LIKE '");
					where.append(t.substring(2));
					where.append("%' or");
				}
				else if(t.toUpperCase().startsWith("@K"))
				{
					where.append(" E01A1 LIKE '");
					where.append(t.substring(2));
					where.append("%' or");
				}
				else
				{
					where.append(" (NBASE='");
					where.append(t.substring(0, 3));
					where.append("' and A0100 = '");
					where.append(t.substring(3));
					where.append("') or");
				}
			}
			where.setLength(where.length()-3);
			where.append(")");
		}
		
		String paperStatus = (String)this.getFormHM().get("paperStatus");
        if ((paperStatus!=null)&&(paperStatus.length()>0))
        {
        	if (!("-9".equalsIgnoreCase(paperStatus)))
                where.append(" AND R5513=" + paperStatus);
        }
          
		String checkStatus = (String)this.getFormHM().get("checkStatus");
        if ((checkStatus!=null)&&(checkStatus.length()>0))
        {
        	if (!("-9".equalsIgnoreCase(checkStatus)))
                where.append(" AND R5515=" + checkStatus);
        }
        
        String studentName = (String)this.getFormHM().get("studentName");
        if ((studentName!=null)&&(studentName.length()>0))
        {
        	where.append(" AND A0101 LIKE '%");
        	where.append(studentName);
        	where.append("%'");
        }
        
		TrainExamStudentBo examStudentBo = new TrainExamStudentBo(this.frameconn);
		ArrayList paperStatusList = examStudentBo.getPaperStatusList();
		ArrayList checkStatusList = examStudentBo.getCheckStatusList();
		String trainDBPres = examStudentBo.getStudentDBPre(this.userView.getPrivDbList()); 
		
		this.getFormHM().put("a_code", a_code1);
		this.getFormHM().put("sqlstr",sql.toString());		
		this.getFormHM().put("column",columns.toString());
		this.getFormHM().put("where",where.toString());
		this.getFormHM().put("paperStatusList", paperStatusList);
		this.getFormHM().put("checkStatusList", checkStatusList);
		this.getFormHM().put("examDBPres", trainDBPres);
		this.getFormHM().put("r5400", SafeCode.encode(PubFunc.encrypt(r5400)));
		this.getFormHM().put("planStatus", planStatus);
		this.getFormHM().put("studentName", studentName);
		
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		this.getFormHM().put("uplevel", uplevel);

	}	
	
}
