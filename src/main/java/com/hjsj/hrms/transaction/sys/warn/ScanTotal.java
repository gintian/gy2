package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 因为定时任务需要逐个预警对象用户发送邮件通知
 * 所以抽象此类型
 * 以便定时任务与界面登录复用该组件。
 * @author zhm
 *
 */
public class ScanTotal implements IConstant{
	private UserView userView = null;
	
	public UserView getUserView() {
		return userView;
	}
	
	public void setUserView(UserView userview) {
		this.userView = userview;
	}
	
	public ScanTotal(UserView userview){
		setUserView(userview);
	}	
	
	/**
	 * 是否本人特征的角色 
	 * @author guodd 2019-07-23
	 * @param roleId 角色id
	 * @return true/false
	 */
	public static boolean isSelfRole(String roleId) {
		
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			RecordVo vo = new RecordVo("t_sys_role");
			vo.setString("role_id", roleId);
			vo = dao.findByPrimaryKey(vo);
			if(14 == vo.getInt("role_property"))
				return true;
			
		}catch(Exception e) {
			
		}finally {
			PubFunc.closeResource(conn);
		}
		return false;
	}
	
	/**
	 * 预警结果是否包含当前userview人员
	 * @param dbean 预警
	 * @return
	 */
	public static boolean isHasSelf(UserView userView,DynaBean dbean) {
		
		//如果不是自助用户或关联了自助的业务用户，直接返回false
		if(userView.getStatus()==0 && userView.getA0100().length()<1) {
			return false;
		}
		//不是人员预警，直接返回false
		String warntyp=(String)dbean.get(Key_HrpWarn_FieldName_Warntyp);
		if(!"0".equals(warntyp)) {
			return false;
		}
		
		Connection conn=null;
		RowSet rs = null;
		try {
			String strWid = (String)dbean.get(Key_HrpWarn_FieldName_ID);
			String dbname = userView.getDbname();
			String A0100 = userView.getA0100();
			
			String sql = "select 1 from hrpwarn_result where wid=? and nbase=? and a0100=?";
			List params = Arrays.asList(new String[] {strWid,dbname,A0100});
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql, params);
			if(rs.next())
				return true;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		return false;
	}
	/**
	 * 根据UserView查询相应的预警信息
	 * @return ArrayList alResult
	 * CommonData firstEmptyData = new CommonData();
	 * firstEmptyData.setDataName(String warnCMsg);
	 * firstEmptyData.setDataValue(String warnWid);
	 * alResult.add( firstEmptyData );	 
	 * @throws GeneralException
	 */
	public ArrayList execute() {
		
		HashMap mapWarnConfig = ContextTools.getWarnConfigCache();
		Iterator it = mapWarnConfig.keySet().iterator();
		//UserView userview = (UserView)getUserView();// pageContext.getSession().getAttribute(WebConstant.userView);
		String strUserOrg = "UN"+getUserView().getUserOrgId();//getManagePrivCode();
		String privValue=getUserView().getManagePrivCode()+getUserView().getManagePrivCodeValue();
		ArrayList alUserRoles = getUserView().getRolelist();
		ArrayList orderlist=new ArrayList();
//		HashMap htmlResultMap = new HashMap();
		ArrayList alResult = new ArrayList();
		HashMap resultMap = new HashMap();
		while(it.hasNext()){
			String strWid = (String)it.next();			
			if(!getHrpWarnValib(strWid))
				continue;
			DynaBean dbean = (DynaBean)mapWarnConfig.get( strWid );
			ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
			String warntyp=(String)dbean.get(Key_HrpWarn_FieldName_Warntyp);
			if(warntyp==null||warntyp.length()<=0)
				warntyp="0";//预警类型 0：人员;1：单位;2:职位；3：业务
			String strDomain = "";
			if(ctrlVo!=null){
				strDomain = ctrlVo.getStrDomain();// 预警对象
			}
			boolean isShow = false;
			/*预警结果列表是否只显示自己的数据 guodd 2019-07-24
	         * 当预警为人员预警，且预警对象为角色，并设置了【本人特征】的角色
	         * 如果当前用户没有预警设置的角色权限，默认走本人角色权限
	         * 本人角色权限的特点是 如果预警结果中有自己，则只显示自己的数据；如果没有，则不显示此预警
	         * 注意：本人角色 只有当前userview为自助用户 或 关联了自助的业务用户 时才适用
	         * */
			boolean onlyShowSelf = false;
			if( getUserView().isSuper_admin() ){
				isShow=true;
			}else if(strDomain==null || strDomain.trim().length()<1){
				isShow=true;
			}else if( strDomain.startsWith("RL")){//预警对象为角色
				String[] roles = strDomain.split(",");
				//预警角色里是否有【本人角色】
				boolean hasSelfRole = false;
				//判断当前userview是否是自助用户 或 关联了自助用户
				boolean isEmpUser = userView.getStatus()==4 || userView.getA0100().length()>0;
				for(int j=0;j<roles.length;j++){
					
					if(  alUserRoles.contains(roles[j].substring(2))){
						isShow = true;
						break;
					}
					
					/*是自助（或关联自助的业务用户） ，且是人员预警，判断当前角色是不是【本人角色】。如果已经存在本人角色了（hasSelfRole=true），就不用判断了  guodd 2019-07-24*/
					if(isEmpUser && !hasSelfRole && "0".equals(warntyp))
						hasSelfRole =  ScanTotal.isSelfRole(roles[j].substring(2));
				}
				
				/*如果当前userview没有 预警对象设置的角色 权限时，但是预警角色中有本人角色，认为显示此预警 guodd 2019-07-24*/
				if(!isShow && hasSelfRole) {
					isShow = true;
					onlyShowSelf = true;
				}
			}else {
				String[] orgs = strDomain.split(",");
				String temp=null;
				for(int j=0;j<orgs.length;j++){
					if( "UN".equals(orgs[j])){//所有组织
						isShow = true;
						break;
					}else if( orgs[j].startsWith("UN")){//分支组织
						//上级单位的
						isShow=orgs[j].startsWith(strUserOrg);
						if(!isShow&&privValue.indexOf("UN")!=-1)
							isShow=orgs[j].startsWith(privValue);
						//下面是子单位的
						if(!isShow&&strUserOrg.indexOf("UN")!=-1)
						   isShow=strUserOrg.startsWith(orgs[j]);
						if(!isShow&&privValue.indexOf("UN")!=-1)
							isShow=privValue.startsWith(orgs[j]);
						if(isShow)
							break;
					}else if( orgs[j].startsWith("UM")){//部门组织
						temp="UM"+getUserView().getUserDeptId();
						//上级部门的
						isShow=orgs[j].startsWith(temp);
						if(!isShow&&privValue.indexOf("UM")!=-1)
							isShow=orgs[j].startsWith(privValue);
						//子部门的
						if(!isShow)
						  isShow=temp.startsWith(orgs[j]);
						if(!isShow&&(privValue.indexOf("UN")!=-1||privValue.indexOf("UM")!=-1))
							isShow=privValue.substring(2).startsWith(orgs[j].substring(2));
						//isShow = (temp.indexOf( orgs[j].substring(2))>=0);
						if(isShow)
							break;
					
					}else if( orgs[j].startsWith("@K")){//职务组织
						temp="@K"+getUserView().getUserPosId();	
						isShow=temp.startsWith(orgs[j]);	
						if(!isShow&&(privValue.indexOf("UN")!=-1||privValue.indexOf("UM")!=-1||privValue.indexOf("@K")!=-1))
							isShow=privValue.substring(2).startsWith(orgs[j].substring(2));
						//isShow = ( temp.indexOf(orgs[j].substring(2))>=0);
						if(isShow)
							break;
					}
				}				
			}
			
			// 因为预警对象的组织树可能包含人员信息，所以需要判断人员的pk
//			if( strDomain.indexOf( getUserView().getDbname()+getUserView().getA0100()) >= 0){
//				isShow = true;
//			}
			
			if( isShow )
			{
				if(orderlist.size()<=0)
					  orderlist.add(strWid);
				else
				{
					boolean isCorrect=false;
					for(int i=0;i<orderlist.size();i++)
					{
						String o_wid=orderlist.get(i).toString();
						DynaBean o_dbean = (DynaBean)mapWarnConfig.get(o_wid);//按优先级排序，xuj update 2011-9-26
						String o_norder=(String)o_dbean.get("norder");
						String norder=(String)dbean.get("norder");
						String wid=(String)dbean.get("wid");//获取 wid号  wangb 20170727 22510
						o_norder = ("".equals(o_norder) || o_norder == null)?"0":o_norder;
						norder = (norder == "" || norder == null)?"0":norder;
						
//						if(Integer.parseInt(o_norder)>=Integer.parseInt(norder)){
//							orderlist.add(i,strWid);
//							isCorrect=true;
//							break;
//						}
						
						if(Integer.parseInt(o_norder)<Integer.parseInt(norder))//比较优先级 当前 norder 优先级 小 直接跳过 wangb 20170727 22510
							continue;
						if(Integer.parseInt(o_norder)==Integer.parseInt(norder) && 
								Integer.parseInt(wid)<Integer.parseInt(o_wid))//优先级相同 比较wid号 wid号小 跳过 wangb 20170727 22510
							continue;
						
						orderlist.add(i,strWid);
						isCorrect=true;
						break;
					}
					if(!isCorrect)
						orderlist.add(strWid);
				}
				if(onlyShowSelf) { //如果是只显示自己的数据时  guodd 2019-07-24
					//判断预警结果中是否包含当前userview用户，如果包含则显示，不包含就跳过
					if(ScanTotal.isHasSelf(userView,dbean)) {
						String strShow = (String)dbean.get(Key_HrpWarn_FieldName_Msg);
						CommonData firstEmptyData = new CommonData();
						firstEmptyData.setDataName(strShow);
						firstEmptyData.setDataValue(strWid);
						resultMap.put(strWid, firstEmptyData);
					}
					
				}else if("1".equals(ctrlVo.getIsComplex())){// 复杂查询
					if("0".equals(warntyp))
					{
						int total = getCount(dbean);
						if( total>0 ){
		//					String strShow = "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
							String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+"人)";
							//strShow=strShow.replace("\"","'");
							strShow=strShow.replaceAll("\"","'");
							strShow=strShow.replaceAll("\n", "");
							strShow=strShow.replaceAll("\r", "");
							CommonData firstEmptyData = new CommonData();
							firstEmptyData.setDataName(strShow);
							firstEmptyData.setDataValue(strWid);
							//alResult.add(firstEmptyData);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
							resultMap.put(strWid, firstEmptyData);
						}
					}else if("1".equals(warntyp)|| "2".equals(warntyp))
					{
						int total=getCountOrg (dbean);
						if( total>0 ){
							String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+")";
							//strShow=strShow.replace("\"","'");
							strShow=strShow.replaceAll("\"","'");
							strShow=strShow.replaceAll("\n", "");
							strShow=strShow.replaceAll("\r", "");
							CommonData firstEmptyData = new CommonData();
							firstEmptyData.setDataName(strShow);
							firstEmptyData.setDataValue(strWid);
							//alResult.add(firstEmptyData);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
							resultMap.put(strWid, firstEmptyData);
						}
					}else if("3".equals(warntyp))
					{
						
					}
					
				}else{// 简单查询
					int total = getCount(dbean);
					if( total>0 ){
	//					String strShow = "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
						String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+"人)";
						CommonData firstEmptyData = new CommonData();
						//strShow=strShow.replace("\"","'");
						strShow=strShow.replaceAll("\"","'");
						strShow=strShow.replaceAll("\n", "");
						strShow=strShow.replaceAll("\r", "");
						firstEmptyData.setDataName(strShow);
						firstEmptyData.setDataValue(strWid);
						resultMap.put(strWid, firstEmptyData);
						//alResult.add(firstEmptyData);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
					}
				}
			}
		}
		for(int i=0;i<orderlist.size();i++)
		{
			String o_wid=orderlist.get(i).toString();
			CommonData firstEmptyData=(CommonData)resultMap.get(o_wid);
			if(firstEmptyData!=null)
			{
				//System.out.println(firstEmptyData);
				alResult.add(firstEmptyData);	
			}
		}		
		return alResult;
//		showHtml(htmlResultMap);
	}
/**
 * 
 * @param show_warntype
 * @return
 */
public ArrayList execute(String show_warntype) {
		
		HashMap mapWarnConfig = ContextTools.getWarnConfigCache();
		Iterator it = mapWarnConfig.keySet().iterator();
		//UserView userview = (UserView)getUserView();// pageContext.getSession().getAttribute(WebConstant.userView);
		String strUserOrg = "UN"+getUserView().getUserOrgId();//getManagePrivCode();
		String privValue=getUserView().getManagePrivCode()+getUserView().getManagePrivCodeValue();
		ArrayList alUserRoles = getUserView().getRolelist();
		ArrayList orderlist=new ArrayList();
//		HashMap htmlResultMap = new HashMap();
		ArrayList alResult = new ArrayList();
		HashMap resultMap = new HashMap();
		while(it.hasNext()){
			String strWid = (String)it.next();			
			if(!getHrpWarnValib(strWid))
				continue;
			DynaBean dbean = (DynaBean)mapWarnConfig.get( strWid );
			ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
			String warntyp=(String)dbean.get(Key_HrpWarn_FieldName_Warntyp);
			if(warntyp==null||warntyp.length()<=0)
				warntyp="0";//预警类型 0：人员;1：单位;2:职位；3：业务
			if(show_warntype.indexOf(warntyp)==-1)
				continue;
			String strDomain = ctrlVo.getStrDomain();// 预警对象
			boolean isShow = false;
			if( getUserView().isSuper_admin() ){
				isShow=true;
			}else if(strDomain==null || strDomain.trim().length()<1){
				isShow=true;
			}else if( strDomain.startsWith("RL")){
				String[] roles = strDomain.split(",");
				for(int j=0;j<roles.length;j++){
					if(  alUserRoles.contains(roles[j].substring(2))){
						isShow = true;
						break;
					}
				}
			}else {
				String[] orgs = strDomain.split(",");
				String temp=null;
				for(int j=0;j<orgs.length;j++){
					if( "UN".equals(orgs[j])){//所有组织
						isShow = true;
						break;
					}else if( orgs[j].startsWith("UN")){//分支组织
						isShow=strUserOrg.startsWith(orgs[j]);
						if(!isShow&&privValue.indexOf("UN")!=-1)
							isShow=privValue.startsWith(orgs[j]);
						break;
					}else if( orgs[j].startsWith("UM")){//部门组织
						temp="UM"+getUserView().getUserDeptId();
						isShow=temp.startsWith(orgs[j]);
						if(!isShow&&privValue.indexOf("UM")!=-1)
							isShow=privValue.startsWith(orgs[j]);
						//isShow = (temp.indexOf( orgs[j].substring(2))>=0);
						break;
					
					}else if( orgs[j].startsWith("@K")){//职务组织
						temp="@K"+getUserView().getUserPosId();	
						isShow=temp.startsWith(orgs[j]);	
						if(!isShow&&privValue.indexOf("@K")!=-1)
							isShow=privValue.startsWith(orgs[j]);
						//isShow = ( temp.indexOf(orgs[j].substring(2))>=0);
						break;
					}
				}				
			}
			
			// 因为预警对象的组织树可能包含人员信息，所以需要判断人员的pk
//			if( strDomain.indexOf( getUserView().getDbname()+getUserView().getA0100()) >= 0){
//				isShow = true;
//			}
			
			if( isShow )
			{
				if(orderlist.size()<=0)
					  orderlist.add(strWid);
				else
				{
					boolean isCorrect=false;
					for(int i=0;i<orderlist.size();i++)
					{
						String o_wid=orderlist.get(i).toString();
						if(Integer.parseInt(o_wid)>Integer.parseInt(strWid))
						{
							orderlist.add(i,strWid);
							isCorrect=true;
							break;
						}
					}
					if(!isCorrect)
						orderlist.add(strWid);
				}				
				if("1".equals(ctrlVo.getIsComplex())){// 复杂查询
					if("0".equals(warntyp))
					{
						int total = getCount(dbean);
						if( total>0 ){
		//					String strShow = "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
							String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+"人)";
							//strShow=strShow.replace("\"","'");
							strShow=strShow.replaceAll("\"","'");
							strShow=strShow.replaceAll("\n", "");
							strShow=strShow.replaceAll("\r", "");
							CommonData firstEmptyData = new CommonData();
							firstEmptyData.setDataName(strShow);
							firstEmptyData.setDataValue(strWid);
							//alResult.add(firstEmptyData);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
							resultMap.put(strWid, firstEmptyData);
						}
					}else if("1".equals(warntyp)|| "2".equals(warntyp))
					{
						int total=getCountOrg (dbean);
						if( total>0 ){
							String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+")";
							//strShow=strShow.replace("\"","'");
							strShow=strShow.replaceAll("\"","'");
							strShow=strShow.replaceAll("\n", "");
							strShow=strShow.replaceAll("\r", "");
							CommonData firstEmptyData = new CommonData();
							firstEmptyData.setDataName(strShow);
							firstEmptyData.setDataValue(strWid);
							//alResult.add(firstEmptyData);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
							resultMap.put(strWid, firstEmptyData);
						}
					}else if("3".equals(warntyp))
					{
						
					}
					
				}else{// 简单查询
					int total = getCount(dbean);
					if( total>0 ){
	//					String strShow = "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
						String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+"人)";
						CommonData firstEmptyData = new CommonData();
						//strShow=strShow.replace("\"","'");
						strShow=strShow.replaceAll("\"","'");
						strShow=strShow.replaceAll("\n", "");
						strShow=strShow.replaceAll("\r", "");
						firstEmptyData.setDataName(strShow);
						firstEmptyData.setDataValue(strWid);
						resultMap.put(strWid, firstEmptyData);
						//alResult.add(firstEmptyData);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
					}
				}
			}
		}
		for(int i=0;i<orderlist.size();i++)
		{
			String o_wid=orderlist.get(i).toString();
			CommonData firstEmptyData=(CommonData)resultMap.get(o_wid);
			if(firstEmptyData!=null)
			{
				//System.out.println(firstEmptyData);
				alResult.add(firstEmptyData);	
			}
		}		
		return alResult;
//		showHtml(htmlResultMap);
	}
	
	/**
	 * 根据UserView查询相应的预警信息
	 * @return ArrayList alResult
	 * CommonData firstEmptyData = new CommonData();
	 * firstEmptyData.setDataName(String warnCMsg);
	 * firstEmptyData.setDataValue(String warnWid);
	 * alResult.add( firstEmptyData );	 
	 * @throws GeneralException
	 */
	public ArrayList myWarnResult() {
		
		HashMap mapWarnConfig = ContextTools.getWarnConfigCache();
		Iterator it = mapWarnConfig.keySet().iterator();
		//UserView userview = (UserView)getUserView();// pageContext.getSession().getAttribute(WebConstant.userView);
		String strUserOrg = "UN"+getUserView().getUserOrgId();//getManagePrivCode();
		ArrayList alUserRoles = getUserView().getRolelist();
		
//		HashMap htmlResultMap = new HashMap();
		ArrayList alResult = new ArrayList();
		while(it.hasNext()){
			String strWid = (String)it.next();
			DynaBean dbean = (DynaBean)mapWarnConfig.get( strWid );
			ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
			String strDomain = ctrlVo.getStrDomain();// 预警对象			
			boolean isShow = false;
			if( getUserView().isSuper_admin() ){
				isShow=true;
			}else if(strDomain==null || strDomain.trim().length()<1){
				isShow=true;
			}else if( strDomain.startsWith("RL")){
				String[] roles = strDomain.split(",");
				for(int j=0;j<roles.length;j++){
					if(  alUserRoles.contains(roles[j].substring(2))){
						isShow = true;
						break;
					}
				}
			}else {
				String[] orgs = strDomain.split(",");
				String temp=null;
				for(int j=0;j<orgs.length;j++){
					if( "UN".equals(orgs[j])){//所有组织
						isShow = true;
						break;
					}else if( orgs[j].startsWith("UN")){//分支组织
						isShow=strUserOrg.startsWith(orgs[j]);
						break;
					}else if( orgs[j].startsWith("UM")){//部门组织
						temp="UM"+getUserView().getUserDeptId();
						isShow=temp.startsWith(orgs[j]);
						//isShow = (temp.indexOf( orgs[j].substring(2))>=0);
						break;
					
					}else if( orgs[j].startsWith("@K")){//职务组织
						temp="@K"+getUserView().getUserPosId();	
						isShow=temp.startsWith(orgs[j]);						
						//isShow = ( temp.indexOf(orgs[j].substring(2))>=0);
						break;
					}
				}				
			}
			
			// 因为预警对象的组织树可能包含人员信息，所以需要判断人员的pk
//			if( strDomain.indexOf( getUserView().getDbname()+getUserView().getA0100()) >= 0){
//				isShow = true;
//			}
			
			if( isShow ){
				if("1".equals(ctrlVo.getIsComplex())){// 复杂查询
					int total = getCount(dbean);
					if( total>0 ){
	//					String strShow = "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
						String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+"人)";
						String wname = (String) dbean.get(Key_HrpWarn_FieldName_Name);
						RecordVo vo = new RecordVo("hrpwarn");
						vo.setString("wname",wname);
						vo.setString("wid", strWid);
						vo.setString("cmsg",strShow);		
						alResult.add(vo);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
					}
				}else{// 简单查询
					int total = getCount(dbean);
					if( total>0 ){
	//					String strShow = "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
						String strShow = dbean.get(Key_HrpWarn_FieldName_Msg)+" (计"+total+"人)";
						String wname = (String) dbean.get(Key_HrpWarn_FieldName_Name);
						RecordVo vo = new RecordVo("hrpwarn");
						vo.setString("wname",wname);
						vo.setString("wid", strWid);
						vo.setString("cmsg",strShow);		
						alResult.add(vo);// "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
					}
				}
			}
		}
		return alResult;
//		showHtml(htmlResultMap);
	}
	
	/**
	 * 检索userView对应的预警信息
	 * @param dbean：预警设置vo
	 * @param names：人员姓名
	 * @return 预警结果：人数
	 */
	public int getCount(DynaBean dbean,StringBuffer names,boolean ishtml){
		String strWid = (String)dbean.get(Key_HrpWarn_FieldName_ID);
		ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
		String strNbase=ctrlVo.getStrNbase();
		DomainTool tool = new DomainTool();
		Connection conn=null;
		ArrayList warn_dblist=new ArrayList();
		ArrayList dblist=new ArrayList();	
		String seprartor="/";
		try
		{
			  conn = AdminDb.getConnection();
			  ContentDAO dao=new ContentDAO(conn);	
			  Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			  seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			  seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
			  ArrayList pre_list= tool. getNbaseList(ctrlVo.getStrNbase(), dao);
			  if(pre_list!=null&&pre_list.size()>0)
			  {
				  for(int i=0;i<pre_list.size();i++)
			      {
					  String nbase=(String)pre_list.get(i);
					  if(nbase==null||nbase.length()<=0)
						  continue;
					  for(int r=0;r<this.userView.getPrivDbList().size();r++)
				      {
						  if(this.userView.getPrivDbList().get(r)!=null&&nbase.equalsIgnoreCase(this.userView.getPrivDbList().get(r).toString()))
							  dblist.add(nbase);
				      }
			      }
			  }
			  else
				dblist=this.userView.getPrivDbList();
			  warn_dblist= tool.  getKqNbaseList(dblist,strNbase,dao);
     	}catch(Exception e)
 	   {
 		   e.printStackTrace();
 	   }finally
 	   {
 		   try{		
 			  if (conn != null){
    				conn.close();
    			}
    		    }catch (Exception ee){
    			    ee.printStackTrace();
    		    }
 	    }
		
		UserView userview = (UserView) getUserView();
		
//		StringBuffer sbTemp = new StringBuffer();
		StringBuffer sbPreCondition = new StringBuffer();		
//		StringBuffer str_pre=new StringBuffer();
//		sbTemp.append("select count(a0100) as total from hrpwarn_result where wid='" +strWid+"'" );
		//ArrayList sqllist=new ArrayList();
		Map<String, String> sqlMap=new HashMap<String, String>();
		//if(!userview.isSuper_admin()) {//如果不是管理员				
			try {
		    	//ArrayList dblist=this.userView.getPrivDbList(); // 管理员返回所有的库，一般操作员返回其权限库
		    	if( dblist.size()<1 ){
		    		return 0; // 权限库为空时，不应取得任何数据
		    	}else
		    	{
		    		if(warn_dblist!=null&&warn_dblist.size()>0)
		    			dblist=warn_dblist;
		    	}
		    	for(int i=0; i< dblist.size(); i++){
					String strPre = (String)dblist.get(i);
					sbPreCondition=new StringBuffer();
		    		sbPreCondition.append("select a0100,nbase from hrpwarn_result where wid='" +strWid+"'" );
		    		sbPreCondition.append(" and nbase='");
		    		sbPreCondition.append(strPre);
		    		sbPreCondition.append("' and (hrpwarn_result.a0100 in (select "+strPre+"a01.a0100 ");
					String strWhere = userview.getPrivSQLExpression("",strPre,false, true,new ArrayList());// fieldList);
					sbPreCondition.append(strWhere);
					sbPreCondition.append(")");
	        		if(this.userView.getStatus()==4&&this.userView.haveTheRoleProperty("14")){//本人角色特征
	        			sbPreCondition.append(" or hrpwarn_result.a0100='"+userView.getA0100()+"'");
	        		}
	        		/*String value = userView.getManagePrivCodeValue();
	    			String item="";
	    			String f = userView.getManagePrivCode();//单位还是部门
	    			if(f.equalsIgnoreCase("UN")){//单位
	    				item="B0110";
	    			}else if(f.equalsIgnoreCase("UM")){//部门
	    				item="E0122";
	    			}else if(f.equalsIgnoreCase("@K")){//职位
	    				item="E01A1";
	    			}
	        		if(item!=null&&item.length()>0&&value!=null&&value.length()>0)
	    			{
	        			sbPreCondition.append(" and ");
	        			sbPreCondition.append(item);
	        			sbPreCondition.append(" like'");
	        			sbPreCondition.append(value);
	        			sbPreCondition.append("%'  ");
	    			}*/
	        		sbPreCondition.append(")");
	        		Category.getInstance(this.getClass()).debug( strWhere );
	        		/*if( i<dblist.size()-1){
	        			sbPreCondition.append(" union ");
	        		}*/
	        		//sqllist.add(sbPreCondition.toString());
	        		sqlMap.put(strPre, sbPreCondition.toString());
		    	}
			
			} catch (Exception e) {
				e.printStackTrace();
			}				
		/*}
		else
		{
			sbPreCondition.append("select count(a0100) as total from hrpwarn_result where wid='" +strWid+"'");
			sqllist.add(sbPreCondition.toString());
		}*/
		int iTotal = 0;
		
		for(String nbase:sqlMap.keySet()) {
			String sql=sqlMap.get(nbase);
			List users  = ExecuteSQL.executeMyQuery("select b0110,e0122,e01a1,a0101 from "+nbase+"a01 where a0100 in (select A0100 from ("+sql+") u ) order by b0110,e0122,e01a1,a0000");
			if(users!=null&&users.size()>0) {
				iTotal+=users.size();
				for(int i=0;i<users.size();i++){
					LazyDynaBean rec= (LazyDynaBean)users.get(i);
					String b0100  = AdminCode.getCodeName("UN",(String)rec.get("b0110"));
					CodeItem code = AdminCode.getCode("UM", (String)rec.get("e0122"), 5);
					String e0122  = (code!=null)?code.getCodename().replaceAll(seprartor, "-"):"";
					String e01a1  = AdminCode.getCodeName("@K", (String)rec.get("e01a1"));
					String a0101 = (String)rec.get("a0101");
					//names.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ (b0100.length()>0?(b0100):"")+(e0122.length()>0?("-"+e0122):"")+(e01a1.length()>0?("-"+e01a1):"")+"&nbsp;&nbsp;&nbsp;&nbsp;"+a0101);
					if(ishtml)
						names.append("<p style=\"margin-bottom:10px;\">"+ (b0100.length()>0?(b0100):"")+(e0122.length()>0?("-"+e0122):"")+(e01a1.length()>0?("-"+e01a1):"")+"&nbsp;&nbsp;"+a0101+"</p>");
					else
						names.append("\n        "+(b0100.length()>0?(b0100):"")+(e0122.length()>0?("-"+e0122):"")+(e01a1.length()>0?("-"+e01a1):"")+"    "+a0101);
				}
			}
		}
		
		/*for(int r=0;r<sqllist.size();r++)
		{
			String sql=sqllist.get(r).toString();
			ArrayList alTotal = TransTool.executeQuerySql(sql);
			
			if(alTotal!=null){
				ArrayList a0100s = new ArrayList();
				String dbpre = "";
				for(int i=0;i<alTotal.size();i++){
					a0100s.add((String)((DynaBean)alTotal.get(i)).get("a0100"));
					if(i==0)
						dbpre = (String)((DynaBean)alTotal.get(i)).get("nbase");
				}
				iTotal=iTotal+a0100s.size();
				if(a0100s.size()>0){
					List users  = ExecuteSQL.executeMyQuery("select b0110,e0122,e01a1,a0101 from "+dbpre+"a01 where a0100 in('"+a0100s.toString().substring(1,a0100s.toString().length()-1).replaceAll(", ", "','")+"') order by b0110,e0122,e01a1,a0000");
					for(int i=0;i<users.size();i++){
						LazyDynaBean rec= (LazyDynaBean)users.get(i);
						String b0100  = AdminCode.getCodeName("UN",(String)rec.get("b0110"));
						CodeItem code = AdminCode.getCode("UM", (String)rec.get("e0122"), 5);
						String e0122  = (code!=null)?code.getCodename().replaceAll(seprartor, "-"):"";
						String e01a1  = AdminCode.getCodeName("@K", (String)rec.get("e01a1"));
						String a0101 = (String)rec.get("a0101");
						//names.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ (b0100.length()>0?(b0100):"")+(e0122.length()>0?("-"+e0122):"")+(e01a1.length()>0?("-"+e01a1):"")+"&nbsp;&nbsp;&nbsp;&nbsp;"+a0101);
						if(ishtml)
							names.append("<p style=\"margin-bottom:10px;\">"+ (b0100.length()>0?(b0100):"")+(e0122.length()>0?("-"+e0122):"")+(e01a1.length()>0?("-"+e01a1):"")+"&nbsp;&nbsp;"+a0101+"</p>");
						else
							names.append("\n        "+(b0100.length()>0?(b0100):"")+(e0122.length()>0?("-"+e0122):"")+(e01a1.length()>0?("-"+e01a1):"")+"    "+a0101);
					}
				}
			}
		}*/
		
		return iTotal;
	}
	
	/**
	 * 检索userView对应的预警信息
	 * @param dbean：预警设置vo
	 * @return 预警结果：人数
	 */
	public int getCount(DynaBean dbean){
		String strWid = (String)dbean.get(Key_HrpWarn_FieldName_ID);
		ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
		String strNbase=ctrlVo.getStrNbase();
		DomainTool tool = new DomainTool();
		Connection conn=null;
		ArrayList warn_dblist=new ArrayList();
		ArrayList dblist=new ArrayList();		
		try
		{
			  conn = AdminDb.getConnection();
			  ContentDAO dao=new ContentDAO(conn);	
			  ArrayList pre_list= tool. getNbaseList(ctrlVo.getStrNbase(), dao);
			  if(pre_list!=null&&pre_list.size()>0)
			  {
				  for(int i=0;i<pre_list.size();i++)
			      {
					  String nbase=(String)pre_list.get(i);
					  if(nbase==null||nbase.length()<=0)
						  continue;
					  for(int r=0;r<this.userView.getPrivDbList().size();r++)
				      {
						  if(this.userView.getPrivDbList().get(r)!=null&&nbase.equalsIgnoreCase(this.userView.getPrivDbList().get(r).toString()))
							  dblist.add(nbase);
				      }
			      }
			  }
			  else
				dblist=this.userView.getPrivDbList();
			  warn_dblist= tool.  getKqNbaseList(dblist,strNbase,dao);
     	}catch(Exception e)
 	   {
 		   e.printStackTrace();
 	   }finally
 	   {
 		   try{		
 			  if (conn != null){
    				conn.close();
    			}
    		    }catch (Exception ee){
    			    ee.printStackTrace();
    		    }
 	    }
		
		UserView userview = (UserView) getUserView();
		
//		StringBuffer sbTemp = new StringBuffer();
		StringBuffer sbPreCondition = new StringBuffer();		
//		StringBuffer str_pre=new StringBuffer();
//		sbTemp.append("select count(a0100) as total from hrpwarn_result where wid='" +strWid+"'" );
		ArrayList sqllist=new ArrayList();
		//if(!userview.isSuper_admin()) {//如果不是管理员				
			try {
		    	//ArrayList dblist=this.userView.getPrivDbList(); // 管理员返回所有的库，一般操作员返回其权限库
		    	if( dblist.size()<1 ){
		    		return 0; // 权限库为空时，不应取得任何数据
		    	}else
		    	{
		    		if(warn_dblist!=null&&warn_dblist.size()>0)
		    			dblist=warn_dblist;
		    	}
		    	for(int i=0; i< dblist.size(); i++){
					String strPre = (String)dblist.get(i);
					sbPreCondition=new StringBuffer();
		    		sbPreCondition.append("select count(a0100) as total from hrpwarn_result where wid='" +strWid+"'" );
		    		sbPreCondition.append(" and nbase='");
		    		sbPreCondition.append(strPre);
		    		sbPreCondition.append("' and (hrpwarn_result.a0100 in (select "+strPre+"a01.a0100 ");
					String strWhere = userview.getPrivSQLExpression("",strPre,false, true,new ArrayList());// fieldList);
					sbPreCondition.append(strWhere);
					sbPreCondition.append(")");
	        		if(this.userView.getStatus()==4&&this.userView.haveTheRoleProperty("14")){//本人角色特征
	        			sbPreCondition.append(" or hrpwarn_result.a0100='"+userView.getA0100()+"'");
	        		}
	        		/*String value = userView.getManagePrivCodeValue();
	    			String item="";
	    			String f = userView.getManagePrivCode();//单位还是部门
	    			if(f.equalsIgnoreCase("UN")){//单位
	    				item="B0110";
	    			}else if(f.equalsIgnoreCase("UM")){//部门
	    				item="E0122";
	    			}else if(f.equalsIgnoreCase("@K")){//职位
	    				item="E01A1";
	    			}
	        		if(item!=null&&item.length()>0&&value!=null&&value.length()>0)
	    			{
	        			sbPreCondition.append(" and ");
	        			sbPreCondition.append(item);
	        			sbPreCondition.append(" like'");
	        			sbPreCondition.append(value);
	        			sbPreCondition.append("%'  ");
	    			}*/
	        		sbPreCondition.append(")");
	        		Category.getInstance(this.getClass()).debug( strWhere );
	        		/*if( i<dblist.size()-1){
	        			sbPreCondition.append(" union ");
	        		}*/
	        		sqllist.add(sbPreCondition.toString());
		    	}
			
			} catch (Exception e) {
				e.printStackTrace();
			}				
		/*}
		else
		{
			sbPreCondition.append("select count(a0100) as total from hrpwarn_result where wid='" +strWid+"'");
			sqllist.add(sbPreCondition.toString());
		}*/
		int iTotal = 0;
		for(int r=0;r<sqllist.size();r++)
		{
			String sql=sqllist.get(r).toString();
			ArrayList alTotal = TransTool.executeQuerySql(sql);
			
			if(alTotal!=null){
				for(int i=0;i<alTotal.size();i++)
					iTotal=iTotal+Integer.parseInt( (String)((DynaBean)alTotal.get(i)).get("total"));
			}
		}
		
		return iTotal;
	}
	
	 public String getHrpwarn_resultUsernanme(DynaBean dbean,ContentDAO dao)
	    {
		 
		 String wid = (String)dbean.get(Key_HrpWarn_FieldName_ID);
			ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
			String strNbase=ctrlVo.getStrNbase();
			DomainTool tool = new DomainTool();
			Connection conn=null;
			ArrayList warn_dblist=new ArrayList();
			ArrayList dblist=new ArrayList();
			RowSet rs=null;
			StringBuffer empStr=new StringBuffer();
			try
			{
				  ArrayList pre_list= tool. getNbaseList(ctrlVo.getStrNbase(), dao);
				  if(pre_list!=null&&pre_list.size()>0)
				  {
					  for(int i=0;i<pre_list.size();i++)
				      {
						  String nbase=(String)pre_list.get(i);
						  if(nbase==null||nbase.length()<=0)
							  continue;
						  for(int r=0;r<this.userView.getPrivDbList().size();r++)
					      {
							  if(this.userView.getPrivDbList().get(r)!=null&&nbase.equalsIgnoreCase(this.userView.getPrivDbList().get(r).toString()))
								  dblist.add(nbase);
					      }
				      }
				  }
				  else
					dblist=this.userView.getPrivDbList();
				  warn_dblist= tool.  getKqNbaseList(dblist,strNbase,dao);
				  
				  if( dblist.size()<1 ){
			    		return ""; // 权限库为空时，不应取得任何数据
			    	}else
			    	{
			    		if(warn_dblist!=null&&warn_dblist.size()>0)
			    			dblist=warn_dblist;
			    	}
			    	int count=10;
			    	int r=0;
		    		boolean isCorrect=true;
		    		String sql="";
			    	for(int i=0; i< dblist.size(); i++){
			    			String nbase=(String)dblist.get(i);
			    			String strWhere = this.userView.getPrivSQLExpression("",nbase,false, true,new ArrayList());// fieldList);
			    			sql="select a0101 "+strWhere+" and exists(select 1 from  Hrpwarn_result H where H.a0100="+nbase+"a01.a0100 and H.nbase='"+nbase+"' and wid='"+wid+"')";
			    			rs=dao.search(sql);
			    			while(rs.next())
			    			{
			    				empStr.append(rs.getString("a0101"));
			    				r++;
			    				if(r>count)
			    				{
			    					isCorrect=false;
			    					break;
			    				}	
			    				empStr.append(",");
			    			}
			    			if(!isCorrect)
			    				break;
			    		
			    	}
				if(empStr.length()>0)
		    		   empStr.setLength(empStr.lastIndexOf(","));
		    		if(!isCorrect)
		    		{
		    			
		    			empStr.append("...");
		    		}
	     	}catch(Exception e)
	 	   {
	 		   e.printStackTrace();
	 	   }finally
	 	   {
	 		   try{		
	 			  if(rs!=null){
		 			   rs.close();
		 		   }
	 			  if (conn != null){
	    				conn.close();
	    			}
	    		    }catch (Exception ee){
	    			    ee.printStackTrace();
	    		    }
	 	    }
	    	
	    	return empStr.toString();
	    }
	public int getCountOrg(DynaBean dbean)
	{
		String strWid = (String)dbean.get(Key_HrpWarn_FieldName_ID);
		
		int iTotal = 0;
		StringBuffer sbPreCondition = new StringBuffer();	
		/*if(!this.userView.isSuper_admin())  机构预警走业务范围，不走人员范围，注掉此处  guodd 2015-04-16
		{
			if(this.userView.getManagePrivCode()==null||this.userView.getManagePrivCode().length()<=0)
			 if(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0)
				return 0;
		}*/
		
		//重新生成 范围sql  guodd 2015-04-16
		String busiPrivStr = this.userView.getUnitIdByBusi("4");
		if(!this.userView.isSuper_admin() && busiPrivStr.indexOf("UN`")==-1)
		{
			if(busiPrivStr.split("`").length==0)
				return 0;
		}
		
		StringBuilder privSql = new StringBuilder(" 1=2 ");
		if(!this.userView.isSuper_admin() && busiPrivStr.indexOf("UN`")==-1){
			String[] busiPrivs = busiPrivStr.split("`");
			for(int i=0;i<busiPrivs.length;i++){
				privSql.append(" or a0100 like '");
				privSql.append(busiPrivs[i].substring(2));
				privSql.append("%' ");
				//privSql.append("or a0100="); 
				//privSql.append(Sql_switcher.substr("'"+busiPrivs[i].substring(2)+"'","1",Sql_switcher.length("a0100")));
			}
		}else{
			privSql.append(" or 1=1 ");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new Date());  
		sbPreCondition.append("select count(a0100) as total from hrpwarn_result where wid='" +strWid+"'" );
		//sbPreCondition.append("and (a0100 like '"+this.userView.getManagePrivCodeValue()+"%'");
		//sbPreCondition.append("or a0100 ="+Sql_switcher.substr("'"+this.userView.getManagePrivCodeValue()+"'","1",Sql_switcher.length("a0100"))+")");
		// 加上新生成的 范围 sql
		sbPreCondition.append(" and (");
		sbPreCondition.append(privSql);
		sbPreCondition.append(" ) ");
		sbPreCondition.append(" and a0100 in (select codeitemid from organization where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)");
		ArrayList alTotal = TransTool.executeQuerySql(sbPreCondition.toString());
		if(alTotal!=null){
			for(int i=0;i<alTotal.size();i++)
				iTotal=iTotal+Integer.parseInt( (String)((DynaBean)alTotal.get(i)).get("total"));
		}
		return iTotal;
	}
	public boolean getHrpWarnValib(String strWid)
	{
	   StringBuffer sql=new StringBuffer();
	   sql.append("select valid from hrpwarn where ");
	   sql.append(" wid='"+strWid+"'");
	   Connection conn=null;
	   boolean isCorrect=false;
	   RowSet rs=null;
	   try
	   {
		  conn = AdminDb.getConnection();
		  ContentDAO dao=new ContentDAO(conn);		  
		  rs=dao.search(sql.toString());
		  if(rs.next())
		  {
			  String valib=rs.getString("valid");
			  if(valib!=null&& "1".equals(valib))
				  isCorrect=true;
		  }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally
	   {
		   try{		
			  if(rs!=null)
				  rs.close();
   			  if (conn != null){
   				conn.close();
   			}
   		    }catch (Exception ee){
   			    ee.printStackTrace();
   		    }
	   }
	   return isCorrect;
	}
	
}
