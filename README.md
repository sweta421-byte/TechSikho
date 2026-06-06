# TechSikho 🎮

> **Gamified Programming Learning Platform** built with Java Swing + MySQL

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-GUI-blue?style=for-the-badge)

---

## 🚀 Features

- 🎯 **Gamified Learning** — XP, Levels, Streaks, Daily Login Rewards
- ⚔️ **Boss Battles** — Timed quiz battles with bosses
- 🎮 **Mini Games** — Word Scramble, Rapid Fire, Mystery Language, Code Breaker
- 🏆 **Leaderboard** — Compete with other learners
- 🛒 **XP Shop** — Buy themes, avatars, power-ups with earned XP
- 📊 **Analytics Dashboard** — Track progress visually
- 🗺️ **Learning Path** — Beginner → Intermediate → Advanced roadmap
- 🔔 **Notifications** — Daily challenges and updates
- 🏅 **Achievements** — Unlock badges as you learn
- 📜 **Certificates** — Earn on course completion
- 👤 **Avatar System** — Customize your profile
- 🌐 **Multi-language Support** — Java, Python, Web, C++, and more

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17+ |
| UI Framework | Java Swing |
| Database | MySQL 8.0 |
| DB Connector | MySQL Connector/J |
| Build | Manual (javac) |

---

## 📁 Project Structure

\\\
TechSikho/
├── src/
│   └── com/techsikho/
│       ├── ui/           # All UI screens (Login, Dashboard, Quiz, etc.)
│       ├── dao/          # Data Access Objects (DB queries)
│       ├── models/       # User, Question, etc.
│       ├── services/     # XP, Auth logic
│       └── utils/        # DBConnection
├── lib/                  # JAR dependencies
├── bin/                  # Compiled classes
└── README.md
\\\

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- MySQL 8.0
- MySQL Connector/J JAR in \lib/\

### Steps

\\\ash
# 1. Clone the repo
git clone https://github.com/sweta421-byte/TechSikho.git
cd TechSikho

# 2. Setup MySQL database
mysql -u root -p < techsikho_db.sql

# 3. Update DB credentials in
# src/com/techsikho/utils/DBConnection.java

# 4. Compile
javac -encoding UTF-8 -d bin -cp "lib/*" \

# 5. Run
java -cp "bin:lib/*" com.techsikho.ui.LoginFrame
\\\

> **Windows:**
> \\\powershell
> javac -encoding UTF-8 -d bin -cp "lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { \.FullName })
> java -cp "bin;lib/*" com.techsikho.ui.LoginFrame
> \\\

---

## 👥 Team

Built by a team of 5 engineering students as a semester project.

| Role | Responsibility |
|------|---------------|
| Team Lead | Architecture & Integration |
| Senior Java Developer | Core Logic & Backend |
| UI/UX Designer | Swing UI Design |
| Database Engineer | MySQL Schema & Queries |
| Product Manager | Features & Testing |

---

## 📸 Screenshots

> Login Screen, Dashboard, Quiz, Boss Battle, Mini Games, Learning Path

---

## 📄 License

This project is for educational purposes.

---

*Made with ❤️ by Team TechSikho*
