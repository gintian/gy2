package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;

import java.sql.Connection;

/**
 * <p>Title:HireOrderBo.java</p>
 * <p>Description:招聘订单</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-11 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class HireOrderBo
{
	
    Connection conn;

    public HireOrderBo(Connection conn)
    {
    	this.conn = conn;
    }
    public HireOrderBo()
    {
    }   

    /**
      * 从系统邮件服务器设置中得到发送邮件的地址
      * 
      * @return
     */
    public String getFromAddr() throws GeneralException
    {
		String str = "";
		RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		if (stmp_vo == null) {
            return "";
        }
		String param = stmp_vo.getString("str_value");
		if (param == null || "".equals(param)) {
            return "";
        }
		try
		{
		    Document doc = PubFunc.generateDom(param);
		    Element root = doc.getRootElement();
		    Element stmp = root.getChild("stmp");
		//  str = stmp.getAttributeValue("username"); JinChunhai 2012.10.23
		    str = stmp.getAttributeValue("from_addr");
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		return str;
    }        

}
