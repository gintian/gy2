package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:批量计算审批模块中的数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 24, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class BatchComputeSpGzTrans extends IBusiness {


	public void execute() throws GeneralException {
	    String gz_module=(String)this.getFormHM().get("gz_module");
		ArrayList itemids=(ArrayList)this.getFormHM().get("itemids");
		String salaryid=(String)this.getFormHM().get("salaryid");	
		String strYm=(String)this.getFormHM().get("strYm");	
		String condid=(String)this.getFormHM().get("condid");
		String reportSql=(String)this.getFormHM().get("reportSql");
		if(reportSql!=null&&reportSql.length()>0&&!"null".equalsIgnoreCase(reportSql))
			reportSql=PubFunc.decrypt(reportSql);
		if(condid==null|| "".equalsIgnoreCase(condid))
			condid="all";
		
		if(strYm!=null&&strYm.length()>0)
			strYm=strYm.replaceAll("\\.","-");
		String strC=(String)this.getFormHM().get("strC");	
		try
		{
			
			//如果用户没有当前薪资类别的资源权限   20140926  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			if(strYm!=null&&strYm.length()>0&&strC!=null&&strC.length()>0)
			{
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				//参数“允许修改发放结束已提交数据” 控制已提交的数据是否能计算  wangrd  2013-11-14	
				boolean bAllowEditSubdata=gzbo.isAllowEditSubdata_Sp(gz_module);	
			
				/**人员计算过滤条件*/
				String[] temps=strYm.split("-");
				String strwhere="";
				
				strwhere+=" (( lower(curr_user)='"+this.userView.getUserId().toLowerCase()+"' and ( sp_flag='02' or sp_flag='07' ) )";
				if (bAllowEditSubdata){
	                strwhere+= " or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("salaryhistory")+"  ) or AppUser Like '%;"
                    +this.userView.getUserName()+";%' ) and   sp_flag='06' ) ";
				}				
				strwhere+=") ";

				strwhere+=" and salaryid="+salaryid+"  and "+Sql_switcher.year("a00z2")+"="+temps[0]+"  and "+Sql_switcher.month("a00z2")+"="+temps[1];
				strwhere+=" and a00z3="+strC;  
			//	strwhere+=" and salaryid="+salaryid+"  and "+Sql_switcher.year("a00z0")+"="+temps[0]+"  and "+Sql_switcher.month("a00z0")+"="+temps[1];
			//	strwhere+=" and a00z1="+strC;  //+" and lower(curr_user)='"+this.userView.getUserName().toLowerCase()+"'  and sp_flag<>'06' and ( sp_flag='02' or sp_flag='07') ";
				if(!"all".equalsIgnoreCase(condid))
				{
					if("new".equalsIgnoreCase(condid))
					{
						if(reportSql!=null&&reportSql.trim().length()>0)
							strwhere+=reportSql;	
					}
					else
						strwhere+=" and ( "+gzbo.getFilterWhere(condid,"salaryhistory")+" ) ";	
				}
				
				gzbo.sp_computing(strwhere,itemids,strYm,strC);
				

				
				/**个税明细表动态指标*/
				TaxMxBo mxbo=new TaxMxBo(this.getFrameconn(),this.userView);
				ArrayList taxfldlist=mxbo.searchDynaItemList();
				 /**取得个税明细表额外定义的字段*/
	            StringBuffer extFlds=new StringBuffer();
	            StringBuffer extValues=new StringBuffer();
				DbNameBo.getExtFlds(extFlds,extValues,this.getFrameconn(),"salaryhistory",taxfldlist);
				if(extFlds.length()>0)
				{
					String aa=extValues.toString().replaceAll(",S.", ",salaryhistory.");
					StringBuffer update_str=new StringBuffer("");
					int db_type=Sql_switcher.searchDbServer();//数据库类型
					String str_where="";
					if(strwhere.trim().length()>0)
						str_where=" and "+strwhere.toString();
					if(db_type==2)//oracle
					{
						update_str.append("update gz_tax_mx set ( "+extFlds.substring(1)+" ) = (select  "+aa.substring(1)+" from  salaryhistory");
						update_str.append("  where gz_tax_mx.a0100=salaryhistory.A0100 AND gz_tax_mx.NBASE=salaryhistory.NBASE and gz_tax_mx.a00z0=salaryhistory.A00z0  and gz_tax_mx.a00z1=salaryhistory.A00z1 ");
						update_str.append(" "+str_where+" ) where salaryid="+salaryid+" and  exists (select null  from salaryhistory");
						update_str.append("  where gz_tax_mx.a0100=salaryhistory.A0100 AND gz_tax_mx.NBASE=salaryhistory.NBASE and gz_tax_mx.a00z0=salaryhistory.A00z0  and gz_tax_mx.a00z1=salaryhistory.A00z1 ");
						update_str.append("   "+str_where+" ) ");
					}
					else
					{
						String set="";
						String[] _temps=extFlds.toString().split(",");
						for(int i=0;i<_temps.length;i++)
						{
							if(_temps[i].trim().length()>0)
								set+=",gz_tax_mx."+_temps[i]+"=salaryhistory."+_temps[i];
						}
						
						
						update_str.append("update gz_tax_mx set   "+set.substring(1)+"  from gz_tax_mx");
						update_str.append(" left join salaryhistory  on gz_tax_mx.a0100=salaryhistory.A0100 AND gz_tax_mx.NBASE=salaryhistory.NBASE and gz_tax_mx.a00z0=salaryhistory.A00z0  and gz_tax_mx.a00z1=salaryhistory.A00z1 ");
						update_str.append(" where gz_tax_mx.salaryid="+salaryid+" and exists (select null  from salaryhistory");
						update_str.append("  where gz_tax_mx.a0100=salaryhistory.A0100 AND gz_tax_mx.NBASE=salaryhistory.NBASE and gz_tax_mx.a00z0=salaryhistory.A00z0  and gz_tax_mx.a00z1=salaryhistory.A00z1 ");
						update_str.append("  "+str_where+" ) ");
					}
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					dao.update(update_str.toString());
				}
				
				
				if(gzbo.getErrorInfo()!=null&&gzbo.getErrorInfo().length()>0)
					throw GeneralExceptionHandler.Handle(new Exception(gzbo.getErrorInfo()));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				PubFunc.resolve8060(this.getFrameconn(),"SalaryHistory");
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex); 
		}

	}
	
	

}
