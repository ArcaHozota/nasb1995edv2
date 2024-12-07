$(document).ready(function() {
    adjustWidth();
    $("#tableBody").hide();
    $("#saraniSearchBtn").prop("disabled", true);
    $("#saraniSearchBtn").on("mousemove", function(e) {
        let x = e.pageX - $(this).offset().left;
        let y = e.pageY - $(this).offset().top;
        $(this).css("--x", x + "px");
        $(this).css("--y", y + "px");
    });
});
$("#saraniSearchBtn").on("click", function(e) {
    e.preventDefault();
    layer.msg('すみませんが、当機能はまだ実装されていません');
    /*adjustWidth();
    $("#loadingBackground").show();
    $("#loadingContainer").show();
    $("#tableBody").show();
    $("#saraniSearchBtn").prop("disabled", true);
    let hymnId = $("#hymnRecordEdit").val();
    saraniRetrieve(hymnId);
    setTimeout(function() {
        $("#loadingContainer").hide();
        $("#loadingBackground").hide();
        $("#saraniSearchBtn").prop("disabled", false);
    }, 22000);*/
});
$("#hymnRecordEdit").on("change", function() {
    let hymnId = $(this).val();
    if (hymnId === "0" || hymnId === 0) {
        $("#tableBody").hide();
        return;
    }
    $.ajax({
        url: '/hymns/getInfoById',
        data: 'hymnId=' + hymnId,
        success: function(response) {
            buildTableBody(response);
        },
        error: function(result) {
            layer.msg(result.responseJSON.message);
        }
    });
    $("#tableBody").show();
});
$("#tableBody").on("click", '.link-btn', function(e) {
    e.preventDefault();
    let transferVal = $(this).attr('transferVal');
    window.open(transferVal);
});
function saraniRetrieve(hymnId) {
    $.ajax({
        url: '/hymns/saraniRetrieve',
        data: 'hymnId=' + hymnId,
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
        let nameMixTd = $("<td class='text-center' style='width: 80%;vertical-align: middle;'></td>")
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