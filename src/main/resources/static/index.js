'use strict';

let puzzleDisplay;
let scrambles = [];
let currentScramble = null;
let request;
let waiting = false;
const MIN_SCRAMBLES = 5;
const MORE_SCRAMBLES_URL = "/getScrambles"

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

function setup() {
    puzzleDisplay = document.getElementById("puzzle");
    $("#getNewScrambleButton").on("click", getNewScramble);
    fetchMoreScrambles();
}


$('document').ready(setup);


