package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hjsj.hrms.utils.components.codeselector.utils.CreatCodeData;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Description: 得到考勤模块权限下的组织机构树</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2018-4-17</p>
 * @author zhaoxj
 * @version 1.0
 */
public class GetKqOrgTree extends CodeDataFactory {

	@Override
	public ArrayList createCodeData(String codesetid, String code, UserView userView) {
	    ArrayList itemList = new ArrayList();
	    try {
	        //获取权限
            String codesstr = "";
            String privCode = RegisterInitInfoData.getKqPrivCode(userView);
            String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
            codesstr = privCode + privCodeValue;
            
            String initCode = code;
            if("root".equalsIgnoreCase(code))
                initCode = privCodeValue;
            
            CreatCodeData codeData = new CreatCodeData(codesetid, initCode);
            
            //超级用户，不用判断权限，直接输出代码
            if(userView.isSuper_admin())
                return codeData.outCodeData(true,false,"true",true,false,"");
            
            // 61996 兼容新考勤权限问题
            KqVer kqVer = new KqVer();
            boolean bool = (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL);
            String newPrivCodeValue = "";
            if(bool) {
            	newPrivCodeValue = KqPrivForHospitalUtil.getB0110(userView);
            	codesstr = newPrivCodeValue;
            	if("HJSJ".equalsIgnoreCase(newPrivCodeValue)) {
            		codesstr = "UN";
            	}
            }
            
            //如果权限有UN`说明有所有权限
            if(codesstr.indexOf("UN`")!=-1 || "UN".equals(codesstr)){//如果 不是刚进入  或者 是超级用户 
                return codeData.outCodeData(true,false,"false",true,false,"");
            }

            /** 走到这里 就代表是 第一次加载树 并且是机构代码，并且需要权限控制*/            
            if(codesstr.trim().length()<1)
                return null;
        
            String codefilter = "";
            if("UN".equals(codesetid))
                codefilter+=" and codesetid<>'UM' and codesetid<>'@K' ";
            else if("UM".equals(codesetid))
                codefilter+=" and codesetid<>'@K' ";
        
            StringBuffer sql = new StringBuffer();
            sql.append("select codesetid,codeitemid,codeitemdesc,(select count(1) from organization where parentid=A.codeitemid ");
            sql.append(codefilter);
            sql.append(") cnum from organization A");
            sql.append(" where ");
            if(!"root".equalsIgnoreCase(code)) {
                sql.append(" parentid ='"+ code +"'");
                sql.append(" and codeitemid<>parentid");
            } else {
            	if(bool) {
            		String[] privs = StringUtils.split(newPrivCodeValue, "`");
            		String privSql = "";
            		for(int i=0;i<privs.length;i++) {
            			privSql += "or codeitemid='" + privs[i] + "'";
            		}
            		sql.append("(" + privSql.substring(2) + ")"); 
            	}else {
            		sql.append(" codeitemid='" + privCodeValue + "'"); 
            	}
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String backdate =sdf.format(new Date());
            sql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
            sql.append(codefilter);
            sql.append(" ORDER BY a0000,codeitemid ");
            
            List codelist = ExecuteSQL.executeMyQuery(sql.toString());
            
            for(int k=0;k<codelist.size();k++){
                LazyDynaBean ldb = (LazyDynaBean)codelist.get(k);
                HashMap treeitem = new HashMap();
                String setid = ldb.get("codesetid").toString();
                treeitem.put("id",ldb.get("codeitemid"));
                treeitem.put("text", ldb.get("codeitemdesc"));
                treeitem.put("codesetid",setid);
                treeitem.put("itemdesc", ldb.get("codeitemdesc"));
                String layerdesc = "";
                
                treeitem.put("layerdesc", layerdesc);
                //设置图片
                if("UN".equals(setid))
                    treeitem.put("icon","/images/unit.gif");
                else if("UM".equals(setid))
                    treeitem.put("icon","/images/dept.gif");
                else
                    treeitem.put("icon","/images/pos_l.gif");
                
                //是否叶子节点
                if(Integer.parseInt(ldb.get("cnum").toString())>0)
                    treeitem.put("leaf", Boolean.FALSE);
                else
                    treeitem.put("leaf", Boolean.TRUE);
                treeitem.put("checked", false);
                itemList.add(treeitem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
        return itemList;
	}

	@Override
	public ArrayList searchCodeByText(String codesetid, String text, UserView userView) {
		
		return null;
	}
}
