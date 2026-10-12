// StudySphere Web Application JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Form validation
    document.querySelectorAll('form[data-validate]').forEach(function(form) {
        form.addEventListener('submit', function(e) {
            var inputs = form.querySelectorAll('[required]');
            var valid = true;
            inputs.forEach(function(input) {
                if (!input.value.trim()) {
                    input.style.borderColor = '#ef4444';
                    valid = false;
                } else {
                    input.style.borderColor = '';
                }
            });
            if (!valid) { e.preventDefault(); alert('Please fill in all required fields.'); }
        });
    });

    // Auto-dismiss alerts after 5 seconds
    document.querySelectorAll('.alert').forEach(function(alert) {
        setTimeout(function() { alert.style.opacity = '0'; setTimeout(function() { alert.remove(); }, 300); }, 5000);
    });
});
