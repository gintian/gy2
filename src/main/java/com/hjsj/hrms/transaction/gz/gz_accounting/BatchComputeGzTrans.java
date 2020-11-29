/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:批量计算薪资交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-29:下午03:35:04</p> 
 *@author cmq
 *@version 4.0
 */
public class BatchComputeGzTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");	
		ArrayList itemids=(ArrayList)this.getFormHM().get("itemids");
		/**是否月奖金管理模块调用	 */
		String  isPremium=(String)this.getFormHM().get("isPremium");
		if(isPremium==null)
			isPremium="no";
		SalaryTemplateBo gzbo=null;
		try
		{
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			
			//如果是月奖金管理模块调用，需先执行导入档案库数据功能
			if("yes".equalsIgnoreCase(isPremium))
			{
				ArrayList importItemList=getImportItemList(gzbo);
				gzbo.batchImport("3", importItemList,"","","");
			}
			
			
			
			
			/**人员计算过滤条件*/
			String strwhere="";
			
			/**需要审批*/ 
			if(gzbo.isApprove()){
				strwhere=" where  sp_flag in('01','04','07')";
			}	    
			else{
                //参数“允许修改发放结束已提交数据” 控制已提交的数据是否能计算  wangrd  2013-11-14
                if (!gzbo.isAllowEditSubdata()) {
         	       strwhere=" where  sp_flag in('01','07')";   
                }
                
			} 	    
			
			String manager=gzbo.getManager();
			if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
			{
				if(gzbo.isApprove()){
					strwhere+=" and sp_flag2 in ('01','07')";
				}else if (!gzbo.isAllowEditSubdata()) {//为了sql拼写正确，前面“允许修改发放结束已提交数据”已加where，此处应为and   zhaoxg add 2013-12-12
         	       strwhere+=" and  sp_flag2 in('01','07')";   
                }else{
					strwhere+=" where sp_flag2 in ('01','07')";
                }

			}
			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName())&&!this.userView.isSuper_admin()){
				String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
				String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units.length()==0&&(unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv))){
					/**导入数据*/
					String dbpres=gzbo.getTemplatevo().getString("cbase");
					/**应用库前缀*/
					String[] dbarr=StringUtils.split(dbpres, ",");
					StringBuffer sub_str=new StringBuffer("");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						
						{
							sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and "+gzbo.getGz_tablename()+".a0100 in (select a0100 "+this.userView.getPrivSQLExpression(pre, false)+" ) )");
						}
						
					}
					if(sub_str.length()>0)
					{
						if(strwhere.trim().length()>0)
							strwhere+=" and ( "+sub_str.substring(3)+" )";
						else
							strwhere+=" where ( "+sub_str.substring(3)+" )";
						
					}
				}else{
					String whl_str=gzbo.getPrivSQL("", "", salaryid, b_units);
					if(whl_str.length()>0)
					{
						if(strwhere.trim().length()>0)
							strwhere+=" and "+whl_str;
						else
							strwhere+=" where 1=1 and "+whl_str;
						
					}
				}
			}
			gzbo.computing(strwhere,itemids);
			if(gzbo.getErrorInfo()!=null&&gzbo.getErrorInfo().length()>0)
				throw GeneralExceptionHandler.Handle(new Exception(gzbo.getErrorInfo()));
			


			/**个税明细表动态指标*/
			TaxMxBo mxbo=new TaxMxBo(this.getFrameconn(),this.userView);
			ArrayList taxfldlist=mxbo.searchDynaItemList();
			 /**取得个税明细表额外定义的字段*/
            StringBuffer extFlds=new StringBuffer();
            StringBuffer extValues=new StringBuffer();
			DbNameBo.getExtFlds(extFlds,extValues,this.getFrameconn(),gzbo.getGz_tablename(),taxfldlist);
			if(extFlds.length()>0)
			{
				String aa=extValues.toString().replaceAll(",S.", ","+gzbo.getGz_tablename()+".");
				StringBuffer update_str=new StringBuffer("");
				int db_type=Sql_switcher.searchDbServer();//数据库类型
				String str_where="";
				if(strwhere.trim().length()>0)
					str_where=" and "+strwhere.substring(6);
				if(db_type==2)//oracle
				{
					update_str.append("update gz_tax_mx set ( "+extFlds.substring(1)+" ) = (select  "+aa.substring(1)+" from "+gzbo.getGz_tablename());
					update_str.append("  where gz_tax_mx.a0100="+gzbo.getGz_tablename()+".A0100 AND gz_tax_mx.NBASE="+gzbo.getGz_tablename()+".NBASE and gz_tax_mx.a00z0="+gzbo.getGz_tablename()+".A00z0  and gz_tax_mx.a00z1="+gzbo.getGz_tablename()+".A00z1 ");
					update_str.append("  "+str_where+" ) where salaryid="+salaryid+" and  exists (select null  from "+gzbo.getGz_tablename());
					update_str.append("  where gz_tax_mx.a0100="+gzbo.getGz_tablename()+".A0100 AND gz_tax_mx.NBASE="+gzbo.getGz_tablename()+".NBASE and gz_tax_mx.a00z0="+gzbo.getGz_tablename()+".A00z0  and gz_tax_mx.a00z1="+gzbo.getGz_tablename()+".A00z1 ");
					update_str.append(" "+str_where+" ) ");
				}
				else
				{
					String set="";
					String[] temps=extFlds.toString().split(",");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0)
							set+=",gz_tax_mx."+temps[i]+"="+gzbo.getGz_tablename()+"."+temps[i];
					}
					
					
					update_str.append("update gz_tax_mx set   "+set.substring(1)+"  from gz_tax_mx");
					update_str.append(" left join "+gzbo.getGz_tablename()+"  on gz_tax_mx.a0100="+gzbo.getGz_tablename()+".A0100 AND gz_tax_mx.NBASE="+gzbo.getGz_tablename()+".NBASE and gz_tax_mx.a00z0="+gzbo.getGz_tablename()+".A00z0  and gz_tax_mx.a00z1="+gzbo.getGz_tablename()+".A00z1 ");
					update_str.append(" where gz_tax_mx.salaryid="+salaryid+" and exists (select null  from "+gzbo.getGz_tablename());
					update_str.append("  where gz_tax_mx.a0100="+gzbo.getGz_tablename()+".A0100 AND gz_tax_mx.NBASE="+gzbo.getGz_tablename()+".NBASE and gz_tax_mx.a00z0="+gzbo.getGz_tablename()+".A00z0  and gz_tax_mx.a00z1="+gzbo.getGz_tablename()+".A00z1 ");
					update_str.append(" "+str_where+" ) ");
				}
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update(update_str.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				PubFunc.resolve8060(this.getFrameconn(),gzbo.getGz_tablename());
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}else if(message.indexOf("归属次数")!=-1){
				throw GeneralExceptionHandler.Handle(ex);
			}
			else
				throw GeneralExceptionHandler.Handle(ex);
			 
		}
		finally
		{
			gzbo=null;
		}
	}
	 


	
	
	
	
	
	
	private ArrayList getImportItemList(SalaryTemplateBo gzbo)
	{
		ArrayList list=new ArrayList();
		try
		{
			ArrayList templist=gzbo.getSalaryItemList2(); //gzbo.getSalaryItemList();
			String manager=gzbo.getManager();
			int j=0;
			String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  					
			HashMap map=new HashMap();
			if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
			{
				ArrayList formulaList=gzbo.getFormulaList(-1);
				for(int i=0;i<formulaList.size();i++)
				{
					  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
					  String itemname=(String)dbean.get("itemname");
					  map.put(itemname.toLowerCase(),"1");
				}
			}
			for(int i=0;i<templist.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)templist.get(i);
				String flag=(String)dynabean.get("initflag");
				String itemid=(String)dynabean.get("itemid");
				if(!("1".equals(flag)|| "2".equals(flag)))
					continue;
				if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				{
					if(map.get(itemid.toLowerCase())!=null)
						continue;
				}
				
				
				String nlock=(String)dynabean.get("nlock");
				if("3".equalsIgnoreCase(flag)&&!"A01Z0".equalsIgnoreCase(itemid))
					continue;
				LazyDynaBean tmp=new LazyDynaBean();
				tmp.set("itemid", dynabean.get("itemid")==null?"":dynabean.get("itemid"));
				tmp.set("itemdesc",dynabean.get("itemdesc")==null?"":dynabean.get("itemdesc"));
				if("a01z0".equalsIgnoreCase(itemid)&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
					continue;
		    	if(manager.length()==0||manager.equalsIgnoreCase(this.userView.getUserName()))
		    	{
		    		if(!"3".equalsIgnoreCase(flag))
		    		{
		    			if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //重新导入
		    			{
		    				if("2".equals(this.userView.analyseFieldPriv(itemid))|| "1".equals(this.userView.analyseFieldPriv(itemid)))
			    				list.add((String)tmp.get("itemid"));
		    			}
		    			else
		    			{
			    			if("2".equals(this.userView.analyseFieldPriv(itemid)))
			    				list.add((String)tmp.get("itemid"));
		    			}
		    		}
		    		else
		    			list.add((String)tmp.get("itemid"));
		    	}
				else
				{
					if("A01Z0".equalsIgnoreCase(itemid))
						list.add((String)tmp.get("itemid"));
					else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
						list.add((String)tmp.get("itemid"));
				}
			}//for i loop end.
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
}
