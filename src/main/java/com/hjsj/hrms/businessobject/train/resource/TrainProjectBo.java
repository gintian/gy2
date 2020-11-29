package com.hjsj.hrms.businessobject.train.resource;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * Title:TrainProjectBo.java
 * </p>
 * <p>
 * Description:培训项目
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-31 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainProjectBo
{
    private Connection cn;

    public TrainProjectBo(Connection conn)
    {
	this.cn = conn;
    }

    public String getR1308(String r1301)
    {

	String r1308 = "";
	ContentDAO dao = new ContentDAO(this.cn);
	try
	{
	    RowSet rs = dao.search("select r1302 from r13 where r1301='" + r1301 + "'");
	    if (rs.next()) {
            r1308 = rs.getString("r1302");
        }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return r1308;
    }
}
