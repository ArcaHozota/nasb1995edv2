let pageNum = $("#pageNumContainer").val();
$("#toStudentsPages").on("click", function(e) {
	e.preventDefault();
	layer.msg(delayApology);
});
$("#accountEdit").on("change", function() {
	checkStudentName(this, $("#idContainer").val());
});
$("#infoUpdationBtn").on("click", function() {
	let inputArrays = ["#accountEdit", "#nameEdit", "#passwordEdit", "#birthdayEdit", "#emailEdit"];
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
		let editId = $("#idContainer").val();
		let userId = $("#toPersonal").find("input").val().replace(/,/g, emptyString);
		if (editId !== userId) {
			layer.msg('システムエラー発生しました');
			return;
		}
		let putData = JSON.stringify({
			'id': editId,
			'loginAccount': $("#accountEdit").val().trim(),
			'username': $("#nameEdit").val().trim(),
			'password': $("#passwordEdit").val(),
			'email': $("#emailEdit").val(),
			'dateOfBirth': $("#birthdayEdit").val()
		});
		projectAjaxModify('/students/infoUpdation', 'PUT', putData, studentsPutSuccessFunction);
	}
});
function studentsPutSuccessFunction(response) {
	let message = response.replace(/^"|"$/g, emptyString);
	localStorage.setItem('redirectMessage', message);
	window.location.replace('/category/toMainmenu');
}
$("#restoreBtn").on("click", function() {
	formReset("#editForm");
});
function checkStudentName(studentName, idVal) {
	let nameVal = $(studentName).val().trim();
	if (nameVal === emptyString) {
		showValidationMsg(studentName, responseFailure, "名称を空になってはいけません。");
	} else {
		$.ajax({
			url: '/students/checkDuplicated',
			data: {
				'id': idVal,
				'loginAccount': nameVal
			},
			success: function(xhr) {
				showValidationMsg(studentName, responseSuccess, xhr.responseText);
			},
			error: function(xhr) {
				showValidationMsg(studentName, responseFailure, xhr.responseText);
			}
		});
	}
}