/**
 * SmartProcure Enterprise JavaScript Core Module
 */

const SmartProcure = {
    init: function() {
        this.setupThemeToggle();
        this.setupAuthInterceptor();
    },

    setupThemeToggle: function() {
        const toggleBtn = document.getElementById('themeToggleBtn');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                const currentTheme = document.documentElement.getAttribute('data-theme');
                const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
                document.documentElement.setAttribute('data-theme', newTheme);
                localStorage.setItem('theme', newTheme);
            });
        }
        
        const savedTheme = localStorage.getItem('theme') || 'light';
        document.documentElement.setAttribute('data-theme', savedTheme);
    },

    setupAuthInterceptor: function() {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            window.fetchWithAuth = function(url, options = {}) {
                options.headers = {
                    ...options.headers,
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                };
                return fetch(url, options);
            };
        } else {
            window.fetchWithAuth = fetch;
        }
    },

    showAlert: function(message, type = 'success') {
        const alertContainer = document.getElementById('alertContainer');
        if (alertContainer) {
            const alert = document.createElement('div');
            alert.className = `alert alert-${type} alert-dismissible fade show shadow-sm`;
            alert.role = 'alert';
            alert.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;
            alertContainer.appendChild(alert);
            setTimeout(() => alert.remove(), 5000);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => SmartProcure.init());
