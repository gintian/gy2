package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KqEmpMonthDataBo {
	private Connection conn;
	private UserView userView;
	
	public KqEmpMonthDataBo() {
		super();
	}
	
	public KqEmpMonthDataBo(Connection conn, UserView userView) {
		super();
		this.conn = conn;
		this.userView = userView;
	}
	
	public HashMap auditEmpMonthData(String chkid,String code,String kind,ArrayList dblist) throws GeneralException{
		HashMap returnMap = new HashMap();
		String checkinfor="";
		boolean flag = true;
		ArrayList formulaList = this.getFormulaList();
		if (formulaList.size() == 0) {
			returnMap.put("msg", "0");
			return returnMap;
		}
		
		try {
			SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn);
			ArrayList midVariableList;
			ArrayList varlist = new ArrayList();
			midVariableList = this.getKqMidVariableList(formulaList);
			varlist.addAll(midVariableList);
			
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = null;
			
			StringBuffer sql = new StringBuffer();
			ResultSet rs = null;
			ContentDAO dao = new ContentDAO(conn);
			
			for (int i = 0; i < formulaList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
				String formula=(String)bean.get("formula");
				String formulaname=(String)bean.get("name");
	            String information=(String)bean.get("information");
				if(formula==null|| "".equals(formula)) {
                    continue;
                }
				HSSFRow row=null;
				HSSFCell csCell=null;
				HSSFCellStyle titlestyle = salaryTemplateBo.style(workbook,0);
				HSSFCellStyle centerstyle = salaryTemplateBo.style(workbook,1);
				HSSFCellStyle cloumnstyle=salaryTemplateBo.style(workbook,2);
				HSSFCellStyle bordernone=salaryTemplateBo.style(workbook,3);
				HSSFCellStyle bordertop=salaryTemplateBo.style(workbook,4);
				centerstyle.setWrapText(true);
				short rows=0;
			
				YksjParser yp=null;
				int x=1;
				int y=0;
	
				for(int j=0;j<dblist.size();j++)
				{
					String nbase = (String)dblist.get(j);
					yp = new YksjParser(this.userView ,varlist,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "", "");
					yp.setCon(conn);
					boolean b = yp.Verify_where(formula.trim());
					if (!b) {
						checkinfor =formulaname+ResourceFactory.getProperty("workdiary.message.review.failure")+"!\n\n";
						checkinfor += yp.getStrError();
						throw GeneralExceptionHandler.Handle(new Exception(checkinfor));
					} 
					yp.setVerify(false);
					yp.run(formula.trim());
					String wherestr = yp.getSQL();//公式的结果
					
					sql.append(this.getSQL(code,kind,nbase,wherestr));
					try{
					rs=dao.search(sql.toString());
					}catch(SQLException e){
						e.printStackTrace();
						throw new SQLException(formulaname+"公式定义错误！");
					}
					while(rs.next())
					{
						if(y==0)
						{
							sheet = workbook.createSheet((i+1)+"");
							row=sheet.createRow(rows);
							csCell =row.createCell((short)0);
							HSSFRichTextString  titlecontext = new HSSFRichTextString(ResourceFactory.getProperty("label.gz.shresult"));
							csCell.setCellStyle(titlestyle);
							csCell.setCellValue(titlecontext);
							ExportExcelUtil.mergeCell(sheet, 0, (short)0,0, (short)3);
							rows++;
							row=sheet.createRow(rows);
							HSSFRichTextString  context = new HSSFRichTextString((i+1)+":"+formulaname);
							csCell =row.createCell((short)0);
							csCell.setCellValue(context);
							csCell.setCellStyle(bordernone);
							ExportExcelUtil.mergeCell(sheet, rows, (short)0,rows, (short)3);
							rows++;
							row=sheet.createRow(rows);
							HSSFRichTextString  text = new HSSFRichTextString(ResourceFactory.getProperty("workdiary.message.message")+"："+information);
							csCell =row.createCell((short)0);
							csCell.setCellValue(text);
							csCell.setCellStyle(bordertop);
							ExportExcelUtil.mergeCell(sheet, rows, (short)0,rows, (short)3);
							rows++;
							row=sheet.createRow(rows);
							HSSFRichTextString  one = new HSSFRichTextString(ResourceFactory.getProperty("gz.bankdisk.sequencenumber"));
							csCell =row.createCell((short)0);
							csCell.setCellStyle(cloumnstyle);
							csCell.setCellValue(one);
							HSSFRichTextString  two = new HSSFRichTextString(ResourceFactory.getProperty("lable.hiremanage.org_id"));
							csCell =row.createCell((short)1);
							csCell.setCellStyle(cloumnstyle);
							csCell.setCellValue(two);
							HSSFRichTextString  three = new HSSFRichTextString(ResourceFactory.getProperty("lable.hiremanage.dept_id"));
							csCell =row.createCell((short)2);
							csCell.setCellStyle(cloumnstyle);
							csCell.setCellValue(three);
							HSSFRichTextString  four = new HSSFRichTextString(ResourceFactory.getProperty("label.title.name"));
							csCell =row.createCell((short)3);
							csCell.setCellStyle(cloumnstyle);
							csCell.setCellValue(four);
							rows++;
						}
						flag = false;
						row=sheet.createRow(rows);
						HSSFRichTextString  on = new HSSFRichTextString(x+"");
						csCell =row.createCell((short)0);
						csCell.setCellStyle(centerstyle);
						csCell.setCellValue(on);
						HSSFRichTextString  tw = new HSSFRichTextString(AdminCode.getCodeName("UN", rs.getString("b0110")==null?"":rs.getString("b0110")));
						csCell =row.createCell((short)1);
						csCell.setCellStyle(centerstyle);
						csCell.setCellValue(tw);
						HSSFRichTextString  th = new HSSFRichTextString(AdminCode.getCodeName("UM", rs.getString("e0122")==null?"":rs.getString("e0122")));
						csCell =row.createCell((short)2);
						csCell.setCellStyle(centerstyle);
						csCell.setCellValue(th);
						HSSFRichTextString  fou = new HSSFRichTextString(rs.getString("a0101")==null?"":rs.getString("a0101"));
						csCell =row.createCell((short)3);
						csCell.setCellStyle(centerstyle);
						csCell.setCellValue(fou);
						rows++;
						x++;
						y++;
					}
					sql.setLength(0);
				}
				if(sheet!=null)
				{
		    		for (int j = 0; j <=3; j++)
		    		{
		    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)6000);
		    		}
		    		for (int j = 0; j <=rows; j++)
		    		{
		    			row=sheet.getRow(j);
		    			if(row==null) {
                            row = sheet.createRow(j);
                        }
		    		    row.setHeight((short) 400);
		    		}
		     	}
			}
			String outName = "";
			outName = "kq_" + PubFunc.getStrg();
			outName += this.userView.getUserName() + ".xls";
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+
					System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			returnMap.put("fileName", SafeCode.encode(PubFunc.encrypt(outName)));
			if(flag)
			{
				returnMap.put("msg", "no");
			}
			else
			{
				returnMap.put("msg", "yes");
			}
			if(rs != null) {
                rs.close();
            }
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
		     throw GeneralExceptionHandler.Handle(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return returnMap;
	}
	
	/**
	 * 获取考勤月汇总审核公式
	 * @return
	 */
	public ArrayList getFormulaList(){
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from hrpchkformula where validflag='1' and flag='5'");
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name"));
				bean.set("validflag", rs.getString("validflag"));
				bean.set("formula", Sql_switcher.readMemo(rs,"formula"));
				bean.set("information",Sql_switcher.readMemo(rs, "information"));
				list.add(bean);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	public ArrayList getKqMidVariableList(ArrayList formulaList) throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		fieldlist = DataDictionary.getFieldList("Q03", com.hrms.hjsj.sys.Constant.USED_FIELD_SET);
		
		//过滤月汇总 计算公式用不到的临时变量
		FieldItem item=null;
		HashMap map=new HashMap();
		for(int i=0;i<formulaList.size();i++)
		{
			  LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
			  String formula=((String)bean.get("formula")).toLowerCase();
			  if(formula==null|| "".equals(formula)) {
                  continue;
              }
              for(int j=0;j<fieldlist.size();j++)
              {
            	  item=(FieldItem)fieldlist.get(j);
            	  String item_id=item.getItemid().toLowerCase();
            	  String item_desc=item.getItemdesc().trim().toLowerCase();
            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)
            	  {
            		  new_fieldList.add(item);
            		  map.put(item_id, "1");
            	  }
            		  
              }
		}
		return new_fieldList;
	}
	
	private String getSQL(String code,String kind,String nbase,String wherestr){
		StringBuffer sql = new StringBuffer();
		try {
			String kq_duration = "";
			kq_duration =RegisterDate.getKqDuration(this.conn);
			String field = KqParam.getInstance().getKqDepartment();//考勤部门
			StringBuffer whereIN = new StringBuffer();
			// 37218 linbz 该人员高级权限 条件  始终是要有的，其它都只是在权限的基础上再一层限制
			whereIN.append(RegisterInitInfoData.getWhereINSql(userView,nbase));
			// 44004 审核公式人员条件拼接SQL错误
			boolean isWhere = false;
			if(!userView.isSuper_admin()) {
                if(whereIN!=null && (whereIN.toString().indexOf("WHERE")!=-1 || whereIN.toString().indexOf("where")!=-1)) {
                    isWhere = true;
                }
            }
			if (StringUtils.isNotEmpty(kind)) {
				if(!isWhere) {
                    whereIN.append(" where 1=1 ");
                }
				if ("2".equals(kind)) 
				{
					whereIN.append(" and b0110 like '" + code + "%'");
				}else if ("1".equals(kind)) 
				{
					whereIN.append(" and e0122 like '" + code + "%'");
				}else if ("0".equals(kind)) 
				{
					whereIN.append(" and e01a1 like '" + code + "%'");
				}else 
				{
					whereIN.append(" and a0100 = '" + code + "'");
				}
				isWhere = true;
			}
			
			if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(code)) {
				if(isWhere) {
                    whereIN.append(" or ");
                }
				whereIN.append(field + " like '" + code + "%'");
			}
			
			sql.append("select a0101,b0110,e0122 ");
			sql.append("from Q05 Q");
			sql.append(" where Q03z0 ='" + kq_duration + "' ");
			sql.append(" and nbase = '" + nbase + "'");
			sql.append(" and (" + wherestr + ")");
			sql.append(" and a0100 in (");
			sql.append("select a0100 " + whereIN + ")");
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return sql.toString();
	}
	
	public String getFormula()
	{
		StringBuffer buf = new StringBuffer("");
		String sql = "select chkid from hrpchkformula where validflag = 1 and flag = 5";
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		try
		{
			
			rs = dao.search(sql);
			while(rs.next())
			{
				buf.append("," + rs.getString("chkid"));
			}
			if(buf.toString().trim().length()>0)
			{
				String xx=buf.toString().substring(1);
				buf.setLength(0);
				buf.append(xx);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return buf.toString();
	}
		
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
}
