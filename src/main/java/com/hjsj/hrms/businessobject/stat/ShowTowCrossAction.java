package com.hjsj.hrms.businessobject.stat;

import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowTowCrossAction {

	public UserView userView;
	public Connection conn=null;
	public String infokind="1";
	public String userbases="";
	public String userbase="Usr";
	public String querycond="";
	
	public ArrayList statIdslist = new ArrayList();
	public ArrayList decimalwidthlist = new ArrayList();
	public ArrayList isneedsumlist = new ArrayList();
	public ArrayList snamedisplaylist = new ArrayList();
	public ArrayList listlist = new ArrayList();
	public ArrayList jfreemaplist = new ArrayList();
	public ArrayList label_enabledlist = new ArrayList();
	public ArrayList xanglelist = new ArrayList();
	public ArrayList sformulalist = new ArrayList();
	public ArrayList archive_setvolist = new ArrayList();

	
	public double[][] statValuess=null;
	public int[][] statValues=null;
	public double totalvalues = 0;
	public ArrayList varrayfirstlist = new ArrayList();
	public ArrayList varraysecondlist = new ArrayList();
	public ArrayList harrayfirstlist = new ArrayList();
	public ArrayList harraysecondlist = new ArrayList();
	public String commlexr="";
	public String commfacor="";
	
	
	public void getTwoCrossChart(String[] statIdlist,String sformula,StatDataEncapsulation simplestat,String preresult,String history){
		ContentDAO dao = new ContentDAO(this.conn);
		int[] statvalues = null;
		double[] statvaluess = null;
		String[] fieldDisplay = null;
		String SNameDisplay = "";
		try {
			for (int k = 0; k < statIdlist.length; k++) {
				ArrayList list = new ArrayList();
				CommonData decimalwidthvo = new CommonData();
				CommonData isneedsumvo = new CommonData();
				CommonData snamedisplayvo = new CommonData();
				HashMap listmap = new HashMap();
				HashMap jfreemapmap = new HashMap();
				CommonData label_enabledvo = new CommonData();
				CommonData xanglevo = new CommonData();
				CommonData sformulavo = new CommonData();
				CommonData archive_setvo = new CommonData();
				CommonData statIdvo = new CommonData();
				if (statIdlist[k] != null && !"".equals(statIdlist[k])) {
					String statId = statIdlist[k].substring(statIdlist[k].lastIndexOf("_")+1);
					statIdvo.setDataName("statId" + k);
					statIdvo.setDataValue(statId);
					//liuy 2015-1-26 6973：非SU用户，常用统计的多维统计，有2项未授权，结果出现的图例就不对了，如附件图所示 start
					//if (null != statId&& !userView.isHaveResource(IResourceConstant.STATICS, statId)) {
						//statId = "-1";
					//}
					//liuy 2015-1-26 end
					boolean isresult = true;
					if (sformula.length() > 0) {
						SformulaXml xml = new SformulaXml(conn,statId);
						Element element = xml.getElement(sformula);
						if (element == null) {
							sformula = "";
							decimalwidthvo.setDataName("decimalwidth" + k);
							decimalwidthvo.setDataValue("0");
							isneedsumvo.setDataName("isneedsum" + k);
							isneedsumvo.setDataValue("true");
						} else {
							String decimalwidth = element.getAttributeValue("decimalwidth");
							decimalwidth = (decimalwidth == null || decimalwidth.length() == 0) ? "2" : decimalwidth;
							decimalwidthvo.setDataName("decimalwidth" + k);
							decimalwidthvo.setDataValue(decimalwidth);
							String type = element.getAttributeValue("type");
							if ("sum".equalsIgnoreCase(type)|| "count".equalsIgnoreCase(type)) {
								isneedsumvo.setDataName("isneedsum" + k);
								isneedsumvo.setDataValue("true");
							} else {
								isneedsumvo.setDataName("isneedsum" + k);
								isneedsumvo.setDataValue("false");
							}
						}
					} else {
						decimalwidthvo.setDataName("decimalwidth" + k);
						decimalwidthvo.setDataValue("0");
						isneedsumvo.setDataName("isneedsum" + k);
						isneedsumvo.setDataValue("true");
					}
		
					if (userbases == null || userbases.length() == 0) {
						if (userbase == null || userbase.length() == 0) {
		
						}
						if (sformula.length() > 0) {
                            statvaluess = simplestat.getLexprDataSformula(
                                    userbase.toUpperCase(), Integer.parseInt(statId), querycond,
                                    userView.getUserName(), userView.getManagePrivCode(), userView,
                                    infokind, isresult, commlexr, commfacor,
                                    preresult, history, sformula,
                                    conn);
                        } else {
                            statvalues = simplestat.getLexprData(userbase,Integer.parseInt(statId), querycond,
                                    userView.getUserName(), userView.getManagePrivCode(), userView,
                                    infokind, isresult, commlexr, commfacor,
                                    preresult, history);
                        }
					} else {
						if (sformula.length() > 0) {
                            statvaluess = simplestat.getLexprDataSformula(
                                    userbase.toUpperCase(), Integer.parseInt(statId), querycond,
                                    userView.getUserName(), userView.getManagePrivCode(), userView,
                                    infokind, isresult, commlexr, commfacor,
                                    preresult, history, userbases, sformula,
                                    conn);
                        } else {
                            statvalues = simplestat.getLexprData(userbase.toUpperCase(), Integer.parseInt(statId),
                                    querycond, userView.getUserName(), userView.getManagePrivCode(), userView,
                                    infokind, isresult, commlexr, commfacor,
                                    preresult, history, userbases);
                        }
					}
					SNameDisplay = simplestat.getSNameDisplay();
					if ((sformula.length() == 0 && statvalues != null && statvalues.length > 0)|| (sformula.length() > 0 && statvaluess != null && statvaluess.length > 0)) {
						fieldDisplay = simplestat.getDisplay();
						int statTotal = 0;
						double statTotals = 0.0;
						if (sformula.length() == 0) {
							for (int i = 0; i < statvalues.length; i++) {
								CommonData vo = new CommonData();
								String str = fieldDisplay[i];
								vo.setDataName(str);
								if (sformula.length() == 0) {
                                    vo.setDataValue(String.valueOf(statvalues[i]));
                                } else {
                                    vo.setDataValue(String.valueOf(statvaluess[i]));
                                }
								list.add(vo);
								if (sformula.length() == 0) {
                                    statTotal += statvalues[i];
                                } else {
                                    statTotals += statvaluess[i];
                                }
							}
						} else {
							for (int i = 0; i < statvaluess.length; i++) {
								CommonData vo = new CommonData();
								String str = fieldDisplay[i];
								vo.setDataName(str);
								if (sformula.length() == 0) {
                                    vo.setDataValue(String.valueOf(statvalues[i]));
                                } else {
                                    vo.setDataValue(String.valueOf(statvaluess[i]));
                                }
								list.add(vo);
								if (sformula.length() == 0) {
                                    statTotal += statvalues[i];
                                } else {
                                    statTotals += statvaluess[i];
                                }
							}
						}
						snamedisplayvo.setDataName("snamedisplay" + k);
						snamedisplayvo.setDataValue(SNameDisplay);
						listmap.put("list" + k, list);
					} else {
						StringBuffer sql = new StringBuffer();
						sql.append("select * from SName where id=");
						sql.append(statId);
						List rs = ExecuteSQL.executeMyQuery(sql.toString());
						if (!rs.isEmpty()) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							SNameDisplay = rec.get("name") != null ? rec.get("name").toString() : "";
						}
						CommonData vo = new CommonData();
						vo.setDataName("");
						vo.setDataValue("0");
						list.add(vo);
						snamedisplayvo.setDataName("snamedisplay" + k);
						snamedisplayvo.setDataValue(SNameDisplay);
						listmap.put("list" + k, list);
					}
					HashMap jfreemap = new HashMap();
					jfreemap.put(SNameDisplay, list);
					jfreemapmap.put("jfreemap"+k, jfreemap);
					String archive_set = "";
					StringBuffer sql = new StringBuffer();
					sql.append("select archive_set from SName where id=");
					sql.append(statId);
					List rs = ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec = (LazyDynaBean) rs.get(0);
						archive_set = rec.get("archive_set") != null ? rec.get("archive_set").toString() : "";
					}
					archive_setvo.setDataName("archive_set" + k);
					archive_setvo.setDataValue(archive_set);
					sformulavo.setDataName("sformula" + k);
					sformulavo.setDataValue(sformula);
					//this.getFormHM().put("queryconde", querycond);
					String xangle = AnychartBo.computeXangle(list);
					xanglevo.setDataName("xangle" + k);
					xanglevo.setDataValue(xangle);
					label_enabledvo.setDataName("label_enabled" + k);
					label_enabledvo.setDataValue(list.size() < 15 ? "true" : "false");
					statIdslist.add(statIdvo);
					decimalwidthlist.add(decimalwidthvo);
					isneedsumlist.add(isneedsumvo);
					snamedisplaylist.add(snamedisplayvo);
					listlist.add(listmap);
					jfreemaplist.add(jfreemapmap);
					label_enabledlist.add(label_enabledvo);
					xanglelist.add(xanglevo);
					sformulalist.add(sformulavo);
					archive_setvolist.add(archive_setvo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTwoCrossTable(String vtotal,String htotal,String vnull,String hnull,
			String[] lengthlist,String[] crosslist,String preresult,String history,String sformula){
		
		StatDataEncapsulation simplestat1=new StatDataEncapsulation();
		simplestat1.setConn(conn);
		try {
			String[] lengthwayslist = new String[lengthlist.length];
			String[] crosswiselist = new String[crosslist.length];
			for(int i = 0;i < lengthlist.length;i++){
				lengthwayslist[lengthlist.length-1-i] = lengthlist[i];
			}
			for(int i = 0;i < crosslist.length;i++){
				crosswiselist[crosslist.length-1-i] = crosslist[i];
			}
			if(userbases==null||userbases.length()==0){
				if(sformula.length()>0){
					statValues=simplestat1.getDoubleLexprData(lengthwayslist,crosswiselist,userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,true,commlexr,commfacor,preresult,history,userbase.toLowerCase(),vtotal,htotal,vnull,hnull);
				}else{
					statValues=simplestat1.getDoubleLexprData(lengthwayslist,crosswiselist,userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,true,commlexr,commfacor,preresult,history,userbase.toLowerCase(),vtotal,htotal,vnull,hnull);
				}
			}else{
				if(sformula.length()>0){
					statValues=simplestat1.getDoubleLexprData(lengthwayslist,crosswiselist,userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,true,commlexr,commfacor,preresult,history,userbases,vtotal,htotal,vnull,hnull);
				}else{
					//this.getFormHM().put("userbases",userbases);
					statValues=simplestat1.getDoubleLexprData(lengthwayslist,crosswiselist,userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,true,commlexr,commfacor,preresult,history,userbases,vtotal,htotal,vnull,hnull);
				}
			}
			List varrayfirstlist=simplestat1.getVerticalFirstArray();
			List varraysecondlist=simplestat1.getVerticalSecondArray();
			List harrayfirstlist=simplestat1.getHorizonFirstArray();
			List harraysecondlist=simplestat1.getHorizonSecondArray();
			double totalvalues = simplestat1.getTotalValues();
			setTotalvalues(totalvalues);
			/*
			this.getFormHM().put("statdoublevalues",statValues);
			this.getFormHM().put("statdoublevaluess",statValuess);
			this.getFormHM().put("varrayfirstlist",varrayfirstlist);
			this.getFormHM().put("varraysecondlist",varraysecondlist);
			this.getFormHM().put("harrayfirstlist",harrayfirstlist);
			this.getFormHM().put("harraysecondlist",harraysecondlist);
			 */
			setStatValues(statValues);
			setStatValuess(statValuess);
			setVarrayfirstlist((ArrayList) varrayfirstlist);
			setVarraysecondlist((ArrayList) varraysecondlist);
			setHarrayfirstlist((ArrayList) harrayfirstlist);
			setHarraysecondlist((ArrayList) harraysecondlist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList getDimension(ContentDAO dao, String str) {
		
		RowSet rs = null;
		ArrayList allList = new ArrayList();
		str = str.replaceAll("1_", "");
		String[] strList = str.split(",");
		try {
			for (int i = strList.length-1;i>=0; i--) {
				if(strList[i].toString().indexOf("2_")!=-1){
					String id = strList[i].toString().replace("2_", "");
					String sql = "select Name,Id from sname where Id="+id;
					rs=dao.search(sql);
					if(rs.next()){
						CommonData cdata=new CommonData("2_"+rs.getString("id"),("　　"+rs.getString("Name")));
						allList.add(cdata);
					}
				}else{
					String sql = "select Name,Id from sname where Id="+strList[i];
					rs=dao.search(sql);
					if(rs.next()){
						CommonData cdata=new CommonData("1_"+rs.getString("id"),(rs.getString("Name")));
						allList.add(cdata);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return allList;
	}
	
	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getInfokind() {
		return infokind;
	}

	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}

	public String getUserbases() {
		return userbases;
	}

	public void setUserbases(String userbases) {
		this.userbases = userbases;
	}

	public String getUserbase() {
		return userbase;
	}

	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}

	public String getQuerycond() {
		return querycond;
	}

	public void setQuerycond(String querycond) {
		this.querycond = querycond;
	}

	public ArrayList getStatIdslist() {
		return statIdslist;
	}

	public void setStatIdslist(ArrayList statIdslist) {
		this.statIdslist = statIdslist;
	}

	public ArrayList getDecimalwidthlist() {
		return decimalwidthlist;
	}

	public void setDecimalwidthlist(ArrayList decimalwidthlist) {
		this.decimalwidthlist = decimalwidthlist;
	}

	public ArrayList getIsneedsumlist() {
		return isneedsumlist;
	}

	public void setIsneedsumlist(ArrayList isneedsumlist) {
		this.isneedsumlist = isneedsumlist;
	}

	public ArrayList getSnamedisplaylist() {
		return snamedisplaylist;
	}

	public void setSnamedisplaylist(ArrayList snamedisplaylist) {
		this.snamedisplaylist = snamedisplaylist;
	}

	public ArrayList getListlist() {
		return listlist;
	}

	public void setListlist(ArrayList listlist) {
		this.listlist = listlist;
	}

	public ArrayList getJfreemaplist() {
		return jfreemaplist;
	}

	public void setJfreemaplist(ArrayList jfreemaplist) {
		this.jfreemaplist = jfreemaplist;
	}

	public ArrayList getLabel_enabledlist() {
		return label_enabledlist;
	}

	public void setLabel_enabledlist(ArrayList label_enabledlist) {
		this.label_enabledlist = label_enabledlist;
	}

	public ArrayList getXanglelist() {
		return xanglelist;
	}

	public void setXanglelist(ArrayList xanglelist) {
		this.xanglelist = xanglelist;
	}

	public ArrayList getSformulalist() {
		return sformulalist;
	}

	public void setSformulalist(ArrayList sformulalist) {
		this.sformulalist = sformulalist;
	}

	public ArrayList getArchive_setvolist() {
		return archive_setvolist;
	}

	public void setArchive_setvolist(ArrayList archive_setvolist) {
		this.archive_setvolist = archive_setvolist;
	}

	public double[][] getStatValuess() {
		return statValuess;
	}

	public void setStatValuess(double[][] statValuess) {
		this.statValuess = statValuess;
	}

	public int[][] getStatValues() {
		return statValues;
	}

	public void setStatValues(int[][] statValues) {
		this.statValues = statValues;
	}
	
	public double getTotalvalues() {
		return totalvalues;
	}

	public void setTotalvalues(double totalvalues) {
		this.totalvalues = totalvalues;
	}

	public ArrayList getVarrayfirstlist() {
		return varrayfirstlist;
	}

	public void setVarrayfirstlist(ArrayList varrayfirstlist) {
		this.varrayfirstlist = varrayfirstlist;
	}

	public ArrayList getVarraysecondlist() {
		return varraysecondlist;
	}

	public void setVarraysecondlist(ArrayList varraysecondlist) {
		this.varraysecondlist = varraysecondlist;
	}

	public ArrayList getHarrayfirstlist() {
		return harrayfirstlist;
	}

	public void setHarrayfirstlist(ArrayList harrayfirstlist) {
		this.harrayfirstlist = harrayfirstlist;
	}

	public ArrayList getHarraysecondlist() {
		return harraysecondlist;
	}

	public void setHarraysecondlist(ArrayList harraysecondlist) {
		this.harraysecondlist = harraysecondlist;
	}

	public String getCommlexr() {
		return commlexr;
	}

	public void setCommlexr(String commlexr) {
		this.commlexr = commlexr;
	}

	public String getCommfacor() {
		return commfacor;
	}

	public void setCommfacor(String commfacor) {
		this.commfacor = commfacor;
	}
}
