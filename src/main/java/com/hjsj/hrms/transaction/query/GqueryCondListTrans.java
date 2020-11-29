package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:GqueryCondListTrans</p>
 * <p>Description:常用查询条件列表交易，主要用于查询常用条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 18, 2005:2:26:14 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class GqueryCondListTrans extends IBusiness {

    /**
     * 
     */
    public GqueryCondListTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        StringBuffer strsql=new StringBuffer();
        String type=(String)this.getFormHM().get("type");
       
        if(type==null|| "".equals(type))
        	type="1";
        strsql.append("select id,name,type,categories from lexpr where type='");//
        strsql.append(type);
        strsql.append("' order by norder");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list =new ArrayList();
        ArrayList catelist= new ArrayList();
        StringBuffer hidcategories = new StringBuffer();
        try
        {
            /**应用库过滤前缀符号*/
            ArrayList dblist=userView.getPrivDbList();
            StringBuffer cond=new StringBuffer();
            cond.append("select pre,dbname from dbname where pre in (");
            for(int i=0;i<dblist.size();i++)
            {
                if(i!=0)
                    cond.append(",");
                cond.append("'");
                cond.append((String)dblist.get(i));
                cond.append("'");
            }
            if(dblist.size()==0)
                cond.append("''");
            cond.append(")");
            cond.append(" order by dbid");
            /**应用库前缀过滤条件*/
            this.getFormHM().put("dbcond",cond.toString());

            /**常用查询条件列表*/
            this.frowset=dao.search(strsql.toString());
            int i=1;
            ArrayList tempcates = new ArrayList();
            while(this.frowset.next())
            {
                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
                if("1".equals(type)){
	                String categories = this.frowset.getString("categories");
	                categories = categories==null?"":categories;
	                if(categories.length()==0){
		                DynaBean vo=new LazyDynaBean();
		                vo.set("id",this.frowset.getString("id"));
		                vo.set("name",i+"."+this.getFrowset().getString("name"));
		                vo.set("type",this.frowset.getString("type"));
		                list.add(vo);
		                ++i;
	                }
	                if(categories.length()>0&&!tempcates.contains(categories)){
	                	CommonData cd = new CommonData(categories,categories);
	                	tempcates.add(categories);
	                	catelist.add(cd);
	                	hidcategories.append(","+categories);
	                }
                }else{
                	DynaBean vo=new LazyDynaBean();
	                vo.set("id",this.frowset.getString("id"));
	                vo.set("name",i+"."+this.getFrowset().getString("name"));
	                vo.set("type",this.frowset.getString("type"));
	                list.add(vo);
	                ++i;
                }
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("condlist",list);
            this.getFormHM().put("catelist", catelist);
            if(hidcategories.length()>1)
              this.getFormHM().put("hidcategories", hidcategories.substring(1));
           // this.getFormHM().put("categories", "");
        }
        Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
    }
   
 private void getCategories(){
	 
 }

}
