import React from "react";
import "./RecentUsersTable.css";

const RecentUsersTable = ({ title = "Recent Users", data = [], loading }) => {

  if (loading) {
    return (
      <div className="users-card">
        <div className="users-loading">Loading users...</div>
      </div>
    );
  }

  return (
    <div className="users-card">
      
      {/* Header */}
      <div className="users-header">
        <h3>{title}</h3>
        <span className="users-count">{data.length} Users</span>
      </div>

      {/* Table */}
      <div className="users-table-wrapper">
        <table className="users-table">
          <thead>
            <tr>
              <th>User</th>
              <th>Mobile No.</th>
              <th>Status</th>
              <th>Joined</th>
            </tr>
          </thead>

          <tbody>
            {data.map((user) => (
              <tr key={user.id}>
                {/* User Info */}
                <td>
                  <div className="user-cell">
                    <div className="avatar">
                      {user.name?.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <div className="user-name">{user.name}</div>
                      <div className="user-email">{user.email}</div>
                    </div>
                  </div>
                </td>
             
                {/* Mobile No. */}
                <td>{user.phone}</td>
                {/* Status */}
                <td>
                  <span className={`status-badge ${user.status?.toLowerCase()}`}>
                    {user.status}
                  </span>
                </td>
                {/* Date */}
                <td className="joined-date">
                  {new Date(user.createdAt).toLocaleDateString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default RecentUsersTable;