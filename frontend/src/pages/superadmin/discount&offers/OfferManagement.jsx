// src/pages/superadmin/discount&offers/OfferManagement.jsx
import React, { useState, useCallback } from 'react';
// css file import
import './OfferManagement.css';
// super admin layout import
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
// page header import
import PageHeader from '../../../components/common/pageheader/PageHeader';
// data table import
import { DataTable } from '../../../components/common/table';
import { OFFER_MANAGEMENT_COLUMNS } from '../../../config/columnConfig';
// coupon services import
import { couponServices } from '../../../services/apiService';
// pop up import
import PopUp from '../../../components/common/popups/PopUp';
// input field import
import InputField from '../../../components/common/inputfield/InputField';
// option input box import
import OptionInputBox from '../../../components/common/optioninputbox/OptionInputBox';
// Theme CSS
import '../../../theme/LightTheme.css';
import formConfig from '../../../config/formConfig';
import { toast } from 'react-toastify';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';


// offer management component 
const OfferManagement = () => {
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
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingOfferId, setEditingOfferId] = useState(null);

  // Form data for create offer
  const [formData, setFormData] = useState({
    offerCode: '',
    description: '',
    offerType: 'percentage',
    offerValue: '',
    percentageValue: '',
    flatDiscount: '',
    percentageDiscount: '',
    validFrom: '',
    validTo: '',
    maxUses: '',
    status: 'active'
  });

  // Fetch data function - matches Spring Boot API contract
 // Fetch data function - matches Spring Boot API contract
  const fetchData = useCallback(async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);
 
      // Since getAllCoupons doesn't support pagination yet, we'll fetch all and paginate locally
      const response = await couponServices.getCouponsPagination({
        page,
        size,
        sortBy: sortField,
        sortDir: sortOrder,
        search: filters?.search,
      });
      const allOffers = response.data.content || [];
 
      // Apply search filter if exists
      let filteredOffers = allOffers;
      if (filters?.search) {
        filteredOffers = allOffers.filter(offer =>
          offer.code.toLowerCase().includes(filters.search.toLowerCase()) ||
          offer.discountType.toLowerCase().includes(filters.search.toLowerCase())
        );
      }
 
 
      // Apply pagination
      const startIndex = (page || 0) * (size || 10);
      const endIndex = startIndex + (size || 10);
      const paginatedOffers = filteredOffers.slice(startIndex, endIndex);
 
      setTableData({
        content: paginatedOffers,
        totalElements: filteredOffers.length,
        totalPages: Math.ceil(filteredOffers.length / (size || 10)),
        number: page || 0,
        size: size || 10,
      });
 
    } catch (error) {
      console.error('Error fetching offers:', error);
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
 


  // function to handle edit offer
  const handleEditOffer = async (offer) => {
   
    
  try {
    const payload = { id: offer.id };
    const response = await couponServices.getoffermanagmentbyid(payload);
    const data = response.data;

    // detect discount type
    const isPercentage = data.discountType === 'PERCENTAGE';

    setFormData({
      offerCode: data.code || '',
      description: data.discription || '',
      offerType: isPercentage ? 'percentage' : 'flat',
      percentageDiscount: isPercentage ? data.discountPercentage : '',
      flatDiscount: !isPercentage ? data.discountValue : '',
      validFrom: data.validFrom || '',
      validTo: data.validTo || '',
      maxUses: data.maxUses || '',
      status: data.status?.toLowerCase() || 'active'
    });

    setEditingOfferId(data.id);
    setIsEditMode(true);
    setIsCreatePopupOpen(true);

  } catch (error) {
    toast.error("Failed to fetch offer details");
  }
};


  // function to handle create offer
  const handleCreateOffer = () => {
    setIsCreatePopupOpen(true);
  };

  // function to handle close create popup
  const handleCloseCreatePopup = () => {
    setIsCreatePopupOpen(false);
    setIsEditMode(false);
    setEditingOfferId(null);
    setFormData({
      offerCode: '',
      description: '',
      offerType: 'percentage',
      offerValue: '',
      percentageValue: '',
      flatDiscount: '',
      percentageDiscount: '',
      validFrom: '',
      validTo: '',
      maxUses: '',
      status: 'active'
    });
  };

  let lastToastTime = 0;

const handleInputChange = (e) => {
  const { name, value } = e.target;
  const numericValue = Number(value);
  const now = Date.now();

  const showToast = (msg) => {
    if (now - lastToastTime > 1500) { // rate limit to avoid spam
      toast.error(msg, { autoClose: 2000 });
      lastToastTime = now;
    }
  };

  // Validate percentage discount
  if (name === 'percentageDiscount') {
    if (numericValue > 100) {
      showToast('Percentage discount cannot be greater than 100%');
      setFormData(prev => ({ ...prev, percentageDiscount: '' }));
      return;
    }
    if (numericValue < 0) {
      showToast('Percentage discount cannot be negative');
      setFormData(prev => ({ ...prev, percentageDiscount: '' }));
      return;
    }
  }

  // Validate flat discount
  if (name === 'flatDiscount') {
    if (numericValue > 100) {
      showToast('Flat discount value cannot be greater than 100');
      setFormData(prev => ({ ...prev, flatDiscount: '' }));
      return;
    }
    if (numericValue < 0) {
      showToast('Flat discount cannot be negative');
      setFormData(prev => ({ ...prev, flatDiscount: '' }));
      return;
    }
  }

  // Update state normally
  setFormData(prev => ({
    ...prev,
    [name]: value
  }));
};

  // function to handle submit create offer
  const handleSubmitCreateOffer = async () => {

  setIsSubmitting(true);

  try {
    const payload = {
      id: null, // since this is create
      code: formData.offerCode,
      discountType: formData.offerType === 'percentage' ? 'PERCENTAGE' : 'FLAT',
      discountPercentage:
        formData.offerType === 'percentage'
          ? Number(formData.percentageDiscount)
          : null,
      discountValue:
        formData.offerType === 'flat'
          ? Number(formData.flatDiscount)
          : null,
      currency: "INR",
      validFrom: formData.validFrom,
      validTo: formData.validTo,
      maxUses: formData.maxUses ?? '0', // fixed here
      discription: formData.description,
      status: formData.status.toUpperCase()
    };

    console.log("Create Offer Payload:", payload);

    await couponServices.upsertCoupon(payload);

    toast.success('Offer Created successfully', {
      onClose: () => {
        handleCloseCreatePopup();
        fetchData({ page: 0, size: 10 });
      },
      autoClose: 1000
    });

  } catch (error) {
    console.error('Create offer error:', error);
    toast.error(error?.response?.data?.message || 'Failed to create offer ❌');
  } finally {
    setIsSubmitting(false);
  }
};



  const handleUpdateOffer = async () => {
    

  if (!editingOfferId) {
    return;
  }

  setIsSubmitting(true);

  try {
    const payload = {
      id: editingOfferId,
      code: formData.offerCode,
      discountType: formData.offerType === 'percentage' ? 'PERCENTAGE' : 'FLAT',
      discountPercentage:
        formData.offerType === 'percentage'
          ? Number(formData.percentageDiscount)
          : null,
      discountValue:
        formData.offerType === 'flat'
          ? Number(formData.flatDiscount)
          : null,
      currency: 'INR',
      validFrom: formData.validFrom,
      validTo: formData.validTo,
      maxUses: formData.maxUses,
      discription: formData.description,
      status: formData.status.toUpperCase()
    };

    console.log('Update Coupon Payload:', payload);
    
    await couponServices.upsertCoupon(payload);
    toast.success('Offer updated successfully', {
      onClose: () => {
        handleCloseCreatePopup();
        fetchData({ page: 0, size: 10 });
      },
      autoClose: 1000 // 2 seconds before onClose triggers
    });

  } catch (error) {
    toast.error("Failed to update offer")
    
  } finally {
    setIsSubmitting(false);
  }
};

const isFormValid = () => {
  // Common required fields
  if (!formData.offerCode.trim()) return false;
  if (!formData.description.trim()) return false;
  if (!formData.offerType) return false;
  if (!formData.validFrom) return false;
  if (!formData.validTo) return false;
  if (!formData.maxUses || Number(formData.maxUses) <= 0) return false;
  if (!formData.status) return false;

  // Discount validation based on type
  if (formData.offerType === 'percentage') {
    return (
      formData.percentageDiscount !== '' &&
      Number(formData.percentageDiscount) > 0 &&
      Number(formData.percentageDiscount) <= 100
    );
  }

  // Flat discount
  return (
    formData.flatDiscount !== '' &&
    Number(formData.flatDiscount) > 0
  );
};


  

  return (
    <SuperAdminLayout>
      
      <div className="offer-management-page">
        {/* Page Header Component */}
        <PageHeader
          title="Offer Management"
          subtitle="Manage special offers and promotions"
          // button component to create offer
          button={<button className="create-plan-btn" onClick={handleCreateOffer}>Create Offer</button>}
        />

        {/* Data Table */}
        <div className="offer-management-table">
          <DataTable
            data={tableData}
            columns={OFFER_MANAGEMENT_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            onEdit={handleEditOffer}
            primaryKeys={['id']}
            showActions={true}
            showEditButton={true}
            showViewButton={false}
            editButtonDisabled={false}
            viewButtonDisabled={false}
            className="offer-management-data-table"
          />
        </div>
      </div>

      {/* // offer create popup div start */}
      <PopUp
        isOpen={isCreatePopupOpen}
        onClose={handleCloseCreatePopup}
        title={isEditMode ? 'Update Offer' : 'Create Offer'}
        // added subtitle to popup
        subtitle={
          isEditMode
            ? 'Modify existing offer details'
            : 'Configure offer settings and offers'
          }
        size="large"
      >
        {/* create offer form div start */}
        <div className="create-plan-form">
          <div className="form-table">
            <div className="form-row">
              <InputField
                label={formConfig.offerManagement.offerCode.label}
                name="offerCode"
                value={formData.offerCode}
                onChange={handleInputChange}
                validationType="ALPHA_NUMERIC_ONLY"
                max={15}
                required
                classN="large"
                placeholder="TECH20"
                disabled={isEditMode}
              />
              <InputField
                label={formConfig.offerManagement.description.label}
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                validationType="EVERYTHING"
                max={500}
                required
                classN="large"
                placeholder="Special offer for tech solutions"
              />
            </div>
            <div className="form-row">
              <OptionInputBox
                label={formConfig.venderDiscount.discountType.label}
                name="offerType"
                value={formData.offerType}
                onChange={handleInputChange}
                options={[
                  { code: 'flat', value: 'Flat' },
                  { code: 'percentage', value: 'Percentage' }
                ]}
                required
                classN="large"
                disabled={isEditMode}
              />
              {formData.offerType === 'percentage' ? (
                <InputField
                  label={formConfig.venderDiscount.percentage.label}
                  name="percentageDiscount"
                  value={formData.percentageDiscount}
                  onChange={handleInputChange}
                  validationType="PERCENTAGE"
                  required
                  max={3}
                  classN="large"
                  placeholder="20%"
                  disabled={isEditMode}
                />
              ) : (
                <InputField
                  label={formConfig.venderDiscount.value.label}
                  name="flatDiscount"
                  value={formData.flatDiscount}
                  onChange={handleInputChange}
                  validationType="AMOUNT"
              
                  required
                  classN="large"
                  placeholder="500"
                />
              )}
            </div>
            <div className="form-row">
              <InputField
                label={formConfig.offerManagement.validFrom.label}
                name="validFrom"
                value={formData.validFrom}
                onChange={handleInputChange}
                type="date"
                validationType="DATE"
                required
                classN="large"
                placeholder="2024-01-01"
              />
              <InputField
                label={formConfig.offerManagement.validTo.label}
                name="validTo"
                value={formData.validTo}
                onChange={handleInputChange}
                type="date"
                validationType="DATE"
                required
                classN="large"
                placeholder="2024-12-31"
                disabled={!formData.validFrom}        // 🔹 Disabled until validFrom is filled
              min={formData.validFrom || undefined} // 🔹 Cannot select before validFrom
              />
            </div>
            <div className="form-row">
              <InputField
                label={formConfig.offerManagement.maxUser.label}
                name="maxUses"
                value={formData.maxUses}
                onChange={handleInputChange}
                trpe="number"
                max={3}
                validationType="NUMBER_ONLY"
                required
                classN="large"
                placeholder="100"
              />
              <OptionInputBox
                label={formConfig.subscriptionPlan.status.label}
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                options={[
                  { code: 'active', value: 'Active' },
                  { code: 'inactive', value: 'Inactive' }
                ]}
                required
                classN="large"
              />
            </div>
          </div>
          {/* form actions div start */}
          <div className="form-actions">
            <button
              className="btn-full btn-primary"
              onClick={isEditMode ? handleUpdateOffer : handleSubmitCreateOffer}
              disabled={isSubmitting || !isFormValid()}
            >
              {isSubmitting
                ? isEditMode
                  ? 'Updating...'
                  : 'Creating...'
                : isEditMode
                  ? 'Update Offer'
                  : 'Create Offer'}
            </button>
          </div>
          {/* form actions div end */}
        </div>
        {/* create offer form div end */}
      </PopUp>
      {/* // offer create popup div end */}

      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </SuperAdminLayout>
  );
};

export default OfferManagement;