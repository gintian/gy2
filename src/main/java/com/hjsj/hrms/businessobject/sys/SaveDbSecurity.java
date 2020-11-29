package com.hjsj.hrms.businessobject.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveDbSecurity extends IBusiness{
	
	@Override
    public void execute() throws GeneralException {
		String rsydid = (String)this.getFormHM().get("rsydid");//人事异动id
		String gjhmcid = (String)this.getFormHM().get("gjhmcid");//高级花名册id
	    String salaryid = (String)this.getFormHM().get("salaryid"); //薪资id
	    String cyhmcid = (String)this.getFormHM().get("cyhmcid");//常用花名册id
	    String reportid = (String)this.getFormHM().get("reportid");//报表id
	    String tablename = (String)this.getFormHM().get("tablename");//表名
	    if(tablename!=null &&!"".equals(tablename)){
	    	 String tablenamenew = "";
			if(tablename.contains(",")){
				String [] tablearr = tablename.split(",");
				if(tablearr.length>0){
					for(int i=0;i<tablearr.length;i++){
						String tablenames = tablearr[i];
						if(tablenames!=null && !"".equals(tablenames)){
							String regEx="[`~!@%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
							Pattern p = Pattern.compile(regEx);     
					        Matcher m = p.matcher(tablenames);
					        String tablenames1=m.replaceAll("").trim();
					        if(tablenames1.contains("\"")){
					        	  tablenames1 = tablenames1.replaceAll("\"","");
					          }
					        tablenamenew+=tablenames1+",";
						}	
					}
					tablename=tablenamenew.substring(0, tablenamenew.length()-1);
				}
			}else{
				String regEx="[`~!@%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
				Pattern p = Pattern.compile(regEx);     
		        Matcher m = p.matcher(tablename);
		        String tablenames1=m.replaceAll("").trim();
		        if(tablenames1.contains("\"")){
		        	tablename = tablenames1.replaceAll("\"","");
		          }else{
		        	  tablename= tablenames1;
		          }
			}
	    }
	    
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"DB_SECURITY","root");
		
	    String param_name = "DB_SECURITY";
	    //检索有没有这个constant，如果没有直接插入，有的话不操作
	    constantXml.ifNoParameterInsert(param_name);
	    //新增节点
	    List<Map<String,String>> listmap = new ArrayList<Map<String,String>>();
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("rsydid", rsydid);
	    map.put("gjhmcid", gjhmcid);
	    map.put("salaryid", salaryid);
	    map.put("cyhmcid", cyhmcid);
	    map.put("reportid", reportid);
	    map.put("tablename", tablename);
	    listmap.add(map);
	    ArrayList nodeList = new ArrayList();
	    for (Map<String, String> m : listmap) {
	        for (String k : m.keySet()) {
	            LazyDynaBean nodeBean=new LazyDynaBean();
				nodeBean.set("name",k);
				nodeBean.set("content",m.get(k));
				nodeList.add(nodeBean);
	        }
	    }
	    constantXml.addElement3("/root", nodeList);
	    constantXml.saveStrValue();

	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    ArrayList sqlvalue = new ArrayList();
	    String sql = "";
	    try {
			Set rsydtableset = new HashSet();
				if(rsydid!=null && !"".equals(rsydid)){//人事异动加密
					//按规则生成的人事异动的表  用户名templet_xxxx 、templet_xxxx、g_templet_xxxx
					List rsydlist = new ArrayList();
					sql = "select table_name from user_tables where lower(table_name) like '%templet$_%' ESCAPE '$' or lower(table_name) LIKE 'templet$_%' ESCAPE '$' or lower(table_name) LIKE 'g$_templet$_%' ESCAPE '$'";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						String rsydtable = this.frowset.getString("TABLE_NAME").toLowerCase();
						rsydlist.add(rsydtable);
						}
					rsydtableset.add("template_archive");
					if(rsydid.contains(",")){
						String[] rsydarray=rsydid.split(",");
						if(rsydarray.length>0){
							for(int i=0;i<rsydarray.length;i++){
								String rsydids = rsydarray[i];
								if(rsydids!=null && !"".equals(rsydids)){
									String rsydtablename = "templet_"+rsydids;
									String rsydtablename1 = "templet_"+rsydids;
									String rsydtablename2 = "g_templet_"+rsydids;
									if(rsydlist.contains(rsydtablename1)){
										rsydtableset.add(rsydtablename1);
									}
									if(rsydlist.contains(rsydtablename2)){
										rsydtableset.add(rsydtablename2);
									}
									for(int k=0;k<rsydlist.size();k++){
										String rsyds = rsydlist.get(k).toString();
										if(rsyds.lastIndexOf("_")==rsyds.indexOf("_")){
											if(rsydlist.get(k).toString().endsWith(rsydtablename)){
												rsydtableset.add(rsydlist.get(k));
											}
										}
									}
								}
							}
						}
					}
					else{
						rsydid = rsydid;
						String rsydtablename = "templet_"+rsydid;
						String rsydtablename1 = "templet_"+rsydid;
						String rsydtablename2 = "g_templet_"+rsydid;
					if(rsydlist.contains(rsydtablename1)){
						rsydtableset.add(rsydtablename1);
					}
					if(rsydlist.contains(rsydtablename2)){
						rsydtableset.add(rsydtablename2);
					}
					for(int k=0;k<rsydlist.size();k++){
						String rsyds = rsydlist.get(k).toString();
						if(rsyds.lastIndexOf("_")==rsyds.indexOf("_")){
							if(rsydlist.get(k).toString().endsWith(rsydtablename)){
								rsydtableset.add(rsydlist.get(k));
							}
						}
					}
					}

				} if(gjhmcid!=null && !"".equals(gjhmcid)){//高级花名册加密
					//用户名_Muster_tabid
					List gjhmclist = new ArrayList();
					sql = "select table_name from user_tables where lower(table_name) like '%$_muster$_%' ESCAPE '$'";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						String gjhmctable = this.frowset.getString("TABLE_NAME").toLowerCase();
						gjhmclist.add(gjhmctable);
						}
					if(gjhmcid.contains(",")){
						String [] gjhmcarr =  gjhmcid.split(",");
						if(gjhmcarr.length>0){
							for(int i=0;i<gjhmcarr.length;i++){
								String gjhmcids = gjhmcarr[i];
								if(gjhmcids!=null && !"".equals(gjhmcids)){
									String gihmctablename = "_muster_"+gjhmcids;
									for(int j=0;j<gjhmclist.size();j++){
										String gjhmcs = gjhmclist.get(j).toString();
										if(gjhmcs.lastIndexOf("_")==gjhmcs.indexOf("_")+7){
											if(gjhmclist.get(j).toString().endsWith(gihmctablename)){
												rsydtableset.add(gjhmclist.get(j));
											}
										}
									}
								}
							}
						}
					}
					else{
						gjhmcid = gjhmcid;
						String gihmctablename = "_muster_"+gjhmcid;
						for(int j=0;j<gjhmclist.size();j++){
							String gjhmcs = gjhmclist.get(j).toString();
							if(gjhmcs.lastIndexOf("_")==gjhmcs.indexOf("_")+7){
								if(gjhmclist.get(j).toString().endsWith(gihmctablename)){
									rsydtableset.add(gjhmclist.get(j));
								}
							}
						}
					}
				} if(salaryid!=null &&!"".equals(salaryid)){
					//用户名_salary_salaryid 、t#用户名_gz_Ins、t#用户名_gz_Dec、t#用户名_gz_Bd、t#用户名_gz
					List salarylist = new ArrayList();
					sql = "select table_name from user_tables where lower(table_name) like '%$_salary$_%' ESCAPE '$' or lower(table_name) like  't#%$_gz$_ins' ESCAPE '$' or lower(table_name) like  't#%$_gz$_dec' ESCAPE '$' or lower(table_name) like  't#%$_gz$_bd' ESCAPE '$' or lower(table_name) like  't#%$_gz' ESCAPE '$'";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						String salarytable = this.frowset.getString("TABLE_NAME").toLowerCase();
						salarylist.add(salarytable);
						}
					rsydtableset.add("salaryhistory");//salaryhistory、salaryarchive 、gz_tax_mx、taxarchive
					rsydtableset.add("salaryarchive");
					rsydtableset.add("gz_tax_mx");
					rsydtableset.add("taxarchive");
					if(salaryid.contains(",")){
						String[] salaryarr = salaryid.split(",");
						if(salaryarr.length>0){
							for(int i=0;i<salaryarr.length;i++){
								String salaryids = salaryarr[i];
								if(salaryids!=null && !"".equals(salaryids)){
									String salarytablename = "_salary_"+salaryids;
									for(int j=0;j<salarylist.size();j++){
										String salarys = salarylist.get(j).toString();
										if(salarys.lastIndexOf("_")==salarys.indexOf("_")+7) {
											if(salarylist.get(j).toString().endsWith(salarytablename)){
												rsydtableset.add(salarylist.get(j));
											}
										}
									}
								}
							}
						}
					}
					if(!salaryid.contains(",")){
						salaryid = salaryid;
						String salarytablename = "_salary_"+salaryid;
						for(int j=0;j<salarylist.size();j++){
							String salarys = salarylist.get(j).toString();
							if(salarys.lastIndexOf("_")==salarys.indexOf("_")+7) {
								if(salarylist.get(j).toString().endsWith(salarytablename)){
									rsydtableset.add(salarylist.get(j));
								}
							}
						}
					}
					for(int j=0;j<salarylist.size();j++){
						if(salarylist.get(j).toString().startsWith("t#") && salarylist.get(j).toString().endsWith("_gz_ins")){
							rsydtableset.add(salarylist.get(j));
						}
						if(salarylist.get(j).toString().startsWith("t#") && salarylist.get(j).toString().endsWith("_gz_dec")){
							rsydtableset.add(salarylist.get(j));
						}
						if(salarylist.get(j).toString().startsWith("t#") && salarylist.get(j).toString().endsWith("_gz_bd")){
							rsydtableset.add(salarylist.get(j));
						}
						if(salarylist.get(j).toString().startsWith("t#") && salarylist.get(j).toString().endsWith("_gz")){
							rsydtableset.add(salarylist.get(j));
						}
					}
					
				} if(cyhmcid!=null &&!"".equals(cyhmcid)){//常用花名册
					//人员花名册:m花名册编号_用户名_人员库前缀(Usr,Oth…),单位花名册:m花名册编号_B,职位花名册:m花名册编号_K
					List cyhmclist = new ArrayList();
					sql = "select table_name from user_tables where lower(table_name) like 'm%$_%$_%' ESCAPE '$' or  lower(table_name) like 'm%$_b' ESCAPE '$' or   lower(table_name) like 'm%$_k' ESCAPE '$' ";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						String cyhmctable = this.frowset.getString("TABLE_NAME").toLowerCase();
						cyhmclist.add(cyhmctable);
						}
					List prelist = new ArrayList();
					sql = "select Pre from dbName";
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						String pre = this.frowset.getString("Pre");
						prelist.add(pre);
					}
					if(cyhmcid.contains(",")){
						String [] cyhmcarr = cyhmcid.split(",");
						if(cyhmcarr.length>0){
							for(int i=0;i<cyhmcarr.length;i++){
								String cyhmcids = cyhmcarr[i];
								if(cyhmcids!=null && !"".equals(cyhmcids)){
									String cyhmctablename = "m"+cyhmcids+"_b";
									String cyhmctablename1 = "m"+cyhmcids+"_k";
									if(cyhmclist.contains(cyhmctablename)){
										rsydtableset.add(cyhmctablename);
									}
									if(cyhmclist.contains(cyhmctablename1)){
										rsydtableset.add(cyhmctablename1);
									}
									for(int j=0;j<cyhmclist.size();j++){
										for(int k=0;k<prelist.size();k++){
											if(cyhmclist.get(j).toString().startsWith("m"+cyhmcids+"_") && cyhmclist.get(j).toString().endsWith(prelist.get(k).toString().toLowerCase())){
												rsydtableset.add(cyhmclist.get(j));
											}
										}
									}
								}
							}
						}
					}
					else{
						cyhmcid = cyhmcid;
						String cyhmctablename = "m"+cyhmcid+"_b";
						String cyhmctablename1 = "m"+cyhmcid+"_b";
						if(cyhmclist.contains(cyhmctablename)){
							rsydtableset.add(cyhmctablename);
						}
						if(cyhmclist.contains(cyhmctablename1)){
							rsydtableset.add(cyhmctablename1);
						}
						for(int j=0;j<cyhmclist.size();j++){
							for(int k=0;k<prelist.size();k++){
								if(cyhmclist.get(j).toString().startsWith("m"+cyhmcid+"_") && cyhmclist.get(j).toString().endsWith(prelist.get(k).toString().toLowerCase())){
									rsydtableset.add(cyhmclist.get(j));
								}
							}
						}
					}
					
				} if(reportid!=null &&!"".equals(reportid)){//报表
					//tb表号  编辑报表统计结果表 tt_表号  BS报表汇总统计结果表 ta_表号  BS报表归档表
					List reportlist = new ArrayList();
					sql = "select table_name from user_tables where lower(table_name) like 'tb%' ESCAPE '$' or lower(table_name) like 'tt$_' ESCAPE '$' or lower(table_name) like 'ta$_' ESCAPE '$'";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						String reporttable = this.frowset.getString("TABLE_NAME").toLowerCase();
						reportlist.add(reporttable);
						}
					if(reportid.contains(",")){
						String[] reportarr = reportid.split(",");
						if(reportarr.length>0){
							for(int i=0;i<reportarr.length;i++){
								String reportids = reportarr[i];
								if(reportids!=null && !"".equals(reportids)){
									String reporttablename = "tb"+reportids;
									String reporttablename1 = "tt_"+reportids;
									String reporttablename2 = "ta_"+reportids;
									if(reportlist.contains(reporttablename)){
										rsydtableset.add(reporttablename);
									}
									if(reportlist.contains(reporttablename)){
										rsydtableset.add(reporttablename1);
									}
									if(reportlist.contains(reporttablename)){
										rsydtableset.add(reporttablename2);
									}
								}
							}
						}
					}
					else{
						reportid = reportid;
						String reporttablename = "tb_"+reportid;
						String reporttablename1 = "tt_"+reportid;
						String reporttablename2 = "ta_"+reportid;
						if(reportlist.contains(reporttablename)){
							rsydtableset.add(reporttablename);
						}
						if(reportlist.contains(reporttablename)){
							rsydtableset.add(reporttablename1);
						}
						if(reportlist.contains(reporttablename)){
							rsydtableset.add(reporttablename2);
						}
					}
					
				} if(tablename!=null &&!"".equals(tablename)){
					if(tablename.contains(",")){
						String [] tablearr = tablename.split(",");
						if(tablearr.length>0){
							for(int i=0;i<tablearr.length;i++){
								String tablenames = tablearr[i];
								if(tablenames!=null && !"".equals(tablenames)){
									//String patternname ="^[a-zA-Z\u4e00-\u9fa5][A-Za-z0-9$#_\u4e00-\u9fa5]{0,29}$";
									String regEx="[`~!@%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
									Pattern p = Pattern.compile(regEx);     
							        Matcher m = p.matcher(tablenames);
							        String tablenames1=m.replaceAll("").trim();
							        if(tablenames1.contains("\"")){
							        	  tablenames1 = tablenames1.replaceAll("\"","");
							          }
										rsydtableset.add(tablenames1);
								}	
							}
						}
					}
					else{
						    tablename = tablename;
							//String patternname ="^[a-zA-Z\u4e00-\u9fa5][A-Za-z0-9$#_\u4e00-\u9fa5]{0,29}$";
							String regEx="[`~!@%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
							Pattern p = Pattern.compile(regEx);     
					        Matcher m = p.matcher(tablename);
					        String tablenames1=m.replaceAll("").trim();
					        if(tablenames1.contains("\"")){
					        	  tablenames1 = tablenames1.replaceAll("\"","");
					          }
								rsydtableset.add(tablenames1);
						}
					
				}else{
					//return;
				}
				Iterator it=rsydtableset.iterator();
			    while(it.hasNext())
			       {
			           DbSecurityImpl dbs = new DbSecurityImpl();
					   dbs.encryptTableName2(this.getFrameconn(), (String)it.next());
			       }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
