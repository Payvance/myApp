import React, { useCallback } from 'react';
import PageContainer from '../../../components/common/pagecontainer/PageContainer';
import VendorLayout from '../../../layouts/VendorLayout';
import { DataTable } from '../../../components/common/table';
import { LICENSE_KEYS_COLUMNS } from '../../../config/columnConfig';
import { vendorLicenseServices } from '../../../services/apiService';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import PopUp from '../../../components/common/popups/PopUp';
import Button from '../../../components/common/button/Button';
import { toast } from 'react-toastify';
import 'bootstrap-icons/font/bootstrap-icons.css';
// Theme CSS
import '../../../theme/LightTheme.css';
import './AssignedClients.css';

const AssignedClients = () => {
  // State for table data
  const [tableData, setTableData] = React.useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = React.useState(false);

  // State for view license details popup
  const [isViewLicensePopupOpen, setIsViewLicensePopupOpen] = React.useState(false);
  const [viewLicenseData, setViewLicenseData] = React.useState(null);

  // Handle issue license key
  const handleIssueKey = useCallback(async (rowData) => {
    try {
      await vendorLicenseServices.issueLicenseKey(rowData.id);
      // Refresh table data
      fetchData({ page: tableData.number, size: tableData.size });
    } catch (error) {
      console.error('Error issuing license key:', error);
    }
  }, [tableData.number, tableData.size]);

  // Handle view license details
  const handleViewLicense = async (rowData) => {
    try {
      console.log('Fetching license details for ID:', rowData.id);
      const response = await vendorLicenseServices.getActivationKeyDetail(rowData.id);
      const data = response.data;
      console.log('License details response:', data);
      
      setViewLicenseData(data);
      setIsViewLicensePopupOpen(true);
    } catch (error) {
      console.error('Error fetching license details:', error);
      const errorMessage = error?.response?.data?.message || 'Failed to fetch license details. Please try again.';
      toast.error(errorMessage);
    }
  };

  // Handle close view license popup
  const handleCloseViewLicensePopup = () => {
    setIsViewLicensePopupOpen(false);
    setViewLicenseData(null);
  };

  // Custom actions for license keys
  const customActions = [
    {
      icon: <i className="bi bi-key"></i>,
      tooltip: 'Issue Key',
      className: 'issue-key-button',
      onClick: handleIssueKey,
      disabled: (rowData) => rowData.status !== 'UNUSED', // Only allow issuing UNUSED keys
    },
  ];

  // Fetch data function - matches Spring Boot API contract
  const fetchData = useCallback(async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);

      // Map table parameters to API parameters
      const apiParams = {
        page: page || 0,
        size: size || 10,
        sortBy: sortField || 'createdAt',
        sortDir: sortOrder || 'desc',
        // Add filter parameters
        ...(filters?.search && { plainCodeLast4: filters.search }), // Map search to last 4 digits of license key
        // TODO: Add other filters as needed
        // status: filters?.status,
        // issuedToEmail: filters?.email,
        // issuedToPhone: filters?.phone,
      };

      // Call Spring Boot API
      const response = await vendorLicenseServices.getLicenseKeys(apiParams);
      const data = response.data;

      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || 0,
        size: data.size || 10,
      });

    } catch (error) {
      console.error('Error fetching license keys:', error);
      // Handle error response
      const errorMessage = error.response?.data?.message || error.message || 'Failed to fetch license keys';

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
  }, []);

  return (
    // vendorlayout
    <VendorLayout>
      <div className="assigned-clients-content">
        <PageHeader
          title="Assigned Clients"
          subtitle="Manage and issue licences to your clients"
        />

        <div className="assigned-clients-table">
          <DataTable
            data={tableData}
            columns={LICENSE_KEYS_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            basePath="/vendor/assigned-clients"
            primaryKeys={['id']}
            showActions={true}
            showEditButton={false}
            showViewButton={true}
            editButtonDisabled={false}
            viewButtonDisabled={false}
            customActions={customActions}
            onView={handleViewLicense}
            className="license-keys-table"
          />
        </div>
      </div>

      {/* View License Details Popup */}
      <PopUp
        isOpen={isViewLicensePopupOpen}
        onClose={handleCloseViewLicensePopup}
        title="Tenant License Details"
        size="medium"
      >
        <div className="license-success-form">
          <div className="success-form-fields">
            <div className="client-details-box">
              <h4 className="client-details-title">Client Details</h4>
              <div className="client-details-content">
                <div className="client-detail-item">
                  <span className="detail-label">Name:</span>
                  <span className="detail-value">{viewLicenseData?.redeemedTenantName || 'N/A'}</span>
                </div>
                
                <div className="client-detail-item">
                  <span className="detail-label">Email:</span>
                  <span className="detail-value">{viewLicenseData?.issuedToEmail || 'N/A'}</span>
                </div>
                
                <div className="client-detail-item">
                  <span className="detail-label">Mobile:</span>
                  <span className="detail-value">{viewLicenseData?.issuedToPhone || 'N/A'}</span>
                </div>
              </div>
            </div>
            
            <div className="activation-key-box">
              <label className="field-label">Activation Key:</label>
              <div className="field-value activation-key">XXXX-XXXX-XXXX-{viewLicenseData?.plainCodeLast4 || 'N/A'}</div>
            </div>
          </div>
          
          <div className="success-actions">
            <Button
              text="Close"
              onClick={handleCloseViewLicensePopup}
            />
          </div>
        </div>
      </PopUp>
    </VendorLayout>
    // vendorlayout end
  );
};

export default AssignedClients;