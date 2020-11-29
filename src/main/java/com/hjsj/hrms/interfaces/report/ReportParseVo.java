package com.hjsj.hrms.interfaces.report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ReportParseVo {
	/**考勤报表**/
    //报表名称
    private String name;
    //报表纸张
    private String pagetype;
    private ArrayList pagetypelist=new ArrayList();
    private ArrayList fnlist = new ArrayList();
    private ArrayList fzlist=new ArrayList();
    //报表长度单位
    private String unit;    
    //纸张方向
    private String orientation;
    //报表头边距
    private String top;
    //报表尾边距
    private String bottom;
    //报表左边距
    private String left;
    //报表右边距
    private String right;
    //值
    private String value;
    //纸的长宽
    private String width;
    private String height;    
     
    /**报表标题**/   
    //&fb[0],&fi[0],&fu[0],&fn,&fz10" height="80"
    private String title_fb;   
    private String title_fi;
    private String title_fu;
    private String title_fn;
    private String title_fz;    
    private String title_h;//标题边框高度   
    private String title_fw;//内容
    private String title_fc;//颜色
    private String title_fs;//删除线
    /**报表表头**/
    //  &p,&c,&e,&u,&d,&t,&fb[0],&fi[0],&fu[0],&fn,&fz10
    private String head_p;//页码）
    private String head_c;//总页数
    private String head_e;//制作人
    private String head_u;//制作人所在的单位
    private String head_d;//日期
    private String head_t;//时间
    private String head_fb;//粗体
    private String head_fi;//斜体
    private String head_fu;//下划线
    private String head_fn;//字体名称
    private String head_fz;//字体大小
    private String head_h;//高
    private String head_flw;//左边内容
    private String head_fmw;//中间内容
    private String head_frw;//右边内容
    private String head_fc;//颜色
    private String head_fw;//考勤内容
    private String head_fs;//删除线
    private String head_flw_hs;//上左内容仅首页显示
    private String head_fmw_hs;//上中内容仅首页显示
    private String head_frw_hs;//上右内容仅首页显示
    /**表尾**/ 
    private String tile_p;//页码）
    private String tile_c;//总页数
    private String tile_e;//制作人
    private String tile_u;//制作人所在的单位
    private String tile_d;//日期
    private String tile_t;//时间
    private String tile_fb;
    private String tile_fi;
    private String tile_fu;
    private String tile_fn;
    private String tile_fz;
    private String tile_h;
    private String tile_flw;
    private String tile_fmw;
    private String tile_frw;
    private String tile_fc;
    private String tile_fw;//考勤内容
    private String tile_fs;
    private String tile_flw_hs;//上左内容仅首页显示
    private String tile_fmw_hs;//上中内容仅首页显示
    private String tile_frw_hs;//上右内容仅首页显示
    /**表体**/    
    private String body_fb;
    private String body_fi;
    private String body_fu;
    private String body_fn;
    private String body_fz;
    private String body_pr;//自动换行0，制定行数1
    private String body_rn;//行数   
    private String body_fc;
    private String body_dept;// 部门
    private String body_pos;//职位
    private String body_gh;//工号
    private String body_kqfu;// 考勤符号
    private String body_tjxm;// 统计项目
    /**工资报表中，增加表头信息*/
    private String thead_fn;
    private String thead_fz;
    private String thead_fb;
    private String thead_fu;
    private String thead_fi;
    private String thead_fc;
	public String getHead_fs() {
		return head_fs;
	}
	public void setHead_fs(String head_fs) {
		this.head_fs = head_fs;
	}
	public String getTile_fs() {
		return tile_fs;
	}
	public void setTile_fs(String tile_fs) {
		this.tile_fs = tile_fs;
	}
	public String getTitle_fs() {
		return title_fs;
	}
	public void setTitle_fs(String title_fs) {
		this.title_fs = title_fs;
	}
	public String getBottom() {
		return bottom;
	}
	public void setBottom(String bottom) {
		this.bottom = bottom;
	}
	
	
	public void setHead_h(String head_h) {
		this.head_h = head_h;
	}
	public String getHeight() 
	{   if(StringUtils.isBlank(height))
		 height="297";
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrientation() {
		if(orientation==null||orientation.length()<=0)
		{
			orientation= "0";
		}
		return orientation;
	}
	public void setOrientation(String orientation) {
		
		this.orientation = orientation;
	}
	public String getPagetype() {
		return pagetype;
	}
	public void setPagetype(String pagetype) {
		if(pagetype==null||pagetype.length()<=0|| "请选择".equals(pagetype))
			pagetype="A4";
		this.pagetype = pagetype;
	}
	public String getRight() {
		return right;
	}
	public void setRight(String right) {
		this.right = right;
	}	
	
	public String getTile_h() {
		return tile_h;
	}
	public void setTile_h(String tile_h) {
		this.tile_h = tile_h;
	}
	
	
	public String getTitle_h() {
		return title_h;
	}
	public void setTitle_h(String title_h) {
		this.title_h = title_h;
	}
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getUnit() {
		
		if(unit==null||unit.length()<=0)
		{
			unit= "px";
		}
		return unit;
	}
	public void setUnit(String unit) {
		
		this.unit = unit;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getWidth() {
		if(StringUtils.isBlank(width))
			width="210";
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHead_h() {
		return head_h;
	}
	public String getBody_fb() {
		return body_fb;
	}
	public void setBody_fb(String body_fb) {
		//System.out.println("String body_fb="+body_fb);
		this.body_fb = body_fb;
		//System.out.println("this.body_fb="+this.body_fb);
	}
	public String getBody_fi() {
		return body_fi;
	}
	public void setBody_fi(String body_fi) {
		this.body_fi = body_fi;
	}
	public String getBody_fn() {
		return body_fn;
	}
	public void setBody_fn(String body_fn) {
		this.body_fn = body_fn;
	}
	public String getBody_fu() {
		return body_fu;
	}
	public void setBody_fu(String body_fu) {
		this.body_fu = body_fu;
	}
	public String getBody_fz() {
		return body_fz;
	}
	public void setBody_fz(String body_fz) {
		this.body_fz = body_fz;
	}
	public String getBody_pr() {
		if(StringUtils.isBlank(body_pr))
		{
			body_pr="#pr[0]";
		}
		return body_pr;
	}
	public void setBody_pr(String body_pr) {
		this.body_pr = body_pr;
	}
	public String getHead_fb() {
		return head_fb;
	}
	public void setHead_fb(String head_fb) {
		this.head_fb = head_fb;
	}
	public String getHead_fi() {
		return head_fi;
	}
	public void setHead_fi(String head_fi) {
		this.head_fi = head_fi;
	}
	public String getHead_fn() {
		return head_fn;
	}
	public void setHead_fn(String head_fn) {
		this.head_fn = head_fn;
	}
	public String getHead_fu() {
		return head_fu;
	}
	public void setHead_fu(String head_fu) {
		this.head_fu = head_fu;
	}
	public String getHead_fz() {
		return head_fz;
	}
	public void setHead_fz(String head_fz) {
		this.head_fz = head_fz;
	}	
	public String getTile_fb() {
		return tile_fb;
	}
	public void setTile_fb(String tile_fb) {
		this.tile_fb = tile_fb;
	}
	public String getTile_fi() {
		return tile_fi;
	}
	public void setTile_fi(String tile_fi) {
		this.tile_fi = tile_fi;
	}
	public String getTile_fn() {
		return tile_fn;
	}
	public void setTile_fn(String tile_fn) {
		this.tile_fn = tile_fn;
	}
	public String getTile_fu() {
		return tile_fu;
	}
	public void setTile_fu(String tile_fu) {
		this.tile_fu = tile_fu;
	}
	public String getTile_fz() {
		return tile_fz;
	}
	public void setTile_fz(String tile_fz) {
		this.tile_fz = tile_fz;
	}
	public String getTitle_fb() {
		return title_fb;
	}
	public void setTitle_fb(String title_fb) {
		this.title_fb = title_fb;
	}
	public String getTitle_fi() {
		return title_fi;
	}
	public void setTitle_fi(String title_fi) {
		this.title_fi = title_fi;
	}
	public String getTitle_fn() {
		return title_fn;
	}
	public void setTitle_fn(String title_fn) {
		this.title_fn = title_fn;
	}
	public String getTitle_fu() {
		return title_fu;
	}
	public void setTitle_fu(String title_fu) {
		this.title_fu = title_fu;
	}
	public String getTitle_fz() {
		return title_fz;
	}
	public void setTitle_fz(String title_fz) {
		this.title_fz = title_fz;
	}
	public ArrayList getPagetypelist() {
		pagetypelist=new ArrayList();
		CommonData vo = new CommonData();		
		vo.setDataName("请选择");
		vo.setDataValue("");
		pagetypelist.add(vo);
		vo=new CommonData();
		vo.setDataName("A3");
		vo.setDataValue("A3");
		pagetypelist.add(vo);
		vo=new CommonData();
		vo.setDataName("A4");
		vo.setDataValue("A4");
		pagetypelist.add(vo);
		vo=new CommonData();
		vo.setDataName("A5");
		vo.setDataValue("A5");
		pagetypelist.add(vo);
		vo=new CommonData();
		vo.setDataName("B5");
		vo.setDataValue("B5");
		pagetypelist.add(vo);
		vo=new CommonData();
		vo.setDataName("16开");
		vo.setDataValue("16开");
		pagetypelist.add(vo);	
		vo=new CommonData();
		vo.setDataName("自定义");
		vo.setDataValue("self");
		pagetypelist.add(vo);
		return pagetypelist;
	}
	public void setPagetypelist(ArrayList pagetypelist) {
		this.pagetypelist = pagetypelist;
	}
	public ArrayList getFzlist() {
		fzlist=new ArrayList();
		CommonData vo = new CommonData();		
		vo.setDataName("5 px");
		vo.setDataValue("5");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("8 px");
		vo.setDataValue("8");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("10 px");
		vo.setDataValue("10");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("12 px");
		vo.setDataValue("12");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("14 px");
		vo.setDataValue("14");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("16 px");
		vo.setDataValue("16");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("24 px");
		vo.setDataValue("24");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("32 px");
		vo.setDataValue("32");
		fzlist.add(vo);
		vo=new CommonData();
		vo.setDataName("48 px");
		vo.setDataValue("48");
		fzlist.add(vo);
		return fzlist;
	}
	public void setFzlist(ArrayList fzlist) {
		this.fzlist = fzlist;
	}
	public ArrayList getFnlist() {
		fnlist=new ArrayList();
		CommonData vo = new CommonData();		
		vo.setDataName("请选择字体");
		vo.setDataValue("");
		fnlist.add(vo);
		vo = new CommonData();		
		vo.setDataName("楷体_GB2312");
		vo.setDataValue("楷体_GB2312");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("方正舒体");
		vo.setDataValue("方正舒体");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("仿宋体");
		vo.setDataValue("仿宋体");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("华文彩云");
		vo.setDataValue("华文彩云");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("华文仿宋");
		vo.setDataValue("华文仿宋");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("华文细黑");
		vo.setDataValue("华文细黑");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("华文行楷");
		vo.setDataValue("华文行楷");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("华文中宋");
		vo.setDataValue("华文中宋");
		fnlist.add(vo);
		vo=new CommonData();
		vo.setDataName("隶书");
		vo.setDataValue("隶书");
		fnlist.add(vo);	
		vo=new CommonData();
		vo.setDataName("幼圆");
		vo.setDataValue("幼圆");
		fnlist.add(vo);
		return fnlist;
	}
	public void setFnlist(ArrayList fnlist) {
		this.fnlist = fnlist;
	}
	public String getBody_rn() {
		return body_rn;
	}
	public void setBody_rn(String body_rn) {
		this.body_rn = body_rn;
	}
	public String getTile_c() {
		return tile_c;
	}
	public void setTile_c(String tile_c) {
		this.tile_c = tile_c;
	}
	public String getTile_d() {
		return tile_d;
	}
	public void setTile_d(String tile_d) {
		this.tile_d = tile_d;
	}
	public String getTile_e() {
		return tile_e;
	}
	public void setTile_e(String tile_e) {
		this.tile_e = tile_e;
	}
	public String getTile_p() {
		return tile_p;
	}
	public void setTile_p(String tile_p) {
		this.tile_p = tile_p;
	}
	public String getTile_t() {
		return tile_t;
	}
	public void setTile_t(String tile_t) {
		this.tile_t = tile_t;
	}
	public String getTile_u() {
		return tile_u;
	}
	public void setTile_u(String tile_u) {
		this.tile_u = tile_u;
	}
	public String getHead_c() {
		return head_c;
	}
	public void setHead_c(String head_c) {
		this.head_c = head_c;
	}
	public String getHead_d() {
		return head_d;
	}
	public void setHead_d(String head_d) {
		this.head_d = head_d;
	}
	public String getHead_e() {
		return head_e;
	}
	public void setHead_e(String head_e) {
		this.head_e = head_e;
	}
	public String getHead_p() {
		return head_p;
	}
	public void setHead_p(String head_p) {
		this.head_p = head_p;
	}
	public String getHead_t() {
		return head_t;
	}
	public void setHead_t(String head_t) {
		this.head_t = head_t;
	}
	public String getHead_u() {
		return head_u;
	}
	public void setHead_u(String head_u) {
		this.head_u = head_u;
	}
	public String getHead_fw() {
		return head_fw;
	}
	public void setHead_fw(String head_fw) {
		this.head_fw = head_fw;
	}
	public String getTile_fw() {
		return tile_fw;
	}
	public void setTile_fw(String tile_fw) {
		this.tile_fw = tile_fw;
	}
	public String getTitle_fw() {
		return title_fw;
	}
	public void setTitle_fw(String title_fw) {
		this.title_fw = title_fw;
	}
	public String getBody_fc() {
		return body_fc;
	}
	public void setBody_fc(String body_fc) {
		this.body_fc = body_fc;
	}
	public String getHead_fc() {
		return head_fc;
	}
	public void setHead_fc(String head_fc) {
		this.head_fc = head_fc;
	}

	public String getHead_flw() {
		return head_flw;
	}
	public void setHead_flw(String head_flw) {
		this.head_flw = head_flw;
	}
	public String getHead_fmw() {
		return head_fmw;
	}
	public void setHead_fmw(String head_fmw) {
		this.head_fmw = head_fmw;
	}
	public String getHead_frw() {
		return head_frw;
	}
	public void setHead_frw(String head_frw) {
		this.head_frw = head_frw;
	}
	public String getTile_flw() {
		return tile_flw;
	}
	public void setTile_flw(String tile_flw) {
		this.tile_flw = tile_flw;
	}
	public String getTile_fmw() {
		return tile_fmw;
	}
	public void setTile_fmw(String tile_fmw) {
		this.tile_fmw = tile_fmw;
	}
	public String getTile_frw() {
		return tile_frw;
	}
	public void setTile_frw(String tile_frw) {
		this.tile_frw = tile_frw;
	}
	public String getTile_fc() {
		return tile_fc;
	}
	public void setTile_fc(String tile_fc) {
		this.tile_fc = tile_fc;
	}
	
	public String getTitle_fc() {
		return title_fc;
	}
	public void setTitle_fc(String title_fc) {
		this.title_fc = title_fc;
	}
	public String getBody_dept() {
		return body_dept;
	}
	public void setBody_dept(String body_dept) {
		this.body_dept = body_dept;
	}
	public String getBody_pos() {
		return body_pos;
	}
	public void setBody_pos(String body_pos) {
		this.body_pos = body_pos;
	}
	public String getBody_gh() {
		return body_gh;
	}
	public void setBody_gh(String body_gh) {
		this.body_gh = body_gh;
	}
	public String getBody_kqfu() {
		return body_kqfu;
	}
	public void setBody_kqfu(String body_kqfu) {
		this.body_kqfu = body_kqfu;
	}
	public String getBody_tjxm() {
		return body_tjxm;
	}
	public void setBody_tjxm(String body_tjxm) {
		this.body_tjxm = body_tjxm;
	}
	public String getThead_fn() {
		return thead_fn;
	}
	public void setThead_fn(String thead_fn) {
		this.thead_fn = thead_fn;
	}
	public String getThead_fz() {
		return thead_fz;
	}
	public void setThead_fz(String thead_fz) {
		this.thead_fz = thead_fz;
	}
	public String getThead_fb() {
		return thead_fb;
	}
	public void setThead_fb(String thead_fb) {
		this.thead_fb = thead_fb;
	}
	public String getThead_fu() {
		return thead_fu;
	}
	public void setThead_fu(String thead_fu) {
		this.thead_fu = thead_fu;
	}
	public String getThead_fi() {
		return thead_fi;
	}
	public void setThead_fi(String thead_fi) {
		this.thead_fi = thead_fi;
	}
	public String getThead_fc() {
		return thead_fc;
	}
	public void setThead_fc(String thead_fc) {
		this.thead_fc = thead_fc;
	}
	public String getHead_flw_hs() {
		return head_flw_hs;
	}
	public void setHead_flw_hs(String head_flw_hs) {
		this.head_flw_hs = head_flw_hs;
	}
	public String getHead_fmw_hs() {
		return head_fmw_hs;
	}
	public void setHead_fmw_hs(String head_fmw_hs) {
		this.head_fmw_hs = head_fmw_hs;
	}
	public String getHead_frw_hs() {
		return head_frw_hs;
	}
	public void setHead_frw_hs(String head_frw_hs) {
		this.head_frw_hs = head_frw_hs;
	}
	public String getTile_flw_hs() {
		return tile_flw_hs;
	}
	public void setTile_flw_hs(String tile_flw_hs) {
		this.tile_flw_hs = tile_flw_hs;
	}
	public String getTile_fmw_hs() {
		return tile_fmw_hs;
	}
	public void setTile_fmw_hs(String tile_fmw_hs) {
		this.tile_fmw_hs = tile_fmw_hs;
	}
	public String getTile_frw_hs() {
		return tile_frw_hs;
	}
	public void setTile_frw_hs(String tile_frw_hs) {
		this.tile_frw_hs = tile_frw_hs;
	}
	
	/**
	 * 将前台的参数拼接成xml格式
	 * @param pagesetupValue页面设置
	 * @param titleValue页标题
	 * @param pageheadValue页头
	 * @param pagetailidValue页尾
	 * @param textValueValue正文设置
	 * @return
	 */
	public static ReportParseVo setReportDetailXml(MorphDynaBean pagesetupValue,MorphDynaBean titleValue,MorphDynaBean pageheadValue,MorphDynaBean pagetailidValue,MorphDynaBean textValueValue) {
		ReportParseVo parsevo = new ReportParseVo();
		/** 页面设置 **/
		String width = "";
		String height = "";
		String pagetype = (String) pagesetupValue.get("pagetype-input");
		if (pagetype == null || pagetype.length() <= 0) {
			pagetype = "A4";
		}else if("a3".equalsIgnoreCase(pagetype)){//由于页面上宽和高是置灰不让修改的form表单提交不上来
			width="297";height="420";
		}else if("a4".equalsIgnoreCase(pagetype)){
			width="210";height="297";
		}else if("a5".equalsIgnoreCase(pagetype)){
			width="148";height="201";
		}else if("b5".equalsIgnoreCase(pagetype)){
			width="182";height="257";
		}else if("16开".equalsIgnoreCase(pagetype)){
			width="184";height="260";
		}else if("32开".equalsIgnoreCase(pagetype)){
			width="130";height="184";
		}
		
		parsevo.setPagetype(pagetype);
		parsevo.setWidth(StringUtils.isBlank(width)?(String)pagesetupValue.get("pagewidth-input"):width);
		parsevo.setHeight(StringUtils.isBlank(height)?(String) pagesetupValue.get("pageheight-input"):height);
		parsevo.setLeft((String) pagesetupValue.get("pageleft-input"));
		parsevo.setRight((String) pagesetupValue.get("pageright-input"));
		parsevo.setTop((String) pagesetupValue.get("pagetop-input"));
		parsevo.setBottom((String) pagesetupValue.get("pagebottom-input"));
		parsevo.setOrientation((String) pagesetupValue.get("Orientation"));
		/** 标题 **/
		parsevo.setTitle_fn((String) titleValue.get("title_fn-input"));
		parsevo.setTitle_fz((String) titleValue.get("title_fz-input"));
		parsevo.setTitle_fw((String) titleValue.get("titleTextarea"));
		parsevo.setTitle_fc((String) titleValue.get("colorTitle-input"));// 颜色				
		Map<String,String> titleValuetemp = PubFunc.DynaBean2Map(titleValue);
		if(titleValuetemp.get("checkboxgroupTitle")!=null){
			Object titleCheckboxgroup = (Object) titleValue.get("checkboxgroupTitle");						
		if (titleCheckboxgroup instanceof ArrayList) {
			ArrayList tcpList = (ArrayList) titleCheckboxgroup;
			for (int i = 0; i < tcpList.size(); i++) {
				if ("#fb[1]".equals(tcpList.get(i))) {
					parsevo.setTitle_fb("#fb[1]");
				}
				if ("#fi[1]".equals(tcpList.get(i))) {
					parsevo.setTitle_fi("#fi[1]");
				}
				if ("#fs[1]".equals(tcpList.get(i))) {
					parsevo.setTitle_fs("#fs[1]");
				}
				if ("#fu[1]".equals(tcpList.get(i))) {
					parsevo.setTitle_fu("#fu[1]");
				}
			}
		} else if (titleCheckboxgroup instanceof String) {
			String tcpListStr = (String) titleCheckboxgroup;
			if (tcpListStr != null) {
				if ("#fb[1]".equals(tcpListStr)) {
					parsevo.setTitle_fb("#fb[1]");
				}
				if ("#fi[1]".equals(tcpListStr)) {
					parsevo.setTitle_fi("#fi[1]");
				}
				if ("#fs[1]".equals(tcpListStr)) {
					parsevo.setTitle_fs("#fs[1]");
				}
				if ("#fu[1]".equals(tcpListStr)) {
					parsevo.setTitle_fu("#fu[1]");
				}
			}
		}}
		
		/** 节点,报表表头 **/

		parsevo.setHead_fn((String) pageheadValue.get("head_fn-input"));
		parsevo.setHead_fz((String) pageheadValue.get("head_fz-input"));
		parsevo.setHead_fc((String) pageheadValue.get("colorHead-input"));// 颜色
		parsevo.setHead_flw((String) pageheadValue.get("hlTextarea"));// 左上
		parsevo.setHead_fmw((String) pageheadValue.get("hcTextarea"));// 左中
		parsevo.setHead_frw((String) pageheadValue.get("hrTextarea"));// 左下		
		Map<String,String> pageheadValuetemp = PubFunc.DynaBean2Map(pageheadValue);	
		
		if(pageheadValuetemp.get("homeShow")!=null){//是否首页显示
			Object osHCheckboxgroup = (Object) pageheadValue.get("homeShow");
			if (osHCheckboxgroup instanceof ArrayList) {
				ArrayList osHList = (ArrayList) osHCheckboxgroup;
				for (int i = 0; i < osHList.size(); i++) {
					if ("lHeadChecked".equals(osHList.get(i))) {
						parsevo.setHead_flw_hs("lHeadChecked");
					}
					if ("mHeadChecked".equals(osHList.get(i))) {
						parsevo.setHead_fmw_hs("mHeadChecked");
					}
					if ("rHeadChecked".equals(osHList.get(i))) {
						parsevo.setHead_frw_hs("rHeadChecked");
					}
				}
			} else if (osHCheckboxgroup instanceof String) {
				String osHStr = (String) osHCheckboxgroup;
				if (osHStr != null) {
					if ("lHeadChecked".equals(osHStr)) {
						parsevo.setHead_flw_hs("lHeadChecked");
					}
					if ("mHeadChecked".equals(osHStr)) {
						parsevo.setHead_fmw_hs("mHeadChecked");
					}
					if ("rHeadChecked".equals(osHStr)) {
						parsevo.setHead_frw_hs("rHeadChecked");
					}
				}
			}
		}
		
		if(pageheadValuetemp.get("phCheckboxgroup")!=null){
			Object phCheckboxgroup = (Object) pageheadValue.get("phCheckboxgroup");
		if (phCheckboxgroup instanceof ArrayList) {
			ArrayList phList = (ArrayList) phCheckboxgroup;
			for (int i = 0; i < phList.size(); i++) {
				if ("#fb[1]".equals(phList.get(i))) {
					parsevo.setHead_fb("#fb[1]");
				}
				if ("#fi[1]".equals(phList.get(i))) {
					parsevo.setHead_fi("#fi[1]");
				}
				if ("#fs[1]".equals(phList.get(i))) {
					parsevo.setHead_fs("#fs[1]");
				}
				if ("#fu[1]".equals(phList.get(i))) {
					parsevo.setHead_fu("#fu[1]");
				}
			}
		} else if (phCheckboxgroup instanceof String) {
			String phStr = (String) phCheckboxgroup;
			if (phStr != null) {
				if ("#fb[1]".equals(phStr)) {
					parsevo.setHead_fb("#fb[1]");
				}
				if ("#fi[1]".equals(phStr)) {
					parsevo.setHead_fi("#fi[1]");
				}
				if ("#fs[1]".equals(phStr)) {
					parsevo.setHead_fs("#fs[1]");
				}
				if ("#fu[1]".equals(phStr)) {
					parsevo.setHead_fu("#fu[1]");
				}
			}
		}
		}
		/** 节点,报表表尾 **/
		parsevo.setTile_fn((String) pagetailidValue.get("tail_fn-input"));
		parsevo.setTile_fz((String) pagetailidValue.get("tail_fz-input"));
		parsevo.setTile_flw((String) pagetailidValue.get("tlTextarea"));
		parsevo.setTile_frw((String) pagetailidValue.get("trTextarea"));
		parsevo.setTile_fmw((String) pagetailidValue.get("tcTextarea"));
		Map<String,String> pagetailidValuetemp = PubFunc.DynaBean2Map(pagetailidValue);	
		
		if(pagetailidValuetemp.get("footShow")!=null){//是否首页显示
			Object footShow = (Object) pagetailidValue.get("footShow");
			if (footShow instanceof ArrayList) {
				ArrayList osFList = (ArrayList) footShow;
				for (int i = 0; i < osFList.size(); i++) {
					if ("lFootChecked".equals(osFList.get(i))) {
						parsevo.setTile_flw_hs("lFootChecked");
					}
					if ("mFootChecked".equals(osFList.get(i))) {
						parsevo.setTile_fmw_hs("mFootChecked");
					}
					if ("rFootChecked".equals(osFList.get(i))) {
						parsevo.setTile_frw_hs("rFootChecked");
					}
				}
			} else if (footShow instanceof String) {
				String osFStr = (String) footShow;
				if (osFStr != null) {
					if ("lFootChecked".equals(osFStr)) {
						parsevo.setTile_flw_hs("lFootChecked");
					}
					if ("mFootChecked".equals(osFStr)) {
						parsevo.setTile_fmw_hs("mFootChecked");
					}
					if ("rFootChecked".equals(osFStr)) {
						parsevo.setTile_frw_hs("rFootChecked");
					}
				}
			}
		}
		
		if(pagetailidValuetemp.get("ptCheckboxgroup")!=null){			
		Object ptCheckboxgroup = (Object) pagetailidValue.get("ptCheckboxgroup");
		if (ptCheckboxgroup instanceof ArrayList) {
			ArrayList ptList = (ArrayList) ptCheckboxgroup;
			for (int i = 0; i < ptList.size(); i++) {
				if ("#fb[1]".equals(ptList.get(i))) {
					parsevo.setTile_fb("#fb[1]");
				}
				if ("#fi[1]".equals(ptList.get(i))) {
					parsevo.setTile_fi("#fi[1]");
				}
				if ("#fs[1]".equals(ptList.get(i))) {
					parsevo.setTile_fs("#fs[1]");
				}
				if ("#fu[1]".equals(ptList.get(i))) {
					parsevo.setTile_fu("#fu[1]");
				}
			}
		} else if (ptCheckboxgroup instanceof String) {
			String ptStr = (String) ptCheckboxgroup;
			if (ptStr != null) {
				if ("#fb[1]".equals(ptStr)) {
					parsevo.setTile_fb("#fb[1]");
				}
				if ("#fi[1]".equals(ptStr)) {
					parsevo.setTile_fi("#fi[1]");
				}
				if ("#fs[1]".equals(ptStr)) {
					parsevo.setTile_fs("#fs[1]");
				}
				if ("#fu[1]".equals(ptStr)) {
					parsevo.setTile_fu("#fu[1]");
				}
			}
		}
		}
		// if(tile_fc!=null&&tile_fc.length()>0)
		parsevo.setTile_fc((String) pagetailidValue.get("colorTail-input"));	
		/** 节点,报表表体 **/	
		parsevo.setBody_fn((String)textValueValue.get("text_fn-input"));
		parsevo.setBody_fz((String)textValueValue.get("text_fz-input"));
		parsevo.setBody_fc((String)textValueValue.get("text_fc-input"));	
		parsevo.setThead_fc((String)textValueValue.get("phead_fc-input"));	
		parsevo.setThead_fn((String)textValueValue.get("phead_fn-input"));		
		parsevo.setThead_fz((String)textValueValue.get("phead_fz-input"));
		Map<String,String> textValueValuetemp = PubFunc.DynaBean2Map(textValueValue);	
		if(textValueValuetemp.get("hiCheckboxgroup")!=null){
		Object hiCheckboxgroup = (Object)textValueValue.get("hiCheckboxgroup");		
		if (hiCheckboxgroup instanceof ArrayList) {
			ArrayList hiList = (ArrayList) hiCheckboxgroup;
			for (int i = 0; i < hiList.size(); i++) {				
				if ("#fb[1]".equals(hiList.get(i))) {
					parsevo.setBody_fb("#fb[1]");
				}
				if ("#fi[1]".equals(hiList.get(i))) {
					parsevo.setBody_fi("#fi[1]");
				}			
				if ("#fu[1]".equals(hiList.get(i))) {
					parsevo.setBody_fu("#fu[1]");
				}
			}
		} else if (hiCheckboxgroup instanceof String) {
			String hiStr = (String) hiCheckboxgroup;
			if (hiStr != null) {
				if ("#fb[1]".equals(hiStr)) {
					parsevo.setBody_fb("#fb[1]");
				}
				if ("#fi[1]".equals(hiStr)) {
					parsevo.setBody_fi("#fi[1]");
				}				
				if ("#fu[1]".equals(hiStr)) {
					parsevo.setBody_fu("#fu[1]");
				}
			}
		}
		}		
		if(textValueValuetemp.get("textCheckboxgroup")!=null){		
		Object textCheckboxgroup = (Object)textValueValue.get("textCheckboxgroup");
		if (textCheckboxgroup instanceof ArrayList) {
			ArrayList teList = (ArrayList) textCheckboxgroup;
			for (int i = 0; i < teList.size(); i++) {				
				if ("#fb[1]".equals(teList.get(i))) {
					parsevo.setThead_fb("#fb[1]");
				}
				if ("#fi[1]".equals(teList.get(i))) {
					parsevo.setThead_fi("#fi[1]");
				}			
				if ("#fu[1]".equals(teList.get(i))) {
					parsevo.setThead_fu("#fu[1]");
				}
			}
		} else if (textCheckboxgroup instanceof String) {
			String teStr = (String) textCheckboxgroup;
			if (teStr != null) {
				if ("#fb[1]".equals(teStr)) {
					parsevo.setThead_fb("#fb[1]");
				}
				if ("#fi[1]".equals(teStr)) {
					parsevo.setThead_fi("#fi[1]");
				}				
				if ("#fu[1]".equals(teStr)) {
					parsevo.setThead_fu("#fu[1]");
				}
			}
		}
		}
		return parsevo;
	}
	
	/**
	 * 对内容做一些特殊处理
	 * @param content
	 * @param view
	 * @param totalNum总人数
	 * @param name报表名称
	 * @param pageNum页码
	 * @return
	 */
	public String getRealcontent(String content,UserView view,int totalNum,String name,int pageNum,ContentDAO dao)
	{
		String str=content;
		RowSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			if(str.indexOf("&[页码]")!=-1)
				str=str.replaceAll("&\\[页码\\]",String.valueOf(pageNum));
		    if(str.indexOf("&[制作人]")!=-1  )
				str=str.replaceAll("&\\[制作人\\]",view.getUserFullName());
			if(str.indexOf("&[日期]")!=-1)
			{
				SimpleDateFormat d=new SimpleDateFormat("yyyy-MM-dd");
				str=str.replaceAll("&\\[日期\\]",d.format(new java.util.Date()));
			}
		    if(str.indexOf("&[时间]")!=-1)
			{
				SimpleDateFormat   formatter   =   new   java.text.SimpleDateFormat("HH:mm:ss");   
				str=str.replaceAll("&\\[时间\\]",formatter.format(new java.util.Date()));
			}
			if(str.indexOf("&[总行数]")!=-1)
			{
				str=str.replaceAll("&\\[总行数\\]",String.valueOf(totalNum));
			}
			if(str.indexOf("&[YYYY年YY月]")!=-1)
			{
				SimpleDateFormat d=new SimpleDateFormat("yyyy-MM");
				String ss = d.format(new Date());
				str = str.replaceAll("&\\[YYYY年YY月\\]", ss.substring(0,4)+"年"+ss.substring(5,7)+"月");
			}
			if(str.indexOf("&[年月]")!=-1)
			{
				SimpleDateFormat d=new SimpleDateFormat("yyyy-MM");
				str=str.replaceAll("&\\[年月\\]",d.format(new java.util.Date()));
			}
			if(str.indexOf("&[单位名称]")!=-1)
			{
				if(view.getA0100()!=null&&view.getA0100().trim().length()>0)
				{
					String sql = "select b0110 from "+view.getDbname()+"A01 where a0100=?";
					list.add(view.getA0100());
			    	rs =dao.search(sql,list);
			     	while(rs.next())
			     	{
			     		String b0110="";
			     		if(rs.getString("b0110")!=null)
			     			b0110=AdminCode.getCodeName("UN",rs.getString("b0110"));
		    	    	str=str.replaceAll("&\\[单位名称\\]",b0110);
			     	}
				}else{
					str=str.replaceAll("&\\[单位名称\\]","");
				}
			}
			if(str.indexOf("&[报表名称]")!=-1&&name!=null&&name.trim().length()>0)
			{
				str=str.replaceAll("&\\[报表名称\\]",name);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return str;
	}
}
