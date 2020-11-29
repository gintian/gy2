package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:SearchLoginBaseTrans</p>
 * <p>Description:查询登录库列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:5:03:03 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchLoginBaseTrans extends IBusiness {

    /**
     * 
     */
    public SearchLoginBaseTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList dblist=new ArrayList();
        ArrayList selectedlist=new ArrayList();
        try
        {
    
            /**系统所有存在的数据库列表usr,oth,trs,ret*/
            StringBuffer strsql=new StringBuffer();
            /**
             * jinguiqun add 20140421 使应聘人才库中设置的人员库在认证应用库设置中不显示
             */
            RecordVo strvalue = ConstantParamter.getConstantVo("ZP_DBNAME");
            String str ="";
            EncryptLockClient lockclient=(EncryptLockClient)SystemConfig.getServletContext().getAttribute("lock");
            /*52533 没有购买招聘管理模块时不按招聘库设置过滤人员库 guodd 2019-09-02*/
            if(strvalue!=null && lockclient!=null && lockclient.isHaveBM(7)){
            	str = strvalue.getString("str_value");
            }
            
            /**oracle 数据库，当没有设置招聘人才库时，认证应用库页面中不显示任何人员库                  wangzhongjun 2015-05-09**/
            if (str == null || str.trim().length() <= 0) {
            	strsql.append("select pre,dbname from dbname order by dbid");
            } else {
            	strsql.append("select pre,dbname from dbname where pre <> '"+str+"' order by dbid");
            }
            
            ContentDAO dao=new ContentDAO(this.getFrameconn());
            
        
            /**登录参数表*/
            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String A01="";
            if(login_vo!=null) 
              A01 = login_vo.getString("str_value").toLowerCase();
            
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                String dbpre=this.frowset.getString("pre").toLowerCase();
                if(A01.indexOf(dbpre)!=-1)
                    selectedlist.add("1");
                else
                    selectedlist.add("0");                    
                CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
                dblist.add(vo);
            }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(sqle);                
        }
        finally
        {
            this.getFormHM().put("dblist",dblist);
            this.getFormHM().put("selectedlist",selectedlist);
        }
    }

}
