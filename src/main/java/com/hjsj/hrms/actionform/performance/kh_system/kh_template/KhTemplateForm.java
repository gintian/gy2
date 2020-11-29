package com.hjsj.hrms.actionform.performance.kh_system.kh_template;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:KhTemplateForm.java</p>
 * <p>Description>:KhTemplateForm.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-4-24 下午01:27:53</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class KhTemplateForm extends FrameForm{
	
	/**考核模板分类表*/
	/**分类号*/
	private String templatesetid;
	/**分类名称*/
	private String fname;
	/**公有还是私有*/
	private String scope;
	/**有效标识*/
	private String fvalidflag;
	/**父亲分类号*/
	private String parentid;
	private String parentsetid;
	private String setname;
	private String isrefresh;
	private String couldEdit;
	/**考核模板表*/
	/**模板号*/
	private String templateid;
	/**模板名称*/
	private String templatename;
	/**有效日期*/
	private String validdate;
	/**失效日期*/
	private String invaliddate;
	/**权重分值标识*/
	private String status;
	/**登记表号*/
	private String tabids;
	/**有效标识*/
	private String validfalg;
	/**总分*/
	private String topscore;
	/**考核模板项目表*/
	/**项目号*/
	private String itemid;
	/**项目名称*/
	private String itemdesc;
	/**考核模板要素表*/
	/**项目号*/
	private String item_id;
	/**要素号*/
	private String pointid;
	/**权重*/
	private String rank;
	/**权重类型*/
	private String ranktype;
	/**权重计算公式*/
	private String formula;
	private String tree;
	private String subsys_id;
	/**用来判断新增还是修改*/
	private String type;
	private String isclose;
	/**后台生成的html代码*/
	private String tableHtml;
	/**是否显示操作按钮*/
	private String isVisible;
	/**模板是否被使用=0已经使用=1未使用*/
	private String isUsed;
	/**模板是否已经有项目=0有项目=1还没有项目*/
	private String isHaveItem;
	/**所有指标id组成的字符串*/
	private String score_str;
	/**模板文件*/
	private FormFile templatefile;
	
	private ArrayList templatelist;
	private String selectid;
	/**method=0,直接进入考核模板模块，否则从考核计划中进入模板时，如果360度考核的话，不出现有个性指标的模板参数 method=1是360=2是目标管理*/
	private String method;
	/**否则从考核计划中进入模板时,status=0 有确定按钮,status!=0 没有确定按钮*/
	private String planStatus;
	/**用来区别右侧点击的是模板还是模板分类*/
	private String t_type;
	private String t_tid;
	private ArrayList itemList = new ArrayList();
	private String infos;
	private String flag;
	private String tname;
	private String persionControl;
	private String templateUsed;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("templateUsed", this.getTemplateUsed());
		this.getFormHM().put("persionControl", this.getPersionControl());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("tname", this.getTname());
		this.getFormHM().put("itemList", this.getItemList());
		this.getFormHM().put("t_tid", this.getT_tid());
		this.getFormHM().put("templatelist",this.getTemplatelist());
		this.getFormHM().put("selectid",this.getSelectid());
		this.getFormHM().put("subsys_id",this.getSubsys_id());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("templatesetid",this.getTemplatesetid());
		this.getFormHM().put("fname", this.getFname());
		this.getFormHM().put("scope", this.getScope());
		this.getFormHM().put("fvalidflag", this.getFvalidflag());
		this.getFormHM().put("topscore",this.getTopscore());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("templatename",this.getTemplatename());
		this.getFormHM().put("templateid",this.getTemplateid());
		this.getFormHM().put("couldEdit",this.getCouldEdit());
		this.getFormHM().put("isclose",this.getIsclose());
		this.getFormHM().put("parentid",this.getParentid());
		this.getFormHM().put("isVisible",this.getIsVisible());
		this.getFormHM().put("templatefile",this.getTemplatefile());
	    this.getFormHM().put("t_type",this.getT_type());
	    this.getFormHM().put("parentsetid",this.getParentsetid());
	    this.getFormHM().put("setname",this.getSetname());
		}

	@Override
    public void outPutFormHM() {
		
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setParentsetid((String)this.getFormHM().get("parentsetid"));
		this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
		this.setTemplateUsed((String)this.getFormHM().get("templateUsed"));
		this.setPersionControl((String)this.getFormHM().get("persionControl"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setTname((String)this.getFormHM().get("tname"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setInfos((String)this.getFormHM().get("infos"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setT_tid((String)this.getFormHM().get("t_tid"));
		this.setT_type((String)this.getFormHM().get("t_type"));
	    this.setPlanStatus((String)this.getFormHM().get("planStatus"));
	    this.setMethod((String)this.getFormHM().get("method"));
		this.setTemplatelist((ArrayList)this.getFormHM().get("templatelist"));
		this.setScore_str((String)this.getFormHM().get("score_str"));
		this.setIsUsed((String)this.getFormHM().get("isUsed"));
		this.setIsHaveItem((String)this.getFormHM().get("isHaveItem"));
		this.setTree((String)this.getFormHM().get("tree"));
		this.setSubsys_id((String)this.getFormHM().get("subsys_id"));
		this.setType((String)this.getFormHM().get("type"));
		this.setTemplatesetid((String)this.getFormHM().get("templatesetid"));
		this.setScope((String)this.getFormHM().get("scope"));
		this.setFname((String)this.getFormHM().get("fname"));
		this.setFvalidflag((String)this.getFormHM().get("fvalidflag"));
		this.setTopscore((String)this.getFormHM().get("topscore"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setTemplatename((String)this.getFormHM().get("templatename"));
		this.setTemplateid((String)this.getFormHM().get("templateid"));
		this.setCouldEdit((String)this.getFormHM().get("couldEdit"));
		this.setIsclose((String)this.getFormHM().get("isclose"));
		this.setParentid((String)this.getFormHM().get("parentid"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setIsVisible((String)this.getFormHM().get("isVisible"));
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFvalidflag() {
		return fvalidflag;
	}

	public void setFvalidflag(String fvalidflag) {
		this.fvalidflag = fvalidflag;
	}

	public String getInvaliddate() {
		return invaliddate;
	}

	public void setInvaliddate(String invaliddate) {
		this.invaliddate = invaliddate;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getPointid() {
		return pointid;
	}

	public void setPointid(String pointid) {
		this.pointid = pointid;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getRanktype() {
		return ranktype;
	}

	public void setRanktype(String ranktype) {
		this.ranktype = ranktype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTabids() {
		return tabids;
	}

	public void setTabids(String tabids) {
		this.tabids = tabids;
	}

	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public String getTemplatename() {
		return templatename;
	}

	public void setTemplatename(String templatename) {
		this.templatename = templatename;
	}

	public String getTemplatesetid() {
		return templatesetid;
	}

	public void setTemplatesetid(String templatesetid) {
		this.templatesetid = templatesetid;
	}

	public String getValiddate() {
		return validdate;
	}

	public void setValiddate(String validdate) {
		this.validdate = validdate;
	}

	public String getValidfalg() {
		return validfalg;
	}

	public void setValidfalg(String validfalg) {
		this.validfalg = validfalg;
	}

	public String getTree() {
		return tree;
	}

	public void setTree(String tree) {
		this.tree = tree;
	}

	public String getSubsys_id() {
		return subsys_id;
	}

	public void setSubsys_id(String subsys_id) {
		this.subsys_id = subsys_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTopscore() {
		return topscore;
	}

	public void setTopscore(String topscore) {
		this.topscore = topscore;
	}

	public String getIsclose() {
		return isclose;
	}

	public void setIsclose(String isclose) {
		this.isclose = isclose;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	public String getIsHaveItem() {
		return isHaveItem;
	}

	public void setIsHaveItem(String isHaveItem) {
		this.isHaveItem = isHaveItem;
	}

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	public String getScore_str() {
		return score_str;
	}

	public void setScore_str(String score_str) {
		this.score_str = score_str;
	}

	public FormFile getTemplatefile() {
		return templatefile;
	}

	public void setTemplatefile(FormFile templatefile) {
		this.templatefile = templatefile;
	}

	public ArrayList getTemplatelist() {
		return templatelist;
	}

	public void setTemplatelist(ArrayList templatelist) {
		this.templatelist = templatelist;
	}

	public String getSelectid() {
		return selectid;
	}

	public void setSelectid(String selectid) {
		this.selectid = selectid;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}

	public String getT_type() {
		return t_type;
	}

	public void setT_type(String t_type) {
		this.t_type = t_type;
	}

	public String getT_tid() {
		return t_tid;
	}

	public void setT_tid(String t_tid) {
		this.t_tid = t_tid;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public String getInfos() {
		return infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getPersionControl() {
		return persionControl;
	}

	public void setPersionControl(String persionControl) {
		this.persionControl = persionControl;
	}

	public String getTemplateUsed() {
		return templateUsed;
	}

	public void setTemplateUsed(String templateUsed) {
		this.templateUsed = templateUsed;
	}
	public String getIsrefresh() {
		return isrefresh;
	}

	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getCouldEdit() {
		return couldEdit;
	}

	public void setCouldEdit(String couldEdit) {
		this.couldEdit = couldEdit;
	}

	public String getParentsetid() {
		return parentsetid;
	}

	public void setParentsetid(String parentsetid) {
		this.parentsetid = parentsetid;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/performance/kh_system/kh_template/add_or_edit_template".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null){
			arg1.setAttribute("targetWindow", "1");
			//报错的时候弹框返回无效问题
        }else if("/performance/kh_system/kh_template/init_kh_item".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null) {
        	arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
        }
		return super.validate(arg0, arg1);
	}
}
