package com.aytao.blindfold;

import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

public class BlindfoldTrainer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Penny port");
            System.exit(1);
        }
        Spark.port(Integer.parseInt(args[0]));

        Spark.get("/",
                (req, res) -> index(req, res));
    }

    private static String index(Request req, Response res) {
        Map<String, Object> model = new HashMap<>();
        model.put("sitename", "Blindfold Trainer");
        ModelAndView mv = new ModelAndView(model, "index.vtl");
        return new VelocityTemplateEngine().render(mv);
    }
}
