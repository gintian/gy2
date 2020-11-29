package com.hjsj.hrms.module.template.templatetoolbar.updown;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * @Title: TemplateUpLoadTrans.java
 * @Package com.hjsj.hrms.module.template.templatetoolbar.updown
 * @Description: 人事异动-上传数据
 * @author gaohy
 * @date 2016-1-15 下午03:19:05
 * @version V7x
 */
public class TemplateUpLoadTrans extends IBusiness {
    /* (non-Javadoc)
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    @Override
    public void execute() throws GeneralException {
        String tabid = (String) getFormHM().get("tabid");
        String fileid = (String) getFormHM().get("fileid");// 导入的文件
        String filename=(String)getFormHM().get("filename");
        filename = PubFunc.decrypt(filename);
        //fileid = PubFunc.decrypt(fileid);
        String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator"); // 文件路径
        InputStream inputStream=null;
        try {
        	inputStream = VfsService.getFile(fileid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        int num = 0;
        String ins_id = (String) this.userView.getHm().get("ins_id");// 流程实例号，进入流程之前是0
        ins_id = ins_id != null && ins_id.length() > 0 ? ins_id : "0";
        String table_name = this.userView.getUserName() + "templet_" + tabid;
        if (!"0".equalsIgnoreCase(ins_id)) {
            table_name = "templet_" + tabid;
        }
        TemplateDataBo dataBo = new TemplateDataBo(this.frameconn, this.userView, Integer.parseInt(tabid));
        ArrayList filedUpdateList=new ArrayList();
        //导入时分析关联指标，用户导入判断关联指标是否级联正确
		TemplateParam paramBo = dataBo.getParamBo();
		ArrayList allTemplateItem = dataBo.getAllTemplateItem(true);
        Boolean isHaveMainImpeople=false;
        Boolean isHaveChildImpeople=false;
        ArrayList mainImpeopleList=new ArrayList();
        ArrayList childImpeopleList=new ArrayList();
        HashMap subFatherMap=new HashMap();
        HashMap mainFatherMap=new HashMap();
        HashMap relationFieldMap=new HashMap();
        for(int itemNum=0;itemNum<allTemplateItem.size();itemNum++){
        	TemplateItem item = (TemplateItem) allTemplateItem.get(itemNum);
        	TemplateSet cellBo = item.getCellBo();
        	if("true".equalsIgnoreCase(cellBo.getImppeople())&&!cellBo.isSubflag()){
        		isHaveMainImpeople=true;
        		String id="";
        		if(StringUtils.isNotBlank(cellBo.getSub_domain_id())){
        			id="_"+cellBo.getSub_domain_id();
        		}
        		mainImpeopleList.add((cellBo.getField_name()+id+"_"+cellBo.getChgstate()).toLowerCase());
        	}
        	if(cellBo.isSubflag()){
        		ArrayList subFieldList = cellBo.getSubFieldList();
        		for(int childNum=0;childNum<subFieldList.size();childNum++){
        			SubField subField = (SubField) subFieldList.get(childNum);
        			if("true".equalsIgnoreCase(subField.getImppeople())){
        				isHaveChildImpeople=true;
        				childImpeopleList.add(subField.getFieldItem().getItemid().toLowerCase());
        			}
        		}
        	}
        	if(StringUtils.isNotBlank(cellBo.getRelation_field())&&!cellBo.isSubflag()){
        		String id=cellBo.getSub_domain_id();
        		if(StringUtils.isBlank(id)){
        			id="";
        		}else{
        			id="_"+id;
        		}
        		relationFieldMap.put(cellBo.getUniqueId(), cellBo.getRelation_field()+":"+(cellBo.getField_name()+id+"_"+cellBo.getChgstate()).toLowerCase());
        	}
        	if(cellBo.isSubflag()){
        		ArrayList subFieldList = cellBo.getSubFieldList();
        		for(int childNum=0;childNum<subFieldList.size();childNum++){
        			SubField subField = (SubField) subFieldList.get(childNum);
        			if(StringUtils.isNotBlank(subField.getRelation_field())){
        				subFatherMap.put(subField.getFieldname().toLowerCase(), subField.getRelation_field().toLowerCase());
        			}
        		}
        	}
        }
        if(relationFieldMap.size()>0){
			Iterator iterator = relationFieldMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry entry=(Entry)	iterator.next();
				String relationField = (String)entry.getValue();
				String[] split = relationField.split(":");
				relationField=split[0];
				String fieldIdChg=split[1];
				String uniqueId=(String)entry.getKey();
				String fatherRelationUniqueId="";
				for(int i=0;i<allTemplateItem.size();i++){
					TemplateItem item = (TemplateItem) allTemplateItem.get(i);
		        	TemplateSet cellBo = item.getCellBo();
					String fieldUniqueId = cellBo.getUniqueId();
					String fieldfldName = cellBo.getField_name();
					int pageId = cellBo.getPageId();//bug 人事异动，两指标做关联，导入模板数据的时候没有控制 
					int gridNo = cellBo.getGridno();
					if((!uniqueId.equalsIgnoreCase(fieldUniqueId))&&relationField.equalsIgnoreCase(pageId+"_"+gridNo)){
						String id=cellBo.getSub_domain_id();
						if(StringUtils.isNotBlank(id)){
							id="_"+id;
						}else{
							id="";
						}
						mainFatherMap.put(fieldIdChg.toLowerCase(), (cellBo.getField_name()+id+"_"+cellBo.getChgstate()).toLowerCase());
					}
				}
			}
        }
        String operationtype = Integer.toString(dataBo.getParamBo().getOperationType());// 获取操作类型，并且转为String
        TemplateUpLoadBo bo = new TemplateUpLoadBo(this.getFormHM().get("tabid").toString(), this.getFrameconn(), this.userView);
        bo.setIsMobile(0);//liuyz 导入过滤手机页
        ArrayList allList = bo.getAllCells();//获取全部页面上的指标
        ArrayList templateSetList = (ArrayList)allList.get(0);//获取打印页的指标
        ArrayList noprnList = (ArrayList)allList.get(1);//获取此页不打印页的指标
        String existtarget = ",";
        for (int i = 0; i < templateSetList.size(); i++) {
            LazyDynaBean bean = (LazyDynaBean) templateSetList.get(i);
            if ("0".equals(bean.get("isvar")))
                existtarget += bean.get("field_name") + "_" + bean.get("chgstate") + ",";
            else
                existtarget += bean.get("field_name") + ",";
        }
        int infor_type = bo.getBo().getInfor_type();
        // String in = (String)getFormHM().get("infor_type");//可以得到
        Boolean isSettingOnlyName=false;
        String onlyname = "";
        String onlyKey="";//系统唯一性指标
        String valid = "";
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
        valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
        if ("0".equals(valid)) {
        	isSettingOnlyName = false;
        }else{
        	FieldItem item = DataDictionary.getFieldItem(onlyname);
			if (item!=null){
				isSettingOnlyName = true;
				onlyKey=onlyname;
			}
			else {
				isSettingOnlyName = false;
			}
        }
        if (infor_type == 1) {// 对人员处理的业务模板
            if ("0".equals(valid)) {
                onlyname = "no";
            }
        } else if (infor_type == 2) {
            RecordVo unit_code_field_constant_vo = ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD", this.getFrameconn());
            if (unit_code_field_constant_vo != null) {
                onlyname = unit_code_field_constant_vo.getString("str_value");
            }
        } else if (infor_type == 3) {
            RecordVo pos_code_field_constant_vo = ConstantParamter.getRealConstantVo("POS_CODE_FIELD", this.getFrameconn());
            if (pos_code_field_constant_vo != null) {
                onlyname = pos_code_field_constant_vo.getString("str_value");
            }
        }
		//循环不打印页上的指标，看是否存在唯一性指标。
        for (int i = 0; i < noprnList.size(); i++) {
            LazyDynaBean bean = (LazyDynaBean) noprnList.get(i);
            if(onlyname.equalsIgnoreCase(String.valueOf(bean.get("field_name")))){
	            if ("0".equals(bean.get("isvar")))
	                existtarget += bean.get("field_name") + "_" + bean.get("chgstate") + ",";
	            else
	                existtarget += bean.get("field_name") + ",";
            }
        }
        StringBuffer sql = new StringBuffer();
        sql.append("update " + table_name + " set ");
        StringBuffer insertsql = new StringBuffer();
        StringBuffer tempinsertstr = new StringBuffer();
        insertsql.append("insert into " + table_name + "(  ");
        int updateFidsCount = 0;// 将要更新的字段数目
        // HSSFWorkbook wb = null;
        // HSSFSheet sheet = null;
        Workbook wb = null;
        Sheet sheet = null;
        StringBuffer errorStr = new StringBuffer();
        TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(), Integer.parseInt(tabid), this.userView);
        HashMap cell_param_map = tablebo.getModeCell4();
        //InputStream inputStream = null;
        try {
          /*  boolean isFileTypeEqual = FileTypeUtil.isFileTypeEqual(form_file);
            if (!isFileTypeEqual) {
                throw new GeneralException(ResourceFactory.getProperty("error.fileuploaderror"));
            }*/
            String onlyflag = "0";// 0表示原有逻辑，1表示唯一指标
            String fieldset_2 = "";// 变化后子集
            HashMap fieldsetmap = new HashMap();
            String cname = "";// 模板名称
            String excelfields = "";
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search("select name from template_table where tabid=" + tabid);
            if (this.frowset.next())
                cname = this.frowset.getString("name");
            cname = cname.replace("\\", "").replace("/", "").replaceAll("\\)", "）").replaceAll("\\(", "（").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "");
            //inputStream = new FileInputStream(form_file);
            wb = WorkbookFactory.create(inputStream);
            int sheetsnum = wb.getNumberOfSheets();
            HashMap sheetnameMap = new HashMap();// key:sheet名字，value：sheet的位置
            ArrayList sheetlist = new ArrayList();
            sheetlist.add(cname);
            String tasklist_str = (String) this.getFormHM().get("task_id");
            tasklist_str = tasklist_str != null ? tasklist_str : "0";
            ArrayList tasklist = new ArrayList();
            if (tasklist_str.length() > 0) {
                String[] temp = tasklist_str.split(",");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i] == null || temp[i].length() == 0)
                        continue;
                    tasklist.add(temp[i]);

                }
            }
            HashMap fieldPrivByNode = new HashMap();
            if (tasklist.size() > 0) {
                fieldPrivByNode = tablebo.getFieldPriv((String) tasklist.get(0), this.getFrameconn());
            }

            for (int i = 0; i < sheetsnum; i++) {
                String sheetname = wb.getSheetName(i);
                if (sheetname.indexOf("(t_") != -1 && sheetname.endsWith("_2)")) {
                    sheetname = sheetname.substring(sheetname.indexOf("(t_")+3, sheetname.indexOf("_2)"));
                    sheetnameMap.put(sheetname, "" + i);
                    sheetlist.add(sheetname);
                } else {
                    if (sheetname.equals(cname)) {
                        sheetnameMap.put(sheetname, "" + i);
                    }
                }

            }
            int onlynamesit = 0;
            int onlynamesit1 = 0;
            int onlynamesit2 = 0;
            boolean select = false;
            // wb.getSheetName(arg0);//通过位置获得sheet名称
            // wb.getSheet(arg0);//通过sheet名称获得sheet
            if (sheetnameMap == null || sheetnameMap.get(cname) == null) {
                throw new GeneralException("导入的excel模板找不到对应的" + cname + "数据页！");
            } else {
                sheet = wb.getSheetAt(Integer.parseInt("" + sheetnameMap.get(cname)));
                Row row = sheet.getRow(0);
                if (row == null)
                    throw new GeneralException("请用导出的模板Excel来导入数据！");
                int cols = row.getPhysicalNumberOfCells();
                int rows = sheet.getPhysicalNumberOfRows();
                if (cols < 1 || rows < 1)
                    throw new GeneralException("请用导出的模板Excel来导入数据！");
                else {
                    for (int i = 0; i < cols; i++) {
                        Cell cell = row.getCell((short) i);
                        if (cell != null) {
                            String field = cell.getCellComment().getString().toString();
                            if (field != null && field.trim().length() > 0) {
                                excelfields += field + ",";
                                if (field.equalsIgnoreCase(onlyname + "_1")) {
                                    onlynamesit1 = i;
                                    select = true;
                                }
                                if (field.equalsIgnoreCase(onlyname + "_2")) {
                                    onlynamesit2 = i;
                                }
                            }
                        }
                    }
                }

            }
            if (select) {
                onlynamesit = onlynamesit1;
            } else
                onlynamesit = onlynamesit2;
            if (onlyname != null && onlyname.trim().length() > 1 && existtarget.toString().toLowerCase().indexOf(onlyname.toLowerCase()) != -1) {// 模板中存在唯一标识
                if (excelfields.toLowerCase().indexOf(onlyname.toLowerCase() + "_1") != -1) {
                    onlyflag = "1";
                } else if (excelfields.toLowerCase().indexOf(onlyname.toLowerCase() + "_2") != -1) {
                    onlyflag = "2";
                } else {
                    onlyflag = "0";
                }
            }

            HashMap onlynameMap = new HashMap();

            String sheetname = "" + sheetlist.get(0);

            sheet = wb.getSheetAt(Integer.parseInt("" + sheetnameMap.get(sheetname)));

            HashMap map = new HashMap();
            HashMap name2map = new HashMap();
            Row row = sheet.getRow(0);
            if (row == null)
                throw new GeneralException("请用导出的模板Excel来导入数据！");
            int cols = row.getPhysicalNumberOfCells();
            int rows = sheet.getPhysicalNumberOfRows();
            StringBuffer a0100s = new StringBuffer();
            StringBuffer codeBuf = new StringBuffer();
            int x = 0;

            HashMap codeColMap = new HashMap();
            String codeSetStr ="";
            HashMap codeLeafItefColMap = new HashMap();//存放控制只选择叶子节点代码类的叶子节点代码
            ArrayList codeLeafSetColList = new ArrayList();//存放控制只选择叶子节点的代码类
            HashMap nameMap = new HashMap();
            if (row != null) {
                boolean errorflag = false;
                if (cols < 1 || rows < 1)
                    errorflag = true;
                else {
                    for (int i = 0; i < 1; i++) {
                        String value = "";
                        Cell cell = row.getCell((short) i);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_FORMULA :
                                    break;
                                case Cell.CELL_TYPE_NUMERIC :
                                    double y = cell.getNumericCellValue();
                                    value = Double.toString(y);
                                    break;
                                case Cell.CELL_TYPE_STRING :
                                    value = cell.getStringCellValue();
                                    break;
                                default :
                                    value = "";
                            }
                        } else {
                            if (!table_name.startsWith("templet") && ("0".equals(operationtype) || "5".equals(operationtype))) {

                            } else {
                                errorflag = true;
                                break;
                            }
                        }

                        if (i == 0 && !"主键标识串".equalsIgnoreCase(value))
                            errorflag = true;
                        if (errorflag)
                            break;
                    }
                }
                if (errorflag && "0".equals(onlyflag))
                    throw new GeneralException("请用导出的模板Excel来导入数据！");

                for (short c = 0; c < cols; c++) {
                    if ("0".equals(onlyflag) && c == 0)
                        continue;
                    Cell cell = row.getCell(c);
                    if (cell != null) {
                        String title = "";
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_FORMULA :
                                break;
                            case Cell.CELL_TYPE_NUMERIC :
                                double y = cell.getNumericCellValue();
                                title = Double.toString(y);
                                break;
                            case Cell.CELL_TYPE_STRING :
                                title = cell.getStringCellValue();
                                break;
                            default :
                                title = "";
                        }
                        String field = cell.getCellComment().getString().toString();
                        if ("".equals(field.trim()))
                            throw new GeneralException("标题行存在空批注！请用导出的模板Excel来导入数据！");
                        if ("i9999".equalsIgnoreCase(field))// i9999跟排序有关
                            continue;
                        if ("主键标识串".equalsIgnoreCase(field))
                            continue;
                        if ("".equals(title.trim()))
                            throw new GeneralException("标题行存在空标题！请用导出的模板Excel来导入数据！");

                        String tempfield = "";
                        tempfield = field;

                        if (field.indexOf("_") != -1) {
                            tempfield = field.substring(0, field.lastIndexOf("_"));
                        }

                        String itemtype = "";
                        String codesetid = "";
                        if (DataDictionary.getFieldItem(tempfield) != null && DataDictionary.getFieldItem(tempfield).getCodesetid().length() > 0) {
                            codesetid = DataDictionary.getFieldItem(tempfield).getCodesetid();
                            itemtype = DataDictionary.getFieldItem(tempfield).getItemtype();
                        } else {
                            codesetid = "0";
                        }

                        // String pri =
                        // this.userView.analyseFieldPriv(tempfield);
                        // if (pri.equals("1") || pri.equals("0")) // 只读或者是没有权限
                        // continue;

                        if (!"0".equals(codesetid)) {
                            if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                            	if(codeSetStr.trim().length()==0){
                            		codeSetStr="'"+codesetid+"'";
                            	}else
                            		codeSetStr=codeSetStr+",'"+codesetid+"'";
                            	codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where upper(codesetid)='" + codesetid + "'   union all ");
                            } else {
                                if ("UN".equalsIgnoreCase(codesetid))
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)='" + codesetid + "' union all ");
                                else if ("UM".equalsIgnoreCase(codesetid))
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)<>'@K' union all ");
                                else
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization   union all ");
                                // 因为导入的时候有可能更新为非叶子机构所以在此放开限制为叶子部门的代码 + "' and
                                // codeitemid not in (select parentid from
                                // organization where codesetid='" + codesetid +
                                // "') union all ");

                            }
                        } else {
                            if ("parentid".equals(tempfield) && infor_type != 1) {
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)<>'@K' union all ");
                            }
                        }
                        // if ("a0100,a0101".indexOf(tempfield.toLowerCase()) ==
                        // -1)//单位 部门 姓名字段不更新b0110,e01a1,e0122,
                        // {
                        name2map.put(new Short(c), field + ":" + cell.getStringCellValue());
                        if (field.indexOf("_1") != -1)
                            continue;
                        if ("codesetid".equalsIgnoreCase(tempfield) || "codeitemdesc".equalsIgnoreCase(tempfield) || "corcode".equalsIgnoreCase(tempfield) || "parentid".equalsIgnoreCase(tempfield) || "start_date".equalsIgnoreCase(tempfield)) {

                        } else {
                            String state = this.userView.analyseFieldPriv(tempfield);
                            if(field.lastIndexOf("_2")!=-1&&tablebo.getUnrestrictedMenuPriv_Input().equals("1")) {
                            	state="2";
                            }
                            String editable = null;
                            if (fieldPrivByNode != null && fieldPrivByNode.get(field.toLowerCase()) != null)
                                editable = (String) fieldPrivByNode.get(field.toLowerCase()); // //0|1|2(无|读|写)
                            if (editable != null)
                                state = editable;

                            if (!this.getUserView().isSuper_admin() && !"2".equalsIgnoreCase(state) && "0".equals(tablebo.getUnrestrictedMenuPriv_Input()))
                                continue; // 无权限的去掉

                            if (tablebo.getOpinion_field() != null && tablebo.getOpinion_field().length() > 0 && tablebo.getOpinion_field().equalsIgnoreCase(tempfield))
                                continue;
                        }
                        if (existtarget.toUpperCase().indexOf("," + field.toUpperCase().trim() + ",") == -1)
                            throw new GeneralException("指标" + field + "不存在！请检查模板数据指标！");
                        // if(itemtype.equals("M"))
                        // continue;//大字段类型单独处理
                        insertsql.append(field + ",");
                        tempinsertstr.append("?,");
                        sql.append(field + "=?,");
                        filedUpdateList.add(field);
                        map.put(new Short(c), field + ":" + cell.getStringCellValue());
                        updateFidsCount++;
                        // }
                    } else
                        continue;
                }
                if (codeBuf.length() > 0) {
                    codeBuf.setLength(codeBuf.length() - " union all ".length());
                    RowSet rs =null;
                    try {
                        rs = dao.search(codeBuf.toString());
                        while (rs.next()) {
                            String codesetid = rs.getString("codesetid");
                            codesetid = StringUtils.isEmpty(codesetid)?"":codesetid;
                            
                            String codeitemid = rs.getString("codeitemid");
                            codeitemid = StringUtils.isEmpty(codeitemid)?"":codeitemid;
                            
                            String codeitemdesc = rs.getString("codeitemdesc");
                            codeitemdesc = StringUtils.isEmpty(codeitemdesc)?"":codeitemdesc;
                            
                            codeColMap.put(codesetid + "a04v2u" + codeitemid + ":" + codeitemdesc.trim(), codeitemid);//liuyz 代码项中前后有空格，在导入数据会去掉空格时造成匹配失败，不能导入。
                            codeColMap.put(codesetid + "a04v2u" + codeitemdesc.trim(), codeitemid);
                            codeColMap.put(codesetid + "a04v2u" + codeitemid, codeitemid);// 考虑手工写入指标代码
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }finally {
						PubFunc.closeDbObj(rs);
					}

                }
                if(codeSetStr.length()>0)
                {
         			String searchCodeset="SELECT  cm.codesetid,cm.codeitemid ,cm.codeitemdesc FROM codeitem cm LEFT JOIN ( SELECT  COUNT(1) AS num ,codesetid,parentid FROM    codeitem WHERE codeitemid<>parentid and  "+Sql_switcher.sqlNow()+" BETWEEN start_date AND end_date   GROUP BY parentid ,codesetid)  cnum ON cm.codesetid = cnum.codesetid AND cm.codeitemid = cnum.parentid left join codeset c on cm.codesetid=c.codesetid WHERE "+Sql_switcher.isnull("cnum.num", "0")+"= 0 and upper(cm.codesetid) in("+ codeSetStr + ") and "+Sql_switcher.isnull("c.leaf_node","0")+"='1'  ";
                	RowSet rs=null;
                    try {
                        rs = dao.search(searchCodeset.toString());
                        while (rs.next()) {
                            String codesetid = rs.getString("codesetid");
                            codesetid = StringUtils.isEmpty(codesetid)?"":codesetid;
                            
                            String codeitemid = rs.getString("codeitemid");
                            codeitemid = StringUtils.isEmpty(codeitemid)?"":codeitemid;
                            
                            String codeitemdesc = rs.getString("codeitemdesc");
                            codeitemdesc = StringUtils.isEmpty(codeitemdesc)?"":codeitemdesc;
                            
                        	if(!codeLeafSetColList.contains(codesetid)) {
                        	    codeLeafSetColList.add(codesetid);
                        	}
                        	codeLeafItefColMap.put(codesetid + "a04v2u" + codeitemid + ":" + codeitemdesc.trim(), codeitemid);//liuyz 代码项中前后有空格，在导入数据会去掉空格时造成匹配失败，不能导入。
                        	codeLeafItefColMap.put(codesetid + "a04v2u" + codeitemdesc.trim(), codeitemid);
                        	codeLeafItefColMap.put(codesetid + "a04v2u" + codeitemid, codeitemid);// 考虑手工写入指标代码
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }finally {
						PubFunc.closeDbObj(rs);
					}
                }
                sql.setLength(sql.length() - 1);
                if ("0".equals(onlyflag)) {
                    if (table_name.startsWith("templet")) {// 审批状态
                        if (bo.getBo() != null && bo.getBo().getInfor_type() == 1) {
                            insertsql.append("BasePre,A0100,ins_id,task_id");
                            tempinsertstr.append("?,?,?,?");
                            sql.append(" where BasePre=? and A0100=? and ins_id=? and task_id=?  ");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 2) {
                            insertsql.append("b0110,ins_id,task_id");
                            tempinsertstr.append("?,?,?");
                            sql.append(" where  b0110=? and ins_id=? and task_id=?  ");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 3) {
                            insertsql.append("e01a1,ins_id,task_id");
                            tempinsertstr.append("?,?,?");
                            sql.append(" where  e01a1=? and ins_id=? and task_id=?  ");
                        } else {
                            insertsql.append("BasePre,A0100,ins_id,task_id");
                            tempinsertstr.append("?,?,?,?");
                            sql.append(" where BasePre=? and A0100=? and ins_id=? and task_id=?  ");
                        }

                    } else {
                        if (bo.getBo() != null && bo.getBo().getInfor_type() == 1) {
                            tempinsertstr.append("?,?,?");
                            insertsql.append("BasePre,A0100,A0000");
                            sql.append(" where BasePre=? and A0100=? ");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 2) {
                            tempinsertstr.append("?,?");
                            insertsql.append("b0110,A0000");
                            sql.append(" where  b0110=? ");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 3) {
                            tempinsertstr.append("?,?");
                            insertsql.append("e01a1,A0000");
                            sql.append(" where  e01a1=? ");
                        } else {
                            tempinsertstr.append("?,?,?");
                            insertsql.append("BasePre,A0100,A0000");
                            sql.append(" where BasePre=? and A0100=? ");
                        }

                    }
                } else {

                    if (table_name.startsWith("templet")) {// 审批状态
                        if (bo.getBo() != null && bo.getBo().getInfor_type() == 1) {// 对人员处理的业务模板
                            insertsql.append("BasePre,A0100,ins_id,task_id");
                            tempinsertstr.append("?,?,?,?");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 2) {
                            insertsql.append("b0110,ins_id,task_id");
                            tempinsertstr.append("?,?,?");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 3) {
                            insertsql.append("e01a1,ins_id,task_id");
                            tempinsertstr.append("?,?,?");
                        } else {
                            insertsql.append("BasePre,A0100,ins_id,task_id");
                            tempinsertstr.append("?,?,?,?");
                        }

                    } else {
                        if (bo.getBo() != null && bo.getBo().getInfor_type() == 1) {
                            tempinsertstr.append("?,?,?");
                            insertsql.append("BasePre,A0100,A0000");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 2) {
                            tempinsertstr.append("?,?");
                            insertsql.append("b0110,A0000");
                        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 3) {
                            tempinsertstr.append("?,?");
                            insertsql.append("e01a1,A0000");
                        } else {
                            tempinsertstr.append("?,?,?");
                            insertsql.append("BasePre,A0100,A0000");
                        }

                    }
                    sql.append(" where " + onlyname + "_" + onlyflag + "=?  ");
                }

                // insertsql.setLength(sql.length() - 1);
                insertsql.append(",seqnum)values(" + tempinsertstr.toString() + ",?)");
            }
            ArrayList list2 = new ArrayList();
            ArrayList listvo = new ArrayList();
            ArrayList insertlist = new ArrayList();
            int num2 = 1;
            HashMap tablemap = getTableMap(table_name, infor_type, onlyflag, onlyname);
            ArrayList listxuhao = new ArrayList();// 新增时更新自动生成的序号
            String errorFileName = "";// 生成提示excel
            String updateCount="0";//更新的数据条数
            String importCount="0";//引入的数据条数
            if ("0".equals(onlyflag)) {
            	if(isHaveMainImpeople&&!isSettingOnlyName){
            		throw new GeneralException("模版中有指标设置了启用人员组件，请设置并且启用唯一性指标。");
            	}
               String[] result = bo.importMainExcel(sheet, table_name, nameMap, name2map, map, codeColMap, listxuhao, insertlist, tablemap, errorStr, list2, updateFidsCount, sql, insertsql, onlyflag, onlyname, onlynamesit, onlynameMap, filename,codeLeafItefColMap,codeLeafSetColList,mainImpeopleList,onlyKey,mainFatherMap,paramBo,filedUpdateList,ins_id);
               errorFileName=result[0];
               updateCount=result[1];
               importCount=result[2];
            } else {// 只有在系统中含有唯一性标识且模版中含有唯一性标识字段时 才会有子集工作表 才需要导入子集信息
            	if(isHaveMainImpeople&&!isSettingOnlyName){
            		throw new GeneralException("模版中有指标设置了启用人员组件，请设置并且启用唯一性指标。");
            	}
            	if(isHaveChildImpeople&&!isSettingOnlyName){
            		throw new GeneralException("模版中有子集设置了启用人员组件，请设置并且启用唯一性指标。");
            	}
            	String[] result = bo.importMainExcel(sheet, table_name, nameMap, name2map, map, codeColMap, listxuhao, insertlist, tablemap, errorStr, list2, updateFidsCount, sql, insertsql, onlyflag, onlyname, onlynamesit, onlynameMap, filename,codeLeafItefColMap,codeLeafSetColList,mainImpeopleList,onlyKey,mainFatherMap,paramBo,filedUpdateList,ins_id);
            	errorFileName=result[0];
                updateCount=result[1];
                importCount=result[2];
            	tablemap = getTableMap(table_name, infor_type, onlyflag, onlyname);
                for (int i = 1; i < sheetlist.size(); i++) {
                    String setname = "" + sheetlist.get(i);
                    sheet = wb.getSheetAt(Integer.parseInt("" + sheetnameMap.get(setname)));
                    TemplateSet cellBo =null;
                    for(int itemNum=0;itemNum<allTemplateItem.size();itemNum++){
                    	TemplateItem item = (TemplateItem) allTemplateItem.get(itemNum);
                    	cellBo = item.getCellBo();
                    	if(cellBo.isSubflag()&&setname.equalsIgnoreCase(cellBo.getSetname())){
                    		break;
                    	}
                    }
                    int subUpdateCount=bo.importSubExcel(sheet, table_name, nameMap, map, listxuhao, insertlist, tablemap, errorStr, list2, updateFidsCount, onlyflag, onlyname, onlynameMap, setname,childImpeopleList,onlyKey,subFatherMap,cellBo,paramBo,ins_id);
                    if("0".equalsIgnoreCase(updateCount))
                    {
                    	updateCount=String.valueOf(subUpdateCount);
                    }else if(Integer.valueOf(updateCount)>subUpdateCount)
                    {
                    	updateCount=String.valueOf(subUpdateCount);
                    }
                }
            }
            this.getFormHM().put("errorFileName", errorFileName);// 导入数据失败提示的excel
            this.getFormHM().put("onlyname", onlyname);// 是否有唯一性标识
            this.getFormHM().put("successNum", updateCount);// 是否有唯一性标识
            this.getFormHM().put("importCount", importCount);// 是否有唯一性标识

            String hintInfo = errorStr.toString();
            if (hintInfo.length() > 0) {
                if (hintInfo.length() > 2000) {
                    hintInfo = hintInfo.substring(0, 2000) + "……";// 字符串太长，前台加载慢
                                                                    // 10几分钟，提示信息多对用户也没太大用处，不会全看。wangrd
                                                                    // 2015-02-07
                }
                throw new GeneralException(hintInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeResource(wb);
            PubFunc.closeResource(inputStream);// 资源释放 jingq 2014.12.29
        }
    }

    /**
     * 获得库中所有的数据
     * 
     * @param table_name
     * @return
     */
    public HashMap getTableMap(String table_name, int infor_type, String onlyflag, String onlyname) {
        HashMap map = new HashMap();
        try {
            String sql = "select A0100  from " + table_name + "  ";
            if ("0".equals(onlyflag)) {
                if (infor_type == 1) {
                    sql = "select A0100  from " + table_name + "  ";
                } else if (infor_type == 2) {
                    sql = "select b0110  from " + table_name + "  ";
                } else if (infor_type == 3) {
                    sql = "select e01a1  from " + table_name + "  ";
                }
            } else {
                sql = "select " + onlyname + "_" + onlyflag + "  from " + table_name + "  ";
            }

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                map.put(this.frowset.getString(1), this.frowset.getString(1));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
