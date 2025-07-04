# PlantBuddy 🌿 - Your Personal Plant Care Dashboard

---

**PlantBuddy** is a user-friendly desktop application built with JavaFX that simplifies plant care by helping you track, schedule, and manage your beloved green companions. Forget missed waterings or forgotten fertilizers – PlantBuddy keeps you on top of your plant care routine with intuitive visuals and timely reminders!

---

## ✨ Features

* **Effortless Plant Management:** Easily add new plants with their unique details and even upload a photo to personalize your dashboard.
* **Custom Care Schedules:** Set precise watering and fertilizing days for each plant, tailoring the care to their specific needs.
* **Visual Progress Tracking:** A dynamic **progress bar** clearly shows you the time remaining until your next watering day, making it easy to see what's due at a glance.
* **One-Click Task Completion:** After you've watered or fertilized, simply click the "Watered" or "Fertilized" button to reset the progress bar and update the schedule.
* **Intuitive User Interface:** A clean and responsive design powered by JavaFX and CSS ensures a smooth and enjoyable user experience.

---

## 💻 Tech Stack

* **Frontend:**
    * ![JavaFX](https://img.shields.io/badge/JavaFX-Green?style=for-the-badge&logo=openjdk&logoColor=white) - For building a rich and interactive desktop user interface.
    * ![CSS](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) - For styling the application, ensuring a modern and appealing look.
* **Backend & Database:**
    * ![JDBC](https://img.shields.io/badge/JDBC-blue?style=for-the-badge&logo=java&logoColor=white) - Used for connecting the Java application to the database.
    * ![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white) - A robust relational database to store all your plant details, schedules, and care history.

---

## 🚀 Getting Started

Follow these steps to get PlantBuddy up and running on your local machine.

### Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK) 11 or higher**
* **MySQL Database Server**
* **Maven** (Highly Recommended for easy dependency management)

### Installation & Setup

1.  **Clone the Repository:**

    ```bash
    git clone [https://github.com/your-username/PlantBuddy.git](https://github.com/your-username/PlantBuddy.git)
    cd PlantBuddy
    ```

    *(Remember to replace `your-username` with the actual GitHub username where your project is hosted.)*

2.  **Database Configuration:**

    * Create a new MySQL database for PlantBuddy (e.g., `plantbuddy_db`).
    * Import the database schema by running the SQL script located at `src/main/resources/database/schema.sql` (or similar path in your project structure) into your new MySQL database.
    * Update the database connection properties in `src/main/resources/application.properties` (or your equivalent configuration file) with your MySQL credentials (username, password, database name).

3.  **Build and Run:**

    * **Using Maven (Recommended):**
        ```bash
        mvn clean install
        mvn javafx:run
        ```
    * **Without Maven:**
        You'll need to manually compile your JavaFX application and include all necessary dependencies (JavaFX modules, MySQL JDBC driver). This method is more complex and depends on your specific IDE setup.

---

## 💡 How to Use

1.  **Launch PlantBuddy:** Run the application after following the installation steps.
2.  **Add Your Plants:** Click on the "Add Plant" button. Fill in details like the plant's name, species, and upload a beautiful photo! Set your preferred watering and fertilizing days.
3.  **Monitor Your Dashboard:** Your plants will appear on the main dashboard, each with a visual progress bar indicating the next watering date.
4.  **Update Care Status:** Once you've watered or fertilized a plant, simply click the corresponding "Watered" or "Fertilized" button next to its entry. The progress bar will reset, and the next care day will be calculated!

---

## 🤝 Contributing

We welcome contributions to make PlantBuddy even better! If you'd like to contribute, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature (`git checkout -b feature/your-feature-name`).
3.  Make your changes and ensure tests (if any) pass.
4.  Commit your changes (`git commit -m 'feat: Add new awesome feature'`).
5.  Push to the branch (`git push origin feature/your-feature-name`).
6.  Open a Pull Request with a clear description of your changes.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

## ✉️ Contact

Have questions, suggestions, or just want to connect?
Feel free to reach out to me!

* **Email:** [bhargava.anandakumar@gmail.com](mailto:bhargava.anandakumar@gmail.com)
* **LinkedIn:** [Bhargava A](https://www.linkedin.com/in/bhargava-a-a1426b325/)

---
