package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.MadeCardCellLine;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

public class TnameHtmlBo {
	private Connection conn=null;
	private ReportResultBo reportResultBo=null;
	private float percent=0.26f;
	private String editupdisk="true";  //是否允许修改子单位报表数据
	private int bottomIndex=0;//底部区域位置
	/** 是否显示纸张背景 */
	private boolean showPaper = true;
	
	public TnameHtmlBo(Connection conn)
	{
		this.conn=conn;
		this.reportResultBo=new ReportResultBo(conn);
		
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		 editupdisk=sysbo.getValue(Sys_Oth_Parameter.EDITUPDISK);
		 if(editupdisk==null||editupdisk.length()==0) {
             editupdisk="true";
         }
	}
	
	
	
	
	
	/**
	 * 生成报表html编辑页面(重构)
	 * @param gridList
	 * @param pageList
	 * @param rowInfoBGrid
	 * @param colInfoBGrid
	 * @param status  =-1，未填  =0,正在编辑  =1,已上报 =2,打回 =3,封存（基层单位的数据不让修改
	 * @param itemGridvo 项目单元格对象
	 * @param minTop_px  报表需整体下移的像素
	 * @param dataArea   数据区对象
	 * @param rowMap     行号对应的实际 2维数组结果的下标值
	 * @param colMap	 列号对应的实际 2维数组结果的下标值
	 * @param operateObject 表对象 1：编辑没上报表 2：编辑上报后的表
	 * @param unitcode
	 * @return
	 */
	public String creatHtmlView2(TnameBo  tnameBo,String userName,String status,int minTop_px,String operateObject,String unitcode,String selfUnitcode)
	{
		HashMap rowBgridMap=new HashMap();
		HashMap colBgridMap=new HashMap();
		
		
		RecordVo vo=null;
		for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
		{
			vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
			rowBgridMap.put(vo.getString("gridno"),String.valueOf(i));				
		}
		for(int i=0;i<tnameBo.getColInfoBGrid().size();i++)
		{
			vo=(RecordVo)tnameBo.getColInfoBGrid().get(i);
			colBgridMap.put(vo.getString("gridno"),String.valueOf(i));
		}
		bottomIndex=tnameBo.getBottomHeight();
		StringBuffer html=new StringBuffer("");	
		String tableHeader=createTableHeader(tnameBo.getGridList(),tnameBo.getItemGridArea(),minTop_px,rowBgridMap,colBgridMap,operateObject);		
	//	String tableDataArea=createData(tabid,userName,rowInfoBGrid,colInfoBGrid,status,dataArea,rowSerialNo,colSerialNo,tnameVo,rowMap,colMap,operateObject,unitcode,selfUnitcode);
		String tableDataArea=createData2(tnameBo,userName,status,operateObject,unitcode,selfUnitcode);
		int[] itemGridArea=tnameBo.getItemGridArea();
		int left=itemGridArea[0];	
		//liuy 2015-2-11 7406: 中亚时代 ：编辑报表界面报表打不开报空指针 start
		//int width=itemGridArea[0]+itemGridArea[2]+tnameBo.getDataArea().getInt("rwidth");
		int width=itemGridArea[0]+itemGridArea[2];
		if(tnameBo.getDataArea()!=null) {
            width = width + tnameBo.getDataArea().getInt("rwidth");
        }
		String tableTitle=createTitle(tnameBo.getPageList(),tnameBo.getParamMap(),tnameBo.getDataArea(),userName,minTop_px+6,status,operateObject,unitcode,selfUnitcode,width);
		int buttonTop=30+3;
		float paperw=(tnameBo.getTnameVo().getInt("paperori")==1?tnameBo.getTnameVo().getInt("paperw"):tnameBo.getTnameVo().getInt("paperh"))/percent;
		float paperh=(tnameBo.getTnameVo().getInt("paperori")==1?tnameBo.getTnameVo().getInt("paperh"):tnameBo.getTnameVo().getInt("paperw"))/percent;
		//float a_width=tnameBo.getDataArea().getInt("rleft")+tnameBo.getDataArea().getInt("rwidth");
		//float a_height=tnameBo.getDataArea().getInt("rtop")+tnameBo.getDataArea().getInt("rheight");
		float a_width=0;
		float a_height=0;
		if(tnameBo.getDataArea()!=null){			
			a_width=tnameBo.getDataArea().getInt("rleft")+tnameBo.getDataArea().getInt("rwidth");
			a_height=tnameBo.getDataArea().getInt("rtop")+tnameBo.getDataArea().getInt("rheight");
		}
		//liuy 2015-2-11 end
		//System.out.print(html.toString());
		html.append(executeAbsoluteBackground (minTop_px,10,paperw>a_width?paperw:a_width+20,paperh>a_height?paperh:a_height+30));
        
		html.append(tableHeader);
		html.append(tableDataArea);	
		html.append(tableTitle);
		return html.toString();
	}
	
	
	
	
	
	
	/**
	 * 生成报表html编辑页面
	 * @param gridList
	 * @param pageList
	 * @param rowInfoBGrid
	 * @param colInfoBGrid
	 * @param status  =-1，未填  =0,正在编辑  =1,已上报 =2,打回 =3,封存（基层单位的数据不让修改
	 * @param itemGridvo 项目单元格对象
	 * @param minTop_px  报表需整体下移的像素
	 * @param dataArea   数据区对象
	 * @param rowMap     行号对应的实际 2维数组结果的下标值
	 * @param colMap	 列号对应的实际 2维数组结果的下标值
	 * @param operateObject 表对象 1：编辑没上报表 2：编辑上报后的表
	 * @param unitcode
	 * @return
	 */
	public String creatHtmlView(String tabid,String userName,ArrayList gridList,ArrayList pageList,ArrayList rowInfoBGrid,ArrayList colInfoBGrid,int[] itemGridArea,String status,RecordVo dataArea,String rowSerialNo,String colSerialNo,RecordVo tnameVo,HashMap paramMap,HashMap rowMap,HashMap colMap,int minTop_px,String operateObject,String unitcode,String selfUnitcode)
	{
		HashMap rowBgridMap=new HashMap();
		HashMap colBgridMap=new HashMap();
		
		
		RecordVo vo=null;
		for(int i=0;i<rowInfoBGrid.size();i++)
		{
			vo=(RecordVo)rowInfoBGrid.get(i);
			rowBgridMap.put(vo.getString("gridno"),String.valueOf(i));				
		}
		for(int i=0;i<colInfoBGrid.size();i++)
		{
			vo=(RecordVo)colInfoBGrid.get(i);
			colBgridMap.put(vo.getString("gridno"),String.valueOf(i));
		}
		
		
		StringBuffer html=new StringBuffer("");	
		String tableHeader=createTableHeader(gridList,itemGridArea,minTop_px,rowBgridMap,colBgridMap,operateObject);		
		String tableDataArea=createData(tabid,userName,rowInfoBGrid,colInfoBGrid,status,dataArea,rowSerialNo,colSerialNo,tnameVo,rowMap,colMap,operateObject,unitcode,selfUnitcode);
		int width=itemGridArea[0]+itemGridArea[2]+dataArea.getInt("rwidth");
		int left=itemGridArea[0];		
		int buttonTop=30+3;
		String tableTitle=createTitle(pageList,paramMap,dataArea,userName,minTop_px,status,operateObject,unitcode,selfUnitcode,width);
		//System.out.println(tnameVo.getInt("paperw")/percent+"    "+tnameVo.getInt("paperh")/percent);
		float paperw=(tnameVo.getInt("paperori")==1?tnameVo.getInt("paperw"):tnameVo.getInt("paperh"))/percent;
		float paperh=(tnameVo.getInt("paperori")==1?tnameVo.getInt("paperh"):tnameVo.getInt("paperw"))/percent;
		float a_width=dataArea.getInt("rleft")+dataArea.getInt("rwidth");
		float a_height=dataArea.getInt("rtop")+dataArea.getInt("rheight");
		//html.append(executeAbsoluteBackground (minTop_px,10,tnameVo.getInt("paperw")/percent>a_width?tnameVo.getInt("paperw")/percent:a_width+20,tnameVo.getInt("paperh")/percent>a_height?tnameVo.getInt("paperh")/percent:a_height+30));
		//html.append(executeAbsoluteBackground (minTop_px,10,paperw>a_width?paperw:a_width+20,paperh>a_height?paperh:a_height+30));
        
		html.append(executeAbsoluteBackground1 (minTop_px,10,paperw>a_width?paperw:a_width+20,paperh>a_height?paperh:a_height+30));
        
		html.append(tableHeader);
		html.append(tableDataArea);	
		html.append(tableTitle);
		html.append(executeAbsoluteBackground2 (minTop_px,10,paperw>a_width?paperw:a_width+20,paperh>a_height?paperh:a_height+30));
        
		return html.toString();
	}
	
	
	

	
	/**
	 * @param flag  1:有边线 0：无边
	 * @param context1
	 * @param context2
	 * @param a_width
	 * @param aValign
	 * @param aAlign
	 * @return
	 */
	public String getContext(int flag,String context1,String context2,float a_width,String aValign,String aAlign)
	{
		StringBuffer tempTable=new StringBuffer("");
		tempTable.append(" <td ");
		if(flag==1) {
            tempTable.append(" class='RecordRow_self' ");
        }
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		if(a_width>1) {
            tempTable.append("' width='"+a_width+"' > \n ");
        } else {
            tempTable.append("' width='"+a_width*100+"%' > \n");
        }
		int aFontSize=9;		
	    String style=getFontStyle("1",aFontSize);
		tempTable.append(" <font face='"+ResourceFactory.getProperty("hmuster.label.fontSt")+"' style='");
//		tempTable.append(aFontSize);
//		tempTable.append("pt' > \n ");
		tempTable.append(style);
		tempTable.append(";' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
//		tempTable.append(getFontStyle2(1,"1"));
		tempTable.append(context1);	
//		tempTable.append(getFontStyle2(0,"1"));
		
		tempTable.append("</font>&nbsp;");
		tempTable.append(context2);
		tempTable.append("</td>");
		return tempTable.toString();
	}
	
	

	
	
	
	/**
	 * 生成表头和项目栏
	 * @param gridList   单元格集合
	 * @param itemGridNo 项目单元格id号
	 * @return
	 */
	public String createTableHeader(ArrayList gridList,int[] itemGridArea,int minTop_px,HashMap rowBgridMap,HashMap colBgridMap,String operateObject)
	{
		StringBuffer htmlHeader=new StringBuffer("");
		MadeCardCellLine madeCardCellLine=new MadeCardCellLine();
		RecordVo vo=null;
		for(Iterator t=gridList.iterator();t.hasNext();)
		{
			vo=(RecordVo)t.next();
            /*
             * 通过css样式去控制
             * if(bottomIndex!=0) {//单元格位于底部位置时 底部边框置为空
             * if(bottomIndex==(vo.getInt("rtop")+vo.getInt("rheight"))) {
             * if(vo.getInt("b")==1) { vo.setInt("b", 0); } } }
             */
			vo.setInt("rtop",vo.getInt("rtop")+minTop_px);			//集体上移
			String context="&nbsp;";	
			//			处理虚线			L,T,R,B,
		    String style_name=madeCardCellLine.GetReportCellLineShowcss(String.valueOf(vo.getInt("l")),String.valueOf(vo.getInt("r")),String.valueOf(vo.getInt("t")),String.valueOf(vo.getInt("b")));	
			if(vo.getString("hz")!=null&&vo.getString("hz").indexOf("`")!=-1)
			{
				context=vo.getString("hz").replaceAll("`","<Br>");	
			}
			if(vo.getInt("flag")!=3)
			{
				autoEditBorder(itemGridArea,vo);			//自动修改单元格的边线位置，使其不会出现重叠效果
				context = ""+context+"";            //解决换行后行间距过大导致显示不全被遮挡的问题 //解决提取数据chrome浏览器项目名称显示不出来问题
				htmlHeader.append(executeAbsoluteTable2(vo.getInt("align"),vo.getString("fontname"),String.valueOf(vo.getInt("fontsize")),String.valueOf(vo.getInt("fonteffect")),"1",String.valueOf(vo.getInt("rtop")),String.valueOf(vo.getInt("rleft")),String.valueOf(vo.getInt("rwidth")),String.valueOf(vo.getInt("rheight")),context,style_name,vo,rowBgridMap,colBgridMap,operateObject));
			
		   }
		}
		return htmlHeader.toString();
	}
	
	
	/**
	 * 将表间计算公式中左表达式中涉及到的号码映射成实际得数据结果集中的下标值
	 * @param list
	 * @param map
	 * @return
	 */
	public String transformDeedIndex2(HashSet set,HashMap rowMap,HashMap colMap)
	{
		StringBuffer lexpr=new StringBuffer("");
		for(Iterator t=set.iterator();t.hasNext();)
		{
			boolean flag=true;
			
			StringBuffer ss=new StringBuffer("[");
			String temp=(String)t.next();
			String temp1[]=temp.split(":");
			String num=(String)rowMap.get(temp1[0]);
			if(num!=null)
			{
				ss.append(num);	
				if("all".equalsIgnoreCase(temp1[1]))
				{
					ss.append(":all,");
				}
				else
				{
					if(temp1[1].indexOf(",")==-1)
					{
						if(colMap.get(temp1[1])!=null)
						{
							ss.append(":"+(String)colMap.get(temp1[1])+",");
						}
						else {
                            flag=false;
                        }
					}
					else
					{
						String temp2[]=temp1[1].split(",");
						ss.append(":");
						for(int i=0;i<temp2.length;i++)
						{
							if(colMap.get(temp2[i])!=null)
							{
								ss.append((String)colMap.get(temp2[i])+",");
							}
							else {
                                flag=false;
                            }
						}
					}
				}
			}
			else {
                flag=false;
            }
			ss.append("];");
			
			if(!flag) {
                ss.setLength(0);
            }
			lexpr.append(ss.toString());
		}
		String lexp="";
		if(lexpr.length()>0) {
            lexp=lexpr.toString().substring(0,lexpr.length()-1);
        }
		return lexp;
	}
	
/*	public String transformDeedIndex2(HashSet set,HashMap map)
	{
		StringBuffer lexpr=new StringBuffer("");
		for(Iterator t=set.iterator();t.hasNext();)
		{
			boolean flag=true;
			
			StringBuffer ss=new StringBuffer("[");
			String temp=(String)t.next();
			String temp1[]=temp.split(":");
			String num=(String)map.get(temp1[0]);
			if(num!=null)
			{
				ss.append(num);	
				if(temp1[1].equalsIgnoreCase("all"))
				{
					ss.append(":all,");
				}
				else
				{
					if(temp1[1].indexOf(",")==-1)
					{
						if(map.get(temp1[1])!=null)
						{
							ss.append(":"+(String)map.get(temp1[1])+",");
						}
						else
							flag=false;
					}
					else
					{
						String temp2[]=temp1[1].split(",");
						ss.append(":");
						for(int i=0;i<temp2.length;i++)
						{
							if(map.get(temp2[i])!=null)
							{
								ss.append((String)map.get(temp2[i])+",");
							}
							else
								flag=false;
						}
					}
				}
			}
			else
				flag=false;
			ss.append("];");
			
			if(!flag)
				ss.setLength(0);
			lexpr.append(ss.toString());
		}
		String lexp="";
		if(lexpr.length()>0)
			lexp=lexpr.toString().substring(0,lexpr.length()-1);
		return lexp;
	}
	*/
	
	/**
	 * 将表内计算公式中左表达式中涉及到的号码映射成实际得数据结果集中的下标值
	 * @param list
	 * @param map
	 * @return
	 */
	public String transformDeedIndex(HashSet set,HashMap map)
	{
		StringBuffer lexpr=new StringBuffer("");
		for(Iterator t=set.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			String num=(String)map.get(temp);
			if(num!=null) {
                lexpr.append(","+num);
            }
		}
		lexpr.append(",");
		return lexpr.toString();
	}
	
	public String transformDeedIndex(HashSet set)
	{
		StringBuffer lexpr=new StringBuffer("");
		for(Iterator t=set.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			lexpr.append(","+temp);	
		}
		return lexpr.toString();
	}
	
	
	
	
	/**
	 *计算单元格是否在计算公式左表达式范围内，如果在，则设置单元格颜色 
	 * @param i	行号
	 * @param j	列号
	 * @param rowDeedIndex
	 * @param colDeedIndex
	 * @return
	 */
	public String getBackColor(int i,int j,String rowDeedIndex,String colDeedIndex)
	{
		String color="";
		if(rowDeedIndex.indexOf(","+i+",")!=-1) {
            color="#7ED6AC";
        } else
		{
			if(colDeedIndex.indexOf(","+j+",")!=-1) {
                color="#7ED6AC";
            }
		}
		return color;
	}
	
	public String getBackColor2(int i,int j,String rowDeedIndex,String colDeedIndex)
	{
		String color="";
		boolean flag=false;
		String[] row_str=null;
		String[] col_str=null;
		if(rowDeedIndex.trim().length()>0)
		{
			if(rowDeedIndex.indexOf(";")==-1)
			{
				row_str=new String[1];
				row_str[0]=rowDeedIndex;
			}
			else {
                row_str=rowDeedIndex.split(";");
            }
		}
		////////////////////////////////////////		
		if(colDeedIndex.trim().length()>0)
		{
			if(colDeedIndex.indexOf(";")==-1)
			{
				col_str=new String[1];
				col_str[0]=colDeedIndex;
			}
			else {
                col_str=colDeedIndex.split(";");
            }
		}
		if(row_str!=null)
		{
			for(int n=0;n<row_str.length;n++)
			{
				String ss=row_str[n].substring(1,row_str[n].length()-1);
				String[] temp=ss.split(":");
				if(String.valueOf(i).equals(temp[0]))
				{
					if("all,".equals(temp[1]))
					{
						flag=true;
						break;
					}
					else
					{
						if((","+temp[1]).indexOf(","+j+",")!=-1)
						{
							flag=true;
							break;
						}
					}
				}
			}
		}
		if(!flag&&col_str!=null)
		{
			for(int n=0;n<col_str.length;n++)
			{
				String ss=col_str[n].substring(1,col_str[n].length()-1);
				String[] temp=ss.split(":");
				if("all,".equals(temp[1]))
				{
					if(String.valueOf(j).equals(temp[0]))
					{
						flag=true;
						break;
					}
				}
				else
				{
					if(String.valueOf(j).equals(temp[0]))
					{
						
						if((","+temp[1]).indexOf(","+i+",")!=-1)
						{
								flag=true;
								break;
						}
					}
					
				}
			}
		}
		if(flag) {
            color="#66ccff";
        }
		return color;
	}
	
	
	/**
	 * 判断该单元格是否需要自动计算
	 * @return
	 */
	public boolean getAccount(int i,int j,String rowChangeIndex,String colChangeIndex)
	{
		boolean flag=false;
		if(rowChangeIndex.indexOf(","+i)!=-1) {
            flag=true;
        } else
		{
			if(colChangeIndex.indexOf(","+j)!=-1) {
                flag=true;
            }
		}
		return flag;
	}
	
	
	
	
	
	/**
	 * get数据区html
	 * @param rowInfoBGrid
	 * @param colInfoBGrid
	 * @param itemGridvo
	 * @param status
	 * @param minTop_px
	 * @param dataArea
	 * @param rowSerialNo 横表栏序号所在位置
	 * @param colSerialNo 纵表栏序号所在位置
	 * @return
	 */
	public String createData2(TnameBo  tnameBo,String userName,String status,String operateObject,String unitcode,String selfUnitcode)
	{
		StringBuffer html=new StringBuffer("");
		try
		{
			ArrayList  resultList=new ArrayList();
			TgridBo tgridBo=new TgridBo(this.conn);
			reportResultBo.setColinfolist(tnameBo.getColInfoList());
			if("1".equals(operateObject))
			{
				//TnameBo tnameBo=new TnameBo(this.conn);
				tnameBo.isExistTable(tnameBo.getTabid(),tnameBo.getRowInfoBGrid().size());
				resultList=reportResultBo.getTBxxResultList(tnameBo.getTabid(),userName);
			}
			else
			{
				
				tgridBo.execute_TT_table(tnameBo.getTabid(),tnameBo.getRowInfoBGrid().size());
				resultList=reportResultBo.getTTxxResultList(tnameBo.getTabid(),unitcode);
			}
			TformulaBo tformulaBo=new TformulaBo(this.conn);
			ArrayList a_rowFormulaList=tformulaBo.getFormula(tnameBo.getTabid(),"0");
			ArrayList a_colFormulaList=tformulaBo.getFormula(tnameBo.getTabid(),"1");
			ArrayList a_between_rowFormulaList=tformulaBo.getFormula(tnameBo.getTabid(),"7");//表间行和表间格
			ArrayList a_between_colFormulaList=tformulaBo.getFormula(tnameBo.getTabid(),"3");//表间列

			HashSet  rowFormulaList=tformulaBo.getLexprNum(a_rowFormulaList);   //表内行计算公式集合
		    HashSet  colFormulaList=tformulaBo.getLexprNum(a_colFormulaList);   //表内列计算公式集合
		    
		    HashSet  between_rowFormulaList=tformulaBo.getLexprNum(a_between_rowFormulaList);   //表间行计算公式集合
		    HashSet  between_colFormulaList=tformulaBo.getLexprNum(a_between_colFormulaList);   //表间列计算公式集合
		    
		    //String formulaType = 
		    String     rowDeedIndex=transformDeedIndex(rowFormulaList,tnameBo.getRowMap());
		    String     colDeedIndex=transformDeedIndex(colFormulaList,tnameBo.getColMap());
		    
		    String     between_rowDeedIndex=transformDeedIndex2(between_rowFormulaList,tnameBo.getRowMap(),tnameBo.getColMap());
		    String     between_colDeedIndex=transformDeedIndex2(between_colFormulaList,tnameBo.getColMap(),tnameBo.getColMap());
			//////////////////////////////////////////////////
			HashSet   rowAllFormulaSet=tformulaBo.getIndexStr(a_rowFormulaList,tnameBo.getRowMap());
			HashSet   colAllFormulaSet=tformulaBo.getIndexStr(a_colFormulaList,tnameBo.getColMap());
			String    rowChangeIndex=transformDeedIndex(rowAllFormulaSet);  //需自动计算的行信息
			String    colChangeIndex=transformDeedIndex(colAllFormulaSet);  //需自动计算的列信息
		
		    //
			String fontName=tnameBo.getTnameVo().getString("fontname");
			int fontSize=tnameBo.getTnameVo().getInt("fontsize");
			int fontStyle=tnameBo.getTnameVo().getInt("fonteffect");	
			int colNum=0;
			int rowNum=0;
			String comp="";
			int fromIndex=0;
			int toIndex=0;
			ArrayList tableTermlist =tnameBo.getTableTermList();
			String[] tableTerm = (String[])tableTermlist.get(0);
			String tempflag =tgridBo.getCexpr2Context(9,tableTerm[5]);
			boolean tabletermflag = false;
			if("True".equalsIgnoreCase(tempflag)){
				tabletermflag =true;
			}
			boolean dmlflag=this.isappeal(selfUnitcode, tnameBo.getTabid());
			String  datevalue = tnameBo.getOwnerDate(tnameBo.getTabid());
			if(resultList.size()>0)
			{
				for(int i=0;i<resultList.size();i++)
				{
					rowNum=0;
					String[] rowInfo=(String[])resultList.get(i);
					if(tnameBo.getColInfoBGrid().size()==0) {
                        continue;
                    }
					RecordVo colVo=(RecordVo)tnameBo.getColInfoBGrid().get(i);
					if(colVo.getInt("flag1")!=4) {
                        colNum++;
                    }
				
					boolean readycolflag = false;
						String tablecol= colVo.getString("cexpr2");	
						ArrayList colinfolist =tnameBo.getColInfoList();
							ArrayList colTermList=(ArrayList)colinfolist.get(i);
							for(Iterator t=colTermList.iterator();t.hasNext();)
							{

								String[] temp=(String[])t.next();
								if(temp[5].trim().length()>0){
									if(temp[5].toUpperCase().indexOf("<ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("<ROWDATETYPE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATETYPE>")!=-1)
									{
										 fromIndex=temp[5].toUpperCase().indexOf("<ROWDATE>");
									 toIndex=temp[5].toUpperCase().indexOf("</ROWDATE>");
									String te1 =temp[5].toUpperCase().substring(fromIndex+9,toIndex).trim();
									 fromIndex=temp[5].toUpperCase().indexOf("<ROWDATETYPE>");
									 toIndex=temp[5].toUpperCase().indexOf("</ROWDATETYPE>");
									String te2 =temp[5].toUpperCase().substring(fromIndex+13,toIndex).trim();
									
										String value=datevalue;
										int app = 0;
//										int start = 0;
										if (value != null && value.length() > 7) {
                                            app = Integer.parseInt(value.substring(5, 7));
                                        }
										if (te2.indexOf("M")!=-1&&app != 0) {
											if(te1.equals(""+app)){
												readycolflag=false;
												
											}else{
												readycolflag=true;
												break;
												
											}
										}
										if(te2.indexOf("Q")!=-1&&app != 0){
											if(0<app&&app<4) {
                                                comp="1";
                                            }
											if(3<app&&app<7) {
                                                comp="2";
                                            }
											if(6<app&&app<10) {
                                                comp="3";
                                            }
											if(9<app&&app<13) {
                                                comp="4";
                                            }
											if(te1.equals(""+comp)){
												readycolflag=false;
												
											}else{
												readycolflag=true;
												break;
												
											}
										}	
										if(te2.indexOf("Y")!=-1&&app != 0){
											if(0<app&&app<7) {
                                                comp="1";
                                            }
											if(6<app&&app<13) {
                                                comp="2";
                                            }
											if(te1.equals(""+comp)){
												readycolflag=false;
												
											}else{
												readycolflag=true;
												break;
												
											}
										}
									
								}
								}
							
							}
						
					
					String tempcol =	tgridBo.getCexpr2Context(9,tablecol);
					if("True".equalsIgnoreCase(tempcol)){
						readycolflag=true;
					}
					for(int j=0;j<rowInfo.length;j++)
					{
						String context="";
						int flag=0;												
					
						String color=getBackColor(i,j,rowDeedIndex,colDeedIndex);
					
						String color2=getBackColor2(i,j,between_rowDeedIndex,between_colDeedIndex);
						if(color2.trim().length()>0) {
                            color=color2;
                        }
						boolean isAutoAccount=getAccount(i,j,rowChangeIndex,colChangeIndex);
						if(SystemConfig.getPropertyValue("reportAutoCompute")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("reportAutoCompute"))) //20141118 dengcan  客户定义的计算公式太多，造成录入时严重影响性能，可通过参数去掉
                        {
                            isAutoAccount=false;
                        }
						
						RecordVo rowVo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
//						if(colVo.getString("archive_item").equals("A0000301")&&rowVo.getString("archive_item").equals("A0000326")){
//							System.out.println("aa");
//						}
						boolean readyrowflag = false;
						String tablerow= rowVo.getString("cexpr2");	
					String temprow =	tgridBo.getCexpr2Context(9,tablerow);
					if("True".equalsIgnoreCase(temprow)){
						readyrowflag=true;
					}
					
						int r=rowVo.getInt("r");
						int npercent=0;
//						if(rowVo.getInt("flag1")==2&&colVo.getInt("flag1")==2)  //统计个数
//							npercent=0;
//						else 
//						if(rowVo.getInt("flag1")==3)
//							npercent=rowVo.getInt("npercent");
//						else if(colVo.getInt("flag1")==3)
//							npercent=colVo.getInt("npercent");
//						else
							npercent=rowVo.getInt("npercent")>=colVo.getInt("npercent")?rowVo.getInt("npercent"):colVo.getInt("npercent");
						
						//平均人数 小数位  xgq不需要特殊处理，因为这里设置了小数位直接影响npercet的值 2011.03.28
//						if(rowVo.getInt("flag2")==5&&rowVo.getInt("flag1")==1&&rowVo.getString("cexpr2").length()>0)
//						{
//							String[] temp=rowVo.getString("cexpr2").substring(rowVo.getString("cexpr2").indexOf("(")+1,rowVo.getString("cexpr2").indexOf(")")).split(";");
//							npercent=0;
//							if(temp.length==3&&Integer.parseInt(temp[2].trim())>0)
//								npercent=Integer.parseInt(temp[2].trim());
//						}
//						if(colVo.getInt("flag2")==5&&colVo.getInt("flag1")==1)
//						{
//							String[] temp=colVo.getString("cexpr2").substring(colVo.getString("cexpr2").indexOf("(")+1,colVo.getString("cexpr2").indexOf(")")).split(";");
//							npercent=0;
//							if(temp.length==3&&Integer.parseInt(temp[2].trim())>0)
//								npercent=Integer.parseInt(temp[2].trim());
//						}
						
						String top=String.valueOf(colVo.getInt("rtop"));
						String left=String.valueOf(rowVo.getInt("rleft"));
						String width=String.valueOf(rowVo.getInt("rwidth"));
						String height=String.valueOf(colVo.getInt("rheight"));

						int flag1 = rowVo.getInt("flag1");
						int flag2 = colVo.getInt("flag1");
//						if(colVo.getInt("l")==0&&colVo.getInt("t")==0&&colVo.getInt("r")==0&&colVo.getInt("b")==0&&rowVo.getInt("l")==0&&rowVo.getInt("t")==0&&rowVo.getInt("r")==0&&rowVo.getInt("b")==0)//zhaoxg
//							continue;
							
						if(rowVo.getInt("flag1")!=4) {
                            rowNum++;
                        }
						if(colVo.getInt("flag1")==4&&rowVo.getInt("flag1")==4)
						{
							context="";
						}
						else if(colVo.getInt("flag1")==4)
						{
							context=String.valueOf(rowNum);
						}
						else if(rowVo.getInt("flag1")==4)
						{
							context=String.valueOf(colNum);
						}
						else
						{
							
							if(rowInfo[j]==null||Float.parseFloat(rowInfo[j])==0) {
                                context="";
                            } else {
                                context=PubFunc.round(rowInfo[j],npercent);
                            }
							
							flag=1;
							if(colVo.getInt("l")==0&&colVo.getInt("t")==0&&colVo.getInt("r")==0&&colVo.getInt("b")==0&&rowVo.getInt("l")==0&&rowVo.getInt("t")==0&&rowVo.getInt("r")==0&&rowVo.getInt("b")==0)//zhaoxg
                            {
                                flag=2;
                            }
						}
						if("2".equals(operateObject)){
							if(dmlflag&&("1".equals(status)|| "4".equals(status))) {
                                html.append(executeAbsoluteTable_data(flag,flag1,flag2,top,left,width,height,context,i,j,"1",fontSize,fontStyle,fontName,npercent,color,isAutoAccount,operateObject,unitcode,selfUnitcode,r,tabletermflag,readycolflag,readyrowflag));
                            } else {
                                html.append(executeAbsoluteTable_data(flag,flag1,flag2,top,left,width,height,context,i,j,"0",fontSize,fontStyle,fontName,npercent,color,isAutoAccount,operateObject,unitcode,selfUnitcode,r,tabletermflag,readycolflag,readyrowflag));
                            }
						}else{
							html.append(executeAbsoluteTable_data(flag,flag1,flag2,top,left,width,height,context,i,j,status,fontSize,fontStyle,fontName,npercent,color,isAutoAccount,operateObject,unitcode,selfUnitcode,r,tabletermflag,readycolflag,readyrowflag));
						}
					}
				}
			}
			else
			{
				for(int i=0;i<tnameBo.getColInfoBGrid().size();i++)
				{
					rowNum=0;
					RecordVo colVo=(RecordVo)tnameBo.getColInfoBGrid().get(i);
					if(colVo.getInt("flag1")!=4) {
                        colNum++;
                    }
					boolean readycolflag = false;
					String tablecol= colVo.getString("cexpr2");
					ArrayList colinfolist =tnameBo.getColInfoList();
					ArrayList colTermList=(ArrayList)colinfolist.get(i);
					for(Iterator t=colTermList.iterator();t.hasNext();)
					{
						String[] temp=(String[])t.next();
						if(temp[5].trim().length()>0){
							if(temp[5].toUpperCase().indexOf("<ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("<ROWDATETYPE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATETYPE>")!=-1)
							{
								 fromIndex=temp[5].toUpperCase().indexOf("<ROWDATE>");
							 toIndex=temp[5].toUpperCase().indexOf("</ROWDATE>");
							String te1 =temp[5].toUpperCase().substring(fromIndex+9,toIndex).trim();
							 fromIndex=temp[5].toUpperCase().indexOf("<ROWDATETYPE>");
							 toIndex=temp[5].toUpperCase().indexOf("</ROWDATETYPE>");
							String te2 =temp[5].toUpperCase().substring(fromIndex+13,toIndex).trim();
								String value=datevalue;
								int app = 0;
//								int start = 0;
								if (value != null && value.length() > 7) {
                                    app = Integer.parseInt(value.substring(5, 7));
                                }
								if (te2.indexOf("M")!=-1&&app != 0) {
									if(te1.equals(""+app)){
										readycolflag=false;
										
									}else{
										readycolflag=true;
										break;
										
									}
								}
								if(te2.indexOf("Q")!=-1&&app != 0){
									if(0<app&&app<4) {
                                        comp="1";
                                    }
									if(3<app&&app<7) {
                                        comp="2";
                                    }
									if(6<app&&app<10) {
                                        comp="3";
                                    }
									if(9<app&&app<13) {
                                        comp="4";
                                    }
									if(te1.equals(""+comp)){
										readycolflag=false;
										
									}else{
										readycolflag=true;
										break;
										
									}
								}	
								if(te2.indexOf("Y")!=-1&&app != 0){
									if(0<app&&app<7) {
                                        comp="1";
                                    }
									if(6<app&&app<13) {
                                        comp="2";
                                    }
									if(te1.equals(""+comp)){
										readycolflag=false;
										
									}else{
										readycolflag=true;
										break;
										
									}
								}
						}
						}
					
					}
				String tempcol =	tgridBo.getCexpr2Context(9,tablecol);
				if("True".equalsIgnoreCase(tempcol)){
					readycolflag=true;
				}
					
					for(int j=0;j<tnameBo.getRowInfoBGrid().size();j++)
					{
					
						
						String context="";
						int flag=0;

					    String color=getBackColor(i,j,rowDeedIndex,colDeedIndex);
					    String  color2=getBackColor2(i,j,between_rowDeedIndex,between_colDeedIndex);
						if(color2.trim().length()>0) {
                            color=color2;
                        }
							
						boolean isAutoAccount=getAccount(i,j,rowChangeIndex,colChangeIndex);
						if(SystemConfig.getPropertyValue("reportAutoCompute")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("reportAutoCompute"))) //20141118 dengcan  客户定义的计算公式太多，造成录入时严重影响性能，可通过参数去掉
                        {
                            isAutoAccount=false;
                        }
						
						RecordVo rowVo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
						boolean readyrowflag = false;
						String tablerow= rowVo.getString("cexpr2");	
					String temprow =	tgridBo.getCexpr2Context(9,tablerow);
					if("True".equalsIgnoreCase(temprow)){
						readyrowflag=true;
					}
						int r=rowVo.getInt("r");
						int npercent=rowVo.getInt("npercent")>=colVo.getInt("npercent")?rowVo.getInt("npercent"):colVo.getInt("npercent");
						String top=String.valueOf(colVo.getInt("rtop"));
						String left=String.valueOf(rowVo.getInt("rleft"));
						String width=String.valueOf(rowVo.getInt("rwidth"));
						String height=String.valueOf(colVo.getInt("rheight"));
						int flag1 = rowVo.getInt("flag1");
						int flag2 = colVo.getInt("flag1");
						if(rowVo.getInt("flag1")!=4) {
                            rowNum++;
                        }
						if(colVo.getInt("flag1")==4&&rowVo.getInt("flag1")==4)
						{
							context="";
						}
						else if(colVo.getInt("flag1")==4)
						{
							context=String.valueOf(rowNum);
						}
						else if(rowVo.getInt("flag1")==4)
						{
							context=String.valueOf(colNum);
						}
						else
						{
							flag=1;
						}	
						html.append(executeAbsoluteTable_data(flag,flag1,flag2,top,left,width,height,context,i,j,status,fontSize,fontStyle,fontName,npercent,color,isAutoAccount,operateObject,unitcode,selfUnitcode,r,tabletermflag,readycolflag,readyrowflag));
					}
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * get数据区html
	 * @param rowInfoBGrid
	 * @param colInfoBGrid
	 * @param itemGridvo
	 * @param status
	 * @param minTop_px
	 * @param dataArea
	 * @param rowSerialNo 横表栏序号所在位置
	 * @param colSerialNo 纵表栏序号所在位置
	 * @return
	 */
	public String createData(String tabid,String userName,ArrayList rowInfoBGrid,ArrayList colInfoBGrid,String status,RecordVo dataArea,String rowSerialNo,String colSerialNo,RecordVo tnameVo,HashMap rowMap,HashMap colMap,String operateObject,String unitcode,String selfUnitcode)
	{
		StringBuffer html=new StringBuffer("");
		TnameBo tnameBo=new TnameBo(this.conn);
		TgridBo tgridBo=new TgridBo(this.conn);
		try
		{
			ArrayList  resultList=new ArrayList();
			if("1".equals(operateObject))
			{
				
				tnameBo.isExistTable(tabid,rowInfoBGrid.size());
				resultList=reportResultBo.getTBxxResultList(tabid,userName);
			}
			else
			{
				
				tgridBo.execute_TT_table(tabid,rowInfoBGrid.size());
				resultList=reportResultBo.getTTxxResultList(tabid,unitcode);
			}
			TformulaBo tformulaBo=new TformulaBo(this.conn);
			ArrayList a_rowFormulaList=tformulaBo.getFormula(tabid,"0");
			ArrayList a_colFormulaList=tformulaBo.getFormula(tabid,"1");
			HashSet  rowFormulaList=tformulaBo.getLexprNum(a_rowFormulaList);   //表内行计算公式集合
		    HashSet  colFormulaList=tformulaBo.getLexprNum(a_colFormulaList);   //表内列计算公式集合
		    String     rowDeedIndex=transformDeedIndex(rowFormulaList,rowMap);
		    String     colDeedIndex=transformDeedIndex(colFormulaList,colMap);
			//////////////////////////////////////////////////
			HashSet   rowAllFormulaSet=tformulaBo.getIndexStr(a_rowFormulaList,rowMap);
			HashSet   colAllFormulaSet=tformulaBo.getIndexStr(a_colFormulaList,colMap);
			String    rowChangeIndex=transformDeedIndex(rowAllFormulaSet);  //需自动计算的行信息
			String    colChangeIndex=transformDeedIndex(colAllFormulaSet);  //需自动计算的列信息
		
		    //
			String fontName=tnameVo.getString("fontname");
			int fontSize=tnameVo.getInt("fontsize");
			int fontStyle=tnameVo.getInt("fonteffect");	
			int colNum=0;
			int rowNum=0;
			ArrayList tableTermlist =tnameBo.getTableTermList();
			String[] tableTerm = (String[])tableTermlist.get(0);
			String tempflag =tgridBo.getCexpr2Context(9,tableTerm[5]);
			boolean tabletermflag = false;
			if("True".equalsIgnoreCase(tempflag)){
				tabletermflag =true;
			}
			if(resultList.size()>0)
			{
				for(int i=0;i<resultList.size();i++)
				{
					rowNum=0;
					String[] rowInfo=(String[])resultList.get(i);
					RecordVo colVo=(RecordVo)colInfoBGrid.get(i);
					boolean readycolflag = false;
					String tablecol= colVo.getString("cexpr2");	
				String tempcol =	tgridBo.getCexpr2Context(9,tablecol);
				if("True".equalsIgnoreCase(tempcol)){
					readycolflag=true;
				}
					if(colVo.getInt("flag1")!=4) {
                        colNum++;
                    }
					
					for(int j=0;j<rowInfo.length;j++)
					{
						String context="";
						int flag=0;
						String color=getBackColor(i,j,rowDeedIndex,colDeedIndex);
						boolean isAutoAccount=getAccount(i,j,rowChangeIndex,colChangeIndex);
						if(SystemConfig.getPropertyValue("reportAutoCompute")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("reportAutoCompute"))) //20141118 dengcan  客户定义的计算公式太多，造成录入时严重影响性能，可通过参数去掉
                        {
                            isAutoAccount=false;
                        }
						
						RecordVo rowVo=(RecordVo)rowInfoBGrid.get(j);

						boolean readyrowflag = false;
											String tablerow= rowVo.getString("cexpr2");	
										String temprow =	tgridBo.getCexpr2Context(9,tablerow);
										if("True".equalsIgnoreCase(temprow)){
											readyrowflag=true;
										}
						int r=rowVo.getInt("r");
						int npercent=0;
						
						npercent=rowVo.getInt("npercent")>=colVo.getInt("npercent")?rowVo.getInt("npercent"):colVo.getInt("npercent");
						
						String top=String.valueOf(colVo.getInt("rtop"));
						String left=String.valueOf(rowVo.getInt("rleft"));
						String width=String.valueOf(rowVo.getInt("rwidth"));
						String height=String.valueOf(colVo.getInt("rheight"));
						int flag1 = rowVo.getInt("flag1");
						int flag2 = colVo.getInt("flag1");
						if(rowVo.getInt("flag1")!=4) {
                            rowNum++;
                        }
						if(colVo.getInt("flag1")==4&&rowVo.getInt("flag1")==4)
						{
							context="";
						}
						else if(colVo.getInt("flag1")==4)
						{
							context=String.valueOf(rowNum);
						}
						else if(rowVo.getInt("flag1")==4)
						{
							context=String.valueOf(colNum);
						}
						else
						{
							if(Float.parseFloat(rowInfo[j])==0) {
                                context="";
                            } else {
                                context=PubFunc.round(rowInfo[j],npercent);
                            }
							
							flag=1;
						}
						html.append(executeAbsoluteTable_data(flag,flag1,flag2,top,left,width,height,context,i,j,status,fontSize,fontStyle,fontName,npercent,color,isAutoAccount,operateObject,unitcode,selfUnitcode,r,tabletermflag,readycolflag,readyrowflag));
					}
				}
			}
			else
			{
				for(int i=0;i<colInfoBGrid.size();i++)
				{
					rowNum=0;
					RecordVo colVo=(RecordVo)colInfoBGrid.get(i);
					if(colVo.getInt("flag1")!=4) {
                        colNum++;
                    }
					
					boolean readycolflag = false;
					String tablecol= colVo.getString("cexpr2");	
				String tempcol =	tgridBo.getCexpr2Context(9,tablecol);
				if("True".equalsIgnoreCase(tempcol)){
					readycolflag=true;
				}

					for(int j=0;j<rowInfoBGrid.size();j++)
					{
						String context="";
						int flag=0;
						
						String color=getBackColor(i,j,rowDeedIndex,colDeedIndex);
						boolean isAutoAccount=getAccount(i,j,rowChangeIndex,colChangeIndex);
						if(SystemConfig.getPropertyValue("reportAutoCompute")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("reportAutoCompute"))) //20141118 dengcan  客户定义的计算公式太多，造成录入时严重影响性能，可通过参数去掉
                        {
                            isAutoAccount=false;
                        }
						
						RecordVo rowVo=(RecordVo)rowInfoBGrid.get(j);

						boolean readyrowflag = false;
											String tablerow= rowVo.getString("cexpr2");	
										String temprow =	tgridBo.getCexpr2Context(9,tablerow);
										if("True".equalsIgnoreCase(temprow)){
											readyrowflag=true;
										}
						int r=rowVo.getInt("r");
						int npercent=rowVo.getInt("npercent")>=colVo.getInt("npercent")?rowVo.getInt("npercent"):colVo.getInt("npercent");
						String top=String.valueOf(colVo.getInt("rtop"));
						String left=String.valueOf(rowVo.getInt("rleft"));
						String width=String.valueOf(rowVo.getInt("rwidth"));
						String height=String.valueOf(colVo.getInt("rheight"));	
						int flag1 = rowVo.getInt("flag1");
						int flag2 = colVo.getInt("flag1");
						if(rowVo.getInt("flag1")!=4) {
                            rowNum++;
                        }
						if(colVo.getInt("flag1")==4&&rowVo.getInt("flag1")==4)
						{
							context="";
						}
						else if(colVo.getInt("flag1")==4)
						{
							context=String.valueOf(rowNum);
						}
						else if(rowVo.getInt("flag1")==4)
						{
							context=String.valueOf(colNum);
						}
						else
						{
							flag=1;
						}	
						html.append(executeAbsoluteTable_data(flag,flag1,flag2,top,left,width,height,context,i,j,status,fontSize,fontStyle,fontName,npercent,color,isAutoAccount,operateObject,unitcode,selfUnitcode,r,tabletermflag,readycolflag,readyrowflag));
					}
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html.toString();
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 自动修改单元格的边线位置，使其不会出现重叠效果
	 * @param itemVo 项目单元格对象
	 * @param vo	 其他单元格对象
	 */
	public void autoEditBorder(int[] itemGridArea,RecordVo vo)
	{
	
		if((vo.getInt("rleft")+vo.getInt("rwidth"))<=(itemGridArea[0]+itemGridArea[2])&&(vo.getInt("rtop")+vo.getInt("rheight"))<=(itemGridArea[1]+itemGridArea[3])) {
            return;
        } else
		{
			if(vo.getInt("flag")==1)		//横表栏
			{
				if(vo.getInt("rtop")!=itemGridArea[1])
				{
					vo.setInt("rtop",vo.getInt("rtop")-1);
					vo.setInt("rheight",vo.getInt("rheight")+1);
				}
				vo.setInt("rleft",vo.getInt("rleft")-1);
				vo.setInt("rwidth",vo.getInt("rwidth")+1);
			}
			else							//纵表栏 
			{
				if(vo.getInt("rleft")!=itemGridArea[0])
				{
					vo.setInt("rleft",vo.getInt("rleft")-1);
					vo.setInt("rwidth",vo.getInt("rwidth")+1);
				}
				vo.setInt("rtop",vo.getInt("rtop")-1);
				vo.setInt("rheight",vo.getInt("rheight")+1);
			}
		}
	}
	
	
	

	/*
	 * 处理页面显示虚线
	 */
	public String getStyleName(RecordVo vo)
	{
		//处理虚线	L,T,R,B,
	    String style_name="RecordRow_self";
	    if(vo.getInt("l")==0)
	    {
	    	style_name="RecordRow_self_l";
	    	if(vo.getInt("r")==0) {
                style_name="RecordRow_self_two";
            }
	    }
	    else if(vo.getInt("t")==0)
	    {
	    	style_name="RecordRow_self_t";
	    }
	    else if(vo.getInt("r")==0)
	    {
	    	style_name="RecordRow_self_r";
	    }
	    else if(vo.getInt("b")==0)
	    {
	    	style_name="RecordRow_self_b";
	    } 
		return style_name;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 生成上标题
	 * @param pageList  标题列表
	 * @param minTop_px 需上移的像素单位
	 * @param dataArea  表格数据区对象
	 * @return
	 */
	public String createTitle(ArrayList pageList,HashMap paramMap,RecordVo dataArea,String username,int minTop_px,String status,String operateObject,String unitcode,String selfUnitcode,int width)
	{
		StringBuffer htmlTitle=new StringBuffer("");
		try
		{
			Date dd=new Date();     //制表时间
			for(Iterator t=pageList.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				StringBuffer content=new StringBuffer("");
				 String extendattr = vo.getString("extendattr");
				 String temp ="";
				 String formattemp ="";
				 int format=0;
				 if(extendattr.indexOf("<prefix>")!=-1){
					int fromIndex=extendattr.indexOf("<prefix>");
					int toIndex=extendattr.indexOf("</prefix>");
					 temp=extendattr.substring(fromIndex+8,toIndex).trim();
				 }
				 if(extendattr.indexOf("<format>")!=-1){
						int fromIndex=extendattr.indexOf("<format>");
						int toIndex=extendattr.indexOf("</format>");
						formattemp=extendattr.substring(fromIndex+8,toIndex).trim();
						if(formattemp.length()>0) {
                            format = Integer.parseInt(formattemp);
                        }
					 } 
				switch(vo.getInt("flag"))
				{
				 case 0:
					String ahz=vo.getString("hz").replaceAll(" ","&nbsp;");
					content.append("<p style= 'font-size:"+String.valueOf(vo.getInt("fontsize"))+"pt;line-height:"+String.valueOf(vo.getInt("fontsize")+2)+"pt;'>"+ahz+"</p>");				
					break;
				 case 1:				 
					 GregorianCalendar d=new GregorianCalendar();
				//	 d.add(d.MONTH,d.get(Calendar.MONTH)+1);
					
					 if("".equals(temp))
				 	 //content.append(ResourceFactory.getProperty("hmuster.label.createTableDate")+":"+formatDateFiledsetValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE),format)); dml 2012年1月17日17:14:06 lisuju提 制表日期去掉前缀符不应该再出现前缀福
                     {
                         content.append(formatDateFiledsetValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE),format));
                     } else{
					 if(!(temp.lastIndexOf(":")!=-1||temp.lastIndexOf("：")!=-1)) {
                         temp+=":";
                     }
					 content.append(temp+formatDateFiledsetValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE),format));
					 }
					 break;	
				 case 2:
					 if("".equals(temp)) {
                         content.append(ResourceFactory.getProperty("hmuster.label.createTableTime")+DateFormat.getTimeInstance(DateFormat.MEDIUM,Locale.CHINA).format(dd));
                     } else{
						 if(temp.lastIndexOf(":")==-1) {
                             temp+=":";
                         }
						 content.append(temp+DateFormat.getTimeInstance(DateFormat.MEDIUM,Locale.CHINA).format(dd));		
						 }
					break;
				 case 3:
					 ContentDAO dao=new ContentDAO(this.conn);
					 RowSet rowSet=dao.search("select fullname from operuser where username='"+username+"'");
					 String fullname=username;
					 if(rowSet.next())
					 {
						 if(rowSet.getString(1)!=null&&rowSet.getString(1).length()>0) {
                             fullname=rowSet.getString(1);
                         }
					 }
					 if("".equals(temp)) {
                         content.append(ResourceFactory.getProperty("hmuster.label.createTableMen")+"："+fullname);
                     } else{
						 if(temp.lastIndexOf(":")==-1&&temp.lastIndexOf("：")==-1) {
                             temp+="：";
                         }
						 content.append(temp+fullname);
						 }
					 break;
				 case 4:   //总页数
					 break;
				 case 5:   //页码
					 break; 
				 case 9:   //参数定义
				//	 String hz=vo.getString("hz");
				//	 HashMap param_map=(HashMap)paramMap.get(hz);
				//	 content.append(getParamHtml(param_map,status,operateObject,unitcode,selfUnitcode,String.valueOf(vo.getInt("fontsize")+1)));
					 break;
				}
				int a_width=content.length()*(vo.getInt("fontsize")+3);
				htmlTitle.append(executeAbsoluteTable(2,6,vo.getString("fontname"),String.valueOf(vo.getInt("fontsize")+1),String.valueOf(vo.getInt("fonteffect")),"0",String.valueOf(vo.getInt("rtop")+minTop_px),String.valueOf(vo.getInt("rleft")),String.valueOf(a_width>vo.getInt("rwidth")?a_width:vo.getInt("rwidth")),String.valueOf(vo.getInt("rheight")+10),content.toString(),""));
			
			}
			
			for(Iterator t=pageList.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				StringBuffer content=new StringBuffer("");
				if(vo.getInt("flag")==9)
				{
					 String hz=vo.getString("hz");
					 HashMap param_map=(HashMap)paramMap.get(hz);
					 if("2".equals(operateObject)){
						 if(this.isappeal(selfUnitcode,vo.getString("tabid"))&&("1".equals(status)|| "3".equals(status))){
							 content.append(getParamHtml(param_map,"1",operateObject,unitcode,selfUnitcode,String.valueOf(vo.getInt("fontsize")),width-vo.getInt("rleft")));
						 }else{
							 content.append(getParamHtml(param_map,"0",operateObject,unitcode,selfUnitcode,String.valueOf(vo.getInt("fontsize")),width-vo.getInt("rleft")));
						 }
					 }else{
						 content.append(getParamHtml(param_map,status,operateObject,unitcode,selfUnitcode,String.valueOf(vo.getInt("fontsize")),width-vo.getInt("rleft")));
					 }
					 
					 int a_width=content.length()*(vo.getInt("fontsize")+3);
					 htmlTitle.append(executeAbsoluteTable(2,6,vo.getString("fontname"),String.valueOf(vo.getInt("fontsize")+1),String.valueOf(vo.getInt("fonteffect")),"0",String.valueOf(vo.getInt("rtop")+minTop_px),String.valueOf(vo.getInt("rleft")),String.valueOf(a_width>vo.getInt("rwidth")?a_width:vo.getInt("rwidth")),String.valueOf(vo.getInt("rheight")),content.toString(),""));
				}
				if(vo.getInt("flag")==10)
				{
					 String hz=vo.getString("hz");
					 HashMap param_map=(HashMap)paramMap.get(hz);
					
					 content.append(getParamPictureHtml(vo,status,operateObject,unitcode,selfUnitcode,String.valueOf(vo.getInt("fontsize")+1)));
					 String extendattr = vo.getString("extendattr");
					 String background= getExtendAttrContext(5,extendattr);
				              String zindex="";
				                if(background==null|| "".equalsIgnoreCase(background))
				                {
				                	return "";
				                }
				               
				                if("True".equalsIgnoreCase(background)){
				                	zindex="z-index:-1;";
				                }
//					htmlTitle.append(" <table   border='0'background='F:/tomcat5.5/temp/ole-2734.jpg' cellspacing='0'  align='center' cellpadding='0' style='position:absolute;filter:alpha(opacity=40);top:312;left:294;width:102;height:102'> ");
//					htmlTitle.append("<tr valign='middle'  align='center'> "); 
//					htmlTitle.append(" <td  nowrap   valign='middle' align='left'>  ");
//					htmlTitle.append("<font face='宋体' style='font-size:12pt' >  ");
////					htmlTitle.append("<img   src='F:/tomcat5.5/temp/ole-2734.jpg' style='height:302px;width:302px;'  onclick='upload_picture(\"100\",\"22\",\"ole-2734.jpg\");' /> ");
//					htmlTitle.append("</td></tr></table> ");
					 htmlTitle.append(executeAbsoluteTable(2,0,vo.getString("fontname"),String.valueOf(vo.getInt("fontsize")+1),String.valueOf(vo.getInt("fonteffect")),"0",String.valueOf(vo.getInt("rtop")+minTop_px),String.valueOf(vo.getInt("rleft")),String.valueOf(vo.getInt("rwidth")),String.valueOf(vo.getInt("rheight")),content.toString(),zindex));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return htmlTitle.toString();
	}
	
	
	
	//得到参数在页面显示的html原码
	public String getParamHtml(HashMap param_map,String status,String operateObject,String unitcode,String selfUnitcode,String fontSize,int width)
	{
		 StringBuffer sb=new StringBuffer("");
		 if(param_map!=null)
		 {
			 //if(((String)param_map.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character")))
			 if(ResourceFactory.getProperty("kq.formula.character").equals(((String)param_map.get("paramtype"))))//liuy 2014-10-21
			 {
				 sb.append("<input type='text' style='font-size:"+fontSize+"pt' name='");
				 sb.append((String)param_map.get("paramename"));
				 sb.append("' maxlength='");
				 sb.append((String)param_map.get("paramlen")+"' ");
				 if(("3".equals(status)|| "1".equals(status))&& "1".equals(operateObject))			//如果封存，则不可操作
                 {
                     sb.append(" readOnly ");
                 } else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                     sb.append(" readOnly ");
                 } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(this.editupdisk)){
					 if(unitcode.equals(selfUnitcode)){
						 
					 }else{
						 sb.append(" readOnly ");
					 }
				 }else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&!unitcode.equals(selfUnitcode)) {
                     sb.append(" readOnly ");
                 }
				 sb.append("  value='");
				 if(param_map.get("a_value")!=null)
				 {
					 sb.append((String)param_map.get("a_value")+"' ");
					 sb.append("  title='");
					 sb.append((String)param_map.get("a_value")+"' ");
					 String tem=(String)param_map.get("a_value");
					 if(tem.trim().length()>0) {
                         sb.append(" size='"+(tem.getBytes().length)+"' ");
                     } else{
						 if(15<Integer.parseInt(""+param_map.get("paramlen"))) {
                             sb.append(" size='15'");
                         } else {
                             sb.append(" size='"+param_map.get("paramlen")+"'");
                         }
					 }
				}
				 else {
					 sb.append("'");
					 if(15<Integer.parseInt(""+param_map.get("paramlen"))) {
                         sb.append(" size='15'");
                     } else {
                         sb.append(" size='"+param_map.get("paramlen")+"'");
                     }
				 }
				 
				 sb.append(" class='text' />");
			 }
			 //else if(((String)param_map.get("paramtype")).equals(ResourceFactory.getProperty("orglist.reportunitlist.code")))
			 else if(ResourceFactory.getProperty("orglist.reportunitlist.code").equals(((String)param_map.get("paramtype"))))//liuy 2014-10-21
			 {			
				 String[] values=new String[2];
				 if(((String)param_map.get("a_value")).indexOf("/")!=-1) {
                     values=((String)param_map.get("a_value")).split("/");
                 } else
				 {
					 values[0]="";values[1]="";
				 }
				 sb.append("<Input type='hidden' value='"+values[0]+"'  style='font-size:"+fontSize+"pt'  name='"+(String)param_map.get("paramename")+".value'    /><input type=text name='"+(String)param_map.get("paramename")+".hzvalue' ");
				 if(("3".equals(status)|| "1".equals(status))&& "1".equals(operateObject))			//如果封存，则不可操作
                 {
                     sb.append(" disabled='false'");
                 } else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                     sb.append(" disabled='false'");
                 } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(this.editupdisk)){
					 if(unitcode.equals(selfUnitcode)){
						 
					 }else{
						 sb.append(" disabled='false'");
					 }
				 }
				 sb.append(" title='"+values[1]+"' value='"+values[1]+"'  size=\"13\"  class='text'  readOnly  onclick='openCondCodeDialog(\""+(String)param_map.get("paramcode")+"\",\""+(String)param_map.get("paramename")+".hzvalue\");'    /> ");
			 }
			 else if(ResourceFactory.getProperty("report.parse.d").equals(((String)param_map.get("paramtype"))))//liuy 2014-10-21
			 {
				 sb.append("<Input type='text' class='text'  style='font-size:"+fontSize+"pt' name='"+(String)param_map.get("paramename")+"' ");
				 if(("3".equals(status)|| "1".equals(status))&& "1".equals(operateObject))			//如果封存，则不可操作
                 {
                     sb.append(" disabled='false'");
                 } else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                     sb.append(" disabled='false'");
                 } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(this.editupdisk)){
					 if(unitcode.equals(selfUnitcode)){
						 
					 }else{
						 sb.append(" disabled='false'");
					 }
				 }
//				 sb.append(" value='"+(String)param_map.get("a_value")+"'    onfocus='inittime(false);setday(this);''  size=\"13\"   readOnly />");
				 sb.append(" value='"+(String)param_map.get("a_value")+"'   itemlength=10 extra=\"editor\" dataType=\"simpledate\" dropDown=\"dropDownDate\"  size=\"13\"   readOnly />");
			 }
			 else if(ResourceFactory.getProperty("kq.formula.counts").equals(((String)param_map.get("paramtype"))))//liuy 2014-10-21
			 {
				 sb.append("<input type='test' class='text'  style='font-size:"+fontSize+"pt' name='"+(String)param_map.get("paramename")+"' ");
				 if(("3".equals(status)|| "1".equals(status))&& "1".equals(operateObject))			//如果封存，则不可操作
                 {
                     sb.append(" readOnly ");
                 } else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                     sb.append(" readOnly ");
                 } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(this.editupdisk)){
						 if(unitcode.equals(selfUnitcode)){
							 
						 }else{
							 sb.append(" disabled='false'");
						 }
					 }
				 if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&!unitcode.equals(selfUnitcode)) {
                     sb.append(" readOnly ");
                 }
				 if(param_map.get("a_value")!=null) {
                     sb.append(" value='"+(String)param_map.get("a_value")+"' ");
                 }
				 sb.append(" size=\"13\"  onBlur=\"check_data2('"+(String)param_map.get("paramename")+"',"+(String)param_map.get("paramlen")+","+(String)param_map.get("paramfmt")+")\"   />");
				 
			 }
			 else  if(ResourceFactory.getProperty("report.parse.text").equals(((String)param_map.get("paramtype"))))//liuy 2014-10-21
			 {
				// System.out.println(width);
				 sb.append("<textarea  style='font-size:"+fontSize+"pt;width:"+width+"; height:60px' name='");
				 //<textarea rows="20" cols="45" id="content" style="border: 1pt solid #A8C4EC;margin-left:10px;" ></textarea>
				 sb.append((String)param_map.get("paramename")+"'");
				 
				 if(("3".equals(status)|| "1".equals(status))&& "1".equals(operateObject))			//如果封存，则不可操作
                 {
                     sb.append(" readOnly ");
                 } else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                     sb.append(" readOnly ");
                 } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(this.editupdisk)){
					 if(unitcode.equals(selfUnitcode)){
						 
					 }else{
						 sb.append(" readOnly ");
					 }
				 }else if(("1".equals(status)|| "3".equals(status))&& "2".equals(operateObject)&&!unitcode.equals(selfUnitcode)) {
                     sb.append(" readOnly ");
                 }
				 sb.append(" >");
				 if(param_map.get("a_value")!=null)
				 {
					 sb.append((String)param_map.get("a_value")+" ");
					
				}
				 else {
					 sb.append("");
					
				 }
				 
				 sb.append("  </textarea>");
			 }
		 }

		 return sb.toString();
	}
	
	//得到图片
	public String getParamPictureHtml(RecordVo vo,String status,String operateObject,String unitcode,String selfUnitcode,String fontSize)
	{
		 File tempFile = null;
		 StringBuffer sb=new StringBuffer("");
		 if(vo.getObject("content")!=null)
		 {
			
			 String extendattr = vo.getString("extendattr");
             String filename="";
             ServletUtilities.createTempDir();
             String ext= getExtendAttrContext(1,extendattr);
             if(ext==null|| "".equalsIgnoreCase(ext))
             {
             	return "";
             }
             String transparent= getExtendAttrContext(3,extendattr);
             if(transparent==null|| "".equalsIgnoreCase(transparent))
             {
             	return "";
             }
             int tran =40;
             if(!"True".equalsIgnoreCase(transparent)){
            	 tran=100; 
             }
             String stretch= getExtendAttrContext(2,extendattr);
             if(stretch==null|| "".equalsIgnoreCase(stretch))
             {
             	return "";
             }
             String  stre ="width:"+vo.getInt("rwidth")+"px;height:"+vo.getInt("rheight")+"px;";
             if(!"True".equalsIgnoreCase(stretch)){
            	 stre=""; 
             }
             String proportional= getExtendAttrContext(4,extendattr);
             if(proportional==null|| "".equalsIgnoreCase(proportional))
             {
             	return "";
             }
            
             if("True".equalsIgnoreCase(stretch)&& "True".equalsIgnoreCase(proportional)){
            	 stre="width:auto;height:"+vo.getInt("rheight")+"px;";
             }
             InputStream in =null;
             java.io.FileOutputStream fout = null;
             try {

              
                   
                     tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,ext,
                             new File(System.getProperty("java.io.tmpdir")));             
                     in =(InputStream) vo.getObject("content"); 
                     if(in==null) {
                         return "";
                     }
                     fout = new java.io.FileOutputStream(tempFile);                
                     int len;
                     byte buf[] = new byte[1024];
                 
                     while ((len = in.read(buf, 0, 1024)) != -1) {
                         fout.write(buf, 0, len);
                    
                     }
                    
                     filename= tempFile.getName();   
                 
             } catch (Exception e) {
                 e.printStackTrace();
             } 
             finally{
            	 PubFunc.closeResource(fout);
                 PubFunc.closeResource(in);
             }
//			 getExtendAttrContext(1,extendattr);
//             <input type="image" src="/images/photo.jpg" field="photo" dataset="sutemplet_60" style="height:145px;width:113px;" extra="editor" ondblclick="upload_picture('sutemplet_60');" onclick="upload_picture('sutemplet_60');"></input>
             Properties props=System.getProperties(); //系统属性
             String filepathname = tempFile.getName();
            if(props.getProperty("os.name").startsWith("Win")) {
                filepathname =filepathname.replace("\\", "/");
            }
            filepathname ="/servlet/DisplayOleContent?filename="+PubFunc.encrypt(filepathname);//wangcq 2014-12-16 图片文件名加密处理
             sb.append("<img  id='"+vo.getInt("tabid")+"_"+vo.getInt("gridno")+"'  ");
				 sb.append("src='"+filepathname+"'");
    //             sb.append("background='"+filepathname+"'");
	//			 sb.append("' maxlength='");
	//			 sb.append((String)param_map.get("paramlen")+"' ");
				 if("3".equals(status)&& "1".equals(operateObject))			//如果封存，则不可操作
                 {
                     sb.append(" onclick='return false' ");
                 } else if("3".equals(status)&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                     sb.append(" onclick='return false' ");
                 } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(this.editupdisk)) {
                     sb.append(" onclick='return false' ");
                 } else{
					 sb.append(" onclick='upload_picture(\""+vo.getInt("tabid")+"\",\""+vo.getInt("gridno")+"\",\""+PubFunc.encrypt(filename)+"\");' ");//wangcq 2014-12-27 图片文件名加密
				 }
//				 sb.append("  value='");
//				 if(param_map.get("a_value")!=null)
//				 {
//					 sb.append((String)param_map.get("a_value")+"' ");
//					 String tem=(String)param_map.get("a_value");
//					 if(tem.trim().length()>0)
//						 sb.append(" size='"+(tem.getBytes().length)+"' ");
//					 else
//						 sb.append(" size='15'");
//				}
//				 else 
//					 sb.append(" size='15'");
				 sb.append(" style='"+stre+"filter:alpha(opacity="+tran+");'  />");
			 }
		
	//	 sb.append("</div>");

		 return sb.toString();
	}
		
	
	
	
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	   
	 * @param top、left、width、height 表格的绝对位置
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	public String executeAbsoluteTable(int type,int Align,String fontName,String fontSize,String fontStyle,String border,String top,String left,String width,String height,String context,String style_name)
	{
		/**不对齐的问题*/
		int topN = Integer.parseInt(top);
		if(!"填报单位：".equals(context)){
			top = (topN-10)+"";
		}else{
			top = (topN-3)+"";
		}
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];		
		String aAlign=temp[1];
		String zindex ="";
		if(style_name.startsWith("z-index")) {
            zindex=style_name;
        }
		tempTable.append(" <table   border='"+border+"' cellspacing='0'  align='center' cellpadding='0'");
		if(type==1) {
            tempTable.append(" class='ListTable' ");
        }
		tempTable.append(" style='position:absolute;"+zindex+"top:");
		tempTable.append(top+"px");
		tempTable.append(";left:");
		tempTable.append(left+"px");
		
		if(type==1)
		{
			tempTable.append(";width:");
			tempTable.append(width+"px");
		}
		tempTable.append(";height:");
		tempTable.append(height+"px");
		tempTable.append("'> \n ");	
//		if(style_name.equalsIgnoreCase("p")){
//			tempTable.append(" <tr valign='top' align='left'> \n ");
//		}else
		tempTable.append(" <tr valign='middle' align='center'> \n ");
		tempTable.append(" <td ");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		else
		{
			tempTable.append(" nowrap ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");	    
		int aFontSize=0;
		aFontSize=Integer.parseInt(fontSize)-1;			
	    String style=getFontStyle(fontStyle,aFontSize);
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		tempTable.append(style);
		tempTable.append(";' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
		tempTable.append(context);	
		tempTable.append("</font></td></tr></table> \n ");
		
		return tempTable.toString();
	}
	
	
	
	/**
	 * 生成绝对定位的table(每个table表示一个单元格,主要针对表头对象,点击底层表头，选中相应的行列数据)
	 * @param border  边宽
	 * @param align	  字体布局位置	   
	 * @param top、left、width、height 表格的绝对位置
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	public String executeAbsoluteTable2(int Align,String fontName,String fontSize,String fontStyle,String border,String top,String left,String width,String height,String context,String style_name,RecordVo vo,HashMap rowBgridMap,HashMap colBgridMap,String operateObject)
	{
		context=context.trim();
		if(context.length()>4)
		{
			if("<br>".equals(context.toLowerCase().substring(0,4))) {
                context=context.substring(4);
            }
		}
		if(context.length()>4)
		{
			if("<br>".equals(context.toLowerCase().substring(context.length()-4))) {
                context=context.substring(0,context.length()-4);
            }
		}	
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		String div_id=style_name.replaceAll("RecordRow_self", "headerDiv");
		
	//	div_id="headerDiv";
		if(Integer.parseInt(height)<19)
		{
			tempTable.append(" <div id='"+div_id+"' ");		
			tempTable.append(" align='");
			tempTable.append(aAlign+"' ");
		}
		else
		{
			tempTable.append(" <table   border='"+border+"' cellspacing='0'  align='center' cellpadding='0'");		
			tempTable.append(" class='ListTable' ");
			
		}
		
		StringBuffer a_style=new StringBuffer("");
		a_style.append(" style='table-layout:fixed;position:absolute;top:");
		
	/*	if(Integer.parseInt(height)<19)
		{
			a_style.append(Integer.parseInt(top)+5);
		}
		else  //首钢27，31模板出现错行*/
			a_style.append(top+"px");
		
		a_style.append(";left:");
		a_style.append(left+"px");
		a_style.append(";width:");
		a_style.append(width+"px");
		a_style.append(";height:");
		a_style.append(height+"px");
		
		
		
		
	
		if("2".equals(operateObject))
		{
			if("1".equals(vo.getString("flag")))	//行
			{
				
				if(vo.getInt("flag1")!=4&&rowBgridMap.get(vo.getString("gridno"))!=null)
				{
						a_style.append(";cursor:hand");	
						tempTable.append(" onclick=\"selectRowOrColumn('a"+(String)rowBgridMap.get(vo.getString("gridno"))+"')\" ");
						tempTable.append(" onDblClick=\"clearSelected('a"+(String)rowBgridMap.get(vo.getString("gridno"))+"')\" ");
				}
			}
			else if("2".equals(vo.getString("flag")))//列
			{
				if(vo.getInt("flag1")!=4&&colBgridMap.get(vo.getString("gridno"))!=null)
				{
					a_style.append(";cursor:hand");	
					tempTable.append(" onclick=\"selectRowOrColumn('b"+(String)colBgridMap.get(vo.getString("gridno"))+"')\" ");
					tempTable.append(" onDblClick=\"clearSelected('b"+(String)colBgridMap.get(vo.getString("gridno"))+"')\" ");
				}
			}
		
		}
		int aFontSize=0;
		aFontSize=Integer.parseInt(fontSize);		
		aFontSize= getFitFontSize(aFontSize,Float.parseFloat(width),Float.parseFloat(height),context);
	    if("2".equals(fontStyle)|| "4".equals(fontStyle))
	    {
	    	aFontSize--;
	    }
	    tempTable.append(a_style+"'");
		if(Integer.parseInt(height)<19)
		{
			tempTable.append("  > \n ");
		}
		else
		{
			tempTable.append("  > \n ");		
			tempTable.append(" <tr > \n ");
			tempTable.append(" <td style=\"position:relative\" ");
			
			tempTable.append(" height='100%' width='100%' ");
			
			
			tempTable.append(" class='"+style_name+"' ");
			tempTable.append(" valign='");
			tempTable.append(aValign);
			tempTable.append("' align='");
			tempTable.append(aAlign);
			tempTable.append("' nowrap  > \n ");	
			tempTable.append("<div style=\"overflow:hidden;text-align:"+aAlign+";width:"+width+"px;max-height:"+(Integer.parseInt(height)-1)+"px;word-wrap:break-word;");
			//控制纵向显示字间距
			if ("2".equals(vo.getString("flag"))) {
				tempTable.append("line-height:" + (aFontSize + 2) + "pt;");
			} else if ("1".equals(vo.getString("flag"))) {
				tempTable.append("line-height:" + (aFontSize + 1) + "pt;");
			}
			tempTable.append("\" ");
			tempTable.append("title='"+context.replaceAll("<Br>", "")+"'");
			tempTable.append(" valign="+aValign+ " >");
		}
		
	  	String style=getFontStyle(fontStyle,aFontSize);
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		//控制纵向显示字间距
		if("2".equals(vo.getString("flag"))) {
			tempTable.append("line-height:"+(aFontSize+2)+"pt;");
		}
		tempTable.append(style);
		tempTable.append(";' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
		tempTable.append(context);	
//		tempTable.append(getFontStyle2(0,fontStyle));
		tempTable.append("</font>");
		if(Integer.parseInt(height)<19)
		{
			tempTable.append("</div>");
		}
		else {
			tempTable.append("</div>");
			tempTable.append("</td></tr></table> \n ");
		} 
		return tempTable.toString();
	}
	
	
	public int  getFitFontSize(int fontSize,float width,float height,String context)
	{

		//纵适应
		int afontSize=fontSize;
		String[] temps=context.split("<Br>");
		int constant=7;
		if(temps.length>2&&temps.length<5) {
            constant=6;
        } else if(temps.length>=5&&temps.length<8) {
            constant=5;
        } else if(temps.length>=8) {
            constant=2;
        }
		
		while(true)
		{
			if((afontSize+constant)*temps.length<=height) {
                break;
            } else {
                afontSize--;
            }
		}
		
		//横适应
		int maxNum=0;
		for(int i=0;i<temps.length;i++)
		{
			int a_max=0;
			if(temps[i].getBytes().length%2==1) {
                a_max=temps[i].getBytes().length/2+1;
            } else {
                a_max=temps[i].getBytes().length/2;
            }
			if(a_max>maxNum)
			{
					maxNum=a_max;
			}
		}
		//constant=8;
		//if(maxNum>2)
		constant=5;
        if(maxNum>=4&&maxNum<8) {
            constant=3;
        } else if(maxNum>=8) {
            constant=2;
        }
		
		while(true)
		{
			if((afontSize+constant)*maxNum<=width) {
                break;
            } else {
                afontSize--;
            }
		}
		if(afontSize<9) {
			afontSize = 9;
		}
		return afontSize;
	}
	
	
	
	
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * @param flag    1:包含编辑框  0：只显示
	 * @param top	  
	 * @param left
	 * @param width
	 * @param height
	 * @param context 值
	 * @param i
	 * @param j
	 * @param status  报表状态
	 * @param color   单元格背景色
	 * @param isAutoAccount 是否需要自动计算
	 * @param operateObject 1:操作未上报的表  2：操作已上报的表
	 * @param r  是否有左边线
	 * @return
	 */
	public String executeAbsoluteTable_data(int flag,int flag1,int flag2,String top,String left,String width,String height,String context,int i,int j,String status,int fontSize,int fontStyle,String fontName,int npercent,String color,boolean isAutoAccount,String operateObject,String unitcode,String selfUnitcode,int r,boolean tabletermflag,boolean readycolflag,boolean readyrowflag)
	{
		
		StringBuffer tempTable=new StringBuffer("");
		if(flag==2){
			tempTable.append(" <table   border='0' cellspacing='0'  align='center' cellpadding='0'");
		}else{
			tempTable.append(" <table   border='1' cellspacing='0'  align='center' cellpadding='0'");
		}
		
		tempTable.append(" class='ListTable' ");
		tempTable.append(" style='table-layout:fixed;position:absolute;top:");
		tempTable.append(top+"px");
		tempTable.append(";left:");
		tempTable.append(left+"px");
		tempTable.append(";width:");
		tempTable.append(width+"px");
		tempTable.append(";height:");
		tempTable.append(height+"px");
		tempTable.append("'> \n ");		
		tempTable.append(" <tr valign='middle' align='center'> \n ");
		tempTable.append(" <td class='");
		if(r==1){
			tempTable.append("RecordRow_self");
		}else if(flag==2){
			tempTable.append("styte=display:none");
		}			
		else {
            tempTable.append("RecordRow_self_r");
        }
		tempTable.append("'  id='aa"+i+"_"+j+"'");
		tempTable.append(" width='");
		tempTable.append("100%");
		tempTable.append("' height='");
		tempTable.append("100%"+"' ");
		
		if(color.length()>1) {
            tempTable.append(" bgcolor='"+color+"'");
        }
		tempTable.append("  align='center' vAlign='bottom'  > \n ");	 
		if(flag==1||flag==0||flag==2)
		{
			tempTable.append("<input type='text' name='a"+i+"_"+j+"' value='");
			tempTable.append(context);
			tempTable.append("'");
			//status  =-1，未填  =0,正在编辑  =1,已上报 =2,打回 =3,封存（基层单位的数据不让修改
			 if(("3".equals(status)|| "1".equals(status)|| "4".equals(status))&& "1".equals(operateObject))			//如果封存，则不可操作
             {
                 tempTable.append(" readOnly ");
             } else if(("3".equals(status)|| "1".equals(status)|| "4".equals(status))&& "2".equals(operateObject)&&unitcode.equals(selfUnitcode)) {
                 tempTable.append(" readOnly ");
             } else if("2".equals(operateObject)&& "false".equalsIgnoreCase(editupdisk)){
				 if(unitcode.equals(selfUnitcode)){
					 
				 }else{
					 tempTable.append(" readOnly ");
				 }
			 }
			 else if(("3".equals(status)|| "1".equals(status)|| "4".equals(status))&& "2".equals(operateObject)&&!unitcode.equals(selfUnitcode)) {
                 tempTable.append(" readOnly ");
             }
			if(tabletermflag) {
                tempTable.append(" readOnly ");
            }
			if(readycolflag) {
                tempTable.append(" readOnly ");
            }
			if(readyrowflag) {
                tempTable.append(" readOnly ");
            }
			 // 用于反查 
			 if("1".equals(operateObject)) {
				 if(flag1!=4&&flag2!=4) {
						tempTable.append("  onDblClick=\"setReverseID('a"+i+"_"+j+"')\"  ");
					}
			 }
			//liuy 2015-2-7 6838：在工具箱设置了正文字体是带下划线和删除线的,在bs查看没有下划线和删除线 start
			int aFontSize=fontSize-1;
			String style=getFontStyle(String.valueOf(fontStyle),aFontSize);
			tempTable.append(" class='TEXT_NB' style='padding:0px 0px 0px 1px;height: "+(Integer.parseInt(height)*0.7)+"px; width: ");
			/*
			tempTable.append((Integer.parseInt(width)*0.8));
			tempTable.append("px;font-size:"+fontSize+"pt;text-align= right;'  onblur=\"check_data('a"+i+"_"+j+"',"+npercent+");\"");
			*/
			tempTable.append((Integer.parseInt(width)*0.8)+"px;");
			tempTable.append(style);
			tempTable.append(";text-align= right;'  onblur=\"check_data('a"+i+"_"+j+"',"+npercent+");\"");//add by xiegh on date 20171201 bug:33052 少了一个分号 导致预设的字体大小无效
			//liuy 2015-2-7 end
			if(isAutoAccount)		//此单元格需要自动计算
			{
				tempTable.append(" onchange=\"autoAccount('a"+i+"_"+j+"',"+npercent+")\"");
			}
			tempTable.append("   onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'   />");
		}
		else
		{
			int aFontSize=fontSize-1;
		    String style=getFontStyle(String.valueOf(fontStyle),aFontSize);
			tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
//			tempTable.append(aFontSize);
//			tempTable.append("pt' > \n ");
			tempTable.append(style);
			tempTable.append(";' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
//			tempTable.append(getFontStyle2(1,String.valueOf(fontStyle)));
			tempTable.append(context);	
//			tempTable.append(getFontStyle2(0,String.valueOf(fontStyle)));
			tempTable.append("</font>");
			
			tempTable.append("<input type='hidden' name='a"+i+"_"+j+"' value='0' > ");
		}
		tempTable.append("</td></tr></table> \n ");
		return tempTable.toString();
	}
	
	
	
	
	
//	private String getFontStyle2(int flag,String fontStyle)
//	{
//		String style="";
//		if(flag==1)
//		{
//			if(fontStyle.equals("2"))
//				style="<b>";
//			else if(fontStyle.endsWith("3"))
//				style="<i>";
//			else if(fontStyle.equals("4"))
//				style="<b><i>";
//		}
//		else
//		{
//			if(fontStyle.equals("2"))
//				style="</b>";
//			else if(fontStyle.endsWith("3"))
//				style="</i>";
//			else if(fontStyle.equals("4"))
//				style="</b></i>";
//			
//		}
//		return style;
//		
//	}
	
	
	/**
	 * 转换成字符样式
	 */
	
	/**
	 * 转换成字符样式
	 */
	private String getFontStyle(String fontStyle, int aFontSize) {//add by wangcq on 2014-11-22
		StringBuffer style = new StringBuffer("");
		int font = Integer.parseInt(fontStyle) - 1;
		boolean b,t,s,u;  //粗体、斜体、删除线、下划线
		b = (font & 0x00000001) == 0x00000001;
		t = (font & 0x00000002) == 0x00000002;
		s = (font & 0x00000004) == 0x00000004;
		u = (font & 0x00000008) == 0x00000008;
		if(font > 0){
			if(u && s) {
                style.append("text-decoration:underline line-through;");//既有下划线，又有删除线
            } else{
				if(u) {
                    style.append("text-decoration:underline;") ;
                }
				if(s) {
                    style.append("text-decoration:line-through;") ;
                }
			}
			if(t) {
                style.append("font-style:italic;") ;
            }
			if(b) {
                style.append("font-weight:bold;") ;
            }
		}
		else {
            style.append("font-weight:normal;");
        }
		style.append("font-size:" + aFontSize + "pt");
		return style.toString();
	}
	/*private String getFontStyle(String fontStyle,int aFontSize) 
	{
		String style="";
		if(fontStyle.equals("2"))
			style="font-weight：bold;font-size:"+aFontSize+"pt";
		else if(fontStyle.endsWith("3"))
			style="font-style：italic;font-size:"+aFontSize+"pt";
		else if(fontStyle.equals("4"))
			style="font-style：italic;font-weight:bold;font-size："+aFontSize+"pt";
		else
			style="font-weight:normal;font-size:"+aFontSize+"pt";
		return style;
	}*/
	
	
	 
	/**
	 * 转换成字体布局字符
	 */
	private String[] transAlign(int Align)
	{
		String[] temp=new String[2];
		if(Align==0)
		{
			temp[0]="top";
			temp[1]="left";
		}
		else if(Align==1)
		{
			temp[0]="top";
			temp[1]="center";
		}
		else if(Align==2)
		{
			temp[0]="top";
			temp[1]="right";
		}
		else if(Align==3)
		{
			temp[0]="bottom";
			temp[1]="left";
		}
		else if(Align==4)
		{
			temp[0]="bottom";
			temp[1]="center";
		}
		else if(Align==5)
		{
			temp[0]="bottom";
			temp[1]="right";
		}
		else if(Align==6)
		{
			temp[0]="middle";
			temp[1]="left";
		}
		else if(Align==7)
		{
			temp[0]="middle";
			temp[1]="center";
		}
		else if(Align==8)
		{
			temp[0]="middle";
			temp[1]="right";
		}		
		return temp;		
	}
	 
	
	
	
	/**
	 * 生成绝对定位的背景页面  
	 * @param top、left、width、height 表格的绝对位置
	 */	
	public String executeAbsoluteBackground (int top,int left,float width,float height)
	{			
		StringBuffer tempHtml=new StringBuffer("");	
		
		tempHtml.append("<div id=idDIV ");
		tempHtml.append(" style='position:absolute;top:");
		tempHtml.append(top+"px");
		tempHtml.append(";left:");
		tempHtml.append(left+"px");
		tempHtml.append(";width:");
		tempHtml.append(width+"px");
		tempHtml.append(";height:");
		tempHtml.append(height+"px"+";");
		tempHtml.append("z-index:-2;");//wangcq 2014-12-16 背景置底
		if(showPaper) {
            tempHtml.append("border:thin outset buttonface;background : #ffffff ;");
        } else {
            tempHtml.append("border:none;");
        }
		tempHtml.append("'> \n ");
		tempHtml.append("&nbsp;</div>");
		
		tempHtml.append("<div ");
		tempHtml.append(" style='position:absolute;top:");
		tempHtml.append(top+"px");
		tempHtml.append(";left:");
		tempHtml.append((left+width)+"px");
		tempHtml.append(";width:");
		tempHtml.append(20+"px");
		tempHtml.append(";height:");
		tempHtml.append(2+"px");
		tempHtml.append(";'> \n ");	
		tempHtml.append("&nbsp;</div>");
		return tempHtml.toString();
	}
	
	public String executeAbsoluteBackground1 (int top,int left,float width,float height)
	{			
		StringBuffer tempHtml=new StringBuffer("");	
		
		tempHtml.append("<div id=idDIV ");
		tempHtml.append(" style='position:absolute;top:");
		tempHtml.append(top);
		tempHtml.append(";left:");
		tempHtml.append(left);
		tempHtml.append(";width:");
		tempHtml.append(width);
		tempHtml.append(";height:");
		tempHtml.append(height);
		tempHtml.append(";border:thin outset buttonface;background : #ffffff ;'> \n ");	
		
		return tempHtml.toString();
	}
	
	public String executeAbsoluteBackground2 (int top,int left,float width,float height)
	{			
		StringBuffer tempHtml=new StringBuffer("");	
		
		
		tempHtml.append("&nbsp;</div>");
		
		tempHtml.append("<div ");
		tempHtml.append(" style='position:absolute;top:");
		tempHtml.append(top);
		tempHtml.append(";left:");
		tempHtml.append(left+width);
		tempHtml.append(";width:");
		tempHtml.append(20);
		tempHtml.append(";height:");
		tempHtml.append(2);
		tempHtml.append(";'> \n ");	
		tempHtml.append("&nbsp;</div>");
		return tempHtml.toString();
	}
	
	
	/**
	 * 取得tpage下的extendAttr字段的内容<image>
     *<ext>.JPG|.BMP</ext><stretch>拉伸True|False</stretch>
     *<transparent>透明True|False</transparent>
    * <proportional>保持比例True|False</proportional>
     *<background>置底True(默认值)|置顶False</background>
     *1表示图片，2表示拉伸，3透明，4保持比列，5置底
	*</image>
	 *
	 * @return
	 */
	public String getExtendAttrContext(int flag,String extendAttr)
	{
		String temp="";
	
		if(extendAttr!=null&&extendAttr.length()>0)  
		{
				if(extendAttr.indexOf("<image>")!=-1){
					if(flag==1){
					if(extendAttr.indexOf("<ext>")==-1) {
                        temp=".jpg";
                    } else
					{
						int fromIndex=extendAttr.indexOf("<ext>");
						int toIndex=extendAttr.indexOf("</ext>");
						temp=extendAttr.substring(fromIndex+5,toIndex).trim();
					
					}
					}
					if(flag==2){
					if(extendAttr.indexOf("<stretch>")==-1) {
                        temp="True";
                    } else
					{
						int fromIndex=extendAttr.indexOf("<stretch>");
						int toIndex=extendAttr.indexOf("</stretch>");
						temp=extendAttr.substring(fromIndex+9,toIndex).trim();
					
					}
					}
					if(flag==3){
					if(extendAttr.indexOf("<transparent>")==-1) {
                        temp="True";
                    } else
					{
						int fromIndex=extendAttr.indexOf("<transparent>");
						int toIndex=extendAttr.indexOf("</transparent>");
						temp=extendAttr.substring(fromIndex+13,toIndex).trim();
					
					}
					}
					if(flag==4){
					if(extendAttr.indexOf("<proportional>")==-1) {
                        temp="True";
                    } else
					{
						int fromIndex=extendAttr.indexOf("<proportional>");
						int toIndex=extendAttr.indexOf("</proportional>");
						temp=extendAttr.substring(fromIndex+14,toIndex).trim();
					
					}
					}
					if(flag==5){
					if(extendAttr.indexOf("<background>")==-1) {
                        temp="True";
                    } else
					{
						int fromIndex=extendAttr.indexOf("<background>");
						int toIndex=extendAttr.indexOf("</background>");
						temp=extendAttr.substring(fromIndex+12,toIndex).trim();
					
					}
				}
			}
		}
		
		return temp;
	}
	/**
	 * 子集中格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @return
	 */
	private String formatDateFiledsetValue(String value,int disformat)
	{
		StringBuffer buf=new StringBuffer();
	
		String prefix="",strext="";
		
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
		Date date=DateUtils.getDate(value,"yyyy-MM-dd");
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		String strv[]=exchangNumToCn(year,month,day);	
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
		case 1://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		case 2://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 3://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;
		case 4: //1991-12-3
			buf.append(year);
			buf.append("-");
			buf.append(month);
			buf.append("-");
			buf.append(day);
			break;
		case 5://1992-02-01
			buf.append(year);
			buf.append("-");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("-");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		case 6://1999年02月
			buf.append(year);
			buf.append("年");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		return buf.toString();
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
	
		/*
		double fcage=ncyear+ncmonth*0.01+ncday*0.0001;
		double fage=nyear+nmonth*0.01+nday*0.0001;
		long nage= Math.round(fcage-fage);
		buf.append(nage);*/
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public boolean isappeal(String unitcode,String tabid){
		boolean flag=false;
		RowSet rs=null;
		ContentDAO dao =new ContentDAO(this.conn);
		try {
			rs=dao.search("select * from treport_ctrl where unitcode='" +unitcode +"' and tabid='"+ tabid+"'");
			if(rs.next()){
				if("1".equals(rs.getString("status"))){
					flag=true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
				try {
					if(rs!=null){
						rs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return flag;
	}

    public boolean isShowPaper() {
        return showPaper;
    }

    public void setShowPaper(boolean showPaper) {
        this.showPaper = showPaper;
    }
}
