# Card Score Tracking Tool

[中文文档](Readme.md)

A card game scoring and accounting tool with real-time transaction tracking and voice announcements.

## Features

1. **User Management**
   - Quick user creation with name only
   - Local persistent storage
   - Unique username validation

2. **Room Management**
   - Room list with search functionality
   - Create and join rooms
   - Persistent room data

3. **Transaction System**
   - Transfer points to other users
   - Quick transfer buttons (configurable amounts)
   - Custom amount input
   - Voice announcements for all transactions

4. **Real-time Updates**
   - Live transaction history
   - Real-time score calculation
   - Persistent transaction records

5. **Data Persistence**
   - Users can rejoin rooms
   - All transaction history preserved
   - Local file-based storage

## Technology Stack

### Server
- Java 17+
- Spark Java (REST API)
- Gson (JSON processing)
- Local file storage

### Client
- Flutter 3.0+
- HTTP client
- Provider (state management)
- Flutter TTS (voice announcements)
- Shared Preferences (local storage)

## Quick Start

See [BUILD_AND_RUN.md](BUILD_AND_RUN.md) for detailed build and deployment instructions.

See [CONFIG.md](CONFIG.md) for configuration options.

## Project Structure

```
card-score/
├── server/                 # Java server
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── cardscore/
│   │                   ├── Application.java
│   │                   ├── controller/
│   │                   ├── model/
│   │                   ├── service/
│   │                   └── storage/
│   └── pom.xml
│
├── client/                 # Flutter client
│   ├── lib/
│   │   ├── main.dart
│   │   ├── models/
│   │   ├── screens/
│   │   └── services/
│   └── pubspec.yaml
│
├── Readme.md
├── BUILD_AND_RUN.md
└── CONFIG.md
```

## License

MIT License
