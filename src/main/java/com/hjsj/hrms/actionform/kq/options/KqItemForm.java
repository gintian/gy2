package com.hjsj.hrms.actionform.kq.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqItemForm extends FrameForm {
	
	private String flag="0";
	
	private String sys ;
	
	private String aler;
	
	private String items;
	
	private String s_expr;
	
	private String c_expr;
	
	private String order[];
	
	private String[] left_fields;
	
	private String code;
	
	private String codeitemid;
	
	private String name;
	
	private String mess;
	
	private String mes;
	
	private String codelen;
	private String childlen;
	
	private String sdata_src;
	
	private String infor_Flag="1";
		
	private String manner;
	
	private String display;
	
	private String colo;
	
	private String sige;
	
	private String sigh;
	private ArrayList fieldlist=new ArrayList();
	
	private ArrayList klist =new ArrayList();
	
	private ArrayList orlist =new ArrayList();
	
	private RecordVo item=new RecordVo("kq_item");
	
	private PaginationForm kqItemForm=new PaginationForm();
    
	private String expr_flag;
	private String item_sy;
	//导入指标
	private ArrayList mainlist=new ArrayList();  //来源子集
	private String temidtype;//指标类型
	private String subset;  //具体那个来源子集
	private String field;   //具体那个来源指标
	private String begindate; //开始时间
	private String enddate;   //结束时间
	private ArrayList itemidlist=new ArrayList();
	private ArrayList listdate= new ArrayList();
	private String akq_item;
	private String returnvalue="1";
	
	private String gw_flag;//国网考勤
	 
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getKqItemForm().setList((ArrayList)this.getFormHM().get("itemlist"));
		this.setItems((String)this.getFormHM().get("items"));
		this.setMess((String)this.getFormHM().get("mess"));
		this.setCodelen((String)this.getFormHM().get("codelen"));
		this.setItem((RecordVo)this.getFormHM().get("item"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setInfor_Flag((String)this.getFormHM().get("infor_Flag"));
		this.setName((String)this.getFormHM().get("name"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setKlist((ArrayList)this.getFormHM().get("klist"));
		this.setSdata_src((String)this.getFormHM().get("sdata_src"));
		this.setMes((String)this.getFormHM().get("mes"));
		this.setDisplay((String)this.getFormHM().get("display"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setOrlist((ArrayList)this.getFormHM().get("orlist"));
		this.setColo((String)this.getFormHM().get("colo"));
		this.setC_expr((String)this.getFormHM().get("c_expr"));
		
		this.setSige((String)this.getFormHM().get("sige"));
		this.setSigh((String)this.getFormHM().get("sigh"));
		
		this.setAler((String)this.getFormHM().get("aler"));
		this.setSys((String)this.getFormHM().get("sys"));
		this.setExpr_flag((String)this.getFormHM().get("expr_flag"));
        this.setChildlen((String)this.getFormHM().get("childlen"));
        this.setItem_sy((String)this.getFormHM().get("item_sy"));
        this.setTemidtype((String)this.getFormHM().get("temidtype"));
        this.setMainlist((ArrayList)this.getFormHM().get("mainlist"));
        this.setSubset((String)this.getFormHM().get("subset"));
        this.setField((String)this.getFormHM().get("field"));
        this.setBegindate((String)this.getFormHM().get("begindate"));
        this.setEnddate((String)this.getFormHM().get("enddate"));
        this.setItemidlist((ArrayList)this.getFormHM().get("itemidlist"));
        this.setListdate((ArrayList)this.getFormHM().get("listdate"));
        this.setAkq_item((String)this.getFormHM().get("akq_item"));
        this.setGw_flag((String)this.getFormHM().get("gw_flag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",(ArrayList)this.getKqItemForm().getSelectedList());
		this.getFormHM().put("item",(RecordVo)this.getItem());
		this.getFormHM().put("items",(String)this.getItems());
		this.getFormHM().put("code",(String)this.getCode());
		this.getFormHM().put("name",(String)this.getName());
		this.getFormHM().put("infor_Flag",(String)this.getInfor_Flag());
		this.getFormHM().put("s_expr",(String)this.getS_expr());
		this.getFormHM().put("c_expr",(String)this.getC_expr());
		this.getFormHM().put("manner",(String)this.getManner());
		this.getFormHM().put("codeitemid",(String)this.getCodeitemid());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("sdata_src",(String)this.getSdata_src());
		
		this.getFormHM().put("dispaly",(String)this.getDisplay());
		this.getFormHM().put("orlist",(ArrayList)this.getOrlist());
		this.getFormHM().put("order",this.getOrder());
        this.getFormHM().put("expr_flag",this.getExpr_flag());
        this.getFormHM().put("item_sy",(String)this.getItem_sy());
        this.getFormHM().put("gw_flag", this.getGw_flag());
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public RecordVo getItem() {
		return item;
	}

	public void setItem(RecordVo item) {
		this.item = item;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public PaginationForm getKqItemForm() {
		return kqItemForm;
	}

	public void setKqItemForm(PaginationForm kqItemForm) {
		this.kqItemForm = kqItemForm;
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
	       
        if("/kq/options/kq_item_details".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
        {
            this.getItem().clearValues();            
            this.setFlag("0");
        }
        if("/kq/options/static_formula".equals(arg0.getPath())&&arg1.getParameter("b_querys")!=null)
        {         
            this.setFlag("0");
        }
        if("/kq/options/static_formula".equals(arg0.getPath())&&arg1.getParameter("b_searcht")!=null)
        {         
            this.setFlag("1");
        }
        if("/kq/options/static_formula".equals(arg0.getPath())&&arg1.getParameter("b_save")!=null)
        {         
            this.setFlag("1");
        }
        if("/kq/options/computer_formula".equals(arg0.getPath())&&arg1.getParameter("b_queryc")!=null)
        {         
            this.setFlag("0");
        }
        if("/kq/options/computer_formula".equals(arg0.getPath())&&arg1.getParameter("b_save")!=null)
        {         
            this.setFlag("1");
        }

        if("/kq/options/kq_item_details".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            this.setFlag("1");
        }
        if("/kq/options/kq_item_details".equals(arg0.getPath())&&arg1.getParameter("b_edit")!=null)
        {
        	//this.getFlag(); 
            this.setFlag("1");
        }
        if("/kq/options/add_item".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {          
            this.setFlag("1");
        	  
        }  
        if("/kq/options/add_item".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
        {          
            this.setFlag("5");
        	  
        }  
        if("/kq/options/add_item_type".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {          
            this.setFlag("1");
        	  
        }
        if("/kq/options/item_tree".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
        {
                       
            this.setFlag("0");
        }
        if("/kq/options/item_tree".equals(arg0.getPath())&&arg1.getParameter("b_edit")!=null)
        {
                       
            this.setFlag("3");
        }
        
        return super.validate(arg0, arg1);
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public ArrayList getKlist() {
		return klist;
	}

	public void setKlist(ArrayList klist) {
		this.klist = klist;
	}

	public String getS_expr() {
		return s_expr;
	}

	public void setS_expr(String s_expr) {
		this.s_expr = s_expr;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String getManner() {
		return manner;
	}

	public void setManner(String manner) {
		this.manner = manner;
	}

	public String getInfor_Flag() {
		return infor_Flag;
	}

	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}

	public String getC_expr() {
		return c_expr;
	}

	public void setC_expr(String c_expr) {
		this.c_expr = c_expr;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.getItem().clearValues();
		super.reset(arg0, arg1);
	}

	public String getCodelen() {
		return codelen;
	}

	public void setCodelen(String codelen) {
		this.codelen = codelen;
	}

	public String getSdata_src() {
		return sdata_src;
	}

	public void setSdata_src(String sdata_src) {
		this.sdata_src = sdata_src;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public ArrayList getOrlist() {
		return orlist;
	}

	public void setOrlist(ArrayList orlist) {
		this.orlist = orlist;
	}

	public String getColo() {
		return colo;
	}

	public void setColo(String colo) {
		this.colo = colo;
	}

	public String[] getOrder() {
		return order;
	}

	public void setOrder(String[] order) {
		this.order = order;
	}

	public String getSige() {
		return sige;
	}

	public void setSige(String sige) {
		this.sige = sige;
	}

	public String getSigh() {
		return sigh;
	}

	public void setSigh(String sigh) {
		this.sigh = sigh;
	}

	public String getAler() {
		return aler;
	}

	public void setAler(String aler) {
		this.aler = aler;
	}

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getExpr_flag() {
		return expr_flag;
	}

	public void setExpr_flag(String expr_flag) {
		this.expr_flag = expr_flag;
	}

	public String getChildlen() {
		return childlen;
	}

	public void setChildlen(String childlen) {
		this.childlen = childlen;
	}

	public String getItem_sy() {
		return item_sy;
	}

	public void setItem_sy(String item_sy) {
		this.item_sy = item_sy;
	}

	public ArrayList getMainlist() {
		return mainlist;
	}

	public void setMainlist(ArrayList mainlist) {
		this.mainlist = mainlist;
	}

	public String getTemidtype() {
		return temidtype;
	}

	public void setTemidtype(String temidtype) {
		this.temidtype = temidtype;
	}

	public String getSubset() {
		return subset;
	}

	public void setSubset(String subset) {
		this.subset = subset;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public ArrayList getItemidlist() {
		return itemidlist;
	}

	public void setItemidlist(ArrayList itemidlist) {
		this.itemidlist = itemidlist;
	}

	public ArrayList getListdate() {
		return listdate;
	}

	public void setListdate(ArrayList listdate) {
		this.listdate = listdate;
	}

	public String getAkq_item() {
		return akq_item;
	}

	public void setAkq_item(String akq_item) {
		this.akq_item = akq_item;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getGw_flag() {
		return gw_flag;
	}

	public void setGw_flag(String gwFlag) {
		gw_flag = gwFlag;
	}

}
