package com.hjsj.hrms.actionform.train.station;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p> Title:TrainStationForm.java </p>
 * <p> Description:岗位培训指标设置 </p>
 * <p> Company:HJSJ </p>
 * <p> Create Time:2011-05-23 10:25:27 </p>
 * 
 * @author LiWeichao
 * @version 5.0
 */
public class TrainStationForm extends FrameForm {

	/**配置参数*/
	private ArrayList nbase_list=new ArrayList();
	private ArrayList sel_nbase=new ArrayList();
	private String nbase;
	private ArrayList emp_list=new ArrayList();
	private ArrayList reg_list=new ArrayList();
	private String emp_setid;
	private String reg_setid;
	private String emp_coursecloumn;
	private String emp_pssscloumn;
	private String emp_passvalues;
	private ArrayList post_list=new ArrayList();
	private String post_setid;
	private String post_setxid;
	private String post_coursecloumn;
	private String dbpre="";
	private ArrayList dblist=new ArrayList();
	private String sqlstr="";
	private String where="";
	private String cloumn="";
	private String code="";
	private String kind="";
	private ArrayList browsefields=new ArrayList();
	private String uplevel="";
	private String ishavepostdesc="";
	private String cardid="";
    private String codename="";
    private String flag="";
    private ArrayList flaglist=new ArrayList();
    private String roster="";
    private String ensql="";
    private ArrayList classlist=new ArrayList();
    private String classid="";
    private String query="";
    
    private ArrayList setlist=new ArrayList();//子集列表
    private String item_field[]; //指标列表
	private String type; //区别查询类型 [1.人员查询  2.单位查询  3.职位查询]
	private String tablename; //库前缀
	private String sexpr="";//因子表达式
	private String sfactor="";//计算条件
	private String history;//历史
	private String likeflag;//模糊
	private String fieldid;
	private String fieldSetId;
	private String fieldSetDesc;
	private String chwhere;
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("dbpre", this.getDbpre());	
		this.getFormHM().put("code", this.getCode());	
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("classid", classid);
		this.getFormHM().put("query", this.getQuery());
		
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("tablename", this.getTablename());
		this.getFormHM().put("sexpr", this.getSexpr());
		this.getFormHM().put("sfactor", this.getSfactor());
		this.getFormHM().put("likeflag", this.getLikeflag());
		this.getFormHM().put("history", this.getHistory());
		this.getFormHM().put("fieldSetId", this.getFieldSetId());
		this.getFormHM().put("fieldSetDesc", this.getFieldSetDesc());
		this.getFormHM().put("chwhere", this.getChwhere());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setNbase_list((ArrayList)this.getFormHM().get("nbase_list"));
		this.setSel_nbase((ArrayList)this.getFormHM().get("sel_nbase"));
		this.setEmp_list((ArrayList)this.getFormHM().get("emp_list"));
		this.setReg_list((ArrayList)this.getFormHM().get("reg_list"));
		this.setReg_setid((String)this.getFormHM().get("reg_setid"));
		this.setEmp_setid((String)this.getFormHM().get("emp_setid"));
		this.setEmp_coursecloumn((String)this.getFormHM().get("emp_coursecloumn"));
		this.setPost_list((ArrayList)this.getFormHM().get("post_list"));
		this.setPost_setid((String)this.getFormHM().get("post_setid"));
		this.setPost_setxid((String)this.getFormHM().get("post_setxid"));
		this.setPost_coursecloumn((String)this.getFormHM().get("post_coursecloumn"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setCloumn((String)this.getFormHM().get("cloumn"));
		this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setIshavepostdesc((String)this.getFormHM().get("ishavepostdesc"));
		this.setCardid((String)this.getFormHM().get("cardid"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setFlaglist((ArrayList)this.getFormHM().get("flaglist"));
		this.setEnsql((String)this.getFormHM().get("ensql"));
		this.setRoster((String)this.getFormHM().get("roster"));
		this.setClasslist((ArrayList)this.getFormHM().get("classlist"));
		this.setClassid((String)this.getFormHM().get("classid"));
		this.setEmp_pssscloumn((String)this.getFormHM().get("emp_pssscloumn"));
		this.setEmp_passvalues((String)this.getFormHM().get("emp_passvalues"));		
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setItem_field((String[])this.getFormHM().get("item_field"));
		this.setType((String)this.getFormHM().get("type"));
		this.setFieldid((String)this.getFormHM().get("fieldid"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setFieldSetId((String)this.getFormHM().get("fieldSetId"));
		this.setFieldSetDesc((String)this.getFormHM().get("fieldSetDesc"));
		this.setChwhere((String)this.getFormHM().get("chwhere"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/train/postAnalyse/notaccordpost".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)
        {
        	if(this.getPagination()!=null){
                this.getPagination().firstPage();//?
                this.pagerows = 21;
        	}
            this.setCode("");
            this.getFormHM().put("code", "");
            this.setCodename("");
            this.getFormHM().put("classid", "###");
            this.setClassid("###");
        } 
        if("/train/postAnalyse/accordpost".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
        	if(this.getPagination()!=null){
                this.getPagination().firstPage();//?
                this.pagerows = 21;
        	}          
        }
        if("/train/postAnalyse/accordpost".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)
        {
        	if(this.getPagination()!=null){
                this.getPagination().firstPage();//?
                this.pagerows = 21;
        	}
              this.setCode("");
              this.getFormHM().put("code", "");
              this.setCodename("");
              this.getFormHM().put("classid", "###");
              this.setClassid("###");
        }        
        if("/train/postAnalyse/accordpost".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
        	if(this.getPagination()!=null){
                this.getPagination().firstPage();//?
                this.pagerows = 21;
        	}
             
        }
        if("/train/postAnalyse/accordpost".equals(arg0.getPath())&&arg1.getParameter("b_alayse")!=null)
        {
        	if(this.getPagination()!=null){
                this.getPagination().firstPage();//?
                this.pagerows = 21;
        	}
             
        }
        if("/train/postAnalyse/notaccordpostwork".equals(arg0.getPath())&&arg1.getParameter("b_searchlesson")!=null)
        {
        	if(this.getPagination()!=null){
                this.getPagination().firstPage();//?
                this.pagerows = 21;
        	}
        }
        return super.validate(arg0, arg1);
    }
	public ArrayList getSel_nbase() {
		return sel_nbase;
	}

	public void setSel_nbase(ArrayList sel_nbase) {
		this.sel_nbase = sel_nbase;
	}

	public ArrayList getNbase_list() {
		return nbase_list;
	}

	public void setNbase_list(ArrayList nbase_list) {
		this.nbase_list = nbase_list;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public ArrayList getEmp_list() {
		return emp_list;
	}

	public void setEmp_list(ArrayList emp_list) {
		this.emp_list = emp_list;
	}

	public String getEmp_setid() {
		return emp_setid;
	}

	public void setEmp_setid(String emp_setid) {
		this.emp_setid = emp_setid;
	}

	public String getEmp_coursecloumn() {
		return emp_coursecloumn;
	}

	public void setEmp_coursecloumn(String emp_coursecloumn) {
		this.emp_coursecloumn = emp_coursecloumn;
	}

	public ArrayList getPost_list() {
		return post_list;
	}

	public void setPost_list(ArrayList post_list) {
		this.post_list = post_list;
	}

	public String getPost_setid() {
		return post_setid;
	}

	public void setPost_setid(String post_setid) {
		this.post_setid = post_setid;
	}

	public String getPost_coursecloumn() {
		return post_coursecloumn;
	}

	public void setPost_coursecloumn(String post_coursecloumn) {
		this.post_coursecloumn = post_coursecloumn;
	}


	public String getDbpre() {
		return dbpre;
	}


	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}


	public ArrayList getDblist() {
		return dblist;
	}


	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}


	public String getSqlstr() {
		return sqlstr;
	}


	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}


	public String getWhere() {
		return where;
	}


	public void setWhere(String where) {
		this.where = where;
	}


	public String getCloumn() {
		return cloumn;
	}


	public void setCloumn(String cloumn) {
		this.cloumn = cloumn;
	}


	public ArrayList getBrowsefields() {
		return browsefields;
	}


	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}


	public String getUplevel() {
		return uplevel;
	}


	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}


	public String getIshavepostdesc() {
		return ishavepostdesc;
	}


	public void setIshavepostdesc(String ishavepostdesc) {
		this.ishavepostdesc = ishavepostdesc;
	}


	public String getCardid() {
		return cardid;
	}


	public void setCardid(String cardid) {
		this.cardid = cardid;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getKind() {
		return kind;
	}


	public void setKind(String kind) {
		this.kind = kind;
	}


	public String getCodename() {
		return codename;
	}


	public void setCodename(String codename) {
		this.codename = codename;
	}


	public ArrayList getFlaglist() {
		return flaglist;
	}


	public void setFlaglist(ArrayList flaglist) {
		this.flaglist = flaglist;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}


	public String getRoster() {
		return roster;
	}


	public void setRoster(String roster) {
		this.roster = roster;
	}


	public String getEnsql() {
		return ensql;
	}


	public void setEnsql(String ensql) {
		this.ensql = ensql;
	}


	public ArrayList getClasslist() {
		return classlist;
	}


	public void setClasslist(ArrayList classlist) {
		this.classlist = classlist;
	}


	public String getClassid() {
		return classid;
	}


	public void setClassid(String classid) {
		this.classid = classid;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}



	public String getEmp_pssscloumn() {
		return emp_pssscloumn;
	}


	public void setEmp_pssscloumn(String emp_pssscloumn) {
		this.emp_pssscloumn = emp_pssscloumn;
	}

	public String getEmp_passvalues() {
		return emp_passvalues;
	}

	public void setEmp_passvalues(String emp_passvalues) {
		this.emp_passvalues = emp_passvalues;
	}



	public String[] getItem_field() {
		return item_field;
	}


	public void setItem_field(String[] item_field) {
		this.item_field = item_field;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTablename() {
		return tablename;
	}


	public void setTablename(String tablename) {
		this.tablename = tablename;
	}


	public String getSexpr() {
		return sexpr;
	}


	public void setSexpr(String sexpr) {
		this.sexpr = sexpr;
	}


	public String getSfactor() {
		return sfactor;
	}


	public void setSfactor(String sfactor) {
		this.sfactor = sfactor;
	}


	public String getFieldid() {
		return fieldid;
	}


	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}


	public ArrayList getSetlist() {
		return setlist;
	}


	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}


	public String getFieldSetId() {
		return fieldSetId;
	}


	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}


	public String getFieldSetDesc() {
		return fieldSetDesc;
	}


	public void setFieldSetDesc(String fieldSetDesc) {
		this.fieldSetDesc = fieldSetDesc;
	}


	public String getHistory() {
		return history;
	}


	public void setHistory(String history) {
		this.history = history;
	}


	public String getLikeflag() {
		return likeflag;
	}


	public void setLikeflag(String likeflag) {
		this.likeflag = likeflag;
	}

	public String getPost_setxid() {
		return post_setxid;
	}

	public void setPost_setxid(String post_setxid) {
		this.post_setxid = post_setxid;
	}

	public ArrayList getReg_list() {
		return reg_list;
	}

	public void setReg_list(ArrayList reg_list) {
		this.reg_list = reg_list;
	}

	public String getReg_setid() {
		return reg_setid;
		
	}

	public void setReg_setid(String reg_setid) {
		this.reg_setid = reg_setid;
	}

	public String getChwhere() {
		return chwhere;
	}

	public void setChwhere(String chwhere) {
		this.chwhere = chwhere;
	}

	
	
}
