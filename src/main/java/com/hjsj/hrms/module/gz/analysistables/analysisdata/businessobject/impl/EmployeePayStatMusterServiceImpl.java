package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.EmployeePayStatMusterService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayStatMusterServiceImpl implements EmployeePayStatMusterService {

    UserView userView = null;
    Connection conn = null;

    public EmployeePayStatMusterServiceImpl(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    @Override
    public ArrayList<ColumnsInfo> getColumnList(ArrayList<LazyDynaBean> headList, String nbases)throws GeneralException {
        ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
        ColumnsInfo column = null;
        GzAnalysisUtil util = new GzAnalysisUtil(this.conn,this.userView);
        column = util.getColumnsInfo("seq", ResourceFactory.getProperty("gz_new.gz_sort"), 50, "", "A", 0, 0);
        column.setTextAlign("right");
        column.setSortable(false);
        column.setLocked(true);
        columns.add(column);

        column = util.getColumnsInfo("b0110", ResourceFactory.getProperty("gz.columns.b0110Name"), 120, "UN", "A", 0, 0);
        //锁列
        column.setTextAlign("left");
        column.setLocked(true);
        columns.add(column);
        column.setSortable(false);
        column = util.getColumnsInfo("e0122", ResourceFactory.getProperty("hrms.e0122"), 120, "UM", "A", 0, 0);
        //锁列
        column.setTextAlign("left");
        column.setLocked(true);
        column.setDoFilterOnLoad(true);
        column.setSortable(false);
        columns.add(column);

        column = util.getColumnsInfo("a0101", ResourceFactory.getProperty("gz.columns.a0101"), 70, "", "A", 0, 0);
        //锁列
        column.setTextAlign("left");
        column.setLocked(true);
        columns.add(column);
        for (LazyDynaBean bean : headList) {
            String itemid = (String) bean.get("itemid");

            if ("b0110".equalsIgnoreCase(itemid)
                    || "e0122".equalsIgnoreCase(itemid)
                    || "a0101".equalsIgnoreCase(itemid)) {
                continue;
            }
            String itemdesc = (String) bean.get("itemdesc");
            String itemtype = (String) bean.get("itemtype");
            String codesetid = (String) bean.get("codesetid");
            //=0左对齐 =1居中 =2右对齐
            int align = Integer.valueOf(String.valueOf(bean.get("align")));
            String alignText = "";
            int nwidth = Integer.valueOf(String.valueOf(bean.get("nwidth")));
            String itemfmt = String.valueOf(bean.get("itemfmt"));
            switch (align) {
                case 0:
                    alignText = "left";
                    break;
                case 1:
                    alignText = "center";
                    break;
                case 2:
                    alignText = "right";
                    break;
                default:
                    alignText = "center";
                    break;
            }
            int decimalWidth = 0;
            if ("N".equalsIgnoreCase(itemtype)) {
                if(itemfmt!=null && itemfmt.contains(".")){
                    decimalWidth = itemfmt.split("\\.")[1].length();
                }

            }
            column = util.getColumnsInfo(itemid, itemdesc, 90, codesetid, itemtype, nwidth, decimalWidth);
            if("nbase".equals(itemid)){
                Map<String,String> dbNameMap = util.getDbNameMap(nbases);
                ArrayList<CommonData> list = new ArrayList<CommonData>();
                for(Map.Entry<String,String> entry : dbNameMap.entrySet()){
                    String pre = entry.getKey();
                    String dbname = entry.getValue();
                    CommonData cd = new CommonData();
                    cd.setDataName(dbname);
                    cd.setDataValue(pre);
                    list.add(cd);
                }
                column.setOperationData(list);
            }
            column.setTextAlign(alignText);
            columns.add(column);
        }

        return columns;
    }

    @Override
    public ArrayList<LazyDynaBean> getTableHeadlist(String rsdtlid) {
        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            //a00z0,a00z1,a00z2,a00z3,nbase指标固定。
            String sql = "select nwidth,align,itemdesc,itemid,itemfmt,sortid from reportitem where "+
                    "rsdtlid=? and lower(itemid) in ('a00z0','a00z1','a00z2','a00z3','nbase')";
            List values = new ArrayList();
            values.add(rsdtlid);
            rs = dao.search(sql,values);
            LazyDynaBean bean = null;
            while (rs.next()) {
                bean = new LazyDynaBean();
                String itemid = rs.getString("itemid");
                if (this.userView != null && "0".equals(this.userView.analyseFieldPriv(itemid)))
                    continue;
                DbWizard dbWizard = new DbWizard(this.conn);
                if(!dbWizard.isExistField("salaryhistory",itemid,false) || !dbWizard.isExistField("salaryarchive",itemid,false)){
                    continue;
                }
                bean.set("itemid", itemid);
                bean.set("itemdesc", rs.getString("itemdesc"));
                bean.set("align", rs.getString("align"));
                bean.set("nwidth", rs.getString("nwidth"));
                String itemtype = "A";
                String codesetid = "0";
                String itemfmt = "";
                if (rs.getString("itemfmt") != null)
                    itemfmt = rs.getString("itemfmt");
                if ("a00z0".equalsIgnoreCase(itemid)
                        ||"a00z2".equalsIgnoreCase(itemid))
                    bean.set("itemtype", "D");
                else
                    bean.set("itemtype", itemtype);
                bean.set("codesetid", codesetid);
                bean.set("itemfmt", itemfmt);
                list.add(bean);
            }

            sql = " select a.nwidth,a.align,a.itemdesc,a.itemid,a.itemfmt,b.itemtype,b.codesetid " +
                    "from reportitem a,fielditem b,(select distinct itemid from salaryset) s " +
                    "where lower(a.itemid)=lower(b.itemid) and lower(s.itemid)=lower(a.itemid) " +
                    "and lower(s.itemid)=lower(b.itemid) and a.rsdtlid=";
            sql += "? order by a.sortid";

            rs = dao.search(sql, values);
            String addedStr = ",a00z0,a00z1,a00z2,a00z3,nbase,";
            while (rs.next()) {
                bean = new LazyDynaBean();
                String itemid = rs.getString("itemid");
                if(addedStr.indexOf(itemid.toLowerCase())>-1)
                    continue;
                if (this.userView != null && "0".equals(this.userView.analyseFieldPriv(itemid)))
                    continue;
                DbWizard dbWizard = new DbWizard(this.conn);
                if(!dbWizard.isExistField("salaryhistory",itemid,false) || !dbWizard.isExistField("salaryarchive",itemid,false)){
                    continue;
                }
                bean.set("itemid", itemid);
                bean.set("itemdesc", rs.getString("itemdesc"));
                bean.set("align", rs.getString("align"));
                bean.set("nwidth", rs.getString("nwidth"));
                String itemtype = "A";
                String codesetid = "0";
                String itemfmt = "";
                if (rs.getString("itemfmt") != null)
                    itemfmt = rs.getString("itemfmt");
                if (rs.getString("itemtype") != null) {
                    itemtype = rs.getString("itemtype");
                    codesetid = rs.getString("codesetid");
                }
                bean.set("itemtype", itemtype);
                bean.set("codesetid", codesetid);
                bean.set("itemfmt", itemfmt);
                list.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }
    /**
     * 因方法需要的参数太多，故放入paramMap 中存放参数
     * @param paramMap 中有以下参数必传
     * nbases 人员库串 ','分隔
     * salaryids 薪资类别串 ','分隔
     * rsdtlid  项目统计表编号
     * headList 显示列
     * tatflag 统计方式 =1 按年统计，=2按区间统计
     * year  年份
     * starttime 开始时间
     * endtime 结束时间
     * limit 分页信息：每页显示多少条
     * page 分页信息：当前页
     * isShowTotal 是否显示合计行
     * condSql 查询条件
     * scope 是否包含过程中数据
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    @Override
    public ArrayList<LazyDynaBean> getDataList(Map paramMap, int totalCount) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet dataRs = null;
        RowSet totalRs = null;
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        GzAnalysisUtil gzUtil = new GzAnalysisUtil(this.conn, this.userView);
        try {

            ArrayList<LazyDynaBean> headList = (ArrayList<LazyDynaBean>)paramMap.get("headList");
            String isShowTotal = (String)paramMap.get("isShowTotal");
            int limit = (Integer)paramMap.get("limit");
            int page = (Integer)paramMap.get("page");
            String nbases = (String)paramMap.get("nbases");
            String condSql = (String)paramMap.get("condSql");


            StringBuffer commonSql = new StringBuffer();
            StringBuffer endSql = new StringBuffer();
            Map<String, String> map1 = this.getTableDataSql(paramMap);
            String viewSql = map1.get("viewSql");
            String buf = map1.get("selectSql");
            String leftjSql = map1.get("leftjSql");
            commonSql.append("select a0101,");
            if (Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2) {
                commonSql.append(" decode(grouping(b0110),1,'un_total',b0110) b0110,");
                commonSql.append(" decode(grouping(e0122),1,'um_total',e0122) e0122");
            } else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                commonSql.append(" coalesce(e0122,'um_total') e0122,coalesce(b0110,'un_total') b0110");
            }
            commonSql.append(",a0100,nbase,max(a0000) a0000");
            commonSql.append(buf.toString());
            commonSql.append(" from ( ");
            commonSql.append(viewSql + ") t");

            endSql.append("select * from (select a0101,b0110,e0122,a0100,nbase,a0000");
            String sumItems = "";
            for (int i = 0; i < headList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) headList.get(i);
                String itemtype = (String) bean.get("itemtype");
                String itemid = (String) bean.get("itemid");
                if ("a0101".equalsIgnoreCase(itemid)
                        || "b0110".equalsIgnoreCase(itemid)
                        || "e0122".equalsIgnoreCase(itemid)
                        || "nbase".equalsIgnoreCase(itemid)
                        || "a0100".equalsIgnoreCase(itemid)) {
                    continue;
                }
                if ("N".equalsIgnoreCase(itemtype)) {
                    sumItems+=",sum(" + Sql_switcher.isnull((String) bean.get("itemid"), "0") + ") as "+itemid;
                } else {
                    endSql.append(",max(" + itemid + ") " + itemid);
                }
            }
            endSql.append(sumItems);
            endSql.append(" from (");
            endSql.append("select a.*");
            String tempStr = "";
            if(leftjSql.length()>0){
                for(LazyDynaBean bean : headList){
                    String itemid = (String)bean.get("itemid");
                    if ("a0101".equalsIgnoreCase(itemid)
                            || "b0110".equalsIgnoreCase(itemid)
                            || "e0122".equalsIgnoreCase(itemid)
                            || "nbase".equalsIgnoreCase(itemid)
                            || "a0100".equalsIgnoreCase(itemid)
                            || "a0000".equalsIgnoreCase(itemid)) {
                        continue;
                    }
                    String itemtype = (String) bean.get("itemtype");
                    if (!"N".equalsIgnoreCase(itemtype)) {
                        tempStr+=",b."+itemid;
                    }
                }
                endSql.append(tempStr);
            }
            endSql.append(" from (");
            endSql.append(commonSql.toString().replaceAll("\\{tablename\\}", "salaryhistory")+ " group by b0110,e0122,a0100,nbase,a0101,a0000) a ");
            if(leftjSql.length()>0) {
                endSql.append("left join (" + leftjSql.toString().replaceAll("\\{tablename\\}", "salaryhistory") + ") b");
                endSql.append(" on a.a0100=b.a0100 and a.nbase=b.nbase");
            }
            endSql.append(" union all ");

            endSql.append("select a.*");
            if(leftjSql.length()>0){
                endSql.append(tempStr);
            }
            endSql.append(" from (");
            endSql.append(commonSql.toString().replaceAll("\\{tablename\\}", "salaryarchive")+ " group by b0110,e0122,a0100,nbase,a0101,a0000) a ");
            if(leftjSql.length()>0) {
                endSql.append("left join (" + leftjSql.toString().replaceAll("\\{tablename\\}", "salaryarchive") + ") b");
                endSql.append(" on a.a0100=b.a0100 and a.nbase=b.nbase");
            }
            endSql.append(") t");
            endSql.append(" group by b0110,e0122,a0101,a0100,nbase,a0000) ct");
            if(condSql.length()>0){
                endSql.append(" where 1=1 "+condSql);
            }
            endSql.append(" order by");
            String sortSql = "";
            if(paramMap.containsKey("sortSql")){
                sortSql = (String)paramMap.get("sortSql");
            }
            if(StringUtils.isEmpty(sortSql)){
                endSql.append(" ct.b0110,ct.e0122,ct.a0000");
            }else{
            	//不显示合计按照选择的列顺序排，如果有合计，按照单位部门，然后是选择的列排序
            	if ("1".equalsIgnoreCase(isShowTotal)) {
            		endSql.append(" ct.b0110 asc,ct.e0122 asc,"+sortSql);
            	}else {
            		endSql.append(" " + sortSql + ",ct.b0110 asc,ct.e0122 asc");
            	}
            }

            //查询合计行数据
            Map<String, LazyDynaBean> totalMap = null;
            if ("1".equalsIgnoreCase(isShowTotal)) {
                StringBuffer totalSql = new StringBuffer();
                if(sumItems.length()>0){
                    totalSql.append("select max(b0110) b0110,max(e0122) e0122");
                    totalSql.append(",max(a0101) a0101,max(a0100) a0100,max(nbase) nbase"+sumItems+" from (");
                }

                totalSql.append(commonSql.toString().replaceAll("\\{tablename\\}", "salaryhistory"));
                if (Sql_switcher.searchDbServer() == Constant.ORACEL
                        || Sql_switcher.searchDbServer() == Constant.DB2) {
                    totalSql.append(" group by rollup( b0110,e0122,a0101,a0100,nbase) ");
                } else {
                    totalSql.append(" group by b0110,e0122,a0101,a0100,nbase with rollup ");
                }
                totalSql.append(" union all ");
                totalSql.append(commonSql.toString().replaceAll("\\{tablename\\}", "salaryarchive"));
                if (Sql_switcher.searchDbServer() == Constant.ORACEL
                        || Sql_switcher.searchDbServer() == Constant.DB2) {
                    totalSql.append(" group by rollup( b0110,e0122,a0101,a0100,nbase)");
                } else {
                    totalSql.append(" group by b0110,e0122,a0101,a0100,nbase with rollup");
                }
                if(sumItems.length()>0){
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL
                            || Sql_switcher.searchDbServer() == Constant.DB2) {
                        totalSql.append(") t1 group by rollup( b0110,e0122,a0101,a0100,nbase)");
                    } else {
                        totalSql.append(") t1 group by b0110,e0122,a0101,a0100,nbase with rollup");
                    }
                }
                //======查询出部门合计
                totalMap = new HashMap<String, LazyDynaBean>();
                totalRs = dao.search(totalSql.toString());
                int bint = 0;
                while (totalRs.next()) {
                    LazyDynaBean bean = new LazyDynaBean();
                    String b0110 = totalRs.getString("b0110")==null?"":totalRs.getString("b0110");
                    String e0122 = totalRs.getString("e0122")==null?"":totalRs.getString("e0122");
                    bean.set("e0122", AdminCode.getCodeName("UM",e0122)==null?"":e0122+"`"+AdminCode.getCodeName("UM",e0122));
                    bean.set("b0110", AdminCode.getCodeName("UN",b0110)==null?"":b0110+"`"+AdminCode.getCodeName("UN",b0110));
                    if ("un_total".equalsIgnoreCase(b0110)) {
                    }
                    if (totalRs.getString("a0100") == null && totalRs.getString("a0101") != null && e0122 != null && b0110 != null) {
                    } else {
                        if (totalRs.getString("a0101") == null) {
                            bint++;
                            //部门的合计
                            if (!"um_total".equalsIgnoreCase(e0122)
                                    || bint == 1) {
                            	
                                bean.set("seq", ResourceFactory.getProperty("gz.gz_acounting.total"));
                                bean.set("a0101", "");
                                for (int i = 0; i < headList.size(); i++) {
                                    LazyDynaBean abean = headList.get(i);
                                    String itemid = (String) abean.get("itemid");
                                    itemid = itemid.toLowerCase();
                                    String itemtype = (String) abean.get("itemtype");
                                    if ("N".equalsIgnoreCase(itemtype)) {
                                        bean.set(itemid, totalRs.getString(itemid));
                                    } else {
                                        bean.set(itemid, "");
                                    }
                                }
                                String e0122_temp = totalRs.getString("e0122");
                                totalMap.put(totalRs.getString("b0110")+"~"+e0122_temp, bean);
                            //单位的总计
                            }else if (!"un_total".equalsIgnoreCase(totalRs.getString("b0110"))) {

                                bean.set("seq", ResourceFactory.getProperty("label.gz.datasum"));
                                bean.set("a0101", "");
                                for (int i = 0; i < headList.size(); i++) {
                                    LazyDynaBean abean = headList.get(i);
                                    String itemid = (String) abean.get("itemid");
                                    itemid = itemid.toLowerCase();
                                    String itemtype = (String) abean.get("itemtype");
                                    if ("N".equalsIgnoreCase(itemtype)) {
                                        bean.set(itemid, totalRs.getString(itemid));
                                    } else {
                                        bean.set(itemid, "");
                                    }
                                }
                                totalMap.put(totalRs.getString("b0110").toLowerCase(), bean);

                            }
                            if ("un_total".equalsIgnoreCase(totalRs.getString("b0110")) && bint > 1) {
                                bean.set("seq", ResourceFactory.getProperty("label.gz.datasum"));
                                for (int i = 0; i < headList.size(); i++) {
                                    LazyDynaBean abean = (LazyDynaBean) headList.get(i);
                                    String itemid = (String) abean.get("itemid");
                                    itemid = itemid.toLowerCase();
                                    String itemtype = (String) abean.get("itemtype");
                                    if ("N".equalsIgnoreCase(itemtype)) {
                                        bean.set(itemid, totalRs.getString(itemid));
                                    } else {
                                        bean.set(itemid, "");
                                    }
                                }
                                //总计
                                totalMap.put("un_total", bean);
                            }


                        } else {
                            bint = 0;
                        }
                    }
                }
                //======查询出部门合计 end
            }

            if(limit>0 && page>0){
                dataRs = dao.search(endSql.toString(),limit,page);
            }else{
                dataRs = dao.search(endSql.toString());
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            LazyDynaBean bean = null;
            String e0122 = "";
            String b0110 = "";
            int init = 0;
            int seq = 1;//序号
            if(page>0 && limit>0){
                seq = (page-1)*limit+1;
            }

            //查询人员库名称

            while (dataRs.next()) {
                bean = new LazyDynaBean();
                //需要显示合计行的时候才显示
                if ("1".equals(isShowTotal)) {
                    if (init == 0) {
                        b0110 = dataRs.getString("b0110")==null?"":dataRs.getString("b0110");
                        e0122 = dataRs.getString("e0122")==null?"":dataRs.getString("e0122");
                    }

                    if (b0110.equalsIgnoreCase(dataRs.getString("b0110"))) {
                        if (e0122.equalsIgnoreCase(dataRs.getString("e0122"))) {

                        } else {
                           String key = b0110+"~"+e0122;
                            LazyDynaBean tempBean = totalMap.get(key);
                            if (tempBean != null) {
                                dataList.add(tempBean);
                            }
                            e0122 = dataRs.getString("e0122")==null?"":dataRs.getString("e0122");
                        }
                    } else {
                        String key = b0110+"~"+e0122;
                        LazyDynaBean tempBean = totalMap.get(key);
                        if (tempBean != null) {
                            dataList.add(tempBean);
                        }
                        key = b0110.toLowerCase();
                        tempBean = totalMap.get(key);
                        if (tempBean != null) {
                            dataList.add(tempBean);
                        }
                        b0110 = dataRs.getString("b0110")==null?"":dataRs.getString("b0110");
                        e0122 = dataRs.getString("e0122")==null?"":dataRs.getString("e0122");
                    }
                }
                bean.set("a0101", dataRs.getString("a0101") == null ? "" : dataRs.getString("a0101"));
                for (int i = 0; i < headList.size(); i++) {
                    LazyDynaBean abean = (LazyDynaBean) headList.get(i);
                    String itemid = (String) abean.get("itemid");
                    itemid = itemid.toLowerCase();
                    String itemtype = (String) abean.get("itemtype");
                    String itemfmt = (String) abean.get("itemfmt");
                    String codesetid = (String) abean.get("codesetid");
                    if("a0101".equalsIgnoreCase(itemid)
                            ||"b0110".equalsIgnoreCase(itemid)
                            ||"e0122".equalsIgnoreCase(itemid)){
                        continue;
                    }
                    if ("D".equalsIgnoreCase(itemtype)) {
                        if(StringUtils.isEmpty(itemfmt)){
                            itemfmt = "yyyy.MM.dd";
                        }
                        format = new SimpleDateFormat(itemfmt);
                        bean.set(itemid, dataRs.getDate(itemid) == null ? "" : format.format(dataRs.getDate(itemid)));
                    }else if("A".equalsIgnoreCase(itemtype) && !"0".equals(codesetid)){
                        String codeId = dataRs.getString(itemid);
                        String codeDesc = AdminCode.getCodeName(codesetid,codeId);
                        bean.set(itemid,codeId+"`"+codeDesc);
                    }else {
                        bean.set(itemid, dataRs.getString(itemid) == null ? "" : dataRs.getString(itemid));
                        //导出Excel时特殊处理
                        if(limit==0 && page==0){
                            if("nbase".equalsIgnoreCase(itemid)){
                                Map<String,String> dbNameMap = gzUtil.getDbNameMap(nbases);
                                String pre = dataRs.getString(itemid);
                                String preName = dbNameMap.get(pre);
                                bean.set(itemid,preName==null?"":preName);
                            }
                        }
                    }
                }
                String bName = AdminCode.getCodeName("UN",dataRs.getString("b0110"));
                String EName = AdminCode.getCodeName("UM",dataRs.getString("e0122"));
                bean.set("b0110", dataRs.getString("b0110")+"`"+bName);
                bean.set("e0122", dataRs.getString("e0122")+"`"+EName);
                bean.set("seq", String.valueOf(seq));
                init++;
                dataList.add(bean);
                seq++;
            }
            //最后一个部门的合计,但是也是在最后一页显示
            if ("1".equalsIgnoreCase(isShowTotal) && (totalCount == -1 || (Math.ceil(totalCount/Double.valueOf(limit)) == page))) {
                LazyDynaBean tempBean = totalMap.get(b0110+"~"+e0122);
                if (tempBean != null) {
                    dataList.add(tempBean);
                }
                
                tempBean = totalMap.get(b0110.toLowerCase());
                if (tempBean != null) {
                    dataList.add(tempBean);
                }
            }

            //最后一页需要总计
            if ("1".equalsIgnoreCase(isShowTotal)) {
                LazyDynaBean un_total = totalMap.get("un_total");
                if (un_total != null) {
                    dataList.add(un_total);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(totalRs);
            PubFunc.closeDbObj(dataRs);
        }
        return dataList;
    }

    /**
     * 因方法需要的参数太多，故放入paramMap 中存放参数
     * @param paramMap 中有以下参数必传
     * nbases 人员库串 ','分隔
     * salaryids 薪资类别串 ','分隔
     * rsdtlid  项目统计表编号
     * tatflag 统计方式 =1 按年统计，=2按区间统计
     * year  年份
     * starttime 开始时间
     * endtime 结束时间
     * condSql 查询条件
     * scope 是否包含过程中数据
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    @Override
    public int getDataCount(Map paramMap) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        int count = 0;
        try {
            StringBuffer commonSql = new StringBuffer();
            StringBuffer endSql = new StringBuffer();
            String condSql = (String)paramMap.get("condSql");
            ArrayList<LazyDynaBean> headList = (ArrayList<LazyDynaBean>)paramMap.get("headList");
            Map<String, String> map1 = this.getTableDataSql(paramMap);
            String viewSql = map1.get("viewSql");
            String buf = map1.get("selectSql");
            String leftjSql = map1.get("leftjSql");
            commonSql.append("select a0101,");
            if (Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2) {
                commonSql.append(" decode(grouping(b0110),1,'un_total',b0110) b0110,");
                commonSql.append(" decode(grouping(e0122),1,'um_total',e0122) e0122");
            } else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                commonSql.append(" coalesce(e0122,'um_total') e0122,coalesce(b0110,'un_total') b0110");
            }
            commonSql.append(",a0100,nbase");
            commonSql.append(buf.toString());
            commonSql.append(" from ( ");
            commonSql.append(viewSql + ") t");

            endSql.append("select count(a0100) cnum from (select a0101,b0110,e0122,a0100,nbase");
            String sumItems = "";
            for (int i = 0; i < headList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) headList.get(i);
                String itemtype = (String) bean.get("itemtype");
                String itemid = (String) bean.get("itemid");
                if ("a0101".equalsIgnoreCase(itemid)
                        || "b0110".equalsIgnoreCase(itemid)
                        || "e0122".equalsIgnoreCase(itemid)
                        || "nbase".equalsIgnoreCase(itemid)
                        || "a0100".equalsIgnoreCase(itemid)) {
                    continue;
                }
                if ("N".equalsIgnoreCase(itemtype)) {
                    sumItems+=",sum(" + Sql_switcher.isnull((String) bean.get("itemid"), "0") + ") as "+itemid;
                } else if("M".equalsIgnoreCase(itemtype)) {//备注型特殊处理
                	endSql.append(",max(" + Sql_switcher.sqlToChar(itemid) + ") " + itemid);
                }else {
                    endSql.append(",max(" + itemid + ") " + itemid);
                }
            }
            endSql.append(sumItems);
            endSql.append(" from (");
            endSql.append("select a.*");
            String tempStr = "";
            if(leftjSql.length()>0){
                for(LazyDynaBean bean : headList){
                    String itemid = (String)bean.get("itemid");
                    if ("a0101".equalsIgnoreCase(itemid)
                            || "b0110".equalsIgnoreCase(itemid)
                            || "e0122".equalsIgnoreCase(itemid)
                            || "nbase".equalsIgnoreCase(itemid)
                            || "a0100".equalsIgnoreCase(itemid)) {
                        continue;
                    }
                    String itemtype = (String) bean.get("itemtype");
                    if (!"N".equalsIgnoreCase(itemtype)) {
                        tempStr+=",b."+itemid;
                    }
                }
                endSql.append(tempStr);
            }
            endSql.append(" from (");
            endSql.append(commonSql.toString().replaceAll("\\{tablename\\}", "salaryhistory")+ " group by b0110,e0122,a0100,nbase,a0101) a ");
            if(leftjSql.length()>0) {
                endSql.append("left join (" + leftjSql.toString().replaceAll("\\{tablename\\}", "salaryhistory") + ") b");
                endSql.append(" on a.a0100=b.a0100 and a.nbase=b.nbase");
            }
            endSql.append(" union all ");

            endSql.append("select a.*");
            if(leftjSql.length()>0){
                endSql.append(tempStr);
            }
            endSql.append(" from (");
            endSql.append(commonSql.toString().replaceAll("\\{tablename\\}", "salaryarchive")+ " group by b0110,e0122,a0100,nbase,a0101) a ");
            if(leftjSql.length()>0) {
                endSql.append("left join (" + leftjSql.toString().replaceAll("\\{tablename\\}", "salaryarchive") + ") b");
                endSql.append(" on a.a0100=b.a0100 and a.nbase=b.nbase");
            }
            endSql.append(") t");
            endSql.append(" group by b0110,e0122,a0101,a0100,nbase ) ct");
            if(condSql.length()>0){
                endSql.append(" where 1=1 "+condSql);
            }
            rs = dao.search(endSql.toString());
            if(rs.next()){
                count = rs.getInt("cnum");
            }

        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return count;
    }

    /**
     *
     *
     * @param rsdtlid   项目统计表编号
     * @param statflag  统计方式 =1 按年统计，=2按区间统计
     * @param year      年份
     * @param starttime 开始时间
     * @param endtime   结束时间
     * @return
     * @throws GeneralException
     */
    /**
     * 获得统计表数据sql <br/>
     * 因方法需要的参数太多，故放入paramMap 中存放参数
     * @param paramMap 中有以下参数必传
     * nbases 人员库串 ','分隔
     * salaryids 薪资类别串 ','分隔
     * headList 显示列
     * tatflag 统计方式 =1 按年统计，=2按区间统计
     * year  年份
     * starttime 开始时间
     * endtime 结束时间
     * scope 是否包含过程中数据
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    private Map<String, String> getTableDataSql(Map paramMap) throws GeneralException {
        Map<String, String> map = new HashMap<String, String>();
        try {

            String statflag = (String)paramMap.get("statflag");
            String year = (String)paramMap.get("year");
            String starttime = (String)paramMap.get("starttime");
            String endtime = (String)paramMap.get("endtime");
            String nbases = (String)paramMap.get("nbases");
            String salaryids = (String)paramMap.get("salaryids");
            String scope = (String)paramMap.get("scope");
            ArrayList headList = (ArrayList)paramMap.get("headList");
            String condSql = (String)paramMap.get("condSql");

            GzAnalysisUtil gzUtil = new GzAnalysisUtil(this.conn, this.userView);
            StringBuffer buf = new StringBuffer();
            /**privSql前有个and*/
            String b_units = this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
            String privSql = gzUtil.getPrivSQL("{tablename}", nbases, salaryids,"","");

            /**查非数值指标值的sql*/
            StringBuffer buf_2 = new StringBuffer();
            /**非数值指标列*/
            StringBuffer buf_1 = new StringBuffer("");
            StringBuffer view_buf = new StringBuffer();
            for (int i = 0; i < headList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) headList.get(i);
                String itemtype = (String) bean.get("itemtype");
                String itemid = (String) bean.get("itemid");
                if ("a0101".equalsIgnoreCase(itemid)
                        || "b0110".equalsIgnoreCase(itemid)
                        || "e0122".equalsIgnoreCase(itemid)
                        || "nbase".equalsIgnoreCase(itemid)
                        || "a0100".equalsIgnoreCase(itemid)) {
                    continue;
                }
                if ("N".equalsIgnoreCase(itemtype)) {
                    buf.append(", sum(" + Sql_switcher.isnull((String) bean.get("itemid"), "0") + ") as ");
                    buf.append((String) bean.get("itemid"));
                } else if("M".equalsIgnoreCase(itemtype)) {//备注型特殊处理
                	buf_1.append(",max(" + Sql_switcher.sqlToChar(itemid) + ") " + itemid);
                } else {
                    buf_1.append(",max(" + itemid + ") " + itemid);
                }
            }
            if(buf_1.length()>0) {
                buf_2.append("select a0100,nbase");
                buf_2.append(buf_1+" from {tablename} where 1=1 ");
            }
            view_buf.append(" select MAX(" + Sql_switcher.isnull("b0110", "''") + ") as b0110," + Sql_switcher.isnull("e0122", "''") + " as e0122");
            view_buf.append(buf.toString());
            view_buf.append(",a0101,nbase,a0100,a0000");
            view_buf.append(" from {tablename} where 1=1");
            /*if(condSql.length()>0){
            	view_buf.append(condSql.replace("ct", "{tablename}"));
            }*/
            //是否包含审批中数据，=1 包含
            if(!"1".equals(scope)){
                view_buf.append(" and sp_flag='06'");
                if(buf_1.length()>0){
                    buf_2.append(" and sp_flag='06'");
                }
            }
            if (StringUtils.isNotBlank(privSql)) {
                view_buf.append(privSql);
                if(buf_1.length()>0) {
                    buf_2.append(privSql);
                }
            }
            if ("1".equals(statflag)) {
                if(buf_1.length()>0){
                    buf_2.append(" and ");
                    buf_2.append(Sql_switcher.year("a00z0"));
                    buf_2.append("=");
                    buf_2.append(year);
                }

                view_buf.append(" and ");
                view_buf.append(Sql_switcher.year("a00z0"));
                view_buf.append("=");
                view_buf.append(year);
            } else {
                String newStarttime = "";
                if(StringUtils.isNotEmpty(starttime)){
                    newStarttime = changeFormat(starttime, ".");
                }
                String newEndtime = "";
                if(StringUtils.isNotEmpty(endtime)){
                    newEndtime = changeFormat(endtime, ".");
                }
                PositionStatBo psb = new PositionStatBo(this.conn);

                if(buf_1.length()>0){
                    if(StringUtils.isNotEmpty(newStarttime)) {
                        buf_2.append(" and ");
                        buf_2.append(psb.getDateSql(">=", "a00z0", newStarttime));
                    }
                    if(StringUtils.isNotEmpty(newEndtime)){
                        buf_2.append(" and ");
                        buf_2.append(psb.getDateSql("<=", "a00z0", newEndtime));
                    }

                }
                if(StringUtils.isNotEmpty(newStarttime)) {
                    view_buf.append(" and ");
                    view_buf.append(psb.getDateSql(">=", "a00z0", newStarttime));
                }
                if(StringUtils.isNotEmpty(newEndtime)){
                    view_buf.append(" and ");
                    view_buf.append(psb.getDateSql("<=", "a00z0", newEndtime));
                }
            }
            if(buf_1.length()>0){
                buf_2.append(" group by a0100,nbase");
            }
            view_buf.append(" group by a0100,a0000,nbase,a0101,e0122 ");
            map.put("viewSql", view_buf.toString());
            map.put("leftjSql", buf_2.toString());
            map.put("selectSql", buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }

    private String changeFormat(String value, String sep) {
        String str = "";
        try {
            if (value == null || "".equals(value))
                return str;
            String[] temp = value.split("-");
            if (temp.length == 3)
                str = temp[0] + sep + temp[1] + sep + temp[2];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
