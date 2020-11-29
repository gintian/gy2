package com.hjsj.hrms.module.kq.util;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**  
 * <p>Title: KqPrivForHospitalUtil</p>  
 * <p>Description: 医院考勤参数权限工具类</p>  
 * <p>Company: hjsj</p>
 * @date 2018年12月22日 上午11:58:50
 * @author linbz  
 * @version 7.5
 */  
public class KqPrivForHospitalUtil {
	/** 加载公共+上级+本级+下级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF_CHILD = 0;
    /** 加载公共+上级+本级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF = 1;
    /** 加载本级+下级 */
    public final static int LEVEL_SELF_CHILD = 2;
    /** 加载上级 */
    public final static int LEVEL_PARENT = 3;
    /** 加载公共+上级 */
    public final static int LEVEL_GLOBAL_PARENT = 4;
    /** 加载本级 */
    public final static int LEVEL_SELF = 5;
    
    private UserView userView;
    private Connection conn;

    public KqPrivForHospitalUtil(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 获取考勤参数
     * getKqParameter
     * @return
     * @throws GeneralException
     * @date 2018年11月17日 下午6:04:48
     * @author linbz
     */
    public static HashMap getKqParameter(Connection conn) throws GeneralException{
    	
    	HashMap map = new HashMap();
        RowSet rs = null;
    	try {
    		RecordVo paramsVo=ConstantParamter.getConstantVo("KAOQIN_PARAM");
			String xmlValue = "";
			// 有缓存则取缓存数据
			if(null != paramsVo){
				xmlValue = paramsVo.getString("str_value");
			}else {
				xmlValue = getConstantXml(conn, "KAOQIN_PARAM");
				if(StringUtils.isEmpty(xmlValue))
					xmlValue = getGenerateXmlStr();
				updateConstantCache(xmlValue);
			}
			map = getParseXmlMap(xmlValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return map;
    }
    /**
     * 从数据库中获取参数
     * getConstantXml
     * @param constant
     * @return
     * @throws GeneralException
     * @date 2018年11月17日 下午6:05:41
     * @author linbz
     */
    private static String getConstantXml(Connection conn, String constant) throws GeneralException {

    	String xmlStr = "";
    	RowSet rs = null;
		try {
			if (StringUtils.isBlank(constant)) 
				return xmlStr;
			
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select Str_Value from constant where Constant = ?";
			ArrayList param = new ArrayList();
			param.add(constant);
			rs = dao.search(sql, param); 
			if(rs.next()) 
				xmlStr = rs.getString("Str_Value");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return xmlStr;
	}
    /**
     * 获取默认考勤参数xml
     * getGenerateXmlStr
     * @return
     * @throws GeneralException
     * @date 2018年11月17日 下午6:09:58
     * @author linbz
     */
	private static String getGenerateXmlStr() throws GeneralException{
		String xmlStr = "";
		try {
			 String xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?> <kq></kq>";
			 //构建Document对象
			 Document doc = PubFunc.generateDom(xml);
			 //获得root节点
			 Element root = doc.getRootElement();
			 Element nbasesNode = new Element("nbase");
			 // 默认在职人员库
			 nbasesNode.setText("Usr");
			 root.addContent(nbasesNode);
			 // 工号
			 Element gnoNode = new Element("g_no");
			 gnoNode.setText("");
			 root.addContent(gnoNode);
			 // 考勤部门
			 Element kqDept = new Element("kq_dept");
			 kqDept.setText("");
			 root.addContent(kqDept);
			 // 考勤部门变动（轮岗）信息
			 Element kqDeptChange = new Element("kq_dept_change");
			 kqDeptChange.setAttribute("setid", "");
			 kqDeptChange.setAttribute("dept_field", "");
			 kqDeptChange.setAttribute("start_field", "");
			 kqDeptChange.setAttribute("end_field", "");
			 root.addContent(kqDeptChange);
			 // 考勤开始日期
			 Element kqStartDate = new Element("kq_start_date");
			 kqStartDate.setText("");
			 root.addContent(kqStartDate);
			 // 考勤结束日期
			 Element kqEndDate = new Element("kq_end_date");
			 kqEndDate.setText("");
			 root.addContent(kqEndDate);
			 // 考勤卡号
			 Element kqCard_no = new Element("card_no");
			 kqCard_no.setText("");
			 root.addContent(kqCard_no);
			 // 请假子集
			 Element kqLeave_subset = new Element("leave_subset");
			 kqLeave_subset.setAttribute("setid", "");
			 kqLeave_subset.setAttribute("type", "");
			 kqLeave_subset.setAttribute("start", "");
			 kqLeave_subset.setAttribute("end", "");
			 kqLeave_subset.setAttribute("reason", "");
			 root.addContent(kqLeave_subset);
			 // 公出子集
			 Element kqOfficeleave_subset = new Element("officeleave_subset");
			 kqOfficeleave_subset.setAttribute("setid", "");
			 kqOfficeleave_subset.setAttribute("type", "");
			 kqOfficeleave_subset.setAttribute("start", "");
			 kqOfficeleave_subset.setAttribute("end", "");
			 kqOfficeleave_subset.setAttribute("reason", "");
			 root.addContent(kqOfficeleave_subset);
			 // 加班子集
			 Element kqOvertime_subset = new Element("overtime_subset");
			 kqOvertime_subset.setAttribute("setid", "");
			 kqOvertime_subset.setAttribute("type", "");
			 kqOvertime_subset.setAttribute("start", "");
			 kqOvertime_subset.setAttribute("end", "");
			 kqOvertime_subset.setAttribute("reason", "");
			 root.addContent(kqOvertime_subset);
			 
			 //设置xml字体编码，然后输出为字符串
			 Format format=Format.getRawFormat();
			 format.setEncoding("UTF-8");
			 XMLOutputter output=new XMLOutputter(format);
			 xmlStr = output.outputString(doc);
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return xmlStr;
	 }
	/**
     * 更新考勤参数缓存
     * updateConstantCache
     * @param xml
     * @throws GeneralException
     * @date 2018年11月17日 下午6:07:00
     * @author linbz
     */
    private static void updateConstantCache(String xml) throws GeneralException {
		try{
			RecordVo vo = new RecordVo("Constant");
			vo.setString("constant", "KAOQIN_PARAM");
			vo.setString("describe", "考勤参数");
			vo.setString("str_value", xml);
			vo.setString("type", "");
			ConstantParamter.putConstantVo(vo, "KAOQIN_PARAM");
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    /**
     * 获取参数map(格式化参数xml)
     * getParseXmlMap
     * @param xml
     * @return
     * @throws GeneralException
     * @date 2018年11月17日 下午6:07:30
     * @author linbz
     */
    private static HashMap getParseXmlMap(String xml) throws GeneralException{
    	
    	HashMap map = new HashMap();
		try{
			 // 构建Document对象
			 Document doc = PubFunc.generateDom(xml);
			 // 获得root节点
			 Element root = doc.getRootElement();
			 String value = "";
			 // 人员库
			 Element nbase = root.getChild("nbase");
			 if(nbase != null){
				 value = nbase.getText();
				 // 默认在职人员库
				 if(StringUtils.isEmpty(value))
					 value = "Usr";
			 }else
				 value = "Usr";
			 map.put("nbase", value);
			 // 卡号
			 Element cardNo = root.getChild("card_no");
			 value = "";
			 if(cardNo != null)
				 value = cardNo.getText();
			 map.put("card_no", value);
			 // 工号
			 Element gNo = root.getChild("g_no");
			 value = "";
			 if(gNo != null)
				 value = gNo.getText();
			 map.put("g_no", value);
			 // 考勤部门
			 Element kqDept = root.getChild("kq_dept");
			 value = "";
			 if(kqDept != null)
				 value = kqDept.getText();
			 map.put("kq_dept", ("null".equalsIgnoreCase(value))?"":value);
			 // 考勤部门变动（轮岗）信息
			 Element kqDeptChange = root.getChild("kq_dept_change");
			 if(kqDeptChange != null) {
				 map.put("setid", kqDeptChange.getAttributeValue("setid"));
				 map.put("dept_field", kqDeptChange.getAttributeValue("dept_field"));
				 map.put("start_field", kqDeptChange.getAttributeValue("start_field"));
				 map.put("end_field", kqDeptChange.getAttributeValue("end_field"));
			 }else {
				 map.put("setid", "");
				 map.put("dept_field", "");
				 map.put("start_field", "");
				 map.put("end_field", "");
			 }
			 // 考勤开始日期
			 Element kqStartDate = root.getChild("kq_start_date");
			 value = "";
			 if(kqStartDate != null)
				 value = kqStartDate.getText();
			 map.put("kq_start_date", ("null".equalsIgnoreCase(value))?"":value);
			 // 考勤结束日期
			 Element kqEndDate = root.getChild("kq_end_date");
			 value = "";
			 if(kqEndDate != null)
				 value = kqEndDate.getText();
			 map.put("kq_end_date", ("null".equalsIgnoreCase(value))?"":value);
			 
			 // 请假子集
			 Element leaveSubset = root.getChild("leave_subset");
			 HashMap leaveSubsetMap = new HashMap();
			 if(leaveSubset != null) {
				 leaveSubsetMap.put("setid", leaveSubset.getAttributeValue("setid"));
				 leaveSubsetMap.put("type", leaveSubset.getAttributeValue("type"));
				 leaveSubsetMap.put("start", leaveSubset.getAttributeValue("start"));
				 leaveSubsetMap.put("end", leaveSubset.getAttributeValue("end"));
				 leaveSubsetMap.put("reason", leaveSubset.getAttributeValue("reason"));
			 }else {
				 leaveSubsetMap.put("setid", "");
				 leaveSubsetMap.put("type", "");
				 leaveSubsetMap.put("start", "");
				 leaveSubsetMap.put("end", "");
				 leaveSubsetMap.put("reason", "");
			 }
			 map.put("leave_subset", leaveSubsetMap);
			 // 公出子集
			 Element officeleaveSubset = root.getChild("officeleave_subset");
			 HashMap officeleaveSubsetMap = new HashMap();
			 if(leaveSubset != null) {
				 officeleaveSubsetMap.put("setid", officeleaveSubset.getAttributeValue("setid"));
				 officeleaveSubsetMap.put("type", officeleaveSubset.getAttributeValue("type"));
				 officeleaveSubsetMap.put("start", officeleaveSubset.getAttributeValue("start"));
				 officeleaveSubsetMap.put("end", officeleaveSubset.getAttributeValue("end"));
				 officeleaveSubsetMap.put("reason", officeleaveSubset.getAttributeValue("reason"));
			 }else {
				 officeleaveSubsetMap.put("setid", "");
				 officeleaveSubsetMap.put("type", "");
				 officeleaveSubsetMap.put("start", "");
				 officeleaveSubsetMap.put("end", "");
				 officeleaveSubsetMap.put("reason", "");
			 }
			 map.put("officeleave_subset", officeleaveSubsetMap);
			 // 加班子集
			 Element overtimeSubset = root.getChild("overtime_subset");
			 HashMap overtimeSubsetMap = new HashMap();
			 if(leaveSubset != null) {
				 overtimeSubsetMap.put("setid", overtimeSubset.getAttributeValue("setid"));
				 overtimeSubsetMap.put("type", overtimeSubset.getAttributeValue("type"));
				 overtimeSubsetMap.put("start", overtimeSubset.getAttributeValue("start"));
				 overtimeSubsetMap.put("end", overtimeSubset.getAttributeValue("end"));
				 overtimeSubsetMap.put("reason", overtimeSubset.getAttributeValue("reason"));
			 }else {
				 overtimeSubsetMap.put("setid", "");
				 overtimeSubsetMap.put("type", "");
				 overtimeSubsetMap.put("start", "");
				 overtimeSubsetMap.put("end", "");
				 overtimeSubsetMap.put("reason", "");
			 }
			 map.put("overtime_subset", overtimeSubsetMap);
			 // 数据上报
			 Element reportDailyApply = root.getChild("report_daily_apply");
			 HashMap reportDailyApplyMap = new HashMap();
			 if(reportDailyApply != null) {
				 // 是否可以修改统计项、计算项
				 reportDailyApplyMap.put("enable_modify", reportDailyApply.getAttributeValue("enable_modify"));
				 //是否填写审批意见
				 reportDailyApplyMap.put("approval_message", reportDailyApply.getAttributeValue("approval_message"));
			 }else {
				 // 默认是允许修改 =1
				 reportDailyApplyMap.put("enable_modify", "1");
				 //默认不填写
				 reportDailyApplyMap.put("approval_message", "0");
			 }
			 map.put("report_daily_apply", reportDailyApplyMap);
			 
			 return map;
		 }catch (Exception e) {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	}

    /**
     * 医院考勤获取用户人员库权限
     * getB0110Dase
     * @param userView
     * @param conn
     * @return
     * @date 2018年12月22日 下午3:05:50
     * @author linbz
     */
    public static ArrayList<String> getB0110Dase(UserView userView, Connection conn) {
        ArrayList<String> kq_dbase_list = new ArrayList<String>();
        try {
            String nbases = (String)getKqParameter(conn).get("nbase");

            ArrayList dbaselist = userView.getPrivDbList(); // 求应用库前缀权限列表
            for (int i = 0; i < dbaselist.size(); i++) {
                String userbase = dbaselist.get(i).toString();
                if (nbases.indexOf(userbase) != -1) {
                    kq_dbase_list.add(userbase);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq_dbase_list;
    }

    /**
     * 获取所属单位sql条件
     * sql形如：(B0110='' or B0110 is null or B0110='UN`' or B0110='HJSJ' OR B0110 LIKE '0102%' OR B0110=LEFT('0102',LEN(B0110)))
     * getPrivB0110Whr
     * @param userView		操作用户
     * @param b0110Fld		所属单位指标 自行更据需要传入是否带前缀，比如 UsrAxx.Axxyy或Axxyy
     * @param levelFlag		加载层级标志  0：加载公共+上级+本级+下级   1：加载公共+上级+本级 ； 2：加载本级+下级；3:加载上级
     * @return
     * @throws GeneralException
     * @date 2019年1月7日 下午1:54:17
     * @author linbz
     */
    public static String getPrivB0110Whr(UserView userView, String b0110Fld, Integer levelFlag) throws GeneralException{
        if (userView == null)
            return "1=2";
        
        if (userView.isSuper_admin())
            return "1=1";
        
        b0110Fld = (b0110Fld == null || "".equals(b0110Fld)) ? "b0110" : b0110Fld;
        levelFlag = (levelFlag == null) ? 0 : levelFlag;
        
        String privB0110Str = getB0110(userView);
        
        String[] privB0110s = privB0110Str.split("`");
        
        StringBuffer sqlWhr = new StringBuffer();
        sqlWhr.append("(");
        
        // UN`是全权
        if ("UN`".equalsIgnoreCase(privB0110Str) || "`".equalsIgnoreCase(privB0110Str))
            return "1=1";
        
        for (int i = 0; i < privB0110s.length; i++) {
            String privB0110 = privB0110s[i].trim();
            if ("".equals(privB0110))
                continue;
            
            if (privB0110.startsWith("UN") || privB0110.startsWith("UM") || privB0110.startsWith("@K"))
                privB0110 = privB0110.substring(2);
            
            if ("HJSJ".equals(privB0110))
                privB0110 = "";            
            
            if (sqlWhr.length() > 1)
                sqlWhr.append(" OR ");
            
            sqlWhr.append(" 1=2 ");
            //公共
            if (levelFlag != LEVEL_SELF_CHILD && levelFlag != LEVEL_PARENT && levelFlag != LEVEL_SELF) {
                sqlWhr.append(" OR ");
                sqlWhr.append(b0110Fld).append("=''");
                sqlWhr.append(" or ").append(b0110Fld).append(" is null");
                sqlWhr.append(" or ").append(b0110Fld).append("='UN`'");
                sqlWhr.append(" or ").append(b0110Fld).append("='HJSJ'");
            }
            
            if (levelFlag != LEVEL_SELF_CHILD && levelFlag != LEVEL_SELF) {
                //上级
                sqlWhr.append(" OR ");
                sqlWhr.append("(");
                sqlWhr.append(b0110Fld).append("=").append(Sql_switcher.left("'" + privB0110 + "'", Sql_switcher.length(b0110Fld)));
                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
                sqlWhr.append(")");
            }
            
            //下级
            if (levelFlag != LEVEL_GLOBAL_PARENT_SELF && levelFlag != LEVEL_PARENT && levelFlag != LEVEL_SELF && levelFlag != LEVEL_GLOBAL_PARENT) {
                sqlWhr.append(" OR ");
                sqlWhr.append("(");
                sqlWhr.append(b0110Fld).append(" LIKE '").append(privB0110).append("%'");
                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
                sqlWhr.append(")");
            }
            
            //本级
            if (levelFlag != LEVEL_PARENT && levelFlag != LEVEL_GLOBAL_PARENT) {
                sqlWhr.append(" OR ");
                sqlWhr.append(b0110Fld).append("='").append(privB0110).append("'");
            }
            
        }
        sqlWhr.append(")");
        return sqlWhr.toString();
    }
    /**
     * 获取考勤权限部门
     * getB0110
     * @param userView
     * @return
     * @throws GeneralException
     * @date 2019年1月7日 下午1:55:01
     * @author linbz
     */
    public static String getB0110(UserView userView) throws GeneralException {
        String b0110 = "";
        try {
            String codeid = "";
            if (userView.isSuper_admin() || "1".equals(userView.getGroupId()))
                return "HJSJ";
            
            codeid = userView.getUnitIdByBusi("11");
            if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)
                    || "UM`".equalsIgnoreCase(codeid) || "@K`".equalsIgnoreCase(codeid)) {
            	// "您没有考勤管理的管理范围权限，请联系管理员！"
                throw new Exception(ResourceFactory.getProperty("kq.data.nopiv"));
            }

            if (codeid.trim().length() < 3)
                return "HJSJ";
            
            if (codeid.indexOf("`") == -1) {
                if (codeid.startsWith("UN") || codeid.startsWith("UM")) {
                    b0110 = codeid.substring(2);
                } else {
                    b0110 = codeid;
                }
                
                return b0110;
            } 
            
            String[] temps = codeid.split("`");
            for (int i = 0; i < temps.length; i++) {
                if (temps[i].startsWith("UN") || temps[i].startsWith("UM")) {
                    b0110 += temps[i].substring(2) + "`";
                } else {
                    b0110 += temps[i] + "`";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.nopiv")));
        }
        return b0110;
    }
    /**
	 * 获取用户默认权限部门
	 * getPrivB0110
	 * @return
	 * @date 2018年12月28日 下午5:58:30
	 * @author linbz
	 */
    public String getPrivB0110() {
		String orgid = "";
		try {
			String privB0110Str = getB0110(userView);
			String[] privB0110s = privB0110Str.split("`");
			for (int i = 0; i < privB0110s.length; i++) {
	            String privB0110 = privB0110s[i].trim();
	            if (StringUtils.isEmpty(privB0110))
	                continue;
	            else if ("HJSJ".equalsIgnoreCase(privB0110))
	                continue;
	            else {
	            	orgid = privB0110;
	            	break;
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orgid;
	}
    /**
	 * 获取最顶层机构(第一个)
	 * getTopUNCodeitemid
	 * @return
	 * @date 2018年12月18日 下午3:33:39
	 * @author linbz
	 */
    public String getTopUNCodeitemid() {
		String codeitemid = "";
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append("select codeitemid");
			sql.append(" from organization");
			sql.append(" where codesetid='UN' and codeitemid=parentid");
			sql.append(" and ").append(Sql_switcher.dateToChar("start_date","yyyy-MM-dd")).append("<=?");
			sql.append(" and ").append(Sql_switcher.dateToChar("end_date","yyyy-MM-dd")).append(">=?");
			sql.append(" ORDER BY A0000");
			String dateStr = DateUtils.format(new Date(), "yyyy-MM-dd");
			ArrayList<String> list = new ArrayList<String>();
			list.add(dateStr);
			list.add(dateStr);
			
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), list);
			if(rs.next()) 
				codeitemid = rs.getString("codeitemid");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return codeitemid;
	}
    
    /**
     * 得到一个考勤参数的HashMap
     * getKqParamterMap
     * @return
     * @date 2019年1月22日 下午4:52:22
     * @author linbz
     */
    public HashMap getKqParamterMap() {
    	
        HashMap hashmap = new HashMap();
        try {
            hashmap = getKqParameter(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashmap;
    }
    /**
     * 获取考勤人员库
     * getKqNbases
     * @return
     * @date 2019年12月25日 下午1:50:45
     * @author linbz
     */
    public String getKqNbases() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("nbase");
    }
    /**
     * 获取工号指标
     * getG_no
     * @return
     * @date 2019年1月22日 下午4:50:45
     * @author linbz
     */
    public String getG_no() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("g_no");
    }
    /**
     * 获取考勤部门指标
     * getKqDeptField
     * @return
     * @date 2019年1月23日 下午4:18:33
     * @author linbz
     */
    public String getKqDeptField() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("kq_dept");
    }
    /**
     * 获取考勤变动子集
     * getKqChangeSetid
     * @return
     * @date 2019年2月18日 下午3:25:46
     * @author linbz
     */
    public String getKqChangeSetid() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("setid");
    }
    /**
     * 获取考勤变动部门
     * getKqChangeDeptField
     * @return
     * @date 2019年2月18日 下午3:26:21
     * @author linbz
     */
    public String getKqChangeDeptField() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("dept_field");
    }
    /**
     * 获取考勤变动部门开始日期
     * getKqChangeStartField
     * @return
     * @date 2019年2月18日 下午3:26:43
     * @author linbz
     */
    public String getKqChangeStartField() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("start_field");
    }
    /**
     * 获取考勤变动部门结束日期
     * getKqChangeEndField
     * @return
     * @date 2019年2月18日 下午3:27:58
     * @author linbz
     */
    public String getKqChangeEndField() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("end_field");
    }
    /**
     * 获取考勤开始日期指标
     * getKqStartDateField
     * @return
     * @date 2019年5月27日 下午3:47:14
     * @author linbz
     */
    public String getKqStartDateField() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("kq_start_date");
    }
    /**
     * 获取考勤结束日期指标
     * getKqEndDateField
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getKqEndDateField() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("kq_end_date");
    }
    /**
     * 获取考勤卡号指标
     * getKqCard_no
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getKqCard_no() {
    	HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("card_no");
    }
    /**
     * 获取请假子集指标
     * getLeave_setid
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getLeave_setid() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("leave_subset");
        return (String) hashmap.get("setid");
    }
    /**
     * 获取请假类型指标
     * getLeave_type
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getLeave_type() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("leave_subset");
        return (String) hashmap.get("type");
    }
    /**
     * 获取请假开始时间指标
     * getLeave_start
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getLeave_start() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("leave_subset");
        return (String) hashmap.get("start");
    }
    /**
     * 获取请假结束时间指标
     * getLeave_end
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getLeave_end() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("leave_subset");
        return (String) hashmap.get("end");
    }
    /**
     * 获取请假事由指标
     * getLeave_reason
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getLeave_reason() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("leave_subset");
        return (String) hashmap.get("reason");
    }
    /**
     * 获取公出子集指标
     * getOfficeleave_setid
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOfficeleave_setid() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("officeleave_subset");
        return (String) hashmap.get("setid");
    }
    /**
     * 获取公出类型指标
     * getOfficeleave_type
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOfficeleave_type() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("officeleave_subset");
        return (String) hashmap.get("type");
    }
    /**
     * 获取公出开始时间指标
     * getOfficeleave_start
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOfficeleave_start() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("officeleave_subset");
        return (String) hashmap.get("start");
    }
    /**
     * 获取公出结束时间指标
     * getOfficeleave_end
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOfficeleave_end() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("officeleave_subset");
        return (String) hashmap.get("end");
    }
    /**
     * 获取公出事由指标
     * getOfficeleave_reason
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOfficeleave_reason() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("officeleave_subset");
        return (String) hashmap.get("reason");
    }
    /**
     * 获取加班子集指标
     * getOvertime_setid
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOvertime_setid() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("overtime_subset");
        return (String) hashmap.get("setid");
    }
    /**
     * 获取加班类型指标
     * getOvertime_type
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOvertime_type() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("overtime_subset");
        return (String) hashmap.get("type");
    }
    /**
     * 获取加班开始时间指标
     * getOvertime_start
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOvertime_start() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("overtime_subset");
        return (String) hashmap.get("start");
    }
    /**
     * 获取加班结束时间指标
     * getOvertime_end
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOvertime_end() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("overtime_subset");
        return (String) hashmap.get("end");
    }
    /**
     * 获取加班事由指标
     * getOvertime_reason
     * @return
     * @date 2019年5月27日 下午3:47:17
     * @author linbz
     */
    public String getOvertime_reason() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("overtime_subset");
        return (String) hashmap.get("reason");
    }
    /**
     * 获取是否允许修改统计项、计算项数据
     * getEnable_modify
     * @return
     * @date 2019年11月21日 下午3:47:17
     * @author linbz
     */
    public String getEnable_modify() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("report_daily_apply");
        return (String) hashmap.get("enable_modify");
    }
    /**
     * 获取是否填写审批意见
     * getApprovalMessage
     * @return
     * @date 2020年02月26日 10:25:13
     * @author xuanz
     */
    public String getApprovalMessage() {
    	HashMap hashmap = (HashMap)getKqParamterMap().get("report_daily_apply");
        return (String) hashmap.get("approval_message");
    }
}
