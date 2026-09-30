# ⏰ Practical 4 — Android Alarm Application

> **Aim:** Develop an Android Alarm application using **AlarmManager, BroadcastReceiver and Service** in Kotlin.

---

## 🎯 Objective

This practical demonstrates how an Android application can schedule an alarm for a selected time and play an alarm sound when that time is reached.

The application provides:
- Selection of alarm time using a TimePickerDialog
- Exact alarm scheduling
- Display of the selected alarm time
- Alarm cancellation
- Alarm sound playback using MediaPlayer
- Foreground service support for reliable background playback

### Main Components

| Component | Purpose |
|---|---|
| `MainActivity` | Handles the UI and alarm scheduling |
| `AlarmBroadcastReceiver` | Receives the scheduled alarm broadcast |
| `AlarmService` | Runs the alarm sound in the background |
| `AlarmManager` | Schedules the alarm |
| `PendingIntent` | Connects AlarmManager with the receiver |
| `MediaPlayer` | Plays the alarm audio |

---

## 📂 Project Structure

```
24012011013_MAD_PRACTICAL4/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/
│           │       └── a24012011013_mad_practical4/
│           │           ├── MainActivity.kt
│           │           ├── AlarmBroadcastReceiver.kt
│           │           └── AlarmService.kt
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   ├── raw/
│           │   │   └── alarm.mp3
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

# 🧩 Main Components

## 1. MainActivity

`MainActivity` controls the main alarm screen.

When the activity starts, it:
1. Loads the main layout.
2. Applies the system window insets.
3. Finds the alarm card and buttons.
4. Keeps the alarm card hidden until an alarm is created.
5. Opens the time picker when **Set Alarm** is pressed.
6. Cancels the scheduled alarm when **Cancel Alarm** is selected.

The alarm card is initially hidden using:

```kotlin
cardSetAlarm.visibility = View.GONE
```

After a valid alarm is scheduled, the selected time is displayed and the card becomes visible.

---

# 🕐 Selecting Alarm Time

The application uses `TimePickerDialog` for selecting the required hour and minute.

```kotlin
val picker = TimePickerDialog(
    this,
    { tp, sHour, sMinute ->
        sendDialogDataToActivity(sHour, sMinute)
    },
    hrs,
    mns,
    false
)

picker.show()
```

The current time is obtained from `Calendar` and is used as the initial value of the time picker.

---

# 📅 Creating the Alarm Time

After the user selects a time, a `Calendar` object is prepared with the current:

- Year
- Month
- Date

along with the selected:

- Hour
- Minute

Seconds are set to zero.

```kotlin
alarmCalendar.set(year, month, day, hour, minute, 0)
```

The final time is converted into milliseconds:

```kotlin
alarmCalendar.timeInMillis
```

This millisecond value is then passed to the alarm scheduling method.

---

# ⏱️ AlarmManager and PendingIntent

A broadcast `PendingIntent` is created for `AlarmBroadcastReceiver`.

```kotlin
val pendingIntent = PendingIntent.getBroadcast(
    applicationContext,
    23245,
    intent,
    PendingIntent.FLAG_IMMUTABLE
)
```

The Android `AlarmManager` is then obtained:

```kotlin
val alarmManager =
    getSystemService(ALARM_SERVICE) as AlarmManager
```

Before scheduling an exact alarm, the application checks whether exact alarm permission is available.

```kotlin
alarmManager.canScheduleExactAlarms()
```

The alarm is scheduled using:

```kotlin
alarmManager.setExact(
    AlarmManager.RTC_WAKEUP,
    millisTime,
    pendingIntent
)
```

---

# 🔐 Exact Alarm Permission

The project uses the following permission:

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

If exact alarm access is not available, the application displays a message and opens the Android settings page so the permission can be enabled.

---

# 📡 AlarmBroadcastReceiver

`AlarmBroadcastReceiver` extends Android's `BroadcastReceiver`.

It reads the service command from the intent using:

```kotlin
SERVICE_KEY = "Service1"
```

Two values are used:

```kotlin
START_VAL = "start"
STOP_VAL = "stop"
```

When the receiver receives **start**, it launches `AlarmService` as a foreground service.

When it receives **stop**, it stops the alarm service.

### Receiver Logic

```
        Alarm Broadcast
              │
              ▼
   AlarmBroadcastReceiver
          /       \
       start      stop
        │           │
        ▼           ▼
 AlarmService    Stop Service
```

---

# 🔊 AlarmService

`AlarmService` is responsible for playing the alarm audio.

The alarm file is stored in:

```
app/src/main/res/raw/alarm.mp3
```

The service uses `MediaPlayer` to load and play the sound.

The audio is configured with alarm usage attributes before playback so that it behaves as an alarm sound.

The service also runs as a **foreground service** while the alarm is playing. A notification channel is created for the running service.

Required manifest permissions include:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

The service is declared with:

```xml
android:foregroundServiceType="mediaPlayback"
```

The audio is released when the service is destroyed.

---

# 🔄 Complete Alarm Flow

```
┌─────────────────────┐
│     MainActivity    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│   TimePickerDialog  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│      Calendar       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     AlarmManager    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│    PendingIntent    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────┐
│ AlarmBroadcastReceiver  │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────┐
│    AlarmService     │
│  Foreground Service │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     MediaPlayer     │
│    Alarm Sound      │
└─────────────────────┘
```

Therefore, the main working sequence is:

**MainActivity → AlarmManager → PendingIntent → BroadcastReceiver → Foreground Service → MediaPlayer**

---

# ❌ Cancelling the Alarm

The **Cancel Alarm** button calls:

```kotlin
setAlarm(0, AlarmBroadcastReceiver.STOP_VAL)
```

The previously created PendingIntent is cancelled through:

```kotlin
alarmManager.cancel(pendingIntent)
```

The service is then stopped and the alarm card is hidden from the screen.

A Toast message is also displayed:

```
Alarm is stopped!
```

---

# 📚 Concepts Used

| Concept | Use in Practical |
|---|---|
| Activity | User interface and controls |
| BroadcastReceiver | Receives the alarm event |
| Foreground Service | Keeps alarm playback running |
| AlarmManager | Schedules the alarm |
| PendingIntent | Delivers the scheduled broadcast |
| TimePickerDialog | Selects alarm time |
| Calendar | Creates the selected date/time |
| MediaPlayer | Plays alarm.mp3 |
| MaterialCardView | Shows the selected alarm |
| MaterialButton | Set and Cancel buttons |
| Toast | Displays status messages |
| Edge-to-Edge | Handles modern screen layout |
| Exact Alarm | Uses `setExact()` |

---

# 🛠️ Development Steps

1. Create the Android Studio project.
2. Design the alarm screen.
3. Implement `MainActivity`.
4. Add Set Alarm and Cancel Alarm buttons.
5. Add `TimePickerDialog`.
6. Store the selected time using `Calendar`.
7. Create the `PendingIntent`.
8. Obtain the `AlarmManager`.
9. Check exact alarm permission.
10. Schedule the alarm using `setExact()`.
11. Create `AlarmBroadcastReceiver`.
12. Create the foreground `AlarmService`.
13. Add the alarm MP3 file.
14. Configure `MediaPlayer`.
15. Implement alarm cancellation.
16. Test setting, ringing and cancelling the alarm.

---

# ▶️ How to Run

1. Open the project in **Android Studio**.
2. Wait for Gradle synchronization to complete.
3. Connect an Android device or start an emulator.
4. Run the application.
5. Press **Set Alarm**.
6. Select the required time.
7. Allow exact alarm access if Android asks for permission.
8. Wait until the selected time.
9. Verify that the alarm sound starts.
10. Press **Cancel Alarm** to stop the alarm.

---

# 🖼️ Output Screenshots

The following screenshot shows the alarm application running on the Android device, including the alarm creation screen and a configured alarm card.

![Alarm Application Output](screenshots/practical4_output_1.webp)

*Figure 1: Alarm application with the Create Alarm screen and configured alarm.*

> **Note:** Additional output screenshots will be added here as they are uploaded.

---

# 📁 Important Files

```
MainActivity.kt
AlarmBroadcastReceiver.kt
AlarmService.kt
activity_main.xml
AndroidManifest.xml
res/raw/alarm.mp3
```

---

# ✅ Result

The Android Alarm application was successfully developed in Kotlin. It allows the user to select an alarm time, schedule an exact alarm using `AlarmManager`, receive the alarm through `BroadcastReceiver`, and play the alarm sound through a foreground `Service` using `MediaPlayer`.

---

## 👨‍💻 Student Details

**Student Name:** Yash Chaudhary  
**Enrollment No.:** 24012011013  
**Practical:** MAD Practical 4  
**Project:** Android Alarm Application
