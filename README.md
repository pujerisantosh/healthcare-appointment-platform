\# 🏥 Healthcare Appointment Platform



A scalable healthcare appointment management system built with Spring Boot, Python, Kafka, PostgreSQL, and React.



\## 🏗 Architecture



React Frontend → Spring Boot API → Kafka → Python Worker



↓



PostgreSQL



\## 🛠 Tech Stack



| Layer | Technology |

|-------|-----------|

| Backend API | Spring Boot 3.2.5 |

| Database | PostgreSQL |

| Messaging | Apache Kafka |

| Worker Service | Python 3 |

| Frontend | React + Vite |

| Auth | JWT |

| API Docs | Swagger / OpenAPI |



\## ✅ Features



\- JWT Authentication (Register/Login)

\- Book Appointments (duplicate prevention)

\- Cancel Appointments

\- View Appointment History

\- Available Slots Check

\- Kafka Event-driven Notifications

\- Python Worker processes events



\## 🚀 Setup Instructions



\### Prerequisites

\- Java 21

\- Python 3

\- PostgreSQL

\- Apache Kafka

\- Node.js



\### 1. Database

```sql

CREATE DATABASE healthcare\_db;

```



\### 2. Start Kafka

```bash

\# Window 1

.\\bin\\windows\\zookeeper-server-start.bat .\\config\\zookeeper.properties



\# Window 2

.\\bin\\windows\\kafka-server-start.bat .\\config\\server.properties

```



\### 3. Start Spring Boot

```bash

cd healthcare-backend

./mvnw spring-boot:run

```



\### 4. Start Python Worker

```bash

cd python-worker

pip install kafka-python psycopg2-binary

python worker.py

```



\### 5. Start Frontend

```bash

cd frontend

npm install

npm run dev

```



\## 📡 API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html



\## 🔐 API Endpoints



| Method | Endpoint | Description |

|--------|----------|-------------|

| POST | /api/auth/register | Register user |

| POST | /api/auth/login | Login user |

| POST | /api/appointments | Book appointment |

| DELETE | /api/appointments/{id} | Cancel appointment |

| GET | /api/appointments | Get user appointments |

| GET | /api/appointments/slots | Get available slots |



\## 🔄 Event Flow

1\. User books appointment via Spring Boot API

2\. Spring Boot publishes event to Kafka

3\. Python worker consumes event

4\. Notification logged and status updated

