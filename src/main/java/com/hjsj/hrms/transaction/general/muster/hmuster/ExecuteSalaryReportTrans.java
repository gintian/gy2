package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterViewBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.StipendHmusterBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFormulaXMLBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:生成用户自定义工资报表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 1, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ExecuteSalaryReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{

			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			int ver=this.userView.getVersion(); //锁版本校验 
 			String salaryid=(String)hm.get("salaryid");
			hm.remove("salaryid");
			if(salaryid==null)
				salaryid=(String)this.getFormHM().get("salaryid");
	        // 检查薪资/保险类别的资源权限
            checkSalaryPriv(salaryid);
            
			String tabid=(String)hm.get("tabid");
			if(tabid==null)
				tabid=(String)this.getFormHM().get("tabid");
			hm.remove("tabid");
			String closeWindow=(String)this.getFormHM().get("closeWindow");
			this.getFormHM().put("closeWindow", closeWindow);
			String reset=(String)hm.get("reset");//是否重填
			reset=reset!=null&&reset.trim().length()>0?reset:"1";
			hm.remove("reset");
			if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, tabid))
				throw GeneralExceptionHandler.Handle(new Exception("没有操作该花名册的权限！"));
			//52729 根据archive(分析历史数据)区分是薪资发放进入要求所见所得 还是薪资分析进入关联用户权限查看高级花名册数据
		    String archive="1";
            boolean isSalary_analyse=true;
            if(hm.get("archive")!=null)
            {
            	archive=(String)hm.get("archive");
            	hm.remove("archive");
            }else if(this.getFormHM().get("archive")!=null) {
            	archive=(String)this.getFormHM().get("archive");
            }else {
            	isSalary_analyse=false;
            }
			
			String a_code=(String)hm.get("a_code");
			a_code=a_code!=null?a_code:"";
			//String temp_code=(String)hm.get("a_code");//liuy 2015-11-16 12054：薪资发放的自定义报表在打印预演后，关闭打印预演页面后，高级花名册页面会刷新页面，所有数据都没了，不对
			hm.remove("a_code");
			if(!this.userView.isSuper_admin()){
				if(a_code.trim().length()<1){
					if(isSalary_analyse) {
						a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
					}else {
						a_code="UN";
					}
				}else{
					if(a_code.indexOf("UN")==-1&&a_code.indexOf("UM")==-1&&a_code.indexOf("@K")==-1){
						CodeItem codeitem = AdminCode.getCode("UN", a_code);
						if(codeitem==null){
							codeitem = AdminCode.getCode("UM", a_code);
							if(codeitem==null){
								codeitem = AdminCode.getCode("@K", a_code);
								if(codeitem!=null){
									a_code = "@K"+a_code;
								}
							}else{
								a_code = "UM"+a_code;
							}
						}else{
							a_code = "UN"+a_code;
						}
					}
				}
			}else{
				if(a_code.trim().length()<1){
					a_code="UN";
				}else{
					if(a_code.indexOf("UN")==-1&&a_code.indexOf("UM")==-1&&a_code.indexOf("@K")==-1){
						CodeItem codeitem = AdminCode.getCode("UN", a_code);
						if(codeitem==null){
							codeitem = AdminCode.getCode("UM", a_code);
							if(codeitem==null){
								codeitem = AdminCode.getCode("@K", a_code);
								if(codeitem!=null){
									a_code = "@K"+a_code;
								}
							}else{
								a_code = "UM"+a_code;
							}
						}else{
							a_code = "UN"+a_code;
						}
					}
				}
			}
			
			String checksalary=(String)hm.get("checksalary");
			checksalary=checksalary!=null&&checksalary.trim().length()>0?checksalary:"salary";
			hm.remove("checksalary");
          
			String currpage="1";
			String opt=(String)hm.get("opt");
			if(opt==null){
				currpage=(String)this.getFormHM().get("currpage");
				currpage=currpage!=null&&currpage.trim().length()>0?currpage:"1";
			}
			hm.remove("opt");
			 
			String zeroPrint=(String)hm.get("zeroPrint");
			zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"";
			if(zeroPrint.trim().length()<1){
				zeroPrint=(String)this.getFormHM().get("zeroPrint");
				zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"0";
			}
			hm.remove("zeroPrint");
			
			String printGrid=(String)hm.get("printGrid");
			printGrid=printGrid!=null&&printGrid.trim().length()>0?printGrid:"";
			if(printGrid.trim().length()<1){
				printGrid=(String)this.getFormHM().get("printGrid");
				printGrid=printGrid!=null&&printGrid.trim().length()>0?printGrid:"1";
			}
			hm.remove("printGrid");

			HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabid);
			String column=hmxml.getValue(HmusterXML.COLUMN);
			String hz=hmxml.getValue(HmusterXML.HZ);
			if("1".equals(column))
				column = hz;
			this.getFormHM().put("column",column);
			String pix=hmxml.getValue(HmusterXML.PIX);
			this.getFormHM().put("pix",pix);
			
			if("TRUE".equalsIgnoreCase(hmxml.getValue(HmusterXML.SHOWZERO))){
				zeroPrint="1";
			}
			
			String dataarea = hmxml.getValue(HmusterXML.DATAAREA);
			dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
			if("1".equals(dataarea)){
				column="1";
				pix="-1";
			}
			this.getFormHM().put("dataarea",dataarea);
			String columnLine=hmxml.getValue(HmusterXML.COLUMNLINE);
			this.getFormHM().put("columnLine",columnLine);
			String groupPoint=hmxml.getValue(HmusterXML.GROUPFIELD);
			this.getFormHM().put("groupPoint",groupPoint);
			String layerid=hmxml.getValue(HmusterXML.GROUPLAYER);
			this.getFormHM().put("layerid",layerid);
			String sortitem=hmxml.getValue(HmusterXML.SORTSTR);
			sortitem=sortitem!=null?sortitem:"";
			this.getFormHM().put("sortitem",sortitem);
			String groupPoint2=hmxml.getValue(HmusterXML.GROUPFIELD2);
			this.getFormHM().put("groupPoint2", groupPoint2);
			String layerid2=hmxml.getValue(HmusterXML.GROUPLAYER2);
			String isGroupPoint2="0";
			if(groupPoint2!=null&&groupPoint2.length()>0)
				isGroupPoint2="1";
			this.getFormHM().put("isGroupPoint2",isGroupPoint2);
			this.getFormHM().put("layerid2",layerid2);
			String isGroupedSerials="0";//按组显示序号
			String groupedSerials = hmxml.getValue(HmusterXML.GROUPEDSERIALS);
			if("1".equals(groupedSerials))
				isGroupedSerials="1";
			String pageRows=(String)hm.get("pageRows");
			pageRows=pageRows!=null&&pageRows.trim().length()>0?pageRows:"";
			if("init".equals(pageRows)){
				// 默认值
				if("1".equals(hmxml.getValue(HmusterXML.ROWCOUNTMODE))&&hmxml.hasParam(HmusterXML.ROWCOUNT))// 手工指定
					pageRows=hmxml.getValue(HmusterXML.ROWCOUNT);
				else 
					pageRows="20";
			}
			if(pageRows.trim().length()<1){
				pageRows=(String)this.getFormHM().get("pageRows");
				pageRows=pageRows!=null&&pageRows.trim().length()>0?pageRows:"20";
			}
			hm.remove("pageRows");
			
			String isAutoCount="0";
			if(!"0".equals(pageRows))
				isAutoCount="1";

			HmusterBo hmusterBo=new HmusterBo(this.getFrameconn(),this.getUserView());
			HashMap cFactorMap=hmusterBo.getCfactor(tabid);
			HmusterViewBo hmusterViewBo=new HmusterViewBo(this.getFrameconn(),tabid);
			hmusterViewBo.setGroupPoint(groupPoint);
			hmusterViewBo.setDataarea(dataarea);
			hmusterViewBo.setIsGroupedSerials(isGroupedSerials);
			hmusterViewBo.setIsGroupPoint2(isGroupPoint2);
			hmusterViewBo.setGroupPoint2(groupPoint2);
			///if(dataarea.equals("1"))
			//{
				hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabid));
				if(hmusterViewBo.getTextFormatMap().size()>0)
				{
					if("1".equals(column)&& "1".equals(dataarea))
		        		hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabid));
				}
			//}
			if(cFactorMap.get("groupN")!=null){
				if(cFactorMap.get("multipleGroupN")!=null)
					hmusterViewBo.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));
			}
			String isGroupPoint="0";
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				isGroupPoint="1";
			}
			this.getFormHM().put("isGroupPoint",isGroupPoint);
			String emptyRow = hmxml.getNgrid();
			this.getFormHM().put("emptyRow",emptyRow);
			
			String username = this.userView.getUserFullName();
			ArrayList list=new ArrayList(); 
			String year="";
			String month="";
			String num="";
			String historyFlag="0";
			String dateStart="";
			String dateEnd="";
			if(!"1".equals(currpage)&& "salary".equalsIgnoreCase(checksalary)){//29920	【中船七一三研究所】薪资自定义高级花名册，插入审批意见，只有第一页显示 GZReportForm 添加对应参数
				String salaryDataTable=(String)this.getFormHM().get("salaryDataTable");
			    String salaryDataTableCond=(String)this.getFormHM().get("salaryDataTableCond");
				if((salaryDataTable!=null&&salaryDataTable.length()>0)
				||(salaryDataTableCond!=null&&salaryDataTableCond.length()>0)){
					hmusterViewBo.setSalaryDataTable(salaryDataTable);
					hmusterViewBo.setSalaryDataTableCond(new StringBuffer(salaryDataTableCond));
				}
			}
			if("salary".equalsIgnoreCase(checksalary)&& "1".equals(reset)){
				//historyFlag="1";
				StipendHmusterBo stipendHmusterBo=new StipendHmusterBo(this.getFrameconn(),this.userView,salaryid);
				stipendHmusterBo.setSelfMusterName(tabid);
				stipendHmusterBo.setLayerid(layerid);
				stipendHmusterBo.setLayerid2(layerid2);
				String condid=(String)this.getFormHM().get("condid");
				condid=condid!=null&&condid.trim().length()>0?condid:"all";
				String gz_module=(String)hm.get("gz_module");//薪资发放取消显示左侧组织机构树 按照薪资发放数据展现
				condid=condid!=null&&condid.trim().length()>0?condid:"";
				condid= "new".equalsIgnoreCase(condid)?"":condid;
				String privSet = "";
				if(!this.userView.isSuper_admin())
				{
					if("1".equals(gz_module))
						privSet = ","+this.userView.getResourceString(IResourceConstant.INS_SET)+",";
					else
						privSet = ","+this.userView.getResourceString(IResourceConstant.GZ_SET)+",";
				}
				
				String filterWhl = (String) this.userView.getHm().get("gz_filterWhl");
				filterWhl=filterWhl!=null&&filterWhl.trim().length()>0?filterWhl:"";
				if(ver>=70){
					if(StringUtils.isNotBlank((String)this.userView.getHm().get("gzsp_filterWhl"))&&!"0".equals(this.getFormHM().get("model"))){
						filterWhl=(String)this.userView.getHm().get("gzsp_filterWhl");
					}else{
						filterWhl="";
					}
				}
				String salryorder=(String)this.getFormHM().get("order");
				salryorder=salryorder!=null&&salryorder.trim().length()>0?salryorder:"";
				String model=(String)this.getFormHM().get("model");//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
				model=model!=null&&model.trim().length()>0?model:"0";
				
				String bosdate=(String)hm.get("bosdate");
				if(StringUtils.isEmpty(bosdate)) {
					bosdate=(String)this.getFormHM().get("bosdate");
				}
				bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"2008.10.10";
				hm.remove("bosdate");
				if(bosdate.trim().length()<1){
					bosdate=(String)this.getFormHM().get("bosdate");
					bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"";
				}
				
				String boscount=(String)this.getFormHM().get("boscount");
				boscount=boscount!=null&&boscount.trim().length()>0?boscount:"";
				//处理新版薪资审批 传输加密的业务日期和处理次数
				if(!(StringUtils.isNumeric(boscount)&&StringUtils.isNumeric(bosdate.substring(0,1)))) {
					bosdate=PubFunc.decrypt(SafeCode.decode(bosdate));
					boscount=PubFunc.decrypt(SafeCode.decode(boscount));
				}
				
				stipendHmusterBo.setIsGroupPoint(isGroupPoint);
				stipendHmusterBo.setGroupPoint(groupPoint);
				stipendHmusterBo.setIsGroupPoint2(isGroupPoint2);
				stipendHmusterBo.setGroupPoint2(groupPoint2);

				a_code=a_code!=null&&a_code.trim().length()>1?a_code:"UN";
				SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
				String priv_mode = ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag"); // 人员范围权限过滤标志 1：有
				String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET,"user");
				if(priv_mode==null|| "".equals(priv_mode)){
					priv_mode = "0";
				}
				/*if(a_code.equals("UN")){
					if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
					{
						if(a_code.substring(2).length()==0&&priv_mode.equals("1"))
						{
							if(this.userView.getManagePrivCode().length()==0)
								a_code="UN-1";
							else if(this.userView.getManagePrivCode().equals("@K"))
								a_code=getUnByPosition(this.userView.getManagePrivCodeValue());
							else
								a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
						}
					}
				}*/
				stipendHmusterBo.setLayerid(layerid);
				stipendHmusterBo.setLayerid2(layerid2);
				if(manager!=null&&manager.length()>0){
					stipendHmusterBo.setManageUserName(manager);
				}
				if(currpage==null|| "0".equals(currpage)||currpage.trim().length()==0)
					currpage="1";
				if("1".equals(currpage)){
					stipendHmusterBo.getMidvariable(tabid);
					stipendHmusterBo.setSortitem(sortitem);
					stipendHmusterBo.setSalaryorder(salryorder);
					stipendHmusterBo.setModel(model);
					stipendHmusterBo.setCheckdata(bosdate);
					stipendHmusterBo.setChecknum(boscount);
					String querysqlStr="";
					if(ver>=70&&!"3".equals(model)){//70及以上版本使用表格控件保存查询方案  model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入
						/*if(this.getFormHM().get("subModuleId")!=null&&!"".equals(this.getFormHM().get("subModuleId"))&&!"1".equals(this.getFormHM().get("model"))){
							if(!"all".equalsIgnoreCase(condid))薪资发放 进入花名册 是否要带入查询方案过滤条件 现在不带入
							querysqlStr=tablecatch.getQuerySql()!=null?tablecatch.getQuerySql():"";
						} */
						
						String searchSql=getSearchSql(salaryid, condid);
						if(searchSql.length()>0) {
							if(searchSql.startsWith(" and"))
								querysqlStr= searchSql;
							else
								querysqlStr="and "+ searchSql;
						}
						
					}
					
					 stipendHmusterBo.setFilterWhl(filterWhl+querysqlStr);//查询控件拼接成的sql查询
						
					//stipendHmusterBo.importData(tabid,salaryid,temp_code,condid,11);
					if(ver<70)//70以下软锁走之前的查询条件
						stipendHmusterBo.importData(tabid,salaryid,a_code,condid,11);
					else//以上版本走表单查询  条件为查询全部 在FilterWhl中放入sql
						stipendHmusterBo.importData(tabid,salaryid,a_code,"all",11);	
					this.getFormHM().put("subModuleId", hm.get("subModuleId"));
					hmusterViewBo.setSalaryDataTable(stipendHmusterBo.getSalaryDataTable());
					hmusterViewBo.setSalaryDataTableCond(stipendHmusterBo.getSalaryDataTableCond());
					this.getFormHM().put("salaryDataTable", stipendHmusterBo.getSalaryDataTable());
					this.getFormHM().put("salaryDataTableCond", stipendHmusterBo.getSalaryDataTableCond().toString());	
					 
//					stipendHmusterBo.importData(tabid,salaryid,a_code,condid,11);
//					hmusterViewBo.setSalaryDataTable(stipendHmusterBo.getSalaryDataTable());
//					hmusterViewBo.setSalaryDataTableCond(sbf);//添加条件
//					hmusterViewBo.setSalaryDataTableCond(stipendHmusterBo.getSalaryDataTableCond());//添加条件
//					System.out.println(stipendHmusterBo.getSalaryDataTableCond());
				}
				this.getFormHM().put("condid",condid);
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				ArrayList manfilterlist=new ArrayList();
				
				if(ver>=70)
				{
					manfilterlist.clear();
					CommonData temp=new CommonData("all",ResourceFactory.getProperty("label.gz.allman"));
					manfilterlist.add(temp);
					if("0".equals(model)||"3".equals(model)||"4".equals(model)){
						manfilterlist.addAll(getSearchList(salaryid));//70以上版本添加查询控件保存的查询方案 changxy
					}
				}else{
					manfilterlist=gzbo.getManFilterList();//人员过滤
					manfilterlist.remove(manfilterlist.size()-1);
				}
				this.getFormHM().put("condlist",manfilterlist);
				this.getFormHM().put("filterWhl",filterWhl);//审批不需要filterwhl
				this.getFormHM().put("orderby",salryorder);
				this.getFormHM().put("model",model);
				this.getFormHM().put("bosdate",bosdate);
				this.getFormHM().put("boscount",boscount);
				stipendHmusterBo = null;
				gzbo = null;
			}else if("analysis".equalsIgnoreCase(checksalary)&& "1".equals(reset)){
				
				String gz_module=(String)hm.get("gz_module");
				gz_module=gz_module!=null&&gz_module.trim().length()>0?gz_module:"";
				hm.remove("gz_module");
				if(gz_module.trim().length()<1){
					gz_module=(String)this.getFormHM().get("gz_module");
					gz_module=gz_module!=null&&gz_module.trim().length()>0?gz_module:"0";
				}
				this.getFormHM().put("gz_module",gz_module);
				
				String dbname=(String)hm.get("dbname");
				dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
				hm.remove("dbname");
				if(dbname.trim().length()<1){
					dbname=(String)this.getFormHM().get("dbname");
					dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
				}
				this.getFormHM().put("dbname",dbname);
				
				String category=(String)hm.get("category");
				category=category!=null&&category.trim().length()>0?category:"";
				hm.remove("category");
				if(category.trim().length()<1){
					category=(String)this.getFormHM().get("category");
					category=category!=null&&category.trim().length()>0?category:"";
				}
				this.getFormHM().put("category",category);
                checkSalaryPriv(category);
				
				String selecttime=(String)hm.get("selecttime");
				selecttime=selecttime!=null&&selecttime.trim().length()>0?selecttime:"";
				hm.remove("selecttime");
				if(selecttime.trim().length()<1){
					selecttime=(String)this.getFormHM().get("selecttime");
					selecttime=selecttime!=null&&selecttime.trim().length()>0?selecttime:"";
				}
				this.getFormHM().put("selecttime",selecttime);
				
				year=(String)hm.get("year");
				year=year!=null&&year.trim().length()>0?year:"";
				hm.remove("year");
				if(year.trim().length()<1){
					year=(String)this.getFormHM().get("year");
					year=year!=null&&year.trim().length()>0?year:"";
				}
				if("1".equals(selecttime)/*全部*/)
					year="";
				this.getFormHM().put("year",year);
				
				month=(String)hm.get("month");
				month=month!=null&&month.trim().length()>0?month:"";
				hm.remove("month");
				if(month.trim().length()<1){
					month=(String)this.getFormHM().get("month");
					month=month!=null&&month.trim().length()>0?month:"";
				}
				if("1".equals(selecttime)/*全部*/||"2".equals(selecttime)/*某年*/)
					month="";
				this.getFormHM().put("month",month);
				
				num=(String)hm.get("num");
				num=num!=null&&num.trim().length()>0?num:"";
				hm.remove("num");
				if(num.trim().length()<1){
					num=(String)this.getFormHM().get("count");
					num=num!=null&&num.trim().length()>0?num:"";
				}
				if(!"4".equals(selecttime)/*非某次*/)
					num="";
				this.getFormHM().put("num",num);
				
				String summary=(String)hm.get("summary");
				summary=summary!=null&&summary.trim().length()>0?summary:"";
				if(summary.trim().length()<1){
					summary=(String)this.getFormHM().get("summary");
					summary=summary!=null&&summary.trim().length()>0?summary:"";
				}
				hm.remove("summary");
				this.getFormHM().put("summary",summary);
				
				if("5".equals(selecttime)){
					historyFlag="2";
					dateStart = year.substring(0,4)+"."+month.substring(0,2)+".01";
					dateEnd = year.substring(5,9)+"."+month.substring(3,5)+".01";
				}
				else if("2".equals(selecttime)||"3".equals(selecttime)||"4".equals(selecttime))
					historyFlag="1";
				
				String conditions=(String)this.getFormHM().get("conditions");
				conditions=conditions!=null&&conditions.trim().length()>0?conditions:"";
				if(conditions.trim().length()<1){
					conditions=(String)hm.get("conditions");
					conditions=conditions!=null&&conditions.trim().length()>0?conditions:"";
					hm.remove("conditions");
				}
				this.getFormHM().put("conditions",conditions);

				String whereDate = dataWhere(selecttime,year,month,num);
				
				String whereFactor = condFacor(tabid,conditions,archive);
				if("0".equals(archive))
				{
					GzAnalyseBo gzbo = new GzAnalyseBo(this.getFrameconn());
					gzbo.setTableName("salaryarchive");
					gzbo.createIndexArchive();
					hmusterBo.setAnalyseTableName("salaryarchive");
					HistoryDataBo bo = new HistoryDataBo(this.getFrameconn());
					bo.syncSalaryarchiveStrut();
					bo.updateDbidValue();
					
				}
				hmusterBo.setOnlyGzSpFinished(!"3".equals(archive));
				hmusterBo.setLayerid(layerid);
				hmusterBo.setUserView(this.userView); 
				hmusterBo.setGroupPointItem(groupPoint);
				hmusterBo.setModelFlag("salary"); 
				hmusterBo.getMidvariable(tabid);
				hmusterBo.setIsGroupPoint2(isGroupPoint2);
				hmusterBo.setGroupPoint2(groupPoint2);
				hmusterBo.setLayerid2(layerid2);
				if((currpage==null|| "1".equals(currpage)
						|| "0".equals(currpage)||currpage.trim().length()==0)){
					hmusterBo.setSortitem(sortitem);
					hmusterBo.importData(tabid,a_code,whereDate,whereFactor,summary,gz_module,dbname,category);
				}
				salaryid = category;
			}
			hmusterViewBo.getResourceCloumn(tabid, "salary");
			DbWizard db=new DbWizard(this.frameconn);
			if(!db.isExistTable(userView.getUserName().trim().replaceAll(" ", "")+"_Muster_" + tabid)) {
				throw new Exception("此用户对应花名册不存在，请重新取数！");
			}
			list=hmusterViewBo.getHumster("salary",tabid,isGroupPoint,groupPoint,this.userView.getUserName().trim().replaceAll(" ","")+"_muster_"+tabid,pageRows,currpage
					,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,"",historyFlag,year,month,num,userView,"direct",printGrid,"salary");
			String tableTitleTop=(String)list.get(0);
			String tableHeader=(String)list.get(1);
			String tableBody=(String)list.get(2);
			String tableTitleBottom=(String)list.get(3);
			String turnPage=(String)list.get(4);
			
			HashMap topDateTitleMap = hmusterViewBo.getTopDateTitleMap();//高级花名册日期型上标题Map集合
			
			StringBuffer html= new StringBuffer();
			html.append(tableTitleTop); //changxy 20160905 jsp编码集为GBK
			html.append(tableHeader);
			html.append(tableBody);
			html.append(tableTitleBottom);
			this.getFormHM().put("html", html.toString().replace("\n", ""));
			/* 去掉重复的代码 xiaoyun 2014-7-16 start */
			//this.getFormHM().put("currpage", currpage);
			/* 去掉重复的代码 xiaoyun 2014-7-16 end */
			this.getFormHM().put("turnPage",turnPage.replace("\n", ""));
			this.getFormHM().put("tabid",tabid);
			this.getFormHM().put("printGrid",printGrid);
			this.getFormHM().put("pageRows",pageRows);
			this.getFormHM().put("a_code",a_code);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("currpage",currpage);
			this.getFormHM().put("zeroPrint",zeroPrint);
			this.getFormHM().put("printGrid",printGrid);
			this.getFormHM().put("checksalary",checksalary);
			this.getFormHM().put("isAutoCount",isAutoCount);
			this.getFormHM().put("archive", archive);
			this.getFormHM().put("historyFlag", historyFlag);
			this.getFormHM().put("dateStart", dateStart);
			this.getFormHM().put("dateEnd", dateEnd);
			this.getFormHM().put("topDateTitleMap", topDateTitleMap);
			/* 标识：3030 先点击薪资发放然后点击薪资历史数据进入报表出现异常 xiaoyun 2014-7-15 start */
			//if(this.userView.getHm().containsKey("gz_filterWhl")) {
				//this.userView.getHm().put("gz_filterWhl", "");
			//}
			/* 标识：3030 先点击薪资发放然后点击薪资历史数据进入报表出现异常 xiaoyun 2014-7-15 end */
			hmusterBo = null;
			hmusterViewBo = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		System.gc();
	}
	
	/***
	 * 获取表格控件保存查询方案组sql changxy 
	 * */
	private String getSearchSql(String tabid,String queryPlanId) throws Exception{
		String model=(String)this.getFormHM().get("model");//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		DbWizard db=new DbWizard(this.frameconn);
		if(!db.isExistTable("t_sys_table_query_plan", false))
			throw new Exception("表t_sys_table_query_plan不存在,请用转库大师库升级维护！");
		String sql=" select expression,conditem from t_sys_table_query_plan  where submoduleid='salary_"+tabid+"'"+" and query_plan_id='"+queryPlanId+"' and username='"+this.userView.getUserName().trim().replaceAll(" ","")+"'";
		try {
			TableDataConfigCache tablecatch=null;
			if("1".equals(model)) {//薪资审批进入
				tablecatch=(TableDataConfigCache)this.userView.getHm().get("salarysp_"+tabid);
			}else {
				tablecatch=(TableDataConfigCache)this.userView.getHm().get("salary_"+tabid);
			}
			StringBuffer str=new StringBuffer();
			str.append((StringUtils.isNotEmpty(tablecatch.getQuerySql())?tablecatch.getQuerySql():""));
			str.append(StringUtils.isNotEmpty(tablecatch.getFilterSql())?tablecatch.getFilterSql():"");
			if(!"".equals(queryPlanId)&&!"all".equalsIgnoreCase(queryPlanId)) {
				rs=dao.search(sql);
				String expression="";
				String conditem="";
				while(rs.next()){
					expression=rs.getString("expression");
					conditem=rs.getString("conditem");
				}
				
				/*String[] condArry=conditem.split("`");
				char[] expresschar=expression.toCharArray();*/
				
				FactorList faclist=new FactorList(expression,conditem,this.userView.getUserName(),tablecatch.getQueryFields());
				String sqlStr="";
				sqlStr=faclist.getSingleTableSqlExpression("data");
				sqlStr=" and "+sqlStr.replaceAll("data.", "");
				str.append(sqlStr);
			}
		return str.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	/***
	 * 表格控件保存查询方案集合 changxy
	 * */
	public ArrayList getSearchList(String tabid) throws Exception{
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		String sql=" select query_plan_id,plan_name from t_sys_table_query_plan  where submoduleid='salary_"+tabid+"' and username='"+this.userView.getUserName().trim().replaceAll(" ","")+"'";
		ArrayList list=new ArrayList();
		try {
			DbWizard db=new DbWizard(this.frameconn);
			if(!db.isExistTable("t_sys_table_query_plan",false)) {
				throw new Exception("当前库版本过低，请库升级维护！");
			}
			rs=dao.search(sql);
			String query_plan_id="";
			String plan_name="";
			CommonData data=new CommonData();
			while(rs.next()){
				query_plan_id=rs.getString("query_plan_id");
				plan_name=rs.getString("plan_name");
				if(!"".equals(query_plan_id)&&!"".equals(plan_name)){
					data=new CommonData(query_plan_id,plan_name);
					list.add(data);
				}
			}
			return list;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
    /**
     * 检查薪资/保险类别的资源权限
     * @param salaryid
     */
    private void checkSalaryPriv(String salaryid) throws GeneralException {
        if(salaryid != null && salaryid.length() > 0) {
            String[] salarys = salaryid.split(",");
            CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
            for(int i=0;i<salarys.length;i++){
                safeBo.isSalarySetResource(salarys[i], null);
            }
        }
    }
	
	/**
	 * 将时间转换成sql语句
	 * @param selecttime 判断是取哪种时间
	 * @param year  年
	 * @param month 月
	 * @param num 次
	 * @return
	 */
	private String dataWhere(String selecttime,String year,String month,String num){
		StringBuffer buf = new StringBuffer("");
		if("2".equals(selecttime)){
			if(year!=null&&year.trim().length()>=4)
				buf.append(Sql_switcher.year("A00Z0")+"="+year.substring(0,4));
		}else if("3".equals(selecttime)){
			if(year!=null&&year.trim().length()>=4)
				buf.append(Sql_switcher.year("A00Z0")+"="+year.substring(0,4));
			if(month!=null&&month.trim().length()>=2)
				buf.append(" and "+Sql_switcher.month("A00Z0")+"="+month.substring(0,2));
		}else if("4".equals(selecttime)){
			if(year!=null&&year.trim().length()>=4)
				buf.append(Sql_switcher.year("A00Z0")+"="+year.substring(0,4));
			if(month!=null&&month.trim().length()>=2)
				buf.append(" and "+Sql_switcher.month("A00Z0")+"="+month.substring(0,2));
			if(num!=null&&num.trim().length()>0)
				buf.append(" and A00Z1="+num);
		}else if("5".equals(selecttime)){
			if(year!=null&&year.trim().length()>8&&month!=null&&month.trim().length()>4){
				String time1 = year.substring(0,4)+"-"+month.substring(0,2)+"-01";
				String time2 = year.substring(5,9)+"-"+month.substring(3,5)+"-01";
				
				WorkdiarySQLStr wss=new WorkdiarySQLStr();
				String tempstart=wss.getDataValue("A00Z0",">=",time1);
				String tempend=wss.getDataValue("A00Z0","<=",time2);
				
				buf.append(tempstart+" and "+tempend);
			}
		}
		return buf.toString();
	}
	/**
	 * 将公式转换成sql语句
	 * @param tabid
	 * @return
	 */
	private String condFacor(String tabid,String seiveid,String archive){
		StringBuffer strwhere = new StringBuffer("");
		GzFormulaXMLBo gzbo = new GzFormulaXMLBo(this.getFrameconn(),tabid);
		ArrayList condlist = gzbo.getSeiveItem(seiveid);
		String whereStr="";
		if(condlist!=null&&condlist.size()>0){
			String sexpr=(String)condlist.get(0);
			String sfactor=(String)condlist.get(1);
			    try {
			    	
			    	ContentDAO dao = new ContentDAO(this.frameconn);
			    	this.frowset=dao.search("select distinct itemid,FIELDSETID,ITEMDESC,ITEMLENGTH,DECWIDTH,codesetid,ITEMTYPE  from SALARYSET ");
			    	HashMap map = new HashMap();
			    	while(this.frowset.next())
			    	{
			    		FieldItem item = new FieldItem();
						item.setCodesetid(this.frowset.getString("codesetid"));
						item.setUseflag("1");
						item.setItemtype(this.frowset.getString("itemtype"));
						item.setItemid(this.frowset.getString("itemid").toUpperCase());
						item.setItemdesc(this.frowset.getString("itemdesc"));
                        
						map.put(this.frowset.getString("itemid").toUpperCase(),item);
			    	}
			    	String tableName="salaryhistory";
			    	if("0".equals(archive))
			    		tableName="salaryarchive";
			    	FactorList factor_bo=new FactorList(sexpr.toString(),sfactor.toString().toUpperCase(),this.userView.getUserId(),map);
			    	whereStr=factor_bo.getSingleTableSqlExpression(tableName);
			    	whereStr=whereStr.toUpperCase().replaceAll(tableName.toUpperCase(),"a");
			    	if(whereStr!=null&&!"".equals(whereStr))
			    		whereStr="("+whereStr+")";
					
			    } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
			
		return whereStr;
	}
	/**
	 * 根据职位找直属部门
	 * @param codeid
	 * @return
	 */
	public String getUnByPosition(String codeid)
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
			if(rowSet.next())
			{
				str=rowSet.getString("codesetid")+rowSet.getString("codeitemid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/*public void getYearMonth(String tableName,String year,String month,String count,String checksalary,String selecttime)
	{
		try
		{
			 String historyFlag="0";//调cs插件用到参数
			 String yearFlag="0";
			 String monthFlag="0";
			 String countFlag="0";
			 String dateStart="";
			 String dateEnd="";
			if(checksalary.equalsIgnoreCase("analysis"))
			{
				if(selecttime.equals("2")){
					if(year!=null&&year.trim().length()>=4)
						buf.append(Sql_switcher.year("A00Z0")+"="+year.substring(0,4));
				}else if(selecttime.equals("3")){
					if(year!=null&&year.trim().length()>=4)
						buf.append(Sql_switcher.year("A00Z0")+"="+year.substring(0,4));
					if(month!=null&&month.trim().length()>=2)
						buf.append(" and "+Sql_switcher.month("A00Z0")+"="+month.substring(0,2));
				}else if(selecttime.equals("4")){
					if(year!=null&&year.trim().length()>=4)
						buf.append(Sql_switcher.year("A00Z0")+"="+year.substring(0,4));
					if(month!=null&&month.trim().length()>=2)
						buf.append(" and "+Sql_switcher.month("A00Z0")+"="+month.substring(0,2));
					if(num!=null&&num.trim().length()>0)
						buf.append(" and A00Z1="+num);
				}else if(selecttime.equals("5")){
					if(year!=null&&year.trim().length()>8&&month!=null&&month.trim().length()>4){
						String time1 = year.substring(0,4)+"-"+month.substring(0,2)+"-01";
						String time2 = year.substring(5,9)+"-"+month.substring(3,5)+"-01";
						
						WorkdiarySQLStr wss=new WorkdiarySQLStr();
						String tempstart=wss.getDataValue("A00Z0",">=",time1);
						String tempend=wss.getDataValue("A00Z0","<=",time2);
						
						buf.append(tempstart+" and "+tempend);
					}
				}
			}
			else
			{
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
}
