/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:SaveUpdateDataTrans</p> 
 *<p>Description:保存修改的记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-21:下午05:30:27</p> 
 *@author cmq
 *@version 4.0
 */
public class SaveUpdateDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("gz_table_table");
		try
		{
			
			String[] temps=name.toLowerCase().split("_salary_");
			//判断是否是审批？wangrd 2013-11-18
			boolean bApprove=true;
			if (temps.length==2){
			    String salaryid =temps[1];
			    if (!"".equals(salaryid)){	
			    	
			    	 
					//如果用户没有当前薪资类别的资源权限   20140903  dengcan
					CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
					safeBo.isSalarySetResource(salaryid,null);
			        SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);		        
			        bApprove=gzbo.isApprove();  
			    } 
			}
			cat.debug("table name="+name);
			ArrayList list=(ArrayList)hm.get("gz_table_record");			
			 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=null;
			boolean subed=false;
			for(int i=0;i<list.size();i++)
			{
				vo=(RecordVo)list.get(i);
				String sp_flag=vo.getString("sp_flag");
				if(bApprove&&sp_flag!=null&&("02".equalsIgnoreCase(sp_flag)|| "03".equalsIgnoreCase(sp_flag)|| "06".equalsIgnoreCase(sp_flag)))
						subed=true;
				HashMap values=vo.getValues();   //2013-11-25 dengcan 修改数据时选了项目过滤条件，会造成其它没选的指标值被清空。
				if(values.get("appprocess")!=null)
				{ 
					String appprocess=vo.getString("appprocess"); 
					appprocess=appprocess.replaceAll("\n\n","\n");
					vo.setString("appprocess", appprocess);
				}
			}
			if(!subed)
			{
				
				try
				{
					dao.updateValueObject(list);
				}
				catch(Exception ee)
				{
				 
					ee.printStackTrace();
					String message=ee.getMessage();
					if(message.indexOf("data is not corrected")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
					if(message.indexOf("转换为数据类型")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	 
					if(message.indexOf("值大于为此列指定的允许精度")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
				}
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("状态为已报批、已批、结束的记录不允许修改"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
