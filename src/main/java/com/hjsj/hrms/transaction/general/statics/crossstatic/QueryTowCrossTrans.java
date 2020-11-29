package com.hjsj.hrms.transaction.general.statics.crossstatic;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.ShowTowCrossAction;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryTowCrossTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String lengthways = (String) hm.get("lengthways");
		hm.remove("lengthways");
		hm.remove("statid");
		if(lengthways == null ){//add by xiegh on date bug36380  form类中是往 this.getFormHM()塞的  这里是从hm中取  现在兼容源代码 如果hm不存在 则从 this.getFormHM()中取
			lengthways = (String) this.getFormHM().get("lengthways");
		}
		String crosswise = (String) hm.get("crosswise");
		hm.remove("crosswise");
		if(crosswise == null ){
			crosswise = (String) this.getFormHM().get("crosswise");
		}
		String statIdString = lengthways + crosswise;
		String[] statIdlist = statIdString.split(",");
		String[] lengthlist = lengthways.split(",");
		String[] crosslist = crosswise.split(",");
		
		ShowTowCrossAction showTowCrossAction = new ShowTowCrossAction();
		ArrayList lengthways_dimension_list =new ArrayList();
		lengthways_dimension_list = showTowCrossAction.getDimension(dao,lengthways);
		ArrayList crosswise_dimension_list =new ArrayList();
		crosswise_dimension_list = showTowCrossAction.getDimension(dao,crosswise);
		this.getFormHM().put("lengthways_dimension_list", lengthways_dimension_list);
		this.getFormHM().put("crosswise_dimension_list", crosswise_dimension_list);
		
		//liuy 2014-12-5 修改存取可选维度的值  start
		ArrayList all_select_dimension_list = (ArrayList) this.getFormHM().get("all_select_dimension_list");//固定不变的可选维度
		ArrayList select_dimension_list = new ArrayList();//一直改变的可选维度
		select_dimension_list.addAll(all_select_dimension_list);
		this.getFormHM().put("all_select_dimension_list",all_select_dimension_list);
		
		statIdString = statIdString.replaceAll("1_", "");
		statIdString = statIdString.replaceAll("2_", "");
		String[] idList = statIdString.split(",");

		for (int i = 0; i < idList.length; i++) {//循环把选中的维度id从固定不变的维度id集合中取出
			for (int j = 0; j < select_dimension_list.size(); j++) {
				CommonData cdata=(CommonData)select_dimension_list.get(j);
			    String value = cdata.getDataValue();
				if(value.equals(idList[i])){					
					select_dimension_list.remove(j);
					break;
				}
			}
		}
		this.getFormHM().put("select_dimension_list",select_dimension_list);//保存改变后的维度
		//liuy 2014-12-5  end
		
		String userbases = (String) this.getFormHM().get("userbases");
//		String userbases = (String) hm.get("dbname");
		//String userbase=(String)this.getFormHM().get("dbname");
		String userbase="Usr";
		CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,
				this.userView);
		// statId=checkPrivSafeBo.checkResource(IResourceConstant.STATICS,
		// statId);
		String querycond = (String) this.getFormHM().get("querycond");// 组织机构
		// String infokind=(String)this.getFormHM().get("infokind");
		String infokind = "1";
		String isshowstatcond = (String) this.getFormHM().get("isshowstatcond");
		String[] curr_id = (String[]) this.getFormHM().get("curr_id");
		String preresult = (String) this.getFormHM().get("preresult");
		String sformula = (String) this.getFormHM().get("sformula");
		sformula = sformula == null ? "" : sformula;
		if (preresult == null || preresult.length() <= 0)
			preresult = "2";
		if ("1".equals(preresult))
			curr_id = null;
		if (curr_id != null && curr_id.length > 0) {
			if (curr_id[0] != null && "#".equals(curr_id[0]))
				curr_id = null;
		}
		if (curr_id == null && infokind != null && "1".equals(infokind)) {
			String statid = (String)this.getFormHM().get("statid");
			String stat_id = (String) this.getFormHM().get("complex_id");
			if (statid == null &&stat_id != null && stat_id.length() > 0) {//此处是多维统计设置的统计条件。 常用统计多维不能走  wangb 2010-03-25 bug 58781
				String[] stat_ids = new String[1];
				stat_ids[0] = stat_id;
				curr_id = stat_ids;
				this.getFormHM().put("complex_id", stat_id);
			}
		}
		
		
		if ("0".equals(preresult))
			preresult = "2";
		String history = (String) this.getFormHM().get("history");
		// 加上常用查询进行的统计
		String commlexr = null;
		String commfacor = null;
		GeneralQueryStat generalstat = new GeneralQueryStat();
		generalstat.getGeneralQueryLexrfacor(curr_id, userbase, history, this
				.getFrameconn());

		if (curr_id != null) {
			commlexr = generalstat.getLexpr();
			commfacor = generalstat.getLfactor();
			history = generalstat.getHistory();
		}
		this.getFormHM().put("isshowstatcond", isshowstatcond);
		StatDataEncapsulation simplestat = new StatDataEncapsulation();
		String org_filter=(String) this.formHM.get("org_filter");
		if("1".equalsIgnoreCase(org_filter)){//按机构筛选 逻辑  多维统计 wangb 2019-08-19
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
	    }
		showTowCrossAction.setConn(this.frameconn);
		showTowCrossAction.setUserView(userView);
		showTowCrossAction.setCommlexr(commlexr);
		showTowCrossAction.setCommfacor(commfacor);
		try {
			userbases = userbases == null ? "" : userbases;
			userbases = new String(userbases.getBytes("ISO-8859-1"));
			userbases = userbases.replaceAll("，", "`");
			if("".equals(userbases)){
				String nbase = "";
				for(int j = 0;j < statIdlist.length;j++){
					String statId = statIdlist[j].substring(statIdlist[j].lastIndexOf("_")+1);
					String sql="select * from sname where id=" + statId;
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next())
					{
						infokind=this.frowset.getString("infokind");
						nbase=this.frowset.getString("nbase");
						if(nbase!=null&&nbase.length()>0)
						{
							String [] baseS=nbase.split(",");
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
												if(userbases.indexOf(baseS[i])==-1){
													userbases+=baseS[i]+"`";
												}
											}
										} 
									}else
									{
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
			// String statId=(String)this.getFormHM().get("statid");
			/* zgd 多张统计图数据分析区 start*/
			showTowCrossAction.setUserbases(userbases);
			showTowCrossAction.getTwoCrossChart(statIdlist, sformula, simplestat, preresult, history);
			String showChart=(String)this.getFormHM().get("showChart");
			if(showChart!=null){				
				this.getFormHM().put("showChart", showChart);
			}else{
				this.getFormHM().put("showChart", "1");				
			}
			this.getFormHM().put("statIdslist", showTowCrossAction.getStatIdslist());
			this.getFormHM().put("decimalwidthlist", showTowCrossAction.getDecimalwidthlist());
			this.getFormHM().put("isneedsumlist", showTowCrossAction.getIsneedsumlist());
			this.getFormHM().put("snamedisplaylist", showTowCrossAction.getSnamedisplaylist());
			this.getFormHM().put("listlist", showTowCrossAction.getListlist());
			this.getFormHM().put("jfreemaplist", showTowCrossAction.getJfreemaplist());
			this.getFormHM().put("label_enabledlist", showTowCrossAction.getLabel_enabledlist());
			this.getFormHM().put("xanglelist", showTowCrossAction.getXanglelist());
			this.getFormHM().put("sformulalist", showTowCrossAction.getSformulalist());
			this.getFormHM().put("archive_setvolist", showTowCrossAction.getArchive_setvolist());
			/* zgd 多张统计图数据分析区 end*/
			/* zgd 二维交叉表数据分析区 start*/
			String vtotal = (String) hm.get("vtotal");
			hm.remove("vtotal");
			if(vtotal==null){
				vtotal = (String)this.getFormHM().get("vtotal");
			}
			vtotal=vtotal==null||vtotal.length()==0?"0":vtotal;
			String htotal = (String) hm.get("htotal");
			hm.remove("htotal");
			if(htotal==null){
				htotal = (String)this.getFormHM().get("htotal");
			}
			htotal=htotal==null||htotal.length()==0?"0":htotal;
			String vnull = (String) hm.get("vnull");
			hm.remove("vnull");
			if(vnull==null){
				vnull = (String)this.getFormHM().get("vnull");
			}
			vnull=vnull==null||vnull.length()==0?"0":vnull;
			hm.remove("vnull");
			String hnull = (String) hm.get("hnull");
			if(hnull==null){
				hnull = (String)this.getFormHM().get("hnull");
			}
			hnull=hnull==null||hnull.length()==0?"0":hnull;
			if("0".equals(vtotal) || "0".equals(htotal)) {//横纵向合计 只要有一个不合计，不允许隐藏空行空列 wangb 2020-03-27
				hnull = "0";
				vnull = "0";
			}
			showTowCrossAction.getTwoCrossTable(vtotal, htotal, vnull, hnull, lengthlist, crosslist, preresult, history, sformula);
			this.getFormHM().put("lengthways",lengthways);
			this.getFormHM().put("crosswise",crosswise);
			this.getFormHM().put("statdoublevalues",showTowCrossAction.getStatValues());
			this.getFormHM().put("statdoublevaluess",showTowCrossAction.getStatValuess());
			this.getFormHM().put("totalvalue",Double.toString(showTowCrossAction.getTotalvalues()));
			this.getFormHM().put("varrayfirstlist",showTowCrossAction.getVarrayfirstlist());
			this.getFormHM().put("varraysecondlist",showTowCrossAction.getVarraysecondlist());
			this.getFormHM().put("harrayfirstlist",showTowCrossAction.getHarrayfirstlist());
			this.getFormHM().put("harraysecondlist",showTowCrossAction.getHarraysecondlist());
			this.getFormHM().put("commlexr", showTowCrossAction.getCommlexr());
			this.getFormHM().put("commfacor", showTowCrossAction.getCommfacor());
			this.getFormHM().put("vtotal", vtotal);
			this.getFormHM().put("htotal", htotal);
			this.getFormHM().put("vnull", vnull);
			this.getFormHM().put("hnull", hnull);
			this.getFormHM().put("dbname", userbases);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException("", e.toString(), "", "");
		}
	}
	
}
