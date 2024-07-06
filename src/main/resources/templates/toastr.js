function toast({ title, message, type, duration }) {
    const toastContainer = document.getElementById('toast');
    const toast = document.createElement('div');
    toast.classList.add('toast', `toast--${type}`);
    toast.innerHTML = `
                <div class="toast__title">${title}</div>
                <div class="toast__message">${message}</div>
            `;
    toastContainer.appendChild(toast);
    setTimeout(() => {
        toast.remove();
    }, duration);
}