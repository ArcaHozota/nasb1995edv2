$(document).ready(function() {
	adjustWidth();
	$("#tableBody").hide();
});
$("#randomSearchBtn").on("click", function() {
	adjustWidth();
	$("#loadingBackground").show();
	$("#loadingContainer").show();
	$("#tableBody").show();
	$("#randomSearchBtn").prop("disabled", true);
	let keyword = $("#keywordInput").val();
	commonRetrieve(keyword);
	setTimeout(function() {
		$("#loadingContainer").hide();
		$("#loadingBackground").hide();
		$("#randomSearchBtn").prop("disabled", false);
	}, 33000);
});
$("#tableBody").on("click", '.link-btn', function(e) {
	e.preventDefault();
	let transferVal = $(this).attr('transferVal');
	window.open(transferVal);
});
$("#toIchiranHyoBtn").on("click", function() {
	swal.fire({
		title: 'メッセージ',
		text: '賛美歌一覧表画面へ移動してよろしいでしょうか。',
		icon: 'question',
		showCloseButton: true,
		confirmButtonText: 'はい',
		confirmButtonColor: '#7F0020'
	}).then((result) => {
		if (result.isConfirmed) {
			window.location.href = '/home/toIchiranhyo';
		} else if (result.isDenied) {
			$(this).close();
		}
	});
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
		let nameMixTd = $("<td class='text-center' style='vertical-align: middle;'></td>")
			.append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + "/" + item.nameKr + "</a>"));
		if (item.linenumber === 'BUNRGUNDY') {
			$("<tr class='table-danger'></tr>").append(nameMixTd).appendTo("#tableBody");
		} else if (item.linenumber === 'NAPLES') {
			$("<tr class='table-warning'></tr>").append(nameMixTd).appendTo("#tableBody");
		} else if (item.linenumber === 'CADIMIUM') {
			$("<tr class='table-success'></tr>").append(nameMixTd).appendTo("#tableBody");
		} else {
			$("<tr class='table-light'></tr>").append(nameMixTd).appendTo("#tableBody");
		}
	});
}
function adjustWidth() {
	const $indexTable = $("#indexTable");
	if ($indexTable.length) {
		$('.background').css('width', $indexTable.outerWidth() + 'px');
	}
}