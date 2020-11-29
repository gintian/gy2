package com.hjsj.hrms.transaction.performance.nworkplan.season;
/*查询附件标题*/
import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		String type = (String)this.getFormHM().get("type");//1：季报 2：年报
		String opt = (String)this.getFormHM().get("opt");//1:自己 2：团队
		String isdept = (String)this.getFormHM().get("isdept");//1：个人 2：部门
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn,this.userView);
		this.getFormHM().remove("srarch_type");
		StringBuffer sb = new StringBuffer("");
		StringBuffer where = new StringBuffer("");
		StringBuffer columns = new StringBuffer("");
		
		if("1".equals(type)){ //季报
			columns.append("name,ayear,startmonth,endmonth,time,log_type");
			sb.append("select name,ayear,startmonth,endmonth,time,log_type from ");
			sb.append("(");
			sb.append("select name," + Sql_switcher.year("p0104") + " ayear,");
			sb.append(Sql_switcher.month("p0104") + " startmonth,");
			sb.append(Sql_switcher.month("p0106") + " endmonth,");
			sb.append("time,log_type");
			sb.append(" from p01 left join per_diary_file on p01.p0100=per_diary_file.p0100 where state=3 and ");
			}else if("2".equals(type)){//年报
				columns.append("name,ayear,log_type");
				sb.append("select name,ayear,log_type");
				sb.append(" from ");
				sb.append("(");
				sb.append("select name," + Sql_switcher.year("p0104") + " ayear,log_type");
				sb.append(" from p01 left join per_diary_file on p01.p0100=per_diary_file.p0100 where state=4 and ");
			}
			if("1".equals(isdept)){//如果是个人
				if("1".equals(opt)){//如果从自己进
					if(bo.isChuZhang(this.userView.getA0100())){
						sb.append(" belong_type = 1 and e0122='"+this.userView.getUserDeptId()+"'");
					}else{
						sb.append(" a0100 = '"+this.userView.getA0100() +"' and nbase = '");
						sb.append(this.userView.getDbname() + "'");
						sb.append(" and (belong_type = 0 or belong_type=null)");
					}
				}else if("2".equals(opt)){//如果从团队进
					String p0100 = (String)this.getFormHM().get("p0100");
					sb.append(" p01.p0100 = '" + p0100 + "'");
				}
			}else if("2".equals(isdept)){//如果是部门
				if("1".equals(opt)){
					sb.append(" belong_type = 2 and ");
					sb.append(" e0122 = '" + bo.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()) + "' ");
				}else if("2".equals(opt)){
					String p0100 = (String)this.getFormHM().get("p0100");
					sb.append(" p01.p0100 ='"+p0100+"'");
				}
			}
		
		String content = (String)this.getFormHM().get("content");
		if(content==null || "".equals(content.trim()))
			content="";
		if("".equals(content)){//要求没有内容时什么都查询不出来
			sb.append(" and 1=2 ");
		}else{
			content = content.trim();
			int index = content.lastIndexOf(".");
			String file_name = "";
			String file_ext = "";
			if(index!=-1){
				file_name = content.substring(0,index);
				file_ext = content.substring(index+1,content.length());
			}else{
				file_name = content;
			}
			if("".equals(file_ext)){//如果没有"."
				sb.append(" and (name like '%"+file_name+"%' or per_diary_file.ext like '%"+file_name+"%')");
			}else if("".equals(file_name)){//如果输入的是.doc
				sb.append(" and  per_diary_file.ext like '%"+file_ext+"%'");
			}else{
				sb.append(" and (name like '%"+file_name+"%' and per_diary_file.ext like '%"+file_ext+"%')");
			}
		}
		sb.append(") T");
		
		this.getFormHM().put("type", type);
		this.getFormHM().put("sql", sb.toString());
		this.getFormHM().put("where", where.toString());
		this.getFormHM().put("cols", columns.toString());
	}
	
}
