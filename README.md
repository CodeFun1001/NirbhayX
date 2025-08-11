# 🚨 NirbhayX  
**Tap Fearlessly. Live NirbhayX.**

[![Platform](https://img.shields.io/badge/platform-Android-blue)]()  
[![Language](https://img.shields.io/badge/language-Kotlin-purple)]()  
[![Architecture](https://img.shields.io/badge/architecture-MVVM-green)]()

---

## 📖 Project Description
NirbhayX is a **modern personal safety application** designed to keep users secure and connected during emergencies.  
It enables **instant SOS alerts via power button**, **real-time location sharing**, **background audio/video recording**, **offline SMS alerts**, and **fake call escape tools** — all in a **bold, user-friendly Android app**.  

> [!IMPORTANT]
> Even without internet, your emergency contacts will receive SMS alerts using stored contact data from the local Room Database.

---

## 📸 Screenshots

### **Core Features**
| Home Screen | SOS Trigger / Notifications | Profile |
|-------------|-----------------------------|---------|
| <img src="screenshots/light/home.jpg" width="200"/> <img src="screenshots/dark/home.jpg" width="200"/> | <img src="screenshots/sos.jpg" width="200"/> <img src="screenshots/notifications.jpg" width="200"/> | <img src="screenshots/light/profile.jpg" width="200"/> <img src="screenshots/dark/profile.jpg" width="200"/> |

| Emergency Sharing | Emergency Contacts | Medical Info |
|-------------------|--------------------|--------------|
| <img src="screenshots/light/sharing.jpg" width="200"/> <img src="screenshots/dark/sharing.jpg" width="200"/> | <img src="screenshots/light/contacts.jpg" width="200"/> <img src="screenshots/dark/contacts.jpg" width="200"/> | <img src="screenshots/light/medical_info.jpg" width="200"/> <img src="screenshots/dark/medical_info.jpg" width="200"/> |

---

### **Additional Features**
| Safety Tips | Fake Call | Widgets / Lock Screen Notifications |
|-------------|-----------|--------------------------------------|
| <img src="screenshots/light/tips.jpg" width="200"/> <img src="screenshots/dark/tips.jpg" width="200"/> | <img src="screenshots/light/fakecall.jpg" width="200"/> <img src="screenshots/fakecall.jpg" width="200"/> | <img src="screenshots/widget.jpg" width="200"/> <img src="screenshots/notifications.jpg" width="200"/> |

| Community Safety Hub *(UI done, backend planned)* | Settings | About | Help |
|---------------------------------------------------|----------|-------|------|
| <img src="screenshots/light/community.jpg" width="200"/> | <img src="screenshots/light/settings.jpg" width="200"/> | <img src="screenshots/light/about.jpg" width="200"/> | <img src="screenshots/light/help.jpg" width="200"/> |

---

### **Auth & Branding**
| Login | App Logo | SignUp                                                |
|-------|----------|-------------------------------------------------------|
| <img src="screenshots/light/login.jpg" width="200"/> | <img src="screenshots/demo_thumbnail.jpg" width="200"/> | <img src="screenshots/light/signup.jpg" width="200"/> | 

---

## 🌐 Hosted URL
Not hosted — distributed as **APK installation**.
[📥 Download NirbhayX APK from Google Drive](https://drive.google.com/file/d/1q8-bdyoa6VkzULgO9xhv5InC396IB3Ym/view?usp=sharing)


---

## ✅ Features Implemented

### **Frontend**
- 🎨 Modern UI with **Jetpack Compose** & Material 3
- 🌗 **Light/Dark theme switching**
- 🧭 Persistent navigation (Drawer + Bottom Navigation + Modal Sheet)
- 🛡 UI ready for **Community Safety Hub** *(backend integration planned)*
- 📌 **App Widget** & **Lock Screen Notifications**
- 📞 **Fake Call Feature** with customizable caller ID & timer
- 🌏 Multilingual support *(planned)*

### **Backend**
- 🔐 Secure Authentication & profile management (**Firebase Auth**)
- 🚨 **Prominent SOS Button** for instant alerts
- 📍 **Real-Time Location Sharing** with pre-set contacts
- 🎥 **Background Audio/Video Recording** on SOS trigger
- ☁ **Firebase Firestore** for cloud data
- 💾 **Room Database** for offline storage of contacts/logs
- 📂 **Firebase Storage** for media storage
- 📜 Detailed **Activity Logs** for all alerts & actions
- 🚑 **Medical Information** Storage & Display on Lock Screen (blood type, allergies, emergency contacts — accessible during emergencies)

---

## 🛠️ Tech Stack
- **Language**: Kotlin  
- **UI**: Jetpack Compose, Material 3  
- **Architecture**: MVVM  
- **Database**: Room Database, Firebase Firestore  
- **Backend Services**: Firebase Auth, Firebase Storage  
- **Others**: OneSignal SDK, Google Play Services Location  

---

## ⚙️ Local Setup

### **Prerequisites**
- Android Studio **Giraffe+**
- Java 17+
- Firebase Project setup

### **Steps**
1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/nirbhayx.git

### Steps to Setup the Project Locally
1. Open the project in **Android Studio**.
2. Place `google-services.json` in the `/app` folder.
3. Sync **Gradle**.
4. Build and run on an Android device or emulator (**Min SDK 21**).
5. *(Optional)* To test **SMS alerts offline** — grant **SMS** & **CALL** permissions on the device and add emergency contacts in-app.

---

## Team Member
- **Beejasani S. Patil** *(Solo Developer — Android & Backend)*

---

## 🎥 Demo Video
[![Watch the demo](screenshots/demo_thumbnail.jpg)](https://drive.google.com/file/d/10afTYZRNaFWXbQPCK02xNDOycP5-v7DV/view?usp=drive_link)


