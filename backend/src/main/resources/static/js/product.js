// Product Catalog Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});

async function loadProducts() {
    const res = await SmartProcure.fetch('/products');
    const tbody = document.getElementById('productTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No products found in catalog.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(p => `
            <tr>
                <td><code>${p.sku}</code></td>
                <td><span class="fw-bold text-dark">${p.name}</span></td>
                <td><span class="badge bg-light text-dark border">${p.categoryName || 'Category #' + p.categoryId}</span></td>
                <td><span class="fw-semibold text-primary">$${Number(p.unitPrice).toFixed(2)}</span></td>
                <td><span class="badge bg-warning-subtle text-warning border border-warning-subtle">${p.reorderLevel} units</span></td>
                <td><span class="badge-status badge-approved">ACTIVE</span></td>
            </tr>
        `).join('');
    }
}

async function submitProductForm() {
    const payload = {
        sku: document.getElementById('prodSku').value,
        name: document.getElementById('prodName').value,
        categoryId: Number(document.getElementById('prodCategoryId').value),
        unitPrice: parseFloat(document.getElementById('prodUnitPrice').value),
        reorderLevel: parseInt(document.getElementById('prodReorderLevel').value),
        description: document.getElementById('prodDescription').value
    };

    const res = await SmartProcure.fetch('/products', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('newProductModal'));
        modal.hide();
        document.getElementById('productForm').reset();
        loadProducts();
    } else {
        alert(res ? res.message : 'Error saving product');
    }
}
