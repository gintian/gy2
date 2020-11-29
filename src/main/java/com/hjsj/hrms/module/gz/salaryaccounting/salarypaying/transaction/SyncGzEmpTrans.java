package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * @ClassName: SyncGzEmpTrans 
 * @Description: TODO(工资发放的人员同步) 
 * @author lis 
 * @date 2015-8-20 下午05:39:48
 */
public class SyncGzEmpTrans extends IBusiness{
	

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid = PubFunc.decrypt(salaryid);//解密
			String viewtype = (String) this.getFormHM().get("viewtype");//0 薪资发放 1 薪资审批
			viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
			/**薪资类别*/
			/**人员同步*/
			if("0".equals(viewtype)){
				this.syncGzEmp(salaryid);
			}else if("1".equals(viewtype)){
				/**业务日期**/
				String appdate =  (String) this.getFormHM().get("appdate");
				appdate = PubFunc.decrypt(SafeCode.decode(appdate));
				this.syncGzSpEmp(salaryid, appdate);
			}
			/**  同步临时表B0110_O,E0122_O字段,暂时不需要 **/
			//salaryAccountBo.synSalaryTable(salaryid, salaryAccountBo.getSalaryTemplateBo().getGz_tablename());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * @Title: syncGzEmp 
	 * @Description: TODO(同步薪资临时表中人员顺序与人员库中一致) 
	 * @param username
	 * @param salaryid
	 * @author lis  
	 * @date 2015-8-12 上午10:23:24
	 */
	public void syncGzEmp(String salaryid)
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.valueOf(salaryid));
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String gz_tablename = null;
			if(manager.length()==0)
				gz_tablename=this.userView.getUserName()+"_salary_"+salaryid;
			else
				gz_tablename=manager+"_salary_"+salaryid;
			ArrayList nbaselist = this.getGz_Nbase(gz_tablename);
			if(nbaselist != null)
			{
				for(int i=0;i<nbaselist.size();i++)
				{
					String nbase = (String)nbaselist.get(i);
					StringBuffer sqlsb = new StringBuffer();
					sqlsb.append(" UPDATE "+gz_tablename+" SET  ");
					sqlsb.append(gz_tablename+".A0000=( select "+nbase+"A01.A0000 ");
					sqlsb.append("FROM "+nbase+"A01 WHERE ");
					sqlsb.append(gz_tablename+".A0100="+nbase+"A01.A0100 )");
					sqlsb.append(" where upper("+gz_tablename+".NBase)='"+nbase.toUpperCase()+"'");
					sqlsb.append(" and "+gz_tablename+".a0100 in ");
					sqlsb.append(" (select "+nbase+"A01.a0100 from "+nbase+"A01)");
					dao.update(sqlsb.toString());
				}
				dao.update("update "+gz_tablename+" set "+gz_tablename+".dbid=(select dbid from dbName where upper("+gz_tablename+".NBase)=upper(dbName.pre)) ");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 更新薪资历史表中的人员，实现人员同步
	 * @param nbaselist
	 * @param salaryid
	 * @param time
	 */
	public void syncGzSpEmp(String salaryid,String time)
	{
		ArrayList nbaselist = this.getnbase(salaryid, time);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			if(nbaselist != null)
			{
				for(int i=0;i<nbaselist.size();i++)
				{
					String nbase = (String)nbaselist.get(i);
					StringBuffer sqlsb = new StringBuffer();
					sqlsb.append(" UPDATE SalaryHistory SET  ");
					sqlsb.append(" SalaryHistory.A0000=( select "+nbase+"A01.A0000 ");
					sqlsb.append("FROM "+nbase+"A01 WHERE ");
					sqlsb.append("SalaryHistory.A0100="+nbase+"A01.A0100 )");
					sqlsb.append(" where upper(SalaryHistory.NBase)='"+nbase.toUpperCase()+"'");
					sqlsb.append(" AND SalaryHistory.salaryid ="+salaryid);
					sqlsb.append(" and SalaryHistory.A00Z2 = ");
					sqlsb.append(Sql_switcher.dateValue(time));
					dao.update(sqlsb.toString());
				}
				
				dao.update("update SalaryHistory set SalaryHistory.dbid=(select dbid from dbName where upper(SalaryHistory.NBase)=upper(dbName.pre)) "
						+ "where SalaryHistory.salaryid ="+salaryid+" and SalaryHistory.A00Z2 = "+Sql_switcher.dateValue(time));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 取库前缀
	 * @param salaryid
	 * @param time
	 * @return
	 */
	public ArrayList getnbase(String salaryid,String time)
	{
		RowSet rs;
		ArrayList retlist = new ArrayList();
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" select distinct NBase FROM SalaryHistory ");
		sqlsb.append("where SalaryHistory.salaryid = "+salaryid+"");
		sqlsb.append(" and SalaryHistory.A00Z2 =");
		sqlsb.append(Sql_switcher.dateValue(time));
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs = dao.search(sqlsb.toString());
			while(rs.next())
			{
				String nbase = rs.getString("NBase");
				retlist.add(nbase);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * @Title: getGz_Nbase 
	 * @Description: TODO(返回临时表中所有人员库) 
	 * @param username 用户名
	 * @param salaryid 薪资列表id
	 * @return ArrayList
	 * @author lis  
	 * @date 2015-8-12 上午10:24:06
	 */
	public ArrayList getGz_Nbase(String tableName)
	{
		RowSet rs = null;
		ArrayList retlist = new ArrayList();
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" select distinct NBase FROM "+tableName);
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sqlsb.toString());
			while(rs.next())
			{
				String nbase = rs.getString("NBase");
				retlist.add(nbase);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return retlist;
	}
}
