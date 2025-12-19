package com.cardscore;

import com.cardscore.controller.ApiController;
import com.cardscore.service.CardScoreService;
import com.cardscore.storage.LocalStorage;

import static spark.Spark.*;

/**
 * 应用主类
 */
public class Application {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        // 设置端口
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: " + DEFAULT_PORT);
            }
        }
        port(port);

        // 初始化组件
        LocalStorage storage = new LocalStorage();
        CardScoreService service = new CardScoreService(storage);
        ApiController controller = new ApiController(service);

        // 设置路由
        controller.setupRoutes();

        // 启动成功日志
        System.out.println("==================================");
        System.out.println("Card Score Server Started");
        System.out.println("Port: " + port);
        System.out.println("==================================");
        System.out.println("API Endpoints:");
        System.out.println("  POST   /api/users                    - Create user");
        System.out.println("  GET    /api/users                    - Get all users");
        System.out.println("  GET    /api/users/:id                - Get user by ID");
        System.out.println("  POST   /api/rooms                    - Create room");
        System.out.println("  GET    /api/rooms                    - Get all rooms");
        System.out.println("  GET    /api/rooms/search?keyword=... - Search rooms");
        System.out.println("  GET    /api/rooms/:id                - Get room by ID");
        System.out.println("  POST   /api/rooms/:id/join           - Join room");
        System.out.println("  POST   /api/rooms/:id/leave          - Leave room");
        System.out.println("  GET    /api/rooms/:id/detail         - Get room detail");
        System.out.println("  POST   /api/transactions             - Create transaction");
        System.out.println("  GET    /api/transactions/room/:roomId - Get room transactions");
        System.out.println("  GET    /api/transactions/room/:roomId/details - Get transaction details");
        System.out.println("  GET    /health                       - Health check");
        System.out.println("==================================");
    }
}
