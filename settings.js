// ====================================
// INITIALIZATION
// ====================================
document.addEventListener('DOMContentLoaded', function() {
    initializeSettings();
});

function initializeSettings() {
    // Navigation
    initializeSidebar();
    
    // Profile Settings
    initializeProfileImage();
    initializePasswordToggle();
    initializeSaveButton();
    
    // Appearance Settings
    initializeThemeToggle();
    initializeAccentColors();
    
    // Video Player Settings
    initializeSliders();
    
    // Interactive Elements
    initializeToggles();
    initializeActionButtons();
}

// ====================================
// SIDEBAR NAVIGATION
// ====================================
function initializeSidebar() {
    const navItems = document.querySelectorAll('.nav-item');
    const sections = document.querySelectorAll('.settings-section');
    
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            // Remove active class from all nav items
            navItems.forEach(nav => nav.classList.remove('active'));
            
            // Add active class to clicked item
            this.classList.add('active');
            
            // Hide all sections
            sections.forEach(section => section.classList.remove('active'));
            
            // Show corresponding section
            const sectionId = this.getAttribute('data-section') + '-section';
            const targetSection = document.getElementById(sectionId);
            if (targetSection) {
                targetSection.classList.add('active');
            }
            
            // Smooth scroll to top on mobile
            if (window.innerWidth <= 768) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    });
}

// ====================================
// PROFILE IMAGE UPLOAD & PREVIEW
// ====================================
function initializeProfileImage() {
    const fileInput = document.getElementById('profileImageInput');
    const preview = document.getElementById('profilePreview');
    const wrapper = document.querySelector('.profile-image-wrapper');
    
    // Click on wrapper to trigger file input
    wrapper.addEventListener('click', function() {
        fileInput.click();
    });
    
    // Handle file selection
    fileInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (file) {
            // Validate file type
            if (!file.type.startsWith('image/')) {
                showToast('Please select a valid image file', 'error');
                return;
            }
            
            // Validate file size (max 5MB)
            if (file.size > 5 * 1024 * 1024) {
                showToast('Image size should be less than 5MB', 'error');
                return;
            }
            
            // Create file reader for preview
            const reader = new FileReader();
            
            reader.onload = function(e) {
                preview.src = e.target.result;
                // Add animation effect
                preview.style.opacity = '0';
                setTimeout(() => {
                    preview.style.transition = 'opacity 0.3s ease';
                    preview.style.opacity = '1';
                }, 50);
            };
            
            reader.readAsDataURL(file);
        }
    });
}

// ====================================
// PASSWORD TOGGLE (SHOW/HIDE)
// ====================================
function initializePasswordToggle() {
    const passwordInput = document.getElementById('password');
    const toggleButton = document.getElementById('passwordToggle');
    
    toggleButton.addEventListener('click', function() {
        const type = passwordInput.type === 'password' ? 'text' : 'password';
        passwordInput.type = type;
        
        // Toggle icon
        const icon = this.querySelector('i');
        if (type === 'text') {
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    });
}

// ====================================
// SAVE BUTTON FUNCTIONALITY
// ====================================
function initializeSaveButton() {
    const saveButtons = document.querySelectorAll('.save-btn, .btn-primary');
    
    saveButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Add loading animation
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
            this.disabled = true;
            
            // Simulate save operation
            setTimeout(() => {
                this.innerHTML = originalText;
                this.disabled = false;
                showToast('Settings saved successfully!');
                
                // Log saved data (mock)
                const formData = gatherFormData();
                console.log('Settings saved:', formData);
            }, 1500);
        });
    });
}

// Gather all form data
function gatherFormData() {
    return {
        username: document.getElementById('username')?.value,
        email: document.getElementById('email')?.value,
        bio: document.getElementById('bio')?.value,
        theme: document.getElementById('themeToggle')?.checked ? 'dark' : 'light',
        accentColor: getComputedStyle(document.documentElement).getPropertyValue('--accent-primary').trim(),
        videoQuality: document.getElementById('videoQuality')?.value,
        subtitleLang: document.getElementById('subtitleLang')?.value,
        subtitleSize: document.getElementById('subtitleSize')?.value,
        autoplay: document.getElementById('autoplayToggle')?.checked,
        twoFactor: document.getElementById('twoFactorToggle')?.checked,
        notifications: {
            episodes: document.querySelector('.notification-toggle:nth-of-type(1)')?.checked,
            bookmarks: document.querySelector('.notification-toggle:nth-of-type(2)')?.checked,
            releases: document.querySelector('.notification-toggle:nth-of-type(3)')?.checked,
            comments: document.querySelector('.notification-toggle:nth-of-type(4)')?.checked
        }
    };
}

// ====================================
// THEME TOGGLE (LIGHT/DARK MODE)
// ====================================
function initializeThemeToggle() {
    const themeToggle = document.getElementById('themeToggle');
    
    // Load saved theme preference
    const savedTheme = localStorage.getItem('theme') || 'dark';
    applyTheme(savedTheme);
    themeToggle.checked = savedTheme === 'dark';
    
    themeToggle.addEventListener('change', function() {
        const theme = this.checked ? 'dark' : 'light';
        applyTheme(theme);
        localStorage.setItem('theme', theme);
        
        // Show feedback
        showToast(`${theme.charAt(0).toUpperCase() + theme.slice(1)} mode activated`);
    });
}

function applyTheme(theme) {
    if (theme === 'light') {
        document.body.classList.add('light-theme');
    } else {
        document.body.classList.remove('light-theme');
    }
}

// ====================================
// ACCENT COLOR PICKER
// ====================================
function initializeAccentColors() {
    const colorButtons = document.querySelectorAll('.accent-color');
    
    // Load saved accent color
    const savedColor = localStorage.getItem('accentColor') || '#00d9ff';
    applyAccentColor(savedColor);
    
    colorButtons.forEach(button => {
        const color = button.getAttribute('data-color');
        
        // Set active state based on saved color
        if (color === savedColor) {
            button.classList.add('active');
        }
        
        button.addEventListener('click', function() {
            // Remove active class from all buttons
            colorButtons.forEach(btn => btn.classList.remove('active'));
            
            // Add active class to clicked button
            this.classList.add('active');
            
            // Get color and apply it
            const selectedColor = this.getAttribute('data-color');
            applyAccentColor(selectedColor);
            localStorage.setItem('accentColor', selectedColor);
            
            // Show feedback
            showToast('Accent color updated');
        });
    });
}

function applyAccentColor(color) {
    // Update CSS variable
    document.documentElement.style.setProperty('--accent-primary', color);
    
    // Calculate hover color (slightly darker)
    const hoverColor = adjustColorBrightness(color, -20);
    document.documentElement.style.setProperty('--accent-hover', hoverColor);
    
    // Calculate glow color (with transparency)
    const glowColor = hexToRgba(color, 0.3);
    document.documentElement.style.setProperty('--accent-glow', glowColor);
}

// Helper function to adjust color brightness
function adjustColorBrightness(color, percent) {
    const num = parseInt(color.replace('#', ''), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
        (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
        (B < 255 ? B < 1 ? 0 : B : 255))
        .toString(16).slice(1);
}

// Helper function to convert hex to rgba
function hexToRgba(hex, alpha) {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

// ====================================
// SLIDER CONTROLS
// ====================================
function initializeSliders() {
    const subtitleSizeSlider = document.getElementById('subtitleSize');
    const subtitleSizeValue = document.getElementById('subtitleSizeValue');
    
    if (subtitleSizeSlider && subtitleSizeValue) {
        // Update value display
        subtitleSizeSlider.addEventListener('input', function() {
            subtitleSizeValue.textContent = this.value + 'px';
        });
        
        // Save on change
        subtitleSizeSlider.addEventListener('change', function() {
            localStorage.setItem('subtitleSize', this.value);
            showToast('Subtitle size updated');
        });
        
        // Load saved value
        const savedSize = localStorage.getItem('subtitleSize');
        if (savedSize) {
            subtitleSizeSlider.value = savedSize;
            subtitleSizeValue.textContent = savedSize + 'px';
        }
    }
}

// ====================================
// TOGGLE SWITCHES
// ====================================
function initializeToggles() {
    const toggles = document.querySelectorAll('input[type="checkbox"]');
    
    toggles.forEach(toggle => {
        // Load saved state if exists
        const toggleId = toggle.id;
        if (toggleId) {
            const savedState = localStorage.getItem(toggleId);
            if (savedState !== null) {
                toggle.checked = savedState === 'true';
            }
        }
        
        // Save on change
        toggle.addEventListener('change', function() {
            if (this.id) {
                localStorage.setItem(this.id, this.checked);
            }
            
            // Special handling for specific toggles
            if (this.id === 'twoFactorToggle') {
                if (this.checked) {
                    showToast('Two-factor authentication enabled', 'success');
                } else {
                    showToast('Two-factor authentication disabled', 'warning');
                }
            }
        });
    });
}

// ====================================
// ACTION BUTTONS
// ====================================
function initializeActionButtons() {
    // Clear History Button
    const clearHistoryBtn = document.getElementById('clearHistoryBtn');
    if (clearHistoryBtn) {
        clearHistoryBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to clear your watch history? This action cannot be undone.')) {
                // Simulate clearing history
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Clearing...';
                setTimeout(() => {
                    this.innerHTML = '<i class="fas fa-trash"></i> Clear History';
                    showToast('Watch history cleared successfully');
                }, 1000);
            }
        });
    }
    
    // Logout All Devices Button
    const logoutAllBtn = document.getElementById('logoutAllBtn');
    if (logoutAllBtn) {
        logoutAllBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to log out from all devices except this one?')) {
                // Simulate logout
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Logging out...';
                setTimeout(() => {
                    this.innerHTML = '<i class="fas fa-power-off"></i> Log Out All';
                    showToast('Logged out from all other devices');
                }, 1000);
            }
        });
    }
    
    // Delete Account Button
    const deleteAccountBtn = document.getElementById('deleteAccountBtn');
    if (deleteAccountBtn) {
        deleteAccountBtn.addEventListener('click', function() {
            const confirmation = prompt('This action is irreversible. Type "DELETE" to confirm:');
            if (confirmation === 'DELETE') {
                // Simulate account deletion
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Deleting...';
                setTimeout(() => {
                    alert('Account deletion initiated. You will receive a confirmation email.');
                    this.innerHTML = '<i class="fas fa-user-times"></i> Delete Account';
                }, 1000);
            } else if (confirmation !== null) {
                showToast('Account deletion cancelled', 'warning');
            }
        });
    }
}

// ====================================
// TOAST NOTIFICATION
// ====================================
function showToast(message, type = 'success') {
    const toast = document.getElementById('successToast');
    const icon = toast.querySelector('i');
    const text = toast.querySelector('span');
    
    // Update content
    text.textContent = message;
    
    // Update icon and color based on type
    if (type === 'success') {
        icon.className = 'fas fa-check-circle';
        toast.style.background = 'var(--success)';
    } else if (type === 'error') {
        icon.className = 'fas fa-times-circle';
        toast.style.background = 'var(--danger)';
    } else if (type === 'warning') {
        icon.className = 'fas fa-exclamation-triangle';
        toast.style.background = 'var(--warning)';
    }
    
    // Show toast
    toast.classList.add('show');
    
    // Hide after 3 seconds
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// ====================================
// SELECT DROPDOWN CHANGES
// ====================================
document.getElementById('videoQuality')?.addEventListener('change', function() {
    localStorage.setItem('videoQuality', this.value);
    showToast(`Video quality set to ${this.value}`);
});

document.getElementById('subtitleLang')?.addEventListener('change', function() {
    localStorage.setItem('subtitleLang', this.value);
    const langNames = {
        'en': 'English',
        'jp': 'Japanese',
        'es': 'Spanish',
        'fr': 'French',
        'de': 'German'
    };
    showToast(`Subtitle language set to ${langNames[this.value]}`);
});

// ====================================
// INPUT VALIDATION
// ====================================
document.getElementById('email')?.addEventListener('blur', function() {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (this.value && !emailRegex.test(this.value)) {
        this.style.borderColor = 'var(--danger)';
        showToast('Please enter a valid email address', 'error');
    } else {
        this.style.borderColor = 'var(--border-color)';
    }
});

document.getElementById('username')?.addEventListener('input', function() {
    if (this.value.length < 3) {
        this.style.borderColor = 'var(--warning)';
    } else {
        this.style.borderColor = 'var(--border-color)';
    }
});

// ====================================
// KEYBOARD SHORTCUTS
// ====================================
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + S to save
    if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        const activeSection = document.querySelector('.settings-section.active');
        const saveBtn = activeSection?.querySelector('.save-btn, .btn-primary');
        if (saveBtn) {
            saveBtn.click();
        }
    }
});

// ====================================
// SMOOTH ANIMATIONS ON LOAD
// ====================================
window.addEventListener('load', function() {
    document.body.style.opacity = '0';
    setTimeout(() => {
        document.body.style.transition = 'opacity 0.5s ease';
        document.body.style.opacity = '1';
    }, 100);
});

// ====================================
// UTILITY FUNCTIONS
// ====================================

// Debounce function for performance
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Log settings changes for debugging
if (console && console.log) {
    console.log('%c⚙️ Settings Page Loaded', 'color: #00d9ff; font-size: 16px; font-weight: bold;');
    console.log('%cAll interactive features initialized', 'color: #22c55e;');
}
