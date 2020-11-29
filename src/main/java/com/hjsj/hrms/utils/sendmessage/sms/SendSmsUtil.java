package com.hjsj.hrms.utils.sendmessage.sms;

import com.hjsj.hrms.businessobject.sys.SmsBo;
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
 * 发送短信公共类
 * <p>
 * 此方法提供sendMsg方法用来发送记录，发送记录所需的收件人信息 可通过listMessageBeanBase方法获取
 *
 * @author ZhangHua
 * @version v75
 * @date 10:46 2018/11/16
 */
public class SendSmsUtil extends AbstractSendMessage {
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
    public SendSmsUtil(UserView userView, Connection connection, String module_id, String function_id) {
        super(userView, connection, SendSmsUtil.MESSAGE, module_id, function_id);
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
    @Override
    public void sendMsg(ArrayList<LazyDynaBean> listMessageBean) throws GeneralException {

        try {
            ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
            LazyDynaBean emailBean;
            for (LazyDynaBean bean : listMessageBean) {
                HashMap map = (HashMap) bean.getMap();
                emailBean = new LazyDynaBean();
                if (map.containsKey("send_user_name")) {
                    emailBean.set("sender", map.get("send_user_name"));
                } else {
                    if (StringUtils.isNotBlank(this.getUserView().getA0100())) {
                        emailBean.set("send_user_name", this.getUserView().getUserName());
                    } else {
                        emailBean.set("send_user_name", this.getUserView().getUserFullName());
                    }

                }
                if (map.containsKey("receiver_name")) {
                    emailBean.set("receiver", map.get("receiver_name"));
                }
                if (map.containsKey("receiver_address")) {
                    emailBean.set("phone_num", map.get("receiver_address"));
                }
                if (map.containsKey("message")) {
                    emailBean.set("msg", map.get("message"));
                }

                if (map.containsKey("id")) {
                    this.updateMessageSendStatus(bean, 3);
                } else {
                    this.insertSys_message(bean);
                }
                emailBean.set("id", bean.get("id"));
                list.add(emailBean);
            }
            SmsBo smsbo = new SmsBo(this.getConnection());
            boolean isSuccess = true;
            try {
                smsbo.batchSendMessage(list);
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            }
            if (!isSuccess) {
                this.updateMessageSendStatusByid(list, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
