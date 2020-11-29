package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 项目名称 ：ehr7.x
 * 类名称：AddSalaryTypeTrans
 * 类描述：新增工资类别
 * 创建人： lis
 * 创建时间：2015-11-12
 */
public class AddOrEditSalaryTypeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			//校验
			String name=SafeCode.decode((String)this.getFormHM().get("name"));//薪资名称
			String type=(String)this.getFormHM().get("type");//0是重命名，1是新增
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.userView);
			
			//新增
			if("1".equals(type)){
				String gz_module=(String)this.getFormHM().get("gz_module");//模块号，薪资类别是0
				FieldItem fielditem=DataDictionary.getFieldItem("a01z0");
				if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
				{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_noAO1Z0Canot")));
				}
				
				//------------------------------新增帐套记入日志  zhaoxg add 2015-4-28--------------------
				int salaryid2=bo.addSalaryTemplate(name,gz_module);  // 取得主键值
				StringBuffer context = new StringBuffer();
				context.append(ResourceFactory.getProperty("gz_new.gz_newSalary")+name+"("+salaryid2+")");
				this.getFormHM().put("@eventlog", context.toString());
			}else if("0".equals(type)){//重命名
				String oldname = SafeCode.decode((String)this.getFormHM().get("oldname"));//重命名时的原名字
				String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别id
				if(!"-1".equals(salaryid))
					salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
				StringBuffer context = new StringBuffer();
					context.append(ResourceFactory.getProperty("gz_new.gz_reNameSalary")+"("+salaryid+"):"+oldname+"---->"+name);
				this.getFormHM().put("@eventlog", context.toString());
				//----------------------------------end---------------------------------------------------
				bo.renameSalaryTemplate(salaryid,name);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
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
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(this.frowset);
		}
		return flag;
	}
}
