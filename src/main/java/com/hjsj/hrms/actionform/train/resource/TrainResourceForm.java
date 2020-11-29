package com.hjsj.hrms.actionform.train.resource;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainResourceForm.java
 * </p>
 * <p>
 * Description:培训体系
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainResourceForm extends FrameForm {
	/* 资源类别[1-培训机构][2-培训教师][3-培训场所][4-培训设施][5-培训资料] */
	private String resType;

	private String recName;

	private String strsql;

	private String strwhere;

	private String columns;

	private ArrayList fields = new ArrayList();

	private String primaryField;

	private String memoFld;

	private String orgparentcode;

	private String dispSaveContinue;

	private String recTable;
	
	private String strParam;

	private String nameFld;
	private String codesetid = "";
	private String codeitemid = "";
	private String codeitemdesc = "";
	private String checkflag = "";
	private String codetitle = "";
	private String a_code = "";
	private String primaryKeyVal;
	private String returnvalue;
	private String type = "";
	private String classid;

	private String flag;

	private String dest = "";
	private String values = "";

	// 培训教师关联指标
	private String nbase;
	private String a0100;
	private String teachertype = "";
	private int aa = 0;
	private String dbname;
	
	private String teachername;
	
	private String itemdesc;

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public int getAa() {
		return aa;
	}

	public void setAa(int aa) {
		this.aa = aa;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("resType", this.getResType());
		this.getFormHM().put("strsql", this.getStrsql());
		this.getFormHM().put("strwhere", this.getStrwhere());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("primaryField", this.getPrimaryField());
		this.getFormHM().put("memoFld", this.getMemoFld());
		this.getFormHM().put("recName", this.getRecName());
		this.getFormHM().put("orgparentcode", this.getOrgparentcode());
		this.getFormHM().put("dispSaveContinue", this.getDispSaveContinue());
		this.getFormHM().put("recTable", this.getRecTable());
		this.getFormHM().put("strParam", this.getStrParam());
		this.getFormHM().put("nameFld", this.getNameFld());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("primaryKeyVal", this.getPrimaryKeyVal());
		this.getFormHM().put(fields, this.getFields());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("teachertype", this.getTeachertype());

		this.getFormHM().put("dest", this.getDest());
		this.getFormHM().put("values", this.getValues());
		this.getFormHM().put("aa", this.getAa() + "");
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("teachername", this.getTeachername());
		this.getFormHM().put("itemdesc", this.getItemdesc());
	}

	@Override
    public void outPutFormHM() {
		this.setResType((String) this.getFormHM().get("resType"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setStrsql((String) this.getFormHM().get("strsql"));
		this.setStrwhere((String) this.getFormHM().get("strwhere"));
		this.setFields((ArrayList) this.getFormHM().get("fields"));
		this.setPrimaryField((String) this.getFormHM().get("primaryField"));
		this.setMemoFld((String) this.getFormHM().get("memoFld"));
		this.setRecName((String) this.getFormHM().get("recName"));
		this.setOrgparentcode((String) this.getFormHM().get("orgparentcode"));
		this.setDispSaveContinue((String) this.getFormHM().get("dispSaveContinue"));
		this.setRecTable((String) this.getFormHM().get("recTable"));
		this.setStrParam((String) this.getFormHM().get("strParam"));
		this.setNameFld((String) this.getFormHM().get("nameFld"));
		this.setCodesetid((String) this.getFormHM().get("codesetid"));
		this.setCodeitemid((String) this.getFormHM().get("codeitemid"));
		this.setCodeitemdesc((String) this.getFormHM().get("codeitemdesc"));
		this.setCheckflag((String) this.getFormHM().get("checkflag"));
		this.setCodetitle((String) this.getFormHM().get("codetitle"));
		this.setA_code((String) this.getFormHM().get("a_code"));
		this.setPrimaryKeyVal((String) this.getFormHM().get("primaryKeyVal"));
		this.setClassid((String) this.getFormHM().get("classid"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setA0100((String) this.getFormHM().get("a0100"));
		this.setNbase((String) this.getFormHM().get("nbase"));
		this.setTeachertype((String) this.getFormHM().get("teachertype"));
		this.setDest((String) this.getFormHM().get("dest"));
		this.setValues((String) this.getFormHM().get("values"));
		this.setAa(Integer.parseInt((String) this.getFormHM().get("aa")));
		this.setTeachername((String) this.getFormHM().get("teachername"));
		this.setItemdesc((String) this.getFormHM().get("itemdesc"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

		try {
			if ("/train/resource/trainRescList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null) {
				if (this.getPagination() != null && "link".equals(arg1.getParameter("b_query"))) {
					this.getPagination().firstPage();
					this.pagerows = 21;
					this.setStrParam("");
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	}

	public String getResType() {

		return resType;
	}

	public void setResType(String resType) {

		this.resType = resType;
	}

	public String getColumns() {

		return columns;
	}

	public void setColumns(String columns) {

		this.columns = columns;
	}

	public String getStrsql() {

		return strsql;
	}

	public void setStrsql(String strsql) {

		this.strsql = strsql;
	}

	public String getStrwhere() {

		return strwhere;
	}

	public void setStrwhere(String strwhere) {

		this.strwhere = strwhere;
	}

	public ArrayList getFields() {

		return fields;
	}

	public void setFields(ArrayList fields) {

		this.fields = fields;
	}

	public String getPrimaryField() {

		return primaryField;
	}

	public void setPrimaryField(String primaryField) {

		this.primaryField = primaryField;
	}

	public String getMemoFld() {

		return memoFld;
	}

	public void setMemoFld(String memoFld) {

		this.memoFld = memoFld;
	}

	public String getRecName() {

		return recName;
	}

	public void setRecName(String recName) {

		this.recName = recName;
	}

	public String getOrgparentcode() {

		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {

		this.orgparentcode = orgparentcode;
	}

	public String getDispSaveContinue() {

		return dispSaveContinue;
	}

	public void setDispSaveContinue(String dispSaveContinue) {

		this.dispSaveContinue = dispSaveContinue;
	}

	public String getRecTable() {

		return recTable;
	}

	public void setRecTable(String recTable) {

		this.recTable = recTable;
	}

	public String getStrParam() {

		return strParam;
	}

	public void setStrParam(String strParam) {

		this.strParam = strParam;
	}

	public String getNameFld() {

		return nameFld;
	}

	public void setNameFld(String nameFld) {

		this.nameFld = nameFld;
	}

	public String getCodeitemdesc() {
		return codeitemdesc;
	}

	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getCodetitle() {
		return codetitle;
	}

	public void setCodetitle(String codetitle) {
		this.codetitle = codetitle;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getPrimaryKeyVal() {

		return primaryKeyVal;
	}

	public void setPrimaryKeyVal(String primaryKeyVal) {

		this.primaryKeyVal = primaryKeyVal;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getNbase() {
		return nbase;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getA0100() {
		return a0100;
	}

	public void setTeachertype(String teachertype) {
		this.teachertype = teachertype;
	}

	public String getTeachertype() {
		return teachertype;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getTeachername() {
		return teachername;
	}

	public void setTeachername(String teachername) {
		this.teachername = teachername;
	}

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc) {
        this.itemdesc = itemdesc;
    }
	
}
