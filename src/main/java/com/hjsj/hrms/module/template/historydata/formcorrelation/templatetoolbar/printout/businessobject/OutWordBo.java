package com.hjsj.hrms.module.template.historydata.formcorrelation.templatetoolbar.printout.businessobject;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.hjsj.hrms.businessobject.general.template.TFieldFormat;
import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TTitle;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateSet;
import com.hjsj.hrms.module.utils.asposeword.AnalysisWordUtil;
import com.hjsj.hrms.module.utils.asposeword.AsposeLicenseUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResetFontSizeUtil;
import com.hjsj.hrms.valueobject.ykcard.TRecParamView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.bjca.seal.SealVerify;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.imageio.ImageIO;
import javax.sql.RowSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 导出word公共方法类
* @Title: OutWordBo
* @Description:
* @author: hej
* @date 2019年11月19日 下午4:43:28
* @version
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
	private TemplateParam paramBo = null;
	private String task_id="";
	private int signtype=0;  //签章标识 0金格 1BJCA 2世纪
	private String downtype = "1"; //=0 一人一文档压缩下载 =1 多人一文档直接下载
	private String outtype = "";// 0 pdf 1 word
	private String show_pageno = "";//指定导出的页
	/**登录用户*/
	private UserView userview;
	private boolean selfApply=false;//是否自助
	private String module_id = "1"; //默认是人事异动
	TemplateUtilBo utilBo = null;
	TemplateDataBo dataBo = null;
	private String archive_year = "";//归档年份
	private String archive_id = "";//
	private String record_id = "";//归档号
	
	public OutWordBo(Connection conn,UserView userView,int tabid,String task_id, String archive_id)throws GeneralException {
		this.conn = conn;
		this.userview = userView;
		this.tabid=tabid;
		this.paramBo = new TemplateParam(conn, userView, tabid,archive_id);
		this.task_id = task_id;
		this.archive_id = archive_id;
		init(conn,userView);
	}
	private void init(Connection conn,UserView userView){
		this.conn = conn;
    	this.userview = userView;
    	dao = new ContentDAO(this.conn);                        
    	utilBo= new TemplateUtilBo(conn, userView);
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
	public String outword(int infor,ArrayList inslist)throws Exception {
		RecordVo tvo = utilBo.getArchiveTableVo(tabid,archive_id);
		float[] wh = getWidthHeight(tvo);
		String prefix=tvo.getString("name") + "_" + this.userview.getUserName();
		String filename="";
		/**根据第一个实例是否为0来分析，具体采用什么表*/
		AnalysisWordUtil awu = new AnalysisWordUtil();
		awu.setDowntype(this.downtype);
		awu.setOuttype(this.outtype);
		awu.setSigntype(this.signtype);
		try
		{
			prefix= prefix.replace("/", "／");
			String obj_id=null;
			String dbpre=null;
			/**所有变量*/
	        ArrayList pageList = utilBo.getTemplateArchivePage(tabid, archive_id);
			ArrayList pgidlist = new ArrayList();
			for(int m=0;m<pageList.size();m++){
				HashMap pagemap = (HashMap) pageList.get(m);
				ArrayList rtoplist = new ArrayList();//内容的行数据集
				ArrayList rleftlist = new ArrayList();//内容的列数据集
				ArrayList titleList = new ArrayList(); //每页标题数据集
				ArrayList cellslist=new ArrayList();//每页显示的内容信息
				String pageid =  String.valueOf(pagemap.get("pageid"));
				String ismobile = String.valueOf(pagemap.get("ismobile"));
				String isprn = String.valueOf(pagemap.get("isprn"));
				if("1".equals(ismobile)) {//手机的跳过
					continue;
				}
				if("0".equals(isprn)) {
					continue;
				}
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
				cellslist = utilBo.getArchiveCell(tabid, archive_id, Integer.parseInt(pageid));
				Boolean isPrintPage=false;
				for(int i=0;i<cellslist.size();i++) {
					TemplateSet setbo =(TemplateSet)cellslist.get(i);
					int nhide = setbo.getNhide();
					if(!isPrintPage)
					{
						isPrintPage=nhide==0?true:false;
					}
				}
				int b=0;
	            int l=0;
	            int r=0;
	            int t=0;
	            if(isPrintPage)
	            {
		            ArrayList new_setbo=new ArrayList();
		            for(int i=0;i<cellslist.size();i++){
		            	TemplateSet cur_setbo =(TemplateSet)cellslist.get(i);  
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
		            ArrayList titlelist = utilBo.getTemplateArchiveTitle(tabid,archive_id, Integer.parseInt(pageid));
					for(int i=0;i<titlelist.size();i++){
						HashMap map = (HashMap) titlelist.get(i);
						String isPhoto="false";//是不是图片
						TTitle title=new TTitle();
						title.setGridno(Integer.parseInt((String)map.get("gridno")));
						title.setPageid(Integer.parseInt((String)map.get("pageid")));
						title.setTabid(Integer.parseInt((String)map.get("tabid")));
						title.setFlag(Integer.parseInt((String)map.get("flag")));
						title.setFonteffect(Integer.parseInt((String)map.get("fonteffect")));
						title.setFontname((String)map.get("fontname"));
						title.setFontsize(Integer.parseInt((String)map.get("fontsize")));
						title.setHz((String)map.get("hz"));
						title.setRtop(Integer.parseInt((String)map.get("rtop")));
						title.setRleft(Integer.parseInt((String)map.get("rleft")));
						title.setRwidth(Integer.parseInt((String)map.get("rwidth")));
						title.setRheight(Integer.parseInt((String)map.get("rheight")));
						title.setExtendattr((String)map.get("extendattr"));
						String titlevalue = "";
						LazyDynaBean titleBean = new LazyDynaBean();
						titleBean.set("gridno", Integer.parseInt((String)map.get("gridno")));
						titleBean.set("pageid",Integer.parseInt((String)map.get("pageid")));
						titleBean.set("tabid",Integer.parseInt((String)map.get("tabid")));
						titleBean.set("flag",Integer.parseInt((String)map.get("flag")));
						titleBean.set("fonteffect",Integer.parseInt((String)map.get("fonteffect")));
						titleBean.set("fontname",(String)map.get("fontname"));
						titleBean.set("fontsize",Integer.parseInt((String)map.get("fontsize")));
						titleBean.set("hz",(String)map.get("hz"));
						titleBean.set("rtop",Integer.parseInt((String)map.get("rtop")));
						titleBean.set("rleft",Integer.parseInt((String)map.get("rleft")));
						titleBean.set("rwidth",Integer.parseInt((String)map.get("rwidth")));
						titleBean.set("rheight",Integer.parseInt((String)map.get("rheight")));
						titleBean.set("extendattr",(String)map.get("extendattr"));
						int flag = Integer.parseInt((String)map.get("flag"));
						if(flag!=7&&flag!=5)//图片
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
					for(int i=0;i<cellslist.size();i++) {
						TemplateSet setbo =(TemplateSet)cellslist.get(i);
						int rtop = setbo.getRtop();
						int rleft = setbo.getRleft();
						if(!rtoplist.contains(rtop)) {
							rtoplist.add(rtop);
						}
						if(!rleftlist.contains(rleft)) 
							rleftlist.add(rleft);
					}
					Collections.sort(rtoplist);
					Collections.sort(rleftlist);
					for(int i=0;i<rtoplist.size();i++) {
						rtoplist.set(i, rtoplist.get(i).toString());
					}
					for(int i=0;i<rleftlist.size();i++) {
						rleftlist.set(i, rleftlist.get(i).toString());
					}
					LazyDynaBean pageBean = new LazyDynaBean();
					pageBean.set("context", new_setbo);//内容
					pageBean.set("title", titleList);//标题
					pageBean.set("rtops", rtoplist);//行 数  内容
					pageBean.set("rlefts", rleftlist);//列 数  内容
					pageBean.set("wh", wh);//			
					pageBean.set("paperOrientation",StringUtils.isBlank((String)pagemap.get("paperorientation"))?"0":pagemap.get("paperorientation"));
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
			String prefix_ = "";
			HashMap archMap = dataBo.analysisJson2Map(record_id,archive_year);
			if (this.paramBo.getInfor_type() == 2
					|| this.paramBo.getInfor_type() == 3) {//单位名称
				if (this.paramBo.getOperationType() == 5) {
					prefix_ = tvo.getString("name") + "_" + archMap.get("codeitemdesc_2");
				} else {
					prefix_ = tvo.getString("name") + "_" + archMap.get("codeitemdesc_1");
				}
			}
			if (this.paramBo.getInfor_type() == 1) {
				if (this.paramBo.getOperationType() == 0) {//人员调入型
					prefix_ = tvo.getString("name") + "_" + archMap.get("a0101_2");
				} else {
					prefix_ = tvo.getString("name") + "_" + archMap.get("a0101_1");
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
				pgmap_new.set("rtops", pgmap.get("rtops"));
				pgmap_new.set("fieldmap", new HashMap());
				pgmap_new.set("rlefts", pgmap.get("rlefts"));
				pgmap_new.set("wh", pgmap.get("wh"));
				pgmap_new.set("paperOrientation", pgmap.get("paperOrientation"));
				org.jdom.Document doc=null;
				org.jdom.Element element = null;
				ArrayList numberRecParam=new ArrayList();  
				double fValue = 0.0f; 
               
				for(int aa = 0;aa<cellslist.size();aa++){
					TemplateSet setbo = (TemplateSet) cellslist.get(aa);
					String fldname =setbo.getTableFieldName();
                    String fldtype =setbo.getField_type();
                    String oldfldtype = setbo.getOld_fieldType();
                    
                    fldname =fldname.toLowerCase();
                    if ("N".equalsIgnoreCase(fldtype)||("M".equalsIgnoreCase(fldtype)&&"N".equalsIgnoreCase(oldfldtype))){
                       TRecParamView recP = new TRecParamView();
                       String numberValue = (String)archMap.get(fldname);
                       if("M".equalsIgnoreCase(fldtype)&&"N".equalsIgnoreCase(oldfldtype)) {
                    	  if(StringUtils.isNotBlank(numberValue)&&numberValue.indexOf("`")==-1) {
                    		 fValue = Double.parseDouble(numberValue);
                    	  }else
                    		  continue;
                       }else {
                    	  if(StringUtils.isNotBlank(numberValue)){
                    	      fValue = Double.parseDouble((String)archMap.get(fldname));
                    	  }
                       }
                       recP.setBflag(true);
                       recP.setFvalue(String.valueOf(fValue));
                       recP.setNid(setbo.getGridno());
                       numberRecParam.add(recP);
                    }
				}
				for(int m=0;m<cellslist.size();m++){
					LazyDynaBean cellbean = new LazyDynaBean();
					TemplateSet setbo = (TemplateSet) cellslist.get(m);
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
					cellbean.set("pageid", setbo.getPageId());
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
					if(xml_param!=null&&!"null".equals(xml_param)&&xml_param.trim().length()>0){
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
					String fldname  = setbo.getTableFieldName();
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
						if("1".equals(this.paramBo.getUnrestrictedMenuPriv_Input()))
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
		                    if (specialItem)
		                    	state = "2";
		                    if("V".equalsIgnoreCase(setbo.getFlag())||"S".equalsIgnoreCase(setbo.getFlag())||"C".equalsIgnoreCase(setbo.getFlag()))
		                    	state = "2";
						}
	                } else {// 子集数据
	                	if("F".equalsIgnoreCase(setbo.getFlag()))
	                		state = "2";
	                	else
	                		state = this.userview.analyseTablePriv(setbo.getSetname());
	                } 
	                if ("1".equals(this.paramBo.getUnrestrictedMenuPriv_Input())&&setbo.getChgstate()==2)
	                	state="2";

				    if(setbo.getFlag()!=null&&"S".equals(setbo.getFlag())){
				    	RowSet rowSet1=null;
						
						String xml = (String) archMap.get("signature")==null?"":(String) archMap.get("signature");
						if(xml.length()<1)
							strc = "";
						else{
							org.jdom.Document doc2=null;
							doc2 =PubFunc.generateDom(xml);
							org.jdom.Element root = doc2.getRootElement();
					        List childlist = root.getChildren("record");
					        ArrayList singerList = new ArrayList();
					        if(childlist!=null&&childlist.size()>0)
							{
								for(int k=0;k<childlist.size();k++){
									org.jdom.Element element1=(org.jdom.Element)childlist.get(k);
									String DocuemntID = element1.getAttributeValue("DocuemntID");
									List childlist2 =element1.getChildren("item");
									if(childlist2!=null&&childlist2.size()>0){
										for(int n=0;n<childlist2.size();n++){
											HashMap singermap = new HashMap();
											org.jdom.Element element2=(org.jdom.Element)childlist2.get(n);
											String pageid = element2.getAttributeValue("PageID");
											String gridno = element2.getAttributeValue("GridNO");
											String nodeId = element2.getAttributeValue("node_id");
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
											if(SignatureID.length()>0&&setbo.getPageId()==pid&&setbo.getGridno()==gridNo){//只有签章中gridno和pageid都相同时才显示。
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
												
												if(this.signtype==0){//JGKJ
													File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
												    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
												    width_ = sourceImg.getWidth();
												    height_ = sourceImg.getHeight();
												    width = width_*1F;
												    height = height_*1F;
												    if (tempFile.exists()&&setbo.getPageId()==pid) {  
												    	strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg";
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
														    if (tempFile.exists()&&setbo.getPageId()==pid) {  
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
													    if (tempFile.exists()&&setbo.getPageId()==pid) {  
													    	strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg";
													    }  
													}
												}
												singermap.put("left", (x-setbo.getRleft())>0?x-setbo.getRleft():x-setbo.getRleft());
												singermap.put("top", (this.signtype==0||this.signtype==2)?y-setbo.getRtop():y-setbo.getRtop()-height_/2);
											    singermap.put("width", width);
											    singermap.put("height", height);
											    singermap.put("value", strc);
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
				    		strc=setbo.getCellContent(dbpre,obj_id,archMap,this.userview); 
				    	}
				    }
				    if("D".equals(setbo.getField_type())&&!setbo.isSubflag()) {
				    	strc = (String) archMap.get(fldname);
				    	String formula = setbo.getFormula()==null?"":setbo.getFormula();//前缀
				    	strc=formula+strc.replace("-", ".");
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
						String name=setbo.getCellContent(dbpre,obj_id,archMap,this.userview); 
						if("".equals(name)){
							String projectName = "hrms";
							strc+=System.getProperty("user.dir").replace("bin", "webapps");  //把bin 文件夹变到 webapps文件里面 
							strc+=System.getProperty("file.separator")+projectName+System.getProperty("file.separator")+"images"+System.getProperty("file.separator")+"photo.jpg";
						}else
							strc=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+name;
					}
					//}
					//仅支持一个单元格放一个标题
					if("H".equalsIgnoreCase(setbo.getFlag())&&"".equals(setbo.getHz())&&!setbo.isSubflag()){
						for(int ii=0;ii<leavetitlelist.size();ii++){
							LazyDynaBean ttitle = (LazyDynaBean)leavetitlelist.get(ii);
							int rtop_ = (Integer)ttitle.get("rtop");
							int rleft_ = (Integer)ttitle.get("rleft");
							int rwidth_ = (Integer)ttitle.get("rwidth");
							int rheight_ = (Integer)ttitle.get("rheight");
							int fonteffect = (Integer)ttitle.get("fonteffect");
							String fontname = (String)ttitle.get("fontname");
							int fontsize = (Integer)ttitle.get("fontsize");
							String titlevalue_ = (String)ttitle.get("titlevalue");
							if(rleft_>=setbo.getRleft()&&rleft_+rwidth_<=setbo.getRleft()+setbo.getRwidth()&&rtop_>=setbo.getRtop()&&rtop_+rheight_<=setbo.getRtop()+setbo.getRheight()){
								strc = titlevalue_;
								cellbean.set("align", 7);
								cellbean.set("fonteffect", fonteffect);
								cellbean.set("fontname", fontname==null?"":fontname);
								cellbean.set("fontsize", fontsize);
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
						ResetFontSizeUtil rfsu=new ResetFontSizeUtil();
						int fontsize=rfsu.ResetFontSize(setbo.getRwidth(), setbo.getRheight(), strc, setbo.getFontsize(), setbo.getFontname(), fonteffect);
						cellbean.set("fontsize", fontsize);
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
		                	RecordList = this.getAttachRecordlist(archMap,setbo);
		                	if("1".equals(this.paramBo.getAutosize())) {
		                		int fontsize = this.rebuildSubFontSize(setbo,subdom,RecordList);
		                		cellbean.set("fontsize", fontsize);
		                	}
		                }else{
		                	String content=(String) archMap.get(field_name.toLowerCase());
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
									if ("1".equals(this.paramBo.getUnrestrictedMenuPriv_Input())&&changeState==2){
										a_state="2";
					                }else{
					                	a_state=this.userview.analyseFieldPriv(item.getItemid());
										//bug 34133子集有读或者写权限，子集指标权限按子集权限走。
					                	String state_subflag = this.userview.analyseTablePriv(setbo.getSetname());
					                	a_state=state_subflag;
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
			
			if("1".equals(this.downtype)){
				filename = awu.analysisWord(prefix,outputList,this.pages);
				if("0".equals(this.outtype))
					filename = this.outPdf(filename);
			}else if("0".equals(this.downtype)){
				 String tmpFileName = tvo.getString("name")+"_" + this.userview.getUserName()+".zip";  
				 tmpFileName= tmpFileName.replace("/", "／");
		          byte[] buffer = new byte[1024];  
		          String strZipPath = System.getProperty("java.io.tmpdir")+File.separator+tmpFileName;
		          try {
		              ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));  
		              // 下载的文件集合
		              for (int i = 0; i < filenameList.size(); i++) {  
		                  FileInputStream fis = new FileInputStream(System.getProperty("java.io.tmpdir")+File.separator+filenameList.get(i));  
		                  out.putNextEntry(new ZipEntry((String)filenameList.get(i))); 
		                   //设置压缩文件内的字符编码，不然会变成乱码  
		                  out.setEncoding("UTF-8");  
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
	private int rebuildSubFontSize(TemplateSet setbo, TSubSetDomain subdom, ArrayList recordList) {
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
	 * 得到附件的表格列头xml
	 * @param sub_domain 
	 * @param setbo
	 * @param b 
	 */
	public String getAcctch_domain(String sub_domain, TemplateSet setbo) {
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
		attach_domain+="<?xml version='1.0' encoding='GB2312'?>";
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
	 * @param archMap
	 * @param setbo 
	 * @return
	 */
	private ArrayList getAttachRecordlist(HashMap archMap, TemplateSet setbo) {
		ArrayList recordList = new ArrayList();
		try {
			String attachmenttype = setbo.getAttachmentType();
			StringBuffer sb = new StringBuffer("");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if (archMap.containsKey("t_wf_file_"+attachmenttype)) {
				ArrayList archList = (ArrayList) archMap.get("t_wf_file_"+attachmenttype);
				for (int i=0;i<archList.size();i++) {
					HashMap wfMap = (HashMap) archList.get(i);
					HashMap map = new HashMap();
					map.put("attachmentname", wfMap.get("name"));
					if("attachment_1".equals(setbo.getSetname()))
						map.put("sortname", wfMap.get("sortname"));
					String d_create = (String)wfMap.get("create_time");
					Date date = format.parse(d_create);
					String create_time=DateUtils.format(date,"yyyy.MM.dd");
					map.put("create_time", create_time);
					String name = (String) wfMap.get("fullname");
					String user_name = (String) wfMap.get("create_user");//下载不要
					if(StringUtils.isBlank(name))
						name = user_name;
					map.put("fullname", name);
					recordList.add(map);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
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
    private int getRlineForList(ArrayList list,String flag,int line,TemplateSet cur_setbo)
    {
        if(line==0)
            return line;
        else
        {
            float cur_rtop=cur_setbo.getRtop();//得到当前单元格的顶部
            float cur_rheight=cur_setbo.getRheight();//得到当前单元格的高度
            float cur_rleft=cur_setbo.getRleft();//得到当前单元格的左部
            float cur_rwidth=cur_setbo.getRwidth();////得到当前单元格的宽度
            TemplateSet setbo;  
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
                    setbo=(TemplateSet)list.get(i);  
                    rtop=setbo.getRtop();
                    rheight=setbo.getRheight();
                    rleft=setbo.getRleft();
                    rwidth=setbo.getRwidth();
                    gridno=setbo.getGridno();
                    if (setbo.getPageId()!=cur_setbo.getPageId()){
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
		org.jdom.Element element=null;
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
								element=(org.jdom.Element)childlist.get(i);
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
        	ArrayList cellList =  utilBo.getArchiveCell(tabid, archive_id, Integer.parseInt(pageId)); 
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

	public String getArchive_year() {
		return archive_year;
	}
	public void setArchive_year(String archive_year) {
		this.archive_year = archive_year;
	}
	public String getArchive_id() {
		return archive_id;
	}
	public void setArchive_id(String archive_id) {
		this.archive_id = archive_id;
	}
	
	public String getRecord_id() {
		return record_id;
	}
	public void setRecord_id(String record_id) {
		this.record_id = record_id;
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