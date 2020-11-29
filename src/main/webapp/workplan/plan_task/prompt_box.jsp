<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div tabindex="0" hidefocus="true" id="prompt-box" class="hj-wzm-five-dinwei" style="position:absolute;display:none;z-index:100;">
	<div class="hj-five-top" >
		<input type="text" class="hj-five-din-ss promptBox-tip" style="margin-left:10px; padding-left:5px;" 
		       onkeyup="basic.prompt.resetPrompt(basic.prompt.getCandidate1(this))" 
			   onclick="basic.prompt.clearTip(this)"
			   onblur="basic.prompt.restoreTip(this)"
		       tip="输入姓名/拼音简码/email..." />
			    <!-- onblur="basic.prompt.resetPrompt(basic.prompt.getCandidate(this))" -->
	</div>
	<ul></ul>
</div>

<style type="text/css">
	.candidatePhoto img {width:32px;height:32px;border-radius:50%;}
	.candidatePhoto {float:left;margin:6px 10px 0 0;}
	.promptBox-tip {color: #888;}
</style>

<script type="text/javascript">
Ext.onReady(function() {
	//为document添加点击事件，如果事件元素在提示框之外，则隐藏提示框
	Ext.get(document).on("click", function(e) {
		e = e || window.event;
		var target = e.target || e.srcElement;

		var promptBox = document.getElementById(basic.prompt.promptBoxId);
		if (target.getAttribute("prompt") === null) {
			// 如果点击事件不是发生在提示框内
			if (!basic.global.hasChild(promptBox, target) && !basic.global.isSameNode(promptBox, target)) {
				basic.prompt.closePrompt();
				basic.util.refreshPromptPlaceHolder(); // 恢复绑定的占位符
			}
		}
	});
});
</script>