package com.cardscore.storage;

import com.cardscore.model.Room;
import com.cardscore.model.Transaction;
import com.cardscore.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地文件存储管理器
 */
public class LocalStorage {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = "users.json";
    private static final String ROOMS_FILE = "rooms.json";

    private final Gson gson;
    private final Map<String, User> users;
    private final Map<String, Room> rooms;

    public LocalStorage() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.users = new ConcurrentHashMap<>();
        this.rooms = new ConcurrentHashMap<>();
        
        initDataDirectory();
        loadData();
    }

    /**
     * 初始化数据目录
     */
    private void initDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {
        loadUsers();
        loadRooms();
    }

    /**
     * 加载用户数据
     */
    private void loadUsers() {
        File file = new File(DATA_DIR, USERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<User>>(){}.getType();
            List<User> userList = gson.fromJson(reader, type);
            if (userList != null) {
                for (User user : userList) {
                    users.put(user.getId(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    /**
     * 加载房间数据
     */
    private void loadRooms() {
        File file = new File(DATA_DIR, ROOMS_FILE);
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Room>>(){}.getType();
            List<Room> roomList = gson.fromJson(reader, type);
            if (roomList != null) {
                for (Room room : roomList) {
                    rooms.put(room.getId(), room);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load rooms: " + e.getMessage());
        }
    }

    /**
     * 保存用户数据
     */
    public synchronized void saveUsers() {
        File file = new File(DATA_DIR, USERS_FILE);
        try (Writer writer = new FileWriter(file)) {
            List<User> userList = new ArrayList<>(users.values());
            gson.toJson(userList, writer);
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    /**
     * 保存房间数据
     */
    public synchronized void saveRooms() {
        File file = new File(DATA_DIR, ROOMS_FILE);
        try (Writer writer = new FileWriter(file)) {
            List<Room> roomList = new ArrayList<>(rooms.values());
            gson.toJson(roomList, writer);
        } catch (IOException e) {
            System.err.println("Failed to save rooms: " + e.getMessage());
        }
    }

    // User operations
    public User getUser(String id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserByName(String name) {
        return users.values().stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        saveUsers();
    }

    public void updateUser(User user) {
        users.put(user.getId(), user);
        saveUsers();
    }

    public void deleteUser(String id) {
        users.remove(id);
        saveUsers();
    }

    // Room operations
    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Room> searchRooms(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRooms();
        }
        
        List<Room> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Room room : rooms.values()) {
            if (room.getName().toLowerCase().contains(lowerKeyword)) {
                result.add(room);
            }
        }
        return result;
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
        saveRooms();
    }

    public void updateRoom(Room room) {
        rooms.put(room.getId(), room);
        saveRooms();
    }

    public void deleteRoom(String id) {
        rooms.remove(id);
        saveRooms();
    }
}
