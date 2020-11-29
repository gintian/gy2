/*
 * 创建日期 2005-9-9
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author luangaojiong
 *
 * 代码转换类
 */
public class DoCodeBean {

    public Hashtable hm = new Hashtable();

    public DoCodeBean() {
        StringBuffer strsql;
        ResultSet rset;
        Statement stmt;
        Connection conn;
        hm = new Hashtable();
        strsql = new StringBuffer();
        strsql.append("select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from codeitem");
        strsql.append(" union all ");
        strsql.append("select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from organization");
        strsql.append(" order by CodeSetID,CodeItemId");

        rset = null;
        stmt = null;
        conn = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
            conn = AdminDb.getConnection();
            stmt = conn.createStatement();
            dbS.open(conn, strsql.toString());
            rset = stmt.executeQuery(strsql.toString());
            do {
                if (!rset.next())
                    break;
                CodeItem item = new CodeItem();
                item.setCodeid(rset.getString("CodeSetID"));
                item.setCodeitem(rset.getString("CodeItemId"));
                item.setCodename(rset.getString("CodeItemDesc"));
                item.setPcodeitem(rset.getString("parentid"));
                item.setCcodeitem(rset.getString("childid"));
                String key = rset.getString("CodeSetID").trim() + rset.getString("CodeItemId").trim();
                if (!hm.containsKey(key))
                    hm.put(key, item);
            } while (true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	
        	try {
        		// 关闭Wallet
        		dbS.close(conn);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}

        	
        	
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 得到代码转成字符
     * @param codeid
     * @param codeitem
     * @return
     */
    public String getCodeName(String codeid, String codeitem) {
        CodeItem code = getCode(codeid, codeitem);
        if (code != null) {
            return code.getCodename();
        } else
            return "";
    }

    public CodeItem getCode(String codeid, String codeitem) {
        if (hm == null)
            return new CodeItem();
        
        if (hm.get(codeid + codeitem) == null) {
            return new CodeItem();
        }
        
        return (CodeItem) hm.get(codeid + codeitem);
    }

    /**
     * 
     *得到动态列函数
     */
    public ArrayList getDynamicList(Connection con) {
        ArrayList dynamicCol = new ArrayList();
        RelatingFactory relatingFactory = new RelatingFactory();
        relatingFactory.getInstance();

        StringBuffer sb = new StringBuffer();
        sb.append("select itemid,fieldsetid,codesetid,itemdesc,codeflag,itemtype,itemlength,decimalwidth from t_hr_busifield where fieldsetid='R19' and useflag='1'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(con);
        try {
        	rs=dao.search(sb.toString());
            String columnName = "";
            while (rs.next()) {
                columnName = PubFunc.nullToStr(rs.getString("itemid"));
                columnName = columnName.toLowerCase();
                if ("r1901".equals(columnName) || "r1906".equals(columnName) || "r1907".equals(columnName)
                        || "r1908".equals(columnName) || "r1909".equals(columnName) || "r1910".equals(columnName)
                        || "r1902".equals(columnName) || "b0110".equals(columnName) || "e0122".equals(columnName)) {

                } else {
                    BusifieldBean busb = BusifieldBean.InstanceFactory();
                    busb.setItemid(columnName.trim());
                    busb.setFieldsetid(PubFunc.nullToStr(rs.getString("fieldsetid")).trim());
                    String codeid = PubFunc.NullToZero(rs.getString("codesetid")).trim();
                    busb.setItemdesc(PubFunc.nullToStr(rs.getString("itemdesc")));
                    busb.setCodesetid(codeid);
                    String codeflag = PubFunc.nullToStr(rs.getString("codeflag")).trim();
                    busb.setCodeflag(codeflag);
                    busb.setItemtype(PubFunc.nullToStr(rs.getString("itemtype")));
                    busb.setItemlength(PubFunc.NullToZero(rs.getString("itemlength")));
                    busb.setDecimalwidth(PubFunc.NullToZero(rs.getString("decimalwidth")));
                    if ("1".equals(codeflag) && !"0".equals(codeid)) {
                        RelatingcodeBean rcb = relatingFactory.getDisplayField(busb);
                        busb.setRelTableName(rcb.getCodetable());
                        busb.setRelFieldId(rcb.getCodevalue());
                        busb.setRelFieldDesc(rcb.getCodedesc());
                    }
                    dynamicCol.add(busb);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return dynamicCol;
    }

    /**
     * 得到关联表的内容
     * @param con
     * @param flag
     * @return
     */

    public String getRelCodeName(RelatingcodeBean rcb, String value) {
        String temp = "";
        StringBuffer strsql;
        ResultSet rset;
        Statement stmt;
        Connection conn;

        strsql = new StringBuffer();
        strsql.append("select ");
        strsql.append(rcb.getCodedesc());
        strsql.append(" from ");
        strsql.append(rcb.getCodetable());
        strsql.append(" where ");
        strsql.append(rcb.codevalue);
        strsql.append("='");
        strsql.append(value);
        strsql.append("'");

        rset = null;
        stmt = null;
        conn = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
            conn = AdminDb.getConnection();
            stmt = conn.createStatement();
            dbS.open(conn, strsql.toString());
            rset = stmt.executeQuery(strsql.toString());
            if (rset.next()) {
                temp = PubFunc.nullToStr(rset.getString(rcb.getCodedesc()));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
		} finally {
			
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (rset != null)
					rset.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        return temp;
    }

    /**
    * 
    *得到动态列函数
    */
    public ArrayList getDynamicList(Connection con, int flag) {
        ArrayList dynamicCol = new ArrayList();
        RelatingFactory relatingFactory = new RelatingFactory();
        relatingFactory.getInstance();

        StringBuffer sb = new StringBuffer();
        sb.append("select itemid,fieldsetid,codesetid,itemdesc,codeflag,itemtype,itemlength,decimalwidth from t_hr_busifield where fieldsetid='R22' and useflag='1'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(con);
        try {
            rs=dao.search(sb.toString());
            String columnName = "";
            while (rs.next()) {
                columnName = PubFunc.nullToStr(rs.getString("itemid"));
                columnName = columnName.toLowerCase();
                if ("r2201".equals(columnName) || "r2202".equals(columnName) || "r2206".equals(columnName)
                        || "r2208".equals(columnName) || "r2209".equals(columnName)

                ) {

                } else {
                    BusifieldBean busb = BusifieldBean.InstanceFactory();
                    busb.setItemid(columnName.trim());
                    busb.setFieldsetid(PubFunc.nullToStr(rs.getString("fieldsetid")).trim());
                    String codeid = PubFunc.NullToZero(rs.getString("codesetid")).trim();
                    busb.setItemdesc(PubFunc.nullToStr(rs.getString("itemdesc")));
                    busb.setCodesetid(codeid);
                    String codeflag = PubFunc.nullToStr(rs.getString("codeflag")).trim();
                    busb.setCodeflag(codeflag);
                    busb.setItemtype(PubFunc.nullToStr(rs.getString("itemtype")).trim());
                    busb.setItemlength(PubFunc.NullToZero(rs.getString("itemlength")));
                    busb.setDecimalwidth(PubFunc.NullToZero(rs.getString("decimalwidth")));
                    if ("1".equals(codeflag) && !"0".equals(codeid)) {
                        RelatingcodeBean rcb = relatingFactory.getDisplayField(busb);
                        busb.setRelTableName(rcb.getCodetable());
                        busb.setRelFieldId(rcb.getCodevalue());
                        busb.setRelFieldDesc(rcb.getCodedesc());
                    }
                    dynamicCol.add(busb);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return dynamicCol;
    }

}
