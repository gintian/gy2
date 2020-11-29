package com.hjsj.hrms.utils.sendmessage.dingtalk;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.utils.sendmessage.AbstractSendMessage;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 发送钉钉消息的类型 目前是根据URL判断的 URL为空 发送纯文本消息，否则发送图文消息
 * 后期会考虑优化
 * 发送多条钉钉消息是循环调用接口，并不是一次调用钉钉接口发送多条。
 *
 * @author xus
 */
public class SendDingTalkUtil extends AbstractSendMessage {
    private static final int SINGEL_MESSAGE = 0;//单条图文消息
    private static final int MUTIPLE_MESSAGE = 1;//多图文消息

    /**
     * @param userView
     * @param connection
     * @param module_id   所属模块id 30：考勤     32:招聘
     *                    33：绩效（日志）34：薪资
     *                    37：人事异动 41：报表      39：保险
     *                    58:关键目标 59：职称管理
     *                    参照t_hr_subsys中内容
     * @param function_id 功能模块
     * @author XuShuo
     */
    public SendDingTalkUtil(UserView userView, Connection connection, String module_id,
                            String function_id) {
        super(userView, connection, SendDingTalkUtil.DINGTALK, module_id, function_id);
    }

    /**
     * 发送通知方法
     * (此方法内需要调用insertSys_message插入记录 发送完成后使用updateMessageSendStatus更新状态)
     *
     * @param listMessageBean {
     *                        //当msgType==0时：
     *                        LazyDynaBean:[
     *                        title:(String),//消息标题not null
     *                        message:(String),//消息内容 not null
     *                        extra:(String),//附加信息 null
     *                        link_url:(String),//微信或钉钉消息点击跳转链接 null
     *                        picUrl:(String),//微信或钉钉消息图片路径
     *                        msgType:(int),//消息类型 ：0 单条消息 ；
     *                        ]
     *                        }
     */
    @Override
    public void sendMsg(ArrayList<LazyDynaBean> listMessageBean) throws GeneralException {

        try {
        	// 60332 改成与邮件方式一样 在该接口外调用获取list
//            listMessageBean = listMessageBeanBase(listMessageBean);
            for (LazyDynaBean bean : listMessageBean) {
                HashMap map=(HashMap )bean.getMap();
                int msgType = map.containsKey("msgType") ? (Integer) map.get("msgType"):0  ; //消息类型
                boolean isSuccess = false;
                if (msgType == SINGEL_MESSAGE) {
                    //单条图文消息
                    String username = (String) map.get("username");
                    String title = (String) map.get("title");
                    String description = (String) map.get("message");
                    String picUrl = (String) map.get("picUrl");
                    String url = (String) map.get("link_url");
                    this.insertSys_message(bean);
                    isSuccess = DTalkBo.sendMessage(username, title, description, picUrl, url);
                }
                //其他类型消息在此处添加类型即可

                //status          =0; //末发 =1; //已发成功  =2; //发送失败 =3; //发送中
                int status = 3;
                if (isSuccess)
                    status = 1;
                else
                    status = 2;
                this.updateMessageSendStatus(bean, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
