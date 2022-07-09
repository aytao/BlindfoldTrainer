package com.aytao.blindfold;

import spark.Spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.aytao.blindfold.cube.Move;
import com.aytao.blindfold.cube.Sequence;
import com.aytao.blindfold.pochmann.Pochmann.PochmannSolution;
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

    private static class ClientSolution {
        private static class PochmannSolutionString {
            private String edgeOrder;
            private String cornerOrder;
            private boolean parity;
        }
        private String scramble;
        private PochmannSolutionString solution;

        private static ArrayList<Character> toCharArrayList(String order) {
            ArrayList<Character> ret = new ArrayList<>();
            
            for (char c : order.toCharArray()) {
                if (Character.isLetter(c)) {
                    ret.add(c);
                }
            }

            return ret;
        }

        public PochmannSolution toPochmannSolution() {
            ArrayList<Character> edgeArr = toCharArrayList(solution.edgeOrder);
            ArrayList<Character> cornerArr = toCharArrayList(solution.cornerOrder);
            return new PochmannSolution(edgeArr, cornerArr, solution.parity);
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

    private static String validateSolution(Request req, Response res) {
        res.type("application/json");
        String bodyStr = req.body();
        ClientSolution clientSolution;
        PochmannSolution pochmannSolution;
        try{
            clientSolution = new Gson().fromJson(bodyStr, ClientSolution.class);
            pochmannSolution = clientSolution.toPochmannSolution();
            System.out.println("What " + pochmannSolution.toMoves().size());
        } catch (Exception exception) {
            System.err.println("Hello?" + exception);
        }
        return "";
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java BlindfoldTrainer port");
            System.exit(1);
        }

        Spark.port(Integer.parseInt(args[0]));
        Spark.staticFiles.location("/static");
        Spark.get("/",
                (req, res) -> index(req, res));
        Spark.get("/getScrambles",
                (req, res) -> getScrambles(req, res));
        Spark.post("/validateSolution",
                (req, res) -> validateSolution(req, res));
    }
}
