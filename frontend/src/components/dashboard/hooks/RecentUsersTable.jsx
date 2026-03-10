import React from "react";
import { formatDateStandardSpace } from '../../../utils/dateUtils';
import "./RecentUsersTable.css";
import "../CardHeader.css";

const RecentUsersTable = ({ title = "Recent Users", data = [], loading, type = "users", onViewClick }) => {

    const isBatches = type === "batches";
  const isTenants = type === "tenants";
  const isVendorBatchTenants = type === "vendor_batch_tenants";
  const isDetailedTenants = type === "detailed_tenants";
  const entityName = isBatches ? "Batches"
    : isTenants ? "Tenants"
      : isVendorBatchTenants ? "Recordings"
        : isDetailedTenants ? "Tenants"
          : "Users";
          
  if (loading) {
    return (
      <div className="users-card">
        <div className="users-loading">Loading {type}...</div>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="users-card">
        <div className="card-header">
          <h3 className="card-title">{title}</h3>
          <div className="card-header__trail">
            <span className="card-header__count">0 {entityName}</span>
          </div>
        </div>
        <div className="users-no-data">
          <div className="users-no-data__icon">
            <i className="bi bi-inbox"></i>
          </div>
          <div className="users-no-data__text">No Data Found</div>
        </div>
      </div>
    );
  }



  return (
    <div className="users-card">

      <div className="card-header">
        <h3 className="card-title">{title}</h3>
        <div className="card-header__trail">
          <span className="card-header__count">{data.length} {entityName}</span>
        </div>
      </div>

      {/* Table */}
      <div className="users-table-wrapper">
        <table className="users-table">
          <thead>
            <tr>
              {isBatches ? (
                <>
                  <th>Plan</th>
                  <th>Activations</th>
                  <th>Status</th>
                  <th>Issued Date</th>
                  {onViewClick && <th>Action</th>}
                </>
              ) : isTenants ? (
                <>
                  <th>Tenant</th>
                  <th>Revenue (₹)</th>
                  <th>Activations</th>
                  {onViewClick && <th>Action</th>}
                </>
              ) : isVendorBatchTenants ? (
                <>
                  <th>Batch ID</th>
                  <th>Vendor ID</th>
                  <th>Plan</th>
                  <th>Keys (Used/Total)</th>
                  <th>Status</th>
                  {onViewClick && <th>Action</th>}
                </>
              ) : isDetailedTenants ? (
                <>
                  <th>Admin</th>
                  <th>Email</th>
                  <th>Status</th>
                  {onViewClick && <th>Action</th>}
                </>
              ) : (
                <>
                  <th>User</th>
                  <th>Mobile No.</th>
                  <th>Status</th>
                  <th>Joined</th>
                  {onViewClick && <th>Action</th>}
                </>
              )}
            </tr>
          </thead>

          <tbody>
            {data.map((item, index) => (
              <tr key={item.id || item.tenantId || item.batchId || index}>
                {isBatches ? (
                  <>
                    <td>
                      <div className="user-cell">
                        <div className="avatar">
                          {item.planName?.charAt(0).toUpperCase() || 'B'}
                        </div>
                        <div>
                          <div className="user-name">{item.planName}</div>
                        </div>
                      </div>
                    </td>
                    <td>{item.totalActivations} keys</td>
                    <td>
                      <span className={`status-badge ${item.status?.toLowerCase()}`}>
                        {item.status}
                      </span>
                    </td>
                    <td className="joined-date">
                      {item.issuedAt ? formatDateStandardSpace(item.issuedAt) : 'N/A'}
                    </td>
                    {onViewClick && (
                      <td>
                        <button className="view-action-btn" onClick={() => onViewClick(item)} title="View Details">
                          <i className="bi bi-eye"></i>
                        </button>
                      </td>
                    )}
                  </>
                ) : isTenants ? (
                  <>
                    <td>
                      <div className="user-cell">
                        <div className="avatar">
                          {item.tenantName?.charAt(0).toUpperCase() || 'T'}
                        </div>
                        <div>
                          <div className="user-name">{item.tenantName}</div>
                          <div className="user-email">{item.tenantEmail}</div>
                        </div>
                      </div>
                    </td>
                    <td className="user-name">
                      ₹{Number(item.revenue).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    </td>
                    <td>{item.activations} key{item.activations !== 1 ? 's' : ''}</td>
                    {onViewClick && (
                      <td>
                        <button className="view-action-btn" onClick={() => onViewClick(item)} title="View Details">
                          <i className="bi bi-eye"></i>
                        </button>
                      </td>
                    )}
                  </>
                ) : isVendorBatchTenants ? (
                  <>
                    <td className="joined-date">{item.batchId}</td>
                    <td className="joined-date">{item.vendorId}</td>
                    <td>{item.batchPlan}</td>
                    <td>{item.used} / {item.total}</td>
                    <td>
                      <span className={`status-badge ${item.activationStatus?.toLowerCase()}`}>
                        {item.activationStatus}
                      </span>
                    </td>
                    {onViewClick && (
                      <td>
                        <button
                          className="view-action-btn"
                          onClick={() => onViewClick(item)}
                          title="View Details"
                        >
                          <i className="bi bi-eye"></i>
                        </button>
                      </td>
                    )}
                  </>
                ) : isDetailedTenants ? (
                  <>
                    <td>
                      <div className="user-cell">
                        <div className="avatar">
                          {item.adminName?.charAt(0).toUpperCase() || 'A'}
                        </div>
                        <div>
                          <div className="user-name">{item.adminName}</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div className="user-email">{item.adminEmail}</div>
                    </td>
                    <td>
                      <span className={`status-badge ${item.status?.toLowerCase()}`}>
                        {item.status}
                      </span>
                    </td>
                    {onViewClick && (
                      <td>
                        <button className="view-action-btn" onClick={() => onViewClick(item)} title="View Details">
                          <i className="bi bi-eye"></i>
                        </button>
                      </td>
                    )}
                  </>
                ) : (
                  <>
                    <td>
                      <div className="user-cell">
                        <div className="avatar">
                          {item.name?.charAt(0).toUpperCase() || 'U'}
                        </div>
                        <div>
                          <div className="user-name">{item.name}</div>
                          <div className="user-email">{item.email}</div>
                        </div>
                      </div>
                    </td>
                    <td>{item.phone}</td>
                    <td>
                      <span className={`status-badge ${item.status?.toLowerCase()}`}>
                        {item.status}
                      </span>
                    </td>
                    <td className="joined-date">
                      {item.createdAt ? formatDateStandardSpace(item.createdAt) : 'N/A'}
                    </td>
                    {onViewClick && (
                      <td>
                        <button className="view-action-btn" onClick={() => onViewClick(item)} title="View Details">
                          <i className="bi bi-eye"></i>
                        </button>
                      </td>
                    )}
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default RecentUsersTable;
