import React from "react";
import { formatDateStandardSpace } from '../../../utils/dateUtils';
import "./RecentUsersTable.css";

const RecentUsersTable = ({ title = "Recent Users", data = [], loading, type = "users" }) => {

  if (loading) {
    return (
      <div className="users-card">
        <div className="users-loading">Loading {type}...</div>
      </div>
    );
  }

  const isBatches = type === "batches";
  const isTenants = type === "tenants";
  const entityName = isBatches ? "Batches" : isTenants ? "Tenants" : "Users";

  return (
    <div className="users-card">

      {/* Header */}
      <div className="users-header">
        <h3>{title}</h3>
        <span className="users-count">{data.length} {entityName}</span>
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
                </>
              ) : isTenants ? (
                <>
                  <th>Tenant</th>
                  <th>Revenue (₹)</th>
                  <th>Activations</th>
                </>
              ) : (
                <>
                  <th>User</th>
                  <th>Mobile No.</th>
                  <th>Status</th>
                  <th>Joined</th>
                </>
              )}
            </tr>
          </thead>

          <tbody>
            {data.map((item, index) => (
              <tr key={item.id || item.batchId || index}>
                {isBatches ? (
                  <>
                    {/* Plan Name with Avatar */}
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

                    {/* Total Activations */}
                    <td>{item.totalActivations} keys</td>

                    {/* Status */}
                    <td>
                      <span className={`status-badge ${item.status?.toLowerCase()}`}>
                        {item.status}
                      </span>
                    </td>

                    {/* Date */}
                    <td className="joined-date">
                      {item.issuedAt ? formatDateStandardSpace(item.issuedAt) : 'N/A'}
                    </td>
                  </>
                ) : isTenants ? (
                  <>
                    {/* Tenant Name with Avatar */}
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

                    {/* Revenue */}
                    <td className="user-name">
                      ₹{Number(item.revenue).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    </td>

                    {/* Activations count */}
                    <td>{item.activations} key{item.activations !== 1 ? 's' : ''}</td>
                  </>
                ) : (
                  <>
                    {/* User Info */}
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

                    {/* Mobile No. */}
                    <td>{item.phone}</td>

                    {/* Status */}
                    <td>
                      <span className={`status-badge ${item.status?.toLowerCase()}`}>
                        {item.status}
                      </span>
                    </td>

                    {/* Date */}
                    <td className="joined-date">
                      {item.createdAt ? formatDateStandardSpace(item.createdAt) : 'N/A'}
                    </td>
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