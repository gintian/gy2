package com.hjsj.hrms.module.recruitment.resumecenter.actionform;

import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ResumeForm extends FrameForm {
	
	private LazyDynaBean evaluationBean;//我的评价
	private ArrayList evaluation;//简历评价
	private String rootPath;   //文件上传根路径
	private String resumeid;   //简历id
	private String nbase;      //人员库前缀
	private String zp_pos_id;   //岗位序号
	private Integer current;    //页码
	private Integer pagesize;   //每页条数
	private Integer rowindex;   //当前简历在当页所处的位置
	private String username;   //用户名
	private String email;      //用户邮箱帐号
	private String recdate;   //创建时间
	private String original;      //简历来源 （待定） 猎头推荐   内部推荐
	private String status;    //简历状态   0：未处理   1：接受   2：拒绝
	private String schemeValues;  //应聘情况栏相应值
	private String isTalent;   //判断是否已加入人才库
	private String jsonStr;   //简历详情页面已上传文件
	
	private ArrayList lastPos;  //最近应聘职位
	private ArrayList othPos;  //其它应聘职位
	
	private Integer nextCurrent;  //下个人员的页码
	private Integer nextPagesize;   //每页条数
	private Integer nextRowindex;   //下个简历在当页所处的位置
	private String nextResumeid;   //下个人员的简历id
	private String nextNbase;     //下个人员的人员库前缀
	private String nextZp_pos_id;  //下个人员的岗位序号
	private Integer lastCurrent;  //上个人员的页码
	private Integer lastPagesize;   //每页条数
	private Integer lastRowindex;   //上个简历在当页所处的位置
	private String lastResumeid;   //上个人员的简历id
	private String lastNbase;     //上个人员的人员库前缀
	private String lastZp_pos_id;  //上个人员的岗位序号
	
	private ArrayList fieldSetList;   //指标集
	private HashMap resumeBrowseSetMap; //应聘者各子集里的信息集合
    private HashMap setShowFieldMap; //子集显示 列 map
	
    private String from="resumeCenter";  // resumeCenter:简历中心  talents:人才库
    private String isAttach="";  // 
    private LazyDynaBean infoBean=new LazyDynaBean();
    private ArrayList uploadFileList; //简历中上传的附件信息
    private ArrayList operationList;//人员简历操作信息
    
    private String isMine; //是否是我的人才库中的人员
    private String next_linkId="";
    private String next_nodeId = "";
	private ArrayList stageList = new ArrayList();//流程阶段信息集合
	private ArrayList projectList = new ArrayList();//查询方案集合
	private String node_flag = "";
	public String flag = "";	//flag=1 不允许评价
	
	private String skipFlag="1"; //招聘环节是否必须顺序进行
	private ArrayList skiplist = new ArrayList();//可以操作的流程阶段集合
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("resumeid", resumeid);
		this.getFormHM().put("nbase", nbase);
		this.getFormHM().put("zp_pos_id", zp_pos_id);
		this.getFormHM().put("current", current);
		this.getFormHM().put("pagesize", pagesize);
		this.getFormHM().put("rowindex", rowindex);
		this.getFormHM().put("from", from);
		this.getFormHM().put("schemeValues", schemeValues);
		this.getFormHM().put("isAttach", isAttach);
		this.getFormHM().put("uploadFileList", uploadFileList);
		this.getFormHM().put("operationList", operationList);
		this.getFormHM().put("infoBean", infoBean);
		this.getFormHM().put("isMine", isMine);
		this.getFormHM().put("stageList", stageList);
		this.getFormHM().put("projectList", projectList);
		this.getFormHM().put("evaluation", evaluation);
		this.getFormHM().put("evaluationBean", evaluationBean);
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("skipFlag",skipFlag);
		this.getFormHM().put("skiplist",skiplist);
		
	}

	@Override
    public void outPutFormHM() {
		HashMap hm = this.getFormHM();
		this.setEvaluationBean((LazyDynaBean) hm.get("evaluationBean"));
		this.setEvaluation((ArrayList) hm.get("evaluation"));
		this.setRootPath((String)hm.get("rootPath"));
		this.setResumeid((String)hm.get("resumeid"));
		this.setNbase((String)hm.get("nbase"));
		this.setZp_pos_id((String)hm.get("zp_pos_id"));
		this.setCurrent((Integer)hm.get("current"));
		this.setPagesize((Integer)hm.get("pagesize"));
		this.setRowindex((Integer)hm.get("rowindex"));
		this.setUsername((String)hm.get("username"));
		this.setEmail((String)hm.get("email"));
		this.setRecdate((String)hm.get("recdate"));
		this.setOriginal((String)hm.get("original"));
		this.setStatus((String)hm.get("status"));
		this.setIsTalent((String)hm.get("isTalent"));
		this.setJsonStr((String)hm.get("jsonStr"));
		
		this.setLastPos((ArrayList)hm.get("lastPos"));
		this.setOthPos((ArrayList)hm.get("othPos"));
		this.setNextCurrent((Integer)hm.get("nextCurrent"));
		this.setNextPagesize((Integer)hm.get("nextPagesize"));
		this.setNextRowindex((Integer)hm.get("nextRowindex"));
		this.setNextResumeid((String)hm.get("nextResumeid"));
		this.setNextNbase((String)hm.get("nextNbase"));
		this.setNextZp_pos_id((String)hm.get("nextZp_pos_id"));
		this.setLastCurrent((Integer)hm.get("lastCurrent"));
		this.setLastPagesize((Integer)hm.get("lastPagesize"));
		this.setLastRowindex((Integer)hm.get("lastRowindex"));
		this.setLastResumeid((String)hm.get("lastResumeid"));
		this.setLastNbase((String)hm.get("lastNbase"));
		this.setLastZp_pos_id((String)hm.get("lastZp_pos_id"));
		this.setFieldSetList((ArrayList)hm.get("fieldSetList"));
		this.setResumeBrowseSetMap((HashMap)hm.get("resumeBrowseSetMap"));
		this.setSetShowFieldMap((HashMap)hm.get("setShowFieldMap"));
		this.setFrom((String)hm.get("from"));
		this.setIsAttach((String)hm.get("isAttach"));
		this.setUploadFileList((ArrayList)hm.get("uploadFileList"));
		this.setOperationList((ArrayList)hm.get("operationList"));
		this.setInfoBean((LazyDynaBean)hm.get("infoBean"));
		this.setIsMine((String)hm.get("isMine"));
		this.setStageList((ArrayList)this.getFormHM().get("stageList"));
		this.setProjectList((ArrayList)this.getFormHM().get("projectList"));
		this.setNext_linkId((String)this.getFormHM().get("next_linkId"));
		this.setNext_nodeId((String)this.getFormHM().get("next_nodeId"));
		this.setNode_flag((String)this.getFormHM().get("node_flag"));
		this.setFlag((String)this.getFormHM().get("flag"));
		
		this.setSkipFlag((String)this.getFormHM().get("skipFlag"));
		this.setSkiplist((ArrayList)this.getFormHM().get("skiplist"));
		hm = null;
	}

	public String getResumeid() {
		return resumeid;
	}

	public void setResumeid(String resumeid) {
		this.resumeid = resumeid;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getZp_pos_id() {
		return zp_pos_id;
	}

	public void setZp_pos_id(String zp_pos_id) {
		this.zp_pos_id = zp_pos_id;
	}

	public Integer getCurrent() {
		return current;
	}

	public void setCurrent(Integer current) {
		this.current = current;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	public Integer getRowindex() {
		return rowindex;
	}

	public void setRowindex(Integer rowindex) {
		this.rowindex = rowindex;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRecdate() {
		return recdate;
	}

	public void setRecdate(String recdate) {
		this.recdate = recdate;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSchemeValues() {
		return schemeValues;
	}

	public void setSchemeValues(String schemeValues) {
		this.schemeValues = schemeValues;
	}

	public String getIsTalent() {
		return isTalent;
	}

	public void setIsTalent(String isTalent) {
		this.isTalent = isTalent;
	}

	public ArrayList getLastPos() {
		return lastPos;
	}

	public void setLastPos(ArrayList lastPos) {
		this.lastPos = lastPos;
	}

	public ArrayList getOthPos() {
		return othPos;
	}

	public void setOthPos(ArrayList othPos) {
		this.othPos = othPos;
	}

	public Integer getNextCurrent() {
		return nextCurrent;
	}

	public void setNextCurrent(Integer nextCurrent) {
		this.nextCurrent = nextCurrent;
	}

	public Integer getNextPagesize() {
		return nextPagesize;
	}

	public void setNextPagesize(Integer nextPagesize) {
		this.nextPagesize = nextPagesize;
	}

	public Integer getNextRowindex() {
		return nextRowindex;
	}

	public void setNextRowindex(Integer nextRowindex) {
		this.nextRowindex = nextRowindex;
	}

	public String getNextResumeid() {
		return nextResumeid;
	}

	public void setNextResumeid(String nextResumeid) {
		this.nextResumeid = nextResumeid;
	}

	public String getNextNbase() {
		return nextNbase;
	}

	public void setNextNbase(String nextNbase) {
		this.nextNbase = nextNbase;
	}

	public String getNextZp_pos_id() {
		return nextZp_pos_id;
	}

	public void setNextZp_pos_id(String nextZp_pos_id) {
		this.nextZp_pos_id = nextZp_pos_id;
	}

	public Integer getLastCurrent() {
		return lastCurrent;
	}

	public void setLastCurrent(Integer lastCurrent) {
		this.lastCurrent = lastCurrent;
	}

	public Integer getLastPagesize() {
		return lastPagesize;
	}

	public void setLastPagesize(Integer lastPagesize) {
		this.lastPagesize = lastPagesize;
	}

	public Integer getLastRowindex() {
		return lastRowindex;
	}

	public void setLastRowindex(Integer lastRowindex) {
		this.lastRowindex = lastRowindex;
	}

	public String getLastResumeid() {
		return lastResumeid;
	}

	public void setLastResumeid(String lastResumeid) {
		this.lastResumeid = lastResumeid;
	}

	public String getLastNbase() {
		return lastNbase;
	}

	public void setLastNbase(String lastNbase) {
		this.lastNbase = lastNbase;
	}

	public String getLastZp_pos_id() {
		return lastZp_pos_id;
	}

	public void setLastZp_pos_id(String lastZp_pos_id) {
		this.lastZp_pos_id = lastZp_pos_id;
	}

	public HashMap getResumeBrowseSetMap() {
		return resumeBrowseSetMap;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public void setResumeBrowseSetMap(HashMap resumeBrowseSetMap) {
		this.resumeBrowseSetMap = resumeBrowseSetMap;
	}

	public HashMap getSetShowFieldMap() {
		return setShowFieldMap;
	}

	public void setSetShowFieldMap(HashMap setShowFieldMap) {
		this.setShowFieldMap = setShowFieldMap;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

    public String getIsAttach() {
        return isAttach;
    }

    public void setIsAttach(String isAttach) {
        this.isAttach = isAttach;
    }

    public ArrayList getUploadFileList() {
        return uploadFileList;
    }

    public void setUploadFileList(ArrayList uploadFileList) {
        this.uploadFileList = uploadFileList;
    }

	public void setOperationList(ArrayList operationList) {
		this.operationList = operationList;
	}

	public ArrayList getOperationList() {
		return operationList;
	}

	public void setInfoBean(LazyDynaBean infoBean) {
		this.infoBean = infoBean;
	}

	public LazyDynaBean getInfoBean() {
		return infoBean;
	}


    public String getIsMine() {
        return isMine;
    }

    public void setIsMine(String isMine) {
        this.isMine = isMine;
    }
	public String getNext_linkId() {
		return next_linkId;
	}

	public void setNext_linkId(String nextLinkId) {
		next_linkId = nextLinkId;
	}

	public ArrayList getStageList() {
		return stageList;
	}

	public void setStageList(ArrayList stageList) {
		this.stageList = stageList;
	}

	public ArrayList getProjectList() {
		return projectList;
	}

	public void setProjectList(ArrayList projectList) {
		this.projectList = projectList;
	}

	public void setNext_nodeId(String next_nodeId) {
		this.next_nodeId = next_nodeId;
	}

	public String getNext_nodeId() {
		return next_nodeId;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setEvaluation(ArrayList evaluation) {
		this.evaluation = evaluation;
	}

	public ArrayList getEvaluation() {
		return evaluation;
	}

	public void setEvaluationBean(LazyDynaBean evaluationBean) {
		this.evaluationBean = evaluationBean;
	}

	public LazyDynaBean getEvaluationBean() {
		return evaluationBean;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public void setNode_flag(String node_flag) {
		this.node_flag = node_flag;
	}

	public String getNode_flag() {
		return node_flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getSkipFlag() {
		return skipFlag;
	}

	public void setSkipFlag(String skipFlag) {
		this.skipFlag = skipFlag;
	}

	public ArrayList getSkiplist() {
		return skiplist;
	}

	public void setSkiplist(ArrayList skiplist) {
		this.skiplist = skiplist;
	}
}
