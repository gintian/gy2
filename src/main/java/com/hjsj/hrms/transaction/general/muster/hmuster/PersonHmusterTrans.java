package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterPdf;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterViewBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PersonHmusterTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5084102460595628256L;

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		/** 哈药领导桌面 按钮（高级花名册页面的“取数”、“输出Excel”、“输出PDF”、“打印预演”等按钮）是否显示开关：1显示按钮(默认值)，0不显示 xiaoyun 2014-8-13 start */
		String showbuttons = "";
		if(hm.get("showbuttons") != null) {
			showbuttons = (String)hm.get("showbuttons");
			this.getFormHM().put("showbuttons", hm.get("showbuttons"));
			hm.remove("showbuttons");
		}else {
			if(this.getFormHM().get("showbuttons") != null) {
				showbuttons =  (String)this.getFormHM().get("showbuttons");
				this.getFormHM().put("showbuttons", (String)this.getFormHM().get("showbuttons"));
			}else {
				this.getFormHM().put("showbuttons", null);
			}
		}		
		/** 哈药领导桌面 按钮（高级花名册页面的“取数”、“输出Excel”、“输出PDF”、“打印预演”等按钮）是否显示开关：1显示按钮(默认值)，0不显示 xiaoyun 2014-8-13 end */
		
		String modelFlag = "3";
		if(hm.get("modelFlag")!=null)
		    modelFlag = (String)hm.get("modelFlag");
		this.getFormHM().put("modelFlag", modelFlag);
        String returnType=(String)this.getFormHM().get("returnType");
		String user = this.userView.getUserName().trim();
		String changeDbpre=(String)hm.get("changeDbpre");
		changeDbpre=changeDbpre==null?"":changeDbpre;
		hm.remove("changeDbpre");
		String tabID = (String) this.getFormHM().get("tabID");
		tabID = tabID != null && tabID.trim().length() > 0 ? tabID : "-1";
		
		if("-1".equals(tabID)){
			tabID=(String)hm.get("tabid");
			tabID = tabID != null && tabID.trim().length() > 0 ? tabID : "-1";
			hm.remove("tabid");
		}
		
		//检查高级花名册权限/检查常用花名册权限
		if (!"-1".equals(tabID) && !this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, tabID)&& !this.userView.isHaveResource(IResourceConstant.MUSTER, tabID))
        {
            tabID = "-1";
        }
		
		String isGetData="0";
	    if(this.getFormHM().get("isReData")!=null&&!"".equals((String)this.getFormHM().get("isReData")))
	    {
	    	isGetData=(String)this.getFormHM().get("isReData");
	    }
	    if(hm.get("isGetData")!=null)
	    {
	    	isGetData=(String)hm.get("isGetData");
	    	hm.remove("isGetData");
	    }
		this.getFormHM().put("tabID", tabID);
		
		
		
		String infor_Flag = "1";
		if("21".equals(modelFlag))
		    infor_Flag = "2";
		else if("41".equals(modelFlag))
		    infor_Flag = "3";
		this.getFormHM().put("infor_Flag", infor_Flag);
		
		String condition= (String)this.getFormHM().get("condition");
		condition=condition!=null?condition:"";
		
		MusterBo musterbo = new MusterBo(this.getFrameconn(),this.userView);
		ArrayList condlist = musterbo.getCondList(infor_Flag, this.userView);
		condlist.add(0,new CommonData("","全部"));
		if(!musterbo.condExists(condition, condlist))
		    condition = "";
		this.getFormHM().put("condition", condition);
		
		String queryScope = "1";
		this.getFormHM().put("queryScope", "1");
		
		String flag = (String) this.getFormHM().get("flag");
		this.getFormHM().put("flag", flag);
		
		String history = "1";
		this.getFormHM().put("history", "1");
		
		String selectedPoint = "";
		this.getFormHM().put("selectedPoint","");
		
		
		String pageRows = (String) this.getFormHM().get("pageRows");
		pageRows = pageRows != null && pageRows.trim().length() > 0 ? pageRows: "20";
		this.getFormHM().put("pageRows", pageRows);
		String zeroPrint = (String) this.getFormHM().get("zeroPrint");
		zeroPrint = zeroPrint != null && zeroPrint.trim().length() > 0 ? zeroPrint: "0";
		this.getFormHM().put("zeroPrint", zeroPrint);

		HmusterXML hmxml = new HmusterXML(this.getFrameconn(), tabID);
		String isAutoCount = hmxml.getValue(HmusterXML.ROWCOUNTMODE);
		String rows=hmxml.getValue(HmusterXML.ROWCOUNT);
		if("0".equals(isAutoCount))
			pageRows="20";
		else{
			pageRows=rows;
		}
		this.getFormHM().put("isAutoCount",isAutoCount);
		String column = hmxml.getValue(HmusterXML.COLUMN);
		String hz = hmxml.getValue(HmusterXML.HZ);
		if ("1".equals(column))
			column = hz;
        
		this.getFormHM().put("column", column);
		String pix = hmxml.getValue(HmusterXML.PIX);
		String dataarea = hmxml.getValue(HmusterXML.DATAAREA);
		dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
		if("1".equals(dataarea)){
			column="1";
			pix="-1";
		}
		this.getFormHM().put("pix", pix);
		String columnLine = hmxml.getValue(HmusterXML.COLUMNLINE);
		this.getFormHM().put("columnLine", columnLine);
		String groupPoint = hmxml.getValue(HmusterXML.GROUPFIELD);
		this.getFormHM().put("groupPoint", groupPoint);
		String layerid = hmxml.getValue(HmusterXML.GROUPLAYER);
		this.getFormHM().put("layerid", layerid);
		String sortitem = hmxml.getValue(HmusterXML.SORTSTR);
		sortitem = sortitem != null ? sortitem : "";
		String factor = hmxml.getValue(HmusterXML.FACTOR);
		String expr=hmxml.getValue(HmusterXML.EXPR);
		/* 任务：3692 哈药集团 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
		String mainParamCond = hmxml.getValue(HmusterXML.MAINPARAMCOND);
		String mainParamTitle = hmxml.getValue(HmusterXML.MAINPARAMTITLE);
		/* 任务：3692 哈药集团 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
		String emptyRow = hmxml.getNgrid();
		this.getFormHM().put("emptyRow", emptyRow);

		String isGroupPoint = "0";
		if (groupPoint != null && groupPoint.trim().length() > 0) {
			isGroupPoint = "1";
		}
		this.getFormHM().put("isGroupPoint", isGroupPoint);

		String historyRecord = "1"; // 0:重新取数 1.上次数据

		String currpage = (String) this.getFormHM().get("currpage"); // 当前页
		currpage = currpage != null && currpage.trim().length() > 0 ? currpage: "1";
		if("1".equals(hm.get("clears")))
			currpage="0";
		this.getFormHM().put("currpage", currpage);
		ArrayList photoList = (ArrayList) this.getFormHM().get("photoList");
		this.getFormHM().put("photoList", photoList);
		String printGrid = (String) this.getFormHM().get("printGrid");
		printGrid = printGrid != null && printGrid.trim().length() > 0 ? printGrid: "1";
		this.getFormHM().put("printGrid", printGrid);
		String operateMethod = (String) hm.get("operateMethod");
		this.getFormHM().put("operateMethod", operateMethod);
		String checkflag = (String) hm.get("checkflag");
		checkflag = checkflag != null ? checkflag : "";
		hm.remove("checkflag");
		String NO_MANAGE_PRIV=hmxml.getValue(HmusterXML.NO_MANAGE_PRIV);
		HmusterBo hmusterBo = new HmusterBo(this.getFrameconn());
		ArrayList groupPointList = new ArrayList();
		hmusterBo.setNo_manger_priv(NO_MANAGE_PRIV);
		boolean isAutoImport=false;
		String username = this.userView.getUserFullName();
		String year="";
		String month="";
		String count="";
		String fromScopt="";
		String toScope="";
		String needsum="0";
		if ("-1".equals(tabID)) {
			throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
		} else {
			this.getFormHM().put("dataarea", dataarea);
			HmusterViewBo hmusterViewBo = new HmusterViewBo(this.getFrameconn(),tabID);
			hmusterViewBo.setGroupPoint(groupPoint);
			hmusterViewBo.setDataarea(dataarea);
			hmusterViewBo.setNo_manger_priv(NO_MANAGE_PRIV);
			/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 start */
			if(hm.containsKey("linktype")) {
				hmusterViewBo.setLinktype((String)hm.get("linktype"));
			}
			/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 end */
			
			ArrayList photoList0 = new ArrayList();
			hmusterBo.setLayerid(layerid);
			// "direct":默认生成 "":通过设置生成
			if ("direct".equals(operateMethod))
				history = "1";
			if("1".equals(dataarea))
			{
				hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabID));
				if(hmusterViewBo.getTextFormatMap().size()>0)
		    		hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabID));
			}
			String res = (String) hm.get("res");
			res = res != null && res.trim().length() > 0 ? res : "0";
			hm.remove("res");
			
			if ("1".equals(res)) {
				historyRecord = "0";
			}
			String dbpre = (String) this.getFormHM().get("dbpre");
			dbpre = dbpre != null ? dbpre : "";
			ArrayList dblist = (ArrayList)this.getFormHM().get("dblist");
			if(dblist==null||dblist.size()==0|| "0".equals(historyRecord))
			{
				if(dblist!=null)
					dblist.clear();
				ArrayList inputList=this.userView.getPrivDbList();
				if(inputList==null||inputList.size()==0)
		    		throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
				for(int j=0;j<inputList.size();j++)
				{
					String pre=(String)inputList.get(j);
					dblist.add(new CommonData(pre,AdminCode.getCodeName("@@",pre)));
				}
				addAllDBItem(dblist);
			}
			/* 任务：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
			this.getFormHM().put("mainParamCondList", null);
			this.getFormHM().put("mainParamTitle", null);
			if(StringUtils.isNotEmpty(mainParamCond)) {
				//dbpre = "";
				// 得到业务用户设置的常用查询
			    condlist = musterbo.getCondArrayList(infor_Flag, mainParamCond);
		        if(!musterbo.condExists(condition, condlist))
		            condition = "";
				this.getFormHM().put("mainParamCondList", condlist);
				this.getFormHM().put("mainParamTitle", mainParamTitle);
				/* 问题修正：如果自定义了参数，那么默认选择第一个为查询条件 xiaoyun 2014-8-28 start */
				if(StringUtils.isEmpty(condition)) {
					CommonData data = (CommonData) condlist.get(0);
					condition = data.getDataValue();
				}
				/* 问题修正：如果自定义了参数，那么默认选择第一个为查询条件 xiaoyun 2014-8-28 end */
			}
			/* 任务：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
            if((factor!=null&&factor.trim().length()>0/* || mainParamCond!=null&&mainParamCond.length()>0*/))
                condlist.clear();
		
			try {
				if((factor!=null&&factor.trim().length()>0 || mainParamCond!=null&&mainParamCond.length()>0)/*&&currpage.equals("1")*/
				        && "1".equals(isGetData))
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
					String subset=hmxml.getValue(HmusterXML.SUBSETID);
					String subfactor=hmxml.getValue(HmusterXML.SUBFACTOR);
					String subexpr=hmxml.getValue(HmusterXML.SUBEXPR);
					/**>模糊查询True|False*/
					String FUZZYFLAG=hmxml.getValue(HmusterXML.FUZZYFLAG);
					/**>模糊查询True|False*/
					if("2".equals(flag))
					{
				    	fromScopt=subexpr+"::"+subfactor.replaceAll(",", "`")+"::"+("TRUE".equals(FUZZYFLAG)?"1":"0");	//history=2 1*2::a58z0=`a58z1=`::0
				    	toScope=subset;	//A58
				    	selectedPoint=subset;	//a58
					}
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
					needsum=hmxml.getValue(HmusterXML.NEEDSUM);
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
						for(int i=0;i<tmp_arr.length;i++)
						{
							if(tmp_arr[i]==null|| "".equals(tmp_arr[i]))
								continue;
							if(temp.toString().toUpperCase().indexOf((","+tmp_arr[i].toUpperCase()+","))==-1)
								continue;
							dblist.add(new CommonData(tmp_arr[i].toUpperCase(),AdminCode.getCodeName("@@", tmp_arr[i])));
						}
						addAllDBItem(dblist);
						// 防止dbpre为""时getFactorSQL未执行复杂查询
		                if((dbpre.trim().length()<1&&dblist.size()>0)||("0".equals(historyRecord)&&!"1".equals(changeDbpre))){
		                    dbpre=((CommonData)dblist.get(0)).getDataValue();
		                }
					}
					queryScope="0";
					if(factor!=null&&factor.trim().length()>0)
					    hmusterBo.getFactorSQL(nbase, FUZZYFLAG, HIS, DEPTONLY, UNITONLY, factor, expr, infor_Flag, userView,
					            dbpre,NO_MANAGE_PRIV,user + "_Muster_" + tabID);
					else{
                       if("3".equals(modelFlag))
                           musterbo.runAllDBCondTable(condition,"", dblist, infor_Flag);
                       else
                           musterbo.runCondTable(condition,"", dbpre, infor_Flag);
                       queryScope="1";  // 查询结果
					}
					condition="";
			    	isAutoImport = true;
				}
				
				if((dbpre.trim().length()<1&&dblist.size()>0)||("0".equals(historyRecord)&&!"1".equals(changeDbpre))){
					dbpre=((CommonData)dblist.get(0)).getDataValue();
				}
				if(!"1".equals(infor_Flag)){
				    dblist.clear();
				}
				hmusterBo.setCountflag(needsum);
				hmusterBo.setFlag(flag);
				HmusterPdf hmusterPdf = new HmusterPdf(this.getFrameconn());

				HashMap cFactorMap = hmusterBo.getCfactor(tabID);

				if (cFactorMap.get("groupN") != null) {
					if (cFactorMap.get("multipleGroupN") != null)
						hmusterViewBo.setIsGroupNoPage((String) cFactorMap.get("multipleGroupN"));
				}
				/* 标识：4288 各单位编制情况：非su登录，该模块，右侧表格显示不全 xiaoyun 2014-9-15 start */
				if(condlist.size() >= 1) { // 防止查询条件挡住花名册，花名册向下偏移
				/* 标识：4288 各单位编制情况：非su登录，该模块，右侧表格显示不全 xiaoyun 2014-9-15 end */
					/* 任务：3692 首页进来，高级花名册上边距过大的问题 xiaoyun 2014-8-29 start */
					//hmusterViewBo.setDeltaTop(hmusterViewBo.getDeltaTop() + 15);
					/* 问题标识：4121 各机构编制情况：在表格工具中，去掉取数条件，进入各机构编制情况，页面显示有错误 xiaoyun 2014-9-4 start */
					if(StringUtils.equals(showbuttons, "1")) {
						hmusterViewBo.setDeltaTop(hmusterViewBo.getDeltaTop() + 45);
					}else {
						hmusterViewBo.setDeltaTop(hmusterViewBo.getDeltaTop() + 20);
					}
					/* 问题标识：4121 各机构编制情况：在表格工具中，去掉取数条件，进入各机构编制情况，页面显示有错误 xiaoyun 2014-9-4 end */
					/* 任务：3692 首页进来，高级花名册上边距过大的问题 xiaoyun 2014-8-29 end */
				} 
				/* 删除上页生成的临时照片 */
				if (photoList != null && photoList.size() > 1)
					hmusterPdf.deletePicture(photoList);

				String state = "1";
				if (currpage == null || "0".equals(currpage)|| currpage.trim().length() == 0)
					currpage = "1";
				DbWizard dbWizard = new DbWizard(this.frameconn);
				

				if (dbWizard.isExistTable(user + "_Muster_" + tabID, false)) {
					if ("0".equals(historyRecord)) {
					    if(!isAutoImport) {
					        if("3".equals(modelFlag)&& "ALL".equalsIgnoreCase(dbpre))
					            musterbo.runAllDBCondTable(condition,"", dblist, infor_Flag);
					        else
					            musterbo.runCondTable(condition,"", dbpre, infor_Flag);
					    }
						hmusterBo.setGroupPointItem(groupPoint);
						hmusterBo.setSortitem(sortitem);
                        if("3".equals(modelFlag)&& "ALL".equalsIgnoreCase(dbpre))
                            state=hmusterBo.importAllDBData(history,user,user+ "_Muster_" + tabID,tabID,infor_Flag,dblist,queryScope,flag,year,month,count,fromScopt,
                                    toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
                                    modelFlag); 
                        else
                            state = hmusterBo.importData(history, user, user+ "_Muster_" + tabID, tabID, infor_Flag, dbpre,
                                    queryScope, flag, year, month, count,fromScopt, toScope, selectedPoint,isGroupPoint, groupPoint, this.getUserView(),modelFlag);
						hmusterBo = null;

					}
				} else {
				    if(!isAutoImport) {
				        if("3".equals(modelFlag)&& "ALL".equalsIgnoreCase(dbpre))
                            musterbo.runAllDBCondTable(condition,"", dblist, infor_Flag);
                        else
                            musterbo.runCondTable(condition,"", dbpre, infor_Flag);
				    }
					hmusterBo.setGroupPointItem(groupPoint);
					hmusterBo.setSortitem(sortitem);
                    if("3".equals(modelFlag)&& "ALL".equalsIgnoreCase(dbpre))
                        state=hmusterBo.importAllDBData(history,user,user+ "_Muster_" + tabID,tabID,infor_Flag,dblist,queryScope,flag,year,month,count,fromScopt,
                                toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
                                modelFlag); 
                    else
                        state = hmusterBo.importData(history, user, user+ "_Muster_" + tabID, tabID, infor_Flag, dbpre,
                                queryScope, flag,year, month, count,fromScopt, toScope, selectedPoint, isGroupPoint, groupPoint,this.getUserView(), modelFlag);
					hmusterBo = null;
				}
				hmusterViewBo.getResourceCloumn(tabID, infor_Flag);
				ArrayList list = hmusterViewBo.getHumster(infor_Flag, tabID,
						isGroupPoint, groupPoint, user + "_Muster_" + tabID,
						pageRows, currpage, isAutoCount, zeroPrint, emptyRow,
						column, pix, columnLine, username, dbpre, history,
						year,month,count, userView, operateMethod, printGrid,
						modelFlag);
				this.getFormHM().put("paperRows",String.valueOf(hmusterViewBo.getPageRows()));
				/** 哈药-领导桌面(高级花名册) 如果只有一页，那么不显示分页区域 xiaoyun 2014-8-13 start */
				//hmusterViewBo = null;
				/** 哈药-领导桌面(高级花名册) 如果只有一页，那么不显示分页区域 xiaoyun 2014-8-13 end */
				groupPointList = getHmusterGroupPointList(infor_Flag);
				this.getFormHM().put("tableHeader", ((String) list.get(1)).replace("\n", ""));
				this.getFormHM().put("tableBody", ((String) list.get(2)).replace("\n", ""));
				this.getFormHM().put("tableTitleTop",((String) list.get(0)).replace("\n", ""));
				this.getFormHM().put("tableTitleBottom",((String) list.get(3)).replace("\n", ""));
				this.getFormHM().put("turnPage", ((String) list.get(4)).replace("'","\"").replace("\n", ""));
				this.getFormHM().put("photoList", (ArrayList) list.get(5));
				this.getFormHM().put("checkflag", checkflag);
				this.getFormHM().put("inforkind", infor_Flag);
				this.getFormHM().put("groupPointList", (ArrayList) list.get(5));
				this.getFormHM().put("dbpre", dbpre);
				this.getFormHM().put("dblist", dblist);

				/** 由于form属性为session，所以清空属性值 * */
				if (hm.get("clears") != null&& "1".equals((String) hm.get("clears"))) {
					this.getFormHM().put("clears", "1");
					hm.remove("clears");
				}
			} catch (Exception e) {
				if (!"-1".equals(tabID)) {
					try {
						UsrResultTable resulttable = new UsrResultTable();
						if (resulttable.isNumber(this.userView.getUserName())) {
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.hroster")));
						}
						if(hmusterBo != null) {
        					hmusterBo.setCountflag(needsum);
        					musterbo.runCondTable(condition,"", dbpre, infor_Flag);
        					hmusterBo.setGroupPointItem(groupPoint);
        					hmusterBo.setSortitem(sortitem);
        					if("ALL".equalsIgnoreCase(dbpre))//liuy 2015-5-7 9443
        						hmusterBo.importAllDBData(history,user,user+ "_Muster_" + tabID,tabID,infor_Flag,dblist,queryScope,flag,year,month,count,fromScopt,
                            			toScope,selectedPoint,isGroupPoint,groupPoint,this.getUserView(),
                            			modelFlag);
        					else
        						hmusterBo.importData(history, user, user + "_Muster_"
        							+ tabID, tabID, infor_Flag, dbpre, queryScope,
        							flag, year,month,count,fromScopt,toScope,
        							selectedPoint, isGroupPoint, groupPoint, this.getUserView(), modelFlag);
						}
						ArrayList list = hmusterViewBo.getHumster(infor_Flag,
								tabID, isGroupPoint, groupPoint, user+ "_Muster_" + tabID, pageRows,
								currpage, isAutoCount, zeroPrint, emptyRow,
								column, pix, columnLine, username, dbpre,
								history, year,month,count, userView,
								operateMethod, printGrid, modelFlag);
						this.getFormHM().put("paperRows",String.valueOf(hmusterViewBo.getPageRows()));

						/* 哈药-领导桌面(高级花名册) 如果只有一页，那么不显示分页区域 xiaoyun 2014-8-13 start */
						//hmusterViewBo = null;
						/* 哈药-领导桌面(高级花名册) 如果只有一页，那么不显示分页区域 xiaoyun 2014-8-13 end */

						/* 任务：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
						this.getFormHM().put("mainParamCondList", null);
						this.getFormHM().put("mainParamTitle", null);
						if(StringUtils.isNotEmpty(mainParamCond)) {
							dbpre = "";
							// 得到业务用户设置的常用查询
							List mainParamCondList = musterbo.getCondList(infor_Flag, mainParamCond);
							this.getFormHM().put("mainParamCondList", mainParamCondList);
							this.getFormHM().put("mainParamTitle", mainParamTitle);
						}
						/* 任务：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
						
						groupPointList = getHmusterGroupPointList(infor_Flag);
						hmusterBo = null;
						this.getFormHM().put("tableHeader", ((String) list.get(1)).replace("\n", ""));
						this.getFormHM().put("tableBody", ((String) list.get(2)).replace("\n", ""));
						this.getFormHM().put("tableTitleTop",((String) list.get(0)).replace("\n", ""));
						this.getFormHM().put("tableTitleBottom",((String) list.get(3)).replace("\n", ""));
						this.getFormHM().put("turnPage", ((String) list.get(4)).replace("\n", ""));
						this.getFormHM().put("photoList", (ArrayList) list.get(5));
						this.getFormHM().put("checkflag", checkflag);
						this.getFormHM().put("inforkind", infor_Flag);
						this.getFormHM().put("groupPointList", groupPointList);

						
						this.getFormHM().put("dblist", dblist);

						/** 由于form属性为session，所以清空属性值 * */
						if (hm.get("clears") != null&& "1".equals((String) hm.get("clears"))) {
							this.getFormHM().put("clears", "1");
							hm.remove("clears");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						throw GeneralExceptionHandler.Handle(ex);
					}
				} else {
					throw GeneralExceptionHandler.Handle(e);
				}
			}
			this.getFormHM().put("isReData", "0");  // isGetData
			this.getFormHM().put("conditionslist", condlist);
			this.getFormHM().put("returnType",returnType);
			this.getFormHM().put("dbpre", dbpre);
			/** 哈药-领导桌面(高级花名册) 如果只有一页，那么不显示分页区域 xiaoyun 2014-8-13 start */
			int totalPage = hmusterViewBo.getTotalPage();
			if(totalPage <= 1) {
				this.getFormHM().put("turnPage", "");
			}
			hmusterViewBo = null;
			/** 哈药-领导桌面(高级花名册) 如果只有一页，那么不显示分页区域 xiaoyun 2014-8-13 end */
		}
		this.getFormHM().put("historyRecord", "1");

		System.gc();
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
			CommonData dataobj = new CommonData("B0110", ResourceFactory
					.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory
					.getProperty("tree.kkroot.kkdesc"));
			arrayList.add(dataobj2);
		} else if ("3".equals(inforkind)) // 职位库
		{
			mainSet = "K01";
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory
					.getProperty("tree.kkroot.kkdesc"));
			//
			CommonData dataobj = new CommonData("E0122", ResourceFactory
					.getProperty("column.sys.dept"));

			arrayList.add(dataobj2);
			arrayList.add(dataobj);

		} else if ("2".equals(inforkind)) // 单位库
		{
			mainSet = "B01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory
					.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet recset = null;
		try {
			strsql
					.append("select itemid,itemdesc from fielditem where fieldsetid='");
			strsql.append(mainSet);
			strsql
					.append("' and codesetid!='0'and useflag='1' order by  displayid ");
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				CommonData dataobj = new CommonData(recset.getString("itemid"),
						recset.getString("itemdesc"));
				arrayList.add(dataobj);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return arrayList;

	}

	private ArrayList getHmusterGroupPointList() {
		ArrayList list = new ArrayList();
		TaxMxBo taxbo = new TaxMxBo(this.getFrameconn());
		/** 取得个税明细表结构字段列表 */
		ArrayList fieldlist = taxbo.getFieldlist();
		CommonData dataobj = null;
		for (int i = 0; i < fieldlist.size(); i++) {
			Field field = (Field) fieldlist.get(i);
			String itemid = field.getName();
			String itendesc = field.getLabel();
			if (!("Tax_max_id".equalsIgnoreCase(itemid) || "A0100"
					.equalsIgnoreCase(itemid))) {
				if (field.isCode()) {
					dataobj = new CommonData(itemid, itendesc);
					list.add(dataobj);
				}
			}
		}

		return list;
	}

	/**
	 * 取得报税时间过滤条件
	 * 
	 * @param declaredate
	 * @return
	 */
	private String getFilterCond(String startime, String endtime) {
		StringBuffer buf = new StringBuffer();
		if (startime != null && startime.trim().length() > 0 && endtime != null
				&& endtime.trim().length() > 0) {
			WorkdiarySQLStr wss = new WorkdiarySQLStr();
			String tempstart = wss.getDataValue("declare_tax", ">=", startime);
			String tempend = wss.getDataValue("declare_tax", "<=", endtime);
			buf.append(tempstart);
			buf.append(" and ");
			buf.append(tempend);
		} else {
			buf.append("");
		}
		return buf.toString();
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
	
}
