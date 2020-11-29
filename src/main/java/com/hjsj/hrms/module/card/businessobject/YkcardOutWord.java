package com.hjsj.hrms.module.card.businessobject;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.RowSet;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.aspose.words.DocumentBuilder;
import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.asposeword.AnalysisWordUtil;
import com.hjsj.hrms.module.utils.asposeword.AsposeLicenseUtil;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResetFontSizeUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import com.ibm.icu.text.SimpleDateFormat;

public class YkcardOutWord {
    private UserView userView;//
    private Connection conn;//
    private int cyyear;//日期查询 年（年）
    private int cymonth;//月（年）
    private int cmyear;//年（月份）
    private int cmmonth;//月（月份）
    private int cseason;//季度
    private int csyear;//年（季度）
    private int ctimes;//次数
    private String cdataStart;//开始时间
    private int marginleft = 0;//页边距设置过大时导出页面过于偏左 ，单元格rleft整体向左偏移量
    private int marginTop = 0;//模板设置元素存储数据库位置信息为负时或者不居中时 页面所有元素整体向下偏移量
    private String display_zero = "";//打印0
    private String officeOrWps = "0";

    public String getOfficeOrWps() {
        return officeOrWps;
    }

    public void setOfficeOrWps(String officeOrWps) {
        this.officeOrWps = officeOrWps;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    private String cdataEnd;//结束时间
    private String infokind;//  /*1人员登记表2单位登记表4职位登记表5计划登记表*/
    private int tabid;//表号
    private String nid;//人员编号
    private int querType;//日期查询类型  1月 2时间段 3季度 4年
    private String userbase;//库标识
    private String userpriv;/*用户权限 selfInfo ... */
    private String havepriv; /*是否有权限*/
    private String fieldpurv;
    private String plan_id;    //活动计划编号
    private int PixelInInch = 96;
    private String fenlei_type = "";//分类设置
    private String exportName = "";
    private HashMap fileNameMap = new HashMap();//存储文件名

    public HashMap getFileNameMap() {
        return fileNameMap;
    }

    public void setFileNameMap(HashMap fileNameMap) {
        this.fileNameMap = fileNameMap;
    }

    private boolean autoSize = false;//单元格字体自适应
    private String filePath = "";//指定导出文件路径
    public static final int FILETYPE_PDF = 1;//导出pdf

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getIsZipFile() {
        return isZipFile;
    }

    public void setIsZipFile(int isZipFile) {
        this.isZipFile = isZipFile;
    }

    private int isZipFile = 0;//0 生成压缩文件  1 不生成压缩文件

    public boolean isAutoSize() {
        return autoSize;
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    /**
     * 我的薪酬 员工薪酬 登记表按照日期查询数据 日期参数设置
     */
    public void setQueryTypeTime(String cyyear, String cymonth, String cmyear, String cmmonth, String cseason, String csyear, String ctimes, String cdataStart, String cdataEnd) {
        SimpleDateFormat sbf = new SimpleDateFormat("yyyy-MM-dd");//不传日期时 给系统默认日期
        if (StringUtils.isEmpty(cyyear))
            cyyear = Calendar.getInstance().get(Calendar.YEAR) + "";
        if (StringUtils.isEmpty(cymonth))
            cymonth = Calendar.getInstance().get(Calendar.MONTH) + "";
        if (StringUtils.isEmpty(cmyear))
            cmyear = Calendar.getInstance().get(Calendar.YEAR) + "";
        if (StringUtils.isEmpty(cmmonth))
            cmmonth = Calendar.getInstance().get(Calendar.MONTH) + "";
        if (StringUtils.isEmpty(cseason))
            cseason = "1";
        if (StringUtils.isEmpty(csyear))
            csyear = Calendar.getInstance().get(Calendar.YEAR) + "";
        if (StringUtils.isEmpty(ctimes))
            ctimes = "1";
        if (StringUtils.isEmpty(cdataStart))
            cdataStart = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.DATE);

        if (StringUtils.isEmpty(cdataEnd))
            cdataEnd = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.DATE);

        this.cyyear = Integer.parseInt(cyyear);
        this.cymonth = Integer.parseInt(cymonth);
        this.cmmonth = Integer.parseInt(cmmonth);
        this.cmyear = Integer.parseInt(cmyear);
        this.cseason = Integer.parseInt(cseason);
        this.csyear = Integer.parseInt(csyear);
        this.ctimes = Integer.parseInt(ctimes);
        this.cdataStart = cdataStart;
        this.cdataEnd = cdataEnd;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public YkcardOutWord(UserView userView, Connection conn) {
        try {
            DocumentBuilder builder = new AsposeLicenseUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.userView = userView;
        this.conn = conn;
        this.fileNameMap.clear();

    }


    public String outWordYkcard(int tabid, String nid, String queryType, String infokind,
                                String dbName, String userpriv, String havepriv, String fieldpurv) throws Exception {
        this.fieldpurv = fieldpurv;
        this.tabid = tabid;
        this.querType = Integer.parseInt(queryType);
        this.infokind = infokind;
        this.userpriv = userpriv;
        this.havepriv = "";
        this.userbase = dbName;
        getCardPageSet();
        searchExportName(tabid);
        if (this.tabid == -1)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "没有选择登记表,请选择!", "", ""));
        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
        if (this.infokind != null && "5".equals(this.infokind) && this.plan_id != null && this.plan_id.length() > 0) {
            StatisticPlan statisticPlan = new StatisticPlan(this.userView, conn);
            alUsedFields = statisticPlan.khResultField(alUsedFields, this.plan_id);
        }
        String prefix = "";
        if(StringUtils.isEmpty(getExportName(nid, dbName))){
			prefix = this.userView.getUserName();
		}else{
			prefix = getExportName(nid, dbName);
		}
        String excel_filename = prefix + "_" + this.exportName;
        String url = "";
        ArrayList outputList = new ArrayList();//存储登记表 标题 表格 位置参数等信息
        try {
            /***
             * 页边距 打印方向等页面参数设置信息
             * */
            float[] wh = getPringSetUp();
            HashMap titleMap = new HashMap();
            HashMap contextMap = new HashMap();
            HashMap gridCNmuMap = new HashMap();
            List<DynaBean> pagelist = getPagecount(tabid, this.conn);
            //查询允许打印的页签
            DataEncapsulation encap = new DataEncapsulation();
            encap.setUserview(this.userView);
            for (DynaBean pagebean : pagelist) {
                int pageId = Integer.parseInt((String) pagebean.get("pageid"));

                float[] rgridArea = YkcardStaticBo.getRGridArea(conn, tabid + "", pageId + "");// 0最左端开始位置 1最上方开始位置 2表格的宽 3表格的高 表格内容区
                float gridMinTop = rgridArea[4];//上标题位置区域 小于等于gridMinTop
                float gridMaxTop = rgridArea[5];//下标题 位置区域 大于等于gridMaxTop

                //水平方向 设置模板内容居中  垂直方向最小的rtop为负时或者小于38时 默认top为38
                marginleft = ((int) wh[0] - ((int) rgridArea[2] + (int) rgridArea[0])) / 2;//默认模板 居中
                if (marginleft < 38) {//lmargin=10 时页面最小居左边距为38，
                    marginleft = 38;
                }
                if (rgridArea[1] <= 38) {
                    //marginTop=((int)wh[1]-((int)rgridArea[3]+(int)rgridArea[1]))/2;
                    marginTop = 38;
                }


                //标题行内容
                //上标题
                ArrayList topTitleList = this.getRPageList(tabid, pageId, "t", gridMinTop, gridMaxTop, encap);
                //标题在单元格内
                ArrayList contextTitlList = this.getRPageList(tabid, pageId, "c", gridMinTop, gridMaxTop, encap);

                //下标题
                ArrayList botmTitleList = this.getRPageList(tabid, pageId, "b", gridMinTop, gridMaxTop, encap);

                //图片在表格区域内
                ArrayList midtitleList = this.getRPageList(tabid, pageId, "p", gridMinTop, gridMaxTop, encap);

                titleMap.put(pageId + "t", topTitleList);
                titleMap.put(pageId + "c", contextTitlList);
                titleMap.put(pageId + "b", botmTitleList);
                titleMap.put(pageId + "p", midtitleList);
                gridCNmuMap.put(pageId, getRgridRCNum(tabid, pageId, gridMinTop, gridMaxTop));//表格行数与列数

                List<RGridView> rgrids = encap.getRgrid(tabid, pageId, conn);
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopMapList = encap.getGridTopIndexList();//初始化记录单元格 rtop+rheight位置信息
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> lastGridTopList = encap.getLastGridTopList();

                for (RGridView rgrid : rgrids) {
                    dealGridBorder(rgrid, pageId + "", gridTopMapList, lastGridTopList);
                    rgrid.setRleft((Float.parseFloat(rgrid.getRleft()) + marginleft) + "");
                    if (StringUtils.isNotEmpty(rgrid.getRleft_0())) {
                        rgrid.setRleft_0((Integer.parseInt(rgrid.getRleft_0()) + marginleft) + "");
                    } else {
                        rgrid.setRleft_0(marginleft + "");
                    }

                    rgrid.setRtop((Float.parseFloat(rgrid.getRtop()) + marginTop) + "");
                    if (StringUtils.isNotEmpty(rgrid.getRtop_0()))
                        rgrid.setRtop_0((Integer.parseInt(rgrid.getRtop_0()) + marginTop) + "");
                    else
                        rgrid.setRtop_0(marginTop + "");
                }

                List setList = encap.GetSets(tabid, pageId, conn);
                contextMap.put(pageId + "rgrids", rgrids);
                contextMap.put(pageId + "setList", setList);
            }


            for (DynaBean pagebean : pagelist) {
                int pageId = Integer.parseInt((String) pagebean.get("pageid"));

                LazyDynaBean pgmap_new = new LazyDynaBean();
                ArrayList topTitleList = (ArrayList) titleMap.get(pageId + "t");
                if (topTitleList != null && topTitleList.size() > 0) {
                    pgmap_new.set("rtops_t", topTitleList.get(1));
                    pgmap_new.set("toptitle", topTitleList.get(0));
                } else {
                    pgmap_new.set("rtops_t", new ArrayList());
                    pgmap_new.set("toptitle", new ArrayList());
                }
                ArrayList botmTitleList = (ArrayList) titleMap.get(pageId + "b");
                if (botmTitleList != null && botmTitleList.size() > 0) {
                    pgmap_new.set("rtops_b", botmTitleList.get(1));
                    pgmap_new.set("bomtitle", botmTitleList.get(0));
                } else {
                    pgmap_new.set("rtops_b", new ArrayList());
                    pgmap_new.set("bomtitle", new ArrayList());
                }
                pgmap_new.set("wh", wh);
                pgmap_new.set("paperOrientation", "0");
                /**
                 * 查询表格内容 rgrid
                 * */
                ArrayList<LazyDynaBean> contextList = getRgridCell(tabid, pageId, contextMap, alUsedFields, (ArrayList) titleMap.get(pageId + "c"), nid);

                pgmap_new.set("context", contextList);
                ArrayList gridRCList = (ArrayList) gridCNmuMap.get(pageId);
                pgmap_new.set("rtops", gridRCList.get(0));//行 数  内容
                pgmap_new.set("rlefts", gridRCList.get(1));//列 数  内容
                ArrayList midtitleList = (ArrayList) titleMap.get(pageId + "p");
                pgmap_new.set("midtitle_img", midtitleList);
                outputList.add(pgmap_new);
            }
            AnalysisWordUtil awu = new AnalysisWordUtil();
            awu.setUsePageMarginSet(true);
            awu.setOfficerOrWps(this.getOfficeOrWps());
            url = awu.analysisWord(excel_filename, outputList, pagelist.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    /***
     * 处理单元格边框 防止单元格重线
     * 导出word子集控件有修改，子集单元格有四边线，先处理子集单元格上方与下方的单元格边线置虚
     * */
    private void dealGridBorder(RGridView rgrid, String pageId,
                                HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopMapList,
                                HashMap<String, HashMap<Double, ArrayList<RGridView>>> lastGridTopList
    ) {
        HashMap<Double, ArrayList<RGridView>> gridTopMap = gridTopMapList.get(pageId);
        HashMap<Double, ArrayList<RGridView>> lastGridMap = lastGridTopList.get(pageId);
        //当前单元格下发单元格坐标
        double nextRowTop = Double.parseDouble(rgrid.getRtop()) + Double.parseDouble(rgrid.getRheight());

        ArrayList<RGridView> nextRowList = gridTopMap.get(nextRowTop);
        if (nextRowList != null && !"1".equals(rgrid.getSubflag()))
            for (RGridView rGridView : nextRowList) {
                if ("1".equals(rGridView.getSubflag()) && Float.parseFloat(rgrid.getRleft()) <= Float.parseFloat(rGridView.getRleft()) && (Float.parseFloat(rgrid.getRleft()) + Float.parseFloat(rgrid.getRwidth()) > Float.parseFloat(rGridView.getRleft()))) {
                    rgrid.setB("0");
                }
            }
        ArrayList<RGridView> lastRowMap = lastGridMap.get(Double.parseDouble(rgrid.getRtop()));
        if (lastRowMap != null && !"1".equals(rgrid.getSubflag()))
            for (RGridView rGridView : lastRowMap) {
                if ("1".equals(rGridView.getSubflag()) && Float.parseFloat(rgrid.getRleft()) <= Float.parseFloat(rGridView.getRleft()) && (Float.parseFloat(rgrid.getRleft()) + Float.parseFloat(rgrid.getRwidth()) > Float.parseFloat(rGridView.getRleft()))) {
                    rgrid.setT("0");
                }
            }


    }


    /***
     * 导出单个pdf文件
     * */
    public String outPdfYkcard(int tabid, String nid, String queryType, String infokind,
                               String dbName, String userpriv, String havepriv, String fieldpurv) {
        String filename = "";
        try {
            String filePath = "";
            filename = outWordYkcard(tabid, nid, queryType, infokind, dbName, userpriv, havepriv, fieldpurv);
            filePath = System.getProperty("java.io.tmpdir") + File.separator + filename;
            filename = wordToPdf(filePath, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    public String wordToPdf(String filePath, String url) {
        try {
            com.aspose.words.Document doc = new com.aspose.words.Document(filePath);
            int lastindex = url.lastIndexOf(".");
            url = url.substring(0, lastindex) + ".pdf";
            doc.save(System.getProperty("java.io.tmpdir") + File.separator + url);
            //清除生成的word(tomcat临时文件中)
            File docfile = new File(filePath);
            if (docfile.exists())
                docfile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * @param fileNamePath 文件路径+文件名
     * @return
     * @throws Exception
     */
    public String wordToPdf(String fileNamePath) throws Exception {
        com.aspose.words.Document doc = new com.aspose.words.Document(fileNamePath);
        int lastindex = fileNamePath.lastIndexOf(".");
        String filename = fileNamePath.substring(0, lastindex) + ".pdf";
        doc.save(filename);
        File docfile = new File(fileNamePath);
        if (docfile.exists())
            docfile.delete();
        File file = new File(filename);
        return file.getName();
    }

    /***
     * 批量导出单个人间 -招聘
     * @param cardMap id:nid
     * @param queryType
     * @param infokind
     * @param dbName
     * @param userpriv
     * @param havepriv
     * @param fieldpurv
     * @return
     * @throws Exception
     */
    public String outWordYkcard(HashMap<String, ArrayList<String>> cardMap, String queryType, String infokind,
                                String dbName, String userpriv, String havepriv, String fieldpurv, int type) throws Exception {
        String url = "";
        if (StringUtils.isEmpty(this.filePath)) {
            this.filePath = System.getProperty("java.io.tmpdir") + File.separator + this.userView.getUserName() + "_tempCard";
        }
        //临时文件夹内创建批量导出pdf文件夹 su_tempCard
        File file = new File(this.filePath);
        if (file.exists() && file.isDirectory()) {
            deleteDirOrFile(this.filePath);
        }
        file.mkdir();
        for (String cardid : cardMap.keySet()) {
            if ("#".equals(cardid)) {
                continue;
            }
            ArrayList nidList = cardMap.get(cardid);
            this.outWordYkcards(Integer.parseInt(cardid), nidList, queryType, infokind, dbName, userpriv, havepriv, fieldpurv, type);
        }
        //循环完成后压缩文件 返回压缩文件夹的文件
        if (file.exists() && file.isDirectory()) {
            File[] fils = file.listFiles();
            if (fils.length > 1 && 0 == isZipFile) {//导出多个文件 生成压缩文件
                url = createZipFile(this.userView, this.filePath);
            } else if (fils.length == 1) {//单个文件不需要压缩
                File oldF = new File(fils[0].getAbsolutePath());
                url = oldF.getName();
                File newF = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + url);
                if (newF.exists())
                    newF.delete();
                oldF.renameTo(newF);
            }

        }
        return url;
    }

    /**
     * 删除子文件夹及文件夹内文件
     **/
    public void deleteDirOrFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                for (int i = 0; i < listFile.length; i++) {
                    String childpath = listFile[i].getAbsolutePath();
                    if (listFile[i].isFile()) {
                        File childFile = new File(childpath);
                        childFile.delete();
                    }
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    //设置导出文件名
    public HashMap<String, String> getFileName(ArrayList nidList, String dbper, String tableName) {
        HashMap<String, String> map = new HashMap<String, String>();
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");//身份证
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < nidList.size(); i++) {
            sbf.append("?");
            if (i < nidList.size() - 1)
                sbf.append(",");
        }
        String sql = "select A0100,A0101" + (StringUtils.isNotEmpty(chk) ? ("," + chk) : "") + " from " + dbper + "A01 where a0100 in (" +
                sbf.toString() + ")";
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap<String, Integer> namesmap = new HashMap<String, Integer>();//当唯一性标识为空时 判断是否有重名 有则+1
        try {
            rs = dao.search(sql, nidList);
            while (rs.next()) {
                String chkValue = "";
                if (StringUtils.isNotEmpty(chk)) {
                    chkValue = rs.getString(chk);
                } else {
                    chkValue = rs.getString("a0100");
                }

                String a0101 = rs.getString("A0101");
                String tempName = "";
                if (StringUtils.isEmpty(chkValue)) {

                    if (namesmap.get(a0101) == null) {
                        namesmap.put(a0101, 0);

                    } else {
                        namesmap.put(a0101, namesmap.get(a0101) + 1);
                        a0101 = a0101 + "(" + (namesmap.get(a0101)) + ")";
                    }
                    tempName += a0101;
                } else {
                    tempName += a0101 + "_" + chkValue;
                }
                tempName = converFileName(tempName);
                map.put(rs.getString("A0100"), tempName + "_" + tableName);
                fileNameMap.put(rs.getString("A0100"), tempName + "_" + tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 登记表导出word接口
     *
     * @param tabid
     * @param nidList
     * @param queryType
     * @param infokind
     * @param dbName
     * @param userpriv
     * @param havepriv
     * @param fieldpurv
     * @param type      文件类型 word pdf
     * @return
     * @throws Exception
     */
    private void outWordYkcards(int tabid, ArrayList nidList, String queryType, String infokind,
                                String dbName, String userpriv, String havepriv, String fieldpurv, int type) throws Exception {
        this.fieldpurv = fieldpurv;
        this.tabid = tabid;
        this.querType = Integer.parseInt(queryType);
        this.infokind = infokind;
        this.userpriv = userpriv;
        this.havepriv = "";
        this.userbase = dbName;
        if (this.tabid == -1)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "没有选择登记表,请选择!", "", ""));
        getCardPageSet();
        searchExportName(tabid);
        HashMap<String, String> filenameList = getFileName(nidList, userbase, this.exportName);//获取用户名 姓名+唯一性标识+模板名称

        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
        if (this.infokind != null && "5".equals(this.infokind) && this.plan_id != null && this.plan_id.length() > 0) {
            StatisticPlan statisticPlan = new StatisticPlan(this.userView, conn);
            alUsedFields = statisticPlan.khResultField(alUsedFields, this.plan_id);
        }
        String url = "";

        try {
            /***
             * 页边距 打印方向等页面参数设置信息
             * */
            float[] wh = getPringSetUp();
            HashMap titleMap = new HashMap();
            HashMap contextMap = new HashMap();
            HashMap gridCNmuMap = new HashMap();
            List<DynaBean> pagelist = getPagecount(tabid, this.conn);
            //查询允许打印的页签
            DataEncapsulation encap = new DataEncapsulation();
            encap.setUserview(this.userView);
            for (DynaBean pagebean : pagelist) {
                int pageId = Integer.parseInt((String) pagebean.get("pageid"));
                float[] rgridArea = YkcardStaticBo.getRGridArea(conn, tabid + "", pageId + "");// 0最左端开始位置 1最上方开始位置 2表格的宽 3表格的高 表格内容区
                //水平方向 设置模板内容居中  垂直方向最小的rtop为负时或者小于38时 默认top为38
                marginleft = ((int) wh[0] - ((int) rgridArea[2] + (int) rgridArea[0])) / 2;//默认模板 居中
                if (marginleft < 38) {//lmargin=10 时页面最小居左边距为38，
                    marginleft = 38;
                }
                if (rgridArea[1] <= 38) {
                    //marginTop=((int)wh[1]-((int)rgridArea[3]+(int)rgridArea[1]))/2;
                    marginTop = 38;
                }
                //rgridArea[2]//表格内容宽度  wh[0]
                float gridMinTop = rgridArea[4];//上标题位置区域 小于等于gridMinTop
                float gridMaxTop = rgridArea[5];//下标题 位置区域 大于等于gridMaxTop
                //标题行内容
                //上标题
                ArrayList topTitleList = this.getRPageList(tabid, pageId, "t", gridMinTop, gridMaxTop, encap);
                //标题在单元格内
                ArrayList contextTitlList = this.getRPageList(tabid, pageId, "c", gridMinTop, gridMaxTop, encap);

                //下标题
                ArrayList botmTitleList = this.getRPageList(tabid, pageId, "b", gridMinTop, gridMaxTop, encap);
                //图片在表格区域内
                ArrayList midtitleList = this.getRPageList(tabid, pageId, "p", gridMinTop, gridMaxTop, encap);

                titleMap.put(pageId + "t", topTitleList);
                titleMap.put(pageId + "c", contextTitlList);
                titleMap.put(pageId + "b", botmTitleList);
                titleMap.put(pageId + "p", midtitleList);

                gridCNmuMap.put(pageId, getRgridRCNum(tabid, pageId, gridMinTop, gridMaxTop));//表格行数与列数

                List<RGridView> rgrids = encap.getRgrid(tabid, pageId, conn);
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridMapList = encap.getGridMapList();//记录每个单元格 rleft+rwidth 等的位置信息
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopMapList = encap.getGridTopIndexList();//初始化记录单元格 rtop+rheight位置信息
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> lastGridTopList = encap.getLastGridTopList();

                for (RGridView rgrid : rgrids) {
                    dealGridBorder(rgrid, pageId + "", gridTopMapList, lastGridTopList);
                    rgrid.setRleft((Float.parseFloat(rgrid.getRleft()) + marginleft) + "");
                    rgrid.setRleft_0(rgrid.getRleft_0() + marginleft);

                    rgrid.setRtop((Float.parseFloat(rgrid.getRtop()) + marginTop) + "");
                    rgrid.setRtop_0(rgrid.getRtop_0() + marginTop);
                }

                List setList = encap.GetSets(tabid, pageId, conn);
                contextMap.put(pageId + "rgrids", rgrids);
                contextMap.put(pageId + "setList", setList);
            }


            for (int r = 0; r < nidList.size(); r++) {
                ArrayList outputList = new ArrayList();//存储登记表 标题 表格 位置参数等信息
                String nid = (String) nidList.get(r);
                for (DynaBean pagebean : pagelist) {
                    int pageId = Integer.parseInt((String) pagebean.get("pageid"));

                    LazyDynaBean pgmap_new = new LazyDynaBean();
                    ArrayList topTitleList = (ArrayList) titleMap.get(pageId + "t");
                    if (topTitleList != null && topTitleList.size() > 0) {
                        pgmap_new.set("rtops_t", topTitleList.get(1));
                        pgmap_new.set("toptitle", topTitleList.get(0));
                    } else {
                        pgmap_new.set("rtops_t", new ArrayList());
                        pgmap_new.set("toptitle", new ArrayList());
                    }
                    ArrayList botmTitleList = (ArrayList) titleMap.get(pageId + "b");
                    if (botmTitleList != null && botmTitleList.size() > 0) {
                        pgmap_new.set("rtops_b", botmTitleList.get(1));
                        pgmap_new.set("bomtitle", botmTitleList.get(0));
                    } else {
                        pgmap_new.set("rtops_b", new ArrayList());
                        pgmap_new.set("bomtitle", new ArrayList());
                    }
                    ArrayList midtitleList = (ArrayList) titleMap.get(pageId + "p");
                    pgmap_new.set("midtitle_img", midtitleList);
                    pgmap_new.set("wh", wh);
                    pgmap_new.set("paperOrientation", "0");
                    /**
                     * 查询表格内容 rgrid
                     * */
                    ArrayList contextList = getRgridCell(tabid, pageId, contextMap, alUsedFields, (ArrayList) titleMap.get(pageId + "c"), nid);
                    pgmap_new.set("context", contextList);
                    ArrayList gridRCList = (ArrayList) gridCNmuMap.get(pageId);
                    pgmap_new.set("rtops", gridRCList.get(0));//行 数  内容
                    pgmap_new.set("rlefts", gridRCList.get(1));//列 数  内容
                    outputList.add(pgmap_new);
                }
                AnalysisWordUtil awu = new AnalysisWordUtil();
                awu.setDirPath(this.filePath);
                awu.setUsePageMarginSet(true);
                awu.setOfficerOrWps(this.getOfficeOrWps());
			    	/*ArrayList list=new ArrayList();
			    	list.add(outputList);*/
                url = awu.analysisWord(filenameList.get(nid), outputList, pagelist.size());
                if (type == FILETYPE_PDF) {
                    wordToPdf(this.filePath + File.separator + url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 文件夹压缩
     * */
    public String createZipFile(UserView userview, String sourceFilePath) throws GeneralException {
        String tmpFileName = userview.getUserName() + "_tempCard.zip";
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {  //System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+userview.getUserName()+"_tempCard"
            if (sourceFile.exists() == false) {
                throw GeneralExceptionHandler.Handle(new Exception("压缩文件夹不存在！"));
            } else {
                File zipFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + tmpFileName);
                if (zipFile.exists()) {
                    zipFile.delete();
                }

                File[] sourceFiles = sourceFile.listFiles();
                if (null == sourceFiles || sourceFiles.length < 1) {
                    throw GeneralExceptionHandler.Handle(new Exception("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩."));
                } else {
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(new BufferedOutputStream(fos));
                    byte[] bufs = new byte[1024 * 10];
                    for (int i = 0; i < sourceFiles.length; i++) {
                        //创建ZIP实体，并添加进压缩包
                        ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                        zos.putNextEntry(zipEntry);
                        //读取待压缩的文件并写进压缩包里
                        fis = new FileInputStream(sourceFiles[i]);
                        bis = new BufferedInputStream(fis, 1024 * 10);
                        int read = 0;
                        while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                            zos.write(bufs, 0, read);
                        }
                        fis.close();
                        bis.close();
                    }
                    zos.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            //关闭流
            PubFunc.closeIoResource(bis);
            PubFunc.closeIoResource(zos);
            PubFunc.closeIoResource(fis);
            PubFunc.closeIoResource(fos);

        }
        return tmpFileName;
    }

    public String outWordYkcard(int tabid, ArrayList nidList, String queryType, String infokind,
                                String dbName, String userpriv, String havepriv, String fieldpurv) throws Exception {
        this.fieldpurv = fieldpurv;
        this.tabid = tabid;
        this.querType = Integer.parseInt(queryType);
        this.infokind = infokind;
        this.userpriv = userpriv;
        this.havepriv = "";
        this.userbase = dbName;
        if (this.tabid == -1)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "没有选择登记表,请选择!", "", ""));
        searchExportName(tabid);
        getCardPageSet();
        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
        if (this.infokind != null && "5".equals(this.infokind) && this.plan_id != null && this.plan_id.length() > 0) {
            StatisticPlan statisticPlan = new StatisticPlan(this.userView, conn);
            alUsedFields = statisticPlan.khResultField(alUsedFields, this.plan_id);
        }

        String excel_filename = converFileName(userView.getUserName() + "_" + this.exportName);
        String url = "";
        ArrayList outputList = new ArrayList();//存储登记表 标题 表格 位置参数等信息
        try {
            /***
             * 页边距 打印方向等页面参数设置信息
             * */
            float[] wh = getPringSetUp();
            HashMap titleMap = new HashMap();
            HashMap contextMap = new HashMap();
            HashMap gridCNmuMap = new HashMap();
            List<DynaBean> pagelist = getPagecount(tabid, this.conn);
            //查询允许打印的页签
            DataEncapsulation encap = new DataEncapsulation();
            encap.setUserview(this.userView);
            for (DynaBean pagebean : pagelist) {
                int pageId = Integer.parseInt((String) pagebean.get("pageid"));
                float[] rgridArea = YkcardStaticBo.getRGridArea(conn, tabid + "", pageId + "");// 0最左端开始位置 1最上方开始位置 2表格的宽 3表格的高 表格内容区
	    		/*marginleft=(int)wh[5];
	    		marginTop=(int)wh[2];*/
                //水平方向 设置模板内容居中  垂直方向最小的rtop为负时或者小于38时 默认top为38
	    		/*marginleft=((int)wh[0]-((int)rgridArea[2]+(int)rgridArea[0]))/2;//默认模板 居中
	    		if(marginleft<38) {//lmargin=10 时页面最小居左边距为38，
	    			marginleft=38;
	    		}
	    		if(rgridArea[1]<=38) {
	    			//marginTop=((int)wh[1]-((int)rgridArea[3]+(int)rgridArea[1]))/2;
	    			marginTop=38;
	    		}*/
                //rgridArea[2]//表格内容宽度  wh[0]
                float gridMinTop = rgridArea[4];//上标题位置区域 小于等于gridMinTop
                float gridMaxTop = rgridArea[5];//下标题 位置区域 大于等于gridMaxTop
                //标题行内容
                //上标题
                ArrayList topTitleList = this.getRPageList(tabid, pageId, "t", gridMinTop, gridMaxTop, encap);
                //标题在单元格内
                ArrayList contextTitlList = this.getRPageList(tabid, pageId, "c", gridMinTop, gridMaxTop, encap);

                //下标题
                ArrayList botmTitleList = this.getRPageList(tabid, pageId, "b", gridMinTop, gridMaxTop, encap);

                //图片在表格区域内
                ArrayList midtitleList = this.getRPageList(tabid, pageId, "p", gridMinTop, gridMaxTop, encap);

                titleMap.put(pageId + "t", topTitleList);
                titleMap.put(pageId + "c", contextTitlList);
                titleMap.put(pageId + "b", botmTitleList);
                titleMap.put(pageId + "p", midtitleList);
                gridCNmuMap.put(pageId, getRgridRCNum(tabid, pageId, gridMinTop, gridMaxTop));//表格行数与列数

                List<RGridView> rgrids = encap.getRgrid(tabid, pageId, conn);
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridMapList = encap.getGridMapList();//记录每个单元格 rleft+rwidth 等的位置信息
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopMapList = encap.getGridTopIndexList();//初始化记录单元格 rtop+rheight位置信息
                HashMap<String, HashMap<Double, ArrayList<RGridView>>> lastGridTopList = encap.getLastGridTopList();

                for (RGridView rgrid : rgrids) {
                    dealGridBorder(rgrid, pageId + "", gridTopMapList, lastGridTopList);
                    rgrid.setRleft((Float.parseFloat(rgrid.getRleft()) + marginleft) + "");
                    rgrid.setRleft_0(rgrid.getRleft_0() + marginleft);

                    rgrid.setRtop((Float.parseFloat(rgrid.getRtop()) + marginTop) + "");
                    rgrid.setRtop_0(rgrid.getRtop_0() + marginTop);
                }

                List setList = encap.GetSets(tabid, pageId, conn);
                contextMap.put(pageId + "rgrids", rgrids);
                contextMap.put(pageId + "setList", setList);
            }


            for (int r = 0; r < nidList.size(); r++) {
                String nid = (String) nidList.get(r);
                if ("1".equals(this.infokind) && nid.indexOf("`") > -1) {
                    this.userbase = nid.split("`")[0];
                    nid = nid.split("`")[1];
                }
                for (DynaBean pagebean : pagelist) {
                    int pageId = Integer.parseInt((String) pagebean.get("pageid"));

                    LazyDynaBean pgmap_new = new LazyDynaBean();
                    ArrayList topTitleList = (ArrayList) titleMap.get(pageId + "t");
                    if (topTitleList != null && topTitleList.size() > 0) {
                        pgmap_new.set("rtops_t", topTitleList.get(1));
                        pgmap_new.set("toptitle", topTitleList.get(0));
                    } else {
                        pgmap_new.set("rtops_t", new ArrayList());
                        pgmap_new.set("toptitle", new ArrayList());
                    }
                    ArrayList botmTitleList = (ArrayList) titleMap.get(pageId + "b");
                    if (botmTitleList != null && botmTitleList.size() > 0) {
                        pgmap_new.set("rtops_b", botmTitleList.get(1));
                        pgmap_new.set("bomtitle", botmTitleList.get(0));
                    } else {
                        pgmap_new.set("rtops_b", new ArrayList());
                        pgmap_new.set("bomtitle", new ArrayList());
                    }
                    pgmap_new.set("wh", wh);
                    pgmap_new.set("paperOrientation", "0");
                    /**
                     * 查询表格内容 rgrid
                     * */
                    ArrayList contextList = getRgridCell(tabid, pageId, contextMap, alUsedFields, (ArrayList) titleMap.get(pageId + "c"), nid);
                    pgmap_new.set("context", contextList);
                    ArrayList gridRCList = (ArrayList) gridCNmuMap.get(pageId);
                    pgmap_new.set("rtops", gridRCList.get(0));//行 数  内容
                    pgmap_new.set("rlefts", gridRCList.get(1));//列 数  内容
                    ArrayList midtitleList = (ArrayList) titleMap.get(pageId + "p");
                    pgmap_new.set("midtitle_img", midtitleList);
                    outputList.add(pgmap_new);
                }
            }
            AnalysisWordUtil awu = new AnalysisWordUtil();
            awu.setUsePageMarginSet(true);
            awu.setOfficerOrWps(this.getOfficeOrWps());
            url = awu.analysisWord(excel_filename, outputList, pagelist.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    /***
     * 获取页面内容
     * */
    public ArrayList getRgridCell(int tabid, int pageId, HashMap contextMap, ArrayList alUsedFields, ArrayList contextTitlList, String nid) throws Exception {
        ArrayList contextList = new ArrayList();
        List rgrids = (List) contextMap.get(pageId + "rgrids");
        List setList = (List) contextMap.get(pageId + "setList");
        GetCardCellValue card = new GetCardCellValue();
        card.setDisplay_zero(display_zero);
        StringBuffer content = new StringBuffer();
        String fenlei_type = "";
        fenlei_type = card.getOneFenleiYype(this.userView, userbase, nid, conn);
        for (int i = 0; i < rgrids.size(); i++) {
            LazyDynaBean bean = new LazyDynaBean();
            RGridView gridView = (RGridView) rgrids.get(i);
            bean.set("rtop", (int) Float.parseFloat(gridView.getRtop()));
            bean.set("rleft", (int) Float.parseFloat(gridView.getRleft()));
            bean.set("rheight", (int) Float.parseFloat(gridView.getRheight()));
            bean.set("rwidth", (int) Float.parseFloat(gridView.getRwidth()));
            bean.set("flag", gridView.getFlag());
            bean.set("l", (int) Float.parseFloat(gridView.getL()));
            bean.set("b", (int) Float.parseFloat(gridView.getB()));
            bean.set("r", (int) Float.parseFloat(gridView.getR()));
            bean.set("t", (int) Float.parseFloat(gridView.getT()));
            bean.set("lsize", Integer.valueOf(gridView.getLsize()));
            bean.set("tsize", Integer.valueOf(gridView.getTsize()));
            bean.set("bsize", Integer.valueOf(gridView.getBsize()));
            bean.set("rsize", Integer.valueOf(gridView.getRsize()));
            bean.set("align", (int) Float.parseFloat(gridView.getAlign()));
            bean.set("fonteffect", (int) Float.parseFloat(gridView.getFonteffect()));
            bean.set("fontname", gridView.getFontName());
            bean.set("fontsize", (int) Float.parseFloat(gridView.getFontsize()));
            bean.set("subflag", ("1".equals(gridView.getSubflag()) ? true : false));
            bean.set("field_type", gridView.getField_type());
            bean.set("nhide", (int) Float.parseFloat(gridView.getNHide()));
            bean.set("inputType", 0); //指标的文本编辑类型(大文本) //0普通编辑器 1 富文本编辑器
//			bean.set("recordlist", value);//gridView.get recordlist 如果是子集的话  子集的数据 (无子集可不传)
            bean.set("pageid", Integer.parseInt(gridView.getPageId()));
            bean.set("gridno", Integer.parseInt(gridView.getGridno()));

            //判断是否是子集和视图 subdomain 中 colheadheight 是否是0 为0 则给5 缩小子集视图内容行间距
            if ("1".equals(gridView.getSubflag()) || ("A".equalsIgnoreCase(gridView.getFlag()) && "1".equalsIgnoreCase(gridView.getIsView()))) {
                String domain = gridView.getSub_domain();
                //setColHeight(domain);
                bean.set("sub_domain", setColHeight(domain, gridView.getIsView()));
            } else {
                bean.set("sub_domain", gridView.getSub_domain());
            }
            bean.set("setname", gridView.getCSetName());
            //gridView.get subfiledstate 插入子集

            bean.set("hz", gridView.getCHz());
            String flag = gridView.getFlag();
            content.setLength(0);
            if (!"C".equalsIgnoreCase(flag)) {
                if ("A".equalsIgnoreCase(flag) || "B".equalsIgnoreCase(flag) || "K".equalsIgnoreCase(flag) || "E".equalsIgnoreCase(flag) || "J".equalsIgnoreCase(flag) || "Z".equalsIgnoreCase(flag)) {//人员库
                    byte nFlag = 0;
                    if ("B".equalsIgnoreCase(flag)) {
                        nFlag = 2;
                    } else if ("K".equalsIgnoreCase(flag)) {
                        nFlag = 4;
                    } else if ("E".equalsIgnoreCase(flag)) {
                        nFlag = 7;
                    } else if ("J".equalsIgnoreCase(flag)) {
                        nFlag = 5;
                    } else if ("Z".equalsIgnoreCase(flag)) {
                        nFlag = 6;
                    }
                    if ("J".equalsIgnoreCase(flag))
                        gridView.setPlan_id(this.plan_id);
                    if (!"1".equals(gridView.getSubflag())) {// 0 主集 1 子集
                        //0表示人员库
                        ArrayList valueList = null;
                        //绩效指标 setname 为空 特殊处理
                        if ("A".equalsIgnoreCase(flag) && "1".equalsIgnoreCase(gridView.getIsView()) || "J".equalsIgnoreCase(flag) || "Z".equalsIgnoreCase(flag))//人员视图
                        {
                            valueList = getTextValue(nid, userbase, conn, card, gridView, userView, nFlag, valueList, null);
                        } else if (!setList.isEmpty())
                            for (int j = 0; j < setList.size(); j++) {
                                DynaBean fieldset = (DynaBean) setList.get(j);
                                if (fieldset.get("fieldsetid").equals(gridView.getCSetName())) {

                                    valueList = getTextValue(nid, userbase, conn, card, gridView, userView, nFlag, valueList, fieldset);
                                    break;
                                }
                            }
                        if (valueList != null && !valueList.isEmpty()) {
                            for (int j = 0; j < valueList.size(); j++) {
                                if (valueList.get(j) != null && valueList.get(j).toString() != null) {
                                    if (j < valueList.size() && j > 0)
                                        content.append("\n");
                                    content.append(valueList.get(j) != null ? valueList.get(j).toString().replaceAll("&nbsp;", " ") : "");
                                } else {
                                    content.append("");
                                }
                            }
                        } else {
                            content.append("");
                        }
                        bean.set("cellvalue", content.toString());
                    } else {
                        bean.set("subflag", true);
                        bean.set("recordlist", viewSubclass(gridView, conn, userView, cyyear, cymonth, this.ctimes, userbase, nid, nFlag, fenlei_type));
                    }

                } else if ("D".equalsIgnoreCase(flag)) {
                    byte nFlag = 0;                                 //0表示人员库
                    ArrayList valueList = card.getTextValueForCexpress(userbase, conn, card, gridView, userView, alUsedFields, infokind, nid, this.plan_id);
                    if (valueList != null && !valueList.isEmpty()) {
                        for (int j = 0; j < valueList.size(); j++) {
                            if (valueList.get(j) != null && valueList.get(j).toString() != null) {
                                if (j < valueList.size() - 1)
                                    content.append("\n");
                                content.append(valueList.get(j) != null ? valueList.get(j).toString() : "");
                            } else {
                                content.append("");
                            }
                        }
                    } else {
                        content.append("");
                    }
                    bean.set("cellvalue", content.toString());
                } else if ("P".equalsIgnoreCase(flag)) {
                    String strc = "";
                    String fileName = createPhotoFile(userbase + "A00", nid, "P", conn);
                    if ("".equals(fileName)) {
                        String projectName = "hrms";
                        strc += System.getProperty("user.dir").replace("bin", "webapps");  //把bin 文件夹变到 webapps文件里面
                        strc += System.getProperty("file.separator") + projectName + System.getProperty("file.separator") + "images" + System.getProperty("file.separator") + "photo.jpg";
                    } else
                        strc = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
                    bean.set("cellvalue", strc);

                }

                if ("H".equalsIgnoreCase(flag)) {//文本型
                    String[] textCell = gridView.getCHz().split("`");
                    if (textCell != null && textCell.length > 0) {
                        if (textCell.length == 1)
                            bean.set("cellvalue", gridView.getCHz().replace("`", ""));
                        else {
                            String hz = "";
                            for (int j = 0; j < textCell.length; j++) {//解析分割符 ` 最后一个`不替换
                                hz += textCell[j];
                                if (j < textCell.length - 1)
                                    hz += "\n";
                            }
                            bean.set("cellvalue", hz);
                        }
                    } else {
                        bean.set("cellvalue", ""); //cellvalue//单元格的内容
                    }
                }
            } else {//插入单元格计算公式
                bean.set("cellvalue", card.getFormulaValue(gridView));
            }

            //标题行在单元格中
            if (contextTitlList != null && contextTitlList.size() > 0) {//由于导出word 不可将内容格与标题格同时存在 现改为判断 单元格的top+height>标题的top+height ，下一个单元格的left>标题的left+width 将标题替换单元格伪造需要的数据
                ArrayList titleList = (ArrayList) contextTitlList.get(0);
                //RGridView gridView=(RGridView)rgrids.get(i);
                for (int j = 0; j < titleList.size(); j++) {
                    int rtop = (int) Float.parseFloat(gridView.getRtop());
                    int rheight = (int) Float.parseFloat(gridView.getRheight());
                    int rleft = (int) Float.parseFloat(gridView.getRleft());
                    int rwidth = (int) Float.parseFloat(gridView.getRwidth());
                    LazyDynaBean contBean = (LazyDynaBean) titleList.get(j);
                    int ttop = (int) Float.parseFloat(contBean.get("rtop") + "");
                    int theight = (int) Float.parseFloat(contBean.get("rheight") + "");
                    int tleft = (int) Float.parseFloat(contBean.get("rleft") + "");
                    int twidth = (int) Float.parseFloat(contBean.get("rwidth") + "");        // 添加设计容错偏差两个像素内都统计
                    if (ttop >= rtop && (rtop + rheight) >= (ttop + theight) && rleft <= tleft && ((rleft + rwidth) >= (tleft + twidth - 2) || (rleft + rwidth) >= (tleft + twidth + 2))) {
                        if (contBean.get("flag").equals(6)) {
                            bean.set("flag", "P");
                        } else
                            bean.set("flag", "h");
                        if (StringUtils.isNotEmpty(contBean.get("titlevalue").toString())) {
                            bean.set("cellvalue", contBean.get("titlevalue"));
                        }
                    }
                }
            }
            //单元格字体自适应
            if (autoSize && !"p".equalsIgnoreCase(flag)) {
                ResetFontSizeUtil rfsu = new ResetFontSizeUtil();
                if (!(Boolean) bean.get("subflag")) {//非子集
                    if (bean.get("cellvalue") != null) {
                        int size = rfsu.ResetFontSize(Double.parseDouble(bean.get("rwidth").toString()),
                                Double.parseDouble(bean.get("rheight").toString()), bean.get("cellvalue").toString(),
                                Integer.parseInt(bean.get("fontsize").toString()), bean.get("fontname").toString(), Integer.parseInt(bean.get("fonteffect").toString()));
                        bean.set("fontsize", size);
                    }
                } else {
                    ArrayList subList = (ArrayList) bean.get("recordlist");
                    if (subList.size() > 0) {// colhead//是否输出列标题   width 列宽  datarowcount 指定行数
                        int size = getSubFontSize(bean, rfsu);
                        if (size != 0)
                            bean.set("fontsize", size);
                    }
                }
            }

            contextList.add(bean);

        }

        return contextList;
    }

    private int getSubFontSize(LazyDynaBean bean, ResetFontSizeUtil rfsu) throws Exception {
        String sub_domain = (String) bean.get("sub_domain");
        int width = (int) Float.parseFloat(bean.get("rwidth").toString());
        int height = (int) Float.parseFloat(bean.get("rheight").toString());
        int fontsize = Integer.parseInt(bean.get("fontsize").toString());
        int fonteffect = Integer.parseInt(bean.get("fonteffect").toString());
        ArrayList subList = (ArrayList) bean.get("recordlist");
        String fontname = bean.get("fontname").toString();
        ArrayList list = new ArrayList();
        Document doc = null;
        doc = PubFunc.generateDom(sub_domain);
        Element paraEl = doc.getRootElement().getChild("para");
        String colhead = paraEl.getAttributeValue("colhead");//是否显示列标题
        String datarowcount = paraEl.getAttributeValue("datarowcount");//指定行数

        List<Element> fieldList = doc.getRootElement().getChildren("field");
        HashMap<String, String> map = new HashMap();
        int fieldWidth = 0;
        for (Element el : fieldList) {
            String name = el.getAttributeValue("name").toLowerCase();
            String fieldW = el.getAttributeValue("width");
            map.put(name, fieldW);
            fieldWidth += Integer.parseInt(fieldW);
        }
        float scale = width * 1.0F / fieldWidth * 1.0F;
        for (String key : map.keySet()) {
            map.put(key, ((int) Integer.parseInt(map.get(key)) * scale) + "");
        }
        int rows = subList.size();
        if (StringUtils.isNotEmpty(datarowcount)) {//设置默认行数
            if (subList.size() < Integer.parseInt(datarowcount)) {//子集数小于设置行数行数改为默认行数
                rows = Integer.parseInt(datarowcount);
            }
        }
        if ("true".equals(colhead)) {
            rows += 1;
        }
        int rowHeight = height / rows;
        paraEl.setAttribute("colheadheight", (int) (rowHeight / 96f * 25.4) + "");
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        bean.set("sub_domain", outputter.outputString(doc));
        int realSize = 0;
        for (String key : map.keySet()) {
            for (int i = 0; i < subList.size(); i++) {
                HashMap subMap = (HashMap) subList.get(i);
                if (subMap.get(key) != null && StringUtils.isNotEmpty(subMap.get(key).toString())) {
                    int subSize = rfsu.ResetFontSize(Double.parseDouble(map.get(key).toString()), rowHeight, subMap.get(key).toString(), fontsize, fontname, fonteffect);
                    if (realSize != 0) {
                        if (subSize < realSize)
                            realSize = subSize;
                    } else {
                        realSize = subSize;
                    }
                }
            }
        }
        return realSize;
    }

    /**
     * 子集导出 标题行高默认高度设置自动计算导致行高过大导致超页
     * 现改为不设置高度给默认高度
     *
     * @param domain
     * @return
     */
    private String setColHeight(String domain, String isView) {
        StringBuffer sbf = new StringBuffer();
        sbf.append("/sub_para/para");
        try {
            Document doc = null;
            if (domain != null && domain.length() > 0)
                doc = PubFunc.generateDom(domain);
            else
                return domain;
            XPath xPath = XPath.newInstance(sbf.toString());
            List childlist = xPath.selectNodes(doc);
            if (childlist.size() > 0) {
                Element el = (Element) childlist.get(0);
                if (el != null) {
                    String colheadheight = el.getAttributeValue("colheadheight");
                    if ("0".equals(colheadheight) && "1".equals(isView)) {
                        el.getAttribute("colheadheight").setValue("5");
                    } else if (StringUtils.isNotEmpty(colheadheight)) {
                        el.getAttribute("colheadheight").setValue(colheadheight);
                    }
                }
            }
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            return outputter.outputString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @param userTable
     * @param userNumber
     * @param flag
     * @param conn
     * @return
     * @throws Exception
     */
    public String createPhotoFile(String userTable, String userNumber, String flag, Connection conn) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        InputStream in = null;
        File tempFile = null;
        ServletUtilities.createTempDir();//校验是否有临时文件夹 没有则创建
        FileOutputStream out = null;
        String filename = "";
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select fileid,ext,Ole from ");
            strsql.append("" + userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and Flag='");
            strsql.append(flag);
            strsql.append("'");
            rowSet = dao.search(strsql.toString());
            if (rowSet.next()) {
                String fileid = rowSet.getString("fileid");
                if (StringUtils.isNotEmpty(fileid)) {
                    in = VfsService.getFile(fileid);
                } else {
                    in = rowSet.getBinaryStream("ole");
                }
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rowSet.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));
                out = new FileOutputStream(tempFile);
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    out.write(buf, 0, len);
                }
                filename = tempFile.getName();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(out);
            if (in != null)
                PubFunc.closeIoResource(in);
            if (rowSet != null)
                PubFunc.closeDbObj(rowSet);
        }
        return filename;
    }

    /*****对子集的显示*****/
    public ArrayList viewSubclass(RGridView rgrid, Connection conn, UserView userview, int statYear,           //年
                                  int statMonth,          //月
                                  int ctimes,             //次数
                                  String userbase,
                                  String nId, byte nFlag, String fenlei_type) {
        ArrayList list = new ArrayList();
        StringBuffer html = new StringBuffer();
        String sub_domain = rgrid.getSub_domain();
        if (sub_domain == null || sub_domain.length() <= 0)
            return new ArrayList();
        YkcardViewSubclass ykcardViewSubclass = new YkcardViewSubclass(conn, cyyear, cymonth, ctimes, userbase, nId, userview);
        int fact_width = (int) Float.parseFloat(rgrid.getRwidth()) + 1;
        int fact_height = (int) Float.parseFloat(rgrid.getRheight()) + 1;
        ykcardViewSubclass.setFenlei_type(fenlei_type);
        ykcardViewSubclass.setFact_width(fact_width);
        ykcardViewSubclass.setFact_height(fact_height);
        ykcardViewSubclass.setNFlag(nFlag);
        ykcardViewSubclass.setFieldpurv(this.fieldpurv);
        ykcardViewSubclass.getXmlSubdomain(rgrid.getSub_domain(), rgrid);
        ArrayList fieldlist = ykcardViewSubclass.getFieldList();//fieldList 在执行getXmlSubdomain（）此方法时已经将list的指标查出 可以直接使用 changxy
        ykcardViewSubclass.setSearchDateSql(getSearchDatasql(fieldlist));//拼接sql按日期查询
        try {
            list = ykcardViewSubclass.outWordListMap(infokind, userbase, conn, userview, rgrid, "1024", nFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /***
     * 子集查询日期条件  由于使用的参数偏多故在此类中添加此方法 需要的指标集合从YkcardViewSubclass中先取出
     * changxy
     * 20160928
     */
    private String getSearchDatasql(ArrayList fieldlist) {
        boolean flag = false;
        String str = null;
        for (int i = 0; i < fieldlist.size(); i++) {
            if (fieldlist.get(i).toString() != null && fieldlist.get(i).toString().length() > 2)
                if (fieldlist.get(i).toString() != null && "z0".equalsIgnoreCase(fieldlist.get(i).toString().substring(fieldlist.get(i).toString().length() - 2, fieldlist.get(i).toString().length())))//指标中有没有日期标识，如果有则按照查询类型拼sql
                {
                    flag = true;
                    str = fieldlist.get(i).toString();
                    break;
                }
        }
        String sql = null;
        StringBuffer sbf = new StringBuffer();
        if (flag && str != null) {//不同查询使用的年月字段不一样，

            switch (this.querType) {
                case 1://年月
                    sbf.append(" and " + Sql_switcher.year(str) + "=" + this.cmyear);//月份使用年月
                    if (this.cmmonth != 13)
                        sbf.append("and " + Sql_switcher.month(str) + "=" + this.cmmonth);
                    break;
                case 2://时间段
                    sbf.append(" and " + Sql_switcher.dateToChar(str) + ">= '" + this.cdataStart + "' and " + Sql_switcher.dateToChar(str) + "<='" + this.cdataEnd + "'");
                    break;
                case 3://季度
                    sbf.append(" and " + Sql_switcher.year(str) + "=" + this.csyear);//季度使用年份
                    switch (this.cseason) {
                        case 1:
                            sbf.append(" and " + Sql_switcher.month(str) + ">=1 ");
                            sbf.append(" and " + Sql_switcher.month(str) + "<=3 ");
                            break;
                        case 2:
                            sbf.append(" and " + Sql_switcher.month(str) + ">=4 ");
                            sbf.append(" and " + Sql_switcher.month(str) + "<=6 ");
                            break;
                        case 3:
                            sbf.append(" and " + Sql_switcher.month(str) + ">=7 ");
                            sbf.append(" and " + Sql_switcher.month(str) + "<=9 ");
                            break;
                        case 4:
                            sbf.append(" and " + Sql_switcher.month(str) + ">=10 ");
                            sbf.append(" and " + Sql_switcher.month(str) + "<=12 ");
                            break;
                    }
                    break;
                case 4://年
                    sbf.append(" and " + Sql_switcher.year(str) + "=" + this.cyyear);
                    break;
            }
        }
        if ("0".equals(querType))//按条件查询不需拼接日期sql 20161011 changxy
            return "";
        return sbf.toString();
    }

    /**
     * @param userbase
     * @param conn
     * @param card
     * @param rgrid
     * @param userview
     * @param nFlag
     * @param valueList
     * @param fieldset
     * @return
     * @throws Exception
     */
    private ArrayList getTextValue(String nid, String userbase, Connection conn, GetCardCellValue card, RGridView rgrid, UserView userview, byte nFlag, ArrayList valueList, DynaBean fieldset) {
        //获得单元格的内容值
        try {
            String changeflag = "0";
            String field_priv = this.fieldpurv;
            if (fieldset != null)
                changeflag = fieldset.get("changeflag").toString();
            if (fieldset == null && "Z03".equals(rgrid.getCSetName())) {
                changeflag = "0";
                field_priv = "0";//招聘指标不校验指标权限
            }
            if ("1".equalsIgnoreCase(rgrid.getIsView())) {
                field_priv = "1";//人员视图指标不校验视图指标权限
            }
            if (querType == 0) {
                valueList = card.GetFldValue(infokind, rgrid.getCSetName(), rgrid.getField_name(), nFlag, userbase, rgrid, querType, Integer.parseInt(changeflag), cyyear, cmmonth, ctimes, nid, userview, cdataStart, cdataEnd, cseason, conn, field_priv);
            } else if (querType == 1) {
                if (infokind != null && "5".equals(infokind)) {
                    StatisticPlan statisticPlan = new StatisticPlan(userview, conn);
                    String table_name = statisticPlan.getPER_RESULT_TableName(this.plan_id);
                    rgrid.setCSetName(table_name);
                    valueList = card.GetFldValue(infokind, rgrid.getCSetName(), rgrid.getField_name(), nFlag, userbase, rgrid, querType, 0, cmyear, cmmonth, ctimes, nid, userview, cdataStart, cdataEnd, cseason, conn, field_priv);
                } else {
                    valueList = card.GetFldValue(infokind, rgrid.getCSetName(), rgrid.getField_name(), nFlag, userbase, rgrid, querType, Integer.parseInt(changeflag), cmyear, cmmonth, this.ctimes, nid, userview, cdataStart, cdataEnd, cseason, conn, field_priv);
                }
            } else if (querType == 2)
                valueList = card.GetFldValue(infokind, rgrid.getCSetName(), rgrid.getField_name(), nFlag, userbase, rgrid, querType, Integer.parseInt(changeflag), cmyear, cmmonth, ctimes, nid, userview, cdataStart, cdataEnd, cseason, conn, field_priv);
            else if (querType == 3)
                valueList = card.GetFldValue(infokind, rgrid.getCSetName(), rgrid.getField_name(), nFlag, userbase, rgrid, querType, Integer.parseInt(changeflag), csyear, cmmonth, ctimes, nid, userview, cdataStart, cdataEnd, cseason, conn, field_priv);
            else if (querType == 4)
                valueList = card.GetFldValue(infokind, rgrid.getCSetName(), rgrid.getField_name(), nFlag, userbase, rgrid, querType, Integer.parseInt(changeflag), cyyear, cymonth, ctimes, nid, userview, cdataStart, cdataEnd, cseason, conn, field_priv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueList;
    }

    /**
     * 表格的列数与行数
     **/
    public ArrayList getRgridRCNum(int tabid, int pageid, float gridMinTop, float gridMaxTop) throws Exception {
    	/*ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rs=null;*/
        ArrayList RClist = new ArrayList();
    	/*ArrayList rtoplist=new ArrayList();//行数
    	ArrayList rleftlist=new ArrayList();//列数
*/
        RClist.add(YkcardStaticBo.getDisRtopList(conn, tabid + "", pageid + "", marginTop));
        RClist.add(YkcardStaticBo.getDisRleftList(conn, tabid + "", pageid + "", marginleft));
        //String sql="select distinct rtop from rgrid where tabid="+tabid+" and pageid="+pageid+" order by rtop";
    	/*String sql="select distinct rtop from rgrid where tabid="+tabid+" and pageid="+pageid+" order by rtop";
    	try {
    		//ArrayList contextList=(ArrayList)contextTitlList.get(0);
			rs=dao.search(sql);
			while(rs.next()){
				rtoplist.add((int)rs.getFloat("rtop")+marginTop+"");
			}
			sql="select distinct rleft from rgrid where tabid="+tabid+" and pageid="+pageid+" order by rleft";
			rs=dao.search(sql);
			while(rs.next()){
				rleftlist.add((int)rs.getFloat("rleft")+marginleft+"");
			}
			RClist.add(rtoplist);
			RClist.add(rleftlist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}*/
        return RClist;
    }


    /***
     *
     * 页面设置参数
     * */
    public float[] getPringSetUp() throws Exception {
    	 /* DataEncapsulation encap = new DataEncapsulation();
          List rnameList=encap.getRname(tabid,conn);*/
        List rnameList = YkcardStaticBo.getRname(tabid + "", conn);
        LazyDynaBean rec = null;
        if (rnameList.size() == 0)
            return null;
        rec = (LazyDynaBean) rnameList.get(0);
        float[] wh = new float[8];
        int direct = Integer.parseInt(rec.get("paperori") + "");//纸张方向横向 1：纵向2：横向
        int paper = Integer.parseInt(rec.get("paper") + "");//纸张标识 1：A3，2：A4…
        int width = 0;
        int height = 0;
        int tmargin = (int) Float.parseFloat(rec.get("tmargin") + "");
        int bmargin = (int) Float.parseFloat(rec.get("bmargin") + "");
        int lmargin = (int) Float.parseFloat(rec.get("lmargin") + "");
        int rmargin = (int) Float.parseFloat(rec.get("rmargin") + "");
        if (direct == 1) {
            width = (int) Float.parseFloat(rec.get("paperw") + "");
            height = (int) Float.parseFloat(rec.get("paperh") + "");
        } else {
            width = (int) Float.parseFloat(rec.get("paperh") + "");
            height = (int) Float.parseFloat(rec.get("paperw") + "");
        }
        wh[0] = Math.round((float) (width / 25.4 * PixelInInch));
        wh[1] = Math.round((float) (height / 25.4 * PixelInInch));
        wh[2] = Math.round((float) (tmargin / 25.4 * PixelInInch));//顶部间距
        wh[3] = Math.round((float) (bmargin / 25.4 * PixelInInch));//底部间距
        wh[4] = Math.round((float) (rmargin / 25.4 * PixelInInch));//右侧间距
        wh[5] = Math.round((float) (lmargin / 25.4 * PixelInInch));//左侧间距
        wh[6] = direct * 1f;//横纵向标识 1纵向 0横向
        wh[7] = paper * 1f;
        return wh;
    }


    /***
     * String tabid
     * String pageid 页签
     * String position 方位 上标题  下标题 封面  标题在单元格内  t 上标题 all 封面 c 标题在单元格内  b 标题在单元格内
     * float pix
     *
     * **/
    public ArrayList getRPageList(int tabid, int pageid, String position, float toppix, float botmpix, DataEncapsulation encap) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String sql = "";
        ArrayList pglist = new ArrayList();
        ArrayList titleList = new ArrayList();
        ArrayList titleRtopList = new ArrayList();
        try {
            float[] rgridArea = YkcardStaticBo.getRGridArea(conn, tabid + "", pageid + "");
            if ("t".equals(position)) {
                sql = "select * from rpage where tabid=" + this.tabid + " and pageid=" + pageid + "  and rtop<=" + toppix + " order by rtop,rleft";
            } else if ("all".equals(position)) {
                sql = "select * from rpage where tabid=" + this.tabid + " and pageid=" + pageid + " order by rtop,rleft";
            } else if ("c".equals(position) || "p".equals(position)) {
                sql = "select * from rpage where tabid=" + this.tabid + " and pageid=" + pageid + " and rtop<" + botmpix + "  and rtop>" + toppix + " order by rtop,rleft";
            } else if ("b".equals(position)) {
                sql = "select * from rpage where tabid=" + this.tabid + " and pageid=" + pageid + " and rtop>=" + botmpix + " order by rleft,rtop";
            }
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                int gridno = rs.getInt("gridno");
                bean.set("gridno", gridno);
                bean.set("pageid", rs.getInt("pageid"));
                bean.set("tabid", rs.getInt("tabid"));
                bean.set("fonteffect", rs.getInt("Fonteffect"));
                bean.set("fontname", rs.getString("fontname"));
                bean.set("fontsize", rs.getInt("fontsize"));
                bean.set("hz", StringUtils.isNotEmpty(rs.getString("Hz")) ? rs.getString("Hz") : "");//表格描述
                bean.set("rtop", rs.getInt("rtop") + marginTop);
                bean.set("rleft", rs.getInt("rleft") + marginleft);
				/*if(rs.getInt("flag")==6&&(!"c".equals(position)&&!"p".equals(position))){
					bean.set("rtop", rs.getInt("rtop"));
					bean.set("rleft", rs.getInt("rleft"));
				}else {
					bean.set("rtop", rs.getInt("rtop")+marginTop);
					bean.set("rleft", rs.getInt("rleft")+marginleft);
				}*/
                bean.set("rwidth", rs.getInt("rwidth"));
                bean.set("rheight", rs.getInt("rheight"));
                String extendattr = Sql_switcher.readMemo(rs, "extendattr");
                bean.set("extendattr", extendattr);
                String hzValue = "";
                if (rs.getInt("flag") == 6) {//插入标题图片
                    if ("c".equals(position)) {//单元格中插入图片不处理 position为p时再处理
                        continue;
                    }
                    if ("c".equals(position))
                        bean.set("flag", 6);
                    else
                        bean.set("flag", 7);
                    String ext = "";
                    if (extendattr.indexOf("<format>") != -1 && extendattr.indexOf("</format>") != -1) {
                        ext = extendattr.substring(extendattr.indexOf("<ext>") + 5, extendattr.indexOf("</ext>"));
                    }
                    if (StringUtils.isEmpty(ext)) {
                        continue;
                    }
                    hzValue = getImageFile(ext, pageid, gridno);//encap.createTitlePhotoFile(tabid,pageid,gridno+"",ext,conn);
                    if ("".equals(hzValue)) {
                        String projectName = "hrms";
                        hzValue += System.getProperty("user.dir").replace("bin", "webapps");  //把bin 文件夹变到 webapps文件里面
                        hzValue += System.getProperty("file.separator") + projectName + System.getProperty("file.separator") + "images" + System.getProperty("file.separator") + "photo.jpg";
                    } else {
                        hzValue = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + hzValue;
                    }
                } else {
                    hzValue = encap.getPageTitle(pageid, rs.getInt("flag"), StringUtils.isNotEmpty(rs.getString("hz")) ? rs.getString("hz") : "", nid, userbase, tabid, this.infokind, rs.getString("extendattr"));
                    bean.set("flag", rs.getInt("flag"));
                }
                bean.set("titlevalue", hzValue);

                if (("t".equals(position) || "b".equals(position)) && StringUtils.isNotEmpty(hzValue)) {
                    //目前标题宽度计算先取库中对应数据。计算实际宽度会存在标题已超出内容宽度，重新计算居中 与模板设置不一致
                    int flag = Integer.parseInt(bean.get("flag").toString());
                    int width = 0;
                    //插入标题图片与纯文本特殊处理
                    if (flag != 0 && flag != 6 && rs.getInt("flag") != 6) {
                        width = StrWidth(rs.getInt("fontsize"), rs.getInt("Fonteffect"), hzValue, rs.getString("fontname"));
                    } else {
                        width = Integer.parseInt(bean.get("rwidth").toString());
                    }
                    int overWidth = (int) rgridArea[6] - (width + Integer.parseInt(bean.get("rleft").toString()));
                    if (overWidth < 0) {
                        bean.set("rleft", Integer.parseInt(bean.get("rleft").toString()) + overWidth / 2);
						/*if(rs.getInt("flag")!=6) {
							bean.set("rleft",Integer.parseInt(bean.get("rleft").toString())+overWidth/2 );
						}else {
							bean.set("rleft",Integer.parseInt(bean.get("rleft").toString()));
						}*/
                    }
                }
                if (!"p".equals(position)) {
                    titleList.add(bean);//标题 位置参数信息
                    titleRtopList.add(rs.getInt("rtop") + marginTop);//标题rtop集合
					/*if(rs.getInt("flag")!=6) {
						titleRtopList.add(rs.getInt("rtop")+marginTop);//标题rtop集合
					}else {
						titleRtopList.add(rs.getInt("rtop"));//标题rtop集合
					}*/
                } else {
                    if (rs.getInt("flag") == 6) {
                        pglist.add(bean);
                    }
                }
            }
            if (!"p".equals(position)) {
                if (titleList.size() > 0 && titleRtopList.size() > 0) {
                    pglist.add(titleList);
                    pglist.add(titleRtopList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return pglist;
    }

    private int StrWidth(int fontSize, int fontEffect, String value, String fontName) {
        int w = 0;
        Font font = new Font(fontName, fontEffect, fontSize);
        BufferedImage gg = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = gg.createGraphics(); // 获得画布
        g.setFont(font);
        //此处方法获取到的是字符的宽度(无论字符串中是否包含汉字等占两个字符的,获取的都是按一个字符的宽度加起来)
        //将传进来的字符串转成字节
        int valueLength = value.length();
        byte[] arrayBytes = value.getBytes();
        int stringWidth = g.getFontMetrics().stringWidth(value);
        w = (int) Math.ceil(stringWidth * arrayBytes.length / valueLength);
        return w;
    }

    /**
     * 生成图片
     **/
    public String getImageFile(String ext, int pageid, int gridno) {
        String sql = "select content from rpage where tabid=" + this.tabid + " and pageid=" + pageid + "and gridno=" + gridno;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        File file = null;
        String fileName = "";
        ServletUtilities.createTempDir();
        InputStream in = null;
        FileOutputStream out = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                file = File.createTempFile(ServletUtilities.tempFilePrefix, ext, new File(System.getProperty("java.io.tmpdir")));
                in = rs.getBinaryStream("content");
                out = new FileOutputStream(file);
                int length;
                byte[] buf = new byte[1024];
                if (in != null) {
                    while ((length = in.read(buf, 0, 1024)) != -1) {
                        out.write(buf, 0, length);
                    }
                }
                fileName = file.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeIoResource(out);
            PubFunc.closeIoResource(in);
        }

        return fileName;
    }
    /**
     * 得到表格区域
     * @param conn
     * @param table
     * @param pageid
     * @return
     */
/*	private float[] getRGridArea(Connection conn,int table,int pageid)
	{
		StringBuffer sql=new StringBuffer();
		String sqlStr="select tabid,gridno,rleft,rtop,rwidth,rheight,pageid from rgrid where tabid="+tabid +" and pageid="+pageid;

		String titleSql=" union all  select tabid,gridno,rleft,rtop,rwidth,rheight,pageid from rpage where tabid="+tabid +" and pageid="+pageid;
	    sql.append("select max(rleft+rwidth) as max_W from ("+sqlStr+titleSql+")grid ");
	    ContentDAO dao=new ContentDAO(this.conn);
	    float rgridA[]=new float[7];
	    try
	    {
	    	RowSet rs=dao.search(sql.toString());
	    	if(rs.next())
	    		rgridA[2]=rs.getFloat("max_W");//模板整体宽
	    	sql.setLength(0);
	    	sql.append("select max(rtop+rheight) as max_H from  ("+sqlStr+titleSql+")grid ");
	    	rs=dao.search(sql.toString());
	    	if(rs.next())
	    		rgridA[3]=rs.getFloat("max_H");//整体高
	    	sql.setLength(0);
	    	sql.append("select min(rtop) as min_top from  ("+sqlStr+titleSql+")grid ");
	    	rs=dao.search(sql.toString());
	    	if(rs.next())
	    		rgridA[1]=rs.getFloat("min_top");//最上方的位置
	    	sql.setLength(0);
	    	sql.append("select min(rleft) as min_left from  ("+sqlStr+titleSql+")grid " );
	    	rs=dao.search(sql.toString());
	    	if(rs.next())
	    		rgridA[0]=rs.getFloat("min_left");//最左边的位置

	    	sql.setLength(0);
	    	sql.append("select min(rtop) as min_top from  ("+sqlStr+")grid ");
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
	    		rgridA[4]=rs.getFloat("min_top");
	    	}

	    	sql.setLength(0);
	    	sql.append("select max(rtop+rheight) as max_H from  ("+sqlStr+")grid ");
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
	    		rgridA[5]=rs.getFloat("max_H");//最左边的位置
	    	}

	    	sql.setLength(0);
	    	sql.append("select max(rleft+rWidth) as max_W from  ("+sqlStr+")grid "); //模板内容的最大宽度即模板最右边的宽度
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
	    		rgridA[6]=rs.getFloat("max_W");//最左边的位置
	    	}

	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return rgridA;
	}*/


    /**
     * @param intTabid
     * @param conn
     * @return
     * @throws Exception
     */
    private List getPagecount(int intTabid, Connection conn) throws Exception {
        return YkcardStaticBo.getRtitleMap(intTabid + "", conn);
    }

    /***
     * 统一导出文件名称格式：登记表名称+用户名
     * */
    public void searchExportName(int tabid) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String sql = "select name from rname where tabid='" + tabid + "'";
        String exportName = "";
        try {

            rs = dao.search(sql);
            while (rs.next()) {
                exportName = rs.getString(1);
            }
            //导出文件名特殊格式转为全角
            exportName = converFileName(exportName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        this.setExportName(exportName);
    }

    /**
     * 文件名转全角
     *
     * @param name
     * @return
     */
    private String converFileName(String exportName) {
        //导出文件名特殊格式转为全角
        exportName = exportName.replace("\\", "＼").replace("/", "／").replace(":", "：")
                .replace("*", "＊").replace("?", "？").replace("<", "＜").replace("\"", "＂")
                .replace(">", "＞").replace("|", "｜");
        return exportName;
    }

    /***
     * 查询需要导出的人员或单位或岗位名称
     * */
    public String getExportName(String nid, String dbname) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String sql = "";
        String exportName = "";
        if (nid == null || "".equals(nid))
            return null;
        try {
            if ("1".equals(infokind) || "5".equals(infokind)) {//人员
                sql = "select A0101 from " + dbname + "A01 where A0100='" + nid + "'";
            } else if ("2".equals(infokind)) {//单位 UN   UM部门
                sql = "select organization.codeitemdesc from t_card_result," +
                        "organization where organization.codeitemid=t_card_result.objid and t_card_result.flag=2 and t_card_result.objid='" + nid + "' and UPPER(t_card_result.username)='" + userView.getUserName().toUpperCase() + "'";

            } else if ("4".equals(infokind)) {//岗位 @K
                sql = "select organization.codeitemdesc " +
                        "from t_card_result,organization" +
                        " where organization.codeitemid=t_card_result.objid and t_card_result.flag=4 and t_card_result.objid='" + nid + "' and UPPER(t_card_result.username)='" + userView.getUserName().toUpperCase() + "'";

            } else if ("6".equals(infokind)) {//基准岗位
                String codeset = new CardConstantSet().getStdPosCodeSetId();
                sql = "select codeitemdesc from t_card_result,CodeItem " +
                        " where CodeItem.codeitemid=t_card_result.objid and codesetid='" + codeset + "'" +
                        " and t_card_result.flag=6" +// 基准岗位
                        " and UPPER(t_card_result.username)='" + userView.getUserName().toUpperCase() + "' and t_card_result.objid= '" + nid + "'";
            }
            rs = dao.search(sql);
            while (rs.next()) {
                exportName = rs.getString(1);
            }
            exportName = converFileName(exportName);
            if (fileNameMap.containsKey(exportName)) {
                int index = Integer.parseInt(fileNameMap.get(exportName).toString()) + 1;
                fileNameMap.put(exportName, index);
                exportName = exportName + "(" + index + ")";
            } else {
                fileNameMap.put(exportName, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exportName;
    }


    /***
     * 获取页面设置参数
     * ykcard_auto 页面自适应
     * display_zero 打印0
     * */
    private void getCardPageSet() {
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
        CardConstantSet cardConstantSet = new CardConstantSet();
        LazyDynaBean rnameExtendAttrBean = cardConstantSet.getRnameExtendAttrBean(this.conn, this.tabid + "");
        if (rnameExtendAttrBean != null) {
            this.display_zero = (String) rnameExtendAttrBean.get("display_zero");
        }
    }

}
