package com.hjsj.hrms.transaction.mobileapp.myteam;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.mobileapp.utils.HTMLParamUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * <p> Title: MyTeamBo </p>
 * <p> Description: 我的团队BO类 </p>
 * <p> Company: hjsj </p>
 * <p> create time 2013-12-26 下午5:10:21 </p>
 *
 * @author yangj
 * @version 1.0
 */
public class MyTeamBo {
    private Connection conn;
    private UserView userView;

    public MyTeamBo() {

    }

    public MyTeamBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     *
     * @Title: searchInfoList
     * @Description:获取人员list
     * @param unitID 组织机构ID
     * @param keywords  便捷查询
     * @param url       网络地址
     * @param pageIndex 第几页
     * @param pageSize  每页显示条数
     * @return List
     * @throws GeneralException
     */
    public List searchInfoList(String unitID, String keywords, String url,
            String pageIndex, String pageSize) throws GeneralException {
        List personList = null;
        String sql = this.getSQL(unitID, keywords, pageIndex, pageSize);
        personList = this.getPersonList(sql, url);
        return personList;
    }

    /**
     *
     * @Title: getSQL
     * @Description: 获取查询SQL语句
     * @param unitID    组织机构ID
     * @param keywords  便捷查询
     * @param pageIndex 第几页
     * @param pageSize  每页显示条数
     * @return String
     * @throws GeneralException
     */
    private String getSQL(String unitID, String keywords, String pageIndex,
            String pageSize) throws GeneralException {
        int index = Integer.parseInt(pageIndex);
        int size = Integer.parseInt(pageSize);
        StringBuffer resultSql = new StringBuffer();
        try {
            List list;
            list = this.getNbaseList();
            if (list.size() == 0)
                throw new GeneralException("没有人员库权限！");
            String dbpre;
            StringBuffer unitIDSql = new StringBuffer();
            // 组织机构树判断
            String codeset = "";
            String codevalue = "";
            // 如果没有传入组织机构，则走管理范围
            if (unitID.length() > 0) {
                String[] temporary = unitID.split("`");
                codeset = temporary[0];
                codevalue = temporary[1];
            } else {
                codeset = userView.getManagePrivCode();
                codevalue = userView.getManagePrivCodeValue();
            }
            // UN单位名称b0110 UM部门e0122 @K岗位名称（职位）e01a1、
            if (codevalue.length() > 0) {
                if ("UN".equals(codeset) || "un".equals(codeset))
                    unitIDSql.append(" b0110 like '" + codevalue + "%'");
                else if ("UM".equals(codeset) || "um".equals(codeset))
                    unitIDSql.append(" e0122 like '" + codevalue + "%'");
            } else if (codeset.length() == 0) {
                // unitIDSql.append(" 1 = 2");
                throw new GeneralException("没有管理范围权限！");
            } else {
                unitIDSql.append(" 1 = 1");
            }

            //zxj 20160512  jazz18604 我的团队也要走高级授权
            String privWhr = userView.getPrivSQLExpression("###",false,true);

            // 快速查询判断
            StringBuffer keywordsSql = new StringBuffer();
            if (keywords.length() > 0)
                keywordsSql.append(this.getKeywordsWhereStr(keywords));

            // 循环组合SQL语句
            for (int i = 0, length = list.size(); i < length; i++) {
                dbpre = (String) list.get(i);
                resultSql.append(" union all ");
                resultSql.append("select distinct " + dbpre + "a01.a0100,'" + (i + 1) + "' ord,'" + dbpre + "' dbpre," + dbpre
                        + "a01.b0110," + dbpre + "a01.e01a1," + dbpre + "a01.e0122,a0101,a0000 ");
                //resultSql.append(" from " + dbpre + "A01");
                resultSql.append(privWhr.replace("###", dbpre));
                resultSql.append(" AND ");
                resultSql.append(unitIDSql);
                resultSql.append(keywordsSql);
            }
            dbpre = resultSql.toString().substring(11);
            resultSql.setLength(0);
            resultSql.append("select * from (select ROW_NUMBER() over(ORDER BY ord, A0000) numberCode, A.* from (");
            resultSql.append(dbpre + ") A");
            resultSql.append(") T where numberCode between " + ((index - 1) * size + 1) + " and " + (size * index));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return resultSql.toString();
    }

    /**
     *
     * @Title: getNbaseList
     * @Description:根据管理范围和系统设置的范围查询人员库，去交集
     * @return List
     * @throws GeneralException
     */
    private List getNbaseList() throws GeneralException {
        ArrayList dbpres;
        try {
            // 系统设置的人员库
            RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
            String logindbpre = "";
            if (login_vo != null)
                logindbpre = login_vo.getString("str_value").toLowerCase();
            // 管理范围
            dbpres = userView.getPrivDbList();
            // 取交集
            for (int i = 0; i < dbpres.size(); i++) {
                String pre = (String) dbpres.get(i);
                if (logindbpre.toUpperCase().indexOf(pre.toUpperCase()) == -1) {
                    dbpres.remove(i);
                    --i;
                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
        return dbpres;
    }

    /**
     *
     * @Title: getWhereStrByBaseInfo
     * @Description: 获得基本信息（姓名，拼音简码，工号）条件语句
     * @param keywords 便捷查询
     * @return String
     * @throws GeneralException
     */
    private String getKeywordsWhereStr(String keywords) throws GeneralException {
        StringBuffer where = new StringBuffer();
        try {
            String keyword[] = keywords.split("\n");
            if(keyword.length<2){
                keyword = keywords.split(" ");
            }
            int flag = 0;
            where.append(" and (  ");// 姓名
            for (int i = 0; i < keyword.length; i++) {
                if ("".equals(keyword[i].trim())) {
                    flag++;
                    continue;
                }
                if (i == flag) {
                    where.append(" a0101 like '%" + keyword[i] + "%' ");
                } else {
                    where.append(" or a0101 like '%" + keyword[i] + "%' ");
                }
            }
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            FieldItem item = DataDictionary.getFieldItem(onlyname);
            if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim()))
                        continue;
                    where.append(" or " + onlyname + " like '%" + keyword[i] + "%' ");
                }
            }
            String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
            item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
            if (!(pinyin_field == null || "".equals(pinyin_field)
                    || "#".equals(pinyin_field) || item == null || "0".equals(item.getUseflag()))
                    && !"a0101".equalsIgnoreCase(pinyin_field)
                    && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim()))
                        continue;
                    where.append(" or " + pinyin_field + " like '%" + keyword[i] + "%' ");
                }
            }
            if(keyword.length>1){
                where.append(" or a0101 like '%" + keywords + "%' ");
                where.append(" or " + onlyname + " like '%" + keywords + "%' ");
                where.append(" or " + pinyin_field + " like '%" + keywords + "%' ");
            }
            where.append(")");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return where.toString();
    }

    /**
     *
     * @Title: getPersonList
     * @Description: 根据sql语句得到人员List
     * @param sql
     * @param url
     * @return List
     * @throws GeneralException
     */
    private List getPersonList(String sql, String url) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        RowSet rs = null;
        HashMap map = null;
        try {
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
            String seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
            seprartor = seprartor != null && seprartor.length() > 0 ? seprartor : "/";
            rs = dao.search(sql.toString());
            while (rs.next()) {
                map = new HashMap();
                String a0100 = rs.getString("a0100");
                String dbpre = rs.getString("dbpre");
                map.put("dbpre", dbpre);
                map.put("a0100", a0100);
                map.put("b0110", rs.getString("b0110"));
                map.put("name", rs.getString("a0101"));
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String e01a1 = rs.getString("e01a1");
                b0110 = AdminCode.getCodeName("UN", b0110);
                b0110 = b0110 == null ? "" : b0110.trim();
                CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
                if (itemid != null)
                    e0122 = itemid.getCodename();
                e0122 = e0122 == null ? "" : e0122.trim();
                e01a1 = AdminCode.getCodeName("@K", e01a1);
                e01a1 = e01a1 == null ? "" : e01a1.trim();
                String org = b0110 + (b0110.length() > 0 && e0122.length() > 0 ? seprartor : "")
                        + e0122 + (e01a1.length() > 0 && e0122.length() > 0 ? seprartor : "") + e01a1;
                map.put("b0110_name", b0110);
                map.put("e0122_name", e0122);
                map.put("e01a1_name", e01a1);
                map.put("org",org);
                // 获取设置的人员描述
                String info = this.getInfo(dbpre, a0100);
                info = info.replaceAll("\r", "").replaceAll("\n", "").replace("\r\n", "").trim();
                map.put("info", info);
                // 照片地址
                StringBuffer photourl = new StringBuffer();
                String filename = getPicUrl( dbpre, rs.getString("a0100"));
                if (!"".equals(filename)) {
                    photourl.append(url);
                    photourl.append(filename);
                } else {
                    photourl.append(url);
                    photourl.append("/images/photo.jpg");
                }
                map.put("photo", photourl.toString());
                list.add(map);
            }
        } catch (SQLException e) {
            throw GeneralExceptionHandler.Handle(e);
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    /**
     * 获取人员信息简介
     *
     * @param dbpre
     * @param a0100
     * @return
     * @throws GeneralException
     */
    private String getInfo(String dbpre, String a0100) throws GeneralException {
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            Map map = HTMLParamUtils.getBasicinfo_Map(conn);
            if (map == null)
                return "";
            String basicinfo_template = (String) map.get("basicinfo_template");
            Map mapsets = (Map) map.get("mapsets");
            Map mapsetstr = (Map) map.get("mapsetstr");
            for (Iterator i = mapsets.keySet().iterator(); i.hasNext();) {
                String setid = (String) i.next();
                List itemids = (List) mapsets.get(setid);
                String itemidstr = ((StringBuffer) mapsetstr.get(setid)).substring(1);
                StringBuffer sql = new StringBuffer();
                sql.append("select " + itemidstr + " from " + dbpre + setid + " where a0100='" + a0100 + "'");
                if (!"A01".equals(setid))
                    sql.append(" and i9999=(select max(i9999) from " + dbpre + setid + " where a0100='" + a0100 + "')");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    for (int n = 0; n < itemids.size(); n++) {
                        String itemid = (String) itemids.get(n);
                        FieldItem fielditem = DataDictionary.getFieldItem(itemid);
                        String itemtype = fielditem.getItemtype();
                        String value = "";
                        if ("N".equals(itemtype)) {
                        	if(fielditem.getDecimalwidth()>0)
                        		value = String.valueOf(rs.getObject(itemid));
                        	else
                        		value = String.valueOf(rs.getInt(itemid));
                        } else if ("D".equals(itemtype)) {
                            Object obj = rs.getDate(itemid);
                            value = String.valueOf(obj == null ? "" : obj);
                            value = value.replace('-', '.');
                        } else if ("A".equals(itemtype)) {
                            String codesetid = fielditem.getCodesetid();
                            value = rs.getString(itemid);
                            value = value == null ? "" : value;
                            if (!(codesetid.length() == 0 || "0".equals(codesetid))) {
                                value = AdminCode.getCodeName(codesetid, value);
                            }
                        }
                        basicinfo_template = basicinfo_template.replace("[" + itemid + "]", value);
                    }
                } else {
                    for (int n = 0; n < itemids.size(); n++) {
                        String itemid = (String) itemids.get(n);
                        basicinfo_template = basicinfo_template.replace("[" + itemid + "]", "");
                    }
                }
            }
            return basicinfo_template;
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

    }

    /**
     * xus 判断内存中是否存在图片，如果存在在内存中获取 否则在库中获取
     * @param dbpre
     * @param A0100
     * @return
     */
    private String getPicUrl(String dbpre,String A0100){
    	String url="";
        String filename="";
    	StringBuffer photourl=new StringBuffer();
    	PhotoImgBo pib = new PhotoImgBo(conn);
    	pib.setIdPhoto(true);
    	String absPath = "";
    	boolean genPhotoSuccess=false;
		try{
			absPath = pib.getPhotoRootDir();
		}catch(Exception ex){
		}
    	if(absPath != null && absPath.length() > 0){
    		try {
				absPath += pib.getPhotoRelativeDir(dbpre, A0100);
				String guid = pib.getGuid();
				//获取 文件名为 “photo.xxx”的文件，格式未知
				String fileWName = pib.getPersonImageWholeName(absPath, "photo");
				// 如果不存在文件，创建文件
				if (fileWName.length() < 1) {
					fileWName = pib.createPersonPhoto(absPath, conn, dbpre,
							A0100, "photo");
				}
				//如果有图片或创建了图片，使用新图片
				if (fileWName.length() > 0) {
					absPath += fileWName;

                    filename = pib.getPhotoPath(dbpre, A0100);
					this.userView.getHm().put(guid, absPath);

					// 只要能走到这里，表示照片成功产生了
					genPhotoSuccess = true;
				}
    		}catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	if(!genPhotoSuccess){
    		// 如果不存在文件，创建文件
				try {
                    filename = pib.getPhotoPath(dbpre, A0100);
				} catch (Exception e) {
					e.printStackTrace();
				}
    	}
    	return filename;
    }
}
