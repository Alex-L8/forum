$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// // 发送AJAX请求之前，将CSRF令牌放入请求头中
	// var token = $("meta[name='_csrf']").attr("content");
	// var header = $("meta[name='_csrf_header']").attr("content");
	//
	// // event - 包含 event 对象
	// // xhr - 包含 XMLHttpRequest 对象
	// // options - 包含 AJAX 请求中使用的选项
	// $(document).ajaxSend(function (event,xhr,options) {
	// 	xhr.setRequestHeader(header, token);
	// })

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	// 发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title": title, "content": content},
		function (data) {
			data = $.parseJSON(data);
			// 在提示框中显示返回的消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2s后，自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");

				// 刷新页面
				if (data.code == 0) {
					// window.location.reload();
				}
			}, 2000);
		}
	);

}