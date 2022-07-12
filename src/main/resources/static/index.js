'use strict';

let puzzleDisplay;
let exampleSolutionCube;
let scrambles = [];
let currentScramble = null;
let request;
let exampleSolutionRequest;
let waiting = false;
const MIN_SCRAMBLES = 5;
const MORE_SCRAMBLES_URL = "/getScrambles";
const VALIDATE_SOLUTION_URL = "/validateSolution";
const EXAMPLE_SOLUTION_URL = "/exampleSolution";

function fetchMoreScrambles() {
    request = $.ajax({
        type: "GET",
        url: MORE_SCRAMBLES_URL,
        success: function (response) {
            if (response.status != "SUCCESS") {
                // TODO: HANDLE ERROR
                return;
            }
            for (const scramble of response.data) {
                scrambles.push(scramble);
            }
            if (waiting) {
                waiting = false;
                getNewScramble();
            }
            request = null;
        }, error: function(response) {
            // TODO: HANDLE ERROR!
            console.log(response);
            request = null;
            waiting = false;
        }
    });
}

function getNewScramble() {
    if (scrambles.length === 0) {
        $("#scramble").text("Generating...");
        puzzleDisplay.alg = "";
        currentScramble = null;
        waiting = true;
        if (request === null) {
            fetchMoreScrambles();
        }
        return;
    } else if (scrambles.length < MIN_SCRAMBLES && request === null) {
        fetchMoreScrambles();
    }
    let scramble = scrambles.pop();
    puzzleDisplay.alg = scramble;
    currentScramble = scramble;
    $("#scramble").text(scramble);
}

function getSolutionFromInput() {
    return {
        scramble: currentScramble,
        solution: {
            edgeOrder: $("#edgeSwap").val(),
            cornerOrder: $("#cornerSwap").val(),
            parity: $("#parity").is(":checked"),
        }
    };
}

function addBanner(message, success) {
    let banner = $('<div>', {
        text: message
    });
    banner.addClass(success ? "alert alert-success" : "alert alert-warning");
    banner.appendTo("#banner");

    setTimeout(function () {
        banner.hide(1000);
        setTimeout(function () {
            banner.remove()
        }, 1000);
    }, 3000);
}

function sendSolution() {
    request = $.ajax({
        type: "POST",
        url: VALIDATE_SOLUTION_URL,
        data: JSON.stringify(getSolutionFromInput()),
        dataType: "json",
        success: function (response) {
            console.log(response);
            if (response.status != "SUCCESS") {
                // TODO: HANDLE ERROR
                return;
            }
            let success = response.data.status == "SUCCESS";
            addBanner(success ? "Success!" : "Incorrect", success);

            request = null;
        }, error: function(response) {
            // TODO: HANDLE ERROR!
            console.log(response);
            request = null;
            waiting = false;
        }
    });
}

function getExampleSolution() {
    let requestedSolutionScramble = currentScramble;
    request = $.ajax({
        type: "GET",
        url: EXAMPLE_SOLUTION_URL + "?scramble=" + currentScramble,
        dataType: "json",
        success: function (response) {
            console.log(response);
            if (response.status != "SUCCESS") {
                // TODO: HANDLE ERROR
                return;
            }
            if (response.data && response.data.solution) {
                $("#exampleSolution").show();
                exampleSolutionCube.alg = response.data.solution;
                exampleSolutionCube.play();
            }

            exampleSolutionRequest = null;
        }, error: function(response) {
            // TODO: HANDLE ERROR!
            console.log(response);
            exampleSolutionRequest = null;
        }
    });
}

function setup() {
    puzzleDisplay = document.getElementById("puzzle");
    currentScramble = $("#scramble").text();
    $("#getNewScrambleButton").on("click", getNewScramble);
    $("#sendSolutionButton").on("click", sendSolution);
    $("#getExampleSolution").on("click", getExampleSolution);
    exampleSolutionCube = document.getElementById("exampleSolutionCube");
    fetchMoreScrambles();
}


$('document').ready(setup);


