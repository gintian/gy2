package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterExcelBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterViewBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.StipendHmusterBo;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class CreateExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		String filename = "";
		try
		{
			System.gc();
			String isGroupPoint=(String)this.getFormHM().get("isGroupPoint");	//是否选用分组指标  1:选用
			String groupPoint=(String)this.getFormHM().get("groupPoint");	//已选的分组指标
			String history=(String)this.getFormHM().get("history"); //1:最后一条历史纪录  3：某次历史纪录 2：部分历史纪录 
			String bosdate=(String)this.getFormHM().get("bosdate");
			bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"";
			if(bosdate.trim().length()<1){
				bosdate=(String)this.getFormHM().get("bosdate");
				bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"";
			}
			
			String boscount=(String)this.getFormHM().get("boscount");
			boscount=boscount!=null&&boscount.trim().length()>0?boscount:"";
			//{0=2016年2月,1=2016年3月}
			String topDateTitle = (String)this.getFormHM().get("topDateTitleMap");
			HashMap topDateTitleMap = new HashMap();//高级花名册日期型上标题Map集合
			if(StringUtils.isNotEmpty(topDateTitle)){				
				//0=2016年2月,1=2016年3月
				topDateTitle = topDateTitle.substring(1, topDateTitle.length()-1);
				if(topDateTitle.length()>0){					
					//0=2016年2月    1=2016年3月
					String[] topDateTitles = topDateTitle.split(",");
					if(topDateTitles.length>0){
						for(int i = 0;i < topDateTitles.length; i++){
							//0    2016年2月
							String[] temp = topDateTitles[i].split("=");
							topDateTitleMap.put(temp[0], temp[1]);
						}
					}
				}
			}
			
			String year=(String)this.getFormHM().get("year"); //年
			String month=(String)this.getFormHM().get("month"); //月
			String count=(String)this.getFormHM().get("count"); //次
			String printGrid=(String)this.getFormHM().get("printGrid"); //打印格线     0:不打印  1：打印
			String dbpre=(String)this.getFormHM().get("dbpre"); //应用库表前缀
			if(dbpre != null && dbpre.length() > 3)
			    dbpre = PubFunc.decrypt(dbpre);
			String modelFlag=(String)this.getFormHM().get("modelFlag"); //模块标识
			String zeroPrint=(String)this.getFormHM().get("zeroPrint");	//0:不为零打印  1：零打印
			String infor_Flag=(String)this.getFormHM().get("infor_Flag");	//1人员, 2机构, 3职位, 5基准岗位
		    String column=(String)this.getFormHM().get("column"); //0:不分栏 1：横向分栏  2：纵向分栏
            String tabID=(String)this.getFormHM().get("tabID");//选中的花名册id
            /*我的薪酬不限*/
            if(!"stipend".equals(modelFlag) && !userView.isHaveResource(IResourceConstant.HIGHMUSTER, tabID))
                throw new GeneralException(ResourceFactory.getProperty("report.noResource.info"));
		    String dataarea=(String)this.getFormHM().get("dataarea"); //0:单行数据区 1：多行数据区	
		    HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabID);
		    if(StringUtils.isEmpty(column)) {
		    	column=hmxml.getValue(HmusterXML.COLUMN);
		    }
		    if(StringUtils.isEmpty(dataarea)) {
		    	dataarea = hmxml.getValue(HmusterXML.DATAAREA);
		    }
			dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
			if("1".equals(dataarea)){
				column="1";
			}
			
			String isGroupedSerials="0";//按组显示序号
			String groupedSerials = hmxml.getValue(HmusterXML.GROUPEDSERIALS);
			if("1".equals(groupedSerials))
				isGroupedSerials="1";
			String isGroupPoint2=(String)this.getFormHM().get("isGroupPoint2");	//是否选用分组指标  1:选用（第二分组指标）
			String groupPoint2=(String)this.getFormHM().get("groupPoint2"); //已选的分组指标 （第二分组指标）
			if(groupPoint2==null) {
			    groupPoint2=hmxml.getValue(HmusterXML.GROUPFIELD2);
			    if(groupPoint2!=null&&groupPoint2.length()>0)
		            isGroupPoint2="1";
			    else
			        isGroupPoint2="0";
			}
			String pageRows=(String)this.getFormHM().get("pageRows");//n:为用户指定的每页行数
			pageRows=pageRows!=null&&pageRows.trim().length()>0?pageRows:"20";
			String emptyRow=(String)this.getFormHM().get("emptyRow"); //0：空行不打印  1：空行打印
			emptyRow=emptyRow!=null&&emptyRow.trim().length()>0?emptyRow:"0";
            //String sql=(String)this.getFormHM().get("sql");
            //sql=sql==null?"":sql;
            HmusterViewBo hmusterViewBo=null;
			/*if(dataarea.equals("1"))
			{*/
				hmusterViewBo=new HmusterViewBo(this.getFrameconn(),tabID);
				hmusterViewBo.setInfor_Flag(infor_Flag);
				hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabID));
				hmusterViewBo.setUserView(this.userView);
				if(hmusterViewBo.getTextFormatMap().size()>0)
				{
					if("1".equals(column)&&"1".equals(dataarea))
		        		hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabID));
				}
				else
				{
					//hmusterViewBo=null;
				}
				if("salary".equals(modelFlag)) {
				    String checksalary=(String)this.getFormHM().get("checksalary");
				    checksalary=checksalary==null?"":checksalary;
				    String archive=(String)this.getFormHM().get("archive");
				    archive=archive==null?"":archive;
		            String salaryid=(String)this.getFormHM().get("salaryid");
		            salaryid=salaryid==null?"":salaryid;
		            if(salaryid.indexOf(",")!=-1) {
		                String[] lst=salaryid.split(",");
		                salaryid = lst[0];
		            }
		            if(salaryid.length()>0) {
    				    String a_code=(String)this.getFormHM().get("a_code");
    				    a_code=a_code==null?"":a_code;
    				    String condid=(String)this.getFormHM().get("condid");
    				    condid=condid==null?"":condid;
                        if("".equals(condid))
                            condid="all";
    				    String model=(String)this.getFormHM().get("model");
    				    model=model==null?"":model;
    				    if("".equals(model)) {
    				        if ("analysis".equals(checksalary)) {
    				            if("0".equals(archive))
    				                model="3";  // 工资历史归档
    				            else
    				                model="1";  // 工资历史
    				        }
    				    }
    	                String filterWhl=(String)this.getFormHM().get("filterWhl");
    	                filterWhl=filterWhl!=null&&filterWhl.trim().length()>0?filterWhl:"";
    	                filterWhl = PubFunc.keyWord_reback(filterWhl);
    	                StipendHmusterBo stipendHmusterBo=new StipendHmusterBo(this.getFrameconn(),this.userView,salaryid);
    	                stipendHmusterBo.setModel(model);
    	                stipendHmusterBo.setFilterWhl(filterWhl);
    	                stipendHmusterBo.setCheckdata(bosdate);
    					stipendHmusterBo.setChecknum(boscount);
    	                HmusterBo hmb=new HmusterBo(this.frameconn,this.userView);
    	                HashMap cFactorMap = hmb.getCfactor(tabID);	                
    	                stipendHmusterBo.createSQL(tabID,salaryid,a_code,condid,cFactorMap);   
    	                hmusterViewBo.setSalaryDataTable(stipendHmusterBo.getSalaryDataTable());         // 工资数据表和取数条件
    	                hmusterViewBo.setSalaryDataTableCond(stipendHmusterBo.getSalaryDataTableCond());
		            }
				}
			/*}*/
			HmusterExcelBo bd=null;
//			modelFlag=modelFlag.equals("15")?"stipend":modelFlag;
			hmusterViewBo.setDataarea(dataarea);
			hmusterViewBo.setModelFlag(modelFlag);
			if("stipend".equals(modelFlag))
				bd=new HmusterExcelBo(this.getFrameconn(),Integer.parseInt(column),this.getUserView(),modelFlag,tabID,hmusterViewBo);
			else
				bd=new HmusterExcelBo(this.getFrameconn(),Integer.parseInt(column),this.getUserView(),infor_Flag,tabID,hmusterViewBo);
			String excetype=(String)this.getFormHM().get("excelType");//设置导出格式 0 xls 1 xlsx
			bd.setExcelType(0);//代码优化 默认导出xls 
			if("1".equals(excetype))
				bd.setExcelType(1);
			bd.setTopDateTitleMap(topDateTitleMap);
			bd.setRows(Integer.parseInt(pageRows));
			if("1".equals(column)) {
				if("0".equals(dataarea)) {
					bd.setEmptyRow("0");
				}else {
					bd.setEmptyRow(emptyRow);
				}
			}else {
				bd.setEmptyRow(emptyRow);
			}
			bd.setDataarea(dataarea);
			bd.setColumn(column);
			
			/*if(modelFlag.equals("5"))
			{
				HmusterViewBo hmusterViewBo=new HmusterViewBo(this.getFrameconn());
				sql=SafeCode.decode(sql);
				String str=hmusterViewBo.getA0100s(sql);
				bd.setSql(str);
			}*/
			HmusterBo hmusterBo=new HmusterBo(this.getFrameconn());
			HashMap cFactorMap=hmusterBo.getCfactor(tabID);
			bd.getResourceCloumn(tabID, infor_Flag);
			bd.setIsGroupPoint2(isGroupPoint2);
			bd.setIsGroupedSerials(isGroupedSerials);
			bd.setGroupPoint2(groupPoint2);
			if(cFactorMap.get("groupN")!=null)
			{
				if(cFactorMap.get("multipleGroupN")!=null)
					bd.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));
			}
			String selectedPoint=(String)this.getFormHM().get("selectedPoint");
			String toScope=(String)this.getFormHM().get("toScope");
			String fromScopt=(String)this.getFormHM().get("fromScopt");
			String showPartJob=(String)this.getFormHM().get("showPartJob");//是否显示兼职人员
			if(showPartJob==null)
				showPartJob="false";
			bd.setShowPartJob(showPartJob);
			if(selectedPoint!=null)
			{
				String[] temp = selectedPoint.split("/");
				if(temp!=null&&temp.length>1){
					if ("D".equals(temp[2])) {
						bd.setYearmonth(bd.parseSelectPoint(toScope, fromScopt));
					}
				}
			}
			if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)
					|| "1".equals(modelFlag)|| "4".equals(modelFlag))//对人员管理、职位管理、机构管理进行操作
			{
						filename = bd.executReportExcel(isGroupPoint,groupPoint,dbpre,history,year,month,count,printGrid,modelFlag,zeroPrint);
			}
			else if("stipend".equals(modelFlag)|| "salary".equals(modelFlag))
			{
				String a0100=(String)this.getFormHM().get("a0100");
				a0100 = PubFunc.decrypt(a0100);
				bd.setA0100(a0100);
				isGroupPoint="0";
				groupPoint="";
				if(cFactorMap.get("groupN")!=null)
				{
					isGroupPoint="1";
					groupPoint=(String)cFactorMap.get("groupN");
					if(cFactorMap.get("multipleGroupN")!=null)
						bd.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));
				}
				String groupCount=(String)this.getFormHM().get("groupCount");
				groupCount=groupCount==null?"0":groupCount;
				bd.setGroupPoint(groupPoint);
				bd.setGroupCount(groupCount);
				filename =bd.executReportExcel(isGroupPoint,groupPoint,dbpre,"0","","","",printGrid,modelFlag,zeroPrint);
			}
			else
			{
				HashMap factorMap=bd.getCfactor(tabID);
				if(factorMap.get("groupN")!=null)
				{
					isGroupPoint="1";								//是否分组
					groupPoint=(String)factorMap.get("groupN");     //分组指标
				}
				if(factorMap.get("isColumn")!=null&& "0".equals((String)factorMap.get("isColumn")))  //不分栏
				{
					column="0";
				}
				else if(factorMap.get("isColumn")!=null)
				{
					column=(String)factorMap.get("columnAspect");   //1：横分  2：纵分
				}
				if("81".equals(modelFlag))
				{
					dbpre="";
					history="0";
					year=""; month="";count="";
				}
					filename = bd.executReportExcel(isGroupPoint,groupPoint,dbpre,history,year,month,count,printGrid,modelFlag,zeroPrint);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			System.gc();
//			filename=filename.replaceAll(".xls","#");
			filename=PubFunc.encrypt(filename);
			this.getFormHM().put("outName", filename);	
		}

	}

}
