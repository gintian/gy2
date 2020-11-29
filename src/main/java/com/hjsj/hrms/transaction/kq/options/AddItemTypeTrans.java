package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.options.KqItem;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;

public class AddItemTypeTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            DynaBean codesetbean = (LazyDynaBean) this.getFormHM().get("codesetvo");
            String code = "";
            String name = "";
            String flag = "";
            String mkey = "";
            String mes = "";

            if (codesetbean != null) {
                code = (String) codesetbean.get("code");
                name = (String) codesetbean.get("name");
                flag = (String) codesetbean.get("flag");
                mkey = (String) codesetbean.get("codeitemid");
                mes = (String) codesetbean.get("mes");
            } else {
                code = (String) this.getFormHM().get("code");
                name = (String) this.getFormHM().get("name");
                flag = (String) this.getFormHM().get("flag");
                mkey = (String) this.getFormHM().get("codeitemid");
                mes = (String) this.getFormHM().get("mes");
            }

            StringBuffer sts = new StringBuffer();
            StringBuffer sbs = new StringBuffer();
            String cid = ""; //考勤号
            String cname = ""; //考勤名称
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            if ("1".equals(flag)) {
                if ("8".equals(mes)) {
                    KqItem kqItem = new KqItem(this.getFrameconn());

                    if (mkey == null || "".equals(mkey))
                        mkey = null;
                    else {
                        if (!kqItem.existKqItem("codeitemid='" + mkey + "'"))
                            mkey = "";
                    }
                    // mes是8是新增页面；否则是修改页面！
                    // 如果点的是考勤项目，即mkey值为空，那么增加一个父节点，否则继续加子节点
                    if (mkey == null || "".equals(mkey)) {
                        if (kqItem.existKqItem("parentid='" + code.toString() + "'"))
                            cid =  code.toString();
                        
                        //szk查询名称是否重复
                        if (kqItem.existKqItem("codeitemdesc='" + name + "'"))
                            cname = name;

                        if ((cid == null || cid.length() == 0) && (cname == null || cname.length() == 0)) {
                            sts.delete(0, sts.length());
                            sts.append("insert into codeitem (codesetid,codeitemid,codeitemdesc,parentid,childid,flag,invalid,end_date,start_date)values('27','");
                            sts.append(code);
                            sts.append("','");
                            sts.append(name);
                            sts.append("','");
                            sts.append(code);
                            sts.append("','");
                            sts.append(code);
                            sts.append("',0");
                            sts.append(",1," + Sql_switcher.dateValue("9999-12-31") + "");
                            sts.append("," + Sql_switcher.dateValue(OperateDate.dateToStr(new Date(), "yyyy-MM-dd")));
                            sts.append(")");
                            dao.insert(sts.toString(), new ArrayList());

                            this.getFormHM().put("sys", "0");
                            this.getFormHM().put("sys2", "0");
                            this.getFormHM().put("codesetid", code);
                            this.getFormHM().put("codesetdesc", name);
                            this.getFormHM().put("codeitemid", mkey);
                        } else {
                            this.getFormHM().put("sys", "2");
                            this.getFormHM().put("sys2", "2");
                        }
                    } else {
                        int diy = 0;
                        sbs.delete(0, sbs.length());
                        sbs.append("select max(displayorder) as dis from kq_item");
                        this.frowset = dao.search(sbs.toString());
                        if (this.frowset.next()) {
                            int cc = this.frowset.getInt("dis") + 1;
                            diy = cc;
                        }

                        // 先查找，看是否存在记录
                        if (kqItem.existKqItem("codeitemid='" + mkey + code + "'"))
                            cid =  mkey + code;
                        
                        //szk查询名称是否重复
                        if (kqItem.existKqItem("codeitemdesc='" + name + "'"))
                            cname = name;

                        if ((cid == null || cid.length() == 0) && (cname == null || cname.length() == 0)) {

                            String ff = null;

                            sbs.delete(0, sbs.length());
                            sbs.append("insert into kq_item (item_id,item_name,has_rest,has_feast,want_sum,c_expr,s_expr,item_color,displayorder)values('");
                            sbs.append(mkey + code + "','");
                            sbs.append(name + "','");
                            sbs.append("0','0','0',");
                            sbs.append(ff + ",");
                            sbs.append(ff + ",'");
                            sbs.append("1000000255','");
                            sbs.append(diy + "')");

                            dao.insert(sbs.toString(), new ArrayList());

                            sts.delete(0, sts.length());
                            sts.append("insert into codeitem (codesetid,codeitemid,codeitemdesc,parentid,childid,flag,invalid,end_date,start_date)values(27,'");
                            sts.append(mkey + code);
                            sts.append("','");
                            sts.append(name);
                            sts.append("','");
                            sts.append(mkey);
                            sts.append("','");
                            sts.append(mkey + code);
                            sts.append("',0");
                            sts.append(",1," + Sql_switcher.dateValue("9999-12-31") + "");
                            sts.append("," + Sql_switcher.dateValue(OperateDate.dateToStr(new Date(), "yyyy-MM-dd")));
                            sts.append(")");
                            dao.insert(sts.toString(), new ArrayList());

                            //检查父节点是否已有子节点
                            String childId = kqItem.getFirstChildItemId(mkey);

                            //如果没有子节点，那么更新父节点的child为本节点
                            if ("".equals(childId))
                                kqItem.updateChildIdForParent(mkey, mkey + code);

                            this.getFormHM().put("sys", "0");
                            this.getFormHM().put("sys2", "0");
                            this.getFormHM().put("codesetid", code);
                            this.getFormHM().put("codesetdesc", name);
                            this.getFormHM().put("codeitemid", mkey);
                        } else {

                            this.getFormHM().put("sys", "2");
                            this.getFormHM().put("sys2", "2");
                        }
                    }

                    if (mkey == null)
                        mkey = "";

                    kqItem.refreshTableStructUsedKqItemCodeSet((mkey + code).length());
                } else {
                    sts.delete(0, sts.length());
                    sts.append("update codeitem set codeitemdesc='");
                    sts.append(name);
                    sts.append("' where codesetid='27' and codeitemid='");
                    sts.append(code);
                    sts.append("'");
                    dao.update(sts.toString());
                    
                    sts.delete(0, sts.length());
                    sts.append("update kq_item set item_name='");
                    sts.append(name);
                    sts.append("' where item_id='");
                    sts.append(code);
                    sts.append("'");
                    dao.update(sts.toString());
                    
                    this.getFormHM().put("codesetid", code);
                    this.getFormHM().put("codesetdesc", name);
                }
            }
        } catch (Exception exx) {
            exx.printStackTrace();
            throw GeneralExceptionHandler.Handle(exx);
        }
        this.getFormHM().put("mes", "8");
        this.getFormHM().put("code", "");
        this.getFormHM().put("name", "");
    }

}
