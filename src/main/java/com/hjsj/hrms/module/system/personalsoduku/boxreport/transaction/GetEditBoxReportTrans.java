package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.module.system.personalsoduku.boxreport.businessobject.BoxReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 得到编辑的数据
* <p>Title:GetEditBoxReportTrans </p>
* <p>Description: </p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 12, 2015 2:58:18 PM
 */
public class GetEditBoxReportTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String cassetteid = (String)this.getFormHM().get("cassetteid");//盒式报表id
		String flag = (String)this.getFormHM().get("flag");
		String sodukusql = (String)this.getFormHM().get("sodukusql");
		HashMap map = new HashMap();
		ArrayList sodukulist = new ArrayList();
		ArrayList dbarr = new ArrayList();
		BoxReportBo bo = new BoxReportBo(this.frameconn,this.userView);
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rst = null;
		try {
			String sql ="select * from t_sys_box_report where box_id='"+cassetteid+"'";
			rst = dao.search(sql);
			while(rst.next()){
				String cassette_name = rst.getString("name");//名称
				String data_source = rst.getString("data_from");//数据源
				String lateral_index = rst.getString("h_field");//横向指标
				int percentage = rst.getInt("show_percent");
				HashMap lateralMap = bo.getIndexMap(lateral_index,data_source);
				
				String lateral_desc = rst.getString("h_field_desc");
				String longitudinal_index = rst.getString("v_field");//纵向指标
				
				HashMap longitudinalMap = bo.getIndexMap(longitudinal_index,data_source);
				
				String longitudinal_desc = rst.getString("v_field_desc");
				String time_dimension = rst.getString("time_dim_field");//时间维度
				
				HashMap dimension = bo.getIndexMap(time_dimension,data_source);
				
				String analysis_interval = rst.getString("time_dim_type");//分析区间
				String staff_view_url = rst.getString("staff_view_url");
				String staff_listview_url = rst.getString("staff_listview_url");
				String personnel_range = rst.getString("static_ids");
				
				ArrayList<HashMap> personallist = bo.getpersonalList(personnel_range);
				
				ArrayList<HashMap<String,String>> indexlist = new ArrayList<HashMap<String,String>>();
				
				if("2".equals(flag)){
					if(!"".equals(sodukusql)){
						sodukusql = PubFunc.decrypt(sodukusql);
					}
					//获得视图中人员库前缀
					ArrayList dbnameList = bo.getDbnamelist(data_source,dao);
					ArrayList dnnamelist = new ArrayList();
					for(Object dbname:dbnameList){
						dbname = dbname.toString().toLowerCase();
						dnnamelist.add(dbname);
					}
					//获得登录人的人员库权限
					StringBuffer sb = this.userView.getDbpriv();//,Usr,Ret,Trs,Oth,
					
					if(this.userView.isSuper_admin()){
						dbarr = dbnameList;
					}else if(sb.toString().indexOf(",")!=-1){
						String [] arr = sb.toString().split(",");
						for(String ar:arr){
							if("".equals(ar)){
							}else{
								ar = ar.toLowerCase();
								if(dnnamelist.indexOf(ar)!=-1){
									dbarr.add(ar);
								}
							}
						}
					}
					
					//获得人员范围勾选项
					ArrayList perlist = (ArrayList)this.getFormHM().get("perlist");
					ArrayList perwhereList = new ArrayList();
					if(dbarr.size()>0){
						if(perlist!=null){
							for(int m=0;m<dbarr.size();m++){
								String dbsql="";
								for(int i=0;i<perlist.size();i++){
									MorphDynaBean md = (MorphDynaBean)perlist.get(i);
									Object obj = md.get("check");
									ArrayList check = (ArrayList)obj;
									String checkSql="";
									for(int j=0;j<check.size();j++){
										MorphDynaBean mdb = (MorphDynaBean)check.get(j);
										String lexpr = (String)mdb.get("lexpr");
										String factor = (String)mdb.get("factor");
										String infokind = (String)mdb.get("infokind");
										String historyflag = (String)mdb.get("flag");
										String sflag = (String)mdb.get("sflag");
										boolean ishavehistory=false;
								    	
							            if(historyflag!=null&&"1".equals(historyflag))
							                ishavehistory=true;
							            
							            boolean isresult=true;	  
							            if(sflag!=null&&"1".equals(sflag))
											isresult=false; 
										String whereSql = "";
										StatCondAnalyse cond = new StatCondAnalyse();
										if(infokind!=null && "1".equals(infokind)){
											whereSql=cond.getCondQueryString(lexpr,factor,(String)dbnameList.get(m),ishavehistory,userView.getUserName(),"",userView,infokind,isresult,false);
											int k=whereSql.indexOf("FROM "+(String)dbnameList.get(m)+"A01 WHERE ");
											whereSql=whereSql.substring(k+17, whereSql.length());
											if(checkSql.length()!=0){
											    checkSql+=" or "+whereSql;
											}else{
												checkSql=whereSql;
											}
										}else if(infokind!=null && "2".equals(infokind)){//暂时不用
											whereSql=cond.getCondQueryString(lexpr,factor,"B",ishavehistory,userView.getUserName(),"",userView,infokind,isresult,false);
										}else if(infokind!=null && "3".equals(infokind)){//暂时不用
											whereSql=cond.getCondQueryString(lexpr,factor,"K",ishavehistory,userView.getUserName(),"",userView,infokind,isresult,false);
										}
									}
									if(dbsql.length()!=0){
										dbsql+=" and ("+checkSql+")";
									}else {
										dbsql="select a0100 from "+(String)dbnameList.get(m)+"A01 where ("+checkSql+")";
									}
								}
								if(!"".equals(dbsql)){
									perwhereList.add(dbsql);
								}
							}
						}
					}
					
					ArrayList codewhereList = new ArrayList();
					
					//获得组织范围勾选项
					ArrayList codelist = (ArrayList)this.getFormHM().get("codelist");
					if(codelist!=null){
						for(int k=0;k<codelist.size();k++){
							String codesql = "";
							MorphDynaBean md = (MorphDynaBean)codelist.get(k);
							String codeitemid = (String)md.get("codeitemid");
							String codesetid = (String)md.get("codesetid");
							if("UN".equals(codesetid)){//单位
								codesql = " B0110 like '"+codeitemid+"%' ";
							}
							else if("UM".equals(codesetid)){//部门
								codesql = " E0122 like '"+codeitemid+"%' ";
							}
							if(!"".equals(codesql)){
								codewhereList.add(codesql);
							}
						}
					}
					//时间维度选择项
					ArrayList datelist = (ArrayList)this.getFormHM().get("datelist");
					if(this.userView.isSuper_admin()||dbarr.size()>0){
						sodukulist = bo.getSoduku(lateral_index,longitudinal_index,data_source,sodukusql,perwhereList,
								                  codewhereList,cassetteid,data_source,datelist,time_dimension,dbarr);
					}
//					String latcode=bo.getCodeItem(lateral_index,data_source,dao,"ASC");//横坐标指标项分项
					ArrayList latlist = bo.getcodeMap(lateral_index,data_source,dao,"DESC");

//					String longcode = bo.getCodeItem(longitudinal_index,data_source,dao,"DESC");//纵坐标指标项分项
					ArrayList longlist = bo.getcodeMap(longitudinal_index,data_source,dao,"ASC");

					map.put("sodukulist", sodukulist);
					map.put("latlist", latlist);
					map.put("longlist", longlist);
				}
				indexlist = bo.gethzindex(data_source);
				map.put("cassette_name", cassette_name);
				map.put("data_source", data_source);
				map.put("lateral_index", lateralMap);
				map.put("lateral_desc", lateral_desc);
				map.put("longitudinal_index", longitudinalMap);
				map.put("longitudinal_desc", longitudinal_desc);
				map.put("time_dimension", dimension);
				map.put("analysis_interval", analysis_interval);
				map.put("personnel_range", personallist);
				map.put("indexlist", indexlist);
				map.put("percentage", percentage);
				map.put("staff_view_url", staff_view_url);
				map.put("staff_listview_url", staff_listview_url);
			}
			this.formHM.put("editmap", map);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 PubFunc.closeDbObj(rst);
		}
	}
}
