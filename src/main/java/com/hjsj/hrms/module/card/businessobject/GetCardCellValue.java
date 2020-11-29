/*
 * Created on 2005-4-29
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.module.card.businessobject;

import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GetCardCellValue{
	ArrayList recParam=new ArrayList();                     //保存格式化显示的最后值          
	private static final int CONDITIONQUERY_TYPE = 0;       //条件查询
	private static final int DATEQUERY_TYPE = 1;            //时间月查询
	private static final int BETWEENDATEQUERY_TYPE = 2;     //时间段查询
	private static final int SEASONQUERY_TYPE = 3;          //季节查询
	private static final int YEARQUERY_TYPE = 4;            //年查询
	private static final String PERSONKEYTYPE = "A0100";    //人员库特殊指标
	private static final String UNITKEYTYPE = "B0110";      //单位库特殊指标
	private static final String POSTKEYTYPE = "E01A1";      //岗位库特殊指标
	private static final String STDPOSKEYTYPE = "H0100";    //基准岗位特殊指标
	private static final char CELLFIELD_NUMBERTYPE = 'N';   //数字类型常量
	private static final char CELLFIELD_DATETYPE = 'D';     //日期类型常量
	private static final char CELLFIELD_MEMOTYPE = 'M';     //备注类型常量
	private static final char CELLFIELD_STRINGTYPE = 'A';   //字符类型常量
	private  int NMIN = 0;                                  /*子集纪录的最小和最大的比较值常量*/
	private  int NMAX = 120;   	
	private int FUNCTYPE=0;
	private int FUNCTYPE_NONE = 0;  // 无
	private int FUNCTYPE_SUM = 1;  // 求和
	private int FUNCTYPE_MAX = 2;
	private int FUNCTYPE_MIN = 3;
	private int FUNCTYPE_CNT = 4;
	private int FUNCTYPE_AVG = 5;
	//考核对象类型：TdegreeObjectType
	private int  PerObject_Dept  = 1;  // 团队
	private int  PerObject_Humam = 2;  // 人员
	private int  PerObject_Unit  = 3;  // 单位
	private int  PerObject_Batch = 4;  // 部门
	private String bizDate;
	private String userpriv="";
	public GetCardCellValue() {
	}
	private String display_zero="";
	private String fenlei_type="";//分类类型
	/*get card cell value**/
    public ArrayList GetFldValue(String infokind,		
	String cName,           //子集的名
	String cFldname,        //子集的属性名
	byte nFlag,             //表示1是人员库2表示单位库4表示职位库...
   	String cBase,           //人员库
	RGridView cell,         //单元格
	int typeFlag,           //按时间1还是按条件0
	int changeFlag,         //看子集是否是按月案年变化的0表示不是变化的1表示安月变化的2表示按年变化的
	int statYear,           //年
	int statMonth,          //月
	int ctimes,             //次数
	String nId,             //人员Id
	UserView userview,
	String cdatestart,
	String cdateend,
	int season,
	Connection conn,
	String fieldpurv)  //控制校验指标 0 不校验 1 校验           
	throws Exception {    
      FieldItem fielditem=(FieldItem)DataDictionary.getFieldItem(cFldname);
      this.userpriv=userpriv;
      if(fielditem==null)
      {
    	  if(infokind!=null&& "5".equals(infokind))
          {
    		  fielditem=new FieldItem();
    		  fielditem.setUseflag("1");
          }
      }      
      String priv_flag="";
      if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
      {
    	  priv_flag= userview.analyseSubFieldPriv(this.fenlei_type,cFldname)+"";    	  
    	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag))
    		  priv_flag="0";
      }else
      {
    	  priv_flag=userview.analyseFieldPriv(cFldname);
      }
      if(fielditem!=null && "1".equals(fielditem.getUseflag()) ||
    		  "e01a1".equalsIgnoreCase(cFldname) || "b0110".equalsIgnoreCase(cFldname) || 
    		  "E0122".equalsIgnoreCase(cFldname) || "h0100".equalsIgnoreCase(cFldname)/*基准岗位*/)
      {
			if(!"5".equals(infokind))
			{
				if((fieldpurv!=null&& "0".equals(fieldpurv))||!"0".equalsIgnoreCase(priv_flag)||"zpselfinfo".equalsIgnoreCase(userpriv))
				{
			   		//System.out.println("fadsfsa"  + cFldname+ userview.analyseFieldPriv(cFldname));
		    	  StringBuffer cSql=new StringBuffer();
				  //生成该单元格的查询语句返回StringBuffer字符串
				  cSql=getCardCellSql(infokind,cName,cFldname,nFlag,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,userview,cdatestart,cdateend,season,conn);		
				  return getCellListValue(cSql,cell,conn);	  //返回单元格的List的值
				}else
				{
					//System.out.println("dfasdf");
					return null;
				}
			}else
			{
				  //绩效
		          StatisticPlan statisticPlan=new StatisticPlan();
		          HashMap h_map=statisticPlan.getFiledSet(cell.getCexpress());
		          String setname=(String)h_map.get("SETNAME");
		          String fieldname=h_map.get("FIELDNAME").toString();

				  StringBuffer cSql=new StringBuffer();				
				  //生成该单元格的查询语句返回StringBuffer字符串
				  cSql=getCardCellSql(infokind,cName,cFldname,nFlag,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,userview,cdatestart,cdateend,season,conn);		
				  if(cSql!=null&& "per_plan.cycle".equals(cSql.toString()))
				  {
					  return getPerCycleStr(cell,conn,"per_plan","cycle");
                }else if(setname!=null&&setname.startsWith("per_table_")&&"reasons".equalsIgnoreCase(fieldname))
                {
                    return getReasons(cSql,cell,conn,"html");
				  }else
				  {
					  return getCellListValue(cSql,cell,conn);	 //返回单元格的List的值
				  }				    
			  }		   	
		    
	      
      }
      return null;
	}
    /*get card cell value*/
    public ArrayList GetFldValuePDF(
    String infokind,
	String cName,           //子集的名
	String cFldname,        //子集的属性名
	byte nFlag,             //表示1是人员库2表示单位库4表示职位库
   	String cBase,           //人员库
	RGridView cell,         //单元格
	int typeFlag,           //按时间1还是按条件0
	int changeFlag,         //看子集是否是按月案年变化的0表示不是变化的1表示安月变化的2表示按年变化的
	int statYear,           //年
	int statMonth,          //月
	int ctimes,             //次数
	String nId,             //人员Id
	UserView userview,
	String userpriv,
	String havepriv,
	String cdatestart,
	String cdateend,
	int season,
	Connection conn,
	String fieldpurv) throws Exception             //开始日
    {
    	  FieldItem fielditem=(FieldItem)DataDictionary.getFieldItem(cFldname);  
    	  this.userpriv=userpriv;
    	  String priv_flag="";
    	  if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
          {
        	  priv_flag= userview.analyseSubFieldPriv(this.fenlei_type,cFldname)+"";    	  
        	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag))
        		  priv_flag="0";
          }else
          {
        	  priv_flag=userview.analyseFieldPriv(cFldname);
          }
    	  if(fielditem==null)
          {
        	  if(infokind!=null&& "5".equals(infokind))
              {
        		  fielditem=new FieldItem();
        		  fielditem.setUseflag("1");
              }
          }
          if(fielditem!=null && "1".equals(fielditem.getUseflag()) || "e01a1".equalsIgnoreCase(cFldname) ||
        		  "b0110".equalsIgnoreCase(cFldname) || "E0122".equalsIgnoreCase(cFldname)||"H0100".equalsIgnoreCase(cFldname))
          {
	    	try{
	    	    if("0".equals(havepriv))
	    	      {
	    	      	  StringBuffer cSql=new StringBuffer();
	    			  //生成该单元格的查询语句返回StringBuffer字符串
	    			  cSql=getCardCellSql(infokind,cName,cFldname,nFlag,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,userview,cdatestart,cdateend,season,conn);		
	    			// System.out.println("sql " + cSql.toString());
	    			  return getCellListValuePDF(cSql,cell,conn);	  //返回单元格的List的值
	    	      }
	    	      else{
	    	      if("selfinfo".equalsIgnoreCase(userpriv))
	    	      {
	    			if((fieldpurv!=null&& "1".equals(fieldpurv))||!"0".equalsIgnoreCase(userview.analyseFieldPriv(cFldname,0)))
	    			{
	    	    	  StringBuffer cSql=new StringBuffer();
	    			  //生成该单元格的查询语句返回StringBuffer字符串
	    			  cSql=getCardCellSql(infokind,cName,cFldname,nFlag,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,userview,cdatestart,cdateend,season,conn);		
	    			  return getCellListValuePDF(cSql,cell,conn);	  //返回单元格的List的值
	    			}else
	    			{
	    				return null;
	    			}
	    		   }else
	    		   {
	    			   if(!"5".equals(infokind))
	    			   {
	    					if((fieldpurv!=null&& "1".equals(fieldpurv))||!"0".equalsIgnoreCase(priv_flag))
	    					{
	    			    	  StringBuffer cSql=new StringBuffer();
	    					  //生成该单元格的查询语句返回StringBuffer字符串
	    					  cSql=getCardCellSql(infokind,cName,cFldname,nFlag,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,userview,cdatestart,cdateend,season,conn);		
	    					  return getCellListValuePDF(cSql,cell,conn);	  //返回单元格的List的值
	    					}else
	    					{
	    						//System.out.println("dfasdf1");
	    						return null;
	    					}
	    				}else
	    				{
	    	                  StatisticPlan statisticPlan=new StatisticPlan();
	    	                  HashMap h_map=statisticPlan.getFiledSet(cell.getCexpress());
	    	                  String setname=(String)h_map.get("SETNAME");
	    	                  String fieldname=h_map.get("FIELDNAME").toString();

	    	                  StringBuffer cSql=new StringBuffer();
	    					  //生成该单元格的查询语句返回StringBuffer字符串	 
	    					  cSql=getCardCellSql(infokind,cName,cFldname,nFlag,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,userview,cdatestart,cdateend,season,conn);		
	    					  if(cSql!=null&& "per_plan.cycle".equals(cSql.toString()))
	    					  {
	    						   return getPerCycleStr(cell,conn,"per_plan","cycle");
	    	                  }else if(setname!=null&&setname.startsWith("per_table_")&&"reasons".equalsIgnoreCase(fieldname))
	    	                  {
	    	                      return getReasons(cSql,cell,conn, "pdf");
	    					  }else
	    					  {
	    					      return getCellListValuePDF(cSql,cell,conn);	  //返回单元格的List的值
	    					  }
	    				}
	    		   
	    		   }
	    	      }
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		throw e;
	    	}
    	}
    	return null;
  
	}
    /*************************
     * 返回单元格的List的值  *
     * ************************/
	private ArrayList getCellListValuePDF(
		StringBuffer cSql,
		RGridView cell,
		Connection conn)
		throws SQLException {
	    ArrayList strLst=new ArrayList();   //保存单元格的值
	    ArrayList strLstNoPre=new ArrayList(); //保存单元格的值前缀的时候得值
		//ResultSet rset = null;
	    ContentDAO dao = null;
		ResultSet data=null;
		try{
			dao = new ContentDAO(conn);
			//执行当前单元格的查询的sql语句		
			//System.out.println(cell.getField_type().toUpperCase().charAt(0));
			 //System.out.println(cSql.toString());
			 data=dao.search(cSql.toString());
			 while(data.next())
			 {
			 	switch(cell.getField_type().toUpperCase().charAt(0))
				{
					case CELLFIELD_STRINGTYPE:         //字符类型
					{ 
					  String val = data.getString(1);
					  if (val == null)
					      val = "";
//					  if(data.getString(1)!=null && data.getString(1).length()>0)
//					  {
						strLst.add(data.getString(1));//明码于代码输出
						strLstNoPre.add(data.getString(1));
//					  }					     
						break;
					}
					case CELLFIELD_MEMOTYPE:          //备注类型 
					{
						String value=data.getString(1);
						if(value == null)
						    value = "";
						String valueresult="";
//						if(value!=null && value.length()>0)
//						{
							/*for(int i=0;i<value.length();i++)
							{
							   if(value.substring(i,i+1).equals("\n"))
								   valueresult+="<br>";
							   else
								   valueresult+=value.substring(i,i+1);
							}*/		
						   valueresult=value;
						   valueresult=value.replaceAll("\\s+$","");//文本只去除尾部空格;
						   valueresult=valueresult.replaceAll(" ", "&nbsp;");
						   strLst.add(valueresult);//明码于代码输出
						   strLstNoPre.add(valueresult);
//						}
						break;
					}
					case CELLFIELD_DATETYPE:               //日期控制格式
					{
						String strdata=data.getDate(1)==null?null:data.getDate(1).toString();//String strdata=data.getString(1);
						java.sql.Date fdate=null;
						if(strdata!=null && strdata.length()>=4)
							fdate=data.getDate(1);
							
						getCellDateListValue(cell, strLst, strdata,fdate);
						break;
					}
					case CELLFIELD_NUMBERTYPE:            //数值类型
					{
						String fv=data.getString(1);
						if(fv==null||fv.length()<=0)
							fv="0.0";
						double df=Double.parseDouble(fv);
						//liuy 2016-2-22 16721：玉溪市商业银行股份有限公司：我的薪酬使用格计算公式，计算结果错误 begin
						//if(display_zero!=null&&display_zero.equals("0")&&df==0)
						//{
							//return strLst;
						//}else
						//liuy 2016-6-22 end							
						   getCellDecimalListValueDouble(cell, strLst, data.getDouble(1),strLstNoPre);
						break;				
					}
				}
			  }
			if(cell.isCode())
			{			
	     	  strLst=GetValueofField(strLst,cell.getCodeid());  //代码的转换
			}
			SaveParamValue(strLstNoPre,cell);                         //设置参数
			//System.out.println(strLst);
			return strLst;						
		   }catch (SQLException sqle){
				sqle.printStackTrace();
			}		
			finally{
				//conn.commit();
				try{
					if (data != null){
						data.close();
					}			
				}catch (SQLException sql){
					//sql.printStackTrace();
				}
			}
		return null;
	}
    /*************************
     * 返回单元格的List的值  *
     * ************************/
	private ArrayList getCellListValue(
		StringBuffer cSql,
		RGridView cell,
		Connection conn){
	    ArrayList strLst=new ArrayList();   //保存单元格的值
	    ArrayList strLstNoPre=new ArrayList(); //保存单元格的值前缀的时候得值
		//ResultSet rset = null;
		ContentDAO dao = null;
		ResultSet data=null;
		Statement stmt = null;
		if(cSql==null||cSql.length()<=0)
			return new ArrayList();
		DbSecurityImpl dbS = new DbSecurityImpl();
		try{
			//执行当前单元格的查询的sql语句		
			//24140 changxy 20161110
			/*dao = new ContentDAO(conn);// 
			data=dao.search(cSql.toString());*/
			stmt = conn.createStatement();			
			dbS.open(conn, cSql.toString());
			data=stmt.executeQuery(cSql.toString());
			 while(data.next())
			 {
			 	switch(cell.getField_type().toUpperCase().charAt(0))
				{
					case CELLFIELD_STRINGTYPE:         //字符类型
					{   
					  if(data.getString(1)!=null && data.getString(1).length()>0)
					  {
						strLst.add(data.getString(1));//明码于代码输出
						strLstNoPre.add(data.getString(1));
					  }else
					  {
						  strLst.add("");//明码于代码输出
							strLstNoPre.add("");
					  }				     
						break;
					}
					case CELLFIELD_MEMOTYPE:          //备注类型 
					{
						String value=data.getString(1);	
						String valueresult="";
						if(value!=null && value.length()>0)
						{
							value=value.replaceAll("\\s+$","");//文本只去除尾部空格;
							for(int i=0;i<value.length();i++)
							{
							   if("\n".equals(value.substring(i,i+1)))
								   valueresult+="<br>";
							   else if(" ".equals(value.substring(i,i+1))){
								   valueresult+="&nbsp;";
							   }else
								   valueresult+=value.substring(i,i+1);
							}						  
						   strLst.add(valueresult);//明码于代码输出
						   strLstNoPre.add(valueresult);
						}
						break;
					}
					case CELLFIELD_DATETYPE:               //日期控制格式
					{
						String strdata=data.getString(1);
						java.sql.Date fdate=null;
						if(strdata!=null && strdata.length()>=4)
							fdate=data.getDate(1);
							
						getCellDateListValue(cell, strLst, strdata,fdate);
						break;
					}
					case CELLFIELD_NUMBERTYPE:            //数值类型
					{
						String fv=data.getString(1);
						if(fv==null||fv.length()<=0)
							fv="0.0";
						double df=Double.parseDouble(fv);
						if(display_zero!=null&& "0".equals(display_zero)&&df==0)
						{
							SaveParamValue(strLstNoPre,cell);      
							return strLst;
						}else							
						   getCellDecimalListValue(cell, strLst, df,strLstNoPre);
						break;				
					}
				}
			  }
			if(cell.isCode())
			{			
	     	  strLst=GetValueofField(strLst,cell.getCodeid());  //代码的转换
			}
			SaveParamValue(strLstNoPre,cell);                         //设置参数
			return strLst;						
		   }catch (SQLException sqle){
			   
			    strLstNoPre.add("");
			    SaveParamValue(strLstNoPre,cell); 
				sqle.printStackTrace();
			}		
			finally{
				dbS.close(conn);
				PubFunc.closeDbObj(stmt);
				PubFunc.closeDbObj(data);
				//conn.commit();
				/*try{
					if (data != null){
						data.close();
					}			
				}catch (SQLException sql){
					//sql.printStackTrace();
				}*/
			}
		return null;
	}
	
    /**
     * 指标评分说明
     * @param format html(html或excel格式), pdf
     */
    private ArrayList getReasons(
        StringBuffer cSql,
        RGridView cell,
        Connection conn, String format){
        ArrayList strLst=new ArrayList();   //保存单元格的值
        if(cSql==null||cSql.length()<=0)
            return new ArrayList();
        ContentDAO dao=new ContentDAO(conn);
        RowSet data=null;
        try{
             data=dao.search(cSql.toString());
             int reccount=0;
             if(data.last())
                 reccount = data.getRow();
             data.beforeFirst();
             if(reccount==1) {
                 if(data.next()) {
                     String valueresult=data.getString("reasons");
                     valueresult=valueresult==null?"":valueresult;
                     if("html".equals(format))
                         valueresult=valueresult.replaceAll("\\n", "<br>");
                     strLst.add(valueresult);
                 }
             }
             else {
                 while(data.next())
                 {
                     String valueresult = "";
                     String desc = AdminCode.getCodeName("UN", data.getString("B0110"));
                     if(desc!=null&&desc.length()>0)
                         valueresult += desc;

                     desc = AdminCode.getCodeName("UM", data.getString("E0122"));
                     if(desc!=null&&desc.length()>0){                         
                         if(valueresult.length()>0)
                             valueresult+="/";
                         valueresult += desc;
                     }

                     desc = AdminCode.getCodeName("@K", data.getString("E01A1"));
                     if(desc!=null&&desc.length()>0){                         
                         if(valueresult.length()>0)
                             valueresult+="/";
                         valueresult += desc;
                     }
                     desc = data.getString("A0101");
                     if(valueresult.length()>0)
                         valueresult+="/";
                     valueresult+=desc;
                     strLst.add(valueresult);

                     valueresult=data.getString("reasons");
                     valueresult=valueresult==null?"":valueresult;
                     if("html".equals(format))
                         valueresult=valueresult.replaceAll("\\n", "<br>");
                     strLst.add(valueresult);
                 }
             }
             return strLst;                      
        }catch (SQLException sqle){
                sqle.printStackTrace();
        }       
        finally{
                try{
                    if(data!=null)
                        data.close();
                }catch (SQLException sql){
                    //sql.printStackTrace();
                }
        }
        return null;
    }
    
	/****************************************
	  * 返回单元格的类型为数值类型List的值       *
	  * *************************************/
	private void getCellDecimalListValue(
		RGridView cell,
		ArrayList strLst,
		float fieldFloatValue,ArrayList strLstNoPre)
		throws SQLException {
		String pattern="###";   //浮点数的精度
		String cStr;
	    if(cell.getSlope()>0)
	       pattern+=".";
	    for(int i=0;i<cell.getSlope();i++)
		   pattern +="#";
	   //cStr=new  BigDecimal(pattern).format((double)fieldFloatValue).trim();
	   BigDecimal b = new BigDecimal(fieldFloatValue);
       BigDecimal one = new BigDecimal("1");
       cStr=b.divide(one,cell.getSlope(),BigDecimal.ROUND_HALF_UP).toString();
	   strLstNoPre.add(cStr);
	    if(cStr !=null)
		   cStr=cell.getStrPre() +cStr;            //前缀加上格式化后的值
	    strLst.add(cStr);
	}
	/****************************************
	  * 返回单元格的类型为数值类型List的值       *
	  * *************************************/
	private void getCellDecimalListValueDouble(
		RGridView cell,ArrayList strLst,double fieldFloatValue,ArrayList strLstNoPre)
		throws SQLException {
		
		String pattern="###";   //浮点数的精度
		String cStr;		
		if(fieldFloatValue==0)
		{
			 BigDecimal b = new BigDecimal(fieldFloatValue);
		     BigDecimal one = new BigDecimal("1");
		     cStr=b.divide(one,cell.getSlope(),BigDecimal.ROUND_HALF_UP).toString();
		     if(!(display_zero!=null&& "0".equals(display_zero)))//是否显示0
		    	 strLst.add(cStr);
		     strLstNoPre.add(cStr);
			 return;
		}		
	    if(cell.getSlope()>0)
	       pattern+=".";
	    for(int i=0;i<cell.getSlope();i++)
		   pattern +="#";
	   //cStr=new  BigDecimal(pattern).format((double)fieldFloatValue).trim();
	   BigDecimal b = new BigDecimal(fieldFloatValue);
       BigDecimal one = new BigDecimal("1");
       cStr=b.divide(one,cell.getSlope(),BigDecimal.ROUND_HALF_UP).toString();
	   strLstNoPre.add(cStr);
	    if(cStr !=null)
		   cStr=cell.getStrPre() +cStr;            //前缀加上格式化后的值	    
	    strLst.add(cStr);
	   
	}
	/****************************************
	  * 返回单元格的类型为数值类型List的值       *
	  * *************************************/
	private void getCellDecimalListValue(
		RGridView cell,
		ArrayList strLst,
		double fieldFloatValue,ArrayList strLstNoPre)
		throws SQLException {
		String pattern="###";   //浮点数的精度
		String cStr;
		if(fieldFloatValue==0)
		{
			 BigDecimal b = new BigDecimal(fieldFloatValue);
		     BigDecimal one = new BigDecimal("1");
		     cStr=b.divide(one,cell.getSlope(),BigDecimal.ROUND_HALF_UP).toString();
		     strLst.add(cStr);
		     strLstNoPre.add(cStr);
			 return;
		}
	    if(cell.getSlope()>0)
	       pattern+=".";
	    for(int i=0;i<cell.getSlope();i++)
		   pattern +="#";
	   //cStr=new  BigDecimal(pattern).format((double)fieldFloatValue).trim();
	   BigDecimal b = new BigDecimal(fieldFloatValue);
       BigDecimal one = new BigDecimal("1");
       cStr=b.divide(one,cell.getSlope(),BigDecimal.ROUND_HALF_UP).toString();
	   strLstNoPre.add(cStr);
	    if(cStr !=null)
		   cStr=cell.getStrPre() +cStr;            //前缀加上格式化后的值
	    strLst.add(cStr);
	}
	/****************************************
	  * 返回单元格的类型为日期类型List的值  *
	  * *************************************/
	private void getCellDateListValue(
		RGridView cell,
		ArrayList strLst,
		String  strdata,
		java.sql.Date fdatetemp)
		throws SQLException {
		boolean bIsNull;
		String cStr1=""; 
		java.sql.Date fdate;
		int wYear=0;
		int wMonth=0;
		int wDay=0;
		int nIdx=0;
		int wHh=0;//时
		int wMm=0;//分
		int wSs=0;//秒
		String strPre="";
		String strExt="";	
		StringBuffer cStr=new StringBuffer();	
	    if(strdata ==null || strdata.length()<4)  //保证值为合法日期形
		{
			bIsNull=true;
		}
		else
		{
			bIsNull=false;
			fdate=fdatetemp;//data.getDate(1);
			Date convertDate=new Date(fdate.getTime());
			wYear=fdate.getYear() + 1900;
			wMonth=fdate.getMonth() + 1;
			wDay=fdate.getDate();
			wHh=convertDate.getHours();
			wMm=convertDate.getMinutes();
			wSs=convertDate.getSeconds();
    	}
		if(cell.getStrPre() !=null && cell.getStrPre().length()>0)    //判断前缀
		{
		   nIdx=cell.getStrPre().indexOf(",");
	  	   if(nIdx ==-1)
		   {
			  strPre=cell.getStrPre();
			  strExt="";						
		   }
		   else
		   {
			  strPre=cell.getStrPre().substring(0,nIdx).trim();
			  strExt=cell.getStrPre().substring(nIdx+1,cell.getStrPre().length());   
		   }
		}
        if(bIsNull)
        {
            // 只定义前缀时，未维护值不显示‘-’
            if (strExt == null || strExt.length() == 0)
                cStr1 = "";
            else {
                cStr1=strPre;
                cStr1+=strExt;
            }
            strLst.add(cStr1);
        }
        else 
		switch(cell.getSlope())                                 //日期的现实格式
		{
			case 6:  //1991.12.3
			{
				cStr.append(wYear);
				cStr.append(".");
				cStr.append(wMonth);
				cStr.append(".");
				cStr.append(wDay);
				if(cStr !=null)
				{
					cStr1=strPre;
					cStr1+=cStr.toString();   //cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1);
				break;
			}
			case 7:   //99.2.23
			{
				cStr.append(String.valueOf(wYear).substring(2,4));
			    cStr.append(".");
				cStr.append(wMonth);
				cStr.append(".");
				cStr.append(wDay);
				if(cStr !=null)
				{
					cStr1=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 8:  //1991.2
			{
				cStr.append(wYear);
				cStr.append(".");
				cStr.append(wMonth);
				if(cStr !=null)
				{
					cStr1=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1);
				break;
			}
			case 9:   //1991.02
			{			
				if(wMonth <10)
				   cStr.append(wYear + ".0" + wMonth);
				else
				   cStr.append(wYear + "." + wMonth);
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				//System.out.println(wYear + "." + wMonth + "." + wDay);
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 10:  //98.2
			{
				cStr.append(String.valueOf(wYear).substring(2,4));
				cStr.append(".");
				cStr.append(wMonth);
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 11:  //98.02
			{
				cStr.append(String.valueOf(wYear).substring(2,4));
				if(wMonth <10)
				  cStr.append(".0" + wMonth);
				else
				  cStr.append("." + wMonth);
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 12:   //一九九一年一月二日
			{
				cStr.append(getDateYear(wYear));
				cStr.append("年");
				cStr.append(getDateMonth(wMonth));
				cStr.append("月");
				cStr.append(getDateDay(wDay));
				cStr.append("日");					
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 13:  //一九九一年一月
			{
				cStr.append(getDateYear(wYear));
				cStr.append("年");
				cStr.append(getDateMonth(wMonth));
				cStr.append("月");
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 14:  //1991年10月5日
			{
				cStr.append(wYear);
				cStr.append("年");
				cStr.append(wMonth);
				cStr.append("月");
				cStr.append(wDay);
				cStr.append("日");		
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 15:  //1991年10月
			{
				cStr.append(wYear);
				cStr.append("年");
				cStr.append(wMonth);
				cStr.append("月");
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 16:   //91年10月5日
			{
				cStr.append(String.valueOf(wYear).substring(2,4));
				cStr.append("年");
				cStr.append(wMonth);
				cStr.append("月");
				cStr.append(wDay);
				cStr.append("日");		
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 17:   //91年10月
			{
				cStr.append(String.valueOf(wYear).substring(2,4));
				cStr.append("年");
				cStr.append(wMonth);
				cStr.append("月");										
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 18:  //求年龄
			{
				int wcYear = Calendar.getInstance().get(Calendar.YEAR);        //获得当前年
				int wcMonth= Calendar.getInstance().get(Calendar.MONTH) + 1;   //获得当前月
				int wcDay= Calendar.getInstance().get(Calendar.DATE) ; 
				if(wYear == 1899 || wYear==1889)
				{
				  wYear = Calendar.getInstance().get(Calendar.YEAR);        //获得当前年
				  wMonth= Calendar.getInstance().get(Calendar.MONTH) + 1;   //获得当前月
				  wDay= Calendar.getInstance().get(Calendar.DATE) ;         //获得当前日
				}        //获得当前日
				int nAge=GetHisAge(wcYear,wcMonth,wcDay,wYear,wMonth,wDay);
				if(nAge>2000)
					cStr.append("");
				else
					cStr.append(String.valueOf(nAge));
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());     
				break;
			}
			case 19:    ///get the year
			{
				cStr.append(String.valueOf(wYear));
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}							
			case 20:   ///get the month
			{
				cStr.append(String.valueOf(wMonth));
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 21:  //get the day
			{
				cStr.append(String.valueOf(wDay));
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 22:    //1991年01月
			{
				if(wMonth >9)
				   cStr.append(wYear + "年" + wMonth + "月");
				else
				   cStr.append(wYear + "年0" + wMonth + "月");
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());	
				break;							
			}
			case 23:    //1991年01月05日
			{
				cStr.append(wYear);
				cStr.append("年");
				if(wMonth>9)
				  cStr.append(String.valueOf(wMonth));
				else
				  cStr.append("0" + wMonth);
				cStr.append("月");
				if(wDay>9)
				  cStr.append(String.valueOf(wDay));
				else
				cStr.append("0" + wDay);
				cStr.append("日");
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 24:    //1991.01.01
			{
				cStr.append(wYear);
				cStr.append(".");
				if(wMonth>9)
				  cStr.append(String.valueOf(wMonth));
				else
				  cStr.append("0" + wMonth);
				cStr.append(".");
				if(wDay>9)
				  cStr.append(String.valueOf(wDay));
				else
				 cStr.append("0" + wDay);
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 25:{//yyyy.MM.dd hh:mm:ss
				cStr.append(wYear);
				cStr.append(".");
				if(wMonth>9)
				  cStr.append(String.valueOf(wMonth));
				else
				  cStr.append("0" + wMonth);
				cStr.append(".");
				if(wDay>9)
				  cStr.append(String.valueOf(wDay));
				else
				 cStr.append("0" + wDay);
				cStr.append(" ");
				if(wHh<=9) {
					cStr.append("0"+String.valueOf(wHh));
				}else {
					cStr.append(String.valueOf(wHh));	
				}
				cStr.append(":");
				if(wMm<=9) {
					cStr.append("0"+String.valueOf(wMm));
				}else {
					cStr.append(String.valueOf(wMm));	
				}
				cStr.append(":");
				if(wSs<=9) {
					cStr.append("0"+String.valueOf(wSs));
				}else {
					cStr.append(String.valueOf(wSs));	
				}
				
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
				strLst.add(cStr1.toString());
				break;
			}
			case 26:{
				switch (wMonth) {
				case 1:
					cStr.append("一月份");	
					break;
				case 2:
					cStr.append("二月份");
					break;
				case 3:
					cStr.append("三月份");
					break;
				case 4:
					cStr.append("四月份");
					break;
				case 5:
					cStr.append("五月份");
					break;
				case 6:
					cStr.append("六月份");
					break;
				case 7:
					cStr.append("七月份");
					break;
				case 8:
					cStr.append("八月份");
					break;
				case 9:
					cStr.append("九月份");
					break;
				case 10:
					cStr.append("十月份");
					break;
				case 11:
					cStr.append("十一月份");
					break;
				case 12:
					cStr.append("十二月份");
					break;
				}
				if(cStr !=null)
				{
					cStr1+=strPre;
					cStr1+=cStr.toString();//cell.getStrPre() + cStr1;				
				}
				strLst.add(cStr1.toString());
				break;
			}
		}
	}
	/****************************************
	* 返回单元格的sql字符串  *
	* *************************************/
	private StringBuffer getCardCellSql(String infokind,	
	String cName,           //子集的名
	String cFldname,        //子集的属性名
	byte nFlag,             //表示1是人员库,2表示单位库,4表示职位库,5表示计划,6招聘,7基准岗位
	String cBase,           //人员库
	RGridView cell,         //单元格
	int typeFlag,           //按时间1还是按条件0
	int changeFlag,         //看子集是否是按月案年变化的0表示不是变化的1表示安月变化的2表示按年变化的
	int statYear,           //年
	int statMonth,          //月
	int ctimes,             //次
	String nId,
	UserView userview,String cdatestart,String cdateend,int season,Connection conn) throws Exception{
		StringBuffer strsql=new StringBuffer();			
		switch(nFlag)
		{	
			//人员库
			case 0:
			{
				strsql=getPersonSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,PERSONKEYTYPE,userview,nFlag,cdatestart,cdateend,season);
				break;
			}
			//岗位库
			case 4:
			{
				if("1".equals(infokind))
				{
					nId=getOrgnId("4",nId,cBase);				
				}else
				{
					cBase="K";
				}
				//nId=getPostNid(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,POSTKEYTYPE,userview,nFlag,cdatestart,cdateend,season);
				strsql=getPostSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,POSTKEYTYPE,userview,nFlag,cdatestart,cdateend,season);
				break;
			}
			//单位库
			case 2:
			{
				if("1".equals(infokind))
				{
					nId=getOrgnId("2",nId,cBase);		
				}else
				{
					cBase="B";
				}
				strsql=getUnitSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,UNITKEYTYPE,userview,nFlag,cdatestart,cdateend,season);
				break;
			}
			//计划人员
			case 5:
			{
				strsql=getPlanSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,PERSONKEYTYPE,userview,nFlag,cdatestart,cdateend,season,conn);
				break;
			}
			case 6://招聘
			{
				strsql=getZPSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,PERSONKEYTYPE,userview,nFlag,cdatestart,cdateend,season,conn);
				break;
			}
			case 7:  // 基准岗位
			{
				strsql=getStdPosSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,STDPOSKEYTYPE,userview,nFlag,cdatestart,cdateend,season);
				break;
			}
		}				
		return strsql;
	}
	private String getOrgnId(String nFlag,String nId,String cBase)
	{
		StringBuffer nIdSql=new StringBuffer();
		if("4".equals(nFlag))
		{
		    nIdSql.append("select k01.e01a1 as orgid from k01,");
		    nIdSql.append(cBase);
		    nIdSql.append("A01 where k01.e01a1=");
		    nIdSql.append(cBase);
		    nIdSql.append("a01.e01a1 and ");
		    nIdSql.append(cBase);
		    nIdSql.append("a01.a0100='");
		    nIdSql.append(nId);
		    nIdSql.append("'");
		}else if("2".equals(nFlag))
		{
			 nIdSql.append("select B01.b0110 as orgid from B01,");
			 nIdSql.append(cBase);
			 nIdSql.append("A01 where B01.b0110=");
			 nIdSql.append(cBase);
			 nIdSql.append("a01.b0110 and ");
			 nIdSql.append(cBase);
			 nIdSql.append("a01.a0100='");
			 nIdSql.append(nId);
			 nIdSql.append("'");
		}
		ResultSet rset = null;
	    ContentDAO dao = null;
		Connection conn = null;
		try{
		    conn = AdminDb.getConnection();
		    dao = new ContentDAO(conn);
			//查询出该人员机构编码的nId;
			rset=dao.search(nIdSql.toString());
			if(rset.next())
				nId=rset.getString("orgid");				
			}catch (SQLException sqle){
	            sqle.printStackTrace();
            }
            catch (GeneralException ge){
	            ge.printStackTrace();
            }
            finally{
	          try{
		        if (rset != null){
			     rset.close();
		        }
		       if (conn != null){
			     conn.close();
		       }
	          }catch (SQLException sql){
		         sql.printStackTrace();
	         }
            }
		return nId;
	}
	/*
	 * 单位库生成查询的语句
	 * */
	private StringBuffer getUnitSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int typeFlag,
		int changeFlag,
		int statYear,
		int statMonth,
		int ctimes,
		String nId,
		String keyType,
		UserView userview,byte nFlag,String cdatestart,String cdateend,int season)
		throws Exception {	
			StringBuffer cSql=new StringBuffer();
			String cStr="";
			String cStr1="";	
			try{
			      cStr="B01";   //主集表名
			      cStr1=cName; //子集表名
			      if(!"B01".equals(cName))   //unit subset
			      {
			 	     cSql =getSubsetSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,season);
			      }
			      else        //mainset
			      {
				    cSql.append("SELECT ");
				    cSql.append(cStr1);
				    cSql.append(".");
				    cSql.append(cFldname);
				    cSql.append(" From ");
				    cSql.append(cStr1);
				    cSql.append(" WHERE ");
				    cSql.append(cStr1);
				    cSql.append(".");
				    cSql.append("B0110='");
				    cSql.append(nId);
				    cSql.append("'");
			      }		
			}catch (SQLException sqle){
				sqle.printStackTrace();
			}
			catch (GeneralException ge){
				ge.printStackTrace();
			}
			finally{
			}
		return cSql;		
	}
	/*
	 * post query sql
	 * */
	private StringBuffer getPostSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int typeFlag,
		int changeFlag,
		int statYear,
		int statMonth,
		int ctimes,
		String nId,
		String keyType,
		UserView userview,byte nFlag,String cdatestart,String cdateend,int season)
		throws Exception {
			StringBuffer cSql=new StringBuffer();
			String cStr="";
			String cStr1="";
			try{
				  cStr="K01";   //主集表名
				  cStr1=cName; //子集表名
				  if(!"K01".equals(cName))   // post sub set
				  {
					 cSql =getSubsetSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,season);
				  }
				  else                        //mainset
				  {				
					cSql.append("SELECT ");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(cFldname);
					cSql.append(" From ");
					cSql.append(cStr1);
					cSql.append(" WHERE ");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append("E01A1='");
					cSql.append(nId);
					cSql.append("'");
				  }		
			}catch (SQLException sqle){
				sqle.printStackTrace();
			}
			catch (GeneralException ge){
				ge.printStackTrace();
			}
			finally{	
			}
		return cSql;	
	}

	/**
	 * 基准岗位
	 * @param cName
	 * @param cFldname
	 * @param cBase
	 * @param cell
	 * @param typeFlag
	 * @param changeFlag
	 * @param statYear
	 * @param statMonth
	 * @param ctimes
	 * @param nId
	 * @param keyType
	 * @param userview
	 * @param nFlag
	 * @param cdatestart
	 * @param cdateend
	 * @param season
	 * @return
	 * @throws Exception
	 */
	private StringBuffer getStdPosSql(
			String cName,
			String cFldname,
			String cBase,
			RGridView cell,
			int typeFlag,
			int changeFlag,
			int statYear,
			int statMonth,
			int ctimes,
			String nId,
			String keyType,
			UserView userview,byte nFlag,String cdatestart,String cdateend,int season)
			throws Exception {
				StringBuffer cSql=new StringBuffer();
				String cStr="";
				String cStr1="";
				try{
					  cStr="H01";   //主集表名
					  cStr1=cName; //子集表名
					  if(!"H01".equals(cName))   // subset
					  {
						 cSql =getSubsetSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,season);
					  }
					  else                        // mainset
					  {				
						cSql.append("SELECT "+cStr1+"."+cFldname);
						cSql.append(" From "+cStr1);
						cSql.append(" WHERE "+cStr1+"."+keyType+"='"+nId+"'");
					  }		
				}catch (SQLException sqle){
					sqle.printStackTrace();
				}
				catch (GeneralException ge){
					ge.printStackTrace();
				}
				finally{	
				}
			return cSql;	
	}

	private StringBuffer getPersonSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int typeFlag,
		int changeFlag,
		int statYear,
		int statMonth,
		int ctimes,
		String nId,
		String keyType,
		UserView userview,byte nFlag,String cdatestart,String cdateend,int season)
		throws Exception {
		   StringBuffer strSql=new StringBuffer();
		   String cStr=cBase+ "A01";   //主集表名
		   String cStr1=cBase + cName; //子集表名		   
		String isView=getISVIEWForCexpress(cell.getCexpress());
		if(isView!=null&& "1".equals(isView))//走试图
		{
			cell.setIsView(isView);
			getViewForCexpress(cell);
			cStr1=cell.getCSetName();			
			strSql=getSubsetSql(cell.getCSetName(),cell.getField_name(),cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,season);
		}else if(!"A01".equals(cName))   //人员子集
		{
			//是子集的情况生成的sql语句
			if(!"P01".equalsIgnoreCase(cName)){
				strSql=getSubsetSql(cName,cFldname,cBase,cell,typeFlag,changeFlag,statYear,statMonth,ctimes,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,season);
			}else{
				strSql =getPsetConditionSql(cFldname,cBase,cell,nId,cName,cName,"P0100",userview,nFlag);
			}
		}else
		{
			//是主集的情况生成的sql语句
			strSql.append("SELECT ");
			strSql.append(cStr1);
			strSql.append(".");
		    strSql.append(cFldname);
		    strSql.append(" From ");
		    strSql.append(cStr1);
		    strSql.append(" WHERE ");
		    strSql.append(cStr1);
		    strSql.append(".");
		    strSql.append("A0100='");
		    strSql.append(nId);
		    strSql.append("'");
		}			
		return strSql;
	}
	/*
	 * sub set sql
	 * */
	private StringBuffer getSubsetSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int typeFlag,
		int changeFlag,
		int statYear,
		int statMonth,
		int ctimes,
		String nId,
		String cStr,
		String cStr1,
		String keyType,
		UserView userview,byte nFlag,String cdatestart,String cdateend,int season)
		throws Exception {
	     StringBuffer strSql=new StringBuffer(); 
		switch(typeFlag)
		{
			//0表示条件查询
			case CONDITIONQUERY_TYPE:
			{
				strSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
				break;									
			}
			//1表示是按时间查询
			case DATEQUERY_TYPE:
			{
				strSql=getSubsetDateSql(cName,cFldname,cBase,cell,changeFlag,statYear,statMonth,ctimes,nId,cStr,cStr1,keyType,userview,nFlag);
				break;
			}
			case BETWEENDATEQUERY_TYPE:
			{
				strSql=getSubsetBetweenDateSql(cName,cFldname,cBase,cell,changeFlag,statYear,statMonth,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend);
				break;
			}
			case SEASONQUERY_TYPE:
			{
				strSql=getSubSeasonDateSql(cName,cFldname,cBase,cell,changeFlag,statYear,statMonth,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,season);
				//System.out.println(strSql);
				break;
			}
			case YEARQUERY_TYPE:
			{
				strSql=getSubSeasonDateSql(cName,cFldname,cBase,cell,changeFlag,statYear,statMonth,nId,cStr,cStr1,keyType,userview,nFlag,cdatestart,cdateend,5);
				break;
			}
		}							
		return strSql;		
	}
	/*
	 * season query sql
	 */
	private StringBuffer getSubSeasonDateSql(
			String cName,
			String cFldname,
			String cBase,
			RGridView cell,
			int changeFlag,
			int statYear,
			int statMonth,
			String nId,
			String cStr,
			String cStr1,
			String keyType,
			UserView userview,byte nFlag,String cdatestart,String cdateend,int season)
			throws Exception {	
				StringBuffer cSql=new StringBuffer();
			switch(changeFlag)
			{  //0表示不安时间变化的得子集
				case 0:
				{
					cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
					break;
				}
				//1表示按月变化的子集
				case 1:
				{
					if("z1".equalsIgnoreCase(cell.getField_name()!=null&&cell.getField_name().length()>=5?cell.getField_name().substring(3,5):""))
					{

						cSql.append("SELECT count(*");              
	                    cSql.append(") as ");
	                    cSql.append(cFldname);
	                    cSql.append(" From ");
	                    cSql.append(cStr1);
	                    cSql.append(",");
	                    cSql.append(cStr);
						cSql.append(" WHERE ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("=");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append(" AND ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("='");
						cSql.append(nId);
						cSql.append("' AND ");
						cSql.append(getSeasonDateSql(cStr1+"." + cName + "Z0",statYear,season));						
					}else
					{
						if(!"N".equalsIgnoreCase(cell.getField_type()))
						{
							cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
							//cSql=getSubSetDateMonthSql(cName,cFldname,cBase,cell,statYear,statMonth,nId,cStr,cStr1,keyType,userview,nFlag);
							/*cSql.append("SELECT ");              
		                    cSql.append(cStr1);
		                    cSql.append(".");
		                    cSql.append(cFldname);
		                    cSql.append(" as ");
		                    cSql.append(cFldname);
		                    cSql.append(" From ");
		                    cSql.append(cStr1);
		                    cSql.append(",");
		                    cSql.append(cStr);
							cSql.append(" WHERE ");
							cSql.append(cStr);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append("=");
							cSql.append(cStr1);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append(" AND ");
							cSql.append(cStr);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append("='");
							cSql.append(nId);
							cSql.append("' AND ");
							cSql.append(getSeasonDateSql(cStr1+"." + cName + "Z0",statYear,season));*/							
						}
						else      //按年按月统计并且是数值型的字段
						{					
						    cSql.append("SELECT sum(");              
	                        cSql.append(cStr1);
	                        cSql.append(".");
	                        cSql.append(cFldname);
	                        cSql.append(") as ");
	                        cSql.append(cFldname);
	                        cSql.append(" From ");
	                        cSql.append(cStr1);
	                        cSql.append(",");
	                        cSql.append(cStr);
							cSql.append(" WHERE ");
							cSql.append(cStr);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append("=");
							cSql.append(cStr1);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append(" AND ");
							cSql.append(cStr);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append("='");
							cSql.append(nId);
							cSql.append("' AND ");
							cSql.append(getSeasonDateSql(cStr1+"." + cName + "Z0",statYear,season));																								
						    //System.out.println(cSql.toString());
						}	
					}
		
					break;
				}
				//2表示按年变化的查询子集
				case 2:
				{
			
					if(!"N".equalsIgnoreCase(cell.getField_type()))
					{
						cSql =getSubSetDateYearSql(cName,cFldname,cBase,cell,statYear,nId,cStr,cStr1,keyType,userview,nFlag);
					}else      //按年统计并且是数值型的字段
					{	
						cSql.append("SELECT sum(");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(cFldname);
						cSql.append(") as ");
						cSql.append(cFldname);
						cSql.append(" From ");
						cSql.append(cStr1);
						cSql.append(",");
						cSql.append(cStr);
						cSql.append(" WHERE ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("=");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append(" AND ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("='");
						cSql.append(nId);
						cSql.append("' AND ");
						cSql.append(getSeasonDateSql(cStr1+"." + cName + "Z0",statYear,season));	
					}		
					break;			
				}
			}	
			if("1".equals(cell.getIsView()))
			{
				cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
			}
		  return cSql;	
		}
	private String getSeasonDateSql(String fieldname,int cyear,int season){
		StringBuffer sqlstr=new StringBuffer();
		String startdate="";
		String enddate="";
		switch(season)
		{
		  case 1:
		  {
		  	startdate=cyear + "-01-01";
		  	enddate=cyear + "-03-31";
		  	break;
		  }
		  case 2:
		  {
		 	startdate=cyear + "-04-01";
		  	enddate=cyear + "-06-30";
		  	break;
		  }
		  case 3:
		  {
		 	startdate=cyear + "-07-01";
		  	enddate=cyear + "-09-30";
		  	break;
		  }
		  case 4:
		  {
		 	startdate=cyear + "-10-01";
		  	enddate=cyear + "-12-31";
		  	break;
		  }
		  case 5:
		  {
		 	startdate=cyear + "-01-01";
		  	enddate=cyear + "-12-31";
		  	break;
		  }
		}
		sqlstr.append("(");
		sqlstr.append(fieldname);
		sqlstr.append(" BETWEEN ");
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			sqlstr.append("'");
		  	sqlstr.append(startdate);
		  	sqlstr.append("' and '");
		  	sqlstr.append(enddate);
		  	sqlstr.append("'");
		  	break;
		  }
		  case Constant.DB2:
		  {
		  	sqlstr.append("TO_Date('");
		  	sqlstr.append(startdate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	sqlstr.append(" and ");
			sqlstr.append("TO_Date('");
		  	sqlstr.append(enddate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	break;
		  }
		  case Constant.ORACEL:
		  {
		  	sqlstr.append("TO_Date('");
		  	sqlstr.append(startdate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	sqlstr.append(" and ");
			sqlstr.append("TO_Date('");
		  	sqlstr.append(enddate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	break;
		  }
		}	
		sqlstr.append(")");		
	  return sqlstr.toString();
	}
	
	/*
	 * between datetime query sql
	 * */
	private StringBuffer getSubsetBetweenDateSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int changeFlag,
		int statYear,
		int statMonth,
		String nId,
		String cStr,
		String cStr1,
		String keyType,
		UserView userview,byte nFlag,String cdatestart,String cdateend)
		throws Exception {	
			StringBuffer cSql=new StringBuffer();
			//System.out.println(changeFlag);
		switch(changeFlag)
		{  //0表示不安时间变化的得子集
			case 0:
			{
				cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
				break;
			}
			//1表示按月变化的子集
			case 1:
			{
				if("z1".equalsIgnoreCase(cell.getField_name()!=null&&cell.getField_name().length()>=5?cell.getField_name().substring(3,5):""))
				{

					cSql.append("SELECT count(*");              
                    cSql.append(") as ");
                    cSql.append(cFldname);
                    cSql.append(" From ");
                    cSql.append(cStr1);
                    cSql.append(",");
                    cSql.append(cStr);
					cSql.append(" WHERE ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("=");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append(" AND ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("='");
					cSql.append(nId);
					cSql.append("' and ");
					cSql.append(getBetweenAndDate(cStr1+"." + cName + "Z0",cdatestart,cdateend));	
					
				
				}else
				{
					if(!"N".equalsIgnoreCase(cell.getField_type()))
					{
						cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
						//cSql=getSubSetDateMonthSql(cName,cFldname,cBase,cell,statYear,statMonth,nId,cStr,cStr1,keyType,userview,nFlag);
					   /* cSql.append("SELECT ");              
	                    cSql.append(cStr1);
	                    cSql.append(".");
	                    cSql.append(cFldname);
	                    cSql.append(" as ");
	                    cSql.append(cFldname);
	                    cSql.append(" From ");
	                    cSql.append(cStr1);
	                    cSql.append(",");
	                    cSql.append(cStr);
						cSql.append(" WHERE ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("=");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append(" AND ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("='");
						cSql.append(nId);
						cSql.append("' and ");
						cSql.append(getBetweenAndDate(cStr1+"." + cName + "Z0",cdatestart,cdateend));*/	
					}
					else      //按年按月统计并且是数值型的字段
					{					
					    cSql.append("SELECT sum(");              
                        cSql.append(cStr1);
                        cSql.append(".");
                        cSql.append(cFldname);
                        cSql.append(") as ");
                        cSql.append(cFldname);
                        cSql.append(" From ");
                        cSql.append(cStr1);
                        cSql.append(",");
                        cSql.append(cStr);
						cSql.append(" WHERE ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("=");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append(" AND ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("='");
						cSql.append(nId);
						cSql.append("' AND ");
						cSql.append(getBetweenAndDate(cStr1+"." + cName + "Z0",cdatestart,cdateend));																								
						//System.out.println(cSql.toString());
					}	
				}
	
				break;
			}
			//2表示按年变化的查询子集
			case 2:
			{
		
				if(!"N".equalsIgnoreCase(cell.getField_type()))
				{
					cSql =getSubSetDateYearSql(cName,cFldname,cBase,cell,statYear,nId,cStr,cStr1,keyType,userview,nFlag);
				}else      //按年统计并且是数值型的字段
				{	
					cSql.append("SELECT sum(");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(cFldname);
					cSql.append(") as ");
					cSql.append(cFldname);
					cSql.append(" From ");
					cSql.append(cStr1);
					cSql.append(",");
					cSql.append(cStr);
					cSql.append(" WHERE ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("=");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append(" AND ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("='");
					cSql.append(nId);
					cSql.append("' AND ");
					cSql.append(getBetweenAndDate(cStr1+"." + cName + "Z0",cdatestart,cdateend));																														
				}		
				break;			
			}
		}		
		if("1".equals(cell.getIsView()))
		{
			cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
		}
	  return cSql;	
	}
	
	/*
	 * between   and  的日期sql函数
	 */
	private String getBetweenAndDate(String fieldname,String startdate,String enddate)
	{
		StringBuffer sqlstr=new StringBuffer();
		sqlstr.append("(");
		sqlstr.append(fieldname);
		sqlstr.append(" BETWEEN ");
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			sqlstr.append("'");
		  	sqlstr.append(startdate);
		  	sqlstr.append("' and '");
		  	sqlstr.append(enddate);
		  	sqlstr.append("'");
		  	break;
		  }
		  case Constant.DB2:
		  {
		  	sqlstr.append("TO_Date('");
		  	sqlstr.append(startdate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	sqlstr.append(" and ");
			sqlstr.append("TO_Date('");
		  	sqlstr.append(enddate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	break;
		  }
		  case Constant.ORACEL:
		  {
		  	sqlstr.append("TO_Date('");
		  	sqlstr.append(startdate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	sqlstr.append(" and ");
			sqlstr.append("TO_Date('");
		  	sqlstr.append(enddate);
		  	sqlstr.append(" 0:0:0','YYYY-MM-DD HH24:MI:SS')");
		  	break;
		  }
		}	
	  sqlstr.append(")");		
	  return sqlstr.toString();
	}
	/*
	 * datetime query sql
	 * */
	private StringBuffer getSubsetDateSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int changeFlag,
		int statYear,
		int statMonth,
		int ctimes,
		String nId,
		String cStr,
		String cStr1,
		String keyType,
		UserView userview,byte nFlag)
		throws Exception {	
	    StringBuffer cSql=new StringBuffer();	   
		switch(changeFlag)
		{  //0表示不安时间变化的得子集
			case 0:
			{
				cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
				break;
			}
			//1表示按月变化的子集
			case 1:
			{
				if("z1".equalsIgnoreCase(cell.getField_name()!=null&&cell.getField_name().length()>=5?cell.getField_name().substring(3,5):""))
				{

					cSql.append("SELECT count(*");              
                    cSql.append(") as ");
                    cSql.append(cFldname);
                    cSql.append(" From ");
                    cSql.append(cStr1);
                    cSql.append(",");
                    cSql.append(cStr);
					cSql.append(" WHERE ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("=");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append(" AND ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("='");
					cSql.append(nId);
					cSql.append("' AND ");
					cSql.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
					cSql.append("=");
					cSql.append(statYear);
					if(statMonth!=13){
						cSql.append(" AND  ");
						cSql.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
						cSql.append("=");
						cSql.append(statMonth);	
					}																				
					if("1".equals(cell.getIsView()))
					{
						cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
					}
				}else
				{
					
					if(!"N".equalsIgnoreCase(cell.getField_type()))
					{
						    StringBuffer cSql1=new StringBuffer();
						    int nI=0;
							int nCur;		
							String cValue="";		
							boolean isCorrect=false;
						    //定位人员编号
							cSql1.append("SELECT ");
							cSql1.append(cStr1);
							cSql1.append(".I9999 From ");
							cSql1.append(cStr1);
							cSql1.append(",");
							cSql1.append(cStr);
							if(cell.getMode() !=null && cell.getMode().length()>0 && Integer.parseInt(cell.getMode()) >=5)
							{
							
								//有一个cell没有编写上 带有条件的
								cValue=GetSqlCond(cell,cBase,userview,nFlag);
								if(cValue==null||cValue.length()<=0)
									cValue="1=1";
								cSql1.append(" WHERE ");
								cSql1.append(cValue);
								cSql1.append(" AND ");
								cSql1.append(cStr);
								cSql1.append(".");
								cSql1.append(keyType);
								cSql1.append("=");
								cSql1.append(cStr1);
								cSql1.append(".");
								cSql1.append(keyType);
								cSql1.append(" AND ");
								cSql1.append(cStr);
								cSql1.append(".");
								cSql1.append(keyType);
								cSql1.append("='");
								cSql1.append(nId+"'");
								cSql1.append(" AND ");
								cSql1.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
								cSql1.append("=");
								cSql1.append(statYear);
								cSql1.append(" AND  ");
								cSql1.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
								cSql1.append("=");
								cSql1.append(statMonth);
								if(!isCorrect)
								{
								  cSql1.append(" ORDER BY ");
								  cSql1.append(cStr1);
								  cSql1.append(".I9999");
								}
							}	
							else
							{
								//组合子集中所有纪录的sql
								cSql1.append(" WHERE ");
								cSql1.append(cStr);
								cSql1.append(".");
								cSql1.append(keyType);
								cSql1.append("=");
								cSql1.append(cStr1);
								cSql1.append(".");
								cSql1.append(keyType);
								cSql1.append(" AND ");
								cSql1.append(cStr);
								cSql1.append(".");
								cSql1.append(keyType);
								cSql1.append("='");
								cSql1.append(nId+"'");
								cSql1.append(" AND ");
								cSql1.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
								cSql1.append("=");
								cSql1.append(statYear);
								cSql1.append(" AND  ");
								cSql1.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
								cSql1.append("=");
								cSql1.append(statMonth);
								if(!isCorrect)
								{
								  cSql1.append(" ORDER BY ");
								  cSql1.append(cStr1);
								  cSql1.append(".I9999");
								}
							}	
							ResultSet rset = null;
						    ContentDAO dao = null;
							Connection conn = null;
							int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
							try{
									conn = AdminDb.getConnection();
									dao = new ContentDAO(conn);
									//查询出该人员的该子集的所有纪录
									rset=dao.search(cSql1.toString());
									for(nI=0;rset.next();nI++)
									{
										//把各个纪录放到数组中以便查询出符合条件的某条纪录
										narrId[nI +1]=Integer.parseInt(rset.getString("I9999"));
										if(narrId[nI + 1]<NMIN)
										   NMIN=narrId[nI + 1];
										if(narrId[nI +1]>NMAX)
										   NMAX=narrId[nI + 1];
									}
									
							}catch (SQLException sqle){
						            sqle.printStackTrace();
					        }
					        catch (GeneralException ge){
						            ge.printStackTrace();
					        }
					        finally{
						          try{
							        if (rset != null){
								     rset.close();
							        }
							       if (conn != null){
								     conn.close();
							       }
						          }catch (SQLException sql){
							         sql.printStackTrace();
						         }
					      }
						//cSql=getSubSetDateMonthSql(cName,cFldname,cBase,cell,statYear,statMonth,nId,cStr,cStr1,keyType,userview,nFlag);
						cSql.append("SELECT ");              
	                    cSql.append(cStr1);
	                    cSql.append(".");
	                    cSql.append(cFldname);
	                    cSql.append(" as ");
	                    cSql.append(cFldname);
	                    cSql.append(" From ");
	                    cSql.append(cStr1);
	                    cSql.append(",");
	                    cSql.append(cStr);
						cSql.append(" WHERE ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("=");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append(" AND ");
						cSql.append(cStr);
						cSql.append(".");
						cSql.append(keyType);
						cSql.append("='");
						cSql.append(nId);
						cSql.append("' AND ");
						cSql.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
						cSql.append("=");
						cSql.append(statYear);
						cSql.append(" AND  ");
						cSql.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
						cSql.append("=");
						cSql.append(statMonth);
						if(ctimes!=11){
							cSql.append(" AND ");
							cSql.append(cStr1);
							cSql.append(".");
							cSql.append(cName);
							cSql.append("Z1=");
							cSql.append(ctimes);
						}else
						{
							//组合主SQL语句
							if(cell.getMode() !=null && cell.getMode().length()>0)
							{
								switch(Integer.parseInt(cell.getMode()))
								{					        		
									case 0:
									{
									   cSql.append(" AND ");
									   cSql.append(cStr1);
									   cSql.append(".I9999=");
									   if(nI>=cell.getRcount())
									     cSql.append(narrId[nI-cell.getRcount()+1]);
									   else
									     cSql.append(0);
									   if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
									   break;
									}
									case 5:
									{
										cSql.append(" AND ");
									    cSql.append(cStr1);
										cSql.append(".I9999=");
										if(nI>=cell.getRcount())
										  cSql.append(narrId[nI-cell.getRcount() +1]);
										else
										  cSql.append(0);
										if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
										break;
									}
									case 1:
									{
										cSql.append(" AND ");
										cSql.append(cStr1);
										cSql.append(".I9999>=");
										if(nI>=cell.getRcount())
										  cSql.append(narrId[nI-cell.getRcount()+1]);
										else
										  cSql.append(0);
										cSql.append(" AND ");
										cSql.append(cStr1);
										cSql.append(".I9999<=");
										cSql.append(NMAX);
										if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
										if(!isCorrect)
										{
										  cSql.append(" ORDER BY ");
										  cSql.append(cStr1);
										  cSql.append(".I9999");;
										}
										break;
									}
									case 2:
									{
										cSql.append(" AND ");
										cSql.append(cStr1);
									    cSql.append(".I9999=");
										cSql.append(narrId[cell.getRcount()]);
										if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
										break;
									}
									case 7:
									{
										cSql.append(" AND ");
										cSql.append(cStr1);
										cSql.append(".I9999=");
										cSql.append(narrId[cell.getRcount()]);
										if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
										break;
									}
									case 3:
									{
										cSql.append(" AND ");
										cSql.append(cStr1);
										cSql.append(".I9999<=");
										if(nI>=cell.getRcount())
										  cSql.append(narrId[cell.getRcount()]);
										else
										  cSql.append(NMAX);
										cSql.append(" AND ");
										cSql.append(cStr1);
										cSql.append(".I9999>=");
										cSql.append(NMIN);
										if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
										if(!isCorrect)
										{
				                          cSql.append(" ORDER BY ");
				                          cSql.append(cStr1);
				                          cSql.append(".I9999");
										}
										break;
									}
									case 4:
									{
										cSql.append(" AND ");
										cSql.append(GetSqlCond(cell,cBase,userview,nFlag));
										if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
										if(!isCorrect)
										{
										  cSql.append(" ORDER BY ");
										  cSql.append(cStr1);
										  cSql.append(".I9999");
										}
										break;
									}
									case 6:
									{
										if(nI>=cell.getRcount())
										 nCur=nI-cell.getRcount()+1;
										else
										 nCur=1;
										 cSql.append(" AND ");
										 cSql.append(cStr1);
										 cSql.append(".I9999 IN(-1");
										 for(int nK=nCur;nK<=nI;nK++)
										 {
											cSql.append("," + narrId[nK]);
										 }
										if(nI>0)
										{
										  //cValue=cSql.toString().substring(0,cSql.toString().length()-1);  //去掉最后一个字符
										  //cSql.delete(0,cSql.length());
									      //cSql.append(cValue);
									    }
									    cSql.append(")" );
									    if("1".equals(cell.getIsView()))
										{
											cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
										}
									    if(!isCorrect)
										{
									       cSql.append(" ORDER BY ");
									       cSql.append(cStr1);
									       cSql.append(".I9999");
										}
										break;
									}
									case 8:
									{
										if(nI>=cell.getRcount())
										  nCur=cell.getRcount();
										else
										  nCur=nI;
										 cSql.append(" AND ");
										 cSql.append(cStr1);
										 cSql.append(".I9999 IN(-1");
										  for(int nK=1;nK<=nCur;nK++)
										  {
											cSql.append( "," + narrId[nK]);
										  }
										  if(nI>0){
										    // cValue=cSql.toString().substring(0,cSql.toString().length()-1);
										    // cSql.delete(0,cSql.length());
										    // cSql.append(cValue);
										  }
										  cSql.append(")" );
										  if("1".equals(cell.getIsView()))
											{
												cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
											}
										  if(!isCorrect)
									      {
										       cSql.append(" ORDER BY ");
										       cSql.append(cStr1);
										       cSql.append(".I9999");
										  }
										  break;
									}					        		
								}
							}
						}
						
					}
					else      //按年按月统计并且是数值型的字段
					{					
						if(statMonth==13)
						{
							cSql.append("SELECT sum(");
							cSql.append(cStr1);
							cSql.append(".");
							cSql.append(cFldname);
	                        cSql.append(") as ");
	                        cSql.append(cFldname);
	                        cSql.append(" From ");     
	                        cSql.append(cStr1);
	                        cSql.append(",");
	                        cSql.append(cStr);
							cSql.append(" WHERE ");
	                        cSql.append(cStr);
	                        cSql.append(".");
	                        cSql.append(keyType);
	                        cSql.append("=");
	                        cSql.append(cStr1);
	                        cSql.append(".");
	                        cSql.append(keyType);
	                        cSql.append(" AND ");
	                        cSql.append(cStr);
	                        cSql.append(".");
	                        cSql.append(keyType);
	                        cSql.append("='");
	                        cSql.append(nId); 
							cSql.append("' AND ");
							cSql.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
	                        cSql.append("=");
	                        cSql.append(statYear);
	                        if("1".equals(cell.getIsView()))
	                		{
	                			cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
	                		}
						}
						else{
							cSql.append("SELECT sum(");              
	                        cSql.append(cStr1);
	                        cSql.append(".");
	                        cSql.append(cFldname);
	                        cSql.append(") as ");
	                        cSql.append(cFldname);
	                        cSql.append(" From ");
	                        cSql.append(cStr1);
	                        cSql.append(",");
	                        cSql.append(cStr);
							cSql.append(" WHERE ");
							cSql.append(cStr);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append("=");
							cSql.append(cStr1);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append(" AND ");
							cSql.append(cStr);
							cSql.append(".");
							cSql.append(keyType);
							cSql.append("='");
							cSql.append(nId);
							cSql.append("' AND ");
							cSql.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
							cSql.append("=");
							cSql.append(statYear);
							cSql.append(" AND  ");
							cSql.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
							cSql.append("=");
							cSql.append(statMonth);	
							if(ctimes!=11){
								cSql.append(" AND ");
								cSql.append(cStr1);
								cSql.append(".");
								cSql.append(cName);
								cSql.append("Z1=");
								cSql.append(ctimes);
							}
							if("1".equals(cell.getIsView()))
							{
								cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
							}
						}
					}	
				}
	
				break;
			}
			//2表示按年变化的查询子集
			case 2:
			{
		
				if(!"N".equalsIgnoreCase(cell.getField_type()))
				{
					cSql =getSubSetDateYearSql(cName,cFldname,cBase,cell,statYear,nId,cStr,cStr1,keyType,userview,nFlag);
				}else      //按年统计并且是数值型的字段
				{	
					cSql.append("SELECT sum(");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(cFldname);
					cSql.append(") as ");
					cSql.append(cFldname);
					cSql.append(" From ");
					cSql.append(cStr1);
					cSql.append(",");
					cSql.append(cStr);
					cSql.append(" WHERE ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("=");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append(" AND ");
					cSql.append(cStr);
					cSql.append(".");
					cSql.append(keyType);
					cSql.append("='");
					cSql.append(nId);
					cSql.append("' AND ");
					cSql.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
					cSql.append("=");
					cSql.append(statYear);
					if("1".equals(cell.getIsView()))
					{
						cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
					}
				}		
				break;			
			}
		}
		
		return cSql;	
	}
	/*
	 * year change gengeral sql method
	 * */
	private StringBuffer getSubSetDateYearSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int statYear,
		String nId,
		String cStr,
		String cStr1,
		String keyType,
		UserView userview,byte nFlag)
		throws Exception {
		StringBuffer cSql =new StringBuffer();
		//定位人员编号
		StringBuffer cSql2=new StringBuffer();
        String cValue;
        int nCur;
        cSql2.append("SELECT ");
        cSql2.append(cStr1);
        cSql2.append(".I9999 as I9999 From ");
        cSql2.append(cStr1);
        cSql2.append(",");
        cSql2.append(cStr);
		if(cell.getMode() !=null && cell.getMode().length()>0 && Integer.parseInt(cell.getMode()) >=5)
		{
			cValue=GetSqlCond(cell,cBase,userview,nFlag);
			if(cValue==null||cValue.length()<=0)
				cValue="1=1";
			cSql2.append(" WHERE ");
			cSql2.append(cValue);
			cSql2.append(" AND ");
			cSql2.append(cStr);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append("=");
			cSql2.append(cStr1);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append(" AND ");
			cSql2.append(cStr);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append("='");
			cSql2.append(nId);
			cSql2.append("' AND ");
			cSql2.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
		    cSql2.append("=");
		    cSql2.append(statYear);
		    cSql2.append(" ORDER BY ");
		    cSql2.append(cStr1);
		    cSql2.append(".I9999");	
		}	
		else
		{
			cSql2.append(" WHERE ");
			cSql2.append(cStr);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append("=");
			cSql2.append(cStr1);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append(" AND ");
			cSql2.append(cStr);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append("='");
			cSql2.append(nId);
			cSql2.append("' AND ");
			cSql2.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
			cSql2.append("=");
			cSql2.append(statYear);
			cSql2.append(" ORDER BY ");
			cSql2.append(cStr1);
			cSql2.append(".I9999");
		}	
		ResultSet rset = null;
		ContentDAO dao = null;
		Connection conn = null;	
		try{
			conn = AdminDb.getConnection();
			dao = new ContentDAO(conn);
			rset=dao.search(cSql2.toString());
			if(!rset.next())
			{
				cSql.append("SELECT ");
				cSql.append(cStr1);
				cSql.append(".");
				cSql.append(cFldname);
				cSql.append(" From ");
				cSql.append(cStr1);
				cSql.append(",");
				cSql.append(cStr);
				cSql.append(" WHERE ");
				cSql.append(cStr);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append("=");
				cSql.append(cStr1);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append(" AND ");
				cSql.append(cStr);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append("='");
				cSql.append(nId);
				cSql.append("'");
				cSql.append(" AND 1=2 ");//按年月查询无数据应返回一个查询空的sql
				/*nCur=rset.getInt("I9999");
				cSql.append(" AND ");
				cSql.append(cStr1);
				cSql.append(".I9999=");
				cSql.append(nCur);*/
			}
			else
			{
				//condition gengeral sql
				cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
			}
		}catch (SQLException sqle){
		   sqle.printStackTrace();
		}
		 catch (GeneralException ge){
			 ge.printStackTrace();
		}
		finally{
			try{
				if (rset != null){
				  rset.close();
				}
				if (conn != null){
				   conn.close();
				}
			  }catch (SQLException sql){
				 sql.printStackTrace();
			  }
		 }		
		return cSql;
	}
	/*
	 * month change gengeral sql method
	 * */
	private StringBuffer getSubSetDateMonthSql(
		String cName,
		String cFldname,
		String cBase,
		RGridView cell,
		int statYear,
		int statMonth,
		String nId,
		String cStr,
		String cStr1,
		String keyType,
		UserView userview,byte nFlag)
		throws Exception {	
			StringBuffer cSql=new StringBuffer();	
			StringBuffer cSql2=new StringBuffer();
			String cValue;
			int nCur;
			//ddd
			cSql2.append("SELECT ");
			cSql2.append(cStr1);
			cSql2.append(".I9999 as I9999 From ");
			cSql2.append(cStr1);
			cSql2.append(",");
			cSql2.append(cStr);
		if(cell.getMode() !=null && cell.getMode().length()>0 && Integer.parseInt(cell.getMode()) >=5)
		{
				cValue=GetSqlCond(cell,cBase,userview,nFlag);
				cSql2.append(" WHERE "); 
				if(cValue==null||cValue.length()<=0)
					cValue="1=1";
				cSql2.append(cValue);
				cSql2.append(" AND ");
				cSql2.append(cStr);
				cSql2.append(".");
				cSql2.append(keyType);
				cSql2.append("=");
				cSql2.append(cStr1);
				cSql2.append(".");
				cSql2.append(keyType);
				cSql2.append(" AND "); 
				cSql2.append(cStr);
				cSql2.append(".");
				cSql2.append(keyType);
				cSql2.append("='");
				cSql2.append(nId);
				cSql2.append("' AND ");
				cSql2.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
				cSql2.append("=");
				cSql2.append(statYear);
				cSql2.append(" AND  ");
				cSql2.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
				cSql2.append("=");
				cSql2.append(statMonth);
				cSql2.append(" ORDER BY ");
				cSql2.append(cStr1);
				cSql2.append(".I9999");
		}	
		else
		{
			cSql2.append(" WHERE ");
			cSql2.append(cStr);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append("=");
			cSql2.append(cStr1);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append(" AND ");
			cSql2.append(cStr);
			cSql2.append(".");
			cSql2.append(keyType);
			cSql2.append("='");
			cSql2.append(nId);
			cSql2.append("' AND ");
			cSql2.append(SqlDifference.getSqlYear(cStr1 + "." + cName + "Z0"));
			cSql2.append("=");
			cSql2.append(statYear);
			cSql2.append(" AND  ");
			cSql2.append(SqlDifference.getSqlMonth(cStr1 + "." + cName + "Z0"));
			cSql2.append("=");
			cSql2.append(statMonth);
			cSql2.append(" ORDER BY ");
			cSql2.append(cStr1);
			cSql2.append(".I9999");
		}
		ResultSet rset = null;
		ContentDAO dao = null;
		Connection conn = null;	
		try{
			conn = AdminDb.getConnection();
			dao = new ContentDAO(conn);
			rset=dao.search(cSql2.toString());
			if(!rset.next())
			{
				cSql.append("SELECT ");
				cSql.append(cStr1);
				cSql.append(".");
				cSql.append(cFldname);
				cSql.append(" From ");
				cSql.append(cStr1);
				cSql.append(",");
				cSql.append(cStr);
				cSql.append(" WHERE ");
				cSql.append(cStr);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append("=");
				cSql.append(cStr1);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append(" AND ");
				cSql.append(cStr);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append("='");
				cSql.append(nId);
				cSql.append("'");
				nCur=rset.getInt("I9999");
			    cSql.append(" AND ");
			    cSql.append(cStr1);
			    cSql.append(".I9999=");
			    cSql.append(nCur);
			}
			else
			{
				//condition gengeral sql
				cSql=getSubsetConditionSql(cFldname,cBase,cell,nId,cStr,cStr1,keyType,userview,nFlag);
			}
		}catch (SQLException sqle){
		   sqle.printStackTrace();
	    }
	     catch (GeneralException ge){
		     ge.printStackTrace();
	    }
	    finally{
	        try{
		        if (rset != null){
		          rset.close();
		        }
	            if (conn != null){
		           conn.close();
	            }
	          }catch (SQLException sql){
		         sql.printStackTrace();
	          }
	     }	
		return cSql;
	}
	/*
	 * condition gengeral query sql method
	 * */
	private StringBuffer getSubsetConditionSql(
		String cFldname,
		String cBase,
		RGridView cell,
		String nId,
		String cStr,
		String cStr1,
		String keyType,
		UserView userview,
		byte nFlag)
		throws Exception {
		StringBuffer cSql=new StringBuffer();
		StringBuffer cSql1=new StringBuffer();
		int nI=0;
		int nCur;		
		String cValue="";		
		boolean isCorrect=false;
		if("N".equalsIgnoreCase(cell.getField_type())&& "1".equals(cell.getFunc()))
		{
			isCorrect=true;
		}
		cSql.append("SELECT ");	
		String sub_domain=cell.getSub_domain();
		anySub_domain(cell);
		switch(this.FUNCTYPE)
		{	
		   case 0://无
		   {
			   if(isCorrect)
				{
					cSql.append("sum(");
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(cFldname);
					cSql.append(") as "+cFldname);
				}else
				{
					cSql.append(cStr1);
					cSql.append(".");
					cSql.append(cFldname);
				}
			   break;
		   }
		   case 1://this.FUNCTYPE_SUM
		   {
			   cSql.append("sum(");
			   cSql.append(cStr1);
			   cSql.append(".");
			   cSql.append(cFldname);
			   cSql.append(") as "+cFldname);
			   isCorrect=true;
			   break;
		   }
		   case 2://FUNCTYPE_MAX  = 2;		  
		   {
			   cSql.append("MAX(");
			   cSql.append(cStr1);
			   cSql.append(".");
			   cSql.append(cFldname);
			   cSql.append(") as "+cFldname);
			   isCorrect=true;
			   break;
		   }
		   case 3: //FUNCTYPE_MIN  = 3;
		   {
			   cSql.append("MIN(");
			   cSql.append(cStr1);
			   cSql.append(".");
			   cSql.append(cFldname);
			   cSql.append(") as "+cFldname);
			   isCorrect=true;
			   break;
		   }
		   case 4:// FUNCTYPE_CNT  = 4;
		   {
			   cSql.append("count(");
			   cSql.append(cStr1);
			   cSql.append(".");
			   cSql.append(cFldname);
			   cSql.append(") as "+cFldname);
			   isCorrect=true;
			   break;
		   }
		   case 5://FUNCTYPE_AVG  = 5;
		   {
			   cSql.append("AVG(");
			   cSql.append(cStr1);
			   cSql.append(".");
			   cSql.append(cFldname);
			   cSql.append(") as "+cFldname);
			   isCorrect=true;
			   break;
		   }
		}
		cSql.append(" From ");
		cSql.append(cStr1);
		cSql.append(",");
		cSql.append(cStr);
		cSql.append(" WHERE ");
		cSql.append(cStr);
		cSql.append(".");
		cSql.append(keyType);
		cSql.append("=");
		cSql.append(cStr1);
		cSql.append(".");
		cSql.append(keyType);
		cSql.append(" AND ");
		cSql.append(cStr);
		cSql.append(".");
		cSql.append(keyType);
		cSql.append("='");
		cSql.append(nId);
		cSql.append("'");
		if("1".equals(cell.getIsView()))
		{
			cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
		}
		//定位人员编号
		cSql1.append("SELECT ");
		cSql1.append(cStr1);
		cSql1.append(".I9999 From ");
		cSql1.append(cStr1);
		cSql1.append(",");
		cSql1.append(cStr);
		if(cell.getMode() !=null && cell.getMode().length()>0 && Integer.parseInt(cell.getMode()) >=5)
		{
		
			//有一个cell没有编写上 带有条件的
			cValue=GetSqlCond(cell,cBase,userview,nFlag);
			if(cValue==null||cValue.length()<=0)
				cValue="1=1";
			cSql1.append(" WHERE (");
			cSql1.append(cValue);
			cSql1.append(") AND ");
			cSql1.append(cStr);
			cSql1.append(".");
			cSql1.append(keyType);
			cSql1.append("=");
			cSql1.append(cStr1);
			cSql1.append(".");
			cSql1.append(keyType);
			cSql1.append(" AND ");
			cSql1.append(cStr);
			cSql1.append(".");
			cSql1.append(keyType);
			cSql1.append("='");
			cSql1.append(nId+"'");
			if(!isCorrect)
			{
			  cSql1.append(" ORDER BY ");
			  cSql1.append(cStr1);
			  cSql1.append(".I9999");
			}
		}	
		else
		{
			//组合子集中所有纪录的sql
			cSql1.append(" WHERE ");
			cSql1.append(cStr);
			cSql1.append(".");
			cSql1.append(keyType);
			cSql1.append("=");
			cSql1.append(cStr1);
			cSql1.append(".");
			cSql1.append(keyType);
			cSql1.append(" AND ");
			cSql1.append(cStr);
			cSql1.append(".");
			cSql1.append(keyType);
			cSql1.append("='");
			cSql1.append(nId+"'");
			if(!isCorrect)
			{
			  cSql1.append(" ORDER BY ");
			  cSql1.append(cStr1);
			  cSql1.append(".I9999");
			}
		}	
		ResultSet rset = null;
	    Statement stmt = null;
		Connection conn = null;
		int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
			try{
				conn = AdminDb.getConnection();
				stmt = conn.createStatement();
				//查询出该人员的该子集的所有纪录
				rset=stmt.executeQuery(cSql1.toString());
				for(nI=0;rset.next();nI++)
				{
					//把各个纪录放到数组中以便查询出符合条件的某条纪录
					narrId[nI +1]=Integer.parseInt(rset.getString("I9999"));
					if(narrId[nI + 1]<NMIN)
					   NMIN=narrId[nI + 1];
					if(narrId[nI +1]>NMAX)
					   NMAX=narrId[nI + 1];
				}
				
			}catch (SQLException sqle){
	            sqle.printStackTrace();
            }
            catch (GeneralException ge){
	            ge.printStackTrace();
            }
            finally{
            	PubFunc.closeDbObj(rset);
            	PubFunc.closeDbObj(stmt);
            	PubFunc.closeDbObj(conn);
            }
		   //组合主SQL语句
			if(cell.getMode() !=null && cell.getMode().length()>0)
			{
				switch(Integer.parseInt(cell.getMode()))
				{					        		
					case 0:
					{
					   cSql.append(" AND ");
					   cSql.append(cStr1);
					   cSql.append(".I9999=");
					   if(nI>=cell.getRcount())
					     cSql.append(narrId[nI-cell.getRcount()+1]);
					   else
					     cSql.append(0);
					  
					   break;
					}
					case 5:
					{
						cSql.append(" AND ");
					    cSql.append(cStr1);
						cSql.append(".I9999=");
						if(nI>=cell.getRcount())
						  cSql.append(narrId[nI-cell.getRcount() +1]);
						else
						  cSql.append(0);
						break;
					}
					case 1:
					{
						cSql.append(" AND ");
						cSql.append(cStr1);
						cSql.append(".I9999>=");
						if(nI>=cell.getRcount())
						  cSql.append(narrId[nI-cell.getRcount()+1]);
						else
						  cSql.append(0);
						cSql.append(" AND ");
						cSql.append(cStr1);
						cSql.append(".I9999<=");
						cSql.append(NMAX);
						if(!isCorrect)
						{
						  cSql.append(" ORDER BY ");
						  cSql.append(cStr1);
						  cSql.append(".I9999");;
						}
						break;
					}
					case 2:
					{
						cSql.append(" AND ");
						cSql.append(cStr1);
					    cSql.append(".I9999=");
						cSql.append(narrId[cell.getRcount()]);
						break;
					}
					case 7:
					{
						cSql.append(" AND ");
						cSql.append(cStr1);
						cSql.append(".I9999=");
						cSql.append(narrId[cell.getRcount()]);
						break;
					}
					case 3:
					{
						cSql.append(" AND ");
						cSql.append(cStr1);
						cSql.append(".I9999<=");
						if(nI>=cell.getRcount())
						  cSql.append(narrId[cell.getRcount()]);
						else
						  cSql.append(NMAX);
						cSql.append(" AND ");
						cSql.append(cStr1);
						cSql.append(".I9999>=");
						cSql.append(NMIN);
						if(!isCorrect)
						{
                          cSql.append(" ORDER BY ");
                          cSql.append(cStr1);
                          cSql.append(".I9999");
						}
						break;
					}
					case 4:
					{
						String css=GetSqlCond(cell,cBase,userview,nFlag);
						if(css!=null&&css.length()>0)
						{
							cSql.append(" AND ");
							cSql.append(css);							
						}	
						if(!isCorrect)
						{
						  cSql.append(" ORDER BY ");
						  cSql.append(cStr1);
						  cSql.append(".I9999");
						}
						break;
					}
					case 6:
					{
						if(nI>=cell.getRcount())
						 nCur=nI-cell.getRcount()+1;
						else
						 nCur=1;
						 cSql.append(" AND ");
						 cSql.append(cStr1);
						 cSql.append(".I9999 IN(-1");
						 for(int nK=nCur;nK<=nI;nK++)
						 {
							cSql.append("," + narrId[nK]);
						 }
						if(nI>0)
						{
						  //cValue=cSql.toString().substring(0,cSql.toString().length()-1);  //去掉最后一个字符
						  //cSql.delete(0,cSql.length());
					      //cSql.append(cValue);
					    }
					    cSql.append(")" );
					    if(!isCorrect)
						{
					       cSql.append(" ORDER BY ");
					       cSql.append(cStr1);
					       cSql.append(".I9999");
						}
						break;
					}
					case 8:
					{
						if(nI>=cell.getRcount())
						  nCur=cell.getRcount();
						else
						  nCur=nI;
						 cSql.append(" AND ");
						 cSql.append(cStr1);
						 cSql.append(".I9999 IN(-1");
						  for(int nK=1;nK<=nCur;nK++)
						  {
							cSql.append( "," + narrId[nK]);
						  }
						  if(nI>0){
						    // cValue=cSql.toString().substring(0,cSql.toString().length()-1);
						    // cSql.delete(0,cSql.length());
						    // cSql.append(cValue);
						  }
						  cSql.append(")" );
						  if(!isCorrect)
					      {
						       cSql.append(" ORDER BY ");
						       cSql.append(cStr1);
						       cSql.append(".I9999");
						  }
						  break;
					}					        		
				}
			}
			return cSql;
	}
	
	private StringBuffer getPsetConditionSql(
			String cFldname,
			String cBase,
			RGridView cell,
			String nId,
			String cStr,
			String cStr1,
			String keyType,
			UserView userview,
			byte nFlag)
			throws Exception {
			StringBuffer cSql=new StringBuffer();
			StringBuffer cSql1=new StringBuffer();
			if("P01".equalsIgnoreCase(cStr)){
				cStr="per_diary_content";
			}
			int nI=0;
			int nCur;		
			String cValue="";		
			boolean isCorrect=false;
			if("N".equalsIgnoreCase(cell.getField_type())&& "1".equals(cell.getFunc()))
			{
				isCorrect=true;
			}
			cSql.append("SELECT ");	
			String sub_domain=cell.getSub_domain();
			anySub_domain(cell);
			switch(this.FUNCTYPE)
			{	
			   case 0://无
			   {
				   if(isCorrect)
					{
						cSql.append("sum(");
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(cFldname);
						cSql.append(") as "+cFldname);
					}else
					{
						cSql.append(cStr1);
						cSql.append(".");
						cSql.append(cFldname);
					}
				   break;
			   }
			   case 1://this.FUNCTYPE_SUM
			   {
				   cSql.append("sum(");
				   cSql.append(cStr1);
				   cSql.append(".");
				   cSql.append(cFldname);
				   cSql.append(") as "+cFldname);
				   isCorrect=true;
				   break;
			   }
			   case 2://FUNCTYPE_MAX  = 2;		  
			   {
				   cSql.append("MAX(");
				   cSql.append(cStr1);
				   cSql.append(".");
				   cSql.append(cFldname);
				   cSql.append(") as "+cFldname);
				   isCorrect=true;
				   break;
			   }
			   case 3: //FUNCTYPE_MIN  = 3;
			   {
				   cSql.append("MIN(");
				   cSql.append(cStr1);
				   cSql.append(".");
				   cSql.append(cFldname);
				   cSql.append(") as "+cFldname);
				   isCorrect=true;
				   break;
			   }
			   case 4:// FUNCTYPE_CNT  = 4;
			   {
				   cSql.append("count(");
				   cSql.append(cStr1);
				   cSql.append(".");
				   cSql.append(cFldname);
				   cSql.append(") as "+cFldname);
				   isCorrect=true;
				   break;
			   }
			   case 5://FUNCTYPE_AVG  = 5;
			   {
				   cSql.append("AVG(");
				   cSql.append(cStr1);
				   cSql.append(".");
				   cSql.append(cFldname);
				   cSql.append(") as "+cFldname);
				   isCorrect=true;
				   break;
			   }
			}
			cSql.append(" From ");
			cSql.append(cStr1);
			if("Content".equalsIgnoreCase(cFldname)){
				cSql.append(",");
				cSql.append(cStr);
				cSql.append(" WHERE ");
				cSql.append(cStr);
				cSql.append(".");
				cSql.append(keyType);
				cSql.append("=");
				cSql.append(cStr1);
				cSql.append(".");
				cSql.append(keyType);
			}else{
				cSql.append(" WHERE 1=1");
			}
			cSql.append(" AND ");
			cSql.append(cStr1);
			cSql.append(".");
			cSql.append("A0100");
			cSql.append("='");
			cSql.append(nId);
			cSql.append("'");
			cSql.append(" AND ");
			cSql.append(cStr1);
			cSql.append(".");
			cSql.append("nbase");
			cSql.append("='");
			cSql.append(cBase);
			cSql.append("'");
			
			//定位人员编号
			cSql1.append("SELECT ");
			cSql1.append(cStr1);
			cSql1.append("."+keyType+" From ");
			cSql1.append(cStr1);
			cSql1.append(",");
			cSql1.append(cStr);
			if(cell.getMode() !=null && cell.getMode().length()>0 && Integer.parseInt(cell.getMode()) >=5)
			{
			
				//有一个cell没有编写上 带有条件的
				cValue=GetSqlCond(cell,cBase,userview,nFlag);
				if(cValue==null||cValue.length()<=0)
					cValue="1=1";
				cSql1.append(" WHERE ");
				cSql1.append(cValue);
				cSql1.append(" AND ");
				cSql1.append(cStr);
				cSql1.append(".");
				cSql1.append(keyType);
				cSql1.append("=");
				cSql1.append(cStr1);
				cSql1.append(".");
				cSql1.append(keyType);
				cSql1.append(" AND ");
				cSql1.append(cStr);
				cSql1.append(".");
				cSql1.append(keyType);
				cSql1.append("='");
				cSql1.append(nId+"'");
				if(!isCorrect)
				{
				  cSql1.append(" ORDER BY ");
				  cSql1.append(cStr1);
				  cSql1.append("."+keyType);
				}
			}	
			else
			{
				//组合子集中所有纪录的sql
				cSql1.append(" WHERE ");
				cSql1.append(cStr);
				cSql1.append(".");
				cSql1.append(keyType);
				cSql1.append("=");
				cSql1.append(cStr1);
				cSql1.append(".");
				cSql1.append(keyType);
				cSql1.append(" AND ");
				cSql1.append(cStr1);
				cSql1.append(".");
				cSql1.append("A0100");
				cSql1.append("='");
				cSql1.append(nId);
				cSql1.append("'");
				cSql1.append(" AND ");
				cSql1.append(cStr1);
				cSql1.append(".");
				cSql1.append("nbase");
				cSql1.append("='");
				cSql1.append(cBase);
				cSql1.append("'");
				if(!isCorrect)
				{
				  cSql1.append(" ORDER BY ");
				  cSql1.append(cStr1);
				  cSql1.append("."+keyType);
				}
			}	
			ResultSet rset = null;
		    ContentDAO dao = null;
			Connection conn = null;
			int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
				try{
					conn = AdminDb.getConnection();
					dao = new ContentDAO(conn);
					//查询出该人员的该子集的所有纪录
					rset=dao.search(cSql1.toString());
					for(nI=0;rset.next();nI++)
					{
						//把各个纪录放到数组中以便查询出符合条件的某条纪录
						narrId[nI +1]=Integer.parseInt(rset.getString("I9999"));
						if(narrId[nI + 1]<NMIN)
						   NMIN=narrId[nI + 1];
						if(narrId[nI +1]>NMAX)
						   NMAX=narrId[nI + 1];
					}
					
				}catch (SQLException sqle){
		            sqle.printStackTrace();
	            }
	            catch (GeneralException ge){
		            ge.printStackTrace();
	            }
	            finally{
		          try{
			        if (rset != null){
				     rset.close();
			        }
			       if (conn != null){
				     conn.close();
			       }
		          }catch (SQLException sql){
			         sql.printStackTrace();
		         }
	            }
			   //组合主SQL语句
				if(cell.getMode() !=null && cell.getMode().length()>0)
				{
					switch(Integer.parseInt(cell.getMode()))
					{					        		
						case 0:
						{
						   cSql.append(" AND ");
						   cSql.append(cStr1);
						   cSql.append("."+keyType+"=");
						   if(nI>=cell.getRcount())
						     cSql.append(narrId[nI-cell.getRcount()+1]);
						   else
						     cSql.append(0);
						  
						   break;
						}
						case 5:
						{
							cSql.append(" AND ");
						    cSql.append(cStr1);
							cSql.append("."+keyType+"=");
							if(nI>=cell.getRcount())
							  cSql.append(narrId[nI-cell.getRcount() +1]);
							else
							  cSql.append(0);
							break;
						}
						case 1:
						{
							cSql.append(" AND ");
							cSql.append(cStr1);
							cSql.append("."+keyType+">=");
							if(nI>=cell.getRcount())
							  cSql.append(narrId[nI-cell.getRcount()+1]);
							else
							  cSql.append(0);
							cSql.append(" AND ");
							cSql.append(cStr1);
							cSql.append("."+keyType+"<=");
							cSql.append(NMAX);
							if(!isCorrect)
							{
							  cSql.append(" ORDER BY ");
							  cSql.append(cStr1);
							  cSql.append("."+keyType);;
							}
							break;
						}
						case 2:
						{
							cSql.append(" AND ");
							cSql.append(cStr1);
						    cSql.append("."+keyType+"=");
							cSql.append(narrId[cell.getRcount()]);
							break;
						}
						case 7:
						{
							cSql.append(" AND ");
							cSql.append(cStr1);
							cSql.append("."+keyType+"=");
							cSql.append(narrId[cell.getRcount()]);
							break;
						}
						case 3:
						{
							cSql.append(" AND ");
							cSql.append(cStr1);
							cSql.append("."+keyType+"<=");
							if(nI>=cell.getRcount())
							  cSql.append(narrId[cell.getRcount()]);
							else
							  cSql.append(NMAX);
							cSql.append(" AND ");
							cSql.append(cStr1);
							cSql.append("."+keyType+">=");
							cSql.append(NMIN);
							if(!isCorrect)
							{
	                          cSql.append(" ORDER BY ");
	                          cSql.append(cStr1);
	                          cSql.append("."+keyType);
							}
							break;
						}
						case 4:
						{
							String css=GetSqlCond(cell,cBase,userview,nFlag);
							if(css!=null&&css.length()>0)
							{
								cSql.append(" AND ");
								cSql.append(css);							
							}	
							if(!isCorrect)
							{
							  cSql.append(" ORDER BY ");
							  cSql.append(cStr1);
							  cSql.append("."+keyType);
							}
							break;
						}
						case 6:
						{
							if(nI>=cell.getRcount())
							 nCur=nI-cell.getRcount()+1;
							else
							 nCur=1;
							 cSql.append(" AND ");
							 cSql.append(cStr1);
							 cSql.append("."+keyType+" IN(-1");
							 for(int nK=nCur;nK<=nI;nK++)
							 {
								cSql.append("," + narrId[nK]);
							 }
							if(nI>0)
							{
							  //cValue=cSql.toString().substring(0,cSql.toString().length()-1);  //去掉最后一个字符
							  //cSql.delete(0,cSql.length());
						      //cSql.append(cValue);
						    }
						    cSql.append(")" );
						    if(!isCorrect)
							{
						       cSql.append(" ORDER BY ");
						       cSql.append(cStr1);
						       cSql.append("."+keyType);
							}
							break;
						}
						case 8:
						{
							if(nI>=cell.getRcount())
							  nCur=cell.getRcount();
							else
							  nCur=nI;
							 cSql.append(" AND ");
							 cSql.append(cStr1);
							 cSql.append("."+keyType+" IN(-1");
							  for(int nK=1;nK<=nCur;nK++)
							  {
								cSql.append( "," + narrId[nK]);
							  }
							  if(nI>0){
							    // cValue=cSql.toString().substring(0,cSql.toString().length()-1);
							    // cSql.delete(0,cSql.length());
							    // cSql.append(cValue);
							  }
							  cSql.append(")" );
							  if(!isCorrect)
						      {
							       cSql.append(" ORDER BY ");
							       cSql.append(cStr1);
							       cSql.append("."+keyType);
							  }
							  break;
						}					        		
					}
				}
				return cSql;
		}
	//set param format value
	private void SaveParamValue(ArrayList strLst,RGridView cell)
	 {
		 double fValue=0.0f;
		 if("N".equalsIgnoreCase(cell.getField_type()))
		 {
			 TRecParamView recP=new TRecParamView();
			 for(int nK=0;nK<strLst.size();nK++)
			 {
				 if(strLst.get(nK) !=null && ((String)strLst.get(nK)).length()>0)
				 {
					 fValue+=Double.parseDouble((String)strLst.get(nK));
				 }
			 }			
			 recP.setBflag(true);
			 recP.setFvalue(String.valueOf(fValue));
			 recP.setNid(Integer.parseInt(cell.getGridno()));
			 recParam.add(recP);
			
		 }
		 else
		 {
			 TRecParamView recP=new TRecParamView();
		 }
	 }
	/*
	 * get year format value
	 * */
public String getDateYear(int year)
{
	String cTemp="",cYear="";
	int nI,nlen;
	char cType;
	cTemp=String.valueOf(year);
	nlen=cTemp.length();
	for(nI=0;nI<nlen;nI++)
	{
		cType=cTemp.charAt(nI);
		switch(cType)
		{
			case '1':
			{
			 cYear+="一";
			 break;
			}
			case '2':
			{
				cYear+="二";
				 break;
			}
			case '3':
			{
				cYear+="三";
				break;
			}
	
			case '4':
			{

				cYear+="四";
			   break;
			}
			case '5':
			{
				cYear+="五";
				break;
			}
			case '6':
			{
				cYear+="六";
				break;
			}
			case '7':
			{
				cYear+="七";
				break;
			}
			case '8':
			{
				cYear+="八";
				break;
			}
			case '9':
			{
				cYear+="九";
				break;
			}
			case '0':
			{
				cYear+="零";
				break;
			}
		 }
	}
	return cYear;		
}
/*
 * get month format value
 * */
public String getDateMonth(int month)
{
	String cMonth="";
	switch(month)
	{
		case 1:
		{
			cMonth="一";
			break;
		}
		case 2:
		{
			cMonth="二";
			break;
		}
		case 3:
		{
			cMonth="三";
			break;
		}
		case 4:
		{
			cMonth="四";
			break;
		}
		case 5:
		{
			cMonth="五";
			break;
		}
		case 6:
		{
			cMonth="六";
			break;
		}
		case 7:
		{
			cMonth="七";
			break;
		}
		case 8:
		{
			cMonth="八";
			break;
		}
		case 9:
		{
			cMonth="九";
			break;
		}
		case 10:
		{
			cMonth="十";
			break;
		}
		case 11:
		{
			cMonth="十一";
			break;
		}
		case 12:
		{
			cMonth="十二";
			break;
		}
	}
	return cMonth;
}
/*
 * get day format value
 * */
public String getDateDay(int day)
{
	String cDay="";
	switch(day)
	{
		case 1:
		{
			cDay="一";
			break;
		}
		case 2:
		{
			cDay="二";
			break;
		}
		case 3:
		{
			cDay="三";
			break;
		}
		case 4:
		{
			cDay="四";
			break;
		}
		case 5:
		{
			cDay="五";
			break;
		}
		case 6:
		{
			cDay="六";
		   break;
		}
		case 7:
		{
			cDay="七";
			break;
		}
		case 8:
		{
			cDay="八";
			break;
		}
		case 9:
		{
			cDay="九";
			break;
		}
		case 10:
		{
			cDay="十";
			break;
		}
		case 11:
		{
			cDay="十一";
			break;
		}
		case 12:
		{
			cDay="十二";
			break;
		}
		case 13:
		{
			cDay="十三";
			break;
		}
		case 14:
		{
			cDay="十四";
			break;
		}
		case 15:
		{
			cDay="十五";
			break;
		}
		case 16:
		{
			cDay="十六";
			break;
		}
		case 17:
		{
			cDay="十七";
			break;
		}
		case 18:
		{
			cDay="十八";
			break;
		}
		case 19:
		{
			cDay="十九";
			break;
		}
		case 20:
		{
			cDay="二十";
			break;
		}
		case 21:
		{
			cDay="二十一";
			break;
		}
		case 22:
		{
			cDay="二十二";
			break;
		}
		case 23:
		{
			cDay="二十三";
			break;
		}
		case 24:
		{
			cDay="二十四";
			break;
		}
		case 25:
		{
			cDay="二十五";
			break;
		}
		case 26:
		{
			cDay="二十六";
			break;
		}
		case 27:
		{
			cDay="二十七";
			break;
		}
		case 28:
		{
			cDay="二十八";
			break;
		}
		case 29:
		{
			cDay="二十九";
			break;
		}
		case 30:
		{
			cDay="三十";
			break;
		}
		case 31:
		{
			cDay="三十一";
			break;
		}
	}
  return cDay;
}
/*
 * get person age method
 * */
public int GetHisAge(int ncYear,int ncMonth,int ncDay,int nYear,int nMonth,int nDay)
{
	/*
	 * 根据日期获得年龄的运算
	 * */
	int nAage,nMM,nDD,Result;
	nAage=ncYear-nYear;                              
	nMM=ncMonth-nMonth;
	nDD=ncDay-nDay;
	if(nMM>0)
	{
		Result=nAage;
	}
	else if(nMM<0)
	{
		Result=nAage-1;
		if(Result <0)
		{
			Result=0;
		}
	}
	else
	{
		if(nDD>=0)
		{
			Result=nAage;
		}
		else
		{
			Result=nAage-1;
			if(Result<0)
			  Result=0;
		}
	}
	return Result;
}
/*
 * sub set condition query
 * */
public String GetSqlCond(RGridView cell,String cBase,UserView userview,byte nFlag) throws Exception
{
	//parse factor gengeral factors
	
	String strFactor=cell.getQuerycond();
	String cexpress=cell.getCexpress().toUpperCase();	
	String lexpr="";
	if(cexpress!=null&&cexpress.indexOf("<EXPR>")!=-1)
	  lexpr=cexpress.substring(cexpress.indexOf("<EXPR>")+6,cexpress.indexOf("</EXPR>"));//xuj add 2010-4-21 提取表达式
	else if(cexpress!=null)
		lexpr=cexpress;
	strFactor=strFactor.replaceAll(",","`") + "`";
	ArrayList fieldlist=new ArrayList();
    //lexpr="1";
	//strFactor="Z0301<>`";
	//cBase="Usr";
	if(nFlag==0)
		nFlag=1;
	String result="";
	if(cell.getCSetName().toUpperCase().startsWith("P")){
		fieldlist = DataDictionary.getFieldList(cell.getCSetName(),Constant.USED_FIELD_SET);
		HashMap map=new HashMap();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			map.put(item.getItemid().toLowerCase(),item);
			map.put(item.getItemid().toUpperCase(),item);
		}
		FieldItem item=new FieldItem();	  
		item.setCodesetid("0");
		item.setItemid("Content");
		item.setItemtype("A");
		item.setItemdesc("工作内容");
		item.setFieldsetid("P01");
		item.setUseflag("1");////用于判断条件所有指标是否已构库
		map.put(item.getItemid(),item);
		FactorList factorslist=new FactorList(lexpr.toString(),strFactor.toString(),cBase,true,true,true,Integer.parseInt(String.valueOf(nFlag)),userview.getUserName(),false,map);
		if(this.bizDate!=null&&this.bizDate.length()==10)
			factorslist.setAppdate(this.bizDate);
		result=factorslist.getSingleTableSqlExpression(cell.getCSetName());
		result=result.toLowerCase().replaceAll("p01.content", "per_diary_content.content");
	}else if(strFactor!=null&&strFactor.indexOf("resume_flag")!=-1)
	{
		/*FieldItem item=new FieldItem();	  
		item.setCodesetid("36");
		item.setItemid("resume_flag");
		item.setItemtype("A");
		item.setItemdesc("简历状态");
		item.setFieldsetid("zp_pos_tache");
		item.setUseflag("1");////用于判断条件所有指标是否已构库
		fieldlist.add(item);*/
		//System.out.println(lexpr.toString()+"----"+strFactor.toString());
		fieldlist = DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
		HashMap map=new HashMap();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			map.put(item.getItemid().toLowerCase(),item);
			map.put(item.getItemid().toUpperCase(),item);
		}
		FieldItem item=new FieldItem();	  
		item.setCodesetid("36");
		item.setItemid("resume_flag");
		item.setItemtype("A");
		item.setItemdesc("简历状态");
		item.setFieldsetid("Z03");
		item.setUseflag("1");////用于判断条件所有指标是否已构库
		map.put(item.getItemid(),item);
		FactorList factorslist=new FactorList(lexpr.toString(),strFactor.toString(),cBase,true,true,true,Integer.parseInt(String.valueOf(nFlag)),userview.getUserName(),false,map);
		if(this.bizDate!=null&&this.bizDate.length()==10)
			factorslist.setAppdate(this.bizDate);
		result=factorslist.getSingleTableSqlExpression("Z03");	
		if(result!=null&&result.toLowerCase().indexOf("z03.resume_flag")!=-1)
	    {
			  result=result.toLowerCase().replaceAll("z03.resume_flag", "zp_pos_tache.resume_flag");
		}
	}else{
		
		if("1".equals(cell.getIsView()))
		{
			FactorList factorslist=new FactorList(lexpr.toString(),strFactor.toString(),"su");			
			if(this.bizDate!=null&&this.bizDate.length()==10)
				factorslist.setAppdate(this.bizDate);
			result=factorslist.getSingleTableSqlExpression(cell.getCSetName());	
		}	
		else
		{
			/*if("selfinfo".equalsIgnoreCase(this.userpriv))
			{
				FactorList factorslist=new FactorList(lexpr.toString(),strFactor.toString(),cBase,true,true,true,Integer.parseInt(String.valueOf(nFlag)),userview.getUserName(),true,new HashMap());
				if(this.bizDate!=null&&this.bizDate.length()==10)
					factorslist.setAppdate(this.bizDate);
				result=factorslist.getSqlExpression();	
				if(result !=null && result.indexOf("WHERE")!=-1)
					result=result.substring(result.indexOf("WHERE") + 5);
				else
					result=null;
			}else
			{
				userview.setBmyself(true);
				if(this.bizDate!=null&&this.bizDate.length()==10)
					userview.setAppdate(this.bizDate);
				result=userview.getPrivSQLExpression(lexpr+"|"+strFactor,cBase,true,true,true,fieldlist);
				userview.setBmyself(false);
				if(result !=null && result.indexOf("WHERE")!=-1)
					result=result.substring(result.indexOf("WHERE") + 5);
				else
					result=null;
			}		*/	
			
			FactorList factorslist=new FactorList(lexpr.toString(),strFactor.toString(),cBase,true,true,true,Integer.parseInt(String.valueOf(nFlag)),userview.getUserName(),true,new HashMap());
			if(this.bizDate!=null&&this.bizDate.length()==10)
				factorslist.setAppdate(this.bizDate);
			if(nFlag==1){
				result=factorslist.getSingleTableSqlExpression(cBase+cell.getCSetName());
			}else
				result=factorslist.getSingleTableSqlExpression(cell.getCSetName());
		}
		    
	}
	return result;
}
/*
 * change code value to show value
 * */
public ArrayList GetValueofField(ArrayList strlst,String strCode)
{
	try{		
		//查询出该人员的该子集的所有纪录
		String sql="";
		String value="";
		for(int nIdx=0;nIdx<strlst.size();nIdx++)
		{
			if(strlst.get(nIdx) !=null)
			{
				String codevalue=strlst.get(nIdx).toString();
			  try{		
			  	if(strCode !=null && codevalue!=null)
			  	{
			  	  if("UN".equals(strCode))
			  	  {
			  	     value=AdminCode.getCode(strCode,codevalue)!=null?AdminCode.getCode(strCode,codevalue).getCodename():"";
			  	     if(value==null || value.length()<=0)
			  	    	 value=AdminCode.getCode("UM",codevalue)!=null?AdminCode.getCode("UM",codevalue).getCodename():"";
			  	    if(value==null || value.length()<=0)
				  	    value=AdminCode.getCode("@K",codevalue)!=null?AdminCode.getCode("@K",codevalue).getCodename():"";
			  	  }else if("UM".equals(strCode))
			  	  {
			  		value=AdminCode.getCode(strCode,codevalue)!=null?AdminCode.getCode(strCode,codevalue).getCodename():"";
			  		//tianye update start
					//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
			  			if(value==null || value.length()<=0){
			  				value=AdminCode.getCode("UN",codevalue)!=null?AdminCode.getCode("UN",codevalue).getCodename():"";
			  			}
			  		//end
			  		if(value==null || value.length()<=0)
			  			value=AdminCode.getCode("@K",codevalue)!=null?AdminCode.getCode("@K",codevalue).getCodename():"";

			  	  }else
			  	  {
			  		value=AdminCode.getCode(strCode,codevalue)!=null?AdminCode.getCode(strCode,codevalue).getCodename():""; 
			  	  }
			  	  if(StringUtils.isNotEmpty(value))
			  		  strlst.set(nIdx,value);
			  	  else
			  		  strlst.set(nIdx,codevalue);//37703 登记表中都插入了公式取兼职（单位）、取兼职（部门），当关联代码处选择了相应的代码UN、UM取不出来值
			  	}
			  	else
		        	strlst.set(nIdx,"");
			  }catch(Exception e){
			  }
			}
			else
			{
				//strlst.set(nIdx,strlst.get(nIdx));
			}		
		}
		
	}catch (Exception e){
	}
	finally{	 
	}
	return strlst;
}
  /*
   * format all values method
   * */
  public String getFormulaValue(RGridView cell)
  {
	 int nI;
	String pattern="###";       //格式化的种子
	TSyntax tsyntax=new TSyntax();
	cell.getGridno();
	for(int i=0;i<recParam.size();i++)
	{ 
		 TRecParamView trec=(TRecParamView)recParam.get(i);
		 //System.out.println(trec.getFvalue());
	}	
	//System.out.println(cell.getCexpress());
	tsyntax.Lexical(cell.getCexpress());
	tsyntax.SetVariableValue(recParam);	
	tsyntax.DoWithProgram();
	 nI=cell.getSlope(); 
	 pattern="###";   //浮点数的精度
	String cStr="";
	if(nI>0)
	 pattern+=".";
	for(int i=0;i<nI;i++)
	   pattern +="#";   
	//if(tsyntax.m_strResult !=null && tsyntax.m_strResult.length()>0)
	  //cStr=new  DecimalFormat(pattern).format(Double.parseDouble(tsyntax.m_strResult)).trim();
    if(tsyntax.m_strResult !=null && tsyntax.m_strResult.length()>0){
    	BigDecimal b = new BigDecimal(Double.parseDouble(tsyntax.m_strResult));
        BigDecimal one = new BigDecimal("1");
        cStr=b.divide(one,cell.getSlope(),BigDecimal.ROUND_HALF_UP).toString();
    }
	if(cStr==null||cStr.length()<=0)
		cStr="　";
	return cStr;
  }
  /**
   * 计划登记表按人员登记
   * @param cName
   * @param cFldname
   * @param cBase
   * @param cell
   * @param typeFlag
   * @param changeFlag
   * @param statYear
   * @param statMonth
   * @param ctimes
   * @param nId
   * @param keyType
   * @param userview
   * @param nFlag
   * @param cdatestart
   * @param cdateend
   * @param season
   * @return
   * @throws Exception
   */
  private StringBuffer getPlanSql(
			String cName,
			String cFldname,
			String cBase,
			RGridView cell,
			int typeFlag,
			int changeFlag,
			int statYear,
			int statMonth,
			int ctimes,
			String nId,     //人员编号
			String keyType,
			UserView userview,byte nFlag,String cdatestart,String cdateend,int season,Connection conn)
			throws Exception {
			StringBuffer strSql=new StringBuffer();
			
		    String cexpress=cell.getCexpress();
			StatisticPlan statisticPlan=new StatisticPlan();
			HashMap h_map=statisticPlan.getFiledSet(cexpress);
			//System.out.println(cexpress);
			String setname=(String)h_map.get("SETNAME");
			String scoretype=(String)h_map.get("SCORETYPE");			
			String fildname=h_map.get("FIELDNAME").toString();
			String func=h_map.get("FUNC").toString();
			String cStr="";	
			String perRelatePlanName=(String)h_map.get("RELATE_PLAN");//计划联名
			String relaPlanId="";
			String relaObjType="0";
			String curObjType=getPerObjectType(cell.getPlan_id(),conn);
			if(perRelatePlanName!=null&&perRelatePlanName.length()>0)
			{
				 HashMap relate_map=getRELATE_PLAN(cell,conn,perRelatePlanName);
				 if(relate_map!=null)
				 {
					 relaPlanId=(String)relate_map.get("id");
					 relaObjType=(String)relate_map.get("type");
				 }
			}			
			String table="";
			if(setname==null||setname.length()<=0)
			{
				if(cFldname!=null&&("B0110".equalsIgnoreCase(cFldname)|| "E0122".equalsIgnoreCase(cFldname)
						|| "E01A1".equalsIgnoreCase(cFldname)|| "A0101".equalsIgnoreCase(cFldname)))
				{
					table="per_object";
				}else 
				{
					if(perRelatePlanName!=null&&perRelatePlanName.length()>0)
					{
						table="per_result_"+relaPlanId;
					}else
					{
						table="per_result_"+cell.getPlan_id();
					}
				}
			}else
			{
				table=setname;
			}
			if ("per_table_xxx".equalsIgnoreCase(table))
			    table = "per_table_" + cell.getPlan_id();
			String curPlanCond="";
			String fld="";
			StringBuffer curObjCond=new StringBuffer();
			if(perRelatePlanName!=null&&perRelatePlanName.length()>0)
			{
				curPlanCond="plan_id = " + relaPlanId;
				if((Integer.parseInt(relaObjType)==this.PerObject_Dept||Integer.parseInt(relaObjType)==this.PerObject_Batch)&&Integer.parseInt(curObjType)==this.PerObject_Humam)
				{
					fld="E0122";
				}else if(Integer.parseInt(relaObjType)==this.PerObject_Unit&&Integer.parseInt(curObjType)==this.PerObject_Humam)
					fld="B0110";
				else
                    fld="object_id";
				 curObjCond.append(" object_id in (select "+fld+" from per_object ");
			     curObjCond.append(" where plan_id = " + cell.getPlan_id() + "");
				 curObjCond.append("  and object_id = '" + nId + "')");		
			}else
			{
				curPlanCond="plan_id = " + cell.getPlan_id();
				curObjCond.append("object_id = '" + nId + "'");
			}        
			String selectField= table + "."+fildname;
			if(func!=null&&func.length()>0)
				selectField= func+"("+selectField+") as "+fildname;
			if(table!=null&& "per_plan".equalsIgnoreCase(table)&&fildname!=null&& "cycle".equalsIgnoreCase(fildname))
			{
				strSql.append("SELECT * FROM " + table+ " where "+curPlanCond);//考核期间				
                return getPerCycleStr(strSql,table,curPlanCond,conn);
				
			}else if(table!=null&& "per_mainbody".equalsIgnoreCase(table))
			{
				if(fildname!=null&& "description".equalsIgnoreCase(fildname))// 员工评价
				{
					String perPlanId=cell.getPlan_id();
					cStr=fildname;
					strSql.append("SELECT " + cStr + " FROM " + table );
					strSql.append(" where " + curPlanCond + " and");
					strSql.append(" status <> 3 and ");  // 打分
					strSql.append(" "+curObjCond.toString()+" and ");
					String ext_flag=(String)h_map.get("EXT_FLAG");
					if(ext_flag!=null&&ext_flag.length()>0)
					{
						strSql.append("  body_id = "+ext_flag+" and "); // 非本人
					}else
					   strSql.append("  body_id <> 5 and "); // 非本人
					strSql.append( fildname + " is not null ");
					strSql.append(" order by body_id ");	
				}else if(fildname!=null&&fildname.toUpperCase().indexOf("A0101_")!=-1)
				{
					// 某类别考核主体姓名
					String body_id=fildname.substring(6);
					String perPlanId=cell.getPlan_id();
					cStr="a0101";
					strSql.append("SELECT " + cStr + " FROM " + table );
					strSql.append(" where " + curPlanCond + " and ");				
					strSql.append(" "+curObjCond.toString()+" and ");
					strSql.append("  body_id = '"+body_id+"'"); // 非本人
					//strSql.append( cStr + " is not null ");
					strSql.append(" order by body_id ");
				}else if(fildname!=null&& "whole_grade_id".equalsIgnoreCase(fildname)) // 总体评价
				{
					String perPlanId=cell.getPlan_id();
					strSql.append("select distinct itemname from per_degreedesc ");
					strSql.append(" where id in (select whole_grade_id from per_mainbody");
					strSql.append(" where " + curPlanCond + " and ");
					strSql.append(" status <> 3 and ");
					strSql.append(" object_id = "+nId+" and whole_grade_id is not null");
					String ext_flag=(String)h_map.get("EXT_FLAG");
					if (ext_flag!=null&&ext_flag.length()>0)
					{
						strSql.append(" and body_id ="+ext_flag+"");
					}else
					{
						strSql.append(" and body_id <>5");// 非本人
					}
			        strSql.append(")");
				}
			}else if(table!=null&& "per_interview".equalsIgnoreCase(table))//// 面谈记录
			{
				cStr=fildname;
				strSql.append("SELECT " + cStr + " FROM " + table );
				strSql.append(" where " + curPlanCond + " and " + curObjCond);
			}else if(table!=null&& "per_object".equalsIgnoreCase(table))
			{
				 if(func!=null&&func.length()>0)
				 {
					 cStr= func+"("+fildname+") as "+fildname;
					 strSql.append("SELECT " + cStr + " FROM " + table );
					 strSql.append(" where " + curPlanCond + " and " + curObjCond);
					 String scope=(String)h_map.get("SCOPE");
					 if(scope!=null&& "2".equals(scope)) // 范围: 同类别人员
				     {
							strSql.append(" and body_id in (select body_id from " + table );
							strSql.append(" where " +curPlanCond+" and "+curObjCond+")");                     
					 }					  
				 }else
				 {
					 cStr=fildname;
					 strSql.append("SELECT " + cStr + " FROM " + table );
					 strSql.append(" where " + curPlanCond + " and " + curObjCond);
				 }
				
			}else if(table!=null&&table.startsWith("per_table_"))
			{
			    if ((cell.getExtflag() != null && cell.getExtflag().length() > 0) && 
			        (cell.getExtflag2() != null && cell.getExtflag2().length() > 0)) {
			    	String mainbody;
			    	if("reasons".equalsIgnoreCase(fildname)) {
			    		cStr = "m."+fildname+"_1 as reasons ";
			    		mainbody="(select per_mainbody.*,per_target_evaluation.reasons as reasons_1  "
			    				+ "from per_mainbody ,per_target_evaluation "
			    				+ "where per_mainbody.mainbody_id=per_target_evaluation.mainbody_id "
			    				+ "and per_mainbody.plan_id='"+cell.getPlan_id()+"' "
			    				+ "and per_mainbody.plan_id = per_target_evaluation.plan_id  "
			    				+ "and per_target_evaluation.p0400 in (select p0400 from p04 where plan_id='"+cell.getPlan_id()+"' "
			    														+ "and A0100='"+nId+"' and p0401='"+cell.getExtflag2()+"')"
			    				+ ")";
			    	}else {
			    		mainbody="per_mainbody";
			    		cStr = "d."+fildname;
			    	}
			    	
			        strSql.append("SELECT m.B0110, m.E0122, m.E01A1, m.A0101," + cStr);
			        strSql.append(" FROM " + table + " d, "+mainbody+" m");
			        strSql.append(" where d.mainbody_id= m.mainbody_id and d.object_id=m.object_id and ");
			        strSql.append(" d."+ curObjCond + " and d.point_id = '"+cell.getExtflag2()+"' and ");
			        strSql.append(" m."+curPlanCond + " and m.body_id = " + cell.getExtflag());
			        strSql.append(" order by m.seq,m.id");
			    }
			}else if(table!=null&& "p04".equalsIgnoreCase(table))
			{
				String bodyid=(String)h_map.get("SCORETYPE");
				String perPlanId=cell.getPlan_id();
				strSql.append(getP04Sql( setname, perPlanId, bodyid,cBase, nId, conn,fildname,cell));
			}else if("A01".equalsIgnoreCase(table))
			{
				cStr=fildname;
				if(Integer.parseInt(curObjType)==this.PerObject_Humam)
				{
					strSql.append("select "+cStr+" from "+cBase+"A01");
					strSql.append(" where ");
					strSql.append("  a0100 = '" +nId+"'");
				}else
				{
					strSql.append("select "+cStr+" from "+cBase+"A01");
					strSql.append(" where 1=2");					
				}	
				
			}else if("per_result_correct".equalsIgnoreCase(table)) // 修正原因/修正分值
			{
				 if(func!=null&&func.length()>0)
				 {
						cStr = func + "(" + fildname + ")";
						strSql.append("select "+cStr+" from "+table+"");
						strSql.append(" where  "+curPlanCond);
						String scope=(String)h_map.get("SCOPE");
						if(scope!=null&& "2".equals(scope))// 范围: 同类别人员
						{
							strSql.append(" and object_id in (select object_id from per_object ");
							strSql.append(" where " + curPlanCond );
							strSql.append(" and body_id in (select body_id from per_object ");
							strSql.append(" where  "+curPlanCond + " and "+  curObjCond +")"); 
							strSql.append(")");
						}
				}else
				{
					 cStr=fildname;
					 strSql.append("SELECT " + cStr + " FROM " + table );
					 strSql.append(" where " + curPlanCond + " and " + curObjCond);
				}
			}else if(fildname!=null&&fildname.indexOf("C_")==0&&scoretype!=null&&scoretype.trim().length()>0)
			{
				//System.out.println(cFldname);
				String pIID = fildname.substring(2);
				String perPlanId=cell.getPlan_id();
				strSql.append("select Avg(" + Sql_switcher.isnull("score", "0") + ") AS "+ cFldname);
				strSql.append(" from PER_TABLE_" + perPlanId+" ");
				strSql.append(" where mainbody_Id in (select mainbody_id FROM per_mainbody ");
				strSql.append(" where " + curPlanCond + " and ");				
				strSql.append(" "+curObjCond+" and");
				strSql.append(" body_id = " + scoretype +" and status=2)");						
				strSql.append(" and "+curObjCond+""); 
				strSql.append(" and point_id = '" + pIID+"'");				
				
			}else if(fildname!=null&& "body_id".equalsIgnoreCase(fildname))
			{
				if(func!=null&&func.length()>0)
				{
					cStr = func + "(m.name) as "+fildname;
					strSql.append("select "+cStr+" from "+table+" r");
					strSql.append(",per_mainbodyset m ");  
					strSql.append(" where r.body_id=m.body_id");
					String scope=(String)h_map.get("SCOPE");
					if(scope!=null&& "2".equals(scope))
					{
						strSql.append(" and r.body_id in (select body_id from " + table );
						strSql.append(" where "+curObjCond+")");                     
					}
				}else
				{
					strSql.append("SELECT m.name FROM "+table+" r,per_mainbodyset m ");   
					strSql.append(" where r.body_id=m.body_id and r."+curObjCond+"");
				}
				
			}else if(func!=null&&func.length()>0)
			{
				cStr = func + "(" + fildname + ")";
				strSql.append("select "+cStr+" from "+table+"");
				strSql.append(" where 1=1 ");
				String scope=(String)h_map.get("SCOPE");
				if(scope!=null&& "2".equals(scope))
				{
					strSql.append(" and body_id in (select body_id from " + table );
					strSql.append(" where "+curObjCond+")");                     
				}
			}else if(fildname!=null&& "summarize".equalsIgnoreCase(fildname))
			{
				String perPlanId=cell.getPlan_id();
				strSql.append(getPlanobjSummarize(cName,perPlanId,nId,conn));
			}else
			{
				cStr=fildname;
				strSql.append("select "+cStr+" from "+table);
				strSql.append(" where 1=1 ");
				strSql.append(" and "+curObjCond+"");
			}		
			//System.out.println(strSql.toString());
			return strSql;
		}
  private StringBuffer getZPSql(
			String cName,
			String cFldname,
			String cBase,
			RGridView cell,
			int typeFlag,
			int changeFlag,
			int statYear,
			int statMonth,
			int ctimes,
			String nId,     //人员编号
			String keyType,
			UserView userview,byte nFlag,String cdatestart,String cdateend,int season,Connection conn)
			throws Exception {
	  StringBuffer cSql=new StringBuffer();
	  String cStr="";
	  String cStr1="";
	  int nI=0;
	  int nMin=0;
	  int nMax=0;
	  String I9999 = "";
	  ArrayList narrId=new ArrayList();
	  boolean isCorrect=false;
	  if("N".equalsIgnoreCase(cell.getField_type())&& "1".equals(cell.getFunc()))
	  {
			isCorrect=true;
	  }
	  String zp_base=getZPBASE(conn);//招聘人员库
	  String selectField = cell.getCSetName() + "."+cell.getField_name();
	  String sub_domain=cell.getSub_domain();
	  anySub_domain(cell);
	  int nMode=Integer.parseInt(cell.getMode());
	  switch(this.FUNCTYPE)
	  {	
		   case 0://无
		   {
			    if(isCorrect)
				{
				   selectField="sum("+selectField+") as "+cFldname;				   
				}
			   break;
		   }
		   case 1://this.FUNCTYPE_SUM
		   {
			   selectField="sum("+selectField+") as "+cFldname;
			   isCorrect=true;
			   break;
		   }
		   case 2://FUNCTYPE_MAX  = 2;		  
		   {
			   selectField="MAX("+selectField+") as "+cFldname;
			   isCorrect=true;
			   break;
		   }
		   case 3: //FUNCTYPE_MIN  = 3;
		   {
			   selectField="MIN("+selectField+") as "+cFldname;
			   isCorrect=true;
			   break;
		   }
		   case 4:// FUNCTYPE_CNT  = 4;
		   {
			   selectField="count("+selectField+") as "+cFldname;
			   isCorrect=true;
			   break;
		   }
		   case 5://FUNCTYPE_AVG  = 5;
		   {
			   selectField="avg("+selectField+") as "+cFldname;
			   isCorrect=true;
			   break;
		   }
		}
	  if("Z03".equals(cell.getCSetName())){
		  cStr=cBase+"A01";
          cStr1= "zp_pos_tache";
	  }else
	  {
		  cStr=cBase+"A01"; //主集表名
          cStr1=cBase+cName; //子集表名
	  }
	  if(!"A01".equalsIgnoreCase(cName))
	  {
		  //System.out.println(cell.getCSetName());
		  if("Z03".equals(cell.getCSetName()))
		  {
			  cSql.append("SELECT "+selectField+" From "+cStr+",Z03,zp_pos_tache");
			  cSql.append(" where "+cStr+".A0100 = zp_pos_tache.A0100 and zp_pos_tache.zp_pos_id=z03.z0301 and ");
			  cSql.append( cStr+".A0100 = '"+nId+"'");
			  if(zp_base!=null&&!zp_base.equalsIgnoreCase(cBase))
				  cSql.append(" and 1=2");
		  }else
		  {
			  cSql.append("SELECT "+selectField+" From "+cStr1+","+cStr);
			  cSql.append(" where "+cStr+".A0100="+cStr1+".A0100 AND "+cStr+".A0100='" +nId+"'");
		  }
		  String cSql1="";
		  String where="";
		  
		  if("Z03".equals(cell.getCSetName()))
		  {
			  cSql1="SELECT zp_pos_tache.theNumber From zp_pos_tache,Z03,"+cStr;
			  where=cStr+".A0100=zp_pos_tache.A0100 and zp_pos_tache.zp_pos_id=z03.z0301 and "+cStr+".A0100='"+nId+"'";
			  if(zp_base!=null&&!zp_base.equalsIgnoreCase(cBase))
				  where=where+" and 1=2";
			  I9999 = "theNumber";
		  }else
		  {
			  cSql1="SELECT "+cStr1+".I9999 From "+cStr1+","+cStr;
              where = cStr+".A0100="+cStr1+".A0100 AND "+cStr+".A0100='"+nId+"'";
              I9999 = "I9999";
		  }
		  
		  if(nMode>=5&&nMode<=10)
		  {
			  String result=GetSqlCond(cell,cBase,userview,nFlag);			  
			  if(result!=null&&result.length()>0)
				  where = where +" and "+result;
			  else
				  where =where +" and 1=2";
		  }
		  cSql1=cSql1+ " where "+where +" order by "+cStr1+"."+ I9999;
		  ContentDAO dao=new ContentDAO(conn);
		  RowSet rs=null;
		  
		  try{
		     rs=dao.search(cSql1);
		     while(rs.next())
		     {
		    	 int n9999=rs.getInt(I9999);
		    	 narrId.add(nI,rs.getInt(I9999)+"");
		    	 nI=nI+1;
                 if (n9999<nMin) 
                    nMin=n9999;
                 if (n9999>nMax)
                    nMax=n9999;
		     }		    
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }finally
		  {
			  if(rs!=null)
				  rs.close();
		  }			  
	  } 
	  String nCur="0";
	  switch(nMode)//历史记录取值方式
	  {	
		   case 0://倒数第...条(最近第)
		   case 5:
		   {
			   if (nI>=cell.getRcount()) //子集记录数大于要取的记录数。
		       {
				   //System.out.println(nI+"--"+cell.getRcount());
				   nCur=(String)narrId.get(nI-cell.getRcount());//[nI-cell.nRCount+1]
		       }
		       else 
		    	   nCur="0";//否则取不到数据。
		       cSql.append(" And "+cStr1+"."+I9999+"="+nCur);
			   break;
		   }
		   case 9://最近第(带偏移)
		   case 10:
		   {
			   //cSql.append(" And "+cStr1+".I9999="+nCur);
			   cSql.append(" And "+cStr1+"."+I9999+"="+nMax);
			   break;
		   }
		   case 1://倒数...条（最近）
		   {

			   if (nI>=cell.getRcount())
			     nCur=(nI-cell.getRcount())+"";
		       else 
		        nCur="0"; //取全部记录。
			   cSql.append(" And "+cStr1+"."+I9999+">="+nCur+" And "+cStr1+"."+I9999+"<="+nMax);
			   cSql.append( " ORDER BY "+cStr1+"."+I9999);
			   break;
		   }
		   case 2://正数第...条（最初第）
		   case 7:
		   {
			   if (nI>=cell.getRcount())
	                 nCur=(String)narrId.get(cell.getRcount()-1);
	               else 
	            	 nCur=nMax+"";
			   cSql.append(" And "+cStr1+"."+I9999+"="+nCur);
			   cSql.append(" ORDER BY "+cStr1+"."+I9999);
			   break;
		   }
		   case 3://正数...条（最初）
		   {
			   if (nI>=cell.getRcount())
                 nCur=(String)narrId.get(cell.getRcount()-1);
               else 
            	 nCur=nMax+"";
			   cSql.append(" And "+cStr1+"."+I9999+"<="+nCur+" And "+cStr1+"."+I9999+">="+nMin);
			   cSql.append( " ORDER BY "+cStr1+"."+I9999);
			   break;
		   }
		   case 4:////条件定位 待语法分析完成后再进行处理？
		   {
			   String result=GetSqlCond(cell,cBase,userview,nFlag);
			   if(result!=null&&result.length()>0)
				  cSql.append(" and "+result);
			   else
				  cSql.append(" and 1=2");
			   cSql.append( " ORDER BY "+cStr1+"."+I9999);
			   break;
		   }
		   case 6://倒数...条（最近）(条件)
		   {
			   if (nI>=cell.getRcount())
			     nCur=(nI-cell.getRcount())+"";
		       else 
		        nCur="0"; //取全部记录。
			   cSql.append(" And "+cStr1+"."+I9999+" IN(");
			   boolean isH=false;
			   for(int i=Integer.parseInt(nCur);i<nI-1;i++)
			   {
				   cSql.append(""+narrId.get(i)+",");
				   isH=true;
			   }
			   if(nI>0&&isH)
				   cSql.setLength(cSql.length()-1);
			   if(!isH)
			   {
				   cSql.append("0");
			   }
			   cSql.append(")");
			   cSql.append( " ORDER BY "+cStr1+"."+I9999);
			   break;
		   }
		   case 8://正数...条（最初）(条件)
		   {
			   if (nI>=cell.getRcount())
		            nCur=cell.getRcount()+"";
		        else nCur=nI+""; //取全部记录。
			   cSql.append(" And "+cStr1+"."+I9999+" IN(");
			   for(int i=0;i<nI-1;i++)
			   {
				   cSql.append(""+narrId.get(i)+",");
			   }
			   if(nI>0)
				   cSql.setLength(cSql.length()-1);
			   cSql.append(")");
			   cSql.append( " ORDER BY "+cStr1+"."+I9999);
			   break;
		   }
	  }	  
	  return cSql;
	  
  }
  /**
   * 招聘人员库
   * @param conn
   * @return
   */
  private String getZPBASE(Connection conn)
  {
	  String sql="select str_value from constant where Upper(constant)='ZP_DBNAME'";
	  ContentDAO dao=new ContentDAO(conn);
	  RowSet rs=null;
	  String zpbase="";
	  try {
		rs=dao.search(sql);
		if(rs.next())
			zpbase=rs.getString("str_value");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally
	{
		if(rs!=null)
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	return zpbase;
  }
  private String getP04Sql(String setname,String plan_id,String bodyid,String userbase,String nid,Connection conn,String fieldname,RGridView cell)
  {
	     String objCond=getPlanobjCond(setname,plan_id,userbase,nid,conn);
	     RowSet rset = null;
		    ContentDAO dao=new ContentDAO(conn);			
			int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
			int nI=0;
			try{
				/**xuj and 2012*/
				String querycond=cell.getQuerycond();
				 String sqls="";
				if(querycond!=null&&querycond.length()>0){
			     String cexpress = cell.getCexpress();
			     String expr=cexpress.substring(cexpress.indexOf("<EXPR>")+6,cexpress.indexOf("</EXPR>"));
			     String[] tmps =querycond.split(",");
			     StringBuffer tmpsql=new StringBuffer(" and (");
			     if(tmps.length>1){
				     for(int i=0;i<tmps.length-1;i++){
				    	 tmpsql.append(tmps[i]);
				    	 String sign=expr.substring(expr.indexOf(""+(i+1))+1,expr.indexOf(""+(i+2)));
				    	 if("*".equals(sign))
				    		 tmpsql.append(" and ");
				    	 else if("+".equals(sign))
				    		 tmpsql.append(" or ");
				     }
				     if(tmpsql.length()>6)
				    	 sqls=tmpsql.substring(0, tmpsql.length()-4)+")";
			     }else{
			    	 tmpsql.append(tmps[0]+")");
			    	 sqls=tmpsql.toString();
			     }
				}
				 //System.out.println(sqls);
					StringBuffer cSql1=new StringBuffer();
					cSql1.append("select seq from P04  where ");
					cSql1.append(objCond);
					cSql1.append(sqls);
					cSql1.append(" order by seq");
					
					//查询出该人员的该子集的所有纪录
					rset=dao.search(cSql1.toString());
					for(nI=0;rset.next();nI++)
					{
						//把各个纪录放到数组中以便查询出符合条件的某条纪录
						narrId[nI +1]=Integer.parseInt(rset.getString("seq"));
						if(narrId[nI + 1]<NMIN)
						   NMIN=narrId[nI + 1];
						if(narrId[nI +1]>NMAX)
						   NMAX=narrId[nI + 1];
					}
					
				}catch (SQLException sqle){
		            sqle.printStackTrace();
	            }	            
	            finally{
		          try{
			        if (rset != null){
				     rset.close();
			        }
			        
			      
		          }catch (SQLException sql){
			         sql.printStackTrace();
		         }
	        }
	     StringBuffer termSql=new StringBuffer();
	     if (cell.getMode() != null && cell.getMode().length() > 0) {
			switch (Integer.parseInt(cell.getMode())) {
			case 0: {
				termSql.append(" AND ");
				termSql.append("p04.seq=");
				if (nI >= cell.getRcount())
					termSql.append(narrId[nI - cell.getRcount() + 1]);
				else
					termSql.append(0);

				break;
			}
			case 5: {
				termSql.append(" AND ");
				termSql.append("p04.seq=");
				if (nI >= cell.getRcount())
					termSql.append(narrId[nI - cell.getRcount() + 1]);
				else
					termSql.append(0);
				break;
			}
			case 1: {
				termSql.append(" AND ");
				termSql.append("p04.seq>=");
				if (nI >= cell.getRcount())
					termSql.append(narrId[nI - cell.getRcount() + 1]);
				else
					termSql.append(0);
				termSql.append(" AND ");
				termSql.append("p04.seq<=");
				termSql.append(NMAX);
				break;
			}
			case 2: {
				termSql.append(" AND ");
				termSql.append("p04.seq=");
				termSql.append(narrId[cell.getRcount()]);
				break;
			}
			case 7: {
				termSql.append(" AND ");
				termSql.append("p04.seq=");
				termSql.append(narrId[cell.getRcount()]);
				break;
			}
			case 3: {
				termSql.append(" AND ");
				termSql.append("p04.seq<=");
				if (nI >= cell.getRcount())
					termSql.append(narrId[cell.getRcount()]);
				else
					termSql.append(NMAX);
				termSql.append(" AND ");
				termSql.append("p04.seq>=");
				termSql.append(NMIN);
				break;
			}
			case 4: {
				break;
			}
			case 6: {
				int nCur = 0;
				if (nI >= cell.getRcount())
					nCur = nI - cell.getRcount() + 1;
				else
					nCur = 1;
				termSql.append(" AND ");
				termSql.append("p04.seq IN(-1");
				for (int nK = nCur; nK <= nI; nK++) {
					termSql.append("," + narrId[nK]);
				}
				termSql.append(")");

				break;
			}
			case 8: {
				int nCur = 0;
				if (nI >= cell.getRcount())
					nCur = cell.getRcount();
				else
					nCur = nI;
				termSql.append(" AND ");
				termSql.append("p04.seq IN(-1");
				for (int nK = 1; nK <= nCur; nK++) {
					termSql.append("," + narrId[nK]);
				}
				termSql.append(")");
				break;
			}
			}
		}
	     
	     StringBuffer cSql=new StringBuffer();
	     if("score".equalsIgnoreCase(fieldname))
	     {
	    	 StringBuffer mainBodyCond =new StringBuffer();
			 mainBodyCond.append(" mainbody_Id in (select mainbody_id FROM per_mainbody");
			 mainBodyCond.append(" where plan_id = '" + plan_id + "' and");
			 mainBodyCond.append(" object_id = '" + nid+ "' and");
			 mainBodyCond.append(" body_id = '" + bodyid+"'" );
			 mainBodyCond.append(")");
			 cSql.append("select ascore "); // cell.cFldName 主体类别可能为负数
			 cSql.append(" from P04, (select P0400, Avg(" + Sql_switcher.isnull("score", "0")+ ") AS ascore ");
			 cSql.append(" from per_target_evaluation ");
			 cSql.append(" where " + mainBodyCond.toString() + " and plan_id = '"+plan_id+"' ");
			 cSql.append(" group by P0400");
			 cSql.append(") a");
			 cSql.append(" where P04.P0400 = a.P0400 and " + objCond);
			 cSql.append(" "+termSql);
			 cSql.append(" order by seq");
	     }else{
	    	 StringBuffer fields=new StringBuffer();
	    	 if("P0424".equalsIgnoreCase(fieldname)) // P0424调整人只取姓名: Usr00000629/张普法
				{
					fields.append(Sql_switcher.substr("P0424", "13", Sql_switcher.length("P0424"))+" as P0424");
				}else{
				   fields.append(fieldname);
	             } 

	          cSql.append("SELECT " + fields.toString() + " From "+setname);
	          cSql.append(" where "+objCond);
	          cSql.append(" "+termSql);
	          cSql.append(" order by seq");	         
	     }
	     //System.out.println( cSql.toString());
	     return cSql.toString();
	     
  }
  
  private String getPlanobjCond(String setname,String plan_id,String userbase,String nid,Connection conn)
  {
	    String sql="select object_type from per_plan where plan_id = " + plan_id;
	  	String cStr1=setname;
	  	
	  	ContentDAO dao=new ContentDAO(conn);
	  	StringBuffer objCond=new StringBuffer();
	  	RowSet rs=null;
	  	String object_type="";    	
	  	try {
				rs=dao.search(sql);
				if(rs.next())
					object_type=rs.getString("object_type");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		objCond.append(" P04.plan_id = " + plan_id+ " and ");
      	objCond.append(Sql_switcher.isnull("P04.Chg_type", "0")+ "<> 3");  // 不显示已删除的任务
  		if(object_type!=null&& "2".equals(object_type))
  			objCond.append(" and upper(p04.nbase)='"+userbase.toUpperCase()+"' and p04.a0100='"+nid+"'");
  		else
  			objCond.append(" and p04.b0110='"+nid+"'");
  	
  	
		return objCond.toString();
  }
  /**
   * 得到考核计划属性
   * @param plan_id
   * @param conn
   * @return
   */
  private String getPerObjectType(String plan_id,Connection conn)
  {
	    String sql="select object_type from per_plan where plan_id = " + plan_id;
	  	
	  	
	  	ContentDAO dao=new ContentDAO(conn);
	  	StringBuffer objCond=new StringBuffer();
	  	RowSet rs=null;
	  	String object_type="";    	
	  	try {
				rs=dao.search(sql);
				if(rs.next())
					object_type=rs.getString("object_type");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
  	
		return object_type;
  }
  private String getPlanobjSummarize(String cName,String plan_id,String nid,Connection conn)
  {
	    String sql="select object_type from per_plan where plan_id = " + plan_id;
	  	
	  	
	  	ContentDAO dao=new ContentDAO(conn);
	  	StringBuffer objCond=new StringBuffer();
	  	RowSet rs=null;
	  	String object_type="";
	  	StringBuffer cSql2 =new StringBuffer();;    	
	  	try {
				rs=dao.search(sql);
				if(rs.next())
					object_type=rs.getString("object_type");
				
				cSql2.append("SELECT Content FROM per_article ");
				cSql2.append(" WHERE plan_id='"+ plan_id +"' AND Article_type=2 AND fileflag=1");
				if(object_type!=null&& "2".equals(object_type))
				{
					cSql2.append(" AND A0100 = '"+nid+"'");
				}else
				{
					cSql2.append(" AND A0100 = (SELECT mainbody_id FROM per_mainbody ");
					cSql2.append("  WHERE plan_id= '"+ plan_id +"' AND body_id=-1 AND ");
					cSql2.append("  object_id='"+nid+"') ");
				}
				rs=dao.search(cSql2.toString());
				String Content="";
				if(rs.next())
				{
					return cSql2.toString();
				}

		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}finally{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
		cSql2=new StringBuffer();
		cSql2.append("select summarize from "+cName);
		cSql2.append(" where 1=1 ");
		cSql2.append(" and object_id = '" +nid+"'");
		return cSql2.toString();

  }
  /**
   * 计划
   * @param cell
   * @param conn
   * @return
   */
     private ArrayList getPerCycleStr(RGridView cell,Connection conn,String table,String field)
     {
    	 String cSql = "SELECT * FROM per_plan where plan_id = "+cell.getPlan_id();
         ContentDAO dao=new ContentDAO(conn);
         ArrayList list=new ArrayList();  
         String text="";
         try
         {
        	 RowSet rs=dao.search(cSql);
        	 if(rs.next())
        	 {
        		 String cycle=rs.getString("cycle");
        		 if(cycle!=null&&cycle.length()>0)
        		 {
        			 switch(Integer.parseInt(cycle))
        			 {
        			    case 0://年度
        				{
        					text=year_GetText(rs.getString("theyear"));
        					break;
        				}
        			    case 1://半年
        				{
        				    if(getSemiYear(rs.getString("thequarter")).length()>0)
        				    {
        				    	text=year_GetText(rs.getString("theyear"))+" "+getSemiYear(rs.getString("thequarter"));
        				    }
        					break;
        				}
        			    case 2://季度
        				{
        					if(!"".equals(quarter_GetText(rs.getString("thequarter"))))
        					{
        						text=year_GetText(rs.getString("theyear"))+" "+quarter_GetText(rs.getString("thequarter"));
        					}
        					break;
        				}
        			    case 3://月度
        				{
        					if(!"".equals(month_GetText(rs.getString("themonth"))))
        					{
        						text=year_GetText(rs.getString("theyear"))+" "+month_GetText(rs.getString("themonth"));
        					}
        					break;
        				}
        			    case 7://不定期
        				{
        					text=DateUtils.format(rs.getDate("start_date"),"yyyy.MM.dd")+"-"+DateUtils.format(rs.getDate("end_date"),"yyyy.MM.dd");
        					break;
        				}
        			 }
        		 }
        		 list.add(text);
        	 }
         }catch(Exception e)
         {
        	 e.printStackTrace();
         }
         
         return list;
     } 
     private String year_GetText(String value)
     {
    	 if(value!=null&&value.length()>0)
    		 value=value+" 年";
    	 return value;
     }
     private String getSemiYear(String quarter)
     {
    	 if(quarter==null||quarter.length()<=0)
    		 return "";
    	 String v1=quarter.substring(0,1);
    	 String re="";
    	 if("1".equals(v1))
    	 {
    		 re="上半年";
    	 }else if("2".equals(v1))
    	 {
    		 re="下半年";
    	 }
    	 return re;
     }
     private String quarter_GetText(String quarter)
     {
    	 String re="";
    	 if(quarter!=null||quarter.length()>0){
    		 re=AdminCode.getCode("12",quarter)!=null?AdminCode.getCode("12",quarter).getCodename():"";;
    	 }
    	 return re;
     }
     private String month_GetText(String quarter)
     {
    	 String re="";
    	 if(quarter!=null||quarter.length()>0){
    		 re=AdminCode.getCode("13",quarter)!=null?AdminCode.getCode("13",quarter).getCodename():"";;
    	 }
    	 return re;
     }
     /***
      * 分析Sub_domain字段的xml内容
      * @param rgrid
      */
     private void anySub_domain(RGridView rgrid)
     {
    	 String sub_domain=rgrid.getSub_domain();
 		 if(sub_domain==null||sub_domain.length()<=0)
 		 {
 			this.FUNCTYPE=0;
 			return ;
 		 }
 			
		try
		{
			Document doc=PubFunc.generateDom(sub_domain);
			String str_path="/sub_para/para";
			String functype=getValue(doc,str_path,"func");
			if(functype==null||functype.length()<=0)
				functype="0";
			this.FUNCTYPE=Integer.parseInt(functype);
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
     }
    private String getValue(Document doc,String str_path,String property)
 	{
 		  String value="";
 		  try
 		  {
 			XPath xpath=XPath.newInstance(str_path);
 			List childlist=xpath.selectNodes(doc);
 			Element element=null;
 			if(childlist.size()!=0)
 			{
 				element=(Element)childlist.get(0);
 				value=element.getAttributeValue(property);	
 			}
 		  }
 		  catch(Exception ex)
 		  {
 			  ex.printStackTrace();
 		  }	
 		return value;		
 	}
	public String getDisplay_zero() {
		return display_zero;
	}
	public void setDisplay_zero(String display_zero) {
		this.display_zero = display_zero;
	}
    
	
	private HashMap getRELATE_PLAN(RGridView cell,Connection conn,String relate_plan)
    {
   	    String cSql = "SELECT * FROM per_plan where plan_id = "+cell.getPlan_id();
        ContentDAO dao=new ContentDAO(conn);
        HashMap map=new HashMap();  
        String text="";
        try
        {
       	   RowSet rs=dao.search(cSql);       	    
       	   if(rs.next())
       	   {
       		 String parameter_content=Sql_switcher.readMemo(rs, "parameter_content");       		 
       		 if(parameter_content!=null&&parameter_content.length()>0)
       		 {

     			Document doc=PubFunc.generateDom(parameter_content);	
     			String str_path="/PerPlan_Parameter/RelatePlan";
     			XPath xpath =XPath.newInstance(str_path);
     			boolean isCorrect=true;
     			try {
     				
     				List list=xpath.selectNodes(doc);
     				Element element=null;
     				String info="";			
     				if(list!=null&&list.size()>0)
     				{				
     					element=(Element)list.get(0);
     					List clist=element.getChildren();     					
     					if(clist!=null&&clist.size()>0)
     					{
     						Iterator i=clist.iterator();
     						while(i.hasNext())
     						{
     							Element elementC=(Element)i.next();
     							if(elementC.getAttributeValue("Name")!=null&&elementC.getAttributeValue("Name").equals(relate_plan))
     							{
     								map.put("id", elementC.getAttributeValue("ID"));
     								map.put("type", elementC.getAttributeValue("Type"));
     								map.put("menus", elementC.getAttributeValue("Menus"));
     							}
     						}
     					}
     				}
     			} catch (Exception e) {
     				// TODO Auto-generated catch block			
     				e.printStackTrace();
     				throw GeneralExceptionHandler.Handle(e);	
     			}	
       		 }
       	   }else
       		   return null;
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }        
        return map;
    } 
	
	/**
	 * 
	 * @param userbase
	 * @param conn
	 * @param card
	 * @param rgrid
	 * @param userview
	 * @param nFlag
	 * @param alUsedFields
	 * @param infokind  1人员登记表2单位登记表4职位登记表5计划登记表
	 * @return
	 */
	public synchronized ArrayList getTextValueForCexpress(String userbase, Connection conn, GetCardCellValue card, RGridView rgrid, UserView userview, ArrayList alUsedFields, String infokind,String nId,String planId)
	{
		String c_expr=rgrid.getCexpress();
		c_expr=getC_exprForCexpress(c_expr);
		int varType = 8;
		int infoGroup=YksjParser.forPerson;
		int nFlag=0;
		if("1".equals(infokind))
		{
			infoGroup=YksjParser.forPerson;
			nFlag=0;
		}else if("2".equals(infokind))
		{
			infoGroup=YksjParser.forUnit;
			nFlag=2;
			userbase="";
		}if("4".equals(infokind))
		{
			infoGroup=YksjParser.forPosition;
			nFlag=4;
			userbase="";
		}else if("5".equals(infokind))
		{
			infoGroup=YksjParser.forPerson;
			
		}else if("6".equals(infokind))  // 基准岗位
		{
			nFlag=6;
			infoGroup=YksjParser.forSearch;			
		}
		String field_type=rgrid.getField_type()!=null&&rgrid.getField_type().length()>0?rgrid.getField_type():"";
		int slope=rgrid.getSlope();
		if("N".equalsIgnoreCase(field_type)&&slope==0)
		{
			varType=5;
		}else if("N".equalsIgnoreCase(field_type)&&slope>0)
		{
			varType=6;
		}else if("A".equalsIgnoreCase(field_type))
		{
			varType=7;
		}else if("D".equalsIgnoreCase(field_type))
		{
			varType=9;
		}
		String FSQL=getFQL(userview,alUsedFields,varType,infoGroup,c_expr,"",userbase,nFlag,field_type,slope,nId,conn,infokind,rgrid,planId);		
		ArrayList valuelist=new ArrayList();
		if(FSQL!=null&&FSQL.length()>0)
		{
			StringBuffer sql=new StringBuffer(FSQL);
			valuelist=getCellListValue(sql,rgrid,conn);
			//创建临时表时，获取用户名去用户名中的“.”和“@” chenxg 2016-08-15
            String usrName = userview.getUserName();
            if(usrName.indexOf(".") > -1)
                usrName = usrName.replace(".", "");
            
            if(usrName.indexOf("@") > -1)
                usrName = usrName.replace("@", "");
            
            String tablename="t#"+usrName;
			dropTable(tablename,conn);
		}
		return valuelist;
	}
	/**
	 * 
	 * @param uv
	 * @param alUsedFields
	 * @param varType  变量类型(整数 浮点 字符 日期 逻辑)  5:int;6:float;7:string;9:date
	 * @param infoGroup // 0:人员 1:单位 2:部门 3:职位
	 * @param c_expr
	 * @param FSQL
	 * @return
	 */
	public  synchronized String getFQL(UserView uv,ArrayList alUsedFields,int varType,int infoGroup,String c_expr,String FSQL,String dbpre,int nFlag,String fldtype,int decimalwidth,String nId,Connection conn,String infokind,RGridView rgrid,String planId)
	{
		String sql="";
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String currym=ConstantParamter.getAppdate(uv.getUserName());
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			String strc="1";
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			//创建临时表时，获取用户名去用户名中的“.”和“@” chenxg 2016-08-15
	        String usrName = uv.getUserName();
	        if(usrName.indexOf(".") > -1)
	            usrName = usrName.replace(".", "");
	        
	        if(usrName.indexOf("@") > -1)
	            usrName = usrName.replace("@", "");
	        
			String tablename="t#"+usrName;
			
			if(nFlag==0&&("1".equals(infokind)|| "7".equals(infokind)))//人员/我的薪酬
			{
				/**按人员分库进行批量计算*/
				
					if(dbpre==null||dbpre.length()==0)
						return "";
					/**创建计算用临时表*/
					
					//yp.setTargetFieldDecimal(item.getDecimalwidth()); //why note this .chenmengqing added 20080322
					/**增加一个计算公式用的临时字段*/
					FieldItem fielditem=new FieldItem("A01","card_ykprase");//AAAAA改为BBBBB原因：人员子集会存在AA子集 计算公式有可能会出问题 
					fielditem.setItemdesc("card_ykprase");
					fielditem.setCodesetid("0");
					fielditem.setItemtype(fldtype);
					if("A".equalsIgnoreCase(fldtype))
					   fielditem.setItemlength(200);
					else if("N".equalsIgnoreCase(fldtype))
						fielditem.setItemlength(9);
					fielditem.setDecimalwidth(decimalwidth);
					
					/**追加公式中使用的指标*/
					ArrayList usedlist=initUsedFields(1);
					usedlist.add(fielditem);
					StringBuffer buf=new StringBuffer();
					StringBuffer strFilter=new StringBuffer();
					if(createMidTable(usedlist,tablename,"A0100",conn))
					{
						/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
						buf.setLength(0);
						buf.append("insert into ");
						buf.append(tablename);
						buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
						buf.append(dbpre+"A01");
						buf.append(" where A0100='"+nId+"'");						
						dao.update(buf.toString());
						/**计算临时变量的导入人员范围条件*/
						strFilter.append("select a0100 from "+dbpre+"A01 B where  B.A0100='"+nId+"'");
						YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forSearch,varType,infoGroup,"",dbpre);						
						yp.setStdTmpTable(tablename);
						yp.setTargetFieldDecimal(decimalwidth); 						
						yp.run(c_expr,ymc,"card_ykprase",tablename,dao,strFilter.toString(),conn,fldtype,fielditem.getItemlength(),1,"");
						sql="select card_ykprase from "+tablename+" where a0100='"+nId+"'";
						
					}					
			}else if(nFlag==0&& "5".equals(infokind))
			{
				YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,varType,infoGroup,"","");
				yp.run(c_expr);   
		        FSQL=yp.getSQL();		       
		        StringBuffer buf=new StringBuffer();
		        buf.append("select "+FSQL+" from per_result_"+planId);
		        buf.append(" where");		       
		        buf.append(" object_id = '" + nId + "'");
		        sql=buf.toString();		        
			}else if(nFlag==2||nFlag==4||nFlag==6/*基准岗位*/)
			{
				    if(c_expr==null||c_expr.trim().length()==0)
						return "";
					if(c_expr.indexOf("取自于")!=-1)
					{
						return "";
					}
					ArrayList usedlist=null;
					if(nFlag==4)
					    usedlist=initUsedFields(3);
					else 
						usedlist=initUsedFields(nFlag);
					ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					
					FieldItem fielditem=null;
					if(nFlag==2||nFlag==4){
						fielditem=new FieldItem("K01","E0122");
						fielditem.setItemdesc("所属部门");
						fielditem.setCodesetid("UM");
						fielditem.setItemtype("A");
						fielditem.setItemlength(50);
						fielditem.setDecimalwidth(0);
						allUsedFields.add(fielditem);
					}
					else if(nFlag==6){
						String codeset=new CardConstantSet().getStdPosCodeSetId();
						fielditem=new FieldItem("H01","H0100");
						fielditem.setItemdesc("基准岗位名称");
						fielditem.setCodesetid(codeset);
						fielditem.setItemtype("A");
						fielditem.setItemlength(30);
						fielditem.setDecimalwidth(0);
						allUsedFields.add(fielditem);
					}
					
					String keyName="";
					String mainset="";
					if(nFlag==2)
					{
						infoGroup=YksjParser.forUnit;
						keyName="B0110";
						mainset="B01";
					}
					else if(nFlag==4)
					{
						infoGroup=YksjParser.forPosition;
						keyName="E01A1";
						mainset="K01";
					}	
					else if(nFlag==6)
					{
						infoGroup=YksjParser.forSearch;
						keyName="H0100";
						mainset="H01";
					}
					/**增加一个计算公式用的临时字段*/
					fielditem=new FieldItem("B01","card_ykprase");
					fielditem.setItemdesc("card_ykprase");
					fielditem.setCodesetid("0");
					fielditem.setItemtype(fldtype);
					if("A".equalsIgnoreCase(fldtype))
						   fielditem.setItemlength(200);
					fielditem.setDecimalwidth(decimalwidth);
					usedlist.add(fielditem);					
					/**创建计算用临时表*/					
					StringBuffer buf=new StringBuffer();
					StringBuffer strFilter=new StringBuffer();
					if(createMidTable(usedlist,tablename,keyName,conn))
		 			{ 
						/**导入 主集数据 */
						buf.setLength(0);
						buf.append("insert into ");
						buf.append(tablename);
						buf.append("( "+keyName+") select "+keyName+" FROM ");
						buf.append(mainset);
						/*buf.append(" where "+keyName+" in (select "+keyName+" from ");
						buf.append(tablename);
						buf.append(")");*/
						dao.update(buf.toString());
						/**计算临时变量的导入人员范围条件*/
						strFilter.append(" (select "+keyName+" from ");
						strFilter.append(tablename);
						strFilter.append(" where "+keyName+"='"+nId+"' )");							
						YksjParser yp = new YksjParser(uv, allUsedFields,
								YksjParser.forSearch, varType,infoGroup, "", "");
						yp.setStdTmpTable(tablename);
						yp.setTargetFieldDecimal(decimalwidth); 
						yp.run(c_expr,ymc,"card_ykprase",tablename,dao,strFilter.toString(),conn,fldtype,fielditem.getItemlength(),1,"0");
						sql="select card_ykprase from "+tablename+" where "+keyName+"='"+nId+"'";
					}// 创建临时表结束.
					
			}else
			{
					return "";
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql;
	}
	
	/**
	 * 创建计算用的临时表
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield,Connection conn)
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(conn);
			if(dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	private ArrayList initUsedFields(int infor_type)
	{
		ArrayList fieldlist=new ArrayList();
		
		if(infor_type==1)
		{
			/**人员排序号*/
			FieldItem fielditem=new FieldItem("A01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员编号*/
			fielditem=new FieldItem("A01","A0100");
			fielditem.setItemdesc("a0100");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(8);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**单位名称*/
			fielditem=new FieldItem("A01","B0110");
			fielditem.setItemdesc("单位名称");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**姓名*/
			fielditem=new FieldItem("A01","A0101");
			fielditem.setItemdesc("姓名");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员排序号*/
			fielditem=new FieldItem("A01","I9999");
			fielditem.setItemdesc("I9999");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**部门名称*/
			fielditem=new FieldItem("A01","E0122");
			fielditem.setItemdesc("部门");
			fielditem.setCodesetid("UM");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);		
		}
		else if(infor_type==2)
		{
			/**排序号*/
			FieldItem fielditem=new FieldItem("B01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			
			/**单位名称*/
			fielditem=new FieldItem("B01","B0110");
			fielditem.setItemdesc("单位ID");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
		}
		else if(infor_type==3)
		{
			/**排序号*/
			FieldItem fielditem=new FieldItem("K01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			
			/**岗位名称*/
			fielditem=new FieldItem("K01","E01A1");
			fielditem.setItemdesc("岗位名称");
			fielditem.setCodesetid("@K");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			
		}
		else if(infor_type==6)  // 基准岗位
		{
			String codeset=new CardConstantSet().getStdPosCodeSetId();
			FieldItem fielditem=new FieldItem("H01","H0100");
			fielditem.setItemdesc("基准岗位名称");
			fielditem.setCodesetid(codeset);
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);			
		}
		return fieldlist;
	}
	
	/**
	 * 得到考核期间
	 * @param sql
	 * @return
	 */
	private StringBuffer  getPerCycleStr(StringBuffer sql,String table,String curPlanCond,Connection conn)
	{
		StringBuffer sql2=new StringBuffer();
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(conn);
		String column="";
		try
		{
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				String cycle=rs.getString("cycle")!=null?rs.getString("cycle"):"";
				String thequarter=rs.getString("thequarter")!=null?rs.getString("thequarter"):"";
				//考核周期（AssessCycle）：(0,1,2,3,7)=(年度,半年,季度,月度,不定期)
				 /* PerCycle_Year      = 0; //年度
				  PerCycle_HalfYear  = 1; //半年
				  PerCycle_Quarter   = 2; //季度
				  PerCycle_Month     = 3; //月度
				  PerCycle_Random    = 7; //不定期*/
	            
				if("0".equals(cycle))//年度
				{
					column="theyear"+Sql_switcher.concat()+"'年'";
				}else if("1".equals(cycle))//半年
				{
					
					if(thequarter!=null&&thequarter.length()>0)
					{
						column="theyear"+Sql_switcher.concat()+"'年'";
						if("1".equals(thequarter))
						{					
							column=column+Sql_switcher.concat()+"'上半年'";
						}else if("2".equals(thequarter))
						{
							column=column+Sql_switcher.concat()+"'下半年'";
						}
					}
					
				}else if("2".equals(cycle))//季度
				{
					if(thequarter!=null&&thequarter.length()>0)
					{
						column="theyear"+Sql_switcher.concat()+"'年'";
						String quarter=AdminCode.getCodeName("12", thequarter);
						column=column+Sql_switcher.concat()+"'"+quarter+"'";
					}
				}else if("3".equals(cycle))//月度
				{
					String themonth=rs.getString("themonth")!=null?rs.getString("themonth"):"";
					if(themonth!=null&&themonth.length()>0)
					{
						column="theyear"+Sql_switcher.concat()+"'年'";
						String month=AdminCode.getCodeName("13", themonth);//liuy 2015-6-4 bug9110
						column=column+Sql_switcher.concat()+"'"+month+"'";					
					}
				}else if("7".equals(cycle))//不定期*/
				{
					java.util.Date start_date=rs.getDate("start_date");
					java.util.Date end_date=rs.getDate("end_date");
					if(start_date!=null&&end_date!=null)
					{
						column="'"+DateUtils.format(start_date,"yyyy.MM.dd")+"-"+DateUtils.format(end_date,"yyyy.MM.dd")+"'";
					}
				}
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if(column.length()>0)
		{
			sql2.append("select "+column);
			sql2.append("  FROM " + table+ " where "+curPlanCond);			
			return sql2;
		}
		else
			return sql;
	}
	/**
	 * 删除临时表
	 * 
	 * @param tablename
	 */
	public void dropTable(String tablename,Connection conn) {
		Table table = new Table(tablename);
		DbWizard dbWizard = new DbWizard(conn);
		if (dbWizard.isExistTable(tablename.toLowerCase(), false)) {
			String deleteSQL = "delete from " + tablename + "";
			ArrayList deletelist = new ArrayList();
			ContentDAO dao = new ContentDAO(conn);
			try {
				dao.delete(deleteSQL, deletelist);
				dbWizard.dropTable(table);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 得到定义的计算公式
	 * @param cexpress
	 * @return
	 */
	public String getC_exprForCexpress(String cexpress)
	{
		String lexpr="";
		if(cexpress!=null&&cexpress.indexOf("<EXPR>")!=-1)
		{
			lexpr=cexpress.substring(cexpress.indexOf("<EXPR>")+6,cexpress.indexOf("</EXPR>"));			
		}else
		{
			lexpr=cexpress;
		}
		return lexpr;
	}
	
	public String getISVIEWForCexpress(String cexpress)
	{
		String ISVIEW="";
		if(cexpress!=null&&cexpress.indexOf("<ISVIEW>")!=-1)
		{
			ISVIEW=cexpress.substring(cexpress.indexOf("<ISVIEW>")+8,cexpress.indexOf("</ISVIEW>"));			
		}                                               
		return ISVIEW;
	}
	public void getViewForCexpress(RGridView cell)
	{
		String cexpress=cell.getCexpress();
		if(cexpress!=null&&cexpress.indexOf("<SETNAME>")!=-1)
		{
			String csetname=cexpress.substring(cexpress.indexOf("<SETNAME>")+9,cexpress.indexOf("</SETNAME>"));
			if(csetname!=null&&csetname.length()>0)
			  cell.setCSetName(csetname);			
		}
		if(cexpress!=null&&cexpress.indexOf("<FIELDNAME>")!=-1)
		{
			String fieldname=cexpress.substring(cexpress.indexOf("<FIELDNAME>")+11,cexpress.indexOf("</FIELDNAME>"));
			if(fieldname!=null&&fieldname.length()>0)
			  cell.setField_name(fieldname);
		}
	}
	public String getFenlei_type() {
		return fenlei_type;
	}
	public void setFenlei_type(String fenlei_type) {
		this.fenlei_type = fenlei_type;
	}
	/**
	 * 得到某人的分类权限指标
	 * @param nbase
	 * @param a0100
	 * @param conn
	 * @return
	 */
	public String  getOneFenleiYype(UserView userview,String nbase,String a0100,Connection conn)
	{
		if(userview.isSuper_admin())
		{
			this.fenlei_type= "";
			return "";
		}
		String sql="select * from constant where upper(constant)='SYS_INFO_PRIV'  and type='1'";
    	String value="";
    	String type="";
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(conn);
		try {
			rs=dao.search(sql);			
			if(rs.next())
			{
				value=rs.getString("str_value");
			}
			if(value==null||value.length()<=0)
			{
				this.fenlei_type= "";
				return "";
			}	
			String []arr=value.split(",");
			if(arr==null||arr.length!=2)
			{
				this.fenlei_type= "";
				return "";
			}
							
			String field=arr[0];
			rs=dao.search("select "+field+" as field from "+nbase+"A01 where a0100='"+a0100+"'");
	    	if(rs.next())
	    		type=rs.getString("field");
	    	type=type!=null?type:"";
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		this.fenlei_type=type;	
		ArrayList list=userview.getSubFieldPrivList(type,"1");
		if(list==null||list.size()<=0)
		{
			list=userview.getSubFieldPrivList(type,"2");
			if(list==null||list.size()<=0)
			{
				this.fenlei_type="";
				return "";
			}	
		}
		return this.fenlei_type;
	}
	public String getBizDate() {
		return bizDate;
	}
	public void setBizDate(String bizDate) {
		this.bizDate = bizDate;
	}
	
	/**
	 * 判断视图是否是年月变化
	 * */
	public String viewIsChangeflag(RGridView rgrid,Connection conn){
		String sql="select changeflag from t_hr_busitable where fieldsetid='"+rgrid.getCSetName()+"'";
		ContentDAO dao=new ContentDAO(conn);
		ResultSet rs=null;
		try {
			rs=dao.search(sql);
			String changeflag="";
			while(rs.next()){
				changeflag=rs.getString("changeflag");
			}
			return changeflag;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return "";
	}
	
}
			
		
			

