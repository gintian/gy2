package com.hjsj.hrms.actionform.general.relation;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class GenRelationForm extends FrameForm {
    private PaginationForm setlistform     = new PaginationForm();
    private ArrayList      setlist         = new ArrayList();
    private RecordVo       checkrelationvo = null;
    private String         relationid      = "";
    private String         relying         = "";                   //依赖关系
    private String         default_line    = "";                   //主汇报关系
    private String         actor_type      = "";
    private ArrayList      relyingList     = new ArrayList();      //依赖关系列表
    private String         relyingId       = "";
    private String         isDefault       = "";                  //是否主控线  1：主
    private String         codeset         = "";                   //机构树中带过来的codeset，组织机构结点没有
    private String         code            = "";                   //机构树中带过来的code，组织机构结点没有
    private String         operate         = "";                   //机构树中带过来的operate，组织机构结点没有

    //zxj 20141011 部门显示层级
    private String         uplevel         = "";

    /** 
     * @return codeset 
     */
    public String getCodeset() {
        return codeset;
    }

    /** 
     * @param codeset 要设置的 codeset 
     */
    public void setCodeset(String codeset) {
        this.codeset = codeset;
    }

    /** 
     * @return code 
     */
    public String getCode() {
        return code;
    }

    /** 
     * @param code 要设置的 code 
     */
    public void setCode(String code) {
        this.code = code;
    }

    /** 
     * @return operate 
     */
    public String getOperate() {
        return operate;
    }

    /** 
     * @param operate 要设置的 operate 
     */
    public void setOperate(String operate) {
        this.operate = operate;
    }

    private String         isshowbutton      = "";

    ArrayList              genObjects        = new ArrayList();
    ArrayList              genMainbodys      = new ArrayList();
    ArrayList              objectTypes       = new ArrayList();
    ArrayList              bodyTypes         = new ArrayList();
    ArrayList              allBodyTypes      = new ArrayList();
    ArrayList              allObjectTypes    = new ArrayList();

    private PaginationForm genObjectForm     = new PaginationForm();
    private PaginationForm genMainbodyForm   = new PaginationForm();
    private String         a_code;
    private String         paramStr;
    private String[]       objectID          = null;                 //删除时候用，用于复选框的标识
    private String[]       mainbodyID        = null;                 //删除时候用，用于复选框的标识
    private ArrayList      khObjectList      = new ArrayList();
    private String         khObject;
    private ArrayList      mainbodys         = new ArrayList();      //指定考核主体时候用
    private String         khObjectCopyed;                           //复制主体时选中的考核对象
    private String         left_fields[];
    private String         right_fields[];
    private ArrayList      leftlist          = new ArrayList();
    private ArrayList      selectedFieldList = new ArrayList();
    private String         selfBodyId        = "";                   //本人主体类别ID
    private String         objSelected;
    private String         enableFlag;
    private HashMap        joinedObjs        = new HashMap();
    private ArrayList      objectTypeList    = new ArrayList();     // 对象类别
    private ArrayList      dblist            = new ArrayList();
    private String         dbpre             = "";
    /**姓名，按姓名快速查找定位*/
    private String         a0101;
    private String         selectid;                                 //定位的考核对象
    private String         usergentree;                              //业务人员树
    private String         select_copy;                              //选择的人员是否被选用
    /** 田野 2013-02-27 添加手动选人添加审批对象时查询应用库的数据的标记approvalRelation**/
    private String         approvalRelation;
    private String         isDelMainbody     = "";                   //删除审批主体时，要刷新审批对象这个页面。但审批页面有个变量operation。此变量为了控制operation。郭峰

    @Override
    public void outPutFormHM() {
        this.setRelyingList((ArrayList) this.getFormHM().get("relyingList"));
        this.setRelyingId((String) this.getFormHM().get("relyingId"));
        this.setIsDefault((String) this.getFormHM().get("isDefault"));

        this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
        this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
        this.setRelationid((String) this.getFormHM().get("relationid"));
        this.setCheckrelationvo((RecordVo) this.getFormHM().get("checkrelationvo"));

        this.setObjectTypeList((ArrayList) this.getFormHM().get("objectTypeList"));
        this.setReturnflag((String) this.getFormHM().get("returnflag"));
        this.setAllObjectTypes((ArrayList) this.getFormHM().get("allObjectTypes"));
        this.setAllBodyTypes((ArrayList) this.getFormHM().get("allBodyTypes"));
        this.setJoinedObjs((HashMap) this.getFormHM().get("joinedObjs"));
        this.setGenObjects((ArrayList) this.getFormHM().get("genObjects"));
        this.setGenMainbodys((ArrayList) this.getFormHM().get("genMainbodys"));
        this.setObjectTypes((ArrayList) this.getFormHM().get("objectTypes"));
        this.setBodyTypes((ArrayList) this.getFormHM().get("bodyTypes"));
        this.setA_code((String) this.getFormHM().get("a_code"));

        this.getGenMainbodyForm().setList((ArrayList) this.getFormHM().get("genMainbodys"));
        this.getGenObjectForm().setList((ArrayList) this.getFormHM().get("genObjects"));
        this.setParamStr((String) this.getFormHM().get("paramStr"));
        this.setObjectID((String[]) this.getFormHM().get("objectID"));
        this.setMainbodyID((String[]) this.getFormHM().get("mainbodyID"));
        this.setKhObject((String) this.getFormHM().get("khObject"));
        this.setKhObjectList((ArrayList) this.getFormHM().get("khObjectList"));
        this.setMainbodys((ArrayList) this.getFormHM().get("mainbodys"));
        this.setKhObjectCopyed((String) this.getFormHM().get("khObjectCopyed"));
        this.setLeftlist((ArrayList) this.getFormHM().get("leftlist"));
        this.setLeft_fields((String[]) this.getFormHM().get("left_fields"));
        this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
        this.setSelectedFieldList((ArrayList) this.getFormHM().get("selectedFieldList"));
        this.setSelfBodyId((String) this.getFormHM().get("selfBodyId"));
        this.setObjSelected((String) this.getFormHM().get("objSelected"));
        this.setEnableFlag((String) this.getFormHM().get("enableFlag"));
        this.setDbpre((String) this.getFormHM().get("dbpre"));
        this.setDblist((ArrayList) this.getFormHM().get("dblist"));
        this.setActor_type((String) this.getFormHM().get("actor_type"));
        this.setUsergentree((String) this.getFormHM().get("usergentree"));
        this.setA0101((String) this.getFormHM().get("a0101"));
        this.setSelect_copy((String) this.getFormHM().get("select_copy"));
        this.setA0101((String) this.getFormHM().get("a0101"));
        this.setRelying((String) this.getFormHM().get("relying"));
        this.setDefault_line((String) this.getFormHM().get("default_line"));
        this.setIsshowbutton((String) this.getFormHM().get("isshowbutton"));
        this.setApprovalRelation((String) this.getFormHM().get("approvalRelation"));
        this.setIsDelMainbody((String) this.getFormHM().get("isDelMainbody"));
        this.setCodeset((String) this.getFormHM().get("codeset"));
        this.setCode((String) this.getFormHM().get("code"));
        this.setOperate((String) this.getFormHM().get("operate"));

        this.setUplevel((String) this.getFormHM().get("uplevel"));
    }

    @Override
    public void inPutTransHM() {
        this.getFormHM().put("relyingList", this.getRelyingList());

        this.getFormHM().put("isDefault", this.getIsDefault());
        this.getFormHM().put("relyingId", this.getRelyingId());

        this.getFormHM().put("objectTypeList", this.getObjectTypeList());
        this.getFormHM().put("allObjectTypes", this.getAllObjectTypes());
        this.getFormHM().put("allBodyTypes", this.getAllBodyTypes());
        this.getFormHM().put("genObjects", this.getGenObjects());
        this.getFormHM().put("genMainbodys", this.getGenMainbodys());
        this.getFormHM().put("objectTypes", this.getObjectTypes());
        this.getFormHM().put("bodyTypes", this.getBodyTypes());
        this.getFormHM().put("a_code", this.getA_code());
        this.getFormHM().put("paramStr", this.getParamStr());
        this.getFormHM().put("objectID", this.getObjectID());
        this.getFormHM().put("mainbodyID", this.getMainbodyID());
        this.getFormHM().put("khObjectList", this.getKhObjectList());
        this.getFormHM().put("khObject", this.getKhObject());
        this.getFormHM().put("mainbodys", this.getMainbodys());
        this.getFormHM().put("khObjectCopyed", this.getKhObjectCopyed());
        this.getFormHM().put("left_fields", this.getLeft_fields());
        this.getFormHM().put("right_fields", this.getRight_fields());
        this.getFormHM().put("selectedFieldList", this.getSelectedFieldList());
        this.getFormHM().put("selfBodyId", this.getSelfBodyId());
        this.getFormHM().put("objSelected", this.getObjSelected());
        this.getFormHM().put("enableFlag", this.getEnableFlag());
        this.getFormHM().put("joinedObjs", this.getJoinedObjs());
        this.getFormHM().put("dbpre", this.getDbpre());
        this.getFormHM().put("a0101", this.getA0101());
        this.getFormHM().put("selectid", this.getSelectid());
        this.getFormHM().put("select_copy", this.getSelect_copy());
        this.getFormHM().put("relying", this.getRelying());
        this.getFormHM().put("default_line", this.getDefault_line());
        this.getFormHM().put("isshowbutton", this.getIsshowbutton());
        this.getFormHM().put("approvalRelation", this.getApprovalRelation());
        this.getFormHM().put("isDelMainbody", this.getIsDelMainbody());
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

        return super.validate(arg0, arg1);
    }

    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        //this.setRet_ctrl("0");
        //this.setA0101("");
        this.setSelect_copy("");

        if ("/general/relation/relationmaintence".equals(arg0.getPath()) && arg1.getParameter("b_int") != null&& "link".equals(arg1.getParameter("b_int"))) {
            /**定位到首页,*/
            if (this.getGenObjectForm().getPagination() != null)
                this.getGenObjectForm().getPagination().firstPage();
        }
        if ("/general/relation/relationobjectlist".equals(arg0.getPath()) && arg1.getParameter("b_query") != null&& "link".equals(arg1.getParameter("b_query"))) {
            /**定位到首页,*/
            if (this.getGenObjectForm().getPagination() != null)
                this.getGenObjectForm().getPagination().firstPage();
        }
        //this.setA0101("");
        if ("/general/relation/relationmainbodylist".equals(arg0.getPath()) && arg1.getParameter("b_queryBody") != null&& "link".equals(arg1.getParameter("b_queryBody"))) {
            /**定位到首页,*/
            if (this.getGenMainbodyForm().getPagination() != null)
                this.getGenMainbodyForm().getPagination().firstPage();
        }
        super.reset(arg0, arg1);
    }

    public PaginationForm getSetlistform() {
        return setlistform;
    }

    public void setSetlistform(PaginationForm setlistform) {
        this.setlistform = setlistform;
    }

    public ArrayList getSetlist() {
        return setlist;
    }

    public void setSetlist(ArrayList setlist) {
        this.setlist = setlist;
    }

    public String getRelationid() {
        return relationid;
    }

    public void setRelationid(String relationid) {
        this.relationid = relationid;
    }

    public ArrayList getObjectTypes() {

        return objectTypes;
    }

    public void setObjectTypes(ArrayList objectTypes) {

        this.objectTypes = objectTypes;
    }

    public ArrayList getGenMainbodys() {

        return genMainbodys;
    }

    public void setGenMainbodys(ArrayList genMainbodys) {

        this.genMainbodys = genMainbodys;
    }

    public ArrayList getGenObjects() {

        return genObjects;
    }

    public void setGenObjects(ArrayList genObjects) {

        this.genObjects = genObjects;
    }

    public String getA_code() {

        return a_code;
    }

    public void setA_code(String a_code) {

        this.a_code = a_code;
    }

    public PaginationForm getGenMainbodyForm() {

        return genMainbodyForm;
    }

    public void setGenMainbodyForm(PaginationForm genMainbodyForm) {

        this.genMainbodyForm = genMainbodyForm;
    }

    public PaginationForm getGenObjectForm() {

        return genObjectForm;
    }

    public void setGenObjectForm(PaginationForm genObjectForm) {

        this.genObjectForm = genObjectForm;
    }

    public String getParamStr() {

        return paramStr;
    }

    public void setParamStr(String paramStr) {

        this.paramStr = paramStr;
    }

    public String[] getMainbodyID() {

        return mainbodyID;
    }

    public void setMainbodyID(String[] mainbodyID) {

        this.mainbodyID = mainbodyID;
    }

    public String[] getObjectID() {

        return objectID;
    }

    public void setObjectID(String[] objectID) {

        this.objectID = objectID;
    }

    public String getKhObject() {

        return khObject;
    }

    public void setKhObject(String khObject) {

        this.khObject = khObject;
    }

    public ArrayList getKhObjectList() {

        return khObjectList;
    }

    public void setKhObjectList(ArrayList khObjectList) {

        this.khObjectList = khObjectList;
    }

    public ArrayList getMainbodys() {

        return mainbodys;
    }

    public void setMainbodys(ArrayList mainbodys) {

        this.mainbodys = mainbodys;
    }

    public String getKhObjectCopyed() {

        return khObjectCopyed;
    }

    public void setKhObjectCopyed(String khObjectCopyed) {

        this.khObjectCopyed = khObjectCopyed;
    }

    public String[] getLeft_fields() {

        return left_fields;
    }

    public void setLeft_fields(String[] left_fields) {

        this.left_fields = left_fields;
    }

    public ArrayList getLeftlist() {

        return leftlist;
    }

    public void setLeftlist(ArrayList leftlist) {

        this.leftlist = leftlist;
    }

    public String[] getRight_fields() {

        return right_fields;
    }

    public void setRight_fields(String[] right_fields) {

        this.right_fields = right_fields;
    }

    public ArrayList getSelectedFieldList() {

        return selectedFieldList;
    }

    public void setSelectedFieldList(ArrayList selectedFieldList) {

        this.selectedFieldList = selectedFieldList;
    }

    public ArrayList getBodyTypes() {

        return bodyTypes;
    }

    public void setBodyTypes(ArrayList bodyTypes) {

        this.bodyTypes = bodyTypes;
    }

    public String getSelfBodyId() {

        return selfBodyId;
    }

    public void setSelfBodyId(String selfBodyId) {

        this.selfBodyId = selfBodyId;
    }

    public String getObjSelected() {

        return objSelected;
    }

    public void setObjSelected(String objSelected) {

        this.objSelected = objSelected;
    }

    public String getSelect_copy() {
        return select_copy;
    }

    public void setSelect_copy(String select_copy) {
        this.select_copy = select_copy;
    }

    public String getEnableFlag() {

        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {

        this.enableFlag = enableFlag;
    }

    public HashMap getJoinedObjs() {
        return joinedObjs;
    }

    public void setJoinedObjs(HashMap joinedObjs) {
        this.joinedObjs = joinedObjs;
    }

    public ArrayList getAllBodyTypes() {
        return allBodyTypes;
    }

    public void setAllBodyTypes(ArrayList allBodyTypes) {
        this.allBodyTypes = allBodyTypes;
    }

    public ArrayList getAllObjectTypes() {
        return allObjectTypes;
    }

    public void setAllObjectTypes(ArrayList allObjectTypes) {
        this.allObjectTypes = allObjectTypes;
    }

    public ArrayList getObjectTypeList() {
        return objectTypeList;
    }

    public void setObjectTypeList(ArrayList objectTypeList) {
        this.objectTypeList = objectTypeList;
    }

    public RecordVo getCheckrelationvo() {
        return checkrelationvo;
    }

    public ArrayList getDblist() {
        return dblist;
    }

    public void setDblist(ArrayList dblist) {
        this.dblist = dblist;
    }

    public String getDbpre() {
        return dbpre;
    }

    public void setDbpre(String dbpre) {
        this.dbpre = dbpre;
    }

    public String getA0101() {
        return a0101;
    }

    public void setA0101(String a0101) {
        this.a0101 = a0101;
    }

    public String getActor_type() {
        return actor_type;
    }

    public void setActor_type(String actor_type) {
        this.actor_type = actor_type;
    }

    public String getSelectid() {
        return selectid;
    }

    public void setSelectid(String selectid) {
        this.selectid = selectid;
    }

    public String getUsergentree() {
        return usergentree;
    }

    public void setUsergentree(String usergentree) {
        this.usergentree = usergentree;
    }

    public void setCheckrelationvo(RecordVo checkrelationvo) {
        this.checkrelationvo = checkrelationvo;
    }

    public ArrayList getRelyingList() {
        return relyingList;
    }

    public void setRelyingList(ArrayList relyingList) {
        this.relyingList = relyingList;
    }

    public String getRelyingId() {
        return relyingId;
    }

    public void setRelyingId(String relyingId) {
        this.relyingId = relyingId;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getRelying() {
        return relying;
    }

    public void setRelying(String relying) {
        this.relying = relying;
    }

    public String getDefault_line() {
        return default_line;
    }

    public void setDefault_line(String default_line) {
        this.default_line = default_line;
    }

    public String getIsshowbutton() {
        return isshowbutton;
    }

    public void setIsshowbutton(String isshowbutton) {
        this.isshowbutton = isshowbutton;
    }

    public String getApprovalRelation() {
        return approvalRelation;
    }

    public void setApprovalRelation(String approvalRelation) {
        this.approvalRelation = approvalRelation;
    }

    public String getIsDelMainbody() {
        return isDelMainbody;
    }

    public void setIsDelMainbody(String isDelMainbody) {
        this.isDelMainbody = isDelMainbody;
    }

    public void setUplevel(String uplevel) {
        this.uplevel = uplevel;
    }

    public String getUplevel() {
        return uplevel;
    }

}
