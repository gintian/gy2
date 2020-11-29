/*
 * Created on 2005-6-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import java.sql.SQLException;
import java.util.*;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchDoubleDataTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm = (HashMap)(this.getFormHM().get("requestPamaHM"));
		GeneralQueryStat generalstat=new GeneralQueryStat();
		// TODO Auto-generated method stub
		String userbases=(String)this.getFormHM().get("userbases");
		userbases=userbases==null?"":userbases;
		userbases=userbases.toUpperCase();
		String userbase=(String)this.getFormHM().get("userbase");
		String statId=(String)this.getFormHM().get("statid");
		
		String statenter = (String)hm.get("statenter");
		hm.remove("statenter");
		statenter = statenter==null ?"":statenter;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(!"true".equals(statenter)){
			String sql1="select * from sname where id='"+statId+"'";
			try {
				this.frowset=dao.search(sql1);
				if(this.frowset.next())
				{
					userbases=this.frowset.getString("nbase");
					userbases = userbases == null?"":userbases.replaceAll(",", "`");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			this.getFormHM().put("userbases",userbases);
			
			String [] nbases = userbases.split("`");
    		if(userbase == null || userbase.length() < 1)
    		    userbase = nbases[0];
    		StringBuffer viewbase = new StringBuffer();
    		for(int i=0;i<nbases.length;i++){
    			viewbase.append(";"+AdminCode.getCodeName("@@", nbases[i].toUpperCase()));
    		}
    		if(viewbase.length()>1)
    			this.getFormHM().put("viewuserbases",viewbase.substring(1));
		}
		String org_filter = "";
		try {
			this.frowset = dao.search("select org_filter from sname where id=?",Arrays.asList(statId));
			if(this.frowset.next()){
				org_filter = String.valueOf(this.frowset.getInt("org_filter"));
				this.formHM.put("org_filter",org_filter);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList privdblist = this.userView.getPrivDbList();
		if(privdblist.size()==0)
			throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
		userbase=userbase==null||userbase.length()==0?(String)privdblist.get(0):userbase;
		
		CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
		statId=checkPrivSafeBo.checkResource(IResourceConstant.STATICS, statId);
		
		generalstat.insertCount(statId, this.frameconn);
		String querycond=(String)this.getFormHM().get("querycond");
		String infokind=(String)this.getFormHM().get("infokind");
		String home=(String)this.getFormHM().get("home");
		home=home==null||home.length()==0?"0":home;
		String sformula = (String)this.getFormHM().get("sformula");
	    sformula = sformula ==null?"":sformula;
	    String[] curr_id=(String[])this.getFormHM().get("curr_id");
	    String preresult=(String)this.getFormHM().get("preresult");
	    
	    if (sformula.length() == 0) {
			SformulaXml xml3 = new SformulaXml(this.frameconn,statId);
			sformula = xml3.getFistSformula();
		}
	    if(preresult==null||preresult.length()<=0)
	    	preresult="2";
	    if("1".equals(preresult))
	    	curr_id=null;
	    if(curr_id!=null&&curr_id.length>0)
	    {
	    	if(curr_id[0]!=null&& "#".equals(curr_id[0]))
	    		curr_id=null;
	    }
	    if(curr_id==null&&infokind!=null&&("1".equals(infokind)||"1,2,3".equals(infokind)))
	    {
	    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
	    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
	    	{
	    		String[] stat_ids=new String[1];
	    		stat_ids[0]=stat_id;
	    		curr_id=stat_ids;	    		
	    	}
	    }
	    if("0".equals(preresult))
	    	preresult="2";
	    String history=(String)this.getFormHM().get("history");
		    boolean isresult=true;
		    String result="";
//		    String result=(String)this.getFormHM().get("result");
		    if(result==null|| "".equals(result))
		    {
		    	StringBuffer sql =new StringBuffer();
				sql.append("select flag from SName where id=");
				sql.append(statId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
					if(flag!=null&& "1".equals(flag))
						isresult=false; //false时才查询，查询结果表
				}
		    }else if("1".equals(result))
		    	isresult=false; 
		    
		    this.getFormHM().put("result", isresult? "0":"1");
		    
		    //加上常用查询进行的统计
		    String commlexr=null;
		    String commfacor=null;
			
			generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.getFrameconn());
		    
		    if(curr_id!=null)
		    {
		    	commlexr=generalstat.getLexpr();
		    	commfacor=generalstat.getLfactor();
		    }
		    if(sformula.length()>0){
			    SformulaXml xml = new SformulaXml(this.frameconn,statId);
				Element element = xml.getElement(sformula);
				if(element==null){
					sformula="";
					this.getFormHM().put("decimalwidth", "0");
					this.getFormHM().put("isneedsum", "true");
				}else{
					String decimalwidth = element.getAttributeValue("decimalwidth");
					decimalwidth=(decimalwidth==null||decimalwidth.length()==0)?"2":decimalwidth;
					this.getFormHM().put("decimalwidth", decimalwidth);
					String type = element.getAttributeValue("type");
					if("sum".equalsIgnoreCase(type)||"count".equalsIgnoreCase(type))
						this.getFormHM().put("isneedsum", "true");
					else
						this.getFormHM().put("isneedsum", "false");
				}
		    }else{
		    	this.getFormHM().put("decimalwidth", "0");
		    	this.getFormHM().put("isneedsum", "true");
		    }
		String vtotal = (String)this.getFormHM().get("vtotal");
		vtotal=vtotal==null||vtotal.length()==0?"0":vtotal;
		String htotal = (String)this.getFormHM().get("htotal");
		htotal=htotal==null||htotal.length()==0?"0":htotal;
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
	    if("1".equalsIgnoreCase(org_filter)){//按机构筛选 逻辑  二维统计 wangb 2019-08-19
	    	querycond = "";
	    	String filter_type = (String) hm.get("filter_type");
	    	filter_type = filter_type == null? "0":filter_type;
	    	hm.remove("filter_type");
	    	String filterId = "";
	    	String filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    	String orgFactor = "";
	    	String orgLexpr = "";
	    	if("1".equalsIgnoreCase(filter_type)){
	    		filterId = (String) this.getFormHM().get("filterId");
	    		if("UN".equalsIgnoreCase(filterId)) {
	    			filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");//组织机构
	    		}
	    		if(AdminCode.getCode("UN", filterId)!=null){
    				orgFactor = "b0110="+filterId+"*`";
    				filterName = AdminCode.getCode("UN", filterId).getCodename();
    			}
    			if(AdminCode.getCode("UM", filterId)!=null){
    				orgFactor = "e0122="+filterId+"*`";
    				filterName = AdminCode.getCode("UM", filterId).getCodename();
    			}
	    	}else{
	    		if(this.userView.isSuper_admin()){
	    			filterId = "UN";
	    			filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");//组织机构
	    		}else{
	    			filterId = this.userView.getManagePrivCodeValue();this.userView.getManagePrivCode();
	    			if("UN".equalsIgnoreCase(userView.getManagePrivCode()) && filterId.trim().length() == 0){
	    				filterId = "UN";
	    			}
	    			if(!"UN".equalsIgnoreCase(filterId)){
	    				if("UN".equalsIgnoreCase(this.userView.getManagePrivCode())){
	    					orgFactor = "b0110="+filterId+"*`";
	    					filterName = AdminCode.getCode("UN", filterId).getCodename();
	    				}
	    				if("UM".equalsIgnoreCase(this.userView.getManagePrivCode())){
	    					orgFactor = "e0122="+filterId+"*`";
	    					filterName = AdminCode.getCode("UM", filterId).getCodename();
	    				}
	    			}else{
	    				filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    			}
	    		}
	    	}
	    	this.formHM.put("filterId", filterId);
	    	this.formHM.put("filterName", filterName);
	    	if(!"UN".equalsIgnoreCase(filterId)){
	    		if(orgFactor != null && orgFactor.trim().length() > 0){
	    			orgLexpr = "1";
	    		}
	    		if(commfacor !=null && commfacor.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
	    			String[] style = simplestat.getCombinLexprFactor(orgLexpr, orgFactor, commlexr, commfacor);
	    			commlexr = style[0];
	    			commfacor = style[1];
	    		}else{
	    			commlexr = orgLexpr;
	    			commfacor = orgFactor;
	    		}
	    	}
	    }else{
	    	this.formHM.put("filterId", "UN");
	    	this.formHM.put("filterName", ResourceFactory.getProperty("tree.orgroot.orgdesc"));
	    }
		
		double[][] statValuess=null;
		int[][] statValues=null;
		if(userbases==null||userbases.length()==0){
			this.getFormHM().put("userbases", userbase);
			//ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search("select dbname from DBName where upper(pre)='"+userbase.toUpperCase()+"'");
				if(this.frowset.next()){
					this.getFormHM().put("viewuserbases", "("+userbase+")"+this.frowset.getString("dbname"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(sformula.length()>0){
				statValuess=simplestat.getDoubleLexprDataSformula(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn,vtotal,htotal);
			}else{
				statValues=simplestat.getDoubleLexprData(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,vtotal,htotal);
			}
		}else{
			if(sformula.length()>0){
				statValuess=simplestat.getDoubleLexprDataSformula(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases,sformula,this.frameconn,vtotal,htotal);
			}else{
				statValues=simplestat.getDoubleLexprData(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases,vtotal,htotal);
			}
		}
		if((sformula.length()>0&&statValuess==null)||(sformula.length()==0&&statValues==null))
        	throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("planar.two.error")));
	    List varraylist=simplestat.getVerticalArray();
		List harraylist=simplestat.getHorizonArray();
		String snameplay=simplestat.getSNameDisplay();
		int totalvalue=simplestat.getTotalValue();
		double totalvalues = simplestat.getTotalValues();
		if(sformula.length()>0)
			this.getFormHM().put("statdoublevaluess",statValuess);
		else
			this.getFormHM().put("statdoublevalues",statValues);

		//System.out.println("querycondddddddd" + querycond);
		this.getFormHM().put("queryconde",this.getFormHM().get(querycond));//特殊处理，按组织机构过滤不走  设置统计范围 值原封还原
		this.getFormHM().put("home",home);
		this.getFormHM().put("varraylist",varraylist);
		this.getFormHM().put("harraylist",harraylist);
		this.getFormHM().put("snamedisplay",snameplay);
		if(sformula.length()==0)
			this.getFormHM().put("totalvalue",String.valueOf(totalvalue));
		else
			this.getFormHM().put("totalvalue",String.valueOf(totalvalues));
		this.getSformula(statId);
		this.getFormHM().put("sformula", sformula);
		this.getFormHM().put("userbase", userbase);
		//二位统计平面直方图、折线图
		String zcFlag = "no";//是否总裁桌面调用
		String chartType ="299";//=29平面直方图  =11折线图
		//获取二维统计图形 wangb 20190704
		try {
			this.frowset = dao.search("select viewtype from sname where id='"+statId+"'");
			if(this.frowset.next())
				chartType = this.frowset.getString("viewtype");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chartType = chartType == null || chartType.trim().length()==0? "299":chartType;
		String chartWidth ="1200";//宽
/*		if(!StringUtils.isEmpty((String)this.getFormHM().get("chartWidth")))
			chartWidth=(String)this.getFormHM().get("chartWidth");*/
//		String chartHeight ="356";//高
		if(!StringUtils.isEmpty((String)this.getFormHM().get("zcFlag")))
			zcFlag=(String)this.getFormHM().get("zcFlag");
		if("no".equals(zcFlag))
			this.getFormHM().put("chartFlag" ,"no");
		else{
			chartWidth = "-1";
			this.getFormHM().put("chartFlag" ,"yes");
		}
		this.getFormHM().put("chartWidth" ,chartWidth);
	/*	if(!StringUtils.isEmpty((String)this.getFormHM().get("chartHeight")))
			chartHeight=(String)this.getFormHM().get("chartHeight");*/
		ArrayList list = new ArrayList();//平面直方图数据集
		LinkedHashMap dbMap = new LinkedHashMap();//折线图数据集
		for(int i=0;i<varraylist.size();i++){
			ArrayList commList = new ArrayList();
				for(int j=0;j<harraylist.size();j++){
					if(sformula.length()>0){
						CommonData vo=new CommonData(String.valueOf(statValuess[i][j]),(String)((LazyDynaBean)harraylist.get(j)).get("legend"));
						commList.add(vo);
						}
					else{
						CommonData vo=new CommonData(String.valueOf(statValues[i][j]),(String)((LazyDynaBean)harraylist.get(j)).get("legend"));
						commList.add(vo);
						}
				}
			LazyDynaBean abean = new LazyDynaBean();
			abean.set("categoryName",(String)((LazyDynaBean)varraylist.get(i)).get("legend"));
			abean.set("dataList",commList);
			dbMap.put((String)((LazyDynaBean)varraylist.get(i)).get("legend"), commList);
			list.add(abean);
		}
		if(hm.get("statid") == null) {
			chartType = (String)this.getFormHM().get("chart_type");
		}
		String viewtype = (String)hm.get("chart_type");
		hm.remove("chart_type");
		viewtype = viewtype == null || viewtype.length() == 0 ? chartType : viewtype;
		if(!("299".equals(viewtype) || "33".equalsIgnoreCase(viewtype) || "11".equals(viewtype)))
			viewtype = "299"; //不是 柱状图、折线图 和 堆叠条形图 进来 默认显示 柱状图 wangb 20180802 bug 39341
		if("299".equals(viewtype))
			this.getFormHM().put("list",list);
		else
			this.getFormHM().put("jfreemap",dbMap);
		this.getFormHM().put("chart_type", viewtype);
		this.getFormHM().put("chartTitle" ,snameplay);	
	}

	private void getSformula(String statid){
		SformulaXml xml = new SformulaXml(this.frameconn,statid);
		List list = xml.getAllChildren();
		ArrayList sformulalist = new ArrayList();
//		CommonData cd = new CommonData("",ResourceFactory.getProperty("kq.formula.count"));
//		sformulalist.add(cd);
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Element element = (Element)list.get(i);
				CommonData cd= new CommonData(element.getAttributeValue("id"),element.getAttributeValue("title"));
				if (!"1".equals(element.getAttributeValue("del"))) {
					sformulalist.add(cd);
				}
			}
			this.getFormHM().put("sformulalist", sformulalist);
			if (sformulalist.size() > 1) {
				this.getFormHM().put("showsformula", "1");
			} else {
				this.getFormHM().put("showsformula", "0");
			}
		}else{
			this.getFormHM().put("showsformula", "0");
		}
	}
}
