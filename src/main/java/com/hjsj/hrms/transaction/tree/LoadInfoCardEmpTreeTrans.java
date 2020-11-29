package com.hjsj.hrms.transaction.tree;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadInfoCardEmpTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		String action=(String)hm.get("action"); 
		String target=(String)hm.get("target");
		String treetype=(String)hm.get("treetype");//org,duty,employee,noum
		String privtype=(String)hm.get("viewPost");//权限类型，如果是kq就使用考勤权限	
		
		// 组织机构是否显示岗位  2011-07-14
		String viewPost = (String) hm.get("viewPost");
		
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		String kind="2";
		treeItem.setTarget(target);
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    String codeid=this.userView.getManagePrivCode();
		String codevalue=userView.getManagePrivCodeValue();
		if(privtype!=null&& "kq".equals(privtype))
	    {
	    	codeid=RegisterInitInfoData.getKqPrivCode(userView);
	    	codevalue=RegisterInitInfoData.getKqPrivCodeValue(userView);
	    }
		String a_code=codeid+codevalue;	
		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,"",this.getFrameconn());  
		String kq_type=kq_paramter.getKq_type();	
	    String url="/common/cardemp/loadtree?target="+target+"&action=" + action + "&flag=1&dbtype=1&kq_type="+kq_type;
	    if ("kq".equalsIgnoreCase(viewPost)) {//组织机构树是否显示
	    	url="/common/cardemp/loadtree?target="+target+"&action=" + action + "&viewPost="+viewPost+"&flag=1&dbtype=1&kq_type="+kq_type;
	    }
	    String urls = "";
		if(!("UN".equals(a_code)))
		{
			urls = "&params=codeitemid%3D'"+codevalue+"'&id="+codeid+codevalue;
		}
		else
		{
			urls = "&params=codeitemid%3Dparentid&id=UN";
		}
		//xiexd 2014.09.23加密
		url = url+"&encryptParam="+PubFunc.encrypt(urls);
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction(url);
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction(url);
		    else
			    treeItem.setLoadChieldAction(url);
			if("UN".equals(codeid))
		    	kind="2";
		    else if("UM".equals(codeid))
		    	kind="1";
		    else if("@K".equals(codeid))
		    	kind="0";
		}	    
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	        treeItem.setAction(action + "?b_search=link&encryptParam="+PubFunc.encrypt("isroot=1&a_code="+codeid+""+ codevalue));
	    else
	    	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());	
	    /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase="";
        if(dblist.size()>0){
        	userbase=dblist.get(0).toString();      
        }
        else
        	userbase="usr";
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
        cat.debug("-----userbase------>" + userbase);
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("setprv",getEditSetPriv(infoSetList,"A01"));
        //linbz--20160420--初始的时候给 sp_flag 赋值   all (全部)into_flag---iscommon
        this.getFormHM().put("sp_flag","all");
	    this.getFormHM().put("into_flag","all");
	    this.getFormHM().put("iscommon","all");
	}
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList,String setname)
	{
		String setpriv="1";
		boolean bflag=false;
		/**先根据子集分析*/
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				setpriv=String.valueOf(fieldset.getPriv_status());
				break;
			}
		}	
	  return setpriv;	
	}

}
