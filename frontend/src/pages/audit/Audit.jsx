import React, { useState, useCallback, useEffect } from 'react';
// added superadmin layout
import SuperAdminLayout from '../../layouts/SuperAdminLayout';
// imported pageheader component
import PageHeader from '../../components/common/pageheader/PageHeader';
// imported common components
import InputField from '../../components/common/inputfield/InputField';
import OptionInputBox from '../../components/common/optioninputbox/OptionInputBox';
import Button from '../../components/common/button/Button';
// data table import
import { DataTable } from '../../components/common/table';
import { AUDIT_LOGS_COLUMNS } from '../../config/columnConfig';
// api service import
import { auditServices } from "../../services/apiService";

// imported audit css
import './Audit.css';

const Audit = () => {
  // State for form data
  const [formData, setFormData] = useState({
    startDate: '',
    endDate: '',
    user: '',
    module: '',
    action: ''
  });

  // State for table data
  const [tableData, setTableData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = useState(false);
const handleInputChange = (e) => {
  const { name, value } = e.target;

  setFormData(prev => {
    // If startDate is cleared, also clear endDate
    if (name === 'startDate' && !value) {
      return {
        ...prev,
        startDate: '',
        endDate: '', // clear endDate
      };
    }

    // If startDate changes and endDate is before it, clear endDate
    if (name === 'startDate' && prev.endDate && prev.endDate < value) {
      return {
        ...prev,
        startDate: value,
        endDate: '', // clear invalid endDate
      };
    }

    return {
      ...prev,
      [name]: value
    };
  });
};


  const fetchData = useCallback(
    async ({ page = 0, size = 10, sortField, sortOrder, filters }) => {
      try {
        setLoading(true);

        // -------------------------------
        // Build filter object (API format)
        // -------------------------------
        const filter = {
          actorType: filters?.user || undefined,
          entityType: filters?.module || undefined,
          action: filters?.action ? filters.action.toUpperCase() : undefined,
          fromDate: filters?.startDate
            ? new Date(filters.startDate).toISOString()
            : undefined,
          toDate: filters?.endDate
            ? new Date(filters.endDate).toISOString()
            : undefined,
        };


        // -------------------------------
        // Pageable
        // -------------------------------
        const pageable = {
          page,
          size,
          sort: sortField
            ? [`${sortField},${sortOrder || "desc"}`]
            : ["createdAt,desc"],
        };

        // -------------------------------
        // API call
        // -------------------------------
        const response = await auditServices.getAuditLogs({
          filter,
          pageable,
        });

        const data = response?.data || {};

        // -------------------------------
        // MAP API → TABLE COLUMN KEYS
        // -------------------------------
        const mappedContent = (data.content || []).map((item) => ({
          id: item.id,

          // matches accessorKey: 'timestamp'
          timestamp: item.createdAt,

          // matches accessorKey: 'action'
          action: item.action,

          // matches accessorKey: 'performedBy'
          performedBy: item.actorType,

          // matches accessorKey: 'module'
          module: item.entityType,

          // matches accessorKey: 'details'
          details: item.metaJson,
        }));

        setTableData({
          content: mappedContent,
          totalElements: data.totalElements || 0,
          totalPages: data.totalPages || 0,
          number: data.number || 0,
          size: data.size || size,
        });
      } catch (error) {
        console.error("Error fetching audit logs:", error);
        setTableData({
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: 10,
        });
      } finally {
        setLoading(false);
      }
    },
    [formData]
  );

  // Handle filter button click
  // Call API using current form values
  const handleFilter = () => {
    fetchData({
      page: 0,
      size: tableData.size,
      filters: {
        user: formData.user,
        module: formData.module,
        action: formData.action,
        startDate: formData.startDate,
        endDate: formData.endDate,
      },
    });
  };


  // Handle reset button click
  const handleReset = () => {
    setFormData({
      startDate: '',
      endDate: '',
      user: '',
      module: '',
      action: ''
    });
    fetchData({ page: 0, size: 10 });
  };

  // Handle view audit log details
  const handleViewAuditLog = (row) => {
    // Navigates to /audits/:id/view via DataTable's basePath
  };


  return (
    <SuperAdminLayout>
      {/* audit container */}
      <div className='audit-container'>
        {/* page header component*/}
        <PageHeader
          title="Audit Logs"
          subtitle="Track all system activities and changes"
        />
        {/* filter section */}
        <div className='audit-filters'>
          <div className='filter-row'>
            {/* Start Date input */}
            <InputField
              label="Start Date"
              name="startDate"
              value={formData.startDate}
              onChange={handleInputChange}
              type="date"
              validationType="EVERYTHING"
              classN="large"
            />
            {/* End Date input */}
            <InputField
              label="End Date"
              name="endDate"
              value={formData.endDate}
              onChange={handleInputChange}
              type="date"
              validationType="EVERYTHING"
              classN="large"
              disabled={!formData.startDate}       // 🔹 Disabled until startDate is set
              min={formData.startDate || undefined}
            />
            {/* User input */}
            <InputField
              label="User"
              name="user"
              value={formData.user}
              onChange={handleInputChange}
              validationType="EVERYTHING"
              classN="large"
            />
            {/* Module dropdown */}
            <OptionInputBox
              label="Module"
              name="module"
              value={formData.module}
              onChange={handleInputChange}
              validationType="EVERYTHING"
              options={[
                { code: '', value: 'All modules' },
                { code: 'vendor-approvals', value: 'Vendor Approvals' },
                { code: 'ca-approvals', value: 'CA Approvals' },
                { code: 'plans-management', value: 'Plans Management' },
                { code: 'discounts-offers', value: 'Discounts & Offers' },
              ]}
            />
            {/* Action dropdown */}
            <OptionInputBox
              label="Action"
              name="action"
              value={formData.action}
              onChange={handleInputChange}
              validationType="EVERYTHING"
              options={[
                { code: '', value: 'All actions' },
                { code: 'create', value: 'Create' },
                { code: 'update', value: 'Update' },
                { code: 'delete', value: 'Delete' },
                { code: 'approve', value: 'Approve' },
                { code: 'reject', value: 'Reject' },
              ]}
            />
            {/* Filter buttons */}
            <div className='filter-buttons'>
              <Button
                text="Filter"
                className="apply-filters-btn"
                onClick={handleFilter}
              />
              <Button
                text="Reset"
                className="apply-filters-btn"
                onClick={handleReset}
              />
            </div>
          </div>
        </div>

        {/* Activity Logs Table */}
        <div className="audit-table">
          <DataTable
            data={tableData}
            columns={AUDIT_LOGS_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            basePath="/audits"
            primaryKeys={['id']}
            showActions={true}
            showEditButton={false}
            showViewButton={true}
            editButtonDisabled={true}
            viewButtonDisabled={false}
            className="audit-logs-data-table"
          />


        </div>
      </div>
    </SuperAdminLayout>
  );
};

export default Audit;