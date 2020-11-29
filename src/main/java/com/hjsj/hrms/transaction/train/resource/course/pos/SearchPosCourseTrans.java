package com.hjsj.hrms.transaction.train.resource.course.pos;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchPosCourseTrans extends IBusiness {

	public void execute() throws GeneralException {

		String state=(String)this.getFormHM().get("state");
		String a_code=(String)this.getFormHM().get("a_code");
		String backdate = (String)this.getFormHM().get("backdate");
		String codesetid=(String)a_code.substring(0,2);
		String codeitemid=a_code.substring(2);
		StringBuffer sbsql=new StringBuffer("select job_id,t.r5000 r5000,r5003,r5012,r5009,r5007");
		StringBuffer sqlwhere=new StringBuffer("from tr_job_course t left join r50 r on t.r5000=r.r5000 left join codeitem c on t.job_id=c.codeitemid ");
		try{
			if(codeitemid.length()>0){
				sqlwhere.append(" where t.job_id="+Sql_switcher.substr("'"+codeitemid+"'", "1", Sql_switcher.length("t.job_id"))+" and t.state="+state);
				//sqlwhere.append(" where t.job_id like '"+codeitemid+"%' and t.state="+state);
			}else{
				sqlwhere.append(" where t.state="+state);
			}
			sqlwhere.append(" and c.codesetid='"+codesetid + "'");
			
			if ((null != backdate) && (!"".equals(backdate.trim())) && (0 < backdate.trim().length()))
			{
			    sqlwhere.append(" and "+com.hrms.hjsj.utils.Sql_switcher.dateValue(backdate));
			    sqlwhere.append(" between ");
			    sqlwhere.append(Sql_switcher.isnull("c.start_date", Sql_switcher.dateValue("1970-01-01")));
			    sqlwhere.append(" and "); 
			    sqlwhere.append(Sql_switcher.isnull("c.end_date", Sql_switcher.dateValue("9999-12-31")));
			}
			
			// begin 加入用户范围控制显示 
			TrainCourseBo tbo = new TrainCourseBo(userView);
			String orgScope = tbo.getUnitIdByBusi();
			if(orgScope!=null && orgScope.length()>0 && orgScope.indexOf("UN`") == -1){
				  String[] b0110 = orgScope.split("`");
				  StringBuffer sql = new StringBuffer(" and (((");
				  for(int i=0;i<b0110.length;i++){
					  sql.append(" r.r5020="+Sql_switcher.substr("'"+b0110[i].substring(2)+"'", "1", Sql_switcher.length("r.r5020"))+" or");
				  }
				  sqlwhere.append(sql.substring(0, sql.length()-2));
				  sqlwhere.append(" ) and r5014=2)  or "+Sql_switcher.isnull("R5014", "1")+"<>2 or r5020 is null)");
			}else if(orgScope==null || orgScope.length()<1){
				sqlwhere.append(" and 1=2");
			}
			//end
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("order_by", "order by job_id");
			this.getFormHM().put("strwhere", sqlwhere.toString());
			this.getFormHM().put("strsql", sbsql.toString());
		}
	}

}
