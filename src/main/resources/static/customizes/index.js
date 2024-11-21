$("#toLoginBtn").on('click', function() {
	window.location.replace('/category/login');
});
$("#randomSearchBtn").on('click', function() {
	$("#loadingBackground").show();
	$("#loadingContainer").show();
	$("#randomSearchBtn").prop("disabled", true);
	let keyword = $("#keywordInput").val();
	commonRetrieve(keyword);
	setTimeout(function() {
		$("#loadingContainer").hide();
		$("#loadingBackground").hide();
		$("#randomSearchBtn").prop("disabled", false);
	}, 3300);
});
function commonRetrieve(keyword) {
	$.ajax({
		url: '/hymns/commonRetrieve',
		data: 'keyword=' + keyword,
		success: function(response) {
			buildTableBody(response);
		},
		error: function(result) {
			layer.msg(result.responseJSON.message);
		}
	});
}
function buildTableBody(response) {
	$("#tableBody").empty();
	$.each(response, (response, item) => {
		let nameMixTd = $("<td class='text-left' style='width: 160px;vertical-align: middle;'></td>")
			.append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + "/" + item.nameKr + "</a>"));
		let scoreTd = $("<td class='text-center' style='width: 20px;vertical-align: middle;'></td>")
			.append($("<a href='#' class='score-download-btn' scoreId='" + item.id + "'>&#x1D11E;</a>"));
		if (item.linenumber === 'BUNRGUNDY') {
			$("<tr class='table-danger'></tr>").append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		} else if (item.linenumber === 'NAPLES') {
			$("<tr class='table-warning'></tr>").append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		} else {
			$("<tr></tr>").append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		}
	});
}
$("#tableBody").on('click', '.link-btn', function(e) {
	e.preventDefault();
	let transferVal = $(this).attr('transferVal');
	window.open(transferVal);
});
$("#tableBody").on('click', '.score-download-btn', function(e) {
	e.preventDefault();
	let scoreId = $(this).attr('scoreId');
	window.location.href = '/hymns/scoreDownload?scoreId=' + scoreId;
});