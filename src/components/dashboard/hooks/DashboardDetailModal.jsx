import React from 'react';
import './DashboardDetailModal.css';

const DashboardDetailModal = ({ show, onClose, data, type }) => {
    if (!show || !data) return null;

    const isBatchType = type === 'batch';
    const isTenantType = type === 'tenant';

    return (
        <div className="dashboard-modal-overlay" onClick={onClose}>
            <div className="dashboard-modal" onClick={e => e.stopPropagation()}>
                <div className="dashboard-modal-header">
                    <h3>
                        {isBatchType ? 'Vendor Batch & Tenant Details' :
                            isTenantType ? 'Tenant Detailed Overview' :
                                'Details'}
                    </h3>
                    <button className="close-btn" onClick={onClose}>×</button>
                </div>

                <div className="dashboard-modal-body">
                    <div className="dashboard-details-grid">

                        {/* --- BATCH TYPE SECTIONS --- */}
                        {isBatchType && (
                            <>
                                <div className="detail-section">
                                    <h4>Batch Information</h4>
                                    <div className="detail-row">
                                        <span className="label">Batch ID:</span>
                                        <span className="value">{data.batchId}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Vendor ID:</span>
                                        <span className="value">{data.vendorId}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Batch Plan:</span>
                                        <span className="value">{data.batchPlan}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Batch Cost:</span>
                                        <span className="value">{data.cost}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Activations:</span>
                                        <span className="value">{data.used} / {data.total} Used</span>
                                    </div>
                                </div>

                                <div className="detail-section">
                                    <h4>Linked Tenant</h4>
                                    <div className="detail-row">
                                        <span className="label">Tenant ID:</span>
                                        <span className="value">{data.tenantId}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Name:</span>
                                        <span className="value">{data.tenantName}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Email:</span>
                                        <span className="value">{data.tenantEmail}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Status:</span>
                                        <span className={`value status-badge ${data.activationStatus?.toLowerCase()}`}>
                                            {data.activationStatus}
                                        </span>
                                    </div>
                                </div>

                                <div className="detail-section full-width">
                                    <h4>Plan Specifications</h4>
                                    <div className="spec-grid">
                                        <div className="spec-item">
                                            <span className="label">Current Plan:</span>
                                            <span className="value highlight-plan">{data.currentPlan}</span>
                                        </div>
                                        <div className="spec-item">
                                            <span className="label">Max Users:</span>
                                            <span className="value">{data.allowedUsers}</span>
                                        </div>
                                        <div className="spec-item">
                                            <span className="label">Max Companies:</span>
                                            <span className="value">{data.allowedCompanies}</span>
                                        </div>
                                        <div className="spec-item">
                                            <span className="label">Validity:</span>
                                            <span className="value">{data.planDuration} {data.billingPeriod}</span>
                                        </div>
                                    </div>
                                </div>
                            </>
                        )}

                        {/* --- TENANT TYPE SECTIONS --- */}
                        {isTenantType && (
                            <>
                                <div className="detail-section">
                                    <h4>Primary Administrator</h4>
                                    <div className="detail-row">
                                        <span className="label">Admin Name:</span>
                                        <span className="value">{data.adminName}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Admin Email:</span>
                                        <span className="value">{data.adminEmail}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Current Status:</span>
                                        <span className={`value status-badge ${data.status?.toLowerCase()}`}>
                                            {data.status}
                                        </span>
                                    </div>
                                </div>

                                <div className="detail-section">
                                    <h4>Organization & Plan</h4>
                                    <div className="detail-row">
                                        <span className="label">Tenant Name:</span>
                                        <span className="value">{data.tenantName}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Tenant ID:</span>
                                        <span className="value">#{data.tenantId}</span>
                                    </div>
                                    <div className="detail-row">
                                        <span className="label">Current Plan:</span>
                                        <span className="value highlight-plan">{data.currentPlan}</span>
                                    </div>
                                </div>

                                <div className="detail-section full-width">
                                    <h4>Team Members (Users under {data.adminName})</h4>
                                    <div className="users-list-container">
                                        {data.users && data.users.length > 0 ? (
                                            <table className="modal-users-table">
                                                <thead>
                                                    <tr>
                                                        <th>Member Name</th>
                                                        <th>Email</th>
                                                        <th>Phone</th>
                                                        <th>Status</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {data.users.map((user, idx) => (
                                                        <tr key={idx}>
                                                            <td>{user.name}</td>
                                                            <td>{user.email}</td>
                                                            <td>{user.phone}</td>
                                                            <td>
                                                                <span className={`status-badge ${user.status?.toLowerCase()}`}>
                                                                    {user.status}
                                                                </span>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                </tbody>
                                            </table>
                                        ) : (
                                            <div className="no-users-msg">No additional team members found for this admin.</div>
                                        )}
                                    </div>
                                </div>

                                <div className="detail-section full-width spec-grid" style={{ marginTop: '0', background: '#fcfcfc', border: '1px dashed #e2e8f0' }}>
                                    <div className="spec-item">
                                        <span className="label">Total Managed Users:</span>
                                        <span className="value">{data.usersCount}</span>
                                    </div>
                                    <div className="spec-item">
                                        <span className="label">Last Activity Sync:</span>
                                        <span className="value">{data.lastSynced}</span>
                                    </div>
                                </div>
                            </>
                        )}

                    </div>
                </div>

                <div className="dashboard-modal-footer">
                    <button className="done-btn" onClick={onClose}>Close Detail View</button>
                </div>
            </div>
        </div>
    );
};

export default DashboardDetailModal;
