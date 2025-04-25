$(document).ready(() => {
	$("#toRandomFive").css('color', '#006b3c');
	$("#toRandomFive").addClass('animate__animated animate__flipInY');
});
$("#randomSearchBtn").on("click", () => {
	keyword = $("#keywordInput").val();
	retrieveRandomFive(keyword);
});
$("#tableBody").on("click", '.link-btn', (e) => {
	e.preventDefault();
	let transferVal = $(this).attr('transferVal');
	window.open(transferVal);
});

function retrieveRandomFive(keyword) {
	$.ajax({
		url: '/hymns/retrieve-random-five',
		data: 'keyword=' + keyword,
		success: (response) => {
			buildTableBody(response);
		},
		error: (result) => {
			layer.msg(result.responseJSON.message);
		}
	});
}

function buildTableBody(response) {
	$("#tableBody").empty();
	$.each(response, (response, item) => {
		let nameMixTd = $("<td class='text-center' style='vertical-align: middle;'></td>").append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + delimiter + item.nameKr + "</a>"));
		$("<tr></tr>").append(nameMixTd).appendTo("#tableBody");
	});
}
