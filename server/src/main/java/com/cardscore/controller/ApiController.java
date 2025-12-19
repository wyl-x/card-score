package com.cardscore.controller;

import com.cardscore.model.*;
import com.cardscore.service.CardScoreService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * REST API控制器
 */
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final CardScoreService service;
    private final Gson gson;

    public ApiController(CardScoreService service) {
        this.service = service;
        this.gson = new Gson();
    }
    
    /**
     * 记录API请求
     */
    private void logRequest(String method, String path, String body) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        logger.info("[{}] {} {} | Body: {}", timestamp, method, path, body != null ? body : "null");
    }
    
    /**
     * 记录API响应
     */
    private void logResponse(String method, String path, int statusCode, String response) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        logger.info("[{}] {} {} | Status: {} | Response: {}", timestamp, method, path, statusCode, 
                    response.length() > 500 ? response.substring(0, 500) + "..." : response);
    }

    /**
     * 设置所有路由
     */
    public void setupRoutes() {
        // CORS配置
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });

        // 设置响应类型
        after((request, response) -> {
            response.type("application/json; charset=utf-8");
        });

        // User API
        path("/api/users", () -> {
            post("", this::createUser);
            get("", this::getAllUsers);
            get("/:id", this::getUser);
        });

        // Room API
        path("/api/rooms", () -> {
            post("", this::createRoom);
            get("", this::getAllRooms);
            get("/search", this::searchRooms);
            get("/:id", this::getRoom);
            post("/:id/join", this::joinRoom);
            post("/:id/leave", this::leaveRoom);
            get("/:id/detail", this::getRoomDetail);
        });

        // Transaction API
        path("/api/transactions", () -> {
            post("", this::createTransaction);
            get("/room/:roomId", this::getRoomTransactions);
            get("/room/:roomId/details", this::getTransactionDetails);
        });

        // Health check
        get("/health", (req, res) -> {
            return gson.toJson(Map.of("status", "OK", "timestamp", System.currentTimeMillis()));
        });

        // Exception handling
        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(gson.toJson(ApiResponse.error(exception.getMessage())));
        });
    }

    // ============= User Controllers =============

    private String createUser(Request req, Response res) {
        logRequest("POST", "/api/users", req.body());
        try {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String name = body.get("name");
            
            User user = service.createUser(name);
            res.status(201);
            String response = gson.toJson(ApiResponse.success(user));
            logResponse("POST", "/api/users", 201, response);
            return response;
        } catch (Exception e) {
            res.status(400);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("POST", "/api/users", 400, response);
            return response;
        }
    }

    private String getAllUsers(Request req, Response res) {
        logRequest("GET", "/api/users", null);
        List<User> users = service.getAllUsers();
        String response = gson.toJson(ApiResponse.success(users));
        logResponse("GET", "/api/users", 200, response);
        return response;
    }

    private String getUser(Request req, Response res) {
        String userId = req.params(":id");
        logRequest("GET", "/api/users/" + userId, null);
        try {
            User user = service.getUser(userId);
            String response = gson.toJson(ApiResponse.success(user));
            logResponse("GET", "/api/users/" + userId, 200, response);
            return response;
        } catch (Exception e) {
            res.status(404);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("GET", "/api/users/" + userId, 404, response);
            return response;
        }
    }

    // ============= Room Controllers =============

    private String createRoom(Request req, Response res) {
        logRequest("POST", "/api/rooms", req.body());
        try {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String name = body.get("name");
            String creatorId = body.get("creatorId");
            
            Room room = service.createRoom(name, creatorId);
            res.status(201);
            String response = gson.toJson(ApiResponse.success(room));
            logResponse("POST", "/api/rooms", 201, response);
            return response;
        } catch (Exception e) {
            res.status(400);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("POST", "/api/rooms", 400, response);
            return response;
        }
    }

    private String getAllRooms(Request req, Response res) {
        logRequest("GET", "/api/rooms", null);
        List<Room> rooms = service.getAllRooms();
        String response = gson.toJson(ApiResponse.success(rooms));
        logResponse("GET", "/api/rooms", 200, response);
        return response;
    }

    private String searchRooms(Request req, Response res) {
        String keyword = req.queryParams("keyword");
        logRequest("GET", "/api/rooms/search?keyword=" + keyword, null);
        List<Room> rooms = service.searchRooms(keyword);
        String response = gson.toJson(ApiResponse.success(rooms));
        logResponse("GET", "/api/rooms/search?keyword=" + keyword, 200, response);
        return response;
    }

    private String getRoom(Request req, Response res) {
        String roomId = req.params(":id");
        logRequest("GET", "/api/rooms/" + roomId, null);
        try {
            Room room = service.getRoom(roomId);
            String response = gson.toJson(ApiResponse.success(room));
            logResponse("GET", "/api/rooms/" + roomId, 200, response);
            return response;
        } catch (Exception e) {
            res.status(404);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("GET", "/api/rooms/" + roomId, 404, response);
            return response;
        }
    }

    private String joinRoom(Request req, Response res) {
        String roomId = req.params(":id");
        logRequest("POST", "/api/rooms/" + roomId + "/join", req.body());
        try {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String userId = body.get("userId");
            
            Room room = service.joinRoom(roomId, userId);
            String response = gson.toJson(ApiResponse.success(room));
            logResponse("POST", "/api/rooms/" + roomId + "/join", 200, response);
            return response;
        } catch (Exception e) {
            res.status(400);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("POST", "/api/rooms/" + roomId + "/join", 400, response);
            return response;
        }
    }

    private String leaveRoom(Request req, Response res) {
        String roomId = req.params(":id");
        logRequest("POST", "/api/rooms/" + roomId + "/leave", req.body());
        try {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String userId = body.get("userId");
            
            service.leaveRoom(roomId, userId);
            String response = gson.toJson(ApiResponse.success(Map.of("message", "已退出房间")));
            logResponse("POST", "/api/rooms/" + roomId + "/leave", 200, response);
            return response;
        } catch (Exception e) {
            res.status(400);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("POST", "/api/rooms/" + roomId + "/leave", 400, response);
            return response;
        }
    }

    private String getRoomDetail(Request req, Response res) {
        String roomId = req.params(":id");
        logRequest("GET", "/api/rooms/" + roomId + "/detail", null);
        try {
            Map<String, Object> detail = service.getRoomDetail(roomId);
            String response = gson.toJson(ApiResponse.success(detail));
            logResponse("GET", "/api/rooms/" + roomId + "/detail", 200, response);
            return response;
        } catch (Exception e) {
            res.status(404);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("GET", "/api/rooms/" + roomId + "/detail", 404, response);
            return response;
        }
    }

    // ============= Transaction Controllers =============

    private String createTransaction(Request req, Response res) {
        logRequest("POST", "/api/transactions", req.body());
        try {
            Map<String, Object> body = gson.fromJson(req.body(), Map.class);
            String roomId = (String) body.get("roomId");
            String fromUserId = (String) body.get("fromUserId");
            String toUserId = (String) body.get("toUserId");
            int amount = ((Double) body.get("amount")).intValue();
            
            Transaction transaction = service.createTransaction(roomId, fromUserId, toUserId, amount);
            res.status(201);
            String response = gson.toJson(ApiResponse.success(transaction));
            logResponse("POST", "/api/transactions", 201, response);
            return response;
        } catch (Exception e) {
            res.status(400);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("POST", "/api/transactions", 400, response);
            return response;
        }
    }

    private String getRoomTransactions(Request req, Response res) {
        String roomId = req.params(":roomId");
        logRequest("GET", "/api/transactions/room/" + roomId, null);
        try {
            List<Transaction> transactions = service.getRoomTransactions(roomId);
            String response = gson.toJson(ApiResponse.success(transactions));
            logResponse("GET", "/api/transactions/room/" + roomId, 200, response);
            return response;
        } catch (Exception e) {
            res.status(404);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("GET", "/api/transactions/room/" + roomId, 404, response);
            return response;
        }
    }

    private String getTransactionDetails(Request req, Response res) {
        String roomId = req.params(":roomId");
        logRequest("GET", "/api/transactions/room/" + roomId + "/details", null);
        try {
            List<Map<String, Object>> details = service.getTransactionDetails(roomId);
            String response = gson.toJson(ApiResponse.success(details));
            logResponse("GET", "/api/transactions/room/" + roomId + "/details", 200, response);
            return response;
        } catch (Exception e) {
            res.status(404);
            String response = gson.toJson(ApiResponse.error(e.getMessage()));
            logResponse("GET", "/api/transactions/room/" + roomId + "/details", 404, response);
            return response;
        }
    }
}
