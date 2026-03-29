# Aura Expense Tracker: Complete Study Guide

Welcome to the **Aura Expense Tracker** project! Since you mentioned you are new to this and want to understand how everything works, this guide is written specifically for you. It explains what we built, how it works, and why we made certain choices.

---

## 1. What Did We Build?
We built a **Full-Stack Application**. "Full-Stack" means we built both the invisible engine that processes data (the Backend) and the user interface that you click and interact with (the Frontend).

*   **The Goal:** A secure, multi-user web app where you and your brother can create separate accounts, log in, track your expenses/income, set monthly budgets, and see a summary of your financial health.

## 2. The Architecture (How It Is Split)
Modern apps separate concerns into two major pieces:

### A. The Backend (The Brains)
*   **Technology:** Java with the **Spring Boot** framework.
*   **Location:** The `expense-tracker-api/` folder.
*   **Role:** It receives requests from the frontend, checks if the user is authorized, does the math, interacts with the database, and sends data back.
*   **Database:** We used **H2 Database**. It's an "in-memory" database, meaning it lives entirely in your computer's RAM while the app is running. It's incredibly fast and perfect for development out-of-the-box.

### B. The Frontend (The Face)
*   **Technology:** HTML, CSS, and Vanilla JavaScript.
*   **Location:** The `frontend/` folder.
*   **Role:** It provides the visually appealing "Classy Pastel" interface. It doesn't do complex math itself; instead, it asks the backend for data using "API Calls" (via JavaScript's `fetch()` command) and displays the answers on your screen.

---

## 3. Step-by-Step Breakdown (What We Did)

### Phase 1: User Authentication & Security
*Why we did it:* We needed a way to separate your expenses from your brother's.
*   **Passwords:** We never save passwords as plain text. We used **BCrypt** to securely scramble (hash) them before saving them to the database.
*   **JWT (JSON Web Tokens):** When you log in successfully, the backend hands you a secret "Token" (like a digital VIP pass). Every time your frontend wants to look at an expense, it shows this Token to the backend to prove it's really you. 
*   **Code:** This logic lives in the `com.example.expense_tracker_api.security` package.

### Phase 2: Transactions List
*Why we did it:* The core of an expense app is tracking money taking place.
*   **Data Structure:** We defined a `Transaction.java` Entity (a Java class that maps directly to a table in the database). It holds fields like `amount`, `date`, `category`, and `description`.
*   **API Endpoints:** We built a `TransactionController.java` the frontend can talk to:
    *   `POST /api/transactions` -> Adds a new expense.
    *   `GET /api/transactions` -> Returns a list of all *your* expenses (it checks your Token to make sure it doesn't return your brother's).

### Phase 3: Advanced Features (Budgets & Summaries)
*Why we did it:* A premium app needs premium features.
*   **Summaries:** Instead of making the frontend calculate the total math, we created a `/api/summary` endpoint. The backend looks at the current month, adds everything up, and returns a neat `MonthlySummary` package stating your Total Balance, Total Income, and Total Expenses.
*   **Budgets:** We created a `Budget.java` entity allowing you to restrict the amount of money you want to spend in a specific category (e.g., "Food", "Bills").
*   **Receipts:** We added an endpoint `FileUploadController.java` to handle uploading images. It saves the file to a local `uploads/` folder and returns a link to the file.

### Phase 4: The Frontend Dashboard
*Why we did it:* Code doesn't look impressive to a user—graphics and UI do.
*   **HTML:** We built a single `index.html` file containing hidden "views". A clever trick we did was hiding the Auth/Login view and showing the Dashboard view instantly without refreshing the page!
*   **CSS:** We designed the app utilizing modern techniques: **Glassmorphism** (semi-transparent blurred backgrounds) and soft box shadows. We stored the colors inside `--variables` at the top of the CSS file so the theme is perfectly consistent.
*   **JavaScript:** The `script.js` acts as the bridge. It intercepts your form clicks, bundles the data into JSON, sends the API request to `http://localhost:8080/api/auth` or `/api/transactions`, waits for the response, and then updates the HTML dynamically based on the backend's response!

---

## 4. How to Study This Project
If you want to read through the most important files and understand the flow, I recommend looking at them in this exact order:

1. **The Database Model:** `expense-tracker-api/src/main/java/.../entity/Transaction.java`
   *(See how Java represents a table of data).*
2. **The Security Backbone:** `.../security/WebSecurityConfig.java` 
   *(See how we tell Spring Boot to block unauthenticated requests except for `/api/auth/**`).*
3. **The API Controller:** `.../controller/TransactionController.java` 
   *(See how the backend actually receives a request and saves data).*
4. **The Frontend Visuals:** `frontend/index.html` and `styles.css` 
   *(See how we structured the layout and made it pretty).*
5. **The Frontend Logic:** `frontend/script.js` 
   *(Scroll to where we make a `fetch()` request—this is how the Frontend talks to the Backend).*

> **Tip!** At any time, if you encounter a block of code in these files that confuses you, simply copy-paste it to me, and I will break it down line-by-line for you. Happy studying!
