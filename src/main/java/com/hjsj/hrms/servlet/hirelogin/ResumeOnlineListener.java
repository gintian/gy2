package com.hjsj.hrms.servlet.hirelogin;

import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.facade.utility.WriteLog;
import org.apache.commons.collections.FastHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;

/**
 * @author Auto
 */
public class ResumeOnlineListener implements HttpSessionBindingListener, Serializable {
    @Override
    public void valueBound(HttpSessionBindingEvent paramHttpSessionBindingEvent) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent paramHttpSessionBindingEvent) {
        HttpSession httpSession = paramHttpSessionBindingEvent.getSession();
        if (httpSession == null) {
            return;
        }
        FastHashMap fastHashMap = (FastHashMap) httpSession.getServletContext().getAttribute("userNames");
        if (fastHashMap == null) {
            return;
        }
        String str = httpSession.getId();
        OnlineUserView onlineUserView = (OnlineUserView) fastHashMap.get(str);
        if (onlineUserView != null) {
            String str1 = onlineUserView.getUserId();
            fastHashMap.remove(str);
            WriteLog.writeExitLog(onlineUserView);
        }
    }
}
