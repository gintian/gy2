package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterPdf;
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

public class CreatePdfTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			System.gc();
			String exce=(String)this.getFormHM().get("exce");
			exce=exce!=null?exce:"";
			String user=this.userView.getUserName();
            String modelFlag=(String)this.getFormHM().get("modelFlag");
            modelFlag=modelFlag!=null?modelFlag:"";
			String tabID=(String)this.getFormHM().get("tabID");	
			tabID=tabID!=null?tabID:"";
			String bosdate=(String)this.getFormHM().get("bosdate");
			bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"";
			if(bosdate.trim().length()<1){
				bosdate=(String)this.getFormHM().get("bosdate");
				bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"";
			}
			
			String boscount=(String)this.getFormHM().get("boscount");
			boscount=boscount!=null&&boscount.trim().length()>0?boscount:"";
			/*我的薪酬不限*/
			if(!"stipend".equals(modelFlag) && !userView.isHaveResource(IResourceConstant.HIGHMUSTER, tabID))
			    throw new GeneralException(ResourceFactory.getProperty("report.noResource.info"));
			String dbpre=(String)this.getFormHM().get("dbpre");
			dbpre=dbpre!=null?dbpre:"";
			if(dbpre.length() > 3)
			    dbpre = PubFunc.decrypt(dbpre);
		    String infor_Flag=(String)this.getFormHM().get("infor_Flag");				
		    infor_Flag=infor_Flag!=null?infor_Flag:"";
			String pageRows=(String)this.getFormHM().get("pageRows");
			pageRows=pageRows!=null&&pageRows.trim().length()>0?pageRows:"20";
			pageRows= "0".equals(pageRows)?"20":pageRows;
			HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabID);
			 
			String zeroPrint=(String)this.getFormHM().get("zeroPrint");	
			zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"0";
			String emptyRow=(String)this.getFormHM().get("emptyRow");
			emptyRow=emptyRow!=null?emptyRow:"0";
			String column=(String)this.getFormHM().get("column");	
			column=column!=null?column:hmxml.getValue(HmusterXML.COLUMN);
			String dataarea=(String)this.getFormHM().get("dataarea");	
			dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:hmxml.getValue(HmusterXML.DATAAREA);
			String pix=(String)this.getFormHM().get("pix");	
			pix=pix!=null?pix:"";
			String columnLine=(String)this.getFormHM().get("columnLine");
			columnLine=columnLine!=null?columnLine:"";
			if(StringUtils.isEmpty(columnLine)) {
				columnLine=hmxml.getValue(HmusterXML.COLUMNLINE);
			}
			pix=hmxml.getValue(HmusterXML.PIX);
			if("1".equals(dataarea)){
				column="1";
				pix="0";
			}
//			String isGroupPoint=(String)this.getFormHM().get("isGroupPoint");	
			String groupPoint=(String)this.getFormHM().get("groupPoint");	
			groupPoint=groupPoint!=null?groupPoint:"";
			String currpage=(String)this.getFormHM().get("currpage");      //当前页	
			currpage=currpage!=null?currpage:"";
			String history=(String)this.getFormHM().get("history");
			history=history!=null?history:"";
			
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
			
			String year=(String)this.getFormHM().get("year");
			year=year!=null?year:"";
			String month=(String)this.getFormHM().get("month");
			String count=(String)this.getFormHM().get("count");
			/**是否打印格线*/
			String printGrid=(String)this.getFormHM().get("printGrid");
			printGrid=printGrid!=null&&printGrid.trim().length()>0?printGrid:"1";
			
			String condition=(String)this.getFormHM().get("condition"); //查询条件（只针对考勤模块）
			condition=condition!=null?condition:"";
			String returnURL=(String)this.getFormHM().get("returnURL"); //返回的连接地址
			returnURL=returnURL!=null?returnURL:"";
			/**人事异动安全平台改造，这个sql存在后台userview中**/
//			String sql = (String) this.userView.getHm().get("template_sql");//(String)this.getFormHM().get("sql");
//			sql=sql==null?"":sql;
			String platform = (String)this.getFormHM().get("platform");
			platform=platform==null?"":platform;
			HmusterBo hmusterBo=new HmusterBo(this.getFrameconn());
			HashMap cFactorMap=hmusterBo.getCfactor(tabID);
			HmusterPdf   hmusterPdf=new HmusterPdf(this.getFrameconn());
			hmusterPdf.setTopDateTitleMap(topDateTitleMap);
			hmusterPdf.setDataarea(dataarea);
			HmusterViewBo hmusterViewBo=null;
			/*if(dataarea.equals("1"))
			{*/
				hmusterViewBo=new HmusterViewBo(this.getFrameconn(),tabID);
				hmusterViewBo.setUserView(this.userView);
                hmusterViewBo.setInfor_Flag(infor_Flag);
				hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabID));
				hmusterViewBo.setModelFlag(modelFlag);
				if(hmusterViewBo.getTextFormatMap().size()>0)
				{
					if("1".equals(column)&& "1".equals(dataarea))
		    	    	hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabID));
		    		hmusterPdf.setHmusterViewBo(hmusterViewBo);
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
                        String filterWhl=(String)this.getFormHM().get("filterWhl");
                        filterWhl=filterWhl!=null&&filterWhl.trim().length()>0?filterWhl:"";
                        filterWhl = PubFunc.keyWord_reback(filterWhl);
                        StipendHmusterBo stipendHmusterBo=new StipendHmusterBo(this.getFrameconn(),this.userView,salaryid);
                        stipendHmusterBo.setModel(model);
                        stipendHmusterBo.setFilterWhl(filterWhl);
                        stipendHmusterBo.setCheckdata(bosdate);
    					stipendHmusterBo.setChecknum(boscount);
                        stipendHmusterBo.createSQL(tabID,salaryid,a_code,condid,cFactorMap);   
                        hmusterViewBo.setSalaryDataTable(stipendHmusterBo.getSalaryDataTable());
                        hmusterViewBo.setSalaryDataTableCond(stipendHmusterBo.getSalaryDataTableCond());
                    }
                }
			/*}*/
			hmusterPdf.setColumn(column);
			String temptable = this.userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+tabID;
			if(temptable.indexOf("（")!=-1||temptable.indexOf("）")!=-1){
				temptable = "\""+temptable+"\"";
			}
			String isAutoCount=(String)this.getFormHM().get("isAutoCount");	
			isAutoCount=isAutoCount!=null&&isAutoCount.trim().length()>0?isAutoCount:"1";
			isAutoCount="1";
			String isGroupPoint="0";
			groupPoint="";
			if(cFactorMap.get("groupN")!=null)
			{
				isGroupPoint="1";
				groupPoint=(String)cFactorMap.get("groupN");	
				if(cFactorMap.get("multipleGroupN")!=null) {
					hmusterPdf.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));
					hmusterViewBo.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));//1：分组不分页;标识
				}
			}
            hmusterPdf.setGroupPoint(groupPoint);
			String isGroupPoint2=(String)this.getFormHM().get("isGroupPoint2");	
			String groupPoint2=(String)this.getFormHM().get("groupPoint2");	
            if(groupPoint2==null) {
               
                groupPoint2=hmxml.getValue(HmusterXML.GROUPFIELD2);
                if(groupPoint2!=null&&groupPoint2.length()>0)
                    isGroupPoint2="1";
                else
                    isGroupPoint2="0";
            }
			hmusterPdf.setIsGroupPoint2(isGroupPoint2);
			hmusterPdf.setGroupPoint2(groupPoint2);
			String url="";
			String selectedPoint=(String)this.getFormHM().get("selectedPoint");
			String toScope=(String)this.getFormHM().get("toScope");
			String fromScopt=(String)this.getFormHM().get("fromScopt");
			String showPartJob = (String)this.getFormHM().get("showPartJob");
			if(showPartJob==null)
				showPartJob="false";
			hmusterPdf.setShowPartJob(showPartJob);
			if(selectedPoint!=null)
			{
				String[] temp = selectedPoint.split("/");
				if(temp!=null&&temp.length>1){
					if ("D".equals(temp[2])) {
						hmusterPdf.setYearmonth(hmusterPdf.parseSelectPoint(toScope, fromScopt));
					}
				}
			}
			 hmusterPdf.setHmusterViewBo(hmusterViewBo);
			if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)
					|| "1".equals(modelFlag)|| "4".equals(modelFlag))//对人员管理、职位管理、机构管理进行操作
			{
				hmusterPdf.getResourceCloumn(tabID, infor_Flag);
				url=hmusterPdf.executePdf(infor_Flag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
						,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,this.userView.getUserFullName(),dbpre,history,year,month,count,userView,printGrid,modelFlag,platform);
				
			}else if("salary".equals(modelFlag)){
			   
				hmusterPdf.getResourceCloumn(tabID, infor_Flag);
				url=hmusterPdf.executePdf(infor_Flag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
						,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,this.userView.getUserFullName(),"","0","","","",userView,printGrid,modelFlag,platform);
				
			}
			else if("stipend".equals(modelFlag))
			{
				String a0100=(String)this.getFormHM().get("a0100");
				a0100 = PubFunc.decrypt(a0100);
				String groupCount=(String)this.getFormHM().get("groupCount");
				groupCount=groupCount==null?"0":groupCount;
				if(!"0".equals(groupCount)) {
					
					if(StringUtils.isEmpty(hmxml.getValue(HmusterXML.GROUPFIELD))) { //分组指标
						throw GeneralExceptionHandler.Handle(new GeneralException("模板内未设置分组指标,请设置!"));
					}
				}
				hmusterPdf.setGroupCount(groupCount);
				hmusterPdf.setA0100(a0100);
				hmusterPdf.getResourceCloumn(tabID, "stipend");
				url=hmusterPdf.executePdf("stipend",tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
						,isAutoCount,zeroPrint,"",column,pix,"",this.userView.getUserFullName(),dbpre,"0","","","",userView,"1",modelFlag,platform);

				
			}
			else if("15".equals(modelFlag)){
				String a0100=(String)this.getFormHM().get("a0100");
				hmusterPdf.setA0100(a0100);
				hmusterPdf.getResourceCloumn(tabID, "stipend");
				url=hmusterPdf.executePdf("stipend",tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
						,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,this.userView.getUserFullName(),dbpre,"0","","","",userView,printGrid,"salary",platform);	
			}else{
				if(currpage!=null&& "0".equals(currpage))
					currpage="1";

				if("81".equals(modelFlag))
				{
					dbpre="";
					history="0";
					year=""; month="";count="";
				}
				/*if(modelFlag.equals("5"))
				{
					HmusterViewBo hmusterViewBo=new HmusterViewBo(this.getFrameconn());
					sql=SafeCode.decode(sql);
					String str=hmusterViewBo.getA0100s(sql);
					hmusterPdf.setSql(str);
				}*/
				hmusterPdf.getResourceCloumn(tabID, modelFlag);
				url=hmusterPdf.executePdf(modelFlag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
						,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,this.userView.getUserFullName(),dbpre,history,year,month,count,userView,printGrid,modelFlag,platform);
			}
				
			hmusterPdf=null;
			url = PubFunc.encrypt(url);
			this.getFormHM().put("url",url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			System.gc();
		}
		
		
		
	}

}
