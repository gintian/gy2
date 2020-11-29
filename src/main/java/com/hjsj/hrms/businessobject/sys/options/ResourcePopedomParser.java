package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.IResourceConstant;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.List;
import java.util.StringTokenizer;

public class ResourcePopedomParser implements IResourceConstant {
	
	private String resourceStr;
	private int res_type;
	private Document doc;
	
	public ResourcePopedomParser(String resourceStr ,int res_type){
		this.resourceStr = resourceStr;
		this.res_type = res_type;
		this.init();
	}
	
	/**
	 * 初始化
	 */
	private void init()
	{
		StringBuffer strxml=new StringBuffer();
		try
		{
	        strxml.append("<?xml version='1.0' encoding='UTF-8'?>");
	        strxml.append("<resource>");
	        strxml.append(resourceStr);
	        strxml.append("</resource>");
			doc= PubFunc.generateDom(strxml.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 取得对应资源节点的名称
	 * @return
	 */
	private String getElementName(int res_type)
	{
		String name=null;
		switch(res_type){
			case CARD://登记表0
				name="Card";
				break;
			case REPORT: //统计表1
				name="Report";
				break;
			case HIGHMUSTER://高级花名册5
				name="HighMuster";
				break;
			case MUSTER: //常用花名册4
				name="Muster";
				break;
			case LEXPR://常用查询2
				name="Lexpr";
				break;
			case STATICS: //常用统计3
				name="Statis";
				break;
			case LAWRULE:  //规章制度6
				name="Lawrule";				
				break;	
			case RSBD:  //人事异动7
				name="rsbd";				
				break;		
			case GZBD://8工资变动
				name="gzbd";
			    break;	
			case INVEST://9;问卷调查表
				name="invest";
				break;	
			case TRAINJOB://10;培训班
				name="trainjob";
				break;	
			case ANNOUNCE://11;公告栏
				name="announce";
				break;	
			case GZ_SET://12;薪资类别
				name="gz_set";
				break;	
			case LAWRULE_FILE://13;规章制度文章或知识文章	
				name="lawrule_file";
				break;	
			case ARCH_TYPE://14;档案分类	
				name="archtype";
				break;	
			case KQ_MACH://15;考勤机
				name="kq_mach";
				break;	
			case MEDIA_EMP://16;人员多媒体分类授权
				name="media_emp";
				break;	
			case INS_BD://17;保险福利变动
				name="ins_bd";
				break;	
			case INS_SET://18;保险福利类别
				name="ins_set";
				break;	
			case DOCTYPE://19;文档分类
				name="doc_type";
				break;	
			case GZ_CHART://20;工资分析图表	
				name="gz_chart";
				break;	
			case KNOWTYPE://21;知识分类
				name="knowtype";
				break;	
			case KH_MODULE://22;考核模板
				name="kh_m";
				break;	
			case KH_FIELD://23考核指标
				name="kh_f";
				break;
			case PARTY:
				name="party";
				break;
			case MEMBER:
				name="member";
				break;
			case ORG_BD:
				name="org_bd";
				break;
			case POS_BD:
				name="pos_bd";
				break;
			case KQ_CLASS_GROUP:
				name="kq_class";
				break;
			case KQ_BASE_CLASS:
				name="kq_base_class";
				break;

		}
		return name;
	}
	
	/**
	 * 取得对应资源类型的值 ,2,3,3,4,
	 * @return
	 */	
	   public String getContent()	
	   {
			String name=getElementName(this.res_type);
			String xpath="/resource/"+name;
			String result="";
			StringBuffer str_value=new StringBuffer();
			try
			{
				XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=reportPath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					result= "";
				}
				else
				{
					element=(Element)childlist.get(0);
					result= element.getText();
				}
				StringTokenizer token=new StringTokenizer(result,",");
				while(token.hasMoreTokens())
				{
					String value=token.nextToken();
					if(value!=null&&value.toUpperCase().indexOf("R")!=-1) {
                        value=value.substring(0,value.length()-1);
                    }
					str_value.append(value);
					str_value.append(",");
				}
				if(str_value.length()>0) {
                    str_value.setLength(str_value.length()-1);
                }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		    return str_value.toString();
	   }

	   
	   /**
	    * @param res_type 资源类型
	    * @return
	    */
	   public String getContent(int res_type)	
	   {
		   this.res_type=res_type;
		   return getContent();
	   }
}
