package com.cardscore.service;

import com.cardscore.model.Room;
import com.cardscore.model.Transaction;
import com.cardscore.model.User;
import com.cardscore.storage.LocalStorage;

import java.util.*;

/**
 * 业务服务层
 */
public class CardScoreService {
    private final LocalStorage storage;

    public CardScoreService(LocalStorage storage) {
        this.storage = storage;
    }

    // ============= User Services =============

    /**
     * 创建用户（如果用户名已存在则返回现有用户）
     */
    public User createUser(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("用户名不能为空");
        }

        // 检查用户名是否重复，如果存在则返回现有用户
        User existing = storage.getUserByName(name);
        if (existing != null) {
            return existing;
        }

        String userId = UUID.randomUUID().toString();
        User user = new User(userId, name, System.currentTimeMillis());
        storage.addUser(user);
        
        return user;
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    /**
     * 根据ID获取用户
     */
    public User getUser(String userId) throws Exception {
        User user = storage.getUser(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        return user;
    }

    // ============= Room Services =============

    /**
     * 创建房间
     */
    public Room createRoom(String name, String creatorId) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("房间名不能为空");
        }

        // 验证创建者存在
        User creator = storage.getUser(creatorId);
        if (creator == null) {
            throw new Exception("用户不存在");
        }

        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, name, System.currentTimeMillis());
        room.addMember(creatorId);
        
        storage.addRoom(room);
        return room;
    }

    /**
     * 获取所有房间
     */
    public List<Room> getAllRooms() {
        return storage.getAllRooms();
    }

    /**
     * 搜索房间
     */
    public List<Room> searchRooms(String keyword) {
        return storage.searchRooms(keyword);
    }

    /**
     * 根据ID获取房间
     */
    public Room getRoom(String roomId) throws Exception {
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }
        return room;
    }

    /**
     * 加入房间
     */
    public Room joinRoom(String roomId, String userId) throws Exception {
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }

        User user = storage.getUser(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        room.addMember(userId);
        storage.updateRoom(room);
        
        // 更新用户的当前房间ID
        user.setCurrentRoomId(roomId);
        storage.updateUser(user);
        
        return room;
    }

    /**
     * 离开房间
     */
    public void leaveRoom(String roomId, String userId) throws Exception {
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }

        User user = storage.getUser(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 从房间成员列表中移除
        room.getMemberIds().remove(userId);
        storage.updateRoom(room);
        
        // 清除用户的当前房间ID
        if (roomId.equals(user.getCurrentRoomId())) {
            user.setCurrentRoomId(null);
            storage.updateUser(user);
        }
    }

    /**
     * 获取房间详情（包含用户信息和分数）
     */
    public Map<String, Object> getRoomDetail(String roomId) throws Exception {
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("room", room);

        // 获取成员信息，同时清理不存在的用户
        List<User> members = new ArrayList<>();
        List<String> memberIdsToRemove = new ArrayList<>();
        boolean needUpdate = false;
        
        for (String memberId : room.getMemberIds()) {
            User user = storage.getUser(memberId);
            if (user != null) {
                members.add(user);
            } else {
                // 用户不存在，标记为需要移除
                memberIdsToRemove.add(memberId);
                needUpdate = true;
            }
        }
        
        // 从房间中移除不存在的用户
        if (needUpdate) {
            for (String memberIdToRemove : memberIdsToRemove) {
                room.getMemberIds().remove(memberIdToRemove);
            }
            storage.updateRoom(room);
        }
        
        detail.put("members", members);

        // 计算分数
        Map<String, Integer> scores = room.calculateScores();
        detail.put("scores", scores);

        return detail;
    }

    // ============= Transaction Services =============

    /**
     * 创建转账记录
     */
    public Transaction createTransaction(String roomId, String fromUserId, String toUserId, int amount) throws Exception {
        // 验证房间存在
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }

        // 验证用户存在且都在房间中
        if (!room.getMemberIds().contains(fromUserId)) {
            throw new Exception("转账用户不在房间中");
        }
        if (!room.getMemberIds().contains(toUserId)) {
            throw new Exception("收款用户不在房间中");
        }

        // 验证转账金额
        if (amount <= 0) {
            throw new Exception("转账金额必须大于0");
        }

        // 创建转账记录
        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
            transactionId,
            roomId,
            fromUserId,
            toUserId,
            amount,
            System.currentTimeMillis()
        );

        // 添加到房间
        room.addTransaction(transaction);
        storage.updateRoom(room);

        return transaction;
    }

    /**
     * 获取房间的所有转账记录
     */
    public List<Transaction> getRoomTransactions(String roomId) throws Exception {
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }
        return room.getTransactions();
    }

    /**
     * 获取转账详情（包含用户名）
     */
    public List<Map<String, Object>> getTransactionDetails(String roomId) throws Exception {
        Room room = storage.getRoom(roomId);
        if (room == null) {
            throw new Exception("房间不存在");
        }

        List<Map<String, Object>> details = new ArrayList<>();
        for (Transaction transaction : room.getTransactions()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("transaction", transaction);

            User fromUser = storage.getUser(transaction.getFromUserId());
            User toUser = storage.getUser(transaction.getToUserId());
            
            detail.put("fromUserName", fromUser != null ? fromUser.getName() : "Unknown");
            detail.put("toUserName", toUser != null ? toUser.getName() : "Unknown");
            
            details.add(detail);
        }

        return details;
    }
}
