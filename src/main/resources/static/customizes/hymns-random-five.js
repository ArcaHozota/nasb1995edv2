$(document).ready(function() {
	$("#toRandomFive").css('color', '#006b3c');
	$("#toRandomFive").addClass('animate__animated animate__flipInY');
});
$("#randomSearchBtn").on("click", function() {
	keyword = $("#keywordInput").val();
	retrieveRandomFive(keyword);
});
function retrieveRandomFive(keyword) {
	$.ajax({
		url: '/hymns/retrieveRandomFive.action',
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
		let nameMixTd = $("<td class='text-center' style='vertical-align: middle;'></td>").append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + delimiter + item.nameKr + "</a>"));
		$("<tr></tr>").append(nameMixTd).appendTo("#tableBody");
	});
}
$("#tableBody").on("click", '.link-btn', function(e) {
	e.preventDefault();
	let transferVal = $(this).attr('transferVal');
	window.open(transferVal);
});