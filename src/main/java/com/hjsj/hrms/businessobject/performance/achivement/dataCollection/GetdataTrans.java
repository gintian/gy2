package com.hjsj.hrms.businessobject.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title:GetdataTrans.java</p>
 * <p>Description:下载模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-10 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */

public class GetdataTrans
{
    private Connection con=null;
    private UserView userView=null;
    private String oname="";
    private String onlyname="";
    private String object_type="";
    private String targetid="";
    private HSSFWorkbook wb ;
    private HSSFSheet sheet;
    private HSSFFont font2;
    private HSSFCellStyle style2 ;
    private HSSFCellStyle styleCol0_title;
    private HSSFCellStyle styleF3;
    private HSSFCellStyle style3;
    public GetdataTrans(Connection con,UserView userview,String targetid)
    {
        this.con=con;
        this.userView=userview;
        this.targetid=targetid;
        init();
    }
    private void init()
    {
        AchivementTaskBo bo=new AchivementTaskBo(this.con,this.userView);
        RecordVo perTargetVo=bo.getPerTargetVo(targetid);
        this.object_type=perTargetVo.getString("object_type");
        if("2".equals(this.object_type))
        {
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.con);
            this.oname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            if(this.oname!=null&&this.oname.trim().length()!=0)
            {
                this.onlyname=DataDictionary.getFieldItem(this.oname).getItemdesc();
            }
        }else
        {
            RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.con);
            if(unit_code_field_constant_vo!=null)
                this.oname=unit_code_field_constant_vo.getString("str_value");
            if( this.oname.indexOf("#")==-1&&this.oname.length()!=0)
            {
                this.onlyname=DataDictionary.getFieldItem(oname).getItemdesc();
            }
        }
        this.wb=new HSSFWorkbook(); 
        this.sheet=wb.createSheet();
         
        font2 = wb.createFont();
        font2.setFontHeightInPoints((short) 10);
        style2 = wb.createCellStyle();
        style3=wb.createCellStyle();
        style2.setFont(font2);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        style2.setWrapText(true);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        
        style2.setBottomBorderColor((short) 8);
        style2.setLeftBorderColor((short) 8);
        style2.setRightBorderColor((short) 8);
        style2.setTopBorderColor((short) 8);
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        styleCol0_title=dataStyle(wb);
        styleCol0_title.setFont(font2);
        styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        styleF3 = dataStyle(wb);
        styleF3.setAlignment(HorizontalAlignment.CENTER);
        styleF3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleF3.setWrapText(true);
        HSSFDataFormat df3 = wb.createDataFormat();
        styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
        
        style3=dataStyle(wb);
        style3.setAlignment(HorizontalAlignment.CENTER);
        style3.setVerticalAlignment(VerticalAlignment.CENTER);
        style3.setWrapText(true);
        
        
    }
    /**
     * 保存指标排序
     * @param pointList   
     */
    public ArrayList getHeadlList(String object_type,ArrayList pointList)
    {
        ArrayList headList=new ArrayList();
        FieldItem fielditem = DataDictionary.getFieldItem("E0122");
        if("2".equals(object_type))
        {           
            headList.add(new CommonData("b0110",ResourceFactory.getProperty("b0110.label")));
            headList.add(new CommonData("e0122",fielditem.getItemdesc()));
            headList.add(new CommonData("a0101",ResourceFactory.getProperty("hire.employActualize.name")));
            
        }else
        {
            headList.add(new CommonData("a0101/b0110/e0122",ResourceFactory.getProperty("label.query.unit")+"/"+fielditem.getItemdesc()));
        }
        headList.add(new CommonData("kh_cyle",ResourceFactory.getProperty("jx.khplan.khqujian")));
        for(int i=0;i<pointList.size();i++)
        {
            CommonData d=(CommonData)pointList.get(i);
            headList.add(d);
        }
        return headList;
    }
    /**
     * 设置数据格式
     * @param pointList
     */
    public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
    {
        
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBottomBorderColor((short) 8);
        style.setLeftBorderColor((short) 8);
        style.setRightBorderColor((short) 8);
        style.setTopBorderColor((short) 8);
        return style;
    }
    /**
     * 获取唯一性指标和下载数据
     * @param pointList
     * @param target_id
     *  @param sql_whl
     *  @param acycle
     *  @param onlyname
     * */
    public ArrayList getdataList(ArrayList pointList,String target_id,String sql_whl,String acycle) throws GeneralException
    {
        if(this.oname!=null && this.oname.trim().length()>0 && !"#".equals(this.oname))
        {
            FieldItem fielditem = DataDictionary.getFieldItem(this.oname);
            String useFlag = fielditem.getUseflag(); 
            if("0".equalsIgnoreCase(useFlag))
                throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");  
        }
        ArrayList list=new ArrayList();
        AchivementTaskBo bo=new AchivementTaskBo(this.con,this.userView);
        try
        {
            ContentDAO dao=new ContentDAO(this.con);
            int cycle=0;
            String sql="select *  from per_target_list where target_id="+target_id;         
            RowSet  rowSet=dao.search(sql);
            String cycle_str="";
            String theyear="";
            String object_type="";
            if(rowSet.next())
            {
                cycle=rowSet.getInt("cycle");
                theyear=rowSet.getString("theyear");
                object_type=rowSet.getString("object_type");
            }
            StringBuffer sql0=new StringBuffer("select per_target_mx.* ");
            if("2".equals(object_type)){
                if(oname!=null&&oname.trim().length()!=0){
                    sql0.append(",usra01."+oname+" ");
                }
                sql0.append(" from per_target_mx ,UsrA01 where per_target_mx.object_Id=UsrA01.a0100  and ");
            }
            else {
                if(oname.indexOf("#")==-1&&oname.trim().length()!=0){
                    sql0.append(",b01."+oname+"  ");
                }
                sql0.append(" from per_target_mx inner join organization on per_target_mx.object_Id=organization.codeitemid  inner join b01 on per_target_mx.object_Id=b01.b0110 where    ");
            }
            
            sql0.append("   per_target_mx.target_id="+target_id);
            if(acycle!=null&&!"-1".equals(acycle))
                sql0.append(" and per_target_mx.kh_cyle='"+acycle+"'");
            if(sql_whl!=null&&sql_whl.length()>0){
                sql_whl = PubFunc.keyWord_reback(sql_whl);
                sql0.append(" and ( "+sql_whl+" )");
            }
            // 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
            String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
            if("2".equals(object_type))
            {
                StringBuffer buf = new StringBuffer();              
                if (operOrg!=null && operOrg.length() > 3)
                {                    
                    StringBuffer tempSql = new StringBuffer("");
                    String[] temp = operOrg.split("`");
                    for (int i = 0; i < temp.length; i++)
                    {
                        if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
                            tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                        else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
                            tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");

                    }
                    buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
                     
                }
                else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
                {
                    String priStrSql = InfoUtils.getWhereINSql(this.userView,"Usr");
                    if(priStrSql.length()>0)
                    {
                        buf.append("select usra01.A0100 ");
                        buf.append(priStrSql);
                    }
                }
                if(buf.length()>0)
                {
                    sql0.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
                }

            }
            else
            {
                if (operOrg!=null && operOrg.length() > 3)
                 {
                    StringBuffer tempSql = new StringBuffer("");
                    String[] temp = operOrg.split("`");
                    for (int i = 0; i < temp.length; i++)
                    {
                        tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
                    }
                    sql0.append(" and ( " + tempSql.substring(3) + " ) ");
                 }
                else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
                 {
                    String codeid=userView.getManagePrivCode();
                    String codevalue=userView.getManagePrivCodeValue();
                    String a_code=codeid+codevalue;
                    
                    if(a_code.trim().length()==0)
                    {
                        sql0.append(" and 1=2 ");
                    }
                    else if(!("UN".equals(a_code)))
                    {
                            sql0.append(" and per_target_mx.object_Id like '"+codevalue+"%' "); 
                            
                    }
                 }
            }
            
            
            sql0.append(" order by per_target_mx.kh_cyle");
            if("2".equals(object_type))
                sql0.append(",Usra01.a0000");
            else
                sql0.append(",organization.a0000");
            rowSet=dao.search(sql0.toString());
            
            LazyDynaBean abean=null;
            DecimalFormat myformat1 = new DecimalFormat("########.###");//
            int n=0;
            while(rowSet.next())
            {
                abean=new LazyDynaBean();
                
                if(oname!=null&&oname.trim().length()!=0&&oname.indexOf("#")==-1){
                    if(rowSet.getString(oname)==null){
                        abean.set(oname, " ");
                    }else{
                        abean.set(oname, rowSet.getString(oname));
                    }
                }
                abean.set("a0101",rowSet.getString("a0101"));
                abean.set("object_id",rowSet.getString("object_id"));
                abean.set("kh_cyle",rowSet.getString("kh_cyle"));
                abean.set("kh_cyle",bo.getCycle_str(rowSet.getString("kh_cyle"),cycle,theyear));
                String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
                String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
                abean.set("b01101", rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"");
                abean.set("e01221", rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
                if("2".equals(object_type))
                {
                    b0110=AdminCode.getCodeName("UN", b0110);
                    e0122=AdminCode.getCodeName("UM", e0122);
                }
                abean.set("b0110",b0110);
                abean.set("e0122",e0122);
                for(int i=0;i<pointList.size();i++)
                {
                    CommonData d=(CommonData)pointList.get(i);
                    if(rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())==0)
                        abean.set(d.getDataValue(),"0");
                    else
                        abean.set(d.getDataValue(),rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())!=0?myformat1.format(rowSet.getDouble("T_"+d.getDataValue())):"");
                }
                abean.set("index",String.valueOf(n));
                list.add(abean);
                n++;
            }
            if(rowSet!=null)
                rowSet.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            return list;
        }
    /**
     * 设置小数点位数
     * @param len 
     * */
    public String decimalwidth(int len)
    {

        StringBuffer decimal = new StringBuffer("0");
        if (len > 0)
            decimal.append(".");
        for (int i = 0; i < len; i++)
        {
            decimal.append("0");
        }
        decimal.append("_ ");
        return decimal.toString();
    }
    
    public HSSFRichTextString cellStr(String context)
    {
        HSSFRichTextString textstr = new HSSFRichTextString(context);
        return textstr;
    }
    /**
     * 反查考察周期和具体值
     * @param String kh_cyle
     * @param String theyear
     * */
    public String rekhcycle(String kh_cyle,String theyear)
    {
        String khcycle="";
        String cycle="";
        String tem=theyear+ResourceFactory.getProperty("columns.archive.year");
        if(kh_cyle.equals(tem))
        {
            cycle="0";
            khcycle="01";
        }
        else
        {
            if(kh_cyle.equals(tem+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")))
            {
                cycle="1";
                khcycle="1";
            }
            if(kh_cyle.equals(tem+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")))
            {
                cycle="1";
                khcycle="2";
            }
            if(kh_cyle.equals(tem+ResourceFactory.getProperty("report.pigionhole.oneQuarter")))
            {
                cycle="2";
                khcycle="01";
            }
            if(kh_cyle.equals(tem+ResourceFactory.getProperty("report.pigionhole.twoQuarter")))
            {
                cycle="2";
                khcycle="02";
            }
            if(kh_cyle.equals(tem+ResourceFactory.getProperty("report.pigionhole.threeQuarter")))
            {
                cycle="2";
                khcycle="03";
            }
            if(kh_cyle.equals(tem+ResourceFactory.getProperty("report.pigionhole.fourQuarter")))
            {
                cycle="2";
                khcycle="04";
            }
            for(int i=1;i<=12;i++)
            {
                if(i<10)
                {
                    if(kh_cyle.equals(tem+i+ResourceFactory.getProperty("columns.archive.month")))
                    {
                        cycle="3";
                        khcycle="0"+i;
                    }
                }else
                {
                    if(kh_cyle.equals(tem+i+ResourceFactory.getProperty("columns.archive.month")))
                    {
                        cycle="3";
                        khcycle=""+i;
                    }
                }
            }
        }
        return khcycle+"/"+cycle;
    }
    /**
     * 生成错误提示Excel
     * @param HashMap map
     * @param Sheet sheet2
     * @param FormFile file
     * @param int okCount
     * */
    public String createfilename(HashMap map,Sheet sheet2,String fileName,int okCount)
    {
        String errorname=fileName.substring(0, fileName.length() - 4)+"_提示.xls";
        FileOutputStream fileOut =null;
        try
        {           
            
            HSSFRow row2=sheet.createRow(0);
            Row row1=sheet2.getRow(0);
            row2.setHeight((short)800);  
            int cols = row1.getPhysicalNumberOfCells();
            HSSFCell cell2=row2.createCell(0);
            cell2.setCellValue("成功导入" + okCount + "条。");
            HSSFComment comm = null;
            HSSFPatriarch patr = sheet.createDrawingPatriarch();
            row2 = sheet.createRow(1);
            if(row1!=null){
                int titleCount = 0;
                row2.setHeight((short)800);  
                for (int i = 0; i < cols; i++)
                {
                    sheet.setColumnWidth(i, (short)2000);
                    Cell cell = row1.getCell(i);
                    if (cell != null)
                    {
                        cell2 = row2.createCell(i);
                        cell2.setCellValue(cell.getStringCellValue());
                        cell2.setCellStyle(style2);
                        comm = patr.createComment(new HSSFClientAnchor(1, 1, 1, 2, (short) (i + 1), 0, (short) (i + 2), 2));
                        comm.setString(new HSSFRichTextString(cell.getCellComment().getString().getString()));
                        cell2.setCellComment(comm);
                        titleCount++;
                    }
                    
                    ExportExcelUtil.mergeCell(sheet2, 0, 0, 0, cols - 1);
                }
            }
            int rowindex=2;
            
            Object[] key=map.keySet().toArray();
            int []key2=new int [key.length];
            
            for(int m=0;m<key.length;m++){
                key2[m]=Integer.parseInt(String.valueOf(key[m]));
            }
            for(int n=0;n<key2.length;n++){
                for(int lm=0;lm<key2.length;lm++){
                int tem;
                    
                        if(key2[n]<key2[lm]){
                            tem=key2[n];
                            key2[n]=key2[lm];
                            key2[lm]=tem;
                            
                        }
                    
                }
            }
            for(int i=0;i<key2.length;i++){
                int trol=key2[i];
                String col=(String)map.get(String.valueOf(trol));
                
                row2=sheet.createRow(rowindex++);
                row1=sheet2.getRow(trol);
            
                row2.setHeight((short)800);  
                for(int k=0;k<cols;k++){
                    
                    sheet.setColumnWidth(i, (short)2000);
                    Cell cell=row1.getCell(k);
                    if(cell!=null){
                        Cell cell1=row2.createCell(k);
                        
                        switch (cell.getCellType())
                        {
                            
                        case Cell.CELL_TYPE_NUMERIC:
                            cell1.setCellValue(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            cell1.setCellValue(cell.getStringCellValue());
                            break;
                        }
                        if(col.indexOf("-1")!=-1){
                            cell1.setCellStyle(style2);
                            continue;
                        }
                        if(col.trim().length()>1){
                            if(col.indexOf(","+k+",")!=-1){
                                cell1.setCellStyle(style2);
                                
                            }else{
                                cell1.setCellStyle(style3);
                            }
                        }else{
                                cell1.setCellStyle(style2);
                        }
                    }
                }
            }
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + errorname);
            wb.write(fileOut);
        }catch(Exception e){                        
            e.printStackTrace();
        }finally{
        	PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(wb);
        }
        return errorname;
    }
    public boolean isnumber(String value)
    {
        boolean flag=false;
        if(value!=null)
        {
            if(value.matches("^[+-]?[\\d]*[.]?[\\d]+")||value.matches("^[+-]?[\\d]+$"))
                flag =true;
        }
        return flag;
    }
    public void setOnlyname(String onlyname) 
    {
        this.onlyname = onlyname;
    }

    public String getOname()
    {
        return oname;
    }
    /**
     * 输出Excel
     * @param String sql_whl2
     * @param String cycle
     * */
    public String outexcel(String sql_whl2,String cycle) throws GeneralException
    {
        if(this.oname!=null && this.oname.trim().length()>0 && !"#".equals(this.oname))
        {
            FieldItem fielditem = DataDictionary.getFieldItem(this.oname);
            String useFlag = fielditem.getUseflag(); 
            if("0".equalsIgnoreCase(useFlag))
                throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");  
        }
        Permission per=new Permission(this.con,this.userView);
        AchivementTaskBo bo=new AchivementTaskBo(this.con,this.userView);
        ArrayList pointList=bo.getTargetPointList(targetid);
        ArrayList targetDataList=new ArrayList();
        RecordVo perTargetVo=bo.getPerTargetVo(targetid);
        ArrayList headList=new ArrayList();
        String outName=perTargetVo.getString("name");
        boolean print;
        FileOutputStream fileOut = null;
        try
        {
        // 创建新的Excel 工作簿
        HSSFPatriarch patr = this.sheet.createDrawingPatriarch();
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short)800);
        HSSFCell cell = null;
        HSSFComment comm = null;        
        //第一列主键标识
        headList=this.getHeadlList(object_type, pointList);
        if(oname!=null&&oname.trim().length()!=0&&oname.indexOf("#")==-1)
        {
            sheet.setColumnWidth(0, (short)0);
        
            cell = row.createCell(0);
            cell.setCellValue(cellStr(onlyname));
            cell.setCellStyle(styleCol0_title);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 1, 0, (short) 2, 1));
            comm.setString(new HSSFRichTextString(oname));
            cell.setCellComment(comm);
        }
        for(int i=0;i<headList.size();i++)
        {
            CommonData d=(CommonData)headList.get(i);
            String comment=d.getDataValue();
            String name=d.getDataName();
            int colindex=i;
            if(oname!=null&&oname.trim().length()!=0&&oname.indexOf("#")==-1)
                 colindex=i+1;
            cell=row.createCell((short)(colindex));
            cell.setCellValue(cellStr(name));
            cell.setCellStyle(style2);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (colindex + 1), 0, (short) (colindex + 2), 1));
            comm.setString(new HSSFRichTextString(comment));
            cell.setCellComment(comm);
        }
        int rocount=1;
        String object_id="";
        String b0110="";
        String e0122="";
        targetDataList=this.getdataList(pointList, targetid, sql_whl2, cycle);
        for(int i=0;i<targetDataList.size();i++)
        {
            row = sheet.createRow(rocount++);
            row.setHeight((short)800);  

            LazyDynaBean abean=(LazyDynaBean)targetDataList.get(i);
            if("2".equals(object_type)){
                b0110=(String)abean.get("b01101");
                e0122=(String)abean.get("e01221");
            }else
                object_id=(String)abean.get("object_id");
            if(oname!=null&&oname.trim().length()!=0&&oname.indexOf("#")==-1){
                cell = row.createCell(0);
                if(abean.get(oname)==null)
                    cell.setCellValue(cellStr(" "));
                else
                    cell.setCellValue(cellStr((String)abean.get(oname)));
                cell.setCellStyle(styleCol0_title);
            }
            for(int j=0;j<headList.size();j++){
                CommonData dd=(CommonData)headList.get(j);
                String temp="";
                if("a0101/b0110/e0122".equalsIgnoreCase(dd.getDataValue())){
                    temp=dd.getDataValue().split("/")[0];
                }else
                    temp=dd.getDataValue();
                String value=(String)abean.get(temp);
                if(oname!=null&&oname.trim().length()!=0&&oname.indexOf("#")==-1){
                    sheet.setColumnWidth(j+1, (short)4000);
                    cell=row.createCell(j+1);
                }
                else{
                    sheet.setColumnWidth(j, (short)4000);
                    cell=row.createCell(j);
                }
                
                if("a0101".equalsIgnoreCase(dd.getDataValue())|| "b0110".equalsIgnoreCase(dd.getDataValue())|| "e0122".equalsIgnoreCase(dd.getDataValue())|| "kh_cyle".equalsIgnoreCase(dd.getDataValue())|| "a0101/b0110/e0122".equalsIgnoreCase(dd.getDataValue())){
                    cell.setCellValue(new HSSFRichTextString(value));
                    cell.setCellStyle(style2);
                }else{

                    if("2".equals(object_type)){
                        print=per.getPrivPoint(b0110, e0122, temp);
                    }else{
                        print=per.getPrivPoint(object_id, "", temp);
                    }
                    if(print)
                        cell.setCellValue(new HSSFRichTextString(value));   
                    else{
                        cell.setCellValue(new HSSFRichTextString("---"));
                    }
                    cell.setCellStyle(styleF3);
                }
            }
        }
        
            outName+=PubFunc.getStrg() + ".xls";
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(wb);
        }
//      outName = outName.replace(".xls", "#");
        outName = PubFunc.encrypt(outName);
        //20/3/5 xus vfs改造
//        outName = SafeCode.encode(outName);     
        
        return outName;
    }
    /**
     * 获取输入Excel表格数据
     * @param FormFile file
     * */
    public HashMap importname(InputStream is, String fileName) throws GeneralException
    {   
        if(this.oname!=null && this.oname.trim().length()>0 && !"#".equals(this.oname))
        {
            FieldItem fielditem = DataDictionary.getFieldItem(this.oname);
            String useFlag = fielditem.getUseflag(); 
            if("0".equalsIgnoreCase(useFlag))
                throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");  
        }
        String errorname="";
        String tablename="per_target_mx";
        StringBuffer sql = new StringBuffer();
        sql.append("update " + tablename + " set ");
        int upcount=0;//将要更新的字段数
        StringBuffer upstr=new StringBuffer();//记录更新字段代码
        ArrayList pointlist=new ArrayList();//指标字段集合
        HashMap allpointmap=new HashMap();//所有指标集合
        AchivementTaskBo bo=new AchivementTaskBo(this.con,this.userView);
        pointlist=bo.getTargetPointList(targetid);
        String onlysql="";
        Permission per=new Permission(this.con,this.userView);
        Workbook wb=null;
        Sheet sheet=null;
        int okcount=0;
//        InputStream is = null;
        try {
//            is = new FileInputStream(file);
            wb=WorkbookFactory.create(is);
            sheet=wb.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(is);
			PubFunc.closeResource(wb);
        }
        try{
            
        
            String onlycol="";//Excel中唯一性指标所在列
            int pattern=0;//存库方式1.唯一性指标相同，2.系统没有，4.都没有
            String a0101col="";
            String kh_cyclecol="";
            String colname="";//当前表的唯一性指标
            HashMap a0101khmap=new HashMap();//记录当前任务书中的记录
            HashMap khcyclemap=new HashMap();//无效行
            HashMap pointmap=new HashMap();
            Row row=sheet.getRow(0);
            if(row==null){
                throw new GeneralException("请用导出的模板Excel来导入数据");
            }
            int utotalrow=sheet.getPhysicalNumberOfRows();//Excel表中所有数据行数
            int utotalcol=row.getPhysicalNumberOfCells();//Excel表中所有数据列数
            ContentDAO dao=new ContentDAO(this.con);
            if(pointlist.size()==0){
                throw new GeneralException("当前系统不存在更新指标！请设置更新指标！");
            }
            String lsql="select object_type,theyear,cycle from per_target_list where target_id="+Integer.parseInt(targetid);
            RowSet rs;
            rs=dao.search(lsql);
            rs.next();
            String objecttype=rs.getString(1);
            String theyear=rs.getString(2);
            String cycle=rs.getString(3);
            int excelpoint=0;
            lsql="select distinct(point_id) from per_target_point";
            rs=dao.search(lsql);
            while(rs.next()){
                allpointmap.put(rs.getString(1),rs.getString(1));
            }
            String buffer=cnotrlpriv();
            String tempsql="select * from " + tablename+ " where target_id="+targetid +"  "+buffer;
            rs=dao.search(tempsql);
            while(rs.next())
            {
                LazyDynaBean bean=new LazyDynaBean();
                bean.set("a0101", rs.getString("a0101")==null?"": rs.getString("a0101"));
                bean.set("b0110", rs.getString("b0110")==null?"": rs.getString("b0110"));
                bean.set("e0122", rs.getString("e0122")==null?"": rs.getString("e0122"));
                bean.set("object_id", rs.getString("object_id"));
                a0101khmap.put(rs.getString("a0101")+rs .getString("kh_cyle"), bean);
            }
            
            //查找当前Excel的唯一性指标列 唯一性指标相同则上传的表与当前页面是同一类型即人员或团队
            int len=0;
            if(row!=null){
                if(utotalrow<1||utotalcol<1){
                    throw new GeneralException("请用导出的模板Excel来导入数据");
                }else{
                    if(oname!=null&&oname.trim().length()>1){
                        for(int k=0;k<utotalcol;k++){
                            
                            Cell cell=row.getCell((short)k);
                            String title="";
                            if(cell!=null){
                                switch(cell.getCellType()){
                                    case Cell.CELL_TYPE_STRING:
                                        if(cell.getCellComment()==null){
                                            throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");
                                        }else{
                                            colname = cell.getCellComment().getString().getString().trim();
                                        }
                                        title = cell.getStringCellValue();
                                        break;
                                        
                                    case Cell.CELL_TYPE_NUMERIC:
                                        double y = cell.getNumericCellValue();
                                        title = Double.toString(y);
                                        break;
                                    default:
                                        colname="";
                                        title = "";
                                }
                            
                            if ("".equals(title.trim()))
                                throw new GeneralException("标题行存在空标题！");
                            if (cell.getCellComment() == null)
                                throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");
                            colname = colname.replaceAll("\\r", "").replaceAll("\\n", "");
                            if (colname.equalsIgnoreCase(oname))
                            {
                                pattern=1;
                                onlycol =""+k;
                                continue;
                            }
                            String field=cell.getCellComment().getString().toString().replaceAll("\\r", "").replaceAll("\\n", "");
                            if("kh_cyle".equalsIgnoreCase(field)){
                                kh_cyclecol=""+k;
                            }
                            
                                if("a0101".equalsIgnoreCase(field)){
                                        a0101col=""+k;
                                }   
                            
                                if("a0101/b0110/e0122".equalsIgnoreCase(field))
                                        a0101col=""+k;
                                    
                            
                            if("2".equals(objecttype)){
                                if((oname+",b0110,e0122,a0101,kh_cyle").indexOf(field.toLowerCase())==-1){
                                    for(int i=0;i<pointlist.size();i++){
                                        CommonData dd=(CommonData)pointlist.get(i);
                                        if(dd.getDataValue().toLowerCase().equals(field.toLowerCase())){
                                            sql.append("T_"+field + "=?,");
                                            pointmap.put(String.valueOf(k), field);
                                            upstr.append("," +k);
                                            upcount++;
                                            break;
                                        }
                                        if(i==pointlist.size()&&upcount==0)
                                            throw new GeneralException("导入模板中不存在更新指标！请用导出的模板Excel来导入数据！");
                                        
                                    }
                                }
                            }else{
                                if((oname+",a0101/b0110/e0122,kh_cyle").indexOf(field.toLowerCase())==-1){
                                    
                                        for(int i=0;i<pointlist.size();i++){
                                            CommonData dd=(CommonData)pointlist.get(i);
                                            if(dd.getDataValue().toLowerCase().equals(field.toLowerCase())){
                                                sql.append("T_"+field + "=?,");
                                                pointmap.put(String.valueOf(k), field);
                                                upstr.append("," +k);
                                                upcount++;
                                                break;
                                            }
                                            if(i==pointlist.size()&&upcount==0)
                                                throw new GeneralException("导入模板中不存在更新指标！请用导出的模板Excel来导入数据！");
                                        }
                                
                                }
                            }
                            }else{
                                throw new GeneralException("唯一性指标列在导入文件中不存在！请用导出的模板Excel来导入数据!");
                            }
                        }
                        if(onlycol.trim().length()==0){
                            throw new GeneralException("唯一性指标列在导入文件中不存在！请用导出的模板Excel来导入数据!");
                        }
                        if(a0101col.trim().length()==0){
                            throw new GeneralException("在导入文件中不存在考核对象！请用导出的模板Excel来导入数据!");
                        }
                        
                        if(kh_cyclecol.trim().length()==0){
                            throw new GeneralException("在导入文件中不存在考核区间！请用导出的模板Excel来导入数据!");
                        }
                    }else{
                        pattern=2;
                        //当前系统没有唯一性指标只通过查找 a0101列来区分上传且必须是相同类表才能上传
                        
                        for(int c=0;c<utotalcol;c++){
                            Cell cell=row.getCell(c);
                            if(cell!=null){
                                String title="";
                                switch(cell.getCellType()){
                                    case Cell.CELL_TYPE_FORMULA:
                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        double y = cell.getNumericCellValue();
                                        title = Double.toString(y);
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        title = cell.getStringCellValue();
                                        break;  
                                    default:
                                        title = "";
                                }
                                if ("".equals(title.trim()))
                                    throw new GeneralException("标题行存在空标题！");
                                if (cell.getCellComment() == null)
                                    throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");
                                String field=cell.getCellComment().getString().toString().replaceAll("\\r", "").replaceAll("\\n", "");
                                
                                    if("a0101".equalsIgnoreCase(field))
                                        a0101col=c+"";
                            
                                    if("a0101/b0110/e0122".equalsIgnoreCase(field))
                                        a0101col=c+"";
                                
                                if("kh_cyle".equalsIgnoreCase(field))
                                    kh_cyclecol=c+"";
                            
                                //区分团队和人员
                                if("2".equals(objecttype)){
                                    if((oname+",b0110,e0122,a0101,kh_cyle").indexOf(field.toLowerCase())==-1){
                                        if(allpointmap.get(field)!=null){
                                            excelpoint++;
                                            for(int i=0;i<pointlist.size();i++){
                                                CommonData dd=(CommonData)pointlist.get(i);
                                                if(dd.getDataValue().toLowerCase().equals(field.toLowerCase())){
                                                    sql.append("T_"+field + "=?,");
                                                    pointmap.put(String.valueOf(c), field);
                                                    upstr.append("," +c);
                                                    upcount++;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    if((oname+",a0101/b0110/e0122,kh_cyle").indexOf(field.toLowerCase())==-1){
                                        if(allpointmap.get(field)!=null){
                                            excelpoint++;
                                            for(int i=0;i<pointlist.size();i++){
                                                CommonData dd=(CommonData)pointlist.get(i);
                                                if(dd.getDataValue().toLowerCase().equals(field.toLowerCase())){
                                                    sql.append("T_"+field + "=?,");
                                                    pointmap.put(String.valueOf(c), field);
                                                    upstr.append("," +c);
                                                    upcount++;
                                                    break;
                                                }
                                            }
                                        }else{
                                            
                                        }
                                    }
                                }
                            }else 
                                break;
                        }
                        if(upcount==0)
                            throw new GeneralException("导入模板中不存在更新指标！请用导出的模板Excel来导入数据！");
                        if(kh_cyclecol.trim().length()==0)
                            throw new GeneralException("Excel表中不存在考核区间！请用导出的模板Excel来导入数据！");
                        if(a0101col.trim().length()==0){
                            throw new GeneralException("在导入文件中不存在考核对象！请用导出的模板Excel来导入数据!");
                        }
                    }
                }
            }
            upstr.append(",");
            sql.setLength(sql.length() - 1);
            
            onlysql=sql.toString();
            ArrayList alist=new ArrayList();//所有行数据
            ArrayList aonlylist=new ArrayList();
            //khcyclemap.put("k", "k");
            
            for(int i=1;i<utotalrow;i++){
                ArrayList dlist=new ArrayList();//当前行数据
                for(int k=0;k<upcount;k++){
                    dlist.add(String.valueOf(k));
                }
                String a0101="";
                String kh_cyle="";
                String onvalue="";
                String objectid="";
                row=sheet.getRow(i);
                String cyle="";
                String value="";
                String onlyFildValue ="";
                String b0110="";
                String e0122="";
                String object_id="";
                String tempoint="";
                boolean print;
                String wrcol="";
                String noonly="";
                boolean no=false;
                
                ArrayList onlylist=new ArrayList();
                if(oname==null||oname.trim().indexOf("#")!=-1||oname.trim().length()==0){
                    HashMap hm2=new HashMap();
                    int n=0;
                    for(int m=0;m<utotalcol;m++){
                        Cell cell=row.getCell(m);
                        if(cell!=null){     
                            if(upstr.toString().indexOf(","+m+",")!=-1){
                                        if(n<=upcount){
                                            hm2.put(String.valueOf(n), String.valueOf(m));
                                            n++;
                                        }
                                }
                                if(m==Integer.parseInt(a0101col)){
                                    switch(cell.getCellType()){
                                    case Cell.CELL_TYPE_NUMERIC:
                                        double y = cell.getNumericCellValue();
                                        value  = y + "";
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        value = cell.getRichStringCellValue().toString();
                                        break;
                                    default:
                                        value="";
                                    }
                                    if(value==null||value.trim().length()==0){
                                        wrcol+=","+m;
                                        continue;
                                    }
                                    else{
                                        a0101=value.trim();
                                        continue;
                                    }
                                }
                                if(m==Integer.parseInt(kh_cyclecol)){
                                    switch(cell.getCellType()){
                                    case Cell.CELL_TYPE_NUMERIC:
                                        double y = cell.getNumericCellValue();
                                        value  = y + "";
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        value = cell.getRichStringCellValue().toString();
                                        break;
                                    default:
                                        value="";
                                    }
                                    if(value==null||value.trim().length()==0){
                                        wrcol+=","+m;
                                        continue;
                                    }
                                    else{
                                        kh_cyle=value.trim();
                                        String []temp=rekhcycle(kh_cyle,theyear).split("/");
                                        if(temp.length==0){
                                            wrcol+=","+m;
                                            continue;
                                        }else{
                                            cyle= temp[0];
                                            if(cycle.equals(temp[1])){
                                                
                                            }else{
                                                wrcol+=","+m;
                                                continue;
                                            }
                                        }
                                        continue;
                                    }
                                }
                        }else{
                            continue;
                        }
                            
                    }
                    
                    if(a0101.trim().length()!=0&&cyle.trim().length()!=0){
                        Iterator tm=hm2.entrySet().iterator();
                        while(tm.hasNext()){
                            Map.Entry entry=(Map.Entry)tm.next();
                            Integer key=new Integer((String)entry.getKey());
                            Integer value1=new Integer((String)entry.getValue());
                            Cell cellw=row.getCell(value1.intValue());
                            LazyDynaBean abean=(LazyDynaBean)a0101khmap.get(a0101+cyle);
                            if(abean==null){
                                khcyclemap.put(String.valueOf(i),"-1");
                                break;
                            }else{
                                b0110=(String)abean.get("b0110");
                                e0122=(String)abean.get("e0122");
                                
                                object_id=(String)abean.get("object_id");
                                
                                tempoint=(String)pointmap.get(String.valueOf(value1));
                                if("2".equals(objecttype)){
                                    print=per.getPrivPoint(b0110, e0122, tempoint);
                                }else
                                    print=per.getPrivPoint(object_id, "", tempoint);
                                if(print){
                                    switch(cellw.getCellType()){
                                        case Cell.CELL_TYPE_NUMERIC:
                                            double y = cellw.getNumericCellValue();
                                            onlyFildValue = y + "";
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            onlyFildValue = cellw.getRichStringCellValue().toString();
                                            break;
                                        default:
                                            onlyFildValue="";
                                    }
                                    
                                    if(onlyFildValue.trim().length()==0){
                                        onlyFildValue="0";
                                        dlist.set(key.intValue(),onlyFildValue);
                                        continue;
                                    }
                                    if(!isnumber(onlyFildValue)){
                                        wrcol+=","+value1;
                                        continue;
                                    }
                                    if (onlyFildValue.indexOf("E") > -1)
                                    {
                                        String x1 = onlyFildValue.substring(0, value.indexOf("E"));
                                        String y1 = onlyFildValue.substring(value.indexOf("E") + 1);
                                        onlyFildValue = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
                                    }
                            
                                        dlist.set(key.intValue(),new Double((PubFunc.round(onlyFildValue, 3))));
                                    
                                    }
                                else{
                                    dlist.set(key.intValue(),"0");
                                }
                                
                            }
                        }
                        
                    }
                    if(wrcol.trim().length()>0){
                        wrcol+=",";
                        khcyclemap.put(String.valueOf(i),wrcol);
                    }
                    if(khcyclemap.get(String.valueOf(i))==null){
                        if(a0101khmap.get(a0101+cyle)!=null){
                            dlist.add(a0101);
                            dlist.add(cyle);
                            alist.add(dlist);
                        }else{
                            if(wrcol.trim().length()==0){
                                khcyclemap.put(String.valueOf(i),"-1");
                            }
                            
                        }
                    }else{
//                      if(a0101khmap.get(a0101+cyle)==null){
//                          khcyclemap.put(String.valueOf(i),"-1");
//                      }
                    }
                }else{
                    HashMap hm2=new HashMap();
                    int n=0;
                    for(int m=0;m<=utotalcol;m++){
                        Cell cellk=row.getCell(m);
                        if(cellk!=null){
                            if(upstr.toString().indexOf(","+m+",")!=-1){
                                if(n<=upcount){
                                    hm2.put(String.valueOf(n),String.valueOf(m));
                                    n++;
                                }
                                continue;
                            }
                        if(onlycol.equalsIgnoreCase(""+m)){
                            switch(cellk.getCellType()){
                            case Cell.CELL_TYPE_NUMERIC:
                                double y = cellk.getNumericCellValue();
                                value  = y + "";
                                break;
                            case Cell.CELL_TYPE_STRING:
                                value = cellk.getRichStringCellValue().toString();
                                break;
                            default:
                                value="";
                            }
                            if(value==null||value.trim().length()==0){
                                noonly+=m;
                                continue;
                                }
                            else{
                                onvalue=value;
                                if("2".equals(objecttype)){
                                    rs=dao.search("select a0100 from usra01 where "+ oname + "='"+ onvalue+"'");
                                    if(rs.next())
                                        objectid=rs.getString(1);
                                    else{
                                        wrcol+=","+m;
                                        continue;
                                    }
                                            
                                }else{
                                    rs=dao.search("select b0110 from b01 where " + oname + "='"+onvalue+"'");
                                    if(rs.next())
                                        objectid=rs.getString(1);
                                    else{
                                        wrcol+=","+m;
                                        continue;
                                    }
                                            
                                }
                            }
                            
                        }
                        if(m==Integer.parseInt(a0101col)){
                            switch(cellk.getCellType()){
                                case Cell.CELL_TYPE_NUMERIC:
                                    double y = cellk.getNumericCellValue();
                                    value  = y + "";
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    value = cellk.getRichStringCellValue().toString();
                                    break;
                                default:
                                    value="";
                                }
                            
                            if(value==null||value.trim().length()==0)
                            {
                                wrcol+=","+m;
                                continue;
                            }
                            else{
                                    a0101=value;
                            }
                            continue;
                        }
                        if(m==Integer.parseInt(kh_cyclecol)){
                                switch(cellk.getCellType()){
                                case Cell.CELL_TYPE_NUMERIC:
                                    double y = cellk.getNumericCellValue();
                                    value  = y + "";
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    value = cellk.getRichStringCellValue().toString();
                                    break;
                                default:
                                    value="";
                                }
                                if(value==null||value.trim().length()==0){
                                    wrcol+=","+m;
                                    continue;
                                }
                                else{
                                    kh_cyle=value;  
                                    String []temp=rekhcycle(kh_cyle,theyear).split("/");
                                    if(temp.length==0){
                                        wrcol+=","+m;
                                        continue;
                                    }
                                    else{
                                        cyle= temp[0];
                                        if(cycle.equals(temp[1])){
                                                
                                        }else{
                                            wrcol+=","+m;
                                            continue;
                                        }
                                    }
                                }
                            }
                        }else{
                            
                            continue;
                        }
                    }
                    
                    if(a0101.trim().length()!=0&&cyle.trim().length()!=0&&a0101khmap.get(a0101+cyle)!=null){//duml 2011-04-01
                        Iterator tm=hm2.entrySet().iterator();
                        while(tm.hasNext()){
                            Map.Entry entry=(Map.Entry)tm.next();
                            Integer key=new Integer((String)entry.getKey());
                            Integer value1=new Integer((String)entry.getValue());
                            Cell cellw=row.getCell(value1.intValue());
                            LazyDynaBean abean=(LazyDynaBean)a0101khmap.get(a0101+cyle);
                            if(abean==null){
                                khcyclemap.put(String.valueOf(i),"-1");
                                break;
                            }else{
                                b0110=(String)abean.get("b0110");
                                e0122=(String)abean.get("e0122");
                                object_id=(String)abean.get("object_id");
                                
                                tempoint=(String)pointmap.get(String.valueOf(value1));
                                if("2".equals(objecttype)){
                                    print=per.getPrivPoint(b0110, e0122, tempoint);
                                }else
                                    print=per.getPrivPoint(object_id, "", tempoint);
                                if(print){
                                    switch(cellw.getCellType()){
                                        case Cell.CELL_TYPE_NUMERIC:
                                            double y = cellw.getNumericCellValue();
                                            onlyFildValue = y + "";
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            onlyFildValue = cellw.getRichStringCellValue().toString();
                                            break;
                                        default:
                                            onlyFildValue="";
                                    }
                                    if(onlyFildValue.trim().length()==0){
                                        onlyFildValue="0";
                                        dlist.set(key.intValue(),onlyFildValue);
                                        continue;
                                    }
                                    if(!isnumber(onlyFildValue)){
                                        wrcol+=","+value1;
                                        continue;
                                    }
                                    if (onlyFildValue.indexOf("E") > -1)
                                    {
                                        String x1 = onlyFildValue.substring(0, value.indexOf("E"));
                                        String y1 = onlyFildValue.substring(value.indexOf("E") + 1);
                                        onlyFildValue = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
                                    }
                                
                                    dlist.set(key.intValue(),new Double((PubFunc.round(onlyFildValue, 3))));
                                }
                                else{
                                    dlist.set(key.intValue(),"0");
                                }
                                
                            }
                            
                        }
                        
                        }
                        if(noonly.trim().length()!=0&&wrcol.length()>0){
                            wrcol+=","+noonly+",";
                            khcyclemap.put(String.valueOf(i),wrcol);
                        }else{
                            if(wrcol.length()>0){
                                wrcol+=",";
                                khcyclemap.put(String.valueOf(i),wrcol);
                            }else{
                                if(noonly.trim().length()!=0){
                                    no=true;
                                    onlylist=dlist;
                                }else{
                                    
                                }
                                
                            }
                        }
                        if(!no){
                            if(khcyclemap.get(""+i)==null){
                                if(a0101khmap.get(a0101+cyle)!=null){
                                    dlist.add(objectid);
                                    dlist.add(cyle);
                                    alist.add(dlist);
                                }else{
                                    if(wrcol.trim().length()==0){
                                        khcyclemap.put(String.valueOf(i),"-1");
                                    }
                                }
                            }
                            else{
//                              if(a0101khmap.get(a0101+cyle)==null){
//                                  khcyclemap.put(String.valueOf(i),"-1");
//                              }
                            }
                        }else{
                            if(a0101khmap.get(a0101+cyle)!=null){//dml 2011-04-01
                                onlylist.add(a0101);
                                onlylist.add(cyle);
                                aonlylist.add(onlylist);
                            }else{//dml 2011-04-01
                                khcyclemap.put(String.valueOf(i),"-1");
                                continue;
                            }
                        }
                }
            }
            switch(pattern){
            case 1:
                sql.append(" where object_id=? and target_id="+ targetid+" and kh_cyle=?");
                break;
            case 2:
                sql.append(" where a0101=? and target_id="+ targetid+" and kh_cyle=?");
                break;
                
            default:
                sql.append("");
            }
            if(alist.size()!=0)
                dao.batchUpdate(sql.toString(), alist);
            if(aonlylist.size()!=0){
                onlysql+=" where a0101=? and target_id="+ targetid+" and kh_cyle=?";
                dao.batchUpdate(onlysql.toString(), aonlylist);
            }
            
            int errorcount=khcyclemap.size();
            okcount=utotalrow-errorcount-1;
            
        
            if(errorcount>0){
                errorname=createfilename(khcyclemap,sheet,fileName,okcount);
        //      errorname = errorname.replace(".xls", "#");
            }
            if(rs!=null){
                rs.close();
            }
        }catch(Exception e){
            
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        HashMap countname=new HashMap();
        countname.put("okcount", String.valueOf(okcount));
        countname.put("errorname", errorname);
        return countname;
        
    }
    public String getOnlyname() {
        return onlyname;
    }
    public String cnotrlpriv()
    {
        // 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
        String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
        StringBuffer sql0=new StringBuffer();
        if("2".equals(object_type))
        {
            StringBuffer buf = new StringBuffer();          
            if (operOrg !=null && operOrg.length() > 3)
            {                
                StringBuffer tempSql = new StringBuffer("");
                String[] temp = operOrg.split("`");
                for (int i = 0; i < temp.length; i++)
                {
                    if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
                        tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");

                }
                buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
                 
            }
            else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
            {
                String priStrSql = InfoUtils.getWhereINSql(this.userView,"Usr");
                if(priStrSql.length()>0)
                {
                    buf.append("select usra01.A0100 ");
                    buf.append(priStrSql);
                }
            }
            if(buf.length()>0)
            {
                sql0.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
            }
        }
        else
        {
            if (operOrg!=null && operOrg.length() > 3)
            {
                StringBuffer tempSql = new StringBuffer("");
                String[] temp = operOrg.split("`");
                for (int i = 0; i < temp.length; i++)
                {
                    tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
                }
                sql0.append(" and ( " + tempSql.substring(3) + " ) ");
            }
            else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
            {
                String codeid=userView.getManagePrivCode();
                String codevalue=userView.getManagePrivCodeValue();
                String a_code=codeid+codevalue;
                
                if(a_code.trim().length()==0)
                {
                    sql0.append(" and 1=2 ");
                }
                else if(!("UN".equals(a_code)))
                {
                    sql0.append(" and per_target_mx.object_Id like '"+codevalue+"%' ");                         
                }
            }
        }
        return sql0.toString();
    }
}
