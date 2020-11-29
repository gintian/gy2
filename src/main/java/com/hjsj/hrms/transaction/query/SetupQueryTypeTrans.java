package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SetupQueryTypeTrans</p>
 * <p>Description:设置查询类型交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 25, 2005:11:18:41 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SetupQueryTypeTrans extends IBusiness {

    /**
     * 
     */
    public SetupQueryTypeTrans() {
        super();
        // TODO Auto-generated constructor stub
    }
   

    /**
     * 查询分析用户或角色已有的高级权限
     * @param flag
     * @param a_id
     * @return
     */
    private ArrayList getSavedFactorList(String flag,String a_id)
    {
    	ArrayList list=new ArrayList();
    	ArrayList factorslist=null;
    	ArrayList factorlist=new ArrayList();
    	StringBuffer strsql=new StringBuffer();
    	String strcond=null;
        ContentDAO dao=new ContentDAO(this.getFrameconn());    	
    	strsql.append("select condpriv from t_sys_function_priv where id=? and status=?");
    	list.add(a_id);
    	list.add(flag);
    	try
    	{
    		cat.debug("sql_priv="+strsql.toString());
    		this.frowset=dao.search(strsql.toString(),list);
    		if(this.frowset.next())
    			strcond=this.frowset.getString("condpriv");

    		if(!(strcond==null|| "".equals(strcond)))
    		{
    			int idx=0;
    			idx=strcond.indexOf("|");
    			String sfactor=strcond.substring(0,idx);
    			String sexpr=strcond.substring(idx+1);
        		cat.debug("condpriv_factor="+sfactor);    	
        		cat.debug("condpriv_expr="+sexpr);           		
    			factorslist=new FactorList(sexpr,sfactor,userView.getUserName());
//    			for(int j=0;j<factorslist.size();j++)
//    			{
//    				Factor vo=(Factor)factorslist.get(j);
//    				cat.debug("factor_vo="+vo.toString());
//    			}
    			/**返回到前台页面*/
                this.getFormHM().put("factorlist",factorslist);
                this.getFormHM().put("expression",sexpr);      			
    			//factorlist.addAll(factorslist);
    		}
    	}
    	catch(SQLException sqle)
    	{
    		sqle.printStackTrace();
    	}
		return factorslist;    	
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        /**查询类型*/
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String setname="";
        if(hm!=null)
        {
            String query_type=(String)hm.get("a_query");
            String a_flag=(String)hm.get("a_flag");
            //String a_id=(String)hm.get("a_roleid");
            String a_id=(String)this.getFormHM().get("role_id");
            String type=(String)hm.get("a_inforkind");
            if(type==null|| "".equals(type))
            	type="1";
            if("1".equals(type))
            	setname="A01";
            if("2".equals(type))
            	setname="B01";   
            if("3".equals(type))
            	setname="K01";             
            /**高级授权再条件再现*/
            if("3".equals(query_type))
            {
            	ArrayList factorlist=new ArrayList();
            	factorlist=getSavedFactorList(a_flag,a_id);
            	if(factorlist!=null)
            	{
            		ArrayList list=new ArrayList();
        			Factor item=null;            		
            		for(int j=0;j<factorlist.size();j++)
            		{
            			item=(Factor)factorlist.get(j);
                        CommonData datavo=new CommonData(item.getFieldname(),item.getHz());
                        list.add(datavo);
                        cat.debug("data_vo="+datavo.toString());
            		}
            		this.getFormHM().put("fieldlist",list);
                	//this.getFormHM().put("factorlist",factorlist);            		
            	}
            }
            this.getFormHM().put("query_type",query_type);
            this.getFormHM().put("type",type);
            this.getFormHM().put("setname",setname);

        }
        this.getFormHM().put("succeedinfo","");
        Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
    }

}
