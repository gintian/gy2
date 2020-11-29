package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataSpService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataMxServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataSpServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.dingtalk.SendDingTalkUtil;
import com.hjsj.hrms.utils.sendmessage.email.SendEmailUtil;
import com.hjsj.hrms.utils.sendmessage.sms.SendSmsUtil;
import com.hjsj.hrms.utils.sendmessage.weixin.SendWeixinUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;


public class KqDataSpMainTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        JSONObject returnJson = new JSONObject();
        try {
            JSONObject jsonStrObject = JSONObject.fromObject(this.getFormHM().get("jsonStr"));
            KqDataSpService kqDataSpService = new KqDataSpServiceImpl(this.getFrameconn(), this.getUserView());


            /**
             * main: 获取页面数据
             * create :新建考勤数据
             * appeal:报批考勤数据
             * reject:驳回考勤数据
             * approve:批准考勤数据
             * submit:提交（归档）考勤数据
             * sendmsg:发送消息
             * beforeCompute:获取计算信息
             * Compute：计算
             * checkCanAppeal:获取未确认的数据
             * msgConfig 获取发送信息的配置信息
             * rejectpersonnel:获取返回人员列表
             * getExportColumns:获取输出列
             * confirm : 代确认操作 将用户待办置为已办
             * downward ：下发数据
             */
            String actionType = (String) jsonStrObject.get("type");
            String scheme_id = (String) jsonStrObject.get("scheme_id");
            String kq_duration = (String) jsonStrObject.get("kq_duration");
            String kq_year = (String) jsonStrObject.get("kq_year");
            if (StringUtils.isNotBlank(scheme_id)) {
                scheme_id = PubFunc.decrypt(scheme_id);
            }

            if ("main".equalsIgnoreCase(actionType)) {

                ArrayList<HashMap<String, String>> mySchemeList = kqDataSpService.listKqSchemeByMySelf();
                if (mySchemeList.size() == 0) {
                    //"没有您负责的考勤方案！"
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.sp.msg.noyoursKqScheme")));
                }
                ArrayList<LazyDynaBean> listKq_extend = new ArrayList<LazyDynaBean>();
                if (StringUtils.isBlank(scheme_id) || StringUtils.isBlank(kq_duration) || StringUtils.isBlank(kq_year)) {
                    String t_sql=" And Appuser LIKE ? ";
                    ArrayList parameterList = new ArrayList();
                    parameterList.add("%;"+this.userView.getUserName()+";%");
                    if(StringUtils.isNotBlank(this.getUserView().getA0100())){
                        parameterList.add("%;"+this.getUserView().getDbname()+this.getUserView().getA0100()+";%");
                        t_sql=" And( Appuser LIKE ? or Appuser LIKE ? )";
                    }
                    // 58644 更改排序 优先显示最新的考勤期间
                    listKq_extend = kqDataSpService.listKq_extend_log(t_sql, parameterList, "kq_year DESC,kq_duration DESC,Scheme_id ");
                }

                if (StringUtils.isBlank(scheme_id)) {
                	// 【45862】获取用户负责的方案中的方案编号优化 
                	// 如果存在与登录用户相关的上报记录，则优先在上报记录中获取在登录用户负责的方案编号
                    if (listKq_extend.size() > 0) {
                    	for(LazyDynaBean bean : listKq_extend) {
                    		// 是否是登录用户负责的方案：=true：是；=false：不是
                    		boolean isMyScheme = false;
                    		String schemeId = String.valueOf(bean.get("scheme_id"));
                    		for(HashMap<String, String> map : mySchemeList) {
                    			String mySchemeId = map.get("scheme_id");
                    			if(schemeId.equalsIgnoreCase(mySchemeId)) {
                    				isMyScheme = true;
                    				scheme_id = schemeId;
                    				break;
                    			}
                    		}
                    		if(isMyScheme)
                    			break;
                    	}
                    }
                    // 如果上报记录中没有找到登录用户负责的方案编号则取负责方案中的第一个的编号
                    if (StringUtils.isBlank(scheme_id))
                        scheme_id = mySchemeList.get(0).get("scheme_id");
                }
                jsonStrObject.put("scheme_id", scheme_id);
                if (StringUtils.isBlank(kq_duration) || StringUtils.isBlank(kq_year)) {
                    HashMap<String, String> dateMap = kqDataSpService.getKqDate(listKq_extend);
                    kq_duration = dateMap.get("kq_duration");
                    kq_year = dateMap.get("kq_year");
                }
                jsonStrObject.put("kq_duration", kq_duration);
                jsonStrObject.put("kq_year", kq_year);

                JSONObject return_data = new JSONObject();
                return_data.put("kq_year", kq_year);
                return_data.put("kq_duration", kq_duration);
                return_data.put("scheme_id", PubFunc.encrypt(scheme_id));
                
                // 46562 提出来该方法
                SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.frameconn, this.userView);
                ArrayList parameterList = new ArrayList();
                parameterList.add(scheme_id);
                // 该方法的年月已暂时无用故提取出来,//机构历史时点显示控制添加年月
                ArrayList<LazyDynaBean> listKq_scheme = schemeMainService.listKq_scheme(" and scheme_id=? ", parameterList, "", kq_year, kq_duration);
                LazyDynaBean schemeBean = (LazyDynaBean) listKq_scheme.get(0);
                
                ArrayList<HashMap<String, String>> dataList = kqDataSpService.listKqSpMainData(jsonStrObject, schemeBean);
                JSONArray jsonList = new JSONArray();
                for (HashMap<String, String> hashMap : dataList) {
                    JSONObject jo = JSONObject.fromObject(hashMap);
                    jsonList.add(jo);
                }
                return_data.put("org_list", jsonList);
                // 47006 当前用户名
                return_data.put("currentUser", this.userView.getUserFullName());
                jsonList = new JSONArray();
                for (HashMap<String, String> hashMap : mySchemeList) {
                    String id = PubFunc.encrypt(hashMap.get("scheme_id"));
                    hashMap.put("scheme_id", id);
                    JSONObject jo = JSONObject.fromObject(hashMap);
                    jsonList.add(jo);
                }
                return_data.put("scheme_list", jsonList);
                
                jsonList = new JSONArray();
                ArrayList<HashMap<String, Object>> yearList = this.listAllDurationLog(scheme_id, kqDataSpService, schemeBean);
                for (HashMap<String, Object> bean : yearList) {
                    JSONObject jo = JSONObject.fromObject(bean);
                    jsonList.add(jo);
                }
                
                KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView());
                int role = kqDataUtil.getKqRole("1", schemeBean);
                
                return_data.put("year_list", jsonList);
                return_data.put("role", role);
                //是否填写审批意见参数
                KqPrivForHospitalUtil privForHospitalUtil = new KqPrivForHospitalUtil(this.userView, this.frameconn);
                String approvalMessage=privForHospitalUtil.getApprovalMessage();
                return_data.put("approvalMessage", approvalMessage);
                // 45772 审批页面增加归档功能授权控制
                JSONObject priv = new JSONObject();
        		priv.put("submitp", (this.userView.hasTheFunction("272030202")) ? "1" : "0");
        		// 计算
        		priv.put("computep", (this.userView.hasTheFunction("272030201")) ? "1" : "0");
        		// 应急中心个性化标识
            	String hlwyjzx_flag = "hlwyjzx".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")) ? "1" : "0";
            	priv.put("hlwyjzx_flag", hlwyjzx_flag);
                return_data.put("privs", priv);
                returnJson.put("return_data", return_data);
                returnJson.put("return_code", "success");
                returnJson.put("return_msg", "success");
            }
            //新建考勤数据
            else if ("create".equalsIgnoreCase(actionType)) {
                String org_Id = (String) jsonStrObject.get("org_id");
                List<String> orgIds = new ArrayList<String>();
                if(StringUtils.isNotBlank(org_Id)){
                    String[] arr = org_Id.split(",");
                    for(int i=0;i<arr.length;i++){
                        orgIds.add(PubFunc.decrypt(arr[i]));
                    }
                }
                kqDataSpService.createNewKqData(scheme_id, kq_year, kq_duration,orgIds);

                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }
            //报批考勤数据
            else if ("appeal".equalsIgnoreCase(actionType)) {
                String viewType = (String) jsonStrObject.get("viewtype");
                String org_Id = (String) jsonStrObject.get("org_id");
                org_Id=PubFunc.decrypt(org_Id);
                JSONObject photo_info = (JSONObject) jsonStrObject.get("photo_info");
                kqDataSpService.appealKqData(viewType, scheme_id, kq_year, kq_duration, org_Id, photo_info);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }
            //驳回考勤数据
            else if ("reject".equalsIgnoreCase(actionType)) {
                String org_Id = (String) jsonStrObject.get("org_id");
                //0:考勤上报 1:考勤审批
                String viewType = (String) jsonStrObject.get("viewtype");
                //驳回身份id 关联kqdatautil常量
                String role_id="";
                //驳回人员id
                String user_id="";
                if(jsonStrObject.containsKey("role_id")&&jsonStrObject.get("role_id")!=null){
                    role_id=String.valueOf(jsonStrObject.get("role_id"));
                }
                if("0".equals(viewType)){
                    role_id=String.valueOf(KqDataUtil.role_Agency_Clerk);
                }
                if(jsonStrObject.containsKey("user_id")&&jsonStrObject.get("user_id")!=null){
                    user_id=PubFunc.decrypt(String.valueOf(jsonStrObject.get("user_id")));
                }

                kqDataSpService.rejectKqData(viewType, scheme_id, kq_year, kq_duration, org_Id,user_id,role_id);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");


            }
            //批准考勤数据
            else if ("approve".equalsIgnoreCase(actionType)) {
                ArrayList<String> orgList = new ArrayList<String>();
                String org_Id = (String) jsonStrObject.get("org_id");
                orgList.add(org_Id);
                String viewType = (String) jsonStrObject.get("viewtype");
                // 机构考勤员或审核人直接点同意时  传签章图片信息
                JSONObject photo_info = (JSONObject) jsonStrObject.get("photo_info");
                kqDataSpService.approveKqData(scheme_id, kq_year, kq_duration, orgList, viewType, photo_info);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }
            //提交（归档）考勤数据
            else if ("submit".equalsIgnoreCase(actionType)) {
                String org_Id = "";
                //0:考勤上报 1:考勤审批
                String viewType = (String) jsonStrObject.get("viewtype");
                if (jsonStrObject.containsKey("org_id") && StringUtils.isNotBlank((String) jsonStrObject.get("org_id"))) {
                    org_Id = (String) jsonStrObject.get("org_id");
                    org_Id = PubFunc.decrypt(org_Id);
                }
                kqDataSpService.submitKqData(viewType,scheme_id, kq_year, kq_duration, org_Id,true);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");

            }
            //获取计算信息
            else if ("beforeCompute".equalsIgnoreCase(actionType)) {
                String org_id = jsonStrObject.getString("org_id");
                String[] orgIdArr = org_id.split(",");
                org_id = "";
                for (String s : orgIdArr) {
                    org_id += PubFunc.decrypt(s) + ",";
                }
                if (StringUtils.isNotBlank(org_id)) {
                    org_id = org_id.substring(0, org_id.length() - 1);
                }
                boolean flag = this.beforeCompute(kq_year, kq_duration, scheme_id, org_id);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
                JSONObject return_data = new JSONObject();
                return_data.put("flag", flag);
                returnJson.put("return_data", return_data);
            }
            //计算时校验应出勤是否设置单位（item_unit） 如果单位是时|分，校验班次是否设置了时长
            else if("checkCanCompute".equals(actionType)) {
            	String msg = this.checkCanCompute(scheme_id);
            	returnJson.put("return_msg", msg);
                returnJson.put("return_code", StringUtils.isEmpty(msg)?"success":"fail");
            //月度考勤表--计算
            } else if ("compute".equalsIgnoreCase(actionType)) {
                String org_id = jsonStrObject.getString("org_id");
                int coverDataFlag = jsonStrObject.getInt("coverDataFlag");
                String[] orgIdArr = org_id.split(",");
                org_id = "";
                for (String s : orgIdArr) {
                    org_id += PubFunc.decrypt(s) + ",";
                }
                if (StringUtils.isNotBlank(org_id)) {
                    org_id = org_id.substring(0, org_id.length() - 1);
                }
                kqDataSpService.calculateMxData(PubFunc.encrypt(scheme_id), kq_year, kq_duration, org_id,coverDataFlag);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }
            //获取未确认的考勤数据
            else if ("checkCanAppeal".equalsIgnoreCase(actionType)) {
                KqDataMxService dataMxService = new KqDataMxServiceImpl(userView, frameconn);
                StringBuffer sqlWhere = new StringBuffer();
                String orgId = (String) jsonStrObject.get("org_id");
                if (StringUtils.isNotBlank(orgId))
                    orgId = PubFunc.decrypt(orgId);
                sqlWhere.append(" and kq_year=? and kq_duration=? and scheme_id=? and (confirm is null or confirm=0)");
                ArrayList parameterList = new ArrayList();
                parameterList.add(kq_year);
                parameterList.add(kq_duration);
                parameterList.add(scheme_id);
                if (StringUtils.isNotEmpty(orgId)) {
                    sqlWhere.append(" and Org_id like ?");
                    parameterList.add(orgId+"%");
                }
                ArrayList<LazyDynaBean> list = dataMxService.listQ35(sqlWhere.toString(), parameterList, null);
                //size ==0的话证明没有未确认的考勤数据
                JSONObject return_data = new JSONObject();
                if (list.size() == 0) {
                    return_data.put("canAppeal", "true");
                } else {
                    return_data.put("canAppeal", "false");
                }
                returnJson.put("return_data", return_data);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }
            //获取发送信息配置
            else if ("msgConfig".equalsIgnoreCase(actionType)) {

                String userName = (String) jsonStrObject.get("username");
                if (StringUtils.isNotBlank(userName)) {
                    userName = PubFunc.decrypt(userName);
                }
                HashMap<String, String> msgMap = kqDataSpService.mapMsgConfig(userName);
                if (StringUtils.isNotBlank(msgMap.get("phone"))) {
                    msgMap.put("phone", "1");
                } else {
                    msgMap.put("phone", "0");
                }
                if (StringUtils.isNotBlank(msgMap.get("wechat"))) {
                    msgMap.put("wechat", "1");
                } else {
                    msgMap.put("wechat", "0");
                }
                if (StringUtils.isNotBlank(msgMap.get("dingtalk"))) {
                    msgMap.put("dingtalk", "1");
                } else {
                    msgMap.put("dingtalk", "0");
                }
                if (StringUtils.isNotBlank(msgMap.get("email"))) {
                    msgMap.put("email", "1");
                } else {
                    msgMap.put("email", "0");
                }

                JSONObject jo = JSONObject.fromObject(msgMap);
                JSONObject return_data = new JSONObject();
                return_data.put("msgconfig", jo);
                returnJson.put("return_data", return_data);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }
            //发送消息
            else if ("sendmsg".equalsIgnoreCase(actionType)) {

                String msgvalue = (String) jsonStrObject.get("msgvalue");
                String clerk_id = PubFunc.decrypt((String) jsonStrObject.get("clerk_id"));
                String sendemail = (String) jsonStrObject.get("sendemail");
                String sendphone = (String) jsonStrObject.get("sendphone");
                String sendweichat = (String) jsonStrObject.get("sendweichat");
                String senddingtalk = (String) jsonStrObject.get("senddingtalk");
                String extra = scheme_id + "_notice_" + kq_year + kq_duration + "_" + clerk_id;

                SendEmailUtil sendEmailUtil = new SendEmailUtil(this.getUserView(), this.getFrameconn(), "30", "");
                LazyDynaBean bean = new LazyDynaBean();
                //"考勤催办"
                bean.set("title", ResourceFactory.getProperty("kq.data.sp.text.kqurgent"));
                bean.set("message", msgvalue);
                bean.set("send_user_name", this.getUserView().getUserFullName());
                bean.set("extra", extra);
                ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
                HashMap<String, String> msgMap = kqDataSpService.mapMsgConfig(clerk_id);
                if (msgMap.containsKey("a0100")) {
                    bean.set("nbase", msgMap.get("nbase"));
                    bean.set("a0100", msgMap.get("a0100"));
                } else {
                    bean.set("receiver", clerk_id);
                    bean.set("receiver_name", clerk_id);
                    bean.set("receiver_address", msgMap.get("email"));
                    bean.set("receiver_b0110", "");
                }
                list.add(bean);
                StringBuffer errorMsg = new StringBuffer();

                StringBuffer strWhere=new StringBuffer(" id in ( ");
                ArrayList parameterList=new ArrayList();
                if ("1".equals(sendemail)) {
                    if (msgMap.containsKey("a0100")) {
                        list = sendEmailUtil.listMessageBeanBase(list);
                    }
                    sendEmailUtil.sendMsg(list);
                    for (LazyDynaBean bean1 : list) {
                        strWhere.append("?").append(",");
                        parameterList.add(bean1.get("id"));
                    }
                    strWhere.deleteCharAt(strWhere.length()-1);
                    strWhere.append(")");

                    if (!sendEmailUtil.getSendStatusByExtra(strWhere.toString(),parameterList)) {
                        //邮件
                        errorMsg.append(ResourceFactory.getProperty("kq.data.sp.text.email")).append("、");
                    }
                }
                if ("1".equals(sendphone)) {
                    SendSmsUtil sendSmsUtil = new SendSmsUtil(this.getUserView(), this.getFrameconn(), "30", "");
                    if (msgMap.containsKey("a0100")) {
                        list = sendSmsUtil.listMessageBeanBase(list);
                    }else{
                        list.get(0).set("receiver_address", msgMap.get("phone"));
                    }
                    sendSmsUtil.sendMsg(list);
                    for (LazyDynaBean bean1 : list) {
                        strWhere.append("?").append(",");
                        parameterList.add(bean1.get("id"));
                    }
                    strWhere.deleteCharAt(strWhere.length()-1);
                    strWhere.append(")");
                    if (!sendSmsUtil.getSendStatusByExtra(strWhere.toString(),parameterList)) {
                        msgMap.put("phone", "0");
                        //短信
                        errorMsg.append(ResourceFactory.getProperty("kq.data.sp.text.sms")).append("、");
                    }
                }
                if ("1".equals(sendweichat)) {
                    SendWeixinUtil sendWeixinUtil = new SendWeixinUtil(this.getUserView(), this.getFrameconn(), "30", "");
                    // 60332 改成与邮件方式一样 先获取list
                    if (msgMap.containsKey("a0100")) {
                        list = sendWeixinUtil.listMessageBeanBase(list);
                    }
                    sendWeixinUtil.sendMsg(list);
                    for (LazyDynaBean bean1 : list) {
                        strWhere.append("?").append(",");
                        parameterList.add(bean1.get("id"));
                    }
                    strWhere.deleteCharAt(strWhere.length()-1);
                    strWhere.append(")");
                    if (!sendWeixinUtil.getSendStatusByExtra(strWhere.toString(),parameterList)) {
                        msgMap.put("wechat", "0");
                        //微信
                        errorMsg.append(ResourceFactory.getProperty("kq.data.sp.text.weichat")).append("、");
                    }
                }
                if ("1".equals(senddingtalk)) {
                    SendDingTalkUtil sendDingTalkUtil = new SendDingTalkUtil(this.getUserView(), this.getFrameconn(), "30", "");
                    // 60332 改成与邮件方式一样 先获取list
                    if (msgMap.containsKey("a0100")) {
                        list = sendDingTalkUtil.listMessageBeanBase(list);
                    }
                    sendDingTalkUtil.sendMsg(list);
                    for (LazyDynaBean bean1 : list) {
                        strWhere.append("?").append(",");
                        parameterList.add(bean1.get("id"));
                    }
                    strWhere.deleteCharAt(strWhere.length()-1);
                    strWhere.append(")");
                    if (!sendDingTalkUtil.getSendStatusByExtra(strWhere.toString(),parameterList)) {
                        msgMap.put("dingtalk", "0");
                        //钉钉
                        errorMsg.append(ResourceFactory.getProperty("kq.data.sp.text.dingtalk")).append("、");
                    }
                }
                if (errorMsg.length() > 0) {
                    errorMsg.deleteCharAt(errorMsg.length() - 1);
                    //发送失败！
                    errorMsg.append(ResourceFactory.getProperty("kq.data.sp.msg.senderror"));
                    returnJson.put("return_msg", errorMsg.toString());
                    returnJson.put("return_code", "fail");
                } else {
                    returnJson.put("return_msg", "success");
                    returnJson.put("return_code", "success");
                }
            }
            //获取退回人员列表
            else if("rejectpersonnel".equalsIgnoreCase(actionType)){
                String org_Id = (String) jsonStrObject.get("org_id");
                org_Id=PubFunc.decrypt(org_Id);
                String viewType = (String) jsonStrObject.get("viewType");
                // 重置标识
                String resetFlag = null==jsonStrObject.get("resetFlag") ? "" : (String) jsonStrObject.get("resetFlag");
                ArrayList<HashMap<String,String>> dataList=kqDataSpService.listrejectPer(scheme_id, org_Id, viewType, resetFlag);
                JSONArray jsonList = new JSONArray();
                for (HashMap<String, String> map : dataList) {
                    map.put("userid",PubFunc.encrypt(map.get("userid")));
                    JSONObject jo = JSONObject.fromObject(map);
                    jsonList.add(jo);
                }
                JSONObject return_data = new JSONObject();
                return_data.put("datalist",jsonList);
                returnJson.put("return_data", return_data);
            }else if("getExportColumns".equalsIgnoreCase(actionType)){
                ArrayList fieldList = DataDictionary.getFieldList("Q35", Constant.USED_FIELD_SET);
                boolean isAddRQ = false;//是否添加过日期列了
                JSONArray jsonList = new JSONArray();
                JSONObject bean = null;
                bean = new JSONObject();
                bean.put("dataIndex","seq");
                bean.put("dataValue",ResourceFactory.getProperty("kq.feast_type_list.styleN"));
                bean.put("dataValueJx",ResourceFactory.getProperty("kq.feast_type_list.styleN"));
                jsonList.add(bean);
                KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
                String onlyFieldName =  kqDataUtil.getOnlyFieldName(this.frameconn);
                for(int i=0;i<fieldList.size();i++){
                    FieldItem fieldItem = (FieldItem)fieldList.get(i);
                    String itemid = fieldItem.getItemid();
                    // 去除没有启用的指标
                    if (!"1".equals(fieldItem.getUseflag())) {
                        continue;
                    }
                    // 去除隐藏的指标
                    if (!"1".equals(fieldItem.getState())) {
                        continue;
                    }
                    String itemdesc = fieldItem.getItemdesc();
                    if ("only_field".equalsIgnoreCase(itemid)) {
                    	itemdesc=onlyFieldName;
					}
                    if(itemid.toUpperCase().startsWith("Q35")
                            && StringUtils.isNumericSpace(itemid.substring(3))
                            && Integer.parseInt(itemid.substring(3))>=1
                            && Integer.parseInt(itemid.substring(3))<=31) {
                        if(isAddRQ)
                            continue;
                        bean = new JSONObject();
                        bean.put("dataIndex","dates");
                        bean.put("dataValue",ResourceFactory.getProperty("kq.data.sp.export.datecolumns"));
                        //超过5个字省略号显示
                        bean.put("dataValueJx",ResourceFactory.getProperty("kq.data.sp.export.datecolumns"));
                        jsonList.add(bean);
                        isAddRQ = true;
                        continue;
                    }
                    bean = new JSONObject();
                    bean.put("dataIndex",itemid);
                    bean.put("dataValue",itemdesc);
                    if(itemdesc.length()>5)
                        itemdesc = itemdesc.substring(0,5)+"...";
                    //超过5个字省略号显示
                    bean.put("dataValueJx",itemdesc);
                    jsonList.add(bean);
                }
                // 处理备注指标
//                bean = new JSONObject();
//                bean.put("dataIndex","memo");
//                bean.put("dataValue",ResourceFactory.getProperty("kq.shift.text"));
//                bean.put("dataValueJx",ResourceFactory.getProperty("kq.shift.text"));
//                jsonList.add(bean);
                
                JSONObject return_data = new JSONObject();
                return_data.put("datalist",jsonList);
                returnJson.put("return_data", return_data);
                returnJson.put("return_code", "success");
                returnJson.put("return_msg", "success");
            }else if("saveExportScheme".equalsIgnoreCase(actionType)){
                String detailsVal = jsonStrObject.getString("detailsVal");
                String sumsVal = jsonStrObject.getString("sumsVal");
                kqDataSpService.saveExportScheme(detailsVal,sumsVal,scheme_id);
                returnJson.put("return_code", "success");
                returnJson.put("return_msg", "success");
            }else if("getExportScheme".equalsIgnoreCase(actionType)){
                Map<String,String> map = kqDataSpService.getExportScheme(scheme_id);
                JSONObject return_data = new JSONObject();
                return_data.put("exportScheme",map);
                returnJson.put("return_code", "success");
                returnJson.put("return_msg", "success");
                returnJson.put("return_data",return_data);
            }else if("confirm".equalsIgnoreCase(actionType)){
            	
                kqDataSpService.doReplaceConfirm(jsonStrObject);
                JSONObject return_data = new JSONObject();
                returnJson.put("return_code", "success");
                returnJson.put("return_msg", "success");
                returnJson.put("return_data",return_data);
            } else if ("downward".equalsIgnoreCase(actionType)) {
                kqDataSpService.doDownward(scheme_id, kq_year, kq_duration, null, null);
                returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }//填写审批意见
            else if("fillProcess".equalsIgnoreCase(actionType)) {
            	String org_id = jsonStrObject.getString("org_id");
            	org_id=PubFunc.decrypt(org_id);
            	String sp_message = jsonStrObject.getString("sp_message");
            	String sp_flag = jsonStrObject.getString("sp_flag");
            	kqDataSpService.fillProcessMsg(kq_year, kq_duration, scheme_id, org_id,sp_message,sp_flag);
            	returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
            }//获取审批意见
            else if("getProcess".equalsIgnoreCase(actionType)) {
            	String org_id = jsonStrObject.getString("org_id");
            	org_id=PubFunc.decrypt(org_id);
            	List listProcessMsg = kqDataSpService.listProcessMsg(kq_year, kq_duration, scheme_id, org_id);
            	JSONObject return_data = new JSONObject();
            	return_data.put("list",listProcessMsg);
            	returnJson.put("return_msg", "success");
                returnJson.put("return_code", "success");
                returnJson.put("return_data", return_data);
            }
            this.getFormHM().put("returnStr", returnJson);

        } catch (Exception e) {
            returnJson.put("return_code", "fail");
            returnJson.put("return_msg", ((GeneralException) e).getErrorDescription());
            this.getFormHM().put("returnStr", returnJson);
        }

    }
    /**
     * 计算时校验应出勤是否设置单位（item_unit） 如果单位是时|分，校验班次是否设置了时长
     * @throws GeneralException 
     */
    private String checkCanCompute(String scheme_id) throws GeneralException {
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	String msg = "";
    	RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select item_unit from kq_item where lower(fielditemid) ='q3533' and item_unit is not null");
			rs = dao.search(sql.toString());
			FieldItem fieldItem = DataDictionary.getFieldItem("Q3533", "Q35");
			if(fieldItem!=null) {
				if(rs.next()) {
					String itemUnit = rs.getString("item_unit");
					//如果维护了应出勤的考勤项 并且设置了计量单位为时|分时，需要校验班次的工作时长
					if("01".equals(itemUnit)
							||"03".equals(itemUnit)) {
						SchemeMainService service = new SchemeMainServiceImpl(frameconn, userView);
						sql.setLength(0);
						sql.append(" and scheme_id=?");
						ArrayList list = new ArrayList();
						list.add(scheme_id);
						ArrayList<LazyDynaBean> schemeList = service.listKq_scheme(sql.toString(), list, null);
						String clazz = String.valueOf(schemeList.get(0).get("class_ids"));
						if(StringUtils.isNotBlank(clazz)) {
							String[] clss = clazz.split(",");
							if(clss.length>0) {
								sql.setLength(0);
								//class_id<>0 休息班次不校验时长
								sql.append("select work_hours from kq_class where class_id<>0 and class_id in (");
								List vals = new ArrayList();
								for(String c : clss) {
									sql.append("?,");
									vals.add(c);
								}
								sql.setLength(sql.length()-1);
								sql.append(") and "+Sql_switcher.isnull("work_hours", "0")+"=0");
								
								rs = dao.search(sql.toString(), vals);
								if(rs.next()) {
									msg = ResourceFactory.getProperty("kq.date.mx.compute.error");
								}
								
							}
							
						}
						sql.setLength(0);
						
						sql.append("");
					}
				}
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
		
	}

	private ArrayList<HashMap<String, Object>> listAllDurationLog(String scheme_id, KqDataSpService kqDataSpService
			, LazyDynaBean schemeBean) throws GeneralException {
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        try {
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            PeriodService periodService = new PeriodServiceImpl(this.getUserView(), this.getFrameconn());
            ArrayList<LazyDynaBean> allDuration = periodService.listKq_duration("", null, "kq_year,kq_duration");
            ArrayList<LazyDynaBean> listKq_extend = kqDataSpService.listKq_extend_log(" And scheme_id =? ", parameterList, "kq_year,kq_duration");
            // 提出去 年月条件在该方法中已无用 不需要一直循环
//            ArrayList<LazyDynaBean> listKq_scheme = schemeMainService.listKq_scheme(" and scheme_id=? ", parameterList, "", "", "");
            ArrayList org_scopeList =(ArrayList) schemeBean.get("org_map");
            for (LazyDynaBean bean : allDuration) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                String year = String.valueOf(bean.get("kq_year"));
                String month = String.valueOf(bean.get("kq_duration"));
                map.put("year", year);
                map.put("monthOrder", Integer.parseInt(month));
                //"月"
                map.put("desc", Integer.parseInt(month) + ResourceFactory.getProperty("label.query.month"));
                map.put("state", 2);
                map.put("kq_duration", month);
                
                //考勤表是否全部归档
                boolean allSpFlag = true;
                //当前区间内已创建考勤表的机构的数量
                int listKqExtendSize = 0;
                Iterator iterator = listKq_extend.iterator();
                while (iterator.hasNext()) {
                    LazyDynaBean b = (LazyDynaBean) iterator.next();
                    if (year.equals(b.get("kq_year")) && Integer.parseInt(month) == Integer.parseInt((String) b.get("kq_duration"))) {
                    	if((!"06".equals(b.get("sp_flag")) || 1 ==(Integer) map.get("state")) && allSpFlag)
                    		allSpFlag = false;
                    	
                        if (allSpFlag) {
                            map.put("state", 0);
                        } else {
                            map.put("state", 1);
                        }
                        iterator.remove();
                        listKqExtendSize++;
                    }
                }
                //【45650】考勤区间内考勤方案中包含的下级机构数量大于已创建考勤表的下级机构数量并且存在创建考勤表的机构时，月份的色块显示为绿色
                if(org_scopeList != null && listKqExtendSize > 0 && org_scopeList.size() > listKqExtendSize)
                	map.put("state", 1);
                
                dataList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return dataList;
    }

    /**
     * 计算前判断是否已维护过日明细数据或者统计项数据
     *
     * @param kq_year
     * @param kq_duration
     * @param scheme_id
     * @param orgId
     * @return falg =true 已有数据
     * @throws Exception
     */
    private boolean beforeCompute(String kq_year, String kq_duration, String scheme_id, String orgId) throws Exception {
        boolean flag = false;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer sql = new StringBuffer();
            sql.append("select * from q35 where (");
            for(int i=1;i<=31;i++){
                String temp = i<10?"0"+i:""+i;
                sql.append(Sql_switcher.isnull("q35"+temp, "''")+"<> '' or ");
            }
            sql.append("(q3533 is not null and q3533>0) or (q3535 is not null and q3535>0)) and kq_year=? and kq_duration=? and scheme_id=? ");
            List values = new ArrayList();
            values.add(kq_year);
            values.add(kq_duration);
            values.add(scheme_id);
            if (StringUtils.isNotBlank(orgId)) {
                sql.append(" and (");
                String[] oids = orgId.split(",");
                for (int i = 0; i < oids.length; i++) {
                    if (i > 0)
                        sql.append(" or ");
                    sql.append("org_id  like ?");
                    values.add(oids[i]+"%");
                }
                sql.append(")");
            }
            rs = dao.search(sql.toString(), values);
            if (rs.next())
                flag = true;
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }


}
