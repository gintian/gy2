package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterPdf;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterViewBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.StipendHmusterBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExecuteHmusterTempTrans extends IBusiness {
	private Logger log = LoggerFactory.getLogger(ExecuteHmusterTempTrans.class);

	public void execute() throws GeneralException {
		long start = System.currentTimeMillis();
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**模块标志=5从人事异动进入=3从人员管理--高级花名册进入=41职位花名册=21机构花名册=15税率表花名册=2合同台帐花名册 5 人事异动 =81考勤*/
			String modelFlag=(String)this.getFormHM().get("modelFlag");
			if (StringUtils.isEmpty(modelFlag)) {
				modelFlag = (String) hm.get("modelFlag");
			} 
			String showbuttons=(String)hm.get("showbuttons");
			if(StringUtils.isNotEmpty(showbuttons)) {
				this.getFormHM().put("showbuttons", showbuttons);
				hm.remove("showbuttons");
			}
			/**定了了自动取数条件，默认取取数条件中定义的人员库的数据，如果changeDbpre=1按默认取，否则取后台传过来的人员库*/
			
			String tabID=(String)this.getFormHM().get("tabID");	
		    tabID=tabID!=null&&tabID.trim().length()>0?tabID:"-1";
		    if(!isExitsTableId(tabID)) {
		    	throw GeneralExceptionHandler.Handle(new Exception("模板不存在，请检查设置!"));
		    }
			HmusterBo hmusterBo=new HmusterBo(this.getFrameconn(),this.getUserView());
			String changeDbpre=(String)hm.get("changeDbpre");
			changeDbpre=changeDbpre==null?"":changeDbpre;
			hm.remove("changeDbpre");
			String combineField=(String)this.getFormHM().get("combineField");
			/**人事异动进入，是否显示打印预演，输出excel，pdf功能按钮*/
			String isPrint="1";
			if("5".equals(modelFlag))
			{
				if(this.getFormHM().get("isPrint")!=null)
					isPrint=(String)this.getFormHM().get("isPrint");
			}
			log.info("ExecuteHmusterTempTrans第 1 段用时:{} ms",(System.currentTimeMillis() - start));
		    String isGetData="0";  // 0点击取数按钮进入,1否
		    if(this.getFormHM().get("isReData")!=null&&!"".equals((String)this.getFormHM().get("isReData")))
		    {
		    	isGetData=(String)this.getFormHM().get("isReData");
		    }
		    if(hm.get("isGetData")!=null)
		    {
		    	isGetData=(String)hm.get("isGetData");
		    	hm.remove("isGetData");
		    }
		    String isCloseButton="0";//0没有关闭按钮==1有关闭按钮
		    if(this.getFormHM().get("isCloseButton")!=null&&((String)this.getFormHM().get("isCloseButton")).trim().length()>0)
		    {
		    	isCloseButton=(String)this.getFormHM().get("isCloseButton");
		    }
		    this.getFormHM().put("isCloseButton", isCloseButton);
		    /**税率表名册专用，=0按管理范围=1按操作单位*/
		    String filterByMdule="0";
		    if(hm.get("filterByMdule")!=null)
		    {
		    	filterByMdule=(String)hm.get("filterByMdule");
		    	hm.remove("filterByMdule");
		    }
		    else
		    {
		    	filterByMdule=(String)this.getFormHM().get("filterByMdule");
		    }
		    /* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
		    // 用户设置的查询条件
		    String conditionBase = (String)this.getFormHM().get("conditionBase");
		    // 常用查询集合
		    //ArrayList condlist = null;
		    // 是否为自动取数
		    boolean isAutoImport = false;
		    // 花名册业务类
		    MusterBo musterbo = new MusterBo(this.getFrameconn(),this.userView);
		    /* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
		    
			String condition=(String)this.getFormHM().get("condition"); //查询条件（只针对考勤模块）
			condition = PubFunc.keyWord_reback(condition);
			condition=condition!=null&&condition.trim().length()>0?condition:"";
			condition=PubFunc.keyWord_reback(condition);
			// 考勤管理  condition参数改为通过 userview传  解决前台传参SQL注入问题
			if("81".equals(modelFlag)) {
				condition = (String)this.userView.getHm().get("kq_condition");
			}
			String single = (String)hm.get("single");
			if (StringUtils.isNotEmpty(single)) {
				condition += "`"+single;
			}
			String returnURL=(String)this.getFormHM().get("returnURL"); //返回的连接地址
			returnURL = PubFunc.keyWord_reback(returnURL);
			returnURL=returnURL!=null&&returnURL.trim().length()>0?returnURL:"";
			String relatTableid=(String)this.getFormHM().get("relatTableid");
			if(relatTableid==null) relatTableid="";
			// relatTableidFlag暂未用先注释
//			String relatTableidFlag="-1";
			if(hm.get("relatTableid")!=null)
			{
				// relatTableidFlag改为relatTableid
				relatTableid=(String)hm.get("relatTableid");
				hm.remove("relatTableid");
			}
			String closeWindow=(String)this.getFormHM().get("closeWindow");
			this.getFormHM().put("closeWindow", closeWindow);
			String user=this.userView.getUserName().trim().replaceAll(" ", "");
			
			if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, tabID))
				throw GeneralExceptionHandler.Handle(new Exception("没有操作该花名册的权限！"));
			HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabID);
			String dbpre=(String)this.getFormHM().get("dbpre");
			dbpre=dbpre!=null?dbpre:"";
			ArrayList dblist = (ArrayList)this.getFormHM().get("dblist");
			if(dblist==null||dblist.size()==0||"1".equals(changeDbpre))
			{
				// 若是考勤模块则走该用户考勤人员库范围,其他则走正常权限范围内人员库
				ArrayList a_list = "81".equals(modelFlag)?KqPrivBo.getB0110Dase(this.userView, this.frameconn):this.userView.getPrivDbList();
				if(a_list==null||a_list.size()==0)
					throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
				if("2".equals(modelFlag)){
					ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
					String dbstr=csxml.getTextValue("/Params/nbase");
					dbstr=dbstr!=null?dbstr:"";
					ArrayList list = new ArrayList();
					for(int i=0;i<a_list.size();i++){
						String pre=(String)a_list.get(i);
						if(dbstr.length()>0){
							if(dbstr.toLowerCase().indexOf(pre.toLowerCase())!=-1)
							{
								list.add(new CommonData(pre.toUpperCase(),AdminCode.getCodeName("@@", pre)));
							}
						}
					}
					if(list.size()>0)
						dblist = list;
				}else{
					ArrayList list = new ArrayList();
					for(int i=0;i<a_list.size();i++){
						String pre=(String)a_list.get(i);
						list.add(new CommonData(pre.toUpperCase(),AdminCode.getCodeName("@@", pre)));
					}
					if(list.size()>0)
						dblist = list;
				}
				addAllDBItem(dblist);
				if((dbpre.trim().length()<1||!dbExists(dbpre, dblist))&&dblist.size()>0){
					dbpre=((CommonData)dblist.get(0)).getDataValue();
				}
			}else if(dbpre.trim().length()<1&&dblist.size()>0){
				dbpre=((CommonData)dblist.get(0)).getDataValue();
			}
			String historyRecord=(String)this.getFormHM().get("historyRecord");  // 0:重新取数  1：上次取数
			historyRecord=historyRecord!=null&&historyRecord.trim().length()>0?historyRecord:"1";
		    String infor_Flag=(String)this.getFormHM().get("infor_Flag");//1人员, 2机构, 3职位, 5基准岗位
		    if(infor_Flag==null) infor_Flag="";
		    /**=1查询结果=2全部记录*/
			String queryScope=(String)this.getFormHM().get("queryScope");
			/**考勤的花名册，取全部记录*/
			if("81".equals(modelFlag))
				queryScope="2";
			String flag=(String)this.getFormHM().get("flag");//"0":无  "1"有子集指标无年月标识,可按最后一条历史纪录查  "2"有子集指标无年月标识,可按取部分历史纪录查   "3"有子集指标和年月标识，可按某次的历史纪录查//4:按某年某次取
			String history=(String)this.getFormHM().get("history");	//1:最后一条历史纪录  3：某次历史纪录 2：部分历史纪录
			history=history!=null?("0".equals(history)?"1":history):"";//history为0时 默认取最后一条历史记录
			String countflag=(String)this.getFormHM().get("countflag");	//汇总
			countflag=countflag!=null?countflag:hmxml.getValue(HmusterXML.NEEDSUM);//为null查询存储的参数
			if(!"2".equals(history))
				countflag="0";//最后一条历史记录 与某次历史记录不汇总 部分历史记录才会汇总
			String year=(String)this.getFormHM().get("year");//年
			String month=(String)this.getFormHM().get("month");	//月
			String count=(String)this.getFormHM().get("count");	//次
			
			//history=2 1*2::a58z0=`a58z1=`::0
			// fromScopt 会有 ≯ 
			String fromScopt=PubFunc.keyWord_reback(PubFunc.reBackWord((String)this.getFormHM().get("fromScopt")));	
			String toScope=PubFunc.keyWord_reback(PubFunc.reBackWord((String)this.getFormHM().get("toScope")));	//A58
			String selectedPoint=PubFunc.keyWord_reback((String)this.getFormHM().get("selectedPoint"));	//a58  //A41Z0/0/D/A41  下拉选中的指标
			
			String isAutoCount=(String)this.getFormHM().get("isAutoCount");	//0:为自动计算  1:用户指定
			String pageRows=(String)this.getFormHM().get("pageRows");//n:为用户指定的每页行数
			if(isAutoCount==null|| "".equals(isAutoCount))
			{
				isAutoCount=hmxml.getValue(HmusterXML.ROWCOUNTMODE);
			}
			                                            // 指定clears参数后，以花名册设计中指定的为准
			if(pageRows==null|| "".equals(pageRows.trim())||"1".equals(hm.get("clears")))
			{
			    isAutoCount=hmxml.getValue(HmusterXML.ROWCOUNTMODE);
				String rows=hmxml.getValue(HmusterXML.ROWCOUNT);
				if("0".equals(isAutoCount))
					pageRows="20";
				else{
					pageRows=rows;
				}
			}
			/**是否显示兼职人员，默认false*/
			String SHOW_PART_JOB="false";
			if("3".equals(modelFlag))
				SHOW_PART_JOB=hmxml.getValue(HmusterXML.SHOW_PART_JOB);
			if(SHOW_PART_JOB==null|| "".equals(SHOW_PART_JOB))
				SHOW_PART_JOB="false";
			this.getFormHM().put("pageRows",pageRows);
			String zeroPrint=(String)this.getFormHM().get("zeroPrint");	
			if("TRUE".equalsIgnoreCase(hmxml.getValue(HmusterXML.SHOWZERO))){
				zeroPrint="1";//人事异动模块调用时无取数按钮 需从cs后台取设置参数
			}
			zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"0";
			String factor = hmxml.getValue(HmusterXML.FACTOR);
			//liuy 2015-11-19 14558：高级花名册设置了自动取数 点击高级花名册后没有数据 begin
			//$THISMONTH[]`生成的sql带年，当月`生成的sql不带年。现在当月统一处理成不带年的
			if("$THISMONTH[]`".equals(factor.substring(factor.indexOf("=")+1)))
				factor = factor.substring(0,factor.indexOf("=")+1) + "当月`";
			//liuy 2015-11-19 end
			String expr=hmxml.getValue(HmusterXML.EXPR);
			log.info("ExecuteHmusterTempTrans第 2 段用时:{} ms",(System.currentTimeMillis() - start));
			/* 任务：3692 哈药集团 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
			// 自定义的常用查询id组成的字符串
			String mainParamCond = hmxml.getValue(HmusterXML.MAINPARAMCOND);
			// 自定义的参数标题
			String mainParamTitle = hmxml.getValue(HmusterXML.MAINPARAMTITLE);
			this.getFormHM().put("mainParamCondList", null);
			this.getFormHM().put("mainParamTitle", null);
			/* 任务：3692 哈药集团 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
			
			String column=hmxml.getValue(HmusterXML.COLUMN);
			String hz=hmxml.getValue(HmusterXML.HZ);
			if("1".equals(column))
				column = hz;
			String dataarea = hmxml.getValue(HmusterXML.DATAAREA);
			dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
			String pix=hmxml.getValue(HmusterXML.PIX);
			if("1".equals(dataarea)){
				column="1";
				pix="-1";
			}
			String columnLine=hmxml.getValue(HmusterXML.COLUMNLINE);
			String groupPoint=hmxml.getValue(HmusterXML.GROUPFIELD); //分组指标
			String layerid=hmxml.getValue(HmusterXML.GROUPLAYER);
			String groupOrgCodeSet = hmxml.getValue(HmusterXML.GROUPORGCODESET);
			String groupOrgCodeSet2 = hmxml.getValue(HmusterXML.GROUPORGCODESET2);
			String sortitem=hmxml.getValue(HmusterXML.SORTSTR);
			/**不按管理范围取数*/
			String NO_MANAGE_PRIV=hmxml.getValue(HmusterXML.NO_MANAGE_PRIV);
			
			sortitem=sortitem!=null?sortitem:"";
			String emptyRow=hmxml.getNgrid();
			String isGroupPoint="0";
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				isGroupPoint="1";
			}
			String isGroupedSerials="0";//按组显示序号
			String groupedSerials = hmxml.getValue(HmusterXML.GROUPEDSERIALS);
			if("1".equals(groupedSerials))
				isGroupedSerials="1";
			String groupPoint2=hmxml.getValue(HmusterXML.GROUPFIELD2);
			String layerid2=hmxml.getValue(HmusterXML.GROUPLAYER2);
			String isGroupPoint2="0";
			if(groupPoint2!=null&&groupPoint2.trim().length()>0)
				isGroupPoint2="1";
			this.getFormHM().put("showPartJob", SHOW_PART_JOB);//兼职人员
			this.getFormHM().put("groupPoint2", groupPoint2);
			this.getFormHM().put("isGroupPoint2", isGroupPoint2);
			
			String currpage=(String)this.getFormHM().get("currpage");      //当前页
			currpage=currpage!=null&&currpage.trim().length()>0?currpage:"1";
			if("0".equals(historyRecord))//重新取数 当前页应为第一页
				currpage="1";
			if("1".equals(hm.get("clears")))
				currpage="0";
			ArrayList photoList=(ArrayList)this.getFormHM().get("photoList");// 临时文件夹中的图片文件列表
			
			String printGrid=(String)this.getFormHM().get("printGrid");
			printGrid=printGrid!=null&&printGrid.trim().length()>0?printGrid:"1";
			
			String operateMethod=(String)hm.get("operateMethod");// "direct":直接生成  "next":设置生成
			if(operateMethod==null) operateMethod="";
			
			String checkflag = (String)hm.get("checkflag");
			checkflag=checkflag!=null?checkflag:"";
			hm.remove("checkflag");
			String temptable = this.userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+tabID;
			if(temptable.indexOf("（")!=-1||temptable.indexOf("）")!=-1){
				temptable = "\""+temptable+"\"";
			}
			if("1".equals(infor_Flag))
			{
				hmusterBo.setNo_manger_priv(NO_MANAGE_PRIV);
				if("3".equals(modelFlag))
				{
					hmusterBo.setShowPartJob(SHOW_PART_JOB);
				}
			}
			hmusterBo.setGroupPoint2(groupPoint2);
			hmusterBo.setIsGroupPoint2(isGroupPoint2);
			hmusterBo.setLayerid2(layerid2);
			ArrayList groupPointList=new ArrayList();
			hmusterBo.setGroupOrgCodeSet(groupOrgCodeSet);
			hmusterBo.setGroupOrgCodeSet2(groupOrgCodeSet2);
			hmusterBo.setTemptable(temptable);
			hmusterBo.setModelFlag(modelFlag);
			String username = this.userView.getUserFullName();
			//调用cs插件用参数
			String historyFlag="0";
			String dateStart="";
			String dateEnd="";
			if("1".equals(history))//历史记录取值方式:0当前,1年月次,2条件
			{
				historyFlag="0";
				
			}else if("3".equals(history)){//取某年月次记录: <Year>年(默认为0)</Year><Month>月(默认为0)</Month><Times>次(默认为0)</Times>
				
				historyFlag="1";
				
			}else if("2".equals(history)){//取满足条件记录时，如果为日期指标:<DateStart>开始日期</DateStart><DateEnd>结束日期</DateEnd>
				
				historyFlag="2";
				if(selectedPoint!=null&&!"".equals(selectedPoint))
				{
					String[] arr=selectedPoint.split("/");
					if(arr.length>3)
					{
						if("D".equalsIgnoreCase(arr[2]))
						{
							dateStart=fromScopt.replaceAll("-", ".");
							dateEnd=toScope.replaceAll("-", ".");
						}
					}
				}
			}
			
			if("-1".equals(tabID)){
				throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
			}else{
				/**returnflag=mobile移动应用*/
				String returnflag=(String)this.getFormHM().get("returnflag");
				HmusterViewBo hmusterViewBo=new HmusterViewBo(this.getFrameconn(),tabID);
				hmusterViewBo.setReturnflag(returnflag);
				hmusterViewBo.setNo_manger_priv(NO_MANAGE_PRIV);
				/**分组指标2*/
				hmusterViewBo.setIsGroupPoint2(isGroupPoint2);
				hmusterViewBo.setGroupPoint2(groupPoint2);
				hmusterViewBo.setGroupPoint(groupPoint);
				hmusterViewBo.setDataarea(dataarea);
				hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabID));
				if(hmusterViewBo.getTextFormatMap().size()>0)
				{
					if("1".equals(column)&& "1".equals(dataarea))
		        		hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabID));
				}
				
				if("5".equals(modelFlag))
				{
				    String sql = (String)this.userView.getHm().get("template_sql");
				    if(sql!=null&&!"".equals(sql)){
				    	//String ss=(String)hm.get("sql");
				    	//if(ss==null)
						//ss=(String)this.getFormHM().get("sql");
				    	//sql=SafeCode.decode(ss);
				    	//sql = PubFunc.keyWord_reback(sql);
				    	String str=hmusterViewBo.getA0100s(sql);
				    	//hmusterViewBo.setSql(str);
				    	hmusterBo.setSql_str(str);
				    	//this.getFormHM().put("sql", SafeCode.encode(sql));
				    }
				}
				else
				{
					this.getFormHM().put("sql", "");
				}
				String tableTitleTop="";
				String tableHeader="";
				String tableBody="";
				String tableTitleBottom="";
				String turnPage="";
				ArrayList photoList0=new ArrayList();
				hmusterBo.setLayerid(layerid);
				try
				{
					//"direct":默认生成 "":通过设置生成 
					if("direct".equals(operateMethod)){//切换人员库按照默认取最后一条历史记录 不汇总
						countflag="0";
						history="1";
					}
					log.info("ExecuteHmusterTempTrans第 3 段用时:{} ms",(System.currentTimeMillis() - start));
					HmusterPdf   hmusterPdf=new HmusterPdf(this.getFrameconn());			

					HashMap cFactorMap=hmusterBo.getCfactor(tabID);
					
					if(cFactorMap.get("groupN")!=null){
						if(cFactorMap.get("multipleGroupN")!=null){
							hmusterViewBo.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));
							hmusterViewBo.setIsGroupedSerials(isGroupedSerials);
						}
					}
					String res = (String)hm.get("res"); 
					res=res!=null&&res.trim().length()>0?res:"0";
					hm.remove("res");
					if("1".equals(res)){
						historyRecord="0";
					}
					if(currpage==null|| "0".equals(currpage)||currpage.trim().length()==0)
						currpage="1";
					if("0".equals(historyRecord)&& "1".equals(currpage))
					{
						// 若是考勤模块则走该用户考勤人员库范围,其他则走正常权限范围内人员库
						ArrayList a_list="81".equals(modelFlag)?KqPrivBo.getB0110Dase(this.userView, this.frameconn):this.userView.getPrivDbList();
						if(a_list==null||a_list.size()==0)
							throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
						if("2".equals(modelFlag)){
							ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
							String dbstr=csxml.getTextValue("/Params/nbase");
							dbstr=dbstr!=null?dbstr:"";
							ArrayList list = new ArrayList();
							for(int i=0;i<a_list.size();i++){
								String pre=(String)a_list.get(i);
								if(dbstr.length()>0){
									if(dbstr.toLowerCase().indexOf(pre.toLowerCase())!=-1)
									{
										list.add(new CommonData(pre.toUpperCase(),AdminCode.getCodeName("@@", pre)));
									}
								}
							}
							if(list.size()>0) {
								dblist = list;
								addAllDBItem(dblist);
							}
						}else{
						    if("3".equals(modelFlag)&&factor!=null&&factor.trim().length()>0) {// 自动取数不处理
						        
						    }else{
    							ArrayList list = new ArrayList();
    							for(int i=0;i<a_list.size();i++){
    								String pre=(String)a_list.get(i);
    								list.add(new CommonData(pre.toUpperCase(),AdminCode.getCodeName("@@", pre)));
    							}
    							if(list.size()>0) {
    								dblist = list;
    								addAllDBItem(dblist);
    							}
						    }
						}
						
					}
					
					/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
					infor_Flag = "1";
					if("21".equals(modelFlag)){
						infor_Flag = "2";
					}else if("41".equals(modelFlag)) {
						infor_Flag = "3";	
					}
					//add by wangchaoqun on 2014-9-29 当为基准岗位花名册时，infoflag为5
					else if("51".equals(modelFlag)) {
						infor_Flag = "5";
					}
					
					/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
					/**如果定义条件，自动重新取数*/
					if((factor!=null&&factor.trim().length()>0 || mainParamCond!=null&&mainParamCond.length()>0)
					        /*&&currpage.equals("1")*/&& "1".equals(isGetData))
					{
						historyRecord="0";
					    /**取子集记录方式：0当前记录(默认值),1某次历史记录,2根据条件取历史记录*/
						String hismode=hmxml.getValue(HmusterXML.HISTORYMODE);
						if(hismode==null) hismode="";
						if("1".equals(hismode)){
							history="3";
							flag="4";
						}else if("0".equals(hismode)){
							history="1";
							flag="1";
						}else{
							history=hismode;
							flag="2";
						}
						year=hmxml.getValue(HmusterXML.YEAR);
						month=hmxml.getValue(HmusterXML.MONTH);
						count=hmxml.getValue(HmusterXML.TIMES);
						/**<!--子集历史记录条件,空表示所有子集记录 -->
						 * <SUBSETID>子集id</SUBSETID><SUBFACTOR>因子A0405<>,A0405<=21</SUBFACTOR><SUBEXPR>表达式1*2</SUBEXPR>
						 */
						String subset=hmxml.getValue(HmusterXML.SUBSETID);
						String subfactor=hmxml.getValue(HmusterXML.SUBFACTOR);
						String subexpr=hmxml.getValue(HmusterXML.SUBEXPR);
						/**>模糊查询True|False*/
						String FUZZYFLAG=hmxml.getValue(HmusterXML.FUZZYFLAG);
						//取部分历史记录时 表达式与因子表达式为空时校验
						if("2".equals(hismode)&&StringUtils.isNotEmpty(subexpr)&&StringUtils.isNotEmpty(subfactor))
						{
					    	fromScopt=subexpr+"::"+subfactor.replaceAll(",", "`")+"::"+("TRUE".equals(FUZZYFLAG)?"1":"0");	//history=2 1*2::a58z0=`a58z1=`::0
					    	toScope=subset;	//A58
					    	selectedPoint=subset;	//a58
						}
						/**每页行数(0: 自动计算, 1: 用户指定)*/
						isAutoCount=hmxml.getValue(HmusterXML.ROWCOUNTMODE);
						/**用户指定行数*/
						pageRows=hmxml.getValue(HmusterXML.ROWCOUNT);
						
						/**对历史记录查询True|False*/
						String HIS=hmxml.getValue(HmusterXML.HIS);
						/**查机构库时，仅查部门True|False*/
						String DEPTONLY=hmxml.getValue(HmusterXML.DEPTONLY);
						/**查机构库时，仅查单位True|False*/
						String UNITONLY=hmxml.getValue(HmusterXML.UNITONLY);
						/**0不汇总,1按人员/单位/职位汇总*/
						countflag=hmxml.getValue(HmusterXML.NEEDSUM);
						/**USR,RET(人员库，逗号分隔，空表示全部人员库)*/
						String nbase=hmxml.getValue(HmusterXML.NBASE);
						if(nbase!=null&&nbase.length()>0)
						{
							ArrayList list = userView.getPrivDbList();
			    			StringBuffer temp=new StringBuffer("");
			    			for(int i=0;i<list.size();i++)
			    			{
			    				temp.append(",");
			    				temp.append((String)list.get(i));
			    			}
			    			temp.append(",");
							String[] tmp_arr=nbase.split(",");
							dblist.clear();
							int j=0;
							for(int i=0;i<tmp_arr.length;i++)
							{
								if(tmp_arr[i]==null|| "".equals(tmp_arr[i]))
									continue;
								if(temp.toString().toUpperCase().indexOf((","+tmp_arr[i].toUpperCase()+","))==-1)
									continue;
//								if(changeDbpre!=null&&changeDbpre.equals("1")&&j==0)
//								{
//							    	dbpre=tmp_arr[i];
//								}
								j++;
								dblist.add(new CommonData(tmp_arr[i].toUpperCase(),AdminCode.getCodeName("@@", tmp_arr[i])));
							}
							addAllDBItem(dblist);
							if(dbpre.length()>0 && !dbExists(dbpre.toUpperCase(), dblist))
							    changeDbpre = "1";  // 人员库无效时自动设置(Bug0042342)
                            if(changeDbpre!=null&& "1".equals(changeDbpre))
                            {
                                if(dblist.size()>0){
                                    dbpre=((CommonData)dblist.get(0)).getDataValue();
                                }
                            }
						}
						queryScope="0";
						if(factor!=null&&factor.trim().length()>0){
							//23796  员工管理 高级花名册 点击5号花名册 后台报错 start 改用dblist的dbname dblist存储的库关联到用户权限范围内的库 changxy 20161025
								nbase="";
								for (int j = 0; j < dblist.size(); j++) {
									String dbpers = ((CommonData)dblist.get(j)).getDataValue();
									if("All".equalsIgnoreCase(dbpers))
										continue;
									if(j<dblist.size()-1)
										nbase+=dbpers+",";
									else
										nbase+=dbpers;	
								}
								//23796  员工管理 高级花名册 点击5号花名册 后台报错 end	
							hmusterBo.getFactorSQL(nbase, FUZZYFLAG, HIS, DEPTONLY, UNITONLY, factor, expr, infor_Flag, 
									userView,dbpre,NO_MANAGE_PRIV,temptable);
						}
						else{
	                       if("3".equals(modelFlag)&& "ALL".equals(dbpre))
	                           musterbo.runAllDBCondTable(conditionBase,"", dblist, infor_Flag);
	                       else
	                           musterbo.runCondTable(conditionBase,"", dbpre, infor_Flag);
	                       queryScope="1";
		                }
						/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
						isAutoImport = true;
						//condlist = new ArrayList();
						conditionBase = "";
						/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
					}
					
					
					if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)|| "51".equals(modelFlag)/*基准岗位*/)
					{
						/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 start */
						if(hm.containsKey("linktype")) {
							hmusterViewBo.setLinktype((String)hm.get("linktype"));
						}
						/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 end */
						
						/*condlist = musterbo.getCondList(infor_Flag, this.userView);
						condlist.add(0,new CommonData("","全部"));
						if(!musterbo.condExists(conditionBase, condlist)) {
							conditionBase = "";
							this.getFormHM().put("conditionBase", conditionBase);
						}*/
						
						if(StringUtils.isNotEmpty(mainParamCond)) {
							// 设置的常用查询id
						    conditionBase = (String)this.getFormHM().get("conditionBase");
						    conditionBase = conditionBase==null?"":conditionBase;
							// 得到业务用户设置的常用查询
							List mainParamCondList = musterbo.getCondList(infor_Flag, mainParamCond);
							if(!musterbo.condExists(conditionBase, mainParamCondList))
							    conditionBase = "";
							this.getFormHM().put("mainParamCondList", mainParamCondList);
							this.getFormHM().put("mainParamTitle", mainParamTitle);
                            if(StringUtils.isEmpty(conditionBase)) {
                                CommonData data = (CommonData) mainParamCondList.get(0);
                                conditionBase = data.getDataValue();
                            }
						}
					}
					if(dbpre.trim().length()<1&&dblist.size()>0){
						dbpre=((CommonData)dblist.get(0)).getDataValue();
					}
					hmusterBo.setCountflag(countflag);
					if("81".equals(modelFlag))//考勤查询条件语句
					{
						String kqtable=(String)this.getFormHM().get("kqtable");
						if(kqtable!=null&&kqtable.length()>0)
						{
							hmusterBo.setKqtable(kqtable);
							this.getFormHM().put("kqtable",kqtable);
						}
						else{
							this.getFormHM().put("kqtable", "");
						}
						hmusterBo.getKQConditionSQL(fromScopt, toScope, selectedPoint);
						hmusterBo.setKqMap(this.getFormHM());
					}
					hmusterBo.setFlag(flag);
					if("2".equals(modelFlag)|| "3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)
							|| "51".equals(modelFlag)|| "1".equals(modelFlag)|| "4".equals(modelFlag))
					{   //对人员管理、职位管理、机构管理进行操作
						/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
						/*if(condlist.size() > 1) { // 防止查询条件挡住花名册，花名册向下偏移
							hmusterViewBo.setDeltaTop(hmusterViewBo.getDeltaTop() + 15);
						}*/ 
						/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
						
						/* 删除上页生成的临时照片  */
						if(photoList!=null&&photoList.size()>1)
							hmusterPdf.deletePicture(photoList);
						
						String state="1";
						DbWizard dbWizard = new DbWizard(this.frameconn);
                        hmusterBo.getMidvariable(tabID);
						if (dbWizard.isExistTable(temptable, false)){
							if("0".equals(historyRecord)&& "1".equals(currpage))
							{
								/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
								if(!isAutoImport&&StringUtils.isNotEmpty(mainParamCond)&& "1".equals(isGetData)) {
							        if("3".equals(modelFlag)&& "ALL".equals(dbpre))
							            musterbo.runAllDBCondTable(conditionBase,"", dblist, infor_Flag);
							        else
							            musterbo.runCondTable(conditionBase,"", dbpre, infor_Flag);
							    }
								/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
								hmusterBo.setGroupPointItem(groupPoint);
								hmusterBo.setSortitem(sortitem);
								if(("3".equals(modelFlag)|| "2".equals(modelFlag))&& "ALL".equals(dbpre))// changxy 20160903 [22142]放开合同管理
                                    state=hmusterBo.importAllDBData(history,user,temptable,tabID,infor_Flag,dblist,queryScope,flag,year,month,count,fromScopt,
                                            toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
                                            modelFlag); 
								else
								    state=hmusterBo.importData(history,user,temptable,tabID,infor_Flag,dbpre,queryScope,flag,year,month,count,fromScopt,
										toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
										modelFlag);	
							}
						}else{
							/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
							if(!isAutoImport&&StringUtils.isNotEmpty(mainParamCond)&& "1".equals(isGetData)) {
						        if("3".equals(modelFlag)&& "ALL".equals(dbpre))
		                            musterbo.runAllDBCondTable(conditionBase,"", dblist, infor_Flag);
		                        else
		                            musterbo.runCondTable(conditionBase,"", dbpre, infor_Flag);
						    }
							/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
							log.info("ExecuteHmusterTempTrans第 4 段用时:{} ms",(System.currentTimeMillis() - start));
							hmusterBo.setGroupPointItem(groupPoint);
							hmusterBo.setSortitem(sortitem);
                            if("3".equals(modelFlag)&& "ALL".equals(dbpre))
                                state=hmusterBo.importAllDBData(history,user,temptable,tabID,infor_Flag,dblist,queryScope,flag,year,month,count,fromScopt,
                                        toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
                                        modelFlag); 
                            else
                                state=hmusterBo.importData(history,user,temptable,tabID,infor_Flag,dbpre,queryScope,flag,year,month,count,
									fromScopt,toScope,selectedPoint,isGroupPoint,groupPoint,
									this.getUserView(),modelFlag);		
						}
						/**目的是为了取上回取数的那个库，可能用户只关心这个库，所以做此*/
						if("3".equals(modelFlag)&&"1".equals(changeDbpre)){
							String dbprename = hmusterBo.dbPre(temptable);
							dbprename=dbprename!=null?dbprename:"";
							if(dbprename.trim().length()>0){//有数据
							    // 超过一个，显示全部人员库
							    int cnt=hmusterBo.dbPreCount(temptable);
							    if(cnt>1&&dbExists("ALL", dblist))
							        dbpre = "ALL";
							    else
								    dbpre = dbprename;
							}else{
//                                if(dbExists("ALL", dblist))  // 没数据,默认全部
//                                    dbpre = "ALL";
							    
								/*hmusterBo.setGroupPointItem(groupPoint);
								hmusterBo.setSortitem(sortitem);
								state=hmusterBo.importData(history,user,temptable,tabID,infor_Flag,dbpre,queryScope,flag,year,month,count,
										fromScopt,toScope,selectedPoint,isGroupPoint,groupPoint,
										this.getUserView(),modelFlag);	*/	
							}
						}
						hmusterViewBo.getResourceCloumn(tabID, infor_Flag);
						if(selectedPoint!=null)
						{
							String[] temp = selectedPoint.split("/");
							if(temp!=null&&temp.length>1){
								if ("D".equals(temp[2])) {
									hmusterViewBo.setYearMont(hmusterViewBo.parseSelectPoint(toScope, fromScopt));
								}
							}
						}
						ArrayList list=hmusterViewBo.getHumster(infor_Flag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
								,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,dbpre,history,year,month,count,userView,operateMethod,printGrid,modelFlag);
						this.getFormHM().put("paperRows", String.valueOf(hmusterViewBo.getPageRows()));
						
						//hmusterViewBo=null;
						
						tableTitleTop=(String)list.get(0);
						tableHeader=(String)list.get(1);
						tableBody=(String)list.get(2);
						tableTitleBottom=(String)list.get(3);
						turnPage=(String)list.get(4);
						photoList0=(ArrayList)list.get(5);
						groupPointList=getHmusterGroupPointList(infor_Flag);
					}else if("15".equals(modelFlag))  //stipend:个税管理花名册taxarchive
					{
						String conSQL=(String)this.getFormHM().get("conSQL");
						conSQL=PubFunc.keyWord_reback(conSQL);
						String startime=(String)this.getFormHM().get("startime");
						startime=startime!=null?startime:"";
						String fromTable=(String)this.getFormHM().get("fromtable");
						int ver=this.userView.getVersion(); //锁版本校验 
						if(ver>=70)
							fromTable=PubFunc.decrypt(fromTable);//解密前台获取的表名   xus 20170214
						String endtime=(String)this.getFormHM().get("endtime");
						endtime=endtime!=null?endtime:"";
						
						String a_code=(String)hm.get("a_code");
						a_code=a_code!=null?a_code:"";
						hm.remove("a_code");
						if(!this.userView.isSuper_admin()){
							if(a_code.trim().length()<1){
								a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
							}
						}else{
							if(a_code.trim().length()<1){
								a_code="UN";
							}
						}
						this.getFormHM().put("conSQL", conSQL);
						String salaryid=(String)hm.get("salaryid");
						salaryid=salaryid!=null?salaryid:"";
						hm.remove("salaryid");
						
						String salarydate=getFilterCond(startime,endtime);
						
						StipendHmusterBo stipendHmusterBo=new StipendHmusterBo(this.getFrameconn(),this.userView,salaryid);
						stipendHmusterBo.setLayerid(layerid);
						stipendHmusterBo.setSelfMusterName(tabID);
						stipendHmusterBo.setModuleSQL(stipendHmusterBo.getPrivPre(filterByMdule, dbpre));
						if(currpage==null|| "0".equals(currpage)||currpage.trim().length()==0)
							currpage="1";
						 combineField=(String)this.getFormHM().get("combineField");
						if(combineField==null|| "".equals(combineField))
							combineField="1";//不汇总
						//ArrayList combineList = this.getCombineList();
						stipendHmusterBo.getMidvariable(tabID);
						if("0".equals(historyRecord)&& "1".equals(currpage))
						{
							stipendHmusterBo.setSortitem(sortitem);
							stipendHmusterBo.setGroupPoint(groupPoint);
							stipendHmusterBo.setIsGroupPoint(isGroupPoint);
							stipendHmusterBo.setIsGroupPoint2(isGroupPoint2);
							stipendHmusterBo.setGroupPoint2(groupPoint2);
							stipendHmusterBo.setLayerid2(layerid2);
							//stipendHmusterBo.getPrivSQLStr(modelFlag, this.userView, dbpre, "a");
							stipendHmusterBo.setCombineField(combineField);
							stipendHmusterBo.setTaxTable(fromTable);
							stipendHmusterBo.setConSQL(conSQL==null?"":conSQL.toUpperCase().replaceAll(fromTable.toUpperCase()+".", ""));
							stipendHmusterBo.importSpData(tabID,a_code,salarydate,dbpre);
						}
						hmusterViewBo.getResourceCloumn(tabID, "salary");
						ArrayList list=hmusterViewBo.getHumster("salary",tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage,
								isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,dbpre,"0","","",count,userView,"direct",printGrid,"salary");
						tableTitleTop=(String)list.get(0);
						tableHeader=(String)list.get(1);
						tableBody=(String)list.get(2);
						tableTitleBottom=(String)list.get(3);
						turnPage=(String)list.get(4);
						photoList0=(ArrayList)list.get(5);
						groupPointList=getHmusterGroupPointList();
						//this.getFormHM().put("combineFieldList", combineList);
						this.getFormHM().put("combineField", combineField);
						this.getFormHM().put("startime",startime);
						this.getFormHM().put("endtime",endtime);
					}else {
						if(currpage==null|| "0".equals(currpage)||currpage.trim().length()==0)
							currpage="1";
						DbWizard dbWizard = new DbWizard(this.frameconn);
						hmusterBo.getMidvariable(tabID);
						if (dbWizard.isExistTable(temptable, false)){
							/**人事异动进入花名册，直接取数，去掉页面的取数按钮*/
							if("5".equals(modelFlag))
							{
								if(!"-1".equals(relatTableid))
								{
									String spflag = (String)this.getFormHM().get("spflag");
				    		    	hmusterBo.setSortitem(sortitem);
				    		    	if(spflag!=null&& "2".equals(spflag)){
					    	            hmusterBo.setGroupPointItem(groupPoint);
					    	    		hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid,spflag);
					        		}else{
					    	    		hmusterBo.setGroupPointItem(groupPoint);
					    		    	hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid);
				    	    		}
								}
							}
							else
							{
			     	     		if(("0".equals(historyRecord)&& "1".equals(currpage)))
			    		    	{
			    			    	String spflag = (String)this.getFormHM().get("spflag");
				    		    	hmusterBo.setSortitem(sortitem);
				    		    	if(spflag!=null&& "2".equals(spflag)){
					    	            hmusterBo.setGroupPointItem(groupPoint);
					    	    		hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid,spflag);
					        		}else{
					    	    		hmusterBo.setGroupPointItem(groupPoint);
					    		    	hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid);
				    	    		}
			    		    	}
			    			}
						}else
						{
							String spflag = (String)this.getFormHM().get("spflag");
			    			hmusterBo.setSortitem(sortitem);
			    			if(spflag!=null&& "2".equals(spflag)){
				    			hmusterBo.setGroupPointItem(groupPoint);
				    			hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid,spflag);
				    		}else{
				    			hmusterBo.setGroupPointItem(groupPoint);
				    			hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid);
			    			}
						}
						
						if(cFactorMap.get("groupN")!=null)
						{
							isGroupPoint="1";								//是否分组
							groupPoint=(String)cFactorMap.get("groupN");     //分组指标
						}
						if("81".equals(modelFlag)|| "5".equals(modelFlag))
						{
							history="0";
							year=""; month="";count="";operateMethod="next";
						}
						// 34150 考勤高级花名册需回显所选人员库，薪资的单独处理
						if("5".equals(modelFlag))
						{
							dbpre="";
						}
						hmusterViewBo.getResourceCloumn(tabID, modelFlag);
						ArrayList list=hmusterViewBo.getHumster(modelFlag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
								,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,dbpre,history,year,month,count,userView,operateMethod,printGrid,modelFlag);
						this.getFormHM().put("paperRows", String.valueOf(hmusterViewBo.getPageRows()));

						tableTitleTop=(String)list.get(0);
						tableHeader=(String)list.get(1);
						tableBody=(String)list.get(2);
						tableTitleBottom=(String)list.get(3);
						turnPage=(String)list.get(4);
						photoList0=(ArrayList)list.get(5);
						groupPointList=getHmusterGroupPointList(infor_Flag);
						//hmusterViewBo=null;
						
					}
					this.getFormHM().put("tableHeader",tableHeader.replace("\n", ""));//员工管理采用ext展现页面 html代码带引号转为双引号 换行符替换为空 
					this.getFormHM().put("tableBody",tableBody.replace("\n", "")/*.replace("'","\"").replace("\n", "")*/);
					this.getFormHM().put("tableTitleTop",tableTitleTop.replace("\n", "")); //jsp编码集为GBK 之前转为gb2312会导致有些字乱码 changxy 20160903
					this.getFormHM().put("tableTitleBottom",tableTitleBottom.replace("\n", ""));
					this.getFormHM().put("turnPage",turnPage.replace("'","\"").replace("\n", ""));
					this.getFormHM().put("photoList",photoList0);    
					this.getFormHM().put("checkflag",checkflag); 
					this.getFormHM().put("inforkind",infor_Flag);
					this.getFormHM().put("groupPointList",groupPointList);
					this.getFormHM().put("dbpre",dbpre.toUpperCase());
					this.getFormHM().put("dblist",dblist);
					
					/**  由于form属性为session，所以清空属性值    **/		
					if(hm.get("clears")!=null&& "1".equals((String)hm.get("clears")))
					{
						this.getFormHM().put("clears","1");
						hm.remove("clears");
					}
				}catch(Exception e){
					if(!"-1".equals(tabID)){
						try{
							UsrResultTable resulttable = new UsrResultTable();
							if(resulttable.isNumber(this.userView.getUserName())){
								throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.hroster")));
							}
							if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)
									|| "1".equals(modelFlag)|| "4".equals(modelFlag))//对人员管理、职位管理、机构管理进行操作
							{
								/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
								infor_Flag = "1";
								if("21".equals(modelFlag))
								    infor_Flag = "2";
								else if("41".equals(modelFlag))
								    infor_Flag = "3";	
								/*condlist = musterbo.getCondList(infor_Flag, this.userView);
								condlist.add(0,new CommonData("","全部"));
								if(!musterbo.condExists(conditionBase, condlist)) {
									conditionBase = "";
									this.getFormHM().put("conditionBase", conditionBase);
								}*/
								
								if(StringUtils.isNotEmpty(mainParamCond)) {
									// 设置的常用查询id
								    conditionBase = (String)this.getFormHM().get("conditionBase");
								    conditionBase = conditionBase==null?"":conditionBase;
									// 得到业务用户设置的常用查询
									List mainParamCondList = musterbo.getCondList(infor_Flag, mainParamCond);
		                            if(!musterbo.condExists(conditionBase, mainParamCondList))
			                            conditionBase = "";
									this.getFormHM().put("mainParamCondList", mainParamCondList);
									this.getFormHM().put("mainParamTitle", mainParamTitle);
									/* 问题修正：如果自定义了参数，那么默认选择第一个为查询条件 xiaoyun 2014-8-29 start */
									if(StringUtils.isEmpty(conditionBase)) {
										CommonData data = (CommonData) mainParamCondList.get(0);
										conditionBase = data.getDataValue();
									}
									/* 问题修正：如果自定义了参数，那么默认选择第一个为查询条件 xiaoyun 2014-8-29 end */
									musterbo.runCondTable(conditionBase,"", dbpre, infor_Flag);
								}
								
								/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
								
								hmusterBo.getMidvariable(tabID);
								hmusterBo.setGroupPointItem(groupPoint);
								hmusterBo.setSortitem(sortitem);
								hmusterBo.importData(history,user,temptable,tabID,
									infor_Flag,dbpre,queryScope,flag,year,month,count,fromScopt,
									toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
									modelFlag);
								hmusterViewBo.getResourceCloumn(tabID, infor_Flag);
								ArrayList list=hmusterViewBo.getHumster(infor_Flag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
										,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,dbpre,history,year,month,count,userView,operateMethod,printGrid,modelFlag);
								this.getFormHM().put("paperRows", String.valueOf(hmusterViewBo.getPageRows()));
								
								/* 任务：3692 哈药集团 如果只有一页，那么不显示分页标签 xiaoyun 2014-8-16 start */
								//hmusterViewBo=null;
								/* 任务：3692 哈药集团 如果只有一页，那么不显示分页标签 xiaoyun 2014-8-16 end */
								
								tableTitleTop=(String)list.get(0);
								tableHeader=(String)list.get(1);
								tableBody=(String)list.get(2);
								tableTitleBottom=(String)list.get(3);
								turnPage=(String)list.get(4);
								/* 任务：3692 哈药集团 如果只有一页，那么不显示分页标签 xiaoyun 2014-8-16 start */
								int totalPage = hmusterViewBo.getTotalPage();
								if(totalPage <= 1) {
									turnPage = "";
								}
								hmusterViewBo=null;
								/* 任务：3692 哈药集团 如果只有一页，那么不显示分页标签 xiaoyun 2014-8-16 end */
								photoList0=(ArrayList)list.get(5);
								groupPointList=getHmusterGroupPointList(infor_Flag);
							}else if("15".equals(modelFlag))  //stipend:员工薪酬-花名册
							{
								String startime=(String)this.getFormHM().get("startime");
								startime=startime!=null?startime:"";
								
								String endtime=(String)this.getFormHM().get("endtime");
								endtime=endtime!=null?endtime:"";
								
								String a_code=(String)hm.get("a_code");
								a_code=a_code!=null?a_code:"";
								hm.remove("a_code");
								if(!this.userView.isSuper_admin()){
									if(a_code.trim().length()<1){
										a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
									}
								}else{
									if(a_code.trim().length()<1){
										a_code="UN";
									}
								}
								
								
								String salaryid=(String)hm.get("salaryid");
								salaryid=salaryid!=null?salaryid:"";
								hm.remove("salaryid");
								
								String salarydate=(String)hm.get("salarydate");
								salarydate=salarydate!=null?salarydate:"";
								hm.remove("salarydate");
								salarydate=getFilterCond(startime,endtime);
								StipendHmusterBo stipendHmusterBo=new StipendHmusterBo(this.getFrameconn(),this.userView,salaryid);
								stipendHmusterBo.setSelfMusterName(tabID);
								stipendHmusterBo.setModuleSQL(stipendHmusterBo.getPrivPre(filterByMdule, dbpre));
								stipendHmusterBo.getMidvariable(tabID);
								stipendHmusterBo.importSpData(tabID,a_code,salarydate,dbpre);
								hmusterViewBo.getResourceCloumn(tabID, "salary");
								ArrayList list=hmusterViewBo.getHumster("salary",tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
										,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,dbpre,"0","","",count,userView,"direct",printGrid,"salary");
								
								tableTitleTop=(String)list.get(0);
								tableHeader=(String)list.get(1);
								tableBody=(String)list.get(2);
								tableTitleBottom=(String)list.get(3);
								turnPage=(String)list.get(4);
								photoList0=(ArrayList)list.get(5);
								groupPointList=getHmusterGroupPointList();
								
								this.getFormHM().put("startime",startime);
								this.getFormHM().put("endtime",endtime);
								
							}else{
								String spflag = (String)this.getFormHM().get("spflag");
								hmusterBo.getMidvariable(tabID);
								if(spflag!=null&& "2".equals(spflag)){
									hmusterBo.setGroupPointItem(groupPoint);
									hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid,spflag);
								}else{
									hmusterBo.setGroupPointItem(groupPoint);
									hmusterBo.importData(modelFlag,user,tabID,dbpre,condition,this.getUserView(),relatTableid);
								}
								hmusterViewBo.setSql("");
								hmusterViewBo.getResourceCloumn(tabID, infor_Flag);
								ArrayList list=hmusterViewBo.getHumster(modelFlag,tabID,isGroupPoint,groupPoint,temptable,pageRows,currpage
										,isAutoCount,zeroPrint,emptyRow,column,pix,columnLine,username,dbpre,history,year,month,count,userView,operateMethod,printGrid,modelFlag);
								this.getFormHM().put("paperRows", String.valueOf(hmusterViewBo.getPageRows()));
								hmusterViewBo=null;
								
								tableTitleTop=(String)list.get(0);
								tableHeader=(String)list.get(1);
								tableBody=(String)list.get(2);
								tableTitleBottom=(String)list.get(3);
								turnPage=(String)list.get(4);
								photoList0=(ArrayList)list.get(5);
								groupPointList=getHmusterGroupPointList(infor_Flag);
								
							}
							
							this.getFormHM().put("tableHeader",tableHeader);
							this.getFormHM().put("tableBody",tableBody);
							this.getFormHM().put("tableTitleTop",tableTitleTop); //jsp编码集为GBK 之前转为gb2312会导致有些字乱码 changxy 20160903
							this.getFormHM().put("tableTitleBottom",tableTitleBottom);
							this.getFormHM().put("turnPage",turnPage);
							this.getFormHM().put("photoList",photoList0);    
							this.getFormHM().put("checkflag",checkflag); 
							this.getFormHM().put("inforkind",infor_Flag);
							this.getFormHM().put("groupPointList",groupPointList);
							this.getFormHM().put("dbpre",dbpre);
							this.getFormHM().put("dblist",dblist);
							
							/**  由于form属性为session，所以清空属性值    **/		
							if(hm.get("clears")!=null&& "1".equals((String)hm.get("clears")))
							{
								this.getFormHM().put("clears","1");
								hm.remove("clears");
							}
						}catch(Exception ex){
							ex.printStackTrace();
							throw GeneralExceptionHandler.Handle(ex);
						}
						hmusterBo=null;
					}else{
						throw GeneralExceptionHandler.Handle(e);
					}
				}
				/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 start */
				//this.getFormHM().put("conditionslist", condlist);
				/* 任务：3692 哈药集团 高级花名册修改为与首页进入的显示效果一致 xiaoyun 2014-8-16 end */
				
				double nn=hmusterViewBo.getDivHeight();
				double mm=hmusterViewBo.getDivWidth();
				this.getFormHM().put("divHeight", hmusterViewBo.getDivHeight()+"");
				this.getFormHM().put("divWidth", hmusterViewBo.getDivWidth()+"");
			}
			
			this.getFormHM().put("modelFlag",modelFlag);
			this.getFormHM().put("historyRecord","0");
			this.getFormHM().put("relatTableid",relatTableid);
			this.getFormHM().put("tabID",tabID);
			this.getFormHM().put("infor_Flag",infor_Flag);
			this.getFormHM().put("queryScope",queryScope);
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("history",history);
			this.getFormHM().put("countflag",countflag);
			this.getFormHM().put("operateMethod",operateMethod);
			this.getFormHM().put("printGrid",printGrid);
			this.getFormHM().put("photoList",photoList);
			this.getFormHM().put("currpage",currpage);
			this.getFormHM().put("isGroupPoint",isGroupPoint);
			this.getFormHM().put("emptyRow",emptyRow);
			this.getFormHM().put("layerid",layerid);
			this.getFormHM().put("groupOrgCodeSet",groupOrgCodeSet);
			this.getFormHM().put("groupOrgCodeSet2",groupOrgCodeSet2);
			this.getFormHM().put("columnLine",columnLine);
			this.getFormHM().put("pix",pix);
			this.getFormHM().put("column",column);
			this.getFormHM().put("dataarea",dataarea);
			this.getFormHM().put("zeroPrint",zeroPrint);
			this.getFormHM().put("year",year);
			this.getFormHM().put("month",month);
			this.getFormHM().put("count",count);
			this.getFormHM().put("fromScopt",fromScopt);
			this.getFormHM().put("toScope",toScope);
			this.getFormHM().put("selectedPoint",selectedPoint);
			this.getFormHM().put("isAutoCount",isAutoCount);
            this.getFormHM().put("isReData", isGetData);
			this.getFormHM().put("groupPoint",groupPoint);
			this.getFormHM().put("filterByMdule", filterByMdule);
			this.getFormHM().put("historyFlag", historyFlag);
			this.getFormHM().put("dateStart", dateStart);
			this.getFormHM().put("dateEnd",dateEnd);
			this.getFormHM().put("isPrint", isPrint);
			log.info("ExecuteHmusterTempTrans第 4 段用时:{} ms",(System.currentTimeMillis() - start));
//			System.gc();
		}
        catch(NullPointerException ex)
        {
            ex.printStackTrace();
            throw new GeneralException("", "加载数据失败，请重试。", "", "");
        }
		catch(Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 增加全部人员库选项
	 * @Title: addAllDBItem   
	 * @Description:    
	 * @param dblist
	 */
	private void addAllDBItem(ArrayList dblist) {
	    if(dblist.size()>1) {
	        dblist.add(0, new CommonData("ALL", "全部人员库"));
	    }	        
	}

	/**
	 * 
	 */
    private boolean dbExists(String dbpre, ArrayList dblist) {
        if(dblist.size()>1&&dbpre!=null) {
            for(int i=0; i<dblist.size(); i++){
                if(dbpre.equals(((CommonData)dblist.get(i)).getDataValue())) {
                    return true;
                }
            }
        }
        return false;
    }
	
	/**
	 * 取得高级花名册中分组指标列表 ( 主集里的代码型指标;对人员信息，单位，职位是硬编码;对单位，B0110硬编码;对职位，E01A1硬编码)
	 * 
	 * @param inforkind
	 * @author dengc
	 * @return ArrayList created: 2006/03/21
	 */

	public ArrayList getHmusterGroupPointList(String inforkind)
			throws GeneralException {

		ArrayList arrayList = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		ArrayList pointList = new ArrayList(); // 指标列表
		String mainSet = ""; // 主集
		if ("1".equals(inforkind)) // 人员库
		{
			mainSet = "A01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory.getProperty("tree.kkroot.kkdesc"));
			arrayList.add(dataobj2);
		} else if ("3".equals(inforkind)) // 职位库
		{
			mainSet = "K01";
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory.getProperty("tree.kkroot.kkdesc"));
			//
			CommonData dataobj = new CommonData("E0122", ResourceFactory.getProperty("column.sys.dept"));
			
			arrayList.add(dataobj2);
			arrayList.add(dataobj);
			
		} else if ("2".equals(inforkind)) // 单位库
		{
			mainSet = "B01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
		} else if ("5".equals(inforkind)) // 基准岗位
		{
			mainSet = "H01";
			CommonData dataobj = new CommonData("H0100", ResourceFactory.getProperty("h0100.label"));
			arrayList.add(dataobj);
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet recset = null;
		try {
			strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
			strsql.append(mainSet); 
			strsql.append("' and (codesetid!='0'  ");
			if("1".equals(inforkind))
				strsql.append(" or UPPER(itemid)='A0101'");
			strsql.append(")");
			strsql.append(" and useflag='1' order by  displayid ");
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				CommonData dataobj = new CommonData(recset.getString("itemid"),recset.getString("itemdesc"));
				arrayList.add(dataobj);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		return arrayList;

	}
	
	/***
	 * 校验模板是否存在
	 * */
	private boolean isExitsTableId(String tabid) {
		
		ContentDAO dao=new ContentDAO(frameconn);
		RowSet rs=null;
		try {
			rs=dao.search("select Tabid from Muster_Name where Tabid=? ",Arrays.asList(tabid));
			if(rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return false;
	}
	
	private ArrayList getHmusterGroupPointList(){
		ArrayList list = new ArrayList();
		TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
		/**取得个税明细表结构字段列表*/
		ArrayList fieldlist = taxbo.getFieldlist();
		CommonData dataobj = null;
		for(int i=0;i<fieldlist.size();i++){
			Field field = (Field)fieldlist.get(i);
			String itemid = field.getName();
			String itendesc = field.getLabel();
			if(!("Tax_max_id".equalsIgnoreCase(itemid) || "A0100".equalsIgnoreCase(itemid)  ) ){
				if(field.isCode()){
					dataobj = new CommonData(itemid,itendesc);
					list.add(dataobj);
				}else if("A0101".equalsIgnoreCase(itemid)) {
					dataobj = new CommonData(itemid,itendesc);
					list.add(dataobj);
				}
			}
		}
		
		return list;
	}
	/**
	 * 取得报税时间过滤条件
	 * @param declaredate
	 * @return
	 */
	private String getFilterCond(String startime,String endtime)
	{
		StringBuffer buf=new StringBuffer();
		if(startime!=null&&startime.trim().length()>0&&endtime!=null&&endtime.trim().length()>0){
			WorkdiarySQLStr wss=new WorkdiarySQLStr();
			String tempstart=wss.getDataValue("declare_tax",">=",startime);
			String tempend=wss.getDataValue("declare_tax","<=",endtime);
			buf.append(tempstart);
			buf.append(" and ");
			buf.append(tempend);
		}else{
			buf.append("");
		}
		return buf.toString();
	}
	
}
