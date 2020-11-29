package com.hjsj.hrms.utils.sendmessage;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;


/**
 * 发送消息抽象类
 * <p>
 * 发送消息的类必须继承此方法以记录日志。
 * 此方法提供sendMsg方法用来发送记录，发送记录所需的收件人信息 可通过listMessageBeanBase方法获取
 *
 * @author ZhangHua
 * @version v75
 * @date 11:42 2018/11/14
 */
public abstract class AbstractSendMessage {
    /**
     * 邮件
     */
    public static final int EMAIL = 1;
    /**
     * 短信
     */
    public static final int MESSAGE = 2;
    /**
     * 微信
     */
    public static final int WEICHAT = 3;
    /**
     * 钉钉
     */
    public static final int DINGTALK = 4;
    /**
     * 系统消息
     */
    public static final int SYSTEMMESSAGE = 5;


    private UserView userView;
    private Connection connection;
    /**
     * 信息类型1-5  关联本类上述常量
     */
    private int massageType;
    /**
     * 所属模块id
     * 30：考勤     32:招聘
     * 33：绩效（日志）34：薪资
     * 37：人事异动 41：报表      39：保险
     * 58:关键目标 59：职称管理
     * 参照t_hr_subsys中内容
     */
    private String module_id;
    /**
     * 功能模块
     * 考勤：
     * 1=数据上报
     * 2=数据审批
     */
    private String function_id = "";

    /**
     * @param userView
     * @param connection
     * @param massageType 信息类型1-5  关联本类上述常量
     * @param module_id   所属模块id
     *                    30：考勤     32:招聘
     *                    33：绩效（日志）34：薪资
     *                    37：人事异动 41：报表      39：保险
     *                    58:关键目标 59：职称管理
     *                    参照t_hr_subsys中内容
     * @param function_id 功能模块号 关联function.xml 中的functionid
     * @author ZhangHua
     * @date 11:42 2018/11/14
     */
    public AbstractSendMessage(UserView userView, Connection connection, int massageType, String module_id, String function_id) {
        this.userView = userView;
        this.connection = connection;
        this.massageType = massageType;
        this.module_id = module_id;
        this.function_id = function_id;
    }

    /**
     * 发送通知方法
     * (此方法内需要调用insertSys_message插入记录 发送完成后使用updateMessageSendStatus更新状态)
     *
     * @param listMessageBean {
     *                        LazyDynaBean:[
     *                        message:(String),//消息内容 not null
     *                        mail_template_id:(int),//邮件模板ID null
     *                        send_address:(String),//邮件发件地址(非发邮件可为null) not null
     *                        receiver:(String),//接收人 not null
     *                        receiver_name:(String),//接收人姓名 not null1
     *                        receiver_address:(String),//接收地址 not null
     *                        receiver_b0110:(String),//接收人所属单位 not null
     *                        extra:(String),//附加信息 null
     *                        link_url:(String),//微信或钉钉消息点击跳转链接 null
     *                        ]
     *                        }
     */
    public abstract void sendMsg(ArrayList<LazyDynaBean> listMessageBean) throws GeneralException;


    /**
     * 添加记录表方法
     *
     * @param sys_MessageBean :[
     *                        message:(String),//消息内容 not null
     *                        mail_template_id:(int),//邮件模板ID null
     *                        send_address:(String),//邮件发件地址(非发邮件可为null) not null
     *                        receiver:(String),//接收人 not null
     *                        receiver_name:(String),//接收人姓名 not null
     *                        receiver_address:(String),//接收地址 not null
     *                        receiver_b0110:(String),//接收人所属单位 not null
     *                        extra:(String),//附加信息 null
     *                        link_url:(String),//微信或钉钉消息点击跳转链接 null
     *                        ]
     * @throws GeneralException
     */
    protected void insertSys_message(LazyDynaBean sys_MessageBean) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());

        try {
            HashMap messageMap = (HashMap) sys_MessageBean.getMap();
            RecordVo t_sys_messageVo = new RecordVo("t_sys_message");
            int id = Integer.parseInt(new IDFactoryBean().getId("t_sys_message.id", "", this.getConnection()));
            sys_MessageBean.set("id", id);
            t_sys_messageVo.setInt("id", id);
            t_sys_messageVo.setString("module_id", this.getModule_id());
            t_sys_messageVo.setString("function_id", this.getFunction_id());
            Date date = new Date();
            t_sys_messageVo.setDate("send_time", date);
            t_sys_messageVo.setDate("create_time", date);
            t_sys_messageVo.setDate("send_time", date);
            t_sys_messageVo.setInt("send_state", 0);
            if (StringUtils.isNotBlank(this.getUserView().getA0100())) {
                String key = this.getMyGuidKey();
                t_sys_messageVo.setString("send_user", key);
                if (!messageMap.containsKey("send_user_name")) {
                    t_sys_messageVo.setString("send_user_name", this.getUserView().getUserFullName());
                }else{
                    t_sys_messageVo.setString("send_user_name",(String) messageMap.get("send_user_name"));
                }
                t_sys_messageVo.setString("create_user", key);
            } else {
                t_sys_messageVo.setString("send_user", this.getUserView().getUserName());
                if (!messageMap.containsKey("send_user_name")) {
                    t_sys_messageVo.setString("send_user_name", this.getUserView().getUserFullName());
                }else{
                    t_sys_messageVo.setString("send_user_name",(String) messageMap.get("send_user_name"));
                }
                t_sys_messageVo.setString("create_user", this.getUserView().getUserName());
            }
            t_sys_messageVo.setString("message", String.valueOf(messageMap.get("message")));
            t_sys_messageVo.setString("receiver", String.valueOf(messageMap.get("receiver")));
            t_sys_messageVo.setString("receiver_name", String.valueOf(messageMap.get("receiver_name")));
            t_sys_messageVo.setString("receiver_address", String.valueOf(messageMap.get("receiver_address")));
            t_sys_messageVo.setString("receiver_b0110", String.valueOf(messageMap.get("receiver_b0110")));
            t_sys_messageVo.setInt("message_type",this.getMassageType());
            t_sys_messageVo.setInt("message_state", 0);
            if (messageMap.containsKey("extra")) {
                t_sys_messageVo.setString("extra", String.valueOf(messageMap.get("extra")));
            }

            if (messageMap.containsKey("mail_template_id")) {
                t_sys_messageVo.setInt("mail_template_id", Integer.valueOf((String) messageMap.get("mail_template_id")));
            }
            if (messageMap.containsKey("send_address")) {
                t_sys_messageVo.setString("send_address", String.valueOf(messageMap.get("send_address")));
            }
            if (messageMap.containsKey("link_url")) {
                t_sys_messageVo.setInt("link_url", Integer.valueOf((String) messageMap.get("link_url")));
            }
            dao.addValueObject(t_sys_messageVo);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }


    }

    /**
     * 删除邮件记录表统一方法(默认删除当前模块下 本人发送记录)
     * @param strWhere 开头需要加and
     * @param parameterList
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:45 2018/12/8
     */
    public void cleanSysMessageBySql(String strWhere,ArrayList parameterList) throws GeneralException {
        ContentDAO dao=new ContentDAO(this.getConnection());
        try{
            StringBuffer strSql=new StringBuffer();
            ArrayList dataList=new ArrayList();

            strSql.append("delete from t_sys_message where ");
            strSql.append(" module_id =? ");
            dataList.add(this.getModule_id());
            if(StringUtils.isNotBlank(strWhere)){
                strSql.append(strWhere);
                if(parameterList!=null&&parameterList.size()>0){
                    dataList.addAll(parameterList);
                }
            }else{
                strSql.append(" and function_id=? and send_user=?");
                dataList.add(this.getFunction_id());
                dataList.add(this.getUserView().getUserName());
            }
            dao.delete(strSql.toString(),dataList);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新发送状态
     *
     * @param sys_MessageBean
     * @param status          =0; //末发 =1; //已发成功
     *                        =2; //发送失败 =3; //发送中
     * @throws GeneralException
     * @author ZhangHua
     * @date 14:24 2018/11/14
     */
    public void updateMessageSendStatus(LazyDynaBean sys_MessageBean, int status) throws GeneralException {
        try {
            this.updateMessageByField(sys_MessageBean, "send_state", status);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 通过id更新发送状态
     *
     * @param sys_MessageBean bean中必须包含id 字段
     * @param status          =0; //末发 =1; //已发成功
     *                        =2; //发送失败 =3; //发送中
     * @throws GeneralException
     * @author ZhangHua
     * @date 14:24 2018/11/14
     */
    public void updateMessageSendStatusByid(ArrayList<LazyDynaBean> sys_MessageBean, int status) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        try {
            StringBuffer strSql = new StringBuffer(" update t_sys_message set send_state=");
            strSql.append(status);
            strSql.append(" where id in (");
            ArrayList<String> dataList = new ArrayList<String>();
            StringBuffer strWhere = new StringBuffer();
            for (int i = 0; i < sys_MessageBean.size(); i++) {
                dataList.add(sys_MessageBean.get(i).get("id").toString());
                strWhere.append("?,");
                if (i != 0 && i % 300 == 0) {
                    strWhere.deleteCharAt(strWhere.length() - 1);
                    strWhere.append(")");
                    dao.update(strSql.toString() + strWhere.toString(), dataList);
                    strWhere.setLength(0);
                    dataList.clear();
                }
            }
            strWhere.deleteCharAt(strWhere.length() - 1);
            strWhere.append(")");
            dao.update(strSql.toString() + strWhere.toString(), dataList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }


    /**
     * 修改信息记录方法
     *
     * @param sys_MessageBean
     * @param field           要修改的字段
     * @param value           值
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:55 2018/11/16
     */
    protected void updateMessageByField(LazyDynaBean sys_MessageBean, String field, Object value) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        try {
            HashMap messageMap = (HashMap) sys_MessageBean.getMap();
            RecordVo t_sys_messageVo = new RecordVo("t_sys_message");
            if (messageMap.containsKey("id")) {
            	// 60332 获取id失败
                t_sys_messageVo.setInt("id", Integer.parseInt(String.valueOf(messageMap.get("id"))));
                t_sys_messageVo = dao.findByPrimaryKey(t_sys_messageVo);
            } else {
                t_sys_messageVo.setString("module_id", this.getModule_id());
                t_sys_messageVo.setString("function_id", this.getFunction_id());

                if (messageMap.containsKey("send_user")) {
                    t_sys_messageVo.setString("send_user", String.valueOf(messageMap.get("send_user")));
                }
                if (messageMap.containsKey("receiver")) {
                    t_sys_messageVo.setString("receiver", String.valueOf(messageMap.get("receiver")));
                }
                if (messageMap.containsKey("mail_template_id")) {
                    t_sys_messageVo.setInt("mail_template_id", (Integer) messageMap.get("mail_template_id"));
                }
                if (messageMap.containsKey("extra")) {
                    t_sys_messageVo.setString("extra", String.valueOf(messageMap.get("extra")));
                }
            }

            if (dao.isExistRecordVo(t_sys_messageVo)) {
                if (value instanceof Integer) {
                    t_sys_messageVo.setInt(field, (Integer) value);
                } else if (value instanceof String) {
                    t_sys_messageVo.setString(field, (String) value);
                }
                dao.updateValueObject(t_sys_messageVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }


    /**
     * 获取根据发送信息所需bean的格式 获取并拼接收件人的基本参数 包括guidkey,b0110,a0101,和所需的邮箱或者微信等通讯指标
     *
     * @param msgList bean中必须包含 nbase 和a0100。
     * @return 传入的bean加入收件人基本参数后无序原样传回
     * @throws GeneralException
     * @author ZhangHua
     * @date 13:42 2018/11/15
     */
    public ArrayList<LazyDynaBean> listMessageBeanBase(ArrayList<LazyDynaBean> msgList) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        RowSet rs = null;
        ArrayList<LazyDynaBean> newMsgList = new ArrayList<LazyDynaBean>();
        try {
            HashMap<String, ArrayList<String>> dbNameMap = new HashMap<String, ArrayList<String>>();
            String msgField = "";
            StringBuffer strSql = new StringBuffer("select guidkey,b0110,a0101,a0100 ");

            switch (this.getMassageType()) {
                case AbstractSendMessage.EMAIL:
                    msgField = this.getConstantField(2);
                    break;
                case AbstractSendMessage.MESSAGE:
                    msgField = this.getConstantField(1);
                    break;
                case AbstractSendMessage.WEICHAT:
                    msgField = this.getConstantField(4);
                    break;
                case AbstractSendMessage.DINGTALK:
                    msgField = this.getConstantField(4);
                    break;
                case AbstractSendMessage.SYSTEMMESSAGE:
                    msgField = "";
                    break;
            }
            if (StringUtils.isNotBlank(msgField)) {
                strSql.append(",").append(msgField);
            }
            strSql.append(" from ");
            StringBuffer sqlWhere = new StringBuffer();


            for (int i = 0; i < msgList.size(); i++) {

                LazyDynaBean bean = msgList.get(i);
                String nbase = (String) bean.get("nbase");

                if (dbNameMap.containsKey(nbase)) {
                    dbNameMap.get(nbase).add((String) bean.get("a0100"));
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add((String) bean.get("a0100"));
                    dbNameMap.put(nbase, list);
                }
            }
            Iterator iterator = dbNameMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, ArrayList<String>> map = (Map.Entry<String, ArrayList<String>>) iterator.next();
                String nbase = map.getKey();
                ArrayList<String> a0100List = map.getValue();
                ArrayList<String> dataList = new ArrayList<String>();

                sqlWhere.setLength(0);
                sqlWhere.append(nbase).append("A01 where a0100 in(");
                for (int i = 0; i < a0100List.size(); i++) {
                    sqlWhere.append("?,");
                    dataList.add(a0100List.get(i));
                    if (i != 0 && i % 300 == 0) {

                        sqlWhere.deleteCharAt(sqlWhere.length() - 1);
                        sqlWhere.append(")");
                        rs = dao.search(strSql.toString() + sqlWhere.toString(), dataList);
                        while (rs.next()) {
                            String a0100 = rs.getString("a0100");
                            Iterator imsgList = msgList.iterator();
                            while (imsgList.hasNext()) {
                                LazyDynaBean bean = (LazyDynaBean) imsgList.next();
                                if (String.valueOf(bean.get("a0100")).equals(a0100) && String.valueOf(bean.get("nbase")).equals(nbase)) {
                                    bean.set("receiver", rs.getString("guidkey"));
                                    bean.set("receiver_name", rs.getString("a0101"));
                                    bean.set("receiver_b0110", rs.getString("b0110"));
                                    if (StringUtils.isNotBlank(msgField)) {
                                        bean.set("receiver_address", rs.getString(msgField));
                                    }
                                    newMsgList.add(bean);
                                    imsgList.remove();
                                }
                            }
                        }
                        sqlWhere.setLength(0);
                        sqlWhere.append(nbase).append("A01 where a0100 in(");
                        dataList.clear();

                    }

                }
                sqlWhere.deleteCharAt(sqlWhere.length() - 1);
                sqlWhere.append(")");


                rs = dao.search(strSql.toString() + sqlWhere.toString(), a0100List);
                while (rs.next()) {
                    String a0100 = rs.getString("a0100");

                    Iterator imsgList = msgList.iterator();
                    while (imsgList.hasNext()) {
                        LazyDynaBean bean = (LazyDynaBean) imsgList.next();
                        if (String.valueOf(bean.get("a0100")).equals(a0100) && String.valueOf(bean.get("nbase")).equals(nbase)) {
                            bean.set("receiver", rs.getString("guidkey"));
                            bean.set("receiver_name", rs.getString("a0101"));
                            bean.set("receiver_b0110", rs.getString("b0110"));
                            if (StringUtils.isNotBlank(msgField)) {
                                bean.set("receiver_address", rs.getString(msgField));
                            }
                            newMsgList.add(bean);
                            imsgList.remove();
                        }
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return newMsgList;
    }

    /**
     * 通过备注获取信息发送是否成功，只要有一条失败即为失败 in方式
     * @param extra
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:08 2018/11/24
     */
    public boolean getSendSucceedByExtra(ArrayList<String> extra) throws GeneralException {
        ContentDAO dao=new ContentDAO(this.getConnection());
        RowSet rs=null;
        StringBuffer strSql=new StringBuffer();
        strSql.append("select ").append(Sql_switcher.isnull("message_state", "''"));
        strSql.append(" as message_state from t_sys_message where ");
        strSql.append(" message_type =").append(this.getMassageType()).append(" and ");
        strSql.append(" extra in (");
        if(extra.size()==0)
            return false;
        try{
            for (int i = 0; i < extra.size(); i++) {
                strSql.append("?,");
            }
            strSql.deleteCharAt(strSql.length()-1);
            strSql.append(")");
            rs=dao.search(strSql.toString(),extra);
            while (rs.next()){
                if(rs.getString("message_state")!="1"){
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return true;
    }
    /**
     * 获取邮件发送是否成功 只要有一条失败即为失败 like方式
     * @param strWhere
     * @param parameterList
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:08 2018/11/24
     */
    public boolean getSendStatusByExtra(String strWhere ,ArrayList parameterList ) throws GeneralException {
        ContentDAO dao=new ContentDAO(this.getConnection());
        RowSet rs=null;
        StringBuffer strSql=new StringBuffer();

        strSql.append("select ").append(Sql_switcher.isnull("send_state", "''"));
        strSql.append(" as send_state from t_sys_message where ");
        strSql.append(" message_type =?").append(" and ");

        if(StringUtils.isNotBlank(strWhere)){
            strSql.append(strWhere);
        }
        ArrayList list=new ArrayList();
        list.add(this.getMassageType());
        if(parameterList!=null&&parameterList.size()>0){
            list.addAll(parameterList);
        }
        boolean iserr;
        boolean needwait;

        try{
            for(int i=0;i<20;i++) {
                iserr=false;
                needwait=false;
                rs = dao.search(strSql.toString(), list);
                while (rs.next()) {
                    if ("2".equals(rs.getString("send_state"))) {
                        iserr=true;
                    }
                    if("0".equals(rs.getString("send_state"))){
                        needwait=true;
                    }
                }

                if(needwait){
                    Thread.sleep(500);
                }else{
                    return !iserr;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return false;
    }


    /**
     * 获取当前用户的guidkey
     *
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:48 2018/11/16
     */
    protected String getMyGuidKey() throws GeneralException {
        String guid = "";
        RowSet frowset = null;
        ContentDAO dao = new ContentDAO(this.getConnection());
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append(" select GUIDKEY from ");
            strSql.append(this.getUserView().getDbname()).append("A01");
            strSql.append(" where a0100='").append(this.getUserView().getA0100()).append("'");

            frowset = dao.search(strSql.toString());
            if (frowset.next() && frowset.getString("GUIDKEY") != null) {
                guid = frowset.getString("GUIDKEY");
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(frowset);
        }
        return guid;
    }

    /**
     * 取系统的通信指标
     *
     * @param type 1电话指标 2邮件指标 3邮件服务器地址4认证用户名
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:48 2018/11/16
     */
    protected String getConstantField(int type) throws GeneralException {
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
                case 4:
                	field = "SS_LOGIN_USER_PWD";
                	break;
                default:break;
            }

            RecordVo vo = ConstantParamter.getConstantVo(field);
            if (vo == null)
                return "";
            String field_name = vo.getString("str_value");
            if(type == 4){
				int idx = field_name.indexOf(",");
				if (idx == -1) {
					field_name = "username";
				} else {
					field_name = field_name.substring(0, idx);
					if ("#".equals(field_name) || "".equals(field_name)) {
						field_name = "username";
					}
				}
				return field_name;
			}
            if (field_name == null || "".equals(field_name))
                return "";

            if (type == 3) {

                Document doc = PubFunc.generateDom(field_name);;
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

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getMassageType() {
        return massageType;
    }

    public void setMassageType(int massageType) {
        this.massageType = massageType;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getFunction_id() {
        return function_id;
    }

    public void setFunction_id(String function_id) {
        this.function_id = function_id;
    }
}
