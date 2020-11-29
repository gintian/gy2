/**   
 * @Title: EmployResumeZipBo.java 
 * @Package com.hjsj.hrms.businessobject.hire 
 * @Description: TODO
 * @author xucs
 * @date 2014-7-8 下午05:10:38 
 * @version V1.0   
*/
package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;

/** 
 * @ClassName: EmployResumeZipBo 
 * @Description:应聘简历导入导出zip格式数据专用的bo类
 * @author xucs 
 * @date 2014-7-8 下午05:10:38 
 *  
 */
public class EmployResumeZipBo {
    private Connection conn=null;
    private UserView userView=null;
    private transient Category cat = Category.getInstance("com.hrms.frame.dao.ContentDAO");
    public  EmployResumeZipBo(Connection conn,UserView view){
        this.conn=conn;
        this.userView=view;
    }
    /**
     * @Title: getResumeExcel 根据条件生成导出的EXCEL文件以及涉及到的附件
     * @param paramMap 包含各个参数
     * @throws 
    */
    public ArrayList getResumeExcelSqlAndColunm(HashMap paramMap) {
        ArrayList returnList = new ArrayList();
        ArrayList setNameList=new ArrayList();
        String nbase,a0100,isSelectedAll,conditionSQL,encryption_sql;//定义各个参数
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs =null;
        try{
            
            nbase= (String) paramMap.get("nbase");//人员库
            a0100= (String) paramMap.get("a0100");//选中人员的a0100
            /**处理a0100**/
            String[]a0100Arr=a0100.split("#");
            a0100="";
            for(int i=0;i<a0100Arr.length;i++){
                a0100=a0100+"'"+a0100Arr[i]+"',";
            }
            a0100=a0100.substring(0, a0100.length()-1);
            isSelectedAll= (String) paramMap.get("isSelectedAll");//是否全选  1：全选
            conditionSQL= (String) paramMap.get("conditionSQL");//简历查询条件
            encryption_sql=(String) paramMap.get("encryption_sql");
            
            
            /**先查询当前涉及到那些指标集和指标**/
            StringBuffer querySql = new StringBuffer();
            querySql.append("select * from constant where constant='ZP_FIELD_LIST'");   
            rs=dao.search(querySql.toString());
            
            HashMap setAndItemMap = new HashMap();//指标集和指标的map key：指标集 value:指标
            String setAndItem="";//取得到的数据
            String[] setAndItemArr=null;
            
            while(rs.next()){
                setAndItem=rs.getString("str_value");
            }
            /**先处理指标集**/
            if(setAndItem.trim().length()>0){
                setAndItemArr = setAndItem.split(",},");
                for(int i=0;i<setAndItemArr.length;i++){
                    
                    String tempvalue=setAndItemArr[i];
                    int setIndex =tempvalue.indexOf("{");
                    String setName=tempvalue.substring(0,setIndex);//指标集名称
                    FieldSet filedSet = DataDictionary.getFieldSetVo(setName);//判断指标集是否存在
                    if(filedSet==null){
                        continue;
                    }
                    /**如果指标集存在，并且指标集中定义了指标，且指标有效那么导出该指标集，并且查询相关数据**/
                    String nowSetField="";
                    String filedValues=tempvalue.substring(setIndex+1);//当前指标集中含有的指标
                    if(filedValues.trim().length()>0){
                        String[] filedArr =filedValues.split(",");
                        for(int j=0;j<filedArr.length;j++){
                            String tempfiedValue=filedArr[j];
                            int filedIndex=tempfiedValue.indexOf("[");
                            String filedvalue=tempfiedValue.substring(0, filedIndex);
                            FieldItem item = DataDictionary.getFieldItem(filedvalue);
                            if(item==null){
                                continue;
                            }
                            nowSetField=nowSetField+filedvalue+",";
                        }
                    }
                    if(nowSetField.trim().length()>0){
                        setAndItemMap.put(setName.toUpperCase(), nowSetField.split(","));//将指标用数组存进去
                        setNameList.add(setName.toUpperCase());
                    }
                }
            }
            setNameList.add("A00");
            setNameList.add("ZP_RELATION");
            setAndItemMap.put("ZP_RELATION", new String[1]);//将最后一个页签也放进去
            setAndItemMap.put("A00", new String[1]);
            
            //涉及到的指标集以及指标集中的指标处理end
            HashMap setAndItemSqlMap=new HashMap();
            Iterator it=setAndItemMap.keySet().iterator();
            while(it.hasNext()){
                String setName=(String) it.next();
                String tableName=nbase+setName;
                querySql.setLength(0);
                querySql.append("select * from "+tableName);
                
                if("0".equals(isSelectedAll)){//不是全选
                    querySql.append(" where "+tableName+".A0100 in " + "(" +a0100+")");
                }else{//全选
                    querySql.append(" where exists ( select null from ("+encryption_sql+") zhubiao where zhubiao.A0100="+tableName+".A0100)");
                }
                if(conditionSQL.trim().length()>0){
                    querySql.append(" and "+tableName+".A0100 in("+conditionSQL+")");
                }
                querySql.append(" order by "+tableName+".A0100");
                setAndItemSqlMap.put(setName.toUpperCase(), querySql.toString());
            }
            //最后一个页签，数据查询sql在这里生成
            querySql.setLength(0);
            //要取得招聘专业指标
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			String hireMajor="";
			if(map.get("hireMajor")!=null) {
                hireMajor=(String)map.get("hireMajor");  //招聘专业指标
            }

			
            querySql.append("select zp.A0100,zp.Thenumber,zp.nbase,org.corCode,z03.z0311,z03.Z0336 ");
            if(hireMajor.trim().length()>0){//如果有招聘专业 就查询出来，不用管它是否是校园招聘 后面生成的时候自动判断
            	querySql.append(",z03."+hireMajor+" zp_major ");
            }
            querySql.append(" from zp_pos_tache zp ,z03,organization org ");
            if("0".equals(isSelectedAll)){//不是全选
                querySql.append(" where zp.Zp_pos_id=z03.z0301 and org.codeitemid =z03.z0311 and zp.a0100 in("+a0100+")");
            }else{//全选
                querySql.append(" where zp.Zp_pos_id=z03.z0301 and org.codeitemid =z03.z0311 and exists (select null from ("+encryption_sql+") zhubiao where zhubiao.a0100=zp.a0100 )");
            }
            if(conditionSQL.trim().length()>0){
                querySql.append(" and zp.A0100 in("+conditionSQL+")");
            }
            querySql.append(" order by zp.a0100 ");
            setAndItemSqlMap.put("ZP_RELATION", querySql.toString());
            /**所有的查询语句处理完成，可以去生成各个页签**/
            returnList.add(setAndItemMap);
            returnList.add(setAndItemSqlMap);
            returnList.add(setNameList);
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return returnList;
    }
    /**
     * @param nbase  
     * @Title: createExcel 
     * @Description: 生成Excel
     * @param dataList 存放指标集和指标列以及查询语句的list
     * @return ArrayList   返回生成的excel和各种附件的名字
     * @throws 
    */
    public ArrayList createExcel(ArrayList dataList, String nbase) {
        ArrayList nameList = new ArrayList();
        RowSet rs =null;
        InputStream in = null;
        java.io.FileOutputStream fout = null;
        FileOutputStream fileOut = null;
        HSSFWorkbook wb = null;
        try{
            String attachName="";//如有附件，则这里面放的是附件的名字
            String userName=this.userView.getUserName();
            String outputFile="T_"+userName+".xls";
            HashMap setAndItemMap=(HashMap) dataList.get(0);
            HashMap setAndItemSqlMap=(HashMap) dataList.get(1);
            ArrayList setNameList = (ArrayList) dataList.get(2);
            wb = new HSSFWorkbook(); // 创建新的Excel 工作簿

            HSSFCellStyle cellStyle=wb.createCellStyle();//创建Excel格式
            cellStyle.setWrapText(true);//设置可以自动换行
            cellStyle.setAlignment(HorizontalAlignment.CENTER);//左右对齐方式居中
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);// 上下左右的边框样式
            cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
            HSSFRow row =null;
            HSSFCell cell=null;

    		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
    		HashMap map=parameterXMLBo.getAttributeValues();
    		String hireMajor="";
    		if(map.get("hireMajor")!=null) {
                hireMajor=(String)map.get("hireMajor");  //招聘专业指标
            }
            //while(it.hasNext()){
              for(int n=0;n<setNameList.size();n++){  
                String setName=(String) setNameList.get(n);
                String querysql=(String) setAndItemSqlMap.get(setName);//得到查询语句
                String[] fieldArr=(String[]) setAndItemMap.get(setName);//得到列名
                
                HSSFSheet sheet = wb.createSheet(setName);
                HSSFPatriarch patr = sheet.createDrawingPatriarch();//创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器.
                
                int rownum=0;//控制创建哪一行
                int colnum=2;
                row=sheet.createRow(rownum);//创建表头行
                /**创建表头行的A0100无论那一个sheet都必须有A0100 begin**/
                cell=row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue("A0100");
                /**创建表头行的A0100无论那一个sheet都必须有A0100 begin**/
                if("A01".equals(setName)){//A010子集需要有UserName这一列
                	colnum=3;
                    HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 1, 0, (short) 2, 1));// 定义注释的大小和位置,详见文档 
                    comment.setString( new HSSFRichTextString(nbase));
                    cell.setCellComment(comment);
                    /**A01里面放上UserName在导入时判断该用户是否在库中存在**/
                    cell=row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("UserName");
                    cell=row.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("UserPassword");
                    sheet.setColumnHidden(2,true);
                }
                
                if(!"A01".equals(setName)&&!"ZP_RELATION".equals(setName)){//第一个页签和最后一个页签都没有I9999这一列
                    cell=row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("I9999");
                }
                
                if("ZP_RELATION".equals(setName)){//最后一个页签的表头信息没有放在map中也不能通过FieldItem来判断
                    cell=row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("ZP_POS_DESC");
                    
                    cell=row.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("THENUMBER");
                    
                }else if("A00".equalsIgnoreCase(setName)){//A00子集也是特殊的子集
                    
                    cell=row.createCell(2);//title列
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("TITLE");
                    
                    cell=row.createCell(3);//EXT列
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("EXT");
                    
                    cell=row.createCell(4);//FLAG列
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("FLAG");
                    
                    cell=row.createCell(5);//CREATETIME列
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("CREATETIME");
                    
                    cell=row.createCell(6);//CREATEUSER列
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("CREATEUSER");
                    
                    cell=row.createCell(7);//FILENAME列
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue("FILENAME");
                    
                }else{
                    for(int i=0;i<fieldArr.length;i++){
                        String itemid=fieldArr[i];
                        FieldItem item=DataDictionary.getFieldItem(itemid);
                        String itemdesc=item.getItemdesc();//描述
                        String itemtype=item.getItemtype();//类型
                        int itemlength=item.getItemlength();//整数位长度
                        int itemDecimalLength=item.getDecimalwidth();//小数位长度
                        String commentValue =itemid+":"+itemtype+":"+itemlength+":"+itemDecimalLength;
                        HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 1, 0, (short) 2, 1));// 定义注释的大小和位置,详见文档 
                        comment.setString( new HSSFRichTextString(commentValue));
                        cell=row.createCell(i+colnum);
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(itemdesc);
                        cell.setCellComment(comment);
                    }
                }
                
                ContentDAO dao = new ContentDAO(this.conn);
//                try { 这个错误只是让以后的判断那一条语句出错时使用
                rs=dao.search(querysql);
                while(rs.next()){
                    rownum=rownum+1;
                    row=sheet.createRow(rownum);
                    /**创建特殊的列**/
                    colnum=2;
                    cell=row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(rs.getString("A0100"));
                    if("A01".equals(setName)){
                    	colnum=3;
                        cell=row.createCell(1);
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String exprotUserName=rs.getString("UserName");
                        if(exprotUserName==null|| "null".equalsIgnoreCase(exprotUserName)){
                            exprotUserName="";
                        }
                        cell.setCellValue(exprotUserName);
                        cell=row.createCell(2);
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String exprotUserPassword=rs.getString("UserPassword");
                        if(exprotUserPassword==null|| "null".equalsIgnoreCase(exprotUserPassword)){
                            exprotUserPassword="";
                        }
                        cell.setCellValue(PubFunc.encrypt(exprotUserPassword));
                    }
                    if(!"A01".equals(setName)&&!"ZP_RELATION".equals(setName)){
                        cell=row.createCell(1);
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(rs.getString("I9999"));
                    }
                   /**特殊列创建完毕**/ 
                    if("ZP_RELATION".equals(setName)){//最后一个页签的数据
                        
                        cell=row.createCell(1);//创建ZP_POS_DESC这一列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String corCode=rs.getString("corCode");
                        String z0311=rs.getString("z0311");
                        String zp_pos_desc_value="";
                        String z0336=rs.getString("z0336");
                        if(z0311!=null&&!"".equals(z0311)){
                            zp_pos_desc_value=z0311+":@k";//如果导出的是岗位的话就是@K
                        }
                        if(corCode!=null&&!"".equals(corCode)){//如果岗位上配置了coreCode就采用coreCode的值
                            zp_pos_desc_value=corCode+":@c";
                        }
                        if(z0336!=null&&"01".equals(z0336)){
                        	if(hireMajor.trim().length()<=0){
                        		throw new GeneralException("招聘参数设置中未定义招聘专业指标,而导出数据中存在校园招聘的应聘人员,导出数据失败");
                        	}else{
                        		String temp_desc_value=rs.getString("zp_major")==null?"":rs.getString("zp_major");
                        		zp_pos_desc_value=temp_desc_value+":@m";//用于校园招聘专业对应@m
                        	}
                        }
                        cell.setCellValue(zp_pos_desc_value);
                        
                        cell=row.createCell(2);//创建THENUMBER这一列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String thenumberValue=rs.getString("THENUMBER");
                        cell.setCellValue(thenumberValue);
                    }else if("A00".equalsIgnoreCase(setName)){//A00特殊的子集
                        cell=row.createCell(2);//title列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String titleValue=rs.getString("TITLE");
                        cell.setCellValue(titleValue);
                        
                        cell=row.createCell(3);//EXT列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String extValue=rs.getString("EXT");
                        cell.setCellValue(extValue);
                        
                        cell=row.createCell(4);//FLAG列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String flagValue=rs.getString("FLAG");
                        cell.setCellValue(flagValue);
                        
                        cell=row.createCell(5);//CREATETIME列
                        cell.setCellStyle(cellStyle);
                        Date createTime=rs.getDate("CREATETIME");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String createTimevalue=format.format(createTime);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(createTimevalue);
                        
                        cell=row.createCell(6);//CREATEUSER列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String createuser=rs.getString("CreateUserName");
                        cell.setCellValue(createuser);
                        
                        /**创建各种附件begin 文件命名 T_用户名A0100_I9999+ext**/
                        
                        
                        String A0100=rs.getString("A0100");
                        String I9999=rs.getString("I9999");
                        String prefix="T_"+userName+"_"+A0100+"_"+I9999;
                        String suffix=extValue;
                        
                        File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+prefix+suffix);
                        in = rs.getBinaryStream("ole");
                        fout = new java.io.FileOutputStream(tempFile);
                        int len=0;
                        byte buf[] = new byte[1024];
                        while (in!=null&&(len = in.read(buf, 0, 1024)) != -1) {
                            fout.write(buf, 0, len);
                        }
                        fout.close();
                        String filename=tempFile.getName();
                        attachName=attachName+filename+",";
                        /**创建各种附件end**/
                        cell=row.createCell(7);//FILENAME列
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(filename);
                    }else{
                        for(int i=0;i<fieldArr.length;i++){
                            String itemid=fieldArr[i];
                            FieldItem item=DataDictionary.getFieldItem(itemid);
                            String itemtype=item.getItemtype();//类型
                            int itemDecimalLength=item.getDecimalwidth();//小数位长度
                            
                            cell=row.createCell(i+colnum);
                            cell.setCellStyle(cellStyle);
                            
                            if("A".equalsIgnoreCase(itemtype)){
                                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                String value=rs.getString(itemid)==null?"":rs.getString(itemid);
                                cell.setCellValue(value);
                            }else if("D".equalsIgnoreCase(itemtype)){
                                Date value=rs.getDate(itemid);
                                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                if(value==null){
                                    cell.setCellValue("");  
                                }else{
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String timevalue=format.format(value);
                                    cell.setCellValue(timevalue);
                                }
                                
                            }else if("N".equalsIgnoreCase(itemtype)){
                                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                if(itemDecimalLength==0){
                                    int value=rs.getInt(itemid);
                                    cell.setCellValue(value);
                                }else{
                                    float value=rs.getFloat(itemid);
                                    cell.setCellValue(value);
                                }
                            }else{//"M".equalsIgnoreCase(itemtype)
                              cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC); 
                              String value=Sql_switcher.readMemo(rs, itemid);
                              cell.setCellValue(value);
                            }
                        }
                    }
                }
//                } catch (SQLException e) {
//                    System.out.println("current sql Error sql is :"+querysql+"and setName is:"+setName);
//                    e.printStackTrace();
//                }
            }
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
            wb.write(fileOut);
            nameList.add(outputFile);
            nameList.add(attachName);
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
    		PubFunc.closeIoResource(in);
    		PubFunc.closeIoResource(fout);
            PubFunc.closeResource(rs);
            PubFunc.closeResource(fileOut);
            PubFunc.closeResource(wb);
        }
        return nameList;
    }
    /** 
     * @Title: relativeExcel 
     * @Description: 对解压后zip文件后得到Execel文件进行分析
     * @param wb void   
     * @throws 
    */
    public ArrayList relativeExcel(Workbook wb)throws GeneralException {
        RecordVo vo= ConstantParamter.getConstantVo("ZP_DBNAME");
        //得到配置的人才库
        String nbase=vo.getString("str_value");
        ArrayList returnList = new ArrayList();
        ArrayList itemNotInList = new ArrayList();//存放凡是有误的itemid
        ArrayList setNotInList=new ArrayList();//存放不存在setName;
        ArrayList inforNotExistList = new ArrayList();//存放不存在信息
        ArrayList inforNotFormatList= new ArrayList();//存放格式不一致的字段
        ArrayList setNotExistList=new ArrayList();
        Sheet sheet = null;
        boolean containsetA01=false;
        int sheetNum =wb.getNumberOfSheets();
        for(int i=0;i<sheetNum;i++){
            String sheetname = wb.getSheetName(i);
            if("A01".equalsIgnoreCase(sheetname)){//t如果包含A01
                containsetA01=true;
                break;
            }
        }
        if(!containsetA01){
            throw new GeneralException(ResourceFactory.getProperty("sys.import.notBaseSet"));
        }
        for(int i=0;i<sheetNum;i++){
            
            String sheetname = wb.getSheetName(i);
            if("ZP_RELATION".equalsIgnoreCase(sheetname)){//这个页签什么都不用判断
                continue;
            }
            
            FieldSet filedSet = DataDictionary.getFieldSetVo(sheetname);//判断指标集是否存在
            if(filedSet==null|| "0".equalsIgnoreCase(filedSet.getUseflag())){//0:表示指标集未使用
                setNotExistList.add(ResourceFactory.getProperty("sys.import.filedSet")+sheetname+ResourceFactory.getProperty("sys.import.inReceiveNotExists"));
                setNotInList.add(sheetname);
                continue;
            }
            
            String setName=filedSet.getFieldsetdesc();
            sheet=wb.getSheet(sheetname);//挨着得到每一个sheet的数据
            Row row = sheet.getRow(0);//得到第一行
            StringBuffer itemNotExits= new StringBuffer();//字段不存在
            StringBuffer itemNotFormat= new StringBuffer();//字段类型不一致
            if (row == null) {
                throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.noData"));
            }
            
            int cols = row.getPhysicalNumberOfCells();
            if(cols<0){
                throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.noData"));
            }
            
            Cell a0100Cell = row.getCell(0);//A0100列
            if("A01".equalsIgnoreCase(sheet.getSheetName())){//如果是A01子集
                String a0100CellValue=a0100Cell.getStringCellValue();
                if(!"A0100".equalsIgnoreCase(a0100CellValue)){
                    throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.noBaseFiled"));
                }
                String cellComment=a0100Cell.getCellComment().getString().toString();//得到comment
                if(!cellComment.equalsIgnoreCase(nbase)){
                    throw new GeneralException(ResourceFactory.getProperty("sys.import.NbaseError"));
                }
                
            }else if(!"ZP_RELATION".equalsIgnoreCase(sheet.getSheetName())){
                if(cols<1){
                    throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.dataError"));  
                }
                Cell i9999Cell = row.getCell(1);//I9999列
                String a0100CellValue=a0100Cell.getStringCellValue();
                if(!"A0100".equalsIgnoreCase(a0100CellValue)){
                    throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.noBaseFiled"));
                }
                String i9999CellValue=i9999Cell.getStringCellValue();
                if(!"I9999".equalsIgnoreCase(i9999CellValue)){
                    throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.noSortFiled"));
                }
            }
            int colNum=3;

            if("A00".equalsIgnoreCase(sheet.getSheetName())|| "ZP_RELATION".equalsIgnoreCase(sheet.getSheetName())){//这两个页签没有comment
                 continue;
            }
            while(colNum<cols){
                Cell cell=row.getCell(colNum);
                String cellComment=cell.getCellComment().getString().toString();//得到comment
                String cellValue=cell.getStringCellValue();//得到字段的描述
                if(cellComment==null|| "".equals(cellComment)){
                    throw new GeneralException(ResourceFactory.getProperty("sys.import.filedSet")+sheet.getSheetName().toUpperCase()+ResourceFactory.getProperty("sys.import.commentError"));
                }
                String[] commentValueArr=cellComment.split(":");
                String itemid=commentValueArr[0];
                String inItemType=commentValueArr[1];
                int inItemlength=Integer.parseInt(commentValueArr[2]);
                int inItemDecimalWidth=Integer.parseInt(commentValueArr[3]);
                FieldItem item =DataDictionary.getFieldItem(itemid);
                if(item==null|| "0".equalsIgnoreCase(item.getUseflag())){//0标识该字段未使用
                    itemNotExits.append(cellValue+",");
                    itemNotInList.add(itemid);
                }else{
                    String nowItemType=item.getItemtype();
                    int nowItemLength=item.getItemlength();
                    int nowItemDecimalWidth=item.getDecimalwidth();
                    if(!nowItemType.equalsIgnoreCase(inItemType)){//任何一个不符合都给出提示
                        itemNotFormat.append(cellValue+",");
                        itemNotInList.add(itemid);
                    }else if(nowItemLength<inItemlength){
                        itemNotFormat.append(cellValue+",");
                        itemNotInList.add(itemid);
                    }else if(nowItemDecimalWidth<inItemDecimalWidth){
                        itemNotFormat.append(cellValue+",");
                        itemNotInList.add(itemid);
                    }
                }
                colNum++;
            }
            if(itemNotExits.length()>0){
                inforNotExistList.add(setName+"("+sheet.getSheetName().toUpperCase()+"):"+itemNotExits.toString().substring(0, itemNotExits.length()-1)+" "+ResourceFactory.getProperty("sys.import.fieldNotExists"));
            }
            if(itemNotFormat.length()>0){
                inforNotFormatList.add(setName+"("+sheet.getSheetName().toUpperCase()+"):"+itemNotFormat.toString().substring(0, itemNotFormat.length()-1)+" "+ResourceFactory.getProperty("sys.import.filedNotAgreement"));
            }
        }
        returnList.add(inforNotExistList);
        returnList.add(inforNotFormatList);
        returnList.add(itemNotInList);
        returnList.add(setNotInList);
        returnList.add(setNotExistList);
        return returnList;
    }
    /**
     * @param setNotInList 
     * @param itemNotInList 
     * @return 
     * @Title: importData 
     * @Description: 开始向数据库中导入数据
     * @param wb 主要的excel文件
     * @param fileHeaderList  zip文件的表头
     * @param zipFile 导入的zip文件  
     * @throws 
    */
    public String[] importData(Workbook wb, List fileHeaderList, ZipFile zipFile, ArrayList itemNotInList, ArrayList setNotInList)throws GeneralException {
        // TODO Auto-generated method stub
        String[] count=new String[2];
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet rs=null;
        int conutNum=0;
        /**
         *这里记录下数据的处理方式，
         *循环所有的sheet 创建一个setColList存放所有sheet列所对应的indexOfFieldMap
         *indexOfFieldMap:itemid所在sheet中的位置   key:(1||2||....)value:(A0114,A0415...) 一个sheet中的列数据对应一个map
         *(注意：以后后面的取数据的循环方式必须一致，否则数据会混乱出错)
         *先处理A01，得到所有的A0100，只有A01里面的A0100是唯一的，创建一个recordList存放所有的recordMap对应的记录 
         * 创建一个recordListMap key:a0100 value:a0100在 recordList的位置，即是第几个
         * recordMap中放置的是各个指标集所对应的Vo key：A01 value：A01Vo  [A04|A04VO] 到此为止所有说的A0100都是导出库的A0100
         * 然后循环所有的sheet 根据a0100生成各自所对应的其他指标集的vo放到每一个recordMap中
         * **/
        
        try{
            //得到配置的人才库
            RecordVo vo= ConstantParamter.getConstantVo("ZP_DBNAME");
            String nbase=vo.getString("str_value");
            String emailColumn=ConstantParamter.getEmailField().toLowerCase();//配置的邮箱字段
            Sheet sheet = null;
            int sheetNum =wb.getNumberOfSheets();
            /**首先处理所有的itemid在各自sheet中的位置begin**/
            ArrayList setColList=new ArrayList();//根据指标集所对应的名字存储指标集中各个指标所位于的excel中的列
            
            for(int i=0;i<sheetNum;i++){
                String sheetName = wb.getSheetName(i);
                if(setNotInList.contains(sheetName)){//如果指标集在接收库中不存在，跳过
                    continue;
                }
                sheet=wb.getSheet(sheetName);//挨着得到每一个sheet的数据
                if("A00".equalsIgnoreCase(sheetName)|| "ZP_RELATION".equalsIgnoreCase(sheetName)){
                    continue;
                }
                Row row = sheet.getRow(0);//得到第一行
                int cellNum=row.getPhysicalNumberOfCells();
                HashMap indexOfFieldMap=new HashMap();
                for(int j=3;j<cellNum;j++){
                    Cell cell=row.getCell(j);
                    String cellComment=cell.getCellComment().getString().toString();//得到comment
                    String[] commentValueArr=cellComment.split(":");
                    String itemid=commentValueArr[0];
                    if(itemNotInList.contains(itemid)){
                        continue;
                    }
                    indexOfFieldMap.put(""+j,itemid);
                }
                setColList.add(indexOfFieldMap);
            }
            /**首先处理所有的itemid在各自sheet中的位置end**/
            /**开始处理每一个记录(a0100)通过A01指标集统一处理begin**/
            ArrayList recordList = new ArrayList();
            HashMap recordListMap=new HashMap();
            HashMap recordListNameMap=new HashMap();
            String userNames="";
            sheet=wb.getSheet("A01");
            conutNum=sheet.getPhysicalNumberOfRows()-1;//所有的行，除去表头行
            count[0]=conutNum+"";
            HashMap colIndexMap =(HashMap) setColList.get(0);
            int recordNumber=0;
            Row row0 = sheet.getRow(0);
            Cell upwdCell = row0.getCell(2);
            String upwd = upwdCell.getStringCellValue();
            for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){//A01子集中的每一行(除了表头行)
                
                Row row=sheet.getRow(i);
                Cell A0100Cell=row.getCell(0);//A0100那一列
                Cell UserNamecell=row.getCell(1);//得到UserNAME那一列
                String userName=UserNamecell.getStringCellValue();
                Cell UserPasswordcell=null;//得到UserPassword那一列
            	String userPassword = "";
                if(upwd!=null&& "UserPassword".equalsIgnoreCase(upwd)){
                	UserPasswordcell=row.getCell(2);//得到UserPassword那一列
                	userPassword = PubFunc.decrypt(UserPasswordcell.getStringCellValue());
                }
                
                String a0100=A0100Cell.getStringCellValue();
                HashMap recordMap=new HashMap();
                if(userName!=null&&!"".equals(userName)){//没有邮箱的记录，不能导入
                    userNames=userNames+"'"+userName+"',";
                    RecordVo a01Vo=new RecordVo(nbase+"A01");
                    recordMap.put("olda0100",a0100);
                    a01Vo.setString("username", userName);
                    a01Vo.setString("userPassword".toLowerCase(), userPassword);
                    int num = 2;
                    if(upwd!=null&& "UserPassword".equalsIgnoreCase(upwd)){
                    	num = 3;
                    }
                    for(int j=num;j<row.getPhysicalNumberOfCells();j++){//列循环
                        Cell colCell=row.getCell(j);
                        String colName=(String) colIndexMap.get(""+j);
                        if(colName==null){
                            continue;
                        }
                        FieldItem item=DataDictionary.getFieldItem(colName);
                        String itemtype=item.getItemtype();
                        if("A".equalsIgnoreCase(itemtype)){
                            String value=colCell.getStringCellValue();
                            a01Vo.setString(colName.toLowerCase(), value);
                        }else if("D".equalsIgnoreCase(itemtype)){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String value=colCell.getStringCellValue();
                            if("".equals(value)){
                                a01Vo.setDate(colName.toLowerCase(), "");
                            }else{
                                Date date = format.parse(value);
                                a01Vo.setDate(colName.toLowerCase(), date);
                            }
                            
                        }else if("N".equalsIgnoreCase(itemtype)){
                            int decimalwidth=item.getDecimalwidth();
                            if(decimalwidth==0){
                               int value=(int) colCell.getNumericCellValue(); 
                               a01Vo.setInt(colName.toLowerCase(), value);
                            }else{
                              double value=colCell.getNumericCellValue();
                              a01Vo.setDouble(colName.toLowerCase(), value);
                            }
                        }else{
                            String value=colCell.getStringCellValue();
                            a01Vo.setString(colName.toLowerCase(), value);
                        }
                    }
                    
                    recordMap.put(sheet.getSheetName(),a01Vo);
                    recordListMap.put(a0100, ""+recordNumber);//存放它是list中的第几条记录
                    recordListNameMap.put(userName, ""+recordNumber);
                    recordList.add(recordMap);//recordList存放的是最终所有的结果
                    recordNumber++;
                }
            }
            
            /**开始处理每一个记录(a0100)通过A01指标集统一处理end**/
            /**向每一条记录中添加它所属的子集信息,A00和ZP_RELATION除外单独处理 begin**/
            for(int i=1;i<sheetNum;i++){//sheet循环
                String sheetName=wb.getSheetName(i);
                if(setNotInList.contains(sheetName)){//如果指标集在接收库中不存在，跳过
                    continue;
                }
                sheet = wb.getSheet(sheetName);
                if("A00".equalsIgnoreCase(sheetName)|| "ZP_RELATION".equalsIgnoreCase(sheetName)){
                    continue;
                }
                HashMap tempColIndexMap =(HashMap) setColList.get(i);
                
                for(int j=1;j<sheet.getPhysicalNumberOfRows();j++){//行循环
                    
                    Row row=sheet.getRow(j);
                    Cell A0100Cell=row.getCell(0);//A0100那一列
                    Cell I9999Cell=row.getCell(1);//I9999那一列
                    String a0100=A0100Cell.getStringCellValue();
                    String i9999=I9999Cell.getStringCellValue();
                    
                    if(recordListMap.get(a0100)!=null){
                        int index=Integer.parseInt((String) recordListMap.get(a0100));
                        //这个map在A01信息集生成的时候 放着 olda0100:a0100 原有的a0100, 和A01vo
                        HashMap recordMap=(HashMap) recordList.get(index);
                        String tableName=nbase+sheet.getSheetName();
                        RecordVo otherVo=new RecordVo(tableName);
                        otherVo.setString("i9999", i9999);
                        
                        for(int k=2;k<row.getPhysicalNumberOfCells();k++){//列循环
                            Cell colCell=row.getCell(k);
                            String colName=(String) tempColIndexMap.get(String.valueOf(k));
                            if(colName==null){
                                continue;
                            }
                            FieldItem item=DataDictionary.getFieldItem(colName);
                            String itemtype=item.getItemtype();
                            if("A".equalsIgnoreCase(itemtype)){
                                String value=colCell.getStringCellValue();
                                otherVo.setString(colName.toLowerCase(), value);
                            }else if("D".equalsIgnoreCase(itemtype)){
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String value=colCell.getStringCellValue();
                                if("".equals(value)){
                                    otherVo.setDate(colName.toLowerCase(), "");
                                }else{
                                    Date date = format.parse(value);
                                    otherVo.setDate(colName.toLowerCase(), date);
                                }
                            }else if("N".equalsIgnoreCase(itemtype)){
                                int decimalwidth=item.getDecimalwidth();
                                if(decimalwidth==0){
                                   int value=(int)(colCell.getNumericCellValue()); 
                                   otherVo.setInt(colName.toLowerCase(), value);
                                }else{
                                  float value=(float)(colCell.getNumericCellValue());
                                  otherVo.setDouble(colName.toLowerCase(), value);
                                }
                            }else{
                                String value=colCell.getStringCellValue();
                                otherVo.setString(colName.toLowerCase(), value);
                            }
                        }
                        
                        if(recordMap.get(sheet.getSheetName())!=null){
                            ArrayList voList = (ArrayList) recordMap.get(sheet.getSheetName());
                            voList.add(otherVo);
                            recordMap.put(sheet.getSheetName(), voList);
                        }else{
                            ArrayList voList =new ArrayList();
                            voList.add(otherVo);
                            recordMap.put(sheet.getSheetName(), voList);
                        }
                        
                    }
                }
                
            }
            /**向每一条记录中添加它所属的子集信息,A00和ZP_RELATION除外单独处理 end**/
            
            if(userNames.trim().length()>0){
            }else{
                throw new GeneralException(ResourceFactory.getProperty("sys.imoport.allEmailNoExits"));
            }
            int realNum=resloveData(recordList,wb,sheetNum,nbase,dao,fileHeaderList,zipFile,userNames,recordNumber,setNotInList);//删除应当删除的数据，增加应当增加唉的数据
            count[1]=realNum+"";
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            if(rs!=null){
               try {
                rs.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            }
        }
        return count;
    }
    /**
     * @param setNotInList 
     * @param recordNumber 
     * @param userNames 
     * @param zipFile 
     * @param fileHeaderList 
     * @param dao 
     * @param nbase  
     * @Title: resloveData 
     * @Description: TODO
     * @param recordList
     * @param wb void   
     * @throws 
    */
    private int resloveData(ArrayList recordList, Workbook wb, int sheetNum, String nbase, ContentDAO dao, List fileHeaderList, ZipFile zipFile, String userNames, int recordNumber, ArrayList setNotInList) {
        int realRecordNumber=0;
        try{
            String a0100s="";
            ArrayList canNotImportNamesList = new ArrayList();//存放状态不是未选和未选岗位的人员的邮箱
            HashMap tempA0100Map=new HashMap();//当一个人选择了多个岗位时要判断是不是所有的岗位都是未选状态的，判断时使用
            String queryaginUserName="";//存放未选岗位的人员的邮箱信息
            
            if(userNames.trim().length()>0){//处理不能导入的用户信息和导入需要删除用户的信息
                
                String[] userNameArray=userNames.split(",");
                int length=userNameArray.length;
                
                int arrLow=length%50;//获得按照50分组后的余数
                int arrLength=length/50;//去的按照50分组后的整数
                
                if(arrLow!=0){//如果余数不是0，那么数组的长度要+1
                    arrLength=arrLength+1;
                }
                String[] splitUserNameArr=new String[arrLength];//将所有的用户名分成数组，按照50个一个分组，这样是为了处理大数据量的情况下sql出错
                //HashMap notExitUserNameMap=new HashMap();//存放用户名的map
                ArrayList nn=new ArrayList();
                for(int i=0;i<arrLength;i++){
                    int begin=50*i;//计算开始取数的个数
                    int end =50*i+49;//计算每次取数的最后的数字
                    if(end>=length){//取数组的最后一个时应该取比数组长度小1的数字
                        end=length-1;
                    }
                    String tempNames="";
                    for(int k=begin;k<=end;k++){
                        //System.out.println("当前个数是："+k);
                        tempNames=tempNames+userNameArray[k]+",";
                        nn.add(userNameArray[k]);
                    }
                    tempNames=tempNames.substring(0, tempNames.length()-1);
                    splitUserNameArr[i]=tempNames;//splitUserNameArr[i]中都存放的一定量的用户名
                }
                //System.out.println("总共的邮箱是："+nn.size());
                ArrayList exitsZpUserName=new ArrayList();//存放着的是已经选择了岗位的用户名(状态包括：未选、已选等等，仅仅不包括未选岗位)
                for(int i=0;i<arrLength;i++){//对splitUserNameArr进行循环，每次只从数据库中取出一定量的用户名的先关信息
                    String tempUserName=splitUserNameArr[i];
                    String sql="select a.a0100,zp.resume_flag,a.UserName from "+nbase+"A01 a, zp_pos_tache zp where a.UserName in("+tempUserName+") and zp.a0100=a.a0100 order by zp.resume_flag desc";
                    RowSet rs=dao.search(sql);//这里是用来判断，当前这些用户名是否选择了岗位，
                    while(rs.next()){
                        String a0100=rs.getString("a0100");
                        String resume_flag=rs.getString("resume_flag");
                        String userName=rs.getString("UserName");
                        if(!exitsZpUserName.contains("'"+userName+"'")){
                            exitsZpUserName.add("'"+userName+"'");
                        }
                        if(!"10".equals(resume_flag)){//如果选择了岗位，并且他的简历不是未选状态的话，这些人的信息是不会进行任何操作,即不导入
                            tempA0100Map.put(a0100, "1");//如果一个人的简历选了多个岗位,只要有一个岗位是非未选状态就记录下来，排序中将未选状态放在最后
                            canNotImportNamesList.add(userName);
                            //canNotImportNames=canNotImportNames+userName+",";
                           realRecordNumber=realRecordNumber+1;
                        }else{//当前这个人所选的岗位有处于未选状态的
                            if(tempA0100Map.get(a0100)!=null){//当前这个人有 所选岗位处于非未选状态，那么他的记录不能更新
                                continue;
                            }//否者的话，他所有的岗位都是处于未选状态的，那么他的信息就全部删除
                            a0100s=a0100s+"'"+a0100+"',";
                            
                        }
                        
                    }
                    if(rs!=null){
                        rs.close();
                    }
                }
                
                //System.out.println("已经选择岗位了的邮箱："+exitsZpUserName.size());
                for(int i=0;i<exitsZpUserName.size();i++){
                    String ssname=(String) exitsZpUserName.get(i);
                    nn.remove(ssname);//将已选岗位的用户移除，剩余的就是未选岗位的人员信息
                }
                //System.out.println("移除未选后的总共剩下邮箱："+nn.size());
               for(int i=0;i<nn.size();i++){
                    String ssname=(String) nn.get(i);
                    queryaginUserName=queryaginUserName+ssname+",";
                }
            }
            
            if(queryaginUserName.trim().length()>0){//这里处理的是未选岗位的人，看看在人员库中是否已经存在，如果存在删除这些人的信息，并且重新导入
                
                String[] queryaginUserNameArr=queryaginUserName.split(",");//将所有需要再次查询的用户名分组确定有多少个分组
                int length=queryaginUserNameArr.length;
                int arrLow=length%50;//获得按照50分组后的余数
                int arrLength=length/50;//取得按照50分组后的整数
                
                if(arrLow!=0){//取数组的最后一个时应该取比数组长度小1的数字
                    arrLength=arrLength+1;
                }
                
                String[] splitqueryaginUserNameArr=new String[arrLength];//根据分组的个数创建数组
                for(int i=0;i<arrLength;i++){
                    int begin=50*i;//计算开始取数的个数
                    int end =50*i+49;//计算每次取数的最后的数字
                    if(end>=length){//取数组的最后一个时应该取比数组长度小1的数字
                        end=length-1;
                    }
                    String tempNames="";
                    for(int k=begin;k<=end;k++){
                        tempNames=tempNames+queryaginUserNameArr[k]+",";
                    }
                    tempNames=tempNames.substring(0, tempNames.length()-1);
                    splitqueryaginUserNameArr[i]=tempNames;
                }
                for(int i=0;i<arrLength;i++){
                    String tempUserName=splitqueryaginUserNameArr[i];
                    String sql="select a0100 from "+nbase+"A01  where UserName in("+tempUserName+")";
                    RowSet rs=dao.search(sql);
                    while(rs.next()){
                        String a0100=rs.getString("a0100");
                        a0100s=a0100s+"'"+a0100+"',";
                    }
                    if(rs!=null){
                        rs.close();
                    }
                }
            }
            realRecordNumber=recordNumber-realRecordNumber;
            if(a0100s.trim().length()>0){//这个a0100s中存放的是可以导入的用户，但是必须删除接受库中原有用户的a0100
                String[] a0100Arr=a0100s.split(",");
                int length=a0100Arr.length;
                
                int arrLow=length%50;//计算余数
                int arrLength=length/50;//计算整数
                if(arrLow!=0){//余数不为零，数组个数+1
                    arrLength=arrLength+1;
                }
                
                String[] splitA0100Arr=new String[arrLength];//根据个数生成循环的数组
                
                for(int i=0;i<arrLength;i++){
                    int begin=50*i;//计算开始取数的数值
                    int end =50*i+49;//计算取数结束的数值
                    if(end>=length){//取数组的最后一个时应该取比数组长度小1的数字
                        end=length-1;
                    }
                    String tempA0100="";
                    for(int k=begin;k<=end;k++){
                        tempA0100=tempA0100+a0100Arr[k]+",";
                    }
                    if(tempA0100.trim().length()>0){
                        tempA0100=tempA0100.substring(0, tempA0100.length()-1);
                        splitA0100Arr[i]=tempA0100;
                    }
                    
                }
                /**取得接受库中涉及到的指标集，要把邮箱涉及到的用户信息给清除begin**/
                StringBuffer querySql = new StringBuffer();
                String setAndItem="";
                ArrayList setNameList=new ArrayList();
                
                ArrayList consturtFieldsetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
                for(int s=0;s<consturtFieldsetList.size();s++){
                	FieldSet filedSet=(FieldSet) consturtFieldsetList.get(s);
                	
                	if(filedSet==null){
                        continue;
                    }
                	String setName=filedSet.getFieldsetid();//指标集名称
                    setNameList.add(setName.toUpperCase());
                }
//                querySql.append("select * from constant where constant='ZP_FIELD_LIST'");   
//                RowSet rs=dao.search(querySql.toString());
//                while(rs.next()){
//                    setAndItem=rs.getString("str_value");
//                }
//                /**处理指标集**/
//                if(setAndItem.trim().length()>0){
//                    String[] setAndItemArr = setAndItem.split(",},");
//                    for(int i=0;i<setAndItemArr.length;i++){
//                        
//                        String tempvalue=setAndItemArr[i];
//                        int setIndex =tempvalue.indexOf("{");
//                        String setName=tempvalue.substring(0,setIndex);//指标集名称
//                        FieldSet filedSet = DataDictionary.getFieldSetVo(setName);//判断指标集是否存在
//                        if(filedSet==null){
//                            continue;
//                        }
//                        setNameList.add(setName.toUpperCase());
//                    }
//                }
//                /**A00不存在上面所以加上**/
//                setNameList.add("A00");   原来处理的方式是有问题的  因为可能存在导入库使用的子集而接收库没使用  就会出现脏数据
                
                /**先处理指标集,取得接受库中涉及到的指标集，要把邮箱涉及到的用户信息给清除end**/
                for(int i=0;i<setNameList.size();i++){
                    String setName=(String) setNameList.get(i);
                    String tableName=nbase+setName;
                    /**数据量过大时采用分批次处理数据**/
                    for(int n=0;n<arrLength;n++){
                        String tempA0100s=splitA0100Arr[n];
                        String sql="delete from "+tableName+" where a0100 in("+tempA0100s+")";
                        dao.update(sql);
                    }
                }
                /**单独处理zp_pos_tache中的数据**/
                for(int n=0;n<arrLength;n++){//因为数据量过大采用分批处理数据
                    String tempA0100s=splitA0100Arr[n]; 
                    dao.update("delete from zp_pos_tache where a0100 in("+tempA0100s+") and nbase='"+nbase+"'");
                }
            }
            Sheet sheet=null;
            for(int i=0;i<recordList.size();i++){//这里面存放着要导入的所有的用户的信息
                //这个map在A01信息集生成的时候 放着 olda0100:a0100 原有的a0100 用于各个sheet之间对应 , 和A01vo，然后循环其他sheet之后又增加了其他子集的vo
                HashMap recordMap=(HashMap) recordList.get(i);
                
                RecordVo a01vo=(RecordVo) recordMap.get("A01");//各个子集的vo用各自的名字取到
                String username=a01vo.getString("username");
                if(canNotImportNamesList.contains(username)){//如果用户名在不可导入的名单中那么这条记录不导入
                    continue;
                }
                String olda0100=(String) recordMap.get("olda0100");//导出库对应这个人的a0100,循环中使用
                String newa0100=DbNameBo.insertMainSetA0100(nbase+"A01",this.conn);//接收库这个人的a0100
                
                for(int j=0;j<sheetNum;j++){//对sheet循环
                    String sheetName=wb.getSheetName(j);//sheet名字，即指标集的名字
                    if(setNotInList.contains(sheetName)){//如果指标集在接收库中不存在，跳过
                        continue;
                    }
                    sheet=wb.getSheet(sheetName);//挨着得到每一个sheet
                    if("A00".equalsIgnoreCase(sheetName)|| "ZP_RELATION".equalsIgnoreCase(sheetName)){//这两个里面的数据要单独处理
                        continue;
                    }
                    if("A01".equalsIgnoreCase(sheetName)){//a01指标集的记录只有一条
                        RecordVo tempvo=(RecordVo) recordMap.get(sheetName);
                        tempvo.setString("a0100", newa0100);
                        dao.updateValueObject(tempvo);
                    }else{//其他指标集的记录可能有好多条
                        ArrayList voList=(ArrayList) recordMap.get(sheetName);//这个list里面存放着好多个同一个人的子集的vo
                        if(voList!=null){
                            for(int k=0;k<voList.size();k++){
                                RecordVo tempvo=(RecordVo)voList.get(k);
                                tempvo.setString("a0100", newa0100);
                            }
                            dao.addValueObject(voList);   
                        }
                    }
                }
                /**开始处理A00子集**/
                sheet=wb.getSheet("A00");//这里面存储的是A00子集的信息     a00子集的字段是固定的 a0100 I9999 title ext flag createtime createtime fileName
                for(int m=1;m<sheet.getPhysicalNumberOfRows();m++){
                    Row row =sheet.getRow(m);
                    Cell A0100Cell=row.getCell(0);
                    String a0100=A0100Cell.getStringCellValue();
                    if(!olda0100.equals(a0100)){
                        continue;
                    }
                    Cell I9999Cell=row.getCell(1);
                    String i9999=I9999Cell.getStringCellValue();
                    Cell titleCell=row.getCell(2);
                    Cell extCell=row.getCell(3);
                    Cell flagCell=row.getCell(4);
                    Cell createTimeCell=row.getCell(5);
                    Cell createUserCell=row.getCell(6);
                    Cell fileNameCell=row.getCell(7);
                    
                    String title=titleCell.getStringCellValue();
                    String ext=extCell.getStringCellValue();
                    String flag=flagCell.getStringCellValue();
                    String createTime=createTimeCell.getStringCellValue();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date createTimeDate=format.parse(createTime);
                    String createUsername=createUserCell.getStringCellValue();
                    String fielname=fileNameCell.getStringCellValue();
                    
                    RecordVo tempvo=new RecordVo(nbase+"A00");
                    tempvo.setString("a0100", newa0100);
                    tempvo.setString("i9999", i9999);
                    tempvo.setString("title", title);
                    tempvo.setString("flag", flag);
                    tempvo.setString("ext", ext);
                    tempvo.setDate("createtime", createTimeDate);
                    tempvo.setString("createusername", createUsername);
                    
                    PreparedStatement ps = null;
                    FileHeader fileHeader = null;//zipFile.getFileHeader("multimedia"+File.separator+fielname);
                    for(int filei=0;filei<fileHeaderList.size();filei++){//得到这个人的附件文件
                        FileHeader tempFileHeader=(FileHeader) fileHeaderList.get(filei);
                        if(tempFileHeader.getFileName().equalsIgnoreCase("multimedia/"+fielname)){
                            fileHeader=tempFileHeader;
                            break;
                        }
                    }
                    if(fileHeader==null){
                        continue;
                    }
                    InputStream is = zipFile.getInputStream(fileHeader);
                    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                    byte [] fileByte = new byte[1024];
                    int len=0;
                    while(( len = is.read(fileByte)) != -1){
                        out.write(fileByte, 0, len);
                    }
                    
                    if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                        tempvo.setObject("ole",out.toByteArray());
                    }
                    dao.addValueObject(tempvo);
                    
                    if ((Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2)) {
                        
                        String str = "update "+nbase+"A00 set ole=? where A0100=? and i9999=?";
                        ArrayList values = new ArrayList();
                        Blob blob = getOracleBlob(newa0100, zipFile,fileHeader,nbase,i9999);
                        values.add(blob);
                        values.add(newa0100);
                        values.add(i9999);
                        dao.update(str, values);
                        /*
                        ps = this.conn.prepareStatement(str);
                        ps.setBlob(1, blob);
                        ps.setString(2, newa0100);
                        ps.setString(3, i9999);
                        ps.executeUpdate();*/
                    }
                    if(is!=null){
                        is.close();
                    }
                    if(out!=null){
                        out.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
                /**开始处理最后一个页签ZP_RELATION**/
        		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
        		HashMap map=parameterXMLBo.getAttributeValues();
        		String hireMajor="";//接收库中的专业指标字段
        		if(map.get("hireMajor")!=null) {
                    hireMajor=(String)map.get("hireMajor");  //招聘专业指标
                }
                sheet=wb.getSheet("ZP_RELATION");//这里面存储的是ZP_RELATION的相关信息
                String corcodes="";
                String majors="";//存放专业指标内容
                ArrayList volunteerList = new ArrayList();
                for(int m=1;m<sheet.getPhysicalNumberOfRows();m++){
                    
                    Row row=sheet.getRow(m);
                    Cell A0100Cell=row.getCell(0);
                    String a0100=A0100Cell.getStringCellValue();
                    if(!olda0100.equals(a0100)){//根据导出库的a0100对应一下，确认sheet中的数据是当前这个人的
                        continue;
                    }
                    
                    HashMap volunteerMap=new HashMap();
                    Cell zp_pos_idCell=row.getCell(1);//  应聘岗位||岗位代码名称||专业  ：@k||@c||@m(专业暂时还没做)
                    Cell theNumberCell=row.getCell(2);
                    
                    String zp_pos_id=zp_pos_idCell.getStringCellValue();
                    String theNumber=theNumberCell.getStringCellValue();
                    String[] zp_pos_idArr=zp_pos_id.split(":");
                    String tempvalue=zp_pos_idArr[1];
                    if("@k".equalsIgnoreCase(tempvalue)){//根据codeitemid走
                        volunteerMap.put("codeitemid",zp_pos_idArr[0]);
                    }else if("@c".equalsIgnoreCase(tempvalue)){//走的是corcode@c
                        corcodes=corcodes+"'"+zp_pos_idArr[0]+"',";
                        volunteerMap.put("corcode",zp_pos_idArr[0]);
                    }else{//根据专业走
                    	if(hireMajor.trim().length()<0){
                    		throw new GeneralException("导入数据中含有校园招聘的数据,请配置招聘专业指标");
                    	}else{
                    		if(zp_pos_idArr[0].trim().length()<=0){
                    			continue;
                    		}else{
                    			majors=majors+"'"+zp_pos_idArr[0]+"',";
                    			volunteerMap.put("major",zp_pos_idArr[0]);
                    		}
                    		
                    	}
                    }
                    volunteerMap.put("a0100",newa0100);
                    volunteerMap.put("thenumber", theNumber);
                    volunteerList.add(volunteerMap);
                }
                if(corcodes.trim().length()>0){//根据corcode取得对应的codeitemid，z0311中全部发布的都是codeitemid
                    corcodes=corcodes.substring(0, corcodes.length()-1);
                    String sql="select codeitemid,corcode from organization where corcode in ("+corcodes+")";
                    RowSet rs=dao.search(sql);
                    while(rs.next()){
                        String codeitemid=rs.getString("codeitemid");
                        String corcode=rs.getString("corcode");
                        for(int n=0;n<volunteerList.size();n++){
                            HashMap volunteerMap=(HashMap) volunteerList.get(n);
                            String incorcode=(String) volunteerMap.get("corcode");
                            if(incorcode!=null&&incorcode.equalsIgnoreCase(corcode)){
                                volunteerMap.put("codeitemid", codeitemid);
                            }
                        }
                    }
                    if(rs!=null){
                        rs.close();
                    }
                }
                String codeitemids="";
                for(int n=0;n<volunteerList.size();n++){//拼接当前人员涉及到的岗位
                    HashMap volunteerMap=(HashMap) volunteerList.get(n);
                    String codeitemid=(String) volunteerMap.get("codeitemid");
                    if(codeitemid==null){
                        continue;
                    }
                    codeitemids=codeitemids+"'"+codeitemid+"',";
                }
                if(codeitemids.trim().length()>0){//根据岗位，获得对应的发布序列号
                    codeitemids=codeitemids.substring(0, codeitemids.length()-1);
                    String sql="select Z0301,Z0311 from z03 where Z0311  in ("+codeitemids+") and Z0319='04'";//查询正在发布的岗位
                    RowSet rs=dao.search(sql);
                    while(rs.next()){
                        String Z0301=rs.getString("Z0301");//即zp_pos_tache中 Zp_pos_id
                        String Z0311=rs.getString("Z0311");//
                        for(int n=0;n<volunteerList.size();n++){
                            HashMap volunteerMap=(HashMap) volunteerList.get(n);
                            String codeitemid=(String) volunteerMap.get("codeitemid");
                            if(codeitemid!=null&&codeitemid.equalsIgnoreCase(Z0311)){
                                volunteerMap.put("zp_pos_id", Z0301);
                            }
                        }
                    }
                    if(rs!=null){
                        rs.close();
                    }
                }
                if(majors.trim().length()>0){
                	majors=majors.substring(0, majors.length()-1);
                	String sql="select Z0301,"+hireMajor+" from z03 where "+hireMajor+" in ("+majors+") and Z0319='04' ";//查询正在发布的专业
                	 RowSet rs=dao.search(sql);
                     while(rs.next()){
                         String Z0301=rs.getString("Z0301");//即zp_pos_tache中 Zp_pos_id
                         String tempMajor=rs.getString(hireMajor);//
                         for(int n=0;n<volunteerList.size();n++){
                             HashMap volunteerMap=(HashMap) volunteerList.get(n);
                             String major=(String) volunteerMap.get("major");
                             if(major!=null&&major.equalsIgnoreCase(tempMajor)){
                                 volunteerMap.put("zp_pos_id", Z0301);
                             }
                         }
                     }
                     if(rs!=null){
                         rs.close();
                     }
                }
                ArrayList volunteerVoList= new ArrayList();
                for(int n=0;n<volunteerList.size();n++){//开始更新zp_pos_tache中的数据
                    RecordVo tempvo=new RecordVo("zp_pos_tache");
                    HashMap volunteerMap=(HashMap) volunteerList.get(n);
                    String a0100=(String) volunteerMap.get("a0100");
                    String zp_pos_id=(String) volunteerMap.get("zp_pos_id");
                    if(zp_pos_id==null){//如果这条数据没有对应的发布序列号 ，不导入
                        continue;
                    }
                    String thenumber=(String) volunteerMap.get("thenumber");
                    String resume_flag="10";//设置成未选
                    tempvo.setString("a0100", a0100);
                    tempvo.setString("zp_pos_id", zp_pos_id);
                    tempvo.setString("thenumber", thenumber);
                    tempvo.setString("resume_flag", resume_flag);
                    tempvo.setString("nbase", nbase);
                    volunteerVoList.add(tempvo);
                }
                dao.addValueObject(volunteerVoList);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return realRecordNumber;
    }
    /**
     * @param i9999 
     * @param nbase 
     * 
     * @Title: getOracleBlob 
     * @Description: TODO
     * @param vo
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException Blob   
     * @throws
     */
    private Blob getOracleBlob(String newa0100, ZipFile zipfile,FileHeader fileHeader, String nbase, String i9999)throws FileNotFoundException, IOException {
        StringBuffer strSearch = new StringBuffer();
        strSearch.append("select ole from "+nbase+"A00 where A0100='");
        strSearch.append(newa0100);
        strSearch.append("' and i9999=");
        strSearch.append(i9999);
        strSearch.append(" FOR UPDATE ");
        
        StringBuffer strInsert = new StringBuffer();
        strInsert.append("update "+nbase+"A00 set ole=EMPTY_BLOB() where a0100='");
        strInsert.append(newa0100);
        strInsert.append("' and i9999=");
        strInsert.append(i9999);
        
        OracleBlobUtils blobutils = new OracleBlobUtils(this.conn);
        Blob blob=null;
        try {
            blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(),zipfile.getInputStream(fileHeader) );
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return blob;
   }
}
