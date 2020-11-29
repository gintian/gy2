package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Title:ListEmpMusterTrans.java
 * Description:员工/单位/岗位名册
 * Company:hjsj
 * create time:2008-12-25 13:00:00
 * @author FanZhiGuo
 * @version 1.0
 */
public class ListEmpMusterTrans extends IBusiness
{
    public void execute() throws GeneralException{
    	HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
    	String returnvalue=(String)map.get("returnvalue");
    	returnvalue=returnvalue==null?"1":returnvalue;//=1没有返回按钮=2返回到5.0首页
	    /**常用花名册*/
		String temp = this.userView.getResourceString(4);
		StringBuffer strsql = new StringBuffer();
	
		if(temp==null|| "".equals(temp.trim()))
			temp="-1";
	
		//普通花名册
		String status = null;
		ArrayList<String> list = new ArrayList<String>();
		if (userView.isSuper_admin()||userView.hasTheFunction("26031")||userView.hasTheFunction("0309")) {
			status ="1";
		}
		if (userView.isSuper_admin()||userView.hasTheFunction("23031")) {
			list.add("2");
		}
        if (userView.isSuper_admin()||userView.hasTheFunction("25031")) {
        	list.add("3");
        	list.add("4");
		}
		strsql.append("SELECT  tabid,hzname name,'0' type,"+Sql_switcher.isnull("norder","99999")+" as norder, flag, flag as sortflag");
		strsql.append(" from lname ");
		if (status!=null) {
			strsql.append(" where flag = '1' ");
		}else {
			strsql.append(" where 1=1 ");
		}
		MusterManageServiceImpl musterManageServiceImpl = new MusterManageServiceImpl(this.frameconn, userView);
		String  priv = musterManageServiceImpl.getMusterPriv("0");
        strsql.append(getPrivSql(priv));
        if (list.size()>0) {
        	strsql.append(" union all ");
    		strsql.append("SELECT  tabid,hzname name,'0' type,"+Sql_switcher.isnull("norder","99999")+" as norder, flag, flag as sortflag");
    		strsql.append(" from lname ");
    		if (list.size()>0) {
    			strsql.append(" where flag in (");
    			for (int i = 0; i < list.size(); i++) {
    				if (i!=0) {
    					strsql.append(",");
    				}
    				strsql.append("'");
    				strsql.append(list.get(i));
    				strsql.append("'");
    			}
    			strsql.append(") ");
    		}else {
    			strsql.append(" where 1=2 ");
    		}
    	    priv = musterManageServiceImpl.getMusterPriv("1");
            strsql.append(getPrivSql(priv));
		}
		/*if (!(this.userView.isAdmin() && this.userView.getGroupId().equals("1"))){
			strsql.append(" and tabid in (");
			strsql.append(temp);
			strsql.append(")");
		}*/
		/**高级花名册*/
		temp = this.userView.getResourceString(5);
		if(temp==null|| "".equals(temp.trim()))
	    	temp="-1";
		strsql.append(" union all ");
		strsql.append("SELECT tabid,CName name,'1' type,"+Sql_switcher.isnull("norder","99999")+" as norder, ");
		strsql.append(" case nmodule when 3 then '3' when 21 then '21' when 41 then '41' end as flag,");
		strsql.append(" case nmodule when 3 then '1' when 21 then '2' when 41 then '3' end as sortflag");
		strsql.append(" FROM muster_name where nmodule in (3,21,41) ");
		/*if (!this.userView.isSuper_admin()){
			strsql.append(" and tabid in (");
			strsql.append(temp);
			strsql.append(") ");
		}*/
		strsql.append(" and tabid<>1000 and tabid<>1010 and tabid<>1020 order by sortflag, type desc,norder");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList tablist = new ArrayList();
		try{
		    this.frowset=dao.search(strsql.toString());
		    while(this.frowset.next()){
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("type", this.frowset.getString("type"));
		    	String tabid=this.frowset.getString("tabid");
		    	String hasQuery="0";
		    	//liuy 2015-11-16 13004：主页点击普通花名册后，点击返回，会返回到花名册列表页面，但是权限没有控制住，会叫权限范围外的花名册也都显示出来 begin
		    	if("0".equals(this.frowset.getString("type"))){
						bean.set("name", this.frowset.getString("name"));
			    		bean.set("flag", this.frowset.getString("flag"));
			    		bean.set("tabid", tabid);
			    		if ("1".equals(this.frowset.getString("flag"))) {
							bean.set("moduleID", "0");
						}else {
							bean.set("moduleID", "1");
						}
			    		bean.set("hasQuery", hasQuery);
			    		tablist.add(bean);
		    	}else{
		    		if(this.userView.isHaveResource(5, tabid)){
			    		/*HmusterXML hmxml = new HmusterXML(this.getFrameconn(), tabid);
			    		String factor = hmxml.getValue(HmusterXML.FACTOR);
			    		if(factor!=null&&factor.length()>0)*/
			    			hasQuery="1";
			    		bean.set("tabid",tabid);
			    		bean.set("name", this.frowset.getString("name"));
			    		bean.set("flag", this.frowset.getString("flag"));
			    		bean.set("hasQuery", hasQuery);
			    		tablist.add(bean);
			    	}
		    	}
		    	//liuy 2015-11-16 end
		    }
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("tablist", tablist);
		this.getFormHM().put("strsql",strsql.toString());
		this.getFormHM().put("columns","tabid,name,type");
		this.getFormHM().put("returnvalue", returnvalue);
    }
    private String getPrivSql(String priv) {
    	StringBuffer strsql = new StringBuffer();
        if (!"UN".equals(priv) && !"".equals(priv) && null != priv&&!"|".equals(priv)) {
            String[] array = priv.split("\\|");
            if (array.length > 1) {
            	strsql.append(" and (B0110 in (");
                //上级权限单位
                String[] unidArray = array[1].split(",");
                for (int j = 0; j < unidArray.length; j++) {
                    String unid = unidArray[j];
                    if (StringUtils.isNotBlank(unid)) {
                    	strsql.append("'");
                    	strsql.append(unid);
                    	strsql.append("',");
                    }
                }
                if (strsql.toString().endsWith(",")) {
                	strsql.deleteCharAt(strsql.length() - 1);
                }
                strsql.append(")");
                strsql.append(" or");
                //权限单位
                String[] unArray = array[0].split(",");
                for (int j = 0; j < unArray.length; j++) {
                    String unid = unArray[j];
                    if (StringUtils.isNotBlank(unid)) {
                    	strsql.append(" B0110 like");
                    	strsql.append("'");
                    	strsql.append(unid);
                    	strsql.append("%' or");
                    }
                }
                if (strsql.toString().endsWith("or")) {
                	strsql.deleteCharAt(strsql.lastIndexOf("or"));
                	strsql.deleteCharAt(strsql.lastIndexOf("r"));
                }
                strsql.append(")");
            }else if(array.length == 1){
            	strsql.append(" and (");
                String[] unidArray = array[0].split(",");
                for (int j = 0; j < unidArray.length; j++) {
                    String unid = unidArray[j];
                    if (StringUtils.isNotBlank(unid)) {
                    	strsql.append(" B0110 like");
                    	strsql.append("'");
                    	strsql.append(unid);
                    	strsql.append("%' or");
                    }
                }
                if (strsql.toString().endsWith("or")) {
                	strsql.deleteCharAt(strsql.lastIndexOf("or"));
                	strsql.deleteCharAt(strsql.lastIndexOf("r"));
                }
                strsql.append(")");
            }
        } else {
            if ("".equals(priv) || null == priv||"|".equals(priv)) {
            	strsql.append(" and 1=2 ");
            }
        }
		return strsql.toString();
	}
}
