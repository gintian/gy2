/**
 * 
 */
package com.hjsj.hrms.service.core;

import com.hjsj.hrms.businessobject.sys.options.interfaces.SetInterfaces;
import com.hjsj.hrms.businessobject.sys.options.interfaces.SetOrgInterfaces;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cmq
 * Jul 1, 20093:56:09 PM
 */
public class HrService implements HrServiceIntf{
	
	private final static int ADD_FLAG = 1;// 新增
	
	private final static int UPDATE_FLAG = 2;//修改
	
	private final static int DELETE_FLAG = 3;//删除
	/**
	 * 创建组织机构
	 * @param org
	 * @return
	 */
	public String createOrganization(Organization org)
	{
//		System.out.println("---->"+org.getOrgId());
//		return "";
		Connection conn = null;		
		
		String flag="0";
		try{
			conn=AdminDb.getConnection();
			SetOrgInterfaces orgf = new SetOrgInterfaces(conn);
			String isCorrect= orgf.createOrganization(org);
			//Category.getInstance("com.hrms.frame.dao.ContentDAO").error("创建组织机构=="+isCorrect);
			if(!"false".equalsIgnoreCase(isCorrect))
			{
				flag=isCorrect;
			}
//			flag="1";
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
        }
		return flag;
	}
	/**
	 * 更新组织机构
	 * @param org
	 * @param outerOrgId
	 * @return
	 */
	public boolean updateOrganization(Organization org,String outerOrgId)
	{
//		return true;
		boolean isCorrect =true;
		Connection conn = null;			
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   SetOrgInterfaces orgf = new SetOrgInterfaces(conn);
   		   isCorrect=orgf.updateOrganization(org,outerOrgId);   		  
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   isCorrect=false;
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
        }		
		return isCorrect;
	}
	/**
	 * 删除机构
	 * @param outerOrgId
	 * @return
	 */
	public boolean removeOrganization(String outerOrgId)
	{
//		return true;
		boolean isCorrect =true;
		Connection conn = null;	
		
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   SetOrgInterfaces orgf = new SetOrgInterfaces(conn);
   		   isCorrect=orgf.removeOrganization(outerOrgId); 
   		   
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   isCorrect=false;
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
        }		
		return isCorrect;
	}
	/**
	 * 创建用户,新建用户最好区分人员类别
	 * @param user
	 * @return
	 */
	public String createUser(User user)
	{
		Connection conn = null;		
		String flag="0";
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   SetInterfaces setInterfaces=new SetInterfaces(conn);
   		   String isCorrect =setInterfaces.createUser(user);
   		   if(!"false".equalsIgnoreCase(isCorrect))
   		   {
   			flag=isCorrect;
   		   }
//   		   flag="1";
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
        }		
		return flag;
	}
	/**
	 * 更新用户信息,对子集记录，则更新当前记录(也即最后一条记录)
	 * @param user
	 * @param outerUserId
	 * @return
	 */
	public boolean updateUser(User user,String outerUserId)
	{
		boolean isCorrect =true;
		Connection conn = null;	
		
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   SetInterfaces setInterfaces=new SetInterfaces(conn);
   		   isCorrect=setInterfaces.updateUser(user,outerUserId);   		  
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   isCorrect=false;
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
        }		
		return isCorrect;
	}
	/**
	 * 删除用户或注销用户,eHR系统最好不能删除员工信息表
	 * @param outerUserId
	 * @return
	 */
	public boolean removeUser(String outerUserId)
	{
		boolean isCorrect =true;
		Connection conn = null;	
		
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   SetInterfaces setInterfaces=new SetInterfaces(conn);
   		   isCorrect=setInterfaces.removeUser(outerUserId);   		  
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   isCorrect=false;
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
        }
   	    return isCorrect;
	}
	
	/**
	 * 改变部门或部门调整
	 * @param newDeptId     新部门编码
	 * @param oldDeptId     老部门编码
	 * @param outerUserId   用户ID
	 * @return
	 */
	public boolean changeUserOrg(String newDeptId,String oldDeptId,String outerUserId)
	{
		 Connection conn = null;		
		 boolean isCorrect=false;
		 try
    	 {
    		conn=AdminDb.getConnection();
    		SetInterfaces setInterfaces=new SetInterfaces(conn);
    		isCorrect=setInterfaces.changeUserOrg(newDeptId, oldDeptId, outerUserId);
    	 }catch(Exception e)
     	 {
   		   e.printStackTrace();
    	 }finally
   	     {
   		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
         }
		 return isCorrect;
	}
	public boolean validateUserId(String UseCode)
	{
		 Connection conn = null;		
		 boolean isCorrect=false;
		 try
    	 {
    		conn=AdminDb.getConnection();
    		SetInterfaces setInterfaces=new SetInterfaces(conn);
    		isCorrect=setInterfaces.validateUserId("UsrA01",UseCode);
    	 }catch(Exception e)
     	 {
   		   e.printStackTrace();
    	 }finally
   	     {
   		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				//sql.printStackTrace();
			}
         }
		 return isCorrect;
	}
	/**
	 * 返回所有组织机构信息列表
	 * @return
	 */
	public Organization[] getAllOrganizations()
	{
//		return new Organization[20];
		Connection conn = null;
		Organization[] orgs=null;
		try
		{
			conn=AdminDb.getConnection();
			SetOrgInterfaces orgf = new SetOrgInterfaces(conn);
			orgs=orgf.getAllOrganizations();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
    	{
    		try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){				
			}
       }
		return orgs;
	}
	/**
	 * 返回指定组织节点下的机构列表
	 * @param outerDeptId
	 * @param withInherit     是否包含下级部门【包含单位和部门节点】
	 * @return
	 */
	public Organization[] getAllOrganizations(String outerDeptId,boolean withInherit)
	{
		
//		return new Organization[20];
		Connection conn = null;
		Organization[] orgs=null;
		try
		{
			conn=AdminDb.getConnection();
			SetOrgInterfaces orgf = new SetOrgInterfaces(conn);
			orgs=orgf.getAllOrganizations(outerDeptId,withInherit);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){				
			}
		}
		return orgs;
	}	
	/**
	 * 返回所有员工信息
	 * @return
	 */
	public User[] getAllUsers()
	{
		 Connection conn = null;
		 User[] users=null;
		 try
     	 {
			
     		conn=AdminDb.getConnection();
     		SetInterfaces setInterfaces=new SetInterfaces(conn);
     		users=setInterfaces.getAllUsers();      		
     	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){				
			}
       }
		return users;
	}
	
	/**eHR支撑平台-BEGIN*/
	/**
	 * 返回指定部门/科室的用户信息
	 * @param outerDeptId   
	 * @param withInherit   是否包含下级部门
	 * @return
	 */
	public User[] getUsersByDeptId(String outerDeptId,boolean withInherit)
	{
		Connection conn = null;
		User[] users=null;
		 try
    	 {
			
    		conn=AdminDb.getConnection();
    		SetInterfaces setInterfaces=new SetInterfaces(conn);
    		users=setInterfaces.getUsersById(outerDeptId,withInherit,false,true);
    	}catch(Exception e)
     	{
   	    	e.printStackTrace();
     	}finally
     	{
   		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){				
			}
      }
		return users;
	}
	/**
	 * 返回指定单位的用户信息
	 * @param outerOrgId
	 * @param withInherit  是否包含下级单位
	 * @return
	 */
	public User[] getUsersByOrgId(String outerOrgId,boolean withInherit)
	{
		
		Connection conn = null;
		User[] users=null;
		 try
    	 {
			
    		conn=AdminDb.getConnection();
    		SetInterfaces setInterfaces=new SetInterfaces(conn);
    		users=setInterfaces.getUsersById(outerOrgId,withInherit,true,false);
    	}catch(Exception e)
     	{
   	    	e.printStackTrace();
     	}finally
     	{
   		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){				
			}
      }
		return users;
	}
	
	/**
	 * 批量追加记录（主集或子集）
	 * @param xml
	 * @return
	 */
	public int batchAppend(String username ,String password,String xml)
	{
		return batchOp(username,password,xml,HrService.ADD_FLAG);
		//return -1;
	}
	/**
	 * 批量更新记录（主集或子集）
	 * @param xml
	 * @return
	 */
	public int batchUpdate(String username ,String password,String xml)
	{
		return batchOp(username,password,xml,HrService.UPDATE_FLAG);
		//return -1;
	}
	
	/**
	 * 批量删除记录（主集或子集）
	 * @param xml
	 * @return
	 */
	public int batchDelete(String username ,String password,String xml)
	{
		return batchOp(username,password,xml,HrService.DELETE_FLAG);
		//return -1;
	}
	
	private int batchOp(String username ,String password,String xml,int opFlag){
		Connection conn = null;
		try {
			Document document = DocumentHelper.parseText(xml);
			Element element = document.getRootElement();
			String inforType = element.attribute("infortype").getValue();// 'A|B|K' 人员|单位|职位
			String setid = element.attribute("setid").getValue();//表名
			String stafftype = element.attribute("stafftype").getValue();//人员库
			String keyfield = element.attribute("keyfield").getValue();//关键字
			String columns = element.attribute("columns_name").getValue();//字段名
			//XML的设置是否完整
			if(!valXmlSetInfo(inforType,setid,stafftype,keyfield,columns,opFlag)){
				return -1;
			}
			List list = document.selectNodes("dataset/rowset/*");
			//是否数据集
			if(list == null || list.size() == 0){
				return 0;
			}
			conn=AdminDb.getConnection();
    		
    		List columnList = null;
			switch (inforType.charAt(0)){
			case 'A':
				SetInterfaces setInterfaces=new SetInterfaces(conn);
				columnList = getFieldList(stafftype + setid,columns,conn);
				if(opFlag == HrService.ADD_FLAG){
					return setInterfaces.batchAppendToUser(username,setid,stafftype,keyfield,columnList,list);
				}else if(opFlag == HrService.UPDATE_FLAG){
					return setInterfaces.batchUpdateToUser(username,setid,stafftype,keyfield,columnList,list);
				}else if((opFlag == HrService.DELETE_FLAG)){
					return setInterfaces.batchDeleteToUser(username,setid,stafftype,keyfield,columnList,list);
				}
				break;
			case 'B':
			case 'K':
				SetOrgInterfaces setOrgInterfaces = new SetOrgInterfaces(conn);
				columnList = getFieldList(setid,columns,conn);
				if(opFlag == HrService.ADD_FLAG){
					return setOrgInterfaces.batchAppendToOrg(username,inforType,setid,keyfield,columnList,list);
				}else if(opFlag == HrService.UPDATE_FLAG){
					return setOrgInterfaces.batchUpdateToOrg(username,inforType,setid,keyfield,columnList,list);
				}else if((opFlag == HrService.DELETE_FLAG)){
					return setOrgInterfaces.batchDeleteToOrg(username,inforType,setid,keyfield,columnList,list);
				}
				break;
			default:
				return -1;
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	/**
	 * 检验XML的配置信息是否完整
	 * @param inforType 'A|B|K' 人员|单位|职位
	 * @param setid 表名
	 * @param stafftype 人员库
	 * @param keyfield 关键字
	 * @param columns 字段名称
	 * @param opFlag 操作标识 1 新增 2 修改 3 删除
	 * @return
	 */
	private boolean valXmlSetInfo(String inforType,String setid,String stafftype,String keyfield,String columns,int opFlag){
		// opFlag 1 新增 2 修改 3 删除
		if(inforType != null && inforType.length() > 0 && setid != null && setid.length() > 0 && columns != null && columns.length() > 0){
			if("A".equals(inforType)){
				if(stafftype == null || stafftype.length() < 1){
					return false;
				}
			}
			if(opFlag == HrService.ADD_FLAG){//新增
				return true;
			}else{//2 修改 3 删除
				if(keyfield != null && keyfield.length() > 0){
					return true;
				}
			}
		}
		return false;
	}
	
	private List getFieldList(String table,String columns,Connection conn){
		String column[] = columns.split(",");
		List columnList = new ArrayList();
		DbWizard dw=new DbWizard(conn);
		for(int i = 0; i < column.length; i++){
			String fieldName = column[i].trim().toLowerCase();
			if(!dw.isExistField(table, fieldName, false)){
				return null;
			}
			columnList.add(fieldName);
		}
		return columnList;
	}
}
