package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TemplateCardBo;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.ExcelLayoutDao;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.impl.ExcelLayoutDaoImpl;
import com.hjsj.hrms.module.template.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Mar 3, 2010 11:39:22 AM
 * </p>
 * 
 * @author dengc
 * @version 5.0
 */
public class ExcelDownLoadBo {
    /**模板号*/
	private int tabid=0;
    private UserView userview = null;
    /**获取dao*/
	private ExcelLayoutDao excelLayoutDao =null;
	private String jpg_path="/com/hjsj/hrms/constant/descript.jpg";
	private Connection conn=null;
    
    public ExcelDownLoadBo(int tabid, Connection conn, UserView userview) {
        this.tabid = tabid;
        this.userview = userview;
        this.excelLayoutDao=new ExcelLayoutDaoImpl(conn);
        this.conn=conn;
    }
    
	/**
	 * 获得TemplateSet对象
	 * @param rset
	 * @param var_hm  模版相关的变量集
	 * @return
	 */
	private TemplateSet getTemplateSet(LazyDynaBean pagevo,HashMap var_hm)
	{
		TemplateSet setBo = new TemplateSet(); 
		try
		{ 
			setBo.setTabId(Integer.parseInt((String)pagevo.get("tabid")));
			setBo.setPageId(Integer.parseInt((String)pagevo.get("pageid")));
			setBo.setHz(nullToSpace((String)pagevo.get("hz")));// 设置表格的汉字描述
			setBo.setSetname(nullToSpace((String)pagevo.get("setname")));// 设置子集的代码
			setBo.setCodeid(nullToSpace((String)pagevo.get("codeid")));// 相关的代码类
			setBo.setField_hz(nullToSpace((String)pagevo.get("field_hz")));// 字段的汉字描述 取自业务字典
			setBo.setField_name(nullToSpace((String)pagevo.get("field_name")));// 指标的代码
			String flag = (String)pagevo.get("flag") == null ? "" : (String)pagevo.get("flag");// 数据源的标识（文本描述、照片......）
			setBo.setFlag((String)pagevo.get("flag"));// 设置数据源的标识
			String temp = (String)pagevo.get("subflag");// 子表控制符 0：字段 1：子集
			if (temp == null || "".equals(temp) || "0".equals(temp))
				setBo.setSubflag(false);
			else{
			    setBo.setSubflag(true);
			}
			setBo.setField_type(nullToSpace((String)pagevo.get("field_type")));
			setBo.setOld_fieldType(nullToSpace((String)pagevo.get("field_type")));
			setBo.setFormula((String)pagevo.get("formula"));// 设置字段的计算公式
			setBo.setAlign(Integer.parseInt((String)pagevo.get("align")));// 文字在单元格中的排列方式
			setBo.setDisformat(Integer.parseInt((String)pagevo.get("disformat")));// 设置数据的格式

			if ("V".equalsIgnoreCase(flag)) {// 变量
				RecordVo vo = (RecordVo) var_hm.get((String)pagevo.get("field_name"));
				if (vo != null) {
					setBo.setDisformat(vo.getInt("flddec"));// 如果是临时变量
															// 么要根据临时变量表里面的小数位数来设置
					setBo.setVarVo(vo);
				}
			}
			setBo.setChgstate(Integer.parseInt((String)pagevo.get("chgstate")));// 设置字段是变化前还是变化后
			setBo.setFonteffect(Integer.parseInt((String)pagevo.get("fonteffect")));// 设置字体效果
			setBo.setFontname((String)pagevo.get("fontName"));// 设置字体名称
			setBo.setFontsize(Integer.parseInt((String)pagevo.get("fontsize")));// 设置字体大小
			setBo.setHismode(Integer.parseInt((String)pagevo.get("hismode")));// 设置历史定位方式
			if (Sql_switcher.searchDbServer() == 2)
				setBo.setMode(Integer.parseInt((String)pagevo.get("mode_o")));
			else
				setBo.setMode(Integer.parseInt((String)pagevo.get("mode")));// 多条记录的时候 那几种选择
			// (最近..最初..)
			setBo.setNsort(Integer.parseInt((String)pagevo.get("nsort")));// 相同指示顺序号
			setBo.setGridno(Integer.parseInt((String)pagevo.get("gridno")));// 单元格号
			setBo.setRcount(Integer.parseInt((String)pagevo.get("rcount")));// 记录数 和HisMode
			// 配合试用（标识最近（Rcount条））
			setBo.setRheight(Integer.parseInt((String)pagevo.get("rheight")));// 设置单元格高度
			setBo.setRleft(Integer.parseInt((String)pagevo.get("rleft")));// 单元格左边的坐标值
			setBo.setRwidth(Integer.parseInt((String)pagevo.get("rwidth")));// 单元格的宽度
			setBo.setRtop(Integer.parseInt((String)pagevo.get("rtop")));// 单元格上边坐标值
			setBo.setL(Integer.parseInt((String)pagevo.get("l")));
			/** LBRT 代表着表格左下右上是否有线 **/
			setBo.setB(Integer.parseInt((String)pagevo.get("b")));
			setBo.setR(Integer.parseInt((String)pagevo.get("r")));
			setBo.setT(Integer.parseInt((String)pagevo.get("t")));

			if (Integer.parseInt((String)pagevo.get("yneed")) == 0)
				setBo.setYneed(false);
			else
				setBo.setYneed(true);
			String sub_domain = (String)pagevo.get("sub_domain");
			setBo.setXml_param(sub_domain);
	
			if ((String)pagevo.get("nhide") != null)
				setBo.setNhide(Integer.parseInt((String)pagevo.get("nhide")));
			else
				setBo.setNhide(0);// 打印还是隐藏 0：打印 1：隐藏
			
			// 普通指标需检查是否构库 未构库的过滤掉
			if ( setBo.isABKItem() && !setBo.isSpecialItem()){
				if (!setBo.isSubflag()) {
				    FieldItem item = DataDictionary.getFieldItem(setBo.getField_name());
				}
				else {
				    FieldSet fieldset=DataDictionary.getFieldSetVo(setBo.getSetname());
                    if(fieldset==null)
                    {
                        return null; 
                    }
                    else {
                    	setBo.setField_hz(fieldset.getFieldsetdesc());
                    }
				}
			}else if(setBo.isSpecialItem()){//特殊字段 lis 20160706
				if("parentid".equals(setBo.getField_name()))//上级组织`单元名称`
					setBo.setCodeid((String)pagevo.get("codeid"));
				else
					setBo.setCodeid("0");
			}
			
			if (setBo.isNeedChangeFieldType()) {
				setBo.setField_type("M");
			}
			if (setBo.isSubflag()){
			    setBo.setField_type("M");
			}
			if ("V".equalsIgnoreCase(flag)) {// 临时变量
				if(setBo.getVarVo()!=null){//如果模板中设置的临时变量在临时变量表中不存在  则不予计算了
					String codeid= setBo.getVarVo().getString("codesetid");
					setBo.setCodeid(codeid); 
				}
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return setBo;
	}
	
	/** 
	 * @Title: getPageCell 
	 * @Description: 返回指定页的所有单元格celllist
	 * @param @param tabId
	 * @param @param pagenum
	 * @param @return
	 * @return ArrayList
	 */ 
	public ArrayList getPageCell(int pageNum) {
		ArrayList new_setbo=new ArrayList();
		boolean readDatabase=false;
		if (pageNum<=-1) {
			pageNum=-1;
		}
		HashMap var_hm = this.excelLayoutDao.getAllVariableHm(this.tabid);
		if(var_hm==null){
			var_hm=new HashMap();
		}
		HashMap setMap=new HashMap();
		ArrayList pageSetbo=this.excelLayoutDao.getPageSetList(tabid, pageNum+"");
		for(int k=0;k<pageSetbo.size();k++){
			LazyDynaBean pagevo = (LazyDynaBean) pageSetbo.get(k);
			TemplateSet setBo=getTemplateSet(pagevo,var_hm);
			if(setBo!=null){
				int page_id=setBo.getPageId();
				if(setMap.containsKey(page_id)){
					ArrayList list=(ArrayList) setMap.get(page_id);
					list.add(setBo);
				}else{
					ArrayList list=new ArrayList();
					list.add(setBo);
					setMap.put(page_id, list);
				}
			}				
		}
		//重新设置单元格四条边线
		int b=0;
		int l=0;
		int r=0;
		int t=0;
		ArrayList setBoList = new ArrayList();
		Iterator iter = setMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			setBoList=(ArrayList) entry.getValue();
			String page_id=String.valueOf(entry.getKey());
			ArrayList page_new_setBo=new ArrayList();
			if(setBoList!=null){
				for(int i=0;i<setBoList.size();i++)
				{
					TemplateSet cur_setbo =(TemplateSet)setBoList.get(i);  
					b=getRlineForList(setBoList,"b",cur_setbo.getB(),cur_setbo);
					l=getRlineForList(setBoList,"l",cur_setbo.getL(),cur_setbo);
					r=getRlineForList(setBoList,"r",cur_setbo.getR(),cur_setbo);
					t=getRlineForList(setBoList,"t",cur_setbo.getT(),cur_setbo);
					cur_setbo.setB(b);                  
					cur_setbo.setL(l);
					cur_setbo.setR(r);
					cur_setbo.setT(t);
					new_setbo.add(cur_setbo);
					page_new_setBo.add(cur_setbo);
				}
			}
		}
		return new_setbo;
	}
	
	    
	    /**
	     * 重新取得线型，由于画线的原因
	     * @param list
	     * @param flag
	     * @param line
	     * @param cur_setbo//当前操作对象
	     * @return
	     */
	    private static int  getRlineForList(ArrayList list,String flag,int line,TemplateSet cur_setbo)
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
		 * 如果为null返回“”字符串
		 * @param value
		 * @return
		 */
		private static String nullToSpace(String value)
		{
			if(value==null)
				return "";
			else 
				return value;
		}
		 
		/**
		 * 输出Excel模板
		 * @return
		 */
		public String outExcel() {
			/**获取所有的模板指标*/
            /**设置的不显示页签*/
            String noshow_pageno="";
			ArrayList pagelist=getPageList(this.tabid, false, noshow_pageno, "0");
			// 创建excel报表并写入数据
        	HSSFWorkbook wb = new HSSFWorkbook();
        	// 第二种style--字体10，水平居中，垂直居中，黑色边框，自动换行
        	HSSFFont font2 = wb.createFont();
        	font2.setFontHeightInPoints((short) 10);
        	HSSFCellStyle style = wb.createCellStyle();
        	style.setFont(font2);
        	style.setAlignment(HorizontalAlignment.CENTER);
        	style.setVerticalAlignment(VerticalAlignment.CENTER);
        	style.setWrapText(true);
        	style.setBorderBottom(BorderStyle.THIN);
        	style.setBorderLeft(BorderStyle.THIN);
        	style.setBorderRight(BorderStyle.THIN);
        	style.setBorderTop(BorderStyle.THIN);
        	style.setBottomBorderColor((short) 8);
        	style.setLeftBorderColor((short) 8);
        	style.setRightBorderColor((short) 8);
        	style.setTopBorderColor((short) 8);
        	
        	HSSFCellStyle style2 = wb.createCellStyle();
        	style2.setFont(font2);
        	style2.setAlignment(HorizontalAlignment.LEFT);
        	style2.setVerticalAlignment(VerticalAlignment.TOP);
        	style2.setWrapText(true);
//        	style.setBorderBottom(BorderStyle.THIN);
//        	style.setBorderLeft(BorderStyle.THIN);
//        	style.setBorderRight(BorderStyle.THIN);
//        	style.setBorderTop(BorderStyle.THIN);
        	style2.setBottomBorderColor((short) 8);
        	style2.setLeftBorderColor((short) 8);
        	style2.setRightBorderColor((short) 8);
        	style2.setTopBorderColor((short) 8);
        	FileOutputStream fileOut =null;
        	HSSFSheet sheet =null;
			String outputFile = this.userview.getUserName()+ "template_" +this.tabid + ".xls";
			InputStream fileinputStream=null;
			String fileid = "";
			try {
				//获取审批意见指标
		        TemplateTableBo tablebo=new TemplateTableBo(this.conn,this.tabid,this.userview);
		        String optionField=tablebo.getOpinion_field();
				Map nameMap=new HashMap();
				/**功能：创建html首页元素功能说明*/
				createHtmlEleFuncDecription(wb,sheet,style2);
				for(int s=0;s<pagelist.size();s++){
					TemplatePage pagevo = (TemplatePage) pagelist.get(s);
					String pageidstr=pagevo.getPageId()+"";
					int pageid=Integer.parseInt(pageidstr);
					String pageTitle=pagevo.getTitle();
					sheet =wb.createSheet();
					//如果页名重复，在之后依次加1 ，如三个第五页，依次展示为 第五页_2,第五页_3,第五页_4
					if(nameMap.containsKey(pageTitle)){
						String temPageTitle=pageTitle;
						String pageTitleM=(String) nameMap.get(pageTitle);
						int reNum=pageTitleM.lastIndexOf("_");
						if(reNum!=-1){
							pageTitle+="_"+(Integer.parseInt(pageTitleM.substring(reNum+1))+1);
						}else{
							pageTitle+="_2";
						}
						nameMap.put(temPageTitle,pageTitle);
					}else{
						nameMap.put(pageTitle,pageTitle);
					}
					wb.setSheetName(s+1, pageTitle);
					 /** 输出单元格 */
				    ArrayList celllist =getPageCell(pageid);
				    int num=1;//定义每行数量
				    int h=-1;//定义第几行。
				    HSSFRow row =sheet.createRow(0);
				    HSSFCell cell0 = row.createCell((short) 0);
				    sheet.setColumnWidth((short) 0, (short) 0);
				    cell0.setCellValue(new HSSFRichTextString("pageid:"+pageid));
//				    cell0.set
				    for(int i=0;i<celllist.size();i++)
					{
						TemplateSet setBo=(TemplateSet)celllist.get(i);	
						String field_name  = setBo.getField_name();
						String hz  = setBo.getHz();
						 hz=hz.replaceAll("\\{", "");
						 hz=hz.replaceAll("\\}", "");
						 hz=hz.replaceAll("`", "");
						String flag=setBo.getFlag();
						String field_type=setBo.getField_type();
						String tabFldName = setBo.getTableFieldName();
						//过滤掉审批意见指标
						if(StringUtils.isNotEmpty(optionField)&&(optionField+"_2").equalsIgnoreCase(tabFldName)){
							continue;
						}
						if("H".equals(flag))//汉字描述
						{		
							continue;
						}
						else if("A".equals(flag)|| "B".equals(flag)|| "K".equals(flag)) //指标
						{
						    if(((setBo.getHismode()==2||setBo.getHismode()==3||(setBo.getHismode()==4))&&setBo.getChgstate()==1)||(setBo.isSubflag()))
						    {
						    	if(!setBo.isSubflag()){
						    		/**条件1：序号定位| 2：(条件序号&&!最近第&&最初第)&&变化前) 3：子集（subflag=true）**/
						    		if(!setBo.isSubflag()&&((setBo.getHismode()==2 || setBo.getHismode()==4) && (setBo.getMode()==0 
						    				|| setBo.getMode()==2))){
						    			//序号定位 、条件定位的最近第、 最初第因为是一条记录  按普通当前记录显示方式显示 wangrd 20151026
						    			FieldItem fldItem = DataDictionary.getFieldItem(field_name,setBo.getSetname());
						    			if (fldItem!=null && "M".equalsIgnoreCase(fldItem.getItemtype())){
						    				h++;
						    				publicFillStyle(style, sheet, num, row);
						    				row =sheet.createRow(h);
						    				if(h==0){
						    					cell0 = row.createCell((short) 0);
						    					sheet.setColumnWidth((short) 0, (short) 0);
						    					//第一行
						    					cell0.setCellValue(new HSSFRichTextString("pageid:"+pageid));
						    				}
						    				createTextAreaEditor(row,style, sheet,num, h, tabFldName+":"+hz);
						    				num=4;
						    				continue;
						    			}
						    			else {
//				                     createInputEditor(td,setBo);
						    			}
						    		}
						    	}
						      else {//子集记录
						    	  h++;
						    	  publicFillStyle(style, sheet, num, row);
								  row =sheet.createRow(h);
								  if(h==0){
									  cell0 = row.createCell((short) 0);
									  sheet.setColumnWidth((short) 0, (short) 0);
									  //第一行
									  cell0.setCellValue(new HSSFRichTextString("pageid:"+pageid));
								  }
								  // 写表头
								  ExportExcelUtil.mergeCell(sheet, h, (short) 1, h, (short) 3);
								  HSSFCell cell = row.createCell((short) 1);
								  cell.setCellValue(new HSSFRichTextString("collapse:"+hz));
								  cell.setCellStyle(style);
								  row.setHeightInPoints(45);
								  sheet.setColumnWidth((short) 1, (short) 10000);
								  
								  publicFillStyle(style, sheet, 2, row);
								  
								  h++;
						    	  publicFillStyle(style, sheet, num, row);
								  row =sheet.createRow(h);
								  // 写表头
								  ExportExcelUtil.mergeCell(sheet, h, (short) 1, h, (short) 3);
								  cell = row.createCell((short) 1);
								  String setname=setBo.getSetname();
								  cell.setCellValue(new HSSFRichTextString(tabFldName+":"+hz));
								  cell.setCellStyle(style);
								  row.setHeightInPoints(45);
								  sheet.setColumnWidth((short) 1, (short) 10000);
								  
								  publicFillStyle(style, sheet, 2, row);
								  
								  num=4;
								  continue;
						      }
						  }
						  else
						  {
							  if("D".equalsIgnoreCase(field_type))
							  {
//									createInputEditor(td,setBo);			  
							  }
							  else if("N".equalsIgnoreCase(field_type))
							  {
//									createInputEditor(td,setBo);				  
							  }
							  else if("M".equalsIgnoreCase(field_type))
							  {
								  h++;
								  publicFillStyle(style, sheet, num, row);
								  String field_content=tabFldName+":"+hz;
								  row =sheet.createRow(h);
								  if(h==0){
									  cell0 = row.createCell((short) 0);
									  sheet.setColumnWidth((short) 0, (short) 0);
									  //第一行
									  cell0.setCellValue(new HSSFRichTextString("pageid:"+pageid));
								  }
								  row=createTextAreaEditor(row,style, sheet,num, h, field_content);
								  num=4;
								  continue;
							  }
							  else if("A".equalsIgnoreCase(field_type))
							  {
//								createInputEditor(td,setBo);
							  }
						  }
						}
						else if("P".equals(flag)) //picture
						{
							continue;
						}
						else if("F".equals(flag)) //attachment
						{
							h++;
							publicFillStyle(style, sheet, num, row);
							String field_content=tabFldName+":附件";
							row =sheet.createRow(h);
							if(h==0){
								cell0 = row.createCell((short) 0);
								sheet.setColumnWidth((short) 0, (short) 0);
								//第一行
								cell0.setCellValue(new HSSFRichTextString("pageid:"+pageid));
							}
							row=createAttachmentEditor(row, style, sheet,num, h, field_content);
							num=4;
							continue;
						}	
						else if("V".equals(flag))//临时变量
						{
//						    createInputEditor(td,setBo);
						}	
						else if("C".equals(flag))//计算公式 wangrd 2013-12-30
						{            
//						    createInputVarEditor(td,setBo);
				            
						}
						else if("S".equals(flag))//签章 不考虑
						{
//							createSignatureEditor(td,setBo);
							continue;
						}else{
							continue;
						}
						
						if(h==-1){
							h++;
						}
						if(num==4){
							num=1;
							h++;
							row =sheet.createRow(h);
						}
						String field_content=tabFldName+":"+hz;
				    	HSSFCell cell = row.createCell((short) num);
				    	cell.setCellValue(new HSSFRichTextString(field_content));
				    	cell.setCellStyle(style);
				    	row.setHeightInPoints(45);
				    	sheet.setColumnWidth((short) num, (short) 10000);
				    	num++;
					}	
				}
				//获取本地临时文件目录，存放在临时文件目录里。
    			String filepath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile;
				// 数据
				fileOut= new FileOutputStream(filepath);
	            wb.write(fileOut);
	            
	          //针对该临时目录存放到VFS上
    			File file=new File(filepath);
    			if(file.exists()) {
    				fileinputStream = new FileInputStream(file);
    				//VFS保存文件。
    				fileid = VfsService.addFile(this.userview.getUserName(), VfsFiletypeEnum.multimedia,VfsModulesEnum.RS, VfsCategoryEnum.other,"", fileinputStream,outputFile, "", true);
    			}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeIoResource(fileinputStream);
				try {
					if(fileOut!=null){
						fileOut.close();
					}
					if(wb!=null){
						wb.close();
					}
					sheet=null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return fileid;
		}
		/**
		 * 功能：创建html首页元素功能说明
		 * 模板设置简介说明：
如下图所示：
A区：
   a0101_2:姓名：字符串型，展示为文本框，
   a0107_1:性别：代码型，展示相关代码，为下拉列表
   a1025_2:聘任起始时间：日期型，展示日期型
B区：
   collapse:教育背景，收缩分割线,教育背景为前台展示字符，可修改。
   t_a04_2:教育背景，子集，展示为表格
   ivider:  ，分割线
C区：
   describe:任 现 职...,describe描述文本，自定义
   a01aa_1:备注,输入方式为简单编辑器，前台展示为文本框，输入方式为html编辑器，前台展示为富文本编辑器
		 * @param wb
		 * @param sheet 
		 * @param style 
		 * @throws IOException 
		 */
		private void createHtmlEleFuncDecription(HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle style) {
		    //先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray   
		    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		    try {
		    	String descripStr="模板设置说明：\nA区：\n   a0101_2:姓名，字符串型，展示为文本框。\n   a0107_1:性别，代码型，展示相关代码，为下拉列表。\n   a1025_2:聘任起始时间，日期型，展示日期型。\nB区：\n   collapse:教育背景，收缩分割线,教育背景为前台展示字符，可修改。\n   t_a04_2:教育背景，子集，展示为表格。\n   ivider: ，分割线。\nC区：\n   describe:任 现 职...，有样式的纯文本描述，自定义。\n   describe_blank:任 现 职...，去除样式的纯文本描述，自定义。\n   a01aa_1:备注,输入方式为简单编辑器，前台展示为文本框，输入方式为html编辑器，前台展示为富文本编辑器。";
		    	sheet =wb.createSheet();
		    	wb.setSheetName(0, "模板设置说明");
		    	HSSFRow row =sheet.createRow(0);
		    	row.setHeightInPoints(170);
		    	HSSFCell cell = row.createCell((short) 0);
		    	cell.setCellStyle(style);
		    	cell.setCellValue(new HSSFRichTextString(descripStr));
				sheet.setColumnWidth((short) 0, (short) 25000);
				jpg_path=this.getClass().getResource("/").getPath()+".."+File.separator+".."+File.separator+"images"+File.separator+"descript.jpg";
				File jpg=new File(jpg_path);
				if(!jpg.exists()){
					return;
				}
				BufferedImage bufferImg = ImageIO.read(new FileInputStream(jpg));  
				ImageIO.write(bufferImg,"jpg",byteArrayOut); 
				
				HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
				HSSFClientAnchor anchor = new HSSFClientAnchor(0,0,0,255,(short)0,1,(short)12,40);
				anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
				//插入图片   
				patriarch.createPicture(anchor , wb.addPicture(byteArrayOut.toByteArray(),HSSFWorkbook.PICTURE_TYPE_JPEG));
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeIoResource(byteArrayOut);
			}
			
		}

		private void publicFillStyle(HSSFCellStyle style, HSSFSheet sheet,
				int num, HSSFRow row) {
			if(num==2){
				  HSSFCell cell = row.createCell((short) 2);
				  cell.setCellStyle(style);
				  row.setHeightInPoints(45);
				  sheet.setColumnWidth((short) 2, (short) 10000);
				  
				  cell = row.createCell((short) 3);
				  cell.setCellStyle(style);
				  row.setHeightInPoints(45);
				  sheet.setColumnWidth((short) 3, (short) 10000);
				  
				}else if(num==3){
					  HSSFCell cell = row.createCell((short) 3);
					  cell.setCellStyle(style);
					  row.setHeightInPoints(45);
					  sheet.setColumnWidth((short) 3, (short) 10000);
					  
				}
		}
		private HSSFRow createAttachmentEditor(HSSFRow row,HSSFCellStyle style,
				HSSFSheet sheet, int num, int h, String field_content) throws GeneralException {
			// 写表头
			ExportExcelUtil.mergeCell(sheet, h, (short) 1, h, (short) 3);
			HSSFCell cell = row.createCell((short) 1);
			cell.setCellValue(new HSSFRichTextString(field_content));
			cell.setCellStyle(style);
			row.setHeightInPoints(45);
			sheet.setColumnWidth((short) 1, (short) 10000);
			
			publicFillStyle(style, sheet, 2, row);
			return row;
		}
		private HSSFRow createTextAreaEditor(HSSFRow row,HSSFCellStyle style,
				HSSFSheet sheet, int num, int h, String field_content) throws GeneralException {
			// 写表头
			ExportExcelUtil.mergeCell(sheet, h, (short) 1, h, (short) 3);
			HSSFCell cell = row.createCell((short) 1);
			cell.setCellValue(new HSSFRichTextString(field_content));
			cell.setCellStyle(style);
			row.setHeightInPoints(45);
			sheet.setColumnWidth((short) 1, (short) 10000);
			publicFillStyle(style, sheet, 2, row);
			return row;
		}
		/** 
		 * @Title: getPageList 
		 * @Description:  获取模板显示的页签
		 * @param @param isMobile 是否显示异动标签
		 * @param noShowPageNo  不显示那些页签
		 * @param @return
		 * @param @throws Exception
		 * @return ArrayList
		 */
		private ArrayList getPageList(int tabId, boolean isMobile, String noShowPageNo,String taskId) {
			ArrayList outlist = new ArrayList();
			try {
				com.hjsj.hrms.module.template.utils.TemplateUtilBo utilBo = new com.hjsj.hrms.module.template.utils.TemplateUtilBo(this.conn,
						this.userview);
				TemplateCardBo cardBo = new TemplateCardBo(this.conn,this.userview,tabId);
							   cardBo.setTask_id(taskId);
				ArrayList list = utilBo.getAllTemplatePage(tabId);
				for (int i = 0; i < list.size(); i++) {
					TemplatePage pagebo = (TemplatePage) list.get(i);
					if(!"".equals(noShowPageNo)){//如果有设置的不显示页签 优先走这个
						String pageid =  String.valueOf(pagebo.getPageId());
						String[] pagearr = noShowPageNo.split(",");
						boolean noprint = false;
						for(String pid:pagearr){
							if(pid.equalsIgnoreCase(pageid)){
								noprint = true;
								break;
							}
						}
						if(noprint)
							continue;
					}else if (!pagebo.isShow()) {
						continue;
					}
					if (isMobile != pagebo.isMobile()) {
						continue;
					}
//					if(!pagebo.isPrint())//设置此页不打印 不显示此页
//						continue;
					//判断此页的指标无读写权限。无读写权限指标的不显示
					if (!cardBo.isHaveReadFieldPriv(pagebo.getPageId() + "")) {//
						continue;
					}
					outlist.add(pagebo);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return outlist;
		}    
}
