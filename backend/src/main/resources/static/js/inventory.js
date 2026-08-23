// Inventory Management Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadInventory();
});

async function loadInventory() {
    const res = await SmartProcure.fetch('/inventory');
    const tbody = document.getElementById('inventoryTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No inventory balances recorded.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(i => `
            <tr>
                <td><code>${i.productSku}</code></td>
                <td><span class="fw-bold text-dark">${i.productName}</span></td>
                <td><span class="badge bg-light text-dark border">${i.warehouseName}</span></td>
                <td><span class="fw-bold">${i.quantityOnHand}</span></td>
                <td><span class="text-muted">${i.quantityAllocated}</span></td>
                <td><span class="fw-bold text-primary">${i.quantityAvailable}</span></td>
                <td><span class="text-secondary">${i.minStockLevel}</span></td>
                <td>
                    ${i.isLowStock 
                        ? '<span class="badge bg-danger-subtle text-danger border border-danger-subtle"><i class="bi bi-exclamation-triangle me-1"></i> LOW STOCK</span>' 
                        : '<span class="badge bg-success-subtle text-success border border-success-subtle">NORMAL</span>'}
                </td>
            </tr>
        `).join('');
    }
}

async function submitStockAdjust() {
    const productId = document.getElementById('adjProductId').value;
    const warehouseId = document.getElementById('adjWarehouseId').value;
    const quantityAdjustment = document.getElementById('adjQuantity').value;

    const res = await SmartProcure.fetch(`/inventory/adjust?productId=${productId}&warehouseId=${warehouseId}&quantityAdjustment=${quantityAdjustment}`, {
        method: 'POST'
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('stockAdjustModal'));
        modal.hide();
        document.getElementById('stockForm').reset();
        loadInventory();
    } else {
        alert(res ? res.message : 'Error adjusting stock');
    }
}
