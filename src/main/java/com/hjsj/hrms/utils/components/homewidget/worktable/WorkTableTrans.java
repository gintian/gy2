package com.hjsj.hrms.utils.components.homewidget.worktable;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try{
			String type = (String)this.getFormHM().get("type");
			//角色id
			String objectid=(String)this.getFormHM().get("objectid");
			//角色标识
			String objecttype=(String)this.getFormHM().get("objecttype");
			
			String sql = "";
			String isHidden ="false";
			if(!PubFunc.isUseNewPrograme(this.userView))
				isHidden = "true";
			
			DbWizard db = new DbWizard(this.frameconn);
			if(!db.isExistTable("t_sys_quicklink", false)){
				Table table = new Table("t_sys_quicklink");
				Field f = new Field("username",DataType.STRING);
				f.setNullable(false);
				f.setKeyable(true);
				f.setLength(50);
				table.addField(f);
				f = new Field("menuinfo",DataType.CLOB);
				table.addField(f);
				//增加角色标识字段
				f = new Field("objecttype",DataType.STRING);
				f.setNullable(true);
				f.setLength(10);
				table.addField(f);
				db.createTable(table);
			}
			//表中没有角色字段  增加角色标识字段
			if(!db.isExistField("t_sys_quicklink", "objecttype",false)){
				Table table = new Table("t_sys_quicklink");
				Field f = new Field("objecttype",DataType.STRING);
				f.setNullable(true);
				f.setLength(10);
				table.addField(f);
				db.addColumns(table);
			}
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			String username=userView.getUserName();
			List roleList=new ArrayList();
			if("role".equals(objecttype))
				username=objectid;
			else
				roleList=getRoleList();
			
			if("save".equals(type)){
				ArrayList menus = (ArrayList)this.getFormHM().get("menus");
				String menu = JSONArray.fromObject(menus).toString();
				ArrayList values = new ArrayList();
				values.add(username);
				sql = "delete from t_sys_quicklink where username=?";
				dao.delete(sql, values);
				
				sql = "insert into t_sys_quicklink(username,menuinfo,objecttype) values(?,?,?)";
				values.add(menu);
				values.add(objecttype);
				dao.insert(sql, values);
				return;
			}
			
			String whereSql="";
			ArrayList list =  new ArrayList();
			if(!"role".equals(objecttype)){
				sql = "select username,menuinfo,objecttype from t_sys_quicklink where username in ";
				whereSql+="(";
				for(Object obj:roleList){
					String roleid=(String)obj;
					whereSql+="'"+roleid+"' ,";
				}
				whereSql+="'"+userView.getUserName()+"'";
				whereSql+=")";
				sql+=whereSql+" order by objecttype desc";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sql += " nulls last";
//				list.add(whereSql);
			}else{
				
				sql = "select username,menuinfo,objecttype from t_sys_quicklink where username=?";
				list.add(username);
			}
			
			List<String> RoleList=new ArrayList<String>();
			String links = "[]";
			try {
				this.frowset = dao.search(sql, list);
				list.clear();
				while(this.frowset.next()){
					links = Sql_switcher.readMemo(this.frowset, "menuinfo");//this.frowset.getString("menuinfo");
				
				JSONArray arr = JSONArray.fromObject(JSON.parse(links));
				MenuMainBo menubo = new MenuMainBo(this.frameconn);
				Document menudoc = menubo.getDocument();
				//String xpath = "//menu[@id=\"" + parentid + "\"]";
				XPath xpath;
				for(int i=0;i<arr.size();i++){
					JSONObject obj = (JSONObject)arr.get(i);
					if(obj.isNullObject())
						continue;
					
					String menuid = obj.get("menuid").toString();//xus 控制 相同的menuid 只允许显示一条
					if(RoleList.contains(menuid))
						continue;
					RoleList.add(menuid);
					
					xpath = XPath.newInstance("//menu[@id=\"" + menuid + "\"]");
					Element el = (Element) xpath.selectSingleNode(menudoc);
					if(el==null)
						continue;
					String bevalidate = el.getAttributeValue("validate");
					bevalidate = "true".equals(bevalidate)?"1":"0";
					String validatetype = SystemConfig.getPropertyValue("validateType");
					validatetype = validatetype==null || validatetype.length()<1?"1":validatetype;
					obj.put("bevalidate",bevalidate);
					obj.put("validatetype",validatetype);
					
					String[] fucnids = obj.get("funcid").toString().split(",");
					String objt=this.frowset.getObject("objecttype")==null?"":this.frowset.getString("objecttype");
					//xus 角色管理页面 不控制菜单权限 
					if("role".equals(objecttype)){
						obj.put("objecttype", objt);
						list.add(obj);
					}else{
						for(String fucnid : fucnids){//xiegh add  obj.get("funcid")可能是一个或多个funcid 不可以直接传入hasTheFunction()
							obj.put("objecttype", objt);
							if(this.userView.hasTheFunction(fucnid) || StringUtils.isEmpty(fucnid)){
								list.add(obj);
								break;
							}
							if(("0KR020302".equals(fucnid)||"0KR020502".equals(fucnid)) && userView.getA0100()!=null){
								
								WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.frameconn, userView);
					            ArrayList deptlist =workPlanUtil.getDeptList(userView.getDbname(),userView.getA0100());
					            if(deptlist==null || deptlist.size()<1)
					            		continue;
								list.add(obj);
								break;
							}
						}
					}
					String[] iconPaths = obj.getString("qicon").split("/");
					String qicon = iconPaths[iconPaths.length-1];
					qicon = "menuicon.png".equals(qicon)?"default.png":qicon;
					obj.put("qicon", qicon);
					
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.formHM.put("isHidden", isHidden);
			this.formHM.put("menus", list);
		}catch(Exception e){
			e.printStackTrace();
			this.formHM.put("menus", "[]");
		}
	}
	//获取角色list
	public List getRoleList() throws GeneralException{
//		List roleList=new ArrayList();
		//xus 18/8/28 获取角色权限
		List roleList=(List)userView.getRolelist().clone();
		StringBuffer strsql=new StringBuffer();
		String user_id=userView.getUserId();
		if(userView.isBThreeUser()){//有三员角色的用户
	    	/**只能查询到自己拥有的角色列表*/
	    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role");
	    	strsql.append(" where role_id in (");
	    	strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
	    	if(userView.getStatus()==4)
	    	{
	    		strsql.append(userView.getDbname());
	    	}
	        strsql.append(user_id);
	        strsql.append("' and status=");
            if(userView.getStatus()==0)
    	        strsql.append("0");            	
            else
  	            strsql.append("1");//4
	        strsql.append(") and role_property not in(0,15,16)");	
	        //strsql.append(" order by role_id");
	    }else{
	    	if(userView.isSuper_admin()){
	    		 if("su".equals(userView.getUserId()))
			    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role  ");
	    		 else
	    			 strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where role_property not in(0,15,16) ");
	    	 } else
			    {
			    	/**只能查询到自己拥有的角色列表*/
			    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role");
			    	strsql.append(" where role_id in (");
			    	strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
			    	if(userView.getStatus()==4)
			    	{
			    		strsql.append(userView.getDbname());
			    	}
			        strsql.append(user_id);
			        strsql.append("' and status=");
		            if(userView.getStatus()==0)
		    	        strsql.append("0");            	
		            else
		  	            strsql.append("1");//4
			        strsql.append(") and role_property not in(0,15,16)");	
			        //strsql.append(" order by role_id");
			    }
	    }
		strsql.append(" order by norder");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(strsql.toString());
		      while(this.frowset.next())
		      {
		    	  if(!roleList.contains(this.getFrowset().getString("role_id")))
		    		  roleList.add( this.getFrowset().getString("role_id"));	         
		      }
//		      this.formHM.put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return roleList;
	}
	
}
