package com.hjsj.hrms.module.template.templatetoolbar.quickselect;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** 
 * @Title: TemplateQuickSelectTrans.java 
 * @Package  
 * @Description:  
 * @author gaohy 
 * @date Feb 20, 2016 6:10:44 PM 
 * @version V7x 
 */

/**
 * 
 * @Description: 人事异动-快速插入
 * @author gaohy 
 * @date Feb 20, 2016 6:10:44 PM 
 * @version V7x
 */
public class TemplateQuickSelectTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		String opt = (String) this.getFormHM().get("opt");
		opt=opt!=null&&opt.trim().length()>0?opt:"";
		//快速查询
		if("1".equals(opt)){
			String inputValue = (String) this.getFormHM().get("inputValue");
			inputValue=inputValue!=null&&inputValue.trim().length()>0?inputValue:"";
			/**是否显示部门=0不显示=1显示*/
			String isVisibleUM="1";
			if(this.getFormHM().get("isVisibleUM")!=null)
			{
				isVisibleUM=(String)this.getFormHM().get("isVisibleUM");
				this.getFormHM().remove("isVisibleUM");
			}
			/**是否显示职位=0不显示=1显示*/
			String isVisibleK="0";
			if(this.getFormHM().get("isVisibleK")!=null)
			{
				isVisibleK=(String)this.getFormHM().get("isVisibleK");
				this.getFormHM().remove("isVisibleK");
			}
			if(inputValue.length()>0){
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一标识
				String valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否设置了唯一标识
				String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
				if("0".equals(valid))
					onlyname="";
				String _onlyname="";
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				if(item!=null&&!"0".equals(item.getUseflag())){
				if(onlyname!=null&&onlyname.trim().length()>0)
					_onlyname=","+onlyname;
				}
				else {
				    onlyname=""; 
				}
				DbNameBo dbbo=new DbNameBo(this.frameconn);
				ArrayList dblist=dbbo.getAllDbNameVoList(this.userView);
				String sql=getQueryString(inputValue,dblist,"1",_onlyname,pinyin_field);//根据查询人员的姓名或编号获取sql
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				RowSet rset=null;
				StringBuffer str_value=new StringBuffer("");
				String str = ""; //xyy记录上一个人员的人员库
				ArrayList objlist=new ArrayList();
				int i=0;
				ArrayList insertDataList = new ArrayList();//多条数据
				ArrayList dataList = new ArrayList();//一条数据
				try {
					rset=dao.search(sql);
					while(rset.next())
					{
						if(i>40)
							break;
						CommonData objvo=new CommonData();
						String b0110=rset.getString("b0110");
						String name="";
						/**当显示唯一性指标时,只显示部门,要不太长了,看不见了*/
						if(_onlyname==null||_onlyname.trim().length()==0)
						{
							name=AdminCode.getCodeName("UN",b0110);
						}
						
						if(b0110!=null&&b0110.trim().length()>0)
							str_value.append(AdminCode.getCodeName("UN",b0110));//单位
						
						
						if("1".equals(isVisibleUM))
						{
							String ename="";
							String e0122=rset.getString("e0122");
							if(e0122!=null)
								ename=AdminCode.getCodeName("UM", e0122);
							if(ename!=null&&!"".equals(ename))
							{
								if(name.length()>0)
							    	name+="/"+ename;
								else
									name+=ename;
								if(b0110!=null&&b0110.trim().length()>0)
									str_value.append("/"+ename);
								else
									str_value.append(ename);
							}
						}
						if("1".equals(isVisibleK)&&(onlyname==null||onlyname.trim().length()==0))
						{
							String ename="";
							String e01a1=rset.getString("e01a1");
							if(e01a1!=null)
								ename=AdminCode.getCodeName("@K", e01a1);
							if(ename!=null&&!"".equals(ename))
								name+="/"+ename;
						}
						
						
						
						String objvoName="";
						
						if(!str.equals(rset.getString("dbpre"))){
						   // objlist.add(new CommonData(rset.getString("dbpre"),"---------【"+AdminCode.getCodeName("@@", rset.getString("dbpre"))+"】---------"));
						    dataList = new ArrayList();
							dataList.add("dbpre|"+i);
							dataList.add("---【"+AdminCode.getCodeName("@@", rset.getString("dbpre"))+"】---");
							dataList.add(rset.getString("dbpre"));
							insertDataList.add(dataList);
						    
						}
						if(onlyname!=null&&onlyname.trim().length()>0)
						{
							String only_value=rset.getString(onlyname);
							if(only_value==null)
								only_value="";
							objvoName=name+"/"+rset.getString("a0101")+"("+only_value+")";
							
						}
						else
						{
							objvoName=AdminCode.getCodeName("@@", rset.getString("dbpre"))+" "+name+"("+rset.getString("a0101")+")";//汉字显示的时候人员库也显示汉字，lzw 2010-11-11
						}
						
						objvo.setDataName(objvoName.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
						//objvo.setDataValue(rset.getString("dbpre")+rset.getString("a0100"));
						
						str_value.append("~"+rset.getString("dbpre")+rset.getString("a0100"));
						objlist.add(objvo);
						dataList = new ArrayList();
						dataList.add(rset.getString("a0100"));
						dataList.add(objvoName.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
						dataList.add(rset.getString("dbpre"));
						insertDataList.add(dataList);
						str = rset.getString("dbpre");//方便与下条数据库前缀对比
						++i;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.getFormHM().put("insertDataList", insertDataList);
			}
		}else if("2".equals(opt)){
			String id = (String) this.getFormHM().get("id");//选中的人员编号
			id=id!=null&&id.trim().length()>0?id:"";
			
			String dbName  = (String) this.getFormHM().get("dbName");//选中的人员库前缀
			dbName=dbName!=null&&dbName.trim().length()>0?dbName:"";
			
			String tabid = (String) this.getFormHM().get("tabid");//模版Id
			tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
			
			ArrayList a0100s = new ArrayList();//人员编号
			a0100s.add(id);
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			Boolean flag = false;//所有数据导入成功
			if(a0100s.size()>0&&!"0".equals(id)){
				Boolean resultflag=tablebo.impDataFromArchive(a0100s, dbName);//按人员库前缀导入数据
				if(resultflag)
					flag=true;//导入失败
			}
			this.getFormHM().put("flag", flag);
		}
	}
	/**
	 * 
	 * @param a0101
	 * @param dblist
	 * @param isPriv
	 * @param onlyname
	 * @param pinyin_field
	 * @return
	 * @throws GeneralException
	 * @Description: 根据查询人员的姓名或编号获取sql
	 * @author gaohy 
	 * @date Mar 2, 2016 10:22:53 AM 
	 * @version V7x
	 */
		private String getQueryString(String a0101,ArrayList dblist,String isPriv,String onlyname,String pinyin_field)throws GeneralException
		{
			StringBuffer buf=new StringBuffer();
	    	ArrayList fieldlist=new ArrayList();
			String strWhere=null;    
			String sexpr="1";
			String sfactor="A0101="+a0101+"*`";
			
			String _onlyname ="";
			if(onlyname.length()>0)
				_onlyname = onlyname.substring(1,onlyname.length());
	  	    for(int i=0;i<dblist.size();i++)
	 	    {
	  	    	RecordVo vo=(RecordVo)dblist.get(i);
	  	    	String pre=vo.getString("pre");
	    		strWhere=userView.getPrivSQLExpression(sexpr+"|"+sfactor,pre,false,fieldlist);
	  	    	buf.append("select "+pre+"a01.a0000, "+pre+"a01.a0101,"+pre+"a01.b0110,"+pre+"a01.a0100,"+pre+"a01.e0122,"+pre+"a01.e01a1, '");
	  	    	buf.append(vo.getString("pre"));
	  	    	buf.append("' as dbpre  "+onlyname);
	  	    	/**lzw 是否加权限控制：=1加=0不加*/
	  	    	
	  	    		buf.append(" from "+vo.getString("pre")+"a01 where ( ");
	  	    		buf.append(vo.getString("pre")+"a01.a0101 like '"+a0101+"%' ");
	  	    	
	  	    		if(onlyname.length()>0){
	  	    	FieldItem item = DataDictionary.getFieldItem(_onlyname);
				String whl = "";
				if (item != null) {
					buf.append( " OR " + item.getItemid() + " like '" + a0101
							+ "%'");
				}
	  	    	}
				FieldItem  pyItem  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
				if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||pyItem==null|| "0".equals(pyItem.getUseflag())))
					buf.append("or " + pinyin_field + " like '"+ a0101 + "%'");
				buf.append(" )");
				
				
				
				if("1".equals(isPriv)&&!this.userView.isSuper_admin())
	  	    	{
	  	        	//权限控制，管理范围和操作单位
					StringBuffer privSQL = new StringBuffer();

					String priStrSql = "";
//					String modeType=(String)this.getFormHM().get("modeType");
//		            if(!"23".equals(modeType)){  //考勤业务   业务模板关联了考勤申请单，模板选人时需按考勤业务范围控制 liuzy 20151125
//						priStrSql = InfoUtils.getWhereINSql(this.userView, pre);
//					}else{
						priStrSql = RegisterInitInfoData.getWhereINSql(this.userView,pre);
					
					
					StringBuffer aa = new StringBuffer("");
					aa.append("select " + pre
							+ "a01.A0100 ");
					if (priStrSql.length() > 0)
						aa.append(priStrSql);
					else
						aa.append(" from " + pre + "a01");
					privSQL.append(" and ( "+pre+"a01.a0100 in ("
							+ aa.toString() + ")");
					/*String code = this.userView.getUnit_id();
					if (code == null|| code.equalsIgnoreCase("UN")|| code.equals("")){
						privSQL.append(" )");
					}
					else if (code.length() == 3){
						privSQL.append(" )");
					}
					else {
						String[] arr = code.split("`");
						StringBuffer temp = new StringBuffer("");
						for (int j = 0; j < arr.length; j++) {
							if (arr[j] == null
									|| arr[j].equals(""))
								continue;
							String codeset = arr[j].substring(
									0, 2);
							String value = arr[j].substring(2);
							temp.append(" or ");
							if (codeset.equalsIgnoreCase("UN"))
								temp.append(" b0110 ");
							else
								temp.append(" e0122 ");
							temp.append(" like '" + value
									+ "%'");
						}
						privSQL.append(" or ("+ temp.toString().substring(3)+"))") ;
					}*/
					privSQL.append(" )");
					buf.append(privSQL);
				
	  	    	}
	  	    	buf.append(" UNION ");
	 	    }
	  	    buf.setLength(buf.length()-7);
	  	    buf.append(" order by dbpre desc,a0000");
			return buf.toString();
		} 

}
