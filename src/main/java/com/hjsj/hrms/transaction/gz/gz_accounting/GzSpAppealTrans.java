package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:GzSpAppealTrans.java</p> 
 *<p>Description:工资审批报批 or 驳回 or 批准</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 9, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GzSpAppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String gzSpCollect = (String) this.getFormHM().get("gzSpCollect");//是否工资汇总审批标识  0：非 1：是
			String opt="";
			if(hm==null){
				opt = (String) this.getFormHM().get("opt");
			}else{
				opt=(String)hm.get("opt");
				if(opt==null||opt.length()==0){
					opt = (String) this.getFormHM().get("opt");
				}
			}

			String approveObject=(String)this.getFormHM().get("approveObject");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String bosdate=(String)this.getFormHM().get("bosdate");  //业务日期(发放日期)
			String count=(String)this.getFormHM().get("count");		 //发放次数
			String rejectCause=(String)this.getFormHM().get("rejectCause");
			rejectCause=rejectCause.replaceAll("\r\n", "\n");
			String selectGzRecords=(String)this.getFormHM().get("selectGzRecords");
			selectGzRecords=selectGzRecords.replaceAll("＃", "#").replaceAll("／", "/");
			String _selectGzRecords = selectGzRecords;
			String sendMen=(String)this.getFormHM().get("sendMen");
			String collectPoint = (String) this.getFormHM().get("collectPoint");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String collectSpSql = "";
			if(gzSpCollect!=null&& "1".equals(gzSpCollect)&&!"sum".equalsIgnoreCase(_selectGzRecords)){
				collectSpSql = gzbo.getCollectSPPriv(bosdate, count, _selectGzRecords, collectPoint);
			}
			//如果用户没有当前薪资类别的资源权限   20140926  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String reportSql=(String)this.getFormHM().get("reportSql");
			if(reportSql==null||reportSql.trim().length()==0)
				reportSql="";
			reportSql = PubFunc.decrypt(reportSql);
			
			String a_code=(String)this.getFormHM().get("a_code");
			if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
			{
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if("UN".equalsIgnoreCase(codesetid)&&value.length()>0)
				{
					reportSql+=" and (b0110 like '";
					reportSql+=value;
					reportSql+="%'";
					if("".equalsIgnoreCase(value))
					{
						reportSql+=" or b0110 is null";
						 
					}
					reportSql+=")";
				}
				if("UM".equalsIgnoreCase(codesetid)&&value.length()>0)
				{
					reportSql+=" and e0122 like '"+value+"%'";  
				}
			}	
			reportSql+=collectSpSql;
			LazyDynaBean dataBean = gzbo.getSalaryPayDate(bosdate, count);
			ArrayList list=null;
			if("reject".equals(opt)|| "rejectAll".equals(opt)){
				list=gzbo.getTable(dataBean, selectGzRecords, opt, reportSql);
			}
			
			gzbo.gzGradeAppeal(approveObject,bosdate,count,selectGzRecords,opt,rejectCause,sendMen,reportSql);
			this.getFormHM().put("records", _selectGzRecords);
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				withNoLock=" WITH(NOLOCK) "; 
			if(bosdate.length()>0&&count!=null){
				//-----------------------------浙江交投  薪资审批待办  start  zhaoxg add 2014-7-28-----------------------------------
				String[] temp=bosdate.split("\\.");
				LazyDynaBean bean=gzbo.getSalaryName(this.getFrameconn(), salaryid);
				bean.set("year", temp[0]);//年
				bean.set("month", temp[1]);//月
				bean.set("count", count);//次数
				bean.set("a00z2", bosdate);
				String name=bean.get("year")+"年"+bean.get("month")+"月"+bean.get("count")+"次  "+bean.get("name")+"("+bean.get("flag")+")";//待办名  “2014年06月1次 月度奖金（薪资）”
				if("appeal".equals(opt)|| "appealAll".equals(opt))	  //报批
				{
					bean.set("sql", "select *  from salaryhistory "+withNoLock+" where   salaryid="+salaryid+" and A00Z3='"+count+"' and A00Z2="+Sql_switcher.dateValue(bosdate)+" and curr_user='"+this.userView.getUserName()+"' ");
					LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, approveObject,salaryid,bean,"1");//1:报批  2：驳回  3：批准  4：阅读
					PendingTask pt = new PendingTask();
					if("add".equals(_bean.get("flag"))){
						pt.insertPending("G"+_bean.get("pending_id"),"G",name,this.userView.getUserName(),approveObject,(String)_bean.get("url"), 0, 1, "薪资审批", this.userView);
					}else if("update".equals(_bean.get("flag"))){
						pt.updatePending("G", "G"+_bean.get("pending_id"), 0, "薪资审批", this.userView);
					}				
					if("update".equals(_bean.get("selfflag"))){
						pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
					}
//					String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
//					if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
//						String username = gzbo.getZizhuUsername(approveObject);
//						WeiXinBo.sendMsgToPerson(username, name, "", "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", (String)_bean.get("url"));
//					}
				}
				else if("reject".equals(opt)|| "rejectAll".equals(opt)) //驳回
				{

					bean.set("sql", "select *  from salaryhistory "+withNoLock+" where   salaryid="+salaryid+" and A00Z3='"+count+"' and A00Z2="+Sql_switcher.dateValue(bosdate)+" and curr_user='"+this.userView.getUserName()+"' ");
					for(int i=0;i<list.size();i++){
						LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, (String) list.get(i),salaryid,bean,"2");
						PendingTask pt = new PendingTask();
						if("add".equals(_bean.get("flag"))){
							pt.insertPending("G"+_bean.get("pending_id"),"G",name,this.userView.getUserName(),(String)_bean.get("receiver"),(String)_bean.get("url"), 0, 1, "薪资审批", this.userView);
						}else if("update".equals(_bean.get("flag"))){
							pt.updatePending("G", "G"+_bean.get("pending_id"), 0, "薪资审批", this.userView);
						}					
						if("update".equals(_bean.get("selfflag"))){
							pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
						}
//						String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
//						if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
//							String username = gzbo.getZizhuUsername(approveObject);
//							WeiXinBo.sendMsgToPerson(username, name, "", "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", (String)_bean.get("url"));
//						}
					}
				}
				else if("confirm".equals(opt)|| "confirmAll".equals(opt)) //批准
				{
					bean.set("sql", "select *  from salaryhistory "+withNoLock+" where   salaryid="+salaryid+" and A00Z3='"+count+"' and A00Z2="+Sql_switcher.dateValue(bosdate)+" and curr_user='"+this.userView.getUserName()+"' ");
					LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, approveObject,salaryid,bean,"3");
					PendingTask pt = new PendingTask();			
					if("update".equals(_bean.get("selfflag"))){
						pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
					}
				}
				//------------------------------浙江交投  end-----------------------------------------------------------------------
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
