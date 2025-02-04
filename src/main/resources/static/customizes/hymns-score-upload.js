let pageNum = $("#pageNumContainer").val();
$(document).ready(function() {
	$("#toCollection").css('color', '#006b3c');
	$("#toCollection").addClass('animate__animated animate__flipInY');
});
$("#toHymnPages").on("click", function(e) {
	e.preventDefault();
	let url = '/hymns/toPages?pageNum=' + pageNum;
	checkPermissionAndTransfer(url);
});
$("#scoreUploadBtn").on("click", function() {
	let inputArrays = ["#scoreEdit"];
	for (const array of inputArrays) {
		$(array).removeClass("is-valid is-invalid");
		$(array).next("span").removeClass("valid-feedback invalid-feedback");
		$(array).next("span").text(emptyString);
	}
	let listArray = projectInputContextGet(inputArrays);
	if (listArray.includes(emptyString)) {
		projectNullInputboxDiscern(inputArrays);
		return;
	}
	let header = $("meta[name=_csrf_header]").attr('content');
	let token = $("meta[name=_csrf_token]").attr('content');
	let editId = $("#idContainer").val();
	let fileInput = document.getElementById("scoreEdit");
	let file = fileInput.files[0];
	let reader = new FileReader();
	reader.onload = function(e) {
		// 将文件内容转换为 base64 字符串
		let base64File = e.target.result.split(",")[1]; // 只取 base64 数据部分
		// 创建 JSON 数据
		let jsonData = JSON.stringify({
			'id': editId,
			'score': base64File
		});
		// 发送 AJAX 请求
		$.ajax({
			url: '/hymns/scoreUpload',
			type: 'POST',
			data: jsonData,
			headers: {
				[header]: token
			},
			contentType: 'application/json',
			success: function(response) {
				let message = response.replace(/^"|"$/g, emptyString);
				localStorage.setItem('redirectMessage', message);
				window.location.replace('/hymns/toPages?pageNum=' + pageNum);
			},
			error: function(xhr) {
				let message = xhr.responseText.replace(/^"|"$/g, emptyString);
				layer.msg(message);
			}
		});
	};
	// 启动文件读取并转换为 base64 格式
	reader.readAsDataURL(file);
});
