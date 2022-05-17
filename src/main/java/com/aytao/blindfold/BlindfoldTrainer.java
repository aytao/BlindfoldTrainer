package com.aytao.blindfold;

import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

public class BlindfoldTrainer {
    public static class StandardResponse {
        public enum StatusResponse {
            SUCCESS,
            ERROR;
        }

        private StatusResponse status;
        private String message;
        private JsonElement data;

        public StandardResponse(StatusResponse status) {
            this.status = status;
        }

        public StandardResponse(StatusResponse status, String message) {
            this.status = status;
            this.message = message;
        }

        public StandardResponse(StatusResponse status, JsonElement data) {
            this.status = status;
            this.data = data;
        }
    }

    private static final int DEFAULT_NUM_SCRAMBLES = 10;
    private static final int MAX_SCRAMBLES = 25;
    private static final Gson SCRAMBLE_GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static String index(Request req, Response res) {
        Map<String, Object> model = new HashMap<>();
        model.put("sitename", "Blindfold Trainer");
        model.put("scramble", Sequence.toString(Sequence.getScramble()));
        ModelAndView mv = new ModelAndView(model, "index.vtl");
        return new VelocityTemplateEngine().render(mv);
    }

    private static String getScrambles(Request req, Response res) {
        res.type("application/json");
        int num = DEFAULT_NUM_SCRAMBLES;
        String numStr = req.queryParams("numScrambles");
        if (numStr != null) {
            try {
                num = Integer.parseInt(numStr);
                num = Math.min(num, MAX_SCRAMBLES);
            } catch (Exception e) {
                
            }
        }
        String[] scrambles = Sequence.getScrambleStrings(num);
        return SCRAMBLE_GSON.toJson(
                new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
                        SCRAMBLE_GSON.toJsonTree(scrambles)));
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Penny port");
            System.exit(1);
        }

        Spark.port(Integer.parseInt(args[0]));
        Spark.staticFiles.location("/static");
        Spark.get("/",
                (req, res) -> index(req, res));
        Spark.get("/getScrambles",
                (req, res) -> getScrambles(req, res));
    }
}
