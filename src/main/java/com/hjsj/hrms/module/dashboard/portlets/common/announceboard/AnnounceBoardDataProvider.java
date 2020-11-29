package com.hjsj.hrms.module.dashboard.portlets.common.announceboard;

import com.hjsj.hrms.module.dashboard.portlets.buildin.ListWidgetBase;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告组件获取公告列表
 * @author ZhangHua
 * @date 16:51 2020/8/3
 */
public class AnnounceBoardDataProvider extends ListWidgetBase {

    @Override
    public List<RecordData> getListData() {

        String unitcode = this.getUnit(this.getFrameconn());
        String unitcodeWhere = this.getUnitWhere(unitcode,"");
//        String diff = "("+ Sql_switcher.diffDays(Sql_switcher.sqlNow(),"approvetime")+")";

        String nowDate = DateStyle.getSystemTime();
        if(nowDate!=null && nowDate.length()>0)
            nowDate=nowDate.substring(0,10);

        String a_tempstr = "("+Sql_switcher.diffDays(Sql_switcher.charToDate("'"+nowDate+"'"),"approvetime")+")<period";
        StringBuffer sql = new StringBuffer("select id,topic,viewcount,priority,");
        sql.append(Sql_switcher.dateToChar("approvetime","yyyy-MM-dd hh:mm:ss"));
        sql.append(" days ");
        sql.append(",noticeunit");
        sql.append(" from announce ");

//        if (annouceFlag == null)
//            annouceFlag = "";
//
//        if(annouceFlag.equals(""))
//        {
            sql.append(" where approve=1");
            sql.append(" and ");
            sql.append(a_tempstr);
//        }
//        else
//        {
//            sql.append(" where flag=" + annouceFlag);
//        }
        sql.append(" "+unitcodeWhere);
        sql.append(" order by priority,createtime desc");

        ArrayList<RecordData> result=new ArrayList<>();
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String url="./#/dashboard/portlets/common/announceboard/";
        try(RowSet rowSet=dao.search(sql.toString())){
            while(rowSet.next()){
                result.add(new RecordData(rowSet.getString("topic"),
                        rowSet.getString("days"), "",
                        url + PubFunc.encrypt(rowSet.getString("id"))));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    /**
     * 获取本单位及上级单位的sql语句条件
     * 或通知单位不为空
     * @param codeid
     * @return
     */
    public String getUnitWhere(String codeid,String flag){
        String strWhere = "";
        if(!("".equals(codeid)) && !(codeid == null)){//如果不是超级用户
            strWhere = "('";
            int n = codeid.length();
            for(int i=0;i<n;i++){
                strWhere +=codeid.substring(0,codeid.length()-i)+"','";
            }
            strWhere = strWhere.substring(0, strWhere.length()-2);
            strWhere = " and (unitcode in "+strWhere+") or unitcode is null or unitcode like '"+codeid+"%'";
            if("".equals(flag)){
                strWhere+=" or (noticeunit is not null";
                if(Sql_switcher.searchDbServer()==1){
                    strWhere+=" and "+Sql_switcher.sqlToChar("noticeunit")+"<>''";
                }
                strWhere+=")";
            }
            strWhere += ")";
        }
        return strWhere;
    }
    /**
     * 获取单位。
     * @return
     */
    public String getUnit(Connection conn){
        String unit = "";
        UserView userView =this.getUserView();
        if(!userView.isSuper_admin()){//如果不是超级用户
            int userType = userView.getStatus();//判断是业务用户还是自助用户。如果是4则是自助用户,0是业务用户。
            if(userType==4){//如果是自助用户
                unit = userView.getUserOrgId();//得到用户所在单位
            }else if(userType==0){//如果是业务用户，先看操作单位。如果没有，则看管理范围
                unit = getOperUnit(conn);
            }
        }
        return unit;
    }

    /*
     * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，则查出管理范围所在的单位。
     * **/
    public String getOperUnit(Connection conn)
    {
        String unit = "";
        UserView userView = this.getUserView();
        String operOrg = userView.getUnit_id();
        if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
        {
            String[] temp = operOrg.split("`");
            String unitordepart = temp[0];
            if ("UN".equalsIgnoreCase(unitordepart.substring(0, 2)))//如果是单位
                unit = unitordepart.substring(2);
            else//如果是部门
                unit = getUnit(unitordepart.substring(2),conn);
        }
        else if((!userView.isSuper_admin()) && ("".equalsIgnoreCase(operOrg))) // 如果不是超级用户，且没有操作单位
        {
            String codePrefix = userView.getManagePrivCode();
            String codeid = userView.getManagePrivCodeValue();
            if("UN".equalsIgnoreCase(codePrefix))//如果是单位
                unit = codeid;
            else//如果是部门
                unit = this.getUnit(codeid,conn);
        }
        return unit;
    }

    /**
     * 通过部门得到所属单位
     * */
    public String getUnit(String codeid,Connection conn){
        String unit = "";
        try{
            RowSet rs=null;
            String style = "";//返回UM或者UN
            StringBuffer sb = new StringBuffer();
            sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sb.toString());
            if(rs.next()){
                style = rs.getString("codesetid");
                unit = rs.getString("codeitemid");
            }
            if("UM".equalsIgnoreCase(style))
                getUnit(unit,conn);

            if(rs!=null)
                rs.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return unit;
    }
}
