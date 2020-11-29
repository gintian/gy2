package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;

public class SaveTimeTrans extends IBusiness {

    public void execute() throws GeneralException {
        String a0100 = (String) this.userView.getA0100();
        String r5100 = (String) this.getFormHM().get("R5100");
        r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
        String nbase = (String) this.userView.getDbname();
        // 课件类型，3为视频文件，1为普通课件，
        String type = (String) this.getFormHM().get("type");
        String flag = "false";
        StringBuffer buff = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.frameconn);
        if ("1".equals(type) || "4".equals(type)) {
            // 更新选课表的状态和进度
            buff.delete(0, buff.length());
            buff.append("update tr_selected_course set ");
            buff.append("lprogress=100");
            buff.append(",state=2 where a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("' and r5100=");
            buff.append(r5100);
            try {
                flag = dao.update(buff.toString()) + "";
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.getFormHM().put("id", SafeCode.encode(PubFunc.encrypt(r5100)));
            this.getFormHM().put("nbase", SafeCode.encode(PubFunc.encrypt(nbase)));
            this.getFormHM().put("a0100", SafeCode.encode(PubFunc.encrypt(a0100)));
        } else {
            String total = (String) this.getFormHM().get("total");
            total = total == null || total.length() < 1 || "undefined".equalsIgnoreCase(total) ? "0" : total;
            String msg = (String) this.getFormHM().get("msg");
            try {
                buff.delete(0, buff.length());
                // 更新课件的时长(单位分钟)
                int len = (int) (Float.parseFloat(total) / 60);
                if (Float.parseFloat(total) % 60 > 0)
                    len = len + 1;
                buff.append("update r51 set r5117=");
                buff.append(len);
                buff.append(" where r5100=");
                buff.append(r5100);
                dao.update(buff.toString());
                // 陈旭光：为视频课件添加点击学习完毕链接时更改课件信息
                if ("1".equals(msg)) {// 点击视频课件的学习完毕链接
                    int r5117 = 0;
                    this.frowset = dao.search("select r5117 from r51 where r5100=" + r5100);
                    if (this.frowset.next()) {
                        r5117 = this.frowset.getInt("r5117");
                        if (r5117 <= 0) {
                            this.getFormHM().put("flag", "false");
                            return;
                        }
                    }

                    buff.delete(0, buff.length());
                    buff.append("update tr_selected_course set Learnedhour=");
                    buff.append(PubFunc.round(r5117 + "", 0));
                    buff.append(",lprogress=100");
                    buff.append(",state=2");
                    buff.append(" where a0100='");
                    buff.append(a0100);
                    buff.append("' and nbase='");
                    buff.append(nbase);
                    buff.append("' and r5100=");
                    buff.append(r5100);

                    flag = dao.update(buff.toString()) + "";
                    this.getFormHM().put("id", SafeCode.encode(PubFunc.encrypt(r5100)));
                    this.getFormHM().put("nbase", SafeCode.encode(PubFunc.encrypt(nbase)));
                    this.getFormHM().put("a0100", SafeCode.encode(PubFunc.encrypt(a0100)));
                } else {
                    String learned = (String) this.getFormHM().get("learned");
                    if (StringUtils.isEmpty(learned) || "0".equals(learned))
                        return;

                    buff.delete(0, buff.length());
                    float f = Float.parseFloat(learned) / 60;
                    float m = Float.parseFloat(learned);

                    buff.append("select Learnedhour from  tr_selected_course where a0100='");
                    buff.append(a0100);
                    buff.append("' and nbase='");
                    buff.append(nbase);
                    buff.append("' and r5100=");
                    buff.append(r5100);

                    this.frowset = dao.search(buff.toString());
                    if (this.frowset.next()) {
                        int fen = this.frowset.getInt("Learnedhour");
                        if (fen > f) {
                            return;
                        }
                    }

                    int r5117 = 0;
                    this.frowset = dao.search("select r5117 from r51 where r5100=" + r5100);
                    if (this.frowset.next()) {
                        r5117 = this.frowset.getInt("r5117");
                        if (r5117 <= 0) {
                            return;
                        }
                    }
                    int n = (int) Float.parseFloat(total);
                    //学习总时长与视频总时长误差在10秒内，默认是全部看完
                    if((n - m) < 10)
                        m=n;
                    
                    int lprogress = (int) (m / n * 100);
                    if (100 < lprogress)
                        lprogress = 100;

                    buff.delete(0, buff.length());
                    buff.append("update tr_selected_course set Learnedhour=");
                    buff.append(PubFunc.round(f + "", 0));
                    buff.append(",lprogress=");
                    buff.append(lprogress);
                    buff.append(",state=1");
                    buff.append(" where a0100='");
                    buff.append(a0100);
                    buff.append("' and nbase='");
                    buff.append(nbase);
                    buff.append("' and r5100=");
                    buff.append(r5100);

                    dao.update(buff.toString());
                }

                buff.delete(0, buff.length());
                buff.append("update tr_selected_lesson set Learnedhour=(select sum(Learnedhour) from tr_selected_course where r5100 in(select r5100 from r51 where r5000 in(select r5000 from r51 where r5100='");
                buff.append(r5100);
                buff.append("')) and a0100='");
                buff.append(a0100);
                buff.append("' and nbase='");
                buff.append(nbase);
                buff.append("'),state=1 where r5000 in (select r5000 from r51 where r5100='");
                buff.append(r5100);
                buff.append("')");
                buff.append(" and a0100='");
                buff.append(a0100);
                buff.append("' and nbase='");
                buff.append(nbase);
                buff.append("'");
                flag = dao.update(buff.toString()) + "";


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // 查询学员学习时可见的课件个数
            buff.delete(0, buff.length());
            buff.append("select count(*) a from tr_selected_course where id = ");
            buff.append("(select id from tr_selected_lesson where r5000 in (select r5000 from r51 where nbase='" + this.userView.getDbname());
            buff.append("' and a0100='" + this.userView.getA0100());
            buff.append("' and r5100=");
            buff.append(r5100);
            buff.append("))");

            int count = 1;
            this.frowset = dao.search(buff.toString());
            if (this.frowset.next()) {
                count = this.frowset.getInt("a");
                count = count == 0 ? 1 : count;
            }

            buff.delete(0, buff.length());
            buff.append("update tr_selected_lesson set lprogress=(select sum(lprogress)/");
            buff.append(count);
            buff.append(" from tr_selected_course where r5100 in (select r5100 from r51 where r5000=(select r5000 from r51 where r5100=");

            buff.append(r5100);
            buff.append("))");
            buff.append(" and a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("'");
            buff.append(") where r5000 in (select r5000 from r51 where r5100='");
            buff.append(r5100);
            buff.append("') and a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("'");

            flag = dao.update(buff.toString()) + "";
            if(flag == null || "0".equalsIgnoreCase(flag))
                flag = "false";
            
            this.getFormHM().put("flag", flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
