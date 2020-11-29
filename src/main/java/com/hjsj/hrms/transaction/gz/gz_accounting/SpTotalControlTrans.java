package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.Calendar;

public class SpTotalControlTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			
			String salaryid=(String)this.getFormHM().get("salaryid");
			String gz_module=(String)this.getFormHM().get("gz_module");
			String bosdate=(String)this.getFormHM().get("bosdate");  //业务日期(发放日期)
			String count=(String)this.getFormHM().get("count");		 //发放次数
			String gzSpCollect = (String) this.getFormHM().get("gzSpCollect");//薪资汇总审批
			String collectPoint = (String) this.getFormHM().get("collectPoint");//薪资汇总审批
			String desc="不允许操作";
			//报批用到的参数
			String opt=(String)this.getFormHM().get("opt");
			String opt2=(String)this.getFormHM().get("opt2");
			String userid=(String)this.getFormHM().get("userid");
			String selectID=(String)this.getFormHM().get("selectID");
			if(selectID!=null)
				selectID=selectID.replaceAll("＃", "#").replaceAll("／", "/");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String collectSpSql = "";
			if(gzSpCollect!=null&& "1".equals(gzSpCollect)&&!"sum".equalsIgnoreCase(selectID)){
				collectSpSql = gzbo.getCollectSPPriv(bosdate, count, selectID, collectPoint);
			}
			if(opt!=null&&opt.trim().length()>0)
				desc="不允许操作";
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			LazyDynaBean abean=new LazyDynaBean();
			String info="success";
			String ctrlType = "1";
			String alertInfo = "";
			if(bosdate!=null&&bosdate.trim().length()>0)
			{
				String[] temps=bosdate.split("\\.");
				abean.set("year",temps[0]);
				abean.set("month",String.valueOf(Integer.parseInt(temps[1])));
				abean.set("day",String.valueOf(Integer.parseInt(temps[2])));
				abean.set("count",count);
				StringBuffer buf=new StringBuffer("");
				buf.append(" and "+Sql_switcher.year("a00z2")+"="+(String)abean.get("year"));
				buf.append(" and "+Sql_switcher.month("a00z2")+"="+(Integer.parseInt((String)abean.get("month"))));
				buf.append(" and a00z3="+(String)abean.get("count"));	
			//	buf.append(" and curr_user='"+this.userView.getUserId()+"' and  sp_flag='02' ");
				
				if(opt!=null&&opt.trim().length()>0&&("appeal".equalsIgnoreCase(opt)|| "appealAll".equalsIgnoreCase(opt)|| "confirm".equalsIgnoreCase(opt)|| "confirmAll".equalsIgnoreCase(opt)))
				{
					if(opt2!=null&& "appeal_group".equalsIgnoreCase(opt2))
					{
						buf.append(" and exists ("+selectID+")");											
					}
					else
					{
						if("appealAll".equals(opt))
						{
							buf.append(" and curr_user='"+this.userView.getUserName()+"' and ( sp_flag='02' or  sp_flag='07' ) "+collectSpSql+" ");
							String filterWhl=(String)this.getFormHM().get("filterWhl");
							filterWhl=PubFunc.decrypt(filterWhl);
							if(filterWhl!=null&&filterWhl.trim().length()>0)
								buf.append(" "+filterWhl);
						}
						else if("confirmAll".equals(opt))
						{
							buf.append(" and sp_flag='02' ");
							buf.append(" and curr_user='"+this.userView.getUserName()+"' "+collectSpSql+" " );
						 
							String filterWhl=(String)this.getFormHM().get("filterWhl");
							filterWhl=PubFunc.decrypt(filterWhl);
							if(filterWhl!=null&&filterWhl.trim().length()>0)
								buf.append(" "+filterWhl);
						}
						else
						{
							
							String[] selects=selectID.split("#");
							Calendar d=Calendar.getInstance();
							StringBuffer whl=new StringBuffer("");
							for(int i=0;i<selects.length;i++)
							{
									if(selects[i]==null||selects[i].trim().length()==0)
										continue;
									String[] a_selects=selects[i].split("/");
									d.setTimeInMillis(Long.parseLong(a_selects[2]));
									whl.append(" or (A0100='"+a_selects[0]+"' and upper(nbase)='"+a_selects[1].toUpperCase()+"' and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+a_selects[3]+" )");
							}
							if(whl.length()>0)
							{
								buf.append(" and ("+whl.substring(3)+")");
							}
							else
								buf.append(" and 1=2 ");
						}
					}
					
				}
				else
				{
					buf.append(" and ( ( AppUser is null or AppUser Like '%;"+this.getUserView().getUserName()+";%' ) and  sp_flag='03' )");		
					String filterWhl=(String)this.getFormHM().get("filterWhl");
					filterWhl=PubFunc.decrypt(filterWhl);
					if(filterWhl!=null&&filterWhl.trim().length()>0)
						buf.append(" "+filterWhl);
					
				}
				buf.append(" and salaryid="+salaryid);
				
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
				String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
				if("1".equals(isControl))
				{
					String amount_ctrl_sp=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_sp");
					if(amount_ctrl_sp!=null&&amount_ctrl_sp.trim().length()>0)
						isControl=amount_ctrl_sp;
					
				}
				
				bo.setDesc(desc);
				ctrlType = bo.getCtrlType();
				if("1".equals(bo.getIsControl())&& "1".equals(isControl))
				{  
					info=bo.calculateSpTotal(buf.toString()); 
					if(!"success".equals(info))
					{
						if(info.indexOf(desc)==-1)
							this.getFormHM().put("isOver","0");
						else
							this.getFormHM().put("isOver","1");
						alertInfo = info.replaceAll(desc, "是否继续？").replaceAll("！", "");
					}
				}
			
			}
			else
			{
				info="没有记录，不允许操作!";
				if("confirm".equalsIgnoreCase(opt))
					info="没有记录，不允许操作!";
			}
			
			this.getFormHM().put("alertInfo", SafeCode.encode(alertInfo));
			this.getFormHM().put("ctrlType", ctrlType);
			this.getFormHM().put("info",SafeCode.encode(info));
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("bosdate",bosdate);
			this.getFormHM().put("count",count);
			
			this.getFormHM().put("opt",opt);
			this.getFormHM().put("userid",userid);
			this.getFormHM().put("selectID",selectID);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
