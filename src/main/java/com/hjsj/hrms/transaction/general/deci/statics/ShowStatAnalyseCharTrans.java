/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.statics;

import com.hjsj.hrms.businessobject.general.statics.ShowExcel;
import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Element;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Owner
 *
 */
public class ShowStatAnalyseCharTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		// TODO Auto-generated method stub
	    HashMap reqHM = (HashMap)this.getFormHM().get("requestPamaHM");
	    String statid=(String)this.getFormHM().get("statid");
	    String showtitle = (String)reqHM.get("showtitle");
	    showtitle = (showtitle==null||"".equals(showtitle))?"1":showtitle;//add by xiegh on 20171117 =0不显示总数 ，非0则显示 
	    //liuy 2014-10-10 4553 一维常用统计支持钻取，即穿透到另一个一维常用统计 start
	    String subIndex = (String)reqHM.get("subIndex");
	    reqHM.remove("subIndex");
	    subIndex=subIndex==null?"":subIndex;
	    String flag = (String)reqHM.get("flag");
	    reqHM.remove("flag");
	    flag=flag==null?"":flag;
	    String substat="";
	    String[] substatstr=null;
	    ArrayList statIdList = new ArrayList();
	    if("".equals(subIndex)){
	    	substat = (String)reqHM.get("substat");
	    	reqHM.remove("substat");//liuy 2015-3-24 8254：领导桌面，图表穿透，第一次点开退休人员预测，点第二年退休人员，直接到了统计人员列表，没有进入单位统计图中。
	    	substat=substat==null?"":substat;
	    	if("zqct".equals(flag)){
	    		substat = (String)this.getFormHM().get("substat");
	    	}
	    	this.getFormHM().put("substat",substat);
	    	this.getFormHM().put("statIdList", new ArrayList());
			this.getFormHM().put("subIndexList", new ArrayList());
			this.getFormHM().put("showLegendList", new ArrayList());
			this.getFormHM().put("lexprList", new ArrayList());
			this.getFormHM().put("factorList", new ArrayList());
			this.getFormHM().put("statOptionList", new ArrayList());
			this.getFormHM().put("statNameList", new ArrayList());
	    }
	    if("start".equals(subIndex)){	    	
	    	substat = (String)reqHM.get("substat");
	    	reqHM.remove("substat");
	    	substat=substat==null?"":substat;
	    	if("zqct".equals(flag)){
	    		substat = (String)this.getFormHM().get("substat");
	    	}
	    	this.getFormHM().put("substat",substat);
	    	if((ArrayList)this.getFormHM().get("statIdList")!=null){	    		
	    		statIdList = (ArrayList)this.getFormHM().get("statIdList");
	    		if(statIdList.size()>0){	    			    				
	    			statid=statIdList.get(0).toString();
	    		}
	    	}
	    	this.getFormHM().put("statIdList", new ArrayList());
			this.getFormHM().put("subIndexList", new ArrayList());
			this.getFormHM().put("showLegendList", new ArrayList());
			this.getFormHM().put("lexprList", new ArrayList());
			this.getFormHM().put("factorList", new ArrayList());
			this.getFormHM().put("statOptionList", new ArrayList());
			this.getFormHM().put("statNameList", new ArrayList());
	    }
	    String lexpr="";
	    String factor="";
	    String changeSubIndex="";
	    if(!"".equals(subIndex)&&!"start".equals(subIndex)){//判断是否是钻取
	    	ArrayList subIndexList = new ArrayList();
	    	substat=(String)this.getFormHM().get("substat");//从form取出用户定义好的统计项
	    	substatstr = substat.split(",");
	    	if((ArrayList)this.getFormHM().get("statIdList")!=null){
	    		statIdList = (ArrayList)this.getFormHM().get("statIdList");
	    		subIndexList = (ArrayList)this.getFormHM().get("subIndexList");
		    	if("zqct".equals(flag)){//判断返回移除条件
		    		statid=statIdList.get(Integer.parseInt(subIndex)).toString();
		    		if(Integer.parseInt(subIndex)+1<substatstr.length){		    			
		    			int array = statIdList.size()-Integer.parseInt(subIndex);
    					if(array>=2){
    						int statIdListSize=statIdList.size()-1;
    						int subIndexListSize=subIndexList.size()-1;
    						for (int i = 0; i < array; i++) {									
    							statIdList.remove(statIdListSize-i);
    							subIndexList.remove(subIndexListSize-i);
							}
    						statIdList.add(statid);
    			    		subIndexList.add(subIndex);
    					}
		    		}
		    	}else{		    		
		    		statIdList.add(statid);
		    		subIndexList.add(subIndex);
		    	}
	    	}else {
	    		statIdList.add(statid);				
	    		subIndexList.add(subIndex);
			}
	    	this.getFormHM().put("statIdList",statIdList);
	    	this.getFormHM().put("subIndexList",subIndexList);
		    String substatid = substatstr[Integer.parseInt(subIndex)];//得到下级统计项id
		    if(Integer.parseInt(subIndex)>=substatstr.length-1)
		    {
		    	changeSubIndex = "next";
		    }else{		    	
		    	changeSubIndex = (Integer.parseInt(subIndex)+1)+"";
		    }
		    this.getFormHM().put("subIndex",changeSubIndex);
		    ArrayList showLegendList = new ArrayList();//保存在图上选中条件
		    String showLegend="";
		    if((ArrayList)this.getFormHM().get("showLegendList")!=null){
		    	showLegendList = (ArrayList)this.getFormHM().get("showLegendList");
		    	if("zqct".equals(flag)){
		    		showLegend=showLegendList.get(Integer.parseInt(subIndex)).toString();
		    		if(Integer.parseInt(subIndex)+1<substatstr.length){
		    			int array = showLegendList.size()-Integer.parseInt(subIndex);
						if(array>=2){
							int showLegendListSize=showLegendList.size()-1;
							for (int i = 0; i < array; i++) {									
								showLegendList.remove(showLegendListSize-i);
							}
				    		showLegendList.add(showLegend);
						}
		    		}
		    	}else{		    	
		    		showLegend=(String)reqHM.get("showLegend");
		    		showLegendList.add(showLegend);
		    	}
		    }else {
		    	showLegend=(String)reqHM.get("showLegend");				
		    	showLegendList.add(showLegend);
			}
	    	if(showLegend==null||showLegend.length()<=0)
	    		showLegend="";
    		this.getFormHM().put("showLegendList",showLegendList);
    		showLegend=SafeCode.decode(showLegend);
    		showLegend=showLegend.replaceAll("\n", "");
    		showLegend=showLegend.replaceAll("\r", "");
    		//-----------------保存统计项选中项名称
    		ArrayList statOptionList=new ArrayList();
    		if((ArrayList)this.getFormHM().get("statOptionList")!=null){    			
    			statOptionList = (ArrayList)this.getFormHM().get("statOptionList");
    			if("zqct".equals(flag)){
		    		if(Integer.parseInt(subIndex)+1<substatstr.length){
		    			int array = statOptionList.size()-Integer.parseInt(subIndex);
						if(array>=2){
							int statOptionListSize=statOptionList.size()-1;
							for (int i = 0; i < array; i++) {									
								statOptionList.remove(statOptionListSize-i);
							}
							statOptionList.add(showLegend);
						}
		    		}
		    	}else{		    	
		    		statOptionList.add(showLegend);
		    	}
    		}else {
				statOptionList.add(showLegend);
			}
    		this.getFormHM().put("statOptionList",statOptionList);
    		
    		//---------------保存统计项名称
    		ArrayList statNameList=new ArrayList();
    		String statName=getSname(statid);
    		if((ArrayList)this.getFormHM().get("statNameList")!=null){    			
    			statNameList = (ArrayList)this.getFormHM().get("statNameList");
    			if("zqct".equals(flag)){
		    		if(Integer.parseInt(subIndex)+1<substatstr.length){
		    			int array = statNameList.size()-Integer.parseInt(subIndex);
						if(array>=2){
							int statNameListSize=statNameList.size()-1;
							for (int i = 0; i < array; i++) {									
								statNameList.remove(statNameListSize-i);
							}
							statNameList.add(statName);
						}
		    		}
		    	}else{		    	
		    		statNameList.add(statName);
		    	}
    		}else {
    			statNameList.add(statName);
			}
    		this.getFormHM().put("statNameList",statNameList);
    		String sqlstr="select * from slegend where id="+statid+" and legend='"+showLegend+"'";
    		try {
    			this.frowset=dao.search(sqlstr);
    			if(this.frowset.next())
    			{
    				lexpr = this.frowset.getString("lexpr")!=null?this.frowset.getString("lexpr"):"";
    				factor = this.frowset.getString("factor")!=null?this.frowset.getString("factor")+"`":"";
    			}
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
	    	statid=substatid;//改变当前统计项id为钻取后统计项id
	    }
	    this.getFormHM().put("statid",statid);
	    String statName=getSname(statid);
	    this.getFormHM().put("statName",statName);
	    reqHM.put("statid", statid);
		//liuy 2014-10-10 end
		String result=(String)this.getFormHM().get("result");
		String dbpre=(String)this.getFormHM().get("dbpre");
		String a_code=(String)this.getFormHM().get("a_code");
	    boolean isresult=true;
	    if(result==null|| "".equals(result)|| "0".equals(result))
	    	isresult=true; 
	    else
	    	isresult =false;
	    String infokind="";//信息群
		String userbase="";//人员库
		String statname="";
		String type=null;   
		String condid="";
		String history="";

		ArrayList dblist=new ArrayList();
		try{
			String sql="";
			if("zqct".equals(flag)||(!"".equals(subIndex)&&!"start".equals(subIndex))){//钻取穿透人员库和人员范围不变
				sql="select * from sname where id=" + statIdList.get(0);
			}else{
				sql="select * from sname where id=" + statid;				
			}
			
			this.frowset=dao.search(sql.toString());
         	if(this.frowset.next())
         	{
         		infokind=this.frowset.getString("infokind");
				userbase=this.frowset.getString("nbase");
				statname = this.frowset.getString("name");
				if(userbase!=null&&userbase.length()>0)
				{
					String [] baseS=userbase.split(",");
					for(int i=0;i<baseS.length;i++)
					{
						if(baseS[i]!=null&&baseS[i].length()>0)
						{
							if(!this.userView.isSuper_admin())
			                {
			         			ArrayList nb_list=this.userView.getPrivDbList();
			         			for(int r=0;r<nb_list.size();r++){
		                    		String ubase=nb_list.get(r).toString();
		                    		if(baseS[i].equalsIgnoreCase(ubase)){
		                    			dblist.add(baseS[i]);
		                    		}
		                    	} 
			                }else
			                {
			                	dblist.add(baseS[i]);
			                }
						}
						  
						
					}
				}				
         		type=this.frowset.getString("type");
   			    condid = this.frowset.getString("condid");
      	    }
         	if(dblist==null||dblist.size()<=0){
				dblist=(ArrayList)this.userView.getPrivDbList().clone();
				//zgd 2014-8-25 常用统计信息集设置中未指定人员库basesize=0
				this.getFormHM().put("basesize","0");
         	}else{
         		//zgd 2014-8-25 设置了人员库则直接调用所选的全部人员库（All）
         		dbpre = "ALL";
         		//zgd 2014-8-25 常用统计信息集设置中指定了人员库basesize=1
         		this.getFormHM().put("basesize","1");
         	}
         	loadprivdb(dblist);//页面显示人员库下拉框sql语句
         	if(type!=null && "1".equals(type))
         		this.getFormHM().put("isonetwostat","1");
         	else if(type!=null && "2".equals(type))
         		this.getFormHM().put("isonetwostat","2");
         	
         	// 指定statid参数，statlist需要重新设置
            if(reqHM.containsKey("statid")) {
                ArrayList statlist = new ArrayList();
                CommonData data=new CommonData();
                data.setDataValue(statid);
                data.setDataName(statname);
                statlist.add(data);
                if(this.getFormHM().get("statlistsize")==null){                	
                	this.getFormHM().put("statlistsize", statlist.size()+"");
                }
                if(this.getFormHM().get("statlist")==null){
                	this.getFormHM().put("statlist", statlist);
                }                	
                String showstatname = "";
                String showcharttype = "";
                if(reqHM.containsKey("showstatname")) {
                    showstatname = (String)reqHM.get("showstatname");
                    showstatname=showstatname==null?"":showstatname;
                    reqHM.remove("showstatname");
                }
                if("".equals(showstatname)){
                	showstatname=(String)this.getFormHM().get("showstatname");
                }
                if(reqHM.containsKey("showcharttype")) {
                	showcharttype = (String)reqHM.get("showcharttype");
                	showcharttype=showcharttype==null?"":showcharttype;
                    reqHM.remove("showcharttype");
                }
                if("".equals(showcharttype)){
                	showcharttype=(String)this.getFormHM().get("showcharttype");
                }
                this.getFormHM().put("showstatname", showstatname);
                this.getFormHM().put("showcharttype", showcharttype);
                reqHM.remove("statid");
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		if(dbpre==null||dbpre.length()<=0|| "ALL".equalsIgnoreCase(dbpre))
		{
			dbpre="ALL";
		}else
		{
			dblist.clear();
			dblist.add(dbpre);
		}
			
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
		ArrayList condlist=simplestat.getCondlist(condid,dao);	
		this.formHM.put("condlist", condlist);
		String lexprId=(String)this.getFormHM().get("lexprId");//常用查询条件
		if(lexprId==null||lexprId.length()<=0)
		{
			if(condlist!=null&&condlist.size()>0)
			{
				CommonData da=(CommonData)condlist.get(0);
				lexprId=da.getDataValue();
			}
		}
		this.getFormHM().put("lexprId",lexprId);//liuy 2015-7-7 10830：hcm领导桌面：人力资源现状-退休人员预测-当年退休人员-业务管理（显示与反查不一致）
		String []curr_id=null;
		String commlexr=null;
	    String commfacor=null;
		if(lexprId!=null&&lexprId.length()>0)
		{
			//加上常用查询进行的统计
			curr_id=new String[1];
			curr_id[0]=lexprId;			
		}		
	    if(curr_id!=null&&curr_id.length>0)
	    {
	    	GeneralQueryStat generalstat=new GeneralQueryStat();
			generalstat.getGeneralQueryLexrfacor(curr_id,userbase,"",this.getFrameconn());	    
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    	history = generalstat.getHistory();
	    }
	  //liuy 2014-10-10 4553 一维常用统计支持钻取，即穿透到另一个一维常用统计 start
    	if(!"".equals(subIndex)&&!"start".equals(subIndex)){//判断是否是钻取，是则合并表达式
    		ArrayList lexprFactor=new ArrayList();
    		ArrayList lexprList = new ArrayList();
    		ArrayList factorList = new ArrayList();
    		if(!"zqct".equals(flag)){//正面钻取穿透   			
    			if((ArrayList)this.getFormHM().get("lexprList")!=null&&(ArrayList)this.getFormHM().get("factorList")!=null){	    			
    				lexprList = (ArrayList)this.getFormHM().get("lexprList");
    				factorList = (ArrayList)this.getFormHM().get("factorList");
    				for (int i = 0; i < lexprList.size(); i++) {//累加合并表达式
    					String lexprstr = lexprList.get(i).toString();
    					String factorstr = factorList.get(i).toString();
    					lexprFactor.add(lexprstr+"|"+factorstr);
    				}
    			}
    		}else{//钻取穿透返回
    			if((ArrayList)this.getFormHM().get("lexprList")!=null&&(ArrayList)this.getFormHM().get("factorList")!=null){	    			
    				lexprList = (ArrayList)this.getFormHM().get("lexprList");
    				factorList = (ArrayList)this.getFormHM().get("factorList");
    				String lexprstr = lexprList.get(Integer.parseInt(subIndex)).toString();
    				String factorstr = factorList.get(Integer.parseInt(subIndex)).toString();
					int array = lexprList.size()-Integer.parseInt(subIndex);
					if(array>=2){
						int lexprListSize=lexprList.size()-1;
						int factorListSize=factorList.size()-1;
						for (int i = 0; i < array; i++) {									
							lexprList.remove(lexprListSize-i);
							factorList.remove(factorListSize-i);
						}
						lexprList.add(lexprstr);
						factorList.add(factorstr);
					}
    				for (int i = 0; i < lexprList.size()-1; i++) {//累加合并表达式
    					lexprstr = lexprList.get(i).toString();
    					factorstr = factorList.get(i).toString();
    					lexprFactor.add(lexprstr+"|"+factorstr);
    				}
    			}
    		}
    		lexprFactor.add(lexpr + "|" + factor);
    		if(commlexr!=null&&commfacor!=null){    			
    			lexprFactor.add(commlexr + "|" + commfacor);
    		}
			CombineFactor combinefactor=new CombineFactor();
			String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
			if(Stok.hasMoreTokens())
			{
				commlexr=Stok.nextToken();
				commfacor=Stok.nextToken();
			}
			//保存表达式
			if(!"zqct".equals(flag)){//返回的时候不用保存后面的条件
				if(commlexr != null)
					lexprList.add(commlexr);
				if(commfacor != null)
					factorList.add(commfacor);
			}
			this.getFormHM().put("commlexr", commlexr);
			this.getFormHM().put("commfacor", commfacor);
			this.getFormHM().put("lexprList", lexprList);
			this.getFormHM().put("factorList", factorList);
    	}
    	//liuy 2014-10-10 end
		try{
		if(type!=null && "1".equals(type))
		{
			int[] statvalues = null;
			double[] statvaluess = null;
			String[] fieldDisplay; 
			String SNameDisplay;
			ArrayList datalist=new ArrayList();	
		    
		    /*String exprfactor="";
		    String exprlexpr="";
		    if(a_code!=null && a_code.length()>=2)
		    {
		    	String codeid=a_code.substring(0,2);
		    	if(codeid.equalsIgnoreCase("UN"))
				{
		    		exprlexpr="1";				
		    		exprfactor="B0110=";
				}
				else if(codeid.equalsIgnoreCase("UM"))
				{
					exprlexpr="1";			
					exprfactor="E0122=";
				}
				else
				{
					exprlexpr="1";				
					exprfactor="E01A1=";
				}
		    	exprfactor+=a_code.substring(2)+"*`";
		    	statvalues =simplestat.getLexprData(dblist, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","");
		    }else
		        statvalues =simplestat.getLexprData(dblist, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,null,"");*/
			
			/* 38915 统计时默认读取第一个统计方式进行统计，如果没设置，按个数统计 guodd 2017-07-26 */
			String formulaId = getFirstSformula(statid);
			if(formulaId.length()>0)
				statvaluess =simplestat.getLexprDataSformula(dblist, Integer.parseInt(statid), a_code, userView.getUserName(),userView.getManagePrivCode(), userView, "1", isresult, commlexr, commfacor, "2", history, formulaId, this.frameconn, "", "");
	    		else
	    			statvalues =simplestat.getLexprData(dblist, Integer.parseInt(statid), a_code, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,commlexr,commfacor,"2",history,"","","");
			SNameDisplay = simplestat.getSNameDisplay();
			/**领导桌面 统计方式  类型控制标题是否显示值   wangb 20190514 */
			if(formulaId.length()>0){
			    SformulaXml xml = new SformulaXml(this.frameconn,statid);
				Element element = xml.getElement(formulaId);
				if(element==null){
					formulaId="";
					this.getFormHM().put("decimalwidth", "0");
					this.getFormHM().put("isneedsum", "true");
				}else{
					String decimalwidth = element.getAttributeValue("decimalwidth");
					decimalwidth=(decimalwidth==null||decimalwidth.length()==0)?"2":decimalwidth;
					this.getFormHM().put("decimalwidth", decimalwidth);
					String formulatype = element.getAttributeValue("type");
					if("sum".equalsIgnoreCase(formulatype)||"count".equalsIgnoreCase(formulatype))
						this.getFormHM().put("isneedsum", "true");
					else
						this.getFormHM().put("isneedsum", "false");
				}
		    }else{
		    	this.getFormHM().put("decimalwidth", "0");
		    	this.getFormHM().put("isneedsum", "true");
		    }
			
			if(formulaId.length()==0 && statvalues != null && statvalues.length > 0){
				fieldDisplay = simplestat.getDisplay();
				int statTotal = 0;
				for (int i = 0; i < statvalues.length; i++) {
					 CommonData vo=new CommonData();
					 vo.setDataName(fieldDisplay[i]);
					 vo.setDataValue(String.valueOf(statvalues[i]));
					 datalist.add(vo);
				     statTotal += statvalues[i];
				}
			  this.getFormHM().put("snamedisplay",SNameDisplay);
		      this.getFormHM().put("datalist",datalist);
		      this.getFormHM().put("dbpre", dbpre);
		      
			  String xangle=AnychartBo.computeXangle(datalist);//update by wangcq on 2014-12-20
			  this.getFormHM().put("xangle", xangle);
		      this.getFormHM().put("totalvalue",String.valueOf(statTotal));
			}else if(formulaId.length()>0 && statvaluess != null && statvaluess.length > 0){
				fieldDisplay = simplestat.getDisplay();
				double statTotal = 0.0;
				for (int i = 0; i < statvaluess.length; i++) {
					 CommonData vo=new CommonData();
					 vo.setDataName(fieldDisplay[i]);
					 vo.setDataValue(String.valueOf(statvaluess[i]));
					 datalist.add(vo);
				     statTotal += statvaluess[i];
				}
			  this.getFormHM().put("snamedisplay",SNameDisplay);
		      this.getFormHM().put("datalist",datalist);
		      this.getFormHM().put("dbpre", dbpre);
		      
			  String xangle=AnychartBo.computeXangle(datalist);//update by wangcq on 2014-12-20
			  this.getFormHM().put("xangle", xangle);
		      this.getFormHM().put("totalvalue",String.valueOf(statTotal));
			}else{// 统计项没有数据时，也要显示统计图      wangb 20180725 bug 38962
				 StringBuffer sql =new StringBuffer();
					sql.append("select * from SName where id=");
					sql.append(statid);
					List rs =ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
					}
					CommonData vo=new CommonData();
					vo.setDataName("");
					vo.setDataValue("0");
					datalist.add(vo);
					this.getFormHM().put("snamedisplay",SNameDisplay);
					this.getFormHM().put("datalist",datalist);
				    this.getFormHM().put("dbpre", dbpre);
				    String xangle=AnychartBo.computeXangle(datalist);//update by wangcq on 2014-12-20
					this.getFormHM().put("xangle", xangle);
			}
			HashMap jfreemap=new HashMap();
			jfreemap.put(SNameDisplay, datalist);
			this.getFormHM().put("jfreemap" ,jfreemap);
		}else if(type!=null && "2".equals(type))
		{
			 int[][] statValues;
			 /*
			 String exprlexpr;
			 String exprfactor;
			 
			   if(a_code!=null && a_code.length()>=2)
			    {
			    	String codeid=a_code.substring(0,2);
			    	if(codeid.equalsIgnoreCase("UN"))
					{
			    		exprlexpr="1";				
			    		exprfactor="B0110=";
					}
					else if(codeid.equalsIgnoreCase("UM"))
					{
						exprlexpr="1";			
						exprfactor="E0122=";
					}
					else
					{
						exprlexpr="1";				
						exprfactor="E01A1=";
					}
			    	exprfactor+=a_code.substring(2)+"*`";
			    	statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dblist,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2",null);
			    }else
			    	statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dblist,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,result,null);
			   */
			statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dblist,a_code,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,commlexr,commfacor,result,history);
		    List varraylist=simplestat.getVerticalArray();
			List harraylist=simplestat.getHorizonArray();
			String snameplay=simplestat.getSNameDisplay();
			int totalvalue=simplestat.getTotalValue();
			ShowExcel show= new ShowExcel(this.getFrameconn());
			/*liuy 2014-12-16注释，修改导出二维统计表为Ajax，不用每次显示的时候就把Excel给生成好
			//liuy 2014-12-4 5447：hcm领导桌面：人力资源情况/用工形式/用工形式岗位分布，不能导出excel  start
			String filename = show.creatExcel(statValues, varraylist, harraylist, snameplay, totalvalue);
			filename = SafeCode.encode(PubFunc.encrypt(filename));
			this.getFormHM().put("filename", filename);
			//liuy end
			 */
			this.getFormHM().put("statdoublevalues",statValues);
			this.getFormHM().put("varraylist",varraylist);
			this.getFormHM().put("harraylist",harraylist);
			this.getFormHM().put("snamedisplay",snameplay);
			this.getFormHM().put("totalvalue",String.valueOf(totalvalue));
			this.getFormHM().put("dbpre", dbpre);
			//zgd 2014-8-13 将常用查询条件存入Form中
			this.getFormHM().put("commlexr", commlexr);
			this.getFormHM().put("commfacor", commfacor);
			this.getFormHM().put("showtitle", showtitle);
		}
	} catch (Exception e) {
    	e.printStackTrace();
    	throw new GeneralException("", e.toString(),"", "");
	}
	}
	/*加载应前缀库过滤条件*/
	private void loadprivdb(ArrayList dblist)
	{
		 /**应用库过滤前缀符号*/       
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
            if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        /**应用库前缀过滤条件*/
        this.getFormHM().put("dbcond",cond.toString());
	}
	
	/*
	 *  钻取穿透查询统计项名称
	 */
	private String getSname(String statid){
		String statName="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sqlName="select name from sname where id="+statid;
		try {
			this.frowset=dao.search(sqlName);
			if(this.frowset.next())
			{
				statName = this.frowset.getString("name")!=null?this.frowset.getString("name"):"";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statName;
	}
	
	/**
	 * 查找第一个统计方式
	 * @param statid
	 * @return
	 */
	private String getFirstSformula(String statid){
		SformulaXml xml = new SformulaXml(this.frameconn,statid);
		List list = xml.getAllChildren();
		String sformulaId = "";
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Element element = (Element)list.get(i);
				if (!"1".equals(element.getAttributeValue("del"))) {
					sformulaId = element.getAttributeValue("id");
					break;
				}
			}
		}
		return sformulaId;
	}
	
}
