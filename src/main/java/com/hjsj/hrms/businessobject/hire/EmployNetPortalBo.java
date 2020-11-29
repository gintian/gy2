package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;
public class EmployNetPortalBo {

	private Connection conn = null;
	private ContentDAO dao;
	public static String only_field = "";//唯一性指标
	private String isAttach = "0";
	private int recruitservice = 0;// =1 说明是微招聘接口 后招聘职位名称前不拼接 部门
	public EmployNetPortalBo(Connection conn){
		this.conn = conn;
		this.dao = new ContentDAO(conn);
		getZpkdbName();
	}

	public EmployNetPortalBo(Connection conn, String isAttach) {
        this.conn = conn;
        this.dao = new ContentDAO(conn);
        this.isAttach = isAttach;
    }

    public EmployNetPortalBo() {

    }
	public void setRecruitservice(int recruitservice) {
		this.recruitservice = recruitservice;
	}
	/**
	 * 存放招聘渠道信息
	 * key为渠道代码，即35号代码中的值
	 * 渠道信息包括招聘单位，职位详细信息，公告信息，缓存刷新时间
	 * 类型为map，key分别为unit、pos、board、cacheDataTime
	 * HashMap infoMap01 = new HashMap();
		infoMap01.put("unit", unitList);
		infoMap01.put("pos", posList);
		infoMap01.put("board", boardList);
		infoMap01.put("cacheDataTime", Date);
	 * channelMap.put("01",infoMap01)
	 * 保存完应聘简历子集和指标参数后应该刷新
	 */
	public static HashMap<String, HashMap> channelMap;

	/**
	 * 存放招聘渠道参数信息
	 * fieldList.add(getZpSubSetMap(paramsMap));子集显示名称
    	fieldList.add(map.get("fieldMap"));子集的所有指标
    	fieldList.add(map.get("fieldSetMap"));是否必填 fieldSetMap.put(fieldId,"1#1");
    	fieldList.add(map.get("fieldNameSetMap"));指标自定义名称
    	fieldList.add(channelSet.get(1));必填子集
	 * zpFieldMap.put("01",fieldList)
	 * 保存完应聘简历子集和指标参数后应该刷新
	 */
	public static HashMap zpFieldMap;
	public static ArrayList ZpFieldList; // 招聘子集数据列表 和 子集对应指标的 map

	public static ArrayList posListField = null;//外网职位列表显示指标
    public static Date unCacheDataTime = null;//单位介绍缓存时间
	public static String isDefineWorkExperience = "0";//是否有工作经验
	public static String workExperience = null;//工作经验
	public static String workExperienceDesc = null;//描述
	public static ArrayList workExperienceCodeList = null;
	private static String a_dbName; //应聘库
	public static HashMap codeSetMap; // 单层代码 映射 map
	public ArrayList<LazyDynaBean> lastLvUnits = new ArrayList<LazyDynaBean>();
	private String loginUserName = "";// 登录用户名,用来判断 猎头招聘用户登录时查询当前岗位
    									// 由当前猎头推荐的人数有多少个
	public ArrayList<String> zp_positions = null;//存放个人申请过的职位，防止频繁访问数据库
	private String hireChannel;// 招聘渠道，用于将职位搜索也区分开
	boolean isVisiablSeqField = false;
    static String[] viewPosInfo; // 职位描述参数
    private String hasXaoYuan = "0"; // 1 既有校园又有社会, 2 只有校园
    public static String isOnlyChecked = null;
    public static String netHref = null;
    public static Map FILE_TYPE_MAP = new HashMap();// 用于存放常见文件头信息
    public static int cacheDataTimeOutMins = -1;
    public static String resume_code = null;//好像没什么用，为了旧代码不报错
    public static ArrayList interviewingRevertItemCodeList = null;
    public static String interviewingRevertItemid = null;
    public static String hirePostByLayer = "";// 是否只显示本级单位的招聘岗位 =1是

	public static HashMap<String, HashMap> getChannelMap() {
		return channelMap;
	}

	public static void setChannelMap(HashMap<String, HashMap> channelMap) {
		EmployNetPortalBo.channelMap = channelMap;
	}

	/**
     * @return the loginUserName
     */
    public String getLoginUserName() {
        return loginUserName;
    }

    /**
     * @param loginUserName
     *            the loginUserName to set
     */
    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public boolean isVisiablSeqField() {
        return isVisiablSeqField;
    }

    public void setVisiablSeqField(boolean isVisiablSeqField) {
        this.isVisiablSeqField = isVisiablSeqField;
    }

    public String getHasXaoYuan() {
        return hasXaoYuan;
    }

    public void setHasXaoYuan(String hasXaoYuan) {
        this.hasXaoYuan = hasXaoYuan;
    }

    public String getHireChannel() {
		return hireChannel;
	}

	public void setHireChannel(String hireChannel) {
		this.hireChannel = hireChannel;
	}
	/**页面上没有用到，为了旧代码不报错start**/
	public static String ZP_SY_MESSAGE = null;
    public static String ZP_SOCIAL_MESSAGE = null;
    public static String ZP_SCHOOL_MESSAGE = null;
    public static String ZP_HEADHUNTER_MESSAGE = null;

    @Deprecated
    public String getZP_SY_MESSAGE() {
        if (ZP_SY_MESSAGE == null || ZP_SY_MESSAGE.trim().length() == 0) {
            RecordVo vo = new RecordVo("constant");
            vo.setString("constant", "ZP_SY_MESSAGE");
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                if (dao.isExistRecordVo(vo)) {
                    vo = dao.findByPrimaryKey(vo);
                    ZP_SY_MESSAGE = vo.getString("str_value");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String temp = ZP_SY_MESSAGE == null ? "" : ZP_SY_MESSAGE;
        String str = temp.toUpperCase().replaceAll("&NBSP;", "");
        if ("".equals(str.trim())) {
            ZP_SY_MESSAGE = "";
        }

        return ZP_SY_MESSAGE;
    }

    @Deprecated
    public void getInterviewingRevertItemCodeList(String itemid) {
        RowSet rs = null;
        try {
            EmployNetPortalBo.interviewingRevertItemid = itemid;
            EmployNetPortalBo.interviewingRevertItemCodeList = new ArrayList();
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid,codeitemdesc from codeitem where codesetid=(select codesetid from fielditem where UPPER(itemid)='"
                            + itemid.toUpperCase() + "')");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("codeitemid", rs.getString("codeitemid"));
                bean.set("codeitemdesc", rs.getString("codeitemdesc"));
                EmployNetPortalBo.interviewingRevertItemCodeList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**页面上没有用到，为了旧代码不报错end**/

    /**
     * 招聘唯一性指标
     * @return
     */
    public String getOnly_field() {
        if (only_field == null || "".equals(only_field.trim())) {
            try {
                RecordVo vo = new RecordVo("constant");
                vo.setString("constant", "ZP_ONLY_FIELD");
                if (dao.isExistRecordVo(vo)) {
                    vo = dao.findByPrimaryKey(vo);
                    String only_field_str = vo.getString("str_value");
                    if (only_field_str != null && only_field_str.trim().length() > 0) {
                        String[] arr = only_field_str.split(",");
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i] == null || "".equals(arr[i])) {
                                continue;
                            }

                            only_field = arr[i].substring(4);
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return only_field;
    }

    /**
     * 刷新静态变量值
     *
     */
    public void refreshStaticAttribute() {
        try {
            ZpFieldList = null;
            a_dbName = null;
            codeSetMap = null;
            zpFieldMap = null;
            only_field = null;
            channelMap = null;
            viewPosInfo = null;
            isOnlyChecked = null;
            posListField = null;
            getZpkdbName();
            getCodeSetMap();
            getOnly_field();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList getZpFieldList() throws GeneralException {
    	ZpFieldList = getSetByWorkExprience(this.hireChannel);
		return ZpFieldList;

    }

    public void refreshPosViewAttribute() {
        try {
            // conditionFieldList=null;
            viewPosInfo = null;
            getViewPosInfo();
            // getPosQueryConditionList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成渠道参数信息
     * @param paramsMap
     * @param hireChannel
     */
    public void createChannelParams(String hireChannel){
    	RowSet search = null;
    	try {
    		if(zpFieldMap==null) {
                zpFieldMap = new HashMap();
            }
	    	ParameterSetBo paramBo = new ParameterSetBo(this.conn);
			Map<String, Map<String, String>> paramsMap = null;
			String subSet = "";
            search = dao.search("select str_value from constant where constant='ZP_SUBSET_LIST'");
            if(search.next()) {
            	subSet = search.getString("str_value");
            	if(!subSet.startsWith("{")) {
                    subSet =  paramBo.convertStringFormat(subSet);
                }

            	Map map = (Map<String, Map<String, String>>) JSON.parse(subSet);
            	ArrayList fieldList = paramBo.getFieldList();
            	paramsMap = paramBo.sortConstantMap(fieldList, map);
            }
	    	ArrayList<String> channelSet = this.getChannelSet(paramsMap, hireChannel);
	    	HashMap map = this.getZpFieldMap(paramsMap, hireChannel);
	    	ArrayList fieldList = new ArrayList();
	    	fieldList.add(map.get("showSetList"));
	    	fieldList.add(map.get("fieldMap"));
	    	fieldList.add(map.get("fieldSetMap"));
	    	fieldList.add(map.get("fieldNameSetMap"));
	    	fieldList.add(channelSet.get(1));
	    	zpFieldMap.put(hireChannel, fieldList);
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(search);
		}
    }

    /**
     * 获取简历指标信息集合
     * fieldMap 子集的所有指标
     * fieldSetMap 是否必填 fieldSetMap.put(fieldId,"1#1");
     * fieldNameSetMap 指标自定义名称
     * showSetList 设置了指标的子集显示名称
     * map.put("fieldMap", fieldMap);
     * map.put("fieldSetMap", fieldSetMap);
     * map.put("fieldNameSetMap", fieldNameSetMap);
     * @param paramsMap
     * @param hireChannel
     * @return
     */
    private HashMap getZpFieldMap(Map<String, Map<String, String>> paramsMap, String hireChannel) {
        HashMap map = new HashMap();
        RowSet search = null;
        Map fieldMap = new HashMap();
        HashMap fieldSetMap = new HashMap();
        HashMap fieldNameSetMap = new HashMap();
        ArrayList<LazyDynaBean> showSetList = new ArrayList<LazyDynaBean>();
        try {
        	ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
        	HashMap params = parameterXMLBo.getAttributeValues();
        	//证件类型
			String id_type=(String)params.get("id_type");
			if(StringUtils.isNotEmpty(id_type)&&!"#".equals(id_type)) {
				FieldItem fieldItem = DataDictionary.getFieldItem(id_type,"A01");
				if(fieldItem==null||!"1".equals(fieldItem.getUseflag())) {
                    throw GeneralExceptionHandler.Handle(new Exception("证件类型指标未构库"));
                }
			}
			String onlyfield = getOnly_field();
        	search = dao.search("select str_value from constant where constant='ZP_FIELD_LIST_JSON'");
        	String field = "";
            if(search.next()) {
                field = search.getString("str_value");
                field = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(field);
                if (StringUtils.isNotEmpty(field)) {//例如：field = "A01{A0101[0#1],A0111#出生日期[0#0],A01AX#最高学历毕业日期[0#0],},"
                    JSONObject json1 = JSONObject.fromObject(field);
                    JSONObject array  =  (JSONObject) json1.get(hireChannel);
                    if(array!=null) {
                    	Iterator<String> it = array.keys();
                    	while(it.hasNext()){
                    		boolean idTypeF = true;
                    		boolean onlyTypeF = true;
                    		ArrayList fielditemList = new ArrayList();
                    		HashMap fieldItemMap = new HashMap();
                    		HashMap fieldNameMap = new HashMap();
                    		// 获得key
                    		String key = it.next();
                    		JSONArray array2 = (JSONArray)array.get(key);
                    		for (int i = 0; i < array2.size(); i++) {
                    			JSONObject json3 =  (JSONObject) array2.get(i);
                    			String  id = (String) json3.get("id");
                    			String in_list =  (String) json3.get("in_list");
                    			String must = (String) json3.get("must");
                    			String name = (String) json3.get("name");
                    			fieldItemMap.put(id, in_list+"#"+must);
                    			if(id.equalsIgnoreCase(id_type)) {
                                    idTypeF = false;
                                } else if(id.equalsIgnoreCase(onlyfield)) {
                                    onlyTypeF = false;
                                }
                    			fielditemList.add(id);
                    			if(!"".equalsIgnoreCase(name)) {
                                    fieldNameMap.put(id,name);
                                }
                    		}
                    		if(idTypeF&&"A01".equalsIgnoreCase(key)&&StringUtils.isNotEmpty(id_type)) {
                    			fielditemList.add(id_type.toLowerCase());
                    			fieldItemMap.put(id_type.toLowerCase(), "0#1");
                    		}
                    		if(onlyTypeF&&"A01".equalsIgnoreCase(key)&&StringUtils.isNotEmpty(onlyfield)) {
                    			fielditemList.add(onlyfield.toLowerCase());
                    			fieldItemMap.put(onlyfield.toLowerCase(), "0#1");
                    		}

                    		fieldMap.put(key, fielditemList);
                    		fieldSetMap.put(key.toLowerCase(), fieldItemMap);
                    		fieldNameSetMap.put(key.toLowerCase(), fieldNameMap);
                    	}
                    }

               }
            }else{
	            search = dao.search("select str_value from constant where constant='ZP_FIELD_LIST'");
	            if(search.next()) {
	            	field = search.getString("str_value");
	            	field = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(field);
            }

            if (StringUtils.isNotEmpty(field)) {//例如：field = "A01{A0101[0#1],A0111#出生日期[0#0],A01AX#最高学历毕业日期[0#0],},"
                 String[] temps = field.split(",},");
                 for (int i = 0; i < temps.length; i++) {
                     String setid = temps[i].substring(0, temps[i].indexOf("{"));
                     String fieldstr = temps[i].substring((temps[i].indexOf("{") + 1));
                     ArrayList fielditemList = new ArrayList();
                     String[] fields = fieldstr.split(",");
                     HashMap fieldItemMap = new HashMap();
                     HashMap fieldNameMap = new HashMap();
                     HashMap displayNameMap = new HashMap();
                     for (int n = 0; n < fields.length; n++) {
                         String a = fields[n].substring(0, fields[n].indexOf("[")).toLowerCase();
                         if (a.indexOf("#") > -1){
                             fieldNameMap.put(a.substring(0, a.indexOf("#")), a.substring(a.indexOf("#")+1));
                             a = a.substring(0, a.indexOf("#"));
                         }

                         String b = fields[n].substring((fields[n].indexOf("[") + 1), fields[n].indexOf("]"));
                         fieldItemMap.put(a, b);
                         fielditemList.add(fields[n].substring(0, fields[n].indexOf("[")));
                     }
                     fieldMap.put(setid, fielditemList);
                     fieldSetMap.put(setid.toLowerCase(), fieldItemMap);
                     fieldNameSetMap.put(setid.toLowerCase(), fieldNameMap);
             		}
            	}
            }
            for (String paramskey : paramsMap.keySet()) {
                String value = paramsMap.get(paramskey).get(hireChannel);
                if(!"-1".equals(value)&&fieldMap!=null&&fieldMap.get(paramskey)!=null){//如果子集参数中选择了改招聘渠道
                    LazyDynaBean abean = new LazyDynaBean();
                    abean.set("fieldSetId", paramskey);

                    abean.set("fieldSetDesc", (String)paramsMap.get(paramskey).get("displayname"));

                    if("A01".equalsIgnoreCase(paramskey)) {
                        showSetList.add(0,abean);
                    } else {
                        showSetList.add(abean);
                    }
                }
            }
            ParameterSetBo paramBo = new ParameterSetBo(this.conn);
            ArrayList<String> fieldList = paramBo.getFieldList();
            fieldMap = this.sortConstantMap(fieldList, fieldMap);
            map.put("fieldMap", fieldMap);
            map.put("fieldSetMap", fieldSetMap);
            map.put("fieldNameSetMap", fieldNameSetMap);
            map.put("showSetList", showSetList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 对获取到的子集信息进行排序
     * @param fieldlist
     * @param maps
     * @return
     */
    public Map<String, ArrayList> sortConstantMap(ArrayList<String> fieldlist, Map<String, ArrayList> maps) {
        Map<String, ArrayList> sortMap = null;
        try {
            sortMap = new LinkedHashMap<String, ArrayList>();
            for(String fieldsetid : fieldlist){
                if(null!=fieldsetid&&!"".equals(fieldsetid)&&maps.containsKey(fieldsetid)){
                    sortMap.put(fieldsetid, maps.get(fieldsetid));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sortMap;
    }

    /**
     * 获取简历简历子集，渠道代码作为key值
     * list.add(setList);
     * list.add(mustSet);
     * map.put("01",list);
     * @return
     */
    private ArrayList<String> getChannelSet(Map<String, Map<String, String>> paramsMap, String hireChannel){
    	ArrayList list = new ArrayList();

    	ArrayList setList = new ArrayList();
    	ArrayList<String> mustSet = new ArrayList<String>();
    	for (String key : paramsMap.keySet()) {
    		Map<String, String> tempMap = paramsMap.get(key);
    		for (String hireChannelId : tempMap.keySet()) {
        		if("displayname".equalsIgnoreCase(hireChannelId)) {
                    continue;
                }
        		if(hireChannel.equals(hireChannelId)){
        			if(!"-1".equals(tempMap.get(hireChannelId)))//所有子集
                    {
                        setList.add(key);
                    }

	        		if("2".equals(tempMap.get(hireChannelId)))//必填子集
                    {
                        mustSet.add(key);
                    }
        		}
    		}
		}
		list.add(setList);
		list.add(mustSet);
		return list;
    }

    public ArrayList getSetByWorkExprience(String workexprience) {
    	if(StringUtils.isEmpty(workexprience)) {
            workexprience = getHireChannelFromTable();
        }
    	this.hireChannel = workexprience;
        if (zpFieldMap == null||zpFieldMap.get(this.hireChannel) == null) {
        	this.createChannelParams(hireChannel);
        	this.cacheDataTimeOut(hireChannel);
        	this.getChannelName(hireChannel);
        	this.getAllZpUnitList(hireChannel, "");//在方法内部取显示单位级数
        }
        ZpFieldList = (ArrayList) zpFieldMap.get(this.hireChannel);
        return ZpFieldList;
    }

    /**
     * 计算当前时间与上次缓存时间的时间差，用于判断是否重新加载缓存数据
     * @param type =02|3 ：社会招聘；   =01|4： 校园招聘 ；=nuInfor：单位介绍
     * @return
     */
    private boolean cacheDataTimeOut(String hireChannel) {
    	if(channelMap == null) {
            channelMap = new HashMap();
        }
    	if (EmployNetPortalBo.unCacheDataTime == null && "unInfor".equalsIgnoreCase(hireChannel)){
            return true;
        }else if(channelMap.get(hireChannel)==null||channelMap.get(hireChannel).get("cacheDataTime")==null) {
            return true;
        }

        Date lastDate = null;
        if ("unInfor".equalsIgnoreCase(hireChannel)) {
            lastDate = EmployNetPortalBo.unCacheDataTime;
        } else {
            lastDate = (Date) channelMap.get(hireChannel).get("cacheDataTime");
        }


        boolean flag = true;
        Date now = new Date();
        int sY=DateUtils.getYear(lastDate);
        int sM=DateUtils.getMonth(lastDate);
        int sD=DateUtils.getDay(lastDate);
        int sH=DateUtils.getHour(lastDate);
        int smm=DateUtils.getMinute(lastDate);

        int eY=DateUtils.getYear(now);
        int eM=DateUtils.getMonth(now);
        int eD=DateUtils.getDay(now);
        int eH=DateUtils.getHour(now);
        int emm=DateUtils.getMinute(now);

        GregorianCalendar d1= new GregorianCalendar(sY,sM,sD,sH,smm,00);
        GregorianCalendar d2= new GregorianCalendar(eY,eM,eD,eH,emm,00);
        Date date1= d1.getTime();
        Date date2= d2.getTime();
        long l1=date1.getTime();
        long l2=date2.getTime();
        long mins=(l2-l1)/(60*1000L);

        if(mins < EmployNetPortalBo.cacheDataTimeOutMins) {
            flag =false;
        } else {
            if ("unInfor".equalsIgnoreCase(hireChannel)) {
                EmployNetPortalBo.unCacheDataTime = null;
            } else
            	if(channelMap.get(hireChannel)==null){
                	HashMap infoMap = new HashMap();
                	infoMap.put("cacheDataTime", null);
                	channelMap.put(hireChannel, infoMap);
                }else{
                	channelMap.get(hireChannel).put("cacheDataTime", null);
                }
        }

        return flag;
    }

    /***
     * 获取所有招聘单位
     *
     * @param employObject
     *            招聘对象 01：校园招聘 02：社会招聘 03：内部招聘 out:外部招聘
     * @return
     */
    public ArrayList getAllZpUnitList(String employObject, String unitLevel) {
    	if(channelMap == null) {
            channelMap = new HashMap();
        }
        if(channelMap.get(employObject)!=null&&channelMap.get(employObject).get("unit")!=null&&!this.cacheDataTimeOut(employObject)) {
            return (ArrayList) channelMap.get(employObject).get("unit");
        }

        ArrayList list = new ArrayList();
        RowSet rs = null;
        RowSet rowSet = null;
        try {
            StringBuffer buf = new StringBuffer("");
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            String hire_object = (String) map.get("hire_object");
            unitLevel = (String) map.get("unitLevel");
            if (map.get("hire_object") == null || "".equals((String) map.get("hire_object"))) {
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
            }

            String sql = "select codeitemid,codeitemdesc,grade from organization where parentid=codeitemid";
            if(StringUtils.isNotEmpty(unitLevel) && !"0".equalsIgnoreCase(unitLevel)) {
                sql += " and grade<= " + unitLevel;
                this.lastLvUnits = getLastLvUnits(unitLevel);
            }
            String unitOrDepart="";
    		if(map.get("unitOrDepart") != null && ((String)map.get("unitOrDepart")).trim().length()>0) {
                unitOrDepart=(String)map.get("unitOrDepart");
            }

            sql += " order by a0000";
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                String id = rowSet.getString("codeitemid");
                int grade = rowSet.getInt("grade");
                buf.setLength(0);
                /** 建研院专版 显示所有两层级单位 **/
                if ("jyy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                    buf.append(" select codeitemid,codeitemdesc,a0000 from organization  where codesetid='UN' and parentid='"
                                    + id + "'");
                    buf.append(" order by a0000");
                } else {
                    buf = getSelectSql(employObject, hire_object, id, "UN","z0321",unitOrDepart);
                    buf.append(" order by A0000");
                }

                rs = dao.search(buf.toString());
                ArrayList alist = new ArrayList();
                boolean isadd = false;
                int j = 0;
                int level = StringUtils.isEmpty(unitLevel) ? 0 : Integer.parseInt(unitLevel);
                while (rs.next()) {
                    String codeitemid = rs.getString("codeitemid");
                    if (codeitemid.equalsIgnoreCase(id) || level == grade) {
                        isadd = true;
                        continue;
                    }

                   int unitLayer = rs.getInt("grade");
                    if(unitLayer > level && level !=0) {

                        continue;
                    }

                    String codeitemdesc = rs.getString("codeitemdesc");
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("codeitemid", codeitemid);
                    int length = codeitemdesc.getBytes().length;
                    String altdesc = codeitemdesc;
                    if (length > 30) {
                        ArrayList blist = this.getMsgList(codeitemdesc, 30);
                        codeitemdesc = (String) blist.get(0) + "...";
                    }

                    bean.set("altdesc", altdesc);
                    bean.set("codeitemdesc", codeitemdesc);
                    bean.set("id_r", id + "_" + j);
                    alist.add(bean);
                    j++;
                }

                if (alist.size() > 0 || isadd) {
                    LazyDynaBean abean = new LazyDynaBean();
                    abean.set("codeitemid", id);

                    String codeitemdesc = rowSet.getString("codeitemdesc");
                    int length = codeitemdesc.getBytes().length;
                    String altdesc = codeitemdesc;
                    if (length > 30) {
                        ArrayList blist = this.getMsgList(codeitemdesc, 30);
                        codeitemdesc = (String) blist.get(0) + "...";
                    }
                    abean.set("codeitemdesc", codeitemdesc);
                    abean.set("altdesc", altdesc);
                    abean.set("list", alist);
                    abean.set("count", j + "");
                    abean.set("id_img", id + "_img");
                    list.add(abean);
                }

            }
            if(channelMap.get(employObject)==null){
            	HashMap infoMap = new HashMap();
            	infoMap.put("unit", list);
            	channelMap.put(employObject, infoMap);
            }else{
            	channelMap.get(employObject).put("unit", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(rowSet);
        }
        return list;
    }

    /**
     * 获取外网招聘职位信息
     * @param unitList
     * @param employObject 招聘渠道 01：校园招聘 02：社会招聘 03：内部招聘 out:外部招聘
     * @return
     * @throws GeneralException
     */
    public ArrayList getZpPostList(ArrayList unitList, String employObject, String unitLevel)  throws GeneralException {
    	if(channelMap == null) {
            channelMap = new HashMap();
        }
    	if(channelMap.get(employObject)!=null&&channelMap.get(employObject).get("pos")!=null&&!this.cacheDataTimeOut(employObject)) {
            return (ArrayList) channelMap.get(employObject).get("pos");
        }

        ArrayList list = new ArrayList();
        try {
            HashMap unitPosMap = getPositionInterviewMap2(unitList,employObject, unitLevel);
            list = getUnitList(unitPosMap,unitList, unitLevel);

	        if(channelMap.get(employObject)==null){
	        	HashMap infoMap = new HashMap();
	        	infoMap.put("pos", list);
	        	channelMap.put(employObject, infoMap);
	        }else{
	        	channelMap.get(employObject).put("pos", list);
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取招聘公告标题 String opt 接口 1：ehr系统公告 2 ：招聘公告 String hireChannel 公告类型 1.ehr首页公告 2
     * 招聘首页公告， 返回值 list 封装bean ，bean中存放 id 公告 id；flag
     * 公告类型；title公告标题；content公告内容；down 是否为直接下载文档类型 true 是 false否；
     * @param other_flag
     *
     * */
    public ArrayList SQLExecute(String opt, String hireChannel, String other_flag) throws GeneralException {
    	if(channelMap == null) {
            channelMap = new HashMap();
        }
    	if(!"2".equals(hireChannel)){
    		if(channelMap.get(hireChannel)!=null&&channelMap.get(hireChannel).get("board")!=null&&!this.cacheDataTimeOut(hireChannel)) {
                return (ArrayList) channelMap.get(hireChannel).get("board");
            }
    	}

        ArrayList list = new ArrayList();
        ArrayList<String> values = new ArrayList<String>();
        StringBuffer strsql = new StringBuffer();
        strsql.append("select id,topic,content,ext,");
        //YYYY-MM-DD HH24:MM:SS 时分秒暂不显示
		strsql.append(Sql_switcher.dateToChar("createtime", "YYYY-MM-DD")+" createtime ");
        strsql.append(" from announce ");
        if(StringUtils.isNotEmpty(hireChannel)) {
        	strsql.append(" where flag=? ");
        	values.add(hireChannel);
        }
        else {
        	strsql.append(" where other_flag=? ");
        	values.add(other_flag);
        }

        strsql.append(" and createtime+").append("(case when "+Sql_switcher.isnull("period", "0")+">999999 then 999999 else "+Sql_switcher.isnull("period", "0")+" end) ").append(">=").append(Sql_switcher.sqlNow());
        strsql.append(" order by priority asc,createtime desc");
        String id = "";
        LazyDynaBean bean = null;
        RowSet rs = null;
        try {
            rs = dao.search(strsql.toString(), values);
            while (rs.next()) {
//                boolean flag = false;
                bean = new LazyDynaBean();
                RecordVo vo = new RecordVo("announce");
                id = rs.getString("id");
                bean.set("id", PubFunc.encrypt(id));
                bean.set("flag", "2");
                bean.set("createtime", rs.getString("createtime"));
                vo.setString("id", rs.getString("id"));

                String temp = rs.getString("topic");
                if (temp != null) {
                    bean.set("title", temp);
                } else {
                    continue;
                }

                temp = Sql_switcher.readMemo(rs, "content");
                if (temp == null || "".equals(temp)) {
//                    flag = true;
                } else {
                    bean.set("content", temp);
                }
                temp = rs.getString("ext");

            	if (temp != null && temp.trim().length() > 0) {
                	bean.set("ext", temp);
                    bean.set("down", "true");
                    bean.set("href", "/selfservice/welcome/downboardview?encryptParam="+PubFunc.encryption("id="+bean.get("id")+"&topic="+bean.get("title")+"&ext="+temp+"&hireNetPortal=hireNetPortal"));
                } else {
                    bean.set("down", "false");
                }

                if (temp != null && temp.trim().length() > 0) {

                    bean.set("hasfile", "true");
                }
                list.add(bean);
            }

        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        } finally {
            PubFunc.closeResource(rs);
        }

        if(channelMap.get(hireChannel)==null){
        	HashMap infoMap = new HashMap();
        	infoMap.put("board", list);
        	channelMap.put(hireChannel, infoMap);
        }else{
        	channelMap.get(hireChannel).put("board", list);
        }

        return list;
    }

    /**
     * 取得当前页面 简历信息列表
     *
     * @param fieldSetList
     *            //指标集
     * @param currentSetID
     * @param fieldMap
     * @param a0100
     * @param dbName
     * @return
     * @throws GeneralException
     */
    public ArrayList getResumeFieldList(ArrayList fieldSetList, HashMap fieldSetMap,
            int currentSetIndex, HashMap fieldMap, String a0100, String dbName, String i9999)
            throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        RowSet rowSet2 = null;
        boolean hasValue = false; // 是否有值
        HashMap<String, HashMap<String, String>> fieldNamesMap = (HashMap<String, HashMap<String, String>>) ZpFieldList.get(3);
        try {
            LazyDynaBean a_bean = (LazyDynaBean) fieldSetList.get(currentSetIndex);
            String setID = (String) a_bean.get("fieldSetId");
            HashMap fielditemMap = (HashMap) fieldSetMap.get(setID.toLowerCase());
            String sql = "";
            if ("A01".equalsIgnoreCase(setID) || "A0A".equalsIgnoreCase(setID)) {
                sql = "select * from  " + dbName + setID + " where a0100='" + a0100 + "'";
            } else {
                sql = "select * from  " + dbName + setID + " where a0100='" + a0100 + "' and i9999=" + i9999;
            }
            if("order".equals(i9999)) {
                sql = "select * from  " + dbName + setID + " where a0100='" + a0100 + "' order by i9999 desc";
            }

            rowSet2 = dao.search(sql);
            if (rowSet2.next()) {
                hasValue = true;
            }

            HashMap<String, String> fieldNameMap = new HashMap<String, String>();
            if(fieldNamesMap != null) {
                fieldNameMap = fieldNamesMap.get(setID.toLowerCase());
            }

            rowSet = dao.search(getFieldItemSql(fieldMap, setID));
            int i = 0;
            HashMap multilayerCodeSetMap = new HashMap();
            SimpleDateFormat dateFormat = null;
            while (rowSet.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                String useflag = rowSet.getString("useflag");// 是否有效
                if ("0".equals(useflag)) {
                    continue;
                }

                String codesetid = rowSet.getString("codesetid");
                String itemtype = rowSet.getString("itemtype");
                if (!"0".equals(codesetid) && i == 0) {// 当得到第一个代码类字段时,将所有的系统中所有多层代码 的信息都取出来放在multilayerCodeSetMap中

                    multilayerCodeSetMap = getMultilayerCodeSetMap();
                    i++;
                }

                String isMore = getIsMore(multilayerCodeSetMap, codesetid);// 取得当前字段是不是多层级的代码类,单位部门岗位自动按照多层级处理,非代码类按照单层级处理
                String itemid = rowSet.getString("itemid");
                FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
                if (item != null && item.isSequenceable() && !this.isVisiablSeqField)// 特殊的字段不能对外展现
                {
                    continue;
                }
                /** 是否自动生成值的指标 */
                String isseqn = "0";
                if (item != null && item.isSequenceable() && "a01".equalsIgnoreCase(setID)) {
                    isseqn = "1";
                }

                abean.set("isseqn", isseqn);
                String show_must = (String) fielditemMap.get(itemid.toLowerCase());
                abean.set("show", show_must.split("#")[0]);// 是否是前台显示指标
                abean.set("must", show_must.split("#")[1]);// 是否是必填指标
                abean.set("itemid", itemid);
                abean.set("decimalwidth", rowSet.getString("decimalwidth"));
                abean.set("itemlength", rowSet.getString("itemlength"));
                String value = "";
                String viewvalue = "";
                if (hasValue) {// 如果有值
                    if ("A".equals(itemtype)) {
                        if (rowSet2.getString(itemid) != null) {
                            if ("0".equals(codesetid)) {
                                value = rowSet2.getString(itemid);
                            } else {
                                value = rowSet2.getString(itemid);
                                viewvalue = AdminCode.getCodeName(codesetid, rowSet2.getString(itemid));
                            }
                        }
                    } else if ("M".equals(itemtype)) {
                        value = Sql_switcher.readMemo(rowSet2, itemid);
                    } else if ("D".equals(itemtype)) {
                        if ("7".equals(rowSet.getString("itemlength"))) {
                            dateFormat = new SimpleDateFormat("yyyy-MM");
                        } else {
                            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        }

                        if (rowSet2.getDate(itemid) != null) {
                            value = dateFormat.format(rowSet2.getDate(itemid));
                        }

                    } else if ("N".equals(itemtype)) {
                        if (rowSet2.getString(itemid) != null) {
                            value = rowSet2.getString(itemid);
                        }
                    }

                }

                abean.set("value", keyWord_reback(value));
                abean.set("viewvalue", viewvalue);
                abean.set("itemtype", itemtype);
                abean.set("codesetid", codesetid);
                if(fieldNameMap != null && fieldNameMap.containsKey(itemid.toLowerCase())) {
                    abean.set("itemdesc", fieldNameMap.get(itemid.toLowerCase()));
                } else {
                    abean.set("itemdesc", rowSet.getString("itemdesc"));
                }

                abean.set("isMore", isMore); // 0：一层代码 1：多层代码
                abean.set("itemmemo", Sql_switcher.readMemo(rowSet, "itemmemo"));// 关于当前指标的描述
                if("order".equals(i9999)) {
                    abean.set("itemmemo2", Sql_switcher.readMemo(rowSet, "itemmemo").replaceAll("\r\n"," "));
                }

                if ("0".equals(isMore) && !"0".equals(codesetid)) {
                    abean.set("options", getOptions(codesetid));// 将单层代码类的选项,生成select--option
                }

                list.add(abean);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(rowSet2);
        }
        return list;
    }

    public ArrayList getResumeFieldList2(ArrayList fieldSetList, HashMap fieldSetMap,
            int currentSetIndex, HashMap fieldMap, String a0100, String dbName) throws GeneralException{
    	ArrayList resumeFieldList = this.getResumeFieldList(fieldSetList, fieldSetMap, currentSetIndex, fieldMap, a0100, dbName, "order");
		return resumeFieldList;
    }

    public ArrayList getResumeFieldList2(ArrayList fieldSetList, HashMap fieldSetMap,
            int currentSetIndex, HashMap fieldMap, String a0100, String dbName, String i9999,
            boolean flag) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        RowSet rowSet2 = null;
        boolean hasValue = false; // 是否有值
        HashMap<String, HashMap<String, String>> fieldNamesMap = (HashMap<String, HashMap<String, String>>) ZpFieldList.get(3);
        try {
            LazyDynaBean a_bean = (LazyDynaBean) fieldSetList.get(currentSetIndex);
            String setID = (String) a_bean.get("fieldSetId");
            HashMap<String, String> fieldNameMap = new HashMap<String, String>();
            if(fieldNamesMap != null) {
                fieldNameMap = fieldNamesMap.get(setID);
            }

            HashMap fielditemMap = (HashMap) fieldSetMap.get(setID.toLowerCase());
            if ("A01".equalsIgnoreCase(setID)) {
                rowSet2 = dao.search("select * from  " + dbName + setID + " where a0100='" + a0100 + "'");
            } else {
                rowSet2 = dao.search("select * from  " + dbName + setID + " where a0100='" + a0100 + "' and i9999=" + i9999);
            }
            if (rowSet2.next()) {
                hasValue = true;
            }
            boolean ebool = false;
            boolean bbool = false;
            rowSet = dao.search(getFieldItemSql(fieldMap, setID));
            if (flag) {
                while (rowSet.next()) {
                    String itemid = rowSet.getString("itemid");
                    if ("e0122".equalsIgnoreCase(itemid)) {
                        ebool = true;
                    }
                    if ("b0110".equalsIgnoreCase(itemid)) {
                        bbool = true;
                    }
                }
                rowSet = dao.search(getFieldItemSql(fieldMap, setID));
            }
            int i = 0;
            HashMap multilayerCodeSetMap = new HashMap();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            while (rowSet.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                String useflag = rowSet.getString("useflag");
                if ("0".equals(useflag)) {
                    continue;
                }
                String codesetid = rowSet.getString("codesetid");
                String itemtype = rowSet.getString("itemtype");
                if (!"0".equals(codesetid) && i == 0) {
                    multilayerCodeSetMap = getMultilayerCodeSetMap();
                    i++;
                }

                String isMore = getIsMore(multilayerCodeSetMap, codesetid);
                String itemid = rowSet.getString("itemid");
                FieldItem item = DataDictionary.getFieldItem(itemid);
                /** 如果是自动增加的序号指标，并且简历状态不满足设置的条件，不予显示出来 */
                if (item != null && item.isSequenceable() && !this.isVisiablSeqField) {
                    continue;
                }
                String show_must = (String) fielditemMap.get(itemid.toLowerCase());
                abean.set("show", show_must.split("#")[0]);
                abean.set("must", show_must.split("#")[1]);
                abean.set("itemid", itemid);
                abean.set("decimalwidth", rowSet.getString("decimalwidth"));
                abean.set("itemlength", rowSet.getString("itemlength"));
                String value = "";
                String viewvalue = "";
                if (hasValue) // 如果有值
                {
                    if ("A".equals(itemtype)) {
                        if (rowSet2.getString(itemid) != null) {
                            if ("0".equals(codesetid)) {
                                value = rowSet2.getString(itemid);
                            } else {
                                value = rowSet2.getString(itemid);
                                viewvalue = AdminCode.getCodeName(codesetid, rowSet2
                                        .getString(itemid));
                            }
                        }
                    } else if ("M".equals(itemtype)) {
                        value = Sql_switcher.readMemo(rowSet2, itemid);
                    } else if ("D".equals(itemtype)) {
                        if (rowSet2.getDate(itemid) != null) {
                            value = dateFormat.format(rowSet2.getDate(itemid));
                        }
                    } else if ("N".equals(itemtype)) {
                        if (rowSet2.getString(itemid) != null) {
                            value = rowSet2.getString(itemid);
                        }
                    }

                }
                abean.set("value", value);
                abean.set("viewvalue", viewvalue);
                abean.set("itemtype", itemtype);
                abean.set("codesetid", codesetid);
                if(fieldNameMap != null && fieldNameMap.containsKey(itemid.toLowerCase())) {
                    abean.set("itemdesc", fieldNameMap.get(itemid.toLowerCase()));
                } else {
                    abean.set("itemdesc", rowSet.getString("itemdesc"));
                }

                abean.set("isMore", isMore); // 0：一层代码 1：多层代码
                abean.set("itemmemo", Sql_switcher.readMemo(rowSet, "itemmemo"));
                if ("0".equals(isMore) && !"0".equals(codesetid)) {
                    abean.set("options", getOptions(codesetid));
                }
                list.add(abean);

            }
            if (flag) {
                if (!ebool) {
                    // wangcq 2015-03-21 begin 新招聘目前只显示选择了的子集，部门和单位先不必须添加
                    // LazyDynaBean bean=new LazyDynaBean();
                    // bean.set("show","0");
                    // bean.set("must","0");
                    // bean.set("itemid","e0122");
                    // bean.set("decimalwidth","0");
                    // bean.set("itemlength","50");
                    // bean.set("value",rowSet2.getString("e0122")==null?"":rowSet2.getString("e0122"));
                    // bean.set("viewvalue",AdminCode.getCodeName("UM",
                    // rowSet2.getString("e0122")==null?"":rowSet2.getString("e0122")));
                    // bean.set("itemtype","A");
                    // bean.set("codesetid","UM");
                    // bean.set("itemdesc","部门");
                    // bean.set("isMore","0"); //0：一层代码 1：多层代码
                    // bean.set("itemmemo","部门");
                    // list.add(0,bean);
                }
                if (!bbool) {
                    // LazyDynaBean bean=new LazyDynaBean();
                    // bean.set("show","0");
                    // bean.set("must","0");
                    // bean.set("itemid","b0110");
                    // bean.set("decimalwidth","0");
                    // bean.set("itemlength","50");
                    // bean.set("value",rowSet2.getString("b0110")==null?"":rowSet2.getString("b0110"));
                    // bean.set("viewvalue",AdminCode.getCodeName("UN",
                    // rowSet2.getString("b0110")==null?"":rowSet2.getString("b0110")));
                    // bean.set("itemtype","A");
                    // bean.set("codesetid","UN");
                    // bean.set("itemdesc","单位");
                    // bean.set("isMore","0"); //0：一层代码 1：多层代码
                    // bean.set("itemmemo","单位");
                    // list.add(0,bean);
                    // wangcq 2015-03-21 end
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(rowSet2);
        }
        return list;
    }

    /**
     * 获取外网职位列表显示指标
     * @return
     */
    public ArrayList getPosListField() {
    	if(posListField == null || posListField.size() == 0) {
    		EmployNetPortalBo.getPosFields();
    	}
        return posListField;
    }

	private static synchronized ArrayList getPosFields() {
		Connection conn = null;
		if (posListField == null || posListField.size() == 0) {
            try {
            	conn = AdminDb.getConnection();
            	posListField = new ArrayList();
                ParameterXMLBo bo = new ParameterXMLBo(conn,"1");
                HashMap map = bo.getAttributeValues();
                String pos_listfield = "";
                //职位列表默认显示指标职位名称，需求单位，工作地点，发布时间，应聘操作
                String[] pos_field = {"z0351","z0321","z0333","opentime","ypljl"};
                if (map.get("pos_listfield") != null
                        && ((String) map.get("pos_listfield")).length() > 0) {
                    pos_listfield = (String) map.get("pos_listfield");
                    if(pos_listfield.indexOf("Z0351")==-1) {
                        pos_listfield = "Z0351`"+pos_listfield;
                    }

                    if(pos_listfield.indexOf("ypljl")==-1) {
                        pos_listfield = pos_listfield+"`ypljl";
                    }

                    pos_field = pos_listfield.split("`");

                }

                for (String field : pos_field) {
                    if (field == null || "".equals(field)) {
                        continue;
                    }

                    if ("yprsl".equalsIgnoreCase(field)) {
                        LazyDynaBean bean = new LazyDynaBean();
                        bean.set("itemid", field.toLowerCase());
                        bean.set("itemtype", "N");
                        bean.set("codesetid", "0");
                        bean.set("deciwidth", "0");
                        bean.set("itemdesc", "应聘人数");
                        posListField.add(bean);
                    } else if ("ypljl".equalsIgnoreCase(field)) {
                        LazyDynaBean bean = new LazyDynaBean();
                        bean.set("itemid", field.toLowerCase());
                        bean.set("itemtype", "A");
                        bean.set("codesetid", "0");
                        bean.set("deciwidth", "0");
                        bean.set("itemdesc", "应聘");
                        posListField.add(bean);
                    } else if ("opentime".equalsIgnoreCase(field)) {
                        LazyDynaBean bean = new LazyDynaBean();
                        bean.set("itemid", field.toLowerCase());
                        bean.set("itemtype", "D");
                        bean.set("codesetid", "0");
                        bean.set("deciwidth", "0");
                        bean.set("itemdesc", "发布日期");
                        posListField.add(bean);
                    }else {
                    	FieldItem item = DataDictionary.getFieldItem(field.toLowerCase());
                    	if (item != null && "1".equals(item.getUseflag())) {
                    		LazyDynaBean bean = new LazyDynaBean();
                    		bean.set("itemid", item.getItemid().toLowerCase());
                    		bean.set("itemtype", item.getItemtype());
                    		bean.set("codesetid", item.getCodesetid());
                    		bean.set("deciwidth", item.getDecimalwidth() + "");
                    		bean.set("itemdesc", item.getItemdesc());
                    		posListField.add(bean);
                    	}
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	PubFunc.closeResource(conn);
            }
        }
        return posListField;
	}

    /**
     * 工作经验指标
     * @return
     */
    public String getWorkExperience() {
        if (workExperience == null || "".equals(workExperience)) {
            try {
                ParameterXMLBo bo = new ParameterXMLBo(this.conn, "0");
                HashMap map = bo.getAttributeValues();
                if (map != null && map.get("workExperience") != null
                        && !"".equals((String) map.get("workExperience"))) {
                    workExperience = (String) map.get("workExperience");
                    isDefineWorkExperience = "1";
                    FieldItem item = DataDictionary.getFieldItem(workExperience.toLowerCase());
                    if (item != null) {
                        workExperienceDesc = item.getItemdesc();
	                    if (item.isCode()) {
                            workExperienceCodeList = this.getCodeList(item.getCodesetid());
                        }

                    }
                } else {
                    isDefineWorkExperience = "0";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return workExperience;
    }

    public ArrayList getCodeList(String codesetid) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
            rs = dao.search("select codeitemid,codeitemdesc from codeitem where codesetid='"
                    + codesetid + "' and " + Sql_switcher.dateValue(bosdate)
                    + " between start_date and end_date order by codeitemid");
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                String itemid = rs.getString("codeitemid");
                bean.set("codeitemdesc", rs.getString("codeitemdesc"));
                bean.set("codeitemid", itemid);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    // 得到招聘应用库
    public String getZpkdbName(){
        if (StringUtils.isEmpty(a_dbName)) {
            try {
                RecordVo vo = ConstantParamter.getRealConstantVo("ZP_DBNAME");
                if (vo == null) {
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));
                }
                a_dbName = vo.getString("str_value");
                if (StringUtils.isEmpty(a_dbName)) {
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return a_dbName;
    }

    public synchronized String getInterviewingRevertCodeValue(String a0100, String itemid) {
        String codeValue = "";
        RowSet rs = null;
        try {

            String sql = " select " + itemid + " from " + a_dbName + "a01 where a0100='" + a0100 + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                if (StringUtils.isNotEmpty(rs.getString(itemid))) {
                    codeValue = rs.getString(itemid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return codeValue;
    }

    /**
     * 根据应聘人员申请岗位判断应聘渠道，从而取对应的预览简历登记表
     *
     * @Title: getResumeTemplateId
     * @Description:
     * @param dao
     * @param hireParams
     * @param a0100
     * @return
     */
    public String getResumeTemplateId(ContentDAO dao, HashMap hireParams, String a0100) {
        String templateId = "-1";

        if (hireParams == null) {
            return templateId;
        }

        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select z0336 from Z03,zp_pos_tache");
            sql.append(" where Z03.Z0301=zp_pos_tache.ZP_POS_ID");
            sql.append(" and a0100=?");

            ArrayList sqlParam = new ArrayList();
            sqlParam.add(a0100);

            rs = dao.search(sql.toString(), sqlParam);
            if (rs.next()) {
                // z0336应聘渠道
                String z0336 = rs.getString("z0336");
                if (null != z0336) {
                    templateId = (String) hireParams.get("CARDTABLE_" + z0336);
                    templateId = PubFunc.hireKeyWord_filter_reback(templateId);
                    if (null == templateId || "".equals(templateId) || "#".equals(templateId)) {
                        templateId = "-1";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        // 没有针对应聘渠道单独制定模板，取公用的预览简历登记表
        if (null == templateId || "".equals(templateId) || "-1".equals(templateId)) {
            templateId = (String) hireParams.get("CARDTABLE_02");
            if (null == templateId || "".equals(templateId) || "#".equals(templateId)) {
                templateId = "-1";
            }
        }

        return templateId;
    }

    /**
     * 取应聘人员简历登记模板id
     * 模板获取优先级：应聘身份->应聘身份上级->已申请职位所属渠道->已申请职位渠道上级->社会招聘
     * @param a0100 应聘人员id
     * @return 登记表id
     */
    public String getResumeTemplateId(String a0100) {
    	String templateId = "-1";
    	String channelId = "";
    	ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
        HashMap hireParamMap = null;
		try {
			hireParamMap = parameterXMLBo.getAttributeValues();
			//应聘身份指标
			String candidateStatusItemId = (String)hireParamMap.get("candidate_status");
			//应聘身份id（渠道id)
			if(StringUtils.isNotEmpty(candidateStatusItemId) && !"#".equals(candidateStatusItemId)) {
				channelId = this.getCandidateStatus(candidateStatusItemId, a0100);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}

		if (hireParamMap == null) {
            return templateId;
        }

		templateId = getTemplateId(channelId, hireParamMap);

		if (StringUtils.isNotEmpty(templateId) && !"#".equals(templateId)) {
			return templateId;
		}

		// 从已应聘职位中判断渠道
		RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select z0336 from Z03,zp_pos_tache");
            sql.append(" where Z03.Z0301=zp_pos_tache.ZP_POS_ID");
            sql.append(" and a0100=?");

            ArrayList sqlParam = new ArrayList();
            sqlParam.add(a0100);

            rs = dao.search(sql.toString(), sqlParam);
            if (rs.next()) {
                // z0336应聘渠道
                channelId = rs.getString("z0336");
                templateId = getTemplateId(channelId, hireParamMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        // 没有针对应聘渠道单独制定模板，取公用的预览简历登记表
        if (StringUtils.isEmpty(templateId) || "-1".equals(templateId)) {
            templateId = (String) hireParamMap.get("CARDTABLE_02");
            if (null == templateId || "".equals(templateId) || "#".equals(templateId)) {
                templateId = "-1";
            }
        }

		return templateId;
    }

    /**
     * 从招聘参数设置中获取某渠道对应的简历登记表
     * @param channelId 渠道id
     * @param hireParamMap 招聘参数map
     * @return 登记表id
     */
    private String getTemplateId(String channelId, HashMap hireParamMap) {
    	if (StringUtils.isEmpty(channelId)) {
            return "";
        }

    	String templateId = (String) hireParamMap.get("CARDTABLE_" + channelId);
        templateId = PubFunc.hireKeyWord_filter_reback(templateId);
        if (StringUtils.isEmpty(templateId) || "#".equals(templateId)) {
			CodeItem codeItem = AdminCode.getCode("35", channelId);
			if (codeItem != null) {
				channelId = codeItem.getPcodeitem();
				if (StringUtils.isNotEmpty(channelId) && !codeItem.getCodeitem().equalsIgnoreCase(channelId)) {
					templateId = (String) hireParamMap.get("CARDTABLE_" + channelId);
					templateId = PubFunc.hireKeyWord_filter_reback(templateId);
				}
			}
		}

        return StringUtils.isEmpty(templateId) || "#".equals(templateId) ? "" : templateId;
	}

    /**
     * 判断zp_pos_tache表里是否有 NBASE字段,没有就新增
     *
     */
    public void autoCreateZPTColumn() {
        try {
            RecordVo vo = new RecordVo("zp_pos_tache");
            if (!vo.hasAttribute("nbase")) {
                DbWizard dbWizard = new DbWizard(this.conn);
                Table table0 = new Table("zp_pos_tache");
                Field temp = new Field("nbase", "nbase");
                temp.setNullable(false);
                temp.setDatatype(DataType.STRING);
                temp.setLength(3);
                table0.addField(temp);
                dbWizard.addColumns(table0);
                DBMetaModel dbmodel = new DBMetaModel(this.conn);
                dbmodel.reloadTableModel("zp_pos_tache");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断职位是否有职位说明书
     *
     * @param e01a1
     * @return
     */
    public String getPosIsBooklet(String e01a1) {
        String flag = "0";
        RowSet rowSet = null;
        try {
            rowSet = dao.search("select ext from k00 where e01a1='" + e01a1 + "' and flag='K'");
            if (rowSet.next()) {
                flag = "1";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return flag;
    }

    public RecordVo getRecordVo(String z0301) {
        RecordVo vo = new RecordVo("z03");
        try {
            vo.setString("z0301", z0301);
            vo = dao.findByPrimaryKey(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    public String getCondSql(String posQueryFieldIDs) {
        StringBuffer whl = new StringBuffer("");
        if (posQueryFieldIDs.indexOf("`") == -1) {
            whl.append(",'" + posQueryFieldIDs + "'");
        } else {
            String[] fields = posQueryFieldIDs.split("`");
            for (int i = 0; i < fields.length; i++) {
                if (fields[i] == null || "".equals(fields[i])) {
                    continue;
                }

                whl.append(",'" + fields[i] + "'");
            }
        }

        return "select * from t_hr_busiField where itemid in (" + whl.substring(1) + ")  and UPPER(fieldsetid)='Z03' and useflag='1'";
    }

    /**
     *
     * @param flag
     *            1添加浏览纪录 2：添加申请纪录
     */
    public void addStatInfo(int flag, String z0301) {
        RowSet rowSet = null;
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            StringBuffer whl = new StringBuffer(" where  z0301='");
            whl.append(z0301);
            whl.append("' and ");
            whl.append(Sql_switcher.year("create_date"));
            whl.append("=" + year);
            whl.append(" and ");
            whl.append(Sql_switcher.month("create_date") + "=" + month + " and " + Sql_switcher.day("create_date") + "=" + day);
            String sql = "select * from zp_static_info " + whl.toString();
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                int b_count = rowSet.getInt("b_count");
                int a_count = rowSet.getInt("a_count");
                if (flag == 1) { // 添加浏览纪录
                    b_count++;
                    dao.update("update zp_static_info set b_count=" + b_count + whl.toString());
                } else {
                    a_count++;
                    dao.update("update zp_static_info set a_count=" + a_count + whl.toString());
                }
            } else {
                RecordVo vo = new RecordVo("zp_static_info");
                vo.setString("z0301", z0301);
                vo.setDate("create_date", calendar.getTime());
                vo.setInt("b_count", 1);
                vo.setInt("a_count", 0);
                int week = 0;
                if (calendar.get(calendar.DAY_OF_WEEK) == 1) {
                    week = 7;
                } else {
                    week = calendar.get(calendar.DAY_OF_WEEK) - 1;
                }

                vo.setInt("the_week", week);
                dao.addValueObject(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
    }

    /**
     * 取得系统中所有多层代码 的信息
     *
     * @param flag
     *            0: 代码 1：单位
     * @return
     */
    public HashMap getMultilayerCodeSetMap() {
        HashMap map = new HashMap();
        RowSet rowSet = null;
        try {
            rowSet = dao.search("select distinct codesetid from codeitem  where codeitemid<>parentid");
            while (rowSet.next()) {
                map.put(rowSet.getString("codesetid"), "1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return map;
    }

    /**
     * 判断该代码是否是多层的
     *
     * @param multilayerCodeSetMap
     * @param codeSetID
     * @return 0：一层 1：多层
     */
    public String getIsMore(HashMap multilayerCodeSetMap, String codeSetID) {
        String isMore = "0";
        try {
            /** 原来单位部门职位按一层处理 */
            if ("0".equals(codeSetID)) {
                return isMore;
            }

            if ("UN".equals(codeSetID) || "UM".equals(codeSetID) || "@K".equals(codeSetID)) {
                isMore = "1";
                return isMore;
            }

            if (multilayerCodeSetMap != null && multilayerCodeSetMap.get(codeSetID) != null) {
                isMore = "1";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isMore;
    }

    /** 外网职位搜素用 */
    public String getIsMore2(HashMap multilayerCodeSetMap, String codeSetID) {
        String isMore = "0";
        try {
            /** 原来单位部门职位按一层处理 */
            if ("0".equals(codeSetID) || "UN".equals(codeSetID) || "UM".equals(codeSetID) || "@K".equals(codeSetID)) {
                return isMore;
            }

            if (multilayerCodeSetMap != null && multilayerCodeSetMap.get(codeSetID) != null) {
                isMore = "1";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMore;
    }

    public ArrayList getOptions(String codesetid) {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            String sql = "";
            if (!"UN".equals(codesetid) && !"UM".equals(codesetid) && !"@K".equals(codesetid)) {
                getCodeSetMap();
                if (codeSetMap.get(codesetid.toLowerCase()) != null) {
                    return (ArrayList) codeSetMap.get(codesetid.toLowerCase());
                } else {
                    return new ArrayList();
                }
            } else {
                String column = "";
                if ("UN".equals(codesetid)) {
                    column = "z0321";
                } else if ("UM".equals(codesetid)) {
                    column = "z0325";
                } else if ("@K".equals(codesetid)) {
                    column = "z0311";
                }

                sql = "select codeitemid,codeitemdesc  from organization where codesetid='"
                        + codesetid + "'" + " and codeitemid in (select distinct " + column
                        + " from z03 where z0319='04' " + getDateSql(">=", "Z0329")
                        + getDateSql("<=", "Z0331") + ")" + " order by codeitemid";

                rowSet = dao.search(sql);
                HashMap unMap = new HashMap();
                while (rowSet.next()) {
                    if (unMap.get(rowSet.getString("codeitemdesc")) != null) {
                        continue;
                    }

                    LazyDynaBean abean = new LazyDynaBean();
                    abean.set("value", rowSet.getString("codeitemid"));
                    abean.set("name", rowSet.getString("codeitemdesc"));

                    unMap.put(rowSet.getString("codeitemdesc"), "1");

                    list.add(abean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }

    /**
     * 根据代码型指标的代码类取得值列表
     * @param codesetid
     * @param itemid
     * @param hireChannel
     * @return
     */
    public ArrayList getOptions2(String codesetid, String itemid, String hireChannel) {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            String sql = "";
            if (!"UN".equals(codesetid) && !"UM".equals(codesetid) && !"@K".equals(codesetid)) {
                getCodeSetMap();
                if (codeSetMap.get(codesetid.toLowerCase()) != null) {
                    return (ArrayList) codeSetMap.get(codesetid.toLowerCase());
                } else {
                    return new ArrayList();
                }

            } else {
                sql = "select codeitemid,codeitemdesc  from organization where codesetid='"
                        + codesetid + "'" + " and codeitemid in (select distinct " + itemid
                        + " from z03 where z0319='04' " + getDateSql(">=", "Z0329")
                        + getDateSql("<=", "Z0331");
                if (hireChannel != null && !"out".equalsIgnoreCase(hireChannel)) {
                    sql += " and z0336='" + hireChannel + "'";
                }

                sql += ")";
                sql += " order by codeitemid";

                rowSet = dao.search(sql);
                ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn, "1");
                HashMap map = xmlBo.getAttributeValues();
                String schoolPosition = "";
                if (map.get("schoolPosition") != null) {
                    schoolPosition = (String) map.get("schoolPosition");
                }

                HashMap unMap = new HashMap();
                while (rowSet.next()) {
                    if (schoolPosition != null && !"".equals(schoolPosition)) {
                        if (schoolPosition.equalsIgnoreCase(rowSet.getString("codeitemid"))) {
                            continue;
                        }
                    }

                    if (unMap.get(rowSet.getString("codeitemdesc")) != null) {
                        continue;
                    }

                    LazyDynaBean abean = new LazyDynaBean();
                    abean.set("value", rowSet.getString("codeitemid"));
                    abean.set("name", rowSet.getString("codeitemdesc"));

                    unMap.put(rowSet.getString("codeitemdesc"), "1");

                    list.add(abean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }

    public void getCodeSetMap() {
        RowSet rowSet = null;
        try {
            if (codeSetMap == null) {
                codeSetMap = new HashMap();
                rowSet = dao.search("select  codesetid,codeitemid,codeitemdesc from codeitem  where codeitemid=parentid and invalid ='1' order by codesetid,codeitemid");
                ArrayList codeitemList = new ArrayList();
                String codesetid = "";
                LazyDynaBean abean = null;
                while (rowSet.next()) {
                    String a_codesetid = rowSet.getString("codesetid");
                    if ("".equals(codesetid))// 第一次进来
                    {
                        codesetid = rowSet.getString("codesetid");
                    }

                    if (!a_codesetid.equals(codesetid)) {
                        codeSetMap.put(codesetid.toLowerCase(), codeitemList);
                        codeitemList = new ArrayList();
                    }

                    if (!a_codesetid.equals(codesetid)) {
                        abean = new LazyDynaBean();
                        abean.set("value", "");
                        abean.set("name", "请选择");
                        codeitemList.add(abean);
                    }

                    abean = new LazyDynaBean();
                    abean.set("value", rowSet.getString("codeitemid"));
                    abean.set("name", rowSet.getString("codeitemdesc"));
                    codeitemList.add(abean);
                    codesetid = rowSet.getString("codesetid");
                }
                codeSetMap.put(codesetid.toLowerCase(), codeitemList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
    }

    // 取得前台职位查询条件列表
    public ArrayList getPosQueryConditionList(String hireChannel, String param)
            throws GeneralException {
        ArrayList conditionFieldList = new ArrayList();
        {
            RowSet rowSet = null;
            try {
                ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
                HashMap map = parameterXMLBo.getAttributeValues();
                String posQueryFieldIDs = "";
                if (map.get(param) != null && ((String) map.get(param)).trim().length() > 0) {
                    posQueryFieldIDs = (String) map.get(param);
                }

                String hireMajor = "";
                if (map.get("hireMajor") != null && !"".equals((String) map.get("hireMajor"))) {
                    hireMajor = (String) map.get("hireMajor");
                }

                if (posQueryFieldIDs.length() > 1) {
                    int i = 0;
                    HashMap multilayerCodeSetMap = new HashMap();
                    if ("pos_query".equalsIgnoreCase(param)) {
                        if ("01".equals(hireChannel) && hireMajor != null && !"".equals(hireMajor)) {
                            posQueryFieldIDs = posQueryFieldIDs.toUpperCase().replaceAll("Z0311", hireMajor.toUpperCase());
                        }

                    } else if ("pos_com_query".equalsIgnoreCase(param)) {
                        if ("01".equals(hireChannel)) {
                            posQueryFieldIDs = posQueryFieldIDs.toUpperCase().replaceAll("Z0311", "");
                        } else if ("02".equals(hireChannel)) {
                            posQueryFieldIDs = posQueryFieldIDs.toUpperCase().replaceAll(hireMajor.toUpperCase(), "");
                        }
                    }

                    rowSet = dao.search(getCondSql(posQueryFieldIDs));
                    HashMap conditionMap = new HashMap();
                    while (rowSet.next()) {
                        LazyDynaBean abean = new LazyDynaBean();
                        String codesetid = rowSet.getString("codesetid");
                        if (!"0".equals(codesetid) && i == 0) {// 在得到第一个代码类的时候就将所有的多层代码给取出来
                            multilayerCodeSetMap = getMultilayerCodeSetMap();
                            i++;
                        }

                        String isMore = getIsMore2(multilayerCodeSetMap, codesetid);

                        String itemid = rowSet.getString("itemid");
                        abean.set("itemid", itemid);
                        abean.set("value", "");
                        abean.set("itemlength", rowSet.getString("itemlength"));
                        abean.set("viewvalue", "");
                        abean.set("itemtype", rowSet.getString("itemtype"));
                        abean.set("codesetid", codesetid);
                        abean.set("itemdesc", rowSet.getString("itemdesc"));
                        abean.set("isMore", isMore); // 0：一层代码 1：多层代码
                        if ("0".equals(isMore) && !"0".equals(codesetid)) {
                            abean.set("options", getOptions2(codesetid, itemid ,hireChannel));
                        }

                        conditionMap.put(itemid.toLowerCase(), abean);

                    }

                    String[] temps = posQueryFieldIDs.split("`");
                    for (int j = 0; j < temps.length; j++) {
                        if (conditionMap.get(temps[j].toLowerCase()) != null) {
                            conditionFieldList.add((LazyDynaBean) conditionMap.get(temps[j].toLowerCase()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            } finally {
                PubFunc.closeDbObj(rowSet);
            }
        }
        return conditionFieldList;
    }

    /**
     * 取得各单位下所有的应聘职位信息列表
     *
     * @return
     */
    public ArrayList getUnitList(HashMap unitPosMap, ArrayList unitList, String unitLevel) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            int level = StringUtils.isEmpty(unitLevel) || "0".equalsIgnoreCase(unitLevel) ? 3 : Integer.parseInt(unitLevel);
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            HashMap amap = new HashMap();
            if (map.get("org_brief") != null && ((String) map.get("org_brief")).trim().length() > 0) {// 参数设置中设置的单位介绍指标和内容形式指标
                String temp = (String) map.get("org_brief");
                String[] temps = temp.split(",");
                DbWizard dbWizard = new DbWizard(this.conn);
                if (dbWizard.isExistField("b01", temps[0], false) && dbWizard.isExistField("b01", temps[1], false)) {
                    rowSet = dao.search("select b0110," + temps[0] + "," + temps[1]
                            + " from b01 where " + temps[0] + " IS NOT NULL AND " + temps[1]
                            + " IS NOT NULL ");

                    while (rowSet.next()) {
                        if ("0".equals(rowSet.getString(temps[1]))) {
                            amap.put(rowSet.getString("b0110").toLowerCase(), Sql_switcher.readMemo(rowSet, temps[0])
                                    + "#" + rowSet.getString(temps[1]));
                        } else if ("1".equals(rowSet.getString(temps[1]))) {
                            amap.put(rowSet.getString("b0110").toLowerCase(), "0" + "#" + rowSet.getString(temps[1]));
                        }
                    }
                }
            }

            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
                display_e0122 = "0";
            }

            for (Iterator t = unitList.iterator(); t.hasNext();) {
                LazyDynaBean aBean = new LazyDynaBean();
                String orgid = (String) t.next();
                aBean.set("id", orgid);
                CodeItem ci = AdminCode.getCode("UN", orgid, level);
                String desc = "";
                if (ci != null && !"0".equals(display_e0122)) {
                    desc = ci.getCodename();
                } else {
                    desc = AdminCode.getCodeName("UN", orgid);
                }

                aBean.set("name", desc);
                aBean.set("list", (ArrayList) unitPosMap.get(orgid));

                String temp = (String) amap.get(orgid.toLowerCase());
                if (temp != null) {
                    aBean.set("content", temp.split("#")[0]);
                    aBean.set("contentType", temp.split("#")[1]);
                } else {
                    aBean.set("content", "");
                    aBean.set("contentType", "");
                }

                list.add(aBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }

    /**
     * 获得单位介绍相关信息，
     *
     * @param map
     * @param unitcode
     * @return
     */
    public HashMap getIntroduceContent(HashMap map, String unitcode) {
        HashMap aMap = new HashMap();
        RowSet rowSet = null;
        try {
            if (map.get("org_brief") != null && ((String) map.get("org_brief")).trim().length() > 0) {
                String temp = (String) map.get("org_brief");
                String[] temps = temp.split(",");
                DbWizard dbWizard = new DbWizard(this.conn);
                if (dbWizard.isExistField("b01", temps[0], false)
                        && dbWizard.isExistField("b01", temps[1], false)) {// zzk 先判断指标是否存在（是否构库）
                    rowSet = dao.search("select b0110," + temps[0] + "," + temps[1]
                            + " from b01 where b0110='" + unitcode + "'");

                    while (rowSet.next()) {
                        String type = "";
                        String content = "";
                        String introducelink = "";
                        if (rowSet.getString(temps[1]) == null || "1".equals(rowSet.getString(temps[1]))) {// 内容

                            type = "1";
                            content = Sql_switcher.readMemo(rowSet, temps[0]);
                        } else if ("0".equals(rowSet.getString(temps[1]))) {// 连接
                            type = "0";
                            if (rowSet.getString(temps[0]) != null && rowSet.getString(temps[0]).length() > 0) {
                                content = "<a href='" + rowSet.getString(temps[0])
                                        + "' target='_blank'>" + rowSet.getString(temps[0])
                                        + "</a>";
                                introducelink = rowSet.getString(temps[0]);
                            }
                        }

                        aMap.put("type", type);
                        aMap.put("content", content);
                        aMap.put("link", introducelink);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return aMap;
    }

 // 取得查询职位的条件sql语句
    public String getAppendWhereSql(ArrayList conditionFieldList) {
        StringBuffer sql = new StringBuffer("");
        try {
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            String hireMajor = "";
            if (map != null && (map.get("hireMajor")) != null) {
                hireMajor = (String) map.get("hireMajor");
            }

            String hireMajorCode = "";
            if (map != null && (map.get("hireMajorCode")) != null) {
                hireMajorCode = (String) map.get("hireMajorCode");
            }


            if (conditionFieldList == null || conditionFieldList.size() == 0) {
                return "";
            }
            //启用应聘身份查询
            String candidateStatus = "";
            if (map != null && map.get("candidate_status") != null) {
                candidateStatus = (String) map.get("candidate_status");
            }

          //人民大学职称要求为多选 z03a2职称要求，z0390职称要求隐藏代码
            String title_Requirements = SystemConfig.getPropertyValue("title_Requirements");
            String z03a2 = "";
            String z0390 = "";
            if(StringUtils.isNotEmpty(title_Requirements)&&title_Requirements.split(":").length==2) {
            	z03a2 = title_Requirements.split(":")[0];
            	z0390 = title_Requirements.split(":")[1];
            }

            for (int i = 0; i < conditionFieldList.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) conditionFieldList.get(i);
                if (abean == null) {
                    continue;
                }

                String itemid = (String) abean.get("itemid");
                String value = PubFunc.getReplaceStr((String) abean.get("value"));
                String viewvalue = PubFunc.getReplaceStr((String) abean.get("viewvalue")==null?"":(String) abean.get("viewvalue"));
                String type = (String) abean.get("itemtype");
                String codesetid = (String) abean.get("codesetid");
                if (value == null || value.trim().length() == 0) {
                    continue;
                } else {
                    if ("D".equals(type)) {
                        value = value.replaceAll("\\.", "-");
                        String[] ss = value.split("-");
                        sql.append(" and " + Sql_switcher.year("Z03." + itemid) + "=" + ss[0]);
                        sql.append(" and " + Sql_switcher.month("Z03." + itemid) + "=" + ss[1]);
                        sql.append(" and " + Sql_switcher.day("Z03." + itemid) + "=" + ss[2]);
                    } else if ("A".equals(type)) {
                        sql.append(" and (");
                        StringBuffer sql2 = new StringBuffer("");
                        if (!"0".equals(codesetid)) {
                            String[] values = value.split(",");
                            for (int z = 0; z < values.length; z++) {
                                if (values[z] == null || values[z].trim().length() == 0) {
                                    continue;
                                }

                                sql2.append(" or Z03." + itemid + " like '" + values[z] + "%' ");

                            }

                            if ("@k".equalsIgnoreCase(codesetid)&&StringUtils.isNotBlank(viewvalue)) {
                                String[] viewvalues = viewvalue.split(",");
                                for (int z = 0; z < viewvalues.length; z++) {
                                    if (viewvalues[z] == null || viewvalues[z].trim().length() == 0) {
                                        continue;
                                    }

                                    sql2.append(" or Z03.z0311 in (select codeitemid from organization where upper(codesetid)='@K' and UPPER(codeitemdesc)='"
                                                    + viewvalues[z].toUpperCase() + "') ");

                                }
                            }
                        } else {
                            if (hireMajor != null && hireMajor.equalsIgnoreCase(itemid)
                                    && hireMajorCode != null && hireMajorCode.trim().length() > 0) {
                                String[] values = viewvalue.split(",");
                                for (int z = 0; z < values.length; z++) {
                                    if (values[z] == null || values[z].trim().length() == 0) {
                                        continue;
                                    }

                                    sql2.append(" or Z03." + itemid + " like '%" + values[z] + "%' ");

                                }

                            } else if(StringUtils.isNotEmpty(candidateStatus) && !"#".equalsIgnoreCase(candidateStatus) && ("z0385".equalsIgnoreCase(itemid)||z03a2.equalsIgnoreCase(itemid))) {
                            	String realItemid = "z0384";
                            	if(z03a2.equalsIgnoreCase(itemid)) {
                                    realItemid = z0390;
                                }
                            	String[] values = value.split("｜");
                            	for (int z = 0; z < values.length; z++) {
                                    if (values[z] == null || values[z].trim().length() == 0) {
                                        continue;
                                    }

                                    if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                                        sql2.append(" or ','+z03."+realItemid+"+',' like '%," + values[z] + "%,%' ");
                                    } else {
                                        sql2.append(" or ','||z03."+realItemid+"||',' like '%," + values[z] + "%,%' ");
                                    }
                                }
                            } else {
                                sql2.append(" or Z03." + itemid + " like '%" + value + "%' ");
                            }

                        }
                        if (sql2.length() > 0) {
                            sql.append(sql2.substring(3) + ")");
                        } else {
                            sql.append(" 1=1 )");
                        }

                    } else if ("N".equals(type)) {
                        if ("z0313".equalsIgnoreCase(itemid)) {
                            sql.append(" and Z03.z0315=" + value + "");
                        } else {
                            sql.append(" and Z03." + itemid + "=" + value + "");
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql.toString();
    }

    /**
     * 取得各单位应聘的职位信息
     *
     * @param conditionFieldList
     * @param unitList
     * @param employObject
     *            招聘对象 01：校园招聘 02：社会招聘 03：内部招聘 out:外部招聘
     * @return
     * @throws GeneralException
     */
    public HashMap getPositionInterviewMap(ArrayList conditionFieldList, ArrayList unitList,
            String employObject) throws GeneralException {
        HashMap unitPosMap = new HashMap();
        try {
        	unitPosMap = getPositionInfo(conditionFieldList, unitList, employObject,"map" , "");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return unitPosMap;
    }

    /**查看岗位信息
     * @param unitList
     * @param employObject
     * @param unitLeveltemp = unitLevel 因为获取的sql几乎一致所以改为相同的获取unitLevel方法
     * @return
     * @throws GeneralException
     */
    public HashMap getPositionInterviewMap2(ArrayList unitList,
            String employObject, String unitLeveltemp) throws GeneralException {
        HashMap unitPosMap = new HashMap();
        try {
        	unitPosMap = getPositionInfo(null, unitList, employObject,"map2" , "");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return unitPosMap;
    }

    /**
     * 点击更多的时候从这里取职位信息，但是取到的信息顺序和之前不一致所以导致显示重复信息，
     * 现在直接从静态变量中拿职位信息this.zpPosList;
     *
     * 取得各单位应聘的职位信息
     *
     * @param conditionFieldList
     * @param zpUnitCode
     *            初次进来默认是查询全部 zpUnitCode=""
     * @param unitList
     * @param employObject
     *            招聘对象 01：校园招聘 02：社会招聘 03：内部招聘 out:外部招聘 04:实习生招聘 headHire:猎头招聘
     * @return
     * @throws GeneralException
     */
    public HashMap getPositionInterviewMap3(String zpUnitCode, ArrayList conditionFieldList,
            ArrayList unitList, String employObject, String unitLevel) throws GeneralException {
        HashMap unitPosMap = new HashMap();
        try {
        	unitPosMap = getPositionInfo(conditionFieldList, unitList, employObject,"map3" , zpUnitCode);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return unitPosMap;
    }

    private HashMap getPositionInfo(ArrayList conditionFieldList,
            ArrayList unitList, String employObject, String from ,String zpUnitCode) throws GeneralException{
    	HashMap unitPosMap = new HashMap();
        RowSet rowSet = null;
        try {
        	ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
        	HashMap map = parameterXMLBo.getAttributeValues();
        	String unitLevel = "";
            if(map != null && map.get("unitLevel") != null) {
                unitLevel = (String) map.get("unitLevel");
            }
            if("map2".equals(from)&&StringUtils.isNotEmpty(unitLevel) && !"0".equalsIgnoreCase(unitLevel)) {
                this.lastLvUnits = getLastLvUnits(unitLevel);
            }

        	if (map.get("hirePostByLayer") != null) {
                hirePostByLayer = (String) map.get("hirePostByLayer");
            }

            boolean isRoot = false;
            if("map3".equals(from)){
	            rowSet = dao.search("select codeitemid,codeitemdesc from organization where parentid=codeitemid and codeitemid='"
	                            + zpUnitCode + "'");
	            while (rowSet.next()) {
	                isRoot = true;// 判断是否是顶级单位
	            }
            }
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
                display_e0122 = "0";
            }
            String hireMajor = "";
            if (map.get("hireMajor") != null) {
                hireMajor = (String) map.get("hireMajor");
            }
            FieldItem hitem = null;
            if (hireMajor != null && !"".equals(hireMajor)) {
                hitem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
            }

            int day = 0;
            String new_pos_date = "";
            if (map.get("new_pos_date") != null) {
                new_pos_date = (String) map.get("new_pos_date");
            }
            try {
                day = Integer.parseInt(new_pos_date);
            } catch (Exception e) {
                day = 0;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - day);
            ArrayList posFieldList = this.getPosListField();
            StringBuffer sql = getSql(conditionFieldList, employObject, isRoot, map, hirePostByLayer, hitem, posFieldList, from, zpUnitCode);
            //设置了外网职位列表显示指标
            if (posFieldList != null && posFieldList.size() > 0 && !"03".equals(employObject)) {

                getInfo(unitList, unitLevel, unitPosMap, display_e0122, hitem, posFieldList, day, calendar, sql);
            } else {
                boolean isadd = isAddHireMajor(hitem);
                getInfoMap(unitList, unitLevel, unitPosMap, display_e0122, hitem, day, calendar, isadd, sql);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return unitPosMap;
    }

    public ArrayList getMsgList(String msg, int arglength) {
        ArrayList list = new ArrayList();
        int len = 0;
        len = msg.getBytes().length;
        if (len <= arglength) {
            list.add(msg);
            return list;
        }

        while (true) {
            String temp = PubFunc.splitString(msg, arglength);
            list.add(temp);
            byte[] bytes = msg.getBytes();
            len = bytes.length;

            /** 实际的长度 */
            int rlen = 0;
            int j = 0;
            for (int i = 0; i < arglength; i++) {
                if (bytes[i] < 0) {
                    j++;
                }
            }
            if ((j % 2) == 1) {
                rlen = arglength - 1;
            } else {
                rlen = arglength;
            }

            byte[] sub = new byte[len - rlen];
            System.arraycopy(bytes, rlen, sub, 0, len - rlen);
            msg = new String(sub);
            if (len - arglength <= arglength) {
                list.add(msg);
                break;
            }
        }
        return list;
    }

    /**
	 * @param conditionFieldList
	 * @param employObject
     * @param isRoot
	 * @param map
     * @param hirePostByLayer
	 * @param hitem
	 * @param posFieldList
	 * @param from 调用的是哪个getmap方法
	 * @return
	 * @throws GeneralException
	 */
	private StringBuffer getSql(ArrayList conditionFieldList,
			String employObject, boolean isRoot, HashMap map, String hirePostByLayer, FieldItem hitem,
			ArrayList posFieldList, String from ,String zpUnitCode) throws GeneralException {
		String hire_object = (String) map.get("hire_object");
		if (map.get("hire_object") == null || "".equals((String) map.get("hire_object"))) {
            throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
        }
		//layer（单位或部门层级）改为grade（等级）
		StringBuffer sql = new StringBuffer("select z03.z0301,z03.Z0351,z03.z0321,z03.z0325,z03.z0307 as newdate,organization.codeitemdesc,organization.grade,z03.state ");
		boolean isadd = true;
		boolean hasOpentime = false;
		if(posFieldList!=null&&posFieldList.size()>0){
			for (int i = 0; i < posFieldList.size(); i++) {
			    LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
			    String itemid = (String) bean.get("itemid");
			    if (hitem != null && hitem.getItemid().equalsIgnoreCase(itemid)) {
                    isadd = false;
                }

			    if ("yprsl".equalsIgnoreCase(itemid) || "ypljl".equalsIgnoreCase(itemid)
			            || "state".equalsIgnoreCase(itemid) || "z0321".equalsIgnoreCase(itemid)
			            || "z0325".equalsIgnoreCase(itemid)) {
                    continue;
                }

			    if ("opentime".equalsIgnoreCase(itemid)) {
			        sql.append(",zp_members.create_time ");
			        hasOpentime = true;
			        continue;
			    }

			    sql.append(",Z03." + itemid);

			}

		}else{
			hasOpentime = true;
			isadd = isAddHireMajor(hitem);
			if("map".equals(from)) {
                sql.append(","+Sql_switcher.isnull("NULLIF(z03.state,'')", "2") + " state ");
            }
			sql.append(",z03.z0315,z03.Z0329,z03.Z0331,z03.Z0333, zp_members.create_time");
		}

		if (hitem != null && isadd) {
            sql.append(",Z03." + hitem.getItemid());
        }

		sql.append(" from organization , z03 ");
		if (hasOpentime) {
			sql.append(" left join (select * from zp_members where zp_members.member_type=4)zp_members ");
			sql.append(" on zp_members.z0301=z03.Z0301 ");
		}
		StringBuffer tableBuffer = new StringBuffer();
		StringBuffer whereBuffer = new StringBuffer();
		whereBuffer.append(" where z03.z0321=organization.codeitemid ");
		// -----增加外网显示指标排序的功能

		String orderSql = getPostFieldSort(map, tableBuffer, whereBuffer);
		if (tableBuffer.length() > 0) {
		    sql.append(tableBuffer.toString());
		}
		sql.append(whereBuffer.toString());

		sql.append(" and z0319='04'");
		sql.append(" and (z03.Z0101 IN(select Z0101 from Z01 WHERE Z0129 ='04')  OR  z03.Z0101 is NULL) ");
		String nowDate=DateStyle.getSystemTime();
		sql.append(" and " +Sql_switcher.dateToChar("Z0329", "yyyy-MM-dd hh24:mi:ss")+ "  <= '" + nowDate + "' ");
		sql.append(" and  '" + nowDate + "' <= " +Sql_switcher.dateToChar("Z0331", "yyyy-MM-dd hh24:mi:ss")+ "  ");
		//如果是map条件中加入 快速查询条件
		if(!"map2".equals(from)){

			if (!"".equals(getAppendWhereSql(conditionFieldList))) {
				sql.append(getAppendWhereSql(conditionFieldList));
			} else {
				if("map3".equals(from)){

					if ("1".equals(hirePostByLayer) && !isRoot) {
                        sql.append("and ( Z03.Z0321 = '" + zpUnitCode + "' )");
                    } else {
                        sql.append("and ( Z03.Z0321 like '" + zpUnitCode + "%' )");
                    }

				}
			}
		}

		if ("out".equals(employObject)|| "headHire".equals(employObject)) {
		    sql.append(" and ( z03." + hire_object + "<>'03' or z03." + hire_object + " is null )");

		    if ("headHire".equals(employObject)) {// 猎头招聘
		    	sql.append(" and z03.Z0373 ='1'");
			    String whl = getHeadHirePrivSql(this.loginUserName);
			    if (whl.length() > 0) {
                    sql.append(whl);
                }
		    }
		} else if ("no".equalsIgnoreCase(employObject)) {
		    sql.append(" and 1=2 ");
		} else {
		    sql.append(" and (z03." + hire_object + " like '" + employObject + "%' ");
		    if (map != null && map.get("candidate_status") != null) {
                String candidateStatus = (String) map.get("candidate_status");
                FieldItem fieldItem = DataDictionary.getFieldItem("z0384", "z03");
                if(StringUtils.isNotEmpty(candidateStatus) && !"#".equalsIgnoreCase(candidateStatus) && fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
                    if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                        sql.append(" or ','+z03.z0384+',' like '%," + employObject + "%,%' ");
                    } else {
                        sql.append(" or ','||z03.z0384||',' like '%," + employObject + "%,%' ");
                    }
                }
            }

		    sql.append(")");
		}

		if (orderSql.length() > 0) {
            sql.append(orderSql.toString());
        } else {
            sql.append(" order by organization.a0000,z03.Z0321,z03.state,z03.z0329 desc ");
        }
		return sql;
	}

	/**
     * 根据sql组装数据,组装后的数据存入unitPosMap中
     * @param unitList
     * @param unitLevel
     * @param unitPosMap
     * @param dao
     * @param display_e0122
     * @param hitem
     * @param day
     * @param calendar
     * @param isadd
     * @param sql
     * @return
     */
    private void getInfoMap(ArrayList unitList, String unitLevel, HashMap unitPosMap, String display_e0122,
			FieldItem hitem, int day, Calendar calendar, boolean isadd, StringBuffer sql) {
		RowSet rowSet = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			HashMap posPersonsMap = this.getPosPersons();
			String orgID = "";
			ArrayList a_posList = new ArrayList();
			int level = StringUtils.isEmpty(unitLevel) ? 0 : Integer.parseInt(unitLevel);
			int grade = 0;
			String z0351 = "";// 职位名称
			boolean hadorgId = false;
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				hadorgId = false;
			    LazyDynaBean lazyBean = new LazyDynaBean();
			    lazyBean.set("z0301", rowSet.getString("z0301"));
			    String z0321 = rowSet.getString("z0321");
			    String unitName = rowSet.getString("codeitemdesc");
			    lazyBean.set("count", (posPersonsMap.get(rowSet.getString("z0301")) == null
			            || "".equals((String) posPersonsMap.get(rowSet.getString("z0301"))))
			            ? "0" : (String) posPersonsMap.get(rowSet.getString("z0301")));
			    String aDate = "";
			    String aDate0 = "";
			    Date d = rowSet.getDate("z0331");
			    Date d0 = rowSet.getDate("z0329");
			    if (d != null) {
                    aDate = dateFormat.format(d);
                }

			    if (d0 != null) {
                    aDate0 = dateFormat.format(d0);
                }

			    lazyBean.set("z0331", aDate); // 有效结束日期
			    lazyBean.set("z0329", aDate0);
			    if (rowSet.getString("state") != null && "1".equals(rowSet.getString("state"))) {
                    lazyBean.set("state", "1");
                } else {
                    lazyBean.set("state", "0");
                }

			    if (rowSet.getString("z0333") != null) {
			        // 工作地点
			        FieldItem item = DataDictionary.getFieldItem("z0333");
			        String codesetid = item.getCodesetid();
			        if (!"0".equals(codesetid)) {
			        	lazyBean.set("z0333", AdminCode.getCodeName(codesetid, rowSet.getString("z0333")));
			        } else {
                        lazyBean.set("z0333", rowSet.getString("z0333"));
                    }
			    } else {
                    lazyBean.set("z0333", " ");
                }
			    String z0325 = rowSet.getString("z0325");
			    z0351 = StringUtils.isEmpty(rowSet.getString("Z0351")) ? "未知职位"
			            : rowSet.getString("Z0351");
			    if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                    lazyBean.set("posName", z0351);
                } else {
			        CodeItem item = AdminCode.getCode("UM", z0325, Integer.parseInt(display_e0122));
			        if (item != null && recruitservice==0) {
			        	lazyBean.set("posName", item.getCodename() + "/" + z0351);
			        } else {
			        	lazyBean.set("posName", z0351);
			        }
			    }

			    String isNewPos = "0";
			    if (rowSet.getDate("newdate") != null && day != 0) {
			        Date d1 = calendar.getTime();
			        Date d2 = rowSet.getDate("newdate");
			        if (d2.getTime() >= d1.getTime()) {
                        isNewPos = "1";
                    }
			    }

			    lazyBean.set("isNewPos", isNewPos);
			    // lazyDynaBean.set("posName",rowSet.getString("codeitemdesc"));
			    // //职位名称
			    lazyBean.set("unitName", unitName); // 所属单位名称
			    lazyBean.set("z0351", z0351);// 设置外网展示职位描述
			    lazyBean.set("z0321", z0321);
//			    lazyBean.set("z0337", rowSet.getString("z0337") == null ? "" : rowSet.getString("z0337"));
			    lazyBean.set("z0315", rowSet.getString("z0315") == null ? "" : rowSet.getString("z0315"));
			    if (rowSet.getDate("create_time") != null) {
			    	lazyBean.set("opentime", dateFormat.format(rowSet.getDate("create_time")));
			    } else {
			    	lazyBean.set("opentime", "");
			    }

			    if (isadd) {
			        String vv = rowSet.getString(hitem.getItemid()) == null ? "" : rowSet.getString(hitem.getItemid());
			        if (hitem.isCode()) {
                        lazyBean.set(hitem.getItemid().toLowerCase(), AdminCode.getCodeName(hitem.getCodesetid(), vv));
                    } else {
                        lazyBean.set(hitem.getItemid().toLowerCase(), vv);
                    }

			        lazyBean.set("major", vv);
			    }

			    if (!"".equals(orgID) && !orgID.equals(z0321) && (grade <= level || level == 0)) {
			        unitPosMap.put(orgID, a_posList);
			        insertOrgId(unitList, orgID, hadorgId);
			        a_posList = new ArrayList();
			        grade = rowSet.getInt("grade");
			    } else if(!"1".equals(hirePostByLayer)&&!"".equals(orgID)) {//添加下级单位发布的职位
			    	for(int i = 0; i < this.lastLvUnits.size(); i++){
			        	hadorgId = false;
			            LazyDynaBean bean = (LazyDynaBean)this.lastLvUnits.get(i);
			            String unitId = (String) bean.get("codeitemid");
			            if(!z0321.startsWith(unitId)) {
                            continue;
                        }

		                insertOrgId(unitList, unitId, hadorgId);

			            unitPosMap.put(unitId, a_posList);
			            break;
			        }
			    }

			    if (grade > level && level != 0 && this.lastLvUnits != null && this.lastLvUnits.size() > 0) {
			        for(int i = 0; i < this.lastLvUnits.size(); i++){
			        	LazyDynaBean bean = (LazyDynaBean)this.lastLvUnits.get(i);
			        	String unitId = (String) bean.get("codeitemid");
			            if(!z0321.startsWith(unitId)) {
                            continue;
                        }
			            insertOrgId(unitList, unitId, hadorgId);

			            ArrayList LastLvPosList = (ArrayList) unitPosMap.get(unitId);
			            if(LastLvPosList == null) {
			                LastLvPosList = new ArrayList<LazyDynaBean>();
			                insertOrgId(unitList, unitId, hadorgId);
			            }

			            LastLvPosList.add(lazyBean);
			            unitPosMap.put(unitId, LastLvPosList);
			            break;
			        }

			        a_posList = new ArrayList();
			    } else {
			    	grade = rowSet.getInt("grade");
			        orgID = z0321;
			        a_posList.add(lazyBean);
			    }

			}

			if (grade > level && level != 0 && a_posList != null && a_posList.size() > 0) {
			    addPosMap(unitList, unitPosMap, orgID, a_posList);

			} else {
			    if(a_posList != null && a_posList.size() > 0) {
			    	hadorgId = false;
			        unitPosMap.put(orgID, a_posList);
			        insertOrgId(unitList, orgID, hadorgId);
			    }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
	}

	private void getInfo(ArrayList unitList, String unitLevel, HashMap unitPosMap, String display_e0122,
			FieldItem hitem, ArrayList posFieldList, int day, Calendar calendar, StringBuffer sql){
		RowSet rowSet = null;
		try {
			//获取第X级的单位
			if(StringUtils.isNotEmpty(unitLevel) && !"0".equalsIgnoreCase(unitLevel)) {
                this.lastLvUnits = getLastLvUnits(unitLevel);
            }

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			HashMap posPersonsMap = this.getPosPersons();
			String orgID = "";
			ArrayList a_posList = new ArrayList();
			int level = StringUtils.isEmpty(unitLevel) ? 0 : Integer.parseInt(unitLevel);
			int grade = 0;

			//20161124 登录人员id,从while循环中拿出来，防止频繁访问
			String logina0100 = this.getA0100(this.loginUserName);
			boolean hadorgId = false;
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				hadorgId = false;
			    LazyDynaBean lazyBean = new LazyDynaBean();
			    for (int i = 0; i < posFieldList.size(); i++) {
			        LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
			        String itemid = (String) bean.get("itemid");
			        String itemtype = (String) bean.get("itemtype");
			        String codesetid = (String) bean.get("codesetid");
			        int deciwidth = Integer.parseInt(((String) bean.get("deciwidth")));
			        if ("state".equalsIgnoreCase(itemid) || "z0321".equalsIgnoreCase(itemid) || "z0325".equalsIgnoreCase(itemid)) {
                        continue;
                    }
			        //yprsl应聘人数 tjrsl 推荐人数
			        if ("yprsl".equalsIgnoreCase(itemid)|| "tjrsl".equalsIgnoreCase(itemid)) {
			            if ("headHire".equals(this.hireChannel)) {// 猎头招聘
			                lazyBean.set("tjrsl", (posPersonsMap.get(rowSet.getString("z0301")) == null
			                        || "".equals((String) posPersonsMap.get(rowSet.getString("z0301")))) ? "0"
			                         : (String) posPersonsMap.get(rowSet.getString("z0301")));
			            } else {// 应聘人员
			                lazyBean.set(itemid.toLowerCase(), (posPersonsMap.get(rowSet.getString("z0301")) == null
			                        || "".equals((String) posPersonsMap.get(rowSet.getString("z0301")))) ? "0"
			                        : (String) posPersonsMap.get(rowSet.getString("z0301")));
			            }
			        //ypljl应聘简历 tjjl推荐简历
			        } else if ("ypljl".equalsIgnoreCase(itemid)|| "tjjl".equalsIgnoreCase(itemid)) {
			            if ("headHire".equals(this.hireChannel)) {
			                lazyBean.set("tjjl", "推荐");
			            } else {
			                lazyBean.set(itemid.toLowerCase(), "应聘");
			            }

			        } else if ("opentime".equalsIgnoreCase(itemid)) {
			            if (rowSet.getDate("create_time") != null) {
			                lazyBean.set(itemid.toLowerCase(), dateFormat.format(rowSet.getDate("create_time")));
			            } else {
			                lazyBean.set(itemid.toLowerCase(), "");
			            }
			        } else {
			            if ("A".equalsIgnoreCase(itemtype)) {
			                if ("0".equalsIgnoreCase(codesetid)) {
			                    lazyBean.set(itemid.toLowerCase(), rowSet.getString(itemid) == null ? "" : rowSet.getString(itemid));
			                } else {
			                    String value = rowSet.getString(itemid) == null ? "" : rowSet.getString(itemid);
			                    value = AdminCode.getCodeName(codesetid, value);
			                    lazyBean.set(itemid.toLowerCase(), value);
			                }
			            } else if ("D".equalsIgnoreCase(itemtype)) {
			                if (rowSet.getDate(itemid) != null) {
			                    lazyBean.set(itemid.toLowerCase(), dateFormat.format(rowSet.getDate(itemid)));
			                } else {
			                    lazyBean.set(itemid.toLowerCase(), "");
			                }
			            } else if ("N".equalsIgnoreCase(itemtype)) {
			                if (rowSet.getString(itemid) != null) {
			                    lazyBean.set(itemid.toLowerCase(), PubFunc.round(rowSet.getString(itemid), deciwidth));
			                } else {
			                    lazyBean.set(itemid.toLowerCase(), "");
			                }
			            } else {
			                if (rowSet.getString(itemid) != null) {
                                lazyBean.set(itemid.toLowerCase(), rowSet.getString(itemid));
                            } else {
                                lazyBean.set(itemid.toLowerCase(), "");
                            }

			            }
			        }
			    }
			    String isNewPos = "0";
			    if (rowSet.getDate("newdate") != null && day != 0) {
			        Date d1 = calendar.getTime();
			        Date d2 = rowSet.getDate("newdate");
			        if (d2.getTime() >= d1.getTime()) {
                        isNewPos = "1";
                    }
			    }
			    lazyBean.set("isNewPos", isNewPos);
			    lazyBean.set("codeitemdesc", rowSet.getString("codeitemdesc") == null ? "" : rowSet.getString("codeitemdesc"));
			    //热点职位
			    if (rowSet.getString("state") != null && "1".equals(rowSet.getString("state"))) {
                    lazyBean.set("state", "1");
                } else {
                    lazyBean.set("state", "0");
                }
			    lazyBean.set("z0321Name", AdminCode.getCodeName("UN", rowSet.getString("z0321")));
			    lazyBean.set("z0321", rowSet.getString("z0321") == null ? "" : rowSet.getString("z0321"));
			    lazyBean.set("z0351", rowSet.getString("z0351"));// 显示职位名称，这里已经添加过下面不再添加
			    lazyBean.set("z0301", rowSet.getString("z0301"));
			    String z0325 = rowSet.getString("z0325") == null ? "" : rowSet.getString("z0325");
			    if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
			        // lazyBean.set("z0311Name",rowSet.getString("codeitemdesc"));如果显示层级为0级那么只显示职位的名称
			    } else {
			        CodeItem item = AdminCode.getCode("UM", z0325, Integer.parseInt(display_e0122));
			        if (item != null && recruitservice==0) {
                        lazyBean.set("z0351", item.getCodename() + "/" + rowSet.getString("z0351"));// 显示职位名称
                    }

			    }
			    String z0321 = rowSet.getString("z0321");
			    lazyBean.set("z0325Id", z0325);
			    lazyBean.set("z0325", AdminCode.getCodeName("UM", z0325));// 不知道,部门送到前台有什么用,暂时先不去掉
			    if (hitem != null)// 招聘专业这个是否可以去掉了?
			    {
			        String vv = rowSet.getString(hitem.getItemid()) == null ? "" : rowSet.getString(hitem.getItemid());
			        if (hitem.isCode()) {
			            lazyBean.set(hitem.getItemid().toLowerCase(), AdminCode.getCodeName(hitem.getCodesetid(), vv));
			        } else {
			            lazyBean.set(hitem.getItemid().toLowerCase(), vv);
			        }
			        lazyBean.set("major", vv);
			    }

			    lazyBean.set("isApplyedPos", String.valueOf(this.isApplyedPosition(rowSet.getString("z0301"),logina0100)));

			    // 将同一个单位的招聘岗位放在同一个map中去,map中key：orgID单位的代码 value：一个list
			    // <list中存放的是bean对象 bean对象中存放的是个个岗位中的相关招聘信息>
		    	insertOrgId(unitList, z0321, hadorgId);
			    ArrayList z0321PosList = (ArrayList) unitPosMap.get(z0321);
	            if(z0321PosList == null) {
                    z0321PosList = new ArrayList<LazyDynaBean>();
                }

	            z0321PosList.add(lazyBean);
	            unitPosMap.put(z0321, z0321PosList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public HashMap getPosPersons() {
        HashMap map = new HashMap();
        RowSet rs = null;
        try {
            String sql = "select zp_pos_id,count(a0100) as sum from zp_pos_tache group by zp_pos_id";
            if ("headHire".equals(hireChannel)) {// 猎头招聘,要查询应聘人员是由当前猎头用户推荐的
                sql = "select zp_pos_id,count(a0100) as sum from zp_pos_tache where recusername='"
                        + this.loginUserName + "' group by zp_pos_id";
            }

            rs = dao.search(sql);
            while (rs.next()) {
                map.put(rs.getString("zp_pos_id"), rs.getString("sum"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;

    }

	/**
	 * 将职位信息插入map中
	 * @param unitList
	 * @param unitPosMap
	 * @param orgID
	 * @param a_posList
	 */
	private void addPosMap(ArrayList unitList, HashMap unitPosMap, String orgID, ArrayList a_posList) {
		boolean hadorgId;
		for(int i = 0; i < this.lastLvUnits.size(); i++){
			hadorgId = false;
		    LazyDynaBean bean = (LazyDynaBean)this.lastLvUnits.get(i);
		    String unitId = (String) bean.get("codeitemid");
		    if(!orgID.startsWith(unitId)) {
                continue;
            }

		    ArrayList LastLvPosList = (ArrayList) unitPosMap.get(unitId);
		    if(LastLvPosList == null) {
		        LastLvPosList = new ArrayList<LazyDynaBean>();
		        insertOrgId(unitList, unitId, hadorgId);
		    }

		    LastLvPosList.addAll(a_posList);
		    unitPosMap.put(unitId, LastLvPosList);
		    break;
		}
	}

	/**
	 * 判断是否已经添加过orgID
	 * @param unitList
	 * @param orgID
	 * @param hadorgId
	 */
	private void insertOrgId(ArrayList unitList, String orgID, boolean hadorgId) {
		for(int i=0;i<unitList.size();i++){
			if(orgID.equals((String)unitList.get(i))) {
                hadorgId = true;
            }
		}
		if(!hadorgId) {
            unitList.add(orgID);
        }
	}

    /**
     * 根据登录名获取a0100
     *
     * @param userName
     * @return
     * @throws GeneralException
     */
    public String getA0100(String userName) throws GeneralException {
        String res = "";
        String sql = "select a0100 from "+a_dbName+"a01 where username=?";
        ArrayList values = new ArrayList();
        values.add(userName);

        RowSet rs = null;
        try {
            rs = dao.search(sql, values);
            if (rs.next()) {
                res = rs.getString("a0100");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

	/**判断是否加入猎头指标
	 * @param hitem
	 * @return
	 */
	private boolean isAddHireMajor(FieldItem hitem) {
		boolean isadd = false;
		if (hitem != null && !"z0337".equalsIgnoreCase(hitem.getItemid())
		        && !"z0315".equalsIgnoreCase(hitem.getItemid())
		        && !"z0301".equalsIgnoreCase(hitem.getItemid())
		        && !"z0325".equalsIgnoreCase(hitem.getItemid())
		        && !"z0329".equalsIgnoreCase(hitem.getItemid())
		        && !"z0333".equalsIgnoreCase(hitem.getItemid())
		        && !"z0311".equalsIgnoreCase(hitem.getItemid())
		        && !"z0321".equalsIgnoreCase(hitem.getItemid())
		        && !"z0351".equalsIgnoreCase(hitem.getItemid())) {
            isadd = true;
        }
		return isadd;
	}

    /**
     * 获得猎头权限范围内可访问应聘职位的SQL条件
     *
     * @param username
     * @author dengc
     * @return
     */
    private String getHeadHirePrivSql(String username) throws GeneralException {
        String where_str = "";
        if (username == null || username.length() == 0) {
            return where_str;
        }

        RowSet rowSet = null;
        try {

            String sql = "select Z6005 from z60 where z6000 =( select z6000 from zp_headhunter_login where lower(username)=? )";
            rowSet = dao.search(sql, Arrays.asList(new Object[] { username.toLowerCase() }));
            if (rowSet.next()) {
                String z6005 = rowSet.getString("z6005") != null ? rowSet.getString("z6005") : "";
                if (z6005.length() > 0) {
                    CodeItem codeitem = AdminCode.getCode("UN", z6005);
                    if (codeitem != null) {
                        where_str = " and z03.z0321 like '" + z6005 + "%'";
                    } else {
                        codeitem = AdminCode.getCode("UM", z6005);
                        if (codeitem != null) {
                            where_str = " and z03.z0325 like '" + z6005 + "%'";
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return where_str;
    }

    /**
     * 取得各单位应聘的职位信息
     *
     * @param conditionFieldList
     * @param employObject
     *            招聘对象 01：校园招聘 02：社会招聘 03：内部招聘 out:外部招聘
     * @return
     * @throws GeneralException
     */
    public ArrayList getPositionByUnitCode(ArrayList conditionFieldList, String employObject,
            String unitCode, String isAll) throws GeneralException {
        RowSet rowSet = null;
        ArrayList a_posList = new ArrayList();
        try {

            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
                display_e0122 = "0";
            }
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            int day = 0;
            String new_pos_date = "";
            if (map.get("new_pos_date") != null) {
                new_pos_date = (String) map.get("new_pos_date");
            }
            /** isAll=1显示全部职位，=0显示最新职位 */
            if (new_pos_date == null || new_pos_date.trim().length() == 0) {
                isAll = "1";
            }

            try {
                day = Integer.parseInt(new_pos_date);
            } catch (Exception e) {
                day = 0;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - day);
            String hireMajor = "";
            if (map.get("hireMajor") != null) {
                hireMajor = (String) map.get("hireMajor");
            }
            FieldItem hitem = null;
            if (hireMajor != null && !"".equals(hireMajor)) {
                hitem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
            }

            ArrayList posFieldList = this.getPosListField();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            boolean flag = false;
            if (posFieldList != null && posFieldList.size() > 0 && !"03".equals(employObject)) {
                boolean isadd = true;
                StringBuffer sql = new StringBuffer(
                        "select z03.z0301,z03.z0311,z03.state,z03.z0321,z03.z0307 as newdate,organization.codeitemdesc,z0325,T2.codeitemdesc unitName");
                for (int i = 0; i < posFieldList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
                    String itemid = (String) bean.get("itemid");
                    if (hitem != null && hitem.getItemid().equalsIgnoreCase(itemid)) {
                        isadd = false;
                    }
                    if ("yprsl".equalsIgnoreCase(itemid) || "ypljl".equalsIgnoreCase(itemid)
                            || "z0311".equalsIgnoreCase(itemid) || "state".equalsIgnoreCase(itemid)
                            || "z0321".equalsIgnoreCase(itemid) || "z0325".equalsIgnoreCase(itemid)) {
                        continue;
                    }
                    if ("opentime".equalsIgnoreCase(itemid)) {
                        sql.append(",zp_members.create_time publishtime");
                        flag = true;
                        continue;
                    }
                    sql.append(",Z03." + itemid);

                }
                if (hitem != null && isadd) {
                    sql.append(",Z03." + hitem.getItemid());
                }
                sql.append(" from z03 ,organization ,organization T2 ");
                if (flag)// 前台岗位显示列存在发布时间
                {
                    sql.append(",(select * from zp_members where zp_members.member_type=4) zp_members");
                }
                StringBuffer tableBuffer = new StringBuffer();
                StringBuffer whereBuffer = new StringBuffer();
                whereBuffer.append(" where (z03.z0311=organization.codeitemid or  Z03.z0321=T2.codeitemid) and z03.Z0325=organization.codeitemid");
                if (flag)// 前台岗位显示列存在发布时间
                {
                    whereBuffer.append(" and zp_members.z0301 = z03.Z0301 ");
                }
                // -----增加外网显示指标排序的功能

                String orderSql = getPostFieldSort(map, tableBuffer, whereBuffer);
                if (tableBuffer.length() > 0) {
                    sql.append(tableBuffer.toString());
                }
                sql.append(whereBuffer.toString());

                sql.append(" and z0319='04' ");
                String hire_object = (String) map.get("hire_object");
                if (map.get("hire_object") == null || "".equals(hire_object)) {
                    throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
                }
                sql.append(getDateSql(">=", "Z0329"));
                sql.append(getDateSql("<=", "Z0331"));
                sql.append(getAppendWhereSql(conditionFieldList));
                if ("out".equals(employObject)) {
                    sql.append(" and ( z03.z0336<>'03' or z03.z0336 is null )");
                } else if ("no".equalsIgnoreCase(employObject)) {
                    sql.append(" and 1=2 ");
                } else {
                    sql.append(" and z03.z0336='" + employObject + "' ");
                }
                sql.append(" and z0321 like '" + unitCode + "%'");
                if ("0".equals(isAll) && day != 0)// 显示最新职位
                {
                    String xx = format.format(calendar.getTime());
                    String itemid = "z0307";
                    sql.append(" and ((" + Sql_switcher.year(itemid) + ">" + xx.substring(0, 4)
                            + ") or (" + Sql_switcher.year(itemid) + "=" + xx.substring(0, 4));
                    sql.append(" and " + Sql_switcher.month(itemid) + ">" + xx.substring(5, 7)
                            + ") or (");
                    sql.append(Sql_switcher.year(itemid) + "=" + xx.substring(0, 4) + " and "
                            + Sql_switcher.month(itemid) + "=" + xx.substring(5, 7) + " and ");
                    sql.append(Sql_switcher.day(itemid) + ">=" + xx.substring(8, 10) + "))");
                }
                if (orderSql.length() > 0) {
                    sql.append(orderSql.toString());
                } else {
                    sql.append(" order by T2.a0000,z03.Z0321,z03.state,z03.z0329 desc,z03.z0311 ");
                }
                // sql.append(" order by T2.a0000,z03.Z0321,z03.state,z03.z0329 desc,z03.z0311 ");
                rowSet = dao.search(sql.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                HashMap posPersonsMap = this.getPosPersons();
                while (rowSet.next()) {
                    LazyDynaBean lazyBean = new LazyDynaBean();
                    for (int i = 0; i < posFieldList.size(); i++) {
                        LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
                        String itemid = (String) bean.get("itemid");
                        String itemtype = (String) bean.get("itemtype");
                        String codesetid = (String) bean.get("codesetid");
                        int deciwidth = Integer.parseInt(((String) bean.get("deciwidth")));
                        if ("z0311".equalsIgnoreCase(itemid) || "state".equalsIgnoreCase(itemid)
                                || "z0321".equalsIgnoreCase(itemid)
                                || "z0325".equalsIgnoreCase(itemid)) {
                            continue;
                        }
                        if ("yprsl".equalsIgnoreCase(itemid)) {
                            lazyBean.set(itemid.toLowerCase(), (posPersonsMap.get(rowSet
                                    .getString("z0301")) == null || "".equals((String) posPersonsMap
                                    .get(rowSet.getString("z0301")))) ? "0"
                                    : (String) posPersonsMap.get(rowSet.getString("z0301")));
                        } else if ("opentime".equalsIgnoreCase(itemid)) {
                            String publish = DateUtils.format(rowSet.getDate("publishtime"),
                                    "yyyy-MM-dd").replace("-", ".");
                            lazyBean.set(itemid.toLowerCase(), StringUtils.isEmpty(publish) ? ""
                                    : publish);
                        } else if ("ypljl".equalsIgnoreCase(itemid)) {
                            lazyBean.set(itemid.toLowerCase(), "应聘");
                        } else {
                            if ("A".equalsIgnoreCase(itemtype)) {
                                if ("0".equalsIgnoreCase(codesetid)) {
                                    lazyBean.set(itemid.toLowerCase(),
                                            rowSet.getString(itemid) == null ? "" : rowSet
                                                    .getString(itemid));
                                } else {
                                    String value = rowSet.getString(itemid) == null ? "" : rowSet
                                            .getString(itemid);
                                    value = AdminCode.getCodeName(codesetid, value);
                                    lazyBean.set(itemid.toLowerCase(), value);
                                }
                            } else if ("D".equalsIgnoreCase(itemtype)) {
                                if (rowSet.getDate(itemid) != null) {
                                    lazyBean.set(itemid.toLowerCase(), dateFormat.format(rowSet
                                            .getDate(itemid)));
                                } else {
                                    lazyBean.set(itemid.toLowerCase(), "");
                                }
                            } else if ("N".equalsIgnoreCase(itemtype)) {
                                if (rowSet.getString(itemid) != null) {
                                    lazyBean.set(itemid.toLowerCase(), PubFunc.round(rowSet
                                            .getString(itemid), deciwidth));
                                } else {
                                    lazyBean.set(itemid.toLowerCase(), "");
                                }
                            } else {
                                if (rowSet.getString(itemid) != null) {
                                    lazyBean.set(itemid.toLowerCase(), rowSet.getString(itemid));
                                } else {
                                    lazyBean.set(itemid.toLowerCase(), "");
                                }
                            }
                        }
                    }
                    String isNewPos = "0";
                    if (rowSet.getDate("newdate") != null && day != 0) {
                        Date d1 = calendar.getTime();
                        Date d2 = rowSet.getDate("newdate");
                        if (d2.getTime() >= d1.getTime()) {
                            isNewPos = "1";
                        }
                    }
                    lazyBean.set("isNewPos", isNewPos);
                    lazyBean.set("unitName", rowSet.getString("unitName") == null ? "" : rowSet
                            .getString("unitName"));
                    if (rowSet.getString("state") != null && "1".equals(rowSet.getString("state"))) {
                        lazyBean.set("state", "1");
                    } else {
                        lazyBean.set("state", "0");
                    }
                    lazyBean.set("z0321Name", AdminCode
                            .getCodeName("UN", rowSet.getString("z0321")));
                    lazyBean.set("z0321", rowSet.getString("z0321") == null ? "" : rowSet
                            .getString("z0321"));
                    lazyBean.set("z0311", StringUtils.isEmpty(rowSet.getString("z0311")) ? ""
                            : rowSet.getString("z0311"));
                    lazyBean.set("z0301", rowSet.getString("z0301"));
                    String z0325 = rowSet.getString("z0325");
                    if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                        lazyBean.set("z0311Name", rowSet.getString("codeitemdesc"));
                    } else {
                        CodeItem item = AdminCode.getCode("UM", z0325, Integer
                                .parseInt(display_e0122));
                        if (item != null && recruitservice==0) {
                            lazyBean.set("z0311Name", item.getCodename() + "/"
                                    + rowSet.getString("codeitemdesc"));
                        } else {
                            lazyBean.set("z0311Name", rowSet.getString("codeitemdesc"));
                        }
                    }
                    String z0321 = rowSet.getString("z0321");
                    lazyBean.set("z0325", AdminCode.getCodeName("UM", z0325));
                    if (hitem != null) {
                        String vv = rowSet.getString(hitem.getItemid()) == null ? "" : rowSet
                                .getString(hitem.getItemid());
                        String prefix = "";
                        if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                            prefix = "";
                        } else {
                            CodeItem item = AdminCode.getCode("UM", z0325, Integer
                                    .parseInt(display_e0122));
                            if (item != null && recruitservice==0) {
                                prefix = item.getCodename() + "/";
                            }
                        }
                        if (hitem.isCode()) {
                            lazyBean.set(hitem.getItemid().toLowerCase(), prefix
                                    + AdminCode.getCodeName(hitem.getCodesetid(), vv));
                        } else {
                            lazyBean.set(hitem.getItemid().toLowerCase(), prefix + vv);
                        }
                        lazyBean.set("major", vv);
                    }
                    a_posList.add(lazyBean);
                }
            } else {
                boolean isadd = isAddHireMajor(hitem);

                String hire_object = (String) map.get("hire_object");
                if (map.get("hire_object") == null || "".equals(hire_object)) {
                    throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
                }
                StringBuffer sql = new StringBuffer(
                        "select z03.z0315,z03.z0301,z03.z0325,z03.Z0329,z0307 as newdate,z03.Z0331,z03.Z0333,"
                                + Sql_switcher.isnull("NULLIF(z03.state,'')", "2")
                                + " state,z03.z0311,organization.codeitemdesc,z03.Z0321,T2.codeitemdesc unitName ");
                if (isadd) {
                    sql.append(",z03." + hitem.getItemid());
                }
                sql.append(" from z03 ,organization,organization T2 ");
                StringBuffer tableBuffer = new StringBuffer();
                StringBuffer whereBuffer = new StringBuffer();
                whereBuffer
                        .append(" where z03.z0311=organization.codeitemid and  Z03.z0321=T2.codeitemid ");
                // -----增加外网显示指标排序的功能

                String orderSql = getPostFieldSort(map, tableBuffer, whereBuffer);
                if (tableBuffer.length() > 0) {
                    sql.append(tableBuffer.toString());
                }
                sql.append(whereBuffer.toString());

                sql.append(" and z0319='04' ");
                sql.append(getDateSql(">=", "Z0329"));
                sql.append(getDateSql("<=", "Z0331"));
                sql.append(getAppendWhereSql(conditionFieldList));
                if ("out".equals(employObject)) {
                    sql.append(" and ( z03.z0336<>'03' or z03.z0336 is null )");
                } else if ("no".equalsIgnoreCase(employObject)) {
                    sql.append(" and 1=2 ");
                } else {
                    sql.append(" and z03.z0336='" + employObject + "' ");
                }
                sql.append(" and z0321 like '" + unitCode + "%'");
                if ("0".equals(isAll) && day != 0)// 显示最新职位
                {
                    String xx = format.format(calendar.getTime());
                    String itemid = "z0307";
                    sql.append(" and ((" + Sql_switcher.year(itemid) + ">" + xx.substring(0, 4)
                            + ") or (" + Sql_switcher.year(itemid) + "=" + xx.substring(0, 4));
                    sql.append(" and " + Sql_switcher.month(itemid) + ">" + xx.substring(5, 7)
                            + ") or (");
                    sql.append(Sql_switcher.year(itemid) + "=" + xx.substring(0, 4) + " and "
                            + Sql_switcher.month(itemid) + "=" + xx.substring(5, 7) + " and ");
                    sql.append(Sql_switcher.day(itemid) + ">=" + xx.substring(8, 10) + "))");
                }

                if (orderSql.length() > 0) {
                    sql.append(orderSql.toString());
                } else {
                    sql.append(" order by T2.a0000,z03.Z0321,z03.state,z03.z0329 desc,z03.z0311 ");
                }
                // sql.append(" order by o2.a0000,Z0321,z03.state ,z03.z0329 desc,z0311 ");
                rowSet = dao.search(sql.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                HashMap posPersonsMap = this.getPosPersons();
                while (rowSet.next()) {
                    LazyDynaBean lazyDynaBean = new LazyDynaBean();
                    lazyDynaBean.set("z0301", rowSet.getString("z0301"));
                    lazyDynaBean.set("z0311", rowSet.getString("z0311"));
                    String z0321 = rowSet.getString("z0321");
                    String unitName = rowSet.getString("unitName");
                    lazyDynaBean.set("count",
                                    (posPersonsMap.get(rowSet.getString("z0301")) == null
                                    || "".equals((String) posPersonsMap.get(rowSet.getString("z0301")))) ? "0"
                                            : (String) posPersonsMap.get(rowSet.getString("z0301")));
                    String aDate = "";
                    String aDate0 = "";
                    Date d = rowSet.getDate("z0331");
                    Date d0 = rowSet.getDate("z0329");
                    if (d != null) {
                        aDate = dateFormat.format(d);
                    }
                    if (d0 != null) {
                        aDate0 = dateFormat.format(d0);
                    }
                    lazyDynaBean.set("z0331", aDate); // 有效结束日期
                    lazyDynaBean.set("z0329", aDate0);
                    if (rowSet.getString("state") != null && "1".equals(rowSet.getString("state"))) {
                        lazyDynaBean.set("state", "1");
                    } else {
                        lazyDynaBean.set("state", "0");
                    }

                    if (rowSet.getString("z0333") != null) {
                        // 工作地点
                        FieldItem item = DataDictionary.getFieldItem("z0333");
                        String codesetid = item.getCodesetid();
                        if (!"0".equals(codesetid)) {
                            lazyDynaBean.set("z0333", AdminCode.getCodeName(codesetid, rowSet
                                    .getString("z0333")));
                        } else {
                            lazyDynaBean.set("z0333", rowSet.getString("z0333"));
                        }
                    } else {
                        lazyDynaBean.set("z0333", " ");
                    }
                    String z0325 = rowSet.getString("z0325");
                    if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                        lazyDynaBean.set("posName", rowSet.getString("codeitemdesc"));
                    } else {
                        CodeItem item = AdminCode.getCode("UM", z0325, Integer
                                .parseInt(display_e0122));
                        if (item != null && recruitservice==0) {
                            lazyDynaBean.set("posName", item.getCodename() + "/"
                                    + rowSet.getString("codeitemdesc"));
                        } else {
                            lazyDynaBean.set("posName", rowSet.getString("codeitemdesc"));
                        }
                    }

                    String isNewPos = "0";
                    if (rowSet.getDate("newdate") != null && day != 0) {
                        Date d1 = calendar.getTime();
                        Date d2 = rowSet.getDate("newdate");
                        if (d2.getTime() >= d1.getTime()) {
                            isNewPos = "1";
                        }
                    }
                    lazyDynaBean.set("isNewPos", isNewPos);
                    lazyDynaBean.set("unitName", unitName); // 所属单位名称
                    lazyDynaBean.set("z0321", z0321);
                    /*lazyDynaBean.set("z0337", rowSet.getString("z0337") == null ? "" : rowSet
                            .getString("z0337"));*/
                    lazyDynaBean.set("z0315", rowSet.getString("z0315") == null ? "" : rowSet
                            .getString("z0315"));
                    if (isadd) {
                        String vv = rowSet.getString(hitem.getItemid()) == null ? "" : rowSet
                                .getString(hitem.getItemid());
                        String prefix = "";
                        if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                            prefix = "";
                        } else {
                            CodeItem item = AdminCode.getCode("UM", z0325, Integer
                                    .parseInt(display_e0122));
                            if (item != null && recruitservice==0) {
                                prefix = item.getCodename() + "/";
                            }
                        }
                        if (hitem.isCode()) {
                            lazyDynaBean.set(hitem.getItemid().toLowerCase(), prefix
                                    + AdminCode.getCodeName(hitem.getCodesetid(), vv));
                        } else {
                            lazyDynaBean.set(hitem.getItemid().toLowerCase(), prefix + vv);
                        }
                        lazyDynaBean.set("major", vv);
                    }
                    a_posList.add(lazyDynaBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return a_posList;
    }

    public ArrayList getPositionByUnitCode(ArrayList conditionFieldList, String employObject,
            String unitCode, String isAll, String z0301) throws GeneralException {
        RowSet rowSet = null;
        ArrayList a_posList = new ArrayList();
        try {

            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
                display_e0122 = "0";
            }
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            int day = 0;
            String new_pos_date = "";
            if (map.get("new_pos_date") != null) {
                new_pos_date = (String) map.get("new_pos_date");
            }
            /** isAll=1显示全部职位，=0显示最新职位 */
            if (new_pos_date == null || new_pos_date.trim().length() == 0) {
                isAll = "1";
            }

            try {
                day = Integer.parseInt(new_pos_date);
            } catch (Exception e) {
                day = 0;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - day);
            String hireMajor = "";
            if (map.get("hireMajor") != null) {
                hireMajor = (String) map.get("hireMajor");
            }
            FieldItem hitem = null;
            if (hireMajor != null && !"".equals(hireMajor)) {
                hitem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
            }

            ArrayList posFieldList = this.getPosListField();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (posFieldList != null && posFieldList.size() > 0 && !"03".equals(employObject)) {
                boolean isadd = true;
                boolean hasOpentime = false;
                StringBuffer sql = new StringBuffer(
                        "select z03.z0301,z03.z0321,z03.z0307 as newdate,organization.codeitemdesc,z0325,Z0351 ");
                for (int i = 0; i < posFieldList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
                    String itemid = (String) bean.get("itemid");
                    if (hitem != null && hitem.getItemid().equalsIgnoreCase(itemid)) {
                        isadd = false;
                    }
                    if ("yprsl".equalsIgnoreCase(itemid) || "ypljl".equalsIgnoreCase(itemid)
                            || "state".equalsIgnoreCase(itemid) || "z0321".equalsIgnoreCase(itemid)
                            || "z0325".equalsIgnoreCase(itemid)) {
                        continue;
                    }
                    if ("opentime".equalsIgnoreCase(itemid)) {
                        sql.append(",zp_members.create_time ");
                        hasOpentime = true;
                        continue;
                    }
                    sql.append(",Z03." + itemid);

                }
                if (hitem != null && isadd) {
                    sql.append(",Z03." + hitem.getItemid());
                }
                sql.append(" from organization ,z03 ");
                if (hasOpentime) {
                    sql
                            .append(" left join (select * from zp_members where zp_members.member_type=4)zp_members ");
                    sql.append(" on zp_members.z0301=z03.Z0301 ");
                }
                StringBuffer tableBuffer = new StringBuffer();
                StringBuffer whereBuffer = new StringBuffer();
                whereBuffer.append(" where z03.z0321=organization.codeitemid ");
                // -----增加外网显示指标排序的功能

                String orderSql = getPostFieldSort(map, tableBuffer, whereBuffer);
                if (tableBuffer.length() > 0) {
                    sql.append(tableBuffer.toString());
                }
                sql.append(whereBuffer.toString());

                sql.append(" and z0319='04' ");
                String hire_object = (String) map.get("hire_object");
                if (map.get("hire_object") == null || "".equals((String) map.get("hire_object"))) {
                    throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
                }
                sql.append(getDateSql(">=", "Z0329"));
                sql.append(getDateSql("<=", "Z0331"));
                sql.append(getAppendWhereSql(conditionFieldList));
                if ("out".equals(employObject)) {
                    sql.append(" and ( z03." + hire_object + "<>'03' or z03." + hire_object
                            + " is null )");
                } else if ("headHire".equals(employObject)) {// 猎头招聘
                    sql.append(" and ( z03." + hire_object + "<>'03' or z03." + hire_object
                            + " is null )");
                    sql.append(" and z03.z0373='1' ");

                    String whl = getHeadHirePrivSql(this.loginUserName);
                    if (whl.length() > 0) {
                        sql.append(whl);
                    }

                } else if ("no".equalsIgnoreCase(employObject)) {
                    sql.append(" and 1=2 ");
                } else {
                    sql.append(" and z03." + hire_object + "='" + employObject + "' ");
                }
                sql.append(" and z03.z0301 in ('");
                String tt[] = z0301.substring(1).split("`");
                for (int i = 0; i < tt.length; i++) {

                    if (i == tt.length - 1) {
                        sql.append(tt[i] + "')");
                    } else {
                        sql.append(tt[i] + "','");
                    }
                }
                if ("0".equals(isAll) && day != 0)// 显示最新职位
                {
                    String xx = format.format(calendar.getTime());
                    String itemid = "z0307";
                    sql.append(" and ((" + Sql_switcher.year(itemid) + ">" + xx.substring(0, 4)
                            + ") or (" + Sql_switcher.year(itemid) + "=" + xx.substring(0, 4));
                    sql.append(" and " + Sql_switcher.month(itemid) + ">" + xx.substring(5, 7)
                            + ") or (");
                    sql.append(Sql_switcher.year(itemid) + "=" + xx.substring(0, 4) + " and "
                            + Sql_switcher.month(itemid) + "=" + xx.substring(5, 7) + " and ");
                    sql.append(Sql_switcher.day(itemid) + ">=" + xx.substring(8, 10) + "))");
                }
                if (orderSql.length() > 0) {
                    sql.append(orderSql.toString());
                } else {
                    sql.append(" order by organization.a0000,z03.Z0321,z03.state,z03.z0329 desc ");
                }

                rowSet = dao.search(sql.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                HashMap posPersonsMap = this.getPosPersons();
                while (rowSet.next()) {
                    LazyDynaBean lazyBean = new LazyDynaBean();
                    for (int i = 0; i < posFieldList.size(); i++) {
                        LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
                        String itemid = (String) bean.get("itemid");
                        String itemtype = (String) bean.get("itemtype");
                        String codesetid = (String) bean.get("codesetid");
                        int deciwidth = Integer.parseInt(((String) bean.get("deciwidth")));
                        if ("state".equalsIgnoreCase(itemid) || "z0321".equalsIgnoreCase(itemid)
                                || "z0325".equalsIgnoreCase(itemid)) {
                            continue;
                        }
                        if ("yprsl".equalsIgnoreCase(itemid)) {
                            if ("headHire".equals(employObject)) {
                                lazyBean.set("tjrsl",
                                                (posPersonsMap.get(rowSet.getString("z0301")) == null ||
                                                "".equals((String) posPersonsMap.get(rowSet.getString("z0301")))) ? "0"
                                                        : (String) posPersonsMap.get(rowSet.getString("z0301")));
                            } else {
                                lazyBean.set(itemid.toLowerCase(), (posPersonsMap.get(rowSet.getString("z0301")) == null ||
                                		"".equals((String) posPersonsMap.get(rowSet.getString("z0301")))) ? "0"
                                        : (String) posPersonsMap.get(rowSet.getString("z0301")));
                            }

                        } else if ("ypljl".equalsIgnoreCase(itemid)) {
                            if ("headHire".equals(employObject)) {
                                lazyBean.set("tjjl", "推荐");
                            } else {
                                lazyBean.set(itemid.toLowerCase(), "应聘");
                            }

                        } else if ("opentime".equalsIgnoreCase(itemid)) {
                            if (rowSet.getDate("create_time") != null) {
                                lazyBean.set(itemid.toLowerCase(), dateFormat.format(rowSet
                                        .getDate("create_time")));
                            } else {
                                lazyBean.set(itemid.toLowerCase(), "");
                            }
                        } else {
                            if ("A".equalsIgnoreCase(itemtype)) {
                                if ("0".equalsIgnoreCase(codesetid)) {
                                    lazyBean.set(itemid.toLowerCase(),
                                            rowSet.getString(itemid) == null ? "" : rowSet
                                                    .getString(itemid));
                                } else {
                                    String value = rowSet.getString(itemid) == null ? "" : rowSet
                                            .getString(itemid);
                                    value = AdminCode.getCodeName(codesetid, value);
                                    lazyBean.set(itemid.toLowerCase(), value);
                                }
                            } else if ("D".equalsIgnoreCase(itemtype)) {
                                if (rowSet.getDate(itemid) != null) {
                                    lazyBean.set(itemid.toLowerCase(), dateFormat.format(rowSet
                                            .getDate(itemid)));
                                } else {
                                    lazyBean.set(itemid.toLowerCase(), "");
                                }
                            } else if ("N".equalsIgnoreCase(itemtype)) {
                                if (rowSet.getString(itemid) != null) {
                                    lazyBean.set(itemid.toLowerCase(), PubFunc.round(rowSet
                                            .getString(itemid), deciwidth));
                                } else {
                                    lazyBean.set(itemid.toLowerCase(), "");
                                }
                            } else {
                                if (rowSet.getString(itemid) != null) {
                                    lazyBean.set(itemid.toLowerCase(), rowSet.getString(itemid));
                                } else {
                                    lazyBean.set(itemid.toLowerCase(), "");
                                }
                            }
                        }
                    }
                    String isNewPos = "0";
                    if (rowSet.getDate("newdate") != null && day != 0) {
                        Date d1 = calendar.getTime();
                        Date d2 = rowSet.getDate("newdate");
                        if (d2.getTime() >= d1.getTime()) {
                            isNewPos = "1";
                        }
                    }
                    lazyBean.set("isNewPos", isNewPos);
                    lazyBean.set("unitName", rowSet.getString("codeitemdesc") == null ? "" : rowSet
                            .getString("codeitemdesc"));
                    lazyBean.set("state", "0");
                    lazyBean.set("z0321Name", AdminCode
                            .getCodeName("UN", rowSet.getString("z0321")));
                    lazyBean.set("z0321", rowSet.getString("z0321") == null ? "" : rowSet
                            .getString("z0321"));
                    lazyBean.set("z0301", rowSet.getString("z0301"));
                    lazyBean.set("z0351", rowSet.getString("Z0351"));
                    String z0325 = rowSet.getString("z0325");
                    if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                        lazyBean.set("z0351", rowSet.getString("Z0351"));
                    } else {
                        CodeItem item = AdminCode.getCode("UM", z0325, Integer.parseInt(display_e0122));
                        if (item != null && recruitservice==0) {
                            lazyBean.set("z0351", item.getCodename() + "/" + rowSet.getString("Z0351"));
                        } else {
                            lazyBean.set("z0351", rowSet.getString("Z0351"));
                        }
                    }
                    String z0321 = rowSet.getString("z0321");
                    lazyBean.set("z0325", AdminCode.getCodeName("UM", z0325));
                    if (hitem != null) {
                        String vv = rowSet.getString(hitem.getItemid()) == null ? "" : rowSet.getString(hitem.getItemid());
                        if (hitem.isCode()) {
                            lazyBean.set(hitem.getItemid().toLowerCase(), AdminCode.getCodeName(hitem.getCodesetid(), vv));
                        } else {
                            lazyBean.set(hitem.getItemid().toLowerCase(), vv);
                        }
                        lazyBean.set("major", vv);
                    }
                    a_posList.add(lazyBean);
                }
            } else {
            	boolean isadd = isAddHireMajor(hitem);

                String hire_object = (String) map.get("hire_object");
                if (map.get("hire_object") == null || "".equals((String) map.get("hire_object"))) {
                    throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
                }
                StringBuffer sql = new StringBuffer(
                        "select z03.z0315,z03.z0301,z03.z0325,z03.Z0329,z0307 as newdate,z03.Z0331,z03.Z0333,organization.codeitemdesc,z03.Z0321,z03.Z0351,zp_members.create_time ");
                if (isadd) {
                    sql.append(",z03." + hitem.getItemid());
                }
                sql.append(" from organization ,z03 ");
                sql.append(" left join (select * from zp_members where zp_members.member_type=4)zp_members ");
                sql.append(" on zp_members.z0301=z03.Z0301 ");
                StringBuffer tableBuffer = new StringBuffer();
                StringBuffer whereBuffer = new StringBuffer();
                whereBuffer.append(" where z03.z0321=organization.codeitemid ");
                // -----增加外网显示指标排序的功能

                String orderSql = getPostFieldSort(map, tableBuffer, whereBuffer);
                if (tableBuffer.length() > 0) {
                    sql.append(tableBuffer.toString());
                }
                sql.append(whereBuffer.toString());
                sql.append(" and z0319='04' ");
                sql.append(getDateSql(">=", "Z0329"));
                sql.append(getDateSql("<=", "Z0331"));
                sql.append(getAppendWhereSql(conditionFieldList));
                if ("out".equals(employObject)) {
                    sql.append(" and ( z03." + hire_object + "<>'03' or z03." + hire_object
                            + " is null )");
                } else if ("headHire".equals(employObject)) {// 猎头招聘
                    sql.append(" and ( z03." + hire_object + "<>'03' or z03." + hire_object
                            + " is null )");
                    sql.append(" and z03.z0373='1' ");

                    String whl = getHeadHirePrivSql(this.loginUserName);
                    if (whl.length() > 0) {
                        sql.append(whl);
                    }
                } else if ("no".equalsIgnoreCase(employObject)) {
                    sql.append(" and 1=2 ");
                } else {
                    sql.append(" and z03." + hire_object + "='" + employObject + "' ");
                }
                sql.append(" and z03.z0301 in ('");
                String tt[] = z0301.substring(1).split("`");
                for (int i = 0; i < tt.length; i++) {

                    if (i == tt.length - 1) {
                        sql.append(tt[i] + "')");
                    } else {
                        sql.append(tt[i] + "','");
                    }
                }
                if ("0".equals(isAll) && day != 0)// 显示最新职位
                {
                    String xx = format.format(calendar.getTime());
                    String itemid = "z0307";
                    sql.append(" and ((" + Sql_switcher.year(itemid) + ">" + xx.substring(0, 4)
                            + ") or (" + Sql_switcher.year(itemid) + "=" + xx.substring(0, 4));
                    sql.append(" and " + Sql_switcher.month(itemid) + ">" + xx.substring(5, 7)
                            + ") or (");
                    sql.append(Sql_switcher.year(itemid) + "=" + xx.substring(0, 4) + " and "
                            + Sql_switcher.month(itemid) + "=" + xx.substring(5, 7) + " and ");
                    sql.append(Sql_switcher.day(itemid) + ">=" + xx.substring(8, 10) + "))");
                }
                if (orderSql.length() > 0) {
                    sql.append(orderSql.toString());
                } else {
                    sql.append(" order by organization.a0000,z03.Z0321,z03.state,z03.z0329 desc ");
                }

                rowSet = dao.search(sql.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                HashMap posPersonsMap = this.getPosPersons();
                while (rowSet.next()) {
                    LazyDynaBean lazyDynaBean = new LazyDynaBean();
                    lazyDynaBean.set("z0301", rowSet.getString("z0301"));
                    String z0321 = rowSet.getString("z0321");
                    String unitName = rowSet.getString("codeitemdesc");
                    lazyDynaBean.set("count",(posPersonsMap.get(rowSet.getString("z0301")) == null
                                            || "".equals((String) posPersonsMap.get(rowSet.getString("z0301")))) ? "0"
                                            : (String) posPersonsMap.get(rowSet.getString("z0301")));
                    String aDate = "";
                    String aDate0 = "";
                    Date d = rowSet.getDate("z0331");
                    Date d0 = rowSet.getDate("z0329");
                    if (d != null) {
                        aDate = dateFormat.format(d);
                    }
                    if (d0 != null) {
                        aDate0 = dateFormat.format(d0);
                    }

                    lazyDynaBean.set("z0331", aDate); // 有效结束日期
                    lazyDynaBean.set("z0329", aDate0);
                    lazyDynaBean.set("state", "0");

                    if (rowSet.getString("z0333") != null) {
                        // 工作地点
                        FieldItem item = DataDictionary.getFieldItem("z0333");
                        String codesetid = item.getCodesetid();
                        if (!"0".equals(codesetid)) {
                            lazyDynaBean.set("z0333", AdminCode.getCodeName(codesetid, rowSet
                                    .getString("z0333")));
                        } else {
                            lazyDynaBean.set("z0333", rowSet.getString("z0333"));
                        }
                    } else {
                        lazyDynaBean.set("z0333", " ");
                    }

                    String z0325 = rowSet.getString("z0325");
                    CodeItem un_item = null;
                    CodeItem um_item = null;
                    if (z0321 != null && !"".equals(z0321)) {
                        un_item = AdminCode.getCode("UN", z0321);// ,Integer.parseInt(display_e0122));
                    }

                    if (z0325 != null && !"".equals(z0325)) {
                        um_item = AdminCode.getCode("UM", z0325);
                    }

                    String posName = rowSet.getString("z0351") == null ? "" : rowSet.getString("z0351");
                    String temp_un_posName = "";
                    String temp_um_posName = "";
                    String temp_all_posName = "";
                    if(recruitservice==0) {
                    	if (un_item != null) {
                    		posName = un_item.getCodename() != null ? un_item.getCodename() + "/" : "";
                    		temp_un_posName = un_item.getCodename() != null ? un_item.getCodename()
                    				: "";

                    	}

                    	if (um_item != null) {
                    		posName = posName
                    				+ (um_item.getCodename() != null ? um_item.getCodename() + "/" : "");
                    		temp_um_posName = um_item.getCodename() != null ? um_item.getCodename()
                    				: "";
                    	}

                    	posName = posName + (rowSet.getString("z0351") == null ? "" : rowSet.getString("z0351"));
                    	temp_all_posName = rowSet.getString("z0351") == null ? "" : rowSet.getString("z0351");

                    	posName = this.getRealPosName(temp_un_posName, temp_um_posName,
                    			temp_all_posName, posName);
                    }
                    lazyDynaBean.set("posName", posName);
                    String isNewPos = "0";
                    if (rowSet.getDate("newdate") != null && day != 0) {
                        Date d1 = calendar.getTime();
                        Date d2 = rowSet.getDate("newdate");
                        if (d2.getTime() >= d1.getTime()) {
                            isNewPos = "1";
                        }
                    }

                    lazyDynaBean.set("isNewPos", isNewPos);
                    lazyDynaBean.set("unitName", unitName); // 所属单位名称
                    lazyDynaBean.set("z0321", z0321);
                    /*lazyDynaBean.set("z0337", rowSet.getString("z0337") == null ? "" : rowSet
                            .getString("z0337"));*/
                    lazyDynaBean.set("z0315", rowSet.getString("z0315") == null ? "" : rowSet
                            .getString("z0315"));
                    if (rowSet.getDate("create_time") != null) {
                        lazyDynaBean.set("opentime", dateFormat.format(rowSet
                                .getDate("create_time")));
                    } else {
                        lazyDynaBean.set("opentime", "");
                    }

                    if (isadd) {
                        String vv = rowSet.getString(hitem.getItemid()) == null ? "" : rowSet
                                .getString(hitem.getItemid());
                        String prefix = "";
                        if (z0325 == null || "".equals(z0325) || "0".equals(display_e0122)) {
                            prefix = "";
                        } else {
                            CodeItem item = AdminCode.getCode("UM", z0325, Integer
                                    .parseInt(display_e0122));
                            if (item != null && recruitservice==0) {
                                prefix = item.getCodename() + "/";
                            }
                        }

                        if (hitem.isCode()) {
                            lazyDynaBean.set(hitem.getItemid().toLowerCase(), prefix
                                    + AdminCode.getCodeName(hitem.getCodesetid(), vv));
                        } else {
                            lazyDynaBean.set(hitem.getItemid().toLowerCase(), prefix + vv);
                        }

                        lazyDynaBean.set("major", vv);
                    }

                    a_posList.add(lazyDynaBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return a_posList;
    }

    /**
     * 根据条件生成sql
     * @param fieldMap
     * @param setid
     * @return
     */
    public String getFieldItemSql(HashMap fieldMap, String setid) {
        StringBuffer sql = new StringBuffer("select * from fielditem where fieldsetid='" + setid
                + "' and itemid in(");
        ArrayList fieldList = (ArrayList) fieldMap.get(setid);
        StringBuffer itemids = new StringBuffer("");
        for (Iterator t = fieldList.iterator(); t.hasNext();) {
            String itemid = (String) t.next();
            if (itemid.indexOf("#") > -1) {
                itemid = itemid.substring(0, itemid.indexOf("#"));
            }

            itemids.append(",'" + itemid.toUpperCase() + "'");
        }
        if (itemids.length() > 0) {
            sql.append(itemids.substring(1));
        } else {
            sql.append("''");
        }
        sql.append(") order by displayid");
        return sql.toString();
    }

    /*
     * 删除简历信息
     */
    public void deleteResumeInfo(String a0100, String dbname, String setid, String i9999) {
        try {
        	ArrayList list = new ArrayList();
        	list.add(a0100);
            if ("A01".equalsIgnoreCase(setid)) {
                dao.delete("delete from " + dbname + setid + " where a0100=?", list);
            } else {
            	list.add(i9999);
                dao.delete("delete from " + dbname + setid + " where a0100=? and i9999=?", list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDateSql(String operate, String itemid) {
        StringBuffer sql = new StringBuffer("");
        Calendar d = Calendar.getInstance();
        int year = d.get(Calendar.YEAR);
        int month = d.get(Calendar.MONTH) + 1;
        int day = d.get(Calendar.DATE);
        if (">".equals(operate) || "<".equals(operate)) {
            sql.append(" and ( " + year + operate + Sql_switcher.year(itemid));
            sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and " + month + operate
                    + Sql_switcher.month(itemid) + "  )");
            sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and "
                    + Sql_switcher.month(itemid) + "=" + month + " and " + day + operate
                    + Sql_switcher.day(itemid) + "  )");
            sql.append(" ) ");
        } else if (">=".equals(operate) || "<=".equals(operate)) {
            if (">=".equals(operate)) {
                sql.append(" and ( " + year + ">" + Sql_switcher.year(itemid));
            } else {
                sql.append(" and ( " + year + "<" + Sql_switcher.year(itemid));
            }

            if (">=".equals(operate)) {
                sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and " + month + ">"
                        + Sql_switcher.month(itemid) + "  )");
            } else {
                sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and " + month + "<"
                        + Sql_switcher.month(itemid) + "  )");
            }

            sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and "
                    + Sql_switcher.month(itemid) + "=" + month + " and " + day + operate
                    + Sql_switcher.day(itemid) + "  )");
            sql.append(" ) ");
        } else if ("=".equals(operate)) {
            sql.append(" and (" + Sql_switcher.year(itemid) + "=" + year + " and "
                    + Sql_switcher.month(itemid) + "=" + month + " and " + Sql_switcher.day(itemid)
                    + "=" + day + "  )");
        }

        return sql.toString();
    }

    /**
     * 得到某职位的详细信息列表
     *
     * @param posID
     *            职位id
     * @return
     */
    public ArrayList getPosDescFiledList(String z0301, HashMap fieldMap) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        RecruitUtilsBo bo = new RecruitUtilsBo(conn);
        try {
            String[] fieldNames = getViewPosInfo(); // 字段
            if (fieldNames == null || fieldNames.length == 0) {
                return list;
            }

            boolean flag = false;
            RecordVo avo = new RecordVo("z03");
            //暂时只能通过长度来区分是否为加密数据
            z0301 = z0301.length()>12 ? PubFunc.decrypt(z0301) : z0301;
            avo.setString("z0301", z0301);
            try {
				avo = dao.findByPrimaryKey(avo);
			} catch (Exception e) {
				e.printStackTrace();
				return list;
			}
            String hireMajor = "";
            if (avo.getString("z0336") != null && "01".equals(avo.getString("z0336"))) {
                ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn, "1");
                HashMap map = xmlBo.getAttributeValues();
                if (map.get("hireMajor") != null) {
                    hireMajor = (String) map.get("hireMajor");
                }

                if (hireMajor != null && hireMajor.trim().length() > 0) {
                    flag = true;
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            StringBuffer buf = new StringBuffer();
            buf.append("select z03.*,a.* from z03 left join (select count(*) total,max(zp_pos_id) zp_pos_id from zp_pos_tache ");
            buf.append(" where zp_pos_tache.zp_pos_id='" + z0301
                    + "' and resume_flag='12') a on z03.z0301=a.zp_pos_id ");
            buf.append(" where z03.z0301='" + z0301 + "'");
            rs = dao.search(buf.toString());
            HashMap amap = new HashMap();
            List<String> fieldNameList = new ArrayList(Arrays.asList(fieldNames));
            if(!fieldNameList.contains("Z0351")) {
            	fieldNameList.add("Z0351");
            }
            if (rs.next()) {

                for (int i = 0; i < fieldNameList.size(); i++) {
                    LazyDynaBean abean = new LazyDynaBean();
                    String itemid = fieldNameList.get(i);
                    if ("z0311".equalsIgnoreCase(itemid) && flag) {
                        itemid = hireMajor.toLowerCase();
                    }
                    if (amap.get(itemid.toLowerCase()) != null) {
                        continue;
                    }

                    if (fieldMap.get(itemid.toLowerCase()) == null) {
                        continue;
                    }

                    String[] temp = ((String) fieldMap.get(itemid.toLowerCase())).split("\\^");
                    String desc = temp[0];
                    String type = temp[1];
                    String codesetid = temp[2];
                    if (codesetid == null) {
                        codesetid = "0";
                    }
                    if ("A".equals(type)) {
                        if (rs.getString(itemid) == null) {
                            abean.set("value", "");
                        } else {
                            if ("0".equals(codesetid)) {
                                abean.set("value", rs.getString(itemid) == null ? "" : rs
                                        .getString(itemid));
                            } else {
                                abean.set("value", AdminCode.getCodeName(codesetid, rs
                                        .getString(itemid)));
                            }
                        }
                    } else if ("D".equals(type)) {
                        if (rs.getDate(itemid) != null) {
                        	String format = bo.getDateFormat(itemid);
                            sdf = new SimpleDateFormat(format);
                            Date time =rs.getTimestamp(itemid);
                            abean.set("value", sdf.format(time));
                        } else {
                            abean.set("value", "");
                        }
                    } else if ("M".equals(type)) {
                        abean.set("value", Sql_switcher.readMemo(rs, itemid).replaceAll("%26amp;",
                                "&").replace("\n", "</br>"));
                    } else if ("N".equals(type)) {
                        if (rs.getString(itemid) == null) {
                            abean.set("value", "");
                        } else {
                            abean.set("value", rs.getString(itemid));
                        }
                    }
                    abean.set("desc", desc);
                    abean.set("type", type);
                    abean.set("itemid", itemid);
                    amap.put(itemid.toLowerCase(), "1");
                    list.add(abean);
                }
                /** 在职位浏览页面加入该职位应聘且已经初试合格的人的数量 */
                // 建研院要求不显示已选故去掉 如与其他需求可放开
                LazyDynaBean abean = new LazyDynaBean();
                abean.set("desc", AdminCode.getCodeName("36", "12"));
                abean.set("type", "D");
                abean.set("itemid", "total");
                abean.set("value", rs.getString("total") == null ? "0" : rs.getString("total"));
                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    /**
     * 职位描述参数
     *
     * @return String[]
     */
    public String[] getViewPosInfo() {
        try {
            if (viewPosInfo == null) {
                String viewPosFieldIDs = "";
                ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
                HashMap map = parameterXMLBo.getAttributeValues();
                if (map.get("view_pos") != null
                        && ((String) map.get("view_pos")).trim().length() > 0) {
                    viewPosFieldIDs = (String) map.get("view_pos");
                }

                if (viewPosFieldIDs.indexOf("`") == -1) {

                    viewPosInfo = new String[1];
                    viewPosInfo[0] = viewPosFieldIDs;
                } else {
                    String[] fields = viewPosFieldIDs.split("`");
                    viewPosInfo = fields;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return viewPosInfo;
    }

    /**
     * 判断 职位id 是否 为已选职位
     *
     * @param posID
     * @param applyedPosList
     * @return
     */
    public String getIsApplyed(String posID, ArrayList applyedPosList) {
        String flag = "0";
        if (applyedPosList == null || applyedPosList.size() == 0) {
            return flag;
        }

        for (int i = 0; i < applyedPosList.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) applyedPosList.get(i);
            String z0301 = (String) abean.get("z0301");
            if (z0301.equalsIgnoreCase(posID)) {
                flag = "1";
                break;
            }
        }
        return flag;
    }


    /**
     * 得到当前用户已经申请职位是否有信息
     *
     * @param a0100
     * @return
     */
    public String getApplyMessage(String a0100) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        String applyMessage = "";
        try {
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            String hireMajor = "";
            if (map.get("hireMajor") != null) {
                hireMajor = (String) map.get("hireMajor");
            }

            FieldItem hitem = null;
            if (hireMajor != null && !"".equals(hireMajor)) {
                hitem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
            }

            boolean isadd = isAddHireMajor(hitem);
            StringBuffer sql = new StringBuffer(
                    "select zpt.description,z03.z0336,z03.z0381,org2.codesetid ,org.codeitemdesc posName,org2.codeitemdesc unitName,z03.z0329,z03.z0331,z03.Z0333,z03.z0315,zpt.thenumber");
            if (isadd) {
                sql.append(",z03." + hitem.getItemid());
            }
            sql.append(",zpt.zp_pos_id,zpt.resume_flag,z03.z0325,z03.z0321,z03.z0351,z03.z0301,zpt.status from zp_pos_tache zpt,z03");
            // 解决只有单位时候查不到已申请职位问题
            sql.append(" left join (select * from organization where codesetid<>'@K' ) org on  z03.z0325=org.codeitemid"
                            + " left join (select * from organization where codesetid='UN' ) org2  on  z03.z0321=org2.codeitemid");

            sql.append("  where (email_confirm is null OR email_confirm = -1) and zpt.zp_pos_id=z03.z0301 and zpt.a0100=? order by zpt.thenumber ");
            ArrayList values = new ArrayList();
            values.add(a0100);
            rowSet = dao.search(sql.toString(),values);
            while (rowSet.next()) {
                if(!StringUtils.isEmpty(rowSet.getString("description")))
                 {
                    applyMessage = "1";
                 }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return applyMessage;
    }



    /**
     * 得到当前用户已经申请的职位信息
     *
     * @param a0100
     * @return
     */
    public ArrayList getApplyedPosList(String a0100) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            String hireMajor = "";
            if (map.get("hireMajor") != null) {
                hireMajor = (String) map.get("hireMajor");
            }

            FieldItem hitem = null;
            if (hireMajor != null && !"".equals(hireMajor)) {
                hitem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
            }

            boolean isadd = isAddHireMajor(hitem);
            StringBuffer sql = new StringBuffer(
                    "select zpt.description,z03.z0336,z03.z0381,org2.codesetid ,org.codeitemdesc posName,org2.codeitemdesc unitName,z03.z0329,z03.z0331,z03.Z0333,z03.z0315,zpt.thenumber");
            if (isadd) {
                sql.append(",z03." + hitem.getItemid());
            }
            sql.append(",zpt.zp_pos_id,zpt.resume_flag,z03.z0325,z03.z0321,z03.z0351,z03.z0301,zpt.status from zp_pos_tache zpt,z03");
            // 解决只有单位时候查不到已申请职位问题
            sql.append(" left join (select * from organization where codesetid<>'@K' ) org on  z03.z0325=org.codeitemid"
                            + " left join (select * from organization where codesetid='UN' ) org2  on  z03.z0321=org2.codeitemid");

            sql.append("  where zpt.zp_pos_id=z03.z0301 and zpt.a0100=? order by zpt.thenumber ");
            ArrayList values = new ArrayList();
            values.add(a0100);
            rowSet = dao.search(sql.toString(),values);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            FieldItem item = DataDictionary.getFieldItem("z0333");
            String codesetid = item.getCodesetid();
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
                display_e0122 = "0";
            }
            int x = 0;
            int s = 0;
            while (rowSet.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                CodeItem citem = AdminCode.getCode(rowSet.getString("codesetid"), rowSet
                        .getString("z0321"));// ,Integer.parseInt(display_e0122));
                CodeItem um_citem = AdminCode.getCode("UM", rowSet.getString("z0325"));
                // abean.set("z0311",rowSet.getString("z0311"));
                abean.set("z0336", rowSet.getString("z0336"));
                String z0336 = rowSet.getString("z0336");
                String temp_un_posName = "";
                String temp_um_posName = "";
                String temp_all_posName = "";
                String posName = rowSet.getString("z0351") == null ? "" : rowSet.getString("z0351");
                temp_un_posName = citem != null ? citem.getCodename() : "";
                temp_um_posName = um_citem != null ? um_citem.getCodename() : "";
                temp_all_posName = rowSet.getString("z0351");
                if(recruitservice==0) {
                    posName = this.getRealPosName(temp_un_posName, temp_um_posName,
                            temp_all_posName, posName);
                }
                abean.set("posName", posName);
                s++;

                String status = rowSet.getString("status") != null ? rowSet.getString("status")
                        : "";
                if ("2".equals(status)) {
                    abean.set("unitName", "拒绝");
                } else {
                    if (StringUtils.isEmpty(rowSet.getString("resume_flag"))) {
                        abean.set("unitName", AdminCode.getCodeName("36",
                        		rowSet.getString("resume_flag")));// resume_flag 简历状态
                    } else {
                        abean.set("unitName", this.getCustomStatusName(rowSet.getString("resume_flag"),
                        		rowSet.getString("z0381")));// resume_flag
                    }
                                                                                       // 简历状态
                }

                String startDateStr = "";
                String endDateStr = "";
                if (rowSet.getDate("z0329") != null) {
                    startDateStr = dateFormat.format(rowSet.getDate("z0329"));
                }

                if (rowSet.getDate("z0331") != null) {
                    endDateStr = dateFormat.format(rowSet.getDate("z0331"));
                }

                abean.set("z0329", startDateStr);
                abean.set("z0331", endDateStr);
                if (rowSet.getString("z0333") != null)// 工作地点
                {
                    if (!"0".equals(codesetid)) {
                        abean.set("z0333", AdminCode.getCodeName(codesetid, rowSet.getString("z0333")));
                    } else {
                        abean.set("z0333", rowSet.getString("z0333"));
                    }
                } else {
                    abean.set("z0333", "");
                }

                abean.set("z0315", rowSet.getString("z0315") == null ? "" : rowSet.getString("z0315"));// 现在作为招聘人数
                abean.set("thenumber", rowSet.getString("thenumber") == null ? "" : rowSet.getString("thenumber"));// 志愿
                abean.set("zp_pos_id", rowSet.getString("zp_pos_id"));// 用工需求号
                abean.set("canDelPos", this.canDelPos(a0100, rowSet.getString("zp_pos_id")));// 申请的职位记录能否删除
                abean.set("z0325", rowSet.getString("z0325") == null ? "" : rowSet.getString("z0325"));// 所属部门
                abean.set("z0321", rowSet.getString("z0321") == null ? "" : rowSet.getString("z0321"));// 所属单位
                abean.set("z0301", rowSet.getString("z0301") == null ? "" : rowSet.getString("z0301"));
                abean.set("description", StringUtils.isEmpty(rowSet.getString("description")) ? 0 : 1);
                abean.set("descripValue", rowSet.getString("description"));
                list.add(abean);
            }
            if (x > 0 && s > 0)// 既有校园又有社会
            {
                this.setHasXaoYuan("1");
            }
            if (x > 0 && s == 0)// 只有校园
            {
                this.setHasXaoYuan("2");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }

    /**
     * 查询制定流程中某个状态的用户定义名称
     *
     * @param resumeFlag
     * @param flow_id
     * @return
     * @throws GeneralException
     */
    public String getCustomStatusName(String resumeFlag, String flow_id) throws GeneralException {
        String res = "";

        String sql = "select sta.* from zp_flow_links link,zp_flow_status sta where link.id = sta.link_id and status=? and link.flow_id = ?";
        ArrayList values = new ArrayList();
        values.add(resumeFlag);
        values.add(flow_id);

        RowSet rs = null;
        try {
            rs = dao.search(sql, values);
            if (rs.next()) {
                res = rs.getString("custom_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return res;
    }

    /**
     * 判断外网在应聘职位里指定职位申请记录能否删除已申请的职位
     *
     * @param dao
     * @param a0100
     * @param dbName
     * @return
     */
    public boolean canDelPos(String a0100, String z0301) {
        boolean res = true;
        //zxj 20181010 原已淘汰的申请不允许删除，现应人大要求改为已接收的职位申请都不允许删除
        StringBuffer sql = new StringBuffer();
        sql.append("select 1 from zp_pos_tache");
        sql.append(" where a0100=?");
        sql.append(" and nbase=?");
        sql.append(" and zp_pos_id=?");
        sql.append(" and resume_flag is not null");
        if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
            sql.append(" and resume_flag<>''");
        }

        ArrayList<String> valueList = new ArrayList<String>();
        valueList.add(a0100);
        valueList.add(a_dbName);
        valueList.add(z0301);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), valueList);
            if (rs.next()) {
                res = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 取得 子集 信息集合
     *
     * @param showFieldList
     * @param a0100
     * @param setid
     * @param dbname
     * @return
     */
    public ArrayList getShowFieldDataList(ArrayList showFieldList, String a0100, String setid,
            String dbname) {
        ArrayList list = new ArrayList();
        if (showFieldList.size() == 0) {
            return list;
        }

        RowSet rowSet2 = null;
        try {
            rowSet2 = dao.search("select * from " + dbname + setid + " where a0100='" + a0100 + "'");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            while (rowSet2.next()) {
                LazyDynaBean a_bean = new LazyDynaBean();
                if (!"a01".equalsIgnoreCase(setid)) {
                    a_bean.set("i9999", rowSet2.getString("i9999"));
                }
                for (int i = 0; i < showFieldList.size(); i++) {
                    LazyDynaBean abean = (LazyDynaBean) showFieldList.get(i);
                    String itemid = (String) abean.get("itemid");
                    String itemtype = (String) abean.get("itemtype");
                    String codesetid = (String) abean.get("codesetid");
                    int itemlength = ((Integer) abean.get("itemlength")).intValue();
                    String value = "";
                    if ("A".equals(itemtype)) {
                        if (rowSet2.getString(itemid) != null) {
                            if ("0".equals(codesetid)) {
                                value = rowSet2.getString(itemid);
                            } else {
                                value = AdminCode.getCodeName(codesetid, rowSet2.getString(itemid));
                            }
                        }
                    } else if ("M".equals(itemtype)) {
                        value = Sql_switcher.readMemo(rowSet2, itemid);
                        value = value.replaceAll("\r\n", "<br>");
                        value = value.replaceAll(" ", "&nbsp;&nbsp;");
                    } else if ("D".equals(itemtype)) {
                        if (rowSet2.getDate(itemid) != null) {
                            value = dateFormat.format(rowSet2.getDate(itemid));
                        }
                    } else if ("N".equals(itemtype)) {
                        if (rowSet2.getString(itemid) != null) {
                            value = rowSet2.getString(itemid);
                        }
                    }
                    a_bean.set(itemid, keyWord_reback(value));
                }
                list.add(a_bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet2);
        }
        return list;
    }

    public HashMap getItemMemo(String setid) {
        HashMap map = new HashMap();
        RowSet rs = null;
        try {
            rs = dao.search("select itemid,itemmemo from fielditem where UPPER(fieldsetid)='"
                    + setid.toUpperCase() + "'");
            while (rs.next()) {
                String itemmemo = Sql_switcher.readMemo(rs, "itemmemo");
                map.put(rs.getString("itemid").toUpperCase(), itemmemo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
     * 取得简历子集 列表需显示的 列指标 集合
     *
     * @param setid
     * @param fieldSetMap
     * @param fieldMap
     * @param flag
     *            0:取得简历子集有效指标 集合 1: 取得简历子集 列表需显示的 列指标 集合
     * @return
     */
    public ArrayList getShowFieldList(String setid, HashMap fieldSetMap, HashMap fieldMap, int flag) {
        ArrayList list = new ArrayList();
        try {
            ArrayList list1 = (ArrayList) fieldMap.get(setid.toUpperCase());
            HashMap map = (HashMap) fieldSetMap.get(setid.toLowerCase());
            if (list1 == null) {
                return list;
            }

            HashMap amap = this.getItemMemo(setid);
            for (int i = 0; i < list1.size(); i++) {
                String itemid = ((String) list1.get(i)).toLowerCase();
                String itemDesc = "";
                if(itemid.indexOf("#") > -1) {
                    itemDesc = itemid.substring(itemid.indexOf("#") + 1);
                    itemid = itemid.substring(0, itemid.indexOf("#"));
                }


                String opt_str = (String) map.get(itemid.toLowerCase());
                String[] t = opt_str.split("#");
                if (flag == 1) {
                    if ("1".equals(t[0])) {
                        FieldItem item = DataDictionary.getFieldItem(itemid);
                        if (item != null) {
                            /** 如果是自动增加的序号指标，并且简历状态不满足设置的条件，不予显示出来 */
                            if (item != null && item.isSequenceable() && !this.isVisiablSeqField) {
                                continue;
                            }

                            LazyDynaBean abean = new LazyDynaBean();
                            abean.set("itemid", item.getItemid());
                            abean.set("itemdesc", item.getItemdesc());
                            if(StringUtils.isNotEmpty(itemDesc)) {
                                abean.set("itemdesc", itemDesc);
                            }

                            abean.set("itemtype", item.getItemtype());
                            abean.set("codesetid", item.getCodesetid());
                            abean.set("itemmemo", (String) amap.get(item.getItemid().toUpperCase()));
                            abean.set("itemlength", Integer.valueOf(item.getItemlength()));
                            list.add(abean);
                        }
                    }
                } else {
                    FieldItem item = DataDictionary.getFieldItem(itemid);
                    if (item != null) {
                        /** 如果是自动增加的序号指标，并且简历状态不满足设置的条件，不予显示出来 */
                        if (item != null && item.isSequenceable() && !this.isVisiablSeqField) {
                            continue;
                        }

                        LazyDynaBean abean = new LazyDynaBean();
                        abean.set("itemid", item.getItemid());
                        abean.set("itemdesc", item.getItemdesc());
                        if(StringUtils.isNotEmpty(itemDesc)) {
                            abean.set("itemdesc", itemDesc);
                        }

                        abean.set("itemtype", item.getItemtype());
                        abean.set("codesetid", item.getCodesetid());
                        abean.set("itemmemo", (String) amap.get(item.getItemid().toUpperCase()));
                        abean.set("itemlength", Integer.valueOf(item.getItemlength()));
                        list.add(abean);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取外网logo连接地址
     *
     * @return
     */
    public String getNetHref() {
        String http = "";
        try {
            if (netHref == null && "".equals(netHref)) {
                ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
                HashMap map = parameterXMLBo.getAttributeValues();
                if (map != null && map.get("net_href") != null) {
                    netHref = (String) map.get("net_href");
                    http = netHref;
                }
            } else {
                http = netHref;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return http;
    }

    public boolean isOnlyChecked() {
        boolean flag = false;
        try {
            if (isOnlyChecked != null && !"".equals(isOnlyChecked)) {
                return true;
            } else {
                RecordVo vo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
                if (vo == null) {
                    return false;
                } else {
                    String str = vo.getString("str_value");
                    if (str == null || "".equals(str)) {
                        return false;
                    } else {
                        StringBuffer buf = new StringBuffer("");
                        String[] arr = str.split(",");
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i] == null || "".equals(arr[i])) {
                                continue;
                            }
                            buf.append("," + arr[i].substring(4));
                        }
                        if (buf.toString().length() > 0) {
                            flag = true;
                            ;
                            isOnlyChecked = buf.toString().substring(1);
                        } else {
                            flag = false;
                            isOnlyChecked = "";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据后台设置禁止修改的简历状态判断该简历是否可修改
     *
     * @param dao
     * @param a0100
     * @param dbName
     * @param resumeState
     * @return
     */
    public synchronized String getWriteable(ContentDAO dao, String a0100, String dbName) {
        /** 默认为可以修改 */
        String writeable = "0";
        RowSet rs = null;
        RowSet rowSet = null;
        try {
        	// 根据简历所报职位的状态和有效结束日期和招聘流程中此环节“是否允许修改简历”的状态控制简历修改
        	StringBuffer sql = new StringBuffer();
			sql.append("select Z0101,Z0331 from Z03 Z, zp_pos_tache P,zp_flow_status  F  ");
			sql.append(" where P.resume_flag=F.status ");
			sql.append(" and Z.Z0301 = P.ZP_POS_ID ");
			sql.append(" and P.link_id=F.link_id  ");
			sql.append(" and (f.resume_modify=0 or ");
			sql.append(" ( f.resume_modify is null  ");
			sql.append(" and ( P.resume_flag not in ('0105','0106','0205','0206','0306','0307', ");
			sql.append(" '0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005'))))  ");
			sql.append(" and a0100= '").append(a0100).append("'");
			sql.append(" and p.nbase= '").append(dbName).append("'");
			sql.append(" and Z.Z0319 <>'06'  ");
			rs = dao.search(sql.toString());

			while(rs.next()) {
            	String z0101 = rs.getString("Z0101");
            	Timestamp z0331Time = rs.getTimestamp("Z0331");
                Date nowTime = new Date();
                int result = z0331Time.compareTo(nowTime);
                if (result > 0)
                {
                	if(StringUtils.isNotEmpty(z0101)){
                		sql = new StringBuffer();
            			sql.append("select Z0109 from Z01 ");
            			sql.append(" where Z0129 <>'06'");
            			sql.append(" and  Z0101 ='").append(z0101).append("'");
            			rowSet= dao.search(sql.toString());
            			if(rowSet.next()){
            				Timestamp Z0109 = rowSet.getTimestamp("Z0109");
                            result = Z0109.compareTo(nowTime);
                            if(result > 0) {
                            	/** 不可修改 */
                                writeable = "1";
                                break;
                            }
            			}
                	}else{
                		/** 不可修改 */
                        writeable = "1";
                        break;
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(rowSet);
        }
        return writeable;
    }

    /**
     * 保存简历子集信息
     *
     * @param dbname
     *            招聘应用库
     * @param resumeFieldList
     * @param setID
     *            子集id
     * @param a0100
     * @param flag
     *            1:增加 0：修改
     */
    public synchronized void addResumeSetInfo(String dbname, ArrayList resumeFieldList,
            String setID, String a0100, String flag, String i9999, boolean flag2)
            throws GeneralException {
        RowSet rs = null;
        try {
            String tableName = dbname.toLowerCase() + setID;

            RecordVo vo = new RecordVo(tableName);
            vo.setString("a0100", a0100);
            if ("A01,A0A".contains(setID.toLowerCase()) || "A01,A0A".contains(setID.toUpperCase())) {
                if ("A0A".equalsIgnoreCase(setID)) {
                    rs = dao.search("select * from " + tableName + " where a0100='" + a0100 + "'");
                    if (rs.next()) {
                        vo.setInt("i9999", 1);
                        vo = dao.findByPrimaryKey(vo);
                        vo = getRecordVo(vo, resumeFieldList);
                        dao.updateValueObject(vo);
                    } else {
                        vo = getRecordVo(vo, resumeFieldList);
                        vo.setInt("i9999", 1);
                        dao.addValueObject(vo);
                    }
                }
                vo = dao.findByPrimaryKey(vo);
                vo = getRecordVo(vo, resumeFieldList);
                dao.updateValueObject(vo);
            } else {
                if (("1".equals(flag) || "2".equals(flag)) && "0".equals(i9999)) // 1:增加
                                                                                 // 0：修改
                                                                                 // 2:保存走下一步
                {
                    String masterless = "";// 全不填不保存
                    for (Iterator t = resumeFieldList.iterator(); t.hasNext();) {
                        LazyDynaBean rec = (LazyDynaBean) t.next();
                        String value = (String) rec.get("value");
                        if (!"".equals(value) && "".equals(masterless)) {
                            masterless = "tt";
                        }
                    }
                    if ("tt".equals(masterless)) {
                        vo = getRecordVo(vo, resumeFieldList);
                        if (flag2) {
                            dao.update("delete from " + tableName + " where a0100='" + a0100 + "'");
                        }
                        String stri9999 = DbNameBo.insertSubSetA0100(tableName, a0100, this.conn);
                        vo.setInt("i9999", Integer.parseInt(stri9999));

                        dao.updateValueObject(vo);
                    }
                } else {
                    vo.setInt("i9999", Integer.parseInt(i9999));
                    rs = dao.search("select * from " + tableName + " where i9999=" + i9999
                            + " and a0100='" + a0100 + "'");
                    if (rs.next()) {
                        vo = dao.findByPrimaryKey(vo);
                        vo = getRecordVo(vo, resumeFieldList);
                        dao.updateValueObject(vo);
                    } else {
                        vo = getRecordVo(vo, resumeFieldList);
                        vo.setInt("i9999", 1);
                        dao.addValueObject(vo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    public RecordVo getRecordVo(RecordVo vo, ArrayList resumeFieldList) throws GeneralException {
        for (Iterator t = resumeFieldList.iterator(); t.hasNext();) {
            LazyDynaBean rec = (LazyDynaBean) t.next();
            String itemtype = (String) rec.get("itemtype");
            String itemid = (String) rec.get("itemid");
            String onlyname = this.getOnly_field();
            if(onlyname!=null) {
                onlyname = onlyname.toLowerCase();
            } else {
                onlyname = "";
            }
            //zxj 20170715 密码指标与账号是绑定的，不允许修改，唯一性指标不允许修改
            if(!"headHire".equals(hireChannel)&&(itemid.equalsIgnoreCase(ConstantParamter.getEmailField()) || (StringUtils.isNotEmpty(onlyname)&&itemid.toLowerCase().contains(onlyname)))) {
                continue;
            }

            String value = PubFunc.keyWord_filter((String) rec.get("value") == null ? ""
                    : (String) rec.get("value"));// 20141023 dengcan
            if(!"m".equalsIgnoreCase(itemtype)) {
                value = value.trim();
            }
            String decimalwidth = (String) rec.get("decimalwidth");
            decimalwidth = (decimalwidth == null || "".equals(decimalwidth.trim())) ? "0"
                    : decimalwidth.trim();

            String itemlength = (String) rec.get("itemlength");
            String itemdesc = (String) rec.get("itemdesc");
            try {
                if (value == null || value.length() <= 0) {
                    vo.setString(itemid.toLowerCase(), null);
                    continue;
                }

                byte[] b = value.getBytes();
                // 后台验证输入数据长度
                if (!"M".equalsIgnoreCase(itemtype) && !"D".equalsIgnoreCase(itemtype)) {

                    if (("A".equalsIgnoreCase(itemtype) || ("N".equalsIgnoreCase(itemtype) && "0"
                            .equals(decimalwidth)))
                            && b.length > Integer.parseInt(itemlength)) {
                        throw new GeneralException(itemdesc + "长度不能大于" + itemlength + "位！");
                    }

                    // 有小数位的
                    if ("N".equalsIgnoreCase(itemtype) && !"0".equals(decimalwidth)) {
                        java.math.BigDecimal decimalValue = new java.math.BigDecimal(value);
                        // 判断小数位长度
                        if (Integer.parseInt(decimalwidth) < decimalValue.scale()) {
                            throw new GeneralException(itemdesc + "的小数位数不能大于" + decimalwidth + "位！");
                        }

                        // 判断整数位长度
                        if (Integer.parseInt(itemlength) < decimalValue.precision()
                                - decimalValue.scale()) {
                            throw new GeneralException(itemdesc + "的整数位数不能大于" + itemlength + "位！");
                        }
                    }
                }

                if ("A".equals(itemtype)) {
                    vo.setString(itemid.toLowerCase(), value);
                } else if ("D".equals(itemtype)) {
                    value = value.replaceAll("\\.", "-");
                    value = value.replaceAll("\\'", "");
                    String[] dd = value.split("-");
                    String year = dd[0];
                    String month = "01";
                    String day = "01";
                    if (dd.length >= 2 && dd[1] != null && dd[1].trim().length() > 0) {
                        month = dd[1];
                    }
                    if (dd.length >= 3 && dd[2] != null && dd[2].trim().length() > 0) {
                        day = dd[2];
                    }
                    Calendar d = Calendar.getInstance();
                    d.set(Calendar.YEAR, Integer.parseInt(year));
                    d.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                    d.set(Calendar.DATE, Integer.parseInt(day));
                    vo.setDate(itemid.toLowerCase(), year + "-" + month + "-" + day);
                } else if ("M".equals(itemtype)) {

                    vo.setString(itemid.toLowerCase(), value);
                } else if ("N".equals(itemtype)) {
                    if ("0".equals(decimalwidth)) {
                        vo.setInt(itemid.toLowerCase(), Integer.parseInt(value));
                    } else {
                        vo.setDouble(itemid.toLowerCase(), Double.parseDouble(value));
                    }
                }

            } catch (GeneralException e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
        }
        return vo;
    }

    // ///////////////////////////////保存照片///////////////////////////////////////////////
    public void insertPic(FormFile file, String userid, String userbase, String userName) {
        try {
            RecordVo vo = new RecordVo(userbase.toLowerCase() + "a00");
            deleteDAO(userid, userbase);
            insertDAO(vo, file, userid, userbase, userName);
            PhotoImgBo photoBo = new PhotoImgBo(this.conn);
            // zxj 20141008 删除多媒体文件路径下用户照片，否则显示简历时仍显示旧照片
            photoBo.delUserPhoto(userbase, userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过底层函数进行文件保存
     *
     * @param vo
     * @param file
     * @param dao
     * @throws GeneralException
     */
    private void insertDAO(RecordVo vo, FormFile file, String userid, String userbase,
            String userName) throws GeneralException {
        boolean bflag = true;
        if (file == null || file.getFileSize() == 0) {
            bflag = false;
        }
        int recid = Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase + "a00",
                userid, "A0100", this.conn));
        try {
            vo.setString("a0100", userid);
            vo.setInt("i9999", recid);
            vo.setString("flag", "P");
            // vo.setString("state",0);
            vo.setInt("id", 0);
            vo.setDate("createtime", DateStyle.getSystemTime());
            vo.setDate("modtime", DateStyle.getSystemTime());
            vo.setString("createusername", userName);
            vo.setString("modusername", userName);
            if (bflag) {
                String fname = file.getFileName();
                int indexInt = fname.lastIndexOf(".");
                String ext = fname.substring(indexInt, fname.length());
                vo.setString("ext", ext);
                /** blob字段保存,数据库中差异 */
                switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL:
                    // Blob blob = getOracleBlob(vo, file);
                    // vo.setObject("thefile",blob);
                    break;
                default:

                    byte[] data = ImageBO.imgByte(file, ext.substring(1)); // 复制图片，过滤掉木马程序
                    if (data == null) {
                        return;
                    }
                    // byte[] data=file.getFileData();
                    vo.setObject("ole", data);
                    break;
                }
            }
            dao.addValueObject(vo);
            if (bflag && Sql_switcher.searchDbServer() == Constant.ORACEL) {
                RecordVo updatevo = new RecordVo(userbase + "a00");
                updatevo.setString("a0100", userid);
                updatevo.setInt("i9999", recid);
                Blob blob = getOracleBlob(updatevo, file, userbase, userid, recid);
                if (blob != null) {
                    updatevo.setObject("ole", blob);
                    dao.updateValueObject(updatevo);
                }
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        }
    }

    public void insertDAO(RecordVo vo, FormFile file, String userid, String userbase,
            String userName, String flag, String i9999, String type, String fileName)
            throws GeneralException {
        boolean bflag = true;
        if ((file == null || file.getFileSize() == 0)
                && (fileName == null || "".equals(fileName.trim()))) {
            bflag = false;
        }

        try {
            if ("0".equals(type))// new
            {
                int recid = Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase
                        + "a00", userid, "A0100", this.conn));
                vo.setString("a0100", userid);
                vo.setInt("i9999", recid);
                vo.setString("flag", flag);
                // vo.setString("state",0);
                vo.setInt("id", 0);
                if (fileName != null && fileName.trim().length() > 0) {
                    vo.setString("title", fileName);
                } else {
                    vo.setString("title", file.getFileName().substring(0,
                            file.getFileName().lastIndexOf(".")));
                }
                vo.setDate("createtime", DateStyle.getSystemTime());
                vo.setDate("modtime", DateStyle.getSystemTime());
                vo.setString("createusername", userName);
                vo.setString("modusername", userName);
                if (bflag) {
                    String fname = file.getFileName();
                    int indexInt = fname.lastIndexOf(".");
                    String ext = fname.substring(indexInt, fname.length());
                    vo.setString("ext", ext);
                    /** blob字段保存,数据库中差异 */
                    switch (Sql_switcher.searchDbServer()) {
                    case Constant.ORACEL:
                        break;
                    default:
                        byte[] data = null;
                        // 图片特殊处理，防挂马
                        if (!isImageFile(ext)) {
                            data = file.getFileData();
                        } else {
                            data = ImageBO.imgByte(file, ext.substring(1));
                        }
                        vo.setObject("ole", data);
                        break;
                    }
                }
                dao.addValueObject(vo);
                if (bflag && Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    RecordVo updatevo = new RecordVo(userbase + "a00");
                    updatevo.setString("a0100", userid);
                    updatevo.setInt("i9999", recid);
                    Blob blob = getOracleBlob(updatevo, file, userbase, userid, recid);
                    if (blob != null) {
                        updatevo.setObject("ole", blob);
                        dao.updateValueObject(updatevo);
                    }
                }
            } else {// edit
                vo.setString("a0100", userid);
                vo.setInt("i9999", Integer.parseInt(i9999));
                vo = dao.findByPrimaryKey(vo);
                if (fileName != null && fileName.trim().length() > 0) {
                    vo.setString("title", fileName);
                }
                vo.setDate("modtime", DateStyle.getSystemTime());
                if (file != null && file.getFileSize() > 0) {

                    String fname = file.getFileName();
                    int indexInt = fname.lastIndexOf(".");
                    String ext = fname.substring(indexInt, fname.length());

                    if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                        Blob blob = getOracleBlob(vo, file, userbase, userid, Integer
                                .parseInt(i9999));
                        if (blob != null) {
                            vo.setObject("ole", blob);
                        }
                    } else {

                        byte[] data = file.getFileData();
                        vo.setObject("ole", data);
                    }
                    vo.setString("ext", ext);
                    dao.updateValueObject(vo);
                } else {
                    String tableName = vo.getModelName();
                    String sql = "update " + tableName + " set title='" + fileName
                            + "' where a0100='" + userid + "' and i9999=" + Integer.parseInt(i9999);
                    dao.update(sql);
                }

            }
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        }
    }

    /**
     * 是否是图片文件（jbp,gif,bmp)
     *
     * @Title: isImageFile
     * @Description:
     * @param fileExt
     *            文件扩展名
     * @return
     */
    private boolean isImageFile(String fileExt) {
        String ext = fileExt;
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }

        return "jpg".equalsIgnoreCase(fileExt.substring(1))
                || "jpeg".equalsIgnoreCase(fileExt.substring(1))
                || "gif".equalsIgnoreCase(fileExt.substring(1))
                || "bmp".equalsIgnoreCase(fileExt.substring(1))
                || "png".equalsIgnoreCase(fileExt.substring(1));
    }

    /**
     * @param vo
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Blob getOracleBlob(RecordVo vo, FormFile file, String userbase, String userid, int recid)
            throws FileNotFoundException, IOException {
        StringBuffer strSearch = new StringBuffer();
        InputStream stream = null;
        Blob blob = null;
        try {
            strSearch.append("select ole from ");
            strSearch.append(userbase);
            strSearch.append("a00 where a0100='");
            strSearch.append(userid);
            strSearch.append("' and i9999=");
            strSearch.append(recid);
            strSearch.append(" FOR UPDATE");

            String fname = file.getFileName();
            int indexInt = fname.lastIndexOf(".");
            String ext = fname.substring(indexInt, fname.length());

            StringBuffer strInsert = new StringBuffer();
            strInsert.append("update  ");
            strInsert.append(userbase);
            strInsert.append("a00 set ole=EMPTY_BLOB() where a0100='");
            strInsert.append(userid);
            strInsert.append("' and i9999=");
            strInsert.append(recid);
            OracleBlobUtils blobutils = new OracleBlobUtils(this.conn);
            stream = file.getInputStream();
            if (isImageFile(ext)) {
                stream = ImageBO.imgStream(file, ext.substring(1)); // 复制图片，过滤掉木马程序
            }

            blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(), stream); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(stream);
        }
        return blob;
    }

    private void deleteDAO(String A0100, String userbase) throws GeneralException {
        StringBuffer deletesql = new StringBuffer();
        deletesql.append("delete from ");
        deletesql.append(userbase);
        deletesql.append("a00 where a0100='");
        deletesql.append(A0100);
        deletesql.append("' and flag='P'");
        try {
            new ExecuteSQL().execUpdate(deletesql.toString(), this.conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList getUploadFileList(String a0100, String nbase) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select title,ext,a0100,i9999 from ");
            sql.append(nbase + "A00 a");
            sql.append(" where UPPER(a.flag)='N' and a.a0100='" + a0100 + "' order by i9999");

            rs = dao.search(sql.toString());
            LazyDynaBean bean = null;
            int i = 1;
            while (rs.next()) {
                bean = new LazyDynaBean();
                bean.set("seq", i + "");
                bean.set("title", rs.getString("title") == null ? "" : rs.getString("title")
                        + rs.getString("ext"));

                bean.set("i9999", PubFunc.encrypt(rs.getString("i9999")));
                bean.set("a0100", PubFunc.encrypt(a0100));
                bean.set("nbase", PubFunc.encrypt(nbase));
                list.add(bean);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    public ArrayList getMediaSortList() {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            StringBuffer buf = new StringBuffer();
            buf.append("select id,flag,sortname from mediasort where dbflag=1");

            rs = dao.search(buf.toString());
            while (rs.next()) {
                list.add(new CommonData(rs.getString("flag"), rs.getString("sortname")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    public boolean getZ0336(String zp_pos_id, String hire_object) {
        boolean flag = false;
        RowSet rs = null;
        try {
            String sql = "select " + hire_object + " from z03 where z0301='" + zp_pos_id + "'";
            rs = dao.search(sql);
            while (rs.next()) {
                if (rs.getString(hire_object) != null && "03".equals(rs.getString(hire_object))) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }

    public boolean isBlackPerson(String blackField, String blackNbase, String blackValue) {
        boolean flag = false;
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("");
            sql.append("select 1 from " + blackNbase + "A01 ");
            sql.append("where UPPER(" + blackField + ") = ");
            sql.append("'" + blackValue.toUpperCase() + "'");
            rs = dao.search(sql.toString());
            if (rs.next()) {
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }

    public boolean isBlackPersonTheSameDB(String blackField, String blackNbase, String blackValue,
            String a0100) {
        boolean flag = false;
        RowSet rs = null;
        try {
            blackValue = PubFunc.getReplaceStr(blackValue);
            a0100 = PubFunc.getReplaceStr(a0100);
            StringBuffer sql = new StringBuffer("");
            sql.append("select * from " + blackNbase + "A01 ");
            sql.append("where UPPER(" + blackField + ") = ");
            sql.append("'" + blackValue.toUpperCase() + "' and a0100<>'" + a0100 + "'");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                flag = true;
                break;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }

    public String getStatus(String a0100, String dbName, String field, ContentDAO dao) {
        RowSet rs = null;
        String status = "";
        FieldItem fieldItem = DataDictionary.getFieldItem(field, "A01");
		if(fieldItem==null || !"1".equals(fieldItem.getUseflag())) {
            return "";
        }
        try {
            a0100 = PubFunc.getReplaceStr(a0100);
            String sql = "select " + field + " from " + dbName + "A01  where a0100='" + a0100 + "'";
            rs = dao.search(sql);
            while (rs.next()) {
                status = rs.getString(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return status;
    }

    public HashMap getRunHeaderList(String zpName) {
        HashMap map = new HashMap();
        RowSet rs = null;
        String hiremajor = "";
        try {
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
            HashMap mp = parameterXMLBo.getAttributeValues();
            if (mp != null && mp.get("hireMajor") != null) {
                hiremajor = (String) mp.get("hireMajor");
            }
            String field_str = SystemConfig.getPropertyValue("hire_run_field");
            String runTime = SystemConfig.getPropertyValue("hire_run_time");
            int day = 0;
            if (runTime != null && runTime.trim().length() > 0) {
                day = Integer.parseInt(runTime);
            }
            ArrayList list = new ArrayList();
            ArrayList dataList = new ArrayList();
            if (field_str.length() > 0) {
                HashMap setMap = new HashMap();
                HashMap itemidmap = new HashMap();
                StringBuffer select = new StringBuffer(" select zpt.z0336,");
                StringBuffer from = new StringBuffer(" from " + zpName + "A01 ");
                String[] arr = field_str.split(",");
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] == null || "".equals(arr[i])) {
                        continue;
                    }
                    String[] item_arr = arr[i].split("`");// {itemid,width,align,nowrap,format}
                    String itemid = "";
                    String width = "80";
                    String align = "center";
                    String nowrap = "1";// =1换行，=2不换行
                    String format = "yyyy-MM-dd";
                    itemid = item_arr[0];
                    String str = "";
                    if (item_arr.length >= 2) {
                        width = item_arr[1];
                        if (width.indexOf("%") != -1) {
                            str = width.substring(0, width.length() - 1);
                            str = (Float.parseFloat(str) / 100) + "";
                        }
                    }
                    if (item_arr.length >= 3) {
                        align = item_arr[2];
                    }
                    if (item_arr.length >= 4) {
                        nowrap = item_arr[3];
                    }
                    if (item_arr.length >= 5) {
                        format = item_arr[4];
                    }
                    FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
                    if (item != null && "1".equals(item.getUseflag())) {
                        String setid = item.getFieldsetid();
                        if (setid.charAt(0) != 'A' && setid.charAt(0) != 'a'
                                && !"Z03".equalsIgnoreCase(setid)) {
                            continue;
                        }
                        if (itemidmap.get(item.getItemid().toLowerCase()) != null) {
                            continue;
                        }
                        itemidmap.put(item.getItemid().toLowerCase(), "1");
                        if ("z03".equalsIgnoreCase(setid)) {
                            if ("z0336".equalsIgnoreCase(item.getItemid().trim())) {

                            } else {
                                select.append("zpt." + item.getItemid() + ",");
                            }
                        } else {
                            select.append(zpName + setid + "." + item.getItemid() + ",");
                        }
                        LazyDynaBean bean = new LazyDynaBean();
                        if ("z0311".equalsIgnoreCase(item.getItemid())) {
                            if (hiremajor == null || hiremajor.length() == 0) {
                                bean.set("itemid", item.getItemid());
                                bean.set("itemdesc", item.getItemdesc());
                            } else {
                                FieldItem item2 = DataDictionary.getFieldItem(hiremajor);
                                bean.set("itemid", item.getItemid());
                                bean.set("itemdesc", item.getItemdesc() + "(" + item2.getItemdesc()
                                        + ")");
                            }
                        } else {
                            bean.set("itemid", item.getItemid());
                            bean.set("itemdesc", item.getItemdesc());
                        }
                        bean.set("align", align);
                        bean.set("width", width);
                        bean.set("nowrap", "1".equals(nowrap) ? "" : "nowrap");
                        bean.set("format", format);
                        bean.set("codesetid", item.getCodesetid() == null ? "0" : item.getCodesetid());
                        bean.set("itemtype", item.getItemtype());
                        bean.set("deci", item.getDecimalwidth() + "");
                        bean.set("str", str);
                        list.add(bean);
                        if (setMap.get(setid.toLowerCase()) == null
                                && !"z03".equalsIgnoreCase(setid)) {
                            if (!"A01".equalsIgnoreCase(setid)) {
                                from.append(" left join (select * from " + zpName + setid
                                        + " a where a.i9999=(select max(i9999) from " + zpName
                                        + setid);
                                from.append(" b where a.a0100=b.a0100)) " + zpName + setid + " on "
                                        + zpName + "A01.a0100=" + zpName + setid + ".a0100 ");
                                setMap.put(setid.toLowerCase(), "1");
                            }
                        }

                    } else {
                        if ("apply_date".equalsIgnoreCase(itemid)) {
                            LazyDynaBean bean = new LazyDynaBean();
                            bean.set("itemid", itemid.toLowerCase());
                            bean.set("itemdesc", "应聘时间");
                            bean.set("align", align);
                            bean.set("width", width);
                            bean.set("nowrap", "1".equals(nowrap) ? "" : "nowrap");
                            bean.set("format", format);
                            bean.set("codesetid", "0");
                            bean.set("itemtype", "D");
                            bean.set("deci", "0");
                            bean.set("str", str);
                            list.add(bean);
                            select.append("zpt." + itemid + ",");
                        } else if ("thenumber".equalsIgnoreCase(itemid)) {
                            LazyDynaBean bean = new LazyDynaBean();
                            bean.set("itemid", itemid.toLowerCase());
                            bean.set("itemdesc", "志愿");
                            bean.set("align", align);
                            bean.set("width", width);
                            bean.set("nowrap", "1".equals(nowrap) ? "" : "nowrap");
                            bean.set("format", format);
                            bean.set("codesetid", "0");
                            bean.set("itemtype", "N");
                            bean.set("deci", "0");
                            bean.set("str", str);
                            list.add(bean);
                            select.append("zpt." + itemid + ",");
                        } else if ("resume_flag".equalsIgnoreCase(itemid)) {
                            LazyDynaBean bean = new LazyDynaBean();
                            bean.set("itemid", itemid.toLowerCase());
                            bean.set("itemdesc", "简历状态");
                            bean.set("align", align);
                            bean.set("width", width);
                            bean.set("nowrap", "1".equals(nowrap) ? "" : "nowrap");
                            bean.set("format", format);
                            bean.set("codesetid", "36");
                            bean.set("itemtype", "A");
                            bean.set("deci", "0");
                            bean.set("str", str);
                            list.add(bean);
                            select.append("zpt." + itemid + ",");
                        }
                        if (itemidmap.get(itemid.toLowerCase()) != null) {
                            continue;
                        }
                        itemidmap.put(itemid.toLowerCase(), "1");
                    }
                }
                from.append(" left join (select Z03.*,zp.* from zp_pos_tache zp left join Z03 on zp.ZP_POS_ID=Z03.z0301) zpt on "
                                + zpName + "A01.a0100=zpt.A0100");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (day != 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - day);
                    String xx = dateFormat.format(calendar.getTime());
                    from.append(" where (" + Sql_switcher.year(zpName + "A01.createtime") + ">"
                            + xx.substring(0, 4) + ") or ("
                            + Sql_switcher.year(zpName + "A01.createtime") + "="
                            + xx.substring(0, 4));
                    from.append(" and " + Sql_switcher.month(zpName + "A01.createtime") + ">"
                            + xx.substring(5, 7) + ") or (");
                    from.append(Sql_switcher.year(zpName + "A01.createtime") + "="
                            + xx.substring(0, 4) + " and "
                            + Sql_switcher.month(zpName + "A01.createtime") + "="
                            + xx.substring(5, 7) + " and ");
                    from.append(Sql_switcher.day(zpName + "A01.createtime") + ">="
                            + xx.substring(8, 10) + ") order by "
                            + Sql_switcher.dateToChar(zpName + "A01.createtime", "YYYY-MM-DD")
                            + " desc");
                }

                FieldItem item1 = DataDictionary.getFieldItem(hiremajor);
                if (item1 != null && item1.getItemid().toString().length() != 0) {
                    select.append("zpt." + item1.getItemid() + ",");
                }
                select.setLength(select.length() - 1);
                rs = dao.search(select.toString() + from.toString());

                while (rs.next()) {
                    String channel = rs.getString("z0336") == null ? "" : rs.getString("z0336");
                    LazyDynaBean bean = new LazyDynaBean();
                    for (int i = 0; i < list.size(); i++) {
                        LazyDynaBean item = (LazyDynaBean) list.get(i);
                        String itemid = (String) item.get("itemid");
                        String codesetid = (String) item.get("codesetid");
                        String format = (String) item.get("format");
                        String itemtype = (String) item.get("itemtype");
                        String deci = (String) item.get("deci");
                        String value = "";
                        String aformat = "yyyy-MM-dd";
                        if ("a0101".equalsIgnoreCase(itemid)) {
                            String avalue = rs.getString(itemid) == null ? "" : rs.getString(itemid);
                            if (avalue.length() > 0) {
                                value = avalue.substring(0, 1) + "XX";
                            } else {
                                value = "";
                            }
                        } else if ("apply_date".equalsIgnoreCase(itemid)) {
                            if (rs.getDate(itemid) != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat(aformat);
                                if (format != null && format.length() > 0) {
                                    try {
                                        sdf = new SimpleDateFormat(format);
                                    } catch (Exception e) {
                                        sdf = new SimpleDateFormat(aformat);
                                    }
                                }
                                value = sdf.format(rs.getDate(itemid));
                            }
                        } else if ("thenumber".equalsIgnoreCase(itemid)) {
                            if (rs.getString(itemid) != null) {
                                value = rs.getString(itemid);
                            }
                        } else if ("resume_flag".equalsIgnoreCase(itemid)) {
                            if (rs.getString(itemid) != null) {
                                value = AdminCode.getCodeName(codesetid, rs.getString(itemid));
                            }
                        } else if ("A".equalsIgnoreCase(itemtype)) {
                            if ("z0311".equalsIgnoreCase(itemid)) {
                                if (channel != null) {
                                    if ("01".equalsIgnoreCase(channel)) {
                                        if (hiremajor == null || hiremajor.trim().length() == 0) {
                                            value = rs.getString(itemid) == null ? "" : rs.getString(itemid);
                                            if (codesetid != null && !"0".equals(codesetid)) {
                                                value = AdminCode.getCodeName(codesetid, value);
                                            }
                                        } else {
                                            value = rs.getString(item1.getItemid()) == null ? ""
                                                    : rs.getString(item1.getItemid());
                                            if (item1.isCode()) {
                                                value = AdminCode.getCodeName(item1.getCodesetid(),
                                                        value);
                                            }
                                        }
                                    } else {
                                        value = rs.getString(itemid) == null ? "" : rs.getString(itemid);
                                        if (codesetid != null && !"0".equals(codesetid)) {
                                            value = AdminCode.getCodeName(codesetid, value);
                                        }
                                    }
                                }
                            } else {
                                value = rs.getString(itemid) == null ? "" : rs.getString(itemid);
                                if (codesetid != null && !"0".equals(codesetid)) {
                                    value = AdminCode.getCodeName(codesetid, value);
                                }
                            }
                        } else if ("N".equalsIgnoreCase(itemtype)) {
                            value = rs.getString(itemid) == null ? "0" : rs.getString(itemid);
                            value = PubFunc.round(value, Integer.parseInt(deci));
                        } else if ("D".equalsIgnoreCase(itemtype)) {
                            if (rs.getDate(itemid) == null) {
                                value = "";
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat(aformat);
                                if (format != null && format.length() > 0) {
                                    try {
                                        sdf = new SimpleDateFormat(format);
                                    } catch (Exception e) {
                                        sdf = new SimpleDateFormat(aformat);
                                    }
                                }
                                value = sdf.format(rs.getDate(itemid));
                            }
                        } else if ("M".equalsIgnoreCase(itemtype)) {
                            value = Sql_switcher.readMemo(rs, itemid);
                        } else {
                            value = rs.getString(itemid) == null ? "" : rs.getString(itemid);
                        }
                        bean.set(itemid.toLowerCase(), value.trim());
                    }
                    dataList.add(bean);
                }
                map.put("list", list);
                map.put("dataList", dataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
	 * 获取左侧机构树sql，unitOrDepart=2的时候为显示单位和部门
	 * @param employObject
	 * @param hire_object招聘渠道
	 * @param id
	 * @param codesetid
	 * @param itemid
	 * @param unitOrDepart
	 * @return
	 */
	private StringBuffer getSelectSql(String employObject, String hire_object, String id,String codesetid,String itemid, String unitOrDepart) {
		StringBuffer buf = new StringBuffer();
		buf.append(" select distinct codesetid,codeitemid,codeitemdesc,grade,a0000 ");
		buf.append(" from z03,organization o ");
		buf.append("where "+itemid+"=o.codeitemid and o.codesetid='"+codesetid+"' and z03.z0319='04' ");
		buf.append(getDateSql(">=", "Z0329"));
		buf.append(getDateSql("<=", "Z0331"));
		if ("out".equals(employObject)) {
		    buf.append(" and ( z03.z0336<'03' or z03.z0336>'03' or z03."
		            + hire_object + " is null )");
		} else if ("no".equalsIgnoreCase(employObject)) {
		    buf.append(" and 1=2 ");
		} else {
		    buf.append(" and z03.z0336='" + employObject + "' ");
		}
		buf.append(" and o.codeitemid like '" + id + "%'");
		if("2".equals(unitOrDepart)){
			buf.append(" union ");
			buf.append(getSelectSql(employObject, hire_object, id, "UM", "z0325", ""));
		}

		return buf;
	}

	public String getcorcode(String a0100) {
        String code = "";
        StringBuffer sql = new StringBuffer();
        sql.append("select * from zp_pos_tache where a0100='");
        sql.append(a0100);
        sql.append("' order by thenumber asc");
        RowSet rs = null;
        String z0301 = "";
        ArrayList list = new ArrayList();
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {

                String thenumber = rs.getString("thenumber");
                if ("1".equalsIgnoreCase(thenumber)) {
                    z0301 = rs.getString("zp_pos_id");
                } else {
                    list.add(rs.getString("zp_pos_id"));
                }
            }
            if (z0301 == null || z0301.trim().length() == 0) {
                if (list != null && list.size() != 0) {
                    z0301 = (String) list.get(0);
                } else {
                    code = "false";
                }
            }
            FieldItem z0311 = DataDictionary.getFieldItem("z0311","Z03");//判断z0311 是否构库
            if ((code == null || code.trim().length() == 0 || "false".equalsIgnoreCase(code))
            		&&z0311!=null&&"1".equals(z0311.getUseflag())) {// corcode 外部职位，如果z0311 需求岗位不存在，就不走这一段
                sql.setLength(0);
                sql.append("select org.corcode,org.codeitemdesc from z03 left join organization org on org.codeitemid=z03.z0311 where z03.z0301='");
                sql.append(z0301);
                sql.append("'");
                rs = dao.search(sql.toString());
                String jobname = "";
                String corcode = "";
                if (rs.next()) {
                    jobname = rs.getString("codeitemdesc");
                    corcode = rs.getString("corcode");
                }
                code = jobname + "&" + corcode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return code;
    }

	public int getI9999(String setid, String a0100, String dbname) {
        int i = 0;
        RowSet rs = null;
        try {
            rs = dao.search("select max(i9999) from " + dbname + setid + " where a0100='" + a0100
                    + "'");
            while (rs.next()) {
                i = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return i;
    }

    /**
     * 存放常见文件头信息
     */
    private static void getAllFileType() {
        FILE_TYPE_MAP.put("jpg", "FFD8FF"); // JPEG (jpg)
        FILE_TYPE_MAP.put("png", "89504E47"); // PNG (png)
        FILE_TYPE_MAP.put("gif", "47494638"); // GIF (gif)
        FILE_TYPE_MAP.put("tif", "49492A00"); // TIFF (tif)
        FILE_TYPE_MAP.put("bmp", "424D"); // Windows Bitmap (bmp)
        FILE_TYPE_MAP.put("dwg", "41433130"); // CAD (dwg)
        FILE_TYPE_MAP.put("html", "68746D6C3E"); // HTML (html)
        FILE_TYPE_MAP.put("rtf", "7B5C727466"); // Rich Text Format (rtf)
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        // FILE_TYPE_MAP.put("zip", "504B0304");
        FILE_TYPE_MAP.put("rar", "52617221");
        FILE_TYPE_MAP.put("psd", "38425053"); // Photoshop (psd)
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A"); // Email
                                                                  // [thorough
                                                                  // only] (eml)
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F"); // Outlook Express (dbx)
        FILE_TYPE_MAP.put("pst", "2142444E"); // Outlook (pst)
        // FILE_TYPE_MAP.put("xls", "D0CF11E0"); //MS Word
        // FILE_TYPE_MAP.put("doc", "D0CF11E0"); //MS Excel 注意：word 和
        // excel的文件头一样
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A"); // MS Access (mdb)
        FILE_TYPE_MAP.put("wpd", "FF575043"); // WordPerfect (wpd)
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("pdf", "255044462D312E"); // Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F"); // Quicken (qdf)
        FILE_TYPE_MAP.put("pwl", "E3828596"); // Windows Password (pwl)
        FILE_TYPE_MAP.put("wav", "57415645"); // Wave (wav)
        FILE_TYPE_MAP.put("avi", "41564920");
        FILE_TYPE_MAP.put("ram", "2E7261FD"); // Real Audio (ram)
        FILE_TYPE_MAP.put("rm", "2E524D46"); // Real Media (rm)
        FILE_TYPE_MAP.put("mpg", "000001BA"); //
        FILE_TYPE_MAP.put("mov", "6D6F6F76"); // Quicktime (mov)
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11"); // Windows Media (asf)
        FILE_TYPE_MAP.put("mid", "4D546864"); // MIDI (mid)
    }

    /**
     * 获取文件类型
     *
     * @param file
     * @return
     */
    public static String getFileTypeByHead(InputStream inputStream) {
        if (FILE_TYPE_MAP.isEmpty()) {
            getAllFileType();
        }
        String fileType = null;
        byte[] b = new byte[20];
        try {
            inputStream.read(b);
            fileType = getFileTypeByStream(b);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace(); // 错误信息
        }
        return fileType;
    }

    /* 由于每一种文件的类型都不一致，所以这个用来判断文件类型。 */
    private static String getFileTypeByStream(byte[] b) {
        String filetypeHex = String.valueOf(getFileHexString(b));
        Iterator it = FILE_TYPE_MAP.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String fileTypeHexValue = (String) entry.getValue();
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    private static String getFileHexString(byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        if (b == null || b.length <= 0) {
            return null;
        }
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @Title: getRealPosName
     * @Description: 获得前台显示已应聘岗位的信息的真实显示
     * @param temp_un_posName
     * @param temp_um_posName
     * @param temp_all_posName
     * @param posName
     * @return
     * @throws UnsupportedEncodingException
     *             String
     * @throws
     */
    public String getRealPosName(String temp_un_posName, String temp_um_posName,
            String temp_all_posName, String posName) throws UnsupportedEncodingException {
        String returnName = "";
        if (this.getLength(temp_un_posName + temp_um_posName + temp_all_posName)) {// 单位+部门+岗位
            returnName = posName;
        } else if (this.getLength(temp_um_posName + temp_all_posName)) {// 部门+岗位
            posName = temp_un_posName.length() > 0 ? temp_un_posName + "/<br>" : "";
            posName = posName + (temp_um_posName.length() > 0 ? temp_um_posName + "/" : "")
                    + temp_all_posName;
            returnName = posName;
        } else if (this.getLength(temp_un_posName + temp_um_posName)) {// 单位+部门
            posName = (temp_un_posName.length() > 0 ? temp_un_posName + "/" : "")
                    + (temp_um_posName.length() > 0 ? temp_um_posName + "/<br>" : "");
            posName = posName + temp_all_posName;
            returnName = posName;
        } else {
            posName = temp_un_posName.length() > 0 ? temp_un_posName + "/<br>" : "";
            posName = posName + (temp_um_posName.length() > 0 ? temp_um_posName + "/<br>" : "");
            posName = posName + temp_all_posName;
            returnName = posName;
        }
        return returnName;
    }

    public boolean getLength(String name) throws UnsupportedEncodingException {
        int length = name.getBytes("GBK").length;
        if (length > 42) {
            return false;
        }
        return true;
    }

    /**
     *
     * @Title: getPostFieldSort
     * @Description: 获得排序方式的sql
     * @param map
     *            全局的招聘参数的配置
     * @param tableBuffer
     *            用于排序关联的table别名 例如organization T3 多了以后是 organization T4
     * @param whereBuffer
     *            用于排序时做关联用的where语句
     * @return String 返回ordersql语句
     * @throws
     */
    public String getPostFieldSort(HashMap map, StringBuffer tableBuffer, StringBuffer whereBuffer) {
        String pos_listfield_sort = "";
        String postitemid = "";
        String postitemsort = "";
        StringBuffer orderSql = new StringBuffer();
        String tempString = "";
        if (map.get("pos_listfield_sort") != null
                && ((String) map.get("pos_listfield_sort")).length() > 0) {
            pos_listfield_sort = (String) map.get("pos_listfield_sort");
        }
        if (pos_listfield_sort.length() > 0) {
            if (pos_listfield_sort.indexOf(",") == -1) {
                String[] tempArray = pos_listfield_sort.split(":");
                postitemid = tempArray[0].trim();
                postitemsort = tempArray[1].trim();
                FieldItem item = DataDictionary.getFieldItem(postitemid.toLowerCase());
                if (item == null || "0".equals(item.getUseflag())) {

                } else {
                    String codesetid = item.getFieldsetid();
                    if (codesetid != null
                            && ("UN".equalsIgnoreCase(codesetid)
                                    || "UM".equalsIgnoreCase(codesetid)
                                    || "@K".equalsIgnoreCase(codesetid))) {
                        tableBuffer.append(",organization T3 ");
                        whereBuffer.append(" and Z03." + postitemid + "==T3.codeitemid ");
                        orderSql.append(" order by z03." + postitemid + " "
                                + postitemsort + ",T3.a0000");
                    } else {
                        orderSql.append(" order by z03." + postitemid + " "+ postitemsort+",organization.a0000");
                    }
                }
            } else {
                String[] postSortArray = pos_listfield_sort.split(",");
                int sortTableI = 3;
                boolean z0301flag = true;
                for (int i = 0; i < postSortArray.length; i++) {
                    String[] tempArray = postSortArray[i].split(":");
                    postitemid = tempArray[0].trim();
                    if("z0301".equalsIgnoreCase(postitemid)) {
                        z0301flag = false;
                    }
                    postitemsort = tempArray[1].trim();
                    FieldItem item = DataDictionary.getFieldItem(postitemid.toLowerCase());
                    if (item == null || "0".equals(item.getUseflag())) {

                    } else {
                        String codesetid = item.getFieldsetid();
                        if (codesetid != null
                                && ("UN".equalsIgnoreCase(codesetid)
                                        || "UM".equalsIgnoreCase(codesetid)
                                        || "@K".equalsIgnoreCase(codesetid))) {
                            tableBuffer.append(",organization T " + sortTableI);
                            whereBuffer.append(" and Z03." + postitemid + "==T" + sortTableI
                                    + ".codeitemid ");
                            tempString = tempString + ",z03." + postitemid + " " + postitemsort
                                    + ",T" + sortTableI + ".a0000";
                            sortTableI++;
                        } else {
                            tempString = tempString + ",z03." + postitemid + " " + postitemsort;
                        }

                    }
                }
                if (tempString.length() > 0) {
                	if(tempString.trim().indexOf(",")==0) {
                        tempString = tempString.substring(1);
                    }
                    orderSql.append(" order by " + tempString +",organization.a0000");
                }
                if(z0301flag) {
                    orderSql.append(" ,z03.Z0301 ASC");
                }
            }
        }
        return orderSql.toString();
    }

    /**
     * @Title: getQuerysql
     * @Description: 查询语句的sql组成的buffer
     * @param a01FiledItemMap
     *            a01FiledItemMap key:字段itemid value:1#0 #前的数字代表是否是 前台显示指标 1：是
     *            0：不是 #后数字是否是必填指标 1：是 0不是
     * @param columnList
     *            表头list
     * @param dbName
     *            招聘人才库
     * @param phoneItemid
     *            系统中配置的移动电话指标
     * @return StringBuffer 查询语句的sql组成的buffer
     * @throws
     */
    public StringBuffer getQuerysql(HashMap a01FiledItemMap, String dbName, ArrayList columnList,
            String phoneItemid, String headHunterNames) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("select ");
        queryBuffer.append("A0101,a01.A0100");// 添加必须显示的字段
        columnList.add(getLazyBean("A0101", ResourceFactory.getProperty("label.title.name")));
        FieldItem item = DataDictionary.getFieldItem("A0107", "A01");
        if("1".equals(item.getUseflag())){
	        queryBuffer.append(",A0107");
	        columnList.add(getLazyBean("A0107", ResourceFactory.getProperty("hire.out.headhunter.rolse.sex")));
        }
        String agePropertys = (String) a01FiledItemMap.get("c0101");// 获得年龄是否勾选
        if (agePropertys != null) {
            String[] agePropertArray = agePropertys.split("#");
            if ("1".equals(agePropertArray[0])) {
                queryBuffer.append(",C0101");// 添加必须显示的字段
                columnList.add(getLazyBean("C0101", ResourceFactory.getProperty("kq.wizard.age")));
            }
        }
        String phonePropertys = (String) a01FiledItemMap.get("c0104");// 获得移动电话是否勾选
        if (phonePropertys != null) {
            String[] phonePropertyArray = agePropertys.split("#");
            if ("1".equals(phonePropertyArray[0]) && !"".equals(phoneItemid)
                    && !"#".equals(phoneItemid)) {
                queryBuffer.append("," + phoneItemid + " ");
                columnList.add(getLazyBean("C0104", ResourceFactory.getProperty("selfservice.param.otherparam.phone_title")));
            }
        }
        queryBuffer.append(",zp.num ");
        columnList.add(getLazyBean("recommendPositions", ResourceFactory.getProperty("hire.our.headhunter.recommendPositions")));
        columnList.add(getLazyBean("operation", ResourceFactory.getProperty("reportcyclelist.option")));
        queryBuffer.append(" from "
                        + dbName
                        + "A01 a01 left join (select count(*) num,A0100,recusername from zp_pos_tache where recusername in "
                        + headHunterNames
                        + " group by recusername,A0100) zp  on a01.A0100=zp.A0100");
        queryBuffer.append(" where CreateUserName in " + headHunterNames);
        return queryBuffer;
    }

    /**
     * 判断当前登录用户是否是主账号,如果是主账号则返回所属机构下所有的账号用户名,否则返回当前登录账号
     *
     * @param headHunterName
     * @return
     */
    public String isLeader(String headHunterName) {
        String sql = "select * from zp_headhunter_login where username='" + headHunterName + "'";
        ResultSet rs = null;
        String res = "";
        String isleader = "";
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                isleader = rs.getString("isleader");
                // 是主账号，则返回该组织下所有猎头账号
                if (isleader != null && "1".equals(isleader)) {
                    res = this.getAllUsername(rs.getString("Z6000"), dao);
                } else {
                    res = "('" + headHunterName + "')";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 返回主账号所属机构下所有用户名
     *
     * @param Z6000
     * @param dao
     * @return
     */
    public String getAllUsername(String Z6000, ContentDAO dao) {
        String sql = "select username from zp_headhunter_login where Z6000='" + Z6000 + "'";
        StringBuffer res = new StringBuffer("(");
        ResultSet rs = null;
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                res.append("'" + rs.getString("username") + "',");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        // 没有查询结果
        if (res.length() == 1) {
            res.setLength(0);
        } else {
            res.setLength(res.length() - 1);
            res.append(")");
        }
        return res.toString();
    }

    /**
     * @Title: getLazyBean
     * @Description: 构建前台展示页面的bean
     * @param itemid
     * @param itemdesc
     * @return bean对象
     * @throws
     */

    private Object getLazyBean(String itemid, String itemdesc) {
        LazyDynaBean bean = new LazyDynaBean();
        bean.set("itemid", itemid);
        bean.set("itemdesc", itemdesc);
        return bean;
    }

    /**
     *
     * @Title: getResumesUser
     * @Description: TODO得到猎头已经填写的简历
     * @param headHunterName
     *            登录猎头的用户名
     * @param queryBuffer
     *            查询语句
     * @param columnList
     *            查询的各个列对象
     * @param phoneItemid
     *            移动信箱字段
     * @return ArrayList recommendUser 存放简历的基本信息
     * @throws
     */

    public ArrayList getResumesUser(StringBuffer queryBuffer, ArrayList columnList,
            String phoneItemid) {
        RowSet rowSet = null;
        ArrayList recommendUser = new ArrayList();
        try {
            rowSet = dao.search(queryBuffer.toString());

            while (rowSet.next()) {
                String a0100 = rowSet.getString("a0100");
                if (this.isRz(a0100))// 当前人员如果有已入职记录则不添加该人员
                {
                    continue;
                }
                a0100 = PubFunc.encrypt(a0100);// 所有被猎头录入的人员的a0100都是进行加密的
                LazyDynaBean valueBean = new LazyDynaBean();
                for (int i = 0; i < columnList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) columnList.get(i);
                    String itemid = (String) bean.get("itemid");
                    String tempid = itemid;
                    if ("recommendPositions".equals(itemid)) {
                        tempid = "num";
                    }
                    if ("C0104".equals(itemid)) {
                        tempid = phoneItemid;
                    }
                    if ("operation".equals(itemid)) {
                        continue;
                    }
                    String value = rowSet.getString(tempid);
                    if (value == null && "num".equals(tempid)) {// 特殊处理一下推荐人数
                        value = "0";
                    } else if (value == null) {
                        value = "";
                    }// 性别是代码字段
                    if ("A0107".equals(tempid)) {
                        FieldItem item = DataDictionary.getFieldItem("A0107");
                        if (item != null) {
                            String codeSetid = item.getCodesetid();
                            if (codeSetid != null && codeSetid.trim().length() > 0
                                    && !"0".equals(codeSetid)) {
                                CodeItem codeItem = AdminCode.getCode(codeSetid, value);
                                if (codeItem != null) {
                                    value = codeItem.getCodename();
                                }
                            }
                        }
                    }

                    valueBean.set(itemid, value);
                }

                valueBean.set("a0100", a0100);
                recommendUser.add(valueBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return recommendUser;
    }

    /***
     * 判断当前人员是否有已入职的记录
     *
     * @Title:isRz
     * @Description：
     * @author xiexd
     * @param a0100
     * @return
     */
    private boolean isRz(String a0100) {
        boolean flag = false;
        RowSet rs = null;
        try {
            String dbName = this.getZpkdbName();
            StringBuffer sql = new StringBuffer(
                    "select resume_flag from zp_pos_tache  where a0100=? and nbase=?");
            ArrayList values = new ArrayList();
            values.add(a0100);
            values.add(dbName);
            rs = dao.search(sql.toString(), values);
            while (rs.next()) {
                String resume = rs.getString("resume_flag");
                if ("0903".equals(resume)) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }

    /**
     * @Title: getDeleteInformation
     * @Description: 获得推荐简历不可删除的信息
     * @param queryBuffer
     *            查询语句
     * @param nameMap
     *            当前被选中的所有人员姓名
     * @param valueList
     *            查询语句的所有参数值
     * @return String
     * @throws
     */

    public String getDeleteInformation(StringBuffer queryBuffer, HashMap nameMap,
            ArrayList valueList) {
        RowSet rs = null;
        StringBuffer informationBuffer = new StringBuffer();
        String information = "";
        try {
            rs = dao.search(queryBuffer.toString(), valueList);
            while (rs.next()) {
                String a0100 = rs.getString("a0100");
                String userName = (String) nameMap.get(a0100);
                if (userName == null) {
                    continue;
                }
                informationBuffer.append(userName + ",");
            }
            information = informationBuffer.toString();
            if (information.trim().length() > 0) {
                information = information.substring(0, information.length() - 1);
                information = information
                        + ResourceFactory.getProperty("hire.out.headhunter.cannot.delete");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return information;
    }

    /**
     * @Title: deleteResume
     * @Description: 猎头招聘删除简历
     * @param fieldSetList
     *            ：涉及到的子集list
     * @param dbName
     *            ：招聘人才库
     * @param selectedList
     *            被选中的人员
     * @throws
     */

    public void deleteResume(ArrayList fieldSetList, String dbName, ArrayList selectedList) {
        RowSet rs = null;
        try {
            StringBuffer a0100s = new StringBuffer();

            ArrayList valueList = new ArrayList();
            for (int i = 0; i < selectedList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) selectedList.get(i);
                String a0100 = (String) bean.get("a0100");
                valueList.add(PubFunc.decrypt(a0100));
                if (i == selectedList.size() - 1) {
                    a0100s.append("?");
                } else {
                    a0100s.append("?,");
                }
            }

            ArrayList sqlList = new ArrayList();// 存放sql
            /** 删除各个子集里面的信息 **/
            for (int i = 0; i < fieldSetList.size(); i++) {
                StringBuffer deletesql = new StringBuffer();
                deletesql.append("delete from ");
                LazyDynaBean bean = (LazyDynaBean) fieldSetList.get(i);
                String setName = (String) bean.get("fieldSetId");
                deletesql.append(dbName + setName + " where a0100 in(");
                deletesql.append(a0100s.toString());
                deletesql.append(")");
                sqlList.add(deletesql.toString());
            }
            // 修改对应职位的新简历数,所有简历数
            String selectZ0301 = "select zp_pos_id from zp_pos_tache where a0100 in("
                    + a0100s.toString() + ")";
            PositionBo pobo = new PositionBo(conn, dao, null);
            rs = dao.search(selectZ0301, valueList);
            ArrayList z0301List = new ArrayList();
            while (rs.next()) {
                String zpid = rs.getString("zp_pos_id");
                if (zpid != null && zpid.trim().length() > 0) {
                    z0301List.add(zpid); // 在最后删除后给这些职位做修改简历数等
                }
            }

            /** 删除应聘简历的相关信息 **/
            String deletesql = "delete from zp_pos_tache where a0100 in(";
            deletesql = deletesql + a0100s.toString() + ")";
            sqlList.add(deletesql);

            /** 删除多媒体的相关信息 **/
            deletesql = "delete from " + dbName + "A00 where a0100 in(";
            deletesql = deletesql + a0100s.toString() + ")";
            sqlList.add(deletesql);

            for (int i = 0; i < sqlList.size(); i++) {
                String sql = (String) sqlList.get(i);
                dao.update(sql, valueList);
            }

            for (int i = 0; i < z0301List.size(); i++) {
                pobo.saveCandiatesNumber((String) z0301List.get(i), 1);
                pobo.saveCandiatesNumber((String) z0301List.get(i), 3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * @return
     *
     * @Title: validationRecommend
     * @Description: TODO验证选中人员是否具有能申请的能力
     * @param a0100Array
     *            选中的a0100
     * @param userNameArray
     *            选中人员的姓名
     * @param max_count
     *            最大申请职位数
     * @return void
     * @throws
     */

    public String validationRecommend(String[] a0100Array, String[] userNameArray, String max_count) {
        RowSet rs = null;
        String infor = "";
        int count = Integer.parseInt(max_count);
        try {
            ArrayList values = new ArrayList();// 存放a0100的参数值
            HashMap userNameMap = new HashMap();// 存放a0100所对应的用户名
            StringBuffer a0100Buffer = new StringBuffer();
            for (int i = 0; i < a0100Array.length; i++) {
                String a0100 = PubFunc.decrypt(a0100Array[i]);
                if (i == a0100Array.length - 1) {
                    a0100Buffer.append("?");
                } else {
                    a0100Buffer.append("?,");
                }
                userNameMap.put(a0100, userNameArray[i]);
                values.add(a0100);
            }
            String querySql = "select count(*)num,a0100 from zp_pos_tache where A0100 in(";
            querySql = querySql + a0100Buffer.toString();
            querySql = querySql + ") group by A0100";
            rs = dao.search(querySql, values);
            while (rs.next()) {
                int num = rs.getInt("num");
                String a0100 = rs.getString("a0100");
                if (num > count) {
                    String userName = (String) userNameMap.get(a0100);
                    infor = infor + userName + ",";
                }
            }
            if (infor.trim().length() > 0) {
                infor = infor + ResourceFactory.getProperty("hire.out.headhunter.applyed.tomore");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return infor;
    }

    /**
     * @Title: bacthInsetPosition
     * @Description: TODO批量为多个人员推荐职位
     * @param a0100List
     *            要更新的人员的A0100组成的list
     * @param z0301
     *            要推荐的职位
     * @param thenumMap
     *            对应人员的支援map
     * @param status
     *            状态置为 0 未通过
     * @param dbName
     *            招聘人才库
     * @param recusername
     *            推荐人
     * @throws
     */

    public void bacthInsetPosition(ArrayList a0100List, String z0301, HashMap thenumMap,
            String status, String dbName, String recusername) {
        try {
        	Date date = new Date();
            for (int i = 0; i < a0100List.size(); i++) {
                ArrayList values = new ArrayList();// sql的value
                StringBuffer insetBuffer = new StringBuffer();
                insetBuffer.append("insert into zp_pos_tache (A0100,Zp_pos_id,Thenumber,status,Nbase,recusername,recdate,apply_date,relation_type) values(?,?,?,?,?,?,?,?,2)");
                String a0100 = (String) a0100List.get(i);
                values.add(a0100);
                values.add(z0301);
                values.add(thenumMap.get(a0100));
                values.add(status);
                values.add(dbName);
                values.add(recusername);
                values.add(new Timestamp(date.getTime()));
                values.add(new Timestamp(date.getTime()));
                dao.insert(insetBuffer.toString(), values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据猎头登录名获取猎头所属部门编码
     *
     * @param username
     * @return
     */
    public String getHeadhunterUnitCode(String username) {
        String sql = "select * from zp_headhunter_login us,z60 cm where us.z6000=cm.z6000 and us.username='"
                + username + "'";
        String res = "";

        ResultSet rs = null;
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                res = rs.getString("z6005");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 是否允许查询成绩
     *
     * @Title: canQueryScore
     * @Description: 判断应聘者当前是否可以查看考试成绩 1、当前时间是否处于应聘者申请的职位对应的招聘批次的成绩查看时间范围内
     *               查看时间范围：如果查看结束时间没有设，那么从查看开始时间起均可查看；如果查看开始时间没有设，那么不允许查看。
     * @param nbase
     * @param a0100
     * @return
     */
    public boolean canQueryScore(String nbase, String a0100) {
        boolean canQueryScore = false;

        if (StringUtils.isEmpty(nbase) || StringUtils.isEmpty(a0100)) {
            return canQueryScore;
        }

        String tabid = this.getScoreTabId();
        //没设置成绩登记表
        if (tabid == null || tabid.length() == 0) {
            return canQueryScore;
        }

        //1、获取当前人员应聘的职位和所处的流程
        StringBuffer sql = new StringBuffer();
        sql.append("select ZP_POS_ID,link_id from zp_pos_tache ");
        sql.append(" where a0100=? and nbase=?");
        //当前申请所在流程环节对应的招聘流程是包含“笔试”环节的
        sql.append(" and link_id in (select id from zp_flow_links");
        sql.append("  where exists(select 1 from zp_flow_links where node_id='03' and valid=1)");
        sql.append(")");

        ArrayList<String> sqlParams = new ArrayList<String>();
        sqlParams.add(a0100);
        sqlParams.add(nbase);

        ResultSet posRs = null;
        ResultSet flowRs = null;
        ResultSet curLinkSeqRs = null;
        ResultSet examLinkSeqRs = null;
        int curLinkSeq = -1;
        int examLinkSeq = -1;
        String curLink = "";
        String batchIds = "";
        try {
            posRs = dao.search(sql.toString(), sqlParams);
            while (posRs.next()) {
                curLink = posRs.getString("link_id");
                if (StringUtils.isBlank(curLink)) {
                    continue;
                }

                //2、获取职位对应的招聘流程
                sql.setLength(0);
                sql.append("select z0381,z0101 from z03 where Z0301='").append(posRs.getString("zp_pos_id")).append("'");
                flowRs = dao.search(sql.toString());
                if (flowRs.next()) {
                    //3、获取流程的笔试环节的顺序号
                    sql.setLength(0);
                    sql.append("select seq from zp_flow_links");
                    sql.append(" where flow_id='").append(flowRs.getString("z0381")).append("'");
                    sql.append(" and node_id='03'");
                    sql.append(" order by seq");
                    examLinkSeqRs = dao.search(sql.toString());
                    // 没笔试环节，退出，看下一个职位
                    if (!examLinkSeqRs.next()) {
                        continue;
                    }

                    examLinkSeq = examLinkSeqRs.getInt("seq");

                    //4、当前所在环节的顺序号
                    sql.setLength(0);
                    sql.append("select seq from zp_flow_links");
                    sql.append(" where id='").append(curLink).append("'");
                    curLinkSeqRs = dao.search(sql.toString());
                    if (!curLinkSeqRs.next()) {
                        continue;
                    }

                    curLinkSeq = curLinkSeqRs.getInt("seq");

                    // 当前所在环节在笔试或笔试之后，是需要判断是否能查看成绩的
                    if (curLinkSeq >= examLinkSeq) {
                        if (StringUtils.isNotBlank(batchIds)) {
                            batchIds = batchIds + ",";
                        }
                        batchIds = batchIds + "'" + flowRs.getString("z0101") + "'";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(posRs);
            PubFunc.closeDbObj(flowRs);
            PubFunc.closeDbObj(curLinkSeqRs);
            PubFunc.closeDbObj(examLinkSeqRs);
        }

        if (StringUtils.isBlank(batchIds)) {
            return  canQueryScore;
        }

		//先获取到查看成绩开始日期和查看成绩结束日期指标
	    FieldItem z0163Item = DataDictionary.getFieldItem("z0163");//查看成绩开始日期
	    FieldItem z0165Item = DataDictionary.getFieldItem("z0165");//查看成绩结束日期

        //判断批次是否处于成绩查看时间段
        sql.setLength(0);
        sql.append("SELECT 1 FROM Z01");
        sql.append(" WHERE Z0163 IS NOT NULL ");
        if(Sql_switcher.searchDbServer()==Constant.MSSQL){
        	//转换格式化开始日期
        	sql.append(" and convert(DATETIME,convert(char(").append(z0163Item.getItemlength()).append("),Z0163,120),120)<=");
        	sql.append(" convert(DATETIME,convert(char(").append(z0163Item.getItemlength()).append("),getdate(),120),120)");
	        //转换格式化结束日期
        	sql.append(" and convert(DATETIME,convert(char(").append(z0165Item.getItemlength()).append("),ISNULL(Z0165,getdate()),120),120)>= ");
        	sql.append(" convert(DATETIME,convert(char(").append(z0165Item.getItemlength()).append("),getdate(),120),120) ");
        }else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
        	String z0163Format = this.getOracleDateFormat(z0163Item.getItemlength());
	        String z0165Format = this.getOracleDateFormat(z0165Item.getItemlength());
			//转换格式化开始日期
        	sql.append(" and to_date(to_char(Z0163,'").append(z0163Format).append("'),'").append(z0163Format).append("')<=");
	        sql.append("  to_date(to_char(SYSDATE,'").append(z0163Format).append("'),'").append(z0163Format).append("')");
	        //转换格式化结束日期
	        sql.append(" and to_date(to_char(Nvl(Z0165,SYSDATE),'").append(z0165Format).append("')").append(",'").append(z0165Format).append("')>=");
	        sql.append("  to_date(to_char(SYSDATE,'").append(z0165Format).append("'),'").append(z0165Format).append("')");
        }

        sql.append(" and z0101 in (").append(batchIds).append(")");

        ResultSet rs = null;
        try {
            rs = dao.search(sql.toString());
            canQueryScore = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return canQueryScore;
    }

    public String getScoreTabId() {
        String tabid = "";
        ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn, "1");
        try {
            // 先取招聘参数设置
            HashMap map = xmlBo.getAttributeValues();
            tabid = (String) map.get("scoreCard");
            // 招聘参数中未设，兼容老客户，检查system参数中是否有设置
            if (tabid == null || "".equals(tabid) || "#".equals(tabid)) {
                tabid = SystemConfig.getPropertyValue("zp_score_tabid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tabid.trim();
    }

    /**
     * 判断当前人员是否可以打印准考证 打印准考证需满足以下三个条件： 1、参数中是否设置了准考证；
     * 2、未结束的批次下的考场中是否安排了当前应聘者并且已分配了准考证号； 3、当前是否处于考场对应的招聘批次中允许打印准考证日期范围内。
     *
     * @param a0100
     * @param admissionCard
     *            准考证模板id,若未设置则为#
     * @return
     * @throws GeneralException
     */
    public boolean canPrintExamNo(String a0100, String admissionCard) {
        boolean res = false;

        // 没定义准考证模板不允许打印
        if (StringUtils.isEmpty(admissionCard) || "#".equalsIgnoreCase(admissionCard)) {
            return false;
        }
		//先取得两个字段的长度,再决定如何进行格式化日期格式
	    FieldItem z0159Item =  DataDictionary.getFieldItem("z0159");//拿到开始日期指标
	    FieldItem z0161Item = DataDictionary.getFieldItem("z0161");//拿到结束日期指标
	    StringBuffer sqlBuffer = new StringBuffer();
	    sqlBuffer.append("select 1 from z01 where z0101 in ");
	    sqlBuffer.append("(select hall.batch_id from zp_exam_hall hall,zp_exam_assign ass where ass.exam_hall_id = hall.id and ass.a0100='");
	    sqlBuffer.append(a0100).append("') and ");
	    if(Sql_switcher.searchDbServer()==Constant.MSSQL){
			sqlBuffer.append(" convert(DATETIME,convert(char(").append(z0159Item.getItemlength());
			sqlBuffer.append("),z0159,120),120)");
			sqlBuffer.append("<=");
		    sqlBuffer.append(" convert(DATETIME,convert(char(").append(z0159Item.getItemlength());
		    sqlBuffer.append("),getdate(),120),120)");
		    sqlBuffer.append(" and (");
		    sqlBuffer.append(" convert(DATETIME,convert(char(").append(z0161Item.getItemlength());
		    sqlBuffer.append("),z0161,120),120)");
		    sqlBuffer.append(">=");
		    sqlBuffer.append(" convert(DATETIME,convert(char(").append(z0161Item.getItemlength());
		    sqlBuffer.append("),getdate(),120),120) or z0161 is null)");
	    }else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
	    	String z0159Format = this.getOracleDateFormat(z0159Item.getItemlength());
		    String z0161Format = this.getOracleDateFormat(z0161Item.getItemlength());
	    	sqlBuffer.append( "to_date(to_char(z0159,'").append(z0159Format).append("'),'").append(z0159Format).append("')");
	    	sqlBuffer.append("<=");
		    sqlBuffer.append( "to_date(to_char(SYSDATE,'").append(z0159Format).append("'),'").append(z0159Format).append("')");
		    sqlBuffer.append(" and(");
		    sqlBuffer.append( "to_date(to_char(z0161,'").append(z0161Format).append("'),'").append(z0161Format).append("')");
		    sqlBuffer.append(">=");
		    sqlBuffer.append( "to_date(to_char(SYSDATE,'").append(z0161Format).append("'),'").append(z0161Format).append("')");
		    sqlBuffer.append(" or z0161 is null)");
	    }
//        String sql = "select 1 from z01 where z0101 in "
//                + "(select hall.batch_id from zp_exam_hall hall,zp_exam_assign ass where ass.exam_hall_id = hall.id and ass.a0100='"
//                + a0100 + "')" + "  and z0159<=" + Sql_switcher.today() + " and (z0161>="
//                + Sql_switcher.today() + "  or z0161 is null)";

        RowSet rs = null;
        try {
            rs = dao.search(sqlBuffer.toString() );
            if (rs.next()) {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return res;
    }

    /**
     * 判断指定人员是否已申请过特定的职位
     *
     * @param z0301
     * @param a0100
     * @return
     * @throws GeneralException
     */
    public boolean isApplyedPosition(String z0301, String a0100) {
        boolean res = false;
        try {
        	if(zp_positions ==null) {
                getPosition(a0100);
            }

        	for (String pos_id: zp_positions) {
        		if(pos_id.equals(z0301)){
        			res = true;
        			break;
        		}
        	}

        } catch (GeneralException e) {
			e.printStackTrace();
		}
        return res;
    }

    /**
     * 获取个人申请过的职位id
     * @param a0100
     * @throws GeneralException
     */
    public void getPosition(String a0100) throws GeneralException {
    	RowSet rs = null;
    	try {
    		zp_positions = new ArrayList<String>();
    		String sql = "select zp_pos_id from zp_pos_tache where a0100=? ";
    		ArrayList values = new ArrayList();
    		values.add(a0100);

    		rs = dao.search(sql, values);
    		while(rs.next()){
    			zp_positions.add(rs.getString("zp_pos_id"));
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	} finally {
    		PubFunc.closeDbObj(rs);
    	}

    }


    /**
     * 获取参数设置的显示的最后一级单位
     * @param unitLevel 招聘外网显示的最后一级单位的层级
     */
    private ArrayList<LazyDynaBean> getLastLvUnits(String unitLevel) {
        if(this.lastLvUnits != null && this.lastLvUnits.size() >0) {
            return this.lastLvUnits;
        }

        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        RowSet rs = null;

        try {
        	//改为获取单位和部门
            String sql = "select codeitemid,codeitemdesc from organization where codesetid<>'@K' and grade = " + unitLevel;
            rs = dao.search(sql);
            while (rs.next()) {
                String id = rs.getString("codeitemid");
                LazyDynaBean abean = new LazyDynaBean();
                abean.set("codeitemid", id);

                String codeitemdesc = rs.getString("codeitemdesc");
                int length = codeitemdesc.getBytes().length;
                String altdesc = codeitemdesc;
                if (length > 30) {
                    ArrayList blist = this.getMsgList(codeitemdesc, 30);
                    codeitemdesc = (String) blist.get(0) + "...";
                }
                abean.set("codeitemdesc", codeitemdesc);
                abean.set("altdesc", altdesc);
                list.add(abean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String checkAccount () {
        String flag = "false";
        // 获取设置的电话 手机号指标
        String phoneField = ConstantParamter.getMobilePhoneField().toLowerCase();
        // 获取唯一性指标
        String onlynField = getOnly_field();

        if (StringUtils.isNotEmpty(onlynField) && !"a0101".equalsIgnoreCase(onlynField) && StringUtils.isNotEmpty(phoneField)) {
            flag = "true";
        }

        return flag;
    }

    /**
     * 获取外网招聘页面需要显示的招聘职位
     * @param zpUnitCode 需要显示的招聘单位
     * @param zpPosList 外网招聘单位的缓存数据
     * @return zpUnitPosList 外网招聘页面需要显示的招聘职位
     */
    public ArrayList getShowPostList(String zpUnitCode, ArrayList posList){
        if(StringUtils.isEmpty(zpUnitCode) || posList == null || posList.size() < 1) {
            return posList;
        }

        ArrayList zpUnitPosList = new ArrayList();
        RowSet rs = null;
        try {
            // 是否是顶级单位
            boolean isRoot = false;
            String codesetid = "";
            rs = dao.search("select codeitemid,codeitemdesc from organization where parentid=codeitemid and codeitemid='" + zpUnitCode + "'");
            if (rs.next()) {
                isRoot = true;
            }
            rs = dao.search("select codesetid from organization where codeitemid='" + zpUnitCode + "'");
            if (rs.next()) {
                codesetid=rs.getString("codesetid");
            }

            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();

            //是否按单位部门显示
            String unitOrDepart="";
            if(map.get("unitOrDepart") != null && ((String)map.get("unitOrDepart")).trim().length()>0) {
                unitOrDepart=(String)map.get("unitOrDepart");
            }
            if (map.get("hirePostByLayer") != null) {
                hirePostByLayer = (String) map.get("hirePostByLayer");
            }

            for (int m = 0; m < posList.size(); m++) {
                LazyDynaBean bean = (LazyDynaBean) posList.get(m);
                String orgId = (String) bean.get("id");

                if("2".equals(unitOrDepart)&&!"UN".equalsIgnoreCase(codesetid)){//选的的是部门的情况
                	ArrayList<LazyDynaBean> a_posList = (ArrayList) bean.get("list");
                	ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
                	String departid = "";
                	String name = (String) bean.get("name");
                	String unitName = name;
                	for (LazyDynaBean obj : a_posList) {
                		 if(obj.get("z0325Id")!=null&&!((String) obj.get("z0325Id")).startsWith(zpUnitCode)) {
                             continue;
                         }
                         //当设置了只显示本级单位的招聘岗位时，如果招聘岗位的所属单位不是顶级单位，同时也不是页面传递的单位，则不添加到页面中显示的list中
                         if ("1".equals(hirePostByLayer) && !isRoot && !zpUnitCode.equalsIgnoreCase((String) obj.get("z0325Id"))) {
                             continue;
                         }
                         departid = (String) obj.get("z0325Id");
                         unitName = name+"/"+(String) obj.get("z0325");
                         list.add(obj);
					}
                	if(list.size()>0){
                		LazyDynaBean aBean = new LazyDynaBean();
	                	aBean.set("id", departid);
	                	aBean.set("name", unitName);
	                	aBean.set("list", list);
	                	zpUnitPosList.add(aBean);
                	}
                }else{
                	if(!orgId.startsWith(zpUnitCode)) {
                        continue;
                    }
	                //当设置了只显示本级单位的招聘岗位时，如果招聘岗位的所属单位不是顶级单位，同时也不是页面传递的单位，则不添加到页面中显示的list中
	                if ("1".equals(hirePostByLayer) && !isRoot && !zpUnitCode.equalsIgnoreCase(orgId)&&!"2".equals(unitOrDepart)) {
                        continue;
                    }
                	zpUnitPosList.add(bean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return zpUnitPosList;
    }

	/**
	 * 所有子集全部检查是否有必填项未填
	 * @param hireChannel
	 * @param a0100
	 * @throws GeneralException
	 * @throws SQLException
	 */
	public String checkRequired(String hireChannel, String a0100) throws GeneralException {
		RowSet rs = null;
		String isResumePerfection="1";
		try {
			ArrayList<String> values = new ArrayList<String>();
			values.add(a0100);
			ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
			HashMap map = parameterXMLBo.getAttributeValues();
			String candidate_status = hireChannel;//应聘身份指标
			//如果应聘身份指标启用，则先校验该职位渠道是否包含应聘者身份，如果包含继续校验该应聘者身份所有必填指标已填写
			if(map.get("candidate_status")!=null) {
				if(StringUtils.isNotEmpty((String)map.get("candidate_status"))&&!"#".equals((String)map.get("candidate_status"))) {
					rs = dao.search("select "+(String)map.get("candidate_status")+" as candidateStatus from "+a_dbName+"A01 where A0100=?", values);
					if(rs.next()) {
                        candidate_status = rs.getString("candidateStatus");
                    }
				}
			}
			//渠道参数信息
			ArrayList paramsInfo = this.getSetByWorkExprience(candidate_status);
			ArrayList list = (ArrayList)paramsInfo.get(0);
			HashMap fieldMap=(HashMap)paramsInfo.get(1);
	        HashMap fieldSetMap=(HashMap)paramsInfo.get(2);
	        ArrayList<String> mustList = (ArrayList)paramsInfo.get(4);
			StringBuffer whl=new StringBuffer("");
			String setstr = "1";
	        /**所有子集全部检查是否有必填项未填*/
	        for(int i=0;i<list.size();i++)
	        {
	        	setstr = "1";
	            LazyDynaBean bean = (LazyDynaBean)list.get(i);
	            String key=(String)bean.get("fieldSetId");
	            HashMap fieldExtendMap=(HashMap)fieldSetMap.get(key.toLowerCase());
	            ArrayList fieldList=(ArrayList)fieldMap.get(key.toUpperCase()) == null?(ArrayList)fieldMap.get(key.toLowerCase()):(ArrayList)fieldMap.get(key.toUpperCase());

	            whl.setLength(0);
	            for(Iterator t=fieldList.iterator();t.hasNext();)
	            {
	                String itemid=(String)t.next();
	                if(StringUtils.isNotEmpty(itemid)) {
                        itemid = itemid.split("#")[0];
                    }
	                String temp=(String)fieldExtendMap.get(itemid.toLowerCase());
	                if(temp==null) {
                        temp=(String)fieldExtendMap.get(itemid.toUpperCase());
                    }
	                String[] temps=temp.split("#");
	                if("1".equals(temps[1])) {
                        whl.append(" or "+itemid+" is null ");
                    }
	            }
	            for (String setId : mustList){
	            	if(key.equalsIgnoreCase(setId)) {
                        setstr = "2";
                    }
	            }

	            if(whl.length()>0)
	            {

					rs=dao.search("select * from "+a_dbName+key+" where a0100='"+a0100+"' and ( "+whl.substring(3)+" )");

	                if(rs.next()) {
                        isResumePerfection ="0";
                    }

	            }
                rs=dao.search("select * from "+a_dbName+key+" where a0100='"+a0100+"'");
	    		boolean flag=true;
	    		if(rs.next()) {
                    flag=false;
                }

	    		if("1".equals(setstr))//当子集为可选，指标为必选时，根据子集进行确定当前子集无记录可通过验证
                {
                    flag=false;
                }

	    		if(flag&&"2".equals(setstr))
	    		{
	    			isResumePerfection = PubFunc.encrypt(key)+"-"+bean.get("fieldSetDesc");
	    			break;
	    		} else if(!flag&&"0".equals(isResumePerfection)){
	    			isResumePerfection = PubFunc.encrypt(key)+"-"+bean.get("fieldSetDesc")+"-"+"有必填指标未填写！";
	    			break;
	    		}
	        }

            if (map != null && map.get("attachCodeset") != null) {
                String codeSetId = (String) map.get("attachCodeset");
                ParameterSetBo bo = new ParameterSetBo(this.conn);
                HashMap<String, HashMap<String, String>> attachHire = bo.searchHire();
                ArrayList codeitemList = AdminCode.getCodeItemList(codeSetId);
                ContentDAO dao = new ContentDAO(this.conn);
                String guidKey = getGuidKey(a_dbName+"A01",a0100);
                for(int i = 0; i < codeitemList.size(); i++) {
                    CodeItem item = (CodeItem) codeitemList.get(i);
                    HashMap<String, String> itemMap = attachHire.get(item.getCodeitem());
                    if(itemMap != null && itemMap.size() > 0) {
                        String notNull = itemMap.get(candidate_status);
                        if(!"2".equals(notNull)) {
                            continue;
                        }

                        String itemDesc = item.getCodename();
                        String sql = "select 1 from zp_attachment where guidkey=? and file_name like ?";
                        ArrayList<String> valueList = new ArrayList<String>();
                        valueList.add(guidKey);
                        valueList.add(itemDesc + "%");
                        rs = dao.search(sql, valueList);
                        if(!rs.next()) {
                            isResumePerfection = "a00-简历附件-" + itemDesc + "附件不能为空！";
                            break;
                        }
                    }
                }
            }

		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return isResumePerfection;
	}

	/**
	 * 获取招聘附件上传分类
	 * @param map 参数map
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getAttachCodeset(HashMap map, String hireChannel) throws GeneralException {
	    ArrayList list = new ArrayList();
	    try {
	        String attach_codeset = (String) map.get("attachCodeset");
	        if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset)) {
	            ParameterSetBo bo = new ParameterSetBo(this.conn);
	            HashMap<String, HashMap<String, String>> arrachHire = bo.searchHire();

	            if(arrachHire.isEmpty()) {
                    return list;
                }

	            boolean showFlag = false;
	            ArrayList codeItemList = AdminCode.getCodeItemList(attach_codeset);
	            HashMap<String, String> codeMap = new HashMap<String, String>();
	            for(int i = 0; i<codeItemList.size(); i++) {
	                CodeItem  item= (CodeItem) codeItemList.get(i);
	                codeMap = new HashMap<String, String>();
	                codeMap.put("itemId", item.getCodeitem());
	                codeMap.put("itemDesc", item.getCodename());
	                String notNull = "0";
	                HashMap<String, String> itemMap = arrachHire.get(item.getCodeitem());
	                if(itemMap != null && itemMap.size() > 0) {
	                    notNull = itemMap.get(hireChannel);
	                    notNull = StringUtils.isEmpty(notNull) ? "0" : notNull;
	                }

	                if("1".equals(notNull) || "2".equals(notNull)) {
                        showFlag = true;
                    }

	                codeMap.put("notNull", notNull);
	                list.add(codeMap);
	            }

	            if(!showFlag) {
                    list.clear();
                }
	        }
	    }catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
        }

		return list;
	}

	/**
	 * 如果有应聘身份指标则获取渠道名称
	 * @param candidate_status_itemId
	 * @throws SQLException
	 */
	public String getChannelName(String candidate_status_itemId, String a0100 ,String candidate_status) {
		String channelName = "";
		//如果应聘身份指标启用，则先校验该职位渠道是否包含应聘者身份，如果包含继续校验该应聘者身份所有必填指标已填写
		if(StringUtils.isNotEmpty(candidate_status_itemId)&&!"#".equals(candidate_status_itemId)||StringUtils.isNotEmpty(candidate_status)) {
			if(StringUtils.isEmpty(candidate_status)) {
                candidate_status = getCandidateStatus(candidate_status_itemId, a0100);
            }

			channelName = getChannelName(candidate_status);
		}
		return channelName;
	}

	/**
	 * 根据职位获取渠道名称
	 * @param hireChannel
	 * @return
	 */
	public String getChannelName(String hireChannel) {
		if(channelMap == null) {
            channelMap = new HashMap();
        }
		String channelName = "";
		if(EmployNetPortalBo.channelMap.get(hireChannel)!=null) {
            channelName = (String) EmployNetPortalBo.channelMap.get(hireChannel).get("channelName");
        }
		if(StringUtils.isEmpty(channelName)) {
			CodeItem code = AdminCode.getCode("35", hireChannel);
			if(code!=null) {
                channelName = code.getCodename();
            }

			if(channelMap.get(hireChannel)==null){
            	HashMap infoMap = new HashMap();
            	infoMap.put("channelName", channelName);
            	channelMap.put(hireChannel, infoMap);
            }else{
            	channelMap.get(hireChannel).put("channelName", channelName);
            }
		}
		return channelName;
	}

	/**
	 * 获取应聘身份
	 * @param candidate_status_itemId 应聘身份指标
	 * @param a0100
	 * @return
	 */
	public String getCandidateStatus(String candidate_status_itemId, String a0100) {
		RowSet rs = null;
		try {
			ArrayList<String> values = new ArrayList<String>();
			values.add(a0100);
			rs = dao.search("select "+candidate_status_itemId+" as candidateStatus from "+a_dbName+"A01 where A0100=?", values);
			if(rs.next()) {
				hireChannel = rs.getString("candidateStatus");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return hireChannel;
	}

	/**
	 * 渠道编号为空时从数据库中获取第一个
	 * @return
	 */
	public String getHireChannelFromTable() {
		RowSet rs = null;
		try {
			rs = dao.search("select params from t_cms_content where visible=1 and params like '%hireChannel%' order by channel_id, content_sort");
			if(rs.next()) {
				String params = rs.getString("params");
				String[] split = params.split("&");
				for (String string : split) {
					if(string.startsWith("hireChannel")) {
						hireChannel = string.split("=")[1];
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return hireChannel;
	}

	public String getIsAttach(HashMap map, String hireChannel, String isAttach) {
	    try {
	        String attach_codeset = (String) map.get("attachCodeset");
	        if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset)) {
	            ParameterSetBo paramBo = new ParameterSetBo(this.conn);
	            HashMap<String, HashMap<String, String>> arrachHire;
	            arrachHire = paramBo.searchHire();
	            if(arrachHire.isEmpty()) {
                    isAttach = "0";
                } else {
	                boolean showFlag = false;
	                ArrayList codeItemList = AdminCode.getCodeItemList(attach_codeset);
	                for(int i = 0; i<codeItemList.size(); i++) {
	                    CodeItem  item= (CodeItem) codeItemList.get(i);
	                    String notNull = "0";
	                    HashMap<String, String> itemMap = arrachHire.get(item.getCodeitem());
	                    if(itemMap != null && itemMap.size() > 0) {
	                        notNull = itemMap.get(hireChannel);
	                        notNull = StringUtils.isEmpty(notNull) ? "0" : notNull;
	                    }

	                    if("1".equals(notNull) || "2".equals(notNull)) {
                            showFlag = true;
                        }

	                }

	                if(!showFlag) {
                        isAttach = "0";
                    } else {
                        isAttach = "1";
                    }
	            }
	        }
	    } catch (GeneralException e) {
	        e.printStackTrace();
	    }

        return isAttach;
    }

	/**
	 * 对已上传文件进行排序
	 * @param list
	 * @param uploadFileList
	 * @return
	 */
	public ArrayList sortFileList(ArrayList<HashMap> list, ArrayList<LazyDynaBean> uploadFileList) {
		//已修改的附件分类
		ArrayList<LazyDynaBean> fileList = new ArrayList<LazyDynaBean>();
		//当前附件附件分类
		ArrayList<LazyDynaBean> fileCodeset = new ArrayList<LazyDynaBean>();
		String itemDesc = "";
		String fileName = "";
		for (HashMap codeMap : list) {
            itemDesc = (String) codeMap.get("itemDesc");
            LazyDynaBean bean = null;
            for(int i = uploadFileList.size()-1; i>=0; i--) {
                bean = uploadFileList.get(i);
                fileName = (String) bean.get("fileName");
                // 20191123 取不到的时候，再换file_name取一下，有的地方用的key是file_name
                if(fileName == null) {
                    fileName = (String) bean.get("file_name");
                }

                if (StringUtils.isBlank(fileName)) {
                    continue;
                }

                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                if(itemDesc.equalsIgnoreCase(fileName)) {
                    fileCodeset.add(bean);
                    uploadFileList.remove(i);
                }
            }
        }
        fileCodeset.addAll(fileList);
        fileCodeset.addAll(uploadFileList);
		return fileCodeset;
	}


	/**
	 * 获取简历附件子集是否是必填子集
	 * @param map 参数map
	 * @return
	 * @throws GeneralException
	 */
	public boolean getMustCodeset(HashMap map, String hireChannel) throws GeneralException {
	    ArrayList list = new ArrayList();
	    boolean mustFlag = false;
	    try {
	        String attach_codeset = (String) map.get("attachCodeset");
	        if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset)) {
	            ParameterSetBo bo = new ParameterSetBo(this.conn);
	            HashMap<String, HashMap<String, String>> arrachHire = bo.searchHire();

	            if(arrachHire.isEmpty()) {
                    return false;
                }

	            ArrayList codeItemList = AdminCode.getCodeItemList(attach_codeset);
	            HashMap<String, String> codeMap = new HashMap<String, String>();
	            for(int i = 0; i<codeItemList.size(); i++) {
	                CodeItem  item= (CodeItem) codeItemList.get(i);
	                String notNull = "0";
	                HashMap<String, String> itemMap = arrachHire.get(item.getCodeitem());
	                if(itemMap != null && itemMap.size() > 0) {
	                    notNull = itemMap.get(hireChannel);
	                    notNull = StringUtils.isEmpty(notNull) ? "0" : notNull;
	                }

	                if("2".equals(notNull)){
	                	mustFlag = true;
	                	return mustFlag;
	                }
	            }

	        }
	    }catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
        }
		return mustFlag;
	}


	/**
	 * 公告分页方法
	 * @param boardlist全部公告
	 * @param hashMap
	 * @return
	 */
	public ArrayList getPageBoardList(ArrayList<LazyDynaBean> boardlist, HashMap hashMap) {
		ArrayList pageBoardList = new ArrayList();
		int pageSize = 5;
		int pageNum = (Integer) hashMap.get("pageNum")==null? 1 :(Integer) hashMap.get("pageNum");
		int startIndex = 0;
		int endIndex = 0;
		String operation = (String) hashMap.get("operation")==null? "previous" :(String) hashMap.get("operation");
		if(boardlist!=null) {
			int pageCount = (boardlist.size()%pageSize)>0?(boardlist.size()/pageSize)+1:boardlist.size()/pageSize;
			//上一页
			if("previous".equals(operation)) {
                pageNum = (pageNum-1)<=0?1:pageNum-1;
            } else if("next".equals(operation))//下一页
            {
                pageNum = (pageNum+1)>pageCount?pageCount:pageNum+1;
            }

			startIndex = pageSize*(pageNum-1);
			endIndex = (startIndex+pageSize)>boardlist.size()?boardlist.size():startIndex+pageSize;
			for(int i = startIndex; i<endIndex;i++) {
				LazyDynaBean bean = boardlist.get(i);
				pageBoardList.add(bean);
			}
			hashMap.put("pageNum", pageNum);
			hashMap.put("pageCount", pageCount);
			hashMap.put("pageBoardList", pageBoardList);
		}
		return pageBoardList;
	}

	/**
	 * 获取guidkey
	 * @param tablename
	 * @param a0100
	 * @return
	 */
	private String getGuidKey(String tablename,String a0100)
    {
        String guid = "";
        RowSet frowset = null;
        try{
            StringBuffer sb = new StringBuffer();
            StringBuffer sWhere  = new StringBuffer();

            sWhere.append(" where A0100 ='");
            sWhere.append(a0100);
            sWhere.append("'");

            sb.append("select GUIDKEY from ");
            sb.append(tablename);
            sb.append(sWhere.toString());
            frowset = dao.search(sb.toString());
            if (frowset.next()) {
                guid = frowset.getString("guidkey");
                if (guid==null || "".equals(guid)){
                    UUID uuid = UUID.randomUUID();
                    String tmpid = uuid.toString();
                    StringBuffer stmp = new StringBuffer();
                    stmp.append("update  ");
                    stmp.append(tablename);
                    stmp.append(" set GUIDKEY ='");
                    stmp.append(tmpid.toUpperCase());
                    stmp.append("'");
                    stmp.append(sWhere.toString());
                    stmp.append(" and guidkey is null ");
                    dao.update(stmp.toString());

                    frowset = dao.search(sb.toString());
                    if (frowset.next()) {
                        guid = frowset.getString("guidkey");
                    }
                }
            }
        }
        catch (Exception e ){
           e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(frowset);
        }
        return guid;
     }

	/**
	 * 特殊字符还原，解决简历内容中特殊符号显示成编码问题
	 * @param value
	 * @return
	 */
	private String keyWord_reback(String value)
	{
		if (value == null||value.trim().length()==0) {
            return value;
        }
        value = value.replaceAll("；", ";");
        value = value.replaceAll("＆", "&");
        value = value.replaceAll("＃", "#");
		return value.toString();
	}

	/**
	 * 根据日期类型指标的长度
	 * 获取日期类型指标的格式化格式
	 * 为oracle使用
	 * @param itemLength 指标长度
	 * @return 格式化字符串
	 */
	private String getOracleDateFormat(int itemLength){
		String format = StringUtils.EMPTY;
		switch (itemLength){
			case 4:
				format = "yyyy";
				break;
			case 7:
				format = "yyyy-MM";
				break;
			case 16:
				format = "yyyy-MM-dd HH24:mi";
				break;
			case 18:
				format = "yyyy-MM-dd HH24:mi:ss";
				break;
			case 10:
			default:
				format = "yyyy-MM-dd";
		}
		return format;
	}
}
