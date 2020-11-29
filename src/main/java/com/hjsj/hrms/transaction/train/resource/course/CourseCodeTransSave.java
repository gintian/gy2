package com.hjsj.hrms.transaction.train.resource.course;

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
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 新增课程或批准diy课程时，将课程名称同步到左侧分类树中
 */
public class CourseCodeTransSave extends IBusiness {

    public void execute() throws GeneralException {
        String id;
        String codeid = "";
        String codeitemdesc = "";
        String codeitemid = "";
        String flag = "";
        String state = (String) this.getFormHM().get("s");
        if (!"1".equals(state)) {// 新增培训课程时，同步到左侧分类树
            id = (String) this.getFormHM().get("id");
            id = PubFunc.decrypt(SafeCode.decode(id));
            codeid = (String) this.getFormHM().get("codeid");
            codeid = PubFunc.decrypt(SafeCode.decode(codeid));
            codeitemdesc = (String) this.getFormHM().get("codeitemdesc");
            this.getFormHM().remove("id");
            this.getFormHM().remove("codeid");
            this.getFormHM().remove("codeitemdesc");
            if (id == null || "".equals(id)) {
                codeitemid = saveAddRecord("55", codeid, codeitemdesc);
                flag = "add";
            } else {
                flag = "edit";
                try {
                    String sql = "select codeitemid from r50 where  R5000 =" + id + "";
                    ContentDAO dao = new ContentDAO(this.frameconn);
                    this.frowset = dao.search(sql);
                    if (this.frowset.next())
                        codeitemid = frowset.getString("codeitemid");
                    sql = "update codeitem set codeitemdesc = '" + codeitemdesc + "' where codesetid='55' and codeitemid='" + codeitemid + "'";
                    dao.update(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            this.getFormHM().put("flag", flag);
            this.getFormHM().put("itemid", SafeCode.encode(PubFunc.encrypt(codeitemid)));
            this.getFormHM().put("codeitemdesc", SafeCode.encode(codeitemdesc));
            this.getFormHM().put("cid", SafeCode.encode(PubFunc.encrypt(codeid)));
        } else {// 批准DIY课程时，同步到左侧分类树
            String sel = (String) this.getFormHM().get("sel");
            String[] sels = sel.split(",");
            RowSet rs = null;
            String codeitem = "";
            String selid = "";
            int n = 0;
            for (int i = 0; i < sels.length; i++) {
                if (n > 0)
                    selid += ",";
                selid += PubFunc.decrypt(SafeCode.decode(sels[i]));
                n++;

                if (n == 1000) {
                    String sqll = "select r5000,r5003,r5004 from r50 where r5000 in (" + selid + ")";
                    ContentDAO dao = new ContentDAO(this.frameconn);
                    try {
                        rs = dao.search(sqll);
                        while (rs.next()) {
                            id = rs.getString("r5000");
                            codeid = rs.getString("r5004");
                            codeitemdesc = rs.getString("r5003");
                            codeitemid = saveAddRecord("55", codeid, codeitemdesc);
                            codeitem += id + ":" + codeitemdesc + ":" + codeid + ":" + SafeCode.encode(PubFunc.encrypt(codeitemid)) + ",";
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    selid = "";
                    n = 0;
                }

            }

            if (selid.length() > 0) {
                String sqll = "select r5000,r5003,r5004 from r50 where r5000 in (" + selid + ")";
                ContentDAO dao = new ContentDAO(this.frameconn);
                try {
                    rs = dao.search(sqll);
                    while (rs.next()) {
                        id = rs.getString("r5000");
                        codeid = rs.getString("r5004");
                        codeitemdesc = rs.getString("r5003");
                        codeitemid = saveAddRecord("55", codeid, codeitemdesc);
                        codeid = StringUtils.isEmpty(codeid) ? "" : SafeCode.encode(PubFunc.encrypt(codeid));
                        codeitem += id + ":" + codeitemdesc + ":" + codeid + ":" + SafeCode.encode(PubFunc.encrypt(codeitemid)) + ",";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            codeitem = codeitem.substring(0, codeitem.length() - 1);
            this.getFormHM().put("codeitem", codeitem);
        }

    }

    private String saveAddRecord(String setid, String itemid, String codedesc) {
        itemid = itemid != null ? itemid.trim() : "";
        boolean updateChildidId = false;
        RowSet rs = null;
        String codeitemid = "";
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
            rs = dao.search(sqlstr.toString());
            if (rs.next()) {
                codeitemid = rs.getString("codeitemid");
                codeitemid = codeitemid != null ? codeitemid : "";
            }
            // 没有父节点和同级节点
            if (itemid.trim().length() < 1 && codeitemid.length() < 1) {
                codeitemid = "01";
                itemid = codeitemid;
            } else {
                // 没有父节点，有同级节点
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
            this.getFormHM().put("itemid", codeitemid);
            this.getFormHM().put("codeitemdesc", SafeCode.encode(codedesc));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return codeitemid;
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
}
