package com.hjsj.hrms.utils.components.tablefactory.model;

import com.google.gson.Gson;

import java.io.Serializable;


/**
 *
 * 导出页面设置模型
 * @author ZhangHua
 * @date 15:01 2019/12/27
 */
public class ExportSettingsModel implements Serializable {


    /**
     * 纸张方向
     */
    private String Pagetype;
    /**
     * 左边距
     */
    private int Left;
    /**
     * 上边距
     */
    private int Top;
    /**
     * 右边距
     */
    private int Right;
    /**
     * 下边距
     */
    private int Bottom;
    /**
     * 纸张方向
     * 0 横 1纵
     */
    private String Orientation;
    /**
     * 纸张高度
     */
    private int Height;
    /**
     * 宽度
     */
    private int Width;
    /**
     * 标题内容
     */
    private String title_content;
    /**
     * 标题字体
     */
    private String title_fontface;
    /**
     * 标题字号
     */
    private int title_fontsize;

    /**
     * 标题粗体
     */
    private String title_fontblob;
    /**
     * 标题下划线
     */
    private String title_underline;
    /**
     * 标题斜体
     */
    private String title_fontitalic;
    /**
     * 标题删除线
     */
    private String title_delline;
    /**
     * 标题颜色
     */
    private String title_color;


    /**
     * 上左
     */
    private String head_left;
    /**
     * 上中
     */
    private String head_center;
    /**
     * 上右
     */
    private String head_right;
    /**
     * 页头粗体
     */
    private String head_fontblob;
    /**
     * 页头下划线
     */
    private String head_underline;
    /**
     * 页头斜体
     */
    private String head_fontitalic;
    /**
     * 页头删除线
     */
    private String head_delline;
    /**
     * 页头字体
     */
    private String head_fontface;
    /**
     * 页头字体大小
     */
    private int head_fontsize;
    /**
     * 页头显示
     */
    private String head_fc;
    /**
     * 上左内容仅首页显示
     */
    private String head_flw_hs;
    /**
     * 上中内容仅首页显示
     */
    private String head_fmw_hs;
    /**
     * 上右内容仅首页显示
     */
    private String head_frw_hs;

    /**
     * 下左
     */
    private String tail_left;
    /**
     * 下中
     */
    private String tail_center;
    /**
     * 下右
     */
    private String tail_right;
    /**
     * 页尾字体
     */
    private String tail_fontface;
    /**
     * 页尾字号
     */
    private int tail_fontsize;
    /**
     * 页尾粗体
     */
    private String tail_fontblob;
    /**
     * 页尾下划线
     */
    private String tail_underline;
    /**
     * 页尾斜体
     */
    private String tail_fontitalic;
    /**
     * 页尾删除线
     */
    private String tail_delline;
    /**
     * 页尾显示
     */
    private String tail_fc;
    /**
     * 页尾下左内容仅首页显示
     */
    private String tail_flw_hs;
    /**
     * 页尾下中内容仅首页显示
     */
    private String tail_fmw_hs;
    /**
     * 页尾下右内容仅首页显示
     */
    private String tail_frw_hs;

    /**
     * 正文字体
     */
    private String text_fn;
    /**
     * 正文字号
     */
    private int text_fz;
    /**
     * 粗体 #fb[1]为粗体
     */
    private String text_fb;
    /**
     * 下划线 #fu[1] 为下划线
     */
    private String text_fu;
    /**
     * 斜体 #fi[1] 为斜体
     */
    private String text_fi;
    /**
     * 正文颜色
     */
    private String text_fc;

    /**
     * 表头字体
     */
    private String phead_fn;
    /**
     * 表头字号
     */
    private int phead_fz;
    /**
     * 表头粗体 #fb[1]为粗体
     */
    private String phead_fb;
    /**
     * 表头下划线 #fu[1] 为下划线
     */
    private String phead_fu;
    /**
     * 表头斜体 #fi[1] 为斜体
     */
    private String phead_fi;
    /**
     * 表头颜色
     */
    private String phead_fc;


    public ExportSettingsModel(){}

    public String toJson(){
        Gson gson=new Gson();

        return gson.toJson(this);
    }

    /**
     * 获取 纸张方向
     *
     * @return Pagetype 纸张方向
     */
    public String getPagetype() {
        return this.Pagetype;
    }

    /**
     * 设置 纸张方向
     *
     * @param Pagetype 纸张方向
     */
    public void setPagetype(String Pagetype) {
        this.Pagetype = Pagetype;
    }

    /**
     * 获取 左边距
     *
     * @return Left 左边距
     */
    public int getLeft() {
        return this.Left;
    }

    /**
     * 设置 左边距
     *
     * @param Left 左边距
     */
    public void setLeft(int Left) {
        this.Left = Left;
    }

    /**
     * 获取 上边距
     *
     * @return Top 上边距
     */
    public int getTop() {
        return this.Top;
    }

    /**
     * 设置 上边距
     *
     * @param Top 上边距
     */
    public void setTop(int Top) {
        this.Top = Top;
    }

    /**
     * 获取 右边距
     *
     * @return Right 右边距
     */
    public int getRight() {
        return this.Right;
    }

    /**
     * 设置 右边距
     *
     * @param Right 右边距
     */
    public void setRight(int Right) {
        this.Right = Right;
    }

    /**
     * 获取 下边距
     *
     * @return Bottom 下边距
     */
    public int getBottom() {
        return this.Bottom;
    }

    /**
     * 设置 下边距
     *
     * @param Bottom 下边距
     */
    public void setBottom(int Bottom) {
        this.Bottom = Bottom;
    }

    /**
     * 获取 纸张方向      0 横 1纵
     *
     * @return Orientation 纸张方向      0 横 1纵
     */
    public String getOrientation() {
        return this.Orientation;
    }

    /**
     * 设置 纸张方向      0 横 1纵
     *
     * @param Orientation 纸张方向      0 横 1纵
     */
    public void setOrientation(String Orientation) {
        this.Orientation = Orientation;
    }

    /**
     * 获取 纸张高度
     *
     * @return Height 纸张高度
     */
    public int getHeight() {
        return this.Height;
    }

    /**
     * 设置 纸张高度
     *
     * @param Height 纸张高度
     */
    public void setHeight(int Height) {
        this.Height = Height;
    }

    /**
     * 获取 宽度
     *
     * @return Width 宽度
     */
    public int getWidth() {
        return this.Width;
    }

    /**
     * 设置 宽度
     *
     * @param Width 宽度
     */
    public void setWidth(int Width) {
        this.Width = Width;
    }

    /**
     * 获取 标题内容
     *
     * @return title_content 标题内容
     */
    public String getTitle_content() {
        return this.title_content;
    }

    /**
     * 设置 标题内容
     *
     * @param title_content 标题内容
     */
    public void setTitle_content(String title_content) {
        this.title_content = title_content;
    }

    /**
     * 获取 标题字体
     *
     * @return title_fontface 标题字体
     */
    public String getTitle_fontface() {
        return this.title_fontface;
    }

    /**
     * 设置 标题字体
     *
     * @param title_fontface 标题字体
     */
    public void setTitle_fontface(String title_fontface) {
        this.title_fontface = title_fontface;
    }

    /**
     * 获取 标题字号
     *
     * @return title_fontsize 标题字号
     */
    public int getTitle_fontsize() {
        return this.title_fontsize;
    }

    /**
     * 设置 标题字号
     *
     * @param title_fontsize 标题字号
     */
    public void setTitle_fontsize(int title_fontsize) {
        this.title_fontsize = title_fontsize;
    }

    /**
     * 获取 标题粗体
     *
     * @return title_fontblob 标题粗体
     */
    public String getTitle_fontblob() {
        return this.title_fontblob;
    }

    /**
     * 设置 标题粗体
     *
     * @param title_fontblob 标题粗体
     */
    public void setTitle_fontblob(String title_fontblob) {
        this.title_fontblob = title_fontblob;
    }

    /**
     * 获取 标题下划线
     *
     * @return title_underline 标题下划线
     */
    public String getTitle_underline() {
        return this.title_underline;
    }

    /**
     * 设置 标题下划线
     *
     * @param title_underline 标题下划线
     */
    public void setTitle_underline(String title_underline) {
        this.title_underline = title_underline;
    }

    /**
     * 获取 标题斜体
     *
     * @return title_fontitalic 标题斜体
     */
    public String getTitle_fontitalic() {
        return this.title_fontitalic;
    }

    /**
     * 设置 标题斜体
     *
     * @param title_fontitalic 标题斜体
     */
    public void setTitle_fontitalic(String title_fontitalic) {
        this.title_fontitalic = title_fontitalic;
    }

    /**
     * 获取 标题删除线
     *
     * @return title_delline 标题删除线
     */
    public String getTitle_delline() {
        return this.title_delline;
    }

    /**
     * 设置 标题删除线
     *
     * @param title_delline 标题删除线
     */
    public void setTitle_delline(String title_delline) {
        this.title_delline = title_delline;
    }

    /**
     * 获取 标题颜色
     *
     * @return title_color 标题颜色
     */
    public String getTitle_color() {
        return this.title_color;
    }

    /**
     * 设置 标题颜色
     *
     * @param title_color 标题颜色
     */
    public void setTitle_color(String title_color) {
        this.title_color = title_color;
    }

    /**
     * 获取 上左
     *
     * @return head_left 上左
     */
    public String getHead_left() {
        return this.head_left;
    }

    /**
     * 设置 上左
     *
     * @param head_left 上左
     */
    public void setHead_left(String head_left) {
        this.head_left = head_left;
    }

    /**
     * 获取 上中
     *
     * @return head_center 上中
     */
    public String getHead_center() {
        return this.head_center;
    }

    /**
     * 设置 上中
     *
     * @param head_center 上中
     */
    public void setHead_center(String head_center) {
        this.head_center = head_center;
    }

    /**
     * 获取 上右
     *
     * @return head_right 上右
     */
    public String getHead_right() {
        return this.head_right;
    }

    /**
     * 设置 上右
     *
     * @param head_right 上右
     */
    public void setHead_right(String head_right) {
        this.head_right = head_right;
    }

    /**
     * 获取 页头粗体
     *
     * @return head_fontblob 页头粗体
     */
    public String getHead_fontblob() {
        return this.head_fontblob;
    }

    /**
     * 设置 页头粗体
     *
     * @param head_fontblob 页头粗体
     */
    public void setHead_fontblob(String head_fontblob) {
        this.head_fontblob = head_fontblob;
    }

    /**
     * 获取 页头下划线
     *
     * @return head_underline 页头下划线
     */
    public String getHead_underline() {
        return this.head_underline;
    }

    /**
     * 设置 页头下划线
     *
     * @param head_underline 页头下划线
     */
    public void setHead_underline(String head_underline) {
        this.head_underline = head_underline;
    }

    /**
     * 获取 页头斜体
     *
     * @return head_fontitalic 页头斜体
     */
    public String getHead_fontitalic() {
        return this.head_fontitalic;
    }

    /**
     * 设置 页头斜体
     *
     * @param head_fontitalic 页头斜体
     */
    public void setHead_fontitalic(String head_fontitalic) {
        this.head_fontitalic = head_fontitalic;
    }

    /**
     * 获取 页头删除线
     *
     * @return head_delline 页头删除线
     */
    public String getHead_delline() {
        return this.head_delline;
    }

    /**
     * 设置 页头删除线
     *
     * @param head_delline 页头删除线
     */
    public void setHead_delline(String head_delline) {
        this.head_delline = head_delline;
    }

    /**
     * 获取 页头字体
     *
     * @return head_fontface 页头字体
     */
    public String getHead_fontface() {
        return this.head_fontface;
    }

    /**
     * 设置 页头字体
     *
     * @param head_fontface 页头字体
     */
    public void setHead_fontface(String head_fontface) {
        this.head_fontface = head_fontface;
    }

    /**
     * 获取 页头字体大小
     *
     * @return head_fontsize 页头字体大小
     */
    public int getHead_fontsize() {
        return this.head_fontsize;
    }

    /**
     * 设置 页头字体大小
     *
     * @param head_fontsize 页头字体大小
     */
    public void setHead_fontsize(int head_fontsize) {
        this.head_fontsize = head_fontsize;
    }

    /**
     * 获取 页头显示
     *
     * @return head_fc 页头显示
     */
    public String getHead_fc() {
        return this.head_fc;
    }

    /**
     * 设置 页头显示
     *
     * @param head_fc 页头显示
     */
    public void setHead_fc(String head_fc) {
        this.head_fc = head_fc;
    }

    /**
     * 获取 上左内容仅首页显示
     *
     * @return head_flw_hs 上左内容仅首页显示
     */
    public String getHead_flw_hs() {
        return this.head_flw_hs;
    }

    /**
     * 设置 上左内容仅首页显示
     *
     * @param head_flw_hs 上左内容仅首页显示
     */
    public void setHead_flw_hs(String head_flw_hs) {
        this.head_flw_hs = head_flw_hs;
    }

    /**
     * 获取 上中内容仅首页显示
     *
     * @return head_fmw_hs 上中内容仅首页显示
     */
    public String getHead_fmw_hs() {
        return this.head_fmw_hs;
    }

    /**
     * 设置 上中内容仅首页显示
     *
     * @param head_fmw_hs 上中内容仅首页显示
     */
    public void setHead_fmw_hs(String head_fmw_hs) {
        this.head_fmw_hs = head_fmw_hs;
    }

    /**
     * 获取 上右内容仅首页显示
     *
     * @return head_frw_hs 上右内容仅首页显示
     */
    public String getHead_frw_hs() {
        return this.head_frw_hs;
    }

    /**
     * 设置 上右内容仅首页显示
     *
     * @param head_frw_hs 上右内容仅首页显示
     */
    public void setHead_frw_hs(String head_frw_hs) {
        this.head_frw_hs = head_frw_hs;
    }

    /**
     * 获取 下左
     *
     * @return tail_left 下左
     */
    public String getTail_left() {
        return this.tail_left;
    }

    /**
     * 设置 下左
     *
     * @param tail_left 下左
     */
    public void setTail_left(String tail_left) {
        this.tail_left = tail_left;
    }

    /**
     * 获取 下中
     *
     * @return tail_center 下中
     */
    public String getTail_center() {
        return this.tail_center;
    }

    /**
     * 设置 下中
     *
     * @param tail_center 下中
     */
    public void setTail_center(String tail_center) {
        this.tail_center = tail_center;
    }

    /**
     * 获取 下右
     *
     * @return tail_right 下右
     */
    public String getTail_right() {
        return this.tail_right;
    }

    /**
     * 设置 下右
     *
     * @param tail_right 下右
     */
    public void setTail_right(String tail_right) {
        this.tail_right = tail_right;
    }

    /**
     * 获取 页尾字体
     *
     * @return tail_fontface 页尾字体
     */
    public String getTail_fontface() {
        return this.tail_fontface;
    }

    /**
     * 设置 页尾字体
     *
     * @param tail_fontface 页尾字体
     */
    public void setTail_fontface(String tail_fontface) {
        this.tail_fontface = tail_fontface;
    }

    /**
     * 获取 页尾字号
     *
     * @return tail_fontsize 页尾字号
     */
    public int getTail_fontsize() {
        return this.tail_fontsize;
    }

    /**
     * 设置 页尾字号
     *
     * @param tail_fontsize 页尾字号
     */
    public void setTail_fontsize(int tail_fontsize) {
        this.tail_fontsize = tail_fontsize;
    }

    /**
     * 获取 页尾粗体
     *
     * @return tail_fontblob 页尾粗体
     */
    public String getTail_fontblob() {
        return this.tail_fontblob;
    }

    /**
     * 设置 页尾粗体
     *
     * @param tail_fontblob 页尾粗体
     */
    public void setTail_fontblob(String tail_fontblob) {
        this.tail_fontblob = tail_fontblob;
    }

    /**
     * 获取 页尾下划线
     *
     * @return tail_underline 页尾下划线
     */
    public String getTail_underline() {
        return this.tail_underline;
    }

    /**
     * 设置 页尾下划线
     *
     * @param tail_underline 页尾下划线
     */
    public void setTail_underline(String tail_underline) {
        this.tail_underline = tail_underline;
    }

    /**
     * 获取 页尾斜体
     *
     * @return tail_fontitalic 页尾斜体
     */
    public String getTail_fontitalic() {
        return this.tail_fontitalic;
    }

    /**
     * 设置 页尾斜体
     *
     * @param tail_fontitalic 页尾斜体
     */
    public void setTail_fontitalic(String tail_fontitalic) {
        this.tail_fontitalic = tail_fontitalic;
    }

    /**
     * 获取 页尾删除线
     *
     * @return tail_delline 页尾删除线
     */
    public String getTail_delline() {
        return this.tail_delline;
    }

    /**
     * 设置 页尾删除线
     *
     * @param tail_delline 页尾删除线
     */
    public void setTail_delline(String tail_delline) {
        this.tail_delline = tail_delline;
    }

    /**
     * 获取 页尾显示
     *
     * @return tail_fc 页尾显示
     */
    public String getTail_fc() {
        return this.tail_fc;
    }

    /**
     * 设置 页尾显示
     *
     * @param tail_fc 页尾显示
     */
    public void setTail_fc(String tail_fc) {
        this.tail_fc = tail_fc;
    }

    /**
     * 获取 页尾下左内容仅首页显示
     *
     * @return tail_flw_hs 页尾下左内容仅首页显示
     */
    public String getTail_flw_hs() {
        return this.tail_flw_hs;
    }

    /**
     * 设置 页尾下左内容仅首页显示
     *
     * @param tail_flw_hs 页尾下左内容仅首页显示
     */
    public void setTail_flw_hs(String tail_flw_hs) {
        this.tail_flw_hs = tail_flw_hs;
    }

    /**
     * 获取 页尾下中内容仅首页显示
     *
     * @return tail_fmw_hs 页尾下中内容仅首页显示
     */
    public String getTail_fmw_hs() {
        return this.tail_fmw_hs;
    }

    /**
     * 设置 页尾下中内容仅首页显示
     *
     * @param tail_fmw_hs 页尾下中内容仅首页显示
     */
    public void setTail_fmw_hs(String tail_fmw_hs) {
        this.tail_fmw_hs = tail_fmw_hs;
    }

    /**
     * 获取 页尾下右内容仅首页显示
     *
     * @return tail_frw_hs 页尾下右内容仅首页显示
     */
    public String getTail_frw_hs() {
        return this.tail_frw_hs;
    }

    /**
     * 设置 页尾下右内容仅首页显示
     *
     * @param tail_frw_hs 页尾下右内容仅首页显示
     */
    public void setTail_frw_hs(String tail_frw_hs) {
        this.tail_frw_hs = tail_frw_hs;
    }

    /**
     * 获取 正文字体
     *
     * @return text_fn 正文字体
     */
    public String getText_fn() {
        return this.text_fn;
    }

    /**
     * 设置 正文字体
     *
     * @param text_fn 正文字体
     */
    public void setText_fn(String text_fn) {
        this.text_fn = text_fn;
    }

    /**
     * 获取 正文字号
     *
     * @return text_fz 正文字号
     */
    public int getText_fz() {
        return this.text_fz;
    }

    /**
     * 设置 正文字号
     *
     * @param text_fz 正文字号
     */
    public void setText_fz(int text_fz) {
        this.text_fz = text_fz;
    }

    /**
     * 获取 粗体 #fb[1]为粗体
     *
     * @return text_fb 粗体 #fb[1]为粗体
     */
    public String getText_fb() {
        return this.text_fb;
    }

    /**
     * 设置 粗体 #fb[1]为粗体
     *
     * @param text_fb 粗体 #fb[1]为粗体
     */
    public void setText_fb(String text_fb) {
        this.text_fb = text_fb;
    }

    /**
     * 获取 下划线 #fu[1] 为下划线
     *
     * @return text_fu 下划线 #fu[1] 为下划线
     */
    public String getText_fu() {
        return this.text_fu;
    }

    /**
     * 设置 下划线 #fu[1] 为下划线
     *
     * @param text_fu 下划线 #fu[1] 为下划线
     */
    public void setText_fu(String text_fu) {
        this.text_fu = text_fu;
    }

    /**
     * 获取 斜体 #fi[1] 为斜体
     *
     * @return text_fi 斜体 #fi[1] 为斜体
     */
    public String getText_fi() {
        return this.text_fi;
    }

    /**
     * 设置 斜体 #fi[1] 为斜体
     *
     * @param text_fi 斜体 #fi[1] 为斜体
     */
    public void setText_fi(String text_fi) {
        this.text_fi = text_fi;
    }

    /**
     * 获取 正文颜色
     *
     * @return text_fc 正文颜色
     */
    public String getText_fc() {
        return this.text_fc;
    }

    /**
     * 设置 正文颜色
     *
     * @param text_fc 正文颜色
     */
    public void setText_fc(String text_fc) {
        this.text_fc = text_fc;
    }

    /**
     * 获取 表头字体
     *
     * @return phead_fn 表头字体
     */
    public String getPhead_fn() {
        return this.phead_fn;
    }

    /**
     * 设置 表头字体
     *
     * @param phead_fn 表头字体
     */
    public void setPhead_fn(String phead_fn) {
        this.phead_fn = phead_fn;
    }

    /**
     * 获取 表头字号
     *
     * @return phead_fz 表头字号
     */
    public int getPhead_fz() {
        return this.phead_fz;
    }

    /**
     * 设置 表头字号
     *
     * @param phead_fz 表头字号
     */
    public void setPhead_fz(int phead_fz) {
        this.phead_fz = phead_fz;
    }

    /**
     * 获取 表头粗体 #fb[1]为粗体
     *
     * @return phead_fb 表头粗体 #fb[1]为粗体
     */
    public String getPhead_fb() {
        return this.phead_fb;
    }

    /**
     * 设置 表头粗体 #fb[1]为粗体
     *
     * @param phead_fb 表头粗体 #fb[1]为粗体
     */
    public void setPhead_fb(String phead_fb) {
        this.phead_fb = phead_fb;
    }

    /**
     * 获取 表头下划线 #fu[1] 为下划线
     *
     * @return phead_fu 表头下划线 #fu[1] 为下划线
     */
    public String getPhead_fu() {
        return this.phead_fu;
    }

    /**
     * 设置 表头下划线 #fu[1] 为下划线
     *
     * @param phead_fu 表头下划线 #fu[1] 为下划线
     */
    public void setPhead_fu(String phead_fu) {
        this.phead_fu = phead_fu;
    }

    /**
     * 获取 表头斜体 #fi[1] 为斜体
     *
     * @return phead_fi 表头斜体 #fi[1] 为斜体
     */
    public String getPhead_fi() {
        return this.phead_fi;
    }

    /**
     * 设置 表头斜体 #fi[1] 为斜体
     *
     * @param phead_fi 表头斜体 #fi[1] 为斜体
     */
    public void setPhead_fi(String phead_fi) {
        this.phead_fi = phead_fi;
    }

    /**
     * 获取 表头颜色
     *
     * @return phead_fc 表头颜色
     */
    public String getPhead_fc() {
        return this.phead_fc;
    }

    /**
     * 设置 表头颜色
     *
     * @param phead_fc 表头颜色
     */
    public void setPhead_fc(String phead_fc) {
        this.phead_fc = phead_fc;
    }
}
