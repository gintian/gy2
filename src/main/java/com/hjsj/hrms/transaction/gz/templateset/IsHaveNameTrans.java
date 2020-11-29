package com.hjsj.hrms.transaction.gz.templateset;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class IsHaveNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String name=SafeCode.decode((String)this.getFormHM().get("name"));
			String gz_module=(String)this.getFormHM().get("gz_module");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String type=(String)this.getFormHM().get("type");
			String msg="0";
			String oldname = SafeCode.decode((String)this.getFormHM().get("oldname"));   //获取要另存的薪资类别名
			boolean flag = this.isHaveName(name, gz_module,type,salaryid);
			if(flag)//已经存在
			{
				if("0".equalsIgnoreCase(gz_module))
				{
					msg=ResourceFactory.getProperty("gz.templateset.havegz");
				}
				else
				{
					msg=ResourceFactory.getProperty("gz.templateset.havebx");
				}
			}
			/*else { //如果不重名，接着判断临时变量是否存在没有共享的
			    flag = isVariablesHaveShare(oldname);
			    if(flag)//存在
			    {  //存在没有共享的临时变量nosharevariable
			       msg=ResourceFactory.getProperty("gz.templateset.nosharevariable");
			    }   
		   }*/
			this.getFormHM().put("msg", msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 
	 * @param name
	 * @param gz_module
	 * @param type =0重命名，=1另存为
	 * @param salaryid
	 * @return
	 */
	public boolean isHaveName(String name,String gz_module,String type,String salaryid)
	{
		boolean flag = false;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from salarytemplate where ");
			if("0".equalsIgnoreCase(gz_module))
				sql.append(" (cstate is null or cstate='')");
			else
				sql.append(" cstate='1'");
			sql.append(" and cname='"+name+"'");
			if("0".equals(type))
			{
				sql.append(" and salaryid<>"+salaryid);
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 判断临时变量是否共享
	 * @param cname
	 * @return
	 */
	public boolean isVariablesHaveShare(String cname){
		boolean flag = false;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select cstate from midvariable,(select salaryid from salarytemplate where cname = ");
			sql.append("'");
			sql.append(cname);
			sql.append("') a where templetid=0 and cstate= a.SALARYID and nflag=0");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
}
