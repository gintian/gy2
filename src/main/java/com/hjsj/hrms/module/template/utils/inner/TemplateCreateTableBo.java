/**
 * 
 */
package com.hjsj.hrms.module.template.utils.inner;

import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;

/**
 * <p>TemplateBo</p>
 * <p>Description>: 代替templatetablebo中的流程相关的方法</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-11-20 上午10:37:39</p>
 * <p>@version: 7.0</p>
 */
public class TemplateCreateTableBo {
	private Connection conn;
	private UserView userView;	
    /**当前使用的模板号*/
    private int tabId;
    private TemplateParam paramBo = null;
    private ContentDAO dao; 
    private TemplateUtilBo utilBo= null;
    private DbWizard dbw=null;
    private boolean onlyComputeFieldVar=false;  //只计算模板指标中引入的临时变量 
   /**
	 * 初始化构造函数 传递TemplateParam类，不用新创建了
	 */
    public TemplateCreateTableBo (Connection conn,UserView userview,TemplateParam param){                       
    	this.paramBo = param;
    	this.tabId=param.getTabId();
    	init(conn,userview);
    }
    /**
	 * 初始化本来，创建一些公共类
	 */ 
    private void init(Connection conn,UserView userview){
    	this.conn = conn;
    	this.userView = userview;
    	dao = new ContentDAO(conn);                        
    	utilBo= new TemplateUtilBo(conn,this.userView);
    	dbw=new DbWizard(this.conn);
    }
    
  	
	
	
	public TemplateParam getParamBo() {
		return paramBo;
	}
	
	   public boolean isOnlyComputeFieldVar() {
	        return onlyComputeFieldVar;
	    }

	    public void setOnlyComputeFieldVar(boolean onlyComputeFieldVar) {
	        this.onlyComputeFieldVar = onlyComputeFieldVar;
	    }
	
}
