package com.hjsj.hrms.utils.components.tablefactory.businessobject;

import com.google.gson.Gson;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ExportSettingsModel;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportReportBO {


    private UserView userView;
    private Connection connection;

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

    public ExportReportBO(UserView userView, Connection connection) {
        this.userView = userView;
        this.connection = connection;
    }

    /**
     * 获取配置
     * @param submoduleid
     * @return
     * @throws SQLException
     */
    public String[] getExportPageOptions(String submoduleid) throws SQLException {

        ContentDAO dao = new ContentDAO(this.getConnection());
        RowSet rowSet = null;
        String str = "select id,settings from t_sys_table_export_settings where submoduleid=? ";
        String []data=new String [2];
        try {
            ArrayList list = new ArrayList(1);
            list.add(submoduleid);
            rowSet = dao.search(str, list);
            if (rowSet.next()) {
                data[0]=rowSet.getString("id");
                data[1]=rowSet.getString("settings");
            }else{
                ExportSettingsModel exportSettingsModel=this.initExportSettingsModel();
                data[0]="0";
                data[1]=exportSettingsModel.toJson();
            }
        } finally {
            PubFunc.closeDbObj(rowSet);
        }

        return data;
    }
    /**
     * 获取配置
     * @param submoduleid
     * @return
     * @throws SQLException
     */
    public ExportSettingsModel getExportSettingsModel(String submoduleid) throws SQLException {

        ContentDAO dao = new ContentDAO(this.getConnection());
        RowSet rowSet = null;
        String str = "select settings from t_sys_table_export_settings where submoduleid=? ";
        ExportSettingsModel exportSettingsModel=null;
        try {
            ArrayList list = new ArrayList(1);
            DBMetaModel dbMetaModel=new DBMetaModel(this.getConnection());
            if(!dbMetaModel.isHaveTheTable("t_sys_table_export_settings")){
                return this.initExportSettingsModel();
            }
            list.add(submoduleid);
            rowSet = dao.search(str, list);
            if (rowSet.next()) {
                String settings=rowSet.getString("settings");
                Gson gson=new Gson();
                exportSettingsModel=gson.fromJson(settings,ExportSettingsModel.class);
            }else{
                exportSettingsModel=this.initExportSettingsModel();
            }
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return exportSettingsModel;
    }


    /**
     * 新增配置
     * @param exportSettingsModel
     * @param submoduleid
     * @return
     * @throws SQLException
     * @author ZhangHua
     * @date 14:35 2019/12/27
     */
    public int addExportPageOptions(ExportSettingsModel exportSettingsModel, String submoduleid) throws SQLException, GeneralException {

        ContentDAO dao = new ContentDAO(this.getConnection());
        String jsonStr = exportSettingsModel.toJson();

        int sid=Integer.parseInt(new IDGenerator(2,this.getConnection()).getId("t_sys_table_export_settings.id"));
        String str = "insert into t_sys_table_export_settings (id,submoduleid,settings,create_username,update_username,create_time,update_time)" +
                " values (?,?,?,?,?," + Sql_switcher.sqlNow() + "," + Sql_switcher.sqlNow() + ")";
        ArrayList list = new ArrayList();
        list.add(sid);
        list.add(submoduleid);
        list.add(jsonStr);
        list.add(this.getUserView().getUserName());
        list.add(this.getUserView().getUserName());
        dao.update(str, list);
        return sid;
    }


    /**
     * 更新配置
     * @param exportSettingsModel
     * @param settingsId
     * @return
     * @throws SQLException
     */
    public boolean updateExportPageOptions(ExportSettingsModel exportSettingsModel, int settingsId) throws SQLException {

        ContentDAO dao = new ContentDAO(this.getConnection());
        String jsonStr = exportSettingsModel.toJson();

        String str = "update t_sys_table_export_settings set  settings=? ,update_username=?,update_time=" +Sql_switcher.sqlNow()+" where id=?";
        ArrayList list = new ArrayList();
        list.add(jsonStr);
        list.add(this.getUserView().getUserName());
        list.add(settingsId);
        dao.update(str, list);
        return true;
    }


    /**
     * 初始化页面设置
     * @return
     * @author ZhangHua
     * @date 15:07 2019/12/28
     */
    public ExportSettingsModel initExportSettingsModel(){

        ExportSettingsModel exportSettingsModel=new ExportSettingsModel();
        exportSettingsModel.setPagetype("A4");
        exportSettingsModel.setHeight(297);
        exportSettingsModel.setWidth(210);
        exportSettingsModel.setOrientation("1");

        exportSettingsModel.setTitle_content("");
        exportSettingsModel.setTitle_fontface("楷体_GB2312");
        exportSettingsModel.setTitle_fontsize(16);
        exportSettingsModel.setTitle_color("#000000");
        exportSettingsModel.setTitle_fontblob("");
        exportSettingsModel.setTitle_underline("");
        exportSettingsModel.setTitle_delline("");
        exportSettingsModel.setTitle_fontitalic("");
//        exportSettingsModel.setTitle_fontblob("#fb[1]");
//        exportSettingsModel.setTitle_underline("#fu[1]");
//        exportSettingsModel.setTitle_delline("#fs[1]");
//        exportSettingsModel.setTitle_fontitalic("#fi[1]");

        exportSettingsModel.setHead_left("");
        exportSettingsModel.setHead_center("");
        exportSettingsModel.setHead_right("");
        exportSettingsModel.setHead_flw_hs("");
        exportSettingsModel.setHead_frw_hs("");
        exportSettingsModel.setHead_fmw_hs("");
//        exportSettingsModel.setHead_flw_hs("lHeadChecked");
//        exportSettingsModel.setHead_frw_hs("rHeadChecked");
//        exportSettingsModel.setHead_fmw_hs("mHeadChecked");
        exportSettingsModel.setHead_fc("#000000");
        exportSettingsModel.setHead_fontsize(14);
        exportSettingsModel.setHead_fontface("楷体_GB2312");
        exportSettingsModel.setHead_fontblob("");
        exportSettingsModel.setHead_underline("");
        exportSettingsModel.setHead_delline("");
        exportSettingsModel.setHead_fontitalic("");
//        exportSettingsModel.setHead_fontblob("#fb[1]");
//        exportSettingsModel.setHead_underline("#fu[1]");
//        exportSettingsModel.setHead_delline("#fs[1]");
//        exportSettingsModel.setHead_fontitalic("#fi[1]");

        exportSettingsModel.setTail_left("");
        exportSettingsModel.setTail_center("");
        exportSettingsModel.setTail_right("");
        exportSettingsModel.setTail_flw_hs("");
        exportSettingsModel.setTail_frw_hs("");
        exportSettingsModel.setTail_fmw_hs("");
//        exportSettingsModel.setTail_flw_hs("lFootChecked");
//        exportSettingsModel.setTail_frw_hs("rFootChecked");
//        exportSettingsModel.setTail_fmw_hs("mFootChecked");
        exportSettingsModel.setTail_fc("#000000");
        exportSettingsModel.setTail_fontsize(14);
        exportSettingsModel.setTail_fontface("楷体_GB2312");
        exportSettingsModel.setTail_fontblob("");
        exportSettingsModel.setTail_underline("");
        exportSettingsModel.setTail_delline("");
        exportSettingsModel.setTail_fontitalic("");
//        exportSettingsModel.setTail_fontblob("#fb[1]");
//        exportSettingsModel.setTail_underline("#fu[1]");
//        exportSettingsModel.setTail_delline("#fs[1]");
//        exportSettingsModel.setTail_fontitalic("#fi[1]");

        exportSettingsModel.setText_fb("");
        exportSettingsModel.setText_fi("");
        exportSettingsModel.setText_fu("");
//        exportSettingsModel.setText_fb("#fb[1]");
//        exportSettingsModel.setText_fi("#fi[1]");
//        exportSettingsModel.setText_fu("#fu[1]");
        exportSettingsModel.setText_fc("#000000");
        exportSettingsModel.setText_fn("楷体_GB2312");
        exportSettingsModel.setText_fz(14);

        exportSettingsModel.setPhead_fb("#fb[1]");
        exportSettingsModel.setPhead_fi("");
        exportSettingsModel.setPhead_fu("");
//        exportSettingsModel.setPhead_fb("#fb[1]");
//        exportSettingsModel.setPhead_fi("#fi[1]");
//        exportSettingsModel.setPhead_fu("#fu[1]");
        exportSettingsModel.setPhead_fc("#000000");
        exportSettingsModel.setPhead_fn("楷体_GB2312");
        exportSettingsModel.setPhead_fz(14);

        return exportSettingsModel;
    }


    public ArrayList getExportDataFromSql(String strSql, HashMap columnMap){
        ArrayList dataList=new ArrayList();

        return dataList;

    }



}
