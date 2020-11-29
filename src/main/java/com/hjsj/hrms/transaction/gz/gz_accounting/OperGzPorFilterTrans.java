package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 *<p>Title:OperGzPorFilterTrans</p> 
 *<p>Description:薪资审批项目过滤操作</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-14:下午01:20:22</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class OperGzPorFilterTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String operation = (String)this.getFormHM().get("operation");
		String projectname = SafeCode.decode((String)this.getFormHM().get("projectname"));
		String proright_str = (String)this.getFormHM().get("proright_str");
		String del_pro_id = (String)this.getFormHM().get("del_pro_id");	
		String salaryid = (String)this.getFormHM().get("salaryid");
		String scopeflag =  (String)this.getFormHM().get("scopeflag");
		String model=this.getFormHM().containsKey("model")?(String)this.getFormHM().get("model"):"";
		if ("save".equalsIgnoreCase(operation)) {
			String chkid = (String) this.getFormHM().get("chkid");
			if (!(proright_str == null || "".equalsIgnoreCase(proright_str))) {


				int id = insert(projectname, proright_str, dao, chkid, salaryid, scopeflag, this.getUserView().getUserName(),model);
				this.getFormHM().put("id", String.valueOf(id));
				this.getFormHM().put("name", SafeCode.encode(projectname));
				this.getFormHM().put("salaryid", salaryid);
				this.getFormHM().put("scopeflag", scopeflag);
			}
		} else if ("delete".equalsIgnoreCase(operation)) {
			this.delete(del_pro_id, dao);
		}
		this.getFormHM().put(proright_str, "");

	}
	/**
	 * 保存选种的过滤项目
	 * @param projectname
	 * @param proright_str
	 * @param dao
	 */
	public int insert(String projectname,String proright_str,ContentDAO dao,String chkid,String salaryid,String scopeflag,String username,String model)
	{
		StringBuffer sqlsb = new StringBuffer();
		int re_id=0;
		try
		{
			if("-1".equals(chkid))
			{
				
	    		//int id = this.getMaxFilterId(dao)+1;
				int id = getMaxFilterId(dao)+1;
				int norder = this.getSeq();
	    		sqlsb.append(" insert into gzItem_filter (chz,cfldname");
	    		if(Sql_switcher.searchDbServer()!=Constant.MSSQL)
		     		sqlsb.append(",id");
		    	sqlsb.append(",norder,scope,username)");
		    	sqlsb.append(" values ('"+projectname+"','"+proright_str+"'");
		    	if(Sql_switcher.searchDbServer()!=Constant.MSSQL)
		    		sqlsb.append(","+id);
		    	sqlsb.append(","+norder+","+scopeflag+",'"+username+"')");
		     	dao.update(sqlsb.toString());
		     	if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		     	{
		     		id=getMaxFilterId(dao);
		     	}
				if("history".equalsIgnoreCase(model)){//history 表示为薪资历史数据分析进入
					this.saveFilterIdFromHistory(String.valueOf(id));
				}else {
					saveIdToSalaryTemplate(salaryid, id);
				}
		     	re_id=id;
			}
			else
			{
				RecordVo vo = new RecordVo("gzItem_filter");
				vo.setInt("id",Integer.parseInt(chkid));
				vo=dao.findByPrimaryKey(vo);
				if(vo!=null)
				{
					vo.setString("chz",projectname);
					vo.setString("cfldname",proright_str);
					if(!"2".equals(scopeflag))
					vo.setInt("scope",Integer.parseInt(scopeflag));
					dao.updateValueObject(vo);
				}
				re_id=Integer.parseInt(chkid);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return re_id;
	}
	/**
	 * 删除选种的过滤项目
	 * @param id
	 * @param dao
	 */
	public void delete(String id,ContentDAO dao)
	{
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" delete gzItem_filter where id = "+id);
//		System.out.println(sqlsb.toString());
		try
		{
			dao.update(sqlsb.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public int getMaxFilterId(ContentDAO dao)
	{
		int  n = 1;
		try
		{
			String sql = "select max(id) as id from gzItem_filter";
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt("id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return n;
	}
	public void saveIdToSalaryTemplate(String salaryid,int filterId)
	{
		try
		{
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			String xml=bo.getCondXML(salaryid);
			SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
			String old_xml = sLPBo.getValue(SalaryLProgramBo.FILTERS);
			String temp_xml="";
			if(old_xml!=null&&!"".equals(old_xml))
			{
				temp_xml=old_xml+","+filterId;
			}
			else
			{
				temp_xml = filterId+"";
			}
			sLPBo.setValue(SalaryLProgramBo.FILTERS, temp_xml);
			String new_xml = sLPBo.outPutContent();
			bo.updateLprogram(salaryid, new_xml);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public int getSeq()
	{
		int i=0;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select max(norder) norder from gzItem_filter");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				i=this.frowset.getInt("norder")+1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}

	private void  saveFilterIdFromHistory(String filterId) throws GeneralException {
		try{
			HistoryDataBo bo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
			bo.saveFilterXmlFromHistory(filterId);

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

}
