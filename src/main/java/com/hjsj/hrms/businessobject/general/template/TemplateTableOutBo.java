/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.ykcard.TRecParamView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.codec.binary.Base64;
import org.bjca.seal.SealVerify;
import org.jdom.xpath.XPath;

import javax.imageio.ImageIO;
import javax.sql.RowSet;
import java.awt.Font;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:TemplateTableOutBo</p>
 * <p>Description:模板输出业务类，主要用于实现业务模板的输出</p> 
 * <p>PDF,EXCEL,WORD等</p>
 * <p>Company:hjsj</p> 
 * create time at:Nov 10, 20064:07:33 PM
 * @author chenmengqing
 * @version 4.0
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class TemplateTableOutBo {

	private Connection conn;
	private UserView userview;
	/**每英寸像素数
	 * default:windows 96
	 * mac             72
	 * */
	private int PixelInInch=96;  	
	/**模板号*/
	private int tabid=0;
	/**宽度及高度*/
	private float[] wh;
	/**总页数*/
	private int pages=1;
	/**当前页数*/
	private int currpage=1;
	/**业务模板对应*/
	private TemplateTableBo tablebo;
	private String type ="0";//0表示正常的打开pdf，1表示生成归档的历史数据产生的pdf
	private String signxml="";
	private String task_ids="";
	private boolean selfApply=false;//是否自助 20160617
	private int signtype=0;  //签章标识 0金格 1BJCA
	private String noshow_pageno = "";//不显示页签
	public TemplateTableOutBo(Connection conn,int tabid,UserView userview)throws GeneralException 
	{
		this.conn=conn;
		this.tabid=tabid;
		this.userview=userview;
		this.tablebo=new TemplateTableBo(conn,tabid,userview);
	}
	public TemplateTableOutBo(Connection conn,int tabid,String task_ids,UserView userview)throws GeneralException 
	{
		this.conn=conn;
		this.tabid=tabid;
		this.task_ids = task_ids;
		this.userview=userview;
		this.tablebo=new TemplateTableBo(conn,tabid,userview);
	}
	/**
	 * 求模板宽度及高度
	 * @param tvo
	 * @return
	 */
	private float[] getWidthHeight(RecordVo tvo)
	{
		int direct=tvo.getInt("paperori");
		float[] wh=new float[6];
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
		wh[1]=Math.round((float)(height/25.4*PixelInInch));	
		wh[2]=Math.round((float)(tmargin /25.4*PixelInInch));//顶部间距
		wh[3]=Math.round((float)(bmargin/25.4*PixelInInch));//底部间距	
		wh[4]=Math.round((float)(rmargin /25.4*PixelInInch));//右侧间距
		wh[5]=Math.round((float)(lmargin/25.4*PixelInInch));//左侧间距  zhaoxg add 2015-4-14	
		
		return wh;
	}
	
	private float[] getWidthHeight(RecordVo tvo,String paperOrientation)
	{
		int direct=tvo.getInt("paperori");
		if ("0".equals(paperOrientation)|| "".equals(paperOrientation)){
		}
		else {
			direct = Integer.parseInt(paperOrientation);
		}
		float[] wh=new float[6];
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
		wh[1]=Math.round((float)(height/25.4*PixelInInch));	
		wh[2]=Math.round((float)(tmargin /25.4*PixelInInch));//顶部间距
		wh[3]=Math.round((float)(bmargin/25.4*PixelInInch));//底部间距	
		wh[4]=Math.round((float)(rmargin /25.4*PixelInInch));//右侧间距
		wh[5]=Math.round((float)(lmargin/25.4*PixelInInch));//左侧间距  zhaoxg add 2015-4-14	
		
		return wh;
	}
   private  boolean isHZChar(char c)
    {
        boolean isCorrect =false;
        if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))
        {   
          //字母,   数字   
            isCorrect =false;  
        }else if(c=='-'||c=='/'){
            isCorrect =true; 
        }else{   
          if(Character.isLetter(c))
          {   //中文   
              isCorrect =true; 
             //System.out.println(Character.isLetter(c));
          }else{   //符号或控制字符   
              isCorrect =false; 
          }   
        } 
        return isCorrect;
    } 
    
   /**   
 * @Title: getFontSize   
 * @Description: 缩放字体，使单元格能容纳所有字符串   
 * @param @param str
 * @param @param columnWidth
 * @param @param fontName
 * @param @param fontEffect
 * @param @param fontSize
 * @param @return 
 * @return int 
 * @author:wangrd   
 * @throws   
*/
public int getFontSize(String str, int columnWidth, int columnHeight,String fontName,
           int fontEffect, int fontSize) 
   {
       
       try{
               BufferedImage gg = new BufferedImage(1, 1,
                   BufferedImage.TYPE_INT_RGB);
               Graphics g = gg.createGraphics(); // 获得画布
 
               Font font = new Font(fontName, fontEffect, fontSize);
               g.setFont(font);

               int aHeight = g.getFontMetrics().getHeight();
               int lines=getLines(str, columnWidth, fontName, fontEffect, fontSize);
               while ((lines*aHeight>columnHeight) &&(fontSize>3)){
                   fontSize=fontSize-1; 
                   lines=getLines(str, columnWidth, fontName, fontEffect, fontSize);
               }

           
       }
       catch(Exception e){
           e.printStackTrace();            
       }

       return fontSize;
   }
   
    /**
     * 根据列宽计算出字符串折行的行数
     * @param str
     *            填入列中字符串
     * @param columnWidth
     *            列的固定宽度
     * @param fontName
     *            字体名称
     * @param fontEffect
     *            已设好的字体样式
     * @param fontSize
     *            字体大小
     * @return
     */
    public int getLines(String str, int columnWidth, String fontName,
            int fontEffect, int fontSize) 
    {
        int lines=1;
        try{
                BufferedImage gg = new BufferedImage(1, 1,
                    BufferedImage.TYPE_INT_RGB);
                Graphics g = gg.createGraphics(); // 获得画布
  
                Font font = new Font(fontName, fontEffect, fontSize);
                g.setFont(font);
                float hzScale=1.0f;
                if (fontSize<=5) {
                    hzScale=2.3f;
                } else if (fontSize<=7) {
                    hzScale=2.0f;
                } else if (fontSize<=9) {
                    hzScale=1.8f;
                } else if (fontSize<11) {
                    hzScale=1.5f;
                } else if (fontSize>14) {
                    hzScale=2;
                }
           

                int awidth = g.getFontMetrics().stringWidth(str);
                int charlen = g.getFontMetrics().charWidth('c');
             //   int hzcharlen = g.getFontMetrics().getHeight();
                int hzcharlen = g.getFontMetrics().charWidth('汉');
               // int length =str.getBytes().length;
                awidth =charlen*str.getBytes().length; 
                int hzLen=0;
                if (str==null){  
                    str="";
                }  
                if (!"".equals(str)){                 
                    for(int i=0;i<str.length();i++)
                    {
                        char c =str.charAt(i); 
                        if (String.valueOf(c).getBytes().length>1){
                            hzLen++;
                            continue;
                        }
                        if(c=='\n') {
                            continue;
                        }
                        if(c=='\r') {     
                            continue;
                        }
                        if(isHZChar(c)){                            
                            hzLen++;  
                        }   
                        
                    }  
                    
                    awidth =awidth+ Math.round(hzLen*hzScale);
                    lines = awidth/columnWidth;
                    //int mod=awidth % columnWidth;
                   // if (mod >0) lines++;
                    lines++;
                    if (fontSize<14){                    
                        if (str.length()==hzLen){//全汉字
                            if (((awidth % columnWidth) *1.0f/columnWidth)>0.90) {
                                lines++;
                            }
                        }
                        else if (hzLen==0){//全非汉字
                            if (((awidth % columnWidth) *1.0f/columnWidth)<0.10) {
                                lines--;
                            }
                        }                        
                    }
                    if (lines>1){//字两边空白大，行数不够的问题
                        awidth=awidth+Math.round(lines*hzcharlen);
                        int alines = awidth/columnWidth;
                        int amod=awidth % columnWidth;
                        if (amod >0) {
                            alines++;
                        }
                        if (lines<alines) {
                            lines=alines;
                        }
                        
                    }
                    //每行只显示一个字 有可能两边空白大 造成行数不够
                   // if ((float)((columnWidth*1.0f%hzcharlen)/hzcharlen)>0.4) {
                   //    if ((lines>6 )&& (lines<hzLen)) {lines =hzLen;}
                   // }
                    
                }
            
        }
        catch(Exception e){
            e.printStackTrace();            
        }
        
        if (lines<1) {
            lines=1;
        }
        return lines;
    }
    
    
    /**   
     * @Title: getMemoFontSize   
     * @Description: 缩放字体，使单元格能容纳所有字符串   
     * @param @param str
     * @param @param columnWidth
     * @param @param fontName
     * @param @param fontEffect
     * @param @param fontSize
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
    */
    public int getMemoFontSize(String str, int columnWidth, int columnHeight,String fontName,
               int fontEffect, int fontSize) 
       {
           
           try{
                   BufferedImage gg = new BufferedImage(1, 1,
                       BufferedImage.TYPE_INT_RGB);
                   Graphics g = gg.createGraphics(); // 获得画布
     
                   Font font = new Font(fontName, fontEffect, fontSize);
                   g.setFont(font);

                   int aHeight = g.getFontMetrics().getHeight();
                   int lines=getMemoLines(str, columnWidth, fontName, fontEffect, fontSize);
                   while ((lines*aHeight>columnHeight) &&(fontSize>3)){
                       fontSize=fontSize-1; 
                       lines=getMemoLines(str, columnWidth, fontName, fontEffect, fontSize);
                       font = new Font(fontName, fontEffect, fontSize);
                       g.setFont(font);
                       aHeight = g.getFontMetrics().getHeight();
                   }

               
           }
           catch(Exception e){
               e.printStackTrace();            
           }

           return fontSize;
       }
    
    /**
     * 根据列宽计算出字符串折行的行数
     * @param str
     *            填入列中字符串
     * @param columnWidth
     *            列的固定宽度
     * @param fontName
     *            字体名称
     * @param fontEffect
     *            已设好的字体样式
     * @param fontSize
     *            字体大小
     * @return
     */
    private int getMemoLines(String str, int columnWidth, String fontName,
            int fontEffect, int fontSize) 
    {
        int lines=0;
        try{
                BufferedImage gg = new BufferedImage(1, 1,
                    BufferedImage.TYPE_INT_RGB);
                Graphics g = gg.createGraphics(); // 获得画布
  
                Font font = new Font(fontName, fontEffect, fontSize);
                g.setFont(font);
                float hzScale=1.0f;
                if (fontSize<=5) {
                    hzScale=2.3f;
                } else if (fontSize<=7) {
                    hzScale=2.0f;
                } else if (fontSize<=9) {
                    hzScale=1.8f;
                } else if (fontSize<11) {
                    hzScale=1.5f;
                } else if (fontSize>14) {
                    hzScale=2;
                }
           

                int awidth = g.getFontMetrics().stringWidth(str);
                int charlen = g.getFontMetrics().charWidth('c');
             //   int hzcharlen = g.getFontMetrics().getHeight();
                int hzcharlen = g.getFontMetrics().charWidth('汉');
               // int length =str.getBytes().length;
                
                int hzLen=0;
                if (str==null){  
                    str="";
                }  
                boolean bDeal=false;
                if (!"".equals(str)){
                    awidth=0;
                    String temp="";
                    for(int i=0;i<str.length();i++)
                    {
                        char c =str.charAt(i); 
                        temp=temp+String.valueOf(c);
                        if (String.valueOf(c).getBytes().length>1){
                            hzLen++;
                            continue;
                        }
                        if(c=='\n'){
                            awidth =charlen*temp.getBytes().length; 
                            awidth =awidth+ Math.round(hzLen*hzScale);
                            int line = awidth/columnWidth;   
                            line++;
                            lines= lines+ line;
                            hzLen=0;
                            awidth=0;
                            temp="";
                            bDeal=true;
                            continue;
                        }
                        if(c=='\r') {     
                            continue;
                        }
                        if(isHZChar(c)){                            
                            hzLen++;  
                        }   
                        
                    }  
                }
                
                if (!bDeal){
                    awidth = g.getFontMetrics().stringWidth(str);
                    awidth =awidth+ Math.round(hzLen*hzScale);
                    lines = awidth/columnWidth; 
                    lines++;
                }
            
        }
        catch(Exception e){
            e.printStackTrace();            
        }
        
        if (lines<1) {
            lines=1;
        }
        return lines;
    }
    /**
     * 获取子集总行数
     * @param setbo
     * @param writer
     * @param rset
     * @param dbpre
     * @param obj_id
     * @throws GeneralException
     */
    private int getSubsetSumLines(TemplateSetBo setbo,TSubSetDomain subdom,
            ArrayList list,int fontsize)throws GeneralException
    {
        int sumRecordLines=0;
        String value="";
        int curRowLine=1;
        try{
            
            if(subdom.isBcolhead())
            {                       
                for(int i=0;i<subdom.getFieldfmtlist().size();i++)
                {
                    TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);   
                    int lines = getLines(fieldformat.getTitle(),fieldformat.getWidth(),
                            setbo.getFontname(),setbo.getFonteffect(),fontsize);                        
                    if (lines >curRowLine ) {
                        curRowLine =lines;
                    }
                }
                sumRecordLines = curRowLine;
            }   
            for(int i=0;i<list.size();i++)
            {
                curRowLine=1;
                HashMap map=(HashMap)list.get(i);
                for(int j=0;j<subdom.getFieldfmtlist().size();j++)
                {
                    TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(j);
                    String name=fieldformat.getName().toLowerCase();
                    String slop =fieldformat.getSlop();
                    if(map.get(name)==null) {
                        value="";
                    } else {
                        value=(String)map.get(name);
                    }
                    FieldItem item=DataDictionary.getFieldItem(name);
                    if(item!=null)
                    {
                        if("A".equalsIgnoreCase(item.getItemtype())&&(!"0".equalsIgnoreCase(item.getCodesetid()))){
                        	String value1="";
                        	if("UM".equals(item.getCodesetid())){   //当codesetid为UM时，先查询是否存在相应的部门，如果不存在则查询codesetid为UN的情况，得到对应的单位名称，liuzy 20150807
                        		value1=AdminCode.getCodeName(item.getCodesetid(), value);
                        		if("".equals(value1)){
                        			value1=AdminCode.getCodeName("UN", value);
                        		}
                        		value=value1;
                        	}else{
                                value=AdminCode.getCodeName(item.getCodesetid(), value);
                        	}
                        }
                        else if("D".equalsIgnoreCase(item.getItemtype())){
                            value=value.replace(".", "-");
                            if(slop!=null&&!"".equals(slop)){
                                value =setbo.formatDateFiledsetValue(value, "", Integer.parseInt(slop));
                            }
                        }
                        
                        String a_state="2";
                        String astate=this.userview.analyseFieldPriv(item.getItemid());
                        if("0".equals(astate)) {
                            astate=this.userview.analyseFieldPriv(item.getItemid(),0);  //员工自助权限
                        }
                        if(this.tablebo!=null&& "1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) {
                            astate="2";
                        }
                        if("0".equals(astate)) {
                            a_state="0";
                        }
                        if("0".equals(a_state)) {
                            value="";
                        }
                    }
                    
                    int lines = getLines(value,fieldformat.getWidth(),
                            setbo.getFontname(),setbo.getFonteffect(),fontsize);
                    if (lines >curRowLine ) {
                        curRowLine =lines;
                    }
                }
                sumRecordLines = sumRecordLines + curRowLine;                                
                
            }      
        }
        
        catch (Exception e)
        {
            e.printStackTrace();            
        }
        return sumRecordLines;
        
    }
	
	/**
	 * 取得单元格的内容
	 * @param setbo
	 * @param writer
	 * @param rset
	 * @param dbpre
	 * @param obj_id
	 * @throws GeneralException
	 */
	private void outCellContent(TemplateSetBo setbo,PdfWriter writer,RowSet rset,String dbpre,String obj_id, ArrayList numberRecParam,Document document)throws GeneralException
	{
		int fontsize;
		String fonteffect=null;	
		try
		{
			fontsize=setbo.getFontsize();
			if(fontsize>=8) {
                fontsize=fontsize+1;
            }
			if(fontsize<=6) {
                fontsize=fontsize-1;
            }
			
			fonteffect=String.valueOf(setbo.getFonteffect());
			PdfPCell cell=null;
			 HashMap recordMap = new HashMap();
             if(this.task_ids!=null&&this.task_ids.trim().length()>0) 
             {
            	ContentDAO dao=new ContentDAO(conn);
     			RowSet rowSet=null;
             	 //记录对应任务id
             	StringBuffer strins = new StringBuffer();
             	String arrayids[] = this.task_ids.split(",");
             	for(int a=0;a<arrayids.length;a++)//按任务号查询需要审批的对象20080418
 				{
 					if(a!=0) {
                        strins.append(",");
                    }
 					strins.append(arrayids[a]);
 				}
             	if(strins!=null&&!"0".equals(strins.toString().trim()))
             	{
	             	rowSet=dao.search("select * from t_wf_task_objlink where task_id in ("+strins+")");
	             	while(rowSet.next())
	             	{
	             		recordMap.put(rowSet.getString("seqnum"), rowSet.getString("task_id"));
	             	}
	             	if(rowSet!=null) {
                        rowSet.close();
                    }
             	}
             }
			if(!setbo.isSubflag())//如果是主集
			{
				/**单元格内容*/
				String state="2";
				if(setbo.getFlag()!=null&&("A".equalsIgnoreCase(setbo.getFlag())|| "B".equalsIgnoreCase(setbo.getFlag())|| "K".equalsIgnoreCase(setbo.getFlag())))
				{//如果是人员人员库、单位库、职位库
					String astate=this.userview.analyseFieldPriv(setbo.getField_name());
	        //		if(astate.equals("0"))
	        //			astate=this.userview.analyseFieldPriv(setbo.getField_name(),0);	//员工自助权限 
				
					if("1".equals(this.type)) {
                        astate="2";
                    }
			//组织机构特殊指标的处理 codesetid,codeitemdesc,corcode,parentid,start_date	xgq20110225（目前这些指标没办法授权）等待后续程序开发后放开
					if("codesetid,codeitemdesc,corcode,parentid,start_date".indexOf(setbo.getField_name())!=-1) {
                        astate="2";
                    }
					if(this.tablebo!=null&& "1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) {
                        astate="2";
                    }
					if("0".equals(astate)) {
                        state="0";
                    }
				}
				String strc="";
				 if(setbo.getFlag()!=null&& "S".equals(setbo.getFlag())){
						
				 }else{
					 strc=setbo.getCellContent(dbpre,obj_id,rset,this.userview,this.tablebo); 
				 }
						 //计算公式 wangrd 2014-01-04
                 if("C".equals(setbo.getFlag())){
                     String pattern = "###"; 
                     TSyntax tsyntax = new TSyntax();    
                     tsyntax.Lexical(setbo.getFormula());
                     tsyntax.SetVariableValue(numberRecParam);
                     tsyntax.DoWithProgram();
                     int decimal = setbo.getDisformat();
                     pattern = "###"; //浮点数的精度
                     if (decimal > 0) {
                         pattern += ".";
                     }
                     for (int i = 0; i < decimal; i++) {
                         pattern += "0";
                     }
                     double dValue =0;
                     if (tsyntax.m_strResult != null && tsyntax.m_strResult.length() > 0) {
                         dValue =Double.parseDouble(tsyntax.m_strResult);
                     }
                     strc = new DecimalFormat(pattern).format(dValue);
                 }
				 
				strc=strc.replaceAll("%26lt;","<");
				strc=strc.replaceAll("%26gt;",">");
				  HashMap filedPrivMap=new HashMap(); //节点下指标权限
				  HashMap templateFieldPriv_node=new HashMap();  //节点下指标权限
            	String task_id= "";//rset.getString("task_id");
            	boolean flag2 = false;
				if(rset.isBeforeFirst()){
					if(rset.next()){
					flag2 = true;	
					}
				}
            	if(recordMap!=null&&recordMap.get(rset.getString("seqnum"))!=null){
            		task_id = ""+recordMap.get(rset.getString("seqnum"));
            	}else{
            		task_id = this.task_ids.split(",")[0];
            	}
            	if(flag2) {
                    rset.previous();
                }
            	if(!"1".equals(this.type)&&task_id!=null&&task_id.length()>0){
	            	if(templateFieldPriv_node.get(task_id)==null)
	            	{
	            		HashMap filedPriv=getFieldPriv(task_id,conn);
	            		if(filedPriv.size()!=0) {
                            templateFieldPriv_node.put(task_id,filedPriv);
                        }
	            	}
	            	if(templateFieldPriv_node.get(task_id)!=null) {
                        filedPrivMap=(HashMap)templateFieldPriv_node.get(task_id);
                    }
	            
					if(setbo.getFlag()!=null&&!setbo.isSubflag()&&("A".equals(setbo.getFlag())|| "B".equals(setbo.getFlag())|| "K".equals(setbo.getFlag()))&&filedPrivMap.size()>0&&filedPrivMap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate())!=null)
	            	{
	            		String editable=(String)filedPrivMap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate()); //	//0|1|2(无|读|写)
	            		if(editable!=null) {
                            state=editable;
                        }
	            		
	            	}
            	}
				if("0".equals(state)) {
                    strc="";
                }
				
				if(setbo.getFlag()!=null&& "P".equals(setbo.getFlag())&&(!(strc==null|| "".equals(strc))))
				{//如果是照片
					/**strc返回的创建的照片的文件名*/
					Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+strc);
					image.scaleAbsolute(setbo.getRwidth(),setbo.getRheight());
					cell=new PdfPCell(image, false);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
					/**画一个边框*/
				}else if(setbo.getFlag()!=null&& "S".equals(setbo.getFlag()))
				{//如果是电子签章
					/**strc返回的创建的照片的文件名*/
					//进库找出所有的signature
					boolean flag = false;
					if(rset.isBeforeFirst()){
						if(rset.next()){
						flag = true;	
						}
					}
					
					String xml = Sql_switcher.readMemo(rset,"signature");
					if(xml.length()<1) {
                        return;
                    }
					this.signxml = xml;
					org.jdom.Document doc2=null;
					doc2 =PubFunc.generateDom(xml);;
					org.jdom.Element root = doc2.getRootElement();
				        List childlist = root.getChildren("record");
				        if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++){
								org.jdom.Element element=(org.jdom.Element)childlist.get(i);
								List childlist2 =element.getChildren("item");
								 if(childlist2!=null&&childlist2.size()>0)
									{
										for(int j=0;j<childlist2.size();j++){
											org.jdom.Element element2=(org.jdom.Element)childlist2.get(j);
											String SignatureID = element2.getAttributeValue("SignatureID");
											
											if(SignatureID.length()>0){
												String pointx  = element2.getAttributeValue("pointx");
												String pointy  = element2.getAttributeValue("pointy");
												int x=0;
												int y=0;
												if(pointx.length()>0){
													if(pointx.endsWith("px")) {
                                                        x = Integer.parseInt(pointx.substring(0,pointx.length()-2));
                                                    } else {
                                                        x = Integer.parseInt(pointx);
                                                    }
												}
												if(pointy.length()>0){
													if(pointy.endsWith("px")) {
                                                        y = Integer.parseInt(pointy.substring(0,pointy.length()-2));
                                                    } else {
                                                        y = Integer.parseInt(pointy);
                                                    }
												}
												String pageid = element2.getAttributeValue("PageID");
												int pid=0;
												if(pageid!=null&&pageid.length()>0) {
                                                    pid = Integer.parseInt(pageid);
                                                }
												
												File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
											      if (!tempFile.exists()) {  
											    	  	continue;
											            }  
												Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
												image.scaleAbsolute(96,96);
												cell=new PdfPCell(image, false);
												cell.setHorizontalAlignment(Element.ALIGN_CENTER);
												cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
												cell.setBorder(0);
											    PdfPTable table = new PdfPTable(1);	
											    table.setTotalWidth(1);
											    table.setLockedWidth(true);
											    table.addCell(cell);
//											    document.add(table);
											    table.writeSelectedRows(0, -1,x,this.wh[1]*(pid+1)-y,writer.getDirectContent());    //固定坐标
											}
										}
									}
							}
						}
				
					/**画一个边框*/
				    if(flag) {
                        rset.previous();
                    }
					
					return;
				}//电子签章 结束
				else//如果是文本
				{
				    int font_size=fontsize;
					if ("M".equals(setbo.getField_type())){//备注可能需要缩小字体 wangrd 2014-07-10
					     			         
			               font_size = getMemoFontSize(strc,setbo.getRwidth(),setbo.getRheight(),
		                            setbo.getFontname(),setbo.getFonteffect(),font_size);			              
					}							    
					    
				    Paragraph para=new Paragraph(strc,FontFamilyType.getFont(setbo.getFontname(),fonteffect,font_size));
				    /**
				     *必须按下面方法，增加段，要不然排列方式不生效
				     *通过"\n"进行换行处理 
				     */
				    cell=new PdfPCell(para);  
				    cell.setBorder(0);
				    setCellAlign(setbo.getAlign(),cell);	
				    cell.setFixedHeight(setbo.getRheight());
				    cell.setNoWrap(false);//
					
				}
				if(setbo.getFlag()!=null&& "S".equals(setbo.getFlag())&&"signature".equalsIgnoreCase(setbo.getField_name())){
					
				}else{//如果不是电子签章
		        PdfPTable table = new PdfPTable(1);	
		        table.setTotalWidth(setbo.getRwidth());
		        table.setLockedWidth(true);
		        table.addCell(cell);
//		        document.add(table);
		        table.writeSelectedRows(0, -1,setbo.getRleft(),this.wh[1]-setbo.getRtop(),writer.getDirectContent());    //固定坐标
				}
			}
			else//插入子集区域
			{
				String xmlparam=setbo.getXml_param();
				String sub_domain_id="";
				if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
					sub_domain_id="_"+setbo.getSub_domain_id();
				}
				String setName =setbo.getSetname();
				String field_name="t_"+setbo.getSetname()+sub_domain_id+"_"+setbo.getChgstate();
//				System.out.println("||"+field_name);
				TSubSetDomain subdom=new TSubSetDomain(xmlparam);
				int columns=subdom.getFieldfmtlist().size();
				if(columns==0) {
                    return;
                }
		        subdom.reSetWidth(setbo.getRwidth());
		        float[] fcolumns=subdom.getColumns();
		        PdfPTable table = new PdfPTable(fcolumns/*columns*/);	
		        table.setTotalWidth(setbo.getRwidth());
		        table.setLockedWidth(true);
	           
		        boolean flag = false;
                if(rset.isBeforeFirst()){
                    if(rset.next()){
                    flag = true;    
                    }
                }
                ArrayList list=new ArrayList();
                //linbz 28653
                if(setName!=null&&setName.indexOf("attachment")>-1){//附件模拟子集
                	TemplatePageBo templatePageBo = new TemplatePageBo(this.conn,this.tabid,0);
                	list = templatePageBo.getAttachRecordlist(rset.getInt("ins_id"),setName,dbpre,obj_id,this.userview);
                }else{
	                String content=Sql_switcher.readMemo(rset, field_name.toLowerCase());
	                list=subdom.getRecordPdfList(content);
                }
                String value="";
                //自动缩放每行高度 及字体大小
                int cell_height=20;
                if (fontsize<10) {
                    cell_height=16;
                }
                if (fontsize<11) {
                    cell_height=18 ;
                } else if (fontsize>16) {
                    cell_height =25;
                } else if (fontsize>18) {
                    cell_height =30;
                } else if (fontsize>=20) {
                    cell_height =40;
                }

                int font_size=fontsize;
                int rheight=setbo.getRheight();
                int sumLines =getSubsetSumLines(setbo, subdom, list, font_size);
                if(rheight/cell_height<sumLines)
                {
                    while(true)
                    {
                        cell_height=cell_height-1;
                        font_size=font_size-1;
                        sumLines =getSubsetSumLines(setbo, subdom, list, font_size);
                        if(rheight/cell_height>=sumLines)
                        {
                            break;
                        }
                        if (font_size <3) {break;}
                    }
                    
                }
		        fontsize = font_size;
		        
		        if(subdom.isBcolhead())
		        {   
			        for(int i=0;i<subdom.getFieldfmtlist().size();i++)
			        {
			        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);    	
						Paragraph para=new Paragraph(fieldformat.getTitle(),FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
						/**
						 *必须按下面方法，增加段，要不然排列方式不生效
						 *通过"\n"进行换行处理 
						 */
						cell=new PdfPCell(para); 
						if(subdom.isBvl()&&subdom.isBhl()) {
                            cell.setBorder(15);
                        } else if(subdom.isBhl()&&!subdom.isBvl()) {
                            cell.setBorder(3);
                        } else if(!subdom.isBhl()&&subdom.isBvl()) {
                            cell.setBorder(12);
                        } else {
                            cell.setBorder(0);
                        }
					//	setCellAlign(1/*fieldformat.getAlign()*/,cell);
                        setSubSetHAlign(1,cell); //居中  
                        setSubSetVAlign(1,cell); //居中 
						int lines = getLines(fieldformat.getTitle(),fieldformat.getWidth(),
						        setbo.getFontname(),setbo.getFonteffect(),fontsize);
					cell.setFixedHeight(cell_height*lines/*setbo.getRheight()*/);
				        cell.setNoWrap(false);//
				        table.addCell(cell);

			        }
		        }
		        /**内容区域*/
		        if(setbo.getNhide()==0)
		        {
		        	
		        	String state="2";
					if(setbo.getFlag()!=null&& "A".equalsIgnoreCase(setbo.getFlag())|| "B".equalsIgnoreCase(setbo.getFlag())|| "K".equalsIgnoreCase(setbo.getFlag()))
					{
                		String setname=setbo.getSetname();
                		String astate=this.userview.analyseTablePriv(setname.toUpperCase());
                		if("0".equals(astate)) {
                            astate=this.userview.analyseTablePriv(setname.toUpperCase(),0);//员工自助权限
                        }
                		if("1".equals(this.type)) {
                            astate="2";
                        }
                		if(this.tablebo!=null&& "1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) {
                            astate="2";
                        }
                		if("0".equalsIgnoreCase(astate)) {
                            state="0";
                        }
					}
		        	if(!"0".equals(state))
		        	{
					        for(int i=0;i<list.size();i++)
					        {
					        	HashMap map=(HashMap)list.get(i);
						        for(int j=0;j<subdom.getFieldfmtlist().size();j++)
						        {
						        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(j);
						        	String name=fieldformat.getName().toLowerCase();
						        	String slop =fieldformat.getSlop();
						        	if(map.get(name)==null) {
                                        value="";
                                    } else {
                                        value=(String)map.get(name);
                                    }
									FieldItem item=DataDictionary.getFieldItem(name);
									//linbz 28653 附件模拟子集时只需按文本输出即可
									if(item!=null && setName.indexOf("attachment")==-1)
									{
										if("A".equalsIgnoreCase(item.getItemtype())&&(!"0".equalsIgnoreCase(item.getCodesetid()))){
											String value1="";
			                        	    if("UM".equals(item.getCodesetid())){   //当codesetid为UM时，先查询是否存在相应的部门，如果不存在则查询codesetid为UN的情况，得到对应的单位名称，liuzy 20150807
											   value1=AdminCode.getCodeName(item.getCodesetid(), value);
											   if("".equals(value1)){
				                        			value1=AdminCode.getCodeName("UN", value);
				                        		}
				                        		value=value1;
			                        	    }else{
			                                    value=AdminCode.getCodeName(item.getCodesetid(), value);
			                            	}  
			                        	}
										else if("D".equalsIgnoreCase(item.getItemtype())){
											value=value.replace(".", "-");
											if(slop!=null&&!"".equals(slop)){
												value =setbo.formatDateFiledsetValue(value, "", Integer.parseInt(slop));
											}
										}
										
										String a_state="2";
										String astate=this.userview.analyseFieldPriv(item.getItemid());
							        	if("0".equals(astate)) {
                                            astate=this.userview.analyseFieldPriv(item.getItemid(),0);	//员工自助权限
                                        }
							        	if(this.tablebo!=null&& "1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) {
                                            astate="2";
                                        }
							        	if("0".equals(astate)) {
                                            a_state="0";
                                        }
										if("0".equals(a_state)) {
                                            value="";
                                        }
										
										
									}
						        	Paragraph para=new Paragraph(value,FontFamilyType.getFont(setbo.getFontname(),fonteffect,font_size));
									cell=new PdfPCell(para);  
									if(subdom.isBvl()&&subdom.isBhl()) {
                                        cell.setBorder(15);
                                    } else if(subdom.isBhl()&&!subdom.isBvl()) {
                                        cell.setBorder(3);
                                    } else if(!subdom.isBhl()&&subdom.isBvl()) {
                                        cell.setBorder(12);
                                    } else {
                                        cell.setBorder(0);
                                    }
									/*
									if(item!=null)
									{
										if(item.getItemtype().equalsIgnoreCase("A")||item.getItemtype().equalsIgnoreCase("M"))
											setCellAlign(6,cell);	
										else if(item.getItemtype().equalsIgnoreCase("N"))
											setCellAlign(8,cell);
										else
											setCellAlign(1,cell);
									}	
									else
										setCellAlign(1,cell);	*/
									setSubSetHAlign(fieldformat.getAlign(),cell);	
									setSubSetVAlign(fieldformat.getValign(),cell);	
									//int lines = getLines(value,fontsize,fieldformat.getWidth());   
									int lines = getLines(value,fieldformat.getWidth(),
			                                setbo.getFontname(),setbo.getFonteffect(),fontsize);

							        cell.setFixedHeight(cell_height*lines);							     
							        cell.setNoWrap(false);//
							        table.addCell(cell);						        	
						        }
					        }	
					     if(flag) {
                             rset.previous();
                         }
					
		        	}
					
		        }
//		        document.add(table);
				/**输出内容结束*/
		        table.writeSelectedRows(0, -1,setbo.getRleft(),this.wh[1]-setbo.getRtop(),writer.getDirectContent());    //固定坐标
		        
			}
		}
		catch(Exception	ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}		
	}
	/**
	 * 画单元格的内容 zhaoxg add 2015-3-30
	 * @param setbo
	 * @param writer
	 * @param rset
	 * @param dbpre
	 * @param obj_id
	 * @throws GeneralException
	 */
	private void newOutCellContent(ArrayList pagelist,PdfWriter writer,RowSet rset,String dbpre,String obj_id, ArrayList numberRecParam,Document document,ArrayList signaboutlist,String enterflag)throws GeneralException
	{
		TemplateSetBo setbo = (TemplateSetBo) pagelist.get(0);
		int fontsize;
		String fonteffect=null;
        FileOutputStream out = null;
		try
		{
			fontsize=setbo.getFontsize();
			if(fontsize>=8) {
                fontsize=fontsize+1;
            }
			if(fontsize<=6) {
                fontsize=fontsize-1;
            }
			
			fonteffect=String.valueOf(setbo.getFonteffect());
			PdfPCell cell=null;
			 HashMap recordMap = new HashMap();
             if(this.task_ids!=null&&this.task_ids.trim().length()>0) 
             {
             	ContentDAO dao=new ContentDAO(conn);
             	RowSet rowSet=null;
             	 //记录对应任务id
             	StringBuffer strins = new StringBuffer();
             	String arrayids[] = this.task_ids.split(",");
             	for(int a=0;a<arrayids.length;a++)//按任务号查询需要审批的对象20080418
 				{
 					if(a!=0) {
                        strins.append(",");
                    }
 					strins.append(arrayids[a]);
 				}
             	if(strins!=null&&!"0".equals(strins.toString().trim()))
             	{
	             	 rowSet=dao.search("select * from t_wf_task_objlink where task_id in ("+strins+")");
	             	while(rowSet.next())
	             	{
	             		recordMap.put(rowSet.getString("seqnum"), rowSet.getString("task_id"));
	             	}
	             	if(rowSet!=null) {
                        rowSet.close();
                    }
             	}
             }
			if(!setbo.isSubflag())//如果是主集
			{
				/**单元格内容*/
				String state="2";
				if(setbo.getFlag()!=null&&("A".equalsIgnoreCase(setbo.getFlag())|| "B".equalsIgnoreCase(setbo.getFlag())|| "K".equalsIgnoreCase(setbo.getFlag())))
				{//如果是人员人员库、单位库、职位库
					String astate=this.userview.analyseFieldPriv(setbo.getField_name());
	        //		if(astate.equals("0"))
	        //			astate=this.userview.analyseFieldPriv(setbo.getField_name(),0);	//员工自助权限 
				
					if("1".equals(this.type)) {
                        astate="2";
                    }
			//组织机构特殊指标的处理 codesetid,codeitemdesc,corcode,parentid,start_date	xgq20110225（目前这些指标没办法授权）等待后续程序开发后放开
					if("codesetid,codeitemdesc,corcode,parentid,start_date".indexOf(setbo.getField_name())!=-1) {
                        astate="2";
                    }
					if(this.tablebo!=null&& "1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) {
                        astate="2";
                    }
					if("0".equals(astate)) {
                        state="0";
                    }
				}
				String strc="";
				 if(setbo.getFlag()!=null&& "S".equals(setbo.getFlag())){
						
				 }else{
					 strc=setbo.getCellContent(dbpre,obj_id,rset,this.userview,this.tablebo); 
				 }
						 //计算公式 wangrd 2014-01-04
                 if("C".equals(setbo.getFlag())){
                     String pattern = "###"; 
                     TSyntax tsyntax = new TSyntax();    
                     tsyntax.Lexical(setbo.getFormula());
                     tsyntax.SetVariableValue(numberRecParam);
                     tsyntax.DoWithProgram();
                     int decimal = setbo.getDisformat();
                     pattern = "###"; //浮点数的精度
                     if (decimal > 0) {
                         pattern += ".";
                     }
                     for (int i = 0; i < decimal; i++) {
                         pattern += "0";
                     }
                     double dValue =0;
                     if (tsyntax.m_strResult != null && tsyntax.m_strResult.length() > 0) {
                         dValue =Double.parseDouble(tsyntax.m_strResult);
                     }
                     strc = new DecimalFormat(pattern).format(dValue);
                 }
				 
				strc=strc.replaceAll("%26lt;","<");
				strc=strc.replaceAll("%26gt;",">");
				  HashMap filedPrivMap=new HashMap(); //节点下指标权限
				  HashMap templateFieldPriv_node=new HashMap();  //节点下指标权限
            	String task_id= "";//rset.getString("task_id");
            	boolean flag2 = false;
				if(rset.isBeforeFirst()){
					if(rset.next()){
					flag2 = true;	
					}
				}
            	if(recordMap!=null&&recordMap.get(rset.getString("seqnum"))!=null){
            		task_id = ""+recordMap.get(rset.getString("seqnum"));
            	}else{
            		task_id = this.task_ids.split(",")[0];
            	}
            	if(flag2) {
                    rset.previous();
                }
            	if(!"1".equals(this.type)&&task_id!=null&&task_id.length()>0){
	            	if(templateFieldPriv_node.get(task_id)==null)
	            	{
	            		HashMap filedPriv=getFieldPriv(task_id,conn);
	            		if(filedPriv.size()!=0) {
                            templateFieldPriv_node.put(task_id,filedPriv);
                        }
	            	}
	            	if(templateFieldPriv_node.get(task_id)!=null) {
                        filedPrivMap=(HashMap)templateFieldPriv_node.get(task_id);
                    }
	            
					if(setbo.getFlag()!=null&&!setbo.isSubflag()&&("A".equals(setbo.getFlag())|| "B".equals(setbo.getFlag())|| "K".equals(setbo.getFlag()))&&filedPrivMap.size()>0&&filedPrivMap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate())!=null)
	            	{
	            		String editable=(String)filedPrivMap.get(setbo.getField_name().toLowerCase()+"_"+setbo.getChgstate()); //	//0|1|2(无|读|写)
	            		if(editable!=null) {
                            state=editable;
                        }
	            		
	            	}
            	}
				if("0".equals(state)) {
                    strc="";
                }
				
				if(setbo.getFlag()!=null&& "P".equals(setbo.getFlag())&&(!(strc==null|| "".equals(strc))))
				{//如果是照片
					/**strc返回的创建的照片的文件名*/
					Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+strc);
					image.scaleAbsolute(setbo.getRwidth(),setbo.getRheight());
					cell=new PdfPCell(image, false);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
					/**画一个边框*/
				}else if(setbo.getFlag()!=null&& "S".equals(setbo.getFlag())&& "0".equals(enterflag))
				{//如果是电子签章
					/**strc返回的创建的照片的文件名*/
					//进库找出所有的signature
					boolean flag = false;
					if(rset.isBeforeFirst()){
						if(rset.next()){
						flag = true;	
						}
					}
					
					String xml = Sql_switcher.readMemo(rset,"signature");
					if(xml.length()<1) {
                        return;
                    }
					this.signxml = xml;
					org.jdom.Document doc2=null;
					doc2 =PubFunc.generateDom(xml);;
					org.jdom.Element root = doc2.getRootElement();
				        List childlist = root.getChildren("record");
				        if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++){
								org.jdom.Element element=(org.jdom.Element)childlist.get(i);
								List childlist2 =element.getChildren("item");
								 if(childlist2!=null&&childlist2.size()>0)
									{
										for(int j=0;j<childlist2.size();j++){
											org.jdom.Element element2=(org.jdom.Element)childlist2.get(j);
											String SignatureID = element2.getAttributeValue("SignatureID");
											
											if(SignatureID.length()>0){
												String pointx  = element2.getAttributeValue("pointx");
												String pointy  = element2.getAttributeValue("pointy");
												int x=0;
												int y=0;
												if(pointx.length()>0){
													if(pointx.endsWith("px")) {
                                                        x = Integer.parseInt(pointx.substring(0,pointx.length()-2));
                                                    } else {
                                                        x = Integer.parseInt(pointx);
                                                    }
												}
												if(pointy.length()>0){
													if(pointy.endsWith("px")) {
                                                        y = Integer.parseInt(pointy.substring(0,pointy.length()-2));
                                                    } else {
                                                        y = Integer.parseInt(pointy);
                                                    }
												}
												String pageid = element2.getAttributeValue("PageID");
												int pid=0;
												if(pageid!=null&&pageid.length()>0) {
                                                    pid = Integer.parseInt(pageid);
                                                }
												
													File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
												    if (!tempFile.exists()) {  
												    	continue;
												    }  
												    Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
												   
												    image.scaleAbsolute(96,96);
													cell=new PdfPCell(image, false);
													cell.setHorizontalAlignment(Element.ALIGN_CENTER);
													cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
													cell.setBorder(0);
												    PdfPTable table = new PdfPTable(1);	
												    table.setTotalWidth(1);
												    table.setLockedWidth(true);
												    table.addCell(cell);
	//											    document.add(table);
												    table.writeSelectedRows(0, -1,x,this.wh[1]*(pid+1)-y,writer.getDirectContent());//固定坐标  
												    //table.writeSelectedRows(0, -1,x,this.wh[1]-y,writer.getDirectContent());//固定坐标  
											}
										}
									}
							}
						}
				
					/**画一个边框*/
				    if(flag) {
                        rset.previous();
                    }
				    
					return;
				}//电子签章 结束
				else//如果是文本
				{
					String field_name=(setbo.getField_name()!=null&&!"".equals(setbo.getField_name()))?setbo.getField_name().toLowerCase():"";
					FieldItem fielditem = null;
					if(field_name.length()>0) {
                        fielditem=DataDictionary.getFieldItem(field_name);
                    }
					if("0".equals(setbo.getCodeid())&&"A".equals(setbo.getField_type())&&"A".equals(setbo.getFlag())){
						if(fielditem!=null){
							if(fielditem.getItemlength()>=255)//大于255的字符型指标看做大文本处理
                            {
                                setbo.setField_type("M");
                            }
						}
					}
					if("M".equals(setbo.getField_type())&&strc.length()>0&&strc.indexOf("\n")>-1){//大文本处理前面空格问题 hej add 20161110
						String strcarr[] = strc.split("\n");
						String newstrc = "";
						for(String s:strcarr){
							int len = 0;
							int addlen = 0;
							String add = "";
							int oldlen = s.length();
							for(int i=0;i<s.length();i++){
					            if(s.charAt(i)!=' '){
				                    s=s.substring(i,s.length());
				                    break;
					             }
							}
							int newlen = s.length();
							if(oldlen>newlen){//前面有空格
								len = oldlen - newlen;//多少个空格
								addlen = len*2;
							}
							for(int j=0;j<addlen;j++){
								add+=" ";
							}
							s = add+s;
							newstrc+=s+"\n";
						}
						//newstrc = newstrc.substring(0,newstrc.length()-2);
						strc = newstrc;
					}
					
				    int font_size=fontsize;
					if ("M".equals(setbo.getField_type())){//备注可能需要缩小字体 wangrd 2014-07-10
					       	        
			               font_size = getMemoFontSize(strc,setbo.getRwidth(),setbo.getRheight(),
		                            setbo.getFontname(),setbo.getFonteffect(),font_size);	
					}							    
					    
				    Paragraph para=new Paragraph(strc,FontFamilyType.getFont(setbo.getFontname(),fonteffect,font_size));
				    /**
				     *必须按下面方法，增加段，要不然排列方式不生效
				     *通过"\n"进行换行处理 
				     */

				    cell=new PdfPCell(para);
//				    cell.setPadding(10);
				    cell.setBorder(0);
//				    cell.setLeading(1f, 1.2f);
				    setCellAlign(setbo.getAlign(),cell);	
				    if ("M".equalsIgnoreCase(setbo.getField_type()))//输出pdf大文本默认靠左显示	 hej 20161202
                    {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
				    if(fielditem!=null){
				    	if(!"M".equalsIgnoreCase(fielditem.getItemtype())){
				    		setCellAlign(setbo.getAlign(),cell);
				    	}
				    }
				    cell.setFixedHeight(setbo.getRheight());
				    cell.setNoWrap(false);//
					
				}
				if(setbo.getFlag()!=null&& "S".equals(setbo.getFlag())&&"signature".equalsIgnoreCase(setbo.getField_name())){
					
				}else{//如果不是电子签章
		        PdfPTable table = new PdfPTable(1);	
		        table.setTotalWidth(setbo.getRwidth());
		        table.setLockedWidth(true);
		        table.addCell(cell);
//		        document.add(table);
		        table.writeSelectedRows(0, -1,setbo.getRleft(),this.wh[1]-setbo.getRtop(),writer.getDirectContent());    //固定坐标
				}
				if(signaboutlist.size()>0&& "1".equals(enterflag)){//电子签章
					ContentDAO dao=new ContentDAO(conn);
	             	RowSet rowSet1=null;
					int diftop = 0;
					int difleft = 0;
					for(int b = 0;b<signaboutlist.size();b++){
						HashMap hm = (HashMap)signaboutlist.get(b);
						List l = (List)hm.get("setbo");
						TemplateSetBo ts = (TemplateSetBo)l.get(0);
						String SignatureID = (String)hm.get("SignatureID");
						String documentid = (String)hm.get("DocuemntID");
						int x = Integer.parseInt((String)hm.get("x"));
						int y = Integer.parseInt((String)hm.get("y"));
						if(setbo.getGridno()==ts.getGridno()&&setbo.getPagebo().getPageid()==ts.getPagebo().getPageid()){
							int settop = setbo.getRtop();
							int setleft = setbo.getRleft();
							int tstop = ts.getRtop();
							int tsleft = ts.getRleft();
							diftop = settop-tstop;
							difleft = setleft-tsleft;
							x = x+difleft;
							y = y+diftop;
							if(this.signtype==1){
								String signature = "";
								//表单原文
								String plain = "hjsoft";
								//签章值
								rowSet1=dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'");
								if(rowSet1.next()){
									signature = rowSet1.getString("signaturetext");
								}
								SealVerify sealVerify = new SealVerify();
								sealVerify.setCoding("GBK");
								
								if (!sealVerify.doSealVerify(plain, signature)) {
									//System.out.println("验证信息出错");
									return;
								} else {
									//System.out.println("验证信息成功");
								}
								
								//得到签章图片（Base64编码）
								String PicData=sealVerify.getPicData(plain, signature);
								//将base64编码生成图片
								if (PicData == null){ // 图像数据为空
							       return;
							    }
						        // Base64解码
						        byte[] bytes = Base64.decodeBase64(PicData);
						        for (int k = 0; k < bytes.length; ++k) {
						            if (bytes[k] < 0) {// 调整异常数据
						                bytes[k] += 256;
						            }
						        }
						        File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
							    if (!file.exists()) {  
							    	// 生成jpeg图片
							        out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
							        out.write(bytes);
							        out.flush();
							    }
								File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
							    if (!tempFile.exists()) {  
							    	continue;
							    }  
							    Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
							    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
								image.scaleAbsolute(sourceImg.getWidth(),sourceImg.getHeight());
								cell=new PdfPCell(image, false);
								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
								cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
								cell.setBorder(0);
							    PdfPTable table = new PdfPTable(1);	
							    table.setTotalWidth(1);
							    table.setLockedWidth(true);
							    table.addCell(cell);
							    //table.writeSelectedRows(0, -1,x,this.wh[1]*(pid+1)-y,writer.getDirectContent());//固定坐标  
							    table.writeSelectedRows(0, -1,x,this.wh[1]-y,writer.getDirectContent());//固定坐标 
							}
							else if(this.signtype==0){
								File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
							    if (!tempFile.exists()) {  
							    	continue;
							    }  
							    Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".jpg");
							    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
								image.scaleAbsolute(sourceImg.getWidth(),sourceImg.getHeight());
							    //image.scaleAbsolute(96,96);
								cell=new PdfPCell(image, false);
								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
								cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
								cell.setBorder(0);
							    PdfPTable table = new PdfPTable(1);	
							    table.setTotalWidth(1);
							    table.setLockedWidth(true);
							    table.addCell(cell);
							    table.writeSelectedRows(0, -1,x,this.wh[1]-y,writer.getDirectContent());//固定坐标 
							}
						}
					}
					if(rowSet1!=null) {
                        rowSet1.close();
                    }
				}
			}
			else//插入子集区域
			{
				ArrayList aa = (ArrayList) pagelist.get(1);
				for(int i=0;i<aa.size();i++){
					PdfPTable table = (PdfPTable) aa.get(i);
					/**输出内容结束*/
			        table.writeSelectedRows(0, -1,setbo.getRleft(),this.wh[1]-setbo.getRtop(),writer.getDirectContent());    //固定坐标

			        String xmlparam = setbo.getXml_param();
			        TSubSetDomain subdom=new TSubSetDomain(xmlparam);
			        subdom.reSetWidth(setbo.getRwidth());
			        if(subdom.isBvl()){
			            PdfContentByte cb =  writer.getDirectContent();
			            cb.setLineWidth(1f);
			            int left = setbo.getRleft();
			            for(int t=0;t<subdom.getFieldfmtlist().size();t++){
			            	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(t); 
			            	left +=fieldformat.getWidth();
			            	//liuyz bug23966
			            	if(left!=setbo.getRwidth()+setbo.getRleft()&&t==subdom.getFieldfmtlist().size()-1)
			            	{
			            		left=setbo.getRwidth()+setbo.getRleft();
			            	}
							cb.moveTo(left,this.wh[1]-setbo.getRtop());
							cb.lineTo(left,this.wh[1]-(setbo.getRtop()+setbo.getRheight()));
			            }
			            cb.stroke();
			        }
				}
			}
		}
		catch(Exception	ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
		    PubFunc.closeResource(out);
        }
	}
	/**
	 *单元格内容的排列方式
	 * @param align =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
	 * @param cell
	 */
	private void setCellAlign(int align, PdfPCell cell) {

		if(align==0)   
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);    //基于最合适的
		}
		else if(align==1)
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		}
		else if(align==2)
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		}
		else if(align==3)
		{
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		}
		else if(align==4)
		{
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		}
		else if(align==5)
		{
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		}
		else if(align==6)
		{
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		else if(align==7)
		{
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);   //居中
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		else if(align==8)
		{
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);   //居右
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
	}
	
    /**
     *子集水平单元格内容的排列方式
     * @param align =
     * @param cell
     */
    private void setSubSetHAlign(int align, PdfPCell cell) {

        if(align==0)   
        {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);    
        }
        else if(align==1)
        {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        else if(align==2)
        {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
    }
    
    /**
     *子集垂直单元格内容的排列方式
     * @param align =
     * @param cell
     */
    private void setSubSetVAlign(int align, PdfPCell cell) {

        if(align==0)   
        {
            cell.setVerticalAlignment(Element.ALIGN_TOP);
        }
        else if(align==1)
        {
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        }
        else if(align==2)
        {
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        }
    }
	/**
	 * 输出单元格的内容及画单元格线
	 * @param dbpre 应用库前缀
	 * @param obj_id 对象号
	 * @param writer
	 * @param pageb 
	 * @throws GeneralException
	 */
	private void outPdfPageGrid(String dbpre,String obj_id,PdfWriter writer, TemplatePageBo pageb,RowSet rset,Document document)throws GeneralException
	{
		int sx=0,sy=0;
		int ex=0,ey=0;
		try
		{
			ArrayList celllist=pageb.getAllCell();
			// 加载数值指标列表，计算公式用 wangrd 2014-01-04
			boolean bHaveCalcItem=false;
            ArrayList numberRecParam=new ArrayList();  
            double fValue = 0.0f;
            if (celllist.size() > 0) {          
                if(rset.isBeforeFirst()){
                    rset.next();                        
                }                      
               for (int i = 0; i < celllist.size(); i++) {
                   TemplateSetBo cell = (TemplateSetBo) celllist.get(i);
                   String flag =cell.getFlag();
                   if("C".equalsIgnoreCase(flag)){
                       bHaveCalcItem=true;
                   }
                   String fldname =cell.getField_name();
                   String fldtype =cell.getField_type();
                   int chgstate = cell.getChgstate();   
                   if (!"V".equals(flag)) {
                       fldname =fldname+"_"+ String.valueOf(chgstate);
                   }
                   fldname =fldname.toLowerCase();
                   if ("N".equalsIgnoreCase(fldtype)) {
                       
                       TRecParamView recP = new TRecParamView();
                       fValue = rset.getDouble(fldname);
                       recP.setBflag(true);
                       recP.setFvalue(String.valueOf(fValue));
                       recP.setNid(cell.getGridno());
                       numberRecParam.add(recP);
                   }
               }
            }   
			
			PdfContentByte cb =  writer.getDirectContent();
			cb.setLineWidth(1f);
			for(int i=0;i<celllist.size();i++)
			{
				TemplateSetBo setbo=(TemplateSetBo)celllist.get(i);
				setbo.setOperationtype(this.tablebo.getOperationtype());
				java.awt.Rectangle rect=setbo.getRect();
				sx=setbo.getRleft();
				sy=setbo.getRtop();
				ex=sx+setbo.getRwidth();
				ey=sy+setbo.getRheight();
				/**输出内容，再画线，可以解决照片边框的问题*/
				outCellContent(setbo,writer,rset,dbpre,obj_id,numberRecParam,document);				
				/**
				 *  ________
				 * |           画线（顶部和左边线）
				 * 
				 */
				if(setbo.getT()==1)
				{
					cb.moveTo(sx,this.wh[1]-sy);
					cb.lineTo(ex,this.wh[1]-sy);
				}
				if(setbo.getL()==1)
				{
					cb.moveTo(sx,this.wh[1]-sy);
					cb.lineTo(sx,this.wh[1]-ey);
				}
				
				
				if(setbo.getB()==1)  //dengcan  010-5-31
				{
					cb.moveTo(sx,this.wh[1]-ey);
					cb.lineTo(ex,this.wh[1]-ey);
					
				}
				
				
				
				
				
				if((ex==(rect.x+rect.width))&&setbo.getR()==1) //右边框
				{
					cb.moveTo(ex,this.wh[1]-sy);
					cb.lineTo(ex,this.wh[1]-ey);
				}
				if((ey==(rect.y+rect.height))&&setbo.getB()==1)//底边框
				{
					cb.moveTo(sx,this.wh[1]-ey);
					cb.lineTo(ex,this.wh[1]-ey);					
				}
				cb.stroke();
			}//for loop end
			/**再画虚线,右边和底部*/
			cb.setLineWidth(0f);
			cb.setColorStroke(new Color(0,0,0));
			for(int i=0;i<celllist.size();i++)
			{
				TemplateSetBo setbo=(TemplateSetBo)celllist.get(i);
				sx=setbo.getRleft();
				sy=setbo.getRtop();
				ex=sx+setbo.getRwidth();
				ey=sy+setbo.getRheight();	
				if(setbo.getR()==0)
				{
					cb.moveTo(ex,this.wh[1]-sy);
					cb.lineTo(ex,this.wh[1]-ey);					
				}
				if(setbo.getB()==0)
				{
					cb.moveTo(sx,this.wh[1]-ey);
					cb.lineTo(ex,this.wh[1]-ey);					
				}
			}
 
		}
		catch(Exception	ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}
	}
	/**
	 * 输出标题内容,通过表格控制定位
	 * @param doc
	 * @param writer
	 * @param pagebo
	 * @throws GeneralException
	 */
	private void outPdfPageTitle(PdfWriter writer, TemplatePageBo pagebo,int ins_id)throws GeneralException
	{
		int fontsize;
		String fonteffect=null;
		try
		{
			ArrayList titlelist=pagebo.getAllTitle();
			for(int i=0;i<titlelist.size();i++)
			{
				TTitle title=(TTitle)titlelist.get(i);
				PdfPCell cell=null;
				if(title.getFlag()==7) //图片
				{
					 String ext=title.getPattern("ext",title.getExtendattr());
					 title.setCon(this.conn);
					 String fileName=title.createPhotoFile(ext);
					 
					 if(fileName!=null&&fileName.length()>0)
					 {
					  /**strc返回的创建的照片的文件名*/
						Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
						image.scaleAbsolute(title.getRwidth(),title.getRheight());
						cell=new PdfPCell(image, false);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
					 }
					 else
					 {
						 fontsize=title.getFontsize();
						 fonteffect=String.valueOf(title.getFonteffect());
						 Paragraph para=new Paragraph(title.getOutText(this.userview,this.pages,this.currpage),FontFamilyType.getFont(title.getFontname(),fonteffect,fontsize));
						 cell = new PdfPCell(para);
					 }
					
				}
				else
				{
					fontsize=title.getFontsize();
					title.setCon(this.conn);
					title.setIns_id(ins_id);
					fonteffect=String.valueOf(title.getFonteffect());
					Paragraph para=new Paragraph(title.getOutText(this.userview,this.pages,this.currpage),FontFamilyType.getFont(title.getFontname(),fonteffect,fontsize));
					cell = new PdfPCell(para);
				}
		        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        cell.setBorder(0);
		        cell.setNoWrap(true);//
		        PdfPTable table = new PdfPTable(1);	
		        if(title.getRwidth()==0) {
                    continue;
                }
		        table.setTotalWidth(title.getRwidth());
		        table.setLockedWidth(false);
		        table.addCell(cell);
		        table.writeSelectedRows(0, -1,title.getRleft(),this.wh[1]-title.getRtop(),writer.getDirectContent());    //固定坐标
			}//for i loop end.
			//cb.endText();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/** 检查模板页是否与模板设置的纸张方向一致，不一致 返回true，需要模板页单独设置纸张方向。
	 * @param pgidlist
	 * @return
	 */
	public boolean checkPageOrientationIsDifferent(ArrayList pgidlist)
	{
    	boolean b=false;
		for(int s=0;s<pgidlist.size();s++){
			HashMap pgmap = (HashMap) pgidlist.get(s);
			String paperOrientation = (String)pgmap.get("paperOrientation");
			if (!"0".equals(paperOrientation) && !paperOrientation.equals(tablebo.getTable_vo().getInt("paperori")+"")){
				b=true;
				break;
			}
    	}
		return  b;
	}
	
	/** 从pagelist获取下一PDF页对应的模板页 从pgidlist中获取模板页的横纵向设置
	 * @param pgidlist
	 * @return
	 */
	public String getPageOrientation(ArrayList pgidlist,ArrayList pagelist,int padeIndex)
	{
		
		int curPageId=-1;
		ArrayList pglist=(ArrayList) pagelist.get(padeIndex);
        if (pglist.size() > 0) {          
           for (int t = 0; t < pglist.size(); t++) {
        	   ArrayList celllist =  (ArrayList) pglist.get(t);
        	   if(celllist.size()>2){
        		   continue;
        	   }
               TemplateSetBo cell = (TemplateSetBo) celllist.get(0);
               if (curPageId==-1){
            	   curPageId =cell.getPagebo().getPageid();			                	   
               }
           }
        }
		String paperOrientation="";
		for(int s=0;s<pgidlist.size();s++){
			HashMap pgmap = (HashMap) pgidlist.get(s);
			String pageid = (String)pgmap.get("pageid");
			if (pageid.equals(curPageId+"")){
				paperOrientation = (String)pgmap.get("paperOrientation");
				
			}
    	}
		return paperOrientation;
	}
	
	/***
	 * 对象列表
	 * @param objlist 存放的是:库前缀+用户编号
	 * 如果对调入人员时，不能对历史记录取数（也即档案中的数据不能取）
	 * @para infor =1人员,=2单位,=3职位
	 * @para inslist[0]=0 ,提交申请时的打印，=1审批过程中的打印
	 * @param enterflag 新人事异动与老人事异动区别标识 '0'老 '1'新
	 * @return
	 * @throws GeneralException
	 */
	public String outPdf(ArrayList objlist,int infor,ArrayList inslist,String enterflag)throws GeneralException
	{
		RecordVo tvo=tablebo.getTable_vo();
		FileOutputStream fs = null;
		Document document = null;
		String filename="";
		try
		{
			RowSet rs = null;
			RowSet rset=null;
			boolean isHasPageOFld=false;
			DbWizard dbw = new DbWizard(this.conn);
			if (dbw.isExistField("template_Page", "paperOrientation",false)) {
				isHasPageOFld=true;				
			}
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer();
			sql.append("select * from Template_Page where tabid=");
			sql.append(this.tabid);
			if("".equals(this.noshow_pageno))//如果有设置的不显示页签 优先走这个
            {
                sql.append(" and isprn<>0");
            }
			sql.append(" and "+Sql_switcher.isnull("ismobile", "0")+"<>1");
			rs=dao.search(sql.toString());
			ArrayList pgidlist = new ArrayList();
			String paperOrientation="";
			while(rs.next())
			{
				if(!"".equals(this.noshow_pageno)){//如果有设置的不显示页签 优先走这个
					String pageid =  String.valueOf(rs.getInt("pageid"));
					String pagearr [] = this.noshow_pageno.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint) {
                        continue;
                    }
				}
				sql.setLength(0);
				sql.append("select * from Template_Set where tabid=");
				sql.append(this.tabid);
				sql.append(" and pageid=");
				sql.append(rs.getInt("pageid"));
				sql.append(" order by rtop,rleft");
				rset=dao.search(sql.toString());
				
				sql.setLength(0);
				sql.append("select * from template_title where tabid=");
				sql.append(this.tabid);
				sql.append(" and pageid=");
				sql.append(rs.getInt("pageid"));
				sql.append(" order by rtop,rleft");
				RowSet roset=dao.search(sql.toString());
				HashMap map = new HashMap();
				map.put("pageid", rs.getInt("pageid")+"");
				map.put("context", rset);
				map.put("title", roset);
				if (isHasPageOFld) {					
					map.put("paperOrientation",rs.getInt("paperOrientation")+"");
					if ("".equals(paperOrientation)){
						paperOrientation=rs.getInt("paperOrientation")+"";
					}
				}
				else {
					map.put("paperOrientation","0");	
				}
				
				pgidlist.add(map);				
		   }
			boolean pageOriDiff =checkPageOrientationIsDifferent(pgidlist);
			if (pageOriDiff) {
				this.wh=getWidthHeight(tvo,paperOrientation);
			}
			else {
				this.wh=getWidthHeight(tvo,"0");
			}
			
	        Rectangle pageSize = new Rectangle(wh[0],wh[1]);
			document = new Document(pageSize);
			//String prefix="template_"+this.userview.getUserName();
			String prefix=this.tablebo.getName() + "_" + this.userview.getUserName();
			
			/**模板临时表名*/
			String tabname=null;
			/**根据第一个实例是否为0来分析，具体采用什么表*/
			int ins_id=Integer.parseInt((String)inslist.get(0));
			if(ins_id==0) {
                tabname=this.userview.getUserName()+"templet_"+this.tabid;
            } else {
                tabname="templet_"+this.tabid;
            }
			if (selfApply){
			    tabname="g_templet_"+this.tabid;
			}

		
//			ArrayList pagelist=this.tablebo.getAllTemplatePage(1);
			prefix= prefix.replace("/", "／");
		    String filePath = System.getProperty("java.io.tmpdir")+File.separator+prefix+".pdf";
	        File tempFile = new File(filePath);  
	        if(!tempFile.exists()) {  
	            try {  
	                tempFile.createNewFile();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	        }  
			/**创建临时文件*/
	       // File tempFile = File.createTempFile(prefix,".pdf", new File(System.getProperty("java.io.tmpdir")));
	        fs = new FileOutputStream(tempFile);
    		PdfWriter writer = PdfWriter.getInstance(document,fs);
			document.open();
			String obj_id=null;
			String dbpre=null;


			ArrayList paralist=new ArrayList();
			ArrayList signaboutlist = new ArrayList();
			String insid="";

			if(true){				
				for(int j=0;j<objlist.size();j++) //人员对象
				{
					buf.setLength(0);
					paralist.clear();
					insid=(String)inslist.get(j);
					obj_id=(String)objlist.get(j);
					if("1".equals(""+this.tablebo.getInfor_type())){
					if(infor==1)//对人员要进行分库
					{
						if(obj_id.length()<11) //为空，未选中人员
						{
						  dbpre="";
	 					  if(obj_id.length()!=8) //人员编号
						  {
	 							obj_id="-1"; //打印空表
						  }
						}
						else
						{
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
					
					if(ins_id!=0)
					{
						buf.append(" and ins_id =?");
						paralist.add(Integer.valueOf(insid));
					}
					rset=dao.search(buf.toString(),paralist);
					if("1".equals(enterflag)){
						if(rset.next()){
							String signature = rset.getString("signature");
							if(signature!=null&&!"".equals(signature)){
								signaboutlist = this.getSignAboutCell(signature,pgidlist);
							}
						}
					}
					ArrayList pagelist=this.getAllTemplatePageAndCell(1, rset, this.userview, this.wh,ins_id,pgidlist,dbpre,obj_id);
					if(pagelist.size()==0) {
                        throw new GeneralException(ResourceFactory.getProperty("error.printpages.zero"));
                    }
					this.pages=pagelist.size();
					int pageid = 1;//页码
					for(int i=0;i<pagelist.size();i++)//pdf的表页，有可能与实际模板页码不同，因为导出时数据多的时候需要延伸。
					{
						ArrayList pglist=(ArrayList) pagelist.get(i);
			            ArrayList numberRecParam=new ArrayList();  
			            ArrayList list=new ArrayList();  
			            double fValue = 0.0f;
			            if (pglist.size() > 0) {          
			                if(rset.isBeforeFirst()){
			                    rset.next();                        
			                }                      
			               for (int t = 0; t < pglist.size(); t++) {
			            	   ArrayList celllist =  (ArrayList) pglist.get(t);
			            	   if(celllist.size()>2){
			            		   continue;
			            	   }
			                   TemplateSetBo cell = (TemplateSetBo) celllist.get(0);
			                   list.add(cell);
			                   String flag =cell.getFlag();

			                   String fldname =cell.getField_name();
			                   String fldtype =cell.getField_type();
			                   int chgstate = cell.getChgstate();   
			                   if (!"V".equals(flag)) {
                                   fldname =fldname+"_"+ String.valueOf(chgstate);
                               }
			                   fldname =fldname.toLowerCase();
			                   if ("N".equalsIgnoreCase(fldtype)) {
			                       
			                       TRecParamView recP = new TRecParamView();
			                       fValue = rset.getDouble(fldname);
			                       recP.setBflag(true);
			                       recP.setFvalue(String.valueOf(fValue));
			                       recP.setNid(cell.getGridno());
			                       numberRecParam.add(recP);
			                   }
			               }
			            }
			            for(int x=0;x<pglist.size();x++){
			            	ArrayList templist = (ArrayList) pglist.get(x);
			            	if(templist.size()>2&& "remember".equals(templist.get(2))){
			            		continue;
			            	}
			            	if(templist.size()>2&& "title".equals(templist.get(2))){//标题单独处理
			            		TTitle title=(TTitle) templist.get(0);
								int fontsize;
								String fonteffect=null;
								PdfPCell cell=null;
								if(title.getFlag()==7) //图片
								{
									 String ext=title.getPattern("ext",title.getExtendattr());
									 title.setCon(this.conn);
									 String fileName=title.createPhotoFile(ext);
									 
									 if(fileName!=null&&fileName.length()>0)
									 {
									  /**strc返回的创建的照片的文件名*/
										Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
										image.scaleAbsolute(title.getRwidth(),title.getRheight());
										cell=new PdfPCell(image, false);
									 }
									 else
									 {
										 fontsize=title.getFontsize();
										 fonteffect=String.valueOf(title.getFonteffect());
										 Paragraph para=new Paragraph(title.getOutText(this.userview,pagelist.size(),x+1),FontFamilyType.getFont(title.getFontname(),fonteffect,fontsize));
										 cell = new PdfPCell(para);
									 }
									
								}
								else
								{
									Paragraph para = null;
									fontsize=title.getFontsize();
									title.setCon(this.conn);
									title.setIns_id(ins_id);
									fonteffect=String.valueOf(title.getFonteffect());
									if(title.getFlag()==5){
										para=new Paragraph(title.getOutText(this.userview,pagelist.size(),pageid),FontFamilyType.getFont(title.getFontname(),fonteffect,fontsize));
										pageid++;
									}else {
                                        para=new Paragraph(title.getOutText(this.userview,pagelist.size(),x+1),FontFamilyType.getFont(title.getFontname(),fonteffect,fontsize));
                                    }
									cell = new PdfPCell(para);
								}
						        cell.setBorder(0);
						        cell.setNoWrap(true);//
						        PdfPTable table = new PdfPTable(1);	
						        if(title.getRwidth()==0) {
                                    continue;
                                }
						        table.setTotalWidth(title.getRwidth());
						        table.setLockedWidth(false);
						        table.addCell(cell);
						        table.writeSelectedRows(0, -1,title.getRleft(),this.wh[1]-title.getRtop(),writer.getDirectContent());  
			            	}else{
			            		this.newOutCellContent(templist, writer, rset, dbpre, obj_id, numberRecParam, document,signaboutlist,enterflag);
			            		TemplateSetBo setbo = (TemplateSetBo) templist.get(0);
//			    				if(setbo.getHz().indexOf("出生年月")!=-1){
//			    					System.out.println("1212");
//			    				}
			    				int b=0;
			    				int l=0;
			    				int r=0;
			    				int t=0;
			    				
		    					b=getRlineForList(list,"b",setbo.getB(),setbo);
		    					l=getRlineForList(list,"l",setbo.getL(),setbo);
		    					r=getRlineForList(list,"r",setbo.getR(),setbo);
		    					t=getRlineForList(list,"t",setbo.getT(),setbo);
		    					setbo.setB(b);					
		    					setbo.setL(l);
		    					setbo.setR(r);
		    					setbo.setT(t);
//			            		java.awt.Rectangle rect=setbo.getRect();
					    		int sx=0,sy=0;
					    		int ex=0,ey=0;
					            PdfContentByte cb =  writer.getDirectContent();
								sx=setbo.getRleft();
								sy=setbo.getRtop();
								ex=sx+setbo.getRwidth();
								ey=sy+setbo.getRheight();
					            cb.setLineWidth(1f);
					            if(setbo.getT()==1)
								{
									cb.moveTo(sx,this.wh[1]-sy);
									cb.lineTo(ex,this.wh[1]-sy);
								}
								if(setbo.getL()==1)
								{
									cb.moveTo(sx,this.wh[1]-sy);
									cb.lineTo(sx,this.wh[1]-ey);
								}														
								if(setbo.getB()==1)
								{
									cb.moveTo(sx,this.wh[1]-ey);
									cb.lineTo(ex,this.wh[1]-ey);
									
								}
								if(/*(ex==(rect.x+rect.width))&&*/setbo.getR()==1) //右边框
								{
									cb.moveTo(ex,this.wh[1]-sy);
									cb.lineTo(ex,this.wh[1]-ey);
								}
								if(/*ey==(rect.y+rect.height))&&*/setbo.getB()==1)//底边框
								{
									cb.moveTo(sx,this.wh[1]-ey);
									cb.lineTo(ex,this.wh[1]-ey);					
								}
								cb.stroke();
			            	}
			            }					
			            if ((pageOriDiff)){
			            	HashMap map= null;
			            	if (i<pagelist.size()-1){
			            		paperOrientation= getPageOrientation(pgidlist,pagelist,i+1);
			            	}
			            	else if (i==pagelist.size()-1) {//最后一页，需要取第一页的设置
			            		map=(HashMap) pgidlist.get(0);
			            		if (map!=null) {
				            		paperOrientation =(String)map.get("paperOrientation");
				            	}
			            	}
			            	this.wh=getWidthHeight(tvo,paperOrientation);
					        pageSize = new Rectangle(wh[0],wh[1]);
							document.setPageSize(pageSize);
			            	
			            }			            
						document.newPage();	
					}
				}
			}else{
				ArrayList pagelist=this.tablebo.getAllTemplatePage(1);
				if(pagelist.size()==0) {
                    throw new GeneralException(ResourceFactory.getProperty("error.printpages.zero"));
                }
				this.pages=pagelist.size();
				for(int j=0;j<objlist.size();j++) //人员对象
				{

					buf.setLength(0);
					paralist.clear();
					insid=(String)inslist.get(j);
					obj_id=(String)objlist.get(j);
					if("1".equals(""+this.tablebo.getInfor_type())){
					if(infor==1)//对人员要进行分库
					{
						if(obj_id.length()<11) //为空，未选中人员
						{
						  //dbpre="usr";
						  dbpre="";
	 					  if(obj_id.length()!=8) //人员编号
						  {
	 							obj_id="-1"; //打印空表
						  }
						}
						else
						{
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
					
					if(ins_id!=0)
					{
						buf.append(" and ins_id =?");
						paralist.add(Integer.valueOf(insid));
					}
					rset=dao.search(buf.toString(),paralist);	
					
					for(int i=0;i<pagelist.size();i++)//模板中的表页
					{
						TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
						if(!pagebo.isIsprint())//不打印的数据页
                        {
                            continue;
                        }
						this.currpage=i+1;					
						outPdfPageTitle(writer,pagebo,ins_id);
						outPdfPageGrid(dbpre,obj_id,writer,pagebo,rset,document);
						document.newPage();					
					}//for i loop end.
				}
			}

			filename=tempFile.getName();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try{
				if(document!=null){
					document.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				PubFunc.closeIoResource(fs);
			}
		}
		return filename;
	}
	/**
	 * 获得与签章最近的单元格
	 * @param signature
	 * @param pgidlist
	 * @return
	 */
    private ArrayList getSignAboutCell(String signature,ArrayList pgidlist) {
    	ArrayList signaboutlist = new ArrayList();
    	ContentDAO dao=new ContentDAO(this.conn);
    	org.jdom.Document doc = null;
		RowSet rset=null;
		String temp=null;
		int top = 0;
    	String pageid = "";
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
    	try {
	    	for(int s=0;s<pgidlist.size();s++){
				HashMap pgmap = (HashMap) pgidlist.get(s);
				pageid = (String)pgmap.get("pageid");
				doc = PubFunc.generateDom(signature);;
				List<org.jdom.Element> elelist = doc.getRootElement().getChildren();
				for(int j = 0; j < elelist.size(); j++){
					org.jdom.Element ele = elelist.get(j);
					String documentid = ele.getAttributeValue("DocuemntID");
					List<org.jdom.Element> list = ele.getChildren();
					for (int i = 0; i < list.size(); i++) {
						HashMap map = new HashMap();
						ArrayList setbolist = new ArrayList();
						org.jdom.Element e = list.get(i);
						if("item".equals(e.getName())){
							String delflag=e.getAttributeValue("delflag");
							if("true".equals(delflag)) {
                                continue;
                            }
							String SignatureID = e.getAttributeValue("SignatureID");
							String pointx = e.getAttributeValue("pointx");
							String pointy = e.getAttributeValue("pointy");
							if(this.signtype==0){
								pointx = pointx.substring(0,pointx.length()-2);
								pointy = pointy.substring(0,pointy.length()-2);
							}
							String PageID = e.getAttributeValue("PageID");
							if(pageid.equals(PageID)){
								rset=(RowSet) pgmap.get("context");
								rset.beforeFirst();
								while(rset.next()){
									int RLeft = rset.getInt("RLeft");
									int RTop = rset.getInt("RTop");
									if(/*RLeft<=Integer.parseInt(pointx)&&*/RTop<=Integer.parseInt(pointy)){
										TemplateSetBo setbo=new TemplateSetBo(this.conn,display_e0122);
										setbo.setHz(rset.getString("hz"));//设置表格的汉字描述
										setbo.setField_hz(rset.getString("Field_hz"));//字段的汉子描述
										String flag=rset.getString("Flag")==null?"":rset.getString("Flag");//数据源的标识（文本描述、照片......）
										if(!"V".equalsIgnoreCase(flag)&&!"S".equalsIgnoreCase(flag)&&!"F".equalsIgnoreCase(flag)&&rset.getString("Field_name")!=null&&rset.getString("Field_type")!=null&&rset.getString("subflag")!=null&& "0".equals(rset.getString("subflag"))&&rset.getString("Field_name").trim().length()>0&&rset.getString("Field_type").trim().length()>0){
											if("codesetid".equalsIgnoreCase(rset.getString("Field_name"))|| "codeitemdesc".equalsIgnoreCase(rset.getString("Field_name"))||
													"corcode".equalsIgnoreCase(rset.getString("Field_name"))|| "parentid".equalsIgnoreCase(rset.getString("Field_name"))|| "start_date".equalsIgnoreCase(rset.getString("Field_name"))){
												//这些特殊的字段的是不能从数据字典里获得的
											}else{
												FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name").trim());
												if(item==null){//数据字典里为空 2011 5 26 xieguiquan
													continue;
												}
											}
										}
										setbo.setFlag(rset.getString("Flag"));//设置数据源的标识
										setbo.setRleft(RLeft);
										setbo.setRtop(RTop);
										setbo.setGridno(rset.getInt("Gridno"));
										setbo.setPagebo(new TemplatePageBo(this.conn,this.tabid,rset.getInt("PageID")));
										if(setbolist.size()==0){
											setbolist.add(setbo);
										}else{
											TemplateSetBo tsb = (TemplateSetBo)(setbolist.get(0));
											if(/*RLeft>=tsb.getRleft()&&*/RTop>=tsb.getRtop()){
												setbolist.clear();
												setbolist.add(setbo);
											}
										}
									}
								}
								map.put("DocuemntID", documentid);
								map.put("SignatureID", SignatureID);
								map.put("x", pointx);
								map.put("y", pointy);
								map.put("pageid", pageid);
								map.put("setbo", setbolist);
								if("BJCA".equals(documentid)&&this.signtype==1){
									signaboutlist.add(map);
								}else if(this.signtype==0&&!"BJCA".equals(documentid)){
									signaboutlist.add(map);
								}
							}
						}
					}
				}
	    	}
    	} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signaboutlist;
	}
	/**
     * 重新取得线型，由于画线的原因
     * @param list
     * @param flag
     * @param line
     * @param cur_setbo//当前操作对象
     * @return
     */
    public int  getRlineForList(ArrayList list,String flag,int line,TemplateSetBo cur_setbo)
    {
    	if(line==0) {
            return line;
        } else
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
        			if(cur_gridno==gridno) {
                        continue;
                    }
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
	 * 根据具体数据算出总共需要导出多少页已经每页的内容 zhaoxg add 2015-3-28
	 * @param flag
	 * @param obj_id 
	 * @param dbpre 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getAllTemplatePageAndCell(int flag,RowSet rs,UserView uv,float[] wh,int ins_id,ArrayList pgidlist, String dbpre, String obj_id)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		try
		{
			int top = 0;
			TemplatePageBo pagebo=new TemplatePageBo(this.conn,this.tabid,0,this.task_ids);
			top = pagebo.getCell(list,rs,uv,wh,top,pgidlist,ins_id,this.tablebo.getUnrestrictedMenuPriv_Input(),dbpre,obj_id);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	
	
	/***
	 * 取得模板里人员的pdf打印文件
	 * @param  
	 * @para infor =1人员,=2单位,=3职位
	 * @para ins_id=0 ,提交申请时的打印 
	 * @return
	 * @throws GeneralException
	 */
	public File outPdfFile(String a0100,String nbase,int infor,int ins_id)throws GeneralException
	{
		RecordVo tvo=tablebo.getTable_vo();
		this.wh=getWidthHeight(tvo);
        Rectangle pageSize = new Rectangle(wh[0],wh[1]);
		Document document = new Document(pageSize);
		String prefix="template_";
		File tempFile=null;
		/**模板临时表名*/
		String tabname=null;
		/**根据第一个实例是否为0来分析，具体采用什么表*/
		if(ins_id==0) {
            tabname=this.userview.getUserName()+"templet_"+this.tabid;
        } else {
            tabname="templet_"+this.tabid;
        }
		PdfWriter writer = null;
		RowSet rset=null;
		try
		{
			ArrayList pagelist=this.tablebo.getAllTemplatePage(1);
			if(pagelist.size()==0) {
                throw new GeneralException(ResourceFactory.getProperty("error.printpages.zero"));
            }
			/**创建临时文件*/
			tempFile = File.createTempFile(prefix,".pdf", new File(System.getProperty("java.io.tmpdir")));
    		writer = PdfWriter.getInstance(document,new FileOutputStream(tempFile));
			document.open();		 
			this.pages=pagelist.size();
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			 
			buf.append("select * from ");	
			buf.append(tabname);
			if(tablebo.getInfor_type()==1){
				paralist.add(a0100);
				paralist.add(nbase.toLowerCase());
				buf.append(" where a0100=? and lower(basepre)=?");
			}else if(tablebo.getInfor_type()==2){
				paralist.add(a0100);
				buf.append(" where b0110=? ");
				}
			else if(tablebo.getInfor_type()==3){
				paralist.add(a0100);
				buf.append(" where e01a1=? ");
				}
			if(ins_id!=0)
			{
					buf.append(" and ins_id =?");
					paralist.add(Integer.valueOf(ins_id));
			}
			rset=dao.search(buf.toString(),paralist);	
				
			for(int i=0;i<pagelist.size();i++)//模板中的表页
			{
				TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
				if(!pagebo.isIsprint())//不打印的数据页
                {
                    continue;
                }
				this.currpage=i+1;					
				outPdfPageTitle(writer,pagebo,ins_id);
				outPdfPageGrid(nbase,a0100,writer,pagebo,rset,document);
				document.newPage();					
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(document);
			PubFunc.closeResource(writer);
			PubFunc.closeResource(rset);
		}
		return tempFile;
	}
	
	
	
	
	
	
	
	/**
	 * 打印某一页
	 * @param objlist
	 * @param infor
	 * @param ins_id
	 * @param pageno 页号
	 * @return
	 * @throws GeneralException
	 */
	public String outPdf(ArrayList objlist,int infor,ArrayList inslist,int pageno)throws GeneralException
	{
		RecordVo tvo=tablebo.getTable_vo();
		this.wh=getWidthHeight(tvo);
        Rectangle pageSize = new Rectangle(wh[0],wh[1]);
		Document document = new Document(pageSize);
		//String prefix="template_"+this.userview.getUserName();;
		String prefix=this.tablebo.getName()+this.userview.getUserName();
		String filename="";
		/**模板临时表名*/
		String tabname=null;
		/**根据第一个实例是否为0来分析，具体采用什么表*/
		int ins_id=Integer.parseInt((String)inslist.get(0));
		if(ins_id==0) {
            tabname=this.userview.getUserName()+"templet_"+this.tabid;
        } else {
            tabname="templet_"+this.tabid;
        }
		PdfWriter writer=null;
		try
		{
			ArrayList pagelist=this.tablebo.getAllTemplatePage();
            String filePath = System.getProperty("java.io.tmpdir")+File.separator+prefix+".pdf";
            File tempFile = new File(filePath);  
            if(!tempFile.exists()) {  
                try {  
                    tempFile.createNewFile();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
			/**创建临时文件*/
	       // File tempFile = File.createTempFile(prefix,".pdf", new File(System.getProperty("java.io.tmpdir")));
    		writer = PdfWriter.getInstance(document,new FileOutputStream(tempFile));
			document.open();
			String obj_id=null;
			String dbpre=null;
			RowSet rset=null;
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			String insid="";
			for(int j=0;j<objlist.size();j++) //人员对象
			{
				buf.setLength(0);
				paralist.clear();
				insid=(String)inslist.get(j);
				obj_id=(String)objlist.get(j);
				if("1".equals(""+this.tablebo.getInfor_type())){
				if(infor==1)//对人员要进行分库
				{
					if(obj_id.length()<11) //为空，未选中人员
					{
						dbpre="usr";
						obj_id="-1"; //打印空表
					}
					else
					{
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
				
				if(ins_id!=0)
				{
					buf.append(" and ins_id=?");
					paralist.add(Integer.valueOf(insid));
				}
				rset=dao.search(buf.toString(),paralist);	
				for(int i=0;i<pagelist.size();i++)//模板中的表页
				{
					TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
					if(pagebo.getPageid()!=pageno) {
                        continue;
                    }
					outPdfPageTitle(writer,pagebo,ins_id);
					outPdfPageGrid(dbpre,obj_id,writer,pagebo,rset,document);
					document.newPage();					
				}//for i loop end.
			}//for j loop end.
			filename=tempFile.getName();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try{
				if(document!=null){
					document.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				if(writer!=null)
				{
					PubFunc.closeIoResource(writer);
				}
			}
		}
		return filename;
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
			ContentDAO dao=new ContentDAO(conn);
			String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					doc=PubFunc.generateDom(ext_param);; 
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
							if(element!=null&&element.getAttributeValue("editable")!=null) {
                                editable=element.getAttributeValue("editable");
                            }
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    public boolean isSelfApply() {
        return selfApply;
    }
    public void setSelfApply(boolean selfApply) {
        this.selfApply = selfApply;
    }
	public int getSigntype() {
		return signtype;
	}
	public void setSigntype(int signtype) {
		this.signtype = signtype;
	}
	public String getNoshow_pageno() {
		return noshow_pageno;
	}
	public void setNoshow_pageno(String noshow_pageno) {
		this.noshow_pageno = noshow_pageno;
	}
}
