package com.hjsj.hrms.interfaces.general;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.Calendar;

public class HmusterXML {
	private Connection conn;
	private String xml;
	private String tabid;
	/**
	 * 分栏设置：0:1:12:0:1:0:
	 * 第一项:是否分栏1|0 
	 * 第二项:纵向还是横向,1:横向,2:纵向 
	 * 第三项:栏间距(象素)
	 * 第四项:是否打印分隔线1|0 
	 * 第五项:记录方式(登记表方式、只有一条记录)1|0 
	 * 第六项:不分栏时，最底行是数据区或允许多行数据区1|0
	 */
	public static final int SPILTCOLUMN=1; 
	/**分组指标*/
	public static final int GROUPFIELD = 2;
	/**分组不分页1|0*/
	public static final int MULTIGROUPS = 3;
	/**装订线 (毫米)(浮点数)0*/
	public static final int GUTTER = 4;
	/**装订线位置(1:左;2:上)*/
	public static final int GUTTERPOS = 5;
	/**页脚位置(1:自动调整;2:固定)*/
	public static final int FOOTERPOS = 6;
	/**按分组显示页数和总页数1|0*/
	public static final int DISPLAY_TAGVALUE_BYGROUP = 7;
	/**每个分组打印表头1|0*/
	public static final int MULTIHEADER = 8;
	/**分组间距(象素)*/
	public static final int MULTIHEADERGAP = 9;
	public static final int COLUMN = 10; //是否分栏1|0
	public static final int HZ = 11; //横向|纵向 1|2
	public static final int PIX = 12; //横向|纵向 间隔距离
	public static final int COLUMNLINE = 13; //是否打印分隔线1|0
	public static final int RECORDWAY = 14; //记录方式(登记表方式、只有一条记录)1|0
	public static final int DATAAREA = 15; //不分栏时，最底行是数据区或允许多行数据区1|0
	public static final int GROUPLAYER = 16; //分组层级汇总
	/**A0101:姓名:1`A00Z0:年月:1(排序,1升序,0降序)*/
	public static final int SORTSTR = 17;
	
	/******************************************后加参数。为可以自动重新取数*/
	/**每页行数(0: 自动计算, 1: 用户指定)*/
	public static final int  ROWCOUNTMODE=18;
	/**用户指定行数*/
	public static final int  ROWCOUNT=19;
	/**打印零True|False*/
	public static final int SHOWZERO=20;
	/**打印格线True|False*/
	public static final int  SHOWLINES=21;
	/**<DATACOND>*/
	/**USR,RET(人员库，逗号分隔，空表示全部人员库)*/
	public static final int NBASE=22;
	/**通用查询条件定义，Factor未定义不进行自动重新取数*/
	/**因子A0101<>`*/
	public static final int FACTOR=23;
	/**表达式1*2*/
	public static final int EXPR=24;
	/**模糊查询True|False*/
	public static final int FUZZYFLAG=25;
	/**对历史记录查询True|False*/
	public static final int  HIS=26;
	/**查机构库时，仅查部门True|False*/
	public static final int DEPTONLY=27;
	/**查机构库时，仅查单位True|False*/
	public static final int UNITONLY=28;
	/**取子集记录方式：0当前记录(默认值),1某次历史记录,2根据条件取历史记录*/
	public static final int HISTORYMODE=29;
	/**<YEAR></YEAR><MONTH></MONTH><TIMES></TIMES><!-- 某次历史记录的年月次数 -->*/
	public static final int YEAR=30;
	public static final int MONTH=31;
	public static final int TIMES=32;
	/**<!-- 子集历史记录条件,空表示所有子集记录 -->
	<SUBSETID>子集id</SUBSETID><SUBFACTOR>因子A0405<>,A0405<=21</SUBFACTOR><SUBEXPR>表达式1*2</SUBEXPR>*/
	public static final int SUBSETID=33;
	public static final int SUBFACTOR=34;
	public static final int SUBEXPR=35;
	/**0不汇总,1按人员/单位/职位汇总*/
	public static final int NEEDSUM=36;
	/**是否不按管理范围取数,默认值False,仅人员花名册有效*/
	public static final int NO_MANAGE_PRIV=37;
	public static final int GROUPFIELD2=38;
	public static final int GROUPLAYER2=39;
	public static final int SHOW_PART_JOB=40;
	// 分组指标1/2,B01.B0110或类似指标取指定层级单位还是部门
	public static final int GROUPORGCODESET=41;
	public static final int GROUPORGCODESET2=42;
	public static final int ShowChangedOrgPersons=43;  // 支持显示变化后单位或部门的人员信息
	public static final int ShowChangedOrgNbases=44;
	private String ngrid; //空行打印
	
	/* 标识：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
	/** 常用查询ids */
	public static final int MAINPARAMCOND = 45;
	/** 参数名称 */
	public static final int MAINPARAMTITLE = 46;
	/* 标识：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
	/**按组显示序号*/
	public static final int GROUPEDSERIALS=47;
	
	public String getEnd(int type){
		String path = "";
		switch(type){
    		case SPILTCOLUMN:
    			path = "</SPILTCOLUMN>";
    			break;
    		case GROUPFIELD:
    			path = "</GROUPFIELD>";
    			break;
    		case MULTIGROUPS:
    			path="</MULTIGROUPS>";
    			break;
    		case GUTTER:
    			path="</GUTTER>";
    			break;
    		case GUTTERPOS:
    			path="</GUTTERPOS>";
    			break;
    		case FOOTERPOS:
    			path="</FOOTERPOS>";
    			break;
    		case DISPLAY_TAGVALUE_BYGROUP:
    			path="</DISPLAY_TAGVALUE_BYGROUP>";
    			break;
    		case MULTIHEADER:
    			path="</MULTIHEADER>";
    			break;
    		case MULTIHEADERGAP:
    			path="</MULTIHEADERGAP>";
    			break;
    		case COLUMN:
    			path="</SPILTCOLUMN>";
    			break;
    		case HZ:
    			path="</SPILTCOLUMN>";
    			break;
    		case PIX:
    			path="</SPILTCOLUMN>";
    			break;
    		case COLUMNLINE:
    			path="</SPILTCOLUMN>";
    			break;
    		case RECORDWAY:
    			path="</SPILTCOLUMN>";
    			break;
    		case DATAAREA:
    			path="</SPILTCOLUMN>";
    			break;
    		case GROUPLAYER:
    			path="</GROUPLAYER>";
    			break;
    		case SORTSTR:
    			path="</SORTSTR>";
    			break;
    		case ROWCOUNTMODE:
		    	path= "</ROWCOUNTMODE>";
		    	break;
		    case ROWCOUNT:
		    	path="</ROWCOUNT>";
		    	break;
		    case SHOWZERO:
		    	path="</SHOWZERO>";
		    	break;
		    case SHOWLINES:
		    	path="</SHOWLINES>";
		    	break;
		    case NBASE:
		    	path="</NBASE>";
		    	break;
		    case FACTOR:
		    	path="</FACTOR>";
		    	break;
		    case EXPR:
		    	path="</EXPR>";
		    	break;
		    case FUZZYFLAG:
		    	path="</FUZZYFLAG>";
		    	break;
		    case HIS:
		    	path="</HIS>";
		    	break;
		    case DEPTONLY:
		    	path="</DEPTONLY>";
		    	break;
		    case UNITONLY:
		    	path="</UNITONLY>";
		    	break;
		    case HISTORYMODE:
		    	path="</HISTORYMODE>";
		    	break;
		    case YEAR:
		    	path="</YEAR>";
		    	break;
		    case MONTH:
		    	path="</MONTH>";
		    	break;
		    case TIMES:
		    	path="</TIMES>";
		    	break;
		    case SUBSETID:
		    	path="</SUBSETID>";
		    	break;
		    case SUBFACTOR:
		    	path="</SUBFACTOR>";
		    	break;
		    case SUBEXPR:
		    	path="</SUBEXPR>";
		    	break;
		    case NEEDSUM:
		    	path="</NEEDSUM>";
		    	break;
		    case NO_MANAGE_PRIV:
		    	path="</NO_MANAGE_PRIV>";
		    	break;
		    case GROUPFIELD2:
		    	path="</GROUPFIELD2>";
		    	break;
		    case GROUPLAYER2:
		    	path="</GROUPLAYER2>";
		    	break;
		    case SHOW_PART_JOB:
		    	path="</SHOW_PART_JOB>";
		    	break;
		    case GROUPORGCODESET:
		    	path="</GROUPORGCODESET>";
		    	break;
		    case GROUPORGCODESET2:
		    	path="</GROUPORGCODESET2>";
		    	break;
		    case ShowChangedOrgPersons:
		        path="</SHOWPERSONS>";
		        break;
		    case ShowChangedOrgNbases:
		        path="</SHOWPERSONS_NBASE>";
		        break;
		    /* 标识：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
		    case MAINPARAMCOND:
		    	path = "</MAINPARAMCOND>";
		    	break;
		    case MAINPARAMTITLE:
		    	path = "</MAINPARAMTITLE>";
		    	break;
		    /* 标识：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
		    case GROUPEDSERIALS:
		    	path="</GROUPEDSERIALS>";
		    	break;
		}
		return path;
		   
			
	}
	public String getStar(int type){
		String name = "";
		switch(type){
    		case SPILTCOLUMN:
    			name = "<SPILTCOLUMN>";
    			break;
    		case GROUPFIELD:
    			name = "<GROUPFIELD>";
    			break;
    		case MULTIGROUPS:
    			name="<MULTIGROUPS>";
    			break;
    		case GUTTER:
    			name="<GUTTER>";
    			break;
    		case GUTTERPOS:
    			name="<GUTTERPOS>";
    			break;
    		case FOOTERPOS:
    			name="<FOOTERPOS>";
    			break;
    		case DISPLAY_TAGVALUE_BYGROUP:
    			name="<DISPLAY_TAGVALUE_BYGROUP>";
    			break;
    		case MULTIHEADER:
    			name="<MULTIHEADER>";
    			break;
    		case MULTIHEADERGAP:
    			name="<MULTIHEADERGAP>";
    			break;
    		case COLUMN:
    			name="<SPILTCOLUMN>";
    			break;
    		case HZ:
    			name="<SPILTCOLUMN>";
    			break;
    		case PIX:
    			name="<SPILTCOLUMN>";
    			break;
    		case COLUMNLINE:
    			name="<SPILTCOLUMN>";
    			break;
    		case RECORDWAY:
    			name="<SPILTCOLUMN>";
    			break;
    		case DATAAREA:
    			name="<SPILTCOLUMN>";
    			break;
    		case GROUPLAYER:
    			name="<GROUPLAYER>";
    			break;
    		case SORTSTR:
    			name="<SORTSTR>";
    			break;
    		case ROWCOUNTMODE:
		    	name= "<ROWCOUNTMODE>";
		    	break;
		    case ROWCOUNT:
		    	name="<ROWCOUNT>";
		    	break;
		    case SHOWZERO:
		    	name="<SHOWZERO>";
		    	break;
		    case SHOWLINES:
		    	name="<SHOWLINES>";
		    	break;
		    case NBASE:
		    	name="<NBASE>";
		    	break;
		    case FACTOR:
		    	name="<FACTOR>";
		    	break;
		    case EXPR:
		    	name="<EXPR>";
		    	break;
		    case FUZZYFLAG:
		    	name="<FUZZYFLAG>";
		    	break;
		    case HIS:
		    	name="<HIS>";
		    	break;
		    case DEPTONLY:
		    	name="<DEPTONLY>";
		    	break;
		    case UNITONLY:
		    	name="<UNITONLY>";
		    	break;
		    case HISTORYMODE:
		    	name="<HISTORYMODE>";
		    	break;
		    case YEAR:
		    	name="<YEAR>";
		    	break;
		    case MONTH:
		    	name="<MONTH>";
		    	break;
		    case TIMES:
		    	name="<TIMES>";
		    	break;
		    case SUBSETID:
		    	name="<SUBSETID>";
		    	break;
		    case SUBFACTOR:
		    	name="<SUBFACTOR>";
		    	break;
		    case SUBEXPR:
		    	name="<SUBEXPR>";
		    	break;
		    case NEEDSUM:
		    	name="<NEEDSUM>";
		    	break;
		    case NO_MANAGE_PRIV:
		    	name="<NO_MANAGE_PRIV>";
		    	break;
		    case GROUPFIELD2:
		    	name="<GROUPFIELD2>";
		    	break;
		    case GROUPLAYER2:
		    	name="<GROUPLAYER2>";
		    	break;
		    case SHOW_PART_JOB:
		    	name="<SHOW_PART_JOB>";
		    	break;
		    case GROUPORGCODESET:
		    	name="<GROUPORGCODESET>";
		    	break;
		    case GROUPORGCODESET2:
		    	name="<GROUPORGCODESET2>";
		    	break;
            case ShowChangedOrgPersons:
                name="<SHOWPERSONS>";
                break;
            case ShowChangedOrgNbases:
                name="<SHOWPERSONS_NBASE>";
                break;
            /* 标识：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 start */
		    case MAINPARAMCOND:
		    	name = "<MAINPARAMCOND>";
		    	break;
		    case MAINPARAMTITLE:
		    	name = "<MAINPARAMTITLE>";
		    	break;
		    /* 标识：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14 end */
		    case GROUPEDSERIALS:
		    	name="<GROUPEDSERIALS>";
		    	break;
		}
		return name;
			
	}
	public String isNullValue(int type){
		String value = "";
		switch(type){
    		case SPILTCOLUMN:
    			value = "0:1:5:1:1:0";
    			break;
    		case GROUPFIELD:
    			value = "";
    			break;
    		case GROUPFIELD2:
    			value="";
    			break;
    		case MULTIGROUPS:
    			value="0";
    			break;
    		case GUTTER:
    			value="0";
    			break;
    		case GUTTERPOS:
    			value="1";
    			break;
    		case FOOTERPOS:
    			value="2";
    			break;
    		case DISPLAY_TAGVALUE_BYGROUP:
    			value="0";
    			break;
    		case MULTIHEADER:
    			value="0";
    			break;
    		case MULTIHEADERGAP:
    			value="0";
    			break;
    		case COLUMN:
    			value="0:1:5:1:1:0";
    			break;
    		case HZ:
    			value="0:1:5:1:1:0";
    			break;
    		case PIX:
    			value="0:1:5:1:1:0";
    			break;
    		case COLUMNLINE:
    			value="0:1:5:1:1:0";
    			break;
    		case RECORDWAY:
    			value="0:1:5:1:1:0";
    			break;
    		case DATAAREA:
    			value="0:1:5:1:1:0";
    			break;
    		case GROUPLAYER:
    			value="";
    			break;
    		case GROUPLAYER2:
    			value="";
    			break;
    		case SORTSTR:
    			value="";
    			break;
    		case ROWCOUNTMODE:
		    	value= "0"/*1*/;// 默认自动计算
		    	break;
		    case ROWCOUNT:
		    	value="20";
		    	break;
		    case SHOWZERO:
		    	value="False";
		    	break;
		    case SHOWLINES:
		    	value="False";
		    	break;
		    case NBASE:
		    	value="";
		    	break;
		    case FACTOR:
		    	value="";
		    	break;
		    case EXPR:
		    	value="";
		    	break;
		    case FUZZYFLAG:
		    	value="False";
		    	break;
		    case HIS:
		    	value="False";
		    	break;
		    case DEPTONLY:
		    	value="False";
		    	break;
		    case UNITONLY:
		    	value="False";
		    	break;
		    case HISTORYMODE:
		    	value="0";
		    	break;
		    case YEAR:
		    	Calendar cd=Calendar.getInstance();
		    	value=cd.get(Calendar.YEAR)+"";
		    	break;
		    case MONTH:
		    	Calendar c=Calendar.getInstance();
		    	value=c.get(Calendar.MONTH)+1+"";
		    	break;
		    case TIMES:
		    	value="1";
		    	break;
		    case SUBSETID:
		    	value="";
		    	break;
		    case SUBFACTOR:
		    	value="";
		    	break;
		    case SUBEXPR:
		    	value="";
		    	break;
		    case NEEDSUM:
		    	value="0";
		    	break;
		    case NO_MANAGE_PRIV:
		    	value="false";
		    	break;
		    case SHOW_PART_JOB:
		    	value="false";
		    	break;
		}
		return value;
			
	}
	public HmusterXML(Connection conn,String tabid){
		this.conn=conn;
		this.tabid=tabid;
		init();
	}
	private void init(){
		StringBuffer temp_xml=new StringBuffer("");	
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select cFactor,nGrid from muster_name where Tabid="+tabid);
			if(rowSet.next()){
				xml=rowSet.getString("cFactor");
				ngrid=rowSet.getString("nGrid");
				ngrid=ngrid!=null&&ngrid.trim().length()>0?ngrid:"0";
			}
			if(xml==null|| "".equals(xml)){
				xml=temp_xml.toString(); 
			}
		}catch(Exception ex){
			xml=temp_xml.toString();
		}
	}
	public String getValue(int type){
		  String str="";
		try{
			String star=getStar(type);
			String end=getEnd(type);
			
			if(xml.indexOf(star)!=-1&&xml.indexOf(end)!=-1){
				str=xml.substring(xml.indexOf(star)+star.length(),xml.indexOf(end));
			}
			if(str!=null&&str.trim().length()>0){
				if(type<17)
					str = getValue(type,str);
			}else{
				str=isNullValue(type);
				if(type<17)
		     		str = getValue(type,str);
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 是否设置了参数
	 * @param type
	 * @return
	 */
	public boolean hasParam(int type){
		String str="";
		try{
			String star=getStar(type);
			String end=getEnd(type);
			
			if(xml.indexOf(star)!=-1&&xml.indexOf(end)!=-1){
				str=xml.substring(xml.indexOf(star)+star.length(),xml.indexOf(end));
			}
			if(str!=null&&str.trim().length()>0){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public String getValue(int type,String str){
		String strvalue="";
		String[] arr = str.split(":"); 
		if(arr.length<4)
			return str;
		switch(type){
			case COLUMN:
				if(arr.length>0)
					strvalue=arr[0];
				break;
			case HZ:
				if(arr.length>1)
					strvalue=arr[1];
				break;
			case PIX:
				if(arr.length>2)
					strvalue=arr[2];
				break;
			case COLUMNLINE:
				if(arr.length>3)
					strvalue=arr[3];
				break;
			case RECORDWAY:
				if(arr.length>4)
					strvalue=arr[4];
				break;
			case DATAAREA:
				if(arr.length>5)
					strvalue=arr[5];
				break;
		}
		return strvalue;
	}
	public void setValue(int type,String strValue){
		try{
			String star=getStar(type);
			String end=getEnd(type);

			String values = "";
			if(xml.indexOf(star)!=-1&&xml.indexOf(end)!=-1){
				values=xml.substring(xml.indexOf(star)+star.length(),xml.indexOf(end));
			}else{
				values = isNullValue(type);
			}
			String str = ""; 
			if(type!=SORTSTR){
				String[] arr = values.split(":");
				switch(type){
				case COLUMN:
					if(arr.length<1){
						values = isNullValue(type);
						arr = values.split(":");
					}
					arr[0]=strValue;
					break;
				case HZ:
					if(arr.length<2){
						values = isNullValue(type);
						arr = values.split(":");
					}
					arr[1]=strValue;
					break;
				case PIX:
					if(arr.length<3){
						values = isNullValue(type);
						arr = values.split(":");
					}
					arr[2]=strValue;
					break;
				case COLUMNLINE:
					if(arr.length<4){
						values = isNullValue(type);
						arr = values.split(":");
					}
					arr[3]=strValue;
					break;
				case RECORDWAY:
					if(arr.length<5){
						values = isNullValue(type);
						arr = values.split(":");
					}
					arr[4]=strValue;
					break;
				case DATAAREA:
					if(arr.length<6){
						values = isNullValue(type);
						arr = values.split(":");
					}
					arr[5]=strValue;
					break;
				}

				if(arr.length>4){
					for(int i=0;i<arr.length;i++){
						if(arr[i]!=null&&arr[i].trim().length()>0){
							str += arr[i];
							if(i+1<arr.length)
								str+=":";
						}
					}
				}else{
					str=strValue;
				}
			}else{
				str=strValue;
			}
			strValue=star+str+end;
			if(xml.indexOf(star)!=-1&&xml.indexOf(end)!=-1){
				String oldstr = star+xml.substring(xml.indexOf(star)+star.length(),xml.indexOf(end))+end;
				xml=xml.replace(oldstr,strValue);	
			}else{
				xml+=strValue;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void saveValue(){
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update("update muster_name set cFactor='"+xml+"',nGrid='"+ngrid+"' where tabid="+tabid); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getNgrid() {
		return ngrid;
	}
	public void setNgrid(String ngrid) {
		this.ngrid = ngrid;
	}
}
