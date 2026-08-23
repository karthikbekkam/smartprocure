// Purchase Order Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadPos();
});

async function loadPos() {
    const res = await SmartProcure.fetch('/purchase-orders');
    const tbody = document.getElementById('poTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No purchase orders found.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(po => `
            <tr>
                <td><span class="fw-bold text-dark">${po.poNumber}</span></td>
                <td><span class="fw-semibold">${po.vendorName}</span></td>
                <td>${po.deliveryDate || 'N/A'}</td>
                <td><span class="fw-semibold text-primary">$${Number(po.totalAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</span></td>
                <td><span class="badge-status ${po.status === 'APPROVED' || po.status === 'SENT_TO_VENDOR' ? 'badge-approved' : 'badge-review'}">${po.status}</span></td>
                <td class="text-end">
                    ${renderPoActions(po)}
                </td>
            </tr>
        `).join('');
    }
}

function renderPoActions(po) {
    if (po.status === 'DRAFT') {
        return `<button class="btn btn-sm btn-outline-success me-1" onclick="approvePo(${po.id})">Approve PO</button>`;
    }
    if (po.status === 'APPROVED') {
        return `<button class="btn btn-sm btn-outline-primary" onclick="sendPoToVendor(${po.id})">Dispatch to Vendor</button>`;
    }
    return '<span class="text-muted small">Sent</span>';
}

async function approvePo(id) {
    if (!confirm('Approve this purchase order?')) return;
    const res = await SmartProcure.fetch(`/purchase-orders/${id}/approve`, { method: 'POST' });
    if (res && res.success) loadPos();
    else alert(res ? res.message : 'Approval failed');
}

async function sendPoToVendor(id) {
    if (!confirm('Dispatch PO to vendor?')) return;
    const res = await SmartProcure.fetch(`/purchase-orders/${id}/send-to-vendor`, { method: 'POST' });
    if (res && res.success) loadPos();
    else alert(res ? res.message : 'Dispatch failed');
}

async function submitPoForm() {
    const payload = {
        vendorId: Number(document.getElementById('poVendorId').value),
        items: [
            {
                productId: Number(document.getElementById('poProductId').value),
                quantityOrdered: Number(document.getElementById('poQuantity').value),
                unitPrice: parseFloat(document.getElementById('poUnitPrice').value)
            }
        ]
    };

    const res = await SmartProcure.fetch('/purchase-orders', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('newPoModal'));
        modal.hide();
        document.getElementById('poForm').reset();
        loadPos();
    } else {
        alert(res ? res.message : 'Error creating purchase order');
    }
}
