
package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.CalcTaxBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:查询个税明细数据</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-7-19:下午06:00:27</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchTaxMxTrans extends IBusiness {
	/**
	 * 取得报税时间过滤条件
	 * @param declaredate
	 * @return
	 */
	private String getFilterCond(String declaredate)
	{
		StringBuffer buf=new StringBuffer();
		if(declaredate==null|| "".equalsIgnoreCase(declaredate)|| "all".equalsIgnoreCase(declaredate))
			return "";
		String[] datearr=StringUtils.split(declaredate, ".");
		String theyear=datearr[0];
		String themonth=datearr[1];
		buf.append(Sql_switcher.year("Declare_tax"));
		buf.append("=");
		buf.append(theyear);
		buf.append(" and ");
		buf.append(Sql_switcher.month("Declare_tax"));
		buf.append("=");
		buf.append(themonth);		
		return buf.toString();
	}
	public void execute() throws GeneralException {
		try
		{
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn(),this.getUserView());
			HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");	
			
			String fromTable=(String)this.getFormHM().get("fromTable"); //gz_tax_mx  | taxarchive
			String returnvalue=(String)reqhm.get("returnvalue"); 
			this.getFormHM().put("returnvalue",returnvalue);
			
			String filterByMdule=(String)this.getFormHM().get("filterByMdule");
			String init = (String)reqhm.get("init");
			reqhm.remove("init");
			String initflag = (String)reqhm.get("initflag");
			reqhm.remove("initflag");
			String is_back = (String)this.getFormHM().get("is_back");
			if(is_back!=null && is_back.length()>0)
			{
				if("back".equalsIgnoreCase(is_back))
				{
					String salaryid = (String)this.getFormHM().get("salaryid");
					taxbo.syncTaxData(salaryid);
					this.getFormHM().put("salaryid",salaryid);
				}
				this.getFormHM().put("is_back",is_back);
			}
			/**如果系统中不存在个税明细表，则自动创建*/
			CalcTaxBo calctaxbo=new CalcTaxBo(this.getFrameconn(),this.userView);
			calctaxbo.createTaxDetails();			
			HistoryDataBo bo = new HistoryDataBo(this.getFrameconn());
			bo.syncSalaryTaxArchiveStrut();
			
			/**取得个税明细表结构字段列表*/
			String a_code=(String)this.getFormHM().get("a_code");
			String declaredate=(String)this.getFormHM().get("declaredate");
			if("1".equals(initflag)){
				declaredate="";
			}
			ArrayList datalist=taxbo.searchDeclareDateList(1,fromTable);
			if((declaredate==null|| "".equals(declaredate))&&datalist.size()>0){
				declaredate=((CommonData)datalist.get(0)).getDataValue();
			}
			if(!(declaredate==null|| "".equalsIgnoreCase(declaredate)|| "all".equalsIgnoreCase(declaredate)))
			{
				boolean isdate=false;
				for(int i=0;i<datalist.size();i++)
				{
					CommonData data=(CommonData)datalist.get(i);
					if(data.getDataValue().equalsIgnoreCase(declaredate))
					{
						isdate=true;
						break;
					}
				}
				if(!isdate)
				{
					if(datalist.size()>0) //2013-11-14  邓灿  
						declaredate=((CommonData)datalist.get(0)).getDataValue();
					else
						declaredate="all"; 
				}
			}
			
			
			String strwhere=getFilterCond(declaredate);
			ArrayList fieldlist=taxbo.getFieldlist();
			this.getFormHM().put("fieldlist", fieldlist);		
			/**数据过滤*/
			StringBuffer buf=new StringBuffer();
			String privPre=taxbo.getPrivPre(filterByMdule);
			buf.append("select "+fromTable+".* from "+fromTable+"  left join dbname on upper("+fromTable+".nbase)=upper(dbname.pre)  where ("+privPre+") ");
			/**FengXiBin Add 2007-11-20*/
			/**为解决当插入一条新记录时,b0110和e0122为NULL,该记录无法显示在前台页面上*/ 
			if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
			{
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if("UN".equalsIgnoreCase(codesetid))
				{
					buf.append(" and ("+fromTable+".b0110 like '");
					buf.append(value==null?"":value);
					buf.append("%'");
					if(value==null|| "".equals(value))
						buf.append(" or "+fromTable+".b0110 is null ");
					buf.append(")");
				}
				if("UM".equalsIgnoreCase(codesetid))
				{
					buf.append(" and ("+fromTable+".e0122 like '");
					buf.append(value==null?"":value);
					buf.append("%'");
					if(value==null|| "".equals(value))
						buf.append(" or "+fromTable+".e0122 is null ");
					buf.append(")");
				}

				
				
			}	
			/*if(!this.userView.isSuper_admin() isAdmin()&&!this.userView.getGroupId().equals("1"))
			{
				String code=this.userView.getManagePrivCode();
	        	 String value=this.userView.getManagePrivCodeValue();
	        	 if(code==null)
	        	 {
	        		 buf.append(" and 1=2 ");
	        	 }
	        	 else if(code.equalsIgnoreCase("UN"))
	        	 {
	        		 buf.append(" and (gz_tax_mx.b0110 like '");
	        		 buf.append((value==null?"":value)+"%'");
	        		 if(value==null)
	        		 {
	        			 buf.append(" or gz_tax_mx.b0110 is null ");
	        		 }
	        		 buf.append(")");
	        	 }
	        	 else if(code.equalsIgnoreCase("UM"))
	        	 {
	        		 buf.append(" and (gz_tax_mx.e0122 like '");
	        		 buf.append((value==null?"":value)+"%'");
	        		 if(value==null)
	        		 {
	        			 buf.append(" or gz_tax_mx.e0122 is null ");
	        		 }
	        		 buf.append(")");
	        	 }
			}*/
			if(strwhere.length()>0)
			{
				if(buf.toString().indexOf("where")==-1)
				{
					buf.append(" where ");
					buf.append(strwhere);
				}else{
					buf.append(" and ");
					buf.append(strwhere);
				}
			}
			if(init.endsWith("search"))
			{
				String condtionsql=(String)this.getFormHM().get("condtionsql");
				condtionsql = PubFunc.keyWord_reback(condtionsql);
				/* 安全问题 sql-in-url 所得税管理 xiaoyun 2014-9-12 start */
				condtionsql = PubFunc.decrypt(SafeCode.decode(condtionsql));
				/* 安全问题 sql-in-url 所得税管理 xiaoyun 2014-9-12 end */
				if(!(condtionsql==null || "".equals(condtionsql)))
				{
					if(buf.toString().indexOf("where")==-1)
					{
						buf.append(" where ("+condtionsql+")");
					}else{
						buf.append(" and ("+condtionsql+")");
					}
					this.getFormHM().put("condtionsql",SafeCode.encode(PubFunc.encrypt(condtionsql)));
				}
			}else{
				this.getFormHM().put("condtionsql","");
			}
			
			// gby，2015-01-20，根据姓名和唯一指标进行查询
			String querywhereValue = (String)this.getFormHM().get("queryValue");
			String signQueryValue = (String)reqhm.get("signQueryValue");
			if(!"1".equalsIgnoreCase(signQueryValue)){
				this.getFormHM().put("queryValue", "");
				querywhereValue = "";
			}
			reqhm.remove("signQueryValue");
			buf.append(this.queryWhereByName(querywhereValue,fromTable));
			
			
			/**人员排序*/
			buf.append(" order by dbname.dbid,a0000,a00z0,a00z1,b0110,e0122");			
			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("sql",buf.toString());
			this.getFormHM().put("declaredate",declaredate);
			this.getFormHM().put("tablename",fromTable);
			this.getFormHM().put("fromTable",fromTable);
			this.getFormHM().put("datelist",datalist);
			this.getFormHM().put("fromTableList", getFromTableList());
//			returnFlag 0：返回薪资发放的类别界面 1：返回部门月奖金界面
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String returnFlag=(String)requestPamaHM.get("returnFlag");
			requestPamaHM.remove("returnFlag");
			returnFlag=returnFlag==null?"0":returnFlag;
			this.getFormHM().put("returnFlag",returnFlag);		
			this.getFormHM().put("filterByMdule", filterByMdule);
			if("1".equals(returnFlag))
			{
			    String year  = (String)requestPamaHM.get("theyear");
			    String month  = (String)requestPamaHM.get("themonth");
			    String operOrg = (String)requestPamaHM.get("operOrg");
			    requestPamaHM.remove("theyear");
			    requestPamaHM.remove("themonth");
			    requestPamaHM.remove("operOrg");
			    this.getFormHM().put("theyear",year);	
			    this.getFormHM().put("themonth",month);
			    this.getFormHM().put("operOrg",operOrg);	
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

	
	private ArrayList getFromTableList()
	{
		ArrayList list=new ArrayList();
		CommonData data=new CommonData("gz_tax_mx","个税明细表");	
		list.add(data);
		data=new CommonData("taxarchive","个税归档表");	
		list.add(data);
		return list;
	}
	
	/**
	 * 根据姓名和唯一指标进行查询
	 * 
	 * @param value 输入的查询值
	 * @return
	 * gby
	 */
	private String queryWhereByName(String value, String fromTable) {
		StringBuffer sqlWhere = new StringBuffer("");
		sqlWhere.reverse();
		try {
			ConstantXml csXML = new ConstantXml(this.frameconn, "SYS_OTH_PARAM");
			org.jdom.Element e = csXML
					.getElement("/param/chk_uniqueness/field[@type='0']");
			String valid = e.getAttributeValue("valid");
			String name = e.getAttributeValue("name");
			if (!(value == null || "".equalsIgnoreCase(value))) {
				value = SafeCode.decode(value);

				value= PubFunc.hireKeyWord_filter(value);
				
				String whereStr = "";
				// 判断字段是否存在
				DbWizard db = new DbWizard(this.frameconn);
				if ("1".equals(valid) && db.isExistField(fromTable, name))
					whereStr = " or " + name + " like '%" + value + "%'";

				sqlWhere.append(" and (A0101 like '%" + value + "%' " + whereStr + ") ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlWhere.toString();
	}
}


