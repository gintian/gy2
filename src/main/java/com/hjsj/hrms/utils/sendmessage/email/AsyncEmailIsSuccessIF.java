package com.hjsj.hrms.utils.sendmessage.email;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.HashMap;

/**
 * 发送邮件获取返回信息方法
 * @author ZhangHua
 * @date 11:26 2018/11/16
 * @version V75
 */
public class AsyncEmailIsSuccessIF implements IAsyncEmailIsSuccessIF {
    /**
     * 更新发送状态发放，可在此方法中独立获取数据库连接，进行数据更新操作。
     *
     * @param emailContent 调用AsyncEmailBo.java send方法时传入的bean
     * @param isSuccess    若成功返回值为""，若失败，返回值为错误信息。
     */
    public void sendEmailIsSuccess(LazyDynaBean emailContent, String isSuccess) {
        //1 发送成功 2发送失败
        int flag= "".equals(isSuccess.trim())?1:2;
        Connection conn = null;
        try
        {
            conn= AdminDb.getConnection();
            ContentDAO dao=new ContentDAO(conn);
            RecordVo t_sys_messageVo = new RecordVo("t_sys_message");
            HashMap map= (HashMap) emailContent.getMap();
            if(map.containsKey("id")){
                t_sys_messageVo.setInt("id", (Integer) map.get("id"));
                t_sys_messageVo=dao.findByPrimaryKey(t_sys_messageVo);
                t_sys_messageVo.setInt("send_state",flag);
                dao.updateValueObject(t_sys_messageVo);
            }

        }catch(Exception s){
            s.printStackTrace();
        }finally{
            PubFunc.closeDbObj(conn);
        }
    }
}
