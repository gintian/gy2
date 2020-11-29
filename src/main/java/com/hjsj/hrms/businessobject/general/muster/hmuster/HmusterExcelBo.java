package com.hjsj.hrms.businessobject.general.muster.hmuster;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class HmusterExcelBo {
	private HSSFWorkbook wb=null;
	private Workbook xWb=null;
	
	private HSSFSheet sheet=null;
	private Sheet xsheet=null;
	
	private HSSFPatriarch patriarch=null;
	private Drawing   xpatriarch=null;
	
	private HSSFCellStyle style=null;
	private XSSFCellStyle xstyle=null;
	
	private HSSFCellStyle style_noZro=null;
	private XSSFCellStyle xstyle_noZro=null;
	
	private HSSFCellStyle style_l=null;
	private XSSFCellStyle xstyle_l=null;
	
	private HSSFCellStyle style_r=null;
	private XSSFCellStyle xstyle_r=null;
	
	private HSSFCellStyle style_cc=null;
	private XSSFCellStyle xstyle_cc=null;
	
	private HSSFCellStyle style_ccc=null;
	private XSSFCellStyle xstyle_ccc=null;
	
	private HSSFRow row=null;
	private Row xrow=null;
	
	private HSSFCell cell=null;
	private Cell xcell=null;
	
	private int index_x=0;
	private int index_y=0;
	private String a0100="";
	private HashMap row_l_map=new HashMap();
	private HashMap row_t_map=new HashMap();
	private ArrayList rowInfoList=new ArrayList();
	private ArrayList rowInfoBGrid=new ArrayList();		 //横表栏底层单元格列表（按顺序排列）
	/** 表格行数 */
	private int rowLayNum=1;
	private double tab_width=0;						  //表宽度 （像素）
	private double tab_height=0;						  //表高度 （像素）
	private int rows=20;
	
	private Connection conn;                            //DB连接
	private ArrayList resultList=new ArrayList();
	private double[]      itemGridArea=new double[4];        //表项目区域 l,t,w,h
	private ArrayList  gridList=new ArrayList();        //表头单元格信息集合
	private ArrayList  pageList=new ArrayList();        //表标题信息集合
	private ArrayList  bodyList=new ArrayList();
	private ArrayList  topPageList=new ArrayList();				//上标题
	private ArrayList  bottomPageList=new ArrayList();				//下标题
	private ArrayList allPageList=new ArrayList();					//所有标题
	private HashMap allParamMap=new HashMap();		    //所有参数值				
	private double top_pix=0;
	private double bottom_pix=0;
	private int topParamLayNum=0;					   // 表头 标题层数
	private HashMap topLayMap=new HashMap();
	private int bottomParamLayNum=0;				   // 表尾 标题层数
	private HashMap botLayMap=new HashMap();
	private String emptyRow="";  //空行打印
	
	private HashMap addInfoMap=new HashMap();//需要合计的单元格信息
	private HashMap bodyMap=new HashMap(); //标题放入表格里面的数据
	private RecordVo  hmusterVo=null;
	private double pageWidth=0;						   //页宽度（像素）
	private double pageHeight=0;					   //页高度（像素）
	public static float scale = 0.27f;
	
	
	private UserView   userView=null;
	private int        opt=0;                           // 0:不分栏 1：横向分栏 2：纵向分栏
	private String     infor_Flag="";					//信息群标示
	private String     tabid="";
	
	private int    totalRowNum=0;         //总行数
	private String zeroPrint="0"; 		  //0:不为零打印  1：零打印
	private String privConditionStr="";
	private String isGroupPoint="0";      //是否选用分组指标  1:选用
	private String groupPoint="";            //已选的分组指标
	private String tableName="";
	private String dbpre="";
	private String history="0";    //1:最后一条历史纪录  3：某次历史纪录 2：部分历史纪录 
	private String year="";
	private String month="";
	private String count="";
	private String printGrid="1";		  //打印格线     0:不打印  1：打印
	private String modelFlag="";         //模块标识
	private boolean isPhoto = false; // 表体是否显示照片
	private HashMap  gridNoMap=null;
	
	private boolean isyxj=false;  
	private boolean isylj=false;
	private boolean isfzhj=false;
	private boolean iszj=false;
	private boolean isfzhj2=false;
	private String dataarea="";
	private String column="";
	private ArrayList rowHeightList=new ArrayList();
	private ArrayList columnWidthList=new ArrayList();
	
	private String isGroupNoPage="0";  // 1:分组不分页
    private String isGroupedSerials="0";//1:按分组显示序列
	private String groupNcode ="";
	private String groupVcode ="";
	private String sql="";
	private int fromRow=0;
	private String isGroupPoint2="0";
	private String groupPoint2="";
	private boolean isGroupTerm2=false;
	private HmusterViewBo hmusterViewBo=null;
	private boolean isGroupV2=false;
	private String groupCount="0";
	private String yearmonth="";
	/**花名册数据区，每个row的高度*/
	private short height=0;
	/**花名册中是否有照片*/
	private boolean isHasPhoto=false;
	private String showPartJob="false";//是否显示兼职人员
	
	//liuy 2014-12-20 新增高级花名册控制excel页面设置参数 start
	private static short A3_PAPERSIZE = 8;
    private static short A4_PAPERSIZE = HSSFPrintSetup.A4_PAPERSIZE;
    private static short A5_PAPERSIZE = HSSFPrintSetup.A5_PAPERSIZE;
    private static short B5_PAPERSIZE = 13;
    private int excelType=0;//导出excel格式 xls 0 或 xlsx 1 后缀名文件 
    private ArrayList fontList=new ArrayList();
    private ArrayList fontXList=new ArrayList();
    //liuy end
    private String tablename="";//当前表名称
    
    private HashMap topDateTitleMap = new HashMap();//高级花名册日期型上标题Map集合
    
	public void setShowPartJob(String showPartJob)
	{
		this.showPartJob=showPartJob;
	}
	public HmusterExcelBo(Connection con,int a_opt,UserView a_userView,String infor_Flag, String tabid,HmusterViewBo hmusterViewBo)
	{
		this.conn=con;
		this.userView=a_userView;
		this.opt=a_opt;
		this.infor_Flag=infor_Flag;
		this.tabid=tabid;
		this.hmusterViewBo=hmusterViewBo;
		init();
	}
	public HmusterExcelBo(Connection con,int a_opt,UserView a_userView,String infor_Flag, String tabid)
	{
		this.conn=con;
		this.userView=a_userView;
		this.opt=a_opt;
		this.infor_Flag=infor_Flag;
		this.tabid=tabid;
		init();
	}
	public boolean isHaveGroup2(String tableName)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData data=rs.getMetaData();
			String group2="groupv2";
			for(int i=1;i<=data.getColumnCount();i++)
			{
				String columnName=data.getColumnName(i).toLowerCase();
				if(group2.toLowerCase().equalsIgnoreCase(columnName))
				{
					flag=true;
					break;
				}
				else {
                    continue;
                }
			}
		}
		catch(Exception e)
		{
			flag=false;
		}
		
		return flag;
	}
	/**
	 * 取得各单元格 高度 or 宽度
	 * @param flag 1:高度 2：宽度
	 * @return
	 */
	private ArrayList getExcelGridSizeList(int flag)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			String sql2="";
			if(flag==1)
			{
				sql="select rtop  from muster_cell where tabid="+this.tabid+" group  by rtop order by rtop";
				sql2="select max(rtop+rheight)  from muster_cell where tabid="+this.tabid;
			}
			else if(flag==2)
			{
				sql="select rleft  from muster_cell where tabid="+this.tabid+" group  by rleft order by rleft ";
				sql2="select max(rleft+rwidth)  from muster_cell where tabid="+this.tabid;
			}
			String value1="0";
			String value2="0";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next()) {
                value1=rowSet.getString(1);
            }
			int tt=0;
			int index=0;
			while(rowSet.next())
			{
				if(flag==1)
				{
			    	value2=rowSet.getString(1);
			    	int value=Integer.parseInt(PubFunc.subtract(value2,value1,0));
			    	if(index==0) {
                        tt=value;
                    }
			    	if(value<=15)
			    	{
			    		//list.add(new Integer(tt+""));
			    		continue;
			    	}
			    	else
			    	{
			    		tt=value;
			    		list.add(new Integer(tt+""));
			    	}
			    	value1=value2;
				}
				else
				{
					value2=rowSet.getString(1);
			    	list.add(new Integer(PubFunc.subtract(value2,value1,0)));
			    	value1=value2;
				}
				index++;
			}
			rowSet=dao.search(sql2);
			if(rowSet.next())
			{
				value2=rowSet.getString(1);
				list.add(new Integer(PubFunc.subtract(value2,value1,0)));
			}
			return list;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	
	public void init()
	{
		try
		{
			
			this.gridList=getGridInfoList();
			this.addInfoMap=getAddInfoMap();
			 //	分析表格,得到报表横表栏和纵表栏的相关信息集合
			this.itemGridArea=getItemGridArea();
			this.top_pix=itemGridArea[1];
			this.bottom_pix=itemGridArea[1]+itemGridArea[3];
//			标题数据初始化
			this.hmusterVo=getHmusterVoById();
			this.tablename=this.hmusterVo.getString("cname");//获取花名册表名称
			if (this.hmusterVo.getInt("paperori")==1) {  //横向  横向为2 纵向为1 纸张方向
				this.pageHeight = (this.hmusterVo.getDouble("paperh") / scale)*10;
				this.pageWidth= (this.hmusterVo.getDouble("paperw")/ scale)*10;
			}
			else									   //横向  宽=>高  高=>宽
			{
				this.pageHeight = (this.hmusterVo.getDouble("paperw") / scale)*10;
				this.pageWidth= (this.hmusterVo.getDouble("paperh")/ scale)*10;
			}
			
			this.pageList=getPageList();
			getResetPageList();//取得修饰后的 表标题 数据集和
			this.rowInfoList=recordToBean(this.gridList);//参数位置等信息
			this.rowInfoBGrid=getBottomGridInfoList();
			this.row_l_map=getTabNumMap("rleft");
			this.row_t_map=this.getTopMap();//getTabNumMap("rtop");t
			this.tab_width=itemGridArea[0]+itemGridArea[2];
			this.tab_height=itemGridArea[1]+itemGridArea[3];
			this.rowLayNum=this.size;//row_t_map.size();t
			resetGridListInfo(this.rowInfoList);//设置坐标fromx fromy 
			this.rowHeightList=getExcelGridSizeList(1);
		    this.columnWidthList=getExcelGridSizeList(2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public String executXReportExcel(String isGroupPoint,String groupPoint,String dbpre,String history,String year,String month,String count,String printGrid,String modelFlag,String zeroPrint)
	{
		zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"0";
		this.zeroPrint=zeroPrint;
		this.isGroupPoint=isGroupPoint;
		this.groupPoint=groupPoint;
		this.dbpre=dbpre;
		this.history=history;
		this.year=year;
		this.month=month;
		this.count=count;
		this.printGrid=printGrid;
		this.modelFlag=modelFlag;
		if("stipend".equals(modelFlag)|| "salary".equals(modelFlag)) {
            this.tableName=userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+this.tabid;
        } else {
            this.tableName=this.userView.getUserName().trim().replaceAll(" ", "")+"_Muster_"+this.tabid;
        }
		if(this.tableName.indexOf("（")!=-1||this.tableName.indexOf("）")!=-1){
			this.tableName = "\""+this.tableName+"\"";
		}
		this.setGroupV2(this.isHaveGroup2(tableName));
		/* 权限控制 */
		if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)) {
            this.privConditionStr=getPrivCondition(infor_Flag,dbpre);
        }
		
		String fileName=this.userView.getUserName()+"_"+PubFunc.hireKeyWord_filter(this.tablename)+ ".xlsx";//this.tablename+"_"+this.userView.getUserName()+".xlsx";
		
		FileOutputStream fileOut=null;
		try
		{
			String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
			File tempfile=new File(url);
			if(tempfile.exists()) {
				tempfile.delete();
			}
			this.xWb = new XSSFWorkbook(XSSFWorkbookType.XLSX);
			this.xsheet = xWb.createSheet();
			this.xpatriarch=xsheet.createDrawingPatriarch();
//			this.patriarch = sheet.createDrawingPatriarch();
			//liuy 2014-12-20 start 
			setXPageParameter(this.tabid);
			//liuy end
			this.xstyle = getXStyle("c",xWb);
			this.xstyle_noZro=getXStyle("c",xWb);
			DataFormat df0=this.xWb.createDataFormat();
			this.xstyle_noZro.setDataFormat(df0.getFormat("0"));
			this.xstyle_l = getXStyle("l",xWb);
			this.xstyle_r = getXStyle("r",xWb);
			this.xstyle_cc = getXStyle("cc",xWb);
			this.xstyle_ccc = getXStyle("ccc",xWb);
			this.resultList=getResultList();
			goupVcodeName();//设置分组指标值
			if(this.opt==0)
			{
				executeXTopParam();//changxy 20160922 【23023】 无法生成信息项头是由于excel写入内容顺序有问题 应该先生成标题再生成信息项头然后内容 自上而下
				executeXTabHeader();
				executeXTabDataArea();
				executeXBottomParam(this.topParamLayNum+this.rowLayNum+this.resultList.size()+this.groupRowNum);
				
				resetXSize(this.topParamLayNum,0);
			}
			else if(this.opt==1)
			{
				executeXTopParam();// changxy 20161018 生成excel内容先写头 自上而下依次生成
				if("1".equals(dataarea)){//不分栏，多行数据区的，分栏的没有表头，
					executeXTabHeader();
				}
				excuteXhorizontal(1);
				//liuy 2016-4-18 18037：关于花名册导出excel问题 begin  //多层表头 只有一行数据时会遮挡数据行。 changxy
                executeXBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+(this.resultList.size()==1?this.rowLayNum:this.resultList.size())+this.groupRowNum);
                /*if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0)
                    executeBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+4+this.groupRowNum);
                else{
                     标识：3053 薪资发放-用户定义表，（1）界面中实线变为虚线了，不对。  （2）输出excel，标题没有居中，页脚下面空了很多行 xiaoyun 2014-7-10 start  
                    //executeBottomParam(this.bottomParamLayNum+this.rowLayNum*this.resultList.size()+this.resultList.size()+2+this.groupRowNum);
                    executeBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+this.resultList.size()+this.groupRowNum);
                     标识：3053 薪资发放-用户定义表，（1）界面中实线变为虚线了，不对。  （2）输出excel，标题没有居中，页脚下面空了很多行 xiaoyun 2014-7-10 end 
                }*/
                //liuy 2016-4-18 end
			}
			else if(this.opt==2)
			{
				executeXTopParam();// changxy 20161018 生成excel内容先写头 自上而下依次生成
				excuteXhorizontal(2);
				executeXBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+this.groupRowNum);
				
			}
			
			if(topPageList.size()>0){				//标题行 取标题层 区分标题行的行高  changxy 
				for (int i = 0; i < topPageList.size(); i++) {
					RecordVo vo=(RecordVo)topPageList.get(i);
					String num=(String)this.topLayMap.get(vo.getInt("rtop")+"");
					num=(num==null|| "".equals(num))?"0":num;
					int laynum=Integer.parseInt(num)-1;
					Row row = xsheet.getRow(laynum);
					if(row==null) {
                        row =xsheet.createRow(laynum);
                    }
					row.setHeight(Short.parseShort(vo.getInt("rheight")*20+""));	
				}
				
			}
			
			fileOut = new FileOutputStream(url);
			this.xWb.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(fileOut!=null){
				try {
					fileOut.flush();
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			this.xWb=null;
			this.xsheet=null;
		}
		return fileName;
	}
	
	public String executReportExcel(String isGroupPoint,String groupPoint,String dbpre,String history,String year,String month,String count,String printGrid,String modelFlag,String zeroPrint)
	{
		String fileName;
		if(excelType==1){//导出xlsx格式
			fileName=executXReportExcel(isGroupPoint,groupPoint,dbpre,history,year,month,count,printGrid,modelFlag,zeroPrint);
			return fileName;
		}
		zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"0";
		this.zeroPrint=zeroPrint;
		this.isGroupPoint=isGroupPoint;
		this.groupPoint=groupPoint;
		this.dbpre=dbpre;
		this.history=history;
		this.year=year;
		this.month=month;
		this.count=count;
		this.printGrid=printGrid;
		this.modelFlag=modelFlag;
		if("stipend".equals(modelFlag)|| "salary".equals(modelFlag)) {
            this.tableName=userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+this.tabid;
        } else {
            this.tableName=this.userView.getUserName().trim().replaceAll(" ", "")+"_Muster_"+this.tabid;
        }
		if(this.tableName.indexOf("（")!=-1||this.tableName.indexOf("）")!=-1){
			this.tableName = "\""+this.tableName+"\"";
		}
		this.setGroupV2(this.isHaveGroup2(tableName));
		/* 权限控制 */
		if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)) {
            this.privConditionStr=getPrivCondition(infor_Flag,dbpre);
        }
		
		fileName=this.userView.getUserName()+"_"+PubFunc.hireKeyWord_filter(this.tablename)+ ".xls";//this.tablename+"_"+this.userView.getUserName()+".xls";
		
		FileOutputStream fileOut=null;
		try
		{
			
			String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
			
			File tempfile=new File(url);
			if(tempfile.exists()) {
				tempfile.delete();
			}
			
			this.wb = new HSSFWorkbook();
			this.sheet = wb.createSheet();
			this.patriarch = sheet.createDrawingPatriarch();
			//liuy 2014-12-20 start 
			setPageParameter(this.tabid);
			//liuy end
			this.style = getStyle("c",wb);
			this.style_noZro=getStyle("c",wb);
			this.style_noZro.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
			this.style_l = getStyle("l",wb);
			this.style_r = getStyle("r",wb);
			this.style_cc = getStyle("cc",wb);
			this.style_ccc = getStyle("ccc",wb);
			this.resultList=getResultList();
			goupVcodeName();//设置分组指标值
			if(this.opt==0)
			{
				executeTopParam();//changxy 20160922 【23023】 无法生成信息项头是由于excel写入内容顺序有问题 应该先生成标题再生成信息项头然后内容 自上而下
				executeTabHeader();
				executeTabDataArea();
				executeBottomParam(this.topParamLayNum+this.rowLayNum+this.resultList.size()+this.groupRowNum);
				
				resetSize(this.topParamLayNum,0);
			}
			else if(this.opt==1)
			{
				executeTopParam();// changxy 20161018 生成excel内容先写头 自上而下依次生成
				if("1".equals(dataarea)){//不分栏，多行数据区的，分栏的没有表头，
					executeTabHeader();
				}
				excutehorizontal(1);
				//liuy 2016-4-18 18037：关于花名册导出excel问题 begin  //多层表头 只有一行数据时会遮挡数据行。 changxy
                executeBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+(this.resultList.size()==1?this.rowLayNum:this.resultList.size())+this.groupRowNum);
                /*if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0)
                    executeBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+4+this.groupRowNum);
                else{
                     标识：3053 薪资发放-用户定义表，（1）界面中实线变为虚线了，不对。  （2）输出excel，标题没有居中，页脚下面空了很多行 xiaoyun 2014-7-10 start  
                    //executeBottomParam(this.bottomParamLayNum+this.rowLayNum*this.resultList.size()+this.resultList.size()+2+this.groupRowNum);
                    executeBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+this.resultList.size()+this.groupRowNum);
                     标识：3053 薪资发放-用户定义表，（1）界面中实线变为虚线了，不对。  （2）输出excel，标题没有居中，页脚下面空了很多行 xiaoyun 2014-7-10 end 
                }*/
                //liuy 2016-4-18 end
			}
			else if(this.opt==2)
			{
				executeTopParam();// changxy 20161018 生成excel内容先写头 自上而下依次生成
				excutehorizontal(2);
				executeBottomParam(this.topParamLayNum+this.rowLayNum*this.resultList.size()+this.groupRowNum);
				
			}
			
			if(topPageList.size()>0){				//标题行 取标题层 区分标题行的行高  changxy 
				for (int i = 0; i < topPageList.size(); i++) {
					RecordVo vo=(RecordVo)topPageList.get(i);
					String num=(String)this.topLayMap.get(vo.getInt("rtop")+"");
					num=(num==null|| "".equals(num))?"0":num;
					int laynum=Integer.parseInt(num)-1;
					HSSFRow row = sheet.getRow(laynum);
					if(row==null) {
                        row =sheet.createRow(laynum);
                    }
					row.setHeight(Short.parseShort(vo.getInt("rheight")*20+""));	
				}
				
			}
			
			fileOut = new FileOutputStream(url);
			this.wb.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(fileOut!=null){
				try {
					fileOut.flush();
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.wb=null;
			this.sheet=null;
		}
		return fileName;
	}
	
	/**
	 * 设置高级花名册输出Excel页面布局
	 * @author liuy 
	 * @param tabid
	 */
	private void setXPageParameter(String tabid){
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select TMargin,BMargin,RMargin,LMargin,Paper,paperOri from muster_name where tabid="+tabid;
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				xsheet.setMargin(XSSFSheet.TopMargin, mmToInches(Double.parseDouble(rs.getString("TMargin"))));//页边距（上）    
				xsheet.setMargin(XSSFSheet.BottomMargin, mmToInches(Double.parseDouble(rs.getString("BMargin"))));//页边距（下）    
				xsheet.setMargin(XSSFSheet.LeftMargin, mmToInches(Double.parseDouble(rs.getString("RMargin"))));//页边距（左）    
				xsheet.setMargin(XSSFSheet.RightMargin, mmToInches(Double.parseDouble(rs.getString("LMargin"))));//页边距（右）    
				short paper = 0;
				//打印方向，true：横向，false：纵向(默认)
				XSSFPrintSetup ps=(XSSFPrintSetup)xsheet.getPrintSetup();
//				HSSFPrintSetup ps = sheet.getPrintSetup();
				ps.setLandscape("2".equals(rs.getString("paperOri")));
				if(rs.getString("Paper")!=null){
					paper = getPaperSize(rs.getString("Paper"));
				}
				if(paper != 0){		        	
					ps.setPaperSize(paper);//纸张类型 
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 设置高级花名册输出Excel页面布局
	 * @author liuy 
	 * @param tabid
	 */
	private void setPageParameter(String tabid){
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select TMargin,BMargin,RMargin,LMargin,Paper,paperOri from muster_name where tabid="+tabid;
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				sheet.setMargin(HSSFSheet.TopMargin, mmToInches(Double.parseDouble(rs.getString("TMargin"))));//页边距（上）    
				sheet.setMargin(HSSFSheet.BottomMargin, mmToInches(Double.parseDouble(rs.getString("BMargin"))));//页边距（下）    
				sheet.setMargin(HSSFSheet.LeftMargin, mmToInches(Double.parseDouble(rs.getString("RMargin"))));//页边距（左）    
				sheet.setMargin(HSSFSheet.RightMargin, mmToInches(Double.parseDouble(rs.getString("LMargin"))));//页边距（右）    
				short paper = 0;
				//打印方向，true：横向，false：纵向(默认)
				HSSFPrintSetup ps = sheet.getPrintSetup();
				ps.setLandscape("2".equals(rs.getString("paperOri")));
				if(rs.getString("Paper")!=null){
					paper = getPaperSize(rs.getString("Paper"));
				}
				if(paper != 0){		        	
					ps.setPaperSize(paper);//纸张类型 
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 毫米转英寸
	 * 将设置的毫米单位转换为excel适用的英寸
	 * @author liuy
	 * @param mm
	 * @return
	 */
	private double mmToInches(double mm) {
        return mm * 0.03937;
    }
	
	/**
	 * 设置打印纸张类型
	 * @author liuy
	 * @param paperType
	 * @return
	 */
	private short getPaperSize(String paperType) {
        short paperSize = 0;
        if("2".equals(paperType)){
        	paperSize=A3_PAPERSIZE;
		}else if("1".equals(paperType)){
			paperSize=A4_PAPERSIZE;
		}else if("3".equals(paperType)){
			paperSize=A5_PAPERSIZE;
		}else if("4".equals(paperType)){
			paperSize=B5_PAPERSIZE;
		}
        return paperSize;
    }
	
	/** 产生表尾标题 */
	public void executeBottomParam(int assist_x)
	{
		HSSFCellStyle a_style=null;
		if(this.bottomPageList!=null){
			for(int i=0;i<this.bottomPageList.size();i++)
			{
				RecordVo vo=(RecordVo)this.bottomPageList.get(i);
				String fontname=vo.getString("fontname");
				String fontsize=String.valueOf(vo.getInt("fontsize"));
				int fonteffect=vo.getInt("fonteffect");
				HSSFFont font=getFont(fontname,fontsize,fonteffect);
				if(i+1<this.bottomPageList.size())
				{
					RecordVo vo_2=(RecordVo)this.bottomPageList.get(i+1);
					
					int from_x=1;
					if(this.botLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                        from_x = Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo.getInt("rtop"))))-1;
                    }
					short from_y=getColumn_y(vo.getInt("rleft"));
					int from2_x=1;
					if(this.botLayMap.get(String.valueOf(vo_2.getInt("rtop")))!=null) {
                        from2_x=Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo_2.getInt("rtop"))))-1;
                    }
					short from2_y=getColumn_y(vo_2.getInt("rleft"));
	                if(vo.getInt("flag") == 13) {
	                    from2_y = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));
	                    //from2_y = from2_y - 2;
	                    int height = 0; 
	                    if(vo.getInt("rtop") > 0) {
	                        height = vo.getInt("rtop") + vo.getInt("rheight");
	                    } else {
	                        height = vo.getInt("rheight") - vo.getInt("rtop");
	                    }
	                    from2_x = from_x;
	                    if(this.botLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
	                        executeCellImage(from_x+assist_x, from_y, from2_x+assist_x+ Math.round((vo.getInt("rheight")/0.067f)/this.sheet.getDefaultRowHeight()), (short)(from2_y), vo, patriarch);
	                    }
	                    continue;
	                }           

					if(from_x==from2_x&&from_y==from2_y)
					{
						String value1=getTitleValue(vo);
						String value2=getTitleValue(vo_2);
						String value=(value1+value2).replaceAll("&nbsp;"," ");
						if(from_y==0)
						{
							executeCell(from_x+assist_x,from_y,from_x+assist_x,(short)1,value,"no_l",font);
						}
						else
						{
							executeCell(from_x+assist_x,from_y,from_x+assist_x,Short.parseShort(String.valueOf(from_y+2)),value,"no_l",font);		
						}
						i++;
					}
					else
					{
						String value1=getTitleValue(vo).replaceAll("&nbsp;"," ");					
						if(from_x==from2_x)
						{
							executeCell(from_x+assist_x,from_y,from_x+assist_x,Short.parseShort(String.valueOf(from_y+(from2_y-from_y-1))),value1,"no_l",font);
						}
						else
						{
							executeCell(from_x+assist_x,from_y,from_x+assist_x,Short.parseShort(String.valueOf(this.rowInfoBGrid.size()-1)),value1,"no_l",font);
						}
					}
				}
				else
				{
					int from_x=Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo.getInt("rtop"))))-1;
					short from_y=getColumn_y(vo.getInt("rleft"));
					short end_y=Short.parseShort(String.valueOf(this.rowInfoBGrid.size()-1));
                    if(vo.getInt("flag") == 13) {
                        int from2_y = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));
                        //from2_y = from2_y - 2;
                        int height = 0; 
                        if(vo.getInt("rtop") > 0) {
                            height = vo.getInt("rtop") + vo.getInt("rheight");
                        } else {
                            height = vo.getInt("rheight") - vo.getInt("rtop");
                        }
                        int from2_x = from_x;
                        if(this.botLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                            executeCellImage(from_x+assist_x, from_y, from2_x+assist_x+ Math.round((vo.getInt("rheight")/0.067f)/this.sheet.getDefaultRowHeight()), (short)(from2_y), vo, patriarch);
                        }
                        continue;
                    }
                    else {
                        String value1=getTitleValue(vo).replaceAll("&nbsp;"," ");
                        executeCell(from_x+assist_x,from_y,from_x+assist_x,end_y,value1,"no_l",font);					
                    }
				}
			}
		}
	}
	public void executeXBottomParam(int assist_x)
	{
		XSSFCellStyle a_style=null;
		if(this.bottomPageList!=null){
			for(int i=0;i<this.bottomPageList.size();i++)
			{
				RecordVo vo=(RecordVo)this.bottomPageList.get(i);
				String fontname=vo.getString("fontname");
				String fontsize=String.valueOf(vo.getInt("fontsize"));
				int fonteffect=vo.getInt("fonteffect");
				XSSFFont font=getXFont(fontname,fontsize,fonteffect);
				if(i+1<this.bottomPageList.size())
				{
					RecordVo vo_2=(RecordVo)this.bottomPageList.get(i+1);
					
					int from_x=1;
					if(this.botLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                        from_x = Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo.getInt("rtop"))))-1;
                    }
					short from_y=getColumn_y(vo.getInt("rleft"));
					int from2_x=1;
					if(this.botLayMap.get(String.valueOf(vo_2.getInt("rtop")))!=null) {
                        from2_x=Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo_2.getInt("rtop"))))-1;
                    }
					short from2_y=getColumn_y(vo_2.getInt("rleft"));
	                if(vo.getInt("flag") == 13) {
	                    from2_y = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));
	                    //from2_y = from2_y - 2;
	                    int height = 0; 
	                    if(vo.getInt("rtop") > 0) {
	                        height = vo.getInt("rtop") + vo.getInt("rheight");
	                    } else {
	                        height = vo.getInt("rheight") - vo.getInt("rtop");
	                    }
	                    from2_x = from_x;
	                    if(this.botLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
	                        executeXCellImage(from_x+assist_x, from_y, from2_x+assist_x+ Math.round((vo.getInt("rheight")/0.067f)/this.xsheet.getDefaultRowHeight()), (short)(from2_y), vo, xpatriarch);
	                    }
	                    continue;
	                }           

					if(from_x==from2_x&&from_y==from2_y)
					{
						String value1=getTitleValue(vo);
						String value2=getTitleValue(vo_2);
						String value=(value1+value2).replaceAll("&nbsp;"," ");
						if(from_y==0)
						{
							executeXCell(from_x+assist_x,from_y,from_x+assist_x,(short)1,value,"no_l",font);
						}
						else
						{
							executeXCell(from_x+assist_x,from_y,from_x+assist_x,Short.parseShort(String.valueOf(from_y+2)),value,"no_l",font);		
						}
						i++;
					}
					else
					{
						String value1=getTitleValue(vo).replaceAll("&nbsp;"," ");					
						if(from_x==from2_x)
						{
							executeXCell(from_x+assist_x,from_y,from_x+assist_x,Short.parseShort(String.valueOf(from_y+(from2_y-from_y-1))),value1,"no_l",font);
						}
						else
						{
							executeXCell(from_x+assist_x,from_y,from_x+assist_x,Short.parseShort(String.valueOf(this.rowInfoBGrid.size()-1)),value1,"no_l",font);
						}
					}
				}
				else
				{
					int from_x=Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo.getInt("rtop"))))-1;
					short from_y=getColumn_y(vo.getInt("rleft"));
					short end_y=Short.parseShort(String.valueOf(this.rowInfoBGrid.size()-1));
                    if(vo.getInt("flag") == 13) {
                        int from2_y = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));
                        //from2_y = from2_y - 2;
                        int height = 0; 
                        if(vo.getInt("rtop") > 0) {
                            height = vo.getInt("rtop") + vo.getInt("rheight");
                        } else {
                            height = vo.getInt("rheight") - vo.getInt("rtop");
                        }
                        int from2_x = from_x;
                        if(this.botLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                            executeXCellImage(from_x+assist_x, from_y, from2_x+assist_x+ Math.round((vo.getInt("rheight")/0.067f)/this.xsheet.getDefaultRowHeight()), (short)(from2_y), vo, xpatriarch);
                        }
                        continue;
                    }
                    else {
                        String value1=getTitleValue(vo).replaceAll("&nbsp;"," ");
                        executeXCell(from_x+assist_x,from_y,from_x+assist_x,end_y,value1,"no_l",font);					
                    }
				}
			}
		}
	}
	
	/** 产生表头标题 */
	public void executeXTopParam()
	{
		try {

			XSSFCellStyle a_style=null;
			//liuy 2014-11-26 修改高级花名册导出表头标题方法  start
			ArrayList withTheTitleList =new ArrayList();
			String withTheTitle="";//合并标题
			for(int i=0;i<this.topPageList.size();i++){
				RecordVo vo = (RecordVo) this.topPageList.get(i);
				String fontname = vo.getString("fontname");
				String fontsize = String.valueOf(vo.getInt("fontsize"));
				int fonteffect = vo.getInt("fonteffect");
				XSSFFont font = getXFont(fontname,fontsize,fonteffect);
				//表头行高  changxy 20160811 start
				Row row=xsheet.createRow(i);
				row.setHeight((short)800);
				//表头行高  changxy 20160811 end
				if(i+1<this.topPageList.size()){
					RecordVo vo_2 = (RecordVo) this.topPageList.get(i + 1);
					int from_x = 1;//当前标题开始的行
					if(this.topLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                        from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
                    }
					short from_y = getColumn_y(vo.getInt("rleft"));//当前标题开始的列
					short from_y_end = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));//当前标题结束的列
					int from2_x = 1;//下一个标题开始的行
					if(this.topLayMap.get(String.valueOf(vo_2.getInt("rtop")))!=null) {
                        from2_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo_2.getInt("rtop")))) - 1;
                    }
					short from2_y = getColumn_y(vo_2.getInt("rleft"));//下一个标题开始的列
					if(vo.getInt("flag") == 13) {//flag=13:标题是logo图片
						int height = 0; 
						if(vo.getInt("rtop") > 0) {
							height = vo.getInt("rtop") + vo.getInt("rheight");
						} else {
							height = vo.getInt("rheight") - vo.getInt("rtop");
						}
						from2_x = getColumn_x(height);
						if(this.topLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
							executeXCellImage(from_x, from_y, from2_x, from_y_end, vo, xpatriarch);
						}
						continue;
					}
					//当前标题开始的行等于下一个标题开始的行&&当前标题结束的列等于下一标题开始的列
					if (from_x == from2_x && from_y_end == from2_y) {//合并标题
						/*
						if("".equals(withTheTitle)){						
							withTheTitle = withTheTitle+i;
						}else{
							withTheTitle = withTheTitle+","+i;						
						}
						*/
						if("".equals(withTheTitle)){						
							withTheTitle = i+","+(i+1);
						}else{
							withTheTitle = withTheTitle+","+(i+1);						
						}
						if(i+2==this.topPageList.size()){
							if(!"".equals(withTheTitle)){
								if(withTheTitle.split(",").length!=this.topPageList.size())//判断标题数组的长度， 相等标题数组不增加  24257 changxy 20161115
                                {
                                    withTheTitle = withTheTitle+","+(i+1);
                                }
								withTheTitleList.add(withTheTitle);
								withTheTitle="";//清空合并标题数据
							}
							i++;
						}
					} else {//非合并标题
						if(!"".equals(withTheTitle)){						
							withTheTitleList.add(withTheTitle);
							withTheTitle="";//清空合并标题数据
						}else {						
							String value = "";
							if((vo.getInt("flag")==8||vo.getInt("flag")==9)&&topDateTitleMap.get(i+"")!=null) {
                                value = (String)topDateTitleMap.get(i+"");
                            } else {
                                value = getTitleValue(vo).replaceAll("&nbsp;"," ");
                            }
							executeXCell(from_x, from_y, from_x, from_y_end,value, "no_l",font, vo);
						}
					}
				}else{//如果顶部最后一个标题未被合并，单独处理顶部最后一个标题
					int from_x = 1;//当前标题开始的行
					if(this.topLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                        from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
                    }
					short from_y = getColumn_y(vo.getInt("rleft"));//当前标题开始的列
					short from_y_end = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));//当前标题结束的列
					String value = "";
					if((vo.getInt("flag")==8||vo.getInt("flag")==9)&&topDateTitleMap.get(i+"")!=null) {
                        value = (String)topDateTitleMap.get(i+"");
                    } else {
                        value = getTitleValue(vo).replaceAll("&nbsp;"," ");
                    }
					executeXCell(from_x, from_y, from_x, from_y_end,value, "no_l",font, vo);
				}
			}
			for(int i=0;i<withTheTitleList.size();i++){
				withTheTitle = (String)withTheTitleList.get(i);
				String[] titles = withTheTitle.split(",");
				String value = getWithTheTitleValue(titles);//得到合并标题的内容
				value = value.replaceAll("&nbsp;", " ");
				RecordVo voStart = (RecordVo) this.topPageList.get(Integer.parseInt(titles[0]));//得到合并标题中的第一个标题
				RecordVo voEnd = (RecordVo) this.topPageList.get(Integer.parseInt(titles[titles.length-1]));//得到合并标题中的最后一个标题

				String fontname = voStart.getString("fontname");
				String fontsize = String.valueOf(voStart.getInt("fontsize"));
				int fonteffect = voStart.getInt("fonteffect");
				XSSFFont font = getXFont(fontname,fontsize,fonteffect);
				
				int withTheTitle_x = 1;//合并标题所在的行（合并标题只能在一行）
				if(this.topLayMap.get(String.valueOf(voStart.getInt("rtop")))!=null) {
                    withTheTitle_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(voStart.getInt("rtop")))) - 1;
                }
				short withTheTitle_y = getColumn_y(voStart.getInt("rleft"));//合并标题开始的列
				short withTheTitle2_y = getColumn_y(voEnd.getInt("rleft")+voEnd.getInt("rwidth"));//合并标题结束的列
				executeXCell(withTheTitle_x, withTheTitle_y, withTheTitle_x, withTheTitle2_y,value, "no_l",font, voStart);//输出到excel中
			}
			//liuy end
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 产生表头标题 */
	public void executeTopParam()
	{
		HSSFCellStyle a_style=null;
		//liuy 2014-11-26 修改高级花名册导出表头标题方法  start
		ArrayList withTheTitleList =new ArrayList();
		String withTheTitle="";//合并标题
		for(int i=0;i<this.topPageList.size();i++){
			RecordVo vo = (RecordVo) this.topPageList.get(i);
			String fontname = vo.getString("fontname");
			String fontsize = String.valueOf(vo.getInt("fontsize"));
			int fonteffect = vo.getInt("fonteffect");
			HSSFFont font = getFont(fontname,fontsize,fonteffect);
			//表头行高  changxy 20160811 start
			HSSFRow row=sheet.createRow(i);
			row.setHeight((short)800);
			//表头行高  changxy 20160811 end
			if(i+1<this.topPageList.size()){
				RecordVo vo_2 = (RecordVo) this.topPageList.get(i + 1);
				int from_x = 1;//当前标题开始的行
				if(this.topLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                    from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
                }
				short from_y = getColumn_y(vo.getInt("rleft"));//当前标题开始的列
				short from_y_end = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));//当前标题结束的列
				int from2_x = 1;//下一个标题开始的行
				if(this.topLayMap.get(String.valueOf(vo_2.getInt("rtop")))!=null) {
                    from2_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo_2.getInt("rtop")))) - 1;
                }
				short from2_y = getColumn_y(vo_2.getInt("rleft"));//下一个标题开始的列
				if(vo.getInt("flag") == 13) {//flag=13:标题是logo图片
					int height = 0; 
					if(vo.getInt("rtop") > 0) {
						height = vo.getInt("rtop") + vo.getInt("rheight");
					} else {
						height = vo.getInt("rheight") - vo.getInt("rtop");
					}
					from2_x = getColumn_x(height);
					if(this.topLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
						executeCellImage(from_x, from_y, from2_x, from_y_end, vo, patriarch);
					}
					continue;
				}
				//当前标题开始的行等于下一个标题开始的行&&当前标题结束的列等于下一标题开始的列
				if (from_x == from2_x && from_y_end == from2_y) {//合并标题
					/*
					if("".equals(withTheTitle)){						
						withTheTitle = withTheTitle+i;
					}else{
						withTheTitle = withTheTitle+","+i;						
					}
					*/
					if("".equals(withTheTitle)){						
						withTheTitle = i+","+(i+1);
					}else{
						withTheTitle = withTheTitle+","+(i+1);						
					}
					if(i+2==this.topPageList.size()){
						if(!"".equals(withTheTitle)){
							if(withTheTitle.split(",").length!=this.topPageList.size()) {//判断标题数组的长度， 相等标题数组不增加  24257 changxy 20161115
								if(withTheTitle.indexOf(","+(i+1))<0){//去除相同标题下标 防止导出标题重复
									withTheTitle = withTheTitle+","+(i+1);
								}
							}
							withTheTitleList.add(withTheTitle);
							withTheTitle="";//清空合并标题数据
						}
						i++;
					}
				} else {//非合并标题
					if(!"".equals(withTheTitle)){						
						withTheTitleList.add(withTheTitle);
						withTheTitle="";//清空合并标题数据
					}else {						
						String value = "";
						if((vo.getInt("flag")==8||vo.getInt("flag")==9)&&topDateTitleMap.get(i+"")!=null) {
                            value = (String)topDateTitleMap.get(i+"");
                        } else {
                            value = getTitleValue(vo).replaceAll("&nbsp;"," ");
                        }
						executeCell(from_x, from_y, from_x, from_y_end,value, "no_l",font, vo);
					}
				}
			}else{//如果顶部最后一个标题未被合并，单独处理顶部最后一个标题
				int from_x = 1;//当前标题开始的行
				if(this.topLayMap.get(String.valueOf(vo.getInt("rtop")))!=null) {
                    from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
                }
				short from_y = getColumn_y(vo.getInt("rleft"));//当前标题开始的列
				short from_y_end = getColumn_y(vo.getInt("rleft")+vo.getInt("rwidth"));//当前标题结束的列
				String value = "";
				if((vo.getInt("flag")==8||vo.getInt("flag")==9)&&topDateTitleMap.get(i+"")!=null) {
                    value = (String)topDateTitleMap.get(i+"");
                } else {
                    value = getTitleValue(vo).replaceAll("&nbsp;"," ");
                }
				executeCell(from_x, from_y, from_x, from_y_end,value, "no_l",font, vo);
			}
		}
		for(int i=0;i<withTheTitleList.size();i++){
			withTheTitle = (String)withTheTitleList.get(i);
			String[] titles = withTheTitle.split(",");
			String value = getWithTheTitleValue(titles);//得到合并标题的内容
			value = value.replaceAll("&nbsp;", " ");
			RecordVo voStart = (RecordVo) this.topPageList.get(Integer.parseInt(titles[0]));//得到合并标题中的第一个标题
			RecordVo voEnd = (RecordVo) this.topPageList.get(Integer.parseInt(titles[titles.length-1]));//得到合并标题中的最后一个标题

			String fontname = voStart.getString("fontname");
			String fontsize = String.valueOf(voStart.getInt("fontsize"));
			int fonteffect = voStart.getInt("fonteffect");
			HSSFFont font = getFont(fontname,fontsize,fonteffect);
			
			int withTheTitle_x = 1;//合并标题所在的行（合并标题只能在一行）
			if(this.topLayMap.get(String.valueOf(voStart.getInt("rtop")))!=null) {
                withTheTitle_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(voStart.getInt("rtop")))) - 1;
            }
			short withTheTitle_y = getColumn_y(voStart.getInt("rleft"));//合并标题开始的列
			short withTheTitle2_y = getColumn_y(voEnd.getInt("rleft")+voEnd.getInt("rwidth"));//合并标题结束的列
			
			executeCell(withTheTitle_x, withTheTitle_y, withTheTitle_x, withTheTitle2_y,value, "no_l",font, voStart);//输出到excel中
		}
		//liuy end
	}
	public String getWithTheTitleValue(String[] titles){
		String value="";
		for(int i = 0;i<titles.length;i++){
			RecordVo vo = (RecordVo) this.topPageList.get(Integer.parseInt(titles[i]));
			value += getTitleValue(vo);
		}
		return value;
	}
	/**
	 * 取得标题在excel中的横坐标
	 * @param height
	 * @return
	 * @author xiaoyun 2014-6-16 标识：1518 潍坊银行插入logo的问题 
	 */
	public int getColumn_x(int height) {
		int x = 0;
		LazyDynaBean abean=null;
		double rtop = 0;
		double rheight = 0;
		for(int i=0;i<this.rowInfoList.size();i++)
		{
			////表项目区域 l,t,w,h
			abean = (LazyDynaBean)this.rowInfoList.get(i);
			double a_rtop = Double.parseDouble((String)abean.get("rtop"));
			double a_rheight = Double.parseDouble((String)abean.get("rheight"));
			if(height >= rtop && height <= (a_rtop+a_rheight)){
				x = Short.parseShort((String)abean.get("from_x"));
				x = x - 2;
				break;
			}
		}
		return x;
	}
	/**
	 * 标识：1518 潍坊银行插入logo的问题 
	 * @author xiaoyun 2014-5-30
	 */
	public void executeCell(int a,short b,int c,short d,String content,String style,HSSFFont font, RecordVo vo) {
		 //HSSFRow row = sheet.createRow(a);
		 HSSFRow row = sheet.getRow(a);
			if(row==null) {
                row = sheet.createRow(a);
            }
		 //float height = (vo.getInt("rwidth")/0.67f)*1.5f;
		 /* 标识：3053 薪资发放-用户定义表 导出excel问题 xiaoyun 2014-7-16 start */
		 /*
		 if(height > 0f) {
			 String r_H=String.valueOf(height);
			 if(r_H.indexOf(".")!=-1)
				 r_H=r_H.substring(0,r_H.indexOf("."));
			 row.setHeight((short)(Short.parseShort(r_H)));
		 }*/
		 /* 标识：3053 薪资发放-用户定义表 导出excel问题 xiaoyun 2014-7-16 end */
		 HSSFCell cell = row.createCell(b);
		// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		 
		 
		 if("c".equalsIgnoreCase(style)){
			 this.style.setFont(font);
			 this.style.setWrapText(true);
			 cell.setCellStyle(this.style); 
		 }else if("l".equalsIgnoreCase(style)){
			 this.style_l.setFont(font);
			 this.style_l.setWrapText(true);
			 cell.setCellStyle(this.style_l);
		 }else if("ccc".equalsIgnoreCase(style)){
			 this.style_ccc.setFont(font);
			 this.style_ccc.setWrapText(true);
			 cell.setCellStyle(this.style_ccc);
		 }else if("R".equalsIgnoreCase(style)){
			 this.style_r.setFont(font);
			 this.style_r.setWrapText(true);
			 cell.setCellStyle(this.style_r);
		 }else if("cc".equalsIgnoreCase(style)){
			 this.style_cc.setFont(font);
			 this.style_cc.setWrapText(true);
			 cell.setCellStyle(this.style_cc);
		 }else if("no_c".equalsIgnoreCase(style)){
			 HSSFCellStyle a_style=wb.createCellStyle();
			 a_style.setAlignment(HorizontalAlignment.CENTER);
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }else if("no_l".equalsIgnoreCase(style)){
			 HSSFCellStyle a_style=wb.createCellStyle();
			 /* 标识：3053 薪资发放-用户定义表，输出excel，标题没有居中 xiaoyun 2014-7-10 start */
 			 //a_style.setAlignment(HorizontalAlignment.LEFT);
			 a_style.setAlignment(HorizontalAlignment.CENTER);
			 /* 标识：3053 薪资发放-用户定义表，输出excel，标题没有居中 xiaoyun 2014-7-10 end */
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }
		 if(content.endsWith("`"))
		 {
			 content=content.substring(0,content.length()-1);
		 }
		 content=content.replaceAll("`","\r\n");
		 
		 cell.setCellValue(content);
		 short b1=b;
		 while(++b1<=d)
		 {
			 cell = row.createCell(b1);
			 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                 cell.setCellStyle(this.style);
             }
		 }
		 for(int a1=a+1;a1<=c;a1++)
		 {
			 //row = sheet.createRow(a1);
			 row = sheet.getRow(a1);
				if(row==null) {
                    row = sheet.createRow(a1);
                }

			 b1=b;
			 while(b1<=d)
			 {
				 cell = row.createCell(b1);
				 if(!"".equals(style)) {
                     cell.setCellStyle(this.style);
                 }
				 b1++;
			 }
		 }
		 /* 任务：1518 潍坊银行 高级花名册-导出excel，如果to_y<from_y，那默认合并三个单元格(这样写也不严禁，短时间内不一定能找不到好的方法，所以先这样吧) xiaoyun 2014-8-22 start */
		 //if(c>=a&&d>=b) {
			 try {
				ExportExcelUtil.mergeCell(sheet, a,b,c,d);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
		 //} else {
			 //ExportExcelUtil.mergeCell(sheet, a,a,b,b+3);
		 //}		 
		 /* 任务：1518 潍坊银行 高级花名册-导出excel，如果to_y<from_y，那默认合并三个单元格(这样写也不严禁，短时间内不一定能找不到好的方法，所以先这样吧) xiaoyun 2014-8-22 end */
		 
	 }
	
	public void executeXCell(int a,short b,int c,short d,String content,String style,XSSFFont font, RecordVo vo) {
		 //HSSFRow row = sheet.createRow(a);
		 Row row = xsheet.getRow(a);
			if(row==null) {
                row = xsheet.createRow(a);
            }
		 //float height = (vo.getInt("rwidth")/0.67f)*1.5f;
		 /* 标识：3053 薪资发放-用户定义表 导出excel问题 xiaoyun 2014-7-16 start */
		 /*
		 if(height > 0f) {
			 String r_H=String.valueOf(height);
			 if(r_H.indexOf(".")!=-1)
				 r_H=r_H.substring(0,r_H.indexOf("."));
			 row.setHeight((short)(Short.parseShort(r_H)));
		 }*/
		 /* 标识：3053 薪资发放-用户定义表 导出excel问题 xiaoyun 2014-7-16 end */
		 Cell cell = row.createCell(b);
		// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		 
		 
		 if("c".equalsIgnoreCase(style)){
			 this.xstyle.setFont(font);
			 this.xstyle.setWrapText(true);
			 cell.setCellStyle(this.xstyle); 
		 }else if("l".equalsIgnoreCase(style)){
			 this.xstyle_l.setFont(font);
			 this.xstyle_l.setWrapText(true);
			 cell.setCellStyle(this.xstyle_l);
		 }else if("ccc".equalsIgnoreCase(style)){
			 this.xstyle_ccc.setFont(font);
			 this.xstyle_ccc.setWrapText(true);
			 cell.setCellStyle(this.xstyle_ccc);
		 }else if("R".equalsIgnoreCase(style)){
			 this.xstyle_r.setFont(font);
			 this.xstyle_r.setWrapText(true);
			 cell.setCellStyle(this.xstyle_r);
		 }else if("cc".equalsIgnoreCase(style)){
			 this.xstyle_cc.setFont(font);
			 this.xstyle_cc.setWrapText(true);
			 cell.setCellStyle(this.xstyle_cc);
		 }else if("no_c".equalsIgnoreCase(style)){
			 XSSFCellStyle a_style=(XSSFCellStyle)this.xWb.createCellStyle();
			 a_style.setAlignment(HorizontalAlignment.CENTER);
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }else if("no_l".equalsIgnoreCase(style)){
			 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
			 /* 标识：3053 薪资发放-用户定义表，输出excel，标题没有居中 xiaoyun 2014-7-10 start */
			 //a_style.setAlignment(HorizontalAlignment.LEFT);
			 a_style.setAlignment(HorizontalAlignment.CENTER);
			 /* 标识：3053 薪资发放-用户定义表，输出excel，标题没有居中 xiaoyun 2014-7-10 end */
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }
		 if(content.endsWith("`"))
		 {
			 content=content.substring(0,content.length()-1);
		 }
		 content=content.replaceAll("`","\r\n");
		 
		 cell.setCellValue(content);
		 short b1=b;
		 while(++b1<=d)
		 {
			// cell = row.createCell(b1);
			 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                 cell.setCellStyle(this.xstyle);
             }
		 }
		 for(int a1=a+1;a1<=c;a1++)
		 {
			 //row = sheet.createRow(a1);
			 row = xsheet.getRow(a1);
				if(row==null) {
                    row = xsheet.createRow(a1);
                }

			 b1=b;
			 while(b1<=d)
			 {
				 //cell = row.createCell(b1);
				 if(!"".equals(style)) {
                     cell.setCellStyle(this.xstyle);
                 }
				 b1++;
			 }
		 }
		 /* 任务：1518 潍坊银行 高级花名册-导出excel，如果to_y<from_y，那默认合并三个单元格(这样写也不严禁，短时间内不一定能找不到好的方法，所以先这样吧) xiaoyun 2014-8-22 start */
		 //if(c>=a&&d>=b) {
		 if(a!=c||d!=b)//xlsx 合并单元格 firstRow与endRow相同时 firstCol lastCol 不能相同
         {
             xsheet.addMergedRegion(new CellRangeAddress(a,c,b,d));
         }
//			 ExportExcelUtil.mergeCell(xsheet, a,b,c,d));
		 //} else {
			 //ExportExcelUtil.mergeCell(sheet, a,a,b,b+3));
		 //}		 
		 /* 任务：1518 潍坊银行 高级花名册-导出excel，如果to_y<from_y，那默认合并三个单元格(这样写也不严禁，短时间内不一定能找不到好的方法，所以先这样吧) xiaoyun 2014-8-22 end */
		 
	 }
	
		
	/** 
     * 导出图片到excel
     * @param a 起始 x坐标
     * @param b 起始 y坐标
     * @param c 终止 x坐标
     * @param d 终止 y坐标
     * @param vo
     * @param rwidth
     * @param rheight 
     * @param patriarch
     * 
     * @author xiaoyun 2014-5-28 标识：1518
     */
    private void executeCellImage(int from_x,short from_y,int from2_x,short from2_y,RecordVo vo,HSSFPatriarch patriarch) {
    	try {
    	    if(from_x == from2_x) {
    	        HSSFRow row = sheet.getRow(from_x);
                if(row==null) {
                    row = sheet.createRow(from_x);
                }
                //float rheight = (float)vo.getInt("rheight");     // 转单位
                float rheight = vo.getInt("rheight")/0.067f;     // 转单位
                row.setHeight((short)rheight);                
    	    }    	    
     	    //HSSFClientAnchor anchor = new HSSFClientAnchor(0,5,200,255,b,Short.parseShort(String.valueOf(a)), d, Short.parseShort(String.valueOf(c)));
    	    //HSSFClientAnchor anchor = new HSSFClientAnchor( 0, 0, 1023, 255, (short)0, 0, (short)3, c );
    	    HSSFClientAnchor anchor = new HSSFClientAnchor( 0, 0, 1023, 255, from_y, from_x, from2_y, from2_x);
     	    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
     	    
    		InputStream in = (InputStream)vo.getObject("content");
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		byte[] buf = new byte[1024];
    		int len = 0;
    		while((len = in.read(buf)) != -1) {
    			baos.write(buf, 0, len);
    		}
    		byte[] byt = baos.toByteArray();
 			HSSFPicture p = patriarch.createPicture(anchor,this.wb.addPicture(byt, HSSFWorkbook.PICTURE_TYPE_JPEG));
        } catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    private void executeXCellImage(int from_x,short from_y,int from2_x,short from2_y,RecordVo vo,Drawing patriarch) {
    	try {
    	    if(from_x == from2_x) {
    	        Row row = xsheet.getRow(from_x);
                if(row==null) {
                    row = xsheet.createRow(from_x);
                }
                //float rheight = (float)vo.getInt("rheight");     // 转单位
                float rheight = vo.getInt("rheight")/0.067f;     // 转单位
                row.setHeight((short)rheight);                
    	    }    	    
     	    //HSSFClientAnchor anchor = new HSSFClientAnchor(0,5,200,255,b,Short.parseShort(String.valueOf(a)), d, Short.parseShort(String.valueOf(c)));
    	    //HSSFClientAnchor anchor = new HSSFClientAnchor( 0, 0, 1023, 255, (short)0, 0, (short)3, c );
    	    
    	    XSSFClientAnchor anchor = new XSSFClientAnchor( 0, 0, 512, 255, from_y, from_x, from2_y+1, from2_x+1);
     	    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
     	    
    		InputStream in = (InputStream)vo.getObject("content");
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		byte[] buf = new byte[1024];
    		int len = 0;
    		while((len = in.read(buf)) != -1) {
    			baos.write(buf, 0, len);
    		}
    		byte[] byt = baos.toByteArray();
 			 patriarch.createPicture(anchor,this.xWb.addPicture(byt, XSSFWorkbook.PICTURE_TYPE_JPEG));
        } catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	
	/**
	 * 取得标题值（包括参数）
	 * @param vo
	 * @return
	 */
	public String getTitleValue(RecordVo vo)
	{
		String context="";
		Date dd=new Date();     //制表时间
		switch(vo.getInt("flag"))
		{
			case 0:
				context = vo.getString("hz"); // 文本描述
				break;
			case 1:
				
				context = this.getCreateTableDate(vo.getString("extendattr"));
				/*GregorianCalendar d = new GregorianCalendar();
				context = ResourceFactory
						.getProperty("hmuster.label.createTableDate")
						+ ":"
						+ d.get(Calendar.YEAR)
						+ "."
						+ (d.get(Calendar.MONTH)+1)
						+ "."
						+ d.get(Calendar.DATE);*/
				break;
			case 2:
				context = ResourceFactory.getProperty("hmuster.label.createTableTime")+ DateFormat.getTimeInstance(DateFormat.MEDIUM,Locale.CHINA).format(dd);
				break;
			case 3:
				
				context = this.getCreateTablePerson(this.userView.getUserFullName(), vo.getString("extendattr"));
				
				/*context = ResourceFactory
						.getProperty("hmuster.label.createTableMen")
						+ ":" + this.userView.getUserFullName(); // 制表人
*/				break;
			case 4: // 总页数
				
				context = ResourceFactory.getProperty("hmuster.label.total")+ 1+ ResourceFactory.getProperty("hmuster.label.paper");
				break;
			case 5:
				context = ResourceFactory.getProperty("hmuster.label.d")+ 1+ ResourceFactory.getProperty("hmuster.label.paper"); // 页码
				break;
			case 12: //-#-
				context = "-"
				+ 1
				+"-"; 
				break;
			case 6:
				context = totalRowNum+"";
				break;
			case 7: // 分组指标
				/*if (!this.isGroupField && isGroupPoint != null&& isGroupPoint.equals("1")&&isGroupNoPage.equals("0")) {
					context="";
				}else*/
					context = this.getGroupVcode();
				break;
			case 8: // ()年()月
				//context=getYearMonth(1);
				context=this.hmusterViewBo.getYearMonth(infor_Flag, history, this.userView.getUserName()+"_muster_"+tabid, year, month, 1, count+"", this.hmusterViewBo.getSetChangeFlag(tabid));
				break;
			case 9: // ()年()月()次
				/*if (history.equals("3")) {
					context = year+ ResourceFactory.getProperty("hmuster.label.year")+ month+ ResourceFactory.getProperty("hmuster.label.month")+ count+ ResourceFactory.getProperty("hmuster.label.count");
				} else {
					context = "&nbsp;";
				}*/
				context=this.hmusterViewBo.getYearMonth(infor_Flag, history, this.userView.getUserName()+"_muster_"+tabid, year, month, 1, count+"", this.hmusterViewBo.getSetChangeFlag(tabid));
				//context=getYearMonth(2);
				break;
			case 10:   //标题变量
				if(vo.getString("extendattr").trim().length()>1)
				{
					context=getTitleVarValue(vo.getString("extendattr").trim());
				}
				else {
                    context="";
                }
				break;
			case 14:   //组内记录
				if (isGroupPoint!= null&& "1".equals(isGroupPoint)&&isGroupNoPage!=null&& "0".equals(isGroupNoPage)) {
					if(vo!=null){
						context="&nbsp;";
					}else {
                        context="&nbsp;";
                    }
				}else{
					context="&nbsp;";
				}
				break;
            case 16:// 审批意见
                if(hmusterViewBo!=null) {
                    context = hmusterViewBo.getGzTaoSpProcess();
                }
                break;
		}
		context=context!=null&&context.trim().length()>0?context:"&nbsp;";
		return context;
	}
	private void goupVcodeName(){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select GroupN,Groupv from ");
		sqlstr.append(this.tableName);
		ContentDAO dao = new ContentDAO(this.conn);
		String groupv = "";
		String groupn = "";
		try {
			RowSet rs = dao.search(sqlstr.toString());
			if(rs.next()){
				groupn = rs.getString(1);
				groupv = rs.getString(2);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setGroupNcode(groupn);
		this.setGroupVcode(groupv);
	}
	
	public String getYearMonth(int type)
	{
		String context="";
        DbWizard dbw = new DbWizard(conn);
		try
		{
			String temptable=this.userView.getUserName()+"_Muster_"+this.tabid;
			Calendar c=Calendar.getInstance();
			if("salary".equals(infor_Flag)|| "stipend".equals(infor_Flag))
			{
				
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select a00z0,a00z1 from "+temptable);
				if(rowSet.next())
				{
					Date d=rowSet.getDate(1);
					if(d!=null)
					{
						c.setTime(d);
						context=c.get(Calendar.YEAR)+ResourceFactory.getProperty("kq.wizard.year")+(c.get(Calendar.MONTH)+1)+ResourceFactory.getProperty("kq.wizard.month");
					    String a00z1=rowSet.getString("a00z1");
					    if(a00z1!=null&&type==2) {
                            context+=a00z1+ResourceFactory.getProperty("hmuster.label.count");
                        }
					}
				}
			}
			else if("81".equals(modelFlag)&&dbw.isExistField(temptable, "Q03Z0", false)) {
                    ContentDAO dao = new ContentDAO(this.conn);
                    RowSet rowSet=dao.search("select Max(Q03Z0) AS Q03Z0 from "+temptable);
                    if(rowSet.next())
                    {
                        String s=rowSet.getString("Q03Z0");
                        if(s != null) {
                            s += "-01";
                        }
                        if(s == null) {
                            s = ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
                        }
                        if(s != null) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date d = df.parse(s);
                            if(d!=null)
                            {
                                c.setTime(d);
                                context=c.get(Calendar.YEAR)+ResourceFactory.getProperty("kq.wizard.year")+(c.get(Calendar.MONTH)+1)+ResourceFactory.getProperty("kq.wizard.month");
                            }
                        }
                    }
			}
			else
			{
                if(type==2)
                {
                	boolean ischangeflag=this.getSetChangeFlag();
                	if(ischangeflag&& "3".equals(history))
                	{
                		context = year
						+ ResourceFactory.getProperty("hmuster.label.year")
						+ month
						+ ResourceFactory.getProperty("hmuster.label.month");
				    	context+=count+ResourceFactory.getProperty("hmuster.label.count");
                	}
                }else{
		    		if ("3".equals(history)) {
		    			context = year
			    				+ ResourceFactory.getProperty("hmuster.label.year")
			    				+ month
			    				+ ResourceFactory.getProperty("hmuster.label.month");
				 
	    			} else if("2".equals(history)) {//取部分历史
	    				if(this.yearmonth!=null&&this.yearmonth.length()>0)
	    				{
	    					context=this.yearmonth;
	    				}else{
				    	
	    				 
				    		String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
				    		String ayear="";
				    		String amonth="";
				    		String aday="";
				    		if(value!=null&&!"".equals(value))
				    		{
				    			ayear=value.substring(0,4);
				    			amonth=value.substring(5,7);
				    			aday=value.substring(8);
				    		}else if(this.userView!=null&&this.userView.getAppdate()!=null&&!"".equals(this.userView.getAppdate())){
				    			ayear=this.userView.getAppdate().substring(0,4);
				    			amonth=this.userView.getAppdate().substring(5,7);
				     			aday=this.userView.getAppdate().substring(8);
				    		}else
				    		{
				    			ayear=c.get(Calendar.YEAR)+"";
				    			amonth=c.get(Calendar.MONTH)+1+"";
				    		}
				    		context = ayear+ResourceFactory.getProperty("kq.wizard.year")+amonth+ResourceFactory.getProperty("kq.wizard.month");
	    				}
	    			}
	    			else{
	    				String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
			    		String ayear="";
			    		String amonth="";
			    		String aday="";
			    		if(value!=null&&!"".equals(value))
			    		{
			    			ayear=value.substring(0,4);
			    			amonth=value.substring(5,7);
			    			aday=value.substring(8);
			    		}else if(this.userView!=null&&this.userView.getAppdate()!=null&&!"".equals(this.userView.getAppdate())){
			    			ayear=this.userView.getAppdate().substring(0,4);
			    			amonth=this.userView.getAppdate().substring(5,7);
			     			aday=this.userView.getAppdate().substring(8);
			    		}else
			    		{
			    			ayear=c.get(Calendar.YEAR)+"";
			    			amonth=c.get(Calendar.MONTH)+1+"";
			    		}
			    		context = ayear+ResourceFactory.getProperty("kq.wizard.year")+amonth+ResourceFactory.getProperty("kq.wizard.month");
	    			}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return context;
	}
	public boolean getSetChangeFlag()
	{
		boolean flag= false;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select setname,Field_Name from Muster_Cell where Tabid="+tabid);
			while(rs.next())
			{
				String setname=rs.getString("setname");
				if(setname==null|| "".equals(setname)) {
                    continue;
                }
				FieldSet vo = DataDictionary.getFieldSetVo(setname.toLowerCase());
				if(vo==null) {
                    continue;
                }
				if(!"0".equals(vo.getChangeflag()))//按年或按月变化的子集
				{
					flag=true;
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
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
		return flag;
	}
	public String extendAttrXML(String ext,String par){
		String value="";
		String starStr = "<"+par+">";
		String endStr = "</"+par+">";
		if(ext.indexOf(starStr)!=-1){
			value = ext.substring(ext.indexOf(starStr)
					+starStr.length(),
					ext.indexOf(endStr));
		}
		return value;
	}
	
	/**
	 * 取得标题变量
	 * @param extendAtrr  变量的描述信息
	 * @return
	 */
	public String getTitleVarValue(String extendAtrr)
	{
		String context="";
		HashMap map=analyseExtendAttr(extendAtrr);   //分析字符串描述
		int mode=Integer.parseInt((String)map.get("mode"));
		String type=(String)map.get("type");
		String expr=(String)map.get("expr");
		String BIGNUM = extendAttrXML(extendAtrr,"BIGNUM");
		if(mode==1)      //求个数
		{
				context=String.valueOf(getCount());
				if("true".equalsIgnoreCase(BIGNUM)){
					context = NumToRMBBo.NumToRMBStr(Double.parseDouble(context));
				}
		}
		else
		{
			// 2 首记录  3末记录   4平均值   5求总合   6求最大   7求最小
			String sql= "";
			if("salary".equalsIgnoreCase(infor_Flag)) {
                sql=getTitleVarSql(map);
            } else {
                sql=getTitleVarSql1(map);
            }
			
			ResultSet resultSet=null;
			Statement statement=null;
			if(sql==null||sql.trim().length()<1) {
                return "";
            }
			try
			{
				ContentDAO dao = new ContentDAO(conn);
				resultSet=dao.search(sql);
				String value="";
			
				if(resultSet.next())
				{
					if(!"M".equals(type)) {
                        value=resultSet.getString(expr);
                    } else {
                        value=Sql_switcher.readMemo(resultSet,expr);
                    }
				}
			
				if("N".equals(type))
				{
					String dec=(String)map.get("dec");
					if(value!=null&&!"".equals(value)) {
                        value=round(value,Integer.parseInt(dec));
                    } else {
                        value="0";
                    }
					if("true".equalsIgnoreCase(BIGNUM)){
						value = NumToRMBBo.NumToRMBStr(Double.parseDouble(value));
					}
				}
				context=value;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return context;
	}
	
	
	
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql(HashMap map)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if(!"stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(this.tableName.split("_")[2]);
            } else {
                this.gridNoMap=getNoMap(this.tableName.split("_")[1]);
            }
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总合
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null){
			sql.append(Sql_switcher.isnull("C"+(String)this.gridNoMap.get(expr), "0"));
			if(!"2".equals(mode)&&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+this.tableName);	
			if("2".equals(mode))
			{
				if (isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN ");
				} else if (isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+this.tableName+")");
            }
		}else{
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				FieldItem fielditem1 = DataDictionary.getFieldItem(groupPoint);
				if("UN".equalsIgnoreCase(fielditem1.getCodesetid())
						|| "UM".equalsIgnoreCase(fielditem1.getCodesetid())){
					FieldItem fielditem = DataDictionary.getFieldItem(expr);
					sql.append(Sql_switcher.isnull(expr, "0"));
					if(!"2".equals(mode)&&!"3".equals(mode))
					{
						sql.append(")");
					}
					sql.append(" "+expr+" from ");
					sql.append(fielditem.getFieldsetid());
					if(year!=null&&year.trim().length()==4){
						sql.append(" where ");
						sql.append(Sql_switcher.year(fielditem.getFieldsetid()+"Z0")+"="+year);
						if(month!=null&&month.length()>0){
							sql.append(" and ");
							sql.append(Sql_switcher.month(fielditem.getFieldsetid()+"Z0")+"="+month);
						}
						sql.append(" and B0110='");
						sql.append(this.getGroupNcode());
						sql.append("'");
					}else{
						ContentDAO dao = new ContentDAO(this.conn);
						RowSet rowSet;
						try {
							rowSet = dao.search("select a00z0 from "+this.tableName);
							Calendar c=Calendar.getInstance();
							while(rowSet.next()){
								Date d=rowSet.getDate(1);
								if(d!=null){
									c.setTime(d);
									year = c.get(Calendar.YEAR)+"";
									month = (c.get(Calendar.MONTH)+1)+"";
									break;
								}
							}
							if(year!=null&&year.trim().length()==4){
								sql.append(" where ");
								sql.append(Sql_switcher.year(fielditem.getFieldsetid()+"Z0")+"="+year);
								if(month!=null&&month.length()>0){
									sql.append(" and ");
									sql.append(Sql_switcher.month(fielditem.getFieldsetid()+"Z0")+"="+month);
								}
							}
							sql.append(" and B0110='");
							sql.append(this.getGroupNcode());
							sql.append("'");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
                    return "";
                }
			}else {
                return "";
            }
		}
		return sql.toString();
	}
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql1(HashMap map)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if(!"stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(this.tableName.split("_")[2]);
            } else {
                this.gridNoMap=getNoMap(this.tableName.split("_")[1]);
            }
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总合
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null){
			sql.append(Sql_switcher.isnull("C"+(String)this.gridNoMap.get(expr), "0"));
			if(!"2".equals(mode)&&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+this.tableName);	
			if("2".equals(mode))
			{
				if (isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN ");
				} else if (isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+this.tableName+")");
            }
		}else{
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				FieldItem fielditem1 = DataDictionary.getFieldItem(groupPoint);
				if("UN".equalsIgnoreCase(fielditem1.getCodesetid())
						|| "UM".equalsIgnoreCase(fielditem1.getCodesetid())){
					FieldItem fielditem = DataDictionary.getFieldItem(expr);
					sql.append(Sql_switcher.isnull(expr, "0"));
					if(!"2".equals(mode)&&!"3".equals(mode))
					{
						sql.append(")");
					}
					sql.append(" "+expr+" from ");
					sql.append(fielditem.getFieldsetid());
					sql.append(" where  B0110='");
					sql.append(this.getGroupNcode());
					sql.append("'");
				}else {
                    return "";
                }
			}else {
                return "";
            }
		}
		return sql.toString();
	}
	public String  isNotNull(String fieldName)
	{
		String str=fieldName+" is not null ";
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
            str+=" and "+fieldName+"<>''";
        }
		return str;
	}
	
	
	public HashMap getNoMap(String tabid)
	{
		HashMap map=new HashMap();
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search("select GridNo,Field_Name from muster_cell where "+isNotNull("Field_Name")+"  and  Tabid="+tabid);
			while(rowSet.next())
			{
				map.put(rowSet.getString("Field_Name").toUpperCase(),rowSet.getString("GridNo"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 取得记录的总行数
	 * @param infor_Flag
	 * @param tempTable
	 * @return
	 */
	public int getCount()
	{
		int count=0;
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String tempCount2 = "";
			String where = this.privConditionStr;
			if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)) {
				tempCount2 = " count(A0100) a ";							
			} else if ("2".equals(infor_Flag)) {
                tempCount2 = " count(B0110) a ";
            } else if ("3".equals(infor_Flag)) {
                tempCount2 = " count(E01A1) a ";
            } else {
                tempCount2 = " count(A0100) a";
            }
			String sql2 = "select " + tempCount2 + " from "+ this.tableName + where;
			dao.update("update "+this.tableName+" set groupN='' where groupN is null");
			HmusterXML hmxml = new HmusterXML(this.conn,tabid);
			String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
			GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
			if(GROUPFIELD.trim().length()>4){
				FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
				if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){
					if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						//orderby=" order by A0000,recidx";
					} else if (this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						sql2="select count(*) from ("+sql2+" group by GroupN,GroupV) a";
					}
					
				}else{
					if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						//orderby=" order by GroupN,recidx";
					} else if (this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						sql2="select count(*) from ("+sql2+" group by GroupN,GroupV) a";
					}
				}
			}else{
				if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					//orderby=" order by GroupN,recidx";
				} else if (this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql2="select count(*) from ("+sql2+" group by GroupN,GroupV) a";
				}
			}
			rowSet = dao.search(sql2);
			if (rowSet.next()) {
				count = rowSet.getInt(1);
			} else {
				count = 0;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	
	
	private HashMap  analyseExtendAttr(String extendAttr)
	{
		HashMap map=new HashMap();
		String s=extendAttr;
		String setid=s.substring(s.indexOf("<SETID>")+7,s.indexOf("</SETID>"));
		String expr=s.substring(s.indexOf("<EXPR>")+6,s.indexOf("</EXPR>"));
		String mode=s.substring(s.indexOf("<MODE>")+6,s.indexOf("</MODE>"));
		String type=s.substring(s.indexOf("<TYPE>")+6,s.indexOf("</TYPE>"));
		String format=s.substring(s.indexOf("<FORMAT>")+8,s.indexOf("</FORMAT>"));
		String len=s.substring(s.indexOf("<LEN>")+5,s.indexOf("</LEN>"));
		String dec=s.substring(s.indexOf("<DEC>")+5,s.indexOf("</DEC>"));
		
		map.put("setid",setid);
		map.put("expr",expr);
		map.put("mode",mode);
		map.put("type",type);
		map.put("format",format);
		map.put("len",len);
		map.put("dec",dec);
		
		return map;
	}
	
	/**
	 * 取得标题在excel的纵坐标位置
	 * @param rleft
	 * @return
	 */
	public short getColumn_y(int rleft)
	{
		short y=0;
		double maxright=0;
		short maxy=0;
		LazyDynaBean abean=null;
		for(int i=0;i<this.rowInfoList.size();i++)
		{
			abean=(LazyDynaBean)this.rowInfoList.get(i);
			double a_rleft=Double.parseDouble((String)abean.get("rleft"));
			double a_width=Double.parseDouble((String)abean.get("rwidth"));
			double a_rtop=Double.parseDouble((String)abean.get("rtop"));
			double a_rheight=Double.parseDouble((String)abean.get("rheight"));
			
			
			if(rleft>=a_rleft&&rleft<=(a_rleft+a_width)&&a_rtop+a_rheight==itemGridArea[1]+itemGridArea[3])
			{
				y=Short.parseShort((String)abean.get("from_y"));
				break;
			}
			if(i == this.rowInfoList.size() - 1){
			    maxright = a_rleft+a_width;
			    maxy = Short.parseShort((String)abean.get("from_y"));
			}
		}
		if(y==0&&rleft>maxright) {
            y = maxy;
        }
		return y;
	}
	/**
	 * 表头
	 */
	public void executeTabHeader()
	{
	    LazyDynaBean abean=null;
		for(int i=0;i<this.rowInfoList.size();i++)
		{
			abean=(LazyDynaBean)this.rowInfoList.get(i);
			String nhide=(String)abean.get("nhide");
			if(nhide!=null&& "1".equals(nhide)) {
                continue;
            }
			String gridno = (String)abean.get("gridno");
			if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().get(gridno)!=null&& "1".equals(this.column)&& "1".equals(this.dataarea)) {
                continue;
            }
			String fontname=(String)abean.get("fontname");
			String fontsize=(String)abean.get("fontsize");
			int    fonteffect=Integer.parseInt((String)abean.get("fonteffect"));
			int from_x=Integer.parseInt((String)abean.get("from_x"));
			short from_y=Short.parseShort((String)abean.get("from_y"));
			
			double rt = roundNum((String)abean.get("rtop"),2);
			double rl = roundNum((String)abean.get("rleft"),2);
			double rw = roundNum((String)abean.get("rwidth"),2);
			double rh = roundNum((String)abean.get("rheight"),2);
		
			if(abean.get("to_x")!=null)
			{
				int to_x=Integer.parseInt((String)abean.get("to_x"));
				short to_y=Short.parseShort((String)abean.get("to_y"));
				String titletemp = "";
				for(int j=0;j<bodyList.size();j++){
					RecordVo vo = (RecordVo) this.bodyList.get(j);
					int rtop = vo.getInt("rtop");
					int rleft = vo.getInt("rleft")+30;
					if((rt+rh)>rtop&&rtop>rt&&rleft>rl&&rleft<(rl+rw)){
						titletemp = getTitleValue(vo).replaceAll("&nbsp;", " ");
					}
				}
				
				String hz=(String)abean.get("hz")+titletemp;
				String align=(String)abean.get("align");
				String a_lign="l";
				if("1".equals(align)|| "4".equals(align)|| "7".equals(align)) {
                    a_lign="ccc";
                }
				if("2".equals(align)|| "5".equals(align)|| "8".equals(align)) {
                    a_lign="r";
                }

				HSSFFont font=getFont(fontname,fontsize,fonteffect);
				//wangcq 2014-12-08 单元格边线格式
				String ltrb = (String)abean.get("l") + (String)abean.get("t") + (String)abean.get("r") + (String)abean.get("b");
				executeCell(from_x,from_y,to_x,to_y,hz,a_lign,font,ltrb);
				if(from_x>this.fromRow) {
                    this.fromRow=from_x;
                }
				if(from_x==to_x&&from_y==to_y){
					//HSSFRow row = sheet.createRow(from_x);//xuj 升级poi改动
					HSSFRow row = sheet.getRow(from_x);
					if(row==null) {
                        row = sheet.createRow(from_x);
                    }

					float r_w = Float.parseFloat((String)abean.get("rwidth"))/0.27f;
					float r_h = Float.parseFloat((String)abean.get("rheight"))/0.67f;
					short rheight=Short.parseShort(PubFunc.round(r_h+"",0));
					row.setHeight((short)(rheight*20));
					if(((short)(rheight*20))>this.height) {
                        this.height=(short)(rheight*20);
                    }
					short rwidth=Short.parseShort(PubFunc.round(r_w+"",0));
					this.sheet.setColumnWidth(to_y, (short)(rwidth*10));
					
				}
			}
		}
		
	/*	if(rowLayNum==1)
		{
			abean=(LazyDynaBean)this.rowInfoList.get(0);
			this.sheet.getRow(Integer.parseInt((String)abean.get("from_x"))).setHeight((short)600);
		}*/
		
	}
	public void executeXTabHeader()
	{
	    LazyDynaBean abean=null;
		for(int i=0;i<this.rowInfoList.size();i++)
		{
			abean=(LazyDynaBean)this.rowInfoList.get(i);
			String nhide=(String)abean.get("nhide");
			if(nhide!=null&& "1".equals(nhide)) {
                continue;
            }
			String gridno = (String)abean.get("gridno");
			if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().get(gridno)!=null&& "1".equals(this.column)&& "1".equals(this.dataarea)) {
                continue;
            }
			String fontname=(String)abean.get("fontname");
			String fontsize=(String)abean.get("fontsize");
			int    fonteffect=Integer.parseInt((String)abean.get("fonteffect"));
			int from_x=Integer.parseInt((String)abean.get("from_x"));
			short from_y=Short.parseShort((String)abean.get("from_y"));
			
			double rt = roundNum((String)abean.get("rtop"),2);
			double rl = roundNum((String)abean.get("rleft"),2);
			double rw = roundNum((String)abean.get("rwidth"),2);
			double rh = roundNum((String)abean.get("rheight"),2);
		
			if(abean.get("to_x")!=null)
			{
				int to_x=Integer.parseInt((String)abean.get("to_x"));
				short to_y=Short.parseShort((String)abean.get("to_y"));
				String titletemp = "";
				for(int j=0;j<bodyList.size();j++){
					RecordVo vo = (RecordVo) this.bodyList.get(j);
					int rtop = vo.getInt("rtop");
					int rleft = vo.getInt("rleft")+30;
					if((rt+rh)>rtop&&rtop>rt&&rleft>rl&&rleft<(rl+rw)){
						titletemp = getTitleValue(vo).replaceAll("&nbsp;", " ");
					}
				}
				
				String hz=(String)abean.get("hz")+titletemp;
				String align=(String)abean.get("align");
				String a_lign="l";
				if("1".equals(align)|| "4".equals(align)|| "7".equals(align)) {
                    a_lign="ccc";
                }
				if("2".equals(align)|| "5".equals(align)|| "8".equals(align)) {
                    a_lign="r";
                }

				XSSFFont font=getXFont(fontname,fontsize,fonteffect);
				//wangcq 2014-12-08 单元格边线格式
				String ltrb = (String)abean.get("l") + (String)abean.get("t") + (String)abean.get("r") + (String)abean.get("b");
				executeXCell(from_x,from_y,to_x,to_y,hz,a_lign,font,ltrb);
				if(from_x>this.fromRow) {
                    this.fromRow=from_x;
                }
				if(from_x==to_x&&from_y==to_y){
					//HSSFRow row = sheet.createRow(from_x);//xuj 升级poi改动
					Row row = xsheet.getRow(from_x);
					if(row==null) {
                        row = xsheet.createRow(from_x);
                    }

					float r_w = Float.parseFloat((String)abean.get("rwidth"))/0.27f;
					float r_h = Float.parseFloat((String)abean.get("rheight"))/0.67f;
					short rheight=Short.parseShort(PubFunc.round(r_h+"",0));
					row.setHeight((short)(rheight*20));
					if(((short)(rheight*20))>this.height) {
                        this.height=(short)(rheight*20);
                    }
					short rwidth=Short.parseShort(PubFunc.round(r_w+"",0));
					this.xsheet.setColumnWidth(to_y, (short)(rwidth*10));
					
				}
			}
		}
		
	/*	if(rowLayNum==1)
		{
			abean=(LazyDynaBean)this.rowInfoList.get(0);
			this.sheet.getRow(Integer.parseInt((String)abean.get("from_x"))).setHeight((short)600);
		}*/
		
	}
	
	public HSSFFont getFont(String fontName,String fontSize,int fontEffect)
	{
		
		 HSSFFont font = wb.createFont();
		 if(fontName==null||fontName.trim().length()==0){
			 font.setFontHeightInPoints((short)10);
			 font.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
		 }else{
			 font.setFontHeightInPoints(Short.parseShort(fontSize));
			 font.setFontName(fontName);
			 if(fontEffect==2)
			 {
				 font.setBold(true);
			 }
			 else if(fontEffect==3)
			 {
				 font.setItalic(true);
			 }
			 else if(fontEffect==4)
			 {
				 font.setBold(true);
				 font.setItalic(true);
			 }
		 }
		return font;
	}
	
	public XSSFFont getXFont(String fontName,String fontSize,int fontEffect)
	{
		
		 XSSFFont font =(XSSFFont)xWb.createFont();
		 if(fontName==null||fontName.trim().length()==0){
			 font.setFontHeightInPoints((short)10);
			 font.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
		 }else{
			 font.setFontHeightInPoints(Short.parseShort(fontSize));
			 font.setFontName(fontName);
			 if(fontEffect==2)
			 {
				 font.setBold(true);
			 }
			 else if(fontEffect==3)
			 {
				 font.setItalic(true);
			 }
			 else if(fontEffect==4)
			 {
				 font.setBold(true);
				 font.setItalic(true);
			 }
		 }
		return font;
	}
	
	/**
	 * @param operater 1:横向 2:纵向
	 */
	public void excuteXhorizontal(int operater)
	{
		try
		{
			XSSFFont font = (XSSFFont)xWb.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
			this.xstyle_r.setFont(font);
			this.xstyle.setFont(font);
			this.xstyle_ccc.setFont(font);
			int xx=0;
			int _to_x=0;
			String groupv="";
			/**当横向分栏的时候，每行数据之间有一个空行，所以不支持合并格，只有在不分栏并且多行数据区时才合并，先这样做吧*/
			ArrayList rowMergeList = new ArrayList();
			int [] rowMergeHeight = new int[rowInfoList.size()];//记录每行合并的格数（高度）
			int [] fromX = new int[rowInfoList.size()];//起始row坐标
			String[] values = new String[rowInfoList.size()];//合并格的值
			boolean tableTypeFlag=false;//纵分 列表展现
			if(operater==2){
				HmusterXML hmxml = new HmusterXML(this.conn,tabid);
				String iscolum=hmxml.getValue(HmusterXML.RECORDWAY);//判断纵向分栏是卡片式还是列表式  
				if("0".equals(iscolum)) {
                    tableTypeFlag=true;
                }
			}
			
			for(int arr_index=0;arr_index<rowMergeHeight.length;arr_index++)
			{
				rowMergeHeight[arr_index]=0;
				fromX[arr_index]=0;
				values[arr_index]="";
			}
			if(resultList.size()>0)
			{
				//HSSFPatriarch patriarch=this.sheet.createDrawingPatriarch();
				ArrayList photList=null;
				int a_j=0;
				int a_r=0;
				HashMap textMap = this.getTextFormatMap();
				int size=this.hsize;
				if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
				{
					this.rowLayNum=this.rowLayNum-size;
				}
				for(int j=0;j<resultList.size();j++)
				{
					LazyDynaBean databean=(LazyDynaBean)resultList.get(j);
					String _groupv=(String)databean.get("groupv");
					if(j==0)
					{
						groupv=(String)databean.get("groupv");
					}
					if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
						if(j==0||(_groupv!=null&&!groupv.equalsIgnoreCase(_groupv)))
						{
							this.xrow = xsheet.getRow(this.rowLayNum+_to_x);//xuj 升级poi改动
							if(this.row==null) {
                                this.xrow = xsheet.createRow(this.rowLayNum+_to_x);
                            }

							this.xcell = xrow.createCell(Short.parseShort(String.valueOf(0)));
							this.xcell.setCellValue(_groupv);
							xx++;
							groupv=_groupv;
						}
					}
					LazyDynaBean abean1= null;
					if(j>0) {
                        abean1=(LazyDynaBean)resultList.get(j-1);
                    }
					photList=new ArrayList();
					LazyDynaBean abean=null;
					LazyDynaBean nextAbean = null;
					for(int i=0;i<this.rowInfoList.size();i++)
					{
						if(i!=rowInfoList.size()-1) {
                            nextAbean=(LazyDynaBean)this.rowInfoList.get(i+1);
                        }
						abean=(LazyDynaBean)this.rowInfoList.get(i);
						String gridno=(String)abean.get("gridno");
						String flag=(String)abean.get("flag");
						String field_type=(String)abean.get("field_type");
						field_type=field_type!=null?field_type:"A";
						String slope=(String)abean.get("slope");
						slope=slope!=null&&slope.trim().length()>0?slope:"0";
						String ColMerge = (String)abean.get("ColMerge");
						String ColMergeByMain = (String)abean.get("ColMergeByMain");
						String RowMerge = (String)abean.get("RowMerge");
						int from_x=0;
						if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&
						        this.getHmusterViewBo().getTextFormatMap().get(gridno)==null&& "1".equals(this.column)&& "1".equals(this.dataarea))
						{
							continue;
						}
						if(operater==1){//横向分栏
							if("1".equals(dataarea)){//多行数据区
								if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&
								        "1".equals(this.column)&& "1".equals(this.dataarea))
								{
						     		from_x=Integer.parseInt((String)abean.get("from_x"))+j*rowLayNum;
								}
								else
								{
									from_x=Integer.parseInt((String)abean.get("from_x"))+j*rowLayNum+rowLayNum/*2*//*标题行数*/;
								}
							}else {
                                from_x=Integer.parseInt((String)abean.get("from_x"))+j*(rowLayNum+1);
                            }
						}else {
                            from_x=Integer.parseInt((String)abean.get("from_x"))+a_r*rowLayNum+a_r;
                        }
						short from_y=0;
						if(operater==1) {
                            from_y=Short.parseShort((String)abean.get("from_y"));
                        } else
						{
							from_y=Short.parseShort(String.valueOf(Integer.parseInt((String)abean.get("from_y"))+row_l_map.size()*a_j+a_j));
						}
						if(abean.get("to_x")!=null)
						{
							int to_x=0;
							if(operater==1){
								if("1".equals(dataarea)) {
                                    if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
                                    {
                                        to_x=Integer.parseInt((String)abean.get("to_x"))+j*rowLayNum;
                                    }
                                    else
                                    {
                                        to_x=Integer.parseInt((String)abean.get("to_x"))+j*rowLayNum+rowLayNum/*2*/;
                                    }
                                } else {
                                    to_x=Integer.parseInt((String)abean.get("to_x"))+j*rowLayNum+j;
                                }
							}else {
                                to_x=Integer.parseInt((String)abean.get("to_x"))+a_r*rowLayNum+a_r;
                            }
							short to_y=0;
							if(operater==1) {
                                to_y=Short.parseShort((String)abean.get("to_y"));
                            } else
							{
								to_y=Short.parseShort(String.valueOf(Integer.parseInt((String)abean.get("to_y"))+row_l_map.size()*a_j+a_j));
							}
							String hz="";
							String align=(String)abean.get("align");
							String a_lign="l";
							if("1".equals(align)|| "4".equals(align)|| "7".equals(align)) {
                                a_lign="ccc";
                            }
							if("2".equals(align)|| "5".equals(align)|| "8".equals(align)) {
                                a_lign="r";
                            }
							if(!"P".equalsIgnoreCase(flag))
							{
									hz=(String)databean.get(gridno);
									if(hz==null) {
                                        hz="";
                                    }
							}
							else {
                                hz="";
                            }
							if("S".equalsIgnoreCase(flag)) {
                                field_type="N";
                            }
							/**只有不分栏，并且是多行数据区的才支持*/
							LazyDynaBean rowMergeBean = null;
							if(j==0)//第一行，全部赋予起始坐标
			    			{
			    				fromX[i]=from_x+xx;
			    				 values[i]=hz; 
			    			}
							if(operater==1&& "1".equals(dataarea)&& "1".equals(this.column)&&ColMerge!=null&& "true".equalsIgnoreCase(ColMerge))
							{
								if(abean1!=null)
								{
									if(i==rowInfoList.size()-1)
									{
										nextAbean = (LazyDynaBean)rowInfoList.get(i-1);
									}
									String RowMerge2 = (String)nextAbean.get("RowMerge"); 
									//此处判断不对
									LazyDynaBean bean =null;
									String beforeRoeMerge="false";
									if(i!=0)
									{
										bean=(LazyDynaBean)rowInfoList.get(i-1);
										beforeRoeMerge=(String)bean.get("RowMerge");
									}
						    		if(((RowMerge!=null&& "true".equalsIgnoreCase(RowMerge)&&RowMerge2!=null&& "true".equalsIgnoreCase(RowMerge2))||("true".equalsIgnoreCase(beforeRoeMerge))))
						    		{
									   String str=(String)abean1.get(gridno);
									   String str2=(String)databean.get(gridno);
									   if(str2.equalsIgnoreCase(str)&&!"".equals(str2))
									   {
										   if(ColMergeByMain!=null&& "true".equalsIgnoreCase(ColMergeByMain))
										   {
											   String mainvalue = "";
								        	    String remainvalue = "";
								     	        if(this.modelFlag!=null&& "3".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("a0100");
									        	    remainvalue = (String)abean1.get("a0100");
								        	    }else if(this.modelFlag!=null&& "21".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("b0110");
									        	    remainvalue = (String)abean1.get("b0110");
									            }else if(this.modelFlag!=null&& "41".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("e01a1");
									            	remainvalue = (String)abean1.get("e01a1");
									            }
								     	       if(mainvalue.equals(remainvalue)){
								     	    	  rowMergeHeight[i]++;
								     	    	  if(resultList.size()-1==j)//当为最后一条记录
								     	    	  {
								     	    		 LazyDynaBean gridBean = new LazyDynaBean();
									     	    	 gridBean.set("fromx",fromX[i]+"");
									     	    	 gridBean.set("content", values[i]);
									     	    	 gridBean.set("height", rowMergeHeight[i]+"");
									     	    	 gridBean.set("fromy", from_y+"");
									     	    	 gridBean.set("to_y",to_y+"");
									     	    	 gridBean.set("cell_index",i+"");
									     	    	 gridBean.set("a_lign", a_lign);
									     	    	 gridBean.set("font", font);
									     	    	 gridBean.set("field_type", field_type);
									     	    	 gridBean.set("slope", slope);
									     	    	 rowMergeList.add(gridBean);
								     	    	  }
								     	       }
								     	       else
								     	       {
								     	    	   // 要画格子了
								     	    	   LazyDynaBean gridBean = new LazyDynaBean();
								     	    	   gridBean.set("fromx",fromX[i]+"");
								     	    	   gridBean.set("content", values[i]);
								     	    	   gridBean.set("height", rowMergeHeight[i]+"");
								     	    	   gridBean.set("fromy", from_y+"");
								     	    	   gridBean.set("to_y",to_y+"");
								     	    	   gridBean.set("cell_index",i+"");
								     	    	   gridBean.set("a_lign", a_lign);
									     	       gridBean.set("font", font);
									     	       gridBean.set("field_type", field_type);
									     	       gridBean.set("slope", slope);
								     	    	   rowMergeList.add(gridBean);
								     	    	   fromX[i]=from_x+xx;
								     	    	   values[i]=hz;
								     	    	   rowMergeHeight[i]=0;
								     	    	  if(resultList.size()-1==j)//当为最后一条记录
								     	    	  {
								     	    		   gridBean = new LazyDynaBean();
									     	    	   gridBean.set("fromx",fromX[i]+"");
									     	    	   gridBean.set("content", values[i]);
									     	    	   gridBean.set("height", rowMergeHeight[i]+"");
									     	    	   gridBean.set("fromy", from_y+"");
									     	    	   gridBean.set("to_y",to_y+"");
									     	    	   gridBean.set("cell_index",i+"");
									     	    	   gridBean.set("a_lign", a_lign);
										     	       gridBean.set("font", font);
										     	       gridBean.set("field_type", field_type);
										     	       gridBean.set("slope", slope);
									     	    	   rowMergeList.add(gridBean);
								     	    	  }
								     	       }
										   }
										   else
										   {
											   rowMergeHeight[i]++;
								     	       if(resultList.size()-1==j)//当为最后一条记录
								     	       {
								     	    	   LazyDynaBean gridBean = new LazyDynaBean();
								     	    	   gridBean.set("fromx",fromX[i]+"");
								     	    	   gridBean.set("content", values[i]);
								     	    	   gridBean.set("height", rowMergeHeight[i]+"");
								     	    	   gridBean.set("fromy", from_y+"");
								     	    	   gridBean.set("to_y",to_y+"");
								     	    	   gridBean.set("cell_index",i+"");
								     	    	   gridBean.set("a_lign", a_lign);
								     	    	   gridBean.set("font", font);
								     	    	   gridBean.set("field_type", field_type);
								     	    	   gridBean.set("slope", slope);
								     	    	   rowMergeList.add(gridBean);  
								     	       }
										   }
									   }
									   else
									   {
										   // 要画格子了
						     	    	   LazyDynaBean gridBean = new LazyDynaBean();
						     	    	   gridBean.set("fromx",fromX[i]+"");
						     	    	   gridBean.set("content", values[i]);
						     	    	   gridBean.set("height", rowMergeHeight[i]+"");
						     	    	   gridBean.set("fromy", from_y+"");
						     	    	   gridBean.set("to_y",to_y+"");
						     	    	   gridBean.set("cell_index",i+"");
						     	    	   gridBean.set("a_lign", a_lign);
							     	       gridBean.set("font", font);
							     	       gridBean.set("field_type", field_type);
							     	       gridBean.set("slope", slope);
						     	    	   rowMergeList.add(gridBean);
						     	    	   fromX[i]=from_x+xx;
						     	    	   values[i]=hz;
						     	    	   rowMergeHeight[i]=0;
						     	    	  if(resultList.size()-1==j)//当为最后一条记录
						     	    	  {
						     	    		   gridBean = new LazyDynaBean();
							     	    	   gridBean.set("fromx",fromX[i]+"");
							     	    	   gridBean.set("content", values[i]);
							     	    	   gridBean.set("height", rowMergeHeight[i]+"");
							     	    	   gridBean.set("fromy", from_y+"");
							     	    	   gridBean.set("to_y",to_y+"");
							     	    	   gridBean.set("cell_index",i+"");
							     	    	   gridBean.set("a_lign", a_lign);
								     	       gridBean.set("font", font);
								     	       gridBean.set("field_type", field_type);
								     	       gridBean.set("slope", slope);
							     	    	   rowMergeList.add(gridBean); 
						     	    	  }
									   }
							    	}
						    		else
						    		{
						             	if(abean1!=null&&abean1.get(gridno).equals(databean.get(gridno))&&!"".equals((String)abean1.get(gridno))){
							            	if(ColMergeByMain!=null&& "true".equalsIgnoreCase(ColMergeByMain)){
								            	String mainvalue = "";
								        	    String remainvalue = "";
								     	        if(this.modelFlag!=null&& "3".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("a0100");
									        	    remainvalue = (String)abean1.get("a0100");
								        	    }else if(this.modelFlag!=null&& "21".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("b0110");
									        	    remainvalue = (String)abean1.get("b0110");
									            }else if(this.modelFlag!=null&& "41".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("e01a1");
									            	remainvalue = (String)abean1.get("e01a1");
									            }
								    	        if(mainvalue.equals(remainvalue)){
								    	        	ExportExcelUtil.mergeCell(xsheet, (from_x+xx-1),(short)(from_y),(int)(to_x+xx),(short)to_y);
								    	        }
							            	}else{
							         	    	ExportExcelUtil.mergeCell(xsheet, (from_x+xx-1),(short)(from_y),(int)(to_x+xx),(short)to_y);
							             	}
						            	}
						    		}
								}
							}
							else if(RowMerge!=null&& "true".equalsIgnoreCase(RowMerge))//列合并
							{
								if(nextAbean!=null)
								{
									String agridno=(String)nextAbean.get("gridno");
									String aRowMerge = (String)nextAbean.get("RowMerge");
									if(aRowMerge!=null&& "true".equalsIgnoreCase(aRowMerge)&&!"".equals(((String)databean.get(gridno)).trim())&&((String)databean.get(gridno)).trim().equalsIgnoreCase(((String)databean.get(agridno))))
									{
										ExportExcelUtil.mergeCell(xsheet, (from_x+xx),(short)(from_y),(int)(to_x+xx),(short)(to_y+1));
									}
								}
							}
							if("S".equalsIgnoreCase(flag)) {//插入序号，序号不需要保留小数位
								field_type="A";
							}
							executeXCell(from_x+xx,from_y,to_x+xx,to_y,hz,a_lign,font,field_type,slope);
							if(operater==1&& "1".equals(dataarea)&& "1".equals(this.column))
							{
								resetSizeX2(from_x+xx,0);
							}
							_to_x=to_x+xx;
							if("P".equalsIgnoreCase(flag))
							{
								 if(databean.get("a0100")!=null&&((String)databean.get("a0100")).trim().length()>0)
								 {
									 	 LazyDynaBean a_bean=new LazyDynaBean();
									 	 a_bean.set("a0100", (String)databean.get("a0100"));
									 	 a_bean.set("from_y", new Short(from_y));
									 	 a_bean.set("from_x", new Integer(from_x+xx));
									 	 a_bean.set("to_y", new Short(to_y));
									 	 a_bean.set("to_x", new Integer(to_x+xx));
									     photList.add(a_bean);
									     hz="";
								 }
							}
						}
					}
					
					
					if(operater==1)
					{
						if("1".equals(dataarea)&& "1".equals(this.column))
						{
							//resetSize2(j*rowLayNum+j+topParamLayNum,0);
						}
						else {
                            resetXSize(j*rowLayNum+j+topParamLayNum,0);
                        }
					}else {
                        resetXSize(a_r*rowLayNum+a_r+topParamLayNum,row_l_map.size()*a_j+a_j);
                    }
					LazyDynaBean a_bean=null;
					for(int d=0;d<photList.size();d++)
					{
						 a_bean=(LazyDynaBean)photList.get(d);
						 String a0100=(String)a_bean.get("a0100");
						 int from_y=((Short)a_bean.get("from_y"));
						 int to_y=((Short)a_bean.get("to_y"));
						 int   from_x=((Integer)a_bean.get("from_x"))+xx;
						 int   to_x=((Integer)a_bean.get("to_x"))+xx;
						 _to_x=to_x;
						 ClientAnchor  anchor = new XSSFClientAnchor( 0,0,1023,255,from_y,from_x,to_y+1,to_x+1);
						 anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
						 ArrayList list=createPhotoFile((String)databean.get("a0100"),"P",(String)databean.get("nbase"));
						 byte[] b=(byte[])list.get(0);
						 String ext=(String)list.get(1);
						 if(b!=null&&(".JPG".equalsIgnoreCase(ext)|| ".PNG".equalsIgnoreCase(ext)))
						 {
							 xpatriarch.createPicture(anchor,this.xWb.addPicture(b, XSSFWorkbook.PICTURE_TYPE_PNG));
						 } 
						
					}
					
					if(tableTypeFlag&&operater==2&&(a_j+1)*row_l_map.size()+a_j>this.rowInfoList.size())//纵分卡片式 限制导出列
					{
						a_j=0;
						a_r++;
					}else if(operater==2&&(a_j+1)*row_l_map.size()+a_j>250){
						a_j=0;
						a_r++;
					}else {
                        a_j++;
                    }
				}
			}
			groupRowNum=xx;
	    	if(operater==1&& "1".equals(dataarea)&& "1".equals(this.column))//画出行列全部合并的格子
		    {
		    	if(rowMergeList.size()>0)
		    	{
		    		HashMap existMap = new HashMap();
		    		for(int out=0;out<rowMergeList.size();out++)
		    		{
		    			if(existMap.get(out+"")!=null) {
                            continue;
                        }
		    			LazyDynaBean outBean = (LazyDynaBean)rowMergeList.get(out);
		    			int fromx=Integer.parseInt(((String)outBean.get("fromx")));
		    			String content=(String)outBean.get("content");
		    			int height=Integer.parseInt(((String)outBean.get("height")));
		    			short fromy=Short.parseShort(((String)outBean.get("fromy")));
		    			short to_y=Short.parseShort(((String)outBean.get("to_y")));
		    			int cell_index=Integer.parseInt(((String)outBean.get("cell_index")));
		    			String a_lign=(String)outBean.get("a_lign");
		    			XSSFFont afont =(XSSFFont)outBean.get("font");
		    			String field_type=(String)outBean.get("field_type");
		    			String slope=(String)outBean.get("slope");
		    			int columnNum=1;
		    			if("".equals(content))
		    			{
		    				executeXCell(fromx,fromy,fromx+height,(short)(fromy+columnNum-1),content,a_lign,font,field_type,slope);
		    				continue;
		    			}
		    			else
		    			{
		    	    		for(int in=0;in<rowMergeList.size();in++)
		    	    		{
		    	    			 LazyDynaBean inBean =(LazyDynaBean)rowMergeList.get(in);
		    		    		 int infromx=Integer.parseInt(((String)inBean.get("fromx")));
				        		 String incontent=(String)inBean.get("content");
				    	    	 int inheight=Integer.parseInt(((String)inBean.get("height")));
				    	    	 short infromy=Short.parseShort(((String)inBean.get("fromy")));
				    	    	 short into_y=Short.parseShort(((String)inBean.get("to_y")));
				    		     int incell_index=Integer.parseInt(((String)inBean.get("cell_index")));
				    		     if(fromx==infromx&&cell_index==incell_index)
				    		     {
				    	    		 continue;
				        		 }
				        		 if(fromx==infromx&&content.equalsIgnoreCase(incontent)&&height==inheight&&incell_index==(cell_index+columnNum))
				    	    	 {
				    	    		  columnNum++;
							    	  existMap.put(in+"", "1");
				    		     }
				        		 else
				        		 {
				        			 continue;
				    	    	 }
		    		    	 }
		    	    		executeXCell(fromx,fromy,fromx+height,(short)(fromy+columnNum-1),content,a_lign,font,field_type,slope);
		    			}
		    			 
		    		}
		    	}
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void excutehorizontal(int operater)
	{
		try
		{
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
			this.style_r.setFont(font);
			this.style.setFont(font);
			this.style_ccc.setFont(font);
			int xx=0;
			int _to_x=0;
			String groupv="";
			/**当横向分栏的时候，每行数据之间有一个空行，所以不支持合并格，只有在不分栏并且多行数据区时才合并，先这样做吧*/
			ArrayList rowMergeList = new ArrayList();
			int [] rowMergeHeight = new int[rowInfoList.size()];//记录每行合并的格数（高度）
			int [] fromX = new int[rowInfoList.size()];//起始row坐标
			String[] values = new String[rowInfoList.size()];//合并格的值
			boolean tableTypeFlag=false;//纵分 列表展现
			if(operater==2){
				HmusterXML hmxml = new HmusterXML(this.conn,tabid);
				String iscolum=hmxml.getValue(HmusterXML.RECORDWAY);//判断纵向分栏是卡片式还是列表式  
				if("0".equals(iscolum)) {
                    tableTypeFlag=true;
                }
			}
			
			for(int arr_index=0;arr_index<rowMergeHeight.length;arr_index++)
			{
				rowMergeHeight[arr_index]=0;
				fromX[arr_index]=0;
				values[arr_index]="";
			}
			if(resultList.size()>0)
			{
				//HSSFPatriarch patriarch=this.sheet.createDrawingPatriarch();
				ArrayList photList=null;
				int a_j=0;
				int a_r=0;
				HashMap textMap = this.getTextFormatMap();
				int size=this.hsize;
				if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
				{
					this.rowLayNum=this.rowLayNum-size;
				}
				for(int j=0;j<resultList.size();j++)
				{
					LazyDynaBean databean=(LazyDynaBean)resultList.get(j);
					String _groupv=(String)databean.get("groupv");
					if(j==0)
					{
						groupv=(String)databean.get("groupv");
					}
					if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
						if(j==0||(_groupv!=null&&!groupv.equalsIgnoreCase(_groupv)))
						{
							if(j==0) {//多层表头 分组标题特殊处理
								this.row = sheet.getRow(this.rowLayNum+1+_to_x+1);//xuj 升级poi改动
								if(this.row==null) {
									this.row = sheet.createRow(this.rowLayNum+1+_to_x+1);
								}
								this.cell = row.createCell(Short.parseShort(String.valueOf(0)));
							}else {
								this.row = sheet.getRow(this.rowLayNum+_to_x);//xuj 升级poi改动
								if(this.row==null) {
									this.row = sheet.createRow(this.rowLayNum+_to_x);
								}
								this.cell = row.createCell(Short.parseShort(String.valueOf(0)));
							}
							
							this.cell.setCellValue(_groupv);
							xx++;
							groupv=_groupv;
						}
					}
					LazyDynaBean abean1= null;
					if(j>0) {
                        abean1=(LazyDynaBean)resultList.get(j-1);
                    }
					photList=new ArrayList();
					LazyDynaBean abean=null;
					LazyDynaBean nextAbean = null;
					for(int i=0;i<this.rowInfoList.size();i++)
					{
						if(i!=rowInfoList.size()-1) {
                            nextAbean=(LazyDynaBean)this.rowInfoList.get(i+1);
                        }
						abean=(LazyDynaBean)this.rowInfoList.get(i);
						String gridno=(String)abean.get("gridno");
						String flag=(String)abean.get("flag");
						String field_type=(String)abean.get("field_type");
						field_type=field_type!=null?field_type:"A";
						String slope=(String)abean.get("slope");
						slope=slope!=null&&slope.trim().length()>0?slope:"0";
						String ColMerge = (String)abean.get("ColMerge");
						String ColMergeByMain = (String)abean.get("ColMergeByMain");
						String RowMerge = (String)abean.get("RowMerge");
						int from_x=0;
						//多层表头时去除文本框，只取插入指标栏
						if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&
						        this.getHmusterViewBo().getTextFormatMap().get(gridno)==null&& "1".equals(this.column)&& "1".equals(this.dataarea))
						{
							continue;
						}
						if(operater==1){//横向分栏
							if("1".equals(dataarea)){//多行数据区
								if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&
								        "1".equals(this.column)&& "1".equals(this.dataarea))
								{
						     		from_x=Integer.parseInt((String)abean.get("from_x"))+j*rowLayNum;
								}
								else
								{
									from_x=Integer.parseInt((String)abean.get("from_x"))+j*rowLayNum+rowLayNum/*2*//*标题行数*/;
								}
							}else {
                                from_x=Integer.parseInt((String)abean.get("from_x"))+j*(rowLayNum+1);
                            }
						}else {
                            from_x=Integer.parseInt((String)abean.get("from_x"))+a_r*rowLayNum+a_r;
                        }
						short from_y=0;
						if(operater==1) {
                            from_y=Short.parseShort((String)abean.get("from_y"));
                        } else
						{
							from_y=Short.parseShort(String.valueOf(Integer.parseInt((String)abean.get("from_y"))+row_l_map.size()*a_j+a_j));
						}
						if(abean.get("to_x")!=null)
						{
							int to_x=0;
							if(operater==1){
								if("1".equals(dataarea)) {
                                    if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
                                    {
                                        to_x=Integer.parseInt((String)abean.get("to_x"))+j*rowLayNum;
                                    }
                                    else
                                    {
                                        to_x=Integer.parseInt((String)abean.get("to_x"))+j*rowLayNum+rowLayNum/*2*/;
                                    }
                                } else {
                                    to_x=Integer.parseInt((String)abean.get("to_x"))+j*rowLayNum+j;
                                }
							}else {
                                to_x=Integer.parseInt((String)abean.get("to_x"))+a_r*rowLayNum+a_r;
                            }
							short to_y=0;
							if(operater==1) {
                                to_y=Short.parseShort((String)abean.get("to_y"));
                            } else
							{
								to_y=Short.parseShort(String.valueOf(Integer.parseInt((String)abean.get("to_y"))+row_l_map.size()*a_j+a_j));
							}
							String hz="";
							String align=(String)abean.get("align");
							String a_lign="l";
							if("1".equals(align)|| "4".equals(align)|| "7".equals(align)) {
                                a_lign="ccc";
                            }
							if("2".equals(align)|| "5".equals(align)|| "8".equals(align)) {
                                a_lign="r";
                            }
							if(!"P".equalsIgnoreCase(flag))
							{
									hz=(String)databean.get(gridno);
									if(hz==null) {
                                        hz="";
                                    }
							}
							else {
                                hz="";
                            }
							if("S".equalsIgnoreCase(flag)&&StringUtils.isNumeric(hz)) {
                                field_type="N";
                            }
							/**只有不分栏，并且是多行数据区的才支持*/
							LazyDynaBean rowMergeBean = null;
							if(j==0)//第一行，全部赋予起始坐标
			    			{
			    				fromX[i]=from_x+xx;
			    				 values[i]=hz; 
			    			}
							if(operater==1&& "1".equals(dataarea)&& "1".equals(this.column)&&ColMerge!=null&& "true".equalsIgnoreCase(ColMerge))
							{
								if(abean1!=null)
								{
									if(i==rowInfoList.size()-1)
									{
										nextAbean = (LazyDynaBean)rowInfoList.get(i-1);
									}
									String RowMerge2 = (String)nextAbean.get("RowMerge"); 
									//此处判断不对
									LazyDynaBean bean =null;
									String beforeRoeMerge="false";
									if(i!=0)
									{
										bean=(LazyDynaBean)rowInfoList.get(i-1);
										beforeRoeMerge=(String)bean.get("RowMerge");
									}
						    		if(((RowMerge!=null&& "true".equalsIgnoreCase(RowMerge)&&RowMerge2!=null&& "true".equalsIgnoreCase(RowMerge2))||("true".equalsIgnoreCase(beforeRoeMerge))))
						    		{
									   String str=(String)abean1.get(gridno);
									   String str2=(String)databean.get(gridno);
									   if(str2.equalsIgnoreCase(str)&&!"".equals(str2))
									   {
										   if(ColMergeByMain!=null&& "true".equalsIgnoreCase(ColMergeByMain))
										   {
											   String mainvalue = "";
								        	    String remainvalue = "";
								     	        if(this.modelFlag!=null&& "3".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("a0100");
									        	    remainvalue = (String)abean1.get("a0100");
								        	    }else if(this.modelFlag!=null&& "21".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("b0110");
									        	    remainvalue = (String)abean1.get("b0110");
									            }else if(this.modelFlag!=null&& "41".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("e01a1");
									            	remainvalue = (String)abean1.get("e01a1");
									            }
								     	       if(mainvalue.equals(remainvalue)){
								     	    	  rowMergeHeight[i]++;
								     	    	  if(resultList.size()-1==j)//当为最后一条记录
								     	    	  {
								     	    		 LazyDynaBean gridBean = new LazyDynaBean();
									     	    	 gridBean.set("fromx",fromX[i]+"");
									     	    	 gridBean.set("content", values[i]);
									     	    	 gridBean.set("height", rowMergeHeight[i]+"");
									     	    	 gridBean.set("fromy", from_y+"");
									     	    	 gridBean.set("to_y",to_y+"");
									     	    	 gridBean.set("cell_index",i+"");
									     	    	 gridBean.set("a_lign", a_lign);
									     	    	 gridBean.set("font", font);
									     	    	 gridBean.set("field_type", field_type);
									     	    	 gridBean.set("slope", slope);
									     	    	 rowMergeList.add(gridBean);
								     	    	  }
								     	       }
								     	       else
								     	       {
								     	    	   // 要画格子了
								     	    	   LazyDynaBean gridBean = new LazyDynaBean();
								     	    	   gridBean.set("fromx",fromX[i]+"");
								     	    	   gridBean.set("content", values[i]);
								     	    	   gridBean.set("height", rowMergeHeight[i]+"");
								     	    	   gridBean.set("fromy", from_y+"");
								     	    	   gridBean.set("to_y",to_y+"");
								     	    	   gridBean.set("cell_index",i+"");
								     	    	   gridBean.set("a_lign", a_lign);
									     	       gridBean.set("font", font);
									     	       gridBean.set("field_type", field_type);
									     	       gridBean.set("slope", slope);
								     	    	   rowMergeList.add(gridBean);
								     	    	   fromX[i]=from_x+xx;
								     	    	   values[i]=hz;
								     	    	   rowMergeHeight[i]=0;
								     	    	  if(resultList.size()-1==j)//当为最后一条记录
								     	    	  {
								     	    		   gridBean = new LazyDynaBean();
									     	    	   gridBean.set("fromx",fromX[i]+"");
									     	    	   gridBean.set("content", values[i]);
									     	    	   gridBean.set("height", rowMergeHeight[i]+"");
									     	    	   gridBean.set("fromy", from_y+"");
									     	    	   gridBean.set("to_y",to_y+"");
									     	    	   gridBean.set("cell_index",i+"");
									     	    	   gridBean.set("a_lign", a_lign);
										     	       gridBean.set("font", font);
										     	       gridBean.set("field_type", field_type);
										     	       gridBean.set("slope", slope);
									     	    	   rowMergeList.add(gridBean);
								     	    	  }
								     	       }
										   }
										   else
										   {
											   rowMergeHeight[i]++;
								     	       if(resultList.size()-1==j)//当为最后一条记录
								     	       {
								     	    	   LazyDynaBean gridBean = new LazyDynaBean();
								     	    	   gridBean.set("fromx",fromX[i]+"");
								     	    	   gridBean.set("content", values[i]);
								     	    	   gridBean.set("height", rowMergeHeight[i]+"");
								     	    	   gridBean.set("fromy", from_y+"");
								     	    	   gridBean.set("to_y",to_y+"");
								     	    	   gridBean.set("cell_index",i+"");
								     	    	   gridBean.set("a_lign", a_lign);
								     	    	   gridBean.set("font", font);
								     	    	   gridBean.set("field_type", field_type);
								     	    	   gridBean.set("slope", slope);
								     	    	   rowMergeList.add(gridBean);  
								     	       }
										   }
									   }
									   else
									   {
										   // 要画格子了
						     	    	   LazyDynaBean gridBean = new LazyDynaBean();
						     	    	   gridBean.set("fromx",fromX[i]+"");
						     	    	   gridBean.set("content", values[i]);
						     	    	   gridBean.set("height", rowMergeHeight[i]+"");
						     	    	   gridBean.set("fromy", from_y+"");
						     	    	   gridBean.set("to_y",to_y+"");
						     	    	   gridBean.set("cell_index",i+"");
						     	    	   gridBean.set("a_lign", a_lign);
							     	       gridBean.set("font", font);
							     	       gridBean.set("field_type", field_type);
							     	       gridBean.set("slope", slope);
						     	    	   rowMergeList.add(gridBean);
						     	    	   fromX[i]=from_x+xx;
						     	    	   values[i]=hz;
						     	    	   rowMergeHeight[i]=0;
						     	    	  if(resultList.size()-1==j)//当为最后一条记录
						     	    	  {
						     	    		   gridBean = new LazyDynaBean();
							     	    	   gridBean.set("fromx",fromX[i]+"");
							     	    	   gridBean.set("content", values[i]);
							     	    	   gridBean.set("height", rowMergeHeight[i]+"");
							     	    	   gridBean.set("fromy", from_y+"");
							     	    	   gridBean.set("to_y",to_y+"");
							     	    	   gridBean.set("cell_index",i+"");
							     	    	   gridBean.set("a_lign", a_lign);
								     	       gridBean.set("font", font);
								     	       gridBean.set("field_type", field_type);
								     	       gridBean.set("slope", slope);
							     	    	   rowMergeList.add(gridBean); 
						     	    	  }
									   }
							    	}
						    		else
						    		{
						             	if(abean1!=null&&abean1.get(gridno).equals(databean.get(gridno))&&!"".equals((String)abean1.get(gridno))){
							            	if(ColMergeByMain!=null&& "true".equalsIgnoreCase(ColMergeByMain)){
								            	String mainvalue = "";
								        	    String remainvalue = "";
								     	        if(this.modelFlag!=null&& "3".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("a0100");
									        	    remainvalue = (String)abean1.get("a0100");
								        	    }else if(this.modelFlag!=null&& "21".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("b0110");
									        	    remainvalue = (String)abean1.get("b0110");
									            }else if(this.modelFlag!=null&& "41".equals(this.modelFlag)){
									            	mainvalue = (String)abean.get("e01a1");
									            	remainvalue = (String)abean1.get("e01a1");
									            }
								    	        if(mainvalue.equals(remainvalue)){
								    	        	ExportExcelUtil.mergeCell(sheet, (from_x+xx-1),(short)(from_y),(int)(to_x+xx),(short)to_y);
								    	        }
							            	}else{
							         	    	ExportExcelUtil.mergeCell(sheet, (from_x+xx-1),(short)(from_y),(int)(to_x+xx),(short)to_y);
							             	}
						            	}
						    		}
								}
							}
							else if(RowMerge!=null&& "true".equalsIgnoreCase(RowMerge))//列合并
							{
								if(nextAbean!=null)
								{
									String agridno=(String)nextAbean.get("gridno");
									String aRowMerge = (String)nextAbean.get("RowMerge");
									if(aRowMerge!=null&& "true".equalsIgnoreCase(aRowMerge)&&!"".equals(((String)databean.get(gridno)).trim())&&((String)databean.get(gridno)).trim().equalsIgnoreCase(((String)databean.get(agridno))))
									{
										ExportExcelUtil.mergeCell(sheet, (from_x+xx),(short)(from_y),(int)(to_x+xx),(short)(to_y+1));
									}
								}
							}
							if("S".equalsIgnoreCase(flag)) {//插入序号，序号不需要保留小数位
								field_type="A";
							}
							executeCell(from_x+xx,from_y,to_x+xx,to_y,hz,a_lign,font,field_type,slope);
							if(operater==1&& "1".equals(dataarea)&& "1".equals(this.column))
							{
								resetSize2(from_x+xx,0);
							}
							_to_x=to_x+xx;
							if("P".equalsIgnoreCase(flag))
							{
								 if(databean.get("a0100")!=null&&((String)databean.get("a0100")).trim().length()>0)
								 {
									 	 LazyDynaBean a_bean=new LazyDynaBean();
									 	 a_bean.set("a0100", (String)databean.get("a0100"));
									 	 a_bean.set("from_y", new Short(from_y));
									 	 a_bean.set("from_x", new Integer(from_x+xx));
									 	 a_bean.set("to_y", new Short(to_y));
									 	 a_bean.set("to_x", new Integer(to_x+xx));
									     photList.add(a_bean);
									     hz="";
								 }
							}
						}
					}
					
					
					if(operater==1)
					{
						if("1".equals(dataarea)&& "1".equals(this.column))
						{
							//resetSize2(j*rowLayNum+j+topParamLayNum,0);
						}
						else {
							resetSize(j*rowLayNum+j+topParamLayNum,0);
						}
					}else {
                        resetSize(a_r*rowLayNum+a_r+topParamLayNum,row_l_map.size()*a_j+a_j);
                    }
					LazyDynaBean a_bean=null;
					for(int d=0;d<photList.size();d++)
					{
						 a_bean=(LazyDynaBean)photList.get(d);
						 String a0100=(String)a_bean.get("a0100");
						 short from_y=((Short)a_bean.get("from_y")).shortValue();
						 short to_y=((Short)a_bean.get("to_y")).shortValue();
						 int   from_x=((Integer)a_bean.get("from_x")).intValue()+xx;
						 int   to_x=((Integer)a_bean.get("to_x")).intValue()+xx;
						 _to_x=to_x;
						 HSSFClientAnchor anchor=new HSSFClientAnchor();
						 anchor = new HSSFClientAnchor( 0,0,1023,255,from_y,from_x,to_y,to_x);
						 anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
						 ArrayList list=createPhotoFile((String)databean.get("a0100"),"P",(String)databean.get("nbase"));
						 byte[] b=(byte[])list.get(0);
						 String ext=(String)list.get(1);
						 if(b!=null&&(".JPG".equalsIgnoreCase(ext)|| ".PNG".equalsIgnoreCase(ext)))
						 {
							 patriarch.createPicture(anchor,this.wb.addPicture(b, HSSFWorkbook.PICTURE_TYPE_JPEG));
						 } 
						
					}
					
					if(tableTypeFlag&&operater==2&&(a_j+1)*row_l_map.size()+a_j>this.rowInfoList.size())//纵分卡片式 限制导出列
					{
						a_j=0;
						a_r++;
					}else if(operater==2&&(a_j+1)*row_l_map.size()+a_j>250){
						a_j=0;
						a_r++;
					}else {
                        a_j++;
                    }
				}
			}
			groupRowNum=xx;
	    	if(operater==1&& "1".equals(dataarea)&& "1".equals(this.column))//画出行列全部合并的格子
		    {
		    	if(rowMergeList.size()>0)
		    	{
		    		HashMap existMap = new HashMap();
		    		for(int out=0;out<rowMergeList.size();out++)
		    		{
		    			if(existMap.get(out+"")!=null) {
                            continue;
                        }
		    			LazyDynaBean outBean = (LazyDynaBean)rowMergeList.get(out);
		    			int fromx=Integer.parseInt(((String)outBean.get("fromx")));
		    			String content=(String)outBean.get("content");
		    			int height=Integer.parseInt(((String)outBean.get("height")));
		    			short fromy=Short.parseShort(((String)outBean.get("fromy")));
		    			short to_y=Short.parseShort(((String)outBean.get("to_y")));
		    			int cell_index=Integer.parseInt(((String)outBean.get("cell_index")));
		    			String a_lign=(String)outBean.get("a_lign");
		    			HSSFFont afont =(HSSFFont)outBean.get("font");
		    			String field_type=(String)outBean.get("field_type");
		    			String slope=(String)outBean.get("slope");
		    			int columnNum=1;
		    			if("".equals(content))
		    			{
		    				executeCell(fromx,fromy,fromx+height,(short)(fromy+columnNum-1),content,a_lign,font,field_type,slope);
		    				continue;
		    			}
		    			else
		    			{
		    	    		for(int in=0;in<rowMergeList.size();in++)
		    	    		{
		    	    			 LazyDynaBean inBean =(LazyDynaBean)rowMergeList.get(in);
		    		    		 int infromx=Integer.parseInt(((String)inBean.get("fromx")));
				        		 String incontent=(String)inBean.get("content");
				    	    	 int inheight=Integer.parseInt(((String)inBean.get("height")));
				    	    	 short infromy=Short.parseShort(((String)inBean.get("fromy")));
				    	    	 short into_y=Short.parseShort(((String)inBean.get("to_y")));
				    		     int incell_index=Integer.parseInt(((String)inBean.get("cell_index")));
				    		     if(fromx==infromx&&cell_index==incell_index)
				    		     {
				    	    		 continue;
				        		 }
				        		 if(fromx==infromx&&content.equalsIgnoreCase(incontent)&&height==inheight&&incell_index==(cell_index+columnNum))
				    	    	 {
				    	    		  columnNum++;
							    	  existMap.put(in+"", "1");
				    		     }
				        		 else
				        		 {
				        			 continue;
				    	    	 }
		    		    	 }
		    	    		executeCell(fromx,fromy,fromx+height,(short)(fromy+columnNum-1),content,a_lign,font,field_type,slope);
		    			}
		    			 
		    		}
		    	}
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public XSSFFont setXContentFont(){
		XSSFFont font =(XSSFFont) xWb.createFont();
		String tableFontStyle = "select distinct FontName,FontEffect,FontSize  from muster_name where tabid="
			+ this.tabid;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search(tableFontStyle);
			if (rowSet.next()) {
				font.setFontHeightInPoints(rowSet.getShort("FontSize"));
				font.setFontName(rowSet.getString("FontName"));
			}
			String FontEffect="";
			rowSet=dao.search("select distinct FontName,FontEffect,FontSize  from muster_cell where tabid="+this.tabid);//光标重新指向第一行
			while(rowSet.next()){
				XSSFFont font1=(XSSFFont)xWb.createFont();
				FontEffect=rowSet.getString("FontEffect");
				 if("3".equals(FontEffect)){
					 font1.setItalic(true);
				   }else if("2".equals(FontEffect)){
					   font1.setBold(true);
				   }else if("4".equals(FontEffect)){
					   font1.setItalic(true);
					   font1.setBold(true);
				   }
				font1.setFontHeightInPoints(rowSet.getShort("FontSize"));
				font1.setFontName(rowSet.getString("FontName"));
				fontXList.add(font1);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		
		return font;
	}
	
	public HSSFFont setContentFont(){
		HSSFFont font = wb.createFont();
		String tableFontStyle = "select distinct FontName,FontEffect,FontSize  from muster_name where tabid="
			+ this.tabid;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search(tableFontStyle);
			if (rowSet.next()) {
				font.setFontHeightInPoints(rowSet.getShort("FontSize"));
				font.setFontName(rowSet.getString("FontName"));
			}
			String FontEffect="";
			rowSet=dao.search("select distinct FontName,FontEffect,FontSize  from muster_cell where tabid="+this.tabid);//光标重新指向第一行
			while(rowSet.next()){
				HSSFFont font1=wb.createFont();
				FontEffect=rowSet.getString("FontEffect");
				 if("3".equals(FontEffect)){
					 font1.setItalic(true);
				   }else if("2".equals(FontEffect)){
					   font1.setBold(true);
				   }else if("4".equals(FontEffect)){
					   font1.setItalic(true);
					   font1.setBold(true);
				   }
				font1.setFontHeightInPoints(rowSet.getShort("FontSize"));
				font1.setFontName(rowSet.getString("FontName"));
				fontList.add(font1);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		
		return font;
	}
	
   private int groupRowNum=0;
   
   
   
   
   /**
	 * 0 左上、 6 左中、3 左下、
	 * 1中上、  7 中中、4 中下、 
	 * 2 右上、 8 右中、5 右下、
		设置单元格格式  rowVo  align 位置
		ALIGN_CENTER
		ALIGN_LEFT
		ALIGN_RIGHT
	 * */
   public HSSFCellStyle setCellStyle(LazyDynaBean bean,HSSFCellStyle style){
	   //HSSFFont font=this.wb
	   int postion=Integer.parseInt((String)bean.get("align"));
	   switch (postion) {
			case 0:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.TOP);
				break;
			case 6:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				break;
			case 3:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				break;
			case 1:
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.TOP);
				break;
			case 7:
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				break;
			case 4:
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				break;
			case 2:
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.TOP);
				break;
			case 8:
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				break;
			case 5:
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				break;
			}
	   /**
	    * 1 常规 3 倾斜 2粗体 4 粗偏斜体
	    * setItalic(true) 3 倾斜
	    * setBold(true);//粗体显示 2
	    * */
	   String fontName=(String)bean.get("fontname");
	   short fontSize=(short)Integer.parseInt((String)bean.get("fontsize"));
	   String fontffect=(String)bean.get("fonteffect");
	  
	   for (int i = 0; i < fontList.size(); i++) {
		HSSFFont font=(HSSFFont)fontList.get(i);
		if(font.getFontName().equals(fontName)&&font.getFontHeightInPoints()==fontSize){
			if("2".equals(fontffect)){//粗体
			   if(font.getBold()){
				    style.setFont(font);
				    break;
			   }
			}else if("3".equals(fontffect)){//斜体
				if(font.getItalic()){
					style.setFont(font);
				    break;
				}
			}else if("4".equals(fontffect)){
			   if(font.getItalic()&&font.getBold()){//粗体+斜体
				   style.setFont(font);
				    break;
			   }	
			}else{
				if(!(font.getItalic()||font.getBold())){
					style.setFont(font);
					break;
				}
			}
		}
	   }
	   return style;
   }
   public XSSFCellStyle setXCellStyle(LazyDynaBean bean,XSSFCellStyle style){
	   //HSSFFont font=this.wb
	   int postion=Integer.parseInt((String)bean.get("align"));
	   switch (postion) {
			case 0:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.TOP);
				break;
			case 6:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				break;
			case 3:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				break;
			case 1:
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.TOP);
				break;
			case 7:
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				break;
			case 4:
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				break;
			case 2:
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.TOP);
				break;
			case 8:
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				break;
			case 5:
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				break;
			}
	   /**
	    * 1 常规 3 倾斜 2粗体 4 粗偏斜体
	    * setItalic(true) 3 倾斜
	    * setBold(true);//粗体显示 2
	    * */
	   String fontName=(String)bean.get("fontname");
	   short fontSize=(short)Integer.parseInt((String)bean.get("fontsize"));
	   String fontffect=(String)bean.get("fonteffect");
	   for (int i = 0; i < fontXList.size(); i++) {
		XSSFFont font=(XSSFFont)fontXList.get(i);
		if(font.getFontName().equals(fontName)&&font.getFontHeightInPoints()==fontSize){
			if("2".equals(fontffect)){
			   if(font.getBold()){
				    style.setFont(font);
				    break;
			   }
			}else if("3".equals(fontffect)){
				if(font.getItalic()){
					style.setFont(font);
				    break;
				}
			}else if("4".equals(fontffect)){
			   if(font.getItalic()&&font.getBold()){
				   style.setFont(font);
				    break;
			   }	
			}else{
				if(!(font.getItalic()&&font.getBold())){
					style.setFont(font);
					break;
				}
			}
		}
	   }
	   
	   return style;
   }
	/**
	 * 数据区
	 */
	public void executeTabDataArea()
	{
		try
		{
//			HSSFFont font = wb.createFont();
			HSSFCellStyle copyStyle=this.wb.createCellStyle();
			copyStyle.setBorderLeft(BorderStyle.THIN);
			copyStyle.setBorderRight(BorderStyle.THIN);
			copyStyle.setBorderTop(BorderStyle.THIN);
			copyStyle.setBorderBottom(BorderStyle.THIN);
			HSSFFont font =setContentFont();
			PhotoImgBo imageBo=new PhotoImgBo(this.conn);
//			font.setFontHeightInPoints((short)9);
//			font.setFontName("宋体");
			this.style_r.setFont(font);
			this.style.setFont(font);
			this.style_ccc.setFont(font);
			HSSFCellStyle styletext = style(this.wb,1);
			styletext.setWrapText(true);
			styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			//styletext.setAlignment(HorizontalAlignment.LEFT );
			
			HSSFCellStyle styleN = style(this.wb,1);
			//styleN.setAlignment(HorizontalAlignment.RIGHT );
			styleN.setWrapText(true);
			HSSFDataFormat df = this.wb.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));
			
			HSSFCellStyle styleF1 = style(this.wb,1);
			//styleF1.setAlignment(HorizontalAlignment.RIGHT );
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = this.wb.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
			
			HSSFCellStyle styleF2 = style(this.wb,1);
			//styleF2.setAlignment(HorizontalAlignment.RIGHT );
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = this.wb.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
			
			HSSFCellStyle styleF3 = style(this.wb,1);
			//styleF3.setAlignment(HorizontalAlignment.RIGHT );
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = this.wb.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
			
			HSSFCellStyle styleF4 = style(this.wb,1);
			//styleF4.setAlignment(HorizontalAlignment.RIGHT );
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = this.wb.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
			
			HSSFCellStyle styleF5 = style(this.wb,1);
			//styleF5.setAlignment(HorizontalAlignment.RIGHT );
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = this.wb.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
			int xx=0;
			boolean canMerge=false;  // 合并格有问题，暂时禁止
			String groupv="";
			String h_r = pageHeight+"";
			int hrows = Integer.parseInt(h_r.substring(0,h_r.indexOf(".")))/rows;
			if(this.height==0) {
                this.height=(short)hrows;
            }
			HashMap<String, HSSFCellStyle> styleMap=new HashMap<String, HSSFCellStyle>();
			if(resultList.size()>0)
			{
				//HSSFPatriarch patriarch=this.sheet.createDrawingPatriarch();
				int jj=1;
				for(int i=0;i<resultList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)resultList.get(i);
					String _groupv=(String)abean.get("groupv");
					if(i==0)
					{
						groupv=(String)abean.get("groupv");
					}
					if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
						if(i==0||(_groupv!=null&&!groupv.equalsIgnoreCase(_groupv)))
						{
							this.row = sheet.getRow(this.rowLayNum+this.topParamLayNum+i+xx);//xuj 升级poi改动
							if(this.row==null) {
                                this.row = sheet.createRow(this.rowLayNum+this.topParamLayNum+i+xx);
                            }

							this.cell = row.createCell(Short.parseShort(String.valueOf(0)));
							this.cell.setCellValue(_groupv);
							xx++;
							groupv=_groupv;
						}
							/*if(i!=0){//设置组内记录数
									LazyDynaBean lastBean=(LazyDynaBean)resultList.get(i-1);
								if(groupv.equals((String)lastBean.get("groupv"))){
									jj++;
								}else{
									this.row=sheet.getRow(this.rowLayNum+this.topParamLayNum+i-jj);
									this.cell=row.createCell(Short.parseShort(String.valueOf(0)));
									this.cell.setCellValue((String)lastBean.get("groupv")+jj);
									jj=1;
								}
								if(i==resultList.size()-1){//最后一个分组记录数
									this.row=sheet.getRow(this.rowLayNum+this.topParamLayNum+xx+i-jj);
									this.cell=row.createCell(Short.parseShort(String.valueOf(0)));
									this.cell.setCellValue((String)lastBean.get("groupv")+jj);
									jj=1;
								}
						}*/
					}
					
					LazyDynaBean abean1= null;
					if(i>0) {
                        abean1=(LazyDynaBean)resultList.get(i-1);
                    }
					
					for(int j=0;j<this.rowInfoBGrid.size();j++)
					{
						LazyDynaBean rowVo=(LazyDynaBean)this.rowInfoBGrid.get(j);
						String context="";	
						String nhide = (String)rowVo.get("nhide");
						if(nhide!=null&& "1".equals(nhide)) {
                            continue;
                        }
						String gridno=(String)rowVo.get("gridno");
						String field_type=(String)rowVo.get("field_type");
						String ColMerge=(String)rowVo.get("ColMerge");
						String ColMergeByMain=(String)rowVo.get("ColMergeByMain");
						//abean.set("RowMerge", RowMerge);
						field_type=field_type!=null?field_type:"A";
						String slope=(String)rowVo.get("slope");
						slope=slope!=null&&slope.trim().length()>0?slope:"0";
						String flag="H";
						if(rowVo.get("flag")!=null) {
                            flag=(String)rowVo.get("flag");
                        }
						if(canMerge&&ColMerge!=null&& "true".equalsIgnoreCase(ColMerge)){
							if(abean1!=null&&abean1.get(gridno).equals(abean.get(gridno))){
								if(ColMergeByMain!=null&& "true".equalsIgnoreCase(ColMergeByMain)){
									String mainvalue = "";
									String remainvalue = "";
									if(this.modelFlag!=null&& "3".equals(this.modelFlag)){
										mainvalue = (String)abean.get("a0100");
										remainvalue = (String)abean1.get("a0100");
									}else if(this.modelFlag!=null&& "21".equals(this.modelFlag)){
										mainvalue = (String)abean.get("b0110");
										remainvalue = (String)abean1.get("b0110");
									}else if(this.modelFlag!=null&& "41".equals(this.modelFlag)){
										mainvalue = (String)abean.get("e01a1");
										remainvalue = (String)abean1.get("e01a1");
									}
									if(mainvalue.equals(remainvalue)){
										ExportExcelUtil.mergeCell(sheet, this.rowLayNum+this.topParamLayNum+i-1+xx,
												(short)j,this.rowLayNum+this.topParamLayNum+i+xx,(short)j);
									}
								}else{
									ExportExcelUtil.mergeCell(sheet, this.rowLayNum+this.topParamLayNum+i-1+xx,
											(short)j,this.rowLayNum+this.topParamLayNum+i+xx,(short)j);
								}
							}
						}
						//this.row = sheet.createRow(this.rowLayNum+this.topParamLayNum+i);
						this.row = sheet.getRow(this.rowLayNum+this.topParamLayNum+i+xx);//xuj 升级poi改动
						if(this.row==null) {
                            this.row = sheet.createRow(this.rowLayNum+this.topParamLayNum+i+xx);
                        }
                        //wangcq 2014-12-08 获取单元格边框
						boolean left = "1".equals(rowVo.get("l"));
						boolean top = "1".equals(rowVo.get("t"));
						boolean right = "1".equals(rowVo.get("r"));
						boolean bottom = "1".equals(rowVo.get("b"));
						
						this.cell = row.createCell(Short.parseShort(String.valueOf(j)));
						if("P".equalsIgnoreCase(flag))
						{
							this.row.setHeight((short)1800);
							//this.cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						    this.cell.setCellStyle(this.style);
							this.cell.setCellValue(context);
							if(abean.get("a0100")!=null&&((String)abean.get("a0100")).trim().length()>0)
							{
								 HSSFClientAnchor anchor=new HSSFClientAnchor();
								 anchor = new HSSFClientAnchor( 0,5,1000,255,Short.parseShort(String.valueOf(j)),this.rowLayNum+this.topParamLayNum+i+xx,Short.parseShort(String.valueOf(j)),this.rowLayNum+this.topParamLayNum+i+xx);
								 anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
								 ArrayList list=createPhotoFile((String)abean.get("a0100"),"P",(String)abean.get("nbase"));
								 byte[] b=(byte[])list.get(0);
									 if(b!=null&&b.length>102400){//大于100k 压缩图片23484 网易：设计高级花名册插入照片时，不显示照片
										 b=imageBo.getlowPir(b);
									 }
								 String ext=(String)list.get(1);
								 if(b!=null&&(".JPG".equalsIgnoreCase(ext)|| ".bmp".equalsIgnoreCase(ext)|| ".PNG".equalsIgnoreCase(ext))) {
                                     patriarch.createPicture(anchor,this.wb.addPicture(b, HSSFWorkbook.PICTURE_TYPE_PNG));
                                 }
							}
						}else{
							
							if(this.isHasPhoto) {
                                this.row.setHeight((short)1800);
                            } else {
                                this.row.setHeight((short)hrows);//this.row.setHeight(this.height);
                            }
							//this.cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							context=(String)abean.get(gridno);	
							/**
							 * 0 左上、 6 左中、3 左下、
							 * 1中上、  7 中中、4 中下、 
							 * 2 右上、 8 右中、5 右下、
								设置单元格格式  rowVo  align 位置
								ALIGN_CENTER
								ALIGN_LEFT
								ALIGN_RIGHT
							 * */
							//String align=(String)rowVo.get("align");
							if("N".equalsIgnoreCase(field_type)|| "S".equalsIgnoreCase(flag)|| "R".equalsIgnoreCase(flag)){
								if("0".equals(slope)){
									styleN=setCellStyle(rowVo,styleN);
									this.cell.setCellStyle(styleN);
								}else if("1".equals(slope)){
									styleF1=setCellStyle(rowVo, styleF1);
									this.cell.setCellStyle(styleF1);
								}else if("2".equals(slope)){
									styleF2=setCellStyle(rowVo, styleF2);
									this.cell.setCellStyle(styleF2);
								}else if("3".equals(slope)){
									styleF3=setCellStyle(rowVo, styleF3);
									this.cell.setCellStyle(styleF3);
								}else if("4".equals(slope)){
									styleF4=setCellStyle(rowVo, styleF4);
									this.cell.setCellStyle(styleF4);
								}else if("5".equals(slope)){
									styleF5=setCellStyle(rowVo, styleF5);
									this.cell.setCellStyle(styleF5);
								}else{  
									styleF5=setCellStyle(rowVo, styleF5);
									this.cell.setCellStyle(styleF5);
								}
								if(context!=null&&context.trim().length()>0){
									boolean numFlag=true;
									try {
										new BigDecimal(context);
									} catch (Exception e) {
										numFlag=false;
									}
									if(numFlag) {
										double values = strToDouble(context);
										this.cell.setCellValue(values);
									}else {
										this.cell.setCellValue(context);
									}
								}else{
									 if("1".equals(zeroPrint)) {
										 cell.setCellValue(0);
									 }
									/*HSSFRichTextString textstr = new HSSFRichTextString(context);
									this.cell.setCellValue(textstr);*/
								}
							}else{
								HSSFCellStyle a_style=null;//wb.createCellStyle();
								if(styleMap.containsKey(j+"")) {
									a_style=styleMap.get(j+"");
								}else {
									a_style=this.wb.createCellStyle();
									styleMap.put(j+"",a_style);
								}
							    a_style.cloneStyleFrom(copyStyle);
							    if(!left) {
                                    a_style.setBorderLeft(BorderStyle.NONE);
                                }
							    if(!top) {
                                    a_style.setBorderTop(BorderStyle.NONE);
                                }
							    if(!right) {
                                    a_style.setBorderRight(BorderStyle.NONE);
                                }
							    if(!bottom) {
                                    a_style.setBorderBottom(BorderStyle.NONE);
                                }
							    a_style.setWrapText(true);
							    	a_style=setCellStyle(rowVo, a_style);
							    this.cell.setCellStyle(a_style);
							    if(StringUtils.isNotEmpty(context)) {
							    	HSSFRichTextString textstr = new HSSFRichTextString(context);
							    	this.cell.setCellValue(textstr);
							    }
							}
						}
					}
					
				}
			}
			groupRowNum=xx;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void executeXTabDataArea()
	{
		try
		{
//			HSSFFont font = wb.createFont();
			XSSFFont font =setXContentFont();
			PhotoImgBo imageBo=new PhotoImgBo(this.conn);
//			font.setFontHeightInPoints((short)9);
//			font.setFontName("宋体");
			this.xstyle_r.setFont(font);
			this.xstyle.setFont(font);
			this.xstyle_ccc.setFont(font);
			XSSFCellStyle styletext = styleX(this.xWb,1);
			styletext.setWrapText(true);
			XSSFDataFormat df0=(XSSFDataFormat)this.xWb.createDataFormat();
			styletext.setDataFormat(df0.getFormat("text"));
//			styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			//styletext.setAlignment(HorizontalAlignment.LEFT );
			
			XSSFCellStyle styleN = styleX(this.xWb,1);
			//styleN.setAlignment(HorizontalAlignment.RIGHT );
			styleN.setWrapText(true);
			XSSFDataFormat df=(XSSFDataFormat)this.xWb.createDataFormat();
//			HSSFDataFormat df = this.wb.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));
			
			XSSFCellStyle styleF1 = styleX(this.xWb,1);
			//styleF1.setAlignment(HorizontalAlignment.RIGHT );
			styleF1.setWrapText(true);
			XSSFDataFormat df1 = (XSSFDataFormat)this.xWb.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
			
			XSSFCellStyle styleF2 = styleX(this.xWb,1);
			//styleF2.setAlignment(HorizontalAlignment.RIGHT );
			styleF2.setWrapText(true);
			XSSFDataFormat df2 = (XSSFDataFormat)this.xWb.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
			
			XSSFCellStyle styleF3 = styleX(this.xWb,1);
			//styleF3.setAlignment(HorizontalAlignment.RIGHT );
			styleF3.setWrapText(true);
			XSSFDataFormat df3 = (XSSFDataFormat)this.xWb.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
			
			XSSFCellStyle styleF4 = styleX(this.xWb,1);
			//styleF4.setAlignment(HorizontalAlignment.RIGHT );
			styleF4.setWrapText(true);
			XSSFDataFormat df4 =(XSSFDataFormat) this.xWb.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
			
			XSSFCellStyle styleF5 = styleX(this.xWb,1);
			//styleF5.setAlignment(HorizontalAlignment.RIGHT );
			styleF5.setWrapText(true);
			XSSFDataFormat df5 = (XSSFDataFormat)this.xWb.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
			
			XSSFCellStyle copyStyle=(XSSFCellStyle)xWb.createCellStyle();
			
			int xx=0;
			boolean canMerge=false;  // 合并格有问题，暂时禁止
			String groupv="";
			String h_r = pageHeight+"";
			int hrows = Integer.parseInt(h_r.substring(0,h_r.indexOf(".")))/rows;
			if(this.height==0) {
                this.height=(short)hrows;
            }
			if(resultList.size()>0)
			{
				//HSSFPatriarch patriarch=this.sheet.createDrawingPatriarch();
				HashMap<String, XSSFCellStyle> styleMap=new HashMap<String, XSSFCellStyle>();
				int jj=1;
				for(int i=0;i<resultList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)resultList.get(i);
					String _groupv=(String)abean.get("groupv");
					if(i==0)
					{
						groupv=(String)abean.get("groupv");
					}
					if (!this.isGroupField && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
						if(i==0||(_groupv!=null&&!groupv.equalsIgnoreCase(_groupv)))
						{
							this.xrow = xsheet.getRow(this.rowLayNum+this.topParamLayNum+i+xx);//xuj 升级poi改动
							if(this.xrow==null) {
                                this.xrow = xsheet.createRow(this.rowLayNum+this.topParamLayNum+i+xx);
                            }

							this.xcell = xrow.createCell(Short.parseShort(String.valueOf(0)));
							this.xcell.setCellValue(_groupv);
							xx++;
							groupv=_groupv;
						}
							/*if(i!=0){//设置组内记录数
									LazyDynaBean lastBean=(LazyDynaBean)resultList.get(i-1);
								if(groupv.equals((String)lastBean.get("groupv"))){
									jj++;
								}else{
									this.row=sheet.getRow(this.rowLayNum+this.topParamLayNum+i-jj);
									this.cell=row.createCell(Short.parseShort(String.valueOf(0)));
									this.cell.setCellValue((String)lastBean.get("groupv")+jj);
									jj=1;
								}
								if(i==resultList.size()-1){//最后一个分组记录数
									this.row=sheet.getRow(this.rowLayNum+this.topParamLayNum+xx+i-jj);
									this.cell=row.createCell(Short.parseShort(String.valueOf(0)));
									this.cell.setCellValue((String)lastBean.get("groupv")+jj);
									jj=1;
								}
						}*/
					}
					
					LazyDynaBean abean1= null;
					if(i>0) {
                        abean1=(LazyDynaBean)resultList.get(i-1);
                    }
					
					for(int j=0;j<this.rowInfoBGrid.size();j++)
					{
															
						LazyDynaBean rowVo=(LazyDynaBean)this.rowInfoBGrid.get(j);
						String context="";	
						String nhide = (String)rowVo.get("nhide");
						if(nhide!=null&& "1".equals(nhide)) {
                            continue;
                        }
						String gridno=(String)rowVo.get("gridno");
						String field_type=(String)rowVo.get("field_type");
						String ColMerge=(String)rowVo.get("ColMerge");
						String ColMergeByMain=(String)rowVo.get("ColMergeByMain");
						//abean.set("RowMerge", RowMerge);
						field_type=field_type!=null?field_type:"A";
						String slope=(String)rowVo.get("slope");
						slope=slope!=null&&slope.trim().length()>0?slope:"0";
						String flag="H";
						if(rowVo.get("flag")!=null) {
                            flag=(String)rowVo.get("flag");
                        }
						if(canMerge&&ColMerge!=null&& "true".equalsIgnoreCase(ColMerge)){
							if(abean1!=null&&abean1.get(gridno).equals(abean.get(gridno))){
								if(ColMergeByMain!=null&& "true".equalsIgnoreCase(ColMergeByMain)){
									String mainvalue = "";
									String remainvalue = "";
									if(this.modelFlag!=null&& "3".equals(this.modelFlag)){
										mainvalue = (String)abean.get("a0100");
										remainvalue = (String)abean1.get("a0100");
									}else if(this.modelFlag!=null&& "21".equals(this.modelFlag)){
										mainvalue = (String)abean.get("b0110");
										remainvalue = (String)abean1.get("b0110");
									}else if(this.modelFlag!=null&& "41".equals(this.modelFlag)){
										mainvalue = (String)abean.get("e01a1");
										remainvalue = (String)abean1.get("e01a1");
									}
									if(mainvalue.equals(remainvalue)){
										ExportExcelUtil.mergeCell(xsheet, this.rowLayNum+this.topParamLayNum+i-1+xx,
												this.rowLayNum+this.topParamLayNum+i+xx,(short)j,(short)j);
									}
								}else{
									ExportExcelUtil.mergeCell(xsheet, this.rowLayNum+this.topParamLayNum+i-1+xx,
											this.rowLayNum+this.topParamLayNum+i+xx,(short)j,(short)j);
								}
							}
						}
						//this.row = sheet.createRow(this.rowLayNum+this.topParamLayNum+i);
						this.xrow = xsheet.getRow(this.rowLayNum+this.topParamLayNum+i+xx);//xuj 升级poi改动
						if(this.xrow==null) {
                            this.xrow = xsheet.createRow(this.rowLayNum+this.topParamLayNum+i+xx);
                        }
                        //wangcq 2014-12-08 获取单元格边框
						boolean left = "1".equals(rowVo.get("l"));
						boolean top = "1".equals(rowVo.get("t"));
						boolean right = "1".equals(rowVo.get("r"));
						boolean bottom = "1".equals(rowVo.get("b"));
						
						this.xcell = xrow.createCell(Short.parseShort(String.valueOf(j)));
						if("P".equalsIgnoreCase(flag))
						{
							this.xrow.setHeight((short)1800);
							//this.cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						    this.xcell.setCellStyle(styletext);
							this.xcell.setCellValue(context);
							if(abean.get("a0100")!=null&&((String)abean.get("a0100")).trim().length()>0)
							{	 
								 ClientAnchor  anchor = new XSSFClientAnchor( 0,0,512,255,Short.parseShort(String.valueOf(j)),this.rowLayNum+this.topParamLayNum+i+xx,(short)(Integer.parseInt(String.valueOf(j))+1),this.rowLayNum+this.topParamLayNum+i+xx+1);
//								 ClientAnchor  anchor = new XSSFClientAnchor( 0,0,512,255,(short)0,1,(short)1,2);  //列 行 列 行

								 anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
								 ArrayList list=createPhotoFile((String)abean.get("a0100"),"P",(String)abean.get("nbase"));
								 byte[] b=(byte[])list.get(0);
									 if(b!=null&&b.length>102400){//大于100k 压缩图片23484 网易：设计高级花名册插入照片时，不显示照片
										 b=imageBo.getlowPir(b);
									 }
								 String ext=(String)list.get(1);
								 if(b!=null&&(".JPG".equalsIgnoreCase(ext)|| ".bmp".equalsIgnoreCase(ext)|| ".PNG".equalsIgnoreCase(ext))){
									
									 xpatriarch.createPicture(anchor, this.xWb.addPicture(b, HSSFWorkbook.PICTURE_TYPE_JPEG));
								 }
							}
						}else{
							
							if(this.isHasPhoto) {
                                this.xrow.setHeight((short)1800);
                            } else {
                                this.xrow.setHeight((short)hrows);//this.row.setHeight(this.height);
                            }
							//this.cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							context=(String)abean.get(gridno);	
							/**
							 * 0 左上、 6 左中、3 左下、
							 * 1中上、  7 中中、4 中下、 
							 * 2 右上、 8 右中、5 右下、
								设置单元格格式  rowVo  align 位置
								ALIGN_CENTER
								ALIGN_LEFT
								ALIGN_RIGHT
							 * */
							//String align=(String)rowVo.get("align");
							if("N".equalsIgnoreCase(field_type)|| "S".equalsIgnoreCase(flag)|| "R".equalsIgnoreCase(flag)){
								if("0".equals(slope)){
									styleN=setXCellStyle(rowVo,styleN);
									this.xcell.setCellStyle(styleN);
								}else if("1".equals(slope)){
									styleF1=setXCellStyle(rowVo, styleF1);
									this.xcell.setCellStyle(styleF1);
								}else if("2".equals(slope)){
									styleF2=setXCellStyle(rowVo, styleF2);
									this.xcell.setCellStyle(styleF2);
								}else if("3".equals(slope)){
									styleF3=setXCellStyle(rowVo, styleF3);
									this.xcell.setCellStyle(styleF3);
								}else if("4".equals(slope)){
									styleF4=setXCellStyle(rowVo, styleF4);
									this.xcell.setCellStyle(styleF4);
								}else if("5".equals(slope)){
									styleF5=setXCellStyle(rowVo, styleF5);
									this.xcell.setCellStyle(styleF5);
								}else{
									styleF5=setXCellStyle(rowVo, styleF5);
									this.xcell.setCellStyle(styleF5);
								}
								if(context!=null&&context.trim().length()>0){
									if(!ResourceFactory.getProperty("hmuster.label.pageCount").equals(context)&&!"合计".equals(context)&&!"总计".equals(context)) {
										double values = strToDouble(context);
										if(values!=0) {
                                            this.xcell.setCellValue(values);
                                        } else{
											if("1".equals(zeroPrint)) {
												this.xcell.setCellValue(0);//设置打印0 
											}
										}
									}else {//非数值类型处理
										this.xcell.setCellValue(context);
									}
								}else{
									this.xcell.setCellValue(0);//设置打印0 
									/*XSSFRichTextString textstr=new XSSFRichTextString(context);
									this.xcell.setCellValue(textstr);*/
								}
							}else{
								XSSFCellStyle a_style=null;
								if(styleMap.containsKey(j+"")) {
									a_style=styleMap.get(j+"");
								}else {
									a_style=(XSSFCellStyle) this.xWb.createCellStyle();
									styleMap.put(j+"",a_style);	
								}
							    a_style.cloneStyleFrom(styletext);
							    a_style.setFont(font);
							    if(!left) {
                                    a_style.setBorderLeft(BorderStyle.NONE);
                                }
							    if(!top) {
                                    a_style.setBorderTop(BorderStyle.NONE);
                                }
							    if(!right) {
                                    a_style.setBorderRight(BorderStyle.NONE);
                                }
							    if(!bottom) {
                                    a_style.setBorderBottom(BorderStyle.NONE);
                                }
							    a_style.setWrapText(true);
							    a_style=setXCellStyle(rowVo, a_style);
							    this.xcell.setCellStyle(a_style);
							    if(StringUtils.isNotEmpty(context)) {
							    	XSSFRichTextString textstr=new XSSFRichTextString(context);
							    	this.xcell.setCellValue(textstr);
							    }
							}
						}
					}
					
				}
			}
			groupRowNum=xx;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public double strToDouble(String str){
		double values = 0;
		try{
			values = Double.parseDouble(str);
		}catch(Exception e){
			values = 0;
		}
		return values;
	}
	public String decimalwidth(int len){
		StringBuffer decimal= new StringBuffer("0");
		if(len>0) {
            decimal.append(".");
        }
		for(int i=0;i<len;i++){
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}
	/**
     * 设置excel表格效果
     * @param styles 设置不同的效果
     * @param workbook 新建的表格
     */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		
		
		switch(styles){
		case 0://this.style.getFont(workbook).getFontHeightInPoints() 获取模板设置字体大小 30280 广西桂林旅游股份薪资表导出字体不按设置的大小输出
				HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),this.style.getFont(workbook).getFontHeightInPoints());
				fonttitle.setBold(true);//加粗 
				style.setFont(fonttitle);
				style.setAlignment(HorizontalAlignment.LEFT );
		        break;			
		case 1:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.style.getFont(workbook).getFontHeightInPoints()));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.style.getFont(workbook).getFontHeightInPoints()));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.style.getFont(workbook).getFontHeightInPoints()));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);               	
				break;		
		case 4:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.style.getFont(workbook).getFontHeightInPoints()));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			  break;
		default:		
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.style.getFont(workbook).getFontHeightInPoints()));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	  
				break;
		}
		return style;
	}
	
	public XSSFCellStyle styleX(Workbook workbook,int styles){
		CellStyle style = workbook.createCellStyle();
		
		
		switch(styles){
		case 0://this.style.getFont(workbook).getFontHeightInPoints() 获取模板设置字体大小 30280 广西桂林旅游股份薪资表导出字体不按设置的大小输出
				XSSFFont fonttitle = fontXs(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),this.xstyle.getFont().getFontHeightInPoints());
				fonttitle.setBold(true);//加粗
				style.setFont(fonttitle);
				style.setAlignment(HorizontalAlignment.LEFT );
		        break;			
		case 1:
				style.setFont(fontXs(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.xstyle.getFont().getFontHeightInPoints()));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setFont(fontXs(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.xstyle.getFont().getFontHeightInPoints()));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				style.setFont(fontXs(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.xstyle.getFont().getFontHeightInPoints()));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);               	
				break;		
		case 4:
				style.setFont(fontXs(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.xstyle.getFont().getFontHeightInPoints()));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			  break;
		default:		
				style.setFont(fontXs(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),this.xstyle.getFont().getFontHeightInPoints()));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	  
				break;
		}
		return (XSSFCellStyle)style;
	}
	
	/**
     * 设置excel字体效果
     * @param fonts 设置不同的字体
     * @param size 设置字体的大小
     * @param workbook 新建的表格
     */
	public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size){
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
	public XSSFFont fontXs(Workbook workbook,String fonts,int size){
		XSSFFont font = (XSSFFont)workbook.createFont();
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
	
	/**
	 * 根据人员库前缀和人员编码生成其对应的文件
	 * 
	 * @param userTable
	 *            应用库 usra01
	 * @param userNumber
	 *            0000001 ,a0100
	 * @param flag
	 *            'P'照片
	 * @param session
	 * @return
	 * @throws Exception
	 */
	private ArrayList createPhotoFile(String userNumber,
			String flag, String nbase) throws Exception {
		ArrayList list=new ArrayList();
		byte[] bytes=null;
		if(nbase==null) {
            nbase=dbpre;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String      ext="";
		InputStream in=null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select ext,Ole,fileid from ");
			strsql.append(nbase+"A00");
			strsql.append(" where A0100='");
			strsql.append(userNumber);
			strsql.append("' and Flag='");
			strsql.append(flag);
			strsql.append("'");
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {
				String fileid=rowSet.getString("fileid");
				if(StringUtils.isNotEmpty(fileid)) {
					in = VfsService.getFile(fileid);
				}else {
					in=rowSet.getBinaryStream("Ole");
				}
				ext=rowSet.getString("ext");
				
				ByteArrayOutputStream outStream=new ByteArrayOutputStream();
				int len;
				byte buf[] = new byte[1024];
				while ((len = in.read(buf, 0, 1024)) != -1) {
					outStream.write(buf, 0, len);
				}
				bytes=outStream.toByteArray();
				outStream.close();
				in.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}  finally
        {
            PubFunc.closeIoResource(in);
        }
		list.add(bytes);
		list.add(ext);
		
		return list;
	}
	
	
	
	
	
	
	
	public ArrayList getNullBean(int n)
	{
		ArrayList list=new ArrayList();
		LazyDynaBean abean=null;
		for(int i=0;i<n;i++)
		{
			abean=new LazyDynaBean();
			for (Iterator t = this.gridList.iterator(); t.hasNext();) {
				RecordVo temp = (RecordVo) t.next();
				abean.set(temp.getString("gridno"),"");
			}
			list.add(abean);
		}
		return list;
	}
	private boolean isGroupField=false;
	/**
	 * 取得表结果数据
	 * @return
	 */
	public ArrayList getResultList(){
	ArrayList list = new ArrayList();
	ArrayList copyList = new ArrayList();
	RowSet rowSet = null;
	try {
		totalRowNum = 0;
		boolean isGroupTerm = false; // 是表内条件否有分组指标
		/* 查询语句 */
		ArrayList aa_list = getSql(isGroupTerm);
		String[] sql_temp = ((String) aa_list.get(0)).split("/");
		if ("1".equals((String) aa_list.get(1))) {
            isGroupTerm = true;
        }
		String sql = sql_temp[0];
		if ("1".equals(sql_temp[1])) {
            isPhoto = true;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		isGroupField = isGroupTerm;
		LazyDynaBean abean = null;
		LazyDynaBean fzabean = null;
		int a_i = 0; // 序号
		int ii = 0; // 分组显示的序号
		String groupV = "n";
		String groupN = "n";
		String groupV2 = "n";
		String groupN2 = "n";
		HashMap addMap = intAddDataHashMap(); // 合计信息

		String macth = "[0-9]+(.[0-9]+)?";
		BigDecimal zero = new BigDecimal("0");
		String oldgroupV = "";
		// 分组 ii每组从0开始 changxy 20161101 GROUPFIELD GROUPFIELD2
		HmusterXML hmuxml = new HmusterXML(this.conn, tabid);
		String GROUPFIELD = hmuxml.getValue(HmusterXML.GROUPFIELD);
		String GROUPFIELD2 = hmuxml.getValue(HmusterXML.GROUPFIELD2);
		String groupedSerials = hmuxml.getValue(HmusterXML.GROUPEDSERIALS);
		boolean flags = false;
		if ("1".equals(groupedSerials) && (!("".equals(GROUPFIELD) || GROUPFIELD == null)
				|| !("".equals(GROUPFIELD2) || GROUPFIELD2 == null))) {
            flags = true;
        }
		// 导出时目前不存在分页的情况 所以去除页小计 页累计
		this.isyxj = false;
		this.isylj = false;
		if (sql.indexOf("order") > -1) {
			rowSet = dao.search("select count(*) maxRow from (" + sql.substring(0, sql.indexOf("order")) + ") AA");
		} else {
			rowSet = dao.search("select count(*) maxRow from (" + sql + ") AA");
		}
		int rowCount = 0;
		if (rowSet.next()) {
			rowCount = rowSet.getInt("maxRow");
		}

		if (sql.indexOf("order") > -1) {
			rowSet = dao.search("select * from (" + sql.substring(0, sql.indexOf("order")) + ") AA where 1=2");
		} else {
			rowSet = dao.search("select * from (" + sql + ") AA where 1=2 ");
		}
		ResultSetMetaData meData = rowSet.getMetaData();
		int colCount = meData.getColumnCount();
		String[] column = new String[colCount];
		for (int i = 1; i <= colCount; i++) {
			column[i - 1] = meData.getColumnName(i);
		}
		if (rowCount * colCount - 3500000 > 0) {
			throw GeneralExceptionHandler.Handle(new Exception("数据量过大，已超过系统限制的350万单元格数据，请分批次导出！"));
		}
		String orderSql = "";
		if (sql.indexOf("order") > -1) {
			orderSql = sql.substring(sql.indexOf("order"));
			sql = sql.substring(0, sql.indexOf("order"));
		}

		PaginationManager paginationm = new PaginationManager(sql, "", "", orderSql, column, "");
		paginationm.setBAllMemo(true);
		paginationm.setPagerows(2000);
		// paginationm.setKeylist(splitKeys(tableCache.getIndexkey()));

		int pageIndex = 0;
		do {
			ArrayList dataList = (ArrayList) paginationm.getPage(pageIndex + 1);
			DynaBean dataBean = null;
			for (int i = 0; i < dataList.size(); i++) {
				dataBean = (DynaBean) dataList.get(i);
				abean = new LazyDynaBean();
				abean.set("groupv",
						(groupPoint == null || groupPoint.length() == 0
								|| dataBean.get("groupv").toString() == null) ? ""
										: dataBean.get("groupv").toString());
				if (this.isfzhj && ((this.isGroupPoint2 != null && "1".equals(this.isGroupPoint2))
						|| (this.isGroupPoint != null && "1".equals(this.isGroupPoint)))) {
					if (!oldgroupV.equalsIgnoreCase(
							dataBean.get("groupv") == null ? "" : dataBean.get("groupv").toString())) {
						fzhj(copyList, fzabean, list); // 取消分组合计，
						copyList = new ArrayList();
					}
					oldgroupV = dataBean.get("groupv").toString() == null ? "" : dataBean.get("groupv").toString();
				}
				if (!isGroupTerm || isGroupPoint != null && "1".equals(isGroupPoint)) {// 分组合计 两个分组指标
					// 之前是设置第二个分组指标后导出才会有合计
					// 现在改为第一个或第二个分组指标存在显示合计
					if (this.isGroupV2 && !this.isGroupTerm2 && this.isGroupPoint2 != null
							&& "1".equals(this.isGroupPoint2) && (this.opt == 0 || this.opt == 1)) {
						String temp = "";
						if (dataBean.get("groupn2").toString() != null) {
                            temp = "".equals(dataBean.get("groupn2").toString()) ? " "
                                    : dataBean.get("groupn2").toString();
                        }
						// 插入分组指标二时 会记录页小计 目前逻辑是分组指标二跟上一个数据不同时会记录页小计，分组指标一与上记录不同时记录分组合计
						if (!groupN2.equals(temp)) {
							/*
							 * if (!groupN2.equals("n")) { list.addAll(getCountBean(addMap));// 页小计 计算
							 * setAddDataHashMap(addMap, false, false, false, false, true); }
							 */
							groupN2 = temp;
						}
					}
					String tempGroupN = " ";
					if (dataBean.get("groupn") != null) {
                        tempGroupN = "".equals(dataBean.get("groupn").toString()) ? " "
                                : dataBean.get("groupn").toString();
                    }
					// 当判断当前分组指标与上一个分组指标不相同时 将addmap计算的分组合计 等值写入bean中
					if (!groupN.equals(tempGroupN) && ("1".equals(isGroupedSerials) || "0".equals(isGroupNoPage)
							|| "1".equals(isGroupNoPage))) {// 按组显示序号或者分组分页
						if (!"n".equals(groupN)) {
							if (this.opt == 0 || this.opt == 1) {
								if ("1".equals(emptyRow) && "0".equals(isGroupNoPage)) {
									list.addAll(getNullBean(rows - ii));
								}
								//list.addAll(getCountBean(addMap, false, false));
							}
//							if(isGroupNoPage.equals("0"))
//								list.addAll(getNullBean(5));
							if(this.opt==0||this.opt==1) {
                                setAddDataHashMap(addMap,true,false,true,false,true);
                            }
						}
						// 分组时 每组开始序号从1开始 不分组时序号递增 changxy
						if (flags) // changxy
                        {
                            ii = 0;// liuy 2015-3-24
                        }
						// 8202：主页/花名册/劳务人员收入花名册，共32条记录，打印预演及输出PDF序号都是对的，但是输出excel，序号到16后又从1开如重新排序了，不对
						groupN = tempGroupN;
						if (dataBean.get("groupv") != null) {
                            groupV = dataBean.get("groupv").toString();
                        } else {
                            groupV = " ";
                        }
					}
					ii++;
				}
				else
				{
					
					if (totalRowNum!=0&&totalRowNum%rows == 0) {
						if(this.opt==0||this.opt==1) {
                            list.addAll(getCountBean(addMap,false,isGroupTerm));
                        }
//						list.addAll(getNullBean(5));
						if(this.opt==0||this.opt==1) {
                            setAddDataHashMap(addMap,true,false,false,false,true);
                        }
					}
					
				}
				
				
				if (("1".equals(infor_Flag) || "stipend".equals(infor_Flag) || "salary".equals(infor_Flag))
						&& !isGroupTerm) {
					if ("1".equals(infor_Flag)) {
						try {
							abean.set("nbase", dataBean.get("nbase").toString());
						} catch (Exception e) {
						}
					}
					try {
						abean.set("a0100", dataBean.get("a0100").toString());
					} catch (Exception e) {
					}
				} else if ("2".equals(infor_Flag) && !isGroupTerm) {
                    abean.set("b0110", dataBean.get("b0110").toString());
                } else if ("3".equals(infor_Flag) && !isGroupTerm) {
                    abean.set("e01a1", dataBean.get("e01a1").toString());
                }
				if (this.opt == 0 || this.opt == 1) {
					countAddMap(addMap, dataBean); // 计算合计
				}
				/** 我的薪酬花名册，数值全为空或者零时，该行数据不显示 */
				int zeroCount = 0;// 我的薪酬花名册，判断值为空或者为零的总列数
				int columnCount = 0;// 我的薪酬花名册，画出的总列数
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String context = "";
					if ("S".equals(temp.getString("flag"))) {
						if (!isGroupTerm && isGroupPoint != null && "1".equals(isGroupPoint)) {
							context = String.valueOf(ii);
						} else {
                            context = String.valueOf(a_i + 1);
                        }
					} else if (temp.getString("flag") == null || "".equals(temp.getString("flag"))) {
						context = "";
					} else if ("H".equals(temp.getString("flag"))) {
						if (this.opt == 0) {
                            context = "";
                        } else {
                            context = temp.getString("hz");
                        }
					} else if (!isGroupTerm && "P".equals(temp.getString("flag"))) {
						context = "";
					} else {
						if (isGroupTerm && "G".equals(temp.getString("flag")) && isGroupPoint != null
								&& "1".equals(isGroupPoint)) {
                            context = dataBean.get("groupv").toString();
                        } else if (isGroupTerm) {
							if (temp.getString("field_name").equalsIgnoreCase(groupPoint) && isGroupPoint != null
									&& "1".equals(isGroupPoint)) {
								context = dataBean.get("groupv").toString();
							} else {
								if ("E".equals(temp.getString("flag")) && this.isGroupV2) {
									context = dataBean.get("groupv2").toString();

								} else if ("E".equals(temp.getString("flag"))) {
									context = "";
								} else {
									context = dataBean.get("c" + temp.getString("gridno")).toString();
								}
							}
						} else {
							if ("E".equals(temp.getString("flag")) && this.isGroupV2) {
								context = dataBean.get("groupv2").toString();
							} else if ("E".equals(temp.getString("flag"))) {
								context = "";
							} else {
								try {
									context = dataBean.get("c" + temp.getString("gridno")).toString();
								} catch (Exception e) {
								}
							}
						}
						if (context == null) {
							context = "";
							if ((zeroPrint != null && "1".equals(zeroPrint))
									&& "N".equals(temp.getString("field_type"))) {
                                context = "0";
                            }
							
						}
						
						if (context != null && !"".equals(context) && "N".equals(temp.getString("field_type"))) {
							float f = Float.parseFloat(context);
							if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
							{
								if (f == 0) {
                                    context = "";
                                } else {
									context = round(context, temp.getInt("slope"));
								}
								
							} else {
								context = round(context, temp.getInt("slope"));
							}
							
						}
					}
					if (temp.getInt("nhide") == 1) {
                        context = "";
                    }
					if (!(hasFieldReadPriv(temp.getString("field_name"), temp.getString("setname")))) {
                        context = "";
                    }
					abean.set(temp.getString("gridno"), context);
					columnCount++;
					if ("stipend".equals(modelFlag)) {
						if ("S".equals(temp.getString("flag")) || "P".equals(temp.getString("flag"))
								|| (temp.getString("field_name") != null
										&& temp.getString("field_name").trim().length() > 0
										&& (temp.getString("field_name").toUpperCase().endsWith("Z0")
												|| temp.getString("field_name").toUpperCase().endsWith("Z1")
												|| "A0101".equalsIgnoreCase(temp.getString("field_name"))
												|| "b0110".equalsIgnoreCase(temp.getString("field_name"))
												|| "e0122".equalsIgnoreCase(temp.getString("field_name"))
												|| "e01a1".equalsIgnoreCase(temp.getString("field_name"))))) {
							zeroCount++;
						} else {
							if ("".equals(context) || "&nbsp;".equalsIgnoreCase(context)) {
								zeroCount++;
							} else if (context.matches(macth)) {
								BigDecimal tempBD = new BigDecimal(context);
								if (zero.compareTo(tempBD) == 0) {
                                    zeroCount++;
                                }
							}
						}
					}
				}
				if ("stipend".equals(modelFlag)) {
					if (columnCount != zeroCount) {
						a_i++;
						totalRowNum++;
						list.add(abean);
						copyList.add(abean);
					}
				} else {
					a_i++;
					totalRowNum++;
					list.add(abean);
					copyList.add(abean);
				}
				/* zgd 2014-7-26 对导出的Excel进行分组总计 最后一条 */
				if (this.isfzhj && (this.isGroupPoint2 != null && "1".equals(this.isGroupPoint2)
						|| this.isGroupPoint != null && "1".equals(this.isGroupPoint))) {
					if (i == dataList.size() - 1
							&& (pageIndex == Math.ceil((double) rowCount / (double) 500) - 1)) {
						// 合计 最后一条计入合计
					//	if (!isGroupNoPage.equals("1"))
							list.addAll(getCountBean(addMap, false, false));// 合计 计算
					}
				}
			}

			pageIndex++;
		} while (pageIndex < Math.ceil((double) rowCount / (double) 2000));

		if (this.opt == 0 || this.opt == 1) {
			if (isGroupTerm || isGroupTerm2
					|| ("stipend".equalsIgnoreCase(infor_Flag) && "1".equals(this.groupCount))) {
                calcZj(addMap); // 重新计算总计
            }
			this.isfzhj = false;
			if ("1".equals(isGroupNoPage)) {
				list.addAll(getCountBean(addMap, true, isGroupTerm));
				// if(emptyRow.equals("1")){
				// list.addAll(getNullBean(rows-ii));
				// }
			} else {
				// if(emptyRow.equals("1")){
				// list.addAll(getNullBean(rows-ii));
				// }
				list.addAll(getCountBean(addMap, true, isGroupTerm));
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		PubFunc.closeDbObj(rowSet);
	}

	return list;}
	
	/**
	 * 用户对该指标无权限
	 * @author liuy
	 * @param field_name
	 * @param setname
	 * @return
	 */
	private boolean hasFieldReadPriv(String field_name, String setname){
		boolean flag = true;
		if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&field_name!=null&&field_name.trim().length()>0)
		{
			FieldItem fielditem = DataDictionary.getFieldItem(field_name);
			if("5".equals(this.modelFlag)&&!setname.toUpperCase().startsWith("V_EMP_")) {
				field_name=field_name.replaceAll("_1", "").replaceAll("_2","");
			}
			if(fielditem!=null){
				if(!"nbase".equalsIgnoreCase(field_name)&&!"A0100".equalsIgnoreCase(field_name)&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(field_name)))
				{
					if(setname!=null&&setname.toUpperCase().startsWith("V_EMP_"))
					{
						
					}
					else
					{
						flag = false;
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * 分组合计
	 * @param list 
	 * @param fzabean 
	 * @param copyList 
	 */
	private void fzhj(ArrayList copyList, LazyDynaBean fzabean, ArrayList list){
		try {
			if(this.addInfoMap.size()>0){
				if(copyList.size()>0){
					fzabean=new LazyDynaBean();
					int index=0;//多层表头情况下记录数据行第一列标记 方便写入合计信息
					for (Iterator t = this.gridList.iterator(); t.hasNext();) {
						RecordVo temp = (RecordVo) t.next();
						String gridno=temp.getString("gridno");
						//分组分页去除只插入文本的行（多层表头情况下）
						if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&
						        this.getHmusterViewBo().getTextFormatMap().get(gridno)==null&& "1".equals(this.column)&& "1".equals(this.dataarea))
						{
							continue;
						}
						index++;
						LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
						String field_type=temp.getString("field_type");
						String flag=temp.getString("flag");
						String extendattr=temp.getString("extendattr").toUpperCase();
						String groupSum="0";  // 0求和/1平均/2最大/3最小
						if(extendattr!=null&&extendattr.indexOf("GROUPSUM")!=-1){  //合计行指标区分计算是求和还是求平均最大或最小
							  boolean RoundBeforeAggregate = true;
					            if(extendattr.indexOf("ROUNDBEFOREAGGREGATE")!=-1) {
                                    RoundBeforeAggregate = "1".equals(extendattr.substring(extendattr.indexOf("<ROUNDBEFOREAGGREGATE>")+
                                            "<ROUNDBEFOREAGGREGATE>".length(),extendattr.indexOf("</ROUNDBEFOREAGGREGATE>")));
                                }
					            groupSum= extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
						}
						double valueDouble = 0.0;
						if(Integer.parseInt(gridno) == 1||index==1){
							fzabean.set(gridno, "合计");
						}else{
							if(a_bean!=null){
								if (("N".equals(field_type) || "R".equals(flag)) && "1".equals((String)a_bean.get("fzhj"))) {
									for(int i=0;i<copyList.size();i++){
										LazyDynaBean fzabean1=(LazyDynaBean)copyList.get(i);
										//if(fzabean1.get(gridno)!=null&&!"".equalsIgnoreCase(fzabean1.get(gridno).toString())){为空的数值下面处理为0 为了计算最大最小值处理
										if(i==0&&("2".equals(groupSum)|| "3".equals(groupSum))){
											String tempValue=((LazyDynaBean)copyList.get(0)).get(gridno).toString().trim();
											valueDouble=Double.parseDouble((tempValue==null||"".equals(tempValue))?"0":tempValue);
										}
										String temp1Value=fzabean1.get(gridno).toString().trim();
										temp1Value=(temp1Value==null||"".equals(temp1Value))?"0":temp1Value;
											if("0".equals(groupSum)|| "1".equals(groupSum)) {
                                                valueDouble += Double.parseDouble(temp1Value);
                                            } else if("2".equals(groupSum)){
												if(valueDouble<(Double.parseDouble(temp1Value))) {
                                                    valueDouble=Double.parseDouble(temp1Value);
                                                }
											}else if("3".equals(groupSum)){
												if(valueDouble>Double.parseDouble(temp1Value)) {
                                                    valueDouble = Double.parseDouble(temp1Value);
                                                }
											}
										//}
									}
									if(valueDouble==0.0){
										fzabean.set(gridno, "");
									}else{
										fzabean.set(gridno,("1".equals(groupSum)? String.valueOf(valueDouble/copyList.size()):String.valueOf(valueDouble)));
									}
								}else{
									fzabean.set(gridno, "");
								}
							}else{
								fzabean.set(gridno, "");
							}
						}
					}
					list.add(fzabean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 计算总计
	 */
	private void calcZj(HashMap addMap) {
	    try{
            HmusterViewBo hmusterViewBo=new HmusterViewBo(conn,tabid);
            hmusterViewBo.setModelFlag(modelFlag);
            hmusterViewBo.setUserView(userView);
            hmusterViewBo.setIsGroupPoint(isGroupPoint);
            hmusterViewBo.setGroupPoint(groupPoint);
            hmusterViewBo.setDataarea(dataarea);
            hmusterViewBo.setIsGroupPoint2(isGroupPoint2);
            hmusterViewBo.setGroupPoint2(groupPoint2);
            hmusterViewBo.setColumn(column);
            hmusterViewBo.setInfor_Flag(infor_Flag);
            hmusterViewBo.setPrintGrind(printGrid);
            hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabid));
            hmusterViewBo.setGroupV2(hmusterViewBo.isHaveGroup2(tableName));
            hmusterViewBo.setGroupCount(groupCount);
            if(hmusterViewBo.getTextFormatMap().size()>0)
            {
                if("1".equals(column)&& "1".equals(dataarea)) {
                    hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabid));
                }
            }
    	    ArrayList aList = hmusterViewBo.getBottomnList(tabid);
    	    ArrayList<String[]> bottomnList = (ArrayList) aList.get(0);  // 取得表头最底端的列
    	    hmusterViewBo.getFieldandFieldsList((ArrayList) aList.get(2));  // 哪些列有页小计或累计
    	    String[] zjfields = hmusterViewBo.getZj(); 
            double[] zjCount = hmusterViewBo.initCount(zjfields);
            zjCount = hmusterViewBo.totalCountsZj(zjCount, zjfields, tableName, bottomnList);
            if(zjfields!=null) {
            	int scale=0;
            	for (int b = 0; b < zjfields.length; b++) {
            		String gridno = zjfields[b].replaceFirst("C", "");
            		for(String[] fields:bottomnList) {
            			if(fields[0].equals(gridno)) {
            				scale=Integer.parseInt(fields[6]);
            			}
            		}
            		LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
            		aa_bean.set("zj", round(String.valueOf(zjCount[b]),scale));
            	}
            }
	    }catch(Exception e){}
	}
	
	/**
	 * 日期纠正格式
	 * @param str
	 * @return
	 */
	public String strToDate(String str){
		String dateStr="";
		String year="";
		String month="";
		String day = "";
		char arr[] = str.toCharArray();
		int n=0;
		for(int i=0;i<arr.length;i++){
			if(n==0){
				if(arr[i]>47&&arr[i]<58){
					year+=arr[i];
				}else{
					n=1;
				}
			}else if(n==1){
				if(arr[i]>47&&arr[i]<58){
					month+=arr[i];
				}else{
					n=2;
				}
			}else if(n==2){
				if(arr[i]>47&&arr[i]<58){
					day+=arr[i];
				}
			}
		}
		if(year.length()>3){
			dateStr+=year;
			dateStr+="-"+month;
			dateStr+="-"+day;
		}else if(day.length()>3){
			dateStr+=day;
			dateStr+="-"+month;
			dateStr+="-"+year;
		}else if(month.length()>3){
			dateStr+=month;
			dateStr+="-"+day;
			dateStr+="-"+year;
		}else{
			dateStr = str;
		}
		return dateStr;
	}
	
	public ArrayList getCountBean(HashMap addMap,boolean flag)
	{
		ArrayList list=new ArrayList();
		if(this.addInfoMap.size()>0){
			if(this.isyxj&&!"1".equals(isGroupNoPage))
			{
				LazyDynaBean abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
						RecordVo temp = (RecordVo) t.next();
						String gridno=temp.getString("gridno");
						LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
						String field_type=temp.getString("field_type");
						String noperation=temp.getString("noperation");
						if("N".equals(field_type)&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("yxj")))
						{
							LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
							double value=Double.parseDouble((String)aa_bean.get("yxj"));
							if (zeroPrint!=null&& "1".equals(zeroPrint)) {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("yxj"));
                            } else
							{
								if(value==0d) {
                                    abean.set(temp.getString("gridno"),"");
                                } else {
                                    abean.set(temp.getString("gridno"),(String)aa_bean.get("yxj"));
                                }
							}
						}else{
//							if (temp.getString("flag").equals("S")) 
//								abean.set(temp.getString("gridno"),"小计");
//							else if(temp.getString("flag").equalsIgnoreCase("H"))
//								abean.set(temp.getString("gridno"),"");   //temp.getString("hz"));
//							else
//								abean.set(temp.getString("gridno"),"");
							if(i==0){
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
							}else{
								abean.set(temp.getString("gridno"),"");
							}
						}
						i++;
				}
				list.add(abean);
			}
			if(this.isylj&&!"1".equals(isGroupNoPage))
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					if("N".equals(field_type)&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("ylj")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
//						abean.set(temp.getString("gridno"),(String)aa_bean.get("ylj"));
						double value=Double.parseDouble((String)aa_bean.get("ylj"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("ylj"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("ylj"));
                            }
						}
						
						
					}
					else
					{
						if(i==0){
							abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
						}else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
			if(this.isfzhj)//&&isGroupPoint!=null&&isGroupPoint.equals("1")  分组部分页与分组分页 页小计都显示
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					if("N".equals(field_type)&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("fzhj")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
						//abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
						double value=Double.parseDouble((String)aa_bean.get("fzhj"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
                            }
						}
						
						
					}
					else
					{
						if(i==0){
							abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
						}else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
			if(this.iszj&&flag)
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					if("N".equals(field_type)&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("zj")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
						//abean.set(temp.getString("gridno"),(String)aa_bean.get("zj"));
						double value=Double.parseDouble((String)aa_bean.get("zj"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("zj"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("zj"));
                            }
						}
					}
					else
					{
						if(i==0){
							abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
						}else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
		}
		return list;
	}
	public ArrayList getCountBean(HashMap addMap)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(this.addInfoMap.size()>0)
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					String flag_N=temp.getString("flag");
					flag_N=flag_N!=null?flag_N:"";
					String field_name=temp.getString("field_name");
					String field_hz=temp.getString("field_hz");
					if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&
					        this.getHmusterViewBo().getTextFormatMap().get(gridno)==null&& "1".equals(this.column)&& "1".equals(this.dataarea))
					{
						continue;
					}
					if(("N".equals(field_type)|| "R".equalsIgnoreCase(flag_N))&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("fzhj2")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
						//abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
						double value=Double.parseDouble((String)aa_bean.get("fzhj2"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj2"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj2"));
                            }
						}
						
						
					}
					else
					{
						if(this.resourceCloumn==1)
						{
					    	if("S".equalsIgnoreCase(flag_N)){
						    	abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
					    	}
					    	else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==2)
						{
							if("G".equalsIgnoreCase(flag_N))
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
							}
							else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==3)
						{
							if("a0101".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==4)
						{
							if("b0110".equalsIgnoreCase(field_name)|| "e0122".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==5)
						{
							if("e01a1".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==6)
						{
							if("姓名".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==7)
						{
							if("部门名称".equalsIgnoreCase(field_hz)|| "单位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==8)
						{
							if("岗位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(i==0&&!"N".equalsIgnoreCase(field_type)) {
                            abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                        } else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getCountBean(HashMap addMap,boolean flag,boolean isGrouptemp)
	{
		ArrayList list=new ArrayList();
		if(this.addInfoMap.size()>0){
			if(this.isyxj)  // &&!isGroupNoPage.equals("1")
			{
				LazyDynaBean abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
						RecordVo temp = (RecordVo) t.next();
						String gridno=temp.getString("gridno");
						LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
						String field_type=temp.getString("field_type");
						String noperation=temp.getString("noperation");
						String flag_N=temp.getString("flag");
						flag_N=flag_N!=null?flag_N:"";
						String field_name=temp.getString("field_name");
						String setname=temp.getString("setname");//liuy 2014-12-20 用于权限判断
						String field_hz=temp.getString("field_hz");
						if(("N".equals(field_type)|| "R".equalsIgnoreCase(flag_N))&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("yxj")))
						{
							LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
							double value=Double.parseDouble((String)aa_bean.get("yxj"));
							//liuy 2014-12-20 6107：HCM7.0：登录用户“吴金金”，员工名册“企业员工月度工资发放台帐”，显示时小计没有值，输出excel不对 start
							String context = "";
							if(hasFieldReadPriv(field_name, setname)) {
                                context = (String)aa_bean.get("yxj");
                            }
							//liuy end
							 
							 
							if (zeroPrint!=null&& "1".equals(zeroPrint)) {
                                abean.set(temp.getString("gridno"),context);
                            } else
							{
								if(value==0d) {
                                    abean.set(temp.getString("gridno"),"");
                                } else {
                                    abean.set(temp.getString("gridno"),context);
                                }
							}
						}else{
							if (temp.getString("flag")==null|| "".equals(temp.getString("flag"))|| "H".equals(temp.getString("flag"))) {
								if(this.opt==0) {
                                    abean.set(temp.getString("gridno"),"");
                                } else {
                                    abean.set(temp.getString("gridno"),temp.getString("hz"));
                                }
							}
							else if(this.resourceCloumn==1)
							{
						    	if("S".equalsIgnoreCase(flag_N)){
							    	abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
						    	}
						    	else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}else if(this.resourceCloumn==2)
							{
								if("1".equals(isGroupPoint)&& "G".equalsIgnoreCase(flag_N))  // 分组一
								{
									abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
								}
								else if("1".equals(isGroupPoint2)&& "E".equalsIgnoreCase(flag_N))  // 分组二
								{
									abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
								}
								else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}else if(this.resourceCloumn==3)
							{
								if("a0101".equalsIgnoreCase(field_name)) {
                                    abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                                } else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}
							else if(this.resourceCloumn==4)
							{
								if("b0110".equalsIgnoreCase(field_name)|| "e0122".equalsIgnoreCase(field_name)) {
                                    abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                                } else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}
							else if(this.resourceCloumn==5)
							{
								if("e01a1".equalsIgnoreCase(field_name)) {
                                    abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                                } else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}
							else if(this.resourceCloumn==6)
							{
								if("姓名".equalsIgnoreCase(field_hz)) {
                                    abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                                } else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}else if(this.resourceCloumn==7)
							{
								if("部门名称".equalsIgnoreCase(field_hz)|| "单位名称".equalsIgnoreCase(field_hz)) {
                                    abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                                } else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}
							else if(this.resourceCloumn==8)
							{
								if("岗位名称".equalsIgnoreCase(field_hz)) {
                                    abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                                } else {
                                    abean.set(temp.getString("gridno"),"");
                                }
							}
							else if(i==0&&!"N".equalsIgnoreCase(field_type)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.pageCount"));
                            } else{
								abean.set(temp.getString("gridno"),"");
							}
						}
						i++;
				}
				list.add(abean);
			}
			if(this.isylj) // &&!isGroupNoPage.equals("1")
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					String flag_N=temp.getString("flag");
					flag_N=flag_N!=null?flag_N:"";
					String field_name=temp.getString("field_name");
					String field_hz=temp.getString("field_hz");
					if(("N".equals(field_type)|| "R".equalsIgnoreCase(flag_N))&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("ylj")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
//						abean.set(temp.getString("gridno"),(String)aa_bean.get("ylj"));
						double value=Double.parseDouble((String)aa_bean.get("ylj"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("ylj"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("ylj"));
                            }
						}
						
						
					}
					else
					{
						if (temp.getString("flag")==null|| "".equals(temp.getString("flag"))|| "H".equals(temp.getString("flag"))) {
							if(this.opt==0) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),temp.getString("hz"));
                            }
						}
						else if(this.resourceCloumn==1)
						{
					    	if("S".equalsIgnoreCase(flag_N)){
						    	abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
					    	}
					    	else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==2)
						{
							if("1".equals(isGroupPoint)&& "G".equalsIgnoreCase(flag_N))  // 分组一
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
							}
							else if("1".equals(isGroupPoint2)&& "E".equalsIgnoreCase(flag_N))  // 分组二
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
							}
							else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==3)
						{
							if("a0101".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==4)
						{
							if("b0110".equalsIgnoreCase(field_name)|| "e0122".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==5)
						{
							if("e01a1".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==6)
						{
							if("姓名".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==7)
						{
							if("部门名称".equalsIgnoreCase(field_hz)|| "单位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==8)
						{
							if("岗位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(i==0&&!"N".equalsIgnoreCase(field_type)) {
                            abean.set(temp.getString("gridno"),ResourceFactory.getProperty("hmuster.label.toatlCount"));
                        } else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
			if(this.isfzhj&&isGroupPoint!=null&& "1".equals(isGroupPoint)&&!isGrouptemp)
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					String flag_N=temp.getString("flag");
					flag_N=flag_N!=null?flag_N:"";
					String field_name=temp.getString("field_name");
					String field_hz=temp.getString("field_hz");
					if(("N".equals(field_type)|| "R".equalsIgnoreCase(flag_N))&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("fzhj")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
						//abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
						double value=Double.parseDouble((String)aa_bean.get("fzhj"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("fzhj"));
                            }
						}
						
						
					}
					else
					{
						if (temp.getString("flag")==null|| "".equals(temp.getString("flag"))|| "H".equals(temp.getString("flag"))) {
							if(this.opt==0) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),temp.getString("hz"));
                            }
						}
						else if(this.resourceCloumn==1)
						{
					    	if("S".equalsIgnoreCase(flag_N)){
						    	abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
					    	}
					    	else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==2)
						{
							if("1".equals(isGroupPoint)&& "G".equalsIgnoreCase(flag_N))  // 分组一
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
							}
							else if("1".equals(isGroupPoint2)&& "E".equalsIgnoreCase(flag_N))  // 分组二
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
							}
							else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==3)
						{
							if("a0101".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==4)
						{
							if("b0110".equalsIgnoreCase(field_name)|| "e0122".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==5)
						{
							if("e01a1".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==6)
						{
							if("姓名".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==7)
						{
							if("部门名称".equalsIgnoreCase(field_hz)|| "单位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==8)
						{
							if("岗位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("planar.stat.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(i==0&&!"N".equalsIgnoreCase(field_type)) {
                            abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                        } else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
			if(this.iszj&&flag)
			{
				LazyDynaBean abean=abean=new LazyDynaBean();
				int i=0;
				for (Iterator t = this.gridList.iterator(); t.hasNext();) {
					RecordVo temp = (RecordVo) t.next();
					String gridno=temp.getString("gridno");
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(gridno);
					String field_type=temp.getString("field_type");
					String noperation=temp.getString("noperation");
					String flag_N=temp.getString("flag");
					flag_N=flag_N!=null?flag_N:"";
					String field_name=temp.getString("field_name");
					String field_hz=temp.getString("field_hz");
					if(("N".equals(field_type)|| "R".equalsIgnoreCase(flag_N))&&!"0".equals(noperation)&& "1".equals((String)a_bean.get("zj")))
					{
						LazyDynaBean aa_bean=(LazyDynaBean)addMap.get(gridno);
						//abean.set(temp.getString("gridno"),(String)aa_bean.get("zj"));
						double value=Double.parseDouble((String)aa_bean.get("zj"));
						if ("1".equals(zeroPrint)) {
                            abean.set(temp.getString("gridno"),(String)aa_bean.get("zj"));
                        } else
						{
							if(value==0d) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),(String)aa_bean.get("zj"));
                            }
						}
					}
					else
					{
						
						if (temp.getString("flag")==null|| "".equals(temp.getString("flag"))|| "H".equals(temp.getString("flag"))) {
							if(this.opt==0) {
                                abean.set(temp.getString("gridno"),"");
                            } else {
                                abean.set(temp.getString("gridno"),temp.getString("hz"));
                            }
						}
						else if(this.resourceCloumn==1)
						{
					    	if("S".equalsIgnoreCase(flag_N)){
						    	abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
					    	}
					    	else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==2)
						{
							if("1".equals(isGroupPoint)&& "G".equalsIgnoreCase(flag_N))  // 分组一
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
							}
							else if("1".equals(isGroupPoint2)&& "E".equalsIgnoreCase(flag_N))  // 分组二
							{
								abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
							}
							else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==3)
						{
							if("a0101".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==4)
						{
							if("b0110".equalsIgnoreCase(field_name)|| "e0122".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==5)
						{
							if("e01a1".equalsIgnoreCase(field_name)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==6)
						{
							if("姓名".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}else if(this.resourceCloumn==7)
						{
							if("部门名称".equalsIgnoreCase(field_hz)|| "单位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(this.resourceCloumn==8)
						{
							if("岗位名称".equalsIgnoreCase(field_hz)) {
                                abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                            } else {
                                abean.set(temp.getString("gridno"),"");
                            }
						}
						else if(i==0&&!"N".equalsIgnoreCase(field_type)) {
                            abean.set(temp.getString("gridno"),ResourceFactory.getProperty("workdiary.message.total"));
                        } else{
							abean.set(temp.getString("gridno"),"");
						}
					}
					i++;
				}
				list.add(abean);
			}
		}
		return list;
	}
	public void countAddMap(HashMap addMap, DynaBean bean) {
		try {
			if (addMap.size() > 0) {
				for (Iterator t = addMap.keySet().iterator(); t.hasNext();) {
					String key = (String) t.next();
					LazyDynaBean abean = (LazyDynaBean) addMap.get(key);
					LazyDynaBean a_bean = (LazyDynaBean) this.addInfoMap.get(key);
					String slope = (String) a_bean.get("slope"); // (小数位数);

					String value = null;
					try {
						value = bean.get("c" + key).toString();
					} catch (Exception e) {
					}
					if (StringUtils.isEmpty(value)) {
                        value = "0";
                    }
					String yxj = PubFunc.add((String) abean.get("yxj"), value, Integer.parseInt(slope));
					String ylj = PubFunc.add((String) abean.get("ylj"), value, Integer.parseInt(slope));
					String fzhj = PubFunc.add((String) abean.get("fzhj"), value, Integer.parseInt(slope));
					String zj = PubFunc.add((String) abean.get("zj"), value, Integer.parseInt(slope));
					String fzhj2 = PubFunc.add((String) abean.get("fzhj2"), value, Integer.parseInt(slope));
					abean.set("yxj", yxj);
					abean.set("ylj", ylj);
					abean.set("fzhj", fzhj);
					abean.set("fzhj2", fzhj2);
					abean.set("zj", zj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void countAddMap(HashMap addMap,RowSet rowSet)
	{
		try
		{
			if(addMap.size()>0)
			{
				for(Iterator t=addMap.keySet().iterator();t.hasNext();)
				{
					String key=(String)t.next();
					LazyDynaBean abean=(LazyDynaBean)addMap.get(key);
					LazyDynaBean a_bean=(LazyDynaBean)this.addInfoMap.get(key);
					String slope=(String)a_bean.get("slope"); //(小数位数);
					
					String value=null;
					try{
					    value = rowSet.getString("C"+key);
					}catch(Exception e) {};
					if(value==null) {
                        value="0";
                    }
					
					String yxj=PubFunc.add((String)abean.get("yxj"),value,Integer.parseInt(slope));
					String ylj=PubFunc.add((String)abean.get("ylj"),value,Integer.parseInt(slope));
					String fzhj=PubFunc.add((String)abean.get("fzhj"),value,Integer.parseInt(slope));
					String zj=PubFunc.add((String)abean.get("zj"),value,Integer.parseInt(slope));
					String fzhj2=PubFunc.add((String)abean.get("fzhj2"),value,Integer.parseInt(slope));
					abean.set("yxj", yxj);
					abean.set("ylj", ylj);
					abean.set("fzhj", fzhj);
					abean.set("fzhj2", fzhj2);
					abean.set("zj", zj);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setAddDataHashMap(HashMap addMap,boolean yxj,boolean ylj,boolean fzhj,boolean zj,boolean fzhj2)
	{
		for(Iterator t=this.addInfoMap.keySet().iterator();t.hasNext();)
		{
			String key=(String)t.next();
			LazyDynaBean abean=(LazyDynaBean)addMap.get(key);
			if(yxj) {
                abean.set("yxj","0");
            }
			if(ylj) {
                abean.set("ylj","0");
            }
			if(fzhj)
			{
				abean.set("fzhj","0");
			}
			if(fzhj2)
			{
				abean.set("fzhj2","0");
			}
			if(zj) {
                abean.set("zj","0");
            }
		}
		
	}
	
	public HashMap intAddDataHashMap()
	{
		HashMap a_map=new HashMap();
		if(this.addInfoMap.size()>0)
		{
			for(Iterator t=this.addInfoMap.keySet().iterator();t.hasNext();)
			{
				String key=(String)t.next();
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("yxj","0");
				abean.set("ylj","0");
				abean.set("fzhj","0");
				abean.set("zj","0");
				abean.set("fzhj2","0");
				a_map.put(key,abean);
			}
		}
		return a_map;
	}
	
	
	
		/**
		 * 提供精确的小数位四舍五入处理。
		 * 
		 * @param v
		 *            需要四舍五入的数字
		 * @param scale
		 *            小数点后保留几位
		 * @return 四舍五入后的结果
		 */
		public String round(String v, int scale) {

			if (scale < 0) {
				throw new IllegalArgumentException(
						"The scale must be a positive integer or zero");
			}
			BigDecimal b = new BigDecimal(v);
			BigDecimal one = new BigDecimal("1");
			return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
		}
		/**
		 * 提供精确的小数位四舍五入处理。
		 * 
		 * @param v
		 *            需要四舍五入的数字
		 * @param scale
		 *            小数点后保留几位
		 * @return 四舍五入后的结果
		 */
		public double roundNum(String v, int scale) {

			if (scale < 0) {
				throw new IllegalArgumentException(
						"The scale must be a positive integer or zero");
			}
			BigDecimal b = new BigDecimal(v);
			BigDecimal one = new BigDecimal("1");
			return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		}

		/**
		 * 根据薪资花名册排序要求 修改sql
		 * @param sql
		 * @return
		 */
		public String getInserSql(String sql,String tableName)
		{
			String temp=sql.substring(0,sql.indexOf(tableName)+tableName.length());
			temp+=",dbname";
			sql=temp+sql.substring(sql.indexOf(tableName)+tableName.length());
			String order="";
			
			if(sql.indexOf("order by")!=-1)
			{
				order=sql.substring(sql.indexOf("order by"));
				sql=sql.substring(0,sql.indexOf("order by"));
			}
			
			if(sql.indexOf("where")!=-1)
			{
				temp=sql.substring(0,sql.indexOf("where")+5);
				temp+=" "+tableName+".nbase=dbname.pre ";
				
				sql=temp+" and "+sql.substring(sql.indexOf("where")+5);
			}
			else {
                sql+=" where "+tableName+".nbase=dbname.pre ";
            }
			
			if(order.length()>0) {
                sql+=" "+order+",dbname.dbid,a0000,A00Z0,A00Z1";
            } else {
                sql+=" order by dbname.dbid,a0000, A00Z0, A00Z1";
            }
			return sql;
		}	
		
		
	private ArrayList getSql(boolean isGroupTerm)
	{
		StringBuffer h_sql = new StringBuffer("select ");
		StringBuffer h_sql_ext = new StringBuffer("");
		String isPhoto="0";
		for (Iterator t =this.gridList.iterator(); t.hasNext();) {
			RecordVo temp = (RecordVo) t.next();
			if (temp.getString("flag")!= null && ("G".equals(temp.getString("flag"))|| "R".equals(temp.getString("flag")))) {
				isGroupTerm = true;
			}
			if(temp.getString("flag")!= null && ("E".equals(temp.getString("flag")))) {
                this.isGroupTerm2=true;
            }
			if (temp.getString("flag")!= null && !"S".equals(temp.getString("flag"))&&!"E".equals(temp.getString("flag"))
					&& !"G".equals(temp.getString("flag")) && !"H".equals(temp.getString("flag"))
					&& !"P".equals(temp.getString("flag")) && !"R".equals(temp.getString("flag"))
					&& !"".equals(temp.getString("flag"))) {
				h_sql_ext.append(",C" + temp.getInt("gridno"));
			}
			if ("P".equals(temp.getString("flag"))) {
                isPhoto ="1";
            }
		}

		h_sql_ext.append(",GroupV,GroupN ");
		if(this.isGroupV2)
		{
	    	if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupTerm&&isGroupPoint != null && "1".equals(isGroupPoint))
	    	{
	        	h_sql_ext.append(",GroupV2,GroupN2");
	        	
	    	}
	    	else if(isGroupTerm) {
                h_sql_ext.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
            } else {
                h_sql_ext.append(",GroupV2, GroupN2");
            }
		}
		if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "salary".equals(infor_Flag)) {
            h_sql_ext.append(",A0100");
        } else if ("2".equals(infor_Flag)) {
            h_sql_ext.append(",B0110");
        } else if ("3".equals(infor_Flag)) {
            h_sql_ext.append(",E01A1");
        }
		String orderby="";
		if (isGroupPoint != null && "1".equals(isGroupPoint)) {
			HmusterXML hmxml = new HmusterXML(this.conn,tabid);
			String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
			GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
			String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
			GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
			if(GROUPFIELD.trim().length()>4){
				FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
				if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){
					h_sql_ext.append(",(select A0000 from organization where codeitemid=");
					h_sql_ext.append(tableName);
					h_sql_ext.append(".GroupN) AS A0000 ");
					if (!isGroupTerm && !isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
						    orderby=" group by "+Sql_switcher.month("a00z0");	
						}
						else
						{
			    			if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
			    			{
					    		if(GROUPFIELD2.trim().length()>4)
					    		{
					    			FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
					    			if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
					    				h_sql_ext.append(",(select A0000 from organization where codeitemid=");
						    			h_sql_ext.append(tableName);
						    			h_sql_ext.append(".GroupN2) AS A00002 ");
						    			orderby=" order by A0000,A00002,recidx";
						    		}else
						    		{
							    		orderby=" order by A0000,GroupN2,recidx";
							    	}
					    		}
					    		else
					    		{
					    			orderby=" order by A0000,GroupN2,recidx";
					    		}
				    		}
				    		else
				    		{
				    			orderby=" order by A0000,recidx";
				    		}
						}
					} else if ((isGroupTerm || isGroupTerm2) && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by A0000 ";
							}
							else
							{
						    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by A0000 ";
							}
						}
						else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by A0000 ";
							}
							else
							{
					        	orderby=" group by GroupN,GroupV order by A0000 ";
							}
						}
					}
					
				}else{
					if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
						    orderby=" group by "+Sql_switcher.month("a00z0");	
						}
						else
						{
			    			if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
				    		{
				    			if(GROUPFIELD2.trim().length()>4)
				    			{
					    			FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
						    		if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
							    		h_sql_ext.append(",(select A0000 from organization where codeitemid=");
							    		h_sql_ext.append(tableName);
							       		h_sql_ext.append(".GroupN2) AS A00002 ");
							    		orderby=" order by GroupN,A00002,recidx";
							    	}else
						    		{
						      			orderby=" order by GroupN,GroupN2,recidx";
						    		}
					    		}
					    		else
				    			{
					    			orderby=" order by GroupN,GroupN2,recidx";
				    			}
				    		}else {
                                orderby=" order by GroupN,recidx";
                            }
						}
					} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}else
							{
						    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}
						}else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
							}
							else
							{
					        	orderby=" group by GroupN,GroupV order by GroupN ";
							}
						}
					}
				}
			}else{
				if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
					{
					    orderby=" group by "+Sql_switcher.month("a00z0");	
					}
					else
					{
			    		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
			    		{
			    			if(GROUPFIELD2.trim().length()>4)
				    		{
					    		FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
					    		if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
						    		h_sql_ext.append(",(select A0000 from organization where codeitemid=");
					    			h_sql_ext.append(tableName);
						    		h_sql_ext.append(".GroupN2) AS A00002 ");
					    			orderby=" order by GroupN,A00002,recidx";
					    		}else
						    	{
						    		orderby=" order by GroupN,GroupN2,recidx";
					    		}
					    	}
					    	else
				    		{
				    			orderby=" order by GroupN,GroupN2,recidx";
				    		}
				    	}else {
                            orderby=" order by GroupN,recidx";
                        }
					}
				} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
					{
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
							orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
						}else
						{
					    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
						}
					}
					else{
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
							orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
						}else
						{
				        	orderby=" group by GroupN,GroupV order by GroupN ";
						}
					}
				}
			}
		}else{
			if (!isGroupTerm) {
                orderby=" order by recidx";
            }
			if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
			{
				orderby=" group by "+Sql_switcher.month("a00z0");
			}
		}
		
		h_sql_ext.append(" from " + tableName);

		if (isGroupTerm||("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
			h_sql_ext = new StringBuffer("");
			h_sql_ext.append(getGroupSql());
		}
		/* 权限控制 */
        h_sql_ext.append(this.privConditionStr);
        if("5".equals(this.modelFlag))
        {
        	if(this.privConditionStr.trim().length()>0)
        	{
        		if(this.getSql().trim().length()>0)
        		{
        			h_sql_ext.append(" and ("+this.getSql()+")");
        		}
        	}
        	else
        	{
        		if(this.getSql().trim().length()>0)
        		{
        			h_sql_ext.append(" where ("+this.getSql()+")");
        		}
        	}
        }
        if("stipend".equals(infor_Flag)&&this.a0100!=null&&this.a0100.length()>0)
		{
			if(h_sql_ext.toString().toLowerCase().substring(h_sql_ext.toString().toLowerCase().lastIndexOf("from")).indexOf("where")>0) {
                h_sql_ext.append(" and  A0100='"+this.a0100+"' ");
            } else {
                h_sql_ext.append(" where A0100='"+this.a0100+"'");
            }
		}
        //h_sql_ext.append(orderby);
		//h_sql.append(h_sql_ext.substring(1));
        
        // 用HmusterViewBo类得到sql
		HmusterViewBo hmusterViewBo=new HmusterViewBo(conn,tabid);
		hmusterViewBo.setModelFlag(modelFlag);
		hmusterViewBo.setUserView(userView);
		hmusterViewBo.setIsGroupPoint(isGroupPoint);
		hmusterViewBo.setGroupPoint(groupPoint);
		hmusterViewBo.setDataarea(dataarea);
		hmusterViewBo.setIsGroupPoint2(isGroupPoint2);
		hmusterViewBo.setGroupPoint2(groupPoint2);
		hmusterViewBo.setColumn(column);
		hmusterViewBo.setInfor_Flag(infor_Flag);
		hmusterViewBo.setPrintGrind(printGrid);
		hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabid));
		hmusterViewBo.setGroupV2(hmusterViewBo.isHaveGroup2(tableName));
		hmusterViewBo.setGroupCount(groupCount);
		if(hmusterViewBo.getTextFormatMap().size()>0)
		{
			if("1".equals(column)&& "1".equals(dataarea)) {
                hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabid));
            }
		}		
		h_sql.delete(0, h_sql.length());
		try{
		    String sql = hmusterViewBo.getMusterSqlAll(tableName);
			h_sql.append(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
		ArrayList list=new ArrayList();
		list.add(h_sql.toString()+"/"+isPhoto);
		list.add(isGroupTerm?"1":"0");
		return list;
		
	}
	
	
//	 取得包含分组指标的sql语句
	public String getGroupSql() {
		StringBuffer tempSql = new StringBuffer("");

		/* 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 */
		// GridNo,Hz,Rleft,RTop,RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,
		String a_temp = "";
		if ("1".equals(infor_Flag)|| "81".equals(this.modelFlag)|| "5".equals(this.modelFlag)|| "stipend".equalsIgnoreCase(infor_Flag)) {
            a_temp = "A0100";
        } else if ("2".equals(infor_Flag)) {
            a_temp = "B0110";
        } else if ("3".equals(infor_Flag)) {
            a_temp = "E01A1";
        }
		if("stipend".equalsIgnoreCase(infor_Flag)) {
            tempSql.append(",max(A0100) as a0100");
        }
		for (Iterator t = this.gridList.iterator(); t.hasNext();) {
			RecordVo temp = (RecordVo) t.next();
			String extendattr=temp.getString("extendattr").toUpperCase();
			if(extendattr.indexOf("GROUPSUM")!=-1&&(temp.getString("field_type")!=null&&("N".equalsIgnoreCase(temp.getString("field_type")))))
			{
				String groupSum = extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
				/**求和*/
				if("0".equals(groupSum))
				{/*
					if(infor_Flag.equalsIgnoreCase("salary"))
		       		{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				        	tempSql.append(",Sum(C"+temp[0]+") C" + temp[0]);
				    	else
			    			tempSql.append(",SUM(C"+temp[0]+") C" + temp[0]);
			    	}
			    	else
		    		    tempSql.append(",SUM(*) C" + temp[0]);*/
					tempSql.append(" ,sum(C"+temp.getInt("gridno")+") C"+temp.getInt("gridno"));
				}else if("1".equals(groupSum))
				{
					tempSql.append(" ,avg(C"+temp.getInt("gridno")+") C"+temp.getInt("gridno"));
				}
				else if("2".equals(groupSum))
				{
					tempSql.append(" ,max(C"+temp.getInt("gridno")+") C"+temp.getInt("gridno"));
				}
				else if("3".equals(groupSum))
				{
					tempSql.append(" ,min(C"+temp.getInt("gridno")+") C"+temp.getInt("gridno"));
				}
			}
			else{
	    		if (temp.getString("field_type")!= null && "N".equals(temp.getString("field_type"))) {
	    			if("stipend".equalsIgnoreCase(infor_Flag)&&temp.getString("field_name")!=null&&temp.getString("field_name").toUpperCase().endsWith("Z1"))
	    			{
	    				tempSql.append(",max(C");
		    	    	tempSql.append(temp.getInt("gridno") + ") C" + temp.getInt("gridno"));
	    			}
	    			else
	    			{
		    	    	tempSql.append(",sum(C");
		    	    	tempSql.append(temp.getInt("gridno") + ") C" + temp.getInt("gridno"));
	    			}
		    	} else if (temp.getString("flag")!= null && "R".equals(temp.getString("flag"))) {
		    		if("salary".equalsIgnoreCase(infor_Flag))
		       		{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            tempSql.append(",count(distinct nbase||a0100");
                        } else {
                            tempSql.append(",count(distinct nbase+a0100");
                        }
			    	}
			    	else {
                        tempSql.append(",count(*");
                    }
			    	tempSql.append(") as C" + temp.getInt("gridno"));
		    	} else if(temp.getString("flag")!=null&&!"S".equals(temp.getString("flag"))&& !"E".equals(temp.getString("flag"))&& !"G".equals(temp.getString("flag")) && !"H".equals(temp.getString("flag"))&& !"P".equals(temp.getString("flag")) && !"R".equals(temp.getString("flag"))&& !"".equals(temp.getString("flag")))
	    		{
 		    		tempSql.append(",max( C" + temp.getInt("gridno")+") as C"+temp.getInt("gridno"));
    			}
		    	else {
                    tempSql.append(",' ' C" + temp.getInt("gridno"));
                }
			}
		}
		
	    	if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupPoint != null && "1".equals(isGroupPoint))
	     	{
	            tempSql.append(",GroupV2,GroupN2");
	     	}
	    	else
	    	{
	    		if(this.isGroupV2) {
                    tempSql.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
                }
	    	}
		
		if (isGroupPoint != null && "1".equals(isGroupPoint)){
			tempSql.append(",GroupN,GroupV");
			HmusterXML hmxml = new HmusterXML(this.conn,tabid);
			String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
			GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
			if(GROUPFIELD.trim().length()>4){
				FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
				if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){

					tempSql.append(",(select A0000 from organization where codeitemid=");
					tempSql.append(tableName);
					tempSql.append(".GroupN) AS A0000 ");
				}
			}
		}else
		{
			tempSql.append(",max(GroupN) groupn,max(GroupV) groupv");
		}
		tempSql.append(" from ");
		tempSql.append(tableName);

		return tempSql.toString();
	}

	
	
	
	
	
	
	
//	取得权限控制语句
	public String getPrivCondition(String infor_Flag,String dbpre)
	{
		StringBuffer privConditionStr=new StringBuffer("");
		try
		{
			if (!userView.isSuper_admin()) {
				if ("1".equals(infor_Flag)&&!"ALL".equals(dbpre)) // 人员库
				{
					String conditionSql = " select "+dbpre+"A01.A0100 "+ userView.getPrivSQLExpression(dbpre, true);
					/**加入兼职人员*/
					if("true".equalsIgnoreCase(this.showPartJob))
					{
						HmusterBo bo = new HmusterBo(this.conn);
			    		String parttimerSQL =""; 
			    		 if(userView.getManagePrivCodeValue()!=null) {
                             parttimerSQL=bo.getQueryFromPartLike(userView, dbpre, userView.getManagePrivCodeValue());
                         }
				    	 if(parttimerSQL!=null&&!"".equals(parttimerSQL))
				    	 {
					    	 conditionSql+=" or ("+parttimerSQL+")";
				    	 }
					}
					privConditionStr.append(" where A0100 in (" + conditionSql + " )");
					
				}
				String codesetid=userView.getManagePrivCode();
				String codeValue=userView.getManagePrivCodeValue();
				if ("2".equals(infor_Flag)) // 2：机构
				{
					String conditionSql = " select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and  codeitemid like '"
						+ codeValue+"%'";
					privConditionStr.append(" where B0110 in (" + conditionSql + " )");
					
				}
				
				if ("3".equals(infor_Flag)) //  3：职位
				{
					String conditionSql = " select codeitemid from organization  where codesetid='@K' and  codeitemid like '"
						+ codeValue+"%'";
					privConditionStr.append(" where E01A1 in (" + conditionSql + " )");
				
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return privConditionStr.toString();
	}
	
	
	/**
	 * 
	 * @param a  起始 x坐标
	 * @param b	 起始 y坐标
	 * @param c	 终止 x坐标
	 * @param d  终止 y坐标
	 * @param content 内容
	 * @param style	  表格样式
	 * @param fontEffect 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
	 */
	 public void executeCell(int a,short b,int c,short d,String content,String style,HSSFFont font)
	 {
		 //HSSFRow row = sheet.createRow(a);
		 HSSFRow row = sheet.getRow(a);
			if(row==null) {
                row = sheet.createRow(a);
            }
		 
		 HSSFCell cell = row.createCell(b);
		// cell.setEncoding(HSSFCell.ENCODING_UTF_16);	
		 
		 if("c".equalsIgnoreCase(style)){
			 this.style.setFont(font);
			 this.style.setWrapText(true);
			 cell.setCellStyle(this.style); 
		 }else if("l".equalsIgnoreCase(style)){
			 this.style_l.setFont(font);
			 this.style_l.setWrapText(true);
			 cell.setCellStyle(this.style_l);
		 }else if("ccc".equalsIgnoreCase(style)){
			 this.style_ccc.setFont(font);
			 this.style_ccc.setWrapText(true);
			 cell.setCellStyle(this.style_ccc);
		 }else if("R".equalsIgnoreCase(style)){
			 this.style_r.setFont(font);
			 this.style_r.setWrapText(true);
			 cell.setCellStyle(this.style_r);
		 }else if("cc".equalsIgnoreCase(style)){
			 this.style_cc.setFont(font);
			 this.style_cc.setWrapText(true);
			 cell.setCellStyle(this.style_cc);
		 }else if("no_c".equalsIgnoreCase(style)){
			 HSSFCellStyle a_style=wb.createCellStyle();
			 a_style.setAlignment(HorizontalAlignment.CENTER);
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }else if("no_l".equalsIgnoreCase(style)){
			 HSSFCellStyle a_style=wb.createCellStyle();
			 a_style.setAlignment(HorizontalAlignment.LEFT);
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }
		 if(content.endsWith("`"))
		 {
			 content=content.substring(0,content.length()-1);
		 }
		 content=content.replaceAll("`","\r\n");
		 cell.setCellValue(content);
		 short b1=b;
		 while(++b1<=d)
		 {
			 cell = row.createCell(b1);
			 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                 cell.setCellStyle(this.style);
             }
		 }
		 for(int a1=a+1;a1<=c;a1++)
		 {
			 //row = sheet.createRow(a1);
			 row = sheet.getRow(a1);
				if(row==null) {
                    row = sheet.createRow(a1);
                }

			 b1=b;
			 while(b1<=d)
			 {
				 cell = row.createCell(b1);
				 if(!"".equals(style)) {
                     cell.setCellStyle(this.style);
                 }
				 b1++;
			 }
		 }
		 if(c>=a&&d>=b) {
             try {
                 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
             } catch (GeneralException e) {
                 e.printStackTrace();
             }
         }
	 }
	
	 public void executeXCell(int a,short b,int c,short d,String content,String style,XSSFFont font)
	 {
		 //HSSFRow row = sheet.createRow(a);
		 Row row = this.xsheet.getRow(a);
			if(row==null) {
                row = this.xsheet.createRow(a);
            }
		 
		 Cell cell =(XSSFCell)row.createCell(b);
		// cell.setEncoding(HSSFCell.ENCODING_UTF_16);	
		 cell.getStringCellValue();
		 if("c".equalsIgnoreCase(style)){
			 this.xstyle.setFont(font);
			 this.xstyle.setWrapText(true);
			 cell.setCellStyle(this.xstyle); 
		 }else if("l".equalsIgnoreCase(style)){
			 this.xstyle_l.setFont(font);
			 this.xstyle_l.setWrapText(true);
			 cell.setCellStyle(this.xstyle_l);
		 }else if("ccc".equalsIgnoreCase(style)){
			 this.xstyle_ccc.setFont(font);
			 this.xstyle_ccc.setWrapText(true);
			 cell.setCellStyle(this.xstyle_ccc);
		 }else if("R".equalsIgnoreCase(style)){
			 this.xstyle_r.setFont(font);
			 this.xstyle_r.setWrapText(true);
			 cell.setCellStyle(this.xstyle_r);
		 }else if("cc".equalsIgnoreCase(style)){
			 this.xstyle_cc.setFont(font);
			 this.xstyle_cc.setWrapText(true);
			 cell.setCellStyle(this.xstyle_cc);
		 }else if("no_c".equalsIgnoreCase(style)){
			 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
			 a_style.setAlignment(HorizontalAlignment.CENTER);
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }else if("no_l".equalsIgnoreCase(style)){
			 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
			 a_style.setAlignment(HorizontalAlignment.LEFT);
			 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			 a_style.setFont(font);
			 a_style.setWrapText(true);
			 cell.setCellStyle(a_style);
		 }
		 if(content.endsWith("`"))
		 {
			 content=content.substring(0,content.length()-1);
		 }
		 content=content.replaceAll("`","\r\n");
		 cell.setCellValue(content);
		 cell.getStringCellValue();
		 short b1=b;
		 while(++b1<=d)
		 {
			 cell = row.createCell(b1);
			 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                 cell.setCellStyle(this.style);
             }
		 }
		 for(int a1=a+1;a1<=c;a1++)
		 {
//			 row = sheet.createRow(a1);
			 row = this.xsheet.getRow(a1);
				if(row==null) {
                    row = this.xsheet.createRow(a1);
                }

			 b1=b;
			 while(b1<=d)
			 {
				 cell = row.createCell(b1);
				 if(!"".equals(style)) {
                     cell.setCellStyle(this.style);
                 }
				 b1++;
			 }
		 }
		 if(c>=a&&d>=b) {
             try {
                 ExportExcelUtil.mergeCell(xsheet, a,b,c,d);//poi升级后合并单元格有问题 不影响导出效果
             } catch (GeneralException e) {
                 e.printStackTrace();
             }
         }
	 }
	 
	 /**
		 * 
		 * @param a  起始 x坐标
		 * @param b	 起始 y坐标
		 * @param c	 终止 x坐标
		 * @param d  终止 y坐标
		 * @param content 内容
		 * @param style	  表格样式
		 * @param fontEffect 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
		 * @param ltrb 单元格样式
		 */
		 public void executeCell(int a,short b,int c,short d,String content,String style,HSSFFont font,String ltrb)
		 {
			 //HSSFRow row = sheet.createRow(a);
			 HSSFRow row = sheet.getRow(a);
				if(row==null) {
                    row = sheet.createRow(a);
                }
			 
			 HSSFCell cell = row.createCell(b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);	
			 boolean left = "1".equals(ltrb.substring(0, 1));
			 boolean top = "1".equals(ltrb.substring(1, 2));
			 boolean right = "1".equals(ltrb.substring(2, 3));
			 boolean bottom = "1".equals(ltrb.substring(3, 4));
			 
			 if("c".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.cloneStyleFrom(this.style);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("l".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.cloneStyleFrom(this.style_l);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("ccc".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.cloneStyleFrom(this.style_ccc);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("R".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.cloneStyleFrom(this.style_r);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("cc".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.cloneStyleFrom(this.style_cc);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("no_c".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("no_l".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.LEFT);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }
			 if(content.endsWith("`"))
			 {
				 content=content.substring(0,content.length()-1);
			 }
			 content=content.replaceAll("`","\r\n");
			 cell.setCellValue(content);
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.createCell(b1);
				 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                     cell.setCellStyle(this.style);
                 }
			 }
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 //row = sheet.createRow(a1);
				 row = sheet.getRow(a1);
					if(row==null) {
                        row = sheet.createRow(a1);
                    }

				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.createCell(b1);
					 if(!"".equals(style)) {
                         cell.setCellStyle(this.style);
                     }
					 b1++;
				 }
			 }
			 if(c>=a&&d>=b) {
                 try {
                     ExportExcelUtil.mergeCell(sheet, a,b,c,d);
                 } catch (GeneralException e) {
                     e.printStackTrace();
                 }
             }
		 }
	 
		 public void executeXCell(int a,short b,int c,short d,String content,String style,XSSFFont font,String ltrb)
		 {
			 //HSSFRow row = sheet.createRow(a);
			 Row row = xsheet.getRow(a);
				if(row==null) {
                    row = xsheet.createRow(a);
                }
			 
			 Cell cell = row.createCell(b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);	
			 boolean left = "1".equals(ltrb.substring(0, 1));
			 boolean top = "1".equals(ltrb.substring(1, 2));
			 boolean right = "1".equals(ltrb.substring(2, 3));
			 boolean bottom = "1".equals(ltrb.substring(3, 4));
			 
			 if("c".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.cloneStyleFrom(this.xstyle);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("l".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.cloneStyleFrom(this.xstyle_l);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("ccc".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.cloneStyleFrom(this.xstyle_ccc);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("R".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.cloneStyleFrom(this.xstyle_r);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("cc".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.cloneStyleFrom(this.xstyle_cc);
				 a_style.setFont(font);
				 if(!left) {
                     a_style.setBorderLeft(BorderStyle.NONE);
                 }
				 if(!top) {
                     a_style.setBorderTop(BorderStyle.NONE);
                 }
				 if(!right) {
                     a_style.setBorderRight(BorderStyle.NONE);
                 }
				 if(!bottom) {
                     a_style.setBorderBottom(BorderStyle.NONE);
                 }
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("no_c".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("no_l".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.LEFT);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }
			 if(content.endsWith("`"))
			 {
				 content=content.substring(0,content.length()-1);
			 }
			 content=content.replaceAll("`","\r\n");
			 cell.setCellValue(content);
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.createCell(b1);
				 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                     cell.setCellStyle(this.xstyle);
                 }
			 }
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 //row = sheet.createRow(a1);
				 row = xsheet.getRow(a1);
					if(row==null) {
                        row = xsheet.createRow(a1);
                    }

				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.createCell(b1);
					 if(!"".equals(style)) {
                         cell.setCellStyle(this.xstyle);
                     }
					 b1++;
				 }
			 }
			 if(c>a || d>b) {
                 xsheet.addMergedRegion(new CellRangeAddress(a,c,b,d));
             }
//				 xExportExcelUtil.mergeCell(sheet, a,b,c,d));
		 }
	 /**
		 * 
		 * @param a  起始 x坐标
		 * @param b	 起始 y坐标
		 * @param c	 终止 x坐标
		 * @param d  终止 y坐标
		 * @param content 内容
		 * @param style	  表格样式
		 * @param fontEffect 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
		 * @param field_type 字体类型
		 * @param slope	  小数点位数
		 */
		 public void executeCell(int a,short b,int c,short d,String content,
				 String style,HSSFFont font,String field_type,String slope)
		 { 
			 
			 //HSSFRow row = sheet.createRow(a);
			 HSSFRow row = sheet.getRow(a);
				if(row==null) {
                    row = sheet.createRow(a);
                }
			 HSSFCell cell = row.createCell(b);
			 if("c".equalsIgnoreCase(style)){
				 this.style.setFont(font);
				 this.style.setWrapText(true);
				 cell.setCellStyle(this.style); 
			 }else if("l".equalsIgnoreCase(style)){
				 this.style_l.setFont(font);
				 this.style_l.setWrapText(true);
				 cell.setCellStyle(this.style_l);
			 }else if("ccc".equalsIgnoreCase(style)){
				 this.style_ccc.setFont(font);
				 this.style_ccc.setWrapText(true);
				 cell.setCellStyle(this.style_ccc);
			 }else if("R".equalsIgnoreCase(style)){
				 this.style_r.setFont(font);
				 this.style_r.setWrapText(true);
				 cell.setCellStyle(this.style_r);
			 }else if("cc".equalsIgnoreCase(style)){
				 this.style_cc.setFont(font);
				 this.style_cc.setWrapText(true);
				 cell.setCellStyle(this.style_cc);
			 }else if("no_c".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("no_l".equalsIgnoreCase(style)){
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.LEFT);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }
			 HSSFCellStyle styleN=cell.getCellStyle();
			 if("N".equalsIgnoreCase(field_type)){
				 slope=(slope==null|| "".equals(slope))?"0":slope;
				 String tt=PubFunc.round("0.0000", Integer.parseInt(slope));
				 if(!"0".equals(slope))
				 {
					 styleN.setDataFormat(HSSFDataFormat.getBuiltinFormat(tt)); 
					 cell.setCellStyle(styleN); 
				 }
				 if(content!=null&&content.trim().length()>0){
					 double values = strToDouble(content);
					 if(values!=0)
					 {
						// cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						
						 cell.setCellValue(Double.parseDouble(content));
					 }
					 else{
						 //判断是否设置打印0  打印0 数值型为空的显示0
						 if("1".equals(zeroPrint)) {
							 cell.setCellValue(0);
						 }
						/* HSSFRichTextString textstr = new HSSFRichTextString(content);
						 cell.setCellValue(textstr);*/
					 }
				 }else{
					 if("1".equals(zeroPrint)) {
						 cell.setCellValue(0);
					 }
				 }
				 short b1=b;
				 while(++b1<=d)
				 {
					 cell = row.createCell(b1);
					 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                         cell.setCellStyle(styleN);
                     }
				 }
				 for(int a1=a+1;a1<=c;a1++)
				 {
					// row = sheet.createRow(a1);
					 row = sheet.getRow(a1);
						if(row==null) {
                            row = sheet.createRow(a1);
                        }

					 b1=b;
					 while(b1<=d)
					 {
						 cell = row.createCell(b1);
						 if(!"".equals(style)) {
                             cell.setCellStyle(styleN);
                         }
						 b1++;
					 }
				 }
			 }else{
				 content=content.replaceAll("`","\r\n");
				 HSSFRichTextString textstr = new HSSFRichTextString(content);
				 if(StringUtils.isNotEmpty(content)) {
                     cell.setCellValue(textstr);
                 }
				 short b1=b;
				 while(++b1<=d)
				 {
					 cell = row.createCell(b1);
					 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                         cell.setCellStyle(this.style);
                     }
				 }
				 for(int a1=a+1;a1<=c;a1++)
				 {
					// row = sheet.createRow(a1);
					 row = sheet.getRow(a1);
						if(row==null) {
                            row = sheet.createRow(a1);
                        }

					 b1=b;
					 while(b1<=d)
					 {
						 cell = row.createCell(b1);
						 if(!"".equals(style)) {
                             cell.setCellStyle(this.style);
                         }
						 b1++;
					 }
				 }
			 }
			 
			 try {
				ExportExcelUtil.mergeCell(sheet, a,b,c,d);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
		 }
		 
		 public void executeXCell(int a,short b,int c,short d,String content,
				 String style,XSSFFont font,String field_type,String slope)
		 { 
			 
			 //HSSFRow row = sheet.createRow(a);
			 Row row = xsheet.getRow(a);
				if(row==null) {
                    row = xsheet.createRow(a);
                }

			 Cell cell = row.createCell(b);
			 if("c".equalsIgnoreCase(style)){
				 this.xstyle.setFont(font);
				 this.xstyle.setWrapText(true);
				 cell.setCellStyle(this.xstyle); 
			 }else if("l".equalsIgnoreCase(style)){
				 this.xstyle_l.setFont(font);
				 this.xstyle_l.setWrapText(true);
				 cell.setCellStyle(this.xstyle_l);
			 }else if("ccc".equalsIgnoreCase(style)){
				 this.xstyle_ccc.setFont(font);
				 this.xstyle_ccc.setWrapText(true);
				 cell.setCellStyle(this.xstyle_ccc);
			 }else if("R".equalsIgnoreCase(style)){
				 this.xstyle_r.setFont(font);
				 this.xstyle_r.setWrapText(true);
				 cell.setCellStyle(this.xstyle_r);
			 }else if("cc".equalsIgnoreCase(style)){
				 this.xstyle_cc.setFont(font);
				 this.xstyle_cc.setWrapText(true);
				 cell.setCellStyle(this.xstyle_cc);
			 }else if("no_c".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }else if("no_l".equalsIgnoreCase(style)){
				 XSSFCellStyle a_style=(XSSFCellStyle)xWb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.LEFT);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setFont(font);
				 a_style.setWrapText(true);
				 cell.setCellStyle(a_style);
			 }
			 XSSFCellStyle styleN=(XSSFCellStyle)cell.getCellStyle();
			 if("N".equalsIgnoreCase(field_type)){
				 slope=(slope==null|| "".equals(slope))?"0":slope;
				 String tt=PubFunc.round("0.0000", Integer.parseInt(slope));
				 if(!"0".equals(slope))
				 {
					 XSSFDataFormat df0=(XSSFDataFormat)xWb.createDataFormat();
					 styleN.setDataFormat(df0.getFormat(tt)); 
					 cell.setCellStyle(styleN); 
				 }
				 if(content!=null&&content.trim().length()>0){
					 double values = strToDouble(content);
					 if(values!=0)
					 {
						// cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						
						 cell.setCellValue(Double.parseDouble(content));
					 }
					 else{
						 if("1".equals(zeroPrint)) {
							 cell.setCellValue(0);
						 }
						 /*XSSFRichTextString textstr = new XSSFRichTextString(content);
						 cell.setCellValue(textstr);*/
					 }
				 }else{
					 if("1".equals(zeroPrint)) {
						 cell.setCellValue(0);
					 }
					/* XSSFRichTextString textstr = new XSSFRichTextString(content);
					 cell.setCellValue(textstr);*/
				 }
				 short b1=b;
				 while(++b1<=d)
				 {
					 cell = row.createCell(b1);
					 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                         cell.setCellStyle(styleN);
                     }
				 }
				 for(int a1=a+1;a1<=c;a1++)
				 {
					// row = sheet.createRow(a1);
					 row = xsheet.getRow(a1);
						if(row==null) {
                            row = xsheet.createRow(a1);
                        }

					 b1=b;
					 while(b1<=d)
					 {
						 cell = row.createCell(b1);
						 if(!"".equals(style)) {
                             cell.setCellStyle(styleN);
                         }
						 b1++;
					 }
				 }
			 }else{
				 
				 content=content.replaceAll("`","\r\n");
				 if(StringUtils.isNotEmpty(content)) {//内容不为空时才写入 为空 给空单元格
					 XSSFRichTextString textstr = new XSSFRichTextString(content);
					 cell.setCellValue(textstr);
				 }
				 short b1=b;
				 while(++b1<=d)
				 {
					 cell = row.createCell(b1);
					 if(!"no_c".equals(style)&&!"no_l".equals(style)) {
                         cell.setCellStyle(this.xstyle);
                     }
				 }
				 for(int a1=a+1;a1<=c;a1++)
				 {
					// row = sheet.createRow(a1);
					 row = xsheet.getRow(a1);
						if(row==null) {
                            row = xsheet.createRow(a1);
                        }

					 b1=b;
					 while(b1<=d)
					 {
						 cell = row.createCell(b1);
						 if(!"".equals(style)) {
                             cell.setCellStyle(this.xstyle);
                         }
						 b1++;
					 }
				 }
			 }
			 
			 try {
				ExportExcelUtil.mergeCell(xsheet, a,b,c,d);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
		 }
	/** 设置表格样式 */
	public HSSFCellStyle getStyle(String align,HSSFWorkbook wb)
	{
		HSSFCellStyle a_style=wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
		//a_style.setWrapText( true );
		if("c".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("l".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.LEFT);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("r".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.RIGHT);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("cc".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("ccc".equals(align)){
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}
		return a_style;
	}
	
	/** 设置表格样式 */
	public XSSFCellStyle getXStyle(String align,Workbook wb)
	{

		XSSFCellStyle a_style=(XSSFCellStyle)wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
		//a_style.setWrapText( true );
		if("c".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("l".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.LEFT);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("r".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.RIGHT);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("cc".equals(align)){
			a_style.setWrapText( true );
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}else if("ccc".equals(align)){
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}
		return a_style;
	}
	
	
	
	
	
	
	
	
	/**
	 * 为横/纵 栏单元格 加入excel坐标
	 * @param infoList
	 */
	public void resetGridListInfo(ArrayList infoList)
	{
		LazyDynaBean abean = null;
		for (int i = 0; i < infoList.size(); i++) {
			abean = (LazyDynaBean) infoList.get(i);
			double rleft = Double.parseDouble((String) abean.get("rleft"));
			double rtop = Double.parseDouble((String) abean.get("rtop"));
			double rwidth = Double.parseDouble((String) abean.get("rwidth"));
			double rheight = Double.parseDouble((String) abean.get("rheight"));

			this.index_x = 0 + this.topParamLayNum;
			this.index_y =0;

			int row_x = Integer.parseInt((String) this.row_t_map.get(String.valueOf(rtop)));
			int row_y = Integer.parseInt((String) this.row_l_map.get(String.valueOf(rleft)));
			abean.set("from_x", String.valueOf(index_x + row_x - 1));
			abean.set("from_y", String.valueOf(index_y + row_y - 1));
			if (rtop + rheight == itemGridArea[1] + itemGridArea[3]) {
				abean.set("to_x", String.valueOf(index_x + rowLayNum - 1));
				if(rleft+rwidth==itemGridArea[0] + itemGridArea[2]) {
                    abean.set("to_y", String.valueOf(index_y + this.row_l_map.size() - 1));
                } else{
					//abean.set("to_y", String.valueOf(index_y + row_y - 1));  // 横行分栏时，表格最后一行单元格合并不对
					int row_t_y = Integer.parseInt((String) this.row_l_map.get(String.valueOf(rleft + rwidth)));
					abean.set("to_y", String.valueOf(index_y + row_t_y - 2));
				}
			} else {
				int childNum = getChildNum(abean);
				if (childNum== 0&&rtop + rheight == itemGridArea[1] + itemGridArea[3])
				{
					abean.set("to_x", String.valueOf(index_x + rowLayNum - 1));
					abean.set("to_y", String.valueOf(index_y + row_y - 1));
				}
				else
				{
					int row_t_x = Integer.parseInt((String) this.row_t_map.get(String.valueOf(rtop + rheight)));
					if (rleft + rwidth == this.tab_width) {
						abean.set("to_x", String.valueOf(index_x + row_t_x - 2));
						abean.set("to_y", String.valueOf(index_y+ this.row_l_map.size() - 1));
					} else {
						int row_t_y = Integer.parseInt((String) this.row_l_map.get(String.valueOf(rleft + rwidth)));
						abean.set("to_x", String.valueOf(index_x + row_t_x- 2));
						abean.set("to_y", String.valueOf(index_y + row_t_y- 2));
					}
				} 
			}

		}
	}
	
	
	/**
	 * 取得 某单元格下包括的子格个数
	 * @param vo
	 * @param flag 1: 横栏  2：纵栏
	 * @return
	 */
	public int getChildNum(LazyDynaBean vo)
	{
		int size=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql= new StringBuffer();
			sql.append("select count(*) from muster_cell where  Tabid=");
			sql.append(this.tabid);
			sql.append("   and RTop+RHeight=");
			sql.append(itemGridArea[1]+itemGridArea[3]);
			sql.append(" and RLeft>=");
			sql.append((String)vo.get("rleft"));
			sql.append(" and (RLeft+rWidth)<=");
			sql.append(Double.parseDouble((String)vo.get("rleft"))+Double.parseDouble((String)vo.get("rwidth")));
			sql.append(" and GridNo<>");
			sql.append((String)vo.get("gridno"));
			RowSet rowSet=dao.search(sql.toString());
			if(rowSet.next()) {
                size=rowSet.getInt(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return size;
	}
	
	
	/**
	 *  取得横纵栏的层数
	 * @return
	 */
	public HashMap getTabNumMap(String columnName)
	{
		HashMap map=new HashMap();
		try
		{
			int num=0;
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select "+columnName+" from muster_cell  where tabid="+this.tabid+"   group by "+columnName+" order by "+columnName;
			RowSet rowSet=dao.search(sql);
		    while(rowSet.next())
			{
				num++;
				double temp=rowSet.getDouble(1);
				map.put(String.valueOf(temp),String.valueOf(num));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/** 表格行数 */
	private int size=0;
	public HashMap getTopMap()
	{
		HashMap map = new HashMap();
		try
		{
			int num=1;
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select rtop from muster_cell  where tabid="+this.tabid+"   group by rtop order by rtop";
			RowSet rowSet=dao.search(sql);
			double tt=0;
			int index=0;
			double ce=5.00d;
		    while(rowSet.next())
			{
		    	if(index==0)
		    	{
		    		tt=rowSet.getDouble(1);
		    	}
		    	double temp=rowSet.getDouble(1);
		    	if(temp-tt>ce)
		    	{
		    		tt=rowSet.getDouble(1);
			    	num++;
		    	}
				map.put(String.valueOf(temp),String.valueOf(num));
				index++;
			}
		    this.size=num;
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/** 表格数据区行数 */
	private int hsize=0;
	public HashMap getTextFormatMap()
	{
		HashMap map=new HashMap();
		try
		{
			int num=1;
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql= new StringBuffer("");
			sql.append("select rtop from muster_cell where tabid="+this.tabid);
			
			if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap()!=null)
			{
				Set keySet = this.getHmusterViewBo().getTextFormatMap().keySet();
				StringBuffer buf = new StringBuffer("");
				Iterator t=keySet.iterator();
				while(t.hasNext())
				{
					buf.append(","+t.next());
				}
				if(buf.toString().length()>0)
				{
					sql.append(" and gridno not  in ("+buf.toString().substring(1)+")");
				}
			}
			sql.append("   group by rtop order by rtop");
			RowSet rowSet=dao.search(sql.toString());
			double tt=0;
			int index=0;
			double ce=5.00d;
		    while(rowSet.next())
			{
		    	if(index==0)
		    	{
		    		tt=rowSet.getDouble(1);
		    	}
		    	double temp=rowSet.getDouble(1);
		    	if(temp-tt>ce)
		    	{
		    		tt=rowSet.getDouble(1);
			    	num++;
		    	}
				map.put(String.valueOf(temp),String.valueOf(num));
				index++;
			}
		    this.hsize=num;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 调整尺寸
	 *
	 */
	public void resetSize(int temp,int temp1)
	{
		
		for(int i=0;i<this.rowHeightList.size();i++)
		{
			HSSFRow row = sheet.getRow(i+temp);
			if(row==null) {
                row = sheet.createRow(i+temp);
            }

			int  d=((Integer)rowHeightList.get(i)).intValue();
			d=d*15;
			row.setHeight(Short.parseShort(String.valueOf(d)));			
		}
		for(int i=0;i<this.columnWidthList.size();i++)
		{
			
			int  d=((Integer)this.columnWidthList.get(i)).intValue();
			d=d*38;
			if(this.sheet.getColumnWidth(Integer.parseInt(String.valueOf(i+temp1)))<d) {
                this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i+temp1)),Short.parseShort(String.valueOf(d)));
            }
			
		}
	}
	
	public void resetXSize(int temp,int temp1)
	{
		
		for(int i=0;i<this.rowHeightList.size();i++)
		{
			Row row = xsheet.getRow(i+temp);
			if(row==null) {
                row = xsheet.createRow(i+temp);
            }

			int  d=((Integer)rowHeightList.get(i)).intValue();
			d=d*15;
			row.setHeight(Short.parseShort(String.valueOf(d)));			
		}
		for(int i=0;i<this.columnWidthList.size();i++)
		{
			
			int  d=((Integer)this.columnWidthList.get(i)).intValue();
			d=d*38;
			if(this.xsheet.getColumnWidth(Integer.parseInt(String.valueOf(i+temp1)))<d) {
                this.xsheet.setColumnWidth(Short.parseShort(String.valueOf(i+temp1)),Short.parseShort(String.valueOf(d)));
            }
//			
		}
	}
	
	public void resetSize2(int temp,int temp1)
	{
		
		
			HSSFRow row = sheet.getRow(temp);
			if(row==null) {
                row = sheet.createRow(temp);
            }

			int  d=60;
			d=d*15;
			row.setHeight(Short.parseShort(String.valueOf(d)));			
	
		for(int i=0;i<this.columnWidthList.size();i++)
		{
			
			int  c=((Integer)this.columnWidthList.get(i)).intValue();
			c=c*38;
			this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i+temp1)),Short.parseShort(String.valueOf(c)));
			
		}
	}
	
	public void resetSizeX2(int temp,int temp1)
	{
		
		
			Row row = xsheet.getRow(temp);
			if(row==null) {
                row = xsheet.createRow(temp);
            }

			int  d=60;
			d=d*15;
			row.setHeight(Short.parseShort(String.valueOf(d)));			
	
		for(int i=0;i<this.columnWidthList.size();i++)
		{
			
			int  c=((Integer)this.columnWidthList.get(i)).intValue();
			c=c*38;
			this.xsheet.setColumnWidth(Short.parseShort(String.valueOf(i+temp1)),Short.parseShort(String.valueOf(c)));
			
		}
	}
	
	/**
	 * 取得 表头底层单元格数据集和
	 * @return
	 */
	public ArrayList getBottomGridInfoList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from muster_cell where Tabid="+this.tabid+"  and RTop+RHeight="+(this.itemGridArea[1]+this.itemGridArea[3])+" order by RLeft";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				
				abean.set("gridno",rowSet.getString("GridNo"));
				abean.set("hz",rowSet.getString("Hz"));
				abean.set("align",rowSet.getString("Align"));
				abean.set("flag",rowSet.getString("Flag")!=null?rowSet.getString("Flag"):"H");
				abean.set("field_type",rowSet.getString("Field_Type")!=null?rowSet.getString("Field_Type"):"A");
				abean.set("slope",rowSet.getString("Slope"));
				abean.set("l",rowSet.getString("l"));
				abean.set("t",rowSet.getString("t"));
				abean.set("r",rowSet.getString("r"));
				abean.set("b",rowSet.getString("b"));
				String extendAttr = rowSet.getString("ExtendAttr");
				extendAttr=extendAttr!=null?extendAttr:"";
				String ColMerge = "false"; //是否合并
				String ColMergeByMain = "false"; //是否按人员,单位,职位合并
				if(extendAttr.indexOf("<ColMerge>")!=-1){
					ColMerge = extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),
							extendAttr.indexOf("</ColMerge>"));
				}
				if(extendAttr.indexOf("<ColMergeByMain>")!=-1){
					ColMergeByMain = extendAttr.substring(extendAttr.indexOf("<ColMergeByMain>")
							+"<ColMergeByMain>".length(),
							extendAttr.indexOf("</ColMergeByMain>"));
				}
				String RowMerge="false";
				if(extendAttr.indexOf("<RowMerge>")!=-1){
					RowMerge = extendAttr.substring(extendAttr.indexOf("<RowMerge>")+"<RowMerge>".length(),extendAttr.indexOf("</RowMerge>"));
				}
				abean.set("fontname", rowSet.getString("fontname"));
				abean.set("fonteffect",rowSet.getString("fonteffect"));
				abean.set("fontsize", rowSet.getString("fontsize"));
				abean.set("RowMerge", RowMerge);
				abean.set("nhide",rowSet.getString("nhide"));
				abean.set("ColMerge",ColMerge);
				abean.set("ColMergeByMain",ColMergeByMain);
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 是否需要合计(页小计,页累计,分组合计,总计)
	 * @param noperation: 
	 * @param flag: 0 总计 1 分组合计,2 页累计,3 页小计
	 * @return
	 * 
	 *                     总计   分组合计  页累计    页小计   | 值
                            0     0         0       0      | 0   无
                            0     0         0       1      | 1   仅页小计
                            0     0         1       0      | 2   仅页累计
                            0     0         1       1      | 3
                            0     1         0       0      | 4
                            0     1         0       1      | 5
                            0     1         1       0      | 6
                            0     1         1       1      | 7
                            1     0         0       0      | 8
                            1     0         0       1      | 9
                            1     0         1       0      | 10=2
                            1     0         1       1      | 11=3
                            1     1         0       0      | 12
                            1     1         0       1      | 13
                            1     1         1       0      | 14
                            1     1         1       1      | 15

                            分组时，分组最后一页，页累计=分组合计
                            不分组时，页累计是前面各页合计；最后一页，页累计=总计

	 * 
	 */
	public String isAdd(String noperation,int flag)
	{
		String isAdd="0";
		if(noperation!=null)
		{
			String s=Integer.toBinaryString(Integer.parseInt(noperation));
			String d=s;
			for(int i=4;i>0;i--)
			{
				if(i>s.length()) {
                    d="0"+d;
                }
			}
			isAdd=String.valueOf(d.charAt(flag));
		}
		return isAdd;
	}
	
	
	
	public static void main(String[] args)
	{
		String s=Integer.toBinaryString(15);
		String d=s;
		for(int i=4;i>0;i--)
		{
			if(i>s.length()) {
                d="0"+d;
            }
		}
	}
	
	
	public ArrayList recordToBean(ArrayList list)
	{
		ArrayList alist=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			RecordVo vo=(RecordVo)list.get(i);
			LazyDynaBean abean=new LazyDynaBean();
			abean.set("gridno",String.valueOf(vo.getInt("gridno")));
			abean.set("hz",vo.getString("hz"));
			abean.set("rleft",String.valueOf(vo.getDouble("rleft")));
			abean.set("rtop",String.valueOf(vo.getDouble("rtop")));
			abean.set("rwidth",String.valueOf(vo.getDouble("rwidth")));
			abean.set("rheight",String.valueOf(vo.getDouble("rheight")));
			abean.set("l",String.valueOf(vo.getInt("l")));
			abean.set("t",String.valueOf(vo.getInt("t")));
			abean.set("r",String.valueOf(vo.getInt("r")));
			abean.set("b",String.valueOf(vo.getInt("b")));
			abean.set("sl",String.valueOf(vo.getInt("sl")));
			abean.set("fontsize",String.valueOf(vo.getInt("fontsize")));
			abean.set("fontname",vo.getString("fontname"));
			abean.set("fonteffect",String.valueOf(vo.getInt("fonteffect")));
			abean.set("flag",String.valueOf(vo.getString("flag")));
			abean.set("align",String.valueOf(vo.getInt("align")));
			abean.set("lsize",String.valueOf(vo.getInt("lsize")));
			abean.set("rsize",String.valueOf(vo.getInt("rsize")));
			abean.set("tsize",String.valueOf(vo.getInt("tsize")));
			abean.set("bsize",String.valueOf(vo.getInt("bsize")));	
			abean.set("field_type",vo.getString("field_type"));
			abean.set("slope",String.valueOf(vo.getInt("slope")));
			abean.set("nhide",String.valueOf(vo.getInt("nhide")));
			String extendAttr=vo.getString("extendattr");
			String ColMerge = "false"; //是否合并
			String ColMergeByMain = "false"; //是否按人员,单位,职位合并
			if(extendAttr.indexOf("<ColMerge>")!=-1){
				ColMerge = extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),extendAttr.indexOf("</ColMerge>"));
			}
			if(extendAttr.indexOf("<ColMergeByMain>")!=-1){
				ColMergeByMain = extendAttr.substring(extendAttr.indexOf("<ColMergeByMain>")+"<ColMergeByMain>".length(),extendAttr.indexOf("</ColMergeByMain>"));
			}
			String RowMerge="false";
			if(extendAttr.indexOf("<RowMerge>")!=-1){
				RowMerge = extendAttr.substring(extendAttr.indexOf("<RowMerge>")+"<RowMerge>".length(),extendAttr.indexOf("</RowMerge>"));
			}
			/**导出excel多层表头合并单元格会有问题（相同单位多行记录连续合并有问题），暂时取消导出合并单元格，后期优化再考虑*/
			abean.set("ColMerge", "false");
			abean.set("ColMergeByMain", "false");
			abean.set("RowMerge", "false");
			alist.add(abean);
		}
		return alist;
	}
	
	
	
	/**
	 * 取得修饰后的 表标题 数据集和
	 * @return
	 */
	public void getResetPageList()
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			isExistTitleTable();
			/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 start */
			//ArrayList tempList=new ArrayList();
			/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 end */
			int rtop=0;
			RecordVo vo=null;
			RecordVo a_vo=null;
			for(int i=0;i<this.pageList.size();i++)
			{
				a_vo=new RecordVo("tmuster_title");
//				if(a_vo!=null){
					vo=(RecordVo)this.pageList.get(i);
					if(rtop==0) {
                        rtop=vo.getInt("rtop");
                    }
					
					if(vo.getInt("rtop")<=rtop+10&&vo.getInt("rtop")>=rtop-10) {
                        vo.setInt("rtop",rtop);
                    } else {
                        rtop=vo.getInt("rtop");
                    }
					if(rtop<=10) {
                        rtop = 0;
                    }
					/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 start */
					int tabid = vo.getInt("tabid");
					int gridno = vo.getInt("gridno");
					int flag = vo.getInt("flag");
					a_vo.setInt("tabid",tabid);
					a_vo.setInt("gridno",gridno);	
					/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 end */
					a_vo.setString("hz",vo.getString("hz"));
					a_vo.setInt("rleft",vo.getInt("rleft"));
					a_vo.setInt("rtop",rtop);
					a_vo.setInt("rwidth",vo.getInt("rwidth"));
					a_vo.setInt("rheight",vo.getInt("rheight"));
					a_vo.setInt("fontsize",vo.getInt("fontsize"));	
					a_vo.setString("fontname",vo.getString("fontname"));				
					a_vo.setInt("fonteffect",vo.getInt("fonteffect"));
					a_vo.setInt("flag",vo.getInt("flag"));
					a_vo.setString("extendattr",vo.getString("extendattr"));
					/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 start */
					//a_vo.setObject("content", vo.getObject("content"));
					dao.addValueObject(a_vo);
					// 如果为图片，则将图片信息存入到数据库
					if(flag == 13) { 
						StringBuffer updateSql = new StringBuffer();
						updateSql.append("update tmuster_title set content=(select content from muster_title where tabid="+tabid+" and gridno="+gridno+")")
							.append(" where tabid="+tabid+" and gridno="+gridno);
						dao.update(updateSql.toString());
					}
					//tempList.add(a_vo);
					/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 end */
//					dao.addValueObject(tempList);
//					this.topPageList=getPageList("t");
//					this.bottomPageList=getPageList("b");
//					this.allPageList=getPageList("all");
//				}else{
//					break;
//				}
			}
			/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 start */
			//dao.addValueObject(tempList);
			/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 end */
			this.topPageList=getPageList("t");
			this.bottomPageList=getPageList("b");
			this.bodyList = getPageList("body");
			this.allPageList=getPageList("all");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * 
	 * @param position  t:表头上部参数   b:表尾 下部参数 all:全部（封面）
	 * @return
	 */
	public ArrayList getPageList(String position)
	{
		ArrayList list=new ArrayList();
		InputStream ins = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if("t".equals(position)) {
                sql="select * from tmuster_title where RTop<"+this.top_pix+" or RTop>10000 order by RTop,RLeft";
            } else if("body".equals(position)) {
                sql="select * from tmuster_title where RTop<="+this.bottom_pix+" and RTop>="+this.top_pix+" order by RTop,RLeft";
            } else if("b".equals(position)) {
                sql="select * from tmuster_title where RTop>"+this.bottom_pix+" and RTop<10000 order by RTop,RLeft";
            } else if("all".equals(position)) {
                sql="select * from tmuster_title  order by RTop,RLeft";
            }
			RowSet recset=dao.search(sql);
			int rtop=0;
			int layNum=0;
			int i=0;
			while(recset.next())
			{
				if(recset.getInt("RTop")!=rtop)
				{
					layNum++;
					if("t".equals(position)) {
                        this.topLayMap.put(recset.getString("RTop"),String.valueOf(layNum));
                    } else if("body".equals(position)) {
                        this.bodyMap.put(recset.getString("RTop"),String.valueOf(layNum));
                    } else if("b".equals(position)) {
                        this.botLayMap.put(recset.getString("RTop"),String.valueOf(layNum));
                    } else if("all".equals(position)) {
                        this.allParamMap.put(recset.getString("RTop"),String.valueOf(layNum));
                    }
				}else{
					if(i==0)
					{
						layNum++;
						if("t".equals(position)) {
                            this.topLayMap.put(recset.getString("RTop"),String.valueOf(layNum));
                        } else if("body".equals(position)) {
                            this.bodyMap.put(recset.getString("RTop"),String.valueOf(layNum));
                        } else if("b".equals(position)) {
                            this.botLayMap.put(recset.getString("RTop"),String.valueOf(layNum));
                        } else if("all".equals(position)) {
                            this.allParamMap.put(recset.getString("RTop"),String.valueOf(layNum));
                        }
					}
				}
				rtop=recset.getInt("RTop");
				if(rtop>10000) {
                    rtop=rtop/1000;
                }
				i++;
				RecordVo vo=new RecordVo("tmuster_title");
				vo.setInt("tabid",recset.getInt("Tabid"));
				vo.setInt("gridno",recset.getInt("GridNo"));	
				vo.setString("hz",recset.getString("Hz"));
				vo.setInt("rleft",recset.getInt("RLeft"));
				vo.setInt("rtop",rtop);
				vo.setInt("rwidth",recset.getInt("rWidth"));
				vo.setInt("rheight",recset.getInt("RHeight"));
				vo.setInt("fontsize",recset.getInt("FontSize"));	
				vo.setString("fontname",recset.getString("FontName"));				
				vo.setInt("fonteffect",recset.getInt("FontEffect"));
				vo.setInt("flag",recset.getInt("Flag"));	
				vo.setString("extendattr",Sql_switcher.readMemo(recset, "ExtendAttr"));
				/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 start */
				ins = recset.getBinaryStream("content");
				vo.setObject("content", ins);
				//liuy 2015-2-3 7310：组织机构：单位管理/信息维护/高级花名册输出Excel的时候服务器报空指针 start 
				//ins.close(); //释放资源 
				//ins = null;
				//liuy 2015-2-3 end
				/* 标识：1518 潍坊银行插入logo后存在的问题 xiaoyun 2014-5-28 end */
				list.add(vo);				
			}
			if("t".equals(position)) {
                this.topParamLayNum=layNum;
            } else {
                this.bottomParamLayNum=layNum;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(ins);//释放资源  guodd 2014-12-29
		}
		return list;
	}
	
	public void topTitles(String groupV){
		for(int i=0;i<this.topPageList.size();i++){
			RecordVo vo = (RecordVo) this.topPageList.get(i);
			String fontname=vo.getString("fontname");
			String fontsize=String.valueOf(vo.getInt("fontsize"));
			int    fonteffect=vo.getInt("fonteffect");
			HSSFFont font=getFont(fontname,fontsize,fonteffect);
			if (i + 1 < this.topPageList.size()) {
				RecordVo vo_2 = (RecordVo) this.topPageList.get(i + 1);
				if(!vo_2.getString("rtop").equals(topTitleNum()+"")){
					int from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
					short from_y = getColumn_y(vo.getInt("rleft"));
					int from2_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo_2.getInt("rtop")))) - 1;
					short from2_y = getColumn_y(vo_2.getInt("rleft"));
					if (from_x == from2_x && from_y == from2_y) {
						String value1 = getTitleValue(vo);
						String value2 = getTitleValue(vo_2);
						String value = (value1 + value2).replaceAll("&nbsp;"," ");
						executeCell(from_x, from_y, from_x, Short.parseShort(String.valueOf(from_y + 2)),value, "no_l",font);	
						i++;
					} else {
						String value1 = getTitleValue(vo).replaceAll("&nbsp;"," ");
						if("7".equals(vo.getString("flag"))){
							value1=groupV;
						}
						if (from_x == from2_x) {
							executeCell(from_x, from_y, from_x, Short
										.parseShort(String.valueOf(from_y
												+ (from2_y - from_y - 1))), value1,
												"no_l",font);
						} else {
							executeCell(from_x, from_y, from_x, Short
										.parseShort(String.valueOf(this.rowInfoBGrid.size() - 1)),
										value1, "no_l",font);
						}
					}
				}
			}
		}
	}
	public int topTitleNum(){
		int tops = 0;
		for(int i=0;i<this.topPageList.size();i++){
			RecordVo vo=(RecordVo)this.topPageList.get(i);
			if(vo.getString("rtop")!=null&&vo.getString("rtop").trim().length()>0){
				int rtop = Integer.parseInt(vo.getString("rtop"));
				if(i==0){
					tops = rtop;
				}
				if(tops>rtop) {
                    tops = rtop;
                }
			}
			
		}
		return tops;
	}
	/**
	 * 取得表头单元格信息集合
	 * @return
	 */
	public ArrayList getGridInfoList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			//wangcq 2014-11-20 begin 查询临时表字段类型
			StringBuffer sql_muster = new StringBuffer("");
			sql_muster.append("select * from ");
			sql_muster.append(this.userView.getUserName());
			sql_muster.append("_Muster_");
			sql_muster.append(this.tabid);
			sql_muster.append(" where 1=2");
			rowSet = dao.search(sql_muster.toString());
			ResultSetMetaData data=rowSet.getMetaData();
			//wangcq 2014-11-20 end	
			rowSet = dao.search("select * from muster_cell where Tabid="+this.tabid+" order by RTop,RLeft");
			RecordVo vo=null;
			while(rowSet.next())
			{
				vo=new RecordVo("muster_cell");
				String fieldname=rowSet.getString("Field_Name");
				fieldname=fieldname!=null?fieldname:"";
				String setname=rowSet.getString("SetName");
				
				/*if(fieldname!=null&&fieldname.length()>0)
				{
					FieldItem item=DataDictionary.getFieldItem(fieldname);
					if(item==null)
						vo.setString("field_type", rowSet.getString("Field_Type"));
					else
						vo.setString("field_type", item.getItemtype());
				}
				else*/
					vo.setString("field_type", rowSet.getString("Field_Type"));
				vo.setInt("gridno",rowSet.getInt("GridNo"));
				vo.setString("hz",rowSet.getString("Hz"));
				vo.setDouble("rleft",rowSet.getDouble("RLeft"));

				vo.setDouble("rtop",rowSet.getDouble("RTop"));
				vo.setDouble("rwidth",rowSet.getDouble("rWidth"));
				vo.setDouble("rheight",rowSet.getDouble("RHeight"));
				vo.setInt("l",rowSet.getInt("L"));
				vo.setInt("t",rowSet.getInt("T"));
				vo.setInt("r",rowSet.getInt("R"));
				vo.setInt("b",rowSet.getInt("B"));
				vo.setInt("sl",rowSet.getInt("SL"));
				vo.setInt("fontsize",rowSet.getInt("FontSize"));
				vo.setString("fontname",rowSet.getString("FontName"));
				vo.setInt("fonteffect",rowSet.getInt("FontEffect"));
				vo.setString("flag",rowSet.getString("Flag"));
				
				if("P".equalsIgnoreCase(rowSet.getString("flag")))
				{
					String ext=Sql_switcher.readMemo(rowSet, "extendattr")==null?"":Sql_switcher.readMemo(rowSet, "extendattr");
					ext = ext.toUpperCase();
					/**照片是否按比例显示*/
					String PhotoProportional="false";
					if(ext.indexOf("PHOTOPROPORTIONAL")!=-1)
					{
						PhotoProportional=ext.substring(ext.indexOf("<PHOTOPROPORTIONAL>")+19, ext.indexOf("</PHOTOPROPORTIONAL>"));
					}
					if("true".equalsIgnoreCase(PhotoProportional)) {
                        this.isHasPhoto=true;
                    }
				}
				vo.setInt("align",rowSet.getInt("Align"));//对齐方式
				vo.setInt("lsize",rowSet.getInt("LSize"));
				vo.setInt("rsize",rowSet.getInt("RSize"));
				vo.setInt("tsize",rowSet.getInt("TSize"));
				vo.setInt("bsize",rowSet.getInt("BSize"));
				vo.setInt("nhide",rowSet.getInt("nhide"));
				if(fieldname!=null&&setname!=null&&fieldname.equalsIgnoreCase(setname+"Z1")&&!"81".equals(this.infor_Flag)){
					vo.setInt("slope",0);
				}else{
					vo.setInt("slope",rowSet.getInt("Slope"));
				}
				vo.setString("setname", setname==null?"":setname);
				vo.setString("field_hz", rowSet.getString("field_hz")==null?"":rowSet.getString("field_hz"));
				vo.setString("field_name",fieldname==null?"":fieldname);
				vo.setString("noperation",rowSet.getString("nOperation"));
				vo.setString("extendattr", Sql_switcher.readMemo(rowSet, "extendattr"));
				
				if(!"M".equals(vo.getString("field_type")) && HmusterViewBo.isTextType("C"+rowSet.getInt("GridNo"), data)) //wangcq 2014-11-20 花名册定位查询多条数据时，相应数据类型改变，此时field_type的类型也需改变
                {
                    vo.setString("field_type", "M");
                }
				
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得需要合计的单元格信息
	 * @return
	 */
	public HashMap getAddInfoMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select GridNo,Flag,Field_Type,Slope,nOperation from muster_cell where Tabid="+this.tabid);
			while(rowSet.next())
			{
				String gridno=rowSet.getString("GridNo");
				String field_type=rowSet.getString("Field_Type");
				if(field_type==null) {
                    field_type="";
                }
				String Flag=rowSet.getString("Flag");
				Flag=Flag!=null?Flag:"";
				int  slope=rowSet.getInt("Slope");
				if(("N".equals(field_type)|| "R".equalsIgnoreCase(Flag))&&!"0".equals(rowSet.getString("nOperation")))
				{
					if(this.opt==1) {//横向分栏  
						if("0".equals(this.hmusterViewBo.getDataarea())) {//单行数据区 不计入合计累计打印空行等设置
							continue;
						}
					}
					LazyDynaBean abean=new LazyDynaBean();
					String noperation=rowSet.getString("nOperation");
					if("N".equalsIgnoreCase(field_type)|| "R".equalsIgnoreCase(Flag)){
						// 0 总计 1 分组合计,2 页累计,3 页小计
						String yxj=isAdd(noperation,3);
						String ylj=isAdd(noperation,2);
						String fzhj=isAdd(noperation,1);
						String zj=isAdd(noperation,0);
						if("1".equals(yxj)) {
                            this.isyxj=true;
                        }
						if("1".equals(ylj)) {
                            this.isylj=true;
                        }
						if("1".equals(fzhj))
						{
							this.isfzhj=true;
							this.isfzhj2=true;
						}
						if("1".equals(zj)) {
                            this.iszj=true;
                        }
						
						abean.set("yxj",yxj);
						abean.set("ylj",ylj);
						abean.set("fzhj",fzhj);
						abean.set("zj",zj);
						abean.set("fzhj2", fzhj);
					}
					else
					{
						abean.set("yxj","0");
						abean.set("ylj","0");
						abean.set("fzhj","0");
						abean.set("fzhj2","0");
						abean.set("zj","0");
					}
					abean.set("slope",String.valueOf(slope));
					map.put(gridno,abean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return map;
	}
	
	
	
	/**
	 * 是否存在 muster_title 的临时表
	 */
	public void isExistTitleTable()
	{
		boolean is=true;
		DbWizard dbWizard=new DbWizard(this.conn);
		ContentDAO dao=new ContentDAO(this.conn);
		String sql="";
		try
		{
			if(Sql_switcher.searchDbServer()==2) {
                sql="create Table tmuster_title as select * from muster_title where 1=2 ";
            } else {
                sql="select *  into tmuster_title  from muster_title where 1=2 ";
            }
			if(!dbWizard.isExistTable("tmuster_title",false))
			{					
				dao.update(sql);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("tmuster_title");
			}
			dao.delete("delete from tmuster_title",new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 取得表标题信息集合
	 * @return
	 */
	private ArrayList getPageList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from muster_title where Tabid="+this.tabid+" order by RTop,RLeft");
			RecordVo vo=null;
			while(rowSet.next())
			{
				vo=new RecordVo("muster_title");
				vo.setInt("tabid",rowSet.getInt("Tabid"));
				vo.setInt("gridno",rowSet.getInt("GridNo"));
				vo.setString("hz",rowSet.getString("Hz"));
				vo.setInt("rleft",rowSet.getInt("RLeft"));
				int rtop = rowSet.getInt("RTop");
//				if(rtop>10000)
//					rtop=rtop/1000;
				vo.setInt("rtop",rtop);
				vo.setInt("rwidth",rowSet.getInt("rWidth"));
				vo.setInt("rheight",rowSet.getInt("RHeight"));
				vo.setInt("fontsize",rowSet.getInt("FontSize"));
				vo.setString("fontname",rowSet.getString("FontName"));
				vo.setInt("fonteffect",rowSet.getInt("FontEffect"));
				vo.setInt("flag",rowSet.getInt("Flag"));
				vo.setString("extendattr",Sql_switcher.readMemo(rowSet,"ExtendAttr"));
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 取得高级花名册记录
	 * @return
	 */
	private RecordVo getHmusterVoById()
	{
		RecordVo vo=new RecordVo("muster_name");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo.setInt("tabid",Integer.parseInt(this.tabid));
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	
	private double[] getItemGridArea()
	{
		double[] area=new double[4];
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select min(RLeft) rleft,min(RTop) rtop,max(RLeft+RWidth)-min(RLeft) rwidth   from muster_cell where Tabid="+this.tabid);
			if(rowSet.next())
			{
				area[0]=rowSet.getDouble("rleft");
				area[1]=rowSet.getDouble("rtop");
				area[2]=rowSet.getDouble("rwidth");
			}
			rowSet=dao.search("select sum(RHeight) rheight from muster_cell where  Tabid="+this.tabid+" and  RLeft=(select min(RLeft)  from muster_cell where Tabid="+this.tabid+" )");
			if(rowSet.next())
			{
				area[3]=rowSet.getDouble("rheight");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return area;
	}


	/**
	 * 取得输出报表的格式信息
	 * @param tabid
	 * @return
	 */
	public HashMap getCfactor(String tabid)
	{
		HashMap map=new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			HmusterXML hxml= new HmusterXML(this.conn,tabid);
			String one = hxml.getValue(HmusterXML.SPILTCOLUMN);
			if (one != null && one.length() > 0) {
				String[] arg = one.split(":");
				if (arg.length > 0) {
                    map.put("isColumn", arg[0]); // 是否分栏
                } else if (arg.length > 1) {
                    map.put("columnAspect", arg[1]); // 分栏方向
                } else if (arg.length > 2) {
                    map.put("columnSpace", arg[2]); // 栏间距
                } else if (arg.length > 3) {
                    map.put("isLine", arg[3]); // 是否打印分隔线
                }
			}
			String two = hxml.getValue(HmusterXML.GROUPFIELD);
			if (two != null && two.trim().length() > 0) {
				map.put("groupN", two);
			}
			String two2 = hxml.getValue(HmusterXML.GROUPFIELD2);
			if (two2 != null && two2.trim().length() > 0) {
				map.put("groupN2", two2);
			}
			String three = hxml.getValue(HmusterXML.MULTIGROUPS);
			if (three != null && three.trim().length() > 0) {
				map.put("multipleGroupN", three);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	public String getCreateTablePerson(String userName,String xml)
	{
		String str="";
		try
		{
			String ss=userName;
			String prefix="";
			if(xml.indexOf("<prefix>")!=-1)
			{
				String fix=xml.substring(xml.indexOf("<prefix>")+8, xml.indexOf("</prefix>"));
				if(fix.length()>0) {
                    prefix=fix;
                }
			}
			str=prefix+ss;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getCreateTableDate(String xml)
	{
		String str="";
		try
		{
			GregorianCalendar d = new GregorianCalendar();//制表日期
			String dateStr= d.get(Calendar.YEAR)+ "."+ (d.get(Calendar.MONTH)+1)+ "."+ d.get(Calendar.DATE);
			String prefix="";
			
			if(xml.indexOf("<prefix>")!=-1)
			{
				String fix=xml.substring(xml.indexOf("<prefix>")+8, xml.indexOf("</prefix>"));
				if(fix.length()>0) {
                    prefix=fix;
                }
			}
			if(xml.indexOf("<format>")!=-1)
			{
				String format=xml.substring(xml.indexOf("<format>")+8, xml.indexOf("</format>"));
				/* 0: 1991.12.3
			      1: 1990.01.01
			      2: 1990年2月10日
			      3: 1990年01月01日
			      4: 1991-12-3
			      5: 1990-01-01*/
                int year=d.get(Calendar.YEAR);
                int month=d.get(Calendar.MONTH)+1;
                int day=d.get(Calendar.DATE);
				if(format.length()>0)
				{
					if("0".equals(format))
					{
						dateStr=year+"."+month+"."+day;
					}
					else if("1".equals(format))
					{
						dateStr=year+"."+((month>=10)?month+"":"0"+month)+"."+((day>=10)?day+"":"0"+day);
					}
					else if("2".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+month+ResourceFactory.getProperty("hmuster.label.month")+day+ResourceFactory.getProperty("hmuster.label.day");
					}
					else if("3".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+((month>=10)?month+"":"0"+month)+ResourceFactory.getProperty("hmuster.label.month")+((day>=10)?day+"":"0"+day)+ResourceFactory.getProperty("hmuster.label.day");
					}
					else if("4".equals(format))
					{
						dateStr=year+"-"+month+"-"+day;
					}
					else if("5".equals(format))
					{
						dateStr=year+"-"+((month>=10)?month+"":"0"+month)+"-"+((day>=10)?day+"":"0"+day);
					}
                    else if("6".equals(format))
                    {
                        dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+((month>=10)?month+"":"0"+month)+ResourceFactory.getProperty("hmuster.label.month");
                    }
				}
			}
			str=prefix+dateStr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	private int resourceCloumn=-1;
	public void getResourceCloumn(String tabid,String info_flag)
	{
		try
		{
			boolean flag=true;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =null;
			String sql = "select * from muster_cell where tabid="+tabid+" and UPPER(flag)='S'";
			rs=dao.search(sql);
			/**是否有序号指标*/
			while(rs.next())
			{
				this.resourceCloumn=1;
				flag=false;
				break;
			}
			/**是否有分组指标*/
			if(flag)
			{
				sql="select * from muster_cell where tabid="+tabid+" and (UPPER(flag)='G' or UPPER(flag)='E')";
				rs=dao.search(sql);
				while(rs.next())
				{
					this.resourceCloumn=2;
					flag=false;
					break;
				}
			}
			if(flag&&info_flag!=null)
			{
				if("1".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='A0101'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=3;
						flag=false;
						break;
					}
				}
				else if("2".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and (UPPER(field_name)='B0110' or UPPER(field_name)='E0122')";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=4;
						flag=false;
						break;
					}
				}
				else if("3".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='E01A1'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=5;
						flag=false;
						break;
					}
				}
			}
			if(flag&&info_flag!=null)
			{
				if("1".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_hz)='姓名'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=6;
						flag=false;
						break;
					}
				}
				else if("2".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and (UPPER(field_hz)='部门名称' or UPPER(field_hz)='单位名称')";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=7;
						flag=false;
						break;
					}
				}
				else if("3".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_hz)='岗位名称'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=8;
						flag=false;
						break;
					}
				}
			}
			if(flag)
			{
				this.resourceCloumn=-2;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String parseSelectPoint(String tocope,String fromCope)
	  {
		  String temp="";
		  try
		  {
			  StringBuffer ss=new StringBuffer("");
			  if(fromCope!=null&&fromCope.length()>0&&tocope!=null&&tocope.length()>0)
			  {
				  ss.append(fromCope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(fromCope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
				  ss.append("~");
				  ss.append(tocope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(tocope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  }else if(fromCope!=null&&fromCope.length()>0)
			  {
				  ss.append(fromCope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(fromCope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  }else if(tocope!=null&&tocope.length()>0)
			  {
				  ss.append(tocope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(tocope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  }
			  temp=ss.toString();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return temp;
	  }
	public String getA0100() {
		return a0100;
	}



	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public int getRows() {
		return rows;
	}



	public void setRows(int rows) {
		rows=rows!=0?rows:20;
		this.rows = rows;
	}


	public String getIsGroupNoPage() {
		return isGroupNoPage;
	}


	public void setIsGroupNoPage(String isGroupNoPage) {
		this.isGroupNoPage = isGroupNoPage;
	}

	public String getIsGroupedSerials() {
		return isGroupedSerials;
	}
	public void setIsGroupedSerials(String isGroupedSerials) {
		this.isGroupedSerials = isGroupedSerials;
	}
	public String getEmptyRow() {
		return emptyRow;
	}


	public void setEmptyRow(String emptyRow) {
		this.emptyRow = emptyRow;
	}


	public String getGroupPoint() {
		return groupPoint;
	}


	public void setGroupPoint(String groupPoint) {
		this.groupPoint = groupPoint;
	}


	public String getDataarea() {
		return dataarea;
	}


	public void setDataarea(String dataarea) {
		this.dataarea = dataarea;
	}


	public String getGroupNcode() {
		return groupNcode;
	}


	public void setGroupNcode(String groupNcode) {
		this.groupNcode = groupNcode;
	}


	public String getGroupVcode() {
		return groupVcode;
	}


	public void setGroupVcode(String groupVcode) {
		this.groupVcode = groupVcode;
	}


	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}


	public HmusterViewBo getHmusterViewBo() {
		return hmusterViewBo;
	}


	public void setHmusterViewBo(HmusterViewBo hmusterViewBo) {
		this.hmusterViewBo = hmusterViewBo;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getIsGroupPoint2() {
		return isGroupPoint2;
	}
	public void setIsGroupPoint2(String isGroupPoint2) {
		this.isGroupPoint2 = isGroupPoint2;
	}
	public String getGroupPoint2() {
		return groupPoint2;
	}
	public void setGroupPoint2(String groupPoint2) {
		this.groupPoint2 = groupPoint2;
	}
	public boolean isGroupV2() {
		return isGroupV2;
	}
	public void setGroupV2(boolean isGroupV2) {
		this.isGroupV2 = isGroupV2;
	}
	public String getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}
	public String getYearmonth() {
		return yearmonth;
	}
	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}
	public HashMap getTopDateTitleMap() {
		return topDateTitleMap;
	}
	public void setTopDateTitleMap(HashMap topDateTitleMap) {
		this.topDateTitleMap = topDateTitleMap;
	}
	public int getExcelType() {
		return excelType;
	}
	public void setExcelType(int excelType) {
		this.excelType = excelType;
	}
	
}
