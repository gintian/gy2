package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
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
 * <p>Title:SearchPerImplementTrans.java</p>
 * <p>Description:审批对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-11-11 10:52:59</p>
 * @author xieguiquan
 * @version 5.0
 */

public class SearchGenRelationTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            //
            String approvalRelation = (String) hm.get("approvalRelation");
            this.getFormHM().put("approvalRelation", approvalRelation);
            hm.remove("approvalRelation");
            hm.remove("operation");
            String returnflag = (String) hm.get("returnflag");
            this.getFormHM().put("returnflag", returnflag);
            //田野修改添加selectAllFlag=1,标记用于判断超级用户组是否查询所有的数据  2013-2-22
            String selectAllFlag = (String) hm.get("selectAllFlag");
            hm.remove("selectAllFlag");

            String code = (String) hm.get("code");
            hm.remove("code");
            String opt = (String) hm.get("operate");

            //hm.remove("opt");
            String a0101 = "";
            if (opt != null && opt.startsWith("init")) {
                this.getFormHM().put("a0101", "");
                hm.remove("operate");
                this.getFormHM().put("a_code", "");
            }
            String codeset = (String) hm.get("codeset");
            a0101 = (String) this.getFormHM().get("a0101");

            String dbpre = (String) this.getFormHM().get("dbpre");
            ArrayList dblist = getLoginBaseList();
            if (dbpre == null || "".equals(dbpre)) {
                dbpre = getFirstDbase(dblist);
            }
            String actor_type = (String) this.getFormHM().get("actor_type");//1:自助用户  4:业务用户
            if (code == null || code.length() == 0) {
                code = (String) this.getFormHM().get("a_code");
            } else {
                this.getFormHM().put("a_code", code);
            }
            if (codeset == null)
                codeset = "1";
            if (actor_type != null && "4".equals(actor_type)) {
                if (code == null || code.length() == 0)//只要选中左边的用户组，那么code就一定会有值的。郭峰
                {
                    code = this.userView.getGroupId();
                    selectAllFlag = "1";//如果没有选中具体的用户组，那么就显示全部
                    //code = "1";
                }
                //			  if(opt!=null&&opt.equals("init")){
                //				  code ="1";
                //				  hm.remove("operate");
                //			  }
            }
            this.getFormHM().put("dbpre", dbpre);
            this.getFormHM().put("dblist", dblist);
            this.getFormHM().put("codeset", codeset);
            this.getFormHM().put("code", code);
            this.getFormHM().put("operate", opt);

            //显示部门层数
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);

            String relation_id = (String) this.getFormHM().get("relationid");
            GenRelationBo bo = new GenRelationBo(this.frameconn, this.userView);
            //首先清空t_wf_mainbody表（这个表是审批关系表）的脏数据。 郭峰
            
            //haosl delete 清空审批关系的操做实在每次查询之前，高并发的时候很影响性能，所以将删除操作放到 信息同步操作中 (MessageSynTrans.java)
            //bo.deleteDataFromRelation(relation_id, dbpre, actor_type);
            //zxj 20141011 getGenObjectDataList2方法已合并
            ArrayList genObjects = bo.getGenObjectDataList2(code, a0101, dbpre, relation_id, codeset, actor_type, selectAllFlag);
            //		ArrayList objectTypes = bo.getObjTypes();
            //		ArrayList allObjectTypes = bo.getObjTypes2();		

            //		ArrayList objectTypeList = new ArrayList();
            //		CommonData temp = new CommonData("", "");
            //		try
            //		{
            //		    ContentDAO dao = new ContentDAO(this.frameconn);
            //		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=1 and status=1 order by seq");
            //		    RowSet rowSet = dao.search(sql.toString());
            //	
            //		    while (rowSet.next())
            //		    {
            //				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
            //				objectTypeList.add(temp);
            //		    }
            //		    
            //		    if(rowSet!=null)
            //		    	rowSet.close();
            //		    
            //		} catch (Exception e)
            //		{
            //		    e.printStackTrace();
            //		    throw GeneralExceptionHandler.Handle(e);
            //		}

            HashMap joinedObjs = bo.getJoinedObjs();
            this.getFormHM().put("joinedObjs", joinedObjs);

            this.getFormHM().put("genObjects", genObjects);
            //		this.getFormHM().put("objectTypes", objectTypes);	
            //		this.getFormHM().put("allObjectTypes", allObjectTypes);
            this.getFormHM().put("code", code);
            //this.getFormHM().put("objectTypeList", objectTypeList);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
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
    private String getFirstDbase(ArrayList dblist)
    {
    	CommonData vo=(CommonData)dblist.get(0);
    	return vo.getDataValue();
    }
}
