package com.hjsj.hrms.module.gz.salarytype.businessobject;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytemplate.businessobject.ProcessMonitorBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.NumberUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ApplicationOrgBo {

    private Connection conn = null;
    private UserView userView = null;
    private String salaryid = "";

    public ApplicationOrgBo(Connection conn, String salaryid, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.salaryid = salaryid;
    }

    /**
     * 添加资源权限范围
     *
     * @param fullname
     * @param salaryid
     * @param isShare是否共享，共享的
     */
    @SuppressWarnings("unused")
    public void resetResource(String gz_module, boolean isShare, MorphDynaBean bean) {
        String a0100 = bean.get("a0100") == null ? "" : PubFunc.decrypt((String) bean.get("a0100"));
        String username = bean.get("username") == null ? "" : (String) bean.get("username");
        String org_id = bean.get("org_id") == null ? "" : (String) bean.get("org_id");
        StringBuffer org_idBuffer = new StringBuffer();
        String[] org_idArray = org_id.split(",");
        for (int j = 0; j < org_id.split(",").length; j++) {
            org_idBuffer.append("," + org_idArray[j].substring(0, 2) + PubFunc.decrypt(org_idArray[j].substring(2)));
        }
        // 0 业务用户 1 角色  4 自助用户
        String role_id = "";
        String flag = "";
        int res_type = 12;//12:薪资，18：保险
        if (isShare) {//自助用户，
            role_id = a0100;
            flag = "4";
        } else {
            role_id = username;
            flag = "0";
        }
        if ("1".equals(gz_module)) {
            res_type = 18;
        }

        try {
        	//保存资源权限
        	saveResource(role_id, flag, res_type);
            //保存业务范围
            if(StringUtils.isNotBlank(role_id))
            	saveManageRange(role_id, flag, org_idBuffer.length() > 0 ? org_idBuffer.substring(1) : "","add");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    /**
     * 保存资源权限
     * @param role_id
     * @param flag 0:业务用户，1：角色，4：自助用户
     * @param res_type 12:薪资，18：保险
     * @throws GeneralException
     */
    public void saveResource(String role_id,String flag,int res_type) throws GeneralException {
    	SysPrivBo privbo = new SysPrivBo(role_id, flag, this.conn, "warnpriv");
        String res_str = privbo.getWarn_str();//取这个人的所有资源权限
        ResourceParser parser = new ResourceParser(res_str, res_type);
        Document doc = null;
        StringBuffer strxml = new StringBuffer();
        String gz_set = "";
        String ins_set = "";
        try {
            strxml.append("<?xml version='1.0' encoding='GB2312'?>");
            strxml.append("<resource>");
            strxml.append(res_str);
            strxml.append("</resource>");
            doc = PubFunc.generateDom(strxml.toString());
            if (res_type == 12) {
                XPath xPath = XPath.newInstance("/resource/gz_set");
                List list = xPath.selectNodes(doc);
                if (list != null && list.size() > 0) {
                    Element element = (Element) list.get(0);
                    if (element != null)
                        gz_set = "," + element.getText() + ",";
                }
            } else {
                XPath xPath = XPath.newInstance("/resource/ins_set");
                @SuppressWarnings("rawtypes")
                List list = xPath.selectNodes(doc);
                if (list != null && list.size() > 0) {
                    Element element = (Element) list.get(0);
                    if (element != null)
                        ins_set = "," + element.getText() + ",";
                }
            }
            if (!(gz_set.contains("," + this.salaryid + ","))) {
                gz_set = gz_set + this.salaryid + ",";
                if (res_type == 12) {
                    if (gz_set.length() != 0)
                        gz_set = gz_set.substring(0, gz_set.length());
                    parser.reSetContent(gz_set);
                } else {
                    if (ins_set.length() != 0)
                        ins_set = ins_set.substring(0, ins_set.length());
                    parser.reSetContent(ins_set);
                }

                res_str = parser.outResourceContent();
                if(StringUtils.isNotBlank(role_id))
                	privbo.saveResourceStringSql(role_id, flag, res_str);
            }
        }catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    /**
     * 保存业务范围
     *
     * @param role_id
     * @param userflag// 0 业务用户 1 角色  4 自助用户
     * @param org_id
     */
    public void saveManageRange(String role_id, String userflag, String org_id,String flag) {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            if ("4".equals(userflag)) {
            	if(StringUtils.isNotBlank(role_id)) {
	                RecordVo vo = new RecordVo("t_sys_function_priv");
	                vo.setString("id", role_id);
	                vo.setString("status", userflag);
	                boolean isExist = dao.isExistRecordVo(vo);
	                if(isExist) {
		                RecordVo vo1 = dao.findByPrimaryKey(vo);
		
		                vo.setString("busi_org_dept", settingOrgManage(1, org_id, vo1.getString("busi_org_dept"), flag));
		                SysPrivBo sysbo = new SysPrivBo(vo, this.conn);
		                sysbo.save();
	                }
            	}
            } else {
            	if(StringUtils.isNotBlank(role_id)) {
	                RecordVo vo = new RecordVo("operuser");
	                vo.setString("username", role_id);
	                boolean isExist = dao.isExistRecordVo(vo);
	                if(isExist) {
		                RecordVo vo1 = dao.findByPrimaryKey(vo);
		                //超级管理员不需要设置业务权限
		                if(!(vo1.getInt("groupid") == 1)) {
		                	vo.setString("busi_org_dept", settingOrgManage(1, org_id, vo1.getString("busi_org_dept"), flag));
		                	dao.updateValueObject(vo);
		                }
	                }
            	}
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加业务范围，如果没有，新增，如果有添加
     *
     * @param module1=工资发放         2=工资总额 3=所得税管理 4=组织机构 5=绩效管理 6=培训管理 7=招聘管理 8=业务模板 9=职称评审
     * @param org_id
     * @param busi_org_dept原先库中的权限
     * @return
     */
    private String settingOrgManage(int module, String org_id, String busi_org_dept,String flag) {
        StringBuffer busi_org = new StringBuffer();
        if (StringUtils.isBlank(busi_org_dept)) {
            for (int i = 1; i < 10; i++) {
                if (i != 1)
                    busi_org.append("|");

                busi_org.append(i + ",");
                if (i == module)
                    busi_org.append(org_id + "`");
            }
        } else {
            String[] str = busi_org_dept.split("\\|");
            for (int i = 1; i <= str.length; i++) {//1,UNxxx`UM000`
                if (i != 1)
                    busi_org.append("|");
                
                String[] strs = str[i-1].split(",");
                if (i == module) {
                	if((strs.length > 1)) {
	                    String[] arr = strs[1].split("`");
	                    boolean isHaveSame = false;
	                    for (int j = 0; j < arr.length; j++) {
	                        if (arr[j].equalsIgnoreCase(org_id)) {
	                            isHaveSame = true;
	                            break;
	                        }
	                    }
	                    if(isHaveSame && "update".equalsIgnoreCase(flag)) {
	                    	String old = strs[1];
	                    	old = old.replace(org_id + "`", "");
	                    	busi_org.append(strs[0] + "," + old);
	                    }
	                    if (!isHaveSame && !"update".equalsIgnoreCase(flag))
	                        busi_org.append(strs[0] + "," + strs[1] + org_id + "`");
	                    else if(!"update".equalsIgnoreCase(flag))
	                    	busi_org.append(str[i-1]);
                	}else if("update".equalsIgnoreCase(flag)){
                		busi_org.append(str[i-1]);
                	}else if(!"update".equalsIgnoreCase(flag)){
                		busi_org.append(str[i-1] + org_id + "`");
                	}
                }else {
                	busi_org.append(str[i-1]);
                }
            }
        }

        return busi_org.toString();
    }

    /**
     * 通过用户名获取nbase+a0100
     *
     * @param tabList
     * @param loginNameField
     * @param username
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getA0100ByUsername(String username,String gz_moudle) {
        String a0100 = username;
        ArrayList<String> list = new ArrayList<String>();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String sql = "";
        try {
        	ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.conn,this.userView,gz_moudle,salaryid);
        	ArrayList dbnameList = DataDictionary.getDbpreList();
            for (int i = 0; i < dbnameList.size(); i++) {
            	list = new ArrayList<String>();
            	String dbnamei = String.valueOf(dbnameList.get(i));
                
                String logonItem = processMonitorBo.getUsernameItem();
                sql = "select a0100 from " + dbnamei + "A01 where " + logonItem + "=?";
                list.add(username);
                rs = dao.search(sql, list);
                if (rs.next()) {
                    a0100 = dbnamei + rs.getString("a0100");
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                PubFunc.closeDbObj(rs);
            }
        }
        return a0100;
    }

    /**
     * 判断在gz_reporting_log表中是否存在对应的数据，如果不存在从Salarytemplate中ctrl_param取数据
     *
     * @return
     */
    public ArrayList<LazyDynaBean> gzReportingLogData(String a00z2,String gz_moudle) {
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        StringBuffer sql = new StringBuffer();
        try {
        	sql.append("select id,Start_date,End_date,B0110,Username,Fullname,Enable from gz_reporting_log where salaryid=?");
        	list.add(Integer.parseInt(this.salaryid));
        	if (StringUtils.isNotBlank(a00z2) && a00z2.split("-").length < 3) {
                a00z2 = a00z2 + "-01";
            }
        	if(StringUtils.isNotBlank(a00z2)) {
        		sql.append(" and a00z2=" + Sql_switcher.dateValue(a00z2));
        	}
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                String org_id = rs.getString("B0110");
                String enable = rs.getString("Enable");
                HashMap<String, String> map = getOrgInfo(org_id);
                String username = StringUtils.isNotBlank(rs.getString("Username"))?rs.getString("Username"):"";
                abean.set("id", rs.getString("id"));
                abean.set("enable", enable);
                abean.set("org_id", map.get("codesetid"));
                abean.set("org_name", map.get("codeitemdesc"));
                abean.set("fullname", StringUtils.isNotBlank(rs.getString("Fullname"))?rs.getString("Fullname"):"");
                abean.set("username", username);
                abean.set("start_date", rs.getString("Start_date"));
                abean.set("end_date", rs.getString("End_date"));
                abean.set("isAppli", "1".equals(enable)?AdminCode.getCodeName("45", "1"):AdminCode.getCodeName("45", "2"));
                abean.set("a0100", PubFunc.encrypt(getA0100ByUsername(username, gz_moudle)));
                dataList.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                PubFunc.closeDbObj(rs);
            }
        }
        return dataList;
    }

    @SuppressWarnings("rawtypes")
    public ArrayList getCtrlXmlAgencyList(ArrayList<LazyDynaBean> dataList, List elementList, ArrayList dbnameList, String loginNameField, String gz_moudle, String type) {
        Element element = null;
        ArrayList<LazyDynaBean> dataLists = new ArrayList<LazyDynaBean>();
        try {
        	if(dataList == null || dataList.size() == 0) {
        		for (int i = 0; i < elementList.size(); i++) {
	        		element = (Element) elementList.get(i);
	                LazyDynaBean abean = new LazyDynaBean();
	                String org_id = StringUtils.isBlank(element.getAttributeValue("org_id")) ? "" : element.getAttributeValue("org_id");
	                String enable = StringUtils.isBlank(element.getAttributeValue("enable")) ? "" : element.getAttributeValue("enable");
	                if("1".equals(type) && "0".equals(enable))
	                	continue;
	                HashMap<String, String> map = getOrgInfo(org_id);
	                String username = StringUtils.isBlank(element.getAttributeValue("username")) ? "" : element.getAttributeValue("username");
	                abean.set("enable", "0".equals(type)?(StringUtils.isBlank(element.getAttributeValue("enable")) ? "" : element.getAttributeValue("enable")):"0");
	                abean.set("org_id", map.get("codesetid"));
	                abean.set("org_name", map.get("codeitemdesc"));
	                abean.set("fullname", StringUtils.isBlank(element.getAttributeValue("fullname")) ? "" : element.getAttributeValue("fullname"));
	                abean.set("username", username);
	                abean.set("isAppli", AdminCode.getCodeName("45", "2"));
	                abean.set("a0100", PubFunc.encrypt(getA0100ByUsername(username, gz_moudle)));
	                dataLists.add(abean);
        		}
        	}else {
	            for (int i = 0; i < elementList.size(); i++) {
	            	boolean flag = false;
	            	element = (Element) elementList.get(i);
	            	String username = StringUtils.isBlank(element.getAttributeValue("username")) ? "" : element.getAttributeValue("username");
	            	String org_id = StringUtils.isBlank(element.getAttributeValue("org_id")) ? "" : element.getAttributeValue("org_id");
	            	String enable = StringUtils.isBlank(element.getAttributeValue("enable")) ? "" : element.getAttributeValue("enable");
	            	String org_idReportLog = "";
	            	String usernameReportLog = "";
	            	for(int j = 0; j < dataList.size(); j++) {
	            		LazyDynaBean data = dataList.get(j);
	            		usernameReportLog = StringUtils.isBlank((String)data.get("username")) ? "" :(String)data.get("username");
	            		org_idReportLog = StringUtils.isBlank((String)data.get("org_id")) ? "" :(String)data.get("org_id");
	            		org_idReportLog = org_idReportLog.substring(0, 2) + PubFunc.decrypt(org_idReportLog.substring(2));
	            		if(org_id.equalsIgnoreCase(org_idReportLog) && username.equals(usernameReportLog)) {
	            			dataLists.add(data);
	            			flag = true;
	            			break;
	            		}
	            	}
	            	if(!flag) {
		            	LazyDynaBean abean = new LazyDynaBean();
		            	if("1".equals(type) && "0".equals(enable))
		                	continue;
		            	HashMap<String, String> map = getOrgInfo(org_id);
		            	abean.set("enable", "0");
		            	abean.set("org_id", map.get("codesetid"));
		            	abean.set("org_name", map.get("codeitemdesc"));
		            	abean.set("fullname", StringUtils.isBlank(element.getAttributeValue("fullname")) ? "" : element.getAttributeValue("fullname"));
		            	abean.set("username", username);
		            	abean.set("isAppli", AdminCode.getCodeName("45", "2"));
		            	abean.set("a0100", PubFunc.encrypt(getA0100ByUsername(username, gz_moudle)));
		            	dataLists.add(abean);
	            	}
	            }
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataLists;
    }

    /**
     * 根据指标获取
     *
     * @param codeitemid
     * @param searchField
     * @return
     */
    private HashMap<String, String> getOrgInfo(String codeitemids) {
        HashMap<String, String> map = new HashMap<String, String>();
        StringBuffer codesetid = new StringBuffer();
        StringBuffer codeitemdescid = new StringBuffer();
        try {
            String[] codeitemidArray = codeitemids.split(",");
            for (int i = 0; i < codeitemidArray.length; i++) {
                codesetid.append("," + codeitemidArray[i].substring(0, 2) + PubFunc.encrypt(codeitemidArray[i].substring(2)));
                codeitemdescid.append("," + AdminCode.getCodeName(codeitemidArray[i].substring(0, 2), codeitemidArray[i].substring(2)));
            }
            map.put("codesetid", codesetid.substring(1));
            map.put("codeitemdesc", codeitemdescid.substring(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 保存或者根据gz_reporting_log表的数据
     * @param dataList
     * @param a00z2
     * @param a00z3
     * @param flag
     */
    public void saveDataToGZReportingLog(ArrayList<MorphDynaBean> dataList, String a00z2, String a00z3, String flag,String gz_module) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<ArrayList<String>>();
        ContentDAO dao = new ContentDAO(this.conn);
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        a00z2 = PubFunc.decrypt(SafeCode.decode(a00z2));
        if (StringUtils.isNotBlank(a00z2) && a00z2.split("-").length < 3) {
            a00z2 = a00z2 + "-01";
        }
        try {
        	//去除不在薪资类别应用机构的人
        	checkDataInOrg( gz_module, a00z2);
        	
        	java.sql.Date date = new java.sql.Date(sim.parse(a00z2).getTime());
        	String insertSql = "insert into gz_reporting_log(id,Salaryid,A00Z2,A00Z3,Start_date,End_date,b0110,SP_FLAG,Username,Fullname,Enable) values (?,?,?,?,?,?,?,?,?,?,?) ";
        	for (int i = 0; i < dataList.size(); i++) {
        		ArrayList list = new ArrayList();
        		MorphDynaBean bean = dataList.get(i);
        		String id = bean.get("id")==null?"":(String) bean.get("id");
        		//可能有的是新增的，有的是原先就有的，这样根据id进行判断就行
	            if (!NumberUtils.isNumber(id)) {//不为空的时候添加
                    String org_id = bean.get("org_id") == null ? "" : (String) bean.get("org_id");
                    String[] org_idArray = org_id.split(",");
                    String org_idBuffer = "";
                    for (int j = 0; j < org_idArray.length; j++) {
                    	String enable = bean.get("enable") == null ? "" : String.valueOf(bean.get("enable"));
                        org_idBuffer = org_idArray[j].substring(0, 2) + PubFunc.decrypt(org_idArray[j].substring(2));
                        IDFactoryBean idf = new IDFactoryBean();
                        String ids = idf.getId("gz_reporting_log.id", "", this.conn);
                        list.add(ids);
                        list.add(this.salaryid);
                        list.add(date);
                        list.add(PubFunc.decrypt(SafeCode.decode(a00z3)));
                        list.add(bean.get("start_date") == null ? "" : (String) bean.get("start_date"));
                        list.add(bean.get("end_date") == null ? "" : (String) bean.get("end_date"));
                        list.add(org_idBuffer);
                        list.add("");
                        list.add(bean.get("username") == null ? "" : (String) bean.get("username"));
                        list.add(bean.get("fullname") == null ? "" : (String) bean.get("fullname"));
                        list.add(enable);
                        recordList.add(list);
                    }
	            } else {
                    RecordVo vo = new RecordVo("gz_reporting_log");
                    vo.setString("id", id);
                    vo.setString("start_date", bean.get("start_date") == null ? "" : (String) bean.get("start_date"));
                    vo.setString("end_date", bean.get("end_date") == null ? "" : (String) bean.get("end_date"));
                    vo.setString("username",  bean.get("username") == null ? "" : (String) bean.get("username"));
                    vo.setString("fullname",  bean.get("fullname") == null ? "" : (String) bean.get("fullname"));
                    vo.setString("enable",  bean.get("enable") == null ? "" : (String) bean.get("enable"));
                    dao.updateValueObject(vo);
	            }
        	}
        	if (recordList.size() > 0)
        		dao.batchInsert(insertSql.toString(), recordList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 共享账套非管理员可以填报数据吗
     *
     * @return
     */
    private String canNonManageDoSalary(String a00z2,List elementList) {
        RowSet rs = null;
        String infoMessage = "";
        boolean flag = true;
        ArrayList list = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select min(start_date) as start_date,max(end_date) as end_date,max(enable) as enable from gz_reporting_log where salaryid=? and username=? and a00z2=" + Sql_switcher.dateValue(a00z2) + " group by username";
            list.add(this.salaryid);
            list.add(getSelfUserName());
            rs = dao.search(sql, list);
            if (rs.next()) {
                flag = false;
                String enable = rs.getString("enable");
                int start_date = rs.getInt("start_date");
                int end_date = rs.getInt("end_date");
                infoMessage = getErrorMess(start_date, end_date, enable);
            }
            if(flag) {//如果管理员没有点击下发,即gz_reporting_log没有数据，并且设置了应用机构，也不能进入
                for (int i = 0; i < elementList.size(); i++) {
                    if (getSelfUserName().equalsIgnoreCase(((Element) elementList.get(i)).getAttributeValue("username"))) {
                    	infoMessage = ResourceFactory.getProperty("label.gz.nopublish");
                    }
                }
                //如果设置了应用机构，并且不再应用机构里面，提示没有权限
                if(StringUtils.isBlank(infoMessage) && elementList.size() > 0) {
                	infoMessage = ResourceFactory.getProperty("label.gz.haventPower");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return infoMessage;
    }

    public String getErrorMess(int start_date, int end_date, String enable) {
        String infoMessage = "";
        try {
            Calendar now = Calendar.getInstance();
            int nowDay = now.get(Calendar.DAY_OF_MONTH);
            if ((start_date == end_date && start_date != nowDay) || (start_date < end_date && !(start_date <= nowDay && nowDay <= end_date)) || (start_date > end_date && (end_date < nowDay && nowDay < start_date))) {
                infoMessage = ResourceFactory.getProperty("label.gz.notInNowDate");
            }

            if ("0".equals(enable)) {//1:启用
                infoMessage = ResourceFactory.getProperty("label.gz.noUse");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoMessage;
    }

    /**
     * 在进入变动比对和帐套的信息
     *
     * @param gz_module
     * @param gzbo
     * @return
     */
    public String getComeInfo(String gz_module, SalaryTemplateBo gzbo,String a00z2) {
        String info = "";
        try {
            //共享账套非管理员只有在指定的业务日期内且应用机构置为启用时才能填报数据
            ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.conn, this.userView, gz_module, salaryid);
            ArrayList<String> gzReportingDataList = processMonitorBo.getGzReportingData("1");

            List elementList = gzbo.getCtrlparam().getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.FILLING_AGENCY, com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.FILLING_AGENCYS);
            if (gzbo.getManager().length() > 0 && !this.userView.getUserName().equalsIgnoreCase(gzbo.getManager())) {
                info = canNonManageDoSalary(a00z2,elementList);//共享账套非管理员只有在指定的业务日期内且应用机构置为启用时才能填报数据
            } else if (gzbo.getManager().length() == 0) {//非共享账套，只有设置了应用机构的需要更具业务日期这些进行判断
                if (gzReportingDataList.size() > 0) {
                    int start_date = Integer.valueOf(gzbo.getCtrlparam().getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.FILLING_AGENCYS, "start_date"));
                    int end_date = Integer.valueOf(gzbo.getCtrlparam().getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.FILLING_AGENCYS, "end_date"));
                    for (int i = 0; i < elementList.size(); i++) {
                        if (this.userView.getUserName().equalsIgnoreCase(((Element) elementList.get(i)).getAttributeValue("username"))) {
                            info = getErrorMess(start_date, end_date, ((Element) elementList.get(i)).getAttributeValue("enable"));
                            if (StringUtils.isBlank(info)) {
                                return info;
                            }
                        }
                    }
                    if(StringUtils.isBlank(info) && elementList.size() > 0) {
                    	info = ResourceFactory.getProperty("label.gz.haventPower");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    
    public void deleteGzReport(String username,String b0110) {
    	ContentDAO dao = new ContentDAO(this.conn);
    	ArrayList list = new ArrayList();
    	try {
    		b0110 = b0110.substring(0, 2) + PubFunc.decrypt(b0110.substring(2));
			String sql = "delete from gz_reporting_log where username = ? and b0110 = ? and salaryid = ? ";
			list.add(username);
			list.add(b0110);
			list.add(this.salaryid);
			dao.delete(sql, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 变更人员改变管理范围
     * @param isShare
     * @param beanOld
     * @param beanNew
     */
    public void resetManageRange(boolean isShare,ArrayList beanOld,ArrayList beanNew) {
    	if(beanOld != null) {
    		HashMap<String,String> map = getparam(beanOld,isShare);
	        saveManageRange(map.get("role_id"), map.get("flag"), map.get("b0110"),"update");
	        //deleteGzReport(map.get("username"),(String)beanOld.get(2));
    	}
    	if(beanNew != null) {
    		HashMap<String,String> map = getparam(beanNew,isShare);
        	saveManageRange(map.get("role_id"), map.get("flag"), map.get("b0110"), "add");
    	}
    }
    
    private HashMap<String,String> getparam(ArrayList bean,boolean isShare) {
    	HashMap map = new HashMap();
    	try {
	    	String a0100 = bean.get(0) == null ? "" : PubFunc.decrypt((String) bean.get(0));
	        String username = bean.get(1) == null ? "" : (String) bean.get(1);
	        String org_id = bean.get(2) == null ? "" : (String) bean.get(2);
	        String b0110 = "";
	        if(StringUtils.isNotBlank(org_id))
	        	b0110 = org_id.substring(0, 2) + PubFunc.decrypt(org_id.substring(2));
	    	// 0 业务用户 1 角色  4 自助用户
	        String role_id = "";
	        String flag = "";
	        int res_type = 12;//12:薪资，18：保险
	        if (isShare) {//自助用户，
	            role_id = a0100;
	            flag = "4";
	        } else {
	            role_id = username;
	            flag = "0";
	        }
	        map.put("role_id", role_id);
	        map.put("flag", flag);
	        map.put("b0110", b0110);
	        map.put("username", username);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return map;
    }
    
    /**
     * 
     * @param a00z2
     * @param gz_module
     * @return
     */
    public String getSalarySql(String a00z2,String gz_module) {
    	StringBuffer sql = new StringBuffer();
    	Element element = null;
    	String sqls = "";
        try {
        	SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn, Integer.parseInt(salaryid), this.userView);
        	String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
			orgid = orgid != null ? orgid : "";
			String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
			deptid = deptid != null ? deptid : ""; 
			if(StringUtils.isBlank(orgid)&&StringUtils.isBlank(deptid))
			{
				orgid="b0110";
				deptid="e0122";
			}
			
        	ArrayList<LazyDynaBean> datas = gzReportingLogData(a00z2,gz_module);
        	
        	if(datas.size() == 0) {
	            List elementList = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCY, SalaryCtrlParamBo.FILLING_AGENCYS);
	            for (int i = 0; i < elementList.size(); i++) {
	                element = (Element) elementList.get(i);
	                String org_id = StringUtils.isBlank(element.getAttributeValue("org_id")) ? "" : element.getAttributeValue("org_id");
	                String username = StringUtils.isBlank(element.getAttributeValue("username")) ? "" : element.getAttributeValue("username");
	                String enable = StringUtils.isBlank(element.getAttributeValue("enable")) ? "" : element.getAttributeValue("enable");
	                org_id = org_id.substring(2);
	                if(username.equalsIgnoreCase(getSelfUserName()) && "1".equals(enable)) {
	                	//如果有归属部门归属单位
	                	if(StringUtils.isNotBlank(orgid)&&StringUtils.isNotBlank(deptid)) {
	                		sql.append(" or (" + orgid + " like '" + org_id + "%' or " + deptid + " like '" + org_id + "%') ");
	                	}else if(StringUtils.isNotBlank(orgid)) {
	                		sql.append(" or (" + orgid + " like '" + org_id + "%') ");
	                	}else {
	                		sql.append(" or (" + deptid + " like '" + org_id + "%') ");
	                	}
	                }
	            }
        	}else {
        		for(int i = 0; i < datas.size(); i++) {
        			LazyDynaBean data = datas.get(i);
        			String org_id = (String)data.get("org_id");
        			String enable =(String)data.get("enable");
        			String username = (String)data.get("username");
        			int start_date = Integer.parseInt((String)data.get("start_date"));
        			int end_date =Integer.parseInt((String)data.get("end_date"));
        			org_id = PubFunc.decrypt(org_id.substring(2));
        			Calendar now = Calendar.getInstance();
                    int nowDay = now.get(Calendar.DAY_OF_MONTH);
                    //获取进入页面的sql，并且按照下发的日期显示
        			if(username.equalsIgnoreCase(getSelfUserName()) && "1".equals(enable) && !((start_date < end_date && !(start_date <= nowDay && nowDay <= end_date)) || (start_date > end_date && (end_date < nowDay && nowDay < start_date)))) {
        				if(StringUtils.isNotBlank(orgid)&&StringUtils.isNotBlank(deptid)) {
	                		sql.append(" or (" + orgid + " like '" + org_id + "%' or " + deptid + " like '" + org_id + "%') ");
	                	}else if(StringUtils.isNotBlank(orgid)) {
	                		sql.append(" or (" + orgid + " like '" + org_id + "%') ");
	                	}else {
	                		sql.append(" or (" + deptid + " like '" + org_id + "%') ");
	                	}
	                }
        		}
        	}
        	sqls = StringUtils.isNotBlank(sql.toString())?" and" + sql.substring(3):"";
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return sqls;
    }
    
    /**
     * 对于薪资类别应用机构中删除了，再次下发的时候验证下人员
     * @param a00z2
     * @param gz_module
     * @return
     */
    private void checkDataInOrg(String gz_module,String a00z2) {
    	ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.conn, this.userView, gz_module, salaryid);
    	ContentDAO dao = new ContentDAO(this.conn);
    	StringBuffer sqlId = new StringBuffer();
    	ArrayList<String> list = new ArrayList<String>();
    	RowSet rs = null;
    	try {
    		ArrayList<String> gzReportingDatas = processMonitorBo.getGzReportingData("1");
    		rs = dao.search("select username,b0110 from gz_reporting_log where salaryid=? and a00z2=" + Sql_switcher.dateValue(a00z2), Arrays.asList(new String[]{this.salaryid}));
    		while(rs.next()) {
    			String username = StringUtils.isNotBlank(rs.getString("username"))?rs.getString("username") : "";
    			String b0110 = StringUtils.isNotBlank(rs.getString("b0110"))?rs.getString("b0110") : "";
    			for(int i = 0; i < gzReportingDatas.size(); i++) {
    				String gzReportingData = gzReportingDatas.get(i);
					String b0110XML = gzReportingData.substring(0, gzReportingData.indexOf("|"));
					String usernameXML = gzReportingData.substring((gzReportingData.indexOf("|",(gzReportingData.indexOf("|")+1)) + 1), gzReportingData.length());
					if(b0110XML.equalsIgnoreCase(b0110) && username.equals(usernameXML)) {//只有在机构和名称都一样的情况下才加进去，这样没加进去的就是不存在的人
						break;
					}else if(i == (gzReportingDatas.size()-1)) {
						sqlId.append(" or username=? and b0110=? ");
						list.add(username);
						list.add(b0110);
					}
    			}
    		}
    		if(list.size() > 0) {
    			list.add(this.salaryid);
    			dao.delete("delete from gz_reporting_log where " + sqlId.substring(3) + " and salaryid=? and a00z2=" + Sql_switcher.dateValue(a00z2), list);
    		}
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
    }
    
    //获取用户名，可能自助用户关联业务用户的情况，数据上报需要取到这个自主用户
    private String getSelfUserName() {
    	String selfUsername = "";
    	try {
    		
    		selfUsername = this.userView.getHm().get("selfUsername")==null?this.userView.getUserName():
    			(String)this.userView.getHm().get("selfUsername");
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return selfUsername;
    }
}
