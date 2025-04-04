let pageNum = $("#pageNumContainer").val();
let totalRecords, totalPages, keyword;
$(document).ready(function() {
	$("#toCollection").css('color', '#006b3c');
	$("#toCollection").addClass('animate__animated animate__flipInY');
	if (keyword === undefined) {
		keyword = emptyString;
	}
	let message = localStorage.getItem('redirectMessage');
	if (message) {
		layer.msg(message);
		localStorage.removeItem('redirectMessage');
	}
	toSelectedPg(pageNum, keyword);
});
$("#searchBtn2").on("click", function() {
	keyword = $("#keywordInput").val();
	toSelectedPg(1, keyword);
});
function toSelectedPg(pageNum, keyword) {
	$.ajax({
		url: '/hymns/pagination.action',
		data: {
			'pageNum': pageNum,
			'keyword': keyword
		},
		success: function(response) {
			buildTableBody(response);
			buildPageInfos(response);
			buildPageNavi(response);
		},
		error: function(xhr) {
			let message = xhr.responseText.replace(/^"|"$/g, emptyString);
			layer.msg(message);
		}
	});
}
function buildTableBody(response) {
	$("#tableBody").empty();
	let index = response.records;
	$.each(index, (index, item) => {
		let nameJpTd = $("<th class='text-left' style='width: 130px;vertical-align: middle;'></th>").append(item.nameJp);
		let nameKrTd = $("<td class='text-left' style='width: 100px;vertical-align: middle;'></td>").append(item.nameKr);
		let linkTd = $("<td class='text-center' style='width: 20px;vertical-align: middle;'></td>")
			.append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>Link</a>"));
		let scoreTd = $("<td class='text-center' style='width: 20px;vertical-align: middle;'></td>")
			.append($("<a href='#' class='score-download-btn' scoreId='" + item.id + "'>&#x1D11E;</a>"));
		let scoreBtn = $("<button></button>").addClass("btn btn-success btn-sm score-btn")
			.append($("<i class='fa-solid fa-music'></i>")).append("楽譜");
		scoreBtn.attr("scoreId", item.id);
		let editBtn = $("<button></button>").addClass("btn btn-primary btn-sm edit-btn")
			.append($("<i class='fa-solid fa-pencil'></i>")).append("編集");
		editBtn.attr("editId", item.id);
		let deleteBtn = $("<button></button>").addClass("btn btn-danger btn-sm delete-btn")
			.append($("<i class='fa-solid fa-trash'></i>")).append("削除");
		deleteBtn.attr("deleteId", item.id);
		let btnTd = $("<td class='text-center' style='width: 80px;vertical-align: middle;'></td>")
			.append(scoreBtn).append(" ").append(editBtn).append(" ").append(deleteBtn);
		$("<tr></tr>").append(nameJpTd).append(nameKrTd).append(linkTd).append(scoreTd).append(btnTd).appendTo("#tableBody");
	});
}
$("#tableBody").on("click", '.delete-btn', function() {
	let deleteId = $(this).attr("deleteId");
	let nameJp = $(this).parents("tr").find("th").text().trim();
	normalDeletebtnFunction('/hymns/', 'この「' + nameJp + '」という歌の情報を削除するとよろしいでしょうか。', deleteId);
});
$("#infoAdditionBtn").on("click", function(e) {
	e.preventDefault();
	let url = '/hymns/toAddition.action?pageNum=' + pageNum;
	checkPermissionAndTransfer(url);
});
$("#tableBody").on("click", '.edit-btn', function() {
	let editId = $(this).attr("editId");
	let url = '/hymns/toEdition.action?editId=' + editId + '&pageNum=' + pageNum;
	checkPermissionAndTransfer(url);
});
$("#tableBody").on("click", '.score-btn', function() {
	let scoreId = $(this).attr('scoreId');
	let url = '/hymns/toScoreUpload.action?scoreId=' + scoreId + '&pageNum=' + pageNum;
	checkPermissionAndTransfer(url);
});
$("#tableBody").on("click", '.link-btn', function(e) {
	e.preventDefault();
	let transferVal = $(this).attr('transferVal');
	window.open(transferVal);
});
$("#tableBody").on("click", '.score-download-btn', function(e) {
	e.preventDefault();
	let scoreId = $(this).attr('scoreId');
	window.location.href = '/hymns/scoreDownload.action?scoreId=' + scoreId;
});