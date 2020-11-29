/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchTemplateTreeTrans</p>
 * <p>Description:查询业务模板树交易</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 26, 20061:08:52 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTemplateTreeTrans extends IBusiness {


	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		if(type==null|| "".equals(type))
			type="1";//1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整   22:资格评审   23:考勤业务办理
		
		HashMap hm=(HashMap) this.getFormHM().get("requestPamaHM");
		String history = (String)hm.get("history");
		/**是否定义了业务分类*/
        SubsysOperation subsysOperation=new SubsysOperation();
	    HashMap map = subsysOperation.getMap();
	    String bostype="false";//=true 按业务分类展示菜单树    =false 按模板展示菜单树
	    String extend_param="";
	    if(history!=null&& "2".equals(history)){
	    	extend_param="&history=2";
//	    	if(type.equals("8")){
//		    	bostype = "true";//保险变动仅走业务模版
//		    }
//		    else if(type.equals("21")){
//		    	bostype = "true";//劳动合同
//		    }
//		    else if(type.equals("12")){
//		    	bostype = "true";//出国管理
//		    }
	    	
	    }else{
	    if("1".equals(type))
	    	bostype=(String)map.get("37");//37是人事异动
	    else if("2".equals(type))
	    bostype=(String)map.get("34");//34是薪资管理	
	    else if("8".equals(type)){
	    	bostype=(String)map.get("39");//39是保险变动
	    	//bostype = "true";//仅走业务模版
	    }
	    else if("21".equals(type)){
	    	bostype=(String)map.get("38");//38是劳动合同
	    	bostype = "true";
	    }
	    else if("23".equals(type)) //考勤业务办理
	    {
	    	bostype = "true";
	    }
	    else if("22".equals(type)){
	    	bostype=(String)map.get("55");//资格评审（职称、任职资格等业务评审）
	    }	    
	    else if("12".equals(type)){
	    	bostype=(String)map.get("40");//40出国管理
	    	bostype = "true";
	    }
	    else if("10".equals(type))
	    	bostype=(String)map.get("56");//56组织机构
	    else if("11".equals(type))
	    	bostype=(String)map.get("57");//57岗位变动
	    else if("3".equals(type))
	    	bostype=(String)map.get("51");//警衔管理
	    else if("4".equals(type))
	    	bostype=(String)map.get("53");//法官等级
	    else if("5".equals(type))
	    	bostype=(String)map.get("54");//关衔管理
	    else if("6".equals(type))
	    	bostype=(String)map.get("52");//检察官管理
	    }
	    hm.remove("history");
	    
	    if(bostype==null|| "".equalsIgnoreCase(bostype))
	    	bostype="false";
	    this.getFormHM().put("bostype", bostype);
		String res_flag=(String)this.getFormHM().get("res_flag");
		if("true".equalsIgnoreCase(bostype))
		{
			String openseal="0";
			if("1".equals(type))
				openseal = "37";     //人事异动
			else if("2".equals(type))
				openseal = "34";     //薪资变动
			else if("8".equals(type))
				openseal = "39";      //保险变动
			else if("21".equals(type))//合同管理处理的业务模板，采用“人事异动”的模板相同的分类及授权
				openseal = "38";		
			else if("12".equals(type))//出国管理处理的业务模板，采用“人事异动”的模板相同的分类及授权
				openseal = "40";
			else if("22".equals(type))//资格评审，采用“人事异动”的模板相同的分类及授权
				openseal = "55";			
			else if("10".equals(type))
				openseal = "56";//组织机构
			else if("11".equals(type))
				openseal = "57";//岗位变动
			else if("3".equals(type))
				 openseal="51";//警衔管理
			else if("4".equals(type))
			    	openseal="53";//法官等级
			else if("5".equals(type))
			    	openseal="54";//关衔管理
			else if("6".equals(type))
			    	openseal="52";//检察官管理
			else if("23".equals(type))
			    	openseal="60";//考勤业务办理
			this.getFormHM().put("openseal", openseal);
		}else
		{
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("il_body");
			String rootdesc=ResourceFactory.getProperty("sys.res.rsbd");
			if("2".equals(type))
				rootdesc=ResourceFactory.getProperty("sys.res.gzbd");
			if("8".equals(type))
				rootdesc=ResourceFactory.getProperty("sys.res.ins_bd");
			if("10".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.zzjg");
				res_flag = ""+IResourceConstant.ORG_BD;
			}
			if("11".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.gangwei");
				res_flag = ""+IResourceConstant.POS_BD;
			}
//			if(type.equals("8")){
//				rootdesc="保险变动";
//				this.getFormHM().put("openseal", "39");
//			}
			if("21".equals(type)){
				rootdesc="合同办理";
				this.getFormHM().put("openseal", "38");
			}
			if("12".equals(type)){
				rootdesc="出国办理";
				this.getFormHM().put("openseal", "40");
			}
			  if("3".equals(type)){
				  rootdesc="警衔管理";
				this.getFormHM().put("openseal", "51");
				res_flag = ""+IResourceConstant.PSORGANS;
			  }
			     if("4".equals(type)){
			    	  rootdesc="法官等级";
						this.getFormHM().put("openseal", "53");
						res_flag = ""+IResourceConstant.PSORGANS_FG;
			     }
			     if("5".equals(type)){
			    	  rootdesc="关衔";
						this.getFormHM().put("openseal", "54");
						res_flag = ""+IResourceConstant.PSORGANS_GX;
			     }
			     if("6".equals(type)){
			    	  rootdesc="检察官";
						this.getFormHM().put("openseal", "52");
						res_flag = ""+IResourceConstant.PSORGANS_JCG;
			     }
			     
			if(history!=null&& "2".equals(history)&&("22".equals(type)|| "1".equals(type)))
					  rootdesc="业务模板";	     
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/template/search_template?module=-1&type="+type+"&res_flag="+res_flag+extend_param);
		    treeItem.setAction("javascript:void(0)");	   
		    try
		    {
		    	this.getFormHM().put("bs_tree",treeItem.toJS());  
		    }
		    catch(Exception ex)
		    {
		    	ex.printStackTrace();
		    	throw GeneralExceptionHandler.Handle(ex);
		    }
		}
		this.getFormHM().put("type",type);
		if("2".equals(type))
		{
			hm.remove("type");
		}
	    
	}

}
