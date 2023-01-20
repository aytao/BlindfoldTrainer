package com.aytao.rubiks;

import spark.Spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.aytao.rubiks.BlindfoldTrainer.SolutionResponse.SolutionStatus;
import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;
import com.aytao.rubiks.pochmann.Pochmann;
import com.aytao.rubiks.pochmann.Pochmann.PochmannSolution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

public class BlindfoldTrainer {
  private static class StandardResponse {
    public enum StatusResponse {
      SUCCESS,
      ERROR;
    }

    static class ExampleSolution {
      String solution;
    }

    StatusResponse status;
    String message;
    JsonElement data;

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

    public static StandardResponse getWithSolution(String solution) {
      ExampleSolution exampleSolution = new ExampleSolution();
      exampleSolution.solution = solution;
      return new StandardResponse(StatusResponse.SUCCESS, RESPONSE_GSON.toJsonTree(exampleSolution));
    }
  }

  public static class SolutionResponse {
    public enum SolutionStatus {
      SUCCESS,
      FAILURE;
    }

    SolutionStatus status;

    public SolutionResponse(SolutionStatus status) {
      this.status = status;
    }

    public JsonElement toJsonElement() {
      return RESPONSE_GSON.toJsonTree(this);
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
  private static final Gson MOVE_SAFE_GSON = new GsonBuilder().disableHtmlEscaping().create();
  private static final Gson RESPONSE_GSON = new Gson();

  private static String index(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    model.put("sitename", "Blindfold Trainer");
    model.put("scramble", "R U R' U' M' U R U' r'");
    // model.put("scramble", Sequence.toString(Sequence.getScramble()));
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
    return MOVE_SAFE_GSON.toJson(
        new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
            MOVE_SAFE_GSON.toJsonTree(scrambles)));
  }

  private static String validateSolution(Request req, Response res) {
    res.type("application/json");
    String bodyStr = req.body();
    System.out.println(bodyStr);
    ClientSolution clientSolution = null;
    PochmannSolution pochmannSolution = null;
    try {
      clientSolution = RESPONSE_GSON.fromJson(bodyStr, ClientSolution.class);
      pochmannSolution = clientSolution.toPochmannSolution();
    } catch (Exception exception) {
      System.err.println(exception);
    }

    if (pochmannSolution != null) {
      System.out.println(pochmannSolution.toSequence());
      Cube cube = new Cube();
      cube.execute(Sequence.getSequence(clientSolution.scramble));
      boolean validSolution = cube.validSolution(pochmannSolution);
      SolutionResponse solutionResponse = new SolutionResponse(
          validSolution ? SolutionResponse.SolutionStatus.SUCCESS : SolutionResponse.SolutionStatus.FAILURE);
      String response = RESPONSE_GSON.toJson(
          new StandardResponse(StandardResponse.StatusResponse.SUCCESS, solutionResponse.toJsonElement()));
      System.out.println(response);
      return response;
    }
    return RESPONSE_GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR));
  }

  private static String exampleSolution(Request req, Response res) {
    res.type("application/json");
    String scramble = req.queryParams("scramble");
    if (scramble == null || scramble.trim().equals("")) {
      return RESPONSE_GSON
          .toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Server did not receive a scramble"));
    }
    try {
      Cube cube = new Cube();
      cube.execute(Sequence.getSequence(scramble));
      PochmannSolution solution = Pochmann.getSolution(cube);
      String response = MOVE_SAFE_GSON
          .toJson(StandardResponse.getWithSolution(Sequence.toString(solution.toSequence())));
      System.out.println(response);
      return response;
    } catch (Exception exception) {
      System.err.println(exception);
      return RESPONSE_GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "An error occurred"));
    }
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
    Spark.get("/exampleSolution",
        (req, res) -> exampleSolution(req, res));
  }
}
