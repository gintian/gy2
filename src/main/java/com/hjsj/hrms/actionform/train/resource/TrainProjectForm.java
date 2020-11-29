package com.hjsj.hrms.actionform.train.resource;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainProjectForm.java
 * </p>
 * <p>
 * Description:培训项目
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainProjectForm extends FrameForm
{
    private String code;

    private String strsql;

    private String strwhere;

    private String columns;

    private ArrayList fields;

    private String memoFld;

    private String dispSaveContinue;

    private String isrefresh;

    private String orgparentcode;

    private String strParam;

    private String returnvalue;

    private String myLessonSql;

    private String myLessonWhere;

    private String myLessonOrder;

    private String myLessonColumns;

    private String commentSql;

    private String commentWhere;

    private String commentOrder;

    private String commentColumns;

    private String lessonName;

    private String isLearned = "0";

    private String isNote = "1";

    private String home = "0"; // 返回地址：0无 5返回门户
    
    private String moduleFlag = "0"; //进入模块  0为自助 ；1为培训管理
    
    private String r1301;
    //课程未学完毕不允许自考 默认=0:未学完允许考试；=1：为学完不允许考试
    private String disableExam="0";
    
    private String state = ""; //all: 全部；0：未学；1：正学；2：已学
    
    private String searchLesson;
    
    private ArrayList viewItemList = new ArrayList();

	@Override
    public void inPutTransHM()
    {
        this.getFormHM().put("code", this.getCode());
        this.getFormHM().put("strsql", this.getStrsql());
        this.getFormHM().put("strwhere", this.getStrwhere());
        this.getFormHM().put("columns", this.getColumns());
        this.getFormHM().put("memoFld", this.getMemoFld());
        this.getFormHM().put("dispSaveContinue", this.getDispSaveContinue());
        this.getFormHM().put("isrefresh", this.getIsrefresh());
        this.getFormHM().put("orgparentcode", this.getOrgparentcode());
        this.getFormHM().put("strParam", this.getStrParam());
        this.getFormHM().put("home", this.getHome());
        this.getFormHM().put("home", this.getR1301());
        this.getFormHM().put("disableExam", this.getDisableExam());
        this.getFormHM().put("state", this.getState());
        this.getFormHM().put("searchLesson", this.getSearchLesson());
        this.getFormHM().put("viewItemList", this.getViewItemList());

    }

    @Override
    public void outPutFormHM()
    {
        this.setCode((String) this.getFormHM().get("code"));
        this.setFields((ArrayList) this.getFormHM().get("fields"));
        this.setColumns((String) this.getFormHM().get("columns"));
        this.setStrsql((String) this.getFormHM().get("strsql"));
        this.setStrwhere((String) this.getFormHM().get("strwhere"));
        this.setMemoFld((String) this.getFormHM().get("memoFld"));
        this.setDispSaveContinue((String) this.getFormHM().get("dispSaveContinue"));
        this.setIsrefresh((String) this.getFormHM().get("isrefresh"));
        this.setOrgparentcode((String) this.getFormHM().get("orgparentcode"));
        this.setStrParam((String) this.getFormHM().get("strParam"));

        this.setMyLessonSql((String) this.getFormHM().get("myLessonSql"));

        this.setMyLessonWhere((String) this.getFormHM().get("myLessonWhere"));

        this.setMyLessonOrder((String) this.getFormHM().get("myLessonOrder"));

        this.setMyLessonColumns((String) this.getFormHM().get("myLessonColumns"));

        this.setIsLearned((String) this.getFormHM().get("isLearned"));
        this.setLessonName((String) this.getFormHM().get("lessonName"));

        this.setCommentSql((String) this.getFormHM().get("commentSql"));

        this.setCommentWhere((String) this.getFormHM().get("commentWhere"));

        this.setCommentOrder((String) this.getFormHM().get("commentOrder"));

        this.setCommentColumns((String) this.getFormHM().get("commentColumns"));
        this.setIsNote((String) this.getFormHM().get("isNote"));
        this.setHome((String)this.getFormHM().get("home"));
        this.setModuleFlag((String)this.getFormHM().get("moduleFlag"));
        this.setR1301((String)this.getFormHM().get("r1301"));
        this.setDisableExam((String)this.getFormHM().get("disableExam"));
        this.setState((String)this.getFormHM().get("state"));
        this.setSearchLesson((String)this.getFormHM().get("searchLesson"));
        this.setViewItemList((ArrayList)this.getFormHM().get("viewItemList"));
    
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
        try
        {
            if ("/train/resource/mylessons".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
            {
                if (this.getPagination() != null)
                    this.getPagination().firstPage();
            }
        
            if ("/train/resource/trainProList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
            {
                if (this.getPagination() != null && "link".equalsIgnoreCase(arg1.getParameter("b_query"))){
                    this.getPagination().firstPage();
                    this.setStrParam("");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.validate(arg0, arg1);
    }

    public String getMyLessonColumns()
    {
        return myLessonColumns;
    }

    public void setMyLessonColumns(String myLessonColumns)
    {
        this.myLessonColumns = myLessonColumns;
    }

    public String getMyLessonSql()
    {
        return myLessonSql;
    }

    public void setMyLessonSql(String myLessonSql)
    {
        this.myLessonSql = myLessonSql;
    }

    public String getMyLessonWhere()
    {
        return myLessonWhere;
    }

    public void setMyLessonWhere(String myLessonWhere)
    {
        this.myLessonWhere = myLessonWhere;
    }

    public String getMyLessonOrder()
    {
        return myLessonOrder;
    }

    public void setMyLessonOrder(String myLessonOrder)
    {
        this.myLessonOrder = myLessonOrder;
    }
    
    public String getCode()
    {

        return code;
    }

    public void setCode(String code)
    {

        this.code = code;
    }

    public ArrayList getFields()
    {

        return fields;
    }

    public void setFields(ArrayList fields)
    {

        this.fields = fields;
    }

    public String getColumns()
    {

        return columns;
    }

    public void setColumns(String columns)
    {

        this.columns = columns;
    }

    public String getDispSaveContinue()
    {

        return dispSaveContinue;
    }

    public void setDispSaveContinue(String dispSaveContinue)
    {

        this.dispSaveContinue = dispSaveContinue;
    }

    public String getMemoFld()
    {

        return memoFld;
    }

    public void setMemoFld(String memoFld)
    {

        this.memoFld = memoFld;
    }

    public String getStrsql()
    {

        return strsql;
    }

    public void setStrsql(String strsql)
    {

        this.strsql = strsql;
    }

    public String getStrwhere()
    {

        return strwhere;
    }

    public void setStrwhere(String strwhere)
    {

        this.strwhere = strwhere;
    }

    public String getIsrefresh()
    {

        return isrefresh;
    }

    public void setIsrefresh(String isrefresh)
    {

        this.isrefresh = isrefresh;
    }

    public String getOrgparentcode()
    {

        return orgparentcode;
    }

    public void setOrgparentcode(String orgparentcode)
    {

        this.orgparentcode = orgparentcode;
    }

    public String getStrParam()
    {

        return strParam;
    }

    public void setStrParam(String strParam)
    {

        this.strParam = strParam;
    }

    public String getReturnvalue()
    {
        return returnvalue;
    }

    public void setReturnvalue(String returnvalue)
    {
        this.returnvalue = returnvalue;
    }

    public String getIsLearned()
    {
        return isLearned;
    }

    public void setIsLearned(String isLearned)
    {
        this.isLearned = isLearned;
    }

    public String getCommentSql()
    {
        return commentSql;
    }

    public void setCommentSql(String commentSql)
    {
        this.commentSql = commentSql;
    }

    public String getCommentWhere()
    {
        return commentWhere;
    }

    public void setCommentWhere(String commentWhere)
    {
        this.commentWhere = commentWhere;
    }

    public String getCommentOrder()
    {
        return commentOrder;
    }

    public void setCommentOrder(String commentOrder)
    {
        this.commentOrder = commentOrder;
    }

    public String getCommentColumns()
    {
        return commentColumns;
    }

    public void setCommentColumns(String commentColumns)
    {
        this.commentColumns = commentColumns;
    }

    public String getLessonName()
    {
        return lessonName;
    }

    public void setLessonName(String lessonName)
    {
        this.lessonName = lessonName;
    }

    public String getIsNote()
    {
        return isNote;
    }

    public void setIsNote(String isNote)
    {
        this.isNote = isNote;
    }

    public String getHome()
    {
        return this.home;
    }

    public void setHome(String home)
    {
        this.home = home;
    }

    public String getModuleFlag() {
		return moduleFlag;
	}

	public void setModuleFlag(String moduleFlag) {
		this.moduleFlag = moduleFlag;
	}

    public String getR1301() {
        return r1301;
    }

    public void setR1301(String r1301) {
        this.r1301 = r1301;
    }

    public String getDisableExam() {
        return disableExam;
    }

    public void setDisableExam(String disableExam) {
        this.disableExam = disableExam;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSearchLesson() {
        return searchLesson;
    }

    public void setSearchLesson(String searchLesson) {
        this.searchLesson = searchLesson;
    }

    public ArrayList getViewItemList() {
        return viewItemList;
    }

    public void setViewItemList(ArrayList viewItemList) {
        this.viewItemList = viewItemList;
    }

}
