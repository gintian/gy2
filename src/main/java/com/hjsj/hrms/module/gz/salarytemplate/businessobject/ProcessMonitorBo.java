package com.hjsj.hrms.module.gz.salarytemplate.businessobject;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalaryUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

/**
 * 薪资流程监控 工具类
 *
 * @author sunjian
 * @createtime 2018-08-23
 */
public class ProcessMonitorBo {

    // 基本属性
    private Connection conn = null;
    private UserView userview = null;
    private String gz_module = null;
    private String salaryid = null;

    /**
     * 构造函数
     *
     * @param conn
     * @param userview
     */
    public ProcessMonitorBo(Connection conn, UserView userview, String gz_module, String salaryid) {
        this.conn = conn;
        this.userview = userview;
        this.gz_module = gz_module;
        this.salaryid = salaryid;
    }

    /**
     * 获取列头、表格渲染
     *
     * @param viewtype 页面区分 0:薪资发放  1:审批  2:上报
     * @param imodule  0:薪资  1:保险
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnList(ArrayList fieldlist, boolean isShare, ArrayList<String> gzReportingDataList) {

        /** 获取类型名称 */
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();

        /** 显示 */
        if (gzReportingDataList.size() > 0) {//如果没有设置应用机构则不显示
            // 机构名称
            ColumnsInfo b0110 = getColumnsInfo("b0110", ResourceFactory.getProperty("org.orginfo.organname"), 120, 0);
            b0110.setCodesetId("UN");
            columnTmp.add(b0110);
        }

        //填报人
        ColumnsInfo fullname = getColumnsInfo("fullname", ResourceFactory.getProperty("label.gz.submitPerson"), 100, 0);
        columnTmp.add(fullname);

        //人数
        ColumnsInfo count = getColumnsInfo("count", ResourceFactory.getProperty("menu.gz.personnum"), 100, 0);
        count.setTextAlign("right");
        columnTmp.add(count);

        //审批状态
        ColumnsInfo sp_flag = getColumnsInfo("sp_flag", ResourceFactory.getProperty("menu.gz.state"), 100, 0);
        sp_flag.setCodesetId("23");
        sp_flag.setTextAlign("center");
        columnTmp.add(sp_flag);

        //当前审批人
        ColumnsInfo curr_userfullname = getColumnsInfo("curr_user_fullname", ResourceFactory.getProperty("rsbd.task.curremp"), 100, 0);
        curr_userfullname.setTextAlign("center");
        columnTmp.add(curr_userfullname);

        String hiddenStr = ",a0000,a00z0,a0100,add_flag,sp_flag,sp_flag2,a00z1,a00z3,";//默认隐藏的字段（栏目设置可以设置成显示）

        ColumnsInfo numField = null;
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem item = (FieldItem) fieldlist.get(i);
            if ("N".equals(item.getItemtype()) && !hiddenStr.contains("," + item.getItemid().toLowerCase() + ",")) {
                //状态
                numField = getColumnsInfo(item.getItemid().toLowerCase(), item.getItemdesc(), 100, item.getDecimalwidth());
                numField.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
                numField.setColumnType("N");
                numField.setTextAlign("right");
                numField.setDefaultValue("0");
                columnTmp.add(numField);
            }
        }

        //操作
        ColumnsInfo operate = getColumnsInfo("operate", ResourceFactory.getProperty("column.operation"), 100, 0);
        operate.setRendererFunc("salarymonitor_me.operate");
        columnTmp.add(operate);
        
        ColumnsInfo username = getColumnsInfo("username", "", 100, 0);
        username.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnTmp.add(username);
        
        ColumnsInfo sp_flag_code = getColumnsInfo("sp_flag_code", "", 100, 0);
        sp_flag_code.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnTmp.add(sp_flag_code);
        
        ColumnsInfo curr_user = getColumnsInfo("curr_user", "", 100, 0);
        curr_user.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnTmp.add(curr_user);
        return columnTmp;
    }

    /**
     * 设置各列的属性
     *
     * @param columnId
     * @param columnDesc
     * @param columnWidth：显示列宽
     * @param decimalWidth：小数位
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
                                       int columnWidth, int decimalWidth) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setColumnType("M");// 类型N|M|A|D
        columnsInfo.setColumnLength(100);// 显示长度
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        columnsInfo.setReadOnly(true);// 是否只读
        columnsInfo.setLocked(false);// 是否锁列

        return columnsInfo;
    }

    /**
     * 获取数据
     * 规则：以发起人的角度是看审批状态，
     * 1.如果发起人有起草就是起草状态，不管其他任何状态
     * 2.如果发起人没有起草，有驳回，无论他还有报批，已批等，都是驳回
     * 依次类推 起草>驳回>已报批>已批>结束
     * 发送邮件的规则：起草和驳回发送给发起人，报批发给审批人，可以发送多个审批人
     * 
     * @param isShare
     * @return
     */
    public ArrayList<LazyDynaBean> getDataList(String manager, boolean isShare, String a00z2, ArrayList fieldlist, ArrayList<String> gzReportingDataList, String curr_stateOfWrite, SalaryTemplateBo gzbo,String hiddenStr) {
        ArrayList<LazyDynaBean> lazyDynaBeanList = new ArrayList<LazyDynaBean>();//最终数据的集合
        ArrayList<String> listNum = new ArrayList<String>();//所有数值型指标集合
        StringBuffer numberItem = new StringBuffer();//所有的数值型指标组成的sql
        
        ApprovalSituationBo approvalSituationBo = new ApprovalSituationBo(this.conn, this.userview, salaryid);
        try {
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem item = (FieldItem) fieldlist.get(i);
                if ("N".equals(item.getItemtype()) && !hiddenStr.contains("," + item.getItemid().toLowerCase() + ",")) {
                    numberItem.append(",sum(" + item.getItemid() + ") as " + item.getItemid());
                    listNum.add(item.getItemid());
                }
            }
            //没有设置发薪机构
            if (gzReportingDataList.size() == 0) {//无论共享还是不共享没有设置发薪机构,显示所有已报填报人的数据审批情况
            	lazyDynaBeanList.addAll(getNotHaveOrg(numberItem.toString(), a00z2, listNum, curr_stateOfWrite));
            }else {
	            //设置了发薪机构
	            if (isShare) {//共享
	            	//先把临时表查出来,临时表中的所有起草和驳回的，再把历史表或者归档表查出来，这样，组成的数据就是全部数据
	                String table = manager + "_salary_" + this.salaryid;
	                DbWizard dbWizard = new DbWizard(this.conn);
	                ArrayList<LazyDynaBean> beanListTemp = new ArrayList<LazyDynaBean>();
	                if (dbWizard.isExistTable(table, false)) {// 判断表是否存在
	                	beanListTemp = getHaveOrg(gzReportingDataList, numberItem.toString(), a00z2, listNum, table, isShare, curr_stateOfWrite, gzbo, approvalSituationBo);  
	                }
	                ArrayList<LazyDynaBean> beanList = getHaveOrg(gzReportingDataList, numberItem.toString(), a00z2, listNum, "salaryhistory", isShare, curr_stateOfWrite, gzbo, approvalSituationBo);
	                ArrayList<LazyDynaBean> beanListTemps = (ArrayList<LazyDynaBean>) beanListTemp.clone();
	                lazyDynaBeanList.addAll(sumTempHistory(beanListTemp, beanListTemps, beanList, listNum));
	            } else {//非共享
	            	//先把临时表查出来,临时表中的所有起草和驳回的，再把历史表或者归档表查出来，这样，组成的数据就是全部数据
	            	ArrayList<LazyDynaBean> beanListTemp = new ArrayList<LazyDynaBean>();
	            	ArrayList<String> duplicate = new ArrayList<String>();
	            	for(int i = 0; i < gzReportingDataList.size(); i++) {
	            		String gzReportingData = gzReportingDataList.get(i);
	            		String username = gzReportingData.substring((gzReportingData.indexOf("|",(gzReportingData.indexOf("|")+1)) + 1), gzReportingData.length());
	            		if(duplicate.contains(username))//可能有重复的
	            			continue;
	            		duplicate.add(username);
	            		String table = username + "_salary_" + this.salaryid;
	            		DbWizard dbWizard = new DbWizard(this.conn);
	                    if (dbWizard.isExistTable(table, false)) {// 判断表是否存在
	                    	beanListTemp.addAll(getHaveOrg(gzReportingDataList, numberItem.toString(), a00z2, listNum, table, isShare, curr_stateOfWrite, gzbo, approvalSituationBo));
	                    }
	            	}
	            	ArrayList<LazyDynaBean> beanList = getHaveOrg(gzReportingDataList, numberItem.toString(), a00z2, listNum, "salaryhistory", isShare, curr_stateOfWrite, gzbo, approvalSituationBo);
	                ArrayList<LazyDynaBean> beanListTemps = (ArrayList<LazyDynaBean>) beanListTemp.clone();
	                lazyDynaBeanList.addAll(sumTempHistory(beanListTemp, beanListTemps, beanList, listNum));
	            }
            }
            if(lazyDynaBeanList.size() > 0) {
            //由于传的是list，表格控件无法算合计，这里最后算一下合计
            	LazyDynaBean bean = sumDataList(gzbo, lazyDynaBeanList, listNum, a00z2, gzReportingDataList.size());
            	if(bean != null) {
            		lazyDynaBeanList.add(bean);
            	}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lazyDynaBeanList;
    }
    
    /**
     * //为了在临时表是驳回并且历史表还有报批的，需要显示出审批人，两张表都需要查，先全部查出来，最后改变历史表中的count和sp_flag
     * @param beanListTemp
     * @param beanListTemps
     * @param beanList
     * @param listNum
     * @return
     */
    private ArrayList<LazyDynaBean> sumTempHistory(ArrayList<LazyDynaBean> beanListTemp,ArrayList<LazyDynaBean> beanListTemps,ArrayList<LazyDynaBean> beanList,ArrayList<String> listNum) {
    	ArrayList<LazyDynaBean> lazyDynaBeanList = new ArrayList<LazyDynaBean>();
    	//为了在临时表是驳回并且历史表还有报批的，需要显示出审批人，两张表都需要查，先全部查出来，最后改变历史表中的count和sp_flag
        for(int i = 0; i < beanListTemp.size(); i++) {
        	String sp_flag = (String)beanListTemp.get(i).get("sp_flag");
        	String sp_flag_code = (String)beanListTemp.get(i).get("sp_flag_code");
        	String tempB0110 = (String)beanListTemp.get(i).get("b0110");
        	int tempCount = (Integer)beanListTemp.get(i).get("count");
        	String username = (String)beanListTemp.get(i).get("username");//用户名，username
        	String curr_user_old = (String)beanListTemp.get(i).get("curr_user");//临时表，审批人
        	String curr_user_fullname_old = (String)beanListTemp.get(i).get("curr_user_fullname");//临时表，审批人全称
        	for(int j = 0; j < beanList.size(); j++) {
        		String user = (String)beanList.get(j).get("username");
        		String b0110 = (String)beanList.get(j).get("b0110");
        		int count = (Integer)beanList.get(j).get("count");
        		if(username.equalsIgnoreCase(user) && tempB0110.equalsIgnoreCase(b0110)) {//如果在历史表有，并且在临时表中有，就只有临时表是报批，或这驳回
        			String curr_user = (String)beanList.get(i).get("curr_user");//历史表，审批人
                	String curr_user_fullname = (String)beanList.get(i).get("curr_user_fullname");//历史表，审批人全称
                	if(StringUtils.isNotBlank(curr_user_old)) {//如果部分驳回，审批人需要合并
                		beanList.get(j).set("curr_user", getUserContact(curr_user_old,curr_user));
                		beanList.get(j).set("curr_user_fullname",getUserContact(curr_user_fullname_old,curr_user_fullname));
                	}
        			beanList.get(j).set("count", count+tempCount);
        			beanList.get(j).set("sp_flag", sp_flag);
        			beanList.get(j).set("sp_flag_code", sp_flag_code);
        			for(int k = 0; k < listNum.size(); k++) {//相对应的临时表和历史表数据之和也得加
        				String listNum_item = listNum.get(k).toLowerCase();
        				String valueTemp = beanListTemp.get(i).get(listNum_item)==null?"":(String)beanListTemp.get(i).get(listNum_item);
        				String value = beanList.get(j).get(listNum_item)==null?"":(String)beanList.get(j).get(listNum_item);
        				BigDecimal decimal = null;//临时表的
        				if(StringUtils.isNotBlank(valueTemp)) {
        					decimal=new BigDecimal(valueTemp);
        				}
        				int decimalWith = DataDictionary.getFieldItem(listNum_item).getDecimalwidth();//小数位
        				if(StringUtils.isNotBlank(value)) {
        					BigDecimal decimalHis=new BigDecimal(value);
        					decimalHis = decimal == null?decimalHis:decimalHis.add(decimal);
        					double valueHistoryD = decimalHis.setScale(decimalWith,BigDecimal.ROUND_HALF_UP).doubleValue();
        					beanList.get(j).set(listNum_item, String.valueOf(valueHistoryD));
        				}else {
        					double decimalTempD = decimal == null?0.00:decimal.setScale(decimalWith,BigDecimal.ROUND_HALF_UP).doubleValue();
        					beanList.get(j).set(listNum_item, String.valueOf(decimalTempD));
        				}
        			}
        			beanListTemps.remove(beanListTemp.get(i));
        		}
        	}
        }
        
        if(beanListTemps.size() > 0)
        	lazyDynaBeanList.addAll(beanListTemps);
        
        if (beanList.size() > 0)
            lazyDynaBeanList.addAll(beanList);
        return lazyDynaBeanList;
    }
    /**
     * 获取gz_reporting_log表中的记录，主要获取b0110，fullname
     * flag:1.返回显示的org_id|fullname  2.返回显示的org_id
     */
    public ArrayList<String> getGzReportingData(String flag) {
        ArrayList<String> gzReportingData = new ArrayList<String>();//格式:b0110|username
        Element element = null;
        try {
            SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn, Integer.parseInt(salaryid), this.userview);
            List elementList = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCY, SalaryCtrlParamBo.FILLING_AGENCYS);
            for (int i = 0; i < elementList.size(); i++) {
                element = (Element) elementList.get(i);
                String org_id = StringUtils.isBlank(element.getAttributeValue("org_id")) ? "" : element.getAttributeValue("org_id");
                String fullname = StringUtils.isBlank(element.getAttributeValue("fullname")) ? "" : element.getAttributeValue("fullname");
                String username = StringUtils.isBlank(element.getAttributeValue("username")) ? "" : element.getAttributeValue("username");
                String enable = StringUtils.isBlank(element.getAttributeValue("enable")) ? "" : element.getAttributeValue("enable");
                if ("1".equals(flag)) {
                    gzReportingData.add(org_id + "|" + fullname + "|" + username);
                } else if ("2".equals(flag)) {
                    gzReportingData.add(org_id);
                } else if ("3".equals(flag)) {
                    gzReportingData.add(enable + "|" + username);
                } else if ("4".equals(flag)) {
                    gzReportingData.add(username);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gzReportingData;
    }


    /**
     * 没有设置应用机构的数据
     * @param numberItem 数值求和sql
     * @param a00z2 发放日期
     * @param listNum 数值求和项
     * @param curr_stateOfWrite 状态筛选
     * @return
     * @author ZhangHua
     * @date 16:02 2019/5/17
     */
    public ArrayList<LazyDynaBean> getNotHaveOrg(String numberItem, String a00z2, ArrayList<String> listNum, String curr_stateOfWrite) {
        ArrayList<LazyDynaBean> lazyDynaBeanList = new ArrayList<LazyDynaBean>();
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> salaryUser = new ArrayList<String>();
        ContentDAO dao = new ContentDAO(this.conn);
        LazyDynaBean bean_old = null;
        LazyDynaBean bean = new LazyDynaBean();
        StringBuffer sql = new StringBuffer();
        RowSet rs = null;
        try {
            StringBuffer itemStr = new StringBuffer();
            for (String str : listNum) {
                itemStr.append(str + ",");
            }

            if (StringUtils.isBlank(a00z2)) {
                sql.append("select " + Sql_switcher.dateToChar("max(a00z2)") + " as a00z2 from gz_extend_log where salaryid=?");
                rs = dao.search(sql.toString(), Arrays.asList(new Object[]{Integer.valueOf(this.salaryid)}));
                if (rs.next()) {
                    a00z2 = rs.getString("a00z2");
                }
                sql.setLength(0);
            }

            sql.append("select username from gz_extend_log where sp_flag<>'06' and salaryid=? and a00z2=" + Sql_switcher.dateValue(a00z2));
            sql.append(" group by username ");
            list.add(this.salaryid);
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                salaryUser.add(rs.getString("username"));
            }
            sql.setLength(0);
            list.clear();

            StringBuffer sqlWhere = new StringBuffer();
            if (StringUtils.isNotBlank(a00z2)) {
                sqlWhere.append("and a00z2 =" + Sql_switcher.dateValue(a00z2));
            }
            if (StringUtils.isNotBlank(curr_stateOfWrite) && !"all".equalsIgnoreCase(curr_stateOfWrite)) {
                sqlWhere.append("and sp_flag = ? ");
            }

            //union all 历史表
            sql.append("select max(a00z2) as a00z2,count(*) as count,sumtable.userflag,curr_user,max(sp_flag) as sp_flag_max,min(sp_flag) as sp_flag_min,");
            sql.append(" max(operuser.fullname) as fullname ");
            sql.append(numberItem);
            sql.append(" from ( ");

            sql.append(" select a00z2,userflag,curr_user, ");
            sql.append(itemStr);
            //历史表的驳回状态，对于发起人是已报批，只有驳回到了发起人才是真正的驳回
            sql.append(" (case when sp_flag = '07' then '02' else sp_flag end) as sp_flag from salaryhistory where salaryid=?  ");
            sql.append(sqlWhere);
            list.add(this.salaryid);
            if (StringUtils.isNotBlank(curr_stateOfWrite) && !"all".equalsIgnoreCase(curr_stateOfWrite)) {
                list.add(curr_stateOfWrite);
            }

            //union all 归档表
            sql.append(" union all ");
            sql.append("select a00z2,userflag,curr_user,");
            sql.append(itemStr);
            //归档表的驳回状态，对于发起人是已报批，只有驳回到了发起人才是真正的驳回
            sql.append(" (case when sp_flag = '07' then '02' else sp_flag end) as sp_flag from salaryarchive where salaryid=? ");
            sql.append(sqlWhere);
            list.add(this.salaryid);
            if (StringUtils.isNotBlank(curr_stateOfWrite) && !"all".equalsIgnoreCase(curr_stateOfWrite)) {
                list.add(curr_stateOfWrite);
            }

            //union all 临时表
            for (String user : salaryUser) {
                String tableName = user + "_salary_" + this.salaryid;
                DbWizard dbWizard = new DbWizard(this.conn);
                if (dbWizard.isExistTable(tableName, false)) {// 判断表是否存在
	                sql.append(" union all ");
	                sql.append("select a00z2,'" + user + "' as userflag,'' as curr_user,");
	                sql.append(itemStr);
	                sql.append(" sp_flag from " + tableName + " where  (sp_flag='01' or sp_flag='07') ");
	                sql.append(sqlWhere);
	                if (StringUtils.isNotBlank(curr_stateOfWrite) && !"all".equalsIgnoreCase(curr_stateOfWrite)) {
	                    list.add(curr_stateOfWrite);
	                }
                }
            }

            sql.append(") sumtable ");
            sql.append(" left join operuser on upper(sumtable.userflag) =operuser.username ");
            sql.append(" group by sumtable.userflag,curr_user ");
            sql.append(" order by sumtable.userflag ");

            rs = dao.search(sql.toString(), list);
            String sp_flag = "";
            String old_userflag = "";
            RecordVo vo = new RecordVo("operuser");
            while (rs.next()) {
            	StringBuffer curr_user_fullname = new StringBuffer();
            	bean = new LazyDynaBean();
                String userflag = rs.getString("userflag");
                String curr = StringUtils.isBlank(rs.getString("curr_user")) ? "" : rs.getString("curr_user");
                String sp_flag_min = rs.getString("sp_flag_min");
                String sp_flag_max = rs.getString("sp_flag_max");
                //找出对应的审批人
                if (StringUtils.isNotBlank(curr)) {
                    vo.removeValues();
                    vo.setString("username", curr);
                    String currname = dao.findByPrimaryKey(vo).getString("fullname");
                    curr_user_fullname.append((StringUtils.isNotBlank(currname) ? currname : curr));
                }
                
                //当前审批人
                bean.set("curr_user", curr);
                //当前审批人全称
                bean.set("curr_user_fullname", curr_user_fullname.toString());
                
                if ("07".equals(sp_flag_max)) {
                    if ("01".equals(sp_flag_min)) {
                        sp_flag = "01";
                    } else {
                        sp_flag = "07";
                    }
                } else {
                    sp_flag = sp_flag_min;
                }
                userflag = rs.getString("userflag");
                bean.set("fullname", StringUtils.isBlank(rs.getString("fullname")) ? userflag : rs.getString("fullname"));
                bean.set("username", userflag);
                bean.set("a00z2", PubFunc.FormatDate(rs.getDate("a00z2"), "yyyy-MM-dd"));
                bean.set("count", rs.getInt("count"));
                bean.set("sp_flag_code", sp_flag);
                bean.set("sp_flag", AdminCode.getCodeName("23", sp_flag));
                for (int i = 0; i < listNum.size(); i++) {
                    bean.set(listNum.get(i).toLowerCase(), rs.getString(listNum.get(i)) == null ? "0" : rs.getString(listNum.get(i)));
                }
                
                //如果每个人有多条记录，有报批的，和已批的这种
                if(old_userflag.equals(userflag) || bean_old == null) {
                	bean_old = megerData(bean_old, bean, listNum, "add");
                }else if(bean_old != null){
                	lazyDynaBeanList.add(bean_old);
                	bean_old = bean;
                }
                old_userflag = userflag;
            }
            
            if(bean_old != null) {
            	lazyDynaBeanList.add(bean_old);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return lazyDynaBeanList;
    }

    /**
     * 设置了应用机构，这里可以将临时表表和salaryhistory分开，这样会代码更容易看懂，以后可优化
     *
     * @param gzReportingDataList
     * @param numberItem
     * @param a00z2
     * @param listNum
     * @return
     */
    private ArrayList<LazyDynaBean> getHaveOrg(ArrayList<String> gzReportingDataList, String numberItem, String a00z2, ArrayList<String> listNum, String table, boolean isShare, String curr_stateOfWrite, SalaryTemplateBo gzbo, ApprovalSituationBo approvalSituationBo) {
        ArrayList<String> filterAgencyList = getGzReportingData("2");
        ArrayList<LazyDynaBean> lazyDynaBeanList = new ArrayList<LazyDynaBean>();
        ArrayList<String> list = new ArrayList<String>();
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();//sql语句
        LazyDynaBean bean_old = null;
        RowSet rs = null;
        try {
            for (String gzReportingData : gzReportingDataList) {
                String b0110 = gzReportingData.substring(2, gzReportingData.indexOf("|"));
                String fullname = gzReportingData.substring((gzReportingData.indexOf("|") + 1), gzReportingData.indexOf("|",(gzReportingData.indexOf("|")+1)));
                String username = gzReportingData.substring((gzReportingData.indexOf("|",(gzReportingData.indexOf("|")+1)) + 1), gzReportingData.length());
                // 不是共享账套，并且是临时表，判断临时表的人是否是下发机构对应的人员，如果是继续查
                if(!isShare && table.contains("_salary_")) {
                	String usernameTable = table.split("_salary_")[0];
                	if(!usernameTable.equalsIgnoreCase(username))
                		continue;
                }
                sql = new StringBuffer();
                list.clear();
                sql.append("select max(a00z2) as a00z2,count(*) as count,max(a00z3) as a00z3," + table + ".userflag,max(sp_flag) as sp_flag1");
                if (isShare && table.contains("_salary_")) {//如果是共享的，并且是临时表，查sp_flag2
                	//因为可能是数据上报sp_flag2已报批，但是sp_flag是起草，需要区别开，sp_flag是起草或者是驳回的时候找报审状态sp_flag2，根据
                	sql.append(",max(sp_flag2) as sp_flag,min(sp_flag2) as sp_flag_min");
                } else {
                    sql.append(",max(sp_flag) as sp_flag,min(sp_flag) as sp_flag_min");
                }
                sql.append(numberItem);
                if (!table.contains("_salary_")) {
                    sql.append(",max(appuser) as appuser,max(" + table + ".curr_user) as curr_user,case when nullif(max(operuser.fullname),'')  is not null  then max(operuser.fullname) else max(" + table + ".curr_user) end  as currusername  "
                            + "from " + table + " left join operuser on " + table + ".curr_user=operuser.username where salaryid=? and ");
                    list.add(this.salaryid);
                } else {
                    sql.append(" from " + table + " where 1=1 and (sp_flag='01' or sp_flag='07') and ");//临时表只需要查起草和驳回的，其他从历史表查
                }
                sql.append(" (b0110 like ? or e0122 like ?) ");
                list.add(b0110 + "%");
                list.add(b0110 + "%");
                if (StringUtils.isNotBlank(a00z2)) {
                    sql.append("and a00z2 =" + Sql_switcher.dateValue(a00z2));
                } else {//找到最大的发放日期
                    sql.append("and a00z2 = (select max(a00z2) from " + table + " where 1=1 ");
                    if ("salaryhistory".equalsIgnoreCase(table)) {
                        sql.append(" and salaryid = ?) ");
                        list.add(this.salaryid);
                    } else {
                        sql.append(") ");
                    }
                }
                //必须是共享的才有sp_flag2,如果sp_flag是1的时候按照sp_flag2判断
                if (StringUtils.isNotBlank(curr_stateOfWrite) && !"all".equalsIgnoreCase(curr_stateOfWrite)) {
                    if (isShare && table.contains("_salary_")) {
                    	sql.append("and sp_flag2 = ? ");
                        list.add(curr_stateOfWrite);
                    } else {
                        sql.append("and sp_flag = ? ");
                        list.add(curr_stateOfWrite);
                    }
                }
                String util = approvalSituationBo.buildstrSql(table, filterAgencyList, gzbo.getCtrlparam());
                //根据归属单位进行限制
                if (!"1=2".equals(util.trim()) && table.contains("_salary_")) {
                    sql.append("and (" + util + ") ");
                }
                sql.append("group by a00z2," + table + ".UserFlag");
                if ("salaryhistory".equals(table))
                    sql.append(" ,curr_user order by a00z2 desc");
                else
                    sql.append(" order by a00z2 desc");

                rs = dao.search(sql.toString(), list);
                
                while (rs.next()) {
                	LazyDynaBean bean = new LazyDynaBean();
                    String sp_flag_min = StringUtils.isNotBlank(rs.getString("sp_flag_min")) ? rs.getString("sp_flag_min") : "01";//最小的sp_flag
                    String sp_flag_max = StringUtils.isNotBlank(rs.getString("sp_flag")) ? rs.getString("sp_flag") : "01";//最大的sp_flag
                    String sp_flag_finally = sp_flag_min;//按照规则应该显示的审批状态 （起草>驳回>已报批>已批>结束）
                    if (!"01".equals(sp_flag_min) && "07".equals(sp_flag_max)) {//对于有驳回的用驳回
                    	if (!table.contains("_salary_")) {//薪资历史表
                    		String appuser = StringUtils.isBlank(rs.getString("appuser")) ? "" : rs.getString("appuser");
                    		if(StringUtils.isNotBlank(appuser)) {//凡是没有驳回到发起人都是已报批
                    			sp_flag_finally = "02";
                    		}else {//如果是空的则说明驳回到发起人了
                    			String[] arg=appuser.split(";");
                    			if(arg.length<2){
                    				sp_flag_finally = sp_flag_max;
                    			}else {
                    				sp_flag_finally = "02";
                    			}
                    		}
                    	}else {
                    		sp_flag_finally = sp_flag_max;
                    	}
                    }

                    bean.set("sp_flag_code",sp_flag_finally);
                    bean.set("sp_flag", AdminCode.getCodeName("23", sp_flag_finally));//审批状态
                    bean.set("username", username);//当前填报人
                    bean.set("fullname", fullname);//填报人
                    bean.set("a00z2", PubFunc.FormatDate(rs.getDate("a00z2"), "yyyy-MM-dd"));//发放日期
                    bean.set("b0110", AdminCode.getCodeName("UN", b0110).length() == 0 ? AdminCode.getCodeName("UM", b0110) : AdminCode.getCodeName("UN", b0110));//应用机构
                    bean.set("count", rs.getInt("count"));//人数
                    //获取审批人start
                    if (!table.contains("_salary_")) {//薪资历史表
                        String curr = StringUtils.isBlank(rs.getString("curr_user")) ? "" : rs.getString("curr_user");
                        String currusername = StringUtils.isBlank(rs.getString("currusername")) ? "" : rs.getString("currusername");
                    	bean.set("curr_user", curr);//当前审批人
                        bean.set("curr_user_fullname", currusername);//当前审批人
                    }
                    
                    if (table.contains("_salary_")) {//薪资临时表
                    	//这里不需要判断状态了，应为sql中临时表只查驳回和起草状态的
                        if (isShare && "02".equals(sp_flag_min)) {//如果共享账套是起草或者驳回状态，但是报审状态是已报批的，显示出审批人
                            String curr = table.split("_salary_")[0];
                            RecordVo vo = new RecordVo("operuser");
                            vo.setString("username", curr);
                            String currname = dao.findByPrimaryKey(vo).getString("fullname");
                            bean.set("curr_user", curr);//当前审批人
                            bean.set("curr_user_fullname", StringUtils.isNotBlank(currname) ? currname : curr);//当前审批人全称
                        } else {
                            bean.set("curr_user", "");//当前审批人
                            bean.set("curr_user_fullname","");//当前审批人全称
                        }
                    }
                    //获取审批人end
                    for (int i = 0; i < listNum.size(); i++) {
                        bean.set(listNum.get(i).toLowerCase(), rs.getString(listNum.get(i)) == null ? "0" : rs.getString(listNum.get(i)));
                    }
                    
                    //因为是根据设置的应用机构去循环的，所以这里肯定必须是一条数据
                    bean_old = megerData(bean_old, bean, listNum, "add");
                }
                
                if(bean_old != null) {
                	lazyDynaBeanList.add(bean_old);
                	bean_old = null;
                }
            }
            if(lazyDynaBeanList.size() == 0 && "salaryhistory".equalsIgnoreCase(table)) {
                lazyDynaBeanList=getHaveOrg(gzReportingDataList, numberItem, a00z2, listNum, "salaryarchive", isShare, curr_stateOfWrite, gzbo, approvalSituationBo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return lazyDynaBeanList;
    }
    
    /**
     * 获取所有的薪资类别
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getSalaryItems() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        RowSet rs = null;
        try {
            sql.append("select salaryid,cname from salarytemplate");
            if ("0".equals(gz_module))
                sql.append(" where (cstate is null or cstate='')");// 薪资类别
            else
                sql.append(" where cstate='1'");// 险种类别
            rs = dao.search(sql.toString() + " order by seq");
            while (rs.next()) {
                HashMap<String, String> map = new HashMap<String, String>();
                String salaryid = rs.getString("salaryid");
                if ("0".equals(gz_module)){
					if (!this.userview.isHaveResource(IResourceConstant.GZ_SET, salaryid))
						continue;
				}else {
					if (!this.userview.isHaveResource(IResourceConstant.INS_SET, salaryid))
						continue;
				}
                
                map.put("id", PubFunc.encrypt(salaryid));
                map.put("name", rs.getString("salaryid") + "." + rs.getString("cname"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    /**
     * 获取所有的业务日期
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getSalaryDate(String manager) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search("select a00z2 from gz_extend_log where salaryid=? group by a00z2 order by a00z2 desc", Arrays.asList(new String[]{this.salaryid}));
            while (rs.next()) {
                HashMap<String, String> map = new HashMap<String, String>();
                String a00z2 = rs.getDate("a00z2") == null ? "" : PubFunc.FormatDate(rs.getDate("a00z2"), "yyyy-MM-dd");
                map.put("id", a00z2);
                map.put("name", a00z2);
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    /**
     * 获取所有的状态
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getSalaryState(ArrayList<LazyDynaBean> dataList) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> mapData = new LinkedHashMap<String, String>();
        try {
        	//这里暂时不根据gz_extend_log表进行查询，可能会出现页面上不显示的状态
        	for(int i = 0; i < dataList.size(); i++) {
        		LazyDynaBean bean = dataList.get(i);
        		String sp_flag_code = (String) bean.get("sp_flag_code");
        		String sp_flag = (String) bean.get("sp_flag");
        		map.put(sp_flag_code, sp_flag);
        	}
        	Iterator iter = map.entrySet().iterator();
        	while (iter.hasNext()) {
        		Map.Entry entry = (Map.Entry) iter.next();
        		mapData = new LinkedHashMap<String, String>();
        		mapData.put("id", (String)entry.getKey());
        		mapData.put("name", (String)entry.getValue());
                list.add(mapData);
        	}
        	map = new LinkedHashMap<String, String>();
        	map.put("id", "all");
        	map.put("name", ResourceFactory.getProperty("label.all"));
        	list.add(0, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getA00z2(String a00z2) {
        RowSet rs = null;
        String maxA00Z2 = "";
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search("select a00z2 from gz_extend_log where salaryid=? group by a00z2 order by a00z2 desc", Arrays.asList(new String[]{this.salaryid}));
            while (rs.next()) {
                String a00z2Date = rs.getDate("a00z2") == null ? "" : PubFunc.FormatDate(rs.getDate("a00z2"), "yyyy-MM-dd");
                if (StringUtils.isBlank(maxA00Z2)) {
                    maxA00Z2 = a00z2Date;
                }
                if (a00z2.equals(a00z2Date)) {
                	maxA00Z2 = a00z2;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return maxA00Z2;
    }
    
    /**
     * 获得可用的发送方式（短信，微信，钉钉）
     * @throws GeneralException
     */
	public HashMap<String, Boolean> getEnableModes() throws GeneralException{
		// 短信
		SendMsgBo send= new SendMsgBo();
		HashMap<String, Boolean> enableModes = new HashMap<String, Boolean>();

		String mobile = send.getMobileField();
		String corpid = (String) ConstantParamter.getAttribute("wx","corpid");//判断微信参数是否设置
		String dd_corpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");//判断钉钉参数是否设置
		enableModes.put("smsflag", StringUtils.isNotBlank(mobile)?true:false);
		enableModes.put("weixinflag", StringUtils.isNotBlank(corpid)?true:false);
		enableModes.put("ddflag", StringUtils.isNotBlank(dd_corpid)?true:false);

		return enableModes;

	}
	
	/**
	 * 发送邮件等，状态是已报的时候发送给审批人，如果是驳回或者起草的时候发给填报人
	 * @param type
	 * @param sp_flag
	 * @param username
	 * @param fullname
	 * @throws GeneralException 
	 */
	public String sendMessage(ArrayList sendList,String sp_flag,String username,String fullname,String content) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		SalaryAccountBo send= new SalaryAccountBo(this.conn,this.userview,Integer.parseInt(salaryid));
		SalaryTemplateBo salaryTemplateBo=new SalaryTemplateBo(conn,Integer.parseInt(salaryid),this.userview);
		RowSet rset = null;
		String msg = "";
		try {
			String manage = salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(StringUtils.isBlank(manage) || "02".equals(sp_flag)) {//如果是非共享账套，发给operuser,或者是审批状态的
				String[] usernameArray = username.split(","); 
				for(int i = 0; i < usernameArray.length; i++) {
					if(StringUtils.isBlank(usernameArray[i]))
						continue;
					
					HashMap<String,String> map = getPhoneEmail(usernameArray[i]);
					//获取对应的自助用户
					String weixin = send.getZizhuUsername(map.get("base") == null?"usr":(String)map.get("base"), usernameArray[i]);
					msg += this.sendTips(sendList,content,fullname.split(",")[i],(String)map.get("email"),(String)map.get("phone"),weixin);
				}
			}else {
				RecordVo user_vo=ConstantParamter.getConstantVo("SS_LOGIN");
			    String A01 = user_vo.getString("str_value");//取认证人员库
			    ArrayList dbList = this.userview.getPrivDbList(); //权限范围内的库
			    String nbases = "";
		        if(dbList.size() > 0 ){
		        	for(int k = 0 ; k < dbList.size() ; k++){
		        		//如果既是设置的认证应用库 又是当前登录用户权限范围内的库
						if(("," + A01.toLowerCase() + ",").indexOf(("," +dbList.get(k).toString().toLowerCase()+",")) !=-1){
								nbases += dbList.get(k).toString() + ",";
						}
		        	}
		        }
				if(",".equals(nbases) || StringUtils.isBlank(nbases)){//没任何人员库权限
					msg += ResourceFactory.getProperty("sys.account.dbpre.msg");
				}else{//有人员库权限，取交集
					String[] tempNbase = nbases.split(",");
	                String email = "";
	                String phone = "";
	                for(int i=0;i<tempNbase.length;i++){
	                    String dbnamei = String.valueOf(tempNbase[i]);
	                    if(StringUtils.isBlank(dbnamei))
	                    	continue;
	                    String logonItem = getUsernameItem();
	                    RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
	    				String email_field=avo.getString("str_value");
	    				String field_name=salaryTemplateBo.getMobileNumber();
	    				String[] usernameArr = username.split(",");
	    				for(int j = 0; j < usernameArr.length; j++) {
		    				String sql= "select "+email_field;
		    				if(email_field!=null&&email_field.length()>0&&field_name.length()>0)
		    					sql+=",";
		    				if(field_name.length()>0)	
		    					sql+=field_name;
		    				sql+=" from " + dbnamei + "A01 where " + logonItem + "='" + usernameArr[j] + "'";//暂时全部是在职人员库
		    				if((email_field!=null&&email_field.length()>0)||field_name.length()>0)
		    				{
		    					rset=dao.search(sql);
		    					if(rset.next())
		    					{
		    						if(email_field!=null&&email_field.length()>0)							
		    							email=rset.getString(email_field);
		    						if(field_name!=null&&field_name.length()>0)
		    							phone=rset.getString(field_name);
		    						
		    						msg += this.sendTips(sendList,content,fullname.split(",")[j],email,phone,username);
		    						break;
		    					}
		    				}
		    				
	    				}
	                }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return msg;
	}
	
	/**
     * 通知提醒-发送方式
     * @param sendlist 已选中的发送方式
     * @param contenMsg 消息内容
     * @param email 邮箱地址
     * @param phone 手机号
     * @param weixin 微信号
     */
    private String sendTips(ArrayList sendlist, String contenMsg, String receiver,String email, String phone, String weixin) throws GeneralException{
        String msg = "";
        try{
        	SendMsgBo send= new SendMsgBo();
            if(sendlist.contains(ResourceFactory.getProperty("train.message.email"))){
                /**邮件*/
            	LazyDynaBean emailbean = new LazyDynaBean();
            	if(StringUtils.isNotBlank(email)) {
            		SalaryUtils salaryUtils = new SalaryUtils();
            		//是否设置了邮箱服务器
            		String emailConfig_msg = salaryUtils.getSetEmailInfo();
            		if(StringUtils.isBlank(emailConfig_msg)) {
		            	emailbean.set("toAddr", email);
		                emailbean.set("subject", ResourceFactory.getProperty("label.gz.tipOfProcess"));
		                emailbean.set("bodyText", contenMsg);
		                emailbean.set("href", "");
		                emailbean.set("hrefDesc", "");
		                AsyncEmailBo emailbo = new AsyncEmailBo(this.conn, this.userview);
		                emailbo.send(emailbean);
            		}else {
            			msg = emailConfig_msg;
            		}
            	}else {
            		msg = " " + receiver + " " +  ResourceFactory.getProperty("label.gz.noEmailAddress");
            	}
            }
            if(sendlist.contains(ResourceFactory.getProperty("train.message.sms"))){
                /**短信*/
				if(StringUtils.isNotBlank(phone))
				{
					SmsBo smsbo=new SmsBo(this.conn);
					boolean send_phone = smsbo.sendMessage2(this.userview.getUserFullName()!=null?this.userview.getUserFullName():this.userview.getUserName(),receiver,phone,contenMsg);
					if(!send_phone) {
						//短信发送失败,可能的原因:\r\n  1.短信接口参数设置错误.\r\n  2.请检查网络连接
						msg = ResourceFactory.getProperty("hire.employResume.sendmessagefaild");
					}
				}else {
					msg = " " + receiver + " " + ResourceFactory.getProperty("label.gz.noPhoneAddress");
				}
			
            }
            if(sendlist.contains(ResourceFactory.getProperty("train.message.dingding"))){
                /**微信*/
            	contenMsg = contenMsg.replaceAll("&nbsp;", "");//去除html标签：空格符
            	contenMsg = contenMsg.replaceAll("&emsp;", "");//去除html标签：制表符
            	contenMsg = contenMsg.replaceAll("<br>", "");//去除html标签：换行符
                WeiXinBo.sendMsgToPerson(weixin, ResourceFactory.getProperty("label.gz.tipOfProcess"), contenMsg, "", "");
            }
            if(sendlist.contains(ResourceFactory.getProperty("train.message.dingding"))){
                /**钉钉*/
            	contenMsg = contenMsg.replaceAll("&nbsp;", "");//去除html标签：空格符
            	contenMsg = contenMsg.replaceAll("&emsp;", "");//去除html标签：制表符
            	contenMsg = contenMsg.replaceAll("<br>", "");//去除html标签：换行符
            	DTalkBo.sendMessage(weixin, ResourceFactory.getProperty("label.gz.tipOfProcess"), contenMsg, "", "");
            }
        } catch (Exception e) {
            msg = e.getMessage();
            throw GeneralExceptionHandler.Handle(e);
        }
        return msg;
    }
    
    /**
     * 获取对应的phone和email指标
     * @param username
     * @return
     */
    public HashMap<String,String> getPhoneEmail(String username) {
    	String phone = "";
    	String email = "";
    	HashMap<String,String> map = new HashMap<String,String>();
    	ContentDAO dao = new ContentDAO(this.conn);
		SalaryTemplateBo salaryTemplateBo=new SalaryTemplateBo(conn,Integer.parseInt(salaryid),this.userview);
		RowSet rset = null;
    	try {
	    	RecordVo vo=new RecordVo("operuser");
			vo.setString("username",username);
			vo=dao.findByPrimaryKey(vo);
			String dbase=vo.getString("nbase");
			String a0100=vo.getString("a0100");
			
			if(!StringUtils.isBlank(a0100))
			{
				RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
				String email_field=avo.getString("str_value");
				String field_name=salaryTemplateBo.getMobileNumber();
				String sql= "select "+email_field;
				if(email_field!=null&&email_field.length()>0&&field_name.length()>0)
					sql+=",";
				if(field_name.length()>0)	
					sql+=field_name;
				sql+=" from "+dbase+"A01 where a0100='"+a0100+"'";
				if((email_field!=null&&email_field.length()>0)||field_name.length()>0)
				{
					rset=dao.search(sql);
					if(rset.next())
					{
						if(email_field!=null&&email_field.length()>0)							
							email=rset.getString(email_field);
						if(field_name!=null&&field_name.length()>0)
							phone=rset.getString(field_name);
					}
				}
				
			}
		
			if(StringUtils.isBlank(email)) {
				email=vo.getString("email");
			}
			
			if(StringUtils.isBlank(phone)) {
				phone=vo.getString("phone");
			}
			map.put("email", email);
			map.put("phone", phone);
			map.put("dbase", dbase);
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
    	return map;
    }
    
    /**
     * 获取登录用户名的itemid
     * @return
     */
    public String getUsernameItem() {
    	String username = "";
    	try {
    		 RecordVo login_vo = ConstantParamter
                     .getConstantVo("SS_LOGIN_USER_PWD");
             if (login_vo == null) {
                 username = "username";
             } else {
                 String login_name = login_vo.getString("str_value").toLowerCase();
                 int idx = login_name.indexOf(",");
                 if (idx == -1) {
                     username = "username";
                 } else {
                     username = login_name.substring(0, idx);
                     if ("#".equals(username) || "".equals(username)) {
                         username = "username";
                     }
                 }
             }
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	return username;
    }
    
    /**
     * 重组datalist，由于可能出现发起人是一个人，但是已报批，已批这种情况，对于这种上面不进行分组，
     * 否则还得相加数据，这里一起合计，由于数据不大，效率不太影响
     * @param gzbo
     */
    public LazyDynaBean megerData(LazyDynaBean bean_old, LazyDynaBean bean, ArrayList<String> listNum, String flag) {
    	try {
    		
    		if(bean_old == null) {
    			if("total".equalsIgnoreCase(flag)) {
	    			bean_old = new LazyDynaBean();
	    			bean_old.set("count", (Integer)bean.get("count"));
	    			for(int i = 0; i < listNum.size(); i++) {
	        			String listNum_item = listNum.get(i).toLowerCase();
	    				String value = bean.get(listNum_item)==null?"0":(String)bean.get(listNum_item);
	    				bean_old.set(listNum_item, value);
	        		}
	    			return bean_old;
    			}else {
    				return bean;
    			}
    		}
    		int count_old = Integer.parseInt(isNullOpe(bean_old.get("count")));
			int count = Integer.parseInt(isNullOpe(bean.get("count")));
			String curr_user_old = isNullOpe(bean_old.get("curr_user"));
			String curr_user_fullname_old = isNullOpe(bean_old.get("curr_user_fullname"));
			String curr_user = isNullOpe(bean.get("curr_user"));
			String curr_user_fullname = isNullOpe(bean.get("curr_user_fullname"));
			String sp_flag_code_old = isNullOpe(bean_old.get("sp_flag_code"));
			String sp_flag_code = isNullOpe(bean.get("sp_flag_code"));
			
			
			bean_old.set("count", count + count_old);
			bean_old.set("curr_user", getUserContact(curr_user_old,curr_user));//当前审批人
			bean_old.set("curr_user_fullname", getUserContact(curr_user_fullname_old,curr_user_fullname));//当前审批人
			
			if ("07".equals(sp_flag_code)) {
                if (!"01".equals(sp_flag_code_old)) {
                	//如果新的是驳回，只有在旧的是起草的情况下，才是取旧的
                	bean_old.set("sp_flag", isNullOpe(bean.get("sp_flag")));
        			bean_old.set("sp_flag_code", sp_flag_code);
                }
            } else if("07".equals(sp_flag_code_old)) {
            	if (!"01".equals(sp_flag_code)) {
                	//如果旧的是驳回，只有在新的是起草的情况下，才是取新的
                	bean_old.set("sp_flag", isNullOpe(bean_old.get("sp_flag")));
        			bean_old.set("sp_flag_code", sp_flag_code_old);
                }
            } else if(StringUtils.isNotBlank(sp_flag_code_old) && (Integer.parseInt(sp_flag_code_old) > Integer.parseInt(sp_flag_code))){//如果 起草01 报批02 已批03 批准06 驳回07，驳回单独判断，其余按照顺序，取最小的
            	//如果新的比旧的小，取新的得状态
                bean_old.set("sp_flag", isNullOpe(bean.get("sp_flag")));
    			bean_old.set("sp_flag_code", sp_flag_code);
            }
			
    		for(int i = 0; i < listNum.size(); i++) {
    			String listNum_item = listNum.get(i).toLowerCase();
				String value_old = isNullOpe(bean_old.get(listNum_item));
				String value = isNullOpe(bean.get(listNum_item));
				BigDecimal decimal = null;
				BigDecimal decimalHis = null;
				if(StringUtils.isNotBlank(value_old)) {
					decimal=new BigDecimal(value_old);
				}
				
				if(StringUtils.isNotBlank(value)) {
					decimalHis=new BigDecimal(value);
				}
    			//数值型的相加
				decimalHis = decimal == null?decimalHis:decimalHis.add(decimal);
				
				if(decimalHis != null) {
					int decimalWith = DataDictionary.getFieldItem(listNum_item).getDecimalwidth();//小数位
					double valueHistoryD = decimalHis.setScale(decimalWith,BigDecimal.ROUND_HALF_UP).doubleValue();
					bean_old.set(listNum_item, String.valueOf(valueHistoryD));
				}
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return bean_old;
    }
    
    /**
     * 重组datalist，由于可能出现发起人是一个人，但是已报批，已批这种情况，对于这种上面不进行分组，
     * 否则还得相加数据，这里一起合计，由于数据不大，效率不太影响
     * @param gzbo
     * @param dataList m没有总计的数据集合
     * @param listNum 数值型指标
     * @param size 应用机构的数量
     */
    public LazyDynaBean sumDataList(SalaryTemplateBo gzbo, ArrayList<LazyDynaBean> dataList, ArrayList<String> listNum, String a00z2, int size) {
    	HashMap<String, Integer> gz_item = null;
    	boolean showNumSum = false;//是否显示合计行
    	LazyDynaBean bean_old = null;
    	
    	try {
    		//如果设置了栏目设置，并且数值型指标展示，显示出合计行
    		int scheme_id = gzbo.getSchemeId("process_monitor_"+salaryid);
    		if(scheme_id > 0) {
    			gz_item = gzbo.getTableItemsWithToMap(scheme_id, "1");
    			for (Map.Entry<String, Integer> m : gz_item.entrySet()) {
			        if(listNum.contains(m.getKey().toUpperCase())) {
			        	showNumSum = true;
			        	break;
			        }
    			}
    		}
    		
    		if(showNumSum) {
    			for(int i = 0; i < dataList.size(); i++) {
    				LazyDynaBean bean = dataList.get(i);
    				
    				bean_old = megerData(bean_old, bean, listNum, "total");
    			}
    			bean_old.set("sp_flag_code", "");
    			bean_old.set("sp_flag", "");//审批状态
    			bean_old.set("username", "");//当前填报人
    			bean_old.set("fullname", size>0?"":"总计");//填报人
    			bean_old.set("a00z2", a00z2);//发放日期
    			bean_old.set("b0110", "总计");//应用机构
    			bean_old.set("curr_user", "");//当前审批人
    			bean_old.set("curr_user_fullname", "");//当前审批人
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return bean_old;
    }
    
    /**
     * 保证审批人显示正确，逗号分隔显示
     * @param old_str
     * @param str
     * @return
     */
    private String getUserContact(String old_str, String str) {
    	String user_new = old_str;
    	try {
    		if(StringUtils.isBlank(old_str) && StringUtils.isNotBlank(str)) {
    			user_new = str;
    		}else if(StringUtils.isNotBlank(old_str) && StringUtils.isNotBlank(str)) {
    			user_new = "";
    			String[] old_str_arr = old_str.split(",");
    			for(int i = 0; i < old_str_arr.length; i++) {
    				String old_str_ = old_str_arr[i];
    				if(StringUtils.isNotBlank(old_str_)) {
    					user_new += "," + old_str_arr[i];
    				}
    			}
    			user_new += "," + str;
    			user_new = user_new.substring(1);
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return user_new;
    }
    
    private String isNullOpe(Object value) {
    	String val = "";
    	if(value != null) {
    		val = String.valueOf(value);
    	}
    	return val;
    }
}
