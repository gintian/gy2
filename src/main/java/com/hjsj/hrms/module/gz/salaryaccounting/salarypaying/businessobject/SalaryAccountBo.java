package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.attestation.mobile.NoteCheckSend;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.CalcTaxBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *<p>Title:薪资发放业务类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2015-7-22</p> 
 *@author dengc
 *@version 7.x
 */
public class SalaryAccountBo  {
	private Connection conn=null; 
	/**登录用户*/
	private UserView userview;
	private int salaryid;//薪资类别id 
	private SalaryTemplateBo salaryTemplateBo=null; 
	private SalaryTableStructBo salaryTableStructBo=null;  //工资表结构操作类
	private String _withNoLock="";//解决sqlserver并发\死锁问题
	private StringBuffer specialUpdTaxSql=null;
	
	public SalaryAccountBo(Connection conn,UserView userview,int salaryid) {
			this.conn = conn; 
			this.userview=userview;
		
			String rex = "^[0-9]*$"; //只能输入数字
			Pattern p = Pattern.compile(rex);
			Matcher m = p.matcher(salaryid+"");
			if (m.find()){
				this.salaryid = salaryid; 
			}
			else
				this.salaryid =0;
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			this.salaryTemplateBo=new SalaryTemplateBo(conn,salaryid,this.userview);
			this.salaryTableStructBo=new SalaryTableStructBo(this.conn,this.userview);
			specialUpdTaxSql = new StringBuffer();//在计算取专项附加值的时候拼接sql
	}
	
	
	public SalaryAccountBo(Connection conn,UserView userview,int salaryid,SalaryTemplateBo salaytemplateBo) {
		this.conn = conn; 
		this.userview=userview;
	
		String rex = "^[0-9]*$"; //只能输入数字
		Pattern p = Pattern.compile(rex);
		Matcher m = p.matcher(salaryid+"");
		if (m.find()){
			this.salaryid = salaryid; 
		}
		else
			this.salaryid =0;
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
			_withNoLock=" WITH(NOLOCK) ";
		this.salaryTemplateBo=salaytemplateBo;
		this.salaryTableStructBo=new SalaryTableStructBo(this.conn,this.userview);
		specialUpdTaxSql = new StringBuffer();//在计算取专项附加值的时候拼接sql
}

	
	
	/**
	 * 工资多级报批/驳回/批准
	 * @param appealObject  报批人 
	 * @param opt  appeal:报批  reject:驳回  confirm:批准   confirmAll:批准
	 * @param content 驳回原因
	 * @param sendMen 批准后抄送人
	 * @param busidate  发放日期和次数  year: 年   month:月   count:次
	 * @throws GeneralException
	 */
	public void gzSp(String appealObject,String opt,String content,String sendMen,String whl_str,LazyDynaBean busidate) throws GeneralException {
		RowSet rowSet=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String varcharLength="MAX";
			//【60523】VFS+UTF-8+达梦：薪资管理/薪资发放/薪资审批，审批人批准数据时报错“For input string: "8"”,详见附件！
			if(Sql_switcher.searchDbServer()!=2){
				DatabaseMetaData dbMeta = this.conn.getMetaData();
				if(dbMeta.getDatabaseMajorVersion()==8) {// sql2000=8 sql2005=9 sql2008=10 sql2012=11
					varcharLength="4000";
				}
			}
			HashMap rejectObjectSet=new HashMap();
			HashMap confirmObjectSet=new HashMap();//批准发给发起人 
			ArrayList primitiveDataTables= getPrimitiveDataTable(whl_str);
			String spFlag="03"; //批准
			if("appeal".equals(opt))	  //报批
				spFlag="02"; 
			else if("reject".equals(opt)) //驳回
				spFlag="07"; 
			String groupName=getGroupName();
			String toName="";
			if("02".equals(spFlag))
				toName=getNameByUsername(appealObject);
			String fromName=getNameByUsername(userview.getUserName());

			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间

			String  reject_mode=""; //驳回方式  1:逐级驳回  2：驳回到发起人
			ArrayList list = new ArrayList();
			if("reject".equals(opt))
			{
			    reject_mode=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode");
				if(reject_mode==null||reject_mode.trim().length()==0)
					reject_mode="1";  //驳回方式  1:逐级驳回  2：驳回到发起人
				list = this.getTable(busidate, reject_mode, whl_str);
			}else if("confirm".equals(opt)) {
				list = this.getTable(busidate, "0", whl_str);
			}
			
			String appprocess ="";
			String appuser = "";
			String curr_user="";
			//先更新临时表，再更新历史表（由于驳回需要从历史表查数据）
			for(int e=0;e<primitiveDataTables.size();e++)
			{
				String primitiveDataTable=(String)primitiveDataTables.get(e);
				String[] atemps=primitiveDataTable.split("_salary_");
				
				
				StringBuffer sql2=new StringBuffer("");

				
				
				if("appeal".equals(opt))	  //报批
				{
					sql2 = getAppConfirmSql(primitiveDataTable,atemps,whl_str,opt);
					appprocess="   \r\n报批: " + currentTime+ "\n  " + groupName + " " + fromName+ " 报批给 " + toName;
					
					ArrayList listSql2 = new ArrayList();
					listSql2.add(appprocess);
					dao.update(sql2.toString(), listSql2);
				}
				else if("confirm".equals(opt)) //批准
				{
					sql2 = getAppConfirmSql(primitiveDataTable,atemps,whl_str,opt);
					appprocess="   \r\n批准: " + currentTime+ "\n  " + groupName + " " + fromName+"\n  "+content;
					
					//批准需要发邮件给发起人
					for(int p = 0; p < list.size(); p++) {
						String tempreceiver = (String)list.get(p);
			            if(tempreceiver.indexOf("#")!=-1){
			                String[] _receiver=tempreceiver.split("#");
			                tempreceiver=_receiver[0];
			            }
			            if(StringUtils.isNotBlank(sendMen) && ("," + sendMen + ",").indexOf("," + tempreceiver + ",") == -1) {
			            	sendMen += "," + tempreceiver;
			            }else {
			            	sendMen = tempreceiver;
			            }
					}
					
					ArrayList listSql2 = new ArrayList();
					listSql2.add(appprocess);
					listSql2.add(spFlag);
					dao.update(sql2.toString(), listSql2);
				}
				else if("reject".equals(opt)) //驳回
				{
					if(Sql_switcher.searchDbServer()==1)//sqlserver这里将appprocess以追加字符串的形式update，这样不会在查询的时候速度慢  sunjian 
						sql2.append("update "+primitiveDataTable+" set sp_flag=?,appprocess=CAST(appprocess as nvarchar("+varcharLength+"))+? where 1=1 ");
					else if(Sql_switcher.searchDbServer()==2)
						sql2.append("update "+primitiveDataTable+" set sp_flag=?,appprocess=appprocess||?  where 1=1 ");
					
					ArrayList listSql2 = new ArrayList();
					sql2.append(" and a0100=? and nbase=? and a00z0=? and a00z1=?");
					StringBuffer sql3=new StringBuffer("");
					sql3.append("select a00z1,a00z0,a0100,nbase,appuser from salaryhistory ");
					sql3.append(" where  lower(userflag)='"+atemps[0].toLowerCase()+"' and  salaryhistory.salaryid="+this.salaryid+"  ");
					sql3.append(whl_str);
					rowSet=dao.search(sql3.toString());	
					while(rowSet.next()){
						boolean isRejectSalaryData=true;  //是否驳回到工资发放表
						String _temp=rowSet.getString("appuser").substring(1);
						String[] arg=_temp.split(";");
						ArrayList listL = new ArrayList();
						if(_temp.split(";").length>=2)
						{
							isRejectSalaryData=false;
						}
						appprocess= "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " + fromName+ " 驳回审批。\n  驳回原因："+content;
					
						if(isRejectSalaryData|| "2".equals(reject_mode))//后面加0表示驳回到发放表
						{
							listL.add("07");
							rejectObjectSet.put(atemps[0],"0");
						}
						else
						{
							listL.add("02");
							//可能是驳回给不同的人
							for(int p = 0; p < list.size(); p++) {
								String tempreceiver = (String)list.get(p);
					            if(tempreceiver.indexOf("#")!=-1){
					                String[] _receiver=tempreceiver.split("#");
					                tempreceiver=_receiver[0];
					            }
								rejectObjectSet.put(tempreceiver,"1");
							}
						}
						listL.add(appprocess);
						listL.add(rowSet.getString("a0100"));
						listL.add(rowSet.getString("nbase"));
						listL.add(rowSet.getDate("a00z0"));
						listL.add(rowSet.getString("a00z1"));
						listSql2.add(listL);
					}
					dao.batchUpdate(sql2.toString(), listSql2);
				}
				
				PubFunc.closeDbObj(rowSet);
			
					
				if("reject".equals(opt)) //驳回
				{
					StringBuffer sql=new StringBuffer("delete from salaryhistory   where exists (select null from ");
					sql.append(primitiveDataTable+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ");
					sql.append(" a.a0100=salaryhistory.a0100 and a.sp_flag='07' )   and lower(salaryhistory.userflag)='"+atemps[0].toLowerCase()+"'  and salaryhistory.salaryid="+this.salaryid);
					dao.update(sql.toString());
				}
			} 
			
			/**-----------------------------------------------------**/
			StringBuffer sql1=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==1)//sqlserver这里将appprocess以追加字符串的形式update，这样不会在查询的时候速度慢  sunjian 
				sql1.append("update salaryhistory set sp_flag=?,appprocess=CAST(appprocess as nvarchar("+varcharLength+"))+?");
			else if(Sql_switcher.searchDbServer()==2)
				sql1.append("update salaryhistory set sp_flag=?,appprocess=appprocess||?");
			
			ArrayList listSql1 = new ArrayList();
			
			if("appeal".equals(opt))	  //报批
			{
				if(Sql_switcher.searchDbServer()==1)
					sql1.append(",appuser=?+appuser,curr_user=? ");
				else if(Sql_switcher.searchDbServer()==2)
					sql1.append(",appuser=?||appuser,curr_user=? ");
				curr_user=appealObject;
				appuser=";"+this.userview.getUserName();
				appprocess ="   \r\n报批: " + currentTime+ "\n  " + groupName + " " + fromName+ " 报批给 " + toName;
				
				listSql1.add(spFlag);
				listSql1.add(appprocess);
				listSql1.add(appuser);
				listSql1.add(curr_user);
			}
			else if("confirm".equals(opt)) //批准
			{
				if(Sql_switcher.searchDbServer()==1)
					sql1.append(",appuser=?+appuser,curr_user=? ");
				else if(Sql_switcher.searchDbServer()==2)
					sql1.append(",appuser=?||appuser,curr_user=? ");
				curr_user="";
				appuser=";"+this.userview.getUserName();
				appprocess="   \r\n批准: " + currentTime+ "\n  " + groupName + " " + fromName+"\n  "+content;
				
				listSql1.add(spFlag);
				listSql1.add(appprocess);
				listSql1.add(appuser);
				listSql1.add(curr_user);
			}
			else if("reject".equals(opt)) //驳回
			{
				if(Sql_switcher.searchDbServer()==1) {//对于驳回做特殊处理，appuser（例：;su;sj;sunj;截取后得到;sj;sunj;）curr_user（根据appuser截取的值得到上例中得到su为curr_user）
					sql1.append(",appuser=substring(appuser,charindex(';',appuser,2),LEN(appuser))");
					sql1.append(",curr_user=substring(appuser,2,charindex(';',appuser,2)-2) ");
				}else if(Sql_switcher.searchDbServer()==2)//instr（'源字符串' , '目标字符串' ,'开始位置','第几次出现'） 
					sql1.append(",appuser=substr(appuser,instr(appuser,';',1,2)),curr_user=substr(appuser,2,instr(appuser,';',1,2)-2) ");
				appprocess= "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " + fromName+ " 驳回审批。\n  驳回原因："+content;
			
				listSql1.add("07");
				listSql1.add(appprocess);
			}
			sql1.append(" where 1=1 " + whl_str);
			dao.update(sql1.toString(), listSql1);
			if("appeal".equalsIgnoreCase(opt))
			{
				//发送 邮件 和 短信通知
				if(this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE)!=null&&this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE).length()>0)
				{
					sendMessage(appealObject,"","薪资报批",false);
				}
				
			}
			if("confirm".equalsIgnoreCase(opt)|| "reject".equals(opt))
			{
//				发送 邮件 和 短信通知
				boolean flag=false;
				if(this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"mail")))
					flag=true;
				if(this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"sms")))
					flag=true;
				if(flag)
				{
					if("reject".equals(opt))
					{
						if(rejectObjectSet.size()>0)
						{
							StringBuffer sendMens=new StringBuffer("");
							StringBuffer sendMensIsUserFlag=new StringBuffer("");//是否驳回到发起人，如果驳回到发起人的话自动登录链接走的是发放界面 sunjian 2017-08-04
							Iterator iter = rejectObjectSet.entrySet().iterator();
							while (iter.hasNext()) 
							{
								Map.Entry entry = (Map.Entry) iter.next();
								Object key = entry.getKey();
								Object val = entry.getValue();
								if("0".equals(val)) {//驳回到发放表，isUserFlag = true
									sendMensIsUserFlag.append(key+",");
								}else if("1".equals(val)) {
									sendMens.append(key+",");
								}
							}
							if(StringUtils.isNotBlank(sendMens.toString())) 
								sendMessage(sendMens.toString(),content,"薪资驳回",false);
							
							if(StringUtils.isNotBlank(sendMensIsUserFlag.toString()))
								sendMessage(sendMensIsUserFlag.toString(),content,"薪资驳回",true);
						}
					}else {
						if(sendMen!=null&&sendMen.length()>0) {
							sendMessage(sendMen,content,"薪资批准",false);
						}
					}
				} 
			}
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				withNoLock=" WITH(NOLOCK) "; 
			String bosdate = (String)busidate.get("year")+"."+(String)busidate.get("month")+"."+(String)busidate.get("day");
			String count = (String)busidate.get("count");
			if(bosdate.length()>0&&count!=null){
				String[] temp=bosdate.split("\\.");
				LazyDynaBean bean=this.getSalaryName(this.conn, salaryid+"");
				bean.set("year", temp[0]);//年
				bean.set("month", temp[1]);//月
				bean.set("count", count);//次数
				bean.set("a00z2", bosdate);
				String name=bean.get("year")+"年"+bean.get("month")+"月第"+bean.get("count")+"次  "+bean.get("name")+"("+bean.get("flag")+")";//待办名  “2014年06月第1次 月度奖金（薪资）”
				if("appeal".equals(opt)|| "appealAll".equals(opt))	  //报批
				{
					bean.set("sql", "select count(salaryid)  from salaryhistory "+withNoLock+" where   curr_user='"+this.userview.getUserName()+"' and salaryid="+salaryid+" and A00Z3='"+count+"' and A00Z2="+Sql_switcher.dateValue(bosdate));
					LazyDynaBean _bean=updatePendingTask(this.conn, this.userview, appealObject,salaryid+"",bean,"1");//1:报批  2：驳回  3：批准  4：阅读
					PendingTask pt = new PendingTask();
					if("add".equals(_bean.get("flag"))){
						pt.insertPending("G"+_bean.get("pending_id"),"G",name,this.userview.getUserName(),appealObject,(String)_bean.get("url"), 0, 1, "薪资审批", this.userview);
					}else if("update".equals(_bean.get("flag"))){
						pt.updatePending("G", "G"+_bean.get("pending_id"), 0, "薪资审批", this.userview);
					}				
					if("update".equals(_bean.get("selfflag"))){
						pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userview);
					}
				}
				else if("reject".equals(opt)|| "rejectAll".equals(opt)) //驳回
				{
					bean.set("sql", "select  count(salaryid)   from salaryhistory "+withNoLock+" where   salaryid="+salaryid+" and A00Z3='"+count+"' and A00Z2="+Sql_switcher.dateValue(bosdate)+" and curr_user='"+this.userview.getUserName()+"' ");
					for(int i=0;i<list.size();i++){
						LazyDynaBean _bean=updatePendingTask(this.conn, this.userview, (String) list.get(i),salaryid+"",bean,"2");
						PendingTask pt = new PendingTask();
						if("add".equals(_bean.get("flag"))){
							pt.insertPending("G"+_bean.get("pending_id"),"G",name,this.userview.getUserName(),(String)_bean.get("receiver"),(String)_bean.get("url"), 0, 1, "薪资审批", this.userview);
						}else if("update".equals(_bean.get("flag"))){
							pt.updatePending("G", "G"+_bean.get("pending_id"), 0, "薪资审批", this.userview);
						}					
						if("update".equals(_bean.get("selfflag"))){
							pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userview);
						}
					}
				}
				else if("confirm".equals(opt)|| "confirmAll".equals(opt)) //批准
				{
					bean.set("sql", "select  count(salaryid)   from salaryhistory "+withNoLock+" where   salaryid="+salaryid+" and A00Z3='"+count+"' and A00Z2="+Sql_switcher.dateValue(bosdate)+" and curr_user='"+this.userview.getUserName()+"' ");
					LazyDynaBean _bean=updatePendingTask(this.conn, this.userview, this.userview.getUserName(),salaryid+"",bean,"3");
					PendingTask pt = new PendingTask();			
					if("update".equals(_bean.get("selfflag"))){
						pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userview);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 获取更新临时表报批和批准的sql,这里必须要salaryhistory的curr_user字段，所以得关联历史表
	 * @Title: getAppConfirmSql   
	 * @Description:    
	 * @param @param primitiveDataTable：临时表名
	 * @param @param atemps：
	 * @param @param whl_str：条件
	 * @param @return 
	 * @return StringBuffer    
	 * @throws
	 * @author sunjian
	 * @date 2017-08-04
	 */
	private StringBuffer getAppConfirmSql(String primitiveDataTable,String[] atemps,String whl_str,String opt) {
		StringBuffer sql2 = new StringBuffer();
		String varcharLength="MAX";
		try {
			if(Sql_switcher.searchDbServer()!=2){
				DatabaseMetaData dbMeta = this.conn.getMetaData();
				if(dbMeta.getDatabaseMajorVersion()==8) {// sql2000=8 sql2005=9 sql2008=10 sql2012=11
					varcharLength="4000";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(Sql_switcher.searchDbServer()==1)//sqlserver这里将appprocess以追加字符串的形式update，这样不会在查询的时候速度慢  sunjian 
			sql2.append("update "+primitiveDataTable+" set "+primitiveDataTable+".appprocess=CAST("+primitiveDataTable+".appprocess as nvarchar("+varcharLength+"))+? ");
		else if(Sql_switcher.searchDbServer()==2)
			sql2.append("update "+primitiveDataTable+" set appprocess=appprocess||? ");
		if("confirm".equals(opt))
			sql2.append(","+primitiveDataTable+".sp_flag=?");
		sql2.append(" where exists(select null from  salaryhistory where "+primitiveDataTable+".a00z0=salaryhistory.a00z0 ");
		sql2.append("and "+primitiveDataTable+".a00z1=salaryhistory.a00z1 ");
		sql2.append("and upper("+primitiveDataTable+".nbase)=upper(salaryhistory.nbase) ");
		sql2.append("and "+primitiveDataTable+".a0100=salaryhistory.a0100 and "+primitiveDataTable+".sp_flag='02' ");
		sql2.append("and lower(salaryhistory.userflag)='"+atemps[0].toLowerCase()+"'");
		sql2.append(whl_str+")");
		return sql2;
	}
	
	/**
	 * 取得 薪资（保险）类别名
	 * @return
	 * @throws GeneralException
	 */
	private LazyDynaBean getSalaryName(Connection conn,String salaryid)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
				ContentDAO dao=new ContentDAO(conn);
				String sql="select cname,cstate from salarytemplate where salaryid="+salaryid+"";
				String name="";
				String flag="";
				String cstate="";
				RowSet rs=dao.search(sql);
				if(rs.next()){
					name=rs.getString("cname");
					cstate=rs.getString("cstate");
					if("1".equals(cstate)){
						flag="保险";
					}else{
						flag="薪资";
					}
					abean.set("name", name);
					abean.set("flag", flag);
					abean.set("cstate", cstate==null||"".equals(cstate)?"0":cstate);
				}
				rs.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	/**
	 * 获取驳回人物
	 * @param dataBean
	 * @param reject_mode
	 * @param whl_str
	 * @return
	 */
	private ArrayList getTable(LazyDynaBean dataBean,String reject_mode,String whl_str)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);		
			StringBuffer sql2=new StringBuffer("select distinct userflag,appuser ");
			StringBuffer temp_sql=new StringBuffer("");
			temp_sql.append(" from salaryhistory "+this._withNoLock+" where Salaryid="+this.salaryid);
			temp_sql.append(" and "+Sql_switcher.year("A00Z2")+"="+(String)dataBean.get("year") );
			temp_sql.append(" and "+Sql_switcher.month("A00Z2")+"="+(String)dataBean.get("month") );
			temp_sql.append(" and "+Sql_switcher.day("A00Z2")+"="+(String)dataBean.get("day") );
			temp_sql.append(" and A00Z3="+(String)dataBean.get("count"));	
			sql2.append(temp_sql.toString());
			if(whl_str.length()>0)
				sql2.append(whl_str);
			RowSet rowSet=dao.search(sql2.toString());
			if("1".equals(reject_mode)){//逐级驳回，找驳回给谁
				while(rowSet.next())
				{
					String rejectuser = "";
					String temp=rowSet.getString("appuser");
					String[] app = temp.split(";");
					for(int i=0;i<app.length;i++){
						if(app[i].length()>0){
							rejectuser = app[i];
							break;
						}
					}
					list.add(rejectuser+"#"+rowSet.getString("userflag"));
				}
			}else{
				while(rowSet.next())
				{
					String temp=rowSet.getString("userflag");
					list.add(temp+"#"+temp);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}	
	/**
	 * 获得报批人所在部门-〉单位 -〉用户组
	 * @return
	 */
	private String getGroupName()
	{
		
		 String groupName="";
		 RowSet rowSet=null;
		try
		{
				 ContentDAO dao=new ContentDAO(this.conn);
		         if(userview.getA0100()!=null&&userview.getA0100().trim().length()>0)
		             rowSet=dao.search("select b0110,e0122 from "+userview.getDbname()+"A01 where a0100='"+userview.getA0100()+"'");
		         else
		             rowSet=dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+userview.getUserName()+"'");
		         if(rowSet.next())
		         {
		             if(userview.getA0100()!=null&&userview.getA0100().trim().length()>0)
		             {
		                 String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110").trim():"";
		                 String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122").trim():"";
		                 if(e0122.length()>0)
		                     groupName=AdminCode.getCodeName("UM",e0122);
		                 else if(b0110.length()>0)
		                     groupName=AdminCode.getCodeName("UN",b0110);
		             }
		             else
		                 groupName=rowSet.getString(1);
		         }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return groupName;
	}

	/**
	 * 根据用户名获得关联用户的姓名，如没有关联用户则获得用户的全称，否则得到用户名
	 * @param username
	 * @author dengc
	 * @serialData 2013-11-27
	 * @return
	 */
	private String getNameByUsername(String username)
	{
		String name=username;
		RowSet rowSet=null; 
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select a0100,nbase,fullname   from operuser  where   username=?",Arrays.asList(new Object[] {username}));
			if(rowSet.next())
			{
				String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100").trim():"";
				String nbase=rowSet.getString("nbase")!=null?rowSet.getString("nbase").trim():"";
				String fullname=rowSet.getString("fullname")!=null?rowSet.getString("fullname").trim():""; 
				if(a0100.length()>0&&nbase.length()>0)
				{
					rowSet=dao.search("select a0101 from "+nbase+"A01 where a0100=?",Arrays.asList(new Object[] {a0100}));
					if(rowSet.next())
						name=rowSet.getString("a0101");
				}
				else if(fullname.length()>0)
				{
					name=fullname;
				}
				
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return name;
	}
	
	/**
	 * 取得薪资历史表中 涉及到的薪资发放临时表
	 * @param  where:   and  salaryid=xxx and A00Z3=xxxx  and A00Z2=xxx and curr_user=xxxx and ( sp_flag='02' or  sp_flag='07' )
	 */
	private ArrayList getPrimitiveDataTable(String whl_str)
	{
		ArrayList primitiveDataTable=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			if(this.salaryTemplateBo.getManager()!=null&&this.salaryTemplateBo.getManager().trim().length()>0)
			{
				primitiveDataTable.add(this.salaryTemplateBo.getManager()+"_salary_"+salaryid);
			}
			else
			{
				StringBuffer sql2=new StringBuffer("select distinct userflag ");
				sql2.append(" from salaryhistory "+this._withNoLock+" where Salaryid=?"); 
				sql2.append(whl_str); 
				rowSet=dao.search(sql2.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid) }));
				while(rowSet.next())
				{
						primitiveDataTable.add(rowSet.getString(1)+"_salary_"+salaryid);
				}  
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return primitiveDataTable;
	}
	
	
	
	/**
	 * 根据条件取得工资历史数据表待批数据的审批意见 
	 * @param  where:   and  salaryid=xxx and A00Z3=xxxx  and A00Z2=xxx and curr_user=xxxx and ( sp_flag='02' or  sp_flag='07' )  
	 * @return
	 */
	private  HashMap getCurrentSalaryhistoryData(String where)throws GeneralException
	{
		RowSet rowSet=null;
		HashMap appprocessMap=new HashMap();
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn); 
			StringBuffer sql=new StringBuffer("select  a0100,nbase,appprocess,appuser,curr_user,a00z0,a00z1 from salaryhistory ");
			sql.append(" where  1=1 "+where); 
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
			 
			rowSet=dao.search(sql.toString());
			String a0100="";
			String nbase="";
			String a00z0="";
			String a00z1="";
			String appprocess="";
			String appuser="";
			while(rowSet.next())
			{
					a0100=rowSet.getString("a0100");
					nbase=rowSet.getString("nbase");
					a00z0=df.format(rowSet.getDate("a00z0"));
					a00z1=rowSet.getString("a00z1");
					appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
					appuser=rowSet.getString("appuser")!=null?rowSet.getString("appuser"):"";
					appprocessMap.put(a0100.toLowerCase()+"/"+nbase.toLowerCase()+"/"+a00z0+"/"+a00z1,appprocess+"~"+appuser+"~"+rowSet.getString("curr_user")); 
			} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return appprocessMap;
	}
	
	
	
	
	
	/**
	 * @Title: isSalaryPayed 
	 * @Description: TODO(hasDoGz:false 判断薪资发放记录表中是否有没提交的工资  hasDoGz:true 判断是否发过工资) 
	 * @param salaryid 薪资类别号
	 * @param hasDoGz  是否发放过工资
	 * @return boolean
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-10 下午06:19:34
	 */
	public boolean isSalaryPayed(String salaryid) throws GeneralException
	{
		boolean flag = false;
		RowSet rowSet = null;
		try {
			String manager = this.salaryTemplateBo.getManager();
			ContentDAO dao = new ContentDAO(this.conn);
			String username=this.userview.getUserName(); 
			String sql = "select * from gz_extend_log where username=? and sp_flag<>'06' and salaryid=?";
			if (StringUtils.isNotBlank(manager)&& !manager.equals(this.userview.getUserName())) {
				username=manager;
			} 
			rowSet = dao.search(sql,Arrays.asList(new Object[] {username,Integer.valueOf(salaryid) }));
			if (rowSet.next()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return flag;
	}
	
	/**
	 * 创建新薪资表
	 * @param year 		年
	 * @param month		月
	 * @return String 发放次数
	 * @throws GeneralException
	 */
	public String createNewGzTable(String year,String month) throws GeneralException
	{ 
		int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
		SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
		String gz_tablename=this.salaryTemplateBo.getGz_tablename(); //获得薪资发放临时表名
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		String count="1"; 
		try
		{
			/**归属日期及归属次数*/
			if(month.length()==1)
				month="0"+month;
			String date=year+"-"+month+"-01";
			
			
			LazyDynaBean	busiDate=new LazyDynaBean(); //业务日期 次数   date:2010-03-01   count:1
			busiDate.set("date",date);
			busiDate.set("count",count);
			
			//根据工资类别id得到类别下面的所有项目列表
			ArrayList itemList=this.salaryTemplateBo.getSalaryItemList("",""+salaryid,1);
			//获得临时变量指标列表（过滤薪资帐套不用的临时变量）
			ArrayList midList=this.salaryTemplateBo.getMidVariableListByTable(""+salaryid);
			/**通过业务日期获得薪资发放次数*/ 
			count=""+getFfCcount(date); 
			String noManyTimes=SystemConfig.getPropertyValue("noManyTimes_gzPlay");
			if(!"1".equals(count)&&StringUtils.isNotBlank(noManyTimes)&& "true".equalsIgnoreCase(noManyTimes))
				throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("gz.acount.noManyTimes_gzPlay")));//由于配置了noManyTimes_gzPlay参数，每月仅可进行一次发放！
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,salaryid,this.userview);
			//在新建之前加上一个同步，防止由于指标被取消构库等风险2017-06-03
			gzbo.SalarySet(itemList);
			/**在薪资发放历史记录表中增加一条发放记录*/
			DbNameBo.appendExtendLog(this.userview.getUserName(),salaryid,date,count,this.conn);
			 
			//创建薪资数据表
		//	DbWizard dbw=new DbWizard(this.conn);
		//	Table table=new Table(gz_tablename);
		//	dbw.dropTable(table);
			salaryTableStructBo.createGzDataTable(gz_tablename,itemList,midList,ctrlparam);
			
			/**第二步根据薪资类别适用范围，过滤人员范围以及生成薪资已停发人员*/ 
			String dataTableName="salaryhistory";//上期数据位置，默认历史表，如上期数据从归档表取得 则为归档表 zhanghua 2017-6-8
			
			String  royalty_valid=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");  //提成工资是否启用
			if(royalty_valid!=null&& "1".equals(royalty_valid))
			{
				
				importAddManData_royalty(year,month,count,itemList);
				delNoConditionData2(gz_tablename);
			}
			else
			{
				
				String noPreData="false";
				if(SystemConfig.getPropertyValue("noPreData_gz")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noPreData_gz").trim()))  //新建工资表自动变动比对,其实就是不取上月数据
						noPreData="true";
				if("true".equalsIgnoreCase(noPreData))
				{
						createAddManTable(1,date,new HashMap());
						/**导入新增人员*/
						importAddManData(false,date,count,itemList,false);
						/**删除薪资停发人员*/
						removeA01Z0ManData();
				}
				else
				{
						LazyDynaBean preDateBean= getPreCountDate(""+salaryid,date,count,dao);
						if(preDateBean==null){
							createAddManTable(1,date,new HashMap());
							/**导入新增人员*/
							importAddManData(false,date,count,itemList,false);
							/**删除薪资停发人员*/
							removeA01Z0ManData();
						}else{
							createAddManTable(preDateBean);
							dataTableName=(String)preDateBean.get("tablename");
							/**导入新增人员*/
							importAddManData(false,date,count,itemList,false);
						}
				}
			 
			
			
				/**第三步设置A00Z0,A00Z1的值*/
				buf.setLength(0);
				buf.append("update ");
				buf.append(gz_tablename);
				buf.append(" set A00Z0=?,A00Z1=?");
				
				Date src_d=DateUtils.getDate(date,"yyyy-MM-dd");
				java.sql.Date d=new java.sql.Date(src_d.getTime());
				ArrayList paramList=new ArrayList();
				paramList.add(d);
				paramList.add(Integer.parseInt(count));
				dao.update(buf.toString(),paramList);
				 
				/**发放日期和发放次数*/
				buf.setLength(0);
				buf.append("update ");
				buf.append(gz_tablename);
				buf.append(" set A00Z2=A00Z0,A00Z3=A00Z1,sp_flag='01'");
				if(this.salaryTemplateBo.getManager().length()>0)
				{
					buf.append(",sp_flag2='01'");
				}
				dao.update(buf.toString());
				
				/** 如果发现历史表中有当月相同的次数，归属次数就自动加1 */
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set a00z1=(select a00z1+1 from "+dataTableName+" where  "+gz_tablename+".a0100="+dataTableName+".a0100 ");
				buf.append(" and "+gz_tablename+".nbase="+dataTableName+".nbase and "+gz_tablename+".a00z0="+dataTableName+".a00z0 and "+gz_tablename+".a00z1="+dataTableName+".a00z1 and salaryid="+salaryid);
				buf.append(" ) where exists (select null from "+dataTableName+" where  "+gz_tablename+".a0100="+dataTableName+".a0100 ");
				buf.append(" and "+gz_tablename+".nbase="+dataTableName+".nbase and "+gz_tablename+".a00z0="+dataTableName+".a00z0 and "+gz_tablename+".a00z1="+dataTableName+".a00z1 and salaryid="+salaryid+"  ) ");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set b0110_o=(select a0000 from organization where organization.codeitemid="+gz_tablename+".b0110 and organization.codesetid='UN' )");
				buf.append(" where exists (select null from organization where organization.codeitemid="+gz_tablename+".b0110 and organization.codesetid='UN' )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set e0122_o=(select a0000 from organization where organization.codeitemid="+gz_tablename+".e0122 and organization.codesetid='UM' )");
				buf.append(" where exists (select null from organization where organization.codeitemid="+gz_tablename+".e0122 and organization.codesetid='UM' )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set dbid=(select dbid from dbname where upper(dbname.pre)=upper("+gz_tablename+".nbase)  )");
				buf.append(" where exists (select null from dbname where upper(dbname.pre)=upper("+gz_tablename+".nbase) )");
				dao.update(buf.toString());
			}
			
			//写入薪资发放数据的映射表
			dao.update("delete from salary_mapping where salaryid=?  and lower(USERFLAG)=? ",Arrays.asList(new Object[]{new Integer(salaryid),this.userview.getUserName().toLowerCase()}));
			dao.update("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) select a0100,nbase,a00z0,a00z1,"+salaryid+",'"+this.userview.getUserName().toLowerCase()+"' from "+gz_tablename);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return count;
	}
	
	
	
	/**
	 * 导入新增人员的数据(提成工资)
	 * @param year 发放年份
	 * @param month 发放月份
	 * @param count 发放次数
	 * @param gzitemList 工资项目<LazyDynaBean>
	 * @throws GeneralException
	 */
	private void importAddManData_royalty(String year,String month,String count,ArrayList gzitemList)throws GeneralException
	{ 
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam(); 
			String royalty_setid=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			
			if(StringUtils.isBlank(royalty_setid))
				throw GeneralExceptionHandler.Handle(new Throwable("请设置提成数据子集！"));
			String royalty_date=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"date");
			String royalty_period=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"period");
			String royalty_relation_fields=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			String strExpression=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"strExpression");
			String strset=null;
			String strc=null;
			String strpre=null;
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase"); 
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer("");
			String pay_flag=ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG); 
			boolean  payFlag_isExist=false;
			LazyDynaBean itemBean=null;
			Set fieldSet=new HashSet();
			for(int e=0;e<gzitemList.size();e++)
			{
				itemBean=(LazyDynaBean)gzitemList.get(e);
				String initflag=(String)itemBean.get("initflag");  //累积方式  =0不累积
				String itemid=(String)itemBean.get("itemid");
				String fieldsetid=(String)itemBean.get("fieldsetid");
				if(!"".equals(fieldsetid)&&fieldsetid.trim().length()>0)
					fieldSet.add(fieldsetid.toUpperCase());
				if("0".equals(initflag))
					continue;
				if(pay_flag!=null&&pay_flag.equalsIgnoreCase(itemid))
					payFlag_isExist=true;
			} 
			for(int i=0;i<dbarr.length;i++)
			{
				strpre=dbarr[i];
				buf.setLength(0);
				String strlst="";
				for(Iterator t=fieldSet.iterator();t.hasNext();)
				{
					strset=(String)t.next();
					if("A01".equalsIgnoreCase(strset)||strset.equalsIgnoreCase(royalty_setid))
					{
						strlst+=","+getInsFieldSQL(strset,gzitemList,",'"+pay_flag.toUpperCase()+"'","");
						 
					} 
				}
				if(strlst.length()>0)
					strlst=strlst.substring(1);
				 
				buf.append("insert into ");
				buf.append(this.salaryTemplateBo.getGz_tablename());
				buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
				if(this.salaryTemplateBo.getManager().length()>0)
					buf.append("sp_flag2,");
				if(payFlag_isExist&&pay_flag.length()!=0)
				{
					
					buf.append(pay_flag);
					buf.append(",");
				}
				buf.append(strlst);
				buf.append(") select '");
				buf.append(this.userview.getUserName());
				buf.append("','"+strpre.toUpperCase()+"',");
				buf.append(Sql_switcher.dateValue(year+"-"+month+"-01"));
				buf.append(","+count+",");
				buf.append(Sql_switcher.dateValue(year+"-"+month+"-01"));
				buf.append(",");
				buf.append(strpre+royalty_setid+".i9999");
				buf.append(",'01',");
				if(this.salaryTemplateBo.getManager().length()>0)
					buf.append("'01',");
				if(payFlag_isExist&&pay_flag.length()!=0)
				{
					buf.append("'0',"); // 0: 正常发薪  1:当月补发 2:全月补发 3:半月补发
				}
				strlst=strlst.replaceAll("A0100", strpre+"A01.A0100");
				buf.append(strlst);
				
				strc=strpre+royalty_setid;
				
				buf.append(" from ");
				buf.append(strc+","+strpre+"A01");
				buf.append(" where "+strc+".a0100="+strpre+"A01.a0100 ");
				int[] months=getMonth(month,royalty_period);
				if("1".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+"="+month);
				if("2".equals(royalty_period)|| "3".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
				{
					buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+" in ( 100");
					for(int n=0;n<months.length;n++)
					{
						buf.append(","+months[n]);
					}
					buf.append(" )");
				}
				if("4".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year);
				
				
				if(strExpression!=null&&strExpression.trim().length()>0)
				{ 
					try
					{
					 
						YksjParser yp = new YksjParser( this.userview ,DataDictionary.getFieldList(royalty_setid, 1),
								YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
						yp.run_where(strExpression);
						String whl=yp.getSQL();
						buf.append(" and ( "+whl+" )");
					}
					catch(Exception ee)
					{
						throw new Exception("薪资类别数据范围定义错误!");
					}
				}
				
				dbw.execute(buf.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 删除不在条件范围中的人员
	 * @param tableName
	 * @author dengcan
	 */
	public void delNoConditionData2(String tablename)
	{
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			/**导入数据*/
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
			/**应用库前缀*/
			SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam(); 
			String[] dbarr=StringUtils.split(dbpres, ",");
			String flag=ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String aflag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			String cond=this.salaryTemplateBo.getTemplatevo().getString("cond");
			String cexpr=this.salaryTemplateBo.getTemplatevo().getString("cexpr");		
			String sql="";
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			/**需要审批,仅导入起草和驳回记录*/ 
			String whl2="";
			if(isApprove())
			{ 
				whl2+=" and "+ tablename+"."+"sp_flag in('01','07')";				
			}	
			
			
			for(int i=0;i<dbarr.length;i++)
			{
					String pre=dbarr[i];
					
					if(aflag!=null&& "1".equals(aflag))
					{
						String asql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )"+whl2;
						dao.delete(asql,new ArrayList());
					}
					
					if(flag!=null&& "0".equals(flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql ="";
						if(factor.size()>0)
						{
							strSql=factor.getSqlExpression();				
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not in (select "+pre+"a01.a0100 "+strSql+" )"+whl2;
							dao.delete(sql,new ArrayList());
						}
					}
					
					if(flag!=null&& "1".equals(flag)&&cond.length()>0)  // 1：复杂条件
					{
						
						int infoGroup = 0; // forPerson 人员
						int varType = 8; // logic	
						
						String whereIN="select a0100 from "+pre+"A01";
						alUsedFields.addAll(this.getMidVariableList());
						YksjParser yp = new YksjParser(this.userview ,alUsedFields,
								YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
						YearMonthCount ymc=null;							
						yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
						String tempTableName = yp.getTempTableName();
						String w = yp.getSQL();
						if(w!=null&&w.trim().length()>0)
						{
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not ";
							sql+=" in (select a0100 from "+tempTableName+" where "+w+" )"+whl2;
							dao.delete(sql,new ArrayList());
						}
					}
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 获得同一周期下的月数
	 * @param month
	 * @param royalty_period 周期类型
	 * @return
	 */
	private int[] getMonth(String month,String royalty_period)
	{
		int[] months=null;
		int a_month=Integer.parseInt(month);
		if("2".equals(royalty_period)) //季
		{
			months=new int[3];
			if(a_month<=3)
			{
				months[0]=1;months[0]=2;months[0]=3;
			}
			else if(a_month>3&&a_month<=6)
			{
				months[0]=4;months[0]=5;months[0]=6;
			}
			else if(a_month>6&&a_month<=9)
			{
				months[0]=7;months[0]=8;months[0]=9;
			}
			else if(a_month>9&&a_month<=12)
			{
				months[0]=10;months[0]=11;months[0]=12;
			}
		}
		else if("3".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
		{
			months=new int[6];
			if(a_month<=6)
			{
				months[0]=1;months[0]=2;months[0]=3;months[0]=4;months[0]=5;months[0]=6;
			}
			else 
			{
				months[0]=7;months[0]=8;months[0]=9;months[0]=10;months[0]=11;months[0]=12;
			}
		}
		return months;
	}
	
	/**
	 * @Title: getMaxYearMonthCount 
	 * @Description: TODO(求当前处理到的最大业务日期和次数 ) 
	 * @param flag  审批状态
	 * @param isAddExtendLog 是否追加到发放日志
	 * @return HashMap ym：年月，count：发放次数
	 * @author lis  
	 * @date 2015-8-10 下午04:54:33
	 */
	public HashMap getMaxYearMonthCount(String sp_flag,boolean isAddExtendLog)
	{
		String manager=this.salaryTemplateBo.getManager();
		int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
		HashMap mp=new HashMap();
		String strYm=null;
		String strC=null;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where salaryid=? and ");
		buf.append(" upper(username)=? ");
		if(StringUtils.isNotBlank(sp_flag))
			buf.append(" and sp_flag=?");
		ArrayList dataList=new ArrayList();
		dataList.add(new Integer(salaryid)); 
		if(StringUtils.isBlank(manager))
			dataList.add(this.userview.getUserName().toUpperCase()); 
		else
			dataList.add(manager.toUpperCase()); 
		if(StringUtils.isNotBlank(sp_flag))//薪资重发审批状态
			dataList.add(sp_flag); 
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString(),dataList);
			if(rset.next())
				strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
			if("".equalsIgnoreCase(strYm))
			{
			
				strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
				String[] tmp=StringUtils.split(strYm, "-");
				strYm=tmp[0]+"-"+tmp[1]+"-01";
				strC="1"; 
				
				if(isAddExtendLog)//薪资重发，需要新增一条记录，重置业务日期时不需要
					DbNameBo.appendExtendLog(this.userview.getUserName(),salaryid,strYm,strC,this.conn);
			}
			else
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
				buf.append(" where salaryid=?  and ");
				buf.append(" upper(username)=? "); 
				if(StringUtils.isNotBlank(sp_flag))//薪资重发审批状态
					buf.append(" and sp_flag=?");
				buf.append(" and A00Z2=?");
				Date date = DateUtils.getSqlDate(strYm,"yyyy-MM-dd");
				dataList.add(date);
				rset=dao.search(buf.toString(),dataList);
				if(rset.next())
					strC=rset.getString("A00Z3");
					strC=strC!=null&&strC.trim().length()>0?strC:"1";
			}
			 
			mp.put("ym",strYm);
			mp.put("count", strC);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return mp;
	}
	
	/**
	 * @Title: getStandardGzItemStr 
	 * @Description: TODO(标准薪资表涉及到的字段) 
	 * @param salaryid
	 * @return String
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-7-31 下午05:44:11
	 */
	public String getStandardGzItemStr(int salaryid) throws GeneralException{
		try {
			String   standardGzItemStr="/";  //标准薪资表涉及到的字段
			ArrayList tempList=this.salaryTemplateBo.getSalaryItemList(null, String.valueOf(salaryid), 2);
			for(int i=0;i<tempList.size();i++)
			{
				FieldItem field=(FieldItem)tempList.get(i);
				String name=field.getItemid().toUpperCase();
				standardGzItemStr+=name+"/";
			}
			standardGzItemStr+="USERFLAG/";
			standardGzItemStr+="SP_FLAG/";
			standardGzItemStr+="APPPROCESS/";
			return standardGzItemStr;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 计算工资数据
	 * @param flag  1:薪资发放   2:薪资审批
	 * @param itemids  计算项
	 * @param paramMap:   where_str  计算数据筛选条件, ym: 薪资发放月份  2015-02-01,count 薪资发放次数
	 * @return  true：成功
	 */
	public String   computeGz(int flag,ArrayList itemids,HashMap paramMap)throws GeneralException
	{
		String msg="success";
		String where_str=(String)paramMap.get("where_str"); //计算数据筛选条件
		String ym=(String)paramMap.get("ym"); //薪资发放月份
		String count=(String)paramMap.get("count"); //薪资发放次数
		if("".equals(ym)&&"".equals(count))
			return msg;
		paramMap.put("templatevo",this.salaryTemplateBo.getTemplatevo());
		String gz_tableName=this.salaryTemplateBo.getGz_tablename();
		ArrayList userList = new ArrayList();
		if(flag==2){
			gz_tableName="t#"+this.userview.getUserName()+"_gzsp";
			this.copyDataToSpTempTable(where_str.substring(6), gz_tableName);
			userList=this.getUsersFromHistory(where_str.substring(6));
			where_str=where_str.replaceAll("salaryhistory", gz_tableName);
			paramMap.put("where_str", where_str);
		}
		try
		{
			int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
			/**取得需要的计算公式列表*/
			ArrayList formulalist=this.salaryTemplateBo.getFormulaList(1,String.valueOf(salaryid),itemids);
			if(formulalist.size()==0)
				return msg;
			/**把临时变量加到薪资表中去*/
			ArrayList usedMidVarList=this.salaryTemplateBo.getMidVariableListByTable(""+salaryid);  //过滤薪资帐套不用的临时变量
			ArrayList allMidVarList=this.salaryTemplateBo.getMidVarItemList(""+salaryid);   //获得薪资帐套所有的临时变量
			this.salaryTableStructBo.addMidVarIntoGzTable(gz_tableName,usedMidVarList,allMidVarList,paramMap);			
			ArrayList stdTableFieldList=this.salaryTemplateBo.searchStdTableFieldList(salaryid); //标准表指标列表 
			if(stdTableFieldList.size()>0)
			{
				String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
				/**标准表数据初始化*/
				if(where_str.length()==0)
					this.salaryTableStructBo.addStdFieldIntoGzTable(where_str,gz_tableName,stdTableFieldList,dbpres);
				else
					this.salaryTableStructBo.addStdFieldIntoGzTable(where_str.substring(6),gz_tableName,stdTableFieldList,dbpres);
			} 
			if(where_str.length()==0)
				secondComputing(formulalist,where_str,gz_tableName,usedMidVarList,paramMap);
			else
				secondComputing(formulalist,where_str.substring(6),gz_tableName,usedMidVarList,paramMap); 
		
			if(flag==2){
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer sql_str=new StringBuffer("update gz_tax_mx set flag=( ");
				sql_str.append(" select 0 from "+gz_tableName+" where gz_tax_mx.salaryid="+gz_tableName+".salaryid ");
				sql_str.append(" and gz_tax_mx.nbase="+gz_tableName+".nbase and gz_tax_mx.a0100="+gz_tableName+".a0100  ");
				sql_str.append(" and gz_tax_mx.a00z0="+gz_tableName+".a00z0 and gz_tax_mx.a00z1="+gz_tableName+".a00z1 ");
				sql_str.append(" and "+gz_tableName+".sp_flag<>'06') where exists ( ");
				sql_str.append(" select null from "+gz_tableName+" where gz_tax_mx.salaryid="+gz_tableName+".salaryid ");
				sql_str.append(" and gz_tax_mx.nbase="+gz_tableName+".nbase and gz_tax_mx.a0100="+gz_tableName+".a0100  ");
				sql_str.append(" and gz_tax_mx.a00z0="+gz_tableName+".a00z0 and gz_tax_mx.a00z1="+gz_tableName+".a00z1 ");
				sql_str.append(" and "+gz_tableName+".sp_flag<>'06' ");
				sql_str.append(" )");
				dao.update(sql_str.toString());
				
				where_str=where_str.replaceAll(gz_tableName,"salaryhistory");
				/** 同步薪资历史表 和 相对应的临时表 */
				RecordVo vo=new RecordVo("salaryhistory");
				StringBuffer sql=new StringBuffer("");
				/*
				StringBuffer a=new StringBuffer("");
				StringBuffer b=new StringBuffer("");
				StringBuffer c=new StringBuffer(""); //MSSQL
				*/
				for(int i=0;i<formulalist.size();i++)
				{
	                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
	                String fieldname=(String)dbean.get("itemname");
	                /**系统项不用计算*/
	                String item = ",NBASE,A00Z1,A00Z2,A00Z3,B0110,E0122,A0101,";//A00Z0
	                if(item.indexOf(","+fieldname+",")!=-1)
	                	continue;
	                /**分析左边项是否在工资表中存在*/
	                if(!vo.hasAttribute(fieldname.toLowerCase()))
	                	continue;
	                /*  a.append(",salaryhistory."+fieldname);
	                b.append(","+gz_tableName+"."+fieldname);
	                c.append(",salaryhistory."+fieldname+"="+gz_tableName+"."+fieldname);	*/
	                
	                sql.setLength(0);
	                sql.append("update salaryhistory set "+fieldname+"=(");
	                sql.append(" select "+fieldname+" from "+gz_tableName+" where salaryhistory.salaryid="+gz_tableName+".salaryid ");
	                sql.append(" and salaryhistory.nbase="+gz_tableName+".nbase and salaryhistory.a0100="+gz_tableName+".a0100  ");
	                sql.append(" and salaryhistory.a00z0="+gz_tableName+".a00z0 and salaryhistory.a00z1="+gz_tableName+".a00z1 ");
	                sql.append(" ) where exists ( select null from "+gz_tableName+" where salaryhistory.salaryid="+gz_tableName+".salaryid ");
	                sql.append(" and salaryhistory.nbase="+gz_tableName+".nbase and salaryhistory.a0100="+gz_tableName+".a0100  ");
	                sql.append(" and salaryhistory.a00z0="+gz_tableName+".a00z0 and salaryhistory.a00z1="+gz_tableName+".a00z1) and  "+where_str.substring(6));
	                dao.update(sql.toString());
	                
	                for(int j=0;j<userList.size();j++)
	                {
	                	String username=(String)userList.get(j);
	                	String tb_name=username+"_salary_"+this.salaryid;
	                	sql.setLength(0);
	                    sql.append("update "+tb_name+" set "+fieldname+"=(");
	                    sql.append(" select "+fieldname+" from "+gz_tableName+" where ");
	                    sql.append("  "+tb_name+".nbase="+gz_tableName+".nbase and "+tb_name+".a0100="+gz_tableName+".a0100  ");
	                    sql.append(" and "+tb_name+".a00z0="+gz_tableName+".a00z0 and "+tb_name+".a00z1="+gz_tableName+".a00z1 ");
	                    sql.append(" ) where exists (select null from "+gz_tableName+" where ");
	                    sql.append("  "+tb_name+".nbase="+gz_tableName+".nbase and "+tb_name+".a0100="+gz_tableName+".a0100  ");
	                    sql.append(" and "+tb_name+".a00z0="+gz_tableName+".a00z0 and "+tb_name+".a00z1="+gz_tableName+".a00z1 ");
	                    sql.append(" ) ");
	                    dao.update(sql.toString());
	                }
	                
				}
				/* 由于可能存在一个指标依次对应多个公式的情况。所以以一条sql进行更新的方式不可取。zhanghua 2017-3-24
				
				 sql.setLength(0);
				 if(Sql_switcher.searchDbServer()==2) //ORACLE
				 {
		             sql.append("update salaryhistory set ("+a.substring(1)+")=(");
		             sql.append(" select "+b.substring(1)+" from "+gz_tableName+" where salaryhistory.salaryid="+gz_tableName+".salaryid ");
		             sql.append(" and salaryhistory.nbase="+gz_tableName+".nbase and salaryhistory.a0100="+gz_tableName+".a0100  ");
		             sql.append(" and salaryhistory.a00z0="+gz_tableName+".a00z0 and salaryhistory.a00z1="+gz_tableName+".a00z1 ");
		             sql.append(" ) where exists ( select null from "+gz_tableName+" where salaryhistory.salaryid="+gz_tableName+".salaryid ");
		             sql.append(" and salaryhistory.nbase="+gz_tableName+".nbase and salaryhistory.a0100="+gz_tableName+".a0100  ");
		             sql.append(" and salaryhistory.a00z0="+gz_tableName+".a00z0 and salaryhistory.a00z1="+gz_tableName+".a00z1) and  "+where_str.substring(6));
		             dao.update(sql.toString());
					
		             for(int j=0;j<userList.size();j++)
		             {
		                	String username=(String)userList.get(j);
		                	String tb_name=username+"_salary_"+this.salaryid;
		                	sql.setLength(0);
		                    sql.append("update "+tb_name+" set ("+a.substring(1).replaceAll("salaryhistory", tb_name)+")=(");
		                    sql.append(" select "+b.substring(1)+" from "+gz_tableName+" where ");
		                    sql.append("  "+tb_name+".nbase="+gz_tableName+".nbase and "+tb_name+".a0100="+gz_tableName+".a0100  ");
		                    sql.append(" and "+tb_name+".a00z0="+gz_tableName+".a00z0 and "+tb_name+".a00z1="+gz_tableName+".a00z1 ");
		                    sql.append(" ) where exists (select null from "+gz_tableName+" where ");
		                    sql.append("  "+tb_name+".nbase="+gz_tableName+".nbase and "+tb_name+".a0100="+gz_tableName+".a0100  ");
		                    sql.append(" and "+tb_name+".a00z0="+gz_tableName+".a00z0 and "+tb_name+".a00z1="+gz_tableName+".a00z1 ");
		                    sql.append(" ) ");
		                    dao.update(sql.toString());
		              }
				 }
				 else //MSSQL
				 {
					 sql.append("update salaryhistory set "+c.substring(1));
		             sql.append(" from "+gz_tableName+" where salaryhistory.salaryid="+gz_tableName+".salaryid ");
		             sql.append(" and salaryhistory.nbase="+gz_tableName+".nbase and salaryhistory.a0100="+gz_tableName+".a0100  ");
		             sql.append(" and salaryhistory.a00z0="+gz_tableName+".a00z0 and salaryhistory.a00z1="+gz_tableName+".a00z1  and  "+where_str.substring(6));
		             dao.update(sql.toString());
					
		             for(int j=0;j<userList.size();j++)
		             {
		                	String username=(String)userList.get(j);
		                	String tb_name=username+"_salary_"+this.salaryid;
		                	sql.setLength(0);
		                    sql.append("update "+tb_name+" set "+c.substring(1).replaceAll("salaryhistory", tb_name));
		                    sql.append(" from "+gz_tableName+" where ");
		                    sql.append("  "+tb_name+".nbase="+gz_tableName+".nbase and "+tb_name+".a0100="+gz_tableName+".a0100  ");
		                    sql.append(" and "+tb_name+".a00z0="+gz_tableName+".a00z0 and "+tb_name+".a00z1="+gz_tableName+".a00z1 ");
		                    dao.update(sql.toString());
		              }
				 }
				*/
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return msg;
	}
	/**
	 * 
	 * @Title: copyDataToSpTempTable   
	 * @Description:复制审批数据
	 * @param @param strWhere
	 * @param @param tableName 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	private void copyDataToSpTempTable(String strWhere,String tableName)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
		//	if(dbw.isExistTable(tableName, false))
				dbw.dropTable(tableName);
			String sql="";
			if(Sql_switcher.searchDbServer()==2)
				sql="create table "+tableName+" as select * from salaryhistory where "+strWhere;
			else 
				sql="select *  into "+tableName+"  from salaryhistory where "+strWhere;
			dao.update(sql);
			dbw.execute("create index  "+tableName+"_id on "+tableName+" (salaryid,A0100,nbase,a00z0,a00z1)");
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @Title: getUsersFromHistory   
	 * @Description:取的当前处理数据的发起用户
	 * @param @param strWhere
	 * @param @return 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	private ArrayList getUsersFromHistory(String strWhere)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select distinct userflag from salaryhistory where userFlag is not null and "+strWhere);
			while(rowSet.next())
				list.add(rowSet.getString(1));
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 创建薪资变动比对数据，如有数据则生成临时表用于存储相关数据，并返回标识
	 * @param salaryid 薪资帐套Id
	 * @param currym 发放日期
	 * @param currcount 发放次数
	 * @return ADCT    A：增加人员  D：减少人员  C：数据变动人员  T：停发人员
	 */
	public String  executeChangeInfo(int salaryid,String currym,String currcount) throws GeneralException
	{
		String  tableInfo="";
		try
		{
			HashMap paramMap=new HashMap();
			paramMap.put("salaryItemList", this.salaryTemplateBo.getSalaryItemList("",""+salaryid,1));  //薪资项
			paramMap.put("midVariableList", this.salaryTemplateBo.getMidVariableListByTable(""+salaryid)); //薪资类别涉及的临时变量
			paramMap.put("currym",currym);  //薪资发放日期
			paramMap.put("currcount",currcount);  //薪资发放次数
			paramMap.put("salaryid",""+salaryid);   //薪资类别ID
			String username=this.userview.getUserName(); 
			String manager=this.salaryTemplateBo.getManager();   //共享薪资类别的管理员 
			if(manager.length()>0)
				username=manager;
			String gz_tablename=username+"_salary_"+salaryid;    //临时表名  
			paramMap.put("username", username);
			SalaryCtrlParamBo ctrlParamBo=this.salaryTemplateBo.getCtrlparam();
			paramMap.put("ctrlParamBo",ctrlParamBo);  
			//同步薪资表结构
			this.salaryTableStructBo.syncGzTableStruct(paramMap);
			String a01z0Flag=ctrlParamBo.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			/* 生成停发人员信息表 */
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
			{
				String tf_table=createA01Z0ChangeManTable(gz_tablename);
				if("1".equals(tf_table))
					tableInfo+="T";
			}
			HashMap complexWhlMap=new HashMap();
			/*减少人员信息表 */
			String del_table=createDelManTable(complexWhlMap,paramMap);
			if("1".equals(del_table))
				tableInfo+="D";
			/*增加人员信息表 */
			String addMan_table=createAddManTable(2,currym,complexWhlMap);
			if("1".equals(addMan_table))
				tableInfo+="A";
			/*变动人员信息表 */
			String change_table=createChangeInfoManTable(paramMap);
			if("1".equals(change_table))
				tableInfo+="C";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
		
		return tableInfo;
	}
	
	
	

	/**
	 * 把信息变动人员生成一张临时表
	 * 单位、部门及姓名|B0110、E0122及A0101信息变动时，在临时中增加记录
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,B01101,E0122,E01221,A0101,A01011,state
	 * 主键字段：DBNAME,A0100
	 * @return 用户名+BdPeoples
	 */
	public String createChangeInfoManTable(HashMap paramMap) throws GeneralException {
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Bd";
		/**表生成结果*/
		String createResult = "";
		try
		{
				ContentDAO dao=new ContentDAO(this.conn);
				SalaryCtrlParamBo ctrlParamBo=this.salaryTemplateBo.getCtrlparam();
				String username=(String)paramMap.get("username");
				String salaryid=(String)paramMap.get("salaryid");
				String currym=(String)paramMap.get("currym");            //薪资发放日期
				String currcount=(String)paramMap.get("currcount");   //薪资发放次数
				String gz_tablename=username+"_salary_"+salaryid;     //临时表名   
				String manager=this.salaryTemplateBo.getManager();
				
				StringBuffer sb = new StringBuffer(" where 1=1"); 
				if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName()))
					sb.append(this.salaryTemplateBo.getWhlByUnits(gz_tablename,false));  //  按业务范围、操作单位、人员范围控制 
				/**加入自定义的指标*/
				ArrayList f_compare_field=add_del_chg_rightList(Integer.parseInt(salaryid),"3");;
				this.salaryTableStructBo.createChangeInfoTableStruct(tablename,f_compare_field,false);
				String tempTableName="t#"+this.userview.getUserName()+"_gz_Bd2";
				this.salaryTableStructBo.createChangeInfoTableStruct(tempTableName,f_compare_field,true);
				/* 判断薪资发放表是否有记录 */
				if(!this.salaryTemplateBo.hasRecordByTable(gz_tablename, "a0100",""))  
				{
					return "";
				}
				/**格式e5881,e5884,e5880*/
				String rightvalue=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD); 
				String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ","); 
				StringBuffer insert_cloumn_buf = new StringBuffer("dbname,a0100,b01101,e01221,a01011,state");
				StringBuffer select_cloumn_buf= new StringBuffer("nbase as dbname,a0100,b0110,e0122,a0101,'1' as state"); 
				StringBuffer insert=new StringBuffer("dbname,a0000,a0100,b0110,b01101,e0122,e01221,a0101,a01011,state");
				StringBuffer where_buf = new StringBuffer();  //薪资数据与人员库不一致的SQL条件
				where_buf.append("(b0110<>b01101 or (nullif(b0110,'') is null and nullif(b01101,'') is not null) or "
												 + "(nullif(b01101,'') is null and nullif(b0110,'') is not null))");
				where_buf.append(" or ");
				where_buf.append("(e0122<>e01221 or (nullif(e0122,'') is null and nullif(e01221,'') is not null) or "
												 + "(nullif(e01221,'') is null and nullif(e0122,'') is not null))");
				where_buf.append(" or ");
				where_buf.append("(a0101<>a01011 or (nullif(a0101,'') is null and nullif(a01011,'') is not null) or "
												 + "(nullif(a01011,'') is null and nullif(a0101,'') is not null))");
				for(int i=0;i<f_compare_field.size();i++)
				{
					DynaBean dynabean=(DynaBean)f_compare_field.get(i);  
					String itemid=(String)dynabean.get("itemid");
					String itemdesc=(String)dynabean.get("itemdesc");
					String itemtype=(String)dynabean.get("itemtype");
					
					insert_cloumn_buf.append(","+itemid+"1");
					select_cloumn_buf.append(","+itemid);
					insert.append(","+itemid+","+itemid+"1");
					if("N".equalsIgnoreCase(itemtype))
					{
						//20140905 dengcan  增加子集必须有数据才校验条件
						where_buf.append(" or ("+Sql_switcher.isnull(itemid, "0")+"<>"+Sql_switcher.isnull(itemid+"1","0")+" and "+Sql_switcher.isnull(itemid+"0","0")+"=1 )");
					}
					else
					{
						//20140905 dengcan  增加子集必须有数据才校验条件
				    	where_buf.append(" or ( ( "+itemid+"<>"+itemid+"1 or (");
			    		where_buf.append(itemid+" is null and nullif("+itemid+"1,'') is not null) or (");
			    		where_buf.append(itemid+"1 is null and nullif("+itemid+",'') is not null) )  and "+Sql_switcher.isnull(itemid+"0","0")+"=1  )");
					}
					
				}
				
				/**先将工资表中的所有数据导入临时表*/
				StringBuffer insert_sql_buf = new StringBuffer();
				for(int i=0;i<dbarr.length;i++)
	    		{
	         		String pre=dbarr[i];
		    		if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
		    			continue;
		     		
			    	insert_sql_buf.append(" insert into "+tempTableName+"("+insert_cloumn_buf.toString()+") ");
			    	insert_sql_buf.append("select  "+select_cloumn_buf+" from (select * from "+gz_tablename+" "+sb.toString()+"   ");
			    	insert_sql_buf.append(" and UPPER("+gz_tablename+".nbase)='"+pre.toUpperCase()+"' and ");
			    	insert_sql_buf.append(gz_tablename+".A0100 in (select A0100 from "+pre+"a01)");
			    	insert_sql_buf.append(" and  A00Z0=(select MAX(A00Z0) from "+gz_tablename+" a where  UPPER(a.nbase)='"+pre.toUpperCase()+"'  and  a.A0100= "+gz_tablename+".A0100)");
			    	insert_sql_buf.append(" ) b ");
			    	insert_sql_buf.append("where A00Z1=(select MAX(A00Z1) from (select * from "+gz_tablename+" where A00Z0=(select MAX(A00Z0) from "+gz_tablename+" a where  UPPER(a.nbase)='"+pre.toUpperCase()+"'  and  a.A0100= "+gz_tablename+".A0100)) c where b.A0100=c.A0100) ");
			    	dao.update(insert_sql_buf.toString());
			    	insert_sql_buf.setLength(0);
	    		}
				
				HashMap map = getSetItemList(rightvalue,salaryid); //获得自定义指标涉及的子集对象
				if(map.get("A01")==null) //如果没有在薪资类别中设置自定义指标，没有主集指标，自动追加
				{
					map.put("A01",new ArrayList());
				}
				Set keyset = map.keySet();
				//将子集与薪资表的数据更新至临时表中
				for(int i=0;i<dbarr.length;i++)
	    		{
	         		String pre=dbarr[i];
		    		if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
		    			continue;
		    		boolean isMainSet=false;
		    		for(Iterator t=keyset.iterator();t.hasNext();)
		    		{
			    			String key=(String)t.next();
			    			insert_cloumn_buf.setLength(0);
			    			select_cloumn_buf.setLength(0);
			    			insert_sql_buf.setLength(0);
			    			ArrayList itemList = (ArrayList)map.get(key.toUpperCase());
			    			String cloumnStr="";
			    			/**主集，更新单位等信息*/
			    			if("a01".equalsIgnoreCase(key))
			    				isMainSet = true;
		    				if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    				{ 
		    					if("a01".equalsIgnoreCase(key))
		    					{
			    					cloumnStr="a0100,b0100,e0122,a0101";
			    	    			insert_cloumn_buf.append("UPDATE "+tempTableName+" SET a0000=S.a0000,b0110=S.b0110");
			    	    			insert_cloumn_buf.append(",e0122=S.e0122,a0101=S.a0101");
		    					}
		    					else
		    					{
		    						insert_cloumn_buf.append("UPDATE "+tempTableName+" SET ");
		    					} 
		    	    			for(int j=0;j<itemList.size();j++)
		    	    			{
		    	    				FieldItem it=(FieldItem)itemList.get(j);
		    	    				if(j!=0|| "a01".equalsIgnoreCase(key))
		    	    					insert_cloumn_buf.append(",");
		    	    				insert_cloumn_buf.append(it.getItemid()+"=S."+it.getItemid());
		    	    				insert_cloumn_buf.append(","+it.getItemid()+"0=1"); //20140905 dengcan  增加子集必须有数据才校验条件
		    	    				cloumnStr+=","+it.getItemid();
		    	    			}
		    	    			String s_tab="";
		    	    			if("a01".equalsIgnoreCase(key))
		    	    			{
		    	    				s_tab=pre+key;
			    	    			boolean addflag=this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), cloumnStr);
			    	    			if(addflag)
			    	    			{
			    	    				insert_cloumn_buf.append(","+this.salaryTableStructBo.getOnlyField()+"=S."+salaryTableStructBo.getOnlyField());
			    	    			} 
		    	    			}else if(key.toLowerCase().startsWith("k")){// 计算岗位指标 zhanghua 2017-6-19
		    	    				s_tab="( select A0100"+cloumnStr+" from "+key+" inner join "+pre+"A01 on "+key+".E01A1="+pre+"A01.E01A1 ) ";
		    	    			}
		    	    			else
		    	    			{
		    	    				s_tab="(select a.* from "+pre+key+" a,(select  a0100";
		    	    				s_tab+=",max(i9999) as i9999 from "+pre+key+" group by a0100) b ";
		    	    				s_tab+=" where a.a0100=b.a0100 and a.i9999=b.i9999";
		    	    				s_tab+=")";	 
		    	    			} 
		    	    			insert_cloumn_buf.append(" FROM "+tempTableName+" LEFT JOIN "+s_tab+" S ON "+tempTableName+".a0100=S.a0100 WHERE ");
		    	    			insert_cloumn_buf.append(" UPPER("+tempTableName+".dbname)='"+pre.toUpperCase()+"'");
		    				}
		    				else
		    				{
		    					
		    					if("a01".equalsIgnoreCase(key))
		    					{
		    						cloumnStr="a0100,b0100,e0122,a0101";
		    						insert_cloumn_buf.append("UPDATE "+tempTableName+" e set (e.a0000,e.b0110,e.e0122,e.a0101");
		    						select_cloumn_buf.append(" K.a0000,K.b0110,K.e0122,K.a0101");
		    					}
		    					else
		    						insert_cloumn_buf.append("UPDATE "+tempTableName+" e set (");
		    					
		    					for(int j=0;j<itemList.size();j++)
		    	    			{
		    	    				FieldItem it=(FieldItem)itemList.get(j);
		    	    				if(j!=0|| "a01".equalsIgnoreCase(key))
		    	    				{
		    	    					insert_cloumn_buf.append(",");
		    	    					select_cloumn_buf.append(",");
		    	    				}
		    	    				
		    	    				insert_cloumn_buf.append("e."+it.getItemid()+",e."+it.getItemid()+"0"); //20140905 dengcan  增加子集必须有数据才校验条件
		    	    				select_cloumn_buf.append("K."+it.getItemid()+",1");
		    	    				cloumnStr+=","+it.getItemid();
		    	    			}
		    					
		    					String s_tab="";
		    	    			if("a01".equalsIgnoreCase(key))
		    	    			{
		    	    				s_tab=pre+key;
			    					boolean addflag=this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), cloumnStr);
			    					if(addflag)
			    						insert_cloumn_buf.append(",e."+this.salaryTableStructBo.getOnlyField());
		    	    			}
		    					insert_cloumn_buf.append(")=(select "+select_cloumn_buf.toString());
		    					if("a01".equalsIgnoreCase(key))
		    					{
		    						boolean addflag=this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), cloumnStr);
		    						if(addflag)
		    							insert_cloumn_buf.append(",K."+this.salaryTableStructBo.getOnlyField());
		    					}
		    					if(key.toLowerCase().startsWith("k")){
		    						insert_cloumn_buf.append(" from "+key+" k where  e.DBNAME='"+pre+"' ");
		    						insert_cloumn_buf.append(" and exists (");
		    						insert_cloumn_buf.append(" select null from (select E01A1,'"+pre+"' as DBNAME ,p.A0100 from "+pre+"A01 p inner join "+tablename+" t on p.a0100=t.A0100 and t.DBNAME='"+pre+"') a "
											+ "inner join "+key+" on a.E01A1="+key+".E01A1 where e.A0100=a.a0100 and e.DBNAME=a.DBNAME )) ");
		    					}else if(!"b01".equalsIgnoreCase(key)){
		    						insert_cloumn_buf.append(" from "+pre+key+" K where 1=1 ");
		    						if(!"a01".equalsIgnoreCase(key))
		    						{
		    							insert_cloumn_buf.append(" and  K.i9999=(select max(i9999) from "+pre+key+" ");
		    							insert_cloumn_buf.append(" b where K.a0100=b.a0100)  ");
		    						}
		    						
		    						insert_cloumn_buf.append(" and K.a0100=e.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
		    						insert_cloumn_buf.append(" where exists (");
		    						insert_cloumn_buf.append("select null from "+pre+key+" K where 1=1 ");
		    						if(!"a01".equalsIgnoreCase(key))
		    						{
		    							insert_cloumn_buf.append(" and  K.i9999=(select max(i9999) from "+pre+key+" ");
		    							insert_cloumn_buf.append(" b where K.a0100=b.a0100) ");
		    						}
		    						insert_cloumn_buf.append(" and K.a0100=e.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"')"); 
		    					}else{//b01里面的指标单独处理，暂时没考虑其他主集 比如k01 zhaoxg add 2016-10"+key+"
		    						insert_cloumn_buf.append(" from b01 K where 1=1 ");
		    						insert_cloumn_buf.append(" and K.b0110=e.b0110 and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
		    						insert_cloumn_buf.append(" where exists (");
		    						insert_cloumn_buf.append("select null from b01 K where 1=1 ");
		    						insert_cloumn_buf.append(" and K.b0110=e.b0110 and UPPER(e.dbname)='"+pre.toUpperCase()+"')"); 
		    					}
		    			} 
		    			dao.update(insert_cloumn_buf.toString());
		    		}
		    		 
		    		insert_cloumn_buf.setLength(0);
	    			select_cloumn_buf.setLength(0);
	    			insert_sql_buf.setLength(0);
		    		
	    		}
				//将变动数据写入 变动表中
				StringBuffer insertSQL = new StringBuffer();
				boolean addflag=this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), insert.toString());
				if(addflag)
					insert.append(","+this.salaryTableStructBo.getOnlyField());
				insertSQL.append(" insert into "+tablename+"("+insert.toString()+") select "+insert.toString());
				insertSQL.append(" from "+tempTableName+" where ("+where_buf.toString()+")");
				dao.update(insertSQL.toString());
				DbWizard dbw=new DbWizard(this.conn);
				Table table=new Table(tempTableName);
				dbw.dropTable(table);
				// 查看生成的表中是否有数据，有则返回1
				if(dbw.isExistTable(tablename, false))
				{	 
					createResult = isExistRecord(tablename);
				}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return createResult;

	}
	
	
	
	/**
	 * 跟据指标,生成map<setid,list<FieldItem,FieldItem>>
	 * @param rightvalue  指标id   例：a0104,a0107,a0403
	 * @param salaryid   薪资帐套号
	 * @return
	 */
	public HashMap getSetItemList(String rightvalue,String salaryid)
	{
		HashMap map = new HashMap();
		RowSet rs =null;
		try
		{
			if(rightvalue==null||rightvalue.length()==0)
				return map;
			StringBuffer  buf = new StringBuffer(); 
			buf.append("select a.itemid,a.itemdesc,a.itemlength,a.decwidth,a.codesetid,");
			buf.append("a.itemtype,b.fieldsetid from salaryset a left join fielditem b on UPPER(a.itemid)=UPPER(b.itemid) where UPPER(a.itemid) in ('");
			buf.append(rightvalue.toUpperCase().replaceAll(",","','")+"') and a.salaryid=?"); 
			buf.append(" order by b.fieldsetid");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString(),Arrays.asList(new Object[]{Integer.valueOf(salaryid)}));
			while(rs.next())
			{ 
				String fieldsetid = rs.getString("fieldsetid");   
				if(fieldsetid==null && !"e01a1".equalsIgnoreCase(rs.getString("itemid"))){
					continue;
				}
				if("e01a1".equalsIgnoreCase(rs.getString("itemid"))){
					fieldsetid = "A01";
				} 
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(rs.getString("itemid"));
				fielditem.setItemdesc(rs.getString("itemdesc"));
				fielditem.setItemlength(rs.getInt("itemlength"));
				fielditem.setDecimalwidth(rs.getInt("decwidth"));
				fielditem.setCodesetid(rs.getString("codesetid"));
				fielditem.setItemtype(rs.getString("itemtype"));
				fielditem.setFieldsetid(fieldsetid);
				if(map.get(fieldsetid.toUpperCase())==null)
				{
					ArrayList alist = new ArrayList();
					alist.add(fielditem);
					map.put(fieldsetid.toUpperCase(), alist);
				}
				else
				{
					ArrayList alist = (ArrayList)map.get(fieldsetid.toUpperCase());
					alist.add(fielditem);
					map.put(fieldsetid.toUpperCase(), alist);
				}
			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	
	
	
	
	/**
	 * 把得减少人员生成一张临时表
	 * @param complexWhlMap 复杂条件下通过算法分析器生成的人员信息临时表，组成条件语句供复用；
	 * @param paramMap   salaryItemList:薪资项   midVariableList：薪资类别涉及的临时变量  currym：薪资发放日期  currcount：薪资发放次数   username：薪资管理员或当前用户名
	 * @return t#用户名+_gz_Dec
	 */
	public String createDelManTable(HashMap complexWhlMap,HashMap paramMap) throws GeneralException  {
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Dec"; 
		/**表生成结果*/
		String createResult = "";
		try
		{ 
			ArrayList midVariableList=(ArrayList)paramMap.get("midVariableList"); //salaryTemplateBo.getMidVariableListByTable(""+salaryid);  :薪资类别涉及的临时变量
			String username=(String)paramMap.get("username");
			String salaryid=(String)paramMap.get("salaryid");
			String currym=(String)paramMap.get("currym");  //薪资发放日期
			String currcount=(String)paramMap.get("currcount");   //薪资发放次数
			String gz_tablename=username+"_salary_"+salaryid;    //临时表名  
			
			SalaryCtrlParamBo ctrlParamBo=this.salaryTemplateBo.getCtrlparam();
			String manager=this.salaryTemplateBo.getManager();  //共享薪资类别，管理员
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList del_item_list = add_del_chg_rightList(Integer.parseInt(salaryid),"2"); //薪资类别参数设置的减少人员指标项
			this.salaryTableStructBo.createInsDecTableStruct(tablename,del_item_list);   //创建减少人员临时表  
			/**导入数据*/ 
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
			boolean onlyFieldflag=this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), cloumnStr);
			String flag=ctrlParamBo.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String cond=this.salaryTemplateBo.getTemplatevo().getString("cond");
			String cexpr=this.salaryTemplateBo.getTemplatevo().getString("cexpr");
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			DbWizard dbw=new DbWizard(this.conn);
		 
			
			/* 判断薪资发放表是否有记录 */
			if(this.salaryTemplateBo.hasRecordByTable(gz_tablename, "a0100",""))  
			{
					StringBuffer column = new StringBuffer();//减少人员的相关字段信息存入临时表，搜房网  zhaoxg add 2013-11-14
					if(del_item_list.size()>0){
						for(Iterator it=del_item_list.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next(); 
							String itemid=((String)dynabean.get("itemid")).toLowerCase();
							if(cloumnStr.toLowerCase().indexOf(itemid)!=-1){//排除默认项
								continue;
							} 
							column.append(","+itemid+"");
						}
					}
					
					for(int i=0;i<dbarr.length;i++)  // 分库处理
					{
							String pre=dbarr[i]; 
							if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)  //非管理员，没有库权限不能处理
								continue;
							ArrayList dataList=new ArrayList();
							
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tablename);
							buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE"+column);
							buf.append(")"); 
							buf.append(" select  ");//distinct
							buf.append("NBASE,A0100,A0000,B0110,E0122,A0101,'1' as STATE "+column);
							buf.append(" from ");
							buf.append("(select * from (select * from "+gz_tablename+" where  nbase=?  and A00Z0=(select MAX(A00Z0) from "+gz_tablename+" a where a.A0100= "+gz_tablename+".A0100  and a.nbase=? )   ) b ");
							buf.append("where A00Z1=(select MAX(A00Z1) from (select * from "+gz_tablename+" where nbase=?   and  A00Z0=(select MAX(A00Z0) from "+gz_tablename+" a where a.A0100= "+gz_tablename+".A0100   and a.nbase=?  )    ) c where b.A0100=c.A0100)) ");
							buf.append(gz_tablename);
							buf.append(" where 1=1 ");
							if (StringUtils.isNotBlank(manager) && !this.userview.getUserName().equalsIgnoreCase(manager)) {
								buf.append(this.salaryTemplateBo.getWhlByUnits(gz_tablename, false));
							}
							buf.append(" and (not exists (select null ");
							
							dataList.add(pre);
							dataList.add(pre);
							dataList.add(pre);
							dataList.add(pre);
							
							String aflag=ctrlParamBo.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
							if((manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())) || (aflag!=null&& "1".equals(aflag)))
							{
										 buf.append(" from "+pre+"A01 where 1=1 "+this.salaryTemplateBo.getWhlByUnits(pre+"A01",false));  //  按业务范围、操作单位、人员范围控制 
							}
							/*else if(aflag!=null&&aflag.equals("1"))  // 人员范围权限过滤标志  1：有  
							{
											buf.append(this.userview.getPrivSQLExpression(pre, false));	
							}*/else{
								buf.append(" from "+pre+"A01 where 1=1 ");
							}
							buf.append(" and "+gz_tablename+".a0100="+pre+"A01.a0100");
							buf.append(" )  ");
							
							
							
							if("0".equals(flag)&&cond.length()>0)  //0：简单条件
							{
								FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");
								String strSql = factor.getSqlExpression(); 
								if(strSql.toLowerCase().indexOf("where")==-1)
									buf.append(" or     not exists   (select null "+strSql+" where "+pre+"a01.a0100="+gz_tablename+".a0100  )   ");
								else
									buf.append(" or    not exists   (select null "+strSql+" and "+pre+"a01.a0100="+gz_tablename+".a0100  )   ");
								
							}
							else if("1".equals(flag)&&cond.length()>0)  // 1：复杂条件
							{
								
								int infoGroup = 0; // forPerson 人员
								int varType = 8; // logic								
								String whereIN=""; 
								alUsedFields.addAll(midVariableList);
								YksjParser yp = new YksjParser(this.userview ,alUsedFields,
										YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
								YearMonthCount ymc=null;	 
								currym = currym.length()==7?currym+"-01":currym;
								cond=cond.replaceAll(ResourceFactory.getProperty("gz.columns.a00z0")+"\\(\\)","#"+currym+"#");
								cond=cond.replaceAll(ResourceFactory.getProperty("gz.columns.a00z0"),"#"+currym+"#"); 
								yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
								if(yp.isFError())
									throw GeneralExceptionHandler.Handle(new Throwable(yp.getStrError()));
								String tempTableName = yp.getTempTableName();
								String w = yp.getSQL();
								if(w!=null&&w.trim().length()>0)
								{
									String _tempName="t#"+this.userview.getUserName()+"_gz_"+pre.toLowerCase()+"_cond"; 
									complexWhlMap.put(pre.toLowerCase()+"_whl",w.replaceAll(tempTableName, _tempName));//2014-08-12 dengcan
									
								//	if (dbw.isExistTable(_tempName, false)) 
										dbw.dropTable(_tempName); 
									if(Sql_switcher.searchDbServer()==2)
										dao.update("create table "+_tempName+" as select * from "+tempTableName);
									else 
										dao.update("select * into "+_tempName+" from "+tempTableName); 
									buf.append(" or    not exists   (select null  from "+tempTableName+" where ("+w+") and "+tempTableName+".a0100="+gz_tablename+".a0100  )  ");
								}
							}
							buf.append(" ) ");
							dao.update(buf.toString(),dataList);
					}//for i loop end.
				
				//将不在薪资类别定义中的人员库范围数据插入减少人员表中
				insertDecFromBase(gz_tablename, dao, dbarr,tablename,column.toString()); 
				/**薪资类别定义的条件，删除不在条件范围中的人员*/
				// delNoConditionData(tablename);
				
				// 报批的数据不能出现在减少人员面板里
				String flow_flag=ctrlParamBo.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");			
				if(flow_flag!=null&& "1".equalsIgnoreCase(flow_flag))  //需要审批
				{
					buf.setLength(0);
					buf.append("delete from "+tablename+" where not exists (select null from "+gz_tablename);
					buf.append("  where   "+tablename+".a0100="+gz_tablename+".a0100 and lower("+tablename+".dbname)=lower("+gz_tablename+".nbase) ");
					buf.append(" and  ( sp_flag='01' or sp_flag='07' or sp_flag='06' ) ");
					buf.append(")");
					dao.update(buf.toString());
				}
				/**导入唯一性指标的值*/
				if(onlyFieldflag)
				{
					updateOnlyValue(tablename,dao, dbw);
				}
				
			} 
			// 查看生成的表中是否有数据，有则返回1
			if(dbw.isExistTable(tablename, false))
			{	 
				createResult = isExistRecord(tablename);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(ex);
		} 
		return createResult;
	}
	
	/**
	 * 更新新增人员（减少人员）临时表里的唯一健值
	 * @param tablename  临时表名
	 * @param dao
	 * @param dbw
	 */
	private void updateOnlyValue(String tablename,ContentDAO dao,DbWizard dbw)
	{
		RowSet rs =null;
		try
		{
			String asql = " select distinct DBNAME from "+tablename;
			rs = dao.search(asql);
			StringBuffer abuf = new StringBuffer("");
			while(rs.next())
			{
				abuf.append(rs.getString("DBNAME")+",");
			} 
			String[] arr=abuf.toString().split(",");
			StringBuffer sql_buf=new StringBuffer("");
			for(int j=0;j<arr.length;j++)
			{
				if(arr[j]==null|| "".equals(arr[j]))
					continue;
				if(!dbw.isExistTable(arr[j]+"A01",false))
					continue;
				sql_buf.setLength(0);
				sql_buf.append(" update "+tablename+" set ");
				sql_buf.append(this.salaryTableStructBo.getOnlyField()+"=(select "+this.salaryTableStructBo.getOnlyField());
				sql_buf.append(" from "+arr[j]+"A01 where "+tablename+".a0100="+arr[j]+"a01.a0100 and ");
				sql_buf.append("UPPER("+tablename+".DBNAME)='"+arr[j].toUpperCase()+"') where exists(");
				sql_buf.append(" select null from "+arr[j]+"a01 where "+tablename+".a0100="+arr[j]+"a01.a0100 and ");
				sql_buf.append("UPPER("+tablename+".dbname)='"+arr[j].toUpperCase()+"')");
				dao.update(sql_buf.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rs);
		}
	}
	
	
	
	/**
	 * 工资报批
	 * @param appealObject  报批人
	 * @param busiDate 业务日期 次数   year:2010  month:03   count:1
	 * @throws GeneralException
	 */
	public void gzDataAppeal(String appealObject,	LazyDynaBean	busiDate)throws GeneralException
	{ 
		RowSet rowSet=null;
		try
		{
			String strYm=(String)busiDate.get("year")+"-"+(((String)busiDate.get("month")).length()==1?"0"+(String)busiDate.get("month"):(String)busiDate.get("month"));//如果月份是个位数 前面加个0
			String strC=(String)busiDate.get("count");
			ContentDAO dao=new ContentDAO(this.conn);
			String tableName=this.salaryTemplateBo.getGz_tablename();  //临时表名
			String manager=this.salaryTemplateBo.getManager();             //共享帐套管理员
			String filterWhl=this.salaryTemplateBo.getfilter(tableName);  //筛选条件 获取表格工具过滤以及页面模糊查询返回的sql片段
			String salaryTemplateName=(String)this.salaryTemplateBo.getTemplatevo().getString("cname"); //薪资帐套名称
			String name=busiDate.get("year")+ResourceFactory.getProperty("datestyle.year")+busiDate.get("month")+ResourceFactory.getProperty("datestyle.month")+busiDate.get("count")+ResourceFactory.getProperty("hmuster.label.count")+"  "+salaryTemplateName;//待办名  “2014年06月1次 月度奖金（薪资）”			
			//判断临时表人员的归属年月次数在历史表相同帐套下是否有相同记录(别人发的) ,如相同需将归属次数自动加1,再将临时表数据导入到历史表中,防止主健冲突
			DbNameBo.autoAppeal(this.conn, this.userview, tableName, salaryid+"",manager, appealObject,filterWhl ,this.salaryTemplateBo.getSalaryFieldStr());
			//			更改 工资数据表中的审批状态 
			String sql0="update "+tableName+" set Sp_flag='02' where ( sp_flag='07' or sp_flag='01') ";
			sql0+=" and exists (select null from salaryhistory where salaryid="+this.salaryid+"  and  salaryhistory.a0100="+tableName+".a0100 ";
			sql0+=" and  upper(salaryhistory.nbase)=upper("+tableName+".nbase) and  salaryhistory.a00z0="+tableName+".a00z0 and  salaryhistory.a00z1="+tableName+".a00z1  ) ";
			if(filterWhl.length()>0)
				sql0+=filterWhl; 
			dao.update(sql0);
			
			String username=this.userview.getUserName();
			if(manager!=null&&manager.length()>0)
				username=manager;
			//将工资发放记录表对应的纪录改为 执行中状态 
			setExtendLogState("05",username,strYm,strC);  
			
			//发送 邮件 和 短信通知
			if(this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE)!=null&&this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE).length()>0)
			{
				sendMessage(appealObject,"",ResourceFactory.getProperty("gz_new.gz_accounting.gzappeal"),false);
			}
			busiDate.set("day","01");
			busiDate.set("a00z2",busiDate.get("year")+"."+busiDate.get("month")+".01"); 
			busiDate.set("name", salaryTemplateName);
			busiDate.set("cstate", "1".equals((String)this.salaryTemplateBo.getTemplatevo().getString("cstate"))?"1":"0");
			handlePendingTask(tableName,busiDate,appealObject,name);
		}
		catch(Exception ex)
		{
			ex.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
	}

    /**
     * 处理薪资发放待办任务
     * @param tablename  薪资临时表名
     * @param busiDate    业务日期 次数
     * @param approveObject  报给谁
     * @param name  待办名称
     */
    private void handlePendingTask(String tablename,LazyDynaBean	busiDate,String approveObject,String name)
    {
        busiDate.set("viewtype","0");//页面区分 0薪资发放 1薪资审批
        String strYm=(String)busiDate.get("year")+"-"+(String)busiDate.get("month");
        String strC=(String)busiDate.get("count");
        busiDate.set("sql", "select count(a00z1) from "+tablename+" where "+Sql_switcher.dateToChar("A00Z2","yyyy-MM")+"='"+strYm+"' and A00Z3='"+strC+"' and (sp_flag='01' or sp_flag='07')");
        LazyDynaBean _bean=updatePendingTask(this.conn, this.userview, approveObject,""+this.salaryid,busiDate,"1");
        PendingTask pt = new PendingTask();
        if("add".equals(_bean.get("flag"))){
            pt.insertPending("G"+_bean.get("pending_id"),"G",name,this.userview.getUserName(),approveObject,(String)_bean.get("url"), 0, 1, ResourceFactory.getProperty("system.options.itemsalary"), this.userview);

        }else if("update".equals(_bean.get("flag"))){
            pt.updatePending("G", "G"+_bean.get("pending_id"), 0, ResourceFactory.getProperty("system.options.itemsalary"), this.userview);
        }
        if("update".equals(_bean.get("selfflag"))){
            pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, ResourceFactory.getProperty("system.options.itemsalary"), this.userview);
        }
    }


    /**
     * 同步推送待办表   zhaoxg add 2014-7-25
     * @param conn
     * @param userview
     * @param receiver 接收者名
     * @param salaryid
     * @param flag    1:报批  2：驳回  3：批准  4：阅读 5：删除驳回到发起人的代办
     * @param bean   发放日期、次数   待办模块 名称 等信息 viewtype页面区分 0薪资发放，1或者null 薪资审批
     */
    public LazyDynaBean updatePendingTask(Connection conn,UserView userview,String receiver,String salaryid,LazyDynaBean bean,String flag) {
        LazyDynaBean _bean=new LazyDynaBean();
        if (receiver == null || receiver.length() ==0)
            return _bean;

        RowSet rs = null;
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            String date = df.format(calendar.getTime());
            String viewtype="1";//页面区分 0 薪资发放 1薪资审批
            if(bean.get("viewtype")!=null&& "0".equals((String)bean.get("viewtype")))
                viewtype="0";
            String sender = userview.getUserName();

            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql=new StringBuffer();
            String name=bean.get("year")+ResourceFactory.getProperty("datestyle.year")+bean.get("month")+ResourceFactory.getProperty("datestyle.month")+ResourceFactory.getProperty("label.query.NO")+bean.get("count")+ResourceFactory.getProperty("hmuster.label.count")+"  "+bean.get("name");//待办名  “2014年06月第1次 月度奖金（薪资）”
            String ext_flag="GZSP_"+bean.get("year")+bean.get("month")+bean.get("count")+"_"+salaryid;

            HashMap<String,HashMap<String,String>> pendingtaskMap=isHavePendingtask(receiver,conn,ext_flag,viewtype,flag);
            HashMap<String,String> map=null;
            HashMap<String,String> _map=null;
            if(pendingtaskMap.containsKey("finish"))
                _map=pendingtaskMap.get("finish");
            if(pendingtaskMap.containsKey("send"))
                map=pendingtaskMap.get("send");
            String pending_type="";
            String str2="";

            pending_type= "0".equals(bean.get("cstate"))?"34":"39";
            if("0".equals(bean.get("cstate"))){
                str2="&imodule="+PubFunc.encrypt("0")+"";
            }else{
                str2="&imodule="+PubFunc.encrypt("1")+"";
            }

            String tempreceiver=receiver;
            if(receiver.indexOf("#")!=-1){
                String[] _receiver=receiver.split("#");
                tempreceiver=_receiver[0];
            }

            //如果是驳回再报，那么自己肯定也有条对应待办   改成已办
            //存在自己报批给自己的情况 ，所以先判断是否有需改状态的代办，之后再新增。否则会错误的将新增的代办改为已办 zhanghua 2017-4-21
//			HashMap _map=isHavePendingtask(sender,conn,ext_flag,viewtype,flag);
            if(_map!=null&&"0".equals(_map.get("pending_status"))&&bean.get("sql")!=null&&bean.get("sql").toString().length()>0&&!"4".equals(flag)){
                int num=1;
                String _sql=(String) bean.get("sql");
                if("null".equalsIgnoreCase(_sql)){//删除代办时特殊标志
                    num=0;
                }else {
                    RowSet _rs = dao.search(_sql);
                    if (_rs.next())
                        num = _rs.getInt(1);
                }
                if(num==0)
                {
                    sql.delete(0, sql.length());
                    sql.append("update t_hr_pendingtask set Pending_status='1',Lasttime="+Sql_switcher.dateValue(date)+"");
                    sql.append(" where Pending_type='"+pending_type+"'");
                    sql.append(" and Receiver='" + sender + "'");
                    sql.append(" and Pending_status='0' and pending_id="+_map.get("pending_id")+"");
                    dao.update(sql.toString());
                    _bean.set("selfpending_id", _map.get("pending_id"));
                    _bean.set("selfflag", "update");
                }
            }else if("4".equals(flag)&&_map!=null&&"0".equals(_map.get("pending_status"))){//改成已阅
                sql.delete(0, sql.length());
                if("0".equals(_map.get("bread"))){
                    sql.append("update t_hr_pendingtask set bread='1',Lasttime="+Sql_switcher.dateValue(date)+" where ");
                    sql.append(" Pending_status='0' and pending_id="+_map.get("pending_id")+"");
                    dao.update(sql.toString());
                    _bean.set("pending_id", _map.get("pending_id"));
                    _bean.set("flag", "update");
                }else{
                    _bean.set("flag", "xxxx");//如果是已阅就不调外部接口
                }
            }


            //若自己报批给自己 驳回时出现发起人和收件人是同一人情况无法通过map==null判断 所以加入map.equals(_map) 若一样则新增代办 zhanghua
            if ((map==null||map.size()==0||map.equals(_map))&&("1".equals(flag)|| "2".equals(flag))) {//在待办任务表中新增待办数据
                IDGenerator idg = new IDGenerator(2, conn);
                String url="";
                SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(conn,Integer.parseInt(salaryid));
                if("1".equals(flag)){
                    url = "/module/utils/jsp.do?br_query=link&param="+SafeCode.encode("/module/gz/salaryspcollect/SalarySpCollect.html?salaryid="+PubFunc.encrypt(salaryid)+"&appdate="+PubFunc.encrypt((String)bean.get("a00z2"))+"&count="+PubFunc.encrypt((String)bean.get("count"))+str2+"&viewtype="+PubFunc.encrypt("1")+"&currentPage=1&returnflag=portal")+"";
//					url="/gz/gz_accounting/gz_sp_orgtree.do?br_newsalary=link&fromModel=wdxx&a00z2="+bean.get("a00z2")+"&zjjt=1&a00z3="+bean.get("count")+"&ori=1&salaryid="+salaryid;
                }else if("2".equals(flag)){
                    String[] _receiver=receiver.split("#");
                    if(_receiver[0].equals(_receiver[1])){//驳回到发起人了
                        url = "/module/utils/jsp.do?br_query=link&param="+SafeCode.encode("/module/gz/salaryaccounting/SalaryAccounting.html?salaryid="+PubFunc.encrypt(salaryid)+"&appdate="+PubFunc.encrypt((String)bean.get("a00z2"))+"&count="+PubFunc.encrypt((String)bean.get("count"))+str2+"&viewtype="+PubFunc.encrypt("0")+"&currentPage=1&returnflag=portal")+"";
//						url="/gz/gz_accounting/gz_org_tree.do?br_newsalary=link&ff_bosdate="+bean.get("a00z2")+"&zjjt=1&ff_count="+bean.get("count")+str2+"&salaryid="+salaryid;
                    }else{
                        url = "/module/utils/jsp.do?br_query=link&param="+SafeCode.encode("/module/gz/salaryspcollect/SalarySpCollect.html?salaryid="+PubFunc.encrypt(salaryid)+"&appdate="+PubFunc.encrypt((String)bean.get("a00z2"))+"&count="+PubFunc.encrypt((String)bean.get("count"))+str2+"&viewtype="+PubFunc.encrypt("1")+"&currentPage=1&returnflag=portal")+"";
//						url="/gz/gz_accounting/gz_sp_orgtree.do?br_newsalary=link&fromModel=wdxx&a00z2="+bean.get("a00z2")+"&zjjt=1&a00z3="+bean.get("count")+"&ori=1&salaryid="+salaryid;
                    }
                    receiver=_receiver[0];
                }
                String pending_id = idg.getId("pengdingTask.pengding_id");
                RecordVo vo = new RecordVo("t_hr_pendingtask");
                vo.setString("pending_id", pending_id);
                vo.setDate("create_time", date);
                vo.setDate("lasttime", date);
                vo.setString("sender", sender);
                vo.setString("pending_type", pending_type);
                vo.setString("pending_title",name);
                vo.setString("pending_url", url);
                vo.setString("pending_status", "0");
                vo.setString("pending_level", "1");
                vo.setInt("bread", 0);
                vo.setString("receiver", receiver);
                vo.setString("ext_flag", ext_flag);
                dao.addValueObject(vo);
                _bean.set("receiver", receiver);
                _bean.set("pending_id", pending_id.replaceAll("^(0+)", ""));//去掉前面的0，因为入库以后是int型的
                _bean.set("flag", "add");
                _bean.set("url", url);
            } else if (map!=null&&(tempreceiver.equals(map.get("receiver")))&&("1".equals(flag)|| "2".equals(flag))) {//在待办任务表中存在对应的待办数据但状态不是待办

                sql.delete(0, sql.length());
                sql.append("update t_hr_pendingtask set Pending_status='0',bread='0',Lasttime="+Sql_switcher.dateValue(date)+",pending_title='"+name+"',");
                sql.append(" sender='" + sender + "'");
                sql.append(" where Pending_type='"+pending_type+"'");
                sql.append(" and Receiver='" + tempreceiver + "'");
                sql.append(" and pending_id="+map.get("pending_id")+"");
                dao.update(sql.toString());
                _bean.set("pending_id", map.get("pending_id"));
                _bean.set("flag", "update");
            }




        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return _bean;
    }

    /**
     * 判断是否已有待办，且返回待办状态   zhaoxg add 2014-7-25
     * @param receiver 接收者名
     * @param conn
     * @param ext_flag  扩展标记  用来区分已有待办
     * @param viewtype 页面区分 0薪资发放，1薪资审批
     * @param flag    1:报批  2：驳回  3：批准  4：阅读
     * @return
     */
    private HashMap<String,HashMap<String,String>> isHavePendingtask(String receiver,Connection conn,String ext_flag,String viewtype,String flag){
        HashMap<String,HashMap<String,String>> map=new HashMap<String,HashMap<String,String>>();
        RowSet rs=null;
        try{
            String _receiver="";
            if(receiver.indexOf("#")!=-1){
                _receiver=receiver.split("#")[0];
            }else
                _receiver=receiver;
            String sender=userview.getUserName();
            String withNoLock="";
            if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
                withNoLock=" WITH(NOLOCK) ";
            ContentDAO dao = new ContentDAO(conn);
            String sql="select pending_id,ext_flag,pending_status,bread,receiver,sender,CASE WHEN LOWER(pending_url) LIKE '%salaryaccounting%' THEN 0 ELSE 1 END AS viewtype " +
                    "from t_hr_pendingtask "+withNoLock+" where (Pending_type='34' or Pending_type='39') and (upper(receiver) in ('"+_receiver.toUpperCase()+"','"+sender.toUpperCase()+"') or upper(sender) in ('"+_receiver.toUpperCase()+"','"+sender.toUpperCase()+"')) and pending_status<>1 and ext_flag='"+ext_flag+"'";
            rs=dao.search(sql);
            HashMap<String,String> tempMap=null;
            while(rs.next()){
                int rsViewType=rs.getInt("viewtype");//页面区分 0薪资发放，1薪资审批
                String rsReceiver=rs.getString("receiver");//接收人
                String rsSender=rs.getString("sender");//发件人
                if("1".equals(flag)){//报批
                    if("0".equals(viewtype)){// 发放页面报批
                        //找发放页面接受人是自己的 结束掉。审批页面接受人是接受人的新增
                        if(rsViewType==0&&rsReceiver.equalsIgnoreCase(sender)){
                            tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                            map.put("finish",tempMap);
                        }else if(rsViewType==1&&rsReceiver.equalsIgnoreCase(_receiver)){
                            tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                            map.put("send",tempMap);
                        }
                    }else{//审批页面报批
                        //找审批页面 接受人是自己的 结束掉 ，审批页面接受人是接受人的新增
                        if(rsViewType==1&&rsReceiver.equalsIgnoreCase(sender)){
                            tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                            map.put("finish",tempMap);
                        }else if(rsViewType==1&&rsReceiver.equalsIgnoreCase(_receiver)){
                            tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                            map.put("send",tempMap);
                        }
                    }

                }else if("2".equals(flag)){//驳回
                    //找审批页面 接受人是自己的 结束掉 如果驳回到发起人了 那么找发放页面接受人是接受人的新增 否则找审批页面接受人是接受人的新增
                    String[] treceiver=receiver.split("#");
                    if(rsViewType==1&&rsReceiver.equalsIgnoreCase(sender)){
                        tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                        map.put("finish",tempMap);
                    }
                    if(treceiver[1].equalsIgnoreCase(treceiver[0])){
                        if(rsViewType==0&&rsReceiver.equalsIgnoreCase(_receiver)){
                            tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                            map.put("send",tempMap);
                        }
                    }else if(rsViewType==1&&rsReceiver.equalsIgnoreCase(_receiver)){
                        tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                        map.put("send",tempMap);
                    }
                }else if("3".equals(flag)|| "4".equals(flag)) {//批准 阅读
                    //找审批页面接受人是自己的结束掉
                    if(rsViewType==1&&rsReceiver.equalsIgnoreCase(sender)){
                        tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
                        map.put("finish",tempMap);
                    }
                }else if("5".equals(flag)){//删除驳回到发起人的代办
					//找薪资发放页面接受人是自己的结束掉
					if(rsViewType==0&&rsReceiver.equalsIgnoreCase(sender)){
						tempMap=getPendingtashMap(rs.getString("pending_status"),rs.getString("bread"),rsReceiver,rs.getString("pending_id"));
						map.put("finish",tempMap);
					}
				}
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
     * 根据传入条件拼接map
     * @param pending_status
     * @param bread
     * @param receiver
     * @param pending_id
     * @return
     */
    private HashMap<String,String> getPendingtashMap(String pending_status,String bread,String receiver,String pending_id){
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("pending_status", pending_status);//待办处理状态
        map.put("bread", bread);//是否阅读
        map.put("receiver",receiver);//接收者账号
        map.put("pending_id",pending_id);//主键
        return map;
    }
	
	
	
	/**
	 * 给当前审批人发送邮件及短信
	 * isUserflag 是都是给发起人
	 * @throws GeneralException
	 */
	private void sendMessage(String appealObject,String content,String title,boolean isUserflag)throws GeneralException
	{
		try
		{
			RecordVo templatevo=this.salaryTemplateBo.getTemplatevo();
			SalaryCtrlParamBo	ctrlparam=this.salaryTemplateBo.getCtrlparam();
			if(templatevo!=null&&templatevo.getString("cstate")!=null&& "1".equals(templatevo.getString("cstate")))
				title=title.replaceAll(ResourceFactory.getProperty("gz.report.salary"),ResourceFactory.getProperty("gz.report.welfare"));
			ContentDAO dao=new ContentDAO(this.conn);			
//			消息内容
			/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 start */
			if(appealObject.indexOf(",")==-1&&ctrlparam.getValue(SalaryCtrlParamBo.NOTE) != null && ctrlparam.getValue(SalaryCtrlParamBo.NOTE).trim().length() > 0)
			{
				RowSet rowSet=dao.search("select subject,content from email_name where id="+ctrlparam.getValue(SalaryCtrlParamBo.NOTE));
				/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 end */
				SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd");
				Date d=new Date();
				if(rowSet.next())
				{
					content=content.replaceAll("＃", "#");
					content=Sql_switcher.readMemo(rowSet,"content");
					
					/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 start */
					String tempTitle = rowSet.getString("subject");
					if(tempTitle != null && tempTitle.trim().length()>0) {
						title = tempTitle;
					}
					/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 end */
					
					content=content.replaceAll("#"+ResourceFactory.getProperty("label.mail.username")+"#",getFullName(appealObject,conn));//this.userview.getUserFullName()   这个名字写反了   zhaoxg add 2014-8-8 //#用户名#
					content=content.replaceAll("#"+ResourceFactory.getProperty("label.query.day")+"#",f.format(d)); //#日期#
					f=new SimpleDateFormat("hh:mm");
					content=content.replaceAll("#"+ResourceFactory.getProperty("report.parse.t")+"#",f.format(d)); //#时间#
					String privCode=userview.getManagePrivCode();
					String privCodeValue=userview.getManagePrivCodeValue();
					if(privCodeValue!=null&&privCodeValue.trim().length()>0)
					{
						if("UN".equalsIgnoreCase(privCode))
						{
							content=content.replaceAll("#"+ResourceFactory.getProperty("hrms.b0110")+"#",AdminCode.getCodeName("UN", privCodeValue));  //#单位名称#
							content=content.replaceAll("#"+ResourceFactory.getProperty("hrms.e0122")+"#","");  //#部门名称#
						}
						if("UM".equalsIgnoreCase(privCode))
						{
							content=content.replaceAll("#"+ResourceFactory.getProperty("hrms.e0122")+"#",AdminCode.getCodeName("UM", privCodeValue));
							if(content.indexOf("#"+ResourceFactory.getProperty("hrms.b0110")+"#")!=-1)
								content=content.replaceAll("#"+ResourceFactory.getProperty("hrms.b0110")+"#",this.salaryTemplateBo.getUnByUm(privCodeValue));
							
						}
					}else{
						content=content.replaceAll("#"+ResourceFactory.getProperty("hrms.b0110")+"#","");
						content=content.replaceAll("#"+ResourceFactory.getProperty("hrms.e0122")+"#","");
					}
					content=content.replaceAll("#"+ResourceFactory.getProperty("label.gz.name")+"#",this.salaryTemplateBo.getTemplatevo().getString("cname"));
				}
				rowSet.close();
				
			}
			
			String[] receiveMens=null;
			if(appealObject.indexOf(",")!=-1)
			{
				receiveMens=appealObject.split(",");
				String name=this.userview.getUserFullName();
				if(name==null||name.trim().length()==0)
					name=this.userview.getUserName();
				
			}
			else
			{
				receiveMens=new String[1];
				receiveMens[0]=appealObject;
			}
				                       
			for(int i=0;i<receiveMens.length;i++)
			{
				
				if(receiveMens[i]==null||receiveMens[i].trim().length()==0)
					continue;
				appealObject=receiveMens[i];
				String email="";
				String phone="";
				RecordVo vo=new RecordVo("operuser");
				vo.setString("username",appealObject);
				vo=dao.findByPrimaryKey(vo);
				String dbase=vo.getString("nbase");
				String a0100=vo.getString("a0100");
				String password=vo.getString("password");
				
				if(!StringUtils.isBlank(a0100))
				{
					RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
					String email_field=avo.getString("str_value");
					String field_name=this.salaryTemplateBo.getMobileNumber();
					String sql= "select "+email_field;
					if(email_field!=null&&email_field.length()>0&&field_name.length()>0)
						sql+=",";
					if(field_name.length()>0)	
						sql+=field_name;
					sql+=" from "+dbase+"A01 where a0100='"+a0100+"'";
					if((email_field!=null&&email_field.length()>0)||field_name.length()>0)
					{
						RowSet rset=dao.search(sql);
						if(rset.next())
						{
							if(email_field!=null&&email_field.length()>0)							
								email=rset.getString(email_field);
							if(field_name!=null&&field_name.length()>0)
								phone=rset.getString(field_name);
						}
						PubFunc.closeDbObj(rset);
					}
					
				}
				
				if(StringUtils.isBlank(email)) {
					email=vo.getString("email");
				}
				
				if(StringUtils.isBlank(phone)) {
					phone=vo.getString("phone");
				}
				
				
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")))
				{
					if(phone!=null&&phone.length()>0)
					{
						if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
						{
							NoteCheckSend sendBo=new NoteCheckSend();
							sendBo.sendMesg(phone,content);
						}
						else
						{
							SmsBo smsbo=new SmsBo(this.conn);
							smsbo.sendMessage2(this.userview.getUserFullName()!=null?this.userview.getUserFullName():this.userview.getUserName(),vo.getString("fullname")!=null?vo.getString("fullname"):vo.getString("username"),phone,content);
						}
					}
				}
				
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")))
				{
					if(email!=null&&email.length()>0)
					{
					//	EMailBo bo = new EMailBo(this.conn,true,"");
						AsyncEmailBo emailbo = new AsyncEmailBo(conn, userview); 
						content=content.replaceAll("\r\n","<br>");
						String weixincontent = content;
						
						//#自动登录连接#
						String url = "";
						if(isUserflag == true)//true说明是给发起人，那么到发放界面
							url="<a href='"+ this.userview.getServerurl() +"/module/utils/jsp.do?br_query=link&param="+SafeCode.encode("/module/gz/salaryaccounting/SalaryAccounting.html?salaryid="+PubFunc.encrypt(this.salaryid+"")+"&currentPage=1&returnflag=portal")+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(appealObject+","+password))+"'>自动登录链接</a>";
						else
							url="<a href='"+ this.userview.getServerurl() +"/module/utils/jsp.do?br_query=link&param="+SafeCode.encode("/module/gz/salaryspcollect/SalarySpCollect.html?salaryid="+PubFunc.encrypt(this.salaryid+"")+"&currentPage=1&returnflag=portal")+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(appealObject+","+password))+"'>自动登录链接</a>";
						//String url="<a href='"+ this.userview.getServerurl() +"/gz/gz_accounting/gz_sp_orgtree.do?br_newsalary=link&fromModel=wdxx&ori=0&zjjt=1&salaryid="+this.salaryid+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(appealObject+","+password))+"' >自动登录连接</a>";
						content=content.replaceAll("#"+ResourceFactory.getProperty("label.gz.autologonaddress")+"#",url);
						weixincontent = weixincontent.replaceAll("#"+ResourceFactory.getProperty("label.gz.autologonaddress")+"#","");
						//content+="<br>"+url;
						
						LazyDynaBean emails = new LazyDynaBean(); 
						emails.set("subject", title);
						emails.set("bodyText", content);
						emails.set("toAddr", email);
						emailbo.send(emails);
						
						String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
						if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
							//String username ="as"; // this.getZizhuUsername(appealObject);
							String username = getZizhuUsername(dbase, appealObject);
							WeiXinBo.sendMsgToPerson(username, title, weixincontent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
						}
						String dd_corpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
						if(dd_corpid!=null&&dd_corpid.length()>0){//推送钉钉  dengcan add 2017-6-1
							//String username ="as"; // this.getZizhuUsername(appealObject);
							String username = getZizhuUsername(dbase, appealObject);
							DTalkBo.sendMessage(username, title, weixincontent, "", "");
						}
					}

				}
				
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 有fullname优先fullname 其次是a0101 最后才是usrname
	 * @param usrname 用户名
	 * @param con  数据库连接
	 * @return
	 * @throws GeneralException
	 */
	private String getFullName(String username,Connection con) throws GeneralException{
		String name="";
		RowSet rs=null;
		RowSet rs1=null;
		try{
			ContentDAO dao = new ContentDAO(con);			
			String sql="select fullname,a0100,nbase from operuser where username=?";
			rs=dao.search(sql,Arrays.asList(new Object[]{username}));
			if(rs.next()){
				String fullname=rs.getString("fullname");
				String a0100=rs.getString("a0100");
				String nbase=rs.getString("nbase");
				if(fullname!=null&&!"".equals(fullname)){//有fullname优先fullname
					name=fullname;
				}
				else if(a0100!=null&&nbase!=null&&a0100.trim().length()>0&&nbase.trim().length()>0)
				{	
					String _sql="select a0101 from "+nbase+"a01 where a0100=?";
					rs1=dao.search(_sql,Arrays.asList(new Object[]{a0100}));
					if(rs1.next()){
						if(rs1.getString("a0101")!=null&&!"".equals(rs1.getString("a0101"))){//其次是a0101
							name=rs1.getString("a0101");
						} 
					} 
				} 
			} 
			if(name.length()==0)
				name=username; 
		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			PubFunc.closeDbObj(rs1);
			PubFunc.closeDbObj(rs);
		}
		return name;
	}
	
	
	/**
	 * 修改薪资发放记录状态。
	 * @throws GeneralException
	 */
	private void setExtendLogState(String state,String username,String ym,String count)throws GeneralException
	{
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("update gz_extend_log set sp_flag='"+state+"' where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(username)=?");		
			buf.append(" and A00Z3=?"); 
			buf.append(" and "+Sql_switcher.dateToChar("A00Z2","yyyy-MM")+"=?"); 
			dao.update(buf.toString(),Arrays.asList(new Object[]{username.toUpperCase(),new Integer(count),ym}));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	/**
	 * 将不在薪资类别定义中的人员库范围数据插入减少人员表中
	 * @param gz_tablename 薪资临时表
	 * @param dao
	 * @param dbarr  薪资套设置的人员库信息
	 * @param tablename  减少人员临时表
	 * @param column   薪资类别参数为减少人员定义的人员信息项
	 */
	private void insertDecFromBase(String gz_tablename,ContentDAO dao,String[] dbarr,String tablename,String column)
	{
		//
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select distinct upper(nbase)  nbase from "+gz_tablename);
			while(rowSet.next())
			{
				String nbase=rowSet.getString("nbase");
				boolean isNoExist=true;
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					if(nbase.equalsIgnoreCase(pre))
					{
						isNoExist=false;
						break;
					}
				}
				if(isNoExist)
				{
					StringBuffer buf=new StringBuffer("");
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE"+column);
					buf.append(") select ");
					buf.append(" distinct  NBASE,A0100,A0000,B0110,E0122,A0101,'1' as STATE "+column);
					buf.append(" from "+gz_tablename+" where upper(nbase)=? ");
					dao.update(buf.toString(),Arrays.asList(new Object[]{nbase.toUpperCase()}));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace(); 
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
	}
	
	
	
	/**
	 * 把停发人员，生成一张临时表
	 * @param gz_tablename  薪资发放临时表 用户名+_salary_salaryid
	 * @return 用户名+tfPeoples
	 */
	private String createA01Z0ChangeManTable(String gz_tablename)throws GeneralException {
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Tf";
		/**表生成结果*/
		String createResult = "";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			this.salaryTableStructBo.createInsDecTableStruct(tablename,new ArrayList());   //创建新增人员临时表 
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A01Z0,A0101,STATE";
			/**导入数据*/
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					continue;
				
				String srctable=pre+"A01";
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,A01Z0,STATE");
				if(this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), cloumnStr))
				{
					buf.append(","+this.salaryTableStructBo.getOnlyField());
				}
				buf.append(")");
				buf.append(" select ");
				buf.append("'");
				buf.append(pre);
				buf.append("'");
				buf.append(" as NBASE,");
				buf.append(srctable);
				buf.append(".A0100,");
				buf.append(srctable);
				buf.append(".A0000,");
				buf.append(srctable);
				buf.append(".B0110,");	
				buf.append(srctable);
				buf.append(".E0122,");	
				buf.append(srctable);
				buf.append(".A0101,");	
				buf.append(srctable);
				buf.append(".A01Z0,");				
				buf.append("'1' as STATE ");
				if(this.salaryTableStructBo.isAddColumn(this.salaryTableStructBo.getOnlyField(), cloumnStr))
				{
					buf.append(","+srctable+"."+this.salaryTableStructBo.getOnlyField());
				}
				buf.append(" from ");
				buf.append(srctable);
				buf.append(" left join ");
				buf.append(gz_tablename);
				buf.append(" on ");
				buf.append(srctable);
				buf.append(".A0100=");
				buf.append(gz_tablename);
				buf.append(".A0100");
				buf.append(" where upper(");
				buf.append(gz_tablename);
				buf.append(".NBASE)=");
				buf.append("'");
				buf.append(pre.toUpperCase());
				buf.append("'");
				buf.append(" and ");
			
				buf.append(" case when  "+gz_tablename+".A01Z0 is null then '1' ");
				buf.append(" when  "+gz_tablename+".A01Z0 ='' then '1' ");
				buf.append(" else "+gz_tablename+".A01Z0 end <>");
				buf.append(" case when  "+srctable+".A01Z0 is null then '1' ");
				buf.append(" when  "+srctable+".A01Z0 ='' then '1' ");
				buf.append(" else "+srctable+".A01Z0 end"); 
				dao.update(buf.toString());
			}//for i loop end.
			// 查看生成的表中是否有数据，有则返回1
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(tablename, false))
			{	 
				createResult = isExistRecord(tablename);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return createResult;
	}
	
	
	
	/**
	 * 对选中的公式进行计算 
	 * @param formulalist 计算公式列表
	 * @param strWhere    计算范围（也即过滤条件）
	 * @param gz_tablename  计算数据所在的表
	 * @param usedMidVarList   薪资帐套的临时变量(过滤薪资帐套不用的)
	 * *@param paramMap:   where_str  计算数据筛选条件, ym: 薪资发放月份  2015-02-01,count 薪资发放次数
	 * @param flag 2:薪资审批 1：薪资发放
	 * @return
	 * @throws GeneralException
	 */
	private boolean secondComputing(ArrayList formulalist,String strWhere,String gz_tablename,ArrayList usedMidVarList,HashMap paramMap)throws GeneralException
	{
		boolean bflag=false;
		try
		{
				int nrunflag=0;
			    int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
				RecordVo vo=new RecordVo(gz_tablename.toLowerCase());
				ArrayList gzItemList=this.salaryTemplateBo.getSalaryItemList("", ""+salaryid, 1);
				
				/**薪资项目和临时变量列表*/
				 ArrayList fldvarlist=new ArrayList();
				 fldvarlist.addAll(this.salaryTemplateBo.getSalaryItemList("", ""+salaryid, 2));
				 fldvarlist.addAll(usedMidVarList);
				//清除所有数据
				for(int i=0;i<formulalist.size();i++)
				{
		                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
		                nrunflag=Integer.parseInt((String)dbean.get("runflag"));
		                String formula=(String)dbean.get("rexpr");
		                String cond=(String)dbean.get("cond");
		                String fieldname=(String)dbean.get("itemname");
		                String strStdId=(String)dbean.get("standid");
		                /**系统项不用计算*/
		                if(isSystemItem(fieldname,gzItemList)&&!"A00Z0".equalsIgnoreCase(fieldname)&&!"a00z1".equalsIgnoreCase(fieldname))//归属次数和归属日期也计算，zhaoxg add 2013-10-16
		                	continue;
		                /**分析左边项是否在工资表中存在*/
		                if(!vo.hasAttribute(fieldname.toLowerCase()))
		                	continue;
		                switch(nrunflag)
		                {
		                	case 2://税率表
		                		deleteTaxMx(dbean,strWhere,fldvarlist,gz_tablename);
			                	break;
		                }
				}//for i loop end. 
				HashMap clearItemMap=new HashMap();  //记录计算项是否多次算税
				for(int i=0;i<formulalist.size();i++)
				{
		                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
		                nrunflag=Integer.parseInt((String)dbean.get("runflag"));
		                String formula=(String)dbean.get("rexpr");
		                String cond=(String)dbean.get("cond");
		                String fieldname=(String)dbean.get("itemname");
		                String strStdId=(String)dbean.get("standid");
		                /**系统项不用计算*/
		                if(isSystemItem(fieldname,gzItemList)&&!"A00Z0".equalsIgnoreCase(fieldname)&&!"a00z1".equalsIgnoreCase(fieldname))//归属次数和归属日期也计算，zhaoxg add 2013-10-16
		                	continue;
		                /**分析左边项是否在工资表中存在*/
		                
		              
		                if(!vo.hasAttribute(fieldname.toLowerCase()))
		                	continue;
		                switch(nrunflag)
		                {
			                case 1://执行工资标准
			                	if(strStdId!=null&&strStdId.length()>0)//可能没定义标准表，那么就不执行了 zhaoxg add 2016-8-2
			                		calcGzStandard(Integer.parseInt(strStdId),fieldname,strWhere,gz_tablename);
			                	break;
			                case 2://税率表
			                	String errorInfo=calcTax(dbean,strWhere,fldvarlist,gz_tablename,clearItemMap);
			                	if(errorInfo!=null&&errorInfo.trim().length()>0)
			                		throw new Exception(errorInfo);
			                	break;
			                case 0://执行计算公式
			                	calcFormula(formula,cond,fieldname,strWhere,fldvarlist,paramMap,gz_tablename,usedMidVarList);
			                	break;
		                } 
				}//for i loop end.
				
				//取专项附加额
				if(specialUpdTaxSql.length() > 0) {
					
					updateTaxByspecial(specialUpdTaxSql.substring(1), strWhere, gz_tablename);
				}
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		} 
		return bflag;
	}
	
	
	/**
	 * 
	 * @param formula    计算公式
	 * @param cond       计算条件
	 * @param fieldname  计算项目
	 * @param strWhere   整个人员过滤条件
	 * @param paramMap:   where_str  计算数据筛选条件, ym: 薪资发放月份  2015-02-01,count 薪资发放次数
	 * @param usedMidVarList   薪资帐套的临时变量(过滤薪资帐套不用的)
	 * @throws GeneralException 
	 */
	private void calcFormula(String formula,String cond,String fieldname,String strWhere,ArrayList fldvarlist,HashMap paramMap,String gz_tablename,
			ArrayList usedMidVarList) throws GeneralException
	{
		YksjParser yp=null;
		RowSet rowSet=null;
		try
		{		
				String strfilter="";
				String ym=(String)paramMap.get("ym"); //薪资发放月份
				String count=(String)paramMap.get("count"); //薪资发放次数 
				ContentDAO dao=new ContentDAO(this.conn);
				SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号  
				RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
				/**先对计算公式的条件进行分析*/
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{ 
					
					yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.setVarList(usedMidVarList);//使用“执行标准”函数时，临时变量需要用到单独传入的fielditem数据集 zhanghua 20170516
					yp.setSupportVar(true);//设置允许临时变量
					if(ym!=null&&ym.trim().length()>0&&count!=null&&count.trim().length()>0)
					{
						String stry=ym.substring(0, 4);
						String strm=ym.substring(5, 7);
						String strc=count;
						YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
						yp.setYmc(ymc);
					}
					yp.run_where(cond);
					strfilter=yp.getSQL();
				}
				StringBuffer strcond=new StringBuffer();
				if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
					strcond.append(strWhere);
				if(!("".equalsIgnoreCase(strfilter)))
				{
					if(strcond.length()>0)
						strcond.append(" and ");
					strcond.append(strfilter);
				}
				
				if(!"归属日期()".equals(formula.trim())&&!"归属日期".equals(formula.trim()))
				{
					/**进行公式计算*/
					if("A00Z0".equalsIgnoreCase(fieldname)){
						yp=new YksjParser( this.userview ,fldvarlist,
								YksjParser.forNormal, getDataType("D"),YksjParser.forPerson , "Ht", "");
					}else if("a00z1".equalsIgnoreCase(fieldname)){
						yp=new YksjParser( this.userview ,fldvarlist,
								YksjParser.forNormal, getDataType("N"),YksjParser.forPerson , "Ht", "");
					}else{
						FieldItem item=DataDictionary.getFieldItem(fieldname);
						yp=new YksjParser( this.userview ,fldvarlist,
								YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", "");
					}
					String temp_tableName="t#"+this.userview.getUserName()+"_gzsp";
					yp.setVarList(usedMidVarList);//使用“执行标准”函数时，临时变量需要用到单独传入的fielditem数据集 zhanghua 20170516
					yp.setSupportVar(true);//设置允许临时变量
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("zxfj_gz_tab", gz_tablename);
					//薪资表待计算的人员范围
					map.put("zxfj_sql_filter", strcond.length()>0?(" and " + strcond.toString()):"");
					map.put("zxfj_target_item", fieldname);//薪资表待写入专项额的目标指标
					map.put("zxfj_tax_date_item", ctrlparam.getValue(SalaryCtrlParamBo.TAX_DATE_FIELD));//计税时间指标
					map.put("zxfj_id", String.valueOf(salaryid));//salaryid
					yp.setZxfj_propertyMap(map);
					yp.run(formula,this.conn,strcond.toString(),gz_tablename);
				}
				/**单表计算*/
				String strexpr="";
				if(!"归属日期()".equals(formula.trim())&&!"归属日期".equals(formula.trim()))
					strexpr=yp.getSQL();
				else {
					if(ym.length() == 7) {
						ym += "-01";
					}
					strexpr=Sql_switcher.dateValue(ym);
				}
				StringBuffer strsql=new StringBuffer();
				strsql.append("update ");
				strsql.append(gz_tablename);
				strsql.append(" set ");
				strsql.append(fieldname);
				strsql.append("=");
				strsql.append(strexpr);
				strsql.append(" where 1=1 ");
				if(strcond.length()>0)
				{
					strsql.append(" and ");
					strsql.append(strcond.toString());
				} 
				String tableName="t#"+this.userview.getUserName()+"_gzsp";  
				if(!gz_tablename.equalsIgnoreCase(tableName)&&manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
				{ 
					String dbpres=templateVo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						StringBuffer tempSql=new StringBuffer(" and upper(nbase)='"+pre.toUpperCase()+"'" ); 
						//					权限过滤
						tempSql.append(this.salaryTemplateBo.getWhlByUnits(gz_tablename,true)); 
						dao.update(strsql.toString()+tempSql.toString());
					}
				}
				else
					dao.update(strsql.toString());
				
				//取专项附加额函数去除注释//
				if(formula.indexOf(ResourceFactory.getProperty("label.specialAddAmount")) != -1)
					formula = removeNotes(formula);
				
				//取专项附加额,因为如果设置了这个函数，默认一定有ZNJY等字段，否则怎么计算都不对
				//累计不写入到税率表
				if(formula.indexOf(ResourceFactory.getProperty("label.specialAddAmount")) != -1 && formula.indexOf(ResourceFactory.getProperty("hmuster.label.toatlCount")) == -1) {
					String field = "";
					if(formula.indexOf(ResourceFactory.getProperty("label.gz.znjy")) != -1) {//子女教育
						field = "ZNJY";
					}else if(formula.indexOf(ResourceFactory.getProperty("label.gz.jxjy")) != -1) {//继续教育
						field = "JXJY";
					}else if(formula.indexOf(ResourceFactory.getProperty("label.gz.zfzj")) != -1) {//住房租金
						field = "ZFZJ";
					}else if(formula.indexOf(ResourceFactory.getProperty("label.gz.zfdk")) != -1) {//住房贷款利息
						field = "ZFDKLX";
					}else if(formula.indexOf(ResourceFactory.getProperty("label.gz.sylr")) != -1) {//赡养老人
						field = "SYLR";
					}
					
					if(specialUpdTaxSql.indexOf(field) == -1) {
						specialUpdTaxSql.append("," + field + "=(select " + gz_tablename + "." + strexpr + " from " + gz_tablename + " where ");
						specialUpdTaxSql.append(" gz_tax_mx.a00z0 = " + gz_tablename + ".a00z0 "
								+ "and gz_tax_mx.a00z1 = " + gz_tablename + ".a00z1 "
								+ "and gz_tax_mx.a0100 = " + gz_tablename + ".a0100 "
								+ "and gz_tax_mx.nbase = " + gz_tablename + ".nbase ");
						if(strcond.length()>0)
						{
							specialUpdTaxSql.append(" and ");
							specialUpdTaxSql.append(strcond.toString());
						} 
						specialUpdTaxSql.append(")");
					}
				}
				
		}
		catch(Exception ex)
		{
				String message=ex.toString();
				if(Sql_switcher.searchDbServer()==1&&message.indexOf("8060")!=-1)
				{ 
					PubFunc.resolve8060(this.conn,gz_tablename);
					throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
				}
				if("a00z1".equalsIgnoreCase(fieldname)&&ex.toString().indexOf("唯一")!=-1){
				  	ex.printStackTrace();
	      	    	throw GeneralExceptionHandler.Handle(new Exception("同一人有多条薪资数据，不能执行归属次数的计算公式！"));
				}
				FieldItem item=DataDictionary.getFieldItem(fieldname);
				if(message.indexOf("取专项附加额")!=-1) {
					ex.printStackTrace();
					if(message.indexOf("^^^^") > -1) {
						message = message.split("\\^\\^\\^\\^")[1];
					}
					//取专项附加额函数需用7.2.1及以上转库大师，库维护处右键执行个税专项附加存储过程维护！
	      	    	throw GeneralExceptionHandler.Handle(new Exception("【" + item.getItemdesc() + "】" + message));
				}
				
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.compute.checkFielditem").replace("{0}", item.getItemdesc())));
		}finally{ 
				PubFunc.closeDbObj(rowSet);
				if("a00z1".equalsIgnoreCase(fieldname)){
					
				}else{
					yp=null;
				}
				
		} 
	}
	
	/**
	 * 因为取专项附加额特殊处理，但是对于由于//注释的处理有问题，这里讲注释去掉
	 * @param formula
	 * @return
	 */
	private String removeNotes(String formula) {
		try {
			if(formula.indexOf("//") != -1) {
				int position_ = formula.indexOf("//");
				for(int i = position_; i < formula.length(); i++) {
					//如果是//最后换行了，截取前面的部分和后面的部分，组成去掉注释的所有字符
					if(formula.charAt(i) == '\n') {
						String formula_pre = formula.substring(0, position_);
						formula = formula_pre + formula.substring(i, formula.length());
						break;
					}
					if(i == (formula.length()-1)) {
						return "";
					}
				}
				removeNotes(formula);
			}else {
				return formula;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return formula;
	}
	/**
	 * 如果是取专项附加额计算，相对应的指标需要计算到gz_tax_mx的隐藏指标中
	 * @param field
	 * @param strexpr
	 * @param strcond
	 * @param tableName
	 * @throws GeneralException
	 */
	private void updateTaxByspecial(String specialUpdTaxSql, String strcond, String tableName) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if(StringUtils.isNotBlank(specialUpdTaxSql)) {
				strsql.setLength(0);
				strsql.append("update gz_tax_mx set ");
				strsql.append(specialUpdTaxSql);
				strsql.append(" where exists (select null from " + tableName + " where ");
				strsql.append(" gz_tax_mx.a00z0 = " + tableName + ".a00z0 "
						+ "and gz_tax_mx.a00z1 = " + tableName + ".a00z1 "
						+ "and gz_tax_mx.a0100 = " + tableName + ".a0100 "
						+ "and gz_tax_mx.nbase = " + tableName + ".nbase ");
				if(strcond.length()>0)
				{
					strsql.append(" and ");
					strsql.append(strcond);
				}
				strsql.append(") and gz_tax_mx.salaryid = " + salaryid);
				dao.update(strsql.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(e));
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
	
	/**
	 * 执行薪资标准
	 * @param standid	标准号
	 * @param fieldname 目标指标
	 * @param strWhere  条件
	 */
	private void calcGzStandard(int standid,String fieldname,String strWhere,String gz_tablename)throws GeneralException
	{
      try
      {
			SalaryStandardBo stdbo=new SalaryStandardBo(this.conn,String.valueOf(standid),""); 	
			RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
			SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号  
			/**如果标准不存在，则退出*/
			if(!stdbo.isExist())
					return;
			/**重新计算相关日期型或数值型区间范围的值*/
			
			StringBuffer buf=new StringBuffer();
			if(!stdbo.checkHVField(buf))
					throw new GeneralException(buf.toString());
			/**把标准横纵坐标为日期型或数值型指标，加至薪资表中*/
			ArrayList list=stdbo.addStdItemIntoTable(gz_tablename);
			stdbo.updateStdItem(list, gz_tablename);
			/**关联更新串*/
			String joinon=stdbo.getStandardJoinOn(gz_tablename);
			FieldItem item=DataDictionary.getFieldItem(fieldname);
			DbWizard dbw=new DbWizard(this.conn);		
			 
			//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
			String atableName="t#"+this.userview.getUserName()+"_gzsp";  //this.userview.getUserName()+"_sp_data";
			if(!gz_tablename.equalsIgnoreCase(atableName)&&manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
			{
					String dbpres=templateVo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
							String pre=dbarr[i];
							StringBuffer temp=new StringBuffer("");
							if(strWhere!=null&&strWhere.trim().length()>0)
								temp.append(" and ");
							temp.append(" upper(nbase)='"+pre.toUpperCase()+"'") ;
							//					权限过滤
							temp.append(this.salaryTemplateBo.getWhlByUnits(gz_tablename,true));
							
							switch(Sql_switcher.searchDbServer())
							{
								case 1: //MSSQL
									dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=gz_item.standard", strWhere+temp.toString(), "");
									break;
								case 2://oracle
									if("N".equalsIgnoreCase(item.getItemtype()))
										dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=to_number(gz_item.standard)", strWhere+temp.toString(), "");
									else
										dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=gz_item.standard", strWhere+temp.toString(), "");
									break;
								case 3://db2
									if("N".equalsIgnoreCase(item.getItemtype()))
										dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=double(gz_item.standard)", strWhere+temp.toString(), "");
									else
										dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=gz_item.standard", strWhere+temp.toString(), "");
									break;
							}
					}
			}
			else
			{
				switch(Sql_switcher.searchDbServer())
				{
						case 1: //MSSQL
							if("N".equalsIgnoreCase(item.getItemtype()))
								dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"="+Sql_switcher.isnull("nullif(gz_item.standard,'')","0"), strWhere, "");
							else
								dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
							break;
						case 2://oracle
							if("N".equalsIgnoreCase(item.getItemtype()))
								dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=to_number(gz_item.standard)", strWhere, "");
							else
								dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
							break;
						case 3://db2
							if("N".equalsIgnoreCase(item.getItemtype()))
								dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=double(gz_item.standard)", strWhere, "");
							else
								dbw.updateRecord(gz_tablename, "gz_item",joinon,gz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
							break;
				}
			}
      }
      catch(Exception ex)
      {
	    	  ex.printStackTrace();
	    	  throw GeneralExceptionHandler.Handle(ex);
      }
	}
	
	
	/**
	 * 个人所得税计算
	 * @param taxid
	 * @param strWhere 算税条件
	 * @param fldvarlist  薪资项目和临时变量列表
	 * @param clearItemMap 记录计算项是否多次算税
	 */
	private String calcTax(DynaBean dbean,String strWhere,ArrayList fldvarlist,String tableName,HashMap clearItemMap)throws GeneralException
	{
		  String errorInfo="";
		  int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
		  CalcTaxBo calcbo=new CalcTaxBo(salaryid,this.conn,this.userview);
	      try
	      {
	    	  String _taxid=(String)dbean.get("standid");
	    	  if(_taxid==null||_taxid.trim().length()==0) //算税公式没定义完整
	    	    	throw new Exception("算税公式定义不完整!");
	    	    ContentDAO dao=new ContentDAO(this.conn);
	    	    String cond=(String)dbean.get("cond");
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{
					YksjParser yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.run_where(cond);
					cond=yp.getSQL();
				}	    	
				if(cond.length()>0&&strWhere.length()>0)
					strWhere=strWhere+" and ( "+cond+" )";
				else if((strWhere==null||strWhere.trim().length()==0)&&cond!=null&&cond.trim().length()>0)
					strWhere=cond; 
	    	    calcbo.setGz_tablename(tableName); 
	    	    String itemid=(String)dbean.get("itemname");
	    	    if(clearItemMap.get(itemid.toLowerCase())!=null) 
	    	    {
	    	    		calcbo.setIsClearItem("0");
	    	    }
	    	    else
	    	    	clearItemMap.put(itemid.toLowerCase(),"1");  //计算项首次算税需清空薪资表的项目值
	    	   
	    	    //是否一个人在同一类别下有多条记录
	    	    String _str=getMultipleDataOneMenStr(tableName);
	    	    if(_str.length()>0)
	    	    {
	    	    	String _strWhere=strWhere;
	    	    	if(_strWhere==null||_strWhere.trim().length()==0)
	    	    		_strWhere=" not ("+_str.substring(3)+")";
	    	    	else
	    	    		_strWhere+=" and not ("+_str.substring(3)+")";
	    	    	//计算当月没有多条记录的人员个税
	    	    	calcbo.calc(_strWhere, dbean);
	    	    	
	    	    	
	    	    	RowSet rowSet=dao.search("select * from "+tableName+" where ("+_str.substring(3)+") order by nbase,a0100,a00z0,a00z1");
	    	    	while(rowSet.next())
	    	    	{ 
	    	    		_strWhere=strWhere;
	    	    		Date d=rowSet.getDate("a00z0");
	    	    		Calendar cd=Calendar.getInstance();
	    	    		cd.setTime(d);
	    	    		
	    	    		String _sql=" ( "+Sql_switcher.year("a00z0")+"="+cd.get(Calendar.YEAR)+" and "+Sql_switcher.month("a00z0")+"="+(cd.get(Calendar.MONTH)+1)
	    	    					+" and a00z1="+rowSet.getString("a00z1")+" and upper(nbase)='"+rowSet.getString("nbase").toUpperCase()
	    	    					+"' and a0100='"+rowSet.getString("a0100")+"' )";
	    	    		if(_strWhere==null||_strWhere.trim().length()==0)
		    	    		_strWhere=_sql;
		    	    	else
		    	    		_strWhere+=" and "+_sql;
	    	    		calcbo.calc(_strWhere, dbean);
	    	    	}
	    	    	
	    	    }
	    	    else
	    	    	calcbo.calc(strWhere, dbean); 
	    	    errorInfo=calcbo.getErrorInfo();
	      }
	      catch(Exception ex)
	      {
	    	  errorInfo=calcbo.getErrorInfo();
	    	  ex.printStackTrace();	  
	      }
	      return errorInfo;
	}

	private void deleteTaxMx(DynaBean dbean,String strWhere,ArrayList fldvarlist,String gz_tablename)throws GeneralException
	{
	      try
	      { 
	    	    String cond=(String)dbean.get("cond");
	    	    String taxid=(String)dbean.get("standid");
	    	    if(taxid==null||taxid.trim().length()==0) //算税公式没定义完整
	    	    	return;
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{
					YksjParser yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.run_where(cond);
					cond=yp.getSQL();
				}	    	
				if(cond.length()>0&&strWhere.length()>0)
					strWhere=strWhere+" and ( "+cond+" )";
				else if((strWhere==null||strWhere.trim().length()==0)&&cond!=null&&cond.trim().length()>0)
					strWhere=" ( "+cond+" ) ";
				
	    	    CalcTaxBo calcbo=new CalcTaxBo(this.salaryid,this.conn,this.userview);
	    	    calcbo.setGz_tablename(gz_tablename);
	    	    calcbo.delTaxMx(strWhere, dbean);
	      }
	      catch(Exception ex)
	      {
	    	  ex.printStackTrace();	 
	    	  throw GeneralExceptionHandler.Handle(ex);
	      }
	}
	
	/**
	 * 是否一个人在同一类别下有多条记录
	 * @return
	 */
	private String getMultipleDataOneMenStr(String tablename)
	{
		StringBuffer _str=new StringBuffer("");
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select count(a0100),a0100,nbase from "+tablename+" group by nbase,a0100 having count(a0100)>1"); 
			while(rowSet.next())
			{
				_str.append(" or (a0100='"+rowSet.getString("a0100")+"' and lower(nbase)='"+rowSet.getString("nbase").toLowerCase()+"')");
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception ex)
	    {
	    	  ex.printStackTrace();	
	    }
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return _str.toString();
	}
	
	
	/**
	 * 分析此指标是否为系统项
	 * @param fieldname
	 * @return
	 */
	private boolean isSystemItem(String fieldname,ArrayList gzItemList)
	{
		if("A01Z0".equalsIgnoreCase(fieldname)){//停放标识放开了，可以计算  zhaoxg add 2015-4-10
			return false;
		}
		boolean bflag=false;
		for(int i=0;i<gzItemList.size();i++)
		{
			LazyDynaBean itemvo=(LazyDynaBean)gzItemList.get(i);
			if(((String)itemvo.get("fieldid")).equalsIgnoreCase(fieldname))
			{
				if("3".equals((String)itemvo.get("initflag")))
				{
					bflag=true;
					break;
				}
			}
		}
		return bflag;
	}
	
	
	
	/**
	 * 从薪资表中删除薪资停发的人员
	 * @throws GeneralException
	 */
	private void removeA01Z0ManData() throws GeneralException
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		StringBuffer buf=new StringBuffer();
		String tablename="t#"+this.userview.getUserName()+"_gz_Tf";
		String gz_tablename=this.salaryTemplateBo.getGz_tablename();
		int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
		String manager=this.salaryTemplateBo.getManager();
		PreparedStatement ps = null;
		try
		{
			String a01z0Flag=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
			{
			
				DbWizard dbw=new DbWizard(this.conn);
				Table table=new Table(tablename);
				if(!dbw.isExistTable(tablename, false))
					return;
				int rows=getRows(tablename);
				if(rows==0)
					return;				
				buf.append("delete from ");
				buf.append(gz_tablename);
				buf.append(" where exists(select * from ");
				buf.append(tablename);
				buf.append(" where state='1' and upper(");
				buf.append(gz_tablename);
				buf.append(".nbase)=upper(");
				buf.append(tablename);
				buf.append(".dbname) and ");
				buf.append(gz_tablename);
				buf.append(".A0100=");
				buf.append(tablename);
				buf.append(".A0100)");
				ContentDAO dao=new ContentDAO(this.conn);
				//删除税率明细 
				StringBuffer buf1=new StringBuffer("");
				buf1.append("delete from gz_tax_mx where salaryid="+salaryid+" and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?");
		    	if(manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
		    		buf1.append(" and ( lower(userflag)='"+manager.toLowerCase()+"' or userflag is null )");
		    	else
		    		buf1.append(" and ( lower(userflag)='"+this.userview.getUserName().toLowerCase()+"' or userflag is null )");
		    	
		    	String sub_str="select * from "+gz_tablename+" where exists(select * from "+tablename+" where state='1' and upper("+gz_tablename+".nbase)=upper(";
		    	sub_str+=tablename+".dbname) and "+gz_tablename+".A0100="+tablename+".A0100)";
		    	ps = this.conn.prepareStatement(buf1.toString());
		    	RowSet rowSet=dao.search(sub_str);
		    	while(rowSet.next())
		    	{
		    		Calendar d=Calendar.getInstance();
		    		d.setTime(rowSet.getDate("a00z0")); 
		    		ps.setString(1,rowSet.getString("nbase").toLowerCase());
		    		ps.setString(2,rowSet.getString("a0100"));
		    		ps.setInt(3, d.get(Calendar.YEAR));
		    		ps.setInt(4, (d.get(Calendar.MONTH)+1));
		    		ps.setInt(5,rowSet.getInt("a00z1"));
		    		ps.addBatch();
		    	}
		    	
		    	// 打开Wallet
				dbS.open(conn, buf1.toString());
		    	ps.executeBatch();
				 
				dao.update(buf.toString());
				
				//同步薪资发放数据的映射表
				buf.setLength(0);
				String username=this.userview.getUserName().toLowerCase();
				if(manager.length()>0)
					username=manager.toLowerCase();	
				buf.append("delete from salary_mapping where salaryid="+salaryid+" and lower(userflag)='"+username+"' and  exists(select * from ");
				buf.append(tablename);
				buf.append(" where state='1' and upper(salary_mapping.nbase)=upper(");
				buf.append(tablename);
				buf.append(".dbname) and salary_mapping.A0100=");
				buf.append(tablename);
				buf.append(".A0100)");
				dao.update(buf.toString());
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PubFunc.closeDbObj(ps);
		}
	}
	
	
	/**
	 * 求当前表的记录数
	 * @param tablename
	 * @return
	 */
	private  int getRows(String tablename)
	{
		int maxrows=0;
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nrow from ");
			buf.append(tablename);
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			if(rset.next())
				maxrows=rset.getInt("nrow"); 
		}
		catch(Exception  ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return maxrows;
	}
	
	
	/**
	 * 把新增人员，生成一张临时表
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,E0122,A0101,state
	 * 主键字段：DBNAME,A0100
	 * @param model  1:新建工资   2：变动比对
	 * @param ff_date  发放日期
	 * @return 用户名+InsPeoples 表名,如果创建不成功，返回空串
	 */
	public String createAddManTable(int model,String ff_date,HashMap complexWhlMap) throws GeneralException  {
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		/**表生成结果*/
		String createResult = "";
		RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
		try
		{
			int salaryid=templateVo.getInt("salaryid");
			SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号

			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list =new ArrayList();
			if(model==2)
				list=add_del_chg_rightList(salaryid,"1");
			this.salaryTableStructBo.createInsDecTableStruct(tablename,list);   //创建新增人员临时表
			/**导入数据*/
			String dbpres=templateVo.getString("cbase");
			/**应用库前缀*/
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE"; 
			String onlyField=this.salaryTableStructBo.getOnlyField(); 
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			
			
			String _flag=ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String cond=templateVo.getString("cond");
			String cexpr=templateVo.getString("cexpr");
			StringBuffer column = new StringBuffer();//新增人员的相关字段信息存入临时表，搜房网  zhaoxg add 2013-11-14
			if(list.size()>0){
				DynaBean abean=null;
				for(int i=0;i<list.size();i++){
					abean=(DynaBean)list.get(i); 
					String itemid=(String)abean.get("itemid");
					if(cloumnStr.toLowerCase().indexOf(itemid.toLowerCase())!=-1){//排除默认项
						continue;
					} 
					column.append(","+itemid.toLowerCase()+"");
				}
			}
			
			//系统定义了唯一指标,且不在增减人员选定指标中
			if(!(onlyField.length()>0&&cloumnStr.indexOf(","+onlyField.toUpperCase())==-1&&column.indexOf(","+onlyField.toLowerCase())==-1 ))
					onlyField="";
			String flag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					continue; 
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE");
				if(onlyField.length()>0)
					buf.append(","+onlyField);
				buf.append(")");
				buf.append(" select '");
				buf.append(pre);
				buf.append("' as DBNAME,");
				buf.append(pre+"A01.A0100,A0000,B0110,E0122,A0101,'1' as STATE  ");
				if(onlyField.length()>0)
					buf.append(","+onlyField);

			
				//新建，比对全部按业务范围、操作单位、人员范围控制走 sunjian 2017-9-19
				if((manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())) || (flag!=null&& "1".equals(flag)))
				{
							//	buf.append(this.userview.getPrivSQLExpression(pre, false));	  按业务范围、操作单位、人员范围控制
							 buf.append(" from "+pre+"A01 where 1=1 "+this.salaryTemplateBo.getWhlByUnits(pre+"A01",false));
								
				}
				/*else if(flag!=null&&flag.equals("1"))  // 人员范围权限过滤标志  1：有  
				{
								//buf.append(this.userview.getPrivSQLExpression(pre, false));	
								buf.append(" from "+pre+"A01 where 1=1 "+this.salaryTemplateBo.getWhlByUnits(pre+"A01",false));
				}*/
				else 
				{
							buf.append(" from "+pre+"A01 where 1=1 ");
				}
				
				
				
				if("0".equals(_flag)&&cond.length()>0)  //0：简单条件
				{
					FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
					String strSql = factor.getSqlExpression();
					buf.append(" and "+pre+"A01.a0100 in ( select "+pre+"A01.a0100 "+strSql+")"); 
					
				}
				else if("1".equals(_flag)&&cond.length()>0)  // 1：复杂条件
				{
					HashMap paramMap=new HashMap();
					paramMap.put("pre",pre);               //人员库
					paramMap.put("ff_date",ff_date);  //发放日期 
					paramMap.put("cond",cond);    //高级条件
					buf.append(getComplexCondSql(complexWhlMap,ctrlparam,paramMap));
				} 
				
				/**停发标志*/
				String a01z0Flag=ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
				if(a01z0Flag!=null&& "1".equals(a01z0Flag))
					buf.append(" and (A01Z0='1' or A01Z0='' or A01Z0 is null)");
			 
				//过滤掉薪资临时表中已存在的人员记录
				String gz_tablename=this.salaryTemplateBo.getGz_tablename();
				buf.append(" and  not exists (select null from "+gz_tablename+" where upper(NBASE)='"+pre.toUpperCase()+"' and "+gz_tablename+".a0100="+pre+"A01.a0100  ) ");
				 
				
				dao.update(buf.toString());
				if(column.length()>0){
						String[] temp=column.substring(1).split(",");
						for(int j=0;j<temp.length;j++){
								StringBuffer _sql=new StringBuffer();	
								FieldItem item=DataDictionary.getFieldItem(temp[j]);
								String fieldsetid=item.getFieldsetid();
								if("a01".equalsIgnoreCase(fieldsetid)){
									_sql.append("update "+tablename+" set ");
									_sql.append(temp[j]+"=(select "+temp[j]+" from ");
									_sql.append(""+pre+"a01");
									_sql.append("   where  a0100="+tablename+".a0100) where "+tablename+".dbname='"+pre+"'");
									_sql.append("and exists (");
									_sql.append("select null from "+pre+"a01  where  a0100="+tablename+".a0100  ");
									_sql.append(")");
								}else if("b01".equalsIgnoreCase(fieldsetid)){//b01里面的指标单独处理，暂时没考虑其他主集 比如k01 zhaoxg add 2016-10-10
									_sql.append("update "+tablename+" set ");
									_sql.append(temp[j]+"=(select "+temp[j]+" from ");
									_sql.append("b01");
									_sql.append("   where  b0110="+tablename+".b0110) where "+tablename+".dbname='"+pre+"'");
									_sql.append(" and exists (");
									_sql.append("select null from b01  where  b0110="+tablename+".b0110  ");
									_sql.append(")");
								}else if(fieldsetid.toLowerCase().startsWith("k")){//zhanghua 2017-6-19 增加对岗位指标支持
									_sql.append("update "+tablename+" set ");
									_sql.append(temp[j]+"=(select "+fieldsetid+"."+temp[j]+" from (select E01A1,'"+pre+"' as DBNAME ,p.A0100 from "+pre+"A01 p inner join "+tablename+" t on p.a0100=t.A0100 and t.DBNAME='"+pre+"') a");
									_sql.append(" inner join  "+fieldsetid+" on a.E01A1="+fieldsetid+".E01A1 where "+tablename+".A0100=a.a0100 and "+tablename+".DBNAME=a.DBNAME) ");
									_sql.append("  where  "+tablename+".DBNAME='"+pre+"' ");
									_sql.append(" and exists (");
									_sql.append(" select null from (select E01A1,'"+pre+"' as DBNAME ,p.A0100 from "+pre+"A01 p inner join "+tablename+" t on p.a0100=t.A0100 and t.DBNAME='"+pre+"') c "
											+ "inner join "+fieldsetid+" on c.E01A1="+fieldsetid+".E01A1 where "+tablename+".A0100=c.a0100 and "+tablename+".DBNAME=c.DBNAME )");
								}
								else{
									_sql.append("update "+tablename+" set ");	
									_sql.append(temp[j]+"=(select "+temp[j]+" from");
									_sql.append("(select a0100,"+temp[j]+" from "+pre+fieldsetid+" a where a.i9999=(select max(i9999) from "+pre+fieldsetid+" b where a.a0100=b.a0100 ) )");
									_sql.append(" c where c.a0100="+tablename+".a0100) where "+tablename+".dbname='"+pre+"'");
									_sql.append(" and exists (");
									_sql.append("select null from (select a0100,"+temp[j]+" from "+pre+fieldsetid+" a where a.i9999=(select max(i9999) from "+pre+fieldsetid+" b where a.a0100=b.a0100 ) ) c where c.a0100="+tablename+".a0100");
									_sql.append(")");
								}
								dao.update(_sql.toString());
						}
				}
			}//for i loop end.  
			// 查看生成的表中是否有数据，有则返回1
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(tablename, false))
			{	 
				createResult = isExistRecord(tablename);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return createResult;
	}
	
	/**
	 * 在薪资手动引入之前 先把对应的人全部塞进临时表
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,E0122,A0101,state
	 * 主键字段：DBNAME,A0100
	 * @param ff_date  发放日期
	 */
	public void searchLike_For_HandImport(String ff_date,HashMap complexWhlMap) throws GeneralException  {
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		/**表生成结果*/
		String sql = "";
		RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
		try
		{
			int salaryid=templateVo.getInt("salaryid");
			SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号

			ContentDAO dao=new ContentDAO(this.conn);
			this.salaryTableStructBo.createInsDecTableStruct(tablename, new ArrayList());   //创建新增人员临时表
			/**导入数据*/
			String dbpres=templateVo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			
			String _flag=ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String cond=templateVo.getString("cond");
			String cexpr=templateVo.getString("cexpr");
			String gz_module = templateVo.getString("cstate");
			String handImportPer = ((!"1".equals(gz_module) && this.userview.hasTheFunction("324021201") == true) || ("1".equals(gz_module) && this.userview.hasTheFunction("325021201") == true)) ? "1" : "0";

			String flag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					continue; 
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122");
				buf.append(")");
				buf.append(" select '");
				buf.append(pre);
				buf.append("' as DBNAME,");
				buf.append(pre+"A01.A0100,A0000,B0110,E0122  ");

				//新建，比对全部按业务范围、操作单位、人员范围控制走 sunjian 2017-9-19
				if("0".equals(handImportPer) &&"1".equals(flag))
				{
					buf.append(" from "+pre+"A01 where 1=1 "+this.salaryTemplateBo.getWhlByUnits(pre+"A01",false));
								
				}else 
				{
					buf.append(" from "+pre+"A01 where 1=1 ");
				}

				//zhanghua 2019-08-01 手工引入如果勾选权限管理-可引入管理范围外人员，暂时不按账套人员范围控制。待后期优化
				if("0".equals(handImportPer)) {
					if ("0".equals(_flag) && cond.length() > 0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond, pre, false, false, true, 1, "su");
						String strSql = factor.getSqlExpression();
						buf.append(" and " + pre + "A01.a0100 in ( select " + pre + "A01.a0100 " + strSql + ")");

					} else if ("1".equals(_flag) && cond.length() > 0)  // 1：复杂条件
					{
						HashMap paramMap = new HashMap();
						paramMap.put("pre", pre);               //人员库
						paramMap.put("ff_date", ff_date);  //发放日期
						paramMap.put("cond", cond);    //高级条件
						buf.append(getComplexCondSql(complexWhlMap, ctrlparam, paramMap));
					}
				}
				
				dao.update(buf.toString());
				
			}//for i loop end.  
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	 
	
	/**
	 * 薪资类别定义复杂条件时，生成的SQL语句
	 * @param complexWhlMap  为优化性能，前面功能已基于复杂条件生成了数据表，此处可直接调用
	 * @param paramMap 参数集合   	paramMap.put("pre",pre);               //人员库 	paramMap.put("ff_date",ff_date);  //发放日期  	paramMap.put("cond",cond);    //高级条件
	 * @return
	 */
	public String  getComplexCondSql(HashMap complexWhlMap,SalaryCtrlParamBo ctrlparam,HashMap paramMap)throws GeneralException
	{
			String w ="";
			try
			{
				   ContentDAO dao=new ContentDAO(this.conn);
				   String pre=(String)paramMap.get("pre");
				   String ff_date=(String)paramMap.get("ff_date"); 
				   String cond=(String)paramMap.get("cond");
				   String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号
				   String flag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				   
				    
					String tempTableName =""; 
					if(complexWhlMap.get(pre.toLowerCase()+"_whl")==null)
					{
						int infoGroup = 0; // forPerson 人员
						int varType = 8; // logic	
						  
						cond=cond.replaceAll("归属日期\\(\\)","#"+ff_date+"#");
						cond=cond.replaceAll("归属日期","#"+ff_date+"#");
						 
						String whereIN="";
						if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName()))
						{
							whereIN=InfoUtils.getWhereINSql(this.userview,pre);
							whereIN="select "+pre+"A01.a0100 from "+pre+"A01 where 1=1 "+this.salaryTemplateBo.getWhlByUnits(pre+"A01",false);	
						}
						if("1".equals(flag))
						{
							whereIN=InfoUtils.getWhereINSql(this.userview,pre);
							whereIN="select "+pre+"A01.a0100 from "+pre+"A01 where 1=1 "+this.salaryTemplateBo.getWhlByUnits(pre+"A01",false); 	
						}
						ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			//			alUsedFields.addAll(this.getMidVariableList());   没有意义吧
						YksjParser yp = new YksjParser(this.userview ,alUsedFields,
								YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString()); 
						YearMonthCount ymc=null;							
						yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
						tempTableName = yp.getTempTableName();
						w = yp.getSQL();
						
						if(w.trim().length()<3||yp.isFError())
							throw GeneralExceptionHandler.Handle(new Exception(this.salaryTemplateBo.getTemplatevo().getString("cname")+" 定义的人员范围有误！"));
					}
					else
					{
					 
							tempTableName="t#"+this.userview.getUserName()+"_gz_"+pre.toLowerCase()+"_cond";
							w=(String)complexWhlMap.get(pre.toLowerCase()+"_whl"); 
					}
					
					
					if(w!=null&&w.trim().length()>0)
					{ 
							return  " and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+pre+"A01.a0100 and ( "+w+" ))"; 
					}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}		
			return w;
			
	}
	
	
	
	/**
	 * 导入新增人员的数据
	 * @param flag  true: 手工引入|变动比对引入      
	 * @param ff_date  发放日期
	 * @param count  发放次数
	 * @param itemList  薪资项目
	 * @param isImportMen  来自人员引入模块调用 
	 * @throws GeneralException
	 */
	public void importAddManData(boolean flag,String ff_date,String count,ArrayList itemList,boolean isImportMen)throws GeneralException
	{
		
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";  //新增人员临时表
		try
		{
				 ContentDAO dao=new ContentDAO(this.conn);
				 RowSet rowSet=dao.search("select count(a0100) from "+tablename);
				 int rows=0;
				 if(rowSet.next())
					 rows=rowSet.getInt(1);
				if(rows==0)
					return;
				LazyDynaBean	busiDate=new LazyDynaBean(); //业务日期 次数   date:2010-03-01   count:1
				busiDate.set("date",ff_date);
				busiDate.set("count",count);

				DbWizard dbw=new DbWizard(this.conn);
				RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
				String gz_tablename=this.salaryTemplateBo.getGz_tablename();  //薪资发放临时表名
				int salaryid=templateVo.getInt("salaryid");
				String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
				ArrayList setlist=this.salaryTemplateBo.searchSetList(salaryid);
				SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ",");
				String strset=null;
				String strc=null;
				String strpre=null;
				String pay_flag=ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);  //发薪标识
				StringBuffer buf=new StringBuffer();
				StringBuffer strWhere=new StringBuffer();


				String expception_str="'A00Z1','A00Z0','A00Z2','A00Z3'";
				boolean noPreData=false;
				//如果是新建工资时，并且没有设置自动比对，则单位，部门，岗位取上期记录 zhanghua 2018-3-7
				if(!isImportMen&&!flag&&(StringUtils.isBlank(SystemConfig.getPropertyValue("noPreData_gz"))|| "false".equalsIgnoreCase(SystemConfig.getPropertyValue("noPreData_gz").trim())))  //新建工资表自动变动比对,其实就是不取上月数据
					noPreData=true;
				if(noPreData) {
					expception_str += ",'E0122','B0110','A0101'";
				}
				RecordVo vo;
				String fieldName="",fieldsetid="";
				for(int i=0;i<dbarr.length;i++)
				{
					strpre=dbarr[i];
					for(int j=0;j<setlist.size();j++)
					{
						strset=(String)setlist.get(j);
						if(strset.toUpperCase().charAt(0)=='A')
							strc=strpre+strset;
						else
							strc=strset;
						if("a00".equalsIgnoreCase(strset))
							continue;
						vo=new RecordVo(strc);
						for(int z=0;z<itemList.size();z++){
							fieldsetid=(String)((LazyDynaBean)itemList.get(z)).get("fieldsetid");
							if(!fieldsetid.equalsIgnoreCase(strset))
								continue;
							fieldName=(String)((LazyDynaBean)itemList.get(z)).get("itemid");
							if(!vo.hasAttribute(fieldName.toLowerCase()))
							{
								throw GeneralExceptionHandler.Handle(new Exception("子集中不存在 "+(String)((LazyDynaBean)itemList.get(z)).get("itemdesc")+"  指标,请到类别维护进行结构同步后再使用 !"));
							}
						}
					}
				}
				
				for(int i=0;i<dbarr.length;i++)
				{
					strpre=dbarr[i];
					buf.setLength(0);
					for(int j=0;j<setlist.size();j++)
					{
						strset=(String)setlist.get(j);
						/**NBASE,A00Z0,A00Z1,A00Z2,A00Z3的子集代码为A00*/
						if("A00".equalsIgnoreCase(strset))
							continue;
						/**先导入人员主集*/
						if(strset.toUpperCase().charAt(0)=='A')
							strc=strpre+strset;
						else
							strc=strset;
						if("A01".equalsIgnoreCase(strset))
						{ 
							boolean  payFlag_isExist=false;
							LazyDynaBean abean=null;
							
							if(StringUtils.isNotBlank(pay_flag))
								for(int e=0;e<itemList.size();e++)
								{
									abean=(LazyDynaBean)itemList.get(e);
									String intflag=(String)abean.get("initflag"); //0：输入项	1：累积项 	2：导入    3：系统项 
									String itemid=(String)abean.get("itemid");
									if("0".equals(intflag))
										continue;
									if(pay_flag!=null&&pay_flag.equalsIgnoreCase(itemid)){
										payFlag_isExist=true;
										break;
									}
								} 
							
							String strlst=getInsFieldSQL(strset,itemList,expception_str+",'"+pay_flag+"'",strc);
							buf.append("insert into ");
							buf.append(gz_tablename);
							buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
							if(this.salaryTemplateBo.getManager().length()>0)
								buf.append("sp_flag2,");
							if(payFlag_isExist&&pay_flag.length()!=0)
							{
								
								buf.append(pay_flag);
								buf.append(",");
							}
							if(noPreData) {
								buf.append("E0122,B0110,A0101,");
							}
							buf.append(getInsFieldSQL(strset,itemList,expception_str+",'"+pay_flag+"'",""));
							buf.append(") select '");
							buf.append(this.userview.getUserName());
							buf.append("','" );
					//		buf.append(strpre.toUpperCase());
							buf.append(strpre);
							buf.append("',"+Sql_switcher.dateValue(ff_date)+","+count+","+Sql_switcher.dateValue(ff_date)+",");
							buf.append(count);
							buf.append(",'01',");
							if(this.salaryTemplateBo.getManager().length()>0)
								buf.append("'01',");
							if(payFlag_isExist&&pay_flag.length()!=0)
							{
								buf.append("'0',");
							}
							if(noPreData) {
								buf.append(tablename).append(".E0122,").append(tablename).append(".B0110,").append(tablename).append(".A0101,");
							}
							buf.append(strlst); 
							buf.append(" from ");
							buf.append(strc);
							if(noPreData) {
								buf.append(" inner join ").append(tablename).append(" on ").append(tablename).append(".state='1' and lower(")
										.append(tablename).append(".dbname)='").append(strpre.toLowerCase());
								buf.append("' and " + tablename + ".a0100=" + strc + ".a0100");
							}else {
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)='");
								buf.append(strpre.toLowerCase());
								buf.append("' and "+tablename+".a0100="+strc+".a0100   )");
							}
							if(isImportMen)  //如果是人员引入模块
							{
								buf.append(" and  not exists (select null from ");
								buf.append(gz_tablename+" where "+gz_tablename+".a0100="+strc+".a0100 ");
								buf.append(" and lower("+gz_tablename+".nbase)='"+strpre.toLowerCase()+"' ) ");
								
								StringBuffer buf2=new StringBuffer("update "+tablename+" set isFlag='1' where ");
								buf2.append(" exists (select null from ");
								buf2.append(gz_tablename+" where "+gz_tablename+".a0100="+tablename+".a0100 ");
								buf2.append(" and lower("+gz_tablename+".nbase)='"+strpre.toLowerCase()+"' ) and  lower("+tablename+".DBNAME)='"+strpre.toLowerCase()+"'");
								dbw.execute(buf2.toString());  
							} 
							dbw.execute(buf.toString()); 
							
							if(isImportMen)  //如果是人员引入模块调用
							{
								buf.setLength(0);
								buf.append("insert into ");
								buf.append(gz_tablename);
								buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
								if(this.salaryTemplateBo.getManager().length()>0)
									buf.append("sp_flag2,");
								if(payFlag_isExist&&pay_flag.length()!=0)
								{
									buf.append(pay_flag);
									buf.append(",");
								}
								buf.append(getInsFieldSQL(strset,itemList,expception_str+",'"+pay_flag+"'",""));
								buf.append(") select '");
								buf.append(this.userview.getUserName()+"','" );
								//buf.append(strpre.toUpperCase());
								buf.append(strpre);//以头字母大写 后面小写的原式格式插入 zhanghua 2017-4-21
								buf.append("',");
								buf.append(Sql_switcher.dateValue(ff_date));
								buf.append(","+count+",");
								buf.append(Sql_switcher.dateValue(ff_date));
								buf.append(",");
								buf.append("b.count");
								buf.append(",'01',");
								if(this.salaryTemplateBo.getManager().length()>0)
									buf.append("'01',");
								if(payFlag_isExist&&pay_flag.length()!=0)
								{
									buf.append("'0',");
								}
								buf.append(strlst);
								buf.append(" from ");
								buf.append(strc);
								buf.append(",(select max(a00z1)+1 count ,a0100 from "+gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"' and a0100 in (select A0100 from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)='");
								buf.append(strpre.toLowerCase());
								buf.append("') group by a0100) b");
								buf.append(" where  "+strc+".a0100=b.a0100  and  "+strc+".A0100 in (select A0100 from ");
								buf.append(tablename);
								buf.append(" where state='1' and isFlag='1'  and lower(dbname)='");
								buf.append(strpre.toLowerCase());
								buf.append("')");
								dbw.execute(buf.toString());
							}
							
							/** 如果发现历史表中有当月相同的次数，归属次数就自动加1 */
							if(flag)  //手工引入|变动比对引入      
							{
								DbNameBo.autoAddZ1(this.conn, this.userview,gz_tablename,String.valueOf(salaryid),this.salaryTemplateBo.getManager(),false,false);
							}
							
							
						}//主集处理结束
						else
						{
							/**人员信息集*/
							if(strset.charAt(0)=='A')
							{
						
							    String strupdate=getUpdateFieldSQL(strc, gz_tablename, strset,itemList);
								
								if(strupdate.length()==0)
									continue;
								String temp1="(select * from "+strc+" a where a.i9999=(select max(b.i9999) from "+strc+" b where a.a0100=b.a0100  ) ) "+strc;
									
								strWhere.setLength(0);
								strWhere.append(" exists (select null from ");
								strWhere.append(tablename);
								strWhere.append(" where "+gz_tablename+".a0100="+tablename+".a0100  and state='1' and lower(dbname)='");
								strWhere.append(strpre.toLowerCase());
								strWhere.append("') and upper(");
								strWhere.append(gz_tablename);
								strWhere.append(".nbase)='");
								strWhere.append(strpre.toUpperCase());
								strWhere.append("'");
								if(isImportMen)
								{
									strWhere.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
									strWhere.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
								}
								dbw.updateRecord(gz_tablename,temp1,gz_tablename+".A0100="+strc+".A0100", strupdate, strWhere.toString(), "");
							}
						}
					}//for j loop end.
					
					
					/**计算导入项和累积项*/
					if(isImportMen)
					{ 
						if(!dbw.isExistField(tablename, "a00z1",false))
						{
							Table table=new Table(tablename);
							Field field=new Field("a00z1","a00z1");
							field.setDatatype(DataType.INT); 		
							table.addField(field);
							dbw.addColumns(table);
							DbSecurityImpl dbS = new DbSecurityImpl();
							dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
						}
						
						String temp_sql="update "+tablename+" set a00z1=(select a00z1 from (select max(a00z1) a00z1,max(nbase) nbase,a0100 from "+gz_tablename+" group by a0100 ) a "
						+" where a.a0100="+tablename+".a0100   and lower(a.nbase)=lower("+tablename+".dbname)   ) where  lower(dbname)='"+strpre.toLowerCase()+"' ";
						dbw.execute(temp_sql);
						
						strWhere.setLength(0);
						strWhere.append(" exists (select null from "+tablename+" where "+gz_tablename+".a0100="+tablename+".a0100 and state='1' and lower(dbname)='");
						strWhere.append(strpre.toLowerCase());
					    strWhere.append("' and lower("+tablename+".dbname)=lower("+gz_tablename+".nbase)   and "+tablename+".a00z1="+gz_tablename+".a00z1  )");
					}
					else
					{
						strWhere.setLength(0);
						strWhere.append(" exists (select null from ");
						strWhere.append(tablename);
						strWhere.append(" where "+gz_tablename+".a0100="+tablename+".a0100 and state='1' and lower(dbname)='");
						strWhere.append(strpre.toLowerCase());
						strWhere.append("')");
		
					}
					String importMenSql_where="";
					importMenSql_where+=" select A0100 from ";
					importMenSql_where+=tablename;
					importMenSql_where+=" where state='1' and upper(dbname)='";
					importMenSql_where+=strpre.toUpperCase();
					importMenSql_where+="'"; 
					
					//有记录才进行下一步计算 
					rowSet=dao.search("select count(nbase) from "+gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"'");
					if(rowSet.next())
					{
						if(rowSet.getInt(1)>0)
							firstComputing(strWhere.toString(), strpre,true,new ArrayList(),itemList,importMenSql_where,busiDate);
					}
					PubFunc.closeDbObj(rowSet);
				}//for i loop end.
				 
				//记录单位、部门、人员库排序序号
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set b0110_o=(select a0000 from organization where organization.codeitemid="+gz_tablename+".b0110 and organization.codesetid='UN' )");
				buf.append(" where exists (select null from organization where organization.codeitemid="+gz_tablename+".b0110 and organization.codesetid='UN' )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set e0122_o=(select a0000 from organization where organization.codeitemid="+gz_tablename+".e0122 and organization.codesetid='UM' )");
				buf.append(" where exists (select null from organization where organization.codeitemid="+gz_tablename+".e0122 and organization.codesetid='UM' )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+gz_tablename+" set dbid=(select dbid from dbname where upper(dbname.pre)=upper("+gz_tablename+".nbase)  )");
				buf.append(" where exists (select null from dbname where upper(dbname.pre)=upper("+gz_tablename+".nbase) )");
				dao.update(buf.toString());
				
				//同步薪资发放数据的映射表
				if(flag) //手工引入|变动比对引入      
				{
					 impGzMappingData(tablename ,gz_tablename,dao,String.valueOf(salaryid));
				} 
				if(flag)//手工引入|变动比对引入人员时,非管理员需自动为引入人员的归属单位\部门赋值;      
				{
					autoImpUnticode(gz_tablename,tablename,dao,ctrlparam);
				}
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	
	
	
	/**
	 * 首次计算
	 * @param strWhere 条件
	 * @param strPre   应用库前缀
	 * @param bMain    =true 人员主集指标也导，=false 人员主集指标不导
	 * @param exceptFlds 不导数据的指标 暂时未用到
	 * @param busiDate 业务日期 次数
	 * @return
	 */
	public boolean firstComputing(String strWhere,String strPre,boolean bMain,ArrayList exeptFlds,ArrayList gzItemList,String  importMenSql_where,LazyDynaBean busiDate)throws GeneralException
	{
		  
		boolean bflag=true;
		int ninit=0;
		try
		{
			String gz_tablename=this.salaryTemplateBo.getGz_tablename();
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field=null; 
			HashMap fieldMap=new HashMap();
			LazyDynaBean abean=null;
			HashMap allFieldMap=new HashMap();
			 
			boolean isE01A1=false; //是否有岗位名称
			for(int i=0;i<gzItemList.size();i++)
			{
					abean=(LazyDynaBean)gzItemList.get(i);
					String itemid=(String)abean.get("itemid");  
					String itemdesc=(String)abean.get("itemdesc");
					fieldMap.put(itemdesc.toLowerCase(),abean);
					
					if("E01A1".equalsIgnoreCase(itemid))
							isE01A1=true;
			}
			
			int isExept=-1;
			String exeid="";
			for(int i=0;exeptFlds!=null&&i<exeptFlds.size();i++){
				isExept=-1;
				exeid=(String)((LazyDynaBean)exeptFlds.get(i)).get("itemid");
				
				for(int j=0;j<gzItemList.size();j++){
					if(exeid.equalsIgnoreCase((String)((LazyDynaBean)gzItemList.get(j)).get("itemid"))){
						isExept=j;
						break;
					}
				}
				
				if(isExept!=-1)
					gzItemList.remove(isExept);
			}
			
			
			for(int i=0;i<allUsedFields.size();i++)
			{
					Field = (FieldItem)((FieldItem) allUsedFields.get(i)).cloneItem();
					String desc=Field.getItemdesc().trim().toLowerCase();
					if(fieldMap.get(desc)!=null)
					{
							abean=(LazyDynaBean)fieldMap.get(desc);
							Field.setFieldsetid((String)abean.get("fieldsetid"));
					}
					allFieldMap.put(desc,Field);
			}
	
			 
			//将导入当前记录数据的项目先批量处理
			ArrayList itemList=new ArrayList();
			HashMap setMap=new HashMap(); 
			for(int i=0;i<gzItemList.size();i++)
			{
				abean=(LazyDynaBean)gzItemList.get(i); 
				String fieldsetid=(String)abean.get("fieldsetid");
				String itemid=(String)abean.get("itemid");  
				String itemdesc=(String)abean.get("itemdesc");
				String nlock=(String)abean.get("nlock");  
				int intflag=Integer.parseInt((String)abean.get("initflag")); //0：输入项	1：累积项 	2：导入    3：系统项 
				String heapflag=(String)abean.get("heapflag");  
				String formula=(String)abean.get("formula");  
				if(!bMain&& "A01".equalsIgnoreCase(fieldsetid))
					continue;
				/**单位指标或职位指标*/
				if(fieldsetid.charAt(0)=='A')
				{ 
					/**=0不锁,=1锁住    2:导入项  */
					if("0".equals(nlock)&& "0".equals(heapflag)&&(intflag==1||intflag==2))
					{
						if(allFieldMap.get(formula.trim().toLowerCase())!=null||DataDictionary.getFieldItem(formula.trim())!=null) 
						{
							
							FieldItem field=(FieldItem)allFieldMap.get(formula.trim().toLowerCase());
							if(field==null)
								field=(FieldItem)DataDictionary.getFieldItem(formula.trim(),fieldsetid); 
							fieldsetid = field.getFieldsetid();
							if(setMap.get(fieldsetid.toUpperCase())!=null)
							{
									ArrayList tempList=(ArrayList)setMap.get(fieldsetid.toUpperCase());
									tempList.add(itemid+"`"+field.getItemid());
									setMap.put(fieldsetid.toUpperCase(), tempList);
							}
							else
							{
									ArrayList tempList=new ArrayList();
									tempList.add(itemid+"`"+field.getItemid());
									setMap.put(fieldsetid.toUpperCase(), tempList);
							}
						}
						else
							itemList.add(abean);
					}
					else
					{
						itemList.add(abean);
					}
				}
				else
					itemList.add(abean);
			}
			//批量导入当前记录数据的项目
			batchImportGzItems(setMap,strWhere,strPre,gz_tablename);
			 
			if(isE01A1)  //更新岗位指标
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="update "+gz_tablename+" set E01A1=(select E01A1 from "+strPre+"A01 where "+strPre+"A01.a0100="+gz_tablename+".a0100  ) where exists ";
				sql+=" (select null from "+strPre+"A01 where "+strPre+"A01.a0100="+gz_tablename+".a0100  ) and lower(nbase)='"+strPre.toLowerCase()+"' ";
				if(strWhere!=null&&strWhere.trim().length()>0)
					sql+=" and "+strWhere;
				dao.update(sql);
			}
			
			
			
			for(int i=0;i<itemList.size();i++)
			{
				abean=(LazyDynaBean)itemList.get(i);
				String setid=(String)abean.get("fieldsetid");  
				int nlock=Integer.parseInt((String)abean.get("nlock"));  // =0不锁, =1锁住
				int initflag=Integer.parseInt((String)abean.get("initflag")); //0：输入项	1：累积项 	2：导入    3：系统项 
				if(!bMain&& "A01".equalsIgnoreCase(setid))
					continue;
				/**单位指标或职位指标*/
				if(setid.charAt(0)!='A')
				{
					computingImportUnitItem(abean,strWhere,strPre,isE01A1,busiDate);
					//...
				}
				else//人员库
				{ 
					if(nlock==0)
					{ 
						switch(initflag)
						{
						case 0://清零项，不管它
							break;
						case 1:  //累积项
							computingImportItem(abean,strWhere,strPre,importMenSql_where, busiDate);						
							break;
						case 2:  //导入项
							computingImportItem(abean,strWhere,strPre,importMenSql_where,busiDate);
							break;
						}
					}// nlock end.
				}
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		return bflag;
	}
	
	
	
	/**
	 * 计算人员导入项和累积项
	 * @param itemvo 薪资项
	 * @param strWhere 计算的人员范围条件(关联新增人员表)
	 * @param strPre 库前缀
	 * @param busiDate 业务日期 次数
	 * @return
	 */
	public boolean computingImportItem(LazyDynaBean abean,String strWhere,String strPre,String importMenSql_where,LazyDynaBean busiDate)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tablename="t#"+this.userview.getUserName()+"_gz"; 
			DbWizard dbw=new DbWizard(this.conn);
			RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
			int salaryid=templateVo.getInt("salaryid");
			String gz_tablename=this.salaryTemplateBo.getGz_tablename();
			String fldtype=(String)abean.get("itemtype");  
			String setid=(String)abean.get("fieldsetid");
			String fldname=(String)abean.get("itemid");  
			String itemdesc=(String)abean.get("itemdesc");
			String nlock=(String)abean.get("nlock");  
			int ninit=Integer.parseInt((String)abean.get("initflag")); //0：输入项	1：累积项 	2：导入    3：系统项 
			int nheap=Integer.parseInt((String)abean.get("heapflag"));  
			String formula=(String)abean.get("formula");  
			String itemlength=(String)abean.get("itemlength");
			String decwidth=(String)abean.get("decwidth");
			String codesetid=(String)abean.get("codesetid");
			
			String stry="";
			String strm="";
			String currcount="";
			
			if(busiDate!=null)
			{
				stry=((String)busiDate.get("date")).substring(0, 4);
				strm=((String)busiDate.get("date")).substring(5, 7);
				currcount=(String)busiDate.get("count");
			} 
			int nM=Integer.parseInt(strm); 
			String axxz1=null;
			String axxz0=null;
			String strQ=null;
			if((nM>=1) && (nM<=3))
				strQ="1";
			else if((nM>=4) && (nM<=6))
				strQ="2";
			else if((nM>=7) && (nM<=9))
				strQ="3";
			else
				strQ="4";
			StringBuffer buf=new StringBuffer();
			
			
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp = new YksjParser(this.userview, allUsedFields,
					YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", strPre);
			yp.setCon(this.conn);
			yp.setStdTmpTable(gz_tablename);
			yp.setStdTmpTable_where(strWhere);
			/**对累积项,组成分析用的计算公式*/
			if(ninit==1)
			{
				axxz1=setid+"Z1";//所属次数指标
				axxz0=setid+"Z0";//所属期指标
				switch(nheap)
				{
				case 0://不累积
					break;
				case 1://月内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",Month(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strm);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(",SUM)");
					break;
				case 2://季度内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",QUARTER(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strQ);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(",SUM)");					
					break;
				case 3://年内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(",SUM)");
					break;
				case 4://无条件累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",1=1");
					buf.append(",SUM)");					
					break;
				case 5://季度内同次累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",QUARTER(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strQ);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);	
					buf.append(" and ");
					buf.append(axxz1);
					buf.append("=");
					buf.append(currcount);	
					buf.append(",SUM)");
					break;
				case 6://年内同次累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);	
					buf.append(" and ");
					buf.append(axxz1);
					buf.append("=");
					buf.append(currcount);	
					buf.append(",SUM)");					
					break;
				case 7://同次累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",");
					buf.append(axxz1);
					buf.append("=");
					buf.append(currcount);	
					buf.append(",SUM)");						
					break;
				case 8://小于本次的月内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",Month(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strm);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(" and ");
					buf.append(axxz1);
					buf.append("<");
					buf.append(currcount);
					buf.append(",SUM)");					
					break;
				}
				formula=buf.toString();
			}//if ninit end.
			else if(ninit==2)
			{
				
				axxz1=setid+"Z1";//所属次数指标
				axxz0=setid+"Z0";//所属期指标
				if(nheap==1||nheap==2||nheap==3||nheap==4)
				{
					try
					{
						ArrayList a_fieldlist=yp.getFormulaFieldList(formula);
						HashSet aset=new HashSet();
						String asetid="";
						for(int i=0;i<a_fieldlist.size();i++)
						{
							FieldItem field=(FieldItem)a_fieldlist.get(i);
							aset.add(field.getFieldsetid());
							asetid=field.getFieldsetid();
						}
						if(aset.size()==1)
						{
							axxz1=asetid+"Z1";//所属次数指标
							axxz0=asetid+"Z0";//所属期指标
						}
					}
					catch(Exception ee)
					{
						ee.printStackTrace();
						return bflag;
					}
				
				}
				
				switch(nheap)
				{
					case 0:// 当前记录
						break;
					case 1:  // 月内最初第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry+",FIRST)";
						 break;
					case 2:  // 月内最近第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry+",LAST)";
						 break;
					case 3:  // 小于本次月内最初第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry
						                          +" AND "+axxz1+"<"+currcount+",FIRST)";
						break;
					case 4:  // 小于本次月内最近第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry
													+" AND "+axxz1+"<"+currcount+",LAST)";
						break;
				}
				
			}
			/**公式计算*/
			ArrayList usedlist=initUsedFields();
			
			ArrayList fieldlist=null;
			try
			{
				fieldlist=yp.getFormulaFieldList(formula);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return bflag;
			}
			
			/**追加公式中使用的指标*/
			appendUsedFields(fieldlist,usedlist);
			/**增加一个计算公式用的临时字段*/
			FieldItem fielditem=new FieldItem("A01","AAAAA");
			fielditem.setItemdesc("AAAAA");
			fielditem.setCodesetid("0");
			fielditem.setItemtype(fldtype);
			fielditem.setItemlength(Integer.parseInt(itemlength));
			fielditem.setDecimalwidth(Integer.parseInt(decwidth));
			usedlist.add(fielditem);
			yp.setTargetFieldDecimal(fielditem.getDecimalwidth());
			/**创建计算用临时表*/
			if(createMidTable(usedlist,tablename,"A0100"))
			{
				/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
				buf.append(strPre+"A01");
				buf.append(" where exists (select A0100 from ");
				buf.append(gz_tablename);
				if(strWhere.length()==0)
				{
					buf.append(" where upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				else
				{
					buf.append(" where ");					
					buf.append(strWhere);
					buf.append(" and upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				buf.append(" and "+strPre+"A01.a0100="+gz_tablename+".a0100 ");
				
				buf.append(")");
				dao.update(buf.toString());
			}// 创建临时表结束.
			/**执行返回的SQLS*/

			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(currcount));
			
			if(("归属日期()".equalsIgnoreCase(formula.trim())|| "归属日期".equalsIgnoreCase(formula.trim()))&& "D".equalsIgnoreCase(fldtype))
			{
				Calendar d=Calendar.getInstance();
				d.set(Calendar.YEAR,ymc.getYear());
				d.set(Calendar.MONTH,ymc.getMonth()-1);
				d.set(Calendar.DATE,1);
				java.sql.Date dd=new java.sql.Date(d.getTimeInMillis());
				ArrayList paramList = new ArrayList();
				paramList.add(dd);
				dao.update("update "+tablename+" set AAAAA=? ",paramList);
			}
			else
			{
				//importMenSql_where="";
				if(nheap==5&&ninit==2) //导入公式 同月同次
					yp.setYearMonthCount(true);
				yp.run(formula,ymc,"AAAAA",tablename,dao,importMenSql_where,this.conn,fldtype,fielditem.getItemlength(),1,codesetid);
				yp.setYearMonthCount(false);
			}

			
			buf.setLength(0);
			if(strWhere.length()==0)
			{
				buf.append(" upper(nbase)='");
				buf.append(strPre.toUpperCase());
				buf.append("'");
			}
			else
			{
				buf.append(strWhere);
				buf.append(" and upper(nbase)='");
				buf.append(strPre.toUpperCase());
				buf.append("'");
			}
			
			//String a_buf=buf.toString().replace("where"," ");   //dengcan  2008-02-03  添加where,报错
			//String strcond=buf.substring(6);
			dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100", gz_tablename+"."+fldname+"="+tablename+".AAAAA", buf.toString(), "");
	
			 if(ninit==2&&nheap==6) //导入项|扣减同月已发金额
			 {
				// 多发的说法
				 StringBuffer sql_str=new StringBuffer("");
				 sql_str.append("update "+gz_tablename+" set "+fldname+"=(select ("+gz_tablename+"."+fldname+"-"+Sql_switcher.isnull("a."+fldname, "0")+") ");
				 sql_str.append(" from ( select sum(salaryhistory."+fldname+") as "+fldname+",a0100,nbase from salaryhistory where salaryid="+salaryid+" "); 
				 sql_str.append(" and "+Sql_switcher.year("a00z2")+"="+stry+" and "+Sql_switcher.month("a00z2")+"="+nM+" and sp_flag='06'  group  by a0100,nbase   ) a ");
				 sql_str.append(" where a.a0100="+gz_tablename+".a0100 and lower(a.nbase)=lower("+gz_tablename+".nbase) ) ");
				 sql_str.append("  where exists (  select null  from ( ");
				 sql_str.append(" select sum(salaryhistory."+fldname+") as "+fldname+",a0100,nbase from salaryhistory where salaryid="+salaryid+"  ");
				 sql_str.append(" and "+Sql_switcher.year("a00z2")+"="+stry+" and "+Sql_switcher.month("a00z2")+"="+nM+" and sp_flag='06'  group  by a0100,nbase   ) a ");
				 sql_str.append("  where a.a0100="+gz_tablename+".a0100 and lower(a.nbase)=lower("+gz_tablename+".nbase) ");
				 sql_str.append("  ) ");
				 dbw.execute(sql_str.toString());
			 }
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	/**
	 * 计算单位导入项和累积项
	 * @param itemvo
	 * @param strWhere
	 * @param strPre
	 * @param isE01A1 是否已导入岗位名称数据
	 * @param busiDate 业务日期 次数
	 * @return
	 * @throws GeneralException
	 */
	public boolean computingImportUnitItem(LazyDynaBean abean,String strWhere,String strPre,boolean isE01A1,LazyDynaBean busiDate)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
			int salaryid=templateVo.getInt("salaryid");
			String gz_tablename=this.salaryTemplateBo.getGz_tablename(); 
			String nbase=strPre;
			strPre="";
			String setid=(String)abean.get("fieldsetid");    
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			String tablename="t#"+this.userview.getUserName()+"_gz";  
			DbWizard dbw=new DbWizard(this.conn);
			String itemid=(String)abean.get("itemid");  
			String formula=(String)abean.get("formula");   
			String itemtype=(String)abean.get("itemtype");  
			String itemlength=(String)abean.get("itemlength");
			String decwidth=(String)abean.get("decwidth");
			String codesetid=(String)abean.get("codesetid");
			/**公式计算*/
			ArrayList usedlist=initUsedFields();
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp =null;
			if(setid.charAt(0)=='K')
				yp=new YksjParser(this.userview, allUsedFields,
						YksjParser.forSearch, getDataType(itemtype), YksjParser.forPosition, "Ht", strPre);
			else
				yp=new YksjParser(this.userview, allUsedFields,
					YksjParser.forSearch, getDataType(itemtype), YksjParser.forUnit, "Ht", strPre);
			yp.setCon(this.conn);
			ArrayList fieldlist=null;
			try
			{
				fieldlist=yp.getFormulaFieldList(formula);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return bflag;
			}
			
			
			//yp.run(formula);
			/**追加公式中使用的指标*/
			appendUsedFields(fieldlist,usedlist);
			/**增加一个计算公式用的临时字段*/
			FieldItem fielditem=new FieldItem("A01","AAAAA");
			fielditem.setItemdesc("AAAAA");
			fielditem.setCodesetid("0");
			fielditem.setItemtype(itemtype);
			fielditem.setItemlength(Integer.parseInt(itemlength));
			
			fielditem.setDecimalwidth(Integer.parseInt(decwidth));  // FixBug:0033205 导入单位指标无小数问题
			yp.setTargetFieldDecimal(fielditem.getDecimalwidth());     // FixBug:0033205
			usedlist.add(fielditem);
			/**创建计算用临时表*/
			if(setid.charAt(0)=='K')
			{
				fielditem=new FieldItem("A01","E01A1");
				fielditem.setItemdesc("职位名称");
				fielditem.setCodesetid("@K");
				fielditem.setItemtype("A");
				fielditem.setItemlength(30);
				fielditem.setDecimalwidth(0);
				usedlist.add(fielditem);
				if(createMidTable(usedlist,tablename,"E01A1"))
				{
					/**导入单位主集数据B0110*/
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(E01A1) select E01A1 FROM K01");
					dao.update(buf.toString());
				}// 创建临时表结束.
			}
			else
			{
				if(createMidTable(usedlist,tablename,"B0110"))
				{
					/**导入单位主集数据B0110*/
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(B0110) select B0110 FROM B01");
					dao.update(buf.toString());
				}// 创建临时表结束.
			}
			
			YearMonthCount ymc=null; 
			if(busiDate!=null)
			{
				String stry=((String)busiDate.get("date")).substring(0, 4);
				String strm=((String)busiDate.get("date")).substring(5, 7);
				String strc=(String)busiDate.get("count");
				ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			}
			yp.run(formula,ymc,"AAAAA",tablename,dao,"",this.conn,itemtype,fielditem.getItemlength(),1,codesetid);
			
			if(setid.charAt(0)=='K')
			{
				if(!isE01A1) //薪资类别没有引入岗位指标时通过人员库信息匹配
				{ 
					StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+itemid+"=(select  "+tablename+".AAAAA from "+tablename);
					sql.append(","+nbase+"a01 where "+gz_tablename+".a0100="+nbase+"a01.a0100 and  "+nbase+"a01.E01A1="+tablename+".E01A1 and lower("+gz_tablename+".nbase)='"+nbase.toLowerCase()+"' ) where exists (select null from "+tablename);
					sql.append(","+nbase+"a01 where "+gz_tablename+".a0100="+nbase+"a01.a0100 and  "+nbase+"a01.E01A1="+tablename+".E01A1 and lower("+gz_tablename+".nbase)='"+nbase.toLowerCase()+"' ) and lower("+gz_tablename+".nbase)='"+nbase.toLowerCase()+"' ");
					if(strWhere!=null&&strWhere.length()>0)
						sql.append(" and "+strWhere);
					dbw.execute(sql.toString());
				}
				else
				{
					StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+itemid+"=(select  "+tablename+".AAAAA from "+tablename);
					sql.append(" where "+gz_tablename+".E01A1="+tablename+".E01A1 ) where exists (select null from "+tablename);
					sql.append(" where "+gz_tablename+".E01A1="+tablename+".E01A1 ) ");
					if(strWhere!=null&&strWhere.length()>0)
						sql.append(" and "+strWhere);
					dbw.execute(sql.toString());
				}
			}
			else
			{ 
				// 先处理部门，再处理单位，即，部门有值用部门值，部门没值用单位值。(FixBug0033282)
				StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+itemid+"=NULL");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" where "+strWhere);
				dbw.execute(sql.toString());
				// 部门
				String cond=null;
				if("N".equalsIgnoreCase(itemtype))
					cond="AAAAA<>0";
				else if("D".equalsIgnoreCase(itemtype))
					cond="NOT AAAAA IS NULL";
				else
				{
					if(Sql_switcher.searchDbServer() < Constant.ORACEL)
						cond="AAAAA<>''";
					else
						cond="NOT AAAAA IS NULL";
				}
				sql.setLength(0);
				sql.append("update "+gz_tablename+" set "+itemid+"="+
						     "(select  "+tablename+".AAAAA from "+tablename+
				             " where "+gz_tablename+".E0122="+tablename+".B0110 and "+cond+")"+
				           " where exists (select null from "+tablename+
				                          " where "+gz_tablename+".E0122="+tablename+".B0110)");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());

				// 单位
				cond=gz_tablename+"."+itemid+" IS NULL";
				sql.setLength(0);
				sql.append("update "+gz_tablename+" set "+itemid+"="+
						        "(select  "+tablename+".AAAAA from "+tablename+
				                " where "+gz_tablename+".B0110="+tablename+".B0110)"+
				           " where "+cond+" and exists (select null from "+tablename+
				                          " where "+gz_tablename+".B0110="+tablename+".B0110)");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;		
	}
	

	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'M':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	
	/**
	 * 创建计算用的临时表
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
		//	if(dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
   /**
	 * 追加不同的指标
	 * @param slist
	 * @param dlist
	 */
	private void appendUsedFields(ArrayList slist,ArrayList dlist)
	{
		boolean bflag=false;
		for(int i=0;i<slist.size();i++)
		{
			FieldItem fielditem=(FieldItem)slist.get(i);
			String itemid=fielditem.getItemid();
			for(int j=0;j<dlist.size();j++)
			{
				bflag=false;
				FieldItem fielditem0=(FieldItem)dlist.get(j);
				String ditemid=fielditem0.getItemid();
				if(itemid.equalsIgnoreCase(ditemid))
				{
					bflag=true;
					break;
				}

			}//for j loop end.
			if(!bflag)
				dlist.add(fielditem);			
		}//for i loop end.
	}
	
	/**
	 * 初始设置使用字段列表
	 * @return
	 */
	private ArrayList initUsedFields()
	{
		ArrayList fieldlist=new ArrayList();
		/**人员排序号*/
		FieldItem fielditem=new FieldItem("A01","A0000");
		fielditem.setItemdesc("a0000");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**人员编号*/
		fielditem=new FieldItem("A01","A0100");
		fielditem.setItemdesc("a0100");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(8);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**单位名称*/
		fielditem=new FieldItem("A01","B0110");
		fielditem.setItemdesc("单位名称");
		fielditem.setCodesetid("UN");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**姓名*/
		fielditem=new FieldItem("A01","A0101");
		FieldItem item=DataDictionary.getFieldItem("a0101");
		fielditem.setItemdesc("姓名");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(item.getItemlength());
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**人员排序号*/
		fielditem=new FieldItem("A01","I9999");
		fielditem.setItemdesc("I9999");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**部门名称*/
		fielditem=new FieldItem("A01","E0122");
		fielditem.setItemdesc("部门");
		fielditem.setCodesetid("UM");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);		
		return fieldlist;
	}
	
	/**
	 * 批量导入当前记录数据的项目
	 * @param setMap
	 */
	public  boolean batchImportGzItems(HashMap setMap,String strWhere,String strPre,String gz_tablename) throws GeneralException
	{
		boolean bflag=true;
		try
		{
			Set set=setMap.keySet();
			DbWizard dbw=new DbWizard(this.conn);
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				
				ArrayList itemList=(ArrayList)setMap.get(key); 
				if(!(key.charAt(0)=='A'))
					continue;
				/**end*/				
				String tablename=strPre+key;
				StringBuffer updStr=new StringBuffer("");
				for(int i=0;i<itemList.size();i++)
				{
					String temp=(String)itemList.get(i);
					String[] temps=temp.split("`");
					updStr.append("`"+gz_tablename+"."+temps[0]+"="+tablename+"."+temps[1]);
				}
				
				StringBuffer buf=new StringBuffer("");
//				if(strWhere.length()==0)
//				{
//					buf.append(" upper(nbase)='");
//					buf.append(strPre.toUpperCase());
//					buf.append("'");
//				}
//				else
//				{
//					buf.append(strWhere);
//					buf.append(" and upper(nbase)='");
//					buf.append(strPre.toUpperCase());
//					buf.append("'");
//				}
				 
				String srcTab=tablename;
				if(!"A01".equalsIgnoreCase(key))
				{
					srcTab="(select * from "+tablename+" a where a.i9999=(select max(b.i9999) from "+tablename+" b where a.a0100=b.a0100  ) ) "+tablename;
				}
				String joinStr=gz_tablename+".A0100="+tablename+".A0100"; 
//				dbw.updateRecord(gz_tablename,srcTab,joinStr,updStr.substring(1), buf.toString(),buf.toString()); 
				
				StringBuffer strSql=new StringBuffer();
				if(Sql_switcher.searchDbServer() == 2){//是oracle
					if(strWhere.length()==0)
					{
						buf.append(" upper(nbase)='");
						buf.append(strPre.toUpperCase());
						buf.append("'");
					}
					else
					{
						buf.append(strWhere);
						buf.append(" and upper(nbase)='");
						buf.append(strPre.toUpperCase());
						buf.append("'");
					}
					
					strSql.append("update "+gz_tablename+" set (");
					String str1="",str2="";
					for(int i=0;i<itemList.size();i++)
					{
						String temp=(String)itemList.get(i);
						String[] temps=temp.split("`");
						str1+=temps[0]+",";
						str2+=tablename+"."+temps[1]+",";
					}
					strSql.append(str1.substring(0, str1.length()-1)+")");
					strSql.append("=(select "+str2.substring(0, str2.length()-1)+" from "+srcTab+" where "+joinStr+" and "+buf.toString()+")");
					strSql.append(" where "+buf.toString());
				}
				else{
					buf.append(" upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
					strSql.append("update "+gz_tablename+" set ");
					for(int i=0;i<itemList.size();i++)
					{
						String temp=(String)itemList.get(i);
						String[] temps=temp.split("`");
						strSql.append(gz_tablename+"."+temps[0]+"="+tablename+"."+temps[1]+",");
					}
					strSql.deleteCharAt(strSql.length()-1);
					strSql.append(" from "+gz_tablename);
					
					strSql.append(" left join "+srcTab+" on "+gz_tablename+".A0100="+tablename+".A0100 where "+buf.toString()+" and "+strWhere);
					
//					strSql.append(" from (select * from "+gz_tablename+" where "+strWhere+") as "+gz_tablename+" left join "+srcTab+" on "+joinStr);
//					strSql.append(" where "+buf.toString());sql2000 不支持此种写法
				}
				dbw.execute(strSql.toString());
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	

	/**
	 * 引入数据时，非管理员需自动附上操作单位的值
	 * @param tablename
	 * @param dao
	 */
	private void autoImpUnticode(String gz_tablename,String tablename,ContentDAO dao,SalaryCtrlParamBo ctrlparam)
	{
		try
		{
			StringBuffer buf=new StringBuffer(""); 
			String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			orgid = orgid != null ? orgid : "";
			String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			deptid = deptid != null ? deptid : "";
			String unitcodes=this.userview.getUnitIdByBusi("1");  //UM010101`UM010105`
		    String manager=this.salaryTemplateBo.getManager();
			if(!(manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager)&&(orgid.length()>0||deptid.length()>0)))
					return ;
			
			if(unitcodes!=null&& "UN`".equalsIgnoreCase(unitcodes))
			{
				
			}
			else
			{
				if(unitcodes==null||unitcodes.length()==0|| "UN".equalsIgnoreCase(unitcodes))
				{
					String a_code=""; 
					if(!this.userview.isSuper_admin())
					{
						if("@K".equals(this.userview.getManagePrivCode()))
							a_code=getUnByPosition(this.userview.getManagePrivCodeValue());
						else
							a_code=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					}
					if(a_code.length()>0)
						unitcodes=a_code+"`";
					else
						unitcodes=""; 
				} 
				if(unitcodes.length()>0)
				{
					String[] temps=unitcodes.split("`");
					for(int i=0;i<temps.length;i++)
					{
						String value=temps[i];
						String code=value.substring(0,2);
						String codevalue=value.substring(2);
					
						if(orgid.length()>0&&deptid.length()>0)
						{
							if("UN".equalsIgnoreCase(code))
							{
								buf.setLength(0);
								buf.append("update "+gz_tablename+" set "+orgid+"='"+codevalue+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
								
								dao.update(buf.toString());
								if(unitcodes.toUpperCase().indexOf("UM")==-1)
								{
									buf.setLength(0);
									buf.append("update "+gz_tablename+" set "+deptid+"=null");
									buf.append(" where exists (select null from ");
									buf.append(tablename);
									buf.append(" where state='1' and lower(dbname)=lower(");
									buf.append(gz_tablename);
									buf.append(".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
									
									buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
									buf.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
									
									dao.update(buf.toString());
								}
							}
							else
							{
								buf.setLength(0);
								
								String un_value=getUnvalueByUm(codevalue); 
								buf.append("update "+gz_tablename+" set "+deptid+"='"+codevalue+"',"+orgid+"='"+un_value+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
							}
							break;
						}
						else if(orgid.length()>0)
						{
							if("UN".equalsIgnoreCase(code))
							{
								buf.setLength(0);
								buf.append("update "+gz_tablename+" set "+orgid+"='"+codevalue+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
							}
							else
							{
								buf.setLength(0);
								
								String un_value=getUnvalueByUm(codevalue); 
								buf.append("update "+gz_tablename+" set "+orgid+"='"+un_value+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
							}
							break;
						}
						else if(deptid.length()>0)
						{
						//	if(code.equalsIgnoreCase("UM"))
							{
								buf.setLength(0);
								buf.append("update "+gz_tablename+" set "+deptid+"='"+codevalue+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+gz_tablename+" group by a0100,nbase ) a where a.a0100="+gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+gz_tablename+".nbase and a.a00z1="+gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
								break;
							} 
						} 
						
					} 
				}
			}
		
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		
	}
	
	
	/**
	 * 依据部门得到所属单位
	 * @param um_value  部门ID
	 * @return
	 */
	private String getUnvalueByUm(String um_value)
	{
		String un_value="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			int n=0;
			while(true)
			{
				rset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+um_value+"')");
				if(rset.next())
				{
					String codesetid=rset.getString("codesetid");
					String codeitemid=rset.getString("codeitemid");
					if("UN".equalsIgnoreCase(codesetid))
					{
						un_value=codeitemid;
						break;
					}
					else 
						um_value=codeitemid;
				}
				n++;
				if(n>30)
					break;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return un_value;
	}
	
	
	/**
	 * 根据职位找直属部门
	 * @param codeid
	 * @return
	 */
	private String getUnByPosition(String codeid)
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
			if(rowSet.next())
			{
				str=rowSet.getString("codesetid")+rowSet.getString("codeitemid");
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	
	/**
	 * 将变动比对|手工引入的人员复制到salary_mapping表中
	 * @param tablename 新增人员临时表
	 * @param gz_tablename  薪资发放临时表
	 * @param dao
	 * @param salaryid   帐套ID
	 */
	private  void  impGzMappingData(String tablename ,String gz_tablename,ContentDAO dao,String salaryid)
	{
		 	StringBuffer buf=new StringBuffer("");
			String username=this.userview.getUserName().toLowerCase();
			if(this.salaryTemplateBo.getManager().length()>0)
				username=this.salaryTemplateBo.getManager();		
			try
			{
					if (Sql_switcher.searchDbServer()==1){//sql server 并发资源死锁 一个个删除。不用lower函数
						    String sql="delete from salary_mapping where salaryid="+salaryid+" and userflag='"+username+"' and nbase=? and a0100=?";
						    RowSet rset = dao.search("select count(*) from "+tablename+" where state='1'");
						    int ncount=0;
						    if (rset.next()){
						        ncount=rset.getInt(1);
						    }
						    if (ncount>100){
						        buf.setLength(0);
			                    buf.append("delete from  salary_mapping ");
			                    buf.append(" where  salaryid="+salaryid+" and userflag='"+username+"' and  exists (select null from ");
			                    buf.append(tablename);
			                    buf.append(" where state='1' and dbname=salary_mapping.nbase and "+tablename+".a0100=salary_mapping.a0100   )");
			                    dao.update(buf.toString());  
						    }
						    else {//人少 人员引入并发
						        ArrayList valuesList =new ArrayList();
						        rset = dao.search("select dbname,a0100 from "+tablename+" where state='1'");
						        while (rset.next()){
						            ArrayList paramList =new ArrayList(); 
						            paramList.add(rset.getString("dbname").toLowerCase());
						            paramList.add(rset.getString("a0100"));
						            valuesList.add(paramList);				       
						        }
						        try{
						            dao.batchUpdate(sql, valuesList);
						        }
						        catch(Exception ex)
						        {				        
						        }   
						        finally{
						            valuesList=null;
						        }
						    } 
					}
					else {
					    buf.setLength(0);
					    buf.append("delete from  salary_mapping ");
					    buf.append(" where  salaryid="+salaryid+" and lower(userflag)='"+username+"' and  exists (select null from ");
					    buf.append(tablename);
					    buf.append(" where state='1' and lower(dbname)=lower(salary_mapping.nbase) and "+tablename+".a0100=salary_mapping.a0100   )");
					    dao.update(buf.toString());
					}
					
					buf.setLength(0);
					buf.append("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) ");
					buf.append(" select a0100,nbase,a00z0,a00z1,"+salaryid+",'"+username+"' from "+gz_tablename);
					buf.append(" where exists (select null from ");
					buf.append(tablename);
					buf.append(" where state='1' and lower(dbname)=lower("+gz_tablename+".nbase) and "+tablename+".a0100="+gz_tablename+".a0100   )");
					dao.update(buf.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	
	/**
	 * 求得更新串
	 * @param strTable
	 * @param strDest
	 * @param setid
	 * @return
	 */
	private String getUpdateFieldSQL(String strSrc,String strDest,String setid,ArrayList itemList)
	{
		StringBuffer buf=new StringBuffer();
		LazyDynaBean abean=null;
		for(int i=0;i<itemList.size();i++)
		{
			abean=(LazyDynaBean)itemList.get(i);
			int intflag=Integer.parseInt((String)abean.get("initflag")); //0：输入项	1：累积项 	2：导入    3：系统项 
			String itemid=(String)abean.get("itemid"); 
			String fieldsetid=(String)abean.get("fieldsetid");
			String formula=(String)abean.get("formula");
			String itemdesc=(String)abean.get("itemdesc");
			if(!(setid.equalsIgnoreCase(fieldsetid)))
				continue; 	
			/**导入项，仅导入当前*/
			if(intflag==2)
			{
				/**未定义计算公式*/
				if(itemid.equalsIgnoreCase(formula)||itemdesc.equalsIgnoreCase(formula))
				{
					buf.append(strDest);
					buf.append(".");
					buf.append(itemid);
					buf.append("=");
					buf.append(strSrc);
					buf.append(".");					
					buf.append(itemid);
					buf.append("`");
				}
			}
			/**执行工资标准项*/
			if(intflag==4)
			{
				buf.append(strDest);
				buf.append(".");
				buf.append(itemid);
				buf.append("=");
				buf.append(strSrc);
				buf.append(itemid);
				buf.append("`");				
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	/**
	 * 求插入更新串列表
	 * @param setname  子集id
	 * @param exception_str 不包含的指标
	 * @param tableName 表前缀
	 * @return
	 */
	private String getInsFieldSQL(String setid,ArrayList itemList,String exception_str,String tableName)
	{
		StringBuffer buf=new StringBuffer();
		LazyDynaBean abean=null;
		for(int i=0;i<itemList.size();i++)
		{
			abean=(LazyDynaBean)itemList.get(i);
			String intflag=(String)abean.get("initflag"); //0：输入项	1：累积项 	2：导入    3：系统项 
			String itemid=(String)abean.get("itemid"); 
			String fieldsetid=(String)abean.get("fieldsetid");
			if(!(setid.equalsIgnoreCase(fieldsetid)))
				continue;
			if("0".equals(intflag))
				continue;
			if(exception_str.indexOf("'"+itemid.toUpperCase()+"'")!=-1)
				continue;
			if(StringUtils.isNotBlank(tableName))
				buf.append(tableName).append(".");
			buf.append(itemid);
			buf.append(",");
		}
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	 /**
     * 新增与减少人员的显示指标，
     * @param flag：1新增  2减少  3:变动比对
     * @param salaryid 薪资帐号号
     * @throws GeneralException
     */
	private ArrayList add_del_chg_rightList(int salaryid,String flag){
			ArrayList list = new ArrayList();
		 
			String rightvalue = ""; 
			if("1".equals(flag)){
				rightvalue = this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
			}else if("2".equals(flag)){
				rightvalue = this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
			}
			else if("3".equals(flag)){
				rightvalue = this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD); 
			}
			rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
			if(rightvalue.length()>0)
			{
				ContentDAO dao = new ContentDAO(conn);
				StringBuffer sql=new StringBuffer();
				sql.append("select itemid,itemdesc,sortid,itemtype,itemlength,decwidth,codesetid from salaryset where  salaryid=");
				sql.append(salaryid);
				sql.append(" and itemid in ('");
				sql.append(rightvalue);
				sql.append("') order by sortid");
				
				try {
					list = dao.searchDynaList(sql.toString());
					 
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
			return list;
	}
	
	
	/**
	 * 把新增人员，生成一张临时表(将指定日期里历史表中的数据导入到临时表中)
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,E0122,A0101,state
	 * 主键字段：DBNAME,A0100
	 * @return 用户名+InsPeoples 表名,如果创建不成功，返回空串
	 */
	private  String createAddManTable(LazyDynaBean dataBean){
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		try
		{
			int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
			ContentDAO dao=new ContentDAO(this.conn);
			salaryTableStructBo.createInsDecTableStruct(tablename,new ArrayList());
			/**导入数据*/
			String[] temps=((String)dataBean.get("ym")).split("-");
			String count=(String)dataBean.get("count");
			
			String dataTableName=(String)dataBean.get("tablename");//取得表名
			
			StringBuffer buf=new StringBuffer("");
			buf.append("insert into ");
			buf.append(tablename);
			buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE)");
			buf.append(" select distinct nbase,");
			buf.append("A0100,A0000,B0110,E0122,A0101,'1' as STATE  ");
			buf.append(" from "+dataTableName+" where lower(userflag)=? and salaryid=?");
			buf.append(" and "+Sql_switcher.year("a00z2")+"=?");
			buf.append(" and "+Sql_switcher.month("a00z2")+"=?");
			buf.append(" and a00z3=?");
			ArrayList dataList=new ArrayList();
			dataList.add(this.userview.getUserName().toLowerCase());
			dataList.add(new Integer(salaryid));
			dataList.add(new Integer(temps[0]));
			dataList.add(new Integer(temps[1]));
			dataList.add(new Integer(count));
			dao.update(buf.toString(), dataList);
			 
			SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
			String a01z0Flag=ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
			{
				/**导入数据*/
				String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
						String pre=dbarr[i];
						StringBuffer _sql=new StringBuffer("delete from "+tablename+" where lower(dbname)='"+pre.toLowerCase()+"' and  exists (select null from ");
						_sql.append(pre+"A01 where "+pre+"A01.a0100="+tablename+".a0100  and A01Z0<>'1' and A01Z0<>'' and A01Z0 is not null  ) ");
						dao.update(_sql.toString());
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			tablename="";
		}
		return tablename;
	}
	
	/**
	 * @Title: getFfCcount 
	 * @Description: TODO(依据业务日期获得发放次数) 
	 * @param date
	 * @return  int
	 * @author lis  
	 * @date 2015-8-10 下午03:01:58
	 */
	private int getFfCcount(String date)
	{
		int count=1;   //默认第1次
		date = date.replaceAll("\\.", "\\-");
		int salaryid=this.salaryTemplateBo.getTemplatevo().getInt("salaryid");
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where  salaryid=?");
		buf.append(" and ");
		buf.append(" upper(username)=?"); 
		buf.append(" and A00Z2=?");
		RowSet rset=null;
		try
		{
			String strYm="";
			ContentDAO dao=new ContentDAO(this.conn); 
			rset=dao.search(buf.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid),this.userview.getUserName().toUpperCase(),DateUtils.getSqlDate(date,"yyyy-MM-dd")}));
			if(rset.next())
				strYm=date;
			if(!"".equalsIgnoreCase(strYm))
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
				buf.append(" where salaryid=? and  upper(username)=? and A00Z2=?");
				
				ArrayList list = new ArrayList();
				Date date2 = DateUtils.getSqlDate(date,"yyyy-MM-dd");
				list.add(Integer.valueOf(salaryid));
				list.add(this.userview.getUserName().toUpperCase());
				list.add(date2);
				
				rset=dao.search(buf.toString(),list);
				if(rset.next())
				{	
					int a00z3=rset.getInt("A00Z3");
					count=++a00z3;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return count;
	}
	
	
	/**
	 * 取得当前次数的前一次发放日期和次数
	 * @param date
	 * @param count
	 * @return abean [ym:a00z2 发放日期 ,count:a00z3 发放次数 ,tablename:salaryhistory or salaryarchive 数据位于历史表还是归档表]
	 */
	private LazyDynaBean getPreCountDate(String salaryid,String date,String count,ContentDAO dao)
	{
		LazyDynaBean abean=null;
		RowSet rowSet=null;
		try
		{
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			String[] temps=date.split("-"); 
			StringBuffer sql=new StringBuffer("");
			boolean flag=false; 
			sql.append("select * from gz_extend_log where username=? and salaryid=?  and (  ("+Sql_switcher.year("a00z2")+"=?  and "+Sql_switcher.month("a00z2")+"=?  and a00z3<?  ) ");
			sql.append(" or ( "+Sql_switcher.year("a00z2")+"=? and "+Sql_switcher.month("a00z2")+"<?  ) ");
			sql.append(" or ( "+Sql_switcher.year("a00z2")+"<?  ) ) order by a00z2 desc,a00z3 desc ");
			ArrayList dataList=new ArrayList();
			dataList.add(this.userview.getUserName());
			dataList.add(new Integer(salaryid));
			dataList.add(new Integer(temps[0]));
			dataList.add(new Integer(temps[1]));
			dataList.add(new Integer(count));
			dataList.add(new Integer(temps[0]));
			dataList.add(new Integer(temps[1]));
			dataList.add(new Integer(temps[0])); 
			rowSet=dao.search(sql.toString(),dataList);
			if(rowSet.next())
			{
					abean=new LazyDynaBean();
					abean.set("ym",df.format(rowSet.getDate("a00z2")));
					abean.set("count",rowSet.getString("a00z3")); 
			}
			
			
			if(abean!=null)
			{
				temps=((String)abean.get("ym")).split("-");
				count=(String)abean.get("count");
				StringBuffer buf=new StringBuffer("");
	 
				buf.append("select count(a0100) from salaryhistory where lower(userflag)=? and salaryid=?");
				buf.append(" and "+Sql_switcher.year("a00z2")+"=? ");
				buf.append(" and "+Sql_switcher.month("a00z2")+"=? ");
				buf.append(" and a00z3=?");
				dataList=new ArrayList();
				dataList.add(this.userview.getUserName().toLowerCase());
				dataList.add(new Integer(salaryid));
				dataList.add(new Integer(temps[0]));
				dataList.add(new Integer(temps[1]));
				dataList.add(new Integer(count));
				rowSet=dao.search(buf.toString(),dataList);
				if(rowSet.next())
				{
					if(rowSet.getInt(1)==0){//新建数据考虑数据位于归档表的情况,并于abean中添加数据位置参数 tablename  zhanghua 2017-6-8  28372
						buf.setLength(0);
						buf.append("select count(a0100) from  salaryarchive where lower(userflag)=? and salaryid=?");
						buf.append(" and "+Sql_switcher.year("a00z2")+"=? ");
						buf.append(" and "+Sql_switcher.month("a00z2")+"=? ");
						buf.append(" and a00z3=?");
						rowSet=dao.search(buf.toString(),dataList);
						if(rowSet.next())
						{
							if(rowSet.getInt(1)==0){
								abean=null;
							}else
								abean.set("tablename", "salaryarchive");
						}
						
					}else
						abean.set("tablename", "salaryhistory");
						
						
				}
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return abean;
	}

	/**
	 * @Title: reLoadHistoryData 
	 * @Description: TODO(重置业务日期) 
	 * @param year		年份
	 * @param month		月份
	 * @param count		次数
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-10 下午04:50:09
	 */
	public void reLoadHistoryData(String year,String month,String count)throws GeneralException
	{
		String ym=year+"-"+month+"-01";
		
		StringBuffer buf=new StringBuffer();
		try
		{
			String manager = this.salaryTemplateBo.getManager();
			String gz_tableName = this.salaryTemplateBo.getGz_tablename();
			ContentDAO dao=new ContentDAO(this.conn);
			/**取得当前处理日期和次数*/
			HashMap map=this.getMaxYearMonthCount(null,false);
			/**需要审批的话,则分析当前处理的业务日期是否处理审批状态*/
			if(this.isApprove())
			{
				if(this.isApproving())
					throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.approving"));//正在审核中
			}
			
			
			
			//如果没有结束的记录 删除历史表中的数据
			this.deleteHistory("06","");
			//---------------------重置之前先删除税表  gz_tax_mx zhaoxg add 2014-12-15-------------
			StringBuffer sql = new StringBuffer();
	    	sql.append("delete from gz_tax_mx where salaryid="+this.salaryid+" and exists (select null from "+gz_tableName+" salary where gz_tax_mx.A00Z0=salary.A00Z0 and gz_tax_mx.A00Z1=salary.A00Z1 and gz_tax_mx.A0100=salary.A0100 ");
	    	sql.append(" and gz_tax_mx.NBASE=salary.NBASE and gz_tax_mx.flag=0");
	    	if(manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
	    		sql.append(" and ( lower(gz_tax_mx.userflag)='"+manager.toLowerCase()+"' or userflag is null )");
	    	else
	    		sql.append(" and ( lower(gz_tax_mx.userflag)='"+this.userview.getUserName().toLowerCase()+"' or userflag is null )");
	    	sql.append(" )");
	    	dao.delete(sql.toString(), new ArrayList());
	    	//-----------------------------end----------------------------
			/**清空当前薪资表中的数据*/
			buf.setLength(0);
			buf.append("delete from ");
			buf.append(gz_tableName);
			dao.update(buf.toString());
			
			
			/** 删除发放日志表中的当前用户起草\执行状态的记录  **/
			deleteDrafeOutRecord(manager);
			/** 重置业务日期，不能将原先的发放纪录状态改变  **/
			appendHistroyLog(ym,count,manager);
			
			/**所有项目*/
			StringBuffer fields=new StringBuffer();
			ArrayList<LazyDynaBean> salaryItems = this.salaryTemplateBo.getSalaryItemList(null, salaryid+"", 1);
			for(int i=0;i<salaryItems.size();i++)
			{
				LazyDynaBean bean = salaryItems.get(i);
				if("4".endsWith((String)bean.get("initflag")))
					continue;
				if(fields.length()==0)
					fields.append((String)bean.get("itemid"));
				else
				{
					fields.append(",");
					fields.append((String)bean.get("itemid"));
				}
			}
			
			
			ArrayList list = new ArrayList();
			ym = ym.replaceAll("\\.", "\\-");
			Date date = DateUtils.getSqlDate(ym,"yyyy-MM-dd");
			list.add(date);
			list.add(count);
			list.add(this.salaryid);
			list.add(this.userview.getUserName().toLowerCase());
			StringBuilder strb=new StringBuilder();
			strb.append(" and A00Z2=?");
			strb.append(" and A00Z3=?");
			strb.append(" and salaryid=?");
			strb.append(" and lower(userflag)=?");
			String historyTableName=this.getHistoryTableName(strb.toString(), list);
			
			buf.setLength(0);
			if(StringUtils.isNotBlank(historyTableName)){
				buf.append("insert into ");
				buf.append(gz_tableName);
				buf.append("(add_flag,userflag,sp_flag,dbid,");//由于在重置业务日期没有加入dbid，导致顺序错乱 sunjian 17-10-19
				if(this.isApprove()||StringUtils.isNotBlank(manager))
					buf.append("Appprocess,");
				if(StringUtils.isNotBlank(manager))
					buf.append("sp_flag2,");
				buf.append(fields.toString());
				buf.append(") select 1,userflag");
				buf.append(",sp_flag,dbid,");
				if(this.isApprove()||StringUtils.isNotBlank(manager))
					buf.append("Appprocess,");
				if(StringUtils.isNotBlank(manager))
					buf.append("'06',");
				buf.append(fields.toString());
				buf.append(" from "+historyTableName+" where");
				buf.append(" A00Z2=?");
				buf.append(" and A00Z3=?");
				buf.append(" and salaryid=?");
				buf.append(" and lower(userflag)=?");
				
				
				dao.update(buf.toString(),list);
			}
			//写入薪资发放数据的映射表
			if (Sql_switcher.searchDbServer()==1){			    
			    dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and USERFLAG='"+this.userview.getUserName().toLowerCase()+"'");
			}
			else {
			    dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and lower(USERFLAG)='"+this.userview.getUserName().toLowerCase()+"'");
			}    
			dao.update("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) select a0100,nbase,a00z0,a00z1,"+this.salaryid+",'"+this.userview.getUserName().toLowerCase()+"' from "+this.salaryTemplateBo.getGz_tablename());
			
			sql.setLength(0);
			//重置业务日期，把当前用户、当前薪资类别的待办全置成已办 zhaoxg add 2014-12-15
			sql.append("update t_hr_pendingtask set Pending_status='4' where (Pending_status='0' or Pending_status='3') and Receiver = '"+this.userview.getUserName()+"' and ext_flag like '%_"+salaryid+"'");
			dao.update(sql.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	/**
	 * @Title: deleteDrafeOutRecord 
	 * @Description: TODO(删除发放日志表中的当前用户起草\执行状态的记录) 
	 * @param manager
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-7-24 上午10:54:13
	 */
	private void deleteDrafeOutRecord(String manager) throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("delete from gz_extend_log where salaryid=?");
			buf.append(" and upper(username)=?");
			buf.append(" and ( sp_flag='01' or sp_flag='05' ) ");
			paralist.add(String.valueOf(this.salaryid));
			if(manager.length()==0)
				paralist.add(this.userview.getUserName().toUpperCase());
			else
				paralist.add(manager.toUpperCase());			
			dao.update(buf.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 获取当前临时表 数据存在于历史表还是归档表 若都不存在则返回空
	 * @param str where条件以and 开头
	 * @return
	 */
	private String getHistoryTableName(String strWhere,ArrayList<Object> list){
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			//String gz_tableName=this.salaryTemplateBo.getGz_tablename();
			if(StringUtils.isBlank(strWhere)){
//				strWhere="and  exists (select null from ";
//				strWhere+=gz_tableName+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ";
//				strWhere+=" salaryhistory.a0100=a.a0100   )";
//				strWhere+=" and salaryid=?";
//				strWhere+=" and lower(userflag)=? ";
//				
//				list=new ArrayList<Object>();
//				list.add(this.salaryid);
//				list.add(gz_tableName.split("_")[0]);
			}
			
			String strSql="select 1 from salaryhistory where 1=1 "+strWhere;
			RowSet rs=dao.search(strSql,list);
			if(rs.next())
				return "salaryhistory";
			else{
				 strSql="select 1 from  salaryarchive where 1=1 "+strWhere;
				 rs=dao.search(strSql,list);
				 if(rs.next())
					 return "salaryarchive";
				 else
					 return "";
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * @Title: appendHistroyLog 
	 * @Description: TODO(分析当前日期是否为历史记录(已发放过),如果没有则追加) 
	 * @param ymd		年月日
	 * @param count     次数
	 * @param manager   薪资类别管理员
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-7-24 上午10:59:19
	 */
	private void appendHistroyLog(String ymd,String count,String manager) throws GeneralException
	{
		RowSet rset = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			buf.append("select id from gz_extend_log where salaryid=?");
			buf.append(" and upper(username)=?");
			buf.append(" and A00Z3=? and A00Z2=? ");
			
			paralist.add(String.valueOf(this.salaryid));			
			if(manager.length()==0)
				paralist.add(this.userview.getUserName().toUpperCase());
			else
				paralist.add(manager.toUpperCase());			
			paralist.add(count);
			Date date = DateUtils.getSqlDate(ymd,"yyyy-MM-dd");
			paralist.add(date);
			rset=dao.search(buf.toString(),paralist);
			if(!rset.next())
			{
				DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,ymd,count,this.conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeResource(rset);
		}
	}
	
	/**
	 * 是否有发放历史记录
	 * @return
	 */
	private boolean isHaveHistroyLog(String currym,String currcount)
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			buf.append("select  id from gz_extend_log where salaryid=?");
			buf.append(" and upper(username)=?");
			buf.append(" and A00Z2=?");
			buf.append(" and A00Z3=?");
			buf.append(" and sp_flag=?");
			
			ArrayList list = new ArrayList();
			list.add(this.salaryid);
			list.add(this.userview.getUserName().toUpperCase());
			Date date = DateUtils.getSqlDate(currym,"yyyy-MM-dd");
			list.add(date);
			list.add(currcount);
			list.add("06");
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next())
			{
				bflag=true;
				
				String manager = this.salaryTemplateBo.getManager();
				/** 删除发放日志表中的当前用户起草\执行状态的记录  **/
				deleteDrafeOutRecord(manager);
				
				/**把最后一次数据设置为当前处理状态*/
				RecordVo vo=new RecordVo("gz_extend_log");
				vo.setInt("id", rset.getInt("id"));
				vo.setString("sp_flag", "01");
				vo.setInt("isredo", 1);
				dao.updateValueObject(vo);
			}
			else
				bflag=false;
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	/**
	 * @Title: isApprove 
	 * @Description: TODO(当前薪资类别是否需要审批) 
	 * @return boolean
	 * @author lis  
	 * @date 2015-7-24 上午10:48:48
	 */
	public boolean isApprove()
	{
		boolean bflag=false;
		String flow_flag=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
		if("1".equalsIgnoreCase(flow_flag))
			bflag=true;
		return bflag;
	}
	
	/**
	 * @Title: isApproving 
	 * @Description: TODO(是否正处在审批状态中) 
	 * @return boolean
	 * @author lis  
	 * @date 2015-7-24 上午10:47:53
	 */
	public boolean isApproving()
	{
		/**查找当前薪资表是否处于审批状态中*/
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet	rowSet=dao.search("select count(a0100) from "+this.salaryTemplateBo.getGz_tablename()+" where sp_flag is not null and ( sp_flag='03' or sp_flag='02' ) ");
			if(rowSet.next())
			{
				if(rowSet.getInt(1)==0)
				{
					flag=false;
				}
			}
				
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 审核校验
	 * @param type 0：薪资发放 1：薪资审批
	 * @param a00z2 业务日期
	 * @param a00z3 次数
	 * @param filtersql 过滤条件
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap verify(String type,String a00z2,String a00z3,String filtersql) throws GeneralException{
		HashMap map = new HashMap();
		try{
			/*boolean isComputeTax = isComputeTax(a00z2, a00z3);
			if(!isComputeTax) {
				throw GeneralExceptionHandler.Handle(new Exception("有人员未计算税率，请重新计算！"));
			}*/
			String tablename = this.salaryTemplateBo.getGz_tablename();
			ArrayList outItemList = new ArrayList();
			String outname=this.userview.getUserName()+"_gz.xls";
			String username=this.userview.getUserName();
			if(this.salaryTemplateBo.getManager()!=null&&this.salaryTemplateBo.getManager().length()>0)
				username = this.salaryTemplateBo.getManager();
			if("".equalsIgnoreCase(a00z2))//无数据不审核
			{
				// 没有数据可以进行审核
				throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("gz_new.gz_accounting.nodataToCheck")));
//				map.put("msg", "no");
//				return map;
			}
            String verify_item = this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL, "verify_item");//审核输出指标
            verify_item = verify_item==null?"":verify_item;
            
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
			
			String a01z0Flag=this.getSalaryTemplateBo().getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有
            String checkedField=SystemConfig.getPropertyValue("checkoutfield");
            checkedField = checkedField==null?"":checkedField;
            HashMap cf;
            cf=this.isHaveField(salaryid+"");
            if (cf.get("b0110")!=null){
                outItemList.add(cf.get("b0110"));
            }
            if (cf.get("e0122")!=null){
                outItemList.add(cf.get("e0122"));
            }
            if (cf.get("a0101")!=null){
                outItemList.add(cf.get("a0101"));
            }
            if (cf.get(checkedField)!=null){
                outItemList.add(cf.get(checkedField));
            }
            String [] arrVarifyItem = verify_item.split(",");
            for (int i=0;i<arrVarifyItem.length;i++){
                 String itemid = arrVarifyItem[i];
                 //如果设置里面不现实停发标识，则审核不通过excel也不显示
                 if ("".equals(itemid)|| ",".equals(itemid)|| itemid.equals(checkedField) || ("a01z0".equalsIgnoreCase(itemid) && !"1".equals(a01z0Flag)))
                     continue;
                 if (cf.get(itemid.toLowerCase())!=null){
                     outItemList.add(cf.get(itemid.toLowerCase()));
                 }
            }
            if (cf.get("a0100")!=null){
                outItemList.add(cf.get("a0100"));
            }
            int itemCount = outItemList.size();
            String outItemIds="";
            for (int i=0;i<outItemList.size();i++){//拼写查询sql片段
                cf = (HashMap)outItemList.get(i);
                String itemid = (String)cf.get("itemid");
                if("a0100".equalsIgnoreCase(itemid)){
    				if("0".equals(uniquenessvalid)){
                        if ("".equals(outItemIds)) 
                            outItemIds = "a."+itemid;
                        else 
                            outItemIds = outItemIds+",a."+itemid;  
    				}else if(onlyname==null|| "".equals(onlyname)){
                        if ("".equals(outItemIds)) 
                            outItemIds = "a."+itemid;
                        else 
                            outItemIds = outItemIds+",a."+itemid;  
    				}else{
                        if ("".equals(outItemIds)) 
                            outItemIds = "b."+onlyname+" onlyname ";
                        else 
                            outItemIds = outItemIds+",b."+onlyname+" onlyname ";  
    				}       				
                }else{
                    if ("".equals(outItemIds)) 
                        outItemIds = "a."+itemid;
                    else 
                        outItemIds = outItemIds+",a."+itemid;   
                }                    
            }
            ArrayList formulaList = this.getVerifyFormulaList(salaryid);
            if(formulaList.size()==0){
            	throw GeneralExceptionHandler.Handle(new Throwable("审核公式未定义！"));
            }
            ArrayList midVariableList = this.getVerifyMidVariableList(formulaList);
			DbWizard db = new DbWizard(this.conn);
			if("1".equals(type))
			{
				tablename="T#"+this.userview.getUserName()+"_gz";
		    //	if(db.isExistTable("T#"+this.userview.getUserName()+"_gz", false))
		    		db.dropTable("T#"+this.userview.getUserName()+"_gz");
		    	this.salaryTableStructBo.createShTempTable(tablename, a00z2, a00z3, this.userview, salaryid,filtersql);
			}
			addMidVarIntoGzTable(filtersql,midVariableList,tablename,a00z2,a00z3,type);

			
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct nbase from "+tablename);
			if("1".equals(type))
				sql.append(" where salaryid="+salaryid);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			ArrayList nbaseList = new ArrayList();
			while(rs.next())
			{
				nbaseList.add(rs.getString("nbase"));
			}

			ArrayList varlist = new ArrayList();
			varlist.addAll(midVariableList);
			varlist.addAll(this.salaryTemplateBo.getSalaryItemList("", salaryid+"", 2));
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet=null;
			String checkinfor="";
			boolean flag=false;
			ExportExcelUtil excelutil = new ExportExcelUtil(this.conn,this.userview);
			for(int i=0;i<formulaList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
				String formula=(String)bean.get("formula");
				String formulaname="";
				if(bean.get("name")!=null)
					formulaname=(String)bean.get("name");
                String information=(String)bean.get("information");
				if(formula==null|| "".equals(formula))
					continue;
				HSSFRow row=null;
				HSSFCell csCell=null;
				HSSFCellStyle titlestyle = style(workbook,0);
				HSSFCellStyle centerstyle = style(workbook,1);
				HSSFCellStyle cloumnstyle=style(workbook,2);
				HSSFCellStyle bordernone=style(workbook,3);
				HSSFCellStyle bordertop=style(workbook,4);
				centerstyle.setWrapText(true);
				int rows=0;
			
				YksjParser yp=null;
				int x=1;
				int y=0;
				
				ArrayList mergedCellList = new ArrayList();
				ArrayList headList = new ArrayList();
				ArrayList dataList = new ArrayList();
				
				for(int j=0;j<nbaseList.size();j++)
				{
					String nbase=(String)nbaseList.get(j);
					yp = new YksjParser(this.userview ,varlist,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.setVarList(midVariableList);//使用“执行标准”函数时，临时变量需要用到单独传入的fielditem数据集 zhanghua 20170516
					yp.setSupportVar(true);//设置允许临时变量
					yp.setStdTmpTable(tablename);
					yp.setTempTableName(tablename);
					yp.setCon(conn);
					boolean b = yp.Verify_where(formula.trim());
					if (!b) {
						checkinfor =formulaname+ResourceFactory.getProperty("workdiary.message.review.failure")+"!\n\n";
						checkinfor += yp.getStrError();
						throw GeneralExceptionHandler.Handle(new Exception(checkinfor));
					} 
					yp.setVerify(false);
					yp.run(formula.trim());
					String wherestr = yp.getSQL();//公式的结果
					sql.setLength(0);
					sql.append("select *");
					sql.append(" from "+tablename+" where ("+wherestr+")");
					sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
					if("1".equals(type))
					{
						if(a00z3!=null&&!"".equals(a00z3))
						{
							sql.append(" and A00Z3=");
							sql.append(a00z3);	
						}
						if(a00z2!=null&&!"".equals(a00z3))
						{
							a00z2=a00z2.replaceAll("\\.","-");
			     			String[] temp=a00z2.split("-");
				    		sql.append(" and "+Sql_switcher.year("a00z2")+"="+temp[0]+" and ");
			     			sql.append(Sql_switcher.month("a00z2")+"="+temp[1]);	
						}
					}
					else if(filtersql!=null&&!"".equals(filtersql)){
						sql.append(filtersql);
					}
					
					StringBuffer str = new StringBuffer();
					str.append("select ");
					str.append(outItemIds);
					str.append(" from ("+sql+") a,"+nbase.toUpperCase()+"a01 b where a.a0100=b.a0100 ");
					str.append(" order by a.a0000, a.A00Z0, a.A00Z1");
					rs=dao.search(str.toString()); 
					while(rs.next())
					{
						if(y==0)
						{

							LazyDynaBean merged = new LazyDynaBean();
							merged.set("content", ResourceFactory.getProperty("label.gz.shresult"));
							merged.set("fromRowNum", 0);
							merged.set("fromColNum", 0);
							merged.set("toRowNum", 0);
							merged.set("toColNum", itemCount);
							mergedCellList.add(merged);
							rows++;
							merged = new LazyDynaBean();
							merged.set("content", (i+1)+":"+formulaname);
							merged.set("fromRowNum", rows);
							merged.set("fromColNum", 0);
							merged.set("toRowNum", rows);
							merged.set("toColNum", itemCount);
							HashMap mergedCellStyleMap = new HashMap();
							mergedCellStyleMap.put("align", HorizontalAlignment.LEFT);
							merged.set("mergedCellStyleMap", mergedCellStyleMap);
							mergedCellList.add(merged);
							rows++;
							merged = new LazyDynaBean();
							merged.set("content", ResourceFactory.getProperty("workdiary.message.message")+"："+information);
							merged.set("fromRowNum", rows);
							merged.set("fromColNum", 0);
							merged.set("toRowNum", rows);
							merged.set("toColNum", itemCount);
							mergedCellStyleMap = new HashMap();
							mergedCellStyleMap.put("align", HorizontalAlignment.LEFT);
							merged.set("mergedCellStyleMap", mergedCellStyleMap);
							mergedCellList.add(merged);
							rows++;
							LazyDynaBean headbean = new LazyDynaBean();
							headbean.set("content", ResourceFactory.getProperty("gz.bankdisk.sequencenumber"));
							headList.add(headbean);
							for (int itemCol=0;itemCol<outItemList.size();itemCol++){								    
							    cf = (HashMap)outItemList.get(itemCol);  
							    String itemDesc = (String)cf.get("itemdesc");
								String itemtype=(String)cf.get("itemtype");
			                    String itemid = (String)cf.get("itemid");
			                    String decwidth = (String)cf.get("decwidth");
			                    if("a0100".equalsIgnoreCase(itemid)){
			        				if("0".equals(uniquenessvalid)){
			                            
			        				}else if(onlyname==null|| "".equals(onlyname)){
			                             
			        				}else{
			        					FieldItem item = DataDictionary.getFieldItem(onlyname);
			        					if(item!=null){
			        						itemDesc = item.getItemdesc();
			        					}else{
			        						itemDesc = "唯一性指标"; 
			        					}				        					
			        				}       				
			                    }
			                    headbean = new LazyDynaBean();
								headbean.set("content", itemDesc);
								headbean.set("colType", itemtype);
								headbean.set("decwidth", decwidth);
								headList.add(headbean);
							}
							rows++;
						}
						flag=true;

						ArrayList _datalist = new ArrayList();
						_datalist.add(x+"");
						
                        for (int itemCol=0;itemCol<outItemList.size();itemCol++){                                 
                            cf = (HashMap)outItemList.get(itemCol);  
                            String itemid = (String)cf.get("itemid");
                            String itemtype=(String)cf.get("itemtype");
                            String codeSetid=(String)cf.get("codesetid");
                            String value="";
                            if("a0100".equalsIgnoreCase(itemid)){
                				if("0".equals(uniquenessvalid)){
                					value=rs.getString(itemid);
                				}else if(onlyname==null|| "".equals(onlyname)){
                					value=rs.getString(itemid);
                				}else{
                					value=rs.getString("onlyname"); 
                				}  
                            }else{
                                if("A".equalsIgnoreCase(itemtype))
                                {
                                    value=rs.getString(itemid);
                                    if(!("".equals(codeSetid)) && !("0".equals(codeSetid)))
                                        value=AdminCode.getCodeName((String)cf.get("codesetid"),value);
                                }else if ("N".equalsIgnoreCase(itemtype))
                                {
                                    value=PubFunc.DoFormatDecimal(rs.getString(itemid),Integer.parseInt((String)cf.get("decwidth")));
                                 }
                                else if("D".equalsIgnoreCase(itemtype))
                                {
                                    if(rs.getDate(itemid)!=null)
                                        value=(new SimpleDateFormat("yyyy-MM-dd")).format(rs.getDate(itemid));
                                }
                                else if("M".equalsIgnoreCase(itemtype))
                                {
                                    value=Sql_switcher.readMemo(rs,itemid);
                                }                                 
                            }
                            _datalist.add(value);
                        }
                        dataList.add(_datalist);
                    	
						rows++;
						x++;
						y++;
					}
				
				}
				if(mergedCellList.size()>0)
					excelutil.exportExcel((i+1)+"", mergedCellList, headList, dataList, new HashMap(), 3);
			}
			excelutil.exportExcel(outname);
			workbook=null;
			map.put("filename",outname);
			if(flag)
			{
				map.put("msg", "no");
			}
			else
			{
				map.put("msg", "yes");
			}
			rs.close();
			if("1".equals(type))
			{
				/**用完临时表，删除*/
		    //	if(db.isExistTable("T#"+this.userview.getUserName()+"_gz", false))
		    		db.dropTable("T#"+this.userview.getUserName()+"_gz");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	/**
	 * 判断薪资项目中是否存在该指标
	 * @param field
	 * @param salaryid
	 * @return
	 */
	public HashMap isHaveField(String salaryid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select * from salaryset where salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				HashMap _map = new HashMap();
				String itemdesc="";
				String codesetid="0";
				String itemtype="A";
				itemdesc=rs.getString("itemdesc");
				codesetid=rs.getString("codesetid");
				itemtype=rs.getString("itemtype");
				_map.put("itemid",rs.getString("itemid").toLowerCase());
				_map.put("itemdesc",itemdesc);
				_map.put("codesetid",codesetid);
				_map.put("itemtype",itemtype);
				_map.put("itemlength",rs.getString("itemlength"));
				_map.put("decwidth",rs.getString("decwidth"));
				map.put(rs.getString("itemid").toLowerCase(), _map);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取审核公式
	 * @param salaryid
	 * @return
	 */
	public ArrayList getVerifyFormulaList(int salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select chkid,name,validflag,formula,information from hrpchkformula where flag=1 and validflag=1 and tabid='"+salaryid+"'");
	    	sql.append(" order by seq");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name")!=null?rs.getString("name"):"");
				bean.set("validflag", rs.getString("validflag"));
				bean.set("formula", Sql_switcher.readMemo(rs,"formula"));
				bean.set("information",Sql_switcher.readMemo(rs, "information"));
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据审核公式获取相关的临时变量
	 * @param formulaList
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getVerifyMidVariableList(ArrayList formulaList) throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}
			rset.close();
			//过滤薪资类别  计算公式用不到的临时变量
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
				  LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
				  String formula=((String)bean.get("formula")).toLowerCase();
				  if(formula==null|| "".equals(formula))
						continue;
	              for(int j=0;j<fieldlist.size();j++)
	              {
	            	  item=(FieldItem)fieldlist.get(j);
	            	  String item_id=item.getItemid().toLowerCase();
	            	  String item_desc=item.getItemdesc().trim().toLowerCase();
	            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)
	            	  {
	            		  //map.put(item_id, item);
	                      //searchVar(fieldlist, item.getFormula(), map, new_fieldList);
	            		  new_fieldList.add(item);
	            		  map.put(item_id, "1");
	            	  }
	            		  
	              }
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return new_fieldList;
	}
	
	/**
	 * 在读取临时变量的时候，如果临时变量中套用临时变量，那么再读取包含中的临时变量中值，应该计算在该临时变量之前
	 * 如： 统计 XXX 临时变量1=XX 的 XXXX
	 * 必须找到临时变量1先执行计算
	 * @param midList
	 * @param formualr_str
	 * @param map
	 * @param new_fieldList
	 */
	/*private void searchVar(ArrayList midList, String formualr_str, HashMap map, ArrayList new_fieldList) {
		for (int j = 0; j < midList.size(); j++) {
			FieldItem item = (FieldItem) midList.get(j);
			String item_id = item.getItemid();
			String item_desc = item.getItemdesc().trim().toLowerCase();
			String formula = item.getFormula();
			if (formualr_str.toLowerCase().indexOf(item_desc) != -1) {
				map.put(item_id, "1");
				new_fieldList.remove(item);
				new_fieldList.add(0, item);
				searchVar(midList, formula, map, new_fieldList);
			}
		}
	}*/
	
	/**
	 * 把临时变量写入对应表中
	 * @param strWhere 过滤条件
	 * @param midVariableList 临时变量集合
	 * @param gz_tablename 对应表名
	 * @param a00z2 业务日期
	 * @param a00z3 次数
	 * @param type 0：薪资发放 1：薪资审批
	 * @throws GeneralException
	 */
	public void addMidVarIntoGzTable(String strWhere,ArrayList midVariableList,String gz_tablename,String a00z2,String a00z3,String type)throws GeneralException
	{
		ArrayList fieldlist=midVariableList;
		ArrayList midList=getMidVariableList();
		try
		{
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);
			RecordVo vo=new RecordVo(gz_tablename);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(gz_tablename);
			String tablename="t#"+this.userview.getUserName()+"_gz_mid1";
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			boolean bflag=false;
			HashMap existMidFieldMap=new HashMap();
			if("1".equals(type))
			strWhere=strWhere.replaceAll("salaryhistory", gz_tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}
				else
					existMidFieldMap.put(fieldname.toLowerCase(),item.cloneItem());
			}
			if(bflag)
			{
				dbw.addColumns(table);
				dbmodel.reloadTableModel(gz_tablename);					
			}
			
			if(existMidFieldMap.size()>0&&"0".equals(type)) //同步表结构
			{
				syncGzField(gz_tablename,existMidFieldMap);
			}
			
			/**导入计算后的临时变量的值*/
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String stry=a00z2.substring(0, 4);
			String strm=a00z2.substring(5, 7);
			String strc=a00z3;
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			/**按人员分库进行批量计算*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=dbarr[i];
				for(int j=0;j<fieldlist.size();j++)
				{
					StringBuffer strFilter=new StringBuffer();
					FieldItem item=(FieldItem)fieldlist.get(j);
					String fldtype=item.getItemtype();
					String fldname=item.getItemid();
					String formula= item.getFormula();
					if(formula.indexOf("取自于")!=-1)
					{
						continue;
					}
					
					ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					allUsedFields.addAll(midList);  //临时变量调用临时变量
					YksjParser yp = new YksjParser(this.userview, allUsedFields,
							YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
					yp.setStdTmpTable(gz_tablename);
					yp.setTargetFieldDecimal(item.getDecimalwidth());

					
					if(yp.isStatMultipleVar())
					{
						ArrayList usedlist=initUsedFields();
						/**追加公式中使用的指标*/
						appendUsedFields(fieldlist,usedlist);
						/**增加一个计算公式用的临时字段*/
						FieldItem fielditem=new FieldItem("A01","AAAAA");
						fielditem.setItemdesc("AAAAA");
						fielditem.setCodesetid(item.getCodesetid());
						fielditem.setItemtype(fldtype);
						fielditem.setItemlength(item.getItemlength());
						fielditem.setDecimalwidth(item.getDecimalwidth());
						usedlist.add(fielditem);					
						/**创建计算用临时表*/
						String tmptable="t#"+this.userview.getUserName()+"_gz_mid1"; //this.userview.getUserName()+"midtable";
						if(createMidTable(usedlist,tmptable,"A0100"))
						{
							/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tablename);
							buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
							buf.append(dbpre+"A01");
							buf.append(" where A0100 in (select A0100 from ");
							buf.append(gz_tablename);
							if(strWhere.length()==0)
							{
								buf.append(" where upper(nbase)='");
								buf.append(dbpre.toUpperCase());
								buf.append("'");
								
								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(gz_tablename);
								strFilter.append(" where upper(nbase)='");
								strFilter.append(dbpre.toUpperCase());
								strFilter.append("')");	
							}
							else
							{
								buf.append(" where 1=1 ");
								buf.append(strWhere);
								buf.append(" and upper(nbase)='");
								buf.append(dbpre.toUpperCase());
								buf.append("'");
								
								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(gz_tablename);
								strFilter.append(" where 1=1 ");
								strFilter.append(strWhere);
								strFilter.append(" and upper(nbase)='");
								strFilter.append(dbpre.toUpperCase());
								strFilter.append("')");	
							}
							buf.append(")");
							dao.update(buf.toString());
						}// 创建临时表结束.
						
						
						buf.setLength(0);
						if(strWhere.length()==0)
						{
							buf.append("where upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
						}
						else
						{
							buf.append(" where 1=1 ");
							buf.append(strWhere);
							buf.append(" and upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
						}
				
						/**前面去掉WHERE*/
						String strcond=buf.substring(6);
						yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
						StringBuffer set_str=new StringBuffer("");
						StringBuffer set_st2=new StringBuffer("");
						for(int e=0;e<yp.getStatVarList().size();e++)
						{
							String temp=(String)yp.getStatVarList().get(e);
							set_st2.append(","+temp+"=null");
							set_str.append(gz_tablename+"."+temp+"="+tablename+"."+temp);
							if(Sql_switcher.searchDbServer()==2)
								set_str.append("`");
							else
								set_str.append(",");
						}
						if(set_str.length()>0)
							set_str.setLength(set_str.length()-1);
						else
							continue;
						
						dao.update("update "+gz_tablename+" set "+set_st2.substring(1)+"   "+buf.toString());
						dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100", set_str.toString(), strcond, strcond);
					}else{
						if(strWhere.length()==0)
						{
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(gz_tablename);
							strFilter.append(" where upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
						}
						else
						{
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(gz_tablename);
							strFilter.append(" where 1=1 ");
							strFilter.append(strWhere);
							strFilter.append(" and upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
						}
						yp.run(item.getFormula(),ymc,fldname,gz_tablename,dao,strFilter.toString(),this.conn,fldtype,item.getItemlength(),5,item.getCodesetid());
//						dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100", gz_tablename+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
					}
				}
			}
		}
		catch(Exception ex)
		{
			String message = ex.getMessage();
			ex.printStackTrace();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				PubFunc.resolve8060(this.conn,gz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.reloadVerify")));
			}else
				throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return fieldlist;

	}
	/**
	 * 同步表结构(判断临时变量字段)
	 * @param gz_tablename
	 * @param existMidFieldList
	 */
	private void  syncGzField(String tableName,HashMap existMidFieldMap)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 DbWizard dbw=new DbWizard(this.conn);
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					if(existMidFieldMap.get(columnName)!=null)
					{
						FieldItem tempItem=(FieldItem)existMidFieldMap.get(columnName);
						int columnType=data.getColumnType(i);	
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								break;
							case Types.TIMESTAMP:
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size)
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
										
									}
								}
								else 
									resetList.add(tempItem.cloneField());
								break;
							case Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()>scale)
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								
								
								break;
							case Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()>scale)
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
										
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								break;	
							case Types.LONGVARCHAR:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}
					}
				}
				rowSet.close();
				
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    }
			     table.clear();
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao)
	{
		try
		{
			FieldItem item=(FieldItem)_item.cloneItem();
			Table table=new Table(tableName);
			 String item_id=item.getItemid();
			 item.setItemid(item_id+"_x");
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 HashMap columnMap=new HashMap(); 
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
				 columnMap.put(data.getColumnName(i).toLowerCase().trim(),"1"); 
			 }
			 if(columnMap.get(item_id.toLowerCase().trim()+"_x")==null)  
			 {
		    	 table.addField(item.cloneField());
		    	 dbw.addColumns(table);
			 }
			 if("N".equalsIgnoreCase(item.getItemtype()))
			 {
				 int dicimal=item.getDecimalwidth();
				 dao.update("update "+tableName+" set "+item_id+"_x=ROUND("+item_id+","+dicimal+")");
			 }
			 if("A".equalsIgnoreCase(item.getItemtype()))
			 {
				 int length=item.getItemlength();
				 dao.update("update "+tableName+" set "+item_id+"_x=substr(to_char("+item_id+"),0,"+length+")");
			 }
			 table.clear();
			 item.setItemid(item_id);
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 dbw.addColumns(table);
			 
			 dao.update("update "+tableName+" set "+item_id+"="+item_id+"_x");
			 table.clear();
			 item.setItemid(item_id+"_x");
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 item.setItemid(item_id);
			 if(rowSet!=null)
				 rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 表格样式
	 * @param workbook
	 * @param styles
	 * @return
	 */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		switch(styles){
		case 0:
				HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),15);
				fonttitle.setBold(true);//加粗 
				style.setFont(fonttitle);
				style.setBorderBottom(BorderStyle.NONE);
				style.setBorderLeft(BorderStyle.NONE);
				style.setBorderRight(BorderStyle.NONE);
				style.setBorderTop(BorderStyle.NONE);
				style.setAlignment(HorizontalAlignment.CENTER );
		        break;			
		case 1:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index); 
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				break;
		case 3:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.NONE);
				style.setBorderLeft(BorderStyle.NONE);
				style.setBorderRight(BorderStyle.NONE);
				style.setBorderTop(BorderStyle.NONE);
				break;		
		case 4:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.NONE);
				style.setBorderLeft(BorderStyle.NONE);
				style.setBorderRight(BorderStyle.NONE);
				style.setBorderTop(BorderStyle.NONE);
			  break;
		default:		
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	 
				break;
		}
		return style;
	}
	/**
	 * 字体样式
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size){
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
 
	
	/**
	 * 查询相关表中是否存在数据
	 * @param tableName 表名
	 * @return isExist
	 */
	private String isExistRecord(String tableName) {
		String isExist = "0";
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select count(*) as count from " + tableName;
			rs = dao.search(sql);
			while (rs.next()) {
				if (!"0".equals(rs.getString("count"))) {
					isExist = "1";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return isExist;
	}	
	/**
	 * @Title: reExtendTheLastOne 
	 * @Description: TODO(重发最后一次的薪资) 
	 * @param bosdate
	 * @param count
	 * @return boolean
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-10 上午11:51:13
	 */
	public boolean reExtendTheLastOne(String bosdate,String count)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			/**当前用户和本薪资类别处理到的年月标识和次数*/
			String currym;
			String currcount;
			String gz_tablename = this.salaryTemplateBo.getGz_tablename();
			String manager = this.salaryTemplateBo.getManager();
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			
			String username=this.userview.getUserName();
			if(StringUtils.isNotBlank(manager))
				username=manager;
			bosdate=bosdate.replaceAll("\\.","-");
			currym=bosdate;
			currcount=count;
			
			/**分析薪资日志库是否有数据*/
			if(!isHaveHistroyLog(currym, currcount))
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.noData_in_gz_extend_log"));//薪资历史库中没有提交的数据
			
			/**清空当前薪资表中的数据*/
			buf.setLength(0);
			buf.append("delete from ");
			buf.append(gz_tablename);
			dao.update(buf.toString());
			/**所有项目*/
			StringBuffer fields=new StringBuffer();
			ArrayList<LazyDynaBean> salaryItemList = this.salaryTemplateBo.getSalaryItemList(null, salaryid+"", 1);
			for(int i=0;i<salaryItemList.size();i++)
			{
				LazyDynaBean fieldItem = salaryItemList.get(i);
				if("4".equals((String)fieldItem.get("initflag")))
					continue;
				if(fields.length()==0)
					fields.append(fieldItem.get("itemid"));
				else
				{
					fields.append(",");
					fields.append(fieldItem.get("itemid"));
				}
			}
			ArrayList list = new ArrayList();
			Date date = DateUtils.getSqlDate(currym,"yyyy-MM-dd");
			list.add(date);
			list.add(currcount);
			list.add(this.salaryid);
			list.add(this.userview.getUserName().toLowerCase());
			StringBuilder strb=new StringBuilder();
			strb.append(" and A00Z2=?");
			strb.append(" and A00Z3=?");
			strb.append(" and salaryid=?");
			strb.append(" and lower(userflag)=?");
			String historyTableName=this.getHistoryTableName(strb.toString(), list);
			
			buf.setLength(0);
			if(StringUtils.isNotBlank(historyTableName)){
				buf.append("insert into ");
				buf.append(gz_tablename);
				buf.append("(add_flag,userflag,sp_flag,dbid,");//由于在重置业务日期没有加入dbid，导致顺序错乱 sunjian 17-10-19
				if(StringUtils.isNotBlank(manager))
					buf.append("sp_flag2,");
				buf.append(fields.toString());
				buf.append(") select 1,userflag");
				buf.append(",'01',dbid,");
				if(StringUtils.isNotBlank(manager))
					buf.append("'01',");
				buf.append(fields.toString());
				buf.append(" from "+historyTableName+" where");
				buf.append(" A00Z2=?");
				buf.append(" and A00Z3=?");
				buf.append(" and salaryid=?");
				buf.append(" and lower(userflag)=?");
				
				
				
				dao.update(buf.toString(),list);
			}
			/**删除最后一次薪资数据*/
			deleteHistory("","");
			/**删除个税明细表中的数据*/
			deleteTaxMx();
			
			
			//写入薪资发放数据的映射表
			dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and lower(USERFLAG)='"+this.userview.getUserName().toLowerCase()+"'");
			dao.update("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) select a0100,nbase,a00z0,a00z1,"+this.salaryid+",'"+this.userview.getUserName().toLowerCase()+"' from "+gz_tablename);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;			
	}
	
	/**
	 * @Title: deleteHistory 
	 * @Description: TODO(删除当前用户历史表的数据) 
	 * @param sp_flag 重置业务日期是“06”
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-10 上午11:50:59
	 */
	public void deleteHistory(String sp_flag ,String filterWhl) throws GeneralException
	{
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String gz_tableName = this.salaryTemplateBo.getGz_tablename();
			int _count=0;
			if(StringUtils.isNotBlank(sp_flag)){
				ArrayList list = new ArrayList();
				list.add(sp_flag);
				rowSet=dao.search("select count(a0100) from "+gz_tableName+" where sp_flag is not null and  sp_flag=? ",list);
				if(rowSet.next())
					_count=rowSet.getInt(1);
			}
			if(_count==0)
			{
				String[] atemps=gz_tableName.toLowerCase().split("_salary_");
				
				StringBuffer sql=new StringBuffer("delete from salaryhistory   where exists (select null from ");
				sql.append(gz_tableName+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ");
				sql.append(" salaryhistory.a0100=a.a0100 "+filterWhl.replaceAll(gz_tableName,"a")+"  )");
				sql.append(" and salaryid=?");
				sql.append(" and lower(userflag)=?");  //20100323
				
				ArrayList list = new ArrayList();
				list.add(this.salaryid);
				list.add(atemps[0].toLowerCase());
				
				dao.update(sql.toString(),list);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeResource(rowSet);
		}
	}
	
	/**
	 * @Title: deleteTaxMx 
	 * @Description: TODO(删除税表明细数据) 
	 * @author lis  
	 * @date 2015-8-10 上午11:50:48
	 */
	private void deleteTaxMx()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);			
			buf.append("delete from gz_tax_mx where exists(select * from ");
			buf.append(this.salaryTemplateBo.getGz_tablename());
			buf.append(" a where upper(gz_tax_mx.nbase)=upper(a.nbase) and gz_tax_mx.a0100=a.a0100");
			buf.append(" and gz_tax_mx.a00z0=a.a00z0 and gz_tax_mx.a00z1=a.a00z1)");
			buf.append(" and salaryid=");
			buf.append(this.salaryid);
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * @Title: DeleteCurrentDraftRecord 
	 * @Description: TODO(删除当前为起草状态的发放纪录) 
	 * @param currcount
	 * @param currym
	 * @return int
	 * @author lis  
	 * @date 2015-8-10 上午11:50:22
	 */
	private int DeleteCurrentDraftRecord( String currcount, String currym)
	{
		int i = 0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("delete from gz_extend_log ");
			buf.append(" where salaryid=?");
			buf.append(" and ");
			buf.append(" upper(username)=?");
			buf.append(" and A00Z3=?");
			buf.append(" and A00Z2=?");
			buf.append(" and sp_flag=?");
			ArrayList valueList = new ArrayList();
			Date date = DateUtils.getSqlDate(currym,"yyyy-MM-dd");
			valueList.add(this.salaryid);
			valueList.add(this.userview.getUserName().toUpperCase());
			valueList.add(currcount);
			valueList.add(date);
			valueList.add("01");
			i = dao.delete(buf.toString(), valueList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	
	/**
	 * @Title: getSubDateList 
	 * @Description: TODO(求当前薪资类别在薪资历史数据表中已发放的历史业务日期列表) 
	 * @return ArrayList
	 * @author lis  
	 * @date 2015-8-10 上午11:37:38
	 */
	public ArrayList getSubDateList()
	{
		ArrayList list=new ArrayList();
		RowSet rset = null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2   from gz_extend_Log  where lower(username)=? and sp_Flag='06' ");
			buf.append(" and salaryid=? order by A00Z2 desc");
			
			ArrayList list2 = new ArrayList();
			list2.add(this.userview.getUserName().toLowerCase());
			list2.add(this.salaryid);
			ContentDAO dao=new ContentDAO(this.conn);
			rset = dao.search(buf.toString(),list2);
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			PubFunc.closeResource(rset);
		}
		return list;
	}
	
	/**
	 * @Title: getRfCountList 
	 * @Description: TODO(求当前薪资类别在薪资历史数据表中已发放业务次数) 
	 * @param date
	 * @return
	 * @author lis  
	 * @date 2015-8-10 上午11:50:03
	 */
	public ArrayList getRfCountList(String date)
	{
		ArrayList list=new ArrayList();
		RowSet rset = null;
		try
		{
			ArrayList valueList = new ArrayList();
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z3 from gz_extend_Log where  ");
			buf.append(" A00Z2=?");
			buf.append(" and upper(username)=?");
			buf.append(" and salaryid=? and sp_flag=? order by A00Z3");//重发只能重发结束状态的
			
			date = date.replaceAll("\\.", "\\-");
			Date dates = DateUtils.getSqlDate(date,"yyyy-MM-dd");
			valueList.add(dates);
			valueList.add(this.userview.getUserName().toUpperCase());
			valueList.add(this.salaryid);
			valueList.add("06");
			ContentDAO dao=new ContentDAO(this.conn);
			rset = dao.search(buf.toString(),valueList);
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=rset.getString("A00Z3");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			PubFunc.closeResource(rset);
		}
		return list;
	}
	
	/**
	 * @Title: searchCurrentDate2 
	 * 1.根据当前用户，查找处理的业务日期和次数 1.当临时表中有数据时根据数据的发放日期确定
	 * 2.当临时表中无数据，发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 * 3.当临时表中无数据，发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
	 * @param salaryid 
	 * @param username
	 * @return LazyDynaBean
	 * @author lis  
	 * @date 2015-8-10 下午05:05:14
	 */
	public LazyDynaBean searchCurrentDate(String salaryid,String username) 
	{
		LazyDynaBean abean=new LazyDynaBean();
		String strYm="";
		String strC="";
		RowSet rowSet=null;		
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn); 
			if(dbWizard.isExistTable(username.toLowerCase()+"_salary_"+salaryid, false))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				LazyDynaBean tableBean=this.getTableInfo(username.toLowerCase()+"_salary_"+salaryid);

				if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null)
					rowSet=dao.search("select distinct A00z2,A00z3 from "+username+"_salary_"+salaryid);
				if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null&&rowSet.next())
				{
					strYm=rowSet.getDate("A00Z2")!=null?PubFunc.FormatDate(rowSet.getDate("A00Z2"), "yyyy-MM-dd"):"";
					strC=rowSet.getString("A00Z3")!=null?rowSet.getString("A00Z3"):"";
				}
				else
				{
					//如果数据库表中没有a00z2和a00z3，则添加
					if(tableBean.get("a00z2")==null||tableBean.get("a00z3")==null)
					{
						DbWizard dbw=new DbWizard(this.conn);
						Table table=new Table(username.toLowerCase()+"_salary_"+salaryid);
						if(tableBean.get("a00z2")==null)
						{
							Field field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
							field.setDatatype(DataType.DATE);
							table.addField(field);
						}
						if(tableBean.get("a00z3")==null)
						{
							Field field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
							field.setDatatype(DataType.INT);
							table.addField(field);
						}
						dbw.addColumns(table);
					}
					
					//查询日志表中未结束状态的记录
					rowSet=dao.search("select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
					if(rowSet.next())
					{
						strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
						strC=rowSet.getString("A00z3");
					}
					else
					{
						rowSet=dao.search("select max(A00z2) A00z2 from gz_extend_log where  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
						if(rowSet.next())
						{
							if(rowSet.getDate("A00z2")!=null)
								strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
							else
								strYm="";
						}
						if(StringUtils.isBlank(strYm))
						{
							strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
							String[] tmp=StringUtils.split(strYm, "-");
							strYm="";// tmp[0]+"-"+tmp[1]+"-01";
							strC="";// "1";
						}
						else
						{
							StringBuffer buf=new StringBuffer("select max(A00Z3) A00Z3 from gz_extend_log");
							buf.append(" where salaryid=");
							buf.append(salaryid);
							buf.append(" and ");
							buf.append(" upper(username)='");
							buf.append(username.toUpperCase());
							buf.append("'");
							buf.append(" and A00Z2=");
							buf.append(Sql_switcher.dateValue(strYm));
						    rowSet=dao.search(buf.toString());
							if(rowSet.next())
								strC=rowSet.getString("A00Z3");
						}
						
						
					}
				}
			}
			abean.set("strYm", strYm);
			abean.set("strC", strC);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					PubFunc.closeResource(rowSet);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return abean;
	}
	
	/**
	 * @Title: getTableInfo 
	 * @Description: TODO(取得表结构信息) 
	 * @param tableName 表名
	 * @return LazyDynaBean 表结构信息
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-10 下午05:04:13
	 */
	public LazyDynaBean getTableInfo(String tableName) throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				abean.set(mt.getColumnName(i+1).toLowerCase(),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return abean;
	}
	
	/**
	 * 同步临时表B0110_O,E0122_O字段
	 * @param salaryid salaryhistory的salaryid字段值
	 * @param tempTable 临时表名称
	 * @throws Exception
	 */
	public void synSalaryTable(String salaryid,String tempTable) throws Exception {
		boolean flag = false;//判断数据库表结构在程序动行中是否有变化
		DbWizard dbw=new DbWizard(this.conn);
		DBMetaModel dbmodel=new DBMetaModel(this.conn);
		Table table=new Table(tempTable.toUpperCase());		
		//判断临时表、salaryhistory中是否存在B0110_O,E0122_O字段
		RecordVo vo = new RecordVo(tempTable.toLowerCase());
		if(!vo.hasAttribute("b0110_o"))
		{
			flag = true;
			Field field=new Field("B0110_O","B0110_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute( "e0122_o"))
		{
			flag = true;
			Field field=new Field("E0122_O","E0122_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute("dbid"))
		{
			flag = true;
			Field field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(flag){
			dbw.addColumns(table);
			dbmodel.reloadTableModel(tempTable.toUpperCase());
		}
		flag = false;
		table=new Table("SalaryHistory".toUpperCase());	
		vo = new RecordVo("SalaryHistory".toUpperCase());
		if(!vo.hasAttribute("b0110_o"))
		{
			flag = true;
			Field field=new Field("B0110_O","B0110_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute("e0122_o"))
		{
			flag = true;
			Field field=new Field("E0122_O","E0122_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute("dbid"))
		{
			flag = true;
			Field field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(flag){
			dbw.addColumns(table);
			dbmodel.reloadTableModel("SalaryHistory".toUpperCase());
		}
		//同步历史表数据
		ContentDAO dao = new ContentDAO(this.conn);
		
		String sql = "update SalaryHistory set E0122_O=(select A0000 from organization where codesetid='UM' and organization.codeitemid=SalaryHistory.E0122) where SalaryHistory.salaryid="+salaryid;
		dao.update(sql);
		sql = "update SalaryHistory set B0110_O=(select A0000 from organization where codesetid='UN' and SalaryHistory.B0110=organization.codeitemid) where SalaryHistory.salaryid="+salaryid;
		dao.update(sql);
		sql = "update SalaryHistory set dbid=(select dbid from dbname where upper(SalaryHistory.nbase)=upper(dbname.pre)) where SalaryHistory.salaryid="+salaryid;
		dao.update(sql);
		//同步临时表数据
		sql = "update "+tempTable+" set B0110_O=(select A0000 from organization where codesetid='UN' and "+tempTable+".B0110=organization.codeitemid)";
		dao.update(sql);
		sql = "update "+tempTable+" set E0122_O=(select A0000 from organization where codesetid='UM' and organization.codeitemid="+tempTable+".E0122)";
		dao.update(sql);
		sql = "update "+tempTable+" set dbid=(select dbid from dbname where upper("+tempTable+".nbase)=upper(dbname.pre))";
		dao.update(sql);
	}
	/**
	 * 薪资发放报审
	 * zhaoxg 2015-9-1
	 * @throws GeneralException
	 */
	public void gzDataReport(String gz_module,String accountingdate, String accountingcount)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String groupName =this.salaryTemplateBo.getGroupName(this.userview);
			RowSet rowSet = null;
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间
			String tableName = this.salaryTemplateBo.getGz_tablename();
			String manager = this.salaryTemplateBo.getManager();
			String sql2="select count(a0100) from "+tableName+" where 1=1 ";
			String sql3="select * from "+tableName+" where 1=1 ";
			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())&&!this.userview.isSuper_admin()){
				String privsql = this.salaryTemplateBo.getWhlByUnits(tableName,true);
				ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.conn,String.valueOf(salaryid),this.userview);
				String orgSql = aorgbo.getSalarySql(accountingdate, gz_module);//取应用机构的权限
				sql2+="  "+privsql+orgSql+"";
				sql3+="  "+privsql+orgSql+"";
			}
			sql2+=" and ( sp_flag2='01' or sp_flag2='07' )  and  sp_flag!='02' and sp_flag!='03' and sp_flag!='06'  ";
			sql3+=" and ( sp_flag2='01' or sp_flag2='07' )  and  sp_flag!='02' and sp_flag!='03' and sp_flag!='06' ";
			rowSet=dao.search(sql2);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)==0)
					throw GeneralExceptionHandler.Handle(new Exception("没有可报审的记录！"));
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("没有可报审的记录！"));
			String toName=this.salaryTemplateBo.getNameByUsername(manager);
			String fromName=this.salaryTemplateBo.getNameByUsername(this.userview.getUserName());
			ArrayList dataList=new ArrayList();
			ArrayList recordList=null;
			rowSet=dao.search(sql3);
			int i = 0;
			while(rowSet.next())
			{
				String appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
				String value="";
				if(appprocess.trim().length()>0)
					value="   \r\n";
				recordList=new ArrayList();
				recordList.add(appprocess+value+"报审: " + currentTime+ "\n  " + groupName + " " +fromName+ " 报审给 " + toName);
				recordList.add(rowSet.getString("nbase"));
				recordList.add(rowSet.getString("a0100"));
				recordList.add(new Integer(rowSet.getInt("a00z1")));
				recordList.add(rowSet.getDate("a00z0"));
				dataList.add(recordList);
				i++;
				if(i==500){
					dao.batchUpdate("update "+tableName+" set sp_flag2='02',appprocess=? where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
					i=0;
					dataList=new ArrayList();
				}
			}
			dao.batchUpdate("update "+tableName+" set sp_flag2='02',appprocess=? where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 薪资发放驳回
	 * @param selectRecords 全选则为未选中的数据，否则为选中的数据
	 * @param content  驳回原因
	 *
	 * @throws GeneralException
	 */
	public void gzDataReportReject(String selectRecords,String content)throws GeneralException
	{
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String fromName=this.salaryTemplateBo.getNameByUsername(userview.getUserName());
			String groupName =this.salaryTemplateBo.getGroupName(this.userview);
			String tableName = this.salaryTemplateBo.getGz_tablename();
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间
			String[] selects=selectRecords.split("#");
			StringBuffer whl=new StringBuffer(" and (");
			ArrayList list=new ArrayList();
			for (String select : selects) {
				if(StringUtils.isBlank(select))
					continue;
				String[] a_selects=select.split("/");
				whl.append(" (A0100 = ? and upper(nbase) = ? and A00Z0 = ? and A00z1 = ? ) or");
				list.add(PubFunc.decrypt(a_selects[0]));
				list.add(PubFunc.decrypt(a_selects[1]).toUpperCase());
				list.add(DateUtils.getSqlDate(a_selects[2],"yyyy-MM-dd"));
				list.add(a_selects[3]);
			}
			if(list.size()>0){
				whl.delete(whl.length()-3,whl.length());
				whl.append(" ) ");
			}else{
				whl.setLength(0);
			}

			rowSet=dao.search("select * from "+tableName+" where (sp_flag='01' or sp_flag='07') and sp_flag2='02' "+whl.toString(),list);
			ArrayList dataList=new ArrayList();
			ArrayList recordList=null;
			int i = 0;
			while(rowSet.next())
			{
				String appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
				recordList=new ArrayList();
				String value="";
				if(appprocess.trim().length()>0)
					value="   \r\n";
				recordList.add(appprocess+value+ResourceFactory.getProperty("edit_report.status.dh")+": " + currentTime+ "\n  " + groupName + " " + fromName+ " "+ResourceFactory.getProperty("edit_report.status.dh")+ResourceFactory.getProperty("jx.param.objectdegree2")+"。\n  "+ResourceFactory.getProperty("edit_report.goBackDescription")+"："+content);
				recordList.add(rowSet.getString("nbase"));
				recordList.add(rowSet.getString("a0100"));
				recordList.add(new Integer(rowSet.getInt("a00z1")));
				recordList.add(rowSet.getDate("a00z0"));
				dataList.add(recordList);
				i++;
				if(i==500){
					dao.batchUpdate("update "+tableName+" set appprocess=?,sp_flag2='07' where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
					i=0;
					dataList=new ArrayList();
				}
			}
			dao.batchUpdate("update "+tableName+" set appprocess=?,sp_flag2='07' where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
	}
	
	 /** 薪资审批-薪资确认
		 * @param setlist				需要归档提交的数据集列表
		 * @param typelist				数据集提交类型列表
		 * @param items				更新指标集
		 * @param uptypes			更新方式
		 * @param ff_bosdate       发放日期
		 * @param ff_count           发放次数
		 */
		public  void submitGzDataFromHistory(ArrayList setlist,ArrayList typelist,String items,String uptypes,String ff_bosdate,String ff_count,String history_where) throws GeneralException
		{
			try
			{
				boolean isComputeTax = isComputeTax(ff_bosdate, ff_count);
				if(!isComputeTax) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.reCompute")));
				}
				ContentDAO dao=new ContentDAO(this.conn);
				DbWizard dbw=new DbWizard(this.conn);
				LazyDynaBean subParamBean=saveAndGetSubmitType(setlist,typelist,items,uptypes,ff_bosdate, ff_count);  //确认时不显示数据操作方式时从数据库中获取，否则保存至数据库中
				setlist=(ArrayList)subParamBean.get("setlist");
				typelist=(ArrayList)subParamBean.get("typelist");
				items=(String)subParamBean.get("items");
				uptypes=(String)subParamBean.get("uptypes");
				/**汇总薪资总额*/ 
				collectGzTotalData(ff_bosdate);
				ArrayList userList=getUserList(history_where,dao);  //获得薪资数据涉及的发起人列表
				synHistoryToTemp(history_where,userList,dao);  //将薪资历史表中的数据同步到各临时表中（在薪资审批模块能修改了数据）
				ArrayList itemList=this.salaryTemplateBo.getSalaryItemList("",this.salaryid+"",1);  //获得薪资账套项目
				
				String tableName="t#"+this.userview.getUserName()+"_gz_3";   //待提交入库的数据
		//		if(dbw.isExistTable(tableName,false))
					 dbw.dropTable(tableName);  
				String _sql="  from salaryhistory "+this._withNoLock+" where  1=1 "+history_where;
				if(Sql_switcher.searchDbServer()==2)
				{
					dbw.execute("create table "+tableName+" as  select * "+_sql);	
					dao.update("create index "+tableName+"_idx2 on "+tableName+" (A0100,UPPER(USERFLAG),A00Z0,A00Z1)"); 
					
				}
				else
				{
					dbw.execute("select  *   into "+tableName+_sql);
					dao.update("create index "+tableName+"_idx2 on "+tableName+" (A0100,USERFLAG,A00Z0,A00Z1)"); 
				} 
						
				//数据提交入库不判断子集及指标权限
				String subNoPriv=this.salaryTemplateBo.getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
				if(subNoPriv==null||subNoPriv.trim().length()==0)
			 		subNoPriv="0";

				//使用实际薪资表中数据所涉及到的人员库进行提交，防止由于人员移库到此薪资类别没有授权的人员库，导致数据无法提交到人员档案库中。
				String strsql="select upper(nbase) as nbase from salaryhistory where 1=1 "+history_where+" group by upper(nbase)";
				RowSet rs=dao.search(strsql);
				String nbase="";
				while (rs.next())
					nbase+=rs.getString("nbase")+",";

				if(StringUtils.isNotBlank(nbase))
					this.salaryTemplateBo.getTemplatevo().setString("cbase", nbase);
				/**取得人员库前缀列表*/
				String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ",");
				String strNow=Sql_switcher.sqlNow(); 
				/**求得对应子集的指标串,比如AXXX1,AXXX0*/
				ArrayList updatelist=this.salaryTemplateBo.getUpdateFields(setlist,itemList);
				/**薪资表数据->档案库*/
				HashMap itemUptype=getItemUpdateType(items,uptypes);
				//判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
				boolean isYMaddSet=isymChangeSet(setlist,typelist); 
				StringBuffer sub_sql=new StringBuffer("");
				ArrayList userFlagList_noRedo=new ArrayList();
				String temp_str="";
				for(int i=0;i<userList.size();i++)
				{
					String userFlag=(String)userList.get(i);
					temp_str+=",'"+userFlag.toUpperCase()+"'"; 
				}
				ArrayList logList=this.salaryTemplateBo.getlogList(ff_bosdate,ff_count,this.salaryid+"",temp_str.substring(1)); //薪资账套各发起人的发放状态
				LazyDynaBean abean=null;
				for(int i=0;i<logList.size();i++)
				{
					abean=(LazyDynaBean)logList.get(i);
					String isRedo=(String)abean.get("isRedo");
					String sp_flag=(String)abean.get("sp_flag");
					String username=(String)abean.get("username");
					if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
					{
						sub_sql.append(" and upper(salaryhistory.userflag)!='"+username.toUpperCase()+"'" );
					}
					else
						userFlagList_noRedo.add(username);
				}
				
				
				
				 String filterWhl="";
				if(userFlagList_noRedo.size()>0)
				{
					filterWhl=history_where+sub_sql.toString();
					ArrayList z0z1List=getZ0z1List("salaryhistory",filterWhl); 
					filterWhl = filterWhl.replaceAll("salaryhistory", tableName);//替换下权限sql片段里面的历史表，防止后面报错 zhaoxg add 2016-9-8
					for(int i=0;i<dbarr.length;i++)
					{
						String cbase=dbarr[i];
						/** 处理只有一条薪资记录的用户一般子集数据 */
						dealwithSingleRecord_history(setlist,updatelist,typelist,cbase,dbw,itemUptype,strNow,subNoPriv);
						String tempName="t#"+this.userview.getUserName()+"_gz";  
						String strym="";
						/** 处理只有一条薪资记录的用户年月子集数据 */
						for(int j=0;j<z0z1List.size();j++)
						{
							abean=(LazyDynaBean)z0z1List.get(j);
							strym=(String)abean.get("strym"); 
						    String[] temp=strym.split("-"); 
							String singleRecord_where="(select count(a0100) c,a0100 from "+tableName+" "+this._withNoLock+" where "; 
							singleRecord_where+=" lower("+tableName+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+" ";
							singleRecord_where+=" and  "+Sql_switcher.year(tableName+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(tableName+".a00z0")+"="+temp[1]+"   group by a0100  having count(a0100)=1 ) aa";
					//	     if(dbw.isExistTable(tempName,false))
						    	 dbw.dropTable(tempName); 
						     StringBuffer sql0=new StringBuffer("");
						     if(Sql_switcher.searchDbServer()==2)
						    	 sql0.append("create table "+tempName+" as ");
						     sql0.append("select "+tableName+".a0100,"+tableName+".a00z0,"+tableName+".add_flag,"+tableName+".a00z1");
						     if(Sql_switcher.searchDbServer()!=2)
						    	 sql0.append(" into "+tempName);
						     sql0.append(" from "+tableName +this._withNoLock +","+singleRecord_where);
						     sql0.append(" where aa.a0100="+tableName+".a0100 and  ");
						     sql0.append(Sql_switcher.year(tableName+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(tableName+".a00z0")+"="+temp[1]);
						     sql0.append(filterWhl+" and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+tableName+".a0100 )    and lower("+tableName+".NBASE)='"+cbase.toLowerCase()+"' ");
						     dao.update(sql0.toString());
						     this.salaryTemplateBo.dealwithSingleRecord_ym_history(userFlagList_noRedo,setlist,true,isYMaddSet,updatelist,typelist,cbase,dbw,itemUptype,strNow,strym,subNoPriv);	
						}
						this.salaryTemplateBo.dealwithMulRecord_history2(setlist,isYMaddSet,updatelist,typelist, dbarr,i,dbw,itemUptype,subNoPriv);
					}
				}
				StringBuffer ss=new StringBuffer("");
				com.hjsj.hrms.businessobject.gz.SalaryTemplateBo gzbo=new com.hjsj.hrms.businessobject.gz.SalaryTemplateBo(this.conn,salaryid,this.userview);
				if(StringUtils.isNotBlank(nbase))
					gzbo.getTemplatevo().setString("cbase", nbase);
			    ff_bosdate=ff_bosdate.replaceAll("-", "\\.");
				for(int i=0;i<userList.size();i++)
				{
					String userFlag=(String)userList.get(i);
					String gz_tablename=userFlag+"_salary_"+this.salaryid;
					gzbo.setGz_tablename(gz_tablename);
					
					ss.setLength(0);
					ss.append(" and exists (select null from  "+tableName+this._withNoLock );
					ss.append(" where "+tableName+".a00z0="+gz_tablename+".a00z0 and "+tableName+".a00z1="+gz_tablename+".a00z1 and upper("+tableName+".nbase)=upper("+gz_tablename+".nbase) and ");
					ss.append(" "+tableName+".a0100="+gz_tablename+".a0100   and upper("+tableName+".userflag)='"+userFlag.toUpperCase()+"'  )");
					gzbo.setFilterWhl(ss.toString());
					gzbo.setFilterWhl2(history_where+" and upper(salaryhistory.userflag)='"+userFlag.toUpperCase()+"'");
					//			this.filterWhl=ss.toString();
		//			this.filterWhl2=history_condWhl+" and upper(salaryhistory.userflag)='"+userFlag.toUpperCase()+"'";
					
					abean=gzbo.getGzExtendLog(String.valueOf(this.salaryid),ff_bosdate,ff_count,userFlag);
					String isRedo=(String)abean.get("isRedo");
					String sp_flag=(String)abean.get("sp_flag");
					ArrayList _setlist=(ArrayList)setlist.clone();
					ArrayList _typelist=(ArrayList)typelist.clone();
					String _items=items;
					String _uptypes=uptypes;
					
			    	if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
					{
						_typelist=new ArrayList();
						for(int j=0;j<_setlist.size();j++)
						{
							 				
							String _setid=(String)_setlist.get(j);
							if(_setid.charAt(0)!='A'|| "A00".equalsIgnoreCase(_setid)|| "A01".equalsIgnoreCase(_setid))
							{
								_typelist.add("2");
							} 
							else
								_typelist.add("0"); 
						}
						_items="";
						_uptypes="";
					} 
			    	gzbo.submitGzData2_history(_setlist,_typelist,_items,_uptypes,userFlag,sp_flag,isRedo); 
				
				} 
				
				
				
				
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
		
		
		
		/** 处理只有一条薪资记录的用户一般子集数据 
		 * @param setlist  子集列表
		 * @param updatelist 对应子集的指标串
		 * @param typelist 数据集提交类型列表
		 * @param cbase 信息库前缀 
		 * @param dbw
		 * @param itemUptype
		 * @param strNow 当前时间
		 * @param subNoPriv 数据提交入库不判断子集及指标权限
		 */
		private  void dealwithSingleRecord_history(ArrayList setlist,ArrayList updatelist,ArrayList typelist,String cbase,DbWizard dbw,HashMap itemUptype,String strNow,String subNoPriv)
		{
			try
			{ 
				String dessetid="";
				String supdate="";
				ContentDAO dao=new ContentDAO(this.conn); 
				String salaryhistory_tmp="t#"+this.userview.getUserName()+"_gz_3";   //待提交入库的数据
				String tableName="t#"+this.userview.getUserName()+"_gz_4";  
	//			if(dbw.isExistTable(tableName,false))
					 dbw.dropTable(tableName);  
				String _sql="  from "+salaryhistory_tmp+this._withNoLock+" where lower("+salaryhistory_tmp+".NBASE)='"+cbase.toLowerCase()+"'   and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+salaryhistory_tmp+".a0100 )  group by a0100  having count(a0100)=1 ";
				if(Sql_switcher.searchDbServer()==2)
					 dbw.execute("create table "+tableName+" as  select a0100 "+_sql);	
				else
					 dbw.execute("select a0100   into "+tableName+_sql);
				 	
				
				String  royalty_valid=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid"); //提成工资是否启用
				String royalty_relation_fields=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
				ArrayList relationfieldList=new ArrayList();
				String royalty_setid=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"setid"); //提成工资子集
				if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
				{
					String[] temps=royalty_relation_fields.toLowerCase().split(",");
					for(int n=0;n<temps.length;n++)
					{
						if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
						{ 
							relationfieldList.add(temps[n]);
						}
					}
				}
				
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;
					String fields=(String)updatelist.get(j);
					/**(0,1,2)=(更新,新增,不变)*/
					String type=(String)typelist.get(j);
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					int ntype=Integer.parseInt(type);
					
					if(nflag==0&&setid.charAt(0)=='A'&&!"A01".equalsIgnoreCase(setid))  /**一般子集*/
					{
						dessetid=cbase+setid;
						supdate=(String)updatelist.get(j);
						if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							ntype=0;
						if(ntype==2)
							continue;
						if("0".equals(subNoPriv))
						{
							if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
								continue;
						}
						
						switch(ntype)
						{
							case 0://更新 
								if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
								{ 
									String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
									String where_str2="select a0100 from "+tableName+" where "; 
									where_str2+="  not exists (select null from "+dessetid+"  where "+salaryhistory_tmp+".a0100="+dessetid+".a0100  )";
									this.salaryTemplateBo.batchInsertSetRecord_royalty(dessetid,tableName+" aa",where_str2,relationfieldList,cbase);
									
								} 
								break;
							case 1://追加记录
							//	String where_str=singleRecord_where;
								 
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								String where_str2="select a0100,1"+strvalue+"from "+tableName+this._withNoLock+" where "; 
								where_str2+="  not exists (select null from "+dessetid+"  where "+tableName+".a0100="+dessetid+".a0100  )"; 
								this.salaryTemplateBo.batchInsertSetRecord(dessetid,tableName+" aa",where_str2);
									
								break;
						}
						 
						String value="";
						HashMap relationFieldMap=new HashMap();
						StringBuffer sqlRelation=new StringBuffer("");
						StringBuffer sqlRelation2=new StringBuffer("");
						if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						{
							for(int n=0;n<relationfieldList.size();n++)
							{
								String key=(String)relationfieldList.get(n);
								relationFieldMap.put(key.toLowerCase(),"");
								FieldItem item=DataDictionary.getFieldItem(key.toLowerCase());
								if(Sql_switcher.searchDbServer()==2)
								{
									if("D".equalsIgnoreCase(item.getItemtype()))
									{
										sqlRelation.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(dessetid+"."+key,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar("t#"+this.userview.getUserName()+"_gz"+"."+key,"YYYY-MM-DD"),"'-'") ); 
										sqlRelation2.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(dessetid+"."+key,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(salaryhistory_tmp+"."+key,"YYYY-MM-DD"),"'-'") ); 
									}
									else
									{
										sqlRelation.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"'-'")+"="+Sql_switcher.isnull("t#"+this.userview.getUserName()+"_gz"+"."+key,"'-'") ); 
										sqlRelation2.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"'-'")+"="+Sql_switcher.isnull(salaryhistory_tmp+"."+key,"'-'") ); 
									}
								}
								else		
								{
									sqlRelation.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"''")+"="+Sql_switcher.isnull("t#"+this.userview.getUserName()+"_gz"+"."+key,"''"));
									sqlRelation2.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"''")+"="+Sql_switcher.isnull(salaryhistory_tmp+"."+key,"''"));
								}
							}
							
							value=this.salaryTemplateBo.getUpdateSQL_royalty(dessetid,salaryhistory_tmp,fields,nflag,itemUptype);
						}
						else
							value=this.salaryTemplateBo.getUpdateSQL(dessetid,salaryhistory_tmp,fields,nflag,itemUptype);
						if(Sql_switcher.searchDbServer()==2)
						{
							 String[] strArr = StringUtils.split(value, "`");
						     StringBuffer sub_str=new StringBuffer("");
						     StringBuffer sub_str2=new StringBuffer("");
							 for(int e = 0; e < strArr.length; e++)
						     {
						            String temp = strArr[e];
						            String[] strtmp = StringUtils.split(temp, "=", 2);
						            if(strtmp[1].indexOf(salaryhistory_tmp)==-1)
						            {
						            	sub_str.append(","+strtmp[1]+" as "+strtmp[0]);
						            }
						            else
						            {
						            	sub_str.append(","+strtmp[1]);
						            	if(relationFieldMap.get(strtmp[1].replaceAll(salaryhistory_tmp+".","").toLowerCase())!=null)
						            		relationFieldMap.put(strtmp[1].replaceAll(salaryhistory_tmp+".","").toLowerCase(),"1");
						            }
						            
						            if(strtmp[0].indexOf(dessetid)==-1)
						            	sub_str2.append(","+strtmp[0]);
						            else
						            {
						            	String[] aa=strtmp[0].split("\\.");
						            	sub_str2.append(","+aa[1]);
						            	
						            }
						     }
	                      
						    String tempName="t#"+this.userview.getUserName()+"_gz";  //this.userview.getUserName()+"_GzTempTable";
				//		     if(dbw.isExistTable(tempName,false))
						    	 dbw.dropTable(tempName); 
						     StringBuffer sql=new StringBuffer("create table "+tempName+" as ");
						     if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						     {
						    	 Set set=relationFieldMap.keySet();
						    	 for(Iterator t=set.iterator();t.hasNext();)
						    	 {
						    		 String key=(String)t.next(); 
						    		 if(!"1".equalsIgnoreCase((String)relationFieldMap.get(key)))
						    			 sub_str.append(","+key);
						    	 }
						     }
							 sql.append("select "+salaryhistory_tmp+".a0100"+sub_str.toString());
							 sql.append(" from "+salaryhistory_tmp+this._withNoLock+","+tableName+" aa");
							 sql.append(" where aa.a0100="+salaryhistory_tmp+".a0100   ");
							 dao.update(sql.toString());
						      
							
						     sql.setLength(0);
						     
						     if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						     {
						    	 sql.append("Update  "+dessetid+" set ("+sub_str2.substring(1)+")=");
							     sql.append(" (select "+sub_str2.substring(1)+" from "+tempName);
							     sql.append(" where "+tempName+".a0100="+dessetid+".a0100  "+sqlRelation.toString()) ;
							     sql.append(" ) where   ");
							     sql.append("   exists (select null from "+tempName+" where "+tempName+".a0100="+dessetid+".a0100  "+sqlRelation.toString()+") ");
						    	 
						     }
						     else
						     { 
							     sql.append("Update  "+dessetid+" set ("+sub_str2.substring(1)+")=");
							     sql.append(" (select "+sub_str2.substring(1)+" from "+tempName);
							     sql.append(" where "+tempName+".a0100="+dessetid+".a0100  ") ;
							     sql.append(" ) where "+dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 )  and ");
							     sql.append("   exists (select null from "+tempName+" where "+tempName+".a0100="+dessetid+".a0100 ) ");
						     }
						     dao.update(sql.toString());
						     
						}
						else
						{
							
							StringBuffer strSWhere=new StringBuffer();
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
							 
								strSWhere.append(dessetid+".A0100="+salaryhistory_tmp+".A0100 "+sqlRelation2.toString()); 
								strSWhere.append(" and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
							}
							else
							{
								strSWhere.append(dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 )  ");
								strSWhere.append(" and "+dessetid+".A0100="+salaryhistory_tmp+".A0100 "); 
								strSWhere.append(" and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
							}
							
							StringBuffer strDWhere=new StringBuffer();
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{ 
								strDWhere.append(dessetid+".A0100="+salaryhistory_tmp+".A0100 "+sqlRelation2.toString());
								strDWhere.append("  and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
							}
							else
							{
								strDWhere.append(dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 ) and ");
								strDWhere.append(dessetid+".A0100="+salaryhistory_tmp+".A0100 ");
								strDWhere.append("  and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
							}	
							
							
							
							if(Sql_switcher.searchDbServer()!=2)  //不为oracle
								dbw.updateRecord(dessetid, salaryhistory_tmp+this._withNoLock,dessetid+".A0100="+salaryhistory_tmp+".A0100", value, strSWhere.toString(),"");
							else
								dbw.updateRecord(dessetid, salaryhistory_tmp,dessetid+".A0100="+salaryhistory_tmp+".A0100", value, strSWhere.toString(), strDWhere.toString());
							
							 
						}
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
		}
		
	
		
		
		/**
		 * 取得薪资临时表中归属日期，次数
		 * @param gzTempTable 临时表名
		 * @return
		 */
		private ArrayList  getZ0z1List(String gzTempTable,String filterWhl)
		{
			ArrayList list=new ArrayList();
			RowSet rowSet=null;
			try
			{
				ContentDAO dao=new ContentDAO(this.conn); 
				rowSet=dao.search("select distinct "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+" from "+gzTempTable+this._withNoLock+" where 1=1 "+filterWhl+" order by "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+"");
				while(rowSet.next())
				{ 
					String year=rowSet.getString(1);
					String month=rowSet.getString(2);
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("strym",year+"-"+month+"-01");
				
					list.add(abean);
				}
				 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				PubFunc.closeDbObj(rowSet);
			}
			return list;
		}
		 
		/**
		 * 判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
		 * @param setlist 薪资提交子集列表
		 * @param typelist  子集更新方式
		 * @author dengcan
		 * @return
		 */
		private boolean isymChangeSet(ArrayList setlist,ArrayList typelist)
		{
			boolean flag=false;
			for(int j=0;j<setlist.size();j++)
			{
				String setid=((String)setlist.get(j)).toUpperCase();
				String type=(String)typelist.get(j);
				if("2".equalsIgnoreCase(type))//当前记录不变
					continue;
				int ntype=Integer.parseInt(type);
				if((ntype==1||ntype==0)&&setid.charAt(0)=='A')
				{
					if("A00".equalsIgnoreCase(setid))
						continue;
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) //修改 当没有数据时没有新增。
						continue;
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					if(nflag==1||nflag==2)
						flag=true;
				}
			}
			return flag;
		}
		
		
		/**
		 * 取得指标的更新方式
		 * @param items 更新指标集
		 * @param uptypes 子集更新方式
		 * @author dengcan
		 * @return
		 */
		private HashMap getItemUpdateType(String items,String uptypes)
		{
			HashMap map=new HashMap();
			if(items.length()>0)
			{
				items = items.replaceAll("／", "/");
				uptypes = uptypes.replaceAll("／", "/");
				String[] item_arr=items.split("/");
				String[] uptype_arr=uptypes.split("/");
				for(int i=0;i<item_arr.length;i++)
				{
					if(item_arr[i].trim().length()>0)
					{
						map.put(item_arr[i].toLowerCase(),uptype_arr[i]);
					}
				}
			}
			return map;
		}
		
		/**
		 * 将薪资历史表中的数据同步到各临时表中（在薪资审批模块能修改了数据）
		 * @param history_where  筛选条件
		 * @param userList   薪资发起人列表
		 * @param dao  数据库操作类
		 */
		private void synHistoryToTemp(String history_where,ArrayList userList,ContentDAO dao)
		{
			RowSet rowSet=null;
			ResultSetMetaData metaData=null;
			try
			{
				String sql="update salaryhistory set appuser=';"+this.userview.getUserName()+";'"+Sql_switcher.concat()+"appuser  where 1=1 "+history_where;
				sql+=" and appuser not like  ';"+this.userview.getUserName()+";%'";//由于多次累加导致字符超长，多次审批确认不再多次累加用户名。zhanghua 2017/5/2
				dao.update(sql); 
				String standardGzItemStr = this.getStandardGzItemStr(this.salaryid);//标准薪资表涉及到的字段 主要是过滤掉临时变量的字段，这些字段历史表中不会有，更新不回来的  zhaoxg add 2016-8-11
				for(int j=0;j<userList.size();j++)
				{
					String userFlag=(String)userList.get(j);
					String tableName=userFlag+"_salary_"+this.salaryid;
					StringBuffer ss=new StringBuffer("delete from "+tableName+" where exists (select null from salaryhistory "+this._withNoLock+"  where ");
					ss.append("  salaryhistory.a00z0="+tableName+".a00z0 and salaryhistory.a00z1="+tableName+".a00z1 and upper(salaryhistory.nbase)=upper("+tableName+".nbase) and ");
					ss.append(" salaryhistory.a0100="+tableName+".a0100 "+history_where+" and upper(userflag)='"+userFlag.toUpperCase()+"'  )");
					dao.delete(ss.toString(),new ArrayList());
				
					rowSet=dao.search("select * from "+tableName+" where 1=2");
					metaData=rowSet.getMetaData();
					StringBuffer s1=new StringBuffer("");
					StringBuffer s2=new StringBuffer("");
					for(int i=1;i<=metaData.getColumnCount();i++)
					{	
						if(standardGzItemStr.indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1)
							continue;
						if("sp_flag".equalsIgnoreCase(metaData.getColumnName(i)))
						{
							s1.append(","+metaData.getColumnName(i));
							s2.append(",'03'");
						}
						else
						{
							s1.append(","+metaData.getColumnName(i));
							s2.append(","+metaData.getColumnName(i));
						}
					}
					dao.update("insert into "+tableName+" ("+s1.substring(1)+") select "+s2.substring(1)+" from salaryhistory "+this._withNoLock+" where  upper(userflag)='"+userFlag.toUpperCase()+"'  "+history_where);
				
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			finally
			{
				metaData=null;
				PubFunc.closeDbObj(rowSet);
			}
		}
		
		
		/**
		 * 获得薪资数据涉及的发起人列表
		 * @param history_where  筛选条件
		 * @return
		 * @throws GeneralException
		 */
		private ArrayList getUserList(String history_where,ContentDAO dao) throws GeneralException
		{
				RowSet rowSet=null;
			
				ArrayList userList=new ArrayList();
				try
				{
					SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
					String manager=this.salaryTemplateBo.getManager();
					if(manager!=null&&manager.length()>0)
					{
						rowSet=dao.search("select distinct userflag from salaryhistory "+this._withNoLock+" where 1=1 "+history_where);
						if(rowSet.next())
							userList.add(manager);
					}
					else
					{
						rowSet=dao.search("select distinct userflag from salaryhistory "+this._withNoLock+" where 1=1 "+history_where);
						while(rowSet.next())
							userList.add(rowSet.getString(1));
					}	
					
					if(userList.size()==0)
						throw GeneralExceptionHandler.Handle(new Exception("没有可审批确认的数据!"));	
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
				finally
				{
					PubFunc.closeDbObj(rowSet);
				}
				return userList;
		}
		
	
	/**
	 *确认时不显示数据操作方式时从数据库中获取，否则保存至数据库中
	 * @param setlist  需要归档提交的数据集列表
	 * @param typelist  数据集提交类型列表
	 * @param items 更新的指标集
	 * @param uptypes 更新指标集对应的更新方式
	 * @param ff_bosdate 发放日期
	 * @param ff_count  发放次数
	 * @return
	 */
	private LazyDynaBean saveAndGetSubmitType(ArrayList setlist,ArrayList typelist,String items,String uptypes,String ff_bosdate,String ff_count)
	{
		LazyDynaBean paramBean=new LazyDynaBean();
		try
		{
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.salaryTemplateBo.getTemplatevo().getString("lprogram"));
			String subNoShowUpdateFashion=lpbo.getValue(SalaryLProgramBo.CONFIRM_TYPE,"no_show");  //确认时不显示操作方式  1不显示数据操作方式  0:显示数据操作方式
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0"; 
			boolean isRedo=this.salaryTemplateBo.getIsRedo(ff_bosdate,ff_count,this.salaryid+"",this.userview.getUserName()); ////重发数据
			if(isRedo|| "1".equals(subNoShowUpdateFashion)) //重发数据 或 不显示数据操作方式
			{
				paramBean=getSubParam(isRedo) ;
				setlist=(ArrayList)paramBean.get("setlist");
				typelist=(ArrayList)paramBean.get("typelist");
				items=(String)paramBean.get("items");
				uptypes=(String)paramBean.get("uptypes");
			}
			else
			{
				paramBean.set("setlist", setlist);
				paramBean.set("typelist", typelist);
				paramBean.set("items", items);
				paramBean.set("uptypes", uptypes);
			}
			/**保存数据集提交方式*/
			if("0".equals(subNoShowUpdateFashion)&&!isRedo)//2016-12-06 zhanghua 重发时不保存数据操作方式
				this.salaryTemplateBo.saveSubmitType(setlist, typelist,items,uptypes,this.salaryid); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return paramBean;
	}
	
	
	 /** 薪资数据提交
	 * @param setlist				需要归档提交的数据集列表
	 * @param typelist				数据集提交类型列表
	 * @param items				更新指标集
	 * @param uptypes			更新方式
	 * @param ff_bosdate       发放日期
	 * @param ff_count           发放次数
	 */
	public void submitGzData(ArrayList setlist,ArrayList typelist,String items,String uptypes,String ff_bosdate,String ff_count) throws GeneralException
	{
		RowSet frowset=null;
		try
		{
			boolean isComputeTax = isComputeTax(ff_bosdate, ff_count);
			if(!isComputeTax) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.reCompute")));
			}
			LazyDynaBean subParamBean=saveAndGetSubmitType(setlist,typelist,items,uptypes,ff_bosdate, ff_count);  //确认时不显示数据操作方式时从数据库中获取，否则保存至数据库中
			setlist=(ArrayList)subParamBean.get("setlist");
			typelist=(ArrayList)subParamBean.get("typelist");
			items=(String)subParamBean.get("items");
			uptypes=(String)subParamBean.get("uptypes");
			/**汇总薪资总额*/ 
			collectGzTotalData(ff_bosdate);
			String filterWhl=this.salaryTemplateBo.getFilterAndPrivSql_ff();
			ContentDAO dao=new ContentDAO(this.conn);
			com.hjsj.hrms.businessobject.gz.SalaryTemplateBo gzbo=new com.hjsj.hrms.businessobject.gz.SalaryTemplateBo(this.conn,salaryid,this.userview);
			//使用实际薪资临时表中数据所涉及到的人员库进行提交，防止由于人员移库到此薪资类别没有授权的人员库，导致数据无法提交到人员档案库中。
			//zhanghua 2017-4-7  26955
			String strsql="select upper(nbase) as nbase from "+gzbo.getGz_tablename()+" where sp_flag<>'06' group by upper(nbase)";
			RowSet rs=dao.search(strsql);
			String nbase="";
			while (rs.next()) 
				nbase+=rs.getString("nbase")+",";
			
			if(StringUtils.isNotBlank(nbase))
				gzbo.getTemplatevo().setString("cbase", nbase);
		//	DbNameBo.autoAddZ1(this.conn, this.userview,this.salaryTemplateBo.getGz_tablename(), salaryid+"",this.salaryTemplateBo.getManager(),false,true);
			DbNameBo.autoAddZ1_subhistory(this.userview,this.salaryTemplateBo.getGz_tablename(), salaryid+"",this.salaryTemplateBo.getManager(),false,true,gzbo.getCtrlparam(),gzbo.getGzitemlist(),filterWhl);
			
		//	com.hjsj.hrms.businessobject.gz.SalaryTemplateBo gzbo=new com.hjsj.hrms.businessobject.gz.SalaryTemplateBo(this.conn,salaryid,this.userview);
			gzbo.setFilterWhl(filterWhl);
			gzbo.submitGzData(setlist, typelist,items,uptypes);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeDbObj(frowset);
		}
	}
	
	
	/**
	 * 汇总薪资总额
	 * @param ff_bosdate  发放日期
	 */
	private void collectGzTotalData(String ff_bosdate) throws GeneralException
	{ 
		String isControl=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
		SalaryTotalBo totalBo=new SalaryTotalBo(this.conn,this.userview,salaryid);
		if(totalBo.getGzXmlMap()!=null&& "1".equals(isControl))
		{  
			GzAmountXMLBo gzAmountXMLBo=new GzAmountXMLBo(this.conn,1);
			HashMap gzXmlMap=gzAmountXMLBo.getValuesMap();
			String ctrl_by_level=(String)gzXmlMap.get("ctrl_by_level");
			if(ctrl_by_level!=null&& "1".equals(ctrl_by_level))
				totalBo.collectData(ff_bosdate.split("-")[0]);
		}
		
	}
	
	/**
	 * 获得数据提交子集\指标更新方式参数
	 * @param isRedo 是否重发
	 * @return
	 * @throws GeneralException
	 */
	private LazyDynaBean getSubParam(boolean isRedo) throws GeneralException
	{
		LazyDynaBean paramBean=new LazyDynaBean();
		/**归档数据集*/
		ArrayList setlist=new ArrayList();
		/**提交方式*/
		ArrayList typelist=new ArrayList();
		String items="";  //更新指标集
		String uptypes="";  //更新方式
		if(isRedo) //重发薪资提交时默认不变的子集提交方式仍为不变
		{ 
			ArrayList list=this.salaryTemplateBo.getSubmitTypeList(this.salaryid+"");
			LazyDynaBean abean=null;
			HashMap map = new HashMap();
			ArrayList _setlist=new ArrayList();
			HashMap setTypeMap=new HashMap();
			for(int i=0;i<list.size();i++)
			{
				map=(HashMap)list.get(i);
				String _setid=((String)map.get("setid")).trim();
				_setlist.add(_setid);
				setTypeMap.put(_setid.toLowerCase(), ((String)map.get("type")).trim());
			} 
			for(int i=0;i<_setlist.size();i++)
			{
				String _setid=(String)_setlist.get(i);
				if(_setid.charAt(0)!='A')
					continue;
				if("A00".equalsIgnoreCase(_setid))
					continue;
				setlist.add(_setid);
				if(setTypeMap.get(_setid.toLowerCase())!=null&& "2".equalsIgnoreCase((String)setTypeMap.get(_setid.toLowerCase()))) //20140924  dengcan  重发薪资时默认不变的子集重发提交仍不变
					typelist.add("2");
				else if(setTypeMap.get(_setid.toLowerCase())!=null&& "1".equalsIgnoreCase((String)setTypeMap.get(_setid.toLowerCase())))
					typelist.add("3");//新增改为更新(薪资重发时) 解决重发时插入人导致归属次数重复 zhanghua 2018-2-28
//				else if(_setid.equalsIgnoreCase("A01")) /**(0,1,2)=(更新,新增,不变)*/  a01也遵守默认更新 不变仍不变的规则 zhanghua 2017-8-7
//					typelist.add("2");
				else
					typelist.add("0");
			}
		}
		else
		{ 
			ArrayList list=this.salaryTemplateBo.getSubmitTypeList(this.salaryid+"");
			LazyDynaBean abean=null;
			HashMap map = new HashMap();
			StringBuffer sets=new StringBuffer("");
			for(int i=0;i<list.size();i++)
			{
				map=(HashMap)list.get(i);
				setlist.add((String)map.get("setid"));
				typelist.add((String)map.get("type"));
				if("0".equals((String)map.get("type")))
					sets.append("/"+(String)map.get("setid"));
			}
			if(sets.length()>0)
			{
				ArrayList gzItemList=this.salaryTemplateBo.getUpdateItemList(sets.toString().split("/"),this.salaryid+"");
				for(int i=0;i<gzItemList.size();i++)
				{
					abean=(LazyDynaBean)gzItemList.get(i);
					String itemid=(String)abean.get("itemid");
					String flag=(String)abean.get("flag");
					items+="/"+itemid;
					uptypes+="/"+flag;
				}
			}
		}
		paramBean.set("setlist", setlist);
		paramBean.set("typelist", typelist);
		paramBean.set("items", items);
		paramBean.set("uptypes", uptypes); 
		return paramBean;
	}
	
	/**
	 * @Title: getYearMonthCount 
	 * @Description: 取得当前薪资表处理的发放日期和次数
	 * 如果初次使用,历史记录表为空，当前处到的日期为系统日期
	 * 当前处理次数为1.
	 * @return 返回HashMap 取得ym(业务日期),yyyy-MM-dd,取得count(发放次数),
	 * @author lis  
	 * @date 2015-10-13 下午06:05:03
	 */
	public HashMap getYearMonthCount()
	{
		HashMap mp=new HashMap();
		
		try
		{
			String username=this.userview.getUserName();
			String manager = this.salaryTemplateBo.getManager();
			if(StringUtils.isNotBlank(manager))
				username=manager;
			LazyDynaBean abean=this.searchCurrentDate(String.valueOf(this.salaryid),username);
			String strYm=(String)abean.get("strYm");
			String strC=(String)abean.get("strC");			
			if("".equalsIgnoreCase(strYm))
			{
				String appdate=ConstantParamter.getAppdate(this.userview.getUserName());
				if(appdate==null||appdate.trim().length()==0)
				{
					strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
				}
				else
					strYm=appdate.replaceAll("\\.","-");
				
				String[] tmp=StringUtils.split(strYm, "-");
				strYm=tmp[0]+"-"+tmp[1]+"-01";
				strC="1";
			
				DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,strYm,strC,this.conn);
			}
			mp.put("ym",strYm);
			mp.put("count", strC);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	  /**
	 * 如果排序指标是否有效
	 * @param sort_str
	 * @param accountBo
	 * @return
     * @throws GeneralException 
	 */
	public boolean isExistErrorItem(String sort_str) throws GeneralException
	{
		boolean flag=true;
		try {
			String[] temps=sort_str.toUpperCase().split(",");
			String zgItemStr=this.getStandardGzItemStr(this.salaryid);
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].length()>0)
				{
					String _str=temps[i].replaceAll("ASC", "");
					_str=_str.replaceAll("DESC", "");
					_str=_str.trim();
					if(DataDictionary.getFieldItem(_str.toLowerCase())!=null&&zgItemStr.indexOf(_str+"/")==-1)
					{
						flag=false;
						break;
					}
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	
	/**
	 * 创建薪资导入模板数据临时表
	 * @param tablename
	 * @throws GeneralException
	 */
	public void createImportTable(String tablename,SalaryTemplateBo gzbo) throws GeneralException {
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tablename);
	//		if(dbw.isExistTable(tablename, false))
			{
				dbw.dropTable(table);
			}
			
			Field field=new Field("NBASE","NBASE");
			field.setDatatype(DataType.STRING);
			field.setLength(3);
			field.setNullable(false);
			field.setKeyable(true);
			table.addField(field);
			
			//人员编号
			field=new Field("A0100","A0100");
			field.setDatatype(DataType.STRING);
			field.setLength(20);
			field.setNullable(false);
			field.setKeyable(true);	
			table.addField(field);

			//姓名
			field=new Field("A0101","A0101");
			field.setDatatype(DataType.STRING); 
			field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());
			field.setNullable(false);
			field.setKeyable(true);
			table.addField(field);	
			
			//归属日期
			field=new Field("A00Z0","A00Z0");
			field.setDatatype(DataType.DATE);
			field.setLength(20);
			field.setNullable(false);
			field.setKeyable(true);
			table.addField(field);
			
			//归属次数
			field=new Field("A00Z1","A00Z1");
			field.setDatatype(DataType.INT);
			field.setNullable(false);
			field.setLength(20);
			field.setKeyable(true);
			table.addField(field);
			
			//部门
			field=new Field("B0110","B0110");
			field.setDatatype(DataType.STRING);
			field.setLength(30);			
			table.addField(field);	
			
			//单位
			field=new Field("E0122","E0122");
			field.setDatatype(DataType.STRING);
			field.setLength(30);			
			table.addField(field);	
			
			//人员库id
			field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(8);			
			table.addField(field);	
			
			//人员排序
			field=new Field("A0000","A0000");
			field.setDatatype(DataType.INT);
			field.setLength(10);			
			table.addField(field);	
			
			String deptid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
			deptid = deptid != null ? deptid : ""; 
			String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
			orgid = orgid != null ? orgid : "";
			
			if(StringUtils.isNotBlank(deptid)){
				//归属部门
				field=new Field(deptid,deptid);
				field.setDatatype(DataType.STRING);
				field.setLength(50);			
				table.addField(field);	
			}
			if(StringUtils.isNotBlank(orgid)){
				//归属单位
				field=new Field(orgid,orgid);
				field.setDatatype(DataType.STRING);
				field.setLength(50);			
				table.addField(field);	
			}
			
			
			dbw.createTable(table);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 导入时删除不在条件范围中的人员
	 * @date 2016-1-8
	 * @param tableName  表面
	 * @param insertRecordMap  参数
	 * @return
	 */
	public HashSet delNoConditionData(String tablename,HashMap insertRecordMap,boolean controlPriv)
	{
		int num=0;
		HashSet keySet=new HashSet();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			/**导入数据*/
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String flag=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String aflag=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			
			String cond=this.salaryTemplateBo.getTemplatevo().getString("cond");
			String cexpr=this.salaryTemplateBo.getTemplatevo().getString("cexpr");		
			String sql="";
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			for(int i=0;i<dbarr.length;i++)
			{
					String pre=dbarr[i];
					
					/*if(aflag!=null&&aflag.equals("1")&&!controlPriv)
					{
						String asql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )";
						rowSet=dao.search(asql);
						while(rowSet.next())
						{
							String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
							keySet.add(key);
						}
						
						asql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )";
						dao.delete(asql,new ArrayList());
					}*/
					// 对比旧代码，这里只需要一段就行，全部按照最新的权限走
				if ((aflag!=null&& "1".equals(aflag)&&!controlPriv) || (StringUtils.isNotBlank(this.getSalaryTemplateBo().getManager()) && !this.userview.getUserName().equalsIgnoreCase(this.getSalaryTemplateBo().getManager()))) {//数据上报，删除没有权限的人员。
					String asql = "select a00z0,a00z1,nbase,a0100 from " + tablename + " t1 where upper(t1.nbase)='" + pre.toUpperCase() + "' and a0100 not in (select a0100 from " + tablename + " t2 where 1=1 " +
							this.getSalaryTemplateBo().getWhlByUnits(this.getSalaryTemplateBo().getGz_tablename(), true).replaceAll(this.getSalaryTemplateBo().getGz_tablename(), "t2") + " and t1.a00z0=t2.a00z0 and"
							+ " t1.a00z1=t2.a00z1 and t1.nbase=t2.nbase and  t1.a0100=t2.a0100  )";
					rowSet = dao.search(asql);
					while (rowSet.next()) {
						String key = pre.toLowerCase() + "|" + rowSet.getString("a0100") + "|" + df.format(rowSet.getDate("a00z0")) + "|" + rowSet.getString("a00z1");
						keySet.add(key);
					}
					asql = "delete from " + tablename + " where upper(nbase)='" + pre.toUpperCase() + "' and a0100 not in (select a0100 from " + tablename + " t2 where 1=1 " +
							this.getSalaryTemplateBo().getWhlByUnits(tablename, true).replaceAll(tablename, "t2") + " and " + tablename + " .a00z0=t2.a00z0 and"
							+ " " + tablename + " .a00z1=t2.a00z1 and " + tablename + " .nbase=t2.nbase and  " + tablename + " .a0100=t2.a0100  )";
					dao.delete(asql, new ArrayList());
				}


					
					if(flag!=null&& "0".equals(flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql ="";
						if(factor.size()>0)
						{
							strSql=factor.getSqlExpression();	
							
							sql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not in (select "+pre+"a01.a0100 "+strSql+" )";
							rowSet=dao.search(sql);
							while(rowSet.next())
							{
									String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
									keySet.add(key);
							} 
							
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not in (select "+pre+"a01.a0100 "+strSql+" )";
							dao.delete(sql,new ArrayList());
						}
					}
					if(flag!=null&& "1".equals(flag)&&cond.length()>0)  // 1：复杂条件
					{
						
						int infoGroup = 0; // forPerson 人员
						int varType = 8; // logic	
						
						String whereIN="select a0100 from "+pre+"A01";
						alUsedFields.addAll(this.getMidVariableList());
						YksjParser yp = new YksjParser(this.userview ,alUsedFields,
								YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
						YearMonthCount ymc=null;							
						yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
						String tempTableName = yp.getTempTableName();
						String w = yp.getSQL();
						if(w!=null&&w.trim().length()>0)
						{
							
							sql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not ";
							sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
							rowSet=dao.search(sql);
							while(rowSet.next())
							{
									String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
									keySet.add(key);
							} 
							
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not ";
							sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
							dao.delete(sql,new ArrayList());
						}
					}
			}
		
//			for(Iterator t=keySet.iterator();t.hasNext();)
//			{
//				String str=(String)t.next();
//				if(insertRecordMap.get(str)!=null)
//					insertRecordMap.remove(str);
//					num++;
//			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return keySet;
	}
	
	/**
	 * 查询薪资类别对应的项目
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList searchGzItem() throws GeneralException 
	{
		ArrayList list=new ArrayList();
		StringBuffer strread=new StringBuffer();
		/**只读字段*/
		strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,E01A1");
		StringBuffer format=new StringBuffer();	
		format.append("###################");		
		StringBuffer buf=new StringBuffer();
		buf.append("select * from salaryset where salaryid=?");
		buf.append(" order by sortid");
		LazyDynaBean abean=null;
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			paralist.add(String.valueOf(salaryid));

			rset=dao.search(buf.toString(),paralist);
		
			boolean isOk=false;
//			加上报审标识
			if(this.getSalaryTemplateBo().getManager().length()>0)
			{
				abean = new LazyDynaBean();
				abean.set("itemid","sp_flag2");
				abean.set("itemdesc","报审状态");
				abean.set("itemtype",DataType.STRING);
				abean.set("codesetid","23");
				abean.set("itemlength",50);
				abean.set("nlock","1");
				
				list.add(abean);
				
				isOk=true;
			}
			if(isApprove())
			{
				/**加上审批标识*/
				abean = new LazyDynaBean();
				abean.set("itemid","sp_flag");
				abean.set("itemdesc",ResourceFactory.getProperty("label.gz.sp"));
				abean.set("itemlength",50);
				abean.set("codesetid","23");
				abean.set("itemtype",DataType.STRING);
				abean.set("nlock","1");
				
				list.add(abean);
				
				isOk=true;
			}
			if(isOk)
			{
				/**加上审批意见*/
				abean = new LazyDynaBean();
				abean.set("itemid","appprocess");
				abean.set("itemdesc","审批意见");
				abean.set("align","left");
				abean.set("itemtype",DataType.CLOB);
				abean.set("nlock","1");
				
				list.add(abean);
			}
			
			//追加标记
			abean = new LazyDynaBean();
			abean.set("itemid","add_flag");
			abean.set("itemdesc","追加标记");
			abean.set("align","left");
			abean.set("itemtype",DataType.INT);
			abean.set("nlock","1");
			list.add(abean);
			
			String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
			while(rset.next())
			{
				String itemid=rset.getString("itemid");
				if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
				{
					FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(_tempItem==null)
						continue;
					
				}
				/**指标隐藏时，把此字段设置为0*/
				int nwidth=rset.getInt("nwidth");
				if("a01z0".equalsIgnoreCase(itemid)&&this.getSalaryTemplateBo().getCtrlparam()!=null)
				{
					String a01z0Flag=this.getSalaryTemplateBo().getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
					if(a01z0Flag==null|| "0".equals(a01z0Flag))
					{
						nwidth=0;
					}
				}
				
				abean = new LazyDynaBean();
				abean.set("itemid",itemid);
				abean.set("itemdesc",rset.getString("itemdesc"));
				abean.set("initflag",rset.getString("initflag"));
				abean.set("fieldsetid",rset.getString("fieldsetid"));
				abean.set("heapflag",rset.getString("heapflag"));
				abean.set("formula",Sql_switcher.readMemo(rset, "formula")); 
				
				String type=rset.getString("itemtype");
				String codesetid=rset.getString("codesetid");
				abean.set("codesetid",codesetid);
				
				/**字段为代码型,长度定为50*/
				if("A".equals(type))
				{
					abean.set("itemtype",DataType.STRING);
					
					if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
						abean.set("itemlength",rset.getString("itemlength"));
					else
						abean.set("itemlength","50");
					abean.set("align","left");
				}
				else if("M".equals(type))
				{
					abean.set("itemtype",DataType.CLOB);
					abean.set("align","left");;				
				}
				else if("N".equals(type))
				{

					abean.set("itemlength",rset.getString("itemlength"));
					int ndec=rset.getInt("decwidth");
					abean.set("decwidth",ndec);
					if(ndec>0)
					{
						abean.set("itemtype",DataType.FLOAT);
						abean.set("format","####."+format.toString().substring(0,ndec));
					}
					else
					{
						abean.set("itemtype",DataType.INT);
						abean.set("format","####");
					}
					abean.set("align","right");		
				}	
				else if("D".equals(type))
				{
					abean.set("itemlength","20");
					abean.set("itemtype",DataType.DATE);
					abean.set("format","yyyy.MM.dd");
					abean.set("align","right");						
				}	
				else
				{
					abean.set("itemtype",DataType.STRING);
					abean.set("itemlength",rset.getString("itemlength"));
					abean.set("align","left");	
				}
				/**对人员库标识，采用“@@”作为相关代码类*/
				if("nbase".equalsIgnoreCase(itemid))
				{
					abean.set("codesetid","@@");
					abean.set("nlock","1");
				}
				if(nwidth==0)
					abean.set("visible","0");

				abean.set("sortable","1");
				/**设置只读字段*/
				int idx=strread.indexOf(itemid.toUpperCase());
				if(idx!=-1)
					abean.set("nlock","1");
				else
				{
					/**分析指标权限*/
					if("1".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						abean.set("nlock","1"); //读权限
					}
					if(!("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))&& "0".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						abean.set("visible","0");//无权限
					}	
					if("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid))
						abean.set("visible","1");
					
				}
				list.add(abean);
			}//loop end.

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return list;
	}	
	/**
	 * 将符合发放日期的薪资历史数据 提交到 档案库中（修改档案库中对应的数据）
	 *  zhaoxg add 2016-1-13
	 * @param bosdate 业务日期(发放日期)
	 * @param count   发放次数
	 * @return
	 */
	public ArrayList<String[]> submitGzDataFromHistory(String bosdate,String count,String[] records)throws GeneralException
	{
		boolean bflag=true;
		ArrayList<String[]> changeList=new ArrayList<String[]>();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList setList=this.salaryTemplateBo.getSubmitTypeList(salaryid+"");
			/** 取得数据集的年月标识 */
			HashMap   setChangeflagMap=getFieldSetChangeflag(setList);  
			LazyDynaBean payDateBean=getSalaryPayDate(bosdate,count);
			/** 薪资历史数据表取数条件 sql */
			String history_condWhl=getWhlConditionSql(payDateBean);
			String history_condWhl2=history_condWhl;
			/**取得人员库前缀列表*/
			String dbpres=this.salaryTemplateBo.getTemplatevo().getString("cbase");
			ArrayList _salaryItemList=this.salaryTemplateBo.getSalaryItemList("", salaryid+"", 1);
			ArrayList salaryItemList=new ArrayList();
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
				subNoPriv="0";
			for(int e=0;e<_salaryItemList.size();e++)
			{
				LazyDynaBean itemBean=(LazyDynaBean)_salaryItemList.get(e);
				String a_setid=(String)itemBean.get("fieldsetid");
				String a_itemid=(String)itemBean.get("itemid"); 
				FieldItem fielditem=DataDictionary.getFieldItem(a_itemid.toLowerCase());
				if(fielditem!=null&&a_setid.charAt(0)=='A'&&!"A00".equalsIgnoreCase(a_setid)&&!"A0100".equalsIgnoreCase(a_itemid))
				{
					if("0".equalsIgnoreCase(fielditem.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equalsIgnoreCase(this.userview.analyseFieldPriv(a_itemid.toLowerCase())))
							continue;
					} 
				}
				salaryItemList.add(itemBean);
			}
 
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			DbWizard dbw=new DbWizard(this.conn);
			String [] strList=new String[4];
			HashMap nbaseMap=this.getNbaseMap(dao);
			for(int i=0;i<dbarr.length;i++)
			{
				StringBuffer buf2=new StringBuffer("");
				StringBuffer buf3=new StringBuffer("");
				HashMap a0100Map=new HashMap();
				for(int f=0;f<records.length;f++)
				{
					if(records[f].length()>0)
					{
						strList=new String[4];
						String[] temp=records[f].split("/");
						String temp0 = PubFunc.decrypt(temp[0]);
						String temp1 = PubFunc.decrypt(temp[1]);
						
						if(changeList.size()<=8){
							strList[0]=(String)nbaseMap.get(temp1.toUpperCase());//nbase
							strList[1]=this.getA0101ByA0100(temp0, temp1, dao);//a0100
							strList[2]=temp[2];//A00Z0
							strList[3]=temp[3];//A00Z1
						}
						
						
						if(temp1.trim().equalsIgnoreCase(dbarr[i]))
						{
							if(changeList.size()<=8)
								changeList.add(strList);
							buf2.append(" or (A0100='"+temp0+"'  and  A00Z0="+Sql_switcher.dateValue(temp[2])+" and A00z1="+temp[3]+" )");
							if(a0100Map.get(temp0)==null)
							{
								buf3.append(" or (A0100='"+temp0+"'  and  A00Z0="+Sql_switcher.dateValue(temp[2])+" and A00z1="+temp[3]+" )");
								a0100Map.put(temp0,"1");
							}
						}
					}
				}
				if(buf2.length()>0)
				{
					history_condWhl=" and ("+buf2.substring(3)+") and salaryid="+this.salaryid+" "+history_condWhl;
					history_condWhl2=" and ("+buf3.substring(3)+") and salaryid="+this.salaryid+" "+history_condWhl2;
				}
				else
				{
					history_condWhl+=" and 1=2";
					history_condWhl2+=" and 1=2";
				}
				
				for(int j=0;j<setList.size();j++)
				{
					HashMap setMap=(HashMap)setList.get(j);
					String setid=(String)setMap.get("setid");
					//更新主集中的数据
					if("A01".equalsIgnoreCase(setid))
					{
						for(int e=0;e<salaryItemList.size();e++)
						{
							LazyDynaBean itemBean=(LazyDynaBean)salaryItemList.get(e);
							String a_setid=(String)itemBean.get("fieldsetid");
							String a_itemid=(String)itemBean.get("itemid");
							if("A01".equals(a_setid))
							{
								if("A0100".equalsIgnoreCase(a_itemid))
									continue;
								StringBuffer sql=new StringBuffer("update "+dbarr[i]+"a01 set "+a_itemid+"=(select "+a_itemid);
								 sql.append(" from salaryhistory where upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl2+" and salaryhistory.a0100="+dbarr[i]+"a01.a0100 ) ");
								 sql.append(" where a0100 in (select a0100 from salaryhistory where upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl2+" )");								 
								 dao.update(sql.toString());
							}
						}
					}
					else
						updateSubSetData(dbarr,setid,dao,salaryItemList,setChangeflagMap,i,history_condWhl);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);	
		}
		return changeList;	
	}
	
	private String getA0101ByA0100(String a0100,String nbase,ContentDAO dao){
		String name="";
		try{
			String sql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
			RowSet rs=dao.search(sql);
			if(rs.next()){
				name=rs.getString("a0101");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
	}
	
	private HashMap getNbaseMap(ContentDAO dao){
		HashMap mp=new HashMap<String,String>();
		try{
			String sql="select pre,dbname from DBName";
			RowSet rs=dao.search(sql);
			while(rs.next()){
				mp.put(rs.getString("pre").toUpperCase(), rs.getString("dbname"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return mp;
		
	}
	/**
	 * 更新子集中的数据
	 *  zhaoxg add 2016-1-13
	 * @param dbarr
	 * @param setid
	 * @param dao
	 * @param salaryItemList
	 * @param setChangeflagMap
	 * @param i
	 * @param history_condWhl
	 */
	public void updateSubSetData(String[] dbarr,String setid,ContentDAO dao,ArrayList salaryItemList,HashMap setChangeflagMap,int i,String history_condWhl)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pst=null;
		RowSet rowSet = null;
		RowSet rowSet2=null;
		try
		{
			String strNow=Sql_switcher.sqlNow();
			String sql = null;
			String tableName=dbarr[i]+setid;
			/** 子集是否有发放日期 */
			boolean is_payDate=isPayDateFlag(tableName,setid);
			rowSet = dao.search("select max(i9999) i9999,a0100 from  "+tableName+"  where a0100 in (select a0100 from salaryhistory where upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl+" )  group by a0100 ");
			HashMap aToi9999_map=new HashMap();
			while(rowSet.next())
			{
				aToi9999_map.put(rowSet.getString(2),rowSet.getString(1));
			}
			StringBuffer sql_up=new StringBuffer("");
			StringBuffer sql_select=new StringBuffer("");
			int num=0;
			for(int e=0;e<salaryItemList.size();e++)
			{
				LazyDynaBean itemBean=(LazyDynaBean)salaryItemList.get(e);
				String a_setid=(String)itemBean.get("fieldsetid");
				String a_itemid=(String)itemBean.get("itemid");
				if(setid.equalsIgnoreCase(a_setid))
				{
					num++;
					sql_up.append(","+a_itemid+"=?");
					sql_select.append(","+a_itemid);
				}
			}
			if(is_payDate)
			{
				sql_up.append(",a00z2=?,a00z3=?");
				sql_select.append(",a00z2,a00z3");
				num+=2;
			}
			if(sql_up.length()>0)
			{
				if("0".equals(setChangeflagMap.get(setid.toLowerCase()))){
					sql= "update "+tableName+" set "+sql_up.substring(1)+" where a0100=? and i9999=? ";
					pst=this.conn.prepareStatement(sql);
				}else{
					sql="update "+tableName+" set "+sql_up.substring(1)+" where "+Sql_switcher.year(setid+"Z0")+"=? and  "+Sql_switcher.month(setid+"Z0")+"=? and "+Sql_switcher.day(setid+"Z0")+"=? and "+setid+"Z1=? and a0100=?";
					pst=this.conn.prepareStatement(sql);
				}	
					rowSet=dao.search("select "+sql_select.substring(1)+",a0100,a00z0,a00z1 from salaryhistory where  upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl);
					while(rowSet.next())
					{
						int index=0;
						for(int a=0;a<salaryItemList.size();a++)
						{
							LazyDynaBean itemBean=(LazyDynaBean)salaryItemList.get(a);
							String a_setid=(String)itemBean.get("fieldsetid");
							String a_itemid=(String)itemBean.get("itemid");
							String a_itemtype=(String)itemBean.get("itemtype");
							String a_decwidth=(String)itemBean.get("decwidth");
							if(setid.equalsIgnoreCase(a_setid))
							{
								index++;
								setPreparedStatementValue(pst,rowSet,a_itemtype,a_decwidth,a_itemid,index);
							}
						}
						if(is_payDate)
						{
							pst.setDate(++index,rowSet.getDate("a00z2"));
							pst.setInt(++index,rowSet.getInt("a00z3"));
						}
						if("0".equals(setChangeflagMap.get(setid.toLowerCase()))){
							pst.setString(num+1,rowSet.getString("a0100"));
							if(aToi9999_map.get(rowSet.getString("a0100"))==null)
							{
								String strIns=",createtime,createusername";
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								StringBuffer buf=new StringBuffer("");
								buf.append("insert into ");
								buf.append(tableName);
								buf.append("(A0100,I9999");
								buf.append(strIns);
								buf.append(") values ('"+rowSet.getString("a0100")+"',1"+strvalue+") ");
								dao.update(buf.toString());
								aToi9999_map.put(rowSet.getString("a0100"), "1");
							}
							pst.setInt(num+2,Integer.parseInt((String)aToi9999_map.get(rowSet.getString("a0100"))));
						}else{
							Date a00z0=rowSet.getDate("a00z0");
							int a00z1=rowSet.getInt("a00z1");
							Calendar d=Calendar.getInstance();
							d.setTimeInMillis(a00z0.getTime());
							String _sql="select * from "+tableName+" where a0100='"+rowSet.getString("a0100")+"' and ";
							_sql+=Sql_switcher.year(setid+"Z0")+"="+d.get(Calendar.YEAR)+" and  "+Sql_switcher.month(setid+"Z0")+"="+(d.get(Calendar.MONTH)+1);
							_sql+=" and "+setid+"Z1="+a00z1;
							rowSet2=dao.search(_sql);
							boolean flag=true;
							if(rowSet2.next())
							{
								flag=false;
							}
							if(flag)
							{
								StringBuffer buf=new StringBuffer("");
								String strIns=",createtime,createusername,"+setid+"Z0,"+setid+"Z1";
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								if(aToi9999_map.get(rowSet.getString("a0100"))!=null)
								{
									buf.append("insert into "+tableName+" (A0100,I9999"+strIns+") "); 
									buf.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1 from ");
									buf.append(" (select a0100,a00z1,a00z0 from salaryhistory where salaryid="+this.salaryid+" and a0100='"+rowSet.getString("a0100")+"' and upper(nbase)='"+dbarr[i].toUpperCase()+"' ");
									buf.append(" and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+" and  "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+"  and A00Z1="+a00z1+"  ) a1, ");
									buf.append("( select a0100,i9999 from "+tableName+" a where a.i9999=(select max(b.i9999) from "+tableName+" b where a.a0100=b.a0100 ) ) a2 ");
									buf.append("where a1.a0100=a2.a0100 ");
									dao.update(buf.toString());
						 		}
								else
								{
									buf.append("insert into ");
									buf.append(tableName);
									buf.append("(A0100,I9999,createtime,createusername,"+setid+"Z1"); 
									buf.append(") values ('"+rowSet.getString("a0100")+"',1"+strvalue+","+a00z1+") ");
									dao.update(buf.toString());
									buf.setLength(0);
									buf.append("update "+tableName+" set "+setid+"Z0=(select a00z0 from salaryhistory  where "+tableName+".a0100=salaryhistory.a0100 and salaryid="+this.salaryid+" and a0100='"+rowSet.getString("a0100")+"' and upper(nbase)='"+dbarr[i].toUpperCase()+"' ");
									buf.append(" and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+" and  "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+"  and A00Z1="+a00z1+"  ) where ");
									buf.append(" a0100='"+rowSet.getString("a0100")+"' and i9999=1 ");
									dao.update(buf.toString());
								}
							}
							pst.setInt(num+1,d.get(Calendar.YEAR));
							pst.setInt(num+2,d.get(Calendar.MONTH)+1);
							pst.setInt(num+3,d.get(Calendar.DATE));
							pst.setInt(num+4,a00z1);
							pst.setString(num+5,rowSet.getString("a0100"));
						}
						pst.addBatch();
					}
					// 打开Wallet
					dbS.open(conn, sql);
					pst.executeBatch();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			PubFunc.closeDbObj(pst);
			PubFunc.closeDbObj(rowSet2);
			PubFunc.closeDbObj(rowSet);
		}
	}
	/**
	 * 判断是否有 a00z2,a00z3 并且字段类型也符合 日期/整数 类型
	 *  zhaoxg add 2016-1-13
	 * @param tableName
	 * @return
	 */
	public boolean isPayDateFlag(String tableName,String setid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo(tableName.toLowerCase());
			if(vo.hasAttribute("a00z2")&&vo.hasAttribute("a00z3"))
			{
				boolean is_a00z2=false;
				boolean is_a00z3=false;
				RowSet rowSet=dao.search("select * from fielditem where useflag=1 and ((fieldsetid='"+setid+"' and itemid='a00z2' ) or (fieldsetid='"+setid+"' and itemid='a00z3' ) )");
				while(rowSet.next())
				{
					if("a00z2".equalsIgnoreCase(rowSet.getString("itemid"))&& "D".equals(rowSet.getString("itemtype")))
						is_a00z2=true;
					if("a00z3".equalsIgnoreCase(rowSet.getString("itemid"))&& "N".equals(rowSet.getString("itemtype"))&&rowSet.getInt("decimalwidth")==0)
						is_a00z3=true;
				}
				if(is_a00z2&&is_a00z3)
					flag=true;
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 *  zhaoxg add 2016-1-13
	 * @param pst
	 * @param rowSet
	 * @param a_itemtype
	 * @param a_decwidth
	 * @param a_itemid
	 * @param index
	 */
	public void setPreparedStatementValue(PreparedStatement pst,RowSet rowSet,String a_itemtype,String a_decwidth,String a_itemid,int index)
	{
		try
		{
			if("A".equals(a_itemtype))
			{
				pst.setString(index,rowSet.getString(a_itemid));
			}
			else if("N".equals(a_itemtype))
			{
				if("0".equals(a_decwidth))
				{
					pst.setInt(index,rowSet.getInt(a_itemid));
				}
				else
				{
					pst.setDouble(index,rowSet.getDouble(a_itemid));
				}
			}
			else if("D".equals(a_itemtype))
			{
				pst.setDate(index,rowSet.getDate(a_itemid));
			}
			else if("M".equals(a_itemtype))
			{
				pst.setString(index,Sql_switcher.readMemo(rowSet,a_itemid));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取得数据集的年月标识
	 *  zhaoxg add 2016-1-13
	 * @param setList
	 * @return
	 */
	public HashMap getFieldSetChangeflag(ArrayList setList)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<setList.size();i++)
			{
				HashMap setMap=(HashMap)setList.get(i);
				String setid=(String)setMap.get("setid");
				whl.append(",'"+setid+"'");
			}
			RowSet rowSet=dao.search("select fieldsetid,changeflag  from fieldset where fieldsetid in ("+whl.substring(1)+")");
			while(rowSet.next())
			{
				map.put(rowSet.getString(1).toLowerCase(),rowSet.getString(2));
			}
			rowSet.close();
		}
		catch(Exception ex)
		{
				ex.printStackTrace();	
		}
		return map;
	}
	/**
	 * 取得 工资数据表 的当前 发放时间
	 *  zhaoxg add 2016-1-13
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getSalaryPayDate(String bosdate,String count)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			if(bosdate==null)
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select a00z2,a00z3 from "+this.salaryTemplateBo.getGz_tablename());
				if(rowSet.next())
				{
					Calendar c=Calendar.getInstance();
					Date d=rowSet.getDate(1);
					c.setTime(d);
					String a00z3=rowSet.getString(2);
					abean.set("year",String.valueOf(c.get(Calendar.YEAR)));
					abean.set("month",String.valueOf(c.get(Calendar.MONTH)+1));
					abean.set("day",String.valueOf(c.get(Calendar.DATE)));
					abean.set("count",a00z3);
				}
				rowSet.close();
			}
			else
			{
				String[] temps=bosdate.split("\\.");
				abean.set("year",temps[0]);
				abean.set("month",String.valueOf(Integer.parseInt(temps[1])));
				abean.set("day",String.valueOf(Integer.parseInt(temps[2])));
				abean.set("count",count);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	/**
	 * 薪资历史数据表取数条件  sql
	 * zhaoxg add 2016-1-13
	 * @param payDateBean
	 * @return
	 */
	public String getWhlConditionSql(LazyDynaBean payDateBean)
	{
		StringBuffer buf=new StringBuffer("");
		try{
			String codesetid=this.userview.getManagePrivCode();
			String value=this.userview.getManagePrivCodeValue();
			buf.append(" and "+Sql_switcher.year("a00z2")+"="+(String)payDateBean.get("year"));
			buf.append(" and "+Sql_switcher.month("a00z2")+"="+(Integer.parseInt((String)payDateBean.get("month"))));
			buf.append(" and "+Sql_switcher.day("a00z2")+"="+(String)payDateBean.get("day"));
			buf.append(" and a00z3="+(String)payDateBean.get("count"));
			buf.append(" and sp_flag='06' ");
		}catch(Exception e){
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	
	/**
	 * 权限限制sql语句
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @param tablename
	 * @return
	 */
	public String getPrivSQL(String role,String tablename,String salaryid,String b_units)
	{
		StringBuffer buf = new StringBuffer("");
		String[] temp = salaryid.split(",");
		if("1".equals(role))//如果是树节点传进来的，那么此处role可传空  role=1 代表超级用户 if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
		{
			buf.append( "  1=1 ");
		}else
		{			
			
	     	HashMap map = new HashMap();
			for (int j= 0; j < temp.length; j++){
				String b0110_item="b0110";
				String e0122_item="e0122";
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(deptid!=null&&deptid.trim().length()>0)//设置了归属部门
				{ 
					e0122_item=deptid;
					if(orgid!=null&&orgid.length()>0)
						b0110_item=orgid;
				}else if(orgid!=null&&orgid.trim().length()>0)//没设置归属部门，只设置了归属单位，走归属单位
				{ 
					b0110_item=orgid;
				}
				String item = (String) map.get(e0122_item+"/"+b0110_item);
		    	if(item!=null&&item.length()>0){
		    		map.put(e0122_item+"/"+b0110_item, item+",'"+temp[j]+"'");
		    	}else{
		    		map.put(e0122_item+"/"+b0110_item, "'"+temp[j]+"'");
		    	}	

			}			
			if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
			{
				String[] unitarr =b_units.split("`");
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					String[] str = key.toString().split("/");
					buf.append("((");
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);							  
							if(privCode!=null&&!"".equals(privCode))
							{		
								buf.append(" ( case");
								if(!"e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//归属单位和部门均设置了
									buf.append("  when  nullif("+tablename+str[0]+",'') is not null  then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if(!"e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//设置了归属部门，没设置归属单位
									buf.append("  when nullif("+tablename+str[0]+",'') is not null then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//没设置归属部门，设置了归属单位
									buf.append("  when nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//啥都没设置
									buf.append("  when nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}
							}
	    				}
					}
					String _str = buf.toString();
					buf.setLength(0);
					buf.append(_str.substring(0, _str.length()-3));
					buf.append(")) or");
				}
				String str = buf.toString();
				buf.setLength(0);
				buf.append("("+str.substring(0, str.length()-3)+")");
			}else if("UN`".equalsIgnoreCase(b_units)){
				buf.append( "  1=1 ");
			}
			else
			{
				buf.append( "  1=2 ");
			}
		}
		return buf.toString();
	}
	/**
	 * 获取lprogram参数
	 * zhaoxg add 2016-1-13
	 * @param attriName
	 * @param nodeValue
	 * @return
	 */
	public String getLprogramAttri(String attriName,int nodeValue)
	{
		String value="";
		try{
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.salaryTemplateBo.getTemplatevo().getString("lprogram"));
			value=lpbo.getValue(nodeValue,attriName);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 根据业务用户名取得关联的自助用户名
	 * 
	 * @param name
	 * @return
	 * @throws GeneralException 
	 */
	public String getZizhuUsername(String pre,String name) throws GeneralException {
		StringBuffer str = new StringBuffer();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			/** 查询所有的自助用户指标 start**/
			AttestationUtils utils = new AttestationUtils();
			LazyDynaBean fieldbean = utils.getUserNamePassField();
			String username_field = (String)fieldbean.get("name");
			/** 查询所有的自助用户指标 end**/
			
			String sql = "select " + username_field + " username from "+ pre +"A01 where A0100 = (select A0100 from OperUser where UserName = '"
					+ name + "')";
			rs = dao.search(sql);
			if (rs.next()) {
				str.append(rs.getString("UserName"));
			}
			if (str.length() == 0) {
				str.append(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			try {
				if(rs!=null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str.toString();
	}
	public SalaryTableStructBo getSalaryTableStructBo() {
		return salaryTableStructBo;
	}
	 
	public SalaryTemplateBo getSalaryTemplateBo() {
		return salaryTemplateBo;
	}
	
	/**
	 * 在报批和提交的时候验证，
	 * 如果计算公式中需要计算税率，但是在报批和提交时候，税率表没有对应的计算，说明可能新增人员等，没有计算
	 * 这时候提示出来
	 * @param bosdate
	 * @param count
	 * @return
	 */
	public boolean isComputeTax(String bosdate,String count) {
		boolean result = true;
		RowSet rs = null;
		StringBuffer buf_ = new StringBuffer();//
		ArrayList list_fielditem = new ArrayList();//可能有多个，计算税率表的顺序指标
		try {
			//找到计税单位指标
			//这里暂时不要了
			/*String tax_mode_item=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_MODE);
			//如果发放日期，传入的只有年月，加上日
			bosdate = bosdate.replaceAll("\\.","-");
			if(bosdate.length() == 7) {
				bosdate = bosdate + "-01";
			}
			ContentDAO dao = new ContentDAO(conn);
			//找到计算公式
			ArrayList formulalist=this.salaryTemplateBo.getFormulaList(0, String.valueOf(salaryid), null);
			if(formulalist.size()==0)
				return true;
			
			boolean isHaveTaxFormula = false;
			for(int i = 0; i < formulalist.size(); i++) {
				DynaBean dbean=(LazyDynaBean)formulalist.get(i);
                String nrunflag= (String)dbean.get("runflag");
                //如果计算公式中有计算税率的才进行判断
                if("2".equalsIgnoreCase(nrunflag)) {
                	String itemid=(String)dbean.get("itemname");
                	String _taxid=(String)dbean.get("standid");
                	
                	list_fielditem.add(itemid);
                	String tax_mode = getTaxMode(_taxid);
                	isHaveTaxFormula = true;
                	//如果设置计算两个个人所得税的时候
                	if(buf_.length() > 0) {
                		buf_.append(" or ");
                	}
                	//必须计税方式对应选择的税率表计税方式
                	buf_.append(getCond(tax_mode, tax_mode_item));
                }
			}
			
			String power = "";
			String tableName = this.salaryTemplateBo.getGz_tablename();
			//权限
			if(this.salaryTemplateBo.getManager()!=null && this.salaryTemplateBo.getManager().length()>0 && 
					!this.userview.getUserName().equalsIgnoreCase(this.salaryTemplateBo.getManager())) {//共享非管理员
				power += this.salaryTemplateBo.getWhlByUnits(tableName, true);
				ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.conn,String.valueOf(salaryid),this.userview);
				
				RecordVo templateVo=this.salaryTemplateBo.getTemplatevo();
				String gz_module = templateVo.getString("cstate");
				power += aorgbo.getSalarySql(bosdate, gz_module);
			}
			
			if(isHaveTaxFormula) {
				StringBuffer sql = new StringBuffer("select count(1) count_ from " + tableName);
				sql.append(" where not exists (select 1 from gz_tax_mx gtm where " + Sql_switcher.dateToChar("gtm.A00Z2") + " = ? ");
				sql.append(" and gtm.A00Z3 = ? and salaryid = ?");
				sql.append(" and " + tableName + ".NBASE = gtm.NBASE and " + tableName + ".A0100 = gtm.A0100 ");
				sql.append(" and " + tableName + ".A00Z0 = gtm.A00Z0 and " + tableName + ".A00Z1 = gtm.A00Z1 and " + tableName + "." + tax_mode_item + " = gtm.taxmode)");
				sql.append(" and (" + buf_ + ") and (A01Z0 is null or A01Z0 <> 0) " + power);
				
				rs = dao.search(sql.toString(), Arrays.asList(new String[] {bosdate, count, String.valueOf(this.salaryid)}));
				if(rs.next()) {
					if(rs.getInt("count_") > 0) {
						result = false;
					}
				}
			}
			
			//如果人员都是一一对应的，还需要判断数据是否一致
			if(result && isHaveTaxFormula) {
				result = compareData(tableName, list_fielditem, bosdate, count, buf_.toString(), power, tax_mode_item);
			}*/
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return result;
	}
	
	private String getTaxMode(String taxid){
		RowSet rs = null;
		String tax_mode = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select param from gz_tax_rate where taxid=?", Arrays.asList(new String[] {taxid}));
			if(rs.next())
			{
				String tmp=Sql_switcher.readMemo(rs, "param");
				TaxTableXMLBo xmlbo=new TaxTableXMLBo(tmp);
				tax_mode=xmlbo.getValue("TaxModeCode");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return tax_mode;
	}
	
	/**
	 * 获取计税方式sql，和CalcTaxBo有重叠部分，为了方便客户换文件，暂时就这样，以后可以封装这个方法到CalcTaxBo类
	 * @param tax_mode
	 * @param tax_mode_item
	 * @return
	 */
	private String getCond(String tax_mode, String tax_mode_item) {
		StringBuffer cond = new StringBuffer();
		if("2".equalsIgnoreCase(tax_mode)|| "3".equalsIgnoreCase(tax_mode)|| "4".equalsIgnoreCase(tax_mode)|| "5".equalsIgnoreCase(tax_mode))
		{
			if(SystemConfig.getPropertyValue("compute_nulltaxmode")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("compute_nulltaxmode")))
			{
				cond.append(Sql_switcher.isnull("nullif("+tax_mode_item+",'')","'x'")); 
				cond.append("='");
				cond.append(tax_mode);
				cond.append("'");
			}
			else
			{
				cond.append(Sql_switcher.isnull("nullif("+tax_mode_item+",'')","'5'")); 
				cond.append("='");
				cond.append(tax_mode);
				cond.append("'");
			}
		}
		else
		{
			if(SystemConfig.getPropertyValue("compute_nulltaxmode")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("compute_nulltaxmode")))
			{
				cond.append(" ((not (");
				cond.append(tax_mode_item);
				cond.append(" in ('2','4','3'))) and ");
				cond.append(tax_mode_item);
				cond.append(" is not null ");
				if(Sql_switcher.searchDbServer()==1)
				{
					cond.append(" and "+tax_mode_item);
					cond.append("<>''");
				}
				cond.append("  )");
			}
			else
			{
				cond.append(" ((not (");
				cond.append(tax_mode_item);
				cond.append(" in ('2','4','3'))) or ");
				cond.append(tax_mode_item);
				cond.append(" is null)");
			}
		}
		return cond.toString();
	}
	
	/**
	 * 薪资表中的数据和税率表数据比较，需要考虑可能一个账套多个指标都计算税率表的情况
	 * @param fielditems
	 * @return
	 */
	private boolean compareData(String tablename, ArrayList list_fielditem, String A00Z2, String A00Z3, String buf_, String power, String tax_mode_item) {
		boolean result = true;
		StringBuffer suf = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		String row_num = "rownum";
		try {
			ContentDAO dao = new ContentDAO(conn);
			//比较两个表数据是否一致，通过分组找到排序号，这样如果有多个计算顺序也是一致
			for(int i = 1; i <= list_fielditem.size(); i++) {
				String itemid = (String)list_fielditem.get(i-1);
				FieldItem item=DataDictionary.getFieldItem(itemid);
				suf.append(" union all select 1 from gz_tax_mx gtm where " + Sql_switcher.dateToChar("gtm.A00Z2") + "=? and gtm.A00Z3=? and gtm.salaryid=? and " 
					+ Sql_switcher.charToFloat(Sql_switcher.isnull(tablename+"."+itemid, "0")) + " = " + Sql_switcher.charToFloat(Sql_switcher.isnull("gtm.sds", "0"))
					+ " and "+tablename+".nbase = gtm.nbase and "+tablename+".A0100 = gtm.A0100 and "+tablename+".A00Z0 = gtm.A00Z0 "
					+ " and "+tablename+".A00Z1 = gtm.A00Z1 and "+tablename+ "." + tax_mode_item + "=gtm.taxmode");
				list.add(A00Z2);
				list.add(A00Z3);
				list.add(salaryid);
			}
			rs = dao.search("select count(1) count_ from " + tablename + " where (not exists (" + suf.substring(10) + ")) and (" + buf_ + ")" + power, list);
			if(rs.next()) {
				if(rs.getInt("count_") > 0) {
					result = false;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
}
