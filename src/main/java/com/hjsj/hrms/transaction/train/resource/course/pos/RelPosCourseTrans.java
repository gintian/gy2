package com.hjsj.hrms.transaction.train.resource.course.pos;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RelPosCourseTrans extends IBusiness {

	public void execute() throws GeneralException {

		String state=(String)this.getFormHM().get("state");
		String a_code=(String)this.getFormHM().get("a_code");
		String codeitemid=a_code.substring(2);
//		String columns="r5000,r5004,r5003,r5012,r5009,r5007,job_id";
//		StringBuffer sbsql=new StringBuffer("select r.r5000 r5000,r5004,r5003,r5012,r5009,r5007,job_id");
//		StringBuffer sqlwhere=new StringBuffer(" from r50 r left join tr_job_course t on t.r5000=r.r5000");
//		if(codeitemid.length()>0){
//			sqlwhere.append(" and job_id='"+codeitemid+"' and state="+state);
//		}else{
//			sqlwhere.append(" and state="+state);
//		}
		//sqlwhere.append(" where r5022='04'");
		String columns="r5000,r5004,r5003,r5012,r5009,r5007";
		StringBuffer sbsql=new StringBuffer("select r5000,r5004,r5003,r5012,r5009,r5007");
		StringBuffer sqlwhere=new StringBuffer();
		sqlwhere.append(" from r50 where R5000 not in (select R5000 from tr_job_course where job_id="+Sql_switcher.substr("'"+codeitemid+"'", "1", Sql_switcher.length("job_id"))+" and state="+state+") and r5022='04' ");
		//sbsql.append(") job_id");
		TrainCourseBo tb = new TrainCourseBo(this.userView,this.frameconn);
//		 判断登录用户为哪种类型的用户：用户管理的还是帐号分配里的
		if(!this.userView.isSuper_admin())
		{
			String code = tb.getUnitIdByBusi();
			code = code==null || code.length()<1 ? "" : code;
			if(code.indexOf("UN`")==-1){
				String unitarr[] = code.split("`"); 
				String str="";
				for(int i=0;i<unitarr.length;i++){
					if(unitarr[i]!=null&&unitarr[i].trim().length()>2&& "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))){
						String tmpb0110 = unitarr[i].substring(2);
						str +="r5020 like '"+tmpb0110+"%' or ";
						tmpb0110=tb.getSupUnit(tmpb0110,0);//上级单位
						if(tmpb0110!=null&&tmpb0110.length()>0)
							str += tmpb0110;
					}
				}
				if(str.length()>0){
					sqlwhere.append(" and ("+str.substring(0, str.lastIndexOf("or")-1)+" or r5020 = '' or r5020 is null or r5014='1' or "+Sql_switcher.isnull("r5014", "1")+"='1')");
				}
			}
		}
		
		String itemize = (String)this.getFormHM().get("itemize");
		if(itemize!=null&&itemize.length()>0)
			sqlwhere.append(" and r5004 ='"+itemize+"'");
		String coursename = (String)this.getFormHM().get("coursename");
		if(coursename!=null&&coursename.length()>0)
			sqlwhere.append(" and r5003 like '%"+coursename+"%'");
		String courseintro = (String)this.getFormHM().get("courseintro");
		if(courseintro!=null&&courseintro.length()>0)
			sqlwhere.append(" and r5012 like '%"+courseintro+"%'");
		String backdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		sqlwhere.append(" and "+Sql_switcher.year("R5030")+"*10000+"+Sql_switcher.month("R5030")+"*100+"+Sql_switcher.day("R5030")+"<="+backdate);
		sqlwhere.append(" and "+Sql_switcher.year("R5031")+"*10000+"+Sql_switcher.month("R5031")+"*100+"+Sql_switcher.day("R5031")+">="+backdate);
		
		ArrayList itemlist = new ArrayList();
		FieldItem fieldItem = DataDictionary.getFieldItem("r5000");
		fieldItem.setVisible(false);
		itemlist.add(fieldItem);
		itemlist.add(DataDictionary.getFieldItem("r5004"));
		itemlist.add(DataDictionary.getFieldItem("r5003"));
		itemlist.add(DataDictionary.getFieldItem("r5012"));
		itemlist.add(DataDictionary.getFieldItem("r5009"));
		itemlist.add(DataDictionary.getFieldItem("r5007"));
		this.getFormHM().put("columns1", columns);
		this.getFormHM().put("itemlist1", itemlist);
		this.getFormHM().put("order_by", "order by r5000");
		this.getFormHM().put("strwhere", sqlwhere.toString());
		this.getFormHM().put("strsql", sbsql.toString());
	}
}
