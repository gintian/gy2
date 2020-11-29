package com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterPdf;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DeptPersonBo {
	private Connection con;
	private UserView userView=null;
	private String nbase = "Usr";
	private String a0100 = "";
	private String unitSet;
	private String planField;
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	private String[] color= {"#e4ffe9","#d1feda"," #bbffc9","#a1ffb4","#8efca5","#7fff99","#6eff8b","#59ff7b","#4ce96c","#44d762","#3ec75a"};
	public DeptPersonBo()
	{
		
	}
	public DeptPersonBo(UserView userView,Connection con)
	{
		this.userView=userView;
		this.con=con;
	}
	public void initBZParameters(){
		PosparameXML pos = new PosparameXML(this.con);
		String ps_set1 = pos.getValue(PosparameXML.AMOUNTS,"setid");
		this.unitSet = ps_set1;
		ArrayList clist = pos.getChildList(PosparameXML.AMOUNTS,ps_set1);
		if(clist.size()>0){
			String planitem = pos.getChildValue(PosparameXML.AMOUNTS,this.unitSet,clist.get(0).toString(),"planitem");
			this.planField=planitem;
		}
	}
	public DeptPersonBo(Connection con,UserView userView,String nbase,String a0100) throws GeneralException, SQLException
	{
		this.con = con;
		this.userView = userView;
		this.nbase = nbase;
		this.a0100 = a0100;
	}
	public ArrayList queryPersonDetails(String fields){
		    ArrayList list = new ArrayList();
			RowSet rowSet = null;
			try{
				ContentDAO dao = new ContentDAO(this.con);
				/*String sql = "select * from "+nbase+"a01  where a0100="+a0100+"";
				rowSet = dao.search(sql);
				if(rowSet.next())
		        {
					list.add("姓名:"+rowSet.getString("a0101"));
		        }*/
				String sql="";
				if(fields==null|| "".equals(fields))
					fields="b0110,e0122,e01a1,a0101";
				String []fieldsArray = fields.split(",");
				for (int i = 0; i < fieldsArray.length; i++) {
					String [] field = new String[2];
					field[0] = fieldsArray[i].split("`")[0];
					field[1] = fieldsArray[i].split("`")[1];
					if("a01".equalsIgnoreCase(field[0]))
						sql = "select "+field[1]+" from "+nbase+field[0]+" where a0100="+a0100 ;
					else		
					    sql = "select "+field[1]+" from "+nbase+field[0]+" where a0100="+a0100+" and i9999=(select max(i9999) from "+nbase+field[0]+" where a0100="+a0100+")";
					rowSet = dao.search(sql);
					if(rowSet.next()){
						FieldItem fielditem = DataDictionary.getFieldItem(field[1].toLowerCase());
						String fielditemValue = "";
						if("N".equalsIgnoreCase(fielditem.getItemtype())){
							fielditemValue = rowSet.getString(field[1])==null?"":rowSet.getString(field[1]);
							fielditemValue=PubFunc.round(fielditemValue,fielditem.getDecimalwidth());
						}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
							if("0".equals(fielditem.getCodesetid())){
								fielditemValue = rowSet.getString(field[1])==null?"":rowSet.getString(field[1]);
							}else{
								fielditemValue = rowSet.getString(field[1])==null?"":rowSet.getString(field[1]);
								fielditemValue = AdminCode.getCodeName(fielditem.getCodesetid(),fielditemValue);
							}
						}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
							    Date date = rowSet.getDate(field[1]);
							    fielditemValue = sdf.format(date);
						}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
							   fielditemValue = Sql_switcher.readMemo(rowSet, field[1])==null?"":Sql_switcher.readMemo(rowSet, field[1]);
						}else{
							fielditemValue = rowSet.getString(field[1])==null?"":rowSet.getString(field[1]);
						}
						list.add(((FieldItem)DataDictionary.getFieldItem(field[1].toLowerCase())).getItemdesc()+":"+fielditemValue);
				    }
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rowSet!=null)
						rowSet.close();
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		    return  list;
		}
	/**
	 * 获得 汇报关系中 直接上级指标
	 * @return
	 */
	public String getPS_SUPERIOR_value()
	{
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
        if(vo==null)
        	return fieldItem;
        String param=vo.getString("str_value");
        if(param==null|| "".equals(param)|| "#".equals(param))
        	return fieldItem;
		fieldItem=param;
		return fieldItem;
	}
	private String parentPosField="";//直接上级岗位指标
	private String EORROR_STR="";
	public String getError_str(){
		return this.EORROR_STR;
	}
	public static int getPositionType(String positionType){
		int ret = -1;
		try{
			if(positionType==null|| "".equals(positionType)){
				ret=5;
			}else if("01".equalsIgnoreCase(positionType)){
				ret=1;
			}else if("02".equalsIgnoreCase(positionType)){
				ret=2;
			}else if("03".equalsIgnoreCase(positionType)){
				ret=3;
			}else if("04".equalsIgnoreCase(positionType)){
				ret=4;
			}else if("05".equalsIgnoreCase(positionType)|| "06".equalsIgnoreCase(positionType)){
				ret=5;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	public int getSelfPositionType(){
		int positionType=0;
		RowSet rSet = null;
		try{
			ContentDAO dao = new ContentDAO(con);
			rSet = dao.search(" select e01a1,c01sc from usra01 where a0100='"+this.userView.getA0100()+"' ");
			while(rSet.next()){
				String c01sc=rSet.getString("c01sc");
				positionType=DeptPersonBo.getPositionType(c01sc);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(rSet!=null)
					rSet.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return positionType;
	}
	private String formFlag="";
	/**
	 * 
	 * @param opt =1部门人员，=2处室人员
	 * @return
	 */
	public String  getDeptPersonsHtmlStr(String fromFlag,String e0122){ 
		StringBuffer htmlStr=new StringBuffer();
		RowSet rs = null;
		try{
			this.formFlag=fromFlag;
			this.parentPosField=this.getPS_SUPERIOR_value();
			if(this.parentPosField==null|| "".equals(this.parentPosField.trim()))
				this.EORROR_STR="系统未设置岗位直接上级指标！";
			if("".equals(this.EORROR_STR)){
				
				 String clientName=SystemConfig.getPropertyValue("clientName");
				 if("gw".equalsIgnoreCase(clientName)&& "1".equals(fromFlag)){//国网的要特殊处理，处室人员进入部门人员，找其上级部门展现
				    String temp=e0122;
				    e0122=this.getParentString(e0122);
				    if(e0122==null|| "".equals(e0122))
				    	e0122=temp;
				}
				HmusterPdf hmusterPdf = new HmusterPdf(this.con);
				htmlStr.append("<table class=\"epm-table-one\"><tr><td align=\"left\">");
				String str=new String(SystemConfig.getPropertyValue("masterName"));
				htmlStr.append("<h2 calss=\"epm-table-two\">"+str+AdminCode.getCodeName("UM", e0122)+"</h2>");
				htmlStr.append("</td></tr>");
				htmlStr.append("<tr><td align=\"center\" class=\"epm-table-three\">");
				String fields=SystemConfig.getPropertyValue("personvisiblefield");
				if(fields==null|| "".equals(fields))
					fields="A01`b0110,A01`e0122,A01`e01a1,A01`a0101";
				String[] arr=fields.split(",");
				/*if(fromFlag.equals("1")){*/
				    this.initBZParameters();
					this.getPosList(e0122);
					this.anylsePosData();
					this.getPersons(e0122,0);
					int colorIndex=0;
					HashMap colorMap= new HashMap();
					FieldItem item = DataDictionary.getFieldItem("e01a1");
					if("gw".equalsIgnoreCase(clientName)){//国网的要特殊处理，
						int positionType=this.getSelfPositionType();
						String selfE01a1 = this.userView.getUserPosId();
						for(int i=1;i<=this.maxLay;i++){
							ArrayList positionList = (ArrayList)this.layMap.get(i+"");
							htmlStr.append("<div class=\"epm-gl-top\">");
				            htmlStr.append("<table border=\"1\" align=\"center\">");
				            htmlStr.append("<tr>");
							for(int j=0;j<positionList.size();j++){
								LazyDynaBean bean = (LazyDynaBean)positionList.get(j);
								String e01a1 =(String)bean.get("e01a1");
								if(colorMap.get(e01a1.toUpperCase())!=null){
									
								}else{
									if(colorIndex>=this.color.length){
										colorIndex=0;
									}
									colorMap.put(e01a1.toUpperCase(), colorIndex+"");
									colorIndex++;
								}
								ArrayList personList = (ArrayList)this.personsMap.get(e01a1.toUpperCase());
								if(personList!=null){
									String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
									for(int k=0;k<personList.size();k++){
										LazyDynaBean abean =(LazyDynaBean)personList.get(k);
										String a0100=(String)abean.get("a0100");
										String nbase=(String)abean.get("nbase");
										String tempName = hmusterPdf.createPhotoFile(nbase+"A00",a0100, "P");
										int ptype=Integer.parseInt((String)abean.get("_c01sc"));
										String src="/images/photo.jpg";
										if(tempName.length()>0){
											src="/servlet/DisplayOleContent?filename="+ tempName;
										}
										htmlStr.append("<td align=\"center\">"); 
										if("1".equals(fromFlag))
							               htmlStr.append("<div style=\"background:"+color+"\" class=\"epm-gl-chuz\">");
										else
										   htmlStr.append("<div  class=\"epm-gl-chuz\">");
							            htmlStr.append("<dl>");
							            htmlStr.append("<dt>");
							            if(positionType < ptype||selfE01a1.equalsIgnoreCase(e01a1)){
								            htmlStr.append("<a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase+"\">");

							            }else{
							            	
							            }
							            htmlStr.append("<img src='"+src+"' />");
							            if(positionType < ptype||selfE01a1.equalsIgnoreCase(e01a1))
							            {
							            	htmlStr.append("</a>");
							            }else{
							            	
							            }
							            htmlStr.append("</dt>");
							            for(int l=0;l<arr.length; l++){
											String itemid=arr[l].split("`")[1];
											htmlStr.append("<dd>"+(String)abean.get(itemid)+"</dd>");
							             }
							             htmlStr.append("</dl>");
							             htmlStr.append("</div>");
							             htmlStr.append("</td>");
									}
							    }else{//该岗位下没人，显示缺编
							    	
							    	String color = "";
									if(colorMap.get(e01a1.toUpperCase())!=null){
										color=this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
									}else{
										if(colorIndex>=this.color.length){
											colorIndex=0;
										}
										color=this.color[colorIndex];
										colorMap.put(e01a1.toUpperCase(), colorIndex+"");
										colorIndex++;
									}
							    	htmlStr.append("<td align=\"center\">"); 
									if("1".equals(fromFlag))
						               htmlStr.append("<div style=\"background:"+color+"\" class=\"epm-gl-chuz\">");
									else
									   htmlStr.append("<div  class=\"epm-gl-chuz\">");
						            htmlStr.append("<dl>");
						            htmlStr.append("<dt>");
						            htmlStr.append("<img src='/images/epm_quebian.jpg' />");
						            htmlStr.append("</dt>");
						            if(item!=null){
						            	htmlStr.append("<dd>"+item.getItemdesc()+":"+AdminCode.getCodeName("@K", e01a1)+"</dd>");
						            }else{
						            	htmlStr.append("<dd>"+AdminCode.getCodeName("@K", e01a1)+"</dd>");
						            }
						            for(int l=1;l<arr.length; l++){
										String itemid=arr[l].split("`")[1];
										htmlStr.append("<dd>&nbsp;</dd>");
						             }
						             htmlStr.append("</dl>");
						             htmlStr.append("</div>");
						             htmlStr.append("</td>");
							    }
							}
							htmlStr.append("</tr>");
							htmlStr.append("</table>");
							htmlStr.append("</div>");
						}
					
						if("1".equals(fromFlag)){//部门人员
							StringBuffer buffer = new StringBuffer();
							ArrayList allDeptList = new ArrayList();
							for(int j=0;j<this.leafList.size();j++){
								LazyDynaBean bean = (LazyDynaBean)this.leafList.get(j);
								String e01a1 =(String)bean.get("e01a1");
								ArrayList deptList = this.getDept(e01a1, "USR");
								if(deptList!=null){
									String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
									for(int k=0;k<deptList.size();k++){
										LazyDynaBean abean =(LazyDynaBean)deptList.get(k);
										String ae0122=(String)abean.get("e0122");
										buffer.append(",'"+ae0122+"'");
										abean.set("color", color);
										allDeptList.add(abean);
									}
								}
							}
							this.getBzData(buffer.toString());
							String selfE0122=this.userView.getUserDeptId();
							if(allDeptList.size()<=5){
								htmlStr.append("<table align=\"center\"><tr><td align=\"center\">");
								htmlStr.append("<div class=\"epm-gl-top\">");
								htmlStr.append("<table align=\"center\"><tr>");
								htmlStr.append("<td align=\"center\">");
								htmlStr.append("<ul>");
								for(int j=0;j<this.leafList.size();j++){
									LazyDynaBean bean = (LazyDynaBean)this.leafList.get(j);
									String e01a1 =(String)bean.get("e01a1");
									ArrayList deptList = this.getDept(e01a1, "USR");
									int ptype=Integer.parseInt((String)bean.get("_c01sc"));
									if(deptList!=null){
										String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
										for(int k=0;k<deptList.size();k++){
											LazyDynaBean abean =(LazyDynaBean)deptList.get(k);
											String deptid=(String)abean.get("e0122");
											int planNum = 0;
											int realNum = 0;
											if(this.planMap!=null&&this.planMap.get(deptid.toUpperCase())!=null)
												planNum=Integer.parseInt(((String)this.planMap.get(deptid.toUpperCase())));
											if(this.realMap!=null&&this.realMap.get(deptid.toUpperCase())!=null)
												realNum=Integer.parseInt(((String)this.realMap.get(deptid.toUpperCase())));
											String string="";
											if(realNum>planNum)
												string="(超编"+(realNum-planNum)+"人)";
											String deptdesc=(String)abean.get("e0122desc");
							            	htmlStr.append("<li><div style=\"background:"+color+"\" class=\"epm-gl-chuz\">");
							            	htmlStr.append("<dl>");
							            	htmlStr.append("<dt>");
									        if(positionType<ptype||selfE0122.equalsIgnoreCase(deptid)){
									        	htmlStr.append("<a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_init=init&fromFlag=2&e0122="+deptid+"\">");
									        }else{
									            	
									        }
									        htmlStr.append("<img src='/images/epm_cs_p.jpg' />");
									        if(positionType<ptype||selfE0122.equalsIgnoreCase(deptid))
									        {
									            htmlStr.append("</a>");
									        }else{
									            	
									        }
									        htmlStr.append("</dt>");
							            	htmlStr.append("<dd style=\"valign:middle;padding-top:10px;height:30px;\">");
							            	//epm_cs_p.jpg
							            	//if(positionType>ptype||selfE01a1.equalsIgnoreCase(e01a1))
							                	//htmlStr.append("<a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_init=init&fromFlag=2&e0122="+deptid+"\">");
							            	htmlStr.append("<font style=\"font-weight:bolder;font-size:14px;\">"+deptdesc+"</font>");
							            	//if(positionType>ptype||selfE01a1.equalsIgnoreCase(e01a1))
							                	//htmlStr.append("</a>");
							            	htmlStr.append("</dd>");
							            	htmlStr.append("<dd>"+string+"</dd>");
							            	//htmlStr.append("<dd>&nbsp;</dd>");
							                htmlStr.append("</dl>");
							                htmlStr.append("</div></li>");
										}
									}
								}
								htmlStr.append("</ul>");
								htmlStr.append("</td>");
								htmlStr.append("</tr></table>");
								htmlStr.append("</div>");
								htmlStr.append("</td></tr></table>");
							}else{
								htmlStr.append("<table align=\"center\"><tr><td align=\"center\">");
								htmlStr.append("<div class=\"v_show\">");
								htmlStr.append("<table align=\"center\"><tr>");
								htmlStr.append("<td align=\"center\">");
								htmlStr.append("<img style=\"cursor:hand;\" id=\"prev\" src='/images/epm_zuojiantou.jpg' />&nbsp;");
								htmlStr.append("</td>");
								htmlStr.append("<td align=\"left\">");
								htmlStr.append("<div class=\"v_content\">");
								htmlStr.append("<div  class=\"v_content_list\">");
								htmlStr.append("<ul>");
								for(int j=0;j<this.leafList.size();j++){
									LazyDynaBean bean = (LazyDynaBean)this.leafList.get(j);
									String e01a1 =(String)bean.get("e01a1");
									int ptype=Integer.parseInt((String)bean.get("_c01sc"));
									ArrayList deptList = this.getDept(e01a1, "USR");
									if(deptList!=null){
										String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
										for(int k=0;k<deptList.size();k++){
											LazyDynaBean abean =(LazyDynaBean)deptList.get(k);
											String deptid=(String)abean.get("e0122");
											int planNum = 0;
											int realNum = 0;
											if(this.planMap!=null&&this.planMap.get(deptid.toUpperCase())!=null)
												planNum=Integer.parseInt(((String)this.planMap.get(deptid.toUpperCase())));
											if(this.realMap!=null&&this.realMap.get(deptid.toUpperCase())!=null)
												realNum=Integer.parseInt(((String)this.realMap.get(deptid.toUpperCase())));
											String string="";
											if(realNum>planNum)
												string="(超编"+(realNum-planNum)+")";
											String deptdesc=(String)abean.get("e0122desc");
							            	htmlStr.append("<li><div style=\"background:"+color+"\" class=\"epm-gl-chuz\">");
							            	htmlStr.append("<dl>");
							            	htmlStr.append("<dt>");
									        if(positionType<ptype||selfE0122.equalsIgnoreCase(deptid)){
									        	htmlStr.append("<a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_init=init&fromFlag=2&e0122="+deptid+"\">");
									        }else{
									            	
									        }
									        htmlStr.append("<img src='/images/epm_cs_p.jpg' />");
									        if(positionType<ptype||selfE0122.equalsIgnoreCase(deptid))
									        {
									            htmlStr.append("</a>");
									        }else{
									            	
									        }
									        htmlStr.append("</dt>");
									        htmlStr.append("<dd style=\"valign:middle;padding-top:10px;height:30px;\">");
							            	htmlStr.append("<font style=\"font-weight:bolder;font-size:14px;\">"+deptdesc+"</font>");
							            	htmlStr.append("</dd>");
							            	htmlStr.append("<dd>"+string+"</dd>");
							                htmlStr.append("</dl>");
							                htmlStr.append("</div></li>");
										}
									}
								}
								htmlStr.append("</ul>");
								htmlStr.append("</div>");
								htmlStr.append("</div>");
								htmlStr.append("</td>");
								htmlStr.append("<td align=\"center\">");
								htmlStr.append(" &nbsp; <img style=\"cursor:hand;\" id=\"next\" src='/images/epm_youjiantou.jpg' />");
								htmlStr.append("</td></tr></table>");
								htmlStr.append("</div>");
								htmlStr.append("</td></tr></table>");
							}
						}
						else//处室人员
						{

                           // ArrayList aplArrayList = this.getAllPos(e0122);
							//ArrayList personList = this.getPersons(e0122, 1);
							int count=0;
							StringBuffer buffer =new StringBuffer("");
							for(int i=0;i<this.leafList.size();i++){
								LazyDynaBean bean = (LazyDynaBean)this.leafList.get(i);
								String posString = (String)bean.get("e01a1");
								ArrayList personList=(ArrayList)this.personsMap.get(posString.toUpperCase());
								if(personList!=null){
									for(int k=0;k<personList.size();k++){
										LazyDynaBean abean =(LazyDynaBean)personList.get(k);
										String a0100=(String)abean.get("a0100");
										String nbase=(String)abean.get("nbase");
										String e01a1 =(String)abean.get("e01a1");
										int ptype=Integer.parseInt((String)abean.get("_c01sc"));
										String tempName = hmusterPdf.createPhotoFile(nbase+"A00",a0100, "P");
										String src="/images/photo.jpg";
										if(tempName.length()>0){
											src="/servlet/DisplayOleContent?filename="+ tempName;
										}
										buffer.append("<li><div class=\"epm-gl-chuz\">");
										buffer.append("<dl>");
										buffer.append("<dt>");
							            if(positionType<ptype||selfE01a1.equalsIgnoreCase(posString))
							            	buffer.append("<a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase+"\">");
							            buffer.append("<img src='"+src+"' />");
							            if(positionType<ptype||selfE01a1.equalsIgnoreCase(posString))
							            	buffer.append("</a>");
							            buffer.append("</dt>");
							            for(int l=0;l<arr.length;l++){
											String itemid=arr[l].split("`")[1];
											buffer.append("<dd>"+(String)abean.get(itemid)+"</dd>");
							             }
							            buffer.append("</dl>");
							            buffer.append("</div></li>");
							            count++;
									}
								}else{
									String color = "";
									if(colorMap.get(posString.toUpperCase())!=null){
										color=this.color[Integer.parseInt((String)colorMap.get(posString.toUpperCase()))];
									}else{
										if(colorIndex>=this.color.length){
											colorIndex=0;
										}
										color=this.color[colorIndex];
										colorMap.put(posString.toUpperCase(), colorIndex+"");
										colorIndex++;
									}
									buffer.append("<td align=\"center\">"); 
									if("1".equals(fromFlag))
										buffer.append("<div style=\"background:"+color+"\" class=\"epm-gl-chuz\">");
									else
										buffer.append("<div  class=\"epm-gl-chuz\">");
									buffer.append("<dl>");
									buffer.append("<dt>");
									buffer.append("<img src='/images/epm_quebian.jpg' />");
									buffer.append("</dt>");
						            if(item!=null){
						            	buffer.append("<dd>"+item.getItemdesc()+":"+AdminCode.getCodeName("@K", posString)+"</dd>");
						            }else{
						            	buffer.append("<dd>"+AdminCode.getCodeName("@K", posString)+"</dd>");
						            }
						            for(int l=1;l<arr.length; l++){
										String itemid=arr[l].split("`")[1];
										buffer.append("<dd>&nbsp;</dd>");
						             }
						            buffer.append("</dl>");
						            buffer.append("</div>");
						            buffer.append("</td>");
								}
							}
								if(count<=5){
									htmlStr.append("<table align=\"center\"><tr><td align=\"center\">");
									htmlStr.append("<div class=\"epm-gl-top\">");
									htmlStr.append("<table align=\"center\"><tr>");
									htmlStr.append("<td align=\"center\">");
									htmlStr.append("<ul>");
									htmlStr.append(buffer.toString());
									htmlStr.append("</ul>");
									htmlStr.append("</td>");
									htmlStr.append("</tr></table>");
									htmlStr.append("</div>");
									htmlStr.append("</td></tr></table>");
								}else{
									htmlStr.append("<table align=\"center\"><tr><td align=\"center\">");
									htmlStr.append("<div class=\"v_show\">");
									htmlStr.append("<table align=\"center\"><tr>");
									htmlStr.append("<td align=\"center\">");
									htmlStr.append("<img style=\"cursor:hand;\" id=\"prev\" src='/images/epm_zuojiantou.jpg' />&nbsp;");
									htmlStr.append("</td>");
									htmlStr.append("<td align=\"left\">");
									htmlStr.append("<div class=\"v_content\">");
									htmlStr.append("<div  class=\"v_content_list\">");
									htmlStr.append("<ul>");
									htmlStr.append(buffer.toString());
									htmlStr.append("</ul>");
									htmlStr.append("</div>");
									htmlStr.append("</div>");
									htmlStr.append("</td>");
									htmlStr.append("<td align=\"center\">");
									htmlStr.append(" &nbsp; <img style=\"cursor:hand;\" id=\"next\" src='/images/epm_youjiantou.jpg' />");
									htmlStr.append("</td></tr></table>");
									htmlStr.append("</div>");
									htmlStr.append("</td></tr></table>");
								}
						}
					}else{//通用程序
						for(int i=1;i<=this.maxLay;i++){
							ArrayList positionList = (ArrayList)this.layMap.get(i+"");
							if(i==this.maxLay){
								ArrayList allPosPersonList = new ArrayList();
								for(int j=0;j<positionList.size();j++){
									LazyDynaBean bean = (LazyDynaBean)positionList.get(j);
									String e01a1 =(String)bean.get("e01a1");
									if(colorMap.get(e01a1)!=null){
										
									}else{
										if(colorIndex>=this.color.length){
											colorIndex=0;
										}
										colorMap.put(e01a1.toUpperCase(), colorIndex+"");
										colorIndex++;
									}
									ArrayList personList = (ArrayList)this.personsMap.get(e01a1.toUpperCase());
									String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
									if(personList!=null){
										for(int k=0;k<personList.size();k++){
											LazyDynaBean abean =(LazyDynaBean)personList.get(k);
											abean.set("color", color);
											allPosPersonList.add(abean);
										}
									}
								}
								if(allPosPersonList.size()<=5){
									htmlStr.append("<table align=\"center\"><tr><td align=\"center\">");
									htmlStr.append("<table align=\"center\"><tr>");
									htmlStr.append("<td align=\"center\">");
									htmlStr.append("<ul>");
									for(int j=0;j<allPosPersonList.size();j++){
										LazyDynaBean bean = (LazyDynaBean)allPosPersonList.get(j);
										String a0100=(String)bean.get("a0100");
										String nbase=(String)bean.get("nbase");
										String color=(String)bean.get("color");
										String tempName = hmusterPdf.createPhotoFile(nbase+"A00",a0100, "P");
										String src="/images/photo.jpg";
										if(tempName.length()>0){
											src="/servlet/DisplayOleContent?filename="+ tempName;
										}
						            	htmlStr.append("<li><div  class=\"epm-gl-chuz\">");//style=\"background:"+color+"\"
						            	htmlStr.append("<dl>");
						                htmlStr.append("<dt><a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase+"\"><img src='"+src+"' /></a></dt>");
						                for(int l=0;l<arr.length;l++){
											String itemid=arr[l].split("`")[1];
											htmlStr.append("<dd>"+(String)bean.get(itemid)+"</dd>");
						                }
						                htmlStr.append("</dl>");
						                htmlStr.append("</div></li>");									}
									htmlStr.append("</ul>");
									htmlStr.append("</td>");
									htmlStr.append("</tr></table>");
									htmlStr.append("</div>");
									htmlStr.append("</td></tr></table>");
								}else{
									htmlStr.append("<table align=\"center\"><tr><td align=\"center\">");
									htmlStr.append("<div class=\"v_show\">");
									htmlStr.append("<table align=\"center\"><tr>");
									htmlStr.append("<td align=\"center\">");
									htmlStr.append("<img style=\"cursor:hand;\" id=\"prev\" src='/images/epm_zuojiantou.jpg' />&nbsp;");
									htmlStr.append("</td>");
									htmlStr.append("<td align=\"left\">");
									htmlStr.append("<div class=\"v_content\">");
									htmlStr.append("<div  class=\"v_content_list\">");
									htmlStr.append("<ul>");
									for(int j=0;j<positionList.size();j++){
										LazyDynaBean bean = (LazyDynaBean)positionList.get(j);
										String e01a1 =(String)bean.get("e01a1");
										if(colorMap.get(e01a1)!=null){
											
										}else{
											if(colorIndex>=this.color.length){
												colorIndex=0;
											}
											colorMap.put(e01a1.toUpperCase(), colorIndex+"");
											colorIndex++;
										}
										ArrayList personList = (ArrayList)this.personsMap.get(e01a1.toUpperCase());
										String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
										if(personList!=null){
											for(int k=0;k<personList.size();k++){
												LazyDynaBean abean =(LazyDynaBean)personList.get(k);
												String a0100=(String)abean.get("a0100");
												String nbase=(String)abean.get("nbase");
												String tempName = hmusterPdf.createPhotoFile(nbase+"A00",a0100, "P");
												String src="/images/photo.jpg";
												if(tempName.length()>0){
													src="/servlet/DisplayOleContent?filename="+ tempName;
												}
								            	htmlStr.append("<li><div  class=\"epm-gl-chuz\">");//style=\"background:"+color+"\"
								            	htmlStr.append("<dl>");
								                htmlStr.append("<dt><a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase+"\"><img src='"+src+"' /></a></dt>");
								                for(int l=0;l<arr.length;l++){
													String itemid=arr[l].split("`")[1];
													htmlStr.append("<dd>"+(String)abean.get(itemid)+"</dd>");
								                }
								                htmlStr.append("</dl>");
								                htmlStr.append("</div></li>");
											}
										}
									}
									htmlStr.append("</ul>");
									htmlStr.append("</div>");
									htmlStr.append("</div>");
									htmlStr.append("</td>");
									htmlStr.append("<td align=\"center\">");
									htmlStr.append(" &nbsp; <img style=\"cursor:hand;\" id=\"next\" src='/images/epm_youjiantou.jpg' />");
									htmlStr.append("</td></tr></table>");
									htmlStr.append("</div>");
									htmlStr.append("</td></tr></table>");
								}
							}else{
								htmlStr.append("<div class=\"epm-gl-top\">");
				            	htmlStr.append("<table border=\"1\" align=\"center\">");
				            	htmlStr.append("<tr>");
								for(int j=0;j<positionList.size();j++){
									LazyDynaBean bean = (LazyDynaBean)positionList.get(j);
									String e01a1 =(String)bean.get("e01a1");
									if(colorMap.get(e01a1)!=null){
										
									}else{
										if(colorIndex>=this.color.length){
											colorIndex=0;
										}
										colorMap.put(e01a1.toUpperCase(), colorIndex+"");
										colorIndex++;
									}
									String color = this.color[Integer.parseInt((String)colorMap.get(e01a1.toUpperCase()))];
									ArrayList personList = (ArrayList)this.personsMap.get(e01a1.toUpperCase());
									if(personList!=null){
										for(int k=0;k<personList.size();k++){
											LazyDynaBean abean =(LazyDynaBean)personList.get(k);
											String a0100=(String)abean.get("a0100");
											String nbase=(String)abean.get("nbase");
											String tempName = hmusterPdf.createPhotoFile(nbase+"A00",a0100, "P");
											String src="/images/photo.jpg";
											if(tempName.length()>0){
												src="/servlet/DisplayOleContent?filename="+ tempName;
											}
											htmlStr.append("<td align=\"center\">"); 
							            	htmlStr.append("<div  class=\"epm-gl-chuz\">");//style=\"background:"+color+"\"
							            	htmlStr.append("<dl>");
							                htmlStr.append("<dt><a href=\"/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase+"\"><img src='"+src+"' /></a></dt>");
							                for(int l=0;l<arr.length;l++){
												String itemid=arr[l].split("`")[1];
												htmlStr.append("<dd>"+(String)abean.get(itemid)+"</dd>");
							                }
							                htmlStr.append("</dl>");
							                htmlStr.append("</div>");
							                htmlStr.append("</td>");
										}
							    	}
								}
								htmlStr.append("</tr>");
								htmlStr.append("</table>");
								htmlStr.append("</div>");
							}
						}
					}
				htmlStr.append("</td></tr></table>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return htmlStr.toString();
	}
	public void anylsePosData(){
		try{
			for(int i=0;i<this.posList.size();i++){
				LazyDynaBean bean = (LazyDynaBean)this.posList.get(i);
				String e01a1=(String)bean.get("e01a1");
				String parentid=(String)bean.get("parentid");
				boolean isTop=true;
				for(int j=0;j<this.posList.size();j++){
					LazyDynaBean abean = (LazyDynaBean)this.posList.get(j);
					String ae01a1=(String)abean.get("e01a1");
					//formFlag  =1从部门人员进入,=2从部门人员进入处室人员,=3从处室人员进入
					if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))&&("2".equals(this.formFlag)|| "3".equals(this.formFlag))){
						if(parentid.equalsIgnoreCase(ae01a1)|| "".equals(parentid)){//国网主要处理处室人员中的职员，因为职员没有汇报关系，
							isTop = false;
							break;
						}
					}else {
                        if(parentid.equalsIgnoreCase(ae01a1)){//有父亲节点，不是最顶层节点
					    	isTop=false;
					    	break;
                        }
					}
				}
				if(isTop){
					topList.add(bean);
				}else{
					boolean isLeaf=true;
					if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))&&("2".equals(this.formFlag)|| "3".equals(this.formFlag))){//国网项目特殊处理
						if(!"".equals(parentid)){
							isLeaf=false;
						}
					}else{
						for(int j=0;j<this.posList.size();j++){
							LazyDynaBean abean = (LazyDynaBean)this.posList.get(j);
							String aparentid=(String)abean.get("parentid");
							if(aparentid.equalsIgnoreCase(e01a1))//有孩子节点，不是叶子节点
							{
								isLeaf=false;
								break;
							}
						}
					}
					if(isLeaf){
						leafList.add(bean);
						leafMap.put(e01a1.toUpperCase(), "1");
					}
				}
			}
			for(int i=0;i<this.topList.size();i++){
				LazyDynaBean bean = (LazyDynaBean)this.topList.get(i);
				if(layMap.get("1")!=null){
					ArrayList alist=(ArrayList)this.layMap.get("1");
					alist.add(bean);
				}else{
					ArrayList alist=new ArrayList();
					alist.add(bean);
					this.layMap.put("1", alist);
				}
				this.doMethod(bean, 1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public boolean isTop(String e01a1){
		RowSet rs = null;
		boolean isTop = true;
		try{
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search("select a0100 from usra01 where UPPER(e01a1)='"+e01a1+"' and c01sc in('05','06')");
			while(rs.next()){
				isTop=false;
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return isTop;
	}
	public void doMethod(LazyDynaBean parentBean,int layer){
		try{
			String e01a1=(String)parentBean.get("e01a1");
			layer++;
			for(int i=0;i<this.posList.size();i++){
				LazyDynaBean bean = (LazyDynaBean)this.posList.get(i);
				String ae01a1 = (String)bean.get("e01a1");
				String aparentid=(String)bean.get("parentid");
				if(e01a1.equalsIgnoreCase(aparentid)){//是孩子节点，继续找下去
					if(layMap.get(layer+"")!=null){
						ArrayList alist=(ArrayList)this.layMap.get(layer+"");
						alist.add(bean);
						StringBuffer buf = (StringBuffer)this.layBufMap.get(layer+"");
						buf.append(",'"+ae01a1+"'");
						this.layBufMap.put(layer+"", buf);
					}else{
						ArrayList alist=new ArrayList();
						alist.add(bean);
						this.layMap.put(layer+"", alist);
						StringBuffer buf = new StringBuffer("");
						buf.append("'"+ae01a1+"'");
						this.layBufMap.put(layer+"", buf);
					}
					if(layer>this.maxLay)
						this.maxLay=layer;
					
					this.doMethod(bean, layer);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	int maxLay=1;//部门内岗位最大层级
	private HashMap layBufMap = new HashMap();
	private HashMap layMap = new HashMap();//每一层对应的岗位列表
	private ArrayList posList = new ArrayList();//所有岗位列表
	private HashMap childMap = new HashMap();
	private ArrayList topList = new ArrayList();//部门内岗位的最顶层
	private ArrayList leafList = new ArrayList();//部门内岗位的最底层
	private HashMap leafMap = new HashMap();//最底层岗位
    private HashMap personsMap = new HashMap();
	/**
	 * 取得部门所有岗位，用来分析上下层级关系
	 * @param e0122
	 * @return
	 */
	public void getPosList(String e0122){
		RowSet rs = null;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" select a.e01a1,a."+this.parentPosField+",a.e0122 ");
			if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				sql.append(",b.c01sc");
			}
			sql.append(" from k01 a ");
			if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				sql.append(" left join usra01 b on a.e01a1=b.e01a1 ");
			sql.append(" where UPPER(a.e0122)='"+e0122.toUpperCase()+"' ");
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while(rs.next()){
				bean = new LazyDynaBean();
				bean.set("e01a1", rs.getString("e01a1"));
				bean.set("e0122", rs.getString("e0122"));
				bean.set("parentid",rs.getString(this.parentPosField)==null?"":rs.getString(this.parentPosField));
				if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
		     		bean.set("_c01sc", DeptPersonBo.getPositionType(rs.getString("c01sc"))+"");
				this.posList.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public ArrayList getPersons(String e0122,int type){
		RowSet rowSet = null;
		ArrayList returnList = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.con);
			String fields=SystemConfig.getPropertyValue("personvisiblefield");
			if(fields==null|| "".equals(fields))
				fields="A01`b0110,A01`e0122,A01`e01a1,A01`a0101";
			String[] arr=fields.split(",");
			StringBuffer select = new  StringBuffer("select USRA01.a0100,USRA01.e01a1");
			StringBuffer from = new StringBuffer(" from USRA01 ");
			HashMap map = new HashMap();
			for(int i=0;i<arr.length;i++){
				String setid=arr[i].split("`")[0];
				String itemid=arr[i].split("`")[1];
				if(map.get(setid.toUpperCase())==null){
					if(!"A01".equalsIgnoreCase(setid)){
						from.append(" left join ");
						from.append("(SELECT * FROM ");
						from.append(" USR"+setid);
						from.append(" A WHERE ");
						from.append(" A.I9999 =(SELECT MAX(B.I9999) FROM ");
						from.append(" USR"+setid);
						from.append(" B WHERE ");
						from.append(" A.A0100=B.A0100  )) ");
						from.append(" USR"+setid);
						from.append(" on USRA01.a0100=");
						from.append(" USR"+setid+".a0100 ");
					    map.put(setid.toUpperCase(), "1"); 
					}
				}
				if(!"e01a1".equalsIgnoreCase(itemid))
			    	select.append(",USR"+setid+"."+itemid);
			}
			String str="";
			if(type==1&& "gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){
				str=" and c01sc in('05','06')";
			}
			if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){
				select.append(" ,USRA01.c01sc ");
			}
			rowSet = dao.search(select.toString()+" "+from.toString()+" where USRA01.e0122='"+e0122.toUpperCase()+"' "+str);
			while(rowSet.next()){
				LazyDynaBean bean = new LazyDynaBean();
				String e01a1 = rowSet.getString("e01a1");
				bean.set("e01a1", e01a1);
				bean.set("nbase", "USR");
				bean.set("a0100", rowSet.getString("a0100"));
				if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){
					bean.set("_c01sc", DeptPersonBo.getPositionType(rowSet.getString("c01sc"))+"");
				}
				for(int i=0;i<arr.length;i++){
					String itemid=arr[i].split("`")[1];
					FieldItem fielditem = DataDictionary.getFieldItem(itemid.toLowerCase());
					String fielditemValue = "";
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						fielditemValue = rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
						fielditemValue=PubFunc.round(fielditemValue,fielditem.getDecimalwidth());
					}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
						if("0".equals(fielditem.getCodesetid())){
							fielditemValue = rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
						}else{
							fielditemValue = rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
							fielditemValue = AdminCode.getCodeName(fielditem.getCodesetid(),fielditemValue);
						}
					}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
						    Date date = rowSet.getDate(itemid);
						    fielditemValue = sdf.format(date);
					}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
						   fielditemValue = Sql_switcher.readMemo(rowSet, itemid)==null?"":Sql_switcher.readMemo(rowSet, itemid);
					}else{
						fielditemValue = rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
					}
					bean.set(itemid,fielditem.getItemdesc()+":"+fielditemValue);
				}
				if(this.personsMap.get(e01a1)!=null){
					ArrayList list = (ArrayList)this.personsMap.get(e01a1);
					list.add(bean);
					this.personsMap.put(e01a1, list);
				}else{
					ArrayList list = new ArrayList();
					list.add(bean);
					this.personsMap.put(e01a1, list);
				}
				returnList.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return returnList;
	}
	/**
	 * 根据岗位查到分管处室
	 * @param e01a1
	 * @return
	 */
	public ArrayList getDept(String e01a1,String nbase){
		RowSet rs = null;
		ArrayList list  = new ArrayList();
		try{
			StringBuffer buf = new StringBuffer("");
			buf.append(" select codeitemid,codeitemdesc from organization");
			buf.append(" where codeitemid in(");
			buf.append(" select e0122 from k01 where ");
			buf.append(" UPPER("+this.parentPosField+")='");
			buf.append(e01a1.toUpperCase()+"')");
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(buf.toString());
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("e0122",rs.getString("codeitemid"));
				bean.set("e0122desc",AdminCode.getCodeName("UM", rs.getString("codeitemid")));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	public String getParentString(String e0122){
		String ret="";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search("select codeitemid from organization where UPPER(codesetid)='UM' and codeitemid=(select parentid from organization where codeitemid='"+e0122+"')");
			while(rs.next()){
				ret=rs.getString("codeitemid")==null?"":rs.getString("codeitemid");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return ret;
	}
	public ArrayList getAllPos(String e0122){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(" select codeitemid,codeitemdesc from organization where parentid='"+e0122+"' and codesetid='@K' ");
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("e01a1", rs.getString("codeitemid"));
				bean.set("codeitemdesc", rs.getString("codeitemdesc"));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs!=null) {
					rs.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}
	private HashMap planMap =null;
	private HashMap realMap =null;
	/**
	 * 取单位编制编制人数和实有人数
	 * @param e0122s
	 */
	public void getBzData(String e0122s){
		RowSet rSet=null;
		try {
			if(this.unitSet!=null&&!"".equals(this.unitSet.trim())&&this.planField!=null&&!"".equals(this.planField.trim())){
				planMap=new HashMap();
				realMap = new HashMap();
				StringBuffer buffer = new StringBuffer();
				buffer.append(" select b0110,"+this.planField+" from ");
				buffer.append(this.unitSet+" where b0110 in ("+e0122s.substring(1)+")");
				ContentDAO dao = new ContentDAO(con);
				rSet=dao.search(buffer.toString());
				while(rSet.next()){
					planMap.put(rSet.getString("b0110"), rSet.getInt(this.planField)+"");
				}
				rSet.close();
				buffer.setLength(0);
				buffer.append(" select e0122,count(a0100) from usra01 where e0122 in ("+e0122s.substring(1)+") group by e0122");
				rSet=dao.search(buffer.toString());
				while(rSet.next()){
					realMap.put(rSet.getString(1).toUpperCase(), rSet.getInt(2)+"");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rSet!=null)
					rSet.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	
	}
}
