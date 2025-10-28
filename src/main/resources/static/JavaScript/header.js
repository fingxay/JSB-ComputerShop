/* Header component JavaScript */

function toggleUserMenu() {
    const dropdown = document.getElementById('userMenuDropdown');
    dropdown.classList.toggle('show');
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.getElementById('userMenuDropdown');
    if (userMenu && !userMenu.contains(event.target)) {
        dropdown?.classList.remove('show');
    }
});