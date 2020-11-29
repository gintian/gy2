package com.hjsj.hrms.module.recruitment.position.actionform;

import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * 
 * <p>Title: PositionForm </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-15 下午04:47:27</p>
 * @author xiongyy
 * @version 1.0
 */
public class PositionForm extends FrameForm{
   
    //职位列表表头
    private ArrayList positionColumn = new ArrayList();
    //职位头信息
    private ArrayList positionInfo = new ArrayList();
    //职位列表sql 用在前台控件展示数据
    private String strsql;
    //查询方案
    private ArrayList queryList = new ArrayList();
    //用来返回时 传来的查询条件
    private String searchStr = "";
    
    private Pageable pageable = new Pageable();
    //生成页面数据json格式的字符串
    private String jsonStr = "";
    
    private String ordersql="";
    //第几页
    private int pageNum=1;
    //每页显示的页数
    private int pagesize=20;
    //返回后如果有查询的方案 那得给方案加下划线
    private String changeIds="";
    //=1进入职位详情 =2进入候选人管理
    private String sign="";
    //招聘负责人名
    private String responsPosiName;
    //招聘负责人id;
    private String reponsA0100;
    //招聘负责人照片url
    private String photosrc;
    //状态
    private String z0319;
    //返回条件
    private String pageDesc = "";
    //=y显示保存&发布 =n不显示
    private String isPublish="";
    //有值的时候就返回到简历中心 没有值的时候就返回到 职位列表
    private String from = "";
    //功能按钮
    private ArrayList buttonList = new ArrayList();
    //推荐职位的人员
    private String a0100s ="";
    
    private String max_count = "";
    //当前职位下是否存在人员
    private String havaPerson = "";
    //是否继续创建职位  true 是；false 否
    private String iscontinue = "false";
    //登录用户权限范围内的招聘渠道
    private String privChannel = "";
    //报名起始时间指标
    private String zp_pos_apply_start_field = "";
    //报名截止时间指标
    private String zp_pos_apply_end_field  = "";
    //招聘职位种招聘成员显示状态
    private String display = "block";
    
    
    public String getZp_pos_apply_start_field() {
		return zp_pos_apply_start_field;
	}

    public void setZp_pos_apply_start_field(String zp_pos_apply_start_field) {
		this.zp_pos_apply_start_field = zp_pos_apply_start_field;
	}

    public String getZp_pos_apply_end_field() {
		return zp_pos_apply_end_field;
	}

    public void setZp_pos_apply_end_field(String zp_pos_apply_end_field) {
		this.zp_pos_apply_end_field = zp_pos_apply_end_field;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getPrivChannel() {
		return privChannel;
	}

	public void setPrivChannel(String privChannel) {
		this.privChannel = privChannel;
	}

	public String getMax_count() {
        return max_count;
    }

    public void setMax_count(String max_count) {
        this.max_count = max_count;
    }

    public String getA0100s() {
        return a0100s;
    }

    public void setA0100s(String a0100s) {
        this.a0100s = a0100s;
    }

    public ArrayList getButtonList() {
        return buttonList;
    }

    public void setButtonList(ArrayList buttonList) {
        this.buttonList = buttonList;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public String getPageDesc() {
        return pageDesc;
    }

    public void setPageDesc(String pageDesc) {
        this.pageDesc = pageDesc;
    }

    public String getZ0319() {
        return z0319;
    }

    public void setZ0319(String z0319) {
        this.z0319 = z0319;
    }

    public String getResponsPosiName() {
        return responsPosiName;
    }

    public void setResponsPosiName(String responsPosiName) {
        this.responsPosiName = responsPosiName;
    }

    public String getReponsA0100() {
        return reponsA0100;
    }

    public void setReponsA0100(String reponsA0100) {
        this.reponsA0100 = reponsA0100;
    }

    public String getPhotosrc() {
        return photosrc;
    }

    public void setPhotosrc(String photosrc) {
        this.photosrc = photosrc;
    }

    public String getSearchStr() {
        return searchStr;
    }
    
    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getChangeIds() {
        return changeIds;
    }

    public void setChangeIds(String changeIds) {
        this.changeIds = changeIds;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
    
    public ArrayList getQueryList() {
        return queryList;
    }

    public void setQueryList(ArrayList queryList) {
        this.queryList = queryList;
    }

    public String getStrsql() {
        return strsql;
    }

    public void setStrsql(String strsql) {
        this.strsql = strsql;
    }

    public ArrayList getPositionColumn() {
        return positionColumn;
    }

    public void setPositionColumn(ArrayList positionColumn) {
        this.positionColumn = positionColumn;
    }
    
   

    public ArrayList getPositionInfo() {
        return positionInfo;
    }

    public void setPositionInfo(ArrayList positionInfo) {
        this.positionInfo = positionInfo;
    }

    @Override
    public void outPutFormHM() {
        // TODO Auto-generated method stub
        this.setPositionColumn((ArrayList)this.getFormHM().get("positionColumn"));
        this.setStrsql((String)this.getFormHM().get("strsql"));
        this.setQueryList((ArrayList)this.getFormHM().get("queryList"));
        this.setJsonStr((String)this.getFormHM().get("jsonStr"));
        this.setPositionInfo((ArrayList)this.getFormHM().get("positionInfo"));
        this.setOrdersql((String)this.getFormHM().get("ordersql"));
        this.setChangeIds((String)this.getFormHM().get("changeIds"));
        this.setSign((String)this.getFormHM().get("sign"));
        this.setReponsA0100((String)this.getFormHM().get("reponsA0100"));
        this.setResponsPosiName((String)this.getFormHM().get("responsPosiName"));
        this.setPhotosrc((String)this.getFormHM().get("photosrc"));
        this.setZ0319((String)this.getFormHM().get("z0319"));
        this.setPageDesc((String)this.getFormHM().get("pageDesc"));
        this.setIsPublish((String)this.getFormHM().get("isPublish"));
        this.setFrom((String)this.getFormHM().get("from"));
        this.setButtonList((ArrayList)this.getFormHM().get("buttonList"));
        this.setA0100s((String)this.getFormHM().get("a0100s"));
        this.setMax_count((String)this.getFormHM().get("max_count"));
        this.setHavaPerson((String)this.getFormHM().get("havaPerson"));
        this.setIscontinue((String)this.getFormHM().get("iscontinue"));
        this.setSearchStr((String)this.getFormHM().get("searchStr"));
        this.setPrivChannel((String) this.getFormHM().get("privChannel"));
        this.setDisplay((String) this.getFormHM().get("display"));
        this.setZp_pos_apply_start_field((String) this.getFormHM().get("zp_pos_apply_start_field"));
        this.setZp_pos_apply_end_field((String) this.getFormHM().get("zp_pos_apply_end_field"));
    }

    public String getOrdersql() {
        return ordersql;
    }

    public void setOrdersql(String ordersql) {
        this.ordersql = ordersql;
    }

    @Override
    public void inPutTransHM() {
        // TODO Auto-generated method stub
    	this.getFormHM().put("positionInfo",positionInfo);
    	this.getFormHM().put("a0100s", this.getA0100s());
    }

	public void setHavaPerson(String havaPerson) {
		this.havaPerson = havaPerson;
	}

	public String getHavaPerson() {
		return havaPerson;
	}

	public void setIscontinue(String iscontinue) {
		this.iscontinue = iscontinue;
	}

	public String getIscontinue() {
		return iscontinue;
	}

}
