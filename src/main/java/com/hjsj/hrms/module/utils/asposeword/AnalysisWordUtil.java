package com.hjsj.hrms.module.utils.asposeword;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import com.hjsj.hrms.businessobject.general.template.TFieldFormat;
import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.utils.ResetFontSizeUtil;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;


/**
 * <p>Title: AnalysisWordUtil </p>
 * <p>Description:分析数据为导出word准备工具类 </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2017-5-15 下午5:43:58</p>
 * @author hej
 * @version 1.0
 */
public class AnalysisWordUtil {
	private HashMap subInfo=new HashMap();//存放子集的数据
	private HashMap subLines=new HashMap();//存放子集行数  确定是不是需要输出空白行和是不是已经输出空白行
	private String downtype = "1";
	private String outtype = "";
	private String out_file_type="1";//导出格式 1 分页导出 2 连续页导出
	private boolean isUsePageMarginSet = false;//是否启用模板设置的页边距
	private String tabid="-1";
	private int signtype = 0;
	private String dirPath="";//设置文件夹路径
	// 导出选择officer/wps
	private String officerOrWps = "0";

	public String getOfficerOrWps() {
		return officerOrWps;
	}

	public void setOfficerOrWps(String officerOrWps) {
		this.officerOrWps = officerOrWps;
	}
	public String getOut_file_type() {
		return out_file_type;
	}
	public void setOut_file_type(String out_file_type) {
		this.out_file_type = out_file_type;
	}
	public String getDirPath() {
		return dirPath;
	}
	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}
	/**每英寸像素数
	 * default:windows 96
	 * mac             72
	 * */
	private int PixelInInch=96;
	public AnalysisWordUtil() {
		
	}
	/**
	 * 分析传入的数据
	 * @param filename
	 * @param outwordlist 用于分析的数据
	 * {
	 *  bomtitle=[], //下标题数据(同上标题属性)
		wh=[794.0, 1123.0, 72.0, 72.0, 64.0, 64.0, 1.0, 1.0], 
		fieldmap={}, //节点定义的指标权限
		rlefts=[78, 122, 154, 159, 181, 193, 261, 285, 298, 309, 371, 378, 388, 395, 415, 470, 534, 547, 568, 628, 646], //内容部分所有的rleft
		context=[
			{
			rheight=54, //指标单元格的高度
			field_type=, //指标类型
			b=1, //是否有下边线
			nhide=0, //指标是否不打印
			rleft=78, //指标的rleft
			align=7, //指标的对齐方式
			recordlist=[], //子集的数据集合
			l=1, //是否有左边线
			fontname=宋体, //字体
			fonteffect=1, //字体样式
			subflag=false, //是否是子集
			setname=, //指标属于哪个指标集  例如A04
			gridno=1, //指标在某一页的排序号
			flag=H, //指标的类型标识
			t=1, //是否有下边线
			rwidth=103, //指标单元格的宽度
			r=1, //是否有右边线
			inputType=0, //大文本编辑方式
			sub_domain_id=, //多个相同子集会用到			
			rtop=157, //指标的rtop
			hz=姓名`, //指标单元格的名称
			cellvalue=姓名, //单元格的值
			fontsize=12, //字体大小
			sub_domain=, //子集的xml格式数据
			pageid=0,//指标在第几页
			signatureValueList=[{left=500,top=200,value='图片的地址'},{}],//签章的集合 里面是n个   设置了签章才有
			subfiledstate={a0415=2, a0440=2, a0430=2, a0435=2},//子集中指标的权限  子集才有
		}
		], 
		 paperOrientation=0, //某一页是否单独设置横纵向
		 rtops=[157, 211, 261, 314, 364, 416, 466, 520, 570, 624, 675, 727, 778, 831, 881], //内容部分所有的rtop
		 rtops_t=[61, 109]//上标题的rtop集合
		 toptitle=[//上标题属性集合
		 { 
		   rheight=42,//标题的高度
		   rleft=302, //标题的rleft
		   fonteffect=1, //标题的字体样式
		   fontname=微软雅黑, //字体
		   titlevalue=XXXX大学, //标题的内容
		   gridno=1, //标题指标的编号
		   flag=0,//标题的类型
		   rwidth=134, //标题的宽度
		   extendattr=<format>0</format><prefix></prefix>, //标题样式的额外设置
		   rtop=61, //标题的rtop
		   hz=XXXX大学, //标题的名称
		   fontsize=22,//字体大小
		   pageid=0,//标题在哪页
		   tabid=221//模板id
		   }
		   ]
		 rtops_b=[]//下标题的rtop集合
		 }
	 * @param pages
	 * @return
	 * @throws Exception
	 */
	public String analysisWord(String filename, ArrayList outwordlist, int pages) throws Exception {
		ArrayList cellslist = new ArrayList();
    	ArrayList rtops = new ArrayList();
    	ArrayList rtops_t = new ArrayList();
    	ArrayList rtops_b = new ArrayList();
    	ArrayList rlefts = new ArrayList();
    	ArrayList toptitleList = new ArrayList();
		ArrayList bomtitleList = new ArrayList();
		ArrayList midtitleList_img = new ArrayList();
		ArrayList outputList = new ArrayList();
		float[] wh =new float[8];
		if("0".equals(this.downtype)){
			this.subInfo.clear();
			this.subLines.clear();
		}
		for(int i=0;i<outwordlist.size();i++){//模板中的表页
			if(i>0&&i%pages==0){
				this.subInfo.clear();
				this.subLines.clear();
			}
			LazyDynaBean pgldb = (LazyDynaBean) outwordlist.get(i);
			this.tabid=String.valueOf(pgldb.get("tabid"));
			toptitleList = (ArrayList) pgldb.get("toptitle");//上标题
			bomtitleList = (ArrayList) pgldb.get("bomtitle");//下标题
			midtitleList_img = (ArrayList) pgldb.get("midtitle_img");//标题(图片)
			cellslist=(ArrayList) pgldb.get("context");//单元格内容
			rtops = (ArrayList) pgldb.get("rtops");//每页内容对应的rtop集合
			rtops_t = (ArrayList) pgldb.get("rtops_t");//每页上标题对应的rtop集合
			rtops_b = (ArrayList) pgldb.get("rtops_b");//每页下标题对应的rtop集合
			rlefts = (ArrayList) pgldb.get("rlefts");//每页内容对应的rleft集合
			HashMap fieldmap =  pgldb.getMap().containsKey("fieldmap")?(HashMap) pgldb.get("fieldmap"):new HashMap();//节点定义的指标权限 (根据情况可不传)
			wh = (float[]) pgldb.get("wh");
			String paperorientation = pgldb.getMap().containsKey("paperOrientation")?(String)pgldb.get("paperOrientation"):"0";//每页的横纵向设置 (根据情况可不传)
			HashMap TitletopMap = this.getTitleMap(toptitleList,rlefts,rtops_t);
			HashMap TitlebomMap = this.getTitleMap(bomtitleList,rlefts,rtops_b);
			ArrayList TitlemidList = this.getTitleMap_mid(midtitleList_img);
			HashMap pagemap = this.analysisContext(cellslist,rtops,rlefts,fieldmap);
			HashMap outpagemap = new HashMap();
			outpagemap.put("toptitle", TitletopMap);
			outpagemap.put("context", pagemap);
			outpagemap.put("midtitle_img", TitlemidList);
			outpagemap.put("bomtitle", TitlebomMap);
			outpagemap.put("rleft", Integer.parseInt((String)rlefts.get(0)));
			outpagemap.put("rtop_t", Integer.parseInt((String)rtops.get(0)));
			outpagemap.put("rtop_b", Integer.parseInt((String)rtops.get(rtops.size()-1)));
			if(pgldb.get("breakPage")!=null) {
				outpagemap.put("breakPage", (String)pgldb.get("breakPage"));
			}
			int pagefirtop = 0;
			int pagefirlef = 0;
			int pagefirrig = 0;
			if(!this.isUsePageMarginSet) {
				if(rtops_t.size()>0){//如果有上标题
					if((Integer)rtops_t.get(0)<=Integer.parseInt((String)rtops.get(0)))
						pagefirtop = (Integer)rtops_t.get(0);
					//else
						//pagefirtop = Integer.parseInt((String)rtops.get(0));
				}else{
					pagefirtop = Integer.parseInt((String)rtops.get(0))/*<(int)wh[2]?Integer.parseInt((String)rtops.get(0)):(int)wh[2]*/;
				}
				pagefirlef = Integer.parseInt((String)rlefts.get(0));
				if(pagemap.containsKey(0)) {//如果有第0行,就取第0行的,找到一行的宽度.(有内容必定有第0行)
					ArrayList celllist = (ArrayList) pagemap.get(0);
					int width = 0;
					for(int j=0;j<celllist.size();j++) {
						HashMap cellmap = (HashMap)celllist.get(j);
						if(cellmap!=null)
							width += (Float)cellmap.get("width");
					}
					//bug 38535 文档整体竖向，个别页设置横向，导致横向获取的坐标不正确需要转换页面长宽值
					Boolean isChangeHW=false;
					if("0".equals(paperorientation)){//默认 1 纵 0横
						if(wh[6]==1)
							isChangeHW=false;//纵向
						else
							isChangeHW=false;//横向
					}else if("1".equals(paperorientation)){//纵向
						isChangeHW=false;
						if(wh[6]==0){//横向
							isChangeHW=false;
						}else{
							isChangeHW=true;
						}
					}else if("2".equals(paperorientation)){//横向
						isChangeHW=true;
						if(wh[6]==1){//纵向
							isChangeHW=true;
						}else{
							isChangeHW=false;
						}
					}
					if(isChangeHW)
						pagefirrig=(int)wh[1]-width-pagefirlef;
					else
						pagefirrig=(int)wh[0]-width-pagefirlef;
				}
			}else {
				pagefirtop = (int)wh[2];
				pagefirlef = (int)wh[5];
				pagefirrig = (int)wh[4];
			}
			Object[] wh1 =new Object[8];
			if("0".equals(paperorientation)){//默认 1 纵 0横
				if(wh[6]==1)
					wh1[0]=1;//纵向
				else
					wh1[0]=2;//横向
				wh1[1]=(int)wh[1];
				wh1[2]=(int)wh[0];
			}else if("1".equals(paperorientation)){//纵向
				wh1[0]=1;//纵向
				if(wh[6]==0){//横向
					wh1[1]=(int)wh[0];
					wh1[2]=(int)wh[1];
				}else{
					wh1[1]=(int)wh[1];
					wh1[2]=(int)wh[0];
				}
			}else if("2".equals(paperorientation)){//横向
				wh1[0]=2;//横向
				if(wh[6]==1){//纵向
					wh1[1]=(int)wh[0];
					wh1[2]=(int)wh[1];
				}else{
					wh1[1]=(int)wh[1];
					wh1[2]=(int)wh[0];
				}
			}
			int paper = (int)wh[7];
			wh1[3] = paper;
			wh1[4] = pagefirtop;//(int)wh[2];//上边距（页边距）
			wh1[5] = 18;//(int)wh[3];//下边距（页边距）
			wh1[6] = pagefirrig;//右边距（页边距）
			wh1[7] = pagefirlef;//左边距（页边距）
			outpagemap.put("wh", wh1);
			outputList.add(outpagemap);
		}
		AsposeOutWordUtil aow = new AsposeOutWordUtil();
		aow.setOut_file_type(this.out_file_type);
		aow.setTab_id(this.tabid);
		aow.setOuttype(this.outtype);
		aow.setSigntype(this.signtype);
		aow.setDirPath(this.dirPath);
		aow.setOfficerOrWps(this.getOfficerOrWps());
		filename = aow.outPutcontext(filename,outputList);
		return filename;
	}
	private ArrayList getTitleMap_mid(ArrayList midtitleList_img) {
		ArrayList titlearray = new ArrayList();//存储每行对应的单元格对象
		for(int i=0;i<midtitleList_img.size();i++){
			LazyDynaBean title=(LazyDynaBean) midtitleList_img.get(i);
			int rtop = (Integer)title.get("rtop");
			int rleft = (Integer)title.get("rleft");
			int rwidth = (Integer)title.get("rwidth");
			int rheight = (Integer)title.get("rheight");
			String titlevalue = (String)title.get("titlevalue");
			HashMap map = new HashMap();
			map.put("width", rwidth);
			map.put("height", rheight);
			map.put("value", titlevalue);
			map.put("rleft", rleft);
			map.put("rtop", rtop);
			titlearray.add(map);
		}
		return titlearray;
	}
	/**
	 * 分析内容部分
	 * @param cellslist
	 * @param rtops
	 * @param rlefts
	 * @param fieldmap
	 * @return
	 * @throws Exception
	 */
	private HashMap analysisContext(ArrayList cellslist, ArrayList rtops, ArrayList rlefts, HashMap fieldmap) throws Exception {
		int row = 0;//行
		HashMap pagemap = new HashMap();//存储每行对应的单元格对象
		HashMap subMap=new HashMap();//记录输出的子集，兼容子集高度画的很小，导出word报错。
		int pageRows=0;
		LazyDynaBean ldb = new LazyDynaBean();
		for(int m=0;m<cellslist.size();m++){
			ldb = (LazyDynaBean) cellslist.get(m);
			int rtop = (Integer)ldb.get("rtop");//单元格的rtop(没有的话模拟)
			int rleft = (Integer)ldb.get("rleft");//单元格的rleft(没有的话模拟)
			int rheight = (Integer)ldb.get("rheight");//单元格的高度
			int rwidth = (Integer)ldb.get("rwidth");//单元格的宽度
			String flag = (String)ldb.get("flag");//单元格类型 h是文本 a指标 f附件 v临时变量
			int L = (Integer)ldb.get("l");//单元格左边线
			int B = (Integer)ldb.get("b");//单元格下边线
			int R = (Integer)ldb.get("r");//单元格右边线
			int T = (Integer)ldb.get("t");//单元格上边线
			int L_size=1;
			if(ldb.get("lsize")!=null) {
				L_size=(Integer)ldb.get("lsize");//单元格左边线宽度
			}
			int B_size=1;
			if(ldb.get("bsize")!=null) {
				B_size=(Integer)ldb.get("bsize");//单元格左边线宽度
			}
			int R_size=1;
			if(ldb.get("rsize")!=null) {
				R_size=(Integer)ldb.get("rsize");//单元格左边线宽度
			}
			int T_size=1;
			if(ldb.get("tsize")!=null) {
				T_size=(Integer)ldb.get("tsize");//单元格左边线宽度
			}
			int Align = (Integer)ldb.get("align");//文字的排列方式
			int fonteffect = (Integer)ldb.get("fonteffect");//设置字体效果
			String fontname = (String)ldb.get("fontname");//设置字体名称
			int fontsize = (Integer)ldb.get("fontsize");//设置字体大小
			String strc = (String)ldb.get("cellvalue");//单元格的内容
			boolean subflag = (Boolean)ldb.get("subflag");//是否是子集
			String field_type = (String)ldb.get("field_type");//指标类型
			int nhide = (Integer)ldb.get("nhide");//指标是否打印
			int inputType = (Integer)ldb.get("inputType");//指标的文本编辑类型(大文本)
			ArrayList recordlist = ldb.getMap().containsKey("recordlist")?(ArrayList)ldb.get("recordlist"):new ArrayList();//如果是子集的话  子集的数据 (无子集可不传)
			int pageid = (Integer)ldb.get("pageid");//指标所在页
			int gridno = ldb.getMap().containsKey("gridno")?(Integer)ldb.get("gridno"):0;//指标编号 (无子集可不传)
			String sub_domain = ldb.getMap().containsKey("sub_domain")?(String)ldb.get("sub_domain"):"";//如果是子集的话  子集的参数设置(xml)数据 (无子集可不传)
			String setname = String.valueOf(ldb.get("setname"));
			String isTrans = String.valueOf(ldb.get("isTrans"));//是否是标题中插入图片转换的
			Float photoWidth =0F;//记录图片的宽度
			Integer photoHeight = 0;//记录图片的高度
			String special_M = String.valueOf(ldb.get("special_M"));
			if("true".equalsIgnoreCase(isTrans)&&"P".equalsIgnoreCase(flag)){
				photoHeight=(Integer) ldb.get("photoHeight");
				photoWidth=((Integer) ldb.get("photoWidth")*1.0f);
			}
			HashMap subfiledstate = ldb.getMap().containsKey("subfiledstate")?(HashMap)ldb.get("subfiledstate"):new HashMap();//如果是子集的话 子集的指标权限 (无子集可不传)
			ArrayList signatureValueList = ldb.getMap().containsKey("signatureValueList")?(ArrayList)ldb.get("signatureValueList"):new ArrayList();//签章专用 没有可不传
			//求出当前rtop在top集合中的位置
			int topindex = rtops.indexOf(rtop+"");
			//求出当前rleft在left集合中的位置
			int leftindex = rlefts.indexOf(rleft+"");
			//得到每两个top之间的高度（不包括最后一个）
			int vertop_ = 0;
			if(topindex==rtops.size()-1)//最后一个
				vertop_ = rheight;
			else{
				int nexttop = Integer.parseInt(String.valueOf(rtops.get(topindex+1)));
				vertop_ = nexttop-rtop;
			}
			if(row!=topindex){
				pageRows++;
				row = topindex;//第几行
			}
			if(row==topindex){
				int endCellindex=0;
				HashMap map = new HashMap();
				String isHaveLine = L==1?"1":"0";
				isHaveLine += "_";
				isHaveLine += B==1?"1":"0";
				isHaveLine += "_";
				isHaveLine += T==1?"1":"0";
				isHaveLine += "_";
				isHaveLine += R==1?"1":"0";
				String lineWidth=L_size+"_"+B_size+"_"+T_size+"_"+R_size;
				map.put("width", rwidth*1.0F);
				map.put("height", vertop_*1.0F);//每行高度
				map.put("realheight", rheight);//单元格实际高度
				map.put("value", strc);//单元格的值
				map.put("align", Align);//
				map.put("flag", flag);
				map.put("valign", 1);//垂直
				map.put("isHaveLine", isHaveLine);//
				map.put("lineWidth", lineWidth);//边框宽度
				map.put("subflag", subflag);
				map.put("title", false);
				map.put("fonteffect", fonteffect);
				map.put("fontname", fontname);
				map.put("fontsize", fontsize);
				map.put("fieldtype", field_type);
				map.put("nhide", nhide);
				map.put("inputtype", inputType);
				map.put("recordlist", recordlist);
				map.put("pageid", pageid);
				map.put("gridno", gridno);
				map.put("sub_domain", sub_domain);
				map.put("setname", setname);
				map.put("special_M", special_M);
				map.put("subfiledstate", subfiledstate);
				map.put("signatureValueList", signatureValueList);
				map.put("endRow", false);
				map.put("isTrans", isTrans);
				map.put("photoHeight", photoHeight);
				map.put("photoWidth", photoWidth);
				if("P".equalsIgnoreCase(flag)) {
					if(ldb.get("isExpand")!=null) {
						map.put("isExpand", (String)ldb.get("isExpand"));
					}
				}
				if(subflag){
					map.put("realRecordSize", recordlist!=null?recordlist.size()+"":"0");//子集实际条数
					reBuildSubflgRow(map, vertop_*1.0F, fieldmap, subMap);
				}
				if(pagemap.get(pageRows)==null){
					ArrayList celllist = new ArrayList(rlefts.size()+1);//每个单元格对应的对象
					for(int a = 0;a<rlefts.size();a++){
						celllist.add(null);
					}
					celllist.set(leftindex,map);
					pagemap.put(pageRows, celllist);//一行的所有单元格
				}else{
					((ArrayList)pagemap.get(pageRows)).set(leftindex,map);
				}
				//判断单元格是否需要行合并
				endCellindex=pageRows;
				for(int n=topindex;n<rtops.size();n++){
					//从topindex开始往下每一行的行高
					if(n+1>=rtops.size())
						break;
					int vertop =0; 
					int nexttop = Integer.parseInt(String.valueOf(rtops.get(n+1)));
					if(rtop+rheight>nexttop){//需要行合并 
						//就在当前行虚拟一个单元格
						//把上一行添加一个合并标记
						if(n+2>=rtops.size())
							vertop = rheight-(nexttop-rtop);
						else
							vertop = Integer.parseInt(String.valueOf(rtops.get(n+2)))-Integer.parseInt(String.valueOf(rtops.get(n+1)));
						((HashMap)((ArrayList)pagemap.get(pageRows)).get(leftindex)).put("first", "FIRST");
						HashMap nextmap = new HashMap();
						nextmap.put("width", rwidth*1.0F);
						nextmap.put("height", vertop*1.0F);//行高
						nextmap.put("realheight", rheight);//单元格实际高度
						nextmap.put("first", "PREVIOUS");
						nextmap.put("align", -1);
						nextmap.put("flag", flag);
						nextmap.put("valign", 1);//垂直
						nextmap.put("isHaveLine", isHaveLine);//
						nextmap.put("lineWidth", lineWidth);//边框宽度
						nextmap.put("subflag", subflag);
						nextmap.put("title", false);
						nextmap.put("fonteffect", fonteffect);
						nextmap.put("fontname", fontname);
						nextmap.put("fontsize", fontsize);
						nextmap.put("fieldtype", field_type);
						nextmap.put("nhide", nhide);
						nextmap.put("inputtype", inputType);
						nextmap.put("recordlist", recordlist);
						nextmap.put("pageid", pageid);
						nextmap.put("gridno", gridno);
						nextmap.put("sub_domain", sub_domain);
						nextmap.put("setname", setname);
						nextmap.put("subfiledstate", subfiledstate);
						nextmap.put("signatureValueList", signatureValueList);
						nextmap.put("endRow", false);
						nextmap.put("isTrans", isTrans);
						nextmap.put("special_M", special_M);
						nextmap.put("photoHeight", photoHeight);
						nextmap.put("photoWidth", photoWidth);
						if("P".equalsIgnoreCase(flag)) {
							if(ldb.get("isExpand")!=null) {
								map.put("isExpand", (String)ldb.get("isExpand"));
							}
						}
						if(pagemap.get(pageRows+n-topindex+1)==null){
							ArrayList celllist = new ArrayList(rlefts.size()+1);//每个单元格对应的对象
							for(int a = 0;a<rlefts.size();a++){
								celllist.add(null);
							}
							celllist.set(leftindex,nextmap);
							pagemap.put(pageRows+n-topindex+1, celllist);//一行的所有单元格
						}else{
							((ArrayList)pagemap.get(pageRows+n-topindex+1)).set(leftindex,nextmap);
						}
						endCellindex=pageRows+n-topindex+1;
					}else{//不需要行合并
					}
				}
				ArrayList nextArray=(ArrayList) pagemap.get(endCellindex);
				HashMap nextMap=(HashMap) nextArray.get(leftindex);
				nextMap.put("endRow", true);
			}
		}
		return pagemap;
	}
	/**
	 * 得到标题数据集
	 * @param titleList
	 * @param rlefts
	 * @param rtops_t
	 * @return
	 */
	private HashMap getTitleMap(ArrayList titleList,ArrayList rlefts,ArrayList rtops_t) {
		int row = 0;//行
		HashMap titlemap = new HashMap();//存储每行对应的单元格对象
		int left =Integer.parseInt((String)rlefts.get(0));
		HashMap topItem=new HashMap();//记录上一行top和 高度
		for(int i=0;i<titleList.size();i++){
			LazyDynaBean title=(LazyDynaBean) titleList.get(i);
			int gridno = (Integer)title.get("gridno");
			int pageid = (Integer)title.get("pageid");
			int tabid = title.getMap().containsKey("tabid")?(Integer)title.get("tabid"):0;//可不传
			int flag = (Integer)title.get("flag");
			int fonteffect = (Integer)title.get("fonteffect");
			String fontname = (String)title.get("fontname");
			int fontsize = (Integer)title.get("fontsize");
			String hz = title.getMap().containsKey("hz")?(String)title.get("hz"):"";//可不传
			int rtop = (Integer)title.get("rtop");
			int rleft = (Integer)title.get("rleft");
			int rwidth = (Integer)title.get("rwidth");
			int rheight = (Integer)title.get("rheight");
			String extendattr = title.getMap().containsKey("extendattr")?(String)title.get("extendattr"):"";//可不传
			String titlevalue = (String)title.get("titlevalue");
			//求出当前rtop在top集合中的位置
			int topindex = rtops_t.indexOf(rtop);
			if(row!=topindex){
				row++;
				if(rtop>=(Integer)topItem.get("top")&&rtop-(Integer)topItem.get("top")<(Integer)topItem.get("height"))
				{
					row--;
					rtop=(Integer)topItem.get("top");
				}
				topindex=row;
			}
			if(row==topindex){
				HashMap map = new HashMap();
				map.put("width", rwidth);
				map.put("height", rheight);
				map.put("value", titlevalue);
				map.put("pageid", pageid);
				map.put("gridno", gridno);
				map.put("flag", flag);
				map.put("rleft", rleft);
				map.put("rtop", rtop);
				map.put("fontname", fontname);
				map.put("fonteffect", fonteffect);
				map.put("fontsize", fontsize);
				map.put("index", i);
				topItem.put("top", rtop);
				topItem.put("height", rheight);
				if(titlemap.get(row)==null){
					/*ArrayList celllist = new ArrayList(rlefts_t.size()+1);//每个单元格对应的对象
					for(int a = 0;a<rlefts_t.size();a++){
						celllist.add(null);
					}
					celllist.set(leftindex,map);
					titlemap.put(row, celllist);//一行的所有单元格*/
					ArrayList celllist = new ArrayList();
					if(rleft>left)
					{
						celllist.add(null);
					}
					celllist.add(map);
					titlemap.put(row, celllist);//一行的所有单元格*/
				}else{
					    if(rleft-(Integer)topItem.get("rleft")>0)
					    {
					    	((ArrayList)titlemap.get(row)).add(null);
					    	((ArrayList)titlemap.get(row)).add(map);
					    }
					    else if((Integer)topItem.get("rleft")-rleft>0)
					    {
					    	((ArrayList)titlemap.get(row)).add(((ArrayList)titlemap.get(row)).size()-1,map);
					    	((ArrayList)titlemap.get(row)).add(((ArrayList)titlemap.get(row)).size()-1,null);
					    	
					    }
					    else
						 ((ArrayList)titlemap.get(row)).add(map);
					}
				}
				topItem.put("rleft", rleft);
			}
		return titlemap;
	}
	/**
	 * 重组子集部分数据集
	 * @param list
	 * @param rowHeight
	 * @param fieldmap
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException 
	 */
	private void reBuildSubflgRow(HashMap map,Float rowHeight,HashMap fieldmap,HashMap subMap) throws SQLException, UnsupportedEncodingException {
		Float titleLineHeight=41F;//标题行高度
		Float dataLineHeight=35F;//数据行高度
		TSubSetDomain subdom=null;
		String flag=(String)map.get("flag");
        ArrayList RecordList=new ArrayList();
        String first=(String)map.get("first");
        int pageid = (Integer)map.get("pageid");
        int gridno = (Integer)map.get("gridno");
        boolean subflag = (Boolean)map.get("subflag");
        int realheight = (Integer)map.get("realheight");//实际高度
		int fonteffect = (Integer)map.get("fonteffect");
		String fontname = (String)map.get("fontname");
		int fontsize = (Integer)map.get("fontsize");
        float rwidth = (Float)map.get("width");
        String field_type = (String)map.get("fieldtype");
        int nhide = (Integer)map.get("nhide");
        String sub_domain = (String)map.get("sub_domain");
        String setname = (String)map.get("setname");
        HashMap subfiledstate = (HashMap)map.get("subfiledstate");
        ArrayList signatureValueList = (ArrayList)map.get("signatureValueList");
        int rows=0;//记录输出的行数
        int  virtualRows=0;//记录虚拟的行数，用于撑高子集所在单元格。
        Float totleHeight=0F;//记录实际输出行累计高度。
        if(subMap.containsKey(pageid+"_"+gridno))//子集被拆分为多个部分，所有数据在第一部分输出。其他部分合并不输出数据。
    	{
        	return;
    	}
    	else
    	{
    		subMap.put(pageid+"_"+gridno, 1);//确定首次显示子集部分
    	}
        subdom=new TSubSetDomain(sub_domain);
		//判断是否需要显示子集序号。需要在子集中增加一列序号列。
        Boolean isNeedSubsetNo=false;
		String showSubsetsOrder= new String(SystemConfig.getPropertyValue("showSubsetsOrder"));
		if(StringUtils.isNotBlank(showSubsetsOrder)){
			String[] tabids=showSubsetsOrder.split(",");
			for(int i=0;i<tabids.length;i++){
				if(this.tabid.equals(tabids[i])){
					isNeedSubsetNo=true;
					break;
				}
			}
		}
		if(isNeedSubsetNo){
	        ArrayList list=subdom.getFieldfmtlist();
	        TFieldFormat fieldformat=new TFieldFormat();
			fieldformat.setName("序号");
			fieldformat.setTitle("序号");
			fieldformat.setValue("");
			fieldformat.setWidth(10);
			fieldformat.setSlop("0");	//xieguiquan 20101027
			fieldformat.setAlign(1);
			fieldformat.setValign(1);
			fieldformat.setBneed(false);
	        list.add(0, fieldformat);
	        subdom.setFieldfmtlist(list);
		}
        ArrayList fieldfmtlist = subdom.getFieldfmtlist();
		for(Iterator it= fieldfmtlist.iterator();it.hasNext();){  //过滤子集附件列
    		TFieldFormat fieldformat=(TFieldFormat)it.next();
    		if("attach".equalsIgnoreCase(fieldformat.getName()))//attach表示是附件列
			{
				it.remove();
			}else{
				String a_state="2";
				if(subfiledstate.size()>0)
					a_state = (String)subfiledstate.get(fieldformat.getName().toLowerCase());
				if("0".equals(a_state))
					it.remove();
			}
    	}
		reSetWidth(rwidth,subdom);//重新计算每列宽度
        RecordList=(ArrayList)map.get("recordlist");//子集的数据
        if(subdom.getColheadheight()>0)//如果设置了标题行高，标题行高按照设置的走，否则为默认值41
		{
        	titleLineHeight = (float)(subdom.getColheadheight()/25.4*PixelInInch);//转换成像素
		}
		if(subdom.getDatarowcount()>0){//如果设置了指定行数 按这个走
			rows = subdom.getDatarowcount();
			totleHeight=rows*dataLineHeight;
			if(subdom.isBcolhead()){//是否输出标题
				totleHeight+=titleLineHeight;
				rows = rows+1;
			}
		}
		else{
			if(RecordList.size()>0){//没有指定行数，子集有记录
				rows = RecordList.size();
				totleHeight=rows*dataLineHeight;
				if(subdom.isBcolhead()){
					totleHeight+=titleLineHeight;
					rows = RecordList.size()+1;	
				}
			}else{//没有指定行数，子集没有记录。
				rows=0;
				totleHeight=rows*dataLineHeight;
				if(subdom.isBcolhead()){//是否输出标题
					totleHeight+=titleLineHeight;
					rows = rows+1;
				}
			}
		}
		//如果计算高度比画的高度大且没有设置自动延伸子集，重新计算数据行高，将数据行高减小。
		if(totleHeight>realheight&&"false".equals(subdom.getAutoextend())){
			dataLineHeight=(float) Math.ceil(realheight*1.0f/rows);
			if(subdom.isBcolhead()){//是否输出标题
				dataLineHeight=(float) Math.ceil((realheight*1.0f-titleLineHeight*1.0f)/(rows-1));
			}
		}
		if(realheight>totleHeight){
			virtualRows= (int) Math.floor((realheight-totleHeight)/dataLineHeight);
		}
		//计算加上虚拟行后的高度，用于和画的真是高度做比较，判断是否有剩余高度需要平均分配到各行上。不会出现小空白条。
		int totleHeight_=0;
		int totleRows=rows+virtualRows;
		if(subdom.isBcolhead()){
			totleRows=totleRows-1;
			totleHeight_=(int) (totleRows*dataLineHeight+titleLineHeight);
		}else{
			totleHeight_=(int) (totleRows*dataLineHeight);
		}
		if(realheight>totleHeight_&&realheight-totleHeight_<dataLineHeight){
			int tempRows=totleRows;
			if(totleRows==0) {
				tempRows=1;
			}
			//因为画的格高度不会正好是行高的整数倍，所以会留有空白，如果想差高度小于一行行高，把这个高度平均分配到每一行上。
			dataLineHeight+=(realheight-totleHeight_+totleRows/2)/tempRows;
		}
		ArrayList newRowList=new ArrayList();//保存子集总数据集合，每一个元素里记录的是一行的数据
		ResetFontSizeUtil rfsu=new ResetFontSizeUtil();
		float totleLocalHeight=0;
		int subrows=rows;
		for(int j=0;j<rows;j++){
			float rowLocalheight=0;
			ArrayList rowList=new ArrayList();//保存子集每行数据集合
				if(j==0&&subdom.isBcolhead()){//组装标题行记录
					if(RecordList.size()==0&&setname!=null&&setname.indexOf("attachment")>-1) {//公共附件、个人附件没有内容不输出标题。
						rows=0;
						map.put("nhide", 1);
						break;
					}
					else {//子集输出标题
						for(int t=0;t<fieldfmtlist.size();t++){
							HashMap newap = new HashMap();
							String isHaveLine = "";//记录四边是否有边框。
							if(setname!=null&&setname.indexOf("attachment")>-1){//公共附件、个人附件都有边框
								isHaveLine = "1_1_1_1";
							}else{//子集按照勾选的参数设置。
								isHaveLine=subdom.isBvl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+= subdom.isBhl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+= subdom.isBhl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+=subdom.isBvl()?"1":"0";
							}
			            	TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(t); 
			            	int lines=rfsu.getStrLines(fieldformat.getTitle(), fontsize, fontname, fonteffect, fieldformat.getWidth());
			            	if(lines>10){
				            	int minuxLines=lines/10;
				            	lines=lines-minuxLines;
			            	}
			            	int height=rfsu.CharHeight(fontsize, fontname, fonteffect);
			            	float localHeight=lines*height;
			            	if(localHeight<titleLineHeight){
			            		localHeight=titleLineHeight;
			            	}
			            	if(rowLocalheight<localHeight){
			            		rowLocalheight=localHeight;
			            	}
			            	newap.put("width", fieldformat.getWidth()*1.0F);
							newap.put("height",  titleLineHeight);
							newap.put("realheight", realheight);
							newap.put("flag", flag);
							newap.put("align", 1);//水平对齐方式
							newap.put("valign", 1);//垂直对齐方式
							newap.put("isHaveLine", isHaveLine);
							newap.put("subflag", subflag);
							newap.put("value", fieldformat.getTitle());
							newap.put("first", null);
							newap.put("h1", subdom.isBhl());//是否显示横线
							newap.put("v1", subdom.isBvl());//是否显示竖线
							newap.put("colhead", subdom.isBcolhead());
							newap.put("title", true);//是否是标题
							newap.put("fonteffect", fonteffect);
							newap.put("fontname", fontname);
							newap.put("fontsize", fontsize);
							newap.put("fieldtype", field_type);
							newap.put("nhide", nhide);
							newap.put("inputtype", map.get("inputtype"));
							newap.put("recordlist", RecordList);
							newap.put("pageid", pageid);
							newap.put("gridno", gridno);
							newap.put("sub_domain", sub_domain);
							newap.put("setname", setname);
							newap.put("subfiledstate", subfiledstate);
							rowList.add(newap);
							map.put("recordTitle", true);
				        }
						totleLocalHeight+=rowLocalheight;
					}
			}else{//组装数据行记录
				if(RecordList.size()>0&&j<rows)//子集或附件有记录，按照内容输出
				{
					HashMap rowRecord=(HashMap)RecordList.get(0);
					Float rowWidth=0F;
					for(int t=0;t<fieldfmtlist.size();t++){
						HashMap newap = new HashMap();
						String isHaveLine = "";
						if(setname!=null&&setname.indexOf("attachment")>-1){//公共附件、个人附件都有边框
							isHaveLine = "1_1_1_1";
						}else{
							isHaveLine=subdom.isBvl()?"1":"0";
							isHaveLine+="_";
							isHaveLine+= subdom.isBhl()?"1":"0";
							isHaveLine+="_";
							isHaveLine+= subdom.isBhl()?"1":"0";
							isHaveLine+="_";
							isHaveLine+=subdom.isBvl()?"1":"0";
						}
		            	TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(t); 
		            	
		            	newap.put("width", fieldformat.getWidth()*1.0F);
		            	newap.put("height", dataLineHeight);
						newap.put("realheight", realheight);
						newap.put("flag",flag);
						newap.put("align", fieldformat.getAlign());
						newap.put("valign", fieldformat.getValign());//垂直
						newap.put("isHaveLine", isHaveLine);
						newap.put("subflag", subflag);
						newap.put("title", false);
						newap.put("fonteffect", fonteffect);
						newap.put("fontname", fontname);
						newap.put("fontsize", fontsize);
						newap.put("fieldtype", field_type);
						newap.put("nhide", nhide);
						newap.put("inputtype", map.get("inputtype"));
						newap.put("recordlist", RecordList);
						newap.put("pageid", pageid);
						newap.put("gridno", gridno);
						newap.put("sub_domain", sub_domain);
						newap.put("setname", setname);
						newap.put("subfiledstate", subfiledstate);
						rowWidth+=fieldformat.getWidth()*1.0F;
						String name=fieldformat.getName().toLowerCase();
						//如果需要显示序号列，序号值根据循环值获取。如果显示标题和j的值相同，不显示标题需要j+1
						String value="";
						if(isNeedSubsetNo&&t==0){
							if(subdom.isBcolhead()){
								value=String.valueOf(j);
							}else{
								value=String.valueOf(j+1);
							}
						}else{
							value=(String) (rowRecord.get(name)!=null?rowRecord.get(name):"");
							FieldItem item=DataDictionary.getFieldItem(name);
							if(item!=null&&!"F".equalsIgnoreCase(flag)){//不是附件的时候（附件是模拟的子集）
								String slop =fieldformat.getSlop();
								if(item!=null&& "A".equalsIgnoreCase(item.getItemtype())&&(!"0".equalsIgnoreCase(item.getCodesetid()))) {
									if("UM".equalsIgnoreCase(item.getCodesetid())) {//bug 47674 
										String value_bak=value;
										value=AdminCode.getCodeName(item.getCodesetid(), value);
										if(StringUtils.isEmpty(value)) {
											value=AdminCode.getCodeName("UN", value_bak);
										}
									}else {
										value=AdminCode.getCodeName(item.getCodesetid(), value);
									}
								}else if(item!=null&& "D".equalsIgnoreCase(item.getItemtype())){
									//if(!"".equals(value)&&(value.indexOf(".")!=-1||value.indexOf("-")!=-1)){//bug36469 空值不输出-至今前缀
										value=value.replace(".", "-");
										if(slop!=null&&!"".equals(slop)){
											value = this.formatDateFiledsetValue(value.trim(), fieldformat.getPre(), Integer.parseInt(slop));
										}
									//}
								}
								String a_state="2";
								if(subfiledstate.size()>0)
									a_state = (String)subfiledstate.get(name);
								if("0".equals(a_state))
									value="";	
							}
						}
						int index=value.lastIndexOf("\n");
						if(index==value.length()-1&&value.length()>0){
							value=value.substring(0,index);
						}
						
						newap.put("first", null);
						int lines=rfsu.getStrLines(value, fontsize, fontname, fonteffect, fieldformat.getWidth());
		            	int height=rfsu.CharHeight(fontsize, fontname, fonteffect);
		            	float localHeight=lines*height;
		            	if(localHeight<dataLineHeight){
		            		localHeight=dataLineHeight;
		            	}
		            	if(rowLocalheight<localHeight){
		            		rowLocalheight=localHeight;
		            	}
		            	value=value.replace("\n", "\r");
						newap.put("value", value);
						rowList.add(newap);
			        }
					totleLocalHeight+=rowLocalheight;
					RecordList.remove(0);//组装完一行，从集合中去掉一行
				}
				else if(j<rows)//子集或附件没有记录，或者记录数少于指定行数，补充空行根据设置单元格边线
				{
					if(totleLocalHeight>=realheight){//补充空行时若数据高度大于实际高度 跳出不添加空行
						subrows=j;//49949 
						break;
					}
					Float rowWidth=0F;
					for(int t=0;t<fieldfmtlist.size();t++){
						HashMap newap = new HashMap();
						String isHaveLine = "";
						if(setname!=null&&setname.indexOf("attachment")>-1){
							isHaveLine = "0_0_0_0";
						}else{
								isHaveLine=subdom.isBvl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+= subdom.isBhl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+= subdom.isBhl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+=subdom.isBvl()?"1":"0";
						}
		            	TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(t); 
		            	newap.put("width", fieldformat.getWidth()*1.0F);
						newap.put("height", dataLineHeight);
						newap.put("realheight", realheight);
						newap.put("flag", flag);
						newap.put("align", fieldformat.getAlign());
						newap.put("valign", fieldformat.getValign());//垂直
						newap.put("subflag", subflag);
						newap.put("isHaveLine", isHaveLine);
						newap.put("value", "");
						newap.put("first", null);
						newap.put("title", false);
						newap.put("fonteffect", fonteffect);
						newap.put("fontname", fontname);
						newap.put("fontsize", fontsize);
						newap.put("fieldtype", field_type);
						newap.put("nhide", nhide);
						newap.put("inputtype", map.get("inputtype"));
						newap.put("recordlist", RecordList);
						newap.put("pageid", pageid);
						newap.put("gridno", gridno);
						newap.put("sub_domain", sub_domain);
						newap.put("setname", setname);
						newap.put("subfiledstate", subfiledstate);
						rowList.add(newap);
					}
					totleLocalHeight+=dataLineHeight;
				}else{//bug 38535 补充的虚拟格不显示单元格边线。
					if(totleLocalHeight>=realheight){
						subrows=j-1;
						break;
					}
					Float rowWidth=0F;
					for(int t=0;t<fieldfmtlist.size();t++){
						HashMap newap = new HashMap();
						String isHaveLine = "";
						if(setname!=null&&setname.indexOf("attachment")>-1){
							isHaveLine = "0_0_0_0";
						}else{
							if(subdom.getDatarowcount()>0){
								isHaveLine = "0_0_0_0";
							}else{
								isHaveLine=subdom.isBvl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+= subdom.isBhl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+= subdom.isBhl()?"1":"0";
								isHaveLine+="_";
								isHaveLine+=subdom.isBvl()?"1":"0";
							}
						}
		            	TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(t); 
		            	newap.put("width", fieldformat.getWidth()*1.0F);
						newap.put("height", dataLineHeight);
						newap.put("realheight", realheight);
						newap.put("flag", flag);
						newap.put("align", fieldformat.getAlign());
						newap.put("valign", fieldformat.getValign());//垂直
						newap.put("subflag", subflag);
						newap.put("isHaveLine", isHaveLine);
						newap.put("value", "");
						newap.put("first", null);
						newap.put("title", false);
						newap.put("fonteffect", fonteffect);
						newap.put("fontname", fontname);
						newap.put("fontsize", fontsize);
						newap.put("fieldtype", field_type);
						newap.put("nhide", nhide);
						newap.put("inputtype", map.get("inputtype"));
						newap.put("recordlist", RecordList);
						newap.put("pageid", pageid);
						newap.put("gridno", gridno);
						newap.put("sub_domain", sub_domain);
						newap.put("setname", setname);
						newap.put("subfiledstate", subfiledstate);
						rowList.add(newap);
					}
					totleLocalHeight+=dataLineHeight;
				}
			}
				newRowList.add(rowList);
		}
		Boolean isNeedWarp=false;
		if(totleLocalHeight<realheight&&realheight-totleLocalHeight>dataLineHeight){
			isNeedWarp=true;
		}
		map.put("subRows",subrows);//记录子集的数据行数
		map.put("isNeedWarp",isNeedWarp);//外面是否需要嵌套
		map.put("subCols", fieldfmtlist.size());//记录子集的列数
		map.put("newRowList", newRowList);//子集数据行
	}
	/**
	 * 子集中格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @param ext 扩展
	 * @return
	 */
	public String formatDateFiledsetValue(String value,String ext ,int disformat)
	{
		StringBuffer buf=new StringBuffer();
		int idx=ext.indexOf(",");  //-,至今
		String prefix="",strext="";
		if(idx==-1)
		{
			String[] preCond=getPrefixCond(ext);
			prefix=preCond[0];
		}
		else
		{
			prefix=ext.substring(0,idx);
			strext=ext.substring(idx+1);
		}
		if("".equals(value))
		{
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		}
		else
		{
			buf.append(prefix);
		}
		value=value.replace(".", "-");
		Date date=DateUtils.getDate(value,"yyyy-MM-dd");
		if(date!=null){
			int year=DateUtils.getYear(date);
			int month=DateUtils.getMonth(date);
			int day=DateUtils.getDay(date);
			String[] strv =exchangNumToCn(year,month,day);
			value=value.replaceAll("-",".");
			switch(disformat)
			{
			case 0: //1991.12.3
				buf.append(year);
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append(day);
				break;
			case 1: //91.12.3
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append(day);
				break;
			case 2://1991.2
				buf.append(year);
				buf.append(".");
				buf.append(month);			
				break;
			case 3://1992.02
	            buf.append(year);
	            buf.append(".");            
	            if (month>9){                
	                buf.append(month);  
	            }
	            else {
	                buf.append("0"+month);    
	            }
				
				break;
			case 4://92.2
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append(".");
				buf.append(month);
				break;
			case 5://98.02
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append(".");
				if(month>=10)
					buf.append(month);
				else
				{
					buf.append("0");
					buf.append(month);
				}
				break;
			case 6://一九九一年一月二日
	
				buf.append(strv[0]);
				buf.append("年");
				buf.append(strv[1]);
				buf.append("月");
				buf.append(strv[2]);
				buf.append("日");
				break;
			case 7://一九九一年一月
				buf.append(strv[0]);
				buf.append("年");
				buf.append(strv[1]);
				buf.append("月");			
				break;
			case 8://1991年1月2日
				buf.append(year);
				buf.append("年");
				buf.append(month);
				buf.append("月");
				buf.append(day);
				buf.append("日");
				break;
			case 9://1991年1月
				buf.append(year);
				buf.append("年");
				buf.append(month);
				buf.append("月");
				break;
			case 10://91年1月2日
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append("年");
				buf.append(month);
				buf.append("月");
				buf.append(day);
				buf.append("日");
				break;
			case 11://91年1月
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append("年");
				buf.append(month);
				buf.append("月");			
				break;
			case 12://年龄
				buf.append(getAge(year,month,day));
				break;
			case 13://1991（年）
				buf.append(year);
				break;
			case 14://1 （月）
				buf.append(month);
				break;
			case 15://23 （日）
				buf.append(day);
				break;
			case 16://1999年02月
				buf.append(year);
				buf.append("年");
				if(month>=10)
					buf.append(month);
				else
				{
					buf.append("0");
					buf.append(month);
				}
				buf.append("月");
				break;
			case 17://1999年02月03日
				buf.append(year);
				buf.append("年");
				if(month>=10)
					buf.append(month);
				else
				{
					buf.append("0");
					buf.append(month);
				}
				buf.append("月");
				if(day>=10)
					buf.append(day);
				else
				{
					buf.append("0");
					buf.append(day);
				}		
				buf.append("日");
				break;
			case 18://1992.02.01
				buf.append(year);
				buf.append(".");
				if(month>=10)
					buf.append(month);
				else
				{
					buf.append("0");
					buf.append(month);
				}
				buf.append(".");
				if(day>=10)
					buf.append(day);
				else
				{
					buf.append("0");
					buf.append(day);
				}		
				break;
			default:
				buf.append(year);
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append(day);			
				break;
			}
		}
		return buf.toString();
	}
	/**
	 * 解释Formula字段的内容
	 * for example
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * @return
	 */
	  private String[] getPrefixCond(String formula)
	  {
		   String[] preCond=new String[3];
		   int idx=formula.indexOf("<");
		   if(idx==-1)
		   {
			   preCond[0]=formula; 
		   }
		   else
		   {
			   preCond[0]=formula.substring(0, idx);
			   preCond[2]=getPattern("FACTOR",formula)+",";
			   preCond[2]=preCond[2].replaceAll(",", "`");
			   preCond[1]=getPattern("EXPR",formula);
		   }
		   return preCond;
	  }	
	private String getPattern(String strPattern,String formula)
	{
		int iS,iE;
		String result="";
		String sSP="<"+strPattern+">";
		iS=formula.indexOf(sSP);
		String sEP="</"+strPattern+">";
		iE=formula.indexOf(sEP);
		if(iS>=0 && iS<iE)
		{
			result=formula.substring(iS+sSP.length(), iE);
		}
		return result;
	}
	/**
	 * 数字换算
	 * @param strV
	 * @param flag
	 * @return
	 */
	private String[] exchangNumToCn(int year,int month,int day)
	{
		String[] strarr=new String[3];
		StringBuffer buf=new StringBuffer();
		String value=String.valueOf(year);
		for(int i=0;i<value.length();i++)
		{
			switch(value.charAt(i))
			{
			case '1':
				buf.append("一");
				break;
			case '2':
				buf.append("二");
				break;
			case '3':
				buf.append("三");
				break;
			case '4':
				buf.append("四");
				break;
			case '5':
				buf.append("五");
				break;
			case '6':
				buf.append("六");
				break;
			case '7':
				buf.append("七");
				break;
			case '8':
				buf.append("八");
				break;
			case '9':
				buf.append("九");
				break;
			case '0':
				buf.append("零");
				break;
			}
		}
		strarr[0]=buf.toString();
		buf.setLength(0);
		switch(month)
		{
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;			
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;
		}
		strarr[1]=buf.toString();
		buf.setLength(0);
		switch(day)
		{
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;			
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;			
		case 13:
			buf.append("十三");
			break;			
		case 14:
			buf.append("十四");
			break;			
		case 15:
			buf.append("十五");
			break;			
		case 16:
			buf.append("十六");
			break;			
		case 17:
			buf.append("十七");
			break;			
		case 18:
			buf.append("十八");
			break;			
		case 19:
			buf.append("十九");
			break;			
		case 20:
			buf.append("二十");	
			break;			
		case 21:
			buf.append("二十一");
			break;			
		case 22:
			buf.append("二十二");	
			break;			
		case 23:
			buf.append("二十三");
			break;			
		case 24:
			buf.append("二十四");	
			break;			
		case 25:
			buf.append("二十五");
			break;			
		case 26:
			buf.append("二十六");	
			break;			
		case 27:
			buf.append("二十七");
			break;			
		case 28:
			buf.append("二十八");	
			break;			
		case 29:
			buf.append("二十九");
			break;			
		case 30:
			buf.append("三十");	
			break;			
		case 31:
			buf.append("三十一");				
			break;
		}		
		strarr[2]=buf.toString();
		return strarr;
	}
	/**
	 * 计算年龄
	 * @param nyear
	 * @param nmonth
	 * @param nday
	 * @return
	 */
	private String getAge(int nyear,int nmonth,int nday)
	{
		int ncyear,ncmonth,ncday;
		Date curdate=new Date();
		ncyear=DateUtils.getYear(curdate);
		ncmonth=DateUtils.getMonth(curdate);
		ncday=DateUtils.getDay(curdate);
		StringBuffer buf=new StringBuffer();
	
		int result =ncyear-nyear;   
        if   (nmonth>ncmonth)   {   
            result = result-1;   
        }   
        else 
        {
            if   (nmonth==ncmonth)  {   
                if   (nday >ncday)   {   
                    result   =   result   -   1;   
                }   
            }   
        }
		buf.append(result);
		return buf.toString();
	}
	public void reSetWidth(float rwidth, TSubSetDomain subdom)
	{
		int sumWidth=0;
		int width_ = 0;
		for(int i=0;i<subdom.getFieldfmtlist().size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);
			sumWidth=sumWidth+fieldformat.getWidth();
		}//for i loop end.
		sumWidth = sumWidth==0?1:sumWidth;
		float fScale=(float)rwidth/sumWidth;
		for(int i=0;i<subdom.getFieldfmtlist().size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);
			int width_mid=Math.round(fScale*fieldformat.getWidth());
			width_+=width_mid;
			if(i==subdom.getFieldfmtlist().size()-1)
			{
				if(rwidth-width_!=0)
				{
					width_mid=width_mid+(int)rwidth-width_;
				}
	
			}
			fieldformat.setWidth(width_mid);
		}
	}
	public String getDowntype() {
		return downtype;
	}
	public void setDowntype(String downtype) {
		this.downtype = downtype;
	}
	public boolean isUsePageMarginSet() {
		return isUsePageMarginSet;
	}
	public void setUsePageMarginSet(boolean isUsePageMarginSet) {
		this.isUsePageMarginSet = isUsePageMarginSet;
	}
	public String getOuttype() {
		return outtype;
	}
	public void setOuttype(String outtype) {
		this.outtype = outtype;
	}
	public int getSigntype() {
		return signtype;
	}
	public void setSigntype(int signtype) {
		this.signtype = signtype;
	}
}
