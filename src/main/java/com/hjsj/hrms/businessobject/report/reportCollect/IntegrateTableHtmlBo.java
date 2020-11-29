package com.hjsj.hrms.businessobject.report.reportCollect;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TpageBo;
import com.hjsj.hrms.businessobject.ykcard.MadeCardCellLine;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DateFormat;
import java.util.*;

public class IntegrateTableHtmlBo {
  private int constantNum = 0;

  private String exceflag = "0";

  private float percent = 0.26f;
  
  private UserView userview;

	public IntegrateTableHtmlBo(UserView userview) {
		this.userview = userview;
	}

	public IntegrateTableHtmlBo() {
	}

/**
   * 自动修改单元格的边线位置，使其不会出现重叠效果
   * 
   * @param itemVo
   *          项目单元格对象
   * @param vo
   *          其他单元格对象
   */
  public void autoEditBorder(int[] itemGridArea, RecordVo vo) {

    if (vo.getInt("rleft") + vo.getInt("rwidth") <= itemGridArea[0]
        + itemGridArea[2]
        && vo.getInt("rtop") + vo.getInt("rheight") <= itemGridArea[1]
            + itemGridArea[3]) {
      return;
    }
    else {
      if (vo.getInt("flag") == 1) // 横表栏
      {
        if (vo.getInt("rtop") != itemGridArea[1]) {
          vo.setInt("rtop", vo.getInt("rtop") - 1);
          vo.setInt("rheight", vo.getInt("rheight") + 1);
        }
        vo.setInt("rleft", vo.getInt("rleft") - 1);
        vo.setInt("rwidth", vo.getInt("rwidth") + 1);
      }
      else // 纵表栏
      {
        if (vo.getInt("rleft") != itemGridArea[0]) {
          vo.setInt("rleft", vo.getInt("rleft") - 1);
          vo.setInt("rwidth", vo.getInt("rwidth") + 1);
        }
        vo.setInt("rtop", vo.getInt("rtop") - 1);
        vo.setInt("rheight", vo.getInt("rheight") + 1);
      }
    }
  }

  /**
   * get数据区html
   * 
   * @param rowInfoBGrid
   * @param colInfoBGrid
   * @param itemGridvo
   * @param status
   * @param minTop_px
   * @param dataArea
   * @param rowSerialNo
   *          横表栏序号所在位置
   * @param colSerialNo
   *          纵表栏序号所在位置
   * @return
   */
  public String createData(String tabid, String userName,
      ArrayList rowInfoBGrid, ArrayList colInfoBGrid, RecordVo dataArea,
      String rowSerialNo, String colSerialNo, RecordVo tnameVo, HashMap rowMap,
      HashMap colMap, ArrayList resultList, String nums) {
    StringBuffer html = new StringBuffer("");
    String fontName = tnameVo.getString("fontname");
    int fontSize = tnameVo.getInt("fontsize");
    int fontStyle = tnameVo.getInt("fonteffect");
    int colNum = 0;
    int rowNum = 0;
    if (resultList.size() > 0) {
      for (int i = 0; i < resultList.size(); i++) {
        rowNum = 0;
        String[] rowInfo = (String[]) resultList.get(i);
        if (i >= colInfoBGrid.size()) {
          continue;
        }

        RecordVo colVo = (RecordVo) colInfoBGrid.get(i);
        if (colVo.getInt("flag1") != 4) {
          colNum++;
        }

        for (int j = 0; j < rowInfo.length; j++) {
          String context = "";
          int flag = 0;

          RecordVo rowVo = (RecordVo) rowInfoBGrid.get(j);
          int r = rowVo.getInt("r");
          int npercent =
              rowVo.getInt("npercent") >= colVo.getInt("npercent") ? rowVo
                  .getInt("npercent") : colVo.getInt("npercent");
          String top = String.valueOf(colVo.getInt("rtop"));
          String left = String.valueOf(rowVo.getInt("rleft"));
          String width = String.valueOf(rowVo.getInt("rwidth"));
          String height = String.valueOf(colVo.getInt("rheight"));
          if (rowVo.getInt("flag1") != 4) {
            rowNum++;
          }
          if (colVo.getInt("flag1") == 4 && rowVo.getInt("flag1") == 4) {
            context = "";
          }
          else if (colVo.getInt("flag1") == 4) {
            context = String.valueOf(rowNum);
          }
          else if (rowVo.getInt("flag1") == 4) {
            context = String.valueOf(colNum);
          }
          else {
            if ("0".equals(rowInfo[j])) {
              context = "";
            }
            else {
              context = PubFunc.round(rowInfo[j], npercent);
              if ("0".equals(context)) {
                context = "";
              }
            }

            flag = 1;
          }
          html.append(this
              .executeAbsoluteTable_data(flag, top, left, width, height,
                  context, i, j, fontSize, fontStyle, fontName, npercent, r));
        }
      }
    }
    else {
      for (int i = 0; i < colInfoBGrid.size(); i++) {
        rowNum = 0;
        RecordVo colVo = (RecordVo) colInfoBGrid.get(i);
        if (colVo.getInt("flag1") != 4) {
          colNum++;
        }

        for (int j = 0; j < rowInfoBGrid.size(); j++) {
          String context = "";
          int flag = 0;

          RecordVo rowVo = (RecordVo) rowInfoBGrid.get(j);
          int r = rowVo.getInt("r");
          int npercent =
              rowVo.getInt("npercent") >= colVo.getInt("npercent") ? rowVo
                  .getInt("npercent") : colVo.getInt("npercent");
          String top = String.valueOf(colVo.getInt("rtop"));
          String left = String.valueOf(rowVo.getInt("rleft"));
          String width = String.valueOf(rowVo.getInt("rwidth"));
          String height = String.valueOf(colVo.getInt("rheight"));
          if (rowVo.getInt("flag1") != 4) {
            rowNum++;
          }
          if (colVo.getInt("flag1") == 4 && rowVo.getInt("flag1") == 4) {
            context = "";
          }
          else if (colVo.getInt("flag1") == 4) {
            context = String.valueOf(rowNum);
          }
          else if (rowVo.getInt("flag1") == 4) {
            context = String.valueOf(colNum);
          }
          else {
            flag = 1;
          }
          html.append(this
              .executeAbsoluteTable_data(flag, top, left, width, height,
                  context, i, j, fontSize, fontStyle, fontName, npercent, r));
        }
      }
    }
    return html.toString();
  }

  /**
   * 生成表头和项目栏
   * 
   * @param gridList
   *          单元格集合
   * @param itemGridNo
   *          项目单元格id号
   * @return
   */
  public String createTableHeader(ArrayList gridList, int[] itemGridArea,
      int minTop_px) {
    StringBuffer htmlHeader = new StringBuffer("");

    MadeCardCellLine madeCardCellLine = new MadeCardCellLine();
    // 统一字体大小
    int fontsize = 0;
    for (Iterator t = gridList.iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      vo.setInt("rtop", vo.getInt("rtop") + minTop_px); // 集体上移

      if (fontsize == 0) {
        fontsize = vo.getInt("fontsize") + 1;
      }
      String context = "&nbsp;";
      // 处理虚线 L,T,R,B,
      String style_name =
          madeCardCellLine.GetCardCellLineShowcss(String
              .valueOf(vo.getInt("l")), String.valueOf(vo.getInt("r")), String
              .valueOf(vo.getInt("t")), String.valueOf(vo.getInt("b")));
      if (vo.getString("hz") != null && vo.getString("hz").indexOf("`") != -1) {
        context = vo.getString("hz").replaceAll("`", "<br>");
        if (context.startsWith("<br>")) {
          context = context.substring(4, context.length());
        }

      }
      else if (vo.getString("hz") != null
          && vo.getString("hz").indexOf("`") == -1) {
        context = vo.getString("hz");
      }
      if (vo.getInt("flag") != 3) {
        this.autoEditBorder(itemGridArea, vo); // 自动修改单元格的边线位置，使其不会出现重叠效果
        htmlHeader.append(this.executeAbsoluteTable2(vo.getInt("align"), vo
            .getString("fontname"), "" + fontsize, String.valueOf(vo
            .getInt("fonteffect")), "1", String.valueOf(vo.getInt("rtop")),
            String.valueOf(vo.getInt("rleft")), String.valueOf(vo
                .getInt("rwidth")), String.valueOf(vo.getInt("rheight")),
            context, style_name, vo));

      }
    }
    return htmlHeader.toString();
  }

  /**
   * 生成上标题
   * 
   * @param pageList
   *          标题列表
   * @param minTop_px
   *          需上移的像素单位
   * @param dataArea
   *          表格数据区对象
   * @return
   */

  public String createTitle(ArrayList pageList, HashMap paramMap,
      RecordVo dataArea, String username, int minTop_px, double a_height,
      TnameBo tnameBo) {
    StringBuffer htmlTitle = new StringBuffer("");
    Date dd = new Date(); // 制表时间
    double difference = this.getDifference(tnameBo, a_height);
    int[] position = tnameBo.getElementPosion();//tgrid2  正文的位置  top right bottom left xiegh bug:20170628
    for (Iterator t = pageList.iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      int rtop = vo.getInt("rtop");
      if(rtop>position[0]&&rtop<position[2]) {
          continue;
      }
      StringBuffer content = new StringBuffer("");
      switch (vo.getInt("flag")) {
        case 0:
          content.append(vo.getString("hz"));
          break;
        case 1:
          GregorianCalendar d = new GregorianCalendar();
          content.append(ResourceFactory
              .getProperty("hmuster.label.createTableDate")
              + ":"
              + d.get(Calendar.YEAR)
              + "."
              + d.get(Calendar.MONTH)
              + "."
              + d.get(Calendar.DATE));
          break;
        case 2:
          content.append(ResourceFactory
              .getProperty("hmuster.label.createTableTime")
              + DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA)
                  .format(dd));
          break;
        case 3:
          content.append(ResourceFactory
              .getProperty("hmuster.label.createTableMen")
              + "：" + username);
          break;
        case 4: // 总页数
          break;
        case 5: // 页码
          break;
        case 9: // 参数定义
          String hz = vo.getString("hz");
          HashMap param_map = (HashMap) paramMap.get(hz);
          content.append(this.getParamHtml(param_map));
          break;
      }
      int a_width = content.length() * (vo.getInt("fontsize") + 6);
      String a_top = String.valueOf(vo.getInt("rtop") + minTop_px);
      int[] itemGridArea = tnameBo.getItemGridArea(); // 表项目区域 l,t,w,h

      if (vo.getInt("rtop") > itemGridArea[1]
          && !"乙".equals(vo.getString("hz"))) {
        a_top = String.valueOf(vo.getInt("rtop") + minTop_px + difference);
      }

      htmlTitle.append(this.executeAbsoluteTable(2, 6,
          vo.getString("fontname"), String.valueOf(vo.getInt("fontsize") + 1),
          String.valueOf(vo.getInt("fonteffect")), "0", (Float
              .parseFloat(a_top) - 10 + ""),
          String.valueOf(vo.getInt("rleft")), String.valueOf(a_width > vo
              .getInt("rwidth") ? a_width + 40 : vo.getInt("rwidth") + 40),
          String.valueOf(vo.getInt("rheight")), "<p>" + content.toString()
              + "</p>", ""));
    }
    for (Iterator t = pageList.iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      StringBuffer content = new StringBuffer("");

      if (vo.getInt("flag") == 10) {
        content.append(this.getParamPictureHtml(vo, String.valueOf(vo
            .getInt("fontsize") + 1)));
        String extendattr = vo.getString("extendattr");
        String background = this.getExtendAttrContext(5, extendattr);
        String zindex = "";
        if (background == null || "".equalsIgnoreCase(background)) {
          return "";
        }

        if ("True".equalsIgnoreCase(background)) {
          zindex = "z-index:-1;";
        }
        htmlTitle.append(this.executeAbsoluteTable(2, 0, vo
            .getString("fontname"), String.valueOf(vo.getInt("fontsize") + 1),
            String.valueOf(vo.getInt("fonteffect")), "0", String.valueOf(vo
                .getInt("rtop")
                + minTop_px), String.valueOf(vo.getInt("rleft")), String
                .valueOf(vo.getInt("rwidth")), String.valueOf(vo
                .getInt("rheight")), content.toString(), zindex));
      }
    }
    return htmlTitle.toString();

  }

  /**
   * 生成综合报表html页面
   * 
   * @return
   */
  public String creatHtmlView(TnameBo tnameBo, int minTop_px, ArrayList result,
      String nums, String[] condition, String tname) {

    ArrayList list = this.getNewGridList(tnameBo, nums, condition);
    ArrayList a_rowInfoBGrid = (ArrayList) list.get(0);
    ArrayList b_colInfoBGrid = (ArrayList) list.get(1);
    /* 根据选中的列行条件 替换单元格对象 */
    ArrayList a_gridList = (ArrayList) list.get(2);
    StringBuffer html = new StringBuffer("");
    String tableHeader =
        this
            .createTableHeader(a_gridList, tnameBo.getItemGridArea(), minTop_px);
    String tableDataArea =
        this.createData(tnameBo.getTabid(), tnameBo.getUserName(),
            a_rowInfoBGrid, b_colInfoBGrid, tnameBo.getDataArea(), tnameBo
                .getRowSerialNo(), tnameBo.getColSerialNo(), tnameBo
                .getTnameVo(), tnameBo.getRowMap(), tnameBo.getColMap(),
            result, nums);

    int width =
        tnameBo.getItemGridArea()[2] + tnameBo.getDataArea().getInt("rwidth");
    int left = tnameBo.getItemGridArea()[0];
    int buttonTop = 30 + 3;

    double a_width =
        tnameBo.getDataArea().getInt("rleft")
            + tnameBo.getDataArea().getInt("rwidth") + 20;
    double a_height =
        tnameBo.getDataArea().getInt("rtop")
            + tnameBo.getDataArea().getInt("rheight") + 30;

    String atemp = nums.substring(3);
    String[] temps = atemp.split(",");
    if (nums.indexOf(",a") != -1) // 列
    {
      if (list.size() > 3) {
        a_width = Double.parseDouble("" + list.get(3)) + 50;
      }
      else {
        a_width =
            (condition.length + 1) * temps.length * 70
                + tnameBo.getItemGridArea()[2] + tnameBo.getItemGridArea()[0]
                + 20;
      }
    }
    else // 行
    {
      if (list.size() > 4) {
        a_height = Double.parseDouble("" + list.get(4)) + 30;
      }
      else {
        a_height =
            (condition.length + 1) * temps.length * this.constantNum
                + tnameBo.getItemGridArea()[3] + tnameBo.getItemGridArea()[1]
                + 30;
      }

    }

    TpageBo tpageBo = tnameBo.getTpageBo();
    ArrayList pageAndParamList =
        tpageBo.getPageListAndTparam2(String.valueOf(tnameBo.getTnameVo()
            .getInt("tabid")), tnameBo.getTnameVo().getInt("tsortid"), tnameBo
            .getUnitcode(tnameBo.getUserName()));
    String tableTitle =
        this.createTitle(tnameBo.getPageList(), (HashMap) pageAndParamList
            .get(1), tnameBo.getDataArea(), userview==null?tnameBo.getUserName():userview.getUserFullName(), minTop_px,
            a_height, tnameBo);

    a_height = a_height + this.getBottomTitleHight(tnameBo) + 30;
    if (nums.indexOf(",a") != -1) // 列
    {
      double aa_width =
          (tnameBo.getTnameVo().getInt("paperori") == 1 ? tnameBo.getTnameVo()
              .getInt("paperw") : tnameBo.getTnameVo().getInt("paperh"))
              / this.percent;
      if (aa_width > a_width) {
        a_width = aa_width;
      }
    }

    html.append(this
        .executeAbsoluteBackground(minTop_px, 10, a_width, a_height));

    html.append(tableTitle);
    html.append(tableHeader);
    html.append(tableDataArea);

    return html.toString();
  }

  /**
   * 生成绝对定位的背景页面
   * 
   * @param top、left、width、height
   *          表格的绝对位置
   */
  public String executeAbsoluteBackground(int top, int left, double width,
      double height) {
    StringBuffer tempHtml = new StringBuffer("");

    tempHtml.append("<div id=idDIV ");
    tempHtml.append(" style='position:absolute;top:");
    tempHtml.append(top+"px");
    tempHtml.append(";left:");
    tempHtml.append(left+"px");
    tempHtml.append(";width:");
    tempHtml.append(width+"px");
    tempHtml.append(";height:");
    tempHtml.append(height+"px");
    tempHtml
        .append(";border:thin outset buttonface;background : #ffffff ;'> \n ");
    tempHtml.append("&nbsp;</div>");

    tempHtml.append("<div ");
    tempHtml.append(" style='position:absolute;top:");
    tempHtml.append(top+"px");
    tempHtml.append(";left:");
    tempHtml.append((left + width)+"px");
    tempHtml.append(";width:");
    tempHtml.append(20+"px");
    tempHtml.append(";height:");
    tempHtml.append(2+"px");
    tempHtml.append(";'> \n ");
    tempHtml.append("&nbsp;</div>");
    return tempHtml.toString();
  }

  /**
   * 生成绝对定位的table(每个table表示一个单元格)
   * 
   * @param border
   *          边宽
   * @param align
   *          字体布局位置
   * @param top、left、width、height
   *          表格的绝对位置
   * @param type
   *          1:表格 2：标题
   * @param context
   *          内容
   */
  public String executeAbsoluteTable(int type, int Align, String fontName,
      String fontSize, String fontStyle, String border, String top,
      String left, String width, String height, String context,
      String style_name) {

    StringBuffer tempTable = new StringBuffer("");
    String[] temp = this.transAlign(Align);
    String aValign = temp[0];
    String aAlign = temp[1];
    String zindex = "";
    if (style_name.startsWith("z-index")) {
      zindex = style_name;
    }
    tempTable.append(" <table   border='" + border
        + "' cellspacing='0'  align='center' cellpadding='1'");
    if (type == 1) {
      tempTable.append(" class='ListTable' ");
    }
    tempTable.append(" style='position:absolute;" + zindex + "top:");
    tempTable.append(top+"px");
    tempTable.append(";left:");
    tempTable.append(left+"px");
    tempTable.append(";width:");
    tempTable.append(width);
    tempTable.append(";height:");
    tempTable.append(height);
    tempTable.append("'> \n ");
    tempTable.append(" <tr valign='middle' align='center'> \n ");
    tempTable.append(" <td ");
    if (type == 1) {
      tempTable.append(" class='" + style_name + "' ");
    }
    tempTable.append(" valign='");
    tempTable.append(aValign);
    tempTable.append("' align='");
    tempTable.append(aAlign);
    tempTable.append("'> \n ");
    int aFontSize = 0;
    aFontSize = Integer.parseInt(fontSize) - 1;
    String style = this.getFontStyle(fontStyle, aFontSize);
    tempTable.append(" <font face='");
    tempTable.append(fontName);
    tempTable.append("' style='");
    tempTable.append(style);
    tempTable.append("' > \n ");
    tempTable.append(context);
    tempTable.append("</font></td></tr></table> \n ");

    return tempTable.toString();
  }

  /**
   * 生成绝对定位的table(每个table表示一个单元格)
   * 
   * @param flag
   *          1:包含编辑框 0：只显示
   * @param top
   * @param left
   * @param width
   * @param height
   * @param context
   *          值
   * @param i
   * @param j
   * @param status
   *          报表状态
   * @param color
   *          单元格背景色
   * @param isAutoAccount
   *          是否需要自动计算
   * @param operateObject
   *          1:操作未上报的表 2：操作已上报的表
   * @return
   */
  public String executeAbsoluteTable_data(int flag, String top, String left,
      String width, String height, String context, int i, int j, int fontSize,
      int fontStyle, String fontName, int npercent, int r) {

    StringBuffer tempTable = new StringBuffer("");
    tempTable
        .append(" <table   border='1' cellspacing='0'  align='center' cellpadding='1'");
    tempTable.append(" class='ListTable' ");
    tempTable.append(" style='table-layout:fixed;position:absolute;top:");
    tempTable.append(top+"px");
    tempTable.append(";left:");
    tempTable.append(left+"px");
    tempTable.append(";width:");
    tempTable.append(width);
    tempTable.append(";height:");
    tempTable.append(height);
    tempTable.append("'> \n ");
    tempTable.append(" <tr valign='middle' align='center'> \n ");
    tempTable.append(" <td class='");
    if (r == 1) {
      tempTable.append("RecordRow_self");
    }
    else {
      tempTable.append("RecordRow_self_r");
    }
    tempTable.append("'  id='aa" + i + "_" + j + "'");
    tempTable.append(" width=");
    tempTable.append(width);
    tempTable.append(" height=");
    tempTable.append(height);

    tempTable.append("  align='center' > \n ");
    if (flag == 1) {
      tempTable
          .append("<input type='text' name='a" + i + "_" + j + "' value='");
      tempTable.append(context);
      tempTable.append("'");
      tempTable.append(" class='TEXT_NB' style='height: 15px; width: ");
      tempTable.append((Integer.parseInt(width) * 0.8));
      tempTable.append("px;font-size:" + fontSize + ";text-align= right' ");
      tempTable
          .append("   onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'   />");
    }
    else {
      int aFontSize = fontSize - 1;
      String style = this.getFontStyle(String.valueOf(fontStyle), aFontSize);
      tempTable.append(" <font face='");
      tempTable.append(fontName);
      tempTable.append("' style='");
      tempTable.append(style);
      tempTable.append("' > \n ");
      tempTable.append(context);
      tempTable.append("</font>");

      tempTable.append("<input type='hidden' name='a" + i + "_" + j
          + "' value='0' ");
    }
    tempTable.append("</td></tr></table> \n ");
    return tempTable.toString();
  }

  /**
   * 生成绝对定位的table(每个table表示一个单元格,主要针对表头对象,点击底层表头，选中相应的行列数据)
   * 
   * @param border
   *          边宽
   * @param align
   *          字体布局位置
   * @param top、left、width、height
   *          表格的绝对位置
   * @param type
   *          1:表格 2：标题
   * @param context
   *          内容
   */
  public String executeAbsoluteTable2(int Align, String fontName,
      String fontSize, String fontStyle, String border, String top,
      String left, String width, String height, String context,
      String style_name, RecordVo vo) {

    StringBuffer tempTable = new StringBuffer("");
    String[] temp = this.transAlign(Align);
    String aValign = temp[0];
    String aAlign = temp[1];
    tempTable.append(" <table   border='" + border
        + "' cellspacing='0'  align='center' cellpadding='1'");
    tempTable.append(" class='ListTable' ");

    StringBuffer a_style = new StringBuffer("");
    a_style.append(" style='table-layout:fixed;position:absolute;top:");
    a_style.append(top+"px");
    a_style.append(";left:");
    a_style.append(left+"px");
    a_style.append(";width:");
    a_style.append(width);
    a_style.append(";height:");
    a_style.append(height);
    tempTable.append(a_style + "'");
    tempTable.append("  > \n ");
    tempTable.append(" <tr valign='middle' align='center'> \n ");
    tempTable.append(" <td ");
    tempTable.append(" class='" + style_name + "' ");
    String contextAnalyse = context.replaceAll("<br>", "");
    boolean flag = false;
    if (contextAnalyse.indexOf(";") != -1) {// xgq 2010 01 18
      String[] contextAnalyse2 = contextAnalyse.split(";");
      if (contextAnalyse2.length == 3 || contextAnalyse2.length == 4) {
        flag = true;
        if ("1".equals(contextAnalyse2[2])) {
          contextAnalyse =
              contextAnalyse2[0]
                  + ResourceFactory.getProperty("columns.archive.year")
                  + contextAnalyse2[1]
                  + ResourceFactory.getProperty("hmuster.label.count");
        }
        else if ("2".equals(contextAnalyse2[2])) {
          contextAnalyse =
              contextAnalyse2[0]
                  + ResourceFactory.getProperty("columns.archive.year");
        }
        if ("3".equals(contextAnalyse2[2])) {
          if ("1".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory
                        .getProperty("report.pigeonhole.uphalfyear");
          }
          else {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory
                        .getProperty("report.pigeonhole.downhalfyear");
          }
        }
        if ("4".equals(contextAnalyse2[2])) {
          if ("1".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory
                        .getProperty("report.pigionhole.oneQuarter");
          }
          else if ("2".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory
                        .getProperty("report.pigionhole.twoQuarter");
          }
          else if ("3".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory
                        .getProperty("report.pigionhole.threeQuarter");
          }
          else if ("4".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory
                        .getProperty("report.pigionhole.fourQuarter");
          }
        }
        if ("5".equals(contextAnalyse2[2])) {
          if ("1".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.january");
          }
          else if ("2".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.february");
          }
          else if ("3".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.march");
          }
          else if ("4".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.april");
          }
          else if ("5".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.may");
          }
          else if ("6".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.june");
          }
          else if ("7".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.july");
          }
          else if ("8".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.auguest");
          }
          else if ("9".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.september");
          }
          else if ("10".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.october");
          }
          else if ("11".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.november");
          }
          else if ("12".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.december");
          }

        }
        if ("6".equals(contextAnalyse2[2])) {
          if ("1".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.january");
          }
          else if ("2".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.february");
          }
          else if ("3".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.march");
          }
          else if ("4".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.april");
          }
          else if ("5".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.may");
          }
          else if ("6".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.june");
          }
          else if ("7".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.july");
          }
          else if ("8".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.auguest");
          }
          else if ("9".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.september");
          }
          else if ("10".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.october");
          }
          else if ("11".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.november");
          }
          else if ("12".equals(contextAnalyse2[1])) {
            contextAnalyse =
                contextAnalyse2[0]
                    + ResourceFactory.getProperty("columns.archive.year")
                    + ResourceFactory.getProperty("date.month.december");
          }
          if ("1".equals(contextAnalyse2[3])) {
            contextAnalyse +=
                ResourceFactory.getProperty("performance.workdiary.one.week");
          }
          else if ("2".equals(contextAnalyse2[3])) {
            contextAnalyse +=
                ResourceFactory.getProperty("performance.workdiary.two.week");
          }
          else if ("3".equals(contextAnalyse2[3])) {
            contextAnalyse +=
                ResourceFactory.getProperty("performance.workdiary.three.week");
          }
          else if ("4".equals(contextAnalyse2[3])) {
            contextAnalyse +=
                ResourceFactory.getProperty("performance.workdiary.four.week");
          }
          else if ("5".equals(contextAnalyse2[3])) {
            contextAnalyse +=
                ResourceFactory.getProperty("performance.workdiary.five.week");
          }
          else if ("6".equals(contextAnalyse2[3])) {
            contextAnalyse +=
                ResourceFactory.getProperty("performance.workdiary.six.week");
          }
        }
      }
    }
    if (flag) {
      tempTable.append(" title='" + contextAnalyse + "' ");

    }
    else {
      tempTable.append(" title='" + context.replaceAll("<br>", "") + "' ");
    }

    tempTable.append(" width=");
    tempTable.append(width);
    tempTable.append(" height=");
    tempTable.append(height);

    tempTable.append(" valign='");
    tempTable.append(aValign);
    tempTable.append("' align='");
    tempTable.append(aAlign);
    tempTable.append("'> \n ");
    int aFontSize = 0;
    aFontSize = Integer.parseInt(fontSize);

    aFontSize =
        this.getFitFontSize(aFontSize, Float.parseFloat(width), Float
            .parseFloat(height), context);
    if (Integer.parseInt(fontSize) - 1 == aFontSize) {
      aFontSize = Integer.parseInt(fontSize);
    }
    else if (Integer.parseInt(fontSize) - 2 == aFontSize) {
      aFontSize = Integer.parseInt(fontSize);
    }
    String style = this.getFontStyle(fontStyle, aFontSize);
    tempTable.append(" <font face='");
    tempTable.append(fontName);
    tempTable.append("' style='");
    tempTable.append(style);
    tempTable.append("' > \n ");
    if (flag) {
      tempTable.append(contextAnalyse);
    }
    else {
      tempTable.append(context);
    }

    tempTable.append("</font></td></tr></table> \n ");

    return tempTable.toString();
  }

  /*
   * 取得下标题的高度
   */
  public float getBottomTitleHight(TnameBo tnameBo) {
    int bottom_minTop = 0;
    int bottom_maxTop = 0;

    int[] itemGridArea = tnameBo.getItemGridArea(); // 表项目区域 l,t,w,h
    for (Iterator t = tnameBo.getPageList().iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      if (vo.getInt("rtop") > itemGridArea[1]) {
        if (bottom_minTop == 0 || bottom_maxTop == 0) {
          bottom_minTop = vo.getInt("rtop");
          bottom_maxTop = vo.getInt("rtop");
        }
        else {
          if (vo.getInt("rtop") < bottom_minTop) {
            bottom_minTop = vo.getInt("rtop");
          }
          if (vo.getInt("rtop") > bottom_maxTop) {
            bottom_maxTop = vo.getInt("rtop");
          }
        }
      }
    }

    return bottom_maxTop - bottom_minTop;
  }

  public int getConstatnNum(String[] condition) {
    int num = 25;
    int length = 0;
    for (int i = 0; i < condition.length; i++) {
      String a_condition = condition[i];
      String[] condition_arr = a_condition.split(":");
      if (condition_arr[1].getBytes().length > length) {
        length = condition_arr[1].getBytes().length;
      }
    }
    if (length > 24) {
      num = 40;
    }
    if (length > 48) {
      num = 60;
    }
    this.constantNum = num;
    return num;
  }

  /**
   * @param flag
   *          1:有边线 0：无边
   * @param context1
   * @param context2
   * @param a_width
   * @param aValign
   * @param aAlign
   * @return
   */
  public String getContext(int flag, String context1, String context2,
      float a_width, String aValign, String aAlign) {
    StringBuffer tempTable = new StringBuffer("");
    tempTable.append(" <td ");
    if (flag == 1) {
      tempTable.append(" class='RecordRow_self' ");
    }
    tempTable.append(" valign='");
    tempTable.append(aValign);
    tempTable.append("' align='");
    tempTable.append(aAlign);
    if (a_width > 1) {
      tempTable.append("' width='" + a_width + "' > \n ");
    }
    else {
      tempTable.append("' width='" + a_width * 100 + "%' > \n");
    }
    int aFontSize = 9;
    String style = this.getFontStyle("1", aFontSize);
    tempTable.append(" <font face='"
        + ResourceFactory.getProperty("hmuster.label.fontSt") + "' style='");
    tempTable.append(style);
    tempTable.append("' > \n ");
    tempTable.append(context1);
    tempTable.append("</font>&nbsp;");
    tempTable.append(context2);
    tempTable.append("</td>");
    return tempTable.toString();
  }

  /*
   * 取得下标题的位移差异值
   */
  public double getDifference(TnameBo tnameBo, double a_height) {
    int up_minTop = 0;
    int[] itemGridArea = tnameBo.getItemGridArea(); // 表项目区域 l,t,w,h
    for (Iterator t = tnameBo.getPageList().iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      if (vo.getInt("rtop") > itemGridArea[1]) {
        if (up_minTop == 0) {
          up_minTop = vo.getInt("rtop");
        }
        else {
          if (vo.getInt("rtop") < up_minTop) {
            up_minTop = vo.getInt("rtop");
          }
        }
      }
    }
    return a_height - up_minTop;
  }

  public String getExceflag() {
    return this.exceflag;
  }

  /**
   * 取得tpage下的extendAttr字段的内容<image>
   *<ext>.JPG|.BMP</ext><stretch>拉伸True|False</stretch>
   *<transparent>透明True|False</transparent>
   * <proportional>保持比例True|False</proportional>
   *<background>置底True(默认值)|置顶False</background>
   *1表示图片，2表示拉伸，3透明，4保持比列，5置底
   *</image>
   * 
   * @return
   */
  public String getExtendAttrContext(int flag, String extendAttr) {
    String temp = "";

    if (extendAttr != null && extendAttr.length() > 0) {
      if (extendAttr.indexOf("<image>") != -1) {
        if (flag == 1) {
          if (extendAttr.indexOf("<ext>") == -1) {
            temp = ".jpg";
          }
          else {
            int fromIndex = extendAttr.indexOf("<ext>");
            int toIndex = extendAttr.indexOf("</ext>");
            temp = extendAttr.substring(fromIndex + 5, toIndex).trim();

          }
        }
        if (flag == 2) {
          if (extendAttr.indexOf("<stretch>") == -1) {
            temp = "True";
          }
          else {
            int fromIndex = extendAttr.indexOf("<stretch>");
            int toIndex = extendAttr.indexOf("</stretch>");
            temp = extendAttr.substring(fromIndex + 9, toIndex).trim();

          }
        }
        if (flag == 3) {
          if (extendAttr.indexOf("<transparent>") == -1) {
            temp = "True";
          }
          else {
            int fromIndex = extendAttr.indexOf("<transparent>");
            int toIndex = extendAttr.indexOf("</transparent>");
            temp = extendAttr.substring(fromIndex + 13, toIndex).trim();

          }
        }
        if (flag == 4) {
          if (extendAttr.indexOf("<proportional>") == -1) {
            temp = "True";
          }
          else {
            int fromIndex = extendAttr.indexOf("<proportional>");
            int toIndex = extendAttr.indexOf("</proportional>");
            temp = extendAttr.substring(fromIndex + 14, toIndex).trim();

          }
        }
        if (flag == 5) {
          if (extendAttr.indexOf("<background>") == -1) {
            temp = "True";
          }
          else {
            int fromIndex = extendAttr.indexOf("<background>");
            int toIndex = extendAttr.indexOf("</background>");
            temp = extendAttr.substring(fromIndex + 12, toIndex).trim();

          }
        }
      }
    }

    return temp;
  }

  public int getFitFontSize(int fontSize, float width, float height,
      String context) {
    width -= 5;
    height -= 5;
    float size =
        Integer.parseInt(String.valueOf(height / (fontSize + 3)).substring(0,
            String.valueOf(height / (fontSize + 3)).indexOf(".")))
            * Integer.parseInt(String.valueOf(width / (fontSize + 3))
                .substring(0,
                    String.valueOf(width / (fontSize + 3)).indexOf(".")));
    while (fontSize > 0 && context.getBytes().length / 2 > size) {
      fontSize = fontSize - 1;
      size =
          Integer.parseInt(String.valueOf(height / (fontSize + 3)).substring(0,
              String.valueOf(height / (fontSize + 3)).indexOf(".")))
              * Integer.parseInt(String.valueOf(width / (fontSize + 3))
                  .substring(0,
                      String.valueOf(width / (fontSize + 3)).indexOf(".")));
    }
    return fontSize;
  }

  // 得到参数在页面显示的html原码

  public RecordVo getGridVo(int gridno, String hz, int rleft, int rtop,
      int rwidth, int rheight, String nums, int flag1, int l, int r,
      int npercent) {
    RecordVo vo = new RecordVo("tgrid2");
    vo.setInt("gridno", gridno);
    vo.setString("hz", hz);
    vo.setInt("rleft", rleft);
    vo.setInt("rtop", rtop);
    vo.setInt("rwidth", rwidth);
    vo.setInt("rheight", rheight);
    vo.setInt("l", l);
    vo.setInt("t", 1);
    vo.setInt("r", r);
    vo.setInt("b", 1);
    vo.setInt("sl", 0);
    vo.setString("cfactor", "");
    vo.setInt("flag2", 1);
    vo.setInt("flag1", flag1);
    vo.setString("cexpr2", "");
    vo.setString("cexpr1", "");
    vo.setInt("scanmode", 1);
    vo.setInt("fontsize", 12);
    vo.setString("fontname", "宋体");
    vo.setInt("fonteffect", 1);
    if (nums.charAt(0) == 'a') {
      vo.setInt("flag", 1);
    }
    else {
      vo.setInt("flag", 2);
    }
    vo.setInt("align", 7);
    vo.setInt("lsize", 1);
    vo.setInt("rsize", 1);
    vo.setInt("tsize", 1);
    vo.setInt("bsize", 1);
    vo.setInt("npercent", npercent);
    vo.setString("archive_item", "");
    return vo;
  }

  /**
   * 得到项目格的区域
   * 
   * @param tabid
   * @param gridInfoList
   *          报表的所有列集合
   * @return
   */
  public int[] getItemGridID(String tabid, Connection conn, String nums,
      String[] right_fields) throws GeneralException {
    int[] area = new int[4];
    ContentDAO dao = new ContentDAO(conn);
    RowSet recset = null;
    try {
      recset =
          dao.search("select * from tgrid3 where tabid=" + tabid
              + " and flag=0");
      if (recset.next()) {
        area[0] = recset.getInt("rleft");
        area[1] = recset.getInt("rtop");
        area[2] = recset.getInt("rwidth");
        area[3] = recset.getInt("rheight");

      }

      int rwidth = 0;
      int rheight = 0;
      recset =
          dao.search("select * from tgrid2 where tabid=" + tabid
              + " and flag=0");
      if (recset.next()) {
        rwidth = recset.getInt("rwidth");
        rheight = recset.getInt("rheight");
      }
      String atemp = nums.substring(3);
      String[] temps = atemp.split(",");
      if (nums.indexOf(",a") != -1) // 列
      {
        area[2] = 60 * (right_fields.length * temps.length + 1) + rwidth;
      }
      else {
        area[3] =
            this.constantNum * (right_fields.length * temps.length + 1)
                + rheight;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
    /*
     * finally { try { if(recset!=null) recset.close(); } catch(Exception e) {
     * e.printStackTrace(); } }
     */
    return area;
  }

  public String getL_R(TnameBo tnameBo) {
    String lr = "";
    ArrayList gridList = tnameBo.getGridList();
    RecordVo vo_l = null;
    for (int i = 0; i < gridList.size(); i++) {
      RecordVo tempVo = (RecordVo) gridList.get(i);
      if (tempVo.getInt("rleft") == tnameBo.getItemGridArea()[0]
          && tempVo.getInt("rtop") + tempVo.getInt("rheight") == tnameBo
              .getItemGridArea()[1]
              + tnameBo.getItemGridArea()[3]) {
        vo_l = tempVo;
        break;
      }
    }
    RecordVo vo_r =
        (RecordVo) tnameBo.getRowInfoBGrid().get(
            tnameBo.getRowInfoBGrid().size() - 1);
    lr = vo_l.getInt("l") + "_" + vo_r.getInt("r");
    return lr;
  }

  // 根据条件生成新的报表格式信息 [0]:a_rowInfoBGrid [1]:b_colInfoBGrid [2]:a_gridList
  public ArrayList getNewGridList(TnameBo tnameBo, String nums,
      String[] condition) {
    ArrayList list = new ArrayList();
    ArrayList a_rowInfoBGrid = new ArrayList();
    ArrayList b_colInfoBGrid = new ArrayList();
    /* 根据选中的列行条件 替换单元格对象 */
    ArrayList a_gridList = new ArrayList();

    String lr = this.getL_R(tnameBo);
    int constantNum = this.getConstatnNum(condition);

    for (Iterator t = tnameBo.getGridList().iterator(); t.hasNext();) {
      RecordVo temp = (RecordVo) t.next();
      if (nums.indexOf(",a") != -1) // 列
      {
        if (temp.getInt("flag") != 1) {
          a_gridList.add(temp);
        }
      }
      else if (nums.indexOf(",b") != -1) // 行
      {

        if (temp.getInt("flag") != 2) {
          if (!"1".equals(this.exceflag) && tnameBo.getItemGridArea()[2] < 100) {
            int rl = temp.getInt("rleft");
            if (temp.getInt("flag") == 0) {
              temp.setInt("rwidth", 100 + temp.getInt("rwidth"));
            }
            else {
              temp.setInt("rleft", 100 + rl);
            }
          }
          a_gridList.add(temp);
        }
      }
    }
    int rleft = 0;
    int rtop = 0;
    int rwidth = 0;
    int rwidth2 = 0;
    int rwidth3 = 0;
    int constantNum2 = 0;
    int constantNum3 = 0;
    int rheight = 0;
    String hz = "";
    int l = 1;
    int r = 1;
    if (nums.indexOf(",a") != -1) // 列
    {
      rleft = tnameBo.getItemGridArea()[0] + tnameBo.getItemGridArea()[2];
      rtop = tnameBo.getItemGridArea()[1];
      rwidth = 60; // constantNum;
      rwidth2 = 60;
      rwidth3 = 60;

      rheight = tnameBo.getItemGridArea()[3];//xiegh [61, 77, 363, 60]
      if (rheight < 60) {
        rheight = 60;
      }
      constantNum2 = rheight;
      constantNum3 = rheight;
      hz = ResourceFactory.getProperty("report.number");
    }
    else if (nums.indexOf(",b") != -1) // 行
    {
      rleft = tnameBo.getItemGridArea()[0];
      rtop = tnameBo.getItemGridArea()[1] + tnameBo.getItemGridArea()[3];
      rwidth = tnameBo.getItemGridArea()[2];
      if (!"1".equals(this.exceflag) && rwidth < 100) {
        rwidth = rwidth + 100;
      }

      rwidth2 = rwidth;
      rwidth3 = rwidth;
      rheight = constantNum;
      constantNum2 = constantNum;
      constantNum3 = constantNum;
      if (lr.charAt(0) == '0') {
        l = 0;
      }
      r = 1;
      hz = "甲";
    }
    ArrayList rowInfoBGrid = tnameBo.getRowInfoBGrid();
    ArrayList colInfoBGrid = tnameBo.getColInfoBGrid();

    RecordVo vo =
        this.getGridVo(100000, hz, rleft, rtop, rwidth, rheight, nums
            .substring(3), 4, l, r, 0);
    a_gridList.add(vo);
    if (nums.indexOf(",a") != -1) {
      a_rowInfoBGrid.add(vo);
    }
    else {
      b_colInfoBGrid.add(vo);
    }

    String atemp = nums.substring(3);
    String[] temps = atemp.split(",");
    boolean flag = false;
    for (int i = 0; i < condition.length; i++) {
      String a_condition = condition[i];
      String[] condition_arr = a_condition.split(":");
      String contextAnalyse = "";
  	  if(flag)//xiegh 20170620 如果是按归档时间汇总，下面的condition取[1],反之，取[2]
      {
          contextAnalyse= condition_arr[1];
      } else {
          contextAnalyse= condition_arr[2];
      }
      if (temps.length > 0)// xgq 2010 01 15 修改1为0
      {
        l = 1;
        r = 1;
        String temp = "";
        int a_rwidth = rwidth2;
        int a_rheigth = rheight;
        int a_rleft = rleft;
        int a_rtop = rtop;
        if (nums.indexOf(",a") != -1) // 列
        {
          // 设置宽度 防止选择单位模糊不清
          String tempname = condition_arr[1];
          String numstemp[] = nums.split(",");
          a_rleft += rwidth2;
          if (numstemp.length == 2) {// 1列
            if (tempname.length() < 13) {
              rwidth2 = 240;
            }
            else if (tempname.length() < 20) {
              rwidth2 = 300;
            }
            else {
              rwidth2 = 396;
            }
          }
          else if (numstemp.length == 3) {
            if (tempname.length() < 13) {
              rwidth2 = 120;
            }
            else if (tempname.length() < 20) {
              rwidth2 = 150;
            }
            else {
              rwidth2 = 183;
            }

          }
          else if (numstemp.length == 4) {
            if (tempname.length() < 13) {
              rwidth2 = 80;
            }
            else if (tempname.length() < 20) {
              rwidth2 = 100;
            }
            else {
              rwidth2 = 132;
            }
          }
          else if (numstemp.length == 5) {// 4列
            if (tempname.length() < 13) {
              rwidth2 = 60;
            }
            else if (tempname.length() < 20) {
              rwidth2 = 75;
            }
            else {
              rwidth2 = 99;
            }
          }
          else {
            rwidth2 = 60;
          }

          a_rwidth = rwidth2;
          if (i == condition.length - 1 && lr.charAt(2) == '0') {
            r = 0;
          }
          temp = "a";
          a_rwidth = temps.length * a_rwidth;
          a_rheigth = a_rheigth / 2 - 15;
        }
        else {
          a_rtop += constantNum2;
          if (lr.charAt(0) == '0') {
            l = 0;
          }
          temp = "b";
          //
          if (temps.length == 1) {
            if (condition_arr[1].getBytes().length <= 18) {
              constantNum2 = 60;
            }
            else if (condition_arr[1].getBytes().length <= 30) {
              constantNum2 = 100;
            }
            else if (condition_arr[1].getBytes().length <= 42) {
              constantNum2 = 120;
            }
            else {
              constantNum2 = 120;
            }

          }
          else if (temps.length == 2) {
            // if(condition_arr[1].getBytes().length<=42&&condition_arr[1].getBytes().length>30){
            // constantNum2=70;
            // }else if(condition_arr[1].getBytes().length>42) {
            // constantNum2=90;
            // }else{
            // constantNum2=60;
            // }
            constantNum2 = 60;
          }
          else {
            constantNum2 = 60;
          }
          a_rheigth = constantNum2 * temps.length;
          if (!"1".equals(this.exceflag) && tnameBo.getItemGridArea()[2] < 100) {
            a_rwidth = 100;
          }
          else {
            a_rwidth = a_rwidth / 2 - 15;
          }
        }

        
        if (contextAnalyse.indexOf(";") != -1) {// xgq 2010 01 18
          String[] contextAnalyse2 = contextAnalyse.split(";");
          if (contextAnalyse2.length == 3 || contextAnalyse2.length == 4) {
            flag = true;
            if ("1".equals(contextAnalyse2[2])) {
              contextAnalyse =
                  contextAnalyse2[0]
                      + ResourceFactory.getProperty("columns.archive.year")
                      + contextAnalyse2[1]
                      + ResourceFactory.getProperty("hmuster.label.count");
            }
            else if ("2".equals(contextAnalyse2[2])) {
              contextAnalyse =
                  contextAnalyse2[0]
                      + ResourceFactory.getProperty("columns.archive.year");
            }
            if ("3".equals(contextAnalyse2[2])) {
              if ("1".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory
                            .getProperty("report.pigeonhole.uphalfyear");
              }
              else {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory
                            .getProperty("report.pigeonhole.downhalfyear");
              }
            }
            if ("4".equals(contextAnalyse2[2])) {
              if ("1".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory
                            .getProperty("report.pigionhole.oneQuarter");
              }
              else if ("2".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory
                            .getProperty("report.pigionhole.twoQuarter");
              }
              else if ("3".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory
                            .getProperty("report.pigionhole.threeQuarter");
              }
              else if ("4".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory
                            .getProperty("report.pigionhole.fourQuarter");
              }
            }
            if ("5".equals(contextAnalyse2[2])) {
              if ("1".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.january");
              }
              else if ("2".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.february");
              }
              else if ("3".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.march");
              }
              else if ("4".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.april");
              }
              else if ("5".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.may");
              }
              else if ("6".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.june");
              }
              else if ("7".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.july");
              }
              else if ("8".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.auguest");
              }
              else if ("9".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.september");
              }
              else if ("10".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.october");
              }
              else if ("11".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.november");
              }
              else if ("12".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.december");
              }

            }
            if ("6".equals(contextAnalyse2[2])) {
              if ("1".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.january");
              }
              else if ("2".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.february");
              }
              else if ("3".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.march");
              }
              else if ("4".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.april");
              }
              else if ("5".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.may");
              }
              else if ("6".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.june");
              }
              else if ("7".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.july");
              }
              else if ("8".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.auguest");
              }
              else if ("9".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.september");
              }
              else if ("10".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.october");
              }
              else if ("11".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.november");
              }
              else if ("12".equals(contextAnalyse2[1])) {
                contextAnalyse =
                    contextAnalyse2[0]
                        + ResourceFactory.getProperty("columns.archive.year")
                        + ResourceFactory.getProperty("date.month.december");
              }
              if ("1".equals(contextAnalyse2[3])) {
                contextAnalyse +=
                    ResourceFactory
                        .getProperty("performance.workdiary.one.week");
              }
              else if ("2".equals(contextAnalyse2[3])) {
                contextAnalyse +=
                    ResourceFactory
                        .getProperty("performance.workdiary.two.week");
              }
              else if ("3".equals(contextAnalyse2[3])) {
                contextAnalyse +=
                    ResourceFactory
                        .getProperty("performance.workdiary.three.week");
              }
              else if ("4".equals(contextAnalyse2[3])) {
                contextAnalyse +=
                    ResourceFactory
                        .getProperty("performance.workdiary.four.week");
              }
              else if ("5".equals(contextAnalyse2[3])) {
                contextAnalyse +=
                    ResourceFactory
                        .getProperty("performance.workdiary.five.week");
              }
              else if ("6".equals(contextAnalyse2[3])) {
                contextAnalyse +=
                    ResourceFactory
                        .getProperty("performance.workdiary.six.week");
              }
            }
            if ("9".equals(contextAnalyse2[2])&& "9".equals(contextAnalyse2[1])) {//如果已归档时间去生成综合报表，先选择总计，报表标题会出现问题：显示成归档时间。对总计进行特殊处理 20170624 bug：28986
            	contextAnalyse="总计";
            }
          }
        }
        if (flag) {
          RecordVo a_vo =
              this.getGridVo(100000 + i + 1, contextAnalyse, a_rleft, a_rtop,
                  a_rwidth, a_rheigth, temp, 1, l, r, 0);
          a_gridList.add(a_vo);
        }
        else {
          RecordVo a_vo =
              this.getGridVo(100000 + i + 1, condition_arr[1], a_rleft, a_rtop,
                  a_rwidth, a_rheigth, temp, 1, l, r, 0);
          a_gridList.add(a_vo);
        }

      }

      for (int j = 0; j < temps.length; j++) {
        String a_hz = "";
        int npercent = 0;
        l = 1;
        r = 1;
        if (nums.indexOf(",a") != -1) // 列
        {
          String tempname = condition_arr[1];
          String numstemp[] = nums.split(",");
          rleft += rwidth3;
          if (numstemp.length == 2) {// 1列
            if (tempname.length() < 13) {
              rwidth3 = 240;
            }
            else if (tempname.length() < 20) {
              rwidth3 = 300;
            }
            else {
              rwidth3 = 396;
            }
          }
          else if (numstemp.length == 3) {
            if (tempname.length() < 13) {
              rwidth3 = 120;
            }
            else if (tempname.length() < 20) {
              rwidth3 = 150;
            }
            else {
              rwidth3 = 183;
            }

          }
          else if (numstemp.length == 4) {
            if (tempname.length() < 13) {
              rwidth3 = 80;
            }
            else if (tempname.length() < 20) {
              rwidth3 = 100;
            }
            else {
              rwidth3 = 132;
            }
          }
          else if (numstemp.length == 5) {// 4列
            if (tempname.length() < 13) {
              rwidth3 = 60;
            }
            else if (tempname.length() < 20) {
              rwidth3 = 75;
            }
            else {
              rwidth3 = 99;
            }
          }
          else {
            rwidth3 = 60;
          }

          if (i == condition.length - 1 && lr.charAt(2) == '0') {
            r = 0;
          }
          RecordVo a_vo =
              (RecordVo) rowInfoBGrid.get(Integer.parseInt(temps[j]
                  .substring(1)));
          a_hz = a_vo.getString("hz");
          npercent = a_vo.getInt("npercent");
        }
        else {
          rtop += constantNum3;

          if (temps.length == 1) {
            if (condition_arr[1].getBytes().length <= 18) {
              constantNum3 = 60;
            }
            else if (condition_arr[1].getBytes().length <= 30) {
              constantNum3 = 100;
            }
            else if (condition_arr[1].getBytes().length <= 42) {
              constantNum3 = 120;
            }
            else {
              constantNum3 = 120;
            }

          }
          else if (temps.length == 2) {
            // if(condition_arr[1].getBytes().length<=42&&condition_arr[1].getBytes().length>30){
            // constantNum3=70;
            // }else if(condition_arr[1].getBytes().length>42) {
            // constantNum3=90;
            // }else{
            // constantNum3=60;
            // }
            constantNum3 = 60;
          }
          else {
            constantNum3 = 60;
          }
          if (lr.charAt(0) == '0') {
            l = 0;
          }

          RecordVo a_vo =
              (RecordVo) colInfoBGrid.get(Integer.parseInt(temps[j]
                  .substring(1)));
          a_hz = a_vo.getString("hz");
          npercent = a_vo.getInt("npercent");
        }
        RecordVo a_vo = null;
        if (temps.length > 0)// xgq 修改 由1改为0
        {
          if (nums.indexOf(",a") != -1) // 列
          {
            a_vo =
                this.getGridVo(100000 + i + 1, a_hz, rleft, rtop + constantNum3
                    / 2 - 15, rwidth3, constantNum3 / 2 + 15, temps[j], 1, l,
                    r, npercent);
          }
          else {
            if (!"1".equals(this.exceflag)
                && tnameBo.getItemGridArea()[2] < 100) {
              a_vo =
                  this.getGridVo(100000 + i + 1, a_hz, rleft + 100, rtop,
                      rwidth3 - 100, constantNum3, temps[j], 1, 1, r, npercent);
            }
            else {//xiegh add 20170818 bug:30580  这个问题的出现 是因为前人在改了bug:12356后出现的  他是将table的css右边线去除了 12356没了 但是出现这个问题   12356：由于线重合 现在将行标题宽度+1 最终： rwidth3 / 2 + 16
              a_vo =
                  this.getGridVo(100000 + i + 1, a_hz,
                      rleft + rwidth3 / 2 - 15, rtop, rwidth3 / 2 + 16,
                      constantNum3, temps[j], 1, 1, r, npercent);
            }
          }
        }
        else {
          a_vo =
              this.getGridVo(100000 + i + 1, condition_arr[1], rleft, rtop,
                  rwidth3, constantNum3, temps[j], 1, l, r, 0);
        }

        if (nums.indexOf(",a") != -1) // 列
        {
          a_rowInfoBGrid.add(a_vo);

        }
        else {
          b_colInfoBGrid.add(a_vo);
        }
        a_gridList.add(a_vo);
      }

    }
    if (a_rowInfoBGrid.size() == 0) {
      a_rowInfoBGrid = tnameBo.getRowInfoBGrid();
    }
    if (b_colInfoBGrid.size() == 0) {
      b_colInfoBGrid = tnameBo.getColInfoBGrid();
    }
    list.add(a_rowInfoBGrid);
    list.add(b_colInfoBGrid);
    list.add(a_gridList);
    list.add(rleft + rwidth2 + "");
    list.add(rtop + constantNum3 + "");
    return list;
  }

  public String getParamHtml(HashMap param_map) {
    StringBuffer sb = new StringBuffer("");
    if (param_map != null) {
      if (((String) param_map.get("paramtype")).equals(ResourceFactory
          .getProperty("kq.wizard.zifu"))) {
        sb.append("<input type='text' name=");
        sb.append((String) param_map.get("paramename"));
        sb.append(" maxlength='");
        sb.append((String) param_map.get("paramlen") + "'");
        sb.append(" readOnly ");
        sb.append(" size='13' value='");
        sb.append((String) param_map.get("a_value"));
        sb.append("'class='text'>");
      }
      else if (((String) param_map.get("paramtype")).equals(ResourceFactory
          .getProperty("orglist.reportunitlist.code"))) {
        String[] values = new String[2];
        if (((String) param_map.get("a_value")).indexOf("/") != -1) {
          values = ((String) param_map.get("a_value")).split("/");
        }
        else {
          values[0] = "";
          values[1] = "";
        }
        sb.append("<Input type='hidden' value='" + values[0] + "' name='"
            + (String) param_map.get("paramename")
            + ".value' /><input type=text name='"
            + (String) param_map.get("paramename") + ".hzvalue' ");
        sb.append(" disabled='false'");
        sb.append(" value='" + values[1]
            + "' size=\"13\" class='text' readOnly />  ");
      }
      else if (((String) param_map.get("paramtype")).equals(ResourceFactory
          .getProperty("report.parse.d"))) {
        sb.append("<Input type='text' class='text' name='"
            + (String) param_map.get("paramename") + "' ");
        sb.append(" disabled='false'");
        sb.append(" value='" + (String) param_map.get("a_value")
            + "'  size=\"13\" readOnly />");
      }
      else if (((String) param_map.get("paramtype")).equals(ResourceFactory
          .getProperty("kq.formula.counts"))) {
        sb.append("<input type='test' class='text' name='"
            + (String) param_map.get("paramename") + "' ");
        sb.append(" readOnly ");
        sb.append(" value='" + (String) param_map.get("a_value")
            + "' size=\"13\"  >");
      }
      else if (((String) param_map.get("paramtype")).equals(ResourceFactory
          .getProperty("report.parse.text"))) {// 2015-4-17 8846
        sb.append("<textarea  name='" + (String) param_map.get("paramename")
            + "' >");
        sb.append((String) param_map.get("a_value"));
        sb.append(" </textarea>");
      }
    }
    return sb.toString();

  }

  // 得到图片
  public String getParamPictureHtml(RecordVo vo, String fontSize) {
    File tempFile = null;
    StringBuffer sb = new StringBuffer("");
    if (vo.getObject("content") != null) {

      String extendattr = vo.getString("extendattr");
      String filename = "";
      ServletUtilities.createTempDir();
      String ext = this.getExtendAttrContext(1, extendattr);
      if (ext == null || "".equalsIgnoreCase(ext)) {
        return "";
      }
      String transparent = this.getExtendAttrContext(3, extendattr);
      if (transparent == null || "".equalsIgnoreCase(transparent)) {
        return "";
      }
      int tran = 40;
      if (!"True".equalsIgnoreCase(transparent)) {
        tran = 100;
      }
      String stretch = this.getExtendAttrContext(2, extendattr);
      if (stretch == null || "".equalsIgnoreCase(stretch)) {
        return "";
      }
      String stre =
          "width:" + vo.getInt("rwidth") + "px;height:" + vo.getInt("rheight")
              + "px;";
      if (!"True".equalsIgnoreCase(stretch)) {
        stre = "";
      }
      String proportional = this.getExtendAttrContext(4, extendattr);
      if (proportional == null || "".equalsIgnoreCase(proportional)) {
        return "";
      }

      if ("True".equalsIgnoreCase(stretch)
          && "True".equalsIgnoreCase(proportional)) {
        stre = "width:auto;height:" + vo.getInt("rheight") + "px;";
      }

      java.io.FileOutputStream fout = null;
      try {

        tempFile =
            File.createTempFile(ServletUtilities.tempFilePrefix, ext, new File(
                System.getProperty("java.io.tmpdir")));
        InputStream in = (InputStream) vo.getObject("content");
        if (in == null) {
          return "";
        }
        fout = new java.io.FileOutputStream(tempFile);
        int len;
        byte buf[] = new byte[1024];

        while ((len = in.read(buf, 0, 1024)) != -1) {
          fout.write(buf, 0, len);

        }

        filename = tempFile.getName();

      }
      catch (Exception e) {
        e.printStackTrace();
      }finally {
    	  PubFunc.closeResource(fout);
      }
      Properties props = System.getProperties(); // 系统属性
      String filepathname = tempFile.toString();
      if (props.getProperty("os.name").startsWith("Win")) {
        filepathname = filepathname.replace("\\", "/");
      }

      sb.append("<img  id='" + vo.getInt("tabid") + "_" + vo.getInt("gridno")
          + "'  ");
      sb.append("src='" + filepathname + "'");

      sb.append(" style='" + stre + "filter:alpha(opacity=" + tran + ");'  />");
    }

    return sb.toString();
  }

  /*
   * 处理页面显示虚线
   */
  public String getStyleName(RecordVo vo) {
    // 处理虚线 L,T,R,B,
    String style_name = "RecordRow_self";
    if (vo.getInt("l") == 0) {
      style_name = "RecordRow_self_l";
      if (vo.getInt("r") == 0) {
        style_name = "RecordRow_self_two";
      }
    }
    else if (vo.getInt("t") == 0) {
      style_name = "RecordRow_self_t";
    }
    else if (vo.getInt("r") == 0) {
      style_name = "RecordRow_self_r";
    }
    else if (vo.getInt("b") == 0) {
      style_name = "RecordRow_self_b";
    }
    return style_name;
  }

  public void setExceflag(String exceflag) {
    this.exceflag = exceflag;
  }

  public String transformDeedIndex(HashSet set) {
    StringBuffer lexpr = new StringBuffer("");
    for (Iterator t = set.iterator(); t.hasNext();) {
      String temp = (String) t.next();
      lexpr.append("," + temp);
    }
    return lexpr.toString();
  }

  /**
   * 将计算公式中左表达式中涉及到的号码映射成实际得数据结果集中的下标值
   * 
   * @param list
   * @param map
   * @return
   */
  public String transformDeedIndex(HashSet set, HashMap map) {
    StringBuffer lexpr = new StringBuffer("");
    for (Iterator t = set.iterator(); t.hasNext();) {
      String temp = (String) t.next();
      String num = (String) map.get(temp);
      if (num != null) {
        lexpr.append("," + num);
      }
    }
    return lexpr.toString();
  }

  /**
   * 转换成字符样式
   */
  private String getFontStyle(String fontStyle, int aFontSize) {
    String style = "";
    if ("2".equals(fontStyle)) {
      style = "font-weight：bold;font-size:" + aFontSize + "pt";
    }
    else if (fontStyle.endsWith("3")) {
      style = "font-style：italic;font-size:" + aFontSize + "pt";
    }
    else if ("4".equals(fontStyle)) {
      style =
          "font-style：italic;font-weight:bold;font-size：" + aFontSize + "pt";
    }
    else {
      style = "font-weight:normal;font-size:" + aFontSize + "pt";
    }
    return style;
  }

  /**
   * 转换成字体布局字符
   */
  private String[] transAlign(int Align) {
    String[] temp = new String[2];
    if (Align == 0) {
      temp[0] = "top";
      temp[1] = "left";
    }
    else if (Align == 1) {
      temp[0] = "top";
      temp[1] = "center";
    }
    else if (Align == 2) {
      temp[0] = "top";
      temp[1] = "right";
    }
    else if (Align == 3) {
      temp[0] = "bottom";
      temp[1] = "left";
    }
    else if (Align == 4) {
      temp[0] = "bottom";
      temp[1] = "center";
    }
    else if (Align == 5) {
      temp[0] = "bottom";
      temp[1] = "right";
    }
    else if (Align == 6) {
      temp[0] = "middle";
      temp[1] = "left";
    }
    else if (Align == 7) {
      temp[0] = "middle";
      temp[1] = "center";
    }
    else if (Align == 8) {
      temp[0] = "middle";
      temp[1] = "right";
    }
    return temp;
  }
}
