package com.hjsj.hrms.module.dashboard.portlets;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.Arrays;

public class PortletProxyTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {

        if(this.getFormHM().containsKey("password")){
            String password = (String)this.getFormHM().get("password");
            if(!this.getUserView().getPassWord().equals(password)){
                return;
            }
        }

        this.getFormHM().put("pwdmatch",true);

        JSONObject portletConfig = (JSONObject)this.getFormHM().get("portletConfig");
        int widgetid  = (Integer)portletConfig.get("widgetid");
        RowSet rowset = null;
        try {
            String sql = "select classname from t_sys_portal_widget where widgetid=?";
            rowset = new ContentDAO(this.getFrameconn()).search(sql, Arrays.asList(widgetid));
            String classname=null;
            if(rowset.next())
                classname = rowset.getString(1);
            IBusiness dataProvider = (IBusiness)Class.forName(classname).newInstance();
            dataProvider.setFormHM(this.getFormHM());
            dataProvider.setFrameconn(this.getFrameconn());
            dataProvider.setUserView(this.userView);
            dataProvider.execute();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            PubFunc.closeResource(rowset);
        }

    }
}
