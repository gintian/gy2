/**
 * 
 */
package com.hjsj.hrms.actionform.sys.codemaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuxiaochun
 * 
 */
public class CodeMaintenceForm extends FrameForm {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	/*
	 * selstr形成select标签 seltree为选择的值
	 */
	private String selstr;
	private String seltree;
	/*
	 * vflag 显示标志 =0增加叶面 =1修改叶面
	 */
	private String vflag;
	// 分叶显示
	private String sql;
	private String where;
	private String column;
	private String orderby;
	private String sqlf;
	private String wheref;
	private String columnf;

	private PaginationForm pageListForm = new PaginationForm();
	
	/*
	 * flag =0 显示codeset信息 =1 显示codeitem信息
	 */
	private String flag;
	private String treecode;
	RecordVo codesetvo = new RecordVo("codeset");
	RecordVo codeitemvo = new RecordVo("codeitem");
	private String cflag;
	private FormFile file;
	private String filevalue;
	/** 相关代码类 */
	private String codesetid;
	// 判断代码长度
	private String len;
	private String upflag;
	private String currnodetext;
	private String selcodesetid;
	private String status;
	
	private String categories;
	private ArrayList catelist = new ArrayList();
	private String hidcategories;

	@Override
    public void outPutFormHM() {
		HashMap hm = this.getFormHM();
		// TODO Auto-generated method stub
		// try {
		String codeflag = SystemConfig.getPropertyValue("dev_flag");
		if (codeflag == null || "0".equals(codeflag) || "".equals(codeflag)) {
			this.setCflag("0");
		} else {
			this.setCflag("1");
		}
		// } catch (GeneralException e) {
		// // TODO Auto-generated catch block
		// this.setCflag("0");
		// }

		this.setTreecode((String) hm.get("treecode"));
		this.setVflag((String) this.getFormHM().get("vflag"));
		/*
		 * 判断flag选择向叶面传递vo对象
		 */
		if (this.getFormHM().containsKey("flag")) {
			String ff = (String) this.getFormHM().get("flag");
			this.setFlag(ff);

			this.setCodesetvo((RecordVo) hm.get("codesetvo"));
			if("0".equalsIgnoreCase(this.getVflag())&&this.getCodesetvo()!=null){//新增代码类
				this.getCodesetvo().setString("validateflag", "1");
			}
			this.setCodeitemvo((RecordVo) hm.get("codeitemvo"));

		}

		
		/*
		 * 将分页查询语句和条件传递给叶面
		 */
		this.setSql((String) hm.get("sqlstr"));
		this.setWhere((String) hm.get("where"));
		this.setColumn((String) hm.get("column"));
		this.setSqlf((String) hm.get("sqlstrf"));
		this.setWheref((String) hm.get("wheref"));
		this.setColumnf((String) hm.get("columnf"));
		this.setSelstr((String) hm.get("selstr"));
		this.setLen((String) (hm.get("len") == null ? null : hm.get("len")));
		this.setUpflag((String) this.getFormHM().get("upflag"));
		this.setFilevalue((String) this.getFormHM().get("filevalue"));
		this.setCurrnodetext((String) hm.get("currnodetext"));
		// this.setFile((File) this.getFormHM().get("upfile"));
		this.setSelcodesetid((String) this.getFormHM().get("selcodesetid"));
		
		this.setCategories((String)this.getFormHM().get("categories"));
		this.setCatelist((ArrayList)this.getFormHM().get("catelist"));
		this.setHidcategories((String)this.getFormHM().get("hidcategories"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("seltree", this.getSeltree());
		this.getFormHM().put("upfile", this.getFile());
		this.getFormHM().put("upflag", this.getUpflag());
		this.getFormHM().put("filevalue", this.getFilevalue());
		this.getFormHM().put("ufile", this.getFile());
		this.setFilevalue("");
		this.getFormHM().put("status", status);
		this.getFormHM().put("categories", categories);
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if ("/system/codemaintence/codeshow".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			/** 定位到首页, */
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		if ("/system/codemaintence/codetree".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null
				) {
			if("link".equalsIgnoreCase(arg1.getParameter("b_search"))){
			/** 定位到首页, */          /**vflag 参数 控制 新增或修改 代码项 不需要定位到首页  wangb 20180810 bug 39458*/
				if(this.userView.getHm().get("codesetid")!=null&& this.getVflag()!=null && this.getVflag().length()!=0/*&&this.categories==null&&this.categories.length()==0*/){
					if (this.getPagination() != null&& "0".equalsIgnoreCase(this.getVflag())){//添加
						try {
							int i = this.pitchpage((String)this.userView.getHm().get("codesetid"), this.pagerows, false);
							this.getPagination().setCurrent(i);
							this.userView.getHm().remove("codesetid");
							//this.userView.getHm().put("flag", "flag");
						} catch (GeneralException e) {
							e.printStackTrace();
						}
					}else if(this.getPagination() != null&& "1".equalsIgnoreCase(this.getVflag())){//修改
						try {
							int i = this.pitchpage((String)this.userView.getHm().get("codesetid"), this.pagerows, true);
							this.getPagination().setCurrent(i);
							this.userView.getHm().remove("codesetid");
							//this.userView.getHm().put("flag", "flag");
						} catch (GeneralException e) {
							e.printStackTrace();
						}
					}
				}else{
					//if(this.userView.getHm().get("flag")==null){
						if (this.getPagination() != null)
							this.getPagination().firstPage();
					//}
					//this.userView.getHm().remove("flag");
				}
				this.setVflag("");
			}
//			if(arg1.getParameter("b_search").equalsIgnoreCase("aa")){//添加代码类定位代码类所在页面
//				if (this.getPagination() != null){
//					try {
//						int i = this.pitchpage((String)this.userView.getHm().get("codesetid"), this.pagerows, false);
//						this.getPagination().setCurrent(i);
//						this.userView.getHm().remove("codesetid");
//					} catch (GeneralException e) {
//						e.printStackTrace();
//					}
//				}
//			}
		}
		//用于导入失败时，点击返回按钮关闭弹出框口      jingq  add    2014.5.19
		if("/system/codemaintence/codeinput".equals(arg0.getPath())
				&& arg1.getParameter("b_input") != null){
			arg1.setAttribute("targetWindow", "1");
		}
		return super.validate(arg0, arg1);
	}

	public String getTreecode() {
		return treecode;
	}

	public void setTreecode(String treecode) {
		this.treecode = treecode;
	}

	public RecordVo getCodeitemvo() {
		return codeitemvo;
	}

	public void setCodeitemvo(RecordVo codeitemvo) {
		this.codeitemvo = codeitemvo;
	}

	public RecordVo getCodesetvo() {
		return codesetvo;
	}

	public void setCodesetvo(RecordVo codesetvo) {
		this.codesetvo = codesetvo;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getVflag() {
		return vflag;
	}

	public void setVflag(String vflag) {
		this.vflag = vflag;
	}

	public String getSeltree() {
		return seltree;
	}

	public void setSeltree(String seltree) {
		this.seltree = seltree;
	}

	public String getSelstr() {
		return selstr;
	}

	public void setSelstr(String selstr) {
		this.selstr = selstr;
	}

	public String getCflag() {
		return cflag;
	}

	public void setCflag(String cflag) {
		this.cflag = cflag;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getLen() {
		return len;
	}

	public void setLen(String len) {
		this.len = len;
	}

	public String getUpflag() {
		return upflag;
	}

	public void setUpflag(String upflag) {
		this.upflag = upflag;
	}

	public String getFilevalue() {
		return filevalue;
	}

	public void setFilevalue(String filevalue) {
		this.filevalue = filevalue;
	}

	public String getCurrnodetext() {
		return currnodetext;
	}

	public void setCurrnodetext(String currnodetext) {
		this.currnodetext = currnodetext;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getSelcodesetid() {
		return selcodesetid;
	}

	public void setSelcodesetid(String selcodesetid) {
		this.selcodesetid = selcodesetid;
	}

	public String getColumnf() {
		return columnf;
	}

	public void setColumnf(String columnf) {
		this.columnf = columnf;
	}

	public String getSqlf() {
		return sqlf;
	}

	public void setSqlf(String sqlf) {
		this.sqlf = sqlf;
	}

	public String getWheref() {
		return wheref;
	}

	public void setWheref(String wheref) {
		this.wheref = wheref;
	}

	/**
	 * 用于分页计算页码
	 * @param codesetid
	 * @param pagesize
	 * @param flag标记是在所有记录中分页还是在系统代码或用户代码记录中分页 true 所有、false 单独
	 * @return
	 */
	private int pitchpage(String codesetid,int pagesize,boolean flag) throws GeneralException {
		Connection conn = null;
		String sql = null;
		int count=0;
		int currentpage=0;
		java.sql.ResultSet rs=null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
		if(flag){
			sql = "select codesetid from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' ${categories} order by codesetid";
		}else{
			 Pattern p = Pattern.compile("^\\d+$");
			 Matcher m = p.matcher(codesetid);
			 boolean b = m.matches();
			 if(b){//系统代码
				 sql = "select codesetid from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' and status in(1,2) ${categories} order by codesetid";
			 }else{//用户代码
				 sql = "select codesetid from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' and (status is null or status='0' or status='') ${categories} order by codesetid";
			 }
		}
		if(this.categories==null || this.categories.length()==0){
			sql=sql.replace("${categories}", "");
			rs = dao.search(sql);
		}else{
			sql=sql.replace("${categories}", "and categories=?");
			ArrayList list = new ArrayList();
			list.add(com.hrms.frame.codec.SafeCode.decode(this.categories));
			rs = dao.search(sql,list);
		}
			while(rs.next()){
				count++;
				if(rs.getString("codesetid").trim().equalsIgnoreCase(codesetid.trim()))
					break;
//				else
//					++count;
					
			}
			
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try{
				if(rs!=null)
					rs.close();
				if(conn!=null)
					conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		currentpage = (count%pagesize==0? count/pagesize:(count/pagesize+1));
		return currentpage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public ArrayList getCatelist() {
		return catelist;
	}

	public void setCatelist(ArrayList catelist) {
		this.catelist = catelist;
	}

	public String getHidcategories() {
		return hidcategories;
	}

	public void setHidcategories(String hidcategories) {
		this.hidcategories = hidcategories;
	}
}
