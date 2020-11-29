package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class UserPopedomAnalyse implements IResourceConstant {

	private Connection conn;	//数据库链接
	private EncryptLockClient lock;
	private String userFlag;	//用户标识(自助/业务)1,2
	private String dbPre;		//人员库前缀(自助用户)
	private String userName;	//用户名(业务用户)
	private UserView userView;  //用户对象
	

	
	private ArrayList setList = new ArrayList(); //授权的所有指标集  集合
	
	//指标集
	private ArrayList setReadList = new ArrayList(); //授权为读的指标集 集合
	private ArrayList setWriteList = new ArrayList();//授权为写的指标集 集合
	
	//指标项
	private ArrayList fieldReadList = new ArrayList(); //授权为读的指标 集合
	private ArrayList fieldWriteList = new ArrayList();//授权为写的指标 集合

	
	/**
	 * 用户权限分析
	 * @param conn	数据库链接
	 * @param userFlag 用户标识(自助/业务)1,2
	 * @param dbPre    人员库前缀
	 * @param userName 用户名
	 */
	public UserPopedomAnalyse(Connection conn,String userFlag, String dbPre, String userName){
		this.conn = conn;
		this.userFlag = userFlag;
		if("1".equals(userFlag)){
			this.dbPre = dbPre;
		}
		this.userName=userName;	
		this.userView =new UserView(this.userName,"",this.conn); /*this.createUserView()*/;
	}
	
	/**
	 * 创建用户对象
	 * @return
	 */
	public UserView createUserView(){
		UserView uv = null;
		String userName ="";
		String passWord ="";
		if("1".equals(this.userFlag)){//自助
		    RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	        if(login_vo==null){ //帐号登录
	        	String sql="select username , userpassword  from "+this.dbPre+"a01 where username='"+this.userName+"'";
	        	RowSet rs =null;
	        	try {
					ContentDAO dao = new ContentDAO(this.conn);
					rs = dao.search(sql);
					if(rs.next()){
						userName = rs.getString("username");
						passWord = rs.getString("userpassword");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
 				{
 					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
 				}
				
	        }else{
	        	String login_name = login_vo.getString("str_value");
	 	        int idx=login_name.indexOf(",");
	 	        
	 	        if(idx==-1){ //帐号登录
	 	        	String sql="select username , userpassword  from "+this.dbPre+"a01 where username='"+this.userName+"'";
	 	        	RowSet rs =null;
	 	        	try {
	 					ContentDAO dao = new ContentDAO(this.conn);
	 					rs = dao.search(sql);
	 					if(rs.next()){
	 						userName = rs.getString("username");
	 						passWord = rs.getString("userpassword");
	 					}
	 				} catch (SQLException e) {
	 					e.printStackTrace();
	 				}finally
	 				{
	 					if(rs!=null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
	 				}
	 				
	 	        }else{
	 	        	String usernameField=login_name.substring(0,idx);
	 		        String passwordField=login_name.substring(idx+1);
	 		        if("#".equals(usernameField)&& "#".equals(passwordField)){ //帐号登录
	 		        	String sql="select username , userpassword  from "+this.dbPre+"a01 where username='"+this.userName+"'";
	 		        	RowSet rs=null;
	 		        	try {
		 					ContentDAO dao = new ContentDAO(this.conn);
		 					rs = dao.search(sql);
		 					if(rs.next()){
		 						userName = rs.getString("username");
		 						passWord = rs.getString("userpassword");
		 					}
		 				} catch (SQLException e) {
		 					e.printStackTrace();
		 				}finally
		 				{
		 					if(rs!=null) {
                                try {
                                    rs.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
		 				}
	 		        }else{
	 		        	 String sql="select "+usernameField+" username , "+ passwordField+" userpassword  from "+this.dbPre+"a01 where a0100='"+this.userName+"'";
	 		        	 RowSet rs=null;
	 		        	 try {
	 	 					ContentDAO dao = new ContentDAO(this.conn);
	 	 					rs = dao.search(sql);
	 	 					if(rs.next()){
	 	 						userName = rs.getString("username");
	 	 						passWord = rs.getString("userpassword");
	 	 					}
	 	 				} catch (SQLException e) {
	 	 					e.printStackTrace();
	 	 				}finally
	 	 				{
	 	 					if(rs!=null) {
                                try {
                                    rs.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
	 	 				}
	 		        }
	 	        }
	        }
			
	        //System.out.println("username=" + userName + " passwrod=" + passWord);
	        
	        
		}else{//业务
			String sql = "select [password] from operuser  where username='"+this.userName+"'";
			RowSet rs =null;
			try {
				ContentDAO dao = new ContentDAO(this.conn);
				 rs = dao.search(sql);
				if(rs.next()){
					userName = this.userName;
					passWord = rs.getString("password");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		}
		
		if(userName == null || "".equals(userName)){
			return null;
		}
		if(passWord == null){
			passWord = "";
		}
		
		uv = new UserView(userName , passWord , conn);
		if(uv == null){
			//System.out.println("创建用户对象失败...............");
		}
		return uv;
	}


	/**
	 * @return
	 */
	public UserPopedom execute(){
		UserPopedom up = new UserPopedom();
		try {
			userView.canLogin(false);
		} catch (Exception e) {
			e.printStackTrace();
			up.setDisplayMessage("");		
			up.setOrgOrUserGroup(this.getValue(""));//单位或组信息
			up.setDept(this.getValue(""));//部门信息
			up.setJob(this.getValue(""));//职位信息
			up.setA0100(this.getValue(""));//用户信息	
			up.setDbPres(this.getValue("")); //人员库信息	
			up.setManagerSpace(this.getValue("")); //管理范围
			up.setFormula(this.getValue(""));//记录授权高级条件
			up.setPartymanager(this.getValue(""));
			up.setMenbermanager(this.getValue(""));
			up.setSetOrItemReadPriv(this.getValue("")); //读
			up.setSetOrItemWritePriv(this.getValue("")); //写			
			up.setSelfFunctionPriv(this.getValue("")); //自助功能权限
			up.setOperFunctionPriv(this.getValue("")); //业务功能权限			
			up.setCardResourcePriv(this.getValue(""));
			up.setReportResourcePriv(this.getValue(""));
			up.setLexprResourcePriv(this.getValue(""));
			up.setStaticsResourcePriv(this.getValue(""));
			up.setMusterResourcePriv(this.getValue(""));
			up.setHighMusterResourcePriv(this.getValue(""));
			up.setLawruleResourcePriv(this.getValue(""));
			up.setRsbdResourcePriv(this.getValue(""));
			up.setXzbdResourcePriv(this.getValue(""));//薪资类别
			up.setWjdcResourcePriv(this.getValue(""));//问卷调查
			up.setPxbResourcePriv(this.getValue(""));//培训班
			up.setGglResourcePriv(this.getValue(""));//公告栏
			up.setXzlbResourcePriv(this.getValue(""));//薪资类别
			up.setGzfxtResourcePriv(this.getValue(""));//工资分析图表
			up.setDaflResourcePriv(this.getValue(""));//档案分类
			up.setKqjResourcePriv(this.getValue(""));//考勤机
			up.setBxbdResourcePriv(this.getValue(""));//保险变动
			up.setOrgbdResourcePriv(this.getValue(""));
			up.setPosbdResourcePriv(this.getValue(""));
			up.setBxlbResourcePriv(this.getValue(""));//保险类别
			up.setWdflResourcePriv(this.getValue(""));//文档分类
			up.setZsflResourcePriv(this.getValue(""));//知识分类
			up.setKhzbResourcePriv(this.getValue(""));//考核指标
			up.setKhmbResourcePriv(this.getValue(""));//考核模板
			up.setJbbcResourcePriv(this.getValue(""));//基本班次
			up.setKqbzResourcePriv(this.getValue(""));//考勤班组
			return up;
		}
		
		//System.out.println( "描述信息开始" + System.currentTimeMillis());
		up.setDisplayMessage(this.getDisplayMessage());
		//System.out.println( "描述信息结束" + System.currentTimeMillis());
		ArrayList orglist=setParenOrglist(this.userView.getUserOrgId(),"UN");
		StringBuffer str=new StringBuffer();
		for(int i=0;i<orglist.size();i++)
		{
			String orgid=(String)orglist.get(i);
			str.append(this.hashMapToHtml(this.getRoleByUserProperty(orgid, 1),"1"));			
		}
		//up.setOrgOrUserGroup(this.getValue(this.hashMapToHtml(this.getRoleByUserProperty(this.userView.getUserOrgId(), 1),"1")));//单位或组信息
		up.setOrgOrUserGroup(this.getValue(str.toString()));
		str.setLength(0);		
		orglist=setParenOrglist(this.userView.getUserDeptId(),"UM");
		for(int i=0;i<orglist.size();i++)
		{
			String orgid=(String)orglist.get(i);
			//System.out.println(orgid);
			str.append(this.hashMapToHtml(this.getRoleByUserProperty(orgid, 1),"1"));			
		}
		//System.out.println();
		//up.
		up.setDept(this.getValue(str.toString()));//部门信息
		up.setJob(this.getValue(this.hashMapToHtml(this.getRoleByUserProperty(this.userView.getUserPosId(), 1),"1")));//职位信息
		HashMap usermap=this.getRoleByUserProperty(userView.getUserId(),0);//用户的角色
		String dbname = userView.getDbname();
		String a0100 = userView.getA0100();		
		up.setA0100(this.getValue(this.hashMapToHtml(this.getRoleByUserProperty(dbname+a0100, 2,usermap),"1")));//用户信息	

		up.setDbPres(this.getValue(this.getUserDbPre())); //人员库信息	
		
		up.setManagerSpace(this.getValue(this.getUserManagerSpace())); //管理范围
//		up.setFormula(this.getValue(this.getFormula()));//记录授权高级条件
		if(this.getFormula() == null || "".equalsIgnoreCase(this.getFormula()))//判断是否有高级条件 wangb 30107 20170728
        {
            up.setFormula(this.getValue(this.getFormula()));//记录授权高级条件
        } else {
            up.setFormula(this.getValue(this.getFormula().substring(0,this.getFormula().length()-1)));//高级条件  最后间隔符截掉页面不显示  wangb 30031 20170727
        }
		
		up.setPartymanager(this.getValue(this.getResourcePriv("29")));
		up.setMenbermanager(this.getValue(this.getResourcePriv("30")));
		
		//System.out.println( "读写权限开始" + System.currentTimeMillis());
		
		this.initSetOrFieldPriv(setList ,setReadList , setWriteList 
				,fieldReadList ,fieldWriteList);
		up.setSetOrItemReadPriv(this.getValue(this.getUserSetOrFieldPriv("1"))); //读
		up.setSetOrItemWritePriv(this.getValue(this.getUserSetOrFieldPriv("2"))); //写
		//System.out.println( "读写权限结束" + System.currentTimeMillis());
		
		//System.out.println( "功能权限开始" + System.currentTimeMillis());
		//up.setSelfFunctionPriv(this.getValue(this.formatFunctionPrivToHtml("1"))); //自助功能权限
		//up.setOperFunctionPriv(this.getValue(this.formatFunctionPrivToHtml("2"))); //业务功能权限
		up.setFunctionPriv(this.showFucntionPriv());
		//System.out.println( "功能权限结束" + System.currentTimeMillis());
		
		//System.out.println( "资源权限开始" + System.currentTimeMillis());
		up.setCardResourcePriv(this.getValue(this.getResourcePriv("0")));
		up.setReportResourcePriv(this.getValue(this.getResourcePriv("1")));
		up.setLexprResourcePriv(this.getValue(this.getResourcePriv("2")));
		up.setStaticsResourcePriv(this.getValue(this.getResourcePriv("3")));
		up.setMusterResourcePriv(this.getValue(this.getResourcePriv("4")));
		up.setHighMusterResourcePriv(this.getValue(this.getResourcePriv("5")));
		up.setLawruleResourcePriv(this.getValue(this.getResourcePriv("6")));
		up.setRsbdResourcePriv(this.getValue(this.getResourcePriv("7")));
		up.setXzbdResourcePriv(this.getResourcePriv("8"));//薪资类别
		up.setWjdcResourcePriv(this.getResourcePriv("9"));//问卷调查
		up.setPxbResourcePriv(this.getResourcePriv("10"));//培训班
		up.setGglResourcePriv(this.getResourcePriv("11"));//公告栏
		up.setXzlbResourcePriv(this.getResourcePriv("12"));//薪资类别
		up.setGzfxtResourcePriv(this.getResourcePriv("20"));//工资分析图表
		up.setDaflResourcePriv(this.getResourcePriv("14"));//档案分类
		up.setKqjResourcePriv(this.getResourcePriv("15"));//考勤机
		up.setBxbdResourcePriv(this.getResourcePriv("17"));//保险变动
		up.setOrgbdResourcePriv(this.getResourcePriv("31"));
		up.setPosbdResourcePriv(this.getResourcePriv("32"));
		up.setBxlbResourcePriv(this.getResourcePriv("18"));//保险类别
		up.setWdflResourcePriv(this.getResourcePriv("19"));//文档分类
		up.setZsflResourcePriv(this.getResourcePriv("21"));//知识分类
		up.setKhzbResourcePriv(this.getResourcePriv("23"));//考核指标
		up.setKhmbResourcePriv(this.getResourcePriv("22"));//考核模板
		up.setJbbcResourcePriv(this.getResourcePriv("24"));//基本班次
		up.setKqbzResourcePriv(this.getResourcePriv("26"));//考勤班组
		//System.out.println( "资源权限结束" + System.currentTimeMillis());
		
		return up;
		
	}

	public String getValue(String temp){
		if(temp == null || "".equals(temp.trim())){
			return "&nbsp;";
		}
		return temp;
	}

	
	/*******************************表格描述信息********************************/
	public String getDisplayMessage(){
		StringBuffer displayMessage = new StringBuffer();
		if("1".equals(userFlag)){ //自助用户
			
			String orgId = userView.getUserOrgId();//用户对应单位编码
			if(orgId == null || "".equals(orgId)){}else{
				String orgDesc = this.getUserOrgOrDeptOrJobDesc(orgId);
				displayMessage.append(orgDesc);
				displayMessage.append("&nbsp;&nbsp;");
			}
			
			String deptId = userView.getUserDeptId();//部门
			if(deptId == null || "".equals(deptId)){}else{
				String deptDesc = this.getUserOrgOrDeptOrJobDesc(deptId);
				displayMessage.append(deptDesc);
				displayMessage.append("&nbsp;&nbsp;");
			}
			
			String jobId = userView.getUserPosId();//职位
			if(jobId == null || "".equals(jobId)){}else{
				String jobDesc = this.getUserOrgOrDeptOrJobDesc(jobId);
				displayMessage.append(jobDesc);
				displayMessage.append("&nbsp;&nbsp;");
			}
			
			displayMessage.append(this.userName);
			
		}else{//业务用户
			String groupid = userView.getGroupId();//用户组ID(业务用户对应)
			if(groupid == null || "".equals(groupid)){}else{
				String groupDesc = this.getUserGroupDesc(groupid);
				displayMessage.append(groupDesc);
			}
			displayMessage.append("&nbsp;&nbsp;");
			displayMessage.append(userName);
		}
		
		return displayMessage.toString();
	}
	
	/**
	 * 
	 * @param m 角色列表
	 * @param flag =1 
	 * @return
	 */
	public String hashMapToHtml(HashMap m ,String flag){
		StringBuffer result = new StringBuffer();
		Iterator it = m.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			String id = (String)entry.getKey();
			String name = (String) entry.getValue();
			if("1".equals(flag)){//组织
				result.append("<a href=\"javaScript:orgPopedom('"+id+"','"+flag+"');\">");				
				
			}else{ //个人
					String dbpre = userView.getDbname();
					result.append("<a href=\"javaScript:userPopedom( '1' ,'"+dbpre+"','"+id+"');\">");
			}
			result.append(name);
			result.append("</a>");
			result.append("&nbsp;");
		}
		return result.toString();
	}	
	/**
	 * 求用户所在单位、部门以及职位、本人对应的角色
	 * @param flag =1 组织单元　=2人员
	 * @return
	 */
	public HashMap getRoleByUserProperty(String id,int flag)
	{
		HashMap map = new HashMap();

		if((id==null|| "".equalsIgnoreCase(id))) {
            return map;
        }
		String sql="";
		if(flag==1) {
            sql=" select role_id from t_sys_staff_in_role where staff_id ='"+id+"' and status=2";
        } else
		{
			if(this.userView.getStatus()==4) {
                sql=" select role_id from t_sys_staff_in_role where staff_id='"+id+"' and status=1";
            } else {
                sql=" select role_id from t_sys_staff_in_role where staff_id='"+id+"' and status=0";
            }
		}
		RowSet rs =null;		
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs= dao.search(sql);
			while(rs.next()){
				String role_id = rs.getString("role_id");
				String role_name = this.getUserRoleDesc(role_id);//?效率问题
				map.put(role_id,role_name);
			}			
		}
		catch(Exception ex)
		{
			;
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return map;
	}
	/**
	 * 求用户所在单位、部门以及职位、本人对应的角色
	 * @param flag =1 组织单元　=2人员
	 * @return
	 */
	public HashMap getRoleByUserProperty(String id,int flag,HashMap map)
	{
		if((id==null|| "".equalsIgnoreCase(id))) {
            return map;
        }
			
		String sql="";
		if(flag==1) {
            sql=" select role_id from t_sys_staff_in_role where staff_id ='"+id+"' and status=2";
        } else
		{
			if(this.userView.getStatus()==4) {
                sql=" select role_id from t_sys_staff_in_role where staff_id='"+id+"' and status=1";
            } else {
                sql=" select role_id from t_sys_staff_in_role where staff_id='"+id+"' and status=0";
            }
		}
		RowSet rs=null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next()){
				String role_id = rs.getString("role_id");
				String role_name = this.getUserRoleDesc(role_id);//?效率问题
				map.put(role_id,role_name);
			}			
		}
		catch(Exception ex)
		{
			;
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return map;
	}
	/**
	 * 求所在单位对应的角色
	 * @return
	 */
	public HashMap getOrgOrGroupMessage(){
		HashMap map = new HashMap();
		
		if("2".equals(this.userFlag)){//业务用户(显示 用户组 用户对应角色 )
			
			String groupid = userView.getGroupId();//用户组ID(业务用户对应)
			//System.out.println("groupid=" + groupid);
			if(groupid == null || "".equals(groupid)){}else{
				String groupDesc = this.getUserGroupDesc(groupid);
				map.put("G"+groupid,groupDesc);
			}
			
			//业务对应的角色信息
			String sql=" select role_id from t_sys_staff_in_role where staff_id='"+
					this.userName+"' and status='0'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			try {
				rs = dao.search(sql);
				while(rs.next()){
					String role_id = rs.getString("role_id");
					String role_name = this.getUserRoleDesc(role_id);//?效率问题
					map.put("R"+role_id,role_name);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
			
		}else{//自助用户(显示单位 员工对应角色)
			
			String orgId = userView.getUserOrgId();//用户对应单位编码
			//System.out.println("orgId=" + orgId);
			if(orgId == null || "".equals(orgId)){}else{
				String orgDesc = this.getUserOrgOrDeptOrJobDesc(orgId);
				map.put("O"+orgId,orgDesc);
			}
			
		
			
			String dbname = userView.getDbname();
			
			//自助用户用户(业务关联的业务用户)对应的角色信息
			String a0100 = userView.getA0100();
			
			//System.out.println("dbname=" + dbname + "  a0100=" + a0100);
			
			//员工对应的角色信息
			String sql=" select role_id from t_sys_staff_in_role where staff_id='"
				+dbname+a0100+"' and status='1'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;;
			try {
				rs = dao.search(sql);
				while(rs.next()){
					String role_id = rs.getString("role_id");
					String role_name = this.getUserRoleDesc(role_id);
					map.put("R"+role_id,role_name);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
			
		}
		return map;
	}
	
	/**
	 * 角色描述信息
	 * @param role_id
	 * @return
	 */
	public String getUserRoleDesc(String role_id){
		String role_desc = "";
		String sql="select role_name from t_sys_role where role_id="+role_id;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				role_desc = rs.getString("role_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return role_desc;
	}
	
	/**
	 * 
	 * @param groupid
	 * @return
	 */
	public String getUserGroupDesc(String groupid){
		String desc = "";
		String sql=" select groupname from usergroup where groupid="+groupid;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				desc = rs.getString("groupname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return desc;
	}
	
	/**************************用户部门信息**********************************/
	
	public HashMap getUserDeptMessage(){
		HashMap map = new HashMap();
		String deptId = userView.getUserDeptId();
		if(deptId == null || "".equals(deptId)){}else{
			String deptDesc = this.getUserOrgOrDeptOrJobDesc(deptId);
			map.put("O"+deptId , deptDesc);
		}
		return map;
	}
	
	/************************用户职位信息**********************************/
	
	public HashMap getUserJobMessage(){
		HashMap map = new HashMap();
		String jobId = userView.getUserPosId();
		if(jobId == null || "".equals(jobId)){}else{
			String jobDesc = this.getUserOrgOrDeptOrJobDesc(jobId);
			map.put("O"+jobId , jobDesc);
		}
		return map;
	}
	
	
	public String getUserOrgOrDeptOrJobDesc(String id){
		String desc = "";
		String sql="select codeitemdesc from organization where codeitemid='"+ id+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				desc = rs.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return desc;
	}
	
	
	/*********************用户信息*********************************/
	public HashMap getUserMessage(){
		HashMap map = new HashMap();
		String a0100 = userView.getA0100();
		String a0101="";
		if(a0100 == null || "".equals(a0100)){}else{
			String dbname = userView.getDbname();
	
			String sql="select a0101 from " + dbname +"A01 where a0100='"+ a0100+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					a0101 = rs.getString("a0101");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
	
		}
		
		if(a0100 == null || "".equals(a0100)){}else{
			String userName = "";
			 RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		        if(login_vo==null){ //帐号登录
		        	String sql="select username  from "+userView.getDbname()+"a01 where a0100='"+a0100+"'";
		        	RowSet rs=null;
		        	try {
						ContentDAO dao = new ContentDAO(this.conn);
						rs = dao.search(sql);
						if(rs.next()){
							userName = rs.getString("username");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}finally
					{
						if(rs!=null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
					}
					
		        }else{
		        	String login_name = login_vo.getString("str_value");
		 	        int idx=login_name.indexOf(",");
		 	        
		 	        if(idx==-1){ //帐号登录
		 	        	String sql="select username  from "+userView.getDbname()+"a01 where a0100='"+a0100+"'";
		 	        	RowSet rs=null;
		 	        	try {
		 					ContentDAO dao = new ContentDAO(this.conn);
		 					rs = dao.search(sql);
		 					if(rs.next()){
		 						userName = rs.getString("username");
		 				
		 					}
		 				} catch (SQLException e) {
		 					e.printStackTrace();
		 				}finally
						{
							if(rs!=null) {
                                try {
                                    rs.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
						}
		 				
		 	        }else{
		 	        	String usernameField=login_name.substring(0,idx);
		 		        String passwordField=login_name.substring(idx+1);
		 		        if("#".equals(usernameField)&& "#".equals(passwordField)){ //帐号登录
		 		        	String sql="select username   from "+userView.getDbname()+"a01 where a0100='"+a0100+"'";
		 		        	RowSet rs=null;
			 				try {
			 					ContentDAO dao = new ContentDAO(this.conn);
			 					rs = dao.search(sql);
			 					if(rs.next()){
			 						userName = rs.getString("username");
			 			
			 					}
			 				} catch (SQLException e) {
			 					e.printStackTrace();
			 				}finally
							{
								if(rs!=null) {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
							}
		 		        }else{
		 		        	 String sql="select "+usernameField+" username , "+ passwordField+" userpassword  from "+userView.getDbname()+"a01 where a0100='"+a0100+"'";
		 		        	 RowSet rs=null;
		 		        	 try {
		 	 					ContentDAO dao = new ContentDAO(this.conn);
		 	 					rs = dao.search(sql);
		 	 					if(rs.next()){
		 	 						userName = rs.getString("username");
		 	 					}
		 	 				} catch (SQLException e) {
		 	 					e.printStackTrace();
		 	 				}finally
							{
								if(rs!=null) {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
							}
		 		        }
		 	        }
		        }
		        if(userName == null){userName="";}
		        map.put(userName,a0101); 
		}
		
			
	        
		return map;
	}
	
	/*************************应用库*******************************/
	public String getUserDbPre(){
		StringBuffer desc = new StringBuffer();
		String dbpriv = userView.getDbpriv().toString();
		String pre[] = dbpriv.split(",");
		for(int i=0; i<pre.length;i++){
			String p = pre[i];
			if(p == null || "".equals(p)){}else{
				String name = this.getUserDbName(p);
				desc.append(name);
				desc.append(",");
			}
		}

		String result = desc.toString();
		if(result == null || "".equals(result.trim())){
		}else{
			result = result.substring(0,result.length()-1);
		}
		return result;
	}
	
	public String getUserDbName(String pre){
		String name = "";
		String sql="select dbname from dbname where pre='"+pre+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				name = rs.getString("dbname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return name;
	}
	
	
	/**********************管理范围**********************************/
	
	public String getUserManagerSpace(){
		String managerSpace = "";
		String managerPrivCode = userView.getManagePrivCode();
		String managerPrivCodeValue = userView.getManagePrivCodeValue();
		
		if((managerPrivCode== null || "".equals(managerPrivCode))&&(managerPrivCodeValue == null || "".equals(managerPrivCodeValue))){
			
		}else if((managerPrivCode!= null || "UN".equals(managerPrivCode))&&(managerPrivCodeValue == null || "".equals(managerPrivCodeValue)|| "`".equals(managerPrivCodeValue))){
			managerSpace=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}else{			
			String sql="select codeitemdesc from organization where codeitemid='"
				+ managerPrivCodeValue+"' and codesetid='"+managerPrivCode+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					managerSpace = rs.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		}
		return managerSpace;
	}
	
	/***************************记录授权高级条件*****************************/
	public String getFormula(){
		return userView.getHighPrivExpression();//getPrivExpression();
	}
	
	/***************************子集及指标的读写权限**************************/
	
	/**
	 * 
	 */
	public void initSetOrFieldPriv(ArrayList setList ,ArrayList setReadList ,ArrayList setWriteList 
				,ArrayList fieldReadList ,ArrayList fieldWriteList){
		
		String tablepriv = userView.getTablepriv().toString();//记录集权限
		String fieldPriv = userView.getFieldpriv().toString();//指标权限
		
		//System.out.println("tablepriv=" + tablepriv);
		//System.out.println("fieldpriv=" + fieldPriv);
		
		if(tablepriv == null){
			tablepriv="";
		}
		if(fieldPriv == null){
			fieldPriv="";
		}
		
		String [] tp = tablepriv.split(",");//授权指标集数组
		String [] fp = fieldPriv.split(",");//授权指标数组
		
		for(int i=0; i< tp.length; i++){
			String temp = tp[i];
			if(temp == null || "".equals(temp)){
				continue;
			}
			setList.add(temp.substring(0,temp.length()-1)); //所有指标集(无读写标识)
		}
		
		//子集区分读写好像无意义?
		for(int i=0; i< tp.length; i++){
			String temp = tp[i];
			if(temp == null || "".equals(temp)){
				continue;
			}
			if(temp.endsWith("1")){ //读权限
				setReadList.add(temp.substring(0,temp.length()-1)); //授权为读的指标集
			}else if(temp.endsWith("2")){ //写权限
				setWriteList.add(temp.substring(0,temp.length()-1));//授权为写的指标集
			}
		}
		
		for(int i=0; i< fp.length; i++){
			String temp = fp[i];
			if(temp == null || "".equals(temp)){
				continue;
			}
			/*【51064】 相同指标可能在指标权限字符串中出现多次（多角色时），此种方式判断不严谨 guodd 2019-07-30
			if(temp.endsWith("1")){ //读权限
				fieldReadList.add(temp.substring(0,temp.length()-1)); //授权为读的指标
			}else if(temp.endsWith("2")){ //写权限
				fieldWriteList.add(temp.substring(0,temp.length()-1));//授权为写的指标
			}
			*/
			/*userview中判断指标权限方法处理了重复的问题 guodd 2019-07-30*/
			String field = temp.substring(0,temp.length()-1);
			String priv = userView.analyseFieldPriv(field);
			if("1".equals(priv) && !fieldReadList.contains(field)){ //读权限
				fieldReadList.add(field); //授权为读的指标
			}else if("2".equals(priv) && !fieldWriteList.contains(field)){ //写权限
				fieldWriteList.add(field);//授权为写的指标
				/*bug:51372 当子集为读，指标设置为写的时候有可能 写权限显示不出来。此处做一下判断，如果有写权限指标，写权限子集列表不存在此子集，追加进去 guodd 2019-08-09*/
				FieldItem item = DataDictionary.getFieldItem(field); 
				//【54557】指标删除后这里获取的FieldItem为null，如果是null，跳过 guodd 2019-10-22
				if(item==null) {
                    continue;
                }
				String fieldsetid = item.getFieldsetid();
				if(!setWriteList.contains(fieldsetid)) {
                    setWriteList.add(fieldsetid);
                }
			}
		}
	}
	
	
	/**
	 * 
	 * @param flag	子集与指标权限分析(1 读 , 2 写 )
	 * @return
	 */
	public String getUserSetOrFieldPriv(String flag){
		
		StringBuffer userSetOrFieldPriv = new StringBuffer();
		/*
		System.out.println("*****************************");
		System.out.println(setList.size());
		System.out.println(setReadList.size());
		System.out.println(setWriteList.size());
		System.out.println(fieldReadList.size());
		System.out.println(fieldWriteList.size());
		System.out.println("*****************************");
		*/
		
		try
		{
			ArrayList tmplist=null;
			if("1".equals(flag)){//读
				tmplist=this.setReadList;
			}else if("2".equals(flag)){
				tmplist=this.setWriteList;
			}
			for(int i=0; i<tmplist.size(); i++){ //遍历授权所有指标集
			//for(int i=0; i<tmplist.size(); i++){	
				//String setid = (String)tmplist.get(i); //指标集
				String setid = (String)tmplist.get(i); //指标集
				if(setid == null){
					continue;
				}
				//String setDesc = this.getFieldSetDesc(setid);//指标集描述
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null) {
                    continue;
                }
				//String setDesc = fieldset.getFieldsetdesc();
				String setDesc = fieldset.getCustomdesc();
				
				StringBuffer items = new StringBuffer();
				
				if("1".equals(flag)){//读
					for(int j=0; j<fieldReadList.size(); j++){//遍历所有授权为读的指标
						String item = (String)fieldReadList.get(j);//指标
						if(item == null){
							continue;
						}
						if("A01".equalsIgnoreCase(setid)){
							if("B0110".equalsIgnoreCase(item)){
								items.append("单位编码");
								items.append("&nbsp;&nbsp;");
							}else if("E01A1".equalsIgnoreCase(item)){
								items.append("职位编码");
								items.append("&nbsp;&nbsp;");
							}else if("E0122".equalsIgnoreCase(item)){
								items.append("部门");
								items.append("&nbsp;&nbsp;");
							}else{
								if(this.checkItem(item,setid)){
									//String itemdesc = this.getFieldItemDesc(item);//指标描述
									String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
									items.append(itemdesc);
									items.append("&nbsp;&nbsp;");
								}
							}
						}else{
							//System.out.println("item=" + item + "setid=" + setid );
							if(this.checkItem(item,setid)){
								//String itemdesc = this.getFieldItemDesc(item);//指标描述
								String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
								items.append(itemdesc);
								items.append("&nbsp;&nbsp;");
							}
						}
	
						
						
					}
				}else if("2".equals(flag)){//写
					for(int j=0; j<fieldWriteList.size(); j++){
						String item = (String)fieldWriteList.get(j);
						if(item == null){
							continue;
						}
						
						if("A01".equalsIgnoreCase(setid)){
							if("B0110".equalsIgnoreCase(item)){
								items.append("单位编码");
								items.append("&nbsp;&nbsp;");
							}else if("E01A1".equalsIgnoreCase(item)){
								items.append("职位编码");
								items.append("&nbsp;&nbsp;");
							}else if("E0122".equalsIgnoreCase(item)){
								items.append("部门");
								items.append("&nbsp;&nbsp;");
							}else{
								if(this.checkItem(item,setid)){
									//String itemdesc = this.getFieldItemDesc(item);//指标描述
									String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
									items.append(itemdesc);
									items.append("&nbsp;&nbsp;");
								}
							}
						}else{
							//System.out.println("item=" + item + "setid=" + setid );
							if(this.checkItem(item,setid)){
								//String itemdesc = this.getFieldItemDesc(item);//指标描述
								String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
								items.append(itemdesc);
								items.append("&nbsp;&nbsp;");
							}
						}
					}
				}
				
				if(items==null||items.length()==0){}else{
					userSetOrFieldPriv.append(setDesc.trim());
					userSetOrFieldPriv.append("<br>");
					userSetOrFieldPriv.append("&nbsp;&nbsp;");
					userSetOrFieldPriv.append(items);
					userSetOrFieldPriv.append("<br>");
				}
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return userSetOrFieldPriv.toString();
	}
	
	public boolean checkItem(String itemid , String setid){
		boolean b = false;
		if(setid == null || "".equals(setid)){
			return b;
		}
		if(itemid == null || "".equals(itemid)){
			return b;
		}
		
		ArrayList list = DataDictionary.getFieldList(setid,Constant.ALL_FIELD_SET);
		if(list == null || list.size()==0){
		}else{
			ArrayList itemsList = new ArrayList();
			for(int i=0; i< list.size(); i++){
				FieldItem temp = (FieldItem) list.get(i);
				itemsList.add(temp.getItemid());
			}
			if(itemsList == null || itemsList.size() == 0){}else{
				if(itemsList.contains(itemid.toLowerCase())){
					b = true;
				}
			}
		}
		
		/*
		String sql="select itemid from fielditem  where itemid = '"+itemid+"' and fieldsetid ='"+setid+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		return b;
	}

		
			
	public String getFieldSetDesc(String setid){
		String setDesc = "";
		String sql="select fieldsetdesc from fieldset where fieldsetid='"+setid+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				setDesc = rs.getString("fieldsetdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return setDesc;
	}
	
	public String getFieldItemDesc(String itemid){
		String itemDesc="";
		String sql="select itemdesc from fielditem where itemid='"+itemid+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				itemDesc = rs.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		
		return itemDesc;
	}
	
	/*************************功能权限分析***********************************/
	/**
	 * 权限分析
	 * @param flag	自助平台/业务平台标识(1,2)
	 */
	public String formatFunctionPrivToHtml(String flag){
		String fp = userView.getFuncpriv().toString();
		fp = this.getFunctionPriv(fp,flag);
		FunctionWizard fw = new FunctionWizard();
		fw.setLock(lock);
		return fw.functionXmlToHtml(fp);
	}
	
	/**
	 * 用户功能权限分析
	 * @param functionPriv  权限id信息
	 * @param flag	自助平台/业务平台标识(1,2)
	 * @return
	 */
	public String getFunctionPriv( String functionPriv,String flag){
		if(functionPriv == null || "".equals(functionPriv)){
			return "";
		}
		String fpss = "";
		StringBuffer fps = new StringBuffer();		
		String [] fp = functionPriv.split(",");
		if("1".equals(flag)){//自助服务平台
			for(int i=0; i< fp.length; i++){
				String fun_id = fp[i];

				if(fun_id == null || "".equals(fun_id)|| "0".equals(fun_id)){
					continue;
				}
				/*if(fun_id.startsWith("2")){
					continue;
				}*/
				if(fun_id.startsWith("0")||fun_id.startsWith("11")){
					fps.append(fun_id);
					fps.append(",");
				}
			}
		}else{//业务平台
			for(int i=0; i< fp.length; i++){
				String fun_id = fp[i];
				if(fun_id == null || "".equals(fun_id)||
						fun_id.startsWith("0")|| "2".equals(fun_id)||fun_id.startsWith("11")){
					continue;
				}
				fps.append(fun_id);
				fps.append(",");
			}
		}
		
		if(fps == null || "".equals(fps)||fps.length() == 0){
			return "";
		}else{
			//System.out.println(fps.length());
			fpss = fps.substring(0,fps.length()-1);
		}
		return fpss;
	}
	
	/**************************资源权限分析**********************************/
	
	public String initResourceStr(){
		String warnpriv = "";
		if("2".equals(this.userFlag)){//(自助/业务)1,2
			String sql="select * from t_sys_function_priv where status ='0' and id='"+this.userName+"'";
			ContentDAO dao = new ContentDAO(this.conn);
		//	System.out.println(sql);
			RowSet rs=null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					
					warnpriv = rs.getString("warnpriv");
					if(warnpriv == null){
						warnpriv = "";
					}
				}else{
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		}else{//自助用户资源信息
			String sql="select * from t_sys_function_priv where status ='4' and id='"+userView.getDbname()+userView.getA0100()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			//System.out.println(sql);
			RowSet rs=null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					
					warnpriv = rs.getString("warnpriv");
					if(warnpriv == null){
						warnpriv = "";
					}
				}else{
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		}
		return warnpriv;
	
	}
	
	public String getResourcePriv(String res_flag){
		/*
		String rp = this.initResourceStr();
		
		if(rp == null || rp.equals("")){
			return "";
		}
		int n = Integer.parseInt(res_flag);
		ResourcePopedomParser rpp = new ResourcePopedomParser(rp,n);
		String str = rpp.getContent();
		*/
		//xuj update2013.4.28 获取当前用户连同角色的资源
		int n = Integer.parseInt(res_flag);
		String rp = this.userView.getResourceString(n);
		if(rp == null || "".equals(rp)){
			return "";
		}
		//【6891】系统管理-权限管理-账号分配-点击王光艳所对应的权限明细的时候后台报错。 jingq add 2015.01.30
		if(n==31||n==32){
			rp = rp.replaceAll("R", "");
		}
		StringBuffer resourcePriv = new StringBuffer();
		//String sql=this.getResourceSql(n,str);
		String sql=this.getResourceSql(n,rp);
		if(sql==null||sql.length()<=0) {
            return "";
        }
		ContentDAO dao = new ContentDAO(this.conn);
		//System.out.println(res_flag+"------"+sql);
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				String name = rs.getString("name");
				resourcePriv.append(name);
				if(n==29||n==30) {
                    resourcePriv.append(",&nbsp;");
                } else {
                    resourcePriv.append("<br>");
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return resourcePriv.toString();
	}
	
	/**
	 * 资源权限SQL语句
	 * @param res_type		资源类型
	 * @param str_content	资源类型授权情况
	 */
	private String getResourceSql(int res_type , String str_content)
	{
		StringBuffer strsql=new StringBuffer();
		if("".equals(str_content)) {
            str_content="-1";
        }
		//【8160】用户管理：选择业务用户a后点击“权限明细”，后台报错  jingq add 2015.03.23
		String sql = "";
		switch(res_type){
			case REPORT: //统计表1
				sql = splitSql("tabid",str_content);
				strsql.append("select name from tname where tabid in (");
				strsql.append(sql);
				strsql.append(") order by tsortid,tabid");
				break;
			case CARD: //登记表0
				sql = splitSql("tabid",str_content);
				strsql.append("select name from rname where tabid in (");
				strsql.append(sql);
				strsql.append(") order by tabid");
				break;
			case MUSTER:  //常用花名册4
				sql = splitSql("tabid",str_content);
				strsql.append("select hzname name from lname where tabid in (");
				strsql.append(sql);
				strsql.append(") order by flag, tabid");			
				break;
			case HIGHMUSTER: //高级花名册5
				sql = splitSql("tabid",str_content);
				strsql.append("select cname name from muster_name where tabid in (");
				strsql.append(sql);
				strsql.append(") order by nmodule, tabid");		
				break;
			case LEXPR: //常用查询2
				sql = splitSql("id",str_content);
				strsql.append("select id tabid,name  from lexpr where id in (");
				strsql.append(sql);
				strsql.append(") order by type, tabid");		
				break;
			case STATICS:  //常用统计3
				sql = splitSql("id",str_content);
				strsql.append("select id tabid,name  from sname where id in (");
				strsql.append(sql);
				strsql.append(") order by type, tabid");					
				break;
			case LAWRULE:  //规章制度6
				sql = splitSql("base_id",str_content);
				strsql.append("select name  from law_base_struct where base_id in (");
				strsql.append(sql);
				strsql.append(") order by displayorder");	
				break;	
			case RSBD:  //人事异动模版7
				sql = splitSql("tabid",str_content);
				strsql.append("select name from template_table where tabid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case GZBD://8工资变动
				sql = splitSql("tabid",str_content);
				strsql.append("select name from template_table where tabid in(");
				strsql.append(sql);
				strsql.append(")");	
			    break;	
			case INVEST://9;问卷调查表
				sql = splitSql("id",str_content);
				strsql.append("select content as name from investigate where  id in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case TRAINJOB://10;培训班
				sql = splitSql("R3101",str_content);
				strsql.append("select R3130 as name from R31 where R3101 in(");
				strsql.append(sql);
				strsql.append(")");	
				break;
			case ANNOUNCE://11;公告栏
				sql = splitSql("id",str_content);
				strsql.append("select topic as name from announce where id in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case GZ_SET://12;薪资类别
				sql = splitSql("salaryid",str_content);
				strsql.append("select cname as name from salarytemplate where salaryid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;				
			case ARCH_TYPE://14;档案分类	
				String archivetype=SystemConfig.getPropertyValue("archivetype");
				if(archivetype==null||archivetype.length()==0) {
                    archivetype="XB";
                }
				sql = splitSql("codeitemid",str_content);
				strsql.append("select codeitemid tabid,codeitemdesc name from codeitem where codesetid='"+archivetype);
				strsql.append("' and codeitemid in(");
				strsql.append(sql);
				strsql.append(")");
				
				break;	
			case KQ_MACH://15;考勤机
				sql = splitSql("type_id",str_content);
				strsql.append("select name from kq_machine_type where type_id in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case MEDIA_EMP://16;人员多媒体分类授权
				strsql.append("");
				break;	
			case INS_BD://17;保险福利变动
				sql = splitSql("tabid",str_content);
				strsql.append("select name from template_table where tabid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;
			case ORG_BD:
				sql = splitSql("tabid",str_content);
				strsql.append("select name from template_table where tabid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;
			case POS_BD:
				sql = splitSql("tabid",str_content);
				strsql.append("select name from template_table where tabid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;
			case INS_SET://18;保险福利类别
				sql = splitSql("salaryid",str_content);
				strsql.append("select cname as name from salarytemplate where salaryid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case DOCTYPE://19;文档分类
				sql = splitSql("base_id",str_content);
				strsql.append("select name from law_base_struct where base_id in(");
				strsql.append(sql);
				strsql.append(")");
				break;	
			case GZ_CHART://20;工资分析图表	
				sql = splitSql("tbid",str_content);
				strsql.append("select tablename as name from stattable where tbid in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case KNOWTYPE://21;知识分类
				sql = splitSql("base_id",str_content);
				strsql.append("select name from law_base_struct where base_id in(");
				strsql.append(sql);
				strsql.append(")");
				break;	
			case KH_MODULE://22;考核模板
				if(str_content!=null&& "-1".equals(str_content)) {
                    str_content="'-1'";
                } else
				{
					String strs[]=str_content.split(",");
					StringBuffer buf=new StringBuffer();
					for(int i=0;i<strs.length;i++)
					{
						buf.append("'"+strs[i]+"',");
					}
					buf.setLength(buf.length()-1);
					str_content=buf.toString();
				}
				sql = splitSql("template_id",str_content);
				strsql.append("select name from per_template where template_id in(");				
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case KH_FIELD://23考核指标
				if(str_content!=null&& "-1".equals(str_content)) {
                    str_content="'-1'";
                } else
				{
					String strs[]=str_content.split(",");
					StringBuffer buf=new StringBuffer();
					for(int i=0;i<strs.length;i++)
					{
						buf.append("'"+strs[i]+"',");
					}
					buf.setLength(buf.length()-1);
					str_content=buf.toString();
				}
				sql = splitSql("point_id",str_content);
				strsql.append("select pointname as name from per_point where  point_id in(");
				strsql.append(sql);
				strsql.append(")");	
				break;	
			case PARTY:
			case MEMBER:
					String strs[]=str_content.split(",");
					StringBuffer buf=new StringBuffer();
					String codesetid="";
					boolean isall = false;
					for(int i=0;i<strs.length;i++)
					{
						String tmp = strs[i];
						if(tmp.length()<2) {
                            continue;
                        }
						if(i==0) {
                            codesetid = tmp.substring(0,2);
                        }
						if(tmp.length()==2&&!"-1".equals(tmp)){
							isall=true;
							continue;
						}
						buf.append("'"+tmp.substring(2)+"',");
					}
					str_content=buf.toString();
				sql = splitSql("codeitemid",str_content);
				strsql.append("select codeitemdesc as name from codeitem where  codeitemid in(");
				strsql.append(sql);
				strsql.append("'') and codesetid='"+codesetid+"'");	
				if(isall){
					StringBuffer tmpsql  = new StringBuffer();
					tmpsql.append("select '");
					switch(res_type){
					case PARTY:
						tmpsql.append("党组织");
						break;
					case MEMBER:
						tmpsql.append("团组织");
						break;
					}
					tmpsql.append("' as name from codeitem");
					strsql.insert(0, com.hrms.hjsj.utils.Sql_switcher.sqlTop(tmpsql.toString(), 1)+" union all ");
				}
				break;	
			case KQ_BASE_CLASS:
				sql = splitSql("class_id",str_content);
				strsql.append("select name from kq_class where class_id in (");
				strsql.append(sql);
				strsql.append(")");
				break;
			case KQ_CLASS_GROUP:
				sql = splitSql("group_id",str_content);
				strsql.append("select name from kq_shift_group where group_id in (");
				strsql.append(sql);
				strsql.append(")");
				break;
		}
		return strsql.toString();
	}
	
	/**
	 * 由于子查询条件过多，需分割查询条件
	 * @Title: splitSql   
	 * @Description:    
	 * @param column 需要查询的列
	 * @param table 表名
	 * @param id 查询条件列
	 * @param content 查询条件
	 * @return jingq 2015.03.24
	 * @return String
	 */
	public String splitSql(String id,String content){
		StringBuffer buff = new StringBuffer();
		String[] str = content.split(",");
		if(str.length>=1000){
			int index = (int) Math.ceil((double)str.length/(double)1000);//向上取整，计算将content截成几段
			int num = (int) Math.ceil((double)str.length/(double)index);//每段几个数据
			int big = 0;//上一次截取到的位置
			for (int i = 1; i <= index; i++) {
				String s = "";
				if(i!=index) {
                    s = str[num*i-1];//当前应该截取的最后一个
                }
				int in = content.indexOf(s)+s.length();//截取的字符在content的位置
				if(i==index){
					buff.append(content.substring(big, content.length()));
				} else {
					buff.append(content.substring(big, in)+") or "+id+" in (");
					big = in+1;
				}
			}
		} else {
			buff.append(content);
		}
		return buff.toString();
	}
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}


	public void setLock(EncryptLockClient lock) {
        this.lock = lock;
    }

    public EncryptLockClient getLock() {
        return lock;
    }

    public String getUserFlag() {
		return userFlag;
	}


	public void setUserFlag(String userFlag) {
		this.userFlag = userFlag;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getDbPre() {
		return dbPre;
	}


	public void setDbPre(String dbPre) {
		this.dbPre = dbPre;
	}
	
	public ArrayList  setParenOrglist(String orgid,String codesetid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		boolean isCorrect=true;		
		do
		{
			list.add(orgid);
			orgid=getParenid(dao,orgid,codesetid);
			if(orgid==null||orgid.length()<=0) {
                isCorrect=false;
            }
		}while(isCorrect);
		return list;
	}
	public String getParenid(ContentDAO dao,String childid,String codesetid)
	{
		String sql="select parentid from organization where codeitemid='"+childid+"' and parentid<>codeitemid and codesetid='"+codesetid+"'";
		RowSet rs =null;
		String parenid="";
		try{
			
			rs = dao.search(sql);
			if(rs.next()){
				parenid = rs.getString("parentid");
				
			}			
		}
		catch(Exception ex)
		{
			;
		}finally
		{
			if(rs!= null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}	
		return parenid;
	}
	
	private TreeMap showFucntionPriv(){
		TreeMap funPrivMap = new TreeMap();
		String fp = userView.getFuncpriv().toString();
		if(!fp.startsWith(",")) {
            fp =","+fp;
        }
		if(!fp.endsWith(",")) {
            fp+=",";
        }
		FunctionWizard fw = new FunctionWizard();
		fw.setLock(lock);
		fw.getFunPrivHtml(funPrivMap, fp);
		return funPrivMap;
	}
}
