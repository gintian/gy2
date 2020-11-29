package com.hjsj.hrms.utils.sendmessage.email;

import com.hjsj.hrms.utils.sendmessage.AbstractSendMessage;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 发放邮件公共类
 * <p>
 * 此方法提供sendMsg方法用来发送记录，发送记录所需的收件人信息 可通过listMessageBeanBase方法获取
 *
 * @author ZhangHua
 * @version v75
 * @date 11:23 2018/11/16
 */
public class SendEmailUtil extends AbstractSendMessage {
    /**
     * @param userView
     * @param connection
     * @param module_id   所属模块id
     *                    30：考勤     32:招聘
     *                    33：绩效（日志）34：薪资
     *                    37：人事异动 41：报表      39：保险
     *                    58:关键目标 59：职称管理
     *                    参照t_hr_subsys中内容
     * @param function_id 功能模块
     * @author ZhangHua
     * @date 11:42 2018/11/14
     */
    public SendEmailUtil(UserView userView, Connection connection, String module_id, String function_id) {
        super(userView, connection, SendEmailUtil.EMAIL, module_id, function_id);
    }

    /**
     * 发送通知方法
     * (此方法内需要调用insertSys_message插入记录 发送完成后使用updateMessageSendStatus更新状态)
     *
     * @param listMessageBean {
     *                        LazyDynaBean:[
     *                        id:(int),//t_sys_message表主键 若在调用此方法前已经在t_sys_message表中生成记录，则可以
     *                        将id传入，将记录更新为发送中状态
     *                        title:(String),//邮件标题 not null
     *                        attach:(String),//邮件的单个附件 这里指的是绝对路径和名字 null
     *                        attachList:(String),//邮件的多个附件 这里指的是绝对路径和名字 null
     *                        returnAddress:(String),//邮件回复地址 null
     *                        hrefDesc:(String),//查看链接的描述，默认是"查 看" null
     *                        message:(String),//消息内容 not null
     *                        mail_template_id:(int),//邮件模板ID null
     *                        send_user_name:(String)//发送用户名称 null 默认业务账号为账号全称，自助用户为姓名
     *                        send_address:(String),//邮件发件地址,(发邮件时默认为邮件服务器地址，非发邮件可为null) not null
     *                        receiver:(String),//接收人 not null
     *                        receiver_name:(String),//接收人姓名 not null
     *                        receiver_address:(String),//接收地址 not null
     *                        receiver_b0110:(String),//接收人所属单位 not null
     *                        extra:(String),//附加信息 null
     *                        link_url:(String),//点击跳转链接 null
     *                        ]
     *                        }
     */
    @Override
    public void sendMsg(ArrayList<LazyDynaBean> listMessageBean) throws GeneralException {

        try {
            ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
            LazyDynaBean emailBean;
            if (this.getMassageType() == AbstractSendMessage.EMAIL) {
                String emailAddress = this.getConstantField(3);
                if (StringUtils.isBlank(emailAddress)) {
                    throw GeneralExceptionHandler.Handle(new Exception("未配置邮件服务器！"));
                }

                for (LazyDynaBean bean : listMessageBean) {
                    HashMap map = (HashMap) bean.getMap();
                    emailBean = new LazyDynaBean();
                    if (map.containsKey("title")) {
                        emailBean.set("subject", map.get("title"));
                    }
                    if (map.containsKey("attach")) {
                        emailBean.set("attach", map.get("attach"));
                    }
                    if (map.containsKey("attachList")) {
                        emailBean.set("attachList", map.get("attachList"));
                    }
                    if (map.containsKey("returnAddress")) {
                        emailBean.set("returnAddress", map.get("returnAddress"));
                    } else {
                        emailBean.set("returnAddress", "");
                    }
                    if (map.containsKey("message")) {
                        emailBean.set("bodyText", map.get("message"));
                    }
                    if (map.containsKey("link_url")) {
                        emailBean.set("href", map.get("link_url"));
                    }
                    if (map.containsKey("hrefDesc")) {
                        emailBean.set("hrefDesc", map.get("hrefDesc"));
                    }
                    if (map.containsKey("receiver_address")&&StringUtils.isNotBlank((String) map.get("receiver_address"))) {
                        emailBean.set("toAddr", map.get("receiver_address"));
                    }else{
                        continue;
                    }

                    bean.set("send_address", emailAddress);
                    if (map.containsKey("id")) {
                        this.updateMessageSendStatus(bean, 3);
                    } else {
                        this.insertSys_message(bean);
                    }
                    emailBean.set("id", bean.get("id"));
                    list.add(emailBean);
                }
                IAsyncEmailIsSuccessIF sendMsgIsSuccess = new AsyncEmailIsSuccessIF();
                AsyncEmailBo bo = new AsyncEmailBo(this.getConnection(), this.getUserView(), sendMsgIsSuccess);
                bo.send(list);

            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        }

    }


}
