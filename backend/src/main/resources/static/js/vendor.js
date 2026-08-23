// Vendor Management Frontend Module

document.addEventListener('DOMContentLoaded', () => {
    loadVendors();
});

async function loadVendors() {
    const status = document.getElementById('statusFilter').value;
    const url = status ? `/vendors/status/${status}` : '/vendors';
    
    const res = await SmartProcure.fetch(url);
    const tbody = document.getElementById('vendorTableBody');

    if (res && res.success && res.data && res.data.content) {
        if (res.data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No vendor records found.</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.content.map(v => `
            <tr>
                <td><span class="fw-semibold">#VND-${v.id}</span></td>
                <td><span class="fw-bold text-dark">${v.companyName}</span></td>
                <td><code>${v.taxId}</code></td>
                <td>${v.contactEmail}</td>
                <td>${v.contactPhone}</td>
                <td><span class="badge bg-light text-dark border">${v.paymentTerms}</span></td>
                <td>${getStatusBadge(v.status)}</td>
                <td class="text-end">
                    ${renderActionButtons(v)}
                </td>
            </tr>
        `).join('');
    }
}

function getStatusBadge(status) {
    switch (status) {
        case 'APPROVED': return '<span class="badge-status badge-approved">APPROVED</span>';
        case 'ACTIVE': return '<span class="badge-status badge-approved">ACTIVE</span>';
        case 'UNDER_REVIEW': return '<span class="badge-status badge-review">UNDER REVIEW</span>';
        case 'REGISTERED': return '<span class="badge bg-info-subtle text-info border border-info-subtle">REGISTERED</span>';
        case 'REJECTED': return '<span class="badge-status badge-rejected">REJECTED</span>';
        case 'INACTIVE': return '<span class="badge bg-secondary-subtle text-secondary border">INACTIVE</span>';
        default: return `<span class="badge bg-light text-dark">${status}</span>`;
    }
}

function renderActionButtons(v) {
    let btns = '';
    if (v.status === 'REGISTERED') {
        btns += `<button class="btn btn-sm btn-outline-warning me-1" onclick="changeStatus(${v.id}, 'submit-review')">Submit Review</button>`;
    }
    if (v.status === 'UNDER_REVIEW' || v.status === 'REGISTERED') {
        btns += `<button class="btn btn-sm btn-outline-success me-1" onclick="changeStatus(${v.id}, 'approve')">Approve</button>`;
        btns += `<button class="btn btn-sm btn-outline-danger me-1" onclick="changeStatus(${v.id}, 'reject')">Reject</button>`;
    }
    if (v.status === 'APPROVED' || v.status === 'INACTIVE') {
        btns += `<button class="btn btn-sm btn-outline-primary me-1" onclick="changeStatus(${v.id}, 'activate')">Activate</button>`;
    }
    if (v.status === 'ACTIVE') {
        btns += `<button class="btn btn-sm btn-outline-secondary me-1" onclick="changeStatus(${v.id}, 'deactivate')">Deactivate</button>`;
    }
    return btns;
}

async function changeStatus(id, action) {
    if (!confirm(`Are you sure you want to ${action} this vendor?`)) return;

    const res = await SmartProcure.fetch(`/vendors/${id}/${action}`, {
        method: 'POST'
    });

    if (res && res.success) {
        loadVendors();
    } else {
        alert(res ? res.message : 'Action failed');
    }
}

async function submitVendorForm() {
    const payload = {
        companyName: document.getElementById('companyName').value,
        taxId: document.getElementById('taxId').value,
        contactEmail: document.getElementById('contactEmail').value,
        contactPhone: document.getElementById('contactPhone').value,
        address: document.getElementById('address').value,
        paymentTerms: document.getElementById('paymentTerms').value,
        businessLicenseNumber: document.getElementById('businessLicenseNumber').value
    };

    const res = await SmartProcure.fetch('/vendors', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res && res.success) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('newVendorModal'));
        modal.hide();
        document.getElementById('vendorForm').reset();
        loadVendors();
    } else {
        alert(res ? res.message : 'Error creating vendor');
    }
}
