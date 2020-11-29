package com.hjsj.hrms.module.kq.kqdata.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.config.item.businessobject.impl.KqItemServiceImpl;
import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataArchiveService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataSpService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.kq.util.KqUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class KqDataSpServiceImpl implements KqDataSpService {
    private Connection connection;
    private UserView userView;
    private Category cat = Category.getInstance(KqDataSpServiceImpl.class);

    @Override
    public void saveExportScheme(String detailsVal, String sumsVal, String scheme_id) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.connection);
        RowSet rs = null;
        try {
            List values = new ArrayList();
            values.add(scheme_id);
            int report_id=0;
            RecordVo vo = null;
            rs = dao.search("select report_id from kq_report where tab_id=?",values);
            boolean updateFlag = false;
            if(rs.next()){
                report_id = rs.getInt(1);
                RecordVo vo1 = new RecordVo("kq_report");
                vo1.setInt("report_id",report_id);
                vo = dao.findByPrimaryKey(vo1);
                updateFlag = true;
            }
            if(vo == null) {
                rs = dao.search("select max(report_id) from kq_report");
                if (rs.next()) {
                    report_id = rs.getInt(1) + 1;
                    vo = new RecordVo("kq_report");
                    vo.setInt("report_id",report_id);
                    vo.setString("name",ResourceFactory.getProperty("kq.data.sp.export.name"));
                    vo.setInt("flag",1);
                    vo.setString("tab_id",scheme_id);
                }
            }
            String xml = genExportExcelXml(detailsVal,sumsVal);
            vo.setString("content",xml);
            if(updateFlag){
                dao.updateValueObject(vo);
            }else{
                dao.addValueObject(vo);
            }
        }catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }
    @Override
    public Map<String,String> getExportScheme(String scheme_id) throws GeneralException {
        Map<String,String> map = new HashMap<String, String>();
        ContentDAO dao = new ContentDAO(this.connection);
        RowSet rs = null;
        String detailsVal = "";
        String sumsVal = "";
        try {
            List values = new ArrayList();
            String sql = "select content from kq_report where tab_id=?";
            values.add(scheme_id);
            rs = dao.search(sql,values);

            String xml = "";
            if(rs.next()){
                xml = rs.getString(1);
                //解析xml
                //通过输入源构造一个Document
                Document doc = PubFunc.generateDom(xml);
                //取的根元素
                Element root = doc.getRootElement();
                List<Element> kq_reports = root.getChildren("kq_report");
                for(Element el : kq_reports) {
                    String type = el.getAttributeValue("type");
                    Element body = el.getChild("body");
                    String content = body.getAttributeValue("cols");
                    if("details".equalsIgnoreCase(type)){
                        detailsVal = content;
                    }else{
                        sumsVal = content;
                    }
                }
            }
            //如未设置过导出方案，则给出默认选项。
            if(StringUtils.isBlank(xml)){
                detailsVal = "seq,a0101,only_field,dates";
                sumsVal = "seq,a0101,only_field";
                KqDataMxService kqDataMxService = new KqDataMxServiceImpl(this.userView,this.connection);
                String sumsVal_ = kqDataMxService.getSumsByScheme(PubFunc.encrypt(scheme_id));
                if(sumsVal_.length()>0){
                    sumsVal+=","+sumsVal_;
                }
            }
        }catch (Exception e){
            throw  GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        map.put("detailsVal",detailsVal);
        map.put("sumsVal",sumsVal);

        return map;
    }
    /**
     * 生成导出Excel配置方案的xml格式字符
     * @return
     * @throws GeneralException
     */
    private String genExportExcelXml(String detailsVal, String sumsVal) throws GeneralException {
        String xml = "";
        try {
            Element root = new Element("params");
            Element details = new Element("kq_report");
            details.setAttribute("id","1");
            details.setAttribute("type","details");
            Element body1 = new Element("body");
            body1.setAttribute("cols",detailsVal);
            details.addContent(body1);
            root.addContent(details);

            Element sums = new Element("kq_report");
            sums.setAttribute("id","2");
            sums.setAttribute("type","sum");
            Element body2 = new Element("body");
            body2.setAttribute("cols",sumsVal);
            sums.addContent(body2);
            root.addContent(sums);
            //生成xml
            Document myDocument = new Document(root);
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            xml = outputter.outputString(myDocument);
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        }
        return xml;
    }

    /**
     * 获取考勤填报记录表
     *
     * @param sqlWhere
     * @param parameterList
     * @param sqlSort
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 16:37 2018/10/30
     */
    @Override
    public ArrayList<LazyDynaBean> listKq_extend_log(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException {

        ContentDAO dao = new ContentDAO(this.getConnection());
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        StringBuffer strSql = new StringBuffer();
        try {
            strSql.append("SELECT scheme_id ,");
            strSql.append(Sql_switcher.isnull("Kq_year", "''")).append(" as kq_year , ");
            strSql.append(Sql_switcher.isnull("Kq_duration", "''")).append(" as kq_duration , ");
            strSql.append(Sql_switcher.isnull("Org_id", "''")).append(" as org_id , ");
            strSql.append(Sql_switcher.isnull("Sp_flag", "''")).append(" as sp_flag , ");
            strSql.append(Sql_switcher.isnull("Curr_user", "''")).append(" as curr_user , ");
            strSql.append(Sql_switcher.isnull("Appuser", "''")).append(" as appuser,  ");
            strSql.append(" case  when sp_process is null then '0' else '1' end").append(" as process  ");
            strSql.append(" FROM kq_extend_log where 1=1 ");
            if (StringUtils.isNotBlank(sqlWhere)) {
                strSql.append(sqlWhere);
            }
            if (StringUtils.isNotBlank(sqlSort)) {
                strSql.append(" ORDER BY ").append(sqlSort);
            } else {
                strSql.append(" ORDER BY Kq_year,Kq_duration desc,Scheme_id ");
            }
            ArrayList pList = new ArrayList();
            if (parameterList != null) {
                pList.addAll(parameterList);
            }
            rs = dao.search(strSql.toString(), pList);
            while (rs.next()) {
                //查询结果 bean 中的key为全小写字段名
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("scheme_id", rs.getString("scheme_id"));
                bean.set("kq_year", rs.getString("kq_year"));
                bean.set("kq_duration", rs.getString("kq_duration"));
                bean.set("org_id", rs.getString("org_id"));
                bean.set("sp_flag", rs.getString("sp_flag"));
                bean.set("curr_user", rs.getString("curr_user"));
                bean.set("appuser", rs.getString("appuser"));
                bean.set("process", rs.getString("process"));
                dataList.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }


    @Override
    public ArrayList<HashMap<String, String>> listKqSpMainData(JSONObject jsonStrObject, LazyDynaBean kq_schemeBean) throws GeneralException {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        try {
            String scheme_id = (String) jsonStrObject.get("scheme_id");
            String kq_duration = (String) jsonStrObject.get("kq_duration");
            String kq_year = (String) jsonStrObject.get("kq_year");
            String selectStatus = (String) jsonStrObject.get("status");
            String queryValues = (String) jsonStrObject.get("query");
            ArrayList<String> queryList = new ArrayList<String>();
            if (StringUtils.isNotBlank(queryValues)) {
                String[] queryvalues = queryValues.split(",");
                for (String value : queryvalues) {
                    queryList.add(SafeCode.decode(value));
                }
            }

            //判断是否需要上报人事处 secondary_admin 0不需要 1需要
            boolean haveSecondary = false;
            if (kq_schemeBean.getMap().containsKey("secondary_admin") && kq_schemeBean.get("secondary_admin") != null) {
                if ("1".equals(String.valueOf(kq_schemeBean.get("secondary_admin")))) {
                    haveSecondary = true;
                }
            }

            KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView(), this.connection);
            int role = kqDataUtil.getKqRole("1", kq_schemeBean);
            String userName = this.getUserView().getUserName();
            if (role == KqDataUtil.role_Reviewer) {
                userName = this.getUserView().getDbname() + this.getUserView().getA0100();
            }
            String clerkName = String.valueOf(kq_schemeBean.get("clerk_username"));
            String reviewerName = String.valueOf(kq_schemeBean.get("reviewer_id"));

            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            parameterList.add(kq_duration);
            parameterList.add(kq_year);
            String strWhere = " and scheme_id=? and kq_duration=? and kq_year=? ";
            if (StringUtils.isNotBlank(selectStatus)) {
                strWhere += " and ";
                strWhere += this.getSelectStatusSql(selectStatus, role, clerkName, reviewerName);
            }
            ArrayList<LazyDynaBean> listKq_extend_log = this.listKq_extend_log(strWhere, parameterList, "");
            HashMap<String, Integer> orgCount = this.mapQ35OrgCount(kq_year, kq_duration, scheme_id);

            ArrayList org_scopeList =(ArrayList) kq_schemeBean.get("org_map");
            for (int i = 0; i < org_scopeList.size(); i++) {
                HashMap orgMap = (HashMap) org_scopeList.get(i);
                HashMap<String, String> org = new HashMap<String, String>();
                String orgId = String.valueOf(orgMap.get("org_id"));
                int num = 0;

                Iterator iterator = orgCount.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
                    if (String.valueOf(entry.getKey()).startsWith(orgId)) {
                        num += entry.getValue();
                        iterator.remove();
                    }
                }

                if (queryList.size() > 0) {
                    String orgName = AdminCode.getCodeName("UM", orgId);
                    if (StringUtils.isBlank(orgName)) {
                        orgName = AdminCode.getCodeName("UN", orgId);
                    }
                    boolean s = false;
                    for (String value : queryList) {
                        if (orgName.indexOf(value) > -1) {
                            s = true;
                            break;
                        }
                    }
                    if (!s) {
                        continue;
                    }
                }

                LazyDynaBean newxRole = kqDataUtil.getKqRoleNextLevel("1", kq_schemeBean, role, orgId);
                String hasNextApprover = "".equals(newxRole.get("role")) ? "0" : "1";
                org.put("org_id", PubFunc.encrypt(orgId));
                org.put("org_name", String.valueOf(orgMap.get("name")));
                org.put("hasNextApprover", hasNextApprover);
                org.put("clerk_id", PubFunc.encrypt(String.valueOf(orgMap.get("y_clerk_username"))));
                org.put("clerk_name", String.valueOf(orgMap.get("clerk_username")));

                boolean isHave = false;
                for (LazyDynaBean bean : listKq_extend_log) {
                    if (orgId.equalsIgnoreCase(String.valueOf(bean.get("org_id")))) {
                        String curr_user = String.valueOf(bean.get("curr_user"));
                        String appuser = String.valueOf(bean.get("appuser"));
                        HashMap<String, String> spMap = this.getSpflagName(role, String.valueOf(bean.get("sp_flag")), curr_user,appuser, clerkName, reviewerName);
                        org.put("sp_flag_name", spMap.get("name"));
                        org.put("sp_flag", spMap.get("sp_flag"));
                        org.put("process", String.valueOf(bean.get("process")));
                        // linbz 更改流程 新的流程存的是角色
                        boolean isNewCurr_user = (",1,2,3,4,".contains(","+curr_user+","));
                        String operation = "0";
                        if ("03".equals(String.valueOf(bean.get("sp_flag")))) {
                            if(haveSecondary) {
                                operation = "2";
                            }else{
                                operation="0";
                            }
                        }// 兼容流程 
                        else if (((curr_user.equals(String.valueOf(role))&&isNewCurr_user) || curr_user.equals(userName))
                        		&& "02".equals(spMap.get("sp_flag"))) {
                            operation = "1";
                        } else if ("07".equals(String.valueOf(bean.get("sp_flag")))
                        		&& ((curr_user.equals(String.valueOf(role)) && isNewCurr_user)
                        				|| (curr_user.equals(userName) && !appuser.endsWith(";"+userName+";")))) {
                            operation = "1";
                        }

                        org.put("operation", operation);
                        org.put("approveUser", getApproveUser(bean, kq_schemeBean, orgMap, haveSecondary, kqDataUtil));
                        isHave = true;
                        break;
                    }
                }
                //【55616】考勤管理 ：数据审批，打开12月份”未提交”考勤表，返回，“全部”考勤表统计数量出错，由7变成了3（oracle存储过程，登录用户“rsc5”）
                if (StringUtils.isNotBlank(selectStatus) &&!"00".equals(selectStatus)&& !isHave) {
                    continue;
                }
                //单据默认状态
                else if (!isHave) {
                    org.put("sp_flag", "00");
                    //"未创建"
                    org.put("sp_flag_name", ResourceFactory.getProperty("kq.data.sp.text.nocreate"));

                    if(haveSecondary&&role == KqDataUtil.role_Clerk) {
                        org.put("operation", "3");
                    }
                    //不需要上报人事处并且不是下级机构的审核人则不显示  计算、同意、退回  三个按钮
                    else if(!haveSecondary && role != KqDataUtil.role_Agency_Reviewer)
                    	org.put("operation", "0");
                }
                org.put("number", String.valueOf(num));
                dataList.add(org);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return dataList;
    }

    private HashMap<String, String> getUserA0100(String userName) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        RowSet rs = null;
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            String strSql = "select fullname,a0100,nbase from OperUser where upper(username)=? ";
            ArrayList list = new ArrayList();
            list.add(userName);
            rs = dao.search(strSql, list);
            if (rs.next()) {
                if (rs.getString("a0100") != null) {
                    map.put("a0100", rs.getString("a0100"));
                    map.put("nbase", rs.getString("nbase"));
                    map.put("a0101", rs.getString("fullname"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;

    }

    private String getSelectStatusSql(String selectStatus, int role, String clerkName, String reviewerName) {
        String strSql = "";
        if ("00".equals(selectStatus)) {
            strSql = " 1=1 ";
        } else if ("03".equals(selectStatus)) {
            strSql = " sp_flag='03' ";
        } else if ("06".equals(selectStatus)) {
            strSql = " sp_flag='06' ";
        } else {
            if (role == KqDataUtil.role_Clerk) {
                if ("07".equals(selectStatus)) {
                    strSql = " sp_flag='07' AND (upper(Curr_user)='" + clerkName.toUpperCase() + "' or Curr_user='"+role+"') ";
                } else if ("01".equals(selectStatus)) {
                	strSql = " ( sp_flag='01' or ((sp_flag='02' or sp_flag='07')"
                    		+ " and upper(Curr_user)!='" + clerkName.toUpperCase() + "' and  (Curr_user='3' or Curr_user='4')";
                    strSql += " and (upper(Curr_user)!='" + reviewerName.toUpperCase() + "'";
                    // 兼容oracle审核人为空的情况
            		if(StringUtils.isEmpty(reviewerName))
            			strSql +=  " or Curr_user is not null ";
        			strSql +=  ") ))";
                } // 47006 增加当前用户待办的考勤表
                else if ("02".equals(selectStatus) || "current".equals(selectStatus)) {
                    strSql = " sp_flag='02' and (upper(Curr_user)='" + clerkName.toUpperCase() + "' or Curr_user='1'";
                    if("02".equals(selectStatus))
                    	strSql += " or Curr_user='2'";
                    strSql += " or upper(Curr_user)='" + reviewerName.toUpperCase() + "')";
                }// 当前登录用户已办理的
                else if("done".equals(selectStatus)) {
                	strSql = " sp_flag='02' and (upper(Curr_user)='" + reviewerName.toUpperCase() + "' or Curr_user='2')";
                }
            } else {
                if ("01".equals(selectStatus)) {
                    strSql = " sp_flag!='03' AND sp_flag!='06' AND upper(Curr_user)!='" + reviewerName.toUpperCase() + "' and Curr_user<>'"+role+"'";
                } else if ("02".equals(selectStatus) || "current".equals(selectStatus)) {
                    strSql = " sp_flag='02' and (upper(Curr_user)='" + reviewerName.toUpperCase() + "' or Curr_user='"+role+"')";
                } else if ("07".equals(selectStatus)) {
                    strSql = " 1=2";
                }// 当前登录用户已办理的
                else if("done".equals(selectStatus)) {
                	strSql = " (sp_flag='03') and (upper(Curr_user)<>'" + reviewerName.toUpperCase() + "' or Curr_user<>'2')";
                }
            }
        }
        return strSql;
    }

    private HashMap<String, String> getSpflagName(int role, String sp_flag, String curr_user,String appuser, String clerkName, String reviewerName) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sp_flag", sp_flag);

        if ("00".equals(sp_flag)) {
            //"未创建"
            map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nocreate"));
        } else if ("01".equals(sp_flag)) {
            //"未提交"
            map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit"));
        } else if ("03".equals(sp_flag)) {
            //"已批准"
            map.put("name", ResourceFactory.getProperty("kq.data.sp.text.approved"));
        } else if ("06".equals(sp_flag)) {
            //"已归档"
            map.put("name",ResourceFactory.getProperty("kq.data.sp.text.archived"));
        } else if ("08".equals(sp_flag)) {
            //"未下发"
            map.put("name",ResourceFactory.getProperty("kq.data.sp.text.nodownward"));
        } else {
        	// linbz 修改流程 兼容
        	if(!",1,2,3,4,".contains(","+curr_user+",")) {
        		map = getSpflagNameOld(role, sp_flag, curr_user, appuser, clerkName, reviewerName);
        		return map;
        	}
        	int curr_user_role = Integer.parseInt(curr_user);
            map.put("name", "");
            if (role == KqDataUtil.role_Clerk) {
                if ("02".equals(sp_flag)) {
                	if (curr_user_role == KqDataUtil.role_Clerk || curr_user_role == KqDataUtil.role_Reviewer) {
                        //已提交"
                        map.put("name", ResourceFactory.getProperty("kq.data.sp.text.submited"));
                    } else {
                        map.put("sp_flag", "01");
                        //"未提交"
                        map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit"));
                    }

                } else if ("07".equals(sp_flag)) {
                	if (curr_user_role == KqDataUtil.role_Clerk) {
                        //"退回"
                        map.put("name", ResourceFactory.getProperty("kq.data.sp.text.back"));
                    } else {
                        map.put("sp_flag", "01");
                        //"未提交"
                        map.put("name",ResourceFactory.getProperty("kq.data.sp.text.nosumbit") );
                    }
                }
            } else if (role == KqDataUtil.role_Reviewer) {
                if ("02".equals(sp_flag)) {
                    if (curr_user_role == KqDataUtil.role_Reviewer) {
                        //"已提交"
                        map.put("name", ResourceFactory.getProperty("kq.data.sp.text.submited"));
                    } else {
                        map.put("sp_flag", "01");
                        //"未提交"
                        map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit") );
                    }
                } else if ("07".equals(sp_flag)) {
                    map.put("sp_flag", "01");
                    //"未提交"
                    map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit"));
                }
            }
        }
        return map;
    }
    /**
     * 兼容旧的流程
     * getSpflagNameOld
     * @param role
     * @param sp_flag
     * @param curr_user
     * @param appuser
     * @param clerkName
     * @param reviewerName
     * @return
     * @date 2019年4月9日 下午2:45:19
     * @author linbz
     */
    private HashMap<String, String> getSpflagNameOld(int role, String sp_flag, String curr_user,String appuser, String clerkName, String reviewerName) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sp_flag", sp_flag);
        map.put("name", "");
        if (role == KqDataUtil.role_Clerk) {
            if ("02".equals(sp_flag)) {
                if (curr_user.equalsIgnoreCase(clerkName) || curr_user.equalsIgnoreCase(reviewerName)) {
                    //已提交"
                    map.put("name", ResourceFactory.getProperty("kq.data.sp.text.submited"));
                } else {
                    map.put("sp_flag", "01");
                    //"未提交"
                    map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit"));
                }

            } else if ("07".equals(sp_flag)) {
                if (curr_user.equalsIgnoreCase(clerkName)&&!appuser.equalsIgnoreCase(";"+clerkName+";")) {
                    //"退回"
                    map.put("name", ResourceFactory.getProperty("kq.data.sp.text.back"));
                } else {
                    map.put("sp_flag", "01");
                    //"未提交"
                    map.put("name",ResourceFactory.getProperty("kq.data.sp.text.nosumbit") );
                }
            }
        } else if (role == KqDataUtil.role_Reviewer) {
            if ("02".equals(sp_flag)) {
                if (curr_user.equalsIgnoreCase(reviewerName)) {
                    //"已提交"
                    map.put("name", ResourceFactory.getProperty("kq.data.sp.text.submited"));
                } else {
                    map.put("sp_flag", "01");
                    //"未提交"
                    map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit") );
                }
            } else if ("07".equals(sp_flag)) {
                map.put("sp_flag", "01");
                //"未提交"
                map.put("name", ResourceFactory.getProperty("kq.data.sp.text.nosumbit"));
            }
        }
        return map;
    }
    @Override
    public HashMap<String, String> getKqDate(ArrayList<LazyDynaBean> listKq_extend) throws GeneralException {
        HashMap<String, String> map = new HashMap<String, String>();
        String Kq_year = "", Kq_duration = "";
        PeriodService periodService = new PeriodServiceImpl(this.getUserView(), this.getConnection());
        try {
            for (LazyDynaBean bean : listKq_extend) {
                if (!"06".equalsIgnoreCase(String.valueOf(bean.get("Sp_flag")))) {
                    map.put("kq_year", (String) bean.get("kq_year"));
                    map.put("kq_duration", (String) bean.get("kq_duration"));
                    return map;
                }
            }
            if (listKq_extend.size() > 0) {
                Kq_year = String.valueOf(listKq_extend.get(0).get("kq_year"));
                Kq_duration = String.valueOf(listKq_extend.get(0).get("kq_duration"));
            }

            ArrayList<LazyDynaBean> list = periodService.listKq_duration("", null, "kq_year,kq_duration DESC");
            if(list.size()>0){
                map.put("kq_year",String.valueOf((list.get(list.size()-1)).get("kq_year")));
                map.put("kq_duration",String.valueOf((list.get(list.size()-1)).get("kq_duration")));
            }else{
                //"请设置考勤期间！"
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.sp.msg.pleaseSetKqDuration")));
            }

            for (LazyDynaBean bean : list) {
                if (Kq_year.equalsIgnoreCase(String.valueOf(bean.get("kq_year"))) &&
                        Kq_duration.equalsIgnoreCase(String.valueOf(bean.get("kq_duration")))) {
                    map.put("kq_year", (String) bean.get("kq_year"));
                    map.put("kq_duration", (String) bean.get("kq_duration"));
                    return map;
                }
                if (StringUtils.isBlank(Kq_year) && StringUtils.isBlank(Kq_duration)) {
                    long kq_start = ((java.sql.Date) bean.get("kq_start")).getTime();
                    long kq_end = ((java.sql.Date) bean.get("kq_end")).getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    cal.set(Calendar.MILLISECOND,0);

                    if (kq_start <= cal.getTimeInMillis() && kq_end >= cal.getTimeInMillis()) {
                        map.put("kq_year", (String) bean.get("kq_year"));
                        map.put("kq_duration", (String) bean.get("kq_duration"));
                        return map;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }

    /**
     * 获取和自己有关的考勤方案
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 14:55 2018/12/17
     */
    @Override
    public ArrayList<HashMap<String, String>> listKqSchemeByMySelf() throws GeneralException {
        ArrayList<HashMap<String, String>> kqSchemeList = new ArrayList<HashMap<String, String>>();
        try {
            SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
            ArrayList parameterList = new ArrayList();
            String userA0100 = "";
            if (StringUtils.isNotBlank(this.getUserView().getA0100())) {
                userA0100 = this.getUserView().getDbname().toUpperCase() + this.getUserView().getA0100();
            }
            parameterList.add(this.getUserView().getUserName().toUpperCase());
            String sql = " and is_validate=1 and (upper(clerk_username) = ?";
            // 如果关联自助用户需要考虑是否为审核人的方案，一次都查出来
            if (StringUtils.isNotBlank(userA0100)) {
            	parameterList.add(userA0100);
            	sql += " or upper(reviewer_id) = ? ";
            }
            sql += ")";
            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(sql, parameterList, " scheme_id desc");
            /**
             * 经查 该参数是区分用户是作为该方案的考勤员sMap.put("isclerk", "1");
             * 还是审核人 parameterList.add(userA0100); and upper(reviewer_id) = ? 
             * sMap.put("isclerk", "0");
             * 但是不知道该参数哪里用到了  故先注释掉
             */
            for (LazyDynaBean bean : schemeList) {
                HashMap<String, String> sMap = new HashMap<String, String>();
                sMap.put("scheme_id", String.valueOf(bean.get("scheme_id")));
                sMap.put("name", String.valueOf(bean.get("name")));
                sMap.put("secondary_admin", String.valueOf(bean.get("secondary_admin")));
//                sMap.put("isclerk", "1");
                kqSchemeList.add(sMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return kqSchemeList;
    }

    /**
     * 新建考勤数据
     *
     * @param scheme_id   方案id
     * @param kq_year     考勤年度
     * @param kq_duration 考勤期间
     * @param orgList 创建机构id 多个逗号分隔
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 14:51 2018/11/5
     */
    @Override
    public void createNewKqData(String scheme_id, String kq_year, String kq_duration, List<String> orgList) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getConnection());
        KqDataUtil kqDataUtil = new KqDataUtil(userView);
        KqDataMxServiceImpl kqDataMxServiceImpl = new KqDataMxServiceImpl(userView,connection);
        try {
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> shemeBeanList = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "",kq_year,kq_duration);
            LazyDynaBean shemeBean=shemeBeanList.get(0);
            ArrayList org_scopeList =(ArrayList)shemeBean.get("org_map");
            ArrayList list = new ArrayList();
            for(String id : orgList){
                for (int i = 0; i < org_scopeList.size(); i++) {
                    HashMap map = (HashMap) org_scopeList.get(i);
                    if(id.equalsIgnoreCase(String.valueOf(map.get("org_id")))){
                        list.add(map);
                    }
                }
            }
            org_scopeList=list;
            String[] dbNameList = String.valueOf(shemeBean.get("cbase")).split(",");
            String cond = "Null".equalsIgnoreCase(String.valueOf(shemeBean.get("cond"))) ? "" : String.valueOf(shemeBean.get("cond"));
            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");//是否定义唯一性指标 0：没定义
            String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");//唯一性指标值

            StringBuffer strSql = new StringBuffer();
            KqPrivForHospitalUtil kqPrivForHospitalUtil=new KqPrivForHospitalUtil(this.getUserView(),this.getConnection());
            // 考勤开始结束日期 > “轮岗部门”>考勤参数之“考勤部门”>人员所在部门（E0122）
            // 优先级1 是否配置考勤开始结束日期
            // 考勤开始日期
            String kqStartDateField = kqPrivForHospitalUtil.getKqStartDateField();
            // 考勤结束日期
            String kqEndDateField = kqPrivForHospitalUtil.getKqEndDateField();
            KqUtil kqUtil = new KqUtil(this.connection);
    		// 考勤开始日期 所在表
    		String startSet = kqUtil.getTableNameByFieldName(kqStartDateField);
            // 考勤结束日期 所在表
            String endSet = kqUtil.getTableNameByFieldName(kqEndDateField);
            boolean hasStartDate = false;
            if(StringUtils.isNotBlank(kqStartDateField) && StringUtils.isNotBlank(startSet))
            	hasStartDate = true;
            boolean hasEndDate = false;
            if(StringUtils.isNotBlank(kqEndDateField) && StringUtils.isNotBlank(endSet))
            	hasEndDate = true;

            //优先级2 考勤部门变动子集职中设置的轮岗部门
            //轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            //轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            //轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            //轮岗结束时间
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            //是否配置了变动子集的变动部门
            boolean hasChangeSet = false;
            if(StringUtils.isNotBlank(changSetId) && StringUtils.isNotBlank(changDept))
                hasChangeSet = true;

            PeriodService periodService = new PeriodServiceImpl(userView,connection);
            parameterList.clear();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            ArrayList<LazyDynaBean> periods = periodService.listKq_duration(" and kq_year=? and kq_duration=?", parameterList, null);
            LazyDynaBean periodBean = periods.size()>0?periods.get(0):null;
            Date kqStart = null;
            Date kqEnd = null;
            Calendar kqStartCal =Calendar.getInstance();
            Calendar kqEndCal =Calendar.getInstance();
            if(periodBean!=null) {
                kqStart = (Date) periodBean.get("kq_start");
                kqEnd = (Date) periodBean.get("kq_end");
                kqStartCal.setTime(kqStart);
                kqEndCal.setTime(kqEnd);
            }
            //优先级3.考勤归属部门
            String dept_org = kqPrivForHospitalUtil.getKqDeptField();
            strSql.setLength(0);
            strSql.append(" insert into Q35(a0101,a0000,Kq_year,Kq_duration,Guidkey,Org_id,Scheme_id,B0110,E0122,E01a1,Confirm,Only_field)");
            strSql.append(" select a0101,$table$.a0000, ? as Kq_year,? as Kq_duration,$table$.Guidkey ,");
            strSql.append(" (CASE ");
            // 45421 变动部门不为空时，设置变动部门下的数据// 45851 isnull函数不兼容
            boolean isOracl = (Sql_switcher.searchDbServer() == Constant.ORACEL);
            if(hasChangeSet){
            	strSql.append(" WHEN (cset."+changDept+" is not null"+(isOracl?"":(" and cset."+changDept+"<>''"))+ ") THEN cset."+changDept);
            }
            if(StringUtils.isNotBlank(dept_org)){
                strSql.append(" WHEN ($table$."+dept_org+" is not null"+(isOracl?"":(" and $table$."+dept_org+"<>''"))+") THEN $table$."+dept_org);
            }
    		strSql.append(" WHEN ($table$.E0122 is null or $table$.E0122='') THEN $table$.B0110 ELSE $table$.E0122 END) "
            		+ "as Org_id,");
            strSql.append(" ? as Scheme_id,$table$.B0110,$table$.E0122,$table$.E01a1, null as Confirm,");
            //Only_field,)");
            if (StringUtils.isNotBlank(onlyname)) {
                strSql.append("$table$.").append(onlyname).append(" as Only_field ");
            } else {
                strSql.append("null").append(" as Only_field ");
            }
            parameterList.clear();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            parameterList.add(scheme_id);
            ArrayList<String> receiverList = new ArrayList<String>();
            String kqDate = kq_year+"-"+kq_duration;
            // 考勤部门或方案限制条件的SQL
            String condWhere = "";
            for (String dbName : dbNameList) {
                String tableName = dbName + "A01";
                for (int i = 0; i < org_scopeList.size(); i++) {
                    HashMap map = (HashMap) org_scopeList.get(i);
                    String org_id = String.valueOf(map.get("y_org_id"));

                    StringBuffer orgSql = new StringBuffer();
                    if(StringUtils.isNotBlank(dept_org)){
                        if ("un".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or ("+dept_org+" like '"+org_id.substring(2)+"%' ");
                            orgSql.append("or ( ("+dept_org+" is null or "+dept_org+"='') and ");
                            orgSql.append(" (B0110 like '"+org_id.substring(2)+"%' ");
                            orgSql.append("or E0122 like '"+org_id.substring(2)+"%' ) ");
                            orgSql.append(" )) ");
                        } else if ("um".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or ("+dept_org+" like '"+org_id.substring(2)+"%' ");
                            orgSql.append("or ( ("+dept_org+" is null or "+dept_org+"='') and ");
                            orgSql.append("E0122 like '"+org_id.substring(2)+"%' ");
                            orgSql.append(" )) ");
                        }

                    }else {
                        if ("un".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or (B0110 like '").append(org_id.substring(2)).append("%' ");
                            orgSql.append("or E0122 like '").append(org_id.substring(2)).append("%' ) ");
                        } else if ("um".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or E0122 like '").append(org_id.substring(2)).append("%' ");
                        }
                    }
                    // 变动子集的条件加入计算公式
                    String hasChangeSetsql = "";
                    if(hasChangeSet) {
                    	hasChangeSetsql = " or "+tableName+".a0100 in (SELECT a0100 FROM "+dbName+changSetId+" z1 ";
                    	hasChangeSetsql += " where ("+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM")+"<='"+kqDate+"'"
                					+ " and "+Sql_switcher.dateToChar("z1."+changeEndField,"yyyy-MM")+">='"+kqDate+"' "
                				+"or ((z1."+changeEndField+" is null or z1."+changeEndField+"='')"
                        			+ " and "+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM")+"<='"+kqDate+"')) ";
                    	hasChangeSetsql += " AND z1."+changDept+" LIKE '"+org_id.substring(2)+"%'";
                    	hasChangeSetsql += ")";
                    }
                    // 校验方案是否有限制条件的SQL
                    if (StringUtils.isNotBlank(cond)) {
                    	StringBuffer whereIN = new StringBuffer();
                    	whereIN.append("select ").append(tableName).append(".a0100 From ").append(tableName);
                        whereIN.append(" where 1=1 ").append("and (" + orgSql.toString().substring(2) + ")"+hasChangeSetsql);
                        condWhere = kqDataUtil.getComplexCondSql(this.connection, cond, dbName, whereIN.toString());
                    } else {
            			condWhere = " and "+orgSql.toString().substring(2)+hasChangeSetsql;
                    }

                    //如果存在考勤归属部门 则按照 归属部门-部门-单位 的顺序取值
                    StringBuffer sql = new StringBuffer(strSql.toString().replace("$table$", tableName));
                    sql.append(" from ").append(tableName);
                    //有变动子集的数据时，优先按照变动部门创建考勤数据
                    if(hasChangeSet){
                    	sql.append(" left join (SELECT a0100,"+changDept+",MAX("+changeStartField+") AS "+changeStartField
                        		+",MAX("+changeEndField+") as "+changeEndField);
                        sql.append(" FROM "+dbName+changSetId+" where (");
                        // 45851 日期校验
                        sql.append("("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDate+"' and ");
                        sql.append(Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+">='"+kqDate+"')");

                        sql.append("or ( ("+changeEndField+" is null or "+changeEndField+"='') and "
                        		+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDate+"')) ");
                        sql.append("and "+changDept+" like '"+org_id.substring(2)+"%'");
                        sql.append(" GROUP BY a0100,"+changDept+") cset");
                        sql.append(" on cset.a0100="+tableName+".a0100 ");
                        sql.append(" where ((cset."+changDept+" like '"+org_id.substring(2)+"%'"+condWhere+")");
                        sql.append(" or (1=1");
                    }else{
                        sql.append(" where 1=1 ");
                    }
                    // 考勤部门或方案限制条件的SQL
                    sql.append(condWhere);

                    if(hasChangeSet) {
                        sql.append(") )");
                    }
                    // 考勤开始结束时间校验
                    if(hasStartDate || hasEndDate) {
                    	sql.append(" and "+tableName+".A0100 not in (");
                    	if(hasStartDate) {
                    		sql.append("(select C.A0100 from " +dbName+startSet+ " C"
                    				+ " WHERE "+Sql_switcher.dateToChar("C."+kqStartDateField, "yyyy-MM")+ ">'"+kqDate+"'");
            				if (!"a01".equalsIgnoreCase(startSet))
                                sql.append(" and C.I9999=(select max(i9999) from " + dbName+startSet + " A WHERE C.A0100=A.A0100)");
            				sql.append(")");
                    	}
                    	if(hasStartDate && hasEndDate)
                    		sql.append(" UNION ");
                    	if(hasEndDate) {
                    		sql.append("(select D.A0100 from " +dbName+endSet+ " D"
                    				+ " WHERE "+Sql_switcher.dateToChar("D."+kqEndDateField, "yyyy-MM")+ "<'"+kqDate+"'");
            				if (!"a01".equalsIgnoreCase(endSet))
                                sql.append(" and D.I9999=(select max(i9999) from " + dbName+endSet + " A WHERE D.A0100=A.A0100)");
            				sql.append(")");
                    	}
                    	sql.append(")");
                    }

                    try {
                        dao.update(sql.toString(), parameterList);
                    } catch (SQLException e) {
                        String errMsg = "生成日明细发生错误！";
                        if (e.getMessage().contains("ORA-00001") || e.getMessage().contains("PRIMARY KEY")) {
                            errMsg = errMsg + "可能的原因：暂不支持上下级同时进行数据上报，人员重复生成失败！";
                        }
                        cat.error(errMsg);
                        e.printStackTrace();
                    }

                    //删除掉整个月都在别的部门轮岗的人员。
                    List delList = new ArrayList();
                    sql.setLength(0);
                    sql.append("delete from q35 where kq_year=? and kq_duration=? and scheme_id=? and org_id like ? and guidkey in(");
                    delList.add(kq_year);
                    delList.add(kq_duration);
                    delList.add(scheme_id);
                    delList.add(org_id.substring(2)+"%");
                    boolean needDel = false;
                    Map<String,List<String>> map1 = kqDataMxServiceImpl.searchChangePerData(kq_year,kq_duration,org_id.substring(2),scheme_id);
                    for(Map.Entry<String,List<String>> entry : map1.entrySet()){
                    	String guidkey = entry.getKey();
                        List<String> list1 = entry.getValue();
                        //说明这个人所有时间都在别的部门轮岗，那他的归属部门可以去掉他了。
                        if(list1.size()==0){
                            needDel = true;
                            sql.append("?,");
                            delList.add(guidkey);
                        }
                    }
                    if(needDel){
                       sql.setLength(sql.length()-1);
                       sql.append(")");
                       dao.delete(sql.toString(),delList);
                    }
                }
        	}

            /* //更新所属机构
            parameterList.clear();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            parameterList.add(scheme_id);
            for (int i = 0; i < org_scopeList.size(); i++) {
                HashMap map = (HashMap) org_scopeList.get(i);
                String org_id = String.valueOf(map.get("org_id"));
                dao.update("update q35 set org_id='" + org_id + "' where  Kq_year=? and Kq_duration=? and scheme_id=? and org_id like '" + org_id + "%' ", parameterList);
            }
            */
            //新建完数据后需要同步变动部门人员的数据
            if(hasChangeSet){
                kqDataUtil.syncchangeDeptKqData(this.connection,kq_year,kq_duration,scheme_id,null);
            }
            // linbz 优化流程 
            int role = kqDataUtil.getKqRole("1", shemeBean);
            // 应急中心个性化标识
            boolean hlwyjzx_flag = "hlwyjzx".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"));
            this.addNewKq_extend_log(scheme_id, kq_year, kq_duration, org_scopeList, dao, role, hlwyjzx_flag);
            /**
             * 个性化下发按钮
             */
            if(!hlwyjzx_flag) {
            	this.doDownward(scheme_id, kq_year, kq_duration, orgList, shemeBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 考勤数据报批
     *
     * @param viewType    页面区分 0:考勤上报 1:考勤审批
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id     所属机构id
     * @return
     * @author ZhangHua
     * @date 17:36 2018/11/6
     */
    @Override
    public boolean appealKqData(String viewType, String scheme_id, String kq_year, String kq_duration, String org_id
    		, JSONObject photo_info) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
        KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView());
        RowSet rs = null;
        try {
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
            int role = kqDataUtil.getKqRole(viewType, schemeList.get(0));
            LazyDynaBean nextRole = kqDataUtil.getKqRoleNextLevel(viewType, schemeList.get(0), role, org_id);
            // linbz 优化流程
            String nextRoleid = (String)nextRole.get("role");
            String nextUsername = (String)nextRole.get("username");
            /**
             * 机构审核人报批时  保存该审核人签章图片信息
             * 校验签章指标
             */
            DbWizard db = new DbWizard(this.connection);
            boolean bool = db.isExistField("kq_extend_log", "signature", false);
            String xml = "";
            // 是否更改标识
            boolean updateFlag = false; 
            if(KqDataUtil.role_Agency_Reviewer == role && "0".equals(viewType)
            		&& null != photo_info && bool) {
            	ArrayList<HashMap> infoList = new ArrayList<HashMap>();
            	HashMap map = new HashMap();
            	map.put("kq_user_id", role+"");
            	map.put("UserName", PubFunc.decrypt((String)photo_info.get("username")));
            	map.put("SignatureID", (String)photo_info.get("signatureID"));
            	map.put("MarkID", (String)photo_info.get("MarkID"));
            	infoList.add(map);
            	
            	xml = kqDataUtil.getSignatureIDXmlStr(infoList);
            	updateFlag = true; 
            }
            
            parameterList.clear();
            StringBuffer strSql = new StringBuffer();
            strSql.append("update kq_extend_log set Sp_flag='02'");
            if(updateFlag) {
            	strSql.append(",signature=?");
            	parameterList.add(xml);
            }
            strSql.append(",Curr_user=?,Appuser=Appuser").append(Sql_switcher.concat()).append("?");
            // 增加签章参数指标
            // linbz  Curr_user不保存用户名 只存身份常量标识
            parameterList.add(nextRoleid);
            parameterList.add(nextUsername + ";");

            strSql.append(" where org_id =? ");
            parameterList.add(org_id);
            strSql.append(" and Scheme_id=? and Kq_year=? and Kq_duration=? ");
            parameterList.add(scheme_id);
            parameterList.add(kq_year);
            parameterList.add(kq_duration);

            try {
            	dao.update(strSql.toString(), parameterList);
            }catch (Exception e) {
            	/**
            	 * 50789 防止Appuser审批流程多导致记录审批人过多，字段长度不够，则先从开始截取5个审批人
            	 */
            	ArrayList selectList = new ArrayList();
            	selectList.add(org_id);
            	selectList.add(scheme_id);
            	selectList.add(kq_year);
            	selectList.add(kq_duration);
            	rs = dao.search("select Appuser from kq_extend_log where org_id =? and Scheme_id=? and Kq_year=? and Kq_duration=? ", selectList);
            	if(rs.next()) {
            		String appuser = rs.getString("Appuser");
            		if(appuser.indexOf(";") != -1) {
            			String[] appusers = StringUtils.split(appuser, ";");
            			String appuserStr = "";
            			for(int i=0;i<5;i++) {
            				appuserStr += ";" + appusers[i];
            			}
            			String appuserNew = appuser.substring(appuserStr.length(), appuser.length());
            			selectList.add(0, appuserNew);
            			// 重新赋值Appuser
            			dao.update("update kq_extend_log set Appuser=? where org_id =? and Scheme_id=? and Kq_year=? and Kq_duration=? ", selectList);
            			// 增加当前Appuser记录
            			dao.update(strSql.toString(), parameterList);
            		}
            	}
            }

            //发送 待办
            StringBuffer title = new StringBuffer();
            //"年"
            title.append(kq_year).append(ResourceFactory.getProperty("label.query.year"));
            //"月"
            title.append(kq_duration).append(ResourceFactory.getProperty("label.query.month"));
            title.append(schemeList.get(0).get("name"));
            //待批
            title.append("({0})_").append(ResourceFactory.getProperty("kq.data.sp.text.pindingapproval"));
            ArrayList<String> receiverList = new ArrayList<String>();
            int taskType = role == KqDataUtil.role_Agency_Clerk ? KqDataUtil.TASKTYPE_FILL : KqDataUtil.TASKTYPE_SP;

            String orgName = AdminCode.getCodeName("UN", org_id);
            if (StringUtils.isBlank(orgName)) {
                orgName = AdminCode.getCodeName("UM", org_id);
            }
            String title_ = title.toString().replace("{0}", orgName);
            // 58614 调整顺序 先置为已办 //将登录人的待办置为已办
            receiverList.clear();
            if (role == KqDataUtil.role_Clerk || role == KqDataUtil.role_Agency_Clerk) {
            	receiverList.add(this.getUserView().getUserName());
            } else {
            	receiverList.add(this.getUserView().getDbname() + this.getUserView().getA0100());
            }
            
            kqDataUtil.kqFinishPengdingTask(this.getConnection(), Integer.parseInt(scheme_id), kq_year, kq_duration,
            		taskType, role, receiverList, org_id);

            receiverList.clear();
            // linbz 优化流程
            receiverList.add(nextUsername+"`"+nextRoleid);
            kqDataUtil.kqSendPengdingTask(this.getConnection(), title_, Integer.parseInt(scheme_id), kq_year, kq_duration
            		, org_id, KqDataUtil.TASKTYPE_SP, role, receiverList, schemeList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return false;
    }

    /**
     * 获取退回人员列表
     * @param scheme_id
     * @param org_id
     * @param viewType
     * @param resetFlag		重置标识=0重置，=其他为退回
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:38 2018/12/24
     */
    @Override
    public ArrayList<HashMap<String,String>> listrejectPer(String scheme_id, String org_id, String viewType
    		, String resetFlag) throws GeneralException {
        ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String, String>>();
        try{
            SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
            LazyDynaBean schemeBean=schemeList.get(0);
            ArrayList<HashMap> orgList=(ArrayList<HashMap>)schemeBean.get("org_map");
            HashMap orgMap=null;
            for (HashMap map : orgList) {
                String id=(String) map.get("org_id");
                if(org_id.equalsIgnoreCase(id)){
                    orgMap=map;
                    break;
                }
            }
            if(orgMap==null||orgMap.size()==0) {
                return dataList;
            }

            KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView());
            String role_id = String.valueOf(kqDataUtil.getKqRole(viewType, schemeBean));

            String reviewer_id=(String)schemeBean.get("reviewer_id");
            String clerk_username=(String)schemeBean.get("clerk_username");
            HashMap<String,String> map=null;

            // 重置目前直接退回到发起人
            if("0".equals(resetFlag)){
                map=new HashMap<String, String>();
                map.put("userid",(String) (orgMap.get("y_clerk_username")));
                map.put("role_id","3");
                map.put("name",orgMap.get("clerk_username")+" - "+orgMap.get("name"));
                dataList.add(map);
                return dataList;
            }

            if(StringUtils.isNotBlank(reviewer_id) && "2".equals(role_id)){
                map=new HashMap<String, String>();
                map.put("userid",clerk_username);
                // 这里退回的应该是人事处考勤员 故角色标识应该是1
                map.put("role_id","1");
                map.put("name",(String)schemeBean.get("clerk_fullname"));
                dataList.add(map);
            }

            if(orgMap.containsKey("reviewer_id")&&StringUtils.isNotBlank((String) (orgMap.get("reviewer_id")))
            		&& ",1,2,".contains(","+role_id+",")){
                map=new HashMap<String, String>();
                map.put("userid",(String) (orgMap.get("reviewer_id")));
                map.put("role_id","4");
                map.put("name",orgMap.get("reviewer")+" - "+orgMap.get("name"));
                dataList.add(map);
            }

            if(orgMap.containsKey("y_clerk_username")&&StringUtils.isNotBlank((String) (orgMap.get("y_clerk_username")))
            		&& ",1,2,4,".contains(","+role_id+",")){
                map=new HashMap<String, String>();
                map.put("userid",(String) (orgMap.get("y_clerk_username")));
                map.put("role_id","3");
                map.put("name",orgMap.get("clerk_username")+" - "+orgMap.get("name"));
                dataList.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return dataList;
    }


    /**
     * 驳回考勤数据
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_Id      所属机构id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 20:29 2018/11/7
     */
    @Override
    public boolean rejectKqData(String viewType, String scheme_id, String kq_year, String kq_duration, String org_Id, String user_id, String role_id) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView());
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
        if(StringUtils.isBlank(user_id)&&StringUtils.isBlank(role_id)){
            //驳回失败，请选择驳回人！
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.sp.msg.nobackuserid")));
        }

        ArrayList parameterList = new ArrayList();
        parameterList.add(scheme_id);
        ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
        org_Id = PubFunc.decrypt(org_Id);
        parameterList.clear();
        parameterList.add(scheme_id);
        parameterList.add(kq_year);
        parameterList.add(kq_duration);
        parameterList.add(org_Id+"%");
        String strSql = " And scheme_id=? and kq_year=? and kq_duration=? and Org_id like ?";
        RowSet rs = null;
        try {
        	// linbz 优化流程
        	LazyDynaBean schemeBean = schemeList.get(0);
            int role = kqDataUtil.getKqRole(viewType, schemeBean);
            ArrayList<LazyDynaBean> logList = this.listKq_extend_log(strSql, parameterList, "");
            String appusers = String.valueOf(logList.get(0).get("appuser"));
            String[] userList = appusers.split(";");
            if (userList.length < 2) {
                // "没有可以退回的数据"
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.sp.msg.cannotBack")));
            }
            // linbz 修改流程 退回到初始  从方案取最初的考勤员
            String appuser = "";
            if(String.valueOf(KqDataUtil.role_Agency_Clerk).equals(role_id)){
            	ArrayList<HashMap> listFillingAgencys = (ArrayList<HashMap>)schemeBean.get("org_map");
            	for (HashMap org : listFillingAgencys) {
            		String orgid_b = (String) org.get("org_id");
            		if(orgid_b.equalsIgnoreCase(org_Id)) {
            			appuser = (String) org.get("y_clerk_username");
            			break;
            		}
            	}
            }

            if(StringUtils.isBlank(appuser)) {
            	appuser = user_id;
            }
            /**
             * 如果退回到 机构考勤员或机构审核人 将审核人照片置空
             * 暂时这样处理
             */
            // 是否更改标识
            boolean updateFlag = false;
    		if(String.valueOf(KqDataUtil.role_Agency_Clerk).equals(role_id) || String.valueOf(KqDataUtil.role_Agency_Reviewer).equals(role_id)) {
    			DbWizard db = new DbWizard(this.connection);
    			updateFlag = db.isExistField("kq_extend_log", "signature", false); 
    		}
            
            strSql = "update Kq_extend_log set sp_flag='07'";
            if(updateFlag)
            	strSql += ",signature=?";
            strSql += ",curr_user=?,Appuser=Appuser"+Sql_switcher.concat()+"?"
            		+ " where scheme_id=? and kq_year=? and kq_duration=? and Org_id like ? ";
            parameterList.clear();
            if(updateFlag)
            	parameterList.add("");
            // 改为人员角色
            parameterList.add(role_id);
            parameterList.add(appuser+";");

            parameterList.add(scheme_id);
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            parameterList.add(org_Id+"%");
            
            /**
        	 * 50789 防止Appuser审批流程多导致记录审批人过多，字段长度不够，则先从开始截取5个审批人
        	 */
            int size = 0;
            try {
            	size = dao.update(strSql, parameterList);
            }catch (Exception e) {
            	ArrayList selectList = new ArrayList();
            	selectList.add(org_Id);
            	selectList.add(scheme_id);
            	selectList.add(kq_year);
            	selectList.add(kq_duration);
            	rs = dao.search("select Appuser from kq_extend_log where org_id =? and Scheme_id=? and Kq_year=? and Kq_duration=? ", selectList);
            	if(rs.next()) {
            		String appuserN = rs.getString("Appuser");
            		if(appuserN.indexOf(";") != -1) {
            			String[] appuserNs = StringUtils.split(appuserN, ";");
            			String appuserStr = "";
            			for(int i=0;i<5;i++) {
            				appuserStr += ";" + appuserNs[i];
            			}
            			String appuserNew = appuserN.substring(appuserStr.length(), appuserN.length());
            			selectList.add(0, appuserNew);
            			// 重新赋值Appuser
            			dao.update("update kq_extend_log set Appuser=? where org_id =? and Scheme_id=? and Kq_year=? and Kq_duration=? ", selectList);
            			// 增加当前Appuser记录
            			size = dao.update(strSql.toString(), parameterList);
            		}
            	}
            }
            if (size > 0) {
                //成功时 发送退回待办
                String orgName = AdminCode.getCodeName("UN", org_Id);
                if (StringUtils.isBlank(orgName)) {
                    orgName = AdminCode.getCodeName("UM", org_Id);
                }
                StringBuffer title = new StringBuffer();
                //"年"
                title.append(kq_year).append(ResourceFactory.getProperty("label.query.year"));
                //"月"
                title.append(kq_duration).append(ResourceFactory.getProperty("label.query.month"));
                title.append(schemeList.get(0).get("name"));
                //退回
                title.append("(" + orgName + ")_").append(ResourceFactory.getProperty("kq.data.sp.text.back"));
                ArrayList<String> receiverList = new ArrayList<String>();
                // 58614 调整顺序 先置为已办//将自己的待办置为已办
                if (role == KqDataUtil.role_Clerk) {
                	receiverList.add(this.getUserView().getUserName());
                } else {
                	receiverList.add(this.getUserView().getDbname() + this.getUserView().getA0100());
                }
                receiverList.add(this.getUserView().getUserName());
                kqDataUtil.kqFinishPengdingTask(connection, Integer.parseInt(scheme_id), kq_year, kq_duration, KqDataUtil.TASKTYPE_SP
                		, role, receiverList, org_Id);
                
                receiverList.clear();
                // 退回接收人特殊处理
                receiverList.add(appuser+"`"+role_id);
                kqDataUtil.kqSendPengdingTask(connection, title.toString(), Integer.parseInt(scheme_id), kq_year, kq_duration,
                        org_Id, KqDataUtil.TASKTYPE_SP_BACK, role, receiverList, schemeList.get(0));

                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 批准考勤数据
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param orgList     所属机构id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:00 2018/11/7
     */
    @Override
    public boolean approveKqData(String scheme_id, String kq_year, String kq_duration, ArrayList<String> orgList
    		, String viewType, JSONObject photo_info) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        ArrayList parameterList = new ArrayList();
        KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView());
        try {
            SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
            int role = kqDataUtil.getKqRole(viewType, schemeList.get(0));
            /**
             * 机构审核人直接同意时  保存该审核人签章图片信息
             * 校验签章指标
             */
            DbWizard db = new DbWizard(this.connection);
            boolean bool = db.isExistField("kq_extend_log", "signature", false);
            String xml = "";
            // 是否更改标识
            boolean updateFlag = false; 
            if(KqDataUtil.role_Agency_Reviewer == role && "0".equals(viewType)
            		&& null != photo_info && bool) {
            	ArrayList<HashMap> infoList = new ArrayList<HashMap>();
            	HashMap map = new HashMap();
            	map.put("kq_user_id", role+"");
            	map.put("UserName", PubFunc.decrypt((String)photo_info.get("username")));
            	map.put("SignatureID", (String)photo_info.get("signatureID"));
            	map.put("MarkID", (String)photo_info.get("MarkID"));
            	infoList.add(map);
            	
            	xml = kqDataUtil.getSignatureIDXmlStr(infoList);
            	updateFlag = true;
            }
            
            StringBuffer strSql = new StringBuffer("update Kq_extend_log set sp_flag='03' ");
            parameterList.clear();
            if(updateFlag) {
            	strSql.append(",signature=?");
            	parameterList.add(xml);
            }
            strSql.append(" where scheme_id=? and kq_year=? and kq_duration=? and org_id in (");
            
            parameterList.add(scheme_id);
            parameterList.add(kq_year);
            parameterList.add(kq_duration);

            for (String org : orgList) {
                strSql.append("?,");
                parameterList.add(PubFunc.decrypt(org));
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(")");

            if (dao.update(strSql.toString(), parameterList) > 0) {
                //将自己待批的待办置为已办
                ArrayList<String> receiverList = new ArrayList<String>();
                if (role == KqDataUtil.role_Clerk) {
                    receiverList.add(this.getUserView().getUserName());
                } else {
                    receiverList.add(this.getUserView().getDbname() + this.getUserView().getA0100());
                }
                for (String org : orgList) {
                    String org_Id = PubFunc.decrypt(org);
                    kqDataUtil.kqFinishPengdingTask(connection, Integer.parseInt(scheme_id), kq_year, kq_duration, KqDataUtil.TASKTYPE_SP, role, receiverList, org_Id);
                }

                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 创建默认的Kq_extend_log记录
     *
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param org_scopeList
     * @param dao
     * @throws GeneralException
     * @author ZhangHua
     * @date 17:26 2018/11/5
     */
    private void addNewKq_extend_log(String scheme_id, String kq_year, String kq_duration, ArrayList org_scopeList
    		, ContentDAO dao, int role, boolean hlwyjzx_flag) throws GeneralException {

        StringBuffer strSql = new StringBuffer();
        strSql.append("insert into Kq_extend_log(Scheme_id ,Kq_year ,Kq_duration ,Org_id ,Sp_flag ,Curr_user ,Appuser) values ");
        strSql.append("(?,?,?,?,?,?,?) ");
        ArrayList dataList = new ArrayList();
        // linbz 修改流程 Curr_user不保存用户名 只存身份常量标识   新建这里 默认为3  即机构考勤员
        try {
        	ArrayList list = new ArrayList();
        	HashMap map = new HashMap();
        	// 个性化  下发按钮
        	String spFlag = "01";
        	// 应急中心个性化标识
        	if(hlwyjzx_flag) {
        		spFlag = "08";
        	}
            for (int i = 0; i < org_scopeList.size(); i++) {

                map = (HashMap) org_scopeList.get(i);
                String Curr_user = String.valueOf(map.get("y_clerk_username"));
                String org_id = String.valueOf(map.get("org_id"));
                list = new ArrayList();
                list.add(scheme_id);
                list.add(kq_year);
                list.add(kq_duration);
                list.add(org_id);
                //Sp_flag
                list.add(spFlag);
                //Curr_user
                list.add("3");
                //Appuser
                list.add(";" + Curr_user + ";");

                dataList.add(list);
            }

            dao.batchUpdate(strSql.toString(), dataList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }


    private HashMap<String, Integer> mapQ35OrgCount(String Kq_year, String Kq_duration, String Scheme_id) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        RowSet rs = null;
        HashMap<String, Integer> dataMap = new HashMap<String, Integer>();
        try {

            String strSql = "select count(*) as num,Org_id from Q35 where Kq_year=? and Kq_duration=? and Scheme_id=? group by Org_id";
            ArrayList parameterList = new ArrayList();
            parameterList.add(Kq_year);
            parameterList.add(Kq_duration);
            parameterList.add(Scheme_id);

            rs = dao.search(strSql, parameterList);
            while (rs.next()) {
                String Org_id = rs.getString("Org_id");
                int num = rs.getInt("num");
                dataMap.put(Org_id, num);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return dataMap;
    }

    public KqDataSpServiceImpl(Connection connection, UserView userView) {
        this.connection = connection;
        this.userView = userView;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection conn) {
        this.connection = conn;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }


    @Override
    public boolean calculateMxData(String scheme_id_e, String kq_year,
                                   String kq_duration, String orgIds, int coverDataFlag)
            throws GeneralException {

        if (StringUtils.isEmpty(orgIds))
            return false;
        CallableStatement cstmt = null;
        SchemeMainService schemeService = new SchemeMainServiceImpl(connection, userView);
        KqItemService itemService = new KqItemServiceImpl(userView, connection);
        ContentDAO dao = new ContentDAO(connection);
        RowSet rs = null;
        try {
            /**
             * 1、首先调用存储过程计算当前考勤期间某数据上报机构 日明细打卡数据
             *kq_year：考勤年份；
             *kq_duration：考勤期间；
             *org_id：考勤数据上报单位，多个时以,分割
             *scheme_id：考勤方案
             *
             */
        	// 49985 配合存储过程更改为 一个机构处理一次
        	String[] orgIdArr = orgIds.split(",");

            String scheme_id = PubFunc.decryption(scheme_id_e);
            KqDataUtil kq = new KqDataUtil(this.userView);
			LazyDynaBean dateBane = kq.getDatesByKqDuration(this.connection, kq_year, kq_duration);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String startDateValue = df.format(dateBane.get("kq_start"));
			String endDateValue = df.format(dateBane.get("kq_end"));
			HashMap<String, HashMap<String, String>> importParamMap = this.getImportPatamMap();
            for(int i=0;i<orgIdArr.length;i++)
            	importData(orgIdArr[i], scheme_id, kq_year, kq_duration, startDateValue, endDateValue, importParamMap);
            //CalcKqDailyDetail是否存在，存在则执行
            StringBuffer sql = new StringBuffer();
            if (DbWizard.dbflag == Constant.ORACEL) {
                sql.append("select status from user_objects where object_type = 'PROCEDURE' and upper(object_name)='CALCKQDAILYDETAIL'");
            } else {
                sql.append("select * from dbo.sysobjects where id = object_id(N'[dbo].[CalcKqDailyDetail]') ");
                sql.append("and OBJECTPROPERTY(id, N'IsProcedure') = 1");
            }
            rs = dao.search(sql.toString());
            boolean flag = rs.next();
            if (flag) {
            	for(int i=0;i<orgIdArr.length;i++) {
            		cstmt = connection.prepareCall("{call CalcKqDailyDetail(?,?,?,?,?)}");
            		cstmt.setString(1, kq_year);
            		cstmt.setString(2, kq_duration);
            		cstmt.setString(3, orgIdArr[i]);
            		cstmt.setString(4, scheme_id);
            		cstmt.setInt(5, coverDataFlag);
            		//执行
            		cstmt.execute();
            		PubFunc.closeIoResource(cstmt);
            	}
            }
            //3、按考勤项目维护的计算顺序调用计算公式。
            //3.1 得到考勤方案下的已选考勤项目
            HashMap<String, String> schemeMap = schemeService.getSchemeDetailDataList(scheme_id_e);
            String itemIds = schemeMap.get("item_ids");
            sql.setLength(0);
            sql.append("seLect item_id from kq_item where lower(fielditemid) in ('q3533','q3535')");
            rs = dao.search(sql.toString());
            while(rs.next()){
            	String item_id = rs.getString("item_id");
            	if(!itemIds.contains(item_id))
            		itemIds+=","+item_id;
            }
            if (StringUtils.isNotBlank(itemIds)) {
                ArrayList<String> parameterList = new ArrayList<String>();
                StringBuffer sqlWherebuf = new StringBuffer();
                sqlWherebuf.append(" and item_id in (");
                String[] idArr = itemIds.split(",");
                for (int i = 0; i < idArr.length; i++) {
                    sqlWherebuf.append("?,");
                    parameterList.add(idArr[i]);
                }
                sqlWherebuf.setLength(sqlWherebuf.length() - 1);
                sqlWherebuf.append(")");
                sqlWherebuf.append(" and " + Sql_switcher.isnull( Sql_switcher.datalength("c_expr") ,"0")  +"> 0");//c_expr是text，用datalength查一下
                ArrayList<LazyDynaBean> kqItems = itemService.listKqItem(sqlWherebuf.toString(), parameterList, "computerorder asc");

                //3.2如果考勤项目下定义了计算公式则需要计算考勤项目的数据
                YksjParser yp = null;
                ArrayList fldvarlist = DataDictionary.getFieldList("Q35", Constant.USED_FIELD_SET);
                String[] orgArr = orgIds.split(",");
                // 多个机构计算SQL错误
                String pholder = orgArr.length>0?" and (":"";
                List orgValue = new ArrayList();
                for (String s : orgArr) {
                    pholder += " org_id like ? or";
                    orgValue.add(s+"%");
                }
                if(pholder.length()>0){
                    pholder = pholder.substring(0, pholder.length() - 2) + ")";
                }

                List<String> sqls = new ArrayList<String>();
                List<List> values = new ArrayList<List>();
                for (LazyDynaBean bean : kqItems) {
                    FieldItem item = DataDictionary.getFieldItem((String) bean.get("fielditemid"), "Q35");
                    if (item == null)
                        continue;
                    yp = new YksjParser(this.userView, fldvarlist,
                            YksjParser.forNormal, getDataType(item.getItemtype()), YksjParser.forPerson, null, "");
                    String formula = (String) bean.get("c_expr");
                    yp.run(formula, this.connection, "", "Q35");

                    String strexpr = yp.getSQL();

                    sql.setLength(0);
                    sql.append("update Q35");
                    sql.append(" set ");
                    sql.append(item.getItemid());
                    sql.append("=");
                    sql.append(strexpr);
                    sql.append(" where 1=1 and kq_year=? and kq_duration=? and scheme_id=? ");
                    if(pholder.length()>0) {
                        sql.append(pholder);
                    }
                    List param = new ArrayList();
                    param.add(kq_year);
                    param.add(kq_duration);
                    param.add(scheme_id);
                    param.addAll(orgValue);
                    sqls.add(sql.toString());
                    values.add(param);
                }
                //批量更新统计指标
                dao.batchUpdate(sqls, values);
            }
            
            /**
             * 4、同步该月汇总指标数据到其他轮岗部门
             */
            KqPrivForHospitalUtil kqPrivForHospitalUtil = new KqPrivForHospitalUtil(this.userView, this.connection);
            //轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            //轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            //轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            //轮岗结束时间
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            //是否配置了变动子集的变动部门
            boolean hasChangeSet = false;
            if(StringUtils.isNotBlank(changSetId) && StringUtils.isNotBlank(changDept))
                hasChangeSet = true;
            if(!hasChangeSet) {
            	return false;
            }
            // 获取考勤项目对应的统计指标
            StringBuffer fielditemids = new StringBuffer("");
            StringBuffer updateFields = new StringBuffer("");

            sql.setLength(0);
            sql.append("select fielditemid from kq_item");
            sql.append(" where fielditemid is not null");
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sql.append(" and fielditemid<>''");
            }
            rs = dao.search(sql.toString());
            while(rs.next()) {
            	String fielditemid = rs.getString("fielditemid");
            	if (StringUtils.isBlank(fielditemid))
            	    continue;

                // 有指标重复对应的情况，只更新一次即可
            	if (fielditemids.toString().contains("," + fielditemid))
            	    continue;

            	fielditemids.append(",").append(fielditemid);
            	updateFields.append(",").append(fielditemid).append("=b.").append(fielditemid).append("");
            }
            // 55150 没有设置考勤项目统计指标直接返回
            if(0 == fielditemids.toString().length() || 0 == updateFields.toString().length()) {
            	return false;
            }
            sql.setLength(0);
            String kqDate = kq_year +"-" +kq_duration;
            ArrayList<String> dbNames = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.connection);
            for(int i=0;i<dbNames.size();i++) {
            	String nbase = dbNames.get(i);
            	if(i > 0) {
                	sql.append(" UNION ");
                }
            	sql.append("select a.guidkey");
                sql.append(" from ").append(nbase).append(changSetId).append(" b");
                sql.append(" left join ").append(nbase).append("A01").append(" a");
                sql.append(" on a.a0100 = b.a0100");
                sql.append(" where "+Sql_switcher.dateToChar("b."+changeStartField, "yyyy-MM")+ "<='"+kqDate+"'"
                			+ " and "+Sql_switcher.dateToChar("b."+changeEndField, "yyyy-MM")+">='"+kqDate+"'");
                
            }
        	// 只获取目前要计算的部门中人员有轮岗的 guidkey
        	String sqlStr = "select guidkey from ("+ sql.toString() +") Q"
        			+ " where guidkey in(select guidkey from Q35 where scheme_id=? and kq_year=? and kq_duration=?"
        			+ " and (";//org_id like ?)
        	ArrayList value = new ArrayList();
        	value.add(scheme_id);
        	value.add(kq_year);
        	value.add(kq_duration);
        	for(int i=0;i<orgIdArr.length;i++) {
        		if(i > 0) {
        			sqlStr = sqlStr + " or";
        		}
        		sqlStr = sqlStr + " org_id like ?";
        		String orgid = orgIdArr[i];
        		value.add(orgid+"%");
        	}
        	sqlStr = sqlStr + "))";
        	ArrayList<String> guidkeyList = new ArrayList<String>();
            rs = dao.search(sqlStr, value);
            while(rs.next()) {
            	guidkeyList.add(rs.getString("guidkey"));
            }
            // 没有查到人员直接返回 不需要同步更新
            if(guidkeyList.size() == 0) {
            	return false;
            }
            boolean isOracl = (Sql_switcher.searchDbServer() == Constant.ORACEL);
            StringBuffer updateSql = new StringBuffer("");
            updateSql.append("update q35 set ")
            	.append(isOracl ? ("("+fielditemids.toString().substring(1)+")") : updateFields.toString().substring(1));
            updateSql.append(isOracl ? "=" : " from ").append("(select ").append(fielditemids.toString().substring(1));
            updateSql.append(" from q35 where scheme_id=? and guidkey=? and  kq_year=? and kq_duration=? and org_id like ?)").append(isOracl ? "" : " b");
            updateSql.append(" where guidkey=? and kq_year=? and kq_duration=? and org_id not like ?");
            // 多个机构循环同步
            List<String> sqls = new ArrayList<String>();
            List<List> values = new ArrayList<List>();
            for(int i=0;i<orgIdArr.length;i++) {
            	String orgid = orgIdArr[i];
            	for(int j=0;j<guidkeyList.size();j++) {
            		String guidkey = guidkeyList.get(j);
            		ArrayList valueList = new ArrayList();
            		valueList.add(scheme_id);
            		valueList.add(guidkey);
            		valueList.add(kq_year);
            		valueList.add(kq_duration);
            		valueList.add(orgid+"%");
            		valueList.add(guidkey);
            		valueList.add(kq_year);
            		valueList.add(kq_duration);
            		valueList.add(orgid+"%");
            		sqls.add(updateSql.toString());
            		values.add(valueList);
            	}
            }
            // 批量更新其他部门的轮岗人员的统计指标
            dao.batchUpdate(sqls, values);

        } catch (Exception e) {
        	e.printStackTrace();
        	// 如果计算失败输出日志信息
        	cat.error("考勤数据计算失败！");
//            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(cstmt);
            PubFunc.closeDbObj(rs);
        }
        return false;
    }

    /**
     * 归档考勤数据
     *
     * @param viewType    页面区分 0:考勤上报 1:考勤审批
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param isCover 是否覆盖前面的归档数据 true:覆盖以前的归档数据只保留最终归档的数据，false:则不覆盖
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:52 2018/11/16
     */
    @Override
    public void submitKqData(String viewType, String scheme_id, String kq_year, String kq_duration, String org_Id, boolean isCover) throws GeneralException {
        KqDataArchiveService kqDataArchiveService = new KqDataArchiveServiceImpl(this.getUserView(),this.getConnection());
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
        KqDataMxService kqDataMxService = new KqDataMxServiceImpl(this.getUserView(), this.getConnection());
        ContentDAO dao = new ContentDAO(this.getConnection());
        KqDataUtil kqDataUtil=new KqDataUtil(this.getUserView(), this.getConnection());
        RowSet rs=null;
        try {

            String strWhere = " and scheme_id=? ";
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> schemeBeanList = schemeMainService.listKq_scheme(strWhere, parameterList, "");
            LazyDynaBean schemeBean=schemeBeanList.get(0);
            //是否是由下级机构考勤员进行的提交 若不存在下级机构审核人 且考勤方案不需要人事处审核 则会出现此情况，此时 全部数据均可直接归档
            boolean clerkSubmit=false;
            int role=kqDataUtil.getKqRole(viewType,schemeBean);
            if(role==KqDataUtil.role_Agency_Clerk) {
                LazyDynaBean nextRole = kqDataUtil.getKqRoleNextLevel(viewType, schemeBean, role, org_Id);
                if(StringUtils.isBlank((String) nextRole.get("role"))){
                    clerkSubmit=true;
                }
            }
            String[] nbases = ((String) schemeBean.get("cbase")).split(",");

            HashMap archiveMap = kqDataArchiveService.getKqDataArchive();
            if (!archiveMap.containsKey("mapping_list") || ((ArrayList<HashMap>) archiveMap.get("mapping_list")).size() == 0) {
                //"请设置归档方案!"
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.sp.msg.pleaseSetKqArchive")));
            }
            // 54240 归档指标失效提示信息
            String messages = (String)archiveMap.get("messages");
            if (StringUtils.isNotBlank(messages)) {
            	throw GeneralExceptionHandler.Handle(new Exception(messages));
            }
            
            ArrayList<HashMap> mapping_list = (ArrayList<HashMap>) archiveMap.get("mapping_list");

            ArrayList<String> fromFieldNameList = new ArrayList<String>();
            ArrayList<String> toFieldNameList = new ArrayList<String>();
            String fieldsetid = (String) archiveMap.get("fieldsetid");
            for (HashMap map : mapping_list) {
                String fromField = "";
                if (map.containsKey("item_id") && StringUtils.isNotBlank((String) map.get("item_id"))) {
                    fromField = (String) map.get("item_id");
                }

                String toField = "";
                if (map.containsKey("to_item_id") && StringUtils.isNotBlank((String) map.get("to_item_id"))) {
                    toField = (String) map.get("to_item_id");
                }
                if (StringUtils.isNotBlank(fromField) && StringUtils.isNotBlank(toField)) {
                    fromFieldNameList.add(fromField);
                    toFieldNameList.add(toField.toLowerCase());
                }
            }
            
            if (fromFieldNameList.size() > 0) {
                fromFieldNameList.add("t_a0100");
                toFieldNameList.add("a0100");
                toFieldNameList.add("I9999");
                toFieldNameList.add("CreateTime");
                toFieldNameList.add("ModTime");
                toFieldNameList.add("CreateUserName");
                toFieldNameList.add("ModUserName");
                toFieldNameList.add(fieldsetid + "Z0");
                toFieldNameList.add(fieldsetid + "Z1");
            }
            
            /**
             * 获取每个机构 
             * 机构考勤员lower_clerk	 机构 审核人lower_reviewer
             * 方案考勤员 upper_clerk	 方案审核人 upper_reviewer
             */
            String upper_clerk = (String) schemeBean.get("clerk_fullname");
            String upper_reviewer = (String) schemeBean.get("reviewer_fullname");
            ArrayList<HashMap> orgMaps = (ArrayList<HashMap>) schemeBean.get("org_map");
            ArrayList<HashMap> orgNameMaps = new ArrayList<HashMap>();
            for(HashMap map : orgMaps) {
            	HashMap newMap = new HashMap();
            	newMap.put("org_id", (String)map.get("org_id"));
            	newMap.put("lower_clerk", (String)map.get("clerk_fullname"));
            	newMap.put("lower_reviewer", (String)map.get("reviewer"));
            	newMap.put("upper_clerk", upper_clerk);
            	newMap.put("upper_reviewer", upper_reviewer);
            	orgNameMaps.add(newMap);
            }
            
            StringBuffer strSql = new StringBuffer("insert into {table}");
            strSql.append(fieldsetid);
            strSql.append("(");

            for (String str : toFieldNameList) {
                strSql.append(str).append(",");
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(") select ");

            for (int i = 0; i < fromFieldNameList.size(); i++) {
                strSql.append(" ?,");
            }
            strSql.append(Sql_switcher.isnull("Max(I9999)", "0")).append("+1,");
            strSql.append(Sql_switcher.charToDate(Sql_switcher.dateToChar(Sql_switcher.sqlNow()))).append(",");
            strSql.append(Sql_switcher.charToDate(Sql_switcher.dateToChar(Sql_switcher.sqlNow()))).append(",");
            strSql.append("'" + this.getUserView().getUserName()).append("',");
            strSql.append("'" + this.getUserView().getUserName()).append("',");
            strSql.append(Sql_switcher.charToDate("?" + Sql_switcher.concat() + "'-'" + Sql_switcher.concat() + "?" + Sql_switcher.concat() + "'-01'")).append(",");
            strSql.append("? as z1");
            strSql.append(" from {table}");
            strSql.append(fieldsetid);
            strSql.append(" t where t.a0100=? ");
//            HashMap<String, LazyDynaBean> itemsMap = kqDataMxService.getClassAndItems(PubFunc.encrypt(scheme_id), "0");
            // 50233 防止轮岗方案不同设置的班次或项目不同，导致归档后出现代码，故这里获取全部班次和考勤项目
            HashMap<String, LazyDynaBean> itemsMap = kqDataUtil.getAllClassAndItems();
            //用于覆盖当前月份下的归档数据 haosl
            List delList = new ArrayList();
            //删除sql
            String delSql = "delete from {table}"+fieldsetid+" where a0100=? and "+Sql_switcher.dateToChar(fieldsetid+"Z0","yyyy-MM")+"=? and "+fieldsetid+"Z1 <?";
            for (String nbase : nbases) {
                HashMap<String, Integer> z1Map = this.listMaxZ1FromFieldSet(dao, fieldsetid, nbase, kq_year, kq_duration);
                
                ArrayList<ArrayList> sumbitDataList = new ArrayList<ArrayList>();
                ArrayList<HashMap> operDataList = new ArrayList<HashMap>();
                ArrayList<String> a0100List = new ArrayList<String>();
            	for(HashMap map : orgNameMaps) {
            		if(StringUtils.isNotBlank(org_Id)) {
            			if(org_Id.equalsIgnoreCase((String)map.get("org_id"))) {
            				sumbitDataList = this.listSubmitData(dao, scheme_id, kq_year, kq_duration, fromFieldNameList, nbase, org_Id
            						, itemsMap, clerkSubmit, map, sumbitDataList, operDataList, a0100List);
            			}
            		}else {
            			String orgid = (String)map.get("org_id");
            			if(StringUtils.isNotBlank(orgid))
	            			sumbitDataList.addAll(this.listSubmitData(dao, scheme_id, kq_year, kq_duration, fromFieldNameList, nbase, orgid
	                        		, itemsMap, clerkSubmit, map, sumbitDataList, operDataList, a0100List));
            		}
            	}
                
                for (ArrayList list : sumbitDataList) {
                    String a0100 = String.valueOf(list.get(list.size() - 1));
                    int z1 = 1;
                    if (z1Map.containsKey(a0100)) {
                        z1 = z1Map.get(a0100) + 1;
                    }
                    list.add(kq_year);
                    list.add(kq_duration);
                    list.add(z1);
                    list.add(a0100);
                    //无条件覆盖当前年月下的归档数据
                    if(isCover && z1>1){
                        List delValues = new ArrayList();
                        delValues.add(a0100);
                        delValues.add(kq_year+"-"+kq_duration);
                        delValues.add(z1);
                        delList.add(delValues);
                    }
                }
                /**
                 * 批量归档时 防止有员工轮岗由于机构考勤员与审核人不一样  导致归档两条
                 */
                for (int i=0;i<sumbitDataList.size();i++) {
                	ArrayList list = sumbitDataList.get(i);
                	HashMap map = operDataList.get(i);
                	for(int j=0;j<list.size();j++) {
                		Object object = (Object)list.get(j);
                		if(object instanceof String) {
							String value = (String) object;
							if("lower_clerk".equalsIgnoreCase(value)) {
								list.set(j, (String)map.get("lower_clerk"));
							}else if("lower_reviewer".equalsIgnoreCase(value)) {
								list.set(j, (String)map.get("lower_reviewer"));
							}else if("upper_clerk".equalsIgnoreCase(value)) {
								list.set(j, (String)map.get("upper_clerk"));
							}else if("upper_reviewer".equalsIgnoreCase(value)) {
								list.set(j, (String)map.get("upper_reviewer"));
							}
						}
                	}
                }

                try {
                	dao.batchUpdate(strSql.toString().replace("{table}", nbase), sumbitDataList);
                    //插入成功再删可能比较好
                    if(delList.size()>0){
                        dao.batchUpdate(delSql.replace("{table}", nbase),delList);
                    }
                }catch (Exception e) {
                	StringBuffer errorMsg = new StringBuffer();
                	for (HashMap map : mapping_list) {
                		String itemId = (String) map.get("item_id");
                		String toItemId = (String) map.get("to_item_id");
                		FieldItem fi = DataDictionary.getFieldItem(itemId, "Q35");
                		FieldItem toFi = DataDictionary.getFieldItem(toItemId, fieldsetid);
                		StringBuffer tempMsg = new StringBuffer();
                		if(!fi.getItemtype().equalsIgnoreCase(toFi.getItemtype()))
                			tempMsg.append(ResourceFactory.getProperty("kq.archive.scheme.itemTypeDiff"));
                		else if(!fi.getCodesetid().equalsIgnoreCase(toFi.getCodesetid()))
                			tempMsg.append(ResourceFactory.getProperty("kq.archive.scheme.codesetIdDiff"));
                		else if(fi.getItemlength() > toFi.getItemlength())
                			tempMsg.append(ResourceFactory.getProperty("kq.archive.scheme.itemLengthDiff"));
                		else if("N".equalsIgnoreCase(fi.getItemtype()) && fi.getDecimalwidth() > toFi.getDecimalwidth())
                			tempMsg.append(ResourceFactory.getProperty("kq.archive.scheme.decimalWidthDiff"));

                		if(StringUtils.isNotEmpty(tempMsg.toString())) {
                			tempMsg.insert(0, "【" + fi.getItemdesc() + "】" + ResourceFactory.getProperty("kq.archive.scheme.fileItem")
                			+ "【" + toFi.getItemdesc() + "】");
                			errorMsg.append(tempMsg);
                		}
                	}

                	if(StringUtils.isNotEmpty(errorMsg.toString())) {
                		errorMsg.insert(0, ResourceFactory.getProperty("kq.archive.scheme.fileErrorMsg"));
                		throw new GeneralException("", errorMsg.toString(), "", "");
                	}
				}
            }


            //更新状态
            strSql.setLength(0);
            strSql.append(" update kq_extend_log set Sp_flag='06' where scheme_id=? and kq_year=? and kq_duration=?  ");
            if(!clerkSubmit){
                strSql.append(" and Sp_flag='03' ");
            }
            parameterList.clear();
            parameterList.add(scheme_id);
            parameterList.add(kq_year);
            parameterList.add(kq_duration);

            if (StringUtils.isNotBlank(org_Id)) {
                strSql.append(" and org_Id=?");
                parameterList.add(org_Id);
            }
            dao.update(strSql.toString(), parameterList);


            /**
             * 所有数据都归档之后 删除所有考勤待办
             */
            strSql.setLength(0);
            strSql.append("select count(*) as num from kq_extend_log where scheme_id=? and kq_year=? and kq_duration=? and Sp_flag<>'06' ");
            parameterList.clear();
            parameterList.add(scheme_id);
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            rs=dao.search(strSql.toString(),parameterList);
            if(rs.next()){
                int num=rs.getInt("num");
                if(num==0){
                	// 51062 取消删除 kq_day_detail表中的数据  防止重置后计算时区分不了手工修改的数据
//                    this.cleanKqdetail(scheme_id,kq_year,kq_duration);
                    kqDataUtil.cleanKqPengdingTaskBySchemeId(this.getConnection(),scheme_id,kq_year,kq_duration,"");
                    kqDataUtil.cleanKqSysMessageBySchemeId(this.getConnection(),scheme_id,kq_year,kq_duration,null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取考勤员配置的通讯参数
     *
     * @param userName 业务用户账号
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 17:11 2018/11/22
     */

    @Override
    public HashMap<String, String> mapMsgConfig(String userName) throws GeneralException {
        HashMap<String, String> msgMap = new HashMap<String, String>(4);
        msgMap.put("phone", "");
        msgMap.put("wechat", "");
        msgMap.put("dingtalk", "");
        msgMap.put("email", "");
        msgMap.put("a0101", "");
        msgMap.put("allMsgConfig", "");
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.getConnection());
        try {
            //判断短信参数是否设置
            String mobile_field = this.getConstantField(1);
            //判断微信参数是否设置
            String corpid = ConstantParamter.getAttribute("wx", "corpid");
            //判断钉钉参数是否设置
            String dingTalk = ConstantParamter.getAttribute("DINGTALK", "corpid");
            //邮件服务器邮箱
            String systemEmailAddress = this.getConstantField(3);
            //个人邮箱
            String emailAddress = this.getConstantField(2);

            if (StringUtils.isBlank(mobile_field) && StringUtils.isBlank(corpid) && StringUtils.isBlank(dingTalk) &&
                    StringUtils.isBlank(systemEmailAddress) && StringUtils.isBlank(emailAddress)) {
            	// 58975 完善校验 如果都没有配置则 明确提示信息
            	msgMap.put("allMsgConfig", "-1");
                return msgMap;
            }
            StringBuffer strSql = new StringBuffer();
            ArrayList dataList = new ArrayList();


            String a0100 = "";
            HashMap<String, String> a0100Map = this.getUserA0100(userName);
            if (a0100Map.containsKey("a0100") && StringUtils.isNotBlank(a0100Map.get("a0100"))) {
                a0100 = a0100Map.get("a0100");
                msgMap.put("a0101", a0100Map.get("a0101"));
                msgMap.put("a0100", a0100Map.get("a0100"));
                msgMap.put("nbase", a0100Map.get("nbase"));
            }

            if (StringUtils.isNotBlank(a0100)) {
                dataList.add(a0100);
                strSql.append("select ");
                if (StringUtils.isNotBlank(emailAddress)) {
                    strSql.append(Sql_switcher.isnull(emailAddress, "''")).append(" as ").append(emailAddress).append(",");
                }
                
                if (StringUtils.isNotBlank(mobile_field)) {
                    strSql.append(Sql_switcher.isnull(mobile_field, "''")).append(" as ").append(mobile_field).append(",");
                }
                
                strSql.append(" a0100 from ").append(a0100Map.get("nbase")).append("A01 where ");
                strSql.append(" a0100 =?");
                rs = dao.search(strSql.toString(), dataList);
                if (rs.next()) {
                    if (StringUtils.isNotBlank(emailAddress)) {
                        if (StringUtils.isNotBlank(rs.getString(emailAddress)) && StringUtils.isNotBlank(systemEmailAddress)) {
                            msgMap.put("email", rs.getString(emailAddress));
                        }
                    }
                    if (StringUtils.isNotBlank(mobile_field)) {
                        if (StringUtils.isNotBlank(rs.getString(mobile_field))) {
                            msgMap.put("phone", rs.getString(mobile_field));
                        }
                    }
                    if (StringUtils.isNotBlank(dingTalk)) {
                        msgMap.put("dingtalk", dingTalk);
                    }
                    if (StringUtils.isNotBlank(corpid)) {
                        msgMap.put("wechat", corpid);
                    }
                }


            } else {
                dataList.add(userName);
                strSql.append(" select ").append(Sql_switcher.isnull("email", "''"));
                strSql.append(" as email,").append(Sql_switcher.isnull("phone", "''"));
                strSql.append(" as phone from OperUser where upper(UserName) =upper(?)");
                rs = dao.search(strSql.toString(), dataList);
                if (rs.next()) {
                    if (StringUtils.isNotBlank(rs.getString("email")) && StringUtils.isNotBlank(systemEmailAddress)) {
                        msgMap.put("email", rs.getString("email"));
                    }
                    if (StringUtils.isNotBlank(rs.getString("phone"))) {
                        msgMap.put("phone", rs.getString("phone"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return msgMap;
    }

    /**
     * 取系统的通信指标
     *
     * @param type 1电话指标 2邮件指标 3邮件服务器地址
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:48 2018/11/16
     */
    private String getConstantField(int type) throws GeneralException {
        try {
            String field = "";

            switch (type) {
                case 1:
                    field = "SS_MOBILE_PHONE";
                    break;
                case 2:
                    field = "SS_EMAIL";
                    break;
                case 3:
                    field = "SS_STMP_SERVER";
                    break;
            }

            RecordVo vo = ConstantParamter.getConstantVo(field);
            if (vo == null)
                return "";
            String field_name = vo.getString("str_value");
            if (field_name == null || "".equals(field_name))
                return "";

            if (type == 3) {

                Document doc = PubFunc.generateDom(field_name);
                Element root = doc.getRootElement();
                Element stmp = root.getChild("stmp");
                field_name = stmp.getAttributeValue("from_addr");

            } else {
                FieldItem item = DataDictionary.getFieldItem(field_name);
                if (item == null)
                    return "";
                /**分析是否构库*/
                if ("0".equals(item.getUseflag()))
                    return "";
            }
            return field_name;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取库中最大的z1
     *
     * @param dao
     * @param fieldSetId
     * @param dbName
     * @param kq_year
     * @param kq_duration
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:53 2018/11/17
     */
    private HashMap<String, Integer> listMaxZ1FromFieldSet(ContentDAO dao, String fieldSetId, String dbName, String kq_year, String kq_duration) throws GeneralException {

        HashMap<String, Integer> dataMap = new HashMap<String, Integer>();
        RowSet rs = null;
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append(" select a0100,Max(").append(fieldSetId).append("z1) as z1 ");
            strSql.append(" from ").append(dbName).append(fieldSetId);
            strSql.append(" where ").append(fieldSetId).append("z0=");
            strSql.append(Sql_switcher.charToDate("?" + Sql_switcher.concat() + "'-'" + Sql_switcher.concat() + "?" + Sql_switcher.concat() + "'-01'"));
            strSql.append(" group by a0100 ");
            ArrayList list = new ArrayList();
            list.add(kq_year);
            list.add(kq_duration);
            rs = dao.search(strSql.toString(), list);
            while (rs.next()) {
                dataMap.put(rs.getString("a0100"), rs.getInt("z1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataMap;

    }

    /**
     * 获取所有待提交的数据
     *
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param fieldNameList
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:53 2018/11/17
     */
    private ArrayList<ArrayList> listSubmitData(ContentDAO dao, String scheme_id, String kq_year, String kq_duration
    		, ArrayList<String> fieldNameList, String nbase, String org_Id, HashMap<String, LazyDynaBean> itemsMap
    		, boolean clerkSubmit, HashMap orgMap, ArrayList<ArrayList> sumbitDataList
    		, ArrayList<HashMap> operDataList, ArrayList<String> a0100List) throws GeneralException {
        RowSet rs = null;
        ArrayList<ArrayList> dataList = new ArrayList<ArrayList>();
        try {
            StringBuffer strSql = new StringBuffer("select q35.*,t.a0100 as t_a0100 from q35 ");
            strSql.append(" inner join kq_extend_log kqlog ");
            strSql.append(" ON q35.org_id like kqlog.Org_id"+Sql_switcher.concat()+"'%'");
            if(!clerkSubmit){
                strSql.append(" and kqlog.Sp_flag='03'");
            }
            strSql.append(" AND kqlog.Scheme_id = Q35.scheme_id ");
            strSql.append(" inner join ").append(nbase).append("A01 t ");
            strSql.append(" on t.guidkey=q35.guidkey ");
            strSql.append(" WHERE q35.kq_year=? AND q35.Kq_duration=? AND q35.scheme_id=? ");

            ArrayList parameterList = new ArrayList();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            parameterList.add(scheme_id);
            if (StringUtils.isNotBlank(org_Id)) {
                strSql.append(" and q35.org_id like ?");
                parameterList.add(org_Id+"%");
            }
            rs = dao.search(strSql.toString(), parameterList);
            while (rs.next()) {
                ArrayList list = new ArrayList();
                HashMap operMap = new HashMap();
                String a0100Str = "";
                for (String field : fieldNameList) {
                	/**
                	 * 归档特殊处理
                     * 机构考勤员lower_clerk	 机构 审核人lower_reviewer
                     * 方案考勤员 upper_clerk	 方案审核人 upper_reviewer
                     */
                	if("lower_clerk".equalsIgnoreCase(field)) {
                		list.add("lower_clerk");
                		operMap.put("lower_clerk", (String)orgMap.get("lower_clerk"));
                        continue;
                	}else if("lower_reviewer".equalsIgnoreCase(field)) {
                		list.add("lower_reviewer");
                		operMap.put("lower_reviewer", (String)orgMap.get("lower_reviewer"));
                        continue;
                	}else if("upper_clerk".equalsIgnoreCase(field)) {
                		list.add("upper_clerk");
                		operMap.put("upper_clerk", (String)orgMap.get("upper_clerk"));
                        continue;
                	}else if("upper_reviewer".equalsIgnoreCase(field)) {
                		list.add("upper_reviewer");
                		operMap.put("upper_reviewer", (String)orgMap.get("upper_reviewer"));
                        continue;
                	}
                	
                    String fieldType="A";
                    if(DataDictionary.getFieldItem(field)!=null){
                        fieldType=DataDictionary.getFieldItem(field).getItemtype();
                    }
                    if("D".equalsIgnoreCase(fieldType)){
                        list.add(rs.getTimestamp(field));
                        continue;
                    }
                    String value = rs.getString(field);
                    if (StringUtils.isBlank(value)) {
                        list.add(null);
                        continue;
                    }
                    if("t_a0100".equalsIgnoreCase(field)) {
                		a0100Str = value;
                	}
                    if (field.startsWith("q350") || field.startsWith("q351")
                            || field.startsWith("q352") || "q3530".equals(field) || "q3531".equals(field)) {
                        String[] values = value.toUpperCase().split(",");
                        StringBuffer newValue = new StringBuffer();
                        for (String str : values) {
                            if (itemsMap.containsKey(str)) {
                                LazyDynaBean bean = itemsMap.get(str);
                                if (str.startsWith("C")) {
                                    if (bean.get("abbreviation") == null || "".equals(String.valueOf(bean.get("abbreviation")))) {
                                        newValue.append(bean.get("name")).append(",");
                                    } else {
                                        newValue.append(bean.get("abbreviation")).append(",");
                                    }
                                } else if (str.startsWith("I")) {
                                    newValue.append(bean.get("item_name")).append(",");
                                }
                            } else {
                                newValue.append(str).append(",");
                            }
                        }
                        if (newValue.length() > 0) {
                            newValue.deleteCharAt(newValue.length() - 1);
                        }
                        value = newValue.toString();
                    }
                    list.add(value);
                }
                // 重复人目前只归档一条
                if(a0100List.contains(a0100Str)) 
                	continue;
                a0100List.add(a0100Str);
                // 50004 归档到子集如果有重复的只添加一条
                if(dataList.contains(list))
                	continue;
                // 53196 如果一个员工存在多个部门轮岗时  重复的只添加一条
                if(sumbitDataList.contains(list))
                	continue;
                
                dataList.add(list);
                operDataList.add(operMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }

    /**
     * 清除计算记录表
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:17 2018/12/10
     */
    private void cleanKqdetail(String scheme_id, String kq_year, String kq_duration) throws GeneralException {
        ContentDAO dao=new ContentDAO(this.getConnection());
        try{
            PeriodService periodService=new PeriodServiceImpl(this.getUserView(),this.getConnection());
            StringBuffer strSql=new StringBuffer();
            strSql.append(" And kq_year=? And kq_duration=? ");
            ArrayList parameterList=new ArrayList();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            ArrayList<LazyDynaBean> durationBean=periodService.listKq_duration(strSql.toString(),parameterList,null);
            if(durationBean.size()==0){
                return;
            }

            strSql.setLength(0);

            strSql.append("delete from kq_day_detail where scheme_id =? and kq_date>=? and kq_date <=? ");
            parameterList.clear();
            parameterList.add(scheme_id);
            parameterList.add(durationBean.get(0).get("kq_start"));
            parameterList.add(durationBean.get(0).get("kq_end"));
            dao.delete(strSql.toString(),parameterList);

        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 数值类型进行转换
     *
     * @param type
     * @return
     */
    private int getDataType(String type) {
        int datatype = 0;
        switch (type.charAt(0)) {
            case 'A':
                datatype = YksjParser.STRVALUE;
                break;
            case 'M':
                datatype = YksjParser.STRVALUE;
                break;
            case 'D':
                datatype = YksjParser.DATEVALUE;
                break;
            case 'N':
                datatype = YksjParser.FLOAT;
                break;
        }
        return datatype;
    }
    /**
     * 获取设置的导入指标参数
     * @return
     */
    private HashMap<String, HashMap<String, String>> getImportPatamMap (){
    	RowSet rs = null;
    	HashMap<String, HashMap<String, String>> importParamMap = new HashMap<String, HashMap<String, String>>();
		try {
			String sql = "select fielditemid,other_param from kq_item where "
					+ Sql_switcher.isnull("fielditemid", "'#'") + "<>'#'";
			ContentDAO dao = new ContentDAO(this.connection);
			rs = dao.search(sql);
			while (rs.next()) {
				String fieldItemId = rs.getString("fielditemid");
				if (StringUtils.isEmpty(fieldItemId))
					continue;

				String otherPatamXml = rs.getString("other_param");
				if (StringUtils.isEmpty(otherPatamXml))
					continue;

				Document doc = PubFunc.generateDom(otherPatamXml);
				Element root = doc.getRootElement();
				Element importEle = root.getChild("import");
				if (importEle == null)
					continue;

				String fieldSetId = importEle.getAttributeValue("subset");
				String importFieldItemId = importEle.getAttributeValue("field");
				if (StringUtils.isEmpty(fieldSetId) || "#".equals(fieldSetId)
						|| StringUtils.isEmpty(importFieldItemId) || "#".equals(fieldItemId))
					continue;

				String beginDate = importEle.getAttributeValue("begindate");
				beginDate = StringUtils.isEmpty(beginDate) || "#".equals(beginDate) ? "" : beginDate;
				String endDate = importEle.getAttributeValue("enddate");
				endDate = StringUtils.isEmpty(endDate) || "#".equals(endDate) ? "" : endDate;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("importFieldSetId", fieldSetId);
				map.put("importFieldItemId", importFieldItemId);
				map.put("beginDate", beginDate);
				map.put("endDate", endDate);
				importParamMap.put(fieldItemId, map);

			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return importParamMap;
    }
	/**
	 * 导入需要导入子集中指标的值
	 *
	 * @param org_id
	 *            组织机构编码
	 * @param scheme_id
	 *            考勤方案编号
	 * @param kq_year
	 *            考勤年
	 * @param kq_duration
	 *            考勤区间
	 */
	private void importData(String org_id, String scheme_id, String kq_year, String kq_duration,
			 String startDateValue, String endDateValue, HashMap<String, HashMap<String, String>> importParamMap) {
		RowSet rs = null;
		try {
			ArrayList<String> updateSqlList = new ArrayList<String>();
			for (Entry<String, HashMap<String, String>> entry : importParamMap.entrySet()) {
				String fieldItem = entry.getKey();
				HashMap<String, String> map = entry.getValue();
				String importSub = map.get("importFieldSetId");
				String importItem = map.get("importFieldItemId");
				String startDate = map.get("beginDate");
				String endDate = map.get("endDate");
				StringBuffer sqlBuff = new StringBuffer();
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
					sqlBuff.append("update q35 set " + fieldItem);
					sqlBuff.append("=(select " + importItem + " from {nbase}" + importSub + " " + importSub);

					if (!"A01".equals(importSub)) {
						sqlBuff.append(" right join {nbase}A01 a01");
						sqlBuff.append(" on " + importSub + ".a0100=a01.a0100");
						sqlBuff.append(" where a01.guidkey=q35.guidkey");
						sqlBuff.append(" and exists (select 1 from (");
						if (StringUtils.isNotEmpty(startDate))
							sqlBuff.append(" select DISTINCT Max(" + startDate + ") " + startDate);
						else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate))
							sqlBuff.append(" select DISTINCT Min(" + endDate + ") " + endDate);
						else
							sqlBuff.append(" select Max(i9999) i9999");

						sqlBuff.append(",a0100 from {nbase}" + importSub);
						sqlBuff.append(" where 1=1");
						if (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate)) {
							sqlBuff.append(" and " + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + endDateValue + "'");
						} else if(StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + startDateValue + "'");
						} else if(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
							sqlBuff.append(" and ((" + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + endDateValue + "'");
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + startDateValue + "')");
							sqlBuff.append(" or (" + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + startDateValue + "'");
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + startDateValue + "')");
							sqlBuff.append(" or (" + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + endDateValue + "'");
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + endDateValue + "'))");
						}

						sqlBuff.append(" group by a0100) temp where temp.a0100=" + importSub + ".a0100");
						if (StringUtils.isNotEmpty(startDate))
							sqlBuff.append(" and temp." + startDate + "=" + importSub + "." + startDate + ")");
						else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate))
							sqlBuff.append(" and temp." + endDate + "=" + importSub + "." + endDate + ")");
						else
							sqlBuff.append(" and temp.i9999=" + importSub + ".i9999)");

					} else
						sqlBuff.append(" where " + importSub + ".guidkey=q35.guidkey");

					sqlBuff.append(") where q35.scheme_id=? and q35.kq_year=?");
					sqlBuff.append(" and kq_duration=? and org_id like ?");
					sqlBuff.append(" and q35.guidkey in (select guidkey from {nbase}A01)");
				} else {
					sqlBuff.append("update q35 set " + fieldItem);
					sqlBuff.append("="+importSub+"." + importItem + " from {nbase}" + importSub + " " + importSub);

					if (!"A01".equals(importSub)) {
						sqlBuff.append(" right join {nbase}A01 a01");
						sqlBuff.append(" on " + importSub + ".a0100=a01.a0100");
						sqlBuff.append(" where a01.guidkey=q35.guidkey");
						sqlBuff.append(" and exists (select 1 from (");
						if (StringUtils.isNotEmpty(startDate))
							sqlBuff.append(" select DISTINCT Max(" + startDate + ") " + startDate);
						else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate))
							sqlBuff.append(" select DISTINCT Min(" + endDate + ") " + endDate);
						else
							sqlBuff.append(" select Max(i9999) i9999");

						sqlBuff.append(",a0100 from {nbase}" + importSub);
						sqlBuff.append(" where 1=1");
						if (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate)) {
							sqlBuff.append(" and " + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + endDateValue + "'");
						} else if(StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + startDateValue + "'");
						} else if(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
							sqlBuff.append(" and ((" + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + endDateValue + "'");
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + startDateValue + "')");
							sqlBuff.append(" or (" + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + startDateValue + "'");
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + startDateValue + "')");
							sqlBuff.append(" or (" + Sql_switcher.dateToChar(startDate, "yyyy-MM-dd"));
							sqlBuff.append("<='" + endDateValue + "'");
							sqlBuff.append(" and " + Sql_switcher.dateToChar(endDate, "yyyy-MM-dd"));
							sqlBuff.append(">='" + endDateValue + "'))");
						}

						sqlBuff.append(" group by a0100) temp where temp.a0100=" + importSub + ".a0100");
						if (StringUtils.isNotEmpty(startDate))
							sqlBuff.append(" and temp." + startDate + "=" + importSub + "." + startDate + ")");
						else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate))
							sqlBuff.append(" and temp." + endDate + "=" + importSub + "." + endDate + ")");
						else
							sqlBuff.append(" and temp.i9999=" + importSub + ".i9999)");

					} else
						sqlBuff.append(" where " + importSub + ".guidkey=q35.guidkey ");

					sqlBuff.append(" and q35.scheme_id=? and q35.kq_year=?");
					sqlBuff.append(" and kq_duration=? and org_id like ?");
					sqlBuff.append(" and q35.guidkey in (select guidkey from {nbase}A01)");
				}

				updateSqlList.add(sqlBuff.toString());
			}

			ArrayList<String> dbNames = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.connection);
			ArrayList<String> sqlList = new ArrayList<String>();
			ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(scheme_id);
			paramList.add(kq_year);
			paramList.add(kq_duration);
			paramList.add(org_id + "%");
			for (String nbase : dbNames) {
				for (String updateSql : updateSqlList) {
					sqlList.add(updateSql.replace("{nbase}", nbase));
					valueList.add(paramList);
				}
			}
			ContentDAO dao = new ContentDAO(this.connection);
			dao.batchUpdate(sqlList, valueList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 获取当前审批人
	 *
	 * @param bean
	 *            页面显示数据集合
	 * @param kqSchemeBean
	 *
	 * @param orgMap
	 *            考勤机构数据集合
	 * @return
	 */
	private String getApproveUser(LazyDynaBean bean, LazyDynaBean kqSchemeBean, HashMap orgMap, boolean haveSecondary, KqDataUtil kqDataUtil) {
		String approveUser = "";
		try {
			String sp_flag = String.valueOf(bean.get("sp_flag"));
			if("06".equals(sp_flag))
				return approveUser;
			if("03".equals(sp_flag))
				return kqDataUtil.getApproveUserFile(PubFunc.DynaBean2Map(kqSchemeBean), orgMap, haveSecondary, "1");

			String curr_user = String.valueOf(bean.get("curr_user"));
			if (curr_user.equals(String.valueOf(KqDataUtil.role_Agency_Clerk))) {
				approveUser = (String) orgMap.get("clerk_username");
				approveUser = StringUtils.isNotEmpty(approveUser)
						? approveUser.substring(approveUser.indexOf("(") + 1, approveUser.indexOf(")")) : "";
			} else if (curr_user.equals(String.valueOf(KqDataUtil.role_Agency_Reviewer)))
				approveUser = (String) orgMap.get("reviewer");
			else if (curr_user.equals(String.valueOf(KqDataUtil.role_Clerk)))
				approveUser = (String) kqSchemeBean.get("clerk_fullname");
			else if (curr_user.equals(String.valueOf(KqDataUtil.role_Reviewer)))
				approveUser = (String) kqSchemeBean.get("reviewer_fullname");
			else if (StringUtils.isNotEmpty(curr_user)) {
				if (curr_user.equals((String) orgMap.get("y_clerk_username"))) {
					approveUser = (String) orgMap.get("clerk_username");
					approveUser = StringUtils.isNotEmpty(approveUser)
							? approveUser.substring(approveUser.indexOf("(") + 1, approveUser.indexOf(")")) : "";
			    } else if (curr_user.equals((String) orgMap.get("reviewer_id")))
			    	approveUser = (String) orgMap.get("reviewer_fullname");
			    else if (curr_user.equals((String) kqSchemeBean.get("clerk_username")))
			    	approveUser = (String) kqSchemeBean.get("clerk_fullname");
			    else if (curr_user.equals((String) kqSchemeBean.get("reviewer_id")))
			    	approveUser = (String) kqSchemeBean.get("reviewer_fullname");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return approveUser;
	}
	
	@Override
    public void doReplaceConfirm(JSONObject jsonStrObject) throws GeneralException{
		try {
			String scheme_id = (String) jsonStrObject.get("scheme_id");
	        String kq_duration = (String) jsonStrObject.get("kq_duration");
	        String kq_year = (String) jsonStrObject.get("kq_year");
	        String org_id = (String) jsonStrObject.get("org_id");
	        String role_id = String.valueOf(jsonStrObject.get("role_id"));
	        JSONArray guidkeys = jsonStrObject.getJSONArray("guidkeys");
			if(guidkeys.size() < 1)
				return ;
			String guidkeyStrs = "";
			for (int i = 0; i < guidkeys.size(); i++) {
//				String guidkey = PubFunc.decrypt((String) guidkeys.get(i));
				guidkeyStrs += ",'"+(String) guidkeys.get(i)+"'";
			}
	        KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView(), this.connection);
	        ArrayList<String> recerverList = kqDataUtil.getNbaseA0100ByGuidkey(guidkeyStrs.substring(1));
	        kqDataUtil.kqFinishPengdingTask(this.connection, Integer.parseInt(PubFunc.decrypt(scheme_id)), kq_year, kq_duration
	        		, KqDataUtil.TASKTYPE_CONFIRM, Integer.parseInt(role_id), recerverList, PubFunc.decrypt(org_id));	        
		} catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	}
	
	@Override
    public void doDownward(String scheme_id, String kq_year, String kq_duration, List<String> orgList
			, LazyDynaBean shemeBean) throws GeneralException {
		ArrayList<String> parameterList = new ArrayList<String>();
		// 应急中心个性化标识
        boolean hlwyjzx_flag = "hlwyjzx".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"));
        String orgids = "";
		if(null == shemeBean) {
			SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getConnection(), this.getUserView());
			parameterList.add(scheme_id);
			ArrayList<LazyDynaBean> shemeBeanList = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "",kq_year,kq_duration);
            shemeBean = shemeBeanList.get(0);
            // 等于null说明只执行下发操作 需要将sp_flag  置为01
            RowSet rs = null;
            try {
            	String sql = "";
            	parameterList.clear();
        		parameterList.add(scheme_id);
        		parameterList.add(kq_year);
        		parameterList.add(kq_duration);
        		parameterList.add("08");
        		
            	ContentDAO dao = new ContentDAO(this.connection);
            	if(hlwyjzx_flag) {
            		sql = "select org_id from kq_extend_log where scheme_id=? and kq_year=? and kq_duration=? and sp_flag=?";
            		rs = dao.search(sql, parameterList);
            		while(rs.next()) {
            			orgids += rs.getString("org_id")+",";
            		}
            	}
            	sql = "update kq_extend_log set sp_flag='01' where scheme_id=? and kq_year=? and kq_duration=? and sp_flag=?";// and org_id=''
            	dao.update(sql, parameterList);
			} catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        PubFunc.closeDbObj(rs);
		    }
		}
		StringBuffer title = new StringBuffer();
    	//"年"
    	title.append(kq_year).append(ResourceFactory.getProperty("label.query.year"));
    	//"月"
    	title.append(kq_duration).append(ResourceFactory.getProperty("label.query.month"));
    	title.append(shemeBean.get("name"));
    	//_填报
    	title.append("({0})_").append(ResourceFactory.getProperty("kq.data.sp.text.report"));
    	KqDataUtil kqDataUtil = new KqDataUtil(userView);
    	ArrayList org_scopeList =(ArrayList)shemeBean.get("org_map");
    	ArrayList list = new ArrayList();
    	// 57004 下发无响应
    	if(null != orgList) {
    		for(String id : orgList){
    			for (int i = 0; i < org_scopeList.size(); i++) {
    				HashMap map = (HashMap) org_scopeList.get(i);
    				if(id.equalsIgnoreCase(String.valueOf(map.get("org_id")))){
    					list.add(map);
    				}
    			}
    		}
    		org_scopeList=list;
    	}
    	for (int i = 0; i < org_scopeList.size(); i++) {
    		HashMap map = (HashMap) org_scopeList.get(i);
    		String orgId = String.valueOf(map.get("org_id"));
    		// 应急中心个性化 下发
    		if(hlwyjzx_flag && !(","+orgids).contains(","+orgId+",")) {
    			continue;
    		}
    		String orgName = String.valueOf(map.get("name"));
    		String title_ = title.toString().replace("{0}", orgName);
    		parameterList.clear();
    		parameterList.add((String) map.get("y_clerk_username"));
    		kqDataUtil.kqSendPengdingTask(this.getConnection(), title_, Integer.parseInt(scheme_id), kq_year, kq_duration
    				, orgId, KqDataUtil.TASKTYPE_FILL, KqDataUtil.role_Clerk, parameterList, null);
    	}
	}
	@Override
	public void fillProcessMsg(String kq_year, String kq_duration, String scheme_id, String org_id, String sp_message,
			String sp_flag) throws GeneralException {
		ArrayList<String> parameterList = new ArrayList<String>();
		String sql="";
		RowSet rs = null;
		String sp_process="";
		sql = "select sp_process from kq_extend_log where scheme_id=? and kq_year=? and kq_duration=? and org_id=? ";
		try {
			parameterList.add(scheme_id);
			parameterList.add(kq_year);
			parameterList.add(kq_duration);
			parameterList.add(org_id);
			ContentDAO dao = new ContentDAO(this.connection);
			//查询出审批意见
			rs = dao.search(sql, parameterList);
			while(rs.next()) {
				sp_process = rs.getString("sp_process");
    		}
			Date nowDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String create_date = simpleDateFormat.format(nowDate);
			HashMap sp_processMap=new HashMap();
			sp_processMap.put("sp_flag", sp_flag);
			sp_processMap.put("opt_user", this.getUserView().getUserName());
			sp_processMap.put("opt_user_fullname",this.getUserView().getUserFullName() );
			sp_processMap.put("sp_message", sp_message);
			sp_processMap.put("opt_time", create_date);
			if (StringUtils.isNotBlank(sp_process)) {
				sp_process =sp_process+"`"+JSONObject.fromObject(sp_processMap).toString();
			}else {
				sp_process = JSONObject.fromObject(sp_processMap).toString();
			}
			//更新审批意见
			sql = "update kq_extend_log set sp_process=?  where scheme_id=? and kq_year=? and kq_duration=? and org_id=? ";
			parameterList.clear();
			parameterList.add(sp_process);
			parameterList.add(scheme_id);
			parameterList.add(kq_year);
			parameterList.add(kq_duration);
			parameterList.add(org_id);
			dao.update(sql, parameterList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			 PubFunc.closeDbObj(rs);
		}
	}
	@Override
	public List listProcessMsg(String kq_year, String kq_duration, String scheme_id, String org_id)
			throws GeneralException {
		ArrayList<String> parameterList = new ArrayList<String>();
		List listProcess = new ArrayList();
		List listProcessMsg = new ArrayList();
		String sql="";
		RowSet rs = null;
		String sp_process="";
		sql = "select sp_process from kq_extend_log where scheme_id=? and kq_year=? and kq_duration=? and org_id=? ";
		try {
			parameterList.add(scheme_id);
			parameterList.add(kq_year);
			parameterList.add(kq_duration);
			parameterList.add(org_id);
			ContentDAO dao = new ContentDAO(this.connection);
			//查询出审批意见
			rs = dao.search(sql, parameterList);
			while(rs.next()) {
				sp_process = rs.getString("sp_process");
    		}
			if (StringUtils.isNotBlank(sp_process)) {
				listProcess= Arrays.asList(sp_process.split("`"));
				if (listProcess.size()>0) {
					for (Object object : listProcess) {
						JSONObject map= JSONObject.fromObject(object);
						Map map2=new HashMap();
						map2.put("name", map.getString("opt_user_fullname"));
						map2.put("time", map.getString("opt_time"));
						String sp_message= map.getString("sp_message");
						sp_message=sp_message.replace( "\n", " <br />");
						map2.put("message", sp_message);
						if ("03".equals(map.getString("sp_flag"))) {
							map2.put("opinion", "0");
						}else if("07".equals(map.getString("sp_flag"))) {
							map2.put("opinion", "1");
						}
						listProcessMsg.add(map2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			 PubFunc.closeDbObj(rs);
		}
		return listProcessMsg;
	}
}
