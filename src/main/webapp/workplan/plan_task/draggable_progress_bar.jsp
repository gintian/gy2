<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style type="text/css">
.progress-bar {display:inline-block;width:200px;height:10px;}
.progress-bar h3,.progress-bar h4 {position:absolute;top:-1px;height:10px;width:0%;background:#00BFFF;border:1px solid #187aab;-moz-border-radius:6px;-webkit-border-radius:6px;border-radius:6px;z-index:10;}
.progress-bar h4 {border: 1px solid #91cdea;background:#f5fafc;z-index:1;}
.progress-bar h3 {filteralpha(opacity=20);opacity:0.2;}
.tip-for-progress-bar {position:relative;top:-2px;font-size:10px;padding-left:10px;display:inline-block;}
</style>
<p style="display:inline-block;margin-top:-2px;float:left;">任务进度：</p>
<div id="progressBar"  class="progress-bar" fieldType="progress"></div>
<!-- <div id="progressBarTip" class="tip-for-progress-bar"></div> -->
<p style="display:inline-block;margin-top:-2px;padding-left:15px;float:right;color:#C0C0C0;"><span id="progressBarTip" style="color:black;"></span>&nbsp;&nbsp;&nbsp;拖动任务进度条，点击发表按钮填报任务的进度</p>
