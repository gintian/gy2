package com.hjsj.hrms.businessobject.report;


import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class TgridBo {

	private Connection conn=null;
	private ExprUtil exprUtil=new ExprUtil();
	private DBMetaModel dbmodel=null;

	private String    result=null;						  //是否从查询结果库中取数
	private ArrayList dbList=new ArrayList();			  //扫描库	
	private UserView userview=null;
	private HashMap tableTermsMap = new HashMap();
	private HashMap table_columnMap=new HashMap();        //临时表理存在的列名
	private String  startdate=null;                       //起始日期
	private ArrayList infoList=new ArrayList();           //表条件

	private HashMap factorListMap = new HashMap();    //项目格条件因子集合

	public HashMap getFactorListMap() {
		return factorListMap;
	}


	public void setFactorListMap(HashMap factorListMap) {
		this.factorListMap = factorListMap;
	}


	public TgridBo(Connection conn) {
		this.conn=conn;
		this.dbmodel=new DBMetaModel(this.conn);
	}
	

	/*
	tgrid2.cexpr2:
		<EXPR>统计（取值）方法公式</EXPR>
		<SCOPE>
		<MODE>0当前记录，1历史记录，空表示当前记录</MODE>
		<COND> 
			--比如，当月：<STARTDATE>当月</STARTDATE><ENDDATE>当月</ENDDATE>
			--      1-当月：<STARTDATE>1</STARTDATE><ENDDATE>当月</ENDDATE>
			<STARTDATE>历史记录条件，开始时间：当月|某月|某年某月|本季|某季度|起始日期|yyyy.mm.dd|起始日期±n月|截止日期±n月</STARTDATE>
			<ENDDATE>结束时间：当月|某月|某年某月|本季|某季度|截止日期|yyyy.mm.dd|截止日期±n月|起始日期±n月</ENDDATE>
			<CONDFACTOR>子集历史记录条件A7910=11,A7910=12</CONDFACTOR> <CONDEXPR>1*2</CONDEXPR>
		</COND>
		</SCOPE>
		<COUNTSET>求个数子集，默认为主集</COUNTSET>
		<SUMFLAG>归档数据汇总发放1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和</SUMFLAG>
		<MANAGEPRIV>按权限范围统计:0:不限制,  1:限制, 默认为限制</MANAGEPRIV>
		<READONLY>网上填报是否为只读True|False,默认False,项目格/列表头底格/行表头最右格可定义</READONLY>
		更新：\\192.192.100.162\dev\HRP7.22\最近变动\tjbdll.dll
    */
	/**
	 * 得到某报表的所有列集合
	 *
	 * @param tabid
	 * @return
	 */
	public ArrayList getGridInfoList(String tabid)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		ArrayList listInfo=new ArrayList();
		try
		{
			if(tabid==null || tabid.trim().length()<=0) {
                return listInfo;
            }
			//获取结果区域位置
			recset=dao.search("select * from tgrid3 where tabid="+tabid+" and flag=3 ");
			int rtop=0;
			while(recset.next()) {
				rtop=recset.getInt("rtop");
			}

			if(rtop!=0) {
				//表格头与内容区域查询排序方式区分 
				recset=dao.search("select *  from tgrid2 where tabid="+tabid+" and rtop<"+rtop+" order by rleft,rtop");
				getresultList(recset, listInfo);

				recset=dao.search("select *  from tgrid2 where tabid="+tabid+" and rtop>="+rtop+" order by rtop,rleft");
				getresultList(recset, listInfo);
			}else {
				recset=dao.search("select *  from tgrid2 where tabid="+tabid+" order by rleft");//xiegh date:20170810 add 原因：生成html横向列标题时 加载顺序非从左到右时 导致中间的边线缺失 bug:28438 
				getresultList(recset, listInfo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(recset);
		}
		return listInfo;
	}

	private void getresultList(RowSet recset, ArrayList listInfo) throws Exception {
		while (recset.next()) {
			RecordVo vo = new RecordVo("tgrid2");
			vo.setInt("gridno", recset.getInt("gridno"));
			vo.setString("hz", recset.getString("hz"));
			// System.out.println("-------------" + recset.getString("hz"));
			vo.setInt("rleft", recset.getInt("rleft"));
			vo.setInt("rtop", recset.getInt("rtop"));
			vo.setInt("rwidth", recset.getInt("rwidth"));
			vo.setInt("rheight", recset.getInt("rheight"));
			vo.setInt("l", recset.getInt("l"));
			vo.setInt("t", recset.getInt("t"));
			vo.setInt("r", recset.getInt("r"));
			vo.setInt("b", recset.getInt("b"));
			vo.setInt("sl", recset.getInt("sl"));
			vo.setString("cfactor", Sql_switcher.readMemo(recset, "cfactor"));
			vo.setInt("flag2", recset.getInt("flag2"));
			vo.setInt("flag1", recset.getInt("flag1"));
			vo.setString("cexpr2", Sql_switcher.readMemo(recset, "cexpr2"));
			vo.setString("cexpr1", Sql_switcher.readMemo(recset, "cexpr1"));
			vo.setInt("scanmode", recset.getInt("scanmode"));
			vo.setInt("fontsize", recset.getInt("fontsize"));
			vo.setString("fontname", recset.getString("fontname"));
			vo.setInt("fonteffect", recset.getInt("fonteffect"));
			vo.setInt("flag", recset.getInt("flag"));
			vo.setInt("align", recset.getInt("align"));
			vo.setInt("lsize", recset.getInt("lsize"));
			vo.setInt("rsize", recset.getInt("rsize"));
			vo.setInt("tsize", recset.getInt("tsize"));
			vo.setInt("bsize", recset.getInt("bsize"));
			vo.setInt("npercent", recset.getInt("npercent"));
			vo.setString("archive_item", recset.getString("archive_item"));
			listInfo.add(vo);
		}
	}


	/**
	 * 得到某报表的所有列集合
	 *
	 * @param tabid
	 * @return
	 */
	public ArrayList MidVariableNotContrlList(String tabid,ArrayList midVariableList)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		ArrayList midVariableNotContrlList=new ArrayList();
		try
		{
			if(tabid==null || tabid.trim().length()<=0) {
                return midVariableNotContrlList;
            }
			recset=dao.search("select *  from tgrid2 where tabid="+tabid);
			while(recset.next())
			{
				String cexpr2 = Sql_switcher.readMemo(recset,"cexpr2");
				if(cexpr2!=null&&cexpr2.length()>0){
					String flag = getCexpr2Context(6,cexpr2);
					String expr = getCexpr2Context(1,cexpr2);
					if("0".equals(flag)&&expr.length()>0){
						for(Iterator t3=midVariableList.iterator();t3.hasNext();)
						{
							RecordVo vo=(RecordVo)t3.next();
							if(vo!=null&&vo.getString("cname").trim().length()>0){
								if(expr.indexOf(vo.getString("cname").trim())!=-1)
								{
									midVariableNotContrlList.add(vo);
									break;
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
			throw GeneralExceptionHandler.Handle(e);
		}
		/*
		 * finally { try { if(recset!=null) recset.close(); } catch(Exception e) {
		 * e.printStackTrace(); } }
		 */
		return midVariableNotContrlList;
	}




	/**
	 * 得到项目格的区域
	 *
	 * @param tabid
	 * @param gridInfoList
	 *            报表的所有列集合
	 * @return
	 */
	public int[] getItemGridID(String tabid)throws GeneralException
	{
		int[] area=new int[4];
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			if(tabid==null || tabid.trim().length()<=0) {
                return area;
            }
			recset=dao.search("select * from tgrid3 where tabid="+tabid+" and flag=0");
			if(recset.next())
			{
				area[0]=recset.getInt("rleft");
				area[1]=recset.getInt("rtop");
				area[2]=recset.getInt("rwidth");
				area[3]=recset.getInt("rheight");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		/*
		 * finally { try { if(recset!=null) recset.close(); } catch(Exception e) {
		 * e.printStackTrace(); } }
		 */
		return area;
	}




	/**
	 * 分析报表,得到横表栏和纵表栏的相关信息集合及表条件 （按最底层行列顺序排列,信息中包括针对每最底层格和次底层格的统计条件）
	 *
	 * @param tabid
	 *            报表id
	 * @return
	 */
	public ArrayList getRowAndColInfoList(ArrayList gridInfoList,String tabid,HashMap rowMap,HashMap colMap)throws GeneralException
	{
		ArrayList list=new ArrayList();
		String[]  tableTerm=null;	                  // 表条件
		int rows=0;
		int cols=0;
		int height=0;
		int width=0;
		String  rowSerialNo="";						  // 横表栏序号所在位置
		String  colSerialNo="";              		  // 纵表栏序号所在位置
		int[] itemArea=new int[4];
		try
		{
			int rowBotPix=0;                          // 横表栏底线象素位置
			int colRigPix=0;	                      // 纵表栏右线像素位置
			RecordVo itemGridVo=null;
			ArrayList itemGridList=new ArrayList(); //项目栏单元格列表
			ArrayList tableTermList=new ArrayList();
			itemArea=getItemGridID(tabid);				//得到项目栏区域 l,t,w,h
			rowBotPix=itemArea[1]+itemArea[3];
			colRigPix=itemArea[0]+itemArea[2];
			height=itemArea[3];
			width=itemArea[2];
			for(Iterator t=gridInfoList.iterator();t.hasNext();)
			{
				RecordVo recset=(RecordVo)t.next();
				if(recset.getInt("flag")==0)
				{
					tableTerm=getGridAnayzeInfo(recset);
					tableTermList.add(tableTerm);
					itemGridList.add(recset);

				}
				if(recset.getInt("flag")==1)
				{
					if((recset.getInt("rtop")+recset.getInt("rheight"))==rowBotPix) {
                        rows++;
                    }
				}
				else if(recset.getInt("flag")==2)
				{
					if((recset.getInt("rleft")+recset.getInt("rwidth"))==colRigPix) {
                        cols++;
                    }
				}

			}
			ArrayList rowInfoList=anayzeRowTable(gridInfoList,rows,rowBotPix,colRigPix,height,colMap,new ArrayList());
			rowSerialNo=(String)rowInfoList.get(1);
			ArrayList colInfoList=anayzeColTable(gridInfoList,cols,rowBotPix,colRigPix,width,rowMap,new ArrayList());
			colSerialNo=(String)colInfoList.get(1);
			list.add((ArrayList)rowInfoList.get(0));
			list.add((ArrayList)colInfoList.get(0));
			list.add(tableTerm);
			list.add(itemGridVo);
			list.add(rowSerialNo);
			list.add(colSerialNo);
			list.add(itemArea);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}







	/**
	 * 分析报表,得到横表栏和纵表栏的相关信息集合及表条件 （按最底层行列顺序排列,信息中包括针对每最底层格和次底层格的统计条件）
	 *
	 * @param tabid
	 *            报表id
	 * @return
	 */
	public ArrayList getRowAndColInfoList2(ArrayList gridInfoList,String tabid,HashMap rowMap,HashMap colMap,ArrayList rowInfoBGrid,ArrayList colInfoBGrid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		String[]  tableTerm=null;	// 表条件
		ArrayList tableTermList=new ArrayList();
		String   numberPosition="";                   // 序号所在位置
		int rows=0;
		int cols=0;
		int height=0;
		int width=0;
		String  rowSerialNo="";						  // 横表栏序号所在位置
		String  colSerialNo="";              		  // 纵表栏序号所在位置
		int[] itemArea=new int[4];
		try
		{
			int rowBotPix=0;     // 横表栏底线象素位置
			int colRigPix=0;	 // 纵表栏右线像素位置
			ArrayList itemGridList=new ArrayList(); //项目栏单元格列表
			RecordVo itemGridVo=null;
			RecordVo dataArea=null;
			itemArea=getItemGridID(tabid);				//得到项目栏区域 l,t,w,h
			rowBotPix=itemArea[1]+itemArea[3];
			colRigPix=itemArea[0]+itemArea[2];
			height=itemArea[3];
			width=itemArea[2];
			for(Iterator t=gridInfoList.iterator();t.hasNext();)
			{
				RecordVo recset=(RecordVo)t.next();
				if(recset.getInt("flag")==0)
				{
					tableTerm=getGridAnayzeInfo(recset);
					tableTermList.add(tableTerm);
					itemGridList.add(recset);

				}
				if(recset.getInt("flag")==1)
				{
					if((recset.getInt("rtop")+recset.getInt("rheight"))==rowBotPix)
					{
						rows++;
					}
				}
				else if(recset.getInt("flag")==2)
				{
					if((recset.getInt("rleft")+recset.getInt("rwidth"))==colRigPix)
					{
						cols++;
					}
				}
				else if(recset.getInt("flag")==3) {
                    dataArea=recset;
                }

			}
			ArrayList rowInfoList=anayzeRowTable(gridInfoList,rows,rowBotPix,colRigPix,height,colMap,rowInfoBGrid);
			rowSerialNo=(String)rowInfoList.get(1);
			ArrayList colInfoList=anayzeColTable(gridInfoList,cols,rowBotPix,colRigPix,width,rowMap,colInfoBGrid);
			colSerialNo=(String)colInfoList.get(1);
			list.add((ArrayList)rowInfoList.get(0));
			list.add((ArrayList)colInfoList.get(0));
			list.add(tableTermList);
			list.add(itemGridList);
			list.add(dataArea);
			list.add(rowSerialNo);
			list.add(colSerialNo);
			list.add(itemArea);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}







	/**
	 * 分析报表,取得colMap,rowMap,digitalResults值
	 *
	 * @param tabid
	 *            报表id
	 * @return
	 */
	public ArrayList getRowAndColInfoList3(ArrayList gridInfoList,String tabid,HashMap rowMap,HashMap colMap,ArrayList rowInfoBGrid,ArrayList colInfoBGrid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		int rows=0;
		int cols=0;
		int height=0;
		int width=0;
		int[][] digitalResults=null;
		try
		{
			int rowBotPix=0;                          // 横表栏底线象素位置
			int colRigPix=0;	                      // 纵表栏右线像素位置

			int[] itemArea=getItemGridID(tabid);				//得到项目栏区域 l,t,w,h
			rowBotPix=itemArea[1]+itemArea[3];
			colRigPix=itemArea[0]+itemArea[2];
			height=itemArea[3];
			width=itemArea[2];
			for(Iterator t=gridInfoList.iterator();t.hasNext();)
			{
				RecordVo recset=(RecordVo)t.next();
				if(recset.getInt("flag")==1)
				{
					if((recset.getInt("rtop")+recset.getInt("rheight"))==rowBotPix) {
                        rows++;
                    }
				}
				else if(recset.getInt("flag")==2)
				{
					if((recset.getInt("rleft")+recset.getInt("rwidth"))==colRigPix) {
                        cols++;
                    }
				}

			}
			String rowSerialNo=anayzeRowTable3(gridInfoList,rows,rowBotPix,colRigPix,height,colMap,rowInfoBGrid);
			String colSerialNo=anayzeColTable3(gridInfoList,cols,rowBotPix,colRigPix,width,rowMap,colInfoBGrid);
			digitalResults=new int[cols][rows];
			for(int i=0;i<cols;i++)
			{
				RecordVo Vo=(RecordVo)colInfoBGrid.get(i);
				for(int j=0;j<rows;j++)
				{
					RecordVo r_Vo=(RecordVo)rowInfoBGrid.get(j);
					digitalResults[i][j]=Vo.getInt("npercent")>r_Vo.getInt("npercent")?Vo.getInt("npercent"):r_Vo.getInt("npercent");
				}

			}

			list.add(digitalResults);
			list.add(rowSerialNo);
			list.add(colSerialNo);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}



	/**
	 * 分析报表，将横表栏的相关信息按最底层列顺序放入list
	 *
	 * @param listInfo
	 *            单元格信息集合
	 * @param rows
	 * @param flag
	 * @return
	 */
	public String anayzeRowTable3(ArrayList listInfo,int rows,int rowBotPix,int colRigPix,int height,HashMap colMap,ArrayList rowInfoBGrid)
	{
		StringBuffer rowSerialNo=new StringBuffer("");
		int a_colRigPix=colRigPix;
		int b=0;
		for(int i=0;i<rows;i++)
		{
			for(Iterator t=listInfo.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				if(vo.getInt("rleft")==a_colRigPix&&vo.getInt("rtop")+vo.getInt("rheight")==rowBotPix)
				{
					rowInfoBGrid.add(vo);
					if(vo.getInt("flag1")!=4)
					{
						b++;
						colMap.put(String.valueOf(b),String.valueOf(i));
					}
					else
					{
						rowSerialNo.append(i+",");
					}
					a_colRigPix=vo.getInt("rleft")+vo.getInt("rwidth");
					break;
				}

			}
		}
		return rowSerialNo.toString();
	}


	/**
	 * 分析报表，将纵表栏的相关信息按最底层列顺序放入list
	 *
	 * @param listInfo
	 *            单元格信息集合
	 * @param cols
	 * @param flag
	 * @return
	 */
	public String  anayzeColTable3(ArrayList listInfo,int cols,int rowBotPix,int colRigPix,int width,HashMap rowMap,ArrayList colInfoBGrid)
	{
		StringBuffer colSerialNo=new StringBuffer("");
		int a_rowBotPix=rowBotPix;
		int b=0;
		for(int i=0;i<cols;i++)
		{

			for(Iterator t=listInfo.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				if(vo.getInt("rtop")==a_rowBotPix&&vo.getInt("rleft")+vo.getInt("rwidth")==colRigPix)
				{

					colInfoBGrid.add(vo);
					if(vo.getInt("flag1")!=4)
					{
						b++;
						rowMap.put(String.valueOf(b),String.valueOf(i));
					}
					else
					{
						colSerialNo.append(i+",");
					}
					a_rowBotPix=vo.getInt("rtop")+vo.getInt("rheight");
					break;
				}
			}
		}
		return colSerialNo.toString();

	}























	/**
	 * 分析报表，将横表栏的相关信息按最底层列顺序放入list
	 *
	 * @param listInfo
	 *            单元格信息集合
	 * @param rows
	 * @param flag
	 * @return
	 */
	public ArrayList anayzeRowTable(ArrayList listInfo,int rows,int rowBotPix,int colRigPix,int height,HashMap colMap,ArrayList rowInfoBGrid)
	{
		ArrayList all_list=new ArrayList();
		ArrayList list=new ArrayList();
		int a_colRigPix=colRigPix;
		RecordVo tempVo=null;   				   	// 临时对象；存放倒数第2层单元格
		int b=0;
		StringBuffer rowSerialNo=new StringBuffer("");
		for(int i=0;i<rows;i++)
		{
			String rowterm="";     				    // 横条件
			RecordVo    gridNo=null;	            // 最底层的单元格
			ArrayList rowsColList=new ArrayList();	// 找到第（i+1）列的单元格
			for(Iterator t=listInfo.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				if(vo.getInt("rleft")==a_colRigPix)
				{
					if(vo.getInt("rtop")+vo.getInt("rheight")==rowBotPix)
					{
						gridNo=vo;
						rowInfoBGrid.add(vo);
						if(vo.getInt("flag1")!=4)
						{
							b++;
							colMap.put(String.valueOf(b),String.valueOf(i));
						}
						else
						{
							rowSerialNo.append(i+",");
						}
					}
					rowsColList.add(vo);
				}
			}
			// 写入
			ArrayList itemList=new ArrayList();			// 单元格分析信息集合
			if(rowsColList.size()==1)
			{
				RecordVo vo=(RecordVo)rowsColList.get(0);
				int a=0;
				if(tempVo!=null&&(tempVo.getInt("rtop")+tempVo.getInt("rheight"))==vo.getInt("rtop")&&(vo.getInt("rleft")>=tempVo.getInt("rleft")&&(vo.getInt("rleft")+vo.getInt("rwidth"))<=(tempVo.getInt("rleft")+tempVo.getInt("rwidth"))))
				{
					//		itemList.add(getGridAnayzeInfo(tempVo));
					a++;
				}
				// 碰到特殊格式时，找到其次底层条件
//						if(vo.getInt("rheight")!=height&&a==0)
//						{
//							for(Iterator tt=listInfo.iterator();tt.hasNext();)
//							{
//								RecordVo a_vo=(RecordVo)tt.next();
//								if((a_vo.getInt("rtop")+a_vo.getInt("rheight"))==vo.getInt("rtop")&&(vo.getInt("rleft")>=a_vo.getInt("rleft")&&(vo.getInt("rleft")+vo.getInt("rwidth"))<=(a_vo.getInt("rleft")+a_vo.getInt("rwidth"))))
//								{
//									itemList.add(getGridAnayzeInfo(a_vo));
//									break;
//								}
//							}
//						}						
				//	itemList.add(getGridAnayzeInfo(vo));
				//递归循环 tempVo 谢桂权 解决多层表头
				tempVo=gridNo;
				getTempVo(listInfo, itemList, tempVo,"1");
				itemList.add(getGridAnayzeInfo(gridNo));
				a_colRigPix=vo.getInt("rleft")+vo.getInt("rwidth");
			}
			else
			{
				for(Iterator t=rowsColList.iterator();t.hasNext();)
				{
					RecordVo vo=(RecordVo)t.next();
					if((vo.getInt("rtop")+vo.getInt("rheight"))==gridNo.getInt("rtop")&&(gridNo.getInt("rleft")>=vo.getInt("rleft")&&(gridNo.getInt("rleft")+gridNo.getInt("rwidth"))<=(vo.getInt("rleft")+vo.getInt("rwidth"))))
					{
						itemList.add(getGridAnayzeInfo(vo));
						tempVo=vo;
						//递归循环 tempVo 谢桂权 解决多层表头
						getTempVo(listInfo, itemList, tempVo,"1");
					}
				}
				a_colRigPix=gridNo.getInt("rleft")+gridNo.getInt("rwidth");
				itemList.add(getGridAnayzeInfo(gridNo));
			}
			list.add(itemList);
		}
		all_list.add(list);
		all_list.add(rowSerialNo.toString());

		return  all_list;
	}



	/**
	 * 分析报表，将纵表栏的相关信息按最底层列顺序放入list
	 *
	 * @param listInfo
	 *            单元格信息集合
	 * @param cols
	 * @param flag
	 * @return
	 */
	public ArrayList anayzeColTable(ArrayList listInfo,int cols,int rowBotPix,int colRigPix,int width,HashMap rowMap,ArrayList colInfoBGrid)
	{
		ArrayList all_list=new ArrayList();
		ArrayList list=new ArrayList();
		int a_rowBotPix=rowBotPix;
		RecordVo tempVo=null;                       // 临时对象；存放倒数第2层单元格
		int b=0;
		StringBuffer colSerialNo=new StringBuffer("");
		for(int i=0;i<cols;i++)
		{
			String rowterm="";     				  	// 横条件
			RecordVo    gridNo=null;	 		    // 最底层的单元格
			ArrayList rowsColList=new ArrayList();	// 找到第（i+1）行的单元格
			for(Iterator t=listInfo.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				if(vo.getInt("rtop")==a_rowBotPix&&vo.getInt("rleft")+vo.getInt("rwidth")==colRigPix)
				{

					gridNo=vo;
					colInfoBGrid.add(vo);
					if(vo.getInt("flag1")!=4)
					{
						b++;
						rowMap.put(String.valueOf(b),String.valueOf(i));
					}
					else
					{
						colSerialNo.append(i+",");
					}
					break;
				}
			}

			for(Iterator t=listInfo.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				if(gridNo != null){   //add by wangchaoqun on 2014-9-19 加条件判断防止空指针异常
					if(vo.getInt("rleft")+vo.getInt("rwidth")==gridNo.getInt("rleft"))
					{
						rowsColList.add(vo);
					}
				}
			}

			// 写入
			ArrayList itemList=new ArrayList();		// 单元格分析信息集合
			if(rowsColList.size()==1)
			{

				RecordVo vo=(RecordVo)rowsColList.get(0);
				int a=0;
				if(tempVo!=null&&(tempVo.getInt("rleft")+tempVo.getInt("rwidth"))==vo.getInt("rleft")&&(vo.getInt("rtop")>=tempVo.getInt("rtop")&&(vo.getInt("rtop")+vo.getInt("rheight"))<=(tempVo.getInt("rtop")+tempVo.getInt("rheight"))))
				{
					//		itemList.add(getGridAnayzeInfo(tempVo));
					a++;
				}
// //碰到特殊格式时，找到其次底层条件
//						if(vo.getInt("rwidth")!=width&&a==0)
//						{
//							for(Iterator tt=listInfo.iterator();tt.hasNext();)
//							{
//								RecordVo a_vo=(RecordVo)tt.next();
//								if((a_vo.getInt("rleft")+a_vo.getInt("rwidth"))==vo.getInt("rleft")&&(vo.getInt("rtop")>=a_vo.getInt("rtop")&&(vo.getInt("rtop")+vo.getInt("rheight"))<=(a_vo.getInt("rtop")+a_vo.getInt("rheight"))))
//								{
//									itemList.add(getGridAnayzeInfo(a_vo));
//									break;
//								}
//							}
//						}									
				//				itemList.add(getGridAnayzeInfo(vo));
				tempVo=gridNo;
				//递归循环 tempVo 谢桂权 解决多层表头
				getTempVo(listInfo, itemList, tempVo,"0");
				itemList.add(getGridAnayzeInfo(gridNo));	//	2006/09/19 添加
				a_rowBotPix=gridNo.getInt("rtop")+gridNo.getInt("rheight");		//	2006/09/19 修改
			}
			else
			{
				//System.out.println("gridno: left width top height --"+gridNo.getInt("gridno")+"*"+gridNo.getInt("rleft")+"/"+gridNo.getInt("rwidth")+"/"+gridNo.getInt("rtop")+"/"+gridNo.getInt("rheight"));
				for(Iterator t=rowsColList.iterator();t.hasNext();)
				{
					RecordVo vo=(RecordVo)t.next();
					//	if((vo.getInt("rleft")+vo.getInt("rwidth"))==gridNo.getInt("rleft")&&(gridNo.getInt("rtop")>=vo.getInt("rtop")&&(gridNo.getInt("rtop")+gridNo.getInt("rheight"))<=(vo.getInt("rtop")+vo.getInt("rheight"))))
					//	System.out.println("vo: left width top height --"+vo.getInt("gridno")+"*"+vo.getInt("rleft")+"/"+vo.getInt("rwidth")+"/"+vo.getInt("rtop")+"/"+vo.getInt("rheight"));
					if((vo.getInt("rleft")+vo.getInt("rwidth"))==gridNo.getInt("rleft")&&(gridNo.getInt("rtop")>=vo.getInt("rtop")&&(gridNo.getInt("rtop")+gridNo.getInt("rheight"))<=(vo.getInt("rtop")+vo.getInt("rheight"))))
					{
						itemList.add(getGridAnayzeInfo(vo));
						tempVo=vo;
						//递归循环 tempVo 谢桂权 解决多层表头
						getTempVo(listInfo, itemList, tempVo,"0");
					}
				}
				if(gridNo != null){ //add by wangchaoqun on 2014-9-19 加条件判断防止空指针异常
					a_rowBotPix=gridNo.getInt("rtop")+gridNo.getInt("rheight");
					itemList.add(getGridAnayzeInfo(gridNo));
				}
			}
			list.add(itemList);
		}
		all_list.add(list);
		all_list.add(colSerialNo.toString());
		return  all_list;
	}

	public void getTempVo(ArrayList listInfo,ArrayList list,RecordVo gridNo , String flag){
		for(Iterator t=listInfo.iterator();t.hasNext();)
		{
			RecordVo vo=(RecordVo)t.next();
			if("0".equals(flag)){ //col
				if((vo.getInt("rleft")+vo.getInt("rwidth"))==gridNo.getInt("rleft")&&(gridNo.getInt("rtop")>=vo.getInt("rtop")&&(gridNo.getInt("rtop")+gridNo.getInt("rheight"))<=(vo.getInt("rtop")+vo.getInt("rheight"))))
				{
					list.add(getGridAnayzeInfo(vo));
					gridNo=vo;
					getTempVo(listInfo, list, gridNo,"0");
				}
			}else{			//row
				if((vo.getInt("rtop")+vo.getInt("rheight"))==gridNo.getInt("rtop")&&(gridNo.getInt("rleft")>=vo.getInt("rleft")&&(gridNo.getInt("rleft")+gridNo.getInt("rwidth"))<=(vo.getInt("rleft")+vo.getInt("rwidth"))))
				{
					list.add(getGridAnayzeInfo(vo));
					gridNo=vo;
					getTempVo(listInfo, list, gridNo,"1");
				}
			}
		}
	}


	int[] a_itemArea=null;  //

	/**
	 * 取某单元格条件信息
	 * str[0]:scanmode    扫描库范围    1:扫描人员库, 2:扫描单位库, 3:扫描单位和部门, 4:扫描部门, 5:扫描职位, 6:历史时点
	 * str[1]:flag1       取值方式标志  1:取值, 2:统计个数, 3:表达式, 4:求序号
	 * str[2]:flag2       取值方式标志  1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数
	 * str[3]:cfactor     条件因子
	 * str[4]:cexpr1      条件表达式
	 * str[5]:cexpr2      运算表达式
	 * str[6]:gridno      单元格号
	 * str[7]:nprecent    小数位数
	 * str[8]:hz          汉字描述
	 * @param tempVo
	 * @return
	 */
	public String[] getGridAnayzeInfo(RecordVo tempVo)
	{
		String[] str=new String[10];

		try
		{
			str[0]=String.valueOf(tempVo.getInt("scanmode"));  // 扫描库范围
			if("0".equals(String.valueOf(tempVo.getInt("flag1")))) {
                str[1]="2";	  // 取值方式标志 xgq 兼容错误情况
            } else {
                str[1]=String.valueOf(tempVo.getInt("flag1"));	  // 取值方式标志
            }
			str[2]=String.valueOf(tempVo.getInt("flag2"));	  // 取值方法标志
			str[3]=tempVo.getString("cfactor");
			str[4]=tempVo.getString("cexpr1");
			str[5]=tempVo.getString("cexpr2");
			str[6]=String.valueOf(tempVo.getInt("gridno"));
			str[7]=String.valueOf(tempVo.getInt("npercent"));

			str[8]=tempVo.getString("hz");//add by xiegh on date 20171129 处理  :列头有因子但是没有表达式时  这时将因子置空，避免在合并条件时 报：数组下标越界
			if(!"".equals(tempVo.getString("cexpr1"))&&"".equals(tempVo.getString("cfactor"))) {
                str[4] = "";
            }

			if(a_itemArea==null) {
                a_itemArea=getItemGridID(String.valueOf(tempVo.getInt("tabid")));				//得到项目栏区域 l,t,w,h
            }
			str[9]="0";
			if(tempVo.getInt("flag1")==4)
			{
				if(tempVo.getInt("flag")==1)
				{
					if(tempVo.getInt("rtop")+tempVo.getInt("rheight")==a_itemArea[1]+a_itemArea[3]) {
                        str[9]="1";
                    }
				}
				else if(tempVo.getInt("flag")==2)
				{
					if(tempVo.getInt("rwidth")+tempVo.getInt("rleft")==a_itemArea[0]+a_itemArea[2]) {
                        str[9]="1";
                    }
				}

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}



		return str;
	}




	/**
	 * 得到报表条件因子里包含的字段标识(分人员库，单位库，职位库 用于反查)
	 *
	 * @param rowInfoList
	 *            得到报表横表栏的相关信息集合
	 * @param colInfoList
	 *            得到报表纵表栏的相关信息集合
	 * @param midVariableList
	 *            报表临时变量集合
	 * @param tableTermsMap
	 *            表条件
	 * @return
	 */
	public ArrayList getFactor2(ArrayList gridList,ArrayList a_rowInfoList,ArrayList a_colInfoList,ArrayList midVariableList,HashMap tableTermsMap,String scanMode)throws GeneralException
	{
		ArrayList list=new ArrayList();
		HashSet aSet=new HashSet();   		 // 人员库包含的指标
		HashSet aVariableSet=new HashSet();  // 人员库包含的临时变量
		HashSet bSet=new HashSet();   		 // 单位库包含的指标
		HashSet bVariableSet=new HashSet();  // 单位库包含的临时变量
		HashSet kSet=new HashSet();  		 // 职位库包含的指标
		HashSet kVariableSet=new HashSet();  // 职位库包含的临时变量
		boolean aflag = false;
		boolean bflag = false;
		boolean kflag = false;
		try
		{

			HashSet rowSet=new HashSet();
			HashSet variableSet=new HashSet();
			boolean isr=false;  // 是否扫描人员库
			boolean isd=false;  // 是否扫描单位库
			boolean isb=false;  // 是否扫描部门库
			boolean isz=false;  // 是否扫描职位库
			boolean isbd=false; // 是否扫描部门、单位库
			boolean  is_noDefine=false;  //是否有未定义的格 默认为的人

			for(int j=0;j<a_rowInfoList.size();j++)
			{
				String[] temp0=(String[])a_rowInfoList.get(j);
				if("4".equals(temp0[1])) //编号过滤
                {
                    continue;
                }

				if("1".equals(temp0[0])) {
                    isr=true;
                } else if("2".equals(temp0[0])) {
                    isd=true;
                } else if("3".equals(temp0[0])) {
                    isbd=true;
                } else if("4".equals(temp0[0])) {
                    isb=true;
                } else if("5".equals(temp0[0])) {
                    isz=true;
                }
				if("2".equals(temp0[1])&& "1".equals(temp0[2])&&temp0[4].trim().length()==0&&temp0[5].trim().length()==0) {
                    continue;
                }
				addFactors(rowSet,variableSet,temp0,midVariableList); // 添加报表条件因子和临时变量
			}
			if((isd==true||isbd==true||isb==true||isz==true)&&is_noDefine==true){
				isr=false;
				is_noDefine=false;
			}
			HashSet colSet=new HashSet();
			HashSet colVariableSet=new HashSet();
			boolean c_isr=false;  // 是否扫描人员库
			boolean c_isd=false;  // 是否扫描单位库
			boolean c_isb=false;  // 是否扫描部门库
			boolean c_isz=false;  // 是否扫描职位库
			boolean c_isbd=false; // 是否扫描部门、单位库
			for(int j=0;j<a_colInfoList.size();j++)
			{
				String[] temp0=(String[])a_colInfoList.get(j);

				if("4".equals(temp0[1])) //编号过滤
                {
                    continue;
                }

				if("1".equals(temp0[0])) {
                    c_isr=true;
                } else if("2".equals(temp0[0])) {
                    c_isd=true;
                } else if("3".equals(temp0[0])) {
                    c_isbd=true;
                } else if("4".equals(temp0[0])) {
                    c_isb=true;
                } else if("5".equals(temp0[0])) {
                    c_isz=true;
                }
				if("2".equals(temp0[1])&& "1".equals(temp0[2])&&temp0[4].trim().length()==0&&temp0[5].trim().length()==0) {
                    continue;
                }
				addFactors(colSet,colVariableSet,temp0,midVariableList); // 添加报表条件因子和临时变量
			}
			if((c_isd==true||c_isbd==true||c_isb==true||c_isz==true)&&is_noDefine==true){
				c_isr=false;
				is_noDefine=false;
			}
			aflag =false;
			bflag =false;
			kflag =false;
			if(isr||c_isr||is_noDefine)
			{
				addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
				aflag=true;
			}
			else if(isz||c_isz)
			{
//						addFactorsTemp(kSet,kVariableSet,rowSet,variableSet,colSet,colVariableSet);
//                        addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
				kflag=true;
			}
			else if(isb||c_isbd||isd||c_isd||isbd||c_isb)
			{
//						addFactorsTemp(bSet,bVariableSet,rowSet,variableSet,colSet,colVariableSet);
//                        addFactorsTemp(kSet,kVariableSet,rowSet,variableSet,colSet,colVariableSet);
//                        addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
				bflag=true;
			}else{
				addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);//历史时点走人员
				aflag=true;
			}
			// 如果有表条件，则整个表扫描的都是人员库
			//	if(tableTermsMap.size()>0)
			//	{
			//		addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
			//	}
			//	else
			//	{
			if("1".equals(scanMode) )
			{
				addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
				//增加临时变量
				int mid=0;
				for (int i = 0; i < dbList.size(); i++) {
					String pre = (String) dbList.get(i);
					String tableTermCondition=(String)tableTermsMap.get(pre);

					if(tableTermCondition!=null&&tableTermCondition.length()>0)
					{
						if(mid==0){
							String tableName="t#"+this.userview.getUserName()+"_tjb_A";;
							for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
							{
								RecordVo vo=(RecordVo)sub_t.next();
								//if(temp.equals(vo.getString("cname").trim()))
								if(tableTermCondition.toLowerCase().indexOf(tableName.toLowerCase()+"."+vo.getString("cname").trim().toLowerCase())!=-1)
								{
									aVariableSet.add(vo.getString("cname").trim());
								}
							}
						}
						mid++;
					}
				}
			}
			else if("5".equals(scanMode) )
			{
				addFactorsTemp(kSet,kVariableSet,rowSet,variableSet,colSet,colVariableSet);
				kflag=true;
			}
			else if("2".equals(scanMode) || "3".equals(scanMode) || "4".equals(scanMode))
			{

				addFactorsTemp(bSet,bVariableSet,rowSet,variableSet,colSet,colVariableSet);
				bflag=true;
			}
			else
			{

				addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
				//增加临时变量
				int mid=0;
				for (int i = 0; i < dbList.size(); i++) {
					String pre = (String) dbList.get(i);
					String tableTermCondition=(String)tableTermsMap.get(pre);

					if(tableTermCondition!=null&&tableTermCondition.length()>0)
					{
						if(mid==0){
							String tableName="t#"+this.userview.getUserName()+"_tjb_A";;
							for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
							{
								RecordVo vo=(RecordVo)sub_t.next();
								//if(temp.equals(vo.getString("cname").trim()))
								if(tableTermCondition.toLowerCase().indexOf(tableName.toLowerCase()+"."+vo.getString("cname").trim().toLowerCase())!=-1)
								{
									aVariableSet.add(vo.getString("cname").trim());
								}
							}
						}
						mid++;
					}
				}
			}
			//	}
			//获得项目上的临时变量
			for(int j=0;j<gridList.size();j++)
			{
				RecordVo vo = (RecordVo)gridList.get(j);
				if(vo!=null&&"0".equals(vo.getString("flag"))){
					String temp[]=new String[10];
					temp[3]=vo.getString("cfactor");
					if(aflag) {
                        addFactor(aSet,temp,aVariableSet,midVariableList);
                    }
					if(bflag) {
                        addFactor(bSet,temp,bVariableSet,midVariableList);
                    }
					if(kflag) {
                        addFactor(kSet,temp,kVariableSet,midVariableList);
                    }
					break;

				}
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		list.add(aSet);
		list.add(aVariableSet);
		list.add(bSet);
		list.add(bVariableSet);
		list.add(kSet);
		list.add(kVariableSet);

		return list;
	}







	/**
	 * 得到报表条件因子里包含的字段标识(分人员库，单位库，职位库)
	 *
	 * @param tabid
	 *            表号
	 * @param gridList
	 *            报表中横表栏和纵表栏所有单元格的信息集合
	 * @param rowInfoList
	 *            得到报表横表栏的相关信息集合
	 * @param colInfoList
	 *            得到报表纵表栏的相关信息集合
	 * @param midVariableList
	 *            报表临时变量集合
	 * @param tableTermsMap
	 *            表条件
	 * @return
	 */
	public ArrayList getFactor(String tabid,ArrayList gridList,ArrayList rowInfoList,ArrayList colInfoList,ArrayList midVariableList,HashMap tableTermsMap)throws GeneralException
	{
		ArrayList list=new ArrayList();
		HashSet aSet=new HashSet();   		 // 人员库包含的指标
		HashSet aVariableSet=new HashSet();  // 人员库包含的临时变量
		HashSet bSet=new HashSet();   		 // 单位库包含的指标
		HashSet bVariableSet=new HashSet();  // 单位库包含的临时变量
		HashSet kSet=new HashSet();  		 // 职位库包含的指标
		HashSet kVariableSet=new HashSet();  // 职位库包含的临时变量
		boolean aflag =false;
		boolean bflag =false;
		boolean kflag =false;
		try
		{
			for(int i=0;i<rowInfoList.size();i++)
			{
				ArrayList a_rowInfoList=(ArrayList)rowInfoList.get(i);
				HashSet rowSet=new HashSet();
				HashSet variableSet=new HashSet();
				boolean isr=false;  // 是否扫描人员库
				boolean isd=false;  // 是否扫描单位库
				boolean isb=false;  // 是否扫描部门库
				boolean isz=false;  // 是否扫描职位库
				boolean isbd=false; // 是否扫描部门、单位库
				boolean  is_noDefine=false;  //是否有未定义的格 默认为的人
				for(int j=0;j<a_rowInfoList.size();j++)
				{
					String[] temp0=(String[])a_rowInfoList.get(j);

					if("4".equals(temp0[1])) //编号过滤
                    {
                        continue;
                    }
					if("2".equals(temp0[1])&& "1".equals(temp0[2])&&temp0[4].trim().length()==0&&temp0[5].trim().length()==0){
						if("1".equals(temp0[0])) {
                            is_noDefine=true;
                        }
						continue;
					}
					if("1".equals(temp0[0])) {
                        isr=true;
                    } else if("2".equals(temp0[0])) {
                        isd=true;
                    } else if("3".equals(temp0[0])) {
                        isbd=true;
                    } else if("4".equals(temp0[0])) {
                        isb=true;
                    } else if("5".equals(temp0[0])) {
                        isz=true;
                    }
					addFactors(rowSet,variableSet,temp0,midVariableList); // 添加报表条件因子和临时变量
				}
				if((isd==true||isbd==true||isb==true||isz==true)&&is_noDefine==true){
					isr=false;
					is_noDefine=false;
				}
				for(int e=0;e<colInfoList.size();e++)
				{
					ArrayList a_colInfoList=(ArrayList)colInfoList.get(e);
					HashSet colSet=new HashSet();
					HashSet colVariableSet=new HashSet();
					boolean c_isr=false;  // 是否扫描人员库
					boolean c_isd=false;  // 是否扫描单位库
					boolean c_isb=false;  // 是否扫描部门库
					boolean c_isz=false;  // 是否扫描职位库
					boolean c_isbd=false; // 是否扫描部门、单位库
					for(int j=0;j<a_colInfoList.size();j++)
					{
						String[] temp0=(String[])a_colInfoList.get(j);

						if("4".equals(temp0[1])) //编号过滤
                        {
                            continue;
                        }
						if("2".equals(temp0[1])&& "1".equals(temp0[2])&&temp0[4].trim().length()==0&&temp0[5].trim().length()==0){
							if("1".equals(temp0[0])) {
                                is_noDefine=true;
                            }
							continue;
						}
						if("1".equals(temp0[0])) {
                            c_isr=true;
                        } else if("2".equals(temp0[0])) {
                            c_isd=true;
                        } else if("3".equals(temp0[0])) {
                            c_isbd=true;
                        } else if("4".equals(temp0[0])) {
                            c_isb=true;
                        } else if("5".equals(temp0[0])) {
                            c_isz=true;
                        }
						addFactors(colSet,colVariableSet,temp0,midVariableList); // 添加报表条件因子和临时变量
					}
					if((c_isd==true||c_isbd==true||c_isb==true||c_isz==true)&&is_noDefine==true){
						c_isr=false;
						is_noDefine=false;
					}
					aflag =false;
					bflag =false;
					kflag =false;
					if(isr||c_isr||is_noDefine)
					{
						addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
						aflag=true;
					}
					else if(isz||c_isz)
					{
						addFactorsTemp(kSet,kVariableSet,rowSet,variableSet,colSet,colVariableSet);
						addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
						kflag=true;
					}
					else if(isb||c_isbd||isd||c_isd||isbd||c_isb)
					{
						addFactorsTemp(bSet,bVariableSet,rowSet,variableSet,colSet,colVariableSet);
						addFactorsTemp(kSet,kVariableSet,rowSet,variableSet,colSet,colVariableSet);
						addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);
						bflag=true;
					}else{
						addFactorsTemp(aSet,aVariableSet,rowSet,variableSet,colSet,colVariableSet);//历史时点走人员
						aflag=true;
					}
				}

			}
			//获得项目上的临时变量
			for(int j=0;j<gridList.size();j++)
			{
				RecordVo vo = (RecordVo)gridList.get(j);
				if(vo!=null&&"0".equals(vo.getString("flag"))){
					String temp[]=new String[10];
					temp[3]=vo.getString("cfactor");
					if(aflag) {
                        addFactor(aSet,temp,aVariableSet,midVariableList);
                    }
					if(bflag) {
                        addFactor(bSet,temp,bVariableSet,midVariableList);
                    }
					if(kflag) {
                        addFactor(kSet,temp,kVariableSet,midVariableList);
                    }
					break;

				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		//考虑统计函数优化  统计 工资标准l = 工资标准小计,奖励工资l=奖励工资小计,津贴补助l =津贴补助小计。。。。。。
		ArrayList varList=new ArrayList();
		for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
		{
			RecordVo vo=(RecordVo)sub_t.next();
			String cname=vo.getString("cname");
			String cvalue=vo.getString("cvalue");
			String chz=vo.getString("chz");
			varList.add(cname);
			varList.add(chz);
		}
		for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
		{
			RecordVo vo=(RecordVo)sub_t.next();
			String cname=vo.getString("cname");
			String cvalue=vo.getString("cvalue");
			if(!aVariableSet.contains(cname)&&cvalue.indexOf("统计")!=-1)
			{
				boolean isValue=false;
				for(int i=0;i<varList.size();i++)
				{
					String temp_value=(String)varList.get(i);
					if(cvalue.indexOf(temp_value)!=-1)
					{
						isValue=true;
						break;
					}
				}
				if(isValue)
				{
					aVariableSet.add(cname);
					continue;
				}

			}
			else if(aVariableSet.contains(cname)&&cvalue.indexOf("统计")!=-1) //统计函数优化含其它临时变量，也得按被引用补充上 20170908 xiegh bug:31296
			{
				for(Iterator sub_t2=midVariableList.iterator();sub_t2.hasNext();)
				{
					RecordVo sub_vo=(RecordVo)sub_t2.next();
					String _cname=sub_vo.getString("cname");
					String _chz=sub_vo.getString("chz");
					if(cvalue.indexOf(_cname)!=-1||cvalue.indexOf(_chz)!=-1)
					{
						if(!aVariableSet.contains(_cname)) {
                            aVariableSet.add(_cname);
                        }
					}
				}


			}

		}


		list.add(aSet);
		list.add(aVariableSet);
		list.add(bSet);
		list.add(bVariableSet);
		list.add(kSet);
		list.add(kVariableSet);

		return list;
	}




	public void addFactorsTemp(HashSet set,HashSet variableSet,HashSet t_set,HashSet t_variableSet,HashSet t_colSet,HashSet t_colVariableSet)
	{
		for(Iterator t=t_set.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			set.add(temp);
		}
		for(Iterator t=t_variableSet.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			variableSet.add(temp);

		}
		for(Iterator t=t_colSet.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			set.add(temp);

		}
		for(Iterator t=t_colVariableSet.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			variableSet.add(temp);

		}
	}



	/**
	 * 添加报表条件因子和临时变量
	 *
	 * @param rowSet
	 * @param variableSet
	 * @param temp0
	 */
	public void addFactors(HashSet rowSet,HashSet variableSet,String[] temp0,ArrayList midVariableList)
	{
		if("1".equals(temp0[1]))			// 取值
		{
			if(!"5".equals(temp0[2]))
			{
				ArrayList fieldORvaribleList=exprUtil.statExprAnalyse(getCexpr2Context(1,temp0[5]),midVariableList);
				ArrayList fieldList=(ArrayList)fieldORvaribleList.get(0);      // 指标集和
				ArrayList variableList=(ArrayList)fieldORvaribleList.get(1);   // 临时变量集合
				for(Iterator t1=fieldList.iterator();t1.hasNext();)
				{
					String temp=(String)t1.next();
					rowSet.add(temp);
				}
				for(Iterator t2=variableList.iterator();t2.hasNext();)
				{
					String temp=(String)t2.next();
					variableSet.add(temp);
				}

				addFactor(rowSet,temp0,variableSet,midVariableList);
			}
			else
			{
				addFactor(rowSet,temp0,variableSet,midVariableList);
			}
		}
		else if("2".equals(temp0[1]))	// 统计个数
		{
			addFactor(rowSet,temp0,variableSet,midVariableList);
		}

	}



	/**
	 * 添加报表取值方式为统计个数的条件因子
	 *
	 * @param rowSet
	 * @param temp0
	 */
	public void addFactor(HashSet rowSet,String[] temp0,HashSet variableSet,ArrayList midVariableList)
	{
		String temp=temp0[3];
		if(temp!=null&&temp.length()>0)
		{
			String[] tempArr=temp.split("`");
			for(int ii=0;ii<tempArr.length;ii++)
			{

				int a=0;
				String subTemp=tempArr[ii];
				if(subTemp.indexOf("=")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))&&!">".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))) {
                    a=subTemp.indexOf("=");
                } else if(subTemp.indexOf(">")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf(">")-1,subTemp.indexOf(">")))&&!"=".equals(subTemp.substring(subTemp.indexOf(">")+1,subTemp.indexOf(">")+2))) {
                    a=subTemp.indexOf(">");
                } else if(subTemp.indexOf("<")!=-1&&!"=".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))&&!">".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))) {
                    a=subTemp.indexOf("<");
                } else if(subTemp.indexOf(">=")!=-1) {
                    a=subTemp.indexOf(">=");
                } else if(subTemp.indexOf("<=")!=-1) {
                    a=subTemp.indexOf("<=");
                } else if(subTemp.indexOf("<>")!=-1) {
                    a=subTemp.indexOf("<>");
                }
				if(a!=0)
				{
					String factor=subTemp.substring(0,a);
					boolean isVariable=false;
					for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
					{
						RecordVo vo=(RecordVo)sub_t.next();
						//if(temp.equals(vo.getString("cname").trim()))
						if(factor.equals(vo.getString("cname").trim()))
						{
							isVariable=true;
							variableSet.add(vo.getString("cname"));
							break;
						}
					}
					if(!isVariable) {
                        rowSet.add(subTemp.substring(0,a));
                    }
				}
			}
		}
	}




	public HashSet getFactorSet(String expr)
	{
		HashSet set=new HashSet();
		String[] tempArr=expr.split("`");
		for(int ii=0;ii<tempArr.length;ii++)
		{

			int a=0;
			String subTemp=tempArr[ii];
			if(subTemp.indexOf("=")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))&&!">".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))) {
                a=subTemp.indexOf("=");
            } else if(subTemp.indexOf(">")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf(">")-1,subTemp.indexOf(">")))&&!"=".equals(subTemp.substring(subTemp.indexOf(">")+1,subTemp.indexOf(">")+2))) {
                a=subTemp.indexOf(">");
            } else if(subTemp.indexOf("<")!=-1&&!"=".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))&&!">".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))) {
                a=subTemp.indexOf("<");
            } else if(subTemp.indexOf(">=")!=-1) {
                a=subTemp.indexOf(">=");
            } else if(subTemp.indexOf("<=")!=-1) {
                a=subTemp.indexOf("<=");
            } else if(subTemp.indexOf("<>")!=-1) {
                a=subTemp.indexOf("<>");
            }
			if(a!=0)
			{
				String factor=subTemp.substring(0,a);
				set.add(factor);
			}
		}
		return set;
	}









	// 判断是否存在统计结果表，如果没有则产生一个
	public void execute_TT_table(String tabid,int cols)throws GeneralException
	{

		try
		{
			Table table=new Table("tt_"+tabid);
			ArrayList fieldList=getTT_TableFields(cols);
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				Field temp=(Field)t.next();
				table.addField(temp);
			}
			//	table.setCreatekey(false);
			DbWizard dbWizard=new DbWizard(this.conn);
			if(!dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.createTable(table);
				if(this.dbmodel==null) {
                    this.dbmodel=new DBMetaModel(this.conn);
                }
				this.dbmodel.reloadTableModel("tt_"+tabid);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}
	}
	// 判断是否存在编辑报表，如果没有则产生一个
	public void execute_TB_table(String tabid,int cols)throws GeneralException
	{

		try
		{
			Table table=new Table("tb"+tabid);
			DbWizard dbWizard=new DbWizard(this.conn);
			if(!dbWizard.isExistTable(table.getName(),false))
			{
				ArrayList fieldList=getTB_TableFields(cols);
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				dbWizard.createTable(table);
				if(this.dbmodel==null) {
                    this.dbmodel=new DBMetaModel(this.conn);
                }
				this.dbmodel.reloadTableModel("tb"+tabid);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}
	}


	// 得到结果表中列的集合
	public ArrayList getTB_TableFields(int cols)
	{

		ArrayList fieldsList=new ArrayList();
		//	fieldsList.add(getField1("UserName",ResourceFactory.getProperty("lable.welcome.invtextresult.username"),"DataType.STRING",50));

		Field temp20=new Field("UserName",ResourceFactory.getProperty("lable.welcome.invtextresult.username"));
		temp20.setDatatype(DataType.STRING);
		temp20.setNullable(false);
		temp20.setKeyable(true);
		temp20.setVisible(false);
		temp20.setLength(50);

		Field temp21=new Field("secid",ResourceFactory.getProperty("ttOrganization.record.secid"));
		temp21.setDatatype(DataType.INT);
		temp21.setNullable(false);
		temp21.setKeyable(true);
		temp21.setVisible(false);
		fieldsList.add(temp20);
		fieldsList.add(temp21);
		for(int i=0;i<cols;i++)
		{
			String fieldname="C"+(i+1);
			Field obj=getField2(fieldname,fieldname,"N");
			fieldsList.add(obj);
		}
		return fieldsList;
	}


	/**
	 * 得到统计结果表中列的集合
	 *
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	public ArrayList getTT_TableFields(int cols)
	{

		ArrayList fieldsList=new ArrayList();
		Field temp20=getField1("unitcode",ResourceFactory.getProperty("ttOrganization.unit.unitcode"),"DataType.STRING",30);
		temp20.setNullable(false);
		temp20.setKeyable(true);
		fieldsList.add(temp20);
		Field temp21=new Field("secid",ResourceFactory.getProperty("ttOrganization.record.secid"));
		temp21.setDatatype(DataType.INT);
		temp21.setNullable(false);
		temp21.setKeyable(true);
		temp21.setVisible(false);
		fieldsList.add(temp21);

		for(int i=0;i<cols;i++)
		{
			String fieldname="C"+(i+1);
			Field obj=getField2(fieldname,fieldname,"N");
			fieldsList.add(obj);
		}
		return fieldsList;
	}





	/**
	 * 根据条件标识创建临时表
	 *
	 * @param factorSet
	 * @return list:临时表列表项集合
	 */
	public ArrayList creatTempTable(ArrayList factorSet,String username,ArrayList midVariableList)throws GeneralException
	{
		HashSet aNameList=new HashSet();
		HashSet aVarList=new HashSet();
		HashSet bNameList=new HashSet();
		HashSet bVarList=new HashSet();
		HashSet kNameList=new HashSet();
		HashSet kVarList=new HashSet();
		String    info="0";                      // 是否有指标没有构库
		ArrayList lists=new ArrayList();
		try
		{
			for(int i=0;i<factorSet.size()-1;i++)
			{
				HashSet aSet=(HashSet)factorSet.get(i++);
				createTable(username, i, aSet);
				if(aSet.size()==0)
				{
					if(((HashSet)factorSet.get(i)).size()==0) {
                        continue;
                    }
				}
				HashSet aVarSet=(HashSet)factorSet.get(i);
				Table table=null;
				int   flag=1;    // 1:人员库 2：单位库 3：职位库
				ArrayList fieldlist=new ArrayList();
				//	if((i-1)==0&&aSet.size()>=1)
				if((i-1)==0)
				{

					table=new Table("t#"+username+"_tjb_A");
					table.setCreatekey(false);
					flag=1;
					ArrayList list=getTableFields(aSet,aVarSet,flag,midVariableList,false);
					fieldlist=(ArrayList)list.get(0);
					aNameList=(HashSet)list.get(1);
					aVarList=(HashSet)list.get(2);
					info=(String)list.get(3);
				}
				//else if((i-1)==2&&aSet.size()>=1)
				else if((i-1)==2)
				{
					table=new Table("t#"+username+"_tjb_B");
					table.setCreatekey(false);
					flag=2;
					ArrayList list=getTableFields(aSet,aVarSet,flag,midVariableList,false);
					fieldlist=(ArrayList)list.get(0);
					bNameList=(HashSet)list.get(1);
					bVarList=(HashSet)list.get(2);
					info=(String)list.get(3);
				}
				//else if((i-1)==4&&aSet.size()>=1)
				else if((i-1)==4)
				{
					flag=3;
					table=new Table("t#"+username+"_tjb_K");
					table.setCreatekey(false);
					ArrayList list=getTableFields(aSet,aVarSet,flag,midVariableList,false);
					fieldlist=(ArrayList)list.get(0);
					kNameList=(HashSet)list.get(1);
					kVarList=(HashSet)list.get(2);
					info=(String)list.get(3);
				}


				if("0".equals(info))
				{
					for(int b=0;b<fieldlist.size();b++)
					{
						table.addField((Field)fieldlist.get(b));
					}

					DbWizard dbWizard=new DbWizard(this.conn);
					if(dbWizard.isExistTable(table.getName(),false))
					{
						dbWizard.dropTable(table);
					}
					dbWizard.createTable(table);
					if(this.dbmodel==null) {
                        this.dbmodel=new DBMetaModel(this.conn);
                    }
					this.dbmodel.reloadTableModel(table.getName());
				}
				else {
                    break;
                }
				aNameList.remove("create_date");
				bNameList.remove("create_date");
				kNameList.remove("create_date");
				aSet.remove("create_date");
				for(int b=0;b<fieldlist.size();b++)
				{
					Field field =(Field)fieldlist.get(b);
					if("create_date".equalsIgnoreCase(field.getCodesetid())){
						fieldlist.remove(b);
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		lists.add(aNameList);
		lists.add(aVarList);
		lists.add(bNameList);
		lists.add(bVarList);
		lists.add(kNameList);
		lists.add(kVarList);
		lists.add(info);
		return lists;
	}

	/**
	 * 无条件时根据scanMode创建相应临时表
	 *
	 * @param factorSet
	 * @return list:临时表列表项集合
	 * wangcq 2014-11-26
	 */
	public ArrayList creatTempTable(ArrayList factorSet,String username,ArrayList midVariableList,String scanMode)throws GeneralException
	{
		HashSet aNameList=new HashSet();
		HashSet aVarList=new HashSet();
		HashSet bNameList=new HashSet();
		HashSet bVarList=new HashSet();
		HashSet kNameList=new HashSet();
		HashSet kVarList=new HashSet();
		String    info="0";                      // 是否有指标没有构库
		ArrayList lists=new ArrayList();
		try
		{
			for(int i=0;i<factorSet.size()-1;i++)
			{
				HashSet aSet=(HashSet)factorSet.get(i++);
				createTable(username, i, aSet);

			}
			HashSet aSet=(HashSet)factorSet.get(0);
			HashSet aVarSet=(HashSet)factorSet.get(1);
			Table table=null;
			int   flag=1;    // 1:人员库 2：单位库 3：职位库
			ArrayList fieldlist=new ArrayList();
			if("1".equals(scanMode)){
				table=new Table("t#"+username+"_tjb_A");
				table.setCreatekey(false);
				flag=1;
				ArrayList list=getTableFields(aSet,aVarSet,flag,midVariableList,true);
				fieldlist=(ArrayList)list.get(0);
				aNameList=(HashSet)list.get(1);
				aVarList=(HashSet)list.get(2);
				info=(String)list.get(3);
			}else if("2".equals(scanMode) || "3".equals(scanMode) || "4".equals(scanMode)){
				table=new Table("t#"+username+"_tjb_B");
				table.setCreatekey(false);
				flag=2;
				ArrayList list=getTableFields(aSet,aVarSet,flag,midVariableList,true);
				fieldlist=(ArrayList)list.get(0);
				bNameList=(HashSet)list.get(1);
				bVarList=(HashSet)list.get(2);
				info=(String)list.get(3);
			}else if("5".equals(scanMode)){
				flag=3;
				table=new Table("t#"+username+"_tjb_K");
				table.setCreatekey(false);
				ArrayList list=getTableFields(aSet,aVarSet,flag,midVariableList,true);
				fieldlist=(ArrayList)list.get(0);
				kNameList=(HashSet)list.get(1);
				kVarList=(HashSet)list.get(2);
				info=(String)list.get(3);
			}
			if("0".equals(info))
			{
				for(int b=0;b<fieldlist.size();b++)
				{
					table.addField((Field)fieldlist.get(b));
				}

				DbWizard dbWizard=new DbWizard(this.conn);
				if(dbWizard.isExistTable(table.getName(),false))
				{
					dbWizard.dropTable(table);
				}
				dbWizard.createTable(table);
				if(this.dbmodel==null) {
                    this.dbmodel=new DBMetaModel(this.conn);
                }
				this.dbmodel.reloadTableModel(table.getName());
				aNameList.remove("create_date");
				bNameList.remove("create_date");
				kNameList.remove("create_date");
				aSet.remove("create_date");
				for(int b=0;b<fieldlist.size();b++)
				{
					Field field =(Field)fieldlist.get(b);
					if("create_date".equalsIgnoreCase(field.getCodesetid())){
						fieldlist.remove(b);
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		lists.add(aNameList);
		lists.add(aVarList);
		lists.add(bNameList);
		lists.add(bVarList);
		lists.add(kNameList);
		lists.add(kVarList);
		lists.add(info);
		return lists;
	}

	/**
	 * 根据用户名创建临时表
	 * @param username
	 * @param i
	 * @param aSet
	 * @throws GeneralException
	 */
	private void createTable(String username, int i, HashSet aSet) throws GeneralException {
		if(aSet.size()==0)
		{
			Table table=null;
			if((i-1)==0)
			{
				table=new Table("t#"+username+"_tjb_A");
				table.setCreatekey(false);
				table.addField(getField1("A0100","hmuster.label.machineNo","string",8));
				table.addField(getField1("B0110","hmuster.label.unitNo","string",50));
				table.addField(getField1("E0122","hmuster.label.departmentNo","string",50));
				table.addField(getField1("E01A1","e01a1.label","string",50));
				table.addField(getField1("NBASE","NBASE","string",3));

			}
			else if((i-1)==2)
			{
				table=new Table("t#"+username+"_tjb_B");
				table.setCreatekey(false);
				table.addField(getField1("B0110","hmuster.label.unitNo","string",50));

			}
			else if((i-1)==4)
			{
				table=new Table("t#"+username+"_tjb_K");
				table.setCreatekey(false);
				table.addField(getField1("E01A1","e01a1.label","string",50));
				table.addField(getField1("E0122","hmuster.label.departmentNo","string",50));
			}
			Field temp21=new Field("I9999",ResourceFactory.getProperty("hmuster.label.no"));
			temp21.setDatatype(DataType.INT);
			temp21.setKeyable(false);
			temp21.setNullable(true);
			temp21.setVisible(false);
			table.addField(temp21);

			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			dbWizard.createTable(table);
			if(this.dbmodel==null) {
                this.dbmodel=new DBMetaModel(this.conn);
            }
			this.dbmodel.reloadTableModel(table.getName());
		}
	}


	/**
	 * 得到临时表中列的集合
	 *
	 * @param aSet
	 * @param aVarSet
	 * @param flag
	 * @return
	 */
	private ArrayList getTableFields(HashSet aSet,HashSet aVarSet,int flag,ArrayList midVariableList,boolean factorIsNull)
	{
		ArrayList list=new ArrayList();
		ArrayList fieldsList=new ArrayList();
		HashSet fieldNameList=new HashSet();
		HashSet fieldVarNameList=new HashSet();
		String    info="0";       			// 判断有指标是否已被删除或还没构库
		//去除重复字段
		HashMap map = new HashMap();
		if(flag==1)
		{
			fieldsList.add(getField1("A0100","hmuster.label.machineNo","string",8));
			fieldNameList.add("A0100");
			fieldsList.add(getField1("B0110","hmuster.label.unitNo","string",50));
			fieldNameList.add("B0110");
			fieldsList.add(getField1("E0122","hmuster.label.departmentNo","string",50));
			fieldNameList.add("E0122");
			fieldsList.add(getField1("E01A1","e01a1.label","string",50));
			fieldNameList.add("E01A1");
			fieldsList.add(getField1("NBASE","NBASE","string",3));
			fieldNameList.add("NBASE");
			fieldsList.add(getField1("create_date","create_date","date",10));//避免找不到这个字段，报错
			fieldNameList.add("create_date");
			map.put("a0100", "A0100");
			map.put("b0110", "B0110");
			map.put("e0122", "E0122");
			map.put("e01a1", "E01A1");
			map.put("nbase", "NBASE");
			map.put("create_date", "create_date");
		}
		else if(flag==2)
		{
			fieldsList.add(getField1("B0110","hmuster.label.unitNo","string",50));
			fieldNameList.add("B0110");
			fieldsList.add(getField1("create_date","create_date","date",10));//避免找不到这个字段，报错
			fieldNameList.add("create_date");
			map.put("b0110", "b0110");
			map.put("create_date", "create_date");

		}
		else if(flag==3)
		{
			fieldsList.add(getField1("E01A1","e01a1.label","string",50));
			fieldNameList.add("E01A1");
			fieldsList.add(getField1("E0122","hmuster.label.departmentNo","string",50));
			fieldNameList.add("E0122");
			fieldsList.add(getField1("create_date","create_date","date",10));//避免找不到这个字段，报错
			fieldNameList.add("create_date");
			map.put("e0122", "E0122");
			map.put("e01a1", "e01a1");
			map.put("create_date", "create_date");
		}
		Field temp21=new Field("I9999",ResourceFactory.getProperty("hmuster.label.no"));
		temp21.setDatatype(DataType.INT);
		temp21.setKeyable(false);
		//	temp21.setNullable(true);
		temp21.setVisible(false);
		fieldsList.add(temp21);
		fieldNameList.add("I9999");
		map.put("i9999", "I9999");

		for(Iterator t=aSet.iterator();t.hasNext();)
		{
			String fieldname=((String)t.next()).trim();
			/* 判断该指标是否已被删除或还没构库 */
			FieldItem item=DataDictionary.getFieldItem(fieldname);
			if(item==null)
			{
				if("create_date".equalsIgnoreCase(fieldname)){//历史时点的归档时间在数据字典里不存在
					continue;
				}else{
					info="1";
					continue;
				}
			}
			if(!"create_date".equalsIgnoreCase(fieldname)&& "0".equals(item.getUseflag()))
			{

				info="1";
				continue;

			}

			String fieldSet="";
			Field obj =null;
//	    	if("create_date".equalsIgnoreCase(fieldname)){//历史时点的归档时间在数据字典里不存在
//	    		 obj=getField2(fieldname,"归档时间","D");
//			}else{
			fieldSet = item.getFieldsetid();
			obj=getField2(fieldname,item.getItemdesc(),item.getItemtype());
//			}

			boolean isExistFieldName=false;
			if(flag==1)
			{
				if(!"A0100".equalsIgnoreCase(fieldname)&&!"B0110".equalsIgnoreCase(fieldname)&&!"E0122".equalsIgnoreCase(fieldname)&&!"E01A1".equalsIgnoreCase(fieldname)) {
                    isExistFieldName=true;
                }
			}
			else if(flag==2)
			{
				if(!"B0110".equalsIgnoreCase(fieldname)) {
                    isExistFieldName=true;
                }
			}
			else if(flag==3)
			{
				if(!"E01A1".equalsIgnoreCase(fieldname)&&!"E0122".equalsIgnoreCase(fieldname)) {
                    isExistFieldName=true;
                }
			}
			if(isExistFieldName)
			{
				if(map.get(fieldname.toLowerCase())==null){
					map.put(fieldname.toLowerCase(), fieldname);
					fieldsList.add(obj);
				}
				if(flag==1)
				{
					fieldNameList.add(fieldname);
					table_columnMap.put("A_"+fieldname.toLowerCase(),"1");
				}
				else if(flag==2)
				{
					if(fieldSet.charAt(0)=='B')
					{
						fieldNameList.add(fieldname);
						table_columnMap.put("B_"+fieldname.toLowerCase(),"1");
					}
				}
				else if(flag==3)
				{
					if(fieldSet.charAt(0)=='K')
					{
						fieldNameList.add(fieldname);
						table_columnMap.put("K_"+fieldname.toLowerCase(),"1");
					}
				}
			}


		}
		for(Iterator t=aVarSet.iterator();t.hasNext();)
		{
			String varCname=(String)t.next();
			for(Iterator t1=midVariableList.iterator();t1.hasNext();)
			{
				RecordVo vo=(RecordVo)t1.next();
				if(vo.getString("cname").equals(varCname))
				{
					String type="A";
					if(vo.getInt("ntype")==1) {
                        type="N";
                    } else if(vo.getInt("ntype")==3) {
                        type="D";
                    }
					Field obj=getField2(varCname,"variable",type);
					if(map.get(varCname.toLowerCase())==null){
						map.put(varCname.toLowerCase(), varCname);
						fieldsList.add(obj);
					}
					fieldVarNameList.add(varCname);

					table_columnMap.put("M_"+varCname.toLowerCase(),"1");
				}
			}
		}

		//wangcq 2014-11-26 增加factorIsNull来判断是否为条件为空时进来的查询
		if(aSet.size()==0&&aVarSet.size()==0&&!factorIsNull)
		{
			fieldsList.clear();
			fieldNameList.clear();
		}
		list.add(fieldsList);
		list.add(fieldNameList);
		list.add(fieldVarNameList);
		list.add(info);
		return list;
	}




	public Field getField1(String name,String describe,String datatype,int length )
	{
		String a_describe=describe;
		if(describe.indexOf(".")!=-1) {
            a_describe=ResourceFactory.getProperty(describe);
        }
		Field temp=new Field(name,a_describe);
		temp.setDatatype(datatype);
		temp.setKeyable(false);
		temp.setVisible(false);
		if(length!=0) {
            temp.setLength(length);
        }
		return temp;
	}


	public Field getField2(String fieldname,String desc,String type)
	{
		Field obj=new Field(fieldname,desc);
		if("A".equals(type))
		{
			obj.setDatatype(DataType.STRING);
			obj.setKeyable(false);
			obj.setVisible(false);
			obj.setLength(255);
			obj.setAlign("left");
		}
		else if("M".equals(type))
		{
			obj.setDatatype(DataType.CLOB);
			obj.setKeyable(false);
			obj.setVisible(false);
			obj.setAlign("left");
		}
		else if("D".equals(type))
		{

			obj.setDatatype(DataType.DATE);
			obj.setKeyable(false);
			obj.setVisible(false);
			obj.setAlign("right");
		}
		else if("N".equals(type))
		{
			obj.setDatatype(DataType.FLOAT);
			obj.setDecimalDigits(6);
			obj.setLength(15);
			obj.setKeyable(false);
			obj.setVisible(false);
			obj.setAlign("left");

		}
		else if("I".equals(type))
		{
			obj.setDatatype(DataType.INT);
			obj.setKeyable(false);
			obj.setVisible(false);

		}
		return obj;
	}




	/**
	 * 得到与列相对应表信息的hashmap
	 *
	 * @param fieldNameList
	 *            列集合
	 * @return
	 */
	public HashMap getFieldSetMap(HashSet fieldNameList)throws GeneralException
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			StringBuffer sql=new StringBuffer("select itemid,fieldsetid from fielditem where itemid in(");
			StringBuffer sql_temp=new StringBuffer("");
			for(Iterator t=fieldNameList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				sql_temp.append(",'");
				sql_temp.append(temp);
				sql_temp.append("'");
			}
			sql.append(sql_temp.substring(1));
			sql.append(")");
			recset=dao.search(sql.toString());
			while(recset.next())
			{
				map.put(recset.getString(1),recset.getString(2));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		/*
		 * finally { try { if(recset!=null) recset.close();
		 *  } catch(Exception e) { e.printStackTrace(); } }
		 */
		return map;
	}


	/**
	 * 得到列相对应的数据类型 的hashmap
	 *
	 * @param fieldNameList
	 *            列集合
	 * @return
	 */
	public HashMap getFieldTypeMap(ArrayList fieldNameList)throws GeneralException
	{
		HashMap map=new HashMap();
		if(fieldNameList.size()<=0) {
            return map;
        }
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			StringBuffer sql=new StringBuffer("select itemid,itemtype from fielditem where itemid in(");
			StringBuffer sql_temp=new StringBuffer("");
			for(Iterator t=fieldNameList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				sql_temp.append(",'");
				sql_temp.append(temp);
				sql_temp.append("'");
			}
			sql.append(sql_temp.substring(1));
			sql.append(")");
			recset=dao.search(sql.toString());
			while(recset.next())
			{
				map.put(recset.getString(1),recset.getString(2));

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		/*
		 * finally { try { if(recset!=null) recset.close();
		 *  } catch(Exception e) { e.printStackTrace(); } }
		 */
		return map;
	}



	/**
	 * 返回扫描单元格的反查sql语句
	 *
	 * @param infoList
	 *            统计条件
	 * @param userName
	 *            用户名
	 * @param j
	 *            列号
	 * @param scanMode
	 *            扫描范围 1：人员库 2：单位 3：单位、部门 4：部门 5 职位库
	 * @param appdate
	 *            截止日期
	 * @return
	 */
	public String getTgridSqls2(ArrayList infoList,String userName,int j,String scanMode,String appdate,ArrayList midVariableList,ArrayList fieldItemSet,ArrayList dbList,String[] rowbottomtemp,String[] colbottomtemp)throws GeneralException
	{
		String sql_str="";
		StringBuffer sql=new StringBuffer("");
		String sub_sql=getSubSql(midVariableList,userName);
		try
		{
			boolean  isStat=false;			// 是否含有统计个数条件
			int      flag1=0;               // 是否取值方式中 含有表达式 或 序号
			this.infoList=infoList;
			String[] statArray=null;
			ArrayList lexprFactor=new ArrayList();
			StringBuffer columnName=new StringBuffer("");
			StringBuffer historyColumnName=new StringBuffer("");   //历史时点指标集合
			StringBuffer historyColumnName2=new StringBuffer("");   //历史时点指标集合,用于拼凑sql
			String tableName="";
			String a_managepriv="1";        //是否按管理范围取数；  0：不限制  1：限制
			String[] statArrayhistory=null;	//历史时点取个数专用
			ArrayList lexprFactorhistory=new ArrayList();//放设置取历史，的表达式
			ArrayList lexprFactornothistory=new ArrayList();//放一般的表达式
			boolean isGetTempData = false ;//是否重新取数
			if("1".equals(scanMode))
			{
				columnName.append("A0100,B0110,E01A1");//加个e01a1  zhaoxg add 214-3-5  有时候定义的表会用到这个字段
				tableName="t#"+userName+"_tjb_A";
			}
			else if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode))
			{
				columnName.append("B0110");
				tableName="t#"+userName+"_tjb_B";
			}
			else if("5".equals(scanMode))
			{
				columnName.append("E01A1");
				tableName="t#"+userName+"_tjb_K";
			}
			else if("6".equals(scanMode))
			{
				columnName.append("a0000,a0101,A0100,B0110");
				tableName="hr_emp_hisdata";
			}
			boolean isTerms=false;


//			判断此列是否需要取历史数据   add by wangchaoqun on 2014-10-30 反查需要查到历史数据
			String a_fieldSet="";
			String a_fielditem="";
//			String a_managepriv="1";        //是否按管理范围取数；  0：不限制  1：限制
//			boolean  isGetTempData=false;   //是否需要重新取数
			String[] qzCondition=null;
			String condition_str="";
			ArrayList monthList=new ArrayList();
			String model="";  //0当前记录，1历史记录，空表示当前记录
			String isCount="0";  //是否有求子集个数
			String[] history_temp=null;
			String historytime = "";  //1表示取值统计方法，设置统计历史时间指标为""
			ArrayList historyfieldlist= new ArrayList(); //存放指标
			String[] temphistory=null;//用于是走取值还是走别的
			for(Iterator t=infoList.iterator();t.hasNext();)
			{
				String[] temp0=(String[])t.next();

				if("1".equals(temp0[1])&&!"5".equals(temp0[2]))  //取值
				{
					ArrayList fieldORvaribleList=exprUtil.statExprAnalyse(getCexpr2Context(1,temp0[5]),midVariableList);
					ArrayList fieldList=(ArrayList)fieldORvaribleList.get(0);      // 指标集和
					ArrayList variableList=(ArrayList)fieldORvaribleList.get(1);   // 临时变量集合
					if(fieldList.size()>0)
					{
						FieldItem item=(FieldItem)DataDictionary.getFieldItem((String)fieldList.get(0));
						a_fieldSet=item.getFieldsetid();
						temphistory=temp0;
						a_fielditem=getCexpr2Context(5,temp0[5]);
						if("null".equals(a_fielditem)){
							a_fielditem="null";
							historytime="1";
						}
						else
						{
							historytime="";
						}

						if((a_fielditem.trim().length()==0|| "null".equals(a_fielditem))&&!"A01".equalsIgnoreCase(a_fieldSet)&&!"B01".equalsIgnoreCase(a_fieldSet)&&!"K01".equalsIgnoreCase(a_fieldSet)&&a_fieldSet.length()>0)
						{
							a_fielditem=a_fieldSet+"z0";
						}

					}
					if(fieldList.size()>0||variableList.size()>0) {
                        a_managepriv=getCexpr2Context(6,temp0[5]);
                    }
					//		if(getCexpr2Context(2,temp0[5]).equals("1"))
					for(Iterator k=infoList.iterator();k.hasNext();)
					{
						String[] _temp0=(String[])k.next();
						//	if(!_temp0[6].equals(temp0[6]))     // temp0[6]为当前单元格号 JinChunhai 2012.09.13
						{
							if("1".equals(getCexpr2Context(2,_temp0[5])))
							{
								qzCondition=temp0;
								break;
							}
						}
					}
					if(temp0[5]!=null&&temp0[5].length()>0&&history_temp==null&& "1".equals(getCexpr2Context(2,temp0[5])))
					{
						history_temp=temp0;
					}

				}
				if("2".equals(temp0[1]))  //求个数
				{
					if(!"主集".equals(getCexpr2Context(4,temp0[5])))
					{
						a_fieldSet=getCexpr2Context(4,temp0[5]);
						temphistory=temp0;
						a_fielditem=getCexpr2Context(5,temp0[5]);
						if("null".equals(a_fielditem)){
							a_fielditem="null";
							historytime="1";
						}
						qzCondition=temp0;
						isCount="1";
					}
					if("1".equals(a_managepriv)) {
                        a_managepriv=getCexpr2Context(6,temp0[5]);
                    }
					if(temp0[5]!=null&&temp0[5].length()>0&&(history_temp==null)&& "1".equals(getCexpr2Context(2,temp0[5])))
					{
						history_temp=temp0;
					}
				}


			}
			if("1".equals(rowbottomtemp[1]))                    // 取值
			{
				statArray=rowbottomtemp;
				isStat=true;
			}
			if("1".equals(colbottomtemp[1]))                    // 取值
			{
				statArray=colbottomtemp;
				isStat=true;
			}
			for(Iterator t=infoList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if("5".equals(temp[1]))
				{

					return " select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' "	;
				}
				else if("3".equals(temp[1])|| "4".equals(temp[1]))	// 碰到取值方式为表达式 或	 序号，不予写数据
				{
					if("4".equals(temp[1])&& "0".equals(temp[9]))
					{
						continue;
					}
					sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
					flag1=1;
					break;
				}
				else if("1".equals(temp[1]))                    // 取值
				{



					if(temp[3]!=null&&temp[3].length()>1)
					{
						lexprFactor.add(temp[4] + "|" + temp[3]);
						isTerms=true;
						if(temphistory!=null&& "1".equals(temphistory[1])&&!"5".equals(temphistory[2])){//定义的是取值
							if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了取值方法的历史条件
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}else{
								if("1".equals(getCexpr2Context(2,temp[5]))){
									lexprFactorhistory.add(temp[4] + "|" + temp[3]);
								}else{
									lexprFactornothistory.add(temp[4] + "|" + temp[3]);
								}
							}
						}else{
							if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了求个数的历史条件
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}else{
								if("1".equals(getCexpr2Context(2,temp[5]))){
									lexprFactorhistory.add(temp[4] + "|" + temp[3]);
								}else{
									lexprFactornothistory.add(temp[4] + "|" + temp[3]);
								}
							}
						}
					}

//					if(temp[3]!=null&&temp[3].length()>1)
//					{
//						lexprFactor.add(temp[4] + "|" + temp[3]);
//						isTerms=true;
//					}
				}
				else if("2".equals(temp[1]))					// 统计个数
				{
					if(!"1".equals(temp[2])) {
                        statArrayhistory = temp;
                    }
					lexprFactor.add(temp[4] + "|" + temp[3]);
					if(temp[3]!=null&&temp[3].length()>1) {
                        isTerms=true;
                    }
					if(temphistory!=null&& "1".equals(temphistory[1])&&!"5".equals(temphistory[2])){//定义的是取值
						if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了取值方法的历史条件
							lexprFactornothistory.add(temp[4] + "|" + temp[3]);
						}else{
							if("1".equals(getCexpr2Context(2,temp[5]))){
								lexprFactorhistory.add(temp[4] + "|" + temp[3]);
							}else{
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}
						}
					}else{
						if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了求个数的历史条件
							lexprFactornothistory.add(temp[4] + "|" + temp[3]);
						}else{
							if("1".equals(getCexpr2Context(2,temp[5]))){
								lexprFactorhistory.add(temp[4] + "|" + temp[3]);
							}else{
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}
						}
					}

				}
			}

			if(a_fielditem.length()>0&&history_temp!=null&&history_temp[5]!=null&&history_temp[5].length()>0&&("1".equals(isCount)|| "1".equals(getCexpr2Context(2,history_temp[5]))))
			{
				if(!"1".equals(historytime))//直接走历史定义的时间范围  需在取值公式中定义了时间范围指标
                {
                    condition_str=getCondition_str(history_temp,a_fieldSet,appdate,a_fieldSet+"."+a_fielditem);  //取得采集数据 历史范围 条件
                }
				//   	monthList=getMonthFromHistory(history_temp,a_fieldSet,appdate);
				model=getCexpr2Context(2,history_temp[5]);
				isGetTempData=true;
				historytime="1";
			}

			if("0".equals(a_managepriv))
			{

				if("1".equals(scanMode)){
					tableName=tableName+"_c";
					sub_sql=sub_sql.replaceAll("t#"+userName+"_tjb_A","t#"+userName+"_tjb_A_c");
				}
			}
			///////////////////////////////
			for(Iterator t=fieldItemSet.iterator();t.hasNext();)
			{
				String fielditemid=(String)t.next();
				if("a0000".equalsIgnoreCase(fielditemid)|| "a0101".equalsIgnoreCase(fielditemid)|| "A0100".equalsIgnoreCase(fielditemid)|| "B0110".equalsIgnoreCase(fielditemid)|| "E01A1".equalsIgnoreCase(fielditemid)){
					continue;
				}
				if(!" ".equals(fielditemid)&&!"".equals(fielditemid)&&!tableName.equals("t#"+userName+"_tjb_A"))
				{
					if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode))
					{
						if("B0110".equalsIgnoreCase(fielditemid)) {
                            continue;
                        }
					}
					else if("5".equals(scanMode))
					{
						if("E01A1".equalsIgnoreCase(fielditemid)) {
                            continue;
                        }
					}
					else if("6".equals(scanMode))
					{
						if("a0101".equalsIgnoreCase(fielditemid)) {
                            continue;
                        }
					}
					FieldItem item = DataDictionary.getFieldItem(fielditemid);
					if(item!=null&& "D".equalsIgnoreCase(item.getItemtype())){
						if(!"1".equals(model) || item.isMainSet()){
							columnName.append(",("+Sql_switcher.numberToChar(Sql_switcher.year(fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(fielditemid))+") "+fielditemid);
						}else{
							historyColumnName2.append("," + fielditemid);
							historyColumnName.append(",max("+Sql_switcher.numberToChar(Sql_switcher.year(a_fieldSet+"."+fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(a_fieldSet+"."+fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(a_fieldSet+"."+fielditemid))+") "+fielditemid);
						}
					}

					else if(item!=null&&a_fieldSet.equalsIgnoreCase(item.getFieldsetid())){
						if(!"1".equals(model) || item.isMainSet()){
							columnName.append(","+fielditemid);
						}else{
							historyColumnName2.append("," + fielditemid);
							if("N".equals(item.getItemtype())){    //数值型用sum，其它类型用max
								historyColumnName.append(",sum("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}else{
								historyColumnName.append(",max("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}
						}

					}
					else{
						if(!"1".equals(model) || item.isMainSet()){
							columnName.append(","+fielditemid);
						}else{
							historyColumnName2.append("," + fielditemid);
							if("N".equals(item.getItemtype())){    //数值型用sum，其它类型用max
								historyColumnName.append(",sum("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}else{
								historyColumnName.append(",max("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}
						}
					}
				}
				else if(!" ".equals(fielditemid)&&!"".equals(fielditemid)&&tableName.equals("t#"+userName+"_tjb_A")&&!"B0110".equals(fielditemid)&&!"A0101".equals(fielditemid))
				{

					FieldItem item = DataDictionary.getFieldItem(fielditemid);
					if(item!=null&& "D".equalsIgnoreCase(item.getItemtype())){
						if(!"1".equals(model) || item.isMainSet()){
							columnName.append(",("+Sql_switcher.numberToChar(Sql_switcher.year(fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(fielditemid))+") "+fielditemid);
						}else{
							historyColumnName2.append("," + fielditemid);
							historyColumnName.append(",max("+Sql_switcher.numberToChar(Sql_switcher.year(a_fieldSet+"."+fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(a_fieldSet+"."+fielditemid))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(a_fieldSet+"."+fielditemid))+") "+fielditemid);
						}
					}
					else if(item!=null&&a_fieldSet.equalsIgnoreCase(item.getFieldsetid())){
						if(!"1".equals(model) || item.isMainSet()){
							columnName.append(","+fielditemid);
						}else{
							historyColumnName2.append("," + fielditemid);
							if("N".equals(item.getItemtype())){    //数值型用sum，其它类型用max
								historyColumnName.append(",sum("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}else{
								historyColumnName.append(",max("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}
						}

					}
					else{
						if(!"1".equals(model) || item.isMainSet()){
							columnName.append(","+fielditemid);
						}else{
							historyColumnName2.append("," + fielditemid);
							if("N".equals(item.getItemtype())){    //数值型用sum，其它类型用max
								historyColumnName.append(",sum("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}else{
								historyColumnName.append(",max("+a_fieldSet+"."+fielditemid+") "+fielditemid);
							}
						}
					}
				}
			}
			historyColumnName = historyColumnName.delete(0, 1);
			historyColumnName2 = historyColumnName2.delete(0, 1);

			if((a_fielditem.length()>0&&
					isGetTempData&&
					sql.length()==0&&a_fieldSet.length()>0&&!(isStat&& "5".equals(statArray[2]))) //取历史数据
					|| StringUtils.equals(isCount, "1"))
			{
				if("1".equals(scanMode)){
					/*if(this.getFactorListMap().size()>0 && a_managepriv.equals("1")){ //控制权限范围且项目格有条件表：_d
						sub_sql = sub_sql.replaceAll(tableName, tableName+"_d");
						tableName = tableName+"_d";
					}*///liuy 2015-5-13 a_managepriv=1 的时候tableName和tableName+"_d"是走的一样的业务
					if(this.getFactorListMap().size()>0 && "0".equals(a_managepriv)){  //不控制权限范围且项目格有条件表：_cd

						if(tableName.endsWith("_c"))//xiegh 20170719 buy 29864 取数时表明被拼成t#rcglg_tjb_A_c_cd  实际是没有这个临时表 会报错 应该拼成t#rcglg_tjb_A_cd
                        {
                            tableName = tableName.replace("_c", "");
                        }

						if(sub_sql.endsWith("_c")) {
                            sub_sql = sub_sql.replaceAll(tableName+"_c", tableName+"_cd");
                        } else {
                            sub_sql = sub_sql.replaceAll(tableName, tableName+"_cd");
                        }

						tableName = tableName+"_cd";
					}
					sql.append(" select "+columnName.toString()+" from "+tableName);
					ArrayList sql_list = new ArrayList();
					sql_list = getHistoryDataSql2(isTerms,flag1,isStat,statArray,midVariableList,a_fieldSet,sub_sql,condition_str
							,userName,j,appdate,lexprFactorhistory,tableName,historyColumnName.toString(),model,a_fielditem,a_managepriv,qzCondition,lexprFactornothistory);
					sql_str=getFullASql3(sql.toString(),sql_list,dbList,historyColumnName2.toString());
				}else if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)|| "5".equals(scanMode)){
					sql.append(" select "+columnName.toString()+" from "+tableName + ")a");
					sql.append( getBKHistoryDataSql2(isTerms,flag1,isStat,statArray,midVariableList,a_fieldSet,sub_sql,condition_str
							,userName,j,appdate,lexprFactorhistory,tableName,historyColumnName.toString(),model,a_fielditem,a_managepriv,qzCondition,historytime,scanMode,lexprFactornothistory));
					if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)) {
                        sql_str=getFull_B_KSql2(sql.toString(),1,historyColumnName2.toString());
                    }
					if("5".equals(scanMode)) {
                        sql_str=getFull_B_KSql2(sql.toString(),2,historyColumnName2.toString());
                    }
				}
			}else{
				if(!"6".equals(scanMode)){
					if(isTerms)  // isTerms 判断是否有统计条件
					{
						if(flag1==0)
						{
							String condition="";
							StringBuffer ext_sql = new StringBuffer();
							if("2".equals(scanMode)) {
                                condition=" codesetid='UN' ";
                            }
							if("3".equals(scanMode)) {
                                condition=" (codesetid='UN' or codesetid='UM') ";
                            }
							if("4".equals(scanMode)) {
                                condition=" codesetid='UM' ";
                            }
							if("4".equals(scanMode)|| "3".equals(scanMode)|| "2".equals(scanMode)){
								Calendar d=Calendar.getInstance();
								int yy=d.get(Calendar.YEAR);
								int mm=d.get(Calendar.MONTH)+1;
								int dd=d.get(Calendar.DATE);
								String date = getBusinessDate();
								if(date!=null&&date.trim().length()>0)
								{
									d.setTime(java.sql.Date.valueOf(date));
									yy=d.get(Calendar.YEAR);
									mm=d.get(Calendar.MONTH)+1;
									dd=d.get(Calendar.DATE);
								}
								ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
								ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
								ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
								ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
								ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
								ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
								condition =condition+ext_sql;
							}
							if(!isStat)    // 按取值方式统计值
							{

								sql.append(" select "+columnName.toString()+" from "+tableName+" where ");
								sql.append(getMergeTerms(lexprFactor,tableName,appdate,userName));
								if(condition.length()>0) {
                                    sql.append(" and B0110 in ( select codeitemid from organization where  "+condition+" ) ");
                                }
							}
						}

					}
					else if(!isTerms&&flag1==0)
					{
						sql.append(" select "+columnName.toString()+" from "+tableName);

					}
				}else{
					String pres="";
					for(int i=0;i<this.dbList.size();i++)
					{
						String pre=(String)this.dbList.get(i);
						pres += "'"+(String)this.dbList.get(i)+"',";
					}
					if(pres.length()>0) {
                        pres= pres.substring(0,pres.length()-1);
                    }
					String manageprivstr =" and  exists ( select A0100 "+ this.userview.getPrivSQLExpression("usr", false)+" and hr_emp_hisdata2.id=hr_emp_hisdata.id and hr_emp_hisdata2.a0100=hr_emp_hisdata.a0100 and hr_emp_hisdata2.nbase=hr_emp_hisdata.nbase  ) ";
					if(manageprivstr.toLowerCase().indexOf("from usra01")!=-1){
						manageprivstr = manageprivstr.toLowerCase().replace("from usra01", "FROM hr_emp_hisdata hr_emp_hisdata2");
						manageprivstr = manageprivstr.toLowerCase().replace("usra01", "hr_emp_hisdata2");
					}else {
                        manageprivstr="";
                    }
					String tablesql ="";
					tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata left join hr_hisdata_list on hr_emp_hisdata.id=hr_hisdata_list.id) hr_emp_hisdata";

					if(isTerms)  // isTerms 判断是否有统计条件
					{
						if(flag1==0)
						{
							String condition="";
							StringBuffer ext_sql = new StringBuffer();
							if("2".equals(scanMode)) {
                                condition=" codesetid='UN' ";
                            }
							if("3".equals(scanMode)) {
                                condition=" (codesetid='UN' or codesetid='UM') ";
                            }
							if("4".equals(scanMode)) {
                                condition=" codesetid='UM' ";
                            }
							if("4".equals(scanMode)|| "3".equals(scanMode)|| "2".equals(scanMode)){
								Calendar d=Calendar.getInstance();
								int yy=d.get(Calendar.YEAR);
								int mm=d.get(Calendar.MONTH)+1;
								int dd=d.get(Calendar.DATE);
								String date = getBusinessDate();
								if(date!=null&&date.trim().length()>0)
								{
									d.setTime(java.sql.Date.valueOf(date));
									yy=d.get(Calendar.YEAR);
									mm=d.get(Calendar.MONTH)+1;
									dd=d.get(Calendar.DATE);
								}
								ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
								ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
								ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
								ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
								ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
								ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
								condition =condition+ext_sql;
							}

							if(!isStat)    // 按取值方式统计值
							{

								sql.append(" select "+columnName.toString()+" from "+tablesql+" where ");
								sql.append(getMergeTerms(lexprFactor,tableName,appdate,userName));
								if(condition.length()>0) {
                                    sql.append(" and B0110 in ( select codeitemid from organization where  "+condition+" ) ");
                                }
								if("1".equals(a_managepriv)){
									sql.append( manageprivstr );
								}
							}
						}

					}
					else if(!isTerms&&flag1==0)
					{
						sql.append(" select "+columnName.toString()+" from "+tablesql);

					}
				}
				if(sql.length()>1)
				{
					if("1".equals(scanMode)|| "6".equals(scanMode))
					{
						if("1".equals(scanMode)){
							sql_str=getFullASql(sql.toString(),dbList);
						}else{
							sql_str=getFullASql2(sql.toString(),dbList);
						}


					}
					else
					{
						if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)) {
                            sql_str=getFull_B_KSql(sql.toString(),1);
                        }
						if("5".equals(scanMode)) {
                            sql_str=getFull_B_KSql(sql.toString(),2);
                        }
					}

				}
			}

//			for(Iterator t=infoList.iterator();t.hasNext();)
//			{
//				String[] temp=(String[])t.next();
//				if(temp[1].equals("3")||temp[1].equals("4"))	// 碰到取值方式为表达式 或
//																// 序号，不予写数据
//				{
//
//					if(temp[1].equals("4")&&temp[9].equals("0"))
//						continue;
//					flag1=1;
//					break;
//				}
//				else if(temp[1].equals("1"))                    // 取值
//				{
//					statArray=temp;
//					isStat=true;
//					if(!temp[2].equals("5"))
//						a_managepriv=getCexpr2Context(6,temp[5]);
//				}
//				else if(temp[1].equals("2"))					// 统计个数
//				{
//					lexprFactor.add(temp[4] + "|" + temp[3]);
//					if(temp[3]!=null&&temp[3].length()>1)
//						isTerms=true;
//					if(a_managepriv.equals("1"))
//						a_managepriv=getCexpr2Context(6,temp[5]);
//					if(!temp[2].equals("1"))
//						statArrayhistory = temp;
//				}
//			}

//			//---------------过滤掉columnName里重复的字段，以免sql中报未明确定义列的错误 zhaoxg 2014-2-17----
//			ArrayList list=new ArrayList();
//			String[] num = columnName.toString().split(",");
//			       for (int i = 0; i < num.length; i++) {
//			             if (!list.contains(num[i])) {//如果list数组不包括num[i]中的值的话，就返回true。
//			                 list.add(num[i]); //在list数组中加入num[i]的值。已经过滤过。
//			             }
//			       }
//			       columnName=new StringBuffer();
//			       for(int i=0;i<list.size();i++){
//			    	   if(i!=0){
//			    		   columnName.append(",");
//			    	   }
//			    	   columnName.append(list.get(i));
//			       }
//			//-------------------------

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return sql_str;
	}




	// 得到取人员库完整的sql
	public String getFullASql(String sql,ArrayList dblist)
	{
		StringBuffer a_sql=new StringBuffer("");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{

			for(int i=0;i<dblist.size();i++)
			{
				String pre=(String)dblist.get(i);
				a_sql.append("union all select  '"+((String)dblist.get(i)).toUpperCase()+"' dbpre ,"+pre.toUpperCase()+"A01.a0000,"+pre.toUpperCase()+"A01.a0101,a.* from "+pre.toUpperCase()+"A01,( ");
				a_sql.append(sql);
				if(sql.indexOf("where")!=-1) {
                    a_sql.append(" and NBASE='"+pre.toUpperCase()+"'");
                } else {
                    a_sql.append(" where NBASE='"+pre.toUpperCase()+"'");
                }
				a_sql.append(" ) a where "+pre.toUpperCase()+"A01.a0100=a.a0100 ");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String ql="select * from ("+a_sql.substring(9)+" )a left join dbname on a.dbpre=dbname.pre  order  by dbname.dbid,a.a0000";
		return ql;
	}

	// 得到取人员库完整的sql
	public String getFullASql2(String sql,ArrayList dblist)
	{
		StringBuffer a_sql=new StringBuffer("");
		try
		{

			for(int i=0;i<dblist.size();i++)
			{
				String pre=(String)dblist.get(i);
				a_sql.append("union all select  '"+(String)dblist.get(i)+"' dbpre ,a.* from ( ");
				a_sql.append(sql);
				if(sql.indexOf("where")!=-1) {
                    a_sql.append(" and NBASE='"+pre+"'");
                } else {
                    a_sql.append(" where NBASE='"+pre+"'");
                }
				a_sql.append(" ) a  ");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String ql="select * from ("+a_sql.substring(9)+" )a left join dbname on a.dbpre=dbname.pre  order  by dbname.dbid,a.a0000";
		return ql;
	}

	// 得到取人员库完整的sql
	public String getFullASql3(String sql,ArrayList sql_list,ArrayList dblist,String historyColumnName)
	{
		StringBuffer a_sql=new StringBuffer("");
		String[] historyColumnNames = null;
		StringBuffer hcolumnName=new StringBuffer("");
		if(historyColumnName.indexOf(",")!=-1){
			historyColumnNames = historyColumnName.split(",");
			for(int i=0; i<historyColumnNames.length; i++){
				hcolumnName.append(",b." + historyColumnNames[i]);
			}
		}else if(!"".equals(historyColumnName)){
			hcolumnName.append(",b." + historyColumnName);
		}
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{

			for(int i=0;i<dblist.size();i++)
			{
				String pre=(String)dblist.get(i);
				a_sql.append("union all select  '"+(String)dblist.get(i)+"' dbpre ,"+pre+"A01.a0000,"+pre+"A01.a0101,a.*"+hcolumnName+" from "+pre+"A01,( ");
				a_sql.append(sql);
				if(sql.indexOf("where")!=-1) {
                    a_sql.append(" and NBASE='"+pre+"')a,");
                } else {
                    a_sql.append(" where NBASE='"+pre+"')a,");
                }
				a_sql.append(sql_list.get(i));
				a_sql.append(" where "+pre+"A01.a0100=a.a0100 and a.a0100=b.a0100 ");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String ql="select * from ("+a_sql.substring(9)+" )a left join dbname on a.dbpre=dbname.pre  order  by dbname.dbid,a.a0000";
		return ql;
	}
	/**
	 *得到取单位或职位库完整的sql
	 * @param sql
	 * @param flag  1:单位、部门 2：职位
	 * @return
	 */
	public String getFull_B_KSql(String sql,int flag) {
		StringBuffer a_sql = new StringBuffer("");
		a_sql.append("select organization.codeitemdesc a_name ,a.* from organization,( ");
		a_sql.append(sql);
		a_sql.append(") a where ");
		if(flag==1) {
            a_sql.append("organization.codeitemid=a.B0110 ");
        } else {
            a_sql.append("organization.codeitemid=a.E01A1");
        }
		a_sql.append(" order by organization.codeitemid ");
		return a_sql.toString();
	}

	public String getFull_B_KSql2(String sql,int flag,String historyColumnName) {
		StringBuffer a_sql = new StringBuffer("");
		String[] historyColumnNames = null;
		StringBuffer hcolumnName=new StringBuffer("");
		if(historyColumnName.indexOf(",")!=-1){
			historyColumnNames = historyColumnName.split(",");
			for(int i=0; i<historyColumnNames.length; i++){
				hcolumnName.append(",b." + historyColumnNames[i]);
			}
		}else if(!"".equals(historyColumnName)){
			hcolumnName.append(",b." + historyColumnName);
		}
		a_sql.append("select organization.codeitemdesc a_name ,a.*"+hcolumnName+" from organization,( ");
		a_sql.append(sql);
		a_sql.append(" where ");
		if(flag==1){
			a_sql.append("organization.codeitemid=a.B0110 and a.B0110=b.B0110");
		}
		else{
			a_sql.append("organization.codeitemid=a.E01A1 and a.E01A1=b.E01A1");
		}
		a_sql.append(" order by organization.codeitemid ");
		return a_sql.toString();
	}



	/**********************************         ********************************8/







	 /**
	 * 分析平均人数公式 分别取得 进入时间指标和调离时间指标
	 * @param cexpr2
	 */
	public ArrayList analyseAvgCexpr3(String cexpr2)
	{
		ArrayList list=new ArrayList();
		String info="a";
		int flag=1;  // 1:累计平均人数公式  2:月平均人数公式
		if("avg".equals(cexpr2.substring(0,3).toLowerCase()))  //月平均人数公式
        {
            flag=2;
        }
		String subStr=cexpr2.substring(cexpr2.indexOf("(")+1,cexpr2.indexOf(")"));
		String[] temps=subStr.split(";");

		if(flag==2&&("Q,".equalsIgnoreCase(temps[0].trim().substring(0,2))|| "M,".equalsIgnoreCase(temps[0].trim().substring(0,2)))) {
            temps[0]=temps[0].substring(2);
        }
		String[] temp1=temps[0].split(",");
		String[] temp2=temps[1].split(",");
		int index=0;
		if(flag==1)
		{
			index=1;
		}
		ArrayList comeList=new ArrayList();
		ArrayList outList=new ArrayList();
		for(int i=0;i<temp1.length;i++)
		{
			if(i+index<temp1.length) {
                comeList.add(temp1[i+index]);
            }
		}
		for(int i=0;i<temp2.length;i++) {
            outList.add(temp2[i]);
        }
		list.add(comeList);
		list.add(outList);
		return list;
	}


	/**
	 * 分析平均人数公式
	 * @param cexpr2
	 * @return info  a:平均人数公式  m:按月统计累计平均人数公式    	q:按季统计累计平均人数公式
	 */
	public String analyseAvgCexpr2(String cexpr2)
	{
		String info="a";
		int flag=1;  // 1:累计平均人数公式  2:月平均人数公式
		if("avg".equals(cexpr2.substring(0,3).toLowerCase()))  //月平均人数公式
        {
            flag=2;
        }
		String subStr=cexpr2.substring(cexpr2.indexOf("(")+1,cexpr2.indexOf(")"));
		String[] temps=subStr.split(";");
		String[] temp1=temps[0].split(",");
		if(flag==1)
		{
			info=temp1[0].toLowerCase();
		}
		return info;
	}

	/**
	 * 分析平均人数公式,取得公式涉及到的指标
	 * @param cexpr2
	 * @param username
	 */
	public HashSet analyseAvgCexpr(String cexpr2,String username)
	{
		HashSet fielditemSet=new HashSet();
		try
		{
			int flag=1;  // 1:累计平均人数公式  2:月平均人数公式
			if("avg".equals(cexpr2.substring(0,3).toLowerCase()))  //月平均人数公式
            {
                flag=2;
            }
			String subStr=cexpr2.substring(cexpr2.indexOf("(")+1,cexpr2.indexOf(")"));
			String[] temps=subStr.split(";");
			String[] temp1=null;
			if(flag==2&&("Q,".equalsIgnoreCase(temps[0].trim().substring(0,2))|| "M,".equalsIgnoreCase(temps[0].trim().substring(0,2)))) {
                temps[0]=temps[0].substring(2);
            }
			temp1=temps[0].split(",");

			String[] temp2=temps[1].split(",");
			int index=0;
			if(flag==1)
			{
				index=1;
			}

			for(int i=0;i<temp1.length;i++)
			{
				if(i+index<temp1.length) {
                    fielditemSet.add(temp1[i+index]);
                }
			}
			for(int i=0;i<temp2.length;i++) {
                fielditemSet.add(temp2[i]);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return  fielditemSet;
	}





	/**
	 * 创建平均人数统计临时表
	 * @param username
	 * @return  info  0:有指标没构库   1:正常
	 */
	public String  createAvgCountTable(HashSet fielditemSet,String username)
	{
		String info="1";
		try
		{
			String tableName="t#"+username+"_tt_1";   //username+"AccAvg";
			DbWizard dbWizard=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			Table table=new Table(tableName);
			if(dbWizard.isExistTable(tableName, false))
			{
				dbWizard.dropTable(table);
			}

			table.addField(getField1("A0100","hmuster.label.machineNo","DataType.STRING",8));
			table.addField(getField1("NBASE","NBASE","DataType.STRING",3));
			for(Iterator t=fielditemSet.iterator();t.hasNext();)
			{
				String itemid=(String)t.next();
				FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
				if(item==null)
				{
					info="0";
					break;
				}
				table.addField(getField2(item.getItemid(),item.getItemid(),"D"));
			}
			if(!"0".equals(info))
			{
				dbWizard.createTable(table);
				if(this.dbmodel==null) {
                    this.dbmodel=new DBMetaModel(this.conn);
                }
				this.dbmodel.reloadTableModel(table.getName());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}


	/**
	 * 创建平均人数统计临时表
	 * @param username
	 * @return
	 */
	public void  createAvgCountTable2(String username)
	{
		String info="1";
		try
		{
			String tableName="t#"+username+"_tt_2"; //username+"AccAvg_result";
			DbWizard dbWizard=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			Table table=new Table(tableName);
			if(dbWizard.isExistTable(tableName, false))
			{
				dbWizard.dropTable(tableName);
				//dao.delete("delete from "+tableName,new ArrayList());
			}
			//	else
			{
				table.addField(getField1("A0100","hmuster.label.machineNo","DataType.STRING",8));
				table.addField(getField1("NBASE","NBASE","DataType.STRING",3));
				table.addField(getField2("addCount","addCount","I"));
				table.addField(getField2("subtractCount","subtractCount","I"));
				table.addField(getField2("month","month","I"));
				table.addField(getField2("year","year","I"));
				dbWizard.createTable(table);
				if(this.dbmodel==null) {
                    this.dbmodel=new DBMetaModel(this.conn);
                }
				this.dbmodel.reloadTableModel(table.getName());

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	/**
	 * 创建平均人数统计临时表
	 * @param username
	 * @return
	 */
	public void  createAvgCountTable3(String username)
	{
		String info="1";
		try
		{
			String tableName="t#"+username+"_tt_3"; //username+"AccAvg_result2";
			DbWizard dbWizard=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			Table table=new Table(tableName);
			if(dbWizard.isExistTable(tableName, false))
			{
				dbWizard.dropTable(tableName);
				//dao.delete("delete from "+tableName,new ArrayList());
			}
			//	else
			{
				table.addField(getField2("count","count","N"));
				dbWizard.createTable(table);
				if(this.dbmodel==null) {
                    this.dbmodel=new DBMetaModel(this.conn);
                }
				this.dbmodel.reloadTableModel(table.getName());

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}





	/**
	 * 返回扫描单元格的sql语句
	 * @param flag //a:平均人数公式  m:按月统计累计平均人数公式  q:按季统计累计平均人数公式
	 * @param infoList 统计条件
	 * @param userName 用户名
	 * @param j  列号
	 * @param appdate  截止日期
	 * @return
	 */
	public String getTgridAccountSqls(boolean isGetTempData,ArrayList monthList,ArrayList infoList,String userName,int j,String appdate,String flag,boolean isTerms,ArrayList lexprFactor)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		createAvgCountTable3(userName);
		int year=0;     //计算的年份
		int month=0;    //计算的月份
		if(appdate!=null&&appdate.indexOf("-")!=-1)
		{
			String[] date=appdate.split("-");
			year=Integer.parseInt(date[0]);
			month=Integer.parseInt(date[1]);
		}
		else
		{
			GregorianCalendar now=new GregorianCalendar();
			year=now.get(Calendar.YEAR);
			month=now.get(Calendar.MONTH)+1;
		}

		int num=1;
		int i=0;
		int ori_month=month;
		if("m".equals(flag))
		{
			num=month;
		}
		if(isGetTempData&& "a".equals(flag))
		{
			num=monthList.size();
			ori_month=Integer.parseInt((String)monthList.get(monthList.size()-1));
		}


		StringBuffer sql=new StringBuffer("");

		try
		{


			String columnName="A0100";
			String tableName="t#"+userName+"_tjb_A";
			String tableName2="t#"+userName+"_tt_2"; //userName+"AccAvg_result";
			/////////////////////////////////////////////
			String[] temp=this.statCexpr.substring(this.statCexpr.indexOf("(")+1,this.statCexpr.indexOf(")")).split(";");

			for(i=0;i<num;i++)
			{
				month=ori_month-i;
				int f_year=year;
				int f_month=month;
				if(month==1)
				{
					f_year=f_year-1;
					f_month=12;
				}
				else {
                    f_month=f_month-1;
                }

				if(isTerms)  // isTerms 判断是否有统计条件
				{
					sql.append("insert into t#"+userName+"_tt_3 (count) ");
					if(temp.length==2) {
                        sql.append("select (aa.a1+bb.a2)/2 v"+j+" from ");
                    } else {
                        sql.append("select (aa.a1+bb.a2)/2.0 v"+j+" from ");
                    }

					sql.append(" ( select count(*) a1 from "+tableName2+" where "+Sql_switcher.isnull("addCount","0")+">"+Sql_switcher.isnull("subtractCount","0"));
					sql.append(" and year="+year+" and month="+month+" and exists ( ");
					sql.append(" select * from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and "+tableName2+".a0100="+tableName+".a0100 and UPPER("+tableName2+".nbase)=UPPER("+tableName+".nbase) "); //update by xiegh on 20180521 bug37756
					sql.append(" ) ) aa,");
					sql.append(" ( select count(*) a2 from "+tableName2+" where "+Sql_switcher.isnull("addCount","0")+">"+Sql_switcher.isnull("subtractCount","0"));
					sql.append(" and year="+f_year+" and month="+f_month+" and exists ( ");
					sql.append(" select * from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and "+tableName2+".a0100="+tableName+".a0100 and UPPER("+tableName2+".nbase)=UPPER("+tableName+".nbase) ");
					sql.append(" ) ) bb");


				}
				else if(!isTerms)
				{

					sql.append("insert into t#"+userName+"_tt_3 (count) ");
					if(temp.length==2) {
                        sql.append("select (aa.a1+bb.a2)/2 v"+j+" from ");
                    } else {
                        sql.append("select (aa.a1+bb.a2)/2.0 v"+j+" from ");
                    }

					sql.append(" ( select count(*) a1 from "+tableName2+" where "+Sql_switcher.isnull("addCount","0")+">"+Sql_switcher.isnull("subtractCount","0"));
					sql.append(" and year="+year+" and month="+month+" and exists ( ");
					sql.append(" select * from "+tableName+" where  "+tableName2+".a0100="+tableName+".a0100 and UPPER("+tableName2+".nbase)=UPPER("+tableName+".nbase) ");
					sql.append(" ) ) aa,");
					sql.append(" ( select count(*) a2 from "+tableName2+" where "+Sql_switcher.isnull("addCount","0")+">"+Sql_switcher.isnull("subtractCount","0"));
					sql.append(" and year="+f_year+" and month="+f_month+" and exists ( ");
					sql.append(" select * from "+tableName+" where  "+tableName2+".a0100="+tableName+".a0100 and   UPPER("+tableName2+".nbase)= UPPER("+tableName+".nbase) ");
					sql.append(" ) ) bb");
				}
				dao.insert(sql.toString(),new ArrayList());
				sql.setLength(0);
			}
			sql.setLength(0);

			if(temp.length==2) {
                sql.append("select sum(count)/"+num+" v"+j+" from  t#"+userName+"_tt_3");
            } else {
                sql.append("select sum(count)/"+(num*1.0)+" v"+j+" from  t#"+userName+"_tt_3");
            }

			RowSet rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				sql.setLength(0);
				sql.append("select distinct "+rowSet.getString(1)+" v"+j+" from  t#"+userName+"_tt_3");
			}
			else
			{
				sql.setLength(0);
				sql.append("select distinct 0 v"+j+" from  t#"+userName+"_tt_3");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}










	/**
	 * 创建 为计算平均人数或 累计平均人数的临时表，并将相关数据塞入其中
	 * @param isGetTempData  统计取值范围是否为历史记录
	 * @param condition_str
	 * @param username       用户名
	 * @param cexpr          公式
	 * @param tableTermsMap  表条件
	 */
	public void calculateAvgAccountData(boolean isGetTempData,ArrayList monthList,String username,String cexpr,HashMap tableTermsMap,String appdate)
	{
		try
		{
			createAvgTable(username,cexpr,tableTermsMap);
			//a:平均人数公式  m:按月统计累计平均人数公式    	q:按季统计累计平均人数公式
			String info=analyseAvgCexpr2(cexpr);
			int year=0;     //计算的年份
			int month=0;    //计算的月份
			if(appdate!=null&&appdate.indexOf("-")!=-1)
			{
				String[] date=appdate.split("-");
				year=Integer.parseInt(date[0]);
				month=Integer.parseInt(date[1]);
			}
			else
			{
				GregorianCalendar now=new GregorianCalendar();
				year=now.get(Calendar.YEAR);
				month=now.get(Calendar.MONTH)+1;
			}
			ArrayList list=analyseAvgCexpr3(cexpr);
			ArrayList comList=(ArrayList)list.get(0);  //进入时间指标
			ArrayList outList=(ArrayList)list.get(1);  //调离时间指标
			if("a".equals(info))
			{
				if(isGetTempData)
				{
					for(int i=0;i<monthList.size();i++)
					{
						if(i==0)
						{
							int tempMonth=Integer.parseInt((String)monthList.get(i));
							if(tempMonth==1)
							{
								insertAvgResult(username,year-1,12,comList,outList);
							}
							else {
                                insertAvgResult(username,year,tempMonth-1,comList,outList);
                            }
						}
						insertAvgResult(username,year,Integer.parseInt((String)monthList.get(i)),comList,outList);
					}

				}
				else
				{
					if(month==1)
					{
						insertAvgResult(username,year-1,12,comList,outList);
					}
					else {
                        insertAvgResult(username,year,month-1,comList,outList);
                    }
					insertAvgResult(username,year,month,comList,outList);
				}

			}
			else if("m".equals(info))
			{
				insertAvgResult(username,year-1,12,comList,outList);
				for(int i=1;i<=month;i++) {
                    insertAvgResult(username,year,i,comList,outList);
                }
			}
			else if("q".equals(info))
			{

			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	//写入每个人到某月的 进入次数 和 离退次数
	public void insertAvgResult(String username,int year,int month,ArrayList comList,ArrayList outList)
	{
		String tableName="t#"+username+"_tt_2"; //username+"AccAvg_result";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			//select  count(a0100),a0100,nbase from su_salary_1 where year(a00z0)>=2005 and month(a00z0)>1  group by a0100,nbase
			StringBuffer sql=new StringBuffer("insert into "+tableName+"(a0100,nbase,addCount,month,year)");
			sql.append(" select a0100,nbase,count(a0100),"+month+","+year+" from  t#"+username+"_tt_1  where ");
			StringBuffer tempSql=new StringBuffer("");
			for(int i=0;i<comList.size();i++)
			{
				String itemid=(String)comList.get(i);
				tempSql.append(" or ( "+Sql_switcher.year(itemid)+"<"+year);
				tempSql.append(" or ( "+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"<="+month+" ) )");
			}
			sql.append(tempSql.substring(3));
			sql.append(" group by a0100,nbase ");
			dao.insert(sql.toString(),new ArrayList());

			sql=new StringBuffer("update "+tableName+" set subtractCount=");
			sql.append("(select a.acount from  ( select "+Sql_switcher.isnull("count(a0100)","0")+" acount ,a0100,nbase  from  t#"+username+"_tt_1  where ");
			tempSql=new StringBuffer("");
			for(int i=0;i<outList.size();i++)
			{
				String itemid=(String)outList.get(i);
				tempSql.append(" or ( "+Sql_switcher.year(itemid)+"<"+year);
				tempSql.append(" or ( "+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"<="+month+" ) )");
			}
			sql.append(tempSql.substring(3));
			sql.append(" group by a0100,nbase ) a  where a.a0100="+tableName+".a0100 and a.nbase="+tableName+".nbase )");
			sql.append(" where  "+ tableName+".year='"+year+"' and  "+tableName+".month='"+month+"'"); // add by xiegh on date 20171222 bug33482 原因：没有根据月份来修改离职状态
			dao.update(sql.toString());

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * 创建 计算平均人数临时表 并插入数据
	 * @param username
	 * @param cexpr
	 * @param tableTermsMap
	 * @return
	 */
	public String createAvgTable(String username,String cexpr,HashMap tableTermsMap)
	{
		String info="";
		HashSet fielditemSet=analyseAvgCexpr(cexpr,username);
		createAvgCountTable(fielditemSet,username);
		createAvgCountTable2(username);
		insertAvgAccountTempDate(username,fielditemSet,tableTermsMap);
		return info;
	}



	/**
	 * 往累计平均人数临时表插入数据
	 * @param fieldNameList	列集合
	 * @param dbList    扫描范围
	 * @param result    是否扫描结果库
	 * @param appdate   扫描截止日期
	 * @param conditionSql  权限控制语句
	 * @param isSuper_admin 是否是超级用户
	 * @return
	 */
	public boolean insertAvgAccountTempDate(String username,HashSet fielditemSet,HashMap tableTermsMap)
	{
		boolean flag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String tableName="t#"+username+"_tt_1"; //username+"AccAvg";
			HashMap setMap=getFieldSetItemList(fielditemSet);
			Set keySet=	setMap.keySet();

			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String keyValue=(String)t.next();
				ArrayList itemList=(ArrayList)setMap.get(keyValue);
				StringBuffer asql=new StringBuffer("");
				for(Iterator tt=dbList.iterator();tt.hasNext();)
				{

					String pre=(String)tt.next();
					String tableTermCondition=(String)tableTermsMap.get(pre);
					StringBuffer sql_sub=new StringBuffer(" select a0100,'"+pre+"'");
					//产生select 前缀子句
					StringBuffer sql_sub_str=new StringBuffer("");
					for(Iterator t1=itemList.iterator();t1.hasNext();)
					{
						String temp=(String)t1.next();
						sql_sub_str.append(","+temp);
					}
					sql_sub.append(sql_sub_str.toString());
					//产生from where 子句
					sql_sub.append(" from "+pre+keyValue);
					//从结果表里取数
					sql_sub.append(" where 1=1 ");
					if(tableTermCondition!=null&&tableTermCondition.length()>0)
					{
						sql_sub.append(" and A0100 in ( "+tableTermCondition+" )");
					}
					if(result!=null&& "true".equals(result))
					{
						sql_sub.append(" and "+pre+"A01.A0100 in (select A0100 from "+username+pre+"Result )");
					}
					if (!this.userview.isSuper_admin()) {

						sql_sub.append(" and A0100 in ( select A0100 "+ this.userview.getPrivSQLExpression(pre, false)+" ) ");
						//System.out.println(" and "+pre+"A01.A0100 in ( select A0100 "+ this.userview.getPrivSQLExpression(pre, false)+" ) ");
					}
					asql.append(" union "+sql_sub.toString());
				}

				StringBuffer sql=new StringBuffer("insert into "+tableName+" (a0100,nbase");
				for(Iterator t1=itemList.iterator();t1.hasNext();)
				{
					String temp=(String)t1.next();
					sql.append(","+temp);
				}
				sql.append(") "+asql.substring(6));
				dao.insert(sql.toString(),new ArrayList());
			}

		}
		catch(Exception e)
		{
			flag=false;
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 将列表中指标按所属子集分类
	 * @param fielditemSet
	 * @return
	 */
	public HashMap getFieldSetItemList(HashSet fielditemSet)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			StringBuffer sql=new StringBuffer("select itemid,fieldsetid from fielditem where itemid in(");
			StringBuffer sql_temp=new StringBuffer("");
			for(Iterator t=fielditemSet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				sql_temp.append(",'");
				sql_temp.append(temp);
				sql_temp.append("'");
			}
			sql.append(sql_temp.substring(1));
			sql.append(") order by fieldsetid");
			recset=dao.search(sql.toString());
			String temp="";
			ArrayList itemList=new	ArrayList();
			while(recset.next())
			{
				String itemid=recset.getString(1);
				String fieldSet=recset.getString(2);
				if(temp.length()==0) {
                    temp=fieldSet;
                }
				if(!temp.equals(fieldSet))
				{
					map.put(temp.toLowerCase(),itemList);
					temp=fieldSet;
					itemList=new ArrayList();
				}
				itemList.add(itemid);
			}
			map.put(temp.toLowerCase(),itemList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}





/**************************** 取历史范围里的数据  *****************************/



	/**
	 * 取得运算表达式内容
	 * @param flag  1：统计（取值）方法公式  2：取值记录范围(0:当前值 1：历史记录) 3：历史记录具体取值范围 4：求个数指标集 5:统计历史记录时间指标
	 * 				6:管理范围限制  7;取值条件 8取值条件因子  9是否只读 （默认False,True 只读）10行只读（满足指定该行所属范围）11行只读（满足指定该行所属范围）
	 * 				12:先按人汇总再统计（取值）13:分位值
	 * @param cexpr2
	 * @return
	 */
	public String getCexpr2Context(int flag,String cexpr2)
	{
		String temp="";

		if(flag==1&&cexpr2!=null&&cexpr2.length()>0)  //统计（取值）方法公式
		{
			if(cexpr2.indexOf("<EXPR>")==-1) {
                temp=cexpr2;
            } else
			{
				int fromIndex=cexpr2.indexOf("<EXPR>");
				int toIndex=cexpr2.indexOf("</EXPR>");
				temp=cexpr2.substring(fromIndex+6,toIndex).trim();

			}
		}
		else if(flag==2)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("MODE")==-1) {
                temp="0";
            } else
			{
				int fromIndex=cexpr2.indexOf("<MODE>");
				int toIndex=cexpr2.indexOf("</MODE>");
				temp=cexpr2.substring(fromIndex+6,toIndex).trim();
			}
		}
		else if(flag==3)
		{
			//<STARTDATE>当月</STARTDATE><ENDDATE>当月</ENDDATE>
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("STARTDATE")==-1) {
                temp="当月/当月";
            } else
			{
				int fromIndex=cexpr2.indexOf("<STARTDATE>");
				int toIndex=cexpr2.indexOf("</STARTDATE>");
				temp=cexpr2.substring(fromIndex+11,toIndex).trim();
				fromIndex=cexpr2.indexOf("<ENDDATE>");
				toIndex=cexpr2.indexOf("</ENDDATE>");
				temp+="/"+cexpr2.substring(fromIndex+9,toIndex).trim();
			}
		}
		else if(flag==4)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("COUNTSET")==-1) {
                temp="主集";
            } else
			{
				int fromIndex=cexpr2.indexOf("<COUNTSET>");
				int toIndex=cexpr2.indexOf("</COUNTSET>");
				temp=cexpr2.substring(fromIndex+10,toIndex).trim();
				if(temp.length()==0|| "A01".equalsIgnoreCase(temp)|| "B01".equalsIgnoreCase(temp)|| "K01".equalsIgnoreCase(temp)) {
                    temp="主集";
                }
			}

		}
		else if(flag==5)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("HISDATEMENU")==-1) {
                temp="";
            } else
			{
				int fromIndex=cexpr2.indexOf("<HISDATEMENU>");
				int toIndex=cexpr2.indexOf("</HISDATEMENU>");
				temp=cexpr2.substring(fromIndex+13,toIndex).trim();
				if(temp.trim().length()==0) {
                    temp="null";
                }
			}

		}
		else if(flag==6)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("MANAGEPRIV")==-1) {
                temp="1";
            } else
			{
				int fromIndex=cexpr2.indexOf("<MANAGEPRIV>");
				int toIndex=cexpr2.indexOf("</MANAGEPRIV>");
				temp=cexpr2.substring(fromIndex+12,toIndex).trim();
			}

		}
		else if(flag==7)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("CONDFACTOR")==-1) {
                temp="";
            } else
			{
				int fromIndex=cexpr2.indexOf("<CONDFACTOR>");
				int toIndex=cexpr2.indexOf("</CONDFACTOR>");
				temp=cexpr2.substring(fromIndex+12,toIndex).trim();
			}

		}
		else if(flag==8)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("CONDEXPR")==-1) {
                temp="";
            } else
			{
				int fromIndex=cexpr2.indexOf("<CONDEXPR>");
				int toIndex=cexpr2.indexOf("</CONDEXPR>");
				temp=cexpr2.substring(fromIndex+10,toIndex).trim();
			}

		}
		else if(flag==9)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("READONLY")==-1) {
                temp="False";
            } else
			{
				int fromIndex=cexpr2.indexOf("<READONLY>");
				int toIndex=cexpr2.indexOf("</READONLY>");
				temp=cexpr2.substring(fromIndex+10,toIndex).trim();
			}

		}else if(flag==10)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("ROWDATE")==-1) {
                temp="";
            } else
			{
				int fromIndex=cexpr2.indexOf("<ROWDATE>");
				int toIndex=cexpr2.indexOf("</ROWDATE>");
				temp=cexpr2.substring(fromIndex+9,toIndex).trim();
			}

		}else if(flag==11)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("ROWDATETYPE")==-1) {
                temp="";
            } else
			{
				int fromIndex=cexpr2.indexOf("<ROWDATETYPE>");
				int toIndex=cexpr2.indexOf("</ROWDATETYPE>");
				temp=cexpr2.substring(fromIndex+13,toIndex).trim();
			}

		}else if(flag==12)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("SUMFIRST")==-1) {
                temp="0";
            } else
			{
				int fromIndex=cexpr2.indexOf("<SUMFIRST>");
				int toIndex=cexpr2.indexOf("</SUMFIRST>");
				temp=cexpr2.substring(fromIndex+10,toIndex).trim();
			}

		}else if(flag==13)
		{
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("MID")==-1) {
                temp="0";
            } else
			{
				int fromIndex=cexpr2.indexOf("<MID>");
				int toIndex=cexpr2.indexOf("</MID>");
				temp=cexpr2.substring(fromIndex+5,toIndex).trim();
			}

		}else if(flag==14){
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("ARCHIVE")==-1) {
                temp="0";
            } else
			{
				String strtemp = "";
				int fromIndex=cexpr2.indexOf("<ARCHIVE>");
				int toIndex=cexpr2.indexOf("</ARCHIVE>");
				strtemp=cexpr2.substring(fromIndex+9,toIndex).trim();
				int a = strtemp.indexOf("<ARCHIVE_ITEM>");
				int b = strtemp.indexOf("</ARCHIVE_ITEM>");
				temp=strtemp.substring(a+14,b).trim();
			}
		}else if(flag==15){
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("NARCH")==-1) {
                temp="0";
            } else
			{
				String strtemp = "";
				int fromIndex=cexpr2.indexOf("<ARCHIVE>");
				int toIndex=cexpr2.indexOf("</ARCHIVE>");
				strtemp=cexpr2.substring(fromIndex+9,toIndex).trim();
				int a = strtemp.indexOf("<NARCH>");
				int b = strtemp.indexOf("</NARCH>");
				temp=strtemp.substring(a+7,b).trim();
			}
		}else if(flag==16){
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("ARCHIVE")==-1) {
                temp="0";
            } else
			{
				String strtemp = "";
				int fromIndex=cexpr2.indexOf("<ARCHIVE>");
				int toIndex=cexpr2.indexOf("</ARCHIVE>");
				strtemp=cexpr2.substring(fromIndex+9,toIndex).trim();
				int a = strtemp.indexOf("<STARTDATE>");
				int b = strtemp.indexOf("</STARTDATE>");
				temp=strtemp.substring(a+11,b).trim();
			}
		}else if(flag==17){
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("ARCHIVE")==-1) {
                temp="0";
            } else
			{
				String strtemp = "";
				int fromIndex=cexpr2.indexOf("<ARCHIVE>");
				int toIndex=cexpr2.indexOf("</ARCHIVE>");
				strtemp=cexpr2.substring(fromIndex+9,toIndex).trim();
				int a = strtemp.indexOf("<ENDDATE>");
				int b = strtemp.indexOf("</ENDDATE>");
				temp=strtemp.substring(a+9,b).trim();
			}
		}else if(flag==18){
			if(cexpr2==null||cexpr2.length()==0||cexpr2.indexOf("ARCHIVE")==-1) {
                temp="0";
            } else
			{
				String strtemp = "";
				int fromIndex=cexpr2.indexOf("<ARCHIVE>");
				int toIndex=cexpr2.indexOf("</ARCHIVE>");
				strtemp=cexpr2.substring(fromIndex+9,toIndex).trim();
				int a = strtemp.indexOf("<ARCHIVE_YEAR>");
				int b = strtemp.indexOf("</ARCHIVE_YEAR>");
				temp=strtemp.substring(a+14,b).trim();
			}
		}
		return temp;
	}

	/*********************************           end         ********************************/

	private String statCexpr="";

	int decimal=0;


	/**
	 * 取得临时表临时变量视图
	 * @param midVariableList
	 * @param userName
	 * @return
	 */
	public String getSubSql(ArrayList midVariableList,String userName)
	{
		String sub_sql="select a0100,b0110,e01a1";
		for(int i=0;i<midVariableList.size();i++)
		{
			RecordVo vo=(RecordVo)midVariableList.get(i);
			if(table_columnMap.get("M_"+vo.getString("cname").toLowerCase())!=null) {
                sub_sql+=","+vo.getString("cname");
            }
		}
		sub_sql+=" from t#"+userName+"_tjb_A";
		return sub_sql;
	}

	//取得当前季
	public String getCurrentQarter(String[] temps)
	{
		String a_quarter="";
		int month=Integer.parseInt(temps[1]);
		if(month>=1&&month<=3)
		{
			a_quarter="1";
		}
		if(month>=4&&month<=6)
		{
			a_quarter="2";
		}
		if(month>=7&&month<=9) {
            a_quarter="3";
        }
		if(month>=10&&month<=12) {
            a_quarter="4";
        }
		return a_quarter;
	}

	//根据季度求得 月份
	public ArrayList getMonthByQuarter(String quarter)
	{
		ArrayList monthList=new ArrayList();
		if("1".equals(quarter))
		{
			monthList.add("1");
			monthList.add("2");
			monthList.add("3");
		}
		else if("2".equals(quarter))
		{
			monthList.add("4");
			monthList.add("5");
			monthList.add("6");
		}
		else if("3".equals(quarter))
		{
			monthList.add("7");
			monthList.add("8");
			monthList.add("9");
		}
		else if("4".equals(quarter))
		{
			monthList.add("10");
			monthList.add("11");
			monthList.add("12");
		}
		return monthList;
	}

	/**
	 * 取得 季度条件 sql语句
	 * @param quarter
	 * @param a_fieldSet
	 * @param operater
	 * @return
	 */
	public String getQuarterSql(String[] temps,String quarter,String a_fieldSet,String operater,String a_fielditem)
	{
		StringBuffer sql=new StringBuffer("");

		String a_quarter="";
		if("本季".equals(quarter)) {
            a_quarter=getCurrentQarter(temps);
        } else {
            a_quarter=quarter.substring(0,quarter.indexOf("季"));
        }
		ArrayList monthList=getMonthByQuarter(a_quarter);
		sql.append(" and "+Sql_switcher.year(a_fielditem)+"="+temps[0]);
		if("=".equals(operater))
		{
			StringBuffer sub_str=new StringBuffer("");
			for(int i=0;i<monthList.size();i++)
			{
				sub_str.append(" or "+Sql_switcher.month(a_fielditem)+operater+(String)monthList.get(i));
			}
			sql.append(" and ( "+sub_str.substring(3)+" )");
		}
		else if(operater.indexOf(">")!=-1)
		{
			sql.append(" and "+Sql_switcher.month(a_fielditem)+operater+(String)monthList.get(0));
		}
		else if(operater.indexOf("<")!=-1)
		{
			sql.append(" and "+Sql_switcher.month(a_fielditem)+operater+(String)monthList.get(2));
		}
		return sql.toString();
	}


	//取得历史范围中的 月份

	public ArrayList getMonthFromHistory(String[] temp0,String a_fieldSet,String appdate)
	{
		ArrayList monthList=new ArrayList();

		Calendar d=Calendar.getInstance();
		if(appdate==null||appdate.length()==0) {
            appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
        }
		String[] temps=appdate.split("-");
		if(startdate==null||startdate.length()==0) {
            startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
        }
		String[] temps2=startdate.split("-");
		if("1".equals(getCexpr2Context(2,temp0[5])))
		{
			String scope_str=getCexpr2Context(3,temp0[5]);
			String[] scopes=scope_str.split("/");

			if(scopes[0].equalsIgnoreCase(scopes[1]))
			{
				if(scopes[0].indexOf("季")!=-1)
				{
					String a_quarter="";
					if("本季".equals(scopes[0])) {
                        a_quarter=getCurrentQarter(temps);
                    } else {
                        a_quarter=scopes[0].substring(0,scopes[0].indexOf("季"));
                    }
					monthList=getMonthByQuarter(a_quarter);
				}
				else
				{
					String a_month=!"当月".equals(scopes[0])?scopes[0]:String.valueOf(Integer.parseInt(temps[1]));
					a_month=a_month.replaceAll("月","");
					monthList.add(a_month);
				}
			}
			else
			{
				if(scopes[0].indexOf("季")!=-1)
				{

					int start_quarter=0;
					if("本季".equals(scopes[0])) {
                        start_quarter=Integer.parseInt(getCurrentQarter(temps));
                    } else {
                        start_quarter=Integer.parseInt(scopes[0].substring(0,scopes[0].indexOf("季")));
                    }
					int end_quarter=0;
					if("本季".equals(scopes[1])) {
                        end_quarter=Integer.parseInt(getCurrentQarter(temps));
                    } else {
                        end_quarter=Integer.parseInt(scopes[1].substring(0,scopes[1].indexOf("季")));
                    }

					if(start_quarter<end_quarter)
					{
						for(int i=start_quarter;i<=end_quarter;i++)
						{
							monthList.addAll(getMonthByQuarter(String.valueOf(i)));
						}
					}


				}
				else
				{
					String startMonth=!"当月".equals(scopes[0])?scopes[0]:String.valueOf(Integer.parseInt(temps[1]));
					startMonth=startMonth.replaceAll("月","");
					String endMonth=!"当月".equals(scopes[1])?scopes[1]:String.valueOf(Integer.parseInt(temps[1]));
					endMonth=endMonth.replaceAll("月","");

					if("起始日期".equalsIgnoreCase(scopes[0]))
					{
						startMonth=temps[1];
					}

					if("截止日期".equalsIgnoreCase(scopes[1]))
					{
						endMonth=temps2[1];
					}

					if(Integer.parseInt(startMonth)<Integer.parseInt(endMonth))
					{
						for(int i=Integer.parseInt(startMonth);i<=Integer.parseInt(endMonth);i++)
						{
							monthList.add(String.valueOf(i));
						}
					}
				}
			}
		}



		return monthList;
	}


	//取得采集数据 历史范围 条件
	public String getCondition_str(String[] temp0,String a_fieldSet,String appdate,String a_fielditem)
	{
		String condition_str="";
		Calendar d=Calendar.getInstance();
		if(appdate==null||appdate.length()==0) {
            appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
        }
		if(startdate==null||startdate.length()==0) {
            startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
        }


		String[] temps=appdate.split("-");
		String[] temps2=startdate.split("-");
		if("1".equals(getCexpr2Context(2,temp0[5])))
		{
			String scope_str=getCexpr2Context(3,temp0[5]);
			String[] scopes=scope_str.split("/");

			if(scopes[0].equalsIgnoreCase(scopes[1]))
			{
				if(scopes[0].indexOf("季")!=-1)
				{
					condition_str+=getQuarterSql(temps,scopes[0],a_fieldSet,"=",a_fielditem);
				}
				else
				{
					if(!"当月".equals(scopes[0]) && scopes[0].indexOf(".")!=-1)
					{
						String timescopey = scopes[0].substring(0, 4);
						String timescopem = scopes[0].substring(scopes[0].indexOf(".")+1,scopes[0].lastIndexOf("."));
						condition_str=" and "+Sql_switcher.year(a_fielditem)+"="+timescopey+" and "+Sql_switcher.month(a_fielditem)+"="+timescopem;
					}
					else
					{
						String a_month = !"当月".equals(scopes[0])?scopes[0]:String.valueOf(Integer.parseInt(temps[1]));
						a_month = a_month.replaceAll("月","");
						condition_str=" and "+Sql_switcher.year(a_fielditem)+"="+temps[0]+" and "+Sql_switcher.month(a_fielditem)+"="+a_month;
					}
					/*
					String a_month=!scopes[0].equals("当月")?scopes[0]:String.valueOf(Integer.parseInt(temps[1]));
					a_month=a_month.replaceAll("月","");
					condition_str=" and "+Sql_switcher.year(a_fielditem)+"="+temps[0]+" and "+Sql_switcher.month(a_fielditem)+"="+a_month;
					*/
				}
			}
			else
			{
				if(scopes[0].indexOf("季")!=-1)
				{
					condition_str+=getQuarterSql(temps,scopes[0],a_fieldSet,">=",a_fielditem);
					condition_str+=getQuarterSql(temps,scopes[1],a_fieldSet,"<=",a_fielditem);
				}
				else
				{
					if(scopes[0].indexOf("起始日期")!=-1)
					{
						if("起始日期".equalsIgnoreCase(scopes[0])){
							condition_str=PubFunc.getDateSql(">=",a_fielditem,startdate);
						}else{
							//组合日期
							condition_str =scopes[0].trim().replace(" ", "");
							String opertortemp = condition_str.replace("起始日期", "").replaceAll("月","");
							String opertor="";
							if(opertortemp.startsWith("+")) {
                                opertor="+";
                            } else if(opertortemp.startsWith("-")) {
                                opertor="-";
                            }
							int num =Integer.parseInt(opertortemp.replace(opertor, ""));
							String newdate =getNewDate(startdate,opertor,num);
							condition_str=PubFunc.getDateSql(">=",a_fielditem,newdate);
						}
					}
					else
					{
						if(!"当月".equals(scopes[0]) && scopes[0].indexOf(".")!=-1)
						{
							//	System.out.println(scopes[0]);
							//	String[] sss = scopes[0].split(".");
							//	System.out.println(sss[0]);
							//	System.out.println(scopes[0].substring(scopes[0].indexOf(".")+1,scopes[0].lastIndexOf(".")));

							String timescopey = scopes[0].substring(0, 4);
							String timescopem = scopes[0].substring(scopes[0].indexOf(".")+1,scopes[0].lastIndexOf("."));
							condition_str=" and ( "+Sql_switcher.year(a_fielditem)+"="+timescopey+" and "+Sql_switcher.month(a_fielditem)+">="+timescopem+" ) ";
						}
						else
						{
							String startMonth=!"当月".equals(scopes[0])?scopes[0]:String.valueOf(Integer.parseInt(temps[1]));
							startMonth=startMonth.replaceAll("月","");
							condition_str=" and ( "+Sql_switcher.year(a_fielditem)+"="+temps[0]+" and "+Sql_switcher.month(a_fielditem)+">="+startMonth+" ) ";
						}
						/*
						String startMonth=!scopes[0].equals("当月")?scopes[0]:String.valueOf(Integer.parseInt(temps[1]));
						startMonth=startMonth.replaceAll("月","");
						condition_str=" and ( "+Sql_switcher.year(a_fielditem)+"="+temps[0]+" and "+Sql_switcher.month(a_fielditem)+">="+startMonth+" ) ";
						*/
					}
					if(scopes[1].indexOf("截止日期")!=-1)
					{
						if("截止日期".equalsIgnoreCase(scopes[1])){
							condition_str+=PubFunc.getDateSql("<=",a_fielditem,appdate);
						}else{
							//组合日期
							String condition = scopes[1].trim().replace(" ", "");
							String opertortemp = condition.replace("截止日期", "").replaceAll("月","");
							String opertor="";
							if(opertortemp.startsWith("+")) {
                                opertor="+";
                            } else if(opertortemp.startsWith("-")) {
                                opertor="-";
                            }
							int num =Integer.parseInt(opertortemp.replace(opertor, ""));
							String newdate =getNewDate(appdate,opertor,num);
							condition_str+=PubFunc.getDateSql("<=",a_fielditem,newdate);
						}
					}
					else
					{
						if(!"当月".equals(scopes[1]) && scopes[1].indexOf(".")!=-1)
						{
							String timescopey = scopes[1].substring(0, 4);
							String timescopem = scopes[1].substring(scopes[1].indexOf(".")+1,scopes[1].lastIndexOf("."));
							condition_str+=" and ( "+Sql_switcher.year(a_fielditem)+"="+timescopey+" and "+Sql_switcher.month(a_fielditem)+"<="+timescopem+" ) ";
						}
						else
						{
							String endMonth=!"当月".equals(scopes[1])?scopes[1]:String.valueOf(Integer.parseInt(temps[1]));
							endMonth=endMonth.replaceAll("月","");
							condition_str+=" and ( "+Sql_switcher.year(a_fielditem)+"="+temps[0]+" and "+Sql_switcher.month(a_fielditem)+"<="+endMonth+" ) ";
						}
						/*
						String endMonth=!scopes[1].equals("当月")?scopes[1]:String.valueOf(Integer.parseInt(temps[1]));
						endMonth=endMonth.replaceAll("月","");
						condition_str+=" and ( "+Sql_switcher.year(a_fielditem)+"="+temps[0]+" and "+Sql_switcher.month(a_fielditem)+"<="+endMonth+" ) ";
						*/
					}
				}
			}
		}
		return condition_str;
	}


	/**
	 * 取得 查询部门或职位的历史数据的sql子句
	 * @param isTerms
	 * @param flag1
	 * @param isStat
	 * @param statArray
	 * @param midVariableList
	 * @param a_fieldSet
	 * @param sub_sql
	 * @param condition_str
	 * @param userName
	 * @param j
	 * @param appdate
	 * @param lexprFactor
	 * @param tableName
	 * @param columnName
	 * @param a_managepriv  是否受权限控制
	 * @return
	 */
	public String getBKHistoryDataSql(boolean isTerms,int flag1,boolean isStat,String[] statArray,ArrayList midVariableList,String a_fieldSet,String sub_sql,String condition_str
			,String userName,int j,String appdate,ArrayList lexprFactor,String tableName,String columnName,String model,String a_fielditem,String a_managepriv,String[] qzCondition,String historytime,String scanMode,ArrayList lexprFactornothistory)
	{
		if("z0".equalsIgnoreCase(a_fielditem)) {
            a_fielditem=a_fieldSet+"z0";
        }
		StringBuffer sql=new StringBuffer("");
		String primarykey = "";
		if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)){
			primarykey = "B0110";
		}else if ("5".equals(scanMode)){
			primarykey = "E01A1";
		}
		String flag ="";
		String condition="";
		if("2".equals(scanMode)) {
            condition=" and "+a_fieldSet+"."+columnName+" in (select CodeItemid From organization WHERE CODESETID='UN' union select CodeItemid From vorganization WHERE CODESETID='UN')  ";
        }
		if("3".equals(scanMode)) {
            condition=" and "+a_fieldSet+"."+columnName+" in (select CodeItemid From organization WHERE CODESETID='UN' or CODESETID='UM' union select CodeItemid From vorganization WHERE CODESETID='UN' or CODESETID='UM') ";
        }
		if("4".equals(scanMode)) {
            condition=" and "+a_fieldSet+"."+columnName+" in (select CodeItemid From organization WHERE CODESETID='UM' union select CodeItemid From vorganization WHERE CODESETID='UM') ";
        }
		String condition_curr = "";
		if("4".equals(scanMode)|| "3".equals(scanMode)|| "2".equals(scanMode)){

			StringBuffer ext_sql = new StringBuffer();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			String date = getBusinessDate();
			if(date!=null&&date.trim().length()>0)
			{
				d.setTime(java.sql.Date.valueOf(date));
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
			}
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			if("2".equals(scanMode)) {
                condition_curr=" and "+a_fieldSet+"."+columnName+" in (select CodeItemid From organization WHERE CODESETID='UN' "+ext_sql+" )  ";
            }
			if("3".equals(scanMode)) {
                condition_curr=" and "+a_fieldSet+"."+columnName+" in (select CodeItemid From organization WHERE (CODESETID='UN' or CODESETID='UM')"+ext_sql+" ) ";
            }
			if("4".equals(scanMode)) {
                condition_curr=" and "+a_fieldSet+"."+columnName+" in (select CodeItemid From organization WHERE CODESETID='UM'"+ext_sql+" ) ";
            }
		}

		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			if(isTerms)  // isTerms 判断是否有统计条件
			{
				if(flag1==0)
				{
					if(isStat)    // 按取值方式统计值
					{
						String operate=statArray[2];
						String expr2=getCexpr2Context(1,statArray[5]);
						if(!"5".equals(operate))  // 不等于平均人数
						{
							ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
							ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
							HashMap fieldSet=getFieldTypeMap(fieldList);
							exprUtil.setDecimal(this.decimal);
							expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate);
							this.decimal=exprUtil.getDecimal();

							StringBuffer temp_sql=new StringBuffer("");
							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);
								String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件 pre 改为""

								temp_sql.append(" union all (");


								if("1".equals(operate))  // 求和
								{
									temp_sql.append(" select SUM( ");
								}
								else if("2".equals(operate))  // 求平均值
								{
									temp_sql.append(" select SUM( ");
								}
								else if("3".equals(operate))  // 求最大值
								{
									temp_sql.append(" select MAX( ");
								}
								else if("4".equals(operate))  // 求最小值
								{
									temp_sql.append(" select MIN( ");
								}
								temp_sql.append(expr2+" ) cnt from "+a_fieldSet+",("+sub_sql+" ) d");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("1".equals(historytime)){
									temp_sql.append(" where "+a_fieldSet+"."+columnName+"=d."+columnName+" "+condition+" and  "+getBKMergeTerms(lexprFactor,a_fieldSet,appdate,userName)+" and ");
									//		temp_sql.append(" "+a_fieldSet+"."+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre+"')");
									temp_sql.append(" exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"   )");
								}else{
									temp_sql.append(" where "+a_fieldSet+"."+columnName+"=d."+columnName+" "+condition+" and  ");
									//		temp_sql.append(" "+a_fieldSet+"."+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre+"')");
									temp_sql.append(" exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"   "+comnnsql+"   and  "+getBKMergeTerms(lexprFactor,tableName,appdate,userName)+"  )");

								}

								temp_sql.append(condition_str);
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								temp_sql.append(" )  ");
								break;
							}

							if("1".equals(operate))  // 求和
							{
								sql.append(" select Sum(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
							}
							else if("2".equals(operate))  //求平均值
							{
								sql.append(" select Sum(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
								String sum="0";
								String count="null";
								RowSet rowSet=dao.search(sql.toString());
								if(rowSet.next())
								{
									if(rowSet.getString(1)!=null) {
                                        sum=rowSet.getString(1);
                                    }
								}
								StringBuffer sssql=new StringBuffer("");
								String pre="";
								for(int i=0;i<this.dbList.size();i++)
								{
									pre=(String)this.dbList.get(i);
									String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件

									sssql.append(" union all ");
									String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
									if(comnnsql.trim().length()>0){
										comnnsql=" and "+comnnsql;
									}
									//	sssql.append(" (select count(*) AS cnt from (select distinct "+columnName+","+Sql_switcher.year(a_fieldSet+"Z0")+" z0y,"+Sql_switcher.month(a_fieldSet+"Z0")+" z0m from "+a_fieldSet+" ");
									if("1".equals(historytime)){
										sssql.append(" (select count(*) AS cnt from (select  "+columnName+" from "+a_fieldSet+" ");
										//	sssql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" ) ");
										sssql.append(" where    "+getBKMergeTerms(lexprFactor,a_fieldSet,appdate,userName)+" and exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"   ) "+condition+"");
									}else{
										sssql.append(" (select count(*) AS cnt from (select  "+columnName+" from "+a_fieldSet+" ");
										//	sssql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" ) ");
										sssql.append(" where exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"   "+comnnsql+"  and  "+getBKMergeTerms(lexprFactor,tableName,appdate,userName)+" ) "+condition+"");
									}
									sssql.append(condition_str);
									if(qz_conditionstr.length()>0) {
                                        sssql.append(" and "+qz_conditionstr);
                                    }
									sssql.append(" ) b) ");
									break;
								}
								sql.setLength(0);
								sql.append(" select Sum(cnt)  v"+j+" from ( ");
								sql.append(sssql.substring(10)+") a");
								rowSet=dao.search(sql.toString());
								if(rowSet.next())
								{
									if(rowSet.getString(1)!=null) {
                                        count=rowSet.getString(1);
                                    }
								}
								sql.setLength(0);
								if(count==null|| "0".equals(count)) {
                                    count="1";
                                }
								sql.append(" select  "+sum+"/"+count+"  v"+j+" from dbname where lower(pre)='"+pre.toLowerCase()+"' ");

							}
							else if("3".equals(operate))  // 求最大值
							{
								sql.append(" select MAX(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
							}
							else if("4".equals(operate))  // 求最小值
							{

								sql.append(" select MIN(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
							}


						}
						else if("5".equals(operate))  // 平均人数
						{
							sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
						}

					}
					else
					{
						StringBuffer temp_sql=new StringBuffer("");
						FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
						String fieldflag = fieldSet.getChangeflag();
						if("1".equals(model))
						{

							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);

								String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件

								temp_sql.append(" union all ");
								//	temp_sql.append(" (select count(*) AS cnt from (select distinct "+columnName+","+Sql_switcher.year(a_fielditem)+" z0y,"+Sql_switcher.month(a_fielditem)+"  z0m from "+pre+a_fieldSet+" ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("1".equals(historytime)){
									temp_sql.append(" (select count(*) AS cnt from (select  "+columnName+"  from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where   "+getBKMergeTerms(lexprFactor,a_fieldSet,appdate,userName)+"  and exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"  ) "+condition+" ");
								}else{
									temp_sql.append(" (select count(*) AS cnt from (select  "+columnName+"  from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"   and  "+getBKMergeTerms(lexprFactor,tableName,appdate,userName)+" )"+condition+" ");
								}

								temp_sql.append(condition_str);
								//temp_sql.append(" and "+a_fieldSet+"z1=1 ");//求单位，部门子集记录数,如果子集中相同月份有多条记录的，按一条记录计算
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								if(fieldflag!=null && fieldflag.trim().length()>0 && !"0".equalsIgnoreCase(fieldflag)) {
                                    temp_sql.append("  group by "+a_fieldSet+"."+primarykey+","+a_fieldSet+"."+a_fieldSet+"Z0 ");
                                }
								temp_sql.append(" ) b) ");
								break;
							}

						}
						else  //但前记录
						{
							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);
								String qz_conditionstr=""; //get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								temp_sql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("1".equals(historytime)){
									temp_sql.append(" (select count(*) AS cnt from (select distinct "+columnName+" from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where   "+getBKMergeTerms(lexprFactor,a_fieldSet,appdate,userName)+" and exists  (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"    ) "+condition_curr+"");
								}else{
									temp_sql.append(" (select count(*) AS cnt from (select distinct "+columnName+" from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where exists  (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"   and  "+getBKMergeTerms(lexprFactor,tableName,appdate,userName)+" )"+condition_curr+" ");
								}


								temp_sql.append(condition_str);
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								temp_sql.append(" ) b) ");
								break;
							}
						}
						sql.append(" select Sum(cnt)  v"+j+" from ( ");
						sql.append(temp_sql.substring(10)+") a");

					}
				}

			}
			else if(!isTerms&&flag1==0&&a_fieldSet.length()>0)
			{
				if(isStat)    // 按取值方式统计值
				{
					String operate=statArray[2];
					String expr2=getCexpr2Context(1,statArray[5]);
					if(!"5".equals(operate))  // 不等于平均人数
					{
						ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
						ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
						HashMap fieldSet=getFieldTypeMap(fieldList);
						exprUtil.setDecimal(this.decimal);
						expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate);
						this.decimal=exprUtil.getDecimal();


						StringBuffer temp_sql=new StringBuffer("");
						for(int i=0;i<this.dbList.size();i++)
						{
							String pre=(String)this.dbList.get(i);
							temp_sql.append(" union all (");


							if("1".equals(operate))  // 求和
							{
								temp_sql.append(" select SUM( ");
							}
							else if("2".equals(operate))  // 求平均值
							{
								temp_sql.append(" select SUM( ");
							}
							else if("3".equals(operate))  // 求最大值
							{
								temp_sql.append(" select MAX( ");
							}
							else if("4".equals(operate))  // 求最小值
							{
								temp_sql.append(" select MIN( ");
							}
							temp_sql.append(expr2+" ) cnt from "+a_fieldSet+",("+sub_sql+" ) d");

							String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							temp_sql.append(" where "+a_fieldSet+"."+columnName+"=d."+columnName+" and ");
							//	temp_sql.append(" "+pre+a_fieldSet+"."+columnName+" in (select "+columnName+" from "+tableName+" where  lower(NBASE) = '"+pre+"')");
							temp_sql.append(" exists (select "+columnName+" from "+tableName+" where  "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"  )"+condition+" ");
							temp_sql.append(condition_str);
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" )  ");
							break;
						}


						if("1".equals(operate))  // 求和
						{
							sql.append(" select Sum(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
						}
						else if("2".equals(operate))  //求平均值
						{
							sql.append(" select Sum(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
							String sum="0";
							String count="null";
							RowSet rowSet=dao.search(sql.toString());
							if(rowSet.next())
							{
								if(rowSet.getString(1)!=null) {
                                    sum=rowSet.getString(1);
                                }
							}
							StringBuffer sssql=new StringBuffer("");
							String pre="";
							for(int i=0;i<this.dbList.size();i++)
							{
								pre=(String)this.dbList.get(i);
								String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件

								sssql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								//sssql.append(" (select count(*) AS cnt from (select distinct "+columnName+","+Sql_switcher.year(a_fieldSet+"Z0")+" z0y,"+Sql_switcher.month(a_fieldSet+"Z0")+" z0m from "+pre+a_fieldSet+" ");
								sssql.append(" (select count(*) AS cnt from (select  "+columnName+" from "+a_fieldSet+" ");
								//	sssql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where  lower(NBASE) = '"+pre.toLowerCase()+"') ");
								sssql.append(" where exists (select "+columnName+" from "+tableName+" where  "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"   "+comnnsql+"  ) "+condition+"");
								sssql.append(condition_str);
								if(qz_conditionstr.length()>0) {
                                    sssql.append(" and "+qz_conditionstr);
                                }
								sssql.append(" ) b) ");
								break;
							}
							sql.setLength(0);
							sql.append(" select Sum(cnt)  v"+j+" from ( ");
							sql.append(sssql.substring(10)+") a");
							rowSet=dao.search(sql.toString());
							if(rowSet.next())
							{
								if(rowSet.getString(1)!=null&&rowSet.getInt(1)!=0) {
                                    count=rowSet.getString(1);
                                }
							}

							sql.setLength(0);
							if(count==null|| "0".equals(count)) {
                                count="1";
                            }
							sql.append(" select  "+sum+"/"+count+"  v"+j+"  from dbname where lower(pre)='"+pre.toLowerCase()+"' ");

						}
						else if("3".equals(operate))  // 求最大值
						{
							sql.append(" select MAX(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
						}
						else if("4".equals(operate))  // 求最小值
						{

							sql.append(" select MIN(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
						}


					}
					else if("5".equals(operate))  // 平均人数
					{
						sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
					}
				}
				else
				{

					StringBuffer temp_sql=new StringBuffer("");

					FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
					if("1".equals(model))
					{

						for(int i=0;i<this.dbList.size();i++)
						{
							String pre=(String)this.dbList.get(i);
							String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							temp_sql.append(" union all ");
							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							//temp_sql.append(" (select count(*) AS cnt from (select distinct "+columnName+", "+Sql_switcher.year(a_fieldSet+"Z0")+" z0y,"+Sql_switcher.month(a_fieldSet+"Z0")+" z0m from "+a_fieldSet+" ");
							temp_sql.append(" (select count(*) AS cnt from (select "+columnName+" from "+a_fieldSet+" ");
							//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where  lower(NBASE) = '"+pre.toLowerCase()+"') ");
							temp_sql.append(" where exists (select "+columnName+" from "+tableName+" where "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"   "+comnnsql+"  )"+condition+" ");
							temp_sql.append(condition_str);
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" ) b) ");
							break;
						}

					}
					else  //但前记录
					{
						for(int i=0;i<this.dbList.size();i++)
						{
							String pre=(String)this.dbList.get(i);
							String qz_conditionstr=""; //get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							temp_sql.append(" union all ");
							temp_sql.append(" (select count(*) AS cnt from (select distinct "+columnName+" from "+a_fieldSet+" ");
							//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where  lower(NBASE) = '"+pre.toLowerCase()+"') ");
							temp_sql.append(" where exists (select "+columnName+" from "+tableName+" where  "+a_fieldSet+"."+columnName+"="+tableName+"."+columnName+"  "+comnnsql+"   )"+condition_curr+" ");
							temp_sql.append(condition_str);
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" ) b) ");
							break;
						}
					}
					sql.append(" select Sum(cnt)  v"+j+" from ( ");
					sql.append(temp_sql.substring(10)+") a");
				}

			}
			else if(sql.length()==0)
			{
				sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");

			}
			flag="1";
			RowSet rowSet=dao.search(sql.toString());
			flag="2";
			String value="";
			if(rowSet.next()) {
                value=PubFunc.round(rowSet.getString(1),4);
            }
			dao.update("insert into historyData_report (username,column_name,value) values ('"+this.userview.getUserName().toLowerCase()+"','v"+j+"',"+value+")");
			sql.setLength(0);
			sql.append(" select value v"+j+" from historyData_report where username='"+this.userview.getUserName().toLowerCase()+"' and column_name='v"+j+"'");
		}
		catch(Exception e)
		{
			if("1".equals(flag)&& "1".equals(historytime)){
				sql.setLength(0);
				sql.append(" select pre v"+j+" from dbname where 1=2 ");
				return sql.toString();
			}else {
                e.printStackTrace();
            }
		}
		return sql.toString();
	}

	/**
	 * 取得 查询部门或职位的历史数据的sql子句
	 * @param isTerms
	 * @param flag1
	 * @param isStat
	 * @param statArray
	 * @param midVariableList
	 * @param a_fieldSet
	 * @param sub_sql
	 * @param condition_str
	 * @param userName
	 * @param j
	 * @param appdate
	 * @param lexprFactor
	 * @param tableName
	 * @param columnName
	 * @param a_managepriv  是否受权限控制
	 * @return
	 */
	public String getBKHistoryDataSql2(boolean isTerms,int flag1,boolean isStat,String[] statArray,ArrayList midVariableList,String a_fieldSet,String sub_sql,String condition_str
			,String userName,int j,String appdate,ArrayList lexprFactor,String tableName,String columnName,String model,String a_fielditem,String a_managepriv,String[] qzCondition,String historytime,String scanMode,ArrayList lexprFactornothistory)
	{
		if(StringUtils.isNotEmpty(columnName)){
			columnName = "," + columnName;
		}
		if("z0".equalsIgnoreCase(a_fielditem)) {
            a_fielditem=a_fieldSet+"z0";
        }
		StringBuffer sql=new StringBuffer("");
		String primarykey = "";
		if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)){
			primarykey = "B0110";
		}else if ("5".equals(scanMode)){
			primarykey = "E01A1";
		}
		String flag ="";
		String condition="";
		if("2".equals(scanMode)) {
            condition=" and "+a_fieldSet+"."+primarykey+" in (select CodeItemid From organization WHERE CODESETID='UN' union select CodeItemid From vorganization WHERE CODESETID='UN')  ";
        }
		if("3".equals(scanMode)) {
            condition=" and "+a_fieldSet+"."+primarykey+" in (select CodeItemid From organization WHERE CODESETID='UN' or CODESETID='UM' union select CodeItemid From vorganization WHERE CODESETID='UN' or CODESETID='UM') ";
        }
		if("4".equals(scanMode)) {
            condition=" and "+a_fieldSet+"."+primarykey+" in (select CodeItemid From organization WHERE CODESETID='UM' union select CodeItemid From vorganization WHERE CODESETID='UM') ";
        }
		String condition_curr = "";
		if("4".equals(scanMode)|| "3".equals(scanMode)|| "2".equals(scanMode)){

			StringBuffer ext_sql = new StringBuffer();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			String date = getBusinessDate();
			if(date!=null&&date.trim().length()>0)
			{
				d.setTime(java.sql.Date.valueOf(date));
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
			}
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			if("2".equals(scanMode)) {
                condition_curr=" and "+a_fieldSet+".B0110 in (select CodeItemid From organization WHERE CODESETID='UN' "+ext_sql+" )  ";
            }
			if("3".equals(scanMode)) {
                condition_curr=" and "+a_fieldSet+".B0110 in (select CodeItemid From organization WHERE (CODESETID='UN' or CODESETID='UM')"+ext_sql+" ) ";
            }
			if("4".equals(scanMode)) {
                condition_curr=" and "+a_fieldSet+".B0110 in (select CodeItemid From organization WHERE CODESETID='UM'"+ext_sql+" ) ";
            }
		}

		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			if(isTerms)  // isTerms 判断是否有统计条件
			{
				if(flag1==0)
				{
//					if(isStat)    // 按取值方式统计值
//					{}
					if(!isStat)
					{
						StringBuffer temp_sql=new StringBuffer("");
						FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
						String fieldflag = fieldSet.getChangeflag();
						if("1".equals(model))
						{

							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);

								String qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件

								temp_sql.append(" union all ");
								//	temp_sql.append(" (select count(*) AS cnt from (select distinct "+columnName+","+Sql_switcher.year(a_fielditem)+" z0y,"+Sql_switcher.month(a_fielditem)+"  z0m from "+pre+a_fieldSet+" ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("1".equals(historytime)){
									temp_sql.append(" select "+primarykey+columnName+"  from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where   "+getBKMergeTerms(lexprFactor,a_fieldSet,appdate,userName)+"  and exists (select "+a_fieldSet+"."+primarykey+columnName+" from "+tableName+" where "+a_fieldSet+"."+primarykey+"="+tableName+"."+primarykey+"  "+comnnsql+"  ) "+condition+" ");
								}else{
									temp_sql.append(" select "+primarykey+columnName+"  from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where exists (select "+a_fieldSet+"."+primarykey+columnName+" from "+tableName+" where "+a_fieldSet+"."+primarykey+"="+tableName+"."+primarykey+"  "+comnnsql+"   and  "+getBKMergeTerms(lexprFactor,tableName,appdate,userName)+" )"+condition+" ");
								}

								temp_sql.append(condition_str);
								//temp_sql.append(" and "+a_fieldSet+"z1=1 ");//求单位，部门子集记录数,如果子集中相同月份有多条记录的，按一条记录计算
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								if(fieldflag!=null && fieldflag.trim().length()>0 && !"0".equalsIgnoreCase(fieldflag)) {
                                    temp_sql.append("  group by "+a_fieldSet+"."+primarykey+", "+a_fieldSet+"."+a_fieldSet+"Z0 ");
                                }
//									temp_sql.append(" ) b) ");
								break;
							}
						}
						else  //但前记录
						{
							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);
								String qz_conditionstr=""; //get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								temp_sql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("1".equals(historytime)){
									temp_sql.append(" select distinct "+primarykey+columnName+" from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where   "+getBKMergeTerms(lexprFactor,a_fieldSet,appdate,userName)+" and exists  (select "+a_fieldSet+"."+primarykey+columnName+" from "+tableName+" where "+a_fieldSet+"."+primarykey+"="+tableName+"."+primarykey+"  "+comnnsql+"    ) "+condition_curr+"");
								}else{
									temp_sql.append(" select distinct "+primarykey+columnName+" from "+a_fieldSet+" ");
									//	temp_sql.append(" where "+columnName+" in (select "+columnName+" from "+tableName+" where "+getMergeTerms(lexprFactor,tableName,appdate,userName)+" and lower(NBASE) = '"+pre.toLowerCase()+"') ");
									temp_sql.append(" where exists  (select "+a_fieldSet+"."+primarykey+columnName+" from "+tableName+" where "+a_fieldSet+"."+primarykey+"="+tableName+"."+primarykey+"  "+comnnsql+"   and  "+getBKMergeTerms(lexprFactor,tableName,appdate,userName)+" )"+condition_curr+" ");
								}


								temp_sql.append(condition_str);
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								if(fieldflag!=null && fieldflag.trim().length()>0 && !"0".equalsIgnoreCase(fieldflag)) {
                                    temp_sql.append("  group by "+a_fieldSet+"."+primarykey+", "+a_fieldSet+"."+a_fieldSet+"Z0 ");
                                }
//								temp_sql.append(" ) b) ");
								break;
							}
						}
//						sql.append(" select Sum(cnt)  v"+j+" from ( ");
						if(temp_sql.length()>0) {
                            sql.append(",(" + temp_sql.substring(10) + ")b");
                        }

					}
				}

			}
//			else if(!isTerms&&flag1==0&&a_fieldSet.length()>0)
//			{}
//			else if(sql.length()==0)
//			{
//				sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
//
//			}
//			flag="1";
//			RowSet rowSet=dao.search(sql.toString());
//			flag="2";
//			String value="";
//			if(rowSet.next())
//				value=PubFunc.round(rowSet.getString(1),4);
//			dao.update("insert into historyData_report (username,column_name,value) values ('"+this.userview.getUserName().toLowerCase()+"','v"+j+"',"+value+")");
//			sql.setLength(0);
//			sql.append(" select value v"+j+" from historyData_report where username='"+this.userview.getUserName().toLowerCase()+"' and column_name='v"+j+"'");
		}
		catch(Exception e)
		{
			if("1".equals(flag)&& "1".equals(historytime)){
				sql.setLength(0);
				sql.append(" select pre v"+j+" from dbname where 1=2 ");
				return sql.toString();
			}else {
                e.printStackTrace();
            }
		}
		return sql.toString();
	}


	public String getHistoryDataSql(boolean isTerms,int flag1,boolean isStat,String[] statArray,ArrayList midVariableList,String a_fieldSet,String sub_sql,String condition_str
			,String userName,int j,String appdate,ArrayList lexprFactor,String tableName,String columnName,String model,String a_fielditem,String a_managepriv,String[] qzCondition,ArrayList lexprFactornothistory )
	{
		//	if(a_fielditem.toLowerCase().indexOf("z0")==-1)
		//		a_fielditem=a_fieldSet+"z0";
		StringBuffer sql=new StringBuffer("");
		//wangcq 2014-12-23 begin
		/*if(this.getFactorListMap().size()>0 && a_managepriv.equals("1")){ //控制权限范围且项目格有条件表：_d
			sub_sql = sub_sql.replaceAll(tableName, tableName+"_d");
			tableName = tableName+"_d";
		}*///liuy 2015-5-13 a_managepriv=1 的时候tableName和tableName+"_d"是走的一样的业务
		if(this.getFactorListMap().size()>0 && "0".equals(a_managepriv)){  //不控制权限范围且项目格有条件表：_cd

			if(tableName.endsWith("_c"))//xiegh 20170719  bug:29864 取数时表明被拼成t#rcglg_tjb_A_c_cd  实际是没有这个临时表 会报错 应该拼成t#rcglg_tjb_A_cd
            {
                tableName = tableName.replace("_c", "");
            }

			if(sub_sql.endsWith("_c")) {
                sub_sql = sub_sql.replaceAll(tableName+"_c", tableName+"_cd");
            } else {
                sub_sql = sub_sql.replaceAll(tableName, tableName+"_cd");
            }

			tableName = tableName+"_cd";
		}
		//wangcq 2014-12-23 end
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			if(isTerms)  // isTerms 判断是否有统计条件
			{
				if(flag1==0)
				{
					if(isStat)    // 按取值方式统计值
					{
						String operate=statArray[2];
						String expr2=getCexpr2Context(1,statArray[5]);
						if(!"5".equals(operate))  // 不等于平均人数
						{
							ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
							ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
							HashMap fieldSet=getFieldTypeMap(fieldList);
							exprUtil.setDecimal(this.decimal);
							expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate,tableName);//xiegh add  bug:29894
							String expr2copy = expr2;   //保留expr2值，便于多个库分别替换为相应库的值
							this.decimal=exprUtil.getDecimal();

							StringBuffer temp_sql=new StringBuffer("");
							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);
								//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
								String strwhere = "";
								expr2 = expr2copy;
								for(int ls=0; ls<fieldList.size(); ls++){
									String field = (String)fieldList.get(ls);
									//if(field.substring(0,1).equalsIgnoreCase("A")){
									if(expr2.indexOf(a_fieldSet+"."+field)==-1){//是否取历史 xiegh add 20171025 bug:28496
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){//liuy 2015-1-21 6851：自动取数：对65号表取数，后台报错
											if(Sql_switcher.searchDbServer()==2) {
                                                expr2 = expr2.replace(field," round("+pre+a_fieldSet + "." + field+",2)");//xiegh 29809 add 20170719
                                            } else if(Sql_switcher.searchDbServer()==1) {
                                                expr2 = expr2.replace(field," convert(numeric(20,2),"+pre+a_fieldSet + "." + field+")");//xiegh 29809 add 20170719
                                            }
										}else{
											if(Sql_switcher.searchDbServer()==2) {
                                                expr2 = expr2.replace(field, " round("+a_fieldSet + "." + field+",2)");
                                            } else if(Sql_switcher.searchDbServer()==1) {
                                                expr2 = expr2.replace(field, " convert(numeric(20,2),"+a_fieldSet + "." + field+")");
                                            }
										}
									}
								}
								if(this.getFactorListMap().size()>0){
									FactorList	factorList= (FactorList)this.getFactorListMap().get(pre.toUpperCase());//update by xiegh on 20170829
									strwhere = factorList.getSingleTableSqlExpression(tableName);
									for(int ls=0; ls<factorList.size(); ls++){
										Factor factor = (Factor)factorList.get(ls);
										String fieldName = (String)factor.getFieldname();
										FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
										String factorFieldSet = "";
										if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                        {
                                            factorFieldSet = item.getFieldsetid();
                                        }
										if(factorFieldSet.equals(a_fieldSet)){
											if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
												strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
											}else{
												strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
											}
										}
									}
								}
								//wangcq 2014-12-11 end
								String qz_conditionstr="";
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}else{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}





								temp_sql.append(" union all (");


								if("1".equals(operate))  // 求和
								{
									temp_sql.append(" select SUM( ");
								}
								else if("2".equals(operate))  // 求平均值
								{
									temp_sql.append(" select SUM( ");
								}
								else if("3".equals(operate))  // 求最大值
								{
									temp_sql.append(" select MAX( ");
								}
								else if("4".equals(operate))  // 求最小值
								{
									temp_sql.append(" select MIN( ");
								}
								temp_sql.append(expr2+" ) cnt from ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(pre+a_fieldSet+",("+sub_sql+" where lower(nbase)='"+pre.toLowerCase()+"') d");
									temp_sql.append(","+tableName+" where "+pre+a_fieldSet+".A0100="+tableName+".A0100 "+comnnsql+"  and "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"'");
									temp_sql.append(" and "+pre+a_fieldSet+".a0100=d.a0100  ");
//										temp_sql.append(" and exists (select A0100 from "+tableName+" where "+pre+a_fieldSet+".A0100="+tableName+".A0100 "+comnnsql+"  and "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"')");
								}
								else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(a_fieldSet+",("+sub_sql+" where lower(nbase)='"+pre.toLowerCase()+"') d");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110  "+comnnsql+" and "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"'");
									temp_sql.append(" and "+a_fieldSet+".B0110=d.B0110 ");
//										temp_sql.append("  and  exists (select A0100 from "+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110  "+comnnsql+" and "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"')");
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(a_fieldSet+",("+sub_sql+" where lower(nbase)='"+pre.toLowerCase()+"') d");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1  "+comnnsql+" and "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"'");
									temp_sql.append(" and "+a_fieldSet+".E01A1=d.E01A1 ");
//										temp_sql.append("  and  exists (select A0100 from "+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1  "+comnnsql+" and "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"')");
								}

								if(!"".equals(strwhere)) {
                                    temp_sql.append(" and " + strwhere);
                                }
								if(condition_str!=null && !"".equals(condition_str)){
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
									}else{
										temp_sql.append(condition_str);
									}
								}
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								temp_sql.append(" )  ");
							}

							if("1".equals(operate))  // 求和
							{
								sql.append(" select Sum(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
							}
							else if("2".equals(operate))  //求平均值
							{
								sql.append(" select Sum(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
								String sum="0";
								String count="null";
								RowSet rowSet=dao.search(sql.toString());
								if(rowSet.next())
								{
									if(rowSet.getString(1)!=null) {
                                        sum=rowSet.getString(1);
                                    }
								}
								StringBuffer sssql=new StringBuffer("");
								String pre="";
								for(int i=0;i<this.dbList.size();i++)
								{
									pre=(String)this.dbList.get(i);
									//wangcq 2014-12-17 begin  生成相应项目格条件sql语句
									String strwhere = "";
									if(this.getFactorListMap().size()>0){
										FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
										strwhere = factorList.getSingleTableSqlExpression(tableName);
										for(int ls=0; ls<factorList.size(); ls++){
											Factor factor = (Factor)factorList.get(ls);
											String fieldName = (String)factor.getFieldname();
											FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
											String factorFieldSet = "";
											if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                            {
                                                factorFieldSet = item.getFieldsetid();
                                            }
											if(factorFieldSet.equals(a_fieldSet)){
												if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
													strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
												}else{
													strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
												}
											}
										}
									}
									//wangcq 2014-12-17 end
									String qz_conditionstr="";
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
									{
										qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
									}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
									{
										qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
									}
									else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
									{
										qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
									}else{
										qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
									}
									sssql.append(" union all ");
									String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
									if(comnnsql.trim().length()>0){
										comnnsql=" and "+comnnsql;
									}
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
									{
										sssql.append(" (select count(*) AS cnt from (select "+pre+a_fieldSet+".A0100 from "+pre+a_fieldSet+" ");
										sssql.append(","+tableName+" where "+pre+a_fieldSet+".a0100="+tableName+".a0100 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
									}
									else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
									{
										sssql.append(" (select count(*) AS cnt from (select "+a_fieldSet+".B0110 from "+a_fieldSet+" ");
										sssql.append(","+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
									}
									else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
									{
										sssql.append(" (select count(*) AS cnt from (select "+a_fieldSet+".E01A1 from "+a_fieldSet+" ");
										sssql.append(","+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
									}

									if(condition_str!=null && !"".equals(condition_str)){
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
											sssql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
										}else{
											sssql.append(condition_str);
										}
									}
									if(!"".equals(strwhere)) {
                                        sssql.append(" and " + strwhere);
                                    }
									if(qz_conditionstr.length()>0) {
                                        sssql.append(" and "+qz_conditionstr);
                                    }
									sssql.append(" ) b) ");
								}
								sql.setLength(0);
								sql.append(" select Sum(cnt)  v"+j+" from ( ");
								sql.append(sssql.substring(10)+") a");
								rowSet=dao.search(sql.toString());
								if(rowSet.next())
								{
									if(rowSet.getString(1)!=null) {
                                        count=rowSet.getString(1);
                                    }
								}
								sql.setLength(0);
								if(count==null|| "0".equals(count)) {
                                    count="1";
                                }
								sql.append(" select  "+sum+"/"+count+"  v"+j+" from dbname where lower(pre)='"+pre.toLowerCase()+"' ");

							}
							else if("3".equals(operate))  // 求最大值
							{
								sql.append(" select MAX(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
							}
							else if("4".equals(operate))  // 求最小值
							{

								sql.append(" select MIN(cnt)  v"+j+" from ( ");
								sql.append(temp_sql.substring(10)+") a");
							}


						}
						else if("5".equals(operate))  // 平均人数
						{
							sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
						}

					}
					else
					{
						StringBuffer temp_sql=new StringBuffer("");
						FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
						String flag = fieldSet.getChangeflag(); // 0:一般子集 1:按月变化子集 2:按年变化子集
						if("1".equals(model))
						{

							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);
								//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
								String strwhere = "";
								if(this.getFactorListMap().size()>0){
									FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
									strwhere = factorList.getSingleTableSqlExpression(tableName);
									for(int ls=0; ls<factorList.size(); ls++){
										Factor factor = (Factor)factorList.get(ls);
										String fieldName = (String)factor.getFieldname();
										FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
										String factorFieldSet = "";
										if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                        {
                                            factorFieldSet = item.getFieldsetid();
                                        }
										if(factorFieldSet.equals(a_fieldSet)){
											if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
												strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
											}else{
												strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
											}
										}
									}
								}
								//wangcq 2014-12-11 end
								String qz_conditionstr="";
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}else{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}
								temp_sql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select count(*) AS cnt from (select  " +pre+a_fieldSet+ ".A0100  from "+pre+a_fieldSet);
									temp_sql.append(","+tableName+" where "+pre+a_fieldSet+".a0100="+tableName+".a0100 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select count(*) AS cnt from (select  " +a_fieldSet+ ".B0110  from "+a_fieldSet);
									temp_sql.append(","+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110  "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select count(*) AS cnt from (select  " +a_fieldSet+ ".E01A1  from "+a_fieldSet);
									temp_sql.append(","+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1  "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								if(!"".equals(strwhere)) {
                                    temp_sql.append(" and " + strwhere);
                                }
								if(condition_str!=null && !"".equals(condition_str)){
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
									}else{
										temp_sql.append(condition_str);
									}
								}
								//	temp_sql.append(" and "+a_fieldSet+"z1=1 ");//求人员子集记录数,如果子集中相同月份有多条记录的，按一条记录计算
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }

								// JinChunhai 2012.10.11
								if(flag!=null && flag.trim().length()>0 && !"0".equalsIgnoreCase(flag)) {
                                    temp_sql.append("  group by "+pre+a_fieldSet+".A0100, "+pre+a_fieldSet+"."+a_fieldSet+"Z0 ");
                                } else if(!"null".equals(a_fielditem)) {
                                    temp_sql.append("  group by "+pre+a_fieldSet+".A0100, "+pre+a_fieldSet+"."+a_fielditem);
                                }
								temp_sql.append(" ) b) ");
							}

						}
						else  //但前记录
						{
							for(int i=0;i<this.dbList.size();i++)
							{
								String pre=(String)this.dbList.get(i);
								//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
								String strwhere = "";
								if(this.getFactorListMap().size()>0){
									FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
									strwhere = factorList.getSingleTableSqlExpression(tableName);
									for(int ls=0; ls<factorList.size(); ls++){
										Factor factor = (Factor)factorList.get(ls);
										String fieldName = (String)factor.getFieldname();
										FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
										String factorFieldSet = "";
										if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                        {
                                            factorFieldSet = item.getFieldsetid();
                                        }
										if(factorFieldSet.equals(a_fieldSet)){
											if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
												strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
											}else{
												strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
											}
										}
									}
								}
								//wangcq 2014-12-11 end
								String qz_conditionstr=""; //get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								temp_sql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select count(*) AS cnt from (select distinct "+pre+a_fieldSet+".A0100 from "+pre+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+pre+a_fieldSet+".a0100="+tableName+".a0100 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select count(*) AS cnt from (select distinct "+a_fieldSet+".B0110 from "+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110 "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");

								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select count(*) AS cnt from (select distinct "+a_fieldSet+".E01A1 from "+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1 "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");


								}
								if(!"".equals(strwhere)) {
                                    temp_sql.append(" and " + strwhere);
                                }
								if(condition_str!=null && !"".equals(condition_str)){
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
									}else{
										temp_sql.append(condition_str);
									}
								}
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								temp_sql.append(" ) b) ");
							}
						}
						sql.append(" select Sum(cnt)  v"+j+" from ( ");
						sql.append(temp_sql.substring(10)+") a");

					}
				}

			}
			else if(!isTerms&&flag1==0&&a_fieldSet.length()>0)
			{
				if(isStat)    // 按取值方式统计值
				{
					String operate=statArray[2];
					String expr2=getCexpr2Context(1,statArray[5]);
					if(!"5".equals(operate))  // 不等于平均人数
					{
						ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
						ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
						HashMap fieldSet=getFieldTypeMap(fieldList);
						exprUtil.setDecimal(this.decimal);
						expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate);
						String expr2copy = expr2;
						this.decimal=exprUtil.getDecimal();


						StringBuffer temp_sql=new StringBuffer("");
						for(int i=0;i<this.dbList.size();i++)
						{
							String pre=(String)this.dbList.get(i);
							//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
							String strwhere = "";
							expr2 = expr2copy;
							for(int ls=0; ls<fieldList.size(); ls++){
								String field = (String)fieldList.get(ls);
								//if(field.substring(0,1).equalsIgnoreCase("A")){
								if(expr2.indexOf(a_fieldSet+"."+field)==-1){//liuy 2015-5-14 9466：君正集团：统计报表 表号为34011，表名为鄂尔多斯君正住房公积金缴纳汇总表（外），在后台可以取出数据，但是在前台无法取出数据
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										expr2 = expr2.replace(field, pre + a_fieldSet + "." + field);
									}else{
										expr2 = expr2.replace(field, a_fieldSet + "." + field);
									}
								}
							}
							if(this.getFactorListMap().size()>0){
								FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
								strwhere = factorList.getSingleTableSqlExpression(tableName);
								for(int ls=0; ls<factorList.size(); ls++){
									Factor factor = (Factor)factorList.get(ls);
									String fieldName = (String)factor.getFieldname();
									FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
									String factorFieldSet = "";
									if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                    {
                                        factorFieldSet = item.getFieldsetid();
                                    }
									if(factorFieldSet.equals(a_fieldSet)){
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
											strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
										}else{
											strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
										}
									}
								}
							}
							//wangcq 2014-12-11 end
							temp_sql.append(" union all (");


							if("1".equals(operate))  // 求和
							{
								temp_sql.append(" select SUM( ");
							}
							else if("2".equals(operate))  // 求平均值
							{
								temp_sql.append(" select SUM( ");
							}
							else if("3".equals(operate))  // 求最大值
							{
								temp_sql.append(" select MAX( ");
							}
							else if("4".equals(operate))  // 求最小值
							{
								temp_sql.append(" select MIN( ");
							}

							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(expr2+" ) cnt from "+pre+a_fieldSet+",("+sub_sql+" where lower(nbase)='"+pre.toLowerCase()+"') d");
                            } else {
                                temp_sql.append(expr2+" ) cnt from "+a_fieldSet+",("+sub_sql+" where lower(nbase)='"+pre.toLowerCase()+"') d");
                            }


							String qz_conditionstr="";
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							}
							else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							}else{
								qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							}
							temp_sql.append(","+tableName+" where  ");
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" "+pre+a_fieldSet+".a0100=d.a0100 and ");
                            } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" "+a_fieldSet+".b0110=d.b0110 and ");
                            } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" "+a_fieldSet+".e01a1=d.e01a1 and ");
                            }

							//	temp_sql.append(" "+pre+a_fieldSet+".A0100 in (select A0100 from "+tableName+" where  lower(NBASE) = '"+pre+"')");
//							temp_sql.append(" exists (select A0100 from "+tableName+" where  ");

							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(pre+a_fieldSet+".A0100="+tableName+".A0100 "+comnnsql+" and  lower(NBASE) = '"+pre.toLowerCase()+"'");
                            } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(a_fieldSet+".b0110="+tableName+".b0110  "+comnnsql+" and  lower(NBASE) = '"+pre.toLowerCase()+"'");
                            } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(a_fieldSet+".e01a1="+tableName+".e01a1 "+comnnsql+" and    lower(NBASE) = '"+pre.toLowerCase()+"'");
                            }
							if(!"".equals(strwhere)) {
                                temp_sql.append(" and " + strwhere);
                            }
							if(condition_str!=null && !"".equals(condition_str)){
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
									temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
								}else{
									temp_sql.append(condition_str);
								}
							}
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" )  ");
						}


						if("1".equals(operate))  // 求和
						{
							sql.append(" select Sum(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
						}
						else if("2".equals(operate))  //求平均值
						{
							sql.append(" select Sum(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
							String sum="0";
							String count="null";
							RowSet rowSet=dao.search(sql.toString());
							if(rowSet.next())
							{
								if(rowSet.getString(1)!=null) {
                                    sum=rowSet.getString(1);
                                }
							}
							StringBuffer sssql=new StringBuffer("");
							String pre="";
							for(int i=0;i<this.dbList.size();i++)
							{
								pre=(String)this.dbList.get(i);
								//wangcq 2014-12-17 begin  生成相应项目格条件sql语句
								String strwhere = "";
								if(this.getFactorListMap().size()>0){
									FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
									strwhere = factorList.getSingleTableSqlExpression(tableName);
									for(int ls=0; ls<factorList.size(); ls++){
										Factor factor = (Factor)factorList.get(ls);
										String fieldName = (String)factor.getFieldname();
										FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
										String factorFieldSet = "";
										if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                        {
                                            factorFieldSet = item.getFieldsetid();
                                        }
										if(factorFieldSet.equals(a_fieldSet)){
											if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
												strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
											}else{
												strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
											}
										}
									}
								}
								//wangcq 2014-12-17 end
								String qz_conditionstr="";
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}else{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}
								sssql.append(" union all ");
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                    sssql.append(" (select count(*) AS cnt from (select "+pre+a_fieldSet+".A0100 from "+pre+a_fieldSet+" ");
                                } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                    sssql.append(" (select count(*) AS cnt from (select "+a_fieldSet+".B0110 from "+a_fieldSet+" ");
                                } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                    sssql.append(" (select count(*) AS cnt from (select "+a_fieldSet+".E01A1 from "+a_fieldSet+" ");
                                }

								sssql.append(","+tableName+" where  ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                    sssql.append(pre+a_fieldSet+".A0100="+tableName+".A0100   "+comnnsql+"   ");
                                } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                    sssql.append(a_fieldSet+".B0110="+tableName+".B0110  "+comnnsql+"   ");
                                } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                    sssql.append(a_fieldSet+".E01A1="+tableName+".E01A1   "+comnnsql+"  ");
                                }
								sssql.append(" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								if(!"".equals(strwhere)) {
                                    sssql.append(" and " + strwhere);
                                }
								if(condition_str!=null && !"".equals(condition_str)){
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										sssql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
									}else{
										sssql.append(condition_str);
									}
								}
								if(qz_conditionstr.length()>0) {
                                    sssql.append(" and "+qz_conditionstr);
                                }
								sssql.append(" ) b) ");
							}
							sql.setLength(0);
							sql.append(" select Sum(cnt)  v"+j+" from ( ");
							sql.append(sssql.substring(10)+") a");
							rowSet=dao.search(sql.toString());
							if(rowSet.next())
							{
								if(rowSet.getString(1)!=null&&rowSet.getInt(1)!=0) {
                                    count=rowSet.getString(1);
                                }
							}

							sql.setLength(0);
							if(count==null|| "0".equals(count)) {
                                count="1";
                            }
							sql.append(" select  "+sum+"/"+count+"  v"+j+"  from dbname where lower(pre)='"+pre.toLowerCase()+"' ");

						}
						else if("3".equals(operate))  // 求最大值
						{
							sql.append(" select MAX(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
						}
						else if("4".equals(operate))  // 求最小值
						{

							sql.append(" select MIN(cnt)  v"+j+" from ( ");
							sql.append(temp_sql.substring(10)+") a");
						}


					}
					else if("5".equals(operate))  // 平均人数
					{
						sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
					}
				}
				else
				{

					StringBuffer temp_sql=new StringBuffer("");
//				    if(this.getFactorListMap().size()>0)
//				    	tableName = tableName+"_ccc";

					FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
					if("1".equals(model))
					{

						for(int i=0;i<this.dbList.size();i++)
						{
							String pre=(String)this.dbList.get(i);
							//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
							String strwhere = "";
							if(this.getFactorListMap().size()>0){
								FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
								strwhere = factorList.getSingleTableSqlExpression(tableName);
								for(int ls=0; ls<factorList.size(); ls++){
									Factor factor = (Factor)factorList.get(ls);
									String fieldName = (String)factor.getFieldname();
									FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
									String factorFieldSet = "";
									if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                    {
                                        factorFieldSet = item.getFieldsetid();
                                    }
									if(factorFieldSet.equals(a_fieldSet)){
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
											strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
										}else{
											strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
										}
									}
								}
							}
							//wangcq 2014-12-11 end
							String qz_conditionstr="";
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							}
							else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							}else{
								qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							}
							temp_sql.append(" union all ");
							//temp_sql.append(" (select count(*) AS cnt from (select distinct A0100, "+Sql_switcher.year(a_fieldSet+"Z0")+" z0y,"+Sql_switcher.month(a_fieldSet+"Z0")+" z0m from "+pre+a_fieldSet+" ");
							temp_sql.append(" (select count(*) AS cnt from  ");
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" (select " +pre+a_fieldSet+ ".A0100 from "+pre+a_fieldSet+" ");
                            } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" (select " +a_fieldSet+ ".B0110 from "+a_fieldSet+" ");
                            } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" (select " +a_fieldSet+ ".E01A1 from "+a_fieldSet+" ");
                            }
							//	temp_sql.append(" where A0100 in (select A0100 from "+tableName+" where  lower(NBASE) = '"+pre.toLowerCase()+"') ");
//								temp_sql.append(" where exists (select A0100 from "+tableName+" where ");
							temp_sql.append(","+tableName+" where ");
							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(pre+a_fieldSet+".A0100="+tableName+".A0100  "+comnnsql+"   ");
                            } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(a_fieldSet+".B0110="+tableName+".B0110   "+comnnsql+"  ");
                            } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(a_fieldSet+".E01A1="+tableName+".E01A1   "+comnnsql+"  ");
                            }

							temp_sql.append("  and  lower(NBASE) = '"+pre.toLowerCase()+"' ");
							if(!"".equals(strwhere)) {
                                temp_sql.append(" and " + strwhere);
                            }
							if(condition_str!=null && !"".equals(condition_str)){
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
									temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
								}else{
									temp_sql.append(condition_str);
								}
							}
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" ) b) ");
						}

					}
					else  //但前记录
					{
						for(int i=0;i<this.dbList.size();i++)
						{
							String pre=(String)this.dbList.get(i);
							//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
							String strwhere = "";
							if(this.getFactorListMap().size()>0){
								FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
								strwhere = factorList.getSingleTableSqlExpression(tableName);
								for(int ls=0; ls<factorList.size(); ls++){
									Factor factor = (Factor)factorList.get(ls);
									String fieldName = (String)factor.getFieldname();
									FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
									String factorFieldSet = "";
									if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                    {
                                        factorFieldSet = item.getFieldsetid();
                                    }
									if(factorFieldSet.equals(a_fieldSet)){
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
											strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
										}else{
											strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
										}
									}
								}
							}
							//wangcq 2014-12-11 end
							String qz_conditionstr=""; //get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							temp_sql.append(" union all ");
							temp_sql.append(" (select count(*) AS cnt from (select distinct ");
							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								temp_sql.append(pre+a_fieldSet+".A0100 from "+pre+a_fieldSet+" ");
								temp_sql.append(","+tableName+" where  "+pre+a_fieldSet+".A0100="+tableName+".A0100   "+comnnsql+"  ");
							}
							else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								temp_sql.append(a_fieldSet+".B0110 from "+a_fieldSet+" ");
								temp_sql.append(","+tableName+" where  "+a_fieldSet+".B0110="+tableName+".B0110   "+comnnsql+"  ");
							}
							else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								temp_sql.append(a_fieldSet+".E01A1 from "+a_fieldSet+" ");
								temp_sql.append(","+tableName+" where  "+a_fieldSet+".E01A1="+tableName+".E01A1   "+comnnsql+"  ");
							}

							temp_sql.append(" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
							if(!"".equals(strwhere)) {
                                temp_sql.append(" and " + strwhere);
                            }
							if(condition_str!=null && !"".equals(condition_str)){
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
									temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
								}else{
									temp_sql.append(condition_str);
								}
							}
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" ) b) ");
						}
					}
					sql.append(" select Sum(cnt)  v"+j+" from ( ");
					sql.append(temp_sql.substring(10)+") a");
				}

			}
			//else if(sql.length()==0)
			if(sql.length()==0) // isTerms=true and  flag1=1时，SQL为空产生 该语句没有返回结果集 错误  20151103 邓灿
			{
				sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");

			}
			//		System.out.println("v"+j+"="+sql.toString());
			RowSet rowSet=dao.search(sql.toString());
			String value="";
			if(rowSet.next()) {
                value=PubFunc.round(rowSet.getString(1),4);
            }
			dao.update("insert into historyData_report (username,column_name,value) values ('"+this.userview.getUserName().toLowerCase()+"','v"+j+"',"+value+")");
			sql.setLength(0);
			sql.append(" select value v"+j+" from historyData_report where username='"+this.userview.getUserName().toLowerCase()+"' and column_name='v"+j+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}

	public ArrayList getHistoryDataSql2(boolean isTerms,int flag1,boolean isStat,String[] statArray,ArrayList midVariableList,String a_fieldSet,String sub_sql,String condition_str
			,String userName,int j,String appdate,ArrayList lexprFactor,String tableName,String historyColumnName,String model,String a_fielditem,String a_managepriv,String[] qzCondition,ArrayList lexprFactornothistory )
	{
		//	if(a_fielditem.toLowerCase().indexOf("z0")==-1)
		//		a_fielditem=a_fieldSet+"z0";
		StringBuffer sql=new StringBuffer("");
		if(!"".equals(historyColumnName)){
			historyColumnName = ","+historyColumnName;
		}
		String historyColumnNameCopy = historyColumnName;//保留historyColumnName值，便于多个库分别替换为相应库的值
		ArrayList sql_list = new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			if(isTerms)  // isTerms 判断是否有统计条件
			{
				if(flag1==0)
				{
//					if(isStat)    // 按取值方式统计值
//					{}
					if(!isStat)  //取个数
					{
						FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
						String flag = fieldSet.getChangeflag(); // 0:一般子集 1:按月变化子集 2:按年变化子集
						if("1".equals(model))
						{

							for(int i=0;i<this.dbList.size();i++)
							{
								StringBuffer temp_sql=new StringBuffer("");
								String pre=(String)this.dbList.get(i);
								//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
								historyColumnName = historyColumnNameCopy.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\.");
								String strwhere = "";
								if(this.getFactorListMap().size()>0){
									FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
									strwhere = factorList.getSingleTableSqlExpression(tableName);
									for(int ls=0; ls<factorList.size(); ls++){
										Factor factor = (Factor)factorList.get(ls);
										String fieldName = (String)factor.getFieldname();
										FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
										String factorFieldSet = "";
										if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                        {
                                            factorFieldSet = item.getFieldsetid();
                                        }
										if(factorFieldSet.equals(a_fieldSet)){
											if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
												strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
											}else{
												strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
											}
										}
									}
								}
								//wangcq 2014-12-11 end

								String qz_conditionstr="";
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
								}else{
									qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
								}
//									temp_sql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select " +pre+a_fieldSet+ ".A0100"+historyColumnName+" from "+pre+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+pre+a_fieldSet+".a0100="+tableName+".a0100 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select " +a_fieldSet+ ".B0110"+historyColumnName+" from "+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110  "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select " +a_fieldSet+ ".E01A1"+historyColumnName+" from "+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1  "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								if(!"".equals(strwhere)) {
                                    temp_sql.append(" and " + strwhere);
                                }
								if(condition_str!=null && !"".equals(condition_str)){
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
									}else{
										temp_sql.append(condition_str);
									}
								}
								//	temp_sql.append(" and "+a_fieldSet+"z1=1 ");//求人员子集记录数,如果子集中相同月份有多条记录的，按一条记录计算
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								temp_sql.append("  and  lower(NBASE) = '"+pre.toLowerCase()+"' ");
								// JinChunhai 2012.10.11
								if(flag!=null && flag.trim().length()>0 && !"0".equalsIgnoreCase(flag)) {
                                    temp_sql.append("  group by "+pre+a_fieldSet+".A0100, "+pre+a_fieldSet+"."+a_fieldSet+"Z0 ");
                                } else if(!"null".equals(a_fielditem)) {
                                    temp_sql.append("  group by "+pre+a_fieldSet+".A0100, "+pre+a_fieldSet+"."+a_fielditem);
                                }
								temp_sql.append(" )b ");
								sql_list.add(temp_sql);
							}

						}
						else  //但前记录
						{

							for(int i=0;i<this.dbList.size();i++)
							{
								StringBuffer temp_sql=new StringBuffer("");
								String pre=(String)this.dbList.get(i);
								//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
								String strwhere = "";
								if(this.getFactorListMap().size()>0){
									FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
									strwhere = factorList.getSingleTableSqlExpression(tableName);
									for(int ls=0; ls<factorList.size(); ls++){
										Factor factor = (Factor)factorList.get(ls);
										String fieldName = (String)factor.getFieldname();
										FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
										String factorFieldSet = "";
										if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                        {
                                            factorFieldSet = item.getFieldsetid();
                                        }
										if(factorFieldSet.equals(a_fieldSet)){
											if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
												strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
											}else{
												strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
											}
										}
									}
								}
								//wangcq 2014-12-11 end
								String qz_conditionstr=""; //get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
//								temp_sql.append(" union all ");
								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
								if(comnnsql.trim().length()>0){
									comnnsql=" and "+comnnsql;
								}
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select distinct "+pre+a_fieldSet+".A0100 from "+pre+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+pre+a_fieldSet+".a0100="+tableName+".a0100 "+comnnsql+" and  "+getMergeTerms2(lexprFactor,pre+a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
								}
								else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select distinct "+a_fieldSet+".B0110 from "+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".B0110="+tableName+".B0110 "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");

								}
								else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
								{
									temp_sql.append(" (select distinct "+a_fieldSet+".E01A1 from "+a_fieldSet+" ");
									temp_sql.append(","+tableName+" where "+a_fieldSet+".E01A1="+tableName+".E01A1 "+comnnsql+"  and  "+getMergeTerms2(lexprFactor,a_fieldSet,appdate,userName,pre,tableName)+" and lower(NBASE) = '"+pre.toLowerCase()+"' ");


								}
								if(!"".equals(strwhere)) {
                                    temp_sql.append(" and " + strwhere);
                                }
								if(condition_str!=null && !"".equals(condition_str)){
									if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
										temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
									}else{
										temp_sql.append(condition_str);
									}
								}
								if(qz_conditionstr.length()>0) {
                                    temp_sql.append(" and "+qz_conditionstr);
                                }
								temp_sql.append(" ) b ");
								sql_list.add(temp_sql);
							}

						}
//						sql.append(" select Sum(cnt)  v"+j+" from ( ");
//						sql.append(",(" + temp_sql.substring(10) + ")b");

					}
				}

			}else if(!isTerms&&flag1==0&&a_fieldSet.length()>0)
			{
				if(isStat)    // 按取值方式统计值
				{}
				else
				{

					FieldSet fieldSet=DataDictionary.getFieldSetVo(a_fieldSet.toLowerCase());
					if("1".equals(model))
					{

						for(int i=0;i<this.dbList.size();i++)
						{
							StringBuffer temp_sql=new StringBuffer("");
							String pre=(String)this.dbList.get(i);
							//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
							String strwhere = "";
							if(this.getFactorListMap().size()>0){
								FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
								strwhere = factorList.getSingleTableSqlExpression(tableName);
								for(int ls=0; ls<factorList.size(); ls++){
									Factor factor = (Factor)factorList.get(ls);
									String fieldName = (String)factor.getFieldname();
									FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
									String factorFieldSet = "";
									if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                    {
                                        factorFieldSet = item.getFieldsetid();
                                    }
									if(factorFieldSet.equals(a_fieldSet)){
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
											strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
										}else{
											strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
										}
									}
								}
							}
							//wangcq 2014-12-11 end
							String qz_conditionstr="";
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							}else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							}
							else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								qz_conditionstr=get_qzConditionSql(qzCondition, "",a_fieldSet,appdate,userName); //取值条件
							}else{
								qz_conditionstr=get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
							}
//								temp_sql.append(" union all ");
							//temp_sql.append(" (select count(*) AS cnt from (select distinct A0100, "+Sql_switcher.year(a_fieldSet+"Z0")+" z0y,"+Sql_switcher.month(a_fieldSet+"Z0")+" z0m from "+pre+a_fieldSet+" ");
//								temp_sql.append(" (select count(*) AS cnt from  ");
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" (select "+pre+a_fieldSet+".A0100 from "+pre+a_fieldSet);
                            } else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" (select "+a_fieldSet+".B0110 from "+a_fieldSet);
                            } else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1))) {
                                temp_sql.append(" (select "+a_fieldSet+".E01A1 from "+a_fieldSet);
                            }
							//	temp_sql.append(" where A0100 in (select A0100 from "+tableName+" where  lower(NBASE) = '"+pre.toLowerCase()+"') ");
							temp_sql.append(","+tableName+" where "+pre+a_fieldSet+".a0100="+tableName+".a0100 ");
//								String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
//								if(comnnsql.trim().length()>0){
//									comnnsql=" and "+comnnsql;
//								}
//								if(a_fieldSet.substring(0,1).equalsIgnoreCase("A"))
//									temp_sql.append(pre+a_fieldSet+".A0100="+tableName+".A0100  "+comnnsql+"   ");
//								else if(a_fieldSet.substring(0,1).equalsIgnoreCase("B"))
//									temp_sql.append(a_fieldSet+".B0110="+tableName+".B0110   "+comnnsql+"  ");
//								else if(a_fieldSet.substring(0,1).equalsIgnoreCase("K"))
//									temp_sql.append(a_fieldSet+".E01A1="+tableName+".E01A1   "+comnnsql+"  ");

//								temp_sql.append("  and  lower(NBASE) = '"+pre.toLowerCase()+"') ");
							if(!"".equals(strwhere)) {
                                temp_sql.append(" and " + strwhere);
                            }
							if(condition_str!=null && !"".equals(condition_str)){
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
									temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
								}else{
									temp_sql.append(condition_str);
								}
							}
							temp_sql.append("  and  lower(NBASE) = '"+pre.toLowerCase()+"' ");
							temp_sql.append(")b ");
//								if(qz_conditionstr.length()>0)
//									temp_sql.append(" and "+qz_conditionstr);
//								temp_sql.append(" ) b) ");
							sql_list.add(temp_sql);
						}

					}
					else  //但前记录
					{
						for(int i=0;i<this.dbList.size();i++)
						{
							StringBuffer temp_sql=new StringBuffer("");
							String pre=(String)this.dbList.get(i);
							//wangcq 2014-12-11 begin  生成相应项目格条件sql语句
							String strwhere = "";
							if(this.getFactorListMap().size()>0){
								FactorList factorList = (FactorList)this.getFactorListMap().get(pre.toUpperCase());
								strwhere = factorList.getSingleTableSqlExpression(tableName);
								for(int ls=0; ls<factorList.size(); ls++){
									Factor factor = (Factor)factorList.get(ls);
									String fieldName = (String)factor.getFieldname();
									FieldItem item = (FieldItem)DataDictionary.getFieldItem(fieldName);
									String factorFieldSet = "";
									if(item!=null)   //当设置了临时变量时，没有相应的item，防止这种空指针情况
                                    {
                                        factorFieldSet = item.getFieldsetid();
                                    }
									if(factorFieldSet.equals(a_fieldSet)){
										if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
											strwhere = strwhere.replace(tableName+"."+fieldName, pre+a_fieldSet+"."+fieldName);
										}else{
											strwhere = strwhere.replace(tableName+"."+fieldName, a_fieldSet+"."+fieldName);
										}
									}
								}
							}
							//wangcq 2014-12-11 end
							String qz_conditionstr=""; //get_qzConditionSql(qzCondition, pre,a_fieldSet,appdate,userName); //取值条件
//							temp_sql.append(" union all ");
							temp_sql.append(" (select distinct ");
							String comnnsql =getMergeTerms(lexprFactornothistory,tableName,appdate,userName,pre,a_fieldSet);
							if(comnnsql.trim().length()>0){
								comnnsql=" and "+comnnsql;
							}
							if("A".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								temp_sql.append(pre+a_fieldSet+".A0100 from "+pre+a_fieldSet+" ");
								temp_sql.append(","+tableName+" where  "+pre+a_fieldSet+".A0100="+tableName+".A0100   "+comnnsql+"  ");
							}
							else if("B".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								temp_sql.append(a_fieldSet+".B0110 from "+a_fieldSet+" ");
								temp_sql.append(","+tableName+" where  "+a_fieldSet+".B0110="+tableName+".B0110   "+comnnsql+"  ");
							}
							else if("K".equalsIgnoreCase(a_fieldSet.substring(0,1)))
							{
								temp_sql.append(a_fieldSet+".E01A1 from "+a_fieldSet+" ");
								temp_sql.append(","+tableName+" where  "+a_fieldSet+".E01A1="+tableName+".E01A1   "+comnnsql+"  ");
							}

							temp_sql.append(" and lower(NBASE) = '"+pre.toLowerCase()+"' ");
							if(!"".equals(strwhere)) {
                                temp_sql.append(" and " + strwhere);
                            }
							if(condition_str!=null && !"".equals(condition_str)){
								if("A".equalsIgnoreCase(a_fieldSet.substring(0,1))){
									temp_sql.append(condition_str.replaceAll(a_fieldSet+"\\.", pre+a_fieldSet+"\\."));
								}else{
									temp_sql.append(condition_str);
								}
							}
							if(qz_conditionstr.length()>0) {
                                temp_sql.append(" and "+qz_conditionstr);
                            }
							temp_sql.append(" ) b ");
							sql_list.add(temp_sql);
						}
					}
//					sql.append(" select Sum(cnt)  v"+j+" from ( ");
//					sql.append(temp_sql.substring(10)+") a");
//					sql.append("," + temp_sql.substring(10) + "))b");
				}

			}
//			else if(sql.length()==0)
//			{
//				sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
//
//			}
//			RowSet rowSet=dao.search(sql.toString());
//			String value="";
//			if(rowSet.next())
//				value=PubFunc.round(rowSet.getString(1),4);
//			dao.update("insert into historyData_report (username,column_name,value) values ('"+this.userview.getUserName().toLowerCase()+"','v"+j+"',"+value+")");
//			sql.setLength(0);
//			sql.append(" select value v"+j+" from historyData_report where username='"+this.userview.getUserName().toLowerCase()+"' and column_name='v"+j+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql_list;
	}

	public String getPersonHistoryTimeDataSql(int flag1,String scanMode,boolean isStat,String[] statArray,String[] statArrayhistory,ArrayList midVariableList,boolean isGetTempData,boolean isTerms,
											  String userName,int j,String appdate,ArrayList lexprFactor,String tableName,String columnName,ArrayList monthList,ArrayList infoList,String a_managepriv,String method)throws GeneralException
	{
		StringBuffer sql = new StringBuffer();
		String pres="";
		for(int i=0;i<this.dbList.size();i++)
		{
			String pre=(String)this.dbList.get(i);
			pres += "'"+(String)this.dbList.get(i)+"',";
		}
		if(pres.length()>0) {
            pres= pres.substring(0,pres.length()-1);
        }
		String manageprivstr =" and  exists ( select A0100 "+ this.userview.getPrivSQLExpression("usr", false)+" and hr_emp_hisdata2.id=hr_emp_hisdata.id and hr_emp_hisdata2.a0100=hr_emp_hisdata.a0100 and hr_emp_hisdata2.nbase=hr_emp_hisdata.nbase  ) ";
		if(manageprivstr.toLowerCase().indexOf("from usra01")!=-1){
			manageprivstr = manageprivstr.toLowerCase().replace("from usra01", "FROM hr_emp_hisdata hr_emp_hisdata2");
			manageprivstr = manageprivstr.toLowerCase().replace("usra01", "hr_emp_hisdata2");
		}else {
            manageprivstr="";
        }

//		String manageprivstr="";
		if(flag1==0)
		{
			String condition="";
			String tablesql ="";
			//if(a_managepriv.equals("0")){
			tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata left join hr_hisdata_list on hr_emp_hisdata.id=hr_hisdata_list.id) hr_emp_hisdata";
			//}else{
			// tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  (select hr_emp_hisdata.* from hr_emp_hisdata,t#"+userName+"_tjb_A where hr_emp_hisdata.a0100=t#"+userName+"_tjb_A.a0100 and hr_emp_hisdata.nbase=t#"+userName+"_tjb_A.nbase )hr_emp_hisdata left join hr_hisdata_list on hr_emp_hisdata.id=hr_hisdata_list.id) hr_emp_hisdata";
			//}
			if(isStat)    // 按取值方式统计值
			{
				String operate=statArray[2];
				String expr2=getCexpr2Context(1,statArray[5]);
				if(!"5".equals(operate))  // 不等于平均人数
				{
					ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
					ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
					String expr_column="";
					String expr_sql ="";
					for(Iterator t=fieldList.iterator();t.hasNext();)
					{
						String temp=(String)t.next();
						expr_column+=","+temp;
						//wangcq 2014-11-25 begin 判断数据类型加上相应的方法
						FieldItem item=(FieldItem)DataDictionary.getFieldItem(temp);
						if("N".equals(item.getItemtype())) {
                            expr_sql+=",sum("+temp+")"+temp;
                        } else if("D".equals(item.getItemtype())) {
                            expr_sql+=",min("+temp+")"+temp;
                        } else {
                            expr_sql+=",max("+temp+")"+temp;
                        }
						//wangcq 2014-11-25 end
					}
					HashMap fieldSet=getFieldTypeMap(fieldList);
					exprUtil.setDecimal(this.decimal);
					expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate);
					this.decimal=exprUtil.getDecimal();
					String sumfirst =getCexpr2Context(12,statArray[5]);//先按人汇总再统计（取值）
					String groupbycolumn=",a0100,nbase ";//默认,a0100,nbase；唯一性指标且为快照指标

					if("1".equals(sumfirst)){
						if(expr_column.length()==0) {
                            throw new GeneralException("指标不存在！");
                        }
						Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
						String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
						String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
						RowSet recset=null;
						ContentDAO dao=new ContentDAO(this.conn);
						if(!("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid))){


							try {
								String str_value = "";

								//wangcq 2015-1-8 新历史时点指标里有数据则不走旧指标
								ConstantXml cx = new ConstantXml(this.conn,"HISPOINT_PARAMETER");
								str_value = cx.getTextValue("Emp_HisPoint/Struct");
								if(StringUtils.isEmpty(str_value)){   //新指标中无数据时
									cx = new ConstantXml(this.conn);
									str_value = cx.getConstantValue("EMP_HISDATA_STRUCT");
								}
								if(str_value!=null&&str_value.length()>0&&str_value.toLowerCase().indexOf(onlyname.toLowerCase())!=-1&&onlyname!=null&&onlyname.trim().length()>0) {
                                    groupbycolumn=","+onlyname;
                                }
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						//去除重复字段
						String groupbycolumn2=groupbycolumn;
						if(expr_column.indexOf("a0100")!=-1) {
                            groupbycolumn2 =groupbycolumn2.replace(",a0100", "");
                        }
						if(expr_column.indexOf("nbase")!=-1) {
                            groupbycolumn2 =groupbycolumn2.replace(",nbase", "");
                        }
						if(expr_column.indexOf(groupbycolumn2)!=-1){
							if(groupbycolumn.indexOf("a0100")==-1){
								groupbycolumn =groupbycolumn+"1";
								groupbycolumn2 = groupbycolumn+"1";
							}
						}
						if("1".equals(operate))  // 求和
						{
							sql.append(" select SUM( ");
						}
						else if("2".equals(operate))  // 求平均值
						{
							sql.append(" select (");
						}
						else if("3".equals(operate))  // 求最大值
						{
							sql.append(" select MAX( ");
						}
						else if("4".equals(operate))  // 求最小值
						{
							sql.append(" select MIN( ");
						}
						else if("6".equals(operate))  // 求中位值
						{
							sql.append(" select ( ");
						}
						if("2".equals(operate))  // 求平均值
						{

							String a_t="";
							if(groupbycolumn.indexOf("a0100")==-1){
								a_t="count("+groupbycolumn.substring(1)+")";
							}else {
                                a_t="count(A0100)";
                            }
							sql.append(Sql_switcher.isnull(" SUM("+expr2+")/NULLIF("+a_t+",0)","0")+" ) v"+j+" from (");

						}
						else
						{
							sql.append(expr2+" ) v"+j+" from (");
						}
						//汇总后再求和/最大值/最小值/平均值/分位值
						sql.append("  select  "+expr_sql.substring(1)+" "+groupbycolumn2+" from "+tablesql);

						sql.append(" where 1=1 ");
						String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);
						if(where_str.trim().length()>0) {
                            sql.append(" and "+where_str);
                        }

						if(pres.length()>0){
							sql.append(" and hr_emp_hisdata.nbase in("+pres+") ");
							if(this.tableTermsMap.size()>0){  //wangcq 2014-12-06   添加项目条件
								String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
								sql.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
							}
						}
						if("1".equals(a_managepriv)){
							sql.append( manageprivstr );
						}
						sql.append( " group by "+groupbycolumn.substring(1)+"" );
						sql.append( " )  hr_emp_hisdata  " );
						if("6".equals(operate))  // 求中位值
						{
							sql.append( "  order by  v"+j+" asc " );

							int i=0;
							HashMap map = new HashMap();
							try {
								recset = dao.search(sql.toString());

								while(recset.next()){
									i++;
									map.put(""+i, recset.getString("v"+j+""));
								}
							} catch (SQLException e) {
							}
							sql.setLength(0);
							String mid =getCexpr2Context(13,statArray[5]);//分位值，如0.1,0.25,0.5,0.75,0.9

							String values ="0";
							if("1".equals(SystemConfig.getPropertyValue("percentile_calcmethod"))){
								// 常规算法（新）  WangJH 2014-06-13
								// ① 分位值*（记录数-1），得出 i.j。 （i,j为整数、小数部分），
								// ②  所求结果＝（1－j）*第(i＋1)个数＋j*第(i+2)个数
								//    等价于  第(i＋1)个数＋j*（第(i+2)个数- 第(i＋1)个数）
								switch (i) {
									case 0:
										break;
									case 1:
										values = ""+map.get("1");
										break;
									default:
										double 	iv = (i-1)*(Double.parseDouble(mid));
										int 	intPart = (int) iv;
										double 	decPart = iv - intPart;

										values = (1-decPart)*Double.parseDouble(map.get(intPart+1+"").toString())
												+ decPart*Double.parseDouble(map.get(intPart+2+"").toString()) + "";
								}
							}else{
								// 原来的算法
								// 结果第n条记录=四舍五入((记录数+1)*分位值)
								int part =(int) Math.round((i+1)*(Double.parseDouble(mid)));//默认=四舍五入
								if(i<=2){

								}else{
									if(part==0){
										values = ""+map.get(part+1+"");
									}else if(part>=i){
										values = ""+map.get(i+"");
									}else{
										values = ""+map.get(part+"");
									}
								}
							}
							sql.append( Sql_switcher.sqlTop("select  "+values+" v"+j+" from  hr_emp_hisdata ",1));
						}

					}else{
						if("1".equals(operate))  // 求和
						{
							sql.append(" select SUM( ");
						}
						else if("2".equals(operate))  // 求平均值
						{
							sql.append(" select (");
						}
						else if("3".equals(operate))  // 求最大值
						{
							sql.append(" select MAX( ");
						}
						else if("4".equals(operate))  // 求最小值
						{
							sql.append(" select MIN( ");
						}
						else if("6".equals(operate))  // 求中位值
						{
							sql.append(" select ( ");
						}
						if("2".equals(operate))  // 求平均值
						{

							String a_t="";

							a_t="count(A0100)";
							sql.append(Sql_switcher.isnull(" SUM("+expr2+")/NULLIF("+a_t+",0)","0")+" ) v"+j+" from "+tablesql);

						}
						else
						{
							sql.append(expr2+" ) v"+j+" from "+tablesql);
						}
						sql.append(" where 1=1 ");
						String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);
						if(where_str.trim().length()>0) {
                            sql.append(" and "+where_str);
                        }

						if(pres.length()>0){
							sql.append(" and hr_emp_hisdata.nbase in("+pres+") ");
							if(this.tableTermsMap.size()>0){  //wangcq 2014-12-06
								String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
								sql.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
							}
						}
						if("1".equals(a_managepriv)){
							sql.append( manageprivstr );
						}
						if("6".equals(operate))  // 求中位值
						{
							sql.append( "  order by  v"+j+" asc " );
							RowSet recset=null;
							ContentDAO dao=new ContentDAO(this.conn);
							int i=0;
							HashMap map = new HashMap();
							try {
								recset = dao.search(sql.toString());

								while(recset.next()){
									i++;
									map.put(""+i, recset.getString("v"+j+""));
								}
							} catch (SQLException e) {
							}
							sql.setLength(0);
							String mid =getCexpr2Context(13,statArray[5]);//分位值，如0.1,0.25,0.5,0.75,0.9
							String values ="0";
//						if ("1"==SystemConfig.getPropertyValue("percentile_calcmethod").trim()){
							if (SystemConfig.getPropertyValue("percentile_calcmethod")!=null &&
									"1".equals(SystemConfig.getPropertyValue("percentile_calcmethod").trim())){
								// 常规算法（新）  WangJH 2014-06-13
								// ① 分位值*（记录数-1），得出 i.j。 （i,j为整数、小数部分），
								// ②  所求结果＝（1－j）*第(i＋1)个数＋j*第(i+2)个数
								//    等价于  第(i＋1)个数＋j*（第(i+2)个数- 第(i＋1)个数）
								switch (i) {
									case 0:
										break;
									case 1:
										values = ""+map.get("1");
										break;
									default:
										double 	iv = (i-1)*(Double.parseDouble(mid));
										int 	intPart = (int) iv;
										double 	decPart = iv - intPart;

										values = (1-decPart)*Double.parseDouble(map.get(intPart+1+"").toString())
												+ decPart*Double.parseDouble(map.get(intPart+2+"").toString()) + "";
								}
							}else{
								// 原来的算法
								// 结果第n条记录=四舍五入((记录数+1)*分位值)
								int part =(int) Math.round((i+1)*(Double.parseDouble(mid)));//默认=四舍五入
								if(i<=2){

								}else{
									if(part==0){
										values = ""+map.get(part+1+"");
									}else if(part>=i){
										values = ""+map.get(i+"");
									}else{
										values = ""+map.get(part+"");
									}
								}
							}
							sql.append( Sql_switcher.sqlTop("select  "+values+" v"+j+" from  hr_emp_hisdata ",1));
						}
					}

				}
//				if(operate.equals("5"))  // 平均人数
//				{
//					if(isGetTempData||statCexpr.length()==0||!statCexpr.equals(getCexpr2Context(1,statArray[5])))
//					{
//
//						statCexpr=getCexpr2Context(1,statArray[5]);
//
//
//						String subStr=statCexpr.substring(statCexpr.indexOf("(")+1,statCexpr.indexOf(")"));
//						String[] temps=subStr.split(";");
//						if(temps[0].trim().substring(0,2).equalsIgnoreCase("Q,"))
//						{
//							isGetTempData=true;
//							Calendar d=Calendar.getInstance();
//							if(appdate==null||appdate.length()==0)
//								appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
//							String[] atemps=appdate.split("-");
//							String a_quarter=getCurrentQarter(atemps);
//							monthList=getMonthByQuarter(a_quarter);
//						}
//						if(temps[0].trim().substring(0,2).equalsIgnoreCase("M,"))
//						{
//							isGetTempData=true;
//							Calendar d=Calendar.getInstance();
//							if(appdate==null||appdate.length()==0)
//								appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
//							String[] atemps=appdate.split("-");
//							monthList.add(atemps[1]);
//						}
//
//						calculateAvgAccountData(isGetTempData,monthList,userName,getCexpr2Context(1,statArray[5]),this.tableTermsMap,appdate);
//					}
//					String[] temp=this.statCexpr.substring(this.statCexpr.indexOf("(")+1,this.statCexpr.indexOf(")")).split(";");
//					if(temp.length>2&&Integer.parseInt(temp[2].trim())>0)
//						this.decimal=Integer.parseInt(temp[2].trim());
//
//					sql.setLength(0);
//					sql.append(getTgridAccountSqls(isGetTempData,monthList,infoList,userName,j,appdate,analyseAvgCexpr2(getCexpr2Context(1,statArray[5])),isTerms,lexprFactor));
////					if(pres.length()>0)
////						sql.append(" and hr_emp_hisdata.nbase in("+pres+") ");
////					if(a_managepriv.equals("1")){
////						sql.append( manageprivstr );
////					}
//				}

			}
			else
			{
				if(statArrayhistory!=null){

					String operate=statArrayhistory[2];
					String expr2=getCexpr2Context(1,statArrayhistory[5]);
					if("1".equals(operate))  // 求个数
					{
						sql.append(" select count("+columnName+") v"+j+" from "+tablesql+" where  1=1  ");
						if(isTerms)
						{
							String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);
							if(where_str.trim().length()>0) {
                                sql.append(" and "+where_str);
                            }
							if(condition.length()>0) {
                                sql.append(" and B0110 in ( select codeitemid from organization where  "+condition+" ) ");
                            }
						}
						else
						{
							//	sql.append(" and 1=2");
						}

					}else if("2".equals(operate))  // 求月平均人数
					{
//						int yy=Integer.parseInt(appdate.substring(0, 4));
//						int mm=Integer.parseInt(appdate.substring(5, 7));
//						int dd=Integer.parseInt(appdate.substring(8, 10));
//						StringBuffer ext_sql = new StringBuffer();
//						ext_sql.append("   ( "+Sql_switcher.year("create_date")+"<"+yy);
//						ext_sql.append(" or ( "+Sql_switcher.year("create_date")+"="+yy+" and "+Sql_switcher.month("create_date")+"<"+mm+" ) ");
//						ext_sql.append(" or ( "+Sql_switcher.year("create_date")+"="+yy+" and "+Sql_switcher.month("create_date")+"="+mm+" and "+Sql_switcher.day("create_date")+"<="+dd+" ) ) ");
						String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);

						String where_str2=getMergeTermsByHistoryTime(lexprFactor,tableName,appdate,userName);
						String str1  ="";
						String str2  ="";
						if(where_str.trim().length()>0) {
                            str1 =" where "+where_str;
                        }
						if(where_str2.trim().length()>0) {
                            str2 =" where "+where_str2;
                        }
						//if(a_managepriv.equals("0")){
						//	tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata , (select list1.* from hr_hisdata_list list1, (select MAX(create_date)create_date  from hr_hisdata_list where "+ext_sql.toString()+"  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.month("create_date")+"  )list2 where list1.create_date=list2.create_date)hr_hisdata_list where hr_emp_hisdata.id=hr_hisdata_list.id ) hr_emp_hisdata";
						String 	tablesql1 ="(select list1.* from (select hr_emp_hisdata.* from ("
								+"select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list"
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata) list1 ,"
								+"	 ("
								+"	 select MAX(create_date)create_date from ("
								+"	 select hr_emp_hisdata.* from ("
								+"		 select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list "
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata"
								+"	 )hr_emp_hisdata "
								+"	  "+str1+""
								+"	  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.month("create_date")+""
								+"	 )list2 where list1.create_date=list2.create_date  "
								+"	 ) hr_emp_hisdata ";
						String 	tablesql2 ="(select list1.* from (select hr_emp_hisdata.* from ("
								+"select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list"
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata) list1 ,"
								+"	 ("
								+"	 select MAX(create_date)create_date from ("
								+"	 select hr_emp_hisdata.* from ("
								+"		 select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list "
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata"
								+"	 )hr_emp_hisdata "
								+"	 "+str2+""
								+"	  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.month("create_date")+""
								+"	 )list2 where list1.create_date=list2.create_date  "
								+"	 ) hr_emp_hisdata ";
						//						}else{
//						 tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  (select hr_emp_hisdata.* from hr_emp_hisdata,t#"+userName+"_tjb_A where hr_emp_hisdata.a0100=t#"+userName+"_tjb_A.a0100 and hr_emp_hisdata.nbase=t#"+userName+"_tjb_A.nbase )hr_emp_hisdata, (select list1.* from hr_hisdata_list list1, (select MAX(create_date)create_date  from hr_hisdata_list  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.month("create_date")+"  )list2 where list1.create_date=list2.create_date)hr_hisdata_list where hr_emp_hisdata.id=hr_hisdata_list.id) hr_emp_hisdata";
//						}
						//sql.append(" select (");
						StringBuffer sql1 = new StringBuffer();
						StringBuffer sql2 = new StringBuffer();
						//sql.append(" select ");
//							String a_t="";
//
//							a_t="count(distinct(id))";

						//	sql.append("("+Sql_switcher.isnull(" count(a0100)","0")+"+"+0.0+")/NULLIF("+Sql_switcher.isnull(a_t+",0)","0")+" ) v"+j+" from "+tablesql);
						sql1.append("select  COUNT(id) idcount,id from "+tablesql1);
						sql2.append("select  COUNT(id) idcount,id from "+tablesql2);

						sql1.append(" where 1=1 ");
						sql2.append(" where 1=1 ");

						if(where_str.trim().length()>0) {
                            sql1.append(" and "+where_str);
                        }
						if(pres.length()>0){
							sql1.append(" and hr_emp_hisdata.nbase in("+pres+") ");
							if(this.tableTermsMap.size()>0){   //wangcq 2014-12-06
								String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
								sql1.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
							}
						}
						if("1".equals(a_managepriv)){
							sql1.append( manageprivstr );
						}
						sql1.append(" group by id order by id ");
						if(where_str2.trim().length()>0){
							sql2.append(" and "+where_str2);
							if(this.tableTermsMap.size()>0){   //wangcq 2014-12-06
								String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
								sql2.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
							}
						}
						if("1".equals(a_managepriv)){
							sql2.append( manageprivstr );
						}
						if(pres.length()>0) {
                            sql2.append(" and hr_emp_hisdata.nbase in("+pres+") ");
                        }
						sql2.append(" group by id order by id ");


						RowSet recset=null;
						ContentDAO dao=new ContentDAO(this.conn);
						//算出每个id对应的时间
						String format_str="yyyy-MM-dd HH:mm";
						try {
							recset = dao.search("   select id,"+Sql_switcher.dateToChar("create_date",format_str)+" create_date from hr_hisdata_list order by create_date,id ");

							HashMap maptime = new HashMap();
							ArrayList listall = new ArrayList();
							ArrayList listall2 =  new ArrayList();
							while(recset.next()){
								maptime.put(recset.getString("id"), recset.getString("create_date"));
								listall.add(recset.getString("id"));
								listall2.add(recset.getString("id"));
							}

							//上月的
							int value = 0;
							String id = "";

							String id2 ="";


							recset = dao.search(sql1.toString());
							int count =0;
							float values = 0;
							HashMap mapvalues = new HashMap();
							while(recset.next()){
								//按时间排序
								String idtemp = recset.getString("id");
								mapvalues.put(idtemp, recset.getString("idcount"));
							}
							Set keySet=	maptime.keySet();

							for(Iterator t=keySet.iterator();t.hasNext();)
							{
								String keyValue=(String)t.next();
								if(mapvalues!=null&&mapvalues.get(keyValue)!=null){

								}else{
									for(int m =0;m<listall.size();m++){
										if(keyValue.equals(""+listall.get(m))){
											listall.remove(m);
											break;
										}
									}
								}
							}
							if(listall.size()>0) {
                                id = ""+listall.get(0);
                            }
							HashMap mapvalues2 = new HashMap();
							recset = dao.search(sql2.toString());
							int value2 =0;
							while(recset.next()){
								//按时间排序
								String idtemp = recset.getString("id");
								mapvalues2.put(idtemp, recset.getString("idcount"));
							}
							keySet=	maptime.keySet();

							for(Iterator t=keySet.iterator();t.hasNext();)
							{
								String keyValue=(String)t.next();
								if(mapvalues2!=null&&mapvalues2.get(keyValue)!=null){

								}else{
									for(int m =0;m<listall2.size();m++){
										if(keyValue.equals(""+listall2.get(m))){
											listall2.remove(m);
											break;
										}
									}
								}
							}
							boolean flag = false;
							for(int m =0;m<listall2.size();m++){
								String idtemp = ""+listall2.get(m);

								//if(id.equals(idtemp)){
								//判断是否给value赋值
								if(maptime!=null&&maptime.get(id)!=null&&maptime.get(idtemp)!=null){
									String time1 = maptime.get(idtemp).toString();
									String time2 = maptime.get(id).toString();
									if((time1.substring(0, 4).equals(time2.substring(0, 4))&&Integer.parseInt(time1.substring(5,7))+1==Integer.parseInt(time2.substring(5,7)))
											||(!time1.substring(0, 4).equals(time2.substring(0, 4))&&Integer.parseInt(time1.substring(0, 4))+1==Integer.parseInt(time2.substring(0, 4))&&Integer.parseInt(time1.substring(5,7))==12&&Integer.parseInt(time2.substring(5,7))==1)){
										value = Integer.parseInt(""+mapvalues2.get(idtemp));
										id2 = idtemp;
										flag =true;
										break;
									}
								}
								//	}
								id2 = idtemp;
								if(mapvalues2!=null&&mapvalues2.get(idtemp)!=null) {
                                    value2 = Integer.parseInt(""+mapvalues2.get(idtemp));
                                }
							}
							for(int m =0;m<listall.size();m++){

								id = ""+listall.get(m);

								if(maptime!=null&&maptime.get(id)!=null&&mapvalues!=null&&mapvalues.get(id)!=null){
									if(maptime.get(id2)==null){
										values +=Integer.parseInt(""+mapvalues.get(id));
										count++;
									}else{
										String time1 = maptime.get(id2).toString();
										String time2 = maptime.get(id).toString();
										if((time1.substring(0, 4).equals(time2.substring(0, 4))&&Integer.parseInt(time1.substring(5,7))+1==Integer.parseInt(time2.substring(5,7)))
												||(!time1.substring(0, 4).equals(time2.substring(0, 4))&&Integer.parseInt(time1.substring(0, 4))+1==Integer.parseInt(time2.substring(0, 4))&&Integer.parseInt(time1.substring(5,7))==12&&Integer.parseInt(time2.substring(5,7))==1)){
											values +=(Integer.parseInt(""+mapvalues.get(id)) +value)/2.0;
											count++;
										}else{
											if(m==0&&!flag){
//										values +=Integer.parseInt(""+mapvalues.get(id))/2.0;   //wangcq 2014-12-09 不清楚这里为什么要再除2
												values +=Integer.parseInt(""+mapvalues.get(id));
												count++;
											}else{
												values +=Integer.parseInt(""+mapvalues.get(id));
												count++;
											}
										}
									}
								}
								id2 = id;
								if(mapvalues!=null&&mapvalues.get(id)!=null) {
                                    value = Integer.parseInt(""+mapvalues.get(id));
                                }
							}
							if(count==0){
								values = 0;
							}else{
								values = values/count;
							}

							sql.append( Sql_switcher.sqlTop("select  "+values+" v"+j+" from  hr_emp_hisdata ",1));
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					else if("3".equals(operate))  // 求季度平均数
					{
//						int yy=Integer.parseInt(appdate.substring(0, 4));
//						int mm=Integer.parseInt(appdate.substring(5, 7));
//						int dd=Integer.parseInt(appdate.substring(8, 10));
//						StringBuffer ext_sql = new StringBuffer();
//						ext_sql.append("   ( "+Sql_switcher.year("create_date")+"<"+yy);
//						ext_sql.append(" or ( "+Sql_switcher.year("create_date")+"="+yy+" and "+Sql_switcher.month("create_date")+"<"+mm+" ) ");
//						ext_sql.append(" or ( "+Sql_switcher.year("create_date")+"="+yy+" and "+Sql_switcher.month("create_date")+"="+mm+" and "+Sql_switcher.day("create_date")+"<="+dd+" ) ) ");
						String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);

						String where_str2=getMergeTermsByHistoryTime(lexprFactor,tableName,appdate,userName);
						String str1  ="";
						String str2  ="";
						if(where_str.trim().length()>0) {
                            str1 =" where "+where_str;
                        }
						if(where_str2.trim().length()>0) {
                            str2 =" where "+where_str2;
                        }
						//if(a_managepriv.equals("0")){
						//	tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata , (select list1.* from hr_hisdata_list list1, (select MAX(create_date)create_date  from hr_hisdata_list where "+ext_sql.toString()+"  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.month("create_date")+"  )list2 where list1.create_date=list2.create_date)hr_hisdata_list where hr_emp_hisdata.id=hr_hisdata_list.id ) hr_emp_hisdata";
						String 	tablesql1 ="(select list1.* from (select hr_emp_hisdata.* from ("
								+"select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list"
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata) list1 ,"
								+"	 ("
								+"	 select MAX(create_date)create_date from ("
								+"	 select hr_emp_hisdata.* from ("
								+"		 select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list "
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata"
								+"	 )hr_emp_hisdata "
								+"	  "+str1+""
								+"	  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.quarter("create_date")+""
								+"	 )list2 where list1.create_date=list2.create_date  "
								+"	 ) hr_emp_hisdata ";
						String 	tablesql2 ="(select list1.* from (select hr_emp_hisdata.* from ("
								+"select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list"
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata) list1 ,"
								+"	 ("
								+"	 select MAX(create_date)create_date from ("
								+"	 select hr_emp_hisdata.* from ("
								+"		 select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata ,hr_hisdata_list "
								+"		where hr_emp_hisdata.id =  hr_hisdata_list.id"
								+"	 )hr_emp_hisdata"
								+"	 )hr_emp_hisdata "
								+"	  "+str2+""
								+"	  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.quarter("create_date")+""
								+"	 )list2 where list1.create_date=list2.create_date  "
								+"	 ) hr_emp_hisdata ";
						//if(a_managepriv.equals("0")){
						//tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  hr_emp_hisdata , (select list1.* from hr_hisdata_list list1, (select MAX(create_date)create_date  from hr_hisdata_list where "+ext_sql.toString()+"  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.quarter("create_date")+" )list2 where list1.create_date=list2.create_date)hr_hisdata_list where hr_emp_hisdata.id=hr_hisdata_list.id ) hr_emp_hisdata";
//						}else{
//						 tablesql = "(select hr_emp_hisdata.*,hr_hisdata_list.create_date from  (select hr_emp_hisdata.* from hr_emp_hisdata,t#"+userName+"_tjb_A where hr_emp_hisdata.a0100=t#"+userName+"_tjb_A.a0100 and hr_emp_hisdata.nbase=t#"+userName+"_tjb_A.nbase )hr_emp_hisdata, (select list1.* from hr_hisdata_list list1, (select MAX(create_date)create_date  from hr_hisdata_list  group by "+Sql_switcher.year("create_date")+","+Sql_switcher.quarter("create_date")+"  )list2 where list1.create_date=list2.create_date)hr_hisdata_list where hr_emp_hisdata.id=hr_hisdata_list.id) hr_emp_hisdata";
//						}
						StringBuffer sql1 = new StringBuffer();
						StringBuffer sql2 = new StringBuffer();
						//sql.append(" select ");
//							String a_t="";
//
//							a_t="count(distinct(id))";

						//	sql.append("("+Sql_switcher.isnull(" count(a0100)","0")+"+"+0.0+")/NULLIF("+Sql_switcher.isnull(a_t+",0)","0")+" ) v"+j+" from "+tablesql);
						sql1.append("select  COUNT(id) idcount,id from "+tablesql1);
						sql2.append("select  COUNT(id) idcount,id from "+tablesql2);

						sql1.append(" where 1=1 ");
						sql2.append(" where 1=1 ");
						if(where_str.trim().length()>0) {
                            sql1.append(" and "+where_str);
                        }
						if(pres.length()>0){
							sql1.append(" and hr_emp_hisdata.nbase in("+pres+") ");
							if(this.tableTermsMap.size()>0){   //wangcq 2014-12-06
								String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
								sql1.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
							}
						}
						if("1".equals(a_managepriv)){
							sql1.append( manageprivstr );
						}
						sql1.append(" group by id order by id ");
						if(where_str2.trim().length()>0) {
                            sql2.append(" and "+where_str2);
                        }
						if(pres.length()>0){
							sql2.append(" and hr_emp_hisdata.nbase in("+pres+") ");
							if(this.tableTermsMap.size()>0){   //wangcq 2014-12-06
								String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
								sql2.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
							}
						}
						if("1".equals(a_managepriv)){
							sql2.append( manageprivstr );
						}
						sql2.append(" group by id order by id ");


						RowSet recset=null;
						ContentDAO dao=new ContentDAO(this.conn);
						//算出每个id对应的时间
						String format_str="yyyy-MM-dd HH:mm";
						try {
							recset = dao.search("   select id,"+Sql_switcher.dateToChar("create_date",format_str)+" create_date from hr_hisdata_list order by create_date,id ");

							HashMap maptime = new HashMap();
							ArrayList listall = new ArrayList();
							ArrayList listall2 = new ArrayList();
							while(recset.next()){
								maptime.put(""+recset.getString("id"), recset.getString("create_date"));
								listall.add(recset.getString("id"));
								listall2.add(recset.getString("id"));
							}


							//上月的
							int value = 0;
							String id = "";

							recset = dao.search(sql1.toString());
							int count =0;
							float values = 0;
							ArrayList list1 = new ArrayList();
							HashMap mapvalues = new HashMap();
							while(recset.next()){
								//按时间排序
								String idtemp = recset.getString("id");
								mapvalues.put(idtemp, recset.getString("idcount"));
							}
							Set keySet=	maptime.keySet();

							for(Iterator t=keySet.iterator();t.hasNext();)
							{
								String keyValue=(String)t.next();
								if(mapvalues!=null&&mapvalues.get(keyValue)!=null){

								}else{
									for(int m =0;m<listall.size();m++){
										if(keyValue.equals(""+listall.get(m))){
											listall.remove(m);
											break;
										}
									}
								}
							}
							if(listall.size()>0) {
                                id = ""+listall.get(0);
                            }
							String id2 ="";
							recset = dao.search(sql2.toString());
							int value2 =0;

							HashMap mapvalues2 = new HashMap();
							while(recset.next()){
								//按时间排序
								String idtemp = recset.getString("id");
								mapvalues2.put(idtemp, recset.getString("idcount"));
							}
							keySet=	maptime.keySet();

							for(Iterator t=keySet.iterator();t.hasNext();)
							{
								String keyValue=(String)t.next();
								if(mapvalues2!=null&&mapvalues2.get(keyValue)!=null){

								}else{
									for(int m =0;m<listall2.size();m++){
										if(keyValue.equals(""+listall2.get(m))){
											listall2.remove(m);
											break;
										}
									}
								}
							}
							boolean flag = false;
							for(int m =0;m<listall2.size();m++){
								String idtemp = ""+listall2.get(m);

								//if(id.equals(idtemp)){
								//判断是否给value赋值
								if(maptime!=null&&maptime.get(id)!=null&&maptime.get(idtemp)!=null){
									String time1 = maptime.get(idtemp).toString();
									String time2 = maptime.get(id).toString();
									if((time1.substring(0, 4).equals(time2.substring(0, 4))&&
											Integer.parseInt(time1.substring(5,7))<Integer.parseInt(time2.substring(5,7))&&Integer.parseInt(time1.substring(5,7))+3>=Integer.parseInt(time2.substring(5,7)))
											||(!time1.substring(0, 4).equals(time2.substring(0, 4))&&Integer.parseInt(time1.substring(0,4))+1==Integer.parseInt(time2.substring(0,4))&&
											Integer.parseInt(time1.substring(5,7))+3>12&&Integer.parseInt(time2.substring(5,7))<4)){
										value = Integer.parseInt(""+mapvalues2.get(idtemp));
										id2 = idtemp;
										flag =true;
										break;
									}
								}
								//}
								id2 = idtemp;
								if(mapvalues2!=null&&mapvalues2.get(idtemp)!=null) {
                                    value2 = Integer.parseInt(""+mapvalues2.get(idtemp));
                                }
							}

							for(int m =0;m<listall.size();m++){

								id = ""+listall.get(m);

								if(maptime!=null&&maptime.get(id)!=null&&mapvalues!=null&&mapvalues.get(id)!=null){
									if(maptime.get(id2)==null){
										values +=Integer.parseInt(""+mapvalues.get(id));
										count++;
									}else{
										String time1 = maptime.get(id2).toString();
										String time2 = maptime.get(id).toString();
										if((time1.substring(0, 4).equals(time2.substring(0, 4))&&
												Integer.parseInt(time1.substring(5,7))<Integer.parseInt(time2.substring(5,7))&&Integer.parseInt(time1.substring(5,7))+3>=Integer.parseInt(time2.substring(5,7)))
												||(!time1.substring(0, 4).equals(time2.substring(0, 4))&&Integer.parseInt(time1.substring(0,4))+1==Integer.parseInt(time2.substring(0,4))&&
												Integer.parseInt(time1.substring(5,7))+3>12&&Integer.parseInt(time2.substring(5,7))<4)){
											values +=(Integer.parseInt(""+mapvalues.get(id)) +value)/2.0;
											count++;
										}else{
											if(m==0&&!flag){
//										values +=Integer.parseInt(""+mapvalues.get(id))/2.0;   //wangcq 2014-12-09 不清楚这里为什么要再除2
												values +=Integer.parseInt(""+mapvalues.get(id));
												count++;
											}else{
												values +=Integer.parseInt(""+mapvalues.get(id));
												count++;
											}
										}
									}
								}
								id2 = id;
								if(mapvalues!=null&&mapvalues.get(id)!=null) {
                                    value = Integer.parseInt(""+mapvalues.get(id));
                                }
							}
							if(count==0){
								values = 0;
							}else{
								values = values/count;
							}
							sql.append( Sql_switcher.sqlTop("select  "+values+" v"+j+" from  hr_emp_hisdata ",1));
						} catch (SQLException e) {
							e.printStackTrace();
						}



					}



				}else{
					sql.append(" select count("+columnName+") v"+j+" from "+tablesql+" where  1=1  ");
//				if(isTerms)
//				{
					String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);
					if(where_str.trim().length()>0) {
                        sql.append(" and "+where_str);
                    }
					if(pres.length()>0){
						sql.append(" and hr_emp_hisdata.nbase in("+pres+") ");
						if(this.tableTermsMap.size()>0){   //wangcq 2014-12-06
							String tableTermsCon = (String)this.tableTermsMap.get(this.dbList.get(0));
							sql.append("and " + tableTermsCon.substring(tableTermsCon.indexOf("where (")+7, tableTermsCon.indexOf(") ) aaa")));
						}
					}
					if("1".equals(a_managepriv)){
						sql.append( manageprivstr );
					}
//				}
//				else
//				{
//					if(a_managepriv.equals("1")){
//						sql.append( manageprivstr );
//					}
//				}
				}

			}
		}

		return sql.toString();
	}
	public String get_qzConditionSql(String[] qzCondition,String pre,String a_fieldSet,String appdate,String userName)
	{
		String qz_conditionstr="";
		try
		{
			if(qzCondition!=null&&getCexpr2Context(7,qzCondition[5]).length()>0)
			{
				String CONDFACTOR=getCexpr2Context(7,qzCondition[5]).replaceAll(",","`");
				String CONDEXPR=getCexpr2Context(8,qzCondition[5]);
				ArrayList tempList=new ArrayList();
				tempList.add(CONDEXPR+"|"+CONDFACTOR+"`");
				qz_conditionstr=getMergeTerms(tempList,pre+a_fieldSet,appdate,userName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return qz_conditionstr;
	}

	String scanMode="";

	/*
	●统计表统计（取值）方法历史记录时间范围，支持直接录入日期(格式yyyy.mm.dd)或起始日期±n月或截止日期±n月
	更新：\\192.192.100.162\dev\HRP7.22\最近变动\tjbdll.dll
	tgrid2.cexpr2格式：
	<EXPR>统计（取值）方法公式</EXPR>
	<SCOPE>
	<MODE>0当前记录，1历史记录，空表示当前记录</MODE>
	<COND>
		--比如，当月：<STARTDATE>当月</STARTDATE><ENDDATE>当月</ENDDATE>
		--      1-当月：<STARTDATE>1</STARTDATE><ENDDATE>当月</ENDDATE>
		<STARTDATE>历史记录条件，开始时间：当月|某月|某年某月|本季|某季度|起始日期|yyyy.mm.dd|起始日期±n月|截止日期±n月</STARTDATE>
		<ENDDATE>结束时间：当月|某月|某年某月|本季|某季度|截止日期|yyyy.mm.dd|截止日期±n月|起始日期±n月</ENDDATE>
		<CONDFACTOR>子集历史记录条件A7910=11,A7910=12</CONDFACTOR> <CONDEXPR>1*2</CONDEXPR>
	</COND>
	</SCOPE>
	<COUNTSET>求个数子集，默认为主集</COUNTSET>
	<SUMFLAG>归档数据汇总发放1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和</SUMFLAG>
	<MANAGEPRIV>按权限范围统计:0:不限制, 1:限制, 默认为限制</MANAGEPRIV>

	*/
	/**
	 * 返回扫描单元格的sql语句
	 *
	 * @param infoList
	 *            统计条件
	 * @param userName
	 *            用户名
	 * @param j
	 *            列号
	 * @param scanMode
	 *            扫描范围 1：人员库 2：单位 3：单位、部门 4：部门 5 职位库
	 * @param appdate
	 *            截止日期
	 * @return
	 */
	public String getTgridSqls(ArrayList infoList,String userName,int j,String scanMode,String appdate,ArrayList midVariableList,String[] rowbottomtemp,String[] colbottomtemp)throws GeneralException
	{
		StringBuffer sql=new StringBuffer("");
		this.infoList=infoList;
		//取得临时表临时变量视图
		String sub_sql=getSubSql(midVariableList,userName);
		String columnName="";
		String tableName="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			boolean  isStat=false;			// 是否含有统计个数条件
			int      flag1=0;               // 是否取值方式中 含有表达式 或 序号
			String[] statArray=null;		//统计取值对应的格，必须是底层格
			String[] statArrayhistory=null;	//历史时点取个数专用
			ArrayList lexprFactor=new ArrayList();
			ArrayList lexprFactorhistory=new ArrayList();//放设置取历史，的表达式
			ArrayList lexprFactornothistory=new ArrayList();//放一般的表达式
			this.scanMode=scanMode;
			if("1".equals(scanMode))
			{
				columnName="A0100";
				tableName="t#"+userName+"_tjb_A";
			}
			else if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode))
			{
				columnName="B0110";
				tableName="t#"+userName+"_tjb_B";
			}
			else if("5".equals(scanMode))
			{
				columnName="E01A1";
				tableName="t#"+userName+"_tjb_K";
			}
			else if("6".equals(scanMode))
			{
				columnName="A0100";
				tableName="hr_emp_hisdata";
			}
			boolean isTerms=false;


			/////////////////////////////////////


//			判断此列是否需要取历史数据
			String a_fieldSet="";
			String a_fielditem="";
			String a_managepriv="1";        //是否按管理范围取数；  0：不限制  1：限制
			boolean  isGetTempData=false;   //是否需要重新取数
			String[] qzCondition=null;
			String condition_str="";
			ArrayList monthList=new ArrayList();
			String model="";  //0当前记录，1历史记录，空表示当前记录
			String isCount="0";  //是否有求子集个数
			String[] history_temp=null;
			String historytime = "";  //1表示取值统计方法，设置统计历史时间指标为""
			ArrayList historyfieldlist= new ArrayList(); //存放指标
			String[] temphistory=null;//用于是走取值还是走别的
			for(Iterator t=infoList.iterator();t.hasNext();)
			{
				String[] temp0=(String[])t.next();

				if("1".equals(temp0[1])&&!"5".equals(temp0[2]))  //取值
				{
					ArrayList fieldORvaribleList=exprUtil.statExprAnalyse(getCexpr2Context(1,temp0[5]),midVariableList);
					ArrayList fieldList=(ArrayList)fieldORvaribleList.get(0);      // 指标集和
					ArrayList variableList=(ArrayList)fieldORvaribleList.get(1);   // 临时变量集合
					if(fieldList.size()>0)
					{
						FieldItem item=(FieldItem)DataDictionary.getFieldItem((String)fieldList.get(0));
						a_fieldSet=item.getFieldsetid();
						temphistory=temp0;
						a_fielditem=getCexpr2Context(5,temp0[5]);
						if("null".equals(a_fielditem)){
							a_fielditem="null";
							historytime="1";
						}
						else
						{
							historytime="";
						}

						if((a_fielditem.trim().length()==0|| "null".equals(a_fielditem))&&!"A01".equalsIgnoreCase(a_fieldSet)&&!"B01".equalsIgnoreCase(a_fieldSet)&&!"K01".equalsIgnoreCase(a_fieldSet)&&a_fieldSet.length()>0)
						{
							a_fielditem=a_fieldSet+"z0";
						}

					}
					if(fieldList.size()>0||variableList.size()>0) {
                        a_managepriv=getCexpr2Context(6,temp0[5]);
                    }
					//		if(getCexpr2Context(2,temp0[5]).equals("1"))
					for(Iterator k=infoList.iterator();k.hasNext();)
					{
						String[] _temp0=(String[])k.next();
						//	if(!_temp0[6].equals(temp0[6]))     // temp0[6]为当前单元格号 JinChunhai 2012.09.13
						{
							if("1".equals(getCexpr2Context(2,_temp0[5])))
							{
								qzCondition=temp0;
								break;
							}
						}
					}
					if(temp0[5]!=null&&temp0[5].length()>0&&history_temp==null&& "1".equals(getCexpr2Context(2,temp0[5])))
					{
						history_temp=temp0;
					}

				}
				if("2".equals(temp0[1]))  //求个数
				{
					if(!"主集".equals(getCexpr2Context(4,temp0[5])))
					{
						a_fieldSet=getCexpr2Context(4,temp0[5]);
						temphistory=temp0;
						a_fielditem=getCexpr2Context(5,temp0[5]);
						if("null".equals(a_fielditem)){
							a_fielditem="null";
							historytime="1";
						}
						qzCondition=temp0;
						isCount="1";
					}
					if("1".equals(a_managepriv)) {
                        a_managepriv=getCexpr2Context(6,temp0[5]);
                    }
					if(temp0[5]!=null&&temp0[5].length()>0&&(history_temp==null)&& "1".equals(getCexpr2Context(2,temp0[5])))
					{
						history_temp=temp0;
					}
				}


			}
			if("1".equals(rowbottomtemp[1]))                    // 取值
			{
				statArray=rowbottomtemp;
				isStat=true;
			}
			if("1".equals(colbottomtemp[1]))                    // 取值
			{
				statArray=colbottomtemp;
				isStat=true;
			}
			for(Iterator t=infoList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if("5".equals(temp[1]))
				{

					return " select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' "	;
				}
				else if("3".equals(temp[1])|| "4".equals(temp[1]))	// 碰到取值方式为表达式 或	 序号，不予写数据
				{
					if("4".equals(temp[1])&& "0".equals(temp[9]))
					{
						continue;
					}
					sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
					flag1=1;
					break;
				}
				else if("1".equals(temp[1]))                    // 取值
				{



					if(temp[3]!=null&&temp[3].length()>1)
					{
						lexprFactor.add(temp[4] + "|" + temp[3]);
						isTerms=true;
						if(temphistory!=null&& "1".equals(temphistory[1])&&!"5".equals(temphistory[2])){//定义的是取值
							if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了取值方法的历史条件
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}else{
								if("1".equals(getCexpr2Context(2,temp[5]))){
									lexprFactorhistory.add(temp[4] + "|" + temp[3]);
								}else{
									lexprFactornothistory.add(temp[4] + "|" + temp[3]);
								}
							}
						}else{
							if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了求个数的历史条件
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}else{
								if("1".equals(getCexpr2Context(2,temp[5]))){
									lexprFactorhistory.add(temp[4] + "|" + temp[3]);
								}else{
									lexprFactornothistory.add(temp[4] + "|" + temp[3]);
								}
							}
						}
					}

//					if(temp[3]!=null&&temp[3].length()>1)
//					{
//						lexprFactor.add(temp[4] + "|" + temp[3]);
//						isTerms=true;
//					}
				}
				else if("2".equals(temp[1]))					// 统计个数
				{
					if(!"1".equals(temp[2])) {
                        statArrayhistory = temp;
                    }
					lexprFactor.add(temp[4] + "|" + temp[3]);
					if(temp[3]!=null&&temp[3].length()>1) {
                        isTerms=true;
                    }
					if(temphistory!=null&& "1".equals(temphistory[1])&&!"5".equals(temphistory[2])){//定义的是取值
						if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了取值方法的历史条件
							lexprFactornothistory.add(temp[4] + "|" + temp[3]);
						}else{
							if("1".equals(getCexpr2Context(2,temp[5]))){
								lexprFactorhistory.add(temp[4] + "|" + temp[3]);
							}else{
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}
						}
					}else{
						if(qzCondition!=null&&qzCondition[3]!=null&&qzCondition[3].length()>1&&getCexpr2Context(7,qzCondition[5]).length()>0){//定义了求个数的历史条件
							lexprFactornothistory.add(temp[4] + "|" + temp[3]);
						}else{
							if("1".equals(getCexpr2Context(2,temp[5]))){
								lexprFactorhistory.add(temp[4] + "|" + temp[3]);
							}else{
								lexprFactornothistory.add(temp[4] + "|" + temp[3]);
							}
						}
					}

				}
			}

			if(a_fielditem.length()>0&&history_temp!=null&&history_temp[5]!=null&&history_temp[5].length()>0&&("1".equals(isCount)|| "1".equals(getCexpr2Context(2,history_temp[5]))))
			{
				if(!"1".equals(historytime))//直接走历史定义的时间范围  需在取值公式中定义了时间范围指标
                {
                    condition_str=getCondition_str(history_temp,a_fieldSet,appdate,a_fieldSet+"."+a_fielditem);  //取得采集数据 历史范围 条件
                }
				//   	monthList=getMonthFromHistory(history_temp,a_fieldSet,appdate);
				model=getCexpr2Context(2,history_temp[5]);
				isGetTempData=true;
				historytime="1";
			}

			if("0".equals(a_managepriv))
			{

				if("1".equals(scanMode)){
					tableName=tableName+"_c";
					sub_sql=sub_sql.replaceAll("t#"+userName+"_tjb_A","t#"+userName+"_tjb_A_c");
				}
			}
			///////////////////////////////
			if((a_fielditem.length()>0&&
					isGetTempData&&
					sql.length()==0&&a_fieldSet.length()>0&&!(isStat&& "5".equals(statArray[2])))  //取历史数据
					|| StringUtils.equals(isCount, "1"))  //取子集记录
			{
				if(StringUtils.equals(isCount, "1")) //20151103 dengcan  sql不置空将与下面语句获得的SQL产生冲突
                {
                    sql.setLength(0);
                }
				if("1".equals(scanMode)){
					sql.append( getHistoryDataSql(isTerms,flag1,isStat,statArray,midVariableList,a_fieldSet,sub_sql,condition_str
							,userName,j,appdate,lexprFactorhistory,tableName,columnName,model,a_fielditem,a_managepriv,qzCondition,lexprFactornothistory));
				}else if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)|| "5".equals(scanMode)){
					sub_sql = " select "+columnName+" from "+tableName+" ";
					sql.append( getBKHistoryDataSql(isTerms,flag1,isStat,statArray,midVariableList,a_fieldSet,sub_sql,condition_str
							,userName,j,appdate,lexprFactorhistory,tableName,columnName,model,a_fielditem,a_managepriv,qzCondition,historytime,scanMode,lexprFactornothistory));
				}
			}
			else if("6".equals(scanMode)){

				sql.append( getPersonHistoryTimeDataSql( flag1,scanMode,isStat,statArray,statArrayhistory, midVariableList, isGetTempData, isTerms,
						userName, j, appdate, lexprFactor, tableName, columnName,monthList,infoList,a_managepriv,"1"));

			}
			else
			{
				//	if(isTerms||1==1)  // isTerms 判断是否有统计条件
				{
					if(flag1==0)
					{
						String condition="";
						StringBuffer ext_sql = new StringBuffer();
						if("2".equals(scanMode)) {
                            condition=" codesetid='UN' ";
                        }
						if("3".equals(scanMode)) {
                            condition=" (codesetid='UN' or codesetid='UM') ";
                        }
						if("4".equals(scanMode)) {
                            condition=" codesetid='UM' ";
                        }
						if("4".equals(scanMode)|| "3".equals(scanMode)|| "2".equals(scanMode)){
							Calendar d=Calendar.getInstance();
							int yy=d.get(Calendar.YEAR);
							int mm=d.get(Calendar.MONTH)+1;
							int dd=d.get(Calendar.DATE);
							String date = getBusinessDate();
							if(date!=null&&date.trim().length()>0)
							{
								d.setTime(java.sql.Date.valueOf(date));
								yy=d.get(Calendar.YEAR);
								mm=d.get(Calendar.MONTH)+1;
								dd=d.get(Calendar.DATE);
							}
							ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
							ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
							ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
							ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
							ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
							ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
							condition =condition+ext_sql;
						}
						if(isStat)    // 按取值方式统计值
						{
							String operate=statArray[2];
							String expr2=getCexpr2Context(1,statArray[5]);
							if(!"5".equals(operate))  // 不等于平均人数
							{
								ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
								ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
								HashMap fieldSet=getFieldTypeMap(fieldList);
								exprUtil.setDecimal(this.decimal);
								expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate);
								this.decimal=exprUtil.getDecimal();
								if("1".equals(operate))  // 求和
								{
									sql.append(" select SUM( ");
								}
								else if("2".equals(operate))  // 求平均值
								{
									sql.append(" select (");
								}
								else if("3".equals(operate))  // 求最大值
								{
									sql.append(" select MAX( ");
								}
								else if("4".equals(operate))  // 求最小值
								{
									sql.append(" select MIN( ");
								}

								if("2".equals(operate))  // 求平均值
								{

									String a_t="";
									if("1".equals(scanMode))
									{
										a_t="count(A0100)";
									}
									else if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode))
									{
										a_t="count(B0110)";
									}
									else if("5".equals(scanMode))
									{
										a_t="count(E01A1)";
									}
									sql.append(Sql_switcher.isnull(" SUM("+expr2+")/NULLIF("+a_t+",0)","0")+" ) v"+j+" from "+tableName);

								}
								else
								{
									sql.append(expr2+" ) v"+j+" from "+tableName);
								}
								sql.append(" where 1=1 ");
								String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);
								if(where_str.trim().length()>0) {
                                    sql.append(" and "+where_str);
                                }

								if(condition.length()>0) {
                                    sql.append(" and B0110 in ( select codeitemid from organization where  "+condition+"  ) ");
                                }

								if(operate.equals(""+6)){//分位值仅仅支持快照
									sql.setLength(0);
								}
							}
							if("5".equals(operate))  // 平均人数
							{
								if(isGetTempData||statCexpr.length()==0||!statCexpr.equals(getCexpr2Context(1,statArray[5])))
								{

									statCexpr=getCexpr2Context(1,statArray[5]);


									String subStr=statCexpr.substring(statCexpr.indexOf("(")+1,statCexpr.indexOf(")"));
									String[] temps=subStr.split(";");
									if("Q,".equalsIgnoreCase(temps[0].trim().substring(0,2)))
									{
										isGetTempData=true;
										Calendar d=Calendar.getInstance();
										if(appdate==null||appdate.length()==0) {
                                            appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                                        }
										String[] atemps=appdate.split("-");
										String a_quarter=getCurrentQarter(atemps);
										monthList=getMonthByQuarter(a_quarter);
									}
									if("M,".equalsIgnoreCase(temps[0].trim().substring(0,2)))
									{
										isGetTempData=true;
										Calendar d=Calendar.getInstance();
										if(appdate==null||appdate.length()==0) {
                                            appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                                        }
										String[] atemps=appdate.split("-");
										monthList.add(atemps[1]);
									}

									calculateAvgAccountData(isGetTempData,monthList,userName,getCexpr2Context(1,statArray[5]),this.tableTermsMap,appdate);
								}
								String[] temp=this.statCexpr.substring(this.statCexpr.indexOf("(")+1,this.statCexpr.indexOf(")")).split(";");
								if(temp.length>2&&Integer.parseInt(temp[2].trim())>0) {
                                    this.decimal=Integer.parseInt(temp[2].trim());
                                }

								sql.setLength(0);
								sql.append(getTgridAccountSqls(isGetTempData,monthList,infoList,userName,j,appdate,analyseAvgCexpr2(getCexpr2Context(1,statArray[5])),isTerms,lexprFactor));
							}

						}
						else
						{

							sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where  1=1  ");
							if(isTerms)
							{
								String where_str=getMergeTerms(lexprFactor,tableName,appdate,userName);
								if(where_str.trim().length()>0) {
                                    sql.append(" and "+where_str);
                                }
								if(condition.length()>0) {
                                    sql.append(" and B0110 in ( select codeitemid from organization where  "+condition+"   ) ");
                                }
							}
							else
							{
								//	sql.append(" and 1=2");
							}

						}
					}

				}
			/*	else if(!isTerms&&flag1==0)
				{
					if(isStat)    // 按取值方式统计值
					{
						String operate=statArray[2];
						String expr2=getCexpr2Context(1,statArray[5]);
						if(!operate.equals("5"))  // 不等于平均人数
						{
							ArrayList statExprArrayList=exprUtil.statExprAnalyse(expr2,midVariableList);
							ArrayList fieldList=(ArrayList)statExprArrayList.get(0);
							HashMap fieldSet=getFieldTypeMap(fieldList);
							exprUtil.setDecimal(this.decimal);
							expr2=exprUtil.tranNormalExpr(expr2,midVariableList,fieldSet,appdate);
							this.decimal=exprUtil.getDecimal();
							if(operate.equals("1"))  // 求和
							{
								sql.append(" select SUM( ");
							}
							else if(operate.equals("2"))  // 求平均值
							{
								sql.append(" select AVG( ");
							}
							else if(operate.equals("3"))  // 求最大值
							{
								sql.append(" select MAX( ");
							}
							else if(operate.equals("4"))  // 求最小值
							{
								sql.append(" select MIN( ");
							}
							sql.append(expr2);
							sql.append(" ) v"+j+" from "+tableName);

						}
						if(operate.equals("5"))  // 平均人数
						{
							if(statCexpr.length()==0||!statCexpr.equals(getCexpr2Context(1,statArray[5])))
							{
								statCexpr=getCexpr2Context(1,statArray[5]);
								calculateAvgAccountData(userName,getCexpr2Context(1,statArray[5]),this.tableTermsMap,appdate);
							}

							String[] temp=this.statCexpr.substring(this.statCexpr.indexOf("(")+1,this.statCexpr.indexOf(")")).split(";");
							if(temp.length>2&&Integer.parseInt(temp[2].trim())>0)
								this.decimal=Integer.parseInt(temp[2].trim());
							sql.setLength(0);
							sql.append(getTgridAccountSqls(infoList,userName,j,appdate,analyseAvgCexpr2(getCexpr2Context(1,statArray[5])),isTerms,lexprFactor));
						}


					}
					else
					{
						System.out.println("ddd");
						sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");

					}

				}
			*/

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(sql.toString().length()<2) {
            sql.append(" select count("+columnName+") v"+j+" from "+tableName+" where "+columnName+"='abc' ");
        }


		return sql.toString();
	}


	/**
	 * 获取归档取数sql  zhaoxg 2013-2-26
	 * @return
	 */
	public String getTAsqls(String tabid,String unitcode,ArrayList infoList,String userName,int j,String scanMode,String appdate,ArrayList midVariableList,String []rowbottomtemp,String[]colbottomtemp){
		StringBuffer sql=new StringBuffer("");

		try{
			for(Iterator t=infoList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if("5".equals(temp[1]))
				{
//					 System.out.println(temp[2]+"AAA");
					String cexpr1 = temp[2];//1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数 6：取值
					String cexpr21 = getCexpr2Context(14,rowbottomtemp[5]);//归档指标代号
					String cexpr22 = getCexpr2Context(15,rowbottomtemp[5]);//归档类型
					String cexpr23 = getCexpr2Context(16,rowbottomtemp[5]);//开始时间
					String cexpr24 = getCexpr2Context(17,rowbottomtemp[5]);//结束时间
					//	System.out.println(cexpr21+"@"+cexpr22+"@"+cexpr23+"@"+cexpr24);
					if("1".equals(cexpr1))  // 求和
					{
						sql.append(" select SUM( ");
					}
					else if("2".equals(cexpr1))  // 求平均值
					{
						sql.append(" select (");
					}
					else if("3".equals(cexpr1))  // 求最大值
					{
						sql.append(" select MAX( ");
					}
					else if("4".equals(cexpr1))  // 求最小值
					{
						sql.append(" select MIN( ");
					}
					else if("5".equals(cexpr1))  // 求平均人数
					{
						sql.append(" select MIN( ");
					}
					else if("6".equals(cexpr1))  // 取值
					{
						sql.append("( select row_item as row_item,"+cexpr21+" as v"+j+" from ta_"+tabid+" where unitcode="+unitcode+" and 1=1 ) as a"+j+"");
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}


	/**
	 * 合并条件
	 *
	 * @param lexprFactor
	 * @param tableName
	 * @return
	 */
	public String getBKMergeTerms(ArrayList lexprFactor,String tableName,String appdate,String userName)throws GeneralException
	{
		String whl="";
		try
		{
			//设置截至日期
			if(appdate!=null) {
                ConstantParamter.putAppdate(userName,appdate.substring(0,4)+"."+appdate.substring(5,7)+"."+appdate.substring(8));
            }
			String lexpr="";
			String strFactor="";
			// 合并条件表达式
			CombineFactor combinefactor=new CombineFactor();

			//表条件定义历史条件
			if(this.tableTermsMap.get("B_history")!=null)
			{
				ArrayList B_history=(ArrayList)this.tableTermsMap.get("B_history");
				for (int a = 0; a < B_history.size(); a++) {
					String[] tableTerms = (String[]) B_history.get(a);
					if (tableTerms[3].length() > 1) {
						lexprFactor.add(tableTerms[4] + "|" + tableTerms[3]);
					}
				}
			}
			if(this.tableTermsMap.get("K_history")!=null)
			{
				ArrayList K_history=(ArrayList)this.tableTermsMap.get("K_history");
				for (int a = 0; a < K_history.size(); a++) {
					String[] tableTerms = (String[]) K_history.get(a);
					if (tableTerms[3].length() > 1) {
						lexprFactor.add(tableTerms[4] + "|" + tableTerms[3]);
					}
				}
			}
			if(lexprFactor.size()<=0){
				return "1=1";
			}
			//	lexprFactor.add(temp[4] + "|" + temp[3]);



			String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");

			if(Stok.hasMoreTokens())
			{
				{
					lexpr=Stok.nextToken();
					strFactor=Stok.nextToken();
				}
			}
			String strFactortemp[] = strFactor.split("`");
			int flag=1;
			for(int i=0;i<strFactortemp.length;i++){
				if(strFactortemp[i].indexOf("=")!=-1){
					String fieldname = strFactortemp[i].substring(0,strFactortemp[i].indexOf("="));
					FieldItem item=DataDictionary.getFieldItem(fieldname);
					if(item==null)
					{
					}else{
						if(tableName.indexOf("_tjb_")==-1){
							if("B0110".equalsIgnoreCase(item.getItemid())|| "E01A1".equalsIgnoreCase(item.getItemid()))
							{

							}
							else if(!item.getFieldsetid().equalsIgnoreCase(tableName)){
								flag=0;
								break;
							}
						}
					}
				}
			}


			// 调用陈总提供的表达式分析器的到sql语句
			if(strFactor.length()>12&&strFactor.indexOf("$THISUNIT[]")!=-1)
			{


				if(this.userview.isSuper_admin()||"UN`".equalsIgnoreCase(this.userview.getUnit_id())) {
                    strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
                } else
				{
					String unit_ids=this.userview.getUnit_id();
					if(unit_ids==null||unit_ids.trim().length()==0)
					{
						strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
					}
					else
					{
						String[] temps=unit_ids.split("`");
						StringBuffer un=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String temp=temps[i];
								String pre=temp.substring(0,2);
								String value=temp.substring(2);
								if("UN".equalsIgnoreCase(pre))
								{
									un.append("|"+value);
								}
								else
								{
									if(this.scanMode==null|| "3".equalsIgnoreCase(this.scanMode)){
										un.append("|"+value);
									}else{
										un.append("|"+getUnByUm(value));
									}
								}
							}

						}
						if(un.length()>0)
						{

							strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]",un.substring(1));

						}
						else {
                            strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
                        }


					}

				}
			}
			//起始日期 §§ 截止日期
			if(strFactor.length()>12&&(strFactor.indexOf("$APPSTARTDATE[]")!=-1||strFactor.indexOf("$APPDATE[]")!=-1))
			{
				Calendar d=Calendar.getInstance();
				if(startdate==null||startdate.length()==0) {
                    startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				String _startdate=startdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
                {
                    strFactor=strFactor.replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
                }

				String _appdate=appdate;
				if(appdate==null) {
                    _appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				_appdate=_appdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPDATE[]")!=-1) //截止日期
                {
                    strFactor=strFactor.replaceAll("\\$APPDATE\\[\\]",_appdate);
                }
			}
			strFactor = repaceBiaoshi(strFactor,appdate);
			FactorList factorlist=new FactorList(lexpr,strFactor,userName);

			FactorList factorlist2 = (FactorList)factorlist.clone();
			int length = factorlist.size();

			for(int i =0;i<length;i++){
				factorlist.remove(0);
			}
			for(int i =0;i<length;i++){
				Factor vo =  null ;
				vo=(Factor)factorlist2.get(i);

				if("$TONGBIQTR[]".equalsIgnoreCase(vo.getValue())){
					vo.setValue("$THISQTR[]");
					int temp =Integer.parseInt(""+appdate.substring(0, 4)) ;
					temp = temp-1;
					vo.setAppdate(temp+appdate.substring(4, appdate.length()));
				}
				if("$LASTQTR[]".equalsIgnoreCase(vo.getValue())){
					vo.setValue("$THISQTR[]");
					int temp =Integer.parseInt(""+appdate.substring(5, 7)) ;
					String add = "";
					if(temp>3){
						temp = temp-3;
						add=appdate.substring(0,5)+"0"+temp+appdate.substring(7,appdate.length());
					}
					else{
						temp = temp+12-3;
						int temp2 =Integer.parseInt(""+appdate.substring(0, 4)) ;
						temp2 = temp2-1;
						if(temp<10) {
                            add=temp2+appdate.substring(4,5)+"0"+temp+appdate.substring(7,appdate.length());
                        } else {
                            add=temp2+appdate.substring(4,5)+temp+appdate.substring(7,appdate.length());
                        }
					}
					vo.setAppdate(add);
				}
				factorlist.add(vo);
			}

			if(tableName.indexOf("_tjb_")>-1||flag==1){
				whl=factorlist.getSingleTableSqlExpression(tableName);
			}else{
				factorlist= new FactorList(
						lexpr, strFactor, "",
						false, false, true, 1, userName);

				factorlist2 = (FactorList)factorlist.clone();
				length = factorlist.size();

				for(int i =0;i<length;i++){
					factorlist.remove(0);
				}
				for(int i =0;i<length;i++){
					Factor vo =  null ;
					vo=(Factor)factorlist2.get(i);

					if("$TONGBIQTR[]".equalsIgnoreCase(vo.getValue())){
						vo.setValue("$THISQTR[]");
						int temp =Integer.parseInt(""+appdate.substring(0, 4)) ;
						temp = temp-1;
						vo.setAppdate(temp+appdate.substring(4, appdate.length()));
					}
					if("$LASTQTR[]".equalsIgnoreCase(vo.getValue())){
						vo.setValue("$THISQTR[]");
						int temp =Integer.parseInt(""+appdate.substring(5, 7)) ;
						String add = "";
						if(temp>3){
							temp = temp-3;
							add=appdate.substring(0,5)+"0"+temp+appdate.substring(7,appdate.length());
						}
						else{
							temp = temp+12-3;
							int temp2 =Integer.parseInt(""+appdate.substring(0, 4)) ;
							temp2 = temp2-1;
							if(temp<10) {
                                add=temp2+appdate.substring(4,5)+"0"+temp+appdate.substring(7,appdate.length());
                            } else {
                                add=temp2+appdate.substring(4,5)+temp+appdate.substring(7,appdate.length());
                            }
						}
						vo.setAppdate(add);
					}
					factorlist.add(vo);
				}


				String	strwhere2 = factorlist.getSqlExpression();
				strwhere2=	strwhere2.replace("i9999=(", "I9999 in (");
				strwhere2=	strwhere2.replace("I9999=(", "I9999 in (");
				strwhere2=	strwhere2.replace("max(I9999)", "I9999  ");
				strwhere2=strwhere2.replace("MAX(I9999)", "I9999  ");
				//if(strwhere2.indexOf(tableName)!=-1){
				if(tableName.startsWith("K")){
					strwhere2=	strwhere2.replaceFirst("A01","t#"+userName+"_tjb_K" );
					strwhere2=strwhere2.replace("A01.","t#"+userName+"_tjb_K." );
					strwhere2=	strwhere2.replace("B0110","E0122" );//君正集团49号报表前后取数不一致 2015-10-12
					//	strwhere2=strwhere2 = "  exists (select null from ( select "+"t#"+userName+"_tjb_K.E01A1,"+tableName+".I9999 " +strwhere2+") c where  c.E01A1="+tableName+".E01A1 and c.I9999="+tableName+".I9999 )";
					strwhere2=strwhere2 = "  exists (select null from ( select "+"t#"+userName+"_tjb_K.E01A1  " +strwhere2+") c where  c.E01A1="+tableName+".E01A1  )";
				}
				else{
					strwhere2=strwhere2.replaceFirst("E0122","B0110" );
					strwhere2=strwhere2.replaceFirst("A01","t#"+userName+"_tjb_B" );
					strwhere2=strwhere2.replace("A01.", "t#"+userName+"_tjb_B.");
					//strwhere2=strwhere2 = "  exists (select null from ( select "+"t#"+userName+"_tjb_B.B0110,"+tableName+".I9999 " +strwhere2+") c where  c.B0110="+tableName+".B0110 and c.I9999="+tableName+".I9999 )";
					strwhere2=strwhere2 = "  exists (select null from ( select "+"t#"+userName+"_tjb_B.B0110  " +strwhere2+") c where  c.B0110="+tableName+".B0110  )";
				}
				whl=strwhere2;
//				}else{
//					whl=factorlist.getSingleTableSqlExpression(tableName);
//				}

			}
			if(whl.trim().length()<=0){
				whl = " 1=1 ";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return whl;
	}


	/**
	 * 合并条件
	 * 去掉归档日期
	 * @param lexprFactor
	 * @param tableName
	 * @return
	 */
	public String getMergeTermsByHistoryTime(ArrayList lexprFactor,String tableName,String appdate,String userName)throws GeneralException
	{
		String whl="";
		try
		{
			//设置截至日期
			if(appdate!=null) {
                ConstantParamter.putAppdate(userName,appdate.substring(0,4)+"."+appdate.substring(5,7)+"."+appdate.substring(8));
            }
			String lexpr="";
			String strFactor="";
			// 合并条件表达式
			CombineFactor combinefactor=new CombineFactor();



			//	lexprFactor.add(temp[4] + "|" + temp[3]);
			String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
			if(Stok.hasMoreTokens())
			{
				lexpr=Stok.nextToken();
				strFactor=Stok.nextToken();
			}
			// 调用陈总提供的表达式分析器的到sql语句
			if(strFactor.length()>12&&strFactor.indexOf("$THISUNIT[]")!=-1)
			{


				if(this.userview.isSuper_admin()) {
                    strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
                } else
				{
					String unit_ids=this.userview.getUnit_id();
					if(unit_ids==null||unit_ids.trim().length()==0)
					{
						strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
					}
					else
					{
						String[] temps=unit_ids.split("`");
						StringBuffer un=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String temp=temps[i];
								String pre=temp.substring(0,2);
								String value=temp.substring(2);
								if("UN".equalsIgnoreCase(pre))
								{
									un.append("|"+value+"*");//update by xiegh bug36865 date 20180425 查询本单位及下级单位
								}
								else
								{
									if(this.scanMode==null|| "3".equalsIgnoreCase(this.scanMode)){
										un.append("|"+value);
									}else{
										un.append("|"+getUnByUm(value));
									}
								}
							}

						}
						if(un.length()>0)
						{

							strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]",un.substring(1));

						}
						else {
                            strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
                        }


					}

				}
			}
			//起始日期 §§ 截止日期
			if(strFactor.length()>12&&(strFactor.indexOf("$APPSTARTDATE[]")!=-1||strFactor.indexOf("$APPDATE[]")!=-1))
			{
				Calendar d=Calendar.getInstance();
				if(startdate==null||startdate.length()==0) {
                    startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				String _startdate=startdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
                {
                    strFactor=strFactor.replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
                }

				String _appdate=appdate;
				if(appdate==null) {
                    _appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				_appdate=_appdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPDATE[]")!=-1) //截止日期
                {
                    strFactor=strFactor.replaceAll("\\$APPDATE\\[\\]",_appdate);
                }
			}
			boolean flag =false;
			if(strFactor.indexOf("create_date")!=-1){
				strFactor = strFactor.replace("create_date=", "nbase<>");
				strFactor = strFactor.replace("create_date<=", "nbase<>");
				strFactor = strFactor.replace("create_date>=", "nbase<>");
				strFactor = strFactor.replace("create_date<", "nbase<>");
				strFactor = strFactor.replace("create_date>", "nbase<>");
				strFactor = strFactor.replace("create_date", "nbase");
			}
			strFactor = repaceBiaoshi(strFactor,appdate);
			FactorList factorlist=new FactorList(lexpr,strFactor,userName);
			factorlist = getStandardFactorList(factorlist,appdate);

			whl=factorlist.getSingleTableSqlExpression(tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return whl;
	}
	/**
	 * 合并条件
	 *
	 *
	 * @param lexprFactor
	 * @param tableName
	 * @return
	 */
	public String getMergeTerms(ArrayList lexprFactor,String tableName,String appdate,String userName)throws GeneralException
	{
		String whl="";
		try
		{
			//设置截至日期
			if(appdate!=null) {
                ConstantParamter.putAppdate(userName,appdate.substring(0,4)+"."+appdate.substring(5,7)+"."+appdate.substring(8));
            }
			String lexpr="";
			String strFactor="";
			// 合并条件表达式
			CombineFactor combinefactor=new CombineFactor();



			//	lexprFactor.add(temp[4] + "|" + temp[3]);
			String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
			if(Stok.hasMoreTokens())
			{
				lexpr=Stok.nextToken();
				strFactor=Stok.nextToken();
			}
			// 调用陈总提供的表达式分析器的到sql语句
			if(strFactor.length()>12&&strFactor.indexOf("$THISUNIT[]")!=-1)
			{


				if(this.userview.isSuper_admin()) {
                    strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
                } else
				{
					String unit_ids=this.userview.getUnit_id();
					if(unit_ids==null||unit_ids.trim().length()==0)
					{
						strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
					}
					else
					{
						String[] temps=unit_ids.split("`");
						StringBuffer un=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String temp=temps[i];
								String pre=temp.substring(0,2);
								String value=temp.substring(2);
								if("UN".equalsIgnoreCase(pre))
								{
									un.append("|"+value+"*");
								}
								else
								{
									if(this.scanMode==null|| "3".equalsIgnoreCase(this.scanMode)){
										un.append("|"+value+"*");
									}else{
										un.append("|"+getUnByUm(value)+"*");
									}

								}
							}

						}
						if(un.length()>0)
						{
							if(un.length()==1){//条件为本单位且操作单位为全部，这时传*，sql拼成like ‘%’ 形式  zhaoxg add 2013-12-31
								strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
							}else{
								strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]",un.substring(1));
							}
						}
						else {
                            strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
                        }


					}

				}
			}
			//起始日期 §§ 截止日期
			if(strFactor.length()>12&&(strFactor.indexOf("$APPSTARTDATE[]")!=-1||strFactor.indexOf("$APPDATE[]")!=-1))
			{
				Calendar d=Calendar.getInstance();
				if(startdate==null||startdate.length()==0) {
                    startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				String _startdate=startdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
                {
                    strFactor=strFactor.replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
                }



				String _appdate=appdate;


				if(appdate==null) {
                    _appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				_appdate=_appdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPDATE[]")!=-1) //截止日期
                {
                    strFactor=strFactor.replaceAll("\\$APPDATE\\[\\]",_appdate);
                }
			}
			boolean flag =false;
			if(strFactor.indexOf("create_date")!=-1){
				strFactor = strFactor.replace("create_date", "nbase");
				flag = true;
			}
			strFactor = repaceBiaoshi(strFactor,appdate);
			FactorList factorlist=new FactorList(lexpr,strFactor,userName);
			factorlist = getStandardFactorList(factorlist,appdate);

			whl=factorlist.getSingleTableSqlExpression(tableName);
			if(flag) {
                whl = whl.replace("nbase", "create_date");
            }
			//------------------------------------------------------zhaoxg start ---------------
			boolean _flag=false;
			if(this.infoList.size()>0){
				for(Iterator t=infoList.iterator();t.hasNext();)
				{
					String[] temp=(String[])t.next();
					if("4".equals(temp[0])){//行列条件里有取部门的
						_flag=true;
					}
				}
			}
			if(tableName.indexOf("_tjb_A")!=-1&&whl.indexOf(tableName+".B0110")!=-1&&_flag){//取部门而带有B0110字样的   都加上or e0122   zhaoxg 2014-2-7
				StringBuffer tempwhl=new StringBuffer();
				String[] tempsql=whl.split(tableName+".B0110");
				for(int i=0;i<tempsql.length;i++){
					if(i==0){
						tempwhl.append(tempsql[i]);
					}else{
						StringBuffer _temp=new StringBuffer();
						String[] tempsqls = tempsql[i].split("\'");
						if(tempsqls.length > 1){
							_temp.append(tableName+".B0110 "+tempsql[i].split("\'")[0]+"'"+tempsql[i].split("\'")[1]+"' or "+tableName+".E0122 "+tempsql[i]);
						}else{
							_temp.append(tableName+".B0110 "+tempsql[i].split("\'")[0]+tableName+".E0122 "+tempsql[i]);
						}
						tempwhl.append(_temp);
					}
				}
				whl=tempwhl.toString();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return whl;
	}
	/**
	 * 合并条件
	 * 子集和临时表分开
	 *
	 * @param lexprFactor
	 * @param tableName
	 * @return
	 */
	public String getMergeTerms(ArrayList lexprFactor,String tableName,String appdate,String userName,String dbpre,String fieldset)throws GeneralException
	{
		String whl="";
		try
		{
			//设置截至日期
			if(appdate!=null) {
                ConstantParamter.putAppdate(userName,appdate.substring(0,4)+"."+appdate.substring(5,7)+"."+appdate.substring(8));
            }
			String lexpr="";
			String strFactor="";
			// 合并条件表达式
			CombineFactor combinefactor=new CombineFactor();


			HashMap map = new HashMap();//存放替换为子集的指标,key:pre子集.指标 ,value:tableName.指标
			//	lexprFactor.add(temp[4] + "|" + temp[3]);
			String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
			if(Stok.hasMoreTokens())
			{
				lexpr=Stok.nextToken();
				strFactor=Stok.nextToken();
				String temp [] =strFactor.split("`");
				for(int j=0;j<temp.length;j++){
					if(temp[j].length()>5){
						FieldItem item=DataDictionary.getFieldItem(temp[j].trim().substring(0, 5));
						if(item!=null){
							if(fieldset.equals(item.getFieldsetid())){
								map.put((dbpre+fieldset+"."+temp[j].trim().substring(0, 5)).toLowerCase(), (tableName+"."+temp[j].trim().substring(0, 5)).toLowerCase());
							}
						}
					}
				}
			}
			// 调用陈总提供的表达式分析器的到sql语句
			if(strFactor.length()>12&&strFactor.indexOf("$THISUNIT[]")!=-1)
			{


				if(this.userview.isSuper_admin()) {
                    strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
                } else
				{
					String unit_ids=this.userview.getUnit_id();
					if(unit_ids==null||unit_ids.trim().length()==0)
					{
						strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
					}
					else
					{
						String[] temps=unit_ids.split("`");
						StringBuffer un=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String temp=temps[i];
								String pre=temp.substring(0,2);
								String value=temp.substring(2);
								if("UN".equalsIgnoreCase(pre))
								{
									un.append("|"+value+"*");//update by xiegh bug36865 date 20180425 查询本单位及下级单位
								}
								else
								{
									if(this.scanMode==null|| "3".equalsIgnoreCase(this.scanMode)){
										un.append("|"+value);
									}else{
										un.append("|"+getUnByUm(value));
									}
								}
							}

						}
						if(un.length()>0)
						{

							strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]",un.substring(1));

						}
						else {
                            strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
                        }


					}

				}
			}

			//起始日期 §§ 截止日期
			if(strFactor.length()>12&&(strFactor.indexOf("$APPSTARTDATE[]")!=-1||strFactor.indexOf("$APPDATE[]")!=-1))
			{
				Calendar d=Calendar.getInstance();
				if(startdate==null||startdate.length()==0) {
                    startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				String _startdate=startdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
                {
                    strFactor=strFactor.replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
                }



				String _appdate=appdate;


				if(appdate==null) {
                    _appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				_appdate=_appdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPDATE[]")!=-1) //截止日期
                {
                    strFactor=strFactor.replaceAll("\\$APPDATE\\[\\]",_appdate);
                }
			}
			boolean flag =false;
			if(strFactor.indexOf("create_date")!=-1){
				strFactor = strFactor.replace("create_date", "nbase");
				flag = true;
			}
			strFactor = repaceBiaoshi(strFactor,appdate);
			FactorList factorlist=new FactorList(lexpr,strFactor,userName);
			factorlist = getStandardFactorList(factorlist,appdate);

			whl=factorlist.getSingleTableSqlExpression(tableName);
			if(flag) {
                whl = whl.replace("nbase", "create_date");
            }
			for(Iterator t=map.keySet().iterator();t.hasNext();){
				String keyValue=(String)t.next();
				String  value=(String)map.get(keyValue);
				whl =whl.toLowerCase();
				whl = whl.replace(value, keyValue);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return whl;
	}
	/**
	 * 获取时间条件上年，上月等
	 * @param strFactor 统计条件
	 * @param _appdate 时间
	 * @return
	 */
	public String repaceBiaoshi(String strFactor,String _appdate){
//		上年$LASTYS[]           截止日期年份-1，无月日
//		上年同月$TONGBIMONTH[]  截止日期年份-1，月不变，不考虑日
//		上年同季$TONGBIQTR[]    截止日期年份-1，季度不变
//		上月$LASTMONTH[]        截止日期月份-1，年、日不变,跨年的，年份-1
//		上季度$LASTQTR[]        本季度-1，年不变，不考虑日,跨年的，年份-1
//		今天$TODAY[]
//		起始日期$APPSTARTDATE[]
//		截止日期$APPDATE[]
		String temp_appdate = _appdate;
		int temp =0;
		//String strFactor ="$LASTYS[],$TONGBIMONTH[],$TONGBIQTR[],$LASTMONTH[],$LASTQTR[]";
		if(strFactor.indexOf("$LASTYS[]")!=-1){
			temp =Integer.parseInt(""+_appdate.substring(0, 4)) ;
			temp = temp-1;
			strFactor = strFactor.replace("$LASTYS[]", ""+temp);
		}
		if(strFactor.indexOf("$TONGBIMONTH[]")!=-1){
			temp =Integer.parseInt(""+_appdate.substring(0, 4)) ;
			temp = temp-1;
			temp_appdate =temp+_appdate.substring(4, 7) ;
			strFactor = strFactor.replace("$TONGBIMONTH[]", temp_appdate);
		}
//		if(strFactor.indexOf("$TONGBIQTR[]")!=-1){
//			temp_appdate =_appdate.substring(0, 7) ;
//			strFactor =  strFactor.replace("$TONGBIQTR[]", temp_appdate);
//		}
		if(strFactor.indexOf("$LASTMONTH[]")!=-1){

			if(Integer.parseInt(""+_appdate.substring(5,7))!=1){
				temp =Integer.parseInt(""+_appdate.substring(5, 7)) ;
				temp = temp-1;
				String lastMonth = temp+"";
				if(temp<10) {
					lastMonth = "0"+temp;
				}
				_appdate = _appdate.substring(0, 5)+ lastMonth +_appdate.substring(9,10);
			}else{
				temp =Integer.parseInt(""+_appdate.substring(0, 4)) ;
				temp = temp-1;
				_appdate =	temp+_appdate.substring(4,5)+"12"+_appdate.substring(7,10);
			}
			temp_appdate =_appdate.substring(0, 7) ;
			strFactor = 	strFactor.replace("$LASTMONTH[]", temp_appdate);
		}

		return strFactor;

	}
	/**
	 * wangcq 2015-1-17 将上季度$LASTQTR[]、上年同季度$TONGBIQTR[]转成本季度，同时将业务日期改成相应的日期
	 * @param factorlist
	 * @param appdate
	 * @return
	 */
	public FactorList getStandardFactorList(FactorList factorlist,String appdate){
		FactorList clonefactorlist = (FactorList)factorlist.clone();
		int length = factorlist.size();

		for(int i =0;i<length;i++){
			factorlist.remove(0);
		}
		for(int i =0;i<length;i++){
			Factor vo =  null ;
			vo=(Factor)clonefactorlist.get(i);
			if("nbase".equalsIgnoreCase(vo.getFieldname())){
				vo.setFieldtype("D");
			}
			if("$TONGBIQTR[]".equalsIgnoreCase(vo.getValue())){
				vo.setValue("$THISQTR[]");
				int temp =Integer.parseInt(""+appdate.substring(0, 4)) ;
				temp = temp-1;
				vo.setAppdate(temp+appdate.substring(4, appdate.length()));
			}
			if("$LASTQTR[]".equalsIgnoreCase(vo.getValue())){
				vo.setValue("$THISQTR[]");
				int temp =Integer.parseInt(""+appdate.substring(5, 7)) ;
				String add = "";
				if(temp>3){
					temp = temp-3;
					add=appdate.substring(0,5)+"0"+temp+appdate.substring(7,appdate.length());
				}
				else{
					temp = temp+12-3;
					int temp2 =Integer.parseInt(""+appdate.substring(0, 4)) ;
					temp2 = temp2-1;
					if(temp<10) {
                        add=temp2+appdate.substring(4,5)+"0"+temp+appdate.substring(7,appdate.length());
                    } else {
                        add=temp2+appdate.substring(4,5)+temp+appdate.substring(7,appdate.length());
                    }
				}
				vo.setAppdate(add);
			}
			factorlist.add(vo);
		}
		return factorlist;
	}
	/**
	 * 合并条件
	 *
	 * @param lexprFactor
	 * @param tableName
	 * @return
	 */
	public String getMergeTerms2(ArrayList lexprFactor,String tableName,String appdate,String userName,String pre,String maintablename)throws GeneralException
	{
		String whl="";
		try
		{
			//设置截至日期
			if(appdate!=null) {
                ConstantParamter.putAppdate(userName,appdate.substring(0,4)+"."+appdate.substring(5,7)+"."+appdate.substring(8));
            }
			String lexpr="";
			String strFactor="";
			// 合并条件表达式
			CombineFactor combinefactor=new CombineFactor();

			if(lexprFactor.size()<=0){
				return "1=1";
			}
			HashMap map = new HashMap();//存放替换为子集的指标,key:pre子集.指标 ,value:tableName.指标
			//	lexprFactor.add(temp[4] + "|" + temp[3]);
			String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
			if(Stok.hasMoreTokens())
			{
				lexpr=Stok.nextToken();
				strFactor=Stok.nextToken();
				String temp [] =strFactor.split("`");
				for(int j=0;j<temp.length;j++){
					if(temp[j].length()>5){
						FieldItem item=DataDictionary.getFieldItem(temp[j].trim().substring(0, 5));
						if(item!=null){
							if(tableName.equalsIgnoreCase(pre+item.getFieldsetid())){
								map.put((pre+item.getFieldsetid()+"."+temp[j].trim().substring(0, 5)), (maintablename+"."+temp[j].trim().substring(0, 5)));
							}
						}
					}
				}
			}
			// 调用陈总提供的表达式分析器的到sql语句
			if(strFactor.length()>12&&strFactor.indexOf("$THISUNIT[]")!=-1)
			{


				if(this.userview.isSuper_admin()) {
                    strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
                } else
				{
					String unit_ids=this.userview.getUnit_id();
					if(unit_ids==null||unit_ids.trim().length()==0)
					{
						strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
					}
					else
					{
						String[] temps=unit_ids.split("`");
						StringBuffer un=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String temp=temps[i];
								String pre2=temp.substring(0,2);
								String value=temp.substring(2);
								if("UN".equalsIgnoreCase(pre2))
								{
									un.append("|"+value+"*");//update by xiegh bug36865 date 20180425 查询本单位及下级单位
								}
								else
								{
									if(this.scanMode==null|| "3".equalsIgnoreCase(this.scanMode)){
										un.append("|"+value);
									}else{
										un.append("|"+getUnByUm(value));
									}
								}
							}

						}
						if(un.length()>0)
						{

							strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]",un.substring(1));

						}
						else {
                            strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
                        }

					}

				}
			}
			//起始日期 §§ 截止日期
			if(strFactor.length()>12&&(strFactor.indexOf("$APPSTARTDATE[]")!=-1||strFactor.indexOf("$APPDATE[]")!=-1))
			{
				Calendar d=Calendar.getInstance();
				if(startdate==null||startdate.length()==0) {
                    startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				String _startdate=startdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
                {
                    strFactor=strFactor.replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
                }

				String _appdate=appdate;
				if(appdate==null) {
                    _appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
                }
				_appdate=_appdate.replaceAll("-","\\.");
				if(strFactor.indexOf("$APPDATE[]")!=-1) //截止日期
                {
                    strFactor=strFactor.replaceAll("\\$APPDATE\\[\\]",_appdate);
                }
			}

//			FactorList factorlist=new FactorList(lexpr,strFactor,userName);
//			whl=factorlist.getSingleTableSqlExpression(tableName);

			strFactor = repaceBiaoshi(strFactor,appdate);
			FactorList factorlist=new FactorList(
					lexpr, strFactor, pre,
					false, false, true, 1, userName);
			factorlist = getStandardFactorList(factorlist,appdate);


//			String	strwhere2 = factorlist.getSqlExpression();
//			strwhere2=	strwhere2.replace("I9999=(", "I9999 in (");
//			strwhere2=	strwhere2.replace("i9999=(", "I9999 in (");
//			strwhere2=	strwhere2.replace("max(I9999)", "I9999  ");
//			strwhere2=strwhere2.replace("MAX(I9999)", "I9999  ");
//
//				strwhere2=	strwhere2.replaceFirst(pre+"A01","t#"+userName+"_tjb_A" );
//				strwhere2=strwhere2.replace(pre+"A01.","t#"+userName+"_tjb_A." );
//				if(strwhere2.indexOf(tableName)!=-1){
//				if(tableName.indexOf("_tjb_A_c")!=-1){
//					strwhere2 =	strwhere2.replaceFirst("t#"+userName+"_tjb_A","t#"+userName+"_tjb_A_c" );
//				}
//				strwhere2 = "  exists (select null from ( select "+"t#"+userName+"_tjb_A.A0100,"+tableName+".I9999 " +strwhere2+") c where  c.A0100="+tableName+".A0100 and c.I9999="+tableName+".I9999 )";
//				whl =strwhere2;
//				}else{
			if(strFactor.trim().length()<=1){
				whl = " 1=1 ";
			}else{
				factorlist=new FactorList(lexpr,strFactor,userName);
				factorlist = getStandardFactorList(factorlist,appdate);


				if(maintablename.equalsIgnoreCase("t#"+userName+"_tjb_A")){
					whl=factorlist.getSingleTableSqlExpression("t#"+userName+"_tjb_A");
				}else{
					whl=factorlist.getSingleTableSqlExpression(maintablename);
				}
				for(Iterator t=map.keySet().iterator();t.hasNext();){
					String keyValue=(String)t.next();
					String  value=(String)map.get(keyValue);
					//	whl =whl.toLowerCase();   //小写会有问题，如果条件里定义了字符是大写
					whl = whl.replace(value, keyValue);
				}
				if(whl.trim().length()<=0){
					whl = " 1=1 ";
				}
			}
//				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return whl;
	}




	//根据部门找单位
	public String getUnByUm(String umCode)
	{
		String un="##";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			int n=0;
			while(true)
			{

				n++;
				if(n>6)
				{
					break;
				}
				String sql="select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+umCode+"')";
				recset=dao.search(sql);
				if(recset.next())
				{
					if("UN".equalsIgnoreCase(recset.getString("codesetid")))
					{
						un=recset.getString("codeitemid");
						break;
					}
					else {
                        umCode=recset.getString("codeitemid");
                    }
				}

			}
			if(recset!=null) {
                recset.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return un;
	}
	public boolean isSetOwnerDate(ArrayList list)throws GeneralException{
		boolean flag =false;
		for(int i=0;i<list.size();i++){
			if(flag) {
                break;
            }
			String tabid = (String)list.get(i);
			HashMap rowMap=new HashMap();
			HashMap colMap=new HashMap();
			ArrayList rowAndColInfoList=getRowAndColInfoList(getGridInfoList(tabid),tabid,rowMap,colMap);
			//	ArrayList  rowInfoList=(ArrayList)rowAndColInfoList.get(0);
			ArrayList colInfoList=(ArrayList)rowAndColInfoList.get(1);
			for(int j=0;j<colInfoList.size();j++)
			{
				if(flag) {
                    break;
                }
				ArrayList colTermList=(ArrayList)colInfoList.get(j);
				int fromIndex=0;
				int toIndex=0;
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
							if (te1.length()>0&&(te2.indexOf("M")!=-1||te2.indexOf("Q")!=-1||te2.indexOf("Y")!=-1)) {
								flag=true;
								break;
							}

						}
					}
				}
			}
		}
		return flag;
	}

	public String getNewDate(String startdate,String opertor,int num){
		String returndate="";
		int n = num/12+1;
		int month = Integer.parseInt(startdate.substring(4,7));
		if("-".equals(opertor)){
			if(month>num){
				if(month-num<10) {
                    returndate=startdate.substring(0,5)+"0"+(month-num)+startdate.substring(7,startdate.length());
                } else {
                    returndate=startdate.substring(0,5)+(month-num)+startdate.substring(7,startdate.length());
                }
			}else{
				int temp2 =Integer.parseInt(""+startdate.substring(0, 4)) ;
				temp2 = temp2-n;
				if((month+12*n-num)<10) {
                    returndate=temp2+startdate.substring(4,5)+"0"+(month+12*n-num)+startdate.substring(7,startdate.length());
                } else {
                    returndate=temp2+startdate.substring(4,5)+(month+12*n-num)+startdate.substring(7,startdate.length());
                }
			}
		}else{
			n = (month+num)/12;
			int yu = (month+num)%12;
			if(yu==0){
				n = n-1;
				yu = 12;
			}
			int temp2 =Integer.parseInt(""+startdate.substring(0, 4)) ;
			temp2 = temp2+n;
			if(yu<10) {
                returndate=temp2+startdate.substring(4,5)+"0"+yu+startdate.substring(7,startdate.length());
            } else {
                returndate=temp2+startdate.substring(4,5)+yu+startdate.substring(7,startdate.length());
            }
		}
		return startdate;
	}

	//获得业务日期
	public String getBusinessDate(){

		String appdate = ""; // 截止日期
		RowSet rs= null;
		ContentDAO dao = new ContentDAO(this.conn);
		String xml = "";
		try {
			// 常量表中查找rp_param常量
			rs = dao
					.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
			if (rs.next()) {
				xml = Sql_switcher.readMemo(rs, "STR_VALUE");
				// xml文件分析类
				AnalyseParams aps = new AnalyseParams(xml);
				if (aps.checkUserid(this.userview.getUserName())) {// DB中存在当前用户的扫描库配置信息
					// 用户配置信息封装在MAP内
					HashMap hm = aps.getAttributeValues(this.userview.getUserName());
					appdate = (String) hm.get("appdate"); // 起始日期
				}else{
					if(ConstantParamter.getAppdate(this.userview.getUserName())!=null)
					{
						String value=ConstantParamter.getAppdate(this.userview.getUserName()).replaceAll("\\.","-");
						appdate=value;
					}
				}

			}else{
				if(ConstantParamter.getAppdate(this.userview.getUserName())!=null)
				{
					String value=ConstantParamter.getAppdate(this.userview.getUserName()).replaceAll("\\.","-");
				}
			}
			rs.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return  appdate;

	}
	public ArrayList getDbList() {
		return dbList;
	}




	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}




	public String getResult() {
		return result;
	}




	public void setResult(String result) {
		this.result = result;
	}




	public UserView getUserview() {
		return userview;
	}




	public void setUserview(UserView userview) {
		this.userview = userview;
	}




	public HashMap getTableTermsMap() {
		return tableTermsMap;
	}




	public void setTableTermsMap(HashMap tableTermsMap) {
		this.tableTermsMap = tableTermsMap;
	}




	public int getDecimal() {
		return decimal;
	}




	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}




	public String getStartdate() {
		return startdate;
	}




	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

}
