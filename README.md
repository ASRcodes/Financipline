# Financipline 🚀💰

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg?style=for-the-badge)](https://developer.android.com/topic/libraries/architecture)

> **"Discipline your finances, automatically."**

**Financipline** is an intelligent expense manager designed to eliminate the "laziness barrier" of manual tracking. By leveraging real-time notification listeners, the app automatically detects your UPI and bank transactions, allowing you to categorize them with a single tap.

---

## 📸 App Interface
| Dashboard & Math | Transaction Detection | Saving Streaks |
| :---: | :---: | :---: |
| ![Dashboa<img width="289" height="633" alt="Screenshot 2026-02-16 002045" src="https://github.com/user-attachments/assets/b3096ef1-e1e3-4824-80ff-c0eb080e1cf9" />
rd] | ![Analyt<img width="273" height="621" alt="Screenshot 2026-02-16 001957" src="https://github.com/user-attachments/assets/b0fa9699-c8fc-4930-a64d-a4c0290075c3" />
ics] | ![History<img width="280" height="624" alt="Screenshot 2026-02-16 002123" src="https://github.com/user-attachments/assets/19a1e44a-9719-4f99-aae9-7060bcbfd786" />
] |
---

## ✨ The Core "Crux" (Features)
- 👂 **Automated "Ear" Tracking:** Uses a `NotificationListenerService` to catch payment alerts from apps like PhonePe, GPay, and Zomato in real-time.
- 🎯 **Daily Discipline Math:** Set a monthly budget and the app calculates your **Daily Remaining Limit** automatically to keep you on track.
- 🔥 **Streak System:** Gamifies your financial health! Stay under your daily limit to grow your saving streak.
- ⚡ **Zero-Manual Entry:** Regex patterns identify amounts and merchants from notifications. You only tap to confirm the category.

## 🛠️ Tech Stack
- **Frontend:** Native Android (Kotlin)
- **Backend:** Supabase (Postgrest & Auth)
- **Architecture:** MVVM with ViewBinding
- **Services:** Android NotificationListenerService & Regex Pattern Matching

## 🏗️ How It Works
1. **Detection:** The `NotificationService` intercepts a payment alert (e.g., "Paid ₹50 to Zomato").
2. **Extraction:** Intelligent Regex patterns identify the specific amount and merchant name.
3. **Sync:** The transaction is pushed to a **Supabase** expenses table as a "Pending" item.
4. **Confirmation:** The user receives a card in the *Action Required* section of the Dashboard to finalize the category.
5. **Impact:** Once categorized, the **Daily Progress Ring** updates instantly.

## 🚀 Getting Started
1. **Clone the Repo:**
   ```bash
   git clone [https://github.com/ASRcodes/Financipline.git](https://github.com/ASRcodes/Financipline.git)
