package com.hjsj.hrms.transaction.general.statics.crossstatic;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.ShowTowCrossAction;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 
 * Title:InitTowCrossTrans
 * Description:二维交叉统计
 * Company:hjsj
 * Create time:Aug 15, 2014:3:16:59 PM
 * @author zhaogd
 * @version 6.x
 */
public class InitTowCrossTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		//liuy 2014-9-16 二维交叉统计保存 start
		String statid = (String)hm.get("statid");
		hm.remove("statid");
		String crossshow = (String)hm.get("crossshow");
		hm.remove("crossshow");
		String categories = (String)hm.get("categories");
		categories=categories==null?"":categories;
		hm.remove("categories");
		if ("3".equals(crossshow)) {
			try {
				String sql = "";
				if(!"".equals(categories)&&categories!=null)					
					sql="select * from sname where infokind in ('1') and categories='"+categories+"' and type in (3) order by snorder";
				else				
					sql="select * from sname where infokind in ('1') and type in (3) order by snorder";
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					statid = this.frowset.getString("id");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String statidFlag=statid==null||statid.length()==0?"false":"true";
		this.getFormHM().put("statid", statid);
		this.getFormHM().put("statidFlag", statidFlag);
		
		String showcond=(String)hm.get("showcond");
		showcond=showcond==null?"1":showcond;
		String stat=(String)hm.get("stat");//用户配置的可选维度
		stat=stat==null?"":stat;
		
		String statFlag="";//=0，从数据库中取可选维度；=1，用户配置了可选维度
		if(stat==null||"".equals(stat)){
			statFlag="0";
		}else{
			statFlag="1";
		}
		//liuy 2014-12-5 修改存取可选维度的值  start
		ArrayList all_select_dimension_list = getAllSelectDimension(dao,statFlag,stat);//初始化固定可选维度
		ArrayList select_dimension_list = getSelectDimension(dao,statFlag,stat);//初始化变化可选维度
		
		this.getFormHM().put("all_select_dimension_list",all_select_dimension_list);//保存初始化后的固定可选维度
		this.getFormHM().put("select_dimension_list",select_dimension_list);//保存初始化后的变化可选维度
		
		String dbname=(String)hm.get("dbname");
		if("".equals(dbname)||dbname==null){
			dbname = (String)this.getFormHM().get("dbname");
		}
		dbname=dbname==null?"":dbname;
		if("".equals(dbname))
			dbname = "Usr";
		ArrayList dblist=userView.getPrivDbList();
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		ArrayList lists=new ArrayList();
		String userbasesstr = "";
		String viewuserbasesstr = "";
		for(int i=0;i<dblist.size();i++)
		{
			CommonData vo=new CommonData();
			RecordVo db=(RecordVo)dblist.get(i);
			vo.setDataName(db.getString("dbname"));
			vo.setDataValue(db.getString("pre"));
			if(dbname.toUpperCase().indexOf(db.getString("pre").toUpperCase())!=-1){						
				userbasesstr += db.getString("pre")+",";
				viewuserbasesstr += db.getString("dbname")+";";
			}
			lists.add(vo);
		}
		if(userbasesstr.length()>1){				
			this.getFormHM().put("userbases", userbasesstr.substring(0, userbasesstr.length()-1));
			this.getFormHM().put("viewuserbases", viewuserbasesstr.substring(0, viewuserbasesstr.length()-1));
		}
		this.getFormHM().put("dblist",lists);
		String org_filter=(String) this.formHM.get("org_filter");
		this.formHM.remove("org_filter");
		if(statid!=null && statid.trim().length()>0){
			this.getFormHM().put("type", "0");//区分数据显示 ="",常用多维统计项
			
			ShowTowCrossAction showTowCrossAction = new ShowTowCrossAction();
			
			String hv="",dbbase="",condid="",hnull="",vnull="",show_chart="",htotal="",vtotal="";
			String sqlstr="select nbase,condid,HV,hide_empty_row,hide_empty_col,show_chart,show_sum_h,show_sum_v,org_filter from SName where Id="+statid;
			try {
				this.frowset=dao.search(sqlstr);
				if(this.frowset.next())
				{
					hv=this.frowset.getString("HV");
					dbbase=this.frowset.getString("nbase");
					condid=","+this.frowset.getString("condid")+",";
					hnull=this.frowset.getString("hide_empty_row");
					vnull=this.frowset.getString("hide_empty_col");
					show_chart=this.frowset.getString("show_chart");
					htotal=this.frowset.getString("show_sum_h");
					vtotal=this.frowset.getString("show_sum_v");
					org_filter = String.valueOf(this.frowset.getInt("org_filter"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.getFormHM().put("org_filter", org_filter);
			this.getFormHM().put("dbname",dbbase);
			ArrayList list =new ArrayList();
			String condsql="select id,name from lexpr where type='1'";
			String complex_id = "";
			try {
				this.frowset=dao.search(condsql);
				int i = 0;
				while(this.frowset.next())
				{
					if(condid.indexOf(","+this.frowset.getString("id")+",")!=-1){
						CommonData dataobj = new CommonData(this.frowset.getString("id"),this.getFrowset().getString("name"));
						list.add(dataobj);
						if(i==0){
							complex_id=this.frowset.getString("id");
						}
						i++;
						
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				this.getFormHM().put("complex_id", complex_id);
				this.getFormHM().put("condlist", list);
			}
			this.getFormHM().put("showChart", show_chart);
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
			this.getFormHM().put("lengthways_dimension_list", lengthways_dimension_list);
			this.getFormHM().put("crosswise_dimension_list", crosswise_dimension_list);
			
			
			String userbases = dbbase;
			String userbase="Usr";
			
			String infokind = "1";
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
			if(statid != null && statid.trim().length() > 0 && "1".equalsIgnoreCase(org_filter)) {//按组织机构过滤 不走统计范围
				curr_id = null;
			}
			if (curr_id == null && infokind != null && "1".equals(infokind)) {

				String stat_id = complex_id;
				if (stat_id != null && stat_id.length() > 0 && !"###".equals(stat_id)&&!"＃＃＃".equals(stat_id)) {
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
			generalstat.getGeneralQueryLexrfacor(curr_id, userbase, history, this.getFrameconn());
			commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
			
			StatDataEncapsulation simplestat = new StatDataEncapsulation();
			if(statid != null && statid.trim().length() > 0 && "1".equalsIgnoreCase(org_filter)){//按机构筛选 逻辑  多维统计 wangb 2019-08-19
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
				
				showTowCrossAction.setUserbases(userbases);
				if("1".equals(show_chart)){					
					showTowCrossAction.getTwoCrossChart(statIdlist, sformula, simplestat, preresult, history);
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
				}

				
				String home=(String)hm.get("home");
				hm.remove("home");
				home=home==null?"0":home;
				this.getFormHM().put("home",home);
				
				showTowCrossAction.getTwoCrossTable( vtotal, htotal, vnull, hnull, lengthlist, crosslist, preresult, history, sformula);
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
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("", e.toString(), "", "");
			}
			//liuy 2014-9-16 二维交叉统计保存 end
		}else {
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
			if(crosswise !=null && lengthways != null) {
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
				all_select_dimension_list = (ArrayList) this.getFormHM().get("all_select_dimension_list");//固定不变的可选维度
				select_dimension_list = new ArrayList();//一直改变的可选维度
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

					String stat_id = (String) this.getFormHM().get("complex_id");
					if (stat_id != null && stat_id.length() > 0) {
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
		
		String type=(String)hm.get("type");
		type=type==null?"":type;
		String showdbname=(String)hm.get("showdbname");
		showdbname=showdbname==null?"":showdbname;
		
		String cond=(String)hm.get("cond");
		cond=cond==null?"":cond;
		
		//liuy 2014-12-5 end
		ArrayList list =new ArrayList();
		String complex_id=(String)this.getFormHM().get("complex_id");
		StringBuffer sql = new StringBuffer();
		if(cond!=null&&!"".equals(cond)){
			try {
				String[] condstr = cond.split(",");
				for(int i=0;i<condstr.length;i++){
					sql.setLength(0);
					sql.append("select id,name from lexpr where type='1'");
					sql.append(" and id="+condstr[i]);
					sql.append(" order by norder");
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next())
					{
						if((userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
						{
							CommonData dataobj = new CommonData(this.frowset.getString("id"),this.getFrowset().getString("name"));
							list.add(dataobj);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			if("0".equals(type)){
				list = (ArrayList)this.getFormHM().get("condlist");
			}else{					
				sql.append("select id,name from lexpr where type='1' order by norder");
				try {
					this.frowset=dao.search(sql.toString());
					while(this.frowset.next())
					{
						if((userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
						{
							CommonData dataobj = new CommonData(this.frowset.getString("id"),this.getFrowset().getString("name"));
							list.add(dataobj);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		this.getFormHM().put("complex_id", complex_id);
		this.getFormHM().put("condlist", list);

		this.getFormHM().put("type", type);
		this.getFormHM().put("showdbname",showdbname);
//		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("showcond",showcond);
		this.getFormHM().put("cond",cond);
		this.getFormHM().put("stat",stat);
		//liuy 2014-12-5 修改存取可选维度的值  start
		//liuy 2014-12-5 end
	}
	
	/**
	 * 查询所有可选维度
	 * @param dao
	 * @param statFlag
	 * @param stat
	 * @return
	 */
	private ArrayList getAllSelectDimension(ContentDAO dao,String statFlag,String stat){
		ArrayList allList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		try {
			if("0".equals(statFlag)){				
				sql.append("select name,id from sname where InfoKind='1' and Type='1'");
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
					if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
					{
						allList.add(cdata);
					}
				}
			}else if("1".equals(statFlag)){
				String[] statstr = stat.split(",");
				for(int i=0;i<statstr.length;i++){
					sql.setLength(0);
					sql.append("select name,id from sname where InfoKind='1' and Type='1'");
					sql.append(" and id="+statstr[i]);
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next())
					{
						CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
						if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
						{
							allList.add(cdata);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allList;
	}
	
	/**
	 * 查询当前可选维度
	 * @param dao
	 * @param statFlag
	 * @param stat
	 * @return
	 */
	private ArrayList getSelectDimension(ContentDAO dao,String statFlag,String stat) {
		ArrayList allList = new ArrayList();
		ArrayList idList = new ArrayList();//横纵维度选中的一维统计项的id集合
		StringBuffer sql = new StringBuffer();
		try {
			ArrayList lengthways_dimension_list = (ArrayList)this.getFormHM().get("lengthways_dimension_list");//从form里面得到选中的纵向维度的值
			ArrayList crosswise_dimension_list = (ArrayList)this.getFormHM().get("crosswise_dimension_list");//从form里面得到选中的横向维度的值
			String lengthways,crosswise,id;//纵向维度的单个值，横向维度的单个值，一维统计项的id
			for (int i = 0; i < lengthways_dimension_list.size(); i++) {//循环取出纵向维度选中id，并放进一维统计项的id集合里面
				lengthways = lengthways_dimension_list.get(i).toString();
				if(lengthways.indexOf("1_")!=-1){
					id = lengthways.substring(lengthways.indexOf("1_")+2,lengthways.length()-1);
					idList.add(id);
				}else if (lengthways.indexOf("2_")!=-1) {
					id = lengthways.substring(lengthways.indexOf("2_")+2,lengthways.length()-1);
					idList.add(id);
				}
			}
			for (int i = 0; i < crosswise_dimension_list.size(); i++) {//循环取出横向维度选中id，并放进一维统计项的id集合里面
				crosswise = crosswise_dimension_list.get(i).toString();
				if(crosswise.indexOf("1_")!=-1){
					id = crosswise.substring(crosswise.indexOf("1_")+2,crosswise.length()-1);
					idList.add(id);
				}else if (crosswise.indexOf("2_")!=-1) {
					id = crosswise.substring(crosswise.indexOf("2_")+2,crosswise.length()-1);
					idList.add(id);
				}
			}
			if("0".equals(statFlag)){				
				sql.append("select name,id from sname where InfoKind='1' and Type='1'");
				for (int i = 0; i < idList.size(); i++) {//循环把选中的id从未选的id集合中取出
					sql.append(" and id<>"+idList.get(i).toString());
				}
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
					if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
					{
						allList.add(cdata);
					}
				}
			}else if("1".equals(statFlag)){
				String[] statstr = stat.split(",");
				boolean flag=false;
				for(int i=0;i<statstr.length;i++){
					flag=false;
					sql.setLength(0);
					sql.append("select name,id from sname where InfoKind='1' and Type='1'");
					for (int j = 0; j < idList.size(); j++) {//循环把选中的id从未选的id集合中取出
						if(statstr[i]!=null&&!"".equals(statstr[i])){							
							if(statstr[i].equals(idList.get(j).toString())){
								flag=true;
								continue;
							}
						}
					}
					if(flag){
						continue;
					}
					sql.append(" and id="+statstr[i]);
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next())
					{
						CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
						if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
						{
							allList.add(cdata);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allList;
	}

}
