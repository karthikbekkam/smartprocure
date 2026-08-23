/**
 * SmartProcure Vendor Management JavaScript Handler
 */
const VendorManager = (() => {
  
  const approveVendor = async (vendorId) => {
    if (!confirm('Are you sure you want to approve this vendor?')) return;
    try {
      await SmartProcure.apiFetch(`/api/v1/vendors/${vendorId}/approve`, { method: 'POST' });
      SmartProcure.showAlert('Vendor approved successfully!', 'success');
      setTimeout(() => location.reload(), 800);
    } catch (err) {
      SmartProcure.showAlert(`Approval failed: ${err.message}`, 'danger');
    }
  };

  const rejectVendor = async (vendorId) => {
    const reason = prompt('Please enter the reason for rejection:');
    if (reason === null) return;
    try {
      await SmartProcure.apiFetch(`/api/v1/vendors/${vendorId}/reject`, {
        method: 'POST',
        body: JSON.stringify({ reason })
      });
      SmartProcure.showAlert('Vendor status set to Rejected.', 'warning');
      setTimeout(() => location.reload(), 800);
    } catch (err) {
      SmartProcure.showAlert(`Rejection failed: ${err.message}`, 'danger');
    }
  };

  const loadVendorDetailModal = async (vendorId) => {
    const modalContent = document.getElementById('vendorDetailModalBody');
    if (!modalContent) return;
    
    modalContent.innerHTML = '<div class="text-center p-4"><span class="spinner-border text-primary"></span><p class="mt-2">Loading vendor details...</p></div>';
    
    try {
      const vendor = await SmartProcure.apiFetch(`/api/v1/vendors/${vendorId}`);
      modalContent.innerHTML = `
        <div class="row g-3">
          <div class="col-md-6">
            <h6 class="text-muted mb-1">Vendor Name</h6>
            <p class="fw-bold">${vendor.name || 'N/A'}</p>
          </div>
          <div class="col-md-6">
            <h6 class="text-muted mb-1">Status</h6>
            <span class="badge badge-status badge-${(vendor.status || 'pending').toLowerCase()}">${vendor.status || 'PENDING'}</span>
          </div>
          <div class="col-md-6">
            <h6 class="text-muted mb-1">Tax ID / Tax Registration</h6>
            <p>${vendor.taxId || 'N/A'}</p>
          </div>
          <div class="col-md-6">
            <h6 class="text-muted mb-1">Contact Email</h6>
            <p>${vendor.email || 'N/A'}</p>
          </div>
          <div class="col-md-6">
            <h6 class="text-muted mb-1">Phone Number</h6>
            <p>${vendor.phone || 'N/A'}</p>
          </div>
          <div class="col-md-6">
            <h6 class="text-muted mb-1">Rating</h6>
            <p class="text-warning"><i class="fas fa-star me-1"></i> ${vendor.rating || '4.5'} / 5.0</p>
          </div>
          <div class="col-12">
            <h6 class="text-muted mb-1">Address</h6>
            <p>${vendor.address || 'N/A'}</p>
          </div>
        </div>
      `;
    } catch (err) {
      modalContent.innerHTML = `<div class="alert alert-danger">Error loading details: ${err.message}</div>`;
    }
  };

  const filterByStatus = (status) => {
    const rows = document.querySelectorAll('.vendor-row');
    rows.forEach(row => {
      const rowStatus = row.getAttribute('data-status');
      if (!status || status === 'ALL' || rowStatus === status) {
        row.style.display = '';
      } else {
        row.style.display = 'none';
      }
    });
  };

  return {
    approveVendor,
    rejectVendor,
    loadVendorDetailModal,
    filterByStatus
  };
})();

document.addEventListener('DOMContentLoaded', () => {
  const statusFilterSelect = document.getElementById('vendorStatusFilter');
  if (statusFilterSelect) {
    statusFilterSelect.addEventListener('change', (e) => {
      VendorManager.filterByStatus(e.target.value);
    });
  }
});
