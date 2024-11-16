$(document).ready(function() {
	$("#toTemporary").css('color', '#006b3c');
	$("#toTemporary").addClass('animate__animated animate__flipInY');
});
$("#bookInput").on('change', function() {
	$("#chapterInput").empty();
	let bookId = $(this).val();
	$.ajax({
		url: '/books/getChapters',
		data: 'bookId=' + bookId,
		success: function(response) {
			let index = response.data;
			$.each(index, (index, item) => {
				let optionElement = $("<option></option>").val(item.id).text(item.name);
				optionElement.appendTo("#chapterInput");
			});
		}
	});
});
$("#infoStorageBtn").on('click', function() {
	let inputArrays = ["#phraseIdInput", "#phraseTextEnInput", "#phraseTextJpInput"];
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
			'chapterId': $("#chapterInput").val(),
			'id': $("#phraseIdInput").val().trim(),
			'textEn': $("#phraseTextEnInput").val().trim(),
			'textJp': $("#phraseTextJpInput").val().trim()
		});
		projectAjaxModify('/books/infoStorage', 'POST', postData, booksPostSuccessFunction);
	}
});
function booksPostSuccessFunction(xhr) {
	formReset("#inputForm");
	formReset("#inputForm2");
	layer.msg(xhr.responseText.replace(/^"|"$/g, emptyString));
}