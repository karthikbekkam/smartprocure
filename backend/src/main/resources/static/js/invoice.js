// Invoice Management Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadInvoices();
});

async function loadInvoices() {
    const res = await SmartProcure.fetch('/invoices');
    const tbody = document.getElementById('invoiceTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No vendor invoices recorded.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(inv => `
            <tr>
                <td><span class="fw-bold text-dark">${inv.invoiceNumber}</span></td>
                <td><code>${inv.poNumber}</code></td>
                <td><span class="fw-semibold">${inv.vendorName}</span></td>
                <td>${inv.invoiceDate}</td>
                <td>${inv.dueDate}</td>
                <td><span class="fw-semibold text-primary">$${Number(inv.totalAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</span></td>
                <td><span class="badge-status ${inv.status === 'APPROVED' || inv.status === 'PAID' ? 'badge-approved' : 'badge-review'}">${inv.status}</span></td>
                <td class="text-end">
                    ${inv.status !== 'APPROVED' && inv.status !== 'PAID' 
                        ? `<button class="btn btn-sm btn-outline-success" onclick="approveInvoice(${inv.id})">Approve Invoice</button>` 
                        : '<span class="text-muted small">Approved</span>'}
                </td>
            </tr>
        `).join('');
    }
}

async function approveInvoice(id) {
    if (!confirm('Approve vendor invoice for 3-way matching settlement?')) return;
    const res = await SmartProcure.fetch(`/invoices/${id}/approve`, { method: 'POST' });
    if (res && res.success) loadInvoices();
    else alert(res ? res.message : 'Approval failed');
}

async function submitInvoiceForm() {
    const payload = {
        purchaseOrderId: Number(document.getElementById('invPoId').value),
        vendorId: Number(document.getElementById('invVendorId').value),
        totalAmount: parseFloat(document.getElementById('invAmount').value)
    };

    const res = await SmartProcure.fetch('/invoices', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('newInvoiceModal'));
        modal.hide();
        document.getElementById('invoiceForm').reset();
        loadInvoices();
    } else {
        alert(res ? res.message : 'Error submitting invoice');
    }
}
