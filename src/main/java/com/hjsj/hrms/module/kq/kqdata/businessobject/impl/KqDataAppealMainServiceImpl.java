package com.hjsj.hrms.module.kq.kqdata.businessobject.impl;

import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.config.item.businessobject.impl.KqItemServiceImpl;
import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataAppealMainService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.sendmessage.email.SendEmailUtil;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
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
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 数据上报  业务实现类
 * create time 2018/11/15
 *
 * @author wangbo
 */
public class KqDataAppealMainServiceImpl implements KqDataAppealMainService {

    private UserView userView;
    private Connection conn;

    public KqDataAppealMainServiceImpl(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 获取 全部|待办  考勤上报数据   分页显示
     *
     * @param filterStatus      考勤上报方案状态  全部 | 待办 01 not null
     * @param filterScheme_id   考勤方案 允许为null
     * @param filterKq_year     考勤年份  允许为null
     * @param filterKq_duration 考勤区间  允许为null
     * @param filterOrg_id      考勤机构id 允许为null
     * @param currentPage 当前页签 not null
     * @param pageSize    每页显示数 not null
     * @return 数据格式 ：{
     * kq_year:"",考勤年份
     * org_list:[{},{...}]考勤上报数据
     * year_list:[2018,.。。] 考勤年份
     * }
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
     * @author wangbo
     * @date 11:29 2018/11/7
     */
    @Override
    public HashMap listKqDataAppeal(String filterStatus, String filterScheme_id, String filterKq_year, String filterKq_duration
    		, String filterOrg_id, int currentPage, int pageSize) throws GeneralException {
        HashMap return_data = new HashMap();
        return_data.put("kq_year", "");
        return_data.put("totalCount", 0);
        return_data.put("org_list", new ArrayList());
        return_data.put("year_list", new ArrayList());
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if(StringUtils.isBlank(filterKq_year)){
                filterKq_year=String.valueOf(cal.get(Calendar.YEAR));
            }
            //获取当年全部考勤期间
            PeriodService periodService = new PeriodServiceImpl(this.userView, this.conn);
            ArrayList parameterList = new ArrayList();
            parameterList.add(filterKq_year);
            ArrayList<LazyDynaBean> durationlist = periodService.listKq_duration(" And kq_year=? ", parameterList, "kq_duration desc");
            //获取当年全部考勤期间
            if (durationlist.size() == 0) {
            	// 57656 防止当前年份未生成考勤期间 则获取前一年的期间 如果还没有则直接返回
            	parameterList.clear();
            	parameterList.add(String.valueOf(cal.get(Calendar.YEAR) - 1));
            	durationlist = periodService.listKq_duration(" And kq_year=? ", parameterList, "kq_duration desc");
            	if (durationlist.size() == 0) {
            		return_data.put("msg", "nodata");
            		return_data.put("erro_msg", ResourceFactory.getProperty("kq.data.sp.msg.pleaseSetKqDuration"));
            		return return_data;
            	}
            }
            //获取自己涉及到的考勤方案
            ArrayList<HashMap<String, Object>> myKqSchemeList = this.listKqSchemeByMyself();
            if (myKqSchemeList.size() == 0) {
                return_data.put("msg", "nodata");
                return return_data;
            }
            int startDuration = -1;

            //获取距当前系统时间最近的考勤期间数组下标
            int k = 0;
            while (startDuration == -1 && k <= 12) {
                for (int j = 0; j < durationlist.size(); j++) {
                    LazyDynaBean bean = durationlist.get(j);
                    long kq_start = ((java.sql.Date) bean.get("kq_start")).getTime();
                    long kq_end = ((java.sql.Date) bean.get("kq_end")).getTime();

                    if (kq_start <= cal.getTimeInMillis() && kq_end >= cal.getTimeInMillis()) {
                        startDuration = j;
                        break;
                    }
                }
                k++;
                cal.add(Calendar.MONTH, 1);
            }
            if (startDuration == -1) {
                startDuration = 0;
            }
            //获取距当前系统时间最近的考勤期间数组下标  结束
            
            KqDataUtil kqDataUtil = new KqDataUtil(this.userView, this.conn);

            /**
             * 获取考勤上报数据，若需要上报人事处，则返回当前考勤年份全部已经创建的数据，
             *
             * 若不需要上报人事处，
             * 则下级单位考勤员可以自己新建考勤数据，
             * 所以不仅需要获取已创建的数据还要，拼接当前考勤年份，可创建的数据，
             * 获取当前考勤期间后3个期间的可创建数据。
             *
             */
            // 55502 在显示待办时  批准后的记录  可能会存在归档功能授权  故增加校验
            boolean filterStatuBool = "01".equals(filterStatus);
            String userName = ","+userView.getUserName()+","+userView.getS_userName()+","+userView.getUserFullName()+",";
            // 46699 校验涉及到登录用户的方案中是否存在  不需要上报人事处的方案
            boolean haveSecondaryFlag = true;
            ArrayList<LazyDynaBean> allDataList = new ArrayList<LazyDynaBean>();
            for (HashMap<String, Object> map : myKqSchemeList) {
                String scheme_id=(String) map.get("scheme_id");
                //判断是否需要上报人事处 secondary_admin 0不需要 1需要
                boolean haveSecondary = false;
                if (map.containsKey("secondary_admin") && map.get("secondary_admin") != null) {
                    if ("1".equals(String.valueOf(map.get("secondary_admin")))) {
                        haveSecondary = true;
                    }
                }
                //获取全部已创建的数据
                LinkedHashMap<String, LazyDynaBean> kqDataOrgMap = this.kqDataOrgData(scheme_id,filterKq_year, filterStatus, map);
                ArrayList<HashMap<String, String>> orglist = (ArrayList<HashMap<String, String>>) map.get("orgList");
                if (haveSecondary) {
                    //若需要上报人事处 则直接返回已创建的数据
                    Iterator iterator = kqDataOrgMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<String, LazyDynaBean> entry = (Entry<String, LazyDynaBean>) iterator.next();
                        LazyDynaBean bean = entry.getValue();
                        HashMap<String, String> orgMap = new HashMap<String, String>();
                        String key = entry.getKey();
                        String orgId = key.substring(key.indexOf(":") + 1);
                        for (int i = 0; i < orglist.size(); i++) {
                        	orgMap = orglist.get(i);
                            String orgid = (String) orgMap.get("org_id");
                            if(orgId.equalsIgnoreCase(orgid))
                            	break;
                        }
                        // 获取当前操作人
                        String approveUserName = getApproveUser(bean, map, orgMap, haveSecondary, kqDataUtil);
                        if(filterStatuBool && !userName.contains(","+approveUserName+",")) {
                        	continue;
                        }
                        bean.set("approveUser", approveUserName);
                        allDataList.add(bean);
                    }
                } else {
                	haveSecondaryFlag = false;
                    //若不需要上报人事处，则遍历考勤期间组合已创建和可创建的数据
                    for (int i = 0; i < durationlist.size(); i++) {
                        LazyDynaBean durationBean = durationlist.get(i);
                        String duration = (String) durationBean.get("kq_duration");
                        for (HashMap orgMap : orglist) {
                            String orgid = (String) orgMap.get("org_id");
                            // 去掉 机构创建时间校验
                            int role =Integer.parseInt(((String)orgMap.get("role")));
                            LazyDynaBean bean = null;
                            //判断是否是已创建的数据
                            if (kqDataOrgMap.containsKey(duration +":"+ orgid)) {
                                bean = kqDataOrgMap.get(duration  +":"+ orgid);
                                String approveUserName = getApproveUser(bean, map, orgMap, haveSecondary, kqDataUtil);
                                if(filterStatuBool && !userName.contains(","+approveUserName+",")) {
                                	continue;
                                }
                                bean.set("approveUser", approveUserName);
                            } else if(!"01".equals(filterStatus)&&role==KqDataUtil.role_Agency_Clerk) {
                                //若没有在页面上进行 待办的过滤，并且是考勤员，则可组合可创建的数据
                                //仅获得后三个月的及前一个月的
                                if  (startDuration-i < 3 && startDuration-i >= -1) {
                                	Date kq_start=(Date) durationBean.get("kq_start");
                                	Date kq_end=(Date) durationBean.get("kq_end");
                                	//机构历史时点显示控制
                                	if (checkOrg(orgid, kq_start,  kq_end)) {
	                                    bean = new LazyDynaBean();
	                                    bean.set("scheme_id", scheme_id);
	                                    bean.set("scheme_name", map.get("scheme_name"));
	                                    bean.set("kq_year", filterKq_year);
	                                    bean.set("kq_duration", filterKq_year + "." + duration);
	                                    bean.set("org_id", orgid);
	                                    bean.set("org_name", orgMap.get("org_name"));
	                                    bean.set("hasNextApprover", orgMap.get("hasNextApprover"));
	                                    bean.set("status", "00");
	                                    // 默认为未提交
	                                    bean.set("status_name", ResourceFactory.getProperty("kq.data.appeal.unsubmit"));
	                                    bean.set("confirms", "");
	                                    bean.set("participants", "");
	                                    bean.set("operation", "3");
                                	}else {
                                		continue;
                                	}
                                } else {
                                    continue;
                                }
                            }else{
                                continue;
                            }
                            allDataList.add(bean);
                        }
                    }
                }
            }

            ArrayList kqYearList = this.listSchemeKqYear(filterScheme_id, filterKq_duration, filterOrg_id);
            // 46699 不需要上报的记录第一次需要在机构考勤员显示出来，这种情况不会出现在kq_extend_log记录中
            if (kqYearList.size() == 0 && haveSecondaryFlag) {
                return return_data;
            }
            LinkedHashSet<Integer> yearSet=new LinkedHashSet<Integer>();
            parameterList.clear();
            cal=Calendar.getInstance();
            cal.add(Calendar.MONTH, -12);
            parameterList.add(String.valueOf(cal.get(Calendar.YEAR)));
            durationlist=periodService.listKq_duration(" And kq_year >= ?", parameterList, "kq_year");
            for (LazyDynaBean bean : durationlist) {
                yearSet.add(Integer.parseInt(((String)bean.get("kq_year"))));
            }
            if(kqYearList.size()>0) {
                for (Integer i : yearSet) {
                    if ((Integer) kqYearList.get(0) < i) {
                        kqYearList.add(0, i);
                    }
                }
            }else{
                kqYearList.addAll(yearSet);
            }
            
            String year = filterKq_year;
            if (StringUtils.isBlank(year)) {
                year = String.valueOf(kqYearList.get(0));
            }

            for(int i=(currentPage-1)*pageSize;i<allDataList.size()&&i<currentPage*pageSize;i++){
                LazyDynaBean bean=allDataList.get(i);
                bean.set("scheme_id",PubFunc.encrypt((String) bean.get("scheme_id")));
                bean.set("org_id",PubFunc.encrypt((String) bean.get("org_id")));
                dataList.add(bean);
            }
            //填写审批意见 0：不填写（默认），1：需要填写意见
            KqPrivForHospitalUtil privForHospitalUtil = new KqPrivForHospitalUtil(this.userView, this.conn);
            String approvalMessage=privForHospitalUtil.getApprovalMessage();
            return_data.put("approvalMessage", approvalMessage);
            
            return_data.put("kq_year", year);
            return_data.put("totalCount", allDataList.size());
            return_data.put("org_list", dataList);
            return_data.put("year_list", kqYearList);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return return_data;
    }

    /**
     * 更新考勤确认状态，和待办状态
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id      机构编号
     * @param flag        更新的状态
     * @return 更新成功true, false 更新失败
     * @throws GeneralException
     * @author wangbo
     * @date 11:29 2018/11/13
     */
    @Override
    public boolean updateKqConfirm(int scheme_id, String kq_year, String kq_duration, String org_id, String flag, String confirmMemo)
            throws GeneralException {
    	//个人确认说明指标
    	String confirmField = SystemConfig.getPropertyValue("confirm_memo");
    	ArrayList list = new ArrayList();
        list.add(flag);
        list.add(scheme_id);
        list.add(kq_year);
        list.add(kq_duration);
        list.add(org_id + "%");
        list.add(this.getGuidkey(this.userView.getDbname(), this.userView.getA0100()));
    	StringBuffer sql = new StringBuffer();
    	sql.append("update Q35 set confirm=? ");
    	if(StringUtils.isNotBlank(confirmField)&&StringUtils.isNotBlank(confirmMemo)) {
    		FieldItem fieldItem = DataDictionary.getFieldItem(confirmField, "Q35");
    		if(fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
    			sql.append(", " + confirmField + "=? ");
    			list.add(1, confirmMemo);
    		}
    	}
    	sql.append("where scheme_id=? and kq_year=? and kq_duration=? and org_id like ? and guidkey=?");
    	//如果考勤已确认则不允许修改
    	sql.append(" and (0=" + Sql_switcher.isnull("confirm", "0") + " or confirm=0)");
    	
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            if (dao.update(sql.toString(), list) <= 0)
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            //修改考勤确认状态
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.date.appeal.error.updateKqConfirmMsg")));
        }
        list.clear();
        list.add(this.userView.getDbname().toUpperCase().substring(0, 1) + this.userView.getDbname().toLowerCase().substring(1) + this.userView.getA0100());
        KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
        kqDataUtil.kqFinishPengdingTask(this.conn, scheme_id, kq_year, kq_duration, KqDataUtil.TASKTYPE_CONFIRM, KqDataUtil.role_Agency_Clerk, list, org_id);
        return true;
    }

    /**
     * 获取具体某人的某月考勤数据明细
     *
     * @param scheme_id   考勤方案编号
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id      考勤机构
     * @return
     * @throws GeneralException
     * @author wangbo
     * @date 11:29 2018/11/13
     */
    @Override
    public String getKqConfirmLetter(int scheme_id, String kq_year, String kq_duration, String org_id)
            throws GeneralException {
        String guidkey = this.getGuidkey(this.userView.getDbname(), this.userView.getA0100());
        String sqlWhere = " and kq_year = ? and kq_duration = ? and scheme_id = ? and org_id like ? and guidkey = ?";
        ArrayList parameterList = new ArrayList();
        parameterList.add(kq_year);
        parameterList.add(kq_duration);
        parameterList.add(scheme_id);
        parameterList.add(org_id + "%");
        parameterList.add(guidkey);
        KqDataMxService kqDataMxService = new KqDataMxServiceImpl(this.userView, this.conn);
        ArrayList<LazyDynaBean> listQ35 = kqDataMxService.listQ35(sqlWhere, parameterList, "");
        if (listQ35 == null || listQ35.size() < 1) {
            return null;
        }

        String title = "";
        ArrayList periodList = new ArrayList();
        periodList.add(kq_year);
        periodList.add(kq_duration);
        PeriodService periodService = new PeriodServiceImpl(this.userView, this.conn);
        ArrayList<LazyDynaBean> listKq_duration = periodService.listKq_duration(" and kq_year = ? and kq_duration = ?", periodList, "");
        Date startDate = null;
        Date endDate = null;
        if (listKq_duration.size() > 0) {
            LazyDynaBean lazyDynaBean = listKq_duration.get(0);
            startDate = (Date) lazyDynaBean.get("kq_start");
            endDate = (Date) lazyDynaBean.get("kq_end");
        }
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        if (startDate.getMonth() == endDate.getMonth()) {
            title = ResourceFactory.getProperty("kq.archive.scheme.kqconfirmletter1");
            title = title.replace("{year}", yearFormat.format(startDate)).replace("{month}", kq_duration);
        } else if (startDate.getYear() == endDate.getYear()) {
            String sDate = yearFormat.format(startDate) + ResourceFactory.getProperty("datestyle.year") + monthFormat.format(startDate) + ResourceFactory.getProperty("datestyle.month") + dayFormat.format(startDate) + ResourceFactory.getProperty("datestyle.day");
            String eDate = monthFormat.format(endDate) + ResourceFactory.getProperty("datestyle.month") + dayFormat.format(endDate) + ResourceFactory.getProperty("datestyle.day");
            title = sDate + "-" + eDate;
        } else {

            String sDate = yearFormat.format(startDate) + ResourceFactory.getProperty("datestyle.year") + monthFormat.format(startDate) + ResourceFactory.getProperty("datestyle.month") + dayFormat.format(startDate) + ResourceFactory.getProperty("datestyle.day");
            String eDate = yearFormat.format(endDate) + ResourceFactory.getProperty("datestyle.year") + monthFormat.format(endDate) + ResourceFactory.getProperty("datestyle.month") + dayFormat.format(endDate) + ResourceFactory.getProperty("datestyle.day");
            title = sDate + "-" + eDate;
        }
        KqDataUtil KqDataUtil = new KqDataUtil(userView, conn);
        // 50470 获取本人明细数据时 不需要考虑方案所选的班次或项目，因为可能出现轮岗不同方案的，所以直接返回全部的
        HashMap kqClassAndItemsHM = KqDataUtil.getAllClassAndItems();
        
        LazyDynaBean lazyDynaBean = listQ35.get(0);
        ArrayList punchDataList = new ArrayList();
        for (int i = 1; i < 32; i++) {
            String itemid = i < 10 ? "q350" + i : "q35" + i;
            punchDataList.add(this.getClassAndItemsDesc(kqClassAndItemsHM, (String) lazyDynaBean.get(itemid)));
        }
        String confirmMemo = "";
        String confirmField = SystemConfig.getPropertyValue("confirm_memo");
    	if(StringUtils.isNotBlank(confirmField)) {
    		FieldItem fieldItem = DataDictionary.getFieldItem(confirmField, "Q35");
    		if(fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
    			Object object = lazyDynaBean.get(confirmField.toLowerCase());
    			confirmMemo = (String) (object==null?"":object);
    		}
    	}
        ArrayList<LazyDynaBean> kqItemDataList = this.getkqItemData(lazyDynaBean.getMap(), scheme_id);
        String dayDetailEnabled = getDayDetailEnabled(scheme_id);
        this.removeHideColumns(kqItemDataList, dayDetailEnabled, kq_duration, kq_year, scheme_id);
        String content = this.kqConfirmContent(title, startDate, endDate, punchDataList, kqItemDataList,
        		String.valueOf(scheme_id), kq_year, kq_duration, org_id, null, "show", dayDetailEnabled, confirmMemo);
        return content;
    }
	/**
	 * 去除在栏目设置中设置为隐藏的统计项
	 * 
	 * @param kqItemDataList
	 *            统计项与统计项对应的值
	 * @param dayDetailEnabled
	 *            是否显示日明细
	 * @param kq_duration
	 *            考勤区间
	 * @param kq_year
	 *            考勤年
	 * @param scheme_id
	 *            方案编号
	 */
	private void removeHideColumns(ArrayList<LazyDynaBean> kqItemDataList, String dayDetailEnabled, 
			String kq_duration, String kq_year, int scheme_id) {
		try {
			String showMx = "1".equals(dayDetailEnabled) ? "true" : "false";
			KqDataMxService kqDataMxService = new KqDataMxServiceImpl(this.userView, this.conn);
			ArrayList<ColumnsInfo> columnsList = kqDataMxService.getColumnList(showMx, kq_duration, 
					kq_year, scheme_id + "", 0);
			for (ColumnsInfo column : columnsList) {
				String columnId = column.getColumnId();
				int display = column.getLoadtype();
				if (ColumnsInfo.LOADTYPE_BLOCK == display)
					continue;

				for (LazyDynaBean bean : kqItemDataList) {
					String itemid = (String) bean.get("item_id");
					if (columnId.equals(itemid)) {
						kqItemDataList.remove(bean);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 确认考勤发布功能
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
     * @author wangbo
     * @date 11:29 2018/11/12
     */
    @Override
    public void releaseKqData(int scheme_id, String kq_year, String kq_duration, String org_id)
            throws GeneralException {
        this.sendKqConfirmPendingTask(scheme_id, kq_year, kq_duration, org_id);
        this.updateKqDataConfirm(0, scheme_id, kq_year, kq_duration, org_id);
        this.sendKqConfirmLetter(scheme_id, kq_year, kq_duration, org_id);
    }

    /**
     * 获取涉及到自己的考勤方案信息
     *
     * @return myScheme [{
     * scheme_id:考勤方案id
     * scheme_name:考勤方案名称
     * orgList:[{
     * org_id:机构id
     * org_name:机构名称
     * role:身份
     * }]
     * <p>
     * }]
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:06 2018/11/29
     */
    private ArrayList<HashMap<String, Object>> listKqSchemeByMyself() throws GeneralException {
        ArrayList<HashMap<String, Object>> myScheme = new ArrayList<HashMap<String, Object>>();
        try {
        	ArrayList dataList = new ArrayList();
        	StringBuffer whereSql = new StringBuffer("");
        	whereSql.append(" and is_validate='1'");
        	whereSql.append(" and ");
            //oracle
            if (Sql_switcher.searchDbServer() == 2) {
            	whereSql.append(" upper(CTRL_PARAM) like '%'");
                dataList.add(this.userView.getUserName().toUpperCase());
            } else {
            	whereSql.append(" CTRL_PARAM like '%'");
                dataList.add(this.userView.getUserName());
            }
            whereSql.append(Sql_switcher.concat()).append("?").append(Sql_switcher.concat()).append("'%'");

            String userName = "";
            if (StringUtils.isNotBlank(this.userView.getA0100())) {
                userName = this.userView.getDbname() + this.userView.getA0100();
                //oracle
                if (Sql_switcher.searchDbServer() == 2) {
                	whereSql.append(" or upper(CTRL_PARAM) like '%'").append(Sql_switcher.concat());
                    dataList.add(userName.toUpperCase());
                } else {
                	whereSql.append(" or CTRL_PARAM like '%'").append(Sql_switcher.concat());
                    dataList.add(userName);
                }
                whereSql.append("?").append(Sql_switcher.concat()).append("'%'");
            }
            // 56327 改为统一查询方案的接口方法
        	SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.conn, this.userView);
        	ArrayList<LazyDynaBean> shemeBeanList = schemeMainService.listKq_scheme(whereSql.toString(), dataList, "", "", "");
        	
        	LazyDynaBean shemeBean = new LazyDynaBean();
        	for(int i=0;i<shemeBeanList.size();i++) {
        		shemeBean = shemeBeanList.get(i);
        		String scheme_id = (String)shemeBean.get("scheme_id");
        		if (StringUtils.isBlank(scheme_id) || StringUtils.isBlank((String)shemeBean.get("ctrl_param"))) {
        			continue;
        		}
        		String confirm_flag = (String) shemeBean.get("confirm_flag");
                //判断是否需要上报人事处 secondary_admin 0不需要 1需要
                String secondary_admin = (String) shemeBean.get("secondary_admin");
                
                ArrayList<HashMap> orglist = (ArrayList<HashMap>)shemeBean.get("org_map");
                HashMap schemeMap = new HashMap();
                schemeMap.put("scheme_id", scheme_id);
                schemeMap.put("scheme_name", (String)shemeBean.get("name"));
                
                ArrayList<HashMap<String, String>> org_List = new ArrayList<HashMap<String, String>>();
                for (HashMap map : orglist) {
                    boolean isHave = false;
                    HashMap<String, String> orgMap = new HashMap<String, String>();
                    if (StringUtils.isNotBlank(userName) && String.valueOf(map.get("reviewer_id")).equalsIgnoreCase(userName)) {
                        orgMap.put("role", String.valueOf(KqDataUtil.role_Agency_Reviewer));
                        if("0".equals(secondary_admin)) {
                            orgMap.put("hasNextApprover", "0");
                        }else{
                            orgMap.put("hasNextApprover", "1");
                        }
                        isHave = true;
                    }
                    if (String.valueOf(map.get("y_clerk_username")).equalsIgnoreCase(this.userView.getUserName())) {
                        orgMap.put("role", String.valueOf(KqDataUtil.role_Agency_Clerk));

                        if("0".equals(secondary_admin)){
                            String a0100=this.userView.getDbname()+this.userView.getA0100();
                            if(StringUtils.isBlank((String) map.get("reviewer_id"))){
                                orgMap.put("hasNextApprover","0");
                            }else{
                                if(StringUtils.isNotBlank(a0100)&&a0100.equalsIgnoreCase((String) map.get("reviewer_id"))){
                                    orgMap.put("hasNextApprover","0");
                                }else{
                                    orgMap.put("hasNextApprover","1");
                                }
                            }
                        }else{
                            orgMap.put("hasNextApprover","1");
                        }
                        isHave = true;
                    }
                    if (!isHave) {
                        continue;
                    }
                    
                    orgMap.put("create_time", String.valueOf(map.get("create_time")));
                    orgMap.put("org_id", String.valueOf(map.get("org_id")));
                    orgMap.put("org_name", String.valueOf(map.get("name")));
                    String clerk_username = String.valueOf(map.get("clerk_username"));
                    clerk_username = clerk_username.substring(clerk_username.indexOf("(") + 1, clerk_username.indexOf(")"));
        			orgMap.put("clerk_username", String.valueOf(map.get("y_clerk_username")));
                    orgMap.put("clerk_fullname", String.valueOf(clerk_username));
                    orgMap.put("reviewer_id", String.valueOf(map.get("reviewer_id")));
                    orgMap.put("reviewer_fullname", String.valueOf(map.get("reviewer")));
                    org_List.add(orgMap);
                }
                if (org_List.size() == 0) {
                    continue;
                }
                schemeMap.put("orgList", org_List);
                schemeMap.put("confirm_flag", confirm_flag);
                schemeMap.put("secondary_admin", secondary_admin);
                schemeMap.put("clerk_username", (String)shemeBean.get("clerk_username"));
                schemeMap.put("clerk_fullname", (String)shemeBean.get("clerk_fullname"));
            	schemeMap.put("reviewer_id", (String)shemeBean.get("reviewer_id"));
            	schemeMap.put("reviewer_fullname", (String)shemeBean.get("reviewer_fullname"));
                myScheme.add(schemeMap);	
        	}
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return myScheme;
    }

    /**
     * 发送考勤确认函邮件
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id      考勤机构id
     */
    private void sendKqConfirmLetter(int scheme_id, String kq_year, String kq_duration, String org_id) throws GeneralException {
        String sqlWhere = " and kq_year = ? and kq_duration = ? and scheme_id = ? and org_id like ?";
        ArrayList parameterList = new ArrayList();
        parameterList.add(kq_year);
        parameterList.add(kq_duration);
        parameterList.add(scheme_id);
        parameterList.add(org_id + "%");
        KqDataMxService kqDataMxService = new KqDataMxServiceImpl(this.userView, this.conn);
        ArrayList<LazyDynaBean> listQ35 = kqDataMxService.listQ35(sqlWhere, parameterList, "");

        String title = "";
        ArrayList periodList = new ArrayList();
        periodList.add(kq_year);
        periodList.add(kq_duration);
        PeriodService periodService = new PeriodServiceImpl(this.userView, this.conn);
        ArrayList<LazyDynaBean> listKq_duration = periodService.listKq_duration(" and kq_year = ? and kq_duration = ?", periodList, "");
        Date startDate = null;
        Date endDate = null;
        if (listKq_duration.size() > 0) {
            LazyDynaBean lazyDynaBean = listKq_duration.get(0);
            startDate = (Date) lazyDynaBean.get("kq_start");
            endDate = (Date) lazyDynaBean.get("kq_end");
        }
        if (startDate.getMonth() == endDate.getMonth()) {
            title = ResourceFactory.getProperty("kq.archive.scheme.kqconfirmletter1");
            title = title.replace("{year}", kq_year).replace("{month}", kq_duration);
        } else {
        	// 56733 取考勤期间范围错误
			title = DateUtils.format(startDate, "yyyy") + ResourceFactory.getProperty("datestyle.year")
					+ DateUtils.format(startDate, "MM") + ResourceFactory.getProperty("datestyle.month")
					+ DateUtils.format(startDate, "dd") + ResourceFactory.getProperty("datestyle.day")
					+ ResourceFactory.getProperty("label.to");
			if(!DateUtils.format(startDate, "yyyy").equals(DateUtils.format(endDate, "yyyy"))) {
				title += DateUtils.format(endDate, "yyyy") + ResourceFactory.getProperty("datestyle.year");
			}
			title += DateUtils.format(endDate, "MM") + ResourceFactory.getProperty("datestyle.month")
					+ DateUtils.format(endDate, "dd") + ResourceFactory.getProperty("datestyle.day");
        }
        HashMap kqClassAndItemsHM = kqDataMxService.getClassAndItems("", "0");
        SendEmailUtil sendEmailUtil = new SendEmailUtil(this.userView, this.conn, "30", "2720301");

        HashMap A01HM = this.getA01Items(scheme_id, kq_year, kq_duration, org_id);

        ArrayList<LazyDynaBean> listMessageBean = new ArrayList<LazyDynaBean>();
        for (int i = 0; i < listQ35.size(); i++) {
            LazyDynaBean lazyDynaBean = listQ35.get(i);
            ArrayList punchDataList = new ArrayList();
            String confirm = (String) lazyDynaBean.get("confirm");
            if (confirm != null && confirm.trim().length() > 0&&!"0".equals(confirm))//未确定考勤数据，推送待办通知
                continue;
            String guidkey = (String) lazyDynaBean.get("guidkey");
            for (int j = 1; j < 32; j++) {
                String itemid = j < 10 ? "q350" + j : "q35" + j;
                punchDataList.add(this.getClassAndItemsDesc(kqClassAndItemsHM, (String) lazyDynaBean.get(itemid)));
            }

            LazyDynaBean A01Bean = (LazyDynaBean) A01HM.get(guidkey);
            if (A01Bean == null)
                continue;
            ArrayList<LazyDynaBean> kqItemDataList = this.getkqItemData(lazyDynaBean.getMap(), scheme_id);
            String dayDetailEnabled = getDayDetailEnabled(scheme_id);
            this.removeHideColumns(kqItemDataList, dayDetailEnabled, kq_duration, kq_year, scheme_id);
            String confirmMemo = "";
            String confirmField = SystemConfig.getPropertyValue("confirm_memo");
            if(StringUtils.isNotBlank(confirmField)) {
        		FieldItem fieldItem = DataDictionary.getFieldItem(confirmField, "Q35");
        		if(fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
        			Object object = lazyDynaBean.get(confirmField.toLowerCase());
        			confirmMemo = (String) (object==null?"":object);
        		}
        	}
            String content = this.kqConfirmContent(title, startDate, endDate, punchDataList, kqItemDataList, 
            		String.valueOf(scheme_id), kq_year, kq_duration, org_id, A01Bean, "email", dayDetailEnabled, confirmMemo);
            LazyDynaBean emailBean = new LazyDynaBean();
            emailBean.set("title", title);
            emailBean.set("message", content);
            emailBean.set("send_user_name", this.userView.getUserName());
            emailBean.set("nbase", A01Bean.get("nbase"));
            emailBean.set("a0100", A01Bean.get("a0100"));
            listMessageBean.add(emailBean);
        }
        //发送考勤确认函邮件
        listMessageBean = sendEmailUtil.listMessageBeanBase(listMessageBean);
        sendEmailUtil.sendMsg(listMessageBean);
    }

    /**
     * 发送考勤确认待办通知
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id      考勤机构id
     */
    private void sendKqConfirmPendingTask(int scheme_id, String kq_year, String kq_duration, String org_id) throws GeneralException {
        String title = ResourceFactory.getProperty("kq.archive.scheme.kqconfirmpendingtask");
        title = title.replace("{year}", kq_year).replace("{month}", kq_duration);
        // 50437 按部门显示确认明细 
        String orgidDesc = AdminCode.getCodeName("UM", org_id);
        orgidDesc = StringUtils.isBlank(orgidDesc) ? AdminCode.getCodeName("UN", org_id) : orgidDesc;
    	String[] tileStr = title.split("月");
    	title = tileStr[0] +ResourceFactory.getProperty("kq.wizard.month")+ orgidDesc + tileStr[1];
    	
        String pending_url = "/module/utils/jsp.do?br_query=link&param=";
        String kq_url = "/kq/kqdata/kqdataconfirm?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kq_year=" + kq_year + "&kq_duration=" + kq_duration + "&org_id=" + PubFunc.encrypt(org_id) + "&label=show";
        pending_url = pending_url + SafeCode.encode(kq_url);
        String ext_flag = "KQ_CONFIRM_" + kq_year + kq_duration + "_" + scheme_id + "_" + PubFunc.encrypt(org_id);
        String sqlWhere = " and kq_year = ? and kq_duration = ? and scheme_id = ? and org_id like ?";
        ArrayList parameterList = new ArrayList();
        parameterList.add(kq_year);
        parameterList.add(kq_duration);
        parameterList.add(scheme_id);
        parameterList.add(org_id + "%");
        KqDataMxService kqDataMxService = new KqDataMxServiceImpl(this.userView, this.conn);
        ArrayList<LazyDynaBean> listQ35 = kqDataMxService.listQ35(sqlWhere, parameterList, "");
        HashMap A01HM = this.getA01Items(scheme_id, kq_year, kq_duration, org_id);
        ArrayList list = new ArrayList();
        KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
        HashSet<String> receivers = kqDataUtil.listUnFinishPengdingTask(new ContentDAO(this.conn), ext_flag);
        for (int i = 0; i < listQ35.size(); i++) {
            LazyDynaBean lazyDynaBean = listQ35.get(i);
            String confirm = (String) lazyDynaBean.get("confirm");
            LazyDynaBean A01Bean = (LazyDynaBean) A01HM.get((String) lazyDynaBean.get("guidkey"));
            String nbase = (String) A01Bean.get("nbase");
            String receiver = nbase.toUpperCase().substring(0, 1) + nbase.toLowerCase().substring(1) + (String) A01Bean.get("a0100");
            //confirm 为空 时，或者未确认时还未发送过代办的
            if ((StringUtils.isEmpty(confirm) || "0".equals(confirm)) && !receivers.contains(receiver.toLowerCase())) {
                list.add(receiver);
            }
        }
        if (list.size() > 0) {
            kqDataUtil.kqSendPengdingTask(this.conn, title, scheme_id, kq_year, kq_duration, org_id, KqDataUtil.TASKTYPE_CONFIRM, KqDataUtil.role_Agency_Clerk, list, null);
        }
    }

    /**
     * 发布后，修改 考勤确认标识confirm 值
     *
     * @param type        确认标识 值
     * @param scheme_id   考勤方案 id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id      考勤机构id
     */
    private void updateKqDataConfirm(int type, int scheme_id, String kq_year, String kq_duration, String org_id) {
        String sql = "update Q35 set confirm = ? where kq_year = ? and kq_duration = ? and scheme_id = ? and org_id = ? and confirm is null ";
        ArrayList list = new ArrayList();
        list.add(type);
        list.add(kq_year);
        list.add(kq_duration);
        list.add(scheme_id);
        list.add(org_id);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(sql, list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限下数据上报机构明细
     *
     * @param scheme_id 考勤方案id
     * @param year      考勤年份
     * @param status    数据显示状态      00 全部  | 01 待办
     * @param schemeMap 考勤方案信息
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 15:42 2019/1/3
     */
    private LinkedHashMap<String, LazyDynaBean> kqDataOrgData(String scheme_id, String year, String status
    		, HashMap<String, Object> schemeMap) throws GeneralException {
        LinkedHashMap<String, LazyDynaBean> kqDataOrgMap = new LinkedHashMap<String, LazyDynaBean>();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            ArrayList<HashMap> orglist = (ArrayList<HashMap>) schemeMap.get("orgList");
            //是否需要确认 1需要 0不需要
            String confirmFlag = "1";
            if (schemeMap.containsKey("confirm_flag") && schemeMap.get("confirm_flag") != null) {
                confirmFlag = String.valueOf(schemeMap.get("confirm_flag"));
            }
            //是否需要上报人事处 1需要 0不需要
            String secondary_admin = "1";
            if (schemeMap.containsKey("secondary_admin") && schemeMap.get("secondary_admin") != null) {
                secondary_admin = String.valueOf(schemeMap.get("secondary_admin"));
            }
            sql.append(" SELECT kl.Kq_year ,kl.Kq_duration ,kl.Org_id ,kl.Sp_flag ,kl.Curr_user ,kl.Appuser ,q.num ,q.confirmnum  FROM kq_extend_log kl ");
            sql.append(" LEFT JOIN (SELECT COUNT(*) AS num ,SUM(CASE  WHEN q35.confirm=1 or q35.confirm=2 then 1 ELSE 0 END ) AS confirmnum,");
            sql.append("$subOrg$ as org_id,kq_duration,kq_year,scheme_id FROM Q35 ");
            sql.append(" WHERE Q35.kq_year=? AND Q35.scheme_id=? and org_id like '$orgValue$%'");
            sql.append(" GROUP BY kq_duration,kq_year, scheme_id) q ON q.org_id=kl.Org_id AND q.kq_duration = kl.Kq_duration ");
            sql.append(" WHERE  kl.Kq_year=? AND kl.Scheme_id=? AND kl.org_id='$orgValue$'");
            // 不显示未下发状态的数据 即 sp_flag='08'
            sql.append(" AND kl.sp_flag<>'08'");
            ArrayList parameterList = new ArrayList();
            parameterList.add(year);
            parameterList.add(scheme_id);
            parameterList.add(year);
            parameterList.add(scheme_id);
            String a0100=userView.getDbname()+userView.getA0100();
            String userName="";
            if(userView.getStatus()==0){
                userName=userView.getUserName();
            }else if(StringUtils.isNotBlank(userView.getS_userName())){
                userName=userView.getS_userName();
            }
            // 54721 流程优化后 待办条件更改
            if("01".equals(status)){
                sql.append( " AND ((UPPER(kl.Curr_user)=UPPER(?)  ");
                if(StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(a0100)){
                    sql.append(" or UPPER(kl.Curr_user)=UPPER(?) ");
                    parameterList.add(userName);
                    parameterList.add(a0100);
                }else if(StringUtils.isNotBlank(a0100)){
                    parameterList.add(a0100);
                }else{
                    parameterList.add(userName);
                }
                sql.append( " or (appuser like ? ");
                if(StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(a0100)){
                    sql.append(" or appuser like ?");
                    parameterList.add("%;"+userName+";");
                    parameterList.add("%;"+a0100+";");
                }else if(StringUtils.isNotBlank(a0100)){
                    parameterList.add("%;"+a0100+";");
                }else{
                    parameterList.add("%;"+userName+";");
                }
                sql.append(")");
                sql.append(" AND kl.Sp_flag<>'06'");
                sql.append(")");
                // 55502 批准后 可能会存在归档功能授权  故增加校验
                sql.append(" or (kl.Sp_flag='03' ");
                sql.append(" AND (appuser like ? ");
                if(StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(a0100)){
                    sql.append(" or appuser like ?");
                    parameterList.add("%;"+userName+";%");
                    parameterList.add("%;"+a0100+";%");
                }else if(StringUtils.isNotBlank(a0100)){
                    parameterList.add("%;"+a0100+";%");
                }else{
                    parameterList.add("%;"+userName+";%");
                }
                sql.append(")");
                sql.append(")");
                
                sql.append(")");
            }else{
                sql.append(" AND (appuser like ? ");
                if(StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(a0100)){
                    sql.append(" or appuser like ?");
                    parameterList.add("%;"+userName+";%");
                    parameterList.add("%;"+a0100+";%");
                }else if(StringUtils.isNotBlank(a0100)){
                    parameterList.add("%;"+a0100+";%");
                }else{
                    parameterList.add("%;"+userName+";%");
                }
                sql.append(")");
            }
            // 50312 增加排序
            sql.append(" order by kl.scheme_id,kl.kq_year,kl.Kq_duration DESC ");
            // 上报页面归档权限校验
            boolean submitPiv = this.userView.hasTheFunction("272030105");
            String sqlstr = "";
            for (HashMap hashMap : orglist) {
                if(hashMap.get("org_id")==null)
                    continue;
                String orgId = (String) hashMap.get("org_id");
                if(StringUtils.isEmpty(orgId))
                    continue;
                String orgName = (String)hashMap.get("org_name");
                String hasNextApprover=(String) hashMap.get("hasNextApprover");
                int role = Integer.parseInt((String) hashMap.get("role"));
                if (Sql_switcher.searchDbServer()== Constant.ORACEL) {
                	 sqlstr = sql.toString().replaceAll("\\$orgValue\\$",orgId).replace("$subOrg$",Sql_switcher.substr("max(org_id)","0",orgId.length()+""));
				}else {
					sqlstr = sql.toString().replaceAll("\\$orgValue\\$",orgId).replace("$subOrg$",Sql_switcher.substr("max(org_id)","0",orgId.length()+1+""));
				}
                rs = dao.search(sqlstr, parameterList);
                while(rs.next()){
                    LazyDynaBean bean = new LazyDynaBean();
                    String curr_user = rs.getString("Curr_user");
                    String sp_flag = rs.getString("Sp_flag");
                    String status_name = getKqFlagDesc(role, secondary_admin, sp_flag, curr_user, rs.getString("Appuser"), bean);
                    String confirms = "0";
                    if (StringUtils.isBlank(rs.getString("confirmnum")) || "0".equals(rs.getString("confirmnum"))) {
                        if ("1".equals(confirmFlag)) {
                            confirms = "";
                        }
                    } else {
                        confirms = rs.getString("confirmnum");
                    }
                    bean.set("scheme_id", scheme_id);
                    bean.set("hasNextApprover", hasNextApprover);
                    bean.set("scheme_name", schemeMap.get("scheme_name"));
                    bean.set("kq_year", rs.getString("Kq_year"));
                    bean.set("kq_duration", rs.getString("Kq_year") + "." + rs.getString("Kq_duration"));
                    bean.set("org_id", orgId);
                    bean.set("org_name", orgName);
                    bean.set("status", sp_flag);
                    bean.set("status_name", status_name);
                    bean.set("confirms", confirms);
                    bean.set("participants", KqDataUtil.nullif(rs.getString("num")));
                    bean.set("curr_user", StringUtils.isEmpty(curr_user) ? "" : curr_user);
                    kqDataOrgMap.put(rs.getString("Kq_duration") + ":" + orgId, bean);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return kqDataOrgMap;

    }

    @Override
    public HashMap getA01Items(int scheme_id, String kq_year, String kq_duration, String org_id) throws GeneralException {
        HashMap map = new HashMap();
        String username = ConstantParamter.getLoginUserNameField();
        String password = ConstantParamter.getLoginPasswordField();
        // 58529 现在取全部人员库 与获取明细数据 规则保持一致 不然导致移库的人不在方案所选库中  再次查询不到
        ArrayList<String> nbases =DataDictionary.getDbpreList();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        for (int i = 0; i < nbases.size(); i++) {
            String nbase = (String)nbases.get(i);
            sql.append("select distinct '" + nbase + "' as nbase,a.a0100,a." + username + " as username,a." + password + " as password,a.guidkey as guidkey ");
            sql.append("from " + nbase + "A01 a,Q35 q ");
            sql.append("where a.guidkey = q.guidkey ");
            sql.append("and q.kq_year = ? and q.kq_duration = ? and q.scheme_id = ? and q.org_id like ? ");
            sql.append("union all ");
            list.add(kq_year);
            list.add(kq_duration);
            list.add(scheme_id);
            list.add(org_id + "%");
        }
        if (sql.length() > 0)
            sql.setLength(sql.length() - 10);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("nbase", rs.getString("nbase"));
                bean.set("a0100", rs.getString("a0100"));
                bean.set("username", rs.getString("username"));
                bean.set("password", rs.getString("password"));
                map.put(rs.getString("guidkey"), bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取人员主集信息出错
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.date.appeal.error.getA01InfoMsg")));
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return map;
    }


    /**
     * 获取月考勤项汇总数据
     *
     * @param map
     * @return
     */
    private ArrayList<LazyDynaBean> getkqItemData(Map map, int scheme_id) throws GeneralException {
        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        try {
            //应出勤
            LazyDynaBean Q3533Bean = new LazyDynaBean();
            String q3533Value = String.valueOf(map.get("q3533"));
            q3533Value = q3533Value.trim().length() == 0 ? "0" : q3533Value;
            Q3533Bean.set("item_id", "q3533");
            Q3533Bean.set("item_unit", "");
            Q3533Bean.set("value", q3533Value);
            Q3533Bean.set("item_name", DataDictionary.getFieldItem("Q3533").getItemdesc());

            //实出勤
            LazyDynaBean Q3535Bean = new LazyDynaBean();
            String q3535Value = String.valueOf(map.get("q3535"));
            q3535Value = q3535Value.trim().length() == 0 ? "0" : q3535Value;
            Q3535Bean.set("item_id", "q3535");
            Q3535Bean.set("item_unit", "");
            Q3535Bean.set("value", q3535Value);
            Q3535Bean.set("item_name", DataDictionary.getFieldItem("Q3535").getItemdesc());

            /**
             * 获取 考勤方案中配置的 具有统计指标 或者 计算公式的考勤项目
             */
            SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.conn, this.userView);
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, null);
            if (schemeList.size() == 0) {
                return list;
            }
            
            ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Q35", Constant.USED_FIELD_SET);
            LazyDynaBean schemeBean = schemeList.get(0);
            String[] itemList = StringUtils.split(String.valueOf(schemeBean.get("item_ids")), ",");
            // 57022 没有考勤项目时直接返回
            if(StringUtils.isBlank(itemList.toString()) || itemList.length == 0) {
            	list.add(Q3533Bean);
                list.add(Q3535Bean);
            	return list;
            }
            StringBuffer strWhere = new StringBuffer(" and item_id in(");
            parameterList.clear();
            for (String s : itemList) {
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                parameterList.add(s);
                strWhere.append("?,");
            }
            strWhere.deleteCharAt(strWhere.length() - 1);
            strWhere.append(")");
            strWhere.append(" and nullIf(fielditemid,'') is not null ");
            KqItemService kqItemService = new KqItemServiceImpl(this.userView, this.conn);
            ArrayList<LazyDynaBean> kq_ItemList = kqItemService.listKqItem(strWhere.toString(), parameterList, null);

            for (LazyDynaBean itemBean : kq_ItemList) {
                LazyDynaBean bean = new LazyDynaBean();
                String item_id = String.valueOf(itemBean.get("item_id"));
                String fielditemid = String.valueOf(itemBean.get("fielditemid"));

                String value = "0";
                if (map.containsKey(fielditemid.toLowerCase()) && map.get(fielditemid.toLowerCase()) != null) {
                    value = "".equals(map.get(fielditemid.toLowerCase())) ? "0" : String.valueOf(map.get(fielditemid.toLowerCase()));
                }
                if ("Q3533".equalsIgnoreCase(fielditemid)) {
                    Q3533Bean.set("item_unit", itemBean.get("item_unit"));
                    Q3533Bean.set("value", value);
                    continue;
                }
                if ("Q3535".equalsIgnoreCase(fielditemid)) {
                    Q3535Bean.set("item_unit", itemBean.get("item_unit"));
                    Q3535Bean.set("value", value);
                    continue;
                }
                String itemdesc = (String)itemBean.get("item_name");
                // 46172 获取考勤项目对应Q35指标描述
                for (FieldItem item : fieldList) {
                	if(!item.getItemid().equalsIgnoreCase(fielditemid)) {
                	    continue;
                	}
                	
                	itemdesc = item.getItemdesc();
                	if("A".equalsIgnoreCase(item.getItemtype()) && !"0".equalsIgnoreCase(item.getCodesetid())) {
                	    value = AdminCode.getCodeName(item.getCodesetid(), value);
                	    value = StringUtils.isEmpty(value) ? ResourceFactory.getProperty("kq.data.sp.nothing") : value;
                	}
                	
                	break;
                }
                bean.set("item_id", item_id);
                bean.set("item_name", itemdesc);
                bean.set("item_unit", itemBean.get("item_unit"));
                bean.set("value", value);
                list.add(bean);

            }

            list.add(0, Q3533Bean);
            list.add(Q3535Bean);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }



    /**
     * 每日打卡代码 转 描述
     *
     * @param kqClassAndItemsHM 所有打卡项 集合
     * @param fielditem         每日打卡项
     * @return
     */
    private ArrayList getClassAndItemsDesc(HashMap kqClassAndItemsHM, String fielditem) {
        ArrayList list = new ArrayList();
        if (fielditem == null || fielditem.trim().length() == 0)
            return list;
        String[] fielditems = fielditem.split(",");
        for (int i = 0; i < fielditems.length; i++) {
            if (StringUtils.isBlank(fielditems[i])) {
                continue;
            }
            LazyDynaBean bean = new LazyDynaBean();
            if (fielditems[i].startsWith("C")) {//班次
                LazyDynaBean lazyDynaBean = (LazyDynaBean) kqClassAndItemsHM.get(fielditems[i]);
                if(lazyDynaBean==null){
                    continue;
                }
//				int id =(Integer) lazyDynaBean.get("class_id");
                String name = (String) lazyDynaBean.get("abbreviation");
                if (StringUtils.isBlank(name)) {
                    name = (String) lazyDynaBean.get("name");
                }
                String color = (String) lazyDynaBean.get("color");
                bean.set("type", "C");
                bean.set("name", name);
                bean.set("color", color);

            } else if (fielditems[i].startsWith("I")) {//项目
                LazyDynaBean lazyDynaBean = (LazyDynaBean) kqClassAndItemsHM.get(fielditems[i]);
                if(lazyDynaBean==null){
                    continue;
                }
//				int id = (Integer) lazyDynaBean.get("item_id");
                String name = (String) lazyDynaBean.get("item_name");
                String color = (String) lazyDynaBean.get("item_color");
                String symbol = (String) lazyDynaBean.get("item_symbol");
                bean.set("type", "I");
                bean.set("name", name);
                bean.set("color", color);
                bean.set("item_symbol", symbol);
            }
            list.add(bean);
        }
        return list;
    }

    /**
     * 获取考勤方案年限
     *
     * @return 考勤方案集合
     * @throws GeneralException
     * @throws SQLException     异常抛出
     * @author ZhangHua
     * @date 13:23 2018/11/29
     */
    private ArrayList listSchemeKqYear(String scheme_id, String kq_duration, String org_id) throws GeneralException {
        ArrayList yearList = new ArrayList();
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select kq_year from kq_extend_log k ");
        sql.append("where 1=1 ");
        if (StringUtils.isNotBlank(scheme_id)) {
            sql.append("and k.schdme_id = ? ");
            list.add(Integer.parseInt(scheme_id));
        }
        if (StringUtils.isNotBlank(kq_duration)) {
            sql.append("and k.kq_duration = ? ");
            list.add(kq_duration);
        }
        if (StringUtils.isNotBlank(org_id)) {
            sql.append("and k.org_id = ? ");
            list.add(org_id);
        }
        sql.append("and k.appuser like ? ");
        if (this.userView.getStatus() == 0) //业务
            list.add("%;" + this.userView.getUserName() + ";%");
        else if (this.userView.getStatus() == 4)//自助
            list.add("%;" + this.userView.getDbname() + this.userView.getA0100() + ";%");

        sql.append(" group by kq_year order by kq_year desc");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                yearList.add(Integer.parseInt(rs.getString("kq_year")));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            //获取考勤数据上报方案年份区间出错
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.date.appeal.error.getKqSchemeDataArichveYearsMsg")));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return yearList;
    }

    /**
     * 获取自助用户guidkey
     *
     * @param nbase 人员库
     * @param A0100 人员编号
     * @return guidkey 人员唯一标识
     * @throws SQLException 抛出异常
     */
    private String getGuidkey(String nbase, String A0100) {
        String guidkey = "";
        String sql = "select guidkey from " + nbase + "A01 where 1=1 and A0100=?";
        ArrayList list = new ArrayList();
        list.add(A0100);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql, list);
            if (rs.next())
                guidkey = rs.getString("guidkey");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return guidkey;
    }

    /**
     * 状态码转 描述汉字
     * @param role 身份 关联kqdataUtil常量
     * @param secondary_admin 是否需要报批人事处 0 不需要 1需要
     * @param codeitemid 审批状态
     * @param curr_user 当前审批人
     * @param appuser 审批过程
     * @param bean
     * @return
     * @author ZhangHua
     * @date 16:34 2019/1/22
     */
    private String getKqFlagDesc(int role, String secondary_admin, String codeitemid, String curr_user, String appuser, LazyDynaBean bean) {
        String desc = "";
        String a0100=userView.getDbname()+userView.getA0100();
        String userName="";
        if(userView.getStatus()==0){
            userName=userView.getUserName();
        }else if(StringUtils.isNotBlank(userView.getS_userName())){
            userName=userView.getS_userName();
        }
        /**
         *linbz 优化流程
         * 兼容老的 
         */
        if(!",1,2,3,4,".contains(","+curr_user+",")) {
        	desc = getKqFlagDescOld(role, secondary_admin, codeitemid, curr_user, appuser
            		, bean, userName, a0100);
        	return desc;
        }
        
        String rolestr = String.valueOf(role);
        //机构考勤员
        if (role == KqDataUtil.role_Agency_Clerk) {
            /**
             * 考勤员 全部
             * 已提交：已上报至人事处考勤员  02 07 不是当前考勤员
             * 退回：人事处考勤员退回给审核人  07 当前考勤人
             * 未提交：考勤员上报至审核员   01  当前审核人
             * 已批准：人事处考勤员（没设置审核人）同意或人事处审核人同意  03
             * 已归档：考勤表数据已同步至人员信息库  06
             */
            if (("02".equalsIgnoreCase(codeitemid) || "07".equalsIgnoreCase(codeitemid))
            		 && !curr_user.equalsIgnoreCase(rolestr)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.submit");
                bean.set("operation", "0");
            } else if ("07".equalsIgnoreCase(codeitemid) && curr_user.equalsIgnoreCase(rolestr)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.return");
                bean.set("operation", "1");
            } else if ("01".equalsIgnoreCase(codeitemid) && curr_user.equalsIgnoreCase(rolestr)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.unsubmit");
                bean.set("operation", "1");
            } else if ("03".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.approval");
                if("0".equals(secondary_admin)){
                    bean.set("operation", "2");
                }else{
                    bean.set("operation", "0");
                }
            } else if ("06".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.arichve");
                bean.set("operation", "0");
            }
        } else
            //机构审核人
        if (role == KqDataUtil.role_Agency_Reviewer) {

            /**
             * 审核员
             * 待批：已上报至人事处或审核人  02   当前审核员
             * 退回：人事处考勤员或机构审核人退回给考勤员  07 当前审核人
             * 已提交：人事处发布考勤表后未上报    02 07 不是当前审核员 也不是 考勤员
             * 已批准：人事处考勤员（没设置审核人）同意或人事处审核人同意  03
             * 已归档：考勤表数据已同步至人员信息库   06
             * 未提交：  01  | 02  初始人 | 07 初始人
             */
            if ("02".equalsIgnoreCase(codeitemid) && rolestr.equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.pending.approval");
                bean.set("operation", "1");
            } else if ("07".equalsIgnoreCase(codeitemid) && rolestr.equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.return");
                bean.set("operation", "1");
            } else if (",01,02,07,".contains(","+codeitemid+",") && "3".equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.unsubmit");
                bean.set("operation", "0");
            } else if (("02".equalsIgnoreCase(codeitemid) || "07".equalsIgnoreCase(codeitemid)) 
            		&& !rolestr.equalsIgnoreCase(curr_user) && ",1,2,".contains(","+curr_user+",")) {
                desc = ResourceFactory.getProperty("kq.data.appeal.submit");
                bean.set("operation", "0");
            } else if ("03".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.approval");
                if("0".equals(secondary_admin)){
                    bean.set("operation", "2");
                }else{
                    bean.set("operation", "0");
                }
            } else if ("06".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.arichve");
                bean.set("operation", "0");
            }

        }
        return desc;
    }
    /**
     * 兼容老的流程中 curr_user存 用户名的情况
     * getKqFlagDescOld
     * @param role
     * @param secondary_admin
     * @param codeitemid
     * @param curr_user
     * @param appuser
     * @param bean
     * @param userName
     * @param a0100
     * @return
     * @date 2019年4月9日 下午2:24:56
     * @author linbz
     */
    private String getKqFlagDescOld(int role, String secondary_admin, String codeitemid, String curr_user, String appuser
    		, LazyDynaBean bean, String userName, String a0100) {
    	
        String desc = "";
        //机构考勤员
        if (role == KqDataUtil.role_Agency_Clerk) {
            /**
             * 考勤员 全部
             * 已提交：已上报至人事处考勤员  02 07 不是当前考勤员
             * 退回：人事处考勤员退回给审核人  07 当前考勤人
             * 未提交：考勤员上报至审核员   01  当前审核人
             * 已批准：人事处考勤员（没设置审核人）同意或人事处审核人同意  03
             * 已归档：考勤表数据已同步至人员信息库  06
             */
            if (("02".equalsIgnoreCase(codeitemid) || "07".equalsIgnoreCase(codeitemid)) && !appuser.equalsIgnoreCase(";"+userName+";")) {
                desc = ResourceFactory.getProperty("kq.data.appeal.submit");
                bean.set("operation", "0");
            } else if ("07".equalsIgnoreCase(codeitemid) && userName.equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.return");
                bean.set("operation", "1");
            } else if ("01".equalsIgnoreCase(codeitemid) && userName.equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.unsubmit");
                bean.set("operation", "1");
            } else if ("03".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.approval");
                if("0".equals(secondary_admin)){
                    bean.set("operation", "2");
                }else{
                    bean.set("operation", "0");
                }
            } else if ("06".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.arichve");
                bean.set("operation", "0");
            }
        } else
            //机构审核人
        if (role == KqDataUtil.role_Agency_Reviewer) {

            /**
             * 审核员
             * 待批：已上报至人事处或审核人  02   当前审核员
             * 退回：人事处考勤员或机构审核人退回给考勤员  07 当前审核人
             * 已提交：人事处发布考勤表后未上报    02 07 不是当前审核员 也不是 考勤员
             * 已批准：人事处考勤员（没设置审核人）同意或人事处审核人同意  03
             * 已归档：考勤表数据已同步至人员信息库   06
             * 未提交：  01  | 02  初始人 | 07 初始人
             */
            if ("02".equalsIgnoreCase(codeitemid) && a0100.equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.pending.approval");
                bean.set("operation", "1");

            } else if ("07".equalsIgnoreCase(codeitemid) && a0100.equalsIgnoreCase(curr_user)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.return");
                bean.set("operation", "1");
            } else if ("01".equalsIgnoreCase(codeitemid) || ("02".equalsIgnoreCase(codeitemid) && appuser.startsWith(";" + curr_user + ";")) || ("07".equalsIgnoreCase(codeitemid) && appuser.startsWith(";" + curr_user + ";"))) {
                desc = ResourceFactory.getProperty("kq.data.appeal.unsubmit");
                bean.set("operation", "0");
            } else if (("02".equalsIgnoreCase(codeitemid) || "07".equalsIgnoreCase(codeitemid)) && appuser.indexOf(";" + a0100 + ";") != -1) {
                desc = ResourceFactory.getProperty("kq.data.appeal.submit");
                bean.set("operation", "0");
            } else if ("03".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.approval");
                if("0".equals(secondary_admin)){
                    bean.set("operation", "2");
                }else{
                    bean.set("operation", "0");
                }
            } else if ("06".equalsIgnoreCase(codeitemid)) {
                desc = ResourceFactory.getProperty("kq.data.appeal.arichve");
                bean.set("operation", "0");
            }

        }
        return desc;
    }
    /**
     * 考勤确认函 html 生成方法
     *
     * @param title         考勤标题
     * @param startDate     考勤起始时间
     * @param endDate       考勤结束时间
     * @param punchDataList 打卡数据集合
     * @param kqDataList    考勤项数据集合
     * @param scheme_id     考勤方案id
     * @param kq_year       考勤年份
     * @param kq_duration   考勤期间
     * @param org_id        考勤机构编号
     * @param A01Bean       考勤用户的 账号和密码
     * @param label         标识 email | pendingtask  邮件 |待办 标识
     * @param confirmMemo 
     * @return 拼接html 代码
     */
    private String kqConfirmContent(String title, Date startDate, Date endDate, ArrayList punchDataList, 
    		ArrayList<LazyDynaBean> kqDataList, String scheme_id, String kq_year, String kq_duration, 
    		String org_id, LazyDynaBean A01Bean, String label, String dayDetailEnabled, String confirmMemo) {
        StringBuffer content = new StringBuffer();
        try {
            String logonUrl = SystemConfig.getPropertyValue("hrp_logon_url");
            //个人确认说明指标
            String confirmField = SystemConfig.getPropertyValue("confirm_memo");
            
            String token = "";
            String nbase = "";
            String a0100 = "";
            if (A01Bean != null) {
                token = PubFunc.encrypt(A01Bean.get("username") + "," + A01Bean.get("password"));
                nbase = (String) A01Bean.get("nbase");
                a0100 = (String) A01Bean.get("a0100");
            } else {
                nbase = this.userView.getDbname();
                a0100 = this.userView.getA0100();
            }
            
            String guidkey = "";
            if(StringUtils.isNotEmpty(nbase)) {
                guidkey = this.getGuidkey(nbase, a0100);
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sendTime = sdf.format(new Date());
            content.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            content.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            content.append("<head>\n");
            content.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />");
            content.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
            content.append("<title>" + ResourceFactory.getProperty("kq.archive.scheme.kqconfirmtitle") + "</title>\n");
            content.append("<style type=\"text/css\">");
            content.append("body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,table,tr,td,img,div,dl,dt,dd,span{margin:0;padding:0; border:none;}\n" + 
            		"body{color:#4C4C4C;font-size:12px;font-family:\"微软雅黑\";widtd:100%; background:#fff;}\n" + 
            		"ul,ol{list-style-type:none;}\n" + 
            		"select,input,img{vertical-align:middle;}\n" + 
            		"a{text-decoration:none;}\n" + 
            		".clearit{clear:both; font-size:0;height:0;width:0;padding:0;margin:0;border:0;}\n" + 
            		".bh-clear{clear:both;}\n" + 
            		".bh-space{height:10px;clear:both;}\n" + 
            		".hj-kq-all{width:862px;margin:0 auto;margin-top:10px;}\n" + 
            		".hj-kq-all-one{width:852px;height:110px;  border:1px #d1d1d1 solid;}\n" + 
            		".hj-kq-all-one-riqi{width:400px;height:40px; margin:25px 0 0 228px;}\n" + 
            		".hj-kq-all-one-riqi a,h2{ float:left;}\n" + 
            		".hj-kq-all-one-riqi a{margin: 0 10px;}\n" + 
            		".hj-kq-all-one-riqi h2{margin:-10px 10px 0 10px; font-size:28px; font-weight:normal;}\n" + 
            		".hj-kq-all-one-xingqi{ font-size:16px;margin-top:10px;}\n" + 
            		".hj-kq-all-one-xingqi td{ float:right; text-align:right; font-size:14px; width:107px;margin-right:15px;}\n" + 
            		".hj-lbiao1-1{width:122px;height:118px;box-sizing: border-box; border-left:1px #d1d1d1 solid;border-bottom:1px #d1d1d1 solid; float:left;}\n" + 
            		".hj-lbiao1-1 a{ font-size:14px;color:#333333; margin-top:4px;margin-right:10px;}\n" + 
            		".hj-lbiao1-2{width:122px;height:118px;box-sizing: border-box; border-left:1px #d1d1d1 solid;border-bottom:1px #d1d1d1 solid; float:left; background:#e2e2e2;}\n" +
            		".hj-lbiao1-2 a{ font-size:14px;color:#666; float:right; margin-top:4px;margin-right:10px;}\n" + 
            		".hj-lbiao1-3{width:145px;height:118px; border-left:1px #d1d1d1 solid;border-bottom:1px #d1d1d1 solid; float:left;}\n" + 
            		".hj-lbiao1-3 a{ font-size:14px;color:#666; float:left; margin-top:2px;margin-left:98px; background:url(../images/kq/kqdata/jin.png) no-repeat;width:24px;height:24px; text-align:center; line-height:24px;color:#FFF;}\n" + 
            		".hj-lbiao1-3 p{ font-size:14px;color:#666; float:right; margin-top:4px;width:20px; height:20px;}\n" + 
            		".hj-item{width:122px;height:22px;margin:0 0 4px 8px;}\n" + 
            		".hj-item a{width:96px;height:22px;display:inline-block;padding-left:10px; line-height:22px;color:#FFF;}\n" + 
            		".hj-xiawu{width:140px;height:24px;margin:0px 0 0 8px;}\n" + 
            		".hj-xiawu a{width:120px;height:24px; background:#30b3f7;display:inline-block;padding-left:10px; line-height:24px;color:#FFF;}\n" + 
            		".hj-jiaban{width:140px;height:24px;margin:0px 0 0 8px;}\n" + 
            		".hj-jiaban a{width:120px;height:24px; background:#f2c722;display:inline-block;padding-left:10px; line-height:24px;color:#FFF;}\n" + 
            		".hj-kq-all-three{width:862px;height:110px;border-left:1px #d1d1d1 solid;}\n" + 
            		".hj-kq-all-three p{ text-align:center;width:122px;height:40px; color:#333; font-size:14px;margin-top:24px;}\n" + 
            		".hj-kq-all-three .hj-scq{width:36px;height:36px; background:url(../images/kq/kqdata/chuqin_bg.png) no-repeat;color:#FFF; display:inline-block; text-align:center; line-height:36px; font-size:16px; font-weight:bolder;margin-top:10px;}\n" + 
            		"hj-kq-all-three .hj-tsp{width:172px;}\n" + 
            		".hj-kq-all-three a{width:36px;height:36px; color:#30b3f7;display:inline-block; text-align:center; line-height:36px; font-size:16px; font-weight:bolder;margin-top:10px;}\n" + 
            		".hj-kq-all-four{ width:860px; text-align:center;margin-top:10px;height:54px}\n" + 
            		".hj-button-enter{width:150px; height:44px;border-radius:6px;background-color:#30b3f7; color:#FFF; text-align:center; line-height:44px; border:none; font-size:20px;}\n" + 
            		".hj-kq-confirm{border:1px #d1d1d1 solid;padding:2px;width:848px;margin-top:10px}\n" + 
            		".hj-kq-more-item{width:100%;display:inline-block; text-align:center; font-size:16px;padding:0px;margin:0px; font-weight:bolder;cursor:default;height:20px;text-overflow:ellipsis;overflow:hidden; }\n" + 
            		"");
            content.append("</style>");
            content.append("</head>\n");
            content.append("<body>\n");
            content.append("<table style='display:none;'></table>\n");
            
            if(("1".equals(dayDetailEnabled) && "email".equalsIgnoreCase(label)) || "show".equalsIgnoreCase(label))
                content.append("<div class=\"hj-kq-all\" style='width:862px;margin:0 auto;margin-top: 10px;'>\n");
            
            if ("email".equalsIgnoreCase(label)) {
                content.append("	<div class=\"hj-kq-all-one\" style='height:50px;border:1px #d1d1d1 solid;'>\n");
            } else {
                content.append("	<div class=\"hj-kq-all-one\" style='height:110px;border:1px #d1d1d1 solid;'>\n");
                content.append("		<div class=\"hj-kq-all-one-riqi\" style='width:400px;height:40px;margin:25px 0 0 228px;' ><div style='text-align:center;font-size:25px;font-weight:400;font-family:\"微软雅黑;\"'>" + title + "</div></div>\n");
            }
            
            if("1".equals(dayDetailEnabled)) {
                content.append("		<div style='font-size:16px;margin-top:10px;text-align:center;' >\n");
                content.append("			<table width=\"860\" border=\"0\">\n");
                if ("email".equalsIgnoreCase(label)) {
                    content.append("				<tr align='right'>\n");
                    content.append("					<td style='width:110px;font-size:14px;font-family:\"微软雅黑\";padding-right:8px;'>" + ResourceFactory.getProperty("kq.archive.scheme.monday") + "</td>\n");
                    content.append("					<td style='width:112px;font-size:14px;font-family:\"微软雅黑\";padding-right:8px;'>" + ResourceFactory.getProperty("kq.archive.scheme.tuesday") + "</td>\n");
                    content.append("					<td style='width:112px;font-size:14px;font-family:\"微软雅黑\";padding-right:8px;'>" + ResourceFactory.getProperty("kq.archive.scheme.wednesday") + "</td>\n");
                    content.append("					<td style='width:111px;font-size:14px;font-family:\"微软雅黑\";padding-right:9px;'>" + ResourceFactory.getProperty("kq.archive.scheme.thursday") + "</td>\n");
                    content.append("					<td style='width:112px;font-size:14px;font-family:\"微软雅黑\";padding-right:8px;''width:107px;font-size:14px;font-family:\"微软雅黑\"'>" + ResourceFactory.getProperty("kq.archive.scheme.firday") + "</td>\n");
                    content.append("					<td style='width:111px;font-size:14px;font-family:\"微软雅黑\";padding-right:9px;'>" + ResourceFactory.getProperty("kq.archive.scheme.Saturday") + "</td>\n");
                    content.append("					<td style='width:110px;font-size:14px;font-family:\"微软雅黑\";padding-right:14px;'>" + ResourceFactory.getProperty("kq.archive.scheme.sunday") + "</td>\n");
                    content.append("				</tr>\n");
                } else {
                    content.append("				<tr>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.monday") + "</td>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.tuesday") + "</td>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.wednesday") + "</td>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.thursday") + "</td>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.firday") + "</td>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.Saturday") + "</td>\n");
                    content.append("					<td>" + ResourceFactory.getProperty("kq.archive.scheme.sunday") + "</td>\n");
                    content.append("				</tr>\n");
                }
                content.append("			</table>\n");
                content.append("		</div>\n");
            }
            
            if(("1".equals(dayDetailEnabled) && "email".equalsIgnoreCase(label)) || "show".equalsIgnoreCase(label))
                content.append("	</div>\n");
            
            content.append("	<div class=\"hj-kq-all-two\" style=''>\n");
            // 获取可编辑的日期
            KqDataMxService service = new KqDataMxServiceImpl(this.userView,this.conn);
            Map<String, List<String>> changePerData = service.searchChangePerData(kq_year, kq_duration, org_id, scheme_id);
            ArrayList<String> dateList = new ArrayList<String>();
            if(StringUtils.isNotEmpty(guidkey)) {
                dateList = (ArrayList<String>) changePerData.get(guidkey);
            }
            
            if("1".equals(dayDetailEnabled)) {
            	// 57519 是否可编辑 置灰 问题
            	int nowday = 1;
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                int maxDay = cal.getActualMaximum(Calendar.DATE);//起始日期  月份 最大天数
                int showDay = cal.get(Calendar.DAY_OF_MONTH);//考勤开始日期
                int startIndex = cal.get(Calendar.DAY_OF_WEEK)-1;//起始 星期几
                //【54720】考勤管理： 本人确认的日期和星期对不上，9月1号和星期天对不上
                startIndex=startIndex==0 ? 7:startIndex;
                int dateIndex = 0; //数据的下标
                content.append("		<div class=\"hj-kq-all-two-lbiao1\">\n");
                for (int i = 1; i <= 7; i++) {
                    //是否可编辑
                    boolean isEditFlag = false;
                    // 考勤开始日期之前的 置灰
                    if (i < startIndex) {
                        if (showDay - (startIndex - i) <= 0) {
                            //获取上个月最大的天数
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            // 0是一月
                            if (month == 0) {
                                year = year - 1;
                                month = 12;
                            } else {
                                month--;
                            }
                            
                            int lastMonthDays = this.getMaxDayByYearMonth(year, month);
                            int showLastDays = lastMonthDays - (startIndex - i - 1);
                            content.append(this.getDayContent(showLastDays, new ArrayList(), i, isEditFlag));
                        } else {
                            content.append(this.getDayContent(showDay - (startIndex - i), new ArrayList(), i, isEditFlag));
                        }
                        continue;
                    }
                    
                    if(null == dateList || 0 == dateList.size()
                            || dateList.contains("q35" + (nowday < 10 ? "0" + nowday : nowday))) {
                        isEditFlag = true;
                    }
                    
                    if (showDay <= maxDay) {
                        content.append(this.getDayContent(showDay, (ArrayList) punchDataList.get(dateIndex), i, isEditFlag));
                    } else {
                        content.append(this.getDayContent(showDay - maxDay, (ArrayList) punchDataList.get(dateIndex), i, isEditFlag));
                    }
                    nowday++;
                    showDay++;
                    dateIndex++;
                }
                content.append("		</div>\n");
                //考勤区间的总天树
                int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1);
                //计算页面展现完第一周剩余日期所在的周数，startIndex=1 的时候是每周的第一个格，因此 7 - startindex 的时候  第一周少算了一天所有要+1
                int weeks = (days - (7 - startIndex + 1)) % Calendar.DAY_OF_WEEK == 0 ? (days - (7 - startIndex + 1)) / Calendar.DAY_OF_WEEK : (days - (7 - startIndex + 1)) / Calendar.DAY_OF_WEEK + 1; //完整一周7天 的周数
                for (int i = 0; i < weeks; i++) {
                    content.append("		<div class=\"hj-kq-all-two-lbiao1\">\n");
                    for (int k = 0; k < Calendar.DAY_OF_WEEK; k++) {
                        //是否可编辑
                        boolean isEditFlag = false;
                        // 考勤结束日期之后的 置灰
                        if (dateIndex >= days) {
                            content.append(this.getDayContent(showDay - maxDay, new ArrayList(), k + 1, isEditFlag));
                        } else {
                        	if(null == dateList || 0 == dateList.size() 
                                    || dateList.contains("q35" + (nowday < 10 ? "0" + nowday : nowday))) {
                                isEditFlag = true;
                            }
                            if (showDay <= maxDay) {
                                content.append(this.getDayContent(showDay, (ArrayList) punchDataList.get(dateIndex), k + 1, isEditFlag));
                            } else {
                                content.append(this.getDayContent(showDay - maxDay, (ArrayList) punchDataList.get(dateIndex), k + 1, isEditFlag));
                            }
                        }
                        nowday++;
                        showDay++;
                        dateIndex++;
                    }
                    content.append("		</div>\n");
                }
                content.append("	</div>\n");
                content.append("	<div class=\"bh-clear\" style='clear:both;'></div>\n");
            }
            
            for (int i = 0; i < kqDataList.size(); i++) {
                content.append("	<div class=\"hj-kq-all-three\" style='height:110px;'>\n");
                
                for (int j = 0; j < 7; j++) {
                    if (i + 1 > kqDataList.size())
                        break;
                    LazyDynaBean bean = kqDataList.get(i);
                    String item_unit = (String) bean.get("item_unit");
                    content.append("<div style='text-align:center; color:#333;text-overflow:ellipsis;"
                    		+ "overflow:hidden;padding:24px 4px 4px 4px;font-size:14px;width:122px;height:100%;float:left;"
                    		+ "border-right:1px #d1d1d1 solid;border-bottom:1px #d1d1d1 solid;box-sizing: border-box;'>");
                    if (item_unit == null || item_unit.trim().length() == 0) {
                        content.append("<div style='cursor:default;width:100%;height:40px;text-align:center;'>" + bean.get("item_name") + "</div>");
                    } else {
                        String desc = this.getItemUnitDesc(item_unit);
                        content.append("<div style='cursor:default;width:100%;height:40px;text-align:center;'>" + bean.get("item_name") + "（" + desc + "）</div>");
                    }
                    String color = "#30b3f7";
                    if (i == 0 || i == kqDataList.size() - 1) {
                        color = "#FFC754";
                    }
                    
                	content.append("<div title='" + bean.get("value") + "' class='hj-kq-more-item' style='color:" + color + ";' >"
                			+ bean.get("value") + "</div>");
                    content.append("</div>");
                    
                    if (j <= 5)
                        i++;
                    if (i + 1 > kqDataList.size())
                        break;
                }
                
                content.append("	</div>\n");
            }
            boolean flag = false;
            if(StringUtils.isNotBlank(confirmField)) {
        		FieldItem fieldItem = DataDictionary.getFieldItem(confirmField, "Q35");
        		if(fieldItem!=null && "1".equals(fieldItem.getUseflag()) && "M".equalsIgnoreCase(fieldItem.getItemtype())) {
        			flag = true;
        		}
        	}
            
            if ("email".equalsIgnoreCase(label)) {
            	//系统名称
            	String sys_name = SystemConfig.getPropertyValue("sys_name");
            	sys_name = StringUtils.isNotBlank(sys_name)?sys_name:"人力系统";
            	String kq_url = "/kq/kqdata/kqdataconfirm?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kq_year=" + kq_year + "&kq_duration=" + kq_duration 
            			+ "&orgId=" + PubFunc.encrypt(org_id) + "&label=show";
            	StringBuffer href = new StringBuffer(logonUrl);
            	href.append("/module/utils/jsp.do?br_query=link&param=");
            	href.append(SafeCode.encode(kq_url));
            	href.append("&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(A01Bean.get("username") + "," + A01Bean.get("password")))));
            	if(flag && StringUtils.isNotBlank(confirmMemo)) {
            		content.append("<div name=\"confirm_memo\" style=\"border:none;padding:2px;width:856px;margin-top:10px\">"+ResourceFactory.getProperty("kq.archive.scheme.confirmTitle") + confirmMemo +"</div>");
            	}
            	if(flag) {
            		content.append("<div style=\"font-style: italic;font-weight: normal;margin-top:16px\">"+ ResourceFactory.getProperty("kq.archive.scheme.confirmTipEmail").replace("{0}", "<a href="+href+">"+sys_name+"</a>")+"</div>");
            	}
            	content.append("	<div class=\"hj-kq-all-four\" style=' width:860px; text-align:center;margin-top:30px;margin-bottom:10px;'>\n");
                content.append("		<a onMouseOut=\"this.style.textDecoration='none'\" href=\"" + logonUrl + "/kq/kqdata/kqdataconfirm?schemeId=" + PubFunc.encrypt(scheme_id)
                + "&kq_year=" + kq_year + "&kq_duration=" + kq_duration + "&org_id=" + PubFunc.encrypt(org_id) + "&flag=1&sendTime=" + sendTime + "&label=email&token="
                + token + "\" style=\"width:150px; height:44px;border-radius:6px;background-color:#30b3f7; color:#FFF; text-align:center; line-height:44px; border:none; font-size:20px;text-decoration:none;display:block;margin:0 auto;\">"
                + ResourceFactory.getProperty("kq.archive.scheme.confirmbtn") + "</a>\n");
                content.append("	</div>\n");
            } else {
            		content.append("<form action=\"/kq/kqdata/kqdataconfirm\" method=\"post\">");
            		content.append("<input name=\"schemeId\" type=\"hidden\" value=" + PubFunc.encrypt(scheme_id) + ">");
            		content.append("<input name=\"kq_year\" type=\"hidden\" value=" + kq_year + ">");
            		content.append("<input name=\"kq_duration\" type=\"hidden\" value=" + kq_duration + ">");
            		content.append("<input name=\"org_id\" type=\"hidden\" value=" + PubFunc.encrypt(org_id) + ">");
            		content.append("<input name=\"flag\" type=\"hidden\" value=\"1\">");
            		content.append("<input name=\"sendTime\" type=\"hidden\" value=" + sendTime + ">");
            		content.append("<input name=\"label\" type=\"hidden\" value=\"pendingtask\">");
            		content.append("<input name=\"token\" type=\"hidden\" value=" + token + ">");
        		if(flag) {
            		content.append("<textarea name=\"confirm_memo\" rows=\"4\" placeholder="+ResourceFactory.getProperty("kq.archive.scheme.confirmEmptyTip")+" class=\"hj-kq-confirm\">"+ confirmMemo +"</textarea>");
            		content.append("<div style=\"font-style: italic;font-weight: normal;margin-top:10px\">"+ResourceFactory.getProperty("kq.archive.scheme.confirmTipSys")+"</div>");
            	}
            	content.append("<div class=\"hj-kq-all-four\" style=' width:860px; text-align:center;margin-top:16px;margin-bottom:10px;'>\n");
            	content.append("<input type=\"submit\" onMouseOut=\"this.style.textDecoration='none'\" class=\"hj-button-enter\" "
            			+ "style=\"display:block;width:150px;float:left;position: absolute; right: 50%;\" value=" + ResourceFactory.getProperty("kq.archive.scheme.confirmbtn") + ">");
            	content.append("<a onMouseOut=\"this.style.textDecoration='none'\" href='/templates/index/hcm_portal.do?b_query=link' class=\"hj-button-enter\" "
            			+ "style=\"display:block;width:150px;margin-left:60px;float:left;position: absolute; left: 50%;\">" + ResourceFactory.getProperty("kq.data.appeal.back") + "</a>\n");
            	content.append("</div>\n");
            	content.append("</form>");
            }
            content.append("</div>\n");
            content.append("</body>\n");
            content.append("</html>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return content.toString();
    }

    /**
     * 每日打卡内容html处理
     *
     * @param day
     *            哪天
     * @param dataList
     *            打卡数据集合
     * @param index
     *            考勤星期几
     * @param isEditFlag
     *            是否可编辑
     * @return
     */
    private String getDayContent(int day, ArrayList dataList, int index, boolean isEditFlag) {
        StringBuffer content = new StringBuffer();
        String cssClassName = "hj-lbiao1-1";
        if(!isEditFlag) {
            cssClassName = "hj-lbiao1-2";
        }
        if (dataList == null || dataList.size() == 0) {
            content.append("			<div class=\"" + cssClassName + "\" style='box-sizing: border-box;width:122px;height:130px; border-left:1px #d1d1d1 solid;border-bottom:1px #d1d1d1 solid; float:left;"
                    + (index == 7 ? "border-right:1px #d1d1d1 solid;" : "") + "'>" +
                    "<a onMouseOut=\"this.style.textDecoration='none'\" style='cursor:default;font-size:14px;color:#666; float:right; margin-top:4px;margin-right:10px;text-decoration:none;' href=\"javascript:void(0);\">"
                    + day + ResourceFactory.getProperty("kq.archive.scheme.day") + "</a></div>\n");
        } else {
        	
            content.append("			<div class=\"" + cssClassName + "\" style='box-sizing: border-box;width:122px;height:130px; border-left:1px #d1d1d1 solid;border-bottom:1px #d1d1d1 solid; float:left;" + (index == 7 ? "border-right:1px #d1d1d1 solid;" : "") + "'>\n");
            content.append("				<a onMouseOut=\"this.style.textDecoration='none'\" style='cursor:default;font-size:14px;color:#666; float:right; margin-top:4px;margin-right:10px;text-decoration:none;' href=\"javascript:void(0);\">" +
                    day + ResourceFactory.getProperty("kq.archive.scheme.day") + "</a>\n");
//			content.append("				<div style=\"width:122px;height:26px;\"></div>\n");
            content.append("				<div style=\"clear:both\"></div>\n");
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean lazyDynaBean = (LazyDynaBean) dataList.get(i);
                String backgroundColor = (String) lazyDynaBean.get("color");
                // 51164 没设置颜色的项目或班次 默认显示黑色
                if(StringUtils.isBlank(backgroundColor))
                	backgroundColor = "#000000";
                String type = (String) lazyDynaBean.get("type");
                String name = "";
                if ("I".equalsIgnoreCase(type)) {

                    name = (String) lazyDynaBean.get("item_symbol") + (String) lazyDynaBean.get("name");
                    content.append("				<div class=\"hj-item\" style='width:122px;height:22px;margin:0 0 4px 8px;'>" +
                            "<a onMouseOut=\"this.style.textDecoration='none'\" style='cursor:default;cursor:default;width:96px;height:22px;display:inline-block;padding-left:10px; line-height:22px;"
                            + "color:"+ backgroundColor + ";text-decoration:none;' href=\"javascript:void(0);\" >" + name + "</a></div>\n");
                } else {
                    name = (String) lazyDynaBean.get("name");
                    content.append("				<div class=\"hj-item\" style='width:122px;height:22px;margin:0 0 4px 8px;'><a onMouseOut=\"this.style.textDecoration='none'\"" +
                            " style='cursor:default;cursor:default;width:96px;height:22px;display:inline-block;padding-left:10px; line-height:22px;"
                            + "color:" + backgroundColor + ";text-decoration:none;' href=\"javascript:void(0);\" >" + name + "</a></div>\n");
                }
            }
            content.append("			</div>\n");
        }
        return content.toString();
    }

    private String getItemUnitDesc(String item_unit) {
        String desc = "";
        if ("01".equalsIgnoreCase(item_unit)) {//小时
            desc = ResourceFactory.getProperty("kq.archive.scheme.shi");
        } else if ("02".equalsIgnoreCase(item_unit)) {//天
            desc = ResourceFactory.getProperty("kq.archive.scheme.tian");
        } else if ("03".equalsIgnoreCase(item_unit)) {//分钟
            desc = ResourceFactory.getProperty("kq.archive.scheme.fen");
        } else if ("04".equalsIgnoreCase(item_unit)) {//次
            desc = ResourceFactory.getProperty("kq.archive.scheme.ci");
        }
        return desc;

    }


    /**
     * 获得某个月最大天数
     *
     * @param year  年份
     * @param month 月份 (1-12)
     * @return 某个月最大天数
     */
    private int getMaxDayByYearMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year - 1);
        calendar.set(Calendar.MONTH, month);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    private String getDayDetailEnabled (int schemeId) {
    	String dayDetailEnabled = "0";
    	RowSet rs = null;
    	try {
			String sql = "select day_detail_enabled from kq_scheme where scheme_id=?";
			ArrayList<Integer> paramList = new ArrayList<Integer>();
			paramList.add(schemeId);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql, paramList);
			if(rs.next())
				dayDetailEnabled = rs.getString("day_detail_enabled");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
    	
		return dayDetailEnabled;
    }
    
	/**
	 * 获取当前审批人
	 * 
	 * @param bean
	 *            页面显示数据集合
	 * @param kqSchemeMap
	 *            考勤方案数据集合
	 * @param orgMap
	 *            考勤机构数据集合
	 * @return
	 */
	private String getApproveUser(LazyDynaBean bean, HashMap<String, Object> kqSchemeMap, HashMap orgMap, boolean haveSecondary, KqDataUtil kqDataUtil) {
		String approveUser = "";
		try {
			String status = String.valueOf(bean.get("status"));
			if("06".equals(status))
				return approveUser;
			// 已批准后 需要查考勤员与审核人是否有归档授权
			if("03".equals(status))
				return kqDataUtil.getApproveUserFile(kqSchemeMap, orgMap, haveSecondary, "0");
			
			String curr_user = String.valueOf(bean.get("curr_user"));
			if (curr_user.equals(String.valueOf(KqDataUtil.role_Agency_Clerk)))
				approveUser = (String) orgMap.get("clerk_fullname");
			else if (curr_user.equals(String.valueOf(KqDataUtil.role_Agency_Reviewer)))
				approveUser = (String) orgMap.get("reviewer_fullname");
			else if (curr_user.equals(String.valueOf(KqDataUtil.role_Clerk)))
				approveUser = (String) kqSchemeMap.get("clerk_fullname");
			else if (curr_user.equals(String.valueOf(KqDataUtil.role_Reviewer)))
				approveUser = (String) kqSchemeMap.get("reviewer_fullname");
			else if (StringUtils.isNotEmpty(curr_user)) {
				if (curr_user.equals((String) orgMap.get("clerk_username")))
					approveUser = (String) orgMap.get("clerk_fullname");
				else if (curr_user.equals((String) orgMap.get("reviewer_id")))
					approveUser = (String) orgMap.get("reviewer_fullname");
				else if (curr_user.equals((String) kqSchemeMap.get("clerk_username")))
					approveUser = (String) kqSchemeMap.get("clerk_fullname");
				else if (curr_user.equals((String) kqSchemeMap.get("reviewer_id")))
					approveUser = (String) kqSchemeMap.get("reviewer_fullname");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return approveUser;
	}
	/**
	 * 机构历史时点显示控制
	 */
	private boolean checkOrg(String org_id,Date kq_start,Date kq_end)throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer sql=new StringBuffer();
			sql.append("select * from organization where codeitemid=? ");
			sql.append("and( (start_date <=? and end_date >=?) ");
			sql.append("or (start_date >=? and start_date <=?)");
			sql.append("or (start_date >=? and end_date <=?))");
			ArrayList pList = new ArrayList();
			pList.add(org_id);
			pList.add(kq_start);
			pList.add(kq_start);
			pList.add(kq_start);
			pList.add(kq_end);
			pList.add(kq_start);
			pList.add(kq_end);
			rs=dao.search(sql.toString(),pList);
			if (rs.next()) {
				String aa=rs.getString("codeitemid");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return false;
	}
	/**
	 * 获取用户名称
	 * @return
	 */
	private HashMap<String, HashMap<String, String>> getUserName(String userNames){
	    HashMap<String, HashMap<String, String>> userNameMap = new HashMap<String, HashMap<String, String>>();
	    RowSet rs = null;
	    try {
	        ContentDAO dao = new ContentDAO(this.conn);
	        StringBuffer sql = new StringBuffer();
	        sql.append("select UserName,FullName,NBase,A0100 from OperUser where UserName in ('#'");
	        ArrayList<String> paramList = new ArrayList<String>();
	        String[] userName = userNames.split(",");
	        for(int i = 0; i < userName.length; i++) {
	            if(StringUtils.isEmpty(userName[i])) {
	                continue;
	            }
	            
	            sql.append(",?");
	            paramList.add(userName[i]);
	        }
	        
	        sql.append(")");
	        rs = dao.search(sql.toString(), paramList);
	        while(rs.next()) {
	            String userNameId = rs.getString("UserName");
	            if(StringUtils.isEmpty(userNameId)) {
	                continue;
	            }
	            
	            HashMap<String, String> map = new HashMap<String, String>();
	            String fullName = rs.getString("FullName");
	            String nbase = rs.getString("nbase");
	            String a0100 = rs.getString("a0100");
	            map.put("fullName", fullName);
	            map.put("nbase", nbase);
	            map.put("a0100", a0100);
	            
	            userNameMap.put(userNameId, map);
	        }
	    } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
	    
	    return userNameMap;
	}
	
    /**
     * 获取自助用户的姓名
     * 
     * @param userNameMap
     *            登录帐号关联人员的对象
     * @return
     */
    private HashMap<String, String> getSelfUserName(HashMap<String, HashMap<String, String>> userNameMap) {
        HashMap<String, String> userMap = new HashMap<String, String>();
        RowSet rs = null;
        try {
            HashMap<String, String> selfMap = new HashMap<String, String>();
            for (Entry<String, HashMap<String, String>> entry : userNameMap.entrySet()) {
                HashMap<String, String> map = entry.getValue();
                String nbase = map.get("nbase");
                if (StringUtils.isEmpty(nbase)) {
                    continue;
                }

                String a0100 = map.get("a0100");
                if (selfMap.containsKey(nbase)) {
                    String a0100s = selfMap.get(nbase);
                    a0100s += "," + a0100;
                    selfMap.put(nbase, a0100s);
                } else {
                    selfMap.put(nbase, a0100);
                }

            }

            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            String sqlTmp = "select '#nbase#' nbase,a0100,a0101 from #nbase#a01 where a0100 in ('#'";
            ArrayList<String> paramList = new ArrayList<String>();
            for (Entry<String, String> entry : selfMap.entrySet()) {
                String nbase = entry.getKey();
                String a0100s = entry.getValue();
                if (StringUtils.isEmpty(nbase)) {
                    continue;
                }

                if (StringUtils.isNotEmpty(sql.toString())) {
                    sql.append(" union all ");
                }

                sql.append(sqlTmp.replace("#nbase#", nbase));
                String[] personid = a0100s.split(",");
                for (int i = 0; i < personid.length; i++) {
                    if (StringUtils.isEmpty(personid[i])) {
                        continue;
                    }

                    sql.append(",?");
                    paramList.add(personid[i]);
                }

                sql.append(")");
            }

            if(StringUtils.isNotEmpty(sql.toString())) {
                rs = dao.search(sql.toString(), paramList);
                selfMap.clear();
                while (rs.next()) {
                    String nbase = rs.getString("nbase");
                    String a0100 = rs.getString("a0100");
                    String a0101 = rs.getString("a0101");
                    if (StringUtils.isEmpty(a0100)) {
                        continue;
                    }
                    
                    selfMap.put(nbase + a0100, a0101);
                }
            }

            for (Entry<String, HashMap<String, String>> entry : userNameMap.entrySet()) {
                HashMap<String, String> map = entry.getValue();
                String userName = entry.getKey();
                String nbase = map.get("nbase");
                String fullName = map.get("fullName");
                fullName = StringUtils.isEmpty(fullName) ? userName : fullName;

                if (StringUtils.isEmpty(nbase)) {
                    userMap.put(userName, fullName);
                } else {
                    String a0100 = map.get("a0100");
                    String a0101 = selfMap.get(nbase + a0100);
                    if (StringUtils.isEmpty(a0101)) {
                        userMap.put(userName, fullName);
                    } else {
                        userMap.put(userName, a0101);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return userMap;
    }
}
