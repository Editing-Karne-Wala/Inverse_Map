# 🗺️ Inverse Map: Safety-First Routing Engine

**Inverse Map** is a navigation concept designed for women's safety. While most maps are optimized for the shortest or fastest route, **Inverse Map** is optimized for **Human Presence.** It prioritizes crowded, well-lit, and arterial roads over deserted alleys and shortcuts.

---

## 🚀 The Mission: "Strength in Numbers"
The core hypothesis is that high-density, crowded areas act as a natural deterrent against assault and kidnapping. By routing users through areas with more "eyes on the road," we provide a safer commute during late-night hours.

---

## 🧠 The Logic: Inverse Weighting
Standard GPS uses **Dijkstra’s Algorithm** with distance as the weight ($W = Distance$).
**Inverse Map** modifies the weight based on a **Crowd Factor**:

$$W_{safety} = \frac{Distance}{(CrowdFactor)^\alpha} \cdot RiskScore$$

*   **Distance**: Physical length of the road segment.
*   **Crowd Factor (0.1 - 1.0)**: Proxy derived from live traffic congestion data.
*   **Alpha ($\alpha$)**: Sensitivity parameter. Higher alpha makes the algorithm prefer even longer detours if they are significantly more crowded.
*   **Risk Score**: Multiplier applied to known high-threat zones (e.g., Red light districts or unlit parks).

---

## 🛠️ Architecture
-   **Backend (Java/JGraphT)**: The core graph engine implementing the weighted Dijkstra algorithm.
-   **Live Layer (Python/Flask)**: A reactive bridge that pulls real-time traffic data from the Google Directions API and translates it into "Crowd Weights."
-   **Frontend (JS/Google Maps)**: A dark-mode visualization layer that highlights the "Safe Path" in Neo-Green.

---

## 🚦 Getting Started (Plug & Play)

### 1. Prerequisites
-   **Python 3.x** (for the live server)
-   **Java 17+** (for the core logic engine)
-   **Google Maps API Key** (Directions, Places, and JavaScript API enabled)

### 2. Live Startup (MVP Mode)
1.  Navigate to the project directory.
2.  Install dependencies:
    ```bash
    pip install flask flask-cors requests
    ```
3.  Launch the bridge:
    ```bash
    python app.py
    ```
4.  Open `http://localhost:5000` in your browser.

### 3. Java Logic Integration
To use the core routing engine in your own Java project:
-   Import `com.safety.route.SafetyRouter`.
-   Use `router.addRiskZone()` to flag dangerous areas.
-   Use `router.findSafestRoute(start, end)` to get the safety-weighted path.

---

## 📍 Kolkata MVP Focus
The current demo is pre-tuned for the **Kolkata Metropolitan Area**, specifically targeting commuter routes like **Madhyamgram to Rajarhat** through the **Biswa Bangla Arterial Road**, ensuring commuters stay on major visibility corridors.

---

## ⚖️ Disclaimer
This is an MVP model. "Crowd" is a proxy for safety but does not guarantee total protection. This tool is designed to be one part of a broader personal safety strategy.
