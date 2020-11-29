package com.hjsj.hrms.taglib.kq;

import com.hrms.struts.constant.SystemConfig;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.HashMap;

public class KqValueChangeTag extends BodyTagSupport{
	private String value;
	private HashMap kqItem_hash=new HashMap();
//	KqValueChangeBo kq = new KqValueChangeBo();
	private String itemid;
	private final String unit_HOUR=  "01";  //小时
	private final String unit_DAY    ="02";  //天
	private final String unit_ONCE  ="04";  //次
	private final String unit_MINUTE  ="03"; //分钟
	public int doEndTag() throws JspException 
	{
//			this.kqItem_hash=kq.count_Leave();
			if(value==null||value.length()<=0)
				return SKIP_BODY;	
			if(kqItem_hash==null)
				return SKIP_BODY;	
//			System.out.println(value);
			String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
			if(!"".equals(sdao_count_field)||sdao_count_field.length()>0)
			{
				if(sdao_count_field.equalsIgnoreCase(itemid.toUpperCase()))
				{
					try {
						pageContext.getOut().println(value);
						return SKIP_BODY;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			HashMap item_Map=(HashMap)this.kqItem_hash.get(itemid.toUpperCase()); 
			String itemUnit="";
			if(item_Map==null)
				itemUnit=unit_HOUR;
			else
			   itemUnit=(String)item_Map.get("item_unit");
			if(itemUnit==null||itemUnit.length()<=0)
	    	{
	    		 itemUnit=unit_HOUR;
	    	}
			try {
				// 郑文龙 修改 不是时间类型进行处理
				if(value.indexOf('.') == -1){
					if(!"0".endsWith(value)){
						pageContext.getOut().println(value);
					}
				}else if(itemUnit.equals(unit_HOUR))
				{
//					String[] on1 = value.split(".");
//					String xiaoshu = on1[1].substring( 0 , 2);
//					System.out.println("小数点 = "+xiaoshu);
					String on = value.substring(value.length()-2,value.length());
					if(on.indexOf(".")!=-1){//如果数据为0.0格式的话
					    value = value + "0";
					    on = value.substring(value.length()-2,value.length());
					}
					if("0.00".equalsIgnoreCase(value)){
						pageContext.getOut().println();
					}else if("00".equalsIgnoreCase(on)){
						String in= value;
						in=in.replace(".", ":");
						pageContext.getOut().println(in);
					}else if(!"00".equalsIgnoreCase(on)){
						String g3 = value.substring(0, value.length()-2); //前两位
						String ys = "0."+on;
						double a1=Double.parseDouble(ys);
						double g = a1*60;
						 g   = Math.round(g);  //四舍五入 
						 String g1 = String.valueOf(g);
						 g1 = g1.substring(0,g1.length()-2);
						 g3=g3+g1;
						 g3=g3.replace(".", ":");
						 pageContext.getOut().println(g3);
						
						
					}
					
				}else if(itemUnit.equals(unit_MINUTE))  //分钟
				{
//					int xs = Integer.parseInt(value);
					if("0.00".equalsIgnoreCase(value))
					{
						pageContext.getOut().println();
					}else{
						double fd=Double.parseDouble(value);
						fd = fd/60;
						String va = String.valueOf(fd);
						/**先算小数点后面为1位，两种情况 **/
						String[] sd = va.split("\\.");
						String ss="";
						String zhsw="";
						for(int i =0;i<sd.length;i++)
						{
							if(i==1)
							{
								ss = sd[i];
								
							}else
							{
								zhsw = sd[i];
							}
						}
						if(ss.length()==1)
						{
							if("0".equals(ss))
							{
								String fhvale=zhsw+":"+ss+"0";
								pageContext.getOut().println(fhvale);
							}else
							{
								String ys = "0."+ss;
								double a1=Double.parseDouble(ys);
								double g = a1*60;
								g   = Math.round(g);  //四舍五入 
								 String g1 = String.valueOf(g);
								 g1 = g1.substring(0,g1.length()-2);
								 zhsw=zhsw+":"+g1;
								 pageContext.getOut().println(zhsw);
							}
						}else
						{
							String jz = ss.substring(0, 2);
							if("00".equals(jz))
							{
								String fhvale=zhsw+":"+jz;
								pageContext.getOut().println(fhvale);
							}else
							{
								String ys = "0."+jz;
								double a1=Double.parseDouble(ys);
								double g = a1*60;
								g   = Math.round(g);  //四舍五入 
								 String g1 = String.valueOf(g);
								 g1 = g1.substring(0,g1.length()-2);
								 zhsw=zhsw+":"+g1;
								 pageContext.getOut().println(zhsw);
							}
						}
					}
				}
//				else if(itemUnit.equals(unit_DAY))  //天
//				{
//					if(value.equalsIgnoreCase("0.00"))
//					{
//						pageContext.getOut().println();
//					}else
//					{
//						double fd=Double.parseDouble(value);
//						fd = fd*8;
//						String va = String.valueOf(fd);
//						/**天后面为1位，两种情况 **/
//						String[] sd = va.split("\\.");
//						String ss1="";
//						String zhsw1="";
//						for(int i =0;i<sd.length;i++)
//						{
//							if(i==1)
//							{
//								ss1 = sd[i];
//								
//							}else
//							{
//								zhsw1 = sd[i];
//							}
//						}
//						if(ss1.length()==1)
//						{
//							if(ss1.equals("0"))
//							{
//								String fhvale=zhsw1+":"+ss1+"0";
//								pageContext.getOut().println(fhvale);
//							}else
//							{
//								String ys = "0."+ss1;
//								double a1=Double.parseDouble(ys);
//								double g = a1*60;
//								g   = Math.round(g);  //四舍五入 
//								 String g1 = String.valueOf(g);
//								 g1 = g1.substring(0,g1.length()-2);
//								 zhsw1=zhsw1+":"+g1;
//								 pageContext.getOut().println(zhsw1);
//							}
//						}else
//						{
//							String jz = ss1.substring(0, 2);
//							if(jz.equals("00"))
//							{
//								String fhvale=zhsw1+":"+jz;
//								pageContext.getOut().println(fhvale);
//							}else
//							{
//								String ys = "0."+jz;
//								double a1=Double.parseDouble(ys);
//								double g = a1*60;
//								g   = Math.round(g);  //四舍五入 
//								 String g1 = String.valueOf(g);
//								 g1 = g1.substring(0,g1.length()-2);
//								 zhsw1=zhsw1+":"+g1;
//								 pageContext.getOut().println(zhsw1);
//							}
//						}
//					}
//				}
				else if("0.00".equalsIgnoreCase(value))
					{
						pageContext.getOut().println();
					}else{
						pageContext.getOut().println(value);
					}
					
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return SKIP_BODY;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public HashMap getKqItem_hash() {
		return kqItem_hash;
	}
	public void setKqItem_hash(HashMap kqItem_hash) {
		this.kqItem_hash = kqItem_hash;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

}