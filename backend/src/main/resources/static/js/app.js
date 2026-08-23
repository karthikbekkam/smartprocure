// SmartProcure API Client & Central UI Controller

const SmartProcure = {
    apiBase: '/smartprocure/api/v1',

    getToken() {
        return localStorage.getItem('smartprocure_token');
    },

    setToken(token) {
        localStorage.setItem('smartprocure_token', token);
    },

    clearToken() {
        localStorage.removeItem('smartprocure_token');
    },

    async fetch(url, options = {}) {
        const token = this.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
            ...options.headers
        };

        const response = await fetch(`${this.apiBase}${url}`, {
            ...options,
            headers
        });

        if (response.status === 401) {
            this.clearToken();
            window.location.href = '/smartprocure/login';
            return;
        }

        return response.json();
    },

    async login(email, password) {
        const res = await this.fetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });

        if (res && res.success && res.data.accessToken) {
            this.setToken(res.data.accessToken);
            window.location.href = '/smartprocure/dashboard';
        } else {
            alert(res.message || 'Authentication failed');
        }
    },

    logout() {
        this.clearToken();
        window.location.href = '/smartprocure/login';
    }
};

document.addEventListener('DOMContentLoaded', () => {
    console.log('SmartProcure Enterprise System initialized.');
});
