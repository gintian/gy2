/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-13:14:36:56</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchStmpOptionsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        String port="25";
        if(stmp_vo==null)
        {
            this.getFormHM().put("port", port);
        	return ;
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        {
        	this.getFormHM().put("port", port);
        	return;
        }
        try
        {
	        Document doc = PubFunc.generateDom(param);// .build(in);
	        cat.debug("Function's Document successfully readed");
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");
	        this.getFormHM().put("stmp_addr",stmp.getAttributeValue("host"));
	        this.getFormHM().put("log_user",stmp.getAttributeValue("username"));
	        this.getFormHM().put("log_pwd",stmp.getAttributeValue("password"));//前台显示保存的密码  wangb 20170916 31639
	        this.getFormHM().put("log_repwd",stmp.getAttributeValue("password"));//前台显示保存的密码  wangb 20170916 31639
	        this.getFormHM().put("authy",stmp.getAttributeValue("authy"));
	        this.getFormHM().put("from_addr",stmp.getAttributeValue("from_addr"));
	        this.getFormHM().put("sendername",stmp.getAttributeValue("sendername"));

	        if(stmp.getAttributeValue("port")!=null&&!"".equals(stmp.getAttributeValue("port")))
	        	port=stmp.getAttributeValue("port");
	        this.getFormHM().put("port", port);
	        this.getFormHM().put("maxsend",stmp.getAttributeValue("max_send"));
	        
	        this.getFormHM().put("encryption",stmp.getAttributeValue("encryption"));
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
	}

}
