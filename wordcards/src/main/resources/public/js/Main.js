$(document).ready(function () {

    $("#wordcards-button").click(function () {
        getWordCards();
    });

    $("#results-button").click(function () {
        getResults();
    });

    $("#mixed-button").click(function () {
        resultToZero();
        $.ajax({
            url: "/mixed_cards",
            type: "GET",
            async: true,
            success: function (resp) {
                getCard();
                $("#card-image-container").hide();
                $("#get-card").removeAttr("style");
            },
            error: function () {
                alert('Problem with function: There are no available word cards.');
            }
        });
    });

    $("#get-card").click(function () {
        getCard()
    });

    $(".exercise-button").click(function () {
        resultToZero();
        var theme = $(this).attr("id");
        $.ajax({
            url: "/exercise",
            type: "POST",
            async: true,
            data: {theme: theme},
            success: function (resp) {
                getCard();
                $("#card-image-container").hide();
                $("#get-card").removeAttr("style");
            },
            error: function () {
                alert('Problem with function: There are no available word cards in this category.')
            }
        });
    });

    function getCard() {
        var hun = $("#hun-student-input").val();
        var eng = $("#eng-student-input").val();
        evaluate(hun, eng);
        $.ajax({
            url: "/getcard",
            type: "GET",
            async: true,
            success: function (resp) {
                var n = JSON.parse(resp);
                if (n == null) {
                    getResult();
                    $('#card-image-container').hide();
                } else {
                    getImage(n);
                    $('#card-image-container').removeAttr('style');
                    $('#hun-student-input').val("");
                    $('#eng-student-input').val("");
                }
            },
            error: function () {
                alert('Problem with function: getCard')
            }
        });
    }

    function getImage(obj) {
        $.ajax({
            url: "/getimage",
            type: "POST",
            data: {picLocation: obj["picLocation"]},
            async: true,
            success: function (resp) {
                $('#card-image').attr("src", resp);
            },
            error: function () {
                alert('Problem with function: getImage')
            }
        });
    }

    function evaluate(hun, eng) {
        $.ajax({
            url: "/evaluate",
            type: "POST",
            data: {hun: hun, eng: eng},
            async: true,
            error: function () {
                alert('Problem with function: evaluate')
            }
        });
    }

    function resultToZero() {
        $.ajax({
            url: "/result_to_zero",
            type: "GET",
            async: true,
            error: function () {
                alert('Problem with function: resultToZero')
            }
        });
    }

    function getResult() {
        $.ajax({
            url: "/get_result",
            type: "GET",
            async: true,
            success: function (resp) {
                $('#result-container').removeAttr("style");
                $('#result-points').html(" 10/" + resp);
            },
            error: function (resp) {
                alert('Problem with function: getResult')
            }
        });
    }

    $("#lists").on("click", ".del-wordcard-button", function () {
        var ItemIndex = $(this).attr("value");
        $.ajax({
            url: "/del_wordcard",
            type: "POST",
            data: {id: ItemIndex},
            async: true,
            success: function (resp) {
                getWordCards();
            },
            error: function () {
                alert('Problem with function: del_wordcard')
            }
        });
    });

    $("#lists").on("click", ".del-result-button", function () {
        var ItemIndex = $(this).attr("value");
        $.ajax({
            url: "/del_result",
            type: "POST",
            data: {id: ItemIndex},
            async: true,
            success: function (resp) {
                getResults();
            },
            error: function () {
                alert('Problem with function: del_result')
            }
        });
    });

    function getWordCards() {
        $.ajax({
            url: "/wordcards",
            type: "GET",
            async: true,
            success: function (resp) {
                var n = JSON.parse(resp);
                $("#big-table").removeAttr("style");
                $("#big-table").html(
                    "<tr><th>ID</th>" +
                    "<th>File</th>" +
                    "<th>Theme</th>" +
                    "<th>HUN</th>" +
                    "<th>EN</th>" +
                    "<th>Remove</th>" +
                    "</tr>");
                for (var i = 0; i < n.length; i++) {
                    var obj = n[i];
                    var id = obj["id"];
                    var picLoc = obj["picLocation"];
                    var theme = obj["theme"];
                    var hun = obj["hun"];
                    var eng = obj["eng"];
                    var delButton = "<button value='" + obj["id"] + "' type='button' class='del-wordcard-button glyphicon glyphicon-remove-sign'></button>";
                    $("#big-table").append(
                        "<tr><td>" + id + "</td>" +
                        "<td>" + picLoc + "</td>" +
                        "<td>" + theme + "</td>" +
                        "<td>" + hun + "</td>" +
                        "<td>" + eng + "</td>" +
                        "<td>" + delButton + "</td></tr>"
                    );
                }
            },
            error: function () {
                alert('Problem with function: getWordCards')
            }
        });
    }

    function getResults() {
        $.ajax({
            url: "/results",
            type: "GET",
            async: true,
            success: function (resp) {
                var n = JSON.parse(resp);
                $("#big-table").removeAttr("style");
                $("#big-table").html(
                    "<tr><th>ID</th>" +
                    "<th>First Name</th>" +
                    "<th>Last Name</th>" +
                    "<th>Result</th>" +
                    "<th>Start</th>" +
                    "<th>End</th>" +
                    "<th>Remove</th>" +
                    "</tr>");
                for (var i = 0; i < n.length; i++) {
                    var obj = n[i];
                    var id = obj["id"];
                    var firstName = obj["firstName"];
                    var lastName = obj["lastName"];
                    var result = obj["result"];
                    var startTime = obj["startTime"];
                    var endTime = obj["endTime"];
                    var delButton = "<button value='" + obj["id"] + "' type='button' class='del-result-button glyphicon glyphicon-remove-sign'></button>";
                    $("#big-table").append(
                        "<tr><td>" + id + "</td>" +
                        "<td>" + firstName + "</td>" +
                        "<td>" + lastName + "</td>" +
                        "<td>" + result + "%</td>" +
                        "<td>" + startTime + "</td>" +
                        "<td>" + endTime + "</td>" +
                        "<td>" + delButton + "</td></tr>"
                    );
                }
            },
            error: function () {
                alert('Problem with function: getResults');
            }
        });
    }
});