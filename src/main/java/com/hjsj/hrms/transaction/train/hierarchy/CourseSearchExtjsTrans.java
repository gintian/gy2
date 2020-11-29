package com.hjsj.hrms.transaction.train.hierarchy;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * @Title:        CourseSearchExtjsTrans.java
 * @Description:  培训自助浏览课程
 * @Company:      hjsj     
 * @Create time:  2015-7-11 下午04:45:51
 * @author        chenxg
 * @version       1.0
 */
public class CourseSearchExtjsTrans extends IBusiness {

    public void execute() throws GeneralException {
        String a_code = (String) this.getFormHM().get("a_code");
        try {
            if (a_code != null && a_code.length() > 0)
                a_code = PubFunc.decrypt(SafeCode.decode(a_code));
    
            if (a_code == null)
                a_code = (String) this.getFormHM().get("a_code1");
    
            int page = Integer.parseInt((String) this.getFormHM().get("page"));
            int limit = Integer.parseInt((String) this.getFormHM().get("limit"));
    
            a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
            this.getFormHM().put("a_code1", a_code);
            this.getFormHM().remove("a_code");
    
            if (this.userView.getA0100() == null || this.userView.getA0100().length() < 1)
                throw GeneralExceptionHandler.Handle(new GeneralException("", "非自助用户不能使用此功能！", "", ""));
    
            TrainCourseBo bo = new TrainCourseBo(this.userView, this.frameconn);
            StringBuffer strwhere = new StringBuffer();
            ArrayList dataList = new ArrayList();
            int totalCount = 0;

            strwhere.append("select r.r5000 r5000,r5003,r5009,r5012,r5016,r5039,r.imageurl,id,lesson_from from R50 r left join tr_selected_lesson t on t.r5000=r.r5000 and nbase='" + userView.getDbname()
                    + "' and a0100='" + userView.getA0100() + "' where (((r5016=1 ");
            if (!this.userView.isSuper_admin()) {

                String unit = bo.getUnitIdByBusi();
                String[] units = unit.split("`");
                String sql = " and (";
                if (units.length > 0 && unit.length() > 0) {
                    for (int i = 0; i < units.length; i++) {
                        String b0110s = units[i].substring(2);
                        sql += "r5020 like '";
                        sql += b0110s;
                        sql += "%'";
                        sql += " or ";
                    }
                }
                sql += Sql_switcher.isnull("r5020", "'-1'");
                sql += "='-1'";
                if (Sql_switcher.searchDbServer() == 1) {
                    sql += " or r5020=''";
                }
                sql += ")";
                strwhere.append(sql);
            }

            strwhere.append(")");
            String tmp = "";

            if (!this.userView.isSuper_admin())
                tmp = getWhereCode(a_code);

            strwhere.append(tmp);
            strwhere.append(")");

            if (!this.userView.isSuper_admin())
                strwhere.append(" or r5014=1");// 显示公开课

            strwhere.append(")");
            strwhere.append(" and r5022='04'");// 已发布

            if (a_code.trim().length() > 0)
                strwhere.append(" and (R5004 like '" + a_code + "%' or codeitemid='" + a_code + "')");

            Date date = new Date();
            SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
            String date1 = f1.format(date);
            strwhere.append(" and ((" + Sql_switcher.year("r5030") + "*10000+" + Sql_switcher.month("r5030") + "*100+" + Sql_switcher.day("r5030") + "<=" + date1);
            strwhere.append(" and " + Sql_switcher.year("r5031") + "*10000+" + Sql_switcher.month("r5031") + "*100+" + Sql_switcher.day("r5031") + ">=" + date1 + ")");
            strwhere.append(" or (r5030 is null and " + Sql_switcher.year("r5031") + "*10000+" + Sql_switcher.month("r5031") + "*100+" + Sql_switcher.day("r5031") + ">=" + date1 + ")");
            strwhere.append(" or (" + Sql_switcher.year("r5030") + "*10000+" + Sql_switcher.month("r5030") + "*100+" + Sql_switcher.day("r5030") + "<=" + date1 + " and r5031 is null)");
            strwhere.append(" or (r5030 is null and r5031 is null))");

            String searchWhere = (String) this.getFormHM().get("search");
            searchWhere = SafeCode.decode(searchWhere);
            searchWhere = PubFunc.nullToStr(searchWhere);

            if (searchWhere != null && searchWhere.trim().length() > 0) {
                strwhere.append(" and (r.r5003 like '%" + searchWhere + "%'");
                strwhere.append(" or r.r5012 like '%" + searchWhere + "%'");
                strwhere.append(" or r.r5039 like '%" + searchWhere + "%')");
            }

            String top = getTop();
            HashMap ls = this.hotLesson(top); //热门课程

            String[] fields = { "r5000", "r5003", "r5009", "r5012", "r5016", "r5039", "imageurl", "lesson_from", "id" };
            PaginationManager paginationm = null;
            paginationm = new PaginationManager(strwhere.toString(), "", "", " order by r.r5000", fields, "");
            paginationm.setBAllMemo(true);
            paginationm.setPagerows(limit);
            totalCount = paginationm.getMaxrows();
            dataList = (ArrayList) paginationm.getPage(page);
            if (dataList.isEmpty() && page != 1) {
                dataList = (ArrayList) paginationm.getPage(page - 1);
            }
            
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean data = (LazyDynaBean) dataList.get(i);
                String r5000 = (String) data.get("r5000");
                data.set("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));

                String r5004 = (String) data.get("r5004");
                data.set("r5004", SafeCode.encode(PubFunc.encrypt(r5004)));

                String id = (String) data.get("id");
                data.set("id", SafeCode.encode(PubFunc.encrypt(id)));

                String imageurl = (String) data.get("imageurl");
                if (imageurl != null && imageurl.trim().length() > 0) {
                    imageurl = "/servlet/vfsservlet?fileid=" + imageurl;
                } else
                    imageurl = "/images/course.png";

                data.set("imageurl", imageurl);

                if (ls.containsKey(r5000))
                    data.set("hot", 1);
                else
                    data.set("hot", 0);

                int count = searchcount(r5000);
                data.set("count", count);
                
                FieldItem r5039 = DataDictionary.getFieldItem("r5039", "r50");
                String r5039Name = r5039.getItemdesc();
                data.set("r5039Name", r5039Name);
                
                String r5012 = (String) data.get("r5012");
                String lessondesc = sublesdesc(r5012);
                data.set("lessondesc", lessondesc);
            }

            this.getFormHM().put("data", dataList);
            this.getFormHM().put("totalCount", totalCount);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 得到热门课程参数
     * 
     * @return
     */
    private String getTop() {
        ConstantXml constantbo = new ConstantXml(this.frameconn, "TR_PARAM");
        String top = constantbo.getNodeAttributeValue("/param/hot_course", "top");
        return top;
    }

    /**
     * 查找热门课程
     * 
     * @param top
     *            热门课程参数
     * @return
     */
    private HashMap hotLesson(String top) {
        HashMap ls = new HashMap();
        String id = "";
        String sql = "";
        if (IsNum(top)) {
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL:
                sql = "select top "
                        + top
                        + " count(*) as 'counts',r5000 from tr_selected_lesson where exists ( select r5000 from r50 where r50.r5000=tr_selected_lesson.r5000 and r5014 = '1' and r5016 = '1' and r5022 = '04') and lesson_from = 1 group by r5000 order by counts desc";
                break;
            case Constant.ORACEL:
                sql = "select  * from (select  count(*),r5000 from tr_selected_lesson where exists ( select r5000 from r50 where r50.r5000=tr_selected_lesson.r5000 and r5014 = '1' and r5016 = '1' and r5022 = '04') and lesson_from = 1 group by r5000 order by count(*) desc)  c where rownum<="
                        + top;
                break;
            }

            ContentDAO dao = new ContentDAO(this.frameconn);
            try {
                this.frowset = dao.search(sql);
                while (this.frowset.next()) {
                    id = this.frowset.getString("r5000");
                    ls.put(id, "1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ls;
    }

    /**
     * 获取权限内的课程分类
     * 
     * @param a_code
     * @return
     */
    private String getWhereCode(String a_code) {
        String tmpCodes = "";
        TrainCourseBo tbo = new TrainCourseBo(this.userView);
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select codeitemid,b0110 from codeitem where codesetid='55'");

        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            this.frowset = dao.search(sqlstr.toString());
            while (this.frowset.next()) {
                String b0110 = this.frowset.getString("b0110");
                if (tbo.isUserParent(b0110) != -1) {
                    tmpCodes += this.frowset.getString("codeitemid") + ",";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (tmpCodes != null && tmpCodes.length() > 0) {
            tmpCodes = tmpCodes.substring(0, tmpCodes.length() - 1);
            if (a_code.trim().length() > 0)
                tmpCodes = " and r5004 in ('" + tmpCodes.replaceAll(",", "','") + "')";
            else
                tmpCodes = " and (r5004 in ('" + tmpCodes.replaceAll(",", "','") + "') or " + Sql_switcher.isnull("r5004", "'-1'") + "='-1')";
        } else if (a_code.trim().length() > 0) {
            if (a_code.trim().length() > 0)
                tmpCodes = " and " + Sql_switcher.isnull("r5004", "'-1'") + "<>'-1'";
        } else {
            tmpCodes = " and " + Sql_switcher.isnull("r5004", "'-1'") + "='-1'";
        }
        return tmpCodes;
    }

    /**
     * 判断热门课程参数是否为大于0的整数
     * 
     * @param s
     * @return
     */
    private boolean IsNum(String s) {
        int num = 0;
        try {
            num = Integer.parseInt(s);
            if (num > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 获取课程的学习人数
     * 
     * @param r5000
     *            课程编号
     * @return
     */
    private int searchcount(String r5000) {
        int count = 0;
        try {
            String sql = "select count(1) count from tr_selected_lesson where r5000=" + r5000;
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                count = this.frowset.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    /**
     * 截取过长的课程简介
     * @param lesdesc
     * @return
     */
    private String sublesdesc(String lesdesc) {
        if(lesdesc == null || lesdesc.length() < 1)
            return "";
        
        String lesdes = "";
        int i = 0;
        while(lesdesc.indexOf("<br>") != -1){
            lesdes += lesdesc.substring(0, lesdesc.indexOf("<br>") + 4);
            
            lesdesc = lesdesc.substring(lesdesc.indexOf("<br>") + 4);
            i++;
        }
        
        if(lesdesc != null && lesdesc.length() > 0)
            lesdes += lesdesc;
        if(lesdes.endsWith("<br>"))
            lesdes = lesdes.substring(0, lesdes.length() - 4);
            
        if(i > 2)
            lesdes = lesdes.replaceAll("<br>", " ");
        
        lesdes = lesdes.replaceAll("&nbsp;", " ");

        if(lesdes.length() > 200)
            lesdes = lesdes.subSequence(0, 200) + "...";
        
        return lesdes;
    }

}
