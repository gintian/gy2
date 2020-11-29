package com.hjsj.hrms.module.gz.tax.transaction;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.CalcTaxBo;
import com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称 ：ehr7.x
 * @author wangjl
 *  所得税管理界面初始化
 *	2016-4-28
 */
public class SearchTaxMxTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			//报税日期
			String date = (String) this.getFormHM().get("datetime");
			boolean firstComing = this.getFormHM().get("firstComing") == null?false:(Boolean) this.getFormHM().get("firstComing");
			
			String table = (String) this.getFormHM().get("tablename");
			String taxMode = String.valueOf(this.getFormHM().get("taxMode"));//计税方式选择，0：全部，1：工资薪金.....codesetid为46的
			if("null".equalsIgnoreCase(taxMode))
				taxMode="";
	    	MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
	    	if(bean!=null){
		    	salaryid = (String) bean.get("salaryid");
		    	salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		    	date = (String) bean.get("datetime");
				table=(String) bean.get("tablename");
				taxMode = String.valueOf(bean.get("taxMode"));
	    	}

			TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView,salaryid);

			if(StringUtils.isEmpty(PubFunc.decrypt(table))){
				taxbo.setTablename("gz_tax_mx");
			}else{
				taxbo.setTablename(PubFunc.decrypt(table));
			}
			if(!PubFunc.encrypt("all").equals(date)&&!"JEPWw5tnIio@3HJD@".equals(date)){
				if(StringUtils.isEmpty(date)){
		    		date = taxbo.searchMaxDeclareDate();
		    		if(StringUtils.isBlank(date)){
		    			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
		    			Date  newDate = new Date();
						date = format.format(newDate);
					}
				}else{
					date = date.replace("-", ".");
					if(date.length()>=7){
						date = date.substring(0, 7);
					}
				}
				Pattern pattern = Pattern.compile("[0-9]*\\.[0-9]*");
	            //时间只能是数字，防止sql注入和跨站脚本
	            Matcher isNum = pattern.matcher(date);
	            if(!isNum.matches()){
	            	throw GeneralExceptionHandler.Handle(new Exception("日期格式异常！"));
	            }
	    	}
			if(StringUtils.isNotEmpty(salaryid))
			this.syncTaxData(salaryid);

			//快速查询条件
			ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
			CalcTaxBo calctaxbo=new CalcTaxBo(this.getFrameconn(),this.userView);
			calctaxbo.createTaxDetails();
			/**同步个税归档表  */
			taxbo.syncSalaryTaxArchiveStrut();

			String getConditon = "";
			TableDataConfigCache cache = (TableDataConfigCache)userView.getHm().get("searchtax_id001");
			//快速查询
			if(valuesList!=null&&valuesList.size()>0){
				//招聘过滤特殊字符
				ArrayList queryList = new ArrayList();
				for (String value : valuesList) {
					queryList.add(PubFunc.hireKeyWord_filter(value));
				}
				getConditon = taxbo.getCondition(queryList);
				cache.setQuerySql(getConditon.toString());
				return;
			// 只有第一次或重新进入，或者取消了快速查询的值
			}else if(cache != null && (firstComing || (valuesList != null && valuesList.size()==0))){
				cache.setQuerySql("");
			}
			TableConfigBuilder builder = taxbo.getTableConfig(salaryid,date,PubFunc.decrypt(table),taxMode);
			String config = builder.createExtTableConfig();
			
			if(cache != null) {
				TableDataConfigCache cache_rebuild = (TableDataConfigCache)userView.getHm().get("searchtax_id001");
				cache_rebuild.setQuerySql(cache.getQuerySql());
			}
			
			ArrayList list = AdminCode.getCodeItemList("46");
			HashMap<String, String> map_name = new HashMap<String, String>();
			for(int i = 0; i < list.size(); i++) {
				CodeItem codeItem = (CodeItem)list.get(i);

				if(codeItem.getInvalid()==1) {
					map_name.put(codeItem.getCcodeitem(), codeItem.getCodename());
				}
			}
			
			this.getFormHM().put("map_name", map_name);
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("archivesPriv", this.userView.hasTheFunction("3240409")?"1":"0");
			this.getFormHM().put("publicPlanPriv", this.userView.hasTheFunction("3240410")?"1":"0");
			//this.getFormHM().put("neardataTime", );
			this.getFormHM().put("items", taxbo.getItems());
			this.getFormHM().put("taxMode", taxMode);
			this.getFormHM().put("isComputeDep", "true".equalsIgnoreCase(taxbo.getDeptID())?"1":"0");
			//this.getFormHM().put("items", taxbo.getItems());
			//this.getFormHM().put("reportMenu",taxbo.getReportMenuJson());
			//表名加密
			this.getFormHM().put("tablename", PubFunc.encrypt(taxbo.getTablename()));
			this.getFormHM().put("datetime", "all".equals(taxbo.getDatetime())?"JEPWw5tnIio@3HJD@":taxbo.getDatetime());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 
	 * @Title: syncTaxData   
	 * @Description:从工资发放进入税率表时，将税率表中存在但是在工资表中不存在的数据删掉
	 * @param @param salaryid 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	private void syncTaxData(String salaryid)
	{
		try
		{
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String gz_tablename="";
			String userflag="";
			if(manager.length()==0)
			{
				gz_tablename=this.userView.getUserName()+"_salary_"+salaryid;
				userflag=this.userView.getUserName();
			}
			else
			{
				gz_tablename=manager+"_salary_"+salaryid;
				userflag=manager;
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			String a00z2="";
			int a00z3=0;
			StringBuffer tempsql=new StringBuffer("");
			tempsql.append("select a00z2,a00z3 from "+gz_tablename);
			RowSet rs = dao.search(tempsql.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next())
			{
				Date d = rs.getDate("a00z2");
				a00z2=format.format(d);
				a00z3=rs.getInt("a00z3");
				break;
			}
			if("".equals(a00z2)||a00z3==0)
				return;
			StringBuffer del_sql = new StringBuffer("");
			del_sql.append(" delete from gz_tax_mx where not exists (select null from ");
			del_sql.append(gz_tablename+" where gz_tax_mx.a00z1="+gz_tablename+".a00z1 and gz_tax_mx.a00z0="+gz_tablename+".a00z0 ");
			del_sql.append(" and gz_tax_mx.a0100="+gz_tablename+".a0100 ");
			del_sql.append(" and UPPER(gz_tax_mx.nbase)=UPPER("+gz_tablename+".nbase)) ");
			del_sql.append(" and salaryid="+salaryid);
			del_sql.append(" and UPPER(userflag)='"+userflag.toUpperCase()+"'");
			del_sql.append(" and a00z3="+a00z3);
			del_sql.append(" and "+Sql_switcher.dateToChar("a00z2", "yyyy-MM-dd")+"='"+a00z2+"'");
			dao.delete(del_sql.toString(), new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
