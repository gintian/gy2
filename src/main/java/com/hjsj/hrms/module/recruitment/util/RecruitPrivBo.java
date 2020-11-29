package com.hjsj.hrms.module.recruitment.util;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 新招聘权限控制类
 * <p>Title: RecruitPrivBo </p>
 * <p>Description: 新招聘权限控制类</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-10-29 下午06:27:01</p>
 * @author zhaoxj
 * @version 1.0
 */
public class RecruitPrivBo {
    /** 加载公共+上级+本级+下级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF_CHILD = 0;
    /** 加载公共+上级+本级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF = 1;
    /** 加载本级+下级 */
    public final static int LEVEL_SELF_CHILD = 2;
    /** 加载上级 */
    public final static int LEVEL_PARENT = 3;
    
    public RecruitPrivBo() {

    }
    
    public String getB0110(UserView userView) throws GeneralException {
        String b0110 = "";
        try {
            String codeid = "";
            if (userView.isSuper_admin() || "1".equals(userView.getGroupId()))
                return "HJSJ";
            
            codeid = userView.getUnitIdByBusi("7");
            if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)
                    || "UM`".equalsIgnoreCase(codeid) || "@K`".equalsIgnoreCase(codeid)) {
                throw new Exception("您没有招聘模块的管理范围权限！请联系管理员。");
            }

            if (codeid.trim().length() < 3)
                return "HJSJ";
            
            if (codeid.indexOf("`") == -1) {
                if (codeid.startsWith("UN") || codeid.startsWith("UM")) {
                    b0110 = codeid.substring(2);
                } else {
                    b0110 = codeid;
                }
                
                return b0110;
            } 
            
            String[] temps = codeid.split("`");
            codeid = "";
            for (int i = 0; i < temps.length; i++) {
                if (codeid.startsWith("UN") || codeid.startsWith("UM")) {
                    b0110 += temps[i].substring(2) + "`";
                } else {
                    b0110 += temps[i] + "`";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("您没有招聘模块的管理范围权限！请联系管理员。"));
        }
        return b0110;
    }

    /**
     * 获取所属单位sql条件
     * sql形如：(B0110='' or B0110 is null or B0110='UN`' or B0110='HJSJ' OR B0110 LIKE '0102%' OR B0110=LEFT('0102',LEN(B0110)))
     * @Title: getPrivB0110Whr   
     * @Description:    
     * @param userView 操作用户
     * @param b0110Fld 所属单位指标
     * @param levelFlag 加载层级标志  0：加载公共+上级+本级+下级   1：加载公共+上级+本级 ； 2：加载本级+下级；3:加载上级
     * @return 
     * @throws GeneralException
     */
    public String getPrivB0110Whr(UserView userView, String b0110Fld, Integer levelFlag) throws GeneralException{
        if (userView == null)
            return "1=2";
        
        if (userView.isSuper_admin())
            return "1=1";
        
        b0110Fld = (b0110Fld == null || "".equals(b0110Fld)) ? "b0110" : b0110Fld;
        levelFlag = (levelFlag == null) ? 0 : levelFlag;
        
        String privB0110Str = getB0110(userView);
        
        String[] privB0110s = privB0110Str.split("`");
        
        StringBuffer sqlWhr = new StringBuffer();
        sqlWhr.append("(");
        
        //UN`是全权
        if ("UN`".equalsIgnoreCase(privB0110Str))
            return "1=1";
        
        for (int i = 0; i < privB0110s.length; i++) {
            String privB0110 = privB0110s[i].trim();
            if ("".equals(privB0110))
                continue;
            
            if (privB0110.startsWith("UN") || privB0110.startsWith("UM") || privB0110.startsWith("@K"))
                privB0110 = privB0110.substring(2);
            
            if ("HJSJ".equals(privB0110))
                privB0110 = "";            
            
            if (sqlWhr.length() > 1)
                sqlWhr.append(" OR ");
            
            sqlWhr.append(" 1=2 ");
            //公共流程
            if (levelFlag != LEVEL_SELF_CHILD && levelFlag != LEVEL_PARENT ) {
                sqlWhr.append(" OR ");
                sqlWhr.append(b0110Fld).append("=''");
                sqlWhr.append(" or ").append(b0110Fld).append(" is null");
                sqlWhr.append(" or ").append(b0110Fld).append("='UN`'");
                sqlWhr.append(" or ").append(b0110Fld).append("='HJSJ'");
            }
            
            if (levelFlag != LEVEL_SELF_CHILD) {
                //上级
                sqlWhr.append(" OR ");
                sqlWhr.append("(");
                sqlWhr.append(b0110Fld).append("=").append(Sql_switcher.left("'" + privB0110 + "'", Sql_switcher.length(b0110Fld)));
                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
                sqlWhr.append(")");
            }
            
            //下级
            if (levelFlag != LEVEL_GLOBAL_PARENT_SELF && levelFlag != LEVEL_PARENT) {
                sqlWhr.append(" OR ");
                sqlWhr.append("(");
                sqlWhr.append(b0110Fld).append(" LIKE '").append(privB0110).append("%'");
                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
                sqlWhr.append(")");
            }
            
            //本级
            if (levelFlag != LEVEL_PARENT) {
                sqlWhr.append(" OR ");
                sqlWhr.append(b0110Fld).append("='").append(privB0110).append("'");
            }
            
        }
        sqlWhr.append(")");
        return sqlWhr.toString();
    }
    
    /**
     * 取得招聘职位权限sql条件【本级+下级+我的职位（创建人、负责人、招聘成员、部门负责人】
     * @Title: getPositionWhr   
     * @Description:    
     * @return
     * @throws GeneralException 
     */
    public String getPositionWhr(UserView userView) throws GeneralException {
        if (userView.isSuper_admin() || userView.isAdmin())
            return "1=1";
        
        StringBuffer whr = new StringBuffer();
        whr.append("((");
        //拼接需求单位权限
//部门为啥一定要为空？        whr.append("((z.z0325 is null or z.z0325='' )");
        whr.append("(");
        whr.append(" ("+this.getPrivB0110Whr(userView,"z.z0321", LEVEL_SELF_CHILD)+"))");
        
        //拼接需求部门权限
        whr.append(" or ((z.z0325 is not null ");
        if(Sql_switcher.searchDbServer()==Constant.MSSQL){
        	whr.append(" and z.z0325<>''");
        }
        whr.append(" ) ");
        whr.append(" and  ("+this.getPrivB0110Whr(userView,"z.z0325", LEVEL_SELF_CHILD)+"))");
        whr.append(") ");
        
        //拼接我的职位（创建人）
        whr.append(" or  z.z0309='"+userView.getUserName()+"' "); 
        //负责人、招聘成员、部门负责人
        if(userView.getA0100().length()>0)
        {
        	whr.append(" or z.z0301 in ( select z0301 from zp_members ");
        	whr.append(" where a0100 = '"+userView.getA0100()+"' and nbase='"+userView.getDbname()+"' ) ");
        }
        whr.append(" ) "); 

        return whr.toString();
    }
    
    /**
     * 取得简历权限sql条件
     * 简历中心：
     *    没有申请职位+招聘职位权限（本级+下级+我的职位（创建人、负责人、招聘成员、部门负责人）)
     * 人才库：
     *    我的人才库+招聘职位权限（本级+下级+我的职位（创建人、负责人、招聘成员、部门负责人）)
     * @Title: getResumeWhr   
     * @Description:    
     * @param flag 使用权限的功能点 "0": 简历中心 ; "1":人才库
     * @param talentsFlag 人才库标志    “2”：我的人才库  “1”：公共人才库
     * @return
     * @throws GeneralException 
     */
    public String getResumeWhr(String flag,UserView userView,String talentsFlag) throws GeneralException {
    	StringBuffer whr = new StringBuffer();
        if (userView.isSuper_admin() || userView.isAdmin())
        {        	
        	//我的人才库
    		if(talentsFlag!=null&&"2".equals(talentsFlag)&&"1".equals(flag))
    		{        		
    			whr.append("( zp_talents.create_user='"+userView.getUserName()+"' ) ");
    			return whr.toString();
    		}else{        		
        		return "1=1";
        	}
        }
        
    	whr.append("(");
        //简历中心权限
        if("0".equals(flag))
        {
        	whr.append("((");
        	//拼接需求单位权限
        	whr.append("((z0321 is not null");
        	if(Sql_switcher.searchDbServer()==Constant.MSSQL){        		
        		whr.append(" and z0321<>''");
        	}
        	whr.append(" )");
        	whr.append(" and ("+this.getPrivB0110Whr(userView,"z0321", LEVEL_SELF_CHILD)+"))");
        	whr.append(" or ");
        	
        	//拼接需求部门权限
        	whr.append("((z0325 is not null and z0321<>'' " );
        	if(Sql_switcher.searchDbServer()==Constant.MSSQL){        		
        		whr.append("and z0325<>''");
        	}
        	whr.append(" )");
        	whr.append(" and ("+this.getPrivB0110Whr(userView,"z0325", LEVEL_SELF_CHILD)+"))");
        	whr.append(")");
        	//当未申请职位时
        	whr.append(" or (z0301 is null or z0301='')) ");
        }else if("1".equals(flag)){
        	//人才库权限
        	whr.append("(");
        	//公共人才库
        	if(talentsFlag!=null&&("1".equals(talentsFlag)||"0".equals(talentsFlag)))
        	{        		
        		whr.append("("+this.getPrivB0110Whr(userView,"zp_talents.b0110", LEVEL_SELF_CHILD)+")");
        		whr.append(" or zp_talents.b0110 is null or ");
        	}
        	whr.append(" zp_talents.create_user='"+userView.getUserName()+"' ) ");
        }
        if(!"2".equals(talentsFlag))
        {        	
        	//拼接我的职位（创建人）
        	whr.append(" or z0309='"+userView.getUserName()+"' "); 
        }
        //负责人、招聘成员、部门负责人
        if(!"2".equals(talentsFlag) && userView.getA0100().length()>0)
        {
            whr.append(" or z0301 in ( select z0301 from zp_members ");
            whr.append(" where a0100 = '"+userView.getA0100()+"' and nbase='"+userView.getDbname()+"' )");
        }
        whr.append(")");
        return whr.toString();
    }
    /**
     * 添加检查当前用户是否有某环节的权限方法
     * @param userView
     * @param posId 职位Id
     * @param flowId
     * @param linkId 环节id
     * @return 
     */
    public boolean hasFlowLinkPriv(Connection conn, UserView userView, String posId,String flowId, String linkId){
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	boolean flag = true;
    	try {
	    	if(userView.isSuper_admin()){
	    		return true;
	    	}
	    	String a0100 = userView.getA0100();
	    	String dbname = userView.getDbname();
	    	
	    	//获取当前操作人员在招聘职位中的角色
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("select member_type,create_user from zp_members");
	    	sql.append(" where z0301=?");
	    	sql.append(" and a0100=?");
	    	sql.append(" and nbase=?");
	    	//发布人不参与权限控制
	    	sql.append(" and member_type<>4");
	    	
	    	ArrayList sqlParams = new ArrayList();
	    	sqlParams.add(posId);
	    	sqlParams.add(a0100);
	    	sqlParams.add(dbname);	    	
	    	
	    	
	    	String memberType = "";		//招聘成员
	    	String create_user = "";	//创建人
			rs = dao.search(sql.toString(), sqlParams);
			if(rs.next()) {
			    memberType = rs.getString("member_type");
			    create_user = rs.getString("create_user");
			}
			PubFunc.closeResource(rs);
			//不是该职位的招聘成员，无环节权限
			if((StringUtils.isEmpty(memberType) || !",1,2,3,".contains("," + memberType + ","))&&!create_user.equalsIgnoreCase(userView.getUserName())){
				sql.setLength(0);
				String positionWhr = getPositionWhr(userView);
				sql.append("select 1 from z03 z ");
				sql.append(" where z0301='"+posId+"' and ");
				sql.append(positionWhr);
				rs = dao.search(sql.toString());
				//取得招聘职位权限
				if(!rs.next())
					return false;
			}
			
			//获取环节是否为用人单位环节
			StringBuffer orgLinkSQL = new StringBuffer();
			ArrayList orgLinkSQLParams = new ArrayList();
			boolean org = false;
			orgLinkSQL.append("select org_flag");
			orgLinkSQL.append(" from zp_flow_links");
			orgLinkSQL.append(" where flow_id=?");
			orgLinkSQLParams.add(flowId);
			rs = dao.search(orgLinkSQL.toString(),orgLinkSQLParams);
			while(rs.next()){
				if("1".equals(rs.getString("org_flag"))){
					org = true;
					break;
				}
			}
			orgLinkSQL.setLength(0);
			orgLinkSQLParams.clear();
			orgLinkSQL.append("select org_flag");
			orgLinkSQL.append(" from zp_flow_links");
			orgLinkSQL.append(" where id=?");	    	
			orgLinkSQLParams.add(linkId);
			if(StringUtils.isNotEmpty(flowId)){
				orgLinkSQL.append(" and flow_id=?");
				orgLinkSQLParams.add(flowId);
			}
			rs = null;
			String orgFlag = "";
			rs = dao.search(orgLinkSQL.toString(), orgLinkSQLParams);
			if(rs.next()) {
				orgFlag = rs.getString("org_flag");
			}

			//创建人所有环节都可以操作(外文局实际情况有冲突，一般创建人就是招聘负责人因此去掉创建人的特殊权限)
			/*flag = create_user.equalsIgnoreCase(userView.getUserName())
					//用人单位环节，只有部门负责人可以操作
					|| */
			flag = 	("1".equals(orgFlag) && "3".equals(memberType))
					//非用人环节，需要检查环节设置的操作人条件
					|| this.checkPriv(dao, linkId, memberType, userView,org,orgFlag);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return flag;
    }
    /**
     * 添加检查当前用户是否有某职位的权限方法
     * @param userView
     * @param posId 职位Id
     * @return 
     */
    public boolean hasPosPriv(Connection conn, UserView userView, String posId){
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	try {
    		if(userView.isSuper_admin()){
    			return true;
    		}
    		
    		String a0100 = userView.getA0100();
    		String dbname = userView.getDbname();
    		
    		//获取当前操作人员在招聘职位中的角色
    		StringBuffer sql = new StringBuffer();
    		sql.append("select member_type,create_user from zp_members");
    		sql.append(" where z0301=?");
    		sql.append(" and a0100=?");
    		sql.append(" and nbase=?");
    		
    		ArrayList sqlParams = new ArrayList();
    		sqlParams.add(posId);
    		sqlParams.add(a0100);
    		sqlParams.add(dbname);	    	
    		
    		
    		String memberType = "";		//招聘成员
    		String create_user = "";	//创建人
    		rs = dao.search(sql.toString(), sqlParams);
    		if(rs.next()) {
    			memberType = rs.getString("member_type");
    			create_user = rs.getString("create_user");
    		}
    		rs.close();
    		//不是该职位的招聘成员，无环节权限
			if((StringUtils.isEmpty(memberType) || !",1,2,3,".contains("," + memberType + ","))&&!create_user.equalsIgnoreCase(userView.getUserName())){
				sql.setLength(0);
				String positionWhr = getPositionWhr(userView);
				sql.append("select 1 from z03 z ");
				sql.append(" where z0301='"+posId+"' and ");
				sql.append(positionWhr);
				rs = dao.search(sql.toString());
				//取得招聘职位权限
				if(!rs.next())
					return false;
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}finally{
    		PubFunc.closeResource(rs);
    	}
    	return true;
    }
    
    /**
     * 检测是否配置了多媒体路径
     * @throws GeneralException
     */
    @Deprecated
    public void checkAttacmentRootDir(Connection con) throws GeneralException {

        try {
            ConstantXml constantXml = new ConstantXml(con, "FILEPATH_PARAM");
            String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            
            if (rootDir == null || "".equals(rootDir)) {
                throw new GeneralException("没有配置多媒体存储路径，请联系管理员！");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("", "没有配置多媒体存储路径，请联系管理员！", "", "");
        }
    }
    
    /**
     * 检查是否具有招聘环节权限
     * （1）	用人单位处理环节不勾选所有能够查看职位的人员都能够操作，勾选用人单位处理环节后，部门负责人只能处理用人单位处理环节，其他人员则相反
	 * （2）	进行了环节操作人设置，选择了“角色”、“岗位”“人员”中的一项或者几项，那么只有这些人能够进行操作，如果在这基础上又选择了招聘负责人或者招聘成员，那么以上所选的人里是招聘负责人或者招聘成员的才能进行操作
	 * （3）	勾选了用人单位处理环节，又进行了环节操作人设置，那么除了部门负责人环节操作人也可以进行操作
     * @param dao
     * @param linkId 环节id
     * @param memberType 成员类型
     * @param userView
     * @param org 是否勾选用人单位环节
     * @param orgFlag "1" 用人单位环节
     * @return
     */
    public Boolean checkPriv(ContentDAO dao ,String linkId,String memberType ,UserView userView, boolean org, String orgFlag){
    	RowSet rs = null;
    	try {
	    	//环节权限判断
	    	StringBuffer sql = new StringBuffer();
			sql.append(" select member_type,role_id,pos_id,emp_id ");
			sql.append(" from zp_flow_links where id='"+linkId+"' ");
			rs = dao.search(sql.toString());
			boolean flag = false;
			boolean isEmptyMember = true;
			if(rs.next()){
				//首先判断成员类别
				String member_type = rs.getString("member_type");
				//岗位
				String pos_id = rs.getString("pos_id");
				//成员
				String emp_id = rs.getString("emp_id");
				//角色
				String role_id = rs.getString("role_id");
				//所有招聘环节操作人参数都没设置
				if(StringUtils.isEmpty(member_type)&&StringUtils.isEmpty(pos_id)&&StringUtils.isEmpty(emp_id)&&StringUtils.isEmpty(role_id))
					return "3".equals(memberType)?!org:!"1".equals(orgFlag);
				//判断环节操作人中有没有设置 招聘负责人  招聘成员  部门负责人 如果没有设置 flag 为true 设置了 并且招聘成员中有 flag为true 否则 false
				if(StringUtils.isNotEmpty(member_type)){
					isEmptyMember = false;
					String[] split = member_type.split(",");
					for (String type : split) {
						if(type.equals(memberType)){
							flag = true;
							break;
						}
					}
				}else{
					flag = true;
				}
				//判断操作人岗位
				String userPosId = userView.getUserPosId();
				
				if(StringUtils.isNotEmpty(pos_id)){
					String[] split = pos_id.split(",");
					for (String type : split) {
						if(type.equals(userPosId))
							return flag&&true;
					}
				}
				//判断操作人员
				if(StringUtils.isNotEmpty(emp_id)){
					String[] split = emp_id.split(",");
					for (String type : split) {
						if(type.equals(userView.getDbname()+userView.getA0100()))
							return flag&&true;
					}
				}
				//判断操作人角色
				ArrayList<String> rolelist = userView.getRolelist();
				if(StringUtils.isNotEmpty(role_id)){
					for (String roleId : rolelist) {
						if(role_id.contains(roleId))
							return flag&&true;
					}
				}
				return isEmptyMember?false:flag
						&&(StringUtils.isEmpty(pos_id)&&StringUtils.isEmpty(emp_id)&&StringUtils.isEmpty(role_id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return true;
    }
    
    /**
     * 将渠道权限参数解析为map
     * @param conn
     * @return
     */
    public HashMap parseHireParames(Connection conn) {
    	HashMap hireParames = new HashMap();
    	try {
	    	ParameterXMLBo parameterXMLBo = new ParameterXMLBo(conn);
	        HashMap map = parameterXMLBo.getAttributeValues();
	        String codeItemIds = this.getCodeItemIds(conn);
	        
	        if (map != null && map.get("hireChannelPriv") != null) {
	        	String hire_object_priv = (String) map.get("hireChannelPriv");
	        	hire_object_priv = "0".equals(hire_object_priv)?"":hire_object_priv;
	        	if(StringUtils.isNotEmpty(hire_object_priv))
	        		hireParames = (HashMap) JSON.parse(hire_object_priv);
	        	
	        	Iterator iter =  hireParames.entrySet().iterator();
	        	while (iter.hasNext()) {
		        	Map.Entry entry = (Map.Entry) iter.next();
		        	String key = (String) entry.getKey();
		        	if(!codeItemIds.contains(","+key+",")){
		        		iter.remove();
		        	}
	        	}
	        }
    	} catch (GeneralException e) {
    		e.printStackTrace();
    	}
		return hireParames;
    }
    
    
    //获取35代码类的有效渠道代码
    private String getCodeItemIds(Connection conn) {
    	RowSet rs = null;
    	ContentDAO dao = new ContentDAO(conn);
    	String CodeItemId ="";
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String backdate = sdf.format(new Date());
        	ArrayList value = new ArrayList();
            StringBuffer sql = new StringBuffer("");
            sql.append("select codeitemid,codeitemdesc,parentid,childid,layer,a0000 ");
        	sql.append(" from codeitem");
        	sql.append(" where codesetid='35'");
        	sql.append(" and invalid=1");
        	sql.append(" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date");
        	sql.append(" order by layer,a0000");
        	rs = dao.search(sql.toString());
        	
        	while(rs.next()) {
        		CodeItemId =CodeItemId + "," +rs.getString("CodeItemId");
        	}
        	CodeItemId = CodeItemId +",";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
		return CodeItemId;
	}
    
    /**获取登录用户渠道权限Map
     * setFalg false 说明没设置权限或者是超级用户，不做处理
     * hirePriv 渠道参数
     * @param userView
     * @param conn
     */
    public HashMap<String, Object> getChannelPrivMap(UserView userView, Connection conn) {
    	RowSet search = null;
    	HashMap<String, Object> parame = new HashMap<String, Object>();
    	try {
    		//是否设置了渠道参数标识 false 说明没设置权限，不做处理
	    	boolean setFlag = false;
    		if(userView.isSuper_admin()){
    			parame.put("setFlag", setFlag);
    			return parame;
    		}
	    	String a0100 = userView.getA0100();
	    	String dbname = userView.getDbname();
	    	ArrayList<String> rolelist = userView.getRolelist();
	    	String userName = userView.getUserName();
	    	ContentDAO dao = new ContentDAO(conn);
	    	String guidkey = "";
	    	if(StringUtils.isNotEmpty(a0100)) {
	    		ArrayList<String> value = new ArrayList<String>();
	    		value.add(a0100);
	    		search = dao.search("select guidkey from "+ dbname+"A01 where a0100=?", value);
	    		if(search.next())
	    			guidkey = search.getString("guidkey");
	    	}
			
	    	ArrayList<String> hirePriv = new ArrayList<String>();
	    	HashMap<String, HashMap> hireParames = this.parseHireParames(conn);
	    	Iterator<Entry<String, HashMap>> iterator = hireParames.entrySet().iterator();
	    	while (iterator.hasNext()) {
		    	Map.Entry<String, HashMap> entry = (Map.Entry<String, HashMap>) iterator.next();
		    	String key = entry.getKey();
		    	HashMap<String, String> parames = entry.getValue();
		    	String emp_id = parames.get("emp_id");
		    	String role_id = parames.get("role_id");
		    	String user_name = parames.get("user_name");
		    	if(StringUtils.isNotEmpty(emp_id) || StringUtils.isNotEmpty(user_name)){
		    		setFlag = true;
		    		if((StringUtils.isNotEmpty(guidkey)&&emp_id.contains(guidkey))
		    			||(StringUtils.isNotEmpty(userName)&&user_name.contains(userName))){
		    		hirePriv.add(key);
		    		continue;
		    	}
		    	}
		    	if(StringUtils.isNotEmpty(role_id)) {
		    		String[] split = role_id.split(",");
		    		for (String string : split) {
		    			if(StringUtils.isNotEmpty(string)) {
		    				setFlag = true;
		    				for (String roleId : rolelist) {
		    					if(string.equals(roleId)) {
		    						hirePriv.add(key);
		    						continue;
		    					}
		    				}
		    			}
					}
		    	}
	    	}
	    	parame.put("setFlag", setFlag);
	    	parame.put("hirePriv", hirePriv);
	    } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(search);
		}
		return parame;
    }
    
    /**
     * 获取招聘职位渠道权限条件
     * @param conn
     * @param parame
     * @return
     */
    public String getHirePrivSql(UserView userView, Connection conn) {
    	String hirePrivSql = "";
    	HashMap<String, Object> parame = getChannelPrivMap(userView, conn);
    	boolean setFlag = (Boolean) parame.get("setFlag");
    	if(setFlag) {
    		ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
    		hirePrivSql = this.getHirePrivSql(conn, hirePriv);
    	}
		return hirePrivSql;
    }
    
    /**
     * 获取登录用户职位的渠道条件
     * @param hirePriv
     * @return
     */
    private String getHirePrivSql(Connection conn, ArrayList<String> hirePriv) {
    	StringBuffer hireSql = new StringBuffer(" and (1=2 ");
    	ParameterXMLBo parameterXMLBo = new ParameterXMLBo(conn, "1");
		try {
			HashMap map = parameterXMLBo.getAttributeValues();
	    	String hire_object = (String) map.get("hire_object");
			if (map.get("hire_object") == null || "".equals((String) map.get("hire_object")))
			    throw GeneralExceptionHandler.Handle(new Exception("请在配置参数中，配置招聘对象指标"));
			FieldItem fieldItem = DataDictionary.getFieldItem("z0384","z03");
	    	if(hirePriv.size()>0) {
	    		for (String hire : hirePriv) {
	    			hireSql.append(" or (z." + hire_object + " like '" + hire + "%' ");
					if (Sql_switcher.searchDbServer() == Constant.MSSQL && fieldItem!=null && "1".equals(fieldItem.getUseflag()))
						hireSql.append(" or ','+z.z0384 like '%," + hire + "%' ");
	                else if(fieldItem!=null && "1".equals(fieldItem.getUseflag()))
	                	hireSql.append(" or ','||z.z0384 like '%," + hire + "%' ");
					hireSql.append(")");
				}
	    	}
	    	hireSql.append(")");
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return hireSql.toString();
    }
}
