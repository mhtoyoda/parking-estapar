# 🅿️ Parking Management System

A backend system to manage a parking garage: track available spots, handle vehicle entry/exit events, and calculate revenue — built for the **Estapar Backend Developer Test**.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.1 |
| Framework | Spring Boot 4.1.0 |
| Database | MySQL 8 |
| Build | Maven |
| Runtime | Java 21 |
| Containers | Docker / Docker Compose |

---

## 📐 Architecture

```
Simulator (garage-sim)
     │
     ├──► GET /garage          ──► GarageInitService   ──► MySQL (sectors + spots)
     │
     └──► POST /webhook
               ├── ENTRY   ──► ParkingService ──► validate capacity + save event
               ├── PARKED  ──► ParkingService ──► occupy spot + assign sector
               └── EXIT    ──► ParkingService ──► PricingService ──► calculate fare

Client
     └──► GET /revenue  ──► RevenueService ──► MySQL (sum by sector/date)
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven
- Docker

### 1. Start the infrastructure

```bash
docker compose up -d
```

### 2. Run the application

```bash
mvn spring-boot:run
```

The app starts on **port 3003** and automatically fetches the garage configuration from the simulator on startup.

---

## ⚙️ Configuration

```yaml
# application.yml
server:
  port: 3003

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parking
    username: root
    password: root

garage:
  simulator-url: http://localhost:9000
```

---

## 📡 API Reference

### `POST /webhook`

Receives vehicle events from the simulator.

**ENTRY**
```json
{
  "event_type": "ENTRY",
  "license_plate": "ZUL0001",
  "entry_time": "2025-01-01T12:00:00.000Z"
}
```

**PARKED**
```json
{
  "event_type": "PARKED",
  "license_plate": "ZUL0001",
  "lat": -23.561684,
  "lng": -46.655981
}
```

**EXIT**
```json
{
  "event_type": "EXIT",
  "license_plate": "ZUL0001",
  "exit_time": "2025-01-01T13:30:00.000Z"
}
```

All events return `HTTP 200`.

---

### `GET /revenue`

Returns total revenue for a given sector and date.

**Request**
```json
{
  "date": "2025-01-01",
  "sector": "A"
}
```

**Response**
```json
{
  "amount": 47.50,
  "currency": "BRL",
  "timestamp": "2025-01-01T14:00:00.000Z"
}
```

---

## 💰 Pricing Rules

### Base calculation

| Stay duration | Charge |
|---|---|
| ≤ 30 minutes | Free |
| > 30 minutes | `ceil(minutes / 60) × hourly rate` |

### Dynamic pricing by occupancy

| Occupancy | Price adjustment |
|---|---|
| < 25% | −10% (discount) |
| 25% – 50% | No change |
| 50% – 75% | +10% surcharge |
| 75% – 100% | +25% surcharge |

### Garage capacity

When all sectors reach 100% occupancy, the garage is **closed** and new entries are blocked until a spot is freed.

---

## 🗂️ Project Structure

```
src/main/kotlin/com/estapar/parking/
├── config/
│   └── AppConfig.kt              # RestTemplate + ObjectMapper beans
├── controller/
│   ├── WebhookController.kt      # POST /webhook
│   └── RevenueController.kt      # GET /revenue
├── service/
│   ├── GarageInitService.kt      # Loads garage config on startup
│   ├── ParkingService.kt         # Entry / parked / exit logic
│   ├── PricingService.kt         # Dynamic pricing calculation
│   └── RevenueService.kt         # Revenue aggregation
├── model/
│   ├── Sector.kt
│   ├── Spot.kt
│   └── ParkingEvent.kt
├── repository/
│   ├── SectorRepository.kt
│   ├── SpotRepository.kt
│   └── ParkingEventRepository.kt
└── dto/
    ├── WebhookEventDTO.kt
    ├── GarageConfigDTO.kt
    ├── RevenueRequestDTO.kt
    └── RevenueResponseDTO.kt
```

---

## 🧪 Running Tests

```bash
mvn test
```

Tests cover:

- ✅ Free parking under 30 minutes
- ✅ Correct hourly rounding (ceiling)
- ✅ 10% discount at low occupancy (< 25%)
- ✅ 25% surcharge at high occupancy (> 75%)

---

## 🐳 Docker Compose

```yaml
services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: parking
    ports:
      - "3306:3306"

  simulator:
    image: cfontes0estapar/garage-sim:1.0.0
    network_mode: host
```

---

## 📝 License

This project was developed as part of a technical assessment for Estapar.