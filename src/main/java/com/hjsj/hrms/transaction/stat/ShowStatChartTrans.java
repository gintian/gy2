/*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowStatChartTrans extends IBusiness{
	public ShowStatChartTrans(){
	   super();
	}
	/**
	 * 解决重复请求，后台报临时表问题
	 * @return
	 */
	private boolean repeatReq(){
		HashMap map = (HashMap) this.getFormHM().get("repeatReq");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		if(map == null){//第一次请求
			map = new HashMap();
			map.put("repeat_statid", hm.get("statid"));
			map.put("repeat_time", System.currentTimeMillis());
			this.getFormHM().put("repeatReq", map);
			return false;
		}
		Long time = (Long) map.get("repeat_time");
		Long reqTime = System.currentTimeMillis();
		if(reqTime-time<1000){//请求间隔1秒
			String statid = (String) map.get("repeat_statid");
			if( ((String)hm.get("statid")).equalsIgnoreCase(statid)){
				map.put("repeat_time", System.currentTimeMillis());
				this.getFormHM().put("repeatReq", map);
				return true;
			}
		}
		map.put("repeat_statid", hm.get("statid"));
		map.put("repeat_time", System.currentTimeMillis());
		this.getFormHM().put("repeatReq", map);
		return false;
	}
	
	/*  ( non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		if(repeatReq())//针对临时表情况重复请求后台报临时表错误问题处理  wangb 20180730 bug 39132
			return;
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		// TODO Auto-generated method stub
		GeneralQueryStat generalstat=new GeneralQueryStat();
		int[] statvalues=null;
		double[] statvaluess=null;
       	String[] fieldDisplay;        
		String SNameDisplay;
		String userbases = (String)this.getFormHM().get("userbases");//Usr`Ret`Oth`Trs
		userbases=userbases==null?"":userbases;
		userbases=userbases.toUpperCase();
		String userbase=(String)this.getFormHM().get("userbase");
		String statId=(String)this.getFormHM().get("statid");	
		if (null != statId && !this.userView.isHaveResource(IResourceConstant.STATICS, statId))
        {
            statId = "-1";
        }
		String statenter = (String)hm.get("statenter");
		hm.remove("statenter");
		statenter = statenter==null ?"":statenter;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(!"true".equals(statenter)){
			String sql1="select * from sname where id='"+statId+"'";
			dao=new ContentDAO(this.getFrameconn());
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
		}
		generalstat.insertCount(statId, this.frameconn);
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
		//CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
		//statId=checkPrivSafeBo.checkResource(IResourceConstant.STATICS, statId);
		String querycond=(String)this.getFormHM().get("querycond");//组织机构
		String infokind=(String)this.getFormHM().get("infokind");	  
	    String isshowstatcond=(String)this.getFormHM().get("isshowstatcond");
	    String[] curr_id=(String[])this.getFormHM().get("curr_id");
	    if("1".equalsIgnoreCase(org_filter)) {
	    	curr_id = null;
	    }
	    String preresult=(String)this.getFormHM().get("preresult");	 
	    String sformula = (String)this.getFormHM().get("sformula");
	    sformula = sformula ==null?"":sformula;
	    if(preresult==null||preresult.length()<=0)
	    	preresult="2";
	    if("1".equals(preresult))
	    	curr_id=null;
	    if(curr_id!=null&&curr_id.length>0)
	    {
	    	if(curr_id[0]!=null&& "#".equals(curr_id[0]))
	    		curr_id=null;
	    }
	    if(curr_id==null&&infokind!=null&& "1".equals(infokind))
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
	    String result=(String)this.getFormHM().get("result");
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
	    
	    
	    if (sformula.length() == 0) {
			SformulaXml xml3 = new SformulaXml(this.frameconn,statId);
			sformula = xml3.getFistSformula();
		}
	    
	    /*if(this.userView.getStatus()!=0)
	    	isresult=true; */
        //加上常用查询进行的统计
	    String commlexr=null;
	    String commfacor=null;
		
	    
		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.getFrameconn());
	    
	    if(curr_id!=null)
	    {
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    	history = generalstat.getHistory();
	    }
	    this.getFormHM().put("isshowstatcond",isshowstatcond);
	    ArrayList list=new ArrayList();	   
	    StatDataEncapsulation simplestat=new StatDataEncapsulation();
	    
	    
	    if("1".equalsIgnoreCase(org_filter)){//按机构筛选 逻辑  一维统计 wangb 2019-08-19
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

	    //liubq start
	    String archive_set="";
	    String viewtype="12";
	    StringBuffer sql1 =new StringBuffer();
		sql1.append("select archive_set,viewtype from SName where id=");
		sql1.append(statId);
		List rs1 =ExecuteSQL.executeMyQuery(sql1.toString());
		if (!rs1.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs1.get(0);
			archive_set = rec.get("archive_set")!=null?rec.get("archive_set").toString():"";
			viewtype = rec.get("viewtype")!=null?rec.get("viewtype").toString():"";
		}
		
	    if("42".equals(viewtype)|| "43".equals(viewtype)|| "44".equals(viewtype)){
			ArrayList valvesList = new ArrayList();
			valvesList=getValve(userbases,userbase.toUpperCase(), statId, querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn);
			if(valvesList.size()==3){
				if(valvesList.get(0)!=null&&valvesList.get(1)!=null&&valvesList.get(2)!=null&&("42".equals(viewtype)|| "43".equals(viewtype)|| "44".equals(viewtype))){
				this.getFormHM().put("minvalue",valvesList.get(0));
				this.getFormHM().put("maxvalue",valvesList.get(1));
				this.getFormHM().put("valves",valvesList.get(2));
				}else{
					ArrayList minv = new ArrayList();
					minv.add("0");
					this.getFormHM().put("minvalue",minv);
					this.getFormHM().put("maxvalue",minv);
					this.getFormHM().put("valves",minv);
				}
			}else{
				ArrayList minv = new ArrayList();
				minv.add("0");
				ArrayList maxv = new ArrayList();
				maxv.add("0");
				ArrayList valv = new ArrayList();
				valv.add("0");
				this.getFormHM().put("minvalue",minv);
				this.getFormHM().put("maxvalue",maxv);
				this.getFormHM().put("valves",valv);
			}
		}
	    
	    //liubq  end
	    
	    
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
	    
	   
		
	    try {
	        if(userbases==null||userbases.trim().length()==0){
	            String onlychart = (String) this.getFormHM().get("onlychart");
                if(onlychart != null && onlychart.length() > 0){
                    dao = new ContentDAO(this.frameconn);
                    this.frowset = dao.search("select nbase from sname where id='" + statId +"'");
                    if(this.frowset.next())
                        userbases = this.frowset.getString("nbase");
                    
                    userbases = userbases.replaceAll(",", "`");
                }
	        }
	        
	    	if(userbases==null||userbases.trim().length()==0){
	    		userbases = userbase;
	    		this.getFormHM().put("userbases", userbase);
				/*ContentDAO dao = new ContentDAO(this.frameconn);
				try {
					this.frowset = dao.search("select dbname from DBName where upper(pre)='"+userbase.toUpperCase()+"'");
					if(this.frowset.next()){
						this.getFormHM().put("viewuserbases", this.frowset.getString("dbname"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}*/
	    		this.getFormHM().put("viewuserbases", AdminCode.getCodeName("@@", userbase.toUpperCase()));
				if(sformula.length()>0)
					statvaluess =simplestat.getLexprDataSformula(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn);
				else
					statvalues =simplestat.getLexprData(userbase, Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history);
	    	}else{
	    		String [] nbases = userbases.split("`");
	    		if(userbase == null || userbase.length() < 1)
	    		    userbase = nbases[0];
	    		
	    		StringBuffer viewbase = new StringBuffer();
	    		for(int i=0;i<nbases.length;i++){
	    			viewbase.append(";"+AdminCode.getCodeName("@@", nbases[i].toUpperCase()));
	    		}
	    		if(viewbase.length()>1)
	    		this.getFormHM().put("viewuserbases",viewbase.substring(1));
	    		if(sformula.length()>0)
	    			statvaluess =simplestat.getLexprDataSformula(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases,sformula,this.frameconn);
	    		else
	    			statvalues =simplestat.getLexprData(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases);
	    	}
	    	SNameDisplay = simplestat.getSNameDisplay();
		    if ((sformula.length()==0&&statvalues != null && statvalues.length > 0)||(sformula.length()>0&&statvaluess != null && statvaluess.length > 0)) 
		    {
			   fieldDisplay = simplestat.getDisplay();
			   int statTotal = 0;
			   double statTotals = 0.0;
			   if(sformula.length()==0){
				   for (int i = 0; i < statvalues.length; i++) 
				   {
					   CommonData vo=new CommonData();
					   String str=fieldDisplay[i];
					   vo.setDataName(str);
					   if(sformula.length()==0)
						   vo.setDataValue(String.valueOf(statvalues[i]));
					   else
						   vo.setDataValue(String.valueOf(statvaluess[i]));
					  list.add(vo);
					  if(sformula.length()==0)
						  statTotal += statvalues[i];
					  else
						  statTotals += statvaluess[i];
				   }
			   }else{
				   for (int i = 0; i < statvaluess.length; i++) 
				   {
					   CommonData vo=new CommonData();
					   String str=fieldDisplay[i];
					   vo.setDataName(str);
					   if(sformula.length()==0)
						   vo.setDataValue(String.valueOf(statvalues[i]));
					   else
						   vo.setDataValue(String.valueOf(statvaluess[i]));
					  list.add(vo);
					  if(sformula.length()==0)
						  statTotal += statvalues[i];
					  else
						  statTotals += statvaluess[i];
				   } 
			   }
		       this.getFormHM().put("snamedisplay",SNameDisplay);
		       this.getFormHM().put("list",list);
		     }else
		     {
		    	    StringBuffer sql =new StringBuffer();
					sql.append("select * from SName where id=");
					sql.append(statId);
					List rs =ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
					}
					CommonData vo=new CommonData();
					vo.setDataName("");
					vo.setDataValue("0");
					list.add(vo);
					this.getFormHM().put("snamedisplay",SNameDisplay);
		    	    this.getFormHM().put("list",list);
		     }
		     HashMap jfreemap=new HashMap();
			 jfreemap.put(SNameDisplay, list);
			 this.getFormHM().put("jfreemap" ,jfreemap);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new GeneralException("", e.toString(),"", "");
		}
		
	    this.getFormHM().put("queryconde",this.getFormHM().get("querycond"));//特殊处理，按组织机构过滤不走  设置统计范围 值原封还原
	    this.getFormHM().put("archive_set",archive_set);
	    this.getSformula(statId);
	    this.getFormHM().put("sformula", sformula);
	    this.getFormHM().put("chart_type", viewtype);//支持默认图形
	    String xangle=AnychartBo.computeXangle(list);
	    this.getFormHM().put("xangle", xangle);
	    
	    //zxj 20150624 暂不控制大于15个不显示label，在前台增加选项
	    this.getFormHM().put("label_enabled", list.size()<10000?"true":"false");
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
	
	/**
	 * 获取各个阀值
	 * 
	 * valvesList
	 * get（0） String 最小值
	 *  get（1） String 最大值
	 * get（2） ArrayList   阀值
	 *	 
	 * 
	 * */
	private ArrayList getValve(
			String userbases,
			String userbase,
			String statId,
			String querycond,
			String username,
			String manageprive,
			UserView userView,
			String infokind,
			boolean isresult,
			String commlexr,
			String commfacor,
			String preresult,
			String history,
			String sformula,
			Connection conn){
		String valvetype ="0";
		ArrayList valvesList = new ArrayList();
		ArrayList valves = new ArrayList();
		int[] statvalues=null;
		double[] statvaluess=null;
		StringBuffer sql =new StringBuffer();
		sql.append("select valve from SLegend where id=");
		sql.append(statId);
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
	    StatDataEncapsulation simplestat=new StatDataEncapsulation();
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			String valve = rec.get("valve")!=null?rec.get("valve").toString():"";
			if(valve==null|| "".equals(valve)||valve==""){
			}else{
			try {
				Document doc=PubFunc.generateDom(valve.toString());
				Element et = doc.getRootElement();;
				ArrayList minvalue = new ArrayList();
				minvalue.add(((Element)et.getChild("minValue")).getText());
				ArrayList maxvalue = new ArrayList();
				maxvalue.add(((Element)et.getChild("maxValue")).getText());
				List valveList = et.getChild("valves").getChildren();
				for(int i=0;i<valveList.size();i++){
					valves.add(((Element)valveList.get(i)).getText());
				}
				valvesList.add(minvalue);
				valvesList.add(maxvalue);
				valvesList.add(valves);
				
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
			if(userbases==null||userbases.length()==0){
	    		if(userbase==null||userbase.length()==0){
	    			
	    		}
	    		this.getFormHM().put("userbases", userbase);
				/*ContentDAO dao = new ContentDAO(this.frameconn);
				try {
					this.frowset = dao.search("select dbname from DBName where upper(pre)='"+userbase.toUpperCase()+"'");
					if(this.frowset.next()){
						this.getFormHM().put("viewuserbases", this.frowset.getString("dbname"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}*/
	    		this.getFormHM().put("viewuserbases", AdminCode.getCodeName("@@", userbase.toUpperCase()));
				if(sformula.length()>0)
					statvaluess =simplestat.getLexprDataSformula(userbase, Integer.parseInt(statId), querycond, username, manageprive, userView, infokind, isresult, commlexr, commfacor, preresult, history, sformula, this.frameconn, valvetype, valves, userbases,"");//getLexprDataSformula(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn,valvetype,valves);
				else
					statvalues =simplestat.getLexprData(userbase, Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history);
	    	}else{
	    		String [] nbases = userbases.split("`");
	    		if(userbase == null || userbase.length() < 1)
	    		    userbase = nbases[0];
	    		
	    		StringBuffer viewbase = new StringBuffer();
	    		for(int i=0;i<nbases.length;i++){
	    			viewbase.append(";"+AdminCode.getCodeName("@@", nbases[i].toUpperCase()));
	    		}
	    		if(viewbase.length()>1)
	    		this.getFormHM().put("viewuserbases",viewbase.substring(1));
	    		if(sformula.length()>0)
					statvaluess =simplestat.getLexprDataSformula(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases,sformula,this.frameconn,valvetype,valves,"");
				else
	    			statvalues =simplestat.getLexprData(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases);
	    	}
			valves.add(statvaluess[0]+"");
	    	} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			}
		}
		return valvesList;
	}
	
}
