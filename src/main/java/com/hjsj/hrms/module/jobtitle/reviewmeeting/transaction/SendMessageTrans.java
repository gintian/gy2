package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p> 评审会议--通知提醒 </p>
 * <p>create time  2016-5-17 上午10:11:35</p>
 * @author linbz
 */
public class SendMessageTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        
        try {
            ReviewMeetingBo bo = new ReviewMeetingBo(this.frameconn,this.userView);
            String tabId = (String)this.getFormHM().get("tabId");//当前页面id
            String w0301 = (String)this.getFormHM().get("w0301");//会议编号
            w0301 = PubFunc.decrypt(w0301);
            ArrayList subjectlist_e = (ArrayList)this.getFormHM().get("subjectlist");//学科组
            String contenMsg = (String)this.getFormHM().get("contenMsg");//消息内容
            ArrayList sendlist = (ArrayList)this.getFormHM().get("sendlist");//发送方式
            boolean isNewModule = (Boolean)this.getFormHM().get("isNewModule");//是否是新版评审会议调用
            
            ArrayList subjectlist = new ArrayList();
            for(int i=0;i<subjectlist_e.size();i++){
                String subjectid = (String)subjectlist_e.get(i);
                subjectid = PubFunc.decrypt(subjectid);
                subjectlist.add(subjectid);
            }
            String msg = "";
            //判断当前页面为哪个页面
            if(tabId!=null && !"".equalsIgnoreCase(tabId)) {
                if("declarer".equalsIgnoreCase(tabId)){//申报人
                    msg = bo.getDeclarer(w0301, contenMsg, sendlist);
                    
                }else if("subject".equalsIgnoreCase(tabId)){//学科组
                    msg = bo.getSubject(w0301, contenMsg, sendlist, subjectlist);
                    
                }else if("judges".equalsIgnoreCase(tabId)){//评委会
            		msg = bo.getJudges(w0301, contenMsg, sendlist,1,isNewModule);
                }else if("subcommitte".equalsIgnoreCase(tabId)){//二级单位
            		msg = bo.getJudges(w0301, contenMsg, sendlist,4,isNewModule);
                }
            }
            this.getFormHM().put("info", msg);//返回信息
            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
    }
    
}
