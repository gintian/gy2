package com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sql.RowSet;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.bjca.seal.SealVerify;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import com.aspose.pdf.Page;
import com.aspose.pdf.devices.JpegDevice;
import com.aspose.pdf.devices.Resolution;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.hjsj.hrms.businessobject.general.template.TFieldFormat;
import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TTitle;
import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.module.utils.asposeword.AnalysisWordUtil;
import com.hjsj.hrms.module.utils.asposeword.AsposeLicenseUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResetFontSizeUtil;
import com.hjsj.hrms.valueobject.ykcard.TRecParamView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;



/**
 * 导出word公共方法类
 * @author hjsoft.com.cn
 *
 */
public class OutWordBo {
	private ContentDAO dao=null;
	private Connection conn=null;
	/**模板号*/
	private int tabid=0;
	/**业务模板对应*/
	private int currentpage = 0;
	private int pages = 0;
	/**每英寸像素数
	 * default:windows 96
	 * mac             72
	 * */
	private int PixelInInch=96;
	private String noshow_pageno = "";//不显示页签
	private TemplateTableBo tablebo = null;
	private TemplateParam paramBo = null;
	private TemplatePageBo pagebo = null;
	private String task_id="";
	private int signtype=0;  //签章标识 0金格 1BJCA 2世纪
	private HashMap field_name_map  = new HashMap();//存储人为改变类型的字段
	private String downtype = "1"; //=0 一人一文档压缩下载 =1 多人一文档直接下载
	private String outtype = "";// 0 pdf 1 word
	private String show_pageno = "";//指定导出的页
	//选择导出兼容wps与officer格式
	private String officeOrWpsFlag = "0";
	/**登录用户*/
	private UserView userview;
	private boolean selfApply=false;//是否自助
	private String module_id = "1"; //默认是人事异动
	private String out_file_type="1"; //导出格式 1 分页导出 2 连续页导出且绘制子集单元格时程序判断当子集数据少于2条，子集表格高度不按模板绘制的高度设置最小值，最小值高度仅为2空行高度 
	TemplateUtilBo utilBo = null;
	TemplateDataBo dataBo = null;
	public String getOfficeOrWpsFlag() {
		return officeOrWpsFlag;
	}
	public void setOfficeOrWpsFlag(String officeOrWpsFlag) {
		this.officeOrWpsFlag = officeOrWpsFlag;
	}
	public OutWordBo(Connection conn,UserView userView,int tabid,String task_id)throws GeneralException {
		this.conn = conn;
		this.userview = userView;
		this.tabid=tabid;
		this.paramBo = new TemplateParam(conn, userView, tabid);
		this.tablebo=new TemplateTableBo(conn,tabid,userView);
		this.pagebo=new TemplatePageBo(this.conn,this.tabid,0,task_id);
		this.task_id = task_id;
		init(conn,userView);
	}
	private void init(Connection conn,UserView userView){
		this.conn = conn;
    	this.userview = userView;
    	dao = new ContentDAO(this.conn);                        
    	utilBo= new TemplateUtilBo(this.conn,this.userview);
    	dataBo= new TemplateDataBo(this.conn,this.userview,this.paramBo);
	}
	/***
	 * 对象列表
	 * @param objlist 存放的是:库前缀+用户编号
	 * 如果对调入人员时，不能对历史记录取数（也即档案中的数据不能取）
	 * @para infor =1人员,=2单位,=3职位
	 * @para inslist[0]=0 ,提交申请时的打印，=1审批过程中的打印
	 * @return
	 * @throws GeneralException
	 */
	public String outword(ArrayList objlist,int infor,ArrayList inslist)throws Exception {
		RecordVo tvo=tablebo.getTable_vo();
		this.out_file_type=tablebo.getOut_type();
		float[] wh =getWidthHeight(tvo);
		String prefix=this.userview.getUserName() + "_" + this.tablebo.getName();
		String filename="";
		/**模板临时表名*/
		String tabname=null;
		/**根据第一个实例是否为0来分析，具体采用什么表*/
		int ins_id=Integer.parseInt((String)inslist.get(0));
		if(ins_id==0)
			tabname=this.userview.getUserName()+"templet_"+this.tabid;
		else
			tabname="templet_"+this.tabid;
		if (selfApply){
		    tabname="g_templet_"+this.tabid;
		}
		AnalysisWordUtil awu = new AnalysisWordUtil();
		awu.setDowntype(this.downtype);
		awu.setOuttype(this.outtype);
		awu.setSigntype(this.signtype);
		awu.setOut_file_type(this.out_file_type);
		awu.setOfficerOrWps(this.getOfficeOrWpsFlag());
		try
		{
			boolean isHasPageOFld=false;
			DbWizard dbw = new DbWizard(this.conn);
			if (dbw.isExistField("template_Page", "paperOrientation",false)) {
				isHasPageOFld=true;				
			}
			prefix= prefix.replace("/", "／");
		   
			String obj_id=null;
			String dbpre=null;
			RowSet rset=null;
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			String insid="";
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
			/**所有变量*/
			HashMap var_hm=utilBo.getAllVariableHm(this.tabid);
			HashMap fieldmap=new HashMap();
			HashMap fieldmaps=new HashMap();
			if(this.task_id!=null&&this.task_id.trim().length()>0&&!"3".equals(this.paramBo.getReturnFlag())&&this.paramBo.getSp_mode()==0){
				fieldmaps=	this.pagebo.getFieldPriv(this.task_id,this.conn);
				fieldmap = this.pagebo.getFieldPrivByNode(this.task_id);
			}
			RowSet rs = null;
			StringBuffer sql=new StringBuffer();
			sql.append("select * from Template_Page where tabid=");
			sql.append(this.tabid);
			if("".equals(this.noshow_pageno))//如果有设置的不显示页签 优先走这个
				sql.append(" and isprn<>0");
			sql.append(" and "+Sql_switcher.isnull("ismobile", "0")+"<>1");
			sql.append(" and "+Sql_switcher.isnull("isshow", "1")+"<>0");
			sql.append(" order by pageid ");
			rs=dao.search(sql.toString());
			ArrayList pgidlist = new ArrayList();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			while(rs.next()){
				ArrayList rtoplist = new ArrayList();//内容的行数据集
				ArrayList rleftlist = new ArrayList();//内容的列数据集
				ArrayList titleList = new ArrayList(); //每页标题数据集
				ArrayList cellslist=new ArrayList();//每页显示的内容信息
				String temp=null;
				org.jdom.Document doc=null;
				Element element=null;
				String xpath="/sub_para/para";
				String pageid =  String.valueOf(rs.getInt("pageid"));
				if(!"".equals(this.noshow_pageno)){//如果有设置的不显示页签 优先走这个
					String[] pagearr = this.noshow_pageno.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint)
						continue;
				}else{
					if (!isHaveReadFieldPriv(pageid)) {//判断此页的指标有无读写权限。无读写权限指标的不导出
						continue;
					}
					String[] showpagearr = this.show_pageno.split(",");
					boolean print = false;
					for(String pid:showpagearr){
						if(pid.equalsIgnoreCase(pageid)){
							print = true;
							break;
						}
					}
					if(!print&&!"".equals(this.show_pageno)&&showpagearr.length!=0)
						continue;
				}
				sql.setLength(0);
				sql.append("select * from Template_Set where tabid=");
				sql.append(this.tabid);
				sql.append(" and pageid=");
				sql.append(rs.getInt("pageid"));
				sql.append(" order by rtop,rleft");
				rset=dao.search(sql.toString());
				Boolean isPrintPage=false;
				while(rset.next())
				{
					TemplateSetBo setbo=new TemplateSetBo(this.conn,display_e0122);
					setbo.setPagebo(new TemplatePageBo(this.conn,this.tabid,rset.getInt("PageID")));
					setbo.setHz(nullToSpace(rset.getString("hz")));//设置表格的汉字描述
					setbo.setSetname(nullToSpace(rset.getString("setname")));//设置子集的代码
					setbo.setCodeid(nullToSpace(rset.getString("codeid")));//相关的代码类
					setbo.setField_hz(nullToSpace(rset.getString("Field_hz")));//字段的汉子描述
					setbo.setField_name(nullToSpace(rset.getString("Field_name")));//指标的代码
					String flag=rset.getString("Flag")==null?"":rset.getString("Flag");//数据源的标识（文本描述、照片......）
					if(!"V".equalsIgnoreCase(flag)&&!"S".equalsIgnoreCase(flag)&&!"F".equalsIgnoreCase(flag)&&rset.getString("Field_name")!=null&&rset.getString("Field_type")!=null
							&&rset.getString("subflag")!=null&& "0".equals(rset.getString("subflag"))&&rset.getString("Field_name").trim().length()>0
							&&rset.getString("Field_type").trim().length()>0){
						if("codesetid".equalsIgnoreCase(rset.getString("Field_name"))|| "codeitemdesc".equalsIgnoreCase(rset.getString("Field_name"))||
								"corcode".equalsIgnoreCase(rset.getString("Field_name"))|| "parentid".equalsIgnoreCase(rset.getString("Field_name"))||
								"start_date".equalsIgnoreCase(rset.getString("Field_name"))){
							//这些特殊的字段的是不能从数据字典里获得的
						}else{
							FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name").trim());
							if(item==null){//数据字典里为空 2011 5 26 xieguiquan
								continue;
							}
						}
					}
					setbo.setField_type(nullToSpace(rset.getString("field_type")));
					setbo.setOld_fieldType(nullToSpace(rset.getString("field_type")));
					setbo.setFlag(nullToSpace(rset.getString("Flag")));//设置数据源的标识
					setbo.setFormula(nullToSpace(Sql_switcher.readMemo(rset,"Formula")));//设置字段的计算公式
					setbo.setAlign(rset.getInt("Align"));//文字在单元格中的排列方式
					setbo.setDisformat(rset.getInt("DisFormat"));//设置数据的格式  1,2,3,4对数值型为数值精度 后面是对时间的控制	
					/**变量*/
					if("V".equalsIgnoreCase(flag))
					{
						RecordVo vo=(RecordVo)var_hm.get(rset.getString("Field_name"));
						if(vo!=null)
						{
							setbo.setDisformat(vo.getInt("flddec"));//如果是临时变量 那么要根据临时变量表里面的小数位数来设置
						}					
					}
					if("S".equalsIgnoreCase(flag)){//签章赋值field_name,否则后面会报空指针
						setbo.setField_name("signature");
					}
					setbo.setChgstate(rset.getInt("ChgState"));//设置字段是变化前还是变化后
					setbo.setFonteffect(rset.getInt("Fonteffect"));//设置字体效果
					setbo.setFontname(rset.getString("FontName"));//设置字体名称
					setbo.setFontsize(rset.getInt("Fontsize"));//设置字体大小
					setbo.setHismode(rset.getInt("HisMode"));//设置历史定位方式
					if(Sql_switcher.searchDbServer()==2)
						setbo.setMode(rset.getInt("Mode_o"));
					else
						setbo.setMode(rset.getInt("Mode"));//多条记录的时候  那几种选择 (最近..最初..)
					setbo.setNsort(rset.getInt("nSort"));//相同指示顺序号
					setbo.setGridno(rset.getInt("gridno"));//单元格号
					setbo.setRcount(rset.getInt("Rcount"));//记录数 和HisMode 配合试用（标识最近（Rcount条））
					setbo.setRheight(rset.getInt("RHeight"));//设置单元格高度
					setbo.setRleft(rset.getInt("RLeft"));//单元格左边的坐标值
					setbo.setRwidth(rset.getInt("RWidth"));//单元格的宽度
					setbo.setRtop(rset.getInt("RTop"));//单元格上边坐标值
					setbo.setL(rset.getInt("L"));/**LBRT 代表着表格左下右上是否有线**/
					setbo.setB(rset.getInt("B"));
					setbo.setR(rset.getInt("R"));
					setbo.setT(rset.getInt("T"));
					temp=rset.getString("subflag");//子表控制符 0：字段 1：子集
					if(temp==null|| "".equals(temp)|| "0".equals(temp))
						setbo.setSubflag(false);
					else
						setbo.setSubflag(true);
					//节点必填项
					if(this.task_id!=null&&this.task_id.trim().length()>0&&!"0".equals(this.task_id)){//这里应该是判断在流程结点中定义的必填项
						if(fieldmaps!=null&&fieldmaps.get((setbo.getField_name()+"_"+setbo.getChgstate()).toLowerCase())!=null
								&&"3".equals((String)fieldmaps.get((setbo.getField_name()+"_"+setbo.getChgstate()).toLowerCase()))){
							setbo.setYneed(true);
						}else{
							if(rset.getInt("yneed")==0)
								setbo.setYneed(false);
							else
								setbo.setYneed(true);
						}
					}else{
						if(rset.getInt("yneed")==0)
							setbo.setYneed(false);
						else
							setbo.setYneed(true);
					}
					String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
					setbo.setXml_param(sub_domain);
					//获得sub_domain_id
					String sub_domain_id="";
					setbo.setSub_domain_id(sub_domain_id);
					if(sub_domain!=null&&sub_domain.trim().length()>0){
						try{
							doc=PubFunc.generateDom(sub_domain);
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
							List childlist=findPath.selectNodes(doc);	
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element)childlist.get(0);
								if(element.getAttributeValue("id")!=null&&("1".equals(""+rset.getInt("ChgState"))||setbo.isSubflag())){
									sub_domain_id=(String)element.getAttributeValue("id");
									if(sub_domain_id!=null&&sub_domain_id.trim().length()>0)
									setbo.setSub_domain_id(sub_domain_id);	
								}
							}
						}catch(Exception e){
							
						}
					}
					setbo.setField_type(rset.getString("Field_type"));
					if(!setbo.isSubflag()&&"1".equals(""+rset.getInt("ChgState"))&&flag!=null&&!"H".equals(flag.toUpperCase())){
						if(Sql_switcher.searchDbServer()==2){
							if(("2".equals(""+rset.getInt("HisMode")))||"3".equals(""+rset.getInt("HisMode"))||"4".equals(""+rset.getInt("HisMode"))){//(序号定位&&(最近||最初)) || 条件定位||条件序号
								setbo.setField_type("M");
								if(setbo.getField_name()!=null&&setbo.getField_name().length()>0){
									if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"));
									}else{
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+rset.getInt("ChgState"));
									}
								}
							}
						}else{
							if("2".equals(""+rset.getInt("HisMode"))||"3".equals(""+rset.getInt("HisMode"))||"4".equals(""+rset.getInt("HisMode"))){
								setbo.setField_type("M");
								if(setbo.getField_name()!=null&&setbo.getField_name().length()>0){
									if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"));
									}else{
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+rset.getInt("ChgState"));
									}
								}
							}
						}
					}
					if(rset.getString("nhide")!=null)
						setbo.setNhide(rset.getInt("nhide"));
					else
						setbo.setNhide(0);//打印还是隐藏 0：打印 1：隐藏
					if(!isPrintPage)
					{
						isPrintPage=setbo.getNhide()==0?true:false;
					}
					cellslist.add(setbo);
				}
				int b=0;
	            int l=0;
	            int r=0;
	            int t=0;
	            if(isPrintPage)
	            {
		            ArrayList new_setbo=new ArrayList();
		            for(int i=0;i<cellslist.size();i++){
		            	TemplateSetBo cur_setbo =(TemplateSetBo)cellslist.get(i);  
		                b=getRlineForList(cellslist,"b",cur_setbo.getB(),cur_setbo);
		                l=getRlineForList(cellslist,"l",cur_setbo.getL(),cur_setbo);
		                r=getRlineForList(cellslist,"r",cur_setbo.getR(),cur_setbo);
		                t=getRlineForList(cellslist,"t",cur_setbo.getT(),cur_setbo);
		                cur_setbo.setB(b);                  
		                cur_setbo.setL(l);
		                cur_setbo.setR(r);
		                cur_setbo.setT(t);
		                new_setbo.add(cur_setbo);
		            }
					sql.setLength(0);
					sql.append("select * from template_title where tabid=");
					sql.append(this.tabid);
					sql.append(" and pageid=");
					sql.append(rs.getInt("pageid"));
					sql.append(" order by rtop,rleft");
					RowSet roset=dao.search(sql.toString());
					while(roset.next()){
						String isPhoto="false";//是不是图片
						TTitle title=new TTitle();
						title.setGridno(roset.getInt("gridno"));
						title.setPageid(roset.getInt("pageid"));
						title.setTabid(roset.getInt("tabid"));
						title.setFlag(roset.getInt("flag"));
						title.setFonteffect(roset.getInt("Fonteffect"));
						title.setFontname(roset.getString("Fontname"));
						title.setFontsize(roset.getInt("Fontsize"));
						title.setHz(roset.getString("hz")==null?"":roset.getString("hz"));
						title.setRtop(roset.getInt("rtop"));
						title.setRleft(roset.getInt("rleft"));
						title.setRwidth(roset.getInt("rwidth"));
						title.setRheight(roset.getInt("rheight"));
						title.setExtendattr(Sql_switcher.readMemo(roset,"extendattr"));
						String titlevalue = "";
						LazyDynaBean titleBean = new LazyDynaBean();
						titleBean.set("gridno", roset.getInt("gridno"));
						titleBean.set("pageid",roset.getInt("pageid"));
						titleBean.set("tabid",roset.getInt("tabid"));
						titleBean.set("flag",roset.getInt("flag"));
						titleBean.set("fonteffect",roset.getInt("Fonteffect"));
						titleBean.set("fontname",roset.getString("Fontname"));
						titleBean.set("fontsize",roset.getInt("Fontsize"));
						titleBean.set("hz",roset.getString("hz")==null?"":roset.getString("hz"));
						titleBean.set("rtop",roset.getInt("rtop"));
						titleBean.set("rleft",roset.getInt("rleft"));
						titleBean.set("rwidth",roset.getInt("rwidth"));
						titleBean.set("rheight",roset.getInt("rheight"));
						titleBean.set("extendattr",Sql_switcher.readMemo(roset,"extendattr"));
						if(roset.getInt("flag")!=7&&roset.getInt("flag")!=5)//图片
							titlevalue = title.getOutText(this.userview,this.pages,this.currentpage);
						else if(title.getFlag()==7){//图片单独处理
							String ext=title.getPattern("ext",title.getExtendattr());
							title.setCon(this.conn);
							String fileName=title.createPhotoFile(ext);
							if(fileName!=null&&fileName.length()>0){
								titlevalue = System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
							}else{
								titlevalue = title.getOutText(this.userview,this.pages,this.currentpage);
							}
							isPhoto="true";
						}
						titleBean.set("titlevalue",titlevalue);
						titleBean.set("isPhoto",isPhoto);
						titleList.add(titleBean);
					}
					sql.setLength(0);
					sql.append("select DISTINCT rtop from Template_Set where tabid=");
					sql.append(this.tabid);
					sql.append(" and pageid=");
					sql.append(rs.getInt("pageid"));
					sql.append(" order by rtop");
					RowSet rosettop=dao.search(sql.toString());
					while(rosettop.next()){
						rtoplist.add(rosettop.getString("rtop"));
					}
					sql.setLength(0);
					sql.append("select DISTINCT rleft from Template_Set where tabid=");
					sql.append(this.tabid);
					sql.append(" and pageid=");
					sql.append(rs.getInt("pageid"));
					sql.append(" order by rleft");
					RowSet rosetleft=dao.search(sql.toString());
					while(rosetleft.next()){
						rleftlist.add(rosetleft.getString("rleft"));
					}
					LazyDynaBean pageBean = new LazyDynaBean();
					pageBean.set("context", new_setbo);//内容
					pageBean.set("title", titleList);//标题
					pageBean.set("rtops", rtoplist);//行 数  内容
					pageBean.set("rlefts", rleftlist);//列 数  内容
					//pageBean.set("fieldmap", fieldmap);//放到外面了 不用传了
					pageBean.set("wh", wh);//
					if (isHasPageOFld)				
						pageBean.set("paperOrientation",rs.getInt("paperOrientation")+"");
					else
						pageBean.set("paperOrientation","0");	
					pgidlist.add(pageBean);
	            }
			}
			this.pages=pgidlist.size();
			if(this.pages==0)
				throw new GeneralException("没有可打印的表页！");
			ArrayList outputList = new ArrayList();
			ArrayList filenameList = new ArrayList();
			HashMap filenameMap = new HashMap();
			HashSet nameOutList = new HashSet();
			for(int j=0;j<objlist.size();j++){ //人员对象
				String prefix_ = "";
				buf.setLength(0);
				paralist.clear();
				insid=(String)inslist.get(j);
				obj_id=(String)objlist.get(j);
				if("1".equals(""+this.tablebo.getInfor_type())){
					if(infor==1){//对人员要进行分库
						if(obj_id.length()<11){ //为空，未选中人员
						  dbpre="";
	 					  if(obj_id.length()!=8){ //人员编号
	 							obj_id="-1"; //打印空表
						  }
						}
						else{
							dbpre=obj_id.substring(0,3); //usr,oth,trs,...
							obj_id=obj_id.substring(3);
						}
					}
				}
				buf.append("select * from ");	
				buf.append(tabname);
				if("1".equals(""+this.tablebo.getInfor_type())){
					paralist.add(obj_id);
					paralist.add(dbpre);
					buf.append(" where a0100=? and basepre=?");
				}else if("2".equals(""+this.tablebo.getInfor_type())){
					paralist.add(obj_id);
					buf.append(" where b0110=? ");
				}else if("3".equals(""+this.tablebo.getInfor_type())){
					paralist.add(obj_id);
					buf.append(" where e01a1=? ");
				}else{
					paralist.add(obj_id);
					paralist.add(dbpre);
					buf.append(" where a0100=? and basepre=?");
				}
				if(ins_id!=0){
					buf.append(" and ins_id =?");
					paralist.add(Integer.valueOf(insid));
				}
				rset=dao.search(buf.toString(),paralist);
				if(rset.isBeforeFirst()){
                    rset.next();                        
                }
				if (this.paramBo.getInfor_type() == 2
						|| this.paramBo.getInfor_type() == 3) {//单位名称
					if (this.paramBo.getOperationType() == 5) {
						prefix_ = this.userview.getUserName() + "_" + this.tablebo.getName() + "_" + rset.getString("codeitemdesc_2");
					} else {
						prefix_ = this.userview.getUserName() + "_" + this.tablebo.getName() + "_" + rset.getString("codeitemdesc_1");
					}
				}
				if (this.paramBo.getInfor_type() == 1) {
					if (this.paramBo.getOperationType() == 0) {//人员调入型
						if (dbw.isExistField(tabname, "a0101_2", false)) {
							prefix_ = this.userview.getUserName() + "_" + this.tablebo.getName() + "_" + rset.getString("a0101_2");
						}
					} else {
						prefix_ = this.userview.getUserName() + "_" + this.tablebo.getName() + "_" + rset.getString("a0101_1");
					}
				}
				prefix_= prefix_.replace("/", "／");
				ArrayList oneoutputList = new ArrayList();
				for(int i=0;i<pgidlist.size();i++)//模板中的表页
				{	
					this.currentpage = i+1;
					ArrayList toptitleList = new ArrayList();
					ArrayList leavetitlelist = new ArrayList();
					ArrayList leavetitlelist_img = new ArrayList();
					ArrayList bomtitleList = new ArrayList();
					ArrayList toprtop_t = new ArrayList();
					ArrayList bomrtop_t = new ArrayList();
					ArrayList cellslist_new = new ArrayList();
					LazyDynaBean pgmap_new = new LazyDynaBean();
					LazyDynaBean pgmap = (LazyDynaBean) pgidlist.get(i);
					ArrayList titleList = (ArrayList) pgmap.get("title");//标题
					ArrayList cellslist=(ArrayList) pgmap.get("context");//单元格内容
					//HashMap fieldmap = (HashMap)pgmap.get("fieldmap");
					pgmap_new.set("rtops", pgmap.get("rtops"));
					pgmap_new.set("rlefts", pgmap.get("rlefts"));
					pgmap_new.set("fieldmap", fieldmap);
					pgmap_new.set("wh", pgmap.get("wh"));
					pgmap_new.set("paperOrientation", pgmap.get("paperOrientation"));
					org.jdom.Document doc=null;
					Element element = null;
					ArrayList numberRecParam=new ArrayList();  
					double fValue = 0.0f; 
	                if(rset.isBeforeFirst()){
	                    rset.next();                        
	                }
	               
					for(int aa = 0;aa<cellslist.size();aa++){
						TemplateSetBo setbo = (TemplateSetBo) cellslist.get(aa);
						String flag = setbo.getFlag();
						String fldname =setbo.getField_name();
	                    String fldtype =setbo.getField_type();
	                    String oldfldtype = setbo.getOld_fieldType();
	                    String sub_domain_id = setbo.getSub_domain_id();
	                    if(sub_domain_id!=null&&sub_domain_id.length()>0){
	                    	fldname=fldname+"_"+sub_domain_id;
	        			}
	                    int chgstate = setbo.getChgstate();   
	                    if (!"V".equals(flag))
	                       fldname =fldname+"_"+ String.valueOf(chgstate);
	                    fldname =fldname.toLowerCase();
	                    if ("N".equalsIgnoreCase(fldtype)||("M".equalsIgnoreCase(fldtype)&&"N".equalsIgnoreCase(oldfldtype))){
 	                       TRecParamView recP = new TRecParamView();
 	                       String numberValue = "";
 	                       if("M".equalsIgnoreCase(fldtype)&&"N".equalsIgnoreCase(oldfldtype)) {
 	                    	  numberValue = rset.getString(fldname);
 	                    	  if(StringUtils.isNotBlank(numberValue)&&numberValue.indexOf("`")==-1) {
 	                    		 fValue = Double.parseDouble(numberValue);
 	                    	  }else
 	                    		  continue;
 	                       }else
 	                    	   fValue = rset.getDouble(fldname);
 	                       recP.setBflag(true);
 	                       recP.setFvalue(String.valueOf(fValue));
 	                       recP.setNid(setbo.getGridno());
 	                       numberRecParam.add(recP);
	                    }
					}
					for(int m=0;m<cellslist.size();m++){
						LazyDynaBean cellbean = new LazyDynaBean();
						TemplateSetBo setbo = (TemplateSetBo) cellslist.get(m);
						int rtop = setbo.getRtop();
						int rheight = setbo.getRheight();
						if(m==0){
							//求得上部的标题数据集
							for(int c=0;c<titleList.size();c++){
								LazyDynaBean ttitle = (LazyDynaBean)titleList.get(c);
								int rtop_t = (Integer)ttitle.get("rtop");
								int flag = (Integer)ttitle.get("flag");
								if(rtop_t<rtop){
									toptitleList.add(ttitle);
									toprtop_t.add(rtop_t);
								}else {
									if(flag==7) {
										leavetitlelist_img.add(ttitle);
									}else
										leavetitlelist.add(ttitle);
								}
							}
							pgmap_new.set("rtops_t", toprtop_t);
							pgmap_new.set("toptitle", toptitleList);
						}
						if(m==cellslist.size()-1){
							//求得下部的标题数据集
							for(int d=0;d<titleList.size();d++){
								LazyDynaBean ttitle = (LazyDynaBean)titleList.get(d);
								int rtop_t = (Integer)ttitle.get("rtop");
								int flag = (Integer)ttitle.get("flag");
								int gridno = (Integer)ttitle.get("gridno");
								if(rtop_t>rtop+rheight){
									bomtitleList.add(ttitle);
									bomrtop_t.add(rtop_t);
								}else {
									if(flag==7) {
										for(int dd=0;dd<toptitleList.size();dd++) {
											LazyDynaBean ldtitle = (LazyDynaBean)toptitleList.get(dd);
											int topgn = (Integer)ldtitle.get("gridno");
											int flag_ = (Integer)ldtitle.get("flag");
											if(flag_==7) {
												if(gridno==topgn) {
													continue;
												}else {
													for(int dc=0;dc<leavetitlelist_img.size();dc++) {
														LazyDynaBean ldtitle_ = (LazyDynaBean)leavetitlelist_img.get(dc);
														int topgn_ = (Integer)ldtitle_.get("gridno");
														if(gridno==topgn_) {
															continue;
														}else {
															leavetitlelist_img.add(ttitle);
														}
													}
												}
											}
										}
									}else
										leavetitlelist.add(ttitle);
								}
							}
							pgmap_new.set("rtops_b", bomrtop_t);
							pgmap_new.set("bomtitle", bomtitleList);
						}
						cellbean.set("isTrans", "false");//默认是不转换的，只有单元格中插入标题图片才是true
						cellbean.set("rtop", setbo.getRtop());
						cellbean.set("rleft", setbo.getRleft());
						cellbean.set("rheight", setbo.getRheight());
						cellbean.set("rwidth", setbo.getRwidth());
						cellbean.set("hz", setbo.getHz()==null?"":setbo.getHz());
						cellbean.set("flag", setbo.getFlag()==null?"":setbo.getFlag());
						cellbean.set("l", setbo.getL());
						cellbean.set("b", setbo.getB());
						cellbean.set("r", setbo.getR());
						cellbean.set("t", setbo.getT());
						cellbean.set("align", setbo.getAlign());//大文本输出word默认靠左
						cellbean.set("fonteffect", setbo.getFonteffect());
						cellbean.set("fontname", setbo.getFontname()==null?"":setbo.getFontname());
						cellbean.set("fontsize", setbo.getFontsize());
						cellbean.set("nhide", setbo.getNhide());
						cellbean.set("field_type", setbo.getField_type()==null?"":setbo.getField_type());
						cellbean.set("pageid", setbo.getPagebo().getPageid());
						cellbean.set("gridno", setbo.getGridno());
						cellbean.set("setname", setbo.getSetname()==null?"":setbo.getSetname());
						String hz = setbo.getHz();//单元格的名称
						String sub_domain_id = setbo.getSub_domain_id();//子集格式
						String xml_param = setbo.getXml_param();//子集格式
						cellbean.set("sub_domain_id", setbo.getSub_domain_id()==null?"":setbo.getSub_domain_id());
						if("F".equalsIgnoreCase(setbo.getFlag())){//附件模拟子集
							setbo.setSubflag(true);
							setbo.setAttachmentXml(xml_param);//保存附件的设置信息，用于后面查询。
							xml_param = this.getAcctch_domain(xml_param,setbo);
							cellbean.set("setname", setbo.getSetname()==null?"":setbo.getSetname());
						}
						setbo.setXml_param(xml_param);
						cellbean.set("sub_domain", setbo.getXml_param()==null?"":setbo.getXml_param());
						if(xml_param!=null&&xml_param.trim().length()>0){
							try{
							    doc=PubFunc.generateDom(xml_param);
								String xpath="/sub_para/para";
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
								List childlist=findPath.selectNodes(doc);	
								if(childlist!=null&&childlist.size()>0)
								{
									element=(Element)childlist.get(0);
									if(element.getAttributeValue("id")!=null&&("1".equals(""+setbo.getChgstate())||setbo.isSubflag())){
										sub_domain_id=(String)element.getAttributeValue("id");
										if(sub_domain_id!=null&&sub_domain_id.trim().length()>0)
											cellbean.set("sub_domain_id", setbo.getSub_domain_id()==null?"":setbo.getSub_domain_id());	
									}
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						FieldItem fielditem = null;
						if(setbo.getField_name()!=null&&!"".equals(setbo.getField_name()))
							fielditem = DataDictionary.getFieldItem(setbo.getField_name());
						int inputType = 0;
						if(fielditem!=null)
							inputType = fielditem.getInputtype();//0普通编辑器 1 富文本编辑器
						cellbean.set("inputType", inputType);
			            String strc="";
						//if(!setbo.isSubflag()){
							String fldname  = setbo.getField_name();
							if (!"V".equalsIgnoreCase(setbo.getFlag())&&!"F".equalsIgnoreCase(setbo.getFlag())&&!"S".equalsIgnoreCase(setbo.getFlag())) {
								if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
									fldname=fldname+"_"+setbo.getSub_domain_id();
								}
			                    fldname =fldname+"_"+ String.valueOf(setbo.getChgstate());
							}
							if(StringUtils.isNotBlank(fldname))
								fldname =fldname.toLowerCase();
							/**单元格内容*/
							String state="2";
							if(setbo.getField_name()!=null&&setbo.getFlag()!=null&&("A".equalsIgnoreCase(setbo.getFlag())|| "B".equalsIgnoreCase(setbo.getFlag())|| "K".equalsIgnoreCase(setbo.getFlag())))
							{//如果是人员人员库、单位库、职位库
								String astate = "2";
								if(setbo.getField_name()!=null&&!"".equals(setbo.getField_name()))
									astate=this.userview.analyseFieldPriv(setbo.getField_name());
								if(setbo.getField_name()!=null&&!"".equals(setbo.getField_name())&&"codesetid,codeitemdesc,corcode,parentid,start_date".indexOf(setbo.getField_name())!=-1)
									astate="2";
								if(this.tablebo!=null&& "1".equals(this.tablebo.getUnrestrictedMenuPriv_Input()))
									astate="2";
								if("0".equals(astate))
									state="0";
							}
							if (!setbo.isSubflag()) {// 这里用来判断非子集的字段
								if(setbo.getField_name()!=null&&!"".equals(setbo.getField_name())){
									state = this.userview.analyseFieldPriv(setbo.getField_name());
				                    boolean specialItem=false;
				            		if (setbo.getField_name().length()>0){
				            			if (",start_date,codesetid,parentid,codeitemdesc,corcode,to_id,".indexOf(setbo.getField_name())>-1){
				            				specialItem=true;
				            			}
				            		}               
				                    if (specialItem) {
				                    	state = "2";
				                    }
				                    if("V".equalsIgnoreCase(setbo.getFlag())||"S".equalsIgnoreCase(setbo.getFlag())||"C".equalsIgnoreCase(setbo.getFlag())) {
				                    	state = "2";
				                    }
				                    //模板指标无读写权限时 判断当前节点有没有对指标授权
			                    	if(fieldmap!=null&&fieldmap.get((setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null) {
			                    		state=((String)fieldmap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate())).toLowerCase();
			                    	}
								}
			                } else {// 子集数据
			                	if("F".equalsIgnoreCase(setbo.getFlag()))
			                		state = "2";
			                	else
			                		state = this.userview.analyseTablePriv(setbo.getSetname());
			                } 
			                if ("1".equals(this.paramBo.getUnrestrictedMenuPriv_Input())&&setbo.getChgstate()==2)
			                	state="2";
			                if(setbo.isSubflag()&&fieldmap!=null&&fieldmap.get((setbo.getSetname().toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null){
	                			//if (!"0".equals(this.task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
	                			state = ((String)fieldmap.get(setbo.getSetname().toLowerCase()+"_"+setbo.getChgstate())).toLowerCase();
	                			//}
		                	}
						    if(setbo.getFlag()!=null&&"S".equals(setbo.getFlag())){
						    	boolean flag = false;
						    	RowSet rowSet1=null;
								if(rset.isBeforeFirst()){
									if(rset.next()){
										flag = true;	
									}
								}
								
								String xml = Sql_switcher.readMemo(rset,"signature");
								if(xml.length()<1)
									strc = "";
								else{
									org.jdom.Document doc2=null;
									doc2 =PubFunc.generateDom(xml);
									Element root = doc2.getRootElement();
							        List childlist = root.getChildren("record");
							        ArrayList singerList = new ArrayList();
							        if(childlist!=null&&childlist.size()>0)
									{
										for(int k=0;k<childlist.size();k++){
											Element element1=(Element)childlist.get(k);
											String DocuemntID = element1.getAttributeValue("DocuemntID");
											List childlist2 =element1.getChildren("item");
											if(childlist2!=null&&childlist2.size()>0){
												for(int n=0;n<childlist2.size();n++){
													HashMap singermap = new HashMap();
													Element element2=(Element)childlist2.get(n);
													String pageid = element2.getAttributeValue("PageID");
													String gridno = element2.getAttributeValue("GridNO");
													String nodeId = element2.getAttributeValue("node_id");
													String delflag = "false";
													if(this.signtype==3) {
														delflag = element2.getAttributeValue("delflag");
													}
													int pid=0;
													if(pageid!=null&&pageid.length()>0)
														pid = Integer.parseInt(pageid);
													int gridNo=0;
													if(gridno!=null&&gridno.length()>0){
														gridNo=Integer.parseInt(gridno);
													}
													int nodeID=0;
													if(nodeId!=null&&nodeId.length()>0){
														nodeID=Integer.parseInt(nodeId);
													}
													String SignatureID = element2.getAttributeValue("SignatureID");
													if(SignatureID.length()>0&&setbo.getPagebo().getPageid()==pid&&setbo.getGridno()==gridNo){//只有签章中gridno和pageid都相同时才显示。
														float width = 0;
														float height = 0;
														int width_= 0;
														int height_= 0;
														String pointx  = element2.getAttributeValue("pointx");
														String pointy  = element2.getAttributeValue("pointy");
														int x=0;
														int y=0;
														if(pointx.length()>0){
															if(pointx.endsWith("px"))
																x = (int) Math.round(Double.parseDouble(pointx.substring(0,pointx.length()-2)));
															else
																x = (int) Math.round(Double.parseDouble(pointx));
														}
														if(pointy.length()>0){
															if(pointy.endsWith("px"))
																y = (int) Math.round(Double.parseDouble(pointy.substring(0,pointy.length()-2)));
															else
																y = (int) Math.round(Double.parseDouble(pointy));
														}
														
														if(this.signtype==0||this.signtype==3){//JGKJ
															String imagetype = ".jpg";
															if(this.signtype==3)
																imagetype = ".gif";
															File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+imagetype);
															if(this.signtype==0) {
															    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
															    width_ = sourceImg.getWidth();
															    height_ = sourceImg.getHeight();
															}else {
																String imgheight  = element2.getAttributeValue("height");
																String imgwidth  = element2.getAttributeValue("width");
																width_ = (int) Math.round(Double.parseDouble(imgwidth));
																height_ = (int) Math.round(Double.parseDouble(imgheight));
															}
														    width = width_*1F;
														    height = height_*1F;
														    if (tempFile.exists()&&setbo.getPagebo().getPageid()==pid) {  
														    	strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+imagetype;
														    }  
														}else if (this.signtype==1){//BJCA
															String signature = "";
															//表单原文
															String plain = "hjsoft";
															//签章值
															rowSet1=dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+DocuemntID+"'");
															if(rowSet1.next()){
																signature = rowSet1.getString("signaturetext");
															}
															SealVerify sealVerify = new SealVerify();
															sealVerify.setCoding("GBK");
															
															if (sealVerify.doSealVerify(plain, signature)) {
																//得到签章图片（Base64编码）
																String PicData=sealVerify.getPicData(plain, signature);
																//将base64编码生成图片
																if (PicData == null){ // 图像数据为空
															       //return;
															    }else{

															        // Base64解码
															        byte[] bytes = Base64.decodeBase64(PicData);
															        for (int kk = 0; kk < bytes.length; ++kk) {
															            if (bytes[kk] < 0) {// 调整异常数据
															                bytes[kk] += 256;
															            }
															        }
															        File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
																    if (!file.exists()) {  
																    	// 生成jpeg图片
																        FileOutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
																        out.write(bytes);
																        out.flush();
																        out.close();
																    }
																	File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
																    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
																    width_ = sourceImg.getWidth();
																    height_ = sourceImg.getHeight();
																    width = width_*1F;
																    height = height_*1F;
																    if (tempFile.exists()&&setbo.getPagebo().getPageid()==pid) {  
																    	strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif";
																    } 
															    }
														    }
														}else if(this.signtype==2) {
															String SignatureHtmlID = element2.getAttributeValue("SignatureHtmlID");
															String documentid = this.tabid+"_"+DocuemntID;
															String picdata = "";
															String sql1 = "select caimg from htmlsignature where documentid='"+documentid+"' and signatureid='"+SignatureHtmlID+"'";
															rowSet1 = dao.search(sql1);
															InputStream in = null;
															if(rowSet1.next()) {
																in = rowSet1.getBinaryStream("caimg");
																if(in!=null) {
																	picdata = PubFunc.getBlobBase64(rowSet1,1);
																}
															}
															if (picdata == null){ // 图像数据为空
															       //return;
															}else{
														        // Base64解码
														        byte[] bytes = Base64.decodeBase64(picdata);
														        for (int kk = 0; kk < bytes.length; ++kk) {
														            if (bytes[kk] < 0) {// 调整异常数据
														                bytes[kk] += 256;
														            }
														        }
														        File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
															    if (!file.exists()) {  
															    	// 生成jpeg图片
															        FileOutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
															        out.write(bytes);
															        out.flush();
															        out.close();
															    }
																File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
															    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
															    width_ = sourceImg.getWidth();
															    height_ = sourceImg.getHeight();
															    width = width_*1F;
															    height = height_*1F;
															    if (tempFile.exists()&&setbo.getPagebo().getPageid()==pid) {  
															    	strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg";
															    }  
															}
														}
														if(this.signtype==3) {
															singermap.put("left",x);
															singermap.put("top", y);
														}else {
															singermap.put("left", (x-setbo.getRleft())>0?x-setbo.getRleft():x-setbo.getRleft());
															singermap.put("top", (this.signtype==0||this.signtype==2)?y-setbo.getRtop():y-setbo.getRtop()-height_/2);
														}
														singermap.put("width", width);
													    singermap.put("height", height);
													    singermap.put("value", strc);
													    if(this.signtype==3&&"true".equals(delflag)) {
													    }else
													    	singerList.add(singermap);
													}
												}
												}
											}
										}
							        cellbean.set("signatureValueList", singerList);
								}
								if(rowSet1!=null)
									rowSet1.close();
							    if(flag)
							    	rset.previous();
						    }else{
						    	if("H".equals(setbo.getFlag())){
						    		if(hz==null||"".equals(hz)){}
						    		else{	
						    			String[] splithz =hz.split("`");
						    			for(int is=0;is<splithz.length;is++){
						    				if(is!=splithz.length-1)
						    					strc += splithz[is]+"\n";
						    				else
						    					strc += splithz[is];
						    			}
						    		}
						    	}else if(!"S".equals(setbo.getFlag())&&!"F".equals(setbo.getFlag())&&!setbo.isSubflag()){
						    		strc=setbo.getCellContent(dbpre,obj_id,rset,this.userview,this.tablebo); 
						    	}
						    }
						    if("D".equals(setbo.getField_type())&&!setbo.isSubflag()) {
						    	if(rset.isBeforeFirst()){
				                    rset.next();                        
				                }
						    	Timestamp time = rset.getTimestamp(fldname);//bug34417 时间值为空转换时报空指针错误
						    	if(time!=null){
							    	if(setbo.getDisformat()==25)
							    		strc=dateFormat.format(time); 
									else
										strc=dateFormat2.format(time); 
						    	}else
						    		strc="";
						    	String formula = setbo.getFormula()==null?"":setbo.getFormula();
						    	String prefix_date = "";//前缀
						    	if(StringUtils.isNotBlank(formula))
						    		prefix_date = setbo.getPrefixCond(formula)[0];
						    	strc=prefix_date+this.utilBo.getFormatDate(strc,setbo.getDisformat());
						    }
						    if("N".equals(setbo.getField_type())&&("A".equals(setbo.getFlag())||"B".equals(setbo.getFlag())||"K".equals(setbo.getFlag()))&&strc.indexOf(".")!=-1&&!setbo.isSubflag()){
						    	//处理浮点型是整数的    以后应该会有个配置参数是否去掉整数后面的小数位
						    	String strc_ = strc.replace("\n", "").replace("\r", "").substring(strc.indexOf(".")+1);
						    	if(Integer.parseInt(strc_)==0)
						    		strc = String.valueOf(Math.round(Double.parseDouble(strc)));
						    }
						    if("C".equals(setbo.getFlag())&&!setbo.isSubflag()){
			                     String pattern = "###"; 
			                     TSyntax tsyntax = new TSyntax();    
			                     tsyntax.Lexical(setbo.getFormula());
			                     tsyntax.SetVariableValue(numberRecParam);
			                     tsyntax.DoWithProgram();
			                     int decimal = setbo.getDisformat();
			                     pattern = "###"; //浮点数的精度
			                     if (decimal > 0)
			                         pattern += ".";
			                     for (int ia = 0; ia < decimal; ia++)
			                         pattern += "0";
			                     double dValue =0;
			                     if (tsyntax.m_strResult != null && tsyntax.m_strResult.length() > 0)
			                       dValue =Double.parseDouble(tsyntax.m_strResult);
			                     strc = new DecimalFormat(pattern).format(dValue);
			                 }
						    strc=strc.replaceAll("%26lt;","<");
							strc=strc.replaceAll("%26gt;",">");
							if(!("M".equalsIgnoreCase(setbo.getField_type())&&!"A".equals(setbo.getFlag()))&&!"H".equals(setbo.getFlag())&&!setbo.isSubflag()){
								String itemtype = "";
								if(fielditem!=null)
									itemtype = fielditem.getItemtype();
								if(!"M".equalsIgnoreCase(itemtype)){
									if(strc.lastIndexOf("\r")!=-1){
										strc=strc.substring(0,strc.lastIndexOf("\r"));
									}
									if(strc.lastIndexOf("\n")!=-1){
										strc=strc.substring(0,strc.lastIndexOf("\n"));
									}
								}
							}
							if("M".equalsIgnoreCase(setbo.getField_type())&&inputType==1&&!setbo.isSubflag()){
								if(strc.indexOf("/UserFiles/")!=-1){
									strc = strc.replace("/UserFiles/", this.userview.getServerurl()+"/UserFiles/");
								}
								strc = strc.replace("<br />", "<br/>");
								if(strc.indexOf("\n")!=-1){
									strc = strc.replace("\n", "");
								}
								strc=strc.replace(" ", "&nbsp;");
								strc=strc.replace("&nbsp;&nbsp;", "&nbsp;");
							}
			            	boolean flag2 = false;
							if(rset.isBeforeFirst()){
								if(rset.next()){
								flag2 = true;	
								}
							}
							if(this.task_id!=null&&!"0".equals(this.task_id)){
								if(setbo.getFlag()!=null&&!setbo.isSubflag()&&("A".equals(setbo.getFlag())|| "B".equals(setbo.getFlag())|| "K".equals(setbo.getFlag()))&&fieldmap.size()>0&&fieldmap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate())!=null)
				            	{
				            		String editable=(String)fieldmap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate()); //	//0|1|2(无|读|写)
				            		if(editable!=null)
				            			state=editable;
				            	}
							}
							if(flag2)
			            		rset.previous();
			            	if("1".equals(this.userview.getHm().get("fillInfo")))
			            		state ="2";
			            	if("0".equals(this.paramBo.getNeedJudgPre()))
			            		state = "2";
			            	if("0".equals(state))
								strc="";
							//bug 34133 子集无权限，不输将子集改为文字，输出外面大框
			            	if("0".equals(state)&&setbo.isSubflag()){
			            		setbo.setFlag("H");
			            		setbo.setSubflag(false);
			            	}
						    if("P".equalsIgnoreCase(setbo.getFlag())&&!setbo.isSubflag()){//图片
								String name=setbo.getCellContent(dbpre,obj_id,rset,this.userview,this.tablebo); 
								if("".equals(name)){
									String projectName = "hrms";
									strc+=System.getProperty("user.dir").replace("bin", "webapps");  //把bin 文件夹变到 webapps文件里面 
									strc+=System.getProperty("file.separator")+projectName+System.getProperty("file.separator")+"images"+System.getProperty("file.separator")+"photo.jpg";
								}else
									strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+name;
							}
						//}
						//仅支持一个单元格放一个标题
					    if("H".equalsIgnoreCase(setbo.getFlag())&&"".equals(setbo.getHz().replace("`", "").trim())&&!setbo.isSubflag()){
							for(int ii=0;ii<leavetitlelist.size();ii++){
								LazyDynaBean ttitle = (LazyDynaBean)leavetitlelist.get(ii);
								int rtop_ = (Integer)ttitle.get("rtop");
								int rleft_ = (Integer)ttitle.get("rleft");
								int rwidth_ = (Integer)ttitle.get("rwidth");
								int rheight_ = (Integer)ttitle.get("rheight");
								int fonteffect = (Integer)ttitle.get("fonteffect");
								String fontname = (String)ttitle.get("fontname");
								int fontsize = (Integer)ttitle.get("fontsize");
								String isPhoto = (String)ttitle.get("isPhoto");
								String titlevalue_ = (String)ttitle.get("titlevalue");
								if(rleft_>=setbo.getRleft()&&rleft_+rwidth_<=setbo.getRleft()+setbo.getRwidth()&&rtop_>=setbo.getRtop()&&rtop_+rheight_<=setbo.getRtop()+setbo.getRheight()){
									strc = titlevalue_;
									cellbean.set("align", 7);
									cellbean.set("fonteffect", fonteffect);
									cellbean.set("fontname", fontname==null?"":fontname);
									cellbean.set("fontsize", fontsize);
									/*if("true".equalsIgnoreCase(isPhoto)){//如果是单元格中插入标题图片，把信息转换为图片标识
										cellbean.set("flag", "P");
										cellbean.set("isTrans", "true");
										cellbean.set("photoWidth",rwidth_);
										cellbean.set("photoHeight", rheight_);
									}*/
									break;
								}
							}
						}
						if("1".equals(this.paramBo.getAutosize())) {//设置了单元格字体自适应
							int fonteffect = setbo.getFonteffect();
							if(fonteffect==1)
								fonteffect = 0;
							else if(fonteffect==2)
								fonteffect = 1;
							else if(fonteffect==3)
								fonteffect = 2;
							//String strc1 = strc.replaceAll("\r","").replaceAll("\n", "").replaceAll("<br/>", "").replaceAll("<br />", "");//不去掉换行符,保留原始数据进行缩放
							ResetFontSizeUtil rfsu=new ResetFontSizeUtil();
							int fontsize=rfsu.ResetFontSize(setbo.getRwidth(), setbo.getRheight(), strc, setbo.getFontsize(), setbo.getFontname(), fonteffect);
							cellbean.set("fontsize", fontsize);
							//if(fontsize<setbo.getFontsize())
								//strc = strc.replaceAll("\r","").replaceAll("\n", ""); //字体缩小时不在去掉换行符
						}
						cellbean.set("cellvalue", strc==null?"":strc);
						boolean subflag = setbo.isSubflag();//是否是子集
						cellbean.set("subflag", subflag);
						ArrayList RecordList=new ArrayList();
						TSubSetDomain subdom = null;
						if(setbo.isSubflag())
						{
							xml_param =setbo.getXml_param();
							subdom=new TSubSetDomain(xml_param);
							reSetWidth(setbo.getRwidth(),subdom);
					        sub_domain_id =setbo.getSub_domain_id();
					        Integer changeState =setbo.getChgstate();
					        if(sub_domain_id!=null&&sub_domain_id.length()>0){
								sub_domain_id="_"+sub_domain_id;
							}
					        String setName =setbo.getSetname();
					        String field_name="t_"+setName+sub_domain_id+"_"+changeState;
			                if(setName!=null&&setName.indexOf("attachment")>-1){//附件模拟子集
			                	RecordList = this.getAttachRecordlist(ins_id,setbo,dbpre,obj_id,this.userview);
			                	if("1".equals(this.paramBo.getAutosize())) {
			                		int fontsize = this.rebuildSubFontSize(setbo,subdom,RecordList);
			                		cellbean.set("fontsize", fontsize);
			                	}
			                }else{
			                	rset.first();
			                	String content=Sql_switcher.readMemo(rset, field_name.toLowerCase());
			                	RecordList=subdom.getRecordPdfList(content);//子集的数据
			                	//通过子集的数据,重置子集的字体大小
			                	if("1".equals(this.paramBo.getAutosize())) {
			                		int fontsize = this.rebuildSubFontSize(setbo,subdom,RecordList);
			                		cellbean.set("fontsize", fontsize);
			                	}
			                	HashMap subfiledstate = new HashMap();
			                	for(int t=0;t<subdom.getFieldfmtlist().size();t++){
			                		TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(t); 
			                		String name=fieldformat.getName().toLowerCase();
									FieldItem item=DataDictionary.getFieldItem(name);
									if(item!=null){
										String a_state="2";
										//bug 34133 变化后指标才判断是否不受权限控制
										if ("1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())&&changeState==2){
											a_state="2";
						                }else{
						                	a_state=this.userview.analyseFieldPriv(item.getItemid());
											//bug 34133子集有读或者写权限，子集指标权限按子集权限走。
						                	String state_subflag = this.userview.analyseTablePriv(setbo.getSetname());
						                	a_state=state_subflag;
						                }
						                if(fieldmap!=null&&fieldmap.get((setbo.getSetname().toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null){
				                			//if (!"0".equals(this.task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
				                			a_state = ((String)fieldmap.get(setbo.getSetname().toLowerCase()+"_"+setbo.getChgstate())).toLowerCase();
				                			//}
					                	}
						                if("1".equals(this.userview.getHm().get("fillInfo")))
						                	a_state="2";
						                if("0".equals(this.paramBo.getNeedJudgPre()))
						                	a_state = "2";
						                subfiledstate.put(name, a_state);
									}
				                }
			                	cellbean.set("subfiledstate", subfiledstate);
			                }
						}
						cellbean.set("recordlist", RecordList);
						cellslist_new.add(cellbean);
					}
					pgmap_new.set("context", cellslist_new);
					pgmap_new.set("tabid",this.tabid);//导出子集序号需要根据tabid，传入
					pgmap_new.set("midtitle_img", leavetitlelist_img);
					if("2".equals(this.out_file_type)&&i==(pgidlist.size()-1)) {
						pgmap_new.set("breakPage", "true");
					}
					if("1".equals(this.downtype))
						outputList.add(pgmap_new);
					else if("0".equals(this.downtype))
						oneoutputList.add(pgmap_new);
				}
				if("0".equals(this.downtype)){//一人一个文档压缩下载
					String _prefix = prefix_;
					if(nameOutList.contains(prefix_)){
						int num = 0;
						if(filenameMap.containsKey(prefix_)){
							num = Integer.parseInt((String)filenameMap.get(prefix_));
							filenameMap.put(prefix_,num+1);
						}else{
							num = 1;
							filenameMap.put(prefix_,1);
						}
						_prefix = _prefix+num;
					}
					nameOutList.add(prefix_);
					filename = awu.analysisWord(_prefix,oneoutputList,this.pages);
					
					if("0".equals(this.outtype))
						filename = this.outPdf(filename);
					filenameList.add(filename);
				}
			}
			if("1".equals(this.downtype)){
				filename = awu.analysisWord(prefix,outputList,this.pages);
				if("0".equals(this.outtype))
					filename = this.outPdf(filename);
			}else if("0".equals(this.downtype)){
				 String tmpFileName = this.userview.getUserName() + "_" + this.tablebo.getName()+".zip";  
				 tmpFileName= tmpFileName.replace("/", "／");
		          byte[] buffer = new byte[1024];  
		          String strZipPath = System.getProperty("java.io.tmpdir")+File.separator+tmpFileName;
		          try {
		              ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));  
		              //59703 VFS+UTF-8：人事异动，导出PDF/导出word，一人一文档时，文件名称显示乱码，见附件。 
		              //设置压缩文件内的字符编码，不然会变成乱码  
		              out.setEncoding("GBK");
		              // 下载的文件集合
		              for (int i = 0; i < filenameList.size(); i++) {  
		                  FileInputStream fis = new FileInputStream(System.getProperty("java.io.tmpdir")+File.separator+filenameList.get(i));  
		                  out.putNextEntry(new ZipEntry((String)filenameList.get(i))); 
//		                  out.setEncoding("UTF-8");  
		                  int len;  
		                  // 读入需要下载的文件的内容，打包到zip文件  
		                  while ((len = fis.read(buffer)) > 0) {  
		                      out.write(buffer, 0, len);  
		                  }  
		                  out.closeEntry();  
		                  fis.close();  
		              }
		               out.close();  
		          } catch (Exception e) {
		              e.printStackTrace();
		          }
		          filename = tmpFileName;
		          //删除打包后剩余的文件
		          for(int i = 0; i < filenameList.size(); i++) { 
		        	  String filePath = System.getProperty("java.io.tmpdir")+File.separator+filenameList.get(i);
		        	  File docfile = new File(filePath);
		      		  if(docfile.exists())
		      			 docfile.delete();
		          }
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return filename;
	}
	/**
	 * 通过子集的数据,重置子集的字体大小
	 * @param setbo 
	 * @param subdom
	 * @param recordList
	 */
	private int rebuildSubFontSize(TemplateSetBo setbo, TSubSetDomain subdom, ArrayList recordList) {
		int colheadheight = subdom.getColheadheight();//设置行高
		int datarowcount = subdom.getDatarowcount();//设置行数
		boolean bcolhead=subdom.isBcolhead();//输出标题
		int height  = setbo.getRheight();//单元格的行高
		int width = setbo.getRwidth();//单元格的宽
		int verHeight = 0;
		int dataLineHeight=35;
		int totleHeight=0;
		int fontsize = setbo.getFontsize();
		String fontName = setbo.getFontname();
		int fonteffect = setbo.getFonteffect();
		if(fonteffect==1)
			fonteffect = 0;
		else if(fonteffect==2)
			fonteffect = 1;
		else if(fonteffect==3)
			fonteffect = 2;
		HashSet sizeSet = new HashSet();
		if(colheadheight>0){//设置行高
			verHeight = (int) (Math.floor(colheadheight/25.4*PixelInInch));
		}
		else {//不设置行高
			verHeight=41;
		}
		int rows=0;
		if(subdom.getDatarowcount()>0){//如果设置了指定行数 按这个走
			rows = subdom.getDatarowcount();
			totleHeight=rows*dataLineHeight;
			if(subdom.isBcolhead()){//是否输出标题
				totleHeight+=verHeight;
				rows = rows+1;
			}
		}
		else{
			if(recordList.size()>0){//没有指定行数，子集有记录
				rows = recordList.size();
				totleHeight=rows*dataLineHeight;
				if(subdom.isBcolhead()){
					totleHeight+=verHeight;
					rows = recordList.size()+1;	
				}
			}else{//没有指定行数，子集没有记录。
				rows=0;
				totleHeight=rows*dataLineHeight;
				if(subdom.isBcolhead()){//是否输出标题
					totleHeight+=verHeight;
					rows = rows+1;
				}
			}
		}
		//如果计算高度比画的高度大且没有设置自动延伸子集，重新计算数据行高，将数据行高减小。
		if(totleHeight>height&&"false".equals(subdom.getAutoextend())){
			dataLineHeight=(int) Math.ceil(height*1.0F/rows);
			if(subdom.isBcolhead()){//是否输出标题
				dataLineHeight=(int) Math.ceil((height*1.0F-verHeight*1.0F)/(rows-1));
			}
		}
		ResetFontSizeUtil rfsu=new ResetFontSizeUtil();
		for(int i=0;i<subdom.getFieldfmtlist().size();i++){
			TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);
			int filedwidth = fieldformat.getWidth();
			String name = fieldformat.getName();
			String title = fieldformat.getTitle();
			fontsize=rfsu.ResetFontSize(filedwidth, verHeight, title, setbo.getFontsize(),fontName,fonteffect);//重算标题行字体大小
			sizeSet.add(fontsize);
			for(int j=0;j<recordList.size();j++) {
				HashMap recordMap = (HashMap) recordList.get(j);
				String value = (String)recordMap.get(name.toLowerCase());
				fontsize=rfsu.ResetFontSize(filedwidth, dataLineHeight, value, setbo.getFontsize(),fontName,fonteffect);//重算内容行字体大小
				sizeSet.add(fontsize);
			}
		}
		if(sizeSet.size()>0) {
			ArrayList sizeList = new ArrayList(sizeSet);
			Collections.sort(sizeList); 
			fontsize = (Integer)sizeList.get(0);
		}
		return fontsize;
	}
	/**
	 * 获取所有的临时变量
	 * @return
	 */
	/*
	private HashMap getAllVariableHm()
	{
		StringBuffer strsql=new StringBuffer();
		HashMap hm=new HashMap();
		try
		{
			strsql.append("select * from midvariable where nflag=0 and templetId <> 0 and (templetId = "+this.tabid+" or cstate = '1')"); //包含共享临时变量 2014-02-22
			strsql.append(" order by sorting");			
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				RecordVo vo=new RecordVo("midvariable");
				vo.setString("cname",rset.getString("cname"));
				vo.setString("chz",rset.getString("chz"));
				vo.setInt("ntype",rset.getInt("ntype"));
				vo.setString("cvalue",rset.getString("cValue"));
				String codesetid=rset.getString("codesetid");
				if(codesetid==null||codesetid.equalsIgnoreCase(""))
					codesetid="0";
				vo.setString("codesetid",codesetid);
				vo.setInt("fldlen",rset.getInt("fldlen"));
				vo.setInt("flddec",rset.getInt("flddec"));
				hm.put(rset.getString("cname"),vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return hm;
	}
	*/
	
	/**
	 * 得到附件的表格列头xml
	 * @param sub_domain 
	 * @param setbo
	 * @param b 
	 */
	public String getAcctch_domain(String sub_domain, TemplateSetBo setbo) {
		String attach_domain = "";
		String attachmenttype = "";
		org.jdom.Document doc=null;
		Element element=null;
		String xpath="/sub_para/para";
		if(sub_domain!=null&&sub_domain.trim().length()>0){
			try{
				doc=PubFunc.generateDom(sub_domain);
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0){
					element=(Element)childlist.get(0);
					if(element.getAttributeValue("attachmentType")!=null){
						attachmenttype = element.getAttributeValue("attachmentType");
						setbo.setSetname("attachment_"+attachmenttype);
					}
					else
						setbo.setSetname("attachment_0");
				}
			    }catch(Exception e){
					
				}
			}else{
				setbo.setSetname("attachment_0");
			}
		attach_domain+="<?xml version='1.0' encoding='UTF-8'?>";
		attach_domain+="<sub_para>";
		if("1".equals(attachmenttype)){
			//if(isNull)
			//	attach_domain+="<para setname='attachment' hl='true' vl='false' colhead='true' colheadheight='8' datarowcount='0' fields='attachmentname`sortname`fullname`create_time`' autoextend='true'/>";
			//else
				attach_domain+="<para setname='attachment' hl='true' vl='true' colhead='true' colheadheight='0' datarowcount='0' fields='attachmentname`sortname`fullname`create_time`' autoextend='false'/>";
		}else{
			//if(isNull)
			//	attach_domain+="<para setname='attachment' hl='true' vl='false' colhead='true' colheadheight='8' datarowcount='0' fields='attachmentname`fullname`create_time`' autoextend='true'/>";
			//else
				attach_domain+="<para setname='attachment' hl='true' vl='true' colhead='true' colheadheight='0' datarowcount='0' fields='attachmentname`fullname`create_time`' autoextend='false'/>";
		}
		attach_domain+="<field name='attachmentname' need='false' width='50' title='名称' default='' align='0' slop='0' pre='' valign='1'/>";
		if("1".equals(attachmenttype))
			attach_domain+="<field name='sortname' need='false' width='15' title='文件类型' default='' align='0' slop='0' pre='' valign='1'/>";
		attach_domain+="<field name='fullname' need='false' width='15' title='创建人' default='' align='0' slop='0' pre='' valign='1'/>";
		attach_domain+="<field name='create_time' need='false' width='20' title='创建时间' default='' align='0' slop='0' pre='' valign='1'/>";
		attach_domain+="</sub_para>";
		return attach_domain;
	}
	/**
	 * 得到附件的数据
	 * @param ins_id
	 * @param setname
	 * @param dbpre
	 * @param obj_id
	 * @param uv
	 * @return
	 */
	private ArrayList getAttachRecordlist(int ins_id, TemplateSetBo setbo, String dbpre, String obj_id, UserView uv) {
		ArrayList recordList = new ArrayList();
		StringBuffer sb = new StringBuffer("");
		String username = uv.getUserName();
		RowSet frowset=null;
		RowSet rowSet = null;
		try{
			if("9".equalsIgnoreCase(module_id)&&uv.getStatus()==0&&StringUtils.isNotBlank(uv.getDbname())&&StringUtils.isNotBlank(uv.getA0100())){
				DbNameBo db = new DbNameBo(this.conn);
				String loginNameField = db.getLogonUserNameField();
				String usernameSele="";
				if(StringUtils.isNotBlank(loginNameField)) {
					loginNameField = loginNameField.toLowerCase();
					String sql="select "+loginNameField+" as username from "+uv.getDbname()+"A01 where a0100='"+uv.getA0100()+"' ";
					rowSet=dao.search(sql);
					while(rowSet.next()){
						usernameSele=rowSet.getString("username");
					}
					if(StringUtils.isNotBlank(usernameSele)){
						username=usernameSele;
					}
				}
			}
			if(ins_id!=0){//进入了审批流
				if("attachment_0".equals(setbo.getSetname())){//公共附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null) and (state=0 or state is null) ");
				}else if("attachment_1".equals(setbo.getSetname())&&obj_id.length()>0){//个人附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and t.attachmenttype=1");
					sb.append(" and t.objectid='");
					sb.append(obj_id);
					sb.append("' and (state=0 or state is null) ");
					if(StringUtils.isNotBlank(dbpre)){//infor_type=1
						sb.append(" and t.basepre='");
						sb.append(dbpre);
						sb.append("'");
					}
					String sub_domain=setbo.getAttachmentXml();
					if(StringUtils.isNotBlank(sub_domain)){
						org.jdom.Document attDoc=null;
						Element element=null;
						attDoc=PubFunc.generateDom(sub_domain);
						String xpath="/sub_para/para";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(attDoc);	
						if(childlist!=null&&childlist.size()>0)
						{
							element=(Element) childlist.get(0);
							String file_type=(String)element.getAttributeValue("file_type");
							if(StringUtils.isNotBlank(file_type)){
								sb.append(" and m.flag='"+file_type+"'");
							}
						}
					}
				}
			}else{//还未进入审批流
				if("attachment_0".equals(setbo.getSetname())){//公共附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)");
					sb.append(" and t.create_user='");
					sb.append(username);
					sb.append("'  and (state=0 or state is null) ");
				}else if("attachment_1".equals(setbo.getSetname())&&obj_id.length()>0){//个人附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and t.attachmenttype=1");
					sb.append(" and t.create_user='");
					sb.append(username);
					sb.append("' and t.objectid='");
					sb.append(obj_id);
					sb.append("' and (state=0 or state is null) ");
					
					if(StringUtils.isNotBlank(dbpre)){//infor_type=1
						sb.append(" and t.basepre='");
						sb.append(dbpre);
						sb.append("'");
					}
					String sub_domain=setbo.getAttachmentXml();
					if(StringUtils.isNotBlank(sub_domain)){
						org.jdom.Document attDoc=null;
						Element element=null;
						attDoc=PubFunc.generateDom(sub_domain);
						String xpath="/sub_para/para";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(attDoc);	
						if(childlist!=null&&childlist.size()>0)
						{
							element=(Element) childlist.get(0);
							String file_type=(String)element.getAttributeValue("file_type");
							if(StringUtils.isNotBlank(file_type)){
								sb.append(" and m.flag='"+file_type+"'");
							}
						}
					}
				}
			}
			if(sb.length()>0){
				sb.append(" order by file_id");
				frowset = dao.search(sb.toString());
				while (frowset.next()) {
					HashMap map = new HashMap();
					map.put("attachmentname", frowset.getString("name"));
					if("attachment_1".equals(setbo.getSetname()))
						map.put("sortname", frowset.getString("sortname"));
					Date d_create=frowset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
					map.put("create_time", d_str);
					String name = frowset.getString("fullname");
					String user_name = frowset.getString("create_user");//下载不要
					if(StringUtils.isBlank(name))
						name = user_name;
					map.put("fullname", name);
					recordList.add(map);
				} //while loop end
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(frowset);
			PubFunc.closeDbObj(rowSet);
		}
		return recordList;
	}
	/**
	 * 得到模板的高、宽
	 * 页边距，横纵向等信息
	 * @param tvo
	 * @return
	 */
	private float[] getWidthHeight(RecordVo tvo)
	{
		int direct=tvo.getInt("paperori");
		int paper = tvo.getInt("paper");
		float[] wh=new float[8];
		int width=0;
		int height=0;
		int tmargin = tvo.getInt("tmargin");
		int bmargin = tvo.getInt("bmargin");
		int rmargin = tvo.getInt("rmargin");
		int lmargin = tvo.getInt("lmargin");
		if(direct==1)
		{
			width=tvo.getInt("paperw");
			height=tvo.getInt("paperh");
		}
		else
		{
			width=tvo.getInt("paperh");
			height=tvo.getInt("paperw");
		}
		wh[0]=Math.round((float)(width /25.4*PixelInInch));
		wh[1]=Math.round((float)((height>500?297:height)/25.4*PixelInInch));//如果设置的页面高度大于577毫米的话,word转pdf会全部空白 ,不明所以
																			//没有内容 现判断高度大于500就按A4的297高度处理		
		wh[2]=Math.round((float)(tmargin /25.4*PixelInInch));//顶部间距
		wh[3]=Math.round((float)(bmargin/25.4*PixelInInch));//底部间距	
		wh[4]=Math.round((float)(rmargin /25.4*PixelInInch));//右侧间距
		wh[5]=Math.round((float)(lmargin/25.4*PixelInInch));//左侧间距
		wh[6]=direct*1f;//横纵向标识 1纵向 0横向
		wh[7]=paper*1f;
		return wh;
	}
	/**
     * 重新取得线型，由于画线的原因
     * @param list
     * @param flag
     * @param line
     * @param cur_setbo//当前操作对象
     * @return
     */
    private int getRlineForList(ArrayList list,String flag,int line,TemplateSetBo cur_setbo)
    {
        if(line==0)
            return line;
        else
        {
            float cur_rtop=cur_setbo.getRtop();//得到当前单元格的顶部
            float cur_rheight=cur_setbo.getRheight();//得到当前单元格的高度
            float cur_rleft=cur_setbo.getRleft();//得到当前单元格的左部
            float cur_rwidth=cur_setbo.getRwidth();////得到当前单元格的宽度
            TemplateSetBo setbo;  
            float rtop=0;
            float rheight=0;
            float rleft=0;
            float rwidth=0;
            int b=0;
            int t=0;
            int r=0;
            int l=0;
            int cur_gridno=cur_setbo.getGridno();
            int gridno=0;
            try
            {  
                for(int i=0;i<list.size();i++)
                {
                    setbo=(TemplateSetBo)list.get(i);  
                    rtop=setbo.getRtop();
                    rheight=setbo.getRheight();
                    rleft=setbo.getRleft();
                    rwidth=setbo.getRwidth();
                    gridno=setbo.getGridno();
                    if (setbo.getPagebo().getPageid()!=cur_setbo.getPagebo().getPageid()){
                        continue;
                    }
                    if(cur_gridno==gridno)
                        continue;
                    if("t".equals(flag))
                    {
                       b=setbo.getB();//得到每一个单元格的下部                    
                       if(b==0)
                       {
                         if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)))
                          {
                             line=0;
                             break;
                          }
                       }
                    }else if("b".equals(flag))
                    {
                        t=setbo.getT();
                        if(t==0)
                        {
                            if(rtop==(cur_rtop+cur_rheight)&&
                                ((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||
                                 (rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)
                                )
                              )
                            {
                                line=0;
                                 break;
                            }
                        }                       
                    }else if("l".equals(flag))
                    {
                        r=setbo.getR();
                        if(r==0)
                        {
                            if((rleft+rwidth)==cur_rleft&&((rtop<=cur_rtop&&(rtop+rheight)>=(cur_rtop+cur_rheight))||(rtop>=cur_rtop&&(rtop+rheight)<=(cur_rtop+cur_rheight))))
                            {
                                line=0;
                                break;
                            }
                        }                       
                    }else if("r".equals(flag))
                    {
                        l=setbo.getL();
                        if(l==0)
                        {
                            if(rleft==(cur_rleft+cur_rwidth)&&((rtop<=cur_rtop&&rtop+rheight>=cur_rtop+cur_rheight)||(rtop>=cur_rtop&&rtop+rheight<=cur_rtop+cur_rheight)))
                            {
                                line=0;
                                break;
                            }
                        }
                    }
                }
                
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }       
        return line; 
    }
	/**
	 * 获得节点定义的指标权限
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPriv(String task_id,Connection conn)
	{
		HashMap _map=new HashMap();
		org.jdom.Document doc=null;
		Element element=null;
		try
		{
			if(task_id!=null&&!"0".equals(task_id.trim()))
			{
				ContentDAO dao=new ContentDAO(conn);
				String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param);
						String xpath="/params/field_priv/field";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);	
						if(childlist.size()==0){
							xpath="/params/field_priv/field";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								String editable="";
								//0|1|2(无|读|写)
								if(element!=null&&element.getAttributeValue("editable")!=null)
									editable=element.getAttributeValue("editable");
								if(editable!=null&&editable.trim().length()>0)
								{
									String columnname=element.getAttributeValue("name").toLowerCase();
									_map.put(columnname, editable);
								}
								
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	/**   
     * @Title: isHaveReadFieldPriv   
     * @Description: 判断此模板页是否显示，
     * （1）有插入指标，且插入指标全部为无权限。   
     * @param @return 
     * @return boolean 
     * @throws   
    */
	public boolean isHaveReadFieldPriv(String pageId) {
        boolean bHavePriv = true;
        try {
        	ArrayList cellList =  utilBo.getPageCell(this.tabid,Integer.parseInt(pageId)); 
            HashMap privMap = getFieldPrivMap(cellList);
            for (int i = 0; i < cellList.size(); i++) {
                TemplateSet setBo = (TemplateSet) cellList.get(i);
                String flag = setBo.getFlag();
                if ("".equals(flag) || "H".equalsIgnoreCase(flag)) {
                    continue;
                }
                if (setBo.isABKItem()||"F".equalsIgnoreCase(flag)) {
                    bHavePriv = false;
                    if (privMap.get(setBo.getUniqueId()) != null) {
                        String rwPriv = (String) privMap.get(setBo.getUniqueId());
                        if ("1".equals(rwPriv) || "2".equals(rwPriv)) {
                            bHavePriv = true;
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bHavePriv;
    }
	/**
	 * 得到模板中指标读写权限
	 * @param allCellList
	 * @return
	 */
    private HashMap getFieldPrivMap(ArrayList allCellList) {
        TemplateDataBo dataBo = new TemplateDataBo(this.conn,this.userview,this.paramBo);
        HashMap filedPrivMap=dataBo.getFieldPrivMap(allCellList, this.task_id);
		return filedPrivMap;
    }
    /**
	 * 输出pdf
	 * @param filePath
	 * @throws Exception 
	 */
	public String outPdf(String filename) throws Exception {
		String filePath = System.getProperty("java.io.tmpdir")+File.separator+filename;
		Document doc = new Document(filePath);
		DocumentBuilder builder = new AsposeLicenseUtil(doc);
		int lastindex = filename.lastIndexOf(".");
		filename = filename.substring(0,lastindex)+".pdf";
		doc.save(System.getProperty("java.io.tmpdir")+File.separator+filename);
		//清除生成的word(tomcat临时文件中)
		File docfile = new File(filePath);
		if(docfile.exists())
			docfile.delete();
		return filename;
	}
	public void reSetWidth(int width, TSubSetDomain subdom)
	{
		int sumWidth=0;
		int width_ = 0;
		for(int i=0;i<subdom.getFieldfmtlist().size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);
			sumWidth=sumWidth+fieldformat.getWidth();
		}//for i loop end.
		sumWidth = sumWidth==0?1:sumWidth;
		float fScale=(float)width/sumWidth;
		for(int i=0;i<subdom.getFieldfmtlist().size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);
			int width_mid=Math.round(fScale*fieldformat.getWidth());
			width_+=width_mid;
			if(i==subdom.getFieldfmtlist().size()-1)
			{
				if(width-width_!=0)
				{
					width_mid=width_mid+width-width_;
				}
	
			}
			fieldformat.setWidth(width_mid);
		}
	}
	public boolean isSelfApply() {
		return selfApply;
	}
	public void setSelfApply(boolean selfApply) {
		this.selfApply = selfApply;
	}
	public String getNoshow_pageno() {
		return noshow_pageno;
	}
	public void setNoshow_pageno(String noshow_pageno) {
		this.noshow_pageno = noshow_pageno;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public int getSigntype() {
		return signtype;
	}
	public void setSigntype(int signtype) {
		this.signtype = signtype;
	}
	public TemplateParam getParamBo() {
		return paramBo;
	}
	public void setParamBo(TemplateParam paramBo) {
		this.paramBo = paramBo;
	}
	public String getDowntype() {
		return downtype;
	}
	public void setDowntype(String downtype) {
		this.downtype = downtype;
	}
	public String getOuttype() {
		return outtype;
	}
	public void setOuttype(String outtype) {
		this.outtype = outtype;
	}
	public String getShow_pageno() {
		return show_pageno;
	}
	public void setShow_pageno(String show_pageno) {
		this.show_pageno = show_pageno==null?"":show_pageno;
	}
	
	public String getModule_id() {
		return module_id;
	}
	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}
	/// 将pdf文档转换为图片的方法        
    /// </summary>  
    /// <param name="originFilePath">pdf文件路径，不包含文件名</param>  
	/// filename 文件名称
    /// <param name="imageOutputDirPath">图片输出路径，如果为空，默认值为pdf所在路径</param>         
    /// <param name="startPageNum">从PDF文档的第几页开始转换，如果为0，默认值为1</param>  
    /// <param name="endPageNum">从PDF文档的第几页开始停止转换，如果为0，默认值为pdf总页数</param>         
    /// <param name="resolution">设置图片的像素，数字越大越清晰，如果为0，默认值为128，建议最大值不要超过1024</param>  
    public static int pdf2Image(String originFilePath, String filename,String imageOutputDirPath, String imageNamePrefix, int startPageNum, int endPageNum, int resolution){ 
    	
    	imageOutputDirPath = imageOutputDirPath==null || imageOutputDirPath.length()<1?originFilePath:imageOutputDirPath;
    	int pageCount=0;
    	OutputStream fs = null;
        try{
        	com.aspose.pdf.Document doc = new com.aspose.pdf.Document(originFilePath+File.separator+filename);  
            if (doc == null){  
                throw new Exception("pdf文件无效或者pdf文件被加密！");  
            }  
            if (startPageNum <= 0){  
                startPageNum = 1;  
            }  
            if (endPageNum > doc.getPages().size() || endPageNum <= 0){  
                endPageNum = doc.getPages().size();  
            }  
            if (startPageNum > endPageNum){  
                int tempPageNum = startPageNum; startPageNum = endPageNum; endPageNum = startPageNum;  
            }  
            if (resolution <= 0){  
                resolution = 128;  
            }  
            for (int i = startPageNum; i <= endPageNum; i++){                      
                String imgPath = imageOutputDirPath+File.separator+imageNamePrefix+"_" + i + ".jpg";  
                fs = new FileOutputStream(imgPath);
                Resolution reso = new Resolution(resolution);  
                JpegDevice jpegDevice = new JpegDevice(reso, 100);  
                Page page = doc.getPages().get_Item(i);
                jpegDevice.process(page, fs);  
                fs.close(); 
            } 
            
            pageCount = endPageNum;
        }catch (Exception ex){  
        	ex.printStackTrace();
        }finally {
        	PubFunc.closeResource(fs);
        }  
        
        return pageCount;
    }  
    /**
	 * 如果为null返回“”字符串
	 * @param value
	 * @return
	 */
	private String nullToSpace(String value)
	{
		if(value==null)
			return "";
		else 
			return value;
	}
}
