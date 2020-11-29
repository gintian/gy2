package com.hjsj.hrms.module.talentmarkets.talenthall.businessobject.impl;

import com.hjsj.hrms.module.talentmarkets.talenthall.businessobject.TalentHallService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.*;

/**
 * @Description 人才展厅功能接口实现类
 * @Author wangz
 * @Date 2019/10/9 18:06
 * @Version V1.0
 **/
public class TalentHallServiceImpl implements TalentHallService {
    private UserView userView;
    private Connection conn;
    private ContentDAO dao;

    public TalentHallServiceImpl(UserView userView, Connection connection) {
        this.userView = userView;
        this.conn = connection;
        this.dao = new ContentDAO(this.conn);
    }

    @Override
    public Map getData(int page, int limit, MorphDynaBean queryValues, String orderyType) throws GeneralException {
        Map dataMap = new HashMap();
        int totalCount = 0;
        ArrayList dataList = new ArrayList();
        try {
            StringBuffer orderSql = new StringBuffer();
            orderSql.append(" order by ");
            if (StringUtils.equalsIgnoreCase(orderyType, "1")) {
                orderSql.append("Z8505");
            } else if (StringUtils.equalsIgnoreCase(orderyType, "2")) {
                orderSql.append("attention");
            } else if (StringUtils.equalsIgnoreCase(orderyType, "3")) {
                orderSql.append("Z8509");
            } else if (StringUtils.equalsIgnoreCase(orderyType, "4")) {
                orderSql.append("Z8507");
            }
            orderSql.append(" desc");
            String sql = this.getDataSql(queryValues);
            String[] fields = {"B0110", "A0101", "E0122", "E01A1", "Z8507", "Z8509", "approval", "attention", "Z8501", "nbase", "a0100", "resumeSelfIntroduction","postTypeField"};
            PaginationManager paginationm = null;
            paginationm = new PaginationManager(sql,
                    "", "", orderSql.toString(), fields, "");
            paginationm.setBAllMemo(true);
            paginationm.setPagerows(limit);
            totalCount = paginationm.getMaxrows();
            dataList = (ArrayList) paginationm.getPage(page);
            if (dataList.isEmpty() && page != 1) {
                dataList = (ArrayList) paginationm.getPage(page - 1);
            }
            RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_LEVEL_CODE", this.conn);
            //个人简介指标项 itemid
            String resumeSelfIntroduction = TalentMarketsUtils.getResumeSelfIntroduction();
            String postTypeFieldCodesetId = vo.getString("str_value");
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean data = (LazyDynaBean) dataList.get(i);
                String dept = "";
                String b0110 = (String) data.get("b0110");
                String b0110Desc = AdminCode.getCodeName("UN", b0110);
                if (StringUtils.isNotEmpty(b0110Desc)) {
                    dept += b0110Desc;
                }
                String e0122 = (String) data.get("e0122");
                String e0122Desc = AdminCode.getCodeName("UM", e0122);
                if (StringUtils.isNotEmpty(e0122Desc)) {
                    dept += "\\" + e0122Desc;
                }
                String e01a1 = (String) data.get("e01a1");
                String postTypeValue = (String) data.get("posttypefield");
                String postTypeDesc = AdminCode.getCodeName(postTypeFieldCodesetId,postTypeValue);
                String e01a1Desc = AdminCode.getCodeName("@K", e01a1);
                if(StringUtils.isNotEmpty(postTypeDesc)){
                    e01a1Desc = postTypeDesc+"\\"+e01a1Desc;
                }
                data.set("dept", dept);
                data.set("e01a1", e01a1Desc);
                data.set("id", i);
                StringBuffer photourl = new StringBuffer();
                String filename = ServletUtilities.createPhotoFile(data.get("nbase") + "A00", (String) data.get("a0100"), "P", null);
                if (StringUtils.isNotBlank(filename)) {
                    photourl.append("/servlet/vfsservlet?fileid=");
                    //库中fileid字段没有值时，其做法是返回了“临时”文件的名称
                    if (filename.startsWith(ServletUtilities.tempFilePrefix)) {
                        photourl.append(PubFunc.encrypt(filename));
                        photourl.append("&fromjavafolder=true");
                    }else{
                        photourl.append(filename);
                    }
                } else {
                    photourl.append("/images/photo.jpg");
                }
                data.set("photo", photourl.toString());
                String approval = (String) data.get("z8507");
                if (StringUtils.isEmpty(approval)) {
                    data.set("z8507", "0");
                }
                String attention = (String) data.get("z8509");
                if (StringUtils.isEmpty(attention)) {
                    data.set("z8509", "0");
                }
                data.set("objectid", PubFunc.encrypt(data.get("nbase") + "`" + data.get("a0100")));
                if(StringUtils.isNotEmpty(resumeSelfIntroduction)){
                    FieldItem item = DataDictionary.getFieldItem(resumeSelfIntroduction);
                    if(item != null){
                        String itemType = item.getItemtype();
                        String codeSetid = item.getCodesetid();
                        if(StringUtils.equalsIgnoreCase("A",itemType)&&!StringUtils.equalsIgnoreCase("0",codeSetid)){
                            String value = AdminCode.getCodeName(codeSetid,(String) data.get("resumeselfintroduction"));
                            data.set("resumeselfintroduction",value);
                        }
                    }
                }
                //String approval =(String) data.get("approval");
                //if(StringUtils.isEmpty(approval)){
                //    data.set("approval","0");
                //}
                //String attention =(String) data.get("attention");
                //if(StringUtils.isEmpty(attention)){
                //    data.set("attention","0");
                //}

            }

        } catch (Exception e) {
            e.printStackTrace();
            String msg = "getDataError";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }
        dataMap.put("totalCount", totalCount);
        dataMap.put("dataList", dataList);

        return dataMap;
    }

    @Override
    public void changeResumeStatus(String guidkey, String opt) throws GeneralException {
        try {
            RecordVo vo = new RecordVo("z85");
            vo.setObject("z8501", guidkey);
            vo = dao.findByPrimaryKey(vo);
            //简历状态 默认为结束
            int z8503 = 0;
            //撤销
            if (StringUtils.equalsIgnoreCase(opt, "1")) {
                z8503 = 0;
            } else if (StringUtils.equalsIgnoreCase(opt, "2")) {
                z8503 = 1;
            }
            vo.setObject("z8503", z8503);
            dao.updateValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("changeResumeStatusError");
        }
    }

    @Override
    public void changeAttentionStatus(String z8501, String attention) throws GeneralException {
        try {
            RecordVo vo = this.autoAddRecord(z8501);
            vo.setObject("attention", attention);
            dao.updateValueObject(vo);
        } catch (Exception e) {
            String msg = "updataAttentionStatusError";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }

    }


    @Override
    public void changeApprovalStatus(String z8501, String approval) throws GeneralException {
        try {
            RecordVo vo = this.autoAddRecord(z8501);
            vo.setObject("approval", approval);
            dao.updateValueObject(vo);
            RecordVo z85Vo = new RecordVo("z85");
            z85Vo.setObject("z8501", z8501);
            z85Vo = dao.findByPrimaryKey(z85Vo);
            int z8509 = z85Vo.getInt("z8509");
            if (StringUtils.equalsIgnoreCase(approval, "1")) {
                z8509 = z8509 + 1;
            } else {
                z8509 = z8509 - 1;
            }
            z85Vo.setObject("z8509", z8509);
            dao.updateValueObject(z85Vo);
        } catch (Exception e) {
            String msg = "updataApprovalStatusError";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }
    }

    @Override
    public Map getInitParam() throws GeneralException {
        Map paramMap = new HashMap();
        String tabid = "";
        //申请模板id
        String talentDisplayTemplateId = TalentMarketsUtils.getTalentDisplayTemplate();
        //撤销模板id
        String cancelTemplateId = TalentMarketsUtils.getCancelTemplate();
        //是否是自助用户 默认是
        String applyFlag = "1";
        if (this.userView.getStatus() == 0 && StringUtils.isEmpty(this.userView.getA0100())) {
            applyFlag = "0";
        }
        String applyType = "";
        if (StringUtils.equalsIgnoreCase(applyFlag, "1")) {
            applyType = this.getApplyType();
            if (StringUtils.equalsIgnoreCase(applyType, "release")) {
                tabid = talentDisplayTemplateId;
            } else if (StringUtils.equalsIgnoreCase(applyType, "cancel")) {
                tabid = cancelTemplateId;
            }
        }
        String browseFlag = "0";
        if (this.userView.hasTheFunction("4010401")) {
            browseFlag = "1";
        }
        String removeFlag = "0";
        if (this.userView.hasTheFunction("4010402")) {
            removeFlag = "1";
        }
        //岗位类别
        String resumePostTypeField = TalentMarketsUtils.getResumePostTypeField();
        boolean isHaveResumePostTypeField = StringUtils.isNotEmpty(TalentMarketsUtils.getResumePostTypeField())?true:false;
        String cardid = TalentMarketsUtils.getTalentRname();
        paramMap.put("tabid", tabid);
        paramMap.put("applyType", applyType);
        paramMap.put("applyFlag", applyFlag);
        paramMap.put("browseFlag", browseFlag);
        paramMap.put("cardid", cardid);
        paramMap.put("removeFlag", removeFlag);
        paramMap.put("isHaveCard",this.userView.isHaveResource(IResourceConstant.CARD, cardid));
        paramMap.put("isHaveResumePostTypeField",isHaveResumePostTypeField);
        RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_LEVEL_CODE", this.conn);
        String postTypeFieldCodesetId = vo.getString("str_value");
        paramMap.put("postTypeFieldCodesetId",postTypeFieldCodesetId);
        return paramMap;
    }

    @Override
    public String getGridConfig(String viewType, String z8501) throws GeneralException {
        String gridConfigs = "";
        List columns = this.getGridColumns(viewType);
        TableConfigBuilder builder = new TableConfigBuilder("talentHallBrowseGrid", (ArrayList) columns, "talentHallBrowseGrid", this.userView, this.conn);
        builder.setDataSql(this.getGridDataSql(viewType, z8501));
        String orderSql = " order by browse_count desc";
        //if (StringUtils.equalsIgnoreCase(viewType, "browseDetails")) {
        //    orderSql = " order by browse_count desc";
        //} else if (StringUtils.equalsIgnoreCase(viewType, "browseTimes")) {
        //    orderSql = " order by browse_count desc";
        //}
        builder.setOrderBy(orderSql);
        gridConfigs = builder.createExtTableConfig();
        return gridConfigs;
    }

    @Override
    public void changeViewCount(String z8501) {
        try {
            RecordVo z85vo = new RecordVo("z85");
            z85vo.setObject("z8501", z8501);
            if (dao.isExistRecordVo(z85vo)) {
                z85vo = dao.findByPrimaryKey(z85vo);
                int z8507 = z85vo.getInt("z8507");
                z8507 = z8507 +1;
                z85vo.setObject("z8507",z8507);
                z85vo.setObject("z8511",new Date());
                dao.updateValueObject(z85vo);
            }
            RecordVo vo = this.autoAddRecord(z8501);
            int browse_count = vo.getInt("browse_count");
            browse_count = browse_count+1;
            vo.setObject("browse_count",browse_count);
            vo.setObject("browse_time", new Date());
            dao.updateValueObject(vo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取浏览详情页 columns
     *
     * @return
     * @throws GeneralException
     */
    private List getGridColumns(String viewType) throws GeneralException {
        ArrayList<ColumnsInfo> columnsInfoArrayList = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo;
        try {
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "a0101", DataDictionary.getFieldItem("a0101").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, null);
            columnsInfoArrayList.add(columnsInfo);
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "UN", "b0110", DataDictionary.getFieldItem("b0110").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, null);
            columnsInfoArrayList.add(columnsInfo);
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "UM", "e0122", DataDictionary.getFieldItem("e0122").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, null);
            columnsInfoArrayList.add(columnsInfo);
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "@K", "e01a1", DataDictionary.getFieldItem("e01a1").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, null);
            columnsInfoArrayList.add(columnsInfo);
            String desc = ResourceFactory.getProperty("talentmarkets.numberOfViews");
            if (StringUtils.equalsIgnoreCase(viewType, "browseTimes")) {
                desc = ResourceFactory.getProperty("talentmarkets.views");
            }
            Map param = new HashMap();
            param.put("summaryType", ColumnsInfo.SUMMARYTYPE_SUM);
            columnsInfo = TalentMarketsUtils.getColumnsInfo("N", "0", "browse_count", desc, ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, param);
            columnsInfoArrayList.add(columnsInfo);
            columnsInfo = TalentMarketsUtils.getColumnsInfo("A", "0", "browse_time", ResourceFactory.getProperty("talentmarkets.browseTime"), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 170, null);
            columnsInfoArrayList.add(columnsInfo);
        } catch (Exception e) {
            throw new GeneralException("");
        }
        return columnsInfoArrayList;
    }

    private String getGridDataSql(String viewType, String z8501) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        String loginTableStr = TalentMarketsUtils.getLoginTableStr();
        if (StringUtils.isNotBlank(loginTableStr)) {
            String[] loginTableArr = loginTableStr.split(",");
            String tableName = "";
            String guidKeyField = "";
            for (int i = 0; i < loginTableArr.length; i++) {
                String daPre = loginTableArr[i];
                sql.append("select b0110,e0122,e01a1,a0101");
                if (StringUtils.equalsIgnoreCase(viewType, "browseDetails")) {
                    tableName = "z85";
                    guidKeyField = "z8501";
                    sql.append(",z8507 browse_count,z8511 browse_time ");
                } else if (StringUtils.equalsIgnoreCase(viewType, "browseTimes")) {
                    tableName = "jp_browse_detail";
                    guidKeyField = "guidkey";
                    sql.append(",browse_count,browse_time");
                }
                sql.append(" from ").append(daPre).append("A01 a01 inner join  ");
                sql.append(tableName).append(" t on ").append("a01.guidkey = t.").append(guidKeyField);
                if (StringUtils.equalsIgnoreCase(viewType, "browseTimes")) {
                    sql.append(" where z8501 = ").append("'").append(z8501).append("'");
                }else if(StringUtils.equalsIgnoreCase(viewType,"browseDetails")){
                    sql.append(" where z8503 = '1'");
                }
                if (i < loginTableArr.length - 1) {
                    sql.append(" union all ");
                }
            }
        }
        return sql.toString();
    }

    private String getDataSql(MorphDynaBean queryValueDynaBean) throws GeneralException {
        Map queryValues = PubFunc.DynaBean2Map(queryValueDynaBean);
        StringBuffer sql = new StringBuffer();
        try {
            //岗位类别
            String resumePostTypeField = TalentMarketsUtils.getResumePostTypeField();
            StringBuffer filterSql = new StringBuffer();
            if (queryValues != null) {
                String b0110 = (String) queryValues.get("org");
                String a0101 = (String) queryValues.get("name");
                String jobCategory = "";
                if (queryValues.containsKey("jobCategory")) {
                    jobCategory = (String) queryValues.get("jobCategory");
                }
                filterSql.append(" and (1=1");
                if (StringUtils.isNotEmpty(b0110)) {
                    String realB0110 = b0110.split("`")[0];
                    filterSql.append(" and B0110 like '").append(realB0110).append("%'");
                }
                if (StringUtils.isNotEmpty(a0101)) {
                    filterSql.append(" and A0101 like '%").append(a0101).append("%'");
                }
                if(StringUtils.isNotEmpty(resumePostTypeField)){
                    if (StringUtils.isNotEmpty(jobCategory)) {
                        filterSql.append("and postTypeField = ").append("'").append(jobCategory.split("`")[0]).append("'");
                    }
                }
                filterSql.append(")");
            }
            StringBuffer partSql = new StringBuffer();
            //个人简介指标项 itemid
            String resumeSelfIntroduction = TalentMarketsUtils.getResumeSelfIntroduction();
            String loginTableStr = TalentMarketsUtils.getLoginTableStr();
            if (StringUtils.isNotBlank(loginTableStr)) {
                String[] loginTableArr = loginTableStr.split(",");
                for (int i = 0; i < loginTableArr.length; i++) {
                    String daPre = loginTableArr[i];
                    partSql.append("select ").append("'").append(daPre).append("' nbase,");
                    if (StringUtils.isNotEmpty(resumeSelfIntroduction)) {
                        partSql.append(resumeSelfIntroduction).append(" resumeSelfIntroduction").append(",");
                    }
                    if(StringUtils.isNotEmpty(resumePostTypeField)){
                        partSql.append("(select ").append(resumePostTypeField).append(" from k01 where e01a1 = ").append(daPre);
                        partSql.append("A01.e01a1) postTypeField,");
                    }
                    partSql.append(" A0100,B0110, A0101, E0122, E01A1,Z8507,Z8509,Z8503,Z8501,Z8505 from z85 ");
                    partSql.append("inner join ").append(daPre).append("A01 on z85.z8501 = ");
                    partSql.append(daPre).append("A01.guidkey");
                    partSql.append(" where z8503 = 1");
                    if (i < loginTableArr.length - 1) {
                        partSql.append(" union all ");
                    }
                }
            }
            sql.append("select nbase, A0100,B0110, A0101, E0122, E01A1,Z8503,t.Z8501,Z8505, ");
            sql.append(Sql_switcher.isnull("approval","0")).append(" approval");
            sql.append(",");
            sql.append(Sql_switcher.isnull("attention","0")).append(" attention");
            sql.append(",");
            sql.append(Sql_switcher.isnull("Z8507","0")).append(" Z8507");
            sql.append(",");
            sql.append(Sql_switcher.isnull("Z8509","0")).append(" Z8509");
            if(StringUtils.isNotEmpty(resumeSelfIntroduction)){
                sql.append(",resumeSelfIntroduction");
            }
            if(StringUtils.isNotEmpty(resumePostTypeField)){
                sql.append(",postTypeField");
            }
            sql.append(" from").append("(").append(partSql);
            sql.append(") t");
            sql.append(" left join jp_browse_detail on t.Z8501 = jp_browse_detail.Z8501 and guidkey = '");
            sql.append(this.userView.getGuidkey()).append("'");
            sql.append(" where 1 = 1");
            sql.append(filterSql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("getDataSqlError");
        }

        return sql.toString();
    }

    /**
     * 判断jp_browse_detail表中是否有当前登录用户浏览该简历人员（z8501）的记录
     * 如果没有则新增  以后改变其余字段时只做更新操作
     *
     * @param z8501
     * @return recordvo  当前记录对象
     */
    private RecordVo autoAddRecord(String z8501) throws GeneralException {
        RecordVo vo = new RecordVo("jp_browse_detail");
        vo.setObject("z8501", z8501);
        vo.setObject("guidkey", this.userView.getGuidkey());
        if (StringUtils.isEmpty(this.userView.getGuidkey())) {
            throw new GeneralException("guidkeyisnull");
        }
        try {
            //如果不存在则插入一条记录
            if (!dao.isExistRecordVo(vo)) {
                dao.addValueObject(vo);
            } else {
                vo = dao.findByPrimaryKey(vo);
            }
        } catch (Exception e) {
            throw new GeneralException("autoAddRecordError");
        }
        return vo;
    }

    /**
     * 查询当前登录用户的简历状态
     *
     * @return release 前台显示发布简历  cancel前台显示撤销简历
     * @throws GeneralException
     */
    private String getApplyType() throws GeneralException {
        //默认申请发布
        String applyType = "release";
        try {
            if (StringUtils.isEmpty(this.userView.getGuidkey())) {
                throw new GeneralException("guidkeyisnull");
            }
            RecordVo vo = new RecordVo("z85");
            vo.setObject("z8501", this.userView.getGuidkey());
            if (dao.isExistRecordVo(vo)) {
                vo = dao.findByPrimaryKey(vo);
                int z8503 = vo.getInt("z8503");
                if (z8503 == 1) {
                    applyType = "cancel";
                }
            }
        } catch (Exception e) {
            String msg = "";
            if (e instanceof GeneralException) {
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }
        return applyType;

    }
}
