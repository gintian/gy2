package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.SealTerm;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 封存校验数据
 * <p>Title:CheckSealDurationDataTrans.java</p>
 * <p>Description>:CheckSealDurationDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 7, 2010 4:55:36 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class CheckSealDurationDataTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
        String kq_duration = (String) this.getFormHM().get("kq_duration");
        if (kq_duration == null || kq_duration.length() <= 0) {
            kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
        }
        ArrayList datelist = RegisterDate.getKqDate(this.getFrameconn(), kq_duration);
        String start_date = datelist.get(0).toString();
        String end_date = datelist.get(datelist.size() - 1).toString();

        String pigeonhole_type = KqParam.getInstance().getArchiveType();
        if (pigeonhole_type != null && "1".equals(pigeonhole_type)) {
            this.getFormHM().put("notapp_list", "");
            this.getFormHM().put("notQ07_list", "");
            this.getFormHM().put("notQ09_list", "");
            this.getFormHM().put("notapptag", "seal");
            this.getFormHM().put("pigeonhole_type", "1");
        } else {
            SealTerm sealTerm = new SealTerm(this.getFrameconn(), this.userView);
            /**得到所有参加考勤的部门**/
            ArrayList baseE0122list = getAllE0122(start_date, end_date);
            /***得到所有没有没有审批通过的部门,里面是vo*****/
            ArrayList notapp_list = new ArrayList();
            this.getFormHM().put("notapptag", "noseal");
            if (notapp_list != null && notapp_list.size() > 0) {
                this.getFormHM().put("notapp_list", "have");
                return;
            } else {
                this.getFormHM().put("notapp_list", "");

                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.frameconn, this.userView);
                ArrayList kq_dbase_list = kqUtilsClass.setKqPerList("", "2");
                String setid = getPigeonholeFldSet();
                if (setid == null || "".equals(setid)) {
					throw new GeneralException(ResourceFactory.getProperty("kq.archive.destfieldset"));
				}
                String month_pigeonhole = RegisterDate.getKqMonth(this.getFrameconn(), kq_duration);
                ArrayList notPige_list = sealTerm.getNotPigeonhole(kq_dbase_list, kq_duration, setid, month_pigeonhole);
                if (notPige_list != null && notPige_list.size() > 0) {
                    this.getFormHM().put("notpige_list", "have");
                    return;
                } else {
                    this.getFormHM().put("notpige_list", "");
                }
            }
            /**得到所有没有日汇总的部门,*/
            ArrayList notQ07_list = sealTerm.getNotDailyCollect(baseE0122list, start_date, end_date);
            ArrayList notQ09_list = sealTerm.getNotSumCollect(baseE0122list, kq_duration);
            if (notQ07_list != null && notQ07_list.size() > 0) {
                this.getFormHM().put("notQ07_list", "have");
                this.getFormHM().put("notapptag", "noseal");
            } else {
                this.getFormHM().put("notQ07_list", "");
            }

            if (notQ09_list != null && notQ09_list.size() > 0) {
                this.getFormHM().put("notQ09_list", "have");
                this.getFormHM().put("notapptag", "noseal");
            } else {
                this.getFormHM().put("notQ09_list", "");
            }
            if (notapp_list == null || notapp_list.size() <= 0) {
                if (notQ07_list == null || notQ07_list.size() <= 0) {
                    if (notQ09_list == null || notQ09_list.size() <= 0) {
                        this.getFormHM().put("notapptag", "seal");
                    }
                }
            }
            this.getFormHM().put("pigeonhole_type", "0");
        }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getAllE0122(String start_date, String end_date) throws GeneralException {
        SealTerm sealTerm = new SealTerm(this.getFrameconn(), this.userView);
        ArrayList list = new ArrayList();
        for (int i = 0; i < userView.getPrivDbList().size(); i++) {
            String userbase = userView.getPrivDbList().get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);
            ArrayList b0100list = sealTerm.getAllBaseOrgid(userbase, "b0110", whereIN, this.getFrameconn(), start_date, end_date);
            for (int n = 0; n < b0100list.size(); n++) {
                String b0110_one = b0100list.get(n).toString();
                String nbase = RegisterInitInfoData.getOneB0110Dase(this.getFormHM(), this.userView, userbase, b0110_one, this
                        .getFrameconn());
                /** ******按照该单位的人员库的操作******** */
                if (nbase != null && nbase.length() > 0) {
                    ArrayList baseE0122list = sealTerm.getPrivBaseE0122(nbase, b0110_one, start_date, end_date, "");
                    for (int r = 0; baseE0122list.size() > r; r++) {
                        list.add(baseE0122list.get(r));
                    }
                }
            }
        }
        return list;
    }

    /**
     * 得到归档方案表里的数据,添加到BS归档方案临时表里面
     *
     */
    private String getPigeonholeFldSet() {
        String sql = "select id,bytes from kq_archive_schema where status=1";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String DestFldSet = "";
        try {
            this.frecset = dao.search(sql);
            if (this.frecset.next()) {

                this.getFormHM().put("bytesid", this.frecset.getString("id"));

                String xpath = "/ArchScheme/RelaSet";
                String xmlContent = Sql_switcher.readMemo(this.frecset, "bytes");
                if (xmlContent != null && xmlContent.length() > 0) {
                    Document doc = PubFunc.generateDom(xmlContent);
                    XPath reportPath = XPath.newInstance(xpath);
                    List setlist = reportPath.selectNodes(doc);
                    Iterator i = setlist.iterator();
                    if (i.hasNext()) {
                        Element childR = (Element) i.next();
                        childR.getAttributeValue("SrcFldSet");//原表表名
                        DestFldSet = childR.getAttributeValue("DestFldSet");//归档目标表名
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DestFldSet;
    }

}
