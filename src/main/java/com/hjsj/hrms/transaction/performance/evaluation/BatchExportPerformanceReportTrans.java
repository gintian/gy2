package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.ComputFormulaBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// import java.util.zip.ZipEntry;
// import java.util.zip.ZipOutputStream;

/**
 * <p>Title:GetPerformanceReportInfoTrans.java</p>
 * <p>Description:批量导出计划的绩效报告</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-09-29 09:24:03</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BatchExportPerformanceReportTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		String planid = (String) this.getFormHM().get("plan_id");
		String namerule = (String) this.getFormHM().get("namerule");
		String name = (String) this.getFormHM().get("name");
		name=SafeCode.decode(name);	
		name = PubFunc.keyWord_reback(name);
		FieldItem fielditem = DataDictionary.getFieldItem("E0122");
		
		this.deleteFile(new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport/"));
		// 产生临时存放导出数据的文件夹 之后还要压缩成一个包
		if (!(new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport/").isDirectory()))
		{
			new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport/").mkdir();
		}

		String outName = "";
		FileOutputStream fileOut = null;
		String zipFileName = "ZipOutOfPerformanceReport_" + PubFunc.getStrg() + ".zip";
		String filename_pre = "";
		byte[] content_pre = null;
		String ext_pre = "";
		String a0100_pre = "";
		int fileflag_pre = 1;
		String filename_pre_pre = "";
		String a0100_pre_pre = "";

		int x = 1;// 同一文件名的不同人标志
		int y = 1;// 同一个人的附件数
		int fileCount = 0;
		String[] lettersUpper =
		{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap map = new HashMap();
		try
		{
			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			String priWhl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
			String object_type = pb.getPlanVo(planid).getString("object_type");
			HashMap priObjMap = new HashMap();			
			String sql = "select object_id from per_object where plan_id=" + planid+" "+priWhl; 
			if("2".equals(object_type))
			{
				this.frowset = dao.search(sql);
				while (this.frowset.next())		
					priObjMap.put(this.frowset.getString("object_id"), "");
			}else
			{
				sql = "select object_id,mainbody_id from per_mainbody where body_id=-1 and plan_id=" + planid +" and object_id in (";
				sql+= "select object_id from per_object where plan_id=" + planid+" "+priWhl+")";
				this.frowset = dao.search(sql);
				while (this.frowset.next())				
					priObjMap.put(this.frowset.getString("mainbody_id"),"");
			}
			
			
			if ("index".equalsIgnoreCase(namerule))
			{	// state :1 提交 2 报批
				sql = "select * from per_article where plan_id=" + planid + "  AND article_type=2 ";
				sql += " and a0100 in (select a0100 from per_article where plan_id=" + planid + "  AND article_type=2 and state in (1,2)) order by b0110,e0122,e01a1,a0100,fileflag";

				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					String context = Sql_switcher.readMemo(this.frowset, "content");
					String ext = this.frowset.getString("ext") == null ? "" : this.frowset.getString("ext");
					InputStream inputStream = this.frowset.getBinaryStream("affix");
					int fileflag = this.frowset.getInt("fileflag"); // =1(文本) =2(附件)
                    if(fileflag==2 && inputStream==null) {
                        continue;
                    }
					if (!(fileflag == 1 && context.trim().length() == 0))// 空文本不算在报告数中
					{
						String b0110 = this.frowset.getString("b0110") == null ? "" : this.frowset.getString("b0110");
						String e0122 = this.frowset.getString("e0122") == null ? "" : this.frowset.getString("e0122");
						String e01a1 = this.frowset.getString("e01a1") == null ? "" : this.frowset.getString("e01a1");
						String a0101 = this.frowset.getString("a0101") == null ? "" : this.frowset.getString("a0101");
						String a0100 = this.frowset.getString("a0100") == null ? "" : this.frowset.getString("a0100");
						
						if(priObjMap.get(a0100)==null)
							continue;
						
						b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode.getCode("UN", b0110).getCodename() : "";
						e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode.getCode("UM", e0122).getCodename() : "";
						e01a1 = AdminCode.getCode("@K", e01a1) != null ? AdminCode.getCode("@K", e01a1).getCodename() : "";

						String filename = "";

						if (name.equals(ResourceFactory.getProperty("b0110.label")))
							filename = b0110;
						else if (name.equals(fielditem.getItemdesc()))
							filename = e0122;
						else if (name.equals(ResourceFactory.getProperty("e01a1.label")))
							filename = e01a1;
						else if ("姓名".equals(name))
							filename = a0101;
						else if ("人员编号".equals(name))
							filename = a0100;

						filename_pre_pre = filename_pre;
						a0100_pre_pre = a0100_pre;

						// 集团总部(A) 集团总部(B)_1 集团总部(B)_2_内容
						// 王光艳 司文辉_1 司文辉_2_内容
						if (filename_pre.equalsIgnoreCase(filename))
						{

							if (name.equals(ResourceFactory.getProperty("b0110.label")) || name.equals(fielditem.getItemdesc()) || name.equals(ResourceFactory.getProperty("e01a1.label")))
							{
								int div = x / 26;
								int mod = x % 26;
								if (div == 0 && mod > 0)
									filename_pre += "(" + lettersUpper[mod - 1] + ")";
								else if (div > 0 && mod > 0)
									filename_pre += "(" + lettersUpper[div - 1] + lettersUpper[mod - 1] + ")";
							}

							filename_pre += ("_" + y);
							if (!a0100_pre.equals(a0100))
							{
								x++;
								y = 1;
							}

							else
								y++;
							if ("".equals(ext_pre))
								filename_pre += "_内容";
						} else
						{
							if (filename_pre.length() > 0)
							{
								if (name.equals(ResourceFactory.getProperty("b0110.label")) || name.equals(fielditem.getItemdesc()) || name.equals(ResourceFactory.getProperty("e01a1.label")))
								{
									int div = x / 26;
									int mod = x % 26;
									if (div == 0 && mod > 0)
										filename_pre += "(" + lettersUpper[mod - 1] + ")";
									else if (div > 0  && mod > 0)
										filename_pre += "(" + lettersUpper[div - 1] + lettersUpper[mod - 1] + ")";
								}

								filename_pre += ("_" + y);
								if ("".equals(ext_pre))
									filename_pre += "_内容";
								x = 1;
								y = 1;
							}
						}

						// 生成前一个的文件
						if (filename_pre.length() > 0)
						{
							if ("".equals(ext_pre))
								ext_pre = "txt";
							outName = filename_pre + "." + ext_pre;
							map.put(outName, outName);
							try{
							    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport" + System.getProperty("file.separator")
	                                    + outName);
	                            fileOut.write(content_pre);
							}catch(Exception e){
							    e.printStackTrace();
							}
							finally{
							    PubFunc.closeIoResource(fileOut);
							}
							
						}

						// 保存当前的附件名字和内容等待加工
						filename_pre = filename;
						content_pre = null;
						if (fileflag == 1)
							content_pre = context.getBytes();
						else
						{
							byte buf[] = new byte[512];
							int len;
							int index = 0;
							int len2 = 0;
							while ((len = inputStream.read(buf)) != -1)
								len2 += len;
							content_pre = new byte[len2];
							inputStream.reset();
							while ((len = inputStream.read(buf)) != -1)
							{
								for (int i = 0; i < len; i++)
									content_pre[index++] = buf[i];
							}
						}
						ext_pre = ext;
						a0100_pre = a0100;
						fileflag_pre = fileflag;

					}
				}
				// 生成最后一个文件
				if (filename_pre.length() > 0)
				{
					if (filename_pre.equalsIgnoreCase(filename_pre_pre))
					{

						if (!a0100_pre.equals(a0100_pre_pre))
							x++;

						if (name.equals(ResourceFactory.getProperty("b0110.label")) || name.equals(fielditem.getItemdesc()) || name.equals(ResourceFactory.getProperty("e01a1.label")))
						{
							int div = x / 26;
							int mod = x % 26;
							if (div == 0 && mod > 0)
								filename_pre += "(" + lettersUpper[mod - 1] + ")";
							else if (div > 0 && mod > 0)
								filename_pre += "(" + lettersUpper[div - 1] + lettersUpper[mod - 1] + ")";
						}

						filename_pre += ("_" + y);
						if (!a0100_pre.equals(a0100_pre_pre))
							y = 1;
						else
							y++;
						if ("".equals(ext_pre))
							filename_pre += "_内容";
					} else
					{
						x = 1;
						y = 1;
						if (filename_pre.length() > 0)
						{
							if (name.equals(ResourceFactory.getProperty("b0110.label")) || name.equals(fielditem.getItemdesc()) || name.equals(ResourceFactory.getProperty("e01a1.label")))
							{
								int div = x / 26;
								int mod = x % 26;
								if (div == 0 && mod > 0)
									filename_pre += "(" + lettersUpper[mod - 1] + ")";
								else if (div > 0 && mod > 0)
									filename_pre += "(" + lettersUpper[div - 1] + lettersUpper[mod - 1] + ")";
							}

							filename_pre += ("_" + y);
							if ("".equals(ext_pre))
								filename_pre += "_内容";
						}
					}

					if ("".equals(ext_pre))
						ext_pre = "txt";
					outName = filename_pre + "." + ext_pre;
					map.put(outName, outName);
	                try{
	                    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport" + System.getProperty("file.separator") + outName);
	                    fileOut.write(content_pre);
	                }catch(Exception e){
	                    e.printStackTrace();
	                }finally{
	                    PubFunc.closeIoResource(fileOut);
	                }
					
				}

			} else if ("formula".equalsIgnoreCase(namerule))
			{
				ComputFormulaBo bo = new ComputFormulaBo("PerformanceReport_nameFormula", this.frameconn, planid, this.userView);
				String sqlFormula = bo.getSqlByFormula2(name);

				if(sqlFormula==null || sqlFormula.trim().length()<=0)
				{
					this.getFormHM().put("outName", "error");
					this.getFormHM().put("info", "未定义公式！");
					return;
				}
				
				String tablename = "per_article";
				Table table = new Table(tablename);
				DbWizard dbWizard = new DbWizard(this.frameconn);
				DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
				boolean isHavaFields = false;
				if (!dbWizard.isExistField(tablename, "planname", false))
				{
					Field obj = new Field("planname");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if (!dbWizard.isExistField(tablename, "b0110_cn", false))
				{
					Field obj = new Field("b0110_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if (!dbWizard.isExistField(tablename, "e0122_cn", false))
				{
					Field obj = new Field("e0122_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if (!dbWizard.isExistField(tablename, "e01a1_cn", false))
				{
					Field obj = new Field("e01a1_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if(isHavaFields)
				{
					dbWizard.addColumns(table);// 更新列
					dbmodel.reloadTableModel(tablename);
				}				
				
				String sqlstr = "";
				try
				{					
					if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
					{
						sqlstr = "update per_article set planname=(select name from per_plan where plan_id=" + planid + ") where plan_id="+planid;
						dao.update(sqlstr);
						sqlstr = "update per_article set b0110_cn=(select codeitemdesc  from organization  where codeitemid=b0110)";
						dao.update(sqlstr);
						sqlstr = "update per_article set e0122_cn=(select codeitemdesc  from organization  where codeitemid=e0122)";
						dao.update(sqlstr);
						sqlstr = "update per_article set e01a1_cn=(select codeitemdesc  from organization  where codeitemid=e01a1)";
						dao.update(sqlstr);
						
					} else
					{
						sqlstr = "update per_article set planname = per_plan.name from per_plan where per_plan.plan_id=per_article.plan_id";
						dao.update(sqlstr);
						sqlstr = "update per_article set b0110_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.b0110";
						dao.update(sqlstr);
						sqlstr = "update per_article set e0122_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.e0122";
						dao.update(sqlstr);
						sqlstr = "update per_article set e01a1_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.e01a1";
						dao.update(sqlstr);
					}

				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}

				sqlstr = "select " + sqlFormula + " aa,content,a0100,ext,affix,fileflag  from per_article where plan_id=" + planid + "  AND article_type=2 ";
				sqlstr += " and a0100 in (select a0100 from per_article where plan_id=" + planid + "  AND article_type=2 and state in (1,2)) order by b0110,e0122,e01a1,a0100,fileflag";

				this.frowset = dao.search(sqlstr);
				while (this.frowset.next())
				{
					String filename = this.frowset.getString(1);
					String context = Sql_switcher.readMemo(this.frowset, "content");
					String ext = this.frowset.getString("ext") == null ? "" : this.frowset.getString("ext");
					InputStream inputStream = this.frowset.getBinaryStream("affix");
					int fileflag = this.frowset.getInt("fileflag"); // =1(文本) =2(附件)
                    if(fileflag==2 && inputStream==null) {
                        continue;
                    }
					String a0100 = this.frowset.getString("a0100") == null ? "" : this.frowset.getString("a0100");
					
					if(priObjMap.get(a0100)==null)
						continue;					
					
					if (!(fileflag == 1 && context.trim().length() == 0))// 空文本不算在报告数中
					{
						filename_pre_pre = filename_pre;
						// 集团总部(A) 集团总部(B)_1 集团总部(B)_2_内容
						// 王光艳 司文辉_1 司文辉_2_内容
						if (filename_pre.equalsIgnoreCase(filename))
						{
							filename_pre += ("_" + y);
							y++;
							if ("".equals(ext_pre))
								filename_pre += "_内容";
						} else
						{
							if (filename_pre.length() > 0)
							{
								filename_pre += ("_" + y);
								if ("".equals(ext_pre))
									filename_pre += "_内容";
								y = 1;
							}
						}

						// 生成前一个的文件
						if (filename_pre.length() > 0)
						{
							if ("".equals(ext_pre))
								ext_pre = "txt";
							outName = filename_pre + "." + ext_pre;
							map.put(outName, outName);
				            try{
				                fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport" + System.getProperty("file.separator")
	                                    + outName);
	                            fileOut.write(content_pre);
				            }catch(Exception e){
				                  e.printStackTrace();
				            }finally{
				                  PubFunc.closeIoResource(fileOut);
				            }
							
						}

						// 保存当前的附件名字和内容等待加工
						filename_pre = filename;
						content_pre = null;
						if (fileflag == 1)
							content_pre = context.getBytes();
						else
						{
							byte buf[] = new byte[512];
							int len;
							int index = 0;
							int len2 = 0;
							while ((len = inputStream.read(buf)) != -1)
								len2 += len;
							content_pre = new byte[len2];
							inputStream.reset();
							while ((len = inputStream.read(buf)) != -1)
							{
								for (int i = 0; i < len; i++)
									content_pre[index++] = buf[i];
							}
						}
						ext_pre = ext;
						fileflag_pre = fileflag;
					}
				}
				// 生成最后一个文件
				if (filename_pre.length() > 0)
				{
					if (filename_pre.equalsIgnoreCase(filename_pre_pre))
					{
						filename_pre += ("_" + y);
						if ("".equals(ext_pre))
							filename_pre += "_内容";
					} else
					{
						y = 1;
						if (filename_pre.length() > 0)
						{
							filename_pre += ("_" + y);
							if ("".equals(ext_pre))
								filename_pre += "_内容";
						}
					}

					if ("".equals(ext_pre))
						ext_pre = "txt";
					outName = filename_pre + "." + ext_pre;
					map.put(outName, outName);
					if(outName.indexOf("?")>-1)
					{
						this.getFormHM().put("outName", "error");
						this.getFormHM().put("info", "文件名称["+filename_pre+"]中含有非法字符,请检查文件名设置指标的值！");
						return;
					}
	                try{
	                    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport" + System.getProperty("file.separator") + outName);
	                    fileOut.write(content_pre);
	                }catch(Exception e){
	                    e.printStackTrace();
	                }finally{
	                    PubFunc.closeIoResource(fileOut);
	                }
					
				}
				isHavaFields = false;
				if (dbWizard.isExistField(tablename, "planname", false))
				{
					Field obj = new Field("planname");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if (dbWizard.isExistField(tablename, " b0110_cn", false))
				{
					Field obj = new Field("b0110_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if (dbWizard.isExistField(tablename, "e0122_cn", false))
				{
					Field obj = new Field("e0122_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}
				if (dbWizard.isExistField(tablename, "e01a1_cn", false))
				{
					Field obj = new Field("e01a1_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					isHavaFields = true;
				}	
				if(isHavaFields)
				{
					dbWizard.dropColumns(table);// 更新列
					dbmodel.reloadTableModel(tablename);
				}				
			}

			// 压缩文件
			ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
			ArrayList files = new ArrayList(); // 存放文件对象
			ZipOutputStream outputStream = null;
			BufferedInputStream origin = null;
			try
			{
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + zipFileName);
				outputStream = new ZipOutputStream(fileOut);
				File rootFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "PerformanceReport");
				listFile(rootFile, fileNames, files);
				byte data[] = new byte[2048];

				for (int loop = 0; loop < files.size(); loop++)
				{
					String a_fileName = (String) fileNames.get(loop);
					if (map.get(a_fileName) == null)
						continue;
					fileCount++;
					FileInputStream fileIn = null;
					try{
						fileIn = new FileInputStream((File) files.get(loop));
						origin = new BufferedInputStream(fileIn, 2048);
						outputStream.putNextEntry(new ZipEntry(a_fileName));
						int count;
						while ((count = origin.read(data)) != -1)
						{
							outputStream.write(data, 0, count);
						}
						//【60075】VFS+UTF-8绩效管理：谷歌，绩效评估，导出绩效报告，压缩包乱码（WinRar）
						outputStream.setEncoding("GBK");
						outputStream.setComment("中文测试");
						origin.close();
						
					}finally{
						PubFunc.closeIoResource(fileIn);
					}
				}
//				outputStream.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			finally{
				PubFunc.closeIoResource(outputStream); //outputStream是依赖fileOut的 所以先关闭outputStream 否则压缩包到处去会坏掉  zhaoxg add 2015-5-6
			    PubFunc.closeIoResource(fileOut); 
			    
			    PubFunc.closeIoResource(origin); 
			}
			//xus 20/4/30 vfs改造
			zipFileName = PubFunc.encrypt(zipFileName);
			this.getFormHM().put("outName", zipFileName);
			this.getFormHM().put("info", "绩效报告导出成功！共导出" + fileCount + "个文件！");
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public static void listFile(File parentFile, List nameList, List fileList)
	{
		if (parentFile.isDirectory())
		{
			File[] files = parentFile.listFiles();
			for (int loop = 0; loop < files.length; loop++)
			{
				listFile(files[loop], nameList, fileList);
			}
		} else
		{
			fileList.add(parentFile);
			nameList.add(parentFile.getName());
		}
	}

	private void deleteFile(File file)
	{
		if (file.exists())
		{
			if (file.isFile())
			{
				file.delete();
			} else if (file.isDirectory())
			{
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					this.deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

}
