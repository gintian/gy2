package com.hjsj.hrms.module.statistical.businessobject.impl;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;
import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.ShowTowCrossAction;
import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.statistical.businessobject.StatisticalService;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class StatisticalServiceImpl implements StatisticalService{

	private static Category log = Category.getInstance(StatisticalServiceImpl.class.getName());
	
	private Connection conn;

	public StatisticalServiceImpl(){}

	public StatisticalServiceImpl(Connection conn){
		this.conn = conn;
	}

	@Override
	public List listAllStatisial(UserView userView)  throws GeneralException{
		ArrayList dataList = new ArrayList();
		List<String> groupList = new ArrayList<String>();
		Map<String,List> dataMap = new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append("select id,name,type,categories,photo,org_filter from sname where infokind = 1 ");
		sql.append(" order by snorder ");
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while(rs.next()){
				int id = rs.getInt("id");
				if(userView.isSuper_admin() || userView.isHaveResource(IResourceConstant.STATICS,String.valueOf(id))){//获取权限范围内的人员 常用统计
					String name = rs.getString("name");//统计名称
					String type = rs.getString("type");//代表当前是几维统计 type=1 一维; type=2 二维; type=3 多维
					String categories = rs.getString("categories");//分类名称
					String photo = rs.getString("photo");//统计图标
					int org_filter = rs.getInt("org_filter");//按组织机构筛选参数
					if(StringUtils.isBlank(categories) || "null".equalsIgnoreCase(categories))//未分组，默认值为 others
						categories = "others";
					if(!groupList.contains(categories))
						groupList.add(categories);

					HashMap map = new HashMap();
					map.put("id", PubFunc.encrypt(String.valueOf(id)));
					map.put("infokind", 1);//infokind 代表统计分析的类型  1：人员  2：单位  3：岗位
					map.put("name", name);
					map.put("stattype", type);
					map.put("photo", photo);
					map.put("org_filter", org_filter);
					List list = null;
					if(dataMap.containsKey(categories)){
						list = dataMap.get(categories);
						list.add(map);
					}else{
						list = new ArrayList();
						list.add(map);
						dataMap.put(categories, list);
					}
				}
			}
			for(int i = 0 ; i < groupList.size() ; i++){
				String categories = groupList.get(i);
				List list = dataMap.get(categories);
				HashMap map = new HashMap();
				map.put("categoryName", categories);
				map.put("groupList", list);
				dataList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取人员常用统计出错
			log.error(ResourceFactory.getProperty("static.error.getpersonstaticmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getpersonstaticmsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return dataList;
	}

	@Override
	public List getStatisticalIconName(String realPath) throws GeneralException {
		List<String> iconNameList = new ArrayList<String>();

		if(realPath.endsWith("/")){//最后一位有可能是/
			realPath = realPath.substring(0,realPath.length()-1);
		}
		String url = realPath+"/images/statistical";
		File file = new File(url);
		File[] fileList = file.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				String fileName = fileList[i].getName();
				iconNameList.add(fileName);
			}
		}
		return iconNameList;
	}
	
	@Override
	public Map getStatisicalChartData(UserView userView, String statid, String infokind, String sformulaId, String org_filter,String filterId) throws GeneralException {
		Map dataHM = new HashMap();
		
		Map snameHM = this.getSname(statid, infokind);//获取统计条件 数据
		dataHM.put("title", snameHM.get("title"));
		String flag = (String) snameHM.get("flag"); //是否查询结果集    1 是  2否
		String chartType = (String) snameHM.get("viewtype");//统计图类型
		String statType = (String) snameHM.get("type");//统计图类型
		String nbases = (String) snameHM.get("nbase");//人员库
		String[] condid = null; //分类统计条件
		
		if(snameHM.get("condid") != null && ((String)snameHM.get("condid")).trim().length() > 0)
			condid = new String[]{((String)snameHM.get("condid")).split(",")[0]};
		
		//获取统计图类型
		if("11".equalsIgnoreCase(chartType))//柱状图
			chartType = "1";
		else if("1000".equalsIgnoreCase(chartType))//折现图
			chartType = "2";
		else if("55".equalsIgnoreCase(chartType))//雷达图
			chartType = "3";
		else if("20".equalsIgnoreCase(chartType))//饼图
			chartType = "4";
		else
			chartType = "1";//默认柱状图
		
		dataHM.put("chart_type", chartType);

		//统计方式处理
		SformulaXml xml = new SformulaXml(this.conn,PubFunc.decrypt(statid));
		String sformula = "";
		if(sformulaId == null || sformulaId.trim().length() == 0)
			sformula = xml.getFistSformula();
		else
			sformula = sformulaId;
		dataHM.put("sformula", sformula);
		
		ArrayList staticTypeList = new ArrayList();//获取统计方式集合 
		List sformulaList = xml.getAllChildren();
		if(sformulaList != null && sformulaList.size()>0){
			for(int i=0;i<sformulaList.size();i++){
				Element element = (Element)sformulaList.get(i);
				if (!"1".equals(element.getAttributeValue("del"))) {
					HashMap map = new HashMap();
					map.put("sformula_id", element.getAttributeValue("id"));
					map.put("title", element.getAttributeValue("title"));
					map.put("type", element.getAttributeValue("type"));
					staticTypeList.add(map);
				}
			}
		}
		dataHM.put("static_type",staticTypeList);
		
		String[] curr_id = null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
    	{
    		String[] stat_ids=new String[1];
    		stat_ids[0]=stat_id;
    		curr_id=stat_ids;	    		
    	}else{
    		curr_id=condid;
    	}
    	boolean isresult = true;
		if(flag != null && "1".equalsIgnoreCase(flag))
			isresult = false;
		Map chartDataHM = this.getStatisticalLengend(userView, PubFunc.decrypt(statid), "",nbases,sformula,infokind,curr_id,"0",isresult,org_filter,filterId);
		String filterName = (String) chartDataHM.get("filterName");
		chartDataHM.remove("filterName");
		filterId = (String) chartDataHM.get("filterId");
		chartDataHM.remove("filterId");
		dataHM.put("chartData", chartDataHM);
		dataHM.put("org_filter", org_filter);
		dataHM.put("filterId", filterId);
		dataHM.put("filterName", filterName);
		return dataHM;
	}
	
	@Override
	public Map getStatisicalPersonList(UserView userView, String statid, String infokind, String showLegend,
			int pageIndex, int pageSize,String filterId) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ArrayList sqlList = new ArrayList();
		sql.append("select norder,lExpr,Factor,flag from SLegend where id=? and legend=? ");
		sqlList.add(PubFunc.decrypt(statid));
		String legend=SafeCode.decode(showLegend);
		legend=legend.replaceAll("\n", "");
		legend=legend.replaceAll("\r", "");
		sqlList.add(PubFunc.decrypt(legend));
		//sqlList.add(legend);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String strlexpr = "";
		String strfactor = "";
		String history = "";
		try {
			rs = dao.search(sql.toString(), sqlList);

			if(rs.next()){
				strlexpr = rs.getString("lexpr")!=null?rs.getString("lexpr"):"";
				strfactor = rs.getString("factor")!=null?rs.getString("factor"):"";
				history = rs.getString("flag")!=null?rs.getString("flag"):"";
			}else{
				//获取统计项出错
				log.error(ResourceFactory.getProperty("static.error.getstaticitemmsg"));
				throw new GeneralException(ResourceFactory.getProperty("static.error.getstaticitemmsg"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取统计项出错
			log.error(ResourceFactory.getProperty("static.error.getstaticitemmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getstaticitemmsg"));
		}
		Map snameHM = this.getSname(statid, infokind);
		String flag = (String) snameHM.get("flag"); //是否查询结果集    1 是  2否
		String nbases = (String) snameHM.get("nbase");//人员库

		String[] condid = null; //分类统计条件
		if(snameHM.get("condid") != null && ((String)snameHM.get("condid")).trim().length() > 0)
			condid = new String[]{((String)snameHM.get("condid")).split(",")[0]};
		String[] curr_id = null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
    	{
    		String[] stat_ids=new String[1];
    		stat_ids[0]=stat_id;
    		curr_id=stat_ids;	    		
    	}else{
    		curr_id=condid;
    	}
    	//是否查询结果集表数据   true 不查  false 查
    	boolean isresult = true;
		if(flag != null && "1".equalsIgnoreCase(flag))
			isresult = false;
		
		strfactor=strfactor+"`";
		//【9397】员工管理-统计分析-常用统计中的123的可以统计出来是3个，但是反查进去就是空的 jingq add 2015.05.06
		strfactor = PubFunc.keyWord_reback(strfactor);
		
		//是否查询历史记录    false 不查   true 查询
		boolean ishavehistory=false;
        //zxj 20150604 不论人员、单位还是岗位(infokind 1 2 3)都有“历史记录”选项
        if(history!=null&& "1".equals(history))
            ishavehistory=true;
		
		
        String userbase = "";
		if(nbases == null || nbases.trim().length() == 0){
			nbases = "USR";
			userbase = nbases;
		}else{
			String[] userbases = nbases.split(",");
			userbase  = userbases[0].toUpperCase();
		}
		if(curr_id != null) {
			if(!checkCurrId(curr_id)) {
				curr_id = null;
			}
		}
        //常用查询进行的统计
		GeneralQueryStat generalstat=new GeneralQueryStat();
		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.conn);
		String commlexr=null;
	    String commfacor=null;
		if(curr_id!=null)
	    {
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    }
		if(StringUtils.isNotBlank(filterId)){
			String orgFactor = "";
			String orgLexpr = "1";
			if(AdminCode.getCode("UN", filterId)!=null){
				orgFactor = "b0110="+filterId+"*`";
			}
			if(AdminCode.getCode("UM", filterId)!=null){
				orgFactor = "e0122="+filterId+"*`";
			}
			
			if(StringUtils.isNotBlank(orgFactor)){
				orgLexpr = "1";
			}
			if(commlexr !=null && commlexr.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
				String[] style = new StatDataEncapsulation().getCombinLexprFactor(orgLexpr, orgFactor, commlexr, commfacor);
				commlexr = style[0];
				commfacor = style[1];
			}else{
				commlexr = orgLexpr;
				commfacor = orgFactor;
			}
		}
		if(commlexr !=null && commlexr.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
			// CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 
			commfacor = commfacor.replaceAll("\\|", "~");
			String[] style=new StatDataEncapsulation().getCombinLexprFactor(strlexpr,strfactor,commlexr,commfacor);
			if(style!=null && style.length==2)
			{
				strlexpr=style[0];
				strfactor=style[1];
			}
		}
	    StatCondAnalyse cond = new StatCondAnalyse();
	    String wheresql=cond.getCondQueryString(strlexpr,strfactor,userbase,ishavehistory,userView.getUserName(),"",userView,infokind,isresult,false);
	    if(wheresql.contains("本单位")){
	    	String sss = userView.getUnit_id();
	    	sss=sss.replace("UN","");
	    	if(sss.length()==1||sss.length()==0){
	    		wheresql=wheresql.replace("='本单位'", "<>null");
	    		wheresql=wheresql.replace("<>'本单位'", "=null");
	    	}else{
	    		sss=sss.substring(0,sss.length()-1);
	    		sss=sss.replace("`", ",");
	    		wheresql=wheresql.replace("='本单位'", " in("+sss+")");
	    		wheresql=wheresql.replace("<>'本单位'", " not in("+sss+")");
	    	}
	    }
	    ArrayList mainlist= getStatItemList(userView,strfactor,infokind);
	    StringBuffer strsql = new StringBuffer();
        strsql.append("select distinct ");
        strsql.append(userbase);
        strsql.append("a01.a0100 as a0100,");
        strsql.append("## as db,");
        strsql.append("a0000 as a0000,");
        strsql.append(userbase);        
        strsql.append("a01.b0110 as b0110,");
        strsql.append(userbase);
        strsql.append("a01.e0122 as e0122,");
        strsql.append(userbase);
        strsql.append("a01.e01a1 as e01a1");
        String columns=getMainQueryFields(mainlist,infokind); 
        columns=(","+columns.toLowerCase()).replaceAll(",e01a1", "").replaceAll(",e0122", "").replaceAll(",b0110", "").replaceAll(",a0100", "");
        strsql.append(columns);
        strsql.append(",UserName ");
		String tmpsql =(strsql.toString()+wheresql);//+" order by db,A0000 "/*.toUpperCase()*/;
        StringBuffer sb = new StringBuffer();
        //tiany add 支持全部人员库统计图穿透
        if(nbases!=null&&nbases.length()>0){
	        if(nbases.indexOf(",")==-1){
	        	nbases = ("".equals(nbases.trim()))? userbase:nbases;//没有设置人员库条件处理 wangb 20180822
				sb.append(" from ("+tmpsql.replaceAll(userbase, nbases).replaceAll("##", "'"+getStart(0)+nbases+"'")+"");
			}else{
				String[] tmpdbpres=nbases.split(",");
				ArrayList dbPrilist = userView.getPrivDbList();
				String dbPri = ","+StringUtils.join(dbPrilist.toArray(new String[dbPrilist.size()]),",")+",";
				for(int n=0;n<tmpdbpres.length;n++){
					String tmpdbpre=tmpdbpres[n];
					if(dbPri.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1/*!dbPrilist.contains(tmpdbpre)*/)//过滤 人员库不存在情况  wangb 20180717 bug 38893
						continue;
					if(tmpdbpre.length()==3){
						if(sb.length()>0){
							sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
						}else{
							sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
						}
					}
				}
			}
        }else{
        	sb.append(" from ("+tmpsql.replaceAll("##", "'"+getStart(0)+userbase+"'")+"");
        }
        if(Sql_switcher.searchDbServerFlag() == 2){
        	wheresql=sb.toString()+") tt order by db,A0000 ";
        }else{
        	wheresql=sb.toString()+") tt ";
        }
        strsql.setLength(0);
        strsql.append("select a0000,");
        strsql.append("a0100,");
        strsql.append("b0110,");
        strsql.append("e0122,");
        strsql.append("e01a1");
        strsql.append(columns);
        strsql.append(",UserName,db ");
        
        //获取统计项总数
        int num = 0;
        try {
			rs = dao.search("select count(1) num "+ wheresql);
			if(rs.next()){
				Object o = rs.getObject("num");
				if(o instanceof BigDecimal) {
					num = rs.getBigDecimal("num").intValue();
				}else {
					num = rs.getInt("num");
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			//获取穿透总人数出错
			log.error(ResourceFactory.getProperty("static.error.getthroughpersontotalmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getthroughpersontotalmsg"));
		}
        
        
        if(0 == num){//人员数为 0时
        	Map dataHM = new HashMap();
            dataHM.put("infoList", null);
            dataHM.put("total", 0);
            dataHM.put("page", pageIndex);
     		return dataHM;
        }
        
        sql.setLength(0);
        if(Sql_switcher.searchDbServerFlag() ==2){//oracle 库
        	sql.append(strsql.toString()+" from ( \n");
        	sql.append("  "+ strsql.toString()+",rownum rn "+wheresql+" \n");
        	sql.append(") ttt where ttt.rn BETWEEN "+((pageIndex-1)*pageSize+1)+" and "+(pageIndex*pageSize));
        }else{
			sql.append(strsql.toString().replace("select","select top "+pageSize+" ")+" " + wheresql+"\n ");
			sql.append("where not exists(\n");
			sql.append("  select * from (\n");
			sql.append("    select top "+((pageIndex-1)*pageSize)+" * "+wheresql+" ");
			sql.append("  ) t2 where tt.a0100 = t2.a0100 and tt.db = t2.db \n");
			sql.append(") order by db,A0000 ");
		}

        ArrayList infoList = new ArrayList();
        try {
			rs = dao.search(sql.toString());
			while(rs.next()){//只写了获取 人员  姓名  部门 岗位 头像  字段，  统计条件字段  移动端没涉及未取   
				HashMap map = new HashMap();
				
				String photo = this.getPicUrl(userView, rs.getString("db").substring(1),rs.getString("a0100"));
				if(photo != null)
					map.put("photo", photo);
				else
					map.put("photo", "/images/photo.jpg");
				map.put("nbase", PubFunc.encrypt(rs.getString("db").substring(1)));
				map.put("A0100", PubFunc.encrypt(rs.getString("a0100")));
				map.put("A0101", rs.getString("a0101"));
				String E0122 = rs.getString("E0122");
				String E01A1 = rs.getString("E01A1");
				CodeItem E0122Code = AdminCode.getCode("UM", E0122,5);
				if(E0122Code != null){
					map.put("E0122", AdminCode.getCode("UN", E0122Code.getPcodeitem(), 0).getCodename() +"/"+E0122Code.getCodename().substring(E0122Code.getCodename().lastIndexOf("/")+1));
				}else{
					map.put("E0122", "");
				}
				CodeItem E01A1Code = AdminCode.getCode("@K", E01A1);
				map.put("E01A1", E01A1Code!=null? E01A1Code.getCodename():"");
				infoList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取统计项人员信息出错
			log.error(ResourceFactory.getProperty("static.error.getitempersoninfomsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getitempersoninfomsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}

        Map dataHM = new HashMap();
        dataHM.put("infoList", infoList);
        dataHM.put("total", num);
        dataHM.put("page", pageIndex);
		return dataHM;
	}	
	
	@Override
	public Map getStatisicalPersonList(UserView userView, String statid, String infokind, String v, String h,
			int pageIndex, int pageSize,String filterId) throws GeneralException {
		String querycond ="";//查询sql 目前使用场景待定
		String preresult = "2";//目前使用场景待定     2 常用查询    1 查询结果  待定
		String history = null;//目前使用场景待定  查询历史 
		
		Map snameHM = this.getSname(statid, infokind);
		String flag = (String) snameHM.get("flag"); //是否查询结果集    1 是  2否
		String nbases = (String) snameHM.get("nbase");//人员库
		
		String[] condid = null; //分类统计条件
		if(snameHM.get("condid") != null && ((String)snameHM.get("condid")).trim().length() > 0)
			condid = new String[]{((String)snameHM.get("condid")).split(",")[0]};
		String[] curr_id = null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
    	{
    		String[] stat_ids=new String[1];
    		stat_ids[0]=stat_id;
    		curr_id=stat_ids;	    		
    	}else{
    		curr_id=condid;
    	}
		
    	String userbase = "";
		if(nbases == null || nbases.trim().length() == 0){
			nbases = "USR";
			userbase = nbases;
		}else{
			String[] userbases = nbases.split(",");
			userbase  = userbases[0].toUpperCase();
		}
		
		//是否查询结果集表数据   true 不查  false 查
    	boolean isresult = true;
		if(flag != null && "1".equalsIgnoreCase(flag))
			isresult = false;
		
		//常用查询进行的统计
		//加上常用查询进行的统计
	    String commlexr=null;
	    String commfacor=null;
	    if(curr_id != null) {
			if(!checkCurrId(curr_id)) {
				curr_id = null;
			}
		}
		GeneralQueryStat generalstat=new GeneralQueryStat();
		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,null,this.conn);// history 值 先为 null，具体使用场景待定
		if(curr_id!=null)
	    {
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    }
		if(StringUtils.isNotBlank(filterId)){
			String orgFactor = "";
			String orgLexpr = "1";
			if(AdminCode.getCode("UN", filterId)!=null){
				orgFactor = "b0110="+filterId+"*`";
			}
			if(AdminCode.getCode("UM", filterId)!=null){
				orgFactor = "e0122="+filterId+"*`";
			}
			
			if(StringUtils.isNotBlank(orgFactor)){
				orgLexpr = "1";
			}
			if(commlexr !=null && commlexr.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
				String[] style = new StatDataEncapsulation().getCombinLexprFactor(orgLexpr, orgFactor, commlexr, commfacor);
				commlexr = style[0];
				commfacor = style[1];
			}else{
				commlexr = orgLexpr;
				commfacor = orgFactor;
			}
		}
	    StatCondAnalyse cond = new StatCondAnalyse();
	    String sql=new StatDataEncapsulation().getDataSQL(Integer.parseInt(PubFunc.decrypt(statid)),userbase,querycond,Integer.parseInt(v),Integer.parseInt(h),userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history);
	    StringBuffer strsql=new StringBuffer();
    	ArrayList mainlist=getMainFieldList(userView,infokind);
	    String columns=getMainQueryFields(mainlist);
	    
	    StringBuffer SQL = new StringBuffer();
	    RowSet rs = null;
	    ContentDAO dao = new ContentDAO(this.conn);
	    if("1".equals(infokind)){
	    	strsql.append("select distinct ");
	        strsql.append(userbase);
	        strsql.append("a01.a0100 as a0100,");
	        strsql.append("## as db,");
	        strsql.append("a0000 as a0000,");
	        strsql.append(userbase);        
	        strsql.append("a01.b0110 as b0110,");
            strsql.append(userbase);
            strsql.append("a01.e0122 as e0122,");
            strsql.append(userbase);
            strsql.append("a01.e01a1 as e01a1");
            
	        columns=(","+columns.toLowerCase()).replaceAll(",e01a1", "").replaceAll(",e0122", "").replaceAll(",b0110", "").replaceAll(",a0100", "");
	        strsql.append(columns);
	        strsql.append(",UserName ");
	        userbase=userbase.toUpperCase();
	        String tmpsql =(strsql.toString()+sql).toUpperCase();//+" order by db,A0000 ";
	        StringBuffer sb = new StringBuffer();
	        if(nbases!=null&&nbases.length()>0){
		        if(nbases.indexOf(",")==-1){
		        	nbases = ("".equals(nbases.trim()))? userbase:nbases;//没有设置人员库条件处理 wangb 20180822
					sb.append(" from ("+tmpsql.replaceAll(userbase, nbases).replaceAll("##", "'"+getStart(0)+nbases+"'")+"");
				}else{
					String[] tmpdbpres=nbases.split(",");
					ArrayList dbPrilist = userView.getPrivDbList();
					String dbPri = ","+StringUtils.join(dbPrilist.toArray(new String[dbPrilist.size()]),",")+",";
					for(int n=0;n<tmpdbpres.length;n++){
						String tmpdbpre=tmpdbpres[n];
						if(dbPri.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1/*!dbPrilist.contains(tmpdbpre)*/)//过滤 人员库不存在情况  wangb 20180717 bug 38893
							continue;
						if(tmpdbpre.length()==3){
							if(sb.length()>0){
								sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
							}else{
								sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
							}
						}
					}
				}
	        }else{
	        	sb.append(" from ("+tmpsql.replaceAll("##", "'"+getStart(0)+userbase+"'")+"");
	        }
	        sql=sb.toString()+") tt ";
	        strsql.setLength(0);
	        strsql.append("select a0000,");
	        strsql.append("a0100,");
	        strsql.append("b0110,");
            strsql.append("e0122,");
            strsql.append("e01a1");
	        //strsql.append(",");
	        strsql.append(columns);
	        strsql.append(",UserName,db ");
	    }
	    //获取统计项总数
        int num = 0;
        try {
			rs = dao.search("select count(1) num "+ sql);
			if(rs.next()){
				if(Sql_switcher.searchDbServerFlag() == 2){
					num = rs.getBigDecimal("num").intValue();
				}else{
					num = rs.getInt("num");
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			//获取穿透总人数出错
			log.error(ResourceFactory.getProperty("static.error.getthroughpersontotalmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getthroughpersontotalmsg"));
		}
        if(0 == num){//人员数为 0时
        	Map dataHM = new HashMap();
            dataHM.put("infoList", null);
            dataHM.put("total", 0);
            dataHM.put("page", pageIndex);
     		return dataHM;
        }
        if(Sql_switcher.searchDbServerFlag() ==2){//oracle 库
        	SQL.append(strsql.toString()+" from ( \n");
        	SQL.append("  "+ strsql.toString()+",rownum rn "+sql+" \n");
        	SQL.append(") ttt where ttt.rn BETWEEN "+((pageIndex-1)*pageSize+1)+" and "+(pageIndex*pageSize) +" ");
        	SQL.append(" order by db,A0100");
        }else{
			SQL.append(strsql.toString().replace("select","select top "+pageSize+" ")+" " + sql+"\n ");
			SQL.append("where not exists(\n");
			SQL.append("  select * from (\n");
			SQL.append("    select top "+((pageIndex-1)*pageSize)+" * "+sql+" ");
			SQL.append("  ) t2 where tt.a0100 = t2.a0100 and tt.db = t2.db \n");
			SQL.append(")  order by db,A0100");
		}
        ArrayList infoList = new ArrayList();
        try {
			rs = dao.search(SQL.toString());
			while(rs.next()){//只写了获取 人员  姓名  部门 岗位 头像  字段，  统计条件字段  移动端没涉及未取   
				HashMap map = new HashMap();
				
				String photo = this.getPicUrl(userView, rs.getString("db").substring(1),rs.getString("a0100"));
				if(photo != null)
					map.put("photo", photo);
				else
					map.put("photo", "/images/photo.jpg");
				map.put("nbase", PubFunc.encrypt(rs.getString("db").substring(1)));
				map.put("A0100", PubFunc.encrypt(rs.getString("a0100")));
				map.put("A0101", rs.getString("a0101"));
				String E0122 = rs.getString("E0122");
				String E01A1 = rs.getString("E01A1");
				CodeItem E0122Code = AdminCode.getCode("UM", E0122,5);
				if(E0122Code != null){
					map.put("E0122", AdminCode.getCode("UN", E0122Code.getPcodeitem(), 0).getCodename() +"/"+E0122Code.getCodename().substring(E0122Code.getCodename().lastIndexOf("/")+1));
				}else{
					map.put("E0122", "");
				}
				CodeItem E01A1Code = AdminCode.getCode("@K", E01A1);
				map.put("E01A1", E01A1Code!=null? E01A1Code.getCodename():"");
				infoList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取统计项人员信息出错
			log.error(ResourceFactory.getProperty("static.error.getitempersoninfomsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getitempersoninfomsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}

        Map dataHM = new HashMap();
        dataHM.put("infoList", infoList);
        dataHM.put("total", num);
        dataHM.put("page", pageIndex);
	    
		return dataHM;
	}
	
	
	@Override
    public Map getStatisicalDoubleChartData(UserView userView , String statid, String infokind, String sformulaId, String vtotal, String htotal, String org_filter, String filterId) throws GeneralException {
		Map dataHM = new HashMap();
		Map snameHM = this.getSname(statid, infokind);//获取统计条件 数据
		String flag = (String) snameHM.get("flag"); //是否查询结果集    1 是  2否
		String chartType = (String) snameHM.get("viewtype");//统计图类型
		String statType = (String) snameHM.get("type");//统计图类型
		String nbases = (String) snameHM.get("nbase");//人员库
		String[] condid = null; //分类统计条件
		String hv = (String) snameHM.get("hv");
		dataHM.put("title", snameHM.get("title"));
		//获取统计图类型
		if("299".equalsIgnoreCase(chartType))//分组柱状图
			chartType = "1";
		else if("11".equalsIgnoreCase(chartType))//分组折线图
			chartType = "2";
		else if("33".equalsIgnoreCase(chartType))//堆叠图
			chartType = "3";
		else
			chartType = "0";//默认面积折线图（移动端）
		
		dataHM.put("chart_type", chartType);
		
		if(snameHM.get("condid") != null && ((String)snameHM.get("condid")).trim().length() > 0)
			condid = new String[]{((String)snameHM.get("condid")).split(",")[0]};

		
		StringBuffer sql = new StringBuffer();
		ArrayList sqlList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		
		//统计方式处理
		SformulaXml xml = new SformulaXml(this.conn,PubFunc.decrypt(statid));
		String sformula = "";
		if(sformulaId == null || sformulaId.trim().length() == 0)
			sformula = xml.getFistSformula();
		else
			sformula = sformulaId;
		dataHM.put("sformula",sformula);
		ArrayList staticTypeList = new ArrayList();//获取统计方式集合 
		List sformulaList = xml.getAllChildren();
		if(sformulaList != null && sformulaList.size()>0){
			for(int i=0;i<sformulaList.size();i++){
				Element element = (Element)sformulaList.get(i);
				if (!"1".equals(element.getAttributeValue("del"))) {
					HashMap map = new HashMap();
					map.put("sformula_id", element.getAttributeValue("id"));
					map.put("title", element.getAttributeValue("title"));
					map.put("type", element.getAttributeValue("type"));
					staticTypeList.add(map);
				}
			}
		}
		dataHM.put("static_type",staticTypeList);
		
		//获取人员从常用查询条件 
		String[] curr_id = null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
    	{
    		String[] stat_ids=new String[1];
    		stat_ids[0]=stat_id;
    		curr_id=stat_ids;	    		
    	}else{
    		curr_id=condid;
    	}
		
    	boolean isresult = true;
		if(flag != null && "1".equalsIgnoreCase(flag))
			isresult = false;
    	
		GeneralQueryStat generalstat=new GeneralQueryStat();
		generalstat.insertCount(PubFunc.decrypt(statid), this.conn);
		
	    String userbase = "";
		if(nbases == null || nbases.trim().length() == 0){
			nbases = "USR";
			userbase = nbases;
		}else{
			String[] userbases = nbases.split(",");
			userbase  = userbases[0].toUpperCase();
		}
		
		//加上常用查询进行的统计
		String commlexr=null;
		String commfacor=null;
		if(curr_id != null) {
			if(!checkCurrId(curr_id)) {
				curr_id = null;
			}
		}
		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,null,this.conn);
		if(curr_id!=null)
	    {
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    }
		
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
		String filterName = "";
		//按机构筛选 逻辑  一维统计 wangb 2019-08-19
		if("1".equalsIgnoreCase(org_filter)){
	    	String orgFactor = "";
	    	String orgLexpr = "";
	    	if(StringUtils.isNotBlank(filterId)){
	    		if(AdminCode.getCode("UN", filterId)!=null){
    				orgFactor = "b0110="+filterId+"*`";
    				filterName = AdminCode.getCode("UN", filterId).getCodename();
    			}
    			if(AdminCode.getCode("UM", filterId)!=null){
    				orgFactor = "e0122="+filterId+"*`";
    				filterName = AdminCode.getCode("UM", filterId).getCodename();
    			}
	    	}else{
	    		if(userView.isSuper_admin()){
	    			filterId = "UN";
	    			filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");//组织机构
	    		}else{
	    			filterId = userView.getManagePrivCodeValue();
	    			if("UN".equalsIgnoreCase(userView.getManagePrivCode()) && filterId.trim().length() == 0){
	    				filterId = "UN";
	    			}
	    			if(!"UN".equalsIgnoreCase(filterId)){
	    				if("UN".equalsIgnoreCase(userView.getManagePrivCode())){
	    					if(AdminCode.getCode("UN", filterId)!=null){
	    	    				orgFactor = "b0110="+filterId+"*`";
	    	    				filterName = AdminCode.getCode("UN", filterId).getCodename();
	    	    			}
	    				}
	    				if("UM".equalsIgnoreCase(userView.getManagePrivCode())){
	    					if(AdminCode.getCode("UM", filterId)!=null){
	    	    				orgFactor = "e0122="+filterId+"*`";
	    	    				filterName = AdminCode.getCode("UM", filterId).getCodename();
	    	    			}
	    				}
	    			}else{
	    				filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    			}
	    		}
	    	}
	    	if(!"UN".equalsIgnoreCase(filterId)){
	    		if(StringUtils.isNotBlank(orgFactor)){
	    			orgLexpr = "1";
	    		}
	    		if(commlexr !=null && commlexr.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
	    			String[] style = simplestat.getCombinLexprFactor(orgLexpr, orgFactor, commlexr, commfacor);
	    			commlexr = style[0];
	    			commfacor = style[1];
	    		}else{
	    			commlexr = orgLexpr;
	    			commfacor = orgFactor;
	    		}
	    	}
	    }
		nbases = nbases.replaceAll(",","`");
		Map chartDataHM = this.getDoubleLexprDataSformula(Integer.parseInt(PubFunc.decrypt(statid)), userbase,"",userView.getUserName(),userView.getManagePrivCode(), userView, infokind, isresult, commlexr, commfacor, "2", null, nbases, sformula, conn, vtotal, htotal);
		
		dataHM.put("chartData",chartDataHM.get("chartData"));
		dataHM.put("table_data",chartDataHM.get("table_data"));
		dataHM.put("org_filter", org_filter);
		dataHM.put("filterId", filterId);
		dataHM.put("filterName", filterName);
		return dataHM;
	}
	
	@Override
	public Map getStatisicalMoreChartData(UserView userView, String statid, String infokind, String vtotal,
			String htotal,String vnull,String hnull,String org_filter, String filterId) throws GeneralException {
		String preresult = "2";// 1 查询结果  2 常用统计  目前场景是常用查询
		String sformula = ""; // 多维统计 不需要 统计方式
		ShowTowCrossAction showTowCrossAction = new ShowTowCrossAction();
		
		vtotal=vtotal==null||vtotal.length()==0?"0":vtotal;
		htotal=htotal==null||htotal.length()==0?"0":htotal;
		vnull=vnull==null||vnull.length()==0?"0":vnull;
		hnull=hnull==null||hnull.length()==0?"0":hnull;
		
		String name="",hv="",dbbase="",condid="",show_chart="";
		StringBuffer sql = new StringBuffer();
		ArrayList sqlList = new ArrayList();
		sql.append("select name,nbase,condid,HV,hide_empty_row,hide_empty_col,show_chart,show_sum_h,show_sum_v from SName where id=?");
		sqlList.add(PubFunc.decrypt(statid));
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			rs = dao.search(sql.toString(),sqlList);
			if(rs.next())
			{
				hv = rs.getString("HV");
				dbbase = rs.getString("nbase");
				condid = ","+rs.getString("condid")+",";
//				hnull = rs.getString("hide_empty_row");
//				vnull = rs.getString("hide_empty_col");
				show_chart = rs.getString("show_chart");
				name = rs.getString("name");
//				htotal = rs.getString("show_sum_h");
//				vtotal = rs.getString("show_sum_v");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//获取多维统计数据出错
			log.error(ResourceFactory.getProperty("static.error.getmorestaticdatamsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getmorestaticdatamsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}
		String[] hvList = hv.split("\\|");
		String crosswise ="1_"+hvList[0];
		crosswise=crosswise.replaceAll(",", ",2_");
		crosswise=crosswise.replaceAll(";", ",1_");
		String[] crosslist = crosswise.split(",");
		crosswise="";
		for (int i =crosslist.length-1; i >=0; i--) {
			crosswise+= crosslist[i]+",";
		}
		crosslist = crosswise.split(",");
		
		String lengthways ="1_"+hvList[1];
		lengthways=lengthways.replaceAll(",", ",2_");
		lengthways=lengthways.replaceAll(";", ",1_");
		String[] lengthlist = lengthways.split(",");
		lengthways="";
		for (int i =lengthlist.length-1; i >=0; i--) {
			lengthways+= lengthlist[i]+",";
		}
		lengthlist = lengthways.split(",");
		
		String statIdString = lengthways + crosswise;
		String[] statIdlist = statIdString.split(",");
		
		ArrayList lengthways_dimension_list =new ArrayList();
		lengthways_dimension_list = showTowCrossAction.getDimension(dao,lengthways);
		ArrayList crosswise_dimension_list =new ArrayList();
		crosswise_dimension_list = showTowCrossAction.getDimension(dao,crosswise);
		
		String userbases = dbbase;
		String userbase="Usr";
		if(StringUtils.isNotBlank(userbases))
			userbase = userbases.split(",")[0];
		
		//获取人员从常用查询条件
		String[] curr_id = null;
		if(condid.split(",").length > 0)
			curr_id = new String[]{condid.split(",")[0]};
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
    	{
    		String[] stat_ids=new String[1];
    		stat_ids[0]=stat_id;
    		curr_id=stat_ids;	    		
    	}
    	
    	// 加上常用查询进行的统计
		String commlexr = null;
		String commfacor = null;
		GeneralQueryStat generalstat = new GeneralQueryStat();
		String history = null;
		if(curr_id != null) {
			if(!checkCurrId(curr_id)) {
				curr_id = null;
			}
		}
		generalstat.getGeneralQueryLexrfacor(curr_id, userbase, history, this.conn);
		if(curr_id!=null)
	    {
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    }
		
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
		String filterName = "";
		//按机构筛选 逻辑  一维统计 wangb 2019-08-19
		if("1".equalsIgnoreCase(org_filter)){
	    	String orgFactor = "";
	    	String orgLexpr = "1";
	    	if(StringUtils.isNotBlank(filterId)){
	    		if(AdminCode.getCode("UN", filterId)!=null){
    				orgFactor = "b0110="+filterId+"*`";
    				filterName = AdminCode.getCode("UN", filterId).getCodename();
    			}
    			if(AdminCode.getCode("UM", filterId)!=null){
    				orgFactor = "e0122="+filterId+"*`";
    				filterName = AdminCode.getCode("UM", filterId).getCodename();
    			}
	    	}else{
	    		if(userView.isSuper_admin()){
	    			filterId = "UN";
	    			filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");//组织机构
	    		}else{
	    			filterId = userView.getManagePrivCodeValue();
	    			if("UN".equalsIgnoreCase(userView.getManagePrivCode()) && filterId.trim().length() == 0){
	    				filterId = "UN";
	    			}
	    			if(!"UN".equalsIgnoreCase(filterId)){
	    				if("UN".equalsIgnoreCase(userView.getManagePrivCode())){
	    					if(AdminCode.getCode("UN", filterId)!=null){
	    	    				orgFactor = "b0110="+filterId+"*`";
	    	    				filterName = AdminCode.getCode("UN", filterId).getCodename();
	    	    			}
	    				}
	    				if("UM".equalsIgnoreCase(userView.getManagePrivCode())){
	    					if(AdminCode.getCode("UM", filterId)!=null){
	    	    				orgFactor = "e0122="+filterId+"*`";
	    	    				filterName = AdminCode.getCode("UM", filterId).getCodename();
	    	    			}
	    				}
	    			}else{
	    				filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    			}
	    		}
	    	}
	    	if(!"UN".equalsIgnoreCase(filterId)){
	    		if(commlexr !=null && commlexr.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
	    			String[] style = simplestat.getCombinLexprFactor(orgLexpr, orgFactor, commlexr, commfacor);
	    			commlexr = style[0];
	    			commfacor = style[1];
	    		}else{
	    			commlexr = orgLexpr;
	    			commfacor = orgFactor;
	    		}
	    	}
	    }
		
		showTowCrossAction.setConn(this.conn);
		showTowCrossAction.setUserView(userView);
		showTowCrossAction.setCommlexr(commlexr);
		showTowCrossAction.setCommfacor(commfacor);
		
		try {
			userbases = userbases == null ? "" : userbases;
			userbases = new String(userbases.getBytes("ISO-8859-1"));
			userbases = userbases.replaceAll("，", "`");
			if(userbases == null || "".equals(userbases)){
				String nbase = "";
				for(int j = 0;j < statIdlist.length;j++){
					String statId = statIdlist[j].substring(statIdlist[j].lastIndexOf("_")+1);
					sql.setLength(0);
					sqlList.clear();
					sql.append("select * from sname where id = ?");
					sqlList.add(statId);
					rs = dao.search(sql.toString(), sqlList);
					if(rs.next()){
						infokind=rs.getString("infokind");
						nbase=rs.getString("nbase");
						if(nbase!=null&&nbase.length()>0){
							String [] baseS=nbase.split(",");
							for(int i=0;i<baseS.length;i++){
								if(baseS[i]!=null&&baseS[i].length()>0){
									if(!userView.isSuper_admin()){
										ArrayList nb_list=userView.getPrivDbList();
										for(int r=0;r<nb_list.size();r++){
											String ubase=nb_list.get(r).toString();
											if(baseS[i].equalsIgnoreCase(ubase)){
												if(userbases.indexOf(baseS[i])==-1){
													userbases+=baseS[i]+"`";
												}
											}
										} 
									}else{
										if(userbases.indexOf(baseS[i])==-1){
											userbases+=baseS[i]+"`";
										}
									}
								}
							}
						}
					}
				}
			}
			userbases = userbases.toUpperCase();
			userbases = userbases.replaceAll(",", "`");
			showTowCrossAction.setUserbases(userbases);
			showTowCrossAction.getTwoCrossChart(statIdlist, sformula, simplestat, preresult, history);
			showTowCrossAction.getTwoCrossTable( vtotal, htotal, vnull, hnull, lengthlist, crosslist, preresult, history, sformula);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//多维统计编码转化出错
			log.error(ResourceFactory.getProperty("static.error.getmorestaticcodingmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getmorestaticcodingmsg"));
		} catch (SQLException e) {
			e.printStackTrace();
			//多维统计条件生成sql语句出错
			log.error(ResourceFactory.getProperty("static.error.getmorestaticconditionssqlmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getmorestaticconditionssqlmsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		/**初始多维统计     单个条件统计图    start */
		ArrayList statidList = showTowCrossAction.getStatIdslist(); //多维统计  常用统计条件
		ArrayList chartDataList = showTowCrossAction.getListlist();//多维统计 一维统计条件统计图 数据
		ArrayList showChartsList = new ArrayList();
		double totalvalues = showTowCrossAction.getTotalvalues();
		for(int i = 0 ;i < statidList.size() ; i++){
			CommonData commonData = (CommonData) statidList.get(i);
			String id = commonData.getDataValue();
			sql.setLength(0);
			sqlList.clear();
			Map snameHM = this.getSname(PubFunc.encrypt(id), infokind);
			HashMap chartsHM = new HashMap();
			chartsHM.put("title", snameHM.get("title"));
			chartsHM.put("chart_type", getChartType((String)snameHM.get("viewtype")));
			HashMap chartDataHM = (HashMap) chartDataList.get(i);
			ArrayList chartList = (ArrayList) chartDataHM.get("list"+i);
			ArrayList chartDataReturnList = new ArrayList();
			int sum = 0;
			for(int n = 0 ; n < chartList.size() ; n++){
				CommonData chartCommonData = (CommonData) chartList.get(n);
				sum += Integer.parseInt(chartCommonData.getDataValue());
			}
			for(int j = 0 ; j < chartList.size() ; j++){//返回 一维统计条件  统计图 相关 数据
				CommonData chartCommonData = (CommonData) chartList.get(j);
				HashMap chartReturnHM = new HashMap();
				chartReturnHM.put("showLegend", PubFunc.encrypt(chartCommonData.getDataName()));
				chartReturnHM.put("name",chartCommonData.getDataName());
				chartReturnHM.put("value",chartCommonData.getDataValue());
				if(sum==0){
					chartReturnHM.put("percent", "0.00");
				}else{
					chartReturnHM.put("percent", new BigDecimal(Integer.parseInt(chartCommonData.getDataValue())*100.0/sum).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
				}
				chartDataReturnList.add(chartReturnHM);
			}
			chartsHM.put("chartData", chartDataReturnList);
			showChartsList.add(chartsHM);
		}
		/**end */

		/**多维统计 获取表格数据  start */

		ArrayList hFirstList =  showTowCrossAction.getVarrayfirstlist();//获取横向轴  统计条件 名称
		ArrayList hSecondList = showTowCrossAction.getVarraysecondlist();//获取横向轴 统计条件对应 统计项名称
		ArrayList hTableList = new ArrayList();
		String h_id = ("1_"+hvList[0]).replaceAll(",",",2_").replaceAll(";",",1_");
		String[] h_ids = h_id.split("1_");
		HashMap hidHM = new HashMap();
		HashMap hidLegendHM = new HashMap();
		HashMap hidsHM = new HashMap();
		for(int i = 0 ; i < h_ids.length ; i++){
            String[] ids = h_ids[i].split(",");
            for(int j = 0 ; j < ids.length ; j++){
                if(StringUtils.isBlank(ids[j]))
                    continue;
                if(!ids[j].startsWith("2_")) {
                    hidHM.put(ids[j], ",");
                    hidLegendHM.put(ids[j],0);
                    hidsHM.put(ids[j],",");
                    for(int n = 0 ; n < statidList.size() ; n++){
                        CommonData commonData = (CommonData) statidList.get(n);
                        if(ids[j].equalsIgnoreCase(commonData.getDataValue())){
                            ArrayList list = (ArrayList)((HashMap)chartDataList.get(n)).get("list"+n);
                            for(int l = 0 ; l < list.size() ; l++){
                                CommonData commonData1 = (CommonData) list.get(l);
                                hidsHM.put(ids[j],(String)hidsHM.get(ids[j])+commonData1.getDataName()+",");
                            }
                            break;
                        }

                    }
                    continue;
                }
                if(ids[j].startsWith("2_")){
                    hidHM.put(ids[0],hidHM.get(ids[0])+ids[j].replace("2_","")+",");
                    hidLegendHM.put(ids[0],(Integer)hidLegendHM.get(ids[0])+this.getLegendSum(ids[j].replace("2_","")));
                }
            }
        }
		int index = 0;
		int length = 0;
        String legends = "";
		for(int i = 0 ; i < hFirstList.size() ; i++){
			LazyDynaBean firstBean = (LazyDynaBean) hFirstList.get(i);
			ArrayList hItemList = new ArrayList();
			HashMap hTableHM = new HashMap();
			String id = (String) firstBean.get("id");
			if(StringUtils.isBlank(id) && i == hFirstList.size() - 1){
			    hTableHM.put("categoryName", firstBean.get("legend"));
			    hTableHM.put("items", new ArrayList());
			    hTableList.add(hTableHM);
			    continue;
			}
            hTableHM.put("categoryName", firstBean.get("name")==null? firstBean.get("legend"):firstBean.get("name"));
            if(!StringUtils.isBlank(id) ) {
                h_id = h_id.substring(h_id.indexOf("1_" + id + ",") + 3 + id.length());
                for(int j = 0 ; j < hSecondList.size() ; j++){
                    LazyDynaBean secondBean = (LazyDynaBean) hSecondList.get(j);
                    if(!id.equalsIgnoreCase((String)secondBean.get("id")))
                        continue;

                    HashMap itemHM = new HashMap();
                    itemHM.put("name", secondBean.get("legend"));
                    hItemList.add(itemHM);
                }
            }else{
                if("1".equalsIgnoreCase(vtotal) && i == hFirstList.size()-1){
                    LazyDynaBean secondBean = (LazyDynaBean) hSecondList.get(hSecondList.size()-1);
                    HashMap itemHM = new HashMap();
                    itemHM.put("name", secondBean.get("legend"));
                    hItemList.add(itemHM);
                    hTableHM.put("items", hItemList);
                    hTableList.add(hTableHM);
                    continue;
                }
                id = h_id.split(",")[0].substring(2);
                legends = (String)hidsHM.get(id);
                if(legends.indexOf(","+(String)firstBean.get("legend")+",") == -1){//上一个统计条件项执行完成
                    h_id = h_id.substring(h_id.indexOf("1_" + id + ",") + 3 + id.length());
                    h_id = h_id.substring(h_id.indexOf("1_"));
                    id = h_id.substring(2,h_id.indexOf(","));
                    legends = (String)hidsHM.get(id);
                    index = 0;
                }

                int count = (Integer) hidLegendHM.get(id);
                String ids = (String)hidHM.get(id);
                int l = 1;
                for(int j = 0 ; j < hSecondList.size() ; j++){
                    LazyDynaBean secondBean = (LazyDynaBean) hSecondList.get(j);
                    if(ids.indexOf(","+(String)secondBean.get("id")+",")== -1)
                        continue;

                    if(l<=index*count){
                        l++;
                        continue;
                    }

                    HashMap itemHM = new HashMap();
                    itemHM.put("name", secondBean.get("legend"));
                    hItemList.add(itemHM);
                    if(l==(index+1)*count)
                        break;
                    l++;
                }
                index++;
            }


            hTableHM.put("items", hItemList);
            hTableList.add(hTableHM);

		}
		
		ArrayList vFirstList = showTowCrossAction.getHarrayfirstlist();//获取纵向轴  统计条件 名称
		ArrayList vSecondList = showTowCrossAction.getHarraysecondlist();//获取纵向轴 统计条件对应 统计项名称
		ArrayList vTableList = new ArrayList();
        String v_id = ("1_"+hvList[1]).replaceAll(",",",2_").replaceAll(";",",1_");
        String[] v_ids = v_id.split("1_");
        HashMap vidHM = new HashMap();//降级与未降级统计条件的对应关系  , 间隔  ：33(未降级):,27,29,（所有属于33的降级id）
        HashMap vidLegendHM = new HashMap();//获取降级的统计项个数总和  33（未降级）：5（降级的作为未降级的子项统计项个数）
        HashMap vidsHM = new HashMap();//获取统计条件对应的统计项名称 , 间隔 "33" -> ",男,女,"
       for(int i = 0 ; i < v_ids.length ; i++){
            String[] ids = v_ids[i].split(",");
            for(int j = 0 ; j < ids.length ; j++){
                if(StringUtils.isBlank(ids[j]))
                    continue;
                if(!ids[j].startsWith("2_")) {
                    vidHM.put(ids[j], ",");
                    vidLegendHM.put(ids[j],0);
                    vidsHM.put(ids[j],",");
                    for(int n = 0 ; n < statidList.size() ; n++){
                        CommonData commonData = (CommonData) statidList.get(n);
                        if(ids[j].equalsIgnoreCase(commonData.getDataValue())){
                            ArrayList list = (ArrayList)((HashMap)chartDataList.get(n)).get("list"+n);
                            for(int l = 0 ; l < list.size() ; l++){
                                CommonData commonData1 = (CommonData) list.get(l);
                                vidsHM.put(ids[j],(String)vidsHM.get(ids[j])+commonData1.getDataName()+",");
                            }
                            break;
                        }
                    }
                    continue;
                }
                if(ids[j].startsWith("2_")){
                    vidHM.put(ids[0],vidHM.get(ids[0])+ids[j].replace("2_","")+",");
                    vidLegendHM.put(ids[0],(Integer)vidLegendHM.get(ids[0])+this.getLegendSum(ids[j].replace("2_","")));
                }
            }
        }
        index = 0;
		for(int i = 0 ; i < vFirstList.size() ; i++){
			LazyDynaBean firstBean = (LazyDynaBean) vFirstList.get(i);
			ArrayList vItemList = new ArrayList();
			HashMap vTableHM = new HashMap();
            String id = (String) firstBean.get("id");
            if(StringUtils.isBlank(id) && i == vFirstList.size() - 1){
                vTableHM.put("categoryName", firstBean.get("legend"));
                vTableHM.put("items", new ArrayList());
                vTableList.add(vTableHM);
                continue;
            }
			vTableHM.put("categoryName", firstBean.get("name")==null? firstBean.get("legend"):firstBean.get("name"));
            if(!StringUtils.isBlank(id) ) {
                v_id = v_id.substring(v_id.indexOf("1_" + id + ",") + 3 + id.length());
                for(int j = 0 ; j < vSecondList.size() ; j++){
                    LazyDynaBean secondBean = (LazyDynaBean) vSecondList.get(j);
                    if(!((String)firstBean.get("id")).equalsIgnoreCase((String)secondBean.get("id")))
                        continue;
                    HashMap itemHM = new HashMap();
                    itemHM.put("name", secondBean.get("legend"));
                    vItemList.add(itemHM);
                }
            }else{
                if("1".equalsIgnoreCase(vtotal) && i == vFirstList.size()-1){
                    LazyDynaBean secondBean = (LazyDynaBean) vSecondList.get(vSecondList.size()-1);
                    HashMap itemHM = new HashMap();
                    itemHM.put("name", secondBean.get("legend"));
                    vItemList.add(itemHM);
                    vTableHM.put("items", vItemList);
                    vTableList.add(vTableHM);
                    continue;
                }
                id = v_id.split(",")[0].substring(2);
                legends = (String)vidsHM.get(id);
                if(legends.indexOf(","+(String)firstBean.get("legend")+",") == -1){//上一个统计条件项执行完成
                    v_id = v_id.substring(v_id.indexOf("1_" + id + ",") + 3 + id.length());
                    v_id = v_id.substring(v_id.indexOf("1_"));
                    id = v_id.substring(2,v_id.indexOf(","));
                    legends = (String)vidsHM.get(id);
                    index = 0;
                }

                int count = (Integer) vidLegendHM.get(id);
                int l = 1;
                String ids = (String)vidHM.get(id);
                for(int j = 0 ; j < vSecondList.size() ; j++){
                    LazyDynaBean secondBean = (LazyDynaBean) vSecondList.get(j);
                    if(ids.indexOf(","+(String)secondBean.get("id")+",")== -1)
                        continue;

                    if(l<=index*count){
                        l++;
                        continue;
                    }

                    HashMap itemHM = new HashMap();
                    itemHM.put("name", secondBean.get("legend"));
                    vItemList.add(itemHM);
                    if(l==(index+1)*count)
                        break;
                    l++;
                }
                index++;
            }

			vTableHM.put("items", vItemList);
			vTableList.add(vTableHM);
		}

		int[][] table_data = showTowCrossAction.getStatValues();//获取table 表格数据        横轴 和 纵轴  值 反了 处理
		int[][] tableValues = new int[table_data[table_data.length-1].length][table_data.length];
		for(int i = 0 ; i < table_data.length ; i++){
			for(int j = 0 ; j < table_data[i].length ; j++){
				tableValues[j][i] = table_data[i][j];
			}
		}
		/**end*/
		HashMap tableDataHM = new HashMap();
		tableDataHM.put("h", hTableList);
		tableDataHM.put("v", vTableList);
		tableDataHM.put("data", tableValues);

		Map dataHM = new HashMap();
		dataHM.put("showChart",show_chart);
		dataHM.put("showCharts",showChartsList);
		dataHM.put("table_data",tableDataHM);
		dataHM.put("title", name);
		dataHM.put("totalvalues", totalvalues);
		dataHM.put("lengthways", lengthways); //多维 纵向   统计条件 id 集合
		dataHM.put("crosswise", crosswise); // 多维 横向 统计条件id 集合
		dataHM.put("org_filter", org_filter);
		dataHM.put("filterId", filterId);
		dataHM.put("filterName", filterName);
		return dataHM;
	}
	
	@Override
	public Map getStatisicalPersonList(UserView userView, String statid, String infokind, String v,String h,String lengthways,
			String crosswise, String vtotal,String htotal,String vnull,String hnull, int pageIndex, int pageSize,String filterId) throws GeneralException {
		
		String preresult = "2" ; // 目前场景 用不到 查询结果  只用 常用统计    1 查询结果    2 常用统计
		String history = null;  //目前场景 永不到     历史查询
		Map snameHM = this.getSname(statid, infokind);
		String flag = (String) snameHM.get("flag"); //是否查询结果集    1 是  2否
		String nbases = (String) snameHM.get("nbase");//人员库
		
		String[] condid = null; //分类统计条件
		if(snameHM.get("condid") != null && ((String)snameHM.get("condid")).trim().length() > 0)
			condid = new String[]{((String)snameHM.get("condid")).split(",")[0]};
		String[] curr_id = null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
    	{
    		String[] stat_ids=new String[1];
    		stat_ids[0]=stat_id;
    		curr_id=stat_ids;	    		
    	}else{
    		curr_id=condid;
    	}
		
    	String userbase = "";
		if(nbases == null || nbases.trim().length() == 0){
			nbases = "USR";
			userbase = nbases;
		}else{
			String[] userbases = nbases.split(",");
			userbase  = userbases[0].toUpperCase();
		}
		
		String[] lengthlist = lengthways.split(",");
		String[] crosslist = crosswise.split(",");
		String[] lengthwayslist = new String[lengthlist.length];
		String[] crosswiselist = new String[crosslist.length];
		for(int i = 0;i < lengthlist.length;i++){
			lengthwayslist[lengthlist.length-1-i] = lengthlist[i];
		}
		for(int i = 0;i < crosslist.length;i++){
			crosswiselist[crosslist.length-1-i] = crosslist[i];
		}
		vtotal=vtotal==null||vtotal.length()==0?"0":vtotal;
		htotal=htotal==null||htotal.length()==0?"0":htotal;
		vnull=vnull==null||vnull.length()==0?"0":vnull;
		hnull=hnull==null||hnull.length()==0?"0":hnull;
		
		//是否查询结果集表数据   true 不查  false 查
    	boolean isresult = true;
		if(flag != null && "1".equalsIgnoreCase(flag))
			isresult = false;
		
		//常用查询进行的统计
		//加上常用查询进行的统计
	    String commlexr=null;
	    String commfacor=null;
	    if(curr_id != null) {
			if(!checkCurrId(curr_id)) {
				curr_id = null;
			}
		}
		GeneralQueryStat generalstat=new GeneralQueryStat();
		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,null,this.conn);// history 值 先为 null，具体使用场景待定
		if(curr_id!=null)
	    {
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    }
		if(StringUtils.isNotBlank(filterId)){
			String orgFactor = "";
			String orgLexpr = "1";
			if(AdminCode.getCode("UN", filterId)!=null){
				orgFactor = "b0110="+filterId+"*`";
			}
			if(AdminCode.getCode("UM", filterId)!=null){
				orgFactor = "e0122="+filterId+"*`";
			}
			
			if(StringUtils.isNotBlank(orgFactor)){
				orgLexpr = "1";
			}
			if(commlexr !=null && commlexr.trim().length()>0 && commlexr != null && commlexr.trim().length() >0){
				String[] style = new StatDataEncapsulation().getCombinLexprFactor(orgLexpr, orgFactor, commlexr, commfacor);
				commlexr = style[0];
				commfacor = style[1];
			}else{
				commlexr = orgLexpr;
				commfacor = orgFactor;
			}
		}
		StringBuffer orderby=new StringBuffer();
		orderby.append(" order by ");
		orderby.append("db,a0000");
		
	    StatCondAnalyse cond = new StatCondAnalyse();
	    String querycond ="";//查询sql 目前不知道使用场景待定
	    String sql=new StatDataEncapsulation().getDataSQL(lengthwayslist,crosswiselist,userbase,querycond,Integer.parseInt(v),Integer.parseInt(h),userView.getUserName(),userView.getManagePrivCode(),userView,infokind,true,commlexr,commfacor,preresult,history,nbases,vtotal,htotal,vnull,hnull);
	    StringBuffer strsql=new StringBuffer();
    	ArrayList mainlist=getMainFieldList(userView,infokind);
	    String columns=getMainQueryFields(mainlist);
	    
	    StringBuffer SQL = new StringBuffer();
	    RowSet rs = null;
	    ContentDAO dao = new ContentDAO(this.conn);
	    if("1".equals(infokind)){
	    	strsql.append("select distinct ");
	        strsql.append(userbase);
	        strsql.append("a01.a0100 as a0100,");
	        strsql.append("## as db,");
	        strsql.append("a0000 as a0000,");
	        strsql.append(userbase);        
	        strsql.append("a01.b0110 as b0110,");
            strsql.append(userbase);
            strsql.append("a01.e0122 as e0122,");
            strsql.append(userbase);
            strsql.append("a01.e01a1 as e01a1");
            
	        columns=(","+columns.toLowerCase()).replaceAll(",e01a1", "").replaceAll(",e0122", "").replaceAll(",b0110", "").replaceAll(",a0100", "");
	        strsql.append(columns);
	        strsql.append(",UserName ");
	        userbase=userbase.toUpperCase();
	        String tmpsql =(strsql.toString()+sql).toUpperCase();//+" order by db,A0000 ";
	        StringBuffer sb = new StringBuffer();
	        if(nbases!=null&&nbases.length()>0){
		        if(nbases.indexOf(",")==-1){
		        	nbases = ("".equals(nbases.trim()))? userbase:nbases;//没有设置人员库条件处理 wangb 20180822
					sb.append(" from ("+tmpsql.replaceAll(userbase, nbases).replaceAll("##", "'"+getStart(0)+nbases+"'")+"");
				}else{
					String[] tmpdbpres=nbases.split(",");
					ArrayList dbPrilist = userView.getPrivDbList();
					String dbPri = ","+StringUtils.join(dbPrilist.toArray(new String[dbPrilist.size()]),",")+",";
					for(int n=0;n<tmpdbpres.length;n++){
						String tmpdbpre=tmpdbpres[n];
						if(dbPri.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1/*!dbPrilist.contains(tmpdbpre)*/)//过滤 人员库不存在情况  wangb 20180717 bug 38893
							continue;
						if(tmpdbpre.length()==3){
							if(sb.length()>0){
								sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
							}else{
								sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
							}
						}
					}
				}
	        }else{
	        	sb.append(" from ("+tmpsql.replaceAll("##", "'"+getStart(0)+userbase+"'")+"");
	        }
	        sql=sb.toString()+") tt ";//order by db,A0000
	        strsql.setLength(0);
	        strsql.append("select a0000,");
	        strsql.append("a0100,");
	        strsql.append("b0110,");
            strsql.append("e0122,");
            strsql.append("e01a1");
	        //strsql.append(",");
	        strsql.append(columns);
	        strsql.append(",UserName,db ");
	    }
	    //获取统计项总数
        int num = 0;
        try {
			rs = dao.search("select count(1) num "+ sql);
			if(rs.next()){
				if(Sql_switcher.searchDbServerFlag() == 2){
					num = rs.getBigDecimal("num").intValue();
				}else{
					num = rs.getInt("num");
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			//人员穿透总人数出错
			log.error(ResourceFactory.getProperty("static.error.getthroughpersontotalmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getthroughpersontotalmsg"));
		}
        if(0 == num){//人员数为 0时
        	Map dataHM = new HashMap();
            dataHM.put("infoList", null);
            dataHM.put("total", 0);
            dataHM.put("page", pageIndex);
     		return dataHM;
        }
        if(Sql_switcher.searchDbServerFlag() ==2){//oracle 库
        	SQL.append(strsql.toString()+" from ( \n");
        	SQL.append("  "+ strsql.toString()+",rownum rn "+sql+" \n");
        	SQL.append(") ttt where ttt.rn BETWEEN "+((pageIndex-1)*pageSize+1)+" and "+(pageIndex*pageSize)+" ");
        	SQL.append(" order by db,A0100 ");
        }else{
			SQL.append(strsql.toString().replace("select","select top "+pageSize+" ")+" " + sql+"\n ");
			SQL.append("where not exists(\n");
			SQL.append("  select * from (\n");
			SQL.append("    select top "+((pageIndex-1)*pageSize)+" * "+sql+" ");
			SQL.append("  ) t2 where tt.a0100 = t2.a0100 and tt.db = t2.db \n");
			SQL.append(")  order by db,A0100");
		}
        ArrayList infoList = new ArrayList();
        try {
			rs = dao.search(SQL.toString());
			while(rs.next()){//只写了获取 人员  姓名  部门 岗位 头像  字段，  统计条件字段  移动端没涉及未取   
				HashMap map = new HashMap();
				
				String photo = this.getPicUrl(userView, rs.getString("db").substring(1),rs.getString("a0100"));
				if(photo != null)
					map.put("photo", photo);
				else
					map.put("photo", "/images/photo.jpg");
				map.put("nbase", PubFunc.encrypt(rs.getString("db").substring(1)));
				map.put("A0100", PubFunc.encrypt(rs.getString("a0100")));
				map.put("A0101", rs.getString("a0101"));
				String E0122 = rs.getString("E0122");
				String E01A1 = rs.getString("E01A1");
				CodeItem E0122Code = AdminCode.getCode("UM", E0122,5);
				if(E0122Code != null){
					map.put("E0122", AdminCode.getCode("UN", E0122Code.getPcodeitem(), 0).getCodename() +"/"+E0122Code.getCodename().substring(E0122Code.getCodename().lastIndexOf("/")+1));
				}else{
					map.put("E0122", "");
				}
				CodeItem E01A1Code = AdminCode.getCode("@K", E01A1);
				map.put("E01A1", E01A1Code!=null? E01A1Code.getCodename():"");
				infoList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取统计项人员信息出错
			log.error(ResourceFactory.getProperty("static.error.getitempersoninfomsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getitempersoninfomsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}

        Map dataHM = new HashMap();
        dataHM.put("infoList", infoList);
        dataHM.put("total", num);
        dataHM.put("page", pageIndex);
	    
		return dataHM;
	}
	
	/**
	 * 二维统计条件返回统计数据
	 * @param queryId   statid 
	 * @param userbase  当前人员库
	 * @param sqlSelect  "" 目前不知道用途
	 * @param username   用户名
	 * @param manageprive 权限
	 * @param userView   用户信息
	 * @param infokind   统计类型
	 * @param bresult    是否查历史数据
	 * @param commlexpr  常用查询条件因子
	 * @param commfactor 常用查询条件表达式
	 * @param preresult  目前不知道
	 * @param history    是否查历史记录     
	 * @param userbases  统计条件人员库
	 * @param sformula   统计方式id
	 * @param conn       连接池
	 * @param vtotal     横向合计     1 合计 0 不合计
	 * @param htotal     纵向合计     1 合计  0 不合计
	 * @return
	 */
	private Map getDoubleLexprDataSformula(
			int queryId,
			String userbase,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn,String vtotal,String htotal) throws GeneralException{
		Map dataHM = new HashMap();
		Map tableDataHM = new HashMap();
		
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		double totalValues = 0;
		double[][] dataValues = null;
		String[][] doubleDataValues = null;
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		
		ArrayList verticalArray=new ArrayList();
		ArrayList horizonArray = new ArrayList();
		StatCondAnalyse cond = new StatCondAnalyse();
		
		SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
		Element element = xml.getElement(sformula);
		String decimalwidth  = element.getAttributeValue("decimalwidth");
		String title = element.getAttributeValue("title");
		String type = element.getAttributeValue("type");
		String expr = element.getText();
		
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userbase);
		yp.setCon(conn);
		try {
			yp.run(expr);
		} catch (SQLException e) {
			e.printStackTrace();
			//获取二维统计统计公式出错
			log.error(ResourceFactory.getProperty("static.error.getdoublestaticformulamsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getdoublestaticformulamsg"));
		} catch (GeneralException e) {
			e.printStackTrace();
			//获取二维统计统计公式出错
			log.error(ResourceFactory.getProperty("static.error.getdoublestaticformulamsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getdoublestaticformulamsg"));
		}
		String field = yp.getSQL();
		ArrayList usedsets = yp.getUsedSets();
		
		StringBuffer sql =new StringBuffer();
		sql.append("select * from SName where id=");
		sql.append(queryId);
		List rs = ExecuteSQL.executeMyQuery(sql.toString());
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";//获取二维统计 定义的   2个一维统计
			if (doubleLexr != null)
				HVLexr = doubleLexr.split(",");
			if(HVLexr[0] !=null && HVLexr[1] !=null && !"x".equalsIgnoreCase(HVLexr[0]) && !"x".equalsIgnoreCase(HVLexr[1])){
				String sqlH = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
				String sqlV = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
				List rsH = ExecuteSQL.executeMyQuery(sqlH);//获取水平 轴  一维统计项
				List rsV = ExecuteSQL.executeMyQuery(sqlV);//获取垂直 轴 一维统计项
				if("1".equals(vtotal)&&"1".equals(htotal)){//合计 
					dataValues = new double[rsH.size()+1][rsV.size()+1];
                    doubleDataValues = new String[rsH.size()+1][rsV.size()+1];
				}else if("1".equals(vtotal)){
					dataValues = new double[rsH.size()+1][rsV.size()];
                    doubleDataValues = new String[rsH.size()+1][rsV.size()];
				}else if("1".equals(htotal)){
					dataValues = new double[rsH.size()][rsV.size()+1];
                    doubleDataValues = new String[rsH.size()][rsV.size()+1];
				}else{
					dataValues = new double[rsH.size()][rsV.size()];
                    doubleDataValues = new String[rsH.size()][rsV.size()];
				}
				if (!rsV.isEmpty() && !rsH.isEmpty()) {
					ArrayList lexprFactorh = new ArrayList();
					for (int j = 0; j < rsV.size(); j++) {
						rec=(LazyDynaBean)rsV.get(j);
						verticalArray.add(rec);
						if("1".equals(htotal)){
							strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
							strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
							lexprFactorh.add(strLexprh + "|" + strFactorh);
						}
					}
					if("1".equals(htotal)){//纵向合计 表达式和因子
						CombineFactor combinefactorh=new CombineFactor();
						String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
						StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
						if(Stokh.hasMoreTokens())
						{
							strLexpr=Stokh.nextToken();
							strFactor=Stokh.nextToken();
						}
						rec = new LazyDynaBean();
						rec.set("factor", strFactor);
						rec.set("lexpr", strLexpr);
						rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
						rec.set("id", HVLexr[1]);
						rsV.add(rec);
						verticalArray.add(rec);
					}
					ArrayList lexprFactorv=new ArrayList();
					
					for (int i = 0; i < rsH.size(); i++) {
						rec=(LazyDynaBean)rsH.get(i);
						horizonArray.add(rec);
						if("1".equals(vtotal)){
							strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
							strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
							lexprFactorv.add(strLexprv + "|" + strFactorv);
						}
					}
					if("1".equals(vtotal)){//横向合计 表达式和因子
						CombineFactor combinefactorv=new CombineFactor();
						String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
						StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
						if(Stokv.hasMoreTokens())
						{
							strLexpr=Stokv.nextToken();
							strFactor=Stokv.nextToken();
						}
						rec = new LazyDynaBean();
						rec.set("factor", strFactor);
						rec.set("lexpr", strLexpr);
						rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
						rec.set("id", HVLexr[0]);
						rsH.add(rec);
						horizonArray.add(rec);
					}
					
					int lenght = 1;
					if("1,2,3".equals(infokind)){
						lenght = 3;
					}
					for(int m=1;m<=lenght;m++){
						if(!"1,2,3".equals(infokind)){
							infokind = String.valueOf(m);
						}
						for (int i = 0; i < rsH.size(); i++) {
							LazyDynaBean recv=(LazyDynaBean)rsH.get(i);
							strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
							strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
							boolean ishavehistory=false;
							for (int j = 0; j < rsV.size(); j++) {
								rec=(LazyDynaBean)rsV.get(j);
								strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + (rec.get("factor").toString().trim().endsWith("`")?"":"`"):"";
								strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								ArrayList lexprFactor=new ArrayList();
								lexprFactor.add(strLexprh + "|" + strFactorh);
								lexprFactor.add(strLexprv + "|" + strFactorv);
								CombineFactor combinefactor=new CombineFactor();
								String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
								StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
								if(Stok.hasMoreTokens())
								{
									strLexpr=Stok.nextToken();
									strFactor=Stok.nextToken();
								}
								
								if(commlexpr!=null && commfactor!=null)
								//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
								{
									String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
								    if(style!=null && style.length==2)
								    {
								    	strLexpr=style[0];
								    	strFactor=style[1];
								    }
								}
								try {
									strHV=cond.getCondQueryString(strLexpr,	strFactor,userbase,ishavehistory,userView.getUserName(),"",userView,infokind,bresult);
								} catch (GeneralException e) {
									e.printStackTrace();
									//二维统计处理一维统计条件出错
									log.error(ResourceFactory.getProperty("static.error.doublehandleonestaticconditionmsg"));
									throw new GeneralException(ResourceFactory.getProperty("static.error.doublehandleonestaticconditionmsg"));
								}
//								if(this.whereIN!=null&&this.whereIN.length()>0)
//									strHV=strHV+" and "+this.getWhereIN();
								String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
								strHV = strHV.toUpperCase();
								for(int n=0;n<usedsets.size();n++){
									String set = (String)usedsets.get(n);
									if("1".equals(infokind)){ //人员
										set = (" "+userbase+set).toUpperCase();
										StringBuffer sb = new StringBuffer();
										if(basesql.indexOf(set)==-1){
											sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
											sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
											sb.append(basesql.substring(basesql.indexOf(" WHERE")));
											sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100) OR "+set+".I9999 IS NULL)");
											basesql= sb.toString();
										}
									}
								}
								
								StringBuffer sb = new StringBuffer();
								String tmpsql="";
								if("1".equals(infokind)){
									tmpsql = ("select "+field+" as recordCount" + basesql+" and "+userbase+"A01.a0100 in(select a0100 "+strHV+")").toUpperCase();
								}else
									tmpsql = ("select "+field+" as recordCount" + strHV).toUpperCase();
								if(userbases.indexOf("`")==-1){
									userbases = userbases.trim().length()==0 || "".equals(userbases.trim())? userbase:userbases;//人员库条件没有设置处理 wangb 20180822
									sb.append(tmpsql.replaceAll(userbase, userbases));
								}else{
									String[] tmpdbpres=userbases.split("`");
									ArrayList dbList = userView.getPrivDbList();
									String dblist = ","+StringUtils.join(dbList.toArray(new String[dbList.size()]),",")+",";
									for(int n=tmpdbpres.length-1;n>=0;n--){
										String tmpdbpre=tmpdbpres[n];
										if(dblist.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1 /*!dbList.contains(tmpdbpre)*/) //bug 38847 人员库不存在，不查询人员  wangb 20180714
											continue;
										if(tmpdbpre.length()==3){
											if(sb.length()>0){
												sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre));
											}else{
												sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
											}
										}
									}
								}
								strHV =	"select "+type+"(recordCount) as recordCount from (" + sb.toString()+") tt";
								//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
								List rscount = ExecuteSQL.executeMyQuery(strHV);
								if (rscount != null && rscount.size()>0) {
									LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
									String tmp=rscountc.get("recordcount").toString();
									if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
										tmp="0";
									if(Double.valueOf(tmp) > 0){
                                        dataValues[i][j] = Double.parseDouble(tmp);
                                        doubleDataValues[i][j]= new BigDecimal(Double.parseDouble(tmp)).setScale(Integer.parseInt(decimalwidth),BigDecimal.ROUND_HALF_UP).toString();
                                    }else{
									    dataValues[i][j] = 0;
                                        doubleDataValues[i][j]="0";
                                    }

									if("1".equals(vtotal)&&"1".equals(htotal)){
										if(j<rsH.size()-1&&i<rsV.size()-1)
											totalValues += dataValues[i][j];
									}else if("1".equals(vtotal)){
										if(i<rsV.size()-1)
											totalValues += dataValues[i][j];
									}else if("1".equals(htotal)){
										if(j<rsH.size()-1)
											totalValues += dataValues[i][j];
									}else{
										totalValues += dataValues[i][j];
									}
								} else {
									dataValues[i][j] = 0;
                                    doubleDataValues[i][j]="0";
                                }
							}
						}
					}
					
				}
			}
		}
		
		HashMap H = new HashMap();
		HashMap V = new HashMap();
		sql.setLength(0);
		ArrayList sqlList = new ArrayList();
		sql.append("select name from sname where id=?");
		sqlList.add(HVLexr[0]);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null ;
		try {
			rowSet =  dao.search(sql.toString(), sqlList);
			if(rowSet.next())
				H.put("categoryName", rowSet.getString("name"));
				
			sqlList.clear();
			sqlList.add(HVLexr[1]);
			rowSet =  dao.search(sql.toString(), sqlList);
			if(rowSet.next())
				V.put("categoryName", rowSet.getString("name"));
		} catch (SQLException e) {
			e.printStackTrace();
			//获取一维统计条件名称出错
			log.error(ResourceFactory.getProperty("static.error.getonestaticnamemsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getonestaticnamemsg"));
		}finally{
			PubFunc.closeDbObj(rowSet);
		}

		ArrayList hLegendList = new ArrayList();
		for(int i = 0 ; i < horizonArray.size(); i++){
			LazyDynaBean lazyDynaBean = (LazyDynaBean) horizonArray.get(i);
			HashMap map = new HashMap();
			map.put("name", lazyDynaBean.get("legend"));
			hLegendList.add(map);
		}

		ArrayList vLegendList = new ArrayList();
		for(int i = 0 ; i < verticalArray.size(); i++){
			LazyDynaBean lazyDynaBean = (LazyDynaBean) verticalArray.get(i);
			HashMap map = new HashMap();
			map.put("name", lazyDynaBean.get("legend"));
			vLegendList.add(map);
		}

		V.put("items", vLegendList);
		H.put("items", hLegendList);
		tableDataHM.put("h", H);
		tableDataHM.put("v", V);
		tableDataHM.put("data", doubleDataValues);
		dataHM.put("table_data", tableDataHM);
		
		ArrayList chartDataList = new ArrayList();
		for(int i = 0 ; i < verticalArray.size(); i++){
			if("1".equalsIgnoreCase(vtotal) && i == verticalArray.size()-1)
				continue;
			LazyDynaBean verticalBean = (LazyDynaBean) verticalArray.get(i);
			ArrayList dataList = new ArrayList();
			for(int j = 0 ; j < horizonArray.size(); j++){
				if("1".equalsIgnoreCase(htotal) && j == horizonArray.size()-1)
					continue;
				LazyDynaBean horizonBean = (LazyDynaBean) horizonArray.get(j);
				HashMap horizonHM = new HashMap();
				horizonHM.put("name", horizonBean.get("legend"));
				horizonHM.put("value",doubleDataValues[j][i]);
				dataList.add(horizonHM);
			}
			HashMap verticalHM = new HashMap();
			verticalHM.put("categoryName", verticalBean.get("legend"));
			verticalHM.put("dataList",dataList);
			chartDataList.add(verticalHM);
		}
		
		dataHM.put("chartData", chartDataList);
		return dataHM;
		
	}
	
	
	/**
	 * 获取统计项信息
	 * @param userView 用户名
	 * @param statid  统计条件 id号
	 * @param sqlSelect 组织机构        人员统计传 "" 值 
	 * @param nbases  统计条件设置的 人员库
	 * @param sformula 显示的统计方式 id号
	 * @param infokind 统计类型 查1 人员
	 * @param curr_id  常用查询id
	 * @param history 查看历史子集数据 1 查看 0 不查看 
	 * @param bresult 结果集取数据     false 查询结果集    true 不查询
	 * @param org_filter 按组织机构筛选  人员范围
	 * @param filterId 筛选机构id
	 * @return
	 * @throws GeneralException
	 */
	private Map getStatisticalLengend(UserView userView,String statid,String sqlSelect,String nbases,String sformula,String infokind,String[] curr_id,String history,boolean bresult,String org_filter,String filterId) throws GeneralException{
		StatCondAnalyse cond = new StatCondAnalyse();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		sql.append("select * from SLegend where id=? order by norder");
		list.add(statid);
		List legendList = ExecuteSQL.executePreMyQuery(sql.toString(), list, this.conn);//获取统计项
		String[] legendTitle = new String[legendList.size()]; //统计项名称 数组
		String[] norderLegend = new String[legendList.size()];//排序 数组
		double[] fieldValues = new double[legendList.size()]; // 统计项对应值
		double[] distinctValues = new double[legendList.size()]; // 统计项对应值
		
		SformulaXml xml = new SformulaXml(conn,statid);//获取显示统计方法
		Element element = xml.getElement(sformula);
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		String userbase = "";
		if(nbases == null || nbases.trim().length() == 0){
			nbases = "USR";
			userbase = nbases;
		}else{
			String[] userbases = nbases.split(",");
			userbase  = userbases[0].toUpperCase();
		}
		String title= element.getAttributeValue("title");//统计方式名称
		String type=element.getAttributeValue("type");//统计方式 类型
		String expr=element.getText();//统计方式  公式
		int decimalwidth = Integer.parseInt(element.getAttributeValue("decimalwidth"));
		String field = "";
		ArrayList usedsets = null;
		YksjParser yp = null;
		try {
			//统计方式 里公式 转sql 
			yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userbase);
			yp.setCon(conn);
			yp.run(expr);
			field = yp.getSQL();
			usedsets = yp.getUsedSets();//获取公式涉及子集
		} catch (GeneralException e) {
			e.printStackTrace();
			//创建计算公式出错
			log.error(ResourceFactory.getProperty("static.error.createcalculateformulamsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.createcalculateformulamsg"));
		} catch (SQLException e) {
			e.printStackTrace();
			//计算公式转sql出错
			log.error(ResourceFactory.getProperty("static.error.calculatechangesqlmsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.calculatechangesqlmsg"));
		}
		GeneralQueryStat generalstat=new GeneralQueryStat();
		generalstat.insertCount(statid, this.conn);
		if(curr_id != null) {
			if(!checkCurrId(curr_id)) {
				curr_id = null;
			}
		}
		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.conn);
		String commlexpr = ""; //条件因子
		String commfactor = "";//条件表达式
		if(curr_id!=null)
	    {
			commlexpr=generalstat.getLexpr();
			commfactor=generalstat.getLfactor();
	    	history = generalstat.getHistory();
	    }
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
		String filterName = "";
		//按机构筛选 逻辑  一维统计 wangb 2019-08-19
		if("1".equalsIgnoreCase(org_filter)){
	    	String orgFactor = "";
	    	String orgLexpr = "";
	    	if(StringUtils.isNotBlank(filterId)){
	    		if(AdminCode.getCode("UN", filterId)!=null){
    				orgFactor = "b0110="+filterId+"*`";
    				filterName = AdminCode.getCode("UN", filterId).getCodename();
    			}
    			if(AdminCode.getCode("UM", filterId)!=null){
    				orgFactor = "e0122="+filterId+"*`";
    				filterName = AdminCode.getCode("UM", filterId).getCodename();
    			}
	    	}else{
	    		if(userView.isSuper_admin()){
	    			filterId = "UN";
	    			filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");//组织机构
	    		}else{
	    			filterId = userView.getManagePrivCodeValue();
	    			if("UN".equalsIgnoreCase(userView.getManagePrivCode()) && filterId.trim().length() == 0){
	    				filterId = "UN";
	    			}
	    			if(!"UN".equalsIgnoreCase(filterId)){
	    				if("UN".equalsIgnoreCase(userView.getManagePrivCode())){
	    					if(AdminCode.getCode("UN", filterId)!=null){
	    	    				orgFactor = "b0110="+filterId+"*`";
	    	    				filterName = AdminCode.getCode("UN", filterId).getCodename();
	    	    			}
	    				}
	    				if("UM".equalsIgnoreCase(userView.getManagePrivCode())){
	    					if(AdminCode.getCode("UM", filterId)!=null){
	    	    				orgFactor = "e0122="+filterId+"*`";
	    	    				filterName = AdminCode.getCode("UM", filterId).getCodename();
	    	    			}
	    				}
	    			}else{
	    				filterName = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    			}
	    		}
	    	}
	    	if(!"UN".equalsIgnoreCase(filterId)){
	    		if(StringUtils.isNotBlank(orgFactor)){
	    			orgLexpr = "1";
	    		}
	    		if(commlexpr !=null && commlexpr.trim().length()>0 && commlexpr != null && commlexpr.trim().length() >0){
	    			String[] style = simplestat.getCombinLexprFactor(orgLexpr, orgFactor, commlexpr, commfactor);
	    			commlexpr = style[0];
	    			commfactor = style[1];
	    		}else{
	    			commlexpr = orgLexpr;
	    			commfactor = orgFactor;
	    		}
	    	}
	    }
		String strLexpr = "";
		String strFactor = "";
		String flag = "";
		String strQuery = "";
//		String distinctPersonSql =""; // 统计人员真实的总数 
		//处理统计项 通过 条件因子和条件表达式 得到统计项对应人数
		for(int i = 0 ; i < legendList.size() ; i++){
			LazyDynaBean rec=(LazyDynaBean)legendList.get(i);
			strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
			strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
			flag=rec.get("flag")!=null?rec.get("flag").toString():"";
			legendTitle[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
			norderLegend[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
			if(commlexpr!=null && commfactor!=null)
			{
				//CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 2017/09/07
				commfactor = commfactor.replaceAll("\\|", "~");
				String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);//常用查询 条件 和 统计项 条件 组合成新的表达式
			    if(style!=null && style.length==2)
			    {
			    	strLexpr=style[0];
			    	strFactor=style[1];
			    }
			}
			//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
			boolean ishavehistory=false;
			if("1".equals(flag))
				ishavehistory=true;
			else if("1".equals(history))
				ishavehistory=true;
			strQuery =cond.getCondQueryString(strLexpr,strFactor,userbase,ishavehistory,userView.getUserName(),sqlSelect,userView,infokind,bresult);
			strQuery = strQuery.toUpperCase();
			String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
			/**
			 * 【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列明白“A0100”不明确”
			 *  新增lsql，根据设置的统计条件获取数据
			 *  jingq add 2015.04.16
			 */
			FactorList factorlist= new FactorList(strLexpr,strFactor,userbase,ishavehistory,true,bresult,Integer.parseInt(infokind),userView.getUserName());
	        String lsql = factorlist.getSqlExpression();
	        lsql = lsql.substring(lsql.indexOf("WHERE")+5);
	        
			for(int n=0;n<usedsets.size();n++){
				String set = (String)usedsets.get(n);
				if("1".equals(infokind)){
					set = (" "+userbase+set).toUpperCase();
					StringBuffer sb = new StringBuffer();
					if(basesql.indexOf(set)==-1){
						sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
						sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
						sb.append(basesql.substring(basesql.indexOf(" WHERE")));
						sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100 AND "+lsql+") OR "+set+".I9999 IS NULL)");
						basesql= sb.toString();
					}
				}
			}
			
			// WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
			if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
				StringBuffer sb = new StringBuffer();
				if("1".equals(infokind)){
					sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
					sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userbase+"A01.A0100="+yp.getTempTableName()+".A0100");
					sb.append(basesql.substring(basesql.indexOf(" WHERE")));
					basesql= sb.toString();
				}
			}
			if("1".equals(infokind)){
				StringBuffer sb = new StringBuffer();//【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列名称“A0100”不明确”  jingq upd 2015.04.15
//				StringBuffer  distinctSB = new StringBuffer();//统计真实人员sql
				String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userbase+"A01.a0100 in(select "+userbase.toUpperCase()+"A01.a0100 "+strQuery+")").toUpperCase();
//				String distinctTmpSql = ("select "+userbase+"A01.a0100 as lexprData" + basesql+" and "+userbase+"A01.a0100 in(select "+userbase.toUpperCase()+"A01.a0100 "+strQuery+")").toUpperCase();
				if(nbases.indexOf(",")==-1){
					nbases = nbases.trim().length()==0 || "".equals(nbases.trim())? userbase:nbases;//人员库条件没有设置处理 wangb 20180822
					sb.append(tmpsql.replaceAll(userbase,nbases));
//					distinctSB.append(distinctTmpSql.replaceAll(userbase, nbases));
				}else{
					String[] tmpdbpres=nbases.split(",");
					ArrayList dbList = userView.getPrivDbList();
					String dblist = ","+StringUtils.join(dbList.toArray(new String[dbList.size()]),",")+",";
					for(int n=tmpdbpres.length-1;n>=0;n--){
						String tmpdbpre=tmpdbpres[n];
						if(dblist.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1 /*!dbList.contains(tmpdbpre)*/) //bug 38847 人员库不存在，不查询人员  wangb 20180714
							continue;
						if(tmpdbpre.length()==3){
							if(sb.length()>0){
								sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre));
//								distinctSB.append(" union all "+distinctTmpSql.replaceAll(userbase, tmpdbpre));
							}else{
								sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
//								distinctSB.append(distinctTmpSql.replaceAll(userbase, tmpdbpre));
							}
						}
					}
				}
				//sql server 数据库  平均值返回整数 处理   wangbs  2020-5-25
				if (Sql_switcher.searchDbServerFlag() == 1 && "avg".equalsIgnoreCase(type) && decimalwidth > 0) {
					strQuery = sb.toString().replace(field.toUpperCase(), "convert(decimal(15," + decimalwidth + ")," + type + "(" + field + "+0.0))");
				}else{
					strQuery = "select "+type+"(lexprData) as lexprData from (" + sb.toString()+") tt";
				}
//				distinctPersonSql = "select "+type+"(lexprData) as lexprData from (" + distinctSB.toString()+") tt";
			}

			List rsset = ExecuteSQL.executeMyQuery(strQuery);
			if (rsset != null && rsset.size()>0) {
				//保存该图例的统计数
				LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
				String tmp=rdata.get("lexprdata").toString();
				if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
					tmp="0";
				fieldValues[i] = Double.parseDouble(tmp);
			}
		}
		
		
		/**
		 * 常用查询条件 
		 * 统计方式 公式 
		 * 统计项 公式
		 * 人员库
		 */

		HashMap chartDataHM = new HashMap();
		double num = 0 ;
		for(int i = 0 ; i < fieldValues.length; i++){
			if("count".equalsIgnoreCase(type) || "sum".equalsIgnoreCase(type) || "avg".equalsIgnoreCase(type)){
				num += fieldValues[i];
			}else if("min".equalsIgnoreCase(type)){
				if(num > fieldValues[i])
					num = fieldValues[i];
			}else if("max".equalsIgnoreCase(type)){
				if(num < fieldValues[i])
					num = fieldValues[i];
			}
		}
		if("avg".equalsIgnoreCase(type)){
			BigDecimal b = new BigDecimal(num*1.0/fieldValues.length);
			chartDataHM.put("num", b.setScale(decimalwidth, BigDecimal.ROUND_HALF_UP));
		}else{
			chartDataHM.put("num", num);
		}
		ArrayList legendData = new ArrayList();
		for(int i = 0 ; i < fieldValues.length ; i++){
			HashMap map = new HashMap();
			map.put("showLegend",PubFunc.encrypt(legendTitle[i]));
			map.put("name", legendTitle[i]);
			BigDecimal bdValue = new BigDecimal(fieldValues[i]);
			map.put("value", (bdValue.setScale(decimalwidth,BigDecimal.ROUND_HALF_UP)).toString());
			if((int)num == 0){
				map.put("percent", "");
			}else{
				BigDecimal b = new BigDecimal(fieldValues[i]*100.0/num);  
				map.put("percent", (b.setScale(2, BigDecimal.ROUND_HALF_UP)).toString());
			}
			legendData.add(map);
		}
		chartDataHM.put("data", legendData);
		chartDataHM.put("filterName", filterName);
		chartDataHM.put("filterId", filterId);
		return chartDataHM;
	}
	
	
	/**
	 * 计算公式类型
	 * @param fieldtype 计算类型
	 * @return
	 */
	private int getvarType(String fieldtype){
		int varType = YksjParser.FLOAT; // float
		if ("D".equals(fieldtype))
			varType = YksjParser.DATEVALUE;
		else if ("A".equals(fieldtype) || "M".equals(fieldtype))
			varType = YksjParser.STRVALUE;
		return varType;
	}
	
	/**
     * 返回YksjParser infoGroup参数
     *
    */ 
	private int infoKindToInfoGroup(String infokind) {
        int infoGroup=0;
        if("1".equals(infokind))
            infoGroup = YksjParser.forPerson;
        else if ("2".equals(infokind)) 
            infoGroup = YksjParser.forUnit;
        else if ("3".equals(infokind))
            infoGroup = YksjParser.forPosition;
        return infoGroup;
	}
	
	//合并表达式
	private String[] getCombinLexprFactor(String lexpr,String factor,String seclexpr,String secfactor){
		String[] style=new String[2];
		ArrayList lexprFactor=new ArrayList();
		factor = PubFunc.keyWord_reback(factor);
		lexprFactor.add(lexpr + "|" + factor);
		lexprFactor.add(seclexpr + "|" + secfactor);
		CombineFactor combinefactor=new CombineFactor();
		String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
		if(Stok.hasMoreTokens())
		{
			style[0]=Stok.nextToken();
			style[1]=Stok.nextToken();
		}
		return style;
	}

	/**
	 * 获取统计条件 数据 
	 * @param statid  统计条件id 加密
	 * @param infokind  1 人员
	 * @return
	 */
	private Map getSname(String statid,String infokind) throws GeneralException{
		Map dataHM = new HashMap();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		sql.append("select id,name,flag,type,hv,archive_type,archive_set,nbase,condid,archive,viewtype from sname where id=? and infokind=? ");
		list.add(Integer.parseInt(PubFunc.decrypt(statid)));
		list.add(Integer.parseInt(infokind));
		
		String flag = "" ; //是否查询结果集    1 是  2否
		String chartType = "";//统计图类型
		String statType = "";//统计图类型
		String nbases = "";//人员库
		String[] condid = new String[1]; //分类统计条件
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString(), list);
			if(rs.next()){
				dataHM.put("title", rs.getString("name"));
				dataHM.put("flag", rs.getString("flag"));
				dataHM.put("type", rs.getString("type"));
				dataHM.put("viewtype", rs.getString("viewtype"));
				dataHM.put("nbase", rs.getString("nbase"));
				dataHM.put("condid", rs.getString("condid"));
				dataHM.put("hv", rs.getString("hv"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取常用统计数据出错
			log.error(ResourceFactory.getProperty("static.error.getcommonstaticdatamsg"));
			throw new GeneralException(ResourceFactory.getProperty("static.error.getcommonstaticdatamsg"));
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return dataHM;
	}
	
	/**
	 * 取得定义的主集指标
	 * @return
	 */
	private ArrayList getStatItemList(UserView userView,String factors,String infokind)
	{
		ArrayList statitemlist=new ArrayList();	
		FieldItem fielditem=new FieldItem();
		String fieldname="";
		if("1".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("a0101");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);	
		}else if("2".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
		}else if("3".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
		}
			
		HashSet fieldItemSet=getStatFieldItem(factors,userView);
		Iterator it = fieldItemSet.iterator();		
		while(it.hasNext())
		{
			   String item=(String)it.next();
			   if("1".equals(infokind)){//LiWeichao
				   if("b0110".equalsIgnoreCase(item)|| "e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item)|| "a0101".equalsIgnoreCase(item))
					   continue;
			   }
			   else if("2".equals(infokind))
			   {
				   if("b0110".equalsIgnoreCase(item))
					   continue;
			   }else if("3".equals(infokind))
			   {
				   if("e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item))
					   continue;
			   }
			   fielditem=DataDictionary.getFieldItem(item);
			   fieldname=fielditem.getItemid();
			   fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			   /**
			    * cmq changed at 20120427 因为多表（数据量大）关联查询速度比较慢，
			    * 查询引擎做了优化,子集指标不能直接从返回的SQL取得  
			    */
			   if(("1".equalsIgnoreCase(infokind)&&!fielditem.isMainSet())||("1".equalsIgnoreCase(infokind)&&!fielditem.getFieldsetid().toUpperCase().startsWith("A")))
				   continue;
			   statitemlist.add(fielditem);
		}		
		return statitemlist;
	}
	
	/**
	 * 得到统计项
	 * @param factors
	 * @param userView
	 * @return HashSet
	 */
	public HashSet getStatFieldItem(String factors,UserView userView)
	{
		HashSet fieldItemSet = new HashSet();
		if(factors==null||factors.length()<=0)
			return fieldItemSet;		
		if(factors!=null&&factors.length()>0)
		{
			String[] factorArr =factors.split("`");
			String factorstr=""; 
			for(int i=0;i<factorArr.length;i++)
			{
				factorstr=factorArr[i];
				factorstr=factorstr.toUpperCase();
				Factor factor = new Factor(userView.getDbname(), factorstr);
				String item=factor.getItem();
				if(item!=null&&item.length()>0)
				{
					fieldItemSet.add(item);
				}
			}
		}	
		return fieldItemSet;
	}
	//获取查询字段
	private String getMainQueryFields(ArrayList list,String infokind)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        if("1".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }else if("2".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }else if("3".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("e01a1,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }
        
        return strfields.toString();    	
    }

	private String getStart(int i){
		String [] str={"A","B","C","D","E","F","G","H","I","J","K","O","P","Q","R","S","T","U","V","X","Y","Z"};
		return str[i];
	}
	
	/**
     * 判断内存中是否存在图片，如果存在在内存中获取 否则在库中获取
     * @param dbpre 人员库
     * @param A0100 人员编号
     * @return
     */
    private String getPicUrl(UserView userView,String dbpre,String A0100) throws GeneralException{
    	String url="";
    	String filename="";
    	StringBuffer photourl=new StringBuffer();
    	PhotoImgBo pib = new PhotoImgBo(conn);
		pib.setIdPhoto(true);
    	String absPath = "";
    	boolean genPhotoSuccess=false;
		try{
			absPath = pib.getPhotoRootDir();
		}catch(Exception ex){
			//获取照片路径出错
			log.error(ResourceFactory.getProperty("static.error.getphotopathmsg"));
//			throw new GeneralException(ResourceFactory.getProperty("static.error.getphotopathmsg"));
		}
    	if(absPath != null && absPath.length() > 0){
    		try {
				absPath += pib.getPhotoRelativeDir(dbpre, A0100);
				
				String guid = pib.getGuid();
				//获取 文件名为 “photo.xxx”的文件，格式未知
				String fileWName = pib.getPersonImageWholeName(absPath, "photo");
	
				// 如果不存在文件，创建文件
				if (fileWName.length() < 1) {
					fileWName = pib.createPersonPhoto(absPath, conn, dbpre,
							A0100, "photo");
				}
	
				//如果有图片或创建了图片，使用新图片
				if (fileWName.length() > 0) {
					absPath += fileWName;
					filename = pib.getPhotoPath(dbpre, A0100);
					
					userView.getHm().put(guid, absPath);
					
					// 只要能走到这里，表示照片成功产生了
					genPhotoSuccess = true;
				}
    		}catch (Exception e) {
				e.printStackTrace();
				//获取人员头像出错
				log.error(ResourceFactory.getProperty("static.error.getpersonalphotomsg"));
				throw new GeneralException(ResourceFactory.getProperty("static.error.getpersonalphotomsg"));
			}   		
    	}
    	if(!genPhotoSuccess){
			// 如果不存在文件，创建文件
			try {
				filename = pib.getPhotoPath(dbpre, A0100);
			} catch (Exception e) {
				e.printStackTrace();
				//创建头像出错
				log.error(ResourceFactory.getProperty("static.error.createpersonalphotomsg"));
				throw new GeneralException(ResourceFactory.getProperty("static.error.createpersonalphotomsg"));
			}
    	}
    	return filename;
    }

    /**
	 * 取得定义的主集指标
	 * @return
	 */
	private ArrayList getMainFieldList(UserView userView,String flag)
	{
		ArrayList mainset=new ArrayList();		
		/**取得人员主集已定义的指标*/
		if("1".equals(flag))
		{
			SaveInfo_paramXml infoxml=new SaveInfo_paramXml(this.conn);
			mainset=infoxml.getMainSetFieldList();
			/**如果未定义，则固定四项指标，单位、部门、职位以及姓名*/
			if(mainset.size()==0)
			{
				mainset.add(DataDictionary.getFieldItem("b0110"));
				mainset.add(DataDictionary.getFieldItem("e0122"));
				mainset.add(DataDictionary.getFieldItem("e01a1"));
				mainset.add(DataDictionary.getFieldItem("a0101"));
			}			
			for(int i=0;i<mainset.size();i++)
			{
				FieldItem fielditem=(FieldItem)mainset.get(i);
				String fieldname=fielditem.getItemid();
				fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			}

		}else if("2".equals(flag)){
			mainset.add(DataDictionary.getFieldItem("b0110"));
		}else{
			mainset.add(DataDictionary.getFieldItem("e01a1"));
		}
		return mainset;
	}
	
	/**
	 * 获取查询的指标
	 * @param list
	 * @return
	 */
	private String getMainQueryFields(ArrayList list)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
			if("b0110,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
				continue;  
            if(j!=0)
                strfields.append(",");
            ++j;
          
            strfields.append(item.getItemid());
        }
        return strfields.toString();    	
    }
	
	/**
	 * 获取一维常用统计默认统计图
	 * @param type 统计图类型
	 * @return
	 */
	private String getChartType(String type){
		String chartType = "1";
		//获取统计图类型
		if("11".equalsIgnoreCase(type))//柱状图
			chartType = "1";
		else if("1000".equalsIgnoreCase(type))//折现图
			chartType = "2";
		else if("55".equalsIgnoreCase(type))//雷达图
			chartType = "3";
		else if("20".equalsIgnoreCase(type))//饼图
			chartType = "4";
		return chartType;
	}

	private int getLegendSum(String id){
	    ArrayList list =new ArrayList();
	    list.add(Integer.parseInt(id));
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rs =null;
        try {
            rs = dao.search("select count(1) from sLegend where id=?",list);
            if(rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return 0;
    }

	@Override
	public String outStatisticalExcel(UserView userView, JSONObject viewAndTableData) throws GeneralException {
		String fileName = userView.getUserName() + "_统计图表.xls";
		try{
			//统计图base64数据
			List chartBase64List = JSONArray.toList(viewAndTableData.getJSONArray("chartBase64Arr"));
			//列头数据
			List columnsInfo = JSONArray.toList(viewAndTableData.getJSONArray("columnsInfo"));
			//表格数据
			List tableData = JSONArray.toList(viewAndTableData.getJSONArray("tableData"));
			//一维 二维 多维
			String chartType = viewAndTableData.getString("chartType");

			//一张图占几行
			int occupyCount = 22;
			if (StringUtils.equalsIgnoreCase("more", chartType)) {
				occupyCount = 30;
			}
			//表格头从哪一行开始渲染
			int headStartRowNum = chartBase64List.size() * occupyCount;
			Map oneTableInfoMap = null;
			if (StringUtils.equalsIgnoreCase("one", chartType) || StringUtils.equalsIgnoreCase("double", chartType)) {
				oneTableInfoMap = this.getOneOrDoubleTableInfoMap(columnsInfo, tableData, chartType);
			} else if (StringUtils.equalsIgnoreCase("more", chartType)) {
				oneTableInfoMap = this.getMoreTableInfoMap(columnsInfo, tableData, headStartRowNum);
			}

			ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn, userView);
			ArrayList mergedCellList = (ArrayList) oneTableInfoMap.get("mergedCellList");
			ArrayList headList = (ArrayList) oneTableInfoMap.get("headList");
			ArrayList dataList = (ArrayList) oneTableInfoMap.get("dataList");

			excelUtil.exportExcel("统计图表", mergedCellList, headList, dataList, null, headStartRowNum + 1);
			HSSFSheet sheet = excelUtil.getSheet();
			if (StringUtils.equalsIgnoreCase("more", chartType)) {
				ArrayList<List> customMergeCellList = (ArrayList) oneTableInfoMap.get("customMergeCellList");
				for (List oneMergeCellList : customMergeCellList) {
					ExportExcelUtil.mergeCell(sheet, (Integer) oneMergeCellList.get(0), (Integer) oneMergeCellList.get(1), (Integer) oneMergeCellList.get(2), (Integer) oneMergeCellList.get(3));
				}
			}
			this.renderChartViewToExcel(excelUtil.getWb(), sheet, chartBase64List, occupyCount);
			excelUtil.exportExcel(fileName);
		}catch (Exception e){
			e.printStackTrace();
			throw new GeneralException("outStatisticalExcelError");
		}
		return PubFunc.encrypt(fileName);
	}

	/**
	 * 获取一维、二维表格数据
	 * @author wangbs
	 * @param columnsInfo 列头信息
	 * @param tableData 表格数据
	 * @param chartType 统计图类型
	 * @return java.util.Map
	 * @throws GeneralException 抛出异常
	 * @date 2020/5/14
	 */
	private Map getOneOrDoubleTableInfoMap(List columnsInfo, List tableData, String chartType) throws GeneralException {
		Map oneTableInfoMap = new HashMap();
		ArrayList headList = new ArrayList();
		ArrayList dataList = new ArrayList();
		try {
			LazyDynaBean bean;
			for (Object columnInfo : columnsInfo) {
				Map oneColumn = PubFunc.DynaBean2Map((MorphDynaBean) columnInfo);
				String columnId = (String) oneColumn.get("columnId");
				//不在excel中显示图例列
				if (StringUtils.equalsIgnoreCase(columnId, "legend")) {
					continue;
				}
				bean = new LazyDynaBean();
				String title = (String) oneColumn.get("title");
				HashMap colStyleMap = new HashMap();
				colStyleMap.put("align", HorizontalAlignment.CENTER);
				bean.set("content", title);
				bean.set("colStyleMap", colStyleMap);
				headList.add(bean);
			}

			List oneDataList;
			for (Object tableDatum : tableData) {
				Map oneData = PubFunc.DynaBean2Map((MorphDynaBean) tableDatum);
				oneDataList = new ArrayList();
				if (StringUtils.equalsIgnoreCase(chartType, "one")) {
					oneDataList.add(oneData.get("name"));
					oneDataList.add(oneData.get("thirdId"));
					oneDataList.add(oneData.get("proportion"));
				} else {
					for (int i = 0; i < headList.size(); i++) {
						LazyDynaBean bean1 = (LazyDynaBean) headList.get(i);
						String key = StringUtils.isNotBlank((String) bean1.get("content")) ? (String) bean1.get("content") : "vTitle";
						oneDataList.add(oneData.get(key));
					}
				}
				dataList.add(oneDataList);
			}

			oneTableInfoMap.put("mergedCellList", null);
			oneTableInfoMap.put("headList", headList);
			oneTableInfoMap.put("dataList", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException("");
		}
		return oneTableInfoMap;
	}

	/**
	 * 获取多维表格数据
	 * @author wangbs
	 * @param columnsInfo 列头信息
	 * @param tableData 表格数据
	 * @param headStartRowNum 起始渲染行索引
	 * @return java.util.Map
	 * @throws GeneralException 抛出异常
	 * @date 2020/5/14
	 */
	private Map getMoreTableInfoMap(List columnsInfo, List tableData, int headStartRowNum) throws GeneralException {
		Map oneTableInfoMap = new HashMap();
		//自定义合并横向标题单元格list
		List customMergeCellList = new ArrayList();
		ArrayList mergedCellList = new ArrayList();
		ArrayList headList = new ArrayList();
		ArrayList dataList = new ArrayList();
		String total = "合计";
		try {
			//列样式
			HashMap colStyleMap = new HashMap();
			colStyleMap.put("align", HorizontalAlignment.CENTER);

			LazyDynaBean colBean;
			LazyDynaBean childColBean;
			int nextFromColIndex = 0;
			for (Object columnInfo : columnsInfo) {
				int fromRowNum = headStartRowNum;
				int toRowNum = headStartRowNum + 1;
				int fromColNum = nextFromColIndex;
				int toColNum = fromColNum;
				colBean = new LazyDynaBean();

				//一级title Map
				Map oneColumn = PubFunc.DynaBean2Map((MorphDynaBean) columnInfo);
				//二级title List
				String columnId = (String) oneColumn.get("columnId");
				colBean.set("itemid", columnId);
				colBean.set("content", oneColumn.get("title"));
				colBean.set("colStyleMap", colStyleMap);

				List childColumnsInfo = (ArrayList) oneColumn.get("childColumnsInfo");
				if (CollectionUtils.isEmpty(childColumnsInfo)) {
					nextFromColIndex++;
				} else {
					for (int i = 0; i < childColumnsInfo.size(); i++) {
						Map oneChildColumn = PubFunc.DynaBean2Map((MorphDynaBean) childColumnsInfo.get(i));
						childColBean = new LazyDynaBean();
						childColBean.set("itemid", oneChildColumn.get("columnId"));
						childColBean.set("colStyleMap", colStyleMap);
						childColBean.set("content", oneChildColumn.get("title"));
						childColBean.set("fromRowNum", fromRowNum + 1);
						childColBean.set("toRowNum", toRowNum);
						childColBean.set("fromColNum", fromColNum + i);
						childColBean.set("toColNum", fromColNum + i );

						headList.add(childColBean);
					}
					toRowNum = toRowNum - 1;
					toColNum = toColNum + childColumnsInfo.size() - 1;
					nextFromColIndex = toColNum + 1;
				}
				colBean.set("fromRowNum", fromRowNum);
				colBean.set("toRowNum", toRowNum);
				colBean.set("fromColNum", fromColNum);
				colBean.set("toColNum", toColNum);
				if (StringUtils.equalsIgnoreCase("vTitle1", columnId) || StringUtils.equalsIgnoreCase("vTitle2", columnId) || StringUtils.equalsIgnoreCase(total, columnId)) {
					headList.add(colBean);
				}else{
					mergedCellList.add(colBean);
				}
			}

			LazyDynaBean rowDataBean;
			LazyDynaBean dataBean;
			//自定义的需要合并的单元格
			List oneMergeCellList;
			String kindName = "";
			int mergeStartIndex = headStartRowNum + 2;
			int needMergeCount = 0;
			for (int i = 0; i < tableData.size(); i++) {
				Map<String, Object> oneData = PubFunc.DynaBean2Map((MorphDynaBean) tableData.get(i));
				String vTitle1Content = (String) oneData.get("vTitle1");
				if (StringUtils.isNotBlank(kindName) && !StringUtils.equalsIgnoreCase(kindName, vTitle1Content)) {
					oneMergeCellList = new ArrayList();
					oneMergeCellList.add(mergeStartIndex);
					oneMergeCellList.add(0);
					oneMergeCellList.add(mergeStartIndex + needMergeCount - 1);
					oneMergeCellList.add(0);
					customMergeCellList.add(oneMergeCellList);

					mergeStartIndex = mergeStartIndex + needMergeCount;
					needMergeCount = 0;
				}
				if (i == (tableData.size() - 1) && StringUtils.equalsIgnoreCase(total, vTitle1Content)) {
					oneMergeCellList = new ArrayList();
					oneMergeCellList.add(mergeStartIndex);
					oneMergeCellList.add(0);
					oneMergeCellList.add(mergeStartIndex);
					oneMergeCellList.add(1);
					customMergeCellList.add(oneMergeCellList);
				}
				needMergeCount++;
				kindName = vTitle1Content;

				rowDataBean = new LazyDynaBean();
				for (String key : oneData.keySet()) {
					if (StringUtils.equalsIgnoreCase("color", key) || StringUtils.equalsIgnoreCase("vShowColor", key)) {
						continue;
					}
					dataBean = new LazyDynaBean();
					dataBean.set("content", oneData.get(key) + "");
					rowDataBean.set(key, dataBean);
				}
				dataList.add(rowDataBean);
			}
			oneTableInfoMap.put("customMergeCellList", customMergeCellList);
			oneTableInfoMap.put("mergedCellList", mergedCellList);
			oneTableInfoMap.put("headList", headList);
			oneTableInfoMap.put("dataList", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException("");
		}
		return oneTableInfoMap;
	}
	/**
	 * 将统计图渲染到excel中
	 * @author wangbs
	 * @param wb 工作簿
	 * @param sheet 页签
	 * @param chartBase64List 统计图数据
	 * @param occupyCount 一张图占几行
	 * @return void 空
	 * @throws GeneralException 抛出异常
	 * @date 2020/5/13
	 */
	private void renderChartViewToExcel(HSSFWorkbook wb, HSSFSheet sheet, List chartBase64List, int occupyCount) throws GeneralException {
		HSSFClientAnchor anchor;
		// 利用HSSFPatriarch将图片写入EXCEL
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		try{
			for (int i = 0; i < chartBase64List.size(); i++) {
				String chartBase64 = (String) chartBase64List.get(i);
				chartBase64 = chartBase64.split(",")[1];
				byte[] bytes = Base64.decodeBase64(chartBase64);
				/**
				 * 该构造函数有8个参数
				 * 前四个参数是控制图片在单元格的位置，分别是图片距离单元格left，top，right，bottom的像素距离
				 * 后四个参数，前连个表示图片左上角所在的cellIndex和 rowIndex，后天个参数对应的表示图片右下角所在的cellIndex和 rowIndex，
				 * excel中的cellIndex和rowIndex的值都是从0开始的
				 */
				//图片导出到单元格中
				anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, i * occupyCount, (short) 20, (i + 1) * occupyCount);
				// 插入图片
				patriarch.createPicture(anchor, wb.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_PNG));
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new GeneralException("");
		}
	}

	/**
	 * 检验常用统计条件id表中是否存在
	 * @return
	 */
	private boolean checkCurrId(String[] currid) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search("select 1 from lexpr where id=? ", Arrays.asList(currid[0]));
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
