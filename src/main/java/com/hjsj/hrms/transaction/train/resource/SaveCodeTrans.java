package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainAddBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveCodeTrans extends IBusiness {

    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        String codeitemid = (String) this.getFormHM().get("itemid");
        codeitemid = codeitemid != null && codeitemid.trim().length() > 0 ? codeitemid : "";
        codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
        String codesetid = (String) this.getFormHM().get("setid");
        codesetid = codesetid != null && codesetid.trim().length() > 0 ? codesetid : "";
        codesetid = PubFunc.decrypt(SafeCode.decode(codesetid));

        String codedesc = (String) this.getFormHM().get("codeitemdesc");
        codedesc = codedesc != null && codedesc.trim().length() > 0 ? codedesc : "";
        codedesc = SafeCode.decode(codedesc);

        String flag = (String) this.getFormHM().get("flag");
        flag = flag != null && flag.trim().length() > 0 ? flag : "";

        String check = "no";
        if ("add".equalsIgnoreCase(flag)) {
            if (!chDesc(flag, codesetid, codeitemid, codedesc)) {
                saveAddRecord(codesetid, codeitemid, codedesc);
                check = "yes";
            }
        } else if ("update".equalsIgnoreCase(flag)) {
            if (!chDesc(flag, codesetid, codeitemid, codedesc)) {
                saveUpdateRecord(codesetid, codeitemid, codedesc);
                check = "yes";
            }
        } else if ("delete".equalsIgnoreCase(flag)) {
            check = savedeleteRecord(codesetid, codeitemid);

            // 如果是diy分类时，删除diy分类参数
            ConstantXml constant = new ConstantXml(this.frameconn, "TR_PARAM");
            String diyType = constant.getNodeAttributeValue("/param/diy_course", "codeitemid");
            if (codeitemid.equals(diyType)) {
                constant.setAttributeValue("/param/diy_course", "codeitemid", "");
                constant.saveStrValue();
            }

        }
        this.getFormHM().put("check", check);
    }

    private void saveAddRecord(String setid, String itemid, String codedesc) {
        boolean updateChildidId = false;
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.frameconn);
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select max(codeitemid) as codeitemid from ");
        sqlstr.append(" codeitem where codesetid='");
        sqlstr.append(setid);
        sqlstr.append("' and ");
        if (itemid.trim().length() < 1) {
            sqlstr.append("parentid=codeitemid");
        } else {
            sqlstr.append("parentid='");
            sqlstr.append(itemid);
            sqlstr.append("' and parentid<>codeitemid");
        }
        try {
            TrainCourseBo cbo = new TrainCourseBo(this.frameconn);
            String codeitemid = "";
            rs = dao.search(sqlstr.toString());
            if (rs.next()) {
                codeitemid = rs.getString("codeitemid");
                codeitemid = codeitemid != null ? codeitemid : "";
            }
            if (itemid.trim().length() < 1 && codeitemid.trim().length() < 1) {
                codeitemid = "01";
                itemid = codeitemid;
            } else {
                if (itemid.trim().length() < 1) {
                    itemid = cbo.getcodeitemid(codeitemid, itemid);
                    codeitemid = itemid;
                } else {
                    if (codeitemid.trim().length() < 1) {
                        codeitemid = itemid + "01";
                        updateChildidId = true;
                    } else {
                        codeitemid = itemid + cbo.getcodeitemid(codeitemid, itemid);
                    }
                }

            }

            ArrayList keylist = new ArrayList();
            keylist.add("codesetid");
            keylist.add("codeitemid");
            RecordVo vo = new RecordVo("codeitem");
            vo.setString("codesetid", setid);
            vo.setString("codeitemid", codeitemid);
            vo.setKeylist(keylist);
            vo.setString("codeitemdesc", codedesc);
            vo.setString("parentid", itemid);
            vo.setString("childid", codeitemid);
            vo.setString("invalid", "1");
            if (codeitemid.equalsIgnoreCase(itemid))
                vo.setInt("layer", 1);
            else
                vo.setInt("layer", layer(setid, itemid) + 1);

            /* newAdd a0000,b0110 */
            TrainCourseBo tbo = new TrainCourseBo(this.userView);
            vo.setInt("a0000", getMaxA0000(itemid, vo.getInt("layer"), setid) + 1);
            vo.setString("b0110", tbo.getUnitIdByBusi());
            vo.setDate("start_date", DateUtils.getDate("1949-10-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
            vo.setDate("end_date", DateUtils.getDate("9999-12-31 00:00:00", "yyyy-MM-dd HH:mm:ss"));
            dao.addValueObject(vo);

            CodeItem ci = new CodeItem();
            ci.setCodeid(setid);
            ci.setCodeitem(codeitemid);
            ci.setCodename(codedesc);
            AdminCode.addCodeItem(ci);
            if (updateChildidId) {// 修改
                vo = new RecordVo("codeitem");
                vo.setString("codesetid", setid);
                vo.setString("codeitemid", itemid);
                vo.setKeylist(keylist);
                vo.setString("childid", codeitemid);
                dao.updateValueObject(vo);
            }

            TrainAddBo bo = new TrainAddBo(this.frameconn);
            bo.upCodeitemLength(setid, codeitemid);
            this.getFormHM().put("itemid", SafeCode.encode(PubFunc.encrypt(codeitemid)));
            this.getFormHM().put("codedesc", SafeCode.encode(codedesc));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUpdateRecord(String setid, String itemid, String codedesc) {
        ContentDAO dao = new ContentDAO(this.frameconn);
        RecordVo vo = new RecordVo("codeitem");
        vo.setString("codesetid", setid);
        vo.setString("codeitemid", itemid);
        try {
            vo = dao.findByPrimaryKey(vo);
            vo.setString("codeitemdesc", codedesc);
            dao.updateValueObject(vo);
            AdminCode.updateCodeItemDesc(setid, itemid, codedesc);
            this.getFormHM().put("itemid", itemid);
            this.getFormHM().put("codedesc", SafeCode.encode(codedesc));
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String savedeleteRecord(String setid, String itemid) {
        String check = "yes";
        check = checkRecord(setid, itemid);
        if (check == null || check.length() > 3)
            return check;
        ContentDAO dao = new ContentDAO(this.frameconn);
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("delete from ");
        sqlstr.append(" codeitem where codesetid='");
        sqlstr.append(setid);
        sqlstr.append("' and codeitemid like '");
        sqlstr.append(itemid);
        sqlstr.append("%'");
        try {
            dao.update(sqlstr.toString());
            AdminCode.removeCodeItem(new CodeItem(setid, itemid));

            if (itemid != null && itemid.length() > 2) {// 修改childid
                sqlstr.setLength(0);
                sqlstr.append("select codeitemid from codeitem where codesetid='" + setid + "'");
                sqlstr.append(" and parentid='" + itemid.substring(0, itemid.length() - 2) + "'");
                sqlstr.append(" and codeitemid<>parentid");
                this.frowset = dao.search(sqlstr.toString());
                if (!this.frowset.next()) {
                    sqlstr.setLength(0);
                    sqlstr.append("update codeitem set childid=codeitemid where codesetid='" + setid + "'");
                    sqlstr.append(" and codeitemid='" + itemid.substring(0, itemid.length() - 2) + "'");
                    dao.update(sqlstr.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            check = "no";
        }
        return check;
    }

    private int layer(String codesetid, String parentid) {
        int layer = 1;
        ContentDAO dao = new ContentDAO(this.frameconn);
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select layer from ");
        sqlstr.append(" codeitem where codesetid='");
        sqlstr.append(codesetid);
        sqlstr.append("' and codeitemid='");
        sqlstr.append(parentid);
        sqlstr.append("'");
        try {
            RowSet rs = dao.search(sqlstr.toString());
            if (rs.next())
                layer = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return layer;
    }

    private boolean chDesc(String flag, String setid, String itemid, String codedesc) {
        boolean ch = false;
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select codeitemid from codeitem where codesetid='");
        sqlstr.append(setid);
        sqlstr.append("' and codeitemdesc='");
        sqlstr.append(codedesc);
        sqlstr.append("'");
        if ("update".equalsIgnoreCase(flag)) {
            if (itemid.trim().length() >= 2) {
                sqlstr.append(" and codeitemid<>'");
                sqlstr.append(itemid);
                sqlstr.append("'");
                sqlstr.append(" and parentid=(select parentid from codeitem where codesetid='");
                sqlstr.append(setid + "' and codeitemid='" + itemid + "')");
            } else {
                sqlstr.append(" and parentid=codeitemid");
            }
        } else {
            if (itemid.trim().length() < 1) {
                sqlstr.append(" and parentid=codeitemid");
            } else {
                sqlstr.append(" and parentid='");
                sqlstr.append(itemid);
                sqlstr.append("' and parentid<>codeitemid");
            }
        }
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            RowSet rs = dao.search(sqlstr.toString());
            if (rs.next())
                ch = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ch;
    }

    private String checkRecord(String setid, String itemid) {
        String tempString = "yes";
        if (itemid == null || itemid.length() < 1)
            return tempString;
        String table = "", field = "", temp = "";
        if ("54".equals(setid)) {
            table = "r07";
            field = "r0700";
            temp = "资料";
        } else if ("55".equals(setid)) {
            table = "r50";
            field = "r5004";
            temp = "课程";
        } else if ("69".equals(setid)) {
            table = "r52";
            field = "r5201";
            temp = "试题";
        }
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select count(1) n from ");
        sqlstr.append(table);
        sqlstr.append(" where ");
        sqlstr.append(field);
        sqlstr.append(" like '" + itemid + "%'");
        if ("55".equals(setid)) {
            sqlstr.append(" or codeitemid='" + itemid + "'");
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sqlstr.toString());
            if (this.frowset.next() && this.frowset.getInt("n") > 0) {
                String title = "";
                this.frowset = dao.search("select codeitemdesc from codeitem  where codesetid='" + setid + "' and codeitemid ='" + itemid + "'");
                if (this.frowset.next())
                    title = this.frowset.getString("codeitemdesc");
                tempString = "“" + title + "”下已创建了" + temp;
                if ("55".equals(setid)) {
                    tempString += ",或“" + title + "”是一门课程";
                }
                tempString += ",不允许删除！";
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            tempString = "no";
        }
        return tempString;
    }

    private int getMaxA0000(String itemid, int layer, String setId) {
        int tmpInt = 0;
        ContentDAO dao = new ContentDAO(this.frameconn);
        String sql = "";
        if (layer == 1)
            sql = "select max(a0000) a from codeitem where codesetid='" + setId + "' and codeitemid=parentid";
        else
            sql = "select max(a0000) a from codeitem where codesetid='" + setId + "' and codeitemid<>parentid and parentid='" + itemid + "'";
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next())
                tmpInt = this.frowset.getInt("a");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmpInt;
    }
}
