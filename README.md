# VitaVault – AI-Powered Mobile Health Tracker

Most health apps are clunky, confusing, or just don’t work. That’s why we built **VitaVault** — an AI-powered mobile health tracker designed to make managing your health smarter, simpler, and actually usable.

---

## 🩺 Features

- ✅ Track health vitals, symptoms, and medication intake
- ✅ Set personalized medication reminders
- ✅ Visualize trends in vitals through intuitive data charts
- ✅ Add and manage dietary restrictions
- ✅ Scan ingredients using OCR to detect dietary conflicts (dairy, vegan, non-halal, kosher, etc.)
- ✅ Get categorized matches and flagged ingredients using **Gemini AI**
- ✅ Generate comprehensive symptom reports for consultations (PDF export)

---

## 🛠 Built With

- **Android (Java)**
- **Firebase** – Authentication, Firestore, and Cloud Storage
- **Google ML Kit** – OCR scanning
- **Gemini AI** – Intelligent ingredient analysis
- **Graph and PDF Libraries** – For visualizations and export

---

## 🔐 API Key Management

This project uses a **Gemini AI API key** stored securely in your local machine using `local.properties`.

> 🔒 `local.properties` is **excluded from version control** and must not be committed.

To run the app locally:

1. Create a `local.properties` file (if it doesn’t exist).
2. Add your Gemini API key like this:

```properties
GEMINI_API_KEY=your-api-key-here
