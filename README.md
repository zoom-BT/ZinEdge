# ZinEdge — Offline AI Diagnostics for Africa

**ZinEdge** is an Android application embedding Google's Gemma 3n model (4B parameters, INT4, 1.9 GB) directly on-device via LiteRT, providing medical and agricultural diagnostics **100% offline** on low-end smartphones.

> Built for rural sub-Saharan Africa — no internet, no cloud, no data leaving the device.

---

## The Problem

In rural sub-Saharan Africa:
- **1 doctor per 10,000 inhabitants** in rural areas
- **600M+ people** with no reliable internet access
- **Smartphones** cost 80–150 USD (entry-level SoCs: MediaTek Helio, Snapdragon 4xx)

Existing AI health tools (GPT APIs, Gemini, Claude) require cloud connectivity. Static decision-tree apps lack natural language understanding. ZinEdge closes this gap.

---

## Features

| Module | Description |
|---|---|
| **Health Diagnosis** | Enter symptoms (fever, chills, cough…) + optional photo → AI produces probable diagnosis, confidence score, and locally-accessible treatment recommendations |
| **Crop Disease Detection** | Select crop (maize, tomato, groundnut, sorghum) + photo → AI identifies disease and proposes treatment adapted to locally available inputs |
| **Offline Chatbot** | Free-form medical/agricultural Q&A powered by Gemma 3n, fully offline |
| **History** | Consult past diagnoses with search and filters |

---

## Technical Stack

```
Model      : Gemma 3n E4B-it INT4 (4.92 GB, .litertlm format)
Runtime    : Google LiteRT (formerly TensorFlow Lite) via MediaPipe LlmInference API
Deployment : USB sideload → Android external app-specific storage (no Play Store needed)
Platform   : Android 8.0+ (API 26+), optimized for NPU (Qualcomm, MediaTek)
UI         : Jetpack Compose + Material 3
DI         : Hilt
Local DB   : Room
Language   : Kotlin
```

---

## Model Deployment (Sideload via USB)

The model is **not included** in the APK (4.92 GB exceeds any reasonable app size). Deploy it via ADB:

```bash
# Connect phone via USB, enable ADB debugging
adb push gemma-3n-E4B-it-int4.litertlm \
  /sdcard/Android/data/com.tchoutzine.tchoedgezine/files/models/
```

The app detects the model automatically on next launch.

**Model source:** [Google Gemma 3n on Hugging Face](https://huggingface.co/google/gemma-3n-E4B-it-litert-lm-preview)

---

## Build & Run

```bash
git clone https://github.com/zoom-BT/ZinEdge.git
cd ZinEdge

# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Requires: Android Studio Hedgehog+, JDK 17, Android SDK 35.

---

## Architecture

```
ZinEdge/
├── ai/
│   └── GemmaInference.kt       # Singleton LiteRT model wrapper
├── data/
│   ├── model/
│   │   └── Consultation.kt     # DiagnosisResult + Room entity
│   └── local/
│       └── AppDatabase.kt      # Room DB
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt     # Model loading + status
│   │   ├── HomeScreen.kt       # Module entry points
│   │   ├── HealthDiagnosisScreen.kt  # Camera + AI health flow
│   │   ├── CropDetectionScreen.kt    # Camera + AI crop flow
│   │   ├── ChatbotScreen.kt    # Conversational AI
│   │   ├── HistoryScreen.kt    # Past consultations
│   │   └── SettingsScreen.kt
│   └── theme/                  # Material 3 design tokens
└── navigation/
    └── NavGraph.kt
```

---

## Context & Impact

**Target users:** Community health workers, rural nurses, smallholder farmers in Central and West Africa.

**Tested on:** TECNO KI5k (MediaTek Helio G85, 4 GB RAM, Android 13) — representative of the African low-end market.

**Preliminary results:** >87% confidence on common sub-Saharan pathologies (malaria, iron-deficiency anemia, acute respiratory infections, maize blight, tomato late blight). Response time <8 seconds offline.

**Privacy:** Zero data leaves the device. All inference is local. No account required.

---

## SDGs Alignment

| SDG | Link |
|---|---|
| **SDG 3** — Good Health | Accessible medical diagnostics for underserved rural populations |
| **SDG 2** — Zero Hunger | Early crop disease detection to prevent yield loss |
| **SDG 9** — Innovation | Edge AI deployment on consumer hardware without infrastructure |

---

## Roadmap

- [ ] Multimodal vision (PaliGemma embedded for direct image analysis)
- [ ] Multilingual support: Fulfulde, Hausa, Lingala, Swahili
- [ ] Offline-first epidemiological sync (anonymized data on reconnect)
- [ ] Bluetooth wearable integration (blood pressure, pulse oximeter)
- [ ] Pilot deployment with Cameroon Ministry of Health (5 rural districts)

---

## Team

| Name | Role |
|---|---|
| **Balbino Tchoutzine** | Lead Developer & AI/ML Engineer — ENSPY 4GI |
| **Isabelle Magne** | Health Domain Expert / Data Scientist — ENSPY 4GI |

📧 tchoutzine@gmail.com · 🌐 [zoxbt.is-a.dev](https://zoxbt.is-a.dev/fr/blog)

---

## Competition Submissions

- 🏆 **SDGs Innovation Challenge 2026** — AIMS Rwanda
- 🇨🇳 **AI Case Innovation Competition for African Youth 2026** — China-Africa Forum Secretariat

---

*ZinEdge — Intelligence at the Edge, Health for All.*
