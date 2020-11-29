/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * <p>Title:CopyCellDataTrans</p>
 * <p>Description:按姓名进行查询</p> 
 * <p>Company:hjsj</p> 
 * create time at:Dec 11, 20069:55:18 AM
 * @author chenmengqing
 * @version 4.0
 */
public class QueryDataByA0101Trans extends IBusiness {
	private String nflag="";//临时变量所在的模块
	private String templetid="";//临时变量所在的模板
	private String isfilter_select ="";//人事异动是否按检索条件

	/*
	 *  //wlh修改添加检索过滤
	 */
	private String getFilterSQL(UserView uv,String BasePre,ArrayList alUsedFields,Connection conn1,String filter_factor)
	{
		String sql=" (1=1)";
		try{
			filter_factor=filter_factor.replaceAll("@","\"");
			ContentDAO dao=new ContentDAO(conn1);
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( uv ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;							
			yp.run_Where(filter_factor, ymc,"","hrpwarn_resulta", dao, whereIN,conn1,"A", null);
			String tempTableName = yp.getTempTableName();			
			sql=yp.getSQL();
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	private String getQueryString(String a0101,ArrayList dblist,String filter_factor,String isPriv,String onlyname,String pinyin_field)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
    	ArrayList fieldlist=new ArrayList();
		String strWhere=null;    
		String sexpr="1";
		String sfactor="A0101="+a0101+"*`";
		/**加权限过滤*/
		ArrayList alUsedFields=null;
		if(this.nflag!=null&& "0".equals(this.nflag)&&isfilter_select!=null&& "0".equals(isfilter_select)){//人事异动不按检索条件查人
			filter_factor="";
		}
		if(filter_factor!=null && !"undefined".equalsIgnoreCase(filter_factor)&& !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
	    { 
		  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		  /**
		   *保持和以前的程序兼容，因为先前单位编码和职位编码、
		   *单位名称和职位名称未统一起来 
		   */
		  FieldItem item=new FieldItem();
		  item.setItemid("b0110");
		  item.setCodesetid("UN");
		  item.setItemdesc("单位编码");
		  item.setItemtype("A");
		  item.setFieldsetid("A01");
		  item.setUseflag("2");
		  item.setItemlength(30);
		  item.setItemlength(30);
		  alUsedFields.add(item);

		  item=new FieldItem();
		  item.setItemid("e01a1");
		  item.setCodesetid("@K");
		  item.setItemdesc("职位编码");
		  item.setItemtype("A");
		  item.setFieldsetid("A01");
		  item.setUseflag("2");
		  item.setItemlength(30);
		  item.setItemlength(30);
		  alUsedFields.add(item);
		  
	    }
		DbNameBo dbnamebo=new DbNameBo(this.getFrameconn());
		
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
			
			
			if(this.nflag!=null&& "0".equals(this.nflag)&&isfilter_select!=null&& "1".equals(isfilter_select))
			{
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(this.templetid),this.userView);
				String no_priv_ctrl=tablebo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
				if("1".equals(no_priv_ctrl))
					isPriv="0";
			}
			
			if("1".equals(isPriv)&&!this.userView.isSuper_admin())
  	    	{
  	        	//权限控制，管理范围和操作单位
				StringBuffer privSQL = new StringBuffer();

				String priStrSql = "";
				String modeType=(String)this.getFormHM().get("modeType");
	            if(!"23".equals(modeType)){  //考勤业务   业务模板关联了考勤申请单，模板选人时需按考勤业务范围控制 liuzy 20151125
					priStrSql = InfoUtils.getWhereINSql(this.userView, pre);
				}else{
					priStrSql = RegisterInitInfoData.getWhereINSql(this.userView,pre);
				}
				
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
  	    	//wlh修改添加检索过滤
     	    if(filter_factor!=null && !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
			{ 
     	    	if(this.nflag!=null&& "0".equals(this.nflag)&&isfilter_select!=null&& "1".equals(isfilter_select)){//人事异动按检索条件查人
     	    		buf.append(" and " + dbnamebo.getComplexCondByRSYD(this.userView, pre, alUsedFields, filter_factor, IParserConstant.forPerson,this.templetid)/*getFilterSQL(userView,vo.getString("pre"),alUsedFields,this.getFrameconn(),filter_factor)*/);
     			}else{
     	    	buf.append(" and " + dbnamebo.getComplexCond(this.userView, pre, alUsedFields, filter_factor, IParserConstant.forPerson)/*getFilterSQL(userView,vo.getString("pre"),alUsedFields,this.getFrameconn(),filter_factor)*/);
     			}
			}
  	    	buf.append(" UNION ");
 	    }
  	    buf.setLength(buf.length()-7);
  	    buf.append(" order by dbpre desc,a0000");
		return buf.toString();
	}
	
	public void execute() throws GeneralException {
		String a0101=(String)this.getFormHM().get("a0101");
		String templateid=(String)this.getFormHM().get("templateid");
		String infor_type=(String)this.getFormHM().get("infor_type");
		this.nflag=(String)this.getFormHM().get("nflag");
		this.isfilter_select =(String)this.getFormHM().get("isfilter_select");
		this.templetid = templateid;
		if(templateid==null)
			templateid="";
		if(infor_type==null)
			infor_type="1";
		/**用于区分不同模块，不同的控制方式，=0是正常的，=2代表特殊的招聘模块的，只找除招聘库外的所有登录用户库的人*/
		String dbtype=(String)this.getFormHM().get("dbtype");
		dbtype=dbtype==null?"0":dbtype;
		a0101=SafeCode.decode(a0101);
		int maxlength=0;
		ArrayList objlist=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer str_value=new StringBuffer("");
			if("1".equals(infor_type))
			{
			     boolean bQueryAllDb=true;//查询全部库
	            String modeType=(String)this.getFormHM().get("modeType");
	            if("23".equals(modeType)) //考勤业务
	                 bQueryAllDb=false;
			         
			       
				String filter_factor=(String)this.getFormHM().get("filter_factor");
				if(filter_factor!=null&&filter_factor.trim().length()>0)
					filter_factor=SafeCode.decode(filter_factor);
				/**是否显示部门=0不显示=1显示*/
				String isVisibleUM="0";
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
				/**lzw 是否加权限控制：=1加=0不加*/
				String isPriv="1";
				if(this.getFormHM().get("isPriv")!=null)
				{
					isPriv=(String)this.getFormHM().get("isPriv");
					this.getFormHM().remove("isPriv");
				}
				DbNameBo dbbo=new DbNameBo(this.getFrameconn());
				ArrayList dblist=dbbo.getAllDbNameVoList(this.userView);
				if("2".equals(dbtype))
				{		
	    			ArrayList logdblist=dbbo.getAllLoginDbNameList();
	    			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
	    			String dbname="";
	    			if(vo!=null)
	    				dbname=vo.getString("str_value");
	    			ArrayList list = new ArrayList();
	    			for(int i=0;i<logdblist.size();i++)
	    			{
	    				RecordVo avo=(RecordVo)logdblist.get(i);
	    				if(avo.getString("pre").equalsIgnoreCase(dbname))
	    		    		continue;
	    				list.add(avo);
	    			}
	    			dblist=list;
				}
				
				if (!bQueryAllDb){	
			        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
			        ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
			       
			        ArrayList _templist=new ArrayList();
			        for (int i = 0; i < kq_dbase_list.size(); i++) {
                        String kq_nbase = kq_dbase_list.get(i).toString();                        
                        for(int j=0;j<dblist.size();j++)
                        {
                            RecordVo _vo=(RecordVo)dblist.get(j);
                            String pre=_vo.getString("pre");
                            
                            if(pre.equalsIgnoreCase(kq_nbase))
                            {
                                _templist.add(_vo);
                                break;
                            }
                        }   
                    } 
                    dblist=_templist;
				}
//					System.out.println("-------"+a0101);
					
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
					String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
					String valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
					String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
					if("0".equals(valid))
						onlyname="";
					
					int i=0; 
					if(!(a0101==null|| "".equalsIgnoreCase(a0101)))
					{
						String _onlyname="";
						FieldItem item = DataDictionary.getFieldItem(onlyname);
						if(item!=null&&!"0".equals(item.getUseflag())){
						if(onlyname!=null&&onlyname.trim().length()>0)
							_onlyname=","+onlyname;
						}
						else {
						    onlyname=""; 
						}
						RowSet rset=dao.search(getQueryString(a0101,dblist,filter_factor,isPriv,_onlyname,pinyin_field));	
						String str = ""; //xyy记录上一个人员的人员库
						while(rset.next())
						{
							if(i>40)
								break;
							CommonData objvo=new CommonData();
							String b0110=rset.getString("b0110");
							String name="";
							/**当显示唯一性指标时,只显示部门,要不太长了,看不见了*/
							if(onlyname==null||onlyname.trim().length()==0)
							{
								name=AdminCode.getCodeName("UN",b0110);
							}
							
							str_value.append("`");
							if(b0110!=null&&b0110.trim().length()>0)
								str_value.append(AdminCode.getCodeName("UN",b0110));
							
							
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
							    objlist.add(new CommonData(rset.getString("dbpre"),"---------【"+AdminCode.getCodeName("@@", rset.getString("dbpre"))+"】---------"));
							    
							}
							if(onlyname!=null&&onlyname.trim().length()>0)
							{
								String only_value=rset.getString(onlyname);
								if(only_value==null)
									only_value="";
								objvoName=name+"/"+rset.getString("a0101")+"("+only_value+")";
								if(only_value.length()>maxlength)
									maxlength=only_value.length();
							}
							else
							{
								if(name.length()>maxlength)
									maxlength=name.length();
								objvoName=AdminCode.getCodeName("@@", rset.getString("dbpre"))+" "+name+"("+rset.getString("a0101")+")";//汉字显示的时候人员库也显示汉字，lzw 2010-11-11
							}
							
							objvo.setDataName(objvoName.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
							objvo.setDataValue(rset.getString("dbpre")+rset.getString("a0100"));
							
							str_value.append("~"+rset.getString("dbpre")+rset.getString("a0100"));
							objlist.add(objvo);
							str = rset.getString("dbpre");
							++i;
						}
					}
				}
				else if("2".equals(infor_type)|| "3".equals(infor_type))
				{
					String code=this.userView.getManagePrivCode();
					String codevalue=this.userView.getManagePrivCodeValue();
					String sql="select * from organization where codeitemdesc like '%"+a0101+"%' ";
					if(!this.userView.isSuper_admin())
					{
						if(code!=null&&!"".equals(code))
						{
							sql+=" and codeitemid like '"+codevalue+"%'";
						}
						else
						{
							sql+=" and 1=2";
						}
					}
			
					Calendar d=Calendar.getInstance();
					int yy=d.get(Calendar.YEAR);
					int mm=d.get(Calendar.MONTH)+1;
					int dd=d.get(Calendar.DATE); 
					sql+=" and ( "+Sql_switcher.year("end_date")+">"+yy;
					sql+=" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ";
					sql+=" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ";
					
					sql+=" and ( "+Sql_switcher.year("start_date")+"<"+yy;
					sql+=" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ";
					sql+=" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ";
				
					
					if("2".equals(infor_type))
						sql+=" and ( codesetid='UM' or codesetid='UN' ) ";
					else if("3".equals(infor_type))
						sql+=" and codesetid='@K' ";
					sql+=" order by a0000";
					this.frowset=dao.search(sql);
					while(this.frowset.next())
					{
						String tempparentid = this.frowset.getString("parentid");
						if(tempparentid==null)
							tempparentid="";
						String parentid=tempparentid;
						String codeitemid=this.frowset.getString("codeitemid");
						String tempname = AdminCode.getCodeName("UM", tempparentid);
						str_value.append("`");
						
						if(tempname==null || "".equals(tempname)){
							tempname = AdminCode.getCodeName("UN", tempparentid);
							if(!codeitemid.equalsIgnoreCase(tempparentid))
								str_value.append(tempname);
						}
						else //部门
						{
							if(tempparentid.length()>0)
							{ 
								while(tempparentid.length()>0)
								{
									tempparentid=tempparentid.substring(0,(tempparentid.length()-1));
									if(AdminCode.getCodeName("UN", tempparentid)!=null&&AdminCode.getCodeName("UN", tempparentid).trim().length()>0)
									{
										str_value.append(AdminCode.getCodeName("UN", tempparentid));
										break;
									}
							
								}
							}
							str_value.append("/"+tempname);
						}
						if(codeitemid.equalsIgnoreCase(parentid))
							tempname="";
						
						String desc=this.frowset.getString("codeitemdesc").replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"");
						if(tempname!=null && !"".equals(tempname)){
							tempname = "/"+tempname+"/";
							str_value.append("/"+desc);
						}else{
							tempname="";
							str_value.append(desc);
						}
						str_value.append("~"+this.frowset.getString("codeitemid"));
						String name=tempname+this.frowset.getString("codeitemdesc").replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")+"("+this.frowset.getString("codeitemid")+")";
						String value=this.frowset.getString("codeitemid");
						if(name.length()>maxlength)
							maxlength=name.length();
						CommonData objvo=new CommonData(value,name);
						objlist.add(objvo);
					}
				}
				if(str_value.length()>0)
					this.getFormHM().put("str_value",str_value.substring(1));
				else
					this.getFormHM().put("str_value","");
				this.getFormHM().put("objlist",objlist);
				this.getFormHM().put("maxlength", ""+maxlength);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	public String getNflag() {
		return nflag;
	}
	public void setNflag(String nflag) {
		this.nflag = nflag;
	}
	public String getTempletid() {
		return templetid;
	}
	public void setTempletid(String templetid) {
		this.templetid = templetid;
	}

}
