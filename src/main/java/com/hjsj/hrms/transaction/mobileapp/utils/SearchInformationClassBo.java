package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.mobileapp.kq.checkin.CheckInMainBo;
import com.hjsj.hrms.transaction.mobileapp.statis.StatisAnalysisBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchInformationClassBo {

	private Connection conn;
    private UserView userView;
    
    //人员库
    private String mpre;
	//是否模糊
    private String mlike="false";
    //是否历史
    private String mhistory="false";
    //是否二次
    private String msecond="true";
    
    //快速查询
    private String queryValue;;
    private String selectField;
    //常用查询
    private String lexprid="0";
    //第几页
    private String pageIndex="1";
    //每页条数
    private String pageSize="10";
    //服务器url
	private String url;
	//1人员,=2单位,=3职位,9考勤
	private String infotype="1";
	//0权限查询 ,1常用查询 ,2快速查询
	private String querytype="1";
	// 用户查询的关键字
	private String keywords="";
	//被点击人员的人员编号
	private String prea0100;
	//快速查询指标数组
	private ArrayList fieldlist = new ArrayList();
	
	/**保存结果集的sql语句，用于二次查询*/
	private String queryResultSQLWhere;
	
	/**-----------------统计分析--------------*/	
	/**点击的ID*/
	private String statisSLegendID;
	/**维度显示，一维、二维*/
	private String statisDim;	
	/**所选的组织机构 0101,101,0101*/
	private String statisOgr;
	/**人员1，单位2，岗位3*/
	private String statisInfokind = "";
	
	public void setStatisInfokind(String statisInfokind) {
		this.statisInfokind = statisInfokind;
	}
	public void setStatisSLegendID(String statisSLegendID) {
		this.statisSLegendID = statisSLegendID;
	}
	public void setStatisDim(String statisDim) {
		this.statisDim = statisDim;
	}
	public void setStatisOgr(String statisOgr) {
		this.statisOgr = statisOgr;
	}	
		
	
	public String getPrea0100() {
		return prea0100;
	}
	public void setPrea0100(String prea0100) {
		this.prea0100 = prea0100;
	}
    
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getQuerytype() {
		return querytype;
	}
	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}
	public SearchInformationClassBo(){
        
    }
    public SearchInformationClassBo(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }
    
    public String getMpre() {
		return mpre;
	}
	public void setMpre(String mpre) {
		this.mpre = mpre;
	}
	public String getMlike() {
		return mlike;
	}
	public void setMlike(String mlike) {
		this.mlike = mlike;
	}
	public String getMhistory() {
		return mhistory;
	}
	public void setMhistory(String mhistory) {
		this.mhistory = mhistory;
	}
	public String getMsecond() {
		return msecond;
	}
	public void setMsecond(String msecond) {
		this.msecond = msecond;
	}
	
	public String getQueryValue() {
		return queryValue;
	}
	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
	}
	public String getSelectField() {
		return selectField;
	}
	public void setSelectField(String selectField) {
		this.selectField = selectField;
	}
	public String getLexprid() {
		return lexprid;
	}
	public void setLexprid(String lexprid) {
		this.lexprid = lexprid;
	}
	public String getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getInfotype() {
		return infotype;
	}
	public void setInfotype(String infotype) {
		this.infotype = infotype;
	}
	
    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String keywords) {
        keywords=PubFunc.getReplaceStr(keywords);
        this.keywords = keywords;
    }
    /**
	 * 获取人员、单位、岗位记录
	 * @return
     * @throws GeneralException 
	 */
	public List searchInfoList() throws GeneralException{
		
		List personList = null;
        String sql= getSQL();
        personList = getPersonList(sql);
        // 只有快速查询和常用查询涉及二次查询，历史查询
		if("1".equals(pageIndex) && (("1".equals(this.querytype) || "2".equals(this.querytype)))){
			this.saveQueryResultBase();
		}
        return personList ; 
	}
	
	/**
	 * 获取人员list
	 * @param sql
	 * @param url
	 * @return
	 * @throws GeneralException 
	 * @throws Exception
	 */
    public List getPersonList(String sql) throws GeneralException{
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        PhotoImgBo pib = new PhotoImgBo(this.conn);
		pib.setIdPhoto(true);
        RowSet rs = null;
        HashMap map = null;
        try {
        	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
        	String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        	display_e0122 = display_e0122==null||display_e0122.length()==0?"0":display_e0122;
        	String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
   		 	seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
        	rs = dao.search(sql.toString());
            while (rs.next()) {
                map = new HashMap();
                String a0100 = rs.getString("a0100");
                String dbpre = rs.getString("dbpre");
                map.put("dbpre", dbpre);
                map.put("a0100", a0100);
                map.put("name", rs.getString("a0101"));
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String e01a1 = rs.getString("e01a1");
                b0110 = AdminCode.getCodeName("UN", b0110);
                b0110 = b0110==null?"":b0110;
                CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
                if(itemid!=null)
                	e0122 = itemid.getCodename();
                e0122 = e0122==null?"":e0122;
                e01a1 = AdminCode.getCodeName("@K", e01a1);
                map.put("posname", e01a1);
                e01a1 = e01a1==null?"":e01a1;
                map.put("unitdeptname", b0110+(b0110.length()>0&&e0122.length()>0?seprartor:"")+e0122);
                map.put("org", b0110+(b0110.length()>0&&e0122.length()>0?seprartor:"")+e0122+(e01a1.length()>0&&e0122.length()>0?seprartor:"")+e01a1);
                String info = this.getInfo(dbpre, a0100);
                info = info.replaceAll("\r", "").replaceAll("\n", "").replace("\r\n", "").trim();
                map.put("info", info);
                StringBuffer photourl=new StringBuffer();
				String filename= pib.getPhotoPath(dbpre,a0100);
				if(!"".equals(filename)){
					photourl.append(url);
					photourl.append(filename);
                } else {
                	photourl.append(url);
                    photourl.append("/images/photo.jpg");
                }
                map.put("photo", photourl.toString());
                list.add(map);
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);  
		} finally {
			PubFunc.closeResource(rs);
        }
        return list;
    }
	
	/**
	 * @throws GeneralException 
     * 
     * @Title: getSQL   
     * @Description: 查询语句
     * @param @return 
     * @return String    
     * @throws
     */
    private String getSQL() throws GeneralException {
    	StringBuffer sqlstr = new StringBuffer();
    	try{
    		int index, size;
            index = Integer.parseInt(pageIndex);
            size = Integer.parseInt(pageSize);
			StringBuffer orderBy = new StringBuffer(" ORDER BY A0000,dbpre");
			String strwhere="";
			 if("9".equals(infotype)){//考勤权限管理人员
			     String tempsql = "";
	                String sql = "";
	                strwhere = getKqWhereSQL();
	                strwhere+=getKeywordsWhereStr();
	                CheckInMainBo cimBo = new CheckInMainBo(userView,conn);
	                ArrayList kqNbaseList = cimBo.getKqNbaseList();
	                tempsql+= "select Usra01.a0100,'Usr' dbpre,Usra01.b0110,Usra01.e01a1,Usra01.e0122,Usra01.a0101,Usra01.a0000 "+strwhere+" "; 
	                if(kqNbaseList.size()==0){
	                    sql = tempsql+" and 1 = 2";
	                }else{
	                    for (int i = 0; i < kqNbaseList.size(); i++) {
	                        String nbase = (String)kqNbaseList.get(i); 
	                        if(i!=0){
	                            sql+=" union ";
	                        }
	                        sql+= tempsql.replace("Usr", nbase);
	                    }
	                }
	                sqlstr.append("select * from ( select ROW_NUMBER() over("+orderBy+" ) numberCode "); 
	                sqlstr.append(",A.* from ( "+sql+" ) A) T where numberCode between "+((index-1)*size+1)+" and "+(size*index));
    		}else if("1".equals(this.infotype)){
    			if("leader".equals(querytype)||"staff".equals(querytype)||"staffadd".equals(querytype)||"staffminus".equals(querytype)){   				
    				strwhere = getEmpListSql();
    			}else if("1".equals(this.querytype)){
					strwhere = commonQuerySql();
					if(keywords.length()>0)
				    	strwhere += getKeywordsWhereStr();
					queryResultSQLWhere = strwhere;
				}else if("2".equals(this.querytype)){
					//strwhere = generalQuerySql();
					strwhere=this.combine_SQL(fieldlist, "true".equals(this.mlike)?"1":"0", "###", this.infotype,"false".equals(this.msecond)?"1":"0");
					if(keywords.length()>0)
				    	strwhere+=getKeywordsWhereStr();
					queryResultSQLWhere = strwhere;
				}else if("statis".equals(this.querytype)){//统计分析反查
					strwhere = this.getStatisChartListSQL();
					if(keywords.length()>0)
				    	strwhere+=getKeywordsWhereStr();
				}else{
				    strwhere=userView.getPrivSQLExpression("###",Boolean.valueOf(this.mhistory).booleanValue(),Boolean.valueOf(this.msecond).booleanValue()); 
				    if(keywords.length()>0)
				    	strwhere+=getKeywordsWhereStr();//用于关键字查询 keywords默认为=''
                }
    			
    			if(!("statis".equals(querytype)||"leader".equals(querytype)||"staffminus".equals(querytype)||"staffadd".equals(querytype))){
    				Map dbprea0100Map =null;
    				if("staff".equals(querytype))
    					dbprea0100Map = getLeaderPreA0100s(this.prea0100.substring(0,3), this.prea0100.substring(3));
    				
    				if(this.mpre.indexOf("`")!=-1){
	            		String[] dbpres = mpre.split("`");
	            		StringBuffer sqlsb = new StringBuffer();
	            		for(int i=0;i<dbpres.length;i++){
	            			String dbpre = dbpres[i];
	            			if(dbpre.length()==3&&userView.hasTheDbName(dbpre)){
	            				sqlsb.append("select distinct "+dbpre+"a01.a0100,'"+(i+1)+"' ord,'"+dbpre+"' dbpre,"+dbpre+"a01.b0110,"+dbpre+"a01.e01a1,"+dbpre+"a01.e0122,a0101,a0000 "+strwhere.replaceAll("###", dbpre));
	            				if("staff".equals(querytype)){
	            					if(dbpre.equalsIgnoreCase(this.prea0100.substring(0,3)))
	            						sqlsb.append(" and "+dbpre+"a01.a0100<>'"+this.prea0100.substring(3)+"'");
	            					if(dbprea0100Map.containsKey(dbpre)){
	    								List a0100s =(List) dbprea0100Map.get(dbpre);
	    								sqlsb.append(" and "+dbpre+"a01.a0100 not in('"+a0100s.toString().substring(1, a0100s.toString().length()-1).replaceAll(", ", "','")+"')");
	    							}
	            				}
	            				sqlsb.append(" union ");
	            			}
	            		}
	            		strwhere = sqlsb.substring(0,sqlsb.length()-7);
	            	}else{
	            		strwhere ="select distinct "+mpre+"a01.a0100,'"+1+"' ord,'" +mpre+"' dbpre,"+mpre+"a01.b0110,"+mpre+"a01.e01a1,"+mpre+"a01.e0122,a0101,a0000 "+strwhere.replaceAll("###", mpre);
	            		if("staff".equals(querytype)){
	            			if(mpre.equalsIgnoreCase(this.prea0100.substring(0,3)))
	            				strwhere+=" and "+mpre+"a01.a0100<>'"+this.prea0100.substring(3)+"'";
	            			if(dbprea0100Map.containsKey(mpre)){
								List a0100s =(List) dbprea0100Map.get(mpre);
								strwhere+=" and "+mpre+"a01.a0100 not in('"+a0100s.toString().substring(1, a0100s.toString().length()-1).replaceAll(", ", "','")+"')";
							}
        				}
	            	}
    			}   		
				sqlstr.append("select * from ( select  ROW_NUMBER() over( ORDER BY ord, A0000 ) numberCode"); 
				sqlstr.append(",A.* from ("+strwhere+") A) T where numberCode between "+((index-1)*size+1)+" and "+(size*index));				
    		}
    	
    	}catch(Exception e){
    	    throw GeneralExceptionHandler.Handle(e);  
		}
		return sqlstr.toString();
    }
    
    /**
     * 
     * @Title: saveQueryResultBase   
     * @Description:数据是否需要保存到结果表    
     * @throws GeneralException 
     * @return void
     */
    private void saveQueryResultBase() throws GeneralException {
		try {
			String[] dbpres = mpre.split("`");
			for (int i = 0; i < dbpres.length; i++) {
				String nbase = dbpres[i];
				if ("true".equals(msecond)) {// 保存查询结果
					this.saveQueryResult("1", nbase, "select " + nbase + "a01.a0100 "
									+ queryResultSQLWhere.replace("###", nbase));
				} else {// 过滤查询结果
					this.filterQueryResult("1", nbase, "select " + nbase + "a01.a0100 "
									+ queryResultSQLWhere.replace("###", nbase));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    
    /**
     * 
     * @Title: saveQueryResult   
     * @Description: 保存查询结果   
     * @param type
     * @param dbpre
     * @param sql
     * @throws GeneralException 
     * @return void
     */
    private void saveQueryResult(String type, String dbpre, String sql) throws GeneralException {
		if (this.userView.getStatus() == 4) {
			try {
				String tabldName = "t_sys_result";
				Table table = new Table(tabldName);
				DbWizard dbWizard = new DbWizard(conn);
				if (!dbWizard.isExistTable(table)) {
					return;
				}
				/** =0 人员=1 单位=2 岗位 */

				String flag = "0";
				if ("2".equalsIgnoreCase(type)) {
					flag = "1";
				} else if ("3".equalsIgnoreCase(type)) {
					flag = "2";
				}
				String str = "delete from " + tabldName + " where flag=" + flag + " and UPPER(username)='" + userView.getUserName().toUpperCase() + "'";
				if ("1".equalsIgnoreCase(type)) {
					str += " and UPPER(nbase)='" + dbpre.toUpperCase() + "'";
				}
				ContentDAO dao = new ContentDAO(conn);
				dao.delete(str, new ArrayList());
				StringBuffer buf_sql = new StringBuffer("");
				if ("1".equals(type)) {
					buf_sql.append("insert into " + tabldName);
					buf_sql.append("(username,nbase,obj_id,flag) ");
					buf_sql.append("select distinct '" + userView.getUserName()
							+ "' as username,'" + dbpre.toUpperCase()
							+ "' as nbase,A0100 as obj_id, 0 as flag");
					buf_sql.append(" from (" + sql + ") myset");
				} else if ("2".equals(type)) {
					buf_sql.append("insert into " + tabldName + " (username,nbase,obj_id,flag) ");
					buf_sql.append("select distinct '" + userView.getUserName()
							+ "' as username,'B',b0110 as obj_id,1 as flag from (" + sql + ") myset");
				} else if ("3".equals(type)) {
					buf_sql.append("insert into " + tabldName + " (username,nbase,obj_id,flag)");
					buf_sql.append("select distinct '" + userView.getUserName() + "' as username,'K',e01a1 as obj_id,2 as flag from(" + sql + ") myset");
				}
				dao.insert(buf_sql.toString(), new ArrayList());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if ("2".equals(type))
				dbpre = "B";
			if ("3".equals(type))
				dbpre = "K";
			String tablename = this.userView.getUserName() + dbpre + "result";
			autoCreatQueryResultTable(conn, tablename, type);
			StringBuffer inssql = new StringBuffer();
			String sqlStr;
			ContentDAO dao = new ContentDAO(conn);
			try {
				
				// 删除上次数据
				sqlStr = "delete from " + tablename;
				dao.update(sqlStr);
				// 插入新数据
				inssql.append("insert into " + tablename + "(###) ");
				inssql.append("select distinct ###  from (" + sql + ") myset");
				
				if ("2".equals(type)) {
					sqlStr = inssql.toString().replace("###", "B0110");
				} else if ("3".equals(type)) {
					sqlStr = inssql.toString().replace("###", "E01A1");
				} else {
					sqlStr = inssql.toString().replace("###", "A0100");
				}
				dao.update(sqlStr);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}
    
    /**
     * 业务用户查询结果表可能不存在，本类检查并自动创建
	 * @param conn
	 * @param tablename
	 * @param type： 1,2,3=  人员，单位，职位
	 * @throws GeneralException
	 */
	public static void autoCreatQueryResultTable(Connection conn, String tablename, String type) throws GeneralException{
    	// 检查查询表，不存在自动创建
		if (conn==null || tablename==null || type==null){
			return;
		}
    	DbWizard dbWizard = new DbWizard(conn);
		if (!dbWizard.isExistTable(tablename)) {
			Table table = new Table(tablename);	
			if("1".equals(type)){
				FieldItem a0100item = DataDictionary.getFieldItem("A0100");
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				table.addField(a0100item);
				table.addField(b0110item);
			}else if("2".equals(type)){
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				table.addField(b0110item);
			}else if("3".equals(type)){
				FieldItem e01a1item = DataDictionary.getFieldItem("E01A1");
				table.addField(e01a1item);
			}else{
				FieldItem a0100item = DataDictionary.getFieldItem("A0100");
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				table.addField(a0100item);
				table.addField(b0110item);
			}
			dbWizard.createTable(table);
		}
		
	}
    
    /**
     * 
     * @Title: filterQueryResult   
     * @Description: 过滤查询结果   
     * @param type
     * @param dbpre
     * @param sql
     * @throws GeneralException 
     * @return void
     */
    private void filterQueryResult(String type, String dbpre, String sql) throws GeneralException {
    	try {
    		ContentDAO dao = new ContentDAO(conn);
			if (this.userView.getStatus() == 4) {
				String tabldName = "t_sys_result";
				Table table = new Table(tabldName);
				DbWizard dbWizard = new DbWizard(conn);
				if (!dbWizard.isExistTable(table)) {
					return;
				}
				/** =0 人员=1 单位=2 岗位 */
				String flag = "0";
				if ("2".equalsIgnoreCase(type)) {
					flag = "1";
				} else if ("3".equalsIgnoreCase(type)) {
					flag = "2";
				}
				StringBuffer str = new StringBuffer("delete from " + tabldName
						+ " where flag=" + flag + " and UPPER(username)='"
						+ userView.getUserName().toUpperCase() + "'");
				if ("1".equalsIgnoreCase(type)) {
					str.append(" and UPPER(nbase)='" + dbpre.toUpperCase() + "'");
				}
				if ("2".equals(type)) {
					str.append(" and obj_id not in ");
					str.append(" (select ");
					str.append("B0110  from (");
					str.append(sql);
					str.append(") myset)");
				} else if ("3".equals(type)) {
					str.append(" and obj_id not in ");
					str.append(" (select ");
					str.append("E01A1  from (");
					str.append(sql);
					str.append(") myset)");
				} else {
					str.append(" and obj_id not in ");
					str.append(" (select ");
					str.append("A0100  from (");
					str.append(sql);
					str.append(") myset)");
				}
				try {
					dao.update(str.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if ("2".equals(type))
					dbpre = "B";
				if ("3".equals(type))
					dbpre = "K";
				String tablename = this.userView.getUserName() + dbpre + "result";
				StringBuffer delsql = new StringBuffer();				
				delsql.append("delete from  ");
				delsql.append(tablename);
				if ("2".equals(type)) {
					delsql.append(" where B0110 not in ");
					delsql.append(" (select ");
					delsql.append("B0110  from (");
					delsql.append(sql);
					delsql.append(") myset)");
				} else if ("3".equals(type)) {
					delsql.append(" where E01A1 not in ");
					delsql.append(" (select ");
					delsql.append("E01A1  from (");
					delsql.append(sql);
					delsql.append(") myset)");
				} else {
					delsql.append(" where A0100 not in ");
					delsql.append(" (select ");
					delsql.append("A0100  from (");
					delsql.append(sql);
					delsql.append(") myset)");
				}
				dao.update(delsql.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
    
    /**
     * 
     * @Title: getStatisChartListSQL   
     * @Description: 得到统计分析查询语句 
     * @throws GeneralException 
     * @return String    
     * @throws
     */
    private String getStatisChartListSQL() throws GeneralException {
    	String sqlstr = new String();
    	try{
    		StatisAnalysisBo statisAnalysisBo = new StatisAnalysisBo(userView, conn);
    		// 统计分析和人员查询的二次查询的标示符不一致
    		if("false".equals(msecond))
    			statisAnalysisBo.setLastResult("true");
//    		System.out.println(statisAnalysisBo.getLastResult());
    		statisAnalysisBo.setmOrg(statisOgr);
    		statisAnalysisBo.setmBase(mpre);
    		statisAnalysisBo.setmCondID(lexprid);
    		//获得统计分析查询语句
    		sqlstr = statisAnalysisBo.getStatisChartListSQL(statisSLegendID, statisDim, statisInfokind);          
    	}catch(Exception e){
    	    throw GeneralExceptionHandler.Handle(e);  
		} 
		return sqlstr;
	}
    
     
	/**
     * 
     * @Title: getWhereStrByBaseInfo   
     * @Description:  获得基本信息（姓名，拼音简码，工号）条件语句  
     * @param @param key
     * @param @return 
     * @return String    
     * @throws
     */
    private String getKeywordsWhereStr(){
        StringBuffer where = new StringBuffer();
        String keyword[] = keywords.split("\n");
        where.append(" and (  ");//姓名
        for(int i=0;i<keyword.length;i++){
            if(i==0){
                where.append(" a0101 like '%"+keyword[i] +"%' ");
            }else{
                where.append(" or a0101 like '%"+keyword[i] +"%' "); 
            }
        }
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		FieldItem item = DataDictionary.getFieldItem(onlyname);
		if (item != null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
		    for(int i=0;i<keyword.length;i++){
	            where.append(" or "+onlyname+" like '%"+keyword[i]+"%' ");  
	        }
			
		}
		String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
		if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag()))&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(userView.analyseFieldPriv(item.getItemid()))){
		    for(int i=0;i<keyword.length;i++){
		        where.append(" or "+pinyin_field+" like '%"+keyword[i]+"%' ");  
		    }
		}   
        where.append(")");
        return where.toString();
    }
    /**
     * 
     * @Title: getKqWhereSQL   
     * @Description: 
     * @param @return 
     * @return String    
     * @throws
     */
    private String getKqWhereSQL() {
        StringBuffer where = new StringBuffer();
        try{
            
            String strfrom =" FROM UsrA01";
            where.append(strfrom+" where 1=1 ");
            //考勤权限范围
            String code =getKqPrivCode(); 
            String codeValue =getKqPrivCodeValue(); 
            //根据考勤权限过滤人员
            if(code!=null&&code.length()!=0){
                if("UN".equalsIgnoreCase(code)){
                    where.append(" and B0110 like '"+codeValue+"%' ") ;  
                }else if("UM".equalsIgnoreCase(code)){
                    where.append(" and E0122 like '"+codeValue+"%' ") ;  
                }else if("@K".equalsIgnoreCase(code)) {
                    where.append(" and E01A1 like '"+codeValue+"%' ") ; 
                }
            }else{//没有权限
                where.append(" and 1=2 ") ;
            }
            //组合sql语句
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
        return where.toString();
    }
    /**
     * 得到考勤范围编码值
     * 先看考勤管理范围，如果没有着按人员范围取值
     * @param userView
     * @return
     */
    public  String getKqPrivCodeValue()
    {
        if(userView.isSuper_admin())
            return "";
        String privCodeValue=userView.getKqManageValue();
        if(privCodeValue!=null&&privCodeValue.length()>0)
            privCodeValue=privCodeValue.substring(2);
        else
            privCodeValue=userView.getManagePrivCodeValue();
        return privCodeValue;
    }
    /**
     * 得到考勤范围code
     * 先看考勤管理范围，如果没有着按人员范围取值
     * @param userView
     * @return
     */
    public  String getKqPrivCode()
    {
        if(userView.isSuper_admin())
            return "UN";
        String privCode=userView.getKqManageValue();
        if(privCode!=null&&privCode.length()>0)
            privCode=privCode.substring(0,2);
        else
            privCode=userView.getManagePrivCode();
        return privCode;
    }
	/**
	 * 常用查询
	 * @return
	 * @throws GeneralException 
	 */
	public String commonQuerySql() throws GeneralException {
		String factor="";
		String strwhere="";
		try
        {
			ContentDAO dao = new ContentDAO(conn);
			RecordVo vo=new RecordVo("lexpr");
	        vo.setString("id",lexprid);
	        vo=dao.findByPrimaryKey(vo);
        	String ret = vo.getString("history");
        	if(ret==null|| "".equals(ret))
        		ret="0";
        	String history = "true".equals(mhistory)?"1":"0";
        	if(!ret.equalsIgnoreCase(history)){
        		if(!"0".equalsIgnoreCase(ret)&& "0".equalsIgnoreCase(history)){
        			history=ret;
        		}
        	}
            
            String expr=vo.getString("lexpr");
            factor=vo.getString("factor");
            expr=PubFunc.keyWord_reback(expr);
            factor=PubFunc.keyWord_reback(factor);
            String type=vo.getString("type");
            String fuzzy=vo.getString("fuzzyflag");
            if(fuzzy==null|| "".equals(fuzzy))
            	fuzzy="0";
            boolean blike=false;
            if("1".equals(fuzzy))
            	blike=true;
            if(this.mlike!=null&& "true".equals(mlike))
            	blike=true;
            factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");  /*兼容报表管理、常用查询*/
            /**表过式分析*/
            boolean bhis=false;
            if("1".equals(history))
            	bhis=true;                                  
            ArrayList fieldlist=null;
            /**1人员　2:单位 3:职位*/
            if("1".equals(type))
            {
            	{
            		if((!userView.isSuper_admin()))
                    {
                        strwhere=userView.getPrivSQLExpression(expr+"|"+factor,"###",bhis,blike,Boolean.valueOf(this.msecond).booleanValue(),fieldlist);
                       
                    }
                    else
                    {
                        FactorList factorlist=new FactorList(expr,factor,"###",bhis ,blike,Boolean.valueOf(this.msecond).booleanValue(),Integer.parseInt(type),userView.getUserId());
                        fieldlist=factorlist.getFieldList();
                        strwhere=factorlist.getSqlExpression();
                    } 

            	}  
            	
            }
            
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(ee);            	
        }
		return strwhere;
	}
	
	/**
	 * 快速查询
	 * @return
	 * @throws GeneralException 
	 */
//	public String generalQuerySql() throws GeneralException{
//		
//		String lxpr="";
//		String factor="";
//		String strwhere="";
//		try{
//		String [] values = queryValue.split(":");
//		String [] selectFields = selectField.split(",");
//		if(values.length>0&&values.length==selectFields.length){
//			int num=1;
//			for(int i=0;i<values.length;i++){
//				String itemid=selectFields[i];
//				FieldItem fielditem = DataDictionary
//				.getFieldItem(itemid);
//				if(fielditem!=null){
//					String itemtype=fielditem.getItemtype();
//					String codesetid = fielditem.getCodesetid();
//					if("N".equals(itemtype)){
//						String value = values[i];
//						String[] vs=value.split(",");
//						if(vs.length==2){
//							String v1=vs[0].substring(1);
//							String v2 = vs[1];
//							if(v1.length()>0){
//								try{
//									Integer.parseInt(v1);
//									lxpr+="*"+num;
//									factor+=itemid;
//									switch(Integer.parseInt(v2)){
//									case 0:
//										factor+=">";
//										break;
//									case 1:
//										factor+="=";
//										break;
//									case 2:
//										factor+="<";
//										break;
//									}
//									factor+=v1+"`";
//									num++;
//								}catch(Exception e){
//								    throw GeneralExceptionHandler.Handle(e);  
//								}
//							}
//						}
//					}else if("A".equals(itemtype)){
//						String value=values[i].substring(1);
//						if(value.length()>0){
//							if("a0101".equalsIgnoreCase(itemid)){
//								 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
//								 String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
//								 String[] tmpvs = value.split("\\n");
//								 if(!(pinyin_field==null || pinyin_field.equals("") || pinyin_field.equals("#"))){
//									 lxpr+="*("+num;
//									 factor+=itemid+"="+value+"`";
//									 num++;
//									 lxpr+="+"+num+")";
//									 factor+=pinyin_field+"="+value+"`";
//									 num++;
//									 if(tmpvs!=null&&tmpvs.length>0){
//										lxpr+="*(";
//										for(int m=0,n=tmpvs.length;m<n;m++){
//											if(m!=0)
//												lxpr+="+";
//											lxpr+=num;
//											factor+=itemid+"="+tmpvs[m]+"`";
//											num++;
//											lxpr+="+"+num+"";
//											factor+=pinyin_field+"="+tmpvs[m]+"`";
//											num++;
//										}
//										lxpr+=")";
//									}
//									 
//								 }else{
//									 if(tmpvs!=null&&tmpvs.length>0){
//										lxpr+="*(";
//										for(int m=0,n=tmpvs.length;m<n;m++){
//											if(m!=0)
//												lxpr+="+";
//											lxpr+=num;
//											factor+=itemid+"="+tmpvs[m]+"`";
//											num++;
//										}
//										lxpr+=")";
//									}
//								 }
//							}else{
//								String[] tmpvs;
//								if("0".equals(codesetid)||codesetid==null||codesetid.length()==0){
//									tmpvs = value.split("\\n");
//									
//								}else{//代码型
//									tmpvs = value.split("`");
//								}
//								if(tmpvs!=null&&tmpvs.length>0){
//									lxpr+="*(";
//									for(int m=0,n=tmpvs.length;m<n;m++){
//										if(m!=0)
//											lxpr+="+";
//										lxpr+=num;
//										factor+=itemid+"="+tmpvs[m]+"`";
//										num++;
//									}
//									lxpr+=")";
//								}
//							}
//						}
//					}else if("D".equals(itemtype)){
//						String value=values[i];
//						String[] vs = value.split(",");
//						if(vs.length==2){
//							String v1=vs[0].substring(1);
//							String v2=vs[1];
//							if(v1.length()>0){
//								if(this.isnumber(v1)){
//									lxpr+="*"+num;
//									factor+=itemid+">=$YRS["+v1+"]`";
//									num++;
//								}else{
//									v1=checkdate(v1);
//									if(!"false".equals(v1)){
//										lxpr+="*"+num;
//										factor+=itemid+">="+v1+"`";
//										num++;
//									}
//								}
//							}
//							if(v2.length()>0){
//								if(this.isnumber(v2)){
//									lxpr+="*"+num;
//									factor+=itemid+"<=$YRS["+v2+"]`";
//									num++;
//								}else{
//									v2=this.checkdate(v2);
//									if(!"false".equals(v2)){
//										lxpr+="*"+num;
//										factor+=itemid+"<="+v2+"`";
//										num++;
//									}
//								}
//							}
//						}else if(vs.length==1){
//							String v1=vs[0].substring(1);
//							if(v1.length()>0){
//								if(this.isnumber(v1)){
//									lxpr+="*"+num;
//									factor+=itemid+">=$YRS["+v1+"]`";
//									num++;
//								}else{
//									v1=checkdate(v1);
//									if(!"false".equals(v1)){
//										lxpr+="*"+num;
//										factor+=itemid+">="+v1+"`";
//										num++;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			if(lxpr.length()>0)
//				lxpr=lxpr.substring(1);
//			strwhere=userView.getPrivSQLExpression(lxpr+"|"+factor,this.getMpre(),Boolean.valueOf(this.mhistory).booleanValue(),Boolean.valueOf(this.mlike).booleanValue(),Boolean.valueOf(this.msecond).booleanValue(),new ArrayList());
//		}
//		}catch(Exception ee)
//        {
//        	ee.printStackTrace();
//  	      	throw GeneralExceptionHandler.Handle(ee);            	
//        }
//	
//		return strwhere;
//	}
	
	/**组合查询SQL*/
    private String combine_SQL(ArrayList list,String like,String dbpre,String strInfr,String result) throws GeneralException
    {
        int j=1;
    	boolean bresult=true;
    	boolean blike=false;
    	if("1".equals(result))
    		bresult=false;  
    	if("1".equals(like))
    		blike=true;
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            //System.out.println(item.getItemdesc());
            /**如果值未填的话，default是否为不查*/
            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype())))
                continue;
            if(("".equals(item.getValue())&& "".equals(item.getViewvalue()))&&("D".equals(item.getItemtype())))
                continue;            
            if("D".equals(item.getItemtype()))
            {
                int sf=analyFieldDate(item,strexpr,strfactor,j);
                if(sf==-1)
                {
                	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
                }
                j=j+sf;
            }else if(!"0".equals(item.getCodesetid())&&item.getViewvalue()!=null&&item.getViewvalue().length()>0&&blike)
            {
            	int sf=analyFieldCodeValue(item,strexpr,strfactor,j,strInfr);
            	j=j+sf;
            }
            else
            {
            	String codesetid = item.getCodesetid();
            	String[] tmpvs=null;
            	String value = item.getValue().trim();
            	if("A".equals(item.getItemtype())){
					if("0".equals(codesetid)||codesetid==null||codesetid.length()==0){
						tmpvs = value.split("\\n");
						
					}else{//代码型
						tmpvs = value.split("`");
					}
            	}
            	if(tmpvs!=null&&tmpvs.length>0){
            		if(j==1)
    	            {
            			strexpr.append("(");
    	            }else{
    	            	strexpr.append("*(");
    	            }
					for(int m=0,n=tmpvs.length;m<n;m++){
						value = tmpvs[m];
						if(m!=0)
							strexpr.append("+");
			                strexpr.append(j);
			            
			            		String q_v=value.trim();
			                    if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
			                    {
					                strfactor.append(item.getItemid().toUpperCase());
					                if("0".equals(item.getCodesetid()))
					                	strfactor.append("=*");
					                else
					                	strfactor.append("=");			                	
					                strfactor.append(PubFunc.getStr(value.trim()));
					                strfactor.append("*`");	   
			                    }
			                    else
			                    {
					                strfactor.append(item.getItemid().toUpperCase());
					                strfactor.append("=");
					                strfactor.append(PubFunc.getStr(value.trim()));
					                strfactor.append("`");	  	                        
			                    }
			            ++j;
					}
					strexpr.append(")");
            	}else{
            		/**组合表达式串*/
    	            if(j==1)
    	            {
    	                strexpr.append(j);
    	            }
    	            else
    	            {
    	                strexpr.append("*");
    	                strexpr.append(j);                
    	            }
    	            
    	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
    	            {
    	            		String q_v=item.getValue().trim();
    	                    if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
    	                    {
    			                strfactor.append(item.getItemid().toUpperCase());
    			                if("0".equals(item.getCodesetid()))
    			                	strfactor.append("=*");
    			                else
    			                	strfactor.append("=");			                	
    			                strfactor.append(PubFunc.getStr(item.getValue()));
    			                strfactor.append("*`");	   
    	                    }
    	                    else
    	                    {
    			                strfactor.append(item.getItemid().toUpperCase());
    			                strfactor.append("=");
    			                strfactor.append(PubFunc.getStr(item.getValue()));
    			                strfactor.append("`");	  	                        
    	                    }
    	            }
    	            else
    	            {
    	                strfactor.append(item.getItemid().toUpperCase());
    	                strfactor.append("=");
    	                strfactor.append(PubFunc.getStr(item.getValue()));
    	                strfactor.append("`");
    	            }
    	            ++j;
            	}
            }
        }//for i loop end.
        ArrayList fieldlist=new ArrayList();
        if(!userView.isSuper_admin()&& "1".equals(strInfr))
        {
            String strpriv=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(), dbpre, Boolean.valueOf(this.mhistory).booleanValue(),blike,bresult, fieldlist);
            return strpriv;
        }
        else
        {
        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,Boolean.valueOf(this.mhistory).booleanValue(),blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
            //fieldlist=factorlist.getFieldList();
        	factorlist.setSuper_admin(userView.isSuper_admin());
            return factorlist.getSqlExpression();
        }
    }
    
    /**分析日期型字段*/
    private int analyFieldDate(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos)
    {
        String s_str_date=item.getValue().trim();
        String e_str_date=item.getViewvalue().trim();
        s_str_date=s_str_date.replaceAll("\\.","-");
        e_str_date=e_str_date.replaceAll("\\.","-");
        //item.setValue(s_str_date);
        //item.setViewvalue(e_str_date);
      
        try
        {
            Date s_date=DateStyle.parseDate(s_str_date);
            Date e_date=DateStyle.parseDate(e_str_date);          	
	        /**起始日期及终止日期格式全对*/
	        if(s_date!=null&&e_date!=null)
	        {
	            if(strexpr.length()==0)
	            {
	              strexpr.append(pos);
	              strexpr.append("*");
	              strexpr.append(pos+1);
	            
	            }
	            else
	            {
	                strexpr.append("*(");                
	                strexpr.append(pos);
	                strexpr.append("*");
	                strexpr.append(pos+1);  
	                strexpr.append(")");
	            }
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append(">=");
	            strfactor.append(item.getValue().replaceAll("-","."));
	            strfactor.append("`");
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append("<=");
	            strfactor.append(item.getViewvalue().replaceAll("-",".")); 
	            strfactor.append("`");   
	            return 2;
	        }
	        else if (isnumber(s_str_date) && isnumber(e_str_date))
	        {
	            if(strexpr.length()==0)
	            {
	              strexpr.append(pos);
	              strexpr.append("*");
	              strexpr.append(pos+1);
	            
	            }
	            else
	            {
	                strexpr.append("*(");                
	                strexpr.append(pos);
	                strexpr.append("*");
	                strexpr.append(pos+1);  
	                strexpr.append(")");
	            }
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append(">=$YRS[");
	            strfactor.append(item.getValue());
	            strfactor.append("]`");
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append("<=$YRS[");
	            strfactor.append(item.getViewvalue()); 
	            strfactor.append("]`");
	            return 2;
	        }
	        else
	        {
	        	if(s_str_date.length()>0){
	        		s_date=DateStyle.parseDate(s_str_date);
	        		if(s_date!=null)
	    	        {
	    	            if(strexpr.length()==0)
	    	            {
	    	              strexpr.append(pos);
	    	            
	    	            }
	    	            else
	    	            {
	    	                strexpr.append("*");                
	    	                strexpr.append(pos);
	    	            }
	    	            strfactor.append(item.getItemid().toUpperCase());
	    	            strfactor.append(">=");
	    	            strfactor.append(item.getValue().replaceAll("-","."));
	    	            strfactor.append("`");  
	    	            return 1;
	    	        }
	    	        else if (isnumber(s_str_date))
	    	        {
	    	            if(strexpr.length()==0)
	    	            {
	    	              strexpr.append(pos);
	    	            
	    	            }
	    	            else
	    	            {
	    	                strexpr.append("*");                
	    	                strexpr.append(pos);
	    	            }
	    	            strfactor.append(item.getItemid().toUpperCase());
	    	            strfactor.append(">=$YRS[");
	    	            strfactor.append(item.getValue());
	    	            strfactor.append("]`");
	    	            return 1;
	    	        }
	                
	        	}
	        	if(e_str_date.length()>0){
	        		e_date=DateStyle.parseDate(e_str_date); 
	        		
	        		if(e_date!=null)
	    	        {
	    	            if(strexpr.length()==0)
	    	            {
	    	              strexpr.append(pos);
	    	            
	    	            }
	    	            else
	    	            {
	    	                strexpr.append("*");                
	    	                strexpr.append(pos);
	    	            }
	    	            strfactor.append(item.getItemid().toUpperCase());
	    	            strfactor.append("<=");
	    	            strfactor.append(item.getViewvalue().replaceAll("-",".")); 
	    	            strfactor.append("`");   
	    	            return 1;
	    	        }
	    	        else if (isnumber(e_str_date))
	    	        {
	    	            if(strexpr.length()==0)
	    	            {
	    	              strexpr.append(pos);
	    	            
	    	            }
	    	            else
	    	            {
	    	                strexpr.append("*");                
	    	                strexpr.append(pos);
	    	            }
	    	            strfactor.append(item.getItemid().toUpperCase());
	    	            strfactor.append("<=$YRS[");
	    	            strfactor.append(item.getViewvalue()); 
	    	            strfactor.append("]`");
	    	            return 1;
	    	        }
	        	}
	        }
        }
        catch(Exception ex)
        {
        	return -1;
        }
        return 0;
    }
    
    private int analyFieldCodeValue(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos,String strInfr)
    {
        String str_Hz=item.getViewvalue();
        String[] strs = str_Hz.split("\\n");
        StringBuffer sql = new StringBuffer();
        if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))
        {
        	sql.append("select codeitemid from organization where codesetid='"+item.getCodesetid()+"' and (");// codeitemdesc like '%"+str_Hz+"%'";	
        }else
        {
        	sql.append("select codeitemid from codeitem where codesetid='"+item.getCodesetid()+"' and (");// codeitemdesc like '%"+str_Hz+"%'";	
        }
        
        if(strs.length>0){
	        for(int i=0,n=strs.length;i<n;i++){
	        	str_Hz = strs[i];
		        if(str_Hz.indexOf("*")==str_Hz.length()-1||str_Hz.indexOf("?")==str_Hz.length()-1||str_Hz.indexOf("？")==str_Hz.length()-1)
		           str_Hz=str_Hz.substring(0,str_Hz.length()-1);
		        if(i!=0)
		        	sql.append(" or ");
		        sql.append(" codeitemdesc like '%"+str_Hz+"%' ");
	        }
        }else{
        	sql.append(" codeitemdesc like '%"+str_Hz+"%' ");
        }
        sql.append(")");
        //item.setValue(s_str_date);
        //item.setViewvalue(e_str_date);
       
        List list=ExecuteSQL.executeMyQuery(sql.toString());
        if(list!=null&&list.size()>0)
        {
        	if(pos>1)
        		strexpr.append("*");
        	strexpr.append("("); 
        	for(int i=0;i<list.size();i++)
        	{
        		LazyDynaBean rec=(LazyDynaBean)list.get(i);
        		String codeitemid=(String)rec.get("codeitemid");
        		strexpr.append(pos++);
        		strexpr.append("+");
        		strfactor.append(item.getItemid().toUpperCase());
        		strfactor.append("=");
        		strfactor.append(""+codeitemid+"");
        		strfactor.append("`");
        	}
        	strexpr.setLength(strexpr.length()-1);
        	strexpr.append(")");
        	return list.size();
        }else
        {
        	return 0;
        }
        
    }
	
	private boolean isnumber(String strvalue)
    {
        boolean bflag=true;
        try
        {
            Float.parseFloat(strvalue.replaceAll("-","."));
        }
        catch(NumberFormatException ne)
        {
            bflag=false;
        }
        return bflag;
    }
	
	/**
	 * 获取人员信息简介
	 * @param dbpre
	 * @param a0100
	 * @return
	 * @throws GeneralException 
	 */
	private String getInfo(String dbpre,String a0100) throws GeneralException{
		RowSet rs = null;
		try
        {  
			ContentDAO dao = new ContentDAO(conn);
			Map map = HTMLParamUtils.getBasicinfo_Map(conn);
			if(map==null)
				return "";
			String basicinfo_template = (String)map.get("basicinfo_template");
			Map mapsets = (Map)map.get("mapsets");
			Map mapsetstr = (Map)map.get("mapsetstr");
			for(Iterator i = mapsets.keySet().iterator();i.hasNext();){
				String setid = (String)i.next();
				List itemids = (List)mapsets.get(setid);
				String itemidstr = ((StringBuffer)mapsetstr.get(setid)).substring(1);
				StringBuffer sql=new StringBuffer();
				sql.append("select "+itemidstr+" from "+dbpre+setid+" where a0100='"+a0100+"'");
				if(!"A01".equals(setid))
					sql.append(" and i9999=(select max(i9999) from "+dbpre+setid+" where a0100='"+a0100+"')");
				rs = dao.search(sql.toString());
				if(rs.next()){
					for(int n=0;n<itemids.size();n++){
						String itemid = (String)itemids.get(n);
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						String itemtype = fielditem.getItemtype();
						String value = "";
						if("N".equals(itemtype)){
							if(fielditem.getDecimalwidth()>0)
                        		value = String.valueOf(rs.getObject(itemid));
                        	else
                        		value=String.valueOf(rs.getInt(itemid));
						}else if("D".equals(itemtype)){
							Object obj=rs.getDate(itemid);
							if(obj==null){
								value = "";
							}else{
								String format = "yyyy-MM-dd";
								FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
								switch (fieldItem.getItemlength()){
									case 4:
										format="yyyy";
										break;
									case 7:
										format = "yyyy-MM";
										break;
									case 10:
										format = "yyyy-MM-dd";
										break;
									case 16:
										format = "yyyy-MM-dd HH:mm";
										break;
									default:
										format = "yyyy-MM-dd HH:mm:ss";
								}
								SimpleDateFormat sdf = new SimpleDateFormat(format);
								value = sdf.format(obj);
							}

							//value = String.valueOf(obj==null?"":obj);
							//value=value.replace('-', '.');
						}else if("A".equals(itemtype)){
							String codesetid = fielditem.getCodesetid();
							value=rs.getString(itemid);
							value=value==null?"":value;
							if(!(codesetid.length()==0||"0".equals(codesetid))){
								value= AdminCode.getCodeName(codesetid, value);
							}
						}
						basicinfo_template = basicinfo_template.replace("["+itemid+"]", value);
					}
				}else{
					for(int n=0;n<itemids.size();n++){
						String itemid = (String)itemids.get(n);
						basicinfo_template = basicinfo_template.replace("["+itemid+"]", "");
					}
				}
			}
	        return basicinfo_template;           
        }
        catch(Exception e)
        {
            
            throw GeneralExceptionHandler.Handle(e);  
        }finally{
			PubFunc.closeResource(rs);
        }
        
	}
	
	/**
	 * 获取常用查询条件项
	 * @return
	 */
	public void getLexpr(HashMap hm){
		ArrayList lexprlist = new ArrayList();
        String type="1";
        StringBuffer strsql= new StringBuffer();
        strsql.append("select id,name,type,categories from lexpr where type='");//
        strsql.append(type);
        strsql.append("' order by norder");
        ContentDAO dao=new ContentDAO(conn);
        ArrayList tempcates = new ArrayList();
        List lexprgruops = new ArrayList();
        List lexprs = new ArrayList();
        RowSet rs = null;
        try
        {
            /**常用查询条件列表*/
            rs=dao.search(strsql.toString());
            while(rs.next())
            {
                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,rs.getString("id"))))
                	continue;
                if("1".equals(type)){
	                String categories = rs.getString("categories");
	                categories = categories==null?"":categories;
	                if(categories.length()==0){
		                DynaBean vo=new LazyDynaBean();
		                vo.set("id",rs.getString("id"));
		                vo.set("name",rs.getString("name"));
		                vo.set("type",rs.getString("type"));
		                lexprlist.add(vo);
	                }
	                if(categories.length()>0&&!tempcates.contains(categories)){
	                	tempcates.add(categories);
	                }
                }else{
                	DynaBean vo=new LazyDynaBean();
	                vo.set("id",rs.getString("id"));
	                vo.set("name",rs.getString("name"));
	                vo.set("type",rs.getString("type"));
	                lexprlist.add(vo);
                }
            }
            
          //创建一级条目容器
            //List<Map<String, String>> lexprgruops = new ArrayList<Map<String,String>>();
            /**
             * 常用查询列表
             */
        	//List<List<Map<String, String>>> lexprlist = new ArrayList<List<Map<String,String>>>();
            
            if(lexprlist.size()>0){
            	Map group = new HashMap();
            	group.put("categories", ResourceFactory.getProperty("mobileapp.emp.categories.null"));
            	lexprgruops.add(group);
            	lexprs.add(lexprlist);
            }
            for(int i=0;i<tempcates.size();i++){
            	String categories = (String)tempcates.get(i);
            	strsql.setLength(0);
            	strsql.append("select id,name,type,categories from lexpr where type='");//
                strsql.append(type);
                strsql.append("' and categories='"+categories);
                strsql.append("' order by norder");
                rs=dao.search(strsql.toString());
                while(rs.next()){
                    if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,rs.getString("id"))))
                    	continue;
                    lexprlist = new ArrayList();
                    DynaBean vo=new LazyDynaBean();
    	            vo.set("id",rs.getString("id"));
    	            vo.set("name",rs.getString("name"));
    	            vo.set("type",rs.getString("type"));
    	            lexprlist.add(vo);
                }
                Map group = new HashMap();
            	group.put("categories", categories);
                lexprgruops.add(group);
                lexprs.add(lexprlist);
            }
            
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();         
        }
        finally
        {
            if(rs!=null){
            	try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
        }
        hm.put("lexprlist", lexprs);
        hm.put("catelist", lexprgruops);
	}
	
	/**
	 * 获取人员联系方式
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public List getContacts(String dbpre,String a0100){
		List contacts = new ArrayList();
		List contactways = HTMLParamUtils.getContacts(conn);
		RowSet rs = null;
		try{
			if(contactways.size()>0){
				List contactlist = new ArrayList();
				for(int i=0,n=contactways.size();i<n;i++){
					HashMap map = (HashMap)contactways.get(i);
					contactlist.add(map.get("menuid"));
				}
				StringBuffer sqlsb = new StringBuffer();
				sqlsb.append("select ");
				sqlsb.append(contactlist.toString().substring(1,contactlist.toString().length()-1));
				sqlsb.append(" from "+dbpre+"A01 where a0100='"+a0100+"'");
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(sqlsb.toString());
				if(rs.next()){
					for(int i=0,n=contactlist.size();i<n;i++){
						String itemid = (String)contactlist.get(i);
						String value = rs.getString(itemid);
						if(value==null||value.length()==0)
							continue;
						HashMap map = (HashMap)contactways.get(i);
						HashMap mapc = new HashMap();
						mapc.putAll(map);
						mapc.put("content", value);
						contacts.add(mapc);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return contacts;
	}
	
	/**
	 * 获取人员联系方式
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public Map getContact(String dbpre,String a0100){
		/*map = new HashMap<String, Object>();
		map.put("content", "13801297310");		
		map.put("img", R.drawable.sms);
		mContactlist.add(map);*/
		Map contact= new HashMap();
		RowSet rs = null;
		try{
			
			String smsField="",mobileField="",phoneField="",emailField="";
			RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", conn);
			if(vo!=null){
				mobileField = vo.getString("str_value");
				mobileField = mobileField==null?"":mobileField;
				
				smsField= vo.getString("str_value");
				smsField = smsField==null?"":smsField;
				FieldItem item = DataDictionary.getFieldItem(mobileField.toLowerCase());
				if(item==null|| "0".equals(item.getUseflag())){
					mobileField= "";
					smsField = "";
				}
			}
			vo = ConstantParamter.getConstantVo("SS_TELEPHONE", conn);
			if(vo!=null){
				phoneField = vo.getString("str_value");
				phoneField = phoneField==null?"":phoneField;
				FieldItem item = DataDictionary.getFieldItem(phoneField.toLowerCase());
				if(item==null|| "0".equals(item.getUseflag())){
					phoneField= "";
				}
			}
			vo = ConstantParamter.getConstantVo("SS_EMAIL", conn);
			if(vo!=null){
				emailField = vo.getString("str_value");
				emailField = emailField==null?"":emailField;
				FieldItem item = DataDictionary.getFieldItem(emailField.toLowerCase());
				if(item==null|| "0".equals(item.getUseflag())){
					emailField= "";
				}
			}
			StringBuffer sqlsb = new StringBuffer();
			sqlsb.append("select ");
			//是否有可显示的联系方式
			boolean flag = false;
			if(mobileField.length()>0){
				sqlsb.append(mobileField);
				flag = true;
			}
			if(phoneField.length()>0){
				sqlsb.append(","+phoneField);
				flag = true;
			}
			if(emailField.length()>0){
				sqlsb.append(","+emailField);
				flag = true;
			}
			if(flag){
				sqlsb.append(" from "+dbpre+"A01 where a0100='"+a0100+"'");
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(sqlsb.toString());
				if(rs.next()){
					if(mobileField.length()>0){
						contact.put("sms", rs.getString(mobileField));
						contact.put("mobile", rs.getString(mobileField));
					}else{
						contact.put("sms", "");
						contact.put("mobile", "");
					}
					if(phoneField.length()>0){
						contact.put("phone", rs.getString(phoneField));
					}
					if(emailField.length()>0){
						contact.put("email", rs.getString(emailField));
					}else{
						contact.put("email", "");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return contact;
	}
	
	/**
	 * 
	 * @Title: getSubSetInfo   
	 * @Description: 获得展示的子集列表   
	 * @param dbpre 库前缀
	 * @param a0100 要查询的用户ID
	 * @return List    
	 * @throws
	 */
	public List getSubSetInfo(String dbpre, String a0100) {
		List subSetList = new ArrayList();
		RowSet rs = null;
		/** 一级目录，子集列表 */
		try {
			ArrayList sortSets = HTMLParamUtils.getSortFieldSets(conn);
			HashMap setsMap = HTMLParamUtils.getSetsMap(conn);
			FieldSet fieldset = null;
			// 子集的ID
			String id = "";
			// 子集的名
			String title = "";
			StringBuffer sql = new StringBuffer();
			// 遍历讲子集信息放入list集合
			for(int i=0,n=sortSets.size();i<n;i++){
				id = (String) sortSets.get(i);
				// 查看自己的信息时不用判断子集权限，否则判断子集权限
				if("0".equals(this.userView.analyseTablePriv(id)) && !a0100.equals(this.userView.getA0100()))
					continue;
				//初始化
				sql.setLength(0);
				// 查询语句
				sql.append("select * from ");
				sql.append(dbpre + id + " where a0100='" + a0100);
				sql.append("'");
				// 查询
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(sql.toString());
				//判断该子集是否有记录，有则显示该记录。
				if(rs.next()){
					List itemids = (List) setsMap.get(id);
					FieldItem item = null;
					ArrayList privItems = new ArrayList();
					for (int m = 0; m < itemids.size(); m++) {
						String itemid = (String) itemids.get(m);
						item = DataDictionary.getFieldItem(itemid);
						if (item == null) {
							itemids.remove(m);
							continue;
						}
						if(!"0".equals(this.userView.analyseFieldPriv(itemid)) || a0100.equals(this.userView.getA0100()))
							privItems.add(itemid);
					}
					if (itemids.size() == 0||privItems.size()==0)
						continue;
					HashMap hm = new HashMap();				
					fieldset = DataDictionary.getFieldSetVo(id);
					title = fieldset.getCustomdesc();
					hm.put("id", id);
					hm.put("title", title);
					subSetList.add(hm);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return subSetList;
	}

	/**
	 * 
	 * @Title: getSubSetDetailed
	 * @Description:子集的详细信息
	 * @param dbpre 库前缀
	 * @param a0100
	 *            人员ID
	 * @param setid
	 *            库后缀
	 * @return
	 * @return List
	 * @throws
	 */
	public List getSubSetDetailed(String dbpre, String a0100, String setid) {
		// 返回的人员库详细信息
		List childList = new ArrayList();
		RowSet rs = null;
		HashMap map = null;
		FieldItem item = null;
		ArrayList itemids = new ArrayList();
		ArrayList privItems = new ArrayList();
		try {
			Map mapSubSet = HTMLParamUtils.getSetsMap(conn);
			// 排查没有描述的指标
			if (mapSubSet != null) {
				itemids = (ArrayList) mapSubSet.get(setid);
				for (int m = 0; m < itemids.size(); m++) {
					String itemid = (String) itemids.get(m);
					item = DataDictionary.getFieldItem(itemid);
					if (item == null) {
						itemids.remove(m);
						continue;
					}
					//if (!"0".equals(this.userView.analyseFieldPriv(itemid)))  
					privItems.add(itemid);
				}
				if (itemids.size() == 0 || privItems.size() == 0)
					return null;
			}
			StringBuffer sql = new StringBuffer();
			// 查询语句
			sql.append("select ");
			sql.append(privItems.toString().substring(1, privItems.toString().length() - 1));
			sql.append(" from ");
			sql.append(dbpre + setid + " where a0100='" + a0100 +"'");
			if(!"A01".equals(setid))
				sql.append(" order by i9999");
			// 查询
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			String value = "";
			boolean flag = false;
			// 遍历将结果放入list，结果用list+map包装
			while (rs.next()) {
				List detailedList = new ArrayList();
				for (int i = 0, n = privItems.size(); i < n; i++) {
					item = DataDictionary.getFieldItem((String) privItems.get(i));
					map = new HashMap();
					map.put("id", privItems.get(i));
					map.put("title", item.getItemdesc());
					// 指标转换
					String itemtype = item.getItemtype();
					String itemid = (String) item.getItemid();
					if ("N".equals(itemtype)) {// 数据类型
						// int类型
						if (0 == item.getDecimalwidth()) {
							value = String.valueOf(rs.getInt(itemid));
						} else {// double
							value = String.valueOf(rs.getDouble(itemid));
						}					
					} else if ("D".equals(itemtype)) {//时间类型
						Object obj = rs.getDate(itemid);
						value = String.valueOf(obj == null ? "" : obj);
						//xus 18/8/22 【39751 】 修改指标长度后 值没变问题
						if(item.getItemlength()<value.length())
							value = value.substring(0, item.getItemlength());
						value = value.replace('-', '.');
					} else if ("A".equals(itemtype)) {//普通类型
						String codesetid = item.getCodesetid();
						value = rs.getString(itemid);
						if (!(codesetid.length() == 0 || "0".equals(codesetid)))
							value = AdminCode.getCodeName(codesetid, value);
					}else if("M".equals(itemtype)){//备注型
						String codesetid = item.getCodesetid();
						value = rs.getString(itemid);
					}
					if (value == null || value.length() == 0)
						map.put("value", "");
					else {
						map.put("value", value);
						//有真实数据，可以显示
						flag = true;
					}
					detailedList.add(map);
				}
				//做出判断是否需要显示该子集
				if (flag) {
					childList.add(detailedList);
					flag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return childList;
	}

	/**
	 * 获取人员汇报对象
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public Map getLeaderInfo(String dbpre,String a0100){
		RowSet rs = null;
		Map leaderBean=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer strsql = new StringBuffer();
			strsql
			.append("select tm.mainbody_id mainbody_id,tm.a0101 a0101 from t_wf_relation tr  left join t_wf_mainbody tm on tr.Relation_id=tm.Relation_id where tr.default_line=1 and upper(tm.object_id)='"
					+ (dbpre+a0100).toUpperCase() + "' and tm.sp_grade=9");
			rs = dao.search(strsql.toString());
			StringBuffer mainbody_idSb = new StringBuffer();
			StringBuffer a0101Sb = new StringBuffer();
			while(rs.next()){
				String mainbody_id = rs.getString("mainbody_id");
				String a0101 = rs.getString("a0101");
				mainbody_idSb.append("`"+mainbody_id);
				a0101Sb.append("、"+a0101);
			}
			if(mainbody_idSb.length()>0){
				leaderBean = new HashMap();
				leaderBean.put("content", a0101Sb.substring(1));
				leaderBean.put("mpre", mainbody_idSb.substring(1));
				leaderBean.put("type", "leader");
				leaderBean.put("prea0100", dbpre+a0100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return leaderBean;
	}

	/**
	 * 获取下属员工信息
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public Map getStaffInfo(String dbpre,String a0100){
		RowSet rs = null;
		Map staffBean=new HashMap();
		try {
			Map dbprea0100Map = getLeaderPreA0100s(dbpre, a0100);
			ContentDAO dao = new ContentDAO(conn);
			DbNameBo dbNameBo = new DbNameBo(conn,this.userView);
			String username = dbNameBo.getLogonUserNameField();
			StringBuffer strsql = new StringBuffer();
			strsql.append("select "+username+" from "+dbpre+"A01 where a0100='"+a0100+"'");
			rs = dao.search(strsql.toString());
			strsql.setLength(0);
			if(rs.next()){
				String logonname = rs.getString(username);
				if(logonname==null||logonname.length()==0){
					staffBean.put("content", "0");
				}else{
					RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
					String logindbpre="";
		            if(login_vo!=null) 
		            	logindbpre = login_vo.getString("str_value").toLowerCase();
					UserView uv = new UserView(logonname,conn);
					uv.canLogin();
					ArrayList dbpres = uv.getPrivDbList();
					for(int i=0;i<dbpres.size();i++){
						String pre = (String)dbpres.get(i);
						if(logindbpre.toUpperCase().indexOf(pre.toUpperCase())==-1){
							dbpres.remove(i);
							--i;
						}
					}
					if(dbpres.size()>0){
						strsql.append("select count(a0100) num from (");
						for(int i=0,n=dbpres.size();i<n;i++){
							String pre = (String)dbpres.get(i);
							if(i!=0)
								strsql.append(" union all ");
							strsql.append("select distinct a0100 "+uv.getPrivSQLExpression(pre, false));
							if(dbpre.equalsIgnoreCase(pre)){
								strsql.append(" and a0100<>'"+a0100+"'");
							}
							if(dbprea0100Map.containsKey(pre)){
								List a0100s =(List) dbprea0100Map.get(pre);
								strsql.append(" and a0100 not in('"+a0100s.toString().substring(1, a0100s.toString().length()-1).replaceAll(", ", "','")+"')");
							}
						}
						strsql.append(") A");
						rs = dao.search(strsql.toString());
						if(rs.next())
							staffBean.put("content", rs.getInt("num")+"");
						staffBean.put("mpre", dbpres.toString().substring(1,dbpres.toString().length()-1).replaceAll(", ", "`"));
					}else{
						staffBean.put("content", "0");
					}
				}
				staffBean.put("prea0100", dbpre+a0100);
				staffBean.put("type", "staff");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return staffBean;
	}
	
	/**
	 * 获取最近入职员工
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public Map getStaffAddInfo(String dbpre,String a0100){
		Map staffBean=new HashMap();		
		try {
			// 从模板中获取查询条件
			HashMap map = HTMLParamUtils.getStaffaddMap(conn);
			if(map != null){
				// 获取条件ID
				int gwhereID = Integer.parseInt((String) map.get("id"));
				// 获取库前缀
				String dbaseIDLibrary = (String) map.get("nbase");
				// 获取最近入职信息
				staffBean = this.getStaffInfoWithGwhereID(dbpre, a0100, gwhereID,dbaseIDLibrary);
				// 追加标示符，用于前台显示时的判断
				staffBean.put("type", "staffadd");
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return staffBean;
	}
	
	/**
	 * 获取最近离职员工人数
	 * 
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public Map getStaffMinusInfo(String dbpre, String a0100) {
		Map staffBean = new HashMap();
		try {
			// 通过这个ID查询，获得条件语句,预留接口
			HashMap map = HTMLParamUtils.getStaffminusMap(conn);
			if(map != null){
				int gwhereID = Integer.parseInt((String) map.get("id"));
				String dbaseIDLibrary = (String) map.get("nbase");
				//获得最近离职员工
				staffBean = this.getStaffInfoWithGwhereID(dbpre, a0100, gwhereID, dbaseIDLibrary);
				//追加标示符
				staffBean.put("type", "staffminus");
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return staffBean;
	}

	
	/**
	 * 
	 * @Title: getStaffInfoWithGwhereID
	 * @Description: 根据 gwhereID,dbaseIDLibrary 查询职员的人数
	 * @param dbpre 库前缀
	 * @param a0100 用户id
	 * @param gwhereID 库中设置的条件ID
	 * @param dbaseIDLibrary 设置的库前缀
	 * @return Map
	 * @throws
	 */
	private Map getStaffInfoWithGwhereID(String dbpre, String a0100, int gwhereID, String dbaseIDLibrary) {
		Map staffBean = new HashMap();
		int content = 0;
		try {
			// 获得该人员的UserView对象
			UserView uView = this.getUserView(dbpre, a0100);
			StringBuffer mpre = new StringBuffer();
			if (uView != null && dbaseIDLibrary != null && dbaseIDLibrary.length() > 0) {
				// 初始查询条件
				String conditions = this.getFindConditions(gwhereID);
				List list = this.getConditionsList(uView, conditions,dbaseIDLibrary);
				// 获取人数
				if (list.size() > 0) {
					for (int i = 0, n = list.size(); i < n; i++) {
						HashMap hashMap = (HashMap) list.get(i);
						String result = (String) hashMap.get("result");
						if (result.length() > 0) {
							String[] strList = result.split(",");
							content += strList.length;
							mpre.append((String) hashMap.get("dbpre"));
							mpre.append("`");
						}
					}
				}
				if (content > 0)
					staffBean.put("content", String.valueOf(content));
				else
					staffBean.put("content", "0");
			} else
				staffBean.put("content", "0");
			staffBean.put("prea0100", dbpre + a0100);
			staffBean.put("mpre", mpre.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return staffBean;
	}
	
	/**
	 * 
	 * @Title: getConditionsList
	 * @Description: 根据数据库中保存的条件查询人员
	 * @param uView
	 * @param conditions
	 * @return
	 * @return ArrayList
	 * @throws
	 */
	private ArrayList getConditionsList(UserView uView, String conditions , String dbaseIDLibrary) {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			conditions = PubFunc.keyWord_reback(conditions);
			ContentDAO dao = new ContentDAO(conn);
			// 计算
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			InfoUtils infoUtils = new InfoUtils();
			alUsedFields.addAll(infoUtils.getMidVariableList("3", "0", conn));
			ArrayList dbaseUserlist = uView.getPrivDbList();
			String[] strID = dbaseIDLibrary.split("`");
			// 组装读取的Listl
			ArrayList dbaseIDList = new ArrayList();
			for (int i = 0, n = strID.length; i < n; i++)
				dbaseIDList.add(strID[i]);
			// 取两个数据的交集
			List dbaselist = this.getIntersectionList(dbaseUserlist, dbaseIDList);
			// 判断是否有没有授权人员库
			if (dbaselist.size() > 0) {
				String tempTableName = "";
				// 弥补sql漏洞，如输入'会产生错误，将其转换
				conditions = PubFunc.getStr(conditions);
				StringBuffer sql = new StringBuffer();
				HashMap map = new HashMap();
				for (int i = 0; i < dbaselist.size(); i++) { 
					// 根据权限,生成select.IN中的查询串
					String whereA0100In = InfoUtils.getWhereINSql(uView,
							dbaselist.get(i).toString());
					// 构造函数分析器
					YksjParser yp = new YksjParser(uView, alUsedFields,
							YksjParser.forSearch, YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
							, YksjParser.forPerson, "gw", dbaselist.get(i).toString());
					YearMonthCount ycm = null;
					yp.setSupportVar(true); // 支持临时变量
					// complex_expr="姓名<>\"\" 且 性别=\"1\"";
					yp.run_Where(conditions, ycm, "", "", dao, "", conn, "A",null);
					tempTableName = yp.getTempTableName();
					// 获取用户的a0100数据，显示的时候用
					sql.append("select a0100 from " + tempTableName + "");
					sql.append(" where " + yp.getSQL() + " ");
					if (!uView.isSuper_admin())
						sql.append(" and   a0100 in(select " + dbaselist.get(i).toString() + "a01.a0100 " + whereA0100In + ") ");
					rs = dao.search(sql.toString());
					StringBuffer strResult = new StringBuffer();
					while (rs.next()) {
						if (rs.getString("a0100").length() > 0) {						
							strResult.append("'");
							strResult.append(rs.getString("a0100"));
							strResult.append("',");
						}
					}
					//只加载有值的库
					if(strResult.length()>0){
						map.put("dbpre", dbaselist.get(i).toString());
						map.put("result", strResult.toString());
						list.add(map);
					}
					// 初始化
					strResult.setLength(0);
					sql.setLength(0);
					map = new HashMap();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: getIntersectionList
	 * @Description: 取两个数组中的交集
	 * @param dbaseUserlist
	 * @param dbaseIDList
	 * @return List
	 * @throws
	 */
	private List getIntersectionList(List dbaseUserList, List dbaseIDList) {
		// 最差的情况就是两个list完全不相同
		Map map = new HashMap(dbaseUserList.size() + dbaseIDList.size());
		// 返回的相同
		List sameList = new ArrayList();
		// 标识符
		Integer flag = new Integer(1);
		boolean boo = true;
		try {
			// 判定两张表的大小，通过不同的语句执行。先放入大的表，然后使用小的表去查询两个list的区别
			if (dbaseIDList.size() > dbaseUserList.size())
				boo = false;
			if (boo) {
				// 放入大的数据库查询到的集合，以下方法不适合用于jdk的高版本，在高版本使用自动封装，效率更高
				for (int i = 0, n = dbaseUserList.size(); i < n; i++) {
					map.put(dbaseUserList.get(i), flag);
				}
				// 循环遍历小的集合，在大的集合找到是，放入返回的sameList中
				for (int i = 0, n = dbaseIDList.size(); i < n; i++) {
					if (map.containsKey(dbaseIDList.get(i)))
						sameList.add(dbaseIDList.get(i));
				}
			} else {
				// 放入大的list集合
				for (int i = 0, n = dbaseIDList.size(); i < n; i++) {
					map.put(dbaseIDList.get(i), flag);
				}
				// 循环遍历小的集合，在大的集合找到是时，不做任何操作，否则放入返回的list中
				for (int i = 0, n = dbaseUserList.size(); i < n; i++) {
					if (map.containsKey(dbaseUserList.get(i)))
						sameList.add(dbaseUserList.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sameList;
	}
		
	/**
	 * 获取员工详细业务列表中员工列表sql语句
	 * @return
	 * @throws GeneralException 
	 */
	public String getEmpListSql() throws GeneralException{
		
		if("leader".equals(querytype)){//汇报对象
			String[] prea0100s = this.mpre.split("`");
			HashMap prea0100Map = new HashMap();
			for(int i=0,n=prea0100s.length;i<n;i++){
				String pre =prea0100s[i].substring(0, 3);
				String a0100 =prea0100s[i].substring(3);
				if(prea0100Map.containsKey(pre)){
					ArrayList a0100s = (ArrayList)prea0100Map.get(pre);
					a0100s.add(a0100);
				}else{
					ArrayList a0100s = new ArrayList();
					a0100s.add(a0100);
					prea0100Map.put(pre, a0100s);
				}
			}
			StringBuffer sql = new StringBuffer();
			ArrayList dbpres = DataDictionary.getDbpreList();
			for(int i=0,n=dbpres.size();i<n;i++){
				String dbpre = (String)dbpres.get(i);
				if(prea0100Map.containsKey(dbpre)){
					ArrayList a0100s = (ArrayList)prea0100Map.get(dbpre);
					sql.append(" union ");
					sql.append("select distinct "+dbpre+"a01.a0100,'"+(i+1)+"' ord,'" +dbpre+"' dbpre,"+dbpre+"a01.b0110,"+dbpre+"a01.e01a1,"+dbpre+"a01.e0122,a0101,a0000 from "+dbpre+"A01 where a0100 in('"+a0100s.toString().substring(1,a0100s.toString().length()-1).replaceAll(", ", "','")+"')"); 
					if(keywords.length()>0)
						sql.append(getKeywordsWhereStr());
				}
			}
			return sql.substring(7);
			
	    }else if("staff".equals(querytype)){//下属员工
	    	UserView uv = getUserView(prea0100.substring(0, 3), prea0100.substring(3));
			if(uv!=null){
				StringBuffer sqlstr = new StringBuffer(uv.getPrivSQLExpression("###", false));
				if(keywords.length()>0)
					sqlstr.append(getKeywordsWhereStr());
				return sqlstr.toString();
			}
	    	
	    }else if("staffadd".equals(querytype)){//最近入职
	    	// 从模板中获取最近入职查询条件
			HashMap map = HTMLParamUtils.getStaffaddMap(conn);
			String sql = "";
			if(map != null){
				int gwhereID = Integer.parseInt((String) map.get("id"));
		    	//最近离职的ID
		    	String dbaseIDLibrary = mpre;//获取的人员库
		    	// 通过这个ID查询，获得条件语句
		    	sql = this.getSQLWithGwhereID(gwhereID,dbaseIDLibrary);
			}
	    	return sql;
	    }else if("staffminus".equals(querytype)){//最近离职
	    	// 从模板中获取查询条件
			HashMap map = HTMLParamUtils.getStaffminusMap(conn);
			String sql = "";
			if(map != null){
				// 最近离职的ID
				int gwhereID = Integer.parseInt((String) map.get("id"));
		    	String dbaseIDLibrary = mpre;//获取的人员库
		    	sql = this.getSQLWithGwhereID(gwhereID,dbaseIDLibrary);
			}
	    	return sql;    	
	    }
		return null;
	}
	
	/**
	 * 
	 * @Title: getSQLWithGwhereID
	 * @Description: 根据 gwhereID的值获得显示员工的条件SQl语句
	 * @param gwhereID
	 * @return
	 * @return String
	 * @throws
	 */
	private String getSQLWithGwhereID(int gwhereID, String dbaseIDLibrary) {
		StringBuffer resultSql = new StringBuffer();
		try {
			String dbpre = prea0100.substring(0, 3);
			String a0100 = prea0100.substring(3);
			// 获得该人员的UserView对象
			UserView uView = this.getUserView(dbpre, a0100);
			// 初始查询条件
			String conditions = this.getFindConditions(gwhereID);
			List list = this.getConditionsList(uView, conditions,
					dbaseIDLibrary);
			if (list.size() > 0) {
				String result = "";
				String resultPartition = "";
				boolean flag = true;
				for (int i = 0, n = list.size(); i < n; i++) {
					HashMap hashMap = (HashMap) list.get(i);
					dbpre = (String) hashMap.get("dbpre");
					result = (String) hashMap.get("result");
					resultPartition = result;
					// 11个字符为1个字段，每450个字段查询一次
					while (flag) {
						if (resultPartition.length() > 11 * 450) {
							result = resultPartition.substring(0, 11 * 450);
							resultPartition = resultPartition
									.substring(11 * 450);
						} else {
							result = resultPartition;
							flag = false;
						}
						result = result.substring(0, result.length() - 1);
						resultSql.append("union all ");
						resultSql.append(" select distinct " + dbpre + "a01.a0100,'" + (i + 1) + "' ord,'" + dbpre + "' dbpre," + dbpre + "a01.b0110," + dbpre
								+ "a01.e01a1," + dbpre + "a01.e0122,a0101,a0000 from ");
						resultSql.append((String) hashMap.get("dbpre"));
						resultSql.append("A01 where A0100 in (");
						resultSql.append(result);
						resultSql.append(") ");
						if(keywords.length()>0)
							resultSql.append(getKeywordsWhereStr());
					}
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 去掉最开始的union all
		return resultSql.toString().substring(10);
	}
	
	/**
	 * 
	 * @Title: getFindConditions
	 * @Description:根据ID从gwhere表中获得条件查询原始语句
	 * @param gwhereID id
	 * @return String
	 * @throws
	 */
	private String getFindConditions(int gwhereID) {
		RowSet rs = null;
		String conditions = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer strsql = new StringBuffer();
			// 查询语句
			strsql.append("select id,name,lexpr from gwhere where id = ");
			strsql.append(gwhereID);
			rs = dao.search(strsql.toString());
			if (rs.next())
				conditions = rs.getString("lexpr");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return conditions;
	}
		
	/**
	 * 
	 * @Title: getUserView
	 * @Description: 根据库前缀和人员ID，获得 UserView
	 * @param dbpre
	 * @param a0100
	 * @return UserView
	 * @throws
	 */
	private UserView getUserView(String dbpre, String a0100) {
		RowSet rs = null;
		UserView uView = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			DbNameBo dbNameBo = new DbNameBo(conn, this.userView);
			//获取设置的用户名登陆字段
			String username = dbNameBo.getLogonUserNameField();
			StringBuffer strsql = new StringBuffer();
			// 查询用户登陆名
			strsql.append("select " + username + " from " + dbpre
					+ "A01 where a0100='" + a0100 + "'");
			rs = dao.search(strsql.toString());
			if (rs.next()) {
				String logonname = rs.getString(username);
				// 构造用户userView
				if (logonname != null && logonname.length() > 0) {
					uView = new UserView(logonname, conn);
					// 登陆
					uView.canLogin();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return uView;
	}
		
	/**
	 * 获取主集简介信息
	 * @return
	 */
	public List getSelfInfo(String dbpre,String a0100){
		List personlist = new ArrayList();
		try {
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select a0100,'" +dbpre+"' dbpre,b0110,e01a1,e0122,a0101,a0000 from "+dbpre+"A01 where a0100='"+a0100+"'");
			personlist = this.getPersonList(sqlstr.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return personlist;
	}

		
	public Map getLeaderPreA0100s(String dbpre,String a0100){
		RowSet rs = null;
		Map prea0100Map = new HashMap();
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer strsql = new StringBuffer();
			strsql
			.append("select tm.mainbody_id mainbody_id,tm.a0101 a0101 from t_wf_relation tr  left join t_wf_mainbody tm on tr.Relation_id=tm.Relation_id where tr.default_line=1 and upper(tm.object_id)='"
					+ (dbpre+a0100).toUpperCase() + "' and tm.sp_grade=9");
			rs = dao.search(strsql.toString());
			while(rs.next()){
				String mainbody_id = rs.getString("mainbody_id");
				String pre = mainbody_id.substring(0,3);
				String id = mainbody_id.substring(3);
				if(prea0100Map.containsKey(pre)){
					List a0100s = (List)prea0100Map.get(pre);
					a0100s.add(id);
				}else {
					List a0100s = new ArrayList();
					a0100s.add(id);
					prea0100Map.put(pre, a0100s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return prea0100Map;
	}
}
