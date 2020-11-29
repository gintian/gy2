package com.hjsj.hrms.businessobject.train.point;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * Title:TrainPointBo
 * </p>
 * <p>
 * Description:培训积分业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-29
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class TrainPointBo {

    // 数据库连接
    private Connection conn;
    private UserView userView;
    
    public TrainPointBo(){
        
    }

    public TrainPointBo(Connection conn) {
        this.conn = conn;
    }
    
    public TrainPointBo(Connection conn,UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * 获得当前用户可用积分
     * 
     * @return
     */
    
    public int getCurUserUsablePoint()
    {
        return getUsablePoint(this.conn, this.userView.getDbname(), this.userView.getA0100());
    }
    
    /**
     * 获得人员可用积分
     * 
     * @return
     */
    public static int getUsablePoint(Connection cn, String nbase, String a0100){
        int point = 0;
        
        TrainCourseBo bo = new TrainCourseBo(cn);
        ContentDAO dao = new ContentDAO(cn);
        String point_set = bo.getTrparam("/param/point_set", "subset");
        String cur_point_field = bo.getTrparam("/param/point_set", "cur_point_field");
        
        //没有配置积分子集
        if (point_set == null || "".equalsIgnoreCase(point_set)) {
            return point;
        }
        
        //没有配置积分指标
        if (cur_point_field == null || "".equalsIgnoreCase(cur_point_field)) {
            return point;
        }
        
        String tab = nbase + point_set;
        
        //积分子集不存在或没有构库
        DbWizard dbWizard = new DbWizard(cn);
        if (!dbWizard.isExistTable(tab, false)){
            return point;
        }
        
        //积分指标不存在或没有构库
        if (!dbWizard.isExistField(tab, cur_point_field, false)){
            return point;
        }

        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("select ");
            sql.append(cur_point_field);
            sql.append(" from ");
            sql.append(tab);
            sql.append(" where a0100='"+a0100+"'");
            if (!"A01".equalsIgnoreCase(point_set))
            {
                sql.append(" AND I9999 = (SELECT MAX(I9999) AS MAXI9999 FROM ");
                sql.append(tab);
                sql.append(" where a0100='"+a0100+"')");
            }
            rs = dao.search(sql.toString());
            if(rs.next()) {
                point = rs.getInt(cur_point_field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }                
            }
        }
        return point;
    }
   
}
