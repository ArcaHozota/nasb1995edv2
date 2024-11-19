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
$("#nameJpInput").on("change", function() {
	checkHymnName(this, null);
});
$("#nameKrInput").on("change", function() {
	checkHymnName2(this, null);
});
$("#infoStorageBtn").on("click", function() {
	let inputArrays = ["#nameJpInput", "#nameKrInput", "#linkInput", "#serifInput"];
	for (const array of inputArrays) {
		$(array).removeClass("is-valid is-invalid");
		$(array).next("span").removeClass("valid-feedback invalid-feedback");
		$(array).next("span").text(emptyString);
	}
	let listArray = projectInputContextGet(inputArrays);
	if (listArray.includes(emptyString)) {
		projectNullInputboxDiscern(inputArrays);
	} else if ($("#inputForm").find('*').hasClass('is-invalid')) {
		layer.msg(inputWarning);
	} else {
		let postData = JSON.stringify({
			'nameJp': $("#nameJpInput").val().trim(),
			'nameKr': $("#nameKrInput").val().trim(),
			'link': $("#linkInput").val(),
			'serif': $("#serifInput").val(),
			'updatedUser': $("#toPersonal").find("input").val().replace(/,/g, emptyString)
		});
		projectAjaxModify('/hymns/infoStorage', 'POST', postData, hymnsPostSuccessFunction);
	}
});
$("#nameJpEdit").on("change", function() {
	checkHymnName(this, $("#idContainer").val());
});
$("#nameKrEdit").on("change", function() {
	checkHymnName2(this, $("#idContainer").val());
});
$("#infoUpdationBtn").on("click", function() {
	let inputArrays = ["#nameJpEdit", "#nameKrEdit", "#linkEdit", "#serifEdit"];
	for (const array of inputArrays) {
		$(array).removeClass("is-valid is-invalid");
		$(array).next("span").removeClass("valid-feedback invalid-feedback");
		$(array).next("span").text(emptyString);
	}
	let listArray = projectInputContextGet(inputArrays);
	if (listArray.includes(emptyString)) {
		projectNullInputboxDiscern(inputArrays);
	} else if ($("#editForm").find('*').hasClass('is-invalid')) {
		layer.msg(inputWarning);
	} else {
		let putData = JSON.stringify({
			'id': $("#idContainer").val(),
			'nameJp': $("#nameJpEdit").val().trim(),
			'nameKr': $("#nameKrEdit").val().trim(),
			'link': $("#linkEdit").val(),
			'serif': $("#serifEdit").val(),
			'updatedTime': $("#datestampContainer").val(),
			'updatedUser': $("#toPersonal").find("input").val().replace(/,/g, emptyString)
		});
		projectAjaxModify('/hymns/infoUpdation', 'PUT', putData, hymnsPutSuccessFunction);
	}
});
function hymnsPostSuccessFunction(response) {
	localStorage.setItem('redirectMessage', inputedString);
	window.location.replace('/hymns/toPages?pageNum=' + response);
}
function hymnsPutSuccessFunction(response) {
	let message = response.replace(/^"|"$/g, emptyString);
	localStorage.setItem('redirectMessage', message);
	window.location.replace('/hymns/toPages?pageNum=' + pageNum);
}
$("#resetBtn").on("click", function() {
	formReset("#inputForm");
});
$("#restoreBtn").on("click", function() {
	formReset("#editForm");
});
function checkHymnName(hymnName, idVal) {
	let nameVal = $(hymnName).val().trim();
	if (nameVal === emptyString) {
		showValidationMsg(hymnName, responseFailure, '名称を空になってはいけません。');
	} else {
		$.ajax({
			url: '/hymns/checkDuplicated',
			data: {
				'id': idVal,
				'nameJp': nameVal
			},
			success: function(response) {
				showValidationMsg(hymnName, responseSuccess, response);
			},
			error: function(xhr) {
				showValidationMsg(hymnName, responseFailure, xhr.responseText);
			}
		});
	}
}
function checkHymnName2(hymnName, idVal) {
	let nameVal = $(hymnName).val().trim();
	if (nameVal === emptyString) {
		showValidationMsg(hymnName, responseFailure, '名称を空になってはいけません。');
	} else {
		$.ajax({
			url: '/hymns/checkDuplicated2',
			data: {
				'id': idVal,
				'nameKr': nameVal
			},
			success: function(response) {
				showValidationMsg(hymnName, responseSuccess, response);
			},
			error: function(xhr) {
				showValidationMsg(hymnName, responseFailure, xhr.responseText);
			}
		});
	}
}