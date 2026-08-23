// Category Management Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
});

async function loadCategories() {
    const res = await SmartProcure.fetch('/categories');
    const tbody = document.getElementById('categoryTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-muted">No category records found.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(cat => `
            <tr>
                <td><span class="fw-semibold">#CAT-${cat.id}</span></td>
                <td><code>${cat.code}</code></td>
                <td><span class="fw-bold text-dark">${cat.name}</span></td>
                <td>${cat.description || 'N/A'}</td>
                <td><span class="badge bg-success-subtle text-success border border-success-subtle">ACTIVE</span></td>
            </tr>
        `).join('');
    }
}

async function submitCategoryForm() {
    const payload = {
        code: document.getElementById('catCode').value,
        name: document.getElementById('catName').value,
        description: document.getElementById('catDescription').value
    };

    const res = await SmartProcure.fetch('/categories', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('newCategoryModal'));
        modal.hide();
        document.getElementById('categoryForm').reset();
        loadCategories();
    } else {
        alert(res ? res.message : 'Error creating category');
    }
}
