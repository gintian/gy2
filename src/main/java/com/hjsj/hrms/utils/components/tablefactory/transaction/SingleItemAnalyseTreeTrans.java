package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import org.mortbay.util.ajax.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author guodd
 * @Description:表格控件单指标分析功能 指标树 和 表格控件 代码型 过滤 生成树
 * @date 2015-5-7
 */
public class SingleItemAnalyseTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList list = null;
		
		try{

			String itemid = this.getFormHM().get("itemid").toString();
			String codesetid = (String)this.getFormHM().get("codesetid");
			String codesource=(String)this.getFormHM().get("codesource");
			
			if(AdminCode.getCodeItemList(codesetid).size()<1)
				return;			
			String codeid = this.getFormHM().get("node").toString(); //节点
			
			if(codesource.length()<0){//如果设置了代码 数据源类，走自定义类
				String classPath="com.hjsj.hrms.utils.components.codeselector.";
				CodeDataFactory codebean=(CodeDataFactory)Class.forName(classPath+codesource).newInstance();
				
				list=codebean.createCodeData(codesetid, codeid, userView);
				return ;
			}

			boolean autoCheck = Boolean.parseBoolean(this.getFormHM().get("autoCheck").toString());
			FieldItem fi = DataDictionary.getFieldItem(itemid);
			if(codesetid==null && fi!=null)
				codesetid = fi.getCodesetid();
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(codesetid.indexOf("UN")!=-1||codesetid.indexOf("UM")!=-1||codesetid.indexOf("@K")!=-1){ //机构 单位 部门
				list = searchOrgCodeData(codesetid,codeid,dao,autoCheck);
			}else{
				list = searchCodeData(codesetid,codeid,dao,autoCheck);
			}
			String str = JSON.toString(list);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().clear();
		this.getFormHM().put("children",list);
	}
	
	/**
	 * 查询机构树
	 * @param codesetid
	 * @param code
	 * @param dao
	 * @param autoCheck
	 * @return
	 */
	 private ArrayList searchOrgCodeData(String codesetid,String code,ContentDAO dao,boolean autoCheck){
		 ArrayList value = new ArrayList();
	    	try{

				/**
				 * ctrltype
				 * 过滤类型  如果codesetid 为机构（UN、UM、@K）
				 *         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
				 *         默认值为1
				 *  如果是普通代码类 
				 *         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
				 */
	    		String ctrltype=(String)this.getFormHM().get("ctrltype");
				ctrltype=ctrltype==null||ctrltype.length()<1?"1":ctrltype;
				String nmodule=(String)this.getFormHM().get("nmodule");//模块号 changxy
				nmodule=nmodule==null||nmodule.length()<1?"4":nmodule;//
	    		//String moduleId = this.getFormHM().get("analyseBusiId").toString();
	    		//moduleId=moduleId.length()<1?"4":moduleId;
	    		//从哪里进入的
	    		String fromFlag = (String)this.getFormHM().get("fromFlag");
	    		StringBuilder sql = new StringBuilder();
	    		String orgFilter = "";
	    		Date date = new Date();
	    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    		if("UN".equals(codesetid))
	    			orgFilter = " and codesetid<>'UM' and codesetid<>'@K' ";
	    		else if("UM".equals(codesetid))
	    			orgFilter = " and codesetid<>'@K' ";
	    		sql.append("select codeitemid,codeitemdesc,codesetid,(select count(1) from organization where parentid=Org.codeitemid and parentid<>codeitemid and ");
	    		sql.append(Sql_switcher.dateValue(format.format(date)));
	    		sql.append(" between start_date and end_date ");
	    		sql.append(orgFilter);
	    		sql.append(") child from organization Org where  ");
	    		
	    		sql.append(Sql_switcher.dateValue(format.format(date)));
	    		sql.append(" between start_date and end_date ");
	    		sql.append(orgFilter);
	    		if("root".equals(code)){
	    			
	    			String priv = null;//userView.getUnitIdByBusi(nmodule);
	    			if(!userView.isSuper_admin()){
		    			if("1".equals(ctrltype)){//管理范围  changxy 20160606
    						priv="XX"+userView.getManagePrivCodeValue();
    				   }else if("2".equals(ctrltype)){//操作单位
    					   	priv=userView.getUnit_id();
    					 }else if(nmodule.length()>0&&"3".equals(ctrltype)){
    						 priv=userView.getUnitIdByBusi(nmodule);
    					 }else {
    						 throw new Exception("获取代码参数出错：ctrytype 或  nmodule");
    					 }
	    				
		    			if("UN".equals(priv)){
		    				sql.append(" and parentid=codeitemid ");
		    			}else{
			    			sql.append(" and codeitemid in (");
			    			if(priv.indexOf("UN`")==-1){
			    				String[] privs = priv.split("`");
			    				for(int i=0;i<privs.length;i++){
			    					sql.append("'");
			    					sql.append(privs[i].substring(2));
			    					sql.append("',");
			    				}
			    			}
			    			sql.append(" '-1') ");
		    			}
		    		}else{
		    			sql.append(" and parentid=codeitemid ");
		    		}
	    		}else{
	    			sql.append(" and parentid='"+code+"' and parentid<>codeitemid ");
	    		}
	    		
	    		sql.append(" order by codeitemid");
	    		frowset = dao.search(sql.toString());
	    		while(frowset.next()){
	    			HashMap treeItem = new HashMap();
	    			treeItem.put("id", frowset.getString("codeitemid"));
	    			treeItem.put("text", frowset.getString("codeitemdesc"));
	    			if("UN".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/images/unit.gif");
	    			}else if("UM".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/images/dept.gif");
	    			}else if("@K".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/images/pos_l.gif");
	    			}
	    			if(frowset.getInt("child")==0)
	    				treeItem.put("leaf", Boolean.TRUE);
	    			
	    			if("root".equals(code) && autoCheck)
	    				treeItem.put("checked", Boolean.TRUE);
	    			else{
	    				treeItem.put("checked", Boolean.FALSE);
	    			}
	    			//从方案统计进入（PlanItemAnalyse.js>createCodeTree()方法 ）
	    			if("planAnalyse".equals(fromFlag) && !codesetid.equals(frowset.getString("codesetid")))
	    				treeItem.remove("checked");
	    			value.add(treeItem);
	    		}
	    	}catch(Exception e){
	    		
	    	}
	    	
	    	
	    	
	    	return value;
	    }
	 
	 /**
	  * 查询代码
	  * @param codesetid
	  * @param code
	  * @param dao
	  * @param autoCheck
	  * @return
	  */
	private ArrayList searchCodeData(String codesetid,String code,ContentDAO dao,boolean autoCheck){
    	ArrayList value = new ArrayList();
    	
    	try{
    		
    		String sql = "select codeitemid,codeitemdesc,(select count(1) from codeitem where codesetid=Code.codesetid and  parentid<>codeitemid and parentid=Code.codeitemid) child from codeitem Code where codesetid=? ";
    		value.add(codesetid);
    		if("root".equals(code))
    			sql+=" and codeitemid=parentid ";
    		else{
    			sql+=" and parentid=? and parentid<>codeitemid";
    			value.add(code);
    		}
    		sql+=" order by a0000,codeitemid ";
    		frowset = dao.search(sql, value);
    		value.clear();
    		while(frowset.next()){
    			HashMap treeItem = new HashMap();
    			treeItem.put("id", frowset.getString("codeitemid"));
    			treeItem.put("text", frowset.getString("codeitemdesc"));
    			
    			if(frowset.getInt("child")==0)
    				treeItem.put("leaf", Boolean.TRUE);
    			
    			if("root".equals(code) && autoCheck)
    				treeItem.put("checked", Boolean.TRUE);
    			else
    				treeItem.put("checked", Boolean.FALSE);
    			
    			value.add(treeItem);
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return value;	
    }
}
