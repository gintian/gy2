package com.hjsj.hrms.businessobject.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.transaction.sys.warn.ScanTotal;
import com.hjsj.hrms.transaction.sys.warn.TransTool;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class WarnScanResult implements IConstant
{
    /**
     * 取全部预警结果
     * @param userView
     * @param conn
     * @return
     */
    public ArrayList getWarnScanResult(UserView userView, Connection conn)
    {
        return getWarnScanResultById(userView, conn, "");
    }

    /**
     * 取某个预警的结果，如果warnId为空，则取全部预警
     * @param userView
     * @param conn
     * @param warnId
     * @return
     */
    public ArrayList getWarnScanResultById(UserView userView, Connection conn, String warnId)
    {
        ArrayList alResult = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);        
        ArrayList orderlist = new ArrayList();
        HashMap resultMap = new HashMap();
        
        String strUserOrg = "UN" + userView.getUserOrgId();// getManagePrivCode();
        String privValue = userView.getManagePrivCode() + userView.getManagePrivCodeValue();
        ArrayList alUserRoles = userView.getRolelist();        
        
        ArrayList scanWidList = getScanWid(dao, warnId);
        for (int s = 0; s < scanWidList.size(); s++)
        {
            String strWid = (String) scanWidList.get(s);
            if (!getHrpWarnValib(strWid, dao)) {
                continue;
            }
            DynaBean dbean = getWarnScan(strWid, conn);
            ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
            String strDomain = ctrlVo.getStrDomain();// 预警对象
            String warntype = (String) dbean.get(Key_HrpWarn_FieldName_Warntyp);
             if ("1".equals(warntype) || "2".equals(warntype)){
            	ArrayList list = new ArrayList();
            	list.add("1");
            	return list;
            }
            boolean isShow = false;
            /*查看预警时，是否只显示自己  guodd 2019-07-24
             * 当预警为人员预警，且预警对象为角色，并设置了【本人特征】的角色
             * 如果当前用户没有预警设置的角色权限，默认走本人角色权限
             * 本人角色权限的特点是 如果预警结果中有自己，则只显示自己的数据；如果没有，则不显示此预警
             * 注意：本人角色 只有当前userview为自助用户 或 关联了自助的业务用户 时才适用
             * */
            boolean onlyShowSelf = false;
            if (userView.isSuper_admin())
            {
                isShow = true;
            }
            else if (strDomain == null || strDomain.trim().length() < 1)
            {
                isShow = true;
            }
            else if (strDomain.startsWith("RL"))
            {
            	//预警角色里是否有【本人角色】
				boolean hasSelfRole = false;
				//判断当前userview是否是自助用户 或 关联了自助用户
				boolean isEmpUser = userView.getStatus()==4 || userView.getA0100().length()>0;
                String[] roles = strDomain.split(",");
                for (int j = 0; j < roles.length; j++)
                {
                    if (alUserRoles.contains(roles[j].substring(2)))
                    {
                        isShow = true;
                        break;
                    }
                    
                    /*是自助（或关联自助的业务用户） ，且是人员预警，判断当前角色是不是【本人角色】。如果已经存在本人角色了（hasSelfRole=true），就不用判断了  guodd 2019-07-24*/
					if(isEmpUser && !hasSelfRole && "0".equals(warntype)) {
                        hasSelfRole =  ScanTotal.isSelfRole(roles[j].substring(2));
                    }
                }
                
                /*如果当前userview没有 预警角色 权限，但是预警角色中有本人角色，认为显示此预警  guodd 2019-07-24*/
				if(!isShow && hasSelfRole) {
					isShow = true;
					onlyShowSelf = true;
				}
            }
            else
            {
                String[] orgs = strDomain.split(",");
                String temp = null;
                for (int j = 0; j < orgs.length; j++)
                {
                    if ("UN".equals(orgs[j]))
                    {// 所有组织
                        isShow = true;
                        break;
                    }
                    else if (orgs[j].startsWith("UN"))
                    {// 分支组织
                        /*isShow = strUserOrg.startsWith(orgs[j]);
                        if (!isShow && privValue.indexOf("UN") != -1)
                            isShow = privValue.startsWith(orgs[j]);
                        break;*/
                    	
                    	//上级单位的
						isShow=orgs[j].startsWith(strUserOrg);
						if(!isShow&&privValue.indexOf("UN")!=-1) {
                            isShow=orgs[j].startsWith(privValue);
                        }
						//下面是子单位的
						if(!isShow&&strUserOrg.indexOf("UN")!=-1) {
                            isShow=strUserOrg.startsWith(orgs[j]);
                        }
						if(!isShow&&privValue.indexOf("UN")!=-1) {
                            isShow=privValue.startsWith(orgs[j]);
                        }
						if(isShow) {
                            break;
                        }
                    }
                    else if (orgs[j].startsWith("UM"))
                    {// 部门组织
                       /* temp = "UM" + userView.getUserDeptId();
                        isShow = temp.startsWith(orgs[j]);
                        if (!isShow && privValue.indexOf("UM") != -1)
                            isShow = privValue.startsWith(orgs[j]);
                        // isShow = (temp.indexOf( orgs[j].substring(2))>=0);
                        break;*/
                    	temp="UM"+userView.getUserDeptId();
						//上级部门的
						isShow=orgs[j].startsWith(temp);
						if(!isShow&&privValue.indexOf("UM")!=-1) {
                            isShow=orgs[j].startsWith(privValue);
                        }
						//子部门的
						if(!isShow) {
                            isShow=temp.startsWith(orgs[j]);
                        }
						if(!isShow&&(privValue.indexOf("UN")!=-1||privValue.indexOf("UM")!=-1)) {
                            isShow=privValue.substring(2).startsWith(orgs[j].substring(2));
                        }
						//isShow = (temp.indexOf( orgs[j].substring(2))>=0);
						if(isShow) {
                            break;
                        }

                    }
                    else if (orgs[j].startsWith("@K"))
                    {// 职务组织
                        temp = "@K" + userView.getUserPosId();
                        isShow = temp.startsWith(orgs[j]);
                        if(!isShow&&(privValue.indexOf("UN")!=-1||privValue.indexOf("UM")!=-1||privValue.indexOf("@K")!=-1)) {
                            isShow=privValue.substring(2).startsWith(orgs[j].substring(2));
                        }
                        // isShow = ( temp.indexOf(orgs[j].substring(2))>=0);
                        if(isShow) {
                            break;
                        }
                    }
                }
            }
            if (isShow)
            {
                if (orderlist.size() <= 0) {
                    orderlist.add(strWid);
                } else
                {
                    boolean isCorrect = false;
                    for (int i = 0; i < orderlist.size(); i++)
                    {
                        String o_wid = orderlist.get(i).toString();
                        if (Integer.parseInt(o_wid) > Integer.parseInt(strWid))
                        {
                            orderlist.add(i, strWid);
                            isCorrect = true;
                            break;
                        }
                    }
                    if (!isCorrect) {
                        orderlist.add(strWid);
                    }
                }
                if(onlyShowSelf) { //如果是只显示自己的数据时  guodd 2019-07-24
					//判断预警结果中是否包含当前userview用户，如果包含则显示，不包含就跳过
					if(ScanTotal.isHasSelf(userView,dbean)) {
						String strShow = (String)dbean.get(Key_HrpWarn_FieldName_Msg);
						CommonData firstEmptyData = new CommonData();
						firstEmptyData.setDataName(strShow);
						firstEmptyData.setDataValue(strWid);
						firstEmptyData.put("onlyShowSelf", true);
						resultMap.put(strWid, firstEmptyData);
					}
					
				}else if ("1".equals(ctrlVo.getIsComplex()))
                {// 复杂查询
                    int total = getCount(dbean, userView, conn);
                    if (total > 0)
                    {
                        // String strShow =
                        // "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
                        String strShow = dbean.get(Key_HrpWarn_FieldName_Msg) + " (计" + total + "人)";
                        // strShow=strShow.replace("\"","'");
                        strShow = strShow.replaceAll("\"", "'");
                        strShow = strShow.replaceAll("\n", "");
                        strShow = strShow.replaceAll("\r", "");
                        CommonData firstEmptyData = new CommonData();
                        firstEmptyData.setDataName(strShow);
                        firstEmptyData.setDataValue(strWid);
                        // alResult.add(firstEmptyData);//
                        // "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
                        resultMap.put(strWid, firstEmptyData);
                    }
                }
                else
                {// 简单查询
                    int total = getCount(dbean, userView, conn);
                    if (total > 0)
                    {
                        // String strShow =
                        // "现有"+total+"人"+dbean.get(Key_HrpWarn_FieldName_Msg);
                        String strShow = dbean.get(Key_HrpWarn_FieldName_Msg) + " (计" + total + "人)";
                        CommonData firstEmptyData = new CommonData();
                        // strShow=strShow.replace("\"","'");
                        strShow = strShow.replaceAll("\"", "'");
                        strShow = strShow.replaceAll("\n", "");
                        strShow = strShow.replaceAll("\r", "");
                        firstEmptyData.setDataName(strShow);
                        firstEmptyData.setDataValue(strWid);
                        resultMap.put(strWid, firstEmptyData);
                        // alResult.add(firstEmptyData);//
                        // "<font size=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+strWid+"\">"+strShow+"</a></font>");
                    }
                }
            }
        }
        for (int i = 0; i < orderlist.size(); i++)
        {
            String o_wid = orderlist.get(i).toString();
            CommonData firstEmptyData = (CommonData) resultMap.get(o_wid);
            if (firstEmptyData != null)
            {
                // System.out.println(firstEmptyData);
                alResult.add(firstEmptyData);
            }
        }
        return alResult;
    }

    private ArrayList getScanWid(ContentDAO dao, String warnId)
    {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try
        {
            String sql = "select Distinct(wid) wid  from hrpwarn_result";
            if (!"".equals(warnId))
            {
                sql = sql + " WHERE wid=" + warnId;
            }
           
            rs = dao.search(sql);
            while (rs.next())
            {
                list.add(rs.getString("wid"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public boolean getHrpWarnValib(String strWid, ContentDAO dao)
    {
        if (strWid == null || strWid.length() <= 0) {
            return false;
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select valid from hrpwarn where ");
        sql.append(" wid='" + strWid + "'");
        boolean isCorrect = false;
        RowSet rs = null;
        try
        {
            rs = dao.search(sql.toString());
            if (rs.next())
            {
                String valib = rs.getString("valid");
                if (valib != null && "1".equals(valib)) {
                    isCorrect = true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return isCorrect;
    }

    private DynaBean getWarnScan(String strID, Connection conn)
    {
        DynaBean dbean = new LazyDynaBean();
        WarnDomainTool tool = new WarnDomainTool(conn);
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;

        try
        {
            StringBuffer sql = new StringBuffer();
            sql.append("select ");
            for (int i = 0; i < Key_HrpWarn_Fields.length; i++)
            {
                sql.append(Key_HrpWarn_Fields[i] + ",");
            }
            sql.setLength(sql.length() - 1);
            sql.append(" from hrpwarn where wid=" + strID + "");
            rs = dao.search(sql.toString());
            if (rs.next())
            {
                for (int i = 0; i < Key_HrpWarn_Fields.length; i++)
                {
                    dbean.set(Key_HrpWarn_Fields[i], rs.getString(Key_HrpWarn_Fields[i]));
                }
            }

            // 解析XML结果存入recordVo的虚拟字段中
            ConfigCtrlInfoVO ctrlVo = new ConfigCtrlInfoVO(dbean.get(Key_HrpWarn_FieldName_CtrlInf).toString());

            dbean.set(Key_XmlResul_Freq, ctrlVo.getFreqShow());
            dbean.set(Key_Domain_Names, tool.getDomainNames(ctrlVo.getStrDomain()));
            dbean.set(Key_HrpWarn_Template, tool.getTemplate(ctrlVo.getStrTemplate(), dao));
            dbean.set(Key_HrpWarn_Nbase, tool.getNbases(ctrlVo.getStrNbase(), dao));
            dbean.set(Key_HrpWarn_Ctrl_VO, ctrlVo);
            dbean.set("Obj", ctrlVo.getStrDomain());

        }
        catch (Exception sqle)
        {
            sqle.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return dbean;
    }

    public int getCount(DynaBean dbean, UserView userView, Connection conn)
    {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        String strNbase = ctrlVo.getStrNbase();
        WarnDomainTool tool = new WarnDomainTool(conn);

        ArrayList warn_dblist = new ArrayList();
        ArrayList dblist = new ArrayList();
        try
        {

            ContentDAO dao = new ContentDAO(conn);
            ArrayList pre_list = tool.getNbaseList(ctrlVo.getStrNbase(), dao);
            ArrayList privdblist = userView.getPrivDbList();
            if (pre_list != null && pre_list.size() > 0)
            {
                for (int i = 0; i < pre_list.size(); i++)
                {
                    String nbase = (String) pre_list.get(i);
                    if (nbase == null || nbase.length() <= 0) {
                        continue;
                    }

                    for (int r = 0; r < privdblist.size(); r++)
                    {
                        if (privdblist.get(r) != null && nbase.equalsIgnoreCase(privdblist.get(r).toString())) {
                            dblist.add(nbase);
                        }
                    }
                }
            }
            else {
                dblist = privdblist;
            }
            warn_dblist = tool.getKqNbaseList(dblist, strNbase, dao);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // StringBuffer sbTemp = new StringBuffer();
        StringBuffer sbPreCondition = new StringBuffer();
        // StringBuffer str_pre=new StringBuffer();
        // sbTemp.append("select count(a0100) as total from hrpwarn_result where wid='"
        // +strWid+"'" );
        ArrayList sqllist = new ArrayList();
        // if(!userview.isSuper_admin()) {//如果不是管理员
        try
        {
            // ArrayList dblist=this.userView.getPrivDbList(); //
            // 管理员返回所有的库，一般操作员返回其权限库
            if (dblist.size() < 1)
            {
                return 0; // 权限库为空时，不应取得任何数据
            }
            else
            {
                if (warn_dblist != null && warn_dblist.size() > 0) {
                    dblist = warn_dblist;
                }
            }
            for (int i = 0; i < dblist.size(); i++)
            {
                String strPre = (String) dblist.get(i);
                sbPreCondition = new StringBuffer();
                sbPreCondition.append("select count(a0100) as total from hrpwarn_result where wid='" + strWid + "'");
                sbPreCondition.append(" and nbase='");
                sbPreCondition.append(strPre);
                sbPreCondition.append("' and (hrpwarn_result.a0100 in (select " + strPre + "a01.a0100 ");
                String strWhere = userView.getPrivSQLExpression("", strPre, false, true, new ArrayList());// fieldList);
                sbPreCondition.append(strWhere);
                sbPreCondition.append(")");
        		if(userView.getStatus()==4&&userView.haveTheRoleProperty("14")){//本人角色特征
        			sbPreCondition.append(" or hrpwarn_result.a0100='"+userView.getA0100()+"'");
        		}
                sbPreCondition.append(")");
                Category.getInstance(this.getClass()).debug(strWhere);
                /*
                 * if( i<dblist.size()-1){ sbPreCondition.append(" union "); }
                 */
                sqllist.add(sbPreCondition.toString());
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /*
         * } else { sbPreCondition.append(
         * "select count(a0100) as total from hrpwarn_result where wid='"
         * +strWid+"'"); sqllist.add(sbPreCondition.toString()); }
         */
        int iTotal = 0;
        for (int r = 0; r < sqllist.size(); r++)
        {
            String sql = sqllist.get(r).toString();
            ArrayList alTotal = TransTool.executeQuerySql(sql);
            if (alTotal != null)
            {
                for (int i = 0; i < alTotal.size(); i++) {
                    iTotal = iTotal + Integer.parseInt((String) ((DynaBean) alTotal.get(i)).get("total"));
                }
            }
        }

        return iTotal;
    }
}
