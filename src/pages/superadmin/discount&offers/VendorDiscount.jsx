// src/pages/superadmin/discount&offers/VendorDiscount.jsx
import React, { useState, useCallback } from 'react';
// css file import
import './VenderDiscount.css';
// super admin layout import
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
// page header import
import PageHeader from '../../../components/common/pageheader/PageHeader';
// data table import
import { DataTable } from '../../../components/common/table';
import { VENDOR_DISCOUNT_COLUMNS } from '../../../config/columnConfig';
// vendor discount services import
import { vendorDiscountServices } from '../../../services/apiService';
// pop up import
import PopUp from '../../../components/common/popups/PopUp';
// input field import
import InputField from '../../../components/common/inputfield/InputField';
// option input box import
import OptionInputBox from '../../../components/common/optioninputbox/OptionInputBox';
// Theme CSS
import '../../../theme/LightTheme.css';

import { toast, ToastContainer } from 'react-toastify';
import formConfig from '../../../config/formConfig';




// vendor discount component 
const VendorDiscount = () => {
  // State for table data
  const [tableData, setTableData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = useState(false);
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [isEditPopupOpen, setIsEditPopupOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingDiscountId, setEditingDiscountId] = useState(null);

  // Form data for create discount
  const [formData, setFormData] = useState({
    discountName: '',
    discountType: 'percentage',
    discountValue: '',
    percentageValue: ''
  });


 // Fetch data function - matches Spring Boot API contract
  const fetchData = useCallback(async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);
 
      // Since getAllDiscounts doesn't support pagination yet, we'll fetch all and paginate locally
      const response = await vendorDiscountServices.getAllDiscounts({
        page,
        size,
        sortBy: sortField,
        sortDir: sortOrder,
        search: filters?.search,
      });
      const allDiscounts = response.data.content || [];
 
      // Apply search filter if exists
      let filteredDiscounts = allDiscounts;
      if (filters?.search) {
        filteredDiscounts = allDiscounts.filter(discount =>
          discount.name.toLowerCase().includes(filters.search.toLowerCase()) ||
          discount.type.toLowerCase().includes(filters.search.toLowerCase())
        );
      }
 
      // Apply pagination
      const startIndex = (page || 0) * (size || 10);
      const endIndex = startIndex + (size || 10);
      const paginatedDiscounts = filteredDiscounts.slice(startIndex, endIndex);
 
      setTableData({
        content: paginatedDiscounts,
        totalElements: filteredDiscounts.length,
        totalPages: Math.ceil(filteredDiscounts.length / (size || 10)),
        number: page || 0,
        size: size || 10,
      });
 
    } catch (error) {
      console.error('Error fetching vendor discounts:', error);
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
 



    const handleEditDiscount = async (discount) => {
    try {
      const payload = { id: discount.id };
      const response = await vendorDiscountServices.getById(payload);
      const data = response.data;

      const isPercentage = data.type === 'PERCENTAGE';

      setFormData({
        discountName: data.name,
        discountType: isPercentage ? 'percentage' : 'flat',
        discountValue: isPercentage ? '' : data.value,
        percentageValue: isPercentage ? data.value : '',
      });
      setEditingDiscountId(data.id);
      setIsEditMode(true);
      setIsCreatePopupOpen(true);
    } catch (error) {
      console.error('Edit discount error:', error);
    }
  };



  // function to handle create discount
  const handleCreateDiscount = () => {
    setIsCreatePopupOpen(true);
  };

  // function to handle close create popup
  const handleCloseCreatePopup = () => {
    setIsCreatePopupOpen(false);
    setIsEditMode(false);
    setEditingDiscountId(null);
    setFormData({
      discountName: '',
      discountType: 'percentage',
      discountValue: '',
      percentageValue: ''
    });
  };

  // function to handle close edit popup
  const handleCloseEditPopup = () => {
    setIsEditPopupOpen(false);
  };

  let lastToastTime = 0;

  const handleInputChange = (e) => {
  const { name, value } = e.target;
  const numericValue = Number(value);
  const now = Date.now();

  // Toast rate limiter
  const showToast = (msg) => {
    if (now - lastToastTime > 1500) {
      toast.error(msg, { autoClose: 2000 });
      lastToastTime = now;
    }
  };

  // Validation for percentage field
  if (name === 'percentageValue') {
    if (numericValue > 100) {
      showToast('Percentage discount cannot be greater than 100%');
      setFormData(prev => ({ ...prev, percentageValue: '' }));
      return;
    }
    if (numericValue < 0) {
      showToast('Percentage cannot be negative');
      setFormData(prev => ({ ...prev, percentageValue: '' }));
      return;
    }
  }

  // Validation for flat discount value
  if (name === 'discountValue') {
    if (numericValue > 100) {
      showToast('Discount value cannot be greater than 100');
      setFormData(prev => ({ ...prev, discountValue: '' }));
      return;
    }
    if (numericValue < 0) {
      showToast('Discount value cannot be negative');
      setFormData(prev => ({ ...prev, discountValue: '' }));
      return;
    }
  }

  // Update the field normally if valid
  setFormData(prev => ({
    ...prev,
    [name]: value
  }));
};


  // function to handle submit create discount
  const handleSubmitCreateDiscount = async () => {
    
    setIsSubmitting(true);

    try {
      // 🔹 Prepare API payload as per Swagger
      const payload = {
        type: formData.discountType === 'percentage' ? 'PERCENTAGE' : 'FLAT',
        name: formData.discountName,
        value:
          formData.discountType === 'percentage'
            ? Number(formData.percentageValue)
            : Number(formData.discountValue),
        effectiveDate: new Date().toISOString().split('T')[0] // YYYY-MM-DD
      };

      await vendorDiscountServices.upsertDiscount(payload);
      toast.success('Vender discount created succesfully.', {
            onClose: () => {
              handleCloseCreatePopup();
              fetchData({ page: 0, size: 10 });
            },
            autoClose: 1000 // 2 seconds before onClose triggers
          });
    } catch (error) {
      toast.error('Failed to create Vendor discount');  
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDiscountUpdate = async(discount) => {
    try {
      
      // 🔹 Prepare API payload as per Swagger
      const payload = {
        id: editingDiscountId,
        type: formData.discountType === 'percentage' ? 'PERCENTAGE' : 'FLAT',
        name: formData.discountName,
        value:
          formData.discountType === 'percentage'
            ? Number(formData.percentageValue)
            : Number(formData.discountValue),
        effectiveDate: new Date().toISOString().split('T')[0] // YYYY-MM-DD
      };
      await vendorDiscountServices.upsertDiscount(payload);
      toast.success('Vender discount updated succesfully.', {
            onClose: () => {
              handleCloseCreatePopup();
              fetchData({ page: 0, size: 10 });
            },
            autoClose: 1000 // 2 seconds before onClose triggers
          });
    } catch (error) {
      toast.error('Failed to update vendor discount')
    } finally {
      setIsSubmitting(false);
    }
    };

    const isFormValid = () => {
  // Common required fields
  if (!formData.discountName.trim()) return false;
  if (!formData.discountType) return false;

  // Conditional validation
  if (formData.discountType === 'percentage') {
    return (
      formData.percentageValue !== '' &&
      Number(formData.percentageValue) > 0 &&
      Number(formData.percentageValue) <= 100
    );
  }

  // Flat discount validation
  return (
    formData.discountValue !== '' &&
    Number(formData.discountValue) > 0
  );
};


    

  return (
    <SuperAdminLayout>
      <ToastContainer />
      <div className="vendor-discount-page">
        {/* Page Header Component */}

        <PageHeader
          title="Vendor Discounts"
          subtitle="Manage vendor discount codes and offers"
          // button component to create discount
          button={<button className="create-plan-btn" onClick={handleCreateDiscount}>Create Discount</button>}
        />

        {/* Data Table */}
        <div className="vendor-discount-table">
          <DataTable
            data={tableData}
            columns={VENDOR_DISCOUNT_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            showActions={true}
            showEditButton={true}
            onEdit={handleEditDiscount}
            showViewButton={false}
            editButtonDisabled={false}
            viewButtonDisabled={false}
            className="vendor-discount-data-table"
          />
        </div>
      </div>

      {/* // discount create popup div start */}
      <PopUp
        isOpen={isCreatePopupOpen}
        onClose={handleCloseCreatePopup}
        title={isEditMode ? 'Edit Vendor Discount' : 'Create Vendor Discount'}
        // added subtitle to popup
        ssubtitle={
          isEditMode
            ? 'Update discount settings and offers'
            : 'Configure discount settings and offers'
        }
        size="large"
      >
        {/* create discount form div start */}
        <div className="create-plan-form">
          <div className="form-table">
            <div className="form-row">
              <InputField
                label={formConfig.venderDiscount.discountName.label}
                name="discountName"
                value={formData.discountName}
                onChange={handleInputChange}
                required
                max={50}
                classN="large"
                placeholder="e.g., 11% OFF"
              />
              <OptionInputBox
                label={formConfig.venderDiscount.discountType.label}
                name="discountType"
                value={formData.discountType}
                onChange={handleInputChange}
                options={[
                  { code: 'flat', value: 'Flat' },
                  { code: 'percentage', value: 'Percentage' }
                ]}
                required
                classN="large"
              />
              {formData.discountType === 'percentage' ? (
                <InputField
                  label={formConfig.venderDiscount.percentage.label}
                  name="percentageValue"
                  value={formData.percentageValue}
                  onChange={handleInputChange}
                  validationType="PERCENTAGE"
                  required
                  max={3}
                  classN="large"
                  placeholder="20%"
                />
              ) : (
                <InputField
                  label={formConfig.venderDiscount.value.label}
                  name="discountValue"
                  value={formData.discountValue}
                  onChange={handleInputChange}
                  validationType="AMOUNT"
                  required
                  classN="large"
                  placeholder="500"
                  max={3}
                  
                />
              )}
            </div>
          </div>
          {/* form actions div start */}
          <div className="form-actions">
            <button
              className="btn-full btn-primary"
              onClick={isEditMode? handleDiscountUpdate : handleSubmitCreateDiscount}
              disabled={isSubmitting || !isFormValid()}
            >
              {isSubmitting
                  ? isEditMode ? 'Updating...' : 'Creating...'
                  : isEditMode ? 'Update Discount' : 'Create Discount'}   
            </button>
          </div>
          {/* form actions div end */}
        </div>
        {/* create discount form div end */}
      </PopUp>
      {/* // discount create popup div end */}

      {/* Edit Discount Popup */}
      <PopUp
        isOpen={isEditPopupOpen}
        onClose={handleCloseEditPopup}
        title="Edit Vendor Discount"
        subtitle="Modify discount settings and offers"
        size="large"
      >
        <div className="edit-discount-form">
          <div className="development-message">
            <p>🚧 Edit functionality is currently in development.</p>
            <p>This feature will be available soon</p>
          </div>
          <div className="form-actions">
            <button
              className="btn-full btn-primary"
              onClick={handleCloseEditPopup}
            >
              Close
            </button>
          </div>
        </div>
      </PopUp>
      {/* // discount edit popup div end */}
    </SuperAdminLayout>
  );
};

export default VendorDiscount;
