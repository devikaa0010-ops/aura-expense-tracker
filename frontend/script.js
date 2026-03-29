const API_BASE = 'https://aura-expense-api.onrender.com/api';
let authToken = localStorage.getItem('aura_token');
let currentUser = null;

// DOM Elements
const toast = document.getElementById('toast');
const authView = document.getElementById('auth-view');
const dashboardView = document.getElementById('dashboard-view');

// Auth Tabs
const tabLogin = document.getElementById('tab-login');
const tabSignup = document.getElementById('tab-signup');
const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('signup-form');

// Initialization
document.addEventListener('DOMContentLoaded', () => {
    if (authToken) {
        showDashboard();
    } else {
        showAuth();
    }
});

// UI Helpers
function showToast(message, type = 'success') {
    toast.textContent = message;
    toast.style.background = type === 'error' ? '#e11d48' : '#16a34a';
    toast.classList.remove('hidden');
    setTimeout(() => {
        toast.classList.add('hidden');
    }, 3000);
}

function showAuth() {
    authView.classList.remove('hidden');
    dashboardView.classList.add('hidden');
}

function showDashboard() {
    authView.classList.add('hidden');
    dashboardView.classList.remove('hidden');
    loadDashboardData();
}

// Tab Switching
tabLogin.addEventListener('click', () => {
    tabLogin.classList.add('active');
    tabSignup.classList.remove('active');
    loginForm.classList.remove('hidden');
    signupForm.classList.add('hidden');
});

tabSignup.addEventListener('click', () => {
    tabSignup.classList.add('active');
    tabLogin.classList.remove('active');
    signupForm.classList.remove('hidden');
    loginForm.classList.add('hidden');
});

// Auth Handlers
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();
        
        if (res.ok) {
            authToken = data.token;
            currentUser = data.username;
            localStorage.setItem('aura_token', authToken);
            document.getElementById('greeting').textContent = `Hello, ${currentUser}`;
            showToast('Welcome back!');
            showDashboard();
        } else {
            showToast(data.message || 'Login failed', 'error');
        }
    } catch (err) {
        showToast('Server connection error', 'error');
    }
});

signupForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('signup-username').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        
        if (res.ok) {
            showToast('Account created! Please login.');
            tabLogin.click();
        } else {
            const err = await res.text();
            showToast(err, 'error');
        }
    } catch (err) {
        showToast('Server connection error', 'error');
    }
});

document.getElementById('logout-btn').addEventListener('click', () => {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('aura_token');
    showToast('Logged out successfully');
    showAuth();
});

// Dashboard Data Loading
async function loadDashboardData() {
    try {
        // Fetch Transactions
        const txRes = await fetch(`${API_BASE}/transactions`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        
        if (!txRes.ok) {
            if(txRes.status === 401) return forceLogout();
            throw new Error('Failed to load transactions');
        }
        
        const transactions = await txRes.json();
        renderTransactions(transactions);

        // Fetch Current Month Summary
        const date = new Date();
        const currentMonth = date.getMonth() + 1;
        const currentYear = date.getFullYear();
        
        const sumRes = await fetch(`${API_BASE}/summary/${currentYear}/${currentMonth}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        let summaryData = null;
        if (sumRes.ok) {
            summaryData = await sumRes.json();
            document.getElementById('total-balance').textContent = `$${summaryData.balance.toFixed(2)}`;
            document.getElementById('total-income').textContent = `+$${summaryData.totalIncome.toFixed(2)}`;
            document.getElementById('total-expense').textContent = `-$${summaryData.totalExpense.toFixed(2)}`;
        }

        // Fetch Budgets
        const budRes = await fetch(`${API_BASE}/budgets/${currentYear}/${currentMonth}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (budRes.ok) {
            const budgets = await budRes.json();
            renderBudgets(budgets, summaryData);
        }

    } catch (err) {
        console.error(err);
        showToast('Error loading data', 'error');
    }
}

function forceLogout() {
    localStorage.removeItem('aura_token');
    showAuth();
}

function renderTransactions(transactions) {
    const list = document.getElementById('transactions-list');
    list.innerHTML = '';
    
    if (transactions.length === 0) {
        list.innerHTML = `<div class="empty-state">No transactions yet. Start adding!</div>`;
        return;
    }

    transactions.reverse().forEach(t => {
        const isIncome = t.type === 'INCOME';
        const sign = isIncome ? '+' : '-';
        const cssClass = isIncome ? 'income' : 'expense';
        const icon = isIncome ? 'fa-arrow-trend-up' : 'fa-basket-shopping';

        const item = document.createElement('div');
        item.className = 'transaction-item';
        item.innerHTML = `
            <div class="t-details">
                <div class="t-icon ${cssClass}"><i class="fa-solid ${icon}"></i></div>
                <div class="t-info">
                    <h4>${t.category}</h4>
                    <p>${t.description || t.paymentMethod || ''} • ${t.date}</p>
                </div>
            </div>
            <div class="t-amount-date">
                <h4 class="${cssClass}">${sign}$${t.amount.toFixed(2)}</h4>
            </div>
        `;
        list.appendChild(item);
    });
}

// Modal Handling
const modal = document.getElementById('transaction-modal');
document.getElementById('open-transaction-modal').addEventListener('click', () => modal.classList.remove('hidden'));
document.getElementById('close-modal').addEventListener('click', () => modal.classList.add('hidden'));

const budgetModal = document.getElementById('budget-modal');
document.getElementById('open-budget-modal').addEventListener('click', () => {
    const today = new Date();
    document.getElementById('b-month').value = today.getMonth() + 1;
    document.getElementById('b-year').value = today.getFullYear();
    budgetModal.classList.remove('hidden');
});
document.getElementById('close-budget-modal').addEventListener('click', () => budgetModal.classList.add('hidden'));

// Transaction Form Handling
document.getElementById('transaction-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const type = document.getElementById('t-type').value;
    const amount = parseFloat(document.getElementById('t-amount').value);
    const category = document.getElementById('t-category').value;
    const date = document.getElementById('t-date').value;
    const description = document.getElementById('t-description').value;
    const paymentMethod = document.getElementById('t-payment').value;
    const location = document.getElementById('t-location').value;
    const tags = document.getElementById('t-tags').value;

    let receiptUrl = null;
    const fileInput = document.getElementById('t-receipt');
    
    // Upload File First if exists
    if (fileInput.files.length > 0) {
        const formData = new FormData();
        formData.append('file', fileInput.files[0]);
        try {
            const upRes = await fetch(`${API_BASE}/upload`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${authToken}` },
                body: formData
            });
            if(upRes.ok) {
                const upData = await upRes.json();
                receiptUrl = upData.fileDownloadUri;
            }
        } catch(e) { console.error('Upload failed', e); }
    }

    const payload = { type, amount, category, date, description, paymentMethod, location, tags, receiptUrl };

    try {
        const res = await fetch(`${API_BASE}/transactions`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            showToast('Transaction added!');
            modal.classList.add('hidden');
            document.getElementById('transaction-form').reset();
            loadDashboardData();
        } else {
            showToast('Failed to save transaction', 'error');
        }
    } catch (err) {
        showToast('Server error', 'error');
    }
});

// Budget Form Handling
document.getElementById('budget-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const category = document.getElementById('b-category').value;
    const limitAmount = parseFloat(document.getElementById('b-amount').value);
    const month = parseInt(document.getElementById('b-month').value);
    const year = parseInt(document.getElementById('b-year').value);

    try {
        const res = await fetch(`${API_BASE}/budgets`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ category, limitAmount, month, year })
        });

        if (res.ok) {
            showToast(`Budget for ${category} saved!`);
            budgetModal.classList.add('hidden');
            document.getElementById('budget-form').reset();
            loadDashboardData();
        } else {
            showToast('Failed to save budget', 'error');
        }
    } catch (err) {
        showToast('Server error', 'error');
    }
});

function renderBudgets(budgets, summary) {
    const list = document.getElementById('budget-list');
    list.innerHTML = '';
    
    if (budgets.length === 0) {
        list.innerHTML = `<div class="empty-state">No budgets set. Click the + icon to track spending limits.</div>`;
        return;
    }

    budgets.forEach(b => {
        let spent = 0;
        // Check actual spending against the budget category
        if (summary && summary.expenseByCategory && summary.expenseByCategory[b.category]) {
            spent = summary.expenseByCategory[b.category];
        }
        
        let percentage = (spent / b.limitAmount) * 100;
        if (percentage > 100) percentage = 100;

        let color = 'var(--primary-color)';
        if (percentage > 85) color = '#e11d48'; // Red if nearing or over limit
        else if (percentage > 50) color = '#f59e0b'; // Orange if half way

        const item = document.createElement('div');
        item.style.marginBottom = '15px';
        item.innerHTML = `
            <div style="display: flex; justify-content: space-between; font-size: 0.9rem; margin-bottom: 5px;">
                <strong>${b.category}</strong>
                <span style="color: var(--text-muted);">$${spent.toFixed(2)} / $${b.limitAmount.toFixed(2)}</span>
            </div>
            <div style="width: 100%; height: 8px; background: rgba(0,0,0,0.05); border-radius: 4px; overflow: hidden;">
                <div style="width: ${percentage}%; height: 100%; background: ${color}; transition: width 0.3s ease;"></div>
            </div>
        `;
        list.appendChild(item);
    });
}
