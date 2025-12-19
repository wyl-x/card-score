package com.cardscore.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间实体类
 */
public class Room {
    private String id;
    private String name;
    private long createdAt;
    private List<String> memberIds;  // 成员用户ID列表
    private List<Transaction> transactions;  // 转账记录

    public Room() {
        this.memberIds = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public Room(String id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.memberIds = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * 添加成员
     */
    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    /**
     * 添加转账记录
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * 计算每个用户的总分
     */
    public Map<String, Integer> calculateScores() {
        Map<String, Integer> scores = new HashMap<>();
        
        // 初始化所有成员分数为0
        for (String memberId : memberIds) {
            scores.put(memberId, 0);
        }
        
        // 计算转账后的分数
        for (Transaction transaction : transactions) {
            String fromId = transaction.getFromUserId();
            String toId = transaction.getToUserId();
            int amount = transaction.getAmount();
            
            scores.put(fromId, scores.getOrDefault(fromId, 0) - amount);
            scores.put(toId, scores.getOrDefault(toId, 0) + amount);
        }
        
        return scores;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", memberIds=" + memberIds +
                ", transactions=" + transactions.size() +
                '}';
    }
}
