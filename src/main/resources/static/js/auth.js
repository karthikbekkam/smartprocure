/**
 * SmartProcure Authentication Handler Script
 */
document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('loginForm');
  const registerForm = document.getElementById('registerForm');
  const forgotPasswordForm = document.getElementById('forgotPasswordForm');
  const resetPasswordForm = document.getElementById('resetPasswordForm');

  // Handle Login
  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const alertContainer = document.getElementById('authAlertContainer');
      alertContainer.innerHTML = '';

      const submitBtn = loginForm.querySelector('button[type="submit"]');
      const originalText = submitBtn.innerHTML;
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Authenticating...';

      const email = document.getElementById('username')?.value || document.getElementById('email')?.value;
      const password = document.getElementById('password')?.value;
      const rememberMe = document.getElementById('rememberMe')?.checked;

      try {
        const response = await SmartProcure.apiFetch('/api/v1/auth/login', {
          method: 'POST',
          body: JSON.stringify({ email, password, rememberMe })
        });

        if (response.token) {
          SmartProcure.setToken(response.token);
          if (response.user) {
            SmartProcure.setUser(response.user);
          }
          SmartProcure.showAlert('Login successful! Redirecting...', 'success');
          setTimeout(() => {
            window.location.href = response.redirectUrl || '/dashboard';
          }, 800);
        } else {
          // Standard form submit fallback if server renders views directly
          loginForm.submit();
        }
      } catch (err) {
        alertContainer.innerHTML = `
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${err.message || 'Invalid email or password.'}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>
        `;
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
      }
    });
  }

  // Handle Registration
  if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const alertContainer = document.getElementById('authAlertContainer');
      alertContainer.innerHTML = '';

      const password = document.getElementById('password').value;
      const confirmPassword = document.getElementById('confirmPassword').value;

      if (password !== confirmPassword) {
        alertContainer.innerHTML = `
          <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i> Passwords do not match.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>
        `;
        return;
      }

      const submitBtn = registerForm.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span> Creating Account...';

      const payload = {
        fullName: document.getElementById('fullName').value,
        email: document.getElementById('email').value,
        companyName: document.getElementById('companyName')?.value || '',
        role: document.getElementById('role').value,
        password: password
      };

      try {
        await SmartProcure.apiFetch('/api/v1/auth/register', {
          method: 'POST',
          body: JSON.stringify(payload)
        });

        SmartProcure.showAlert('Registration successful! Please sign in.', 'success');
        setTimeout(() => {
          window.location.href = '/login?registered=true';
        }, 1000);
      } catch (err) {
        alertContainer.innerHTML = `
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${err.message || 'Registration failed.'}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>
        `;
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Register Account';
      }
    });
  }

  // Handle Forgot Password
  if (forgotPasswordForm) {
    forgotPasswordForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const email = document.getElementById('email').value;
      const alertContainer = document.getElementById('authAlertContainer');
      
      try {
        await SmartProcure.apiFetch('/api/v1/auth/forgot-password', {
          method: 'POST',
          body: JSON.stringify({ email })
        });
        alertContainer.innerHTML = `
          <div class="alert alert-success fade show" role="alert">
            <i class="fas fa-paper-plane me-2"></i> Password reset link sent to your email address!
          </div>
        `;
      } catch (err) {
        alertContainer.innerHTML = `
          <div class="alert alert-danger fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${err.message || 'Could not process password reset.'}
          </div>
        `;
      }
    });
  }
});
