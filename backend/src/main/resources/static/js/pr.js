// Purchase Request Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadPrs();
});

async function loadPrs() {
    const res = await SmartProcure.fetch('/purchase-requests');
    const tbody = document.getElementById('prTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No purchase requisitions found.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(pr => `
            <tr>
                <td><span class="fw-bold text-dark">${pr.prNumber}</span></td>
                <td><span class="badge bg-light text-dark border">${pr.department}</span></td>
                <td>${pr.requestedByName}</td>
                <td><span class="fw-semibold text-primary">$${Number(pr.totalAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</span></td>
                <td><span class="badge-status ${pr.status === 'APPROVED' ? 'badge-approved' : 'badge-review'}">${pr.status}</span></td>
                <td class="text-end">
                    ${pr.status !== 'APPROVED' ? `<button class="btn btn-sm btn-outline-success" onclick="approvePr(${pr.id})">Approve PR</button>` : '<span class="text-muted small">Approved</span>'}
                </td>
            </tr>
        `).join('');
    }
}

async function approvePr(id) {
    if (!confirm('Approve this purchase request?')) return;
    const res = await SmartProcure.fetch(`/purchase-requests/${id}/approve`, { method: 'POST' });
    if (res && res.success) {
        loadPrs();
    } else {
        alert(res ? res.message : 'Approval failed');
    }
}

async function submitPrForm() {
    const payload = {
        department: document.getElementById('prDepartment').value,
        items: [
            {
                productId: Number(document.getElementById('prProductId').value),
                quantity: Number(document.getElementById('prQuantity').value),
                estimatedUnitPrice: parseFloat(document.getElementById('prUnitPrice').value)
            }
        ]
    };

    const res = await SmartProcure.fetch('/purchase-requests', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('newPrModal'));
        modal.hide();
        document.getElementById('prForm').reset();
        loadPrs();
    } else {
        alert(res ? res.message : 'Error creating requisition');
    }
}
