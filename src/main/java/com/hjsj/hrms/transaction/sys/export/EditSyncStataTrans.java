package com.hjsj.hrms.transaction.sys.export;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class EditSyncStataTrans extends IBusiness {

	public void execute() throws GeneralException {
		String emporg = (String)this.getFormHM().get("emporg");//人员=1，单位=2，岗位=3
		String ids = (String)this.getFormHM().get("ids");//待修改项
		String unique_ids = (String)this.getFormHM().get("unique_ids");//待修改项
		ids = ids == null ? "" : ids;
		String starflag = (String)this.getFormHM().get("starflag");//修改状态
		if(emporg==null||emporg.length()<1)
			return;
		String unique_idSql = getUnique_idSql(unique_ids);
		
		editSync(starflag,emporg,ids,unique_idSql);
		this.getFormHM().put("check", "ok");
	}

	private void editSync(String flag,String table,String ids,String unique_idSql) throws GeneralException{
		String[] id = ids.split(",");
		DbWizard dbw = new DbWizard(this.getFrameconn());
		StringBuffer strsql = new StringBuffer();
		StringBuffer syssql = new StringBuffer();//新增状态后 改为修改状态  wangb 20170706
		StringBuffer sqlflag = new StringBuffer();//新增状态后 改为修改状态  判断是否更改状态wangb 20170706
		strsql.append("update "+table+" set ");
		for(int i = 0; id!=null && i < id.length; i++){
			if(id[i]!=null&&id[i].length()>0){
				if(dbw.isExistField(table, id[i], false)){
					strsql.append(id[i]+"="+flag+",");
					sqlflag.append(id[i] +"= case when "+ id[i] +"=1 then 1 else "+ flag +" end , ");//如果同步系统状态为1 不修改 wangb 20170706
					syssql.append(" and "+ id[i] +"<>1 ");//外部系统 同步状态不为1 才可以改为修改状态 wangb 20170706
				}
			}
		}
		if(strsql.toString().indexOf(",")!=-1){
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			strsql.setLength(strsql.length()-1);
			strsql.append(" where 1=1 ");
			if(!"0".equalsIgnoreCase(flag)){//删除后，sys_flag 状态为3时，同步状态不能修改  wangb 20170706
				strsql.append(" and sys_flag<>3 ");
			}
			if("2".equalsIgnoreCase(flag)){//新增后修改时，新增同步状态1时，不能更改为修改状态 wangb 20170706
				strsql.delete(0,strsql.length());
				sqlflag.setLength(sqlflag.length()-2);
				strsql.append("update "+table+" set ");
				strsql.append(sqlflag);
				strsql.append(" where 1=1 and sys_flag<>3 ");
				strsql.append(syssql.toString());
			}
			if(unique_idSql!=null && unique_idSql.length()>1)
				strsql.append(unique_idSql);
			try {
				//System.out.println(strsql);
				dao.update(strsql.toString());
			} catch (SQLException e) {
				//e.printStackTrace();
				throw GeneralExceptionHandler.Handle(new GeneralException("","修改同步状态失败!","",""));
			}
		}
	}
	private String getUnique_idSql(String unique_ids){
		if(unique_ids == null || unique_ids.length()==0)
			return null;
		String[] unique_id = unique_ids.split(",");
		String unique_idSql = "";
		if(unique_id.length==1)
			return "and unique_id = '" + unique_id[0] + "'";
		for (int i = 0; i < unique_id.length; i++) {
			if(i==0){
				unique_idSql += "and (unique_id = '" + unique_id[i] + "' ";
			}else if(i==unique_id.length-1){
				unique_idSql += "or unique_id = '" + unique_id[i] + "')";
			}else{
				unique_idSql += "or unique_id = '" + unique_id[i] + "' ";
			}
		}
		return unique_idSql;
	}
}
