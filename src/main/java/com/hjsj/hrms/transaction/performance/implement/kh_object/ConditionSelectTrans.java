package com.hjsj.hrms.transaction.performance.implement.kh_object;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 条件选择
 * JinChunhai
 */

public class ConditionSelectTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        //条件选择是否按部门匹配 
    	//条件选择这部分程序被考核实施和考核关系中都用到了 在考核实施中人员的条件选择可出现是否按部门匹配的选项 而考核关系中就不用这个匹配了
		String accordByDepartmentFlag = (String) hm.get("accordByDepartmentFlag");
		hm.remove("accordByDepartmentFlag");
		accordByDepartmentFlag=accordByDepartmentFlag==null?"0":accordByDepartmentFlag;
		this.getFormHM().put("accordByDepartmentFlag", accordByDepartmentFlag);	
		
		PerformanceImplementBo bo = new PerformanceImplementBo (this.getFrameconn(),this.getUserView());	
		ArrayList tablelist=bo.getTablelist();
		this.getFormHM().put("tablelist", tablelist);	
		
		String setname = (String)this.getFormHM().get("setname");
		ArrayList fieldlist=bo.getFieldlist(setname);
		this.getFormHM().put("leftlist", fieldlist);	
		
		//zgd 2015-1-13 条件选择类型 general=通用查询 start
		String selectType = (String) hm.get("selecttype");
		selectType = selectType==null?"":selectType;
		hm.remove("selecttype");
		this.getFormHM().put("selectType", selectType);
		//zgd 2015-1-13 条件选择类型 general=通用查询 end
		
		ArrayList dblist=getLoginBaseList();//条件选人人员库
		this.getFormHM().put("dblist", dblist);
		this.getFormHM().remove("dbpre");
		//this.getFormHM().put("rightlist", new ArrayList());
    }
    /**求得登录用户的应用库列表*/
    private ArrayList getLoginBaseList()throws GeneralException
    {
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String A01 = login_vo.getString("str_value");
        /**系统所有存在的数据库列表usr,oth,trs,ret*/
        StringBuffer strsql=new StringBuffer();
        strsql.append("select pre,dbname from dbname order by dbid");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList dblist=new ArrayList();
        try
        {
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                String dbpre=this.frowset.getString("pre");
                /**权限分析*/
	                if((A01.indexOf(dbpre)!=-1)&&(userView.isSuper_admin()||userView.hasTheDbName(dbpre)))
	                {
	                	CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
	                	dblist.add(vo);
	                }
            }
            /**认为是在职人员库*/
            if(dblist.size()==0)
            {
                CommonData vo=new CommonData("usr",ResourceFactory.getProperty("label.sys.userbase"));
                dblist.add(vo);                
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(sqle);                
        }
        return dblist;
    }
}
