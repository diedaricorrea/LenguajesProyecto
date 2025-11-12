function toggleNotifications() {
    const modal = document.getElementById('notification-modal');
    modal.classList.remove('hidden');
    modal.classList.add('show');
}

function closeNotifications(event) {
    const modal = document.getElementById('notification-modal');
    modal.classList.remove('show');
    modal.classList.add('hidden');
}

// Cierra con Escape
document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        closeNotifications();
    }
});
