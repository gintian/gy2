package com.hjsj.hrms.businessobject.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalarySetItemBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * <p>
 * Title:HmusterBo
 * </p>
 * <p>
 * Description:对高级花名册的一些操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2006-3-18:12:00:55
 * </p>
 * 
 * @author dengcan
 * @version 1.0
 * 
 */
public class HmusterBo {
	private Connection conn = null;

	private boolean fieldIsExist = true;

	public String modelFlag = "";

	private ArrayList fieldsList = new ArrayList();

	private String privConditionStr = " ";

	private UserView userView = null;

	private String salaryid = "";

	private String groupPointItem = "";
	private String sortitem="";
	private String layerid="";
	/** 0不汇总,1按人员/单位/职位汇总 */
	private String countflag="";
	private String temptable="";
	/**从人事异动进入的限制语句*/
	private String sql_str="";
	private String analyseTableName;
	/**不按管理范围来取数*/
	private String no_manger_priv="";
	/**第二分组指标信息*/
	private String groupPoint2="";
	private String layerid2="";
	private String isGroupPoint2="0";
	private String groupOrgCodeSet="";
	private String groupOrgCodeSet2="";
	private String kqtable="";//考勤历史数据（考勤归档表）
	private HashMap kqMap;//考勤专用，为了取考勤人员库 只取UN的
	private String kqTableId="";  // 5: Q05, ...
	private String personView="V_EMP_";
	private HashMap personViewName = new HashMap();
	private String indexColumn="";//创建索引的列
	private boolean isPersonView=false;//是否用了人员视图
	private String queryField="";//作为查询条件的子集
	private String selectedPoint="";
	private String flag="";//"0":无  "1"有子集指标无年月标识,可按最后一条历史纪录查  "2"有子集指标无年月标识,可按取部分历史纪录查   "3"有子集指标和年月标识，可按某次的历史纪录查//4:按某年某次取
	/** 只取工资审批结束数据 */
	private boolean onlyGzSpFinished=true;
	/** 模板临时表，包含涉及到的变化后单位或部门下的全部人员 */
	private String tmplTempTableName=null;
	
	/** 用于全部人员库排序 select sql */
	private String curInsertFlds;  // 含 insert
	private String curMusterFlds;  // 仅 select 中字段名
    private String curFlds;  // 含 select
    private String curFrom;  // 含 from, where 
    private String curGroupBy;  // 含 group by
	private String curOrderBy;  // 含 order by 
	private HashMap cellGridNoMap=new HashMap();//查询全部人员时  所有指标以C或D定义虚拟列map
	
	public void setFlag(String flag)
	{
		this.flag=flag;
	}
	public HashMap getKqMap() {
		return kqMap;
	}
	public void setKqMap(HashMap kqMap) {
		this.kqMap = kqMap;
	}
	public HmusterBo() {
	}
	public HmusterBo(Connection conn) {
		this.conn = conn;
	}
	public HmusterBo(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	public String getAnalyseTabNameStr()
	{
		if(this.getAnalyseTableName()!=null&&!"".equals(this.getAnalyseTableName().trim())) {
            return this.getAnalyseTableName();
        } else {
            return "salaryhistory";
        }
	}

	/**
	 * 工资自定义表/工资分析按单位/部门排序时的 select 语句，用于 from 子句
	 * @param gzTableName
	 * @param alias 别名,null 表示没有
	 * @return
	 */
    public String getGzTableSortView(String gzTableName, String alias) {
        String s="";
        if(alias == null) {
            s = gzTableName;
        } else {
            s = gzTableName + " " + alias;
        }
        if(hasOrgSortItem()) {
            String select = gzTableName+".*";
            String from = gzTableName;
            sortitem=sortitem!=null?sortitem:"";
            String arr[] = sortitem.split("`");
            for(int i=0;i<arr.length;i++){
                if(arr[i]!=null&&arr[i].trim().length()>0){
                    String[] itemarr = arr[i].split(":");
                    if(itemarr!=null&&itemarr.length==3){
                        FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
                        if(item!=null){
                            if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
                                    "@K".equalsIgnoreCase(item.getCodesetid())) {
                                String orgAlias = "org"+i;
                                select += ","+orgAlias+".A0000 as org" + item.getItemid();
                                from += " left join organization "+orgAlias+
                                    " on " + gzTableName + "."+item.getItemid() + "="+orgAlias+".codeitemid";
                            }
                        }   
                    }
                }
            }
            s = "(select "+select + " from " + from + ") ";
            if(alias == null) {
                s += gzTableName;
            } else {
                s += alias;
            }
        }
        return s;
    }
    
	/**
	 * 判断库中该表是否已有记录 0:没有 1:有
	 */
	public String isTable(String tempTableName) {
		String flag = "0";
		if(tempTableName==null|| "".equals(tempTableName)) {
            flag="0";
        } else
		{
	    	DbWizard dbWizard = new DbWizard(this.conn);
	       	Table table = new Table(tempTableName);
	     	if (dbWizard.isExistTable(table.getName(), false)) {
                flag = "1";
            }
		}
		return flag;

	}
	
    private boolean isExistField(String table_name, String field_name){
        boolean bflag = false;
        RowSet rs = null;
        try{
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ");
            strsql.append(field_name);
            strsql.append(" from ");
            strsql.append(table_name);
            strsql.append(" where 1=2");
            ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(strsql.toString());
            bflag = true;
        }catch(Exception ex){
            try
            {
                if(rs != null) {
                    rs.close();
                }
            }
            catch(Exception exx) { }
        }
        return bflag;
    }	

	public static void main(String[] arg) {
		String fd = "adfadsz0";
		System.out.println(fd.substring(fd.length() - 2));
	}

	/**
	 * 得到可按部分历史纪录查询所选的子标集列表
	 * 
	 * @param tabID
	 *            高级花名册id
	 * @author dengc
	 * @return ArrayList
	 */

	public ArrayList getSubClassList(String tabID) throws GeneralException {
		ArrayList arrayList = new ArrayList();

		String sql1 = "select SetName,Field_Name,Field_Hz,CodeId ,Field_Type from muster_cell where ( "
				+ isNotNull("CodeId") + " ) and   Tabid=" + tabID;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		try {
			recset = dao.search(sql1);
			boolean is_timeChange = false;
			HashSet hash = new HashSet();
			while (recset.next()) {
				String setName = recset.getString("SetName");
				String field_name = recset.getString("Field_Name");
				String field_hz = recset.getString("Field_Hz");
				String codeid = recset.getString("CodeId");
				String field_type = recset.getString("Field_Type");
				/**计算结果没有子集先去掉，2010/01/08*/
                if(setName==null) {
                    continue;
                }
                /**考勤中，q05，q07，q09与q03表一样*/
                FieldSet fieldSet = DataDictionary.getFieldSetVo(setName.toLowerCase());
                if("q05".equalsIgnoreCase(setName)|| "q09".equalsIgnoreCase(setName)|| "q07".equalsIgnoreCase(setName))
                {
                	fieldSet = DataDictionary.getFieldSetVo("Q03".toLowerCase());
                }
				if (fieldSet == null) {
                    continue;
                }
				if("3".equals(this.modelFlag)&&setName!=null&&(setName.startsWith("K")||setName.startsWith("B")))
				{
					continue;
				}
				if (!"0".equals(fieldSet.getChangeflag())) {
                    is_timeChange = true;
                }
				if (field_name == null	|| field_name.trim().length() == 0|| field_hz.equalsIgnoreCase(ResourceFactory.getProperty("hmuster.label.nybs"))) {
                    continue;
                }
				if (!"A01".equals(setName) && !"B01".equals(setName)&& !"K01".equals(setName) && setName != null&& !"".equals(setName)) {
					if (field_name != null && !"".equals(field_name)) {
						CommonData dataobj = new CommonData(field_name + "/"+ codeid + "/" + field_type + "/" + setName,field_hz);
						hash.add(setName);
						arrayList.add(dataobj);
					}
				}
			}

			if (is_timeChange) {
				CommonData dataobj = new CommonData("Z0/0/D/all",
						ResourceFactory.getProperty("hmuster.label.nybs"));
				//arrayList.add(dataobj);
				arrayList.add(0, dataobj);//bug号： 38462 将年月标识指标默认放到第一位 
			}
			if(hash.size()>0){
				Iterator ihash = hash.iterator();
	            while(ihash.hasNext()){
	            	String formu = (String)ihash.next();
	            	if(formu!=null&&formu.trim().length()>0){
	            		String itemdesc="";
	            		if("q05".equalsIgnoreCase(formu)|| "q07".equalsIgnoreCase(formu)|| "q09".equalsIgnoreCase(formu))
	            		{
	            			if("q05".equalsIgnoreCase(formu)) {
                                itemdesc="员工出勤月汇总表";
                            }
	            			if("q07".equalsIgnoreCase(formu)) {
                                itemdesc="部门出勤日明细表";
                            }
	            			if("q09".equalsIgnoreCase(formu)) {
                                itemdesc="部门出勤月汇总表";
                            }
	            		}
	            		else
	            		{
	            			FieldSet fieldset = DataDictionary.getFieldSetVo(formu);
	            			itemdesc=fieldset.getCustomdesc();
	            		}
	            		
	            		CommonData dataobj = new CommonData(formu,itemdesc+"条件...");
	            		arrayList.add(dataobj);
	            	}
	             }
			}
			/*if (arrayList.size() < 1) {
				CommonData dataobj = new CommonData("", "");
				arrayList.add(dataobj);
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return arrayList;
	}

	/**
	 * 判断高级花名册指标中是否有子集指标及子集指标中是否有年月标识
	 * 
	 * @param tabID
	 *            高级花名册id
	 * @author dengc
	 * @return String "0":无 "1"有子集指标无年月标识,可按最后一条历史纪录查 "2"有子集指标无年月标识,可按取部分历史纪录查
	 *         "3"有子集指标和年月标识，可按某次的历史纪录查
	 */
	public String isSubClass(String tabID) throws GeneralException {
		String result = "0";
		String sql1 = "select SetName,field_name,field_hz from muster_cell where Tabid="+ tabID+" order by gridno";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		try {
			recset = dao.search(sql1);
			int i = 0;
			while (recset.next()) {
				String setName = recset.getString("SetName");
				String field_name = recset.getString("field_name");
				if("3".equals(this.modelFlag)&&setName!=null&&(setName.startsWith("K")||setName.startsWith("B")))
				{
					continue;
				}
				if (setName != null && !"A01".equals(setName)&& !"B01".equals(setName) && !"K01".equals(setName)&& !"".equals(setName)) {
					if (field_name != null && !"".equals(field_name)) {
                        i++;
                    }
					String sql2 = "select itemid from fielditem where itemdesc like '"+ ResourceFactory.getProperty("hmuster.label.nybs")+ "' and fieldsetid='" + setName + "'  ";
					RowSet rowSet = dao.search(sql2);
					FieldSet fieldSet = DataDictionary.getFieldSetVo(setName.toLowerCase());
					if (rowSet.next()||(fieldSet!=null&&!"0".equals(fieldSet.getChangeflag()))) {
						if("2".equals(fieldSet.getChangeflag())) {
                            result="3";
                        } else {
                            result = "4";
                        }
						break;
					} else {
						result = "2";
					}

				}
			}
			// 2006-12-12 改：高级花名册，按月变化子集，高级中无法选取某次历史记录
			/*
			 * if (result.equals("2") || result.equals("3")) { if (i == 0)
			 * result = "1"; }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 取得高级花名册中分组指标列表 ( 主集里的代码型指标;对人员信息，单位，职位是硬编码;对单位，B0110硬编码;对职位，E01A1硬编码)
	 * 
	 * @param inforkind
	 * @author dengc
	 * @return ArrayList created: 2006/03/21
	 */

	public ArrayList getHmusterGroupPointList(String inforkind)
			throws GeneralException {

		ArrayList arrayList = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		ArrayList pointList = new ArrayList(); // 指标列表
		String mainSet = ""; // 主集
		if ("1".equals(inforkind)) // 人员库
		{
			mainSet = "A01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory.getProperty("tree.kkroot.kkdesc"));
			arrayList.add(dataobj2);
		} else if ("3".equals(inforkind)) // 职位库
		{
			mainSet = "K01";
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory.getProperty("tree.kkroot.kkdesc"));
			//
			CommonData dataobj = new CommonData("E0122", ResourceFactory.getProperty("column.sys.dept"));

			arrayList.add(dataobj2);
			arrayList.add(dataobj);

		} else if ("2".equals(inforkind)) // 单位库
		{
			mainSet = "B01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
		}

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		try {
			strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
			strsql.append(mainSet);
			strsql.append("' and codesetid!='0'and useflag='1' order by  displayid ");
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				CommonData dataobj = new CommonData(recset.getString("itemid"),recset.getString("itemdesc"));
				arrayList.add(dataobj);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return arrayList;

	}

	/**
	 * 得到考勤的sql语句
	 * 
	 * @param tabid
	 *            表id
	 * @param tableName
	 *            表名
	 * @param condition
	 *            条件
	 * @return
	 */
	public String getKQSql(String tabid, String tableName, String condition,
			HashMap cFactorMap, ArrayList mustCellList, String pre) {
	    /* cond[0]: 考勤表名中的编号
	       cond[1]: 考勤表条件
	       cond[2]: 工号、卡号或查询条件(以 and 开头), cond[2]可能没有
	       结果sql:
	       insert into xxx_muster_xxx(...) 
           select ... 
           from (select ... from Qxx inner join 
                  (SELECT A0100,'Usr' nbase,E0127,C01TC FROM UsrA01 WHERE ...) b on Qxx.a0100=b.a0100 cond[2] 
                 where UPPER(Qxx.nbase)='USR' ) B  
           where 1=1 and (cond[1]) 
 
	     */
		String[] cond = condition.split("`");
		KqParameter para = new KqParameter(this.userView, "", conn);
		HashMap hashmap = para.getKqParamterMap();
		String g_no = (String) hashmap.get("g_no");//工号
		String cardno = (String) hashmap.get("cardno");//考勤卡号
		String field = KqParam.getInstance().getKqDepartment();//考勤部门
        String ischecker = RegisterInitInfoData.ischecker(userView.getDbname(), userView.getA0100(), this.conn);
        String kind = null;
        kind=RegisterInitInfoData.getKindValue(kind,this.userView);
        String expres = userView.getPrivExpression();
        String whereIN = null;
		ArrayList kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getKqMap(),this.userView,this.conn,"UN");
		StringBuffer kq_sql_insert = new StringBuffer("insert into "+ tableName + "(recidx,A0100,B0110,E0122,E01A1,nbase");
		StringBuffer kq_sql_select = new StringBuffer("select ");
		 if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
	     {
	        kq_sql_select.append("1,");
	     }
		 String tablename="";
			if(this.getKqtable()!=null&&this.getKqtable().length()>0)
			{
				//kq_sql_select.append(this.getKqtable());
				tablename=this.getKqtable();
			}else{
				int a_tabid = Integer.parseInt(cond[0]);
	    		if (a_tabid < 10 && a_tabid > 0) {
	    			//kq_sql_select.append("Q0" + cond[0]);
	    			tablename="Q0" + cond[0];
	    		} else {
	    			//kq_sql_select.append("Q" + cond[0]);
		    		tablename="Q" + cond[0];
		    	}
			}
		if (tablename.toUpperCase().indexOf("09")!=-1||tablename.toUpperCase().indexOf("07")!=-1|| "9".equals(cond[0]) || "7".equals(cond[0])) {
			kq_sql_select.append(" '' A0100 ,B0110,'' e0122,'' e01a1 ,'' nbase");
		} else {
			kq_sql_select.append(" A0100,B0110,E0122,E01A1,nbase");
		}
		// 主键
		if(getKqKeyFieldName().length() > 0) {
		    kq_sql_insert.append(","+getKqKeyFieldName());
		    kq_sql_select.append(","+getKqKeyFieldName());
		}
		if (cFactorMap.get("groupN") != null) {
			kq_sql_insert.append(",groupN,groupV");
			kq_sql_select.append("," + (String) cFactorMap.get("groupN") + " as groupN,"+ (String) cFactorMap.get("groupN")+" as groupV");
		}
		if (cFactorMap.get("groupN2") != null) {
			kq_sql_insert.append(",groupN2,groupV2");
			kq_sql_select.append("," + (String) cFactorMap.get("groupN2") + " as groupN2,"+ (String) cFactorMap.get("groupN2")+" as groupV2");
		} 
		ArrayList list = getSubSql(mustCellList);
		kq_sql_insert.append((String) list.get(0));
		kq_sql_select.append((String) list.get(1));

		kq_sql_insert.append(" ) ");
		kq_sql_select.append(" from ");
		/**考勤中，q05，q07，q09与q03表一样
		 * 有班次的表,要特殊处理
		 * Q11--->Q1104
		 * Q13--->Q1304
		 * Q15--->q1504
		 * Q23--->Q23Z7
		 * Q25--->Q25Z7
		 * Q27--->Q2703
		 * */
		
		
		if(("Q03".equalsIgnoreCase(tablename)|| "Q03_arc".equalsIgnoreCase(tablename)|| "q05".equalsIgnoreCase(tablename)|| "q05_arc".equalsIgnoreCase(tablename)
				|| "Q11".equalsIgnoreCase(tablename)|| "Q11_arc".equalsIgnoreCase(tablename)|| "Q13".equalsIgnoreCase(tablename)|| "q13_arc".equalsIgnoreCase(tablename)
				|| "q15".equalsIgnoreCase(tablename)|| "q15_arc".equalsIgnoreCase(tablename)|| "q17".equalsIgnoreCase(tablename)|| "q19".equalsIgnoreCase(tablename)
				|| "q21".equalsIgnoreCase(tablename)|| "q23".equalsIgnoreCase(tablename)|| "q25".equalsIgnoreCase(tablename)|| "q27".equalsIgnoreCase(tablename)
				|| "q31".equalsIgnoreCase(tablename))&&kq_dbase_list.size()>0){
			StringBuffer temp = new StringBuffer();
			RowSet rs = null;
			StringBuffer columnBuf = new StringBuffer();
			try
			{
		    	ContentDAO dao = new ContentDAO(this.conn);
		    	rs = dao.search("select * from "+tablename+" where 1=2 ");
		    	ResultSetMetaData rsmd = rs.getMetaData();
		    	for(int i=1;i<=rsmd.getColumnCount();i++)
		    	{
		    		String columnName=rsmd.getColumnName(i).toLowerCase();
			    	if(!columnName.equalsIgnoreCase(g_no)&&!columnName.equalsIgnoreCase(cardno))
			    	{
			    		columnBuf.append(tablename+"."+columnName+",");
			    	}
			    		
		    	}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try
				{
					if(rs!=null) {
                        rs.close();
                    }
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			int j=0;
			String str = "";
	        if("1".equals(kind))
	        {
	            str = "E0122";
	        }else if("0".equals(kind))
	        {
	            str = "E01A1";  
	        }else
	        {
	            str = "B0110";      
	        }
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String nbase=(String)kq_dbase_list.get(i);
				// 34102  考勤高级花名册切换人员库时没有增加选中的库条件
				if(!"ALL".equalsIgnoreCase(pre) && StringUtils.isNotEmpty(pre)) {
					if(!pre.equalsIgnoreCase(nbase)) {
                        continue;
                    }
				}
//				if(expres.length() > 0){//wangcq 2014-12-06 根据权限,生成select.IN中的查询串 
//                    try
//                    {
//                        whereIN = userView.getPrivSQLExpression(expres,nbase,false,new ArrayList());
//                    }
//                    catch (GeneralException e)
//                    {
//                        e.printStackTrace();
//                    }
                    
//                }else{
				//if (cond.length>=3&&StringUtils.isEmpty(cond[cond.length-1]))					
				whereIN = RegisterInitInfoData.getWhereINSql(userView,nbase);
			//else 					
				//whereIN = " from " +nbase+ "A01 ";
                    
//                }
				temp.append(" union all ");
				temp.append(" select "+columnBuf.toString()+"b."+g_no+",b."+cardno+" ");
				if(!(field == null || "".equals(field))) {
                    temp.append(","+field+" ");
                }
				temp.append(" from "+tablename+" inner join (");
		        if("1".equals(ischecker)){//加上考勤员、考勤部门的判断
                    if("".equals(field) || field == null){
                        temp.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + " from " + nbase + "A01");
                    }else{
                        temp.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + "," +field + " from " + nbase + "A01");
                    }
                }else{
                    if("".equals(field) || field == null){
                        temp.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + whereIN);
                    }else{
                        temp.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + "," +field + whereIN);
                    }
                }
                if (field != null && field.length() > 0) {
                    if("1".equals(ischecker)){
                        temp.append(" union SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + "," + field + " from " + nbase + "A01");
                    }else{
                        temp.append(" union SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + "," +field +whereIN.replaceAll(str, field));
                    }
                }
                temp.append(" ) b");       
                String oncond="";
                if (cond.length > 2 && cond[2] != null && cond[2].trim().length() > 0 && !"1".equals(cond[2])) {
                    oncond = cond[2];
                }
				temp.append(" on "+tablename+".a0100=b.a0100 "+oncond+" where ");
				temp.append(" UPPER("+tablename+".nbase)='"+nbase.toUpperCase()+"' ");
				if(this.getKqconditionSQL()!=null&&!"".equals(this.getKqconditionSQL())) {
                    temp.append(" and ("+this.getKqconditionSQL()+")");
                }
			}
			kq_sql_select.append(" ("+temp.toString().substring(11)+") B ");
			
		}else{
			if ("q09".equalsIgnoreCase(tablename)) {
				String str = kq_sql_select.toString().replace("State", "B.State");
				kq_sql_select.setLength(0);
				kq_sql_select.append(str);
			}
			String kqOrgView = tablename;
            if(this.getKqconditionSQL()!=null&&!"".equals(this.getKqconditionSQL())) {
                kqOrgView = "(select * from " + tablename + " where " + this.getKqconditionSQL() + ")";
            }
	    	kq_sql_select.append(kqOrgView+" B");
		}
		if ("9".equals(cond[0]) || "7".equals(cond[0])) {
			kq_sql_select.append(" left join organization on B.b0110=organization.codeitemid ");
		}
		kq_sql_select.append(" where 1=1 ");
		if (cond.length > 1 && cond[1] != null && cond[1].trim().length() > 0) {
			kq_sql_select.append(" and (" + cond[1]+")");
		}

		String orderby="";//"order by b0110,e0122,e01a1,a0100";
		if ("9".equals(cond[0]) || "7".equals(cond[0])) {
			orderby=" order by a0000 ";
		} else if("5".equals(cond[0])) { //zxj 20161209 与考勤月汇总页面默认排序保持一致，暂不考虑自定义排序  jazz24170
            orderby = " order by dbid,a0000,q03z0";
        }else if("3".equals(cond[0])) {//日明细数据 添加日期排序
        	orderby=" order by b0110,e0122,e01a1,a0100,q03z0 ";
        }
		else
		{
			orderby=" order by b0110,e0122,e01a1,a0100 ";
		}
		if(getSortStr(tablename)!=null&&!"".equals(getSortStr(tablename))) {
            orderby=getSortStr2(tablename);
        }
		kq_sql_select.append(orderby);
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
		{
			kq_sql_insert.append(kq_sql_select.toString());
		}
		else
		{
			kq_sql_insert.append(" select RowNum,a.* from ("+kq_sql_select.toString()+") a");
		}
		
		
		
		
		return kq_sql_insert.toString();
	}

	public ArrayList getSubSql(ArrayList mustCellList) {
		
		return getSubSql1(mustCellList, "");//liuy 2015-8-6 11639：模板花名册前台出不来数据
	}
	
	/**
	 * 日期型，数据库里面为大文本是特殊处理
	 * @param mustCellList
	 * @param tmplTabName
	 * @return
	 */
	public ArrayList getSubSql1(ArrayList mustCellList,String tmplTabName) {
		ArrayList list = new ArrayList();
		StringBuffer kq_sql_insert = new StringBuffer("");
		StringBuffer kq_sql_select = new StringBuffer("");
		StringBuffer kq_sql_insert_update = new StringBuffer("");
		StringBuffer kq_sql_select_update = new StringBuffer("");
		for (Iterator t = mustCellList.iterator(); t.hasNext();) {
			DynaBean a_dynaBean = (DynaBean) t.next();
			String flag = (String) a_dynaBean.get("flag");
			if (!"G".equals(flag) && !"R".equals(flag) && !"S".equals(flag)&& !"P".equals(flag) && !"H".equals(flag)) {
				String a_fieldName = (String) a_dynaBean.get("field_name");
				if("5".equals(this.modelFlag)&&a_fieldName!=null&&!"".equals(a_fieldName))
				{
	    			if(cloumnMap==null||cloumnMap.get(a_fieldName.toLowerCase())==null) {
                        continue;
                    }
				}
				if (a_fieldName == null || a_fieldName.trim().length() == 0) {
                    continue;
                }
				kq_sql_insert.append(",C" + (String) a_dynaBean.get("gridno"));
				if (!"D".equals((String) a_dynaBean.get("field_type"))){
					kq_sql_select.append("," + a_fieldName+" as C"+(String) a_dynaBean.get("gridno"));
					kq_sql_insert_update.append(",C"+(String) a_dynaBean.get("gridno"));
					kq_sql_select_update.append("," + a_fieldName);
				}
				else {
					String[] field = new String[4];
					field[0] = a_fieldName;
					field[1] = "D";
					field[2] = (String) a_dynaBean.get("slope");
					field[3] = (String) a_dynaBean.get("codeid");
					String tt = "";
					
					int type = 0;
					if(StringUtils.isNotEmpty(tmplTabName)) {
                        type = getTableColumnType(tmplTabName, a_fieldName);
                    }
					if(java.sql.Types.CLOB == type ||java.sql.Types.LONGVARCHAR == type){
						if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            tt = " REPLACE("+ a_fieldName +",'-','.') ";
                        } else {
                            tt = " REPLACE(cast("+ a_fieldName +" as nvarchar),'-','.') ";
                        }
					}else {						
						tt = getField(field);
					}
				    if(tt.toUpperCase().indexOf(" AS")==-1|| "NBASE".equalsIgnoreCase(tt)) {
                        kq_sql_select.append("," + tt+" as C"+(String) a_dynaBean.get("gridno"));
                    } else {
                        kq_sql_select.append(","+tt);
                    }
				}
			}
		}
		list.add(kq_sql_insert.toString());
		list.add(kq_sql_select.toString());
		list.add(kq_sql_insert_update.toString());
		list.add(kq_sql_select_update.toString());
		return list;
	}
	/**
	 * 根据表名、列名得到列类型
	 * @param tmplTabName  表名
	 * @param columnName  列名
	 * @return
	 */
	private int getTableColumnType(String tmplTabName,String columnName){
		int type = 0;
		String sql ="select * from " + tmplTabName + " where 1=2";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {						
			rs = dao.search(sql);
			ResultSetMetaData data = rs.getMetaData();
			for(int i=1; i<=data.getColumnCount(); i++){
				if(columnName.equalsIgnoreCase((String)data.getColumnName(i).toUpperCase())){
					type = data.getColumnType(i);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (rs!=null) {
				PubFunc.closeResource(rs);
			}
		}
		return type;
	}
	
	/**
	 * 得到工资的sql语句<br>
	 * 模板数据表：userName + "Templet_" + relatTableid
	 * 
	 * @param tabid
	 *            表id
	 * @param tableName
	 *            表名
	 * @param condition
	 *            条件
	 * @return
	 */
	public String getGZSql(String tabid, String tableName, String condition,
			HashMap cFactorMap, ArrayList mustCellList, String relatTableid,
			String userName) {
        String[] cond = condition.split("`");
	    String tmplcond = "";
        if (cond.length > 1 && cond[1] != null && cond[1].trim().length() > 0) {
            tmplcond = cond[1];
        }
        else {
            tmplcond = "1=1";
        }
        if("5".equals(this.modelFlag))
        {
            if(this.getSql_str().trim().length()>0&&this.getSql_str().indexOf("where")>-1)
            {
                tmplcond += " and ("+this.getSql_str().substring(this.getSql_str().indexOf("where")+5, this.getSql_str().indexOf("order"))+")";
            }
        }

	    String tmplTabName = userName + "templet_" + relatTableid;
	    if("True".equals(cFactorMap.get("ShowChangedOrgPersons"))) {
	        prepareTmplTempTable(cFactorMap, Integer.parseInt(relatTableid), tmplTabName, tmplcond);
            tmplcond = "1=1";
	        tmplTabName = tmplTempTableName;
	    }
		StringBuffer gz_sql_insert = new StringBuffer("insert into "+ tableName + "(recidx,A0000,A0100,NBASE");
		StringBuffer gz_sql_select = new StringBuffer("select ");
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
		{
			gz_sql_select.append("1,");
		}
		gz_sql_select.append(" A0000,A0100,BasePre");
		if (cFactorMap.get("groupN") != null) {
			gz_sql_insert.append(",groupN,groupV");
			String groupN = (String) cFactorMap.get("groupN");
			String groupV=groupN;
			if (!isGroupPoint(relatTableid, groupN)) {
				groupN = "null as groupN" ;//+ groupN;
				groupV=" null as groupV";
			}
			else
			{
				groupN=groupN+" as groupN";
				groupV=groupV+" as groupV";
			}
			gz_sql_select.append("," + groupN + "," + groupV);
		}
		if (cFactorMap.get("groupN2") != null) {
			gz_sql_insert.append(",groupN2,groupV2");
			String groupN2 = (String) cFactorMap.get("groupN2");
			String groupV2=groupN2;
			if (!isGroupPoint(relatTableid, groupN2)) {
				groupN2 = "null as groupN" ;//+ groupN;
				groupV2=" null as groupV";
			}
			else
			{
				groupN2=groupN2+" as groupN";
				groupV2=groupV2+" as groupV";
			}
			gz_sql_select.append("," + groupN2 + "," + groupV2);
		}
		ArrayList list = getSubSql1(mustCellList, tmplTabName);
		gz_sql_insert.append((String) list.get(0));
		gz_sql_select.append((String) list.get(1));

		gz_sql_insert.append(" ) ");
		
		gz_sql_select.append(" from ");
		gz_sql_select.append(tmplTabName);
		gz_sql_select.append(" where " + tmplcond);
		String orderby = getSortStr(tmplTabName);
		if(orderby==null|| "".equals(orderby)) {
            orderby=" order by a0000 ";
        }
		gz_sql_select.append(orderby);
		
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
		{
			gz_sql_insert.append(gz_sql_select.toString());
		}
		else
		{
			gz_sql_insert.append(" select RowNum,a.* from ("+gz_sql_select+") a");
		}
		return gz_sql_insert.toString();
	}
	public String  getSortStrYd(String tableName,String relatTableid){
		StringBuffer fieldsetStr= new StringBuffer();
		sortitem=sortitem!=null?sortitem:"";
		if(sortitem.length()>1) {
            fieldsetStr.append(" order by ");
        }
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					fieldsetStr.append( tableName+ "Templet_" + relatTableid);
					fieldsetStr.append(".");
					fieldsetStr.append(itemarr[0]);
					fieldsetStr.append(" ");
					if("0".equals(itemarr[2])) {
                        fieldsetStr.append("DESC,");
                    } else {
                        fieldsetStr.append("ASC,");
                    }
				}
			}
		}
		if(fieldsetStr.length()>1) {
            return fieldsetStr.toString().substring(0, fieldsetStr.toString().length()-1);
        } else {
            return "";
        }
	}
	
	/**
	 * 得到排序指标
	 * @param tableName
	 * @param group_str
	 * @return
	 */
	private String  getFieldSortStr(String tableName,StringBuffer group_select){
		String str = "";
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				StringBuffer temp = new StringBuffer();
				String fldname= arr[i].substring(0,arr[i].indexOf(":"));
				FieldItem item = DataDictionary.getFieldItem(fldname.toLowerCase());
                if(!"".equals(group_select.toString())&&group_select.toString().toLowerCase().indexOf(fldname.toLowerCase())==-1){
     				if(item!=null&& "N".equalsIgnoreCase(item.getItemtype())) {
                        temp.append(",SUM("+tableName);
                    } else {
                        temp.append(",MAX("+tableName);
                    }
	                temp.append(".");
	                temp.append(fldname);
	                temp.append(") as "+fldname);
					str+=temp.toString();
                }
			}
		}
		return str;
	}
	
	/**
	 * 
	 * @param tableName
	 * @param orgOrder 关联机构代码类排序指标，是否使用机构顺序指标(org+指标编号)
	 * @return
	 */
	private String getSortStr(String tableName, boolean orgOrder){
		StringBuffer fieldsetStr= new StringBuffer();
		sortitem=sortitem!=null?sortitem:"";
		if(sortitem.length()>1) {
            fieldsetStr.append(" order by ");
        }
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					fieldsetStr.append(tableName);
					fieldsetStr.append(".");
					FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
                    boolean isOrgItem = (item!=null) && ("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
                                "@K".equalsIgnoreCase(item.getCodesetid()));
					if(isOrgItem && orgOrder) {
                        fieldsetStr.append("org"+itemarr[0]);
                    } else {
                        fieldsetStr.append(itemarr[0]);
                    }
					fieldsetStr.append(" ");
					if("0".equals(itemarr[2])) {
                        fieldsetStr.append("DESC,");
                    } else {
                        fieldsetStr.append("ASC,");
                    }
				}
			}
		}
		if(fieldsetStr.length()>1) {
            return fieldsetStr.toString().substring(0, fieldsetStr.toString().length()-1);
        } else {
		    if("5".equals(this.modelFlag)) {
		        String flds="";
		        DbWizard dbw=new DbWizard(conn);
		        if(dbw.isExistField(tableName, "NBASEID", false)) {
                    flds+=",NBASEID";
                } else if(dbw.isExistField(tableName, "basepre", false)) {
                    flds+=",basepre";
                }
                if(dbw.isExistField(tableName, "A0000", false)) {
                    flds+=",A0000";
                }
                if(flds.length()>0){
                    flds = flds.substring(1);
                    return " order by "+flds;
                }
		    }
			return "";
		}
	}

    private String getSortStr(String tableName){
       return getSortStr(tableName, false);
    }
    
	public String  getSortStr2(String tableName){
		StringBuffer fieldsetStr= new StringBuffer();
		sortitem=sortitem!=null?sortitem:"";
		if(sortitem.length()>1) {
            fieldsetStr.append(" order by ");
        }
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					fieldsetStr.append("B");
					fieldsetStr.append(".");
					fieldsetStr.append(itemarr[0]);
					fieldsetStr.append(" ");
					if("0".equals(itemarr[2])) {
                        fieldsetStr.append("DESC,");
                    } else {
                        fieldsetStr.append("ASC,");
                    }
				}
			}
		}
		if(fieldsetStr.length()>1) {
            return fieldsetStr.toString().substring(0, fieldsetStr.toString().length()-1);
        } else {
            return "";
        }
	}
	public String  getSortOrGroupSelectSQL(String tableName,String itemStr,String existItem,String type){
		StringBuffer fieldsetStr= new StringBuffer();
		try
		{
	      	/*sortitem=sortitem!=null?sortitem:"";
	    	if(sortitem.length()>1)
	    		fieldsetStr.append(" order by ");
	    	String arr[] = sortitem.split("`");
	    	for(int i=0;i<arr.length;i++){
	    		if(arr[i]!=null&&arr[i].trim().length()>0){
	    			String[] itemarr = arr[i].split(":");
		     		if(itemarr!=null&&itemarr.length==3){
			    		fieldsetStr.append(tableName);
			    		fieldsetStr.append(".");
				    	fieldsetStr.append(itemarr[0]);
				    	fieldsetStr.append(" ");
				     	if(itemarr[2].equals("0"))
				    		fieldsetStr.append("DESC,");
			    	  	else
			    			fieldsetStr.append("ASC,");	
		    		}
	    		}
	    	}*/
			itemStr=itemStr!=null?itemStr:"";
			String arr[] = itemStr.split("`");
			for(int i=0;i<arr.length;i++)
			{
				String[] itemarr = arr[i].split(":");
	     		if(itemarr!=null&&itemarr.length==3){
                    FieldItem item = DataDictionary.getFieldItem(itemarr[0].toLowerCase());
                    String fldname = itemarr[0];
                    if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
                            "@K".equalsIgnoreCase(item.getCodesetid()))) {
                        fldname = "org" + item.getItemid();
                    }

	     			if(existItem.toUpperCase().indexOf(("AS "+fldname.toUpperCase()))==-1)
	     			{
	     				if(item!=null&& "N".equalsIgnoreCase(item.getItemtype())) {
                            fieldsetStr.append("SUM("+tableName);
                        } else {
                            fieldsetStr.append("MAX("+tableName);
                        }
		    	    	fieldsetStr.append(".");
			        	fieldsetStr.append(fldname);
			        	fieldsetStr.append(") as "+fldname+",");
			        	existItem+=","+fldname;
	     			}
	    		}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(fieldsetStr.length()>1) {
            return fieldsetStr.toString().substring(0, fieldsetStr.toString().length()-1);
        } else {
            return "";
        }
	}
	public String  getGroupStr(String tableName){
		StringBuffer fieldsetStr= new StringBuffer();
		sortitem=sortitem!=null?sortitem:"";
		if(sortitem.length()<1) {
            return "";
        }
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					fieldsetStr.append(tableName);
					fieldsetStr.append(".");
					fieldsetStr.append(itemarr[0]);
					fieldsetStr.append(",");
				}
			}
		}
		if(fieldsetStr.length()>1) {
            return fieldsetStr.toString().substring(0, fieldsetStr.toString().length()-1);
        } else {
            return "";
        }
	}
	/**
	 * 取得高级花名册中分组指标列表 ( 主集里的代码型指标;对人员信息，单位，职位是硬编码;对单位，B0110硬编码;对职位，E01A1硬编码)
	 * 
	 * @param inforkind
	 * @author dengc
	 * @return ArrayList created: 2006/03/21
	 */

	public boolean isGroupPoint(String tabid, String fieldname) {
		boolean checkflag = true;

		StringBuffer buf = new StringBuffer();
		buf.append("select Field_Name,Field_hz,ChgState,codeid from Template_Set where ");
		buf.append("CodeId<>'0' and CodeId is not null and TabId='");
		buf.append(tabid);
		buf.append("'  and (Field_Name='");
		buf.append(fieldname + "' or Field_Name='"+ fieldname.substring(0, fieldname.indexOf("_"))+ "') ORDER BY nSort");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		boolean flag=true;
		try {
			rs = dao.search(buf.toString());
			if (rs.next()) {
				String codeid=rs.getString("codeid");
				if(codeid==null|| "".equals(codeid.trim()))
				{
					
				}
				else
				{
			    	String Field_Name = rs.getString("Field_Name");
			    	Field_Name = Field_Name != null ? Field_Name : "";
			    	FieldItem fielditem = DataDictionary.getFieldItem(Field_Name);
			    	if (fielditem == null) {
				    	checkflag = false;
			    	}
			    	else
			    	{
			    		flag=false;
			    	}
				}
			} 
            if(flag) {
				if (fieldname.toUpperCase().indexOf("E0122") != -1) {
                    checkflag = true;
                } else if (fieldname.toUpperCase().indexOf("B0110") != -1) {
                    checkflag = true;
                } else {
                    checkflag = false;
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return checkflag;
	}
	
	/**
	 * 模板数据表："Templet_" + relatTableid
	 * @param tabid
	 * @param tableName
	 * @param condition
	 * @param cFactorMap
	 * @param mustCellList
	 * @param relatTableid
	 * @param userName
	 * @param spflag
	 * @return
	 */

	public String getGZSql(String tabid, String tableName, String condition,
			HashMap cFactorMap, ArrayList mustCellList, String relatTableid,
			String userName, String spflag) {
        String[] cond = condition.split("`");
        String tmplcond = "";
        if (cond.length > 1 && cond[1] != null && cond[1].trim().length() > 0) {
            tmplcond = cond[1];
        }
        else {
            tmplcond = "1=1";
        }
        String tmplTabName = "templet_" + relatTableid;
        String orderSql="";
        if("5".equals(this.modelFlag))
        {
        	String sql_Str="";
        	if(this.getSql_str().startsWith(this.userView.getUserName()+"templet_" + relatTableid))//人事异动起草状态 应该查的是操作人保存未提交的数据
            {
                tmplTabName=this.userView.getUserName()+"templet_" + relatTableid;
            }
        /*		
        	}else{//人事异动报批时 查的是 "templet_" + relatTableid;这个表中的数据
        		sql_Str=this.getSql_str().toLowerCase();
        		sql_Str=sql_Str.substring(sql_Str.indexOf("where")+5);
        	}*/
        	sql_Str=this.getSql_str().toLowerCase();
        	if(sql_Str.indexOf("order")>-1){
        		orderSql=sql_Str.substring(sql_Str.indexOf("order"));
        		if(sql_Str.indexOf("1=1 and")>-1) {
                    sql_Str=sql_Str.substring(sql_Str.indexOf("1=1 and")+7, sql_Str.indexOf("order"));
                } else {
                    sql_Str=sql_Str.substring(sql_Str.indexOf("where")+5,sql_Str.indexOf("order"));
                }
        	}else{
        		sql_Str=sql_Str.substring(sql_Str.indexOf("where")+5);
        	}
        		/*{sql_Str=this.getSql_str().toLowerCase();
        		orderSql=sql_Str.substring(sql_Str.indexOf("order"));
        		sql_Str=sql_Str.substring(sql_Str.indexOf("where")+5,sql_Str.indexOf("order"));
        	}else{
        		sql_Str=sql_Str.substring(sql_Str.indexOf("where")+5);
        	}*/
            if(this.getSql_str().trim().length()>0)
            {
                tmplcond += " and ("+sql_Str+")";
                StringBuffer whereSql=new StringBuffer();
                if( this.sqlMap!=null) {
                	for(Object obj:sqlMap.keySet()) {
                		String key=obj.toString();
                		whereSql.append("or (UPPER(basepre)='"+key.toUpperCase()+"' and a0100 "+sqlMap.get(key).toString().substring(sqlMap.get(key).toString().indexOf("in"))+" )");
                	}
                	if(whereSql.length()>0) {
                		tmplcond+=" and ("+whereSql.substring(2)+")";
                	}
                }
                
            }
            
        }

	  
        if("True".equals(cFactorMap.get("ShowChangedOrgPersons"))) {
            prepareTmplTempTable(cFactorMap, Integer.parseInt(relatTableid), tmplTabName, tmplcond);
            tmplcond = "1=1";
            tmplTabName = tmplTempTableName;
        }
		StringBuffer gz_sql_insert = new StringBuffer("insert into "+ tableName + "(recidx,A0100");
		StringBuffer gz_sql_select = new StringBuffer("select ");
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
		{
			gz_sql_select.append(" 1,");
		}
		gz_sql_select.append("a0100");
		if (cFactorMap.get("groupN") != null) {
			gz_sql_insert.append(",groupN,groupV");
			gz_sql_select.append("," + (String) cFactorMap.get("groupN") + " as groupN,"+ (String) cFactorMap.get("groupN")+" as groupV");
		}
		if (cFactorMap.get("groupN2") != null) {
			gz_sql_insert.append(",groupN2,groupV2");
			gz_sql_select.append("," + (String) cFactorMap.get("groupN2") + " as groupN2,"+ (String) cFactorMap.get("groupN2")+" as groupV2");
		}
		ArrayList list =null;
		if("5".equals(this.modelFlag)) {
			list=getSubSql1(mustCellList, tmplTabName);
		}else {
			list=getSubSql(mustCellList);
		}
		gz_sql_insert.append((String) list.get(0));
		gz_sql_select.append((String) list.get(1));

		gz_sql_insert.append(" ) ");
		gz_sql_select.append(" from ");
		gz_sql_select.append(tmplTabName);
		gz_sql_select.append(" where " + tmplcond);
		if("5".equals(this.modelFlag)&&!"".equals(orderSql)&&orderSql.length()>0)//人事异动取sql 按照sql的排序规则
        {
            gz_sql_select.append(orderSql);
        } else {
            gz_sql_select.append(getSortStr(tmplTabName));
        }
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
		{
			gz_sql_insert.append(gz_sql_select);
		}
		else
		{
			gz_sql_insert.append(" select RowNum,T.* from ("+gz_sql_select+") T");
		}
		
		return gz_sql_insert.toString();
	}

	/**
	 * 根据条件取得插入高级花名册数据的sql语句
	 * 
	 * @param nFlag
	 *            标记
	 * @param tabid
	 *            高级花名册表id
	 * @param pre
	 *            应用库前缀
	 * @param condition
	 *            条件
	 * @return
	 */
	public String getSql(String nFlag, String tabid, String tableName,
			String pre, String condition, HashMap cFactorMap,
			ArrayList mustCellList, String relatTableid, String userName) {
		StringBuffer sql = new StringBuffer("");
		switch (Integer.parseInt(nFlag)) {
		case 81:
			sql.append(getKQSql(tabid, tableName, condition, cFactorMap, mustCellList, pre));
			break;
		case 5:
			sql.append(getGZSql(tabid, tableName, condition, cFactorMap,mustCellList, relatTableid, userName));
			break;

		}

		return sql.toString();
	}

	/**
	 * 根据条件取得插入高级花名册数据的sql语句
	 * 
	 * @param nFlag
	 *            标记
	 * @param tabid
	 *            高级花名册表id
	 * @param pre
	 *            应用库前缀
	 * @param condition
	 *            条件
	 * @return
	 */
	public String getSql(String nFlag, String tabid, String tableName,
			String pre, String condition, HashMap cFactorMap,
			ArrayList mustCellList, String relatTableid, String userName,
			UserView userveiw) {
		StringBuffer sql = new StringBuffer("");
		switch (Integer.parseInt(nFlag)) {
		case 81:
			sql.append(getKQSql(tabid, tableName, condition, cFactorMap, mustCellList, pre));
			break;
		case 5:
			sql.append(getGZSql(tabid, tableName, condition, cFactorMap,mustCellList, relatTableid, userName));
			break;

		}

		return sql.toString();
	}

	/**
	 * 将临时表中的代码型数据转换成业务数据
	 * 
	 * @param tabid
	 *            高级花名册id
	 * @param tableName
	 *            临时表名
	 * @param mustCellList
	 *            表列信息集合
	 */
	public void transformCode(String tableName, ArrayList mustCellList,HashMap cFactorMap) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		//wangcq 2014-11-20 begin 查询临时表字段类型
		StringBuffer sql_muster = new StringBuffer("");
		sql_muster.append("select * from ");
		sql_muster.append(tableName);
		sql_muster.append(" where 1=2");
		//wangcq 2014-11-20 end
		try {

			recset = dao.search(sql_muster.toString());
			ResultSetMetaData data=recset.getMetaData();
			if (cFactorMap.get("groupN") != null) {
				StringBuffer sql_str = new StringBuffer(" update " + tableName+ " set ");
				String groupPoint = (String) cFactorMap.get("groupN");
				groupPoint = groupPoint != null&& groupPoint.trim().length() > 4 ? groupPoint.substring(0, 5) : "";
				FieldItem fielditem = DataDictionary.getFieldItem(groupPoint);
				if ("nbase".equalsIgnoreCase(groupPoint)) {
					sql_str.append(" "+tableName+".GroupV");
					sql_str.append("=(");
					sql_str.append("select dbname from dbname S where  "+tableName+".GroupN=S.pre");
					sql_str.append(") where exists (select null from dbname S where "+tableName+".GroupN=S.pre)");
					dao.update(sql_str.toString());
				} else if ((fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid())))|| "B0110".equals(groupPoint)|| "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_str.append(" "+tableName+".GroupV");
					sql_str.append("=(");
					sql_str.append("select codeitemdesc from organization S where  "+tableName+".GroupN=S.codeitemid");
					sql_str.append(") where exists (select null from organization S where "+tableName+".GroupN=S.codeitemid)");
					dao.update(sql_str.toString());
				} else {
					if (fielditem != null && fielditem.isCode()) {
						DynaBean a_dynaBean = null;
						String codeId = "";
						for (Iterator t = mustCellList.iterator(); t.hasNext();) {
							a_dynaBean = (DynaBean) t.next();
							if (((String) a_dynaBean.get("field_name")).equalsIgnoreCase(groupPoint)) {
								codeId = (String) a_dynaBean.get("codeid");
								break;
							}
						}
						if (codeId == null || codeId.trim().length() < 1) {
                            codeId = fielditem.getCodesetid();
                        }
						sql_str.append(" "+tableName+".GroupV");
						sql_str.append("=(");
						sql_str.append("select codeitemdesc from codeitem S where S.codesetid='"+ codeId+ "' and "+tableName+".GroupN=S.codeitemid");
						sql_str.append(") where exists (select null from codeitem S where S.codesetid='"+ codeId+ "' and "+tableName+".GroupN=S.codeitemid)");
						dao.update(sql_str.toString());
					}else if(fielditem!=null)
					{
						dao.update(" update "+tableName+" set GroupV=GroupN");
					}
				}
			}
			if (cFactorMap.get("groupN2") != null) {
				StringBuffer sql_str = new StringBuffer(" update " + tableName+ " set ");
				String groupPoint2 = (String) cFactorMap.get("groupN2");
				groupPoint2 = groupPoint2 != null&& groupPoint2.trim().length() > 4 ? groupPoint2.substring(0, 5) : "";
				FieldItem fielditem = DataDictionary.getFieldItem(groupPoint2);
				if ("nbase".equalsIgnoreCase(groupPoint2)) {
					sql_str.append(" "+tableName+".GroupV2");
					sql_str.append("=(");
					sql_str.append("select dbname from dbname S where  "+tableName+".GroupN2=S.pre");
					sql_str.append(") where exists (select null from dbname S where "+tableName+".GroupN2=S.pre)");
					dao.update(sql_str.toString());
				} else if ((fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid())))|| "B0110".equals(groupPoint2)|| "E01A1".equals(groupPoint2)|| "E0122".equals(groupPoint2)) // 采用的硬编码指标
				{
					sql_str.append(" "+tableName+".GroupV2");
					sql_str.append("=(");
					sql_str.append("select codeitemdesc from organization S where  "+tableName+".GroupN2=S.codeitemid");
					sql_str.append(") where exists (select null from organization S where "+tableName+".GroupN2=S.codeitemid)");
					dao.update(sql_str.toString());
				} else {
					if (fielditem != null && fielditem.isCode()) {
						DynaBean a_dynaBean = null;
						String codeId = "";
						for (Iterator t = mustCellList.iterator(); t.hasNext();) {
							a_dynaBean = (DynaBean) t.next();
							if (((String) a_dynaBean.get("field_name")).equalsIgnoreCase(groupPoint2)) {
								codeId = (String) a_dynaBean.get("codeid");
								break;
							}
						}
						if (codeId == null || codeId.trim().length() < 1) {
                            codeId = fielditem.getCodesetid();
                        }
						sql_str.append(" "+tableName+".GroupV2");
						sql_str.append("=(");
						sql_str.append("select codeitemdesc from codeitem S where S.codesetid='"+ codeId+ "' and "+tableName+".GroupN2=S.codeitemid");
						sql_str.append(") where exists (select null from codeitem S where S.codesetid='"+ codeId+ "' and "+tableName+".GroupN2=S.codeitemid)");
						dao.update(sql_str.toString());
					}else if(fielditem!=null)
					{
						dao.update(" update "+tableName+" set GroupV2=GroupN2");
					}
				}
			}

			for (Iterator t = mustCellList.iterator(); t.hasNext();) {
				DynaBean a_dynaBean = (DynaBean) t.next();
				String codeId = (String) a_dynaBean.get("codeid");
				String gridNo = (String) a_dynaBean.get("gridno");
				String flag = (String) a_dynaBean.get("flag");
				String field_name=(String)a_dynaBean.get("field_name");
				/*if(flag!=null&&flag.equalsIgnoreCase("C"))
					continue;*/
				
				if ((codeId != null && codeId.trim().length() > 0&& !"0".equals(codeId.trim()))|| "nbase".equalsIgnoreCase(field_name)) {
					
					//wangcq 2014-11-19 begin 历史记录取值为条件定位或多条记录中的最近几条记录和最初几条记录，多条记录时逐个查询放入临时表 
					if(!"M".equals(flag) && HmusterViewBo.isTextType("C"+gridNo, data)){
						StringBuffer sql_gridNo = new StringBuffer("select recidx,C" +gridNo+ " from " + tableName);
						recset = dao.search(sql_gridNo.toString());
						ArrayList resultList = new ArrayList();
						recset = dao.search(sql_gridNo.toString());
						while(recset.next()){
							int recidx = recset.getInt(1);
							String gridValue = recset.getString(2);
							gridValue=gridValue==null?"":gridValue;
							String result = "";
							if(gridValue.indexOf("`") != -1){
								String[] value = gridValue.split("`");
								for(int i=0; i<value.length; i++){
//									if(value[i].indexOf("^^") != -1){  //当数据之前有`时，后面继续插入内容用`分开，之前的`变为^^的情况，继续分解数据里的内容
//										String[] value2 = value[i].split("\\^\\^");
//										for(int j=0; j<value2.length; j++){
//											StringBuffer sql_sql = new StringBuffer("");
//											if (codeId.equals("UN") || codeId.equals("UM")|| codeId.equals("@K")) {
//												sql_sql.append("select codeitemdesc from organization S where  "+value2[j]+ "=S.codeitemid");
//												
//											} else if (codeId.equals("@@")||field_name.equalsIgnoreCase("nbase")) {
//												sql_sql.append("select dbname from dbname S where  "+value2[j]+ "=S.pre");
//												
//											} else {
//												sql_sql.append("select codeitemdesc from codeitem S where codesetid='"+ codeId+ "' and  "+value2[j]+ "=S.codeitemid");
//											}
//											RowSet row = dao.search(sql_sql.toString());
//											while(row.next()){
//												if(j != value2.length-1){
//													result += row.getString(1) + "`";
//												}else{
//													result += row.getString(1);
//												}
//											}
//										}
//									}else{
										StringBuffer sql_sql = new StringBuffer("");
										if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
											sql_sql.append("select codeitemdesc from organization S where  '"+value[i]+"'=S.codeitemid");
											
										} else if ("@@".equals(codeId)|| "nbase".equalsIgnoreCase(field_name)) {
											sql_sql.append("select dbname from dbname S where  '"+value[i]+"'=S.pre");
											
										} else {
											sql_sql.append("select codeitemdesc from codeitem S where codesetid='"+ codeId+ "' and  '"+value[i]+"'=S.codeitemid");
										}
										RowSet row = dao.search(sql_sql.toString());
										while(row.next()){
											if(i != value.length-1){
												result += row.getString(1) + "\r\n";
											}else{
												result += row.getString(1);
											}
										}
//									}
								}
								resultList.add(result);
								StringBuffer sql_str = new StringBuffer(" update "+ tableName + " set C" + gridNo + "=? where recidx =" + recidx);
								dao.update(sql_str.toString(), resultList);
								resultList.clear();
							}else{
								if(!"".equals(gridValue)){ //单条记录，防止为空出现异常
									StringBuffer sql_sql = new StringBuffer("");
									if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
										sql_sql.append("select codeitemdesc from organization S where  '"+gridValue+ "'=S.codeitemid");
										
									} else if ("@@".equals(codeId)|| "nbase".equalsIgnoreCase(field_name)) {
										sql_sql.append("select dbname from dbname S where  '"+gridValue+"'=S.pre");
										
									} else {
										sql_sql.append("select codeitemdesc from codeitem S where codesetid='"+ codeId+ "' and  '"+gridValue+ "'=S.codeitemid");
									}
									RowSet row = dao.search(sql_sql.toString());
									while(row.next()){
										result += row.getString(1);
									}
									resultList.add(result);
									StringBuffer sql_str = new StringBuffer(" update "+ tableName + " set C" + gridNo + "=? where recidx =" + recidx);
									dao.update(sql_str.toString(), resultList);
									resultList.clear();
								}
							}
						}
					    //wangcq 2014-11-19 end
					}else{
						StringBuffer sql_str = new StringBuffer(" update "+ tableName + " set ");
						StringBuffer sql_update = new StringBuffer("");
						if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
							sql_update.append(" "+tableName+".");
							sql_update.append("C");
							sql_update.append(gridNo);
							sql_update.append("=(");
							sql_update.append("select codeitemdesc from organization S where  "+tableName+".C"+ gridNo+ "=S.codeitemid");
							sql_update.append(") where exists (select null from organization S where "+tableName+".C"+ gridNo+ "=S.codeitemid)");

						} else if ("@@".equals(codeId)|| "nbase".equalsIgnoreCase(field_name)) {
							sql_update.append(" "+tableName+".");
							sql_update.append("C");
							sql_update.append(gridNo);
							sql_update.append("=(");
							sql_update.append("select dbname from dbname S where  "+tableName+".C" + gridNo + "=S.pre");
							sql_update.append(") where exists (select null from dbname S where "+tableName+".C" + gridNo + "=S.pre)");

						} else {
							sql_update.append(" "+tableName+".");
							sql_update.append("C");
							sql_update.append(gridNo);
							sql_update.append("=(");
							sql_update.append("select codeitemdesc from codeitem S where codesetid='"+ codeId+ "' and  "+tableName+".C"+ gridNo + "=S.codeitemid");
							sql_update.append(")  where exists (select null from codeitem S where S.codesetid='"+ codeId+ "' and "+tableName+".C"+ gridNo + "=S.codeitemid)");
						}
						sql_str.append(sql_update.toString());
						dao.update(sql_str.toString());
					}
					
					
					
				}else{
					//wangcq 2014-11-19 begin 历史记录取值为条件定位或多条记录中的最近几条记录和最初几条记录，多条记录时逐个查询放入临时表 
					if(!"M".equals(flag) && HmusterViewBo.isTextType("C"+gridNo, data)){
						StringBuffer sql_gridNo = new StringBuffer("select recidx,C" +gridNo+ " from " + tableName);
						recset = dao.search(sql_gridNo.toString());
						ArrayList resultList = new ArrayList();
						while(recset.next()){
							String recidx = recset.getString(1);
							String gridValue = recset.getString(2);
							String result = "";
							if(StringUtils.isNotEmpty(gridValue)&&gridValue.indexOf("`") != -1){// bug 号：35350
								String[] value = gridValue.split("`");
								for(int i=0; i<value.length; i++){
									if(i != value.length-1){
										result += value[i] + "\r\n";
									}else{
										result += value[i];
									} 
								}
								resultList.add(result.replaceAll("\\^\\^", "`"));//将result中的^^转为·
								StringBuffer sql_str = new StringBuffer(" update "+ tableName + " set C" + gridNo + "=? where recidx =" + recidx);
								dao.update(sql_str.toString(), resultList);
								resultList.clear();
							}
						}
					}
					//wangcq 2014-11-19 end
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据条件从档案库中导入数据到高级花名册的临时表中
	 * 
	 * @param nFlag
	 *            1: 保险台帐, 2: 合同台帐, 3: 人员(忽略), 4: 工资, 5: 调资模板, 6: 工资分析, 7:
	 *            报批花名册, 21: 机构花名册(忽略), 41: 职位名册(忽略), 61: 培训名册, 81: 考勤
	 *            stipend:员工薪酬-花名册
	 * 
	 * @param username
	 *            用户名
	 * @param tabid
	 *            高级花名册id
	 * @param pre
	 *            应用库前缀(调资模板，考勤中为空)
	 * @param condition
	 *            (2)档案目录和档案转递时(档案中调用) 档案目录格式： $$@|#+A0100[干部][工人] 档案转递格式：
	 *            $%@|#+A0100,A0100, 前2位：$%，$@ 第3位：表号
	 * 
	 * (3)培训时为记录条件串,没有where,培训中调用 格为： 条件串
	 * 
	 * (4)考勤时为记录条件串,没有where,考勤中调用 考勤子集`条件串
	 * 
	 * 考勤子集: 子集号，-1表示所有,如:
	 * 
	 * -1 表示所有考勤子集的相关的报表 3 表示Q03相关的报表 5 表示Q05相关的报表 (6)工资分析 <TABID></TABID>打开哪张表
	 * <DBNAME></DBNAME>人员库条件 <FLAG></FLAG> (工资,保险)=(空值,1)
	 * 
	 */
	private HashMap cloumnMap=new HashMap();
	
	/**
	 * 创建模板临时表并导入数据(未变化人员)<br>
	 * 朝阳卫生局-完整聘任名册
	 */
	private void prepareTmplTempTable(HashMap cFactorMap, int tmplTabId, String tmplTabName, String tmplCond) {
	    tmplTempTableName = "T#"+this.userView.getUserName()+"_mus_2";
	    DbWizard dbw = new DbWizard(this.conn);
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        if(dbw.isExistTable(tmplTempTableName,false)) {
            dbw.dropTable(tmplTempTableName);
        }
	    dbw.createTempTable(tmplTabName, tmplTempTableName, "*", tmplCond, null);

	    /* 加主键避免tablebo.impDataFromArchive()报错：
	         java.lang.StringIndexOutOfBoundsException: String index out of range: -3
	              at com.hrms.frame.dao.tablemodel.TableModel.getUpdateSql(TableModel.java:258)
        */
        Table dest = new Table(tmplTempTableName);
	    DatabaseMetaData dbmeta = null;
	    ResultSet rtset = null;
	    try
	    {
	          dbmeta = conn.getMetaData();
              if(DbWizard.dbflag == Constant.ORACEL) {
                  rtset = dbmeta.getPrimaryKeys(null, dbmeta.getUserName(), tmplTabName.toUpperCase());
              } else {
                  rtset = dbmeta.getPrimaryKeys(null, null, tmplTabName);
              }
              while(rtset.next()){
                  String colname = rtset.getString("COLUMN_NAME");
                  if("ins_id".equalsIgnoreCase(colname)) {
                      continue;  // ins_id不加主键
                  }
                  Field f = new Field(colname);
                  f.setKeyable(true);
                  dest.addField(f);
	          }
              if(dest.size()>0) {
                  dbw.addPrimaryKey(dest);
                  DBMetaModel dbmodel=new DBMetaModel(this.conn);
                  dbmodel.reloadTableModel(tmplTempTableName);  // 必须重新加载，否则还会报错
              }
	    }
        catch(Exception e){
            e.printStackTrace();
        }

        // ins_id设为允许空
	    if(dbw.isExistField(tmplTempTableName, "ins_id", false)){
	        Table table = new Table(tmplTempTableName);
	        Field field = new Field("ins_id","ins_id");
	        field.setVisible(true);
	        field.setKeyable(false);
	        field.setNullable(true);
	        field.setDatatype(DataType.INT);
	        field.setLength(10);
            table.addField(field);
            try{
                dbw.alterColumns(table);
            }
            catch(Exception e){
                e.printStackTrace();
            }
	    }
	    // 加NBASEID
	    if(!dbw.isExistField(tmplTempTableName, "NBASEID", false)){
            Table table = new Table(tmplTempTableName);
            Field field = new Field("NBASEID","NBASEID");
            field.setVisible(true);
            field.setKeyable(false);
            field.setNullable(true);
            field.setDatatype(DataType.INT);
            field.setLength(10);
            table.addField(field);
            try{
                dbw.addColumns(table);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
	    boolean hasE0122_2 = dbw.isExistField(tmplTempTableName, "E0122_2", false);
	    boolean hasB0110_2 = dbw.isExistField(tmplTempTableName, "B0110_2", false);
	    for(int i=0;i < userView.getPrivDbList().size();i++){
	        String base = userView.getPrivDbList().get(i).toString().toUpperCase();
	        String a01 = base + "A01";
	        if(cFactorMap.get("ShowChangedOrgNbases").toString().length() > 0 &&
	                cFactorMap.get("ShowChangedOrgNbases").toString().toUpperCase().indexOf(base) == -1) {
                continue;
            }
	        try{
	            String cond = "(";
	            if(hasE0122_2) {
                    cond += "exists(select 1 from "+ tmplTempTableName +
                        " where "+Sql_switcher.substr(a01+".E0122", "1", Sql_switcher.length("E0122_2")) +"=E0122_2)";
                } else if(hasB0110_2) {
                    cond += "exists(select 1 from "+ tmplTempTableName +
" where "+Sql_switcher.substr(a01+".B0110", "1", Sql_switcher.length("B0110_2")) +"=B0110_2)";
                } else {
                    continue;
                }
	            cond += " and A0100 in (select "+a01+".A0100 "+ userView.getPrivSQLExpression(base, true)+")";  // 管理范围(含高级授权)
                
                if("1".equals(sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"occupy_quota")))// 兼职占编制数
                {
                     String partSQL = ""; 
                     String partSet = base + sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                     String partCond = null;
                     String partunit=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
                     String partdept=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
                     if(hasE0122_2) {
                         partCond = "exists(select 1 from " + tmplTempTableName +
                             " where "+Sql_switcher.substr(partSet+"."+partdept, "1", Sql_switcher.length("E0122_2")) +"=E0122_2)";
                     } else if(hasB0110_2) {
                         partCond = "exists(select 1 from "+ tmplTempTableName +
                             " where "+Sql_switcher.substr(partSet+"."+partunit, "1", Sql_switcher.length("B0110_2")) +"=B0110_2)";
                     }

                     if(userView.getManagePrivCodeValue()!=null) {
                         partSQL=this.getQueryFromPartTmpl(userView, base, userView.getManagePrivCodeValue(), partCond);
                     }
                     if(partSQL!=null&&!"".equals(partSQL))
                     {
                         cond += " or ("+partSQL+")";
                     }
                }
                cond += ")";

                // 模板中没有的人员
                cond += " and not exists(select 1 from " + tmplTempTableName+" t where upper(basepre)='"+base+"' and t.A0100="+a01+".A0100)";
                String sql="select A0100 from "+a01+" where " + cond + " order by A0000";
                StringBuffer a0100s = new StringBuffer();
                
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rset = dao.search(sql);
                while(rset.next()){
                    a0100s.append("'"+rset.getString("A0100")+"',");
                }
                if(a0100s.length()>0) {
                    a0100s.setLength(a0100s.length()-1);
                }
                TemplateTableBo tablebo=new TemplateTableBo(conn, tmplTabId, this.userView);
                tablebo.setImpOthTableName(tmplTempTableName);
                tablebo.setChange_after_get_data("1");   // 变化后指标取值方式,0:不取(默认值),1:取当前记录
                tablebo.impDataFromArchive(a0100s.toString(), base, 0);
                
                // A0000
                String strSet = tmplTempTableName+".A0000="+a01+".A0000";
                String strJoin = a01+".A0100="+tmplTempTableName+".A0100";
                String destCond = "upper("+tmplTempTableName+".basepre) = '"+base+"' and "+tmplTempTableName+".A0000 is null";
                String srcCond = "";
                dbw.updateRecord(tmplTempTableName, a01, strJoin, strSet, destCond, srcCond);

	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	    }
        // 导入NBASEID
	    try{
    	    String strSet = tmplTempTableName+".NBASEID=dbname.dbid";
    	    String strJoin = "upper(dbname.Pre)=upper("+tmplTempTableName+".basepre)";
    	    String destCond = "";
    	    String srcCond = "";
            dbw.updateRecord(tmplTempTableName, "dbname", strJoin, strSet, destCond, srcCond);
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 删除模板临时表
	 */
	private void dropTmplTempTable() {
	    if(tmplTempTableName != null) {
            DbWizard dbw = new DbWizard(this.conn);
            if(dbw.isExistTable(tmplTempTableName,false))
            {
                dbw.dropTable(tmplTempTableName);
            }
	        tmplTempTableName = null;
	    }
	}
	
	/**
	 * 导入考勤/业务模板(用户名+templet_模板号)数据
	 * @param nFlag
	 * @param userName
	 * @param tabid
	 * @param pre
	 * @param condition
	 * @param userView
	 * @param relatTableid
	 */
	public void importData(String nFlag, String userName, String tabid,
			String pre, String condition, UserView userView, String relatTableid) {
	    kqTableId = relatTableid==null?"":relatTableid;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 创建临时表 */
			DbWizard dbWizard = new DbWizard(this.conn);
			String tableName = userName + "_Muster_" + tabid; // 临时表名
			if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
                tableName = this.getTemptable();
            }
			HashMap cFactorMap = getCfactor(tabid);
			MusterBo mbo = new MusterBo(this.conn);
			Table temp_table = createMusterTempTable(tableName, tabid, dbWizard,(String)cFactorMap.get("groupN"),(String)cFactorMap.get("groupN2"),mbo);
			
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tableName);
			
			ArrayList mustCellList = getMusterCellList(tabid);
			//gz_sql_insert.append(userName + "Templet_" + relatTableid);
			if("5".equals(nFlag))//工资变动中用
			{
		    	RowSet rs=dao.search("select * from "+userName + "Templet_" + relatTableid+" where 1=2");
		    	ResultSetMetaData data=rs.getMetaData();
		    	for(int i=1;i<=data.getColumnCount();i++)
		    	{
		    		String columnName=data.getColumnName(i).toLowerCase();
			    	cloumnMap.put(columnName, columnName);
		    	}
			}
			String sql = getSql(nFlag, tabid, tableName, pre, condition,cFactorMap, mustCellList, relatTableid, userName);
			if("5".equals(nFlag)){  //wangcq 2014-12-06 花名册模版才需要比较字段类型
				ArrayList list = getSubSql(mustCellList);
				updateTableColumnType(dbWizard, temp_table, (String)list.get(2), (String)list.get(3), tableName, userName + "templet_" + relatTableid);//更改表字段的类型
			}
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
	     		HmusterBo.createMusterRecidx(this.conn,tableName);
			}
			dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
	     		HmusterBo.updateMusterRecidxS(this.conn,tableName);
			}
			dbWizard.addPrimaryKey(temp_table);
			this.transformMidvariable(pre, tabid, tableName);
			this.transformQueryCond(tableName, tabid, userView, "0", pre,relatTableid);
			transformCode(tableName, mustCellList, cFactorMap); // 将临时表中的代码型数据转换成业务数据
			if (cFactorMap.get("groupN") != null) {
				layerOrg(tableName,(String)cFactorMap.get("groupN"),1, "");
			}
			if (cFactorMap.get("groupN2") != null) {
				layerOrg(tableName,(String)cFactorMap.get("groupN2"),2, "");
			}
            dropTmplTempTable();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**考勤中，取数时，部分记录不起作用，取到 当取部分记录时的sql条件*/
	private String kqconditionSQL="";
	public void getKQConditionSQL(String fromCope,String toCope,String selectPoint)
	{
		try
		{
			/**selectPoint的格式  2971*/
			//field_name + "/"+ codeid + "/" + field_type + "/" + setName 或者是子集id
			if((fromCope==null|| "".equals(fromCope))&&(toCope==null|| "".equals(toCope))) {
                return;
            }
            StringBuffer viewSql = new StringBuffer("");
            String[] temp=null;
			if (selectPoint != null) {
				temp = selectPoint.split("/");
			}
			//A5805/0/N/A58
			if(temp!=null&&temp.length>1){
			    String tabName = "";
			    if (temp.length == 4) {
                    tabName = temp[3];
                }
				if (fromCope != null && !"".equals(fromCope)) {
					viewSql.append(getStr(temp, fromCope, 1, tabName));
				}
				if (fromCope != null && !"".equals(fromCope)&& toCope != null && !"".equals(toCope)) {
					viewSql.append(" and ");
					viewSql.append(getStr(temp, toCope, 2, tabName));
				} else if (toCope != null && !"".equals(toCope)) {
					viewSql.append(getStr(temp, toCope, 2, tabName));
				}
			}else{
				if (fromCope != null && !"".equals(fromCope)) {
					if(fromCope.startsWith("+")||fromCope.startsWith("*")) {
                        fromCope=fromCope.substring(1);
                    }
					String[] fomula = fromCope.split("::");
					if(fomula!=null&&fomula.length==3){
						boolean blike=false;
				    	if("1".equals(fomula[2])) {
                            blike=true;
                        }
				        boolean bresult=true; 
				    	boolean bhis=true;
				    	String dbpre = "";
				    	/*ArrayList list = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
						String wherestr = userView.getPrivSQLExpression(fomula[0]+"|"+PubFunc.getStr(fomula[1]),dbpre,bhis,blike,bresult,list);
						wherestr=wherestr.substring(wherestr.toUpperCase().indexOf("WHERE")+5);*/
					    FactorList factorlist=new FactorList(PubFunc.keyWord_reback(fomula[0]),PubFunc.keyWord_reback(fomula[1]),dbpre,bhis ,blike,bresult,1,userView.getUserId());
					    
		                String str=factorlist.getSingleTableSqlExpression((this.getKqtable()!=null&&this.getKqtable().length()>0)?this.getKqtable():toCope);
						//wherestr=wherestr.toUpperCase();
						viewSql.append(str);
					}
				}
			}
			kqconditionSQL=viewSql.toString();		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 导入业务模板审批临时表(templet_模板号)数据
	 * @param nFlag
	 * @param userName
	 * @param tabid
	 * @param pre
	 * @param condition
	 * @param userView
	 * @param relatTableid
	 * @param spflag
	 */
	public void importData(String nFlag, String userName, String tabid,
			String pre, String condition, UserView userView,
			String relatTableid, String spflag) {

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			/** 创建临时表 */
			this.userView = userView;
			DbWizard dbWizard = new DbWizard(this.conn);
			String tableName = userName + "_Muster_" + tabid; // 临时表名
			if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
                tableName = this.getTemptable();
            }
			HashMap cFactorMap = getCfactor(tabid);
			MusterBo mbo = new MusterBo(this.conn);
			Table temp_table = createMusterTempTable(tableName, tabid, dbWizard,(String)cFactorMap.get("groupN"),(String)cFactorMap.get("groupN2"),mbo);
			
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tableName);
			
			ArrayList mustCellList = getMusterCellList(tabid);
			if("5".equals(nFlag))//工资变动中用
			{
		    	rs=dao.search("select * from Templet_" + relatTableid+" where 1=2");
		    	ResultSetMetaData data=rs.getMetaData();
		    	for(int i=1;i<=data.getColumnCount();i++)
		    	{
		    		String columnName=data.getColumnName(i).toLowerCase();
			    	cloumnMap.put(columnName, columnName);
		    	}
			}
			String sql = getGZSql(tabid, tableName, condition, cFactorMap,mustCellList, relatTableid, userName, spflag);
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
	     		HmusterBo.createMusterRecidx(this.conn,tableName);
			}
			dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
				HmusterBo.updateMusterRecidxS(this.conn,tableName);
			}
			this.transformMidvariable(pre, tabid, tableName);
			this.transformInsQueryCond(tableName, tabid, userView, "0", pre,relatTableid);
			transformCode(tableName, mustCellList, cFactorMap); // 将临时表中的代码型数据转换成业务数据
			if (cFactorMap.get("groupN") != null) {
				layerOrg(tableName,(String) cFactorMap.get("groupN"),1,"");
			}
			if (cFactorMap.get("groupN2") != null) {
				layerOrg(tableName,(String) cFactorMap.get("groupN2"),2,"");
			}
            dropTmplTempTable();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * 根据条件从薪资历史/归档表中导入数据到高级花名册临时表
	 * 
	 * @param tabid
	 * @param salaryid
	 * @param a_code
	 * @param dateWhere
	 * @param num
	 * @param preWhere
	 * @param sumFlag
	 * @return
	 * @throws GeneralException
	 */
	public String importData(String tabid, String a_code, String dateWhere,
			String preWhere, String sumFlag, String gz_module, String dbname,
			String category) throws GeneralException {
		String isSuccess = "1"; // 成功
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 创建临时表 */
			DbWizard dbWizard = new DbWizard(this.conn);
			String tableName = userView.getUserName().trim().replaceAll(" ", "")+"_Muster_" + tabid; // 临时表名
			if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
                tableName = this.getTemptable();
            }
			int nmodule = 4;
			if("0".equals(gz_module)) {
                nmodule = 6;
            } else if("1".equals(gz_module)) {
                nmodule = 8;
            }
			MusterBo mbo = new MusterBo(this.conn);
			Table temp_table = createMusterTempTable(tabid, dbWizard, nmodule,this.groupPointItem,this.groupPoint2,mbo);
			
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tableName);
			
			HashMap cFactorMap = getCfactor(tabid);
			ArrayList mustCellList = getMusterCellList(tabid);
			// 表中有指标没构库
			String sql = createGzSQL(tabid, a_code, cFactorMap, dateWhere,
					preWhere, sumFlag, gz_module, dbname, category);
			//薪资分析 默认查看薪资历史表和薪资归档表数据，考虑性能采用先查询再union
			 StringBuffer sql_new=new StringBuffer();
			 String sql_insert="";
			 String sql_select="";
			 String sql_order="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
			    sql_insert=sql.substring(0, sql.indexOf("select"));
                sql_select=sql.substring(sql.indexOf("select"));
                sql_order=sql.substring(sql.lastIndexOf("order")).substring(0, sql.substring(sql.lastIndexOf("order")).indexOf(")"));
			}else {
			    sql_insert=sql.substring(0, sql.indexOf("select"));
	            sql_select=sql.substring(sql.indexOf("select"), sql.lastIndexOf("order"));
	            sql_order=sql.substring(sql.lastIndexOf("order"),sql.length());
			}
			 sql_new.append(sql_insert);
             sql_new.append(" select * from ( ");
             sql_new.append(sql_select);
             sql_new.append(" union all ");
             sql_new.append(sql_select.replace("salaryhistory","salaryarchive"));
             sql_new.append(" )aa ");
             sql_new.append(sql_order.replace("a.", "aa."));
			dao.delete("delete from " + tableName, new ArrayList());
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                HmusterBo.createMusterRecidx(this.conn,tableName);
            }
			dao.insert(sql_new.toString(), new ArrayList()); // 将不用计算的数据全部插入临时表
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                HmusterBo.updateMusterRecidxS(this.conn,tableName);
            }
			dbWizard.addPrimaryKey(temp_table);
			ArrayList alUsedFields = setSalaryList();
			this.transformMidvariable(dbname, tabid, tableName);
			runCountFormula3(tabid, alUsedFields, dbname, this.getAnalyseTabNameStr(), this.whl_str,sumFlag);
			transformCode(tableName, mustCellList, cFactorMap);// 将临时表中的代码型数据转换成业务数据
			
			if (groupPointItem != null && groupPointItem.trim().length() > 0) {
				layerOrg(tableName,groupPointItem,1,groupOrgCodeSet);
			}
			if (this.isGroupPoint2 != null && "1".equals(this.isGroupPoint2)) {
				layerOrg(tableName,this.groupPoint2,2,groupOrgCodeSet2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}

		return isSuccess;

	}
	public String whl_str="";

	public ArrayList setSalaryList() {
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		/*FieldItem fielditem = DataDictionary.getFieldItem("A00Z0");
		alUsedFields.add(fielditem);
		fielditem = DataDictionary.getFieldItem("A00Z1");
		alUsedFields.add(fielditem);
		fielditem = DataDictionary.getFieldItem("A00Z2");
		if(fielditem!=null)
	    	alUsedFields.add(fielditem);
		fielditem = DataDictionary.getFieldItem("A00Z3");
		if(fielditem!=null)
	    	alUsedFields.add(fielditem);
		fielditem = DataDictionary.getFieldItem("NBASE");
		alUsedFields.add(fielditem);*/
		RowSet rset = null;
		//ArrayList alUsedFields=new ArrayList();
		try
		{
			/**把几个特殊的也加上*/
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search("select max(itemid) as itemid,max(itemdesc) as itemdesc,max(fieldsetid) as fieldsetid,max(itemlength) as itemlength,max(decwidth) as decwidth" +
					",max(codesetid) as codesetid,max(itemtype) as itemtype from SALARYSET where UPPER(itemid) in ('A00Z0','A00Z1','A00Z2','A00Z3','NBASE') group by ITEMID ");
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("ITEMID"));
				item.setFieldsetid(rset.getString("FIELDSETID"));//没有实际含义
				item.setItemdesc(rset.getString("ITEMDESC"));
				item.setItemlength(rset.getInt("ITEMLENGTH"));
				item.setDecimalwidth(rset.getInt("DECWIDTH"));
				item.setCodesetid(rset.getString("CODESETID"));
				FieldItem aitem = DataDictionary.getFieldItem(rset.getString("ITEMID").toLowerCase());
				if(aitem!=null) {
                    item.setFormula(aitem.getFormula());
                } else {
                    item.setFormula("");
                }
				item.setItemtype(rset.getString("ITEMTYPE"));
				//liuy 2015-2-3 6595：君正集团：薪资分析-用户自定义表，su登录105号表，定义了公式列“所得期间止”算不出来值，后台越界错。 start
				alUsedFields.add(0, item);
				//liuy 2015-2-3 end
			}
			if(midvariableList!=null&&midvariableList.size()>0) {
                alUsedFields.addAll(this.midvariableList);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rset!=null)
			{
				try
				{
					rset.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return alUsedFields;
	}

	/**
	 * 计算公式
	 * 
	 * @param tabid
	 * @param dbpre
	 * @param userView
	 * @param a0100
	 * @throws GeneralException
	 */
	public void runCountFormula3(String tabid, ArrayList allUsedFields,
			String dbpre, String tableName, String sqlwhere,String sumFlag) {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = getFormula(dao,tabid);
			String tablename = userView.getUserName()+"_Muster_" + tabid; // 临时表名
			if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
                tablename = this.getTemptable();
            }
			/**如果公式中包含临时变量*/
//			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView);
//			gzbo.getYearMonthCount2();
//			gzbo.addMidVarIntoGzTable("",getFormulaTempList(list));
			for (int i=0;i<list.size();i++) {
				HashMap hm = (HashMap)list.get(i);
				if(hm==null) {
                    continue;
                }
				String gridNo = (String)hm.get("gridno");
				String fieldType = (String)hm.get("field_type");
				String queryCond = (String)hm.get("QueryCond");
				String codeSetID = (String)hm.get("CodeId");
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员

				// 解析公式
				YksjParser yp = new YksjParser(userView, allUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht", dbpre);
				ArrayList fieldList = yp.getFormulaFieldList1(queryCond);
				if (fieldList.size() != 0) {
					analyseOptTable3(tabid, fieldList, tableName, sqlwhere,sumFlag);
				}
				// 解析公式
				yp = new YksjParser(this.userView, allUsedFields, YksjParser.forNormal, varType, YksjParser.forPerson, "Ht", dbpre);
//				yp.run(queryCond);
				try{
					yp.run(queryCond,this.conn,"",tablename);
					String FSQL = yp.getSQL();
					//liuy 2015-2-10 7511：薪资分析-用户自定义表， 105号花名册，插入了计算公式，日期型，取出来的数据带了时分秒，不对。 start
					if("D".equalsIgnoreCase(fieldType)){
						FSQL = Sql_switcher.numberToChar(Sql_switcher.year(FSQL))
								+ Sql_switcher.concat()
								+ "'.'"
								+ Sql_switcher.concat()
								+ Sql_switcher.numberToChar(Sql_switcher
										.month(FSQL)) + Sql_switcher.concat() + "'.'"
								+ Sql_switcher.concat()
								+ Sql_switcher.numberToChar(Sql_switcher.day(FSQL));
					}
					//liuy 2015-2-10 end
					dao.update("update "+tablename+ " set C" + gridNo + "="+ FSQL);
					if(codeSetID!=null&&codeSetID.trim().length()>0&&!"0".equals(codeSetID)) {
                        changeCode(dao,tablename,"C" + gridNo,codeSetID,"");//liuy 2015-1-27 将tableName换成tablename
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private ArrayList getFormula(ContentDAO dao,String tabid){
		ArrayList formulalist=new ArrayList();
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select * from muster_cell where tabid="
					+ tabid + " and flag='C'");
			while (rowSet.next()) {
				HashMap hm = new HashMap();
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				String codeSetID = rowSet.getString("CodeId");
				hm.clear();
				hm.put("gridno", gridNo+"");
				hm.put("field_type", fieldType);
				hm.put("QueryCond", queryCond);
				hm.put("CodeId", codeSetID);
				formulalist.add(hm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return formulalist;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getFormulaTempList(ArrayList formulaList)throws GeneralException{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset = null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next()){
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			
			
			//过滤薪资类别  计算公式用不到的临时变量
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++){
				HashMap hm=(HashMap)formulaList.get(i);
				if(hm==null) {
                    continue;
                }
				String formula = (String)hm.get("QueryCond");
				for(int j=0;j<fieldlist.size();j++){
					item=(FieldItem)fieldlist.get(j);
					String item_id=item.getItemid().toLowerCase();
					String item_desc=item.getItemdesc().trim().toLowerCase();
					if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null){
						new_fieldList.add(item);
						map.put(item_id, "1");
					}
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//return fieldlist;
		return new_fieldList;
	}

	/**
	 * 动态创建表结构
	 * 
	 * @param tabid
	 * @param fieldList
	 * @param dbpre
	 * @throws GeneralException
	 */
	public void analyseOptTable3(String tabid, ArrayList fieldList,
			String tableName, String sqlwhre,String sumFlag) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			DbWizard dbWizard = new DbWizard(this.conn);
			String musterName = userView.getUserName()+"_Muster_" + tabid; // 临时表名
			if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
                musterName = this.getTemptable();
            }
			
			Table table = new Table(musterName);

			HashMap existColumnMap = new HashMap();
			rowSet = dao.search("select * from "+musterName + " where 1=2");
			ResultSetMetaData metaData = rowSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(), "1");
			}

			int num = 0;
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				FieldItem fieldItem1 = (FieldItem) t.next();
				if (existColumnMap.get(fieldItem1.getItemid().toLowerCase()) != null) {
                    continue;
                }
				Field a_field = fieldItem1.cloneField();
				table.addField(a_field);
				num++;
			}
			if (num != 0) {
                dbWizard.addColumns(table);
            }

			ArrayList valuelist = new ArrayList();
			StringBuffer sqlstr = new StringBuffer();
			StringBuffer updatesql = new StringBuffer();
			sqlstr.append("select ");
			if(!"1".equals(sumFlag)&&!"2".equals(sumFlag)) {
                sqlstr.append(" a00z0,a00z1,");
            }
			updatesql.append("update ");
			updatesql.append(musterName);
			updatesql.append(" set ");
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				FieldItem fieldItem1 = (FieldItem) t.next();
				if(!dbWizard.isExistField(tableName, fieldItem1.getItemid(), false)) {
                    continue;
                }
				if("1".equals(sumFlag)|| "2".equals(sumFlag)){
					if("N".equalsIgnoreCase(fieldItem1.getItemtype())){
						sqlstr.append("sum("+fieldItem1.getItemid().toUpperCase());
						sqlstr.append(") as "+fieldItem1.getItemid().toUpperCase() + ",");
					}else{
						if ("NBASE".equalsIgnoreCase(fieldItem1.getItemid())) {
							sqlstr.append("(select DBName from dbname where Pre=a."+ fieldItem1.getItemid().toUpperCase()+ ") as "+ fieldItem1.getItemid().toUpperCase()+",");
						} else {
                            sqlstr.append("max(a." + fieldItem1.getItemid().toUpperCase()+ ") as " + fieldItem1.getItemid().toUpperCase()+",");
                        }
					}
				}else {
                    sqlstr.append(fieldItem1.getItemid().toUpperCase() + ",");
                }
				updatesql.append(fieldItem1.getItemid().toUpperCase() + "=?,");
			}
			String sql = updatesql.substring(0, updatesql.length() - 1);
			updatesql = new StringBuffer(sql);
			updatesql.append(" where A0100=? and NBASE=?");
			boolean haveSalaryid = dbWizard.isExistField(tableName, "salaryid", false);
			//liuy 2015-1-4 6363：君正集团：薪资分析-用户自定义表，设置了分组指标，当分组指标的值维护的有null和空时，会出现两条记录，不对。（bs的） start
			if(haveSalaryid){				
				updatesql.append(" and salaryid=?");
				if("1".equals(sumFlag)|| "2".equals(sumFlag)) {
                    sqlstr.append("max(salaryid) as salaryid,");
                } else {
                    sqlstr.append("salaryid,");
                }
			}
			if(!"1".equals(sumFlag)&&!"2".equals(sumFlag)) {
                updatesql.append(" and a00z0=? and a00z1=?");
            }
			//liuy 2015-9-21 12935：广东中烟：薪资分析表如果选择多个人员库、多个薪资类别并且按人员汇总就会打不开 start
			if("1".equals(sumFlag)) {
                sqlstr.append("A0100,NBASE from ");//sqlstr.append("max(A0100) as A0100,max(NBASE) as NBASE from ");
            } else if("2".equals(sumFlag)) {
                sqlstr.append("A0100,NBASE,max(a00z0) as a00z0 from ");//sqlstr.append("max(A0100) as A0100,max(NBASE) as NBASE,max(a00z0) as a00z0 from ");
            } else {
                sqlstr.append("A0100,NBASE from ");
            }
			//liuy 2015-9-21 end			
			sqlstr.append(tableName);
			sqlstr.append(" a " + sqlwhre);
			StringBuffer groupBy = new StringBuffer(" group by A0100,NBASE");
			if("2".equals(sumFlag)) {
                groupBy.append(",A00Z0");
            }
			if("1".equals(sumFlag)|| "2".equals(sumFlag)) {
                sqlstr.append(" "+groupBy.toString());
            }
			String destTab = musterName;// 目标表
			String srcTab = "";// 源表
			if("1".equals(sumFlag)|| "2".equals(sumFlag)) {
                srcTab = "("+sqlstr.toString()+") "+tableName;
            } else {
                srcTab = tableName;
            }
			String strJoin = "";// 关联串
			if("1".equals(sumFlag)){
				strJoin = musterName + ".a0100=" + tableName + ".a0100 and "
				+ musterName + ".nbase=" + tableName + ".nbase";
			}else if ("2".equals(sumFlag)) {
				strJoin = musterName + ".a0100=" + tableName + ".a0100 and "
				+ musterName + ".nbase=" + tableName + ".nbase and "
				+ musterName + ".a00z0=" + tableName + ".a00z0";
			}else {
				if(haveSalaryid) {
                    strJoin = musterName + ".salaryid=" + tableName + ".salaryid and "
                    + musterName + ".a0100=" + tableName + ".a0100 and "
                    + musterName + ".nbase=" + tableName + ".nbase and "
                    + musterName + ".a00z0=" + tableName + ".a00z0 and "
                    + musterName + ".a00z1=" + tableName + ".a00z1";
                } else {
                    strJoin = musterName + ".a0100=" + tableName + ".a0100 and "
                    + musterName + ".nbase=" + tableName + ".nbase and "
                    + musterName + ".a00z0=" + tableName + ".a00z0 and "
                    + musterName + ".a00z1=" + tableName + ".a00z1";
                }
			}

			String strSet = "";// 更新串
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				FieldItem fieldItem1 = (FieldItem) t.next();
				if(!dbWizard.isExistField(tableName, fieldItem1.getItemid(), false)) {
                    continue;
                }
				strSet+=musterName+"."+fieldItem1.getItemid()+"="+tableName+"."+fieldItem1.getItemid()+"`";//liuy 2015-2-6 7431：保险分析：自定义表，重新取数时后台报语法错误
			}
			strSet = strSet.substring(0, strSet.length() - 1);
			String strDWhere = "";// 更新目标的表过滤条件
			String strSWhere = "";// 源表的过滤条件
			String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
			dao.update(update, new ArrayList());
			//liuy 2015-1-4 end
			/*
			RowSet rs = dao.search(sqlstr.toString());
			while (rs.next()) {
				ArrayList list = new ArrayList();
				for (Iterator t = fieldList.iterator(); t.hasNext();) {
					FieldItem fieldItem1 = (FieldItem) t.next();
	                if(!dbWizard.isExistField(tableName, fieldItem1.getItemid(), false))
	                    continue;
					// 标识：3063 薪资分析-用户定义表，定义了公式,ora库取不出来数据，sql是可以取出来数据的 xiaoyun 2014-7-10 start
					//list.add(rs.getString(fieldItem1.getItemid().toUpperCase()));
					if(StringUtils.equals("D", fieldItem1.getItemtype().toUpperCase())){
						list.add(rs.getDate(fieldItem1.getItemid().toUpperCase()));
					}else{
						list.add(rs.getString(fieldItem1.getItemid().toUpperCase()));
					}
					// 标识：3063 薪资分析-用户定义表，定义了公式,ora库取不出来数据，sql是可以取出来数据的 xiaoyun 2014-7-10 end 
				}
				list.add(rs.getString("A0100"));
				list.add(rs.getString("NBASE"));
				if(haveSalaryid)
				    list.add(String.valueOf(rs.getInt("salaryid")));
				if(!sumFlag.equals("1")&&!sumFlag.equals("2"))
				{
					list.add(rs.getDate("a00z0"));
					list.add(rs.getString("a00z1"));
				}
				valuelist.add(list);
			}
			dao.batchUpdate(updatesql.toString(), valuelist);
			*/

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * 
	 * @param tabid
	 * @param a_code
	 * @param declaredate
	 *            时间
	 * @param num
	 *            次数
	 * @param sumFlag
	 *            汇总
	 * @param condwhere
	 *            过滤条件
	 * @param dbname
	 *            人员库
	 * @return
	 * @throws GeneralException
	 */

	public String createGzSQL(String tabid, String a_code, HashMap cFactorMap,
			String declaredate, String condwhere, String sumFlag,
			String gz_module, String dbname, String category)
			throws GeneralException {
		DbWizard dbw=new DbWizard(this.conn);
		StringBuffer sql = new StringBuffer("");
		String musterName = userView.getUserName()+"_Muster_" + tabid; // 临时表名
		if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
            musterName = this.getTemptable();
        }
		try {
			ArrayList musterCellList = getMusterCellListSa(tabid); // 取得花名册不用计算的数据字段
			String tableName = this.getAnalyseTabNameStr();
			boolean haveSalaryid = dbw.isExistField(tableName, "salaryid", false);
            StringBuffer group_select = new StringBuffer("");
            group_select.append("select MAX(a.A0000) as A0000,MAX(a.A0100) as A0100,MAX(a.B0110) as B0110,MAX(a.E0122) as E0122,MAX(a.NBASE) as NBASE," +
            		"MAX(a.A00Z1) as A00Z1,MAX(a.A00z0) as A00Z0,MAX(a.A00Z2) as A00Z2,MAX(a.A00z3) as A00Z3,MAX(a.dbid) as dbid");
            if(haveSalaryid){   //liuy 2015-1-4 修改高级花名册汇总取数的时候报salaryid无效
            	group_select.append(",MAX(a.salaryid) as salaryid");
			}
            //liuy 2015-1-27 7030：薪资管理-薪资分析-用户自定义表，41号花名册，三处工资花名册， 取2014年数据， 选择按人员汇总、或按单位、归属日期汇总，报a00z2无效。 start
            group_select.append(getFieldSortStr("a",group_select));
            //liuy 2015-1-27 end
			StringBuffer sql_insert = new StringBuffer(" insert into "+musterName+ " (recidx,A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1,A00Z2,A00Z3");
			if(haveSalaryid){   //wangcq 2014-12-31 多个帐套需salaryid区分
				sql_insert.append(",salaryid");
			}
			StringBuffer sql_from = new StringBuffer(" from " + getGzTableSortView(tableName, "a"));
			StringBuffer sql_whl = new StringBuffer(" where ");
			StringBuffer groupBy = new StringBuffer(" group by A0100,NBASE");
			if("2".equals(sumFlag)) {
                groupBy.append(",A00Z0");
            }

			String isGroupPoint = "0";
			if (groupPointItem != null && groupPointItem.trim().length() > 0) {
				isGroupPoint = "1";
			}

			if (gz_module != null && "0".equals(gz_module)) {
				sql_whl.append(" salaryid in (select salaryid from salarytemplate where salaryid in(");
				sql_whl.append(category);
				sql_whl.append(") and (CSTATE IS NULL OR CSTATE=''))");
			} else if (gz_module != null && "1".equals(gz_module)) {
				sql_whl
						.append(" salaryid in (select salaryid from salarytemplate where salaryid in(");
				sql_whl.append(category);
				sql_whl.append(") and CSTATE='1' )");
			} else {
				sql_whl.append(" 1=1 ");
			}

			if (dbname != null && dbname.trim().length() > 0) {
				String[] pre = dbname.split(",");
				if (pre.length > 0) {
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
						sql_whl.append(" and (");
						for (int i = 0; i < pre.length; i++) {
							sql_whl.append(" lower(NBASE)=");
							sql_whl.append("'");
							sql_whl.append(pre[i].toLowerCase());
							sql_whl.append("'");
							if (i + 1 < pre.length) {
                                sql_whl.append(" or ");
                            }
						}
						sql_whl.append(")");
					} else {
						sql_whl.append(" and lower(NBASE) in(");
						for (int i = 0; i < pre.length; i++) {
							sql_whl.append("'");
							sql_whl.append(pre[i].toLowerCase());
							sql_whl.append("'");
							if (i + 1 < pre.length) {
                                sql_whl.append(",");
                            }
						}
						sql_whl.append(")");
					}
					// 用户管理范围(含高级授权)
					/*GzAnalyseBo gzbo = new GzAnalyseBo(conn,userView);
					gzbo.setTableName("a");  // 别名
					for (int i = 0; i < pre.length; i++) {
						sql_whl.append(" and ");
						String privcond=gzbo.getDbSQL(pre[i]);
						sql_whl.append(privcond);
					}*/
				}
			}
			// 用户管理范围：业务范围>操作单位>人员范围
			GzAnalyseBo gzbo = new GzAnalyseBo(conn,userView);
//			gzbo.setSpFlag(onlyGzSpFinished?"1":"3");
//			gzbo.setTableName("a");  // 别名
//          String privcond = gzbo.getDbSQL(dbname, category);
//          String privcond = getGzboDbSQL(dbname, category, onlyGzSpFinished?"1":"3", "a", userView);
            String b_units = userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
            String privcond = gzbo.getPrivSQL(userView.isSuper_admin()?"1":"0","a.",category,b_units);
            if(StringUtils.isNotEmpty(privcond)) {
                sql_whl.append(" and " + privcond);
            }
			
			if(onlyGzSpFinished) {
                sql_whl.append(" and a.sp_flag = '06'");
            }

			StringBuffer sql_select = new StringBuffer(" ");
			sql_select.append(" select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1 recidx,");
            }
			sql_select.append("a.A0000,a.A0100,a.B0110,a.E0122,a.NBASE,a.A00Z0,a.A00Z1,a.A00Z2,a.A00Z3");
			if(haveSalaryid){
				sql_select.append(",a.salaryid");
			}
			groupBy.append("");
/*			if (a_code != null && a_code.trim().length() > 1) {
				String codesetid = a_code.substring(0, 2);
				String value = a_code.substring(2);
				if (value != null && value.length() > 0) {
					if (codesetid.equalsIgnoreCase("UN")) {
						sql_whl.append(" and (a.B0110 like '");
						sql_whl.append(value);
						sql_whl.append("%'");
						if (value.equalsIgnoreCase("")) {
							sql_whl.append(" or a.B0110 is null");
						}
						sql_whl.append(")");
					} else if (codesetid.equalsIgnoreCase("UM")) {
						sql_whl.append(" and a.E0122 like '");
						sql_whl.append(value);
						sql_whl.append("%'");
					}

				}
			} */
			String wheredate = declaredate;
			if (wheredate != null && wheredate.trim().length() > 1) {
				sql_whl.append(" and ");
				sql_whl.append(wheredate);
			}

			if (condwhere != null && condwhere.trim().length() > 4) {
				sql_whl.append(" and ");
				sql_whl.append(condwhere);
			}
			ArrayList fielditemlist = getMidVariableList("");
			for (Iterator t = musterCellList.iterator(); t.hasNext();) {
				LazyDynaBean abean = (LazyDynaBean) t.next();
				String gridno = (String) abean.get("gridno");
				String fieldname = null;
				if(abean.get("fieldname") instanceof String) {
                    fieldname = (String) abean.get("fieldname");
                }
				String fieldtype = null;
				if(abean.get("fieldtype") instanceof String) {
                    fieldtype = (String) abean.get("fieldtype");
                }
				String codeid = null;
				if(abean.get("codeid") instanceof String) {
                    codeid = (String) abean.get("codeid");
                }
				String slope = (String) abean.get("slope");
				if (fieldname != null && fieldname.trim().length() > 0) {
					sql_insert.append(",C" + gridno);
					String xx="C" + gridno;
					String[] fields = { fieldname, fieldtype, slope, codeid };
					if ("1".equals(sumFlag)|| "2".equals(sumFlag)) {
						// String itemid = getField(fields);
						FieldItem item = null;
						for (int i = 0; i < fielditemlist.size(); i++) {
							FieldItem fielditem = (FieldItem) fielditemlist.get(i);
							if (fieldname.equalsIgnoreCase(fielditem.getItemid())) {
                                item = fielditem;
                            }
						}
						if (item != null) {
							if (fieldtype != null&& "N".equalsIgnoreCase(fieldtype)) {
								sql_select.append(","+xx);
								group_select.append(",sum(a." + fieldname+ ") as " + xx);
							} else {
								if ("NBASE".equalsIgnoreCase(fieldname)) {
									group_select.append(",(select DBName from dbname where Pre=a."+ fieldname+ ") as "+ xx);
									sql_select.append(","+xx);
								} else{
									fields[0]="a."+fields[0];
									String tt=getField2(fields, fielditemlist,"max");
									//if(tt.toUpperCase().indexOf("AS")==-1||tt.equalsIgnoreCase("a.nbase"))
								    	group_select.append(","+tt+" as "+xx);
									//else
										//group_select.append(","+tt);
									sql_select.append(","+xx);
								}
							}
						} else {
							group_select.append(",null as " + xx);
							sql_select.append(","+xx);
						}
					} else {
						String tt=getField1(fields, fielditemlist);
						//if(tt.toUpperCase().indexOf("AS")==-1||tt.equalsIgnoreCase("a.nbase"))
					    	sql_select.append(","+tt+" as "+xx);
						//else
							//sql_select.append(","+tt);
					}
				}

			}
			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem fielditem = DataDictionary.getFieldItem(groupPointItem);
				if ((fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid())))|| "B0110".equals(groupPointItem)|| "E01A1".equals(groupPointItem)|| "E0122".equals(groupPointItem)) // 采用的硬编码指标
				{
					if ("1".equals(sumFlag)|| "2".equals(sumFlag)) {
						group_select.append(",");
						group_select.append("max(a.");
						group_select.append(groupPointItem);
						group_select.append(") as GroupN"+ ",max(organization.codeitemdesc) as GroupV");
						sql_select.append(",groupN,groupV");
					} else {
						sql_select.append(",");
						sql_select.append("a.");
						sql_select.append(groupPointItem+" as GroupN");
						sql_select.append(",organization.codeitemdesc as GroupV");
					}
					sql_from.append(" left join organization on ");
					sql_from.append("a.");
					sql_from.append(groupPointItem);
					sql_from.append("=organization.codeitemid");
				} else {
					
					if ("1".equals(sumFlag)|| "2".equals(sumFlag)) {

						if (fielditem != null && fielditem.isCode()) {
							group_select.append(",max(a."+ groupPointItem+ ") as GroupN"+ ",max(codeitem.codeitemdesc) as GroupV");
						} else {
							group_select.append(",max(a." + groupPointItem);
							group_select.append(") as GroupN" );
							group_select.append(",max(a." + groupPointItem);
							group_select.append(") as GroupV" );
						}
						sql_select.append(",groupN,groupV");
					} else {
						if (fielditem != null && fielditem.isCode()) {
							sql_select.append(",a." + groupPointItem+ " as GroupN,codeitem.codeitemdesc as GroupV");
						} else {
							sql_select.append(",a." + groupPointItem+" as GroupN");
							sql_select.append(",a." + groupPointItem+" as GroupV");
						}
					}
					if (fielditem != null && fielditem.isCode()) {
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPointItem + "' )) codeitem ");
						sql_from.append(" on   codeitem.codeitemid=" + "a."+ groupPointItem);
					}
				}
				sql_insert.append(",GroupN,GroupV");
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem fielditem = DataDictionary.getFieldItem(this.groupPoint2);
				if ((fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid())))|| "B0110".equals(this.groupPoint2)|| "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					if ("1".equals(sumFlag)|| "2".equals(sumFlag)) {
						group_select.append(",");
						group_select.append("max(a.");
						group_select.append(this.groupPoint2);
						group_select.append(") as GroupN2"+ ",max(org.codeitemdesc) as GroupV2");
						sql_select.append(",groupN2,groupV2");
					} else {
						sql_select.append(",");
						sql_select.append("a.");
						sql_select.append(this.groupPoint2+" as GroupN2");
						sql_select.append(",org.codeitemdesc as GroupV2");
					}
					sql_from.append(" left join organization org on ");
					sql_from.append("a.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				} else {
					
					if ("1".equals(sumFlag)|| "2".equals(sumFlag)) {

						if (fielditem != null && fielditem.isCode()) {
							group_select.append(",max(a."+ this.groupPoint2+ ") as GroupN2"+ ",max(CD.codeitemdesc) as GroupV2");
						} else {
							group_select.append(",max(a." + this.groupPoint2);
							group_select.append(") as GroupN2" );
							group_select.append(",max(a." + this.groupPoint2);
							group_select.append(") as GroupV2" );
						}
						sql_select.append(",groupN2,groupV2");
					} else {
						if (fielditem != null && fielditem.isCode()) {
							sql_select.append(",a." + this.groupPoint2+ " as GroupN2,CD.codeitemdesc as GroupV2");
						} else {
							sql_select.append(",a." + this.groupPoint2+" as GroupN2");
							sql_select.append(",a." + this.groupPoint2+" as GroupV2");
						}
					}
					if (fielditem != null && fielditem.isCode()) {
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD ");
						sql_from.append(" on   CD.codeitemid=" + "a."+ this.groupPoint2);
					}
				}
				sql_insert.append(",GroupN2,GroupV2");
			}
			sql_insert.append(")");
            if("1".equals(sumFlag)|| "2".equals(sumFlag))
            {
            	String temp=this.getSortOrGroupSelectSQL("a", this.sortitem, group_select.toString(), "");
            	if(temp!=null&&temp.trim().length()>0) {
                    group_select.append(","+temp);
                }
            	sql.append(sql_insert.toString());
            	sql.append(sql_select.toString());
            	sql.append(" from ("+group_select);
            	sql.append(sql_from.toString());
            	sql.append(sql_whl.toString()+" "+groupBy.toString());
            	sql.append(") a ");
            	if(sortitem.length()>1){
    				sql.append(getSortStr("a", true));
    			}
    			else
    			{
    				sql.append(" order by a.a0000,a.a00z0,a.a00z1");
    			}
            	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    			{
    				sql.setLength(0);
    				sql.append(sql_insert.toString());
    				sql.append(" select RowNum,b.* from (");
                	sql.append(sql_select.toString());
                	sql.append(" from ("+group_select);
                	sql.append(sql_from.toString());
                	sql.append(sql_whl.toString()+" "+groupBy.toString());
                	sql.append(") a ");
    				if(sortitem.length()>1){
    					sql.append(getSortStr("a", true));
    				}
    				else
    				{
    					sql.append(" order by a.a0000,a.a00z0,a.a00z1");
    				}
    				sql.append(") b");
    			}
            }
            else
            {
            	sql.append(sql_insert.toString());
    			sql.append(sql_select.toString());
    			sql.append(sql_from.toString());
    			sql.append(sql_whl.toString());
      			if(sortitem.length()>1){
    				sql.append(getSortStr("a", true));
    			}
    			else
    			{
    				sql.append(" order by a.a0000,a.a00z0,a.a00z1");
    			}
    			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    			{
    				sql.setLength(0);
    				sql.append(sql_insert.toString());
    				sql.append(" select RowNum,b.* from (");
    				sql.append(sql_select.toString());
    				sql.append(sql_from.toString());
    				sql.append(sql_whl.toString());
    				if(sortitem.length()>1){
    					sql.append(getSortStr("a", true));
    				}
    				else
    				{
    					sql.append(" order by a.a0000,a.a00z0,a.a00z1");
    				}
    				sql.append(") b");
    			}
            }
		
			this.whl_str=sql_whl.toString();

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return sql.toString();
	}
	
   /**
     * SQL语句人员库限制部分（高级授权）
     * 为了避免引用GzAnalyseBo类, 引用会导致放到旧包GzAnalyseBo类不兼容
     * @param pre
     * @return
     * @see GzAnalyseBo#getDbSQL(String, String)
     * @see GzAnalyseBo#getPrivSQL(String, String, String, String)
     * @deprecated 使用GzAnalyseBo#getPrivSQL代替
     */
    private String getGzboDbSQL(String pre,String salaryid, String spFlag, String analyseTable, UserView view) {
        StringBuffer dbSql = new StringBuffer();
        boolean isBhighPriv=view.isBhighPriv();//是否定义了高级权限
        try 
        {
            //采用下面这种方式，防止有人员移库现象导致的看不见这个人问题
            if(pre!=null&&pre.length()>0)
            {
                if (pre.indexOf(",") == -1) {

                    /**加入高级授权*/
                    StringBuffer sql = new StringBuffer("");
                    String priStrSql = InfoUtils.getWhereINSql(view, pre);
                    sql.append("select "+pre+"a01.A0100 ");
                    if (priStrSql.length() > 0) {
                        sql.append(priStrSql);
                    } else {
                        sql.append(" from "+pre+"a01");
                    }
                    
                    String sql1=""+analyseTable+".A0100 in ("+sql+")";
                    if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
                    {
                        dbSql.append("(upper("+analyseTable+".nbase)='");
                        dbSql.append(pre.toUpperCase()+"')");
                    }
                    else
                    {
                        if(isBhighPriv){
                            dbSql.append("(upper("+analyseTable+".nbase)='");
                            dbSql.append(pre.toUpperCase()+"'");                            
                            dbSql.append(" and "+sql1+")");//走高级了   zhaoxg 2013-7-18
                        }else{

                            StringBuffer _sql=new StringBuffer();
                            String b_units=view.getUnitIdByBusi("1");
                            if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
                            {
                                String unitarr[] =b_units.split("`");
                                for(int i=0;i<unitarr.length;i++)
                                {
                                    String codeid=unitarr[i];
                                    if(codeid==null|| "".equals(codeid)) {
                                        continue;
                                    }
                                    if(codeid!=null&&codeid.trim().length()>2)
                                    {


                                    //-----
                                    String b0110_item="b0110";
                                    String e0122_item="e0122";
                                    
                                    if (i == 0) {
//                                      _sql.append("(");
                                    }else{
                                        _sql.append(" or ");
                                    }
                                    
                                        String[] temp = salaryid.split(",");
                                        for (int j = 0; j < temp.length; j++) {
                                            SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
                                            String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
                                            String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
                                            if(orgid!=null&&orgid.trim().length()>0)
                                            { 
                                                 b0110_item=orgid;
                                                if(deptid!=null&&deptid.trim().length()>0) {
                                                    e0122_item=deptid;
                                                } else {
                                                    e0122_item="";
                                                }
                                            }
                                            else if(deptid!=null&&deptid.trim().length()>0)
                                            { 
                                                e0122_item=deptid;
                                                b0110_item="";
                                            }
                                              String codesetid = codeid.substring(0,2);
                                              String value = codeid.substring(2);
                                               

                                            _sql.append("("+analyseTable+".salaryid=");
                                            _sql.append(temp[j]);
                                            
                                            
                                             if ("UN".equalsIgnoreCase(codesetid))
                                                {
                                                    
                                                    if(b0110_item.length()>0)   
                                                    {
                                                        _sql.append(" and ("+analyseTable+"."+b0110_item+" like '");
                                                        _sql.append(value);
                                                        _sql.append("%'");
                                                        if ("".equalsIgnoreCase(value))
                                                        {
                                                            _sql.append(" or "+analyseTable+"."+b0110_item+" is null");
                                                        }
                                                        _sql.append(")");
                                                    }
                                                    else 
                                                    {
                                                        _sql.append(" and ("+analyseTable+"."+e0122_item+" like '");
                                                        _sql.append(value);
                                                        _sql.append("%'");
                                                        if ("".equalsIgnoreCase(value))
                                                        {
                                                            _sql.append(" or "+analyseTable+"."+e0122_item+" is null");
                                                        }
                                                        _sql.append(")");
                                                    }
                                                }
                                                if ("UM".equalsIgnoreCase(codesetid))
                                                {
                                                    if(e0122_item.length()>0)   
                                                    {
                                                        _sql.append(" and ("+analyseTable+"."+e0122_item+" like '");
                                                        _sql.append(value);
                                                        _sql.append("%')");
                                                    }
                                                }
                                            
                                                _sql.append(")");
                                            if (j != temp.length - 1){
                                                _sql.append(" OR ");
                                            }                                           

                                        }
                                    //-----
//                                      _sql.append(")");
                                    }
                                    else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
                                    {
                                        _sql.append("  1=1 ");
                                    }   
                                }
                                
                            }
                            dbSql.append("(upper("+analyseTable+".nbase)='");
                            dbSql.append(pre.toUpperCase()+"'");                            
                            dbSql.append(" and ("+_sql+"))");                   
                        }
                    }
                 
                }
                else
                {
                    if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
                    {
                        String[] temp = pre.split(",");
                        for (int i = 0; i < temp.length; i++) {
                            if (i == 0) {
                                dbSql.append("(");
                            }
                            dbSql.append("upper(nbase)='");
                            dbSql.append(temp[i].toUpperCase()+"'");
                            if (i != temp.length - 1) {
                                   dbSql.append(" OR ");
                                } else {
                                dbSql.append(")");
                            }
                        }
                    }
                    else
                    {
                        if(isBhighPriv){
                            String[] temp = pre.split(",");
                            for (int i = 0; i < temp.length; i++) {
                                if (i == 0) {
                                    dbSql.append("(");
                                }
                                StringBuffer sql = new StringBuffer("");
                                String priStrSql = InfoUtils.getWhereINSql(view, temp[i]);
                                sql.append("select "+temp[i]+"a01.A0100 ");
                                if (priStrSql.length() > 0) {
                                    sql.append(priStrSql);
                                } else {
                                    sql.append(" from "+temp[i]+"a01");
                                }
                                String sql1=""+analyseTable+".A0100 in ("+sql+")";
                                dbSql.append("(upper(nbase)='");
                                dbSql.append(temp[i].toUpperCase()+"'");
                                dbSql.append(" and "+sql1+")");
                                if (i != temp.length - 1) {
                                   dbSql.append(" OR ");
                                } else {
                                    dbSql.append(")");
                                }
                            }
                        }else{

                            StringBuffer _sql=new StringBuffer();
                            String b_units=view.getUnitIdByBusi("1");
                            if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
                            {
                                String unitarr[] =b_units.split("`");
                                for(int i=0;i<unitarr.length;i++)
                                {
                                    String codeid=unitarr[i];
                                    if(codeid==null|| "".equals(codeid)) {
                                        continue;
                                    }
                                    if(codeid!=null&&codeid.trim().length()>2)
                                    {


                                    //-----
                                    String b0110_item="b0110";
                                    String e0122_item="e0122";
                                    
                                    if (i == 0) {
//                                      _sql.append("(");
                                    }else{
                                        _sql.append(" or ");
                                    }
                                    
                                        String[] temp = salaryid.split(",");
                                        for (int j = 0; j < temp.length; j++) {
                                            SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
                                            String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
                                            String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
                                            if(orgid!=null&&orgid.trim().length()>0)
                                            { 
                                                 b0110_item=orgid;
                                                if(deptid!=null&&deptid.trim().length()>0) {
                                                    e0122_item=deptid;
                                                } else {
                                                    e0122_item="";
                                                }
                                            }
                                            else if(deptid!=null&&deptid.trim().length()>0)
                                            { 
                                                e0122_item=deptid;
                                                b0110_item="";
                                            }
                                              String codesetid = codeid.substring(0,2);
                                              String value = codeid.substring(2);
                                               

                                            _sql.append("("+analyseTable+".salaryid=");
                                            _sql.append(temp[j]);
                                            
                                            
                                             if ("UN".equalsIgnoreCase(codesetid))
                                                {
                                                    
                                                    if(b0110_item.length()>0)   
                                                    {
                                                        _sql.append(" and ("+analyseTable+"."+b0110_item+" like '");
                                                        _sql.append(value);
                                                        _sql.append("%'");
                                                        if ("".equalsIgnoreCase(value))
                                                        {
                                                            _sql.append(" or "+analyseTable+"."+b0110_item+" is null");
                                                        }
                                                        _sql.append(")");
                                                    }
                                                    else 
                                                    {
                                                        _sql.append(" and ("+analyseTable+"."+e0122_item+" like '");
                                                        _sql.append(value);
                                                        _sql.append("%'");
                                                        if ("".equalsIgnoreCase(value))
                                                        {
                                                            _sql.append(" or "+analyseTable+"."+e0122_item+" is null");
                                                        }
                                                        _sql.append(")");
                                                    }
                                                }
                                                if ("UM".equalsIgnoreCase(codesetid))
                                                {
                                                    if(e0122_item.length()>0)   
                                                    {
                                                        _sql.append(" and ("+analyseTable+"."+e0122_item+" like '");
                                                        _sql.append(value);
                                                        _sql.append("%')");
                                                    }
                                                }
                                            
                                                _sql.append(")");
                                            if (j != temp.length - 1){
                                                _sql.append(" OR ");
                                            }                                           

                                        }
                                    //-----
//                                      _sql.append(")");
                                    }
                                    else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
                                    {
                                        _sql.append("  1=1 ");
                                    }   
                                }
                                
                            }
                            String[] temp = pre.split(",");
                            for (int i = 0; i < temp.length; i++) {
                                if (i == 0) {
                                    dbSql.append("((");
                                }
                                dbSql.append("upper(nbase)='");
                                dbSql.append(temp[i].toUpperCase()+"'");
                                if (i != temp.length - 1) {
                                   dbSql.append(" OR ");
                                } else {
                                    dbSql.append(")");
                                }
                            }                       
                            dbSql.append(" and ("+_sql+"))");                   
                        }

                    }
                }
            }
            else
            {
                dbSql.append(" 1=2 ");
            }
            if("3".equals(spFlag)){
                //System.out.println("包含审批");
            }else if("1".equals(spFlag)){  // 
                dbSql.append(" and "+analyseTable+".sp_flag = '06'");  // 支持表别名
            }else if(analyseTable!=null&& "salaryhistory".equalsIgnoreCase(analyseTable)){
                dbSql.append(" and salaryhistory.sp_flag = '06'");//06:结束
                //System.out.println("不包含审批");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbSql.toString();
    }
	
	/**
	 * 取分组指标可选层级
	 * @param codesetid
	 * @return
	 */
	public ArrayList getGroupLayer(String itemid, String codesetid){
		ArrayList layerlist = new ArrayList();
		CommonData dataobj = new CommonData("","");
		layerlist.add(dataobj);
		
		// 机构花名册按B0110或类似指标分组，可以取某层级单位或部门
		if(("21".equals(modelFlag))&&("B0110".equalsIgnoreCase(itemid))){
			getGroupLayer0(layerlist, "UN", true);
			getGroupLayer0(layerlist, "UM", true);
		}
		else {
            getGroupLayer0(layerlist, codesetid, false);
        }
		return layerlist;
	}
	
	private ArrayList getGroupLayer0(ArrayList layerlist, String codesetid, boolean isOrgB0110){
		ContentDAO dao = new ContentDAO(conn);
		CommonData dataobj = null;
		String val = null;
		String name = null;
		try {
			RowSet rowset = dao.search("select layer from organization where codesetid='"+codesetid+"' group by layer order by layer");
			while(rowset.next()){
				int layer = rowset.getInt("layer");
				if(layer>0){
					if(isOrgB0110){
						val = layer+","+codesetid;
						if("UN".equalsIgnoreCase(codesetid)) {
                            name = layer+"级单位";
                        } else {
                            name = layer+"级部门";
                        }
					}
					else{
						val = layer+"";
						name = layer+"";
					}
					dataobj = new CommonData(val , name);
					layerlist.add(dataobj);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return layerlist;
	}	

	/**
	 * 从临时变量中取得对应指标列表
	 * 
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String salaryid) {
		ArrayList fieldlist = new ArrayList();
		RowSet rset = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(salaryid);
			buf.append("')");
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(buf.toString());
			while (rset.next()) {
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");// 没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch (rset.getInt("ntype")) {
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			String sqlstr = "select * from salaryset";
			if (salaryid != null && salaryid.trim().length() > 0) {
				sqlstr += " where salaryid=" + salaryid;
			}
			rset = dao.search(sqlstr);
			while (rset.next()) {
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("ITEMID"));
				item.setItemdesc(rset.getString("ITEMDESC"));
				item.setFieldsetid(rset.getString("FIELDSETID"));
				item.setItemlength(rset.getInt("ITEMLENGTH"));
				item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
				item.setDecimalwidth(rset.getInt("DECWIDTH"));
				item.setItemtype(rset.getString("ITEMTYPE"));
				item.setVarible(1);
				fieldlist.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldlist;
	}

	/**
	 * 取得花名册不用计算的数据字段
	 * 
	 * @param tabid
	 * @return
	 */
	public ArrayList getMusterCellListSa(String tabid) {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select GridNo,SetName,Field_Name,Field_Type,Flag,codeid,slope from muster_cell where flag!='G' and flag!='R' and flag!='H'  and flag!='C' and flag!='S' and flag!='P'  and tabid="+ tabid);
			while (rowSet.next()) {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("gridno", rowSet.getString("gridno"));
				abean.set("setname", rowSet.getString("SetName"));
				abean.set("fieldname", rowSet.getString("Field_Name"));
				abean.set("fieldtype", rowSet.getString("Field_Type"));
				abean.set("codeid", rowSet.getString("codeid"));
				abean.set("flag", rowSet.getString("Flag"));
				abean.set("slope", rowSet.getString("slope"));
				list.add(abean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;

	}

	/**
	 * 取得输出报表的格式信息
	 * 
	 * @param tabid
	 * @return
	 */
	public HashMap getCfactor(String tabid) {
		HashMap map = new HashMap();
		//ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			HmusterXML hxml= new HmusterXML(this.conn,tabid);
			String one = hxml.getValue(HmusterXML.SPILTCOLUMN);
			if (one != null && one.length() > 0) {
				String[] arg = one.split(":");
				if (arg.length > 0) {
                    map.put("isColumn", arg[0]); // 是否分栏
                } else if (arg.length > 1) {
                    map.put("columnAspect", arg[1]); // 分栏方向
                } else if (arg.length > 2) {
                    map.put("columnSpace", arg[2]); // 栏间距
                } else if (arg.length > 3) {
                    map.put("isLine", arg[3]); // 是否打印分隔线
                }
			}
			String two = hxml.getValue(HmusterXML.GROUPFIELD);
			if (two != null && two.trim().length() > 0) {
				map.put("groupN", two);
			}
			String two2 = hxml.getValue(HmusterXML.GROUPFIELD2);
			if (two2 != null && two2.trim().length() > 0) {
				map.put("groupN2", two2);
			}
			String three = hxml.getValue(HmusterXML.MULTIGROUPS);
			if (three != null && three.trim().length() > 0) {
				map.put("multipleGroupN", three);
			}
			String s = hxml.getValue(HmusterXML.ShowChangedOrgPersons);
			if (s == null || s.length() == 0) {
                s = "False";
            }
			map.put("ShowChangedOrgPersons", s);
            s = hxml.getValue(HmusterXML.ShowChangedOrgNbases);
            if (s == null) {
                s = "";
            }
            map.put("ShowChangedOrgNbases", s);
			
			/*rowSet = dao.search("select cfactor from muster_name where tabid="
					+ tabid);
			// <SPILTCOLUMN>0:1:12:0</SPILTCOLUMN><GROUPFIELD></GROUPFIELD><MULTIGROUPS>0</MULTIGROUPS>
			// MULTIGROUPS :分组不分页 小耿 后加的
			if (rowSet.next()) {
				String cfactor = Sql_switcher.readMemo(rowSet, "cfactor");
				if (cfactor != null && cfactor.trim().length() > 0) {
					String a = cfactor.replaceAll("<SPILTCOLUMN>", "");
					a = a.replaceAll("</SPILTCOLUMN>", "");
					String one = null;
					String two = null;
					String three = null;
					if (a.indexOf("<GROUPFIELD>") != -1) {
						one = a.substring(0, a.indexOf("<GROUPFIELD>"));

						a = a.substring(a.indexOf("<GROUPFIELD>"));
						a = a.replaceAll("<GROUPFIELD>", "");
						a = a.replaceAll("</GROUPFIELD>", "");
						if (a.indexOf("<MULTIGROUPS>") != -1) {
							two = a.substring(0, a.indexOf("<MULTIGROUPS>"));
							a = a.substring(a.indexOf("<MULTIGROUPS>"));
							a = a.substring(0, a.indexOf("</MULTIGROUPS>"));
							a = a.replaceAll("<MULTIGROUPS>", "");
							a = a.replaceAll("</MULTIGROUPS>", "");
							three = a;
						} else
							two = a;
					} else
						one = a;
					if (one != null && one.length() > 0) {
						String[] arg = one.split(":");
						if (arg.length > 0)
							map.put("isColumn", arg[0]); // 是否分栏
						else if (arg.length > 1)
							map.put("columnAspect", arg[1]); // 分栏方向
						else if (arg.length > 2)
							map.put("columnSpace", arg[2]); // 栏间距
						else if (arg.length > 3)
							map.put("isLine", arg[3]); // 是否打印分隔线
					}
					if (two != null && two.trim().length() > 0) {
						map.put("groupN", two);
					}
					if (three != null && three.trim().length() > 0) {
						map.put("multipleGroupN", three);
					}

				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 返回花名册列表项
	 * 
	 * @param tabid
	 * @return
	 */
	public ArrayList getMusterCellList(String tabid) {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select * from muster_Cell where tabid="+ tabid);
			list = dao.getDynaBeanList(rowSet);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	private String showPartJob="false";
	public void setShowPartJob(String showPartJob)
	{
		this.showPartJob=showPartJob;
	}
	// 取得权限控制语句
	public String getPrivCondition(String infor_Flag, String dbpre) {
		StringBuffer privConditionStr = new StringBuffer("");
		try {
			if (!userView.isSuper_admin()) {
				if ("1".equals(infor_Flag)&& "false".equalsIgnoreCase(this.getNo_manger_priv())) // 人员库
				{
					String conditionSql = " select "+dbpre+"A01.A0100 "+ userView.getPrivSQLExpression(dbpre, true); //userview 取权限sql 转小写后 查询单位部门权限时 单位部门代码字母转换后 导致查询会有问题
					// 28421  如果没有管理范围权限 就只查看登录用户
					if(this.userView.getStatus()==4&&(this.userView.getManagePrivCodeValue()==null||"".equals(this.userView.getManagePrivCodeValue()))){
						if(!"UN".equalsIgnoreCase(this.userView.getManagePrivCode())) {//自助用户 人员范围设置顶级节点时 this.userView.getManagePrivCodeValue()会为空  判断this.userView.getManagePrivCode()是否为UN UN则为顶级节点
							if(conditionSql.indexOf("WHERE")!=-1) {//兼容 防止获取到的权限sql where为小写非大写
								conditionSql=conditionSql.substring(0, conditionSql.indexOf("WHERE"));
								conditionSql=conditionSql+" WHERE "+dbpre+"A01.A0100 ='"+this.userView.getA0100()+"'";
							}else if(conditionSql.indexOf("where")!=-1) {
								conditionSql=conditionSql.substring(0, conditionSql.indexOf("where"));
								conditionSql=conditionSql+" WHERE "+dbpre+"A01.A0100 ='"+this.userView.getA0100()+"'";
							}
							
						}
					}
					/**加入兼职人员*/
					if("true".equalsIgnoreCase(this.showPartJob))
					{
			    		String parttimerSQL =""; 
			    		 if(userView.getManagePrivCodeValue()!=null) {
                             parttimerSQL=this.getQueryFromPartLike(userView, dbpre, userView.getManagePrivCodeValue());
                         }
				    	 if(parttimerSQL!=null&&!"".equals(parttimerSQL))
				    	 {
					    	 conditionSql+=" or ("+parttimerSQL+")";
				    	 }
					}
					privConditionStr.append(" in (" + conditionSql + " )");
				}
				
/*				String codesetid = userView.getManagePrivCode();
				String codeValue = userView.getManagePrivCodeValue();
				if (infor_Flag.equals("2")) // 2：机构
				{
					String conditionSql = " select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and  codeitemid like '"+ codeValue + "%'";
					privConditionStr.append(" in (" + conditionSql + " )");
				}

				if (infor_Flag.equals("3")) // 3：职位
				{
					String conditionSql = " select codeitemid from organization  where codesetid='@K' and  codeitemid like '"+ codeValue + "%'";
					privConditionStr.append("  in (" + conditionSql + " )");
				}
*/
				if("2".equals(infor_Flag) || "3".equals(infor_Flag)) {
			        String codevalue="";
			        codevalue=userView.getUnitIdByBusi("4");
			        String[] valuearr=StringUtils.split(codevalue,"`");
			        if(valuearr.length==0) {
                        return "";
                    }
			        StringBuffer value=new StringBuffer();
			        if ("2".equals(infor_Flag)) // 2：机构
                    {
                        value.append("select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and  ");
                    } else {
                        value.append("select codeitemid from organization  where codesetid='@K' and ");
                    }
			        value.append("(");
			        for(int i=0;i<valuearr.length;i++)
			        {
			          if(i!=0) {
                          value.append(" or ");
                      }
		              value.append(" codeitemid like '");          
			          value.append(valuearr[i].substring(2));
			          value.append("%'");
			        }
			        value.append(")");
			        privConditionStr.append("  in (" + value + " )");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privConditionStr.toString();
	}
	
	
	/***
	 * 校验花名册模板是否全部插入视图指标,符合条件花名册临时表内数据筛选
	 * **/
	public void filterdata(String tabid,String musname){
		ContentDAO dao=new ContentDAO(this.conn);
		ResultSet rs=null;
		ArrayList<String> viewList=new ArrayList<String>();
		boolean flag=true;
		try {
			//如果花名册插入的指标都是视图指标则按照视图内数据过滤花名册临时表中数据，花名册临时表A0100与视图A0100对应
			rs=dao.search("select distinct setName from muster_cell where tabid="+tabid);
			while(rs.next()){
				if(StringUtils.isNotEmpty(rs.getString("setName"))&&rs.getString("setName").toUpperCase().startsWith("V_EMP")){
					viewList.add(rs.getString("setName"));
				}else{
					flag=false;
				}
			}
			if(flag){//模板指标存在除视图外的指标不执行
				StringBuffer sbf=new StringBuffer();
				if(viewList.size()>0){//有可能有多个视图指标  多个视图去重查询a0100 过滤花名册临时表内不符合的数据
					sbf.append(" delete from "+musname+"  where  not exists ( ");
					sbf.append(" select distinct a0100,nbase from ( ");
					for (int i = 0; i < viewList.size(); i++) {
						sbf.append(" select A0100,nbase from "+viewList.get(i));
						if(i<viewList.size()-1) {
                            sbf.append("  union ");
                        }
					}
					sbf.append(" ) v where v.a0100="+musname+".a0100 and Lower(v.nbase)=Lower("+musname+".nbase)");
					
					sbf.append(" )");
					dao.update(sbf.toString());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}
	

	/**
	 * 根据条件从档案库中导入数据到高级花名册的临时表中
	 * 对人员、职位、机构花名册进行操作
	 * @param tabid
	 *            高级花名册的id
	 * @param infor_Flag
	 *            信息群标识
	 * @param dbpre
	 *            应用库表前缀
	 * @param queryScope
	 *            查询的范围
	 * @param flag
	 *            "0":无 "1"有子集指标无年月标识,可按最后一条历史纪录查 "2"有子集指标无年月标识,可按取部分历史纪录查
	 *            "3"有子集指标和年月标识，可按某次的历史纪录查
	 * @param year
	 *            年;month 月;count 次
	 * @param fromScope
	 * @param toScope
	 * @param selectedPoint
	 * @param isGroupPoint
	 *            是否选用分组指标 1:选用
	 * @param groupPoint;
	 *            已选的分组指标
	 * @return void
	 * @author dengc created: 2003/03/22
	 * 
	 */

	public String importData(String history, String userName, String tableName,
			String tabid, String infor_flag, String dbpre, String queryScope,
			String flag, String year, String month, String count,
			String fromScope, String toScope, String selectedPoint,
			String isGroupPoint, String groupPoint, UserView userView,
			String modelFlag) throws GeneralException {
		this.userView = userView;
		this.modelFlag = modelFlag;
		this.selectedPoint=selectedPoint;
		if ("3".equals(modelFlag) || "21".equals(modelFlag)|| "41".equals(modelFlag)|| "1".equals(modelFlag) || "4".equals(modelFlag)|| "2".equals(modelFlag)) {
            this.privConditionStr = getPrivCondition(infor_flag, dbpre);
        }

		String isSuccess = "1"; // 成功
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 创建临时表 */
			DbWizard dbWizard = new DbWizard(this.conn);
            MusterBo mbo = new MusterBo(this.conn);
			Table temp_table = createMusterTempTable(tableName, tabid, dbWizard,groupPoint,this.groupPoint2,mbo);
			
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tableName);
			
			// if(!this.fieldIsExist)
			// return "2"; //表中有指标没构库
			String sql = "";
			if("1".equals(countflag)){
				sql = createCountSQL(history, userName, tableName, tabid,infor_flag, dbpre, queryScope, flag, year, month, count,fromScope, toScope, selectedPoint, isGroupPoint, groupPoint);
			}else{
				sql = createSQL2(history, userName, tableName, tabid,infor_flag, dbpre, queryScope, flag, year, month, count,fromScope, toScope, selectedPoint, isGroupPoint, groupPoint);
			}
			 if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
	          {
	     		HmusterBo.createMusterRecidx(conn, tableName);
	          }
			dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表
		
			if("1".equals(infor_flag)&&this.personViewName.size()>0)//针对视图
			{
				this.updatePersonView(history, tableName, dbpre, year, month, count,fromScope,toScope);
				filterdata(tabid, tableName);
			}
			
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
            {
		     	HmusterBo.updateMusterRecidxS(this.conn,tableName);
            }
//			dbWizard.addPrimaryKey(temp_table);
            this.transformMidvariable(dbpre, tabid, tableName);
            transFoumlorGeidno(tabid, tableName, infor_flag,userName);
            //计算公式
			runCountFormula(tableName, tabid, userView, infor_flag, dbpre,year, month, count, history, queryScope, userName);
			transformCode(tabid, tableName, infor_flag); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
			photoCreatClum(tabid,tableName,dbpre,dbWizard);//添加相片字段
			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				layerOrg(tableName,groupPoint,1,groupOrgCodeSet);
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				layerOrg(tableName,this.groupPoint2,2,groupOrgCodeSet2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}

		return isSuccess;

	}
	
	private String getOrderExFlds(String dbpre, String flds) {
	    String result="";
        sortitem=sortitem!=null?sortitem:"";
        String arr[] = sortitem.split("`");
        for(int i=0;i<arr.length;i++){
            if(arr[i]!=null&&arr[i].trim().length()>0){
                String[] itemarr = arr[i].split(":");
                if(itemarr!=null&&itemarr.length==3){
                    FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
                    if(item!=null){
                        String fld = itemarr[0];
                        /* 标识：2036 凤凰传媒：高级花名册，设置了多行数据区，设置了单位、部门、岗位排序指标，插入了照片，设置了自动取数，bs前台取不出来数据，后台报错 xiaoyun 2014-5-29 start */
                        //if(flds.indexOf(fld)==-1){
                            //if(result.length()>0)
                              //  result+=",";
                            if(("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
                                    "@K".equalsIgnoreCase(item.getCodesetid()))) {
                            	if(flds.indexOf("ORG" + i + fld) == -1) {
                            		if(result.length()>0) {
                            			result+=",";
                            		}
                            		if(!"1".equals(this.countflag)) {
                                        result+= " organization"+i+".a0000 as ORG" + i + fld;
                                    } else//汇总时不用organization
                                    {
                                        result+=(dbpre+item.getFieldsetid())+"."+item.getItemid()+" ";//fieldsetStr.append((dbpre+item.getFieldsetid())+"."+item.getItemid()+" ");
                                    }
                            	}
                            }
                            else {
                            	if(flds.indexOf(fld)==-1){
                            		if(result.length()>0) {
                            			result+=",";
                            		}
                            		result+=dbpre+item.getFieldsetid()+"."+fld;
                            	}
                            }
                        //}
                        /* 标识：2036 凤凰传媒：高级花名册，设置了多行数据区，设置了单位、部门、岗位排序指标，插入了照片，设置了自动取数，bs前台取不出来数据，后台报错 xiaoyun 2014-5-29 end */
                    }
                }
            }
        }
	    return result;
	}
	
	private void importAllDBDataSort(String history, String userName, String tableName,
            String tabid, String infor_flag, ArrayList dblist, String queryScope,
            String flag, String year, String month, String count,
            String fromScope, String toScope, String selectedPoint,
            String isGroupPoint, String groupPoint, UserView userView,
            String modelFlag) {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            DbWizard dbWizard = new DbWizard(this.conn);

            String sql = "";
            String insertFlds = "";
            String sqlUnion = "";
           // String order = getSortStr("ALL","1");
            String musterFlds = "";
    	    for(int i=0;i<dblist.size();i++)
            {
                String dbpre = ((CommonData)dblist.get(i)).getDataValue();
                if("ALL".equals(dbpre)) {
                    continue;
                }
                this.privConditionStr = getPrivCondition(infor_flag, dbpre);
                orderLeftJoin.setLength(0);
                if("1".equals(countflag)){
                    sql = createCountSQL(history, userName, tableName, tabid,infor_flag, dbpre, queryScope, flag, year, month, count,fromScope, toScope, selectedPoint, isGroupPoint, groupPoint);
                }else{
                    sql = createSQL2(history, userName, tableName, tabid,infor_flag, dbpre, queryScope, flag, year, month, count,fromScope, toScope, selectedPoint, isGroupPoint, groupPoint);
                }
                if(sqlUnion.length()>0) {
                    sqlUnion+=" union all ";
                }
                String sqlSelect=curFlds;
                String orderExFlds=getOrderExFlds(dbpre, curMusterFlds);
                if(orderExFlds.length()>0&&!"1".equals(this.countflag)) {
                    sqlSelect+=","+orderExFlds;
                }
                sqlSelect+=" "+curFrom;
                sqlUnion+=sqlSelect;
                insertFlds = curInsertFlds;
            }
            musterFlds = curMusterFlds;
            String order = getSortStr("ALL","1",history);  // order by  AllA01.b0110 ASC,ALLA01.a0183 ASC,A0000
           
            
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
            {
                musterFlds = musterFlds.replaceFirst("recidx,", "");  // 去掉recidx, 使用RowNum代替
                /* 标识：2018 全部人员库时，并没有按内部编号的升序规则排序 xiaoyun 2014-5-29 start */
                //sql=insertFlds+" select RowNum,"+musterFlds+" from ("+sqlUnion+") ALLA01 " + order;
                sql=insertFlds+" select RowNum,"+musterFlds+" from (select " + musterFlds + " from ("+sqlUnion+") ALLA01 " + order+") ALLA01";
                /* 标识：2018 全部人员库时，并没有按内部编号的升序规则排序 xiaoyun 2014-5-29 end */
            }
            else
            {
                sql=insertFlds+" select "+musterFlds+" from ("+sqlUnion+") ALLA01 " + order;
            }
            dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
            {
                HmusterBo.updateMusterRecidxS(this.conn,tableName);
            }
        
            for(int i=0;i<dblist.size();i++)
            {
                String dbpre = ((CommonData)dblist.get(i)).getDataValue();
                if("ALL".equals(dbpre)) {
                    continue;
                }
                this.privConditionStr = getPrivCondition(infor_flag, dbpre);
                if("1".equals(infor_flag)&&this.personViewName.size()>0)
                {
                    this.updatePersonView(history, tableName, dbpre, year, month, count,fromScope,toScope);
                    filterdata(tabid, tableName);
                }
                
                this.transformMidvariable(dbpre, tabid, tableName);
                runCountFormula(tableName, tabid, userView, infor_flag, dbpre,year, month, count, history, queryScope, userName);
                photoCreatClum(tabid,tableName,dbpre,dbWizard);//添加相片字段
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    /**
     * 人员高级花名册导入多个人员库数据
     * 
     */
    public String importAllDBData(String history, String userName, String tableName,
            String tabid, String infor_flag, ArrayList dblist, String queryScope,
            String flag, String year, String month, String count,
            String fromScope, String toScope, String selectedPoint,
            String isGroupPoint, String groupPoint, UserView userView,
            String modelFlag) throws GeneralException {
        this.userView = userView;
        this.modelFlag = modelFlag;
        this.selectedPoint=selectedPoint;

        String isSuccess = "1";
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            /** 创建临时表 */
            DbWizard dbWizard = new DbWizard(this.conn);
            MusterBo mbo = new MusterBo(this.conn);
            Table temp_table = createMusterTempTable(tableName, tabid, dbWizard,groupPoint,this.groupPoint2,mbo);
           
            DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tableName);
            
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
            {
              HmusterBo.createMusterRecidx(conn, tableName);
            }

            // 默认排序
            if(sortitem==null||sortitem.length()==0) {
                for(int i=0;i<dblist.size();i++)
                {
                    String dbpre = ((CommonData)dblist.get(i)).getDataValue();
                    if("ALL".equals(dbpre)) {
                        continue;
                    }
                    try{
                        int reccnt = 0;
                        if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            reccnt = recCount(tableName);
                        }
                        this.privConditionStr = getPrivCondition(infor_flag, dbpre);
                        this.tmpStr="";//查询全部人员库 切换人员库 参数置空
                        // if(!this.fieldIsExist)
                        // return "2"; //表中有指标没构库
                        String sql = "";
                        if("1".equals(countflag)){
                            sql = createCountSQL(history, userName, tableName, tabid,infor_flag, dbpre, queryScope, flag, year, month, count,fromScope, toScope, selectedPoint, isGroupPoint, groupPoint);
                        }else{
                            sql = createSQL2(history, userName, tableName, tabid,infor_flag, dbpre, queryScope, flag, year, month, count,fromScope, toScope, selectedPoint, isGroupPoint, groupPoint);
                        }
                        dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表
                        if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
                        {
                            updateMusterRecidx2(this.conn,tableName, dbpre);
                        }
                        else {
                            if(reccnt>0)// ORACLE RowNum从1开始, 因此需要: recidx = RowNum + reccnt
                            {
                                updateMusterRecidx3(this.conn,tableName, dbpre, reccnt);
                            }
                        }
                    
                        if("1".equals(infor_flag)&&this.personViewName.size()>0)
                        {
                            this.updatePersonView(history, tableName, dbpre, year, month, count,fromScope,toScope);
                            filterdata(tabid, tableName);
                        }
                        
        //              dbWizard.addPrimaryKey(temp_table);
                        this.transformMidvariable(dbpre, tabid, tableName);
                        runCountFormula(tableName, tabid, userView, infor_flag, dbpre,year, month, count, history, queryScope, userName);
                        photoCreatClum(tabid,tableName,dbpre,dbWizard);//添加相片字段
                    }catch(Exception e){
                        isSuccess = "0";
                        e.printStackTrace();
                    }
                }
                if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                    dropSeqs(conn, tableName);
                }
            }
            else{
                importAllDBDataSort(history, userName, tableName, tabid, infor_flag, dblist, queryScope, flag, year, month, count,
                        fromScope, toScope, selectedPoint, isGroupPoint, groupPoint, userView, modelFlag);
            }
            transformCode(tabid, tableName, infor_flag); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
            if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
            {
                layerOrg(tableName,groupPoint,1,groupOrgCodeSet);
            }
            if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
            {
                layerOrg(tableName,this.groupPoint2,2,groupOrgCodeSet2);
            }
        } catch (Exception e) {
            isSuccess = "0";
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
        }

        return isSuccess;

    }
    
	private String tmpStr="";
	public void updatePersonView(String history, String tableName,
			String dbpre,String year, String month, String count,String fromCope,String toScope)
	{
		try
		{
			Set keySet = this.personViewName.keySet();
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard db = new DbWizard(conn);
			for(Iterator it=keySet.iterator();it.hasNext();)
			{
				String key=(String)it.next();	
				if(this.queryField.toUpperCase().indexOf(key.toUpperCase())==-1||this.queryField.startsWith("V_EMP_"))
				{
					tmpStr="_tmp";
					ArrayList list = (ArrayList)this.personViewName.get(key);
					StringBuffer insert_buf = new StringBuffer("");
					StringBuffer select_buf = new StringBuffer("");
					StringBuffer tmpBuf=new StringBuffer(key+".a0100");
					if("2".equals(history)) {
						select_buf.append("i9999");
						tmpBuf.append(","+key+".i9999");
					}
					StringBuffer mssql = new StringBuffer();
					this.queryField+=key.toUpperCase();
					for(int i=0;i<list.size();i++)
					{
						LazyDynaBean bean = (LazyDynaBean)list.get(i);
						String gridno = (String)bean.get("gridno");
						String str=(String)bean.get("str");
						String[] s=str.split("/");
						String field="";
						if("1".equals(this.countflag)) {
                            field=this.getCountField(s, dbpre);
                        } else {
							field=this.getField(s, dbpre);
							if(field.toUpperCase().indexOf("END")>0)  // 日期型加别名
                            {
                                field = field + " as " + s[0] +"_time";
                            }
							if("nbase".equalsIgnoreCase(s[0])&&(field.toUpperCase().indexOf("NBASE")==-1)) {
                                field = field + " as " + s[0];
                            }
						}
						
						if(i!=0)
						{
							insert_buf.append(",");
							mssql.append(",");
						}
						mssql.append("C"+gridno+"=S."+s[0]);
						if("D".equalsIgnoreCase(s[1])) {
                            mssql.append("_time");
                        }
						insert_buf.append("T.C"+gridno);
						if(!"a0100".equalsIgnoreCase(s[0])) {
						    if(select_buf.length()>0) {
                                select_buf.append(",");
                            }
	                        select_buf.append(field);
						    tmpBuf.append(","+key+"."+s[0]);
						}
					}
					StringBuffer createTableBuffer = new StringBuffer("");
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						createTableBuffer.append("create Table "+key+"_tmp ");
						createTableBuffer.append(" as select "+tmpBuf.toString()+" from "+key);
					}else{
						createTableBuffer.append("select "+tmpBuf.toString()+" into "+key+"_tmp ");
						createTableBuffer.append(" from "+key);
					}
					if("3".equals(history))//取某次记录
					{
						createTableBuffer.append(" where ");
						String z0=key+"Z0";
						createTableBuffer.append(Sql_switcher.year(key+"."+z0)+"="+year);
						createTableBuffer.append(" and "+Sql_switcher.month(key+"."+z0)+"="+month);
						String z1=key+"Z1";
						createTableBuffer.append(" and "+key+"."+z1+"="+count);
						createTableBuffer.append(" and UPPER(NBASE)='"+dbpre.toUpperCase()+"'");
						createTableBuffer.append(" and "+key+".a0100 in (select a0100 from "+tableName+")");
					}
					else{
						createTableBuffer.append(",");
						StringBuffer maxTempTable=new StringBuffer("");
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						{
							maxTempTable.append("create Table max_temp_aaaa ");
							if(!"2".equals(history)) {
                                maxTempTable.append(" as select nbase,a0100,max(i9999) i9999 from "+key);
                            } else {
                                maxTempTable.append(" as select nbase,a0100,i9999 i9999 from "+key);
                            }
						}else{
							if(!"2".equals(history)) {
                                maxTempTable.append("select nbase,a0100,max(i9999) i9999 into max_temp_aaaa");
                            } else {
                                maxTempTable.append("select nbase,a0100,i9999 i9999 into max_temp_aaaa");
                            }
							maxTempTable.append(" from "+key);
						}
						maxTempTable.append(" where UPPER(nbase)='"+dbpre.toUpperCase()+"'");
						maxTempTable.append(" and a0100 in (select a0100 from "+tableName+")");
						if("2".equals(history)) {//取部分历史记录  视图子集条件  视图临时表插入数据添加过滤条件
							String[] fomula = fromCope.split("::");
							 if(fomula!=null&&fomula.length==3){
								 boolean blike=false;
							    	if("1".equals(fomula[2])) {
                                        blike=true;
                                    }
								 FactorList factorlist2 = new FactorList(
					    					PubFunc.keyWord_reback(fomula[0]), PubFunc.reBackWord((fomula[1])), "",
					    					true, blike, true, 1, userView.getUserName());	
								 maxTempTable.append(" and "+ factorlist2.getSingleTableSqlExpression(toScope));
							 }
						}
						
						if(!"2".equals(history)) {
                            maxTempTable.append(" group by nbase,a0100");
                        } else {
                            maxTempTable.append(" group by nbase,a0100,i9999");
                        }
						if (db.isExistTable("max_temp_aaaa", false)) {
                            db.dropTable("max_temp_aaaa");
                        }
						dao.update(maxTempTable.toString());
						dao.update("create index max_temp_aaaa_nbase on max_temp_aaaa (nbase) ");
						dao.update("create index max_temp_aaaa_a0100 on max_temp_aaaa (a0100) ");
						dao.update("create index max_temp_aaaa_i9999 on max_temp_aaaa (i9999) ");
						createTableBuffer.append(" max_temp_aaaa aaa where "+key+".I9999 = aaa.I9999 and "+key+".A0100 = aaa.A0100 and "+key+".NBASE = aaa.NBASE");
					}					
					Table table = new Table(key+"_tmp");
					if (db.isExistTable(table.getName(), false)) {
                        db.dropTable(table);
                    }
					dao.update(createTableBuffer.toString());
					dao.update("create index "+key+"_tmp_index on "+key+"_tmp (a0100) ");
					StringBuffer insert=new StringBuffer("");
					if(Sql_switcher.searchDbServer()==Constant.MSSQL)
					{
						insert.append(" update "+tableName+" set "+mssql.toString());
						insert.append(" from "+tableName+" left join ");
						if("1".equals(this.countflag)) {
                            insert.append(" (select a0100,"+select_buf.toString()+" from "+key+"_tmp group by a0100 ) S ");
                        } else {
                            insert.append(" (select a0100,"+select_buf.toString()+" from "+key+"_tmp ) S ");
                        }
						if("2".equals(history)) {
                            insert.append(" on "+tableName+".a0100=S.a0100 and "+tableName+".I9999=S.I9999  where  upper("+tableName+".nbase)='"+dbpre.toUpperCase()+"'");
                        } else {
                            insert.append(" on "+tableName+".a0100=S.a0100 where  upper("+tableName+".nbase)='"+dbpre.toUpperCase()+"'");
                        }
					}
					else
					{
						insert.append(" update "+tableName+" T set ("+insert_buf+")=(select "+select_buf.toString()+"");
						if("2".equals(history)) {
                            insert.append(" from "+key+"_tmp where "+key+"_tmp.a0100=T.a0100 and "+tableName+".I9999=T.I9999 ");
                        } else {
                            insert.append(" from "+key+"_tmp where "+key+"_tmp.a0100=T.a0100 ");
                        }
						if("1".equals(this.countflag)) {
                            insert.append(" group by a0100 ");
                        }
						insert.append(")");
						insert.append(" where exists (select null from "+key+"_tmp A where A.a0100=T.a0100 and upper(T.nbase)='"+dbpre.toUpperCase()+"'");
						insert.append(")");
					}
					dao.update(insert.toString());
					dao.update("drop table "+key+"_tmp ");
					if (db.isExistTable("max_temp_aaaa", false)) {
                        db.dropTable("max_temp_aaaa");
                    }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public int dbPreCount(String tabname){
        int dbprecnt = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            StringBuffer buf = new StringBuffer();
            buf.append("select NBASE from ");
            buf.append(tabname);
            buf.append(" group by NBASE");
            rs = dao.search(buf.toString());
            while(rs.next()){
                dbprecnt++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try
            {
                if(rs!=null) {
                    rs.close();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return dbprecnt;
    }
	
    private int recCount(String tabname){
        int reccnt = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            StringBuffer buf = new StringBuffer();
            buf.append("select count(*) as cnt from ");
            buf.append(tabname);
            rs = dao.search(buf.toString());
            if(rs.next()){
                reccnt=rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try
            {
                if(rs!=null) {
                    rs.close();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return reccnt;
    }
    
	public String dbPre(String tabname){
		String dbprename = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select NBASE from ");
			buf.append(tabname);
			buf.append(" group by NBASE");
			rs = dao.search(buf.toString());
			if(rs.next()){
				dbprename = rs.getString("NBASE");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return dbprename;
	}
	public boolean layerOrg(String tablename,String groupPoint,int type, String groupOrgCodeSet){
		boolean check = false;
		if(type==1)
		{
    		if(layerid==null||layerid.trim().length()<1) {
                return true;
            }
		}else if(type==2)
		{
			if(this.layerid2==null||this.layerid2.trim().length()<1) {
                return true;
            }
		}
		if(groupPoint==null||groupPoint.trim().length()<5) {
            return true;
        }
		String cond="";
		String cond2="";
		String codesetid="";
		// 机构花名册按单位分组，可以按指定层级的单位或部门分组
		if("21".equals(this.modelFlag) && "B0110".equalsIgnoreCase(groupPoint.substring(0,5)) &&(groupOrgCodeSet.length()>0)){
			codesetid=groupOrgCodeSet;
			cond = "codesetid = '"+codesetid+"'";
			cond2 = "A.codesetid in ('UN', 'UM')";
		}
		else{
			if("B0110".equalsIgnoreCase(groupPoint.substring(0,5))) {
				codesetid="UN";
			}
			else if("E0122".equalsIgnoreCase(groupPoint.substring(0,5))) {
				codesetid="UM";
			}else {
				FieldItem item=DataDictionary.getFieldItem(groupPoint);
				if(item!=null) {
					String codeset_id=item.getCodesetid();
					if("UM".equalsIgnoreCase(codeset_id)||"UN".equalsIgnoreCase(codeset_id)) {
						codesetid=codeset_id;
					}
				}
			}
			cond="codesetid='"+codesetid+"'";
			cond2="A.codesetid='"+codesetid+"'";
		}
		if(codesetid.trim().length()>0){
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			try {
				StringBuffer buf = new StringBuffer();
				buf.append("SELECT A.codeitemid,B.codeitemdesc as codeitemdesc,B.codeitemid AS parentid "); 
				buf.append("FROM organization A LEFT JOIN (SELECT codeitemid,codeitemdesc FROM organization ");
				buf.append("WHERE " + cond+" AND layer='");
				if(type==1) {
                    buf.append(layerid);
                } else if(type==2) {
                    buf.append(this.layerid2);
                }
				buf.append("') B ON (");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2) {
					buf.append("substr(A.codeitemid,0,LENGTH(B.codeitemid))=B.codeitemid AND A.codeitemid<>B.codeitemid");
				}else{
					buf.append("LEFT(A.codeitemid, LEN(B.codeitemid))=B.codeitemid AND A.codeitemid<>B.codeitemid");	
				}
				buf.append(") WHERE "+cond2+" and B.codeitemid is not null");
				rowSet = dao.search(buf.toString());
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("update ");
				sqlstr.append(tablename);
				if(type==1) {
                    sqlstr.append(" set GroupN=?,GroupV=? where GroupN=?");
                } else if(type==2) {
                    sqlstr.append(" set GroupN2=?,GroupV2=? where GroupN2=?");
                }
				ArrayList updatelist = new ArrayList();
				while(rowSet.next()){
					String codeitemid = rowSet.getString("codeitemid");
					String parentid = rowSet.getString("parentid");
					String codeitemdesc = rowSet.getString("codeitemdesc");
					if(parentid!=null&&parentid.trim().length()>0){
						ArrayList list = new ArrayList();
						list.add(parentid);
						list.add(codeitemdesc);
						list.add(codeitemid);
						updatelist.add(list);
					}
				}
				if(updatelist.size()>0) {
                    dao.batchUpdate(sqlstr.toString(),updatelist);
                }
				check = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				try
				{
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return check;
	}
	public boolean isSequence(int dbflag) {
		boolean flag = false;
		RowSet rowSet =null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if (dbflag == Constant.ORACEL) {
				rowSet = dao.search("select sequence_name from user_sequences where lower(sequence_name)='xxx'");
				if (rowSet.next()) {
                    flag = true;
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}
	synchronized static public void createMusterRecidx(Connection dbconn,String name) throws GeneralException {
		try{
			StringBuffer strsql = new StringBuffer();
			int dbflag = Sql_switcher.searchDbServer();
			DbWizard db = new DbWizard(dbconn);
			HmusterBo hmusterBo=new HmusterBo(dbconn);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" add xxx int identity(1,1)");
				break;
			default:
				if (hmusterBo.isSequence(dbflag)) {
					db.execute("drop sequence xxx");
				}
				strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}
			db.execute(strsql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 更新花名册的排序指标序号
	 * 
	 * @param name
	 */
	synchronized static public void updateMusterRecidxS(Connection dbconn,String name) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		int dbflag = Sql_switcher.searchDbServer();
		HmusterBo hmusterBo=new HmusterBo(dbconn);
		try {
			DbWizard db = new DbWizard(dbconn);
			/*switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" add xxx int identity(1,1)");
				break;
			default:
				if (hmusterBo.isSequence(dbflag)) {
					db.execute("drop sequence xxx");
				}
				strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}

			db.execute(strsql.toString());
			strsql.setLength(0);*/
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			case Constant.DB2:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=nextval for xxx");
				break;
			case Constant.ORACEL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx.nextval");
				break;
			default:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			}
			db.execute(strsql.toString());
			strsql.setLength(0);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" drop column xxx");
				break;
			default:
				strsql.append(" drop sequence xxx");
				break;
			}
			db.execute(strsql.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	
    /**
     * 更新花名册的排序指标序号
     * 
     * @param name
     */
    private void updateMusterRecidx2(Connection dbconn,String name, String dbpre) throws GeneralException {
        StringBuffer strsql = new StringBuffer();
        int dbflag = Sql_switcher.searchDbServer();
        HmusterBo hmusterBo=new HmusterBo(dbconn);
        try {
            DbWizard db = new DbWizard(dbconn);
            switch (dbflag) {
            case Constant.MSSQL:
                strsql.append("update ");
                strsql.append(name);
                strsql.append(" set recidx=xxx");
                break;
            case Constant.DB2:
                strsql.append("update ");
                strsql.append(name);
                strsql.append(" set recidx=nextval for xxx");
                break;
            case Constant.ORACEL:
                strsql.append("update ");
                strsql.append(name);
                strsql.append(" set recidx=xxx.nextval");
                break;
            default:
                strsql.append("update ");
                strsql.append(name);
                strsql.append(" set recidx=xxx");
                break;
            }
            if(dbpre!=null) {
                strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
            }
            db.execute(strsql.toString());
        } catch (Exception ex) {
            // ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

    }

    private void updateMusterRecidx3(Connection dbconn,String name, String dbpre, int addVal) throws GeneralException {
        StringBuffer strsql = new StringBuffer();
        int dbflag = Sql_switcher.searchDbServer();
        HmusterBo hmusterBo=new HmusterBo(dbconn);
        try {
            DbWizard db = new DbWizard(dbconn);
            switch (dbflag) {
            case Constant.ORACEL:
                strsql.append("update ");
                strsql.append(name);
                strsql.append(" set recidx=recidx+"+addVal);
                break;
            }
            if(dbpre!=null) {
                strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
            }
            db.execute(strsql.toString());
        } catch (Exception ex) {
            // ex.printStackTrace();
            //throw GeneralExceptionHandler.Handle(ex);
        }

    }
    
    /**
     * 删除序列号
     * 
     */
    private void dropSeqs(Connection dbconn,String name) throws GeneralException {
        StringBuffer strsql = new StringBuffer();
        int dbflag = Sql_switcher.searchDbServer();
        HmusterBo hmusterBo=new HmusterBo(dbconn);
        try {
            DbWizard db = new DbWizard(dbconn);
            switch (dbflag) {
            case Constant.MSSQL:
                strsql.append("alter table ");
                strsql.append(name);
                strsql.append(" drop column xxx");
                break;
            default:
                strsql.append(" drop sequence xxx");
                break;
            }
            db.execute(strsql.toString());
        } catch (Exception ex) {
            // ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

    }
    
	/**
	 * 更新花名册的排序指标序号
	 * 
	 * @param name
	 */
	public void updateMusterRecidx(String name) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		int dbflag = Sql_switcher.searchDbServer();
		try {
			DbWizard db = new DbWizard(this.conn);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" add xxx int identity(1,1)");
				break;
			default:
				if (isSequence(dbflag)) {
					db.execute("drop sequence xxx");
				}
				strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}

			db.execute(strsql.toString());
			strsql.setLength(0);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			case Constant.DB2:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=nextval for xxx");
				break;
			case Constant.ORACEL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx.nextval");
				break;
			default:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			}
			db.execute(strsql.toString());
			strsql.setLength(0);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" drop column xxx");
				break;
			default:
				strsql.append(" drop sequence xxx");
				break;
			}
			db.execute(strsql.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	private ArrayList getcList(RowSet rowSet){
		ArrayList list = new ArrayList();
		try {
			while(rowSet.next()){
				String Field_Name = rowSet.getString("Field_Name");
				String Field_Hz = rowSet.getString("Field_Hz");
				String fieldType = rowSet.getString("field_type");
				int gridNo = rowSet.getInt("gridno");
				FieldItem fielditem = new FieldItem("A01","人员表");
				fielditem.setItemid("C"+gridNo);
				fielditem.setItemdesc(Field_Hz);
				fielditem.setItemtype(fieldType);
				list.add(fielditem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 插入计算公式结果
	 * 
	 * @param tableName
	 * @param tabid
	 */
	public void runCountFormula(String tableName, String tabid,
			UserView userview, String infor_flag, String dbpre, String year,
			String month, String count, String history, String queryScope,
			String userName) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			ArrayList fielditemlist = new ArrayList();
			rowSet = dao.search("select * from muster_cell where tabid="+ tabid + " and UPPER(flag)='C' order by gridno");
			String whl = this.getInSql(infor_flag, dbpre, tableName);//getScopeDataSQL(queryScope, infor_flag, userName,dbpre);
			String existSql = this.getExistSql(infor_flag, dbpre, tableName);
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String flag = rowSet.getString("flag");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				int slope = rowSet.getInt("slope");
				int rcount = rowSet.getInt("rcount");
                if ("N".equals(fieldType)) {
                    rcount = 10;
                }

				String extendAttr = rowSet.getString("ExtendAttr")==null?"":rowSet.getString("ExtendAttr");
				String SimpleFormula = "false";
				if(extendAttr.indexOf("<SimpleFormula>")!=-1){
					SimpleFormula = extendAttr.substring(extendAttr.indexOf("<SimpleFormula>")+"<SimpleFormula>".length(),extendAttr.indexOf("</SimpleFormula>"));
				}
				/*FieldItem fieldC = new FieldItem("A01","人员表");
				fieldC.setItemid("C"+gridNo);
				fieldC.setItemdesc(rowSet.getString("Field_Hz")!=null?rowSet.getString("Field_Hz"):"");
				fieldC.setItemtype(fieldType);
				fielditemlist.add(fieldC);*/
				if(!"C".equalsIgnoreCase(flag)) {
                    continue;
                }
				String codeId = rowSet.getString("codeId");
				String codeSetID = null;
				if (codeId != null && codeId.trim().length() > 0&& !"0".equals(codeId.trim())) {
                    codeSetID = codeId.trim();
                }
				/**
				 * getAllFieldItemList(int ,int)
				 * 
				 * @param flag
				 *            int
				 *            ＝ALL_FIELD_SET全部，NOT_USED_FIELD_SET未构库,＝USED_FIELD_SET已构库
				 * @param domain
				 *            int
				 *            =ALLL_FIELD_SET全部,＝EMPLOY_FIELD_SET人员，UNIT_FIELD_SET单位，POS_FIELD_SET职位
				 */

				ArrayList alUsedFields = new ArrayList();
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				if ("2".equals(infor_flag)) {
					infoGroup = 3; // forUnit 单位
					alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
				} else if ("3".equals(infor_flag)) {
					infoGroup = 1; // forPosition 职位
					ArrayList unitFieldList = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
					ArrayList positionFieldList = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
					for (Iterator t = unitFieldList.iterator(); t.hasNext();) {
						FieldItem fielditem = (FieldItem) t.next();
						alUsedFields.add(fielditem);
					}

					for (int a = 0; a < positionFieldList.size(); a++) {
						FieldItem afielditem = (FieldItem) positionFieldList.get(a);
						alUsedFields.add(afielditem);
					}
					FieldItem item = new FieldItem();
					item.setItemid("e0122");
					item.setItemdesc("所属部门");
					item.setFieldsetid("K01");
					item.setItemtype("A");
					item.setCodesetid("UM");
					item.setItemlength(50);
					alUsedFields.add(item);

				} else if ("5".equals(infor_flag)) {// 基准岗位
					infoGroup = 1; 
					ArrayList stdPosFieldList = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.JOB_FIELD_SET);
					for (Iterator t = stdPosFieldList.iterator(); t.hasNext();) {
						FieldItem fielditem = (FieldItem) t.next();
						alUsedFields.add(fielditem);
					}

					/*FieldItem item = new FieldItem();
					item.setItemid("H0100");
					item.setItemdesc(ResourceFactory.getProperty("h0100.label"));
					item.setFieldsetid("H01");
					item.setItemtype("A");
					item.setCodesetid(getStdPosCodeSetId());
					item.setItemlength(30);
					alUsedFields.add(item);*/
				}
				String a_dbpre = "";
				if (infoGroup == 0) {
					a_dbpre = dbpre;
					alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				}
				FieldItem fielditem = DataDictionary.getFieldItem("B0110");
				alUsedFields.add(fielditem);
				YksjParser yp = new YksjParser(userview, alUsedFields,YksjParser.forSearch, varType, infoGroup, "Ht", a_dbpre);
				YearMonthCount ycm = null;
				// 2006-12-12 改 对于计算公式里的（子集）数据全取最后一次（按李群的建议）
				/*
				 * if (history.equals("3")) ycm = new
				 * YearMonthCount(Integer.parseInt(year), Integer
				 * .parseInt(month), Integer.parseInt(count));
				 */
				yp.setCon(conn);
				try {
					if(!"true".equalsIgnoreCase(SimpleFormula)){
						if ("N".equals(fieldType) && slope != 0) {
                            yp.setTargetFieldDecimal(slope);
                        }
						yp.setExistWhereText(existSql);
						yp.run(queryCond, ycm, "C" + gridNo, tableName, dao, whl,this.conn, fieldType, 
						        rcount, 2/*设为2更新到花名册表才能加NBASE条件*//*1*/, codeSetID);
						if(codeSetID!=null&&codeSetID.trim().length()>0&&!"0".equals(codeSetID)) {
                            changeCode(dao,tableName,"C" + gridNo,codeSetID,dbpre);
                        }

						if ("D".equalsIgnoreCase(fieldType)) {
							if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
                                excuteSql(dao, infoGroup, yp, "C" + gridNo,tableName, dbpre);
                            } else{
							    String sql="update "+tableName+" set C"+gridNo+"=replace(C"+gridNo+",'-','.')";
							    if("1".equals(infor_flag)) {
                                    sql+=" where upper(nbase)='"+dbpre.toUpperCase()+"'";
                                }
								dao.update(sql);
							}
						}
						else
						{
							excuteSql2(dao, infoGroup, yp, "C" + gridNo,tableName, dbpre);
						}
					}
				} catch (Exception ex) {
                   ex.printStackTrace();
				}
			}
			rowSet = dao.search("select gridno,field_type,querycond,extendattr,slope from muster_cell where tabid="+ tabid + " and flag='C'");
			ArrayList musterfieldlist= this.getMusterField(tabid, tableName);
			fielditemlist.addAll(musterfieldlist);
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				String extendAttr = rowSet.getString("ExtendAttr")==null?"":rowSet.getString("ExtendAttr");
				String SimpleFormula = "false";
				if(extendAttr.indexOf("<SimpleFormula>")!=-1){
					SimpleFormula = extendAttr.substring(extendAttr.indexOf("<SimpleFormula>")+"<SimpleFormula>".length(),
							extendAttr.indexOf("</SimpleFormula>"));
				}
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				if(varType==6)
				{
					int slope = rowSet.getInt("slope");
					if(slope==0) {
                        varType=5;
                    }
				}
				int infoGroup = 0; // forPerson 人员
				if ("2".equals(infor_flag)) {
					infoGroup = 3; // forUnit 单位
				} else if ("3".equals(infor_flag)) {
					infoGroup = 1; // forPosition 职位
				}
				String a_dbpre = "";
				if (infoGroup == 0) {
					a_dbpre = dbpre;
				}

				try {
					if("true".equalsIgnoreCase(SimpleFormula)){
						YksjParser yp = new YksjParser(userview, fielditemlist,YksjParser.forNormal, varType, infoGroup, "Ht", a_dbpre);
						yp.setCon(conn);
						yp.run(queryCond);
						String sqlstr = yp.getSQL();
						String updatesql = "update "+tableName+" set C"+gridNo+"="+sqlstr;
						if("1".equals(infor_flag)) {
                            updatesql += " where upper(nbase)='"+dbpre.toUpperCase()+"'";
                        }
						dao.update(updatesql);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	private boolean hasSimpleFormula(String tabid) {
	    boolean result = false;
	    ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try {
            rowSet = dao.search("select gridno,field_type,querycond,extendattr,slope from muster_cell where tabid="+ tabid + " and flag='C'");
            while (rowSet.next()) {
                int gridNo = rowSet.getInt("gridno");
                String fieldType = rowSet.getString("field_type");
                String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
                String extendAttr = rowSet.getString("ExtendAttr")==null?"":rowSet.getString("ExtendAttr");
                String SimpleFormula = "false";
                if(extendAttr.indexOf("<SimpleFormula>")!=-1){
                    SimpleFormula = extendAttr.substring(extendAttr.indexOf("<SimpleFormula>")+"<SimpleFormula>".length(),
                            extendAttr.indexOf("</SimpleFormula>"));
                }
                if("true".equalsIgnoreCase(SimpleFormula)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try
            {
                if(rowSet!=null) {
                    rowSet.close();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
	    return result;
	}
	
	/**
	 * 将代码型公式转换成汉字
	 * @param dao
	 * @param tableName
	 * @param cloumItem
	 * @param codeId
	 */
	private void changeCode(ContentDAO dao,String tableName,String cloumItem,String codeId,String dbpre){
		StringBuffer sql_str = new StringBuffer();
		sql_str.append("update ");
		sql_str.append(tableName);
		sql_str.append(" set ");
		if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
			sql_str.append(cloumItem);
			sql_str.append("=(select codeitemdesc from organization where codeitemid=");
			sql_str.append(tableName);
			sql_str.append(".");
			sql_str.append(cloumItem);
			sql_str.append(" and codesetid = '");
			sql_str.append(codeId);
			sql_str.append("')");
			sql_str.append(" where (select codeitemdesc from organization where codeitemid=");
			sql_str.append(tableName);
			sql_str.append(".");
			sql_str.append(cloumItem);
			sql_str.append(" and codesetid = '");
			sql_str.append(codeId);
			sql_str.append("') is not null");
		} else {
			sql_str.append(cloumItem);
			sql_str.append("=(select codeitemdesc from codeitem where codeitemid=");
			sql_str.append(tableName);
			sql_str.append(".");
			sql_str.append(cloumItem);
			sql_str.append(" and codesetid = '");
			sql_str.append(codeId);
			sql_str.append("')");
			sql_str.append(" where (select codeitemdesc from codeitem where codeitemid=");
			sql_str.append(tableName);
			sql_str.append(".");
			sql_str.append(cloumItem);
			sql_str.append(" and codesetid = '");
			sql_str.append(codeId);
			sql_str.append("') is not null");
		}
		//liuy 2015-4-9 8594：汉口银行：代码名称和代码项值一样都是字母时，高及花名册中插入了统计函数求这个管理了此代码的指标，结果就不对了。 begin
		if(StringUtils.isNotEmpty(dbpre)&&!"21".equals(this.modelFlag)&&!"41".equals(this.modelFlag)){//机构模块不应关联人员标识 changxy 20161018  23577 群姐提 组织机构 信息维护高级花名册  11号和17号花名册 取数 后台报错
			sql_str.append(" and upper("+tableName+".nbase)='"+dbpre.toUpperCase()+"'");
		}
		//liuy 2015-4-9 end
		try {
			dao.update(sql_str.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void excuteSql(ContentDAO dao, int InfoGroupFlag, YksjParser yp,
			String targetField, String targetTable, String dbpre) {
		/** ************修改临时表操作字段的数据******************* */
		String strJoin = "";
		if (InfoGroupFlag == YksjParser.forPerson) {
			strJoin = ".A0100";
		} else if (InfoGroupFlag == YksjParser.forPosition) {
			strJoin = ".E01A1";
		} else if (InfoGroupFlag == YksjParser.forUnit) {
			strJoin = ".B0110";
		}
		String TempTableName = yp.getTempTableName();
		StringBuffer sqlText = new StringBuffer();
		StringBuffer sqlText1 = new StringBuffer();
		/** ***************以上都是对临时表数据的操作******************** */
		String dateField = targetField.replace("C", "D");
		String updateStr = "update " + targetTable + " set ";
		String selectStr = "=(select ";
		String fromStr = " from " + TempTableName;
		String whereStr = " where " + targetTable + strJoin + "=" + TempTableName + strJoin + ")";
		String value = Sql_switcher.dateToChar(TempTableName + "."+ targetField, "yyyy.mm.dd");
		sqlText.append(updateStr + targetField + selectStr + value + fromStr +whereStr);
		sqlText1.append(updateStr + dateField + selectStr + TempTableName + "."+ targetField + fromStr +whereStr);
		if(InfoGroupFlag == YksjParser.forPerson){
			sqlText.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
			sqlText1.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
		}
		try {
			if (targetTable != null && targetTable.trim().length() > 0&& TempTableName != null&& TempTableName.trim().length() > 0) {
				dao.update(sqlText.toString());
				if (isExistField(targetTable, targetField.replace("C", "D"))) {
                    dao.update(sqlText1.toString());
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void excuteSql2(ContentDAO dao, int InfoGroupFlag, YksjParser yp,
			String targetField, String targetTable, String dbpre) {
		/** ************修改临时表操作字段的数据******************* */
		String strJoin = "";
		if (InfoGroupFlag == YksjParser.forPerson) {
			strJoin = ".A0100";
		} else if (InfoGroupFlag == YksjParser.forPosition) {
			strJoin = ".E01A1";
		} else if (InfoGroupFlag == YksjParser.forUnit) {
			strJoin = ".B0110";
		}
		String TempTableName = yp.getTempTableName();
		StringBuffer sqlText = new StringBuffer();
		/** ***************以上都是对临时表数据的操作******************** */
		sqlText.setLength(0);
		sqlText.append("update ");
		sqlText.append(targetTable);
		sqlText.append(" set ");
		sqlText.append(targetField);
		sqlText.append("=(").append("select ");
		//String value = Sql_switcher.dateToChar(TempTableName + "."+ targetField, "yyyy.mm.dd");
		sqlText.append(TempTableName + "."+ targetField);
		sqlText.append(" from " + TempTableName);
		sqlText.append(" where " + targetTable + strJoin);
		sqlText.append("=" + TempTableName + strJoin);
        if(InfoGroupFlag == YksjParser.forPerson) {
            sqlText.append(" and upper("+targetTable+".nbase)='"+dbpre.toUpperCase()+"'");
        }
		sqlText.append(")");
		if(InfoGroupFlag == YksjParser.forPerson) {
            sqlText.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
        }
		try {
			if (targetTable != null && targetTable.trim().length() > 0&& TempTableName != null&& TempTableName.trim().length() > 0) {
				dao.update(sqlText.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getScopeDataSQL(String queryScope, String infor_flag,
			String userName, String dbpre) {
		StringBuffer str = new StringBuffer("");
		if ("1".equals(infor_flag)) // 人员库
		{
			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView.getStatus()==4)
				{
					str.append(" select obj_id as a0100 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					str.append(" and flag=0 and UPPER(nbase)='"+dbpre.toUpperCase()+"'");
				}
				else
				{
		    		str.append("select a0100 from " + userName + dbpre + "Result");
				}
			} else {
				str.append("select a0100 from " + dbpre + "A01");
			}
		} else if ("2".equals(infor_flag)) // 单位库
		{
			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView.getStatus()==4)
				{
					str.append(" select obj_id as B0110 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					str.append(" and flag=1 ");
				}
				else
				{
			    	str.append("select B0110 from " + userName + "BResult");
				}
			} else {
				str.append("select B0110 from B01");
			}
		} else if ("3".equals(infor_flag)) // 职位库
		{
			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView.getStatus()==4)
				{
					str.append(" select obj_id as K01A1 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					str.append(" and flag=2 ");
				}
				else
				{
		    		str.append("select E01A1 from " + userName + "KResult");
				}
			} else {
				str.append("select E01A1 from K01");
			}
		}
		return str.toString();
	}

	/**
	 * 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据 (默认在职人员库)
	 */
	public void transformCode(String tabid, String tableName, String inforFlag)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;

		StringBuffer fieldsSql = new StringBuffer("");
		ArrayList fieldList = new ArrayList(); // 记下人员名单的字段名称
		HashMap map = new HashMap();
		HashMap queryCondMap = new HashMap();
		String department_no = ""; // 所属部门
		fieldsSql.append("select GridNo,SetName,Field_Name,Field_Type,");
		fieldsSql.append("Flag,Slope,codeId,Field_Hz,extendattr,querycond  from muster_cell where ");
		fieldsSql.append(isNotNull("flag"));
		fieldsSql.append(" and  tabid=");//and flag<>'C' 
		fieldsSql.append(tabid);
		ArrayList nameCountList=new ArrayList();
		try {
			rowSet = dao.search(fieldsSql.toString());
			while (rowSet.next()) {

				String gridNo = rowSet.getString("GridNo");
				String codeId = rowSet.getString("codeId");
				String flag = rowSet.getString("Flag");
				String field_Hz = rowSet.getString("Field_Hz");
				String field_type=rowSet.getString("Field_Type");
				String field_name=rowSet.getString("field_name")==null?"":rowSet.getString("field_name");
                String extendAttr=Sql_switcher.readMemo(rowSet, "extendattr");
                if((extendAttr.toUpperCase().indexOf("<NAMECOUNT>")!=-1||extendAttr.toUpperCase().indexOf("<NAMEVALUE>")!=-1)&&"N".equalsIgnoreCase(field_type))
                {
                	LazyDynaBean bean = new LazyDynaBean();
                	bean.set("gridno", gridNo);
                	bean.set("extendattr", extendAttr);
                	bean.set("querycond", rowSet.getString("querycond")==null?"":rowSet.getString("querycond"));
                	nameCountList.add(bean);
                }
				if ("A".equals(flag)&& (field_Hz.equals(ResourceFactory.getProperty("hmuster.label.mainlist"))|| "A0101".equalsIgnoreCase(field_name))&& !"1".equals(inforFlag))
				{
					fieldList.add("C" + gridNo);
					map.put("C"+gridNo, extendAttr);
					queryCondMap.put("C"+gridNo, rowSet.getString("querycond")==null?"":rowSet.getString("querycond"));
				}

				if ("K".equals(flag)&& "UM".equals(codeId)&& "e0122".equalsIgnoreCase(rowSet.getString("field_name")) && "3".equals(inforFlag)) {
                    department_no += ",C" + gridNo;
                }

				if (codeId != null && !"".equals(codeId) && !"0".equals(codeId)) {
					StringBuffer sql_str = new StringBuffer(" update " + tableName+ " set ");
					if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
						sql_str.append(" "+tableName+".");
						sql_str.append("C");
						sql_str.append(gridNo);
						sql_str.append("=(");
						sql_str.append("select codeitemdesc from organization S where  "+tableName+".C"+ gridNo+ "=S.codeitemid");
						sql_str.append(") where exists (select null from organization S where "+tableName+".C"+ gridNo+ "=S.codeitemid)");

					} else if ("@@".equals(codeId)) {
						sql_str.append(" "+tableName+".");
						sql_str.append("C");
						sql_str.append(gridNo);
						sql_str.append("=(");
						sql_str.append("select dbname from dbname S where  "+tableName+".C" + gridNo + "=S.pre");
						sql_str.append(") where exists (select null from dbname S where "+tableName+".C" + gridNo + "=S.pre)");

					} else {
						sql_str.append(" "+tableName+".");
						sql_str.append("C");
						sql_str.append(gridNo);
						sql_str.append("=(");
						sql_str.append("select codeitemdesc from codeitem S where codesetid='"+ codeId+ "' and  "+tableName+".C"+ gridNo + "=S.codeitemid");
						sql_str.append(")  where exists (select null from codeitem S where S.codesetid='"+ codeId+ "' and "+tableName+".C"+ gridNo + "=S.codeitemid)");
					}
					dao.update(sql_str.toString());
				}
			}
			if (this.groupPointItem != null&& this.groupPointItem.trim().length() > 0) {
				String codeId = "";
				FieldItem item = DataDictionary.getFieldItem(this.groupPointItem.toLowerCase());
				if (item != null && item.getCodesetid() != null&& !"0".equals(item.getCodesetid())) {
					codeId = item.getCodesetid();
				} else if ("b0110".equals(this.groupPointItem.toLowerCase())) {
                    codeId = "UN";
                } else if ("e0122".equals(this.groupPointItem.toLowerCase())) {
                    codeId = "UM";
                } else if ("e01a1".equals(this.groupPointItem.toLowerCase())) {
                    codeId = "@K";
                }
				if(codeId!=null&&!"".equals(codeId))
				{
			    	String whl_extend = "";
			    	String codeTable = "codeitem";
			    	if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
                        codeTable = "organization";
                    } else {
                        whl_extend = " and codesetid='" + codeId + "'";
                    }
			    	dao.update("update " + tableName+ " set GroupV=(select codeitemdesc from " + codeTable+ "  where  " + tableName + ".GroupN=codeitemid "+ whl_extend + " ) " +
						"where exists (select null from "+codeTable+" S where S.codesetid='"+ codeId+ "' and "+tableName+".GroupN=S.codeitemid)");
				}else{
					dao.update(" update "+tableName+" set GroupV=GroupN");
				}
			}
			if (this.groupPoint2 != null&& this.groupPoint2.trim().length() > 0) {
				String codeId = "";
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2.toLowerCase());
				if (item != null && item.getCodesetid() != null&& !"0".equals(item.getCodesetid())) {
					codeId = item.getCodesetid();
				} else if ("b0110".equals(this.groupPoint2.toLowerCase())) {
                    codeId = "UN";
                } else if ("e0122".equals(this.groupPoint2.toLowerCase())) {
                    codeId = "UM";
                } else if ("e01a1".equals(this.groupPoint2.toLowerCase())) {
                    codeId = "@K";
                }
				if(codeId!=null&&!"".equals(codeId))
				{
			    	String whl_extend = "";
			    	String codeTable = "codeitem";
			    	if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
                        codeTable = "organization";
                    } else {
                        whl_extend = " and codesetid='" + codeId + "'";
                    }
			    	 dao.update("update " + tableName+ " set GroupV2=(select codeitemdesc from " + codeTable+ "  where  " + tableName + ".GroupN2=codeitemid "+ whl_extend + " )"
					    	+"where exists (select null from "+codeTable+" S where S.codesetid='"+ codeId+ "' and "+tableName+".GroupN2=S.codeitemid)");
				}else{
					dao.update(" update "+tableName+" set GroupV2=GroupN2");
				}
			}
			// 如果机构或职位信息库中有人员名单字段也填上数据
			ArrayList privDbList = userView.getPrivDbList();
			String privDb ="";
			for(int j=0;j<privDbList.size();j++)
			{
				privDb+=(String)privDbList.get(j);
				if(j!=privDbList.size()-1) {
                    privDb+=",";
                }
			}
			if ("2".equals(inforFlag)) // 单位
			{

				if (fieldList.size() > 0&&privDb!=null&&privDb.trim().length()>0) {
					String sq = "select B0110 from " + tableName;
					rowSet = dao.search(sq);
					StringBuffer update = new StringBuffer("");
					ArrayList llist = new ArrayList();
					while (rowSet.next()) {
						String b0110 = rowSet.getString("B0110");
						ArrayList alist=new ArrayList();
						update.setLength(0);
			    		for (Iterator t1 = fieldList.iterator(); t1.hasNext();) {
				    		String gridno=(String)t1.next();
				    		update.append(","+gridno+"=?");
					     	String extendattr=((String)map.get(gridno.toUpperCase())).toUpperCase();
					     	String nbase="USR";
					     	String whereSQL="";
					     	if(extendattr.indexOf("<NBASE>")!=-1)
					     	{
					     		nbase=extendattr.substring(extendattr.indexOf("<NBASE")+"<NBASE>".length(), extendattr.indexOf("</NBASE>"));
					     	}
					     	String factor="";
					     	if(extendattr.indexOf("<FACTOR>")!=-1)
					     	{
					     		factor=extendattr.substring(extendattr.indexOf("<FACTOR")+"<FACTOR>".length(), extendattr.indexOf("</FACTOR>"));
					     	}
					     	String FuzzyFlag="false";
					     	if(extendattr.indexOf("<FUZZYFLAG>")!=-1)
					     	{
					     		FuzzyFlag=extendattr.substring(extendattr.indexOf("<FUZZYFLAG")+"<FUZZYFLAG>".length(), extendattr.indexOf("</FUZZYFLAG>"));
					     	}
					     	String IncludeSubOrgs="true";//是否包含下级组织单元
					    	if(extendattr.indexOf("<INCLUDESUBORGS>")!=-1)
					     	{
					    		IncludeSubOrgs=extendattr.substring(extendattr.indexOf("<INCLUDESUBORGS")+"<INCLUDESUBORGS>".length(), extendattr.indexOf("</INCLUDESUBORGS>"));
					     	}
					     	String querycond=(String)queryCondMap.get(gridno.toUpperCase());
					     	boolean blike=false;
					        if("true".equalsIgnoreCase(FuzzyFlag)) {
                                blike=true;
                            }
					        ArrayList fieldlist = new ArrayList();
					     	if(nbase==null||nbase.trim().length()==0)
					     	{
					     		nbase=privDb;
					     	}
					    	String[] arr=nbase.split(",");
					    	StringBuffer buf = new StringBuffer();
					    	for(int j=0;j<arr.length;j++)
					    	{
							    if(arr[j]==null|| "".equals(arr[j])||privDb.toUpperCase().indexOf(arr[j].toUpperCase())==-1) {
                                    continue;
                                }
							    if((factor==null|| "".equals(factor))&&querycond!=null&&querycond.length()>0)//复杂查询（还未支持）
						     	{
						     		
						     	}
						     	else if(factor!=null&&factor.length()>0&&querycond!=null&&querycond.length()>0)
						     	{

						            if((!userView.isSuper_admin()))
						            {
						            	whereSQL=userView.getPrivSQLExpression(querycond+"|"+factor,arr[j],false,blike,true,fieldlist);
						            }
						            else
						            {
						                FactorList factorlist=new FactorList(querycond,factor,arr[j],false ,blike,true,1,userView.getUserId());
						                fieldlist=factorlist.getFieldList();
						                whereSQL=factorlist.getSqlExpression();
						            }
						     	}
							    if(whereSQL.length()>0)
							    {
							    	if("true".equalsIgnoreCase(IncludeSubOrgs))
							    	{
							    		 buf.append(" union all select "+arr[j]+"A01.a0101,"+arr[j]+"A01.A0000,"+arr[j]+"A01.B0110,"+arr[j]+"A01.E0122 "+whereSQL+" and ("+arr[j]+"A01.B0110='"+ b0110 + "' or "+arr[j]+"A01.E0122 like '" + b0110+ "%') and "+arr[j]+"A01.A0101 is not null");
							    	}
							    	else
							    	{
							    		 buf.append(" union all select "+arr[j]+"A01.a0101,"+arr[j]+"A01.A0000,"+arr[j]+"A01.B0110,"+arr[j]+"A01.E0122 "+whereSQL+" and (("+arr[j]+"A01.B0110='"+ b0110 + "' and "+arr[j]+"A01.e0122 is null) or "+arr[j]+"A01.E0122 ='" + b0110+ "') and "+arr[j]+"A01.A0101 is not null");
							    	}
							    }
							    else
							    {
							    	  if("true".equalsIgnoreCase(IncludeSubOrgs))
								      {
							    		  buf.append(" union all select "+arr[j]+"A01.a0101,"+arr[j]+"A01.A0000,"+arr[j]+"A01.B0110,"+arr[j]+"A01.E0122 from "+arr[j]+"A01 where  ("+arr[j]+"A01.B0110='"+ b0110 + "' or "+arr[j]+"A01.E0122 like '" + b0110+ "%') and "+arr[j]+"A01.A0101 is not null");
								      }
								      else
								      {
								    	  buf.append(" union all select "+arr[j]+"A01.a0101,"+arr[j]+"A01.A0000,"+arr[j]+"A01.B0110,"+arr[j]+"A01.E0122 from "+arr[j]+"A01 where  (("+arr[j]+"A01.B0110='"+ b0110 + "' and "+arr[j]+"A01.e0122 is null) or "+arr[j]+"A01.E0122 = '" + b0110+ "') and "+arr[j]+"A01.A0101 is not null");
								      }
							    }
					    	}
					    	StringBuffer tempBuffer = new StringBuffer("");
					    	if(buf.toString().length()>0)
					    	{
					        	String sqlT="select a0101 from ("+buf.toString().substring(10)+") T  order by T.A0000,T.B0110,T.E0122";//30305	部门花名册在前台和后台中的人员名单顺序不一致";
					        	RowSet rowSet2 = dao.search(sqlT);
						    	//while (rowSet2.next()) {
						    	//	tempBuffer.append(",");
						    	//	tempBuffer.append(rowSet2.getString("A0101"));
						    	//}
					        	tempBuffer.append(getNamelist(rowSet2,extendattr));
					    	}
					    	alist.add(tempBuffer.toString());
			    		}
			    		update.append(" where b0110=?");
			    		alist.add(b0110);	
			    		llist.add(alist);
					}
					if(llist.size()>0) {
                        dao.batchUpdate(" update " + tableName + " set "+ update.substring(1), llist);
                    }
				}

			} else if ("3".equals(inforFlag)) // 职位
			{
				if (fieldList.size() > 0 && privDb != null && privDb.trim().length() > 0) {

					String sq = "select e01a1 from " + tableName;
					rowSet = dao.search(sq);
					StringBuffer update = new StringBuffer("");
					ArrayList llist = new ArrayList();
					while (rowSet.next()) {
						String b0110 = rowSet.getString("e01a1");
						ArrayList alist=new ArrayList();
						update.setLength(0);
			    		for (Iterator t1 = fieldList.iterator(); t1.hasNext();) {
			    			String gridno=(String)t1.next();
				    		update.append(","+gridno+"=?");
					     	String extendattr=((String)map.get(gridno.toUpperCase())).toUpperCase();
					     	String nbase="USR";
					     	String whereSQL="";
					     	if(extendattr.indexOf("<NBASE>")!=-1)
					     	{
					     		nbase=extendattr.substring(extendattr.indexOf("<NBASE")+"<NBASE>".length(), extendattr.indexOf("</NBASE>"));
					     	}
					     	String factor="";
					     	if(extendattr.indexOf("<FACTOR>")!=-1)
					     	{
					     		factor=extendattr.substring(extendattr.indexOf("<FACTOR")+"<FACTOR>".length(), extendattr.indexOf("</FACTOR>"));
					     	}
					     	String FuzzyFlag="false";
					     	if(extendattr.indexOf("<FUZZYFLAG>")!=-1)
					     	{
					     		FuzzyFlag=extendattr.substring(extendattr.indexOf("<FUZZYFLAG")+"<FUZZYFLAG>".length(), extendattr.indexOf("</FUZZYFLAG>"));
					     	}
					     	String querycond=(String)queryCondMap.get(gridno.toUpperCase());
					     	boolean blike=false;
					        if("true".equalsIgnoreCase(FuzzyFlag)) {
                                blike=true;
                            }
					        ArrayList fieldlist = new ArrayList();
					    	if(nbase==null||nbase.trim().length()==0)
					     	{
					     		nbase=privDb;
					     	}
					    	String[] arr=nbase.split(",");
					    	StringBuffer buf = new StringBuffer();
					    	for(int j=0;j<arr.length;j++)
					    	{
							    if(arr[j]==null|| "".equals(arr[j])||privDb.toUpperCase().indexOf(arr[j].toUpperCase())==-1) {
                                    continue;
                                }
							    if(querycond==null||querycond.length()==0)//复杂查询（还未支持）
						     	{
						     		
						     	}
						     	else if(factor!=null&&factor.length()>0&&querycond!=null&&querycond.length()>0)
						     	{

						            if((!userView.isSuper_admin()))
						            {
						            	whereSQL=userView.getPrivSQLExpression(querycond+"|"+factor,arr[j],false,blike,true,fieldlist);
						            }
						            else
						            {
						                FactorList factorlist=new FactorList(querycond,factor,arr[j],false ,blike,true,1,userView.getUserId());
						                fieldlist=factorlist.getFieldList();
						                whereSQL=factorlist.getSqlExpression();
						            }
						     	}
							    if(whereSQL.length()>0)
							    {
							    	  buf.append(" union all select "+arr[j]+"A01.a0101 "+whereSQL+" and ("+arr[j]+"A01.e01a1='"+ b0110 + "') and "+arr[j]+"A01.A0101 is not null");
							    }
							    else
							    {
							    	  buf.append(" union all select "+arr[j]+"A01.a0101 from "+arr[j]+"A01 where  ("+arr[j]+"A01.e01a1='"+ b0110 + "') and "+arr[j]+"A01.A0101 is not null");

							    }
							    	
					    	}
					    	StringBuffer tempBuffer = new StringBuffer("");
					    	if(buf.toString().length()>0)
					    	{
				    	    	String sqlT="select a0101 from ("+buf.toString().substring(10)+") T";
				    	    	RowSet rowSet2 = dao.search(sqlT);
				    			//while (rowSet2.next()) {
					     		//	tempBuffer.append(",");
					    		//	tempBuffer.append(rowSet2.getString("A0101"));
					    		//}
				    	    	tempBuffer.append(getNamelist(rowSet2,extendattr));
					    	}
					    	alist.add(tempBuffer.toString());
			    		}
                        update.append(" where e01a1=?");
			    		alist.add(b0110);	
			    		llist.add(alist);
					}
					if(llist.size()>0)
					{
			     		dao.batchUpdate(" update " + tableName + " set "+ update.substring(1), llist);
					}
					/*StringBuffer s_update = new StringBuffer("");
					ArrayList list = new ArrayList();
					int ii = 0;
					for (Iterator t1 = fieldList.iterator(); t1.hasNext();) {
						ii++;
						s_update.append(" , ");
						s_update.append((String) t1.next() + "=?");

					}
					s_update.append(" where E01A1=? ");

					String sq = "select E01A1 from " + tableName;
					rowSet = dao.search(sq);
					while (rowSet.next()) {
						String b0110 = rowSet.getString("E01A1");
						ArrayList tempList = new ArrayList();
						String sq2 = "select A0101 from UsrA01 where E01A1='"+ b0110 + "' and A0101 is not null ";
						StringBuffer tempBuffer = new StringBuffer("");
						RowSet rowSet2 = dao.search(sq2);
						while (rowSet2.next()) {

							tempBuffer.append(",");
							tempBuffer.append(rowSet2.getString("A0101"));

						}
						String value = "";
						if (tempBuffer.length() > 1)
							value = tempBuffer.substring(1);

						for (int a = 0; a < ii; a++) {
							tempList.add(value);
						}
						tempList.add(b0110);

						list.add(tempList);

					}
					dao.batchUpdate(" update " + tableName + " set "+ s_update.substring(2), list);*/
				}

				if (department_no.length() > 0) // 所属部门
				{
					String [] sql_arr= department_no.split(",");
					for(int i=0;i<sql_arr.length;i++)
					{
						if(sql_arr[i]==null|| "".equals(sql_arr[i])) {
                            continue;
                        }
			    		String sql = "update " + tableName + " set "+ sql_arr[i]+ "=(select a.parentid from organization a"+ " where a.codeitemid=" + tableName + ".e01a1)";
				    	dao.update(sql);
				    	sql = "update " + tableName + " set " + sql_arr[i]+ "=(select a.codeitemdesc	from organization a"+ " where a.codeitemid=" + tableName + "."+ sql_arr[i] + ")";
				    	dao.update(sql);
					}
				}
			}
			if("2".equals(inforFlag)|| "3".equals(inforFlag))
			{

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 返回人员名单数据
	 * @param rowSet
	 * @param extendattr
	 * @return
	 */
	private String getNamelist(RowSet rowSet, String extendattr){
		StringBuffer tempBuffer=new StringBuffer("");
		String scnt="";
		int cnt=0;  // 每行姓名个数
		String sep="";
     	if(extendattr.indexOf("<NAMESPERLINE>")!=-1)
     	{
     		scnt=extendattr.substring(extendattr.indexOf("<NAMESPERLINE")+"<NAMESPERLINE>".length(), 
     										extendattr.indexOf("</NAMESPERLINE>"));
     		if(scnt!=null&&scnt.length()>0) {
                cnt=Integer.parseInt(scnt);
            }
     	}
     	if(extendattr.indexOf("<NAMESEP>")!=-1)
     	{
     		sep=extendattr.substring(extendattr.indexOf("<NAMESEP")+"<NAMESEP>".length(), 
     										extendattr.indexOf("</NAMESEP>"));
     	}
 		if(sep==null||sep.length()==0) {
            sep=" ";  // 默认空格
        }
		try{
			int i=1;
			while (rowSet.next()) {
				tempBuffer.append(rowSet.getString("A0101"));
				if(!rowSet.isLast()){
					if(cnt!=0&&i%cnt==0) {
                        tempBuffer.append("`");  // 换行
                    } else {
                        tempBuffer.append(sep);
                    }
				}
				i++;
			}
		}catch(Exception e){
			
		}
		return tempBuffer.toString();
	}
	
	/**
	 * 单位、职位花名册，人员名单/人员统计(取值)
	 * @Title: transFoumlorGeidno   
	 * @Description:    
	 * @param tabid
	 * @param tableName
	 * @param inforFlag
	 * @param userName
	 */
	public void transFoumlorGeidno(String tabid, String tableName, String inforFlag,String userName){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;

		StringBuffer fieldsSql = new StringBuffer("");
		ArrayList fieldList = new ArrayList(); // 记下人员名单的字段名称
		HashMap map = new HashMap();
		HashMap queryCondMap = new HashMap();
		String department_no = ""; // 所属部门
		fieldsSql.append("select GridNo,SetName,Field_Name,Field_Type,");
		fieldsSql.append("Flag,Slope,codeId,Field_Hz,extendattr,querycond  from muster_cell where ");
		fieldsSql.append(isNotNull("flag"));
		fieldsSql.append(" and  tabid=");//and flag<>'C' 
		fieldsSql.append(tabid);
		ArrayList nameCountList=new ArrayList();
		RowSet rs=null;
		RowSet ars = null;
		try
		{
			rowSet = dao.search(fieldsSql.toString());
			while (rowSet.next()) {

				String gridNo = rowSet.getString("GridNo");
				String codeId = rowSet.getString("codeId");
				String flag = rowSet.getString("Flag");
				String field_Hz = rowSet.getString("Field_Hz");
                String extendAttr=Sql_switcher.readMemo(rowSet, "extendattr");
                if((extendAttr.toUpperCase().indexOf("<NAMECOUNT>")/*单位/职位花名册中统计人数True|False*/!=-1
                		||extendAttr.toUpperCase().indexOf("<NAMEVALUE>")/*单位/职位花名册中人员取值:求和SUM/最大MAX/最小MIN/平均AVG*/!=-1)
                		&&"N".equalsIgnoreCase(rowSet.getString("Field_Type")/*数据类型*/))
                {
                	LazyDynaBean bean = new LazyDynaBean();
                	bean.set("gridno", gridNo);
                	bean.set("extendattr", extendAttr);
                	bean.set("querycond", rowSet.getString("querycond")==null?"":rowSet.getString("querycond"));
                	nameCountList.add(bean);
                }
			}
			if(nameCountList.size()>0)
			{
				DbWizard dbw=new DbWizard(this.conn);
	  	        DBMetaModel dbmodel=new DBMetaModel(this.conn);
	  	        
	    		//liuy 优化高级花名册公式取数慢  start
	    		String keyColumn="B0110";
	    		if("3".equals(inforFlag)) {
                    keyColumn="E01A1";
                }
	    		String htable1="T#"+userName+"_mus1";
	  	        Table table1 = new Table(htable1);
	  	        Field tempF1=null;
	  	        tempF1 = new Field("Total","Total");
	    		tempF1.setVisible(true);
	    		tempF1.setKeyable(false);
	    		tempF1.setNullable(true);
	    		tempF1.setDatatype(DataType.FLOAT);
	    		tempF1.setLength(20);
	    		tempF1.setDecimalDigits(5);
	    		table1.addField(tempF1);
	    		
	    		tempF1 = new Field(keyColumn,keyColumn);
         	  	tempF1.setVisible(true);
         	  	tempF1.setKeyable(false);
         	  	tempF1.setNullable(true);
         	  	tempF1.setDatatype(DataType.STRING);
         	  	tempF1.setLength(40);
	    		table1.addField(tempF1);
	    		
	    		tempF1 = new Field("nbase","nbase");
	    		tempF1.setVisible(true);
	    		tempF1.setKeyable(false);
	    		tempF1.setNullable(false);
	    		tempF1.setDatatype(DataType.STRING);
	    		tempF1.setLength(10);
	    		table1.addField(tempF1);
	    		if(dbw.isExistTable(htable1, false))
	    		{
	    			dbw.dropTable(table1);
	    		}
	    		dbw.createTable(table1);
	    		
	    		String htable2="T#"+userName+"_mus2";
	  	        Table table2 = new Table(htable2);
	  	        Field tempF2=null;
	  	        tempF2 = new Field("Total","Total");
	    		tempF2.setVisible(true);
	    		tempF2.setKeyable(false);
	    		tempF2.setNullable(true);
	    		tempF2.setDatatype(DataType.FLOAT);
	    		tempF2.setLength(20);
	    		tempF2.setDecimalDigits(5);
	    		table2.addField(tempF2);
	    		
	    		tempF2 = new Field(keyColumn,keyColumn);
         	  	tempF2.setVisible(true);
         	  	tempF2.setKeyable(false);
         	  	tempF2.setNullable(true);
         	  	tempF2.setDatatype(DataType.STRING);
         	  	tempF2.setLength(40);
	    		table2.addField(tempF2);
	    		
	    		if(dbw.isExistTable(htable2, false))
	    		{
	    			dbw.dropTable(table2);
	    		}
	    		dbw.createTable(table2);

				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				InfoUtils infoUtils=new InfoUtils();
				alUsedFields.addAll(infoUtils.getMidVariableList("3", "0", this.conn));
				ArrayList list = this.userView.getPrivDbList();
				String privDb="";
				for(int j=0;j<list.size();j++)
				{
					privDb+=(String)list.get(j);
					if(j!=list.size()-1) {
                        privDb+=",";
                    }
				}
				for(int i=0;i<nameCountList.size();i++)
				{
					dao.delete("delete from "+htable1, new ArrayList());
					dao.delete("delete from "+htable2, new ArrayList());
					HashMap valueMap = new HashMap();
					LazyDynaBean bean = (LazyDynaBean)nameCountList.get(i);
					String gridno=(String)bean.get("gridno");
					String extattr=((String)bean.get("extendattr")).toUpperCase();
					String querycond=(String)bean.get("querycond");
					String nameCount="false";
					if(extattr.indexOf("<NAMECOUNT>")!=-1)//单位/职位花名册中统计人数True|False
					{
						nameCount=extattr.substring(extattr.indexOf("<NAMECOUNT>")+"<NAMECOUNT>".length(), extattr.indexOf("</NAMECOUNT>"));
					}
					String nbase="USR";
					if(extattr.indexOf("<NBASE>")!=-1)//人员列表/人员统计（取值）扫描人员库，USR,RET...默认为USR
					{
						nbase=extattr.substring(extattr.indexOf("<NBASE>")+"<NBASE>".length(), extattr.indexOf("</NBASE>"));
					}
					String factor="";
					if(extattr.indexOf("<FACTOR>")!=-1)//因子A0101<>`(表达式1*2保存在QueryCond字段，为空表示复杂查询)
					{
						factor=extattr.substring(extattr.indexOf("<FACTOR>")+"<FACTOR>".length(), extattr.indexOf("</FACTOR>"));
					}
					String FuzzyFlag="false";
			     	if(extattr.indexOf("<FUZZYFLAG>")!=-1)//模糊查询True|False
			     	{
			     		FuzzyFlag=extattr.substring(extattr.indexOf("<FUZZYFLAG")+"<FUZZYFLAG>".length(), extattr.indexOf("</FUZZYFLAG>"));
			     	}
			     	String IncludeSubOrgs="true";//是否包含下级组织单元
			    	if(extattr.indexOf("<INCLUDESUBORGS>")!=-1)//机构人员名单是否包含下级组织单元的人员True|False,默认值True
			     	{
			    		IncludeSubOrgs=extattr.substring(extattr.indexOf("<INCLUDESUBORGS")+"<INCLUDESUBORGS>".length(), extattr.indexOf("</INCLUDESUBORGS>"));
			     	}
			    	String wherestr = "";
			    	if("E01A1".equalsIgnoreCase(keyColumn)) {
                        wherestr = "A01.E01A1 = m.E01A1";
                    } else {
			    		if("true".equalsIgnoreCase(IncludeSubOrgs)) {
                            wherestr = "(A01.B0110 like m.B0110"+ Sql_switcher.concat() +"'%' or A01.E0122 like m.B0110 "+ Sql_switcher.concat() +"'%')";
                        } else {
                            wherestr = "(A01.B0110 = m.B0110 and (A01.E0122 is null or A01.E0122 = '') or  A01.E0122 = m.B0110)";
                        }
			    	}
					if("true".equalsIgnoreCase(nameCount))//统计人数
					{
						if(nbase==null||nbase.trim().length()==0) {
                            nbase=privDb;
                        }
						String[] arr=nbase.split(",");
						for(int j=0;j<arr.length;j++)
						{
							if(arr[j]==null|| "".equals(arr[j])||privDb.toUpperCase().indexOf(arr[j].toUpperCase())==-1) {
                                continue;
                            }
							String whereSQL = "";
							String whereA0100In=InfoUtils.getWhereINSql(this.userView,arr[j]);
							if(querycond==null|| "".equals(querycond))//复杂查询
							{
								YksjParser yp = new YksjParser(
						    			getUserView()//Trans交易类子类中可以直接获取userView
						    			,alUsedFields
						    			,YksjParser.forSearch
						    			,YksjParser.LOGIC//此处需要调用者知道该公式的数据类型
						    			,YksjParser.forPerson
						    			,"gw",arr[j]);
								YearMonthCount ycm = null;	
								yp.setSupportVar(true);  //支持临时变量
								if(factor==null|| "".equals(factor.trim())) {
                                    factor=" 1=1 ";
                                }
							    String existSql ="";// this.getExistSql("1", arr[j], tableName);
							    String inSql = "";//this.getInSql("1", arr[j], tableName);
							    yp.setExistWhereText(existSql);
								yp.run_Where(factor, ycm,"","", dao, inSql,this.conn,"A", null); 
								whereSQL=yp.getSQL();
								String tempTableName = yp.getTempTableName();
								whereSQL="(a0100 in (select a0100 from "+tempTableName+" where "+whereSQL+")) ";
							}
							else//通用查询
							{
								boolean blike=false;
						        if("true".equalsIgnoreCase(FuzzyFlag)) {
                                    blike=true;
                                }
						        ArrayList fieldlist = new ArrayList();
								if((!userView.isSuper_admin())){
									whereSQL=userView.getPrivSQLExpression(querycond+"|"+factor,arr[j],false,blike,true,fieldlist);
									whereSQL="(a0100 in (select "+arr[j] + "a01.a0100  "+whereSQL+")) ";
								}
						        else
						        {
						            FactorList factorlist=new FactorList(querycond,factor,arr[j],false ,blike,true,1,userView.getUserId());
						            fieldlist=factorlist.getFieldList();
						            whereSQL=factorlist.getSqlExpression();
						            whereSQL="(a0100 in (select "+arr[j] + "a01.a0100  "+whereSQL+")) ";
						        }
							}
							StringBuffer buf = new StringBuffer();
							buf.append("insert into "+htable1);
							buf.append("(total,nbase,"+keyColumn+")");
							buf.append(" select count(a0100) as total,'"+arr[j]+"' as nbase ,");
							buf.append("m."+keyColumn+" from "+arr[j]+"A01 A01,");
							buf.append(" (select "+ keyColumn +" from "+ tableName +" group by "+ keyColumn +") m");
							buf.append(" where "+ wherestr);
							buf.append(" and "+whereSQL);
							if(!this.userView.isSuper_admin()) {
                                buf.append(" and   a0100 in(select "+arr[j] + "a01.a0100 "+whereA0100In+") ");
                            }
							buf.append(" group by m."+ keyColumn);
							dao.insert(buf.toString(), new ArrayList());//将区分人员库的数据存入临时表T#su_mus1
						}
						
						StringBuffer insertsql = new StringBuffer();
						insertsql.append("insert into " + htable2 + " (" + keyColumn + ",total)");
						insertsql.append(" select " + keyColumn +", sum(total) as total from " + htable1);
						insertsql.append(" group by "+keyColumn);
						dao.insert(insertsql.toString(), new ArrayList());//将汇总人员库的数据存入临时表T#su_mus2
						
						String destTab = tableName;// 目标表
						String srcTab = htable2;// 源表
						String strJoin = "";// 关联串
						if("E01A1".equalsIgnoreCase(keyColumn)) {
                            strJoin = tableName + ".E01A1=" + htable2 + ".E01A1";
                        } else if("B0110".equalsIgnoreCase(keyColumn)) {
                            strJoin = tableName + ".B0110=" + htable2 + ".B0110";
                        }
						String strSet = tableName + ".C"+gridno + "=" + htable2 + ".total";// 更新串
						String strDWhere = "";// 更新目标的表过滤条件
						String strSWhere = "";// 源表的过滤条件
						String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
						dao.update(update, new ArrayList());//更新高级花名册
					}
					else//统计取值
					{
						String NameValue="SUM";
						if(extattr.indexOf("<NAMEVALUE>")!=-1)
				     	{
							NameValue=extattr.substring(extattr.indexOf("<NAMEVALUE")+"<NAMEVALUE>".length(), extattr.indexOf("</NAMEVALUE>"));
				     	}
						String NameExpr="";
						if(extattr.indexOf("<NAMEEXPR>")!=-1)
				     	{
							NameExpr=extattr.substring(extattr.indexOf("<NAMEEXPR")+"<NAMEEXPR>".length(), extattr.indexOf("</NAMEEXPR>"));
				     	}
						String[] arr=nbase.split(",");
						
						for(int j=0;j<arr.length;j++)
						{
							if(arr[j]==null|| "".equals(arr[j])||privDb.toUpperCase().indexOf(arr[j].toUpperCase())==-1) {
                                continue;
                            }
							String existSql ="";// this.getExistSql("1", arr[j], tableName);
							String inSql ="";// this.getInSql("1", arr[j], tableName);
							String whereSQL = "";
							String FSQL = "";
							String tempTableName2 = "";
							String whereA0100In=InfoUtils.getWhereINSql(this.userView,arr[j]);
							if(querycond==null|| "".equals(querycond))//复杂查询
							{
								/**创建筛选条件表达式的临时表，将该表另存，在创建取值表达式的临时表用两个表来算统计*/
								YksjParser yp = new YksjParser(
						    			getUserView()//Trans交易类子类中可以直接获取userView
						    			,alUsedFields
						    			,YksjParser.forSearch
						    			,YksjParser.LOGIC//此处需要调用者知道该公式的数据类型
						    			,YksjParser.forPerson
						    			,"gw",arr[j]);
								YearMonthCount ycm = null;	
								yp.setSupportVar(true);  //支持临时变量
								if(factor==null|| "".equals(factor.trim())) {
                                    factor=" 1=1 ";
                                }
								
								yp.setExistWhereText(existSql);
								yp.run_Where(factor, ycm,"","", dao, inSql,this.conn,"A", null); 
								whereSQL=yp.getSQL();
								String tempTableName = yp.getTempTableName();
								StringBuffer sql = new StringBuffer();
								String ttn="hmuster";
								String temp=ttn+"_TEMP";
								if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								{
									sql.append("CREATE GLOBAL TEMPORARY TABLE  "+ttn+"_TEMP On Commit Preserve Rows  as select * from "+tempTableName);
								}
								else
								{
									sql.append("select * into ##"+ttn+"_TEMP from "+tempTableName);
									temp="##"+ttn+"_TEMP";
								}
								if(dbw.isExistTable(temp, false))
								{
									dbw.dropTable(temp);
								}
								dao.update(sql.toString());
								whereSQL="(a0100 in (select a0100 from "+temp+" where "+whereSQL+")) ";
								yp = new YksjParser(
						    			getUserView()
						    			,alUsedFields
						    			,YksjParser.forSearch
						    			,YksjParser.FLOAT
						    			,YksjParser.forPerson
						    			,"gw",arr[j]);
								YearMonthCount ycm2 = null;	
								yp.setSupportVar(true);  //支持临时变量
								yp.setExistWhereText(existSql);
								yp.run_Where(NameExpr, ycm,"","", dao, inSql,this.conn,"N", null); 
								FSQL=yp.getSQL();
								tempTableName2 = yp.getTempTableName();
							}
							else//通用查询
							{
								YksjParser yp = new YksjParser(
						    			getUserView()
						    			,alUsedFields
						    			,YksjParser.forSearch
						    			,YksjParser.FLOAT
						    			,YksjParser.forPerson
						    			,"gw",arr[j]);
								YearMonthCount ycm = null;	
								yp.setSupportVar(true);  //支持临时变量
								yp.setExistWhereText(existSql);
								yp.run_Where(NameExpr, ycm,"","", dao, inSql,this.conn,"N", null); 
								FSQL=yp.getSQL();
								tempTableName2 = yp.getTempTableName();
								boolean blike=false;
						        if("true".equalsIgnoreCase(FuzzyFlag)) {
                                    blike=true;
                                }
						        ArrayList fieldlist = new ArrayList();
								if((!userView.isSuper_admin()))
						        {
						            whereSQL=userView.getPrivSQLExpression(querycond+"|"+factor,arr[j],false,blike,true,fieldlist);
						        }
						        else
						        {
						            FactorList factorlist=new FactorList(querycond,factor,arr[j],false ,blike,true,1,userView.getUserId());
						            fieldlist=factorlist.getFieldList();
						            whereSQL=factorlist.getSqlExpression();
						            whereSQL="(a0100 in (select "+arr[j] + "a01.a0100  "+whereSQL+")) ";
						        }
							}
							StringBuffer buf = new StringBuffer();
							buf.append("insert into "+htable1);
							buf.append("(total,nbase,"+keyColumn+")");
							buf.append(" select "+NameValue+"("+FSQL+") as total,'"+arr[j]+"' as nbase ,");
							buf.append("m."+keyColumn+" from "+tempTableName2+" A01,");
							buf.append(" (select "+ keyColumn +" from "+ tableName +" group by "+ keyColumn +") m");
							buf.append(" where "+ wherestr);
							buf.append(" and "+whereSQL);
							if(!this.userView.isSuper_admin()) {
                                buf.append(" and a0100 in(select "+arr[j] + "a01.a0100 "+whereA0100In+") ");
                            }
							buf.append(" group by m."+ keyColumn);
							dao.insert(buf.toString(), new ArrayList());//将区分人员库的数据存入临时表T#su_mus1
						}
						StringBuffer insertsql = new StringBuffer();
						insertsql.append("insert into " + htable2 + " (" + keyColumn + ",total)");
						insertsql.append(" select " + keyColumn +", sum(total) as total from " + htable1);
						insertsql.append(" group by "+keyColumn);
						dao.insert(insertsql.toString(), new ArrayList());//将汇总人员库的数据存入临时表T#su_mus2
						
						String destTab = tableName;// 目标表
						String srcTab = htable2;// 源表
						String strJoin = "";// 关联串
						if("E01A1".equalsIgnoreCase(keyColumn)) {
                            strJoin = tableName + ".E01A1=" + htable2 + ".E01A1";
                        } else if("B0110".equalsIgnoreCase(keyColumn)) {
                            strJoin = tableName + ".B0110=" + htable2 + ".B0110";
                        }
						String strSet = tableName + ".C"+gridno + "=" + htable2 + ".total";// 更新串
						String strDWhere = "";// 更新目标的表过滤条件
						String strSWhere = "";// 源表的过滤条件
						String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
						dao.update(update, new ArrayList());//更新高级花名册
					}
				}
				if(dbw.isExistTable(htable1, false))//将临时表删除
	    		{
	    			dbw.dropTable(table1);
	    		}
				if(dbw.isExistTable(htable2, false))//将临时表删除
	    		{
	    			dbw.dropTable(table2);
	    		}
	    		//liuy 优化高级花名册公式取数慢  end
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(rs!=null) {
                    rs.close();
                }
				if(ars!=null) {
                    ars.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据 (默认在职人员库)
	 */
	public void photoCreatClum(String tabid, String tableName,String dbpre, 
			DbWizard dbWizard)throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String tableA00 = dbpre+"A00";
		Table table = new Table(tableName);
		StringBuffer fieldsSql = new StringBuffer("");
		ArrayList fieldList = new ArrayList(); // 记下人员名单的字段名称
		String department_no = ""; // 所属部门
		fieldsSql.append("select GridNo,Field_Name");
		fieldsSql.append("  from muster_cell where tabid=");
		fieldsSql.append(tabid);
		fieldsSql.append(" and flag='P'");
		try {
			rowSet = dao.search(fieldsSql.toString());
			ArrayList fieldsList = new ArrayList();
			ArrayList missFlds = new ArrayList();
			while (rowSet.next()) {
				String fieldname = rowSet.getString("Field_Name");
				String gridNo = rowSet.getString("GridNo");
		
				Field a_temp = new Field("C" + gridNo, fieldname);
				a_temp.setDatatype(DataType.BLOB);
				a_temp.setVisible(false);
				a_temp.setAlign("left");
				fieldsList.add(a_temp);
                if(!dbWizard.isExistField(tableName, "C" + gridNo,false)) {
                    missFlds.add(a_temp);
                }
			}
            for (int i = 0; i < missFlds.size(); i++){
                Field a_temp =(Field) missFlds.get(i);
                table.addField(a_temp);
            }
            if(missFlds.size()>0){
                dbWizard.addColumns(table);
            }
            //达梦、oracle、db2 照片特殊处理 兼容达梦数据库
            StringBuffer updateSql_ole = new StringBuffer();
			StringBuffer updateSql = new StringBuffer();
			updateSql.append("update ");
			updateSql.append(tableName);
			updateSql.append(" set ");
			
			
			
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG||Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2) {
				 updateSql_ole.append("update ");
				 updateSql_ole.append(tableName);
				 updateSql_ole.append(" set ");
				 StringBuffer sqlstr= new StringBuffer("(select ");
				 StringBuffer sqlstr_ole = new StringBuffer("(select ");
				 updateSql.append("(");
				 for (int i = 0; i < fieldsList.size(); i++){
					 Field a_temp =(Field) fieldsList.get(i);
					 updateSql_ole.append(tableName+"."+a_temp.getName()+" = ");
					 sqlstr_ole.append(tableA00+".Ole");
				 }
				 updateSql.append(tableName+".ext)=");
				 sqlstr.append(tableA00+".ext from ");
				 sqlstr.append(tableA00);
				 sqlstr.append(" where "+tableA00+".Flag='P' ");
				 sqlstr.append(" and "+tableName+".A0100="+tableA00+".A0100 and upper("+tableName+".nbase)='"+dbpre.toUpperCase()+"')");
				 
				 sqlstr_ole.append(" from ");
				 sqlstr_ole.append(tableA00);
				 sqlstr_ole.append(" where "+tableA00+".Flag='P' ");
				 sqlstr_ole.append(" and "+tableName+".A0100="+tableA00+".A0100 and upper("+tableName+".nbase)='"+dbpre.toUpperCase()+"')");
				// sqlstr.append(",(select max(i9999) as i9999,a0100 from "+tableA00+" where "+tableA00+".Flag='P' group by a0100) T where "+tableA00+".Flag='P'");
				 //sqlstr.append(" and  "+tableName+".A0100="+tableA00+".A0100 and T.a0100="+tableA00+".a0100 and T.i9999="+tableA00+".i9999)");
				// sqlstr.append(" where exists (select null from "+tableA00+",( select max(i9999) as i9999,a0100 from "+tableA00+" where "+tableA00+".Flag='P' group by a0100) T");
				// sqlstr.append(" where  T.a0100="+tableA00+".a0100 and T.i9999="+tableA00+".i9999 and "+tableA00+".Flag='P')");
		
				 updateSql.append(sqlstr.toString());
				 updateSql_ole.append(sqlstr_ole.toString());
				 
				 updateSql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
				 updateSql_ole.append(" where upper(nbase)='"+dbpre.toUpperCase()+"'");
			}else{
				 for (int i = 0; i < fieldsList.size(); i++){
					 Field a_temp =(Field) fieldsList.get(i);
					 updateSql.append(tableName+"."+a_temp.getName()+"=");
					 updateSql.append(tableA00+".Ole,");
				 }
				 updateSql.append(tableName+".ext=");
				 updateSql.append(tableA00+".ext");
				 updateSql.append(" from ");
				 updateSql.append(tableName);
				 updateSql.append(" LEFT JOIN ");
				 updateSql.append(tableA00+" ON ");
				 updateSql.append(tableName+".A0100="+tableA00+".A0100");
				 updateSql.append(" where "+tableA00+".Flag='P'");
				 updateSql.append(" and upper("+tableName+".nbase)='"+dbpre.toUpperCase()+"'");
				 updateSql.append(" and "+tableA00+".A0100 IN(");
				 updateSql.append("SELECT A0100 FROM "+tableName+" where upper(nbase)='"+dbpre.toUpperCase()+"'"+")");
			}
			
            if(fieldsList.size()>0){
                dao.update(updateSql.toString());
                if(updateSql_ole.length()>0) {
                	dao.update(updateSql_ole.toString());
                }
            } 

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	public String  getFieldArr(String dbpre,String infor){
		String fieldsetStr="";
		sortitem=sortitem!=null?sortitem:"";
		HashSet set = new HashSet();
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
					if(item!=null){
						String setid=item.getFieldsetid();
						if("B0110".equalsIgnoreCase(itemarr[0])|| "E0122".equalsIgnoreCase(itemarr[0])|| "E01A1".equalsIgnoreCase(itemarr[0])){
							if("2".equals(infor)) {
                                setid="B01";
                            } else if("3".equals(infor)) {
                                setid="K01";
                            }
						}
						set.add(setid);
					}
				}
			}
		}
		for (Iterator t = set.iterator(); t.hasNext();) {
			String tt = (String) t.next();
			if(tt!=null&&tt.trim().length()>0) {
                fieldsetStr+=tt+",";
            }
		}
		return fieldsetStr;
	}
	private StringBuffer orderLeftJoin=new StringBuffer("");
	private StringBuffer orderSelect = new StringBuffer("");
	public String  getSortStr(String dbpre,String infor,String history){
		StringBuffer fieldsetStr= new StringBuffer(" order by ");
		sortitem=sortitem!=null?sortitem:"";
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
					if(item!=null){
						String setid=item.getFieldsetid();
						/**oracle按照汉字排序，默认是根据二进制编码排的，此处改为按拼音编码排*/
						if("A".equalsIgnoreCase(item.getItemtype())&& "0".equals(item.getCodesetid())&&Sql_switcher.searchDbServer()==Constant.ORACEL)
						{
							fieldsetStr.append("nlssort(");
							fieldsetStr.append(dbpre);
							if("1".equals(infor)&&"ALL".equals(dbpre))//changxy 20160902 [22264] 查询全部人员库报错  不使用setid 排序sql 别名与 插入查询sql别名不一致
                            {
                                fieldsetStr.append("A01");
                            } else {
                                fieldsetStr.append(setid);
                            }
							fieldsetStr.append(".");
							fieldsetStr.append(item.getItemid());
							fieldsetStr.append(",'NLS_SORT=SCHINESE_PINYIN_M') ");
							if("0".equals(itemarr[2])) {
                                fieldsetStr.append("DESC,");
                            } else {
                                fieldsetStr.append("ASC,");
                            }
						}
						else
						{
							if("1".equals(infor)&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))
							{
							    if("ALL".equals(dbpre)) {
                                    if(!"1".equals(this.countflag)) {
                                        fieldsetStr.append(" ORG" + i + itemarr[0] + " ");
                                    } else{
                                        if(!this.cellGridNoMap.isEmpty()){
                                            if(this.cellGridNoMap.containsKey(itemarr[0])||("D".equalsIgnoreCase(item.getItemtype())&&this.cellGridNoMap.containsKey(itemarr[0]+"_D"))) {
                                                fieldsetStr.append(" AllA01." + this.cellGridNoMap.get(itemarr[0]) + " ");
                                            } else {
                                                fieldsetStr.append(" AllA01." + itemarr[0] + " ");
                                            }
                                        }else {
                                            fieldsetStr.append(" AllA01." + itemarr[0] + " ");
                                        }
                                    }
                                } else{
							    	if("1".equals(this.countflag)){
							    		fieldsetStr.append((dbpre+item.getFieldsetid())+"."+item.getItemid()+" ");
							    	}else {
                                        fieldsetStr.append(" organization"+i+".a0000 ");
                                    }
							    }
								if("0".equals(itemarr[2])) {
                                    fieldsetStr.append("DESC,");
                                } else {
                                    fieldsetStr.append("ASC,");
                                }
								orderLeftJoin.append(" left join (select * from organization where UPPER(codesetid)='"+item.getCodesetid().toUpperCase()+"') organization"+i);
								orderLeftJoin.append(" on "+dbpre+item.getFieldsetid()+"."+itemarr[0]+"=organization"+i+".codeitemid ");
							}else if("2".equals(infor)&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))){
								fieldsetStr.append(" organization"+i+".a0000 ");
								if("0".equals(itemarr[2])) {
                                    fieldsetStr.append("DESC,");
                                } else {
                                    fieldsetStr.append("ASC,");
                                }
								if("B0110".equalsIgnoreCase(itemarr[0])|| "E0122".equalsIgnoreCase(itemarr[0])|| "E01A1".equalsIgnoreCase(itemarr[0])){
						    		if("2".equals(infor)) {
                                        setid="B01";
                                    } else if("3".equals(infor)) {
                                        setid="K01";
                                    }
						    	}else{
						    		setid=item.getFieldsetid();
						    	}
								// 单位库中有单位也有部门
								//orderLeftJoin.append(" left join (select * from organization where UPPER(codesetid)='"+item.getCodesetid().toUpperCase()+"') organization"+i);
								orderLeftJoin.append(" left join (select * from organization) organization"+i);
								orderLeftJoin.append(" on "+setid+"."+itemarr[0]+"=organization"+i+".codeitemid ");
								
							}else if("3".equals(infor)&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))){
								fieldsetStr.append(" organization"+i+".a0000 ");
								if("0".equals(itemarr[2])) {
                                    fieldsetStr.append("DESC,");
                                } else {
                                    fieldsetStr.append("ASC,");
                                }
								if("B0110".equalsIgnoreCase(itemarr[0])|| "E0122".equalsIgnoreCase(itemarr[0])|| "E01A1".equalsIgnoreCase(itemarr[0])){
						    		if("2".equals(infor)) {
                                        setid="B01";
                                    } else if("3".equals(infor)) {
                                        setid="K01";
                                    }
						    	}else{
						    		setid=item.getFieldsetid();
						    	}
								orderLeftJoin.append(" left join (select * from organization where UPPER(codesetid)='"+item.getCodesetid().toUpperCase()+"') organization"+i);
								orderLeftJoin.append(" on "+setid+"."+itemarr[0]+"=organization"+i+".codeitemid ");

							}
							else{
					    		fieldsetStr.append(dbpre);
						    	if("B0110".equalsIgnoreCase(itemarr[0])|| "E0122".equalsIgnoreCase(itemarr[0])|| "E01A1".equalsIgnoreCase(itemarr[0])){
						    		if("2".equals(infor)) {
                                        setid="B01";
                                    } else if("3".equals(infor)) {
                                        setid="K01";
                                    }
						    	}
						    	//liuy 2015-2-6 7359：主页的花名册中点击企业员工月度发放台账后台报“无法绑定由多个部分组成的标示符” start
						    	if("ALL".equalsIgnoreCase(dbpre)) {
                                    fieldsetStr.append("A01");
                                } else {
                                    fieldsetStr.append(setid);
                                }
						    	//liuy 2015-2-6 end
						    	fieldsetStr.append(".");
						    	if("ALL".equalsIgnoreCase(dbpre)&&
						    	(this.cellGridNoMap.containsKey(itemarr[0].toUpperCase())||("D".equalsIgnoreCase(item.getItemtype())&&
						    			this.cellGridNoMap.containsKey(itemarr[0].toUpperCase()+"_D"))))
						    	{
						    		if(this.cellGridNoMap.containsKey(itemarr[0].toUpperCase()+"_D"))//日期型
                                    {
                                        fieldsetStr.append(this.cellGridNoMap.get(itemarr[0].toUpperCase()+"_D"));//按日期型指标排序 时 取XX_D的指标排序
                                    } else {
                                        fieldsetStr.append(this.cellGridNoMap.get(itemarr[0].toUpperCase()));
                                    }
						    	}
				    			else {
                                    fieldsetStr.append(item.getItemid());
                                }
//						    	fieldsetStr.append(item.getItemid());
						     	fieldsetStr.append(" ");
						    	if("0".equals(itemarr[2])) {
                                    fieldsetStr.append("DESC,");
                                } else {
                                    fieldsetStr.append("ASC,");
                                }
							}
						}
					}	
				}
			}
		}
		if("1".equals(infor)&&fieldsetStr.toString().trim().length()>8) {
        	//order by XXX,A0000 排序 查询部分历史记录 时应按照 先按人 排序 然后再排其他指标 34431
        	String str=fieldsetStr.substring(9).toString();
        	if("2".equals(history)||"3".equals(history)) {
        		if(!"1".equals(this.countflag)) {
//            		fieldsetStr.append(dbpre + "A01.A0000,");
            		str="order by "+str+dbpre + "A01.A0000";
            	}
            	else {
            		str="order by "+str+" A0000";
//            		fieldsetStr.append("A0000,");	
            	}
        	}else {
        		str="order by "+(str.endsWith(",")?str.substring(0, str.length()-1):str);
        	}
        	
        	fieldsetStr.setLength(0);
        	fieldsetStr.append(str);
        }
		if(",".equals(fieldsetStr.substring(fieldsetStr.length()-1, fieldsetStr.length()))) {
			return fieldsetStr.substring(0, fieldsetStr.length()-1);
		}
		return fieldsetStr.toString();
	}
	
    private boolean hasOrgSortItem(){
        sortitem=sortitem!=null?sortitem:"";
        String arr[] = sortitem.split("`");
        for(int i=0;i<arr.length;i++){
            if(arr[i]!=null&&arr[i].trim().length()>0){
                String[] itemarr = arr[i].split(":");
                if(itemarr!=null&&itemarr.length==3){
                    FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
                    if(item!=null){
                        if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
                                "@K".equalsIgnoreCase(item.getCodesetid())) {
                            return true;
                        }
                    }   
                }
            }
        }
        return false;
    }
	
	public String  getGroupStr(String dbpre,String infor){
		StringBuffer fieldsetStr= new StringBuffer(" group by ");
		if("1".equals(infor)) {
            fieldsetStr.append(dbpre+"A01.A0100");
        } else if("2".equals(infor)) {
            fieldsetStr.append("B01.B0110");
        } else if("3".equals(infor)) {
            fieldsetStr.append("K01.E01A1");
        } else {
            fieldsetStr.append(dbpre+"A01.A0100");
        }
		sortitem=sortitem!=null?sortitem:"";
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
					FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
					if(item!=null){
						String setid=item.getFieldsetid();
						fieldsetStr.append(","+dbpre);
						if("B0110".equalsIgnoreCase(itemarr[0])|| "E0122".equalsIgnoreCase(itemarr[0])|| "E01A1".equalsIgnoreCase(itemarr[0])){
							if("2".equals(infor)) {
                                setid="B01";
                            } else if("3".equals(infor)) {
                                setid="K01";
                            }
						}
						
						fieldsetStr.append(setid);
						fieldsetStr.append(".");
						fieldsetStr.append(item.getItemid());
					}	
				}
			}
		}
		return fieldsetStr.toString();
	}


	/**
	 * 根据花名册id得到 可取某次历史纪录的子集列表
	 * 
	 * @return list
	 */
	public ArrayList getSetList(String tabid) throws GeneralException {
		ArrayList list = new ArrayList();

		String sql1 = "select distinct SetName from muster_cell where Tabid="+ tabid;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		RowSet rowSet = null;
		try {
			recset = dao.search(sql1);
			while (recset.next()) {
				String setName = recset.getString("SetName");

				if (setName != null && !"A01".equals(setName)&& !"B01".equals(setName) && !"K01".equals(setName)&& !"".equals(setName)) {
					String sql2 = "select itemid,itemdesc from fielditem where (itemdesc like '"+ ResourceFactory.getProperty("hmuster.label.nybs")+ "' or itemdesc like '"
							+ ResourceFactory.getProperty("hmuster.label.counts")+ "') and fieldsetid='" + setName + "'  ";
					sql2+=" union "+"select itemid,itemdesc from t_hr_busifield where (itemdesc like '"+ ResourceFactory.getProperty("hmuster.label.nybs")+ "' or itemdesc like '"
							+ ResourceFactory.getProperty("hmuster.label.counts")+ "') and fieldsetid='" + setName + "'  ";
				    rowSet = dao.search(sql2);

					int i = 0;
					String temp[] = new String[3];
					while (rowSet.next()) {
						i++;
						temp[0] = setName;
						if (rowSet.getString("itemdesc").equals(ResourceFactory.getProperty("hmuster.label.nybs"))) {
                            temp[1] = rowSet.getString("itemid");
                        }
						if (rowSet.getString("itemdesc").equals(ResourceFactory.getProperty("hmuster.label.counts"))) {
                            temp[2] = rowSet.getString("itemid");
                        }
					}

					if (i != 0) {
						if(temp[1]==null) {
                            temp[1]=setName+"Z0";
                        }
						if(temp[2]==null) {
                            temp[2]=setName+"Z1";
                        }
						list.add(temp);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 得到一个子集视图表的sql语句，通过它可控制数据范围选取的纪录
	 * 
	 * @param tlist
	 *            getSetList(String tabid)
	 * @param fullName
	 *            表全称
	 * @param fieldName
	 *            从fielditem中的到的表名
	 * @param history
	 *            1:最后一条历史纪录 3：某次历史纪录 2：部分历史纪录
	 * @param selectPoint
	 *            取部分历史纪录选中的子标
	 * @param fromCope
	 *            范围
	 * @param toCope
	 *            范围
	 * @param year
	 * @param month
	 * @param count
	 * @author dengc created: 2006/03/23
	 */
	public String getView(String infor_flag, ArrayList tlist, String fullName,
			String fieldName, String history, String selectPoint,
			String fromCope, String toCope, String year, String month,
			String count,StringBuffer whereBuf,String nbase) {
		StringBuffer viewSql = new StringBuffer("");
		String tempName = fullName;
		String[] temp = null;
		String aSetName = null;

		if (!"0".equals(history)) {
			int i = 0;
			if ("3".equals(history)) {
				for (Iterator t = tlist.iterator(); t.hasNext();) {
					String[] tt = (String[]) t.next();
					if (tt != null) {
						if (tt[0].equals(fieldName)) {
							FieldSet vo = DataDictionary.getFieldSetVo(fieldName.toLowerCase());
							if (vo == null) {
                                continue;
                            }
							StringBuffer tempTable = new StringBuffer("");
							tempTable.append(tempName);
							viewSql.append("(select * from "+tempName);
							viewSql.append(" where ");
							viewSql.append("(");
							viewSql.append(Sql_switcher.year(tempName+"."+tt[1]));
							viewSql.append("=");
							viewSql.append(year);
							if("4".equals(this.flag))
							{
								viewSql.append(" and ");
								viewSql.append(Sql_switcher.month(tempName+"."+tt[1]));
								viewSql.append("=");
								viewSql.append(month);
							}
							viewSql.append(" and ");
							viewSql.append(tempName+"."+tt[2]);
							viewSql.append("=");
							viewSql.append(count+"");
							viewSql.append(")) "+tempName);
							i++;
						}
					}
				}

			} else if ("2".equals(history)) {//取部分历史记录时，除定义条件的子集外，全部取当前记录

				if (selectPoint != null) {
					temp = selectPoint.split("/");
					if(temp!=null&&temp.length>1) {
                        aSetName = temp[3].trim(); // 得到选取的按部分查询选择的指标的指标集
                    }
				}
				if(temp!=null&&temp.length>1){
					if (!"0".equals(((FieldSet) DataDictionary.getFieldSetVo(fieldName.toLowerCase())).getChangeflag())&& "Z0".equals(temp[0])) {
						aSetName = fieldName;
						temp[3] = fieldName;
						temp[0] = fieldName + "Z0";
					}
					if (fieldName.equals(aSetName)) {
                        if("1".equals(infor_flag)&& "Z03".equals(tempName)) {
                            viewSql.append("("+getZ03View(nbase)+") Z03");
                        } else {
                            viewSql.append(tempName);
                        }
                        if((temp[0].equalsIgnoreCase(aSetName+"Z0")&&whereBuf.toString().length()==0)||!temp[0].equalsIgnoreCase(aSetName+"Z0")){
                        	if (fromCope != null && !"".equals(fromCope)) {
                        		if(whereBuf.toString().length()<=0) {
                                    whereBuf.append(" where ");
                                } else {
                                    whereBuf.append(" and ");
                                }
                        		whereBuf.append(getStr(temp, fromCope, 1,tempName));
                        		
                        	}
                        	if (fromCope != null && !"".equals(fromCope)&& toCope != null && !"".equals(toCope)) {
                        		if(whereBuf.toString().length()<=0) {
                                    whereBuf.append(" where ");
                                } else {
                                    whereBuf.append(" and ");
                                }
                        		whereBuf.append(getStr(temp, toCope, 2,tempName));
                        		
                        	} else if (toCope != null && !"".equals(toCope)) {
                        		if(whereBuf.toString().length()<=0) {
                                    whereBuf.append(" where ");
                                } else {
                                    whereBuf.append(" and ");
                                }
                        		whereBuf.append(getStr(temp, toCope, 2,tempName));
                        	}
                        	if(tempName.toUpperCase().startsWith(this.personView))
                        	{
                        		if(whereBuf.toString().length()<=0) {
                                    whereBuf.append(" where ");
                                } else {
                                    whereBuf.append(" and ");
                                }
                        		whereBuf.append(" UPPER("+tempName+".nbase)='"+nbase.toUpperCase()+"'");
                        	}
                        }
						i++;
					}
				}else{
					if (fromCope != null && !"".equals(fromCope)) {
						String[] fomula = fromCope.split("::");
						if(fomula!=null&&fomula.length==3){
							if(fieldName.equalsIgnoreCase(toCope))
							{
							    if("1".equals(infor_flag)&& "Z03".equals(tempName)) {
                                    viewSql.append("("+getZ03View(nbase)+") Z03");
                                } else {
                                    viewSql.append(tempName);
                                }
							    if((temp[0].equalsIgnoreCase(aSetName+"Z0")&&whereBuf.toString().length()==0)||!temp[0].equalsIgnoreCase(aSetName+"Z0")){
							    	boolean blike=false;
							    	if("1".equals(fomula[2])) {
                                        blike=true;
                                    }
							    	boolean bresult=true; 
							    	boolean bhis=true;
							    	/*String dbpre = "";
				    	    	if(tempName.trim().length()>3)
				    	    		dbpre = tempName.substring(0,3);*/
							    	try {
							    		//viewSql.append(tempName+".A0100 in(select "+tempName+".A0100 ");
							    		String wherestr="";
							    		//liuy 2015-1-17 6771：中亚时代：高级花名册使用数据视图前台无法取数，急急急 start
							    		if("2".equals(infor_flag)|| "3".equals(infor_flag)||
							    				"1".equals(infor_flag) && tempName.toUpperCase().startsWith(this.personView))
							    		{
							    			FactorList factorlist2 = new FactorList(
							    					PubFunc.keyWord_reback(fomula[0]), PubFunc.reBackWord((fomula[1])), "",
							    					bhis, blike, true, Integer.parseInt(infor_flag), userView.getUserName());	
							    			//wherestr = factorlist2.getSqlExpression();
							    			wherestr = factorlist2.getSingleTableSqlExpression(this.queryField);
							    			
							    		}else
							    		{
							    			FactorList factorlist2 = new FactorList(
							    					PubFunc.keyWord_reback(fomula[0]), PubFunc.reBackWord((fomula[1])),nbase,
							    					bhis, blike, true, Integer.parseInt(infor_flag), userView.getUserName());	//汇总时 业务用户拼接查询条件先不关联管理范围 后面sql 会按照管理范围过滤人员  27938
							    			wherestr =factorlist2.getSqlExpression();
							    			//wherestr = userView.getPrivSQLExpression(PubFunc.keyWord_reback(fomula[0])+"|"+PubFunc.reBackWord(fomula[1]),nbase,bhis,blike,bresult,new ArrayList());
							    		}
							    		//liuy 2015-1-17 end
							    		if(wherestr.length()>0)
							    		{
							    			if(whereBuf.toString().length()<=0) {
                                                whereBuf.append(" where ");
                                            } else {
                                                whereBuf.append(" and ");
                                            }
							    			if(wherestr.indexOf(" WHERE ")!=-1) {
                                                whereBuf.append("("+wherestr.substring(wherestr.indexOf(" WHERE ")+6)+")");
                                            } else {
                                                whereBuf.append("("+wherestr+")");
                                            }
							    			if(tempName.toUpperCase().startsWith(this.personView)) {
                                                whereBuf.append(" and UPPER("+tempName+".nbase)='"+nbase.toUpperCase()+"'");
                                            }
							    		}
							    	} catch (GeneralException e) {
							    		e.printStackTrace();
							    	}
							    }
							}else
							{
								viewSql.append(getViewSql(infor_flag, tempName, tempName,nbase));
								/*viewSql.append(tempName);
								if(whereBuf.toString().length()<=0)
									whereBuf.append(" where ");
								else
									whereBuf.append(" and ");
								String columnName="a0100";
								if(infor_flag.equals("2"))
									columnName="b0110";
								else if(infor_flag.equals("3"))
									columnName="e01a1";
								whereBuf.append(" "+tempName+".i9999=(select max(i9999) from ");
								whereBuf.append(tempName+" B where B."+columnName+"="+tempName+"."+columnName+") ");*/
							}
							i++;
						}
					}
				}
			}
			if (i == 0) {
				viewSql.append(getViewSql(infor_flag, tempName, tempName,nbase));
				/*viewSql.append(tempName);
				if(whereBuf.toString().length()<=0)
					whereBuf.append(" where ");
				else
					whereBuf.append(" and ");
				String columnName="a0100";
				if(infor_flag.equals("2"))
					columnName="b0110";
				else if(infor_flag.equals("3"))
					columnName="e01a1";
				whereBuf.append(" "+tempName+".i9999=(select max(i9999) from ");
				whereBuf.append(tempName+" B where B."+columnName+"="+tempName+"."+columnName+") ");*/
			}
		} else {
            viewSql.append(tempName);
        }
		return viewSql.toString();

	}
	
	/**
	 * 
	 * @return
	 */
	private String getZ03View(String nbase) {
        String view = "SELECT "+nbase+"A01.A0100,zp_pos_tache.theNumber as I9999,Z03.* " +
            " from "+nbase+"A01,Z03,zp_pos_tache " +
            " where "+nbase+"A01.A0100 = zp_pos_tache.A0100 and zp_pos_tache.zp_pos_id=Z03.Z0301";
        if (!nbase.equalsIgnoreCase(getZpNbase())) {
            view += " and 1=2";
        }
        return view;
	}

	/**
	 * 取得相同纪录最大的一条
	 * 
	 * @param infor_flag
	 * @param tempName
	 * @param tableName
	 * @return
	 */
	public String getViewSql(String infor_flag, String tempName,String tableName,String dbpre) {
		StringBuffer temp_sql = new StringBuffer("");
		if ("1".equals(infor_flag) && "Z03".equals(tableName)) {
            String view = "SELECT "+dbpre+"A01.A0100,zp_pos_tache.theNumber as I9999,Z03.* " +
                " from "+dbpre+"A01,Z03,zp_pos_tache " +
                " where "+dbpre+"A01.A0100 = zp_pos_tache.A0100 and zp_pos_tache.zp_pos_id=Z03.Z0301";
            if (dbpre.equalsIgnoreCase(getZpNbase())) {
                view += " and zp_pos_tache.theNumber = (SELECT MAX(p.theNumber) FROM Z03 s,zp_pos_tache p "+
                               " WHERE p.zp_pos_id=s.Z0301 and p.A0100=zp_pos_tache.A0100)";
            } else {
                view += " and 1=2";
            }
            temp_sql.append("("+view+") Z03");
		}
		else {
    		temp_sql.append("(SELECT * FROM ");
    		temp_sql.append(tempName);
    		temp_sql.append(" A WHERE ");
    		if(tempName.toUpperCase().startsWith(this.personView)) {
                temp_sql.append("  UPPER(A.nbase)='"+dbpre.toUpperCase()+"' and ");
            }
    		temp_sql.append(" A.I9999 =(SELECT MAX(B.I9999) FROM ");
    		temp_sql.append(tempName);
    		temp_sql.append(" B WHERE ");
    		if ("1".equals(infor_flag)) {
    			temp_sql.append(" A.A0100=B.A0100  )) ");
    			temp_sql.append(tableName);
    		} else if ("2".equals(infor_flag)) {
    			temp_sql.append(" A.B0110=B.B0110  )) ");
    			temp_sql.append(tableName);
    		} else if ("3".equals(infor_flag)) {
    			temp_sql.append(" A.E01A1=B.E01A1  )) ");
    			temp_sql.append(tableName);
    		} else if ("5".equals(infor_flag)) {// 基准岗位
    			temp_sql.append(" A.H0100=B.H0100  )) ");
    			temp_sql.append(tableName);
    		}
		}

		return temp_sql.toString();
	}
	/**
	 * 取得相同纪录某次的一条记录
	 * 
	 * @param infor_flag
	 * @param tempName
	 * @param tableName
	 * @return
	 */
	public String getViewSqlcoum(String infor_flag, String tempName,String tableName,String coum) {
		StringBuffer temp_sql = new StringBuffer("");

		temp_sql.append("(SELECT * FROM ");
		temp_sql.append(tempName);
		temp_sql.append(" A WHERE A.I9999 ='"+coum+"') ");
		temp_sql.append(tableName);
	
		return temp_sql.toString();
	}

	public String getStr(String[] temp, String cope, int flag,String tableName) {
		StringBuffer a_sql = new StringBuffer("");
		if(tableName!=null&&tableName.length()>0) {
            a_sql.append(tableName+".");
        }
		a_sql.append(temp[0]);
		if (flag == 1) {
            a_sql.append(">=");
        } else {
            a_sql.append("<=");
        }
		if ("D".equals(temp[2])) {
			a_sql.append(Sql_switcher.charToDate("'" + cope + "'"));
		} else if ("A".equals(temp[2])) {
			a_sql.append("'" + cope + "' ");
		} else if ("N".equals(temp[2])) {
			a_sql.append(cope);
		}
		if(tableName!=null&&tableName.length()>0) {
            a_sql.append(" and ( " + isNotNullNumber(tableName+"."+temp[0]) + " )");
        } else {
            a_sql.append(" and ( " + isNotNullNumber(temp[0]) + " )");
        }
		return a_sql.toString();
	}

	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getField(String[] fields, String dbpre) {
		StringBuffer field = new StringBuffer("");
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);

		}
		FieldItem item = null; 
		if(fields.length==5)//视图指标特殊处理
        {
            item = DataDictionary.getFieldItem(aField,fields[4]);
        } else {
            item = DataDictionary.getFieldItem(aField);
        }
		String setname = "";
		if(fields.length==5) {
            setname=fields[4];
        }
		if (item!=null&&dbpre != null && dbpre.trim().length() > 0) {
			if(setname.toUpperCase().startsWith(this.personView))//人员视图指标，先不往里插入数据
			{
				//setname=item.getFieldsetid();
				String [] temp=null;
				if (selectedPoint != null) {
				    temp = selectedPoint.split("/");
				}
				 if(temp!=null&&temp.length>1){
					if (!"0".equals(((FieldSet) DataDictionary.getFieldSetVo(setname.toLowerCase())).getChangeflag())&& "Z0".equals(temp[0])) {
						 if(this.queryField.toUpperCase().indexOf(setname.toLowerCase())==-1) {
                             this.queryField+=","+setname.toLowerCase();
                         }
					}
				 }
	          if(this.queryField.toUpperCase().indexOf(setname.toUpperCase())==-1) {
                  return " null ";
              }
			}else{
				if(item.getFieldsetid().toUpperCase().startsWith("K")||
				   item.getFieldsetid().toUpperCase().startsWith("B")||//人员名册中加入了岗位指标
				   "Z03".equalsIgnoreCase(item.getFieldsetid())) {
                    setname = item.getFieldsetid();
                } else {
                    setname = dbpre + item.getFieldsetid();
                }
			}
		} else {
			if ("E01A1".equalsIgnoreCase(aField)) {
                setname = "K01";
            } else if ("E0122".equalsIgnoreCase(aField)) {
                setname = "K01";
            } else if ("H0100".equalsIgnoreCase(aField))// 基准岗位
            {
                setname = "H01";
            } else if ("B0110".equalsIgnoreCase(aField))
			{
				if("41".equals(this.modelFlag)) {
                    setname = "K01";
                } else {
                    setname = "B01";
                }
			}
			else{
				if (item!=null) {
                    setname = item.getFieldsetid();
                }
			}
		}
		if(item!=null){
			if ("D".equals(fields[1])) {

				if (item != null && "D".equals(item.getItemtype())) {
					field.append(" case when " +(fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0] + " is null then null");//日期格式为'' 数据库存储日期为1900.01.01 00:00:00.0 不对 changxy
				    if("19".equals(fields[2])){
					     field.append(" when "+Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])+">9 then ");
					     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])), "3","2"));
						 field.append(getString() + "'.'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
						 field.append(" else  ");
						 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])), "3","2"));
						 field.append(getString() + "'.0'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
					}else{
						field.append(" else ");
						if ("6".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
	
						} else if ("7".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])), "3","2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
	
						} else if ("8".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
						} else if ("9".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])), "3","2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
	
						} else if ("10".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("11".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("12".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])), "3","2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("13".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])), "3","2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("14".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
						} else if ("15".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
						} else if ("16".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
						} else if ("17".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
						}else if("18".equals(fields[2])){//日期格式为 yyyy.mm.dd HH:mm changxy
							field.append(Sql_switcher.numberToChar(Sql_switcher.year((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0])));
							field.append(getString() + "'.'" + getString());
							
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
							field.append(getString() + "'.'" + getString());
							
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
							field.append(getString() + "' '" + getString());
							
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.hour((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
							field.append(getString() + "':'" + getString());
							
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.minute((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]))));
							
							
						}else {
							field.append((fields.length==5?fields[4]+this.tmpStr+".":"")+fields[0]);
						}
					}
				    /*xupengyu修改 取到相同的fields[0].substring(fields[0].indexOf(".") + 1)+"_time 名会让sql报错*/
//					if (fields[0].indexOf(".") == -1)
//						field.append(" end as " + fields[0]+"_time");
//					else {
//						field.append(" end as "+ fields[0].substring(fields[0].indexOf(".") + 1)+"_time");
//
//					}
				    field.append(" end ");
				    

				} else {
					field.append(setname + this.tmpStr+"." + aField);
				}
			} else {
				if ("NBASE".equalsIgnoreCase(aField)) {
                    field.append("(select DBName from dbname where UPPER(pre)='"+ dbpre.toUpperCase() + "') ");//as NBASE
                } else {
                    field.append(setname +this.tmpStr+ "." + aField);
                }
			}
		} else {
			if("H0100".equalsIgnoreCase(aField))  // 基准岗位
            {
                field.append(setname +this.tmpStr+ "." + aField);
            } else {
                field.append("null " );//as " + aField
            }
		}
		return field.toString();
	}
	
	/**
	 * 招聘人员库
	 * @return
	 */
	private String getZpNbase() {
	    String s="";
        RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME",this.conn);
        if(vo!=null) {
            s=vo.getString("str_value");
        }
	    return s;
	}
	
	public String getCountField(String[] fields, String dbpre) {
		StringBuffer field = new StringBuffer("");
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);

		}
		FieldItem item = DataDictionary.getFieldItem(aField);
		String setname = "";
		if(fields.length==5) {
            setname=fields[4];
        }
		if (item!=null&&dbpre != null && dbpre.trim().length() > 0) {
			if(setname.toUpperCase().startsWith(this.personView))
			{
					//setname=item.getFieldsetid();
					String [] temp=null;
					if (selectedPoint != null) {
					    temp = selectedPoint.split("/");
					}
					 if(temp!=null&&temp.length>1){
						if (!"0".equals(((FieldSet) DataDictionary.getFieldSetVo(setname.toLowerCase())).getChangeflag())&& "Z0".equals(temp[0])) {
							 if(this.queryField.toUpperCase().indexOf(setname.toLowerCase())==-1) {
                                 this.queryField+=","+setname.toLowerCase();
                             }
						}
					 }
		          if(this.queryField.toUpperCase().indexOf(setname.toUpperCase())==-1) {
                      return " null ";
                  }
			}else{
				if(item.getFieldsetid().toUpperCase().startsWith("K")||item.getFieldsetid().toUpperCase().startsWith("B"))//人员名册中加入了岗位指标
                {
                    setname=item.getFieldsetid();
                } else {
                    setname = dbpre + item.getFieldsetid();
                }
			}
		} else {
			if ("E01A1".equalsIgnoreCase(aField)) {
                setname = "K01";
            } else if ("E0122".equalsIgnoreCase(aField)) {
                setname = "K01";
            } else if ("B0110".equalsIgnoreCase(aField))
			{
				if("41".equals(this.modelFlag)) {
                    setname="K01";
                } else {
                    setname = "B01";
                }
			}
			
			else{
				if (item!=null) {
                    setname = item.getFieldsetid();
                }
			}
		}
		if(item!=null){
			if ("D".equals(fields[1])) {

				if (item != null && "D".equals(item.getItemtype())) {
					String timecount="max("+fields[0]+")";
					field.append(" case when " + timecount + " is null then ''");
					if("19".equals(fields[2])){
					     field.append(" when "+Sql_switcher.month(timecount)+">9 then ");
					     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(timecount)), "3","2"));
						 field.append(getString() + "'.'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
						 field.append(" else  ");
						 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(timecount)), "3","2"));
						 field.append(getString() + "'.0'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
					}else{
						field.append(" else ");
	
						if ("6".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(timecount)));
	
						} else if ("7".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(timecount)), "3","2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(timecount)));
	
						} else if ("8".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
						} else if ("9".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(timecount)), "3","2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
	
						} else if ("10".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(timecount)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("11".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("12".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(timecount)), "3","2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(timecount)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("13".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(timecount)), "3","2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(timecount)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("14".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
						} else if ("15".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
						} else if ("16".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(timecount))));
						} else if ("17".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(timecount)));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(timecount))));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(timecount))));
						}else {
							field.append(timecount);
						}
					}
					if (fields[0].indexOf(".") == -1) {
                        field.append(" end as " + fields[0]+"_time");
                    } else {
						field.append(" end as "+ fields[0].substring(fields[0].indexOf(".") + 1)+"_time");

					}

				} else {
					field.append("max("+setname +this.tmpStr+ "." + aField+")");//as "+aField
				}
			} else {
				if ("NBASE".equalsIgnoreCase(aField)) {
                    field.append("(select DBName from dbname where UPPER(pre)='"+ dbpre.toUpperCase() + "') ");//as NBASE
                } else{
					if("N".equals(fields[1])) {
                        field.append("sum("+setname +this.tmpStr+ "." + aField+") as "+aField);
                    } else {
                        field.append("max("+setname + this.tmpStr+"." + aField+") as "+aField);
                    }
				}
			}
		} else {
			field.append("null ");
		}
		return field.toString();
	}

	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getField(String[] fields) {
		StringBuffer field = new StringBuffer("");
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);

		}
		String id="";
		FieldItem item = null;
		if (aField.indexOf("_") != -1){
			item = DataDictionary.getFieldItem(aField.substring(0, aField.indexOf("_")));
			id = aField.substring(0, aField.indexOf("_"));
		}else{
			item = DataDictionary.getFieldItem(aField);
		}
		/**人事异动中的临时标量，item=null，先把人事异动放开*/
		if(item!=null|| "5".equals(this.modelFlag)){
			if ("D".equals(fields[1])) {

				if ((item != null && "D".equals(item.getItemtype()))|| "5".equals(this.modelFlag)) {
					field.append(" case when " + fields[0] + " is null then ''");
					 if("19".equals(fields[2])){
					     field.append(" when "+Sql_switcher.month(fields[0])+">9 then ");
					     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						 field.append(getString() + "'.'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						 field.append(" else  ");
						 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						 field.append(getString() + "'.0'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					}else{
						field.append(" else ");
	
						if ("6".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							//wangcq 2014-12-01 begin 调整月份、日期为一位时显示格式为完整，例如：2014.01.01
							field.append(getString() + "'.'" + getString());
							field.append(" case when " + Sql_switcher.length(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))) + "=1 then ");
							field.append("'0'" + getString() + Sql_switcher.numberToChar(Sql_switcher.month(fields[0])) + " else ");
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])) + " end ");
							field.append(getString() + "'.'" + getString());
							field.append(" case when " + Sql_switcher.length(Sql_switcher.numberToChar(Sql_switcher.day(fields[0]))) + "=1 then ");
							field.append("'0'" + getString() + Sql_switcher.numberToChar(Sql_switcher.day(fields[0])) + " else ");
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])) + " end ");
							//wangcq 2014-12-01 end
//							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
//							field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
						} else if ("7".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
						} else if ("8".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						} else if ("9".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
	
						} else if ("10".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("11".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("12".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("13".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("14".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
						} else if ("15".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						} else if ("16".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
						}else if ("17".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
	
							field.append(getString() + "'.'" + getString());
	
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(fields[0]))));
						} else if("18".equals(fields[2])){
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString());
							field.append(" case when "+Sql_switcher.month(fields[0])+">9 then '.'"+getString()+Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(" else '.0'"+getString()+Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))+" end ");
							field.append(getString());
							field.append(" case when "+Sql_switcher.day(fields[0])+">9 then '.'"+getString()+Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
							field.append(" else '.0'"+getString()+Sql_switcher.numberToChar(Sql_switcher.day(fields[0]))+" end ");
							field.append(getString());
							field.append(" case when "+PubFunc.hour(fields[0])+">9 then ' '"+getString()+Sql_switcher.numberToChar(PubFunc.hour(fields[0])));
							field.append(" else ' 0'"+getString()+Sql_switcher.numberToChar(PubFunc.hour(fields[0]))+" end ");
							field.append(getString());
							field.append(" case when "+PubFunc.minute(fields[0])+">9 then ':'"+getString()+Sql_switcher.numberToChar(PubFunc.minute(fields[0])));
							field.append(" else ':0'"+getString()+Sql_switcher.numberToChar(PubFunc.minute(fields[0]))+" end ");
						}else {
							field.append(fields[0]);
						}
					}
					if (fields[0].indexOf(".") == -1) {
                        field.append(" end as " + fields[0]+"_time");
                    } else {
						field.append(" end as "+ fields[0].substring(fields[0].indexOf(".") + 1)+"_time");

					}

				} else {
					field.append(aField);
				}
			} else {
				field.append(aField);
			}
		}else{
			field.append("null ");//as " + aField
		}
		return field.toString();
	}

	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getField1(String[] fields, ArrayList fielditemlist) {
		StringBuffer field = new StringBuffer("");
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);
		}
		FieldItem item = null;
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if (aField.equalsIgnoreCase(fielditem.getItemid())) {
                item = fielditem;
            }
		}
		if(item!=null){
			if (item != null && "D".equals(fields[1])) {

				if (item != null && "D".equals(item.getItemtype())) {
					field.append(" case when " + aField + " is null then ''");
					 if("19".equals(fields[2])){
					     field.append(" when "+Sql_switcher.month(fields[0])+">9 then ");
					     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						 field.append(getString() + "'.'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						 field.append(" else  ");
						 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						 field.append(getString() + "'.0'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					}else{
						field.append(" else ");
	
						if ("6".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(aField)));
	
							field.append(getString() + "'.'" + getString());
	
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(aField)));
	
						} else if ("7".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(aField)),"3", "2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(aField)));
	
						} else if ("8".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(aField)));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
						} else if ("9".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(aField)),"3", "2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
	
						} else if ("10".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(aField)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(aField)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("11".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(aField)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("12".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(aField)),"3", "2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(aField)));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(aField)));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("13".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(aField)),"3", "2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("14".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
						} else if ("15".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						} else if ("16".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
						} else if ("17".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(aField)));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(aField))));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(aField))));
						}else {
							field.append(fields[0]);
						}
					}
					field.append(" end ");//as " + aField+"_time
				} else {
					field.append("a." + aField);
				}
			} else {
				if ("NBASE".equalsIgnoreCase(aField)) {
                    field.append("(select DBName from dbname where Pre=a."+ aField + ") ");//as dbname
                } else{
					field.append("a." + aField);
				}
			}
		}else{
			field.append("null ");//as " + aField
		}
		return field.toString();
	}
	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getField2(String[] fields, ArrayList fielditemlist,String cond) {
		StringBuffer field = new StringBuffer("");
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);
		}
		FieldItem item = null;
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if (aField.equalsIgnoreCase(fielditem.getItemid())) {
                item = fielditem;
            }
		}
		if(item!=null){
			if (item != null && "D".equals(fields[1])) {

				if (item != null && "D".equals(item.getItemtype())) {
					field.append(" case when "+cond+"("+ fields[0] + ") is null then ''");
					 if("19".equals(fields[2])){
					     field.append(" when "+Sql_switcher.month(cond+"("+fields[0]+")")+">9 then ");
					     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")), "3","2"));
						 field.append(getString() + "'.'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
						 field.append(" else  ");
						 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")), "3","2"));
						 field.append(getString() + "'.0'" + getString());
						 field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
					}else{
						field.append(" else ");
						if ("6".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
	
							field.append(getString() + "'.'" + getString());
	
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(cond+"("+fields[0]+")")));
	
						} else if ("7".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")),"3", "2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(cond+"("+fields[0]+")")));
	
						} else if ("8".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
						} else if ("9".equals(fields[2])) {
							field
							.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")),"3", "2"));
							field.append(getString() + "'.'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
	
						} else if ("10".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(cond+"("+fields[0]+")")));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("11".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
						} else if ("12".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")),"3", "2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.day(cond+"("+fields[0]+")")));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
						} else if ("13".equals(fields[2])) {
							field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")),"3", "2"));
							field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
							field.append(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")")));
							field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
						} else if ("14".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
							field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
						} else if ("15".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
						} else if ("16".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")"))));
						} else if ("17".equals(fields[2])) {
							field.append(Sql_switcher.numberToChar(Sql_switcher.year(cond+"("+fields[0]+")")));
	
							field.append(getString() + "'.'" + getString());
	
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(cond+"("+fields[0]+")"))));
							field.append(getString() + "'.'" + getString());
							field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(cond+"("+fields[0]+")"))));
	
						}else {
							field.append(cond+"("+fields[0]+")");
						}
					}
					field.append(" end ");//as " + aField+"_time
				}  else {
					field.append(cond+"("+fields[0]+")");
			}
		} else {
			if ("NBASE".equalsIgnoreCase(aField)) {
                field.append("(select DBName from dbname where Pre=a."+ aField + ")");//as dbname
            } else{
				field.append(cond+"("+fields[0]+")");
			}
		}
	}else{
		field.append("null ");//as " + aField
	}
	return field.toString();
}

	// 取sql得到连接符
	public String getString() {
		String operate = "";
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			operate = "+";
			break;
		}
		case Constant.DB2: {
			operate = "+";
			break;
		}
		case Constant.ORACEL: {
			operate = "||";
			break;
		}
		}
		return operate;
	}
	
	public String dateStr(String date){
		StringBuffer datestr = new StringBuffer();;
		switch (Sql_switcher.searchDbServer()) {
		
		case Constant.MSSQL: {
			datestr.append("case when len(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		case Constant.DB2: {
			datestr.append("case when length(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		case Constant.ORACEL: {
			datestr.append("case when length(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		default:{
			datestr.append("case when len(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		}
		return datestr.toString();
	}

	// 取sql得到连接符
	public String getString2() {
		String operate = "";
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			operate = " and ";
			break;
		}
		case Constant.DB2: {
			operate = " and ";
			break;
		}
		case Constant.ORACEL: {
			operate = " or ";
			break;
		}
		}
		return operate;
	}

	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * 
	 * @param tabid
	 *            高级花名册的id
	 * @param history
	 *            1:最后一条历史纪录 3：某次历史纪录 2：部分历史纪录
	 * @param infor_Flag
	 *            信息群标识
	 * @param dbpre
	 *            应用库表前缀
	 * @param queryScope
	 *            查询的范围
	 * @param flag
	 *            "0":无 "1"有子集指标无年月标识,可按最后一条历史纪录查 "2"有子集指标无年月标识,可按取部分历史纪录查
	 *            "3"有子集指标和年月标识，可按某次的历史纪录查
	 * @param year
	 *            年;
	 * @param month 月;
	 * @param count 次
	 * @param fromScope
	 * @param toScope
	 * @param selectedPoint
	 * @param isGroupPoint
	 *            是否选用分组指标 1:选用
	 * @param groupPoint;
	 *            已选的分组指标
	 * @return void
	 * @author dengc created: 2003/03/22
	 */

	public String createSQL2(String history, String userName, String tableName,
			String tabid, String infor_flag, String dbpre, String queryScope,
			String flag, String year, String month, String count,
			String fromScope, String toScope, String selectedPoint,
			String isGroupPoint, String groupPoint) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		boolean isYMB = false; // 是否用到硬编码 分组指标
		if("2".equals(history))
		{
			String [] temp=null;
			if (selectedPoint != null) {
			    temp = selectedPoint.split("/");
			}
			if(temp!=null&&temp.length>1){
				this.queryField = temp[3].trim(); // 得到选取的按部分查询选择的指标的指标集
			}else{
				if (fromScope != null && !"".equals(fromScope)) {
					String[] fomula = fromScope.split("::");
					if(fomula!=null&&fomula.length==3){
						this.queryField=toScope;
			        }
			       }
			}
		}
			/*String [] temp=null;
			if (selectedPoint != null) {
			    temp = selectedPoint.split("/");
			}
			 if(temp!=null&&temp.length>1){
				if (!((FieldSet) DataDictionary.getFieldSetVo(fieldName.toLowerCase())).getChangeflag().equals("0")&& temp[0].equals("Z0")) {
					aSetName = fieldName;
					temp[3] = fieldName;
					temp[0] = fieldName + "Z0";
				}*/
		ArrayList list = existForwardSql_fields(tableName, tabid, infor_flag,isGroupPoint);

		sql.append((String) list.get(0));
		String fields = ((String) list.get(1)).trim();
        String re_name=((String)list.get(2)).trim();
		StringBuffer sql_suffix = new StringBuffer("");
		StringBuffer sql_from = new StringBuffer("");
		StringBuffer sql_whl = new StringBuffer("");
		curInsertFlds = sql.toString();
        curFlds = "";
        curFrom = "";
        curGroupBy = "";
        curOrderBy = "";
      
		String orderSql = ""; // 排序sql
		ArrayList tlist = getSetList(tabid); // 根据花名册id得到 可取某次历史纪录的子集列表
		StringBuffer sql_select = new StringBuffer(" ");
		if ("1".equals(infor_flag)) // 人员库
		{
			String order = getSortStr(dbpre,"1",history);
			order=order!=null?order:"";
			if(order.trim().length()>8) {
                orderSql = order;
            } else{
				if("2".equals(history)){
					String fieldItemZ0="";
					for(int i=0;i<tlist.size();i++){//查询部分历史记录带有年月标识的数据 i9999为null 改为按照年月标识指标排序 29095 中国电科51所-花名册数据排序问题
						String[] setArry=(String[])tlist.get(i);
						if(setArry[1]!=null&&setArry[1].length()>0&&
						   ("Z0".equalsIgnoreCase(setArry[1].substring(3))||
							setArry[1].toUpperCase().startsWith("V_EMP_")&&setArry[1].toUpperCase().endsWith("Z0"))){//查询部分历史记录排序兼容试图指标
							fieldItemZ0+=","+setArry[1];
						}
					}
					orderSql = " order by "+  dbpre + "A01.A0000"+fieldItemZ0;
				}else {
                    orderSql = " order by " + dbpre + "A01.A0000";
                }
			}
			curOrderBy = orderSql;
			sql_select.append("select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append("1 as recidx,");
            }
			/*else
				sql_select.append(" RowNum,");*/
			sql_select.append("'"+dbpre+"' as NBASE,");
			sql_select.append(dbpre);
			sql_select.append("A01.A0000,");
			sql_select.append(dbpre);
			sql_select.append("A01.A0100,");
			sql_select.append(dbpre);
			sql_select.append("A01.B0110,");
			sql_select.append(dbpre);
			sql_select.append("A01.E0122,");
			//插入视图指标取数时 取部分历史记录需要i9999
			if("2".equals(history)&& "1".equals(infor_flag)&&this.personViewName.size()>0&&StringUtils.isNotEmpty(this.queryField)) {
                sql_select.append(this.queryField+".I9999 as I9999");
            } else {
                sql_select.append("null as I9999");
            }
            String temptablename=dbpre+"A01";
            String resulttable = "";
            if(this.userView!=null&&this.userView.getStatus()==4) {
                resulttable = "(select obj_id as a0100 from t_sys_result where UPPER(username)='"
                        + this.userView.getUserName().toUpperCase()
                        + "' and flag=0 and UPPER(nbase)='"
                        + dbpre.toUpperCase() + "')";
            } else {
                resulttable = userName + dbpre + "Result";
            }
            if("true".equalsIgnoreCase(this.showPartJob)) {
                temptablename=this.getShowPartJobSQL(dbpre,queryScope,resulttable);
            }
			if ("1".equals(queryScope) && dbpre != null&& dbpre.trim().length() > 0) // 如果从查询结果里取数
			{
				if("true".equalsIgnoreCase(this.showPartJob)) {
                    sql_from.append("," + temptablename);
                } else{
					if(this.userView!=null&&this.userView.getStatus()==4) {
                        sql_from.append(","+resulttable+" T left join "+temptablename+" on T.a0100="+dbpre+"a01.a0100 ");
                    } else {
                        sql_from.append("," +resulttable+ " T left join "+ temptablename + " on T.A0100=" + dbpre + "A01.A0100");
                    }
				}
			} else {
				dbpre = dbpre != null && dbpre.trim().length() > 0 ? dbpre: "Usr";
				sql_from.append("," + temptablename);
			}

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,organization.codeitemdesc as GroupV");

					sql_from.append(" left join organization on ");
					sql_from.append(dbpre);
					sql_from.append("A01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				} else {
					sql_select.append("," + dbpre + "A01." + groupPoint+ " as GroupN,codeitem.codeitemdesc as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem ");
					sql_from.append(" on   codeitem.codeitemid=" + dbpre+ "A01." + groupPoint);
				}
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(" as GroupN2,org.codeitemdesc as GroupV2");

					sql_from.append(" left join organization org on ");
					sql_from.append(dbpre);
					sql_from.append("A01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				} else {
					sql_select.append("," + dbpre + "A01." + this.groupPoint2+ " as GroupN2,CD.codeitemdesc as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD ");
					sql_from.append(" on   CD.codeitemid=" + dbpre+ "A01." + this.groupPoint2);
				}
			}
		} else if ("2".equals(infor_flag)) // 单位库
		{
			dbpre = "";
			String order = getSortStr(dbpre,"2",history);
			order=order!=null?order:"";
			if(order.trim().length()>8){
				orderSql = order;
			}else{
				//21568 江苏省苏豪控股集团有限公司----表格设置中没有设置任何的排序指标，但是子集多条记录输出时顺序错乱  changxy 20161013  start
				orderLeftJoin.append(" right join (select * from organization where UPPER(codesetid)='UN' or UPPER(codesetid)='UM') organization0");
				orderLeftJoin.append(" on B01.B0110=organization0.codeitemid ");
				orderSql = " order by organization0.codeitemid";
				//21568 end   left join 改为 right join   order by organization0.a0000改为codeitemid
			}   
			sql_select.append("select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append("1,");
            }
			/*else
				sql_select.append(" RowNum,");*/
			sql_select.append(" B01.B0110,null");
			//sql_select.append("select 1, B01.B0110,null");

			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView!=null&&this.userView.getStatus()==4)
				{
					sql_from.append(",(select obj_id as b0110 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					sql_from.append(" and flag=1 ) T");
					sql_from.append(" left join b01 on T.b0110=b01.b0110 ");
				}
				else
				{//liuy 2015-4-7 8351：哈药集团：机构花名册插入取值方法，前台无法取数
			    	sql_from.append(",(select DISTINCT B0110 from "+userName+"BResult) " + userName + "BResult left join B01 on "+ userName + "BResult.B0110=B01.B0110");
				}
			} else {
                sql_from.append(",B01");
            }

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",B01.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,organization.codeitemdesc as GroupV");
					sql_from.append(" left join organization on B01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				} else {
					sql_select.append(",B01." + groupPoint+ " as GroupN,codeitem.codeitemdesc as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem  ");
					sql_from.append(" on codeitem.codeitemid=B01." + groupPoint);
				}
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",B01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(" as GroupN2,org.codeitemdesc as GroupV2");
					sql_from.append(" left join organization org on B01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				} else {
					sql_select.append(",B01." + this.groupPoint2+ " as GroupN2,CD.codeitemdesc as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD  ");
					sql_from.append(" on CD.codeitemid=B01." + this.groupPoint2);
				}
			}
		} else if ("3".equals(infor_flag)) // 职位库
		{
			dbpre = "";
			String order = getSortStr(dbpre,"3",history);
			order=order!=null?order:"";
			if(order.trim().length()>8){
				orderSql = order;
			}else{
				orderLeftJoin.append(" left join (select * from organization where UPPER(codesetid)='@K') organization0");
				orderLeftJoin.append(" on K01.e01a1=organization0.codeitemid ");
				orderSql = " order by organization0.a0000";
			}
			sql_select.append("select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append("1,");
            }
			/*else
				sql_select.append(" RowNum,");*/
			sql_select.append(" K01.E01A1,null");
			//sql_select.append("select 1, K01.E01A1,null");
			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView!=null&&this.userView.getStatus()==4)
				{
					sql_from.append(",(select obj_id as e01a1 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					sql_from.append(" and flag=2 ) T");
					sql_from.append(" left join k01 on T.e01a1=k01.e01a1 ");
				}
				else
				{//liuy 2015-4-7 8351：哈药集团：机构花名册插入取值方法，前台无法取数
			     	sql_from.append(",(select DISTINCT E01A1 from "+userName+"KResult) " + userName + "KResult left join K01 on "+ userName + "KResult.E01A1=K01.E01A1");
				}
			} else {
                sql_from.append(",K01");
            }

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",K01.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,organization.codeitemdesc as GroupV");
					sql_from.append(" left join organization on K01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				}
				else {
					sql_select.append(",K01.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,codeitem.codeitemdesc as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem ");
					sql_from.append("on  codeitem.codeitemid=K01." + groupPoint);

				}
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",K01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(" as GroupN2,org.codeitemdesc as GroupV2");
					sql_from.append(" left join organization org on K01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				}
				else {
					sql_select.append(",K01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(" as GroupN2,CD.codeitemdesc as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD ");
					sql_from.append("on  CD.codeitemid=K01." + this.groupPoint2);

				}
			}
		}else if ("5".equals(infor_flag)) // 基准岗位
		{
			dbpre = "";
			String order = getSortStr(dbpre,infor_flag,history);
			order=order!=null?order:"";
			if(order.trim().length()>8){
				orderSql = order;
			}else{
				orderLeftJoin.append(" left join (select * from codeitem where UPPER(codesetid)='"+getStdPosCodeSetId()+"') c0");
				orderLeftJoin.append(" on H01.H0100=c0.codeitemid ");
				orderSql = " order by c0.A0000";
			}
			sql_select.append("select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append("1,");
            }
			/*else
				sql_select.append(" RowNum,");*/
			sql_select.append(" H01.H0100,null");
			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				sql_from.append(",(select obj_id as H0100 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
				sql_from.append(" and flag=5 ) T");
				sql_from.append(" left join H01 on T.H0100=H01.H0100 ");
			} else {
                sql_from.append(",H01");
            }

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",H01.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,organization.codeitemdesc as GroupV");
					sql_from.append(" left join organization on H01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				}
				else {
					sql_select.append(",H01.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,codeitem.codeitemdesc as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem ");
					sql_from.append("on  codeitem.codeitemid=H01." + groupPoint);

				}
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",H01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(" as GroupN2,org.codeitemdesc as GroupV2");
					sql_from.append(" left join organization org on H01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				}
				else {
					sql_select.append(",H01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(" as GroupN2,CD.codeitemdesc as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD ");
					sql_from.append("on  CD.codeitemid=H01." + this.groupPoint2);

				}
			}
		}
        boolean isWHere=false;
        boolean hasZ03=false;
        DbWizard dbw=new DbWizard(this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer subsql = new StringBuffer("select DISTINCT fieldsetid from fielditem");
		StringBuffer sbf=new StringBuffer("select DISTINCT fieldsetid from t_hr_busifield");
		StringBuffer subsql_whl = new StringBuffer("");
		String[] field = null;
		if (fields != null && !"".equals(fields)) {
            field = fields.split(",");
        }
		
		boolean isBEValue = false;//是否同时取单位部门值
		int e0122_value = 0;
		int b0110_value = 0;
 		if (field != null) {
 			String [] _rename=re_name.split(",");
			subsql.append(" where ");
			sbf.append(" where ");
			
			for (int i = 0; i < field.length; i++) {
				String personGetDeptValue = getPersonGetDeptValue(rowSet, dao, tabid, _rename[i]);
				if("1".equals(personGetDeptValue)) {
                    e0122_value++;
                } else if("0".equals(personGetDeptValue)) {
                    b0110_value++;
                }
			}
			if(e0122_value>0&&b0110_value>0) {
                isBEValue = true;
            }
			
			for (int i = 0; i < field.length; i++) {

				String[] temp0 = field[i].split("/");
				String[] temp1 = field[i].split("/");//field_name + "/" + fieldType + "/" + slop + "/"+ codeId
				
				boolean isDeptValue = false;//是否是取部门值
				if("1".equals(getPersonGetDeptValue(rowSet, dao, tabid, _rename[i]))) {
                    isDeptValue = true;
                }
				
				String temp = "";
				FieldItem item=null;
				if(temp0.length>=5)//插入视图指标 filed记录视图的setname
                {
                    item =DataDictionary.getFieldItem(temp0[0],temp0[4]);
                } else {
                    item =DataDictionary.getFieldItem(temp0[0]);
                }
				if ("2".equals(infor_flag) && temp0[0].indexOf("B01") != -1) {
					temp0[0] = "B01." + temp0[0];
				} else if ("1".equals(infor_flag)&&((item!=null&& "A01".equalsIgnoreCase(item.getFieldsetid()))||
				                "b0110".equalsIgnoreCase(temp0[0])|| "e0122".equalsIgnoreCase(temp0[0])||
				                "e01a1".equalsIgnoreCase(temp0[0]))) {
					temp0[0] = dbpre + "A01." + temp0[0];
				} else if ("3".equals(infor_flag)&& (temp0[0].indexOf("E01") != -1 || temp0[0].indexOf("K01") != -1)) {
					temp0[0] = "K01." + temp0[0];
				} else if ("5".equals(infor_flag)&& (temp0[0].indexOf("H01") != -1)) {// 基准岗位
					temp0[0] = "H01." + temp0[0];
				} else if (item != null && "Z03".equalsIgnoreCase(item.getFieldsetid())) {
                    hasZ03=true;
                }
				temp = getField(temp0, dbpre);
				String gridno=_rename[i];
				if(temp.toUpperCase().indexOf("END")>0||temp.toUpperCase().indexOf(" AS")==-1|| "nbase".equalsIgnoreCase(temp)){//会有指标名称包含AS 前面带个空格
					if (isBEValue&&isDeptValue) {//单位部门值都取并且当前列为取部门值
						String[] str = temp.trim().split("\\.");
						String sub_query = "(select "+str[1]+" from "+str[0]+" where b0110="+dbpre+"A01.E0122)";
						sql_select.append(","+sub_query+" AS C"+gridno);
					}else {
                        sql_select.append(","+temp.trim()+" AS C"+gridno);
                    }
				}else {
                    sql_select.append("," + temp.trim());
                }
				if("D".equals(temp0[1])&& dbw.isExistField(tableName, "D"+gridno, false)) {
                    sql_select.append(","+temp0[0]+" AS D"+gridno);  // 保存日期型数据
                }
				if(!"NBASE".equalsIgnoreCase(temp0[0])) {//bug 号 38267
					subsql_whl.append(" or ( itemid='");
					subsql_whl.append(temp1[0].trim());
					subsql_whl.append("' and fieldsetid='"+item.getFieldsetid()+"')");
				}else {
					if(temp1.length>=5) {// 49886 模板插入人员库指标，视图子集视图人员库指标处理，其余不处理
						subsql_whl.append(" or (itemid='");
						subsql_whl.append(temp1[0].trim());
						subsql_whl.append("'");
						subsql_whl.append(" and fieldsetid='"+item.getFieldsetid()+"')");
					}
				}
			}
			subsql.append(subsql_whl.substring(3));
			sbf.append(subsql_whl.substring(3));
		}

		try {
			int ct=0;
			StringBuffer whereBuffer = new StringBuffer("");
			String strid="";
			if (field != null) {
				rowSet = dao.search(subsql.toString()+" union "+sbf.toString()+"");
				while (rowSet.next()) {
					String fieldsetid = rowSet.getString("fieldsetid");
					if ("1".equals(infor_flag)) // 人员库
					{
						if ("A01".equals(fieldsetid)) {
                            continue;
                        }
						if ("2".equals(history)) {
                            sql_from.append(" left join ");
                        } else {
                            sql_from.append(" left join ");
                        }
						if(fieldsetid.toUpperCase().startsWith("K"))
						{
							if("K01".equalsIgnoreCase(fieldsetid)) {
                                sql_from.append(fieldsetid);
                            } else{
								StringBuffer temp_sql = new StringBuffer("");
								temp_sql.append("(SELECT * FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" A WHERE ");
								temp_sql.append(" A.I9999 =(SELECT MAX(B.I9999) FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" B WHERE ");
								temp_sql.append(" A.E01A1=B.E01A1  )) ");
								temp_sql.append(fieldsetid);
								sql_from.append(temp_sql);
							}
							sql_from.append(" on "+dbpre+"A01.e01a1="+fieldsetid+".e01a1 ");
						}else if(fieldsetid.toUpperCase().startsWith("B"))
						{
							if("B01".equalsIgnoreCase(fieldsetid)) {
                                sql_from.append(fieldsetid);
                            } else{
								StringBuffer temp_sql = new StringBuffer("");
								temp_sql.append("(SELECT * FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" A WHERE ");
								temp_sql.append(" A.I9999 =(SELECT MAX(B.I9999) FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" B WHERE ");
								temp_sql.append(" A.b0110=B.b0110  )) ");
								temp_sql.append(fieldsetid);
								sql_from.append(temp_sql);
							}
							if(e0122_value>0&&b0110_value==0)//只取部门值
                            {
                                sql_from.append(" on "+dbpre+"A01.e0122="+fieldsetid+".b0110 ");
                            } else//取单位值 或 单位部门值都取
                            {
                                sql_from.append(" on "+dbpre+"A01.b0110="+fieldsetid+".b0110 ");
                            }
						}else{
							//此处应判断是否有视图指标
							String tempName = dbpre + fieldsetid;
							if(fieldsetid.toUpperCase().startsWith("V_EMP")) {
                                tempName=fieldsetid;
                            }
							sql_from.append(getView(infor_flag, tlist, tempName,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));
							if(fieldsetid.toUpperCase().startsWith("V_EMP")) {
                                sql_from.append(" on "+dbpre+"A01.A0100="+fieldsetid + ".A0100");
                            } else {
                                sql_from.append(" on "+dbpre+"A01.A0100="+dbpre + fieldsetid + ".A0100");
                            }
							// 如果某主集的子集记录中，没有子集记录满足条件，该主集记录需要保留.
							if("2".equals(history)&&whereBuffer.length()>0){
								String hiscond="";
								if(whereBuffer.indexOf(" where ")!=-1)
				    			{
				     				hiscond = whereBuffer.substring(6);
				    			}
								if (hiscond.length()>0){
									sql_from.append(" and "+hiscond);
									whereBuffer.setLength(0);
									/*//sql_from.append(" and "+hiscond);
									whereBuffer.append(" and "+hiscond);*/
								}
							}
							if(selectedPoint!=null&&selectedPoint.length()>2&&!"Z0/0/D/all".equalsIgnoreCase(selectedPoint)&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								sql_from.append(" and " +dbpre + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+dbpre +strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						}
					} else if ("2".equals(infor_flag)) // 单位库
					{
						if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                            continue;
                        }
						if ("3".equals(history) || "2".equals(history))
//							sql_from.append(" inner join ");
                        {
                            sql_from.append(" left join ");
                        } else {
                            sql_from.append(" left join ");
                        }
						sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

						sql_from.append(" on B01.B0110="+rowSet.getString("fieldsetid")+".B0110");
						if("2".equals(history)&&fieldsetid.equals(toScope)&&whereBuffer.length()>0){
							String hiscond="";
							if(whereBuffer.indexOf(" where ")!=-1)
			    			{
			     				hiscond = whereBuffer.substring(6);
			    			}
							if (hiscond.length()>0){
								sql_from.append(" and "+hiscond);
								whereBuffer.setLength(0);
							}
						}
						if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
							sql_from.append(" and " + fieldsetid);
							sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
							sql_from.append("."+strid.substring(0,3)+"Z0");
						}
					} else if ("3".equals(infor_flag)) // 职位库
					{
						if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)|| fieldsetid.indexOf("A0") != -1) {
                            continue;
                        }
						if ("3".equals(history) || "2".equals(history))
//							sql_from.append(" inner join ");
                        {
                            sql_from.append(" left join ");
                        } else {
                            sql_from.append(" left join ");
                        }
						sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

						sql_from.append(" on " + "K01.E01A1=" + fieldsetid+ ".E01A1");
						if("2".equals(history)&&fieldsetid.equals(toScope)&&whereBuffer.length()>0){
							String hiscond="";
							if(whereBuffer.indexOf(" where ")!=-1)
			    			{
			     				hiscond = whereBuffer.substring(6);
			    			}
							if (hiscond.length()>0){
								sql_from.append(" and "+hiscond);
								whereBuffer.setLength(0);
							}
						}
						if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
							sql_from.append(" and " + fieldsetid);
							sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
							sql_from.append("."+strid.substring(0,3)+"Z0");
						}
					} else if ("5".equals(infor_flag)) // 基准岗位
					{
						if ("H01".equals(fieldsetid)) {
                            continue;
                        }
						sql_from.append(" left join ");
						sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

						sql_from.append(" on " + "H01.H0100=" + fieldsetid+ ".H0100");
						if("2".equals(history)&&fieldsetid.equals(toScope)&&whereBuffer.length()>0){
							String hiscond="";
							if(whereBuffer.indexOf(" where ")!=-1)
			    			{
			     				hiscond = whereBuffer.substring(6);
			    			}
							if (hiscond.length()>0){
								sql_from.append(" and "+hiscond);
								whereBuffer.setLength(0);
							}
						}
						if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
							sql_from.append(" and " + fieldsetid);
							sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
							sql_from.append("."+strid.substring(0,3)+"Z0");
						}
					}
					ct++;
					strid+=fieldsetid+",";
				}
				if("1".equals(infor_flag))
				{
					Set keySet = this.personViewName.keySet();
					for(Iterator it=keySet.iterator();it.hasNext();)
					{
						String key=(String)it.next();	
						String tempName = key;
						if("2".equals(history)&&this.queryField.toUpperCase().indexOf(key.toUpperCase())!=-1){
						    String viewsql = getView(infor_flag, tlist, tempName,key, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre);
							if(viewsql.length()>0&&!(sql_from.indexOf(tempName)>-1))
							{
					    		sql_from.append(" left join "+viewsql+" on "+dbpre+"A01.A0100="+tempName+ ".A0100 ");
								if("2".equals(history)&&tempName.equals(toScope)&&whereBuffer.length()>0){
									String hiscond="";
									if(whereBuffer.indexOf(" where ")!=-1)
					    			{
					     				hiscond = whereBuffer.substring(6);
					    			}
									if (hiscond.length()>0){
										sql_from.append(" and "+hiscond);
										whereBuffer.setLength(0);
									}
								}
							}
						}
					}
					if(hasZ03) {
                        String tempName = "Z03";
                        sql_from.append(" left join ");
                        sql_from.append(getView(infor_flag, tlist, tempName,"Z03", history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));
                        sql_from.append(" on "+dbpre+"A01.A0100=Z03.A0100");

					    // 如果某主集的子集记录中，没有子集记录满足条件，该主集记录需要保留.
                        if("2".equals(history)&&"Z03".equals(toScope)&&whereBuffer.length()>0){
                            String hiscond="";
                            if(whereBuffer.indexOf(" where ")!=-1)
                            {
                                hiscond = whereBuffer.substring(6);
                            }
                            if (hiscond.length()>0){
                                sql_from.append(" and "+hiscond);
                                whereBuffer.setLength(0);
                            }
                        }
					}
				}
			}
			if(this.orderLeftJoin.toString().length()>0)
			{
				sql_from.append(this.orderLeftJoin);
			}
				/**排序指标的子集*/
				String fieldsetStr = getFieldArr(dbpre,infor_flag);
				String[] field_Arr = fieldsetStr.split(",");
				for(int i=0;i<field_Arr.length;i++){
					String fieldsetid = field_Arr[i].toUpperCase();
					if(fieldsetid!=null&&fieldsetid.trim().length()>0){
						if(strid.indexOf(fieldsetid)!=-1) {
                            continue;
                        }
						if ("1".equals(infor_flag)) // 人员库
						{
							if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                                continue;
                            }

							if ("2".equals(history))
//								sql_from.append(" inner join ");
                            {
                                sql_from.append(" left join ");
                            } else {
                                sql_from.append(" left join ");
                            }
							String tempName = dbpre + fieldsetid;
							sql_from.append(getView(infor_flag, tlist, tempName,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on ");
							sql_from.append(dbpre);
							sql_from.append("A01.A0100=");
							sql_from.append(dbpre + fieldsetid + ".A0100");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								sql_from.append(" and " +dbpre + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+dbpre +strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						} else if ("2".equals(infor_flag)) // 单位库
						{
							if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                                continue;
                            }
							if ("3".equals(history) || "2".equals(history))
//								sql_from.append(" inner join ");
                            {
                                sql_from.append(" left join ");
                            } else {
                                sql_from.append(" left join ");
                            }
							sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on B01.B0110=");
							sql_from.append(fieldsetid);//rowSet.getString("fieldsetid")
							sql_from.append(".B0110");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								sql_from.append(" and " + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						} else if ("3".equals(infor_flag)) // 职位库
						{
							if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)|| fieldsetid.indexOf("A0") != -1) {
                                continue;
                            }
							if ("3".equals(history) || "2".equals(history))
//								sql_from.append(" inner join ");
                            {
                                sql_from.append(" left join ");
                            } else {
                                sql_from.append(" left join ");
                            }
							sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on " + "K01.E01A1=" + fieldsetid+ ".E01A1");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								sql_from.append(" and " + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						} else if ("5".equals(infor_flag)) // 基准岗位
						{
							if ("H01".equals(fieldsetid)) {
                                continue;
                            }
							sql_from.append(" left join ");
							sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on " + "H01.H0100=" + fieldsetid+ ".H0100");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								sql_from.append(" and " + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						}
						ct++;
						strid+=fieldsetid+",";
					}
				}

			if(field!=null){
                if(whereBuffer.toString().length()>0)
                {
                	sql_from.append(whereBuffer);
                }else{
                	sql_from.append(" where 1=1 ");
                }
				if ("1".equals(infor_flag)) // 人员库
				{
					sql_from.append(" and " + dbpre+ "A01.A0100 is not null ");
				} else if ("2".equals(infor_flag)) // 单位库
				{
					sql_from.append(" and  B01.B0110 is not null ");
				} else if ("3".equals(infor_flag)) // 职位库
				{
					sql_from.append(" and  K01.E01A1 is not null ");
				}
                isWHere=true;
			}
			if (!isWHere)
			{
			    	sql_from.append(" where 1=1 ");
			    	isWHere=true;
			}
			if (this.privConditionStr.trim().length() > 0) {
				if (sql_from.indexOf("where") == -1) {
                    sql_from.append(" where 1=1 ");
                }
				//取全部记录 关联兼职 此处不跳过//取查询结果暂不处理
				if(!"true".equalsIgnoreCase(this.showPartJob)||!(this.getDbpreSQL(dbpre)!=null&&((String)this.getDbpreSQL(dbpre)).trim().length()>0)){//关联兼职此处权限不处理 在下面处理28728
					
					if ("1".equals(infor_flag)) // 人员库
					{
						sql_from.append(" and " + dbpre + "A01.A0100 ");
					} else if ("2".equals(infor_flag)) // 单位库
					{
						sql_from.append(" and B01.B0110 ");
					} else if ("3".equals(infor_flag)) // 职位库
					{
						sql_from.append(" and K01.E01A1 ");
					}else{
						sql_from.append(" and " + dbpre + "A01.A0100 ");
					}
					sql_from.append(this.privConditionStr);
				}
			}
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			if("1".equals(infor_flag))
			{
				if(this.getDbpreSQL(dbpre)!=null&&((String)this.getDbpreSQL(dbpre)).trim().length()>0)
				{
					if("true".equalsIgnoreCase(this.showPartJob)){//liuy 2015-2-7 自动取数的时候，兼职不用加这个条件（相关bug6624） //28728 自动取数添加条件 会不生效 现取消此设置 changxy
					String str=this.getDbpreSQL(dbpre);
					if(str.indexOf("WHERE ")>-1) {
						str=str.substring(str.indexOf("WHERE ")+6);
						str=str.substring(0, str.lastIndexOf(")"));
					}
						sql_from.append(" and "+str+"");//cfactor 公式要 权限不要
					}else{
						sql_from.append(" and ("+((String)this.getDbpreSQL(dbpre))+")");
					}	
				}
			}
			else if("2".equals(infor_flag))
			{
				if(this.getB01SQL()!=null&&this.getB01SQL().trim().length()>0) {
                    sql_from.append(" and ("+this.getB01SQL()+")");
                }
				sql_from.append(" and B01.B0110 in (select codeitemid from organization where (codesetid='UN' or codesetid='UM') ");
				sql_from.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
					
			}
			else if("3".equals(infor_flag))
			{
				if(this.getK01SQL()!=null&&this.getK01SQL().trim().length()>0) {
                    sql_from.append(" and ("+this.getK01SQL()+")");
                }
				sql_from.append(" and K01.E01A1 in (select codeitemid from organization where (codesetid='@K') ");
				sql_from.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
			}
			curFlds = sql_select.toString();
			curFrom = " from " + sql_from.substring(1);
			sql_select.append(" from " + sql_from.substring(1));
	    	sql_select.append(orderSql);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sql.append("select RowNum,a.* from ("+sql_select.toString()+") a");
			}
			else
			{
	    	    sql.append(sql_select.toString());
			}
           
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return sql.toString();
	}
	
	/**
	 * 人员花名册机构指标取单位值还是取部门值
	 * @param rowSet
	 * @param dao
	 * @param tabid
	 * @param GridNo
	 * @author liuy
	 * @return
	 */
	private String getPersonGetDeptValue(RowSet rowSet, ContentDAO dao, String tabid, String GridNo){
		String personGetDeptValue = "";//人员花名册机构指标取单位值0(默认值)，取部门值1
		try {
			rowSet = dao.search("select SetName,ExtendAttr from Muster_Cell where Tabid="+tabid+" and GridNo="+GridNo);
			if(rowSet.next()){
				String star = "<PersonGetDeptValue>";
				String end = "</PersonGetDeptValue>";
				String setName = rowSet.getString("SetName");
				String xml = rowSet.getString("ExtendAttr");
				if(StringUtils.isEmpty(setName)) {
                    setName = "";
                }
				if(StringUtils.isEmpty(xml)) {
                    xml = "";
                }
				if(setName.startsWith("B")){					
					if(xml.indexOf(star)!=-1&&xml.indexOf(end)!=-1) {
                        personGetDeptValue = xml.substring(xml.indexOf(star)+star.length(),xml.indexOf(end));
                    }
					if(StringUtils.isEmpty(personGetDeptValue)) {
                        personGetDeptValue = "0";
                    }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personGetDeptValue;
	}
	
	/**
	 * 返回岗位体系(基准岗位)代码
	 * @return
	 */
	private String getStdPosCodeSetId(){
		String codesetid="";
		// 常量表  PS_C_CODE
		String strSQL="select str_Value from constant where constant='PS_C_CODE'";
		RowSet rs=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(strSQL);
			if(rs.next()) {
                codesetid=rs.getString(1);
            }
		}catch (Exception  ex){
			ex.printStackTrace();
		}
		finally
		{
			try{
				if (rs != null){
					rs.close();
				}
			}catch (SQLException sql){
				sql.printStackTrace();  
			}
		}
		return codesetid;
	}
	
	public String createCountSQL(String history, String userName, String tableName,
			String tabid, String infor_flag, String dbpre, String queryScope,
			String flag, String year, String month, String count,
			String fromScope, String toScope, String selectedPoint,
			String isGroupPoint, String groupPoint) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		boolean isYMB = false; // 是否用到硬编码 分组指标
		if("2".equals(history))
		{
			String [] temp=null;
			if (selectedPoint != null) {
			    temp = selectedPoint.split("/");
			}
			if(temp!=null&&temp.length>1){
				this.queryField = temp[3].trim(); // 得到选取的按部分查询选择的指标的指标集
			}else{
				if (fromScope != null && !"".equals(fromScope)) {
					String[] fomula = fromScope.split("::");
					if(fomula!=null&&fomula.length==3){
						this.queryField=toScope;
			        }
			       }
			}
		}
		if(!this.cellGridNoMap.isEmpty()) {
            this.cellGridNoMap.clear();//每次进入清空map内容
        }
		ArrayList list = existForwardSql_fields(tableName, tabid, infor_flag,isGroupPoint);

		sql.append((String) list.get(0));
		String fields = ((String) list.get(1)).trim();
        String re_name=((String)list.get(2)).trim();
		StringBuffer sql_suffix = new StringBuffer("");
		StringBuffer sql_from = new StringBuffer("");
		StringBuffer sql_whl = new StringBuffer("");

		String orderSql = ""; // 排序sql
		ArrayList tlist = getSetList(tabid); // 根据花名册id得到 可取某次历史纪录的子集列表
		String resultTable = userName + dbpre + "Result";
		if ("2".equals(infor_flag)) {
            resultTable = userName  + "BResult";
        } else if ("3".equals(infor_flag)) {
            resultTable = userName  + "KResult";
        }
		if(resultTable.indexOf("（")!=-1||resultTable.indexOf("）")!=-1) {
            resultTable = "\""+resultTable+"\"";
        }
		
		StringBuffer sql_select = new StringBuffer(" ");
		curInsertFlds = sql.toString();
		curFlds = "";
		curFrom = "";
		curGroupBy = "";
		curOrderBy = "";
		if ("1".equals(infor_flag)) // 人员库
		{
			String order = getSortStr(dbpre,"1",history);
			order=order!=null?order:"";
			if(order.trim().length()>8){
			    curGroupBy = getGroupStr(dbpre,"1");
			    curOrderBy = order;
				orderSql = getGroupStr(dbpre,"1")+ " " +order;
			}else{
			    curGroupBy = "group by " + dbpre + "A01.A0100";
			    curOrderBy = "order by A0000";
			    if(Sql_switcher.searchDbServer()==Constant.ORACEL){//orcale 汇总设置时 group by 有问题
			    	
			    		String fileditems="";
			    		String[] filditem=((String)list.get(1)).split(",");
			    		for (int i = 0; i < filditem.length; i++) {
			    			if(!"D".equalsIgnoreCase(filditem[i].split("/")[1])&&
			    			   !"a0100".equalsIgnoreCase(filditem[i].split("/")[0])&&
			    			   ("b0110".equalsIgnoreCase(filditem[i].split("/")[0])||
					    	    "e0122".equalsIgnoreCase(filditem[i].split("/")[0])||
					    		"e01a1".equalsIgnoreCase(filditem[i].split("/")[0])
			    			   )){//oracle 汇总时排除日期型指标，防止由于多条日期数据汇总不上
			    				fileditems+=","+dbpre+"A01."+filditem[i].split("/")[0]+" ";
			    			}
						}
			    		if(fileditems.length()>0) {
                            orderSql=" group by " + dbpre + "A01.A0100"+fileditems+" order by A0000";
                        } else {
                            orderSql=" group by " + dbpre + "A01.A0100 order by A0000";
                        }
			    
			    }else{
			    	
			    	orderSql = " group by " + dbpre + "A01.A0100 order by A0000";
			    }
			}
			sql_select.append("select ");
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1 as recidx,");
            }
            /*else
				sql_select.append(" RowNum,");*/
			sql_select.append("'"+dbpre+"' as NBASE,max(");
			sql_select.append(dbpre);
			sql_select.append("A01.A0000) as A0000,max(");
			sql_select.append(dbpre);
			sql_select.append("A01.A0100) as A0100,max(");
			sql_select.append(dbpre);
			sql_select.append("A01.B0110) as B0110,max(");
			sql_select.append(dbpre);
			sql_select.append("A01.E0122) as E0122,");
			sql_select.append("null as I9999");
            String temptablename=dbpre+"A01";
            String resulttable = "";
            if(this.userView!=null&&this.userView.getStatus()==4) {
                resulttable = "(select obj_id as a0100 from t_sys_result where UPPER(username)='"
                        + this.userView.getUserName().toUpperCase()
                        + "' and flag=0 and UPPER(nbase)='"
                        + dbpre.toUpperCase() + "') T";
            } else {
                resulttable = userName + dbpre + "Result";
            }
            if("true".equalsIgnoreCase(this.showPartJob)) {
                temptablename=this.getShowPartJobSQL(dbpre,queryScope,resulttable);
            }
			if ("1".equals(queryScope) && dbpre != null&& dbpre.trim().length() > 0) // 如果从查询结果里取数
			{
				if("true".equalsIgnoreCase(this.showPartJob)) {
                    sql_from.append("," + temptablename);
                } else{
					if(this.userView!=null&&this.userView.getStatus()==4) {
                        sql_from.append(","+resulttable+" left join "+temptablename+" on T.a0100="+dbpre+"a01.a0100 ");
                    } else {
                        sql_from.append("," +resulttable+ " T left join "+ temptablename + " on T.A0100=" + dbpre + "A01.A0100");
                    }
				}
			} else {
				dbpre = dbpre != null && dbpre.trim().length() > 0 ? dbpre: "Usr";
				sql_from.append("," + temptablename + "");
			}

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",max(");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(groupPoint);
					sql_select.append(") as GroupN,max(organization.codeitemdesc) as GroupV");

					sql_from.append(" left join organization on ");
					sql_from.append(dbpre);
					sql_from.append("A01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				} else {
					sql_select.append(",max(");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(groupPoint);
					sql_select.append(") as ");
					sql_select.append(" GroupN,max(codeitem.codeitemdesc) as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem ");
					sql_from.append(" on   codeitem.codeitemid=" + dbpre+ "A01." + groupPoint);
				}
			}
			if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
			{
				FieldItem item=DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",max(");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(") as GroupN2,max(org.codeitemdesc) as GroupV2");

					sql_from.append(" left join organization org on ");
					sql_from.append(dbpre);
					sql_from.append("A01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				} else {
					sql_select.append(",max(");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(") as ");
					sql_select.append(" GroupN2,max(CD.codeitemdesc) as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD ");
					sql_from.append(" on   CD.codeitemid=" + dbpre+ "A01." + this.groupPoint2);
				}
				
			}
		} else if ("2".equals(infor_flag)) // 单位库
		{
			dbpre = "";
			String order = getSortStr(dbpre,"2",history);
			order=order!=null?order:"";
			if(order.trim().length()>8) {
                orderSql = getGroupStr(dbpre,"1")+ " "+order;
            } else {
                orderSql = " group by B01.B0110 order by B0110";
            }
			
			sql_select.append("select ");
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1 ,");
            }
            /*else
				sql_select.append(" RowNum,");*/
			sql_select.append(" max(B01.B0110) as B0110,null");

			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView!=null&&this.userView.getStatus()==4)
				{
					sql_from.append(",(select obj_id as b0110 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					sql_from.append(" and flag=1 ) T");
					sql_from.append(" left join b01 on T.b0110=b01.b0110 ");
				}
				else
				{
			    	sql_from.append("," + resultTable + " left join B01 on "+ resultTable + ".B0110=B01.B0110");
				}
			} else {
                sql_from.append(",B01");
            }

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",max(B01.");
					sql_select.append(groupPoint);
					sql_select.append(") as GroupN,max(organization.codeitemdesc) as GroupV");
					sql_from.append(" left join organization on B01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				} else {
					sql_select.append(",max(B01." + groupPoint+ ") as GroupN,max(codeitem.codeitemdesc) as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem  ");
					sql_from.append(" on codeitem.codeitemid=B01." + groupPoint);
				}
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",max(B01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(") as GroupN2,max(org.codeitemdesc) as GroupV2");
					sql_from.append(" left join organization org on B01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				} else {
					sql_select.append(",max(B01." + this.groupPoint2+ ") as GroupN2,max(CD.codeitemdesc) as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD  ");
					sql_from.append(" on CD.codeitemid=B01." + this.groupPoint2);
				}
			}
		} else if ("3".equals(infor_flag)) // 职位库
		{
			dbpre = "";
			String order = getSortStr(dbpre,"3",history);
			order=order!=null?order:"";
			if(order.trim().length()>8) {
                orderSql = getGroupStr(dbpre,"1")+ " "+order;
            } else {
                orderSql = " group by K01.E01A1 order by E01A1";
            }

			sql_select.append("select ");
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1 ,");
            }
            /*else
				sql_select.append(" RowNum,");*/
			sql_select.append("max(K01.E01A1) as E01A1,null");
			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if(this.userView!=null&&this.userView.getStatus()==4)
				{
					sql_from.append(",(select obj_id as e01a1 from t_sys_result where UPPER(username)='"+this.userView.getUserName().toUpperCase()+"'");
					sql_from.append(" and flag=2 ) T");
					sql_from.append(" left join k01 on T.e01a1=k01.e01a1 ");
				}
				else
				{
		    		sql_from.append("," + resultTable + " left join K01 on "+ resultTable + ".E01A1=K01.E01A1");
				}
			} else {
                sql_from.append(",K01");
            }

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",max(K01.");
					sql_select.append(groupPoint);
					sql_select.append(") as GroupN,max(organization.codeitemdesc) as GroupV");
					sql_from.append(" left join organization on K01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				}
				else {
					sql_select.append(",max(K01.");
					sql_select.append(groupPoint);
					sql_select.append(") as GroupN,max(codeitem.codeitemdesc) as GroupV");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ groupPoint + "' )) codeitem ");
					sql_from.append("on  codeitem.codeitemid=K01." + groupPoint);

				}
			}
			if (this.isGroupPoint2 != null && this.isGroupPoint2.trim().length() > 0&& "1".equals(this.isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(this.groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(this.groupPoint2) || "E01A1".equals(this.groupPoint2)|| "E0122".equals(this.groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",max(K01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(") as GroupN2,max(org.codeitemdesc) as GroupV2");
					sql_from.append(" left join organization org on K01.");
					sql_from.append(this.groupPoint2);
					sql_from.append("=org.codeitemid");
				}
				else {
					sql_select.append(",max(K01.");
					sql_select.append(this.groupPoint2);
					sql_select.append(") as GroupN2,max(CD.codeitemdesc) as GroupV2");
					sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='"+ this.groupPoint2 + "' )) CD ");
					sql_from.append("on  CD.codeitemid=K01." + this.groupPoint2);

				}
			}

		}
        boolean isWHere=false;
        DbWizard dbw=new DbWizard(this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer subsql = new StringBuffer("select DISTINCT fieldsetid from fielditem");
		StringBuffer subsql_whl = new StringBuffer("");
		String[] field = null;
		if (fields != null && !"".equals(fields)) {
            field = fields.split(",");
        }
		if (field != null) {
			subsql.append(" where ");
            String [] _re=re_name.split(",");
			for (int i = 0; i < field.length; i++) {

				String[] temp0 = field[i].split("/");
				String[] temp1 = field[i].split("/");

				String temp = "";

				if ("2".equals(infor_flag) && temp0[0].indexOf("B01") != -1) {
					temp0[0] = "B01." + temp0[0];

				} else if ("1".equals(infor_flag)&& (temp0[0].indexOf("A01") != -1|| temp0[0].indexOf("B01") != -1 || temp0[0].indexOf("E01") != -1)) {
					temp0[0] = dbpre + "A01." + temp0[0];

				} else if ("3".equals(infor_flag)&& (temp0[0].indexOf("E01") != -1 || temp0[0].indexOf("K01") != -1)) {
					temp0[0] = "K01." + temp0[0];
				}

				temp = getCountField(temp0, dbpre);
				String gridNo=_re[i];

				if(temp.toUpperCase().indexOf(" AS")==-1|| "NBASE".equalsIgnoreCase(temp))//AS 会用自定义指标带有as字符 改为 空格+AS
                {
                    sql_select.append("," + temp.trim()+" AS C"+gridNo);
                } else if("D".equals(temp0[1])){//日期型指标 temp
					if (temp0[0].indexOf(".") == -1) {
                        temp=temp.replace( temp0[0]+"_time",  "C"+gridNo);
                    } else {
						temp=temp.replace( temp0[0].substring(temp0[0].indexOf(".") + 1)+"_time",  "C"+gridNo);
					}
					sql_select.append("," + temp.trim());
				}else {
					sql_select.append("," + temp.trim().replace(" as "+temp1[0], " as C"+gridNo)); //汇总时 插入单位部门岗位指标与临时表固定列有冲突  修改为插入指标 起别名防止冲突
				}
                if("D".equals(temp0[1])&& dbw.isExistField(tableName, "D"+gridNo, false)){
                	if("1".equals(this.getCountflag()))//部分历史记录汇总
                    {
                        sql_select.append(",max("+temp0[0]+") AS D"+gridNo);  // 保存日期型数据
                    } else {
                        sql_select.append(",max("+temp0[0]+") AS D"+gridNo);
                    }
                }
				subsql_whl.append(" or itemid='");
				subsql_whl.append(temp1[0].trim());
				subsql_whl.append("'");
			}
			subsql.append(subsql_whl.substring(3));
		}

		try {
			StringBuffer whereBuffer = new StringBuffer("");
			if (field != null) {
				String strid="";
				rowSet = dao.search(subsql.toString());
				int ct = 0;
				while (rowSet.next()) {
					String fieldsetid = rowSet.getString("fieldsetid");
					if ("1".equals(infor_flag)) // 人员库
					{
						if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                            continue;
                        }

						if ("2".equals(history)) {
                            sql_from.append(" left join ");
                        }
//							sql_from.append(" inner join ");
						else {
                            sql_from.append(" left join ");
                        }
						if(fieldsetid.toUpperCase().startsWith("K"))
						{
							if("K01".equalsIgnoreCase(fieldsetid))
							{
								sql_from.append(fieldsetid);
							}else{
								StringBuffer temp_sql = new StringBuffer("");
								temp_sql.append("(SELECT * FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" A WHERE ");
								temp_sql.append(" A.I9999 =(SELECT MAX(B.I9999) FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" B WHERE ");
								temp_sql.append(" A.E01A1=B.E01A1  )) ");
								temp_sql.append(fieldsetid);
								sql_from.append(temp_sql);
							}
							sql_from.append(" on "+dbpre+"A01.e01a1="+fieldsetid+".e01a1 ");
						}else if(fieldsetid.toUpperCase().startsWith("B"))
						{
							if("B01".equalsIgnoreCase(fieldsetid))
							{
								sql_from.append(fieldsetid);
							}else{
								StringBuffer temp_sql = new StringBuffer("");
								temp_sql.append("(SELECT * FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" A WHERE ");
								temp_sql.append(" A.I9999 =(SELECT MAX(B.I9999) FROM ");
								temp_sql.append(fieldsetid);
								temp_sql.append(" B WHERE ");
								temp_sql.append(" A.b0110=B.b0110  )) ");
								temp_sql.append(fieldsetid);
								sql_from.append(temp_sql);
							}
							sql_from.append(" on "+dbpre+"A01.b0110="+fieldsetid+".b0110 ");
						}
						else{
							String tempName = dbpre + fieldsetid;
							//关于人员汇总的问题，27938北控集团：人员高级花名册前后台取数不一致
							/**
							 * 查询人员子集 条件查询时拼接sql 改为 先查询子集 按条件筛选记录 再左连接查询数据
							 * FROM USRA01
								LEFT JOIN (SELECT *
									FROM USRA58
									WHERE YEAR(USRA58.A58Z0) * 10000 + MONTH(USRA58.A58Z0) * 100 + DAY(USRA58.A58Z0) >= 20150101
										AND (YEAR(USRA58.A58Z0) * 10000 + MONTH(USRA58.A58Z0) * 100 + DAY(USRA58.A58Z0) <= 20151231
											OR USRA58.A58Z0 IS NULL)
									) USRA58 ON USRA01.A0100 = USRA58.A0100
							WHERE 1 = 1
								AND USRA01.A0100 IS NOT NULL
								AND USRa01.a0100 IN (SELECT USRa01.a0100
									FROM USRA01
									WHERE USRA01.E0122 = '010101')
							之前是左连接查询 先左连接查询 再按照条件筛选 为空的数据为筛选掉
							FROM USRA01
								LEFT JOIN 
									 USRA58 ON USRA01.A0100 = USRA58.A0100
							WHERE YEAR(USRA58.A58Z0) * 10000 + MONTH(USRA58.A58Z0) * 100 + DAY(USRA58.A58Z0) >= 20150101
										AND (YEAR(USRA58.A58Z0) * 10000 + MONTH(USRA58.A58Z0) * 100 + DAY(USRA58.A58Z0) <= 20151231
											OR USRA58.A58Z0 IS NULL
								AND USRA01.A0100 IS NOT NULL
								AND USRa01.a0100 IN (SELECT USRa01.a0100
									FROM USRA01
									WHERE USRA01.E0122 = '010101')		
							 * */
							String fieldItemid=getView(infor_flag, tlist, tempName,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre);
							if(!(fieldItemid.indexOf("from")>-1||fieldItemid.indexOf("FROME")>-1||fieldItemid.indexOf("where")>-1||fieldItemid.indexOf("WHERE")>-1)) {//fieldItemid 有可能是一个查询sql 需做判断
								
								sql_from.append("( select * from "+fieldItemid);
								if(whereBuffer.toString().indexOf("where")>-1) {
                                    sql_from.append(whereBuffer);
                                } else {
                                    sql_from.append(" where 1=1"+whereBuffer);
                                }
								
								sql_from.append(")");
							}
							sql_from.append(fieldItemid);
							sql_from.append(" on ");
							sql_from.append(dbpre);
							sql_from.append("A01.A0100=");
							sql_from.append(dbpre + fieldsetid + ".A0100");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								FieldItem z0item = DataDictionary.getFieldItem(strid.substring(0,3)+"Z0");
								FieldItem z0item1 = DataDictionary.getFieldItem(fieldsetid+"Z0");
								if(z0item!=null&&z0item1!=null){
									sql_from.append(" and " + dbpre + fieldsetid);
									sql_from.append("."+fieldsetid+"Z0="+dbpre+strid.substring(0,3));
									sql_from.append("."+strid.substring(0,3)+"Z0");
								}
							}
						}
					} else if ("2".equals(infor_flag)) // 单位库
					{
						if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                            continue;
                        }
						if ("3".equals(history) || "2".equals(history))
//							sql_from.append(" inner join ");
                        {
                            sql_from.append(" left join ");
                        } else {
                            sql_from.append(" left join ");
                        }
						sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

						sql_from.append(" on B01.B0110=");
						sql_from.append(rowSet.getString("fieldsetid"));
						sql_from.append(".B0110");
						if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
							FieldItem z0item = DataDictionary.getFieldItem(strid.substring(0,3)+"Z0");
							FieldItem z0item1 = DataDictionary.getFieldItem(fieldsetid+"Z0");
							if(z0item!=null&&z0item1!=null){
								sql_from.append(" and " + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						}
					} else if ("3".equals(infor_flag)) // 职位库
					{
						if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)|| fieldsetid.indexOf("A01") != -1) {
                            continue;
                        }
						if ("3".equals(history) || "2".equals(history))
//							sql_from.append(" inner join ");
                        {
                            sql_from.append(" left join ");
                        } else {
                            sql_from.append(" left join ");
                        }
						sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

						sql_from.append(" on " + "K01.E01A1=" + fieldsetid+ ".E01A1");
						if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
							FieldItem z0item = DataDictionary.getFieldItem(strid.substring(0,3)+"Z0");
							FieldItem z0item1 = DataDictionary.getFieldItem(fieldsetid+"Z0");
							if(z0item!=null&&z0item1!=null){
								sql_from.append(" and " + fieldsetid);
								sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
								sql_from.append("."+strid.substring(0,3)+"Z0");
							}
						}
					}
					strid+=fieldsetid+",";
					ct++;
				}
				if("1".equals(infor_flag))
				{
					Set keySet = this.personViewName.keySet();
					for(Iterator it=keySet.iterator();it.hasNext();)
					{
						String key=(String)it.next();	
						String tempName = key;
						if("2".equals(history)&&this.queryField.toUpperCase().indexOf(key.toUpperCase())!=-1){
						    String viewsql = getView(infor_flag, tlist, tempName,key, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre);
							if(viewsql.length()>0)
							{
					    		sql_from.append(" left join ");
						    	sql_from.append(viewsql);
						    	sql_from.append(" on ");
							    sql_from.append(dbpre);
						    	sql_from.append("A01.A0100=");
							    sql_from.append(tempName+ ".A0100 ");
							}
						}
					}
				}
				String fieldsetStr = getFieldArr(dbpre,infor_flag);
				String[] field_Arr = fieldsetStr.split(",");
				for(int i=0;i<field_Arr.length;i++){
					String fieldsetid = field_Arr[i].toUpperCase();
					if(fieldsetid!=null&&fieldsetid.trim().length()>0){
						if(strid.indexOf(fieldsetid)!=-1) {
                            continue;
                        }
						
						if ("1".equals(infor_flag)) // 人员库
						{
							if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                                continue;
                            }

							if ("2".equals(history))
//								sql_from.append(" inner join ");
                            {
                                sql_from.append(" left join ");
                            } else {
                                sql_from.append(" left join ");
                            }
							String tempName = dbpre + fieldsetid;
							sql_from.append(getView(infor_flag, tlist, tempName,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on ");
							sql_from.append(dbpre);
							sql_from.append("A01.A0100=");
							sql_from.append(dbpre + fieldsetid + ".A0100");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								FieldItem z0item = DataDictionary.getFieldItem(strid.substring(0,3)+"Z0");
								FieldItem z0item1 = DataDictionary.getFieldItem(fieldsetid+"Z0");
								if(z0item!=null&&z0item1!=null){
									sql_from.append(" and "+dbpre + fieldsetid);
									sql_from.append("."+fieldsetid+"Z0="+dbpre+strid.substring(0,3));
									sql_from.append("."+strid.substring(0,3)+"Z0");
								}
							}
						} else if ("2".equals(infor_flag)) // 单位库
						{
							if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)) {
                                continue;
                            }
							if ("3".equals(history) || "2".equals(history))
//								sql_from.append(" inner join ");
                            {
                                sql_from.append(" left join ");
                            } else {
                                sql_from.append(" left join ");
                            }
							sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on B01.B0110=");
							sql_from.append(rowSet.getString("fieldsetid"));
							sql_from.append(".B0110");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								FieldItem z0item = DataDictionary.getFieldItem(strid.substring(0,3)+"Z0");
								FieldItem z0item1 = DataDictionary.getFieldItem(fieldsetid+"Z0");
								if(z0item!=null&&z0item1!=null){
									sql_from.append(" and " + fieldsetid);
									sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
									sql_from.append("."+strid.substring(0,3)+"Z0");
								}
							}
						} else if ("3".equals(infor_flag)) // 职位库
						{
							if ("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid)|| fieldsetid.indexOf("A0") != -1) {
                                continue;
                            }
							if ("3".equals(history) || "2".equals(history))
//								sql_from.append(" inner join ");
                            {
                                sql_from.append(" left join ");
                            } else {
                                sql_from.append(" left join ");
                            }
							sql_from.append(getView(infor_flag, tlist, fieldsetid,fieldsetid, history, selectedPoint, fromScope,toScope, year, month, count,whereBuffer,dbpre));

							sql_from.append(" on " + "K01.E01A1=" + fieldsetid+ ".E01A1");
							if(selectedPoint!=null&&selectedPoint.length()>2&& "Z0".equalsIgnoreCase(selectedPoint.substring(0,2))&&ct>0){
								FieldItem z0item = DataDictionary.getFieldItem(strid.substring(0,3)+"Z0");
								FieldItem z0item1 = DataDictionary.getFieldItem(fieldsetid+"Z0");
								if(z0item!=null&&z0item1!=null){
									sql_from.append(" and " + fieldsetid);
									sql_from.append("."+fieldsetid+"Z0="+strid.substring(0,3));
									sql_from.append("."+strid.substring(0,3)+"Z0");
								}
							}
						}
						ct++;
						strid+=fieldsetid+",";
					}
				}
                if(whereBuffer.toString().length()>0&&!"1".equals(infor_flag))
                {
                	sql_from.append(" "+whereBuffer);
                }else
                {
                	sql_from.append(" where 1=1 ");
                }
				if ("1".equals(infor_flag)) // 人员库
				{
					sql_from.append(" and " + dbpre+ "A01.A0100 is not null ");
				} else if ("2".equals(infor_flag)) // 单位库
				{
					sql_from.append(" and B01.B0110 is not null ");
				} else if ("3".equals(infor_flag)) // 职位库
				{
					sql_from.append(" and K01.E01A1 is not null ");
				}
				isWHere=true;

			}
			if (!isWHere)
			{
			    	sql_from.append(" where 1=1 ");
			    	isWHere=true;
			}
			if (this.privConditionStr.trim().length() > 0) {
				if ("1".equals(infor_flag)) // 人员库
				{
					sql_from.append(" and " + dbpre + "A01.A0100 ");
				} else if ("2".equals(infor_flag)) // 单位库
				{
					sql_from.append(" and B01.B0110 ");

				} else if ("3".equals(infor_flag)) // 职位库
				{

					sql_from.append(" and K01.E01A1 ");
				}else{
					sql_from.append(" and " + dbpre + "A01.A0100 ");
				}
				sql_from.append(this.privConditionStr);
			}
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			if("1".equals(infor_flag))
			{
				if(this.getDbpreSQL(dbpre)!=null&&((String)this.getDbpreSQL(dbpre)).trim().length()>0)
				{
					sql_from.append(" and ("+((String)this.getDbpreSQL(dbpre))+")");
				}
			}
			else if("2".equals(infor_flag))
			{
				if(this.getB01SQL()!=null&&this.getB01SQL().trim().length()>0) {
                    sql_from.append(" and ("+this.getB01SQL()+")");
                }
				sql_from.append(" and B01.B0110 in (select codeitemid from organization where (codesetid='UN' or codesetid='UM') ");	
				sql_from.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
			}
			else if("3".equals(infor_flag))
			{
				if(this.getK01SQL()!=null&&this.getK01SQL().trim().length()>0) {
                    sql_from.append(" and ("+this.getK01SQL()+")");
                }
				sql_from.append(" and K01.E01A1 in (select codeitemid from organization where (codesetid='@K') ");	
				sql_from.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
			}
			curFlds = sql_select.toString();
			curFrom = " from " + sql_from.substring(1)+" "+orderSql.substring(0, orderSql.indexOf(" order by"))+" ";//自动取数插入排序指标 且按人员汇总 去除排序sql 只留分组sql bug 47445 
			sql_select.append(" from " + sql_from.substring(1));
			sql_select.append(orderSql);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sql.append("select RowNum,a.* from ("+sql_select.toString()+") a");
			}
			else
			{
		    	
		    	sql.append(sql_select.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return sql.toString();
	}
	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * 
	 * @param tabid
	 *            高级花名册的id
	 * @param history
	 *            1:最后一条历史纪录 3：某次历史纪录 2：部分历史纪录
	 * @param infor_Flag
	 *            信息群标识
	 * @param dbpre
	 *            应用库表前缀
	 * @param queryScope
	 *            查询的范围
	 * @param flag
	 *            "0":无 "1"有子集指标无年月标识,可按最后一条历史纪录查 "2"有子集指标无年月标识,可按取部分历史纪录查
	 *            "3"有子集指标和年月标识，可按某次的历史纪录查
	 * @param year
	 *            年;month 月;count 次
	 * @param fromScope
	 * @param toScope
	 * @param selectedPoint
	 * @param isGroupPoint
	 *            是否选用分组指标 1:选用
	 * @param groupPoint;
	 *            已选的分组指标
	 * @return void
	 * @author dengc created: 2003/03/22
	 */

	public String createSQL(String history, String userName, String tableName,
			String tabid, String infor_flag, String dbpre, String queryScope,
			String flag, String year, String month, String count,
			String fromScope, String toScope, String selectedPoint,
			String isGroupPoint, String groupPoint) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		boolean isYMB = false; // 是否用到硬编码 分组指标
		if(!this.cellGridNoMap.isEmpty()) {
            this.cellGridNoMap.clear();
        }
		ArrayList list = existForwardSql_fields(tableName, tabid, infor_flag,
				isGroupPoint);

		sql.append((String) list.get(0));
		String fields = ((String) list.get(1)).trim();
        String rename=((String)list.get(2)).trim();
		StringBuffer sql_suffix = new StringBuffer("");
		StringBuffer sql_from = new StringBuffer("");
		StringBuffer sql_whl = new StringBuffer("");

		String orderSql = ""; // 排序sql
		String searchResultSql = ""; // 数据范围的条件SQL
		ArrayList tlist = getSetList(tabid); // 根据花名册id得到 可取某次历史纪录的子集列表

		StringBuffer sql_select = new StringBuffer(" ");
		if ("1".equals(infor_flag)) // 人员库
		{
			searchResultSql = " and " + dbpre + "A01.A0100=" + userName + dbpre
					+ "Result.A0100";
			orderSql = " order by " + dbpre + "A01.A0100";

			sql_select.append("select 1, ");
			sql_select.append(dbpre);
			sql_select.append("A01.A0000,");
			sql_select.append(dbpre);
			sql_select.append("A01.A0100,");
			sql_select.append(dbpre);
			sql_select.append("A01.B0110,");
			sql_select.append(dbpre);
			sql_select.append("A01.E0122,");
			sql_select.append("null");
			sql_from.append(",");
			sql_from.append(dbpre + "A01");

			if (isGroupPoint != null && "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(groupPoint);
					sql_select.append(" as groupN,organization.codeitemdesc as groupV");

					sql_from.append(",");
					sql_from.append("organization");
					sql_whl.append(" and ");
					sql_whl.append(dbpre);
					sql_whl.append("A01.");
					sql_whl.append(groupPoint);
					sql_whl.append("=organization.codeitemid");
				} else {
					sql_select.append("," + dbpre + "A01." + groupPoint
							+ " as groupN,codeitem.codeitemdesc as groupV");
					sql_from.append(",");
					sql_from.append("codeitem");
					sql_whl.append(" and  codeitem.codesetid=(select codesetid from fielditem where itemid='"
									+ groupPoint+ "' ) and codeitem.codeitemid="+ dbpre+ "A01." + groupPoint);
				}
			}
			if (isGroupPoint2 != null && "1".equals(isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint2) || "E01A1".equals(groupPoint2)|| "E0122".equals(groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(groupPoint2);
					sql_select.append(" as groupN2,org.codeitemdesc as groupV2");

					sql_from.append(",");
					sql_from.append("organization org");
					sql_whl.append(" and ");
					sql_whl.append(dbpre);
					sql_whl.append("A01.");
					sql_whl.append(groupPoint2);
					sql_whl.append("=org.codeitemid");
				} else {
					sql_select.append("," + dbpre + "A01." + groupPoint2+ " as groupN2,CD.codeitemdesc as groupV2");
					sql_from.append(",");
					sql_from.append("codeitem CD");
					sql_whl.append(" and  CD.codesetid=(select codesetid from fielditem where itemid='"
									+ groupPoint2+ "' ) and CD.codeitemid="+ dbpre+ "A01." + groupPoint2);
				}
			}
		} else if ("2".equals(infor_flag)) // 单位库
		{
			searchResultSql = " and B01.B0110=" + userName + "BResult.B0110";
			orderSql = " order by B01.B0110";

			sql_select.append("select 1,B01.B0110,null");
			sql_from.append(",");
			sql_from.append("B01");

			if (isGroupPoint != null && "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",B01.");
					sql_select.append(groupPoint);
					sql_select.append(" as groupN,organization.codeitemdesc as groupV");
					sql_from.append(",");
					sql_from.append("organization");

					sql_whl.append(" and B01.");
					sql_whl.append(groupPoint);
					sql_whl.append("=organization.codeitemid");
				} else {
					sql_select.append(",B01." + groupPoint+ " as groupN,codeitem.codeitemdesc as groupV");
					sql_from.append(",");
					sql_from.append("codeitem");
					sql_whl.append(" and  codeitem.codesetid=(select codesetid from fielditem where itemid='"+ groupPoint+ "' ) and codeitem.codeitemid=B01."+ groupPoint);
				}
			}
			if (isGroupPoint2 != null && "1".equals(isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint2) || "E01A1".equals(groupPoint2)|| "E0122".equals(groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",B01.");
					sql_select.append(groupPoint2);
					sql_select.append(" as groupN2,org.codeitemdesc as groupV2");
					sql_from.append(",");
					sql_from.append("organization org");

					sql_whl.append(" and B01.");
					sql_whl.append(groupPoint2);
					sql_whl.append("=org.codeitemid");
				} else {
					sql_select.append(",B01." + groupPoint2+ " as groupN2,CD.codeitemdesc as groupV2");
					sql_from.append(",");
					sql_from.append("codeitem CD");
					sql_whl.append(" and  CD.codesetid=(select codesetid from fielditem where itemid='"+ groupPoint2+ "' ) and CD.codeitemid=B01."+ groupPoint2);
				}
			}
		} else if ("3".equals(infor_flag)) // 职位库
		{
			searchResultSql = " and K01.E01A1=" + userName + "KResult.E01A1";
			orderSql = " order by K01.E01A1";

			sql_select.append("select 1, K01.E01A1,null");
			sql_from.append(",");
			sql_from.append("K01");
			if (isGroupPoint != null && "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					sql_select.append(",K01.");
					sql_select.append(groupPoint);
					sql_select.append(" as groupN,organization.codeitemdesc as groupV");
					sql_from.append(",");
					sql_from.append("organization");
					sql_whl.append(" and K01.");
					sql_whl.append(groupPoint);
					sql_whl.append("=organization.codeitemid");

				} else {
					sql_select.append(",K01.");
					sql_select.append(groupPoint);
					sql_select.append(" as groupN,codeitem.codeitemdesc as groupV");

					sql_from.append(",");
					sql_from.append("codeitem");
					sql_whl.append(" and  codeitem.codesetid=(select codesetid from fielditem where itemid='"
									+ groupPoint+ "' ) and codeitem.codeitemid=K01."+ groupPoint);

				}
			}
			if (isGroupPoint2 != null && "1".equals(isGroupPoint2)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint2) || "E01A1".equals(groupPoint2)|| "E0122".equals(groupPoint2)) // 采用的硬编码指标
				{
					sql_select.append(",K01.");
					sql_select.append(groupPoint2);
					sql_select.append(" as groupN2,org.codeitemdesc as groupV2");
					sql_from.append(",");
					sql_from.append("organization org");
					sql_whl.append(" and K01.");
					sql_whl.append(groupPoint2);
					sql_whl.append("=org.codeitemid");

				} else {
					sql_select.append(",K01.");
					sql_select.append(groupPoint2);
					sql_select.append(" as groupN2,CD.codeitemdesc as groupV2");

					sql_from.append(",");
					sql_from.append("codeitem CD");
					sql_whl.append(" and  CD.codesetid=(select codesetid from fielditem where itemid='"
									+ groupPoint2+ "' ) and CD.codeitemid=K01."+ groupPoint2);

				}
			}

		}

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer subsql = new StringBuffer(
				"select DISTINCT fieldsetid from fielditem");
		StringBuffer subsql_whl = new StringBuffer("");
		String[] field = null;
		if (fields != null && !"".equals(fields)) {
            field = fields.split(",");
        }
		if (field != null) {
			String [] _re=rename.split(",");
			subsql.append(" where ");

			for (int i = 0; i < field.length; i++) {
				String[] temp0 = field[i].split("/");
				String[] temp1 = field[i].split("/");
				String temp = "";

				if ("2".equals(infor_flag) && temp0[0].indexOf("B01") != -1) {
					temp0[0] = "B01." + temp0[0];
				} else if ("1".equals(infor_flag)&& (temp0[0].indexOf("A01") != -1|| temp0[0].indexOf("B01") != -1 || temp0[0].indexOf("E01") != -1)) {
                    temp0[0] = dbpre + "A01." + temp0[0];
                } else if ("3".equals(infor_flag)&& (temp0[0].indexOf("E01") != -1 || temp0[0].indexOf("K01") != -1)) {
                    temp0[0] = "K01." + temp0[0];
                }
				temp = getField(temp0, dbpre);
				String gridNo=_re[i];
				if(temp.toUpperCase().indexOf(" AS")==-1|| "NBASE".equalsIgnoreCase(temp)) {
                    sql_select.append("," + temp.trim()+" AS C"+gridNo);
                } else {
                    sql_select.append("," + temp.trim());
                }
				subsql_whl.append(" or itemid='");
				subsql_whl.append(temp1[0].trim());
				subsql_whl.append("'");
			}
			subsql.append(subsql_whl.substring(3));
		}

		try {
			StringBuffer whereBuffer = new StringBuffer("");
			if (field != null) {
				rowSet = dao.search(subsql.toString());
				while (rowSet.next()) {
					if ("1".equals(infor_flag)) // 人员库
					{
						if ("A01".equals(rowSet.getString("fieldsetid"))) {
                            continue;
                        }
						sql_from.append(",");
						String tempName = dbpre+ rowSet.getString("fieldsetid");
						sql_from.append(getView(infor_flag, tlist, tempName,rowSet.getString("fieldsetid"), history,selectedPoint, fromScope, toScope, year, month,count,whereBuffer,dbpre));
						sql_whl.append(" and ");
						sql_whl.append(dbpre);
						sql_whl.append("A01.A0100=");
						sql_whl.append(dbpre + rowSet.getString("fieldsetid")+ ".A0100");
					} else if ("2".equals(infor_flag)) // 单位库
					{
						if ("B01".equals(rowSet.getString("fieldsetid"))) {
                            continue;
                        }
						sql_from.append(",");
						sql_from.append(getView(infor_flag, tlist, rowSet.getString("fieldsetid"), rowSet.getString("fieldsetid"), history,selectedPoint, fromScope, toScope, year, month,count,whereBuffer,dbpre));
						sql_whl.append(" and B01.B0110=");
						sql_whl.append(rowSet.getString("fieldsetid"));
						sql_whl.append(".B0110");
					} else if ("3".equals(infor_flag)) // 职位库
					{
						if ("K01".equals(rowSet.getString("fieldsetid"))|| rowSet.getString("fieldsetid").indexOf("A0") != -1) {
                            continue;
                        }
						sql_from.append(",");
						sql_from.append(getView(infor_flag, tlist, rowSet.getString("fieldsetid"), rowSet.getString("fieldsetid"), history,selectedPoint, fromScope, toScope, year, month,count,whereBuffer,dbpre));
						sql_whl.append(" and " + "K01.E01A1="+ rowSet.getString("fieldsetid") + ".E01A1");
					}
				}
			}

			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				if ("1".equals(infor_flag)) // 人员库
				{
					sql_from.append("," + userName + dbpre + "Result");
				} else if ("2".equals(infor_flag)) // 单位库
				{
					sql_from.append("," + userName + "BResult");
				} else if ("3".equals(infor_flag)) // 职位库
				{
					sql_from.append("," + userName + "KResult");
				}
			}

			sql_select.append(" from " + sql_from.substring(1));
            if(whereBuffer.toString().length()>0)
            {
            	sql_select.append(whereBuffer);
            }else{
            	sql.append(" where 1=1 ");
            }
			if (sql_whl.length() > 2) {
				sql_select.append(" and  ");
				sql_select.append(sql_whl.substring(4));
			}

			if ("1".equals(queryScope)) // 如果从查询结果里取数
			{
				/*if (sql_whl.length() < 2) {
					sql_select.append(" where ");
					sql_select.append(searchResultSql.substring(4));
				} else {*/
					sql_select.append(searchResultSql);
//				}
			}
			sql_select.append(orderSql);
			sql.append(sql_select.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return sql.toString();
	}

	public String isNotNull(String fieldName) {
		String str = fieldName + " is not null ";
		if (Sql_switcher.searchDbServer() != Constant.ORACEL) {
            str += " and " + fieldName + "<>''";
        }
		return str;
	}
	public String isNotNullNumber(String fieldName) {
		String str = fieldName + " is not null ";
		return str;
	}

	/**
	 * 生成可直接插入临时表数据的sql语句的前缀和所需要的字段代码 （ example: insert into tablename
	 * (##,##,##) fields1,fields2,fields3 ）
	 * 
	 * @param tableName
	 *            表名
	 * @param tabid
	 *            高级花名册id
	 * @param infor_flag
	 *            信息群标识
	 * @param isGroupPoint
	 *            是否选用分组指标 1:选用
	 * @return ArrayList [0]:sql语句的前缀 [1]：所需要的字段代码
	 * @author dengc created:2003/03/22
	 * 
	 */
	private ArrayList existForwardSql_fields(String tableName, String tabid,
			String infor_flag, String isGroupPoint) throws GeneralException {
		ArrayList list = new ArrayList();
		boolean isSuccess = false;
		StringBuffer sql_forward = new StringBuffer("insert into ");
		StringBuffer fields = new StringBuffer(""); // 可以直接从库中插入数据的字段名称列表
		StringBuffer re_name=new StringBuffer("");
		// (以,号分开)；
		RowSet rowSet = null;
		DbWizard dbw=new DbWizard(this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		sql_forward.append(tableName);
		sql_forward.append(" ( ");

		StringBuffer sql = new StringBuffer("");
		try {

			if ("1".equals(infor_flag)) {
				sql.append(",recidx,NBASE,A0000,A0100,B0110,E0122");
			} else if ("2".equals(infor_flag)) {
				sql.append(",recidx,B0110");
			} else if ("3".equals(infor_flag)) {
				sql.append(",recidx,E01A1");
			} else if ("5".equals(infor_flag)) {// 基准岗位
				sql.append(",recidx,H0100");
			}else{
				sql.append(",recidx,NBASE,A0000,A0100,B0110,E0122");
			}

			sql.append(",I9999");

			if (isGroupPoint != null && "1".equals(isGroupPoint)) {
				sql.append(",GroupN,GroupV");

			}
			if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
			{
				sql.append(",GroupN2,GroupV2");
			}

			StringBuffer fieldsSql = new StringBuffer("");
			fieldsSql.append("select GridNo,SetName,Field_Name,Field_Type,Flag,Slope,codeId,setname  from muster_cell where ( "
							+ isNotNull("SetName")+ ") and ( "+ isNotNull("Field_Name")+ " ) and flag!='C'  and  tabid=");
			fieldsSql.append(tabid);
			rowSet = dao.search(fieldsSql.toString());
			while (rowSet.next()) {
				if (!"1".equals(infor_flag)&& "A".equals(rowSet.getString("Flag"))) {
                    continue;
                }
				String gridNo = rowSet.getString("GridNo");
				String field_name = rowSet.getString("Field_Name");
				String setname = rowSet.getString("setname");
				/* 判断该指标是否已被删除或还没构库 */
				if(!"H0100".equals(field_name)){
					FieldItem item = DataDictionary.getFieldItem(field_name);
					if (item == null) {
                        continue;
                    }
					if (!"NBASE".equalsIgnoreCase(item.getItemid())) {
						try {
							item = DataDictionary.getFieldItem(field_name, setname.toLowerCase());
						} catch (Exception e) {
							throw GeneralExceptionHandler.Handle(new
									 Exception("高级花名册指标已被删除或未构库"));//针对花名册指标未构库导致报空指针添加提示信息							
						}
						if (item == null) {
                            continue;
                        }
					}
					if ("0".equals(item.getUseflag())) {
                        continue;
                    }
				}
				String fieldType = rowSet.getString("Field_Type");
				String slop = rowSet.getString("Slope");
				String codeId = rowSet.getString("codeId");
				if(setname!=null&&setname.toUpperCase().startsWith(this.personView))//人员视图
				{
					if(this.personViewName.get(setname.toUpperCase())!=null)
					{
						ArrayList alist = (ArrayList)this.personViewName.get(setname.toUpperCase());
						boolean flag=false;//personViewName查询全部人员库时 防止grid重复
						for (int i = 0; i < alist.size(); i++) {
							LazyDynaBean beas=(LazyDynaBean)alist.get(i);
							if(gridNo.equals(beas.get("gridno"))) {
								flag=true;
								break;
							}
								
						}
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("gridno",gridNo);
						bean.set("str",field_name + "/" + fieldType + "/" + slop + "/"+ codeId+"/"+setname);
						bean.set("fieldname",field_name);
						if(!flag) {
							alist.add(bean);
							this.personViewName.put(setname.toUpperCase(), alist);
						}
					}else{
						ArrayList alist = new ArrayList();
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("gridno",gridNo);
						bean.set("str",field_name + "/" + fieldType + "/" + slop + "/"+ codeId+"/"+setname);
						bean.set("fieldname",field_name);
						alist.add(bean);
						this.personViewName.put(setname.toUpperCase(), alist);
					}
				}
			    sql.append(",C");
			    sql.append(gridNo);
			    this.cellGridNoMap.put(field_name, "C"+gridNo);
			    if("D".equals(fieldType)&& dbw.isExistField(tableName, "D"+gridNo, false)) {
	                sql.append(",D"+gridNo);  // 日期型数据
	                this.cellGridNoMap.put(field_name+"_D", "D"+gridNo); //日期型存储两个 以_D 区分 
			    }
				fields.append(",");
				if(setname!=null&&setname.toUpperCase().startsWith(this.personView)) {
                    fields.append(field_name + "/" + fieldType + "/" + slop + "/"+ codeId+"/"+setname);
                } else {
                    fields.append(field_name + "/" + fieldType + "/" + slop + "/"+ codeId);
                }
				re_name.append(","+gridNo);
			}
			sql_forward.append(sql.substring(1));
			curMusterFlds = sql.substring(1);
			sql_forward.append(" ) ");

			list.add(sql_forward.toString());
			if (fields.length() > 3)
			{
				list.add(fields.substring(1));
				list.add(re_name.toString().substring(1));
			}
			else
			{
				list.add(fields.toString());
				list.add(re_name.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 根据用户名及花名册id创建临时高级花名册表
	 * 
	 * @param username
	 *            用户名称
	 * @param tabid
	 *            高级花名册id
	 * @author dengc created:2006/03/21
	 */

	public Table createMusterTempTable(String tableName, String tabid,
			DbWizard dbWizard,String groupPoint,String groupPoint2,MusterBo mbo) throws GeneralException {
		Table table = new Table(tableName);
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select FlagA,nModule,nPrint from muster_name where tabid="
				+ tabid;
		String flag = "A"; // A人员 B单位 K职位
		int nModule = 0; // 模块标志
		RowSet recset = null;
		try {
			UsrResultTable resulttable = new UsrResultTable();
			if (userView != null&& resulttable.isNumber(this.userView.getUserName())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.hroster")+ "!"));
			}
			recset = dao.search(sql);
			if (recset.next()) {
				flag = recset.getString("FlagA");
				nModule = recset.getInt("nModule");
			}

			ArrayList fieldlist = getMusterFields(tabid, flag, nModule,groupPoint,groupPoint2,mbo);
			table.setCreatekey(false);
			for (int i = 0; i < fieldlist.size(); i++) {
                table.addField((Field) fieldlist.get(i));
            }
			if(hasSimpleFormula(tabid)) {
	            ArrayList tempFieldlist = getMusterTempFields(tabid, flag, nModule,mbo);
	            for (int i = 0; i < tempFieldlist.size(); i++) {
                    table.addField((Field) tempFieldlist.get(i));
                }
			}

			if (dbWizard.isExistTable(table.getName(), false)) {
                dbWizard.dropTable(table);
            }
			dbWizard.createTable(table);
            if(this.indexColumn!=null)
            {
            	String[] arr=this.indexColumn.split(",");
            	for(int i=0;i<arr.length;i++)
            	{
            		if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
            		dao.update("create index "+table.getName()+i+" on "+table.getName()+" ("+arr[i]+")");
            	}
            	/*if(this.indexColumn.equalsIgnoreCase("recidx,A0100,Nbase"))
            	{
            		dao.update("create index "+table.getName()+"_INDEX on "+table.getName()+" (A0100,NBASE)");
            	}*/
            }
		} catch (Exception ex) {

			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return table;
	}

	/**
	 * 根据用户名及花名册id创建临时高级花名册表
	 * 
	 * @param username
	 *            用户名称
	 * @param tabid
	 *            高级花名册id
	 * @author dengc created:2006/03/21
	 */

	public Table createMusterTempTable(String tabid, DbWizard dbWizard,
			int nModule,String groupPoint,String groupPoint2,MusterBo mbo) throws GeneralException {
		String musterName = userView.getUserName().trim().replaceAll(" ", "")+"_Muster_" + tabid; // 临时表名
		
		if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
            musterName = this.getTemptable();
        }
		Table table = new Table(musterName);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset = null;
		try {
			ArrayList fieldlist = null;
			if (nModule == 15) {
                fieldlist = getSpMusterFields(tabid, nModule,groupPoint,groupPoint2,mbo);
            } else {
                fieldlist = getMusterFields(tabid, nModule,groupPoint,groupPoint2,mbo);
            }
			table.setCreatekey(false);
			ArrayList fields = new ArrayList();
			for (int i = 0; i < fieldlist.size(); i++) {
				table.addField((Field) fieldlist.get(i));
				fields.add(((Field) fieldlist.get(i)).getName().toLowerCase()+ "/" + ((Field) fieldlist.get(i)).getDatatype());
			}
			if (dbWizard.isExistTable(table.getName(), false)) {
				rowset = dao.search("select * from "+musterName+ " where 1=2");
				ResultSetMetaData meta = rowset.getMetaData();
				boolean isEdited = false;
				HashMap tableColumn = new HashMap();
				for (int i = 0; i < meta.getColumnCount(); i++) {

					String tempName = meta.getColumnName(i + 1).toLowerCase();
					tableColumn.put(tempName, "1/" + meta.getColumnType(i + 1));

				}
				for (int i = 0; i < fields.size(); i++) {
					String temp = (String) fields.get(i);
					String[] temps = temp.split("/");
					if (tableColumn.get(temps[0]) == null) {
						isEdited = true;
						break;
					} else {
						String[] dd = ((String) tableColumn.get(temps[0])).split("/");
						if ("12".equals(dd[1])&& !temps[1].equals(String.valueOf(DataType.STRING))) {
							isEdited = true;
							break;
						} else if (("6".equals(dd[1]) || "8".equals(dd[1]) || "4".equals(dd[1]))&& !temps[1].equals(String.valueOf(DataType.FLOAT))) {
							isEdited = true;
							break;
						}

					}
				}
				// if(isEdited)
				// {
				dbWizard.dropTable(table);
				dbWizard.createTable(table);
				// }
			} else {
                dbWizard.createTable(table);
            }
			// dbWizard.addPrimaryKey(table);
			  if(this.indexColumn!=null)
	            {
	            	String[] arr=this.indexColumn.split(",");
	            	for(int i=0;i<arr.length;i++)
	            	{
	            		if(arr[i]==null|| "".equals(arr[i])) {
                            continue;
                        }
	            		dao.update("create index "+table.getName()+"_"+arr[i]+" on "+table.getName()+" ("+arr[i]+")");
	            	}
	            	/*if(this.indexColumn.equalsIgnoreCase("recidx,A0100,Nbase"))
	            	{
	            		dao.update("create index "+table.getName()+"_INDEX on "+table.getName()+" (A0100,NBASE)");
	            	}*/
	            }
		} catch (Exception ex) {

			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rowset!=null) {
                    rowset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return table;
	}

	/**
	 * 计算公式
	 * 
	 * @param tabid
	 * @param dbpre
	 * @param userView
	 * @param a0100
	 * @throws GeneralException
	 */
	public void runCountFormula2(String tabid, ArrayList allUsedFields,
			String dbpre, String tableName) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String musterName = userView.getUserName()+"_Muster_" + tabid; // 临时表名
		if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
            musterName = this.getTemptable();
        }
		try {
			rowSet = dao.search("select * from muster_cell where tabid="+ tabid + " and flag='C'");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				String codeSetID = rowSet.getString("CodeId");
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员

				// 解析公式
				YksjParser yp = new YksjParser(userView, allUsedFields,YksjParser.forSearch, varType, infoGroup, "Ht", dbpre);
				ArrayList fieldList = yp.getFormulaFieldList1(queryCond);
				if (fieldList.size() != 0) {
					analyseOptTable2(tabid, fieldList, tableName);
				}
				// 解析公式
				yp = new YksjParser(this.userView, fieldList, YksjParser.forNormal, varType, YksjParser.forPerson, "", "");
				yp.run(queryCond);
				String FSQL = yp.getSQL();
				dao.update("update "+musterName + " set C" + gridNo + "="+ FSQL);
				if(codeSetID!=null&&codeSetID.trim().length()>0&&!"0".equals(codeSetID)) {
                    changeCode(dao,tableName,"C" + gridNo,codeSetID,"");
                }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 动态创建表结构
	 * 
	 * @param tabid
	 * @param fieldList
	 * @param dbpre
	 * @throws GeneralException
	 */
	public void analyseOptTable2(String tabid, ArrayList fieldList,
			String tableName) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String musterName = userView.getUserName()+"_Muster_" + tabid; // 临时表名
		if(this.getTemptable()!=null&&this.getTemptable().trim().length()>0) {
            musterName = this.getTemptable();
        }
		try {
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(musterName);

			HashMap existColumnMap = new HashMap();
			rowSet = dao.search("select * from "+musterName+ " where 1=2");
			ResultSetMetaData metaData = rowSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(), "1");
			}

			int num = 0;
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				FieldItem fieldItem1 = (FieldItem) t.next();
				if (existColumnMap.get(fieldItem1.getItemid().toLowerCase()) != null) {
                    continue;
                }
				Field a_field = fieldItem1.cloneField();
				table.addField(a_field);
				num++;
			}
			if (num != 0) {
                dbWizard.addColumns(table);
            }

			StringBuffer whl = new StringBuffer(musterName + ".a0100= "+ tableName + ".a0100");
			whl.append(" and " + musterName + ".nbase=" + tableName + ".nbase");
			whl.append(" and " + musterName + ".a00z0=" + tableName + ".a00z0");
			whl.append(" and " + musterName + ".a00z1=" + tableName + ".a00z1");
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				StringBuffer sql = new StringBuffer("update " + musterName+ " set ");
				FieldItem fieldItem1 = (FieldItem) t.next();
				sql.append(" " + musterName + "." + fieldItem1.getItemid()+ "=(select " + tableName + "."+ fieldItem1.getItemid());
				sql.append(" from " + tableName + " where " + whl.toString()+ ")");
				sql.append(" where exists (select null from " + tableName+ " where " + whl.toString() + ")");
				dao.update(sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 取得花名册结构指标项列表
	 * 
	 * @param tabid
	 *            flag:信息集
	 * @param nModule
	 *            模块标志
	 * @return
	 */
	public ArrayList getSpMusterFields(String tabid, int nModule,String groupPoint,String groupPoint2,MusterBo mbo)
			throws GeneralException {

		this.fieldsList = getMusterFixupFields(tabid, "A", nModule,groupPoint,groupPoint2,mbo);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select GridNo,SetName,Field_Name,Field_Type,Flag,Field_Hz from muster_cell where flag!='G' and flag!='R' and flag!='H' and flag!='S' and flag!='P'  and tabid=");
		strsql.append(tabid);
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());
			HashMap setMap = new HashMap();
			setMap.put("A01", "1");
			while (rset.next()) {
				String fieldname = rset.getString("Field_Name");
				String aSetName = rset.getString("SetName");
				if (aSetName != null && aSetName.trim().length() > 0&& !"A00".equalsIgnoreCase(aSetName)) {
                    setMap.put(aSetName.toUpperCase(), "1");
                }
				String type = rset.getString("Field_Type");
				String gridNo = rset.getString("GridNo");
				String flags = rset.getString("Flag");
				String Field_Hz = rset.getString("Field_Hz");
				int length = 15;
				int dewidth = 4;
				if ("A00Z1".equalsIgnoreCase(fieldname)) {
					dewidth = 0;
				}
				if (fieldname != null && !"".equals(fieldname)) {
					FieldItem item = DataDictionary.getFieldItem(fieldname);
					if (item != null) {
						length = item.getItemlength();
						dewidth = item.getDecimalwidth();
						if(item.isCode())
						{
							if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                                length=mbo.getOrgdesc_length();
                            } else {
                                length=mbo.getCodedesc_length();
                            }
						}
					}
					/* 判断该指标是否已被删除或还没构库 */

					// if (item == null||item.getUseflag().equals("0"))
					// {
					// throw GeneralExceptionHandler.Handle(new
					// Exception(musterName+" 花名册中 "+fieldname+" 指标已被删除或还没构库"));
					// }
					//					
					// Field obj = new Field("C" + gridNo, item.getItemdesc());
					Field obj = new Field("C" + gridNo, Field_Hz);
					if ("A".equals(type)) {
						obj.setDatatype(DataType.STRING);
						obj.setVisible(false);
						if(item!=null&& "0".equals(item.getCodesetid())) {
                            obj.setLength(item.getItemlength());
                        } else {
                            obj.setLength(length);
                        }
						obj.setAlign("left");

					} else if ("M".equals(type)) {
						obj.setDatatype(DataType.CLOB);
						obj.setVisible(false);
						obj.setAlign("left");
					}

					else if ("D".equals(type)) {
						obj.setDatatype(DataType.STRING);
						obj.setLength(20);
						obj.setVisible(false);
						obj.setFormat("yyyy.MM.dd");
						obj.setAlign("right");
					} else if ("N".equals(type)) {
						obj.setDatatype(DataType.FLOAT);
						obj.setDecimalDigits(dewidth);
						obj.setLength(length);
						obj.setVisible(false);
						obj.setAlign("left");

					}
					fieldsList.add(obj);
				} else {

					if ("C".equals(flags)) {
						Field temp3 = new Field("C" + gridNo, ResourceFactory.getProperty("hmuster.label.expressions"));

						if ("N".equals(type)) {
							temp3.setDatatype(DataType.FLOAT);
							temp3.setDecimalDigits(dewidth);
							temp3.setLength(length);
							temp3.setVisible(false);
						} else if ("A".equals(type) || "M".equals(type)) {
							temp3.setDatatype(DataType.STRING);
							temp3.setVisible(false);
							temp3.setLength(255);
							temp3.setAlign("left");
						} else if ("D".equals(type)) {
							temp3.setDatatype(DataType.STRING);
							temp3.setLength(20);
							temp3.setVisible(false);
							temp3.setFormat("yyyy.MM.dd");
							temp3.setAlign("right");
						}
						fieldsList.add(temp3);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldsList;
	}

	/**
	 * 取得花名册结构指标项列表
	 * 
	 * @param tabid
	 *            flag:信息集
	 * @param nModule
	 *            模块标志
	 * @return
	 */
	public ArrayList getMusterFields(String tabid, int nModule,String groupPoint,String groupPoint2,MusterBo mbo)
			throws GeneralException {

		this.fieldsList = getMusterFixupFields(tabid, "A", nModule,groupPoint,groupPoint2,mbo);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select GridNo,SetName,Field_Name,Field_Type,Slope,Flag from muster_cell ");
		strsql.append("where flag!='R' and flag!='H' and flag!='S' and flag!='P' and tabid=");
		strsql.append(tabid);
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());
			HashMap itemMap = null;
			if (salaryid != null && salaryid.length() > 0) {
				SalarySetItemBo salsetbo = new SalarySetItemBo();
				itemMap = salsetbo.fieldItemMap(conn, salaryid, userView);
			}
			while (rset.next()) {
				String fieldname = rset.getString("Field_Name");
				String aSetName = rset.getString("SetName");
				String type = rset.getString("Field_Type");
				String gridNo = rset.getString("GridNo");
				String flags = rset.getString("Flag");
				int Slope = rset.getInt("Slope");

				if (fieldname != null && !"".equals(fieldname)) {

					/* 判断该指标是否已被删除或还没构库 */
					if ("salary".equalsIgnoreCase(this.modelFlag)|| (salaryid != null && salaryid.length() > 0)) {
						if (!"salary".equalsIgnoreCase(this.modelFlag)) {
							Field field = (Field) itemMap.get(fieldname.toUpperCase());
							if (field == null) {
								throw GeneralExceptionHandler.Handle(new Exception(fieldname+ ResourceFactory.getProperty("workdiary.message.roster.del")));
							}
						}
					}
					FieldItem field = DataDictionary.getFieldItem(fieldname.toLowerCase());
					if (field == null) {
//						throw GeneralExceptionHandler
//								.Handle(new Exception(
//										fieldname
//												+ ResourceFactory
//														.getProperty("workdiary.message.roster.del")));
						this.fieldIsExist = false;
						Field a_temp = new Field("C" + gridNo, fieldname);
						if("N".equalsIgnoreCase(type)){
							if(Slope<1) {
                                a_temp.setDatatype(DataType.INT);
                            } else{
								a_temp.setDatatype(DataType.FLOAT);
								a_temp.setDecimalDigits(Slope);
								a_temp.setLength(15);
							}
						}else if("M".equalsIgnoreCase(type)){
							a_temp.setDatatype(DataType.CLOB);
						}else {
							a_temp.setDatatype(DataType.STRING);
							a_temp.setLength(255);
						}
						a_temp.setVisible(false);
						a_temp.setAlign("left");
						fieldsList.add(a_temp);
						continue;
					}

					Field obj = new Field("C" + gridNo, "C" + gridNo);
					if ("A".equals(type)) {
						obj.setDatatype(DataType.STRING);
						obj.setVisible(false);
						if(field!=null&& "0".equals(field.getCodesetid())){
							obj.setLength(field.getItemlength());
						}else{
							if(field!=null)
							{
								if("UN".equalsIgnoreCase(field.getCodesetid())|| "UM".equalsIgnoreCase(field.getCodesetid())|| "@K".equalsIgnoreCase(field.getCodesetid())) {
                                    obj.setLength(mbo.getOrgdesc_length());
                                } else {
                                    obj.setLength(mbo.getCodedesc_length());
                                }
							}else {
                                obj.setLength(60);
                            }
						}
						obj.setAlign("left");

					} else if ("M".equals(type)) {
						obj.setDatatype(DataType.CLOB);
						obj.setVisible(false);
						obj.setAlign("left");
					} else if ("D".equals(type)) {
						 obj.setDatatype(DataType.STRING);
						 obj.setLength(20);
						 obj.setVisible(false);
						 obj.setFormat("yyyy.MM.dd");
//						obj.setDatatype(DataType.DATE);
//						obj.setVisible(false);
						obj.setAlign("right");
					} else if ("N".equals(type)) {
						if(Slope<1){
							obj.setDatatype(DataType.INT);

						}else{
							obj.setDatatype(DataType.FLOAT);
							obj.setDecimalDigits(Slope);
							obj.setLength(15);
						}
						obj.setVisible(false);
						obj.setAlign("left");

					}
					fieldsList.add(obj);
				} else {

					if ("C".equals(flags) || "G".equals(flags)) {
						Field temp3 = new Field("C" + gridNo, ResourceFactory.getProperty("hmuster.label.expressions"));
						if ("G".equals(flags)) {
                            type = "A";
                        }
						if ("N".equals(type)) {
							temp3.setDatatype(DataType.FLOAT);
							temp3.setDecimalDigits(4);
							temp3.setLength(15);
							temp3.setVisible(false);
						} else if ("A".equals(type)) {
							temp3.setDatatype(DataType.STRING);
							temp3.setVisible(false);
							temp3.setLength(255);
							temp3.setAlign("left");
						} else if("M".equals(type)){
							temp3.setDatatype(DataType.CLOB);
							temp3.setVisible(false);
							temp3.setAlign("left");
						}else if ("D".equals(type)) {
							//liuy 2015-2-10 7511：薪资分析-用户自定义表， 105号花名册，插入了计算公式，日期型，取出来的数据带了时分秒，不对。 start
							temp3.setDatatype(DataType.STRING);
							temp3.setLength(20);
							temp3.setVisible(false);
							temp3.setFormat("yyyy.MM.dd");
							//temp3.setDatatype(DataType.DATE);
							//temp3.setVisible(false);
							temp3.setAlign("right");
							//liuy 2015-2-10 end
						}
						fieldsList.add(temp3);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldsList;
	}

	/**
	 * 取得花名册结构固定指标项列表
	 * 
	 * @param tabid
	 * @param nModule
	 *         	1;  // 保险台帐
  	 *			2;  // 合同台帐
  	 *			3;  // 人员名册
  	 *			4;  // 工资台帐
  	 *			5;  // 模板花名册
  	 *			6;  // 工资分析名册
  	 *			7;  // 报批花名册
  	 *			8;  // 保险分析花名册
  	 *			11;  // 保险自定义花名册
  	 *			14;  // 工资自定义报表
  	 *			15;  // 个人所得税
  	 *			16;  // 模板归档
  	 *			17;  // 绩效评估花名册
	 *
  	 *			21;  // 机构花名册
  	 *			41;  // 职位花名册
  	 *			51:  // 基准岗位
  	 *			61;  // 培训花名册
  	 *			81;  // 考勤花名册
	 * @return
	 */
	public ArrayList getMusterFixupFields(String tabid, String flag, int nModule,String groupPoint,String groupPoint2,MusterBo mbo) {
		ArrayList list = new ArrayList();
		Field a_temp = new Field("recidx", ResourceFactory.getProperty("recidx.label"));
		a_temp.setNullable(false);
		a_temp.setKeyable(true);
		a_temp.setDatatype(DataType.INT);
		a_temp.setSortable(true);
		list.add(a_temp);
		if (nModule == 21) {
			Field temp = new Field("B0110", ResourceFactory.getProperty("hmuster.label.unitNo"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setLength(mbo.getOrg_length());
			list.add(temp);
			this.indexColumn="recidx,B0110";
		} else if (nModule == 41) {

			Field temp = new Field("E01A1", ResourceFactory.getProperty("hmuster.label.post"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setLength(mbo.getOrg_length());
			list.add(temp);
            this.indexColumn="recidx,e01a1";
		} else if (nModule == 51) {

			Field temp = new Field("H0100", ResourceFactory.getProperty("h0100.label"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setLength(mbo.getOrg_length());
			list.add(temp);
            this.indexColumn="recidx,h0100";
		} else{
			if(nModule == 15||nModule == 14||nModule == 11||nModule == 81||nModule == 16||nModule == 8||
			        nModule == 6||nModule == 5||nModule == 3||nModule == 1||nModule == 4||nModule ==2){
				Field temp0 = new Field("NBASE", ResourceFactory.getProperty("popedom.db"));
				temp0.setDatatype(DataType.STRING);
				temp0.setVisible(false);
				temp0.setLength(8);
				list.add(temp0);
				this.indexColumn="recidx,A0100,Nbase";
			}else{
				this.indexColumn="recidx,A0100";
			}

			Field temp = new Field("A0000", ResourceFactory.getProperty("hmuster.label.innerSerial"));
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			list.add(temp);

			Field temp2 = new Field("A0100", ResourceFactory.getProperty("hmuster.label.machineNo"));
			temp2.setDatatype(DataType.STRING);
			temp2.setVisible(false);
			temp2.setLength(8);
			list.add(temp2);

			Field temp3 = null;
			temp3 = new Field("B0110", ResourceFactory.getProperty("hmuster.label.unitNo"));
			temp3.setDatatype(DataType.STRING);
			temp3.setVisible(false);
			temp3.setLength(mbo.getOrg_length());
			list.add(temp3);

			Field temp4 = null;
			temp4 = new Field("E0122", ResourceFactory.getProperty("hmuster.label.departmentNo"));
			temp4.setDatatype(DataType.STRING);
			temp4.setVisible(false);
			temp4.setLength(mbo.getOrg_length());
			list.add(temp4);
			
			// 考勤
			if(nModule == 81){
			    // 主键
			    if(getKqKeyFieldName().length() > 0) {
                    Field temp5 = new Field(getKqKeyFieldName(), getKqKeyFieldName());
                    temp5.setDatatype(DataType.STRING);
                    temp5.setVisible(false);
                    temp5.setLength(10);
                    list.add(temp5);
			    }
			    
				Field temp52 = new Field("E01A1", ResourceFactory.getProperty("hmuster.label.post"));
				temp52.setDatatype(DataType.STRING);
				temp52.setVisible(false);
				temp52.setLength(mbo.getOrg_length());
				list.add(temp52);
			}
			
			Field temp5 = new Field("ext", ResourceFactory.getProperty("hmuster.label.ext"));
			temp5.setDatatype(DataType.STRING);
			temp5.setVisible(false);
			temp5.setLength(8);
			list.add(temp5);
			
			if(nModule == 15||nModule == 14||nModule == 11||nModule == 8||nModule == 6){
				Field temp01 = new Field("A00Z0", ResourceFactory.getProperty("gz.columns.A00Z0"));
				temp01.setDatatype(DataType.DATE);
				temp01.setVisible(false);
				list.add(temp01);

				Field temp02 = new Field("A00Z1", ResourceFactory.getProperty("gz.columns.A00Z1"));
				temp02.setDatatype(DataType.INT);
				temp02.setVisible(false);
				list.add(temp02);
				
				Field tempz2 = new Field("A00Z2", "发放日期");
				tempz2.setDatatype(DataType.DATE);
				tempz2.setVisible(false);
				list.add(tempz2);

				Field tempz3 = new Field("A00Z3", "发放次数");
				tempz3.setDatatype(DataType.INT);
				tempz3.setVisible(false);
				list.add(tempz3);
				
			}
			if(nModule == 15||nModule == 14||nModule == 11||nModule == 8||nModule == 6){
				Field temp1 = new Field("salaryId","工资类别号");
				temp1.setDatatype(DataType.INT);	
				temp1.setVisible(false);
				list.add(temp1);
			}
			if(nModule == 15){
				Field temp1 = new Field("tax_max_id","个税明细id");
				temp1.setDatatype(DataType.INT);
				temp1.setVisible(false);
				list.add(temp1);
			}
			if(nModule == 7){
				Field temp1 = new Field("TempletId","摸板id");
				temp1.setDatatype(DataType.INT);	
				temp1.setVisible(false);
				list.add(temp1);
			}
			if(nModule == 16){
				Field temp1 = new Field("id","id");
				temp1.setDatatype(DataType.INT);	
				temp1.setVisible(false);
				list.add(temp1);
				
				Field temp01 = new Field("tabid","tabid");
				temp01.setDatatype(DataType.INT);
				temp01.setVisible(false);
				list.add(temp01);
			}
		}

		Field temp21 = new Field("I9999", ResourceFactory.getProperty("hmuster.label.no"));
		temp21.setDatatype(DataType.INT);
		temp21.setVisible(false);
		list.add(temp21);

		Field temp31 = new Field("GroupN", ResourceFactory.getProperty("hmuster.label.groupValue"));
		temp31.setDatatype(DataType.STRING);
		temp31.setVisible(false);
		if(groupPoint!=null&&groupPoint.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
	    		        "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp31.setLength(mbo.getOrg_length());
                } else if(item.isCode()) {
                    temp31.setLength(mbo.getCode_length());
                } else {
                    temp31.setLength(item.getItemlength());
                }
	    	}else{
	    		temp31.setLength(30);
	    	}
		}else
		{
			temp31.setLength(30);
		}
		list.add(temp31);
		Field temp41 = new Field("GroupV", ResourceFactory.getProperty("hmuster.label.groupName"));
		temp41.setDatatype(DataType.STRING);
		temp41.setVisible(false);
		if(groupPoint!=null&&groupPoint.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
	    		        "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp41.setLength(mbo.getOrgdesc_length());
                } else if(item.isCode()) {
                    temp41.setLength(mbo.getCodedesc_length());
                } else {
                    temp41.setLength(item.getItemlength());
                }
	    	}else{
	    		temp41.setLength(50);
	    	}
		}else
		{
			temp41.setLength(50);
		}
		list.add(temp41);
		/**新加双分组指标*/
		Field temp32 = new Field("GroupN2", ResourceFactory.getProperty("hmuster.label.groupValue"));
		temp32.setDatatype(DataType.STRING);
		temp32.setVisible(false);
		if(groupPoint2!=null&&groupPoint2.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint2.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
	    		        "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp32.setLength(mbo.getOrg_length());
                } else if(item.isCode()) {
                    temp32.setLength(mbo.getCode_length());
                } else {
                    temp32.setLength(item.getItemlength());
                }
	    	}else{
	    		temp32.setLength(30);
	    	}
		}else
		{
			temp32.setLength(30);
		}
		list.add(temp32);

		Field temp42 = new Field("GroupV2", ResourceFactory.getProperty("hmuster.label.groupName"));
		temp42.setDatatype(DataType.STRING);
		temp42.setVisible(false);
		if(groupPoint2!=null&&groupPoint2.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint2.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
	    		        "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp42.setLength(mbo.getOrgdesc_length());
                } else if(item.isCode()) {
                    temp42.setLength(mbo.getCodedesc_length());
                } else {
                    temp42.setLength(item.getItemlength());
                }
	    	}else{
	    		temp42.setLength(50);
	    	}
		}else
		{
			temp42.setLength(50);
		}
		list.add(temp42);

		return list;

	}
	
	private String getKqKeyFieldName() {
	    String result = "";
	    if ("3".equals(kqTableId)||"5".equals(kqTableId)||"7".equals(kqTableId)||"9".equals(kqTableId)) {
	        result = "Q03Z0";
	    }
	    else {
	        if (kqTableId.length() == 1) {
                result = "Q0"+kqTableId+"01";
            } else if (kqTableId.length() == 2) {
                result = "Q"+kqTableId+"01";
            }
	    }
	    return result;
	}

	/**
	 * 取得花名册结构指标项列表
	 * 
	 * @param tabid
	 *            flag:信息集
	 * @param nModule
	 *            模块标志
	 * @return
	 */
	private ArrayList getMusterFields(String tabid, String flag, int nModule,String groupPoint,String groupPoint2,MusterBo mbo)
			throws GeneralException {
		ArrayList fieldsList = new ArrayList();
		
		fieldsList = getMusterFixupFields(tabid, flag, nModule,groupPoint,groupPoint2,mbo);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select GridNo,SetName,Field_Name,Field_Type,Flag,RCount,extendattr,codeid from muster_cell where flag!='E' and  flag!='G' and flag!='R' and flag!='H' and flag!='S' and flag!='P'  and tabid=");
		strsql.append(tabid);
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());
			
			ArrayList<String> sortList=new ArrayList<String>();
			if(sortitem!=null&&sortitem.length()>0) {//ADLAN:入职经办时间:0
				for(int i=0;i<sortitem.split("`").length;i++) {
					sortList.add(sortitem.split("`")[i].split(":")[0].toUpperCase());
				}
			}
			
			while (rset.next()) {
				String fieldname = rset.getString("Field_Name");
				String aSetName = rset.getString("SetName");
				String type = rset.getString("Field_Type");
				String gridNo = rset.getString("GridNo");
				String flags = rset.getString("Flag");
				int RCount = rset.getInt("RCount");
				String codeid=rset.getString("codeid");
				codeid=(codeid==null|| "".equals(codeid.trim()))?"0":codeid;
				String extendattr=rset.getString("extendattr")==null?"":rset.getString("extendattr");
				int length=20;
				int dewidth=4;
				if (fieldname != null && !"".equals(fieldname)) {
         			// 人员库
				    if("NBASE".equalsIgnoreCase(fieldname)&&"A01".equalsIgnoreCase(aSetName)) {
                        Field a_temp = new Field("C" + gridNo, fieldname);
                        a_temp.setLength(30);
                        a_temp.setDatatype(DataType.STRING);
                        a_temp.setVisible(false);
                        a_temp.setAlign("left");
                        fieldsList.add(a_temp);
                        continue;
				    }
                    /* 判断该指标是否已被删除或还没构库 */
                    FieldItem item = DataDictionary.getFieldItem(fieldname);
					if (item == null) {
						this.fieldIsExist = false;
						Field a_temp = new Field("C" + gridNo, fieldname);
						item=(FieldItem)this.midvariableMap.get(fieldname.toUpperCase());
						if(item!=null){
							length = item.getItemlength();
							dewidth = item.getDecimalwidth();
						}
						if ("N".equals(type)) {
							a_temp.setDatatype(DataType.FLOAT);
							a_temp.setDecimalDigits(dewidth);
							a_temp.setLength(length);
							a_temp.setVisible(false);
							a_temp.setAlign("left");
						}else if ("M".equals(type)) {
							a_temp.setDatatype(DataType.CLOB);
							a_temp.setVisible(false);
							a_temp.setAlign("left");
						} else if ("D".equals(type)) {
							if(sortList.contains(fieldname.toUpperCase())) {
								Field d_temp=new Field("D"+gridNo,fieldname);
								d_temp.setDatatype(DataType.DATETIME);
								d_temp.setLength(20);
								d_temp.setVisible(false);
								d_temp.setFormat("yyyy.MM.dd ");
								d_temp.setAlign("right");
								fieldsList.add(d_temp);
							}
							
							a_temp.setDatatype(DataType.STRING);
							a_temp.setLength(20);//a_temp.setLength(20);//liuy 2015-11-12 14124:合同管理模块高级花名册后台能取到数据，前台取不到数据
							a_temp.setVisible(false);
							a_temp.setFormat("yyyy.MM.dd");
							a_temp.setAlign("right");
						} else{
							if("UN".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid)|| "@K".equalsIgnoreCase(codeid)) {
                                a_temp.setLength(mbo.getOrgdesc_length());
                            } else if(!"0".equals(codeid)) {
                                a_temp.setLength(mbo.getCodedesc_length());
                            } else {
                                a_temp.setLength(255);
                            }
							a_temp.setDatatype(DataType.STRING);
							a_temp.setVisible(false);
							a_temp.setAlign("left");
						}
						fieldsList.add(a_temp);
						continue;
						// break;

					}
					if ("0".equals(item.getUseflag())) {
						this.fieldIsExist = false;
						Field a_temp = new Field("C" + gridNo, fieldname);
						
						if ("N".equals(type)) {
							a_temp.setDatatype(DataType.FLOAT);
							a_temp.setDecimalDigits(item.getDecimalwidth());//4
							a_temp.setLength(item.getItemlength());//15
							a_temp.setVisible(false);
							a_temp.setAlign("left");
						}else if ("M".equals(type)) {
							a_temp.setDatatype(DataType.CLOB);
							a_temp.setVisible(false);
							a_temp.setAlign("left");
						} else if ("D".equals(type)) {
							if(sortList.contains(fieldname)) {
								Field d_temp=new Field("D"+gridNo,fieldname);
								d_temp.setDatatype(DataType.DATETIME);
								d_temp.setLength(20);
								d_temp.setVisible(false);
								d_temp.setFormat("yyyy.MM.dd ");
								d_temp.setAlign("right");
								fieldsList.add(d_temp);
							}
							
							a_temp.setDatatype(DataType.STRING);
							a_temp.setLength(20);
							a_temp.setVisible(false);
							a_temp.setFormat("yyyy.MM.dd");
							a_temp.setAlign("right");
						} else{
							a_temp.setDatatype(DataType.STRING);
							a_temp.setVisible(false);
							if(item.getCodesetid()==null|| "0".equals(item.getCodesetid())) {
                                a_temp.setLength(item.getItemlength());
                            } else
							{
								if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                                    a_temp.setLength(mbo.getOrgdesc_length());
                                } else {
                                    a_temp.setLength(mbo.getCodedesc_length());
                                }
							}
							a_temp.setAlign("left");
						}
						fieldsList.add(a_temp);
						continue;
						// break;
					}
					Field obj = new Field("C" + gridNo, item.getItemdesc());
					if((extendattr.toUpperCase().indexOf("<NAMECOUNT>")!=-1||extendattr.toUpperCase().indexOf("<NAMEVALUE>")!=-1)&&"N".equalsIgnoreCase(type))
					{
						obj.setDatatype(DataType.FLOAT);
			    		obj.setDecimalDigits(5);
			    		obj.setLength(10);
		    			obj.setVisible(false);
		    			obj.setAlign("left");

					}
					else{
						type = item.getItemtype();
					    if ("A".equals(type)) {
					    	if ("A".equals(flags) && !"A".equals(flag)) // 如果查询的不是人员库并且字段中含有人员字段
					    	{
						    	if("C".equalsIgnoreCase(flag)){
						    		obj.setDatatype(DataType.STRING);
							     	obj.setVisible(false);
								    obj.setLength(RCount);
							    	obj.setAlign("left");
						     	}else{
							    	obj.setDatatype(DataType.CLOB);
							    	obj.setVisible(false);
						    		obj.setAlign("left");
					    		}
					    	} else {
						    	obj.setDatatype(DataType.STRING);
						    	obj.setVisible(false);
						    	if(item.getCodesetid()==null|| "0".equals(item.getCodesetid())) {
                                    obj.setLength(item.getItemlength());
                                } else
						    	{
						    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                                        obj.setLength(mbo.getOrgdesc_length());
                                    } else {
                                        obj.setLength(mbo.getCodedesc_length());
                                    }
						    	}
						    	obj.setAlign("left");
					    	}
				    	} else if ("M".equals(type)) {
					    	obj.setDatatype(DataType.CLOB);
					    	obj.setVisible(false);
					    	obj.setAlign("left");
				    	} else if ("D".equals(type)) {
				    		if(sortList.contains(fieldname)) {
								Field d_temp=new Field("D"+gridNo,fieldname);
								d_temp.setDatatype(DataType.DATETIME);
								d_temp.setLength(20);
								d_temp.setVisible(false);
								d_temp.setFormat("yyyy.MM.dd ");
								d_temp.setAlign("right");
								fieldsList.add(d_temp);
							}
				    		
					    	obj.setDatatype(DataType.STRING);
					    	obj.setLength(20);
					    	obj.setVisible(false);
					    	obj.setFormat("yyyy.MM.dd");
				    		obj.setAlign("right");
				    	} else if ("N".equals(type)) {
					    	obj.setDatatype(DataType.FLOAT);
				    		obj.setDecimalDigits(item.getDecimalwidth());
				    		obj.setLength(item.getItemlength());
			    			obj.setVisible(false);
			    			obj.setAlign("left");

		    			}
					}
					fieldsList.add(obj);
				} else {

					if ("C".equals(flags)) {
						Field temp3 = new Field("C" + gridNo, ResourceFactory.getProperty("hmuster.label.expressions"));
                        
						if ("N".equals(type)) {
							temp3.setDatatype(DataType.FLOAT);
							temp3.setDecimalDigits(4);
							temp3.setLength(15);
							temp3.setVisible(false);
						}else if ("A".equals(type)){
							temp3.setDatatype(DataType.STRING);
							temp3.setVisible(false);
							if("0".equals(codeid)) {
                                temp3.setLength(RCount);
                            } else if("UN".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid)|| "@K".equalsIgnoreCase(codeid)) {
                                temp3.setLength(mbo.getOrgdesc_length());
                            } else {
                                temp3.setLength(mbo.getCodedesc_length());
                            }
							temp3.setAlign("left");
						} else if ("M".equals(type)) {
							temp3.setDatatype(DataType.CLOB);
							temp3.setVisible(false);
							temp3.setAlign("left");
						} else if ("D".equals(type)) {
							if(sortList.contains(fieldname)) {
								Field d_temp=new Field("D"+gridNo,fieldname);
								d_temp.setDatatype(DataType.DATETIME);
								d_temp.setLength(20);
								d_temp.setVisible(false);
								d_temp.setFormat("yyyy.MM.dd ");
								d_temp.setAlign("right");
								fieldsList.add(d_temp);
							}
							
							temp3.setDatatype(DataType.STRING);
							temp3.setLength(20);
							temp3.setVisible(false);
							temp3.setFormat("yyyy.MM.dd");
							temp3.setAlign("right");
						}
						fieldsList.add(temp3);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldsList;
	}

	/**
     * 取得花名册结构临时指标项列表
     * 
     * @param tabid
     *            flag:信息集
     * @param nModule
     *            模块标志
     * @return
     */
    private ArrayList getMusterTempFields(String tabid, String flag, int nModule,MusterBo mbo)
            throws GeneralException {
        ArrayList fieldsList = new ArrayList();
        StringBuffer strsql = new StringBuffer();
        strsql.append("select GridNo,SetName,Field_Name,Field_Type,Flag,RCount,extendattr,codeid from muster_cell where flag!='E' and  flag!='G' and flag!='R' and flag!='H' and flag!='S' and flag!='P'  and tabid=");
        strsql.append(tabid);
        RowSet rset = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rset = dao.search(strsql.toString());
            while (rset.next()) {
                String fieldname = rset.getString("Field_Name");
                String aSetName = rset.getString("SetName");
                String type = rset.getString("Field_Type");
                String gridNo = rset.getString("GridNo");
                String flags = rset.getString("Flag");
                int RCount = rset.getInt("RCount");
                String codeid=rset.getString("codeid");
                codeid=(codeid==null|| "".equals(codeid.trim()))?"0":codeid;
                String extendattr=rset.getString("extendattr")==null?"":rset.getString("extendattr");
                int length=20;
                int dewidth=4;
                if ("A".equals(type)) {
                    //
                } else if ("M".equals(type)) {
                    //
                } else if ("D".equals(type)) {
                    Field obj = new Field("D" + gridNo, "D" + gridNo);  // 用于花名册指标计算
                    obj.setDatatype(DataType.DATETIME);
                    obj.setVisible(false);
                    fieldsList.add(obj);
                } else if ("N".equals(type)) {
                    //
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }finally{
            try
            {
                if(rset!=null) {
                    rset.close();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return fieldsList;
    }
    
	/**
	 * 取得所有花名册列表
	 * 
	 * @param inforkind
	 * @return
	 */
	public ArrayList getMusterList(String inforkind) throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		int nModule = 0;
		if ("1".equals(inforkind)) // 人员库
		{
			inforkind = "A";
			nModule = 3;
		} else if ("3".equals(inforkind)) // 职位库
		{
			inforkind = "K";
			nModule = 1;
		} else if ("2".equals(inforkind)) // 单位库
		{
			inforkind = "B";
			nModule = 2;

		}
		strsql.append("select tabid,cname from muster_name where flagA='");
		strsql.append(inforkind);
		strsql.append("'");
		if ("A".equals(inforkind)) {
            strsql.append(" and nModule=" + nModule);
        }
		/* 此三条记录不予显示 */
		strsql.append(" and tabid!=1000 and tabid!=1010 and tabid!=1020");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		try {
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				String[] temp = new String[2];
				temp[0] = recset.getString("tabid");
				temp[1] = recset.getString("cname");
				list.add(temp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	public String getQueryFromPartLike(UserView userview,String dbpre,String vorgcode)
	{
		StringBuffer buf=new StringBuffer();
		ArrayList fieldlist=new ArrayList();
		String strWhere=null;		
		try
		{
			
			
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			 /**兼职参数*/
			 String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
			
			 String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
			 /**兼职单位字段*/
			 String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");	
			 /**兼职部门字段*/
			 String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
			 /**兼职排序字段*/
			 String order_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "order");
			 /**兼职岗位字段*/
			 String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
			 /**任免标识=0任=1免*/
			 String appoint=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
			 /**分析此定义的数据集和指标是否构库*/
			 FieldSet fieldset=null;
			 if(setid!=null&&!"".equals(setid.trim())&&!"#".equals(setid)) {
                 fieldset=DataDictionary.getFieldSetVo(setid);
             }
			 FieldItem unititem=null;
			 if(unit_field!=null&&!"".equals(unit_field.trim())&&!"#".equals(unit_field)) {
                 unititem=DataDictionary.getFieldItem(unit_field);
             }
			 FieldItem deptitem=null;
			 if(dept_field!=null&&!"".equals(dept_field.trim())&&!"#".equals(dept_field)) {
                 deptitem=DataDictionary.getFieldItem(dept_field);
             }
			 FieldItem orderitem=null;
			 if(order_field!=null&&!"".equals(order_field.trim())&&!"#".equals(order_field)) {
                 orderitem=DataDictionary.getFieldItem(order_field);
             }
			 FieldItem positem =null;
			 if(pos_field!=null&&!"".equals(pos_field.trim())&&!"#".equals(pos_field)) {
                 positem=DataDictionary.getFieldItem(pos_field);
             }
			 FieldItem appointitem = null;
			 if(appoint!=null&&!"".equals(appoint.trim())&&!"#".equals(appoint)) {
                 appointitem=DataDictionary.getFieldItem(appoint);
             }
			 if(partflag==null|| "".equalsIgnoreCase(partflag)|| "false".equalsIgnoreCase(partflag)||
			         fieldset==null|| "0".equals(fieldset.getUseflag())) {
                 return "";
             }
			 String app_set=dbpre.toUpperCase()+setid.toUpperCase();
			 String privsql=userview.getPrivSQLExpression(dbpre, true);
			 privsql=privsql.substring(12);
			 String mainset=dbpre.toUpperCase()+"A01".toUpperCase();
			 buf.append(" "+dbpre+"A01.a0100 in (");
			 buf.append("select "+dbpre+"A01.a0100 from "+dbpre+"A01 ");
			 if(privsql.indexOf(app_set)!=-1)
			 {
				 
			 }else {
				 buf.append(" left join ");
				 buf.append(app_set+" on "+dbpre+"A01.A0100="+app_set+".A0100 ");
			 }
			 if(unititem!=null&& "1".equals(unititem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".B0110", app_set+"."+unit_field);
             }
			 if(deptitem!=null&& "1".equals(deptitem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".E0122", app_set+"."+dept_field);
             }
			 buf.append(privsql);
			 if(appointitem!=null&& "1".equals(appointitem.getUseflag())) {
                 buf.append(" and "+app_set+"."+appointitem.getItemid()+"='0'");
             }
			 buf.append(")");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return buf.toString();
	}	
	
    private String getQueryFromPartTmpl(UserView userview,String dbpre,String vorgcode, String partCond)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;       
        try
        {
            
            
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
            
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
             /**兼职部门字段*/
             String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
             /**兼职排序字段*/
             String order_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "order");
             /**兼职岗位字段*/
             String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
             /**任免标识=0任=1免*/
             String appoint=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=null;
             if(setid!=null&&!"".equals(setid.trim())&&!"#".equals(setid)) {
                 fieldset=DataDictionary.getFieldSetVo(setid);
             }
             FieldItem unititem=null;
             if(unit_field!=null&&!"".equals(unit_field.trim())&&!"#".equals(unit_field)) {
                 unititem=DataDictionary.getFieldItem(unit_field);
             }
             FieldItem deptitem=null;
             if(dept_field!=null&&!"".equals(dept_field.trim())&&!"#".equals(dept_field)) {
                 deptitem=DataDictionary.getFieldItem(dept_field);
             }
             FieldItem orderitem=null;
             if(order_field!=null&&!"".equals(order_field.trim())&&!"#".equals(order_field)) {
                 orderitem=DataDictionary.getFieldItem(order_field);
             }
             FieldItem positem =null;
             if(pos_field!=null&&!"".equals(pos_field.trim())&&!"#".equals(pos_field)) {
                 positem=DataDictionary.getFieldItem(pos_field);
             }
             FieldItem appointitem = null;
             if(appoint!=null&&!"".equals(appoint.trim())&&!"#".equals(appoint)) {
                 appointitem=DataDictionary.getFieldItem(appoint);
             }
             if(partflag==null|| "".equalsIgnoreCase(partflag)|| "false".equalsIgnoreCase(partflag)||
                     fieldset==null|| "0".equals(fieldset.getUseflag())) {
                 return "";
             }
             String app_set=dbpre.toUpperCase()+setid.toUpperCase();
             String privsql=userview.getPrivSQLExpression(dbpre, true).toUpperCase();
             privsql=privsql.substring(12);
             String mainset=dbpre.toUpperCase()+"A01".toUpperCase();
             buf.append(" "+dbpre+"A01.a0100 in (");
             buf.append("select "+dbpre+"A01.a0100 from "+dbpre+"A01 ");
             if(privsql.indexOf(app_set)!=-1)
             {
                 
             }else {
                 buf.append(" left join ");
                 buf.append(app_set+" on "+dbpre+"A01.A0100="+app_set+".A0100 ");
             }
             if(unititem!=null&& "1".equals(unititem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".B0110", app_set+"."+unit_field);
             }
             if(deptitem!=null&& "1".equals(deptitem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".E0122", app_set+"."+dept_field);
             }
             buf.append(privsql);
             if(appointitem!=null&& "1".equals(appointitem.getUseflag())) {
                 buf.append(" and "+app_set+"."+appointitem.getItemid()+"='0'");
             }
             if(partCond != null) {
                 buf.append(" and "+ partCond);
             }
             buf.append(")");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
	   
	public String getGroupPointItem() {
		return groupPointItem;
	}

	public void setGroupPointItem(String groupPointItem) {
		this.groupPointItem = groupPointItem;
	}

	public ArrayList getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(ArrayList fieldsList) {
		this.fieldsList = fieldsList;
	}

	public String getModelFlag() {
		return modelFlag;
	}

	public void setModelFlag(String modelFlag) {
		this.modelFlag = modelFlag;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getLayerid() {
		return layerid;
	}

	public void setLayerid(String layerid) {
		this.layerid = layerid;
	}
	
	public String getCountflag() {
		return countflag;
	}

	public void setCountflag(String countflag) {
		this.countflag = countflag;
	}

	public String getTemptable() {
		return temptable;
	}

	public void setTemptable(String temptable) {
		this.temptable = temptable;
	}
    public void transformQueryCond(String tableName, String tabid,
			UserView userview, String infor_flag, String dbpre, String relatTableid/*, String year*/
			/*,String month*//*, String count*//*, String history*//*, String queryScope*//*,
			String userName*/)
    {
    	ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			RecordVo vo=new RecordVo("Template_table");
			vo.setInt("tabid",Integer.parseInt(relatTableid));
			ArrayList fielditem=new ArrayList();
			if(dao.isExistRecordVo(vo))
			{
		    	TemplateTableBo bo = new TemplateTableBo(this.conn,Integer.parseInt(relatTableid),userview);
		    	fielditem=bo.getAllFieldItem();
			}
			String changeBeforeItemdescs = "";//变化前指标名称字符串
			for(int i=0;i<fielditem.size();i++)
			{
				FieldItem item=(FieldItem)fielditem.get(i);
				if(item.getVarible()!=1)
				{
					String sql = "select HisMode,sub_domain,hz from Template_Set where tabid="+relatTableid+" and field_name='"+item.getItemid().toUpperCase()+"'";
					if(item.isChangeAfter()){//变化后
						//liuy 2015-11-12 14124:合同管理模块高级花名册后台能取到数据，前台取不到数据 begin
						rowSet = dao.search(sql + " and chgstate=2");
						if(rowSet.next()){
							String hz = rowSet.getString("hz");
							item.setItemdesc(hz.replaceAll("`", ""));
						}
						//liuy 2015- 14124:合同管理模块高级花名册后台能取到数据，前台取不到数据 end
						item.setItemid(item.getItemid()+"_2");
					}else if(item.isChangeBefore()){//变化前
						//liuy 2016-6-15 16547：北医三院：业务模块中的高级花名册，名册中插入计算公式，在bs输出高级花名册时，算不出数据 end
						rowSet = dao.search(sql + " and chgstate=1");
						String itemid = "";
						while(rowSet.next()){
							String sub_domain = rowSet.getString("sub_domain");
							String sub_domain_id = getSubDomainId(sub_domain);
							String hisMode = rowSet.getString("HisMode");
							String hz = rowSet.getString("hz");
							if(hz!=null){
								hz = hz.replaceAll("`", "");
								if(changeBeforeItemdescs.indexOf(hz + ",")==-1){									
									item.setItemdesc(hz.replaceAll("`", ""));
									changeBeforeItemdescs += hz + ",";
									if(StringUtils.isNotEmpty(sub_domain_id)){
										itemid = item.getItemid()+"_"+sub_domain_id+"_1";
										if("2".equals(hisMode)||"3".equals(hisMode)||"4".equals(hisMode)) {
                                            item.setItemtype("M");
                                        }
									}else {
                                        itemid = item.getItemid()+"_1";
                                    }
									break;
								}
							}
						}
						//liuy 2016-6-15 16547：北医三院：业务模块中的高级花名册，名册中插入计算公式，在bs输出高级花名册时，算不出数据 end
						item.setItemid(itemid);
					}
				}
			}
			String ttable=userview.getUserName() + "Templet_" + relatTableid;
			rowSet = dao.search("select * from muster_cell where tabid="
					+ tabid + " and flag='C'");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				String extendAttr = rowSet.getString("ExtendAttr");
				String SimpleFormula = "false";
				if(extendAttr.indexOf("<SimpleFormula>")!=-1){
					SimpleFormula = extendAttr.substring(extendAttr.indexOf("<SimpleFormula>")+"<SimpleFormula>".length(),extendAttr.indexOf("</SimpleFormula>"));
				}
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				if ("2".equals(infor_flag)) {
					infoGroup = 3; // forUnit 单位
				} else if ("3".equals(infor_flag)) {
					infoGroup = 1; // forPosition 职位
				}
				String a_dbpre = "";
				if (infoGroup == 0) {
					a_dbpre = dbpre;
				}
				try {
						YksjParser yp=new YksjParser(userview ,fielditem,YksjParser.forNormal, varType,YksjParser.forPerson , "Ht", "");
						yp.run(queryCond,this.conn,"",ttable);
						/**单表计算*/
						String sqlstr = yp.getSQL();
						StringBuffer buf = new StringBuffer();
						 /* update su_muster_51 T set T.C6=(select  NVL(c9301_2,0) * 0.12 as C6 from suTemplet_101 S
					    where T.a0100=S.a0100) where exists (select null from suTemplet_101 S where T.a0100=S.a0100)*/
						if(varType==9){//计算公式关于日期类型的全部转为YYYY.MM.dd   27294 云南金孔雀交通运输集团：后台模板花名册取出的数据与前台BS取出的数据显示不一致
							StringBuffer sbf=new StringBuffer();
							sbf.append(Sql_switcher.numberToChar(Sql_switcher.year(sqlstr)));
							sbf.append(getString());
							sbf.append(" case when "+Sql_switcher.month(sqlstr)+">9 then '.'"+getString()+Sql_switcher.numberToChar(Sql_switcher.month(sqlstr)));
							sbf.append(" else '.0'"+getString()+Sql_switcher.numberToChar(Sql_switcher.month(sqlstr))+" end ");
							sbf.append(getString());
							sbf.append(" case when "+Sql_switcher.day(sqlstr)+">9 then '.'"+getString()+Sql_switcher.numberToChar(Sql_switcher.day(sqlstr)));
							sbf.append(" else '.0'"+getString()+Sql_switcher.numberToChar(Sql_switcher.day(sqlstr))+" end ");
							sqlstr=sbf.toString();//Sql_switcher.dateToChar(sqlstr,"YYYY.MM.dd");
						}
						if(Sql_switcher.searchDbServer()==Constant.MSSQL)
						{
							StringBuffer tempTableSQL=new StringBuffer();
							tempTableSQL.append("select "+sqlstr+" as C"+gridNo+",");
							if(infoGroup==0) {
                                tempTableSQL.append("a0100");
                            } else if(infoGroup==3) {
                                tempTableSQL.append("b0110");
                            } else if(infoGroup==1) {
                                tempTableSQL.append("e01a1");
                            }
							tempTableSQL.append(" from "+ttable);
							
							buf.append("update "+tableName+" set C"+gridNo);
				     		buf.append("=");
				    		buf.append("S.C"+gridNo+" From  "+tableName+" lEFT JOIN ("+tempTableSQL+") S ON ");
				    		buf.append(tableName+".");
					    	if(infoGroup==0) {
                                buf.append("a0100=S.a0100");
                            } else if(infoGroup==3) {
                                buf.append("b0110=S.b0110");
                            } else if(infoGroup==1) {
                                buf.append("e01a1=S.e01a1");
                            }
			     			
						}
						else
						{
							buf.append("update "+tableName+" T set T.C"+gridNo);
				     		buf.append("=(select "+sqlstr+" as C"+gridNo+" from ");
				    		buf.append(ttable+" S where ");
					    	if(infoGroup==0) {
                                buf.append("T.a0100=S.a0100");
                            } else if(infoGroup==3) {
                                buf.append("T.b0110=S.b0110");
                            } else if(infoGroup==1) {
                                buf.append("T.e01a1=S.e01a1");
                            }
				    		buf.append(") where exists (select null from "+ttable+" S where ");
					    	if(infoGroup==0) {
                                buf.append("T.a0100=S.a0100");
                            } else if(infoGroup==3) {
                                buf.append("T.b0110=S.b0110");
                            } else if(infoGroup==1) {
                                buf.append("T.e01a1=S.e01a1");
                            }
					    	buf.append(")");
				    		
						}
						dao.update(buf.toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

    }
    
    /**
     * 根据xml取得para节点的id值
     * @param sub_domain
     * @return
     */
    private String getSubDomainId(String sub_domain){
    	String sub_domain_id = "";
		Document doc = null;
		Element element = null;
        String xpath = "/sub_para/para";
        if (sub_domain != null && sub_domain.trim().length() > 0) {
            try {
				doc =PubFunc.generateDom(sub_domain);
	            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
	            List childlist = findPath.selectNodes(doc);
	            if (childlist != null && childlist.size() > 0) {
	                element = (Element) childlist.get(0);
	                if (element.getAttributeValue("id") != null) {
	                    sub_domain_id = (String) element.getAttributeValue("id");
	                }
	            }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        return sub_domain_id;
    }
    
    public void transformInsQueryCond(String tableName, String tabid,
			UserView userview, String infor_flag, String dbpre, String relatTableid/*, String year*/
			/*,String month*//*, String count*//*, String history*//*, String queryScope*//*,
			String userName*/)
    {
    	ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			TemplateTableBo bo = new TemplateTableBo(this.conn,Integer.parseInt(relatTableid),userview);
			ArrayList fielditem=bo.getAllFieldItem();
			for(int i=0;i<fielditem.size();i++)
			{
				FieldItem item=(FieldItem)fielditem.get(i);
				if(item.getVarible()!=1)
				{
					if(item.isChangeAfter()) {
                        item.setItemid(item.getItemid()+"_2");
                    }
					if(item.isChangeBefore()) {
                        item.setItemid(item.getItemid()+"_1");
                    }
				}
			}
			String ttable="Templet_" + relatTableid;
			if(this.getSql_str().startsWith(this.userView.getUserName()+"templet_" + relatTableid))//人事异动起草状态 应该查的是操作人保存未提交的数据
            {
                ttable=this.userView.getUserName()+"templet_" + relatTableid;
            }
			rowSet = dao.search("select * from muster_cell where tabid="
					+ tabid + " and flag='C'");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				String extendAttr = rowSet.getString("ExtendAttr");
				String SimpleFormula = "false";
				if(extendAttr.indexOf("<SimpleFormula>")!=-1){
					SimpleFormula = extendAttr.substring(extendAttr.indexOf("<SimpleFormula>")+"<SimpleFormula>".length(),extendAttr.indexOf("</SimpleFormula>"));
				}
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				if ("2".equals(infor_flag)) {
					infoGroup = 3; // forUnit 单位
				} else if ("3".equals(infor_flag)) {
					infoGroup = 1; // forPosition 职位
				}
				String a_dbpre = "";
				if (infoGroup == 0) {
					a_dbpre = dbpre;
				}
				try {
						YksjParser yp=new YksjParser(userview ,fielditem,YksjParser.forNormal, varType,YksjParser.forPerson , "Ht", "");
						yp.run(queryCond,this.conn,"",ttable);
						/**单表计算*/
						String sqlstr = yp.getSQL();
						StringBuffer buf = new StringBuffer();
						 /* update su_muster_51 T set T.C6=(select  NVL(c9301_2,0) * 0.12 as C6 from suTemplet_101 S
					    where T.a0100=S.a0100) where exists (select null from suTemplet_101 S where T.a0100=S.a0100)*/
						if(Sql_switcher.searchDbServer()==Constant.MSSQL)
						{
							StringBuffer tempTableSQL=new StringBuffer();
							tempTableSQL.append("select "+sqlstr+" as C"+gridNo+",");
							if(infoGroup==0) {
                                tempTableSQL.append("a0100");
                            } else if(infoGroup==3) {
                                tempTableSQL.append("b0110");
                            } else if(infoGroup==1) {
                                tempTableSQL.append("e01a1");
                            }
							tempTableSQL.append(" from "+ttable);
							if(this.getSql_str()!=null&&this.getSql_str().length()>0)
							{
								tempTableSQL.append(" where 1=1 and ("+this.getSql_str().substring(this.getSql_str().indexOf("where")+5, this.getSql_str().indexOf("order")>-1?this.getSql_str().indexOf("order"):this.getSql_str().length())+")");
							}
							
							buf.append("update "+tableName+" set C"+gridNo);
				     		buf.append("=");
				    		buf.append("S.C"+gridNo+" From  "+tableName+" lEFT JOIN ("+tempTableSQL+") S ON ");
				    		buf.append(tableName+".");
					    	if(infoGroup==0) {
                                buf.append("a0100=S.a0100");
                            } else if(infoGroup==3) {
                                buf.append("b0110=S.b0110");
                            } else if(infoGroup==1) {
                                buf.append("e01a1=S.e01a1");
                            }
			     			
						}
						else
						{
							buf.append("update "+tableName+" T set T.C"+gridNo);
				     		buf.append("=(select "+sqlstr+" as C"+gridNo+" from ");
				     		if(this.getSql_str()!=null&&this.getSql_str().length()>0)
				     		{
				     			buf.append("( select * from "+ttable+" where 1=1 and ("+this.getSql_str().substring(this.getSql_str().indexOf("where")+5, this.getSql_str().indexOf("order")>-1?this.getSql_str().indexOf("order"):this.getSql_str().length())+")) S where ");
				     		}
				     		else {
                                buf.append(ttable+" S where ");
                            }
					    	if(infoGroup==0) {
                                buf.append("T.a0100=S.a0100");
                            } else if(infoGroup==3) {
                                buf.append("T.b0110=S.b0110");
                            } else if(infoGroup==1) {
                                buf.append("T.e01a1=S.e01a1");
                            }
				    		buf.append(") where exists (select null from ");
				    		if(this.getSql_str()!=null&&this.getSql_str().length()>0) //新版人事异动修改为按选择的人取数据，sql有修改 
				    		{
				    			buf.append("( select * from "+ttable+" where 1=1 and ("+this.getSql_str().substring(this.getSql_str().indexOf("where")+5, this.getSql_str().indexOf("order")>-1?this.getSql_str().indexOf("order"):this.getSql_str().length())+")) S where ");
				    		}
				    		else
				    		{
				    			buf.append(ttable+" S where ");
				    		}
					    	if(infoGroup==0) {
                                buf.append("T.a0100=S.a0100");
                            } else if(infoGroup==3) {
                                buf.append("T.b0110=S.b0110");
                            } else if(infoGroup==1) {
                                buf.append("T.e01a1=S.e01a1");
                            }
					    	buf.append(")");
				    		
						}
						dao.update(buf.toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

    }
    /**
     * 
     * @param nbaseUSR,RET(人员库，逗号分隔，空表示全部人员库)
     * @param fuzzyFlag模糊查询True|False
     * @param his对历史记录查询True|False
     * @param deptOnly查机构库时，仅查部门True|False
     * @param unitOnly查机构库时，仅查单位True|False
     * @param factor
     * @param expr
     * @param info_kind=1人员=2单位=3职位
     * @param userView 用户信息
     * @return
     */
    private HashMap sqlMap = new HashMap();
    private String k01SQL="";
    private String b01SQL="";
    public String getDbpreSQL(String dbpre)
    {
    	if(this.sqlMap.get(dbpre.toUpperCase())!=null) {
            return (String)this.sqlMap.get(dbpre.toUpperCase());
        } else {
            return "";
        }
    }
    public void setDbpreSQL(String dbpre,String sql)
    {
    	this.sqlMap.put(dbpre.toUpperCase(), sql);
    }
    public void getFactorSQL(String nbase,String fuzzyFlag,String his,String deptOnly,
    		String unitOnly,String factor,String expr,String info_kind,UserView userView,String dbpre,String NO_MANAGE_PRIV,String tableName)
    {
    	try
    	{
    		if(nbase==null|| "".equals(nbase))
    		{
    			ArrayList list = userView.getPrivDbList();
    			StringBuffer temp=new StringBuffer("");
    			for(int i=0;i<list.size();i++)
    			{
    				if(i!=0) {
                        temp.append(",");
                    }
    				temp.append((String)list.get(i));
    			}
    			nbase=temp.toString();
    		}
    		if((nbase==null|| "".equals(nbase))&& "1".equals(info_kind))
    		{
    			return;
    		}
    		else
    		{
    			//如果表达式是空就按高级复杂条件来处理
    			if(expr!=null&&expr.trim().length()>0)
    			{
    		    	boolean blike=false;
    	     		if("true".equalsIgnoreCase(fuzzyFlag)) {
                        blike=true;
                    }
    	    		boolean bHistory=false;
    		    	if("true".equalsIgnoreCase(his)) {
                        bHistory=true;
                    }
    		    	boolean bRun=false;
    		    	ArrayList fieldlist = new ArrayList();
    	    		ContentDAO dao = new ContentDAO(this.conn);
    		    	if("1".equals(info_kind))
    		    	{
    		        	String[] arr=nbase.split(",");
    		        	for(int i=0;i<arr.length;i++)
    		        	{
    		        		StringBuffer sb = new StringBuffer("");
    		      	      	String pre=arr[i];
    			      	/*if(!dbpre.equalsIgnoreCase(pre))
    			      		continue;*/
    			        	String strwhere="";
    		        		if((!userView.isSuper_admin())&& "1".equals(info_kind)&& "false".equalsIgnoreCase(NO_MANAGE_PRIV))
    	                    {
         	                    strwhere=userView.getPrivSQLExpression(expr+"|"+factor,pre,bHistory,blike,true,fieldlist);
         	                    if(userView.getStatus()==4&&(userView.getManagePrivCodeValue()==null||"".equals(userView.getManagePrivCodeValue()))){
        	                    	strwhere=strwhere.replaceAll("1=2", "1=1");
								}
    	                    }
        	                else
    	                    {
        	                    FactorList factorlist=new FactorList(expr,factor,pre,bHistory ,blike,true,Integer.parseInt(info_kind),userView.getUserId());
        	                //FactorList factor_bo=new FactorList(sexpr,sfactor,dbpre[i],bhis,blike,bresult,Integer.parseInt(type),userView.getUserId());FactorList factor_bo=new FactorList(sexpr.toString(),PubFunc.getStr(sfactor.toString()),dbpre[i],bhis,blike,bresult,Integer.parseInt(type),userView.getUserId());
        	                    fieldlist=factorlist.getFieldList();
    	                        strwhere=factorlist.getSqlExpression();
    	                    }
//    		        		String sql =" select "+pre+"a01.a0100 "+strwhere;
    		        		String sql ="";
    		        		if(userView.getStatus()==4)
    		        		{
    		        			this.executeSelfServiceResultData(userView, info_kind, strwhere, pre);
    		        			sql=" select obj_id from t_sys_result where "
        		        				+ " UPPER(username)='"+userView.getUserName().toUpperCase()+"'"
        		        			    +" and UPPER(nbase)='"+pre.toUpperCase()+"'";
        		        			if("1".equals(info_kind)) {
        		        				sql+=" and flag=0 ";
        		        			}else if("2".equals(info_kind)) {
        		        				sql+=" and flag=1 ";
        		        			}else if("3".equals(info_kind)) {
        		        				sql+=" and flag=2 ";
        		        			}
    		        		}
    		        		else
    		        		{
    		            		this.executeResultData(userView, info_kind, strwhere, pre);
    		            		if ("1".equals(info_kind)) { // 人员
    		            			sql=" select A0100 from "+userView.getUserName() + pre + "result";
    		        			} else if ("2".equals(info_kind)) { // 单位
    		        				sql=" select A0100 from "+ userView.getUserName() + "B" + "result";
    		        			} else if ("3".equals(info_kind)) { // 部门
    		        				sql=" select A0100 from "+ userView.getUserName() + "K" + "result";
    		        			}
    		        		}
    		    	    	sb.append(pre+"a01.a0100 in ("+sql+")");//设置查询条件，查询结果表更新数据 直接从查询结果表去对应数据
    		    	    	this.setDbpreSQL(pre, sb.toString());
    	    	    	}
    	    		}
        			else
        			{
    	    			String sql = "";
    		    		FactorList factorlist=new FactorList(expr,factor,"USR",bHistory ,blike,true,Integer.parseInt(info_kind),userView.getUserId());
                        fieldlist=factorlist.getFieldList();
                        String strwhere=factorlist.getSqlExpression();
                        StringBuffer buf = new StringBuffer("");
    		    	    if("2".equals(info_kind))
    		    		{
    		    			sql="select b01.b0110 "+strwhere;
    		    			if("true".equalsIgnoreCase(deptOnly))
    		     			{
    		     				sql = " select a.b0110 from ("+sql+") a,(select codeitemid from organization where UPPER(codesetid)='UM') b where a.b0110=b.codeitemid";
    			    		}
    			    		if("true".equalsIgnoreCase(unitOnly))
    			    		{
    			    			sql = " select a.b0110 from ("+sql+") a,(select codeitemid from organization where UPPER(codesetid)='UN') b where a.b0110=b.codeitemid";
    			    		}
    			    	}else if("3".equals(info_kind))
    			    	{
    		    			sql="select k01.e01a1 "+strwhere;
    		    		}
    		    	    if(userView.getStatus()==4)
    		    	    {
    		    	    	this.executeSelfServiceResultData(userView, info_kind, strwhere, "");
    		    	    }
    		    	    else
    		    	    {
    		    	        this.executeResultData(userView, info_kind, strwhere, "");
    		    	    }
    		    	    if("2".equals(info_kind))
    		    	    {
    		    	    	buf.append("b01.b0110 in ("+sql+")");
    		    	    	this.setB01SQL(buf.toString());
    		    	    }
    		    	    else
    		    	    {
    		    	    	buf.append("k01.e01a1 in ("+sql+")");
    		    	    	this.setK01SQL(buf.toString());
    		    	    }
    	    		}
         		}
    			else 
    			{
    				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
    				InfoUtils infoUtils=new InfoUtils();
    				if("1".equals(info_kind))
    				{
    			    	/**取所有临时变量=3是人员的=？是机构=？是职位*/
    			     	alUsedFields.addAll(infoUtils.getMidVariableList("3", "0", this.conn));
    			     	String[] arr=nbase.split(",");
    			    	ContentDAO dao = new ContentDAO(this.conn);
		            	for(int i=0;i<arr.length;i++)
		            	{
		             		String pre=arr[i];
		             		if(!"ALL".equals(dbpre) && !dbpre.equalsIgnoreCase(pre)) {
                                continue;
                            }
		            		String userTableName="";
		    	    		String tempTableName="";
		    	    		String FSQL="";
		        	    	String whereA0100In=InfoUtils.getWhereINSql(userView,pre);
					    	YksjParser yp = new YksjParser(
					    			userView//Trans交易类子类中可以直接获取userView
				    		    	,alUsedFields
				    		    	,YksjParser.forSearch
				    		    	,YksjParser.LOGIC//此处需要调用者知道该公式的数据类型
				    		    	,YksjParser.forPerson
				    		    	,"gw",pre);
					    	YearMonthCount ycm = null;	
					    	yp.setSupportVar(true);  //支持临时变量
					    	String existSql = this.getExistSql(info_kind, arr[i], tableName);
					    	String inSql = this.getInSql(info_kind, arr[i], tableName);
					    	//yp.setExistWhereText(existSql);
					    	yp.run_Where(factor, ycm,"","", dao, "",this.conn,"A", null); 
					    	FSQL=yp.getSQL();
				    		tempTableName = yp.getTempTableName();
				    		StringBuffer sql=new StringBuffer();
	                        sql.append("(select a0100 from " + tempTableName + "");
	                        if(StringUtils.isNotEmpty(yp.getSQL())) {
	                        	sql.append(" where " + yp.getSQL()+" ");
	                        }
	                        if(!userView.isSuper_admin()&& "false".equalsIgnoreCase(NO_MANAGE_PRIV)) {
	                        	if(StringUtils.isNotEmpty(yp.getSQL())) {
	                        		sql.append(" and   a0100 in(select "+pre + "a01.a0100 "+whereA0100In+") ");
	                        	}else {
	                        		sql.append(" where   a0100 in(select "+pre + "a01.a0100 "+whereA0100In+") ");
	                        	}
	                        }
	                        sql.append(")");
	                        String importCond="";
				    		if(userView.getStatus()==0)
				    		{
				        		userTableName=userView.getUserName()+pre+"Result";
		                        dao.delete("delete from "+userTableName+"", new ArrayList());
		                        dao.insert("insert into "+userTableName+"(a0100) "+sql.toString(), new ArrayList()); 
		                        importCond="select a0100 from "+userTableName;
				    		}
				    		else if(userView.getStatus()==4)
				    		{
				    			//this.executeSelfServiceResultData(userView, info_kind, sql.toString(), pre);
				    			String tabldName = "t_sys_result";
				    			Table table = new Table(tabldName);
				    			DbWizard dbWizard = new DbWizard(conn);
				    			if(dbWizard.isExistTable(table)) {
				    				String str = "delete from " + tabldName+" where flag=0 and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
				    				str+=" and UPPER(nbase)='"+pre.toUpperCase()+"'";
				    				dao.delete(str, new ArrayList());
				    				StringBuffer buf_sql = new StringBuffer("");
				    				buf_sql.append("insert into " + tabldName);
				    				buf_sql.append("(username,nbase,obj_id,flag) ");
				    				buf_sql.append("select '"+userView.getUserName()+"' as username,'"+pre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
				    				buf_sql.append(" from "+sql+" T");
				    				dao.insert(buf_sql.toString(), new ArrayList());
				    				importCond="select obj_id from " + tabldName + 
				    				        " where flag=0 and UPPER(username)='"+userView.getUserName().toUpperCase()+"' and UPPER(nbase)='"+pre.toUpperCase()+"'";
				    			}
				    		}
			    		    this.setDbpreSQL(pre, pre+"a01.a0100 in ("+importCond/*sql.toString()*/+")");
		            	}
    				}
    				else if("2".equals(info_kind))
    				{
    					
    				}
    				else if("3".equalsIgnoreCase(info_kind))
    				{
    					
    				}
    				
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public void executeResultData(UserView userView,String info_flag,String sql,String dbpre)
    {
    	try
    	{
    		String tabldName = "";
			if ("1".equals(info_flag)) { // 人员
				tabldName = userView.getUserName() + dbpre + "result";
			} else if ("2".equals(info_flag)) { // 单位
				tabldName = userView.getUserName() + "B" + "result";
			} else if ("3".equals(info_flag)) { // 部门
				tabldName = userView.getUserName() + "K" + "result";
			}
			Table table = new Table(tabldName);
			DbWizard dbWizard = new DbWizard(conn);
			if (dbWizard.isExistTable(table)) {
				dbWizard.dropTable(table);
			}
			ArrayList fieldList = getFieldList(info_flag);
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				Field temp = (Field) t.next();
				table.addField(temp);
			}
			dbWizard.createTable(table);
			String str = "delete from " + tabldName;
			ContentDAO dao = new ContentDAO(conn);
			dao.delete(str, new ArrayList());
			StringBuffer buf_sql = new StringBuffer("");
			if ("1".equals(info_flag)) {
				buf_sql.append("insert into " + tabldName);
				buf_sql.append("(A0100,b0110) ");
				buf_sql.append("select distinct "+dbpre+"A01.A0100,"+dbpre+"A01.b0110 ");//liuy 加distinct去重复数据
				buf_sql.append(sql);
			} else if ("2".equals(info_flag)) {
				buf_sql.append("insert into " + tabldName + " (B0110) ");
				buf_sql.append("select distinct B01.b0110 "+sql);//liuy 加distinct去重复数据
			} else if ("3".equals(info_flag)) {
				buf_sql.append("insert into " + tabldName+ " (E01A1)");
				buf_sql.append("select distinct K01.e01a1 "+sql);//liuy 加distinct去重复数据
			}
			dao.insert(buf_sql.toString(), new ArrayList());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 自助用户的结果表
     * @param userView
     * @param info_flag
     * @param sql
     * @param dbpre
     */
    public void executeSelfServiceResultData(UserView userView,String info_flag,String sql,String dbpre)
    {
    	try{
    		String tabldName = "t_sys_result";
			Table table = new Table(tabldName);
			DbWizard dbWizard = new DbWizard(conn);
			if (!dbWizard.isExistTable(table)) {
				return;
			}
			/**=0 人员=1 单位=2 岗位*/

			String flag="0";
			if("2".equalsIgnoreCase(info_flag))
			{
				flag="1";
			}
			else if("3".equalsIgnoreCase(info_flag))
			{
				flag="2";
			}
			String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
			if("1".equalsIgnoreCase(info_flag))
			{
				str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";
			}
			ContentDAO dao = new ContentDAO(conn);
			dao.delete(str, new ArrayList());
			StringBuffer buf_sql = new StringBuffer("");
			if ("1".equals(info_flag)) {
				buf_sql.append("insert into " + tabldName);
				buf_sql.append("(username,nbase,obj_id,flag) ");
				buf_sql.append("select distinct '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,"+dbpre+"A01.A0100 as obj_id, 0 as flag");
				buf_sql.append(sql);
			} else if ("2".equals(info_flag)) {
				buf_sql.append("insert into " + tabldName + " (username,obj_id,flag,nbase) ");
				buf_sql.append("select '"+userView.getUserName()+"' as username,B01.b0110 as obj_id,1 as flag,'B'"+sql);
			} else if ("3".equals(info_flag)) {
				buf_sql.append("insert into " + tabldName+ " (username,obj_id,flag,nbase)");
				buf_sql.append("select '"+userView.getUserName()+"' as username,K01.e01a1 as obj_id,2 as flag,'K' "+sql);
			}
			dao.insert(buf_sql.toString(), new ArrayList());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    private ArrayList getFieldList(String strInfkind)
    {
    	ArrayList list=new ArrayList();
    	Field temp=null;
		if("1".equals(strInfkind))
		{
				/*temp=new Field("dbase",ResourceFactory.getProperty("label.dbase"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(false);
			    temp.setNullable(true);
			    temp.setSortable(false);	
				temp.setLength(30);
				temp.setCodesetid("@@");
				list.add(temp);	*/	
				
				temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(true);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				temp.setLength(30);
				temp.setCodesetid("0");
				list.add(temp);
				temp=new Field("a0000",ResourceFactory.getProperty("a0000.label"));
				temp.setDatatype(DataType.INT);
				temp.setKeyable(false);
			    temp.setVisible(false);
			    temp.setNullable(true);
			    temp.setSortable(false);
			    temp.setCodesetid("0");
				list.add(temp);
				
				/*FieldItem item=DataDictionary.getFieldItem("a0101");
				temp=item.cloneField();
				temp.setVisible(true);
			    temp.setSortable(true);	
				temp.setKeyable(false);	
			    temp.setNullable(true);	
			    temp.setCodesetid("0");
				list.add(temp);		*/		    
				
				temp=new Field("b0110",ResourceFactory.getProperty("b0110.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(true);
			    temp.setNullable(true);
			    temp.setSortable(false);
			    temp.setLength(50);
			    temp.setCodesetid("UN");
				list.add(temp);	
				
				/*item=DataDictionary.getFieldItem("e0122");
				temp=item.cloneField();
				temp.setVisible(true);
			    temp.setSortable(true);	
				temp.setKeyable(false);
			    temp.setNullable(true);
			    temp.setLength(50);
			    temp.setCodesetid("UM");
				list.add(temp);	*/
				
				/*temp=new Field("e01a1",ResourceFactory.getProperty("e01a1.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(true);
			    temp.setNullable(true);
			    temp.setSortable(true);
			    temp.setLength(50);
			    temp.setCodesetid("@K");
				list.add(temp);	*/
				
				
		}
		else if("2".equals(strInfkind))
		{
			temp=new Field("b0110",ResourceFactory.getProperty("b0110.label"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(false);
		    temp.setLength(50);
		    temp.setCodesetid("UN");
			list.add(temp);			
		}
		else
		{
			temp=new Field("e01a1",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(true);
		    temp.setLength(50);
		    temp.setCodesetid("@K");
			list.add(temp);				
		}
		return list;
    }
    private HashMap midvariableMap = new HashMap();
	 private ArrayList midvariableList = new ArrayList();
	 private HashMap midvarCellMap = new HashMap();
	    public void getMidvariable(String tabid)
	    {
	    	//ArrayList list = new ArrayList();
	    	RowSet rset = null;
	    	try
	    	{
	    		StringBuffer buf=new StringBuffer();
				buf.append("select a.cname,a.chz,a.ntype,a.cvalue,a.fldlen,a.flddec,a.codesetid,b.gridno from ");
				buf.append(" midvariable a,muster_cell b ");
				/*buf.append(" where nflag=3 and (cstate is null or cstate='");
				buf.append(tabid+"')");*/
				buf.append(" where UPPER(a.cname)=UPPER(b.field_name) and UPPER(b.flag)='V'");
				buf.append(" and b.tabid='"+tabid+"'");
				buf.append(" order by sorting");
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
				while(rset.next())
				{
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("cname"));
					item.setFieldsetid(/*"A01"*/"");//没有实际含义
					item.setItemdesc(rset.getString("chz"));
					item.setItemlength(rset.getInt("fldlen"));
					item.setDecimalwidth(rset.getInt("flddec"));
					item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
					item.setCodesetid(rset.getString("codesetid")==null?"":rset.getString("codesetid"));
					switch(rset.getInt("ntype"))
					{
					case 1://
						item.setItemtype("N");
						break;
					case 2:
					case 4://代码型					
						item.setItemtype("A");
						break;
					case 3:
						item.setItemtype("D");
						break;
					}
					item.setVarible(1);
					this.midvariableMap.put(rset.getString("cname").toUpperCase(), item);
					this.midvariableList.add(item);
					this.midvarCellMap.put(rset.getString("cname").toUpperCase(), rset.getString("gridno"));
				}// while loop end.
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally{
				try
				{
					if(rset!=null) {
                        rset.close();
                    }
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	    }
	    public void transformMidvariable(String dbpre,String tabid,String tablename)
	    {
	    	try
	    	{
	    		if(this.midvariableList.size()>0)
	    		{
	    			DBMetaModel dbmodel=new DBMetaModel(this.conn);
	    			dbmodel.reloadTableModel(tablename);
	    			RecordVo vo=new RecordVo(tablename);
	    			DbWizard dbw=new DbWizard(this.conn);
	    			Table table=new Table(tablename);
	    			ContentDAO dao=new ContentDAO(this.conn);
	    			StringBuffer buf=new StringBuffer();
	    			boolean bflag=false;
	    			for(int i=0;i<this.midvariableList.size();i++)
	    			{
	    				FieldItem item=(FieldItem)midvariableList.get(i);
	    				String fieldname=item.getItemid();
	    				/**变量如果未加，则构建*/
	    				if(!vo.hasAttribute(fieldname.toLowerCase()))
	    				{
	    					Field field=item.cloneField();
	    					bflag=true;
	    					table.addField(field);
	    				}//if end.
	    			}
	    			if(bflag)
	    			{
	    				dbw.addColumns(table);
	    				dbmodel.reloadTableModel(tablename);					
	    			}
	    			String tmptable="T#"+this.userView.getUserName()+"_mus_1";
	    			for(int j=0;j<midvariableList.size();j++)
					{
						StringBuffer strFilter=new StringBuffer();
						FieldItem item=(FieldItem)midvariableList.get(j);
						String fldtype=item.getItemtype();
						String fldname=item.getItemid();
						ArrayList usedlist=initUsedFields();
						ArrayList allUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
						YksjParser yp = new YksjParser(this.userView, allUsedFields,YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
						yp.setStdTmpTable(tablename);
						yp.setTargetFieldDecimal(item.getDecimalwidth());
						appendUsedFields(midvariableList,usedlist);
						FieldItem fielditem=new FieldItem("A01","AAAAA");
						fielditem.setItemdesc("AAAAA");
						fielditem.setCodesetid(item.getCodesetid());
						fielditem.setItemtype(fldtype);
						fielditem.setItemlength(item.getItemlength());
						fielditem.setDecimalwidth(item.getDecimalwidth());
						usedlist.add(fielditem);
						
						if(createMidTable(usedlist,tmptable,"A0100"))
						{
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tmptable);
							buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
							buf.append(dbpre+"A01");
							buf.append(" where A0100 in (select A0100 from ");
							buf.append(tablename);
							buf.append(" where upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
								
								/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(tablename);
							strFilter.append(" where upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
							buf.append(")");
							dao.update(buf.toString());
						}
						yp.run(item.getFormula(),null,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
						
						buf.setLength(0);
						buf.append("where upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
						String strcond=buf.substring(6);
						String gridno=(String)this.midvarCellMap.get(fldname.toUpperCase());
						StringBuffer sql = new StringBuffer();
						sql.append("update "+tablename+" set "+tablename+".C"+gridno+"=(select ");
						if("D".equalsIgnoreCase(fldtype))
						{
							sql.append(Sql_switcher.dateToChar(tmptable+".AAAAA", "yyyy.MM.dd"));
						}
						else
						{
							sql.append(tmptable+".AAAAA");
						}
						sql.append(" from "+tmptable+" where "+tmptable+".a0100="+tablename+".a0100 and UPPER("+tablename+".nbase)='"+dbpre.toUpperCase()+"')");
						sql.append(" where exists (select null from "+tmptable+" where "+tmptable+".a0100="+tablename+".a0100 and UPPER("+tablename+".nbase)='"+dbpre.toUpperCase()+"')");
						dao.update(sql.toString());
					}
	    			if(dbw.isExistTable(tmptable,false))//用完临时表删除
	    			{
	    				dbw.dropTable(tmptable);
	    			}
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    }
	    
	    /**
		 * 初始设置使用字段列表
		 * @return
		 */
		private ArrayList initUsedFields()
		{
			ArrayList fieldlist=new ArrayList();
			/**人员排序号*/
			FieldItem fielditem=new FieldItem("A01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员编号*/
			fielditem=new FieldItem("A01","A0100");
			fielditem.setItemdesc("a0100");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(8);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**单位名称*/
			fielditem=new FieldItem("A01","B0110");
			fielditem.setItemdesc("单位名称");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**姓名*/
			fielditem=new FieldItem("A01","A0101");
			fielditem.setItemdesc("姓名");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员排序号*/
			fielditem=new FieldItem("A01","I9999");
			fielditem.setItemdesc("I9999");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**部门名称*/
			fielditem=new FieldItem("A01","E0122");
			fielditem.setItemdesc("部门");
			fielditem.setCodesetid("UM");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);		
			return fieldlist;
		}
		private int getDataType(String type)
		{
			int datatype=0;
			switch(type.charAt(0))
			{
			case 'A':  
				datatype=YksjParser.STRVALUE;
				break;
			case 'D':
				datatype=YksjParser.DATEVALUE;
				break;
			case 'N':
				datatype=YksjParser.FLOAT;
				break;
			}
			return datatype;
		}
		private void appendUsedFields(ArrayList slist,ArrayList dlist)
		{
			boolean bflag=false;
			for(int i=0;i<slist.size();i++)
			{
				FieldItem fielditem=(FieldItem)slist.get(i);
				String itemid=fielditem.getItemid();
				for(int j=0;j<dlist.size();j++)
				{
					bflag=false;
					FieldItem fielditem0=(FieldItem)dlist.get(j);
					String ditemid=fielditem0.getItemid();
					if(itemid.equalsIgnoreCase(ditemid))
					{
						bflag=true;
						break;
					}

				}//for j loop end.
				if(!bflag) {
                    dlist.add(fielditem);
                }
			}//for i loop end.
		}
		private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
		{
			boolean bflag=true;
			try
			{
				DbWizard dbw=new DbWizard(this.conn);
				if(dbw.isExistTable(tablename, false)) {
                    dbw.dropTable(tablename);
                }
				Table table=new Table(tablename);
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(i);
					Field field=fielditem.cloneField();
					if(field.getName().equalsIgnoreCase(keyfield))
					{
						field.setNullable(false);
						field.setKeyable(true);
					}
					table.addField(field);
				}//for i loop end.
				Field field=new Field("userflag","userflag");
				field.setLength(50);
				field.setDatatype(DataType.STRING);
				table.addField(field);
				dbw.createTable(table);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				bflag=false;
			}
			return bflag;
		}
	public ArrayList getMusterField(String tabid,String tableName)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			StringBuffer buf = new StringBuffer("");
			/* 标识：3775 插入花名册指标公式，“全体人数/编制总数”BS计算不出来，把“全体人数”改成指标编号“C5”就可以计算出来。 xiaoyun 2014-8-15 start */
			//buf.append("select gridno,field_hz,codeid,field_type,flag,slope,field_name,hz  from muster_cell where UPPER(flag)<>'H' and tabid="+tabid);
			buf.append("select gridno,field_hz,codeid,field_type,flag,slope,field_name,hz,extendattr from muster_cell where UPPER(flag)<>'H' and tabid="+tabid);
			/* 标识：3775 插入花名册指标公式，“全体人数/编制总数”BS计算不出来，把“全体人数”改成指标编号“C5”就可以计算出来。 xiaoyun 2014-8-15 end */
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				String fieldname=rs.getString("field_name")==null?"":rs.getString("field_name");
				int length=20;
				int dewidth=4;
				FieldItem item  = new FieldItem();
				item.setFieldsetid(tableName);
				if("D".equals(rs.getString("field_type"))) {
                    item.setItemid("D"+rs.getString("gridno"));  // 日期型字段
                } else {
                    item.setItemid("C"+rs.getString("gridno"));
                }
				item.setCodesetid(rs.getString("codeid")==null?"":rs.getString("codeid"));
				String field_hz=rs.getString("field_hz");
				if(field_hz==null||field_hz.trim().length()<=0) {
                    field_hz=rs.getString("hz");
                }
				if(field_hz!=null&&!"".equals(field_hz.trim()))
				{
					/* 标识：3775 插入花名册指标公式，“全体人数/编制总数”BS计算不出来，把“全体人数”改成指标编号“C5”就可以计算出来。 xiaoyun 2014-8-15 start */
					String extendAttr = rs.getString("extendattr")==null? "":rs.getString("extendattr");
					if(extendAttr.toUpperCase().indexOf("<NAMECOUNT>") != -1 || extendAttr.toUpperCase().indexOf("<NAMEVALUE>") != -1) {
						field_hz = rs.getString("hz");
					}
					/* 标识：3775 插入花名册指标公式，“全体人数/编制总数”BS计算不出来，把“全体人数”改成指标编号“C5”就可以计算出来。 xiaoyun 2014-8-15 end */
		    		field_hz=field_hz.replaceAll("`", "");
		    		item.setItemdesc(field_hz);
		    		item.setItemtype(rs.getString("field_type"));
		    		if(fieldname!=null&&!"".equals(fieldname))
		    		{
			    		FieldItem aitem = DataDictionary.getFieldItem(fieldname);
			    		if(aitem!=null)
			    		{
				    		item.setItemlength(aitem.getItemlength());
			    			item.setDecimalwidth(aitem.getDecimalwidth());
				    	}
				    	else
				    	{
				    		item.setItemlength(length);
				     		item.setDecimalwidth(dewidth);
				    	}
		     		}
		    		else
		    		{
		        		item.setItemlength(length);
			    		item.setDecimalwidth(dewidth);
		    		}
			    	list.add(item);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 取得调用算法分析器时，限制权限范围的sql语句，给exist用的。
	 * @param info_flag
	 * @param dbpre
	 * @param tableName
	 * @return
	 */
	public String getExistSql(String info_flag,String dbpre,String tableName)
	{
		StringBuffer sql = new StringBuffer("");
		if("2".equals(info_flag))
		{
			sql.append(" select 1 from ");
			sql.append(tableName+" where ");
			sql.append("B01.B0110="+tableName+".B0110");
		}
		else if("3".equals(info_flag))
		{
			sql.append(" select 1 from ");
			sql.append(tableName+" where ");
			sql.append("K01.E01A1="+tableName+".E01A1");
		}else if("5".equals(info_flag))  // 基准岗位
		{
			sql.append(" select 1 from ");
			sql.append(tableName+" where ");
			sql.append("H01.H0100="+tableName+".H0100");
		}else{
			sql.append(" select 1 from ");
			sql.append(tableName+" where ");
			sql.append(dbpre+"A01.a0100="+tableName+".a0100 and UPPER("+tableName+".NBASE)='"+dbpre.toUpperCase()+"'");
		}
		return sql.toString();
	}
	/**
	 * 取得调用算法分析器时，限制权限范围的sql语句，给in用的。
	 * @param info_flag
	 * @param dbpre
	 * @param tableName
	 * @return
	 */
	public String getInSql(String info_flag,String dbpre,String tableName)
	{
		StringBuffer sql = new StringBuffer("");
		if("2".equals(info_flag))
		{
			sql.append(" select B0110 from ");
			sql.append(tableName);
		}
		else if("3".equals(info_flag))
		{
			sql.append(" select E01A1 from ");
			sql.append(tableName);
		}else{
			sql.append(" select A0100 from ");
			sql.append(tableName);
			sql.append(" where UPPER(NBASE)='"+dbpre.toUpperCase()+"'");
		}
		return sql.toString();
	}
	/**
	 * 如果显示兼职人员，得到显示兼职人员的sql，因为要排序，只能union all了
	 * @param dbpre
	 * @return
	 */
	public String getShowPartJobSQL(String dbpre,String queryScope,String resulttable)
	{
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try
		{
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			 /**兼职参数*/
			 String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
			
			 String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
			 /**兼职单位字段*/
			 String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");	
			 /**兼职部门字段*/
			 String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
			 /**兼职排序字段*/
			 String order_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "order");
			 /**兼职岗位字段*/
			 String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
			 /**任免标识=0任=1免*/
			 String appoint=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
			 /**分析此定义的数据集和指标是否构库*/
			 FieldSet fieldset=null;
			 if(setid!=null&&!"".equals(setid.trim())&&!"#".equals(setid)) {
                 fieldset=DataDictionary.getFieldSetVo(setid);
             }
			 FieldItem unititem=null;
			 if(unit_field!=null&&!"".equals(unit_field.trim())&&!"#".equals(unit_field)) {
                 unititem=DataDictionary.getFieldItem(unit_field);
             }
			 FieldItem deptitem=null;
			 if(dept_field!=null&&!"".equals(dept_field.trim())&&!"#".equals(dept_field)) {
                 deptitem=DataDictionary.getFieldItem(dept_field);
             }
			 FieldItem orderitem=null;
			 if(order_field!=null&&!"".equals(order_field.trim())&&!"#".equals(order_field)) {
                 orderitem=DataDictionary.getFieldItem(order_field);
             }
			 FieldItem positem =null;
			 if(pos_field!=null&&!"".equals(pos_field.trim())&&!"#".equals(pos_field)) {
                 positem=DataDictionary.getFieldItem(pos_field);
             }
			 FieldItem appointitem = null;
			 if(appoint!=null&&!"".equals(appoint.trim())&&!"#".equals(appoint)) {
                 appointitem=DataDictionary.getFieldItem(appoint);
             }
			 if(partflag==null|| "".equalsIgnoreCase(partflag)|| "false".equalsIgnoreCase(partflag)||fieldset==null|| "0".equals(fieldset.getUseflag()))
			 {
				 if(StringUtils.isNotEmpty(resulttable)&& "1".equals(queryScope))//取查询结果或者自助用户 应返回结果集
                 {
                     return "(select AA.* from "+resulttable+"  BB left join "+dbpre+"A01"+"  AA on BB.a0100=AA.a0100 ) "+dbpre+"A01";
                 } else {
                     return dbpre+"A01";
                 }
				 
			 } 
			 ContentDAO dao = new ContentDAO(this.conn);
			 rs = dao.search("select * from "+dbpre+"A01 where 1=2 ");
			 ResultSetMetaData rsmd=rs.getMetaData();
			 StringBuffer select_part_job = new StringBuffer(" select ");
			 StringBuffer select = new StringBuffer("select ");
			 for(int i=1;i<=rsmd.getColumnCount();i++)//数据库下标从1 开始 结束需小于等于列的总和 changxy
			 {
				 String name=rsmd.getColumnName(i);
				 select.append(dbpre+"A01."+name+",");
				 if("B0110".equalsIgnoreCase(name)&&unititem!=null&& "1".equals(unititem.getUseflag()))
				 {
					 select_part_job.append(dbpre+setid+"."+unit_field+" as "+name+",");
				 }else if("e0122".equalsIgnoreCase(name)&&deptitem!=null&& "1".equals(deptitem.getUseflag()))
				 {
					 select_part_job.append(dbpre+setid+"."+dept_field+" as "+name+",");
				 }else if("e01a1".equalsIgnoreCase(name)&&positem!=null&& "1".equals(positem.getUseflag()))
				 {
					//liuy 2015-1-6 6446：武汉经济发展投资（集团）有限公司：高级花名册显示兼职人员有问题(BS) start
					 if("@K".equals(positem.getCodesetid()) || !positem.isCode()) {
                         select_part_job.append(dbpre+setid+"."+pos_field+" as "+name+",");
                     } else {
                         select_part_job.append("codeitem.codeitemdesc as "+name+",");//liuy 2015-1-6 修改高级花名册兼职
                     }
					//liuy 2015-1-6 end
				 }else if("a0000".equalsIgnoreCase(name)&&orderitem!=null&& "1".equals(orderitem.getUseflag()))
				 {
					 select_part_job.append(dbpre+setid+"."+order_field+" as "+name+",");
				 }else{
					 select_part_job.append(dbpre+"A01."+name+",");
				 }
			 }
			 select.setLength(select.length()-1);
			 select_part_job.setLength(select_part_job.length()-1);
			 sql.append("(");
			 sql.append(select.toString());
			//sql.append(" from "+dbpre+"A01 ");
			 //liuy 2015-1-7 6568：高级花名册显示兼职人员有问题(BS) start
			 //String tmppri= userView.getPrivSQLExpression(dbpre, true);
			 //sql.append(" "+tmppri);
			 //liuy 2015-4-17 8885 前台高级花名册46，只有2个人员库权限，每个库1人，取数全部人员正常，但是勾选显示兼职人，就取不出数据了，不对 begin
			 if("1".equals(queryScope)) {
                 sql.append(" from " +resulttable+ " T left join "+dbpre+"A01 on T.A0100=" + dbpre + "A01.A0100");
             } else {
				 sql.append(" from "+dbpre+"A01");
				 resulttable = dbpre+"A01";
			 }
			 //liuy 2015-4-17 end
			 //liuy 2015-1-7 end
			 sql.append(" union all ");
			 sql.append(select_part_job.toString());
			 sql.append(" from "+dbpre+setid+" left join "+dbpre+"A01 on ");
			 sql.append(dbpre+setid+".a0100="+dbpre+"A01.a0100");
			 //liuy 2015-1-6 6446：武汉经济发展投资（集团）有限公司：高级花名册显示兼职人员有问题(BS) start
			 //兼职岗位字段 为判断是否为空 30910	前台高级花名册，取数，选择显示兼职人员，报空指针
			 if(positem!=null&&positem.isCode() && !"@K".equals(positem.getCodesetid())){
				 sql.append(" left join (select * from codeitem where UPPER(codesetid)='"+positem.getCodesetid().toUpperCase()+"') codeitem on "+dbpre+setid+"."+pos_field+" = codeitem.codeitemid");
			 }
			 //liuy 2015-1-6 end
			 String app_set=dbpre.toUpperCase()+setid.toUpperCase();
			 String privsql=userView.getPrivSQLExpression(dbpre, true).toUpperCase();
			 if(privsql.substring(0, privsql.indexOf("WHERE")).indexOf(" "+dbpre+setid+" ")>-1) // 29269  中科实业集团（控股）有限公司：高级花名册取数报无法绑定由多个部分组成的标识符
             {
                 privsql=" "+privsql.substring(privsql.indexOf("WHERE"));//校验sql是否已经有dbpre+setid 若有去除
             } else {
                 privsql=privsql.substring(12);
             }
			 String mainset=dbpre.toUpperCase()+"A01".toUpperCase();
			 if(unititem!=null&& "1".equals(unititem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".B0110", app_set+"."+unit_field);
             }
			 if(deptitem!=null&& "1".equals(deptitem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".E0122", app_set+"."+dept_field);
             }
			 sql.append(privsql);
			 if(appointitem!=null&& "1".equals(appointitem.getUseflag())) {
                 sql.append(" and  "+dbpre+setid+"."+appoint+"='0' ");
             }
			//liuy 2015-1-7 6568：高级花名册显示兼职人员有问题(BS) start
			// if(StringUtils.equals("1", queryScope)){//liuy 2015-2-7 6624：高级花名册：设置了显示兼职人员，bs显示不出来兼职人员了，不对。
				 if(unititem!=null&& "1".equals(unititem.getUseflag())) {
					 if(resulttable.equalsIgnoreCase(dbpre+"A01")) {
						 sql.append("and ( exists(select 1 from "
								 + resulttable
								 + " T "
								 + "where T.A0100="+ dbpre+setid+".A0100  and "
								 + Sql_switcher.substr(dbpre + setid + "."
										 + unit_field, "1",  Sql_switcher.length(dbpre
												 + "A01.B0110")) + "=" + dbpre
								 + "A01.B0110)"); 
					 }else {
						 sql.append("and ( exists(select 1 from "
								 +" (select "+dbpre+"A01.* from "
								 + resulttable
								 + " T left join "
								 + dbpre
								 + "A01 on T.a0100 = "
								 + dbpre
								 + "A01.a0100) TT  left join "+dbpre+setid+" on TT.a0100="+dbpre+setid+".a0100  and TT.A0100="+ dbpre+setid+".A0100  where "
								 + Sql_switcher.substr(dbpre + setid + "."
										 + unit_field, "1",  Sql_switcher.length("TT.B0110")) + "=" + "TT.B0110)"); 
					 }
				 }
				 if(deptitem!=null&& "1".equals(deptitem.getUseflag())) {
					 if(resulttable.equalsIgnoreCase(dbpre+"A01")) {
						 sql.append("or exists(select 1 from "    //查询兼职人员时 如果兼职部门编号在结果集中没有会导致无法查询出兼职人员
								 + resulttable
								 + " T "
								 + " where T.A0100="+ dbpre+setid+".A0100  and "
								 + Sql_switcher.substr(dbpre + setid + "."
										 + dept_field, "1", Sql_switcher.length(dbpre
												 + "A01.E0122")) + "=" + dbpre
								 + "A01.E0122)"); 
					 }else {
						 sql.append("or exists(select 1 from "    //查询兼职人员时 如果兼职部门编号在结果集中没有会导致无法查询出兼职人员
								 +" (select "+dbpre+"A01.* from "
								 + resulttable
								 + " T left join "
								 + dbpre
								 + "A01 on T.a0100 = "
								 + dbpre
								 + "A01.a0100) TT  left join "+dbpre+setid+" on TT.a0100="+dbpre+setid+".a0100  and TT.A0100="+ dbpre+setid+".A0100  where "
								 + Sql_switcher.substr(dbpre + setid + "."
										 + unit_field, "1",  Sql_switcher.length("TT.E0122")) + "=" + "TT.E0122)"); 
					 }
				 }
			 //}
				 if(unititem!=null&& "1".equals(unititem.getUseflag())) // 29076 or exists  与之前的 and exists 一起拼接会有问题  改为 and （ exists（） or exists（））
                 {
                     sql.append(" ) ");
                 }
			 //liuy 2015-1-7 end
			 sql.append(") "+dbpre+"A01 ");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return sql.toString();
	}
	/**
	 * 转换考勤班次代码
	 * @param tableName
	 * @param tableId
	 */
	public void transformKQClass(String tableName,String tableId)
	{
		try
		{
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 当插入数据的临时表和获取数据的表相应字段有类型不一致时，将插入数据的临时表的字段更改为相应类型
	 * @param dbWizard          对数据库表进行操作的对象
	 * @param table             创建临时表的table
	 * @param sql_insert_update 插入数据临时表的相关字段字符串
	 * @param sql_select_update 获取数据表的相关字段字符串
	 * @param insertTableName   插入数据临时表表名
	 * @param selectTableName   获取数据表表名
	 * @author wangchaoqun
	 * @since 2014-11-19
	 */
	public void updateTableColumnType(DbWizard dbWizard, Table table, String sql_insert_update, String sql_select_update, String insertTableName, String selectTableName) {
		
		ContentDAO dao = new ContentDAO(this.conn);
		
		StringBuffer itn_sql_select = new StringBuffer(" select * from " + insertTableName + " where 1=2");
		StringBuffer stn_sql_select = new StringBuffer(" select * from " + selectTableName + " where 1=2");
		
		RowSet recset = null;
		
		if(sql_insert_update.startsWith(",")) {
            sql_insert_update.substring(1);
        }
		if(sql_select_update.startsWith(",")) {
            sql_select_update.substring(1);
        }
		String[] sql_ins_str = sql_insert_update.split(",");
		String[] sql_sel_str = sql_select_update.split(",");
		
		LinkedHashMap insert_dataType = new LinkedHashMap();
		LinkedHashMap select_dataType = new LinkedHashMap();
		try{
			//获取临时表相关字段名称和类型
			recset = dao.search(itn_sql_select.toString());
			ResultSetMetaData data=recset.getMetaData();
			for(int i=0; i<sql_ins_str.length; i++){    //根据顺序进行比较并加入linkedhashmap中
				for(int j=1; j<=data.getColumnCount(); j++){
					if(sql_ins_str[i].toUpperCase().indexOf((String)data.getColumnName(j).toUpperCase()) != -1){
						insert_dataType.put(data.getColumnName(j), String.valueOf(data.getColumnType(j)));
					}
				}
			}
			
			//获取数据表相关字段名称和类型
			recset = dao.search(stn_sql_select.toString());
			ResultSetMetaData data1=recset.getMetaData();
			for(int i=0; i<sql_sel_str.length; i++){
				for(int j=1; j<=data1.getColumnCount(); j++){
					if(sql_sel_str[i].toUpperCase().indexOf((String)data1.getColumnName(j).toUpperCase()) != -1){
						select_dataType.put(data1.getColumnName(j), String.valueOf(data1.getColumnType(j)));
					}
					    
				}
			}
			
			//类型进行比较，不想等则将临时表该字段类型转换为数据表字段类型
			if(insert_dataType.size() == select_dataType.size()){
				Iterator insert_iter = insert_dataType.entrySet().iterator();
				Iterator select_iter = select_dataType.entrySet().iterator();
				while (insert_iter.hasNext()) {
				    Map.Entry insert_entry = (Map.Entry) insert_iter.next();
				    Map.Entry select_entry = (Map.Entry) select_iter.next();
				    if(!insert_entry.getValue().equals(select_entry.getValue()) && 
			    		(java.sql.Types.CLOB == Integer.parseInt((String)select_entry.getValue()) || 
			    			java.sql.Types.LONGVARCHAR == Integer.parseInt((String)select_entry.getValue()) || 
			    				java.sql.Types.LONGVARBINARY == Integer.parseInt((String)select_entry.getValue()))){//判断数据类型是否相等 且 是否为备注类型
				    	//由于字段类型不一致，表不能直接更改字段类型，需先删除、后增加
				    	Table delTable = new Table(insertTableName);   //删除该字段的容器
						Table addTable = new Table(insertTableName);   //增加该字段的容器
						
				    	delTable.addField(table.getField((String)insert_entry.getKey()));
				    	dbWizard.dropColumns(delTable);   //删除字段
				    	delTable.removeField((String)insert_entry.getKey());
				    	table.removeField((String)insert_entry.getKey());
				    	
				    	FieldItem tempItem = null;
				    	int length = 20;
				    	int dewidth = 0;
				    	String columnName = (String)select_entry.getKey();
				    	if(columnName.indexOf("_1")!=-1||columnName.indexOf("_2")!=-1){
				    		columnName = columnName.substring(0, columnName.length()-2);
				    	}
				    	tempItem = DataDictionary.getFieldItem(columnName);   //获取此字段的fieldItem
				    	if(tempItem!=null){
				    		length = tempItem.getItemlength();     //长度
					    	dewidth = tempItem.getDecimalwidth();  //如为float类型、小数位数标志
				    	}
				    	
				    	Field temp = new Field((String)insert_entry.getKey(),(String)select_entry.getKey());
				    	switch(Integer.parseInt((String)select_entry.getValue()))
						{
							case java.sql.Types.BIGINT:
							case java.sql.Types.INTEGER:
								temp.setDatatype(DataType.INT);
								temp.setLength(length);
								break;
							case java.sql.Types.TIMESTAMP:
							case java.sql.Types.DATE:
							case java.sql.Types.TIME :
							    temp.setDatatype(DataType.STRING);
								temp.setFormat("yyyy.MM.dd");
								temp.setLength(length);
								break;
							case java.sql.Types.VARCHAR:
								temp.setDatatype(DataType.STRING);
								temp.setLength(length);
								break;
							case java.sql.Types.DOUBLE:
							case java.sql.Types.NUMERIC:
								temp.setDatatype(DataType.FLOAT);
								temp.setLength(length);
								temp.setDecimalDigits(dewidth);
								break;
							case java.sql.Types.CLOB:
							case java.sql.Types.LONGVARCHAR:
							case java.sql.Types.LONGVARBINARY:
							    temp.setDatatype(DataType.CLOB);
								temp.setLength(length);
								break;
						}
//				    	temp.setDatatype(Integer.parseInt((String)select_entry.getValue()));
				    	addTable.addField(temp);
				    	dbWizard.addColumns(addTable);  //增加字段
				    	addTable.removeField((String)insert_entry.getKey());
				    }
			    }
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String getSql_str() {
		return sql_str;
	}
	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}
	public String getK01SQL() {
		return k01SQL;
	}
	public void setK01SQL(String k01sql) {
		k01SQL = k01sql;
	}
	public String getB01SQL() {
		return b01SQL;
	}
	public void setB01SQL(String b01sql) {
		b01SQL = b01sql;
	}
	public String getAnalyseTableName() {
		return analyseTableName;
	}
	public void setAnalyseTableName(String analyseTableName) {
		this.analyseTableName = analyseTableName;
	}
	public String getNo_manger_priv() {
		return no_manger_priv;
	}
	public void setNo_manger_priv(String no_manger_priv) {
		this.no_manger_priv = no_manger_priv;
	}
	private String getKqconditionSQL() {
		return kqconditionSQL;
	}
	private void setKqconditionSQL(String kqconditionSQL) {
		this.kqconditionSQL = kqconditionSQL;
	}
	public String getGroupPoint2() {
		return groupPoint2;
	}
	public void setGroupPoint2(String groupPoint2) {
		this.groupPoint2 = groupPoint2;
	}
	public String getLayerid2() {
		return layerid2;
	}
	public void setLayerid2(String layerid2) {
		this.layerid2 = layerid2;
	}
	public String getIsGroupPoint2() {
		return isGroupPoint2;
	}
	public void setIsGroupPoint2(String isGroupPoint2) {
		this.isGroupPoint2 = isGroupPoint2;
	}
	public String getKqtable() {
		return kqtable;
	}
	public void setKqtable(String kqtable) {
		this.kqtable = kqtable;
	}
	public void setGroupOrgCodeSet(String groupOrgCodeSet) {
		this.groupOrgCodeSet = groupOrgCodeSet;
	}
	
	public void setGroupOrgCodeSet2(String groupOrgCodeSet2) {
		this.groupOrgCodeSet2 = groupOrgCodeSet2;
	}
    public boolean isOnlyGzSpFinished() {
        return onlyGzSpFinished;
    }
    public void setOnlyGzSpFinished(boolean onlyGzSpFinished) {
        this.onlyGzSpFinished = onlyGzSpFinished;
    }
	public HashMap getCellGridNoMap() {
		return cellGridNoMap;
	}
	public void setCellGridNoMap(HashMap cellGridNoMap) {
		this.cellGridNoMap = cellGridNoMap;
	}	
	
	
	
}
