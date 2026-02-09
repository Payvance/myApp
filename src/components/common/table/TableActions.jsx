import React from 'react';
import { NavLink } from 'react-router-dom';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './DataTable.css';

const TableActions = ({
  rowData,
  basePath = '',
  actionPath = '',
  showEditButton = true,
  showViewButton = true,
  showDeleteButton = false,

  showApproveButton = true,
  showIssueButton = false,
  showRejectButton = true,
  editButtonDisabled = false,
  viewButtonDisabled = false,
  deleteButtonDisabled = false,
  approveButtonDisabled = false,
  rejectButtonDisabled = false,
  issueButtonDisabled = false,
  onEdit,
  onView,
  onDelete,
  onApprove,
  onReject,
  onIssue,
  customActions = [],
}) => {
  const handleEdit = () => {
    if (onEdit) {
      onEdit(rowData);
    }
  };

  const handleView = () => {
    if (onView) {
      onView(rowData);
    }
  };

  const handleDelete = () => {
    if (onDelete) {
      onDelete(rowData);
    }
  };

  const handleApprove = () => {
    if (onApprove) {
      onApprove(rowData);
    }
  };

  const handleReject = () => {
    if (onReject) {
      onReject(rowData);
    }
  };

  const handleIssue = () => {
    if (onIssue) {
      onIssue(rowData);
    }
  };

  return (
    <div className="table-actions">
      {showEditButton && (
        onEdit ? (
          <button
            onClick={handleEdit}
            disabled={editButtonDisabled}
            className="action-button edit-button"
            title="Edit"
          >
            <i className="bi bi-pencil"></i>
          </button>
        ) : (
          <NavLink
            to={`${basePath}/${actionPath}/edit`}
            className="action-button edit-button"
            title="Edit"
          >
            <i className="bi bi-pencil"></i>
          </NavLink>
        )
      )}

      {showViewButton && (
        onView ? (
          <button
            onClick={handleView}
            disabled={viewButtonDisabled}
            className="action-button view-button"
            title="View"
          >
            <i className="bi bi-eye"></i>
          </button>
        ) : (
          <NavLink
            to={`${basePath}/${actionPath}/view`}
            className="action-button view-button"
            title="View"
          >
            <i className="bi bi-eye"></i>
          </NavLink>
        )
      )}

      {showDeleteButton && (
        onDelete ? (
          <button
            onClick={handleDelete}
            disabled={deleteButtonDisabled}
            className="action-button delete-button"
            title="Delete"
          >
            <i className="bi bi-trash"></i>
          </button>
        ) : (
          <button
            onClick={() => {
              if (window.confirm('Are you sure you want to delete this item?')) {
                // Handle delete navigation or API call
                console.log('Delete action for:', rowData);
              }
            }}
            disabled={deleteButtonDisabled}
            className="action-button delete-button"
            title="Delete"
          >
            <i className="bi bi-trash"></i>
          </button>
        )
      )}

      {showApproveButton && (
        <button
          onClick={handleApprove}
          disabled={typeof approveButtonDisabled === 'function' ? approveButtonDisabled(rowData) : approveButtonDisabled}
          className="action-button approve-button"
          title="Approve"
        >
          <i className="bi bi-check-circle"></i>
        </button>
      )}

      {showRejectButton && (
        <button
          onClick={handleReject}
          disabled={rejectButtonDisabled}
          className="action-button reject-button"
          title="Reject"
        >
          <i className="bi bi-x-circle"></i>
        </button>
      )}
      {showIssueButton && (
        onIssue ? (
          <button
            onClick={handleIssue}
            disabled={issueButtonDisabled}
            className="action-button issue-button"
            title="Issue"
          >
            <i className="bi bi-key"></i>
          </button>
        ) : (
          <NavLink
            to={`${basePath}/${actionPath}/issue`}
            className="action-button issue-button"
            title="Issue"
          >
            <i className="bi bi-key"></i>
          </NavLink>
        )
      )}

      {/* Custom actions */}
      {customActions.map((action, index) => (
        <button
          key={index}
          onClick={() => action.onClick(rowData)}
          disabled={action.disabled}
          className={`action-button ${action.className || ''}`}
          title={action.tooltip}
        >
          {action.icon}
        </button>
      ))}
    </div>
  );
};

export default TableActions;
